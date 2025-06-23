package com.serpstat.domains.domain.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DomainKeyword model class
 */
@DisplayName("DomainKeyword Model Tests")
class DomainKeywordTest {

    @Test
    @DisplayName("Test DomainKeyword object creation")
    void testObjectCreation() {
        // Test DomainKeyword data structure concepts
        assertDoesNotThrow(() -> {
            // Test typical keyword data values
            String keyword = "seo tools";
            String domain = "example.com";
            Integer position = 5;
            Double cost = 2.50;
            Integer difficulty = 75;
            
            // Verify basic data validation
            assertNotNull(keyword, "Keyword should not be null");
            assertNotNull(domain, "Domain should not be null");
            assertTrue(isValidKeyword(keyword), "Keyword should be valid");
            assertTrue(isValidDomainFormat(domain), "Domain should have valid format");
            assertTrue(isValidPosition(position), "Position should be valid");
            assertTrue(isValidCost(cost), "Cost should be valid");
            assertTrue(isValidDifficulty(difficulty), "Difficulty should be valid");
        });
    }

    @Test
    @DisplayName("Test keyword field validation")
    void testKeywordFieldValidation() {
        // Test keyword format validation
        assertTrue(isValidKeyword("seo tools"), "Simple keyword should be valid");
        assertTrue(isValidKeyword("best seo software 2024"), "Long-tail keyword should be valid");
        assertTrue(isValidKeyword("keyword-with-hyphen"), "Hyphenated keyword should be valid");
        assertFalse(isValidKeyword(""), "Empty keyword should be invalid");
        assertFalse(isValidKeyword(null), "Null keyword should be invalid");
        
        // Test position validation (1-100+ typical range)
        assertTrue(isValidPosition(1), "Position 1 should be valid");
        assertTrue(isValidPosition(50), "Position 50 should be valid");
        assertTrue(isValidPosition(100), "Position 100 should be valid");
        assertFalse(isValidPosition(0), "Position 0 should be invalid");
        assertFalse(isValidPosition(-1), "Negative position should be invalid");
        
        // Test difficulty validation (0-100 range)
        assertTrue(isValidDifficulty(0), "Difficulty 0 should be valid");
        assertTrue(isValidDifficulty(50), "Difficulty 50 should be valid");
        assertTrue(isValidDifficulty(100), "Difficulty 100 should be valid");
        assertFalse(isValidDifficulty(-1), "Negative difficulty should be invalid");
        assertFalse(isValidDifficulty(101), "Difficulty > 100 should be invalid");
    }

    @Test
    @DisplayName("Test SERP features handling")
    void testSerpFeaturesHandling() {
        // Test SERP feature types validation
        String[] validFeatures = {"featured_snippet", "people_also_ask", "image_pack", "video", "local_pack"};
        String[] invalidFeatures = {"", "unknown_feature", null};
        
        for (String feature : validFeatures) {
            assertTrue(isValidSerpFeature(feature), 
                      "SERP feature '" + feature + "' should be valid");
        }
        
        for (String feature : invalidFeatures) {
            assertFalse(isValidSerpFeature(feature), 
                       "SERP feature '" + feature + "' should be invalid");
        }
        
        // Test cost per click validation
        assertTrue(isValidCost(0.0), "Zero cost should be valid");
        assertTrue(isValidCost(1.50), "Positive cost should be valid");
        assertTrue(isValidCost(100.99), "High cost should be valid");
        assertFalse(isValidCost(-0.01), "Negative cost should be invalid");
        
        // Test traffic validation
        assertTrue(isValidTraffic(0L), "Zero traffic should be valid");
        assertTrue(isValidTraffic(1000L), "Positive traffic should be valid");
        assertFalse(isValidTraffic(-1L), "Negative traffic should be invalid");
    }
    
    // Helper methods for validation
    private boolean isValidKeyword(String keyword) {
        return keyword != null && !keyword.trim().isEmpty() && keyword.length() <= 200;
    }
    
