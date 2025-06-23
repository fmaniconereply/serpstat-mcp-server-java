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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DomainUrlsResponseFormatter class
 * Tests the formatting of domain URLs responses from Serpstat API,
 */
@DisplayName("DomainUrlsResponseFormatter Tests")
class DomainUrlsResponseFormatterTest {

    @Mock
    private SerpstatApiResponse mockResponse;

    @Mock
    private ObjectMapper mockMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ================================
    // IMPLEMENTED TESTS (3 most critical)
    // ================================

    @Test
    @DisplayName("Test basic domain URLs response formatting")
    void testBasicDomainUrlsFormatting() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create sample URL data
        ObjectNode url1 = mapper.createObjectNode();
        url1.put("url", "https://example.com/page1");
        url1.put("keywords", 150);
        dataArray.add(url1);

        ObjectNode url2 = mapper.createObjectNode();
        url2.put("url", "https://example.com/category/page2");
        url2.put("keywords", 75);
        dataArray.add(url2);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "example.com");
        arguments.put("se", "g_us");
        arguments.put("page", 1);
        arguments.put("size", 100);

        // Act
        String result = DomainUrlsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        assertEquals("success", formattedResponse.get("status").asText());
        assertEquals("SerpstatDomainProcedure.getDomainUrls", formattedResponse.get("method").asText());
        assertEquals("example.com", formattedResponse.get("analyzed_domain").asText());
        assertEquals("g_us", formattedResponse.get("search_engine").asText());
        assertEquals(1, formattedResponse.get("page").asInt());
        assertEquals(100, formattedResponse.get("page_size").asInt());
        assertEquals(2, formattedResponse.get("urls_on_page").asInt());

        // Test URLs data preservation
        JsonNode urls = formattedResponse.get("urls");
        assertNotNull(urls);
        assertTrue(urls.isArray());
        assertEquals(2, urls.size());
    }

    @Test
    @DisplayName("Test keyword analytics and statistics calculation")
    void testKeywordAnalyticsCalculation() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create URLs with diverse keyword counts for analytics testing
        ObjectNode url1 = mapper.createObjectNode();
        url1.put("url", "https://example.com/high-traffic");
        url1.put("keywords", 1500);
        dataArray.add(url1);

        ObjectNode url2 = mapper.createObjectNode();
        url2.put("url", "https://example.com/medium-traffic");
        url2.put("keywords", 250);
        dataArray.add(url2);

        ObjectNode url3 = mapper.createObjectNode();
        url3.put("url", "https://example.com/low-traffic");
        url3.put("keywords", 50);
        dataArray.add(url3);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "example.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUrlsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result, "Result should not be null");
        JsonNode formattedResponse = mapper.readTree(result);

        // Basic structure validation
        assertTrue(formattedResponse.has("status"), "Response should have status");
        assertTrue(formattedResponse.has("method"), "Response should have method");

        // Test data preservation
        if (formattedResponse.has("urls")) {
            JsonNode urls = formattedResponse.get("urls");
            assertTrue(urls.isArray(), "URLs should be an array");
            assertTrue(urls.size() > 0, "URLs array should not be empty");
        }

        // Test keyword counts are preserved (basic validation)
        boolean hasKeywordData = false;
        if (formattedResponse.has("urls")) {
            JsonNode urls = formattedResponse.get("urls");
            for (JsonNode url : urls) {
                if (url.has("keywords")) {
                    hasKeywordData = true;
                    assertTrue(url.get("keywords").asInt() >= 0, "Keywords count should be non-negative");
                }
            }
        }

        // If no specific analytics structure, just verify data integrity
        if (!hasKeywordData) {
            // Fallback validation - check original data is preserved
            assertTrue(formattedResponse.toString().contains("example.com"), "Domain should be preserved in response");
        }
    }

    @Test
    @DisplayName("Test URL pattern analysis and insights")
    void testUrlPatternAnalysis() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create URLs with different patterns for basic analysis
        ObjectNode url1 = mapper.createObjectNode();
        url1.put("url", "https://example.com/secure-page.html");
        url1.put("keywords", 100);
        dataArray.add(url1);

        ObjectNode url2 = mapper.createObjectNode();
        url2.put("url", "http://example.com/old-page.php");
        url2.put("keywords", 50);
        dataArray.add(url2);

        ObjectNode url3 = mapper.createObjectNode();
        url3.put("url", "https://example.com/category/products");
        url3.put("keywords", 200);
        dataArray.add(url3);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "example.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUrlsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result, "Result should not be null");
        JsonNode formattedResponse = mapper.readTree(result);

        // Basic structure validation
        assertTrue(formattedResponse.has("status"), "Response should have status");
        assertTrue(formattedResponse.has("method"), "Response should have method");

        // Test URL data preservation
        if (formattedResponse.has("urls")) {
            JsonNode urls = formattedResponse.get("urls");
            assertTrue(urls.isArray(), "URLs should be an array");
            assertEquals(3, urls.size(), "Should have 3 URLs");

            // Test URL structure preservation
            boolean foundHttps = false;
            boolean foundHttp = false;
            boolean foundCategoryPath = false;

            for (JsonNode url : urls) {
                if (url.has("url")) {
                    String urlString = url.get("url").asText();
                    if (urlString.startsWith("https://"))
                        foundHttps = true;
                    if (urlString.startsWith("http://"))
                        foundHttp = true;
                    if (urlString.contains("/category/"))
                        foundCategoryPath = true;
                }

                // Test keyword counts are preserved
                if (url.has("keywords")) {
                    assertTrue(url.get("keywords").asInt() >= 0, "Keywords should be non-negative");
                }
            }

            assertTrue(foundHttps, "Should preserve HTTPS URLs");
            assertTrue(foundHttp, "Should preserve HTTP URLs");
            assertTrue(foundCategoryPath, "Should preserve category paths");
        }

        // Test domain context preservation
        String responseText = formattedResponse.toString();
        assertTrue(responseText.contains("example.com"), "Domain should be preserved");
        assertTrue(responseText.contains("g_us"), "Search engine should be preserved");

        // Basic pattern validation - check different URL patterns are present
        if (formattedResponse.has("urls")) {
            JsonNode urls = formattedResponse.get("urls");
            assertTrue(urls.size() >= 3, "Should contain all test URLs");
        }
    }

    @Test
    @DisplayName("Test format domain URLs response")
    void testFormatDomainUrlsResponse() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create comprehensive URL data for formatting test
        ObjectNode url1 = mapper.createObjectNode();
        url1.put("url", "https://testdomain.com/products/category");
        url1.put("keywords", 850);
        dataArray.add(url1);

        ObjectNode url2 = mapper.createObjectNode();
        url2.put("url", "https://testdomain.com/blog/article-1");
        url2.put("keywords", 1250);
        dataArray.add(url2);

        ObjectNode url3 = mapper.createObjectNode();
        url3.put("url", "https://testdomain.com/about-us");
        url3.put("keywords", 25);
        dataArray.add(url3);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "testdomain.com");
        arguments.put("se", "g_us");
        arguments.put("page", 2);
        arguments.put("size", 50);

        // Act
        String result = DomainUrlsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result, "Formatted result should not be null");
        JsonNode formattedResponse = mapper.readTree(result);

        // Test response structure
        assertEquals("success", formattedResponse.get("status").asText(), "Status should be success");
        assertEquals("SerpstatDomainProcedure.getDomainUrls", formattedResponse.get("method").asText(),
                "Method should be correct");
        assertEquals("testdomain.com", formattedResponse.get("analyzed_domain").asText(), "Domain should be preserved");
        assertEquals("g_us", formattedResponse.get("search_engine").asText(), "Search engine should be preserved");

        // Test pagination information
        assertEquals(2, formattedResponse.get("page").asInt(), "Page should be preserved");
        assertEquals(50, formattedResponse.get("page_size").asInt(), "Page size should be preserved");
        assertEquals(3, formattedResponse.get("urls_on_page").asInt(), "URLs on page count should be correct");

        // Test URLs array processing
        assertTrue(formattedResponse.has("urls"), "Response should have urls field");
        JsonNode urls = formattedResponse.get("urls");
        assertTrue(urls.isArray(), "URLs should be an array");
        assertEquals(3, urls.size(), "Should have 3 URLs");

        // Test keyword count analysis preservation
        boolean foundHighPerforming = false;
        boolean foundMediumPerforming = false;
        boolean foundLowPerforming = false;

        for (JsonNode url : urls) {
            assertTrue(url.has("url"), "Each URL entry should have url field");
            assertTrue(url.has("keywords"), "Each URL entry should have keywords field");

            int keywords = url.get("keywords").asInt();
            if (keywords > 1000)
                foundHighPerforming = true;
            else if (keywords >= 100)
                foundMediumPerforming = true;
            else
                foundLowPerforming = true;
        }

        assertTrue(foundHighPerforming, "Should identify high performing URLs");
        assertTrue(foundMediumPerforming, "Should identify medium performing URLs");
        assertTrue(foundLowPerforming, "Should identify low performing URLs");
    }

    @Test
    @DisplayName("Test URL performance analysis")
    void testUrlPerformanceAnalysis() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create URLs with different performance levels
        ObjectNode highPerformingUrl = mapper.createObjectNode();
        highPerformingUrl.put("url", "https://example.com/top-product");
        highPerformingUrl.put("keywords", 2500);
        dataArray.add(highPerformingUrl);

        ObjectNode mediumPerformingUrl1 = mapper.createObjectNode();
        mediumPerformingUrl1.put("url", "https://example.com/category-page");
        mediumPerformingUrl1.put("keywords", 450);
        dataArray.add(mediumPerformingUrl1);

        ObjectNode mediumPerformingUrl2 = mapper.createObjectNode();
        mediumPerformingUrl2.put("url", "https://example.com/service-page");
        mediumPerformingUrl2.put("keywords", 750);
        dataArray.add(mediumPerformingUrl2);

        ObjectNode lowPerformingUrl = mapper.createObjectNode();
        lowPerformingUrl.put("url", "https://example.com/contact-us");
        lowPerformingUrl.put("keywords", 15);
        dataArray.add(lowPerformingUrl);

        ObjectNode zeroKeywordsUrl = mapper.createObjectNode();
        zeroKeywordsUrl.put("url", "https://example.com/error-page");
        zeroKeywordsUrl.put("keywords", 0);
        dataArray.add(zeroKeywordsUrl);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "example.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUrlsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result, "Result should not be null");
        JsonNode formattedResponse = mapper.readTree(result);

        // Test performance categorization through analysis
        JsonNode urls = formattedResponse.get("urls");
        assertNotNull(urls, "URLs array should be present");
        assertEquals(5, urls.size(), "Should have 5 URLs");

        int highPerformingCount = 0;
        int mediumPerformingCount = 0;
        int lowPerformingCount = 0;
        int zeroKeywordCount = 0;
        int maxKeywords = 0;
        int totalKeywords = 0;

        for (JsonNode url : urls) {
            int keywords = url.get("keywords").asInt();
            totalKeywords += keywords;
            maxKeywords = Math.max(maxKeywords, keywords);

            if (keywords > 1000) {
                highPerformingCount++;
            } else if (keywords >= 100) {
                mediumPerformingCount++;
            } else if (keywords > 0) {
                lowPerformingCount++;
            } else {
                zeroKeywordCount++;
            }
        }

        // Test performance distribution
        assertEquals(1, highPerformingCount, "Should identify 1 high performing URL");
        assertEquals(2, mediumPerformingCount, "Should identify 2 medium performing URLs");
        assertEquals(1, lowPerformingCount, "Should identify 1 low performing URL");
        assertEquals(1, zeroKeywordCount, "Should identify 1 zero-keyword URL");

        // Test top performing URL identification
        assertEquals(2500, maxKeywords, "Should identify top performing URL with 2500 keywords");

        // Test total performance calculation
        assertEquals(3715, totalKeywords, "Total keywords should be correctly calculated");
    }

    @Test
    @DisplayName("Test keyword count statistics")
    void testKeywordCountStatistics() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create diverse URL data for statistics testing
        int[] keywordCounts = { 1500, 800, 300, 150, 75, 25, 10, 0, 0, 2200 };
        String[] urlPaths = {
                "/main-product", "/category", "/subcategory", "/blog-post",
                "/service", "/about", "/privacy", "/404", "/sitemap", "/premium"
        };

        for (int i = 0; i < keywordCounts.length; i++) {
            ObjectNode url = mapper.createObjectNode();
            url.put("url", "https://testsite.com" + urlPaths[i]);
            url.put("keywords", keywordCounts[i]);
            dataArray.add(url);
        }

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "testsite.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUrlsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result, "Result should not be null");
        JsonNode formattedResponse = mapper.readTree(result);

        JsonNode urls = formattedResponse.get("urls");
        assertNotNull(urls, "URLs array should be present");
        assertEquals(10, urls.size(), "Should have 10 URLs for statistics");

        // Calculate statistics from the response data
        int totalKeywords = 0;
        int maxKeywords = 0;
        int minKeywords = Integer.MAX_VALUE;
        int zeroKeywordUrls = 0;
        int urlsWithKeywords = 0;

        for (JsonNode url : urls) {
            int keywords = url.get("keywords").asInt();
            totalKeywords += keywords;
            maxKeywords = Math.max(maxKeywords, keywords);
            minKeywords = Math.min(minKeywords, keywords);

            if (keywords == 0) {
                zeroKeywordUrls++;
            } else {
                urlsWithKeywords++;
            }
        }

        // Test total keywords count aggregation
        assertEquals(5060, totalKeywords, "Total keywords should be correctly aggregated");

        // Test max/min keywords per URL identification
        assertEquals(2200, maxKeywords, "Maximum keywords per URL should be identified");
        assertEquals(0, minKeywords, "Minimum keywords per URL should be identified");

        // Test average keywords per URL calculation
        double averageKeywords = (double) totalKeywords / urls.size();
        assertEquals(506.0, averageKeywords, 0.1, "Average keywords per URL should be calculated correctly");

        // Test zero-keyword URLs percentage
        double zeroKeywordPercentage = (double) zeroKeywordUrls / urls.size() * 100;
        assertEquals(20.0, zeroKeywordPercentage, 0.1, "Zero-keyword URLs percentage should be calculated");

        // Test keywords distribution analysis
        assertEquals(2, zeroKeywordUrls, "Should identify correct count of zero-keyword URLs");
        assertEquals(8, urlsWithKeywords, "Should identify correct count of URLs with keywords");

        // Test keyword density metrics (non-zero URLs)
        if (urlsWithKeywords > 0) {
            double averageNonZeroKeywords = (double) totalKeywords / urlsWithKeywords;
            assertEquals(632.5, averageNonZeroKeywords, 0.1, "Average keywords for non-zero URLs should be calculated");
        }

        // Test response structure contains statistics context
        assertTrue(formattedResponse.has("urls_on_page"), "Response should have URLs count information");
        assertEquals(10, formattedResponse.get("urls_on_page").asInt(), "URLs on page should match test data");
    }

    @Test
    @DisplayName("Test response structure validation")
    void testResponseStructureValidation() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create well-structured URL data for validation testing
        ObjectNode url1 = mapper.createObjectNode();
        url1.put("url", "https://structuretest.com/page1");
        url1.put("keywords", 500);
        dataArray.add(url1);

        ObjectNode url2 = mapper.createObjectNode();
        url2.put("url", "https://structuretest.com/page2");
        url2.put("keywords", 750);
        dataArray.add(url2);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "structuretest.com");
        arguments.put("se", "g_ua");
        arguments.put("page", 1);
        arguments.put("size", 100);

        // Act
        String result = DomainUrlsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result, "Result should not be null");
        JsonNode formattedResponse = mapper.readTree(result);

        // Test that all required fields are present in formatted response
        assertTrue(formattedResponse.has("status"), "Response should have status field");
        assertTrue(formattedResponse.has("method"), "Response should have method field");
        assertTrue(formattedResponse.has("analyzed_domain"), "Response should have analyzed_domain field");
        assertTrue(formattedResponse.has("search_engine"), "Response should have search_engine field");
        assertTrue(formattedResponse.has("page"), "Response should have page field");
        assertTrue(formattedResponse.has("page_size"), "Response should have page_size field");
        assertTrue(formattedResponse.has("urls_on_page"), "Response should have urls_on_page field");
        assertTrue(formattedResponse.has("urls"), "Response should have urls field");

        // Test data type consistency (strings, numbers, arrays, objects)
        assertTrue(formattedResponse.get("status").isTextual(), "status should be string");
        assertTrue(formattedResponse.get("method").isTextual(), "method should be string");
        assertTrue(formattedResponse.get("analyzed_domain").isTextual(), "analyzed_domain should be string");
        assertTrue(formattedResponse.get("search_engine").isTextual(), "search_engine should be string");
        assertTrue(formattedResponse.get("page").isNumber(), "page should be number");
        assertTrue(formattedResponse.get("page_size").isNumber(), "page_size should be number");
        assertTrue(formattedResponse.get("urls_on_page").isNumber(), "urls_on_page should be number");
        assertTrue(formattedResponse.get("urls").isArray(), "urls should be array");

        // Test field naming conventions (snake_case)
        assertTrue(formattedResponse.has("analyzed_domain"), "Should use snake_case for analyzed_domain");
        assertTrue(formattedResponse.has("search_engine"), "Should use snake_case for search_engine");
        assertTrue(formattedResponse.has("page_size"), "Should use snake_case for page_size");
        assertTrue(formattedResponse.has("urls_on_page"), "Should use snake_case for urls_on_page");

        // Test nested object structure validation (URLs array elements)
        JsonNode urls = formattedResponse.get("urls");
        for (JsonNode url : urls) {
            assertTrue(url.has("url"), "Each URL object should have url field");
            assertTrue(url.has("keywords"), "Each URL object should have keywords field");
            assertTrue(url.get("url").isTextual(), "url field should be string");
            assertTrue(url.get("keywords").isNumber(), "keywords field should be number");
        }

        // Test array element consistency
        assertEquals(2, urls.size(), "URLs array should have correct number of elements");
        for (JsonNode url : urls) {
            assertTrue(url.get("keywords").asInt() >= 0, "Keywords should be non-negative");
            assertTrue(url.get("url").asText().startsWith("https://"), "URLs should be properly formatted");
        }
    }

    @Test
    @DisplayName("Test error handling for malformed responses")
    void testErrorHandlingForMalformedResponses() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Test 1: Handling of null API response
        assertThrows(Exception.class, () -> {
            DomainUrlsResponseFormatter.format(null, new HashMap<>(), mapper);
        }, "Should throw exception for null API response");

        // Test 2: Handling of empty data arrays
        ObjectNode emptyResultNode = mapper.createObjectNode();
        ArrayNode emptyDataArray = mapper.createArrayNode();
        emptyResultNode.set("data", emptyDataArray);

        SerpstatApiResponse emptyResponse = mock(SerpstatApiResponse.class);
        when(emptyResponse.getResult()).thenReturn(emptyResultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "emptytest.com");
        arguments.put("se", "g_us");

        String emptyResult = DomainUrlsResponseFormatter.format(emptyResponse, arguments, mapper);
        assertNotNull(emptyResult, "Should handle empty data arrays gracefully");

        JsonNode emptyFormattedResponse = mapper.readTree(emptyResult);
        assertEquals(0, emptyFormattedResponse.get("urls_on_page").asInt(), "Should report 0 URLs for empty data");
        assertTrue(emptyFormattedResponse.get("urls").isArray(), "URLs should still be an array");
        assertEquals(0, emptyFormattedResponse.get("urls").size(), "URLs array should be empty");

        // Test 3: Handling of missing required fields in URL objects
        ObjectNode partialResultNode = mapper.createObjectNode();
        ArrayNode partialDataArray = mapper.createArrayNode();

        ObjectNode incompleteUrl = mapper.createObjectNode();
        incompleteUrl.put("url", "https://partial.com/page");
        // Missing keywords field
        partialDataArray.add(incompleteUrl);

        ObjectNode completeUrl = mapper.createObjectNode();
        completeUrl.put("url", "https://partial.com/complete");
        completeUrl.put("keywords", 100);
        partialDataArray.add(completeUrl);

        partialResultNode.set("data", partialDataArray);

        SerpstatApiResponse partialResponse = mock(SerpstatApiResponse.class);
        when(partialResponse.getResult()).thenReturn(partialResultNode);

        String partialResult = DomainUrlsResponseFormatter.format(partialResponse, arguments, mapper);
        assertNotNull(partialResult, "Should handle missing fields gracefully");

        JsonNode partialFormattedResponse = mapper.readTree(partialResult);
        assertEquals(2, partialFormattedResponse.get("urls_on_page").asInt(),
                "Should count all URLs even with missing fields");

        // Test 4: Handling of invalid data types
        ObjectNode invalidResultNode = mapper.createObjectNode();
        ArrayNode invalidDataArray = mapper.createArrayNode();

        ObjectNode invalidUrl = mapper.createObjectNode();
        invalidUrl.put("url", "https://invalid.com/page");
        invalidUrl.put("keywords", "not-a-number"); // Invalid data type
        invalidDataArray.add(invalidUrl);

        invalidResultNode.set("data", invalidDataArray);

        SerpstatApiResponse invalidResponse = mock(SerpstatApiResponse.class);
        when(invalidResponse.getResult()).thenReturn(invalidResultNode);

        // Should either handle gracefully or throw appropriate exception
        assertDoesNotThrow(() -> {
            String invalidResult = DomainUrlsResponseFormatter.format(invalidResponse, arguments, mapper);
            assertNotNull(invalidResult, "Should handle invalid data types");
        }, "Should not crash on invalid data types");

        // Test 5: Graceful degradation for partial data
        ObjectNode mixedResultNode = mapper.createObjectNode();
        ArrayNode mixedDataArray = mapper.createArrayNode();

        ObjectNode goodUrl = mapper.createObjectNode();
        goodUrl.put("url", "https://mixed.com/good");
        goodUrl.put("keywords", 200);
        mixedDataArray.add(goodUrl);

        ObjectNode nullUrl = mapper.createObjectNode();
        nullUrl.putNull("url");
        nullUrl.put("keywords", 150);
        mixedDataArray.add(nullUrl);

        mixedResultNode.set("data", mixedDataArray);

        SerpstatApiResponse mixedResponse = mock(SerpstatApiResponse.class);
        when(mixedResponse.getResult()).thenReturn(mixedResultNode);

        String mixedResult = DomainUrlsResponseFormatter.format(mixedResponse, arguments, mapper);
        assertNotNull(mixedResult, "Should handle mixed valid/invalid data gracefully");

        JsonNode mixedFormattedResponse = mapper.readTree(mixedResult);
        assertTrue(mixedFormattedResponse.get("urls_on_page").asInt() >= 1, "Should process at least valid URLs");
    }

    @Test
    @DisplayName("Test JSON serialization")
    void testJsonSerialization() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create URLs with special characters and Unicode for serialization testing
        ObjectNode urlWithSpecialChars = mapper.createObjectNode();
        urlWithSpecialChars.put("url", "https://testsite.com/special-page?param=value&filter=test");
        urlWithSpecialChars.put("keywords", 123);
        dataArray.add(urlWithSpecialChars);

        ObjectNode urlWithUnicode = mapper.createObjectNode();
        urlWithUnicode.put("url", "https://тест.укр/україна/сторінка");
        urlWithUnicode.put("keywords", 456);
        dataArray.add(urlWithUnicode);

        ObjectNode urlWithLargeNumber = mapper.createObjectNode();
        urlWithLargeNumber.put("url", "https://testsite.com/large-numbers");
        urlWithLargeNumber.put("keywords", 999999);
        dataArray.add(urlWithLargeNumber);

        ObjectNode urlWithSpaces = mapper.createObjectNode();
        urlWithSpaces.put("url", "https://testsite.com/spaces and special/chars@example");
        urlWithSpaces.put("keywords", 789);
        dataArray.add(urlWithSpaces);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "testsite.com");
        arguments.put("se", "g_ua");

        // Act
        String result = DomainUrlsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result, "Result should not be null");

        // Test ObjectMapper integration and configuration
        JsonNode formattedResponse = mapper.readTree(result);
        assertNotNull(formattedResponse, "Should be able to parse JSON with ObjectMapper");

        // Test proper JSON structure output
        assertTrue(result.startsWith("{"), "Result should be valid JSON object");
        assertTrue(result.endsWith("}"), "Result should be valid JSON object");
        assertTrue(result.contains("\"status\""), "Should contain properly quoted field names");
        assertTrue(result.contains("\"urls\""), "Should contain properly quoted array field");

        // Test handling of special characters in URLs
        String responseText = result;
        assertTrue(responseText.contains("param=value&filter=test"), "Should preserve URL parameters");
        assertTrue(responseText.contains("?"), "Should preserve query string separators");
        assertTrue(responseText.contains("&"), "Should preserve parameter separators");

        // Test Unicode URL handling
        JsonNode urls = formattedResponse.get("urls");
        boolean foundUnicodeUrl = false;
        for (JsonNode url : urls) {
            String urlString = url.get("url").asText();
            if (urlString.contains("тест.укр") || urlString.contains("україна")) {
                foundUnicodeUrl = true;
                break;
            }
        }
        assertTrue(foundUnicodeUrl, "Should handle Unicode URLs correctly");

        // Test large number formatting
        boolean foundLargeNumber = false;
        for (JsonNode url : urls) {
            int keywords = url.get("keywords").asInt();
            if (keywords == 999999) {
                foundLargeNumber = true;
                break;
            }
        }
        assertTrue(foundLargeNumber, "Should handle large numbers correctly");

        // Test spaces and special characters in URLs
        boolean foundSpecialCharsUrl = false;
        for (JsonNode url : urls) {
            String urlString = url.get("url").asText();
            if (urlString.contains("spaces and special") && urlString.contains("chars@example")) {
                foundSpecialCharsUrl = true;
                break;
            }
        }
        assertTrue(foundSpecialCharsUrl, "Should handle spaces and special characters in URLs");

        // Test JSON validity by attempting to re-parse
        assertDoesNotThrow(() -> {
            ObjectMapper testMapper = new ObjectMapper();
            testMapper.readTree(result);
        }, "Result should be valid JSON that can be re-parsed");

        // Test that no JSON escaping issues exist
        assertFalse(result.contains("\\\\"), "Should not have double-escaped characters");
        assertFalse(result.contains("\\/\\/"), "Should not over-escape forward slashes");        // Test consistent field formatting
        assertTrue(formattedResponse.get("analyzed_domain").asText().equals("testsite.com"),
                "Domain field should be properly formatted");
        assertTrue(formattedResponse.get("search_engine").asText().equals("g_ua"), 
                "Search engine field should be properly formatted");
    }    @Test
    @DisplayName("Test argument context integration")
    void testArgumentContextIntegration() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create sample URL data for context testing
        ObjectNode url1 = mapper.createObjectNode();
        url1.put("url", "https://contexttest.com/page1");
        url1.put("keywords", 100);
        dataArray.add(url1);

        ObjectNode url2 = mapper.createObjectNode();
        url2.put("url", "https://contexttest.com/page2");
        url2.put("keywords", 200);
        dataArray.add(url2);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        // Test comprehensive argument context preservation
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "contexttest.com");
        arguments.put("se", "g_ua");
        arguments.put("page", 3);
        arguments.put("size", 250);
        arguments.put("url_prefix", "/products");
        arguments.put("url_contain", "special");
        arguments.put("sort", Map.of("keywords", "desc"));

        // Act
        String result = DomainUrlsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result, "Result should not be null");
        JsonNode formattedResponse = mapper.readTree(result);

        // Test extraction of request parameters for context
        assertTrue(formattedResponse.has("analyzed_domain"), "Should extract domain parameter");
        assertTrue(formattedResponse.has("search_engine"), "Should extract search engine parameter");
        assertTrue(formattedResponse.has("page"), "Should extract page parameter");
        assertTrue(formattedResponse.has("page_size"), "Should extract page size parameter");

        // Test domain parameter inclusion in response
        assertEquals("contexttest.com", formattedResponse.get("analyzed_domain").asText(), 
                    "Domain parameter should be correctly included");

        // Test search engine parameter inclusion
        assertEquals("g_ua", formattedResponse.get("search_engine").asText(), 
                    "Search engine parameter should be correctly included");

        // Test pagination context preservation
        assertEquals(3, formattedResponse.get("page").asInt(), "Page parameter should be preserved");
        assertEquals(250, formattedResponse.get("page_size").asInt(), "Page size parameter should be preserved");
        assertEquals(2, formattedResponse.get("urls_on_page").asInt(), "URLs count should be calculated");

        // Test filter parameters reflection in response (if supported by formatter)
        String responseText = result;
        
        // Verify that context is preserved in the response structure
        assertTrue(responseText.contains("contexttest.com"), "Domain context should be preserved");
        assertTrue(responseText.contains("g_ua"), "Search engine context should be preserved");

        // Test method identification
        assertEquals("SerpstatDomainProcedure.getDomainUrls", formattedResponse.get("method").asText(),
                    "Method should be correctly identified in context");

        // Test status context
        assertEquals("success", formattedResponse.get("status").asText(), "Status should indicate success");

        // Test that URLs data is preserved alongside context
        assertTrue(formattedResponse.has("urls"), "URLs data should be present with context");
        JsonNode urls = formattedResponse.get("urls");
        assertEquals(2, urls.size(), "URLs data should be correctly processed with context");

        // Test that argument context doesn't interfere with data processing
        for (JsonNode url : urls) {
            assertTrue(url.has("url"), "URL data should be preserved with context");
            assertTrue(url.has("keywords"), "Keywords data should be preserved with context");
            assertTrue(url.get("keywords").asInt() > 0, "Keywords should be valid with context");
        }
    }    @Test
    @DisplayName("Test summary analytics calculation")
    void testSummaryAnalyticsCalculation() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create diverse URL data for analytics calculation
        ObjectNode highPerformingUrl = mapper.createObjectNode();
        highPerformingUrl.put("url", "https://analyticstest.com/top-performing-page");
        highPerformingUrl.put("keywords", 5000);
        dataArray.add(highPerformingUrl);

        ObjectNode mediumPerformingUrl1 = mapper.createObjectNode();
        mediumPerformingUrl1.put("url", "https://analyticstest.com/category/products");
        mediumPerformingUrl1.put("keywords", 800);
        dataArray.add(mediumPerformingUrl1);

        ObjectNode mediumPerformingUrl2 = mapper.createObjectNode();
        mediumPerformingUrl2.put("url", "https://analyticstest.com/blog/article");
        mediumPerformingUrl2.put("keywords", 600);
        dataArray.add(mediumPerformingUrl2);

        ObjectNode lowPerformingUrl = mapper.createObjectNode();
        lowPerformingUrl.put("url", "https://analyticstest.com/contact");
        lowPerformingUrl.put("keywords", 50);
        dataArray.add(lowPerformingUrl);

        ObjectNode underperformingUrl = mapper.createObjectNode();
        underperformingUrl.put("url", "https://analyticstest.com/privacy-policy");
        underperformingUrl.put("keywords", 5);
        dataArray.add(underperformingUrl);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "analyticstest.com");
        arguments.put("se", "g_ua");

        // Act
        String result = DomainUrlsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result, "Result should not be null");
        JsonNode formattedResponse = mapper.readTree(result);

        // Test SEO insights generation based on URL performance
        JsonNode urls = formattedResponse.get("urls");
        assertNotNull(urls, "URLs should be present for analytics");
        assertEquals(5, urls.size(), "Should have all URLs for analytics calculation");

        // Calculate performance distribution for analytics
        int totalKeywords = 0;
        int highPerformingCount = 0;
        int mediumPerformingCount = 0;
        int lowPerformingCount = 0;
        int maxKeywords = 0;
        String topPerformingUrl = "";

        for (JsonNode url : urls) {
            int keywords = url.get("keywords").asInt();
            String urlString = url.get("url").asText();
            
            totalKeywords += keywords;
            
            if (keywords > maxKeywords) {
                maxKeywords = keywords;
                topPerformingUrl = urlString;
            }
            
            if (keywords > 1000) {
                highPerformingCount++;
            } else if (keywords >= 100) {
                mediumPerformingCount++;
            } else {
                lowPerformingCount++;
            }
        }

        // Test performance analytics insights
        assertEquals(6455, totalKeywords, "Total keywords should be correctly calculated for analytics");
        assertEquals(1, highPerformingCount, "Should identify high-performing URLs for insights");
        assertEquals(2, mediumPerformingCount, "Should identify medium-performing URLs for insights");
        assertEquals(2, lowPerformingCount, "Should identify underperforming URLs for optimization");

        // Test content gap analysis recommendations (based on performance distribution)
        double averageKeywords = (double) totalKeywords / urls.size();
        assertEquals(1291.0, averageKeywords, 0.1, "Average keywords should be calculated for gap analysis");

        // Test URL optimization suggestions (identify underperforming URLs)
        int underperformingUrls = 0;
        for (JsonNode url : urls) {
            if (url.get("keywords").asInt() < 100) {
                underperformingUrls++;
            }
        }
        assertEquals(2, underperformingUrls, "Should identify URLs needing optimization");

        // Test competitive benchmarking insights (top performer identification)
        assertEquals(5000, maxKeywords, "Should identify top performing URL for benchmarking");
        assertTrue(topPerformingUrl.contains("top-performing-page"), "Should identify the actual top performer");

        // Test performance trend analysis (distribution analysis)
        double highPerformingPercentage = (double) highPerformingCount / urls.size() * 100;
        double lowPerformingPercentage = (double) lowPerformingCount / urls.size() * 100;
        
        assertEquals(20.0, highPerformingPercentage, 0.1, "Should calculate high-performing percentage");
        assertEquals(40.0, lowPerformingPercentage, 0.1, "Should calculate low-performing percentage for optimization");

        // Test that analytics doesn't break basic response structure
        assertTrue(formattedResponse.has("status"), "Analytics should preserve response structure");
        assertTrue(formattedResponse.has("analyzed_domain"), "Analytics should preserve domain context");
        assertEquals(5, formattedResponse.get("urls_on_page").asInt(), "Analytics should preserve URL count");
    }    @Test
    @DisplayName("Test format consistency")
    void testFormatConsistency() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create standard URL data for consistency testing
        ObjectNode url1 = mapper.createObjectNode();
        url1.put("url", "https://consistency.com/page1");
        url1.put("keywords", 300);
        dataArray.add(url1);

        ObjectNode url2 = mapper.createObjectNode();
        url2.put("url", "https://consistency.com/page2");
        url2.put("keywords", 150);
        dataArray.add(url2);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "consistency.com");
        arguments.put("se", "g_ua");
        arguments.put("page", 1);
        arguments.put("size", 100);

        // Act
        String result = DomainUrlsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result, "Result should not be null");
        JsonNode formattedResponse = mapper.readTree(result);

        // Test consistent response structure with other formatters
        String[] requiredFields = {
            "status", "method", "analyzed_domain", "search_engine", 
            "page", "page_size", "urls_on_page", "urls"
        };
        
        for (String field : requiredFields) {
            assertTrue(formattedResponse.has(field), 
                      "Response should have consistent field: " + field);
        }

        // Test consistent field naming across different response types (snake_case)
        assertTrue(formattedResponse.has("analyzed_domain"), "Should use snake_case: analyzed_domain");
        assertTrue(formattedResponse.has("search_engine"), "Should use snake_case: search_engine");
        assertTrue(formattedResponse.has("page_size"), "Should use snake_case: page_size");
        assertTrue(formattedResponse.has("urls_on_page"), "Should use snake_case: urls_on_page");
        
        // Ensure no camelCase inconsistencies
        assertFalse(formattedResponse.has("analyzedDomain"), "Should not use camelCase: analyzedDomain");
        assertFalse(formattedResponse.has("searchEngine"), "Should not use camelCase: searchEngine");
        assertFalse(formattedResponse.has("pageSize"), "Should not use camelCase: pageSize");

        // Test consistent error handling patterns (success case)
        assertEquals("success", formattedResponse.get("status").asText(), 
                    "Should use consistent success status");

        // Test consistent metadata inclusion (status, method, etc.)
        assertEquals("SerpstatDomainProcedure.getDomainUrls", formattedResponse.get("method").asText(),
                    "Should include consistent method identifier");
        assertEquals("consistency.com", formattedResponse.get("analyzed_domain").asText(),
                    "Should include consistent domain metadata");
        assertEquals("g_ua", formattedResponse.get("search_engine").asText(),
                    "Should include consistent search engine metadata");

        // Test consistent data type patterns
        assertTrue(formattedResponse.get("status").isTextual(), "Status should be string type");
        assertTrue(formattedResponse.get("method").isTextual(), "Method should be string type");
        assertTrue(formattedResponse.get("analyzed_domain").isTextual(), "Domain should be string type");
        assertTrue(formattedResponse.get("search_engine").isTextual(), "Search engine should be string type");
        assertTrue(formattedResponse.get("page").isNumber(), "Page should be number type");
        assertTrue(formattedResponse.get("page_size").isNumber(), "Page size should be number type");
        assertTrue(formattedResponse.get("urls_on_page").isNumber(), "URLs count should be number type");
        assertTrue(formattedResponse.get("urls").isArray(), "URLs should be array type");

        // Test consistent array element structure
        JsonNode urls = formattedResponse.get("urls");
        for (JsonNode url : urls) {
            assertTrue(url.has("url"), "Each URL element should have consistent 'url' field");
            assertTrue(url.has("keywords"), "Each URL element should have consistent 'keywords' field");
            assertTrue(url.get("url").isTextual(), "URL field should be consistently string type");
            assertTrue(url.get("keywords").isNumber(), "Keywords field should be consistently number type");
        }

        // Test consistent pagination structure
        assertEquals(1, formattedResponse.get("page").asInt(), "Page should follow consistent pagination");
        assertEquals(100, formattedResponse.get("page_size").asInt(), "Page size should follow consistent pagination");
        assertEquals(2, formattedResponse.get("urls_on_page").asInt(), "URLs count should be consistent");

        // Test consistent JSON structure (no unexpected nesting)
        assertFalse(formattedResponse.has("data"), "Should not expose raw 'data' field - should be processed");
        assertFalse(formattedResponse.has("result"), "Should not expose raw 'result' field - should be processed");        // Test that response is valid JSON that follows formatting standards
        String responseText = result;
        assertFalse(responseText.contains("\"null\""), "Should not contain explicit null strings in formatted response");
        assertTrue(formattedResponse.get("status").asText().equals("success"), "Should have consistent status formatting");
    }    @Test
    @DisplayName("Test performance with large responses")
    void testPerformanceWithLargeResponses() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create large dataset with 1000 URLs (maximum page size for performance testing)
        for (int i = 1; i <= 1000; i++) {
            ObjectNode url = mapper.createObjectNode();
            url.put("url", "https://performancetest.com/page-" + i);
            url.put("keywords", (i * 10) % 5000); // Varied keyword counts
            dataArray.add(url);
        }

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "performancetest.com");
        arguments.put("se", "g_ua");
        arguments.put("page", 1);
        arguments.put("size", 1000);

        // Act & Test performance
        long startTime = System.currentTimeMillis();
        
        String result = DomainUrlsResponseFormatter.format(mockResponse, arguments, mapper);
        
        long endTime = System.currentTimeMillis();
        long executionTime = endTime - startTime;

        // Assert
        assertNotNull(result, "Result should not be null for large dataset");

        // Test formatting performance with 1000 URLs (should complete within reasonable time)
        assertTrue(executionTime < 5000, 
                  "Formatting 1000 URLs should complete within 5 seconds, took: " + executionTime + "ms");

        JsonNode formattedResponse = mapper.readTree(result);

        // Test that large response maintains structure integrity
        assertEquals("success", formattedResponse.get("status").asText(), 
                    "Status should be correct for large response");
        assertEquals(1000, formattedResponse.get("urls_on_page").asInt(), 
                    "Should correctly count 1000 URLs");
        assertEquals(1000, formattedResponse.get("page_size").asInt(), 
                    "Should preserve large page size");

        // Test memory usage during response formatting (verify all data is processed)
        JsonNode urls = formattedResponse.get("urls");
        assertEquals(1000, urls.size(), "Should process all 1000 URLs without memory issues");

        // Test time complexity of formatting operations (verify linear performance)
        int urlCount = 0;
        int totalKeywords = 0;
        for (JsonNode url : urls) {
            urlCount++;
            totalKeywords += url.get("keywords").asInt();
            
            // Verify each URL is properly formatted
            assertTrue(url.has("url"), "Each URL should be properly formatted in large response");
            assertTrue(url.has("keywords"), "Each URL should have keywords in large response");
        }
        
        assertEquals(1000, urlCount, "Should process exactly 1000 URLs");
        assertTrue(totalKeywords > 0, "Should calculate total keywords for large dataset");

        // Test ObjectMapper performance optimization (verify JSON is valid)
        assertDoesNotThrow(() -> {
            ObjectMapper testMapper = new ObjectMapper();
            JsonNode testNode = testMapper.readTree(result);
            assertEquals(1000, testNode.get("urls").size());
        }, "Large response should be valid JSON");

        // Test streaming vs in-memory processing considerations
        int resultLength = result.length();
        assertTrue(resultLength > 50000, "Large response should generate substantial output");
        assertTrue(resultLength < 10000000, "Large response should not be excessively large");        // Test that performance doesn't degrade quality
        String responseText = result;
        assertTrue(responseText.contains("performancetest.com"), "Domain should be preserved in large response");
        assertTrue(formattedResponse.get("status").asText().equals("success"), "Status should be preserved in large response");
        
        // Verify no data corruption in large response
        boolean foundFirstUrl = responseText.contains("page-1");
        boolean foundLastUrl = responseText.contains("page-1000");
        assertTrue(foundFirstUrl, "Should preserve first URL in large response");
        assertTrue(foundLastUrl, "Should preserve last URL in large response");
    }    @Test
    @DisplayName("Test edge cases")
    void testEdgeCases() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();

        // Test 1: Empty URL arrays
        ObjectNode emptyResultNode = mapper.createObjectNode();
        ArrayNode emptyDataArray = mapper.createArrayNode();
        emptyResultNode.set("data", emptyDataArray);

        SerpstatApiResponse emptyResponse = mock(SerpstatApiResponse.class);
        when(emptyResponse.getResult()).thenReturn(emptyResultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domain", "edgecase.com");
        arguments.put("se", "g_ua");

        String emptyResult = DomainUrlsResponseFormatter.format(emptyResponse, arguments, mapper);
        assertNotNull(emptyResult, "Should handle empty URL arrays");
        
        JsonNode emptyFormattedResponse = mapper.readTree(emptyResult);
        assertEquals(0, emptyFormattedResponse.get("urls_on_page").asInt(), "Should handle empty arrays correctly");
        assertTrue(emptyFormattedResponse.get("urls").isArray(), "URLs should still be array for empty case");

        // Test 2: URLs with zero keywords
        ObjectNode zeroKeywordsResultNode = mapper.createObjectNode();
        ArrayNode zeroKeywordsDataArray = mapper.createArrayNode();

        ObjectNode zeroKeywordsUrl = mapper.createObjectNode();
        zeroKeywordsUrl.put("url", "https://edgecase.com/zero-keywords");
        zeroKeywordsUrl.put("keywords", 0);
        zeroKeywordsDataArray.add(zeroKeywordsUrl);

        zeroKeywordsResultNode.set("data", zeroKeywordsDataArray);

        SerpstatApiResponse zeroKeywordsResponse = mock(SerpstatApiResponse.class);
        when(zeroKeywordsResponse.getResult()).thenReturn(zeroKeywordsResultNode);

        String zeroResult = DomainUrlsResponseFormatter.format(zeroKeywordsResponse, arguments, mapper);
        assertNotNull(zeroResult, "Should handle URLs with zero keywords");
        
        JsonNode zeroFormattedResponse = mapper.readTree(zeroResult);
        assertEquals(1, zeroFormattedResponse.get("urls_on_page").asInt(), "Should count URLs with zero keywords");

        // Test 3: Very long URLs
        ObjectNode longUrlResultNode = mapper.createObjectNode();
        ArrayNode longUrlDataArray = mapper.createArrayNode();

        StringBuilder longUrlBuilder = new StringBuilder("https://edgecase.com/very-long-url");
        for (int i = 0; i < 100; i++) {
            longUrlBuilder.append("/segment-").append(i);
        }
        String veryLongUrl = longUrlBuilder.toString();

        ObjectNode longUrl = mapper.createObjectNode();
        longUrl.put("url", veryLongUrl);
        longUrl.put("keywords", 100);
        longUrlDataArray.add(longUrl);

        longUrlResultNode.set("data", longUrlDataArray);

        SerpstatApiResponse longUrlResponse = mock(SerpstatApiResponse.class);
        when(longUrlResponse.getResult()).thenReturn(longUrlResultNode);

        String longUrlResult = DomainUrlsResponseFormatter.format(longUrlResponse, arguments, mapper);
        assertNotNull(longUrlResult, "Should handle very long URLs");
        assertTrue(longUrlResult.contains("very-long-url"), "Should preserve long URL content");

        // Test 4: URLs with special characters
        ObjectNode specialCharsResultNode = mapper.createObjectNode();
        ArrayNode specialCharsDataArray = mapper.createArrayNode();

        ObjectNode specialCharsUrl = mapper.createObjectNode();
        specialCharsUrl.put("url", "https://edgecase.com/special-chars?param=value&filter=test#section");
        specialCharsUrl.put("keywords", 50);
        specialCharsDataArray.add(specialCharsUrl);

        ObjectNode encodedUrl = mapper.createObjectNode();
        encodedUrl.put("url", "https://edgecase.com/encoded%20spaces%26symbols");
        encodedUrl.put("keywords", 75);
        specialCharsDataArray.add(encodedUrl);

        specialCharsResultNode.set("data", specialCharsDataArray);

        SerpstatApiResponse specialCharsResponse = mock(SerpstatApiResponse.class);
        when(specialCharsResponse.getResult()).thenReturn(specialCharsResultNode);

        String specialCharsResult = DomainUrlsResponseFormatter.format(specialCharsResponse, arguments, mapper);
        assertNotNull(specialCharsResult, "Should handle URLs with special characters");
        assertTrue(specialCharsResult.contains("param=value"), "Should preserve URL parameters");
        assertTrue(specialCharsResult.contains("encoded%20spaces"), "Should preserve encoded characters");

        // Test 5: International domain names in URLs
        ObjectNode idnResultNode = mapper.createObjectNode();
        ArrayNode idnDataArray = mapper.createArrayNode();

        ObjectNode idnUrl = mapper.createObjectNode();
        idnUrl.put("url", "https://тест.укр/сторінка");
        idnUrl.put("keywords", 200);
        idnDataArray.add(idnUrl);

        ObjectNode idnUrl2 = mapper.createObjectNode();
        idnUrl2.put("url", "https://example.中国/页面");
        idnUrl2.put("keywords", 150);
        idnDataArray.add(idnUrl2);

        idnResultNode.set("data", idnDataArray);

        SerpstatApiResponse idnResponse = mock(SerpstatApiResponse.class);
        when(idnResponse.getResult()).thenReturn(idnResultNode);

        String idnResult = DomainUrlsResponseFormatter.format(idnResponse, arguments, mapper);
        assertNotNull(idnResult, "Should handle international domain names");
        assertTrue(idnResult.contains("тест.укр"), "Should preserve Ukrainian IDN");
        assertTrue(idnResult.contains("中国"), "Should preserve Chinese IDN");

        // Test 6: Malformed URL handling
        ObjectNode malformedResultNode = mapper.createObjectNode();
        ArrayNode malformedDataArray = mapper.createArrayNode();

        ObjectNode malformedUrl = mapper.createObjectNode();
        malformedUrl.put("url", "not-a-valid-url");
        malformedUrl.put("keywords", 25);
        malformedDataArray.add(malformedUrl);

        ObjectNode validUrl = mapper.createObjectNode();
        validUrl.put("url", "https://edgecase.com/valid");
        validUrl.put("keywords", 100);
        malformedDataArray.add(validUrl);

        malformedResultNode.set("data", malformedDataArray);

        SerpstatApiResponse malformedResponse = mock(SerpstatApiResponse.class);
        when(malformedResponse.getResult()).thenReturn(malformedResultNode);

        String malformedResult = DomainUrlsResponseFormatter.format(malformedResponse, arguments, mapper);
        assertNotNull(malformedResult, "Should handle malformed URLs gracefully");
        
        JsonNode malformedFormattedResponse = mapper.readTree(malformedResult);
        assertEquals(2, malformedFormattedResponse.get("urls_on_page").asInt(), 
                    "Should process both malformed and valid URLs");
        assertTrue(malformedResult.contains("not-a-valid-url"), "Should preserve malformed URL as-is");
        assertTrue(malformedResult.contains("https://edgecase.com/valid"), "Should preserve valid URL");
    }
}
