package com.serpstat.domains.credits;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.serpstat.core.SerpstatApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreditsResponseFormatter class.
 * 
 * Tests the formatting of API responses for credits/limits domain.
 */
@DisplayName("CreditsResponseFormatter Tests")
class CreditsResponseFormatterTest {

    private ObjectMapper objectMapper;
    private Map<String, Object> arguments;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        arguments = new HashMap<>();
    }

    @Test
    @DisplayName("Should format basic credits response successfully")
    void testFormatBasicResponse() throws Exception {
        // Arrange
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("max_lines", 10000);
        dataNode.put("used_lines", 2500);
        dataNode.put("left_lines", 7500);
        
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("data", dataNode);
        
        SerpstatApiResponse response = new SerpstatApiResponse(resultNode, "getStats", arguments);

        // Act
        String result = CreditsResponseFormatter.format(response, arguments, objectMapper);

        // Assert
        JsonNode resultJson = objectMapper.readTree(result);
        
        assertEquals("success", resultJson.get("status").asText());
        assertEquals("SerpstatLimitsProcedure.getStats", resultJson.get("method").asText());
        assertNotNull(resultJson.get("timestamp"));
        
        // Verify API stats
        JsonNode apiStats = resultJson.get("api_stats");
        assertNotNull(apiStats);
        assertEquals(10000, apiStats.get("max_lines").asLong());
        assertEquals(2500, apiStats.get("used_lines").asLong());
        assertEquals(7500, apiStats.get("left_lines").asLong());
        
        // Verify analytics structure exists
        JsonNode analytics = resultJson.get("analytics");
        assertNotNull(analytics);
        assertNotNull(analytics.get("usage"));
        assertNotNull(analytics.get("recommendations"));
    }

    @Test
    @DisplayName("Should handle null or empty data gracefully")
    void testFormatWithNullData() throws Exception {
        // Arrange
        ObjectNode resultNode = objectMapper.createObjectNode();
        // No data node added
        
        SerpstatApiResponse response = new SerpstatApiResponse(resultNode, "getStats", arguments);

        // Act
        String result = CreditsResponseFormatter.format(response, arguments, objectMapper);

        // Assert
        JsonNode resultJson = objectMapper.readTree(result);
        
        assertEquals("success", resultJson.get("status").asText());
        assertNotNull(resultJson.get("api_stats"));
        assertTrue(resultJson.get("api_stats").isEmpty());
    }

    @Test
    @DisplayName("Should calculate usage percentage correctly")
    void testUsagePercentageCalculation() throws Exception {
        // Test case 1: 25% usage
        ObjectNode dataNode1 = objectMapper.createObjectNode();
        dataNode1.put("max_lines", 10000);
        dataNode1.put("used_lines", 2500);
        dataNode1.put("left_lines", 7500);
        
        ObjectNode resultNode1 = objectMapper.createObjectNode();
        resultNode1.set("data", dataNode1);
        
        SerpstatApiResponse response1 = new SerpstatApiResponse(resultNode1, "getStats", arguments);
        String result1 = CreditsResponseFormatter.format(response1, arguments, objectMapper);
        JsonNode resultJson1 = objectMapper.readTree(result1);
        
        JsonNode usage1 = resultJson1.get("analytics").get("usage");
        assertEquals(25.0, usage1.get("usage_percentage").asDouble());
        assertEquals("NORMAL", usage1.get("status").asText());
        assertEquals("🟢", usage1.get("status_icon").asText());

        // Test case 2: Zero max_lines
        ObjectNode dataNode2 = objectMapper.createObjectNode();
        dataNode2.put("max_lines", 0);
        dataNode2.put("used_lines", 0);
        dataNode2.put("left_lines", 0);
        
        ObjectNode resultNode2 = objectMapper.createObjectNode();
        resultNode2.set("data", dataNode2);
        
        SerpstatApiResponse response2 = new SerpstatApiResponse(resultNode2, "getStats", arguments);
        String result2 = CreditsResponseFormatter.format(response2, arguments, objectMapper);
        JsonNode resultJson2 = objectMapper.readTree(result2);
        
        JsonNode usage2 = resultJson2.get("analytics").get("usage");
        assertEquals(0.0, usage2.get("usage_percentage").asDouble());
        assertEquals("UNKNOWN", usage2.get("status").asText());
        assertEquals("❓", usage2.get("status_icon").asText());
    }

    @Test
    @DisplayName("Should determine correct usage status levels")
    void testUsageStatusDetermination() throws Exception {
        // Test CRITICAL status (>95% usage)
        testUsageStatus(10000, 9600, "CRITICAL", "🔴");
        
        // Test HIGH status (>90% usage)
        testUsageStatus(10000, 9200, "HIGH", "🟠");
        
        // Test MODERATE status (>75% usage)
        testUsageStatus(10000, 8000, "MODERATE", "🟡");
        
        // Test NORMAL status (<75% usage)
        testUsageStatus(10000, 5000, "NORMAL", "🟢");
    }

    private void testUsageStatus(long maxLines, long usedLines, String expectedStatus, String expectedIcon) throws Exception {
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("max_lines", maxLines);
        dataNode.put("used_lines", usedLines);
        dataNode.put("left_lines", maxLines - usedLines);
        
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("data", dataNode);
        
        SerpstatApiResponse response = new SerpstatApiResponse(resultNode, "getStats", arguments);
        String result = CreditsResponseFormatter.format(response, arguments, objectMapper);
        JsonNode resultJson = objectMapper.readTree(result);
        
        JsonNode usage = resultJson.get("analytics").get("usage");
        assertEquals(expectedStatus, usage.get("status").asText());
        assertEquals(expectedIcon, usage.get("status_icon").asText());
    }

    @Test
    @DisplayName("Should generate accurate recommendations")
    void testRecommendationsGeneration() throws Exception {
        // Arrange - 1000 credits remaining
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("max_lines", 10000);
        dataNode.put("used_lines", 9000);
        dataNode.put("left_lines", 1000);
        
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("data", dataNode);
        
        SerpstatApiResponse response = new SerpstatApiResponse(resultNode, "getStats", arguments);

        // Act
        String result = CreditsResponseFormatter.format(response, arguments, objectMapper);

        // Assert
        JsonNode resultJson = objectMapper.readTree(result);
        JsonNode recommendations = resultJson.get("analytics").get("recommendations");
        
        // Domain analyses: 1000 / 5 = 200
        assertEquals(200, recommendations.get("estimated_domain_analyses").asLong());
        
        // Keyword researches: 1000 / 1 = 1000
        assertEquals(1000, recommendations.get("estimated_keyword_researches").asLong());
        
        // Tips should be MEDIUM priority for 1000 credits
        JsonNode tips = recommendations.get("tips");
        assertEquals("MEDIUM", tips.get("priority").asText());
        assertTrue(tips.get("message").asText().contains("Credits running low"));
    }

    @Test
    @DisplayName("Should handle zero remaining credits scenario")
    void testZeroRemainingCredits() throws Exception {
        // Arrange
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("max_lines", 10000);
        dataNode.put("used_lines", 10000);
        dataNode.put("left_lines", 0);
        
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("data", dataNode);
        
        SerpstatApiResponse response = new SerpstatApiResponse(resultNode, "getStats", arguments);

        // Act
        String result = CreditsResponseFormatter.format(response, arguments, objectMapper);

        // Assert
        JsonNode resultJson = objectMapper.readTree(result);
        JsonNode recommendations = resultJson.get("analytics").get("recommendations");
        
        assertEquals(0, recommendations.get("estimated_domain_analyses").asLong());
        assertEquals(0, recommendations.get("estimated_keyword_researches").asLong());
        
        JsonNode tips = recommendations.get("tips");
        assertEquals("CRITICAL", tips.get("priority").asText());
        assertTrue(tips.get("message").asText().contains("No credits remaining"));
    }

    @Test
    @DisplayName("Should handle different credit levels for recommendations")
    void testRecommendationPriorities() throws Exception {
        // Test HIGH priority (<100 credits)
        testRecommendationPriority(50, "HIGH", "Very low credits remaining");
        
        // Test MEDIUM priority (<1000 credits)
        testRecommendationPriority(500, "MEDIUM", "Credits running low");
        
        // Test LOW priority (>=1000 credits)
        testRecommendationPriority(2000, "LOW", "Sufficient credits available");
    }

    private void testRecommendationPriority(long leftLines, String expectedPriority, String expectedMessagePart) throws Exception {
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("max_lines", 10000);
        dataNode.put("used_lines", 10000 - leftLines);
        dataNode.put("left_lines", leftLines);
        
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("data", dataNode);
        
        SerpstatApiResponse response = new SerpstatApiResponse(resultNode, "getStats", arguments);
        String result = CreditsResponseFormatter.format(response, arguments, objectMapper);
        JsonNode resultJson = objectMapper.readTree(result);
        
        JsonNode tips = resultJson.get("analytics").get("recommendations").get("tips");
        assertEquals(expectedPriority, tips.get("priority").asText());
        assertTrue(tips.get("message").asText().contains(expectedMessagePart));
    }

    @Test
    @DisplayName("Should include user information when available")
    void testUserInformationInclusion() throws Exception {
        // Arrange with user info
        ObjectNode userInfo = objectMapper.createObjectNode();
        userInfo.put("username", "testuser");
        userInfo.put("email", "test@example.com");
        
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("max_lines", 10000);
        dataNode.put("used_lines", 2500);
        dataNode.put("left_lines", 7500);
        dataNode.set("user_info", userInfo);
        
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("data", dataNode);
        
        SerpstatApiResponse response = new SerpstatApiResponse(resultNode, "getStats", arguments);

        // Act
        String result = CreditsResponseFormatter.format(response, arguments, objectMapper);

        // Assert
        JsonNode resultJson = objectMapper.readTree(result);
        JsonNode userAnalytics = resultJson.get("analytics").get("user");
        assertNotNull(userAnalytics);
        
        JsonNode userDetails = userAnalytics.get("user_details");
        assertNotNull(userDetails);
        assertEquals("testuser", userDetails.get("username").asText());
        assertEquals("test@example.com", userDetails.get("email").asText());
    }

    @Test
    @DisplayName("Should handle missing user information gracefully")
    void testMissingUserInformation() throws Exception {
        // Arrange without user info
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("max_lines", 10000);
        dataNode.put("used_lines", 2500);
        dataNode.put("left_lines", 7500);
        // No user_info added
        
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("data", dataNode);
        
        SerpstatApiResponse response = new SerpstatApiResponse(resultNode, "getStats", arguments);

        // Act
        String result = CreditsResponseFormatter.format(response, arguments, objectMapper);

        // Assert
        JsonNode resultJson = objectMapper.readTree(result);
        JsonNode analytics = resultJson.get("analytics");
        assertNotNull(analytics);
        
        // User section should not exist when no user_info is provided
        assertNull(analytics.get("user"));
    }

    @Test
    @DisplayName("Should format timestamp correctly")
    void testTimestampFormatting() throws Exception {
        // Arrange
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("max_lines", 10000);
        dataNode.put("used_lines", 2500);
        dataNode.put("left_lines", 7500);
        
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("data", dataNode);
        
        SerpstatApiResponse response = new SerpstatApiResponse(resultNode, "getStats", arguments);

        // Act
        LocalDateTime beforeCall = LocalDateTime.now();
        String result = CreditsResponseFormatter.format(response, arguments, objectMapper);
        LocalDateTime afterCall = LocalDateTime.now();

        // Assert
        JsonNode resultJson = objectMapper.readTree(result);
        String timestampStr = resultJson.get("timestamp").asText();
        
        // Parse the timestamp to verify it's in correct format
        LocalDateTime parsedTimestamp = LocalDateTime.parse(timestampStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        // Verify timestamp is within reasonable range (should be between before and after the call)
        assertTrue(parsedTimestamp.isAfter(beforeCall.minusSeconds(1)));
        assertTrue(parsedTimestamp.isBefore(afterCall.plusSeconds(1)));
    }

    @Test
    @DisplayName("Should maintain JSON structure consistency")
    void testJsonStructureConsistency() throws Exception {
        // Arrange
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("max_lines", 10000);
        dataNode.put("used_lines", 2500);
        dataNode.put("left_lines", 7500);
        
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("data", dataNode);
        
        SerpstatApiResponse response = new SerpstatApiResponse(resultNode, "getStats", arguments);

        // Act
        String result = CreditsResponseFormatter.format(response, arguments, objectMapper);

        // Assert
        JsonNode resultJson = objectMapper.readTree(result);
        
        // Verify root structure
        assertTrue(resultJson.has("status"));
        assertTrue(resultJson.has("method"));
        assertTrue(resultJson.has("timestamp"));
        assertTrue(resultJson.has("api_stats"));
        assertTrue(resultJson.has("analytics"));
        
        // Verify analytics structure
        JsonNode analytics = resultJson.get("analytics");
        assertTrue(analytics.has("usage"));
        assertTrue(analytics.has("recommendations"));
        
        // Verify usage structure
        JsonNode usage = analytics.get("usage");
        assertTrue(usage.has("max_credits"));
        assertTrue(usage.has("used_credits"));
        assertTrue(usage.has("remaining_credits"));
        assertTrue(usage.has("usage_percentage"));
        assertTrue(usage.has("status"));
        assertTrue(usage.has("status_icon"));
        
        // Verify recommendations structure
        JsonNode recommendations = analytics.get("recommendations");
        assertTrue(recommendations.has("estimated_domain_analyses"));
        assertTrue(recommendations.has("estimated_keyword_researches"));
        assertTrue(recommendations.has("tips"));
        
        JsonNode tips = recommendations.get("tips");
        assertTrue(tips.has("priority"));
        assertTrue(tips.has("message"));
    }

    @Test
    @DisplayName("Should handle edge case with exact percentage thresholds")
    void testExactPercentageThresholds() throws Exception {
        // Test exactly 95% usage (should be CRITICAL)
        testUsageStatus(10000, 9500, "CRITICAL", "🔴");
        
        // Test exactly 90% usage (should be HIGH)
        testUsageStatus(10000, 9000, "HIGH", "🟠");
        
        // Test exactly 75% usage (should be MODERATE)
        testUsageStatus(10000, 7500, "MODERATE", "🟡");
    }

    @Test
    @DisplayName("Should round usage percentage correctly")
    void testUsagePercentageRounding() throws Exception {
        // Arrange - 3333 used out of 10000 = 33.33%, should round to 33.33
        ObjectNode dataNode = objectMapper.createObjectNode();
        dataNode.put("max_lines", 10000);
        dataNode.put("used_lines", 3333);
        dataNode.put("left_lines", 6667);
        
        ObjectNode resultNode = objectMapper.createObjectNode();
        resultNode.set("data", dataNode);
        
        SerpstatApiResponse response = new SerpstatApiResponse(resultNode, "getStats", arguments);

        // Act
        String result = CreditsResponseFormatter.format(response, arguments, objectMapper);

        // Assert
        JsonNode resultJson = objectMapper.readTree(result);
        JsonNode usage = resultJson.get("analytics").get("usage");
        
        double percentage = usage.get("usage_percentage").asDouble();
        assertEquals(33.33, percentage, 0.01); // Allow small tolerance for floating point
    }
}
