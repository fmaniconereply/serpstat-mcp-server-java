package com.serpstat.domains.projects;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for ProjectsSchemas class
 * Tests schema constants, JSON validity, structure, and specific properties.
 */
@DisplayName("ProjectsSchemas Tests")
class ProjectsSchemasTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test schema constants are loaded and accessible")
    void testSchemaConstantsAccessibility() {
        // Test that all schema constants are not null and accessible
        assertNotNull(ProjectsSchemas.PROJECTS_LIST_SCHEMA, "PROJECTS_LIST_SCHEMA should not be null");

        // Test that schemas are not empty
        assertFalse(ProjectsSchemas.PROJECTS_LIST_SCHEMA.trim().isEmpty(), "PROJECTS_LIST_SCHEMA should not be empty");

        // Test that multiple accesses return the same instance (static loading)
        assertSame(ProjectsSchemas.PROJECTS_LIST_SCHEMA, ProjectsSchemas.PROJECTS_LIST_SCHEMA);
    }

    @Test
    @DisplayName("Test schema JSON validity and structure")
    void testSchemaJsonValidity() throws Exception {
        // Test schema is valid JSON and has proper structure
        testSchemaValidation(ProjectsSchemas.PROJECTS_LIST_SCHEMA, "PROJECTS_LIST_SCHEMA");
    }

    @Test
    @DisplayName("Test PROJECTS_LIST_SCHEMA constant")
    void testProjectsListSchema() throws Exception {
        // Test that PROJECTS_LIST_SCHEMA is not null
        assertNotNull(ProjectsSchemas.PROJECTS_LIST_SCHEMA, "PROJECTS_LIST_SCHEMA should not be null");
        assertFalse(ProjectsSchemas.PROJECTS_LIST_SCHEMA.trim().isEmpty(), "PROJECTS_LIST_SCHEMA should not be empty");

        // Test that schema is valid JSON
        JsonNode schema = objectMapper.readTree(ProjectsSchemas.PROJECTS_LIST_SCHEMA);
        assertNotNull(schema, "PROJECTS_LIST_SCHEMA should be valid JSON");
        assertEquals("object", schema.get("type").asText(), "Schema should be object type");

        JsonNode properties = schema.get("properties");
        assertNotNull(properties, "Schema should have properties");

        // Test optional properties: page, size
        assertTrue(properties.has("page"), "Schema should have 'page' property");
        assertTrue(properties.has("size"), "Schema should have 'size' property");

        // Test page property
        JsonNode pageProperty = properties.get("page");
        assertEquals("integer", pageProperty.get("type").asText(), "page should be integer type");
        assertEquals(1, pageProperty.get("minimum").asInt(), "page should have minimum 1");
        assertEquals(1, pageProperty.get("default").asInt(), "page should have default 1");
        assertEquals("Page number in projects list",
                pageProperty.get("description").asText(), "page should have correct description");

        // Test size property
        JsonNode sizeProperty = properties.get("size");
        assertEquals("integer", sizeProperty.get("type").asText(), "size should be integer type");
        assertTrue(sizeProperty.has("enum"), "size should have enum values");
        assertEquals(20, sizeProperty.get("default").asInt(), "size should have default 20");
        assertEquals("Number of results per page",
                sizeProperty.get("description").asText(), "size should have correct description");

        // Test size enum values
        JsonNode sizeEnum = sizeProperty.get("enum");
        assertTrue(sizeEnum.isArray(), "size enum should be array");
        assertTrue(sizeEnum.size() > 0, "size enum should have values");

        List<Integer> sizeValues = new ArrayList<>();
        sizeEnum.forEach(node -> sizeValues.add(node.asInt()));
        assertTrue(sizeValues.contains(20), "size enum should contain 20");
        assertTrue(sizeValues.contains(50), "size enum should contain 50");
        assertTrue(sizeValues.contains(100), "size enum should contain 100");

        // Test required fields (should be empty for projects list)
        if (schema.has("required")) {
            JsonNode required = schema.get("required");
            assertTrue(required.isArray(), "Required should be array");
            assertEquals(0, required.size(), "Should have 0 required fields for projects list");
        }

        // Test additionalProperties
        assertFalse(schema.get("additionalProperties").asBoolean(),
                "Schema should not allow additional properties");
    }

    @Test
    @DisplayName("Test schema property definitions")
    void testSchemaPropertyDefinitions() throws Exception {
        JsonNode schema = objectMapper.readTree(ProjectsSchemas.PROJECTS_LIST_SCHEMA);

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
        assertNotNull(ProjectsSchemas.PROJECTS_LIST_SCHEMA);

        // Schema should be loadable and parseable
        assertDoesNotThrow(() -> objectMapper.readTree(ProjectsSchemas.PROJECTS_LIST_SCHEMA));
    }

    @Test
    @DisplayName("Test schema performance")
    void testSchemaPerformance() {
        // Test that schema access is fast
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            assertNotNull(ProjectsSchemas.PROJECTS_LIST_SCHEMA);
        }

        long endTime = System.currentTimeMillis();
        // Should complete very quickly (less than 100ms)
        assertTrue(endTime - startTime < 100, "Schema access should be fast");
    }

    @Test
    @DisplayName("Test pagination consistency")
    void testPaginationConsistency() throws Exception {
        JsonNode schema = objectMapper.readTree(ProjectsSchemas.PROJECTS_LIST_SCHEMA);
        JsonNode properties = schema.get("properties");

        // Test pagination properties consistency
        if (properties.has("page")) {
            JsonNode pageProperty = properties.get("page");
            assertEquals("integer", pageProperty.get("type").asText(), "page should be consistent integer type");
            assertTrue(pageProperty.get("minimum").asInt() >= 1, "page should have minimum >= 1");
        }

        if (properties.has("size")) {
            JsonNode sizeProperty = properties.get("size");
            assertEquals("integer", sizeProperty.get("type").asText(), "size should be consistent integer type");
            assertTrue(sizeProperty.has("enum") || sizeProperty.has("minimum"),
                    "size should have enum or minimum constraint");
        }
    }

    @Test
    @DisplayName("Test schema documentation completeness")
    void testSchemaDocumentationCompleteness() throws Exception {
        JsonNode schema = objectMapper.readTree(ProjectsSchemas.PROJECTS_LIST_SCHEMA);

        // Test schema-level documentation
        if (schema.has("description")) {
            String description = schema.get("description").asText();
            assertFalse(description.isEmpty(), "Schema description should not be empty");
        }

        // Test property-level documentation
        JsonNode properties = schema.get("properties");
        properties.fieldNames().forEachRemaining(fieldName -> {
            JsonNode property = properties.get(fieldName);
            if (property.has("description")) {
                String description = property.get("description").asText();
                assertFalse(description.isEmpty(), "Property " + fieldName + " description should not be empty");
            }
        });
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

        // Verify JSON is re-serializable (no corruption)
        String reserialized = objectMapper.writeValueAsString(schemaNode);
        assertNotNull(reserialized, schemaName + " should be re-serializable");
        assertFalse(reserialized.isEmpty(), schemaName + " reserialized content should not be empty");
    }
}
