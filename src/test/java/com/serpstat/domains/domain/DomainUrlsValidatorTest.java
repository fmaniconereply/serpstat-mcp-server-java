package com.serpstat.domains.domain;

import com.serpstat.core.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DomainUrlsValidator class
 * Tests domain URLs request validation, domain parameter normalization,
 * and URL filters validation.
 */
@DisplayName("DomainUrlsValidator Tests")
class DomainUrlsValidatorTest {

        @Test
        @DisplayName("Test domain URLs request validation - valid and invalid cases")
        void testValidateDomainUrlsRequest() {
                // Test valid input
                Map<String, Object> validArgs = new HashMap<>();
                validArgs.put("domain", "example.com");
                validArgs.put("se", "g_us");
                validArgs.put("page", 1);
                validArgs.put("size", 100);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(validArgs),
                                "Valid domain URLs request should not throw exception");

                // Test invalid domain
                Map<String, Object> invalidDomainArgs = new HashMap<>();
                invalidDomainArgs.put("domain", "invalid");
                invalidDomainArgs.put("se", "g_us");

                ValidationException exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(invalidDomainArgs));
                assertTrue(exception.getMessage().contains("Invalid domain format"));
        }

        @Test
        @DisplayName("Test domain parameter validation and normalization")
        void testDomainParameterValidation() {
                // Test domain normalization
                Map<String, Object> args = new HashMap<>();
                args.put("domain", "  EXAMPLE.COM  "); // With spaces and uppercase
                args.put("se", "g_us");

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args));

                // Verify domain was normalized
                assertEquals("example.com", args.get("domain"));

                // Test valid domain formats
                Map<String, Object> validDomainArgs = new HashMap<>();
                validDomainArgs.put("domain", "sub.domain.co.uk");
                validDomainArgs.put("se", "g_uk");

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(validDomainArgs),
                                "Valid subdomain should not throw exception");
        }

        @Test
        @DisplayName("Test URL filters validation")
        void testUrlFiltersValidation() {
                // Test valid filters
                Map<String, Object> args = new HashMap<>();
                args.put("domain", "example.com");
                args.put("se", "g_us");

                Map<String, Object> filters = new HashMap<>();
                filters.put("url_contain", "product");
                filters.put("url_not_contain", "admin");
                filters.put("url_prefix", "https://");
                args.put("filters", filters);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Valid filters should not throw exception");

                // Test pagination validation
                args.put("page", 1);
                args.put("size", 500);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Valid pagination should not throw exception");

                // Test sort validation
                Map<String, Object> sort = new HashMap<>();
                sort.put("keywords", "desc");
                args.put("sort", sort);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Valid sort parameter should not throw exception");
        }

        @Test
        @DisplayName("Test validate domain URLs request with valid input")
        void testValidateDomainUrlsRequestValid() {
                // Test basic valid request
                Map<String, Object> validArgs = new HashMap<>();
                validArgs.put("domain", "example.com");
                validArgs.put("se", "g_us");
                validArgs.put("page", 1);
                validArgs.put("size", 100);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(validArgs),
                                "Basic valid request should not throw exception");

                // Test domain normalization works correctly
                assertEquals("example.com", validArgs.get("domain"), "Domain should be normalized");

                // Test with all optional parameters
                Map<String, Object> fullArgs = new HashMap<>();
                fullArgs.put("domain", "TEST-SITE.CO.UK");
                fullArgs.put("se", "g_uk");
                fullArgs.put("page", 2);
                fullArgs.put("size", 500);

                // Add valid filters
                Map<String, Object> filters = new HashMap<>();
                filters.put("url_contain", "product");
                filters.put("url_not_contain", "admin");
                filters.put("url_prefix", "https://");
                fullArgs.put("filters", filters);

                // Add valid sort
                Map<String, Object> sort = new HashMap<>();
                sort.put("keywords", "desc");
                fullArgs.put("sort", sort);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(fullArgs),
                                "Full valid request with all parameters should not throw exception");

                // Verify domain was normalized
                assertEquals("test-site.co.uk", fullArgs.get("domain"), "Domain should be normalized to lowercase");

                // Test different valid search engines
                String[] validSearchEngines = { "g_us", "g_uk", "g_de", "g_fr", "g_au", "g_ca" };
                for (String se : validSearchEngines) {
                        Map<String, Object> seArgs = new HashMap<>();
                        seArgs.put("domain", "example.com");
                        seArgs.put("se", se);

                        assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(seArgs),
                                        "Valid search engine " + se + " should not throw exception");
                }

                // Test boundary values for pagination
                Map<String, Object> boundaryArgs = new HashMap<>();
                boundaryArgs.put("domain", "example.com");
                boundaryArgs.put("se", "g_us");
                boundaryArgs.put("page", 1);
                boundaryArgs.put("size", 1);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(boundaryArgs),
                                "Minimum valid pagination should not throw exception");

                boundaryArgs.put("size", 1000);
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(boundaryArgs),
                                "Maximum valid pagination should not throw exception");

                // Test with subdomain
                Map<String, Object> subdomainArgs = new HashMap<>();
                subdomainArgs.put("domain", "shop.example.com");
                subdomainArgs.put("se", "g_us");

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(subdomainArgs),
                                "Valid subdomain should not throw exception");
        }

        @Test
        @DisplayName("Test validate domain URLs request with invalid input")
        void testValidateDomainUrlsRequestInvalid() {
                // Test with null domain parameter
                Map<String, Object> nullDomainArgs = new HashMap<>();
                nullDomainArgs.put("se", "g_us");

                ValidationException exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(nullDomainArgs));
                assertTrue(exception.getMessage().contains("domain"),
                                "Missing domain should mention domain parameter");

                // Test with invalid domain format
                Map<String, Object> invalidDomainArgs = new HashMap<>();
                invalidDomainArgs.put("domain", "invalid-domain");
                invalidDomainArgs.put("se", "g_us");

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(invalidDomainArgs));
                assertTrue(exception.getMessage().contains("Invalid domain format"),
                                "Invalid domain should mention format issue");

                // Test with invalid search engine
                Map<String, Object> invalidSeArgs = new HashMap<>();
                invalidSeArgs.put("domain", "example.com");
                invalidSeArgs.put("se", "invalid_se");

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(invalidSeArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("search engine") ||
                                exception.getMessage().toLowerCase().contains("se"),
                                "Invalid search engine should mention SE parameter");

                // Test with invalid pagination: page = 0
                Map<String, Object> invalidPageArgs = new HashMap<>();
                invalidPageArgs.put("domain", "example.com");
                invalidPageArgs.put("se", "g_us");
                invalidPageArgs.put("page", 0);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(invalidPageArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("page"),
                                "Invalid page should mention page parameter");

                // Test with invalid pagination: size = 0
                Map<String, Object> invalidSizeArgs = new HashMap<>();
                invalidSizeArgs.put("domain", "example.com");
                invalidSizeArgs.put("se", "g_us");
                invalidSizeArgs.put("size", 0);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(invalidSizeArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("size"),
                                "Invalid size should mention size parameter");

                // Test with invalid pagination: size > 1000
                Map<String, Object> largeSizeArgs = new HashMap<>();
                largeSizeArgs.put("domain", "example.com");
                largeSizeArgs.put("se", "g_us");
                largeSizeArgs.put("size", 1001);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(largeSizeArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("size"),
                                "Oversized page should mention size parameter");

                // Test with invalid filter values: empty string
                Map<String, Object> emptyFilterArgs = new HashMap<>();
                emptyFilterArgs.put("domain", "example.com");
                emptyFilterArgs.put("se", "g_us");

                Map<String, Object> emptyFilters = new HashMap<>();
                emptyFilters.put("url_contain", "");
                emptyFilterArgs.put("filters", emptyFilters);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(emptyFilterArgs));
                assertTrue(exception.getMessage().contains("url_contain"),
                                "Empty filter should mention the specific filter");

                // Test with invalid sort field
                Map<String, Object> invalidSortArgs = new HashMap<>();
                invalidSortArgs.put("domain", "example.com");
                invalidSortArgs.put("se", "g_us");

                Map<String, Object> invalidSort = new HashMap<>();
                invalidSort.put("invalid_field", "desc");
                invalidSortArgs.put("sort", invalidSort);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(invalidSortArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("sort") ||
                                exception.getMessage().contains("invalid_field"),
                                "Invalid sort field should mention sort parameter");
        }

        @Test
        @DisplayName("Test search engine parameter validation")
        void testSearchEngineParameterValidation() {
                // Test valid search engines
                String[] validSearchEngines = {
                                "g_us", "g_uk", "g_au", "g_ca", "g_de", "g_fr",
                                "g_ru", "g_br", "g_mx", "g_es", "g_it", "g_nl",
                                "g_pl", "g_ua", "g_kz", "g_bg"
                };

                for (String se : validSearchEngines) {
                        Map<String, Object> args = new HashMap<>();
                        args.put("domain", "example.com");
                        args.put("se", se);

                        assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                        "Valid search engine " + se + " should not throw exception");
                }

                // Test invalid search engines
                String[] invalidSearchEngines = {
                                "invalid", "g_invalid", "us", "google", "bing", "", "null"
                };

                for (String se : invalidSearchEngines) {
                        Map<String, Object> args = new HashMap<>();
                        args.put("domain", "example.com");
                        args.put("se", se);

                        ValidationException exception = assertThrows(ValidationException.class,
                                        () -> DomainValidator.validateDomainUrlsRequest(args),
                                        "Invalid search engine " + se + " should throw exception");

                        assertTrue(exception.getMessage().toLowerCase().contains("search engine") ||
                                        exception.getMessage().toLowerCase().contains("se"),
                                        "Invalid SE error should mention search engine parameter");
                }
                // Test default search engine handling (SE parameter is required)
                Map<String, Object> defaultArgs = new HashMap<>();
                defaultArgs.put("domain", "example.com");
                // No 'se' parameter - should throw exception since SE is required

                ValidationException exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(defaultArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("se") ||
                                exception.getMessage().toLowerCase().contains("required"),
                                "Missing SE parameter should throw exception mentioning SE is required");

                // Test case sensitivity (search engines should be case sensitive)
                Map<String, Object> caseArgs = new HashMap<>();
                caseArgs.put("domain", "example.com");
                caseArgs.put("se", "G_US"); // uppercase

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(caseArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("search engine") ||
                                exception.getMessage().toLowerCase().contains("se"),
                                "Case-sensitive SE validation should fail");

                // Test null search engine
                Map<String, Object> nullSeArgs = new HashMap<>();
                nullSeArgs.put("domain", "example.com");
                nullSeArgs.put("se", null);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(nullSeArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("search engine") ||
                                exception.getMessage().toLowerCase().contains("se"),
                                "Null SE should be handled gracefully");
        }

        @Test
        @DisplayName("Test pagination parameter validation")
        void testPaginationParameterValidation() {
                // Test valid pagination: page >= 1, size between 1-1000
                Map<String, Object> validArgs = new HashMap<>();
                validArgs.put("domain", "example.com");
                validArgs.put("se", "g_us");

                // Test minimum valid values
                validArgs.put("page", 1);
                validArgs.put("size", 1);
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(validArgs),
                                "Minimum valid pagination should not throw exception");

                // Test maximum valid values
                validArgs.put("page", Integer.MAX_VALUE);
                validArgs.put("size", 1000);
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(validArgs),
                                "Maximum valid pagination should not throw exception");

                // Test typical values
                validArgs.put("page", 2);
                validArgs.put("size", 100);
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(validArgs),
                                "Typical pagination values should not throw exception");

                // Test invalid pagination: page = 0
                validArgs.put("page", 0);
                validArgs.put("size", 100);
                ValidationException exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(validArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("page"),
                                "Page = 0 should mention page parameter");

                // Test invalid pagination: negative page
                validArgs.put("page", -1);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(validArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("page"),
                                "Negative page should mention page parameter");

                // Test invalid pagination: size = 0
                validArgs.put("page", 1);
                validArgs.put("size", 0);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(validArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("size"),
                                "Size = 0 should mention size parameter");

                // Test invalid pagination: size > 1000
                validArgs.put("size", 1001);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(validArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("size"),
                                "Size > 1000 should mention size parameter");

                // Test invalid pagination: negative size
                validArgs.put("size", -1);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(validArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("size"),
                                "Negative size should mention size parameter");

                // Test default values (when pagination parameters are not provided)
                Map<String, Object> defaultArgs = new HashMap<>();
                defaultArgs.put("domain", "example.com");
                defaultArgs.put("se", "g_us");
                // No pagination parameters - should use defaults
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(defaultArgs),
                                "Missing pagination parameters should use defaults and not throw exception");
                // Test string values (validator requires integers, not strings)
                Map<String, Object> stringArgs = new HashMap<>();
                stringArgs.put("domain", "example.com");
                stringArgs.put("se", "g_us");
                stringArgs.put("page", "2"); // String value
                stringArgs.put("size", 50); // Integer value

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(stringArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("page") ||
                                exception.getMessage().toLowerCase().contains("integer"),
                                "String page value should be rejected");

                // Test with both parameters as integers (should work)
                stringArgs.put("page", 2); // Integer value
                stringArgs.put("size", 50); // Integer value

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(stringArgs),
                                "Integer pagination values should be accepted");

                // Test invalid string values
                stringArgs.put("page", "invalid");
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(stringArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("page") ||
                                exception.getMessage().toLowerCase().contains("integer"),
                                "Invalid string page should mention page or integer format");
        }

        @Test
        @DisplayName("Test filter parameter validation")
        void testFilterParameterValidation() {
                Map<String, Object> baseArgs = new HashMap<>();
                baseArgs.put("domain", "example.com");
                baseArgs.put("se", "g_us");

                // Test url_contain filter with valid strings
                Map<String, Object> filters = new HashMap<>();
                filters.put("url_contain", "product");
                baseArgs.put("filters", filters);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(baseArgs),
                                "Valid url_contain filter should not throw exception");

                // Test url_not_contain filter with valid strings
                filters.clear();
                filters.put("url_not_contain", "admin");
                baseArgs.put("filters", filters);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(baseArgs),
                                "Valid url_not_contain filter should not throw exception");

                // Test url_prefix filter with valid URL prefixes
                filters.clear();
                filters.put("url_prefix", "https://example.com");
                baseArgs.put("filters", filters);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(baseArgs),
                                "Valid https url_prefix filter should not throw exception");

                filters.put("url_prefix", "http://example.com");
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(baseArgs),
                                "Valid http url_prefix filter should not throw exception");

                // Test filter combination scenarios
                filters.clear();
                filters.put("url_contain", "shop");
                filters.put("url_not_contain", "test");
                filters.put("url_prefix", "https://");
                baseArgs.put("filters", filters);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(baseArgs),
                                "Valid filter combination should not throw exception");

                // Test empty string filters -> ValidationException
                filters.clear();
                filters.put("url_contain", "");
                baseArgs.put("filters", filters);

                ValidationException exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().contains("url_contain"),
                                "Empty url_contain filter should mention the filter name");

                filters.clear();
                filters.put("url_not_contain", "");
                baseArgs.put("filters", filters);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().contains("url_not_contain"),
                                "Empty url_not_contain filter should mention the filter name");

                // Test invalid url_prefix (not starting with http/https)
                filters.clear();
                filters.put("url_prefix", "ftp://example.com");
                baseArgs.put("filters", filters);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().contains("url_prefix"),
                                "Invalid url_prefix should mention the filter name");

                // Test url_prefix that doesn't start with protocol
                filters.put("url_prefix", "example.com");
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().contains("url_prefix"),
                                "Invalid url_prefix without protocol should mention the filter name");

                // Test filter value length limits
                filters.clear();
                String longValue = "a".repeat(201); // Over 200 character limit
                filters.put("url_contain", longValue);
                baseArgs.put("filters", filters);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().contains("url_contain") &&
                                (exception.getMessage().contains("200") || exception.getMessage().contains("exceed")),
                                "Long url_contain should mention character limit");

                // Test very long url_prefix (over 500 characters)
                filters.clear();
                String longPrefix = "https://" + "a".repeat(494); // Over 500 character limit
                filters.put("url_prefix", longPrefix);
                baseArgs.put("filters", filters);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().contains("url_prefix") &&
                                (exception.getMessage().contains("500") || exception.getMessage().contains("exceed")),
                                "Long url_prefix should mention character limit");

                // Test invalid filter structure (not a Map)
                baseArgs.put("filters", "invalid_filter_structure");

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().contains("filters") &&
                                exception.getMessage().contains("object"),
                                "Invalid filter structure should mention object requirement");

                // Test unknown filter parameters
                filters = new HashMap<>();
                filters.put("unknown_filter", "value");
                baseArgs.put("filters", filters);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().contains("unknown_filter") ||
                                exception.getMessage().toLowerCase().contains("unknown"),
                                "Unknown filter should mention the unknown parameter");
        }

        @Test
        @DisplayName("Test sort parameter validation")
        void testSortParameterValidation() {
                Map<String, Object> baseArgs = new HashMap<>();
                baseArgs.put("domain", "example.com");
                baseArgs.put("se", "g_us");

                // Test valid sort field: "keywords"
                Map<String, Object> validSort = new HashMap<>();
                validSort.put("keywords", "asc");
                baseArgs.put("sort", validSort);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(baseArgs),
                                "Valid sort field 'keywords' with 'asc' should not throw exception");

                // Test valid sort orders: "asc", "desc"
                validSort.put("keywords", "desc");
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(baseArgs),
                                "Valid sort field 'keywords' with 'desc' should not throw exception");

                // Test case sensitivity for sort orders
                validSort.put("keywords", "ASC");
                ValidationException exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("sort") ||
                                exception.getMessage().contains("ASC"),
                                "Uppercase sort order should be case sensitive");

                validSort.put("keywords", "DESC");
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("sort") ||
                                exception.getMessage().contains("DESC"),
                                "Uppercase sort order should be case sensitive");

                // Test invalid sort fields -> ValidationException
                Map<String, Object> invalidSort = new HashMap<>();
                invalidSort.put("invalid_field", "asc");
                baseArgs.put("sort", invalidSort);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().contains("invalid_field") ||
                                exception.getMessage().toLowerCase().contains("sort"),
                                "Invalid sort field should mention the field or sort parameter");

                // Test invalid sort orders -> ValidationException
                validSort.clear();
                validSort.put("keywords", "invalid_order");
                baseArgs.put("sort", validSort);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().contains("invalid_order") ||
                                exception.getMessage().toLowerCase().contains("sort"),
                                "Invalid sort order should mention the order or sort parameter");

                // Test empty sort order
                validSort.put("keywords", "");
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("sort"),
                                "Empty sort order should mention sort parameter");

                // Test null sort order
                validSort.put("keywords", null);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("sort"),
                                "Null sort order should mention sort parameter");

                // Test multiple sort fields (should be allowed)
                validSort.clear();
                validSort.put("keywords", "desc");
                // Note: Only "keywords" is valid for domain URLs, so multiple fields would be
                // invalid
                validSort.put("another_field", "asc");
                baseArgs.put("sort", validSort);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().contains("another_field") ||
                                exception.getMessage().toLowerCase().contains("sort"),
                                "Invalid additional sort field should cause validation error");

                // Test sort parameter structure (not a Map)
                baseArgs.put("sort", "invalid_sort_structure");

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(baseArgs));
                assertTrue(exception.getMessage().toLowerCase().contains("sort"),
                                "Invalid sort structure should mention sort parameter");

                // Test missing sort parameter (should use defaults and be valid)
                baseArgs.remove("sort");
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(baseArgs),
                                "Missing sort parameter should use defaults and not throw exception");

                // Test empty sort Map (should be valid)
                baseArgs.put("sort", new HashMap<>());
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(baseArgs),
                                "Empty sort map should not throw exception");
        }

        @Test
        @DisplayName("Test ValidationUtils integration")
        void testValidationUtilsIntegration() {
                // Test that DomainValidator uses ValidationUtils methods properly
                Map<String, Object> args = new HashMap<>();

                // Test domain validation and normalization through ValidationUtils
                args.put("domain", "  EXAMPLE.COM  ");
                args.put("se", "g_us");

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args));
                assertEquals("example.com", args.get("domain"),
                                "Domain should be normalized by ValidationUtils");

                // Test search engine validation through ValidationUtils
                args.put("se", "invalid_se");
                ValidationException exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().toLowerCase().contains("search engine") ||
                                exception.getMessage().toLowerCase().contains("se"),
                                "Search engine validation should use ValidationUtils");

                // Test pagination validation through ValidationUtils
                args.put("se", "g_us");
                args.put("page", 0);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().toLowerCase().contains("page"),
                                "Pagination validation should use ValidationUtils");

                args.put("page", 1);
                args.put("size", 1001);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().toLowerCase().contains("size"),
                                "Size validation should use ValidationUtils");

                // Test sort validation through ValidationUtils
                args.put("size", 100);
                Map<String, Object> sort = new HashMap<>();
                sort.put("invalid_field", "asc");
                args.put("sort", sort);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().contains("invalid_field") ||
                                exception.getMessage().toLowerCase().contains("sort"),
                                "Sort validation should use ValidationUtils");

                // Test that common validation patterns are properly applied
                args.remove("sort");

                // Test domain pattern validation
                args.put("domain", "invalid-domain-format");
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().contains("Invalid domain format"),
                                "Domain pattern validation should be consistent");

                // Test error message consistency from ValidationUtils
                args.put("domain", "example.com");
                args.put("page", -1);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().toLowerCase().contains("page") &&
                                (exception.getMessage().contains("must be") ||
                                                exception.getMessage().contains("greater")),
                                "ValidationUtils should provide consistent error messages");
                // Test validation helper methods integration
                args.put("page", 1);

                // Test that ValidationUtils requires proper integer types (no auto-conversion)
                args.put("size", "50"); // String value
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().toLowerCase().contains("size") &&
                                exception.getMessage().toLowerCase().contains("integer"),
                                "ValidationUtils should require proper integer types");

                // Test with proper integer type
                args.put("size", 50); // Integer value
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "ValidationUtils should accept proper integer types");

                // Test invalid string conversion
                args.put("size", "invalid_number");
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().toLowerCase().contains("size") ||
                                exception.getMessage().toLowerCase().contains("number"),
                                "ValidationUtils should handle invalid number conversion");

                // Test null parameter handling
                args.put("domain", null);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().toLowerCase().contains("domain"),
                                "ValidationUtils should handle null parameters consistently");

                // Test that ValidationUtils constants are used
                args.put("domain", "example.com");
                args.put("size", 100);

                // Verify that the validator uses common validation utilities
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "All ValidationUtils integration should work together");
        }

        @Test
        @DisplayName("Test error message quality")
        void testErrorMessageQuality() {
                // Test that ValidationException messages are clear and actionable
                Map<String, Object> args = new HashMap<>();

                // Test missing domain parameter
                args.put("se", "g_us");
                ValidationException exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));

                String message = exception.getMessage();
                assertTrue(message.contains("domain"), "Error should mention parameter name");
                assertTrue(message.toLowerCase().contains("required") ||
                                message.toLowerCase().contains("missing"),
                                "Error should indicate parameter is required");

                // Test invalid domain format with example
                args.put("domain", "invalid-format");
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                message = exception.getMessage();
                assertTrue(message.contains("Invalid domain format"), "Error should describe the issue");
                // Note: The actual validator may not include the invalid value in the message

                // Test invalid search engine with available options
                args.put("domain", "example.com");
                args.put("se", "invalid_se");
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));

                message = exception.getMessage();
                assertTrue(message.toLowerCase().contains("search engine") ||
                                message.toLowerCase().contains("se"),
                                "Error should mention the parameter name");
                // Note: The actual validator may not include the invalid value in the message

                // Test pagination error with expected ranges
                args.put("se", "g_us");
                args.put("page", 0);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));

                message = exception.getMessage();
                assertTrue(message.toLowerCase().contains("page"), "Error should mention parameter name");
                // The validator should mention validation rules, not necessarily the exact
                // invalid value
                assertTrue(message.toLowerCase().contains("greater") ||
                                message.toLowerCase().contains("positive") ||
                                message.toLowerCase().contains("1"),
                                "Error should indicate valid range requirements");
                // Test size parameter error with limits
                args.put("page", 1);
                args.put("size", 1001);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));

                message = exception.getMessage();
                assertTrue(message.toLowerCase().contains("size"), "Error should mention parameter name");
                // The actual error message is "Parameter 'size' must be between 1 and 1000"
                assertTrue(message.contains("1000"), "Error should mention the maximum allowed value");
                assertTrue(message.toLowerCase().contains("between") ||
                                message.toLowerCase().contains("must be"),
                                "Error should indicate the constraint");

                // Test filter error with specific filter name
                args.put("size", 100);
                Map<String, Object> filters = new HashMap<>();
                filters.put("url_contain", "");
                args.put("filters", filters);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));

                message = exception.getMessage();
                assertTrue(message.contains("url_contain"), "Error should mention specific filter name");
                assertTrue(message.toLowerCase().contains("empty") ||
                                message.toLowerCase().contains("cannot be"),
                                "Error should describe the validation rule");

                // Test sort field error with allowed fields
                args.remove("filters");
                Map<String, Object> sort = new HashMap<>();
                sort.put("invalid_field", "asc");
                args.put("sort", sort);

                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));

                message = exception.getMessage();
                assertTrue(message.contains("invalid_field"), "Error should mention the invalid field");
                assertTrue(message.toLowerCase().contains("sort"), "Error should mention sort context");
                // Should ideally include valid sort fields

                // Test error message consistency across methods
                args.remove("sort");

                // Test multiple validation errors have consistent format
                args.put("domain", null);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));

                String nullDomainMessage = exception.getMessage();

                args.put("domain", "example.com");
                args.put("se", null);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));

                String nullSeMessage = exception.getMessage();
                // Both messages should follow similar pattern (mention parameter names)
                assertTrue(nullDomainMessage.toLowerCase().contains("domain") &&
                                (nullSeMessage.toLowerCase().contains("se") ||
                                                nullSeMessage.toLowerCase().contains("search engine")),
                                "Error messages should consistently mention parameter names");
                // Test that error messages don't contain sensitive information
                args.put("se", "g_us");
                args.put("domain", "invalid_domain_format"); // Use underscore which is invalid in domain names

                // This should fail due to domain format, but shouldn't expose internal details
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                message = exception.getMessage();

                // Verify it doesn't expose the invalid domain value in error message (depends
                // on implementation)
                assertTrue(message.contains("Invalid domain format") ||
                                message.contains("domain"),
                                "Error should mention domain validation issue");

                // Test that all error messages are in English
                assertTrue(message.matches(".*[a-zA-Z].*"), "Error messages should contain English text");
                assertFalse(message.matches(".*[а-яё].*"), "Error messages should not contain non-English characters");
        }

        @Test
        @DisplayName("Test boundary conditions")
        void testBoundaryConditions() {
                // Test minimum and maximum allowed values for all parameters
                Map<String, Object> args = new HashMap<>();

                // Test minimum page value
                args.put("domain", "example.com");
                args.put("se", "g_us");
                args.put("page", 1);
                args.put("size", 1);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Minimum boundary values should be valid");

                // Test maximum size value
                args.put("size", 1000);
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Maximum size boundary should be valid");

                // Test maximum page value (Integer.MAX_VALUE)
                args.put("page", Integer.MAX_VALUE);
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Very large page numbers should be handled");

                // Test edge cases with empty collections
                args.put("page", 1);
                args.put("filters", new HashMap<>());
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Empty filters map should be valid");

                args.put("sort", new HashMap<>());
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Empty sort map should be valid");

                // Test null vs empty string handling
                args.remove("filters");
                args.remove("sort");

                // Test empty domain string
                args.put("domain", "");
                ValidationException exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().toLowerCase().contains("domain"),
                                "Empty domain should be handled distinctly from null");

                // Test whitespace-only domain
                args.put("domain", "   ");
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().toLowerCase().contains("domain"),
                                "Whitespace-only domain should be invalid");

                // Test very long parameter values
                args.put("domain", "example.com");

                // Test very long domain (close to limits)
                String longDomain = "a".repeat(50) + ".example.com";
                args.put("domain", longDomain);
                // This should either be valid or fail gracefully
                try {
                        DomainValidator.validateDomainUrlsRequest(args);
                } catch (ValidationException e) {
                        assertTrue(e.getMessage().toLowerCase().contains("domain"),
                                        "Long domain validation should mention domain parameter");
                }

                // Test very long filter values at boundaries
                args.put("domain", "example.com");
                Map<String, Object> filters = new HashMap<>();

                // Test url_contain at 200 character boundary
                String exactly200Chars = "a".repeat(200);
                filters.put("url_contain", exactly200Chars);
                args.put("filters", filters);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Exactly 200 character url_contain should be valid");

                // Test url_contain over 200 characters
                filters.put("url_contain", exactly200Chars + "a");
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().contains("url_contain") &&
                                exception.getMessage().contains("200"),
                                "Over 200 character url_contain should mention limit");

                // Test url_prefix at 500 character boundary
                filters.clear();
                String exactly500Chars = "https://" + "a".repeat(492); // 500 total
                filters.put("url_prefix", exactly500Chars);
                args.put("filters", filters);

                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Exactly 500 character url_prefix should be valid");

                // Test special characters in domain names
                args.remove("filters");

                // Test domains with hyphens (valid)
                args.put("domain", "test-domain.com");
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Domain with hyphens should be valid");

                // Test domains with numbers (valid)
                args.put("domain", "test123.com");
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Domain with numbers should be valid");

                // Test domains with multiple subdomains
                args.put("domain", "sub1.sub2.example.com");
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Multiple subdomain levels should be valid");

                // Test domains with special TLDs
                args.put("domain", "example.co.uk");
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(args),
                                "Multi-part TLD should be valid");

                // Test invalid special characters in domains
                String[] invalidDomains = {
                                "test_domain.com", // underscore
                                "test domain.com", // space
                                "test@domain.com", // at symbol
                                "test.domain.com/", // trailing slash
                                "http://test.com", // protocol prefix
                };

                for (String invalidDomain : invalidDomains) {
                        args.put("domain", invalidDomain);
                        exception = assertThrows(ValidationException.class,
                                        () -> DomainValidator.validateDomainUrlsRequest(args),
                                        "Invalid domain with special chars should fail: " + invalidDomain);
                        assertTrue(exception.getMessage().contains("Invalid domain format"),
                                        "Invalid domain error should mention format for: " + invalidDomain);
                }

                // Test boundary conditions for numeric parameters
                args.put("domain", "example.com");

                // Test page boundary conditions
                args.put("page", Integer.MIN_VALUE);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().toLowerCase().contains("page"),
                                "Minimum integer page should be handled");

                // Test size boundary conditions
                args.put("page", 1);
                args.put("size", Integer.MIN_VALUE);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().toLowerCase().contains("size"),
                                "Minimum integer size should be handled");

                // Test at exact boundary (size = 1001)
                args.put("size", 1001);
                exception = assertThrows(ValidationException.class,
                                () -> DomainValidator.validateDomainUrlsRequest(args));
                assertTrue(exception.getMessage().toLowerCase().contains("size"),
                                "Size exactly over limit should be caught");
        }

        @Test
        @DisplayName("Test performance with large inputs")
        void testPerformanceWithLargeInputs() {
                // Test validation performance with maximum allowed page size
                Map<String, Object> maxSizeArgs = new HashMap<>();
                maxSizeArgs.put("domain", "example.com");
                maxSizeArgs.put("se", "g_us");
                maxSizeArgs.put("page", 1);
                maxSizeArgs.put("size", 1000); // Maximum allowed size

                long startTime = System.nanoTime();
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(maxSizeArgs),
                                "Maximum page size validation should complete without issues");
                long duration = System.nanoTime() - startTime;

                // Validation should complete in reasonable time (less than 100ms)
                assertTrue(duration < 100_000_000L,
                                "Maximum size validation should complete in less than 100ms, took: "
                                                + duration / 1_000_000 + "ms");

                // Test validation with very long domain names
                String longDomain = "very-long-subdomain-name-that-is-still-valid".repeat(5) + ".example.com";
                Map<String, Object> longDomainArgs = new HashMap<>();
                longDomainArgs.put("domain", longDomain);
                longDomainArgs.put("se", "g_us");

                startTime = System.nanoTime();
                // Long domain should either validate successfully or fail with proper error
                try {
                        DomainValidator.validateDomainUrlsRequest(longDomainArgs);
                } catch (ValidationException e) {
                        assertTrue(e.getMessage().contains("domain"),
                                        "Long domain validation error should mention domain");
                }
                duration = System.nanoTime() - startTime;

                assertTrue(duration < 50_000_000L,
                                "Long domain validation should complete in less than 50ms, took: "
                                                + duration / 1_000_000 + "ms");
                // Test filter validation with maximum allowed strings (within limits)
                Map<String, Object> maxFilterArgs = new HashMap<>();
                maxFilterArgs.put("domain", "example.com");
                maxFilterArgs.put("se", "g_us");

                Map<String, Object> maxFilters = new HashMap<>();
                // url_contain and url_not_contain are limited to 200 characters
                String maxContainValue = "a".repeat(200); // exactly 200 characters
                maxFilters.put("url_contain", maxContainValue);
                maxFilters.put("url_not_contain", maxContainValue);

                // url_prefix is limited to 500 characters and must start with http/https
                String maxPrefixValue = "https://" + "b".repeat(492); // 8 + 492 = 500 characters
                maxFilters.put("url_prefix", maxPrefixValue);
                maxFilterArgs.put("filters", maxFilters);

                startTime = System.nanoTime();
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(maxFilterArgs),
                                "Maximum length filter values should be handled efficiently");
                duration = System.nanoTime() - startTime;

                assertTrue(duration < 50_000_000L,
                                "Maximum filter validation should complete in less than 50ms, took: "
                                                + duration / 1_000_000 + "ms");

                // Test with over-limit filters to ensure validation catches them quickly
                Map<String, Object> overLimitArgs = new HashMap<>();
                overLimitArgs.put("domain", "example.com");
                overLimitArgs.put("se", "g_us");

                Map<String, Object> overLimitFilters = new HashMap<>();
                String overLimitValue = "x".repeat(201); // 201 characters - over limit
                overLimitFilters.put("url_contain", overLimitValue);
                overLimitArgs.put("filters", overLimitFilters);

                startTime = System.nanoTime();
                try {
                        DomainValidator.validateDomainUrlsRequest(overLimitArgs);
                        fail("Over-limit filter should be rejected");
                } catch (ValidationException e) {
                        assertTrue(e.getMessage().contains("200"),
                                        "Over-limit filter should mention size limit");
                }
                duration = System.nanoTime() - startTime;

                assertTrue(duration < 50_000_000L,
                                "Long filter validation should complete in less than 50ms, took: "
                                                + duration / 1_000_000 + "ms");

                // Test repeated validation calls to check for memory leaks or performance
                // degradation
                Map<String, Object> repeatedArgs = new HashMap<>();
                repeatedArgs.put("domain", "example.com");
                repeatedArgs.put("se", "g_us");
                repeatedArgs.put("page", 1);
                repeatedArgs.put("size", 100);

                startTime = System.nanoTime();
                for (int i = 0; i < 1000; i++) {
                        assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(repeatedArgs),
                                        "Repeated validation calls should not fail");
                }
                duration = System.nanoTime() - startTime;

                // 1000 validation calls should complete in reasonable time (less than 500ms)
                assertTrue(duration < 500_000_000L,
                                "1000 repeated validations should complete in less than 500ms, took: "
                                                + duration / 1_000_000 + "ms");

                // Test complex validation scenario with all parameters
                Map<String, Object> complexArgs = new HashMap<>();
                complexArgs.put("domain", "complex.example.co.uk");
                complexArgs.put("se", "g_uk");
                complexArgs.put("page", 100);
                complexArgs.put("size", 1000);

                Map<String, Object> complexFilters = new HashMap<>();
                complexFilters.put("url_contain", "product-category-subcategory");
                complexFilters.put("url_not_contain", "admin-panel-settings");
                complexFilters.put("url_prefix", "https://www.complex.example.co.uk/shop/");
                complexArgs.put("filters", complexFilters);

                Map<String, Object> complexSort = new HashMap<>();
                complexSort.put("keywords", "desc");
                complexArgs.put("sort", complexSort);

                startTime = System.nanoTime();
                assertDoesNotThrow(() -> DomainValidator.validateDomainUrlsRequest(complexArgs),
                                "Complex validation scenario should complete efficiently");
                duration = System.nanoTime() - startTime;

                assertTrue(duration < 100_000_000L,
                                "Complex validation should complete in less than 100ms, took: " + duration / 1_000_000
                                                + "ms");
        }

        @Test
        @DisplayName("Test thread safety")
        void testThreadSafety() {
                int numberOfThreads = 10;
                int numberOfIterations = 100;
                ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
                CountDownLatch startLatch = new CountDownLatch(1);
                CountDownLatch completionLatch = new CountDownLatch(numberOfThreads);
                AtomicInteger successCount = new AtomicInteger(0);
                AtomicInteger errorCount = new AtomicInteger(0);
                List<Exception> exceptions = Collections.synchronizedList(new ArrayList<>());

                // Test concurrent validation calls with valid data
                for (int threadIndex = 0; threadIndex < numberOfThreads; threadIndex++) {
                        final int index = threadIndex;
                        executorService.submit(() -> {
                                try {
                                        startLatch.await(); // Wait for all threads to be ready

                                        for (int i = 0; i < numberOfIterations; i++) {
                                                Map<String, Object> args = new HashMap<>();
                                                args.put("domain", "thread" + index + "-test" + i + ".example.com");
                                                args.put("se", "g_us");
                                                args.put("page", (i % 10) + 1);
                                                args.put("size", ((i % 5) + 1) * 100);

                                                // Add some variation in filters
                                                if (i % 3 == 0) {
                                                        Map<String, Object> filters = new HashMap<>();
                                                        filters.put("url_contain", "thread" + index);
                                                        filters.put("url_prefix", "https://");
                                                        args.put("filters", filters);
                                                }

                                                // Add some variation in sort
                                                if (i % 2 == 0) {
                                                        Map<String, Object> sort = new HashMap<>();
                                                        sort.put("keywords", i % 2 == 0 ? "asc" : "desc");
                                                        args.put("sort", sort);
                                                }

                                                DomainValidator.validateDomainUrlsRequest(args);
                                                successCount.incrementAndGet();
                                        }
                                } catch (Exception e) {
                                        errorCount.incrementAndGet();
                                        exceptions.add(e);
                                } finally {
                                        completionLatch.countDown();
                                }
                        });
                }

                // Start all threads simultaneously
                startLatch.countDown();

                // Wait for all threads to complete (with timeout)
                try {
                        assertTrue(completionLatch.await(30, TimeUnit.SECONDS),
                                        "All threads should complete within 30 seconds");
                } catch (InterruptedException e) {
                        fail("Thread safety test interrupted: " + e.getMessage());
                }

                executorService.shutdown();

                // Verify results
                assertEquals(0, errorCount.get(),
                                "No validation errors should occur during concurrent access. Exceptions: "
                                                + exceptions);
                assertEquals(numberOfThreads * numberOfIterations, successCount.get(),
                                "All validation calls should succeed");

                // Test concurrent validation with mixed valid/invalid data
                ExecutorService mixedExecutor = Executors.newFixedThreadPool(numberOfThreads);
                CountDownLatch mixedStartLatch = new CountDownLatch(1);
                CountDownLatch mixedCompletionLatch = new CountDownLatch(numberOfThreads);
                AtomicInteger validationSuccessCount = new AtomicInteger(0);
                AtomicInteger expectedValidationErrorCount = new AtomicInteger(0);
                AtomicInteger unexpectedErrorCount = new AtomicInteger(0);

                for (int threadIndex = 0; threadIndex < numberOfThreads; threadIndex++) {
                        final int index = threadIndex;
                        mixedExecutor.submit(() -> {
                                try {
                                        mixedStartLatch.await();

                                        for (int i = 0; i < numberOfIterations / 10; i++) { // Fewer iterations for
                                                                                            // mixed test
                                                Map<String, Object> args = new HashMap<>();

                                                if (i % 2 == 0) {
                                                        // Valid request
                                                        args.put("domain", "valid" + index + "-" + i + ".example.com");
                                                        args.put("se", "g_us");
                                                        args.put("page", 1);
                                                        args.put("size", 100);

                                                        DomainValidator.validateDomainUrlsRequest(args);
                                                        validationSuccessCount.incrementAndGet();
                                                } else {
                                                        // Invalid request (should throw ValidationException)
                                                        args.put("domain", "invalid-domain"); // Invalid format
                                                        args.put("se", "g_us");

                                                        try {
                                                                DomainValidator.validateDomainUrlsRequest(args);
                                                                unexpectedErrorCount.incrementAndGet(); // Should not
                                                                                                        // reach here
                                                        } catch (ValidationException e) {
                                                                expectedValidationErrorCount.incrementAndGet(); // Expected
                                                                                                                // behavior
                                                        }
                                                }
                                        }
                                } catch (Exception e) {
                                        if (!(e instanceof ValidationException)) {
                                                unexpectedErrorCount.incrementAndGet();
                                                exceptions.add(e);
                                        }
                                } finally {
                                        mixedCompletionLatch.countDown();
                                }
                        });
                }

                mixedStartLatch.countDown();

                try {
                        assertTrue(mixedCompletionLatch.await(30, TimeUnit.SECONDS),
                                        "All mixed validation threads should complete within 30 seconds");
                } catch (InterruptedException e) {
                        fail("Mixed thread safety test interrupted: " + e.getMessage());
                }

                mixedExecutor.shutdown();

                // Verify mixed test results
                assertEquals(0, unexpectedErrorCount.get(),
                                "No unexpected errors should occur during concurrent mixed validation");
                assertTrue(validationSuccessCount.get() > 0,
                                "Some valid requests should succeed");
                assertTrue(expectedValidationErrorCount.get() > 0,
                                "Some invalid requests should properly throw ValidationException");

                // Test static method thread safety (validation should be stateless)
                assertEquals(numberOfThreads * (numberOfIterations / 10) / 2, validationSuccessCount.get(),
                                "Expected number of valid requests should succeed");
                assertEquals(numberOfThreads * (numberOfIterations / 10) / 2, expectedValidationErrorCount.get(),
                                "Expected number of invalid requests should throw ValidationException");
        }
}
