package com.serpstat.domains.projects;

import com.serpstat.core.ValidationException;
import com.serpstat.domains.utils.ValidationUtils;

import java.util.Map;

/**
 * Validator for projects-related requests
 */
public class ProjectsValidator {

    /**
     * Validate getProjects request parameters
     */
    public static void validateProjectsListRequest(Map<String, Object> arguments)
            throws ValidationException {

        // Validate page parameter
        ValidationUtils.validatePaginationParameters(arguments);
        ValidationUtils.validatePaginationSizeParameters(arguments,ValidationUtils.VALID_PAGE_SIZES);

    }
}

