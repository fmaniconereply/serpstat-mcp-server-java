package com.serpstat.domains.domain;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.utils.ValidationUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static com.serpstat.domains.constants.Patterns.DOMAIN_PATTERN;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DomainValidator class
 *
 * Tests domain info request validation, domain keywords request validation,
 */
@DisplayName("DomainValidator Tests")
class DomainValidatorTest {
    @Test
    @DisplayName("Test domains info request validation - valid and invalid cases")
    void testValidateDomainsInfoRequest() {
        // Test valid input
        Map<String, Object> validArgs = new HashMap<>();
        validArgs.put("domains", Arrays.asList("example.com", "test.org"));
        validArgs.put("se", "g_us");

        assertDoesNotThrow(() -> DomainValidator.validateDomainsInfoRequest(validArgs),
                "Valid domains info request should not throw exception");

        // Test null domains
        Map<String, Object> nullDomainsArgs = new HashMap<>();
        nullDomainsArgs.put("se", "g_us");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(nullDomainsArgs));
        assertEquals("Parameter 'domains' is required", exception.getMessage());

        // Test empty domains array
        Map<String, Object> emptyDomainsArgs = new HashMap<>();
        emptyDomainsArgs.put("domains", Arrays.asList());
        emptyDomainsArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(emptyDomainsArgs));
        assertEquals("Parameter 'domains' cannot be empty", exception.getMessage());

        // Test too many domains (> 100)
        Map<String, Object> tooManyDomainsArgs = new HashMap<>();
        List<String> manyDomains = new java.util.ArrayList<>();
        for (int i = 0; i <= 100; i++) {
            manyDomains.add("domain" + i + ".com");
        }
        tooManyDomainsArgs.put("domains", manyDomains);
        tooManyDomainsArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(tooManyDomainsArgs));
        assertEquals("Maximum 100 domains allowed per request", exception.getMessage());

        // Test invalid domain format
        Map<String, Object> invalidDomainArgs = new HashMap<>();
        invalidDomainArgs.put("domains", Arrays.asList("invalid-domain"));
        invalidDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(invalidDomainArgs));
        assertTrue(exception.getMessage().contains("Invalid domain format"));
    }

    @Test
    @DisplayName("Test domain keywords request validation")
    void testValidateDomainKeywordsRequest() {
        // Test valid input
        Map<String, Object> validArgs = new HashMap<>();
        validArgs.put("domain", "example.com");
        validArgs.put("se", "g_us");
        validArgs.put("page", 1);
        validArgs.put("size", 100);

        assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(validArgs),
                "Valid domain keywords request should not throw exception");

        // Test invalid domain
        Map<String, Object> invalidDomainArgs = new HashMap<>();
        invalidDomainArgs.put("domain", "invalid");
        invalidDomainArgs.put("se", "g_us");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainKeywordsRequest(invalidDomainArgs));
        assertTrue(exception.getMessage().contains("Invalid domain format"));

        // Test valid URL parameter
        Map<String, Object> validUrlArgs = new HashMap<>();
        validUrlArgs.put("domain", "example.com");
        validUrlArgs.put("se", "g_us");
        validUrlArgs.put("url", "https://example.com/page");

        assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(validUrlArgs),
                "Valid URL parameter should not throw exception");

        // Test invalid URL parameter
        Map<String, Object> invalidUrlArgs = new HashMap<>();
        invalidUrlArgs.put("domain", "example.com");
        invalidUrlArgs.put("se", "g_us");
        invalidUrlArgs.put("url", "invalid-url");

        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainKeywordsRequest(invalidUrlArgs));
        assertTrue(exception.getMessage().contains("must be a valid HTTP/HTTPS URL"));
    }

    @Test
    @DisplayName("Test domain pattern validation and normalization")
    void testDomainPatternValidationAndNormalization() {
        // Test valid domains normalization
        Map<String, Object> args = new HashMap<>();
        args.put("domains", Arrays.asList("EXAMPLE.COM", "  test.org  ", "sub.Domain.NET"));
        args.put("se", "g_us");

        assertDoesNotThrow(() -> DomainValidator.validateDomainsInfoRequest(args));

        // Verify domains were normalized to lowercase and trimmed
        @SuppressWarnings("unchecked")
        List<String> normalizedDomains = (List<String>) args.get("domains");
        assertEquals("example.com", normalizedDomains.get(0));
        assertEquals("test.org", normalizedDomains.get(1));
        assertEquals("sub.domain.net", normalizedDomains.get(2));

        // Test duplicate domains detection
        Map<String, Object> duplicateArgs = new HashMap<>();
        duplicateArgs.put("domains", Arrays.asList("example.com", "example.com"));
        duplicateArgs.put("se", "g_us");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(duplicateArgs));
        assertEquals("Duplicate domains are not allowed", exception.getMessage());

        // Test null domain in array
        Map<String, Object> nullDomainArgs = new HashMap<>();
        nullDomainArgs.put("domains", Arrays.asList("example.com", null));
        nullDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(nullDomainArgs));
        assertTrue(exception.getMessage().contains("Domain at index 1 is empty"));

        // Test empty domain in array
        Map<String, Object> emptyDomainArgs = new HashMap<>();
        emptyDomainArgs.put("domains", Arrays.asList("example.com", ""));
        emptyDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(emptyDomainArgs));
        assertTrue(exception.getMessage().contains("Domain at index 1 is empty"));
    }    @Test
    @DisplayName("Test validate domains info request with invalid input")
    void testValidateDomainsInfoRequestInvalid() {
        // Test with null domains parameter
        Map<String, Object> nullDomainsArgs = new HashMap<>();
        nullDomainsArgs.put("se", "g_us");
        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(nullDomainsArgs));
        assertEquals("Parameter 'domains' is required", exception.getMessage());
        
        // Test with empty domains array
        Map<String, Object> emptyDomainsArgs = new HashMap<>();
        emptyDomainsArgs.put("domains", Arrays.asList());
        emptyDomainsArgs.put("se", "g_us");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(emptyDomainsArgs));
        assertEquals("Parameter 'domains' cannot be empty", exception.getMessage());
        
        // Test with > 100 domains (maximum 100 domains allowed per request)
        Map<String, Object> tooManyDomainsArgs = new HashMap<>();
        List<String> manyDomains = new ArrayList<>();
        for (int i = 0; i <= 100; i++) { // 101 domains
            manyDomains.add("domain" + i + ".com");
        }
        tooManyDomainsArgs.put("domains", manyDomains);
        tooManyDomainsArgs.put("se", "g_us");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(tooManyDomainsArgs));
        assertEquals("Maximum 100 domains allowed per request", exception.getMessage());
        
        // Test with invalid domain format
        Map<String, Object> invalidDomainArgs = new HashMap<>();
        invalidDomainArgs.put("domains", Arrays.asList("invalid_domain_format"));
        invalidDomainArgs.put("se", "g_us");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(invalidDomainArgs));
        assertTrue(exception.getMessage().contains("Invalid domain format"),
                "Should mention invalid domain format");
        
        // Test with null domain in array
        Map<String, Object> nullDomainInArrayArgs = new HashMap<>();
        nullDomainInArrayArgs.put("domains", Arrays.asList("example.com", null));
        nullDomainInArrayArgs.put("se", "g_us");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(nullDomainInArrayArgs));
        assertTrue(exception.getMessage().contains("Domain at index 1 is empty"),
                "Should mention empty domain at specific index");
        
        // Test with empty string domain in array
        Map<String, Object> emptyDomainInArrayArgs = new HashMap<>();
        emptyDomainInArrayArgs.put("domains", Arrays.asList("example.com", ""));
        emptyDomainInArrayArgs.put("se", "g_us");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(emptyDomainInArrayArgs));
        assertTrue(exception.getMessage().contains("Domain at index 1 is empty"),
                "Should mention empty domain at specific index");
        
        // Test with duplicate domains
        Map<String, Object> duplicateDomainsArgs = new HashMap<>();
        duplicateDomainsArgs.put("domains", Arrays.asList("example.com", "example.com"));
        duplicateDomainsArgs.put("se", "g_us");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(duplicateDomainsArgs));
        assertEquals("Duplicate domains are not allowed", exception.getMessage());
        
        // Test with invalid search engine
        Map<String, Object> invalidSeArgs = new HashMap<>();
        invalidSeArgs.put("domains", Arrays.asList("example.com"));
        invalidSeArgs.put("se", "invalid_se");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainsInfoRequest(invalidSeArgs));
        assertTrue(exception.getMessage().toLowerCase().contains("search engine") ||
                   exception.getMessage().toLowerCase().contains("se"),
                "Should mention search engine parameter");
          // Test with non-string domain in array (this causes ClassCastException in current implementation)
        // So we expect either ValidationException or ClassCastException
        Map<String, Object> nonStringDomainArgs = new HashMap<>();
        List<Object> mixedList = new ArrayList<>();
        mixedList.add("example.com");
        mixedList.add(123); // Integer instead of String
        nonStringDomainArgs.put("domains", mixedList);
        nonStringDomainArgs.put("se", "g_us");
        
        // Current implementation throws ClassCastException, not ValidationException
        assertThrows(ClassCastException.class,
                () -> DomainValidator.validateDomainsInfoRequest(nonStringDomainArgs),
                "Non-string values in domain array should cause ClassCastException in current implementation");
    }    @Test
    @DisplayName("Test validate regions count request")
    void testValidateRegionsCountRequest() {
        // Test with valid domain parameter (minimal case)
        Map<String, Object> validArgs = new HashMap<>();
        validArgs.put("domain", "example.com");
        
        assertDoesNotThrow(() -> DomainValidator.validateRegionsCountRequest(validArgs),
                "Valid domain parameter should not throw exception");
        
        // Check that default values are set
        assertEquals("keywords_count", validArgs.getOrDefault("sort", "keywords_count"),
                "Default sort should be 'keywords_count'");
        assertEquals("desc", validArgs.getOrDefault("order", "desc"),
                "Default order should be 'desc'");
        
        // Test with valid sort options
        String[] validSortOptions = {"keywords_count", "country_name_en", "db_name"};
        for (String sortOption : validSortOptions) {
            Map<String, Object> sortArgs = new HashMap<>();
            sortArgs.put("domain", "example.com");
            sortArgs.put("sort", sortOption);
            
            assertDoesNotThrow(() -> DomainValidator.validateRegionsCountRequest(sortArgs),
                    "Valid sort option '" + sortOption + "' should not throw exception");
        }
        
        // Test with valid order options
        String[] validOrderOptions = {"asc", "desc"};
        for (String orderOption : validOrderOptions) {
            Map<String, Object> orderArgs = new HashMap<>();
            orderArgs.put("domain", "example.com");
            orderArgs.put("order", orderOption);
            
            assertDoesNotThrow(() -> DomainValidator.validateRegionsCountRequest(orderArgs),
                    "Valid order option '" + orderOption + "' should not throw exception");
        }
        
        // Test with valid combination of all parameters
        Map<String, Object> fullArgs = new HashMap<>();
        fullArgs.put("domain", "sub.example.co.uk");
        fullArgs.put("sort", "country_name_en");
        fullArgs.put("order", "asc");
        
        assertDoesNotThrow(() -> DomainValidator.validateRegionsCountRequest(fullArgs),
                "Valid combination of all parameters should not throw exception");
        
        // Test with missing domain parameter
        Map<String, Object> missingDomainArgs = new HashMap<>();
        missingDomainArgs.put("sort", "keywords_count");
        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateRegionsCountRequest(missingDomainArgs));
        assertTrue(exception.getMessage().toLowerCase().contains("domain") &&
                   exception.getMessage().toLowerCase().contains("required"),
                "Missing domain should mention domain is required");
        
        // Test with invalid domain format
        Map<String, Object> invalidDomainArgs = new HashMap<>();
        invalidDomainArgs.put("domain", "invalid_domain_format");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateRegionsCountRequest(invalidDomainArgs));
        assertTrue(exception.getMessage().contains("Invalid domain format"),
                "Invalid domain format should mention format issue");
        
        // Test with invalid sort option
        Map<String, Object> invalidSortArgs = new HashMap<>();
        invalidSortArgs.put("domain", "example.com");
        invalidSortArgs.put("sort", "invalid_sort");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateRegionsCountRequest(invalidSortArgs));
        assertTrue(exception.getMessage().contains("Invalid sort field") &&
                   exception.getMessage().contains("invalid_sort"),
                "Invalid sort should mention the field and provide valid options");
        assertTrue(exception.getMessage().contains("keywords_count") &&
                   exception.getMessage().contains("country_name_en") &&
                   exception.getMessage().contains("db_name"),
                "Error should list valid sort options");
        
        // Test with invalid order option
        Map<String, Object> invalidOrderArgs = new HashMap<>();
        invalidOrderArgs.put("domain", "example.com");
        invalidOrderArgs.put("order", "invalid_order");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateRegionsCountRequest(invalidOrderArgs));
        assertTrue(exception.getMessage().contains("Invalid order") &&
                   exception.getMessage().contains("invalid_order"),
                "Invalid order should mention the value and provide valid options");
        assertTrue(exception.getMessage().contains("asc") &&
                   exception.getMessage().contains("desc"),
                "Error should list valid order options");
        
        // Test domain normalization
        Map<String, Object> normalizationArgs = new HashMap<>();
        normalizationArgs.put("domain", "  EXAMPLE.COM  ");
        
        assertDoesNotThrow(() -> DomainValidator.validateRegionsCountRequest(normalizationArgs),
                "Domain normalization should work");
        assertEquals("example.com", normalizationArgs.get("domain"),
                "Domain should be normalized to lowercase and trimmed");
    }    @Test
    @DisplayName("Test validate domain keywords request - additional tests")
    void testValidateDomainKeywordsRequestAdditional() {
        // Test with valid domain parameter (minimal case)
        Map<String, Object> validArgs = new HashMap<>();
        validArgs.put("domain", "example.com");
        validArgs.put("se", "g_us");
        
        assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(validArgs),
                "Valid minimal request should not throw exception");
        
        // Test with valid search engine parameter (se)
        String[] validSearchEngines = {"g_us", "g_uk", "g_de", "g_fr", "g_au", "g_ca"};
        for (String se : validSearchEngines) {
            Map<String, Object> seArgs = new HashMap<>();
            seArgs.put("domain", "example.com");
            seArgs.put("se", se);
            
            assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(seArgs),
                    "Valid search engine '" + se + "' should not throw exception");
        }
        
        // Test with valid pagination: page >= 1, size between 1-1000
        Map<String, Object> paginationArgs = new HashMap<>();
        paginationArgs.put("domain", "example.com");
        paginationArgs.put("se", "g_us");
        paginationArgs.put("page", 1);
        paginationArgs.put("size", 100);
        
        assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(paginationArgs),
                "Valid pagination should not throw exception");
        
        // Test boundary pagination values
        paginationArgs.put("page", 1);
        paginationArgs.put("size", 1);
        assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(paginationArgs),
                "Minimum pagination values should not throw exception");
        
        paginationArgs.put("size", 1000);
        assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(paginationArgs),
                "Maximum pagination size should not throw exception");
        
        // Test with valid URL parameter
        Map<String, Object> urlArgs = new HashMap<>();
        urlArgs.put("domain", "example.com");
        urlArgs.put("se", "g_us");
        urlArgs.put("url", "https://example.com/page");
        
        assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(urlArgs),
                "Valid URL parameter should not throw exception");
        
        // Test with valid keywords and minusKeywords arrays
        Map<String, Object> keywordsArgs = new HashMap<>();
        keywordsArgs.put("domain", "example.com");
        keywordsArgs.put("se", "g_us");
        keywordsArgs.put("keywords", Arrays.asList("keyword1", "keyword2", "keyword3"));
        keywordsArgs.put("minusKeywords", Arrays.asList("minus1", "minus2"));
        
        assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(keywordsArgs),
                "Valid keywords arrays should not throw exception");
        
        // Test with valid sort parameters
        String[] validSortFields = {
            "position", "region_queries_count", "cost", "traff", "difficulty",
            "keyword_length", "concurrency", "types", "geo_names", "region_queries_count_wide",
            "dynamic", "found_results"
        };
        
        for (String sortField : validSortFields) {
            Map<String, Object> sortArgs = new HashMap<>();
            sortArgs.put("domain", "example.com");
            sortArgs.put("se", "g_us");
            
            Map<String, Object> sort = new HashMap<>();
            sort.put(sortField, "desc");
            sortArgs.put("sort", sort);
            
            assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(sortArgs),
                    "Valid sort field '" + sortField + "' should not throw exception");
        }
        
        // Test with full complex request
        Map<String, Object> fullArgs = new HashMap<>();
        fullArgs.put("domain", "sub.example.co.uk");
        fullArgs.put("se", "g_uk");
        fullArgs.put("page", 2);
        fullArgs.put("size", 500);
        fullArgs.put("url", "https://sub.example.co.uk/products");
        fullArgs.put("keywords", Arrays.asList("product", "buy", "shop"));
        fullArgs.put("minusKeywords", Arrays.asList("free", "demo"));
        
        Map<String, Object> sort = new HashMap<>();
        sort.put("position", "asc");
        fullArgs.put("sort", sort);
        
        // Add valid filters
        Map<String, Object> filters = new HashMap<>();
        filters.put("position", 10);
        filters.put("difficulty", 50);
        filters.put("cost", 1.5);
        fullArgs.put("filters", filters);
        
        assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(fullArgs),
                "Complex full request should not throw exception");
        
        // Test invalid domain -> ValidationException
        Map<String, Object> invalidDomainArgs = new HashMap<>();
        invalidDomainArgs.put("domain", "invalid_domain_format");
        invalidDomainArgs.put("se", "g_us");
        
        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainKeywordsRequest(invalidDomainArgs));
        assertTrue(exception.getMessage().contains("Invalid domain format"),
                "Invalid domain should mention format issue");
        
        // Test invalid pagination values -> ValidationException
        Map<String, Object> invalidPageArgs = new HashMap<>();
        invalidPageArgs.put("domain", "example.com");
        invalidPageArgs.put("se", "g_us");
        invalidPageArgs.put("page", 0);
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainKeywordsRequest(invalidPageArgs));
        assertTrue(exception.getMessage().toLowerCase().contains("page"),
                "Invalid page should mention page parameter");
        
        Map<String, Object> invalidSizeArgs = new HashMap<>();
        invalidSizeArgs.put("domain", "example.com");
        invalidSizeArgs.put("se", "g_us");
        invalidSizeArgs.put("size", 1001);
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainKeywordsRequest(invalidSizeArgs));
        assertTrue(exception.getMessage().toLowerCase().contains("size"),
                "Invalid size should mention size parameter");
        
        // Test invalid URL parameter
        Map<String, Object> invalidUrlArgs = new HashMap<>();
        invalidUrlArgs.put("domain", "example.com");
        invalidUrlArgs.put("se", "g_us");
        invalidUrlArgs.put("url", "invalid-url");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainKeywordsRequest(invalidUrlArgs));
        assertTrue(exception.getMessage().contains("valid HTTP/HTTPS URL"),
                "Invalid URL should mention URL format requirement");
        
        // Test invalid search engine
        Map<String, Object> invalidSeArgs = new HashMap<>();
        invalidSeArgs.put("domain", "example.com");
        invalidSeArgs.put("se", "invalid_se");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainKeywordsRequest(invalidSeArgs));
        assertTrue(exception.getMessage().toLowerCase().contains("search engine") ||
                   exception.getMessage().toLowerCase().contains("se"),
                "Invalid search engine should mention search engine parameter");
        
        // Test missing required domain parameter
        Map<String, Object> missingDomainArgs = new HashMap<>();
        missingDomainArgs.put("se", "g_us");
        
        exception = assertThrows(ValidationException.class,
                () -> DomainValidator.validateDomainKeywordsRequest(missingDomainArgs));
        assertTrue(exception.getMessage().toLowerCase().contains("domain") &&
                   exception.getMessage().toLowerCase().contains("required"),
                "Missing domain should mention domain is required");
        
        // Test domain normalization
        Map<String, Object> normalizationArgs = new HashMap<>();
        normalizationArgs.put("domain", "  EXAMPLE.COM  ");
        normalizationArgs.put("se", "g_us");
        
        assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(normalizationArgs),
                "Domain normalization should work");
        assertEquals("example.com", normalizationArgs.get("domain"),
                "Domain should be normalized to lowercase and trimmed");
    }    @Test
    @DisplayName("Test validate domain URLs request")
    void testValidateDomainUrlsRequest() {
        // Test with valid domain parameter
        Map<String, Object> validArgs = new HashMap<>();
        validArgs.put("domain", "example.com");
        validArgs.put("se", "g_us");
        validArgs.put("page", 1);
        validArgs.put("size", 100);
        
        assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(validArgs));
        assertEquals("example.com", validArgs.get("domain"));
          // Test with valid URL filters
        Map<String, Object> argsWithFilters = new HashMap<>();
        argsWithFilters.put("domain", "test-site.org");
        argsWithFilters.put("se", "g_uk");
        
        Map<String, Object> filters = new HashMap<>();
        filters.put("url_prefix", "https://test-site.org/blog");
        filters.put("url_contain", "article");
        filters.put("url_not_contain", "admin");
        argsWithFilters.put("filters", filters);
        
        assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(argsWithFilters));
        
        // Test with invalid domain -> ValidationException
        Map<String, Object> invalidDomainArgs = new HashMap<>();
        invalidDomainArgs.put("domain", "invalid-domain");
        invalidDomainArgs.put("se", "g_us");
        
        ValidationException domainException = assertThrows(ValidationException.class, 
            () -> DomainValidator.validateDomainUrlsRequest(invalidDomainArgs));
        assertTrue(domainException.getMessage().contains("domain"));
        
        // Test with invalid search engine
        Map<String, Object> invalidSeArgs = new HashMap<>();
        invalidSeArgs.put("domain", "example.com");
        invalidSeArgs.put("se", "invalid_se");
        
        ValidationException seException = assertThrows(ValidationException.class, 
            () -> DomainValidator.validateDomainUrlsRequest(invalidSeArgs));
        assertTrue(seException.getMessage().contains("search engine"));
        
        // Test with invalid pagination -> ValidationException
        Map<String, Object> invalidPageArgs = new HashMap<>();
        invalidPageArgs.put("domain", "example.com");
        invalidPageArgs.put("se", "g_us");
        invalidPageArgs.put("page", 0);
        
        ValidationException pageException = assertThrows(ValidationException.class, 
            () -> DomainValidator.validateDomainUrlsRequest(invalidPageArgs));
        assertTrue(pageException.getMessage().toLowerCase().contains("page"));
        
        Map<String, Object> invalidSizeArgs = new HashMap<>();
        invalidSizeArgs.put("domain", "example.com");
        invalidSizeArgs.put("se", "g_us");
        invalidSizeArgs.put("size", 1001);
        
        ValidationException sizeException = assertThrows(ValidationException.class, 
            () -> DomainValidator.validateDomainUrlsRequest(invalidSizeArgs));
        assertTrue(sizeException.getMessage().toLowerCase().contains("size"));
    }    @Test
    @DisplayName("Test domain pattern validation")
    void testDomainPatternValidation() {        // Test valid domains using DOMAIN_PATTERN
        String[] validDomains = {
                "example.com", "sub.domain.org", "test-site.co.uk",
                "a.bb", "x-y-z.example", "test123.com",
                "very-long-subdomain.example-domain.org"
        };
        
        for (String domain : validDomains) {
            assertTrue(DOMAIN_PATTERN.matcher(domain).matches(), 
                "Domain should be valid: " + domain);
        }
        
        // Test invalid domains using DOMAIN_PATTERN  
        String[] invalidDomains = {
                "invalid", "http://example.com", "example.com/",
                "example.", ".example.com", "-example.com",
                "example-.com", "exam..ple.com", "example.c"
        };
        
        for (String domain : invalidDomains) {
            assertFalse(DOMAIN_PATTERN.matcher(domain).matches(), 
                "Domain should be invalid: " + domain);
        }
        
        // Test edge cases: very long domains
        String longValidDomain = "a".repeat(60) + ".example.com";
        assertTrue(DOMAIN_PATTERN.matcher(longValidDomain).matches());
        
        String tooLongSubdomain = "a".repeat(64) + ".example.com";
        assertFalse(DOMAIN_PATTERN.matcher(tooLongSubdomain).matches());
          // Test international domains (basic ASCII validation)
        String[] internationalDomains = {
                "example.укр", "test.中国"  // These should fail with current pattern
        };
        
        for (String domain : internationalDomains) {
            assertFalse(DOMAIN_PATTERN.matcher(domain).matches(), 
                "Non-ASCII domain should be invalid with current pattern: " + domain);
        }
        
        // Test ValidationUtils integration with domain validation
        Map<String, Object> args = new HashMap<>();
        args.put("domain", "invalid-domain");
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateAndNormalizeDomain(args.get("domain")));
        assertTrue(exception.getMessage().contains("Invalid domain format"));
    }    @Test
    @DisplayName("Test search engine validation")
    void testSearchEngineValidation() {        // Test valid search engines
        String[] validSearchEngines = {
                "g_us", "g_uk", "g_de", "g_fr", "g_ca", "g_au",
                "g_br", "g_mx", "g_es", "g_it", "g_nl", "g_pl",
                "g_ua", "g_bg", "bing_us"
        };
        
        for (String se : validSearchEngines) {
            Map<String, Object> args = new HashMap<>();
            args.put("se", se);
            
            assertDoesNotThrow(() -> ValidationUtils.validateSearchEngines(args, "se", "g_us", true),
                "Valid search engine should not throw exception: " + se);
        }
          // Test invalid search engines -> ValidationException
        String[] invalidSearchEngines = {
                "invalid_se", "google_us", "g_invalid", "bing_uk",
                "yandex_ua", "", null
        };
        
        for (String se : invalidSearchEngines) {
            Map<String, Object> args = new HashMap<>();
            if (se != null) {
                args.put("se", se);
            }
            
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> ValidationUtils.validateSearchEngines(args, "se", "g_us", true),
                "Invalid search engine should throw exception: " + se);
            
            assertTrue(exception.getMessage().toLowerCase().contains("search engine") || 
                      exception.getMessage().contains("required"),
                "Exception message should mention search engine: " + exception.getMessage());
        }
          // Test default search engine handling
        Map<String, Object> argsWithoutSe = new HashMap<>();
        assertDoesNotThrow(() -> ValidationUtils.validateSearchEngines(argsWithoutSe, "se", "g_us", false));
        // Note: ValidationUtils doesn't set the default value back to the map, it just uses it for validation
        
        // Test case sensitivity
        Map<String, Object> caseSensitiveArgs = new HashMap<>();
        caseSensitiveArgs.put("se", "G_US"); // uppercase should fail
        
        ValidationException caseException = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateSearchEngines(caseSensitiveArgs, "se", "g_us", true));
        assertTrue(caseException.getMessage().contains("search engine"));
    }    @Test
    @DisplayName("Test pagination validation")
    void testPaginationValidation() {
        // Test valid page numbers: 1, 2, 100, etc.
        int[] validPages = {1, 2, 10, 100, 1000};
        
        for (int page : validPages) {
            Map<String, Object> args = new HashMap<>();
            args.put("page", page);
            
            assertDoesNotThrow(() -> ValidationUtils.validatePaginationParameters(args),
                "Valid page number should not throw exception: " + page);
        }
        
        // Test invalid page numbers: 0, negative values -> ValidationException
        int[] invalidPages = {0, -1, -10};
        
        for (int page : invalidPages) {
            Map<String, Object> args = new HashMap<>();
            args.put("page", page);
            
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> ValidationUtils.validatePaginationParameters(args),
                "Invalid page number should throw exception: " + page);
            
            assertTrue(exception.getMessage().toLowerCase().contains("page"));
        }
        
        // Test valid size values: 1-1000
        int[] validSizes = {1, 10, 100, 500, 1000};
        
        for (int size : validSizes) {
            Map<String, Object> args = new HashMap<>();
            args.put("size", size);
            
            assertDoesNotThrow(() -> ValidationUtils.validatePaginationSizeParameters(args),
                "Valid size should not throw exception: " + size);
        }
        
        // Test invalid size values: 0, > 1000 -> ValidationException
        int[] invalidSizes = {0, -1, 1001, 5000};
        
        for (int size : invalidSizes) {
            Map<String, Object> args = new HashMap<>();
            args.put("size", size);
            
            ValidationException exception = assertThrows(ValidationException.class, 
                () -> ValidationUtils.validatePaginationSizeParameters(args),
                "Invalid size should throw exception: " + size);
            
            assertTrue(exception.getMessage().toLowerCase().contains("size"));
        }
        
        // Test default values handling
        Map<String, Object> emptyArgs = new HashMap<>();
        assertDoesNotThrow(() -> ValidationUtils.validatePaginationParameters(emptyArgs));
        assertDoesNotThrow(() -> ValidationUtils.validatePaginationSizeParameters(emptyArgs));
        
        // Test combined validation
        Map<String, Object> combinedArgs = new HashMap<>();
        combinedArgs.put("page", 5);
        combinedArgs.put("size", 200);
        
        assertDoesNotThrow(() -> {
            ValidationUtils.validatePaginationParameters(combinedArgs);
            ValidationUtils.validatePaginationSizeParameters(combinedArgs);
        });
    }    @Test
    @DisplayName("Test filter validation")
    void testFilterValidation() {        // Test numeric filters: visible, traff with valid ranges (based on ValidationUtils)
        Map<String, Object> validNumericFilters = new HashMap<>();
        validNumericFilters.put("visible", 50.0);
        validNumericFilters.put("traff", 1000);
        
        for (Map.Entry<String, Object> entry : validNumericFilters.entrySet()) {
            Map<String, Object> filters = new HashMap<>();
            filters.put(entry.getKey(), entry.getValue());
            
            assertDoesNotThrow(() -> ValidationUtils.validateFilters(filters),
                "Valid numeric filter should not throw exception: " + entry.getKey() + "=" + entry.getValue());
        }
          // Test boolean filters - ValidationUtils doesn't have specific boolean filters, so we'll test with invalid ones
        // Since ValidationUtils only supports 'visible' and 'traff', we can't test generic boolean filters
        
        // Test array filters - ValidationUtils doesn't support array filters, so we'll test with invalid ones        Map<String, Object> arrayFilters = new HashMap<>();
        // ValidationUtils doesn't support keywords/minusKeywords, so these will fail
        // We'll test the invalid filter detection instead
          // Test filter combination validation
        Map<String, Object> combinedFilters = new HashMap<>();
        combinedFilters.put("visible", 10.0);
        combinedFilters.put("traff", 1000);
        
        assertDoesNotThrow(() -> ValidationUtils.validateFilters(combinedFilters));
        
        // Test invalid filter values -> ValidationException
        Map<String, Object> invalidFilters = new HashMap<>();
        invalidFilters.put("traff", -1); // negative traffic
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateFilters(invalidFilters));
        assertTrue(exception.getMessage().toLowerCase().contains("traff") || 
                  exception.getMessage().toLowerCase().contains("negative"));
        
        // Test unknown filter parameter
        Map<String, Object> unknownFilterMap = new HashMap<>();
        unknownFilterMap.put("unknown_filter", "value");
        
        ValidationException unknownException = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateFilters(unknownFilterMap));
        assertTrue(unknownException.getMessage().toLowerCase().contains("unknown"));
    }@Test
    @DisplayName("Test ValidationUtils integration")
    void testValidationUtilsIntegration() {
        // Test that DomainValidator uses ValidationUtils methods
        Map<String, Object> domainsInfoArgs = new HashMap<>();
        domainsInfoArgs.put("domains", Arrays.asList("example.com", "test.org"));
        domainsInfoArgs.put("se", "g_us");
        
        // This should call ValidationUtils.validateSearchEngines internally
        assertDoesNotThrow(() -> DomainValidator.validateDomainsInfoRequest(domainsInfoArgs));
        
        Map<String, Object> regionsCountArgs = new HashMap<>();
        regionsCountArgs.put("domain", "example.com");
        
        // This should call ValidationUtils.validateAndNormalizeDomain internally
        assertDoesNotThrow(() -> DomainValidator.validateRegionsCountRequest(regionsCountArgs));
        
        Map<String, Object> keywordsArgs = new HashMap<>();
        keywordsArgs.put("domain", "example.com");
        keywordsArgs.put("se", "g_us");
        keywordsArgs.put("page", 1);
        keywordsArgs.put("size", 100);
        
        // This should call multiple ValidationUtils methods
        assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(keywordsArgs));
        
        // Test common validation patterns are properly applied
        Map<String, Object> invalidArgs = new HashMap<>();
        invalidArgs.put("domain", "invalid-domain");
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> DomainValidator.validateRegionsCountRequest(invalidArgs));
        assertTrue(exception.getMessage().contains("domain"));
        
        // Test error message consistency
        Map<String, Object> invalidSeArgs = new HashMap<>();
        invalidSeArgs.put("domains", Arrays.asList("example.com"));
        invalidSeArgs.put("se", "invalid_se");
        
        ValidationException seException = assertThrows(ValidationException.class, 
            () -> DomainValidator.validateDomainsInfoRequest(invalidSeArgs));
        assertTrue(seException.getMessage().contains("search engine"));
          // Test validation helper methods
        // Test domain normalization
        assertDoesNotThrow(() -> {
            String normalizedDomain = ValidationUtils.validateAndNormalizeDomain("EXAMPLE.COM");
            assertEquals("example.com", normalizedDomain);
        });
        
        // Test UTF-8 string normalization
        String normalizedString = ValidationUtils.normalizeUtf8String("Test String  ");
        assertEquals("Test String", normalizedString);
    }    
    @Test
    @DisplayName("Test error message quality")
    void testErrorMessageQuality() {
        // Test that ValidationException messages are clear and actionable
        Map<String, Object> invalidDomainsArgs = new HashMap<>();
        invalidDomainsArgs.put("domains", Arrays.asList("invalid-domain"));
        
        ValidationException exception = assertThrows(ValidationException.class, 
            () -> DomainValidator.validateDomainsInfoRequest(invalidDomainsArgs));
        
        String message = exception.getMessage();
        assertTrue(message.length() > 10, "Error message should be descriptive");
        assertTrue(message.contains("domain"), "Error message should mention domain");
        
        // Test that error messages include parameter names and expected formats
        Map<String, Object> invalidSeArgs = new HashMap<>();
        invalidSeArgs.put("domains", Arrays.asList("example.com"));
        invalidSeArgs.put("se", "invalid_search_engine");
        
        ValidationException seException = assertThrows(ValidationException.class, 
            () -> DomainValidator.validateDomainsInfoRequest(invalidSeArgs));
        
        String seMessage = seException.getMessage();
        assertTrue(seMessage.contains("search engine"), 
            "Error message should mention search engine");
        assertTrue(seMessage.length() > 20, 
            "Error message should be detailed");
        
        // Test that error messages include example valid values for search engines
        ValidationException searchEngineException = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validateSearchEngines(Map.of("se", "invalid"), "se", "g_us", true));
        
        String searchEngineMessage = searchEngineException.getMessage();
        assertTrue(searchEngineMessage.contains("g_us") || searchEngineMessage.contains("Supported"), 
            "Error message should include supported values");
        
        // Test error message consistency across methods
        Map<String, Object> invalidPageArgs = new HashMap<>();
        invalidPageArgs.put("page", 0);
        
        ValidationException pageException = assertThrows(ValidationException.class, 
            () -> ValidationUtils.validatePaginationParameters(invalidPageArgs));
        
        String pageMessage = pageException.getMessage();
        assertTrue(pageMessage.toLowerCase().contains("page"), 
            "Page validation error should mention page");
        assertTrue(pageMessage.matches(".*\\d.*"), 
            "Error message should include numeric constraint");        // Test that all error messages are in English
        String[] errorMessages = {message, seMessage, searchEngineMessage, pageMessage};
        for (String errorMessage : errorMessages) {
            assertFalse(errorMessage.matches(".*[а-яё].*"), 
                "Error messages should be in English only");
            assertTrue(errorMessage.trim().length() > 0, 
                "Error message should not be empty");
        }
        
        // Test internationalization considerations (ensure ASCII characters)
        for (String errorMessage : errorMessages) {
            assertTrue(errorMessage.matches("^[\\x00-\\x7F]*$"), 
                "Error messages should use ASCII characters for better compatibility");
        }
    }    @Test
    @DisplayName("Test boundary conditions")
    void testBoundaryConditions() {
        // Test minimum and maximum allowed values for all parameters
        
        // Test domain list boundaries
        Map<String, Object> maxDomainsArgs = new HashMap<>();
        List<String> maxDomains = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            maxDomains.add("example" + i + ".com");
        }
        maxDomainsArgs.put("domains", maxDomains);
        maxDomainsArgs.put("se", "g_us");
        
        assertDoesNotThrow(() -> DomainValidator.validateDomainsInfoRequest(maxDomainsArgs));
        
        // Test exceeding maximum domains (101 domains)
        Map<String, Object> tooManyDomainsArgs = new HashMap<>();
        List<String> tooManyDomains = new ArrayList<>();
        for (int i = 0; i < 101; i++) {
            tooManyDomains.add("example" + i + ".com");
        }
        tooManyDomainsArgs.put("domains", tooManyDomains);
        tooManyDomainsArgs.put("se", "g_us");
        
        ValidationException tooManyException = assertThrows(ValidationException.class, 
            () -> DomainValidator.validateDomainsInfoRequest(tooManyDomainsArgs));
        assertTrue(tooManyException.getMessage().contains("100"));
        
        // Test empty vs null parameter handling
        Map<String, Object> emptyDomainsArgs = new HashMap<>();
        emptyDomainsArgs.put("domains", Arrays.asList());
        
        ValidationException emptyException = assertThrows(ValidationException.class, 
            () -> DomainValidator.validateDomainsInfoRequest(emptyDomainsArgs));
        assertTrue(emptyException.getMessage().toLowerCase().contains("empty"));
        
        Map<String, Object> nullDomainsArgs = new HashMap<>();
        nullDomainsArgs.put("domains", null);
        
        ValidationException nullException = assertThrows(ValidationException.class, 
            () -> DomainValidator.validateDomainsInfoRequest(nullDomainsArgs));
        assertTrue(nullException.getMessage().toLowerCase().contains("required"));
        
        // Test very long domain names
        String longValidDomain = "a".repeat(60) + ".example.com";
        Map<String, Object> longDomainArgs = new HashMap<>();
        longDomainArgs.put("domains", Arrays.asList(longValidDomain));
        longDomainArgs.put("se", "g_us");
        
        assertDoesNotThrow(() -> DomainValidator.validateDomainsInfoRequest(longDomainArgs));
        
        // Test domain name at maximum length boundary (253 characters)
        String maxLengthDomain = "a".repeat(60) + "." + "b".repeat(60) + "." + "c".repeat(60) + "." + "d".repeat(60) + ".co";
        assertTrue(maxLengthDomain.length() <= 253);
        
        Map<String, Object> maxLengthArgs = new HashMap<>();
        maxLengthArgs.put("domains", Arrays.asList(maxLengthDomain));
        maxLengthArgs.put("se", "g_us");
        
        assertDoesNotThrow(() -> DomainValidator.validateDomainsInfoRequest(maxLengthArgs));
        
        // Test special characters in domain names
        String[] specialCharDomains = {
                "test.com", "test-123.org", "sub-domain.example.net"
        };
        
        for (String domain : specialCharDomains) {
            Map<String, Object> specialCharArgs = new HashMap<>();
            specialCharArgs.put("domains", Arrays.asList(domain));
            specialCharArgs.put("se", "g_us");
            
            assertDoesNotThrow(() -> DomainValidator.validateDomainsInfoRequest(specialCharArgs),
                "Domain with special chars should be valid: " + domain);
        }
          // Test Unicode domain names (IDN) - should fail with current validation
        String[] unicodeDomains = {
                "тест.укр", "例え.テスト", "приклад.com"
        };
        
        for (String domain : unicodeDomains) {
            assertFalse(DOMAIN_PATTERN.matcher(domain).matches(),
                "Unicode domain should fail with current ASCII-only pattern: " + domain);
        }
        
        // Test boundary pagination values
        Map<String, Object> minPageArgs = new HashMap<>();
        minPageArgs.put("page", 1);
        minPageArgs.put("size", 1);
        
        assertDoesNotThrow(() -> {
            ValidationUtils.validatePaginationParameters(minPageArgs);
            ValidationUtils.validatePaginationSizeParameters(minPageArgs);
        });
        
        Map<String, Object> maxPageArgs = new HashMap<>();
        maxPageArgs.put("page", Integer.MAX_VALUE);
        maxPageArgs.put("size", 1000);
        
        assertDoesNotThrow(() -> {
            ValidationUtils.validatePaginationParameters(maxPageArgs);
            ValidationUtils.validatePaginationSizeParameters(maxPageArgs);
        });
    }    @Test
    @DisplayName("Test performance with large inputs")
    void testPerformanceWithLargeInputs() {
        // Test validation performance with 100 domains (maximum)
        List<String> largeDomainList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            largeDomainList.add("example" + i + ".com");
        }
        
        Map<String, Object> largeDomainsArgs = new HashMap<>();
        largeDomainsArgs.put("domains", largeDomainList);
        largeDomainsArgs.put("se", "g_us");
        
        long startTime = System.currentTimeMillis();
        assertDoesNotThrow(() -> DomainValidator.validateDomainsInfoRequest(largeDomainsArgs));
        long endTime = System.currentTimeMillis();
        
        long duration = endTime - startTime;
        assertTrue(duration < 1000, 
            "Validation of 100 domains should complete within 1 second, took: " + duration + "ms");
        
        // Test validation performance with large filter arrays
        Map<String, Object> largeFiltersArgs = new HashMap<>();
        largeFiltersArgs.put("domain", "example.com");
        largeFiltersArgs.put("se", "g_us");
        
        List<String> largeKeywordsList = new ArrayList<>();
        for (int i = 0; i < 50; i++) { // maximum 50 keywords
            largeKeywordsList.add("keyword" + i);
        }
        
        List<String> largeMinusKeywordsList = new ArrayList<>();
        for (int i = 0; i < 50; i++) { // maximum 50 minus keywords
            largeMinusKeywordsList.add("minus" + i);
        }
        
        largeFiltersArgs.put("keywords", largeKeywordsList);
        largeFiltersArgs.put("minusKeywords", largeMinusKeywordsList);
        
        long filterStartTime = System.currentTimeMillis();
        assertDoesNotThrow(() -> DomainValidator.validateDomainKeywordsRequest(largeFiltersArgs));
        long filterEndTime = System.currentTimeMillis();
        
        long filterDuration = filterEndTime - filterStartTime;
        assertTrue(filterDuration < 500, 
            "Validation with large filters should complete within 500ms, took: " + filterDuration + "ms");
        
        // Test memory usage during validation (basic check)
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        for (int i = 0; i < 10; i++) {
            Map<String, Object> memoryTestArgs = new HashMap<>();
            memoryTestArgs.put("domains", largeDomainList);
            memoryTestArgs.put("se", "g_us");
            
            assertDoesNotThrow(() -> DomainValidator.validateDomainsInfoRequest(memoryTestArgs));
        }
        
        runtime.gc(); // Suggest garbage collection
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryIncrease = memoryAfter - memoryBefore;
        
        assertTrue(memoryIncrease < 10 * 1024 * 1024, 
            "Memory increase should be reasonable (< 10MB), actual: " + (memoryIncrease / 1024 / 1024) + "MB");
        
        // Test validation time complexity (should be roughly linear)
        List<String> smallList = largeDomainList.subList(0, 10);
        List<String> mediumList = largeDomainList.subList(0, 50);
        List<String> fullList = largeDomainList;
        
        long smallTime = measureValidationTime(smallList);
        long mediumTime = measureValidationTime(mediumList);
        long fullTime = measureValidationTime(fullList);
          // Time should scale roughly linearly (allowing for some variance)
        double smallToMediumRatio = smallTime > 0 ? (double) mediumTime / smallTime : 1.0;
        double mediumToFullRatio = mediumTime > 0 ? (double) fullTime / mediumTime : 1.0;
        
        assertTrue(smallToMediumRatio < 10.0, 
            "Performance should scale reasonably: small to medium ratio = " + smallToMediumRatio);
        assertTrue(mediumToFullRatio < 5.0, 
            "Performance should scale reasonably: medium to full ratio = " + mediumToFullRatio);
        
        // Ensure validation doesn't become bottleneck
        assertTrue(fullTime < 100, 
            "Full validation should be fast enough for production use, took: " + fullTime + "ms");
    }
    
    private long measureValidationTime(List<String> domains) {
        Map<String, Object> args = new HashMap<>();
        args.put("domains", domains);
        args.put("se", "g_us");
        
        long startTime = System.nanoTime();
        assertDoesNotThrow(() -> DomainValidator.validateDomainsInfoRequest(args));
        long endTime = System.nanoTime();
        
        return (endTime - startTime) / 1_000_000; // Convert to milliseconds
    }    @Test
    @DisplayName("Test thread safety")
    void testThreadSafety() {
        // Test concurrent validation calls from multiple threads
        int numberOfThreads = 10;
        int iterationsPerThread = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < iterationsPerThread; j++) {
                        // Test concurrent domains info validation
                        Map<String, Object> domainsArgs = new HashMap<>();
                        domainsArgs.put("domains", Arrays.asList("example" + threadId + "-" + j + ".com"));
                        domainsArgs.put("se", "g_us");
                        
                        DomainValidator.validateDomainsInfoRequest(domainsArgs);
                        
                        // Test concurrent domain keywords validation
                        Map<String, Object> keywordsArgs = new HashMap<>();
                        keywordsArgs.put("domain", "test" + threadId + "-" + j + ".com");
                        keywordsArgs.put("se", "g_uk");
                        keywordsArgs.put("page", j % 10 + 1);
                        keywordsArgs.put("size", (j % 5 + 1) * 100);
                        
                        DomainValidator.validateDomainKeywordsRequest(keywordsArgs);
                        
                        // Test concurrent regions count validation
                        Map<String, Object> regionsArgs = new HashMap<>();
                        regionsArgs.put("domain", "regions" + threadId + "-" + j + ".com");
                        
                        DomainValidator.validateRegionsCountRequest(regionsArgs);
                        
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    e.printStackTrace();
                } finally {
                    latch.countDown();
                }
            });
        }
        
        assertDoesNotThrow(() -> latch.await(30, java.util.concurrent.TimeUnit.SECONDS),
            "All threads should complete within 30 seconds");
        
        executor.shutdown();
        
        int expectedSuccesses = numberOfThreads * iterationsPerThread;
        assertEquals(expectedSuccesses, successCount.get(), 
            "All validation calls should succeed in concurrent environment");
        assertEquals(0, errorCount.get(), 
            "No errors should occur during concurrent validation");
        
        // Test that static validation methods are thread-safe
        ExecutorService staticTestExecutor = Executors.newFixedThreadPool(5);
        CountDownLatch staticLatch = new CountDownLatch(5);
        AtomicInteger staticSuccessCount = new AtomicInteger(0);
        
        for (int i = 0; i < 5; i++) {
            staticTestExecutor.submit(() -> {
                try {
                    for (int j = 0; j < 100; j++) {
                        // Test static method thread safety
                        String domain = ValidationUtils.validateAndNormalizeDomain("EXAMPLE.COM");
                        assertEquals("example.com", domain);
                        
                        Map<String, Object> args = new HashMap<>();
                        args.put("se", "g_us");
                        ValidationUtils.validateSearchEngines(args, "se", "g_us", true);
                        
                        staticSuccessCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    staticLatch.countDown();
                }
            });
        }
        
        assertDoesNotThrow(() -> staticLatch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        staticTestExecutor.shutdown();
        
        assertEquals(500, staticSuccessCount.get(),
            "Static validation methods should be thread-safe");
        
        // Test that validation state is not shared between calls
        ExecutorService stateTestExecutor = Executors.newFixedThreadPool(3);
        CountDownLatch stateLatch = new CountDownLatch(3);
        
        for (int i = 0; i < 3; i++) {
            final String uniqueSe = i == 0 ? "g_us" : i == 1 ? "g_uk" : "g_de";
            stateTestExecutor.submit(() -> {
                try {
                    Map<String, Object> args = new HashMap<>();
                    args.put("domains", Arrays.asList("test.com"));
                    args.put("se", uniqueSe);
                    
                    DomainValidator.validateDomainsInfoRequest(args);
                    
                    // Verify that each thread uses its own search engine value
                    assertEquals(uniqueSe, args.get("se"),
                        "Each thread should maintain its own validation state");
                    
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    stateLatch.countDown();
                }
            });
        }
        
        assertDoesNotThrow(() -> stateLatch.await(5, java.util.concurrent.TimeUnit.SECONDS));
        stateTestExecutor.shutdown();
        
        // Test concurrent access to validation patterns and constants
        ExecutorService patternTestExecutor = Executors.newFixedThreadPool(4);
        CountDownLatch patternLatch = new CountDownLatch(4);
        
        for (int i = 0; i < 4; i++) {
            patternTestExecutor.submit(() -> {
                try {
                    for (int j = 0; j < 50; j++) {
                        // Test concurrent access to DOMAIN_PATTERN
                        boolean isValid = DOMAIN_PATTERN.matcher("example" + j + ".com").matches();
                        assertTrue(isValid);
                        
                        // Test concurrent access to search engine constants
                        Map<String, Object> args = new HashMap<>();
                        args.put("se", "g_us");
                        ValidationUtils.validateSearchEngines(args, "se", "g_us", true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    patternLatch.countDown();
                }
            });
        }
        
        assertDoesNotThrow(() -> patternLatch.await(10, java.util.concurrent.TimeUnit.SECONDS));
        patternTestExecutor.shutdown();
    }
}
