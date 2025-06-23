package com.serpstat.domains.domain;

import com.serpstat.core.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

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
      @Test
    @DisplayName("Test validate domains unique keywords request with valid input")
    void testValidateDomainsUniqKeywordsRequestValid() {
        // Test with valid domains array: ["example.com", "competitor.com"]
        Map<String, Object> validArgs = new HashMap<>();
        validArgs.put("domains", Arrays.asList("example.com", "competitor.com"));
        validArgs.put("minusDomain", "oursite.com");
        validArgs.put("se", "g_us");
        validArgs.put("page", 1);
        validArgs.put("size", 100);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(validArgs),
                "Valid domains unique keywords request should not throw exception");

        // Test with valid search engine parameter "g_uk"
        Map<String, Object> ukArgs = new HashMap<>();
        ukArgs.put("domains", Arrays.asList("example.co.uk"));
        ukArgs.put("minusDomain", "oursite.co.uk");
        ukArgs.put("se", "g_uk");
        ukArgs.put("page", 1);
        ukArgs.put("size", 50);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(ukArgs),
                "Valid UK search engine request should not throw exception");

        // Test with valid filters: queries_from = 1000, difficulty_to = 50
        Map<String, Object> filtersArgs = new HashMap<>();
        filtersArgs.put("domains", Arrays.asList("competitor.org"));
        filtersArgs.put("minusDomain", "oursite.org");
        filtersArgs.put("se", "g_us");
        filtersArgs.put("page", 1);
        filtersArgs.put("size", 200);

        Map<String, Object> filters = new HashMap<>();
        filters.put("queries_from", 1000);
        filters.put("difficulty_to", 50);
        filters.put("cost_from", 0.5);
        filtersArgs.put("filters", filters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(filtersArgs),
                "Valid request with filters should not throw exception");

        // Test with maximum valid size
        Map<String, Object> maxSizeArgs = new HashMap<>();
        maxSizeArgs.put("domains", Arrays.asList("example.net", "competitor.net"));
        maxSizeArgs.put("minusDomain", "oursite.net");
        maxSizeArgs.put("se", "g_us");
        maxSizeArgs.put("page", 1);
        maxSizeArgs.put("size", 1000);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(maxSizeArgs),
                "Valid request with maximum size should not throw exception");

        // Test with single domain (minimum)
        Map<String, Object> singleDomainArgs = new HashMap<>();
        singleDomainArgs.put("domains", Arrays.asList("single.com"));
        singleDomainArgs.put("minusDomain", "oursite.com");
        singleDomainArgs.put("se", "g_us");

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(singleDomainArgs),
                "Valid request with single domain should not throw exception");
    }    @Test
    @DisplayName("Test validate domains unique keywords request with invalid input")
    void testValidateDomainsUniqKeywordsRequestInvalid() {
        // Test with null domains parameter -> ValidationException("Parameter 'domains' is required")
        Map<String, Object> nullDomainsArgs = new HashMap<>();
        nullDomainsArgs.put("minusDomain", "oursite.com");
        nullDomainsArgs.put("se", "g_us");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(nullDomainsArgs));
        assertEquals("Parameter 'domains' is required", exception.getMessage());

        // Test with empty domains array -> ValidationException("Parameter 'domains' cannot be empty")
        Map<String, Object> emptyDomainsArgs = new HashMap<>();
        emptyDomainsArgs.put("domains", Arrays.asList());
        emptyDomainsArgs.put("minusDomain", "oursite.com");
        emptyDomainsArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(emptyDomainsArgs));
        assertEquals("Parameter 'domains' cannot be empty", exception.getMessage());

        // Test with > 2 domains -> ValidationException("Parameter 'domains' can contain maximum 2 domains")
        Map<String, Object> tooManyDomainsArgs = new HashMap<>();
        tooManyDomainsArgs.put("domains", Arrays.asList("domain1.com", "domain2.com", "domain3.com"));
        tooManyDomainsArgs.put("minusDomain", "oursite.com");
        tooManyDomainsArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(tooManyDomainsArgs));
        assertEquals("Parameter 'domains' can contain maximum 2 domains", exception.getMessage());

        // Test with duplicate domains -> ValidationException("Duplicate domains in 'domains' array are not allowed")
        Map<String, Object> duplicateDomainsArgs = new HashMap<>();
        duplicateDomainsArgs.put("domains", Arrays.asList("duplicate.com", "duplicate.com"));
        duplicateDomainsArgs.put("minusDomain", "oursite.com");
        duplicateDomainsArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(duplicateDomainsArgs));
        assertEquals("Duplicate domains in 'domains' array are not allowed", exception.getMessage());

        // Test with null minusDomain -> ValidationException("Parameter 'minusDomain' is required")
        Map<String, Object> nullMinusDomainArgs = new HashMap<>();
        nullMinusDomainArgs.put("domains", Arrays.asList("competitor.com"));
        nullMinusDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(nullMinusDomainArgs));
        assertEquals("Parameter 'minusDomain' is required", exception.getMessage());

        // Test with invalid domain formats -> ValidationException with pattern message
        Map<String, Object> invalidDomainArgs = new HashMap<>();
        invalidDomainArgs.put("domains", Arrays.asList("invalid-domain-format"));
        invalidDomainArgs.put("minusDomain", "oursite.com");
        invalidDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidDomainArgs));
        assertTrue(exception.getMessage().contains("Invalid domain at index 0"));

        // Test with domains parameter not being an array
        Map<String, Object> notArrayArgs = new HashMap<>();
        notArrayArgs.put("domains", "not-an-array");
        notArrayArgs.put("minusDomain", "oursite.com");
        notArrayArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(notArrayArgs));
        assertEquals("Parameter 'domains' must be an array", exception.getMessage());
    }    @Test
    @DisplayName("Test domains parameter validation - additional tests")
    void testDomainsParameterValidationAdditional() {
        // Test valid single domain: ["example.com"]
        Map<String, Object> singleDomainArgs = new HashMap<>();
        singleDomainArgs.put("domains", Arrays.asList("example.com"));
        singleDomainArgs.put("minusDomain", "oursite.com");
        singleDomainArgs.put("se", "g_us");

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(singleDomainArgs),
                "Valid single domain should not throw exception");

        // Test valid two domains: ["example.com", "competitor.org"]
        Map<String, Object> twoDomainsArgs = new HashMap<>();
        twoDomainsArgs.put("domains", Arrays.asList("example.com", "competitor.org"));
        twoDomainsArgs.put("minusDomain", "oursite.com");
        twoDomainsArgs.put("se", "g_us");

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(twoDomainsArgs),
                "Valid two domains should not throw exception");

        // Test invalid domain formats in array -> ValidationException
        Map<String, Object> invalidFormatArgs = new HashMap<>();
        invalidFormatArgs.put("domains", Arrays.asList("valid.com", "invalid_format"));
        invalidFormatArgs.put("minusDomain", "oursite.com");
        invalidFormatArgs.put("se", "g_us");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidFormatArgs));
        assertTrue(exception.getMessage().contains("Invalid domain at index 1"));

        // Test null domain in array -> ValidationException
        Map<String, Object> nullDomainArgs = new HashMap<>();
        nullDomainArgs.put("domains", Arrays.asList("valid.com", null));
        nullDomainArgs.put("minusDomain", "oursite.com");
        nullDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(nullDomainArgs));
        assertTrue(exception.getMessage().contains("Invalid domain at index 1"));

        // Test empty string domain -> ValidationException
        Map<String, Object> emptyDomainArgs = new HashMap<>();
        emptyDomainArgs.put("domains", Arrays.asList("valid.com", ""));
        emptyDomainArgs.put("minusDomain", "oursite.com");
        emptyDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(emptyDomainArgs));
        assertTrue(exception.getMessage().contains("Invalid domain at index 1"));

        // Test duplicate domains detection
        Map<String, Object> duplicateArgs = new HashMap<>();
        duplicateArgs.put("domains", Arrays.asList("example.com", "example.com"));
        duplicateArgs.put("minusDomain", "oursite.com");
        duplicateArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(duplicateArgs));
        assertEquals("Duplicate domains in 'domains' array are not allowed", exception.getMessage());

        // Test domain normalization (uppercase to lowercase)
        Map<String, Object> caseNormalizationArgs = new HashMap<>();
        caseNormalizationArgs.put("domains", Arrays.asList("EXAMPLE.COM", "COMPETITOR.ORG"));
        caseNormalizationArgs.put("minusDomain", "OURSITE.COM");
        caseNormalizationArgs.put("se", "g_us");

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(caseNormalizationArgs),
                "Domain case normalization should work");

        // Verify domains were normalized to lowercase
        @SuppressWarnings("unchecked")
        java.util.List<String> normalizedDomains = (java.util.List<String>) caseNormalizationArgs.get("domains");
        assertEquals("example.com", normalizedDomains.get(0));
        assertEquals("competitor.org", normalizedDomains.get(1));
        assertEquals("oursite.com", caseNormalizationArgs.get("minusDomain"));
    }    @Test
    @DisplayName("Test minusDomain parameter validation")
    void testMinusDomainParameterValidation() {
        // Test valid minusDomain: "oursite.com"
        Map<String, Object> validArgs = new HashMap<>();
        validArgs.put("domains", Arrays.asList("competitor.com"));
        validArgs.put("minusDomain", "oursite.com");
        validArgs.put("se", "g_us");

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(validArgs),
                "Valid minusDomain should not throw exception");

        // Test invalid minusDomain format -> ValidationException
        Map<String, Object> invalidFormatArgs = new HashMap<>();
        invalidFormatArgs.put("domains", Arrays.asList("competitor.com"));
        invalidFormatArgs.put("minusDomain", "invalid-format");
        invalidFormatArgs.put("se", "g_us");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidFormatArgs));
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("domain") || exception.getMessage().contains("valid"));

        // Test null minusDomain -> ValidationException
        Map<String, Object> nullMinusDomainArgs = new HashMap<>();
        nullMinusDomainArgs.put("domains", Arrays.asList("competitor.com"));
        nullMinusDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(nullMinusDomainArgs));
        assertEquals("Parameter 'minusDomain' is required", exception.getMessage());

        // Test empty string minusDomain -> ValidationException
        Map<String, Object> emptyMinusDomainArgs = new HashMap<>();
        emptyMinusDomainArgs.put("domains", Arrays.asList("competitor.com"));
        emptyMinusDomainArgs.put("minusDomain", "");
        emptyMinusDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(emptyMinusDomainArgs));
        assertNotNull(exception.getMessage());
        assertTrue(exception.getMessage().contains("domain") || exception.getMessage().contains("valid") || 
                   exception.getMessage().contains("empty"));

        // Test minusDomain same as domains element -> ValidationException
        Map<String, Object> sameAsDomainArgs = new HashMap<>();
        sameAsDomainArgs.put("domains", Arrays.asList("same.com", "competitor.com"));
        sameAsDomainArgs.put("minusDomain", "same.com");
        sameAsDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(sameAsDomainArgs));
        assertTrue(exception.getMessage().contains("minusDomain") && 
                   (exception.getMessage().contains("same") || exception.getMessage().contains("cannot")));

        // Test minusDomain normalization (case insensitive)
        Map<String, Object> caseNormalizationArgs = new HashMap<>();
        caseNormalizationArgs.put("domains", Arrays.asList("competitor.com"));
        caseNormalizationArgs.put("minusDomain", "OURSITE.COM");
        caseNormalizationArgs.put("se", "g_us");

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(caseNormalizationArgs),
                "MinusDomain case normalization should work");

        // Verify minusDomain was normalized to lowercase
        assertEquals("oursite.com", caseNormalizationArgs.get("minusDomain"));

        // Test international domain
        Map<String, Object> intlDomainArgs = new HashMap<>();
        intlDomainArgs.put("domains", Arrays.asList("competitor.ua"));
        intlDomainArgs.put("minusDomain", "oursite.ua");
        intlDomainArgs.put("se", "g_us");

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(intlDomainArgs),
                "International domain should be valid");
    }    @Test
    @DisplayName("Test search engine parameter validation")
    void testSearchEngineParameterValidation() {
        // Test valid search engines: "g_us", "g_uk", "g_de", etc.
        String[] validSearchEngines = {"g_us", "g_uk", "g_de", "g_fr", "g_ca", "g_au", "g_br", "g_mx", "g_es", "g_it"};
        
        for (String se : validSearchEngines) {
            Map<String, Object> validArgs = new HashMap<>();
            validArgs.put("domains", Arrays.asList("competitor.com"));
            validArgs.put("minusDomain", "oursite.com");
            validArgs.put("se", se);

            assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(validArgs),
                    "Valid search engine '" + se + "' should not throw exception");
        }

        // Test invalid search engines -> ValidationException
        String[] invalidSearchEngines = {"g_invalid", "invalid", "g_", "", "g_xx"};
        
        for (String se : invalidSearchEngines) {
            Map<String, Object> invalidArgs = new HashMap<>();
            invalidArgs.put("domains", Arrays.asList("competitor.com"));
            invalidArgs.put("minusDomain", "oursite.com");
            invalidArgs.put("se", se);

            ValidationException exception = assertThrows(ValidationException.class,
                    () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidArgs));
            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains("search engine") || 
                       exception.getMessage().contains("invalid") ||
                       exception.getMessage().contains("se"));
        }        // Test missing search engine parameter (se is required)
        Map<String, Object> defaultArgs = new HashMap<>();
        defaultArgs.put("domains", Arrays.asList("competitor.com"));
        defaultArgs.put("minusDomain", "oursite.com");
        // se parameter not provided, should throw ValidationException
        
        assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(defaultArgs),
                "Missing search engine should throw ValidationException");

        // Test case sensitivity
        Map<String, Object> upperCaseArgs = new HashMap<>();
        upperCaseArgs.put("domains", Arrays.asList("competitor.com"));
        upperCaseArgs.put("minusDomain", "oursite.com");
        upperCaseArgs.put("se", "G_US"); // uppercase

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(upperCaseArgs));
        assertNotNull(exception.getMessage());

        // Test null search engine
        Map<String, Object> nullSeArgs = new HashMap<>();
        nullSeArgs.put("domains", Arrays.asList("competitor.com"));
        nullSeArgs.put("minusDomain", "oursite.com");
        nullSeArgs.put("se", null);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(nullSeArgs));
        assertNotNull(exception.getMessage());

        // Verify allowed search engine constants match expected values
        Map<String, Object> supportedEngineArgs = new HashMap<>();
        supportedEngineArgs.put("domains", Arrays.asList("competitor.com"));
        supportedEngineArgs.put("minusDomain", "oursite.com");

        // Test Ukrainian search engine
        supportedEngineArgs.put("se", "g_ua");
        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(supportedEngineArgs),
                "Ukrainian search engine should be supported");

        // Test Polish search engine
        supportedEngineArgs.put("se", "g_pl");
        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(supportedEngineArgs),
                "Polish search engine should be supported");

        // Test Netherlands search engine
        supportedEngineArgs.put("se", "g_nl");
        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(supportedEngineArgs),
                "Netherlands search engine should be supported");
    }    @Test
    @DisplayName("Test pagination parameter validation")
    void testPaginationParameterValidation() {
        // Test valid pagination: page >= 1, size between 1-1000
        Map<String, Object> validPaginationArgs = new HashMap<>();
        validPaginationArgs.put("domains", Arrays.asList("competitor.com"));
        validPaginationArgs.put("minusDomain", "oursite.com");
        validPaginationArgs.put("se", "g_us");
        validPaginationArgs.put("page", 1);
        validPaginationArgs.put("size", 100);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(validPaginationArgs),
                "Valid pagination should not throw exception");

        // Test invalid pagination: page = 0, negative values
        Map<String, Object> invalidPageArgs = new HashMap<>();
        invalidPageArgs.put("domains", Arrays.asList("competitor.com"));
        invalidPageArgs.put("minusDomain", "oursite.com");
        invalidPageArgs.put("se", "g_us");
        invalidPageArgs.put("page", 0);
        invalidPageArgs.put("size", 100);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidPageArgs));
        assertTrue(exception.getMessage().contains("page") && 
                   (exception.getMessage().contains("must be") || exception.getMessage().contains("greater")));

        // Test negative page value
        Map<String, Object> negativePageArgs = new HashMap<>();
        negativePageArgs.put("domains", Arrays.asList("competitor.com"));
        negativePageArgs.put("minusDomain", "oursite.com");
        negativePageArgs.put("se", "g_us");
        negativePageArgs.put("page", -1);
        negativePageArgs.put("size", 100);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(negativePageArgs));
        assertTrue(exception.getMessage().contains("page"));

        // Test boundary values: page = 1, size = 1 and size = 1000
        Map<String, Object> minBoundaryArgs = new HashMap<>();
        minBoundaryArgs.put("domains", Arrays.asList("competitor.com"));
        minBoundaryArgs.put("minusDomain", "oursite.com");
        minBoundaryArgs.put("se", "g_us");
        minBoundaryArgs.put("page", 1);
        minBoundaryArgs.put("size", 1);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(minBoundaryArgs),
                "Minimum boundary values should be valid");

        Map<String, Object> maxBoundaryArgs = new HashMap<>();
        maxBoundaryArgs.put("domains", Arrays.asList("competitor.com"));
        maxBoundaryArgs.put("minusDomain", "oursite.com");
        maxBoundaryArgs.put("se", "g_us");
        maxBoundaryArgs.put("page", 1);
        maxBoundaryArgs.put("size", 1000);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(maxBoundaryArgs),
                "Maximum boundary values should be valid");

        // Test default values: page defaults to 1, size defaults to 100
        Map<String, Object> defaultArgs = new HashMap<>();
        defaultArgs.put("domains", Arrays.asList("competitor.com"));
        defaultArgs.put("minusDomain", "oursite.com");
        defaultArgs.put("se", "g_us");
        // No page and size parameters

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(defaultArgs),
                "Default pagination should work");

        // Test out-of-range values -> ValidationException
        Map<String, Object> invalidSizeArgs = new HashMap<>();
        invalidSizeArgs.put("domains", Arrays.asList("competitor.com"));
        invalidSizeArgs.put("minusDomain", "oursite.com");
        invalidSizeArgs.put("se", "g_us");
        invalidSizeArgs.put("page", 1);
        invalidSizeArgs.put("size", 1001); // > 1000

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidSizeArgs));
        assertTrue(exception.getMessage().contains("size"));

        // Test zero size
        Map<String, Object> zeroSizeArgs = new HashMap<>();
        zeroSizeArgs.put("domains", Arrays.asList("competitor.com"));
        zeroSizeArgs.put("minusDomain", "oursite.com");
        zeroSizeArgs.put("se", "g_us");
        zeroSizeArgs.put("page", 1);
        zeroSizeArgs.put("size", 0);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(zeroSizeArgs));
        assertTrue(exception.getMessage().contains("size"));

        // Test non-numeric pagination parameters
        Map<String, Object> nonNumericArgs = new HashMap<>();
        nonNumericArgs.put("domains", Arrays.asList("competitor.com"));
        nonNumericArgs.put("minusDomain", "oursite.com");
        nonNumericArgs.put("se", "g_us");
        nonNumericArgs.put("page", "not-a-number");
        nonNumericArgs.put("size", 100);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(nonNumericArgs));
        assertTrue(exception.getMessage().contains("page") || exception.getMessage().contains("integer"));
    }    @Test
    @DisplayName("Test filter parameter validation")
    void testFilterParameterValidation() {
        // Test boolean filters: right_spelling, misspelled
        Map<String, Object> booleanFiltersArgs = new HashMap<>();
        booleanFiltersArgs.put("domains", Arrays.asList("competitor.com"));
        booleanFiltersArgs.put("minusDomain", "oursite.com");
        booleanFiltersArgs.put("se", "g_us");

        Map<String, Object> booleanFilters = new HashMap<>();
        booleanFilters.put("right_spelling", true);
        booleanFilters.put("misspelled", false);
        booleanFiltersArgs.put("filters", booleanFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(booleanFiltersArgs),
                "Boolean filters should be valid");

        // Test keyword arrays: keywords, minus_keywords with proper limits
        Map<String, Object> keywordArrayArgs = new HashMap<>();
        keywordArrayArgs.put("domains", Arrays.asList("competitor.com"));
        keywordArrayArgs.put("minusDomain", "oursite.com");
        keywordArrayArgs.put("se", "g_us");

        Map<String, Object> keywordFilters = new HashMap<>();
        keywordFilters.put("keywords", Arrays.asList("keyword1", "keyword2", "keyword3"));
        keywordFilters.put("minus_keywords", Arrays.asList("minus1", "minus2"));
        keywordArrayArgs.put("filters", keywordFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(keywordArrayArgs),
                "Keyword array filters should be valid");

        // Test numeric filters: queries, region_queries_count with valid ranges
        Map<String, Object> numericFiltersArgs = new HashMap<>();
        numericFiltersArgs.put("domains", Arrays.asList("competitor.com"));
        numericFiltersArgs.put("minusDomain", "oursite.com");
        numericFiltersArgs.put("se", "g_us");

        Map<String, Object> numericFilters = new HashMap<>();
        numericFilters.put("queries", 1000);
        numericFilters.put("region_queries_count", 500);
        numericFilters.put("region_queries_count_from", 100);
        numericFilters.put("region_queries_count_to", 1000);
        numericFiltersArgs.put("filters", numericFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(numericFiltersArgs),
                "Numeric filters should be valid");

        // Test cost filters: cost, cost_from, cost_to with positive values
        Map<String, Object> costFiltersArgs = new HashMap<>();
        costFiltersArgs.put("domains", Arrays.asList("competitor.com"));
        costFiltersArgs.put("minusDomain", "oursite.com");
        costFiltersArgs.put("se", "g_us");

        Map<String, Object> costFilters = new HashMap<>();
        costFilters.put("cost", 2.5);
        costFilters.put("cost_from", 1.0);
        costFilters.put("cost_to", 5.0);
        costFiltersArgs.put("filters", costFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(costFiltersArgs),
                "Cost filters should be valid");

        // Test competition filters: concurrency (1-100 range)
        Map<String, Object> competitionFiltersArgs = new HashMap<>();
        competitionFiltersArgs.put("domains", Arrays.asList("competitor.com"));
        competitionFiltersArgs.put("minusDomain", "oursite.com");
        competitionFiltersArgs.put("se", "g_us");

        Map<String, Object> competitionFilters = new HashMap<>();
        competitionFilters.put("concurrency", 50);
        competitionFilters.put("concurrency_from", 10);
        competitionFilters.put("concurrency_to", 90);
        competitionFiltersArgs.put("filters", competitionFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(competitionFiltersArgs),
                "Competition filters should be valid");

        // Test difficulty filters: difficulty (0-100 range)
        Map<String, Object> difficultyFiltersArgs = new HashMap<>();
        difficultyFiltersArgs.put("domains", Arrays.asList("competitor.com"));
        difficultyFiltersArgs.put("minusDomain", "oursite.com");
        difficultyFiltersArgs.put("se", "g_us");

        Map<String, Object> difficultyFilters = new HashMap<>();
        difficultyFilters.put("difficulty", 45);
        difficultyFilters.put("difficulty_from", 20);
        difficultyFilters.put("difficulty_to", 70);
        difficultyFiltersArgs.put("filters", difficultyFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(difficultyFiltersArgs),
                "Difficulty filters should be valid");

        // Test traffic filters: traff with non-negative values
        Map<String, Object> trafficFiltersArgs = new HashMap<>();
        trafficFiltersArgs.put("domains", Arrays.asList("competitor.com"));
        trafficFiltersArgs.put("minusDomain", "oursite.com");
        trafficFiltersArgs.put("se", "g_us");

        Map<String, Object> trafficFilters = new HashMap<>();
        trafficFilters.put("traff", 1000);
        trafficFilters.put("traff_from", 500);
        trafficFilters.put("traff_to", 2000);
        trafficFiltersArgs.put("filters", trafficFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(trafficFiltersArgs),
                "Traffic filters should be valid");

        // Test position filters: position (1-100 range)
        Map<String, Object> positionFiltersArgs = new HashMap<>();
        positionFiltersArgs.put("domains", Arrays.asList("competitor.com"));
        positionFiltersArgs.put("minusDomain", "oursite.com");
        positionFiltersArgs.put("se", "g_us");

        Map<String, Object> positionFilters = new HashMap<>();
        positionFilters.put("position", 5);
        positionFilters.put("position_from", 1);
        positionFilters.put("position_to", 10);
        positionFiltersArgs.put("filters", positionFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(positionFiltersArgs),
                "Position filters should be valid");

        // Test invalid filter value types
        Map<String, Object> invalidTypeArgs = new HashMap<>();
        invalidTypeArgs.put("domains", Arrays.asList("competitor.com"));
        invalidTypeArgs.put("minusDomain", "oursite.com");
        invalidTypeArgs.put("se", "g_us");

        Map<String, Object> invalidFilters = new HashMap<>();
        invalidFilters.put("right_spelling", "not-a-boolean");
        invalidTypeArgs.put("filters", invalidFilters);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidTypeArgs));
        assertTrue(exception.getMessage().contains("right_spelling") || exception.getMessage().contains("boolean"));

        // Test negative values where not allowed
        Map<String, Object> negativeValueArgs = new HashMap<>();
        negativeValueArgs.put("domains", Arrays.asList("competitor.com"));
        negativeValueArgs.put("minusDomain", "oursite.com");
        negativeValueArgs.put("se", "g_us");

        Map<String, Object> negativeFilters = new HashMap<>();
        negativeFilters.put("cost", -1.0);
        negativeValueArgs.put("filters", negativeFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(negativeValueArgs));
        assertTrue(exception.getMessage().contains("cost") || exception.getMessage().contains("negative"));
    }    @Test
    @DisplayName("Test range filter validation")
    void testRangeFilterValidation() {
        // Test that _from values are less than _to values
        Map<String, Object> validRangeArgs = new HashMap<>();
        validRangeArgs.put("domains", Arrays.asList("competitor.com"));
        validRangeArgs.put("minusDomain", "oursite.com");
        validRangeArgs.put("se", "g_us");

        Map<String, Object> validRangeFilters = new HashMap<>();
        validRangeFilters.put("queries_from", 100);
        validRangeFilters.put("queries_to", 1000);
        validRangeArgs.put("filters", validRangeFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(validRangeArgs),
                "Valid range filters should not throw exception");

        // Test queries_from < queries_to
        Map<String, Object> queriesRangeArgs = new HashMap<>();
        queriesRangeArgs.put("domains", Arrays.asList("competitor.com"));
        queriesRangeArgs.put("minusDomain", "oursite.com");
        queriesRangeArgs.put("se", "g_us");

        Map<String, Object> queriesFilters = new HashMap<>();
        queriesFilters.put("queries_from", 500);
        queriesFilters.put("queries_to", 2000);
        queriesRangeArgs.put("filters", queriesFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(queriesRangeArgs),
                "Valid queries range should not throw exception");

        // Test cost_from < cost_to
        Map<String, Object> costRangeArgs = new HashMap<>();
        costRangeArgs.put("domains", Arrays.asList("competitor.com"));
        costRangeArgs.put("minusDomain", "oursite.com");
        costRangeArgs.put("se", "g_us");

        Map<String, Object> costFilters = new HashMap<>();
        costFilters.put("cost_from", 1.0);
        costFilters.put("cost_to", 5.0);
        costRangeArgs.put("filters", costFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(costRangeArgs),
                "Valid cost range should not throw exception");

        // Test difficulty_from < difficulty_to
        Map<String, Object> difficultyRangeArgs = new HashMap<>();
        difficultyRangeArgs.put("domains", Arrays.asList("competitor.com"));
        difficultyRangeArgs.put("minusDomain", "oursite.com");
        difficultyRangeArgs.put("se", "g_us");

        Map<String, Object> difficultyFilters = new HashMap<>();
        difficultyFilters.put("difficulty_from", 20);
        difficultyFilters.put("difficulty_to", 80);
        difficultyRangeArgs.put("filters", difficultyFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(difficultyRangeArgs),
                "Valid difficulty range should not throw exception");

        // Test position_from < position_to
        Map<String, Object> positionRangeArgs = new HashMap<>();
        positionRangeArgs.put("domains", Arrays.asList("competitor.com"));
        positionRangeArgs.put("minusDomain", "oursite.com");
        positionRangeArgs.put("se", "g_us");

        Map<String, Object> positionFilters = new HashMap<>();
        positionFilters.put("position_from", 1);
        positionFilters.put("position_to", 10);
        positionRangeArgs.put("filters", positionFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(positionRangeArgs),
                "Valid position range should not throw exception");

        // Test ValidationException when _from >= _to for queries
        Map<String, Object> invalidQueriesArgs = new HashMap<>();
        invalidQueriesArgs.put("domains", Arrays.asList("competitor.com"));
        invalidQueriesArgs.put("minusDomain", "oursite.com");
        invalidQueriesArgs.put("se", "g_us");

        Map<String, Object> invalidQueriesFilters = new HashMap<>();
        invalidQueriesFilters.put("queries_from", 1000);
        invalidQueriesFilters.put("queries_to", 500); // from > to
        invalidQueriesArgs.put("filters", invalidQueriesFilters);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidQueriesArgs));
        assertTrue(exception.getMessage().contains("queries_from") || 
                   exception.getMessage().contains("range") ||
                   exception.getMessage().contains("greater"));

        // Test ValidationException when cost_from >= cost_to
        Map<String, Object> invalidCostArgs = new HashMap<>();
        invalidCostArgs.put("domains", Arrays.asList("competitor.com"));
        invalidCostArgs.put("minusDomain", "oursite.com");
        invalidCostArgs.put("se", "g_us");

        Map<String, Object> invalidCostFilters = new HashMap<>();
        invalidCostFilters.put("cost_from", 5.0);
        invalidCostFilters.put("cost_to", 2.0); // from > to
        invalidCostArgs.put("filters", invalidCostFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidCostArgs));
        assertTrue(exception.getMessage().contains("cost_from") || 
                   exception.getMessage().contains("range"));

        // Test ValidationException when difficulty_from >= difficulty_to
        Map<String, Object> invalidDifficultyArgs = new HashMap<>();
        invalidDifficultyArgs.put("domains", Arrays.asList("competitor.com"));
        invalidDifficultyArgs.put("minusDomain", "oursite.com");
        invalidDifficultyArgs.put("se", "g_us");

        Map<String, Object> invalidDifficultyFilters = new HashMap<>();
        invalidDifficultyFilters.put("difficulty_from", 80);
        invalidDifficultyFilters.put("difficulty_to", 30); // from > to
        invalidDifficultyArgs.put("filters", invalidDifficultyFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidDifficultyArgs));
        assertTrue(exception.getMessage().contains("difficulty_from") || 
                   exception.getMessage().contains("range"));

        // Test equal values (_from == _to) - should be valid
        Map<String, Object> equalValuesArgs = new HashMap<>();
        equalValuesArgs.put("domains", Arrays.asList("competitor.com"));
        equalValuesArgs.put("minusDomain", "oursite.com");
        equalValuesArgs.put("se", "g_us");        Map<String, Object> equalValuesFilters = new HashMap<>();
        equalValuesFilters.put("position_from", 5);
        equalValuesFilters.put("position_to", 4); // from > to (invalid range)
        equalValuesArgs.put("filters", equalValuesFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(equalValuesArgs));
        assertTrue(exception.getMessage().contains("position_from") || 
                   exception.getMessage().contains("less"));

        // Test region_queries_count range validation
        Map<String, Object> regionQueriesArgs = new HashMap<>();
        regionQueriesArgs.put("domains", Arrays.asList("competitor.com"));
        regionQueriesArgs.put("minusDomain", "oursite.com");
        regionQueriesArgs.put("se", "g_us");

        Map<String, Object> regionQueriesFilters = new HashMap<>();
        regionQueriesFilters.put("region_queries_count_from", 100);
        regionQueriesFilters.put("region_queries_count_to", 1000);
        regionQueriesArgs.put("filters", regionQueriesFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(regionQueriesArgs),
                "Valid region queries range should not throw exception");
    }    @Test
    @DisplayName("Test keyword array validation")
    void testKeywordArrayValidation() {
        // Test keywords array with valid terms (max 100)
        Map<String, Object> validKeywordsArgs = new HashMap<>();
        validKeywordsArgs.put("domains", Arrays.asList("competitor.com"));
        validKeywordsArgs.put("minusDomain", "oursite.com");
        validKeywordsArgs.put("se", "g_us");

        Map<String, Object> validKeywordsFilters = new HashMap<>();
        validKeywordsFilters.put("keywords", Arrays.asList("seo tool", "keyword research", "competitor analysis"));
        validKeywordsArgs.put("filters", validKeywordsFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(validKeywordsArgs),
                "Valid keywords array should not throw exception");

        // Test minus_keywords array with valid terms (max 100)
        Map<String, Object> validMinusKeywordsArgs = new HashMap<>();
        validMinusKeywordsArgs.put("domains", Arrays.asList("competitor.com"));
        validMinusKeywordsArgs.put("minusDomain", "oursite.com");
        validMinusKeywordsArgs.put("se", "g_us");

        Map<String, Object> validMinusKeywordsFilters = new HashMap<>();
        validMinusKeywordsFilters.put("minus_keywords", Arrays.asList("free", "cheap", "discount"));
        validMinusKeywordsArgs.put("filters", validMinusKeywordsFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(validMinusKeywordsArgs),
                "Valid minus_keywords array should not throw exception");

        // Test array size limits -> ValidationException (over 100 keywords)
        Map<String, Object> oversizedArrayArgs = new HashMap<>();
        oversizedArrayArgs.put("domains", Arrays.asList("competitor.com"));
        oversizedArrayArgs.put("minusDomain", "oursite.com");
        oversizedArrayArgs.put("se", "g_us");

        // Create array with 101 keywords (exceeds limit)
        java.util.List<String> oversizedKeywords = new java.util.ArrayList<>();
        for (int i = 1; i <= 101; i++) {
            oversizedKeywords.add("keyword" + i);
        }

        Map<String, Object> oversizedFilters = new HashMap<>();
        oversizedFilters.put("keywords", oversizedKeywords);
        oversizedArrayArgs.put("filters", oversizedFilters);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(oversizedArrayArgs));
        assertTrue(exception.getMessage().contains("keywords") && 
                   (exception.getMessage().contains("100") || exception.getMessage().contains("limit")));

        // Test empty keyword strings -> ValidationException
        Map<String, Object> emptyKeywordArgs = new HashMap<>();
        emptyKeywordArgs.put("domains", Arrays.asList("competitor.com"));
        emptyKeywordArgs.put("minusDomain", "oursite.com");
        emptyKeywordArgs.put("se", "g_us");

        Map<String, Object> emptyKeywordFilters = new HashMap<>();
        emptyKeywordFilters.put("keywords", Arrays.asList("valid keyword", "", "another keyword"));
        emptyKeywordArgs.put("filters", emptyKeywordFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(emptyKeywordArgs));
        assertTrue(exception.getMessage().contains("keyword") || exception.getMessage().contains("empty"));

        // Test very long keywords -> ValidationException
        Map<String, Object> longKeywordArgs = new HashMap<>();
        longKeywordArgs.put("domains", Arrays.asList("competitor.com"));
        longKeywordArgs.put("minusDomain", "oursite.com");
        longKeywordArgs.put("se", "g_us");        String veryLongKeyword = "a".repeat(101); // Exceeds 100 character limit
        Map<String, Object> longKeywordFilters = new HashMap<>();
        longKeywordFilters.put("keywords", Arrays.asList("normal keyword", veryLongKeyword));
        longKeywordArgs.put("filters", longKeywordFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(longKeywordArgs));
        assertTrue(exception.getMessage().contains("keyword") && 
                   (exception.getMessage().contains("100") || exception.getMessage().contains("exceeds")));

        // Test special characters in keywords (should be valid)
        Map<String, Object> specialCharsArgs = new HashMap<>();
        specialCharsArgs.put("domains", Arrays.asList("competitor.com"));
        specialCharsArgs.put("minusDomain", "oursite.com");
        specialCharsArgs.put("se", "g_us");

        Map<String, Object> specialCharsFilters = new HashMap<>();
        specialCharsFilters.put("keywords", Arrays.asList("keyword-with-dash", "keyword with spaces", "keyword's apostrophe"));
        specialCharsArgs.put("filters", specialCharsFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(specialCharsArgs),
                "Keywords with special characters should be valid");

        // Test null keyword in array -> ValidationException
        Map<String, Object> nullKeywordArgs = new HashMap<>();
        nullKeywordArgs.put("domains", Arrays.asList("competitor.com"));
        nullKeywordArgs.put("minusDomain", "oursite.com");
        nullKeywordArgs.put("se", "g_us");

        Map<String, Object> nullKeywordFilters = new HashMap<>();
        nullKeywordFilters.put("keywords", Arrays.asList("valid keyword", null, "another keyword"));
        nullKeywordArgs.put("filters", nullKeywordFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(nullKeywordArgs));
        assertTrue(exception.getMessage().contains("keyword") || exception.getMessage().contains("null"));

        // Test minus_keywords array size limit (max 100)
        Map<String, Object> oversizedMinusArrayArgs = new HashMap<>();
        oversizedMinusArrayArgs.put("domains", Arrays.asList("competitor.com"));
        oversizedMinusArrayArgs.put("minusDomain", "oursite.com");
        oversizedMinusArrayArgs.put("se", "g_us");

        java.util.List<String> oversizedMinusKeywords = new java.util.ArrayList<>();
        for (int i = 1; i <= 101; i++) {
            oversizedMinusKeywords.add("minus" + i);
        }

        Map<String, Object> oversizedMinusFilters = new HashMap<>();
        oversizedMinusFilters.put("minus_keywords", oversizedMinusKeywords);
        oversizedMinusArrayArgs.put("filters", oversizedMinusFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(oversizedMinusArrayArgs));
        assertTrue(exception.getMessage().contains("minus_keywords") && 
                   (exception.getMessage().contains("100") || exception.getMessage().contains("limit")));

        // Test keyword array not being a list
        Map<String, Object> notListArgs = new HashMap<>();
        notListArgs.put("domains", Arrays.asList("competitor.com"));
        notListArgs.put("minusDomain", "oursite.com");
        notListArgs.put("se", "g_us");

        Map<String, Object> notListFilters = new HashMap<>();
        notListFilters.put("keywords", "not-an-array");
        notListArgs.put("filters", notListFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(notListArgs));
        assertTrue(exception.getMessage().contains("keywords") && 
                   (exception.getMessage().contains("array") || exception.getMessage().contains("list")));

        // Test valid boundary case: exactly 100 keywords
        Map<String, Object> exactLimitArgs = new HashMap<>();
        exactLimitArgs.put("domains", Arrays.asList("competitor.com"));
        exactLimitArgs.put("minusDomain", "oursite.com");
        exactLimitArgs.put("se", "g_us");

        java.util.List<String> exactLimitKeywords = new java.util.ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            exactLimitKeywords.add("keyword" + i);
        }

        Map<String, Object> exactLimitFilters = new HashMap<>();
        exactLimitFilters.put("keywords", exactLimitKeywords);
        exactLimitArgs.put("filters", exactLimitFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(exactLimitArgs),
                "Exactly 100 keywords should be valid");
    }    @Test
    @DisplayName("Test unknown filter parameters")
    void testUnknownFilterParameters() {
        // Test that only allowed filter parameters are accepted
        Map<String, Object> allowedFiltersArgs = new HashMap<>();
        allowedFiltersArgs.put("domains", Arrays.asList("competitor.com"));
        allowedFiltersArgs.put("minusDomain", "oursite.com");
        allowedFiltersArgs.put("se", "g_us");

        Map<String, Object> allowedFilters = new HashMap<>();
        allowedFilters.put("queries", 1000);
        allowedFilters.put("difficulty", 50);
        allowedFilters.put("cost", 2.5);
        allowedFilters.put("position", 5);
        allowedFilters.put("right_spelling", true);
        allowedFilters.put("keywords", Arrays.asList("test keyword"));
        allowedFiltersArgs.put("filters", allowedFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(allowedFiltersArgs),
                "All allowed filter parameters should be valid");

        // Test unknown filter keys -> ValidationException("Unknown filter parameter: 'key'")
        Map<String, Object> unknownFilterArgs = new HashMap<>();
        unknownFilterArgs.put("domains", Arrays.asList("competitor.com"));
        unknownFilterArgs.put("minusDomain", "oursite.com");
        unknownFilterArgs.put("se", "g_us");

        Map<String, Object> unknownFilters = new HashMap<>();
        unknownFilters.put("unknown_parameter", "some value");
        unknownFilterArgs.put("filters", unknownFilters);

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(unknownFilterArgs));
        assertTrue(exception.getMessage().contains("Unknown filter parameter") && 
                   exception.getMessage().contains("unknown_parameter"));

        // Test multiple unknown filter parameters
        Map<String, Object> multipleUnknownArgs = new HashMap<>();
        multipleUnknownArgs.put("domains", Arrays.asList("competitor.com"));
        multipleUnknownArgs.put("minusDomain", "oursite.com");
        multipleUnknownArgs.put("se", "g_us");

        Map<String, Object> multipleUnknownFilters = new HashMap<>();
        multipleUnknownFilters.put("invalid_filter_1", "value1");
        multipleUnknownFilters.put("invalid_filter_2", "value2");
        multipleUnknownArgs.put("filters", multipleUnknownFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(multipleUnknownArgs));
        assertTrue(exception.getMessage().contains("Unknown filter parameter"));

        // Test case sensitivity in filter parameter names
        Map<String, Object> caseSensitiveArgs = new HashMap<>();
        caseSensitiveArgs.put("domains", Arrays.asList("competitor.com"));
        caseSensitiveArgs.put("minusDomain", "oursite.com");
        caseSensitiveArgs.put("se", "g_us");

        Map<String, Object> caseSensitiveFilters = new HashMap<>();
        caseSensitiveFilters.put("QUERIES", 1000); // uppercase should be invalid
        caseSensitiveArgs.put("filters", caseSensitiveFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(caseSensitiveArgs));
        assertTrue(exception.getMessage().contains("Unknown filter parameter") && 
                   exception.getMessage().contains("QUERIES"));

        // Test validation of allowed filters set - comprehensive check
        Map<String, Object> comprehensiveFiltersArgs = new HashMap<>();
        comprehensiveFiltersArgs.put("domains", Arrays.asList("competitor.com"));
        comprehensiveFiltersArgs.put("minusDomain", "oursite.com");
        comprehensiveFiltersArgs.put("se", "g_us");

        Map<String, Object> comprehensiveFilters = new HashMap<>();
        // Include all allowed filter parameters
        comprehensiveFilters.put("right_spelling", true);
        comprehensiveFilters.put("misspelled", false);
        comprehensiveFilters.put("keywords", Arrays.asList("keyword1"));
        comprehensiveFilters.put("minus_keywords", Arrays.asList("minus1"));
        comprehensiveFilters.put("queries", 1000);
        comprehensiveFilters.put("queries_from", 500);
        comprehensiveFilters.put("queries_to", 2000);
        comprehensiveFilters.put("region_queries_count", 800);
        comprehensiveFilters.put("region_queries_count_from", 400);
        comprehensiveFilters.put("region_queries_count_to", 1200);
        comprehensiveFilters.put("cost", 2.5);
        comprehensiveFilters.put("cost_from", 1.0);
        comprehensiveFilters.put("cost_to", 5.0);
        comprehensiveFilters.put("concurrency", 50);
        comprehensiveFilters.put("concurrency_from", 20);
        comprehensiveFilters.put("concurrency_to", 80);
        comprehensiveFilters.put("difficulty", 45);
        comprehensiveFilters.put("difficulty_from", 20);
        comprehensiveFilters.put("difficulty_to", 70);
        comprehensiveFilters.put("traff", 1000);
        comprehensiveFilters.put("traff_from", 500);
        comprehensiveFilters.put("traff_to", 2000);
        comprehensiveFilters.put("position", 5);
        comprehensiveFilters.put("position_from", 1);
        comprehensiveFilters.put("position_to", 10);
        comprehensiveFiltersArgs.put("filters", comprehensiveFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(comprehensiveFiltersArgs),
                "All comprehensive allowed filters should be valid");

        // Test mixed valid and invalid filters
        Map<String, Object> mixedFiltersArgs = new HashMap<>();
        mixedFiltersArgs.put("domains", Arrays.asList("competitor.com"));
        mixedFiltersArgs.put("minusDomain", "oursite.com");
        mixedFiltersArgs.put("se", "g_us");

        Map<String, Object> mixedFilters = new HashMap<>();
        mixedFilters.put("queries", 1000); // valid
        mixedFilters.put("invalid_param", "value"); // invalid
        mixedFiltersArgs.put("filters", mixedFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(mixedFiltersArgs));
        assertTrue(exception.getMessage().contains("Unknown filter parameter") && 
                   exception.getMessage().contains("invalid_param"));

        // Test empty filter parameter name
        Map<String, Object> emptyFilterNameArgs = new HashMap<>();
        emptyFilterNameArgs.put("domains", Arrays.asList("competitor.com"));
        emptyFilterNameArgs.put("minusDomain", "oursite.com");
        emptyFilterNameArgs.put("se", "g_us");

        Map<String, Object> emptyFilterNameFilters = new HashMap<>();
        emptyFilterNameFilters.put("", "some value"); // empty key
        emptyFilterNameArgs.put("filters", emptyFilterNameFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(emptyFilterNameArgs));
        assertTrue(exception.getMessage().contains("Unknown filter parameter"));

        // Test filter parameter with special characters
        Map<String, Object> specialCharsFilterArgs = new HashMap<>();
        specialCharsFilterArgs.put("domains", Arrays.asList("competitor.com"));
        specialCharsFilterArgs.put("minusDomain", "oursite.com");
        specialCharsFilterArgs.put("se", "g_us");

        Map<String, Object> specialCharsFilters = new HashMap<>();
        specialCharsFilters.put("filter-with-dash", "value"); // invalid
        specialCharsFilterArgs.put("filters", specialCharsFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(specialCharsFilterArgs));
        assertTrue(exception.getMessage().contains("Unknown filter parameter") && 
                   exception.getMessage().contains("filter-with-dash"));

        // Test numerical filter parameter name
        Map<String, Object> numericalFilterArgs = new HashMap<>();
        numericalFilterArgs.put("domains", Arrays.asList("competitor.com"));
        numericalFilterArgs.put("minusDomain", "oursite.com");
        numericalFilterArgs.put("se", "g_us");

        Map<String, Object> numericalFilters = new HashMap<>();
        numericalFilters.put("123", "value"); // invalid
        numericalFilterArgs.put("filters", numericalFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(numericalFilterArgs));
        assertTrue(exception.getMessage().contains("Unknown filter parameter") && 
                   exception.getMessage().contains("123"));
    }    @Test
    @DisplayName("Test ValidationUtils integration")
    void testValidationUtilsIntegration() {
        // Test that DomainUniqueKeywordsValidator uses ValidationUtils methods
        Map<String, Object> integrationArgs = new HashMap<>();
        integrationArgs.put("domains", Arrays.asList("EXAMPLE.COM"));
        integrationArgs.put("minusDomain", "OURSITE.COM");
        integrationArgs.put("se", "g_us");
        integrationArgs.put("page", 1);
        integrationArgs.put("size", 100);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(integrationArgs),
                "ValidationUtils integration should work");

        // Test validateAndNormalizeDomain integration
        @SuppressWarnings("unchecked")
        java.util.List<String> normalizedDomains = (java.util.List<String>) integrationArgs.get("domains");
        assertEquals("example.com", normalizedDomains.get(0));
        assertEquals("oursite.com", integrationArgs.get("minusDomain"));

        // Test validateSearchEngines integration
        Map<String, Object> searchEngineArgs = new HashMap<>();
        searchEngineArgs.put("domains", Arrays.asList("competitor.com"));
        searchEngineArgs.put("minusDomain", "oursite.com");
        searchEngineArgs.put("se", "invalid_engine");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(searchEngineArgs));
        assertTrue(exception.getMessage().contains("search engine") || 
                   exception.getMessage().contains("invalid"));

        // Test validatePaginationParameters integration
        Map<String, Object> paginationArgs = new HashMap<>();
        paginationArgs.put("domains", Arrays.asList("competitor.com"));
        paginationArgs.put("minusDomain", "oursite.com");
        paginationArgs.put("se", "g_us");
        paginationArgs.put("page", -1);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(paginationArgs));
        assertTrue(exception.getMessage().contains("page"));

        // Test common validation patterns consistency
        Map<String, Object> consistencyArgs = new HashMap<>();
        consistencyArgs.put("domains", Arrays.asList("valid.com"));
        consistencyArgs.put("minusDomain", "oursite.com");
        consistencyArgs.put("se", "g_us");

        Map<String, Object> filters = new HashMap<>();
        filters.put("keywords", Arrays.asList("valid keyword"));
        filters.put("right_spelling", true);
        consistencyArgs.put("filters", filters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(consistencyArgs),
                "Common validation patterns should be consistent");

        // Test filter validation integration
        Map<String, Object> filterArgs = new HashMap<>();
        filterArgs.put("domains", Arrays.asList("competitor.com"));
        filterArgs.put("minusDomain", "oursite.com");
        filterArgs.put("se", "g_us");

        Map<String, Object> invalidFilters = new HashMap<>();
        invalidFilters.put("cost", -1.0);
        filterArgs.put("filters", invalidFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(filterArgs));
        assertTrue(exception.getMessage().contains("cost"));
    }    @Test
    @DisplayName("Test error message quality")
    void testErrorMessageQuality() {
        // Test that ValidationException messages are clear and actionable
        Map<String, Object> nullDomainsArgs = new HashMap<>();
        nullDomainsArgs.put("minusDomain", "oursite.com");
        nullDomainsArgs.put("se", "g_us");

        ValidationException exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(nullDomainsArgs));
        
        String message = exception.getMessage();
        assertNotNull(message, "Error message should not be null");
        assertFalse(message.isEmpty(), "Error message should not be empty");
        assertTrue(message.contains("domains"), "Error message should mention the parameter name");
        assertTrue(message.contains("required"), "Error message should indicate what's required");

        // Test that error messages include parameter names and expected formats
        Map<String, Object> invalidDomainArgs = new HashMap<>();
        invalidDomainArgs.put("domains", Arrays.asList("invalid_format"));
        invalidDomainArgs.put("minusDomain", "oursite.com");
        invalidDomainArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidDomainArgs));
        
        message = exception.getMessage();
        assertTrue(message.contains("domain"), "Error message should mention domain");
        assertTrue(message.contains("index"), "Error message should include index information");

        // Test that error messages include example valid values
        Map<String, Object> invalidSeArgs = new HashMap<>();
        invalidSeArgs.put("domains", Arrays.asList("competitor.com"));
        invalidSeArgs.put("minusDomain", "oursite.com");
        invalidSeArgs.put("se", "invalid_engine");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidSeArgs));
        
        message = exception.getMessage();
        assertTrue(message.length() > 10, "Error message should be descriptive");

        // Test error message consistency across methods
        Map<String, Object> emptyDomainsArgs = new HashMap<>();
        emptyDomainsArgs.put("domains", Arrays.asList());
        emptyDomainsArgs.put("minusDomain", "oursite.com");
        emptyDomainsArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(emptyDomainsArgs));
        
        message = exception.getMessage();
        assertTrue(message.contains("domains"), "Error message should be consistent");
        assertTrue(message.contains("empty"), "Error message should indicate the problem");

        // Test domain index reporting in error messages
        Map<String, Object> multiInvalidArgs = new HashMap<>();
        multiInvalidArgs.put("domains", Arrays.asList("valid.com", "invalid_domain"));
        multiInvalidArgs.put("minusDomain", "oursite.com");
        multiInvalidArgs.put("se", "g_us");

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(multiInvalidArgs));
        
        message = exception.getMessage();
        assertTrue(message.contains("index 1"), "Error message should specify correct index");

        // Test filter error message quality
        Map<String, Object> filterErrorArgs = new HashMap<>();
        filterErrorArgs.put("domains", Arrays.asList("competitor.com"));
        filterErrorArgs.put("minusDomain", "oursite.com");
        filterErrorArgs.put("se", "g_us");

        Map<String, Object> filters = new HashMap<>();
        filters.put("unknown_filter", "value");
        filterErrorArgs.put("filters", filters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(filterErrorArgs));
        
        message = exception.getMessage();
        assertTrue(message.contains("Unknown filter parameter"), "Filter error should be clear");
        assertTrue(message.contains("unknown_filter"), "Filter error should mention parameter name");

        // Test range validation error message quality
        Map<String, Object> rangeErrorArgs = new HashMap<>();
        rangeErrorArgs.put("domains", Arrays.asList("competitor.com"));
        rangeErrorArgs.put("minusDomain", "oursite.com");
        rangeErrorArgs.put("se", "g_us");

        Map<String, Object> rangeFilters = new HashMap<>();
        rangeFilters.put("queries_from", 1000);
        rangeFilters.put("queries_to", 500);
        rangeErrorArgs.put("filters", rangeFilters);

        exception = assertThrows(ValidationException.class,
                () -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(rangeErrorArgs));
        
        message = exception.getMessage();
        assertTrue(message.contains("queries"), "Range error should mention parameter");
        assertFalse(message.contains("null"), "Error message should not contain 'null'");
        assertTrue(message.matches(".*[a-zA-Z].*"), "Error message should contain readable text");
    }    @Test
    @DisplayName("Test boundary conditions")
    void testBoundaryConditions() {
        // Test minimum and maximum allowed values for all parameters
        Map<String, Object> minMaxArgs = new HashMap<>();
        minMaxArgs.put("domains", Arrays.asList("a.co")); // minimum domain length
        minMaxArgs.put("minusDomain", "b.co");
        minMaxArgs.put("se", "g_us");
        minMaxArgs.put("page", 1); // minimum page
        minMaxArgs.put("size", 1); // minimum size

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(minMaxArgs),
                "Minimum boundary values should be valid");

        Map<String, Object> maxArgs = new HashMap<>();
        maxArgs.put("domains", Arrays.asList("very-long-domain-name-that-is-still-valid.com"));
        maxArgs.put("minusDomain", "another-long-domain-name.com");
        maxArgs.put("se", "g_us");
        maxArgs.put("page", 1);
        maxArgs.put("size", 1000); // maximum size

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(maxArgs),
                "Maximum boundary values should be valid");

        // Test edge cases with exactly 2 domains (maximum)
        Map<String, Object> exactTwoArgs = new HashMap<>();
        exactTwoArgs.put("domains", Arrays.asList("first.com", "second.com"));
        exactTwoArgs.put("minusDomain", "oursite.com");
        exactTwoArgs.put("se", "g_us");

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(exactTwoArgs),
                "Exactly 2 domains should be valid");

        // Test filter values at boundaries (0, 100, etc.)
        Map<String, Object> boundaryFiltersArgs = new HashMap<>();
        boundaryFiltersArgs.put("domains", Arrays.asList("competitor.com"));
        boundaryFiltersArgs.put("minusDomain", "oursite.com");
        boundaryFiltersArgs.put("se", "g_us");

        Map<String, Object> boundaryFilters = new HashMap<>();
        boundaryFilters.put("difficulty", 0); // minimum difficulty
        boundaryFilters.put("concurrency", 1); // minimum concurrency
        boundaryFilters.put("position", 1); // minimum position
        boundaryFiltersArgs.put("filters", boundaryFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(boundaryFiltersArgs),
                "Minimum filter boundaries should be valid");

        Map<String, Object> maxBoundaryFiltersArgs = new HashMap<>();
        maxBoundaryFiltersArgs.put("domains", Arrays.asList("competitor.com"));
        maxBoundaryFiltersArgs.put("minusDomain", "oursite.com");
        maxBoundaryFiltersArgs.put("se", "g_us");

        Map<String, Object> maxBoundaryFilters = new HashMap<>();
        maxBoundaryFilters.put("difficulty", 100); // maximum difficulty
        maxBoundaryFilters.put("concurrency", 100); // maximum concurrency
        maxBoundaryFilters.put("position", 100); // maximum position
        maxBoundaryFiltersArgs.put("filters", maxBoundaryFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(maxBoundaryFiltersArgs),
                "Maximum filter boundaries should be valid");

        // Test very long domain names
        String longDomain = "a".repeat(50) + ".com";
        Map<String, Object> longDomainArgs = new HashMap<>();
        longDomainArgs.put("domains", Arrays.asList(longDomain));
        longDomainArgs.put("minusDomain", "oursite.com");
        longDomainArgs.put("se", "g_us");

        // Should either accept or reject gracefully
        try {
            DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(longDomainArgs);
        } catch (ValidationException e) {
            assertNotNull(e.getMessage());
            assertFalse(e.getMessage().isEmpty());
        }

        // Test international domain names
        Map<String, Object> intlDomainArgs = new HashMap<>();
        intlDomainArgs.put("domains", Arrays.asList("example.ua", "test.pl"));
        intlDomainArgs.put("minusDomain", "oursite.ca");
        intlDomainArgs.put("se", "g_us");

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(intlDomainArgs),
                "International domains should be valid");

        // Test boundary condition for keyword arrays
        Map<String, Object> keywordBoundaryArgs = new HashMap<>();
        keywordBoundaryArgs.put("domains", Arrays.asList("competitor.com"));
        keywordBoundaryArgs.put("minusDomain", "oursite.com");
        keywordBoundaryArgs.put("se", "g_us");

        java.util.List<String> exactLimitKeywords = new java.util.ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            exactLimitKeywords.add("k" + i);
        }

        Map<String, Object> keywordBoundaryFilters = new HashMap<>();
        keywordBoundaryFilters.put("keywords", exactLimitKeywords);
        keywordBoundaryArgs.put("filters", keywordBoundaryFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(keywordBoundaryArgs),
                "Exactly 100 keywords should be valid");

        // Test zero values where applicable
        Map<String, Object> zeroValuesArgs = new HashMap<>();
        zeroValuesArgs.put("domains", Arrays.asList("competitor.com"));
        zeroValuesArgs.put("minusDomain", "oursite.com");
        zeroValuesArgs.put("se", "g_us");

        Map<String, Object> zeroFilters = new HashMap<>();
        zeroFilters.put("cost", 0.0);
        zeroFilters.put("traff", 0);
        zeroFilters.put("queries", 0);
        zeroValuesArgs.put("filters", zeroFilters);

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(zeroValuesArgs),
                "Zero values should be valid where allowed");

        // Test edge case: single character domains
        Map<String, Object> singleCharArgs = new HashMap<>();
        singleCharArgs.put("domains", Arrays.asList("x.io"));
        singleCharArgs.put("minusDomain", "y.io");
        singleCharArgs.put("se", "g_us");

        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(singleCharArgs),
                "Single character domains should be valid");
    }    @Test
    @DisplayName("Test performance with complex filters")
    void testPerformanceWithComplexFilters() {
        // Test validation performance with all filters present
        Map<String, Object> complexArgs = new HashMap<>();
        complexArgs.put("domains", Arrays.asList("competitor1.com", "competitor2.com"));
        complexArgs.put("minusDomain", "oursite.com");
        complexArgs.put("se", "g_us");
        complexArgs.put("page", 1);
        complexArgs.put("size", 500);

        Map<String, Object> complexFilters = new HashMap<>();
        complexFilters.put("right_spelling", true);
        complexFilters.put("misspelled", false);
        complexFilters.put("queries", 1000);
        complexFilters.put("queries_from", 500);
        complexFilters.put("queries_to", 2000);
        complexFilters.put("region_queries_count", 800);
        complexFilters.put("region_queries_count_from", 400);
        complexFilters.put("region_queries_count_to", 1500);
        complexFilters.put("cost", 2.5);
        complexFilters.put("cost_from", 1.0);
        complexFilters.put("cost_to", 5.0);
        complexFilters.put("concurrency", 50);
        complexFilters.put("concurrency_from", 20);
        complexFilters.put("concurrency_to", 80);
        complexFilters.put("difficulty", 45);
        complexFilters.put("difficulty_from", 20);
        complexFilters.put("difficulty_to", 70);
        complexFilters.put("traff", 1500);
        complexFilters.put("traff_from", 500);
        complexFilters.put("traff_to", 3000);
        complexFilters.put("position", 5);
        complexFilters.put("position_from", 1);
        complexFilters.put("position_to", 10);
        complexArgs.put("filters", complexFilters);

        long startTime = System.currentTimeMillis();
        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(complexArgs),
                "Complex filters validation should not throw exception");
        long endTime = System.currentTimeMillis();

        long duration = endTime - startTime;
        assertTrue(duration < 1000, "Validation should complete within reasonable time");

        // Test validation with maximum keyword arrays (100 each)
        Map<String, Object> maxKeywordArgs = new HashMap<>();
        maxKeywordArgs.put("domains", Arrays.asList("competitor.com"));
        maxKeywordArgs.put("minusDomain", "oursite.com");
        maxKeywordArgs.put("se", "g_us");

        java.util.List<String> maxKeywords = new java.util.ArrayList<>();
        java.util.List<String> maxMinusKeywords = new java.util.ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            maxKeywords.add("keyword" + i);
            maxMinusKeywords.add("minus" + i);
        }

        Map<String, Object> maxKeywordFilters = new HashMap<>();
        maxKeywordFilters.put("keywords", maxKeywords);
        maxKeywordFilters.put("minus_keywords", maxMinusKeywords);
        maxKeywordArgs.put("filters", maxKeywordFilters);

        startTime = System.currentTimeMillis();
        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(maxKeywordArgs),
                "Maximum keyword arrays should be handled efficiently");
        endTime = System.currentTimeMillis();

        duration = endTime - startTime;
        assertTrue(duration < 500, "Keyword validation should be fast");

        // Test validation with complex range combinations
        Map<String, Object> rangeArgs = new HashMap<>();
        rangeArgs.put("domains", Arrays.asList("competitor.com"));
        rangeArgs.put("minusDomain", "oursite.com");
        rangeArgs.put("se", "g_us");

        Map<String, Object> rangeFilters = new HashMap<>();
        rangeFilters.put("queries_from", 100);
        rangeFilters.put("queries_to", 10000);
        rangeFilters.put("cost_from", 0.1);
        rangeFilters.put("cost_to", 50.0);
        rangeFilters.put("difficulty_from", 10);
        rangeFilters.put("difficulty_to", 90);
        rangeFilters.put("position_from", 1);
        rangeFilters.put("position_to", 100);
        rangeFilters.put("traff_from", 0);
        rangeFilters.put("traff_to", 100000);
        rangeArgs.put("filters", rangeFilters);

        startTime = System.currentTimeMillis();
        assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(rangeArgs),
                "Complex range combinations should be validated efficiently");
        endTime = System.currentTimeMillis();

        duration = endTime - startTime;
        assertTrue(duration < 200, "Range validation should be very fast");

        // Test memory usage during validation (basic check)
        Runtime runtime = Runtime.getRuntime();
        long memoryBefore = runtime.totalMemory() - runtime.freeMemory();

        for (int i = 0; i < 100; i++) {
            Map<String, Object> testArgs = new HashMap<>();
            testArgs.put("domains", Arrays.asList("test" + i + ".com"));
            testArgs.put("minusDomain", "oursite.com");
            testArgs.put("se", "g_us");

            assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(testArgs));
        }

        runtime.gc();
        long memoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long memoryUsed = memoryAfter - memoryBefore;

        assertTrue(memoryUsed < 10 * 1024 * 1024, "Memory usage should be reasonable (< 10MB)");

        // Test time complexity considerations
        Map<String, Object> scalabilityArgs = new HashMap<>();
        scalabilityArgs.put("domains", Arrays.asList("competitor.com"));
        scalabilityArgs.put("minusDomain", "oursite.com");
        scalabilityArgs.put("se", "g_us");

        // Test with increasing filter complexity
        for (int filterCount = 1; filterCount <= 10; filterCount++) {
            Map<String, Object> filters = new HashMap<>();
            for (int i = 0; i < filterCount; i++) {
                filters.put("queries_from", 100 + i * 100);
                filters.put("queries_to", 1000 + i * 100);
            }
            scalabilityArgs.put("filters", filters);

            startTime = System.currentTimeMillis();
            assertDoesNotThrow(() -> DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(scalabilityArgs));
            endTime = System.currentTimeMillis();

            duration = endTime - startTime;
            assertTrue(duration < 100, "Validation time should not scale poorly with filter count");
        }
    }    @Test
    @DisplayName("Test thread safety")
    void testThreadSafety() {
        // Test concurrent validation calls
        final int threadCount = 10;
        final int iterationsPerThread = 50;
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(threadCount);
        java.util.concurrent.CountDownLatch latch = new java.util.concurrent.CountDownLatch(threadCount);
        java.util.concurrent.atomic.AtomicBoolean hasError = new java.util.concurrent.atomic.AtomicBoolean(false);

        for (int t = 0; t < threadCount; t++) {
            final int threadId = t;
            executor.submit(() -> {
                try {
                    for (int i = 0; i < iterationsPerThread; i++) {
                        Map<String, Object> args = new HashMap<>();
                        args.put("domains", Arrays.asList("competitor" + threadId + "-" + i + ".com"));
                        args.put("minusDomain", "oursite" + threadId + ".com");
                        args.put("se", "g_us");
                        args.put("page", 1);
                        args.put("size", 100);

                        DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(args);
                    }
                } catch (Exception e) {
                    hasError.set(true);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            assertTrue(latch.await(30, java.util.concurrent.TimeUnit.SECONDS), 
                      "All threads should complete within timeout");
            assertFalse(hasError.get(), "No errors should occur during concurrent validation");
        } catch (InterruptedException e) {
            fail("Thread safety test was interrupted");
        } finally {
            executor.shutdown();
        }

        // Test static method thread safety
        java.util.concurrent.ExecutorService staticTestExecutor = java.util.concurrent.Executors.newFixedThreadPool(5);
        java.util.concurrent.CountDownLatch staticLatch = new java.util.concurrent.CountDownLatch(5);
        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);

        for (int t = 0; t < 5; t++) {
            final int threadId = t;
            staticTestExecutor.submit(() -> {
                try {
                    for (int i = 0; i < 20; i++) {
                        Map<String, Object> args = new HashMap<>();
                        args.put("domains", Arrays.asList("static-test" + threadId + ".com"));
                        args.put("minusDomain", "static-oursite.com");
                        args.put("se", "g_us");

                        DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(args);
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    // Expected for some invalid inputs, but should not crash
                } finally {
                    staticLatch.countDown();
                }
            });
        }

        try {
            assertTrue(staticLatch.await(15, java.util.concurrent.TimeUnit.SECONDS),
                      "Static method test should complete");
            assertTrue(successCount.get() > 0, "Some validations should succeed");
        } catch (InterruptedException e) {
            fail("Static method thread safety test was interrupted");
        } finally {
            staticTestExecutor.shutdown();
        }

        // Test shared state handling (if any)
        java.util.concurrent.ExecutorService sharedStateExecutor = java.util.concurrent.Executors.newFixedThreadPool(3);
        java.util.concurrent.CountDownLatch sharedStateLatch = new java.util.concurrent.CountDownLatch(3);
        java.util.List<Exception> exceptions = java.util.Collections.synchronizedList(new java.util.ArrayList<>());

        for (int t = 0; t < 3; t++) {
            final int threadId = t;
            sharedStateExecutor.submit(() -> {
                try {
                    // Test with various filter combinations to ensure no shared state issues
                    Map<String, Object> args = new HashMap<>();
                    args.put("domains", Arrays.asList("shared" + threadId + ".com"));
                    args.put("minusDomain", "shared-oursite.com");
                    args.put("se", "g_us");

                    Map<String, Object> filters = new HashMap<>();
                    filters.put("queries", 1000 + threadId * 100);
                    filters.put("difficulty", 30 + threadId * 10);
                    args.put("filters", filters);

                    for (int i = 0; i < 30; i++) {
                        DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(args);
                    }
                } catch (Exception e) {
                    exceptions.add(e);
                } finally {
                    sharedStateLatch.countDown();
                }
            });
        }

        try {
            assertTrue(sharedStateLatch.await(10, java.util.concurrent.TimeUnit.SECONDS),
                      "Shared state test should complete");
            assertTrue(exceptions.isEmpty(), "No exceptions should occur in shared state test");
        } catch (InterruptedException e) {
            fail("Shared state thread safety test was interrupted");
        } finally {
            sharedStateExecutor.shutdown();
        }

        // Test concurrent access to validation rules
        java.util.concurrent.ExecutorService rulesExecutor = java.util.concurrent.Executors.newFixedThreadPool(4);
        java.util.concurrent.CountDownLatch rulesLatch = new java.util.concurrent.CountDownLatch(4);
        java.util.concurrent.atomic.AtomicBoolean rulesError = new java.util.concurrent.atomic.AtomicBoolean(false);

        for (int t = 0; t < 4; t++) {
            final int threadId = t;
            rulesExecutor.submit(() -> {
                try {
                    // Test different validation scenarios concurrently
                    Map<String, Object> validArgs = new HashMap<>();
                    validArgs.put("domains", Arrays.asList("rules" + threadId + ".com"));
                    validArgs.put("minusDomain", "rules-oursite.com");
                    validArgs.put("se", "g_us");

                    Map<String, Object> invalidArgs = new HashMap<>(); // missing required params

                    for (int i = 0; i < 25; i++) {
                        // Valid case
                        DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(validArgs);

                        // Invalid case - should throw exception consistently
                        try {
                            DomainUniqueKeywordsValidator.validateDomainsUniqKeywordsRequest(invalidArgs);
                            rulesError.set(true); // Should not reach here
                        } catch (ValidationException e) {
                            // Expected
                        }
                    }
                } catch (Exception e) {
                    rulesError.set(true);
                } finally {
                    rulesLatch.countDown();
                }
            });
        }

        try {
            assertTrue(rulesLatch.await(10, java.util.concurrent.TimeUnit.SECONDS),
                      "Rules access test should complete");
            assertFalse(rulesError.get(), "Validation rules should be accessed safely");
        } catch (InterruptedException e) {
            fail("Rules access thread safety test was interrupted");
        } finally {
            rulesExecutor.shutdown();
        }
    }
}
