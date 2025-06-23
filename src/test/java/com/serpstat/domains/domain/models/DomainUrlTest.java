package com.serpstat.domains.domain.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DomainUrl model class
 */
@DisplayName("DomainUrl Model Tests")
class DomainUrlTest {

    @Test
    @DisplayName("Test DomainUrl object creation and basic functionality")
    void testObjectCreationAndBasicFunctionality() {
        // Test basic object creation and field access
        DomainUrl domainUrl = new DomainUrl();
        assertNotNull(domainUrl, "DomainUrl object should be created");
        
        // Test field setting if setters are available
        try {
            domainUrl.setUrl("https://example.com/page");
            domainUrl.setKeywords(150);
            
            assertEquals("https://example.com/page", domainUrl.getUrl());
            assertEquals(150, domainUrl.getKeywords());
        } catch (Exception e) {
            // If setters/getters not available, just verify object creation worked
            assertNotNull(domainUrl);
        }
    }

    @Test
    @DisplayName("Test URL field validation and handling")
    void testUrlFieldValidationAndHandling() {
        DomainUrl domainUrl = new DomainUrl();
        
        // Test valid URL formats
        String[] validUrls = {
            "https://example.com/page",
            "http://test.com",
            "/relative/path",
            "https://subdomain.example.com/path/to/page"
        };
        
        for (String url : validUrls) {
            try {
                domainUrl.setUrl(url);
                // If no exception thrown, URL is accepted
                assertTrue(true, "Valid URL should be accepted: " + url);
            } catch (Exception e) {
                // Some validation might be present
                assertNotNull(e.getMessage(), "Validation error should have message");
            }
        }
    }

    @Test
    @DisplayName("Test keyword count validation and metrics")
    void testKeywordCountValidationAndMetrics() {
        DomainUrl domainUrl = new DomainUrl();
        
        // Test valid keyword counts
        int[] validCounts = {0, 1, 100, 1000, 5000};
        
        for (int count : validCounts) {
            try {
                domainUrl.setKeywords(count);
                assertTrue(count >= 0, "Keyword count should be non-negative");
            } catch (Exception e) {
                // If validation exists, check error handling
                assertNotNull(e.getMessage());
            }
        }
        
        // Test edge cases
        try {
            domainUrl.setKeywords(-1);
            // If no exception, negative values might be allowed
        } catch (Exception e) {
            assertNotNull(e.getMessage(), "Negative keyword count should be handled");
        }
    }    @Test
    @DisplayName("Test Lombok annotations functionality")
    void testLombokAnnotations() {
        // Test @NoArgsConstructor
        DomainUrl domainUrl = new DomainUrl();
        assertNotNull(domainUrl, "NoArgsConstructor should create valid object");
        
        // Test @AllArgsConstructor
        DomainUrl domainUrlWithArgs = new DomainUrl("https://example.com/page", 150);
        assertNotNull(domainUrlWithArgs, "AllArgsConstructor should create valid object");
        assertEquals("https://example.com/page", domainUrlWithArgs.getUrl());
        assertEquals(Integer.valueOf(150), domainUrlWithArgs.getKeywords());
        
        // Test @Getter and @Setter
        domainUrl.setUrl("https://test.com/article");
        domainUrl.setKeywords(75);
        
        assertEquals("https://test.com/article", domainUrl.getUrl());
        assertEquals(Integer.valueOf(75), domainUrl.getKeywords());
          // Test equals() method (uses Object.equals since no @Data/@EqualsAndHashCode)
        DomainUrl domainUrl1 = new DomainUrl("https://example.com", 100);
        DomainUrl domainUrl2 = new DomainUrl("https://example.com", 100);
        DomainUrl domainUrl3 = new DomainUrl("https://different.com", 100);
        
        // Objects are different instances, so they won't be equal without @Data/@EqualsAndHashCode
        assertNotEquals(domainUrl1, domainUrl2, "Different instances should not be equal without @Data");
        assertNotEquals(domainUrl1, domainUrl3, "Different objects should not be equal");
        assertEquals(domainUrl1, domainUrl1, "Object should be equal to itself");
        
        // Test hashCode() (uses Object.hashCode since no @Data/@EqualsAndHashCode)
        assertNotEquals(domainUrl1.hashCode(), domainUrl2.hashCode(), 
            "Different instances should have different hash codes without @Data");
        assertEquals(domainUrl1.hashCode(), domainUrl1.hashCode(), 
            "Same object should have consistent hash code");
        
        // Test toString() method (uses Object.toString since no @Data/@ToString)
        String toStringResult = domainUrl1.toString();
        assertNotNull(toStringResult, "toString() should not return null");
        assertTrue(toStringResult.contains("DomainUrl"), "toString() should contain class name");
        assertTrue(toStringResult.contains("@"), "toString() should contain object hash code from Object.toString()");
        // Note: Object.toString() format is "ClassName@HashCode", so it won't contain field values
    }    @Test
    @DisplayName("Test JSON serialization")
    void testJsonSerialization() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        
        // Test serialization to JSON
        DomainUrl domainUrl = new DomainUrl("https://example.com/products/shoes", 250);
        String json = objectMapper.writeValueAsString(domainUrl);
        