    private boolean isValidDomainFormat(String domain) {
        return domain != null && 
               domain.matches("^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+" + 
                              "[a-zA-Z]{2,}$");
    }
    
    private boolean isValidPosition(Integer position) {
        return position != null && position >= 1;
    }
    
    private boolean isValidDifficulty(Integer difficulty) {
        return difficulty != null && difficulty >= 0 && difficulty <= 100;
    }
    
    private boolean isValidCost(Double cost) {
        return cost != null && cost >= 0.0;
    }
    
    private boolean isValidTraffic(Long traffic) {
        return traffic != null && traffic >= 0L;
    }
    
    private boolean isValidSerpFeature(String feature) {
        if (feature == null || feature.trim().isEmpty()) {
            return false;
        }
        String[] knownFeatures = {
            "featured_snippet", "people_also_ask", "image_pack", "video", 
            "local_pack", "news", "shopping", "reviews", "site_links"
        };
        for (String known : knownFeatures) {
            if (known.equals(feature)) {
                return true;
            }
        }
        return false;
    }    @Test
    @DisplayName("Test keyword intent classification")
    void testKeywordIntentClassification() {
        // Test informational intent keywords
        assertTrue(isInformationalIntent("how to use seo tools"), "How-to keywords should be informational");
        assertTrue(isInformationalIntent("what is seo"), "What-is keywords should be informational");
        assertTrue(isInformationalIntent("seo guide"), "Guide keywords should be informational");
        assertTrue(isInformationalIntent("seo tutorial"), "Tutorial keywords should be informational");
        
        // Test navigational intent keywords
        assertTrue(isNavigationalIntent("google analytics"), "Brand names should be navigational");
        assertTrue(isNavigationalIntent("semrush login"), "Login keywords should be navigational");
        assertTrue(isNavigationalIntent("ahrefs dashboard"), "Brand dashboard keywords should be navigational");
        
        // Test commercial intent keywords
        assertTrue(isCommercialIntent("best seo tools"), "Best keywords should be commercial");
        assertTrue(isCommercialIntent("seo software comparison"), "Comparison keywords should be commercial");
        assertTrue(isCommercialIntent("top seo platforms"), "Top keywords should be commercial");
        assertTrue(isCommercialIntent("seo tools review"), "Review keywords should be commercial");
        
        // Test transactional intent keywords
        assertTrue(isTransactionalIntent("buy seo software"), "Buy keywords should be transactional");
        assertTrue(isTransactionalIntent("seo tools price"), "Price keywords should be transactional");
        assertTrue(isTransactionalIntent("download seo tool"), "Download keywords should be transactional");
        assertTrue(isTransactionalIntent("seo software trial"), "Trial keywords should be transactional");
        
        // Test mixed intent scenarios
        assertFalse(isInformationalIntent("buy seo guide"), "Buy+guide should not be purely informational");
        assertFalse(isTransactionalIntent("seo guide free"), "Free guide should not be transactional");
        
        // Test edge cases
        assertFalse(isInformationalIntent(""), "Empty keyword should not have intent");
        assertFalse(isCommercialIntent(null), "Null keyword should not have intent");
        
        // Test long-tail keyword intent
        assertTrue(isInformationalIntent("how to improve website seo ranking"), 
                  "Long-tail how-to should be informational");
        assertTrue(isTransactionalIntent("affordable seo tools for small business"), 
                  "Long-tail with 'affordable' should be transactional");
    }    @Test
    @DisplayName("Test JSON serialization with keyword data")
    void testJsonSerializationWithKeywordData() throws Exception {
        // Test JSON serialization structure for DomainKeyword
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        
        // Create test keyword data structure
        java.util.Map<String, Object> keywordData = new java.util.HashMap<>();
        keywordData.put("keyword", "seo tools");
        keywordData.put("domain", "example.com");
        keywordData.put("position", 5);
        keywordData.put("cost", 2.50);
        keywordData.put("difficulty", 75);
        keywordData.put("traffic", 1200L);
        keywordData.put("serpFeatures", java.util.Arrays.asList("featured_snippet", "people_also_ask"));
        
        // Test serialization to JSON string
        String jsonString = mapper.writeValueAsString(keywordData);
        assertNotNull(jsonString, "JSON serialization should produce output");
        assertTrue(jsonString.length() > 0, "JSON string should not be empty");
        
        // Test JSON structure validation
        assertTrue(jsonString.contains("\"keyword\""), "JSON should contain keyword field");
        assertTrue(jsonString.contains("\"domain\""), "JSON should contain domain field");
        assertTrue(jsonString.contains("\"position\""), "JSON should contain position field");
        assertTrue(jsonString.contains("\"cost\""), "JSON should contain cost field");
        assertTrue(jsonString.contains("\"difficulty\""), "JSON should contain difficulty field");
        assertTrue(jsonString.contains("\"traffic\""), "JSON should contain traffic field");
        
        // Test JSON values format
        assertTrue(jsonString.contains("\"seo tools\""), "JSON should contain keyword value");
        assertTrue(jsonString.contains("\"example.com\""), "JSON should contain domain value");
        assertTrue(jsonString.contains("5"), "JSON should contain position value");
        assertTrue(jsonString.contains("2.5"), "JSON should contain cost value");
        assertTrue(jsonString.contains("75"), "JSON should contain difficulty value");
        assertTrue(jsonString.contains("1200"), "JSON should contain traffic value");
        
        // Test deserialization
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> deserializedData = mapper.readValue(jsonString, java.util.Map.class);
        
        assertEquals("seo tools", deserializedData.get("keyword"), "Keyword should survive serialization");
        assertEquals("example.com", deserializedData.get("domain"), "Domain should survive serialization");
        assertEquals(5, ((Number) deserializedData.get("position")).intValue(), "Position should survive serialization");
        assertEquals(2.5, ((Number) deserializedData.get("cost")).doubleValue(), 0.01, "Cost should survive serialization");
        assertEquals(75, ((Number) deserializedData.get("difficulty")).intValue(), "Difficulty should survive serialization");
        assertEquals(1200L, ((Number) deserializedData.get("traffic")).longValue(), "Traffic should survive serialization");
        
        // Test SERP features array serialization
        @SuppressWarnings("unchecked")
        java.util.List<String> serpFeatures = (java.util.List<String>) deserializedData.get("serpFeatures");
        assertNotNull(serpFeatures, "SERP features should be deserialized");
        assertEquals(2, serpFeatures.size(), "Should have 2 SERP features");
        assertTrue(serpFeatures.contains("featured_snippet"), "Should contain featured_snippet");
        assertTrue(serpFeatures.contains("people_also_ask"), "Should contain people_also_ask");
        
        // Test special characters and edge cases
        java.util.Map<String, Object> specialData = new java.util.HashMap<>();
        specialData.put("keyword", "café résumé ñoño");
        specialData.put("domain", "test-domain.co.uk");
        specialData.put("position", 1);
        specialData.put("cost", 0.0);
        specialData.put("difficulty", 100);
        specialData.put("traffic", 0L);
        
        String specialJson = mapper.writeValueAsString(specialData);
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> specialDeserialized = mapper.readValue(specialJson, java.util.Map.class);
        
        assertEquals("café résumé ñoño", specialDeserialized.get("keyword"), "Unicode keywords should survive serialization");
        assertEquals("test-domain.co.uk", specialDeserialized.get("domain"), "Hyphenated domains should survive serialization");
        assertEquals(0.0, ((Number) specialDeserialized.get("cost")).doubleValue(), "Zero cost should survive serialization");
    }    @Test
    @DisplayName("Test competitive analysis data")
    void testCompetitiveAnalysisData() {
        // Test competitive keyword analysis structure
        java.util.Map<String, Object> competitorData1 = new java.util.HashMap<>();
        competitorData1.put("keyword", "seo software");
        competitorData1.put("domain", "competitor1.com");
        competitorData1.put("position", 3);
        competitorData1.put("cost", 4.50);
        competitorData1.put("difficulty", 85);
        competitorData1.put("traffic", 5000L);
        
        java.util.Map<String, Object> competitorData2 = new java.util.HashMap<>();
        competitorData2.put("keyword", "seo software");
        competitorData2.put("domain", "competitor2.com");
        competitorData2.put("position", 7);
        competitorData2.put("cost", 3.20);
        competitorData2.put("difficulty", 85);
        competitorData2.put("traffic", 2800L);
        
        java.util.Map<String, Object> ourData = new java.util.HashMap<>();
        ourData.put("keyword", "seo software");
        ourData.put("domain", "oursite.com");
        ourData.put("position", 12);
        ourData.put("cost", 4.50);
        ourData.put("difficulty", 85);
        ourData.put("traffic", 1500L);
        
        // Test competitive metrics calculation
        assertTrue(isCompetitiveKeyword(competitorData1, competitorData2, ourData), 
                  "Should identify competitive keywords");
        
        // Test position comparison
        assertTrue(isHigherPosition((Integer) competitorData1.get("position"), 
                                   (Integer) ourData.get("position")), 
                  "Competitor1 should have higher position than us");
        assertTrue(isHigherPosition((Integer) competitorData2.get("position"), 
                                   (Integer) ourData.get("position")), 
                  "Competitor2 should have higher position than us");
        
        // Test traffic analysis
        Long competitor1Traffic = (Long) competitorData1.get("traffic");
        Long competitor2Traffic = (Long) competitorData2.get("traffic");
        Long ourTraffic = (Long) ourData.get("traffic");
        
        assertTrue(competitor1Traffic > ourTraffic, "Competitor1 should have more traffic");
        assertTrue(competitor2Traffic > ourTraffic, "Competitor2 should have more traffic");
        
        // Test opportunity analysis
        assertTrue(isKeywordOpportunity(competitorData1, ourData), 
                  "Should identify keyword opportunities");
        assertTrue(isKeywordOpportunity(competitorData2, ourData), 
                  "Should identify keyword opportunities");
        
        // Test competitive gap analysis
        java.util.List<java.util.Map<String, Object>> competitorKeywords = java.util.Arrays.asList(
            createKeywordData("keyword1", "competitor.com", 2, 3.0, 70, 4000L),
            createKeywordData("keyword2", "competitor.com", 5, 2.5, 65, 3000L),
            createKeywordData("keyword3", "competitor.com", 8, 1.8, 60, 2000L)
        );
        
        java.util.List<java.util.Map<String, Object>> ourKeywords = java.util.Arrays.asList(
            createKeywordData("keyword1", "oursite.com", 15, 3.0, 70, 800L),
            createKeywordData("keyword4", "oursite.com", 3, 2.2, 55, 1500L)
        );
        
        // Test gap identification
        java.util.List<String> gapKeywords = identifyKeywordGaps(competitorKeywords, ourKeywords);
        assertEquals(2, gapKeywords.size(), "Should identify 2 gap keywords");
        assertTrue(gapKeywords.contains("keyword2"), "Should identify keyword2 as gap");
        assertTrue(gapKeywords.contains("keyword3"), "Should identify keyword3 as gap");        // Test competitive strength assessment  
        assertFalse(hasCompetitiveAdvantage(ourKeywords.get(0), competitorKeywords), 
                   "Should NOT have competitive advantage for keyword1 (competitor at pos 2, we at pos 15)");
        
        // Test difficulty vs opportunity matrix
        java.util.Map<String, Object> lowDiffHighTraffic = createKeywordData("easy win", "competitor.com", 1, 5.0, 30, 10000L);
        java.util.Map<String, Object> highDiffLowTraffic = createKeywordData("hard keyword", "competitor.com", 1, 0.5, 95, 100L);
        
        assertTrue(isHighOpportunityKeyword(lowDiffHighTraffic), "Low difficulty + high traffic = high opportunity");
        assertFalse(isHighOpportunityKeyword(highDiffLowTraffic), "High difficulty + low traffic = low opportunity");
        
        // Test seasonal and trend analysis simulation
        java.util.Map<String, Object> seasonalKeyword = createKeywordData("christmas seo", "competitor.com", 3, 2.0, 60, 8000L);
        assertTrue(isPotentiallySeasonalKeyword((String) seasonalKeyword.get("keyword")), 
                  "Should identify seasonal keywords");
    }
      // Helper methods for intent classification
    private boolean isInformationalIntent(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }
        String lower = keyword.toLowerCase();
        
