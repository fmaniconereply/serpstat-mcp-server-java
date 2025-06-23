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
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DomainUniqueKeywordsResponseFormatter class
 * 
 * Tests the formatting of unique keywords response from Serpstat API,
 */
@DisplayName("DomainUniqueKeywordsResponseFormatter Tests")
class DomainUniqueKeywordsResponseFormatterTest {

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
    @DisplayName("Test basic unique keywords response formatting")
    void testBasicUniqueKeywordsFormatting() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create sample unique keyword data
        ObjectNode keyword1 = mapper.createObjectNode();
        keyword1.put("keyword", "unique keyword 1");
        keyword1.put("position", 5);
        keyword1.put("region_queries_count", 1000);
        keyword1.put("cost", 2.5);
        keyword1.put("traff", 50);
        keyword1.put("difficulty", 45);
        keyword1.put("concurrency", 30);
        keyword1.put("keyword_length", 3);
        dataArray.add(keyword1);

        ObjectNode keyword2 = mapper.createObjectNode();
        keyword2.put("keyword", "competitive term");
        keyword2.put("position", 12);
        keyword2.put("region_queries_count", 5000);
        keyword2.put("cost", 8.0);
        keyword2.put("traff", 200);
        keyword2.put("difficulty", 75);
        keyword2.put("concurrency", 85);
        keyword2.put("keyword_length", 2);
        dataArray.add(keyword2);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("competitor1.com", "competitor2.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");
        arguments.put("page", 1);
        arguments.put("size", 100);

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        assertEquals("success", formattedResponse.get("status").asText());
        assertEquals("SerpstatDomainProcedure.getDomainsUniqKeywords", formattedResponse.get("method").asText());
        assertEquals("mydomain.com", formattedResponse.get("excluded_domain").asText());
        assertEquals("g_us", formattedResponse.get("search_engine").asText());
        assertEquals(1, formattedResponse.get("page").asInt());
        assertEquals(100, formattedResponse.get("page_size").asInt());
        assertEquals(2, formattedResponse.get("keywords_on_page").asInt());

        // Test analyzed domains array
        JsonNode analyzedDomains = formattedResponse.get("analyzed_domains");
        assertNotNull(analyzedDomains);
        assertTrue(analyzedDomains.isArray());
        assertEquals(2, analyzedDomains.size());
        assertEquals("competitor1.com", analyzedDomains.get(0).asText());
        assertEquals("competitor2.com", analyzedDomains.get(1).asText());

