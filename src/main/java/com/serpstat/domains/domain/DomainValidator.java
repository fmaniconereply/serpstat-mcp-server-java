package com.serpstat.domains.domain;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.utils.ValidationUtils;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.serpstat.domains.constants.Patterns.DOMAIN_PATTERN;


/**
 * Validator for domain-related requests
 */
public class DomainValidator {

    /**
     * Validate getDomainsInfo request parameters
     */
    public static void validateDomainsInfoRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate domains array
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

        if (domains.size() > 100) {
            throw new ValidationException("Maximum 100 domains allowed per request");
        }

        // Validate each domain
        for (int i = 0; i < domains.size(); i++) {
            String domain = domains.get(i);
            if (domain == null || domain.trim().isEmpty()) {
                throw new ValidationException(String.format("Domain at index %d is empty", i));
            }

            domain = domain.trim().toLowerCase();
            if (!DOMAIN_PATTERN.matcher(domain).matches()) {
                throw new ValidationException(String.format("Invalid domain format: %s ", domain));
            }

            // Update the normalized domain back to the list
            domains.set(i, domain);
        }

        // Check for duplicates
        Set<String> uniqueDomains = Set.copyOf(domains);
        if (uniqueDomains.size() != domains.size()) {
            throw new ValidationException("Duplicate domains are not allowed");
        }

        // Validate search engine

        ValidationUtils.validateSearchEngines(arguments,"se", "g_us",true);

        // Validate filters if present
        Object filtersObj = arguments.get("filters");
        if (filtersObj != null) {
            ValidationUtils.validateFilters(filtersObj);
        }
    }

    /**
     * Validate getRegionsCount request parameters
     */
    public static void validateRegionsCountRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate domain parameter
        String domain = ValidationUtils.validateAndNormalizeDomain(arguments.get("domain"));
        arguments.put("domain", domain);

        // Validate sort parameter
        String sort = (String) arguments.getOrDefault("sort", "keywords_count");
        Set<String> validSortFields = Set.of("keywords_count", "country_name_en", "db_name");

        if (!validSortFields.contains(sort)) {
            throw new ValidationException(String.format(
                    "Invalid sort field: '%s'. Valid options: %s", sort, validSortFields
            ));
        }

        // Validate order parameter
        String order = (String) arguments.getOrDefault("order", "desc");
        Set<String> validOrders = Set.of("asc", "desc");
        if (!validOrders.contains(order)) {
            throw new ValidationException(String.format(
                    "Invalid order: '%s'. Valid options: %s", order, validOrders
            ));
        }
    }
    public static void validateDomainKeywordsRequest(Map<String, Object> arguments)
            throws ValidationException {
        // Validate domain parameter
        String domain = ValidationUtils.validateAndNormalizeDomain(arguments.get("domain"));
        arguments.put("domain", domain);

        ValidationUtils.validateSearchEngines(arguments,"se", "g_us",true);

        // Validate pagination parameters
        ValidationUtils.validatePaginationParameters(arguments);
        ValidationUtils.validatePaginationSizeParameters(arguments);

        // Validate URL parameter
        Object urlObj = arguments.get("url");
        if (urlObj != null) {
            if (!(urlObj instanceof String)) {
                throw new ValidationException("Parameter 'url' must be a string");
            }
            String url = (String) urlObj;
            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                throw new ValidationException("Parameter 'url' must be a valid HTTP/HTTPS URL");
            }
        }

        ValidationUtils.validateKeywordArray(arguments, "keywords", 50);
        ValidationUtils.validateKeywordArray(arguments, "minusKeywords", 50);

        // Validate sort parameters
        Set<String> validSortFields = Set.of(
                "position", "region_queries_count", "cost", "traff", "difficulty",
                "keyword_length", "concurrency", "types", "geo_names", "region_queries_count_wide",
                "dynamic", "found_results"
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

            // Validate position filters
            ValidationUtils.validateIntegerFilter(filters, "position", 1, 100);
            ValidationUtils.validateIntegerFilter(filters, "position_from", 1, 100);
            ValidationUtils.validateIntegerFilter(filters, "position_to", 1, 100);

            // Validate cost filters
            ValidationUtils.validateNumberFilter(filters, "cost", 0.0, null);
            ValidationUtils.validateNumberFilter(filters, "cost_from", 0.0, null);
            ValidationUtils.validateNumberFilter(filters, "cost_to", 0.0, null);

            // Validate difficulty filters
            ValidationUtils.validateNumberFilter(filters, "difficulty", 0.0, 100.0);
            ValidationUtils.validateNumberFilter(filters, "difficulty_from", 0.0, 100.0);
            ValidationUtils.validateNumberFilter(filters, "difficulty_to", 0.0, 100.0);

            // Validate concurrency filters
            ValidationUtils.validateIntegerFilter(filters, "concurrency", 1, 100);
            ValidationUtils.validateIntegerFilter(filters, "concurrency_from", 1, 100);
            ValidationUtils.validateIntegerFilter(filters, "concurrency_to", 1, 100);

            // Validate volume filters
            ValidationUtils.validateIntegerFilter(filters, "region_queries_count", 0, null);
            ValidationUtils.validateIntegerFilter(filters, "region_queries_count_from", 0, null);
            ValidationUtils.validateIntegerFilter(filters, "region_queries_count_to", 0, null);

            // Validate traffic filter
            ValidationUtils.validateIntegerFilter(filters, "traff", 0, null);

            // Validate keyword length
            ValidationUtils.validateIntegerFilter(filters, "keyword_length", 1, null);

            // Validate intent filters
            ValidationUtils.validateIntentFilter(filters, "intents_contain");
            ValidationUtils.validateIntentFilter(filters, "intents_not_contain");
        }
    }
}