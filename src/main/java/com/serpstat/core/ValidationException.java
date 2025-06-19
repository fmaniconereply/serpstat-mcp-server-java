package com.serpstat.core;

/**
 * Validation exception for request parameters
 * needs for future improvements
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}