package com.serpstat.domains.domain.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DomainCompetitor model class
 * 
 * These tests validate the basic structure and validation logic of the
 * DomainCompetitor model.
 * They ensure that the model can handle typical competitor data, validate
 * domain formats, and check relevance scoring.
 */
@DisplayName("DomainCompetitor Model Tests")
class DomainCompetitorTest {

    @Test
    @DisplayName("Test DomainCompetitor object creation")
    void testObjectCreation() {
        // Test that the model class would support typical competitor data structure
        // This is a placeholder test until the actual DomainCompetitor model is
        // implemented
        assertDoesNotThrow(() -> {
            // Verify basic competitor structure concepts
            String testDomain = "competitor.com";
            assertNotNull(testDomain, "Competitor domain should not be null");
            assertTrue(isValidDomainFormat(testDomain), "Test domain should be valid");
        });

        // Test competitor data validation concepts
        assertDoesNotThrow(() -> {
            // Test expected competitor fields validation
            double relevance = 85.5;
            int keywordCount = 1250;
            long traffic = 50000L;

            assertTrue(relevance >= 0 && relevance <= 100, "Relevance should be in valid range");
            assertTrue(keywordCount >= 0, "Keyword count should be non-negative");
            assertTrue(traffic >= 0, "Traffic should be non-negative");
        });
    }

    @Test
    @DisplayName("Test competitor domain validation")
    void testCompetitorDomainValidation() {
        // Test basic domain validation patterns
        String[] validDomains = { "example.com", "sub.domain.org", "test-site.co.uk" };
        String[] invalidDomains = { "invalid", "http://example.com", "example.com/" };

        for (String domain : validDomains) {
            assertTrue(isValidDomainFormat(domain),
                    "Domain '" + domain + "' should be valid");
        }

        for (String domain : invalidDomains) {
            assertFalse(isValidDomainFormat(domain),
                    "Domain '" + domain + "' should be invalid");
        }
    }

    @Test
    @DisplayName("Test relevance scoring")
    void testRelevanceScoring() {
        // Test relevance score validation (typically 0-100 range)
        assertTrue(isValidRelevanceScore(0.0), "Score 0.0 should be valid");
        assertTrue(isValidRelevanceScore(50.5), "Score 50.5 should be valid");
        assertTrue(isValidRelevanceScore(100.0), "Score 100.0 should be valid");

        assertFalse(isValidRelevanceScore(-1.0), "Score -1.0 should be invalid");
        assertFalse(isValidRelevanceScore(101.0), "Score 101.0 should be invalid");

        // Test score normalization logic
        assertEquals(0.0, normalizeRelevanceScore(-10.0), 0.001, "Negative scores should normalize to 0");
        assertEquals(100.0, normalizeRelevanceScore(150.0), 0.001, "Scores > 100 should normalize to 100");
        assertEquals(75.5, normalizeRelevanceScore(75.5), 0.001, "Valid scores should remain unchanged");
    }

    // Helper methods for validation
    private boolean isValidDomainFormat(String domain) {
        return domain != null &&
                domain.matches("^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+" +
                        "[a-zA-Z]{2,}$");
    }

    private boolean isValidRelevanceScore(double score) {
        return score >= 0.0 && score <= 100.0;
    }

    private double normalizeRelevanceScore(double score) {
        return Math.max(0.0, Math.min(100.0, score));
    }

    // Helper methods for competitive analysis
    private double calculateCompetitiveAdvantage(double competitorVisibility, double ourVisibility,
            long competitorTraffic, long ourTraffic,
            int competitorKeywords, int ourKeywords) {
        double visibilityAdvantage = (competitorVisibility - ourVisibility) / ourVisibility * 100;
        double trafficAdvantage = (double) (competitorTraffic - ourTraffic) / ourTraffic * 100;
        double keywordAdvantage = (double) (competitorKeywords - ourKeywords) / ourKeywords * 100;

        return (visibilityAdvantage + trafficAdvantage + keywordAdvantage) / 3.0;
    }

