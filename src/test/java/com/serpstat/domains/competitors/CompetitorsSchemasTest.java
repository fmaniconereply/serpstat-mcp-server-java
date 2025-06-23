package com.serpstat.domains.competitors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CompetitorsSchemas class
 * Tests schema constants, JSON validity, structure, and specific properties.
 */
@DisplayName("CompetitorsSchemas Tests")
class CompetitorsSchemasTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test schema constants are loaded and accessible")
    void testSchemaConstantsAccessibility() {
        // Test that all schema constants are not null and accessible
        assertNotNull(CompetitorsSchemas.COMPETITORS_SCHEMA, "COMPETITORS_SCHEMA should not be null");

        // Test that schemas are not empty
        assertFalse(CompetitorsSchemas.COMPETITORS_SCHEMA.trim().isEmpty(), "COMPETITORS_SCHEMA should not be empty");

        // Test that multiple accesses return the same instance (static loading)
        assertSame(CompetitorsSchemas.COMPETITORS_SCHEMA, CompetitorsSchemas.COMPETITORS_SCHEMA);
    }

    @Test
    @DisplayName("Test schema JSON validity and structure")
    void testSchemaJsonValidity() throws Exception {
        // Test schema is valid JSON and has proper structure
        testSchemaValidation(CompetitorsSchemas.COMPETITORS_SCHEMA, "COMPETITORS_SCHEMA");
    }

    @Test
    @DisplayName("Test COMPETITORS_SCHEMA constant")
    void testCompetitorsSchema() throws Exception {
        // Test that COMPETITORS_SCHEMA is not null
        assertNotNull(CompetitorsSchemas.COMPETITORS_SCHEMA, "COMPETITORS_SCHEMA should not be null");
        assertFalse(CompetitorsSchemas.COMPETITORS_SCHEMA.trim().isEmpty(), "COMPETITORS_SCHEMA should not be empty");

        // Test that schema is valid JSON
        JsonNode schema = objectMapper.readTree(CompetitorsSchemas.COMPETITORS_SCHEMA);
        assertNotNull(schema, "COMPETITORS_SCHEMA should be valid JSON");
        assertEquals("object", schema.get("type").asText(), "Schema should be object type");

        JsonNode properties = schema.get("properties");
        assertNotNull(properties, "Schema should have properties");

        // Test required properties: domain, se, size
        assertTrue(properties.has("domain"), "Schema should have 'domain' property");
        assertTrue(properties.has("se"), "Schema should have 'se' property");
        assertTrue(properties.has("size"), "Schema should have 'size' property");

        // Test domain property
        JsonNode domainProperty = properties.get("domain");
        assertEquals("string", domainProperty.get("type").asText(), "domain should be string type");
        assertTrue(domainProperty.has("pattern"), "domain should have pattern validation");
        assertEquals("^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$",
                domainProperty.get("pattern").asText(), "domain should have correct pattern");
        assertEquals(4, domainProperty.get("minLength").asInt(), "domain should have minLength 4");
        assertEquals(253, domainProperty.get("maxLength").asInt(), "domain should have maxLength 253");

        // Test se property
        JsonNode seProperty = properties.get("se");
        assertEquals("string", seProperty.get("type").asText(), "se should be string type");
        assertTrue(seProperty.has("enum"), "se should have enum values");
        assertEquals("g_us", seProperty.get("default").asText(), "se default should be g_us");

        // Test se enum values
        JsonNode seEnum = seProperty.get("enum");
        assertTrue(seEnum.isArray(), "se enum should be array");
        assertTrue(seEnum.size() > 0, "se enum should have values");

        List<String> seValues = new ArrayList<>();
        seEnum.forEach(node -> seValues.add(node.asText()));
        assertTrue(seValues.contains("g_us"), "se enum should contain g_us");
        assertTrue(seValues.contains("g_uk"), "se enum should contain g_uk");

        // Test size property
        JsonNode sizeProperty = properties.get("size");
        assertEquals("integer", sizeProperty.get("type").asText(), "size should be integer type");
        assertEquals(1, sizeProperty.get("minimum").asInt(), "size should have minimum 1");
        assertEquals(100, sizeProperty.get("maximum").asInt(), "size should have maximum 100");
        assertEquals(10, sizeProperty.get("default").asInt(), "size should have default 10");

        // Test optional properties
        if (properties.has("filters")) {
            JsonNode filtersProperty = properties.get("filters");
            assertEquals("object", filtersProperty.get("type").asText(), "filters should be object type");
            assertTrue(filtersProperty.has("properties"), "filters should have properties");

            JsonNode filtersProps = filtersProperty.get("properties");
            // Test common filter properties
            if (filtersProps.has("minus_domains")) {
                JsonNode minusDomainsProperty = filtersProps.get("minus_domains");
                assertEquals("array", minusDomainsProperty.get("type").asText(), "minus_domains should be array type");
            }
            if (filtersProps.has("traff")) {
                JsonNode traffProperty = filtersProps.get("traff");
                assertEquals("integer", traffProperty.get("type").asText(), "traff should be integer type");
                assertEquals(0, traffProperty.get("minimum").asInt(), "traff should have minimum 0");
            }
            if (filtersProps.has("visible")) {
                JsonNode visibleProperty = filtersProps.get("visible");
                assertEquals("number", visibleProperty.get("type").asText(), "visible should be number type");
                assertEquals(0, visibleProperty.get("minimum").asInt(), "visible should have minimum 0");
            }
        }

        // Test required fields
        JsonNode required = schema.get("required");
        assertNotNull(required, "Schema should have required fields");
        assertTrue(required.isArray(), "Required should be array");
        assertEquals(3, required.size(), "Should have 3 required fields");

        List<String> requiredFields = new ArrayList<>();
        required.forEach(node -> requiredFields.add(node.asText()));
        assertTrue(requiredFields.contains("domain"), "domain should be required");
        assertTrue(requiredFields.contains("se"), "se should be required");
        assertTrue(requiredFields.contains("size"), "size should be required");

        // Test additionalProperties
        assertFalse(schema.get("additionalProperties").asBoolean(),
                "Schema should not allow additional properties");
    }

    @Test
    @DisplayName("Test schema property definitions")
    void testSchemaPropertyDefinitions() throws Exception {
        JsonNode schema = objectMapper.readTree(CompetitorsSchemas.COMPETITORS_SCHEMA);

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
        assertNotNull(CompetitorsSchemas.COMPETITORS_SCHEMA);

        // Schema should be loadable and parseable
        assertDoesNotThrow(() -> objectMapper.readTree(CompetitorsSchemas.COMPETITORS_SCHEMA));
    }

    @Test
    @DisplayName("Test schema performance")
    void testSchemaPerformance() {
        // Test that schema access is fast
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < 100; i++) {
            assertNotNull(CompetitorsSchemas.COMPETITORS_SCHEMA);
        }

        long endTime = System.currentTimeMillis();
        // Should complete very quickly (less than 100ms)
        assertTrue(endTime - startTime < 100, "Schema access should be fast");
    }

    @Test
    @DisplayName("Test schema consistency with domain schemas")
    void testSchemaConsistency() throws Exception {
        JsonNode schema = objectMapper.readTree(CompetitorsSchemas.COMPETITORS_SCHEMA);
        JsonNode properties = schema.get("properties");

        // Test consistency with domain schemas
        if (properties.has("domain")) {
            JsonNode domainProperty = properties.get("domain");
            assertEquals("string", domainProperty.get("type").asText(), "domain should be consistent string type");
            assertTrue(domainProperty.has("pattern"), "domain should have consistent pattern validation");
        }

        if (properties.has("se")) {
            JsonNode seProperty = properties.get("se");
            assertEquals("string", seProperty.get("type").asText(), "se should be consistent string type");
            assertTrue(seProperty.has("enum"), "se should have consistent enum structure");
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
