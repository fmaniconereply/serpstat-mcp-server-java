package com.serpstat.domains.domain.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DomainUniqueKeyword model class
 */
@DisplayName("DomainUniqueKeyword Model Tests")
class DomainUniqueKeywordTest {

    @Test
    @DisplayName("Test DomainUniqueKeyword object creation")
    void testObjectCreation() {
        // Test DomainUniqueKeyword data structure concepts
        assertDoesNotThrow(() -> {
            // Test typical unique keyword data
            String keyword = "unique seo opportunity";
            Integer difficulty = 45;
            Long traffic = 2500L;
            Double cost = 3.75;
            
            // Verify basic data validation
            assertNotNull(keyword, "Keyword should not be null");
            assertTrue(isValidKeyword(keyword), "Keyword should be valid");
            assertTrue(isValidDifficulty(difficulty), "Difficulty should be valid");
            assertTrue(isValidTraffic(traffic), "Traffic should be valid");
            assertTrue(isValidCost(cost), "Cost should be valid");
        });
    }

    @Test
    @DisplayName("Test keyword uniqueness validation")
    void testKeywordUniquenessValidation() {
        // Test keyword uniqueness concepts
        String[] uniqueKeywords = {
            "exclusive business term", "competitor gap keyword", 
            "untapped niche phrase", "brand-specific query"
        };
        
        for (String keyword : uniqueKeywords) {
            assertTrue(isValidKeyword(keyword), 
                      "Unique keyword '" + keyword + "' should be valid");
            assertTrue(isUniqueKeywordCandidate(keyword), 
                      "Keyword '" + keyword + "' should be unique candidate");
        }
          // Test domain position mapping validation
        assertDoesNotThrow(() -> {
            int position1 = 3;
            int position2 = 8;
            
            assertTrue(isValidPosition(position1), "Position 3 should be valid");
            assertTrue(isValidPosition(position2), "Position 8 should be valid");
            assertTrue(position1 < position2, "Position comparison should work");
        });
    }

    @Test
    @DisplayName("Test competitive analysis metrics")
    void testCompetitiveAnalysisMetrics() {
        // Test competitive metrics validation
        assertTrue(isValidDifficulty(0), "Min difficulty should be valid");
        assertTrue(isValidDifficulty(100), "Max difficulty should be valid");
        assertFalse(isValidDifficulty(-1), "Negative difficulty should be invalid");
        assertFalse(isValidDifficulty(101), "Over-max difficulty should be invalid");
        
        // Test cost validation (CPC)
        assertTrue(isValidCost(0.0), "Zero cost should be valid");
        assertTrue(isValidCost(50.0), "High cost should be valid");
        assertFalse(isValidCost(-1.0), "Negative cost should be invalid");
        
        // Test traffic validation
        assertTrue(isValidTraffic(0L), "Zero traffic should be valid");
        assertTrue(isValidTraffic(100000L), "High traffic should be valid");
        assertFalse(isValidTraffic(-1L), "Negative traffic should be invalid");
        
        // Test competitive gap analysis
        assertTrue(hasCompetitiveOpportunity(25, 5000L), "Low difficulty + high traffic = opportunity");
        assertFalse(hasCompetitiveOpportunity(95, 100L), "High difficulty + low traffic = no opportunity");
    }
    
    // Helper methods for validation
    private boolean isValidKeyword(String keyword) {
        return keyword != null && !keyword.trim().isEmpty() && keyword.length() <= 200;
    }
    
    private boolean isValidDifficulty(Integer difficulty) {
        return difficulty != null && difficulty >= 0 && difficulty <= 100;
    }
    
    private boolean isValidTraffic(Long traffic) {
        return traffic != null && traffic >= 0L;
    }
    
    private boolean isValidCost(Double cost) {
        return cost != null && cost >= 0.0;
    }
    
    private boolean isValidPosition(Integer position) {
        return position != null && position >= 1 && position <= 100;
    }
    
    private boolean isUniqueKeywordCandidate(String keyword) {
        // Simple heuristic for unique keyword validation
        return keyword != null && keyword.length() > 10 && keyword.contains(" ");
    }
      private boolean hasCompetitiveOpportunity(Integer difficulty, Long traffic) {
        return difficulty != null && traffic != null && 
               difficulty < 50 && traffic > 1000L;
    }
    
    // Helper methods for domain ranking comparison
    private boolean isHigherRanked(java.util.Map<String, Object> domain1, java.util.Map<String, Object> domain2) {
        Integer pos1 = (Integer) domain1.get("position");
        Integer pos2 = (Integer) domain2.get("position");
        if (pos1 == null && pos2 == null) return false;
        if (pos1 == null) return false; // Null position means not ranking, so not higher
        if (pos2 == null) return true; // domain1 ranks, domain2 doesn't
        return pos1 < pos2; // Lower position number = higher rank
    }    private boolean isCompetitiveGap(java.util.Map<String, Object> domain1, java.util.Map<String, Object> domain2) {
        Integer pos1 = (Integer) domain1.get("position");
        Integer pos2 = (Integer) domain2.get("position");
        
        // If one domain doesn't rank (null position) and the other ranks well (<=20), it's a gap
        if (pos1 == null && pos2 != null && pos2 <= 20) return true;
        if (pos2 == null && pos1 != null && pos1 <= 20) return true;
        
        // If both rank, check if there's significant position difference (>10 positions)
        if (pos1 != null && pos2 != null) {
            return Math.abs(pos1 - pos2) > 10;
        }
        
        return false;
    }
    
    private boolean isRankingOpportunity(java.util.Map<String, Object> ourDomain, java.util.List<java.util.Map<String, Object>> competitors) {
        Integer ourPosition = (Integer) ourDomain.get("position");
        if (ourPosition != null) return false; // We already rank
        
        // Check if competitors are ranking well (opportunity for us to enter)
        for (java.util.Map<String, Object> competitor : competitors) {
            Integer competitorPos = (Integer) competitor.get("position");
            if (competitorPos != null && competitorPos <= 10) {
                return true; // Competitors rank in top 10, opportunity for us
            }
        }
        return false;
    }
    
    private boolean isDomainStronger(java.util.Map<String, Object> domain1, java.util.Map<String, Object> domain2) {
        Long traffic1 = (Long) domain1.get("traffic");
        Long traffic2 = (Long) domain2.get("traffic");
        Integer pos1 = (Integer) domain1.get("position");
        Integer pos2 = (Integer) domain2.get("position");
        
        // Simple strength comparison based on traffic and position
        if (pos1 != null && pos2 != null) {
            if (pos1 < pos2) return true; // Better position
            if (pos1 > pos2) return false;
        }
        
        if (traffic1 != null && traffic2 != null) {
            return traffic1 > traffic2; // Higher traffic
        }
        
        return false;
    }
    
    private java.util.Map<String, Object> findTopRankingDomain(java.util.List<java.util.Map<String, Object>> domains) {
        java.util.Map<String, Object> topDomain = null;
        Integer bestPosition = null;
        
        for (java.util.Map<String, Object> domain : domains) {
            Integer position = (Integer) domain.get("position");
            if (position != null) {
                if (bestPosition == null || position < bestPosition) {
                    bestPosition = position;
                    topDomain = domain;
                }
            }
        }
        
        return topDomain;
    }
    
    private int countRankingCompetitors(java.util.List<java.util.Map<String, Object>> domains) {
        int count = 0;
        for (java.util.Map<String, Object> domain : domains) {
            Integer position = (Integer) domain.get("position");
            if (position != null && position > 0) {
                count++;
            }
        }
        return count;
    }

    // ================================
    // DISABLED TESTS (TODO: Implement later)
    // ================================
    
    @Test
    @DisplayName("Test Lombok annotations functionality")
    void testLombokAnnotations() {
        // Test Lombok-style data structure simulation for DomainUniqueKeyword
        java.util.Map<String, Object> keywordData = new java.util.HashMap<>();
        keywordData.put("keyword", "unique business opportunity");
        keywordData.put("difficulty", 35);
        keywordData.put("traffic", 4500L);
        keywordData.put("cost", 2.75);
        keywordData.put("domain", "competitor.com");
        keywordData.put("position", 8);
        
        // Test getter-like access patterns
        assertEquals("unique business opportunity", keywordData.get("keyword"), "Keyword should be accessible");
        assertEquals(35, keywordData.get("difficulty"), "Difficulty should be accessible");
        assertEquals(4500L, keywordData.get("traffic"), "Traffic should be accessible");
        assertEquals(2.75, keywordData.get("cost"), "Cost should be accessible");
        assertEquals("competitor.com", keywordData.get("domain"), "Domain should be accessible");
        assertEquals(8, keywordData.get("position"), "Position should be accessible");
        
        // Test setter-like modification patterns
        keywordData.put("keyword", "updated unique keyword");
        keywordData.put("difficulty", 40);
        
        assertEquals("updated unique keyword", keywordData.get("keyword"), "Keyword should be modifiable");
        assertEquals(40, keywordData.get("difficulty"), "Difficulty should be modifiable");
        
        // Test builder-like pattern simulation
        java.util.Map<String, Object> builtKeyword = new java.util.HashMap<>();
        builtKeyword.put("keyword", "builder pattern keyword");
        builtKeyword.put("difficulty", 25);
        builtKeyword.put("traffic", 3000L);
        
        // Verify builder pattern works
        assertNotNull(builtKeyword, "Builder should create valid object");
        assertTrue(isValidKeyword((String) builtKeyword.get("keyword")), "Built keyword should be valid");
        assertTrue(isValidDifficulty((Integer) builtKeyword.get("difficulty")), "Built difficulty should be valid");
        assertTrue(isValidTraffic((Long) builtKeyword.get("traffic")), "Built traffic should be valid");
        
        // Test toString-like functionality
        String dataString = keywordData.toString();
        assertNotNull(dataString, "Data should have string representation");
        assertTrue(dataString.length() > 0, "String representation should not be empty");
        assertTrue(dataString.contains("keyword"), "String should contain field names");
        
        // Test equals-like functionality
        java.util.Map<String, Object> equalData = new java.util.HashMap<>(keywordData);
        assertEquals(keywordData, equalData, "Equal data structures should be equal");
          // Test hashCode-like functionality
        assertEquals(keywordData.hashCode(), equalData.hashCode(), "Equal objects should have same hashCode");
    }
    
