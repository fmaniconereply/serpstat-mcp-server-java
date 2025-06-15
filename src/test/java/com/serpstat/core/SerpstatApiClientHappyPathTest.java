package com.serpstat.core;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.junit.jupiter.api.*;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Comprehensive Happy Path test coverage for SerpstatApiClient.
 * Tests successful API interactions with mocked HTTP responses.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SerpstatApiClientHappyPathTest {

    private static WireMockServer wireMockServer;
    private TestableSerpstatApiClient client;
    private static final int WIREMOCK_PORT = 8090;

    @BeforeAll
    static void setUpWireMock() {
        // Start WireMock server once for all tests
        wireMockServer = new WireMockServer(WIREMOCK_PORT);
        wireMockServer.start();
        WireMock.configureFor("localhost", WIREMOCK_PORT);
    }

    @AfterAll
    static void tearDownWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @BeforeEach
    void setUp() {

        // Reset WireMock state before each test
        WireMock.reset();
        
        // Create testable client that points to WireMock server
        client = new TestableSerpstatApiClient("test-api-token", "http://localhost:" + WIREMOCK_PORT);
    }

    @Test
    @Order(1)
    @DisplayName("Should successfully call api_stats method")
    void shouldCallApiStats() throws Exception {
        // Given: Mock api_stats response
        stubFor(post(urlPathEqualTo("/v4/"))
                .withQueryParam("token", equalTo("test-api-token"))
                .withHeader("Content-Type", equalTo("application/json"))
                .withRequestBody(matchingJsonPath("$.method", equalTo("api_stats")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "id": 1,
                                "result": {
                                    "credits_left": 9850,
                                    "credits_total": 10000,
                                    "plan": "Standard",
                                    "plan_expire": "2025-12-31"
                                }
                            }
                            """)));

        // When: Call API method
        SerpstatApiResponse response = client.callMethod("api_stats", Map.of());

        // Then: Verify response
        assertThat(response).isNotNull();
        assertThat(response.getMethod()).isEqualTo("api_stats");
        assertThat(response.getResult().get("credits_left").asInt()).isEqualTo(9850);
        assertThat(response.getResult().get("credits_total").asInt()).isEqualTo(10000);
        assertThat(response.getResult().get("plan").asText()).isEqualTo("Standard");
        assertThat(response.getTimestamp()).isGreaterThan(0);

        // Verify correct HTTP request was made
        verify(exactly(1), postRequestedFor(urlPathEqualTo("/v4/"))
                .withQueryParam("token", equalTo("test-api-token"))
                .withHeader("Content-Type", equalTo("application/json")));
    }

    @Test
    @Order(2)
    @DisplayName("Should successfully call domain_keywords with parameters")
    void shouldCallDomainKeywords() throws Exception {
        // Given: Mock domain_keywords response
        stubFor(post(urlPathEqualTo("/v4/"))
                .withQueryParam("token", equalTo("test-api-token"))
                .withRequestBody(matchingJsonPath("$.method", equalTo("domain_keywords")))
                .withRequestBody(matchingJsonPath("$.params.domain", equalTo("example.com")))
                .withRequestBody(matchingJsonPath("$.params.se", equalTo("g_us")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "id": 1,
                                "result": {
                                    "keywords": [
                                        {
                                            "keyword": "example website",
                                            "position": 1,
                                            "volume": 1200,
                                            "cpc": 0.45,
                                            "competition": 0.8
                                        },
                                        {
                                            "keyword": "example site",
                                            "position": 3,
                                            "volume": 800,
                                            "cpc": 0.32,
                                            "competition": 0.6
                                        }
                                    ],
                                    "total_count": 2,
                                    "domain": "example.com",
                                    "se": "g_us"
                                }
                            }
                            """)));

        // When: Call API method with parameters
        Map<String, Object> params = Map.of(
                "domain", "example.com",
                "se", "g_us",
                "limit", 10
        );
        SerpstatApiResponse response = client.callMethod("domain_keywords", params);

        // Then: Verify response structure
        assertThat(response).isNotNull();
        assertThat(response.getMethod()).isEqualTo("domain_keywords");
        assertThat(response.getRequestParams()).containsEntry("domain", "example.com");
        assertThat(response.getRequestParams()).containsEntry("se", "g_us");
        
        // Verify keywords array
        assertThat(response.getResult().get("keywords").isArray()).isTrue();
        assertThat(response.getResult().get("keywords")).hasSize(2);
        assertThat(response.getResult().get("total_count").asInt()).isEqualTo(2);
        
        // Verify first keyword
        var firstKeyword = response.getResult().get("keywords").get(0);
        assertThat(firstKeyword.get("keyword").asText()).isEqualTo("example website");
        assertThat(firstKeyword.get("position").asInt()).isEqualTo(1);
        assertThat(firstKeyword.get("volume").asInt()).isEqualTo(1200);
        assertThat(firstKeyword.get("cpc").asDouble()).isEqualTo(0.45);
    }

    @Test
    @Order(3)
    @DisplayName("Should handle empty parameters successfully")
    void shouldHandleEmptyParameters() throws Exception {
        // Given: Mock response for method with empty parameters
        stubFor(post(urlPathEqualTo("/v4/"))
                .withRequestBody(matchingJsonPath("$.method", equalTo("api_stats")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "id": 1,
                                "result": {
                                    "credits_left": 5000,
                                    "credits_total": 10000
                                }
                            }
                            """)));

        // When: Call with empty parameters
        SerpstatApiResponse response = client.callMethod("api_stats", Map.of());

        // Then: Verify successful response
        assertThat(response).isNotNull();
        assertThat(response.getMethod()).isEqualTo("api_stats");
        assertThat(response.getResult().get("credits_left").asInt()).isEqualTo(5000);
    }

    @Test
    @Order(4)
    @DisplayName("Should handle null parameters successfully")
    void shouldHandleNullParameters() throws Exception {
        // Given: Mock response for method with null parameters
        stubFor(post(urlPathEqualTo("/v4/"))
                .withRequestBody(matchingJsonPath("$.method", equalTo("api_stats")))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "id": 1,
                                "result": {
                                    "credits_left": 7500,
                                    "credits_total": 10000
                                }
                            }
                            """)));

        // When: Call with null parameters (should be converted to empty map)
        SerpstatApiResponse response = client.callMethod("api_stats", null);

        // Then: Verify successful response
        assertThat(response).isNotNull();
        assertThat(response.getMethod()).isEqualTo("api_stats");
        assertThat(response.getResult().get("credits_left").asInt()).isEqualTo(7500);
        assertThat(response.getRequestParams()).isNotNull();
        assertThat(response.getRequestParams()).isEmpty();
    }

    @Test
    @Order(5)
    @DisplayName("Should send correct request format")
    void shouldSendCorrectRequestFormat() throws Exception {
        // Given: Mock any successful response
        stubFor(post(urlPathEqualTo("/v4/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "id": 1,
                                "result": {}
                            }
                            """)));

        // When: Make API call
        Map<String, Object> params = Map.of(
                "domain", "test.com",
                "limit", 50
        );
        client.callMethod("domain_info", params);

        // Then: Verify request format (without jsonrpc field)
        verify(postRequestedFor(urlPathEqualTo("/v4/"))
                .withRequestBody(matchingJsonPath("$.id", equalTo("1")))
                .withRequestBody(matchingJsonPath("$.method", equalTo("domain_info")))
                .withRequestBody(matchingJsonPath("$.params.domain", equalTo("test.com")))
                .withRequestBody(matchingJsonPath("$.params.limit", equalTo("50"))));
    }

    @Test
    @Order(6)
    @DisplayName("Should preserve request context in response")
    void shouldPreserveRequestContext() throws Exception {
        // Given: Mock response
        stubFor(post(urlPathEqualTo("/v4/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                            {
                                "id": 1,
                                "result": {
                                    "success": true
                                }
                            }
                            """)));

        // When: Make API call with specific parameters
        Map<String, Object> requestParams = Map.of(
                "domain", "test-domain.com",
                "se", "g_uk",
                "limit", 25
        );
        SerpstatApiResponse response = client.callMethod("keyword_research", requestParams);

        // Then: Verify request context is preserved
        assertThat(response.getMethod()).isEqualTo("keyword_research");
        assertThat(response.getRequestParams()).isEqualTo(requestParams);
        assertThat(response.getRequestParams().get("domain")).isEqualTo("test-domain.com");
        assertThat(response.getRequestParams().get("se")).isEqualTo("g_uk");
        assertThat(response.getRequestParams().get("limit")).isEqualTo(25);
        assertThat(response.getTimestamp()).isCloseTo(System.currentTimeMillis(), within(1000L));
    }

    /**
     * Testable version of SerpstatApiClient that allows custom API URL for testing
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
