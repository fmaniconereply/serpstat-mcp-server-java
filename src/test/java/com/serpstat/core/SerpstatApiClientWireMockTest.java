package com.serpstat.core;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * WireMock example test for SerpstatApiClient
 * Demonstrates how to test HTTP functionality without real API calls
 */
class SerpstatApiClientWireMockTest {

    private WireMockServer wireMockServer;
    private TestableSerpstatApiClient client;
    private static final int WIREMOCK_PORT = 8089;

    @BeforeEach
    void setUp() {
        // Start WireMock server
        wireMockServer = new WireMockServer(WIREMOCK_PORT);
        wireMockServer.start();
        
        // Configure WireMock client
        WireMock.configureFor("localhost", WIREMOCK_PORT);
        
        // Create testable client that points to WireMock server
        client = new TestableSerpstatApiClient("test-token", "http://localhost:" + WIREMOCK_PORT);
    }

    @AfterEach
    void tearDown() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    void shouldMakeSuccessfulApiCall() throws Exception {
        // Given: Mock successful API response
        stubFor(post(urlPathEqualTo("/v4/"))
                .withQueryParam("token", equalTo("test-token"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8"))
                .withRequestBody(containing("api_stats"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBody("""
                            {
                                "id": 1,
                                "result": {
                                    "credits_left": 1000,
                                    "credits_total": 10000
                                }
                            }
                            """)));

        // When: Make API call
        Map<String, Object> params = Map.of();
        SerpstatApiResponse response = client.callMethod("api_stats", params);        // Then: Verify response
        assertThat(response).isNotNull();
        assertThat(response.getMethod()).isEqualTo("api_stats");
        assertThat(response.getResult().get("credits_left").asInt()).isEqualTo(1000);

        // Verify HTTP request was made correctly
        verify(postRequestedFor(urlPathEqualTo("/v4/"))
                .withQueryParam("token", equalTo("test-token"))
                .withHeader("Content-Type", equalTo("application/json; charset=UTF-8")));
    }

    @Test
    void shouldHandleApiError() {
        // Given: Mock API error response
        stubFor(post(urlPathEqualTo("/v4/"))
                .withQueryParam("token", equalTo("test-token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBody("""
                            {
                                "id": 1,
                                "error": {
                                    "message": "Invalid API token",
                                    "code": -32001
                                }
                            }
                            """)));

        // When & Then: Expect exception
        assertThatThrownBy(() -> client.callMethod("api_stats", Map.of()))
                .isInstanceOf(SerpstatApiException.class)
                .hasMessageContaining("Serpstat API Error: Invalid API token");
    }

    @Test
    void shouldHandleHttpError() {
        // Given: Mock HTTP error response
        stubFor(post(urlPathEqualTo("/v4/"))
                .withQueryParam("token", equalTo("test-token"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("Internal Server Error")));

        // When & Then: Expect exception
        assertThatThrownBy(() -> client.callMethod("api_stats", Map.of()))
                .isInstanceOf(SerpstatApiException.class)
                .hasMessageContaining("HTTP Error: 500");
    }

    @Test
    void shouldSendCorrectJsonRpcRequest() throws Exception {
        // Given: Mock any response
        stubFor(post(urlPathEqualTo("/v4/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBody("""
                            {
                                "id": 1,
                                "result": {}
                            }
                            """)));

        // When: Make API call with parameters
        Map<String, Object> params = Map.of(
                "domain", "example.com",
                "se", "g_us"
        );
        client.callMethod("domain_keywords", params);

        // Then: Verify JSON-RPC request format
        verify(postRequestedFor(urlPathEqualTo("/v4/"))
                .withRequestBody(matchingJsonPath("$.id", equalTo("1")))
                .withRequestBody(matchingJsonPath("$.method", equalTo("domain_keywords")))
                .withRequestBody(matchingJsonPath("$.params.domain", equalTo("example.com")))
                .withRequestBody(matchingJsonPath("$.params.se", equalTo("g_us"))));
    }

    @Test
    void shouldHandleComplexJsonResponse() throws Exception {
        // Given: Mock complex API response
        stubFor(post(urlPathEqualTo("/v4/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json; charset=UTF-8")
                        .withBody("""
                            {
                                "id": 1,
                                "result": {
                                    "keywords": [
                                        {
                                            "keyword": "example",
                                            "position": 1,
                                            "volume": 1000
                                        },
                                        {
                                            "keyword": "test",
                                            "position": 5,
                                            "volume": 500
                                        }
                                    ],
                                    "total_count": 2
                                }
                            }
                            """)));

        // When: Make API call
        SerpstatApiResponse response = client.callMethod("domain_keywords", Map.of("domain", "example.com"));        // Then: Verify complex response parsing
        assertThat(response.getResult().get("total_count").asInt()).isEqualTo(2);
        assertThat(response.getResult().get("keywords").isArray()).isTrue();
        assertThat(response.getResult().get("keywords")).hasSize(2);
        assertThat(response.getResult().get("keywords").get(0).get("keyword").asText()).isEqualTo("example");
        assertThat(response.getResult().get("keywords").get(1).get("position").asInt()).isEqualTo(5);
    }

    /**
     * Testable version of SerpstatApiClient that allows custom API URL
     */
    private static class TestableSerpstatApiClient extends SerpstatApiClient {
        private final String customApiUrl;

        public TestableSerpstatApiClient(String apiToken, String apiUrl) {
            super(apiToken);
            this.customApiUrl = apiUrl;
        }

        @Override
        protected String getApiUrl() {
            return customApiUrl + "/v4";
        }
    }
}