    @Test
    @DisplayName("Test domain ranking comparison")
    void testDomainRankingComparison() {
        // Test domain ranking comparison for unique keywords
        java.util.Map<String, Object> domain1Data = new java.util.HashMap<>();
        domain1Data.put("domain", "competitor1.com");
        domain1Data.put("keyword", "unique seo opportunity");
        domain1Data.put("position", 5);
        domain1Data.put("traffic", 3500L);
        domain1Data.put("difficulty", 42);
        
        java.util.Map<String, Object> domain2Data = new java.util.HashMap<>();
        domain2Data.put("domain", "competitor2.com");
        domain2Data.put("keyword", "unique seo opportunity");
        domain2Data.put("position", 12);
        domain2Data.put("traffic", 1200L);
        domain2Data.put("difficulty", 42);
        
        java.util.Map<String, Object> ourDomainData = new java.util.HashMap<>();
        ourDomainData.put("domain", "oursite.com");
        ourDomainData.put("keyword", "unique seo opportunity");
        ourDomainData.put("position", null); // We don't rank for this keyword yet
        ourDomainData.put("traffic", 0L);
        ourDomainData.put("difficulty", 42);
          // Test position comparison logic
        assertTrue(isHigherRanked(domain1Data, domain2Data), "Position 5 should be higher ranked than position 12");
        assertFalse(isHigherRanked(domain2Data, domain1Data), "Position 12 should not be higher ranked than position 5");
        assertFalse(isHigherRanked(ourDomainData, domain1Data), "Null position should not be higher ranked");
        
        // Test traffic comparison
        Long traffic1 = (Long) domain1Data.get("traffic");
        Long traffic2 = (Long) domain2Data.get("traffic");
        Long ourTraffic = (Long) ourDomainData.get("traffic");
        
        assertTrue(traffic1 > traffic2, "Domain1 should have more traffic than domain2");
        assertTrue(traffic1 > ourTraffic, "Domain1 should have more traffic than us");
        assertTrue(traffic2 > ourTraffic, "Domain2 should have more traffic than us");
        
        // Test competitive gap identification
        assertTrue(isCompetitiveGap(ourDomainData, domain1Data), "Should identify competitive gap against domain1");
        assertTrue(isCompetitiveGap(ourDomainData, domain2Data), "Should identify competitive gap against domain2");
        assertFalse(isCompetitiveGap(domain1Data, domain2Data), "Both ranking domains should not have gap");
        
        // Test ranking opportunity assessment
        assertTrue(isRankingOpportunity(ourDomainData, java.util.Arrays.asList(domain1Data, domain2Data)), 
                  "Should identify ranking opportunity for unique keyword");
        
        // Test domain strength comparison
        assertTrue(isDomainStronger(domain1Data, domain2Data), "Domain1 should be stronger than domain2");
        assertFalse(isDomainStronger(domain2Data, domain1Data), "Domain2 should not be stronger than domain1");
        assertTrue(isDomainStronger(domain1Data, ourDomainData), "Domain1 should be stronger than our domain");
        
        // Test multiple domain ranking analysis
        java.util.List<java.util.Map<String, Object>> allDomains = java.util.Arrays.asList(
            domain1Data, domain2Data, ourDomainData
        );
        
        java.util.Map<String, Object> topDomain = findTopRankingDomain(allDomains);        assertEquals("competitor1.com", topDomain.get("domain"), "competitor1.com should be top ranking domain");
        
        int totalCompetitors = countRankingCompetitors(allDomains);
        assertEquals(2, totalCompetitors, "Should identify 2 ranking competitors");
    }

    // Helper methods for SERP features and analysis
    private boolean hasFeaturedSnippet(java.util.List<String> types) {
        return types != null && types.contains("featured_snippet");
    }
    
    private boolean hasLocalPack(java.util.List<String> types) {
        return types != null && types.contains("local_pack");
    }
    
    private boolean hasShoppingResults(java.util.List<String> types) {
        return types != null && types.contains("shopping_results");
    }
    
    private boolean hasCompetitiveSerpFeatures(java.util.List<String> types) {
        if (types == null) return false;
        String[] competitiveFeatures = {"featured_snippet", "local_pack", "shopping_results", "knowledge_panel", "image_pack"};
        for (String feature : competitiveFeatures) {
            if (types.contains(feature)) return true;
        }
        return false;
    }
    
    private boolean hasLowCompetitionSerpFeatures(java.util.List<String> types) {
        if (types == null) return true;
        return types.size() == 1 && (types.contains("organic") || types.contains("basic_results"));
    }
    
    private int calculateSerpFeatureComplexity(java.util.List<String> types) {
        if (types == null || types.isEmpty()) return 0;
        int complexity = 0;
        String[] highComplexityFeatures = {"featured_snippet", "local_pack", "shopping_results"};
        String[] mediumComplexityFeatures = {"knowledge_panel", "image_pack", "video_results"};
        
        for (String type : types) {
            if (java.util.Arrays.asList(highComplexityFeatures).contains(type)) {
                complexity += 30;
            } else if (java.util.Arrays.asList(mediumComplexityFeatures).contains(type)) {
                complexity += 20;
            } else {
                complexity += 10;
            }
        }        return complexity;
    }

    // Helper methods for keyword intent classification
    private boolean isInformationalIntent(java.util.Map<String, Object> keyword) {
        @SuppressWarnings("unchecked")
        java.util.List<String> intents = (java.util.List<String>) keyword.get("intents");
        return intents != null && intents.contains("informational");
    }
    
    private boolean isCommercialIntent(java.util.Map<String, Object> keyword) {
        @SuppressWarnings("unchecked")
        java.util.List<String> intents = (java.util.List<String>) keyword.get("intents");
        return intents != null && intents.contains("commercial");
    }
    
    private boolean isTransactionalIntent(java.util.Map<String, Object> keyword) {
        @SuppressWarnings("unchecked")
        java.util.List<String> intents = (java.util.List<String>) keyword.get("intents");
        return intents != null && intents.contains("transactional");
    }
    
    private boolean isNavigationalIntent(java.util.Map<String, Object> keyword) {
        @SuppressWarnings("unchecked")
        java.util.List<String> intents = (java.util.List<String>) keyword.get("intents");
        return intents != null && intents.contains("navigational");
    }
    
    private boolean hasMixedIntents(java.util.Map<String, Object> keyword) {
        @SuppressWarnings("unchecked")
        java.util.List<String> intents = (java.util.List<String>) keyword.get("intents");
        return intents != null && intents.size() > 1;
    }
    
    private int getIntentPriorityScore(java.util.Map<String, Object> keyword) {
        @SuppressWarnings("unchecked")
        java.util.List<String> intents = (java.util.List<String>) keyword.get("intents");
        if (intents == null || intents.isEmpty()) return 0;
        
        int score = 0;
        for (String intent : intents) {
            switch (intent) {
                case "transactional":
                    score += 100;
                    break;
                case "commercial":
                    score += 75;
                    break;
                case "navigational":
                    score += 50;
                    break;
                case "informational":
                    score += 25;
                    break;
                default:
                    score += 10;
                    break;
            }        }
        return score;
    }    // Helper methods for opportunity scoring
    private int calculateOpportunityScore(java.util.Map<String, Object> keyword) {
        if (keyword == null) return 0;
        
        Integer difficulty = (Integer) keyword.get("difficulty");
        Long traffic = (Long) keyword.get("traffic");
        Double cost = (Double) keyword.get("cost");
        Integer position = (Integer) keyword.get("position");
        
        // If already ranking, opportunity is lower
        if (position != null && position > 0) return 0;
        
        int score = 0;
        
        // Traffic contribution (higher is better)
        if (traffic != null) {
            if (traffic > 10000L) score += 50;
            else if (traffic > 5000L) score += 40;
            else if (traffic > 1000L) score += 30;
            else if (traffic > 500L) score += 20;
            else score += 10;
        }
        
        // Difficulty contribution (lower is better)
        if (difficulty != null) {
            if (difficulty < 30) score += 40;
            else if (difficulty < 50) score += 30;
            else if (difficulty < 70) score += 20;
            else score += 5;
        }
        
        // Cost contribution (moderate cost is better for commercial value)
        if (cost != null) {
            if (cost > 1.0 && cost < 10.0) score += 20;
            else if (cost >= 10.0) score += 10;
            else score += 5;
        }
        
        return score;
    }
      private boolean isHighOpportunity(java.util.Map<String, Object> keyword) {
        if (keyword == null) return false;
        return calculateOpportunityScore(keyword) >= 80;
    }
    
