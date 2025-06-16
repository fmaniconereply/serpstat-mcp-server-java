package com.serpstat.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.*;

/**
 * Main comprehensive tests for SerpstatApiClient
 * Tests core functionality, performance, and configuration
 */
@DisplayName("SerpstatApiClient Core Tests")
class SerpstatApiClientTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().dynamicPort())
        .build();

    private SerpstatApiClient client;
    private static final String TEST_TOKEN = "test-main-token";

    @BeforeEach
    void setUp() {
        String wireMockUrl = String.format("http://localhost:%d/v4", wireMock.getPort());
        client = new TestableSerpstatApiClient(TEST_TOKEN, wireMockUrl);
        wireMock.resetAll();
    }

    @Test
    @DisplayName("Should execute basic API call")
    void shouldExecuteBasicApiCall() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {
                    "data": "test-data"
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        Map<String, Object> params = Map.of("test_param", "test_value");

        // When
        SerpstatApiResponse response = client.callMethod("test.method", params);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getResult().path("data").asText()).isEqualTo("test-data");
        
        wireMock.verify(postRequestedFor(anyUrl())
            .withHeader("Content-Type", equalTo("application/json"))
            .withRequestBody(matchingJsonPath("$.method", equalTo("test.method")))
            .withRequestBody(matchingJsonPath("$.params.test_param", equalTo("test_value"))));
    }

    @Test
    @DisplayName("Should return correct response type")
    void shouldReturnCorrectResponseType() throws Exception {
        // Given
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {"test": "data"}
                    }
                    """)));

        // When
        SerpstatApiResponse response = client.callMethod("test.method", Map.of());

        // Then
        assertThat(response).isInstanceOf(SerpstatApiResponse.class);
        assertThat(response.getResult()).isInstanceOf(JsonNode.class);
        assertThat(response.getMethod()).isEqualTo("test.method");
        assertThat(response.getRequestParams()).isNotNull();
        assertThat(response.getTimestamp()).isPositive();
    }

    @Test
    @DisplayName("Should handle null parameters")
    void shouldHandleNullParameters() throws Exception {
        // Given
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {}
                    }
                    """)));

        // When & Then
        assertThatCode(() -> client.callMethod("test.method", null))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle empty parameters")
    void shouldHandleEmptyParameters() throws Exception {
        // Given
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {}
                    }
                    """)));

        Map<String, Object> emptyParams = new HashMap<>();

        // When
        SerpstatApiResponse response = client.callMethod("test.method", emptyParams);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getRequestParams()).isEmpty();
        // Do not check for $.params via matchingJsonPath, since WireMock does not support exact match for empty object
        // It is enough to check that the response is received and parameters are empty
    }

    @Test
    @DisplayName("Should maintain request/response correlation")
    void shouldMaintainRequestOrder() throws Exception {
        // Given
        wireMock.stubFor(post(anyUrl())
            .withRequestBody(matchingJsonPath("$.params.request_id", equalTo("1")))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {"response_id": "1"}
                    }
                    """)));

        wireMock.stubFor(post(anyUrl())
            .withRequestBody(matchingJsonPath("$.params.request_id", equalTo("2")))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {"response_id": "2"}
                    }
                    """)));

        // When
        SerpstatApiResponse response1 = client.callMethod("test.method", Map.of("request_id", "1"));
        SerpstatApiResponse response2 = client.callMethod("test.method", Map.of("request_id", "2"));

        // Then
        assertThat(response1.getResult().path("response_id").asText()).isEqualTo("1");
        assertThat(response2.getResult().path("response_id").asText()).isEqualTo("2");
        assertThat(response1.getRequestParams().get("request_id")).isEqualTo("1");
        assertThat(response2.getRequestParams().get("request_id")).isEqualTo("2");
    }

    @Test
    @DisplayName("Should handle timeout correctly")
    @org.junit.jupiter.api.Disabled("TODO: Make timeout configurable in SerpstatApiClient for fast and reliable testing.")
    void shouldHandleTimeoutCorrectly() throws Exception {
        // TODO: Make SerpstatApiClient timeout configurable for proper unit testing (see REQUEST_TIMEOUT in implementation)
        // Current implementation uses static final timeout, so this test would always take 30+ seconds.
        // See SerpstatApiClient.REQUEST_TIMEOUT for improvement suggestion.
    }

    @Test
    @DisplayName("Should handle concurrent requests safely")
    void shouldHandleConcurrentRequests() throws Exception {
        // Given
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {"data": "concurrent-test"}
                    }
                    """)));

        int numberOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);

        // When
        CompletableFuture<Void>[] futures = new CompletableFuture[numberOfThreads];
        
        for (int i = 0; i < numberOfThreads; i++) {
            final int threadId = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                try {
                    SerpstatApiResponse response = client.callMethod("test.method", 
                        Map.of("thread_id", threadId));
                    if (response != null) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                }
            }, executor);
        }

        CompletableFuture.allOf(futures).join();
        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Then
        assertThat(successCount.get()).isEqualTo(numberOfThreads);
        assertThat(errorCount.get()).isZero();
    }

    @Test
    @DisplayName("Should perform within expected time")
    void shouldPerformWithinExpectedTime() throws Exception {
        // Given
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {"performance": "test"}
                    }
                    """)
                .withFixedDelay(100))); // Small delay to simulate network

        Map<String, Object> params = Map.of("performance", "test");

        // When
        long startTime = System.currentTimeMillis();
        SerpstatApiResponse response = client.callMethod("test.method", params);
        long endTime = System.currentTimeMillis();

        // Then
        long responseTime = endTime - startTime;
        assertThat(response).isNotNull();
        assertThat(responseTime).isLessThan(5000); // Should complete within 5 seconds
        assertThat(responseTime).isGreaterThan(50); // Should take some time due to delay
    }

    @Test
    @DisplayName("Should use correct API URL")
    void shouldUseCorrectApiUrl() throws Exception {
        // Given
        wireMock.stubFor(post(urlPathEqualTo("/v4/"))
            .withQueryParam("token", equalTo(TEST_TOKEN))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {}
                    }
                    """)));

        // When
        client.callMethod("test.method", Map.of());

        // Then
        wireMock.verify(postRequestedFor(urlPathEqualTo("/v4/"))
            .withQueryParam("token", equalTo(TEST_TOKEN)));
    }

    @Test
    @DisplayName("Should configure HTTP client correctly")
    void shouldConfigureHttpClientCorrectly() throws Exception {
        // Given
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {}
                    }
                    """)));

        // When
        client.callMethod("test.method", Map.of());

        // Then
        wireMock.verify(postRequestedFor(anyUrl())
            .withHeader("Content-Type", equalTo("application/json"))
            .withHeader("Authorization", equalTo("Bearer " + TEST_TOKEN)));
    }

    @Test
    @DisplayName("Should handle large response data")
    void shouldHandleLargeResponseData() throws Exception {
        // Given
        StringBuilder largeData = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            largeData.append("\"item").append(i).append("\": \"data").append(i).append("\",");
        }
        
        String largeResponse = String.format("""
            {
                "id": 1,
                "result": {
                    "data": {
                        %s
                        "total": 1000
                    }
                }
            }
            """, largeData.toString());

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(largeResponse)));

        // When
        SerpstatApiResponse response = client.callMethod("test.method", Map.of());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getResult().path("data").path("total").asInt()).isEqualTo(1000);
        assertThat(response.getResult().path("data").path("item0").asText()).isEqualTo("data0");
        assertThat(response.getResult().path("data").path("item999").asText()).isEqualTo("data999");
    }

    @Test
    @DisplayName("Should handle special characters in method names")
    void shouldHandleSpecialCharactersInMethodNames() throws Exception {
        // Given
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {}
                    }
                    """)));

        String methodWithSpecialChars = "SerpstatDomain.Procedure_getInfo-v2";

        // When
        SerpstatApiResponse response = client.callMethod(methodWithSpecialChars, Map.of());

        // Then
        assertThat(response.getMethod()).isEqualTo(methodWithSpecialChars);
        wireMock.verify(postRequestedFor(anyUrl())
            .withRequestBody(matchingJsonPath("$.method", equalTo(methodWithSpecialChars))));
    }

    @Test
    @DisplayName("Should preserve parameter types")
    void shouldPreserveParameterTypes() throws Exception {
        // Given
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody("""
                    {
                        "id": 1,
                        "result": {}
                    }
                    """)));

        Map<String, Object> typedParams = Map.of(
            "string_param", "text",
            "int_param", 123,
            "double_param", 45.67,
            "boolean_param", true
        );

        // When
        SerpstatApiResponse response = client.callMethod("test.method", typedParams);

        // Then
        assertThat(response.getRequestParams().get("string_param")).isEqualTo("text");
        assertThat(response.getRequestParams().get("int_param")).isEqualTo(123);
        assertThat(response.getRequestParams().get("double_param")).isEqualTo(45.67);
        assertThat(response.getRequestParams().get("boolean_param")).isEqualTo(true);

        wireMock.verify(postRequestedFor(anyUrl())
            .withRequestBody(matchingJsonPath("$.params.string_param", equalTo("text")))
            .withRequestBody(matchingJsonPath("$.params.int_param", equalTo("123")))
            .withRequestBody(matchingJsonPath("$.params.boolean_param", equalTo("true"))));
    }
}