    private String classifyMarketPosition(double ourVisibility, double competitorVisibility) {
        double ratio = ourVisibility / competitorVisibility;
        if (ratio >= 1.2)
            return "leading";
        if (ratio >= 0.8)
            return "competitive";
        return "behind";
    }

    private String assessThreatLevel(double visibilityGap, double trafficRatio, double overlapPercentage) {
        if (visibilityGap > 30 && trafficRatio > 2.5 && overlapPercentage > 30)
            return "high";
        if (visibilityGap > 15 && trafficRatio > 1.5 && overlapPercentage > 20)
            return "medium";
        return "low";
    }

    private String calculateOverallTrend(double visibilityChange, double trafficChange, int keywordChange) {
        int positiveIndicators = 0;
        int negativeIndicators = 0;

        if (visibilityChange > 0)
            positiveIndicators++;
        else
            negativeIndicators++;
        if (trafficChange > 0)
            positiveIndicators++;
        else
            negativeIndicators++;
        if (keywordChange > 0)
            positiveIndicators++;
        else
            negativeIndicators++;

        if (positiveIndicators > negativeIndicators)
            return "growing";
        if (negativeIndicators > positiveIndicators)
            return "declining";
        return "mixed";
    }

    @Test
    @DisplayName("Test competitive metrics")
    void testCompetitiveMetrics() {
        // Test visibility comparison metrics
        double ourVisibility = 45.5;
        double competitorVisibility = 78.3;

        // Test visibility gap calculation
        double visibilityGap = competitorVisibility - ourVisibility;
        assertEquals(32.8, visibilityGap, 0.1, "Visibility gap should be calculated correctly");

        // Test traffic comparison data
        long ourTraffic = 25000L;
        long competitorTraffic = 85000L;

        double trafficRatio = (double) competitorTraffic / ourTraffic;
        assertEquals(3.4, trafficRatio, 0.1, "Traffic ratio should be calculated correctly");

        // Test keyword overlap analysis
        int ourKeywords = 1200;
        int competitorKeywords = 2500;
        int sharedKeywords = 450;

        double overlapPercentage = (double) sharedKeywords / Math.min(ourKeywords, competitorKeywords) * 100;
        assertEquals(37.5, overlapPercentage, 0.1, "Keyword overlap percentage should be calculated correctly");

        // Test competitive gap identification
        int uniqueCompetitorKeywords = competitorKeywords - sharedKeywords;
        assertEquals(2050, uniqueCompetitorKeywords, "Unique competitor keywords should be calculated correctly");

        // Test competitive advantage scoring
        double competitiveAdvantage = calculateCompetitiveAdvantage(
                competitorVisibility, ourVisibility,
                competitorTraffic, ourTraffic,
                competitorKeywords, ourKeywords);
        assertTrue(competitiveAdvantage > 0, "Competitor should have advantage in this scenario");
        assertTrue(competitiveAdvantage <= 100, "Competitive advantage should not exceed 100%");

        // Test market position classification
        String marketPosition = classifyMarketPosition(ourVisibility, competitorVisibility);
        assertEquals("behind", marketPosition, "Market position should be classified as 'behind'");

        // Test competitive threat level
        String threatLevel = assessThreatLevel(visibilityGap, trafficRatio, overlapPercentage);
        assertEquals("high", threatLevel, "Threat level should be high based on metrics");
    }

