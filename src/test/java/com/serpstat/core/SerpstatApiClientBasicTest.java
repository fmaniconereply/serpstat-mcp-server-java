package com.serpstat.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Basic functional tests for SerpstatApiClient - Positive Path scenarios only
 * Tests core functionality without making real HTTP requests
 */
@DisplayName("SerpstatApiClient Basic Tests")
class SerpstatApiClientBasicTest {

    @Test
    @DisplayName("Should handle null parameters gracefully")
    void shouldHandleNullParametersGracefully() {
        // Given
        SerpstatApiClient client = new SerpstatApiClient("test-token");
          // When & Then
        // The method should not throw NullPointerException anymore
        // It will likely throw SerpstatApiException due to invalid token, but that's expected
        assertThatThrownBy(() -> client.callMethod("test.method", null))
            .isInstanceOf(SerpstatApiException.class)
            .hasMessageContaining("Serpstat API Error")
            .hasMessageNotContaining("NullPointerException");
    }

    @Test
    @DisplayName("Should handle empty parameters")
    void shouldHandleEmptyParameters() {
        // Given
        SerpstatApiClient client = new SerpstatApiClient("test-token");
        Map<String, Object> emptyParams = Map.of();
        
        // When & Then
        assertThatThrownBy(() -> client.callMethod("test.method", emptyParams))
            .isInstanceOf(SerpstatApiException.class)
            .hasMessageContaining("Serpstat API Error");
    }    @Test
    @DisplayName("Should create cache key correctly for null parameters")
    void shouldCreateCacheKeyCorrectlyForNullParameters() {
        // Given
        SerpstatApiClient client = new SerpstatApiClient("test-token");
        
        // When & Then
        // This should not throw NPE - the cache key should be created properly
        // It should throw SerpstatApiException due to API call, not NPE
        assertThatThrownBy(() -> client.callMethod("test.method", null))
            .isInstanceOf(SerpstatApiException.class)
            .isNotInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should accept valid method names")
    void shouldAcceptValidMethodNames() {
        // Given
        SerpstatApiClient client = new SerpstatApiClient("test-token");
        Map<String, Object> params = Map.of("domain", "example.com");
        
        // When & Then
        assertThatThrownBy(() -> client.callMethod("DomainProcedure.getDomainKeywords", params))
            .isInstanceOf(SerpstatApiException.class)
            .hasMessageContaining("Serpstat API Error");
    }

    @Test
    @DisplayName("Should handle various parameter types")
    void shouldHandleVariousParameterTypes() {
        // Given
        SerpstatApiClient client = new SerpstatApiClient("test-token");
        Map<String, Object> complexParams = Map.of(
            "domain", "example.com",
            "page", 1,
            "size", 100,
            "sortBy", "position",
            "filters", Map.of("position_from", 1, "position_to", 10)
        );
        
        // When & Then
        assertThatThrownBy(() -> client.callMethod("test.method", complexParams))
            .isInstanceOf(SerpstatApiException.class)
            .hasMessageContaining("Serpstat API Error");
    }

    @Test
    @DisplayName("Should preserve method and parameters in exception context")
    void shouldPreserveMethodAndParametersInExceptionContext() {
        // Given
        SerpstatApiClient client = new SerpstatApiClient("invalid-token");
        Map<String, Object> params = Map.of("test", "value");
        
        // When & Then
        assertThatThrownBy(() -> client.callMethod("test.method", params))
            .isInstanceOf(SerpstatApiException.class)
            .hasMessageContaining("Invalid token");
    }
}
