package com.serpstat.domains.domain;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.utils.ValidationUtils;

import java.util.Map;
import java.util.Set;

/**
 * Validator for domain URLs requests
 */
public class DomainUrlsValidator {

    /**
     * Validate getDomainUrls request parameters
     */
    public static void validateDomainUrlsRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate domain parameter
        String domain = ValidationUtils.validateAndNormalizeDomain(arguments.get("domain"));
        arguments.put("domain", domain);

        // Validate search engine parameter
        ValidationUtils.validateSearchEngines(arguments, "se", "g_us", true);

        // Validate pagination parameters
        ValidationUtils.validatePaginationParameters(arguments);
        ValidationUtils.validatePaginationSizeParameters(arguments);

        // Validate sort parameters
        Set<String> validSortFields = Set.of("keywords");
        ValidationUtils.validateSortParameters(arguments, validSortFields);

        // Validate filters if present
        Object filtersObj = arguments.get("filters");
        if (filtersObj != null) {
            validateFiltersParameters(filtersObj);
        }
    }

    /**
     * Validate filters parameters for domain URLs
     */
    static void validateFiltersParameters(Object filtersObj) throws ValidationException {
        if (!(filtersObj instanceof Map)) {
            throw new ValidationException("Parameter 'filters' must be an object");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> filters = (Map<String, Object>) filtersObj;

        // Validate URL prefix filter
        Object urlPrefixObj = filters.get("url_prefix");
        if (urlPrefixObj != null) {
            if (!(urlPrefixObj instanceof String urlPrefix)) {
                throw new ValidationException("Filter 'url_prefix' must be a string");
            }
            if (urlPrefix.length() > 500) {
                throw new ValidationException("Filter 'url_prefix' must not exceed 500 characters");
            }
            if (!urlPrefix.startsWith("http://") && !urlPrefix.startsWith("https://")) {
                throw new ValidationException("Filter 'url_prefix' must start with http:// or https://");
            }
        }

        // Validate URL contain filter
        Object urlContainObj = filters.get("url_contain");
        if (urlContainObj != null) {
            if (!(urlContainObj instanceof String urlContain)) {
                throw new ValidationException("Filter 'url_contain' must be a string");
            }
            if (urlContain.length() > 200) {
                throw new ValidationException("Filter 'url_contain' must not exceed 200 characters");
            }
            if (urlContain.trim().isEmpty()) {
                throw new ValidationException("Filter 'url_contain' cannot be empty");
            }
        }

        // Validate URL not contain filter
        Object urlNotContainObj = filters.get("url_not_contain");
        if (urlNotContainObj != null) {
            if (!(urlNotContainObj instanceof String urlNotContain)) {
                throw new ValidationException("Filter 'url_not_contain' must be a string");
            }
            if (urlNotContain.length() > 200) {
                throw new ValidationException("Filter 'url_not_contain' must not exceed 200 characters");
            }
            if (urlNotContain.trim().isEmpty()) {
                throw new ValidationException("Filter 'url_not_contain' cannot be empty");
            }
        }

        // Check for unknown filter parameters
        Set<String> allowedFilters = Set.of("url_prefix", "url_contain", "url_not_contain");
        for (String key : filters.keySet()) {
            if (!allowedFilters.contains(key)) {
                throw new ValidationException(String.format("Unknown filter parameter: '%s'", key));
            }
        }
    }
}