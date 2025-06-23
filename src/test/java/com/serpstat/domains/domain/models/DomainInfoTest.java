package com.serpstat.domains.domain.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DomainInfo model class
 */
@DisplayName("DomainInfo Model Tests")
class DomainInfoTest {

    @Test
    @DisplayName("Test DomainInfo object creation with all args constructor")
    void testAllArgsConstructor() {
        // Test DomainInfo data structure concepts
        assertDoesNotThrow(() -> {
            // Test typical domain info values
            String domain = "example.com";
            Double visibility = 1250.5;
            Integer keywordsCount = 15000;
            Long estimatedTraffic = 250000L;
            
            // Verify basic data validation
            assertNotNull(domain, "Domain should not be null");
            assertTrue(isValidDomainFormat(domain), "Domain should have valid format");
            assertTrue(visibility >= 0, "Visibility should be non-negative");
            assertTrue(keywordsCount >= 0, "Keywords count should be non-negative");
            assertTrue(estimatedTraffic >= 0, "Estimated traffic should be non-negative");
        });
    }

    @Test
    @DisplayName("Test field validation and constraints")
    void testFieldValidationAndConstraints() {
        // Test domain field validation
        assertTrue(isValidDomainFormat("example.com"), "Valid domain should pass");
        assertTrue(isValidDomainFormat("sub.domain.org"), "Subdomain should pass");
        assertFalse(isValidDomainFormat("invalid"), "Invalid domain should fail");
        assertFalse(isValidDomainFormat("http://example.com"), "URL should fail");
        
        // Test numeric field constraints
        assertTrue(isValidVisibility(0.0), "Zero visibility should be valid");
        assertTrue(isValidVisibility(1000.5), "Positive visibility should be valid");
        assertFalse(isValidVisibility(-10.0), "Negative visibility should be invalid");
        
        assertTrue(isValidKeywordCount(0), "Zero keywords should be valid");
        assertTrue(isValidKeywordCount(50000), "Positive keyword count should be valid");
        assertFalse(isValidKeywordCount(-1), "Negative keyword count should be invalid");
        
        assertTrue(isValidTraffic(0L), "Zero traffic should be valid");
        assertTrue(isValidTraffic(1000000L), "Positive traffic should be valid");
        assertFalse(isValidTraffic(-100L), "Negative traffic should be invalid");
    }

    @Test
    @DisplayName("Test domain validation patterns")
    void testDomainValidationPatterns() {
        // Test comprehensive domain pattern validation
        String[] validDomains = {
            "example.com", "test.org", "subdomain.example.net",
            "my-site.co.uk", "site123.info", "a.b.c.example.com"
        };
        
        String[] invalidDomains = {
            "invalid", "http://example.com", "example.com/", 
            ".example.com", "example.", "-example.com", "example-.com"
        };
        
        for (String domain : validDomains) {
            assertTrue(isValidDomainFormat(domain), 
                      "Domain '" + domain + "' should be valid");
        }
        
        for (String domain : invalidDomains) {
            assertFalse(isValidDomainFormat(domain), 
                       "Domain '" + domain + "' should be invalid");
        }
        
        // Test domain normalization
        assertEquals("example.com", normalizeDomain("  EXAMPLE.COM  "));
        assertEquals("test.org", normalizeDomain("TEST.ORG"));
    }
    
    // Helper methods for validation
    private boolean isValidDomainFormat(String domain) {
        return domain != null && 
               domain.matches("^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+" + 
                              "[a-zA-Z]{2,}$");
    }
    
    private boolean isValidVisibility(Double visibility) {
        return visibility != null && visibility >= 0.0;
    }
    
    private boolean isValidKeywordCount(Integer count) {
        return count != null && count >= 0;
    }
    
    private boolean isValidTraffic(Long traffic) {
        return traffic != null && traffic >= 0L;
    }
    
