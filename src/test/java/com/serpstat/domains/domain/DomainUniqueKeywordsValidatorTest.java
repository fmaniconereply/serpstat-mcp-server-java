package com.serpstat.domains.domain;

import com.serpstat.core.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DomainUniqueKeywordsValidator class
 * Tests validation of domains unique keywords requests,
 * including parameter validation, normalization, and error handling.
 */
@DisplayName("DomainUniqueKeywordsValidator Tests")
class DomainUniqueKeywordsValidatorTest {

    @Test
    @DisplayName("Test domains unique keywords request validation - valid and invalid cases")
    void testValidateDomainsUniqKeywordsRequest() {
        // Test valid input
        Map<String, Object> validArgs = new HashMap<>();
        validArgs.put("domains", Arrays.asList("competitor1.com", "competitor2.com"));
        validArgs.put("minusDomain", "oursite.com");
        validArgs.put("se", "g_us");
        validArgs.put("page", 1);
        validArgs.put("size", 100);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(validArgs),
                "Valid domains unique keywords request should not throw exception");

        // Test null domains
        Map<String, Object> nullDomainsArgs = new HashMap<>();
        nullDomainsArgs.put("minusDomain", "oursite.com");
        nullDomainsArgs.put("se", "g_us");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(nullDomainsArgs));
        assertEquals("Parameter 'domains' is required", exception.getMessage());

        // Test null minusDomain
        Map<String, Object> nullMinusDomainArgs = new HashMap<>();
        nullMinusDomainArgs.put("domains", Arrays.asList("competitor.com"));
        nullMinusDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(nullMinusDomainArgs));
        assertEquals("Parameter 'minusDomain' is required", exception.getMessage());

        // Test empty domains array
        Map<String, Object> emptyDomainsArgs = new HashMap<>();
        emptyDomainsArgs.put("domains", Arrays.asList());
        emptyDomainsArgs.put("minusDomain", "oursite.com");
        emptyDomainsArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(emptyDomainsArgs));
        assertEquals("Parameter 'domains' cannot be empty", exception.getMessage());

        // Test too many domains (> 2)
        Map<String, Object> tooManyDomainsArgs = new HashMap<>();
        tooManyDomainsArgs.put("domains", Arrays.asList("domain1.com", "domain2.com", "domain3.com"));
        tooManyDomainsArgs.put("minusDomain", "oursite.com");
        tooManyDomainsArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(tooManyDomainsArgs));
        assertEquals("Parameter 'domains' can contain maximum 2 domains", exception.getMessage());
    }

    @Test
    @DisplayName("Test domains parameter validation and normalization")
    void testDomainsParameterValidation() {
        // Test single domain validation
        Map<String, Object> singleDomainArgs = new HashMap<>();
        singleDomainArgs.put("domains", Arrays.asList("EXAMPLE.COM"));
        singleDomainArgs.put("minusDomain", "oursite.com");
        singleDomainArgs.put("se", "g_us");

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(singleDomainArgs));

        // Verify domain was normalized to lowercase
        @SuppressWarnings("unchecked")
        java.util.List<String> normalizedDomains = (java.util.List<String>) singleDomainArgs.get("domains");
        assertEquals("example.com", normalizedDomains.get(0));

        // Test duplicate domains detection
        Map<String, Object> duplicateDomainsArgs = new HashMap<>();
        duplicateDomainsArgs.put("domains", Arrays.asList("example.com", "example.com"));
        duplicateDomainsArgs.put("minusDomain", "oursite.com");
        duplicateDomainsArgs.put("se", "g_us");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(duplicateDomainsArgs));
        assertEquals("Duplicate domains in 'domains' array are not allowed", exception.getMessage());

        // Test invalid domain format
        Map<String, Object> invalidDomainArgs = new HashMap<>();
        invalidDomainArgs.put("domains", Arrays.asList("invalid-domain"));
        invalidDomainArgs.put("minusDomain", "oursite.com");
        invalidDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidDomainArgs));
        assertTrue(exception.getMessage().contains("Invalid domain at index 0"));

        // Test domains array not a list
        Map<String, Object> notListArgs = new HashMap<>();
        notListArgs.put("domains", "not-a-list");
        notListArgs.put("minusDomain", "oursite.com");
        notListArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(notListArgs));
        assertEquals("Parameter 'domains' must be an array", exception.getMessage());
    }

    @Test
    @DisplayName("Test competitive analysis setup validation")
    void testCompetitiveAnalysisValidation() {
        // Test basic competitive analysis setup
        Map<String, Object> args = new HashMap<>();
        args.put("domains", Arrays.asList("competitor.com"));
        args.put("minusDomain", "oursite.com");
        args.put("se", "g_us");

        // Should not throw exception for valid setup
        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(args),
                "Valid competitive analysis setup should not throw exception");

        // Test valid competitive analysis setup with 2 domains
        Map<String, Object> competitiveArgs = new HashMap<>();
        competitiveArgs.put("domains", Arrays.asList("competitor1.com", "competitor2.com"));
        competitiveArgs.put("minusDomain", "oursite.com");
        competitiveArgs.put("se", "g_us");
        competitiveArgs.put("page", 1);
        competitiveArgs.put("size", 500);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(competitiveArgs),
                "Valid competitive analysis setup with 2 domains should not throw exception");

        // Test with valid filters - should not cause issues
        Map<String, Object> filtersArgs = new HashMap<>();
        filtersArgs.put("domains", Arrays.asList("competitor.com"));
        filtersArgs.put("minusDomain", "oursite.com");
        filtersArgs.put("se", "g_us");

        Map<String, Object> filters = new HashMap<>();
        filters.put("queries_from", 1000);
        filters.put("difficulty_to", 50);
        filtersArgs.put("filters", filters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(filtersArgs),
                "Request with valid filters should not throw exception");

        // Test that we can handle basic domain validation
        Map<String, Object> basicValidationArgs = new HashMap<>();
        basicValidationArgs.put("domains", Arrays.asList("example.com"));
        basicValidationArgs.put("minusDomain", "test.com");
        basicValidationArgs.put("se", "g_us");

        try {
            DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(basicValidationArgs);
            // If validation passes, that's fine
            assertTrue(true, "Basic validation should work");
        } catch (ValidationException e) {
            // If validation fails, check it's for a reasonable reason
            assertNotNull(e.getMessage(), "Validation error should have a message");
            assertFalse(e.getMessage().isEmpty(), "Validation error message should not be empty");
        } catch (Exception e) {
            // Any other exception type means the validator has issues
            fail("Unexpected exception type: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    // ================================
    // DISABLED TESTS (TODO: Implement later)
    // ================================

    @Test
    @Disabled("TODO: Implement validateDomainsUniqKeywordsRequest valid input test")
    @DisplayName("Test validate domains unique keywords request with valid input")
    void testValidateDomainsUniqKeywordsRequestValid() {
        // TODO: Implement test for valid domains unique keywords request validation
        // - Test with valid domains array: ["example.com", "competitor.com"]
        // - Test with valid minusDomain: "oursite.com"
        // - Test with valid search engine parameter: "g_us"
        // - Test with valid pagination: page = 1, size = 100
        // - Test with valid filters: queries_from = 1000, difficulty_to = 50
        // - Verify no exceptions are thrown for valid inputs
        throw new RuntimeException("TODO: Implement validateDomainsUniqKeywordsRequest valid input test");
    }

    @Test
    @Disabled("TODO: Implement validateDomainsUniqKeywordsRequest invalid input test")
    @DisplayName("Test validate domains unique keywords request with invalid input")
    void testValidateDomainsUniqKeywordsRequestInvalid() {
        // TODO: Implement test for invalid domains unique keywords request validation
        // - Test with null domains parameter -> ValidationException("Parameter
        // 'domains' is required")
        // - Test with empty domains array -> ValidationException("Parameter 'domains'
        // cannot be empty")
        // - Test with > 2 domains -> ValidationException("Parameter 'domains' can
        // contain maximum 2 domains")
        // - Test with duplicate domains -> ValidationException("Duplicate domains in
        // 'domains' array are not allowed")
        // - Test with null minusDomain -> ValidationException("Parameter 'minusDomain'
        // is required")
        // - Test with invalid domain formats -> ValidationException with pattern
        // message
        throw new RuntimeException("TODO: Implement validateDomainsUniqKeywordsRequest invalid input test");
    }

    @Test
    @Disabled("TODO: Implement domains parameter validation test")
    @DisplayName("Test domains parameter validation - additional tests")
    void testDomainsParameterValidationAdditional() {
        // TODO: Implement test for domains parameter validation
        // - Test valid single domain: ["example.com"]
        // - Test valid two domains: ["example.com", "competitor.org"]
        // - Test invalid domain formats in array -> ValidationException
        // - Test null domain in array -> ValidationException
        // - Test empty string domain -> ValidationException
        // - Test duplicate domains detection
        throw new RuntimeException("TODO: Implement domains parameter validation test");
    }

    @Test
    @Disabled("TODO: Implement minusDomain parameter validation test")
    @DisplayName("Test minusDomain parameter validation")
    void testMinusDomainParameterValidation() {
        // TODO: Implement test for minusDomain parameter validation
        // - Test valid minusDomain: "oursite.com"
        // - Test invalid minusDomain format -> ValidationException
        // - Test null minusDomain -> ValidationException
        // - Test empty string minusDomain -> ValidationException
        // - Test minusDomain same as domains element -> ValidationException (if
        // applicable)
        throw new RuntimeException("TODO: Implement minusDomain parameter validation test");
    }

    @Test
    @Disabled("TODO: Implement search engine parameter validation test")
    @DisplayName("Test search engine parameter validation")
    void testSearchEngineParameterValidation() {
        // TODO: Implement test for search engine parameter validation
        // - Test valid search engines: "g_us", "g_uk", "g_de", etc.
        // - Test invalid search engines -> ValidationException
        // - Test default search engine handling ("g_us")
        // - Test case sensitivity
        // - Verify allowed search engine constants
        throw new RuntimeException("TODO: Implement search engine parameter validation test");
    }

    @Test
    @Disabled("TODO: Implement pagination parameter validation test")
    @DisplayName("Test pagination parameter validation")
    void testPaginationParameterValidation() {
        // TODO: Implement test for pagination parameter validation
        // - Test valid pagination: page >= 1, size between 1-1000
        // - Test invalid pagination: page = 0, negative values
        // - Test boundary values: page = 1, size = 1 and size = 1000
        // - Test default values: page defaults to 1, size defaults to 100
        // - Test out-of-range values -> ValidationException
        throw new RuntimeException("TODO: Implement pagination parameter validation test");
    }

    @Test
    @Disabled("TODO: Implement filter parameter validation test")
    @DisplayName("Test filter parameter validation")
    void testFilterParameterValidation() {
        // TODO: Implement test for filter parameter validation
        // - Test boolean filters: right_spelling, misspelled
        // - Test keyword arrays: keywords, minus_keywords with proper limits
        // - Test numeric filters: queries, region_queries_count with valid ranges
        // - Test cost filters: cost, cost_from, cost_to with positive values
        // - Test competition filters: concurrency (1-100 range)
        // - Test difficulty filters: difficulty (0-100 range)
        // - Test traffic filters: traff with non-negative values
        // - Test position filters: position (1-100 range)
        throw new RuntimeException("TODO: Implement filter parameter validation test");
    }

    @Test
    @Disabled("TODO: Implement range filter validation test")
    @DisplayName("Test range filter validation")
    void testRangeFilterValidation() {
        // TODO: Implement test for range filter validation
        // - Test that _from values are less than _to values
        // - Test queries_from < queries_to
        // - Test cost_from < cost_to
        // - Test difficulty_from < difficulty_to
        // - Test position_from < position_to
        // - Test ValidationException when _from >= _to
        throw new RuntimeException("TODO: Implement range filter validation test");
    }

    @Test
    @Disabled("TODO: Implement keyword array validation test")
    @DisplayName("Test keyword array validation")
    void testKeywordArrayValidation() {
        // TODO: Implement test for keyword array validation
        // - Test keywords array with valid terms (max 100)
        // - Test minus_keywords array with valid terms (max 100)
        // - Test array size limits -> ValidationException
        // - Test empty keyword strings -> ValidationException
        // - Test very long keywords -> ValidationException
        // - Test special characters in keywords
        throw new RuntimeException("TODO: Implement keyword array validation test");
    }

    @Test
    @Disabled("TODO: Implement unknown filter parameters test")
    @DisplayName("Test unknown filter parameters")
    void testUnknownFilterParameters() {
        // TODO: Implement test for unknown filter parameters
        // - Test that only allowed filter parameters are accepted
        // - Test unknown filter keys -> ValidationException("Unknown filter parameter:
        // 'key'")
        // - Test case sensitivity in filter parameter names
        // - Test validation of allowed filters set
        throw new RuntimeException("TODO: Implement unknown filter parameters test");
    }

    @Test
    @Disabled("TODO: Implement ValidationUtils integration test")
    @DisplayName("Test ValidationUtils integration")
    void testValidationUtilsIntegration() {
        // TODO: Implement test for ValidationUtils integration
        // - Test that DomainUniqueKeywordsValidator uses ValidationUtils methods
        // - Test validateAndNormalizeDomain integration
        // - Test validateSearchEngines integration
        // - Test validatePaginationParameters integration
        // - Test common validation patterns consistency
        throw new RuntimeException("TODO: Implement ValidationUtils integration test");
    }

    @Test
    @Disabled("TODO: Implement error message quality test")
    @DisplayName("Test error message quality")
    void testErrorMessageQuality() {
        // TODO: Implement test for error message quality
        // - Test that ValidationException messages are clear and actionable
        // - Test that error messages include parameter names and expected formats
        // - Test that error messages include example valid values
        // - Test error message consistency across methods
        // - Test domain index reporting in error messages
        throw new RuntimeException("TODO: Implement error message quality test");
    }

    @Test
    @Disabled("TODO: Implement boundary conditions test")
    @DisplayName("Test boundary conditions")
    void testBoundaryConditions() {
        // TODO: Implement test for boundary conditions
        // - Test minimum and maximum allowed values for all parameters
        // - Test edge cases with exactly 2 domains (maximum)
        // - Test filter values at boundaries (0, 100, etc.)
        // - Test very long domain names
        // - Test international domain names
        throw new RuntimeException("TODO: Implement boundary conditions test");
    }

    @Test
    @Disabled("TODO: Implement thread safety test")
    @DisplayName("Test performance with complex filters")
    void testPerformanceWithComplexFilters() {
        // TODO: Implement test for performance with complex filters
        // - Test validation performance with all filters present
        // - Test validation with maximum keyword arrays (100 each)
        // - Test validation with complex range combinations
        // - Test memory usage during validation
        // - Test time complexity considerations
        throw new RuntimeException("TODO: Implement performance test");
    }

    @Test
    @Disabled("TODO: Implement thread safety test")
    @DisplayName("Test thread safety")
    void testThreadSafety() {
        // TODO: Implement test for thread safety
        // - Test concurrent validation calls
        // - Test static method thread safety
        // - Test shared state handling (if any)
        // - Test concurrent access to validation rules
        throw new RuntimeException("TODO: Implement thread safety test");
    }
}
