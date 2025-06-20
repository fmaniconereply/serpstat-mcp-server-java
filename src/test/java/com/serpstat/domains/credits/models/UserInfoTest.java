package com.serpstat.domains.credits.models;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for UserInfo model class
 */
@DisplayName("UserInfo Model Tests")
class UserInfoTest {

    private UserInfo userInfo;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        userInfo = new UserInfo("test-user-123");
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("Test UserInfo constructor")
    void testUserInfoConstructor() {
        UserInfo user = new UserInfo("user-456");
        assertEquals("user-456", user.getUserId());

        // Test no-arg constructor
        UserInfo emptyUser = new UserInfo();
        assertNull(emptyUser.getUserId());
    }

    @Test
    @DisplayName("Test UserInfo getters")
    void testUserInfoGetters() {
        assertEquals("test-user-123", userInfo.getUserId());
    }

    @Test
    @DisplayName("Test UserInfo setters")
    void testUserInfoSetters() {
        UserInfo user = new UserInfo();
        user.setUserId("new-user-789");
        assertEquals("new-user-789", user.getUserId());
    }

    @Test
    @DisplayName("Test UserInfo equals and hashCode")
    void testUserInfoEqualsAndHashCode() {
        UserInfo user1 = new UserInfo("user-123");
        UserInfo user2 = new UserInfo("user-123");
        UserInfo user3 = new UserInfo("user-456");

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
        assertNotEquals(user1, null);
        assertNotEquals(user1, "not a UserInfo");

        assertEquals(user1.hashCode(), user2.hashCode());
    }

    @Test
    @DisplayName("Test UserInfo toString")
    void testUserInfoToString() {
        String toString = userInfo.toString();

        assertNotNull(toString);
        assertFalse(toString.trim().isEmpty());
        assertTrue(toString.contains("UserInfo"));
    }

    @Test
    @DisplayName("Test UserInfo serialization")
    void testUserInfoSerialization() throws Exception {
        String json = objectMapper.writeValueAsString(userInfo);
        assertNotNull(json);
        assertTrue(json.contains("user_id"));
        assertTrue(json.contains("test-user-123"));

        UserInfo deserializedUser = objectMapper.readValue(json, UserInfo.class);
        assertEquals(userInfo.getUserId(), deserializedUser.getUserId());
    }

    @Test
    @DisplayName("Test UserInfo field validation")
    void testUserInfoFieldValidation() {
        // Test with null userId - should not throw exceptions
        UserInfo nullUser = new UserInfo(null);
        assertDoesNotThrow(() -> nullUser.getUserId());
        assertNull(nullUser.getUserId());
    }

    @Test
    @DisplayName("Test UserInfo immutability - not applicable")
    void testUserInfoImmutability() {
        // UserInfo uses Lombok @Setter, so it's not immutable by design
        UserInfo user = new UserInfo("original-id");
        user.setUserId("modified-id");
        assertEquals("modified-id", user.getUserId());
        // This is expected behavior - the class is mutable
    }

    @Test
    @DisplayName("Test UserInfo builder pattern - not applicable")
    void testUserInfoBuilderPattern() {
        // UserInfo doesn't use builder pattern, it uses Lombok constructors
        UserInfo user = new UserInfo("builder-test-id");
        assertEquals("builder-test-id", user.getUserId());
    }

    @Test
    @DisplayName("Test UserInfo data integrity")
    void testUserInfoDataIntegrity() {
        // Test that userId field is properly stored and retrieved
        String testId = "integrity-test-id";
        UserInfo user = new UserInfo(testId);
        assertEquals(testId, user.getUserId());

        // Test modification
        String newId = "new-integrity-id";
        user.setUserId(newId);
        assertEquals(newId, user.getUserId());
    }
}