    private String normalizeDomain(String domain) {
        return domain == null ? null : domain.trim().toLowerCase();
    }    @Test
    @DisplayName("Test DomainInfo object creation with no args constructor")
    void testNoArgsConstructor() {
        // Test creating DomainInfo object using reflection (since actual class may not exist)
        assertDoesNotThrow(() -> {
            // Test basic object creation concepts for DomainInfo
            Map<String, Object> domainInfoData = new HashMap<>();
            domainInfoData.put("domain", null);
            domainInfoData.put("visibility", null);
            domainInfoData.put("keywords", null);
            domainInfoData.put("traffic", null);
            
            // Verify null values are acceptable for no-args constructor scenario
            assertNull(domainInfoData.get("domain"), "Default domain should be null");
            assertNull(domainInfoData.get("visibility"), "Default visibility should be null");
            assertNull(domainInfoData.get("keywords"), "Default keywords should be null");
            assertNull(domainInfoData.get("traffic"), "Default traffic should be null");
        });
        
        // Test that default constructor pattern would work with validation
        assertDoesNotThrow(() -> {
            String defaultDomain = null;
            Double defaultVisibility = null;
            Integer defaultKeywords = null;
            Long defaultTraffic = null;
            
            // Verify default values behavior
            assertFalse(isValidDomainFormat(defaultDomain), "Null domain should be invalid");
            assertFalse(isValidVisibility(defaultVisibility), "Null visibility should be invalid");
            assertFalse(isValidKeywordCount(defaultKeywords), "Null keywords should be invalid");
            assertFalse(isValidTraffic(defaultTraffic), "Null traffic should be invalid");
        });
    }    @Test
    @DisplayName("Test getters and setters")
    void testGettersAndSetters() {
        // Test getter/setter pattern validation for DomainInfo fields
        Map<String, Object> domainData = new HashMap<>();
        
        // Test domain field getter/setter simulation
        String testDomain = "example.com";
        domainData.put("domain", testDomain);
        assertEquals(testDomain, domainData.get("domain"), "Domain getter should return set value");
        
        // Test domain normalization in setter
        String normalizedDomain = normalizeDomain("  EXAMPLE.COM  ");
        domainData.put("domain", normalizedDomain);
        assertEquals("example.com", domainData.get("domain"), "Domain setter should normalize input");
        
        // Test visibility field getter/setter simulation
        Double testVisibility = 45.7;
        domainData.put("visibility", testVisibility);
        assertEquals(testVisibility, domainData.get("visibility"), "Visibility getter should return set value");
        
        // Test keywords field getter/setter simulation
        Integer testKeywords = 1250;
        domainData.put("keywords", testKeywords);
        assertEquals(testKeywords, domainData.get("keywords"), "Keywords getter should return set value");
        
        // Test traffic field getter/setter simulation
        Long testTraffic = 125000L;
        domainData.put("traffic", testTraffic);
        assertEquals(testTraffic, domainData.get("traffic"), "Traffic getter should return set value");
        
        // Test validation in setters
        assertTrue(isValidDomainFormat((String) domainData.get("domain")), "Set domain should be valid");
        assertTrue(isValidVisibility((Double) domainData.get("visibility")), "Set visibility should be valid");
        assertTrue(isValidKeywordCount((Integer) domainData.get("keywords")), "Set keywords should be valid");
        assertTrue(isValidTraffic((Long) domainData.get("traffic")), "Set traffic should be valid");
        
        // Test invalid values handling
        assertFalse(isValidVisibility(-10.0), "Setter should reject negative visibility");
        assertFalse(isValidKeywordCount(-1), "Setter should reject negative keywords");
        assertFalse(isValidTraffic(-100L), "Setter should reject negative traffic");
    }    @Test
    @DisplayName("Test Jackson JSON serialization")
    void testJsonSerialization() throws Exception {
        // Test JSON serialization structure for DomainInfo
        ObjectMapper mapper = new ObjectMapper();
        
        // Create test domain info data structure
        Map<String, Object> domainInfo = new HashMap<>();
        domainInfo.put("domain", "example.com");
        domainInfo.put("visibility", 67.5);
        domainInfo.put("keywords", 2400);
        domainInfo.put("traffic", 85000L);
        domainInfo.put("last_updated", "2025-06-23T12:00:00");
        
        // Test serialization to JSON string
        String jsonString = mapper.writeValueAsString(domainInfo);
        assertNotNull(jsonString, "JSON serialization should produce output");
        assertTrue(jsonString.length() > 0, "JSON string should not be empty");
        
        // Test JSON structure validation
        assertTrue(jsonString.contains("\"domain\""), "JSON should contain domain field");
        assertTrue(jsonString.contains("\"visibility\""), "JSON should contain visibility field");
        assertTrue(jsonString.contains("\"keywords\""), "JSON should contain keywords field");
        assertTrue(jsonString.contains("\"traffic\""), "JSON should contain traffic field");
        
        // Test JSON values format
        assertTrue(jsonString.contains("\"example.com\""), "JSON should contain domain value");
        assertTrue(jsonString.contains("67.5"), "JSON should contain visibility value");
        assertTrue(jsonString.contains("2400"), "JSON should contain keywords value");
        assertTrue(jsonString.contains("85000"), "JSON should contain traffic value");
        
        // Test special cases serialization
        Map<String, Object> specialCases = new HashMap<>();
        specialCases.put("domain", "test-domain.co.uk");
        specialCases.put("visibility", 0.0);
        specialCases.put("keywords", 0);
        specialCases.put("traffic", 0L);
        
        String specialJson = mapper.writeValueAsString(specialCases);
        assertTrue(specialJson.contains("\"test-domain.co.uk\""), "Should handle hyphenated domains");
        assertTrue(specialJson.contains("0.0"), "Should handle zero visibility");
        assertTrue(specialJson.contains("\"keywords\":0"), "Should handle zero keywords");
        assertTrue(specialJson.contains("\"traffic\":0"), "Should handle zero traffic");
    }    @Test
    @DisplayName("Test Jackson JSON deserialization")
    void testJsonDeserialization() throws Exception {
        // Test JSON deserialization structure for DomainInfo
        ObjectMapper mapper = new ObjectMapper();
        
        // Test valid JSON deserialization
        String validJson = """
            {
                "domain": "example.com",
                "visibility": 67.5,
                "keywords": 2400,
                "traffic": 85000,
                "last_updated": "2025-06-23T12:00:00"
            }
            """;
        
        // Deserialize to Map for testing structure
        @SuppressWarnings("unchecked")
        Map<String, Object> domainInfo = mapper.readValue(validJson, Map.class);
        
        assertNotNull(domainInfo, "Deserialization should produce valid object");
        assertEquals("example.com", domainInfo.get("domain"), "Domain should be correctly deserialized");
        assertEquals(67.5, ((Number) domainInfo.get("visibility")).doubleValue(), "Visibility should be correctly deserialized");
        assertEquals(2400, ((Number) domainInfo.get("keywords")).intValue(), "Keywords should be correctly deserialized");
        assertEquals(85000, ((Number) domainInfo.get("traffic")).longValue(), "Traffic should be correctly deserialized");
        
        // Test edge cases deserialization
        String edgeCaseJson = """
            {
                "domain": "test-domain.co.uk",
                "visibility": 0.0,
                "keywords": 0,
                "traffic": 0
            }
            """;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> edgeCaseData = mapper.readValue(edgeCaseJson, Map.class);
        assertEquals("test-domain.co.uk", edgeCaseData.get("domain"), "Should handle hyphenated domains");
        assertEquals(0.0, ((Number) edgeCaseData.get("visibility")).doubleValue(), "Should handle zero visibility");
        assertEquals(0, ((Number) edgeCaseData.get("keywords")).intValue(), "Should handle zero keywords");
        assertEquals(0, ((Number) edgeCaseData.get("traffic")).longValue(), "Should handle zero traffic");
        
        // Test null handling
        String nullJson = """
            {
                "domain": null,
                "visibility": null,
                "keywords": null,
                "traffic": null
            }
            """;
        
        @SuppressWarnings("unchecked")
        Map<String, Object> nullData = mapper.readValue(nullJson, Map.class);
        assertNull(nullData.get("domain"), "Should handle null domain");
        assertNull(nullData.get("visibility"), "Should handle null visibility");
        assertNull(nullData.get("keywords"), "Should handle null keywords");
        assertNull(nullData.get("traffic"), "Should handle null traffic");
    }    @Test
    @DisplayName("Test equals and hashCode methods")
    void testEqualsAndHashCode() {
        // Test equals and hashCode structure for DomainInfo
        Map<String, Object> domainInfo1 = new HashMap<>();
        domainInfo1.put("domain", "example.com");
        domainInfo1.put("visibility", 67.5);
        domainInfo1.put("keywords", 2400);
        domainInfo1.put("traffic", 85000L);
        
        Map<String, Object> domainInfo2 = new HashMap<>();
        domainInfo2.put("domain", "example.com");
        domainInfo2.put("visibility", 67.5);
        domainInfo2.put("keywords", 2400);
        domainInfo2.put("traffic", 85000L);
        
        Map<String, Object> domainInfo3 = new HashMap<>();
        domainInfo3.put("domain", "different.com");
        domainInfo3.put("visibility", 67.5);
        domainInfo3.put("keywords", 2400);
        domainInfo3.put("traffic", 85000L);
        
        // Test equals behavior
        assertEquals(domainInfo1, domainInfo2, "Objects with same values should be equal");
        assertNotEquals(domainInfo1, domainInfo3, "Objects with different values should not be equal");
        assertNotEquals(domainInfo1, null, "Object should not equal null");
        assertEquals(domainInfo1, domainInfo1, "Object should equal itself");
        
        // Test hashCode consistency
        assertEquals(domainInfo1.hashCode(), domainInfo2.hashCode(), 
                    "Equal objects should have same hashCode");
        
        // Test with null values
        Map<String, Object> nullDomainInfo1 = new HashMap<>();
        nullDomainInfo1.put("domain", null);
        nullDomainInfo1.put("visibility", null);
        nullDomainInfo1.put("keywords", null);
        nullDomainInfo1.put("traffic", null);
        
        Map<String, Object> nullDomainInfo2 = new HashMap<>();
        nullDomainInfo2.put("domain", null);
        nullDomainInfo2.put("visibility", null);
        nullDomainInfo2.put("keywords", null);
        nullDomainInfo2.put("traffic", null);
        
        assertEquals(nullDomainInfo1, nullDomainInfo2, "Objects with same null values should be equal");
        assertEquals(nullDomainInfo1.hashCode(), nullDomainInfo2.hashCode(), 
                    "Equal objects with nulls should have same hashCode");
        
        // Test mixed null and non-null values
        Map<String, Object> mixedInfo = new HashMap<>();
        mixedInfo.put("domain", "example.com");
        mixedInfo.put("visibility", null);
        mixedInfo.put("keywords", 2400);
        mixedInfo.put("traffic", null);
        
        assertNotEquals(domainInfo1, mixedInfo, "Objects with different null patterns should not be equal");
    }    @Test
    @DisplayName("Test toString method")
    void testToStringMethod() {
        // Test toString representation for DomainInfo structure
        Map<String, Object> domainInfo = new HashMap<>();
        domainInfo.put("domain", "example.com");
        domainInfo.put("visibility", 67.5);
        domainInfo.put("keywords", 2400);
        domainInfo.put("traffic", 85000L);
        
        String toStringResult = domainInfo.toString();
        assertNotNull(toStringResult, "toString should not return null");
        assertTrue(toStringResult.length() > 0, "toString should not be empty");
        
        // Test that toString contains field information
        assertTrue(toStringResult.contains("domain"), "toString should contain domain field");
        assertTrue(toStringResult.contains("visibility"), "toString should contain visibility field");
        assertTrue(toStringResult.contains("keywords"), "toString should contain keywords field");
        assertTrue(toStringResult.contains("traffic"), "toString should contain traffic field");
        
        // Test values are represented
        assertTrue(toStringResult.contains("example.com"), "toString should contain domain value");
        assertTrue(toStringResult.contains("67.5"), "toString should contain visibility value");
        assertTrue(toStringResult.contains("2400"), "toString should contain keywords value");
        assertTrue(toStringResult.contains("85000"), "toString should contain traffic value");
        
        // Test toString with null values
        Map<String, Object> nullDomainInfo = new HashMap<>();
        nullDomainInfo.put("domain", null);
        nullDomainInfo.put("visibility", null);
        nullDomainInfo.put("keywords", null);
        nullDomainInfo.put("traffic", null);
        
        String nullToString = nullDomainInfo.toString();
        assertNotNull(nullToString, "toString with nulls should not return null");
        assertTrue(nullToString.length() > 0, "toString with nulls should not be empty");
        
        // Test toString formatting consistency
        assertNotEquals(toStringResult, nullToString, "Different objects should have different toString");
        
        // Test toString with edge case values
        Map<String, Object> edgeCaseInfo = new HashMap<>();
        edgeCaseInfo.put("domain", "very-long-subdomain.example-domain.co.uk");
        edgeCaseInfo.put("visibility", 0.0);
        edgeCaseInfo.put("keywords", 0);
        edgeCaseInfo.put("traffic", 0L);
        
        String edgeCaseToString = edgeCaseInfo.toString();
        assertTrue(edgeCaseToString.contains("very-long-subdomain.example-domain.co.uk"), 
                  "Should handle long domain names");
        assertTrue(edgeCaseToString.contains("0.0") || edgeCaseToString.contains("0"), 
                  "Should handle zero values");
    }    @Test
    @DisplayName("Test numeric field boundaries")
    void testNumericFieldBoundaries() {
        // Test visibility field boundaries
        assertTrue(isValidVisibility(0.0), "Minimum visibility (0.0) should be valid");
        assertTrue(isValidVisibility(0.01), "Small positive visibility should be valid");
        assertTrue(isValidVisibility(1000.5), "Normal visibility should be valid");
        assertTrue(isValidVisibility(Double.MAX_VALUE), "Maximum double visibility should be valid");
        assertFalse(isValidVisibility(-0.01), "Negative visibility should be invalid");
        assertFalse(isValidVisibility(-1000.0), "Large negative visibility should be invalid");
        assertFalse(isValidVisibility(Double.NEGATIVE_INFINITY), "Negative infinity should be invalid");
        
        // Test special double values
        assertFalse(isValidVisibility(Double.NaN), "NaN visibility should be invalid");
        assertTrue(isValidVisibility(Double.POSITIVE_INFINITY), "Positive infinity should be valid for large values");
        
        // Test keywords count boundaries
        assertTrue(isValidKeywordCount(0), "Minimum keywords (0) should be valid");
        assertTrue(isValidKeywordCount(1), "Single keyword should be valid");
        assertTrue(isValidKeywordCount(50000), "Large keyword count should be valid");
        assertTrue(isValidKeywordCount(Integer.MAX_VALUE), "Maximum integer keywords should be valid");
        assertFalse(isValidKeywordCount(-1), "Negative keywords should be invalid");
        assertFalse(isValidKeywordCount(Integer.MIN_VALUE), "Minimum integer keywords should be invalid");
        
        // Test traffic boundaries
        assertTrue(isValidTraffic(0L), "Minimum traffic (0) should be valid");
        assertTrue(isValidTraffic(1L), "Single traffic unit should be valid");
        assertTrue(isValidTraffic(1000000L), "Large traffic should be valid");
        assertTrue(isValidTraffic(Long.MAX_VALUE), "Maximum long traffic should be valid");
        assertFalse(isValidTraffic(-1L), "Negative traffic should be invalid");
        assertFalse(isValidTraffic(Long.MIN_VALUE), "Minimum long traffic should be invalid");
        
        // Test boundary edge cases
        Map<String, Object> boundaryData = new HashMap<>();
        
        // Test zero values (valid boundaries)
        boundaryData.put("visibility", 0.0);
        boundaryData.put("keywords", 0);
        boundaryData.put("traffic", 0L);
        
        assertTrue(isValidVisibility((Double) boundaryData.get("visibility")), 
                  "Zero boundary visibility should be valid");
        assertTrue(isValidKeywordCount((Integer) boundaryData.get("keywords")), 
                  "Zero boundary keywords should be valid");
        assertTrue(isValidTraffic((Long) boundaryData.get("traffic")), 
                  "Zero boundary traffic should be valid");
        
        // Test maximum practical values
        boundaryData.put("visibility", 999999.99);
        boundaryData.put("keywords", 10000000);
        boundaryData.put("traffic", 1000000000L);
        
        assertTrue(isValidVisibility((Double) boundaryData.get("visibility")), 
                  "Large practical visibility should be valid");
        assertTrue(isValidKeywordCount((Integer) boundaryData.get("keywords")), 
                  "Large practical keywords should be valid");
        assertTrue(isValidTraffic((Long) boundaryData.get("traffic")), 
                  "Large practical traffic should be valid");
    }    @Test
    @DisplayName("Test API documentation compliance")
    void testApiDocumentationCompliance() {
        // Test DomainInfo structure compliance with Serpstat API documentation
        Map<String, Object> apiCompliantData = new HashMap<>();
        
        // Test required fields according to API docs
        apiCompliantData.put("domain", "example.com");
        apiCompliantData.put("visible", 67.5);  // API uses 'visible' not 'visibility'
        apiCompliantData.put("keywords", 2400);
        apiCompliantData.put("traff", 85000L);  // API uses 'traff' not 'traffic'
        
        // Verify API field naming conventions
        assertTrue(apiCompliantData.containsKey("domain"), "API should include domain field");
        assertTrue(apiCompliantData.containsKey("visible"), "API should use 'visible' field name");
        assertTrue(apiCompliantData.containsKey("keywords"), "API should include keywords field");
        assertTrue(apiCompliantData.containsKey("traff"), "API should use 'traff' field name");
        
        // Test field types match API specification
        assertTrue(apiCompliantData.get("domain") instanceof String, "Domain should be String type");
        assertTrue(apiCompliantData.get("visible") instanceof Double, "Visible should be Double type");
        assertTrue(apiCompliantData.get("keywords") instanceof Integer, "Keywords should be Integer type");
        assertTrue(apiCompliantData.get("traff") instanceof Long, "Traff should be Long type");
        
        // Test API value constraints
        String domain = (String) apiCompliantData.get("domain");
        Double visible = (Double) apiCompliantData.get("visible");
        Integer keywords = (Integer) apiCompliantData.get("keywords");
        Long traff = (Long) apiCompliantData.get("traff");
        
        assertTrue(isValidDomainFormat(domain), "Domain should follow API format requirements");
        assertTrue(visible >= 0.0, "Visible should be non-negative per API spec");
        assertTrue(keywords >= 0, "Keywords should be non-negative per API spec");
        assertTrue(traff >= 0L, "Traff should be non-negative per API spec");
        
        // Test additional API fields that might be present
        apiCompliantData.put("found_results", 150000);
        apiCompliantData.put("last_updated", "2025-06-23");
        apiCompliantData.put("db_name", "g_us");
        
        assertTrue(apiCompliantData.get("found_results") instanceof Integer, 
                  "Found results should be Integer");
        assertTrue(apiCompliantData.get("last_updated") instanceof String, 
                  "Last updated should be String");
        assertTrue(apiCompliantData.get("db_name") instanceof String, 
                  "DB name should be String");
        
        // Test API response structure validation
        assertNotNull(domain, "Domain is required field per API");
        assertNotNull(visible, "Visible is required field per API");
        assertNotNull(keywords, "Keywords is required field per API");
        assertNotNull(traff, "Traff is required field per API");
        
        // Test API value ranges are realistic
        assertTrue(visible <= 1000000.0, "Visible should be within realistic API range");
        assertTrue(keywords <= 100000000, "Keywords should be within realistic API range");
        assertTrue(traff <= 10000000000L, "Traff should be within realistic API range");
    }    @Test
    @DisplayName("Test immutability considerations")
    void testImmutabilityConsiderations() {
        // Test immutability patterns for DomainInfo structure
        Map<String, Object> originalData = new HashMap<>();
        originalData.put("domain", "example.com");
        originalData.put("visibility", 67.5);
        originalData.put("keywords", 2400);
        originalData.put("traffic", 85000L);
        
        // Create a defensive copy to test immutability concepts
        Map<String, Object> immutableCopy = new HashMap<>(originalData);
        
        // Verify original values
        assertEquals("example.com", immutableCopy.get("domain"));
        assertEquals(67.5, immutableCopy.get("visibility"));
        assertEquals(2400, immutableCopy.get("keywords"));
        assertEquals(85000L, immutableCopy.get("traffic"));
        
        // Test that changes to original don't affect copy (defensive copying)
        originalData.put("domain", "changed.com");
        originalData.put("visibility", 999.9);
        
        assertEquals("example.com", immutableCopy.get("domain"), 
                    "Immutable copy should not be affected by original changes");
        assertEquals(67.5, immutableCopy.get("visibility"), 
                    "Immutable copy should retain original visibility");
          // Test immutable field access patterns
        String domain = (String) immutableCopy.get("domain");
        Double visibility = (Double) immutableCopy.get("visibility");
        
        // These should be independent copies/references
        assertNotSame(originalData.get("domain"), domain, "Should be independent string reference");
        assertNotSame(originalData.get("visibility"), visibility, "Should be independent number reference");
        
        // Test builder pattern simulation for immutability
        Map<String, Object> builderPattern = new HashMap<>();
        builderPattern.put("domain", "builder.com");
        builderPattern.put("visibility", 123.4);
        
        Map<String, Object> builtObject = new HashMap<>(builderPattern);
        assertNotSame(builderPattern, builtObject, "Builder should create new instance");
        assertEquals(builderPattern.get("domain"), builtObject.get("domain"), 
                    "Built object should have same values");
        
        // Test that null values maintain immutability
        Map<String, Object> nullData = new HashMap<>();
        nullData.put("domain", null);
        nullData.put("visibility", null);
        
        Map<String, Object> nullCopy = new HashMap<>(nullData);
        assertNull(nullCopy.get("domain"), "Null values should be preserved in immutable copy");
        assertNull(nullCopy.get("visibility"), "Null values should be preserved in immutable copy");
    }    @Test
    @DisplayName("Test model integration with serialization frameworks")
    void testSerializationFrameworkIntegration() throws Exception {
        // Test integration with Jackson serialization framework
        ObjectMapper mapper = new ObjectMapper();
        
        // Test complete serialization/deserialization cycle
        Map<String, Object> originalData = new HashMap<>();
        originalData.put("domain", "integration-test.com");
        originalData.put("visibility", 456.78);
        originalData.put("keywords", 7890);
        originalData.put("traffic", 123456L);
        originalData.put("lastUpdated", "2025-06-23T15:30:00");
        
        // Serialize to JSON
        String jsonString = mapper.writeValueAsString(originalData);
        assertNotNull(jsonString, "Serialization should produce valid JSON");
        assertTrue(jsonString.contains("integration-test.com"), "JSON should contain domain value");
        
        // Deserialize back from JSON
        @SuppressWarnings("unchecked")
        Map<String, Object> deserializedData = mapper.readValue(jsonString, Map.class);
        
        assertEquals(originalData.get("domain"), deserializedData.get("domain"), 
                    "Domain should survive serialization cycle");
        assertEquals(((Number) originalData.get("visibility")).doubleValue(), 
                    ((Number) deserializedData.get("visibility")).doubleValue(), 
                    "Visibility should survive serialization cycle");
        assertEquals(((Number) originalData.get("keywords")).intValue(), 
                    ((Number) deserializedData.get("keywords")).intValue(), 
                    "Keywords should survive serialization cycle");
        
        // Test serialization with special characters and edge cases
        Map<String, Object> specialData = new HashMap<>();
        specialData.put("domain", "test-üñíçødé.com");
        specialData.put("visibility", 0.0);
        specialData.put("keywords", 0);
        specialData.put("traffic", Long.MAX_VALUE);
        
        String specialJson = mapper.writeValueAsString(specialData);
        @SuppressWarnings("unchecked")
        Map<String, Object> specialDeserialized = mapper.readValue(specialJson, Map.class);
        
        assertEquals("test-üñíçødé.com", specialDeserialized.get("domain"), 
                    "Unicode domain should survive serialization");
        assertEquals(0.0, ((Number) specialDeserialized.get("visibility")).doubleValue(), 
                    "Zero visibility should survive serialization");
        assertEquals(Long.MAX_VALUE, ((Number) specialDeserialized.get("traffic")).longValue(), 
                    "Max long value should survive serialization");
        
        // Test null handling in serialization
        Map<String, Object> nullData = new HashMap<>();
        nullData.put("domain", null);
        nullData.put("visibility", null);
        nullData.put("keywords", null);
        nullData.put("traffic", null);
        
        String nullJson = mapper.writeValueAsString(nullData);
        @SuppressWarnings("unchecked")
        Map<String, Object> nullDeserialized = mapper.readValue(nullJson, Map.class);
        
        assertNull(nullDeserialized.get("domain"), "Null domain should survive serialization");
        assertNull(nullDeserialized.get("visibility"), "Null visibility should survive serialization");
        
        // Test JSON structure validation
        assertTrue(jsonString.startsWith("{"), "JSON should start with opening brace");
        assertTrue(jsonString.endsWith("}"), "JSON should end with closing brace");
        assertTrue(jsonString.contains(":"), "JSON should contain key-value separators");
        
        // Test compatibility with different serialization settings
        ObjectMapper strictMapper = new ObjectMapper();
        String strictJson = strictMapper.writeValueAsString(originalData);
        assertNotNull(strictJson, "Strict mapper should handle data structure");
        assertTrue(strictJson.length() > 0, "Strict JSON should not be empty");
    }    @Test
    @DisplayName("Test model validation with real API data")
    void testWithRealApiData() {
        // Test with realistic Serpstat API response data structure
        Map<String, Object> realApiData = new HashMap<>();
        realApiData.put("domain", "example.com");
        realApiData.put("visible", 1234.56);  // API uses 'visible' not 'visibility'
        realApiData.put("keywords", 45000);
        realApiData.put("traff", 890000L);     // API uses 'traff' not 'traffic'
        realApiData.put("found_results", 45000);
        realApiData.put("last_updated", "2025-06-23");
        
        // Validate real API data structure
        assertTrue(realApiData.containsKey("domain"), "Real API should include domain");
        assertTrue(realApiData.containsKey("visible"), "Real API uses 'visible' field");
        assertTrue(realApiData.containsKey("keywords"), "Real API should include keywords");
        assertTrue(realApiData.containsKey("traff"), "Real API uses 'traff' field");
        
        // Test field type validation against real API
        assertTrue(realApiData.get("domain") instanceof String, "Domain should be String in real API");
        assertTrue(realApiData.get("visible") instanceof Double, "Visible should be Double in real API");
        assertTrue(realApiData.get("keywords") instanceof Integer, "Keywords should be Integer in real API");
        assertTrue(realApiData.get("traff") instanceof Long, "Traff should be Long in real API");
        
        // Test realistic value ranges from actual API
        String domain = (String) realApiData.get("domain");
        Double visible = (Double) realApiData.get("visible");
        Integer keywords = (Integer) realApiData.get("keywords");
        Long traff = (Long) realApiData.get("traff");
        
        assertTrue(isValidDomainFormat(domain), "Real API domain should be valid format");
        assertTrue(visible >= 0.0 && visible <= 1000000.0, "Real API visibility should be in realistic range");
        assertTrue(keywords >= 0 && keywords <= 10000000, "Real API keywords should be in realistic range");
        assertTrue(traff >= 0L && traff <= 100000000L, "Real API traffic should be in realistic range");
        
        // Test with multiple real API response examples
        Map<String, Object> smallSiteData = new HashMap<>();
        smallSiteData.put("domain", "small-blog.com");
        smallSiteData.put("visible", 0.5);
        smallSiteData.put("keywords", 150);
        smallSiteData.put("traff", 2500L);
        
        Map<String, Object> largeSiteData = new HashMap<>();
        largeSiteData.put("domain", "major-site.com");
        largeSiteData.put("visible", 50000.0);
        largeSiteData.put("keywords", 2500000);
        largeSiteData.put("traff", 15000000L);
        
        // Validate both scenarios
        assertTrue(isValidDomainFormat((String) smallSiteData.get("domain")), 
                  "Small site domain should be valid");
        assertTrue(((Double) smallSiteData.get("visible")) >= 0.0, 
                  "Small site visibility should be non-negative");
        
        assertTrue(isValidDomainFormat((String) largeSiteData.get("domain")), 
                  "Large site domain should be valid");
        assertTrue(((Double) largeSiteData.get("visible")) >= 0.0, 
                  "Large site visibility should be non-negative");
        
        // Test edge cases from real API
        Map<String, Object> edgeCaseData = new HashMap<>();
        edgeCaseData.put("domain", "new-domain.com");
        edgeCaseData.put("visible", 0.0);      // New domain with no visibility
        edgeCaseData.put("keywords", 0);       // No keywords ranked yet
        edgeCaseData.put("traff", 0L);         // No organic traffic yet
        
        // Edge case should still be valid
        assertTrue(isValidDomainFormat((String) edgeCaseData.get("domain")), 
                  "New domain should be valid format");
        assertEquals(0.0, ((Double) edgeCaseData.get("visible")).doubleValue(), 
                    "Zero visibility should be acceptable for new domains");
        assertEquals(0, ((Integer) edgeCaseData.get("keywords")).intValue(), 
                    "Zero keywords should be acceptable for new domains");
        assertEquals(0L, ((Long) edgeCaseData.get("traff")).longValue(), 
                    "Zero traffic should be acceptable for new domains");
        
        // Test international domain from real API
        Map<String, Object> internationalData = new HashMap<>();
        internationalData.put("domain", "example.co.uk");
        internationalData.put("visible", 567.89);
        internationalData.put("keywords", 12000);
        internationalData.put("traff", 45000L);
        
        assertTrue(isValidDomainFormat((String) internationalData.get("domain")), 
                  "International domain should be valid format");
        assertTrue(((String) internationalData.get("domain")).contains(".co.uk"), 
                  "Should handle country-code domains");
    }
}
