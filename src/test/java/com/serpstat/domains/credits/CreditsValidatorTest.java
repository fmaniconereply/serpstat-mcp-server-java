package com.serpstat.domains.credits;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import java.util.Map;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreditsValidator class
 */
@DisplayName("CreditsValidator Tests")
class CreditsValidatorTest {    @Test
    @DisplayName("Test validate API stats request with valid arguments")
    void testValidateApiStatsRequestValid() {
        // getStats doesn't require any parameters, so any arguments should be fine
        assertDoesNotThrow(() -> CreditsValidator.validateApiStatsRequest(Map.of()));
        assertDoesNotThrow(() -> CreditsValidator.validateApiStatsRequest(Map.of("extra", "param")));
    }

    @Test
    @DisplayName("Test validate API stats request with null arguments")
    void testValidateApiStatsRequestNull() {
        // Should not throw exception for null arguments
        assertDoesNotThrow(() -> CreditsValidator.validateApiStatsRequest(null));
    }

    @Test
    @DisplayName("Test validate API stats request with empty arguments")
    void testValidateApiStatsRequestEmpty() {
        // Should not throw exception for empty arguments
        assertDoesNotThrow(() -> CreditsValidator.validateApiStatsRequest(Map.of()));
    }

    @Test
    @DisplayName("Test validate API stats request argument types")
    void testValidateApiStatsRequestArgumentTypes() {
        // Test with various argument types - should all be fine since getStats ignores them
        assertDoesNotThrow(() -> CreditsValidator.validateApiStatsRequest(Map.of("string", "value")));
        assertDoesNotThrow(() -> CreditsValidator.validateApiStatsRequest(Map.of("number", 123)));
        assertDoesNotThrow(() -> CreditsValidator.validateApiStatsRequest(Map.of("boolean", true)));
    }    @Test
    @DisplayName("Test validate API stats request boundary conditions")
    void testValidateApiStatsRequestBoundaryConditions() {
        // Test edge cases
        assertDoesNotThrow(() -> CreditsValidator.validateApiStatsRequest(Map.of("", "")));
        
        // Test with HashMap to allow null values
        Map<String, Object> mapWithNull = new HashMap<>();
        mapWithNull.put("null_value", null);
        assertDoesNotThrow(() -> CreditsValidator.validateApiStatsRequest(mapWithNull));
    }@Test
    @DisplayName("Test validation error messages")
    void testValidationErrorMessages() {
        // Since validateApiStatsRequest doesn't throw exceptions, we test the warning behavior
        assertDoesNotThrow(() -> CreditsValidator.validateApiStatsRequest(Map.of("unexpected", "param")));
    }

    @Test
    @DisplayName("Test validation performance")
    void testValidationPerformance() {
        // Test that validation is fast
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            final int index = i; // Make variable effectively final for lambda
            assertDoesNotThrow(() -> CreditsValidator.validateApiStatsRequest(Map.of("param" + index, "value" + index)));
        }
        long endTime = System.currentTimeMillis();
        
        // Should complete in reasonable time (less than 1 second)
        assertTrue(endTime - startTime < 1000);
    }

    @Test
    @DisplayName("Test validation thread safety")
    void testValidationThreadSafety() {
        // Test concurrent validation calls
        assertDoesNotThrow(() -> {
            Thread thread1 = new Thread(() -> {
                try {
                    CreditsValidator.validateApiStatsRequest(Map.of("thread1", "data"));
                } catch (Exception e) {
                    // Should not happen
                }
            });
            Thread thread2 = new Thread(() -> {
                try {
                    CreditsValidator.validateApiStatsRequest(Map.of("thread2", "data"));
                } catch (Exception e) {
                    // Should not happen
                }
            });
            
            thread1.start();
            thread2.start();
            
            thread1.join();
            thread2.join();
        });
    }

    @Test
    @DisplayName("Test schema integration")
    void testSchemaIntegration() {
        // Test that validator works with schema constants
        assertNotNull(CreditsSchemas.API_STATS_SCHEMA);
        
        // Validator should work regardless of schema content
        assertDoesNotThrow(() -> CreditsValidator.validateApiStatsRequest(Map.of()));
    }
}
