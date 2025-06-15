package com.serpstat.core;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.*;

/**
 * Caching mechanism tests for SerpstatApiClient
 * Tests cache hits, misses, and configuration behavior
 * 
 * TODO: DISABLED - Convert to WireMock instead of real API calls
 * These tests currently make real HTTP requests to Serpstat API and fail with "Invalid token!"
 * Need to refactor to use WireMock for HTTP mocking like SerpstatApiClientPositivePathTest
 */
@Disabled("TODO: Convert to WireMock - currently makes real API calls that fail with Invalid token!")
@DisplayName("SerpstatApiClient Cache Tests")
class SerpstatApiClientCacheTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().port(8090))
        .build();

    private SerpstatApiClient client;
    private static final String TEST_TOKEN = "test-cache-token";

    @BeforeEach
    void setUp() {
        client = new SerpstatApiClient(TEST_TOKEN);
        wireMock.resetAll();
    }

    @Test
    @DisplayName("Should return cached response for same request")
    void shouldReturnCachedResponse() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {
                    "data": "cached-data",
                    "timestamp": "2025-06-15T10:00:00Z"
                }
            }
            """;

        // Stubbing for only one request - second should come from cache
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        Map<String, Object> params = Map.of("domain", "example.com");
        String method = "SerpstatDomainProcedure.getInfo";

        // When - Make first request
        SerpstatApiResponse firstResponse = client.callMethod(method, params);
        
        // When - Make identical second request
        SerpstatApiResponse secondResponse = client.callMethod(method, params);

        // Then
        assertThat(firstResponse).isNotNull();
        assertThat(secondResponse).isNotNull();
        assertThat(firstResponse.getResult().path("data").asText()).isEqualTo("cached-data");
        assertThat(secondResponse.getResult().path("data").asText()).isEqualTo("cached-data");
        
        // Verify only one HTTP request was made
        wireMock.verify(1, postRequestedFor(anyUrl()));
    }

    @Test
    @DisplayName("Should use cache key correctly")
    void shouldUseCacheKeyCorrectly() throws Exception {
        // Given
        String response1 = """
            {
                "id": 1,
                "result": {"data": "response1"}
            }
            """;
        
        String response2 = """
            {
                "id": 1,
                "result": {"data": "response2"}
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .inScenario("cache-key-test")
            .whenScenarioStateIs("STARTED")
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(response1))
            .willSetStateTo("FIRST_CALLED"));

        wireMock.stubFor(post(anyUrl())
            .inScenario("cache-key-test")
            .whenScenarioStateIs("FIRST_CALLED")
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(response2)));

        Map<String, Object> params1 = Map.of("domain", "example.com");
        Map<String, Object> params2 = Map.of("domain", "different.com");
        String method = "SerpstatDomainProcedure.getInfo";

        // When
        SerpstatApiResponse firstResponse = client.callMethod(method, params1);
        SerpstatApiResponse secondResponse = client.callMethod(method, params2);
        SerpstatApiResponse thirdResponse = client.callMethod(method, params1); // Should be cached

        // Then
        assertThat(firstResponse.getResult().path("data").asText()).isEqualTo("response1");
        assertThat(secondResponse.getResult().path("data").asText()).isEqualTo("response2");
        assertThat(thirdResponse.getResult().path("data").asText()).isEqualTo("response1"); // From cache
        
        // Verify two HTTP requests were made (third was cached)
        wireMock.verify(2, postRequestedFor(anyUrl()));
    }

    @Test
    @DisplayName("Should not make HTTP request on cache hit")
    void shouldNotMakeHttpRequestOnCacheHit() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {"data": "test"}
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        Map<String, Object> params = Map.of("test", "value");
        String method = "test.method";

        // When
        client.callMethod(method, params); // First call - HTTP request
        long startTime = System.currentTimeMillis();
        client.callMethod(method, params); // Second call - from cache
        long endTime = System.currentTimeMillis();

        // Then
        // Cache hit should be much faster than HTTP request
        assertThat(endTime - startTime).isLessThan(100); // Should be very fast
        wireMock.verify(1, postRequestedFor(anyUrl())); // Only one HTTP request
    }

    @Test
    @DisplayName("Should make HTTP request on cache miss")
    void shouldMakeHttpRequestOnCacheMiss() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {"data": "test"}
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        Map<String, Object> params1 = Map.of("domain", "example1.com");
        Map<String, Object> params2 = Map.of("domain", "example2.com");
        String method = "SerpstatDomainProcedure.getInfo";

        // When
        client.callMethod(method, params1); // First request
        client.callMethod(method, params2); // Different params - cache miss

        // Then
        wireMock.verify(2, postRequestedFor(anyUrl()));
    }

    @Test
    @DisplayName("Should cache response after HTTP request")
    void shouldCacheResponseAfterHttpRequest() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {"data": "cached-value"}
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        Map<String, Object> params = Map.of("domain", "example.com");
        String method = "SerpstatDomainProcedure.getInfo";

        // When
        SerpstatApiResponse firstResponse = client.callMethod(method, params);
        
        // Reset WireMock to ensure no more requests can be made
        wireMock.resetAll();
        
        // This should succeed from cache even though WireMock is reset
        SerpstatApiResponse cachedResponse = client.callMethod(method, params);

        // Then
        assertThat(firstResponse.getResult().path("data").asText()).isEqualTo("cached-value");
        assertThat(cachedResponse.getResult().path("data").asText()).isEqualTo("cached-value");
    }

    @Test
    @DisplayName("Should handle different parameters correctly")
    void shouldHandleDifferentParametersCorrectly() throws Exception {
        // Given
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {"data": "response"}
                    }
                    """)));

        String method = "SerpstatDomainProcedure.getInfo";
        Map<String, Object> params1 = Map.of("domain", "example.com", "size", 10);
        Map<String, Object> params2 = Map.of("domain", "example.com", "size", 20);
        Map<String, Object> params3 = Map.of("domain", "different.com", "size", 10);

        // When
        client.callMethod(method, params1);
        client.callMethod(method, params2);
        client.callMethod(method, params3);
        client.callMethod(method, params1); // Should be cached

        // Then
        // Three different parameter combinations = 3 HTTP requests
        // Fourth call with same params as first should be cached
        wireMock.verify(3, postRequestedFor(anyUrl()));
    }

    @Test
    @DisplayName("Should respect cache size limit")
    void shouldRespectCacheSize() throws Exception {
        // Given
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {"data": "test"}
                    }
                    """)));

        String method = "test.method";

        // When - Fill cache beyond typical limit (testing behavior, not exact limit)
        for (int i = 0; i < 10; i++) {
            Map<String, Object> params = Map.of("id", i);
            client.callMethod(method, params);
        }

        // Then - All requests should have been made (cache doesn't interfere with different params)
        wireMock.verify(10, postRequestedFor(anyUrl()));
    }

    @Test
    @DisplayName("Should handle cache expiration correctly")
    void shouldRespectCacheExpiration() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {"data": "test"}
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        Map<String, Object> params = Map.of("domain", "example.com");
        String method = "SerpstatDomainProcedure.getInfo";

        // When
        client.callMethod(method, params); // First call
        
        // Note: Cache expiration is 60 minutes by default, so we can't easily test
        // expiration in a unit test. This test verifies the cache works within
        // the expiration window.
        client.callMethod(method, params); // Second call - should be cached

        // Then
        wireMock.verify(1, postRequestedFor(anyUrl()));
    }

    @Test
    @DisplayName("Should handle method name in cache key")
    void shouldIncludeMethodNameInCacheKey() throws Exception {
        // Given
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {"data": "test"}
                    }
                    """)));

        Map<String, Object> params = Map.of("domain", "example.com");
        String method1 = "SerpstatDomainProcedure.getInfo";
        String method2 = "SerpstatDomainProcedure.getKeywords";

        // When - Same params, different methods
        client.callMethod(method1, params);
        client.callMethod(method2, params);
        client.callMethod(method1, params); // Should be cached

        // Then - Two different methods + one cached = 2 HTTP requests
        wireMock.verify(2, postRequestedFor(anyUrl()));
    }
}
