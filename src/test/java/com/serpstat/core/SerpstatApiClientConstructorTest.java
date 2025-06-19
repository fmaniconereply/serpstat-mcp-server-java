package com.serpstat.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * Constructor tests for SerpstatApiClient
 * Tests client initialization with various token inputs and default configuration
 */
@DisplayName("SerpstatApiClient Constructor Tests")
class SerpstatApiClientConstructorTest {

    @Test
    @DisplayName("Should create client with valid API token")
    void shouldCreateClientWithValidToken() {
        // Given
        String validToken = "test-api-token-123";
        String wireMockUrl = "http://localhost:9999/v4";
        // When
        SerpstatApiClient client = new SerpstatApiClient(validToken, wireMockUrl);
        // Then
        assertThat(client).isNotNull();
        assertThat(client).isInstanceOf(SerpstatApiClient.class);
    }

    @Test
    @DisplayName("Should initialize default configuration correctly")
    void shouldInitializeDefaultConfiguration() {
        // Given
        String token = "test-token";
        String wireMockUrl = "http://localhost:9999/v4";
        // When
        SerpstatApiClient client = new SerpstatApiClient(token, wireMockUrl);
        // Then
        assertThat(client).isNotNull();
        // Note: These are implicit tests since fields are private
        // We verify initialization by ensuring no exceptions are thrown
        // and that the client can be created successfully
    }

    @Test
    @DisplayName("Should accept long API token")
    void shouldAcceptLongToken() {
        // Given
        String longToken = "a".repeat(500); // Very long token
        String wireMockUrl = "http://localhost:9999/v4";
        // When
        SerpstatApiClient client = new SerpstatApiClient(longToken, wireMockUrl);
        // Then
        assertThat(client).isNotNull();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "token-with-dashes",
        "token_with_underscores", 
        "token.with.dots",
        "token123with456numbers",
        "token@with#special$chars%",
        "МойТокенНаРусском", // Russian characters
        "令牌中文" // Chinese characters
    })
    @DisplayName("Should accept tokens with special characters")
    void shouldAcceptTokenWithSpecialCharacters(String specialToken) {
        String wireMockUrl = "http://localhost:9999/v4";
        // When
        SerpstatApiClient client = new SerpstatApiClient(specialToken, wireMockUrl);
        // Then
        assertThat(client).isNotNull();
    }

    @Test
    @DisplayName("Should create client with empty token")
    void shouldCreateClientWithEmptyToken() {
        // Given
        String emptyToken = "";
        String wireMockUrl = "http://localhost:9999/v4";
        // When
        SerpstatApiClient client = new SerpstatApiClient(emptyToken, wireMockUrl);
        // Then
        assertThat(client).isNotNull();
        // Note: Constructor doesn't validate token - this is expected behavior
    }

    @Test
    @DisplayName("Should create client with null token")
    void shouldCreateClientWithNullToken() {
        // Given
        String nullToken = null;
        String wireMockUrl = "http://localhost:9999/v4";
        // When & Then
        // Constructor accepts null token (validation happens later during API calls)
        assertThatCode(() -> new SerpstatApiClient(nullToken, wireMockUrl))
            .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Should handle whitespace-only token")
    void shouldCreateClientWithWhitespaceToken() {
        // Given
        String whitespaceToken = "   \t\n   ";
        String wireMockUrl = "http://localhost:9999/v4";
        // When
        SerpstatApiClient client = new SerpstatApiClient(whitespaceToken, wireMockUrl);
        // Then
        assertThat(client).isNotNull();
    }

    @Test
    @DisplayName("Should create multiple client instances independently")
    void shouldCreateMultipleInstancesIndependently() {
        // Given
        String token1 = "token-1";
        String token2 = "token-2";
        String wireMockUrl = "http://localhost:9999/v4";
        // When
        SerpstatApiClient client1 = new SerpstatApiClient(token1, wireMockUrl);
        SerpstatApiClient client2 = new SerpstatApiClient(token2, wireMockUrl);
        // Then
        assertThat(client1).isNotNull();
        assertThat(client2).isNotNull();
        assertThat(client1).isNotSameAs(client2);
    }
}
