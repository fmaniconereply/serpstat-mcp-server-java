package com.serpstat.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.*;

/**
 * HTTP request/response tests for SerpstatApiClient
 * Tests request formation, response parsing, and error handling
 */
@DisplayName("SerpstatApiClient HTTP Tests")
class SerpstatApiClientHttpTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
        .options(wireMockConfig().dynamicPort())
        .build();

    private SerpstatApiClient client;
    private ObjectMapper objectMapper;
    private static final String TEST_TOKEN = "test-api-token";

    @BeforeEach
    void setUp() {
        String wireMockUrl = String.format("http://localhost:%d/v4", wireMock.getPort());
        client = new SerpstatApiClient(TEST_TOKEN, wireMockUrl);
        objectMapper = new ObjectMapper();
        wireMock.resetAll();
    }

    @Test
    @DisplayName("Should format JSON-RPC request correctly")
    void shouldFormatJsonRpcRequestCorrectly() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {
                    "data": ["test"]
                }
            }
            """;

        wireMock.stubFor(post(urlPathEqualTo("/v4/"))
            .withQueryParam("token", equalTo(TEST_TOKEN))
            .willReturn(aResponse()
                .withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withBody(successResponse)));

        Map<String, Object> params = Map.of("domain", "example.com");

        // When
        client.callMethod("SerpstatDomainProcedure.getInfo", params);

        // Then
        wireMock.verify(postRequestedFor(urlPathEqualTo("/v4/"))
            .withHeader("Content-Type", equalTo("application/json; charset=UTF-8")) // Исправлено ожидание заголовка
            .withRequestBody(matchingJsonPath("$.id", equalTo("1")))
            .withRequestBody(matchingJsonPath("$.method", equalTo("SerpstatDomainProcedure.getInfo")))
            .withRequestBody(matchingJsonPath("$.params.domain", equalTo("example.com"))));
    }

    @Test
    @DisplayName("Should include all required headers")
    void shouldIncludeAllRequiredHeaders() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {}
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        Map<String, Object> params = new HashMap<>();

        // When
        client.callMethod("test.method", params);

        // Then
        wireMock.verify(postRequestedFor(anyUrl())
            .withHeader("Content-Type", equalTo("application/json; charset=UTF-8")));
    }

    @Test
    @DisplayName("Should build correct URL with token")
    void shouldBuildCorrectUrlWithToken() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {}
            }
            """;

        wireMock.stubFor(post(urlPathEqualTo("/v4/"))
            .withQueryParam("token", equalTo(TEST_TOKEN))
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        Map<String, Object> params = new HashMap<>();

        // When
        client.callMethod("test.method", params);

        // Then
        wireMock.verify(postRequestedFor(urlPathEqualTo("/v4/"))
            .withQueryParam("token", equalTo(TEST_TOKEN)));
    }

    @Test
    @DisplayName("Should serialize parameters correctly")
    void shouldSerializeParametersCorrectly() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {}
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        Map<String, Object> complexParams = Map.of(
            "domain", "example.com",
            "size", 100,
            "filters", Map.of("position_from", 1, "position_to", 10),
            "keywords", java.util.List.of("keyword1", "keyword2")
        );

        // When
        client.callMethod("test.method", complexParams);

        // Then
        wireMock.verify(postRequestedFor(anyUrl())
            .withRequestBody(matchingJsonPath("$.params.domain", equalTo("example.com")))
            .withRequestBody(matchingJsonPath("$.params.size", equalTo("100")))
            .withRequestBody(matchingJsonPath("$.params.filters.position_from", equalTo("1")))
            .withRequestBody(matchingJsonPath("$.params.keywords[0]", equalTo("keyword1"))));
    }

    @Test
    @DisplayName("Should parse successful response")
    void shouldParseSuccessfulResponse() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {
                    "data": [
                        {
                            "domain": "example.com",
                            "keywords": 1500
                        }
                    ],
                    "summary_info": {
                        "total": 1,
                        "page": 1
                    }
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        Map<String, Object> params = Map.of("domain", "example.com");

        // When
        SerpstatApiResponse response = client.callMethod("SerpstatDomainProcedure.getInfo", params);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().path("data").isArray()).isTrue();
        assertThat(response.getResult().path("data").get(0).path("domain").asText()).isEqualTo("example.com");
        assertThat(response.getResult().path("summary_info").path("total").asInt()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return SerpstatApiResponse object")
    void shouldReturnSerpstatApiResponseObject() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {
                    "test": "data"
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        Map<String, Object> params = Map.of("param", "value");

        // When
        SerpstatApiResponse response = client.callMethod("test.method", params);

        // Then
        assertThat(response).isInstanceOf(SerpstatApiResponse.class);
        assertThat(response.getMethod()).isEqualTo("test.method");
        assertThat(response.getRequestParams()).containsEntry("param", "value");
        assertThat(response.getTimestamp()).isPositive();
    }

    @Test
    @DisplayName("Should handle empty result response")
    void shouldHandleEmptyResultResponse() throws Exception {
        // Given
        String emptyResponse = """
            {
                "id": 1,
                "result": {}
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(emptyResponse)));

        Map<String, Object> params = new HashMap<>();

        // When
        SerpstatApiResponse response = client.callMethod("test.method", params);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getResult()).isNotNull();
        assertThat(response.getResult().isEmpty()).isTrue();
    }

    @Test
    @DisplayName("Should handle complex JSON response")
    void shouldHandleComplexJsonResponse() throws Exception {
        // Given
        String complexResponse = """
            {
                "id": 1,
                "result": {
                    "data": [
                        {
                            "domain": "example.com",
                            "metrics": {
                                "keywords": 1500,
                                "traffic": 25000,
                                "visibility": 75.5
                            },
                            "competitors": ["competitor1.com", "competitor2.com"]
                        }
                    ],
                    "summary_info": {
                        "page": 1,
                        "page_total": 10,
                        "count": 1,
                        "total": 100
                    }
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(complexResponse)));

        Map<String, Object> params = Map.of("domain", "example.com");

        // When
        SerpstatApiResponse response = client.callMethod("SerpstatDomainProcedure.getInfo", params);

        // Then
        JsonNode result = response.getResult();
        assertThat(result.path("data").get(0).path("metrics").path("keywords").asInt()).isEqualTo(1500);
        assertThat(result.path("data").get(0).path("competitors").isArray()).isTrue();
        assertThat(result.path("summary_info").path("page_total").asInt()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should preserve request metadata")
    void shouldPreserveRequestMetadata() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {"test": "data"}
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        String method = "SerpstatDomainProcedure.getInfo";
        Map<String, Object> params = Map.of("domain", "example.com", "size", 50);

        // When
        SerpstatApiResponse response = client.callMethod(method, params);

        // Then
        assertThat(response.getMethod()).isEqualTo(method);
        assertThat(response.getRequestParams()).isEqualTo(params);
        assertThat(response.getRequestParams()).containsEntry("domain", "example.com");
        assertThat(response.getRequestParams()).containsEntry("size", 50);
    }

    @Test
    @DisplayName("Should set timestamp on response")
    void shouldSetTimestampOnResponse() throws Exception {
        // Given
        String successResponse = """
            {
                "id": 1,
                "result": {}
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(successResponse)));

        long beforeCall = System.currentTimeMillis();
        Map<String, Object> params = new HashMap<>();

        // When
        SerpstatApiResponse response = client.callMethod("test.method", params);

        // Then
        long afterCall = System.currentTimeMillis();
        assertThat(response.getTimestamp()).isBetween(beforeCall, afterCall);
    }

    @Test
    @DisplayName("Should throw exception on HTTP error")
    void shouldThrowExceptionOnHttpError() {
        // Given
        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(500)
                .withBody("Internal Server Error")));

        Map<String, Object> params = new HashMap<>();

        // When & Then
        assertThatThrownBy(() -> client.callMethod("test.method", params))
            .isInstanceOf(SerpstatApiException.class)
            .hasMessageContaining("HTTP Error: 500");
    }    @Test
    @DisplayName("Should throw exception on API error")
    void shouldThrowExceptionOnApiError() {
        // Given
        String errorResponse = """
            {
                "id": 1,
                "error": {
                    "code": -32000,
                    "message": "Invalid API token"
                }
            }
            """;

        wireMock.stubFor(post(anyUrl())
            .willReturn(aResponse()
                .withStatus(200)
                .withBody(errorResponse)));

        Map<String, Object> params = new HashMap<>();

        // When & Then
        assertThatThrownBy(() -> client.callMethod("test.method", params))
            .isInstanceOf(SerpstatApiException.class)
            .hasMessageContaining("Serpstat API Error: Invalid API token");
    }
}
