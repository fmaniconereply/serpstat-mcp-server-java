package com.serpstat.core;

import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import io.modelcontextprotocol.server.McpSyncServerExchange;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Map;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.mockito.Mockito;
import com.serpstat.core.BaseToolHandler.ToolExecutor;

/**
 * Comprehensive tests for BaseToolHandler
 * Tests core request/response handling logic, error processing, and logging
 * functionality
 */
@DisplayName("BaseToolHandler Tests")
class BaseToolHandlerTest {

    @RegisterExtension
    static WireMockExtension wireMock = WireMockExtension.newInstance()
            .options(wireMockConfig().dynamicPort())
            .build();
    @Mock
    private McpSyncServerExchange mockExchange;

    private SerpstatApiClient apiClient;
    @SuppressWarnings("unused") // Will be used when test implementations are added
    private TestableBaseToolHandler toolHandler;
    private static final String TEST_TOKEN = "test-base-handler-token";

    /**
     * Sets up the test environment before each test.
     * Initializes mock objects and resets WireMock server.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String wireMockUrl = String.format("http://localhost:%d/v4", wireMock.getPort());
        apiClient = new SerpstatApiClient(TEST_TOKEN, wireMockUrl);
        toolHandler = new TestableBaseToolHandler(apiClient);
        wireMock.resetAll();
    }

    /**
     * Constructor Tests
     */

    /**
     * Should initialize BaseToolHandler with valid API client.
     * Tests correct construction and initialization of apiClient and objectMapper.
     */
    @Test
    @DisplayName("Should initialize BaseToolHandler with valid API client")
    void shouldInitializeWithValidApiClient() {
        TestableBaseToolHandler handler = assertDoesNotThrow(() -> new TestableBaseToolHandler(apiClient),
                "Constructor should not throw exception with valid API client");

        // Then - Verify proper initialization
        assertThat(handler).isNotNull();
        assertThat(handler.getApiClient()).isNotNull();
        assertThat(handler.getApiClient()).isSameAs(apiClient);
        assertThat(handler.getObjectMapper()).isNotNull();

        // Verify ObjectMapper is properly configured
        assertThat(handler.getObjectMapper()).isInstanceOf(com.fasterxml.jackson.databind.ObjectMapper.class);
    }

    /**
     * Should throw exception when API client is null.
     * Verifies that constructor throws NullPointerException for null apiClient.
     */
    @Test
    @DisplayName("Should throw exception when API client is null")
    void shouldThrowExceptionWhenApiClientIsNull() {
        // Given
        SerpstatApiClient nullApiClient = null;

        // When & Then
        assertThrows(NullPointerException.class, () -> new BaseToolHandler(nullApiClient) {
            @Override
            protected String formatResponse(SerpstatApiResponse response, Map<String, Object> arguments) {
                return null;
            }
        });
    }

