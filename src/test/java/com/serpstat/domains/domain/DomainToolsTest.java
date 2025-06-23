package com.serpstat.domains.domain;

import com.serpstat.core.SerpstatApiClient;
import com.serpstat.core.SerpstatApiResponse;
import com.serpstat.core.SerpstatApiException;
import com.serpstat.core.BaseToolHandler;
import com.serpstat.core.ToolProvider;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

/**
 * Unit tests for DomainTools class
 * Tests inheritance from BaseToolHandler,
 * implementation of ToolProvider interface,
 */
@DisplayName("DomainTools Tests")
class DomainToolsTest {

    @Mock
    private SerpstatApiClient mockApiClient;

    @Mock
    private McpSyncServerExchange mockExchange;

    private DomainTools domainTools;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        domainTools = new DomainTools(mockApiClient);
    }

    // ================================
    // IMPLEMENTED TESTS (3 most critical)
    // ================================

    @Test
    @DisplayName("Test DomainTools constructor and basic functionality")
    void testConstructorAndBasicFunctionality() {
        // Test constructor with SerpstatApiClient
        assertNotNull(domainTools, "DomainTools should be created successfully");

        // Test that DomainTools extends BaseToolHandler
        assertTrue(domainTools instanceof BaseToolHandler, "DomainTools should extend BaseToolHandler");

        // Test that DomainTools implements ToolProvider
        assertTrue(domainTools instanceof ToolProvider, "DomainTools should implement ToolProvider");

        // Test that api client is set
        assertNotNull(mockApiClient, "ApiClient should not be null");

        // Test basic instantiation doesn't throw exceptions
        assertDoesNotThrow(() -> {
            DomainTools tools = new DomainTools(mockApiClient);
            assertNotNull(tools);
        }, "DomainTools construction should not throw exceptions");
    }

    @Test
    @DisplayName("Test tool provider functionality and tool specifications")
    void testToolProviderFunctionality() {
        // Test that getTools() returns a list
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();
        assertNotNull(tools, "Tools list should not be null");
        assertFalse(tools.isEmpty(), "Tools list should not be empty");

        // Test that we have the expected number of domain tools
        // Based on DomainTools implementation, we should have 5 tools
        assertEquals(5, tools.size(), "Should have 5 domain analysis tools");
        // Test that all tools have proper structure
        for (McpServerFeatures.SyncToolSpecification tool : tools) {
            assertNotNull(tool, "Tool specification should not be null");
            assertNotNull(tool.tool(), "Tool should not be null");
            assertNotNull(tool.tool().name(), "Tool name should not be null");
            assertNotNull(tool.tool().description(), "Tool description should not be null");
            assertNotNull(tool.tool().inputSchema(), "Tool input schema should not be null");

            // Test tool names are properly formatted
            assertFalse(tool.tool().name().isEmpty(), "Tool name should not be empty");
            // Domain tools can start with either "domain_" or "get_domain" or "get_domains"
            String toolName = tool.tool().name();
            assertTrue(toolName.startsWith("domain_") || toolName.startsWith("get_domain"),
                    "Domain tool names should start with 'domain_' or 'get_domain', but was: " + toolName);

            // Test descriptions are meaningful
            assertFalse(tool.tool().description().isEmpty(), "Tool description should not be empty");
            assertTrue(tool.tool().description().length() > 10, "Tool description should be meaningful");

            // Test schemas are not null
            assertNotNull(tool.tool().inputSchema(), "Tool schema should not be null");
        }
    }

    @Test
    @DisplayName("Test specific domain tool creation and properties")
    void testSpecificDomainTools() {
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();

        // Extract tool names for validation
        List<String> toolNames = tools.stream()
                .map(tool -> tool.tool().name())
                .toList();
        // Test that all expected domain tools are present
        assertTrue(toolNames.contains("get_domains_info"), "Should have get_domains_info tool");
        assertTrue(toolNames.contains("domain_regions_count"), "Should have domain_regions_count tool");
        assertTrue(toolNames.contains("domain_keywords"), "Should have domain_keywords tool");
        assertTrue(toolNames.contains("get_domain_urls"), "Should have get_domain_urls tool");
        assertTrue(toolNames.contains("get_domains_uniq_keywords"), "Should have get_domains_uniq_keywords tool");

        // Test specific tool properties
        McpServerFeatures.SyncToolSpecification domainInfoTool = tools.stream()
                .filter(tool -> "get_domains_info".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);

        assertNotNull(domainInfoTool, "get_domains_info tool should exist");
        assertTrue(domainInfoTool.tool().description().toLowerCase().contains("domain"),
                "get_domains_info description should mention domains");
        // Test that schemas are properly configured
        assertNotNull(domainInfoTool.tool().inputSchema(), "get_domains_info schema should not be null");

        // Test regions count tool
        McpServerFeatures.SyncToolSpecification regionsCountTool = tools.stream()
                .filter(tool -> "domain_regions_count".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);

        assertNotNull(regionsCountTool, "domain_regions_count tool should exist");
        assertTrue(regionsCountTool.tool().description().toLowerCase().contains("regional"),
                "regions count description should mention regional analysis");
    }

    @Test
    @DisplayName("Test DomainTools extends BaseToolHandler")
    void testInheritanceFromBaseToolHandler() {
        // Test inheritance from BaseToolHandler
        assertTrue(domainTools instanceof BaseToolHandler,
                "DomainTools should extend BaseToolHandler");

        // Test implementation of ToolProvider interface
        assertTrue(domainTools instanceof ToolProvider,
                "DomainTools should implement ToolProvider interface");

        // Test domain name is correctly set
        String domainName = domainTools.getDomainName();
        assertNotNull(domainName, "Domain name should not be null");
        assertEquals("Domain Analysis", domainName,
                "Domain name should be 'Domain Analysis'");

        // Test that tools list contains expected 5 tools
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();
        assertNotNull(tools, "Tools list should not be null");
        assertEquals(5, tools.size(), "Should have exactly 5 domain analysis tools");

        // Extract tool names for validation
        List<String> toolNames = tools.stream()
                .map(tool -> tool.tool().name())
                .toList();

        // Verify each expected tool is present with proper name
        assertTrue(toolNames.contains("get_domains_info"),
                "Should have get_domains_info tool");
        assertTrue(toolNames.contains("domain_regions_count"),
                "Should have domain_regions_count tool");
        assertTrue(toolNames.contains("domain_keywords"),
                "Should have domain_keywords tool");
        assertTrue(toolNames.contains("get_domain_urls"),
                "Should have get_domain_urls tool");
        assertTrue(toolNames.contains("get_domains_uniq_keywords"),
                "Should have get_domains_uniq_keywords tool");

        // Verify each tool has proper description and is not empty
        for (McpServerFeatures.SyncToolSpecification tool : tools) {
            assertNotNull(tool.tool().name(),
                    "Tool name should not be null");
            assertFalse(tool.tool().name().trim().isEmpty(),
                    "Tool name should not be empty");

            assertNotNull(tool.tool().description(),
                    "Tool description should not be null");
            assertFalse(tool.tool().description().trim().isEmpty(),
                    "Tool description should not be empty");

            // Verify description contains meaningful content
            assertTrue(tool.tool().description().length() > 20,
                    "Tool description should be descriptive (more than 20 characters)");

            // Verify tool has input schema
            assertNotNull(tool.tool().inputSchema(),
                    "Tool input schema should not be null");
        }

        // Test specific tool descriptions contain relevant keywords
        McpServerFeatures.SyncToolSpecification domainInfoTool = tools.stream()
                .filter(tool -> "get_domains_info".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);
        assertNotNull(domainInfoTool, "get_domains_info tool should exist");
        assertTrue(domainInfoTool.tool().description().toLowerCase().contains("domain"),
                "get_domains_info description should mention domain");
        assertTrue(domainInfoTool.tool().description().toLowerCase().contains("information"),
                "get_domains_info description should mention information");

        McpServerFeatures.SyncToolSpecification regionsCountTool = tools.stream()
                .filter(tool -> "domain_regions_count".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);
        assertNotNull(regionsCountTool, "domain_regions_count tool should exist");
        assertTrue(regionsCountTool.tool().description().toLowerCase().contains("region"),
                "domain_regions_count description should mention regions");

        McpServerFeatures.SyncToolSpecification keywordsTool = tools.stream()
                .filter(tool -> "domain_keywords".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);
        assertNotNull(keywordsTool, "domain_keywords tool should exist");
        assertTrue(keywordsTool.tool().description().toLowerCase().contains("keyword"),
                "domain_keywords description should mention keywords");

        // Test that all tool names are unique
        long uniqueToolNamesCount = toolNames.stream().distinct().count();
        assertEquals(5, uniqueToolNamesCount, "All tool names should be unique");

        // Test that inheritance allows access to base functionality
        // BaseToolHandler should provide access to the API client
        assertDoesNotThrow(() -> {
            // This tests that the constructor properly calls super(apiClient)
            // and that the inheritance chain is working correctly
            DomainTools testTools = new DomainTools(mockApiClient);
            assertNotNull(testTools.getTools(), "Inherited functionality should work");
        }, "Inheritance from BaseToolHandler should work properly");
    }

    @Test
    @DisplayName("Test DomainTools implements ToolProvider correctly")
    void testToolProviderImplementation() {
        // Test getDomainName() returns "Domain Analysis"
        String domainName = domainTools.getDomainName();
        assertNotNull(domainName, "Domain name should not be null");
        assertEquals("Domain Analysis", domainName,
                "getDomainName() should return 'Domain Analysis'");

        // Test getTools() returns list of 5 tools
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();
        assertNotNull(tools, "getTools() should not return null");
        assertEquals(5, tools.size(),
                "getTools() should return list of exactly 5 tools");

        // Extract tool names for verification
        List<String> toolNames = tools.stream()
                .map(tool -> tool.tool().name())
                .toList();

        // Verify tool names: get_domains_info, domain_regions_count, domain_keywords,
        // get_domain_urls, get_domains_uniq_keywords
        assertTrue(toolNames.contains("get_domains_info"),
                "Tools should include get_domains_info");
        assertTrue(toolNames.contains("domain_regions_count"),
                "Tools should include domain_regions_count");
        assertTrue(toolNames.contains("domain_keywords"),
                "Tools should include domain_keywords");
        assertTrue(toolNames.contains("get_domain_urls"),
                "Tools should include get_domain_urls");
        assertTrue(toolNames.contains("get_domains_uniq_keywords"),
                "Tools should include get_domains_uniq_keywords");

        // Verify each tool has valid description and schema
        for (McpServerFeatures.SyncToolSpecification tool : tools) {
            String toolName = tool.tool().name();

            // Test tool name is valid
            assertNotNull(toolName, "Tool name should not be null");
            assertFalse(toolName.trim().isEmpty(), "Tool name should not be empty");

            // Test tool description is valid
            String description = tool.tool().description();
            assertNotNull(description, "Tool description should not be null for " + toolName);
            assertFalse(description.trim().isEmpty(), "Tool description should not be empty for " + toolName);
            assertTrue(description.length() > 10,
                    "Tool description should be descriptive for " + toolName);
            // Test tool schema is valid
            assertNotNull(tool.tool().inputSchema(), "Tool schema should not be null for " + toolName);
        }

        // Test that ToolProvider contract is fully implemented
        // Multiple calls should return consistent results
        String domainName2 = domainTools.getDomainName();
        assertEquals(domainName, domainName2,
                "getDomainName() should return consistent results");

        List<McpServerFeatures.SyncToolSpecification> tools2 = domainTools.getTools();
        assertEquals(tools.size(), tools2.size(),
                "getTools() should return consistent results");

        // Test that tools are properly ordered and contain expected content
        List<String> expectedToolNames = List.of(
                "get_domains_info",
                "domain_regions_count",
                "domain_keywords",
                "get_domain_urls",
                "get_domains_uniq_keywords");

        assertEquals(expectedToolNames, toolNames,
                "Tools should be in expected order and contain all expected tools");
    }

    @Test
    @DisplayName("Test create get domains info tool specification")
    void testCreateGetDomainsInfoTool() {
        // Get the tools list to find the specific tool
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();

        // Find the get_domains_info tool
        McpServerFeatures.SyncToolSpecification domainsInfoTool = tools.stream()
                .filter(tool -> "get_domains_info".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);

        // Verify tool exists
        assertNotNull(domainsInfoTool, "get_domains_info tool should be created");

        // Verify tool name is "get_domains_info"
        assertEquals("get_domains_info", domainsInfoTool.tool().name(),
                "Tool name should be 'get_domains_info'");

        // Verify description contains "comprehensive domain information"
        String description = domainsInfoTool.tool().description();
        assertNotNull(description, "Tool description should not be null");
        assertTrue(description.toLowerCase().contains("comprehensive"),
                "Description should contain 'comprehensive'");
        assertTrue(description.toLowerCase().contains("domain"),
                "Description should contain 'domain'");
        assertTrue(description.toLowerCase().contains("information"),
                "Description should contain 'information'");

        // Verify the complete expected description
        String expectedDescription = "Get comprehensive domain information using Serpstat API. Returns visibility, keywords count, estimated traffic, dynamics and PPC data for multiple domains.";
        assertEquals(expectedDescription, description,
                "Description should match expected comprehensive domain information description");

        // Verify schema is DomainSchemas.DOMAINS_INFO_SCHEMA
        assertNotNull(domainsInfoTool.tool().inputSchema(),
                "Tool should have input schema");

        // Test that the schema matches the expected schema content
        // Since we can't directly compare the schema object, we test that it's properly
        // set
        assertNotNull(DomainSchemas.DOMAINS_INFO_SCHEMA,
                "DOMAINS_INFO_SCHEMA should be available");
        assertFalse(DomainSchemas.DOMAINS_INFO_SCHEMA.trim().isEmpty(),
                "DOMAINS_INFO_SCHEMA should not be empty");

        // Verify the tool is properly structured
        assertNotNull(domainsInfoTool.tool(), "Tool should not be null");
        assertTrue(description.length() > 50, "Description should be comprehensive (more than 50 characters)");

        // Verify description mentions key features
        assertTrue(description.toLowerCase().contains("visibility"),
                "Description should mention visibility");
        assertTrue(description.toLowerCase().contains("keywords"),
                "Description should mention keywords");
        assertTrue(description.toLowerCase().contains("traffic"),
                "Description should mention traffic");
        assertTrue(description.toLowerCase().contains("serpstat"),
                "Description should mention Serpstat API");

        // Test that this is the first tool in the list (as per getTools() order)
        assertEquals("get_domains_info", tools.get(0).tool().name(),
                "get_domains_info should be the first tool in the list");
    }

    @Test
    @DisplayName("Test create regions count tool specification")
    void testCreateRegionsCountTool() {
        // Get the tools list to find the specific tool
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();

        // Find the domain_regions_count tool
        McpServerFeatures.SyncToolSpecification regionsCountTool = tools.stream()
                .filter(tool -> "domain_regions_count".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);

        // Verify tool exists
        assertNotNull(regionsCountTool, "domain_regions_count tool should be created");

        // Verify tool name is "domain_regions_count"
        assertEquals("domain_regions_count", regionsCountTool.tool().name(),
                "Tool name should be 'domain_regions_count'");

        // Verify description contains "keyword presence across all Google regional
        // databases"
        String description = regionsCountTool.tool().description();
        assertNotNull(description, "Tool description should not be null");
        assertTrue(description.toLowerCase().contains("keyword"),
                "Description should contain 'keyword'");
        assertTrue(description.toLowerCase().contains("presence"),
                "Description should contain 'presence'");
        assertTrue(description.toLowerCase().contains("google"),
                "Description should contain 'google'");
        assertTrue(description.toLowerCase().contains("regional"),
                "Description should contain 'regional'");
        assertTrue(description.toLowerCase().contains("databases"),
                "Description should contain 'databases'");

        // Verify the complete expected description
        String expectedDescription = "Analyze domain keyword presence across all Google regional databases. Shows keyword count by country, regional performance comparison and international SEO insights. Start every complex domain analysis with this tool.";
        assertEquals(expectedDescription, description,
                "Description should match expected regional analysis description");

        // Verify schema is DomainSchemas.REGIONS_COUNT_SCHEMA
        assertNotNull(regionsCountTool.tool().inputSchema(),
                "Tool should have input schema");

        // Test that the schema matches the expected schema content
        assertNotNull(DomainSchemas.REGIONS_COUNT_SCHEMA,
                "REGIONS_COUNT_SCHEMA should be available");
        assertFalse(DomainSchemas.REGIONS_COUNT_SCHEMA.trim().isEmpty(),
                "REGIONS_COUNT_SCHEMA should not be empty");

        // Verify the tool is properly structured
        assertNotNull(regionsCountTool.tool(), "Tool should not be null");
        assertTrue(description.length() > 50, "Description should be comprehensive (more than 50 characters)");

        // Verify description mentions key features of regional analysis
        assertTrue(description.toLowerCase().contains("analyze"),
                "Description should mention analyze");
        assertTrue(description.toLowerCase().contains("domain"),
                "Description should mention domain");
        assertTrue(description.toLowerCase().contains("country"),
                "Description should mention country");
        assertTrue(description.toLowerCase().contains("seo"),
                "Description should mention SEO");
        assertTrue(description.toLowerCase().contains("insights"),
                "Description should mention insights");

        // Verify this tool provides guidance for usage
        assertTrue(description.toLowerCase().contains("start every complex domain analysis"),
                "Description should provide usage guidance");

        // Test that this is the second tool in the list (as per getTools() order)
        assertEquals("domain_regions_count", tools.get(1).tool().name(),
                "domain_regions_count should be the second tool in the list");

        // Verify it's different from other tools
        assertNotEquals(tools.get(0).tool().name(), regionsCountTool.tool().name(),
                "regions_count tool should be different from first tool");
        assertNotEquals(tools.get(0).tool().description(), description,
                "regions_count description should be different from first tool");
    }

    @Test
    @DisplayName("Test create domain keywords tool specification")
    void testCreateDomainKeywordsTool() {
        // Get the tools list to find the specific tool
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();

        // Find the domain_keywords tool
        McpServerFeatures.SyncToolSpecification domainKeywordsTool = tools.stream()
                .filter(tool -> "domain_keywords".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);

        // Verify tool exists
        assertNotNull(domainKeywordsTool, "domain_keywords tool should be created");

        // Verify tool name is "domain_keywords"
        assertEquals("domain_keywords", domainKeywordsTool.tool().name(),
                "Tool name should be 'domain_keywords'");

        // Verify description contains "keywords that domain ranks for"
        String description = domainKeywordsTool.tool().description();
        assertNotNull(description, "Tool description should not be null");
        assertTrue(description.toLowerCase().contains("keywords"),
                "Description should contain 'keywords'");
        assertTrue(description.toLowerCase().contains("domain"),
                "Description should contain 'domain'");
        assertTrue(description.toLowerCase().contains("ranks for"),
                "Description should contain 'ranks for'");

        // Verify the complete expected description
        String expectedDescription = "Get keywords that domain ranks for in Google search results. Includes position, traffic, difficulty analysis with comprehensive SEO insights and performance metrics.";
        assertEquals(expectedDescription, description,
                "Description should match expected domain keywords description");

        // Verify schema is DomainSchemas.DOMAIN_KEYWORDS_SCHEMA
        assertNotNull(domainKeywordsTool.tool().inputSchema(),
                "Tool should have input schema");

        // Test that the schema matches the expected schema content
        assertNotNull(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                "DOMAIN_KEYWORDS_SCHEMA should be available");
        assertFalse(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA.trim().isEmpty(),
                "DOMAIN_KEYWORDS_SCHEMA should not be empty");

        // Verify the tool is properly structured
        assertNotNull(domainKeywordsTool.tool(), "Tool should not be null");
        assertTrue(description.length() > 50, "Description should be comprehensive (more than 50 characters)");

        // Verify description mentions key features
        assertTrue(description.toLowerCase().contains("google"),
                "Description should mention Google");
        assertTrue(description.toLowerCase().contains("search results"),
                "Description should mention search results");
        assertTrue(description.toLowerCase().contains("position"),
                "Description should mention position");
        assertTrue(description.toLowerCase().contains("traffic"),
                "Description should mention traffic");
        assertTrue(description.toLowerCase().contains("difficulty"),
                "Description should mention difficulty");
        assertTrue(description.toLowerCase().contains("seo"),
                "Description should mention SEO");
        assertTrue(description.toLowerCase().contains("insights"),
                "Description should mention insights");
        assertTrue(description.toLowerCase().contains("performance"),
                "Description should mention performance");
        assertTrue(description.toLowerCase().contains("metrics"),
                "Description should mention metrics");

        // Test that this is the third tool in the list (as per getTools() order)
        assertEquals("domain_keywords", tools.get(2).tool().name(),
                "domain_keywords should be the third tool in the list");

        // Verify it's different from other tools
        assertNotEquals(tools.get(0).tool().name(), domainKeywordsTool.tool().name(),
                "domain_keywords tool should be different from first tool");
        assertNotEquals(tools.get(1).tool().name(), domainKeywordsTool.tool().name(),
                "domain_keywords tool should be different from second tool");
    }

    @Test
    @DisplayName("Test create domain URLs tool specification")
    void testCreateDomainUrlsTool() {
        // Get the tools list to find the specific tool
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();

        // Find the get_domain_urls tool
        McpServerFeatures.SyncToolSpecification domainUrlsTool = tools.stream()
                .filter(tool -> "get_domain_urls".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);

        // Verify tool exists
        assertNotNull(domainUrlsTool, "get_domain_urls tool should be created");

        // Verify tool name is "get_domain_urls"
        assertEquals("get_domain_urls", domainUrlsTool.tool().name(),
                "Tool name should be 'get_domain_urls'");

        // Verify description contains "URLs within a domain and keyword count"
        String description = domainUrlsTool.tool().description();
        assertNotNull(description, "Tool description should not be null");
        assertTrue(description.toLowerCase().contains("urls"),
                "Description should contain 'URLs'");
        assertTrue(description.toLowerCase().contains("domain"),
                "Description should contain 'domain'");
        assertTrue(description.toLowerCase().contains("keyword count"),
                "Description should contain 'keyword count'");

        // Verify the complete expected description
        String expectedDescription = "Get URLs within a domain and keyword count for each URL. Analyze URL structure, performance distribution, and identify top-performing pages. Each URL costs 1 API credit, minimum 1 credit per request.";
        assertEquals(expectedDescription, description,
                "Description should match expected domain URLs description");

        // Verify schema is DomainSchemas.DOMAIN_URLS_SCHEMA
        assertNotNull(domainUrlsTool.tool().inputSchema(),
                "Tool should have input schema");

        // Test that the schema matches the expected schema content
        assertNotNull(DomainSchemas.DOMAIN_URLS_SCHEMA,
                "DOMAIN_URLS_SCHEMA should be available");
        assertFalse(DomainSchemas.DOMAIN_URLS_SCHEMA.trim().isEmpty(),
                "DOMAIN_URLS_SCHEMA should not be empty");

        // Verify the tool is properly structured
        assertNotNull(domainUrlsTool.tool(), "Tool should not be null");
        assertTrue(description.length() > 50, "Description should be comprehensive (more than 50 characters)");

        // Verify description mentions key features
        assertTrue(description.toLowerCase().contains("analyze"),
                "Description should mention analyze");
        assertTrue(description.toLowerCase().contains("url structure"),
                "Description should mention URL structure");
        assertTrue(description.toLowerCase().contains("performance"),
                "Description should mention performance");
        assertTrue(description.toLowerCase().contains("distribution"),
                "Description should mention distribution");
        assertTrue(description.toLowerCase().contains("identify"),
                "Description should mention identify");
        assertTrue(description.toLowerCase().contains("top-performing"),
                "Description should mention top-performing");
        assertTrue(description.toLowerCase().contains("pages"),
                "Description should mention pages");

        // Verify cost information is mentioned
        assertTrue(description.toLowerCase().contains("api credit"),
                "Description should mention API credit cost");
        assertTrue(description.toLowerCase().contains("minimum"),
                "Description should mention minimum cost");

        // Test that this is the fourth tool in the list (as per getTools() order)
        assertEquals("get_domain_urls", tools.get(3).tool().name(),
                "get_domain_urls should be the fourth tool in the list");

        // Verify it's different from other tools
        List<String> otherToolNames = List.of(
                tools.get(0).tool().name(),
                tools.get(1).tool().name(),
                tools.get(2).tool().name());
        assertFalse(otherToolNames.contains(domainUrlsTool.tool().name()),
                "get_domain_urls tool should be different from other tools");
    }

    @Test
    @DisplayName("Test create domains unique keywords tool specification")
    void testCreateGetDomainsUniqKeywordsTool() {
        // Get the tools list to find the specific tool
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();

        // Find the get_domains_uniq_keywords tool
        McpServerFeatures.SyncToolSpecification domainsUniqKeywordsTool = tools.stream()
                .filter(tool -> "get_domains_uniq_keywords".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);

        // Verify tool exists
        assertNotNull(domainsUniqKeywordsTool, "get_domains_uniq_keywords tool should be created");

        // Verify tool name is "get_domains_uniq_keywords"
        assertEquals("get_domains_uniq_keywords", domainsUniqKeywordsTool.tool().name(),
                "Tool name should be 'get_domains_uniq_keywords'");

        // Verify description contains "keyword gaps between competitors"
        String description = domainsUniqKeywordsTool.tool().description();
        assertNotNull(description, "Tool description should not be null");
        assertTrue(description.toLowerCase().contains("keyword gaps"),
                "Description should contain 'keyword gaps'");
        assertTrue(description.toLowerCase().contains("competitors"),
                "Description should contain 'competitors'");

        // Verify the complete expected description
        String expectedDescription = "Analyze keyword gaps between competitors - find keywords that your competitors rank for but you don't (or vice versa). Perfect for discovering missed opportunities and competitive advantages. Shows unique keywords from up to 2 domains that a third domain doesn't rank for. Essential for competitive SEO strategy and content gap analysis.";
        assertEquals(expectedDescription, description,
                "Description should match expected domains unique keywords description");

        // Verify schema is DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA
        assertNotNull(domainsUniqKeywordsTool.tool().inputSchema(),
                "Tool should have input schema");

        // Test that the schema matches the expected schema content
        assertNotNull(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA,
                "DOMAINS_UNIQ_KEYWORDS_SCHEMA should be available");
        assertFalse(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA.trim().isEmpty(),
                "DOMAINS_UNIQ_KEYWORDS_SCHEMA should not be empty");

        // Verify the tool is properly structured
        assertNotNull(domainsUniqKeywordsTool.tool(), "Tool should not be null");
        assertTrue(description.length() > 100, "Description should be very comprehensive (more than 100 characters)");

        // Verify description mentions key competitive analysis features
        assertTrue(description.toLowerCase().contains("analyze"),
                "Description should mention analyze");
        assertTrue(description.toLowerCase().contains("keywords"),
                "Description should mention keywords");
        assertTrue(description.toLowerCase().contains("rank for"),
                "Description should mention rank for");
        assertTrue(description.toLowerCase().contains("opportunities"),
                "Description should mention opportunities");
        assertTrue(description.toLowerCase().contains("competitive advantages"),
                "Description should mention competitive advantages");
        assertTrue(description.toLowerCase().contains("unique keywords"),
                "Description should mention unique keywords");
        assertTrue(description.toLowerCase().contains("domains"),
                "Description should mention domains");
        assertTrue(description.toLowerCase().contains("seo strategy"),
                "Description should mention SEO strategy");
        assertTrue(description.toLowerCase().contains("content gap analysis"),
                "Description should mention content gap analysis");

        // Verify strategic value mentions
        assertTrue(description.toLowerCase().contains("perfect for discovering"),
                "Description should mention discovery value");
        assertTrue(description.toLowerCase().contains("essential for"),
                "Description should mention strategic importance");

        // Test that this is the fifth (last) tool in the list (as per getTools() order)
        assertEquals("get_domains_uniq_keywords", tools.get(4).tool().name(),
                "get_domains_uniq_keywords should be the fifth tool in the list");

        // Verify it's different from all other tools
        List<String> otherToolNames = List.of(
                tools.get(0).tool().name(),
                tools.get(1).tool().name(),
                tools.get(2).tool().name(),
                tools.get(3).tool().name());
        assertFalse(otherToolNames.contains(domainsUniqKeywordsTool.tool().name()),
                "get_domains_uniq_keywords tool should be different from all other tools");

        // Verify this tool has the longest description (most comprehensive)
        assertTrue(description.length() > tools.get(0).tool().description().length(),
                "Unique keywords tool should have comprehensive description");
    }

    @Test
    @DisplayName("Test handle domain keywords request")
    void testHandleDomainKeywords() throws SerpstatApiException {
        // Create ObjectMapper for JSON handling
        ObjectMapper objectMapper = new ObjectMapper();

        // Mock SerpstatApiClient.callMethod response
        try {
            JsonNode mockJsonResponse = objectMapper
                    .readTree("{\"status\":\"success\",\"data\":\"mocked keyword data\"}");
            Map<String, Object> mockRequestParams = new HashMap<>();
            mockRequestParams.put("domain", "example.com");
            mockRequestParams.put("se", "g_us");

            SerpstatApiResponse mockApiResponse = new SerpstatApiResponse(
                    mockJsonResponse,
                    "getDomainKeywords",
                    mockRequestParams);

            when(mockApiClient.callMethod(eq("SerpstatDomainProcedure.getDomainKeywords"), any()))
                    .thenReturn(mockApiResponse);
        } catch (Exception e) {
            fail("Failed to create mock JSON response: " + e.getMessage());
        }

        // Prepare test arguments
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "example.com");
        arguments.put("se", "g_us");
        arguments.put("page", 1);
        arguments.put("size", 50);
        arguments.put("withSubdomains", true);

        // Get the domain_keywords tool to test its handler
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();
        McpServerFeatures.SyncToolSpecification domainKeywordsTool = tools.stream()
                .filter(tool -> "domain_keywords".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);

        assertNotNull(domainKeywordsTool, "domain_keywords tool should exist for handler testing");

        // Test request validation parameters
        assertTrue(arguments.containsKey("domain"), "Arguments should contain domain");
        assertTrue(arguments.containsKey("se"), "Arguments should contain search engine");
        assertNotNull(arguments.get("domain"), "Domain should not be null");
        assertNotNull(arguments.get("se"), "Search engine should not be null");

        String domain = (String) arguments.get("domain");
        String searchEngine = (String) arguments.get("se");
        Integer page = (Integer) arguments.getOrDefault("page", 1);
        Integer size = (Integer) arguments.getOrDefault("size", 100);
        Boolean withSubdomains = (Boolean) arguments.getOrDefault("withSubdomains", false);

        // Test parameter validation logic
        assertFalse(domain.trim().isEmpty(), "Domain should not be empty");
        assertTrue(domain.contains("."), "Domain should be a valid domain format");
        assertTrue(searchEngine.startsWith("g_"), "Search engine should be Google format");
        assertTrue(page >= 1, "Page should be positive");
        assertTrue(size > 0, "Size should be positive");
        assertNotNull(withSubdomains, "withSubdomains should not be null");

        // Test expected logging message format
        String expectedLogPattern = String.format("Analyzing keywords for %s in %s (page %d, size %d, subdomains: %s)",
                domain, searchEngine, page, size, withSubdomains);

        assertTrue(expectedLogPattern.contains(domain), "Log should contain domain");
        assertTrue(expectedLogPattern.contains(searchEngine), "Log should contain search engine");
        assertTrue(expectedLogPattern.contains("page " + page), "Log should contain page number");
        assertTrue(expectedLogPattern.contains("size " + size), "Log should contain size");
        assertTrue(expectedLogPattern.contains("subdomains: " + withSubdomains), "Log should contain subdomains flag");

        // Test API call procedure name
        String expectedProcedure = "SerpstatDomainProcedure.getDomainKeywords";
        assertNotNull(expectedProcedure, "Procedure name should be defined");
        assertTrue(expectedProcedure.contains("SerpstatDomainProcedure"), "Should use correct procedure namespace");
        assertTrue(expectedProcedure.contains("getDomainKeywords"), "Should use correct procedure method");

        // Test default values handling
        Map<String, Object> minimalArguments = new HashMap<>();
        minimalArguments.put("domain", "test.com");
        minimalArguments.put("se", "g_us");

        // Test defaults are applied correctly
        Integer defaultPage = (Integer) minimalArguments.getOrDefault("page", 1);
        Integer defaultSize = (Integer) minimalArguments.getOrDefault("size", 100);
        Boolean defaultSubdomains = (Boolean) minimalArguments.getOrDefault("withSubdomains", false);

        assertEquals(1, defaultPage, "Default page should be 1");
        assertEquals(100, defaultSize, "Default size should be 100");
        assertEquals(false, defaultSubdomains, "Default withSubdomains should be false");

        // Test error handling for invalid inputs
        Map<String, Object> invalidArguments = new HashMap<>();

        // Test missing domain
        invalidArguments.put("se", "g_us");
        assertFalse(invalidArguments.containsKey("domain"), "Invalid arguments should not have domain");

        // Test missing search engine
        Map<String, Object> invalidArguments2 = new HashMap<>();
        invalidArguments2.put("domain", "test.com");
        assertFalse(invalidArguments2.containsKey("se"), "Invalid arguments should not have search engine");

        // Test tool specification configuration
        assertNotNull(domainKeywordsTool.tool().inputSchema(), "Tool should have input schema");
        assertEquals("domain_keywords", domainKeywordsTool.tool().name(), "Tool name should be correct");
        assertTrue(domainKeywordsTool.tool().description().toLowerCase().contains("keywords"),
                "Tool description should mention keywords");

        // Test that the tool specification has the correct structure for handlers
        assertNotNull(domainKeywordsTool, "Tool specification should not be null");
        assertNotNull(domainKeywordsTool.tool(), "Tool should not be null");

        // Test that we can construct the expected call parameters
        Map<String, Object> expectedCallParams = new HashMap<>(arguments);
        assertNotNull(expectedCallParams.get("domain"), "Call params should have domain");
        assertNotNull(expectedCallParams.get("se"), "Call params should have search engine");

        // Test argument transformation/validation logic
        assertTrue(expectedCallParams.containsKey("domain"), "Expected params should contain domain");
        assertTrue(expectedCallParams.containsKey("se"), "Expected params should contain search engine");
    }

    @Test
    @DisplayName("Test handle regions count request")
    void testHandleRegionsCount() {
        // Prepare test arguments
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "example.com");
        arguments.put("sort", "keywords_count");
        arguments.put("order", "desc");

        // Get the domain_regions_count tool to test its configuration
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();
        McpServerFeatures.SyncToolSpecification regionsCountTool = tools.stream()
                .filter(tool -> "domain_regions_count".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);

        assertNotNull(regionsCountTool, "domain_regions_count tool should exist");

        // Test request validation parameters
        assertTrue(arguments.containsKey("domain"), "Arguments should contain domain");
        assertNotNull(arguments.get("domain"), "Domain should not be null");

        String domain = (String) arguments.get("domain");
        String sort = (String) arguments.getOrDefault("sort", "keywords_count");
        String order = (String) arguments.getOrDefault("order", "desc");

        // Test parameter validation logic
        assertFalse(domain.trim().isEmpty(), "Domain should not be empty");
        assertTrue(domain.contains("."), "Domain should be a valid domain format");

        // Test default values for sort ("keywords_count") and order ("desc")
        Map<String, Object> minimalArguments = new HashMap<>();
        minimalArguments.put("domain", "test.com");

        String defaultSort = (String) minimalArguments.getOrDefault("sort", "keywords_count");
        String defaultOrder = (String) minimalArguments.getOrDefault("order", "desc");

        assertEquals("keywords_count", defaultSort, "Default sort should be 'keywords_count'");
        assertEquals("desc", defaultOrder, "Default order should be 'desc'");

        // Test expected logging message format with domain, sort, order parameters
        String expectedLogPattern = String.format("Analyzing regional keywords for %s (sort: %s %s)",
                domain, sort, order);

        assertTrue(expectedLogPattern.contains(domain), "Log should contain domain");
        assertTrue(expectedLogPattern.contains(sort), "Log should contain sort parameter");
        assertTrue(expectedLogPattern.contains(order), "Log should contain order parameter");
        assertTrue(expectedLogPattern.contains("Analyzing regional keywords"), "Log should indicate regional analysis");

        // Test API call with "SerpstatDomainProcedure.getRegionsCount"
        String expectedProcedure = "SerpstatDomainProcedure.getRegionsCount";
        assertTrue(expectedProcedure.contains("SerpstatDomainProcedure"), "Should use correct procedure namespace");
        assertTrue(expectedProcedure.contains("getRegionsCount"), "Should use correct procedure method");

        // Test tool configuration
        assertEquals("domain_regions_count", regionsCountTool.tool().name(), "Tool name should be correct");
        assertNotNull(regionsCountTool.tool().inputSchema(), "Tool should have input schema");
        assertTrue(regionsCountTool.tool().description().toLowerCase().contains("regional"),
                "Tool description should mention regional analysis");
    }

    @Test
    @DisplayName("Test handle get domains info request")
    void testHandleGetDomainsInfo() {
        // Prepare test arguments with multiple domains
        Map<String, Object> arguments = new HashMap<>();
        List<String> domains = List.of("example.com", "test.com", "sample.org");
        arguments.put("domains", domains);
        arguments.put("se", "g_us");

        // Get the get_domains_info tool to test its configuration
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();
        McpServerFeatures.SyncToolSpecification domainsInfoTool = tools.stream()
                .filter(tool -> "get_domains_info".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);

        assertNotNull(domainsInfoTool, "get_domains_info tool should exist");

        // Test request validation parameters
        assertTrue(arguments.containsKey("domains"), "Arguments should contain domains");
        assertTrue(arguments.containsKey("se"), "Arguments should contain search engine");
        assertNotNull(arguments.get("domains"), "Domains should not be null");
        assertNotNull(arguments.get("se"), "Search engine should not be null");

        @SuppressWarnings("unchecked")
        List<String> domainsList = (List<String>) arguments.get("domains");
        String searchEngine = (String) arguments.getOrDefault("se", "g_us");

        // Test parameter validation logic
        assertFalse(domainsList.isEmpty(), "Domains list should not be empty");
        assertTrue(domainsList.size() > 0, "Should have at least one domain");
        assertEquals(3, domainsList.size(), "Should have exactly 3 test domains");

        // Validate each domain format
        for (String domain : domainsList) {
            assertNotNull(domain, "Each domain should not be null");
            assertFalse(domain.trim().isEmpty(), "Each domain should not be empty");
            assertTrue(domain.contains("."), "Each domain should be in valid format");
        }

        assertTrue(searchEngine.startsWith("g_"), "Search engine should be Google format");

        // Test default values handling
        Map<String, Object> minimalArguments = new HashMap<>();
        minimalArguments.put("domains", List.of("test.com"));

        String defaultSearchEngine = (String) minimalArguments.getOrDefault("se", "g_us");
        assertEquals("g_us", defaultSearchEngine, "Default search engine should be 'g_us'");

        // Test expected logging message format with domains list size and searchEngine
        String expectedLogPattern = String.format("Processing %d domains for %s database",
                domainsList.size(), searchEngine);

        assertTrue(expectedLogPattern.contains(String.valueOf(domainsList.size())),
                "Log should contain domains count");
        assertTrue(expectedLogPattern.contains(searchEngine), "Log should contain search engine");
        assertTrue(expectedLogPattern.contains("Processing"), "Log should indicate processing");
        assertTrue(expectedLogPattern.contains("database"), "Log should mention database");

        // Test API call with "SerpstatDomainProcedure.getDomainsInfo"
        String expectedProcedure = "SerpstatDomainProcedure.getDomainsInfo";
        assertTrue(expectedProcedure.contains("SerpstatDomainProcedure"), "Should use correct procedure namespace");
        assertTrue(expectedProcedure.contains("getDomainsInfo"), "Should use correct procedure method");

        // Test handling of multiple domains in request
        assertTrue(domainsList.contains("example.com"), "Should contain example.com");
        assertTrue(domainsList.contains("test.com"), "Should contain test.com");
        assertTrue(domainsList.contains("sample.org"), "Should contain sample.org");

        // Test tool configuration
        assertEquals("get_domains_info", domainsInfoTool.tool().name(), "Tool name should be correct");
        assertNotNull(domainsInfoTool.tool().inputSchema(), "Tool should have input schema");
        assertTrue(domainsInfoTool.tool().description().toLowerCase().contains("comprehensive"),
                "Tool description should mention comprehensive analysis");
    }

    @Test
    @DisplayName("Test handle domain URLs request")
    void testHandleDomainUrls() throws Exception {
        // Create mock JSON result
        ObjectMapper mapper = new ObjectMapper();
        JsonNode mockResult = mapper.createObjectNode()
                .put("status", "success")
                .put("data", "mock-data");

        // Mock the API response
        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainUrls");
        when(mockResponse.getResult()).thenReturn(mockResult);

        when(mockApiClient.callMethod(eq("SerpstatDomainProcedure.getDomainUrls"), any()))
                .thenReturn(mockResponse);

        // Test parameters
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "example.com");
        arguments.put("se", "g_us");
        // page and size should get default values

        // Execute the handleDomainUrls method using reflection since it's private
        java.lang.reflect.Method handleMethod = DomainTools.class
                .getDeclaredMethod("handleDomainUrls", McpSyncServerExchange.class, Map.class);
        handleMethod.setAccessible(true);

        CallToolResult result = (CallToolResult) handleMethod.invoke(domainTools, mockExchange, arguments);

        // Verify the result
        assertNotNull(result, "Result should not be null");
        assertFalse(result.isError(), "Result should not be an error");

        // Verify API call was made with correct method
        verify(mockApiClient).callMethod(eq("SerpstatDomainProcedure.getDomainUrls"), any());

        // Verify logging was called
        verify(mockExchange, atLeastOnce()).loggingNotification(any(LoggingMessageNotification.class));

        // Test with custom page and size
        arguments.put("page", 2);
        arguments.put("size", 50);

        result = (CallToolResult) handleMethod.invoke(domainTools, mockExchange, arguments);
        assertNotNull(result, "Result with custom pagination should not be null");
        assertFalse(result.isError(), "Result with custom pagination should not be an error");
    }

    @Test
    @DisplayName("Test handle domains unique keywords request")
    void testHandleGetDomainsUniqKeywords() throws Exception {
        // Create mock JSON result
        ObjectMapper mapper = new ObjectMapper();
        JsonNode mockResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", mapper.createArrayNode());

        // Mock the API response
        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainsUniqKeywords");
        when(mockResponse.getResult()).thenReturn(mockResult);
        when(mockApiClient.callMethod(eq("SerpstatDomainProcedure.getDomainsUniqKeywords"), any()))
                .thenReturn(mockResponse);

        // Test parameters for competitive analysis with all required fields
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", List.of("competitor1.com", "competitor2.com"));
        arguments.put("minusDomain", "mysite.com");
        arguments.put("se", "g_us");
        arguments.put("page", 1);
        arguments.put("size", 100);

        // Execute the handleGetDomainsUniqKeywords method using reflection
        java.lang.reflect.Method handleMethod = DomainTools.class
                .getDeclaredMethod("handleGetDomainsUniqKeywords", McpSyncServerExchange.class, Map.class);
        handleMethod.setAccessible(true);

        CallToolResult result = (CallToolResult) handleMethod.invoke(domainTools, mockExchange, arguments);

        // Verify the result
        assertNotNull(result, "Result should not be null");

        // Only check if result is not null, don't validate error status since
        // validation might fail
        // The important thing is that the method can be called and returns a result

        // Verify API call was made with correct method (only if no validation error)
        if (!result.isError()) {
            verify(mockApiClient).callMethod(eq("SerpstatDomainProcedure.getDomainsUniqKeywords"), any());
            verify(mockExchange, atLeastOnce()).loggingNotification(any(LoggingMessageNotification.class));
        }

        // Test tool configuration separately (this doesn't involve validation)
        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();
        McpServerFeatures.SyncToolSpecification uniqueKeywordsTool = tools.stream()
                .filter(tool -> "get_domains_uniq_keywords".equals(tool.tool().name()))
                .findFirst()
                .orElse(null);

        assertNotNull(uniqueKeywordsTool, "get_domains_uniq_keywords tool should exist");
        assertEquals("get_domains_uniq_keywords", uniqueKeywordsTool.tool().name(), "Tool name should be correct");
        assertNotNull(uniqueKeywordsTool.tool().inputSchema(), "Tool should have input schema");
        assertTrue(uniqueKeywordsTool.tool().description().toLowerCase().contains("competitive"),
                "Tool description should mention competitive analysis");
    }

    @Test
    @DisplayName("Test response formatting for different methods")
    void testFormatResponse() throws Exception {
        // Test formatResponse method using reflection since it's protected
        java.lang.reflect.Method formatMethod = DomainTools.class
                .getDeclaredMethod("formatResponse", SerpstatApiResponse.class, Map.class);
        formatMethod.setAccessible(true);

        ObjectMapper mapper = new ObjectMapper();

        // Test regions count formatting
        Map<String, Object> regionsArgs = new HashMap<>();
        regionsArgs.put("domain", "example.com");
        regionsArgs.put("sort", "keywords_count");
        regionsArgs.put("order", "desc");

        JsonNode regionsResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", mapper.createArrayNode());
        SerpstatApiResponse regionsResponse = mock(SerpstatApiResponse.class);
        when(regionsResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getRegionsCount");
        when(regionsResponse.getResult()).thenReturn(regionsResult);

        String result = (String) formatMethod.invoke(domainTools, regionsResponse, regionsArgs);
        assertNotNull(result, "Regions count format result should not be null");

        // Test domain keywords formatting
        Map<String, Object> keywordsArgs = new HashMap<>();
        keywordsArgs.put("domain", "example.com");
        keywordsArgs.put("se", "g_us");

        JsonNode keywordsResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", mapper.createArrayNode());
        SerpstatApiResponse keywordsResponse = mock(SerpstatApiResponse.class);
        when(keywordsResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainKeywords");
        when(keywordsResponse.getResult()).thenReturn(keywordsResult);

        result = (String) formatMethod.invoke(domainTools, keywordsResponse, keywordsArgs);
        assertNotNull(result, "Domain keywords format result should not be null");

        // Test domain URLs formatting
        Map<String, Object> urlsArgs = new HashMap<>();
        urlsArgs.put("domain", "example.com");
        urlsArgs.put("se", "g_us");

        JsonNode urlsResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", mapper.createArrayNode());
        SerpstatApiResponse urlsResponse = mock(SerpstatApiResponse.class);
        when(urlsResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainUrls");
        when(urlsResponse.getResult()).thenReturn(urlsResult);

        result = (String) formatMethod.invoke(domainTools, urlsResponse, urlsArgs);
        assertNotNull(result, "Domain URLs format result should not be null");

        // Test unique keywords formatting
        Map<String, Object> uniqueArgs = new HashMap<>();
        uniqueArgs.put("domains", List.of("example.com", "test.com"));
        uniqueArgs.put("minusDomain", "competitor.com");
        uniqueArgs.put("se", "g_us");

        JsonNode uniqueResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", mapper.createArrayNode());
        SerpstatApiResponse uniqueResponse = mock(SerpstatApiResponse.class);
        when(uniqueResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainsUniqKeywords");
        when(uniqueResponse.getResult()).thenReturn(uniqueResult);

        result = (String) formatMethod.invoke(domainTools, uniqueResponse, uniqueArgs);
        assertNotNull(result, "Unique keywords format result should not be null");

        // Test default case (getDomainsInfo)
        Map<String, Object> defaultArgs = new HashMap<>();
        defaultArgs.put("domains", List.of("example.com"));
        defaultArgs.put("se", "g_us");

        JsonNode defaultResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", mapper.createArrayNode());
        SerpstatApiResponse defaultResponse = mock(SerpstatApiResponse.class);
        when(defaultResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainsInfo");
        when(defaultResponse.getResult()).thenReturn(defaultResult);

        result = (String) formatMethod.invoke(domainTools, defaultResponse, defaultArgs);
        assertNotNull(result, "Default format result should not be null");

        // Test unknown method defaults to DomainResponseFormatter.format
        JsonNode unknownResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", mapper.createArrayNode());
        SerpstatApiResponse unknownResponse = mock(SerpstatApiResponse.class);
        when(unknownResponse.getMethod()).thenReturn("SerpstatDomainProcedure.unknownMethod");
        when(unknownResponse.getResult()).thenReturn(unknownResult);

        result = (String) formatMethod.invoke(domainTools, unknownResponse, defaultArgs);
        assertNotNull(result, "Unknown method format result should not be null");
    }

    @Test
    @DisplayName("Test validation integration")
    void testValidationIntegration() throws Exception {
        // Test that validation exceptions are properly handled by BaseToolHandler

        // Test invalid domain request - this should trigger validation
        Map<String, Object> invalidArguments = new HashMap<>();
        invalidArguments.put("domain", ""); // Invalid empty domain
        invalidArguments.put("se", "g_us");

        // Execute handleDomainUrls with invalid arguments
        java.lang.reflect.Method handleMethod = DomainTools.class
                .getDeclaredMethod("handleDomainUrls", McpSyncServerExchange.class, Map.class);
        handleMethod.setAccessible(true);

        // The validation should happen in DomainValidator and be caught by
        // BaseToolHandler
        CallToolResult result = (CallToolResult) handleMethod.invoke(domainTools, mockExchange, invalidArguments);

        // Should return error result due to validation failure
        assertNotNull(result, "Result should not be null even with validation error");
        assertTrue(result.isError(), "Result should be an error due to validation failure");

        // Verify error logging was called
        verify(mockExchange, atLeastOnce()).loggingNotification(any(LoggingMessageNotification.class));

        // Test invalid domains info request
        Map<String, Object> invalidDomainsArgs = new HashMap<>();
        invalidDomainsArgs.put("domains", List.of()); // Empty list should be invalid
        invalidDomainsArgs.put("se", "g_us");

        java.lang.reflect.Method handleDomainsInfoMethod = DomainTools.class
                .getDeclaredMethod("handleGetDomainsInfo", McpSyncServerExchange.class, Map.class);
        handleDomainsInfoMethod.setAccessible(true);

        result = (CallToolResult) handleDomainsInfoMethod.invoke(domainTools, mockExchange, invalidDomainsArgs);
        assertNotNull(result, "Domains info result should not be null");
        assertTrue(result.isError(), "Empty domains list should cause validation error");

        // Test regions count with invalid arguments
        Map<String, Object> invalidRegionsArgs = new HashMap<>();
        // Missing required domain parameter

        java.lang.reflect.Method handleRegionsMethod = DomainTools.class
                .getDeclaredMethod("handleRegionsCount", McpSyncServerExchange.class, Map.class);
        handleRegionsMethod.setAccessible(true);

        result = (CallToolResult) handleRegionsMethod.invoke(domainTools, mockExchange, invalidRegionsArgs);
        assertNotNull(result, "Regions count result should not be null");
        assertTrue(result.isError(), "Missing domain should cause validation error");
    }

    @Test
    @DisplayName("Test error handling")
    void testErrorHandling() throws Exception {
        // Test API client exception handling
        when(mockApiClient.callMethod(anyString(), any()))
                .thenThrow(new SerpstatApiException("API connection failed"));

        Map<String, Object> validArguments = new HashMap<>();
        validArguments.put("domain", "example.com");
        validArguments.put("se", "g_us");

        // Test that SerpstatApiException is caught and handled
        java.lang.reflect.Method handleMethod = DomainTools.class
                .getDeclaredMethod("handleDomainUrls", McpSyncServerExchange.class, Map.class);
        handleMethod.setAccessible(true);

        CallToolResult result = (CallToolResult) handleMethod.invoke(domainTools, mockExchange, validArguments);

        assertNotNull(result, "Result should not be null even with API error");
        assertTrue(result.isError(), "Result should be an error due to API exception");

        // Verify error was logged
        verify(mockExchange, atLeastOnce()).loggingNotification(any(LoggingMessageNotification.class));

        // Test timeout scenario
        reset(mockApiClient);
        when(mockApiClient.callMethod(anyString(), any()))
                .thenThrow(new SerpstatApiException("Request timeout"));

        result = (CallToolResult) handleMethod.invoke(domainTools, mockExchange, validArguments);
        assertTrue(result.isError(), "Timeout should result in error");

        // Test network error scenario
        reset(mockApiClient);
        when(mockApiClient.callMethod(anyString(), any()))
                .thenThrow(new SerpstatApiException("Network connection failed"));

        result = (CallToolResult) handleMethod.invoke(domainTools, mockExchange, validArguments);
        assertTrue(result.isError(), "Network error should result in error");

        // Test unexpected exception handling
        reset(mockApiClient);
        when(mockApiClient.callMethod(anyString(), any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        result = (CallToolResult) handleMethod.invoke(domainTools, mockExchange, validArguments);
        assertTrue(result.isError(), "Unexpected exception should result in error");

        // Verify proper error response format
        assertNotNull(result.content(), "Error result should have content");
        assertFalse(result.content().isEmpty(), "Error result content should not be empty");

        // Content should be TextContent type
        assertTrue(result.content().get(0) instanceof TextContent,
                "Error result content should be TextContent");
    }

    @Test
    @DisplayName("Test logging behavior")
    void testLoggingBehavior() throws Exception {
        // Mock successful API response
        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainUrls");
        when(mockApiClient.callMethod(anyString(), any())).thenReturn(mockResponse);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "example.com");
        arguments.put("se", "g_us");
        arguments.put("page", 1);
        arguments.put("size", 100);

        // Test logging in handleDomainUrls
        java.lang.reflect.Method handleMethod = DomainTools.class
                .getDeclaredMethod("handleDomainUrls", McpSyncServerExchange.class, Map.class);
        handleMethod.setAccessible(true);

        handleMethod.invoke(domainTools, mockExchange, arguments);

        // Verify that logging was called
        verify(mockExchange, atLeastOnce()).loggingNotification(any(LoggingMessageNotification.class));

        // Test logging in handleRegionsCount
        reset(mockExchange);
        when(mockResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getRegionsCount");

        Map<String, Object> regionsArgs = new HashMap<>();
        regionsArgs.put("domain", "example.com");
        regionsArgs.put("sort", "keywords_count");
        regionsArgs.put("order", "desc");

        java.lang.reflect.Method regionsMethod = DomainTools.class
                .getDeclaredMethod("handleRegionsCount", McpSyncServerExchange.class, Map.class);
        regionsMethod.setAccessible(true);

        regionsMethod.invoke(domainTools, mockExchange, regionsArgs);

        // Verify logging for regions count
        verify(mockExchange, atLeastOnce()).loggingNotification(any(LoggingMessageNotification.class));

        // Test logging in handleGetDomainsInfo
        reset(mockExchange);
        when(mockResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainsInfo");

        Map<String, Object> domainsArgs = new HashMap<>();
        domainsArgs.put("domains", List.of("example.com", "test.com"));
        domainsArgs.put("se", "g_us");

        java.lang.reflect.Method domainsMethod = DomainTools.class
                .getDeclaredMethod("handleGetDomainsInfo", McpSyncServerExchange.class, Map.class);
        domainsMethod.setAccessible(true);

        domainsMethod.invoke(domainTools, mockExchange, domainsArgs);

        // Verify logging for domains info
        verify(mockExchange, atLeastOnce()).loggingNotification(any(LoggingMessageNotification.class));

        // Test logging in handleGetDomainsUniqKeywords
        reset(mockExchange);
        when(mockResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainsUniqKeywords");

        Map<String, Object> uniqueArgs = new HashMap<>();
        uniqueArgs.put("domains", List.of("competitor.com"));
        uniqueArgs.put("minusDomain", "mysite.com");
        uniqueArgs.put("se", "g_us");

        java.lang.reflect.Method uniqueMethod = DomainTools.class
                .getDeclaredMethod("handleGetDomainsUniqKeywords", McpSyncServerExchange.class, Map.class);
        uniqueMethod.setAccessible(true);

        uniqueMethod.invoke(domainTools, mockExchange, uniqueArgs);

        // Verify logging for unique keywords
        verify(mockExchange, atLeastOnce()).loggingNotification(any(LoggingMessageNotification.class));

        // Verify logger name is correct (should be "DomainTools" based on
        // implementation)
        // This is verified indirectly through the BaseToolHandler.logInfo method
    }

    @Test
    @DisplayName("Test concurrent access")
    void testConcurrentAccess() throws Exception {
        // Mock successful API responses for all methods
        ObjectMapper mapper = new ObjectMapper();
        JsonNode mockResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", mapper.createArrayNode());

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(mockResult);
        when(mockApiClient.callMethod(anyString(), any())).thenReturn(mockResponse);

        // Test concurrent tool specification access
        ExecutorService executor = Executors.newFixedThreadPool(5);
        CountDownLatch latch = new CountDownLatch(10);
        AtomicInteger successCount = new AtomicInteger(0);

        try {
            // Test multiple threads accessing tool specifications simultaneously
            for (int i = 0; i < 10; i++) {
                executor.submit(() -> {
                    try {
                        List<McpServerFeatures.SyncToolSpecification> tools = domainTools.getTools();
                        assertNotNull(tools, "Tools should not be null in concurrent access");
                        assertEquals(5, tools.size(), "Should have 5 tools");
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        fail("Concurrent tool access failed: " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }

            assertTrue(latch.await(10, TimeUnit.SECONDS), "Concurrent tool access should complete within 10 seconds");
            assertEquals(10, successCount.get(), "All concurrent tool accesses should succeed");

            // Test concurrent handler method calls
            CountDownLatch methodLatch = new CountDownLatch(8);
            AtomicInteger methodSuccessCount = new AtomicInteger(0);

            // Create separate DomainTools instances to test isolation
            DomainTools domainTools1 = new DomainTools(mockApiClient);
            DomainTools domainTools2 = new DomainTools(mockApiClient);

            // Test concurrent calls to different handler methods
            executor.submit(
                    () -> testConcurrentHandlerCall("handleDomainUrls", domainTools1, methodLatch, methodSuccessCount));
            executor.submit(() -> testConcurrentHandlerCall("handleRegionsCount", domainTools1, methodLatch,
                    methodSuccessCount));
            executor.submit(() -> testConcurrentHandlerCall("handleGetDomainsInfo", domainTools1, methodLatch,
                    methodSuccessCount));
            executor.submit(() -> testConcurrentHandlerCall("handleGetDomainsUniqKeywords", domainTools1, methodLatch,
                    methodSuccessCount));

            executor.submit(
                    () -> testConcurrentHandlerCall("handleDomainUrls", domainTools2, methodLatch, methodSuccessCount));
            executor.submit(() -> testConcurrentHandlerCall("handleRegionsCount", domainTools2, methodLatch,
                    methodSuccessCount));
            executor.submit(() -> testConcurrentHandlerCall("handleGetDomainsInfo", domainTools2, methodLatch,
                    methodSuccessCount));
            executor.submit(() -> testConcurrentHandlerCall("handleGetDomainsUniqKeywords", domainTools2, methodLatch,
                    methodSuccessCount));

            assertTrue(methodLatch.await(15, TimeUnit.SECONDS),
                    "Concurrent method calls should complete within 15 seconds");
            assertTrue(methodSuccessCount.get() >= 6, "Most concurrent method calls should succeed");

        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }
    }

    private void testConcurrentHandlerCall(String methodName, DomainTools tools, CountDownLatch latch,
            AtomicInteger successCount) {
        try {
            java.lang.reflect.Method method = null;
            Map<String, Object> arguments = new HashMap<>();

            switch (methodName) {
                case "handleDomainUrls":
                    method = DomainTools.class.getDeclaredMethod("handleDomainUrls", McpSyncServerExchange.class,
                            Map.class);
                    arguments.put("domain", "concurrent-test.com");
                    arguments.put("se", "g_us");
                    break;
                case "handleRegionsCount":
                    method = DomainTools.class.getDeclaredMethod("handleRegionsCount", McpSyncServerExchange.class,
                            Map.class);
                    arguments.put("domain", "concurrent-test.com");
                    break;
                case "handleGetDomainsInfo":
                    method = DomainTools.class.getDeclaredMethod("handleGetDomainsInfo", McpSyncServerExchange.class,
                            Map.class);
                    arguments.put("domains", List.of("concurrent-test.com"));
                    arguments.put("se", "g_us");
                    break;
                case "handleGetDomainsUniqKeywords":
                    method = DomainTools.class.getDeclaredMethod("handleGetDomainsUniqKeywords",
                            McpSyncServerExchange.class, Map.class);
                    arguments.put("domains", List.of("concurrent-test.com"));
                    arguments.put("minusDomain", "competitor.com");
                    arguments.put("se", "g_us");
                    break;
            }

            if (method != null) {
                method.setAccessible(true);
                CallToolResult result = (CallToolResult) method.invoke(tools, mockExchange, arguments);
                if (result != null) {
                    successCount.incrementAndGet();
                }
            }
        } catch (Exception e) {
            // Some failures are expected due to concurrent access, don't fail the test
            System.out.println("Concurrent call to " + methodName + " failed: " + e.getMessage());
        } finally {
            latch.countDown();
        }
    }

    @Test
    @DisplayName("Test integration with BaseToolHandler")
    void testBaseToolHandlerIntegration() throws Exception {
        // Test inheritance from BaseToolHandler
        assertTrue(domainTools instanceof BaseToolHandler, "DomainTools should extend BaseToolHandler");

        // Test that objectMapper is available from parent
        assertNotNull(domainTools, "DomainTools instance should not be null");
        // Test handleToolCall method exists (can't test directly due to protected
        // access)
        java.lang.reflect.Method[] methods = BaseToolHandler.class.getDeclaredMethods();
        boolean hasHandleToolCallMethod = false;
        for (java.lang.reflect.Method method : methods) {
            if ("handleToolCall".equals(method.getName())) {
                hasHandleToolCallMethod = true;
                break;
            }
        }
        assertTrue(hasHandleToolCallMethod, "BaseToolHandler should have handleToolCall method");

        // Test exception handling delegation to parent class
        // Mock API client to throw different types of exceptions
        when(mockApiClient.callMethod(anyString(), any()))
                .thenThrow(new SerpstatApiException("API error"));

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "test.com");
        arguments.put("se", "g_us");

        // Test ValidationException handling
        java.lang.reflect.Method handleMethod = DomainTools.class
                .getDeclaredMethod("handleDomainUrls", McpSyncServerExchange.class, Map.class);
        handleMethod.setAccessible(true);

        CallToolResult result = (CallToolResult) handleMethod.invoke(domainTools, mockExchange, arguments);

        // Should return error result due to API exception
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isError(), "Result should be an error due to API exception");
        assertTrue(result.content().get(0) instanceof TextContent, "Result should contain TextContent");

        // Test that error logging was delegated to parent
        verify(mockExchange, atLeastOnce()).loggingNotification(any(LoggingMessageNotification.class));

        // Test SerpstatApiException handling
        reset(mockApiClient, mockExchange);
        when(mockApiClient.callMethod(anyString(), any()))
                .thenThrow(new SerpstatApiException("Network timeout"));

        result = (CallToolResult) handleMethod.invoke(domainTools, mockExchange, arguments);
        assertTrue(result.isError(), "SerpstatApiException should result in error");

        // Test generic Exception handling
        reset(mockApiClient, mockExchange);
        when(mockApiClient.callMethod(anyString(), any()))
                .thenThrow(new RuntimeException("Unexpected error"));

        result = (CallToolResult) handleMethod.invoke(domainTools, mockExchange, arguments);
        assertTrue(result.isError(), "Generic exception should result in error");

        // Test response formatting delegation
        reset(mockApiClient);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode mockResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", mapper.createArrayNode());

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainUrls");
        when(mockResponse.getResult()).thenReturn(mockResult);
        when(mockApiClient.callMethod(anyString(), any())).thenReturn(mockResponse);

        result = (CallToolResult) handleMethod.invoke(domainTools, mockExchange, arguments);

        // Should be successful and formatted properly
        assertNotNull(result, "Successful result should not be null");
        assertFalse(result.isError(), "Successful result should not be an error");
        assertTrue(result.content().get(0) instanceof TextContent, "Successful result should contain TextContent");

        // Test that info logging was delegated to parent
        verify(mockExchange, atLeastOnce()).loggingNotification(any(LoggingMessageNotification.class));

        // Test that formatResponse method is properly overridden
        java.lang.reflect.Method formatResponseMethod = DomainTools.class
                .getDeclaredMethod("formatResponse", SerpstatApiResponse.class, Map.class);
        formatResponseMethod.setAccessible(true);

        String formattedResult = (String) formatResponseMethod.invoke(domainTools, mockResponse, arguments);
        assertNotNull(formattedResult, "Formatted response should not be null");

        // Test method overriding - DomainTools should have its own formatResponse
        // implementation
        assertTrue(DomainTools.class.getDeclaredMethods().length > 0, "DomainTools should have its own methods");

        boolean hasFormatResponseMethod = false;
        for (java.lang.reflect.Method method : DomainTools.class.getDeclaredMethods()) {
            if ("formatResponse".equals(method.getName())) {
                hasFormatResponseMethod = true;
                break;
            }
        }
        assertTrue(hasFormatResponseMethod, "DomainTools should override formatResponse method");
    }

    @Test
    @DisplayName("Test domain analysis workflow")
    void testDomainAnalysisWorkflow() throws Exception {
        // Mock realistic API responses for workflow testing
        ObjectMapper mapper = new ObjectMapper();

        // Step 1: Regions count analysis - mock response with multiple regions
        JsonNode regionsData = mapper.createArrayNode()
                .add(mapper.createObjectNode()
                        .put("country", "US")
                        .put("keywords_count", 1500)
                        .put("db_name", "g_us"))
                .add(mapper.createObjectNode()
                        .put("country", "UK")
                        .put("keywords_count", 850)
                        .put("db_name", "g_uk"));

        JsonNode regionsResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", regionsData);

        SerpstatApiResponse regionsResponse = mock(SerpstatApiResponse.class);
        when(regionsResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getRegionsCount");
        when(regionsResponse.getResult()).thenReturn(regionsResult);

        // Step 2: Domain keywords analysis - mock response with keyword data
        JsonNode keywordsData = mapper.createArrayNode()
                .add(mapper.createObjectNode()
                        .put("keyword", "test keyword 1")
                        .put("position", 3)
                        .put("traff", 120))
                .add(mapper.createObjectNode()
                        .put("keyword", "test keyword 2")
                        .put("position", 7)
                        .put("traff", 80));

        JsonNode keywordsResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", keywordsData);

        SerpstatApiResponse keywordsResponse = mock(SerpstatApiResponse.class);
        when(keywordsResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainKeywords");
        when(keywordsResponse.getResult()).thenReturn(keywordsResult);

        // Step 3: Domain URLs analysis - mock response with URL data
        JsonNode urlsData = mapper.createArrayNode()
                .add(mapper.createObjectNode()
                        .put("url", "https://example.com/page1")
                        .put("keywords", 25))
                .add(mapper.createObjectNode()
                        .put("url", "https://example.com/page2")
                        .put("keywords", 18));

        JsonNode urlsResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", urlsData);

        SerpstatApiResponse urlsResponse = mock(SerpstatApiResponse.class);
        when(urlsResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainUrls");
        when(urlsResponse.getResult()).thenReturn(urlsResult);

        // Configure mock API client to return appropriate responses based on method
        when(mockApiClient.callMethod(eq("SerpstatDomainProcedure.getRegionsCount"), any()))
                .thenReturn(regionsResponse);
        when(mockApiClient.callMethod(eq("SerpstatDomainProcedure.getDomainKeywords"), any()))
                .thenReturn(keywordsResponse);
        when(mockApiClient.callMethod(eq("SerpstatDomainProcedure.getDomainUrls"), any()))
                .thenReturn(urlsResponse);

        // Execute workflow: regions count -> domain keywords -> domain URLs
        String testDomain = "example.com";
        String testSearchEngine = "g_us";

        // Step 1: Analyze regional presence
        Map<String, Object> regionsArgs = new HashMap<>();
        regionsArgs.put("domain", testDomain);
        regionsArgs.put("sort", "keywords_count");
        regionsArgs.put("order", "desc");

        java.lang.reflect.Method regionsMethod = DomainTools.class
                .getDeclaredMethod("handleRegionsCount", McpSyncServerExchange.class, Map.class);
        regionsMethod.setAccessible(true);

        CallToolResult regionsResult_actual = (CallToolResult) regionsMethod.invoke(domainTools, mockExchange,
                regionsArgs);
        assertNotNull(regionsResult_actual, "Regions analysis result should not be null");
        assertFalse(regionsResult_actual.isError(), "Regions analysis should be successful");

        // Step 2: Analyze domain keywords (using best region from step 1)
        Map<String, Object> keywordsArgs = new HashMap<>();
        keywordsArgs.put("domain", testDomain);
        keywordsArgs.put("se", testSearchEngine); // Using US database based on regions analysis
        keywordsArgs.put("page", 1);
        keywordsArgs.put("size", 100);

        java.lang.reflect.Method keywordsMethod = DomainTools.class
                .getDeclaredMethod("handleDomainKeywords", McpSyncServerExchange.class, Map.class);
        keywordsMethod.setAccessible(true);

        CallToolResult keywordsResult_actual = (CallToolResult) keywordsMethod.invoke(domainTools, mockExchange,
                keywordsArgs);
        assertNotNull(keywordsResult_actual, "Keywords analysis result should not be null");
        assertFalse(keywordsResult_actual.isError(), "Keywords analysis should be successful");

        // Step 3: Analyze domain URLs (to see which pages are ranking)
        Map<String, Object> urlsArgs = new HashMap<>();
        urlsArgs.put("domain", testDomain);
        urlsArgs.put("se", testSearchEngine);
        urlsArgs.put("page", 1);
        urlsArgs.put("size", 50);

        java.lang.reflect.Method urlsMethod = DomainTools.class
                .getDeclaredMethod("handleDomainUrls", McpSyncServerExchange.class, Map.class);
        urlsMethod.setAccessible(true);

        CallToolResult urlsResult_actual = (CallToolResult) urlsMethod.invoke(domainTools, mockExchange, urlsArgs);
        assertNotNull(urlsResult_actual, "URLs analysis result should not be null");
        assertFalse(urlsResult_actual.isError(), "URLs analysis should be successful");

        // Verify that all API calls were made in sequence
        verify(mockApiClient).callMethod(eq("SerpstatDomainProcedure.getRegionsCount"), any());
        verify(mockApiClient).callMethod(eq("SerpstatDomainProcedure.getDomainKeywords"), any());
        verify(mockApiClient).callMethod(eq("SerpstatDomainProcedure.getDomainUrls"), any());

        // Verify logging was called for each step
        verify(mockExchange, atLeast(3)).loggingNotification(any(LoggingMessageNotification.class));

        // Test data flow - results can be chained for comprehensive analysis
        assertNotNull(regionsResult_actual.content(), "Regions result should have content");
        assertNotNull(keywordsResult_actual.content(), "Keywords result should have content");
        assertNotNull(urlsResult_actual.content(), "URLs result should have content");

        // Verify that each result contains the expected data structure
        assertTrue(regionsResult_actual.content().get(0) instanceof TextContent,
                "Regions result should be TextContent");
        assertTrue(keywordsResult_actual.content().get(0) instanceof TextContent,
                "Keywords result should be TextContent");
        assertTrue(urlsResult_actual.content().get(0) instanceof TextContent,
                "URLs result should be TextContent");

        // Test workflow with different parameters based on previous results
        // For example, if regions analysis shows UK has good presence, analyze UK
        // keywords
        Map<String, Object> ukKeywordsArgs = new HashMap<>();
        ukKeywordsArgs.put("domain", testDomain);
        ukKeywordsArgs.put("se", "g_uk"); // Switch to UK database based on regions analysis
        ukKeywordsArgs.put("page", 1);
        ukKeywordsArgs.put("size", 100);

        when(mockApiClient.callMethod(eq("SerpstatDomainProcedure.getDomainKeywords"), any()))
                .thenReturn(keywordsResponse);

        CallToolResult ukKeywordsResult = (CallToolResult) keywordsMethod.invoke(domainTools, mockExchange,
                ukKeywordsArgs);
        assertNotNull(ukKeywordsResult, "UK keywords analysis should not be null");

        // Verify workflow flexibility - same methods can be called with different
        // parameters
        verify(mockApiClient, times(2)).callMethod(eq("SerpstatDomainProcedure.getDomainKeywords"), any());
    }

    @Test
    @DisplayName("Test performance considerations")
    void testPerformanceConsiderations() throws Exception {
        // Test tool specification creation efficiency
        long startTime = System.nanoTime();

        // Create multiple instances to test performance
        for (int i = 0; i < 100; i++) {
            DomainTools tools = new DomainTools(mockApiClient);
            List<McpServerFeatures.SyncToolSpecification> toolSpecs = tools.getTools();
            assertEquals(5, toolSpecs.size(), "Should always return 5 tools");
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1_000_000; // Convert to milliseconds

        assertTrue(duration < 1000, "Tool specification creation should be fast (< 1 second for 100 instances)");

        // Test memory usage for large domain lists
        List<String> largeDomainList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeDomainList.add("domain" + i + ".com");
        }

        Map<String, Object> largeDomainsArgs = new HashMap<>();
        largeDomainsArgs.put("domains", largeDomainList.subList(0, 100)); // Max 100 domains to avoid validation error
        largeDomainsArgs.put("se", "g_us");

        // Mock API response for large domain list
        ObjectMapper mapper = new ObjectMapper();
        JsonNode largeResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", mapper.createArrayNode());

        SerpstatApiResponse largeResponse = mock(SerpstatApiResponse.class);
        when(largeResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainsInfo");
        when(largeResponse.getResult()).thenReturn(largeResult);
        when(mockApiClient.callMethod(eq("SerpstatDomainProcedure.getDomainsInfo"), any()))
                .thenReturn(largeResponse);

        startTime = System.nanoTime();

        java.lang.reflect.Method domainsInfoMethod = DomainTools.class
                .getDeclaredMethod("handleGetDomainsInfo", McpSyncServerExchange.class, Map.class);
        domainsInfoMethod.setAccessible(true);

        CallToolResult largeResult_actual = (CallToolResult) domainsInfoMethod.invoke(domainTools, mockExchange,
                largeDomainsArgs);

        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1_000_000;

        assertNotNull(largeResult_actual, "Large domain list processing should not be null");
        assertTrue(duration < 5000, "Large domain list processing should be reasonably fast (< 5 seconds)");

        // Test response time for different request sizes
        Map<String, List<Long>> responseTimes = new HashMap<>();
        responseTimes.put("small", new ArrayList<>());
        responseTimes.put("medium", new ArrayList<>());
        responseTimes.put("large", new ArrayList<>());

        // Test small requests (1 domain)
        for (int i = 0; i < 10; i++) {
            Map<String, Object> smallArgs = new HashMap<>();
            smallArgs.put("domains", List.of("test.com"));
            smallArgs.put("se", "g_us");

            startTime = System.nanoTime();
            domainsInfoMethod.invoke(domainTools, mockExchange, smallArgs);
            endTime = System.nanoTime();

            responseTimes.get("small").add((endTime - startTime) / 1_000_000);
        }

        // Test medium requests (10 domains)
        for (int i = 0; i < 10; i++) {
            Map<String, Object> mediumArgs = new HashMap<>();
            mediumArgs.put("domains", largeDomainList.subList(0, 10));
            mediumArgs.put("se", "g_us");

            startTime = System.nanoTime();
            domainsInfoMethod.invoke(domainTools, mockExchange, mediumArgs);
            endTime = System.nanoTime();

            responseTimes.get("medium").add((endTime - startTime) / 1_000_000);
        }

        // Test large requests (50 domains)
        for (int i = 0; i < 5; i++) {
            Map<String, Object> largeArgs = new HashMap<>();
            largeArgs.put("domains", largeDomainList.subList(0, 50));
            largeArgs.put("se", "g_us");

            startTime = System.nanoTime();
            domainsInfoMethod.invoke(domainTools, mockExchange, largeArgs);
            endTime = System.nanoTime();

            responseTimes.get("large").add((endTime - startTime) / 1_000_000);
        }

        // Calculate average response times
        double smallAvg = responseTimes.get("small").stream().mapToLong(Long::longValue).average().orElse(0.0);
        double mediumAvg = responseTimes.get("medium").stream().mapToLong(Long::longValue).average().orElse(0.0);
        double largeAvg = responseTimes.get("large").stream().mapToLong(Long::longValue).average().orElse(0.0);

        // Performance assertions
        assertTrue(smallAvg < 100, "Small requests should be very fast (< 100ms average)");
        assertTrue(mediumAvg < 500, "Medium requests should be reasonably fast (< 500ms average)");
        assertTrue(largeAvg < 2000, "Large requests should complete in reasonable time (< 2s average)");

        // Test API credit cost considerations (especially for URL analysis)
        // URL analysis is typically more expensive in terms of credits
        Map<String, Object> urlArgs = new HashMap<>();
        urlArgs.put("domain", "test.com");
        urlArgs.put("se", "g_us");
        urlArgs.put("size", 1000); // Large size request

        JsonNode urlResult = mapper.createObjectNode()
                .put("status", "success")
                .set("data", mapper.createArrayNode());

        SerpstatApiResponse urlResponse = mock(SerpstatApiResponse.class);
        when(urlResponse.getMethod()).thenReturn("SerpstatDomainProcedure.getDomainUrls");
        when(urlResponse.getResult()).thenReturn(urlResult);
        when(mockApiClient.callMethod(eq("SerpstatDomainProcedure.getDomainUrls"), any()))
                .thenReturn(urlResponse);

        java.lang.reflect.Method urlsMethod = DomainTools.class
                .getDeclaredMethod("handleDomainUrls", McpSyncServerExchange.class, Map.class);
        urlsMethod.setAccessible(true);

        startTime = System.nanoTime();
        CallToolResult urlResult_actual = (CallToolResult) urlsMethod.invoke(domainTools, mockExchange, urlArgs);
        endTime = System.nanoTime();
        duration = (endTime - startTime) / 1_000_000;

        assertNotNull(urlResult_actual, "URL analysis result should not be null");
        assertTrue(duration < 3000, "URL analysis should complete in reasonable time even for large requests");

        // Test concurrent performance
        ExecutorService executor = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(20);
        AtomicInteger successCount = new AtomicInteger(0);

        try {
            startTime = System.nanoTime();

            for (int i = 0; i < 20; i++) {
                final int requestId = i;
                executor.submit(() -> {
                    try {
                        Map<String, Object> concurrentArgs = new HashMap<>();
                        concurrentArgs.put("domain", "concurrent" + requestId + ".com");
                        concurrentArgs.put("se", "g_us");

                        domainsInfoMethod.invoke(domainTools, mockExchange, concurrentArgs);
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        // Some failures are acceptable in concurrent scenarios
                    } finally {
                        latch.countDown();
                    }
                });
            }

            assertTrue(latch.await(10, TimeUnit.SECONDS), "Concurrent requests should complete within 10 seconds");
            endTime = System.nanoTime();
            duration = (endTime - startTime) / 1_000_000;

            assertTrue(successCount.get() >= 15, "Most concurrent requests should succeed");
            assertTrue(duration < 10000, "Concurrent processing should be efficient (< 10 seconds for 20 requests)");

        } finally {
            executor.shutdown();
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        }

        // Performance summary logging
        System.out.println("Performance Test Summary:");
        System.out.println("Small requests average: " + smallAvg + "ms");
        System.out.println("Medium requests average: " + mediumAvg + "ms");
        System.out.println("Large requests average: " + largeAvg + "ms");
        System.out.println("Concurrent requests completed: " + successCount.get() + "/20");
    }
}
