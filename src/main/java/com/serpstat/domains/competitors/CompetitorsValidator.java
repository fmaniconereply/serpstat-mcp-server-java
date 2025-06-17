package com.serpstat.domains.competitors;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.utils.ValidationUtils;

import java.util.Map;



/**
 * Validator for domain-related requests
 */
public class CompetitorsValidator {

    /**
     * Validate getCompetitors request parameters
     */
    public static void validateDomainCompetitorsRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate domain

        String domain = (String) arguments.get("domain");

        String normalizedDomain = ValidationUtils.validateAndNormalizeDomain(domain);
        arguments.put("domain", normalizedDomain);

        ValidationUtils.validateSearchEngines(arguments,"se", "g_us",true);

        // Validate filters if present
        Object filtersObj = arguments.get("filters");
        if (filtersObj != null) {
            ValidationUtils.validateFilters(filtersObj);
        }
    }

}