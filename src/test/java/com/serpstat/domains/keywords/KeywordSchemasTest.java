package com.serpstat.domains.keywords;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KeywordSchemas class
 * Tests schema constants, JSON validity, structure, and specific properties.
 */
@DisplayName("KeywordSchemas Tests")
class KeywordSchemasTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test schema constants are loaded and accessible")
    void testSchemaConstantsAccessibility() {
        // Test that all schema constants are not null and accessible
        assertNotNull(KeywordSchemas.GET_KEYWORDS_SCHEMA, "GET_KEYWORDS_SCHEMA should not be null");
        assertNotNull(KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA, "GET_RELATED_KEYWORDS_SCHEMA should not be null");

        // Test that schemas are not empty
        assertFalse(KeywordSchemas.GET_KEYWORDS_SCHEMA.trim().isEmpty(), "GET_KEYWORDS_SCHEMA should not be empty");
        assertFalse(KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA.trim().isEmpty(),
                "GET_RELATED_KEYWORDS_SCHEMA should not be empty");

        // Test that multiple accesses return the same instance (static loading)
        assertSame(KeywordSchemas.GET_KEYWORDS_SCHEMA, KeywordSchemas.GET_KEYWORDS_SCHEMA);
        assertSame(KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA, KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA);
    }

    @Test
    @DisplayName("Test schema JSON validity and structure")
    void testSchemaJsonValidity() throws Exception {
        // Test all schemas are valid JSON and have proper structure
        testSchemaValidation(KeywordSchemas.GET_KEYWORDS_SCHEMA, "GET_KEYWORDS_SCHEMA");
        testSchemaValidation(KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA, "GET_RELATED_KEYWORDS_SCHEMA");
    }

    @Test
    @DisplayName("Test GET_KEYWORDS_SCHEMA constant")
    void testGetKeywordsSchema() throws Exception {
        // Test that GET_KEYWORDS_SCHEMA is not null
        assertNotNull(KeywordSchemas.GET_KEYWORDS_SCHEMA, "GET_KEYWORDS_SCHEMA should not be null");
        assertFalse(KeywordSchemas.GET_KEYWORDS_SCHEMA.trim().isEmpty(), "GET_KEYWORDS_SCHEMA should not be empty");

        // Test that schema is valid JSON
        JsonNode schema = objectMapper.readTree(KeywordSchemas.GET_KEYWORDS_SCHEMA);
        assertNotNull(schema, "GET_KEYWORDS_SCHEMA should be valid JSON");
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
        assertEquals(100, keywordProperty.get("maxLength").asInt(), "keyword should have maxLength 100");

        // Test se property
        JsonNode seProperty = properties.get("se");
        assertEquals("string", seProperty.get("type").asText(), "se should be string type");
        assertTrue(seProperty.has("enum"), "se should have enum values");
        assertEquals("g_us", seProperty.get("default").asText(), "se default should be g_us");

        // Test optional properties
        assertTrue(properties.has("page"), "Schema should have 'page' property");
        assertTrue(properties.has("size"), "Schema should have 'size' property");
        assertTrue(properties.has("filters"), "Schema should have 'filters' property");
        assertTrue(properties.has("sort"), "Schema should have 'sort' property");

        // Test pagination
        JsonNode pageProperty = properties.get("page");
        assertEquals("integer", pageProperty.get("type").asText(), "page should be integer type");
        assertEquals(1, pageProperty.get("minimum").asInt(), "page should have minimum 1");
        assertEquals(1, pageProperty.get("default").asInt(), "page should have default 1");

        JsonNode sizeProperty = properties.get("size");
        assertEquals("integer", sizeProperty.get("type").asText(), "size should be integer type");
        assertEquals(1, sizeProperty.get("minimum").asInt(), "size should have minimum 1");
        assertEquals(1000, sizeProperty.get("maximum").asInt(), "size should have maximum 1000");
        assertEquals(100, sizeProperty.get("default").asInt(), "size should have default 100");

        // Test filters object
        JsonNode filtersProperty = properties.get("filters");
        assertEquals("object", filtersProperty.get("type").asText(), "filters should be object type");
        assertTrue(filtersProperty.has("properties"), "filters should have properties");

        // Test sort object
        JsonNode sortProperty = properties.get("sort");
        assertEquals("object", sortProperty.get("type").asText(), "sort should be object type");
        assertTrue(sortProperty.has("properties"), "sort should have properties");

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
    @DisplayName("Test GET_RELATED_KEYWORDS_SCHEMA constant")
    void testGetRelatedKeywordsSchema() throws Exception {
        // Test that GET_RELATED_KEYWORDS_SCHEMA is not null
        assertNotNull(KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA, "GET_RELATED_KEYWORDS_SCHEMA should not be null");
        assertFalse(KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA.trim().isEmpty(),
                "GET_RELATED_KEYWORDS_SCHEMA should not be empty");

        // Test that schema is valid JSON
        JsonNode schema = objectMapper.readTree(KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA);
        assertNotNull(schema, "GET_RELATED_KEYWORDS_SCHEMA should be valid JSON");
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
        assertTrue(properties.has("page"), "Schema should have 'page' property");
        assertTrue(properties.has("size"), "Schema should have 'size' property");
        assertTrue(properties.has("filters"), "Schema should have 'filters' property");
        assertTrue(properties.has("sort"), "Schema should have 'sort' property");
        assertTrue(properties.has("withIntents"), "Schema should have 'withIntents' property");

        // Test withIntents property
        JsonNode withIntentsProperty = properties.get("withIntents");
        assertEquals("boolean", withIntentsProperty.get("type").asText(), "withIntents should be boolean type");
        assertFalse(withIntentsProperty.get("default").asBoolean(), "withIntents should default to false");

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
        // Test GET_KEYWORDS_SCHEMA properties
        JsonNode getKeywordsSchema = objectMapper.readTree(KeywordSchemas.GET_KEYWORDS_SCHEMA);
        testSchemaPropertyDefinitions(getKeywordsSchema, "GET_KEYWORDS_SCHEMA");

        // Test GET_RELATED_KEYWORDS_SCHEMA properties
        JsonNode getRelatedKeywordsSchema = objectMapper.readTree(KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA);
        testSchemaPropertyDefinitions(getRelatedKeywordsSchema, "GET_RELATED_KEYWORDS_SCHEMA");
    }

    @Test
    @DisplayName("Test schema integration with validation")
    void testSchemaIntegrationWithValidation() {
        // Test that schemas can be used for validation purposes
        assertNotNull(KeywordSchemas.GET_KEYWORDS_SCHEMA);
        assertNotNull(KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA);

        // Schemas should be loadable and parseable
        assertDoesNotThrow(() -> objectMapper.readTree(KeywordSchemas.GET_KEYWORDS_SCHEMA));
        assertDoesNotThrow(() -> objectMapper.readTree(KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA));
    }

    @Test
    @DisplayName("Test schema performance")
    void testSchemaPerformance() {
        // Test that schema access is fast
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            assertNotNull(KeywordSchemas.GET_KEYWORDS_SCHEMA);
            assertNotNull(KeywordSchemas.GET_RELATED_KEYWORDS_SCHEMA);
        }

        long endTime = System.currentTimeMillis();
        // Should complete very quickly (less than 100ms)
        assertTrue(endTime - startTime < 100, "Schema access should be fast");
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

    // Helper method for property definition validation
    private void testSchemaPropertyDefinitions(JsonNode schema, String schemaName) {
        JsonNode properties = schema.get("properties");
        assertNotNull(properties, schemaName + " should have properties");

        // Iterate through all properties and validate they have type
        properties.fieldNames().forEachRemaining(fieldName -> {
            JsonNode property = properties.get(fieldName);
            assertTrue(property.has("type"), "Property " + fieldName + " in " + schemaName + " should have type");
            assertTrue(property.has("description"),
                    "Property " + fieldName + " in " + schemaName + " should have description");
        });
    }
}
