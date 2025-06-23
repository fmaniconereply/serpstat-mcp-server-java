package com.serpstat.domains.keywords;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KeywordCompetitorsSchemas class
 * Tests schema constants, JSON validity, structure, and specific properties.
 */
@DisplayName("KeywordCompetitorsSchemas Tests")
class KeywordCompetitorsSchemasTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test schema constants are loaded and accessible")
    void testSchemaConstantsAccessibility() {
        // Test that all schema constants are not null and accessible
        assertNotNull(KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA,
                "KEYWORD_COMPETITORS_SCHEMA should not be null");

        // Test that schemas are not empty
        assertFalse(KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA.trim().isEmpty(),
                "KEYWORD_COMPETITORS_SCHEMA should not be empty");

        // Test that multiple accesses return the same instance (static loading)
        assertSame(KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA,
                KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA);
    }

    @Test
    @DisplayName("Test schema JSON validity and structure")
    void testSchemaJsonValidity() throws Exception {
        // Test schema is valid JSON and has proper structure
        testSchemaValidation(KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA, "KEYWORD_COMPETITORS_SCHEMA");
    }

    @Test
    @DisplayName("Test KEYWORD_COMPETITORS_SCHEMA constant")
    void testKeywordCompetitorsSchema() throws Exception {
        // Test that KEYWORD_COMPETITORS_SCHEMA is not null
        assertNotNull(KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA,
                "KEYWORD_COMPETITORS_SCHEMA should not be null");
        assertFalse(KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA.trim().isEmpty(),
                "KEYWORD_COMPETITORS_SCHEMA should not be empty");

        // Test that schema is valid JSON
        JsonNode schema = objectMapper.readTree(KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA);
        assertNotNull(schema, "KEYWORD_COMPETITORS_SCHEMA should be valid JSON");
        assertEquals("object", schema.get("type").asText(), "Schema should be object type");

        JsonNode properties = schema.get("properties");
        assertNotNull(properties, "Schema should have properties");

        // Test required properties: keyword, se
        assertTrue(properties.has("keyword"), "Schema should have 'keyword' property");
        assertTrue(properties.has("se"), "Schema should have 'se' property");

        // Test keyword property
        JsonNode keywordProperty = properties.get("keyword");
        assertEquals("string", keywordProperty.get("type").asText(), "keyword should be string type");
        assertTrue(keywordProperty.has("description"), "keyword should have description");
        assertEquals(1, keywordProperty.get("minLength").asInt(), "keyword should have minLength 1");
        assertEquals(200, keywordProperty.get("maxLength").asInt(), "keyword should have maxLength 200");

        // Test se property
        JsonNode seProperty = properties.get("se");
        assertEquals("string", seProperty.get("type").asText(), "se should be string type");
        assertTrue(seProperty.has("enum"), "se should have enum values");
        assertEquals("g_us", seProperty.get("default").asText(), "se default should be g_us");

        // Test optional properties
        assertTrue(properties.has("size"), "Schema should have 'size' property");
        if (properties.has("filters")) {
            JsonNode filtersProperty = properties.get("filters");
            assertEquals("object", filtersProperty.get("type").asText(), "filters should be object type");
        }

        if (properties.has("sort")) {
            JsonNode sortProperty = properties.get("sort");
            assertEquals("object", sortProperty.get("type").asText(), "sort should be object type");
        }

        // Test size property
        JsonNode sizeProperty = properties.get("size");
        assertEquals("integer", sizeProperty.get("type").asText(), "size should be integer type");
        assertEquals(1, sizeProperty.get("minimum").asInt(), "size should have minimum 1");
        assertEquals(1000, sizeProperty.get("maximum").asInt(), "size should have maximum 1000");
        assertEquals(20, sizeProperty.get("default").asInt(), "size should have default 20");

        // Test required fields
        JsonNode required = schema.get("required");
        assertNotNull(required, "Schema should have required fields");
        assertTrue(required.isArray(), "Required should be array");
        assertEquals(2, required.size(), "Should have 2 required fields");

        List<String> requiredFields = new ArrayList<>();
        required.forEach(node -> requiredFields.add(node.asText()));
        assertTrue(requiredFields.contains("keyword"), "keyword should be required");
        assertTrue(requiredFields.contains("se"), "se should be required");

        // Test additionalProperties
        assertFalse(schema.get("additionalProperties").asBoolean(),
                "Schema should not allow additional properties");
    }

    @Test
    @DisplayName("Test schema property definitions")
    void testSchemaPropertyDefinitions() throws Exception {
        JsonNode schema = objectMapper.readTree(KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA);

        // Test that all properties have proper type definitions
        JsonNode properties = schema.get("properties");
        assertNotNull(properties, "Schema should have properties");

        // Iterate through all properties and validate they have type
        properties.fieldNames().forEachRemaining(fieldName -> {
            JsonNode property = properties.get(fieldName);
            assertTrue(property.has("type"), "Property " + fieldName + " should have type");
            assertTrue(property.has("description"), "Property " + fieldName + " should have description");
        });
    }

    @Test
    @DisplayName("Test schema integration with validation")
    void testSchemaIntegrationWithValidation() {
        // Test that schema can be used for validation purposes
        assertNotNull(KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA);

        // Schema should be loadable and parseable
        assertDoesNotThrow(() -> objectMapper.readTree(KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA));
    }

    @Test
    @DisplayName("Test schema performance")
    void testSchemaPerformance() {
        // Test that schema access is fast
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            assertNotNull(KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA);
        }

        long endTime = System.currentTimeMillis();
        // Should complete very quickly (less than 100ms)
        assertTrue(endTime - startTime < 100, "Schema access should be fast");
    }

    @Test
    @DisplayName("Test schema consistency with other keyword schemas")
    void testSchemaConsistency() throws Exception {
        JsonNode schema = objectMapper.readTree(KeywordCompetitorsSchemas.KEYWORD_COMPETITORS_SCHEMA);
        JsonNode properties = schema.get("properties");

        // Test consistency with other keyword schemas
        if (properties.has("se")) {
            JsonNode seProperty = properties.get("se");
            assertEquals("string", seProperty.get("type").asText(), "se should be consistent string type");
            assertTrue(seProperty.has("enum"), "se should have consistent enum structure");
        }

        if (properties.has("keyword")) {
            JsonNode keywordProperty = properties.get("keyword");
            assertEquals("string", keywordProperty.get("type").asText(), "keyword should be consistent string type");
            assertTrue(keywordProperty.has("minLength"), "keyword should have minLength constraint");
            assertTrue(keywordProperty.has("maxLength"), "keyword should have maxLength constraint");
        }
    }

    // Helper method for schema validation
    private void testSchemaValidation(String schemaContent, String schemaName) throws Exception {
        // Parse as JSON to verify validity
        JsonNode schemaNode = objectMapper.readTree(schemaContent);
        assertNotNull(schemaNode, schemaName + " should be valid JSON");

        // Verify it's an object schema
        assertTrue(schemaNode.has("type"), schemaName + " should have 'type' property");
        assertEquals("object", schemaNode.get("type").asText(), schemaName + " should be object type");

        // Verify it has properties
        assertTrue(schemaNode.has("properties"), schemaName + " should have 'properties'");
        JsonNode properties = schemaNode.get("properties");
        assertTrue(properties.isObject(), schemaName + " properties should be an object");
        assertTrue(properties.size() > 0, schemaName + " should have at least one property");

        // Verify JSON is re-serializable (no corruption)
        String reserialized = objectMapper.writeValueAsString(schemaNode);
        assertNotNull(reserialized, schemaName + " should be re-serializable");
        assertFalse(reserialized.isEmpty(), schemaName + " reserialized content should not be empty");
    }
}
