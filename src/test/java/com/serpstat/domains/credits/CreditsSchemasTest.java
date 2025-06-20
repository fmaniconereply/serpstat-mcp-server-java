package com.serpstat.domains.credits;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for CreditsSchemas class
 */
@DisplayName("CreditsSchemas Tests")
class CreditsSchemasTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Test API stats schema is not null")
    void testApiStatsSchemaNotNull() {
        assertNotNull(CreditsSchemas.API_STATS_SCHEMA);
        assertFalse(CreditsSchemas.API_STATS_SCHEMA.isEmpty());
    }

    @Test
    @DisplayName("Test API stats schema is valid JSON")
    void testApiStatsSchemaValidJson() {
        assertDoesNotThrow(() -> {
            JsonNode schemaNode = objectMapper.readTree(CreditsSchemas.API_STATS_SCHEMA);
            assertNotNull(schemaNode);
        });
    }

    @Test
    @DisplayName("Test API stats schema structure")
    void testApiStatsSchemaStructure() throws Exception {
        JsonNode schemaNode = objectMapper.readTree(CreditsSchemas.API_STATS_SCHEMA);
        
        // Verify it's an object schema
        assertTrue(schemaNode.has("type"));
        assertEquals("object", schemaNode.get("type").asText());
        
        // Verify it has properties field
        assertTrue(schemaNode.has("properties"));
        
        // Verify additionalProperties is set to false
        assertTrue(schemaNode.has("additionalProperties"));
        assertFalse(schemaNode.get("additionalProperties").asBoolean());
        
        // Verify description is present
        assertTrue(schemaNode.has("description"));
        assertFalse(schemaNode.get("description").asText().isEmpty());
    }

    @Test
    @DisplayName("Test API stats schema properties")
    void testApiStatsSchemaProperties() throws Exception {
        JsonNode schemaNode = objectMapper.readTree(CreditsSchemas.API_STATS_SCHEMA);
        JsonNode properties = schemaNode.get("properties");
        
        // API stats schema should have empty properties (no parameters required)
        assertNotNull(properties);
        assertTrue(properties.isEmpty() || properties.size() == 0);
    }

    @Test
    @DisplayName("Test API stats schema description")
    void testApiStatsSchemaDescription() throws Exception {
        JsonNode schemaNode = objectMapper.readTree(CreditsSchemas.API_STATS_SCHEMA);
        String description = schemaNode.get("description").asText();
        
        assertNotNull(description);
        assertFalse(description.isEmpty());
        assertTrue(description.toLowerCase().contains("no parameters") || 
                  description.toLowerCase().contains("api stats"));
    }

    @Test
    @DisplayName("Test schema constants accessibility")
    void testSchemaConstantsAccessibility() {
        // Verify the constant is accessible and immutable
        assertNotNull(CreditsSchemas.API_STATS_SCHEMA);
        
        // Test that multiple accesses return the same instance
        String schema1 = CreditsSchemas.API_STATS_SCHEMA;
        String schema2 = CreditsSchemas.API_STATS_SCHEMA;
        assertSame(schema1, schema2);
    }

    @Test
    @DisplayName("Test schema integration with validation")
    void testSchemaValidationIntegration() {
        // Verify schema can be used for validation purposes
        assertNotNull(CreditsSchemas.API_STATS_SCHEMA);
        
        // Schema should be loadable and parseable
        assertDoesNotThrow(() -> objectMapper.readTree(CreditsSchemas.API_STATS_SCHEMA));
    }

    @Test
    @DisplayName("Test schema JSON compliance")
    void testSchemaJsonCompliance() throws Exception {
        JsonNode schemaNode = objectMapper.readTree(CreditsSchemas.API_STATS_SCHEMA);
        
        // Basic JSON Schema compliance checks
        assertTrue(schemaNode.isObject());
        assertTrue(schemaNode.has("type"));
        
        // Verify no syntax errors in JSON
        String reserializedJson = objectMapper.writeValueAsString(schemaNode);
        assertNotNull(reserializedJson);
        assertFalse(reserializedJson.isEmpty());
    }

    @Test
    @DisplayName("Test schema loading performance")
    void testSchemaLoadingPerformance() {
        // Test that schema access is fast
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 100; i++) {
            assertNotNull(CreditsSchemas.API_STATS_SCHEMA);
        }
        
        long endTime = System.currentTimeMillis();
        // Should complete very quickly (less than 100ms)
        assertTrue(endTime - startTime < 100);
    }

    @Test
    @DisplayName("Test schema immutability")
    void testSchemaImmutability() {
        // Verify schema content doesn't change between accesses
        String initialSchema = CreditsSchemas.API_STATS_SCHEMA;
        String secondAccess = CreditsSchemas.API_STATS_SCHEMA;
        
        assertEquals(initialSchema, secondAccess);
        assertEquals(initialSchema.hashCode(), secondAccess.hashCode());
    }
}
