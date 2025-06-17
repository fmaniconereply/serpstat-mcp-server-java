package com.serpstat.domains.keywords;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.utils.ValidationUtils;

import java.util.Map;
import java.util.Set;

/**
 * Validator for related keywords analysis requests
 */
public class RelatedKeywordsValidator {

    /**
     * Validates related keywords request parameters
     *
     * @param arguments The request arguments to validate
     * @throws ValidationException If validation fails
     */
    public static void validateRelatedKeywordsRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate keyword parameter
        validateKeywordParameter(arguments);

        // Validate search engine parameter
        ValidationUtils.validateSearchEngines(arguments, "se", "g_us", true);

        // Validate withIntents parameter
        Object withIntentsObj = arguments.get("withIntents");
        if (withIntentsObj != null && !(withIntentsObj instanceof Boolean)) {
            throw new ValidationException("Parameter 'withIntents' must be a boolean");
        }

        // Validate pagination parameters
        ValidationUtils.validatePaginationParameters(arguments);
        ValidationUtils.validatePaginationSizeParameters(arguments);

        // Validate sort parameters
        Set<String> validSortFields = Set.of(
                "region_queries_count", "cost", "difficulty", "concurrency",
                "weight", "keyword"
        );
        ValidationUtils.validateSortParameters(arguments, validSortFields);

        // Validate filters if present
        Object filtersObj = arguments.get("filters");
        if (filtersObj != null) {
            validateFiltersParameters(filtersObj);
        }
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
     * Validates filter parameters for related keywords
     */
    private static void validateFiltersParameters(Object filtersObj) throws ValidationException {
        if (!(filtersObj instanceof Map)) {
            throw new ValidationException("Parameter 'filters' must be an object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> filters = (Map<String, Object>) filtersObj;

        // Validate numeric filters
        ValidationUtils.validateNumberFilter(filters, "cost", 0.0, 200.0);
        ValidationUtils.validateNumberFilter(filters, "cost_from", 0.0, 200.0);
        ValidationUtils.validateNumberFilter(filters, "cost_to", 0.0, 200.0);

        ValidationUtils.validateIntegerFilter(filters, "region_queries_count", 0, 100000000);
        ValidationUtils.validateIntegerFilter(filters, "region_queries_count_from", 0, 100000000);
        ValidationUtils.validateIntegerFilter(filters, "region_queries_count_to", 0, 100000000);

        ValidationUtils.validateIntegerFilter(filters, "keyword_length", 1, null);
        ValidationUtils.validateIntegerFilter(filters, "keyword_length_from", 1, null);
        ValidationUtils.validateIntegerFilter(filters, "keyword_length_to", 1, null);

        ValidationUtils.validateIntegerFilter(filters, "difficulty", 0, 100);
        ValidationUtils.validateIntegerFilter(filters, "difficulty_from", 0, 100);
        ValidationUtils.validateIntegerFilter(filters, "difficulty_to", 0, 100);

        ValidationUtils.validateIntegerFilter(filters, "concurrency", 1, 100);
        ValidationUtils.validateIntegerFilter(filters, "concurrency_from", 1, 100);
        ValidationUtils.validateIntegerFilter(filters, "concurrency_to", 1, 100);

        ValidationUtils.validateIntegerFilter(filters, "weight", 1, null);
        ValidationUtils.validateNumberFilter(filters, "weight_from", 1.0, null);
        ValidationUtils.validateNumberFilter(filters, "weight_to", 1.0, null);

        // Validate boolean filters
        ValidationUtils.validateBooleanFilter(filters, "right_spelling");

        // Validate array filters
        ValidationUtils.validateStringArrayFilter(filters, "keyword_contain");
        ValidationUtils.validateStringArrayFilter(filters, "keyword_not_contain");
        ValidationUtils.validateStringArrayFilter(filters, "keyword_contain_one_of");
        ValidationUtils.validateStringArrayFilter(filters, "keyword_not_contain_one_of");
        ValidationUtils.validateStringArrayFilter(filters, "keyword_contain_broad_match");
        ValidationUtils.validateStringArrayFilter(filters, "keyword_not_contain_broad_match");
        ValidationUtils.validateStringArrayFilter(filters, "keyword_contain_one_of_broad_match");
        ValidationUtils.validateStringArrayFilter(filters, "keyword_not_contain_one_of_broad_match");
        ValidationUtils.validateStringArrayFilter(filters, "types");

        // Validate geo names filter
        Object geoNamesObj = filters.get("geo_names");
        if (geoNamesObj != null) {
            if (!(geoNamesObj instanceof String)) {
                throw new ValidationException("Filter 'geo_names' must be a string");
            }
            String geoNames = (String) geoNamesObj;
            if (!Set.of("contain", "not_contain").contains(geoNames)) {
                throw new ValidationException("Filter 'geo_names' must be either 'contain' or 'not_contain'");
            }
        }

        // Validate intent filters
        ValidationUtils.validateIntentFilter(filters, "intents_contain");
        ValidationUtils.validateIntentFilter(filters, "intents_not_contain");

        // Validate that _from is less than _to for range filters
        validateRangeFilters(filters, "cost");
        validateRangeFilters(filters, "region_queries_count");
        validateRangeFilters(filters, "keyword_length");
        validateRangeFilters(filters, "difficulty");
        validateRangeFilters(filters, "concurrency");
        validateRangeFilters(filters, "weight");

        // Check for unknown filter parameters
        Set<String> allowedFilters = Set.of(
                "cost", "cost_from", "cost_to",
                "concurrency", "concurrency_from", "concurrency_to",
                "keyword_length", "keyword_length_from", "keyword_length_to",
                "difficulty", "difficulty_from", "difficulty_to",
                "region_queries_count", "region_queries_count_from", "region_queries_count_to",
                "right_spelling", "keyword_contain", "keyword_not_contain",
                "keyword_contain_one_of", "keyword_not_contain_one_of",
                "keyword_contain_broad_match", "keyword_not_contain_broad_match",
                "keyword_contain_one_of_broad_match", "keyword_not_contain_one_of_broad_match",
                "weight", "weight_from", "weight_to", "geo_names", "types",
                "intents_contain", "intents_not_contain"
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
