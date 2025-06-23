package com.serpstat.domains.backlinks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for BacklinksSchemas class
 * Tests schema constants, JSON validity, structure, and specific properties.
 */
@DisplayName("BacklinksSchemas Tests")
class BacklinksSchemasTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test schema constants are loaded and accessible")
    void testSchemaConstantsAccessibility() {
        // Test that all schema constants are not null and accessible
        assertNotNull(BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA, "BACKLINKS_SUMMARY_SCHEMA should not be null");

        // Test that schemas are not empty
        assertFalse(BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA.trim().isEmpty(),
                "BACKLINKS_SUMMARY_SCHEMA should not be empty");

        // Test that multiple accesses return the same instance (static loading)
        assertSame(BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA, BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA);
    }

    @Test
    @DisplayName("Test schema JSON validity and structure")
    void testSchemaJsonValidity() throws Exception {
        // Test schema is valid JSON and has proper structure
        testSchemaValidation(BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA, "BACKLINKS_SUMMARY_SCHEMA");
    }

    @Test
    @DisplayName("Test BACKLINKS_SUMMARY_SCHEMA constant")
    void testBacklinksSummarySchema() throws Exception {
        // Test that BACKLINKS_SUMMARY_SCHEMA is not null
        assertNotNull(BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA, "BACKLINKS_SUMMARY_SCHEMA should not be null");
        assertFalse(BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA.trim().isEmpty(),
                "BACKLINKS_SUMMARY_SCHEMA should not be empty");

        // Test that schema is valid JSON
        JsonNode schema = objectMapper.readTree(BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA);
        assertNotNull(schema, "BACKLINKS_SUMMARY_SCHEMA should be valid JSON");
        assertEquals("object", schema.get("type").asText(), "Schema should be object type");

        // Test that schema contains required properties
        JsonNode properties = schema.get("properties");
        assertNotNull(properties, "Schema should have properties");
        assertTrue(properties.has("query"), "Schema should have 'query' property");

        // Test query property
        JsonNode queryProperty = properties.get("query");
        assertEquals("string", queryProperty.get("type").asText(), "query should be string type");
        assertTrue(queryProperty.has("pattern"), "query should have pattern validation");
        assertEquals("^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$",
                queryProperty.get("pattern").asText(), "query should have correct domain pattern");
        assertEquals(4, queryProperty.get("minLength").asInt(), "query should have minLength 4");
        assertEquals(253, queryProperty.get("maxLength").asInt(), "query should have maxLength 253");
        assertEquals("Domain or subdomain to analyze",
                queryProperty.get("description").asText(), "query should have correct description");

        // Test searchType property (optional)
        if (properties.has("searchType")) {
            JsonNode searchTypeProperty = properties.get("searchType");
            assertEquals("string", searchTypeProperty.get("type").asText(), "searchType should be string type");
            assertTrue(searchTypeProperty.has("enum"), "searchType should have enum values");
            assertEquals("domain", searchTypeProperty.get("default").asText(), "searchType default should be domain");

            JsonNode searchTypeEnum = searchTypeProperty.get("enum");
            assertTrue(searchTypeEnum.isArray(), "searchType enum should be array");

            List<String> searchTypeValues = new ArrayList<>();
            searchTypeEnum.forEach(node -> searchTypeValues.add(node.asText()));
            assertTrue(searchTypeValues.contains("domain"), "searchType enum should contain domain");
            assertTrue(searchTypeValues.contains("domain_with_subdomains"),
                    "searchType enum should contain domain_with_subdomains");
        }

        // Test required fields
        JsonNode required = schema.get("required");
        assertNotNull(required, "Schema should have required fields");
        assertTrue(required.isArray(), "Required should be array");
        assertEquals(1, required.size(), "Should have 1 required field");
        assertEquals("query", required.get(0).asText(), "query should be required");

        // Test additionalProperties
        assertFalse(schema.get("additionalProperties").asBoolean(),
                "Schema should not allow additional properties");
    }

    @Test
    @DisplayName("Test schema property definitions")
    void testSchemaPropertyDefinitions() throws Exception {
        JsonNode schema = objectMapper.readTree(BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA);

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
        assertNotNull(BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA);

        // Schema should be loadable and parseable
        assertDoesNotThrow(() -> objectMapper.readTree(BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA));
    }

    @Test
    @DisplayName("Test schema performance")
    void testSchemaPerformance() {
        // Test that schema access is fast
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            assertNotNull(BacklinksSchemas.BACKLINKS_SUMMARY_SCHEMA);
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
}