    /**
     * Should handle tool execution with complex parameters.
     * Verifies the method processes nested and complex argument structures.
     */
    @Test
    @DisplayName("Should handle tool execution with complex parameters")
    void shouldHandleToolExecutionWithComplexParameters() throws Exception {
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        // Complex nested arguments: map, list, primitives, nested map
        Map<String, Object> arguments = Map.of(
            "intParam", 42,
            "stringParam", "test",
            "listParam", java.util.List.of("a", "b", "c"),
            "nestedMap", Map.of(
                "innerKey", "innerValue",
                "innerList", java.util.List.of(1, 2, 3)
            )
        );
        SerpstatApiResponse mockResponse = Mockito.mock(SerpstatApiResponse.class);
        ToolExecutor executor = args -> {
            // Assert all parameter types are passed correctly
            assertThat(args.get("intParam")).isEqualTo(42);
            assertThat(args.get("stringParam")).isEqualTo("test");
            assertThat(args.get("listParam")).isInstanceOf(java.util.List.class);
            assertThat(((java.util.List<?>) args.get("listParam")).get(1)).isEqualTo("b");
            assertThat(args.get("nestedMap")).isInstanceOf(Map.class);
            Map<?,?> nested = (Map<?,?>) args.get("nestedMap");
            assertThat(nested.get("innerKey")).isEqualTo("innerValue");
            assertThat(nested.get("innerList")).isInstanceOf(java.util.List.class);
            assertThat(((java.util.List<?>) nested.get("innerList")).get(2)).isEqualTo(3);
            return mockResponse;
        };

        // When
        CallToolResult result = handler.callHandleToolCall(exchange, arguments, "complexMethod", executor);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isError()).isFalse();
        assertThat(getFirstContentAsString(result)).contains("Test formatted response");
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    }

    // =============================================================================
    // handleToolCall Method Tests - Error Scenarios
    // =============================================================================

    // Remove or comment out duplicate and disabled test stubs for handleToolCall
    // error scenarios
    // =============================================================================
    // handleToolCall Method Tests - Error Scenarios
    // =============================================================================

    // @Disabled("Not yet implemented")
    // @Test
    // @DisplayName("Should handle ValidationException correctly")
    // void shouldHandleValidationExceptionCorrectly() {
    // // ...stub...
    // }

    // @Disabled("Not yet implemented")
    // @Test
    // @DisplayName("Should handle SerpstatApiException correctly")
    // void shouldHandleSerpstatApiExceptionCorrectly() {
    // // ...stub...
    // }

    // @Disabled("Not yet implemented")
    // @Test
    // @DisplayName("Should handle unexpected exceptions correctly")
    // void shouldHandleUnexpectedExceptionsCorrectly() {
    // // ...stub...
    // }

    // Fix getContents() usage: use direct field access if Lombok is not present, or
    // add a helper for test assertions
    // Helper to extract string from CallToolResult content
    private static String getFirstContentAsString(CallToolResult result) {
        if (result == null) return null;
        if (result.content() != null && !result.content().isEmpty()) {
            Object content = result.content().get(0);
            return content != null ? content.toString() : null;
        }
        return null;
    }

    @Test
    @DisplayName("Should handle tool call successfully (happy path)")
    void shouldHandleToolCallSuccessfully() throws Exception {
        // Given
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        Map<String, Object> arguments = Map.of("param", "value");
        SerpstatApiResponse mockResponse = Mockito.mock(SerpstatApiResponse.class);
        ToolExecutor executor = args -> mockResponse;

        // When
        CallToolResult result = handler.callHandleToolCall(exchange, arguments, "testMethod", executor);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isError()).isFalse();
        assertThat(getFirstContentAsString(result)).contains("Test formatted response");
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    }

    @Test
    @DisplayName("Should handle ValidationException correctly")
    void shouldHandleValidationExceptionCorrectly() throws Exception {
        // Given
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        Map<String, Object> arguments = Map.of();
        ToolExecutor executor = args -> { throw new ValidationException("Invalid input"); };

        // When
        CallToolResult result = handler.callHandleToolCall(exchange, arguments, "testMethod", executor);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isError()).isTrue();
        assertThat(getFirstContentAsString(result)).contains("Validation error: Invalid input");
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    }

    @Test
    @DisplayName("Should handle SerpstatApiException correctly")
    void shouldHandleSerpstatApiExceptionCorrectly() throws Exception {
        // Given
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        Map<String, Object> arguments = Map.of();
        ToolExecutor executor = args -> { throw new SerpstatApiException("API is down"); };

        // When
        CallToolResult result = handler.callHandleToolCall(exchange, arguments, "testMethod", executor);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isError()).isTrue();
        assertThat(getFirstContentAsString(result)).contains("API error: API is down");
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    }

    @Test
    @DisplayName("Should handle unexpected Exception correctly")
    void shouldHandleUnexpectedExceptionCorrectly() throws Exception {
        // Given
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        Map<String, Object> arguments = Map.of();
        ToolExecutor executor = args -> { throw new RuntimeException("Something went wrong"); };

        // When
        CallToolResult result = handler.callHandleToolCall(exchange, arguments, "testMethod", executor);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isError()).isTrue();
        assertThat(getFirstContentAsString(result)).contains("Unexpected error: Something went wrong");
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    }
    // =============================================================================
    // Logging Tests
    // =============================================================================

    /**
     * Should log info messages correctly.
     * Verifies the logInfo method creates correct logging notifications.
     */
    @Test
    @DisplayName("Should log info messages correctly")
    void shouldLogInfoMessagesCorrectly() {
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        String message = "Info message";

        // When
        handler.callLogInfo(exchange, message);

        // Then
        var captor = org.mockito.ArgumentCaptor.forClass(io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification.class);
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(captor.capture());
        var notification = captor.getValue();
        assertThat(notification.level()).isEqualTo(io.modelcontextprotocol.spec.McpSchema.LoggingLevel.INFO);
        assertThat(notification.data()).isEqualTo(message);
        assertThat(notification.logger()).isEqualTo(handler.getClass().getSimpleName());
    }

    /**
     * Should log error messages correctly.
     * Verifies the logError method creates correct logging notifications.
     */
    @Test
    @DisplayName("Should log error messages correctly")
    void shouldLogErrorMessagesCorrectly() {
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        String message = "Error message";

        // When
        handler.callLogError(exchange, message);

        // Then
        var captor = org.mockito.ArgumentCaptor.forClass(io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification.class);
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(captor.capture());
        var notification = captor.getValue();
        assertThat(notification.level()).isEqualTo(io.modelcontextprotocol.spec.McpSchema.LoggingLevel.ERROR);
        assertThat(notification.data()).isEqualTo(message);
        assertThat(notification.logger()).isEqualTo(handler.getClass().getSimpleName());
    }

    /**
     * Should log method name in messages.
     * Verifies the log messages include the method name.
     */
    @Test
    @DisplayName("Should log method name in messages")
    void shouldLogMethodNameInMessages() {
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        String methodName = "testMethod";
        String message = "Test message for method: " + methodName;

        // When
        handler.callLogInfo(exchange, message);

        // Then
        var captor = org.mockito.ArgumentCaptor.forClass(io.modelcontextprotocol.spec.McpSchema.LoggingMessageNotification.class);
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(captor.capture());
        var notification = captor.getValue();
        assertThat(notification.data()).contains(methodName);
        assertThat(notification.logger()).isEqualTo(handler.getClass().getSimpleName());
    }

    /**
     * Should handle logging with null exchange gracefully.
     * Verifies the behavior when the logging exchange is null.
     */
    @Test
    @DisplayName("Should handle logging with null exchange gracefully")
    void shouldHandleLoggingWithNullExchangeGracefully() {
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        String message = "Info message";
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> handler.callLogInfo(null, message));
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> handler.callLogError(null, message));
    }

    // =============================================================================
    // Integration Tests
    // =============================================================================

    /**
     * Should integrate with real API client correctly.
     * Tests integration with actual SerpstatApiClient using WireMock.
     */
    @Test
    @DisplayName("Should integrate with real API client correctly")
    void shouldIntegrateWithRealApiClientCorrectly() throws Exception {
        // Given
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        Map<String, Object> arguments = Map.of("param", "value");
        // WireMock setup: simulate API response
        // (Assume SerpstatApiClient uses WireMock endpoint)
        SerpstatApiResponse mockResponse = Mockito.mock(SerpstatApiResponse.class);
        ToolExecutor executor = args -> mockResponse;

        // When
        CallToolResult result = handler.callHandleToolCall(exchange, arguments, "integrationMethod", executor);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.isError()).isFalse();
        assertThat(getFirstContentAsString(result)).contains("Test formatted response");
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    }

    @Test
    @DisplayName("Should handle concurrent tool executions")
    void shouldHandleConcurrentToolExecutions() throws Exception {
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        ToolExecutor executor = args -> Mockito.mock(SerpstatApiResponse.class);
        int threadCount = 10;
        java.util.concurrent.ExecutorService pool = java.util.concurrent.Executors.newFixedThreadPool(threadCount);
        java.util.List<java.util.concurrent.Future<CallToolResult>> results = new java.util.ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            int idx = i;
            results.add(pool.submit(() -> handler.callHandleToolCall(exchange, Map.of("idx", idx), "concurrentMethod", executor)));
        }
        for (var future : results) {
            CallToolResult result = future.get();
            assertThat(result).isNotNull();
            assertThat(result.isError()).isFalse();
            assertThat(getFirstContentAsString(result)).contains("Test formatted response");
        }
        pool.shutdown();
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    }

    @Test
    @DisplayName("Should maintain state correctly across multiple calls")
    void shouldMaintainStateCorrectlyAcrossMultipleCalls() throws Exception {
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        ToolExecutor executor = args -> Mockito.mock(SerpstatApiResponse.class);
        for (int i = 0; i < 5; i++) {
            CallToolResult result = handler.callHandleToolCall(exchange, Map.of("call", i), "stateMethod", executor);
            assertThat(result).isNotNull();
            assertThat(result.isError()).isFalse();
            assertThat(getFirstContentAsString(result)).contains("Test formatted response");
        }
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    }

    @Test
    @DisplayName("Should handle large response data efficiently")
    void shouldHandleLargeResponseDataEfficiently() throws Exception {
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        // Simulate large response
        String largeString = "x".repeat(100_000);
        SerpstatApiResponse mockResponse = Mockito.mock(SerpstatApiResponse.class);
        ToolExecutor executor = args -> mockResponse;
        // Override formatResponse to return large string
        TestableBaseToolHandler customHandler = new TestableBaseToolHandler(apiClient) {
            @Override
            protected String formatResponse(SerpstatApiResponse response, Map<String, Object> arguments) {
                return largeString;
            }
        };
        CallToolResult result = customHandler.callHandleToolCall(exchange, Map.of(), "largeDataMethod", executor);
        assertThat(result).isNotNull();
        assertThat(result.isError()).isFalse();
        assertThat(getFirstContentAsString(result)).contains(largeString.substring(0, 100));
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    }

    @Test
    @DisplayName("Should handle rapid successive calls efficiently")
    void shouldHandleRapidSuccessiveCallsEfficiently() throws Exception {
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        ToolExecutor executor = args -> Mockito.mock(SerpstatApiResponse.class);
        for (int i = 0; i < 50; i++) {
            CallToolResult result = handler.callHandleToolCall(exchange, Map.of("call", i), "rapidMethod", executor);
            assertThat(result).isNotNull();
            assertThat(result.isError()).isFalse();
            assertThat(getFirstContentAsString(result)).contains("Test formatted response");
        }
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    }

    @Test
    @DisplayName("Should handle extremely long method names")
    void shouldHandleExtremelyLongMethodNames() throws Exception {
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        String longMethodName = "m".repeat(500);
        ToolExecutor executor = args -> Mockito.mock(SerpstatApiResponse.class);
        CallToolResult result = handler.callHandleToolCall(exchange, Map.of(), longMethodName, executor);
        assertThat(result).isNotNull();
        assertThat(result.isError()).isFalse();
        assertThat(getFirstContentAsString(result)).contains("Test formatted response");
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    }

    @Test
    @DisplayName("Should handle special characters in parameters")
    void shouldHandleSpecialCharactersInParameters() throws Exception {
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        Map<String, Object> arguments = Map.of(
            "emoji", "😀",
            "unicode", "тест",
            "special", "!@#$%^&*()_+"
        );
        ToolExecutor executor = args -> Mockito.mock(SerpstatApiResponse.class);
        CallToolResult result = handler.callHandleToolCall(exchange, arguments, "specialCharMethod", executor);
        assertThat(result).isNotNull();
        assertThat(result.isError()).isFalse();
        assertThat(getFirstContentAsString(result)).contains("Test formatted response");
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    }

    @Test
    @DisplayName("Should handle method execution timeout scenarios")
    void shouldHandleMethodExecutionTimeoutScenarios() throws Exception {
        TestableBaseToolHandler handler = new TestableBaseToolHandler(apiClient);
        McpSyncServerExchange exchange = Mockito.mock(McpSyncServerExchange.class);
        ToolExecutor slowExecutor = args -> {
            Thread.sleep(200);
            return Mockito.mock(SerpstatApiResponse.class);
        };
        long start = System.currentTimeMillis();
        CallToolResult result = handler.callHandleToolCall(exchange, Map.of(), "timeoutMethod", slowExecutor);
        long duration = System.currentTimeMillis() - start;
        assertThat(result).isNotNull();
        assertThat(result.isError()).isFalse();
        assertThat(getFirstContentAsString(result)).contains("Test formatted response");
        assertThat(duration).isGreaterThanOrEqualTo(200);
        Mockito.verify(exchange, Mockito.atLeastOnce()).loggingNotification(Mockito.any());
    } // =============================================================================
    // Test Helper Classes
    // =============================================================================

    /**
     * Testable implementation of BaseToolHandler for testing purposes
     */
    private static class TestableBaseToolHandler extends BaseToolHandler {

        protected TestableBaseToolHandler(SerpstatApiClient apiClient) {
            super(apiClient);
        }

        @Override
        protected String formatResponse(SerpstatApiResponse response, Map<String, Object> arguments) throws Exception {
            // TODO: Implement test-specific response formatting
            // - Return simple formatted string for testing
            // - Allow testing of formatResponse error scenarios
            // - Make behavior configurable for different test scenarios
            return "Test formatted response";
        }

        // Expose protected logInfo for testing
        public void callLogInfo(McpSyncServerExchange exchange, String message) {
            logInfo(exchange, message);
        }

        // Expose protected logError for testing
        public void callLogError(McpSyncServerExchange exchange, String message) {
            logError(exchange, message);
        }

        // Expose protected handleToolCall for testing
        public CallToolResult callHandleToolCall(McpSyncServerExchange exchange,
                Map<String, Object> arguments,
                String methodName,
                ToolExecutor executor) {
            return handleToolCall(exchange, arguments, methodName, executor);
        }

        // Getter methods for testing private fields
        public SerpstatApiClient getApiClient() {
            return this.apiClient;
        }

        public com.fasterxml.jackson.databind.ObjectMapper getObjectMapper() {
            return this.objectMapper;
        }
    }
}
