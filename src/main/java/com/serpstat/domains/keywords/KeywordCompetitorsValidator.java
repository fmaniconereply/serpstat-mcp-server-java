package com.serpstat.domains.keywords;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.utils.ValidationUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Validator for keyword competitors analysis requests
 */
public class KeywordCompetitorsValidator {

    private static final Set<String> VALID_COMPETITOR_SORT_FIELDS = Set.of(
            "domain", "visible", "keywords", "traff", "visible_dynamic", "keywords_dynamic",
            "traff_dynamic", "ads_dynamic", "new_keywords", "out_keywords", "rised_keywords",
            "down_keywords", "ad_keywords", "ads", "intersected", "relevance", "our_relevance"
    );

    /**
     * Validates keyword competitors request parameters
     *
     * @param arguments The request arguments to validate
     * @throws ValidationException If validation fails
     */
    public static void validateKeywordCompetitorsRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate keyword parameter
        validateKeywordParameter(arguments);

        // Validate search engine parameter
        ValidationUtils.validateSearchEngines(arguments, "se", "g_us", true);

        // Validate size parameter
        ValidationUtils.validatePaginationSizeParameters(arguments);

        // Validate filters if present
        Object filtersObj = arguments.get("filters");
        if (filtersObj != null) {
            validateCompetitorFilters(filtersObj);
        }

        // Validate sort parameters if present
        ValidationUtils.validateSortParameters(arguments, VALID_COMPETITOR_SORT_FIELDS);
    }

    /**
     * Validates the keyword parameter
     */
    private static void validateKeywordParameter(Map<String, Object> arguments) throws ValidationException {
        Object keywordObj = arguments.get("keyword");
        if (keywordObj == null) {
            throw new ValidationException("Parameter 'keyword' is required");
        }

        if (!(keywordObj instanceof String)) {
            throw new ValidationException("Parameter 'keyword' must be a string");
        }

        String keyword = ValidationUtils.normalizeUtf8String((String) keywordObj);
        if (keyword == null || keyword.isEmpty()) {
            throw new ValidationException("Parameter 'keyword' cannot be empty");
        }

        if (keyword.length() > 200) {
            throw new ValidationException("Parameter 'keyword' must not exceed 200 characters");
        }

        // Update the normalized keyword back to arguments
        arguments.put("keyword", keyword);
    }


    /**
     * Validates competitor-specific filters
     */
    private static void validateCompetitorFilters(Object filtersObj) throws ValidationException {
        if (!(filtersObj instanceof Map)) {
            throw new ValidationException("Parameter 'filters' must be an object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> filters = (Map<String, Object>) filtersObj;

        // Validate domain arrays
        validateDomainArrayFilter(filters, "domain");
        validateDomainArrayFilter(filters, "minus_domain");

        // Validate numeric filters
        ValidationUtils.validateNumberFilter(filters, "visible", 0.0, null);
        ValidationUtils.validateNumberFilter(filters, "visible_from", 0.0, null);
        ValidationUtils.validateNumberFilter(filters, "visible_to", 0.0, null);

        ValidationUtils.validateIntegerFilter(filters, "traff", 0, null);
        ValidationUtils.validateIntegerFilter(filters, "traff_from", 0, null);
        ValidationUtils.validateIntegerFilter(filters, "traff_to", 0, null);

        ValidationUtils.validateNumberFilter(filters, "relevance", 0.0, 100.0);
        ValidationUtils.validateNumberFilter(filters, "relevance_from", 0.0, 100.0);
        ValidationUtils.validateNumberFilter(filters, "relevance_to", 0.0, 100.0);

        ValidationUtils.validateNumberFilter(filters, "our_relevance", 0.0, 100.0);
        ValidationUtils.validateNumberFilter(filters, "our_relevance_from", 0.0, 100.0);
        ValidationUtils.validateNumberFilter(filters, "our_relevance_to", 0.0, 100.0);

        // Validate that _from is less than _to for range filters
        validateRangeFilters(filters, "visible");
        validateRangeFilters(filters, "traff");
        validateRangeFilters(filters, "relevance");
        validateRangeFilters(filters, "our_relevance");

        // Check for unknown filter parameters
        Set<String> allowedFilters = Set.of(
                "domain", "minus_domain", "visible", "visible_from", "visible_to",
                "traff", "traff_from", "traff_to", "relevance", "relevance_from",
                "relevance_to", "our_relevance", "our_relevance_from", "our_relevance_to"
        );

        for (String key : filters.keySet()) {
            if (!allowedFilters.contains(key)) {
                throw new ValidationException(String.format("Unknown filter parameter: '%s'", key));
            }
        }
    }

    /**
     * Validates domain array filters
     */
    private static void validateDomainArrayFilter(Map<String, Object> filters, String filterName)
            throws ValidationException {
        Object filterValue = filters.get(filterName);
        if (filterValue != null) {
            if (!(filterValue instanceof List)) {
                throw new ValidationException(String.format("Filter '%s' must be an array", filterName));
            }

            @SuppressWarnings("unchecked")
            List<Object> domains = (List<Object>) filterValue;

            if (domains.size() > 100) {
                throw new ValidationException(String.format("Filter '%s' cannot have more than 100 domains", filterName));
            }

            for (int i = 0; i < domains.size(); i++) {
                Object domainObj = domains.get(i);
                if (!(domainObj instanceof String)) {
                    throw new ValidationException(String.format("All items in filter '%s' must be strings", filterName));
                }

                try {
                    String normalizedDomain = ValidationUtils.validateAndNormalizeDomain(domainObj);
                    domains.set(i, normalizedDomain);
                } catch (ValidationException e) {
                    throw new ValidationException(String.format("Invalid domain in filter '%s' at index %d: %s",
                            filterName, i, e.getMessage()));
                }
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