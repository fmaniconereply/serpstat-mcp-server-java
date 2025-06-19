package com.serpstat.domains.domain;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.utils.ValidationUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validator for domain unique keywords analysis requests
 */
public class DomainUniqueKeywordsValidator {

    /**
     * Validates getDomainsUniqKeywords request parameters
     *
     * @param arguments The request arguments to validate
     * @throws ValidationException If validation fails
     */
    public static void validateDomainsUniqKeywordsRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate domains array parameter
        validateDomainsParameter(arguments);

        // Validate minusDomain parameter
        validateMinusDomainParameter(arguments);

        // Validate search engine parameter
        ValidationUtils.validateSearchEngines(arguments, "se", "g_us", true);

        // Validate pagination parameters
        ValidationUtils.validatePaginationParameters(arguments);
        ValidationUtils.validatePaginationSizeParameters(arguments);

        // Validate filters if present
        Object filtersObj = arguments.get("filters");
        if (filtersObj != null) {
            validateFiltersParameters(filtersObj);
        }
    }

    /**
     * Validates the domains array parameter
     */
    private static void validateDomainsParameter(Map<String, Object> arguments) throws ValidationException {
        Object domainsObj = arguments.get("domains");
        if (domainsObj == null) {
            throw new ValidationException("Parameter 'domains' is required");
        }

        if (!(domainsObj instanceof List)) {
            throw new ValidationException("Parameter 'domains' must be an array");
        }

        @SuppressWarnings("unchecked")
        List<String> domains = (List<String>) domainsObj;

        if (domains.isEmpty()) {
            throw new ValidationException("Parameter 'domains' cannot be empty");
        }

        if (domains.size() > 2) {
            throw new ValidationException("Parameter 'domains' can contain maximum 2 domains");
        }

        // Validate each domain
        for (int i = 0; i < domains.size(); i++) {
            String domain = domains.get(i);
            try {
                String normalizedDomain = ValidationUtils.validateAndNormalizeDomain(domain);
                domains.set(i, normalizedDomain);
            } catch (ValidationException e) {
                throw new ValidationException(String.format("Invalid domain at index %d: %s", i, e.getMessage()));
            }
        }

        // Check for duplicates
        Set<String> uniqueDomains = Set.copyOf(domains);
        if (uniqueDomains.size() != domains.size()) {
            throw new ValidationException("Duplicate domains in 'domains' array are not allowed");
        }
    }

    /**
     * Validates the minusDomain parameter
     */
    private static void validateMinusDomainParameter(Map<String, Object> arguments) throws ValidationException {
        Object minusDomainObj = arguments.get("minusDomain");
        if (minusDomainObj == null) {
            throw new ValidationException("Parameter 'minusDomain' is required");
        }

        String normalizedMinusDomain = ValidationUtils.validateAndNormalizeDomain(minusDomainObj);
        arguments.put("minusDomain", normalizedMinusDomain);

        // Check that minusDomain is not in the domains array
        @SuppressWarnings("unchecked")
        List<String> domains = (List<String>) arguments.get("domains");
        if (domains != null && domains.contains(normalizedMinusDomain)) {
            throw new ValidationException("Parameter 'minusDomain' cannot be the same as any domain in 'domains' array");
        }
    }

    /**
     * Validates filter parameters for domain unique keywords
     */
    private static void validateFiltersParameters(Object filtersObj) throws ValidationException {
        if (!(filtersObj instanceof Map)) {
            throw new ValidationException("Parameter 'filters' must be an object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> filters = (Map<String, Object>) filtersObj;

        // Validate boolean filters
        ValidationUtils.validateBooleanFilter(filters, "right_spelling");
        ValidationUtils.validateBooleanFilter(filters, "misspelled");

        // Validate keyword arrays
        ValidationUtils.validateKeywordArray(filters, "keywords", 100);
        ValidationUtils.validateKeywordArray(filters, "minus_keywords", 100);

        // Validate numeric filters - search volume
        ValidationUtils.validateIntegerFilter(filters, "queries", 0, null);
        ValidationUtils.validateIntegerFilter(filters, "queries_from", 0, null);
        ValidationUtils.validateIntegerFilter(filters, "queries_to", 0, null);

        ValidationUtils.validateIntegerFilter(filters, "region_queries_count", 0, null);
        ValidationUtils.validateIntegerFilter(filters, "region_queries_count_from", 0, null);
        ValidationUtils.validateIntegerFilter(filters, "region_queries_count_to", 0, null);

        ValidationUtils.validateIntegerFilter(filters, "region_queries_count_wide", 0, null);
        ValidationUtils.validateIntegerFilter(filters, "region_queries_count_wide_from", 0, null);
        ValidationUtils.validateIntegerFilter(filters, "region_queries_count_wide_to", 0, null);

        // Validate cost filters
        ValidationUtils.validateNumberFilter(filters, "cost", 0.0, null);
        ValidationUtils.validateNumberFilter(filters, "cost_from", 0.0, null);
        ValidationUtils.validateNumberFilter(filters, "cost_to", 0.0, null);

        // Validate competition filters
        ValidationUtils.validateIntegerFilter(filters, "concurrency", 1, 100);
        ValidationUtils.validateIntegerFilter(filters, "concurrency_from", 1, 100);
        ValidationUtils.validateIntegerFilter(filters, "concurrency_to", 1, 100);

        // Validate difficulty filters
        ValidationUtils.validateIntegerFilter(filters, "difficulty", 0, 100);
        ValidationUtils.validateIntegerFilter(filters, "difficulty_from", 0, 100);
        ValidationUtils.validateIntegerFilter(filters, "difficulty_to", 0, 100);

        // Validate keyword length filters
        ValidationUtils.validateIntegerFilter(filters, "keyword_length", 1, null);
        ValidationUtils.validateIntegerFilter(filters, "keyword_length_from", 1, null);
        ValidationUtils.validateIntegerFilter(filters, "keyword_length_to", 1, null);

        // Validate traffic filters
        ValidationUtils.validateIntegerFilter(filters, "traff", 0, null);
        ValidationUtils.validateIntegerFilter(filters, "traff_from", 0, null);
        ValidationUtils.validateIntegerFilter(filters, "traff_to", 0, null);

        // Validate position filters
        ValidationUtils.validateIntegerFilter(filters, "position", 1, 100);
        ValidationUtils.validateIntegerFilter(filters, "position_from", 1, 100);
        ValidationUtils.validateIntegerFilter(filters, "position_to", 1, 100);

        // Validate that _from is less than _to for range filters
        validateRangeFilters(filters, "queries");
        validateRangeFilters(filters, "region_queries_count");
        validateRangeFilters(filters, "region_queries_count_wide");
        validateRangeFilters(filters, "cost");
        validateRangeFilters(filters, "concurrency");
        validateRangeFilters(filters, "difficulty");
        validateRangeFilters(filters, "keyword_length");
        validateRangeFilters(filters, "traff");
        validateRangeFilters(filters, "position");

        // Check for unknown filter parameters
        Set<String> allowedFilters = Set.of(
                "right_spelling", "misspelled", "keywords", "minus_keywords",
                "queries", "queries_from", "queries_to",
                "region_queries_count", "region_queries_count_from", "region_queries_count_to",
                "region_queries_count_wide", "region_queries_count_wide_from", "region_queries_count_wide_to",
                "cost", "cost_from", "cost_to",
                "concurrency", "concurrency_from", "concurrency_to",
                "difficulty", "difficulty_from", "difficulty_to",
                "keyword_length", "keyword_length_from", "keyword_length_to",
                "traff", "traff_from", "traff_to",
                "position", "position_from", "position_to"
        );

        for (String key : filters.keySet()) {
            if (!allowedFilters.contains(key)) {
                throw new ValidationException(String.format("Unknown filter parameter: '%s'", key));
            }
        }
    }

    /**
     * Validates that _from is less than _to for range filters
     */
    private static void validateRangeFilters(Map<String, Object> filters, String baseName)
            throws ValidationException {
        Object fromObj = filters.get(baseName + "_from");
        Object toObj = filters.get(baseName + "_to");

        if (fromObj != null && toObj != null) {
            try {
                double fromValue = ((Number) fromObj).doubleValue();
                double toValue = ((Number) toObj).doubleValue();

                if (fromValue > toValue) {
                    throw new ValidationException(String.format(
                            "Filter '%s_from' (%s) must be less than or equal to '%s_to' (%s)",
                            baseName, fromValue, baseName, toValue));
                }
            } catch (ClassCastException e) {
                // This will be caught by individual filter validation
            }
        }
    }
}