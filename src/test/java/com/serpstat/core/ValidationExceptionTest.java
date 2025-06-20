package com.serpstat.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ValidationException class
 * Tests constructor, inheritance, usage, message formatting, and stack trace.
 */
@DisplayName("ValidationException Tests")
class ValidationExceptionTest {

    @Test
    @DisplayName("Test ValidationException constructor with message")
    void testConstructorWithMessage() {
        String message = "Test validation error";
        ValidationException exception = new ValidationException(message);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
        assertNotNull(exception.getStackTrace());
    }

    @Test
    @DisplayName("Test ValidationException constructor with message and cause")
    void testConstructorWithMessageAndCause() {
        String message = "Validation error with cause";
        Throwable cause = new IllegalArgumentException("Root cause");
        ValidationException exception = new ValidationException(message, cause);
        
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
        assertNotNull(exception.getStackTrace());
    }

    @Test
    @DisplayName("Test ValidationException inheritance from Exception")
    void testExceptionInheritance() {
        ValidationException exception = new ValidationException("Test");
        
        assertTrue(exception instanceof Exception);
        assertTrue(exception instanceof Throwable);
    }

    @Test
    @DisplayName("Test ValidationException usage in validation scenarios")
    void testValidationScenarios() {
        String message = "Invalid parameter value";
        
        ValidationException thrown = assertThrows(ValidationException.class, () -> {
            throw new ValidationException(message);
        });
        
        assertEquals(message, thrown.getMessage());
    }

    @Test
    @DisplayName("Test ValidationException message formatting")
    void testMessageFormatting() {
        String formattedMessage = String.format("Invalid parameter: %s with value: %d", "param1", 42);
        ValidationException exception = new ValidationException(formattedMessage);
        
        assertEquals("Invalid parameter: param1 with value: 42", exception.getMessage());
    }    @Test
    @DisplayName("Test ValidationException stack trace preservation")
    void testStackTracePreservation() {
        ValidationException exception = new ValidationException("Test stack trace");
        StackTraceElement[] stackTrace = exception.getStackTrace();
        
        assertNotNull(stackTrace);
        assertTrue(stackTrace.length > 0);
        assertEquals("testStackTracePreservation", stackTrace[0].getMethodName());
        assertEquals("com.serpstat.core.ValidationExceptionTest", stackTrace[0].getClassName());
    }
    
    @Test
    @DisplayName("Test ValidationException with null message")
    void testConstructorWithNullMessage() {
        ValidationException exception = new ValidationException(null);
        
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    @DisplayName("Test ValidationException with null message and cause")
    void testConstructorWithNullMessageAndCause() {
        Throwable cause = new RuntimeException("Cause");
        ValidationException exception = new ValidationException(null, cause);
        
        assertNull(exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
    
    @Test
    @DisplayName("Test ValidationException with null cause")
    void testConstructorWithNullCause() {
        String message = "Message without cause";
        ValidationException exception = new ValidationException(message, null);
        
        assertEquals(message, exception.getMessage());
        assertNull(exception.getCause());
    }
    
    @Test
    @DisplayName("Test ValidationException cause chaining")
    void testCauseChaining() {
        IllegalArgumentException rootCause = new IllegalArgumentException("Root cause");
        RuntimeException intermediateCause = new RuntimeException("Intermediate cause", rootCause);
        ValidationException exception = new ValidationException("Final message", intermediateCause);
        
        assertEquals("Final message", exception.getMessage());
        assertEquals(intermediateCause, exception.getCause());
        assertEquals(rootCause, exception.getCause().getCause());
    }
}
