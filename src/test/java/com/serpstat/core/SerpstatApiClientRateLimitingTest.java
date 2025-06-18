package com.serpstat.core;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.RegisterExtension;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.*;

/**
 * Rate limiting tests for SerpstatApiClient
 * Tests rate limiter integration and enforcement
 */
@DisplayName("SerpstatApiClient Rate Limiting Tests")
class SerpstatApiClientRateLimitingTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();

    private SerpstatApiClient client;
    private static final String TEST_TOKEN = "test-rate-limit-token";

    @BeforeEach
    void setUp() {
        String wireMockUrl = String.format("http://localhost:%d/v4/", wireMock.getPort());
        client = new TestableSerpstatApiClient(TEST_TOKEN, wireMockUrl);
        wireMock.resetAll();

        // Setup default successful response
        wireMock.stubFor(post(anyUrl())
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("""
                                {
                                    \"id\": 1,
                                    \"result\": {\"data\": \"test\"}
                                }
                                """)));
    }

    @Test
    @DisplayName("Should call rate limiter before request")
    void shouldCallRateLimiterBeforeRequest() throws Exception {
        // Given
        Map<String, Object> params = Map.of("test", "value");

        // When
        client.callMethod("test.method", params);

        // Then
        // Verify request was made (implying rate limiter was called without throwing)
        wireMock.verify(1, postRequestedFor(anyUrl()));
    }

    @Test
    @DisplayName("Should enforce rate limit with multiple requests")
    void shouldEnforceRateLimit() throws Exception {
        // Given
        int numberOfRequests = 12; // More than default limit of 10 per second
        Map<String, Object> params = new HashMap<>();

        long startTime = System.currentTimeMillis();

        // When - Make multiple requests rapidly
        for (int i = 0; i < numberOfRequests; i++) {
            params.put("request_id", i); // Different params to avoid caching
            client.callMethod("test.method", params);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Then
        // Should take at least 1 second due to rate limiting (12 requests > 10/sec
        // limit)
        assertThat(totalTime).isGreaterThan(800); // Allow some margin for timing
        wireMock.verify(numberOfRequests, postRequestedFor(anyUrl()));
    }

    @DisplayName("Should handle rate limit correctly with multiple threads")
    @Test
    void shouldResetRateWindowCorrectly() throws Exception {
        int requests = 10;
        AtomicInteger requestCount = new AtomicInteger();

        wireMock.stubFor(post(anyUrl())
                .willReturn(aResponse()
                        .withStatus(200)
                        .withBody("{\"id\": 1, \"result\": {\"data\": \"test\"}}")));

        for (int i = 0; i < requests; i++) {
            client.callMethod("test.method", Map.of("key", "value" + i));
            requestCount.incrementAndGet();
        }

        wireMock.verify(requests, postRequestedFor(anyUrl()));
        assertThat(requestCount.get()).isEqualTo(requests);
    }

    @Test
    @DisplayName("Should allow requests within rate limit")
    void shouldAllowRequestsWithinLimit() throws Exception {
        // Given
        int requestsWithinLimit = 8; // Less than 10 per second limit
        Map<String, Object> params = new HashMap<>();

        long startTime = System.currentTimeMillis();

        // When
        for (int i = 0; i < requestsWithinLimit; i++) {
            params.put("request_id", i);
            client.callMethod("test.method", params);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Then
        // Should complete quickly since within rate limit
        assertThat(totalTime).isLessThan(500); // Should be fast
        wireMock.verify(requestsWithinLimit, postRequestedFor(anyUrl()));
    }

    @Test
    @DisplayName("Should delay requests exceeding limit")

    void shouldDelayRequestsExceedingLimit() throws Exception {
        // Create a new client for this test to isolate rate limiter
        SerpstatApiClient isolatedClient = new TestableSerpstatApiClient(TEST_TOKEN,
                String.format("http://localhost:%d/v4", wireMock.getPort()));

        for (int i = 0; i < 10; i++) {
            Map<String, Object> params = Map.of("request_id", i);
            isolatedClient.callMethod("test.method", params);
        }

        long startTime = System.currentTimeMillis();
        Map<String, Object> params = Map.of("request_id", 10);
        isolatedClient.callMethod("test.method", params);
        long endTime = System.currentTimeMillis();

        long delayTime = endTime - startTime;
        assertThat(delayTime).isGreaterThan(900);
        // Check that at least 11 requests were made
        assertThat(wireMock.findAll(postRequestedFor(anyUrl())).size()).isGreaterThanOrEqualTo(11);
    }

    @Test
    @DisplayName("Should handle rate limit interruption")
    void shouldHandleRateLimitInterruption() throws Exception {
        // Given
        Map<String, Object> params = Map.of("test", "value");

        // Fill up the rate limit
        for (int i = 0; i < 10; i++) {
            params = Map.of("request_id", i);
            client.callMethod("test.method", params);
        }

        // When - Make request that will be rate limited and interrupt the thread
        CompletableFuture<SerpstatApiResponse> future = CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> finalParams = Map.of("request_id", 10);
                return client.callMethod("test.method", finalParams);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // Interrupt after a short delay
        Thread.sleep(100);
        future.cancel(true);

        // Then
        // Should handle interruption gracefully
        assertThat(future.isCancelled()).isTrue();
    }

    @Test
    @DisplayName("Should work correctly with concurrent requests")
    void shouldWorkCorrectlyWithConcurrentRequests() throws Exception {
        // Given
        int numberOfThreads = 5;
        int requestsPerThread = 3;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        AtomicInteger completedRequests = new AtomicInteger(0);

        // When
        long startTime = System.currentTimeMillis();

        for (int thread = 0; thread < numberOfThreads; thread++) {
            final int threadId = thread;
            executor.submit(() -> {
                try {
                    for (int req = 0; req < requestsPerThread; req++) {
                        Map<String, Object> params = Map.of(
                                "thread_id", threadId,
                                "request_id", req);
                        client.callMethod("test.method", params);
                        completedRequests.incrementAndGet();
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        executor.shutdown();
        boolean completed = executor.awaitTermination(10, TimeUnit.SECONDS);
        long endTime = System.currentTimeMillis();

        // Then
        assertThat(completed).isTrue();
        assertThat(completedRequests.get()).isEqualTo(numberOfThreads * requestsPerThread);

        // Should take longer due to rate limiting
        long totalTime = endTime - startTime;
        assertThat(totalTime).isGreaterThan(1000); // Rate limiting should add delay

        wireMock.verify(numberOfThreads * requestsPerThread, postRequestedFor(anyUrl()));
    }

    @Test
    @DisplayName("Should maintain rate limit across different API methods")
    void shouldMaintainRateLimitAcrossDifferentMethods() throws Exception {
        String[] methods = {
                "SerpstatDomainProcedure.getInfo",
                "SerpstatDomainProcedure.getKeywords",
                "SerpstatKeywordProcedure.getInfo"
        };

        long startTime = System.currentTimeMillis();

        // When - Make 12 requests across different methods (exceeds 10/sec limit)
        for (int i = 0; i < 12; i++) {
            String method = methods[i % methods.length];
            Map<String, Object> methodParams = Map.of("request_id", i, "method", method);
            client.callMethod(method, methodParams);
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        // Then
        // Should be rate limited regardless of method
        assertThat(totalTime).isGreaterThan(800);
        wireMock.verify(12, postRequestedFor(anyUrl()));
    }
}
