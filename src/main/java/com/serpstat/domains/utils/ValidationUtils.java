package com.serpstat.domains.utils;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.constants.Intents;
import com.serpstat.domains.constants.Languages;

import java.text.Normalizer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.serpstat.domains.constants.Patterns.DOMAIN_PATTERN;
import static com.serpstat.domains.constants.SearchEngines.SUPPORTED_SEARCH_ENGINES;

/**
 * Utility class for validating various parameters and filters.
 */
public class ValidationUtils {

    /**
     * Set of valid page sizes for pagination.
     */
    public static final Set<Integer> VALID_PAGE_SIZES = Set.of(20, 50, 100, 200, 500);
    /**
     * Validates filter parameters.
     *
     * @param filtersObj The filters object to validate.
     * @throws ValidationException If validation fails.
     */
    public static void validateFilters(Object filtersObj) throws ValidationException {
        if (!(filtersObj instanceof Map)) {
            throw new ValidationException("Parameter 'filters' must be an object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> filters = (Map<String, Object>) filtersObj;

        // Validate visibility filter
        Object visibleObj = filters.get("visible");
        if (visibleObj != null) {
            try {
                double visible = ((Number) visibleObj).doubleValue();
                if (visible < 0) {
                    throw new ValidationException("Filter 'visible' must be non-negative");
                }
            } catch (ClassCastException | NullPointerException e) {
                throw new ValidationException("Filter 'visible' must be a number");
            }
        }

        // Validate traffic filter
        Object traffObj = filters.get("traff");
        if (traffObj != null) {
            try {
                long traff = ((Number) traffObj).longValue();
                if (traff < 0) {
                    throw new ValidationException("Filter 'traff' must be non-negative");
                }
            } catch (ClassCastException | NullPointerException e) {
                throw new ValidationException("Filter 'traff' must be a number");
            }
        }

        // Check for unknown filter parameters
        Set<String> allowedFilters = Set.of("visible", "traff");
        for (String key : filters.keySet()) {
            if (!allowedFilters.contains(key)) {
                throw new ValidationException(String.format("Unknown filter parameter: '%s'", key));
            }
        }
    }

    /**
     * Validates pagination parameters.
     *
     * @param arguments The arguments map containing pagination parameters.
     * @throws ValidationException If validation fails.
     */
    public static void validatePaginationParameters(Map<String, Object> arguments) throws ValidationException {
        Object pageObj = arguments.get("page");
        if (pageObj != null) {
            try {
                int page = ((Number) pageObj).intValue();
                if (page < 1) {
                    throw new ValidationException("Parameter 'page' must be >= 1");
                }
            } catch (ClassCastException e) {
                throw new ValidationException("Parameter 'page' must be an integer");
            }
        }

    }

    /**
     * Validates pagination size parameters.
     *
     * @param arguments The argument map containing pagination size parameters.
     * @throws ValidationException If validation fails.
     */
    public static void validatePaginationSizeParameters(Map<String, Object> arguments) throws ValidationException {
        {
            Object sizeObj = arguments.get("size");
            if (sizeObj != null) {
                try {
                    int size = ((Number) sizeObj).intValue();
                    if (size < 1 || size > 1000) {
                        throw new ValidationException("Parameter 'size' must be between 1 and 1000");
                    }
                } catch (ClassCastException e) {
                    throw new ValidationException("Parameter 'size' must be an integer");
                }
            }
        }
    }

    /**
     * Validates pagination size parameters against a set of valid sizes.
     *
     * @param arguments  The argument map containing pagination size parameters.
     * @param validSizes The set of valid sizes.
     * @throws ValidationException If validation fails.
     */
    public static void validatePaginationSizeParameters(Map<String, Object> arguments,Set<Integer> validSizes) throws ValidationException {
        Object sizeObj = arguments.get("size");
        if (sizeObj != null) {
            try {
                int size = ((Number) sizeObj).intValue();
                if (!validSizes.contains(size)) {
                    throw new ValidationException(String.format(
                            "Parameter 'size' must be one of: %s", VALID_PAGE_SIZES
                    ));
                }
            } catch (ClassCastException e) {
                throw new ValidationException("Parameter 'size' must be an integer");
            }
        }
    }

    /**
     * Validates a keyword array parameter.
     *
     * @param arguments The argument map containing the keyword array.
     * @param paramName The name of the parameter to validate.
     * @param maxItems  The maximum number of items allowed in the array.
     * @throws ValidationException If validation fails.
     */
    public static void validateKeywordArray(Map<String, Object> arguments, String paramName, int maxItems)
            throws ValidationException {
        Object keywordsObj = arguments.get(paramName);
        if (keywordsObj != null) {
            if (!(keywordsObj instanceof List)) {
                throw new ValidationException(String.format("Parameter '%s' must be an array", paramName));
            }

            @SuppressWarnings("unchecked")
            List<Object> keywords = (List<Object>) keywordsObj;

            if (keywords.size() > maxItems) {
                throw new ValidationException(String.format("Parameter '%s' cannot have more than %d items", paramName, maxItems));
            }

            for (int i = 0; i < keywords.size(); i++) {
                Object keyword = keywords.get(i);
                if (!(keyword instanceof String)) {
                    throw new ValidationException(String.format("All items in '%s' must be strings", paramName));
                }

                String keywordStr = normalizeUtf8String((String) keyword);
                if (keywordStr == null || keywordStr.isEmpty()) {
                    throw new ValidationException(String.format("Empty keyword found in '%s' at index %d", paramName, i));
                }


                if (keywordStr.length() > 100) {
                    throw new ValidationException(String.format("Keyword in '%s' at index %d exceeds 100 characters", paramName, i));
                }

                // Update the normalized keyword back to the list
                keywords.set(i, keywordStr);
            }
        }
    }

    /**
     * Validates a numeric filter value.
     *
     * @param filters   The filters map containing the numeric filter.
     * @param filterName The name of the filter to validate.
     * @param minValue  The minimum allowed value (nullable).
     * @param maxValue  The maximum allowed value (nullable).
     * @throws ValidationException If validation fails.
     */
    public static void validateNumberFilter(Map<String, Object> filters, String filterName, Double minValue, Double maxValue)
            throws ValidationException {
        Object filterValue = filters.get(filterName);
        if (filterValue != null) {
            try {
                double value = ((Number) filterValue).doubleValue();
                if (minValue != null && value < minValue) {
                    throw new ValidationException(String.format("Filter '%s' must be >= %s", filterName, minValue));
                }
                if (maxValue != null && value > maxValue) {
                    throw new ValidationException(String.format("Filter '%s' must be <= %s", filterName, maxValue));
                }
            } catch (ClassCastException e) {
                throw new ValidationException(String.format("Filter '%s' must be a number", filterName));
            }
        }
    }

    /**
     * Validates an integer filter value.
     *
     * @param filters   The filters map containing the integer filter.
     * @param filterName The name of the filter to validate.
     * @param minValue  The minimum allowed value (nullable).
     * @param maxValue  The maximum allowed value (nullable).
     * @throws ValidationException If validation fails.
     */
    public static void validateIntegerFilter(Map<String, Object> filters, String filterName, Integer minValue, Integer maxValue)
            throws ValidationException {
        Object filterValue = filters.get(filterName);
        if (filterValue != null) {
            try {
                int value = ((Number) filterValue).intValue();
                if (minValue != null && value < minValue) {
                    throw new ValidationException(String.format("Filter '%s' must be >= %d", filterName, minValue));
                }
                if (maxValue != null && value > maxValue) {
                    throw new ValidationException(String.format("Filter '%s' must be <= %d", filterName, maxValue));
                }
            } catch (ClassCastException e) {
                throw new ValidationException(String.format("Filter '%s' must be an integer", filterName));
            }
        }
    }

    /**
     * Validates a boolean filter value.
     *
     * @param filters   The filters map containing the boolean filter.
     * @param filterName The name of the filter to validate.
     * @throws ValidationException If validation fails.
     */
    public static void validateBooleanFilter(Map<String, Object> filters, String filterName) throws ValidationException {
        Object filterValue = filters.get(filterName);
        if (filterValue != null && !(filterValue instanceof Boolean)) {
            throw new ValidationException(String.format("Filter '%s' must be a boolean", filterName));
        }
    }

    /**
     * Validates a string array filter value.
     *
     * @param filters   The filters map containing the string array filter.
     * @param filterName The name of the filter to validate.
     * @throws ValidationException If validation fails.
     */
    public static void validateStringArrayFilter(Map<String, Object> filters, String filterName) throws ValidationException {
        Object filterValue = filters.get(filterName);
        if (filterValue != null) {
            if (!(filterValue instanceof List)) {
                throw new ValidationException(String.format("Filter '%s' must be an array", filterName));
            }

            @SuppressWarnings("unchecked")
            List<Object> array = (List<Object>) filterValue;

            for (Object item : array) {
                if (!(item instanceof String)) {
                    throw new ValidationException(String.format("All items in filter '%s' must be strings", filterName));
                }
            }
        }
    }

    /**
     * Validates a language filter value.
     *
     * @param filters   The filters map containing the language filter.
     * @param filterName The name of the filter to validate.
     * @throws ValidationException If validation fails.
     */
    public static void validateLanguageFilter(Map<String, Object> filters, String filterName) throws ValidationException {
        Object filterValue = filters.get(filterName);
        if (filterValue != null) {
            if (!(filterValue instanceof String) || !Languages.SUPPORTED_LANGUAGES.contains(filterValue)) {
                throw new ValidationException(String.format("Invalid language in filter '%s': '%s'. Valid languages: %s", filterName, filterValue, Languages.SUPPORTED_LANGUAGES));
            }
        }
    }

    /**
     * Validates an intent filter value.
     *
     * @param filters   The filter map containing the intent filter.
     * @param filterName The name of the filter to validate.
     * @throws ValidationException If validation fails.
     */
    public static void validateIntentFilter(Map<String, Object> filters, String filterName) throws ValidationException {
        Object filterValue = filters.get(filterName);
        if (filterValue != null) {
            if (!(filterValue instanceof List)) {
                throw new ValidationException(String.format("Filter '%s' must be an array", filterName));
            }

            @SuppressWarnings("unchecked")
            List<Object> intents = (List<Object>) filterValue;

            for (Object intent : intents) {
                if (!(intent instanceof String) || !Intents.SUPPORTED_INTENTS.contains(intent)) {
                    throw new ValidationException(String.format("Invalid intent in '%s': '%s'. Valid intents: %s", filterName, intent, Intents.SUPPORTED_INTENTS));
                }
            }
        }
    }

    /**
     * Validates sort parameters.
     *
     * @param arguments      The arguments map containing sort parameters.
     * @param validSortFields The set of valid sort fields.
     * @throws ValidationException If validation fails.
     */
    public static void validateSortParameters(Map<String, Object> arguments, Set<String> validSortFields) throws ValidationException {
        Object sortObj = arguments.get("sort");
        if (sortObj != null) {
            if (!(sortObj instanceof Map)) {
                throw new ValidationException("Parameter 'sort' must be an object");
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> sort = (Map<String, Object>) sortObj;


            Set<String> validSortOrders = Set.of("asc", "desc");

            for (Map.Entry<String, Object> entry : sort.entrySet()) {
                String field = entry.getKey();
                Object order = entry.getValue();

                if (!validSortFields.contains(field)) {
                    throw new ValidationException(String.format("Invalid sort field: '%s'. Valid fields: %s", field, validSortFields));
                }

                if (!(order instanceof String) || !validSortOrders.contains(order)) {
                    throw new ValidationException(String.format("Invalid sort order for field '%s': '%s'. Valid orders: %s", field, order, validSortOrders));
                }
            }
        }
    }

    /**
     * Validates and normalizes a domain parameter.
     *
     * @param domainObj The domain object to validate.
     * @return The normalized domain string.
     * @throws ValidationException If validation fails.
     */
    public static String validateAndNormalizeDomain(Object domainObj) throws ValidationException {
        if (domainObj == null) {
            throw new ValidationException("Parameter 'domain' is required");
        }

        if (!(domainObj instanceof String)) {
            throw new ValidationException("Parameter 'domain' must be a string");
        }

        String domain = ((String) domainObj).trim();
        if (domain.isEmpty()) {
            throw new ValidationException("Parameter 'domain' cannot be empty");
        }

        if (domain.length() < 4 || domain.length() > 253) {
            throw new ValidationException("Parameter 'domain' must be between 4 and 253 characters");
        }

        if (!DOMAIN_PATTERN.matcher(domain.toLowerCase()).matches()) {
            throw new ValidationException(String.format(
                    "Invalid domain format: '%s'. Expected format: example.com", domain
            ));
        }

        return domain.toLowerCase();
    }

    /**
     * Validates search engine parameters.
     *
     * @param arguments     The arguments map containing search engine parameters.
     * @param parameterName The name of the parameter to validate.
     * @param defaultValue  The default value for the parameter.
     * @param required      Whether the parameter is required.
     * @throws ValidationException If validation fails.
     */
    public static void validateSearchEngines(Map<String, Object> arguments,String parameterName, String defaultValue, boolean required) throws ValidationException {

        String searchEngine;
        if(required) {
            searchEngine = (String) arguments.get("se");
            if (searchEngine == null) {
                throw new ValidationException("Parameter 'se' is required");
            }
        } else {
             searchEngine = (String) arguments.getOrDefault(parameterName, defaultValue);
        }



        if (!SUPPORTED_SEARCH_ENGINES.contains(searchEngine)) {
            throw new ValidationException(String.format(
                    "Unsupported search engine: '%s'. Supported: %s",
                    searchEngine,
                    SUPPORTED_SEARCH_ENGINES
            ));
        }
    }

    public static String normalizeUtf8String(String input) {
        if (input == null) {
            return null;
        }

        // Normalize Unicode characters to composed form (NFC)
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFC);

        // Trim whitespace
        normalized = normalized.trim();

        return normalized;
    }
}