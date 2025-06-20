package com.serpstat.core;

import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ToolProvider interface
 * Tests interface contract, implementation behavior, integration with ToolRegistry, 
 * error handling, and tool specification validation.
 */
@DisplayName("ToolProvider Tests")
class ToolProviderTest {

    private ToolProvider mockToolProvider;
    private McpServerFeatures.SyncToolSpecification testTool;
    private SerpstatApiClient mockApiClient;    // Helper method to create mock tools (same as ToolRegistryTest)
    @SuppressWarnings({"unchecked", "rawtypes"})
    static McpServerFeatures.SyncToolSpecification createMockTool(String name, String description) {
        McpSchema.Tool tool = new McpSchema.Tool(name, description, (McpSchema.JsonSchema) null);
        BiFunction<Object, Map<String, Object>, McpSchema.CallToolResult> call =
                (exchange, args) -> new McpSchema.CallToolResult("result text", false);
        // Cast to the required type for SyncToolSpecification
        return new McpServerFeatures.SyncToolSpecification(tool, (BiFunction) call);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Create a test tool specification
        testTool = createMockTool("test_tool", "Test tool for unit testing");

        // Create mock tool provider using the existing MockToolProvider
        mockToolProvider = new MockToolProvider("Test Domain", List.of(testTool));
        
        // Create mock API client for ToolRegistry tests
        mockApiClient = new SerpstatApiClient("test-token");
    }

    @Test
    @DisplayName("Test ToolProvider interface contract")
    void testToolProviderInterfaceContract() {
        // Test that interface methods are properly defined and accessible
        assertNotNull(mockToolProvider.getDomainName());
        assertNotNull(mockToolProvider.getTools());
        
        // Verify interface contract
        assertTrue(mockToolProvider instanceof ToolProvider);
        
        // Test method return types (implicitly tested by compilation)
        String domainName = mockToolProvider.getDomainName();
        List<McpServerFeatures.SyncToolSpecification> tools = mockToolProvider.getTools();
        
        assertInstanceOf(String.class, domainName);
        assertInstanceOf(List.class, tools);
    }

    @Test
    @DisplayName("Test ToolProvider implementation behavior")
    void testToolProviderImplementationBehavior() {
        // Test domain name behavior
        assertEquals("Test Domain", mockToolProvider.getDomainName());
        
        // Test tools behavior
        List<McpServerFeatures.SyncToolSpecification> tools = mockToolProvider.getTools();
        assertEquals(1, tools.size());
        assertEquals("test_tool", tools.get(0).tool().name());
        assertEquals("Test tool for unit testing", tools.get(0).tool().description());
    }

    @Test
    @DisplayName("Test ToolProvider integration with ToolRegistry")
    void testToolProviderIntegration() {
        // Create ToolRegistry with mock API client
        ToolRegistry registry = new ToolRegistry(mockApiClient);
        
        // Verify registry has built-in providers
        assertTrue(registry.getToolCount() > 0);
        assertTrue(registry.getDomainCount() > 0);
        
        // Test that tools are accessible through registry
        assertTrue(registry.getToolCount() >= registry.getDomainCount());
    }

    @Test
    @DisplayName("Test ToolProvider error handling")
    void testToolProviderErrorHandling() {
        // Test behavior with null domain name
        ToolProvider nullDomainProvider = new MockToolProvider(null, List.of(testTool));
        assertNull(nullDomainProvider.getDomainName());
        assertNotNull(nullDomainProvider.getTools());
        
        // Test behavior with empty tools list
        ToolProvider emptyToolsProvider = new MockToolProvider("Empty Domain", Collections.emptyList());
        assertEquals("Empty Domain", emptyToolsProvider.getDomainName());
        assertTrue(emptyToolsProvider.getTools().isEmpty());
        
        // Test behavior with null tools list
        ToolProvider nullToolsProvider = new MockToolProvider("Null Tools Domain", null);
        assertEquals("Null Tools Domain", nullToolsProvider.getDomainName());
        assertNull(nullToolsProvider.getTools());
    }

