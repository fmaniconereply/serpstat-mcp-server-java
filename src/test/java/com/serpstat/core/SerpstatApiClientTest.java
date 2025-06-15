package com.serpstat.core;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SerpstatApiClient
 */
class SerpstatApiClientTest {

    private SerpstatApiClient apiClient;

    @BeforeEach
    void setUp() {
        // Use fake token for tests
        apiClient = new SerpstatApiClient("test-api-token");
    }

    @Test
    @DisplayName("Should create client with valid token")
    void shouldCreateClientWithToken() {
        // Given & When
        SerpstatApiClient client = new SerpstatApiClient("my-token");
        
        // Then
        assertNotNull(client);
    }

    @Test
    @DisplayName("Should create client with null token")
    void shouldCreateClientWithNullToken() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            SerpstatApiClient client = new SerpstatApiClient(null);
            assertNotNull(client, "Client should be created even with null token");
        });
    }

    @Test
    @DisplayName("Should create client with empty token")
    void shouldCreateClientWithEmptyToken() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            SerpstatApiClient client = new SerpstatApiClient("");
            assertNotNull(client, "Client should be created even with empty token");
        });
    }

    @Test
    @DisplayName("Should create client with whitespace-only token")
    void shouldCreateClientWithWhitespaceToken() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            SerpstatApiClient client = new SerpstatApiClient("   ");
            assertNotNull(client, "Client should be created with whitespace token");
        });
    }

    @Test
    @DisplayName("Should create client with special characters in token")
    void shouldCreateClientWithSpecialCharactersToken() {
        // Act & Assert
        assertDoesNotThrow(() -> {
            SerpstatApiClient client = new SerpstatApiClient("test-token_123!@#");
            assertNotNull(client, "Client should be created with special characters in token");
        });
    }

    @Test
    @DisplayName("Should initialize client in setUp method")
    void shouldInitializeClientInSetUp() {
        // Assert
        assertNotNull(apiClient, "API client should be initialized in setUp");
    }
}