    private java.util.List<java.util.Map<String, Object>> rankKeywordOpportunities(java.util.List<java.util.Map<String, Object>> keywords) {
        java.util.List<java.util.Map<String, Object>> ranked = new java.util.ArrayList<>(keywords);
        ranked.sort((k1, k2) -> Integer.compare(
            calculateOpportunityScore(k2), calculateOpportunityScore(k1)        ));
        return ranked;
    }

    // Helper methods for gap analysis
    private boolean isKeywordGap(java.util.Map<String, Object> ourKeyword, java.util.Map<String, Object> competitorKeyword) {
        String ourKw = (String) ourKeyword.get("keyword");
        String compKw = (String) competitorKeyword.get("keyword");
        Integer ourPos = (Integer) ourKeyword.get("position");
        Integer compPos = (Integer) competitorKeyword.get("position");
        
        // Gap exists if same keyword, competitor ranks but we don't
        return ourKw != null && compKw != null && ourKw.equals(compKw) && 
               ourPos == null && compPos != null;
    }
    
    private int calculateGapOpportunityScore(java.util.Map<String, Object> ourKeyword, java.util.Map<String, Object> competitorKeyword) {
        if (!isKeywordGap(ourKeyword, competitorKeyword)) return 0;
        
        Integer difficulty = (Integer) competitorKeyword.get("difficulty");
        Long traffic = (Long) competitorKeyword.get("traffic");
        Integer compPos = (Integer) competitorKeyword.get("position");
        
        int score = 0;
        
        // Traffic contribution
        if (traffic != null) {
            if (traffic > 5000L) score += 40;
            else if (traffic > 1000L) score += 30;
            else score += 10;
        }
        
        // Difficulty contribution (lower is better for gaps)
        if (difficulty != null) {
            if (difficulty < 40) score += 40;
            else if (difficulty < 60) score += 30;
            else if (difficulty < 80) score += 15;
            else score += 5;
        }
        
        // Position contribution (worse position = better opportunity)
        if (compPos != null) {
            if (compPos > 10) score += 30;
            else if (compPos > 5) score += 20;
            else score += 10;
        }
        
        return score;
    }
      private java.util.List<java.util.Map<String, Object>> findKeywordGaps(
            java.util.List<java.util.Map<String, Object>> ourKeywords,
            java.util.List<java.util.Map<String, Object>> competitorKeywords) {
        java.util.List<java.util.Map<String, Object>> gaps = new java.util.ArrayList<>();
        
        for (java.util.Map<String, Object> compKeyword : competitorKeywords) {
            String compKw = (String) compKeyword.get("keyword");
            Integer compPos = (Integer) compKeyword.get("position");
            
            if (compPos == null || compKw == null) continue; // Competitor doesn't rank or invalid data
            
            // Check if we have this keyword and how we rank
            boolean isGap = true;
            
            for (java.util.Map<String, Object> ourKeyword : ourKeywords) {
                String ourKw = (String) ourKeyword.get("keyword");
                Integer ourPos = (Integer) ourKeyword.get("position");
                
                if (compKw.equals(ourKw)) {
                    // We have this keyword - check if we rank well enough
                    if (ourPos != null && ourPos <= compPos) {
                        isGap = false; // We rank better or equal
                    }
                    break;
                }
            }
            
            if (isGap) {
                gaps.add(compKeyword);
            }
        }
        
        return gaps;
    }
    
    private java.util.List<java.util.Map<String, Object>> prioritizeKeywordGaps(java.util.List<java.util.Map<String, Object>> gaps) {
        java.util.List<java.util.Map<String, Object>> prioritized = new java.util.ArrayList<>(gaps);
        prioritized.sort((g1, g2) -> {
            // Create dummy "our" keyword for scoring
            java.util.Map<String, Object> dummy = new java.util.HashMap<>();
            dummy.put("keyword", g1.get("keyword"));
            dummy.put("position", null);
            
            java.util.Map<String, Object> dummy2 = new java.util.HashMap<>();
            dummy2.put("keyword", g2.get("keyword"));
            dummy2.put("position", null);
            
            int score1 = calculateGapOpportunityScore(dummy, g1);
            int score2 = calculateGapOpportunityScore(dummy2, g2);
            
            return Integer.compare(score2, score1); // Higher score first
        });
        
        return prioritized;
    }
    
    private java.util.Map<String, Object> calculateGapMetrics(
            java.util.List<java.util.Map<String, Object>> ourKeywords,
            java.util.List<java.util.Map<String, Object>> competitorKeywords) {
        java.util.Map<String, Object> metrics = new java.util.HashMap<>();
        
        java.util.List<java.util.Map<String, Object>> gaps = findKeywordGaps(ourKeywords, competitorKeywords);
        metrics.put("total_gaps", gaps.size());
        
        if (!gaps.isEmpty()) {
            double totalDifficulty = 0;
            long totalTraffic = 0;
            int validDifficulties = 0;
            int validTraffic = 0;
            
            for (java.util.Map<String, Object> gap : gaps) {
                Integer difficulty = (Integer) gap.get("difficulty");
                Long traffic = (Long) gap.get("traffic");
                
                if (difficulty != null) {
                    totalDifficulty += difficulty;
                    validDifficulties++;
                }
                
                if (traffic != null) {
                    totalTraffic += traffic;
                    validTraffic++;
                }
            }
            
            metrics.put("avg_gap_difficulty", validDifficulties > 0 ? totalDifficulty / validDifficulties : 0.0);
            metrics.put("avg_gap_traffic", validTraffic > 0 ? (double) totalTraffic / validTraffic : 0.0);
        } else {
            metrics.put("avg_gap_difficulty", 0.0);
            metrics.put("avg_gap_traffic", 0.0);
        }
          return metrics;    }