    @Test
    @DisplayName("Test ToolProvider tool specification validation")
    void testToolSpecificationValidation() {
        // Test with valid tool specification
        McpServerFeatures.SyncToolSpecification validTool = createMockTool("valid_tool", "Valid tool description");
        
        ToolProvider validProvider = new MockToolProvider("Valid Domain", List.of(validTool));
        List<McpServerFeatures.SyncToolSpecification> tools = validProvider.getTools();
        
        assertNotNull(tools);
        assertEquals(1, tools.size());
        
        McpServerFeatures.SyncToolSpecification retrievedTool = tools.get(0);
        assertEquals("valid_tool", retrievedTool.tool().name());
        assertEquals("Valid tool description", retrievedTool.tool().description());
    }
    
    @Test
    @DisplayName("Test ToolProvider with multiple tools")
    void testToolProviderWithMultipleTools() {
        McpServerFeatures.SyncToolSpecification tool1 = createMockTool("tool_1", "First tool");
        McpServerFeatures.SyncToolSpecification tool2 = createMockTool("tool_2", "Second tool");
        
        ToolProvider multiToolProvider = new MockToolProvider("Multi Tool Domain", Arrays.asList(tool1, tool2));
        
        assertEquals("Multi Tool Domain", multiToolProvider.getDomainName());
        
        List<McpServerFeatures.SyncToolSpecification> tools = multiToolProvider.getTools();
        assertEquals(2, tools.size());
        assertTrue(tools.contains(tool1));
        assertTrue(tools.contains(tool2));
    }
    
    @Test
    @DisplayName("Test ToolProvider immutability expectations")
    void testToolProviderImmutabilityExpectations() {
        // Test that the same instance returns consistent results
        String domainName1 = mockToolProvider.getDomainName();
        String domainName2 = mockToolProvider.getDomainName();
        assertEquals(domainName1, domainName2);
        
        List<McpServerFeatures.SyncToolSpecification> tools1 = mockToolProvider.getTools();
        List<McpServerFeatures.SyncToolSpecification> tools2 = mockToolProvider.getTools();
        assertEquals(tools1, tools2);
    }
    
    @Test
    @DisplayName("Test ToolProvider domain name constraints")
    void testDomainNameConstraints() {
        // Test empty domain name
        ToolProvider emptyDomainProvider = new MockToolProvider("", List.of(testTool));
        assertEquals("", emptyDomainProvider.getDomainName());
        
        // Test whitespace domain name
        ToolProvider whitespaceDomainProvider = new MockToolProvider("   ", List.of(testTool));
        assertEquals("   ", whitespaceDomainProvider.getDomainName());
        
        // Test long domain name
        String longDomainName = "A".repeat(1000);
        ToolProvider longDomainProvider = new MockToolProvider(longDomainName, List.of(testTool));
        assertEquals(longDomainName, longDomainProvider.getDomainName());
    }
      @Test
    @DisplayName("Test ToolProvider tool access methods")
    void testToolAccessMethods() {
        // Test tool access through provider
        McpServerFeatures.SyncToolSpecification tool = mockToolProvider.getTools().get(0);
        
        // Verify tool properties
        assertNotNull(tool.tool());
        assertEquals("test_tool", tool.tool().name());
        assertEquals("Test tool for unit testing", tool.tool().description());
        
        // Verify tool has implementation (not null)
        assertNotNull(tool);
    }
    
    @Test
    @DisplayName("Test ToolProvider toString behavior")
    void testToStringBehavior() {
        // Test that toString doesn't throw exceptions
        assertDoesNotThrow(() -> mockToolProvider.toString());
        
        String toString = mockToolProvider.toString();
        assertNotNull(toString);
        assertTrue(toString.length() > 0);
    }
}
