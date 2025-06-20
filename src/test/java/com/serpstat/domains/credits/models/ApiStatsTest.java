package com.serpstat.domains.credits.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ApiStats model class
 */
@DisplayName("ApiStats Model Tests")
class ApiStatsTest {

    private ApiStats apiStats;
    private UserInfo userInfo;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userInfo = new UserInfo("test-user-123");
        apiStats = new ApiStats(1000L, 750L, 250L, userInfo);
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Test ApiStats constructor with all parameters")
    void testApiStatsConstructor() {
        ApiStats stats = new ApiStats(5000L, 3000L, 2000L, userInfo);
        
        assertEquals(5000L, stats.getMaxLines());
        assertEquals(3000L, stats.getUsedLines());
        assertEquals(2000L, stats.getLeftLines());
        assertEquals(userInfo, stats.getUserInfo());
    }

    @Test
    @DisplayName("Test ApiStats getters")
    void testApiStatsGetters() {
        assertEquals(1000L, apiStats.getMaxLines());
        assertEquals(750L, apiStats.getUsedLines());
        assertEquals(250L, apiStats.getLeftLines());
        assertEquals(userInfo, apiStats.getUserInfo());
    }

    @Test
    @DisplayName("Test ApiStats setters")
    void testApiStatsSetters() {
        ApiStats stats = new ApiStats();
        UserInfo newUserInfo = new UserInfo("new-user-456");
        
        stats.setMaxLines(2000L);
        stats.setUsedLines(1500L);
        stats.setLeftLines(500L);
        stats.setUserInfo(newUserInfo);
        
        assertEquals(2000L, stats.getMaxLines());
        assertEquals(1500L, stats.getUsedLines());
        assertEquals(500L, stats.getLeftLines());
        assertEquals(newUserInfo, stats.getUserInfo());
    }

    @Test
    @DisplayName("Test ApiStats equals and hashCode")
    void testApiStatsEqualsAndHashCode() {
        ApiStats stats1 = new ApiStats(1000L, 750L, 250L, userInfo);
        ApiStats stats2 = new ApiStats(1000L, 750L, 250L, userInfo);
        ApiStats stats3 = new ApiStats(2000L, 1500L, 500L, userInfo);

        // Test equality
        assertEquals(stats1, stats2);
        assertNotEquals(stats1, stats3);
        assertNotEquals(stats1, null);
        assertNotEquals(stats1, "not an ApiStats");

        // Test hashCode consistency
        assertEquals(stats1.hashCode(), stats2.hashCode());
    }

    @Test
    @DisplayName("Test ApiStats toString")
    void testApiStatsToString() {
        String toString = apiStats.toString();
        
        assertNotNull(toString);
        assertFalse(toString.trim().isEmpty());
        // Verify it contains the class name
        assertTrue(toString.contains("ApiStats"));
    }

    @Test
    @DisplayName("Test ApiStats serialization")
    void testApiStatsSerialization() throws Exception {
        // Test serialization
        String json = objectMapper.writeValueAsString(apiStats);
        assertNotNull(json);
        assertTrue(json.contains("max_lines"));
        assertTrue(json.contains("used_lines"));
        assertTrue(json.contains("left_lines"));
        assertTrue(json.contains("user_info"));

        // Test deserialization
        ApiStats deserializedStats = objectMapper.readValue(json, ApiStats.class);
        assertEquals(apiStats.getMaxLines(), deserializedStats.getMaxLines());
        assertEquals(apiStats.getUsedLines(), deserializedStats.getUsedLines());
        assertEquals(apiStats.getLeftLines(), deserializedStats.getLeftLines());
    }

    @Test
    @DisplayName("Test ApiStats field validation")
    void testApiStatsFieldValidation() {
        // Test with null values - should not throw exceptions
        ApiStats nullStats = new ApiStats(null, null, null, null);
        assertDoesNotThrow(() -> nullStats.getUsagePercentage());
        assertDoesNotThrow(() -> nullStats.isCriticalUsage());
        assertDoesNotThrow(() -> nullStats.isHighUsage());
    }

    @Test
    @DisplayName("Test ApiStats immutability - not applicable")
    void testApiStatsImmutability() {
        // ApiStats uses Lombok @Setter, so it's not immutable by design
        // This test verifies that setters work as expected
        ApiStats stats = new ApiStats(1000L, 500L, 500L, userInfo);
        stats.setUsedLines(800L);
        assertEquals(800L, stats.getUsedLines());
        // This is expected behavior - the class is mutable
    }

    @Test
    @DisplayName("Test ApiStats builder pattern - not applicable")
    void testApiStatsBuilderPattern() {
        // ApiStats doesn't use builder pattern, it uses Lombok constructors
        // This test verifies that all-args constructor works as builder replacement
        ApiStats stats = new ApiStats(2000L, 1000L, 1000L, userInfo);
        assertEquals(2000L, stats.getMaxLines());
        assertEquals(1000L, stats.getUsedLines());
        assertEquals(1000L, stats.getLeftLines());
        assertEquals(userInfo, stats.getUserInfo());
    }

    @Test
    @DisplayName("Test ApiStats data integrity")
    void testApiStatsDataIntegrity() {
        // Test business logic consistency
        ApiStats stats = new ApiStats(1000L, 600L, 400L, userInfo);
        
        // Usage percentage should be consistent
        double expectedUsage = (600.0 / 1000.0) * 100.0;
        assertEquals(expectedUsage, stats.getUsagePercentage(), 0.01);
        
        // High usage thresholds should be consistent
        assertFalse(stats.isCriticalUsage()); // 60% is not critical
        assertFalse(stats.isHighUsage()); // 60% is not high usage
        
        // Test edge case: exactly at threshold
        ApiStats exactStats = new ApiStats(1000L, 750L, 250L, userInfo);
        assertFalse(exactStats.isHighUsage()); // exactly 75%, should be false (> 75%)
    }
}
