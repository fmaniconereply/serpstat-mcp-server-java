package com.serpstat.domains.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serpstat.core.SerpstatApiResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DomainResponseFormatter class
 * 
 * These tests cover various scenarios including JSON serialization, summary
 * statistics,
 * argument context integration, data transformation, and performance with large
 * responses.
 */
@DisplayName("DomainResponseFormatter Tests")
class DomainResponseFormatterTest {

    @Mock
    private SerpstatApiResponse mockResponse;

    @Mock
    private ObjectMapper mockMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test JSON serialization")
    void testJsonSerialization() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Test with special characters and Unicode
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        ObjectNode domain = mapper.createObjectNode();
        domain.put("domain", "тест-домен.рф"); // Cyrillic domain
        domain.put("visible", 42.5);
        domain.put("traff", 1500L);
        domain.put("keywords", 250);
        dataArray.add(domain);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("тест-домен.рф"));
        arguments.put("se", "g_ru");

        // Act
        String result = DomainResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        assertTrue(result.contains("тест-домен.рф"));

        // Test that JSON is valid and can be parsed back
        JsonNode parsed = mapper.readTree(result);
        assertNotNull(parsed);
        assertEquals("success", parsed.get("status").asText());

        // Test ObjectMapper integration
        ObjectNode testNode = mapper.createObjectNode();
        testNode.put("test", "value");
        assertNotNull(testNode);

        ArrayNode testArray = mapper.createArrayNode();
        testArray.add("item");
        assertEquals(1, testArray.size());
    }

    @Test
    @DisplayName("Test summary statistics calculation")
    void testSummaryStatisticsCalculation() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Test with various data scenarios
        ObjectNode domain1 = mapper.createObjectNode();
        domain1.put("domain", "domain1.com");
        domain1.put("visible", 100.5);
        domain1.put("traff", 15000L);
        domain1.put("keywords", 500);
        dataArray.add(domain1);

        ObjectNode domain2 = mapper.createObjectNode();
        domain2.put("domain", "domain2.com");
        domain2.put("visible", 50.25);
        domain2.put("traff", 8000L);
        domain2.put("keywords", 300);
        dataArray.add(domain2);

        // Domain with zero values
        ObjectNode domain3 = mapper.createObjectNode();
        domain3.put("domain", "domain3.com");
        domain3.put("visible", 0.0);
        domain3.put("traff", 0L);
        domain3.put("keywords", 0);
        dataArray.add(domain3);

        // Domain with missing values (should be handled gracefully)
        ObjectNode domain4 = mapper.createObjectNode();
        domain4.put("domain", "domain4.com");
        // Missing visible, traff, keywords fields
        dataArray.add(domain4);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("domain1.com", "domain2.com", "domain3.com", "domain4.com"));
        arguments.put("se", "g_us");

        // Act
        String result = DomainResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        JsonNode formattedResponse = mapper.readTree(result);
        JsonNode summary = formattedResponse.get("summary");
        assertNotNull(summary);

        // Total calculations
        assertEquals(150.75, summary.get("total_visibility").asDouble(), 0.01);
        assertEquals(23000L, summary.get("total_estimated_traffic").asLong());
        assertEquals(800, summary.get("total_keywords").asInt());

        // Average calculations (should handle division by total domains)
        assertEquals(37.69, summary.get("average_visibility").asDouble(), 0.01);

        // Test empty domain list
        ObjectNode emptyResultNode = mapper.createObjectNode();
        emptyResultNode.set("data", mapper.createArrayNode());

        SerpstatApiResponse emptyMockResponse = mock(SerpstatApiResponse.class);
        when(emptyMockResponse.getResult()).thenReturn(emptyResultNode);

        arguments.put("domains", Arrays.asList());
        String emptyResult = DomainResponseFormatter.format(emptyMockResponse, arguments, mapper);
        JsonNode emptyFormatted = mapper.readTree(emptyResult);

        if (emptyFormatted.has("summary")) {
            JsonNode emptySummary = emptyFormatted.get("summary");
            assertEquals(0.0, emptySummary.get("total_visibility").asDouble());
            assertEquals(0.0, emptySummary.get("average_visibility").asDouble());
        }
    }

    @Test
    @DisplayName("Test argument context integration")
    void testArgumentContextIntegration() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        ObjectNode domain = mapper.createObjectNode();
        domain.put("domain", "example.com");
        domain.put("visible", 75.0);
        dataArray.add(domain);

        resultNode.set("data", dataArray);

        // Add pagination info
        ObjectNode summaryInfo = mapper.createObjectNode();
        summaryInfo.put("left_lines", 9500L);
        summaryInfo.put("page", 2);
        resultNode.set("summary_info", summaryInfo);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        // Test with various request parameters
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("example.com", "notfound.com"));
        arguments.put("se", "g_uk");
        arguments.put("customParam", "testValue");

        // Act
        String result = DomainResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        JsonNode formattedResponse = mapper.readTree(result);

        // Check request parameter extraction
        assertEquals("g_uk", formattedResponse.get("search_engine").asText());
        assertEquals(2, formattedResponse.get("requested_domains_count").asInt());
        assertEquals(1, formattedResponse.get("found_domains_count").asInt());

        // Check pagination context preservation
        JsonNode apiInfo = formattedResponse.get("api_info");
        assertNotNull(apiInfo);
        assertEquals(2, apiInfo.get("page").asInt());
        assertEquals(9500L, apiInfo.get("credits_remaining").asLong());

        // Test default search engine handling
        arguments.remove("se");
        String resultDefault = DomainResponseFormatter.format(mockResponse, arguments, mapper);
        JsonNode formattedDefault = mapper.readTree(resultDefault);
        assertEquals("g_us", formattedDefault.get("search_engine").asText());
    }

    @Test
    @DisplayName("Test data transformation")
    void testDataTransformation() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Test various data types and transformations
        ObjectNode rawDomain = mapper.createObjectNode();
        rawDomain.put("domain", "test.com");
        rawDomain.put("visible", "45.67"); // String that should be converted to number
        rawDomain.put("traff", 12500); // Number
        rawDomain.put("keywords", "350"); // String number
        rawDomain.putNull("extra_field"); // Null value
        dataArray.add(rawDomain);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("test.com"));
        arguments.put("se", "g_us");

        // Act
        String result = DomainResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        JsonNode formattedResponse = mapper.readTree(result);

        // Check that raw data is preserved in domains array
        JsonNode domainsArray = formattedResponse.get("domains");
        assertNotNull(domainsArray);
        assertTrue(domainsArray.isArray());
        assertEquals(1, domainsArray.size());

        JsonNode firstDomain = domainsArray.get(0);
        assertEquals("test.com", firstDomain.get("domain").asText());

        // Check that summary calculations handle string-to-number conversion
        JsonNode summary = formattedResponse.get("summary");
        assertNotNull(summary);
        assertTrue(summary.get("total_visibility").asDouble() > 0);

        // Test field restructuring in summary
        assertTrue(summary.has("total_visibility"));
        assertTrue(summary.has("total_estimated_traffic"));
        assertTrue(summary.has("total_keywords"));
        assertTrue(summary.has("average_visibility"));

        // Test handling of missing fields
        ObjectNode domainWithMissingFields = mapper.createObjectNode();
        domainWithMissingFields.put("domain", "incomplete.com");
        // Missing visibility, traffic, keywords

        ArrayNode incompleteArray = mapper.createArrayNode();
        incompleteArray.add(domainWithMissingFields);

        ObjectNode incompleteResult = mapper.createObjectNode();
        incompleteResult.set("data", incompleteArray);

        SerpstatApiResponse incompleteMockResponse = mock(SerpstatApiResponse.class);
        when(incompleteMockResponse.getResult()).thenReturn(incompleteResult);

        String incompleteResultString = DomainResponseFormatter.format(incompleteMockResponse, arguments, mapper);
        JsonNode incompleteFormatted = mapper.readTree(incompleteResultString);

        // Should handle missing fields gracefully with default values
        JsonNode incompleteSummary = incompleteFormatted.get("summary");
        assertNotNull(incompleteSummary);
        assertEquals(0.0, incompleteSummary.get("total_visibility").asDouble());
    }

    @Test
    @DisplayName("Test format consistency across methods")
    void testFormatConsistencyAcrossMethods() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Test format method
        ObjectNode domainsResult = mapper.createObjectNode();
        ArrayNode domainsArray = mapper.createArrayNode();
        ObjectNode domain = mapper.createObjectNode();
        domain.put("domain", "test.com");
        domain.put("visible", 50.0);
        domainsArray.add(domain);
        domainsResult.set("data", domainsArray);

        SerpstatApiResponse domainsResponse = mock(SerpstatApiResponse.class);
        when(domainsResponse.getResult()).thenReturn(domainsResult);

        Map<String, Object> domainsArgs = new HashMap<>();
        domainsArgs.put("domains", Arrays.asList("test.com"));
        domainsArgs.put("se", "g_us");

        // Test formatRegionsCount method
        ObjectNode regionsResult = mapper.createObjectNode();
        ArrayNode regionsArray = mapper.createArrayNode();
        ObjectNode region = mapper.createObjectNode();
        region.put("keywords_count", 100);
        region.put("country_name_en", "United States");
        regionsArray.add(region);
        regionsResult.set("data", regionsArray);

        SerpstatApiResponse regionsResponse = mock(SerpstatApiResponse.class);
        when(regionsResponse.getResult()).thenReturn(regionsResult);

        Map<String, Object> regionsArgs = new HashMap<>();
        regionsArgs.put("domain", "test.com");

        // Test formatDomainKeywords method
        ObjectNode keywordsResult = mapper.createObjectNode();
        ArrayNode keywordsArray = mapper.createArrayNode();
        ObjectNode keyword = mapper.createObjectNode();
        keyword.put("keyword", "test");
        keyword.put("position", 5);
        keywordsArray.add(keyword);
        keywordsResult.set("data", keywordsArray);

        SerpstatApiResponse keywordsResponse = mock(SerpstatApiResponse.class);
        when(keywordsResponse.getResult()).thenReturn(keywordsResult);

        Map<String, Object> keywordsArgs = new HashMap<>();
        keywordsArgs.put("domain", "test.com");
        keywordsArgs.put("se", "g_us");

        // Act
        String domainsFormatted = DomainResponseFormatter.format(domainsResponse, domainsArgs, mapper);
        String regionsFormatted = DomainResponseFormatter.formatRegionsCount(regionsResponse, regionsArgs, mapper);
        String keywordsFormatted = DomainResponseFormatter.formatDomainKeywords(keywordsResponse, keywordsArgs, mapper);

        // Assert consistency
        JsonNode domainsJson = mapper.readTree(domainsFormatted);
        JsonNode regionsJson = mapper.readTree(regionsFormatted);
        JsonNode keywordsJson = mapper.readTree(keywordsFormatted);

        // All should have consistent status field
        assertEquals("success", domainsJson.get("status").asText());
        assertEquals("success", regionsJson.get("status").asText());
        assertEquals("success", keywordsJson.get("status").asText());

        // All should have method field with consistent naming pattern
        assertTrue(domainsJson.get("method").asText().startsWith("SerpstatDomainProcedure."));
        assertTrue(regionsJson.get("method").asText().startsWith("SerpstatDomainProcedure."));
        assertTrue(keywordsJson.get("method").asText().startsWith("SerpstatDomainProcedure."));

        // All should be valid JSON
        assertNotNull(domainsJson);
        assertNotNull(regionsJson);
        assertNotNull(keywordsJson);

        // Check consistent field types
        assertTrue(domainsJson.get("status").isTextual());
        assertTrue(regionsJson.get("status").isTextual());
        assertTrue(keywordsJson.get("status").isTextual());
    }

    @Test
    @DisplayName("Test performance with large responses")
    void testPerformanceWithLargeResponses() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create 100 domains (maximum according to API)
        for (int i = 1; i <= 100; i++) {
            ObjectNode domain = mapper.createObjectNode();
            domain.put("domain", "domain" + i + ".com");
            domain.put("visible", Math.random() * 100);
            domain.put("traff", (long) (Math.random() * 100000));
            domain.put("keywords", (int) (Math.random() * 1000));
            dataArray.add(domain);
        }

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        // Create 100 domain arguments
        List<String> domains = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            domains.add("domain" + i + ".com");
        }

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", domains);
        arguments.put("se", "g_us");

        // Act & Assert performance
        long startTime = System.currentTimeMillis();
        String result = DomainResponseFormatter.format(mockResponse, arguments, mapper);
        long endTime = System.currentTimeMillis();

        // Performance assertions
        long executionTime = endTime - startTime;
        assertTrue(executionTime < 5000, "Formatting should complete within 5 seconds for 100 domains");

        // Memory usage test (basic check)
        assertNotNull(result);
        assertTrue(result.length() > 0);

        // Verify result structure with large data
        JsonNode formattedResponse = mapper.readTree(result);
        assertEquals(100, formattedResponse.get("found_domains_count").asInt());
        assertEquals(100, formattedResponse.get("requested_domains_count").asInt());

        // Test ObjectMapper performance optimization
        JsonNode domainsArray = formattedResponse.get("domains");
        assertEquals(100, domainsArray.size());

        // Verify summary calculations work with large datasets
        JsonNode summary = formattedResponse.get("summary");
        assertNotNull(summary);
        assertTrue(summary.get("total_visibility").asDouble() >= 0);
        assertTrue(summary.get("total_estimated_traffic").asLong() >= 0);
        assertTrue(summary.get("total_keywords").asInt() >= 0);
    }

    @Test
    @DisplayName("Test internationalization support")
    void testInternationalizationSupport() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Test Unicode domain names
        ObjectNode domain1 = mapper.createObjectNode();
        domain1.put("domain", "тест.рф"); // Cyrillic
        domain1.put("visible", 45.5);
        domain1.put("traff", 12000L);
        domain1.put("keywords", 300);
        dataArray.add(domain1);

        ObjectNode domain2 = mapper.createObjectNode();
        domain2.put("domain", "测试.中国"); // Chinese
        domain2.put("visible", 30.25);
        domain2.put("traff", 8000L);
        domain2.put("keywords", 200);
        dataArray.add(domain2);

        ObjectNode domain3 = mapper.createObjectNode();
        domain3.put("domain", "テスト.jp"); // Japanese
        domain3.put("visible", 25.75);
        domain3.put("traff", 5000L);
        domain3.put("keywords", 150);
        dataArray.add(domain3);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("тест.рф", "测试.中国", "テスト.jp"));
        arguments.put("se", "g_us");

        // Act
        String result = DomainResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert Unicode handling
        assertNotNull(result);
        assertTrue(result.contains("тест.рф"));
        assertTrue(result.contains("测试.中国"));
        assertTrue(result.contains("テスト.jp"));

        JsonNode formattedResponse = mapper.readTree(result);

        // Verify Unicode domains are properly preserved in JSON
        JsonNode domainsArray = formattedResponse.get("domains");
        boolean foundCyrillic = false, foundChinese = false, foundJapanese = false;

        for (JsonNode domainNode : domainsArray) {
            String domainName = domainNode.get("domain").asText();
            if (domainName.equals("тест.рф"))
                foundCyrillic = true;
            if (domainName.equals("测试.中国"))
                foundChinese = true;
            if (domainName.equals("テスト.jp"))
                foundJapanese = true;
        }

        assertTrue(foundCyrillic, "Cyrillic domain should be preserved");
        assertTrue(foundChinese, "Chinese domain should be preserved");
        assertTrue(foundJapanese, "Japanese domain should be preserved");

        // Test summary calculations work with international domains
        JsonNode summary = formattedResponse.get("summary");
        assertNotNull(summary);
        assertEquals(101.5, summary.get("total_visibility").asDouble(), 0.01);
        assertEquals(25000L, summary.get("total_estimated_traffic").asLong());
        assertEquals(650, summary.get("total_keywords").asInt());

        // Test different search engines for different regions
        arguments.put("se", "g_ru");
        String resultRu = DomainResponseFormatter.format(mockResponse, arguments, mapper);
        JsonNode formattedRu = mapper.readTree(resultRu);
        assertEquals("g_ru", formattedRu.get("search_engine").asText());
    }

    @Test
    @DisplayName("Test format domains info response")
    void testFormatDomainsInfoResponse() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Create mock API response data
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Add two domain entries
        ObjectNode domain1 = mapper.createObjectNode();
        domain1.put("domain", "example.com");
        domain1.put("visible", 45.67);
        domain1.put("traff", 12500L);
        domain1.put("keywords", 350);
        dataArray.add(domain1);

        ObjectNode domain2 = mapper.createObjectNode();
        domain2.put("domain", "test.com");
        domain2.put("visible", 23.45);
        domain2.put("traff", 8700L);
        domain2.put("keywords", 180);
        dataArray.add(domain2);

        resultNode.set("data", dataArray);

        // Add summary info
        ObjectNode summaryInfo = mapper.createObjectNode();
        summaryInfo.put("left_lines", 9500L);
        summaryInfo.put("page", 1);
        resultNode.set("summary_info", summaryInfo);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        // Create arguments
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("example.com", "test.com"));
        arguments.put("se", "g_us");

        // Act
        String result = DomainResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        assertEquals("success", formattedResponse.get("status").asText());
        assertEquals("SerpstatDomainProcedure.getDomainsInfo", formattedResponse.get("method").asText());
        assertEquals("g_us", formattedResponse.get("search_engine").asText());
        assertEquals(2, formattedResponse.get("requested_domains_count").asInt());
        assertEquals(2, formattedResponse.get("found_domains_count").asInt());
        assertEquals(10, formattedResponse.get("estimated_credits_used").asInt()); // 2 domains * 5 credits

        // Check summary calculations
        JsonNode summary = formattedResponse.get("summary");
        assertNotNull(summary);
        assertEquals(69.12, summary.get("total_visibility").asDouble(), 0.01);
        assertEquals(21200L, summary.get("total_estimated_traffic").asLong());
        assertEquals(530, summary.get("total_keywords").asInt());
        assertEquals(34.56, summary.get("average_visibility").asDouble(), 0.01);

        // Check API info
        JsonNode apiInfo = formattedResponse.get("api_info");
        assertNotNull(apiInfo);
        assertEquals(9500L, apiInfo.get("credits_remaining").asLong());
        assertEquals(1, apiInfo.get("page").asInt());
    }

    @Test
    @DisplayName("Test format regions count response")
    void testFormatRegionsCountResponse() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Create mock API response data
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Add regional data
        ObjectNode region1 = mapper.createObjectNode();
        region1.put("keywords_count", 1500);
        region1.put("country_name_en", "United States");
        region1.put("db_name", "g_us");
        dataArray.add(region1);

        ObjectNode region2 = mapper.createObjectNode();
        region2.put("keywords_count", 800);
        region2.put("country_name_en", "United Kingdom");
        region2.put("db_name", "g_uk");
        dataArray.add(region2);

        ObjectNode region3 = mapper.createObjectNode();
        region3.put("keywords_count", 0);
        region3.put("country_name_en", "Germany");
        region3.put("db_name", "g_de");
        dataArray.add(region3);

        resultNode.set("data", dataArray);

        // Add summary info
        ObjectNode summaryInfo = mapper.createObjectNode();
        summaryInfo.put("regions_db_count", 15);
        summaryInfo.put("total_keywords", 2300L);
        summaryInfo.put("left_lines", 9800L);
        resultNode.set("summary_info", summaryInfo);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        // Create arguments
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "example.com");
        arguments.put("sort", "keywords_count");
        arguments.put("order", "desc");

        // Act
        String result = DomainResponseFormatter.formatRegionsCount(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        assertEquals("success", formattedResponse.get("status").asText());
        assertEquals("SerpstatDomainProcedure.getRegionsCount", formattedResponse.get("method").asText());
        assertEquals("example.com", formattedResponse.get("analyzed_domain").asText());
        assertEquals("keywords_count", formattedResponse.get("sort_field").asText());
        assertEquals("desc", formattedResponse.get("sort_order").asText());
        assertEquals(3, formattedResponse.get("regions_found").asInt());

        // Check analytics
        JsonNode analytics = formattedResponse.get("analytics");
        assertNotNull(analytics);

        JsonNode summary = analytics.get("summary");
        assertEquals(2300L, summary.get("total_keywords_across_regions").asLong());
        assertEquals(2, summary.get("regions_with_keywords").asInt());
        assertEquals(1, summary.get("regions_without_keywords").asInt());
        assertEquals(1150.0, summary.get("average_keywords_per_region").asDouble(), 0.01);
        assertEquals(1500, summary.get("max_keywords_in_region").asInt());
        assertEquals(800, summary.get("min_keywords_in_region").asInt());
        assertEquals("United States", summary.get("top_performing_region").asText());
        assertEquals("g_us", summary.get("top_performing_database").asText());

        // Check insights
        JsonNode insights = analytics.get("insights");
        assertEquals("LIMITED_REGIONS", insights.get("status").asText());
        assertTrue(insights.get("message").asText().contains("2 regions"));

        // Check API info
        JsonNode apiInfo = formattedResponse.get("api_info");
        assertEquals(15, apiInfo.get("total_databases_checked").asInt());
        assertEquals(2300L, apiInfo.get("api_total_keywords").asLong());
        assertEquals(9800L, apiInfo.get("credits_remaining").asLong());
    }

    @Test
    @DisplayName("Test format domain keywords response")
    void testFormatDomainKeywordsResponse() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Create mock API response data
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Add keyword entries
        ObjectNode keyword1 = mapper.createObjectNode();
        keyword1.put("keyword", "test keyword");
        keyword1.put("position", 3);
        keyword1.put("traff", 150);
        keyword1.put("cost", 1.25);
        keyword1.put("difficulty", 45);
        keyword1.put("concurrency", 80);
        keyword1.put("keyword_length", 2);
        ArrayNode intents1 = mapper.createArrayNode();
        intents1.add("informational");
        keyword1.set("intents", intents1);
        ArrayNode types1 = mapper.createArrayNode();
        types1.add("organic");
        types1.add("featured_snippet");
        keyword1.set("types", types1);
        dataArray.add(keyword1);

        ObjectNode keyword2 = mapper.createObjectNode();
        keyword2.put("keyword", "another test");
        keyword2.put("position", 15);
        keyword2.put("traff", 80);
        keyword2.put("cost", 0.75);
        keyword2.put("difficulty", 30);
        keyword2.put("concurrency", 60);
        keyword2.put("keyword_length", 2);
        ArrayNode intents2 = mapper.createArrayNode();
        intents2.add("commercial");
        keyword2.set("intents", intents2);
        ArrayNode types2 = mapper.createArrayNode();
        types2.add("organic");
        keyword2.set("types", types2);
        dataArray.add(keyword2);

        resultNode.set("data", dataArray);

        // Add summary info
        ObjectNode summaryInfo = mapper.createObjectNode();
        summaryInfo.put("total", 250);
        summaryInfo.put("page", 1);
        summaryInfo.put("left_lines", 9700L);
        resultNode.set("summary_info", summaryInfo);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        // Create arguments
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "example.com");
        arguments.put("se", "g_us");
        arguments.put("page", 1);
        arguments.put("size", 100);
        arguments.put("withSubdomains", false);
        arguments.put("withIntents", true);

        // Act
        String result = DomainResponseFormatter.formatDomainKeywords(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        assertEquals("success", formattedResponse.get("status").asText());
        assertEquals("SerpstatDomainProcedure.getDomainKeywords", formattedResponse.get("method").asText());
        assertEquals("example.com", formattedResponse.get("analyzed_domain").asText());
        assertEquals("g_us", formattedResponse.get("search_engine").asText());
        assertEquals(1, formattedResponse.get("page").asInt());
        assertEquals(100, formattedResponse.get("page_size").asInt());
        assertEquals(false, formattedResponse.get("with_subdomains").asBoolean());
        assertEquals(true, formattedResponse.get("with_intents").asBoolean());
        assertEquals(2, formattedResponse.get("keywords_on_page").asInt());

        // Check analytics
        JsonNode analytics = formattedResponse.get("analytics");
        assertNotNull(analytics);

        JsonNode summary = analytics.get("summary");
        assertEquals(230L, summary.get("total_traffic_estimate").asLong());
        assertEquals(2.0, summary.get("total_cost_estimate").asDouble(), 0.01);
        assertEquals(37.5, summary.get("average_difficulty").asDouble(), 0.01);
        assertEquals(70.0, summary.get("average_concurrency").asDouble(), 0.01);

        // Check position distribution
        JsonNode positions = analytics.get("position_distribution");
        assertEquals(1, positions.get("top_3_positions").asInt());
        assertEquals(1, positions.get("first_page_positions").asInt());
        assertEquals(1, positions.get("second_page_positions").asInt());
        assertEquals(0, positions.get("beyond_second_page").asInt());
        assertEquals(50.0, positions.get("top_3_percentage").asDouble(), 0.01);
        assertEquals(50.0, positions.get("first_page_percentage").asDouble(), 0.01);

        // Check keyword length distribution
        JsonNode lengthDist = analytics.get("keyword_length_distribution");
        assertEquals(2, lengthDist.get("2_words").asInt());

        // Check intent distribution
        JsonNode intentDist = analytics.get("intent_distribution");
        assertEquals(1, intentDist.get("informational").asInt());
        assertEquals(1, intentDist.get("commercial").asInt());

        // Check SERP features
        JsonNode serpFeatures = analytics.get("serp_features");
        assertEquals(2, serpFeatures.get("organic").asInt());
        assertEquals(1, serpFeatures.get("featured_snippet").asInt());

        // Check insights
        JsonNode insights = analytics.get("insights");
        assertEquals("MODERATE", insights.get("status").asText());
        assertTrue(insights.get("message").asText().contains("50.0%"));

        // Check API info
        JsonNode apiInfo = formattedResponse.get("api_info");
        assertEquals(250, apiInfo.get("total_keywords_found").asInt());
        assertEquals(1, apiInfo.get("current_page").asInt());
        assertEquals(9700L, apiInfo.get("credits_remaining").asLong());
        assertEquals(3, apiInfo.get("total_pages").asInt()); // ceil(250/100)
        assertEquals(true, apiInfo.get("has_next_page").asBoolean());
        assertEquals(2, apiInfo.get("credits_used_this_request").asInt());
    }

    @Test
    @DisplayName("Test response structure validation")
    void testResponseStructureValidation() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();
        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("example.com"));
        arguments.put("se", "g_us");

        // Act
        String result = DomainResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        // Check required fields are present
        assertTrue(formattedResponse.has("status"));
        assertTrue(formattedResponse.has("method"));
        assertTrue(formattedResponse.has("search_engine"));
        assertTrue(formattedResponse.has("requested_domains_count"));
        assertTrue(formattedResponse.has("found_domains_count"));
        assertTrue(formattedResponse.has("domains"));
        assertTrue(formattedResponse.has("estimated_credits_used"));

        // Check data types
        assertTrue(formattedResponse.get("status").isTextual());
        assertTrue(formattedResponse.get("method").isTextual());
        assertTrue(formattedResponse.get("search_engine").isTextual());
        assertTrue(formattedResponse.get("requested_domains_count").isNumber());
        assertTrue(formattedResponse.get("found_domains_count").isNumber());
        assertTrue(formattedResponse.get("domains").isArray());
        assertTrue(formattedResponse.get("estimated_credits_used").isNumber());
    }

    @Test
    @DisplayName("Test error handling for malformed responses")
    void testErrorHandlingForMalformedResponses() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Test with null result node
        SerpstatApiResponse mockResponseNull = mock(SerpstatApiResponse.class);
        when(mockResponseNull.getResult()).thenReturn(null);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("example.com"));
        arguments.put("se", "g_us");

        // Act & Assert - should handle null gracefully
        assertThrows(Exception.class, () -> {
            DomainResponseFormatter.format(mockResponseNull, arguments, mapper);
        });

        // Test with missing data field
        ObjectNode resultNodeMissingData = mapper.createObjectNode();
        // No "data" field

        SerpstatApiResponse mockResponseMissingData = mock(SerpstatApiResponse.class);
        when(mockResponseMissingData.getResult()).thenReturn(resultNodeMissingData);

        // Should handle missing data field gracefully
        String result = DomainResponseFormatter.format(mockResponseMissingData, arguments, mapper);
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);
        assertEquals(0, formattedResponse.get("found_domains_count").asInt());
        assertTrue(formattedResponse.get("domains").isArray());
        assertEquals(0, formattedResponse.get("domains").size());
    }

    @Test
    @DisplayName("Test edge cases")
    void testEdgeCases() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Test empty API response
        ObjectNode resultNodeEmpty = mapper.createObjectNode();
        ArrayNode emptyArray = mapper.createArrayNode();
        resultNodeEmpty.set("data", emptyArray);

        SerpstatApiResponse mockResponseEmpty = mock(SerpstatApiResponse.class);
        when(mockResponseEmpty.getResult()).thenReturn(resultNodeEmpty);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("nonexistent.domain"));
        arguments.put("se", "g_us");

        // Act
        String result = DomainResponseFormatter.format(mockResponseEmpty, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);
        assertEquals(0, formattedResponse.get("found_domains_count").asInt());
        assertEquals(0, formattedResponse.get("domains").size());

        // Test summary with empty data
        JsonNode summary = formattedResponse.get("summary");
        if (summary != null) {
            assertEquals(0.0, summary.get("total_visibility").asDouble());
            assertEquals(0L, summary.get("total_estimated_traffic").asLong());
            assertEquals(0, summary.get("total_keywords").asInt());
            assertEquals(0.0, summary.get("average_visibility").asDouble());
        }

        // Test single domain response
        ObjectNode resultNodeSingle = mapper.createObjectNode();
        ArrayNode singleArray = mapper.createArrayNode();
        ObjectNode singleDomain = mapper.createObjectNode();
        singleDomain.put("domain", "single.com");
        singleDomain.put("visible", 100.0);
        singleDomain.put("traff", 50000L);
        singleDomain.put("keywords", 1000);
        singleArray.add(singleDomain);
        resultNodeSingle.set("data", singleArray);

        SerpstatApiResponse mockResponseSingle = mock(SerpstatApiResponse.class);
        when(mockResponseSingle.getResult()).thenReturn(resultNodeSingle);

        arguments.put("domains", Arrays.asList("single.com"));

        String resultSingle = DomainResponseFormatter.format(mockResponseSingle, arguments, mapper);
        JsonNode formattedSingle = mapper.readTree(resultSingle);

        assertEquals(1, formattedSingle.get("found_domains_count").asInt());
        assertEquals(1, formattedSingle.get("requested_domains_count").asInt());

        JsonNode singleSummary = formattedSingle.get("summary");
        assertNotNull(singleSummary);
        assertEquals(100.0, singleSummary.get("total_visibility").asDouble());
        assertEquals(100.0, singleSummary.get("average_visibility").asDouble()); // Same as total for single domain
    }
}