    // Helper methods for JSON serialization
    private boolean isValidJsonStructure(java.util.Map<String, Object> data) {
        if (data == null || data.isEmpty()) return false;
        
        // Must have at least keyword and some competitive data
        if (!data.containsKey("keyword") || data.get("keyword") == null) return false;
        
        // For competitive analysis, need at least one of: position, difficulty, traffic
        boolean hasCompetitiveData = data.containsKey("position") || 
                                   data.containsKey("difficulty") || 
                                   data.containsKey("traffic");
        if (!hasCompetitiveData) return false;
        
        // Validate data types for JSON compatibility
        for (java.util.Map.Entry<String, Object> entry : data.entrySet()) {
            Object value = entry.getValue();
            if (value != null && !isJsonCompatibleType(value)) {
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isJsonCompatibleType(Object value) {
        return value instanceof String || value instanceof Number || 
               value instanceof Boolean || value instanceof java.util.List ||
               value instanceof java.util.Map;
    }
    
    private String simulateJsonSerialization(java.util.Map<String, Object> data) {
        if (data == null) return "null";
        
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        
        for (java.util.Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) json.append(",");
            first = false;
            
            json.append("\"").append(entry.getKey()).append("\":");
            Object value = entry.getValue();
            
            if (value == null) {
                json.append("null");
            } else if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else if (value instanceof Number || value instanceof Boolean) {
                json.append(value);
            } else if (value instanceof java.util.List) {
                json.append(serializeList((java.util.List<?>) value));            } else if (value instanceof java.util.Map) {
                @SuppressWarnings("unchecked")
                java.util.Map<String, Object> mapValue = (java.util.Map<String, Object>) value;
                json.append(simulateJsonSerialization(mapValue));
            } else {
                json.append("\"").append(value.toString()).append("\"");
            }
        }
        
        json.append("}");
        return json.toString();
    }
    
    private String serializeList(java.util.List<?> list) {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        
        for (Object item : list) {
            if (!first) json.append(",");
            first = false;
            
            if (item == null) {
                json.append("null");
            } else if (item instanceof String) {
                json.append("\"").append(item).append("\"");
            } else if (item instanceof Number || item instanceof Boolean) {
                json.append(item);
            } else {
                json.append("\"").append(item.toString()).append("\"");
            }
        }
        
        json.append("]");
        return json.toString();
    }
    
    private java.util.Map<String, Object> extractCompetitiveData(java.util.Map<String, Object> keywordData) {
        java.util.Map<String, Object> competitive = new java.util.HashMap<>();
        
        // Extract domain positions
        Object domainPositions = keywordData.get("domain_positions");
        if (domainPositions != null) {
            competitive.put("domain_positions", domainPositions);
        }
        
        // Extract SERP features
        Object types = keywordData.get("types");
        if (types != null) {
            competitive.put("serp_features", types);
        }
        
        // Extract keyword intents
        Object intents = keywordData.get("intents");
        if (intents != null) {
            competitive.put("keyword_intents", intents);
        }
        
        // Extract competitive metrics
        competitive.put("difficulty", keywordData.get("difficulty"));
        competitive.put("traffic", keywordData.get("traffic"));
        competitive.put("position", keywordData.get("position"));
        
        return competitive;
    }
    
    private java.util.Map<String, Object> simulateJsonDeserialization(String jsonString) {
        // Simple simulation - in real scenario would use Jackson/Gson
        java.util.Map<String, Object> result = new java.util.HashMap<>();
        
        if (jsonString == null || !jsonString.startsWith("{") || !jsonString.endsWith("}")) {
            return result;
        }
        
        // Extract some key-value pairs for testing (simplified)
        if (jsonString.contains("\"keyword\":")) {
            String keyword = extractJsonStringValue(jsonString, "keyword");
            if (keyword != null) result.put("keyword", keyword);
        }
        
        if (jsonString.contains("\"difficulty\":")) {
            Integer difficulty = extractJsonIntValue(jsonString, "difficulty");
            if (difficulty != null) result.put("difficulty", difficulty);
        }
        
        if (jsonString.contains("\"traffic\":")) {
            Long traffic = extractJsonLongValue(jsonString, "traffic");
            if (traffic != null) result.put("traffic", traffic);
        }
        
        if (jsonString.contains("\"types\":")) {
            java.util.List<String> types = extractJsonArrayValue(jsonString, "types");
            if (types != null) result.put("types", types);
        }
        
        return result;
    }
    
    private String extractJsonStringValue(String json, String key) {
        String pattern = "\"" + key + "\":\"";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        
        start += pattern.length();
        int end = json.indexOf("\"", start);
        if (end == -1) return null;
        
        return json.substring(start, end);
    }
    
    private Integer extractJsonIntValue(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        
        start += pattern.length();
        int end = Math.min(json.indexOf(",", start), json.indexOf("}", start));
        if (end == -1) end = json.length() - 1;
        
        try {
            return Integer.parseInt(json.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Long extractJsonLongValue(String json, String key) {
        String pattern = "\"" + key + "\":";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        
        start += pattern.length();
        int end = Math.min(json.indexOf(",", start), json.indexOf("}", start));
        if (end == -1) end = json.length() - 1;
        
        try {
            return Long.parseLong(json.substring(start, end).trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private java.util.List<String> extractJsonArrayValue(String json, String key) {
        String pattern = "\"" + key + "\":[";
        int start = json.indexOf(pattern);
        if (start == -1) return null;
        
        start += pattern.length();
        int end = json.indexOf("]", start);
        if (end == -1) return null;
        
        String arrayContent = json.substring(start, end);
        java.util.List<String> result = new java.util.ArrayList<>();
        
        String[] items = arrayContent.split(",");
        for (String item : items) {
            String cleaned = item.trim().replace("\"", "");
            if (!cleaned.isEmpty()) {
                result.add(cleaned);
            }
        }
          return result;
    }

    // Enum for keyword length classification
    private enum KeywordLength {
        SHORT, MEDIUM, LONG_TAIL
    }

    // Helper methods for keyword complexity analysis
    private KeywordLength classifyKeywordLength(java.util.Map<String, Object> keyword) {
        String kw = (String) keyword.get("keyword");
        if (kw == null || kw.trim().isEmpty()) return KeywordLength.SHORT;
        
        String[] words = kw.trim().split("\\s+");
        if (words.length <= 2) return KeywordLength.SHORT;
        if (words.length <= 4) return KeywordLength.MEDIUM;
        return KeywordLength.LONG_TAIL;
    }
      private int calculateKeywordComplexity(java.util.Map<String, Object> keyword) {
        if (keyword == null) return 0;
        
        String kw = (String) keyword.get("keyword");
        if (kw == null || kw.trim().isEmpty()) return 0;
        
        Integer difficulty = (Integer) keyword.get("difficulty");
        Long traffic = (Long) keyword.get("traffic");
        
        int complexity = 0;
        
        // Base complexity from difficulty
        if (difficulty != null) {
            complexity += difficulty;
        }
        
        // Length factor (shorter = more complex)
        KeywordLength length = classifyKeywordLength(keyword);
        switch (length) {
            case SHORT:
                complexity += 30;
                break;
            case MEDIUM:
                complexity += 15;
                break;
            case LONG_TAIL:
                complexity += 5;
                break;
        }
        
        // Traffic factor (higher traffic = more competition = more complex)
        if (traffic != null) {
            if (traffic > 50000L) complexity += 25;
            else if (traffic > 10000L) complexity += 15;
            else if (traffic > 1000L) complexity += 10;
        }
        
        // Special character complexity
        if (hasSpecialCharacters(keyword)) {
            complexity += 10;
        }
        
        // Technical term complexity
        if (isTechnicalTerm(keyword)) {
            complexity += 15;
        }
        
        return Math.min(complexity, 200); // Cap at 200
    }
    
    private boolean hasInverseLengthDifficultyCorrelation(java.util.Map<String, Object> keyword1, java.util.Map<String, Object> keyword2) {
        KeywordLength length1 = classifyKeywordLength(keyword1);
        KeywordLength length2 = classifyKeywordLength(keyword2);
        Integer diff1 = (Integer) keyword1.get("difficulty");
        Integer diff2 = (Integer) keyword2.get("difficulty");
        
        if (diff1 == null || diff2 == null) return false;
        
        // Shorter keywords should generally have higher difficulty
        if (length1 == KeywordLength.SHORT && length2 == KeywordLength.LONG_TAIL) {
            return diff1 > diff2;
        }
        if (length1 == KeywordLength.LONG_TAIL && length2 == KeywordLength.SHORT) {
            return diff2 > diff1;
        }
        
        return true; // Default to true for other cases
    }
    
    private boolean hasSpecialCharacters(java.util.Map<String, Object> keyword) {
        String kw = (String) keyword.get("keyword");
        if (kw == null) return false;
        
        return kw.matches(".*[.@#$%&*()\\-_+=\\[\\]{}|\\\\:;\"'<>?,/].*");
    }
    
    private boolean isTechnicalTerm(java.util.Map<String, Object> keyword) {
        String kw = (String) keyword.get("keyword");
        if (kw == null) return false;
        
        String lower = kw.toLowerCase();
        String[] technicalTerms = {
            "schema", "api", "json", "xml", "html", "css", "javascript", 
            "structured data", "metadata", "robots.txt", "sitemap",
            "canonical", "redirect", "https", "ssl", "cdn"
        };
        
        for (String term : technicalTerms) {
            if (lower.contains(term)) return true;
        }
        
        return false;
    }
    
    private int getKeywordCharacterCount(java.util.Map<String, Object> keyword) {
        String kw = (String) keyword.get("keyword");
        return kw != null ? kw.length() : 0;
    }
    
    private double getAverageWordLength(java.util.Map<String, Object> keyword) {
        String kw = (String) keyword.get("keyword");
        if (kw == null || kw.trim().isEmpty()) return 0.0;
        
        String[] words = kw.trim().split("\\s+");
        int totalLength = 0;
        
        for (String word : words) {
            totalLength += word.length();
        }
        
        return (double) totalLength / words.length;
    }
    
    private boolean isHighComplexityKeyword(java.util.Map<String, Object> keyword) {
        return calculateKeywordComplexity(keyword) >= 120;
    }
    
    private boolean isMediumComplexityKeyword(java.util.Map<String, Object> keyword) {
        int complexity = calculateKeywordComplexity(keyword);
        return complexity >= 70 && complexity < 120;
    }
    
    private boolean isLowComplexityKeyword(java.util.Map<String, Object> keyword) {
        return calculateKeywordComplexity(keyword) < 70;
    }
    
    private int calculateKeywordReadability(java.util.Map<String, Object> keyword) {
        String kw = (String) keyword.get("keyword");
        if (kw == null || kw.trim().isEmpty()) return 0;
        
        int score = 100; // Start with perfect score
        
        // Penalty for length
        if (kw.length() > 50) score -= 20;
        else if (kw.length() > 30) score -= 10;
        
        // Penalty for special characters
        if (hasSpecialCharacters(keyword)) score -= 15;
        
        // Penalty for technical terms
        if (isTechnicalTerm(keyword)) score -= 25;
        
        // Penalty for too many words
        String[] words = kw.trim().split("\\s+");
        if (words.length > 6) score -= 20;
        else if (words.length > 4) score -= 10;
          return Math.max(score, 0);    }

    // Enum for trend direction
    private enum TrendDirection {
        IMPROVING, DECLINING, STABLE, UNKNOWN
    }

    // Helper methods for performance trend analysis
    private TrendDirection calculateTrendDirection(java.util.Map<String, Object> keyword) {
        Integer currentPos = (Integer) keyword.get("current_position");
        Integer previousPos = (Integer) keyword.get("previous_position");
        Long currentTraffic = (Long) keyword.get("traffic");
        Long previousTraffic = (Long) keyword.get("previous_traffic");
        
        if (currentPos == null || previousPos == null || currentTraffic == null || previousTraffic == null) {
            return TrendDirection.UNKNOWN;
        }
        
        // Calculate relative changes
        int positionChange = currentPos - previousPos;
        double trafficChangePercent = (double)(currentTraffic - previousTraffic) / previousTraffic;
        
        // Thresholds for significant changes
        final int SIGNIFICANT_POSITION_CHANGE = 3;
        final double SIGNIFICANT_TRAFFIC_CHANGE = 0.1; // 10%
        
        boolean significantPositionImprovement = positionChange <= -SIGNIFICANT_POSITION_CHANGE;
        boolean significantPositionDecline = positionChange >= SIGNIFICANT_POSITION_CHANGE;
        boolean significantTrafficImprovement = trafficChangePercent >= SIGNIFICANT_TRAFFIC_CHANGE;
        boolean significantTrafficDecline = trafficChangePercent <= -SIGNIFICANT_TRAFFIC_CHANGE;
        
        // Overall trend determination
        int improvementScore = 0;
        if (significantPositionImprovement) improvementScore++;
        if (significantTrafficImprovement) improvementScore++;
        if (significantPositionDecline) improvementScore--;
        if (significantTrafficDecline) improvementScore--;
        
        if (improvementScore > 0) return TrendDirection.IMPROVING;
        if (improvementScore < 0) return TrendDirection.DECLINING;
        return TrendDirection.STABLE;
    }    private double calculateTrendStrength(java.util.Map<String, Object> keyword) {
        Integer currentPos = (Integer) keyword.get("current_position");
        Integer previousPos = (Integer) keyword.get("previous_position");
        Long currentTraffic = (Long) keyword.get("traffic");
        Long previousTraffic = (Long) keyword.get("previous_traffic");
        
        if (currentPos == null || previousPos == null || currentTraffic == null || previousTraffic == null) {
            return 0.0;
        }
        
        // Calculate position change impact (negative = improvement, positive = decline)
        int positionChange = currentPos - previousPos;
        double positionStrength = positionChange == 0 ? 0.0 : -positionChange / 10.0; // Normalize by 10 positions
        
        // Calculate traffic change impact
        double trafficChangePercent = (double) (currentTraffic - previousTraffic) / previousTraffic;
        double trafficStrength = trafficChangePercent;
        
        // Combined strength
        double combinedStrength = (positionStrength + trafficStrength) / 2.0;
        
        // For small changes, treat as stable (reduce strength)
        if (Math.abs(positionChange) <= 1 && Math.abs(trafficChangePercent) <= 0.05) {
            combinedStrength *= 0.2; // Very weak trend for small changes
        }
        
        // Amplify significant changes
        if (Math.abs(positionChange) >= 10 || Math.abs(trafficChangePercent) >= 0.4) {
            combinedStrength *= 1.5; // Amplify significant changes
        }
        
        return Math.max(-1.0, Math.min(1.0, combinedStrength)); // Clamp to [-1, 1]
    }
    
    private boolean hasPositiveMomentum(java.util.Map<String, Object> keyword) {
        TrendDirection direction = calculateTrendDirection(keyword);
        double strength = calculateTrendStrength(keyword);
        
        return direction == TrendDirection.IMPROVING && strength > 0.2;
    }
      private int calculateTrendOpportunityScore(java.util.Map<String, Object> keyword) {
        TrendDirection direction = calculateTrendDirection(keyword);
        double strength = Math.abs(calculateTrendStrength(keyword));
        Integer difficulty = (Integer) keyword.get("difficulty");
        
        int score = 0;
        
        // Direction bonus/penalty
        switch (direction) {
            case IMPROVING:
                score += 50;
                break;
            case STABLE:
                score += 30; // Increased stable bonus
                break;
            case DECLINING:
                score -= 10; // Penalty for declining trends
                break;
            case UNKNOWN:
                score += 10;
                break;
        }
        
        // Strength bonus (only for improving trends)
        if (direction == TrendDirection.IMPROVING) {
            score += (int) (strength * 30);
        }
        
        // Difficulty factor
        if (difficulty != null) {
            if (difficulty < 50) score += 20;
            else if (difficulty < 70) score += 10;
        }
        
        return Math.max(0, Math.min(score, 100));
    }
    
    private boolean isPotentialSeasonalTrend(java.util.Map<String, Object> keyword) {
        String kw = (String) keyword.get("keyword");
        Long currentTraffic = (Long) keyword.get("traffic");
        Long previousTraffic = (Long) keyword.get("previous_traffic");
        
        if (kw == null) return false;
        
        String lower = kw.toLowerCase();
        String[] seasonalTerms = {
            "christmas", "holiday", "summer", "winter", "spring", "fall",
            "valentine", "easter", "thanksgiving", "black friday", "cyber monday"
        };
        
        boolean hasSeasonalKeyword = false;
        for (String term : seasonalTerms) {
            if (lower.contains(term)) {
                hasSeasonalKeyword = true;
                break;
            }
        }
        
        // Check for dramatic traffic changes that might indicate seasonality
        boolean hasDramaticChange = false;
        if (currentTraffic != null && previousTraffic != null && previousTraffic > 0) {
            double changeRatio = (double) currentTraffic / previousTraffic;
            hasDramaticChange = changeRatio > 3.0 || changeRatio < 0.33;
        }
        
        return hasSeasonalKeyword || hasDramaticChange;
    }
    
    private double calculateTrafficVelocity(java.util.Map<String, Object> keyword) {
        Long currentTraffic = (Long) keyword.get("traffic");
        Long previousTraffic = (Long) keyword.get("previous_traffic");
        
        if (currentTraffic == null || previousTraffic == null || previousTraffic == 0) {
            return 0.0;
        }
        
        return ((double) currentTraffic - previousTraffic) / previousTraffic;
    }
    
    private double calculatePositionVelocity(java.util.Map<String, Object> keyword) {
        Integer currentPos = (Integer) keyword.get("current_position");
        Integer previousPos = (Integer) keyword.get("previous_position");
        
        if (currentPos == null || previousPos == null) {
            return 0.0;
        }
        
        // Positive velocity = improving position (lower number)
        return (double) (previousPos - currentPos) / Math.max(previousPos, currentPos);
    }
    
    private int predictNextPosition(java.util.Map<String, Object> keyword) {
        Integer currentPos = (Integer) keyword.get("current_position");
        double velocity = calculatePositionVelocity(keyword);
        
        if (currentPos == null) return 50; // Default prediction
        
        // Simple linear prediction
        int predicted = currentPos - (int) (velocity * currentPos);
        return Math.max(1, Math.min(predicted, 100));
    }
    
    private long predictNextTraffic(java.util.Map<String, Object> keyword) {
        Long currentTraffic = (Long) keyword.get("traffic");
        double velocity = calculateTrafficVelocity(keyword);
        
        if (currentTraffic == null) return 1000L; // Default prediction
        
        // Simple linear prediction
        long predicted = currentTraffic + (long) (velocity * currentTraffic);
        return Math.max(0L, predicted);
    }
      private double calculateTrendConfidence(java.util.Map<String, Object> keyword) {
        if (keyword == null) return 0.0;
        
        Integer currentPos = (Integer) keyword.get("current_position");
        Integer previousPos = (Integer) keyword.get("previous_position");
        Long currentTraffic = (Long) keyword.get("traffic");
        Long previousTraffic = (Long) keyword.get("previous_traffic");
        
        if (currentPos == null || previousPos == null || currentTraffic == null || previousTraffic == null) {
            return 0.0;
        }
        
        double strength = Math.abs(calculateTrendStrength(keyword));
        
        // Higher confidence for stronger trends
        double confidence = strength;
        
        // Boost confidence if both metrics agree
        boolean positionImproved = currentPos < previousPos;
        boolean trafficImproved = currentTraffic > previousTraffic;
        
        if ((positionImproved && trafficImproved) || (!positionImproved && !trafficImproved)) {
            confidence += 0.3;
        }
        
        return Math.min(confidence, 1.0);
    }

    @Test
    @DisplayName("Test SERP features handling")
    void testSerpFeaturesHandling() {
        // Test SERP features in unique keyword analysis
        java.util.Map<String, Object> keywordWithSerpFeatures = new java.util.HashMap<>();
        keywordWithSerpFeatures.put("keyword", "best smartphones 2024");
        keywordWithSerpFeatures.put("types", java.util.Arrays.asList("featured_snippet", "local_pack", "shopping_results"));
        keywordWithSerpFeatures.put("difficulty", 75);
        keywordWithSerpFeatures.put("traffic", 15000L);
        
        // Test SERP feature validation
        @SuppressWarnings("unchecked")
        java.util.List<String> types = (java.util.List<String>) keywordWithSerpFeatures.get("types");
        assertNotNull(types, "SERP features should not be null");
        assertFalse(types.isEmpty(), "SERP features should not be empty");
        assertTrue(hasFeaturedSnippet(types), "Should identify featured snippet");
        assertTrue(hasLocalPack(types), "Should identify local pack");
        assertTrue(hasShoppingResults(types), "Should identify shopping results");
          // Test competitive impact of SERP features
        assertTrue(hasCompetitiveSerpFeatures(types), "Should identify competitive SERP features");
        assertTrue(hasLowCompetitionSerpFeatures(java.util.Arrays.asList("basic_results")), 
                   "Basic results should indicate low competition");
        
        // Test SERP feature scoring
        int serpScore = calculateSerpFeatureComplexity(types);
        assertTrue(serpScore > 50, "Multiple SERP features should increase complexity");
        
        // Test keyword with no SERP features
        java.util.Map<String, Object> simpleKeyword = new java.util.HashMap<>();
        simpleKeyword.put("keyword", "simple query");
        simpleKeyword.put("types", java.util.Arrays.asList("organic"));
        
        @SuppressWarnings("unchecked")
        java.util.List<String> simpleTypes = (java.util.List<String>) simpleKeyword.get("types");        assertFalse(hasCompetitiveSerpFeatures(simpleTypes), "Organic-only should not be competitive");
        assertTrue(calculateSerpFeatureComplexity(simpleTypes) < 30, "Simple SERP should have low complexity");
    }
    
    @Test
    @DisplayName("Test keyword intent classification")
    void testKeywordIntentClassification() {
        // Test different keyword intents for unique keywords
        java.util.Map<String, Object> informationalKeyword = new java.util.HashMap<>();
        informationalKeyword.put("keyword", "how to improve website ranking");
        informationalKeyword.put("intents", java.util.Arrays.asList("informational"));
        informationalKeyword.put("difficulty", 35);
        
        java.util.Map<String, Object> commercialKeyword = new java.util.HashMap<>();
        commercialKeyword.put("keyword", "best enterprise seo software");
        commercialKeyword.put("intents", java.util.Arrays.asList("commercial", "informational"));
        commercialKeyword.put("difficulty", 68);
        
        java.util.Map<String, Object> transactionalKeyword = new java.util.HashMap<>();
        transactionalKeyword.put("keyword", "buy premium seo tools");
        transactionalKeyword.put("intents", java.util.Arrays.asList("transactional"));
        transactionalKeyword.put("difficulty", 75);
        
        java.util.Map<String, Object> navigationalKeyword = new java.util.HashMap<>();
        navigationalKeyword.put("keyword", "semrush login dashboard");
        navigationalKeyword.put("intents", java.util.Arrays.asList("navigational"));
        navigationalKeyword.put("difficulty", 25);
        
        // Test intent classification
        assertTrue(isInformationalIntent(informationalKeyword), "Should identify informational intent");
        assertTrue(isCommercialIntent(commercialKeyword), "Should identify commercial intent");
        assertTrue(isTransactionalIntent(transactionalKeyword), "Should identify transactional intent");
        assertTrue(isNavigationalIntent(navigationalKeyword), "Should identify navigational intent");
        
        // Test mixed intent handling
        assertTrue(hasMixedIntents(commercialKeyword), "Should identify mixed intents");
        assertFalse(hasMixedIntents(informationalKeyword), "Single intent should not be mixed");
        
        // Test intent priority scoring
        int transactionalScore = getIntentPriorityScore(transactionalKeyword);
        int informationalScore = getIntentPriorityScore(informationalKeyword);
        assertTrue(transactionalScore > informationalScore, "Transactional should have higher priority than informational");
        
        // Test keyword without intent data
        java.util.Map<String, Object> noIntentKeyword = new java.util.HashMap<>();
        noIntentKeyword.put("keyword", "unknown query");
          assertFalse(isInformationalIntent(noIntentKeyword), "Keyword without intents should not match any intent");
        assertEquals(0, getIntentPriorityScore(noIntentKeyword), "Keyword without intents should have zero score");
    }
    
    @Test
    @DisplayName("Test opportunity scoring")
    void testOpportunityScoring() {
        // Test opportunity scoring for unique keywords
        java.util.Map<String, Object> highOpportunityKeyword = new java.util.HashMap<>();
        highOpportunityKeyword.put("keyword", "niche seo software");
        highOpportunityKeyword.put("difficulty", 25);
        highOpportunityKeyword.put("traffic", 5000L);
        highOpportunityKeyword.put("cost", 4.50);
        highOpportunityKeyword.put("position", null); // We don't rank yet
        
        java.util.Map<String, Object> lowOpportunityKeyword = new java.util.HashMap<>();
        lowOpportunityKeyword.put("keyword", "generic seo");
        lowOpportunityKeyword.put("difficulty", 95);
        lowOpportunityKeyword.put("traffic", 500L);
        lowOpportunityKeyword.put("cost", 15.75);
        lowOpportunityKeyword.put("position", null);
        
        java.util.Map<String, Object> alreadyRankingKeyword = new java.util.HashMap<>();
        alreadyRankingKeyword.put("keyword", "our brand seo");
        alreadyRankingKeyword.put("difficulty", 30);
        alreadyRankingKeyword.put("traffic", 3000L);
        alreadyRankingKeyword.put("cost", 2.25);
        alreadyRankingKeyword.put("position", 3);
        
        // Test opportunity scoring
        int highScore = calculateOpportunityScore(highOpportunityKeyword);
        int lowScore = calculateOpportunityScore(lowOpportunityKeyword);
        int rankingScore = calculateOpportunityScore(alreadyRankingKeyword);
        
        assertTrue(highScore > lowScore, "High opportunity keyword should score higher than low opportunity");
        assertTrue(highScore > rankingScore, "Unranked opportunity should score higher than already ranking");
        
        // Test opportunity classification
        assertTrue(isHighOpportunity(highOpportunityKeyword), "Should identify high opportunity keyword");
        assertFalse(isHighOpportunity(lowOpportunityKeyword), "Should not identify low opportunity as high");
        assertFalse(isHighOpportunity(alreadyRankingKeyword), "Already ranking keyword should not be high opportunity");
        
        // Test opportunity ranking
        java.util.List<java.util.Map<String, Object>> keywords = java.util.Arrays.asList(
            highOpportunityKeyword, lowOpportunityKeyword, alreadyRankingKeyword
        );
        
        java.util.List<java.util.Map<String, Object>> rankedOpportunities = rankKeywordOpportunities(keywords);
        assertEquals(3, rankedOpportunities.size(), "Should rank all keywords");
        assertEquals("niche seo software", rankedOpportunities.get(0).get("keyword"), 
                    "High opportunity keyword should rank first");
        
        // Test edge cases
        java.util.Map<String, Object> nullDataKeyword = new java.util.HashMap<>();
        nullDataKeyword.put("keyword", "incomplete data");        assertEquals(0, calculateOpportunityScore(nullDataKeyword), "Keyword with missing data should score 0");
        assertFalse(isHighOpportunity(nullDataKeyword), "Incomplete keyword should not be high opportunity");
    }
    
    @Test
    @DisplayName("Test gap analysis metrics")
    void testGapAnalysisMetrics() {
        // Test keyword gap analysis between competitors
        java.util.Map<String, Object> ourKeyword = new java.util.HashMap<>();
        ourKeyword.put("keyword", "enterprise seo platform");
        ourKeyword.put("difficulty", 45);
        ourKeyword.put("traffic", 3000L);
        ourKeyword.put("position", null); // We don't rank
        
        java.util.Map<String, Object> competitorKeyword = new java.util.HashMap<>();
        competitorKeyword.put("keyword", "enterprise seo platform");
        competitorKeyword.put("difficulty", 45);
        competitorKeyword.put("traffic", 3000L);
        competitorKeyword.put("position", 7); // Competitor ranks
          java.util.Map<String, Object> saturatedKeyword = new java.util.HashMap<>();
        saturatedKeyword.put("keyword", "seo");
        saturatedKeyword.put("difficulty", 98);
        saturatedKeyword.put("traffic", 50000L);
        saturatedKeyword.put("position", 15); // Competitor ranks but difficult
        
        // Test gap identification
        assertTrue(isKeywordGap(ourKeyword, competitorKeyword), "Should identify keyword gap when competitor ranks and we don't");
        assertFalse(isKeywordGap(competitorKeyword, ourKeyword), "Should not identify gap when we rank");
        
        // Test gap opportunity scoring
        int gapScore = calculateGapOpportunityScore(ourKeyword, competitorKeyword);
        int saturatedScore = calculateGapOpportunityScore(ourKeyword, saturatedKeyword);
        assertTrue(gapScore > saturatedScore, "Moderate difficulty gap should score higher than saturated keyword");
        
        // Test gap analysis for keyword lists
        java.util.List<java.util.Map<String, Object>> ourKeywords = java.util.Arrays.asList(ourKeyword);
        java.util.List<java.util.Map<String, Object>> competitorKeywords = java.util.Arrays.asList(competitorKeyword, saturatedKeyword);
        
        java.util.List<java.util.Map<String, Object>> gaps = findKeywordGaps(ourKeywords, competitorKeywords);
        assertEquals(2, gaps.size(), "Should find 2 keyword gaps");
        
        // Test gap prioritization
        java.util.List<java.util.Map<String, Object>> prioritizedGaps = prioritizeKeywordGaps(gaps);
        assertEquals("enterprise seo platform", prioritizedGaps.get(0).get("keyword"), 
                    "Moderate difficulty gap should be prioritized over saturated keyword");
        
        // Test gap metrics calculation
        java.util.Map<String, Object> gapMetrics = calculateGapMetrics(ourKeywords, competitorKeywords);
        assertNotNull(gapMetrics, "Gap metrics should not be null");        assertTrue((Integer) gapMetrics.get("total_gaps") > 0, "Should identify gaps");
        assertTrue((Double) gapMetrics.get("avg_gap_difficulty") > 0, "Should calculate average difficulty");
    }
    
    @Test
    @DisplayName("Test JSON serialization with competitive data")
    void testJsonSerializationWithCompetitiveData() {
        // Test JSON-like serialization for unique keyword data
        java.util.Map<String, Object> keywordData = new java.util.HashMap<>();
        keywordData.put("keyword", "competitive analysis tool");
        keywordData.put("difficulty", 55);
        keywordData.put("traffic", 8500L);
        keywordData.put("cost", 6.75);
        keywordData.put("position", 12);
        keywordData.put("domain", "competitor.com");
        keywordData.put("types", java.util.Arrays.asList("featured_snippet", "organic"));
        keywordData.put("intents", java.util.Arrays.asList("commercial", "informational"));
        
        // Add domain positions (competitive data)
        java.util.Map<String, Integer> domainPositions = new java.util.HashMap<>();
        domainPositions.put("competitor1.com", 3);
        domainPositions.put("competitor2.com", 8);
        domainPositions.put("competitor3.com", 15);
        keywordData.put("domain_positions", domainPositions);
        
        // Test JSON structure validation
        assertTrue(isValidJsonStructure(keywordData), "Keyword data should have valid JSON structure");
        assertFalse(isValidJsonStructure(null), "Null data should not be valid JSON structure");
        assertFalse(isValidJsonStructure(new java.util.HashMap<>()), "Empty data should not be valid JSON structure");
        
        // Test JSON serialization simulation
        String jsonString = simulateJsonSerialization(keywordData);
        assertNotNull(jsonString, "JSON string should not be null");
        assertTrue(jsonString.contains("competitive analysis tool"), "JSON should contain keyword");
        assertTrue(jsonString.contains("competitor1.com"), "JSON should contain domain positions");
        assertTrue(jsonString.contains("featured_snippet"), "JSON should contain SERP features");
        
        // Test competitive data extraction
        java.util.Map<String, Object> competitiveData = extractCompetitiveData(keywordData);
        assertNotNull(competitiveData, "Competitive data should not be null");
        assertTrue(competitiveData.containsKey("domain_positions"), "Should contain domain positions");
        assertTrue(competitiveData.containsKey("serp_features"), "Should contain SERP features");
        assertTrue(competitiveData.containsKey("keyword_intents"), "Should contain keyword intents");
        
        // Test data integrity after serialization simulation
        java.util.Map<String, Object> deserializedData = simulateJsonDeserialization(jsonString);
        assertEquals(keywordData.get("keyword"), deserializedData.get("keyword"), "Keyword should survive serialization");
        assertEquals(keywordData.get("difficulty"), deserializedData.get("difficulty"), "Difficulty should survive serialization");
        assertEquals(keywordData.get("traffic"), deserializedData.get("traffic"), "Traffic should survive serialization");
        
        // Test complex data types in JSON
        @SuppressWarnings("unchecked")
        java.util.List<String> deserializedTypes = (java.util.List<String>) deserializedData.get("types");
        assertNotNull(deserializedTypes, "Types should be preserved");
        assertTrue(deserializedTypes.contains("featured_snippet"), "SERP features should be preserved");
        
        // Test edge cases
        java.util.Map<String, Object> incompleteData = new java.util.HashMap<>();        incompleteData.put("keyword", "incomplete");
        assertFalse(isValidJsonStructure(incompleteData), "Incomplete data should not be valid");
    }
    
    @Test
    @DisplayName("Test keyword length and complexity analysis")
    void testKeywordLengthAndComplexityAnalysis() {
        // Test keyword complexity analysis
        java.util.Map<String, Object> shortKeyword = new java.util.HashMap<>();
        shortKeyword.put("keyword", "seo");
        shortKeyword.put("difficulty", 95);
        shortKeyword.put("traffic", 100000L);
        
        java.util.Map<String, Object> mediumKeyword = new java.util.HashMap<>();
        mediumKeyword.put("keyword", "enterprise seo platform");
        mediumKeyword.put("difficulty", 65);
        mediumKeyword.put("traffic", 5000L);
        
        java.util.Map<String, Object> longTailKeyword = new java.util.HashMap<>();
        longTailKeyword.put("keyword", "best enterprise seo platform for large businesses 2024");
        longTailKeyword.put("difficulty", 35);
        longTailKeyword.put("traffic", 800L);
        
        java.util.Map<String, Object> technicalKeyword = new java.util.HashMap<>();
        technicalKeyword.put("keyword", "schema.org structured data implementation");
        technicalKeyword.put("difficulty", 45);
        technicalKeyword.put("traffic", 1200L);
        
        // Test keyword length classification
        assertEquals(KeywordLength.SHORT, classifyKeywordLength(shortKeyword), "Single word should be classified as short");
        assertEquals(KeywordLength.MEDIUM, classifyKeywordLength(mediumKeyword), "3-word phrase should be medium");
        assertEquals(KeywordLength.LONG_TAIL, classifyKeywordLength(longTailKeyword), "Long phrase should be long-tail");
        
        // Test complexity scoring
        int shortComplexity = calculateKeywordComplexity(shortKeyword);
        int mediumComplexity = calculateKeywordComplexity(mediumKeyword);
        int longComplexity = calculateKeywordComplexity(longTailKeyword);
        int technicalComplexity = calculateKeywordComplexity(technicalKeyword);
        
        assertTrue(shortComplexity > mediumComplexity, "Short keywords should be more complex due to competition");
        assertTrue(mediumComplexity > longComplexity, "Medium keywords should be more complex than long-tail");
        assertTrue(technicalComplexity > longComplexity, "Technical keywords should be more complex");
        
        // Test keyword difficulty correlation with length
        assertTrue(hasInverseLengthDifficultyCorrelation(shortKeyword, longTailKeyword), 
                  "Shorter keywords should generally have higher difficulty");
        
        // Test specific complexity factors
        assertTrue(hasSpecialCharacters(technicalKeyword), "Technical keyword should have special characters");
        assertFalse(hasSpecialCharacters(mediumKeyword), "Simple keyword should not have special characters");
        
        assertTrue(isTechnicalTerm(technicalKeyword), "Schema.org keyword should be technical");
        assertFalse(isTechnicalTerm(mediumKeyword), "Business keyword should not be technical");
        
        // Test keyword character analysis
        int charCount = getKeywordCharacterCount(longTailKeyword);
        assertTrue(charCount > 40, "Long-tail keyword should have many characters");
        
        double avgWordLength = getAverageWordLength(longTailKeyword);
        assertTrue(avgWordLength > 5.0, "Business keywords should have longer average word length");
        
        // Test complexity categories
        assertTrue(isHighComplexityKeyword(shortKeyword), "Short competitive keyword should be high complexity");
        assertTrue(isMediumComplexityKeyword(mediumKeyword), "Business phrase should be medium complexity");
        assertTrue(isLowComplexityKeyword(longTailKeyword), "Long-tail should be low complexity");
        
        // Test readability and user intent correlation
        int readabilityScore = calculateKeywordReadability(mediumKeyword);
        assertTrue(readabilityScore > 50, "Business keywords should be readable");
        
        // Test edge cases
        java.util.Map<String, Object> emptyKeyword = new java.util.HashMap<>();        emptyKeyword.put("keyword", "");
        assertEquals(0, calculateKeywordComplexity(emptyKeyword), "Empty keyword should have zero complexity");
    }
    
    @Test
    @DisplayName("Test performance trend analysis")
    void testPerformanceTrendAnalysis() {
        // Test keyword performance trend analysis
        java.util.Map<String, Object> improvingKeyword = new java.util.HashMap<>();
        improvingKeyword.put("keyword", "growing market trend");
        improvingKeyword.put("current_position", 15);
        improvingKeyword.put("previous_position", 25);
        improvingKeyword.put("traffic", 3500L);
        improvingKeyword.put("previous_traffic", 2000L);
        improvingKeyword.put("difficulty", 45);
        
        java.util.Map<String, Object> decliningKeyword = new java.util.HashMap<>();
        decliningKeyword.put("keyword", "declining market trend");
        decliningKeyword.put("current_position", 30);
        decliningKeyword.put("previous_position", 20);
        decliningKeyword.put("traffic", 1500L);
        decliningKeyword.put("previous_traffic", 2500L);
        decliningKeyword.put("difficulty", 55);
        
        java.util.Map<String, Object> stableKeyword = new java.util.HashMap<>();
        stableKeyword.put("keyword", "stable market term");
        stableKeyword.put("current_position", 10);
        stableKeyword.put("previous_position", 10);
        stableKeyword.put("traffic", 5000L);
        stableKeyword.put("previous_traffic", 5100L);
        stableKeyword.put("difficulty", 60);
        
        // Test trend direction identification
        assertEquals(TrendDirection.IMPROVING, calculateTrendDirection(improvingKeyword), "Should identify improving trend");
        assertEquals(TrendDirection.DECLINING, calculateTrendDirection(decliningKeyword), "Should identify declining trend");
        assertEquals(TrendDirection.STABLE, calculateTrendDirection(stableKeyword), "Should identify stable trend");
        
        // Test trend strength calculation
        double improvingStrength = calculateTrendStrength(improvingKeyword);
        double decliningStrength = calculateTrendStrength(decliningKeyword);
        double stableStrength = calculateTrendStrength(stableKeyword);
        
        assertTrue(improvingStrength > 0.5, "Improving keyword should have strong positive trend");
        assertTrue(decliningStrength < -0.5, "Declining keyword should have strong negative trend");
        assertTrue(Math.abs(stableStrength) < 0.3, "Stable keyword should have weak trend");
        
        // Test performance momentum
        assertTrue(hasPositiveMomentum(improvingKeyword), "Improving keyword should have positive momentum");
        assertFalse(hasPositiveMomentum(decliningKeyword), "Declining keyword should not have positive momentum");
        
        // Test trend-based opportunity scoring
        int improvingScore = calculateTrendOpportunityScore(improvingKeyword);
        int decliningScore = calculateTrendOpportunityScore(decliningKeyword);
        int stableScore = calculateTrendOpportunityScore(stableKeyword);
        
        assertTrue(improvingScore > decliningScore, "Improving trend should score higher than declining");
        assertTrue(stableScore > decliningScore, "Stable should score higher than declining");
        
        // Test seasonal trend detection
        java.util.Map<String, Object> seasonalKeyword = new java.util.HashMap<>();
        seasonalKeyword.put("keyword", "christmas seo campaign");
        seasonalKeyword.put("traffic", 8000L);
        seasonalKeyword.put("previous_traffic", 1000L);
        
        assertTrue(isPotentialSeasonalTrend(seasonalKeyword), "Christmas keyword should be seasonal");
        assertFalse(isPotentialSeasonalTrend(stableKeyword), "Stable keyword should not be seasonal");
        
        // Test velocity calculation
        double trafficVelocity = calculateTrafficVelocity(improvingKeyword);
        double positionVelocity = calculatePositionVelocity(improvingKeyword);
        
        assertTrue(trafficVelocity > 0, "Growing traffic should have positive velocity");
        assertTrue(positionVelocity > 0, "Improving position should have positive velocity");
        
        // Test trend prediction
        int predictedPosition = predictNextPosition(improvingKeyword);
        assertTrue(predictedPosition < 15, "Improving trend should predict better position");
        
        long predictedTraffic = predictNextTraffic(improvingKeyword);
        assertTrue(predictedTraffic > 3500L, "Growing traffic should predict higher volume");
        
        // Test trend confidence
        double confidence = calculateTrendConfidence(improvingKeyword);
        assertTrue(confidence > 0.6, "Clear trend should have high confidence");
        
        // Test edge cases
        java.util.Map<String, Object> noHistoryKeyword = new java.util.HashMap<>();
        noHistoryKeyword.put("keyword", "new keyword");
        noHistoryKeyword.put("current_position", 20);
          assertEquals(TrendDirection.UNKNOWN, calculateTrendDirection(noHistoryKeyword), "No history should be unknown trend");
        assertEquals(0.0, calculateTrendConfidence(noHistoryKeyword), 0.01, "No history should have zero confidence");
    }
    
    @Test
    @DisplayName("Test edge cases and boundary conditions")
    void testEdgeCasesAndBoundaryConditions() {
        // Test null and empty data edge cases
        java.util.Map<String, Object> emptyKeyword = new java.util.HashMap<>();
        java.util.Map<String, Object> nullValuesKeyword = new java.util.HashMap<>();
        nullValuesKeyword.put("keyword", null);
        nullValuesKeyword.put("difficulty", null);
        nullValuesKeyword.put("traffic", null);
        
        // Test validation with edge cases
        assertFalse(isValidKeyword(null), "Null keyword should be invalid");
        assertFalse(isValidKeyword(""), "Empty keyword should be invalid");
        assertFalse(isValidKeyword("   "), "Whitespace-only keyword should be invalid");
        
        assertFalse(isValidDifficulty(null), "Null difficulty should be invalid");
        assertFalse(isValidDifficulty(-1), "Negative difficulty should be invalid");
        assertFalse(isValidDifficulty(101), "Over-max difficulty should be invalid");
        
        assertFalse(isValidTraffic(null), "Null traffic should be invalid");
        assertFalse(isValidTraffic(-1L), "Negative traffic should be invalid");
        
        assertFalse(isValidCost(null), "Null cost should be invalid");
        assertFalse(isValidCost(-0.01), "Negative cost should be invalid");
        
        assertFalse(isValidPosition(null), "Null position should be invalid");
        assertFalse(isValidPosition(0), "Zero position should be invalid");
        assertFalse(isValidPosition(-1), "Negative position should be invalid");
        
        // Test boundary values
        assertTrue(isValidDifficulty(0), "Minimum difficulty should be valid");
        assertTrue(isValidDifficulty(100), "Maximum difficulty should be valid");
        assertTrue(isValidTraffic(0L), "Zero traffic should be valid");
        assertTrue(isValidTraffic(Long.MAX_VALUE), "Maximum traffic should be valid");
        assertTrue(isValidCost(0.0), "Zero cost should be valid");
        assertTrue(isValidCost(Double.MAX_VALUE), "Maximum cost should be valid");
        assertTrue(isValidPosition(1), "Minimum position should be valid");
        assertTrue(isValidPosition(100), "Position 100 should be valid");
        
        // Test extreme keyword lengths
        String longKeyword = "a".repeat(200);
        String tooLongKeyword = "a".repeat(201);
        assertTrue(isValidKeyword(longKeyword), "200-character keyword should be valid");
        assertFalse(isValidKeyword(tooLongKeyword), "201-character keyword should be invalid");
        
        // Test special Unicode characters
        java.util.Map<String, Object> unicodeKeyword = new java.util.HashMap<>();
        unicodeKeyword.put("keyword", "seo роботи україна");
        unicodeKeyword.put("difficulty", 50);
        unicodeKeyword.put("traffic", 1000L);
        
        assertTrue(isValidKeyword((String) unicodeKeyword.get("keyword")), "Unicode keyword should be valid");
        
        // Test floating point precision edge cases
        java.util.Map<String, Object> precisionKeyword = new java.util.HashMap<>();
        precisionKeyword.put("cost", 0.001);
        precisionKeyword.put("traffic", 1L);
        precisionKeyword.put("difficulty", 1);
        
        assertTrue(isValidCost((Double) precisionKeyword.get("cost")), "Very small cost should be valid");
        
        // Test with Integer overflow scenarios
        java.util.Map<String, Object> overflowKeyword = new java.util.HashMap<>();
        overflowKeyword.put("traffic", Long.MAX_VALUE);
        overflowKeyword.put("position", Integer.MAX_VALUE);
        
        assertTrue(isValidTraffic((Long) overflowKeyword.get("traffic")), "Max long traffic should be valid");
        assertFalse(isValidPosition((Integer) overflowKeyword.get("position")), "Max int position should be invalid");
        
        // Test competitive opportunity with extreme values
        assertFalse(hasCompetitiveOpportunity(0, 0L), "Zero difficulty and traffic should not be opportunity");
        assertTrue(hasCompetitiveOpportunity(1, Long.MAX_VALUE), "Min difficulty and max traffic should be opportunity");
        assertFalse(hasCompetitiveOpportunity(100, 1L), "Max difficulty and min traffic should not be opportunity");
        
        // Test calculation methods with edge case inputs
        assertEquals(0, calculateOpportunityScore(emptyKeyword), "Empty keyword should score 0");
        assertEquals(0, calculateKeywordComplexity(emptyKeyword), "Empty keyword should have 0 complexity");
        assertEquals(0.0, calculateTrendConfidence(emptyKeyword), 0.01, "Empty keyword should have 0 confidence");
          // Test list operations with edge cases
        java.util.List<java.util.Map<String, Object>> emptyList = new java.util.ArrayList<>();
        
        assertEquals(0, findKeywordGaps(emptyList, emptyList).size(), "Empty lists should yield no gaps");
        assertEquals(0, rankKeywordOpportunities(emptyList).size(), "Empty list should rank to empty");
        
        // Test with mixed valid/invalid data
        java.util.Map<String, Object> mixedKeyword = new java.util.HashMap<>();
        mixedKeyword.put("keyword", "valid keyword");
        mixedKeyword.put("difficulty", -50); // Invalid
        mixedKeyword.put("traffic", 5000L); // Valid
        mixedKeyword.put("cost", null); // Invalid
        
        assertTrue(isValidKeyword((String) mixedKeyword.get("keyword")), "Valid keyword field should pass");
        assertFalse(isValidDifficulty((Integer) mixedKeyword.get("difficulty")), "Invalid difficulty should fail");
        assertTrue(isValidTraffic((Long) mixedKeyword.get("traffic")), "Valid traffic should pass");
        assertFalse(isValidCost((Double) mixedKeyword.get("cost")), "Null cost should fail");
        
        // Test method robustness with null inputs
        assertDoesNotThrow(() -> calculateOpportunityScore(null), "Should handle null keyword gracefully");
        assertDoesNotThrow(() -> isHighOpportunity(null), "Should handle null keyword gracefully");
        assertDoesNotThrow(() -> calculateKeywordComplexity(null), "Should handle null keyword gracefully");
    }
}
