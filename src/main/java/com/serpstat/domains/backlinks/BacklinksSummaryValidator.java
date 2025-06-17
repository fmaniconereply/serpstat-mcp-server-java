package com.serpstat.domains.backlinks;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.utils.ValidationUtils;

import java.util.Map;
import java.util.Set;

public class BacklinksSummaryValidator {

    private static final Set<String> SUPPORTED_SEARCH_TYPES = Set.of("domain", "domain_with_subdomains");

    /**
     * Validate getSummaryV2 request parameters
     */
    public static void validateBacklinksSummaryRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate query parameter
        Object queryObj = arguments.get("query");
        if (queryObj == null) {
            throw new ValidationException("Parameter 'query' is required");
        }

        if (!(queryObj instanceof String)) {
            throw new ValidationException("Parameter 'query' must be a string");
        }

        String query = ((String) queryObj).trim();
        if (query.isEmpty()) {
            throw new ValidationException("Parameter 'query' cannot be empty");
        }

        if (query.length() > 255) {
            throw new ValidationException("Parameter 'query' must not exceed 255 characters");
        }

        // Validate searchType parameter
        String searchType = (String) arguments.getOrDefault("searchType", "domain");
        if (!SUPPORTED_SEARCH_TYPES.contains(searchType)) {
            throw new ValidationException(String.format(
                    "Unsupported searchType: '%s'. Supported: %s",
                    searchType,
                    SUPPORTED_SEARCH_TYPES
            ));
        }

        // Validate domain
        query = ValidationUtils.validateAndNormalizeDomain(query);

        // Normalize domain to lowercase
        arguments.put("query", query.toLowerCase());


    }

}