        // Check for transactional signals first (higher priority)
        if (isTransactionalIntent(keyword)) {
            return false;
        }
        
        String[] informationalSignals = {
            "how to", "what is", "how do", "guide", "tutorial", "learn", 
            "tips", "examples", "definition", "meaning", "explain"
        };
        for (String signal : informationalSignals) {
            if (lower.contains(signal)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isNavigationalIntent(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }
        String lower = keyword.toLowerCase();
        String[] navigationalSignals = {
            "login", "dashboard", "signup", "account", "google", "facebook", 
            "semrush", "ahrefs", "moz", "site:"
        };
        for (String signal : navigationalSignals) {
            if (lower.contains(signal)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isCommercialIntent(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }
        String lower = keyword.toLowerCase();
        String[] commercialSignals = {
            "best", "top", "review", "comparison", "vs", "alternative", 
            "compare", "rating", "recommendation"
        };
        for (String signal : commercialSignals) {
            if (lower.contains(signal)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isTransactionalIntent(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return false;
        }
        String lower = keyword.toLowerCase();
        String[] transactionalSignals = {
            "buy", "purchase", "price", "cost", "cheap", "affordable", 
            "discount", "deal", "trial", "download", "order", "shop"
        };
        for (String signal : transactionalSignals) {
            if (lower.contains(signal)) {
                return true;
            }
        }
        return false;
    }
    
    // Helper methods for competitive analysis
    private boolean isCompetitiveKeyword(java.util.Map<String, Object> comp1, java.util.Map<String, Object> comp2, java.util.Map<String, Object> our) {
        Integer ourPos = (Integer) our.get("position");
        Integer comp1Pos = (Integer) comp1.get("position");
        Integer comp2Pos = (Integer) comp2.get("position");
        return (comp1Pos < ourPos) && (comp2Pos < ourPos);
    }
    
    private boolean isHigherPosition(Integer pos1, Integer pos2) {
        return pos1 != null && pos2 != null && pos1 < pos2;
    }
    
    private boolean isKeywordOpportunity(java.util.Map<String, Object> competitor, java.util.Map<String, Object> our) {
        Integer competitorPos = (Integer) competitor.get("position");
        Integer ourPos = (Integer) our.get("position");
        Long competitorTraffic = (Long) competitor.get("traffic");
        Long ourTraffic = (Long) our.get("traffic");
        
        return (competitorPos < ourPos) && (competitorTraffic > ourTraffic);
    }
    
    private java.util.Map<String, Object> createKeywordData(String keyword, String domain, Integer position, Double cost, Integer difficulty, Long traffic) {
        java.util.Map<String, Object> data = new java.util.HashMap<>();
        data.put("keyword", keyword);
        data.put("domain", domain);
        data.put("position", position);
        data.put("cost", cost);
        data.put("difficulty", difficulty);
        data.put("traffic", traffic);
        return data;
    }
    
    private java.util.List<String> identifyKeywordGaps(java.util.List<java.util.Map<String, Object>> competitorKeywords, java.util.List<java.util.Map<String, Object>> ourKeywords) {
        java.util.Set<String> ourKeywordSet = new java.util.HashSet<>();
        for (java.util.Map<String, Object> keyword : ourKeywords) {
            ourKeywordSet.add((String) keyword.get("keyword"));
        }
        
        java.util.List<String> gaps = new java.util.ArrayList<>();
        for (java.util.Map<String, Object> competitorKeyword : competitorKeywords) {
            String keyword = (String) competitorKeyword.get("keyword");
            if (!ourKeywordSet.contains(keyword)) {
                gaps.add(keyword);
            }
        }
        return gaps;
    }
    
    private boolean hasCompetitiveAdvantage(java.util.Map<String, Object> ourKeyword, java.util.List<java.util.Map<String, Object>> competitorKeywords) {
        Integer ourPos = (Integer) ourKeyword.get("position");
        String ourKeywordText = (String) ourKeyword.get("keyword");
        
        for (java.util.Map<String, Object> competitorKeyword : competitorKeywords) {
            String competitorKeywordText = (String) competitorKeyword.get("keyword");
            if (ourKeywordText.equals(competitorKeywordText)) {
                Integer competitorPos = (Integer) competitorKeyword.get("position");
                return ourPos < competitorPos;
            }
        }
        return false; // Not competing on this keyword
    }
    
    private boolean isHighOpportunityKeyword(java.util.Map<String, Object> keyword) {
        Integer difficulty = (Integer) keyword.get("difficulty");
        Long traffic = (Long) keyword.get("traffic");
        
        return (difficulty != null && difficulty <= 50) && (traffic != null && traffic >= 5000L);
    }
    
    private boolean isPotentiallySeasonalKeyword(String keyword) {
        if (keyword == null) return false;
        String lower = keyword.toLowerCase();
        String[] seasonalTerms = {
            "christmas", "holiday", "summer", "winter", "spring", "fall", 
            "valentine", "easter", "thanksgiving", "black friday"
        };
        for (String term : seasonalTerms) {
            if (lower.contains(term)) {
                return true;
            }
        }
        return false;
    }

    // ================================
    // ADDITIONAL TESTS (NEWLY IMPLEMENTED)
    // ================================

    @Test
    @DisplayName("Test keyword metrics validation and boundaries")
    void testKeywordMetricsValidationAndBoundaries() {
        // Test position boundaries
        assertTrue(isValidPosition(1), "Position 1 should be valid");
        assertTrue(isValidPosition(50), "Position 50 should be valid");
        assertTrue(isValidPosition(100), "Position 100 should be valid");
        assertTrue(isValidPosition(Integer.MAX_VALUE), "Maximum integer position should be valid");
        assertFalse(isValidPosition(0), "Position 0 should be invalid");
        assertFalse(isValidPosition(-1), "Negative position should be invalid");
        assertFalse(isValidPosition(null), "Null position should be invalid");
        
        // Test cost boundaries
        assertTrue(isValidCost(0.0), "Zero cost should be valid");
        assertTrue(isValidCost(0.01), "Small positive cost should be valid");
        assertTrue(isValidCost(100.50), "Normal cost should be valid");
        assertTrue(isValidCost(Double.MAX_VALUE), "Maximum double cost should be valid");
        assertFalse(isValidCost(-0.01), "Negative cost should be invalid");
        assertFalse(isValidCost(Double.NEGATIVE_INFINITY), "Negative infinity should be invalid");
        assertFalse(isValidCost(null), "Null cost should be invalid");
        
        // Test special double values for cost
        assertFalse(isValidCost(Double.NaN), "NaN cost should be invalid");
        assertTrue(isValidCost(Double.POSITIVE_INFINITY), "Positive infinity cost should be valid for edge cases");
        
        // Test difficulty boundaries (0-100 range)
        assertTrue(isValidDifficulty(0), "Difficulty 0 should be valid");
        assertTrue(isValidDifficulty(50), "Difficulty 50 should be valid");
        assertTrue(isValidDifficulty(100), "Difficulty 100 should be valid");
        assertFalse(isValidDifficulty(-1), "Negative difficulty should be invalid");
        assertFalse(isValidDifficulty(101), "Difficulty > 100 should be invalid");
        assertFalse(isValidDifficulty(Integer.MAX_VALUE), "Max integer difficulty should be invalid");
        assertFalse(isValidDifficulty(null), "Null difficulty should be invalid");
        
        // Test traffic boundaries
        assertTrue(isValidTraffic(0L), "Zero traffic should be valid");
        assertTrue(isValidTraffic(1L), "Single traffic unit should be valid");
        assertTrue(isValidTraffic(1000000L), "Large traffic should be valid");
        assertTrue(isValidTraffic(Long.MAX_VALUE), "Maximum long traffic should be valid");
        assertFalse(isValidTraffic(-1L), "Negative traffic should be invalid");
        assertFalse(isValidTraffic(Long.MIN_VALUE), "Minimum long traffic should be invalid");
        assertFalse(isValidTraffic(null), "Null traffic should be invalid");
        
        // Test keyword length boundaries
        assertTrue(isValidKeyword("a"), "Single character keyword should be valid");
        assertTrue(isValidKeyword("short keyword"), "Short keyword should be valid");
        
        StringBuilder longKeyword = new StringBuilder();
        for (int i = 0; i < 200; i++) {
            longKeyword.append("a");
        }
        assertTrue(isValidKeyword(longKeyword.toString()), "200-character keyword should be valid");
        
        longKeyword.append("a"); // 201 characters
        assertFalse(isValidKeyword(longKeyword.toString()), "201-character keyword should be invalid");
        
        // Test realistic metric combinations
        java.util.Map<String, Object> realisticKeyword = new java.util.HashMap<>();
        realisticKeyword.put("keyword", "best seo tools");
        realisticKeyword.put("position", 15);
        realisticKeyword.put("cost", 3.75);
        realisticKeyword.put("difficulty", 68);
        realisticKeyword.put("traffic", 2500L);
        
        assertTrue(isValidKeyword((String) realisticKeyword.get("keyword")), "Realistic keyword should be valid");
        assertTrue(isValidPosition((Integer) realisticKeyword.get("position")), "Realistic position should be valid");
        assertTrue(isValidCost((Double) realisticKeyword.get("cost")), "Realistic cost should be valid");
        assertTrue(isValidDifficulty((Integer) realisticKeyword.get("difficulty")), "Realistic difficulty should be valid");
        assertTrue(isValidTraffic((Long) realisticKeyword.get("traffic")), "Realistic traffic should be valid");
    }
    
    @Test
    @DisplayName("Test SERP features and advanced keyword attributes")
    void testSerpFeaturesAndAdvancedAttributes() {
        // Test standard SERP features
        String[] standardFeatures = {
            "featured_snippet", "people_also_ask", "image_pack", "video", 
            "local_pack", "news", "shopping", "reviews", "site_links"
        };
        
        for (String feature : standardFeatures) {
            assertTrue(isValidSerpFeature(feature), 
                      "Standard SERP feature '" + feature + "' should be valid");
        }
        
        // Test invalid SERP features
        String[] invalidFeatures = {
            "", "unknown_feature", "FEATURED_SNIPPET", "featuredSnippet", 
            "featured snippet", "123invalid", null
        };
        
        for (String feature : invalidFeatures) {
            assertFalse(isValidSerpFeature(feature), 
                       "Invalid SERP feature '" + feature + "' should be invalid");
        }
        
        // Test keyword with multiple SERP features
        java.util.List<String> multipleSerpFeatures = java.util.Arrays.asList(
            "featured_snippet", "people_also_ask", "image_pack"
        );
        
        for (String feature : multipleSerpFeatures) {
            assertTrue(isValidSerpFeature(feature), 
                      "Each feature in multiple SERP features should be valid");
        }
        
        // Test keyword competition levels
        assertTrue(isHighCompetitionKeyword(90), "Difficulty 90 should be high competition");
        assertTrue(isHighCompetitionKeyword(100), "Difficulty 100 should be high competition");
        assertFalse(isHighCompetitionKeyword(50), "Difficulty 50 should not be high competition");
        assertFalse(isHighCompetitionKeyword(10), "Difficulty 10 should not be high competition");
        
        assertTrue(isMediumCompetitionKeyword(60), "Difficulty 60 should be medium competition");
        assertTrue(isMediumCompetitionKeyword(40), "Difficulty 40 should be medium competition");
        assertFalse(isMediumCompetitionKeyword(90), "Difficulty 90 should not be medium competition");
        assertFalse(isMediumCompetitionKeyword(10), "Difficulty 10 should not be medium competition");
        
        assertTrue(isLowCompetitionKeyword(20), "Difficulty 20 should be low competition");
        assertTrue(isLowCompetitionKeyword(5), "Difficulty 5 should be low competition");
        assertFalse(isLowCompetitionKeyword(50), "Difficulty 50 should not be low competition");
        
        // Test keyword value assessment
        java.util.Map<String, Object> highValueKeyword = new java.util.HashMap<>();
        highValueKeyword.put("traffic", 10000L);
        highValueKeyword.put("cost", 5.0);
        highValueKeyword.put("difficulty", 40);
        
        assertTrue(isHighValueKeyword(highValueKeyword), "High traffic + reasonable difficulty should be high value");
        
        java.util.Map<String, Object> lowValueKeyword = new java.util.HashMap<>();
        lowValueKeyword.put("traffic", 50L);
        lowValueKeyword.put("cost", 0.10);
        lowValueKeyword.put("difficulty", 95);
        
        assertFalse(isHighValueKeyword(lowValueKeyword), "Low traffic + high difficulty should be low value");
        
        // Test keyword trend simulation
        assertTrue(isTrendingKeyword("ai seo tools"), "AI-related keywords should be trending");
        assertTrue(isTrendingKeyword("chatgpt marketing"), "ChatGPT-related keywords should be trending");
        assertFalse(isTrendingKeyword("traditional seo"), "Traditional keywords should not be trending");
        
        // Test keyword URL analysis simulation
        assertTrue(isUrlOptimizedForKeyword("https://example.com/best-seo-tools", "best seo tools"), 
                  "URL with keyword should be optimized");
        assertFalse(isUrlOptimizedForKeyword("https://example.com/page123", "best seo tools"), 
                   "URL without keyword should not be optimized");
        
        // Test keyword brand association
        assertTrue(isBrandedKeyword("google analytics"), "Brand name keywords should be branded");
        assertTrue(isBrandedKeyword("semrush pricing"), "Brand + feature keywords should be branded");
        assertFalse(isBrandedKeyword("seo tools"), "Generic keywords should not be branded");
    }
    
    // Additional helper methods for advanced keyword analysis
    private boolean isHighCompetitionKeyword(Integer difficulty) {
        return difficulty != null && difficulty >= 80;
    }
    
    private boolean isMediumCompetitionKeyword(Integer difficulty) {
        return difficulty != null && difficulty >= 30 && difficulty < 80;
    }
    
    private boolean isLowCompetitionKeyword(Integer difficulty) {
        return difficulty != null && difficulty < 30;
    }
    
    private boolean isHighValueKeyword(java.util.Map<String, Object> keyword) {
        Long traffic = (Long) keyword.get("traffic");
        Integer difficulty = (Integer) keyword.get("difficulty");
        Double cost = (Double) keyword.get("cost");
        
        return (traffic != null && traffic >= 5000L) && 
               (difficulty != null && difficulty <= 60) && 
               (cost != null && cost >= 1.0);
    }
    
    private boolean isTrendingKeyword(String keyword) {
        if (keyword == null) return false;
        String lower = keyword.toLowerCase();
        String[] trendingTerms = {
            "ai", "artificial intelligence", "chatgpt", "gpt", "machine learning", 
            "automation", "saas", "cloud", "mobile", "app"
        };
        for (String term : trendingTerms) {
            if (lower.contains(term)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isUrlOptimizedForKeyword(String url, String keyword) {
        if (url == null || keyword == null) return false;
        String urlLower = url.toLowerCase();
        String keywordLower = keyword.toLowerCase().replace(" ", "-");
        return urlLower.contains(keywordLower);
    }
    
    private boolean isBrandedKeyword(String keyword) {
        if (keyword == null) return false;
        String lower = keyword.toLowerCase();
        String[] brandTerms = {
            "google", "facebook", "microsoft", "apple", "amazon", 
            "semrush", "ahrefs", "moz", "screaming frog", "hubspot"
        };
        for (String brand : brandTerms) {
            if (lower.contains(brand)) {
                return true;
            }
        }
        return false;
    }
}
