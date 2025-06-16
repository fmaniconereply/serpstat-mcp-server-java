package com.serpstat.domains.keywords;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.utils.ValidationUtils;

import java.util.Map;
import java.util.Set;

public class KeywordValidator {


    /**
     * Validate getKeywords request parameters
     */
    public static void validateGetKeywordsRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate keyword parameter
        Object keywordObj = arguments.get("keyword");
        if (keywordObj == null) {
            throw new ValidationException("Parameter 'keyword' is required");
        }

        if (!(keywordObj instanceof String)) {
            throw new ValidationException("Parameter 'keyword' must be a string");
        }

        String keyword = ((String) keywordObj).trim();
        if (keyword.isEmpty()) {
            throw new ValidationException("Parameter 'keyword' cannot be empty");
        }

        if (keyword.length() > 100) {
            throw new ValidationException("Parameter 'keyword' must not exceed 100 characters");
        }

        keyword = ValidationUtils.normalizeUtf8String(keyword);

        // Normalize keyword
        arguments.put("keyword", keyword);

        // Validate search engine
        ValidationUtils.validateSearchEngines(arguments,"se", "g_us",true);

        // Validate minusKeywords
        ValidationUtils.validateKeywordArray(arguments, "minusKeywords", 50);

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
                "found_results", "keyword_length"
        );
        ValidationUtils.validateSortParameters(arguments,validSortFields);

        // Validate filters
        validateFiltersParameters(arguments);
    }



    /**
     * Validate filters parameters
     */
    private static void validateFiltersParameters(Map<String, Object> arguments) throws ValidationException {
        Object filtersObj = arguments.get("filters");
        if (filtersObj != null) {
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

            // Validate boolean filters
            ValidationUtils.validateBooleanFilter(filters, "right_spelling");

            // Validate array filters
            ValidationUtils.validateStringArrayFilter(filters, "keyword_contain");
            ValidationUtils.validateStringArrayFilter(filters, "keyword_not_contain");
            ValidationUtils.validateStringArrayFilter(filters, "keyword_contain_one_of");
            ValidationUtils.validateStringArrayFilter(filters, "keyword_not_contain_one_of");
            ValidationUtils.validateStringArrayFilter(filters, "keyword_contain_broad_match");
            ValidationUtils.validateStringArrayFilter(filters, "keyword_not_contain_broad_match");

            // Validate language filter
            ValidationUtils.validateLanguageFilter(filters, "lang");

            // Validate intent filters
            ValidationUtils.validateIntentFilter(filters, "intents_contain");
            ValidationUtils.validateIntentFilter(filters, "intents_not_contain");
        }
    }



}