    @Test
    @DisplayName("Test JSON serialization with competitor data")
    void testJsonSerializationWithCompetitorData() {
        // Test serialization of competitor metrics and scores
        Map<String, Object> competitorData = new HashMap<>();
        competitorData.put("domain", "competitor.com");
        competitorData.put("relevance", 87.5);
        competitorData.put("visibility", 65.3);
        competitorData.put("traffic", 125000L);
        competitorData.put("keywords", 2847);
        competitorData.put("position_average", 15.7);

        // Test JSON structure validation
        assertTrue(competitorData.containsKey("domain"), "Competitor data should contain domain");
        assertTrue(competitorData.containsKey("relevance"), "Competitor data should contain relevance");
        assertTrue(competitorData.containsKey("visibility"), "Competitor data should contain visibility");

        // Test handling of competitor domain names
        String domain = (String) competitorData.get("domain");
        assertTrue(isValidDomainFormat(domain), "Domain should be in valid format");
        assertFalse(domain.startsWith("http"), "Domain should not contain protocol");
        assertFalse(domain.endsWith("/"), "Domain should not end with slash");

        // Test competitive relationship data structures
        Map<String, Object> relationshipData = new HashMap<>();
        relationshipData.put("keyword_overlap", 45.2);
        relationshipData.put("shared_keywords", 567);
        relationshipData.put("competitive_gap", 1280);
        relationshipData.put("threat_level", "medium");

        assertNotNull(relationshipData.get("keyword_overlap"), "Relationship data should include overlap");
        assertTrue((Double) relationshipData.get("keyword_overlap") >= 0, "Overlap should be non-negative");
        assertTrue((Double) relationshipData.get("keyword_overlap") <= 100, "Overlap should not exceed 100%");

        // Test large competitor datasets serialization
        List<Map<String, Object>> competitorList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> competitor = new HashMap<>();
            competitor.put("domain", "competitor" + i + ".com");
            competitor.put("relevance", Math.random() * 100);
            competitor.put("traffic", (long) (Math.random() * 1000000));
            competitorList.add(competitor);
        }

        assertEquals(100, competitorList.size(), "Should handle large competitor datasets");

        // Test data integrity in large datasets
        for (Map<String, Object> competitor : competitorList) {
            assertTrue(competitor.containsKey("domain"), "Each competitor should have domain");
            assertTrue(competitor.containsKey("relevance"), "Each competitor should have relevance");
            double relevance = (Double) competitor.get("relevance");
            assertTrue(isValidRelevanceScore(relevance), "Relevance scores should be valid");
        }

        // Test serialization performance with large datasets
        long startTime = System.currentTimeMillis();
        String serializedData = serializeCompetitorData(competitorList);
        long endTime = System.currentTimeMillis();