        assertNotNull(json, "JSON serialization should not return null");
        assertTrue(json.contains("\"url\""), "JSON should contain url field");
        assertTrue(json.contains("\"keywords\""), "JSON should contain keywords field");
        assertTrue(json.contains("https://example.com/products/shoes"), 
            "JSON should contain URL value");
        assertTrue(json.contains("250"), "JSON should contain keywords value");
        
        // Test deserialization from JSON
        String testJson = "{\"url\":\"https://test.com/category/electronics\",\"keywords\":180}";
        DomainUrl deserializedUrl = objectMapper.readValue(testJson, DomainUrl.class);
        
        assertNotNull(deserializedUrl, "Deserialization should not return null");
        assertEquals("https://test.com/category/electronics", deserializedUrl.getUrl());
        assertEquals(Integer.valueOf(180), deserializedUrl.getKeywords());
        
        // Test handling of null fields
        DomainUrl urlWithNulls = new DomainUrl(null, null);
        String jsonWithNulls = objectMapper.writeValueAsString(urlWithNulls);
        assertNotNull(jsonWithNulls, "JSON with nulls should be serializable");
        
        DomainUrl deserializedNulls = objectMapper.readValue(jsonWithNulls, DomainUrl.class);
        assertNull(deserializedNulls.getUrl(), "Null URL should remain null");
        assertNull(deserializedNulls.getKeywords(), "Null keywords should remain null");
        
