package com.serpstat.core;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

/**
 * Basic functional tests for SerpstatApiClient - Positive Path scenarios only
 * Tests core functionality without making real HTTP requests
 */
@DisplayName("SerpstatApiClient Basic Tests")
class SerpstatApiClientBasicTest {
    private static WireMockServer wireMockServer;
    private static final int MOCK_PORT = 9999;
    private static final String MOCK_URL = "http://localhost:9999/v4";

    @BeforeAll
    static void startWireMock() {
        wireMockServer = new WireMockServer(WireMockConfiguration.options().port(MOCK_PORT));
        wireMockServer.start();
        WireMock.configureFor("localhost", MOCK_PORT);
    }

    @AfterAll
    static void stopWireMock() {
        if (wireMockServer != null) {
            wireMockServer.stop();
        }
    }

    @Test
    @DisplayName("Should create cache key correctly for null parameters")
    void shouldCreateCacheKeyCorrectlyForNullParameters() {
        // Given
        String wireMockUrl = "http://localhost:9999/v4";
        SerpstatApiClient client = new SerpstatApiClient("test-token", wireMockUrl);
        
        // When & Then
        // This should not throw NPE - the cache key should be created properly
        // It should throw SerpstatApiException due to API call, not NPE
        assertThatThrownBy(() -> client.callMethod("test.method", null))
            .isInstanceOf(SerpstatApiException.class)
            .isNotInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should parse successful response from mock server")
    void shouldParseSuccessfulResponseFromMock() throws Exception {
        // Given
        String responseBody = "{" +
                "\"id\":1," +
                "\"result\":{\"data\":[{\"domain\":\"example.com\",\"keywords\":123}],\"summary_info\":{\"total\":1}}" +
                "}";
        WireMock.stubFor(WireMock.post(WireMock.urlPathMatching("/v4/?"))
                .withQueryParam("token", WireMock.matching(".*"))
                .willReturn(WireMock.aResponse().withStatus(200).withBody(responseBody)));
        SerpstatApiClient client = new SerpstatApiClient("test-token", MOCK_URL);
        // When
        SerpstatApiResponse apiResponse = client.callMethod("SerpstatDomainProcedure.getInfo", Map.of("domain", "example.com"));
        // Then
        assertThat(apiResponse.getResult().path("data").get(0).path("domain").asText()).isEqualTo("example.com");
        assertThat(apiResponse.getResult().path("data").get(0).path("keywords").asInt()).isEqualTo(123);
        assertThat(apiResponse.getResult().path("summary_info").path("total").asInt()).isEqualTo(1);
    }
}