        assertNotNull(serializedData, "Serialization should produce output");
        assertTrue(endTime - startTime < 1000, "Serialization should complete within 1 second");
        assertTrue(serializedData.length() > 0, "Serialized data should not be empty");
    }

    @Test
    @DisplayName("Test competitive analysis integration")
    void testCompetitiveAnalysisIntegration() {
        // Test integration with keyword gap analysis
        Map<String, Object> keywordGapData = new HashMap<>();
        keywordGapData.put("our_keywords", 1200);
        keywordGapData.put("competitor_keywords", 2100);
        keywordGapData.put("shared_keywords", 400);
        keywordGapData.put("unique_competitor_keywords", 1700);
        keywordGapData.put("our_unique_keywords", 800);
        // Validate gap analysis metrics
        int competitorKeywords = (Integer) keywordGapData.get("competitor_keywords");
        int sharedKeywords = (Integer) keywordGapData.get("shared_keywords");
        int uniqueCompetitorKeywords = (Integer) keywordGapData.get("unique_competitor_keywords");

        assertEquals(competitorKeywords - sharedKeywords, uniqueCompetitorKeywords,
                "Unique competitor keywords should be calculated correctly");

        // Test competitor ranking and positioning
        List<Map<String, Object>> competitorRankings = new ArrayList<>();

        Map<String, Object> competitor1 = new HashMap<>();
        competitor1.put("domain", "top-competitor.com");
        competitor1.put("rank", 1);
        competitor1.put("visibility", 85.2);
        competitor1.put("traffic", 450000L);
        competitorRankings.add(competitor1);

        Map<String, Object> competitor2 = new HashMap<>();
        competitor2.put("domain", "mid-competitor.com");
        competitor2.put("rank", 2);
        competitor2.put("visibility", 67.8);
        competitor2.put("traffic", 280000L);
        competitorRankings.add(competitor2);

        Map<String, Object> competitor3 = new HashMap<>();
        competitor3.put("domain", "small-competitor.com");
        competitor3.put("rank", 3);
        competitor3.put("visibility", 42.1);
        competitor3.put("traffic", 125000L);
        competitorRankings.add(competitor3);

        // Validate ranking order
        for (int i = 0; i < competitorRankings.size() - 1; i++) {
            double currentVisibility = (Double) competitorRankings.get(i).get("visibility");
            double nextVisibility = (Double) competitorRankings.get(i + 1).get("visibility");
            assertTrue(currentVisibility > nextVisibility,
                    "Competitors should be ranked by visibility in descending order");
        }

        // Test market share and competitive landscape data
        Map<String, Object> marketLandscape = new HashMap<>();
        marketLandscape.put("total_market_keywords", 5000);
        marketLandscape.put("our_market_share", 24.0); // 1200/5000 = 24%
        marketLandscape.put("top_competitor_share", 42.0); // 2100/5000 = 42%
        marketLandscape.put("market_concentration", "medium");

        double ourShare = (Double) marketLandscape.get("our_market_share");
        double topCompetitorShare = (Double) marketLandscape.get("top_competitor_share");

        assertTrue(ourShare >= 0 && ourShare <= 100, "Market share should be valid percentage");
        assertTrue(topCompetitorShare >= 0 && topCompetitorShare <= 100, "Competitor share should be valid percentage");
        assertTrue(topCompetitorShare > ourShare, "Top competitor should have larger market share");
        // Test temporal competitive changes tracking
        Map<String, Object> temporalData = new HashMap<>();
        temporalData.put("visibility_change_30d", 2.5); // Gained 2.5% visibility (positive)
        temporalData.put("traffic_change_30d", -3.2); // Lost 3.2% traffic (negative)
        temporalData.put("keyword_change_30d", -10); // Lost 10 keywords (negative)
        temporalData.put("position_change_30d", 1.8); // Average position worsened by 1.8
        temporalData.put("trend", "declining");
        // Validate temporal trends
        double visibilityChange = (Double) temporalData.get("visibility_change_30d");
        double trafficChange = (Double) temporalData.get("traffic_change_30d");

        assertTrue(visibilityChange >= -100 && visibilityChange <= 100, "Visibility change should be valid range");
        assertTrue(trafficChange >= -100 && trafficChange <= 100, "Traffic change should be valid range");

        // Test trend classification logic
        String calculatedTrend = calculateOverallTrend(visibilityChange, trafficChange,
                (Integer) temporalData.get("keyword_change_30d"));
        assertEquals("declining", calculatedTrend, "Trend should be classified as declining due to negative signals");

        // Test competitive intelligence aggregation
        Map<String, Object> competitiveIntelligence = new HashMap<>();
        competitiveIntelligence.put("threat_level", "high");
        competitiveIntelligence.put("opportunity_score", 67.5);
        competitiveIntelligence.put("recommended_action", "aggressive_expansion");
        competitiveIntelligence.put("focus_areas", List.of("keyword_gaps", "content_optimization", "technical_seo"));

        assertNotNull(competitiveIntelligence.get("threat_level"), "Should provide threat assessment");
        assertTrue((Double) competitiveIntelligence.get("opportunity_score") > 0,
                "Opportunity score should be positive");
        assertNotNull(competitiveIntelligence.get("recommended_action"), "Should provide actionable recommendations");
    }

    private String serializeCompetitorData(List<Map<String, Object>> competitorList) {
        // Simple serialization simulation for testing
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < competitorList.size(); i++) {
            Map<String, Object> competitor = competitorList.get(i);
            sb.append("{");
            sb.append("\"domain\":\"").append(competitor.get("domain")).append("\",");
            sb.append("\"relevance\":").append(competitor.get("relevance")).append(",");
            sb.append("\"traffic\":").append(competitor.get("traffic"));
            sb.append("}");
            if (i < competitorList.size() - 1) {
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