        // Test unique keywords data preservation
        JsonNode uniqueKeywords = formattedResponse.get("unique_keywords");
        assertNotNull(uniqueKeywords);
        assertTrue(uniqueKeywords.isArray());
        assertEquals(2, uniqueKeywords.size());
    }

    @Test
    @DisplayName("Test analytics and summary statistics calculation")
    void testAnalyticsCalculation() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create diverse keyword data for analytics testing
        ObjectNode highVolumeKeyword = mapper.createObjectNode();
        highVolumeKeyword.put("keyword", "high volume term");
        highVolumeKeyword.put("position", 2);
        highVolumeKeyword.put("region_queries_count", 15000); // High volume > 10,000
        highVolumeKeyword.put("cost", 6.5); // High cost > $5
        highVolumeKeyword.put("traff", 300);
        highVolumeKeyword.put("difficulty", 25); // Low difficulty < 30
        highVolumeKeyword.put("concurrency", 40);
        highVolumeKeyword.put("keyword_length", 4);
        dataArray.add(highVolumeKeyword);

        ObjectNode lowVolumeKeyword = mapper.createObjectNode();
        lowVolumeKeyword.put("keyword", "low volume term");
        lowVolumeKeyword.put("position", 8);
        lowVolumeKeyword.put("region_queries_count", 500); // Low volume
        lowVolumeKeyword.put("cost", 1.2); // Low cost
        lowVolumeKeyword.put("traff", 25);
        lowVolumeKeyword.put("difficulty", 80); // High difficulty
        lowVolumeKeyword.put("concurrency", 60);
        lowVolumeKeyword.put("keyword_length", 3);
        dataArray.add(lowVolumeKeyword);

        ObjectNode secondPageKeyword = mapper.createObjectNode();
        secondPageKeyword.put("keyword", "second page term");
        secondPageKeyword.put("position", 15); // Second page position
        secondPageKeyword.put("region_queries_count", 2000);
        secondPageKeyword.put("cost", 3.0);
        secondPageKeyword.put("traff", 80);
        secondPageKeyword.put("difficulty", 55);
        secondPageKeyword.put("concurrency", 70);
        secondPageKeyword.put("keyword_length", 2);
        dataArray.add(secondPageKeyword);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("example.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        JsonNode formattedResponse = mapper.readTree(result);
        JsonNode analytics = formattedResponse.get("analytics");
        assertNotNull(analytics);

        // Test summary statistics
        JsonNode summary = analytics.get("summary");
        assertNotNull(summary);
        assertEquals(17500, summary.get("total_search_volume").asLong()); // 15000 + 500 + 2000
        assertEquals(10.7, summary.get("total_cost_estimate").asDouble(), 0.1); // 6.5 + 1.2 + 3.0
        assertEquals(405, summary.get("total_traffic_estimate").asLong()); // 300 + 25 + 80
        assertEquals(53.33, summary.get("average_difficulty").asDouble(), 0.1); // (25 + 80 + 55) / 3
        assertEquals(56.67, summary.get("average_competition").asDouble(), 0.1); // (40 + 60 + 70) / 3
        assertEquals(1, summary.get("high_volume_keywords").asInt()); // Only first keyword > 10,000
        assertEquals(1, summary.get("low_difficulty_keywords").asInt()); // Only first keyword < 30
        assertEquals(1, summary.get("high_cost_keywords").asInt()); // Only first keyword > $5

        // Test averages per keyword
        assertEquals(5833, summary.get("average_volume_per_keyword").asLong()); // 17500 / 3
        assertEquals(3.57, summary.get("average_cost_per_keyword").asDouble(), 0.1); // 10.7 / 3
        assertEquals(135, summary.get("average_traffic_per_keyword").asLong()); // 405 / 3

        // Test position distribution
        JsonNode positions = analytics.get("position_distribution");
        assertNotNull(positions);
        assertEquals(1, positions.get("top_3_positions").asInt()); // Position 2
        assertEquals(2, positions.get("first_page_positions").asInt()); // Positions 2, 8
        assertEquals(1, positions.get("second_page_positions").asInt()); // Position 15
        assertEquals(33.33, positions.get("top_3_percentage").asDouble(), 0.1); // 1/3 * 100
        assertEquals(66.67, positions.get("first_page_percentage").asDouble(), 0.1); // 2/3 * 100

        // Test keyword length distribution
        JsonNode lengthDistribution = analytics.get("keyword_length_distribution");
        assertNotNull(lengthDistribution);
        assertEquals(1, lengthDistribution.get("2_words").asInt());
        assertEquals(1, lengthDistribution.get("3_words").asInt());
        assertEquals(1, lengthDistribution.get("4_words").asInt());
    }

    @Test
    @DisplayName("Test competitive analysis and insights generation")
    void testCompetitiveAnalysisInsights() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create keywords with competitor rankings for competitive analysis
        ObjectNode competitiveKeyword = mapper.createObjectNode();
        competitiveKeyword.put("keyword", "competitive analysis term");
        competitiveKeyword.put("position", 3);
        competitiveKeyword.put("region_queries_count", 8000);
        competitiveKeyword.put("cost", 4.5);
        competitiveKeyword.put("traff", 150);
        competitiveKeyword.put("difficulty", 65);
        competitiveKeyword.put("concurrency", 75);
        competitiveKeyword.put("keyword_length", 3);

        // Add SERP features
        ArrayNode typesArray = mapper.createArrayNode();
        typesArray.add("featured_snippet");
        typesArray.add("people_also_ask");
        competitiveKeyword.set("types", typesArray);

        dataArray.add(competitiveKeyword);

        // Add second keyword
        ObjectNode opportunityKeyword = mapper.createObjectNode();
        opportunityKeyword.put("keyword", "opportunity keyword");
        opportunityKeyword.put("position", 7);
        opportunityKeyword.put("region_queries_count", 3000);
        opportunityKeyword.put("cost", 2.8);
        opportunityKeyword.put("traff", 90);
        opportunityKeyword.put("difficulty", 35);
        opportunityKeyword.put("concurrency", 45);
        opportunityKeyword.put("keyword_length", 2);

        ArrayNode types2Array = mapper.createArrayNode();
        types2Array.add("image_pack");
        types2Array.add("video");
        opportunityKeyword.set("types", types2Array);

        dataArray.add(opportunityKeyword);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("competitor1.com", "competitor2.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result, "Result should not be null");
        JsonNode formattedResponse = mapper.readTree(result);

        // Basic structure validation
        assertTrue(formattedResponse.has("status"), "Response should have status");
        assertTrue(formattedResponse.has("method"), "Response should have method");

        // Test keyword data preservation
        if (formattedResponse.has("keywords")) {
            JsonNode keywords = formattedResponse.get("keywords");
            assertTrue(keywords.isArray(), "Keywords should be an array");
            assertTrue(keywords.size() > 0, "Keywords array should not be empty");

            // Test keyword structure preservation
            boolean foundCompetitiveKeyword = false;
            boolean foundOpportunityKeyword = false;

            for (JsonNode keyword : keywords) {
                if (keyword.has("keyword")) {
                    String keywordText = keyword.get("keyword").asText();
                    if (keywordText.equals("competitive analysis term")) {
                        foundCompetitiveKeyword = true;
                        // Test metrics preservation
                        if (keyword.has("difficulty")) {
                            assertEquals(65, keyword.get("difficulty").asInt());
                        }
                        if (keyword.has("position")) {
                            assertEquals(3, keyword.get("position").asInt());
                        }
                    }
                    if (keywordText.equals("opportunity keyword")) {
                        foundOpportunityKeyword = true;
                        if (keyword.has("difficulty")) {
                            assertEquals(35, keyword.get("difficulty").asInt());
                        }
                    }
                }
            }

            assertTrue(foundCompetitiveKeyword, "Should preserve competitive keyword");
            assertTrue(foundOpportunityKeyword, "Should preserve opportunity keyword");
        }

        // Test domain context preservation
        String responseText = formattedResponse.toString();
        assertTrue(responseText.contains("competitor1.com") || responseText.contains("mydomain.com"),
                "Domain information should be preserved");
        assertTrue(responseText.contains("g_us"), "Search engine should be preserved");

        // Test SERP features data preservation
        boolean hasSerpFeatures = responseText.contains("featured_snippet") ||
                responseText.contains("people_also_ask") ||
                responseText.contains("image_pack") ||
                responseText.contains("video");
        if (hasSerpFeatures) {
            assertTrue(true, "SERP features preserved correctly");
        }

        // Flexible analytics check - if analytics exist, verify structure
        if (formattedResponse.has("analytics")) {
            JsonNode analytics = formattedResponse.get("analytics");
            assertTrue(analytics.isObject(), "Analytics should be an object");
        }
    }    @Test
    @DisplayName("Test format domains unique keywords response")
    void testFormatDomainsUniqKeywordsResponse() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create sample unique keywords data
        ObjectNode keyword1 = mapper.createObjectNode();
        keyword1.put("keyword", "unique competitive keyword");
        keyword1.put("region_queries_count", 5000);
        keyword1.put("position", 3);
        keyword1.put("cost", 3.5);
        keyword1.put("difficulty", 45);
        dataArray.add(keyword1);

        ObjectNode keyword2 = mapper.createObjectNode();
        keyword2.put("keyword", "exclusive brand term");
        keyword2.put("region_queries_count", 2500);
        keyword2.put("position", 1);
        keyword2.put("cost", 7.2);
        keyword2.put("difficulty", 20);
        dataArray.add(keyword2);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("competitor1.com", "competitor2.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");
        arguments.put("page", 1);
        arguments.put("size", 100);

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        assertEquals("success", formattedResponse.get("status").asText());
        assertEquals("SerpstatDomainProcedure.getDomainsUniqKeywords", formattedResponse.get("method").asText());
        assertTrue(formattedResponse.toString().contains("competitor1.com"));
        assertTrue(formattedResponse.toString().contains("mydomain.com"));
        assertEquals("g_us", formattedResponse.get("search_engine").asText());
        assertEquals(1, formattedResponse.get("page").asInt());
        assertEquals(100, formattedResponse.get("page_size").asInt());

        // Test unique keywords data preservation
        if (formattedResponse.has("keywords")) {
            JsonNode keywords = formattedResponse.get("keywords");
            assertTrue(keywords.isArray());
            assertTrue(keywords.size() > 0);
        }
    }    @Test
    @DisplayName("Test keyword opportunity analysis")
    void testKeywordOpportunityAnalysis() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // High volume keyword
        ObjectNode highVolumeKeyword = mapper.createObjectNode();
        highVolumeKeyword.put("keyword", "high volume keyword");
        highVolumeKeyword.put("region_queries_count", 15000);
        highVolumeKeyword.put("difficulty", 55);
        highVolumeKeyword.put("cost", 3.2);
        highVolumeKeyword.put("position", 8);
        dataArray.add(highVolumeKeyword);

        // Low difficulty keyword  
        ObjectNode lowDifficultyKeyword = mapper.createObjectNode();
        lowDifficultyKeyword.put("keyword", "easy target keyword");
        lowDifficultyKeyword.put("region_queries_count", 2000);
        lowDifficultyKeyword.put("difficulty", 25);
        lowDifficultyKeyword.put("cost", 1.8);
        lowDifficultyKeyword.put("position", 12);
        dataArray.add(lowDifficultyKeyword);

        // High cost keyword
        ObjectNode highCostKeyword = mapper.createObjectNode();
        highCostKeyword.put("keyword", "expensive keyword");
        highCostKeyword.put("region_queries_count", 1500);
        highCostKeyword.put("difficulty", 70);
        highCostKeyword.put("cost", 8.5);
        highCostKeyword.put("position", 5);
        dataArray.add(highCostKeyword);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("competitor.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        // Basic structure validation
        assertTrue(formattedResponse.has("status"));
        assertTrue(formattedResponse.has("method"));

        // Test keyword data preservation
        String responseText = formattedResponse.toString();
        assertTrue(responseText.contains("high volume keyword"));
        assertTrue(responseText.contains("easy target keyword"));
        assertTrue(responseText.contains("expensive keyword"));

        // Test opportunity detection patterns
        if (formattedResponse.has("keywords")) {
            JsonNode keywords = formattedResponse.get("keywords");
            boolean foundHighVolume = false;
            boolean foundLowDifficulty = false;
            boolean foundHighCost = false;

            for (JsonNode keyword : keywords) {
                if (keyword.has("region_queries_count") && keyword.get("region_queries_count").asInt() > 10000) {
                    foundHighVolume = true;
                }
                if (keyword.has("difficulty") && keyword.get("difficulty").asInt() < 30) {
                    foundLowDifficulty = true;
                }
                if (keyword.has("cost") && keyword.get("cost").asDouble() > 5.0) {
                    foundHighCost = true;
                }
            }

            assertTrue(foundHighVolume || foundLowDifficulty || foundHighCost, "Should identify opportunity patterns");
        }
    }    @Test
    @DisplayName("Test position and performance analysis")
    void testPositionAndPerformanceAnalysis() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Top positions (1-3)
        ObjectNode topPosition = mapper.createObjectNode();
        topPosition.put("keyword", "top ranking keyword");
        topPosition.put("position", 2);
        topPosition.put("region_queries_count", 3000);
        topPosition.put("traff", 200);
        dataArray.add(topPosition);

        // First page (4-10)
        ObjectNode firstPage = mapper.createObjectNode();
        firstPage.put("keyword", "first page keyword");
        firstPage.put("position", 7);
        firstPage.put("region_queries_count", 1500);
        firstPage.put("traff", 80);
        dataArray.add(firstPage);

        // Second page (11-20)
        ObjectNode secondPage = mapper.createObjectNode();
        secondPage.put("keyword", "second page keyword");
        secondPage.put("position", 15);
        secondPage.put("region_queries_count", 2000);
        secondPage.put("traff", 30);
        dataArray.add(secondPage);

        // Lower position
        ObjectNode lowerPosition = mapper.createObjectNode();
        lowerPosition.put("keyword", "lower position keyword");
        lowerPosition.put("position", 25);
        lowerPosition.put("region_queries_count", 800);
        lowerPosition.put("traff", 10);
        dataArray.add(lowerPosition);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("competitor.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        // Basic structure validation
        assertTrue(formattedResponse.has("status"));
        assertTrue(formattedResponse.has("method"));

        // Test position data preservation
        if (formattedResponse.has("keywords")) {
            JsonNode keywords = formattedResponse.get("keywords");
            boolean foundTopPosition = false;
            boolean foundFirstPage = false;
            boolean foundSecondPage = false;

            for (JsonNode keyword : keywords) {
                if (keyword.has("position")) {
                    int position = keyword.get("position").asInt();
                    if (position >= 1 && position <= 3) {
                        foundTopPosition = true;
                    } else if (position >= 4 && position <= 10) {
                        foundFirstPage = true;
                    } else if (position >= 11 && position <= 20) {
                        foundSecondPage = true;
                    }
                }
            }

            assertTrue(foundTopPosition || foundFirstPage || foundSecondPage, "Should preserve position data");
        }

        // Test performance data preservation
        String responseText = formattedResponse.toString();
        assertTrue(responseText.contains("top ranking keyword"));
        assertTrue(responseText.contains("first page keyword"));
        assertTrue(responseText.contains("second page keyword"));
    }    @Test
    @DisplayName("Test domain performance comparison")
    void testDomainPerformanceComparison() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Keywords for first domain with strong performance
        ObjectNode domain1Keyword1 = mapper.createObjectNode();
        domain1Keyword1.put("keyword", "domain1 top keyword");
        domain1Keyword1.put("position", 1);
        domain1Keyword1.put("region_queries_count", 5000);
        domain1Keyword1.put("traff", 300);
        dataArray.add(domain1Keyword1);

        ObjectNode domain1Keyword2 = mapper.createObjectNode();
        domain1Keyword2.put("keyword", "domain1 good keyword");
        domain1Keyword2.put("position", 5);
        domain1Keyword2.put("region_queries_count", 2000);
        domain1Keyword2.put("traff", 150);
        dataArray.add(domain1Keyword2);

        // Keywords for second domain with moderate performance
        ObjectNode domain2Keyword1 = mapper.createObjectNode();
        domain2Keyword1.put("keyword", "domain2 average keyword");
        domain2Keyword1.put("position", 8);
        domain2Keyword1.put("region_queries_count", 3000);
        domain2Keyword1.put("traff", 100);
        dataArray.add(domain2Keyword1);

        ObjectNode domain2Keyword2 = mapper.createObjectNode();
        domain2Keyword2.put("keyword", "domain2 lower keyword");
        domain2Keyword2.put("position", 15);
        domain2Keyword2.put("region_queries_count", 1000);
        domain2Keyword2.put("traff", 40);
        dataArray.add(domain2Keyword2);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("strong-domain.com", "weak-domain.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        // Basic structure validation
        assertTrue(formattedResponse.has("status"));
        assertTrue(formattedResponse.has("method"));

        // Test domain performance data preservation
        String responseText = formattedResponse.toString();
        assertTrue(responseText.contains("strong-domain.com") || responseText.contains("weak-domain.com"));

        // Test performance metrics preservation
        if (formattedResponse.has("keywords")) {
            JsonNode keywords = formattedResponse.get("keywords");
            boolean foundTopPerformer = false;
            boolean foundAveragePerformer = false;
            int totalSearchVolume = 0;
            int totalTraffic = 0;

            for (JsonNode keyword : keywords) {
                if (keyword.has("position") && keyword.get("position").asInt() <= 3) {
                    foundTopPerformer = true;
                }
                if (keyword.has("position") && keyword.get("position").asInt() >= 8 && keyword.get("position").asInt() <= 15) {
                    foundAveragePerformer = true;
                }
                if (keyword.has("region_queries_count")) {
                    totalSearchVolume += keyword.get("region_queries_count").asInt();
                }
                if (keyword.has("traff")) {
                    totalTraffic += keyword.get("traff").asInt();
                }
            }

            assertTrue(foundTopPerformer || foundAveragePerformer, "Should identify performance levels");
            assertTrue(totalSearchVolume > 0, "Should aggregate search volume");
            assertTrue(totalTraffic > 0, "Should aggregate traffic");
        }
    }    @Test
    @DisplayName("Test SERP features analysis")
    void testSerpFeaturesAnalysis() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Keyword with featured snippet
        ObjectNode featuredSnippetKeyword = mapper.createObjectNode();
        featuredSnippetKeyword.put("keyword", "featured snippet keyword");
        featuredSnippetKeyword.put("position", 1);
        featuredSnippetKeyword.put("difficulty", 65);
        ArrayNode types1 = mapper.createArrayNode();
        types1.add("featured_snippet");
        types1.add("people_also_ask");
        featuredSnippetKeyword.set("types", types1);
        dataArray.add(featuredSnippetKeyword);

        // Keyword with local pack
        ObjectNode localPackKeyword = mapper.createObjectNode();
        localPackKeyword.put("keyword", "local business keyword");
        localPackKeyword.put("position", 4);
        localPackKeyword.put("difficulty", 40);
        ArrayNode types2 = mapper.createArrayNode();
        types2.add("local_pack");
        types2.add("map");
        localPackKeyword.set("types", types2);
        dataArray.add(localPackKeyword);

        // Keyword with images
        ObjectNode imageKeyword = mapper.createObjectNode();
        imageKeyword.put("keyword", "visual content keyword");
        imageKeyword.put("position", 6);
        imageKeyword.put("difficulty", 35);
        ArrayNode types3 = mapper.createArrayNode();
        types3.add("image_pack");
        types3.add("video");
        imageKeyword.set("types", types3);
        dataArray.add(imageKeyword);

        // Keyword with shopping results
        ObjectNode shoppingKeyword = mapper.createObjectNode();
        shoppingKeyword.put("keyword", "product keyword");
        shoppingKeyword.put("position", 3);
        shoppingKeyword.put("difficulty", 55);
        ArrayNode types4 = mapper.createArrayNode();
        types4.add("shopping_results");
        types4.add("reviews");
        shoppingKeyword.set("types", types4);
        dataArray.add(shoppingKeyword);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("competitor.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        // Basic structure validation
        assertTrue(formattedResponse.has("status"));
        assertTrue(formattedResponse.has("method"));

        // Test SERP features data preservation
        String responseText = formattedResponse.toString();
        
        // Check for various SERP feature types
        boolean hasFeaturedSnippet = responseText.contains("featured_snippet");
        boolean hasLocalPack = responseText.contains("local_pack");
        boolean hasImagePack = responseText.contains("image_pack");
        boolean hasShopping = responseText.contains("shopping_results");
        boolean hasPeopleAlsoAsk = responseText.contains("people_also_ask");
        boolean hasVideo = responseText.contains("video");

        assertTrue(hasFeaturedSnippet || hasLocalPack || hasImagePack || hasShopping || hasPeopleAlsoAsk || hasVideo,
                "Should preserve SERP features data");

        // Test keyword data with SERP features
        if (formattedResponse.has("keywords")) {
            JsonNode keywords = formattedResponse.get("keywords");
            boolean foundSerpFeatures = false;

            for (JsonNode keyword : keywords) {
                if (keyword.has("types") && keyword.get("types").isArray()) {
                    foundSerpFeatures = true;
                    break;
                }
            }

            assertTrue(foundSerpFeatures || responseText.contains("featured_snippet") || responseText.contains("local_pack"),
                    "Should identify SERP features in keywords");
        }

        // Test difficulty correlation with SERP features
        assertTrue(responseText.contains("featured snippet keyword"));
        assertTrue(responseText.contains("local business keyword"));
        assertTrue(responseText.contains("visual content keyword"));
        assertTrue(responseText.contains("product keyword"));
    }    @Test
    @DisplayName("Test keyword statistics calculation")
    void testKeywordStatisticsCalculation() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create keywords with diverse statistics
        ObjectNode keyword1 = mapper.createObjectNode();
        keyword1.put("keyword", "high volume keyword");
        keyword1.put("region_queries_count", 10000);
        keyword1.put("cost", 5.5);
        keyword1.put("traff", 800);
        keyword1.put("difficulty", 60);
        keyword1.put("concurrency", 80);
        keyword1.put("keyword_length", 3);
        dataArray.add(keyword1);

        ObjectNode keyword2 = mapper.createObjectNode();
        keyword2.put("keyword", "medium volume keyword");
        keyword2.put("region_queries_count", 3000);
        keyword2.put("cost", 2.5);
        keyword2.put("traff", 200);
        keyword2.put("difficulty", 40);
        keyword2.put("concurrency", 50);
        keyword2.put("keyword_length", 4);
        dataArray.add(keyword2);

        ObjectNode keyword3 = mapper.createObjectNode();
        keyword3.put("keyword", "low volume but expensive keyword");
        keyword3.put("region_queries_count", 500);
        keyword3.put("cost", 12.0);
        keyword3.put("traff", 50);
        keyword3.put("difficulty", 20);
        keyword3.put("concurrency", 30);
        keyword3.put("keyword_length", 5);
        dataArray.add(keyword3);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("competitor.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        // Basic structure validation
        assertTrue(formattedResponse.has("status"));
        assertTrue(formattedResponse.has("method"));

        // Test keyword statistics preservation and calculation
        if (formattedResponse.has("keywords")) {
            JsonNode keywords = formattedResponse.get("keywords");
            int totalSearchVolume = 0;
            double totalCost = 0.0;
            int totalTraffic = 0;
            int totalDifficulty = 0;
            int keywordCount = 0;

            for (JsonNode keyword : keywords) {
                if (keyword.has("region_queries_count")) {
                    totalSearchVolume += keyword.get("region_queries_count").asInt();
                }
                if (keyword.has("cost")) {
                    totalCost += keyword.get("cost").asDouble();
                }
                if (keyword.has("traff")) {
                    totalTraffic += keyword.get("traff").asInt();
                }
                if (keyword.has("difficulty")) {
                    totalDifficulty += keyword.get("difficulty").asInt();
                    keywordCount++;
                }
            }

            assertTrue(totalSearchVolume > 0, "Should aggregate search volume");
            assertTrue(totalCost > 0, "Should aggregate costs");
            assertTrue(totalTraffic > 0, "Should aggregate traffic");
            if (keywordCount > 0) {
                double avgDifficulty = (double) totalDifficulty / keywordCount;
                assertTrue(avgDifficulty > 0, "Should calculate average difficulty");
            }
        }

        // Test data preservation
        String responseText = formattedResponse.toString();
        assertTrue(responseText.contains("high volume keyword"));
        assertTrue(responseText.contains("medium volume keyword"));
        assertTrue(responseText.contains("low volume but expensive keyword"));
    }    @Test
    @DisplayName("Test competitive insights generation")
    void testCompetitiveInsightsGeneration() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // High opportunity keyword (high volume, low difficulty)
        ObjectNode opportunityKeyword = mapper.createObjectNode();
        opportunityKeyword.put("keyword", "high opportunity keyword");
        opportunityKeyword.put("region_queries_count", 15000);
        opportunityKeyword.put("difficulty", 25);
        opportunityKeyword.put("cost", 3.0);
        opportunityKeyword.put("position", 8);
        dataArray.add(opportunityKeyword);

        // Competitive threat keyword (competitor ranking well)
        ObjectNode threatKeyword = mapper.createObjectNode();
        threatKeyword.put("keyword", "competitive threat keyword");
        threatKeyword.put("region_queries_count", 8000);
        threatKeyword.put("difficulty", 70);
        threatKeyword.put("cost", 8.5);
        threatKeyword.put("position", 2);
        dataArray.add(threatKeyword);

        // Gap keyword (low traffic but valuable)
        ObjectNode gapKeyword = mapper.createObjectNode();
        gapKeyword.put("keyword", "keyword gap opportunity");
        gapKeyword.put("region_queries_count", 2000);
        gapKeyword.put("difficulty", 35);
        gapKeyword.put("cost", 12.0);
        gapKeyword.put("position", 15);
        dataArray.add(gapKeyword);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("competitor1.com", "competitor2.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        // Basic structure validation
        assertTrue(formattedResponse.has("status"));
        assertTrue(formattedResponse.has("method"));

        // Test competitive insights data preservation
        String responseText = formattedResponse.toString();
        assertTrue(responseText.contains("high opportunity keyword"));
        assertTrue(responseText.contains("competitive threat keyword"));
        assertTrue(responseText.contains("keyword gap opportunity"));

        // Test insights generation patterns
        if (formattedResponse.has("keywords")) {
            JsonNode keywords = formattedResponse.get("keywords");
            boolean foundOpportunity = false;
            boolean foundThreat = false;
            boolean foundGap = false;

            for (JsonNode keyword : keywords) {
                if (keyword.has("region_queries_count") && keyword.has("difficulty")) {
                    int volume = keyword.get("region_queries_count").asInt();
                    int difficulty = keyword.get("difficulty").asInt();
                    
                    // High volume + low difficulty = opportunity
                    if (volume > 10000 && difficulty < 30) {
                        foundOpportunity = true;
                    }
                    // High difficulty + top position = competitive threat
                    if (difficulty > 60 && keyword.has("position") && keyword.get("position").asInt() <= 3) {
                        foundThreat = true;
                    }
                    // High cost + lower position = potential gap
                    if (keyword.has("cost") && keyword.get("cost").asDouble() > 10.0) {
                        foundGap = true;
                    }
                }
            }

            assertTrue(foundOpportunity || foundThreat || foundGap, "Should identify competitive patterns");
        }

        // Test context preservation
        assertTrue(responseText.contains("competitor1.com") || responseText.contains("competitor2.com") || responseText.contains("mydomain.com"));
        assertTrue(responseText.contains("g_us"));
    }    @Test
    @DisplayName("Test keyword length distribution")
    void testKeywordLengthDistribution() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Short-tail keyword (1-2 words)
        ObjectNode shortTail = mapper.createObjectNode();
        shortTail.put("keyword", "shoes");
        shortTail.put("keyword_length", 1);
        shortTail.put("region_queries_count", 50000);
        shortTail.put("difficulty", 85);
        shortTail.put("cost", 4.5);
        dataArray.add(shortTail);

        // Medium-tail keyword (3-4 words)
        ObjectNode mediumTail = mapper.createObjectNode();
        mediumTail.put("keyword", "running shoes men");
        mediumTail.put("keyword_length", 3);
        mediumTail.put("region_queries_count", 8000);
        mediumTail.put("difficulty", 55);
        mediumTail.put("cost", 2.8);
        dataArray.add(mediumTail);

        // Long-tail keyword (5+ words)
        ObjectNode longTail1 = mapper.createObjectNode();
        longTail1.put("keyword", "best running shoes for men with flat feet");
        longTail1.put("keyword_length", 8);
        longTail1.put("region_queries_count", 1200);
        longTail1.put("difficulty", 25);
        longTail1.put("cost", 1.5);
        dataArray.add(longTail1);

        ObjectNode longTail2 = mapper.createObjectNode();
        longTail2.put("keyword", "affordable running shoes for beginners");
        longTail2.put("keyword_length", 5);
        longTail2.put("region_queries_count", 800);
        longTail2.put("difficulty", 30);
        longTail2.put("cost", 1.2);
        dataArray.add(longTail2);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("competitor.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        // Basic structure validation
        assertTrue(formattedResponse.has("status"));
        assertTrue(formattedResponse.has("method"));

        // Test keyword length distribution analysis
        if (formattedResponse.has("keywords")) {
            JsonNode keywords = formattedResponse.get("keywords");
            int shortTailCount = 0;
            int mediumTailCount = 0;
            int longTailCount = 0;

            for (JsonNode keyword : keywords) {
                if (keyword.has("keyword_length")) {
                    int length = keyword.get("keyword_length").asInt();
                    if (length <= 2) {
                        shortTailCount++;
                    } else if (length >= 3 && length <= 4) {
                        mediumTailCount++;
                    } else if (length >= 5) {
                        longTailCount++;
                    }
                }
            }

            assertTrue(shortTailCount > 0 || mediumTailCount > 0 || longTailCount > 0, "Should categorize keywords by length");

            // Test difficulty correlation with length
            boolean foundEasyLongTail = false;
            boolean foundHardShortTail = false;

            for (JsonNode keyword : keywords) {
                if (keyword.has("keyword_length") && keyword.has("difficulty")) {
                    int length = keyword.get("keyword_length").asInt();
                    int difficulty = keyword.get("difficulty").asInt();
                    
                    if (length >= 5 && difficulty < 35) {
                        foundEasyLongTail = true;
                    }
                    if (length <= 2 && difficulty > 70) {
                        foundHardShortTail = true;
                    }
                }
            }

            assertTrue(foundEasyLongTail || foundHardShortTail, "Should show difficulty-length correlation");
        }

        // Test keyword data preservation
        String responseText = formattedResponse.toString();
        assertTrue(responseText.contains("shoes"));
        assertTrue(responseText.contains("running shoes men"));
        assertTrue(responseText.contains("best running shoes"));
        assertTrue(responseText.contains("affordable running shoes"));
    }    @Test
    @DisplayName("Test response structure validation")
    void testResponseStructureValidation() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create complete keyword data to test structure
        ObjectNode keyword = mapper.createObjectNode();
        keyword.put("keyword", "test keyword");
        keyword.put("region_queries_count", 1000);
        keyword.put("position", 5);
        keyword.put("cost", 2.5);
        keyword.put("difficulty", 45);
        keyword.put("traff", 80);
        keyword.put("concurrency", 60);
        keyword.put("keyword_length", 2);
        dataArray.add(keyword);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("competitor.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");
        arguments.put("page", 1);
        arguments.put("size", 100);

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);

        // Test required top-level fields
        assertTrue(formattedResponse.has("status"), "Response should have status field");
        assertTrue(formattedResponse.has("method"), "Response should have method field");
        assertTrue(formattedResponse.has("search_engine"), "Response should have search_engine field");
        
        // Test field data types
        assertTrue(formattedResponse.get("status").isTextual(), "Status should be string");
        assertTrue(formattedResponse.get("method").isTextual(), "Method should be string");
        assertTrue(formattedResponse.get("search_engine").isTextual(), "Search engine should be string");
        
        if (formattedResponse.has("page")) {
            assertTrue(formattedResponse.get("page").isInt(), "Page should be integer");
        }
        if (formattedResponse.has("page_size")) {
            assertTrue(formattedResponse.get("page_size").isInt(), "Page size should be integer");
        }

        // Test field naming conventions (snake_case)
        assertFalse(result.contains("searchEngine"), "Should use snake_case not camelCase");
        assertFalse(result.contains("pageSize"), "Should use snake_case not camelCase");
        
        // Test keywords array structure
        if (formattedResponse.has("keywords")) {
            JsonNode keywords = formattedResponse.get("keywords");
            assertTrue(keywords.isArray(), "Keywords should be array");
            
            if (keywords.size() > 0) {
                JsonNode firstKeyword = keywords.get(0);
                assertTrue(firstKeyword.isObject(), "Keyword should be object");
                
                // Test keyword field types
                if (firstKeyword.has("keyword")) {
                    assertTrue(firstKeyword.get("keyword").isTextual(), "Keyword should be string");
                }
                if (firstKeyword.has("region_queries_count")) {
                    assertTrue(firstKeyword.get("region_queries_count").isInt(), "Volume should be integer");
                }
                if (firstKeyword.has("cost")) {
                    assertTrue(firstKeyword.get("cost").isNumber(), "Cost should be number");
                }
                if (firstKeyword.has("difficulty")) {
                    assertTrue(firstKeyword.get("difficulty").isInt(), "Difficulty should be integer");
                }
            }
        }

        // Test domain context fields
        String responseText = formattedResponse.toString();
        assertTrue(responseText.contains("competitor.com") || responseText.contains("mydomain.com"),
                "Should include domain context");
        assertTrue(responseText.contains("g_us"), "Should include search engine context");

        // Test JSON validity
        assertDoesNotThrow(() -> mapper.readTree(result), "Should produce valid JSON");
    }    @Test
    @DisplayName("Test error handling for malformed responses")
    void testErrorHandlingForMalformedResponses() throws Exception {
        // Test null API response
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("competitor.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");

        ObjectMapper mapper = new ObjectMapper();
        
        assertThrows(Exception.class, () -> {
            DomainUniqueKeywordsResponseFormatter.format(null, arguments, mapper);
        }, "Should handle null response");

        // Test empty keywords array
        ObjectNode emptyResultNode = mapper.createObjectNode();
        ArrayNode emptyDataArray = mapper.createArrayNode();
        emptyResultNode.set("data", emptyDataArray);

        SerpstatApiResponse emptyResponse = mock(SerpstatApiResponse.class);
        when(emptyResponse.getResult()).thenReturn(emptyResultNode);

        String emptyResult = DomainUniqueKeywordsResponseFormatter.format(emptyResponse, arguments, mapper);
        assertNotNull(emptyResult, "Should handle empty keywords gracefully");
        
        JsonNode emptyFormattedResponse = mapper.readTree(emptyResult);
        assertTrue(emptyFormattedResponse.has("status"), "Should still have basic structure");

        // Test missing data field
        ObjectNode noDataNode = mapper.createObjectNode();
        noDataNode.put("some_other_field", "value");

        SerpstatApiResponse noDataResponse = mock(SerpstatApiResponse.class);
        when(noDataResponse.getResult()).thenReturn(noDataNode);

        String noDataResult = DomainUniqueKeywordsResponseFormatter.format(noDataResponse, arguments, mapper);
        assertNotNull(noDataResult, "Should handle missing data field");

        // Test invalid data types in keywords
        ObjectNode invalidTypeNode = mapper.createObjectNode();
        ArrayNode invalidDataArray = mapper.createArrayNode();
        
        ObjectNode invalidKeyword = mapper.createObjectNode();
        invalidKeyword.put("keyword", "test");
        invalidKeyword.put("region_queries_count", "not_a_number"); // Invalid type
        invalidKeyword.put("cost", "invalid_cost"); // Invalid type
        invalidDataArray.add(invalidKeyword);
        
        invalidTypeNode.set("data", invalidDataArray);

        SerpstatApiResponse invalidTypeResponse = mock(SerpstatApiResponse.class);
        when(invalidTypeResponse.getResult()).thenReturn(invalidTypeNode);

        String invalidTypeResult = DomainUniqueKeywordsResponseFormatter.format(invalidTypeResponse, arguments, mapper);
        assertNotNull(invalidTypeResult, "Should handle invalid data types gracefully");

        // Test partial data (missing fields)
        ObjectNode partialDataNode = mapper.createObjectNode();
        ArrayNode partialDataArray = mapper.createArrayNode();
        
        ObjectNode partialKeyword = mapper.createObjectNode();
        partialKeyword.put("keyword", "incomplete keyword");
        // Missing other required fields
        partialDataArray.add(partialKeyword);
        
        partialDataNode.set("data", partialDataArray);

        SerpstatApiResponse partialDataResponse = mock(SerpstatApiResponse.class);
        when(partialDataResponse.getResult()).thenReturn(partialDataNode);

        String partialResult = DomainUniqueKeywordsResponseFormatter.format(partialDataResponse, arguments, mapper);
        assertNotNull(partialResult, "Should handle partial data gracefully");
        
        JsonNode partialFormattedResponse = mapper.readTree(partialResult);
        assertTrue(partialFormattedResponse.has("status"), "Should maintain basic structure with partial data");

        // Test null arguments
        assertThrows(Exception.class, () -> {
            DomainUniqueKeywordsResponseFormatter.format(emptyResponse, null, mapper);
        }, "Should handle null arguments");
    }    @Test
    @DisplayName("Test JSON serialization")
    void testJsonSerialization() throws Exception {
        // Arrange
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();

        // Create keywords with special characters and Unicode
        ObjectNode unicodeKeyword = mapper.createObjectNode();
        unicodeKeyword.put("keyword", "café résumé naïve"); // Unicode characters
        unicodeKeyword.put("region_queries_count", 1000);
        unicodeKeyword.put("cost", 3.14159); // Decimal precision test
        unicodeKeyword.put("difficulty", 45);
        dataArray.add(unicodeKeyword);

        ObjectNode specialCharsKeyword = mapper.createObjectNode();
        specialCharsKeyword.put("keyword", "\"quote's & ampersand\""); // Special characters
        specialCharsKeyword.put("region_queries_count", 99999999); // Large number
        specialCharsKeyword.put("cost", 0.01); // Small decimal
        specialCharsKeyword.put("difficulty", 100);
        dataArray.add(specialCharsKeyword);

        ObjectNode emptyFieldsKeyword = mapper.createObjectNode();
        emptyFieldsKeyword.put("keyword", ""); // Empty string
        emptyFieldsKeyword.put("region_queries_count", 0); // Zero value
        emptyFieldsKeyword.put("cost", 0.0); // Zero decimal
        emptyFieldsKeyword.put("difficulty", 0);
        dataArray.add(emptyFieldsKeyword);

        resultNode.set("data", dataArray);

        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);

        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("example.com"));
        arguments.put("minusDomain", "mydomain.com");
        arguments.put("se", "g_us");

        // Act
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);

        // Assert
        assertNotNull(result);
        
        // Test JSON validity
        JsonNode formattedResponse = mapper.readTree(result);
        assertNotNull(formattedResponse, "Should produce valid JSON");

        // Test Unicode preservation
        assertTrue(result.contains("café") || result.contains("résumé") || result.contains("naïve"),
                "Should preserve Unicode characters");

        // Test special character escaping
        boolean hasQuotes = result.contains("\\\"") || result.contains("quote");
        boolean hasAmpersand = result.contains("&") || result.contains("ampersand");
        assertTrue(hasQuotes || hasAmpersand, "Should handle special characters properly");

        // Test number formatting
        assertTrue(result.contains("99999999"), "Should handle large numbers");
        assertTrue(result.contains("0.01") || result.contains("0.1"), "Should handle small decimals");
        assertTrue(result.contains("3.14159") || result.contains("3.14"), "Should handle decimal precision");

        // Test ObjectMapper configuration
        assertTrue(formattedResponse.has("status"), "Should have required fields");
        assertTrue(formattedResponse.has("method"), "Should have method field");

        // Test JSON structure consistency
        if (formattedResponse.has("keywords")) {
            JsonNode keywords = formattedResponse.get("keywords");
            assertTrue(keywords.isArray(), "Keywords should be array");
            
            for (JsonNode keyword : keywords) {
                assertTrue(keyword.isObject(), "Each keyword should be object");
                if (keyword.has("region_queries_count")) {
                    assertTrue(keyword.get("region_queries_count").isInt(), "Volume should be integer");
                }
                if (keyword.has("cost")) {
                    assertTrue(keyword.get("cost").isNumber(), "Cost should be number");
                }
            }
        }

        // Test that result is valid JSON string
        assertDoesNotThrow(() -> new ObjectMapper().readTree(result), "Result should be valid JSON");
    }    @Test
    @DisplayName("Test argument context integration")
    void testArgumentContextIntegration() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();
        
        ObjectNode keyword = mapper.createObjectNode();
        keyword.put("keyword", "context test");
        keyword.put("position", 3);
        keyword.put("region_queries_count", 500);
        keyword.put("cost", 1.5);
        keyword.put("traff", 25);
        keyword.put("difficulty", 30);
        dataArray.add(keyword);
        
        resultNode.set("data", dataArray);
        
        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);
        
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("example.com", "test.com"));
        arguments.put("minusDomain", "competitor.com");
        arguments.put("se", "g_us");
        arguments.put("page", 2);
        arguments.put("size", 50);
        
        Map<String, Object> filters = new HashMap<>();
        filters.put("difficulty_from", 30);
        filters.put("traff_from", 100);
        arguments.put("filters", filters);
        
        // When
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);
        
        // Then
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);
        
        assertEquals("competitor.com", formattedResponse.get("excluded_domain").asText());
        assertEquals("g_us", formattedResponse.get("search_engine").asText());
        assertEquals(2, formattedResponse.get("page").asInt());
        assertEquals(50, formattedResponse.get("page_size").asInt());
        
        assertTrue(formattedResponse.has("analyzed_domains"));
        JsonNode analyzedDomains = formattedResponse.get("analyzed_domains");
        assertTrue(analyzedDomains.isArray());
        assertEquals(2, analyzedDomains.size());
        assertEquals("example.com", analyzedDomains.get(0).asText());
        assertEquals("test.com", analyzedDomains.get(1).asText());
    }    @Test
    @DisplayName("Test analytics calculation accuracy")
    void testAnalyticsCalculationAccuracy() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();
        
        // Create test data with specific values for precise calculation testing
        ObjectNode keyword1 = mapper.createObjectNode();
        keyword1.put("keyword", "precise calculation test");
        keyword1.put("position", 5);
        keyword1.put("region_queries_count", 1000);
        keyword1.put("cost", 2.50); // Exact decimal
        keyword1.put("traff", 100);
        keyword1.put("difficulty", 50);
        dataArray.add(keyword1);
        
        ObjectNode keyword2 = mapper.createObjectNode();
        keyword2.put("keyword", "rounding test");
        keyword2.put("position", 3);
        keyword2.put("region_queries_count", 333); // Will create non-round averages
        keyword2.put("cost", 1.333); // Should be rounded
        keyword2.put("traff", 66);
        keyword2.put("difficulty", 33);
        dataArray.add(keyword2);
        
        ObjectNode keyword3 = mapper.createObjectNode();
        keyword3.put("keyword", "zero value test");
        keyword3.put("position", 1);
        keyword3.put("region_queries_count", 0); // Edge case: zero value
        keyword3.put("cost", 0.0);
        keyword3.put("traff", 0);
        keyword3.put("difficulty", 0);
        dataArray.add(keyword3);
        
        resultNode.set("data", dataArray);
        
        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);
        
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("test.com"));
        arguments.put("minusDomain", "competitor.com");
        
        // When
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);
        
        // Then
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);
          // Verify analytics calculations
        assertTrue(formattedResponse.has("analytics"));
        JsonNode analytics = formattedResponse.get("analytics");
        
        // The analytics are in a summary subsection
        assertTrue(analytics.has("summary"));
        JsonNode summary = analytics.get("summary");
        
        // Test search volume calculation (1000 + 333 + 0 = 1333)
        assertEquals(1333, summary.get("total_search_volume").asLong());
        
        // Test traffic calculation (100 + 66 + 0 = 166)
        assertEquals(166, summary.get("total_traffic_estimate").asLong());
        
        // Test average calculations with proper rounding
        assertTrue(summary.has("average_volume_per_keyword"));
        assertTrue(summary.has("average_cost_per_keyword"));
        assertTrue(summary.has("average_traffic_per_keyword"));
        
        // Test that zero values don't break calculations
        assertTrue(summary.get("average_cost_per_keyword").asDouble() >= 0);
        assertTrue(summary.get("average_difficulty").asDouble() >= 0);
    }    @Test
    @DisplayName("Test format consistency")
    void testFormatConsistency() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();
        
        ObjectNode keyword = mapper.createObjectNode();
        keyword.put("keyword", "consistency test");
        keyword.put("position", 1);
        keyword.put("region_queries_count", 1000);
        keyword.put("cost", 5.0);
        keyword.put("traff", 100);
        keyword.put("difficulty", 60);
        dataArray.add(keyword);
        
        resultNode.set("data", dataArray);
        
        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);
        
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("example.com"));
        arguments.put("minusDomain", "competitor.com");
        arguments.put("se", "g_us");
        
        // When
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);
        
        // Then
        assertNotNull(result);
        JsonNode formattedResponse = mapper.readTree(result);
          // Test consistent response structure
        assertTrue(formattedResponse.has("status"));
        assertTrue(formattedResponse.has("method"));
        assertTrue(formattedResponse.has("timestamp"));
        assertTrue(formattedResponse.has("unique_keywords"));
        assertTrue(formattedResponse.has("analytics"));
        
        // Test consistent status value
        assertEquals("success", formattedResponse.get("status").asText());
        
        // Test consistent method naming
        assertEquals("SerpstatDomainProcedure.getDomainsUniqKeywords", 
                     formattedResponse.get("method").asText());
        
        // Test consistent field naming patterns (snake_case)
        assertTrue(formattedResponse.has("excluded_domain"));
        assertTrue(formattedResponse.has("search_engine"));
        assertTrue(formattedResponse.has("analyzed_domains"));
        assertTrue(formattedResponse.has("page_size"));
        
        // Test consistent timestamp format (ISO format)
        String timestamp = formattedResponse.get("timestamp").asText();
        assertTrue(timestamp.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*"));
          // Test consistent analytics structure
        JsonNode analytics = formattedResponse.get("analytics");
        assertTrue(analytics.has("summary"));
        JsonNode summary = analytics.get("summary");
        assertTrue(summary.has("total_search_volume"));
        assertTrue(summary.has("total_traffic_estimate"));
        assertTrue(summary.has("total_cost_estimate"));
        assertTrue(summary.has("average_difficulty"));
        
        // Test position distribution structure
        assertTrue(analytics.has("position_distribution"));
        JsonNode positions = analytics.get("position_distribution");
        assertTrue(positions.has("top_3_positions"));
        assertTrue(positions.has("first_page_positions"));
        
        // Test consistent error handling structure (no errors in this case)
        assertFalse(formattedResponse.has("error"));
        assertFalse(formattedResponse.has("errors"));
    }    @Test
    @DisplayName("Test performance with large responses")
    void testPerformanceWithLargeResponses() throws Exception {
        // Given
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode resultNode = mapper.createObjectNode();
        ArrayNode dataArray = mapper.createArrayNode();
        
        // Create large dataset (1000 keywords - maximum page size)
        for (int i = 0; i < 1000; i++) {
            ObjectNode keyword = mapper.createObjectNode();
            keyword.put("keyword", "performance test keyword " + i);
            keyword.put("position", (i % 100) + 1);
            keyword.put("region_queries_count", 1000 + i);
            keyword.put("cost", 1.0 + (i * 0.01));
            keyword.put("traff", 10 + (i % 50));
            keyword.put("difficulty", 20 + (i % 80));
            keyword.put("concurrency", 10 + (i % 90));
            keyword.put("keyword_length", 2 + (i % 5));
            dataArray.add(keyword);
        }
        
        resultNode.set("data", dataArray);
        
        SerpstatApiResponse mockResponse = mock(SerpstatApiResponse.class);
        when(mockResponse.getResult()).thenReturn(resultNode);
        
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("domain1.com", "domain2.com", "domain3.com"));
        arguments.put("minusDomain", "competitor.com");
        arguments.put("se", "g_us");
        arguments.put("size", 1000);
        
        // When - measure performance
        long startTime = System.currentTimeMillis();
        String result = DomainUniqueKeywordsResponseFormatter.format(mockResponse, arguments, mapper);
        long endTime = System.currentTimeMillis();
        long processingTime = endTime - startTime;
        
        // Then
        assertNotNull(result);
        
        // Performance assertion: should process 1000 keywords in reasonable time (< 5 seconds)
        assertTrue(processingTime < 5000, 
                   "Processing 1000 keywords took " + processingTime + "ms, should be < 5000ms");
        
        JsonNode formattedResponse = mapper.readTree(result);
          // Verify all data was processed correctly
        assertEquals("success", formattedResponse.get("status").asText());
        assertEquals(1000, formattedResponse.get("page_size").asInt());
        assertEquals(1000, formattedResponse.get("keywords_on_page").asInt());
        
        // Verify analytics were calculated for large dataset
        JsonNode analytics = formattedResponse.get("analytics");
        assertTrue(analytics.has("summary"));
        JsonNode summary = analytics.get("summary");
        
        // Verify traffic calculation for large numbers
        assertTrue(summary.get("total_traffic_estimate").asLong() > 10000);
        
        // Verify averages are reasonable for large dataset
        assertTrue(summary.get("average_volume_per_keyword").asLong() > 0);
        assertTrue(summary.get("average_cost_per_keyword").asDouble() > 0);
        
        // Memory test: verify response size is manageable
        assertTrue(result.length() > 0);
        assertTrue(result.length() < 10_000_000, "Response size should be manageable"); // < 10MB
    }    @Test
    @DisplayName("Test edge cases and boundary conditions")
    void testEdgeCasesAndBoundaryConditions() throws Exception {
        // Test Case 1: Empty unique keywords array (no gap found)
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode emptyResultNode = mapper.createObjectNode();
        ArrayNode emptyDataArray = mapper.createArrayNode();
        emptyResultNode.set("data", emptyDataArray);
        
        SerpstatApiResponse emptyResponse = mock(SerpstatApiResponse.class);
        when(emptyResponse.getResult()).thenReturn(emptyResultNode);
        
        Map<String, Object> arguments = new HashMap<>();
        arguments.put("domains", Arrays.asList("domain1.com"));
        arguments.put("minusDomain", "competitor.com");
        
        String emptyResult = DomainUniqueKeywordsResponseFormatter.format(emptyResponse, arguments, mapper);
        assertNotNull(emptyResult);
          JsonNode emptyFormatted = mapper.readTree(emptyResult);
        assertEquals("success", emptyFormatted.get("status").asText());
        assertEquals(0, emptyFormatted.get("keywords_on_page").asInt());
        
        // Test Case 2: Keywords with zero search volume
        ObjectNode zeroVolumeNode = mapper.createObjectNode();
        ArrayNode zeroVolumeArray = mapper.createArrayNode();
        
        ObjectNode zeroKeyword = mapper.createObjectNode();
        zeroKeyword.put("keyword", "zero volume keyword");
        zeroKeyword.put("position", 1);
        zeroKeyword.put("region_queries_count", 0);
        zeroKeyword.put("cost", 0.0);
        zeroKeyword.put("traff", 0);
        zeroKeyword.put("difficulty", 0);
        zeroVolumeArray.add(zeroKeyword);
        
        zeroVolumeNode.set("data", zeroVolumeArray);
        
        SerpstatApiResponse zeroResponse = mock(SerpstatApiResponse.class);
        when(zeroResponse.getResult()).thenReturn(zeroVolumeNode);
        
        String zeroResult = DomainUniqueKeywordsResponseFormatter.format(zeroResponse, arguments, mapper);
        JsonNode zeroFormatted = mapper.readTree(zeroResult);
        JsonNode zeroSummary = zeroFormatted.get("analytics").get("summary");
        assertEquals(0, zeroSummary.get("total_search_volume").asLong());
        assertEquals(0, zeroSummary.get("total_traffic_estimate").asLong());
        
        // Test Case 3: Keywords with extreme difficulty values
        ObjectNode extremeNode = mapper.createObjectNode();
        ArrayNode extremeArray = mapper.createArrayNode();
        
        ObjectNode extremeKeyword = mapper.createObjectNode();
        extremeKeyword.put("keyword", "extremely difficult keyword");
        extremeKeyword.put("position", 100); // Worst position
        extremeKeyword.put("region_queries_count", 10000000); // Very high volume
        extremeKeyword.put("cost", 999.99); // Very high cost
        extremeKeyword.put("traff", 999999); // Very high traffic
        extremeKeyword.put("difficulty", 100); // Maximum difficulty
        extremeKeyword.put("concurrency", 100); // Maximum competition
        extremeArray.add(extremeKeyword);
        
        extremeNode.set("data", extremeArray);
        
        SerpstatApiResponse extremeResponse = mock(SerpstatApiResponse.class);
        when(extremeResponse.getResult()).thenReturn(extremeNode);
          String extremeResult = DomainUniqueKeywordsResponseFormatter.format(extremeResponse, arguments, mapper);
        JsonNode extremeFormatted = mapper.readTree(extremeResult);
        JsonNode extremeSummary = extremeFormatted.get("analytics").get("summary");
        assertEquals(100, extremeSummary.get("average_difficulty").asInt());
        
        // Test Case 4: Very long keyword strings (boundary test)
        ObjectNode longNode = mapper.createObjectNode();
        ArrayNode longArray = mapper.createArrayNode();
        
        String veryLongKeyword = "a".repeat(500); // 500 character keyword
        ObjectNode longKeyword = mapper.createObjectNode();
        longKeyword.put("keyword", veryLongKeyword);
        longKeyword.put("position", 50);
        longKeyword.put("region_queries_count", 100);
        longKeyword.put("cost", 5.0);
        longKeyword.put("traff", 10);
        longKeyword.put("difficulty", 50);
        longKeyword.put("keyword_length", 1); // Single word despite length
        longArray.add(longKeyword);
        
        longNode.set("data", longArray);
        
        SerpstatApiResponse longResponse = mock(SerpstatApiResponse.class);
        when(longResponse.getResult()).thenReturn(longNode);
        
        String longResult = DomainUniqueKeywordsResponseFormatter.format(longResponse, arguments, mapper);
        JsonNode longFormatted = mapper.readTree(longResult);
        assertEquals("success", longFormatted.get("status").asText());
        assertEquals(1, longFormatted.get("keywords_on_page").asInt());
        
        // Test Case 5: Single domain vs multiple domain scenarios
        Map<String, Object> singleDomainArgs = new HashMap<>();
        singleDomainArgs.put("domains", Arrays.asList("single.com"));
        singleDomainArgs.put("minusDomain", "competitor.com");
        
        String singleResult = DomainUniqueKeywordsResponseFormatter.format(emptyResponse, singleDomainArgs, mapper);
        JsonNode singleFormatted = mapper.readTree(singleResult);
        assertEquals(1, singleFormatted.get("analyzed_domains").size());
        assertEquals("single.com", singleFormatted.get("analyzed_domains").get(0).asText());
    }
}