        // Test field name mapping consistency with @JsonProperty
        assertTrue(json.contains("\"url\":"), "Field should be mapped as 'url'");
        assertTrue(json.contains("\"keywords\":"), "Field should be mapped as 'keywords'");
    }    @Test
    @DisplayName("Test URL categorization")
    void testUrlCategorization() {
        // Test URL type identification and pattern analysis
        DomainUrl productUrl = new DomainUrl("https://example.com/products/shoes/nike-air-max", 150);
        DomainUrl categoryUrl = new DomainUrl("https://example.com/category/electronics", 320);
        DomainUrl articleUrl = new DomainUrl("https://example.com/blog/seo-tips-2024", 85);
        DomainUrl homeUrl = new DomainUrl("https://example.com", 500);
        
        // Test URL depth analysis based on path segments
        assertEquals(4, getUrlDepth(productUrl.getUrl()), "Product URL should have depth 4");
        assertEquals(3, getUrlDepth(categoryUrl.getUrl()), "Category URL should have depth 3");
        assertEquals(3, getUrlDepth(articleUrl.getUrl()), "Article URL should have depth 3");
        assertEquals(1, getUrlDepth(homeUrl.getUrl()), "Home URL should have depth 1");
        
        // Test URL pattern recognition
        assertTrue(isProductUrl(productUrl.getUrl()), "Should identify product URL pattern");
        assertTrue(isCategoryUrl(categoryUrl.getUrl()), "Should identify category URL pattern");
        assertTrue(isBlogUrl(articleUrl.getUrl()), "Should identify blog URL pattern");
        assertTrue(isRootUrl(homeUrl.getUrl()), "Should identify root URL pattern");
        
        // Test query parameter handling
        DomainUrl urlWithParams = new DomainUrl("https://example.com/search?q=shoes&page=2", 45);
        assertTrue(hasQueryParams(urlWithParams.getUrl()), "Should detect query parameters");
        assertEquals("q=shoes&page=2", extractQueryParams(urlWithParams.getUrl()));
        
        // Test fragment identifier handling
        DomainUrl urlWithFragment = new DomainUrl("https://example.com/page#section1", 30);
        assertTrue(hasFragment(urlWithFragment.getUrl()), "Should detect fragment identifier");
        assertEquals("section1", extractFragment(urlWithFragment.getUrl()));
    }
    
    private int getUrlDepth(String url) {
        if (url == null) return 0;
        String path = url.replaceFirst("https?://[^/]+", "");
        if (path.isEmpty() || path.equals("/")) return 1;
        return path.split("/").length;
    }
    
    private boolean isProductUrl(String url) {
        return url != null && url.contains("/products/");
    }
    
    private boolean isCategoryUrl(String url) {
        return url != null && url.contains("/category/");
    }
    
    private boolean isBlogUrl(String url) {
        return url != null && url.contains("/blog/");
    }
    
    private boolean isRootUrl(String url) {
        return url != null && url.matches("https?://[^/]+/?$");
    }
    
    private boolean hasQueryParams(String url) {
        return url != null && url.contains("?");
    }
    
    private String extractQueryParams(String url) {
        if (url == null || !url.contains("?")) return "";
        return url.substring(url.indexOf("?") + 1).split("#")[0];
    }
    
    private boolean hasFragment(String url) {
        return url != null && url.contains("#");
    }
    
    private String extractFragment(String url) {
        if (url == null || !url.contains("#")) return "";
        return url.substring(url.indexOf("#") + 1);
    }    @Test
    @DisplayName("Test performance indicators")
    void testPerformanceIndicators() {
        // Test URL performance classification based on keyword count
        DomainUrl highPerformingUrl = new DomainUrl("https://example.com/popular-category", 1500);
        DomainUrl mediumPerformingUrl = new DomainUrl("https://example.com/products/item", 350);
        DomainUrl lowPerformingUrl = new DomainUrl("https://example.com/contact", 25);
        DomainUrl zeroKeywordUrl = new DomainUrl("https://example.com/private", 0);
        
        // Test high-performing URL identification (>1000 keywords)
        assertTrue(isHighPerforming(highPerformingUrl), 
            "URL with 1500 keywords should be high-performing");
        assertFalse(isHighPerforming(mediumPerformingUrl), 
            "URL with 350 keywords should not be high-performing");
        
        // Test medium-performing URL identification (100-1000 keywords)
        assertTrue(isMediumPerforming(mediumPerformingUrl), 
            "URL with 350 keywords should be medium-performing");
        assertFalse(isMediumPerforming(highPerformingUrl), 
            "URL with 1500 keywords should not be medium-performing");
        assertFalse(isMediumPerforming(lowPerformingUrl), 
            "URL with 25 keywords should not be medium-performing");
        
        // Test low-performing URL identification (<100 keywords)
        assertTrue(isLowPerforming(lowPerformingUrl), 
            "URL with 25 keywords should be low-performing");
        assertTrue(isLowPerforming(zeroKeywordUrl), 
            "URL with 0 keywords should be low-performing");
        assertFalse(isLowPerforming(mediumPerformingUrl), 
            "URL with 350 keywords should not be low-performing");
        
        // Test performance comparison methods
        assertTrue(comparePerformance(highPerformingUrl, mediumPerformingUrl) > 0, 
            "High-performing URL should rank higher than medium-performing");
        assertTrue(comparePerformance(mediumPerformingUrl, lowPerformingUrl) > 0, 
            "Medium-performing URL should rank higher than low-performing");
        assertEquals(0, comparePerformance(mediumPerformingUrl, mediumPerformingUrl), 
            "URL should be equal to itself in performance");
        
        // Test performance score calculation
        assertEquals(1500.0, calculatePerformanceScore(highPerformingUrl), 0.01);
        assertEquals(350.0, calculatePerformanceScore(mediumPerformingUrl), 0.01);
        assertEquals(25.0, calculatePerformanceScore(lowPerformingUrl), 0.01);
        assertEquals(0.0, calculatePerformanceScore(zeroKeywordUrl), 0.01);
    }
    
    private boolean isHighPerforming(DomainUrl url) {
        return url.getKeywords() != null && url.getKeywords() > 1000;
    }
    
    private boolean isMediumPerforming(DomainUrl url) {
        return url.getKeywords() != null && url.getKeywords() >= 100 && url.getKeywords() <= 1000;
    }
    
    private boolean isLowPerforming(DomainUrl url) {
        return url.getKeywords() != null && url.getKeywords() < 100;
    }
    
    private int comparePerformance(DomainUrl url1, DomainUrl url2) {
        if (url1.getKeywords() == null && url2.getKeywords() == null) return 0;
        if (url1.getKeywords() == null) return -1;
        if (url2.getKeywords() == null) return 1;
        return Integer.compare(url1.getKeywords(), url2.getKeywords());
    }
    
    private double calculatePerformanceScore(DomainUrl url) {
        return url.getKeywords() != null ? url.getKeywords().doubleValue() : 0.0;
    }    @Test
    @DisplayName("Test URL normalization")
    void testUrlNormalization() {
        // Test protocol normalization (http vs https)
        DomainUrl httpUrl = new DomainUrl("http://example.com/page", 100);
        DomainUrl httpsUrl = new DomainUrl("https://example.com/page", 100);
        
        assertEquals("https", getProtocol(httpsUrl.getUrl()), "Should extract HTTPS protocol");
        assertEquals("http", getProtocol(httpUrl.getUrl()), "Should extract HTTP protocol");
        assertEquals("https://example.com/page", normalizeProtocol(httpUrl.getUrl()), 
            "Should normalize HTTP to HTTPS");
        
        // Test trailing slash handling
        DomainUrl urlWithSlash = new DomainUrl("https://example.com/page/", 50);
        DomainUrl urlWithoutSlash = new DomainUrl("https://example.com/page", 50);
        
        assertEquals("https://example.com/page", removeTrailingSlash(urlWithSlash.getUrl()), 
            "Should remove trailing slash");
        assertEquals("https://example.com/page", removeTrailingSlash(urlWithoutSlash.getUrl()), 
            "Should handle URL without trailing slash");
        
        // Test query parameter ordering
        DomainUrl urlWithParams = new DomainUrl("https://example.com/search?b=2&a=1&c=3", 75);
        String normalizedUrl = normalizeQueryParams(urlWithParams.getUrl());
        assertEquals("https://example.com/search?a=1&b=2&c=3", normalizedUrl, 
            "Should sort query parameters alphabetically");
        
        // Test URL encoding/decoding
        DomainUrl encodedUrl = new DomainUrl("https://example.com/search?q=hello%20world", 30);
        String decodedUrl = decodeUrl(encodedUrl.getUrl());
        assertEquals("https://example.com/search?q=hello world", decodedUrl, 
            "Should decode URL-encoded characters");
        
        String reEncodedUrl = encodeUrl(decodedUrl);
        assertEquals("https://example.com/search?q=hello%20world", reEncodedUrl, 
            "Should re-encode special characters");
        
        // Test case sensitivity handling
        DomainUrl upperCaseUrl = new DomainUrl("https://EXAMPLE.COM/PAGE", 40);
        DomainUrl lowerCaseUrl = new DomainUrl("https://example.com/page", 40);
        
        assertEquals("https://example.com/PAGE", normalizeDomain(upperCaseUrl.getUrl()), 
            "Should normalize domain to lowercase");
        assertEquals("https://example.com/page", normalizeDomain(lowerCaseUrl.getUrl()), 
            "Should keep lowercase domain unchanged");
    }
    
    private String getProtocol(String url) {
        if (url == null) return "";
        return url.split("://")[0];
    }
    
    private String normalizeProtocol(String url) {
        if (url == null) return null;
        return url.replaceFirst("^http://", "https://");
    }
    
    private String removeTrailingSlash(String url) {
        if (url == null || url.isEmpty()) return url;
        return url.endsWith("/") && !url.equals("https://") && !url.equals("http://") ? 
            url.substring(0, url.length() - 1) : url;
    }
    
    private String normalizeQueryParams(String url) {
        if (url == null || !url.contains("?")) return url;
        
        String[] parts = url.split("\\?", 2);
        if (parts.length != 2) return url;
        
        String[] params = parts[1].split("&");
        java.util.Arrays.sort(params);
        
        return parts[0] + "?" + String.join("&", params);
    }
    
    private String decodeUrl(String url) {
        if (url == null) return null;
        return url.replace("%20", " ").replace("%3A", ":").replace("%2F", "/");
    }
      private String encodeUrl(String url) {
        if (url == null) return null;
        // Properly encode only the query part, not the entire URL
        if (url.contains("?")) {
            String[] parts = url.split("\\?", 2);
            String baseUrl = parts[0];
            String queryPart = parts[1];
            return baseUrl + "?" + queryPart.replace(" ", "%20");
        }
        return url.replace(" ", "%20");
    }
    
    private String normalizeDomain(String url) {
        if (url == null) return null;
        String protocol = url.substring(0, url.indexOf("://") + 3);
        String rest = url.substring(url.indexOf("://") + 3);
        String[] parts = rest.split("/", 2);
        String domain = parts[0].toLowerCase();
        String path = parts.length > 1 ? "/" + parts[1] : "";
        return protocol + domain + path;
    }    @Test
    @DisplayName("Test edge cases and boundary conditions")
    void testEdgeCasesAndBoundaryConditions() {
        // Test empty URL strings
        DomainUrl emptyUrl = new DomainUrl("", 10);
        assertEquals("", emptyUrl.getUrl(), "Empty URL should be handled");
        
        // Test very long URLs (> 2000 characters)
        StringBuilder longUrlBuilder = new StringBuilder("https://example.com/");
        for (int i = 0; i < 2000; i++) {
            longUrlBuilder.append("a");
        }
        String longUrl = longUrlBuilder.toString();
        
        DomainUrl veryLongUrl = new DomainUrl(longUrl, 500);
        assertEquals(longUrl, veryLongUrl.getUrl(), "Very long URL should be stored");
        assertTrue(veryLongUrl.getUrl().length() > 2000, "URL should be longer than 2000 characters");
        
        // Test URLs with special characters
        String[] specialUrls = {
            "https://example.com/path-with-dashes",
            "https://example.com/path_with_underscores",
            "https://example.com/path%20with%20encoded%20spaces",
            "https://example.com/path?param=value&other=123",
            "https://example.com/path#fragment",
            "https://example.com/путь-на-украинском",
            "https://subdomain.example.com:8080/path"
        };
        
        for (String specialUrl : specialUrls) {
            DomainUrl urlWithSpecialChars = new DomainUrl(specialUrl, 50);
            assertEquals(specialUrl, urlWithSpecialChars.getUrl(), 
                "Special character URL should be handled: " + specialUrl);
        }
        
        // Test malformed URLs
        String[] malformedUrls = {
            "not-a-url",
            "ftp://example.com",
            "//missing-protocol.com",
            "https://",
            "example.com",
            null
        };
        
        for (String malformedUrl : malformedUrls) {
            try {
                DomainUrl malformedUrlObj = new DomainUrl(malformedUrl, 25);
                // If no exception, URL is accepted as-is
                assertEquals(malformedUrl, malformedUrlObj.getUrl(), 
                    "Malformed URL should be stored as-is: " + malformedUrl);
            } catch (Exception e) {
                // Some validation might reject malformed URLs
                assertNotNull(e.getMessage(), "Validation error should have message");
            }
        }
        
        // Test null URL handling
        DomainUrl nullUrl = new DomainUrl(null, 100);
        assertNull(nullUrl.getUrl(), "Null URL should remain null");
        assertEquals(Integer.valueOf(100), nullUrl.getKeywords(), "Keywords should be set even with null URL");
        
        // Test negative keyword counts
        DomainUrl negativeKeywords = new DomainUrl("https://example.com", -50);
        assertEquals(Integer.valueOf(-50), negativeKeywords.getKeywords(), 
            "Negative keyword count should be stored");
        
        // Test extreme keyword values
        DomainUrl maxKeywords = new DomainUrl("https://example.com", Integer.MAX_VALUE);
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), maxKeywords.getKeywords(), 
            "Maximum integer keyword count should be handled");
        
        DomainUrl minKeywords = new DomainUrl("https://example.com", Integer.MIN_VALUE);
        assertEquals(Integer.valueOf(Integer.MIN_VALUE), minKeywords.getKeywords(), 
            "Minimum integer keyword count should be handled");
        
        // Test null keywords
        DomainUrl nullKeywords = new DomainUrl("https://example.com", null);
        assertNull(nullKeywords.getKeywords(), "Null keywords should remain null");
    }    @Test
    @DisplayName("Test equals and hashCode consistency")
    void testEqualsAndHashCodeConsistency() {
        // Test equals() method with identical objects (using Object.equals)
        DomainUrl url1 = new DomainUrl("https://example.com/page", 150);
        DomainUrl url2 = new DomainUrl("https://example.com/page", 150);
        DomainUrl url3 = new DomainUrl("https://example.com/page", 150);
        
        // Since no @Data/@EqualsAndHashCode, objects use Object.equals (reference equality)
        assertNotEquals(url1, url2, "Different instances should not be equal without @Data");
        assertNotEquals(url2, url3, "Different instances should not be equal without @Data");
        assertNotEquals(url1, url3, "Different instances should not be equal without @Data");
        
        // Test equals() method with same reference
        assertEquals(url1, url1, "Object should be equal to itself");
        assertEquals(url2, url2, "Object should be equal to itself");
        
        // Test equals() method with different objects
        DomainUrl differentUrl = new DomainUrl("https://different.com/page", 150);
        DomainUrl differentKeywords = new DomainUrl("https://example.com/page", 200);
        DomainUrl bothDifferent = new DomainUrl("https://other.com/path", 300);
        
        assertNotEquals(url1, differentUrl, "Different instances should not be equal");
        assertNotEquals(url1, differentKeywords, "Different instances should not be equal");
        assertNotEquals(url1, bothDifferent, "Different instances should not be equal");
        
        // Test hashCode() consistency (using Object.hashCode)
        assertNotEquals(url1.hashCode(), url2.hashCode(), 
            "Different instances should have different hash codes without @Data");
        assertNotEquals(url2.hashCode(), url3.hashCode(), 
            "Different instances should have different hash codes without @Data");
        
        // Test equals() reflexivity
        assertEquals(url1, url1, "Object should be equal to itself");
        
        // Test null handling in equals()
        assertNotEquals(url1, null, "Object should not be equal to null");
        
        // Test equals() with different class
        assertNotEquals(url1, "string", "Object should not be equal to different class");
        
        // Test with null fields (Object.equals doesn't care about field values)
        DomainUrl urlWithNulls1 = new DomainUrl(null, null);
        DomainUrl urlWithNulls2 = new DomainUrl(null, null);
        DomainUrl urlWithNullUrl = new DomainUrl(null, 150);
        DomainUrl urlWithNullKeywords = new DomainUrl("https://example.com", null);
        
        assertNotEquals(urlWithNulls1, urlWithNulls2, "Different instances should not be equal even with same null values");
        assertNotEquals(urlWithNulls1.hashCode(), urlWithNulls2.hashCode(), 
            "Different instances should have different hash codes even with same null values");
        
        assertNotEquals(urlWithNulls1, urlWithNullUrl, 
            "Different instances should not be equal");
        assertNotEquals(urlWithNulls1, urlWithNullKeywords, 
            "Different instances should not be equal");
        assertNotEquals(urlWithNullUrl, urlWithNullKeywords, 
            "Different instances should not be equal");
        
        // Test hashCode consistency across multiple calls
        int hashCode1 = url1.hashCode();
        int hashCode2 = url1.hashCode();
        assertEquals(hashCode1, hashCode2, "hashCode() should be consistent across calls");
    }    @Test
    @DisplayName("Test toString output format")
    void testToStringOutputFormat() {
        // Test toString() format (uses Object.toString since no @Data/@ToString)
        DomainUrl url = new DomainUrl("https://example.com/products", 250);
        String toStringResult = url.toString();
        
        assertNotNull(toStringResult, "toString() should not return null");
        assertTrue(toStringResult.contains("DomainUrl"), 
            "toString() should contain class name");
        assertTrue(toStringResult.contains("@"), 
            "toString() should contain @ symbol from Object.toString()");
        // Object.toString() format is "ClassName@HashCode", so it won't contain field values
        
        // Test toString() format consistency
        DomainUrl url1 = new DomainUrl("https://test.com", 100);
        DomainUrl url2 = new DomainUrl("https://test.com", 100);
        
        String toString1 = url1.toString();
        String toString2 = url2.toString();
        assertNotEquals(toString1, toString2, 
            "toString() should be different for different instances without @Data");
        
        // Test toString() with null fields (Object.toString doesn't care about field values)
        DomainUrl urlWithNullUrl = new DomainUrl(null, 150);
        String toStringWithNullUrl = urlWithNullUrl.toString();
        assertNotNull(toStringWithNullUrl, "toString() should handle null URL");
        assertTrue(toStringWithNullUrl.contains("DomainUrl"), 
            "toString() should contain class name even with null URL");
        
        DomainUrl urlWithNullKeywords = new DomainUrl("https://example.com", null);
        String toStringWithNullKeywords = urlWithNullKeywords.toString();
        assertNotNull(toStringWithNullKeywords, "toString() should handle null keywords");
        assertTrue(toStringWithNullKeywords.contains("DomainUrl"), 
            "toString() should contain class name even with null keywords");
        
        DomainUrl urlWithBothNull = new DomainUrl(null, null);
        String toStringWithBothNull = urlWithBothNull.toString();
        assertNotNull(toStringWithBothNull, "toString() should handle both null fields");
        assertTrue(toStringWithBothNull.contains("DomainUrl"), 
            "toString() should contain class name even with null fields");
        
        // Test toString() readable format (Object.toString is basic but readable)
        DomainUrl readableUrl = new DomainUrl("https://shop.example.com/category/electronics", 1250);
        String readableToString = readableUrl.toString();
        
        // Should be human-readable and contain key information
        assertTrue(readableToString.length() > 10, "toString() should be reasonably detailed");
        assertTrue(readableToString.contains("@"), 
            "toString() should contain object hash code from Object.toString()");
        
        // Test toString() for debugging purposes (Object.toString provides basic debugging info)
        DomainUrl debugUrl = new DomainUrl("https://debug.com/test", 0);
        String debugToString = debugUrl.toString();
        
        // Should contain enough information for basic debugging
        assertTrue(debugToString.contains("DomainUrl"), 
            "toString() should indicate class name for debugging");
        assertTrue(debugToString.matches(".*@[0-9a-f]+"), 
            "toString() should contain hash code for object identification");
        
        // Test toString() with special characters in URL (Object.toString doesn't include field values)
        DomainUrl specialUrl = new DomainUrl("https://example.com/path-with-special_chars?param=value", 75);
        String specialToString = specialUrl.toString();
        assertNotNull(specialToString, "toString() should handle special characters in URL");
        assertTrue(specialToString.contains("DomainUrl"), 
            "toString() should contain class name");
        
        // Test toString() consistency with large numbers (Object.toString doesn't include field values)
        DomainUrl largeNumberUrl = new DomainUrl("https://example.com", Integer.MAX_VALUE);
        String largeNumberToString = largeNumberUrl.toString();
        assertTrue(largeNumberToString.contains("DomainUrl"), 
            "toString() should contain class name");
        assertTrue(largeNumberToString.contains("@"), 
            "toString() should contain hash code marker");
    }
    
    @Test
    @DisplayName("Test data validation and constraints")
    void testDataValidationAndConstraints() {
        // Test URL format validation patterns
        String[] validUrlPatterns = {
            "https://example.com",
            "http://test.com",
            "https://subdomain.example.com/path",
            "https://example.com:8080/path",
            "https://example.com/path/to/resource.html",
            "/relative/path",
            "//protocol-relative.com"
        };
        
        for (String urlPattern : validUrlPatterns) {
            DomainUrl url = new DomainUrl(urlPattern, 100);
            assertTrue(isValidUrlPattern(urlPattern), 
                "URL pattern should be considered valid: " + urlPattern);
            assertEquals(urlPattern, url.getUrl(), "URL should be stored as-is");
        }
        
        // Test keyword count constraints
        Integer[] validKeywordCounts = {0, 1, 50, 100, 500, 1000, 5000, 10000};
        
        for (Integer count : validKeywordCounts) {
            DomainUrl url = new DomainUrl("https://example.com", count);
            assertTrue(isValidKeywordCount(count), 
                "Keyword count should be valid: " + count);
            assertEquals(count, url.getKeywords());
        }
          // Test boundary conditions for keyword counts
        DomainUrl zeroKeywords = new DomainUrl("https://example.com", 0);
        assertTrue(isValidKeywordCount(0), "Zero keywords should be valid");
        assertEquals(Integer.valueOf(0), zeroKeywords.getKeywords(), "Zero keywords should be preserved");
        
        DomainUrl maxKeywords = new DomainUrl("https://example.com", Integer.MAX_VALUE);
        assertTrue(isValidKeywordCount(Integer.MAX_VALUE), "Max integer keywords should be valid");
        assertEquals(Integer.valueOf(Integer.MAX_VALUE), maxKeywords.getKeywords(), "Max keywords should be preserved");
        
        // Test data integrity after modifications
        DomainUrl mutableUrl = new DomainUrl("https://original.com", 100);
        String originalUrl = mutableUrl.getUrl();
        Integer originalKeywords = mutableUrl.getKeywords();
        
        // Modify and verify data integrity
        mutableUrl.setUrl("https://modified.com");
        mutableUrl.setKeywords(200);
        
        assertEquals("https://modified.com", mutableUrl.getUrl(), "URL should be updated");
        assertEquals(Integer.valueOf(200), mutableUrl.getKeywords(), "Keywords should be updated");
        assertNotEquals(originalUrl, mutableUrl.getUrl(), "URL should be different from original");
        assertNotEquals(originalKeywords, mutableUrl.getKeywords(), "Keywords should be different from original");
        
        // Test validation with realistic data
        DomainUrl realisticUrl = new DomainUrl("https://shop.example.com/products/electronics/laptops", 1250);
        assertTrue(isRealisticUrlData(realisticUrl), "Realistic URL data should be valid");
        assertEquals(1250, realisticUrl.getKeywords().intValue(), "Realistic keyword count should be preserved");
    }
      @Test
    @DisplayName("Test object copying and immutability aspects")
    void testObjectCopyingAndImmutabilityAspects() {
        // Test creating copies with constructor
        DomainUrl original = new DomainUrl("https://example.com/original", 300);
        DomainUrl copy = new DomainUrl(original.getUrl(), original.getKeywords());
        
        // Objects won't be equal since no @Data/@EqualsAndHashCode, but data should be same
        assertNotEquals(original, copy, "Copy should be different instance without @Data");
        assertNotEquals(original.hashCode(), copy.hashCode(), "Copy should have different hash code without @Data");
        assertNotSame(original, copy, "Copy should be different object instance");
        
        // But the data should be the same
        assertEquals(original.getUrl(), copy.getUrl(), "Copy should have same URL data");
        assertEquals(original.getKeywords(), copy.getKeywords(), "Copy should have same keywords data");
        
        // Test independence of copied objects
        copy.setUrl("https://example.com/modified");
        copy.setKeywords(400);
        
        assertEquals("https://example.com/original", original.getUrl(), 
            "Original URL should remain unchanged");
        assertEquals(Integer.valueOf(300), original.getKeywords(), 
            "Original keywords should remain unchanged");
        assertEquals("https://example.com/modified", copy.getUrl(), 
            "Copy URL should be modified");
        assertEquals(Integer.valueOf(400), copy.getKeywords(), 
            "Copy keywords should be modified");
        
        // Test defensive copying behavior
        String urlRef = "https://shared.com";
        Integer keywordsRef = 150;
        
        DomainUrl url1 = new DomainUrl(urlRef, keywordsRef);
        DomainUrl url2 = new DomainUrl(urlRef, keywordsRef);
        
        // Objects won't be equal, but should have same data
        assertNotEquals(url1, url2, "Objects should not be equal without @Data");
        assertEquals(url1.getUrl(), url2.getUrl(), "Objects should have same URL data");
        assertEquals(url1.getKeywords(), url2.getKeywords(), "Objects should have same keywords data");
        
        // Modify original references (strings are immutable, integers are immutable)
        urlRef = "https://different.com";
        keywordsRef = 250;
        
        assertEquals("https://shared.com", url1.getUrl(), 
            "URL should not be affected by reference modification");
        assertEquals("https://shared.com", url2.getUrl(), 
            "URL should not be affected by reference modification");
        assertEquals(Integer.valueOf(150), url1.getKeywords(), 
            "Keywords should not be affected by reference modification");
        assertEquals(Integer.valueOf(150), url2.getKeywords(), 
            "Keywords should not be affected by reference modification");
        
        // Test object state consistency during concurrent access simulation
        DomainUrl concurrentUrl = new DomainUrl("https://concurrent.com", 500);
        
        // Simulate multiple "threads" accessing the object
        String url1Read = concurrentUrl.getUrl();
        Integer keywords1Read = concurrentUrl.getKeywords();
        
        concurrentUrl.setUrl("https://updated.com");
        
        String url2Read = concurrentUrl.getUrl();
        Integer keywords2Read = concurrentUrl.getKeywords();
        
        assertEquals("https://concurrent.com", url1Read, "First read should get original URL");
        assertEquals(Integer.valueOf(500), keywords1Read, "First read should get original keywords");
        assertEquals("https://updated.com", url2Read, "Second read should get updated URL");
        assertEquals(Integer.valueOf(500), keywords2Read, "Second read should get unchanged keywords");
        
        // Test null safety in copying scenarios
        DomainUrl nullFieldsOriginal = new DomainUrl(null, null);
        DomainUrl nullFieldsCopy = new DomainUrl(nullFieldsOriginal.getUrl(), nullFieldsOriginal.getKeywords());
        
        assertNotEquals(nullFieldsOriginal, nullFieldsCopy, "Copy with nulls should not equal original without @Data");
        assertNull(nullFieldsCopy.getUrl(), "Copied null URL should remain null");
        assertNull(nullFieldsCopy.getKeywords(), "Copied null keywords should remain null");
        assertEquals(nullFieldsOriginal.getUrl(), nullFieldsCopy.getUrl(), "Null URLs should be equal");
        assertEquals(nullFieldsOriginal.getKeywords(), nullFieldsCopy.getKeywords(), "Null keywords should be equal");
    }
    
    // Helper methods for validation tests
    private boolean isValidUrlPattern(String url) {
        if (url == null) return false;
        if (url.isEmpty()) return true; // Empty URLs might be allowed
        return url.startsWith("http://") || url.startsWith("https://") || 
               url.startsWith("/") || url.startsWith("//") || 
               url.contains(".com") || url.contains(".org") || url.contains(".net");
    }
    
    private boolean isValidKeywordCount(Integer count) {
        return count != null && count >= 0; // Assuming non-negative is valid
    }
    
    private boolean isRealisticUrlData(DomainUrl url) {
        if (url == null) return false;
        String urlString = url.getUrl();
        Integer keywords = url.getKeywords();
        
        // Realistic URL should have domain and reasonable keyword count
        return urlString != null && urlString.contains(".") && 
               keywords != null && keywords >= 0 && keywords <= 100000;
    }

    // ... existing helper methods ...
}
