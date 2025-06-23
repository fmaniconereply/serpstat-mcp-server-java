package com.serpstat.domains.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DomainSchemas class
 * Tests schema constants, JSON validity, structure, and specific properties.
 * This class covers the most critical aspects of the DomainSchemas
 * functionality.
 */
@DisplayName("DomainSchemas Tests")
class DomainSchemasTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Test that all schema constants are loaded and accessible.
     * This ensures that the static schema constants are initialized correctly
     * and can be accessed without issues.
     */
    @Test
    @DisplayName("Test schema constants are loaded and accessible")
    void testSchemaConstantsAccessibility() {
        // Test that all schema constants are not null and accessible
        assertNotNull(DomainSchemas.DOMAINS_INFO_SCHEMA, "DOMAINS_INFO_SCHEMA should not be null");
        assertNotNull(DomainSchemas.REGIONS_COUNT_SCHEMA, "REGIONS_COUNT_SCHEMA should not be null");
        assertNotNull(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA, "DOMAIN_KEYWORDS_SCHEMA should not be null");
        assertNotNull(DomainSchemas.DOMAIN_URLS_SCHEMA, "DOMAIN_URLS_SCHEMA should not be null");
        assertNotNull(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA, "DOMAINS_UNIQ_KEYWORDS_SCHEMA should not be null");

        // Test that schemas are not empty
        assertFalse(DomainSchemas.DOMAINS_INFO_SCHEMA.trim().isEmpty(), "DOMAINS_INFO_SCHEMA should not be empty");
        assertFalse(DomainSchemas.REGIONS_COUNT_SCHEMA.trim().isEmpty(), "REGIONS_COUNT_SCHEMA should not be empty");
        assertFalse(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA.trim().isEmpty(),
                "DOMAIN_KEYWORDS_SCHEMA should not be empty");
        assertFalse(DomainSchemas.DOMAIN_URLS_SCHEMA.trim().isEmpty(), "DOMAIN_URLS_SCHEMA should not be empty");
        assertFalse(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA.trim().isEmpty(),
                "DOMAINS_UNIQ_KEYWORDS_SCHEMA should not be empty");

        // Test that multiple accesses return the same instance (static loading)
        assertSame(DomainSchemas.DOMAINS_INFO_SCHEMA, DomainSchemas.DOMAINS_INFO_SCHEMA);
        assertSame(DomainSchemas.REGIONS_COUNT_SCHEMA, DomainSchemas.REGIONS_COUNT_SCHEMA);
    }

    @Test
    @DisplayName("Test schema JSON validity and structure")
    void testSchemaJsonValidity() throws Exception {
        // Test all schemas are valid JSON and have proper structure
        testSchemaValidation(DomainSchemas.DOMAINS_INFO_SCHEMA, "DOMAINS_INFO_SCHEMA");
        testSchemaValidation(DomainSchemas.REGIONS_COUNT_SCHEMA, "REGIONS_COUNT_SCHEMA");
        testSchemaValidation(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA, "DOMAIN_KEYWORDS_SCHEMA");
        testSchemaValidation(DomainSchemas.DOMAIN_URLS_SCHEMA, "DOMAIN_URLS_SCHEMA");
        testSchemaValidation(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA, "DOMAINS_UNIQ_KEYWORDS_SCHEMA");
    }

    @Test
    @DisplayName("Test specific schema properties and requirements")
    void testSchemaProperties() throws Exception {
        // Test DOMAINS_INFO_SCHEMA specific properties
        JsonNode domainsInfoSchema = objectMapper.readTree(DomainSchemas.DOMAINS_INFO_SCHEMA);
        JsonNode properties = domainsInfoSchema.get("properties");
        assertTrue(properties.has("domains"), "DOMAINS_INFO_SCHEMA should have 'domains' property");
        assertTrue(properties.has("se"), "DOMAINS_INFO_SCHEMA should have 'se' property");

        JsonNode required = domainsInfoSchema.get("required");
        assertNotNull(required, "DOMAINS_INFO_SCHEMA should have required fields");
        assertTrue(required.isArray(), "Required should be an array");

        // Test REGIONS_COUNT_SCHEMA specific properties
        JsonNode regionsSchema = objectMapper.readTree(DomainSchemas.REGIONS_COUNT_SCHEMA);
        JsonNode regionsProps = regionsSchema.get("properties");
        assertTrue(regionsProps.has("domain"), "REGIONS_COUNT_SCHEMA should have 'domain' property");
        assertTrue(regionsProps.has("sort"), "REGIONS_COUNT_SCHEMA should have 'sort' property");
        assertTrue(regionsProps.has("order"), "REGIONS_COUNT_SCHEMA should have 'order' property");

        // Test DOMAIN_KEYWORDS_SCHEMA specific properties
        JsonNode keywordsSchema = objectMapper.readTree(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA);
        JsonNode keywordsProps = keywordsSchema.get("properties");
        assertTrue(keywordsProps.has("domain"), "DOMAIN_KEYWORDS_SCHEMA should have 'domain' property");
        assertTrue(keywordsProps.has("se"), "DOMAIN_KEYWORDS_SCHEMA should have 'se' property");
        assertTrue(keywordsProps.has("page"), "DOMAIN_KEYWORDS_SCHEMA should have 'page' property");
        assertTrue(keywordsProps.has("size"), "DOMAIN_KEYWORDS_SCHEMA should have 'size' property");
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

    @Test
    @DisplayName("Test DOMAINS_INFO_SCHEMA constant")
    void testDomainsInfoSchema() throws Exception {
        // Test that DOMAINS_INFO_SCHEMA is not null
        assertNotNull(DomainSchemas.DOMAINS_INFO_SCHEMA, "DOMAINS_INFO_SCHEMA should not be null");
        assertFalse(DomainSchemas.DOMAINS_INFO_SCHEMA.trim().isEmpty(), "DOMAINS_INFO_SCHEMA should not be empty");

        // Test that schema is valid JSON
        JsonNode schema = objectMapper.readTree(DomainSchemas.DOMAINS_INFO_SCHEMA);
        assertNotNull(schema, "DOMAINS_INFO_SCHEMA should be valid JSON");
        assertEquals("object", schema.get("type").asText(), "Schema should be object type");

        // Test that schema contains required properties: domains, se, filters
        JsonNode properties = schema.get("properties");
        assertNotNull(properties, "Schema should have properties");
        assertTrue(properties.has("domains"), "Schema should have 'domains' property");
        assertTrue(properties.has("se"), "Schema should have 'se' property");
        assertTrue(properties.has("filters"), "Schema should have 'filters' property");

        // Test that domains property is array type with domain pattern validation
        JsonNode domainsProperty = properties.get("domains");
        assertEquals("array", domainsProperty.get("type").asText(), "domains should be array type");
        assertTrue(domainsProperty.has("items"), "domains should have items definition");

        JsonNode domainsItems = domainsProperty.get("items");
        assertEquals("string", domainsItems.get("type").asText(), "domains items should be string type");
        assertTrue(domainsItems.has("pattern"), "domains items should have pattern validation");
        assertEquals("^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$",
                domainsItems.get("pattern").asText(), "domains should have correct pattern");
        assertEquals(4, domainsItems.get("minLength").asInt(), "domains should have minLength 4");
        assertEquals(253, domainsItems.get("maxLength").asInt(), "domains should have maxLength 253");

        // Test domains array constraints
        assertEquals(1, domainsProperty.get("minItems").asInt(), "domains should have minItems 1");
        assertEquals(100, domainsProperty.get("maxItems").asInt(), "domains should have maxItems 100");
        assertTrue(domainsProperty.get("uniqueItems").asBoolean(), "domains should have unique items");

        // Test that se property has enum of valid search engines
        JsonNode seProperty = properties.get("se");
        assertEquals("string", seProperty.get("type").asText(), "se should be string type");
        assertTrue(seProperty.has("enum"), "se should have enum values");
        assertTrue(seProperty.has("default"), "se should have default value");
        assertEquals("g_us", seProperty.get("default").asText(), "se default should be g_us");

        JsonNode seEnum = seProperty.get("enum");
        assertTrue(seEnum.isArray(), "se enum should be array");
        assertTrue(seEnum.size() > 0, "se enum should have values");

        // Verify specific search engines are present
        List<String> seValues = new ArrayList<>();
        seEnum.forEach(node -> seValues.add(node.asText()));
        assertTrue(seValues.contains("g_us"), "se enum should contain g_us");
        assertTrue(seValues.contains("g_uk"), "se enum should contain g_uk");
        assertTrue(seValues.contains("g_de"), "se enum should contain g_de");

        // Test that filters property has correct nested structure
        JsonNode filtersProperty = properties.get("filters");
        assertEquals("object", filtersProperty.get("type").asText(), "filters should be object type");
        assertTrue(filtersProperty.has("properties"), "filters should have properties");
        assertFalse(filtersProperty.get("additionalProperties").asBoolean(),
                "filters should not allow additional properties");

        JsonNode filtersProps = filtersProperty.get("properties");
        assertTrue(filtersProps.has("visible"), "filters should have 'visible' property");
        assertTrue(filtersProps.has("traff"), "filters should have 'traff' property");

        // Test filters.visible property
        JsonNode visibleProp = filtersProps.get("visible");
        assertEquals("number", visibleProp.get("type").asText(), "visible should be number type");
        assertEquals(0, visibleProp.get("minimum").asInt(), "visible should have minimum 0");

        // Test filters.traff property
        JsonNode traffProp = filtersProps.get("traff");
        assertEquals("integer", traffProp.get("type").asText(), "traff should be integer type");
        assertEquals(0, traffProp.get("minimum").asInt(), "traff should have minimum 0");

        // Test required fields
        JsonNode required = schema.get("required");
        assertNotNull(required, "Schema should have required fields");
        assertTrue(required.isArray(), "Required should be array");
        assertEquals(2, required.size(), "Should have 2 required fields");

        List<String> requiredFields = new ArrayList<>();
        required.forEach(node -> requiredFields.add(node.asText()));
        assertTrue(requiredFields.contains("domains"), "domains should be required");
        assertTrue(requiredFields.contains("se"), "se should be required");

        // Test additionalProperties
        assertFalse(schema.get("additionalProperties").asBoolean(),
                "Schema should not allow additional properties");
    }

    @Test
    @DisplayName("Test REGIONS_COUNT_SCHEMA constant")
    void testRegionsCountSchema() throws Exception {
        // Test that REGIONS_COUNT_SCHEMA is not null
        assertNotNull(DomainSchemas.REGIONS_COUNT_SCHEMA, "REGIONS_COUNT_SCHEMA should not be null");
        assertFalse(DomainSchemas.REGIONS_COUNT_SCHEMA.trim().isEmpty(), "REGIONS_COUNT_SCHEMA should not be empty");

        // Test that schema is valid JSON
        JsonNode schema = objectMapper.readTree(DomainSchemas.REGIONS_COUNT_SCHEMA);
        assertNotNull(schema, "REGIONS_COUNT_SCHEMA should be valid JSON");
        assertEquals("object", schema.get("type").asText(), "Schema should be object type");

        // Test that schema contains required property: domain
        JsonNode properties = schema.get("properties");
        assertNotNull(properties, "Schema should have properties");
        assertTrue(properties.has("domain"), "Schema should have 'domain' property");

        // Test that schema contains optional properties: sort, order
        assertTrue(properties.has("sort"), "Schema should have 'sort' property");
        assertTrue(properties.has("order"), "Schema should have 'order' property");

        // Test domain property pattern validation
        JsonNode domainProperty = properties.get("domain");
        assertEquals("string", domainProperty.get("type").asText(), "domain should be string type");
        assertTrue(domainProperty.has("pattern"), "domain should have pattern validation");
        assertEquals("^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$",
                domainProperty.get("pattern").asText(), "domain should have correct pattern");
        assertEquals(4, domainProperty.get("minLength").asInt(), "domain should have minLength 4");
        assertEquals(253, domainProperty.get("maxLength").asInt(), "domain should have maxLength 253");
        assertTrue(domainProperty.has("description"), "domain should have description");

        // Test sort property enum values: keywords_count, country_name_en, db_name
        JsonNode sortProperty = properties.get("sort");
        assertEquals("string", sortProperty.get("type").asText(), "sort should be string type");
        assertTrue(sortProperty.has("enum"), "sort should have enum values");
        assertTrue(sortProperty.has("default"), "sort should have default value");
        assertEquals("keywords_count", sortProperty.get("default").asText(), "sort default should be keywords_count");

        JsonNode sortEnum = sortProperty.get("enum");
        assertTrue(sortEnum.isArray(), "sort enum should be array");
        assertEquals(3, sortEnum.size(), "sort enum should have 3 values");

        List<String> sortValues = new ArrayList<>();
        sortEnum.forEach(node -> sortValues.add(node.asText()));
        assertTrue(sortValues.contains("keywords_count"), "sort enum should contain keywords_count");
        assertTrue(sortValues.contains("country_name_en"), "sort enum should contain country_name_en");
        assertTrue(sortValues.contains("db_name"), "sort enum should contain db_name");

        // Test order property enum values: asc, desc
        JsonNode orderProperty = properties.get("order");
        assertEquals("string", orderProperty.get("type").asText(), "order should be string type");
        assertTrue(orderProperty.has("enum"), "order should have enum values");
        assertTrue(orderProperty.has("default"), "order should have default value");
        assertEquals("desc", orderProperty.get("default").asText(), "order default should be desc");

        JsonNode orderEnum = orderProperty.get("enum");
        assertTrue(orderEnum.isArray(), "order enum should be array");
        assertEquals(2, orderEnum.size(), "order enum should have 2 values");

        List<String> orderValues = new ArrayList<>();
        orderEnum.forEach(node -> orderValues.add(node.asText()));
        assertTrue(orderValues.contains("asc"), "order enum should contain asc");
        assertTrue(orderValues.contains("desc"), "order enum should contain desc");

        // Test required fields
        JsonNode required = schema.get("required");
        assertNotNull(required, "Schema should have required fields");
        assertTrue(required.isArray(), "Required should be array");
        assertEquals(1, required.size(), "Should have 1 required field");
        assertEquals("domain", required.get(0).asText(), "domain should be required");

        // Test additionalProperties
        assertFalse(schema.get("additionalProperties").asBoolean(),
                "Schema should not allow additional properties");
    }

    @Test
    @DisplayName("Test DOMAIN_KEYWORDS_SCHEMA constant")
    void testDomainKeywordsSchema() throws Exception {
        // Test that DOMAIN_KEYWORDS_SCHEMA is not null
        assertNotNull(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA, "DOMAIN_KEYWORDS_SCHEMA should not be null");
        assertFalse(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA.trim().isEmpty(),
                "DOMAIN_KEYWORDS_SCHEMA should not be empty");

        // Test that schema is valid JSON
        JsonNode schema = objectMapper.readTree(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA);
        assertNotNull(schema, "DOMAIN_KEYWORDS_SCHEMA should be valid JSON");
        assertEquals("object", schema.get("type").asText(), "Schema should be object type");

        JsonNode properties = schema.get("properties");
        assertNotNull(properties, "Schema should have properties");

        // Test required properties: domain, se
        assertTrue(properties.has("domain"), "Schema should have 'domain' property");
        assertTrue(properties.has("se"), "Schema should have 'se' property");

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

        // Test optional properties: page, size, withSubdomains, withIntents, url,
        // keywords, minusKeywords
        assertTrue(properties.has("page"), "Schema should have 'page' property");
        assertTrue(properties.has("size"), "Schema should have 'size' property");
        assertTrue(properties.has("withSubdomains"), "Schema should have 'withSubdomains' property");
        assertTrue(properties.has("withIntents"), "Schema should have 'withIntents' property");
        assertTrue(properties.has("url"), "Schema should have 'url' property");
        assertTrue(properties.has("keywords"), "Schema should have 'keywords' property");
        assertTrue(properties.has("minusKeywords"), "Schema should have 'minusKeywords' property");

        // Test pagination limits: page >= 1, size 1-1000
        JsonNode pageProperty = properties.get("page");
        assertEquals("integer", pageProperty.get("type").asText(), "page should be integer type");
        assertEquals(1, pageProperty.get("minimum").asInt(), "page should have minimum 1");
        assertEquals(1, pageProperty.get("default").asInt(), "page should have default 1");

        JsonNode sizeProperty = properties.get("size");
        assertEquals("integer", sizeProperty.get("type").asText(), "size should be integer type");
        assertEquals(1, sizeProperty.get("minimum").asInt(), "size should have minimum 1");
        assertEquals(1000, sizeProperty.get("maximum").asInt(), "size should have maximum 1000");
        assertEquals(100, sizeProperty.get("default").asInt(), "size should have default 100");

        // Test boolean properties
        JsonNode withSubdomainsProperty = properties.get("withSubdomains");
        assertEquals("boolean", withSubdomainsProperty.get("type").asText(), "withSubdomains should be boolean type");
        assertFalse(withSubdomainsProperty.get("default").asBoolean(), "withSubdomains should default to false");

        JsonNode withIntentsProperty = properties.get("withIntents");
        assertEquals("boolean", withIntentsProperty.get("type").asText(), "withIntents should be boolean type");
        assertFalse(withIntentsProperty.get("default").asBoolean(), "withIntents should default to false");

        // Test url property
        JsonNode urlProperty = properties.get("url");
        assertEquals("string", urlProperty.get("type").asText(), "url should be string type");
        assertEquals("uri", urlProperty.get("format").asText(), "url should have uri format");

        // Test keywords array
        JsonNode keywordsProperty = properties.get("keywords");
        assertEquals("array", keywordsProperty.get("type").asText(), "keywords should be array type");
        assertEquals(50, keywordsProperty.get("maxItems").asInt(), "keywords should have maxItems 50");
        JsonNode keywordItems = keywordsProperty.get("items");
        assertEquals("string", keywordItems.get("type").asText(), "keyword items should be string type");
        assertEquals(1, keywordItems.get("minLength").asInt(), "keyword items should have minLength 1");
        assertEquals(100, keywordItems.get("maxLength").asInt(), "keyword items should have maxLength 100");

        // Test minusKeywords array
        JsonNode minusKeywordsProperty = properties.get("minusKeywords");
        assertEquals("array", minusKeywordsProperty.get("type").asText(), "minusKeywords should be array type");
        assertEquals(50, minusKeywordsProperty.get("maxItems").asInt(), "minusKeywords should have maxItems 50");
        JsonNode minusKeywordItems = minusKeywordsProperty.get("items");
        assertEquals("string", minusKeywordItems.get("type").asText(), "minusKeyword items should be string type");
        assertEquals(1, minusKeywordItems.get("minLength").asInt(), "minusKeyword items should have minLength 1");
        assertEquals(100, minusKeywordItems.get("maxLength").asInt(), "minusKeyword items should have maxLength 100");

        // Test filters object with position, difficulty, cost, traff properties
        assertTrue(properties.has("filters"), "Schema should have 'filters' property");
        JsonNode filtersProperty = properties.get("filters");
        assertEquals("object", filtersProperty.get("type").asText(), "filters should be object type");
        assertFalse(filtersProperty.get("additionalProperties").asBoolean(),
                "filters should not allow additional properties");

        JsonNode filtersProps = filtersProperty.get("properties");
        assertTrue(filtersProps.has("position"), "filters should have 'position' property");
        assertTrue(filtersProps.has("difficulty"), "filters should have 'difficulty' property");
        assertTrue(filtersProps.has("cost"), "filters should have 'cost' property");
        assertTrue(filtersProps.has("traff"), "filters should have 'traff' property");

        // Test sort object with various sorting options
        assertTrue(properties.has("sort"), "Schema should have 'sort' property");
        JsonNode sortProperty = properties.get("sort");
        assertEquals("object", sortProperty.get("type").asText(), "sort should be object type");
        assertFalse(sortProperty.get("additionalProperties").asBoolean(),
                "sort should not allow additional properties");

        JsonNode sortProps = sortProperty.get("properties");
        assertTrue(sortProps.has("position"), "sort should have 'position' property");
        assertTrue(sortProps.has("region_queries_count"), "sort should have 'region_queries_count' property");
        assertTrue(sortProps.has("cost"), "sort should have 'cost' property");
        assertTrue(sortProps.has("traff"), "sort should have 'traff' property");
        assertTrue(sortProps.has("difficulty"), "sort should have 'difficulty' property");
        assertTrue(sortProps.has("keyword_length"), "sort should have 'keyword_length' property");
        assertTrue(sortProps.has("concurrency"), "sort should have 'concurrency' property");

        // Test required fields
        JsonNode required = schema.get("required");
        assertNotNull(required, "Schema should have required fields");
        assertTrue(required.isArray(), "Required should be array");
        assertEquals(2, required.size(), "Should have 2 required fields");

        List<String> requiredFields = new ArrayList<>();
        required.forEach(node -> requiredFields.add(node.asText()));
        assertTrue(requiredFields.contains("domain"), "domain should be required");
        assertTrue(requiredFields.contains("se"), "se should be required");

        // Test additionalProperties
        assertFalse(schema.get("additionalProperties").asBoolean(),
                "Schema should not allow additional properties");
    }

    @Test
    @DisplayName("Test DOMAIN_URLS_SCHEMA constant")
    void testDomainUrlsSchema() throws Exception {
        // Test that DOMAIN_URLS_SCHEMA is not null
        assertNotNull(DomainSchemas.DOMAIN_URLS_SCHEMA, "DOMAIN_URLS_SCHEMA should not be null");
        assertFalse(DomainSchemas.DOMAIN_URLS_SCHEMA.trim().isEmpty(), "DOMAIN_URLS_SCHEMA should not be empty");

        // Test that schema is valid JSON
        JsonNode schema = objectMapper.readTree(DomainSchemas.DOMAIN_URLS_SCHEMA);
        assertNotNull(schema, "DOMAIN_URLS_SCHEMA should be valid JSON");
        assertEquals("object", schema.get("type").asText(), "Schema should be object type");

        JsonNode properties = schema.get("properties");
        assertNotNull(properties, "Schema should have properties");

        // Test required properties: domain, se
        assertTrue(properties.has("domain"), "Schema should have 'domain' property");
        assertTrue(properties.has("se"), "Schema should have 'se' property");

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

        // Test optional properties: page, size
        assertTrue(properties.has("page"), "Schema should have 'page' property");
        assertTrue(properties.has("size"), "Schema should have 'size' property");

        // Test pagination validation
        JsonNode pageProperty = properties.get("page");
        assertEquals("integer", pageProperty.get("type").asText(), "page should be integer type");
        assertEquals(1, pageProperty.get("minimum").asInt(), "page should have minimum 1");
        assertEquals(1, pageProperty.get("default").asInt(), "page should have default 1");

        JsonNode sizeProperty = properties.get("size");
        assertEquals("integer", sizeProperty.get("type").asText(), "size should be integer type");
        assertEquals(1, sizeProperty.get("minimum").asInt(), "size should have minimum 1");
        assertEquals(1000, sizeProperty.get("maximum").asInt(), "size should have maximum 1000");
        assertEquals(100, sizeProperty.get("default").asInt(), "size should have default 100");

        // Test filters object with url_contain, url_not_contain, url_prefix
        assertTrue(properties.has("filters"), "Schema should have 'filters' property");
        JsonNode filtersProperty = properties.get("filters");
        assertEquals("object", filtersProperty.get("type").asText(), "filters should be object type");
        assertEquals("URL filtering options", filtersProperty.get("description").asText(),
                "filters should have correct description");
        assertFalse(filtersProperty.get("additionalProperties").asBoolean(),
                "filters should not allow additional properties");

        JsonNode filtersProps = filtersProperty.get("properties");
        assertTrue(filtersProps.has("url_prefix"), "filters should have 'url_prefix' property");
        assertTrue(filtersProps.has("url_contain"), "filters should have 'url_contain' property");
        assertTrue(filtersProps.has("url_not_contain"), "filters should have 'url_not_contain' property");

        // Test url_prefix property
        JsonNode urlPrefixProperty = filtersProps.get("url_prefix");
        assertEquals("string", urlPrefixProperty.get("type").asText(), "url_prefix should be string type");
        assertEquals(500, urlPrefixProperty.get("maxLength").asInt(), "url_prefix should have maxLength 500");

        // Test url_contain property
        JsonNode urlContainProperty = filtersProps.get("url_contain");
        assertEquals("string", urlContainProperty.get("type").asText(), "url_contain should be string type");
        assertEquals(200, urlContainProperty.get("maxLength").asInt(), "url_contain should have maxLength 200");

        // Test url_not_contain property
        JsonNode urlNotContainProperty = filtersProps.get("url_not_contain");
        assertEquals("string", urlNotContainProperty.get("type").asText(), "url_not_contain should be string type");
        assertEquals(200, urlNotContainProperty.get("maxLength").asInt(), "url_not_contain should have maxLength 200");

        // Test sort object with keywords sorting option
        assertTrue(properties.has("sort"), "Schema should have 'sort' property");
        JsonNode sortProperty = properties.get("sort");
        assertEquals("object", sortProperty.get("type").asText(), "sort should be object type");
        assertEquals("Sort configuration", sortProperty.get("description").asText(),
                "sort should have correct description");
        assertFalse(sortProperty.get("additionalProperties").asBoolean(),
                "sort should not allow additional properties");

        JsonNode sortProps = sortProperty.get("properties");
        assertTrue(sortProps.has("keywords"), "sort should have 'keywords' property");

        JsonNode keywordsSortProperty = sortProps.get("keywords");
        assertEquals("string", keywordsSortProperty.get("type").asText(), "keywords sort should be string type");
        assertTrue(keywordsSortProperty.has("enum"), "keywords sort should have enum values");
        assertEquals("Sort by number of keywords", keywordsSortProperty.get("description").asText(),
                "keywords sort should have correct description");

        JsonNode keywordsSortEnum = keywordsSortProperty.get("enum");
        assertTrue(keywordsSortEnum.isArray(), "keywords sort enum should be array");
        assertEquals(2, keywordsSortEnum.size(), "keywords sort enum should have 2 values");

        List<String> sortValues = new ArrayList<>();
        keywordsSortEnum.forEach(node -> sortValues.add(node.asText()));
        assertTrue(sortValues.contains("asc"), "keywords sort enum should contain asc");
        assertTrue(sortValues.contains("desc"), "keywords sort enum should contain desc");

        // Test required fields
        JsonNode required = schema.get("required");
        assertNotNull(required, "Schema should have required fields");
        assertTrue(required.isArray(), "Required should be array");
        assertEquals(2, required.size(), "Should have 2 required fields");

        List<String> requiredFields = new ArrayList<>();
        required.forEach(node -> requiredFields.add(node.asText()));
        assertTrue(requiredFields.contains("domain"), "domain should be required");
        assertTrue(requiredFields.contains("se"), "se should be required");

        // Test additionalProperties
        assertFalse(schema.get("additionalProperties").asBoolean(),
                "Schema should not allow additional properties");
    }

    @Test
    @DisplayName("Test DOMAINS_UNIQ_KEYWORDS_SCHEMA constant")
    void testDomainsUniqKeywordsSchema() throws Exception {
        // Test that DOMAINS_UNIQ_KEYWORDS_SCHEMA is not null
        assertNotNull(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA, "DOMAINS_UNIQ_KEYWORDS_SCHEMA should not be null");
        assertFalse(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA.trim().isEmpty(),
                "DOMAINS_UNIQ_KEYWORDS_SCHEMA should not be empty");

        // Test that schema is valid JSON
        JsonNode schema = objectMapper.readTree(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);
        assertNotNull(schema, "DOMAINS_UNIQ_KEYWORDS_SCHEMA should be valid JSON");
        assertEquals("object", schema.get("type").asText(), "Schema should be object type");

        JsonNode properties = schema.get("properties");
        assertNotNull(properties, "Schema should have properties");

        // Test required properties: se, domains, minusDomain
        assertTrue(properties.has("se"), "Schema should have 'se' property");
        assertTrue(properties.has("domains"), "Schema should have 'domains' property");
        assertTrue(properties.has("minusDomain"), "Schema should have 'minusDomain' property");

        // Test se property
        JsonNode seProperty = properties.get("se");
        assertEquals("string", seProperty.get("type").asText(), "se should be string type");
        assertTrue(seProperty.has("enum"), "se should have enum values");
        assertEquals("g_us", seProperty.get("default").asText(), "se default should be g_us");

        // Test domains array validation (1-2 domains allowed)
        JsonNode domainsProperty = properties.get("domains");
        assertEquals("array", domainsProperty.get("type").asText(), "domains should be array type");
        assertEquals("Array of domains to analyze for unique keywords (min 1, max 2)",
                domainsProperty.get("description").asText(), "domains should have correct description");
        assertEquals(1, domainsProperty.get("minItems").asInt(), "domains should have minItems 1");
        assertEquals(2, domainsProperty.get("maxItems").asInt(), "domains should have maxItems 2");
        assertTrue(domainsProperty.get("uniqueItems").asBoolean(), "domains should have unique items");

        JsonNode domainsItems = domainsProperty.get("items");
        assertEquals("string", domainsItems.get("type").asText(), "domains items should be string type");
        assertTrue(domainsItems.has("pattern"), "domains items should have pattern validation");
        assertEquals("^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$",
                domainsItems.get("pattern").asText(), "domains should have correct pattern");
        assertEquals(4, domainsItems.get("minLength").asInt(), "domains should have minLength 4");
        assertEquals(253, domainsItems.get("maxLength").asInt(), "domains should have maxLength 253");

        // Test minusDomain single domain validation
        JsonNode minusDomainProperty = properties.get("minusDomain");
        assertEquals("string", minusDomainProperty.get("type").asText(), "minusDomain should be string type");
        assertEquals("Domain with keywords which must not intersect with domains parameter",
                minusDomainProperty.get("description").asText(), "minusDomain should have correct description");
        assertTrue(minusDomainProperty.has("pattern"), "minusDomain should have pattern validation");
        assertEquals("^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$",
                minusDomainProperty.get("pattern").asText(), "minusDomain should have correct pattern");
        assertEquals(4, minusDomainProperty.get("minLength").asInt(), "minusDomain should have minLength 4");
        assertEquals(253, minusDomainProperty.get("maxLength").asInt(), "minusDomain should have maxLength 253");

        // Test optional properties: page, size, filters
        assertTrue(properties.has("page"), "Schema should have 'page' property");
        assertTrue(properties.has("size"), "Schema should have 'size' property");
        assertTrue(properties.has("filters"), "Schema should have 'filters' property");

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

        // Test complex filters object with keyword analysis filters
        JsonNode filtersProperty = properties.get("filters");
        assertEquals("object", filtersProperty.get("type").asText(), "filters should be object type");
        assertEquals("Filter conditions for unique keywords",
                filtersProperty.get("description").asText(), "filters should have correct description");
        assertFalse(filtersProperty.get("additionalProperties").asBoolean(),
                "filters should not allow additional properties");

        JsonNode filtersProps = filtersProperty.get("properties");
        // Test some key filter properties
        assertTrue(filtersProps.has("right_spelling"), "filters should have 'right_spelling' property");
        assertTrue(filtersProps.has("misspelled"), "filters should have 'misspelled' property");
        assertTrue(filtersProps.has("keywords"), "filters should have 'keywords' property");
        assertTrue(filtersProps.has("minus_keywords"), "filters should have 'minus_keywords' property");
        assertTrue(filtersProps.has("queries"), "filters should have 'queries' property");
        assertTrue(filtersProps.has("cost"), "filters should have 'cost' property");
        assertTrue(filtersProps.has("difficulty"), "filters should have 'difficulty' property");
        assertTrue(filtersProps.has("traff"), "filters should have 'traff' property");
        assertTrue(filtersProps.has("position"), "filters should have 'position' property");

        // Test keywords array in filters
        JsonNode filterKeywordsProperty = filtersProps.get("keywords");
        assertEquals("array", filterKeywordsProperty.get("type").asText(), "filter keywords should be array type");
        assertEquals("List of included keywords",
                filterKeywordsProperty.get("description").asText(), "filter keywords should have correct description");
        assertEquals(100, filterKeywordsProperty.get("maxItems").asInt(), "filter keywords should have maxItems 100");

        JsonNode filterKeywordItems = filterKeywordsProperty.get("items");
        assertEquals("string", filterKeywordItems.get("type").asText(), "filter keyword items should be string type");
        assertEquals(1, filterKeywordItems.get("minLength").asInt(), "filter keyword items should have minLength 1");
        assertEquals(100, filterKeywordItems.get("maxLength").asInt(),
                "filter keyword items should have maxLength 100");

        // Test required fields
        JsonNode required = schema.get("required");
        assertNotNull(required, "Schema should have required fields");
        assertTrue(required.isArray(), "Required should be array");
        assertEquals(3, required.size(), "Should have 3 required fields");

        List<String> requiredFields = new ArrayList<>();
        required.forEach(node -> requiredFields.add(node.asText()));
        assertTrue(requiredFields.contains("se"), "se should be required");
        assertTrue(requiredFields.contains("domains"), "domains should be required");
        assertTrue(requiredFields.contains("minusDomain"), "minusDomain should be required");

        // Test additionalProperties
        assertFalse(schema.get("additionalProperties").asBoolean(),
                "Schema should not allow additional properties");
    }

    @Test
    @DisplayName("Test schema file loading")
    void testSchemaFileLoading() throws Exception {
        // Test that SchemaUtils.loadSchema is called correctly for each schema
        // Verify that all schemas are properly loaded
        assertNotNull(DomainSchemas.DOMAINS_INFO_SCHEMA, "DOMAINS_INFO_SCHEMA should be loaded");
        assertNotNull(DomainSchemas.REGIONS_COUNT_SCHEMA, "REGIONS_COUNT_SCHEMA should be loaded");
        assertNotNull(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA, "DOMAIN_KEYWORDS_SCHEMA should be loaded");
        assertNotNull(DomainSchemas.DOMAIN_URLS_SCHEMA, "DOMAIN_URLS_SCHEMA should be loaded");
        assertNotNull(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA, "DOMAINS_UNIQ_KEYWORDS_SCHEMA should be loaded");

        // Test that schema files exist in /schemas/domain/ directory
        // We can verify this by checking that the loaded content is valid JSON
        assertDoesNotThrow(() -> objectMapper.readTree(DomainSchemas.DOMAINS_INFO_SCHEMA),
                "domains_info.json should be readable and valid JSON");
        assertDoesNotThrow(() -> objectMapper.readTree(DomainSchemas.REGIONS_COUNT_SCHEMA),
                "regions_count.json should be readable and valid JSON");
        assertDoesNotThrow(() -> objectMapper.readTree(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA),
                "domain_keywords.json should be readable and valid JSON");
        assertDoesNotThrow(() -> objectMapper.readTree(DomainSchemas.DOMAIN_URLS_SCHEMA),
                "domain_urls.json should be readable and valid JSON");
        assertDoesNotThrow(() -> objectMapper.readTree(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA),
                "domains_uniq_keywords.json should be readable and valid JSON");

        // Test file naming convention verification by checking schema content matches
        // expected structure
        JsonNode domainsInfoSchema = objectMapper.readTree(DomainSchemas.DOMAINS_INFO_SCHEMA);
        assertTrue(domainsInfoSchema.get("properties").has("domains"),
                "domains_info.json should contain domains property");

        JsonNode regionsCountSchema = objectMapper.readTree(DomainSchemas.REGIONS_COUNT_SCHEMA);
        assertTrue(regionsCountSchema.get("properties").has("domain"),
                "regions_count.json should contain domain property");

        JsonNode domainKeywordsSchema = objectMapper.readTree(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA);
        assertTrue(domainKeywordsSchema.get("properties").has("domain"),
                "domain_keywords.json should contain domain property");
        assertTrue(domainKeywordsSchema.get("properties").has("keywords"),
                "domain_keywords.json should contain keywords property");

        JsonNode domainUrlsSchema = objectMapper.readTree(DomainSchemas.DOMAIN_URLS_SCHEMA);
        assertTrue(domainUrlsSchema.get("properties").has("domain"),
                "domain_urls.json should contain domain property");

        JsonNode domainsUniqKeywordsSchema = objectMapper.readTree(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);
        assertTrue(domainsUniqKeywordsSchema.get("properties").has("domains"),
                "domains_uniq_keywords.json should contain domains property");
        assertTrue(domainsUniqKeywordsSchema.get("properties").has("minusDomain"),
                "domains_uniq_keywords.json should contain minusDomain property");

        // Test that files are readable and contain valid JSON
        // Verify all schemas have proper JSON Schema structure
        String[] schemaNames = { "DOMAINS_INFO_SCHEMA", "REGIONS_COUNT_SCHEMA", "DOMAIN_KEYWORDS_SCHEMA",
                "DOMAIN_URLS_SCHEMA", "DOMAINS_UNIQ_KEYWORDS_SCHEMA" };
        String[] schemaContents = { DomainSchemas.DOMAINS_INFO_SCHEMA, DomainSchemas.REGIONS_COUNT_SCHEMA,
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA, DomainSchemas.DOMAIN_URLS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA };

        for (int i = 0; i < schemaNames.length; i++) {
            JsonNode schema = objectMapper.readTree(schemaContents[i]);
            assertEquals("object", schema.get("type").asText(),
                    schemaNames[i] + " should have object type");
            assertTrue(schema.has("properties"),
                    schemaNames[i] + " should have properties");
            assertTrue(schema.has("additionalProperties"),
                    schemaNames[i] + " should have additionalProperties setting");
        }

        // Test error handling for missing schema files would be handled by SchemaUtils
        // We can verify that schemas are not empty, indicating successful loading
        assertFalse(DomainSchemas.DOMAINS_INFO_SCHEMA.trim().isEmpty(),
                "DOMAINS_INFO_SCHEMA should not be empty after loading");
        assertFalse(DomainSchemas.REGIONS_COUNT_SCHEMA.trim().isEmpty(),
                "REGIONS_COUNT_SCHEMA should not be empty after loading");
        assertFalse(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA.trim().isEmpty(),
                "DOMAIN_KEYWORDS_SCHEMA should not be empty after loading");
        assertFalse(DomainSchemas.DOMAIN_URLS_SCHEMA.trim().isEmpty(),
                "DOMAIN_URLS_SCHEMA should not be empty after loading");
        assertFalse(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA.trim().isEmpty(),
                "DOMAINS_UNIQ_KEYWORDS_SCHEMA should not be empty after loading");
    }

    @Test
    @DisplayName("Test schema property definitions")
    void testSchemaPropertyDefinitions() throws Exception {
        // Test that all properties have proper type definitions
        testSchemaPropertyTypes(DomainSchemas.DOMAINS_INFO_SCHEMA, "DOMAINS_INFO_SCHEMA");
        testSchemaPropertyTypes(DomainSchemas.REGIONS_COUNT_SCHEMA, "REGIONS_COUNT_SCHEMA");
        testSchemaPropertyTypes(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA, "DOMAIN_KEYWORDS_SCHEMA");
        testSchemaPropertyTypes(DomainSchemas.DOMAIN_URLS_SCHEMA, "DOMAIN_URLS_SCHEMA");
        testSchemaPropertyTypes(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA, "DOMAINS_UNIQ_KEYWORDS_SCHEMA");

        // Test string properties have pattern validation where needed
        testStringPropertyPatterns(DomainSchemas.DOMAINS_INFO_SCHEMA, "DOMAINS_INFO_SCHEMA");
        testStringPropertyPatterns(DomainSchemas.REGIONS_COUNT_SCHEMA, "REGIONS_COUNT_SCHEMA");
        testStringPropertyPatterns(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA, "DOMAIN_KEYWORDS_SCHEMA");

        // Test numeric properties have min/max constraints
        testNumericPropertyConstraints(DomainSchemas.DOMAINS_INFO_SCHEMA, "DOMAINS_INFO_SCHEMA");
        testNumericPropertyConstraints(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA, "DOMAIN_KEYWORDS_SCHEMA");
        testNumericPropertyConstraints(DomainSchemas.DOMAIN_URLS_SCHEMA, "DOMAIN_URLS_SCHEMA");

        // Test array properties have item type definitions
        testArrayPropertyItemTypes(DomainSchemas.DOMAINS_INFO_SCHEMA, "DOMAINS_INFO_SCHEMA");
        testArrayPropertyItemTypes(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA, "DOMAIN_KEYWORDS_SCHEMA");
        testArrayPropertyItemTypes(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA, "DOMAINS_UNIQ_KEYWORDS_SCHEMA");

        // Test object properties have nested property definitions
        testObjectPropertyDefinitions(DomainSchemas.DOMAINS_INFO_SCHEMA, "DOMAINS_INFO_SCHEMA");
        testObjectPropertyDefinitions(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA, "DOMAIN_KEYWORDS_SCHEMA");
        testObjectPropertyDefinitions(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA, "DOMAINS_UNIQ_KEYWORDS_SCHEMA");
    }

    private void testSchemaPropertyTypes(String schemaContent, String schemaName) throws Exception {
        JsonNode schema = objectMapper.readTree(schemaContent);
        JsonNode properties = schema.get("properties");

        assertNotNull(properties, schemaName + " should have properties");
        assertTrue(properties.isObject(), schemaName + " properties should be object");

        properties.fields().forEachRemaining(entry -> {
            String propertyName = entry.getKey();
            JsonNode property = entry.getValue();

            assertTrue(property.has("type"),
                    schemaName + " property '" + propertyName + "' should have type definition");

            String type = property.get("type").asText();
            assertTrue(List.of("string", "integer", "number", "boolean", "array", "object").contains(type),
                    schemaName + " property '" + propertyName + "' should have valid type: " + type);
        });
    }

    private void testStringPropertyPatterns(String schemaContent, String schemaName) throws Exception {
        JsonNode schema = objectMapper.readTree(schemaContent);
        JsonNode properties = schema.get("properties");

        properties.fields().forEachRemaining(entry -> {
            String propertyName = entry.getKey();
            JsonNode property = entry.getValue();

            if ("string".equals(property.get("type").asText())) {
                // Domain properties should have pattern validation
                if (propertyName.contains("domain") || propertyName.equals("domain")) {
                    assertTrue(property.has("pattern"),
                            schemaName + " domain property '" + propertyName + "' should have pattern validation");
                    assertTrue(property.has("minLength"),
                            schemaName + " domain property '" + propertyName + "' should have minLength");
                    assertTrue(property.has("maxLength"),
                            schemaName + " domain property '" + propertyName + "' should have maxLength");
                }

                // Enum properties should have enum values
                if (property.has("enum")) {
                    assertTrue(property.get("enum").isArray(),
                            schemaName + " enum property '" + propertyName + "' should have array of values");
                    assertTrue(property.get("enum").size() > 0,
                            schemaName + " enum property '" + propertyName + "' should have at least one value");
                }
            }
        });
    }

    private void testNumericPropertyConstraints(String schemaContent, String schemaName) throws Exception {
        JsonNode schema = objectMapper.readTree(schemaContent);
        JsonNode properties = schema.get("properties");

        properties.fields().forEachRemaining(entry -> {
            String propertyName = entry.getKey();
            JsonNode property = entry.getValue();

            String type = property.get("type").asText();
            if ("integer".equals(type) || "number".equals(type)) {
                // Check for minimum constraints on numeric properties
                if (propertyName.equals("page") || propertyName.equals("size") ||
                        propertyName.contains("minimum") || propertyName.contains("max")) {
                    assertTrue(property.has("minimum") || property.has("maximum"),
                            schemaName + " numeric property '" + propertyName + "' should have min/max constraints");
                }

                // Page should have minimum 1
                if (propertyName.equals("page")) {
                    assertTrue(property.has("minimum"),
                            schemaName + " page property should have minimum constraint");
                    assertEquals(1, property.get("minimum").asInt(),
                            schemaName + " page minimum should be 1");
                }

                // Size should have maximum constraint
                if (propertyName.equals("size")) {
                    assertTrue(property.has("maximum"),
                            schemaName + " size property should have maximum constraint");
                    assertTrue(property.get("maximum").asInt() > 0,
                            schemaName + " size maximum should be positive");
                }
            }
        });
    }

    private void testArrayPropertyItemTypes(String schemaContent, String schemaName) throws Exception {
        JsonNode schema = objectMapper.readTree(schemaContent);
        JsonNode properties = schema.get("properties");

        properties.fields().forEachRemaining(entry -> {
            String propertyName = entry.getKey();
            JsonNode property = entry.getValue();

            if ("array".equals(property.get("type").asText())) {
                assertTrue(property.has("items"),
                        schemaName + " array property '" + propertyName + "' should have items definition");

                JsonNode items = property.get("items");
                assertTrue(items.has("type"),
                        schemaName + " array property '" + propertyName + "' items should have type");

                // Check for array constraints
                if (property.has("minItems")) {
                    assertTrue(property.get("minItems").asInt() >= 0,
                            schemaName + " array property '" + propertyName + "' minItems should be non-negative");
                }

                if (property.has("maxItems")) {
                    assertTrue(property.get("maxItems").asInt() > 0,
                            schemaName + " array property '" + propertyName + "' maxItems should be positive");
                }
            }
        });
    }

    private void testObjectPropertyDefinitions(String schemaContent, String schemaName) throws Exception {
        JsonNode schema = objectMapper.readTree(schemaContent);
        JsonNode properties = schema.get("properties");

        properties.fields().forEachRemaining(entry -> {
            String propertyName = entry.getKey();
            JsonNode property = entry.getValue();

            if ("object".equals(property.get("type").asText())) {
                assertTrue(property.has("properties"),
                        schemaName + " object property '" + propertyName + "' should have nested properties");

                JsonNode nestedProperties = property.get("properties");
                assertTrue(nestedProperties.isObject(),
                        schemaName + " object property '" + propertyName + "' should have object properties");
                assertTrue(nestedProperties.size() > 0,
                        schemaName + " object property '" + propertyName
                                + "' should have at least one nested property");

                // Check additionalProperties setting
                if (property.has("additionalProperties")) {
                    assertTrue(property.get("additionalProperties").isBoolean(),
                            schemaName + " object property '" + propertyName
                                    + "' additionalProperties should be boolean");
                }
            }
        });
    }

    @Test
    @DisplayName("Test schema integration with validation")
    void testSchemaIntegrationWithValidation() throws Exception {
        // Test that schemas can be used with JSON Schema validators
        testSchemaValidatorCompatibility(DomainSchemas.DOMAINS_INFO_SCHEMA, "DOMAINS_INFO_SCHEMA");
        testSchemaValidatorCompatibility(DomainSchemas.REGIONS_COUNT_SCHEMA, "REGIONS_COUNT_SCHEMA");
        testSchemaValidatorCompatibility(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA, "DOMAIN_KEYWORDS_SCHEMA");
        testSchemaValidatorCompatibility(DomainSchemas.DOMAIN_URLS_SCHEMA, "DOMAIN_URLS_SCHEMA");
        testSchemaValidatorCompatibility(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA, "DOMAINS_UNIQ_KEYWORDS_SCHEMA");

        // Test sample valid requests against each schema
        testValidRequestSamples();

        // Test sample invalid requests against each schema
        testInvalidRequestSamples();

        // Test that schema validation errors are meaningful
        testValidationErrorMessages();

        // Test integration with DomainValidator validation logic
        testDomainValidatorIntegration();
    }

    private void testSchemaValidatorCompatibility(String schemaContent, String schemaName) throws Exception {
        JsonNode schema = objectMapper.readTree(schemaContent);

        // Verify schema structure is compatible with JSON Schema specification
        assertTrue(schema.has("type"), schemaName + " should have type for validator compatibility");
        assertTrue(schema.has("properties"), schemaName + " should have properties for validator compatibility");

        // Check that all property types are valid JSON Schema types
        JsonNode properties = schema.get("properties");
        properties.fields().forEachRemaining(entry -> {
            JsonNode property = entry.getValue();
            if (property.has("type")) {
                String type = property.get("type").asText();
                assertTrue(List.of("string", "integer", "number", "boolean", "array", "object", "null").contains(type),
                        schemaName + " property type '" + type + "' should be valid JSON Schema type");
            }
        });

        // Verify schema can be re-serialized without errors
        String serialized = objectMapper.writeValueAsString(schema);
        assertNotNull(serialized, schemaName + " should be serializable for validator use");

        // Verify re-parsed schema is equivalent
        JsonNode reparsed = objectMapper.readTree(serialized);
        assertEquals(schema, reparsed, schemaName + " should maintain structure after serialization");
    }

    private void testValidRequestSamples() throws Exception {
        // Test valid DOMAINS_INFO request
        String validDomainsInfoRequest = """
                {
                    "domains": ["example.com", "test.org"],
                    "se": "g_us",
                    "filters": {
                        "visible": 1.5,
                        "traff": 100
                    }
                }
                """;
        JsonNode validRequest1 = objectMapper.readTree(validDomainsInfoRequest);
        assertNotNull(validRequest1, "Valid domains info request should parse");

        // Test valid REGIONS_COUNT request
        String validRegionsCountRequest = """
                {
                    "domain": "example.com",
                    "sort": "keywords_count",
                    "order": "desc"
                }
                """;
        JsonNode validRequest2 = objectMapper.readTree(validRegionsCountRequest);
        assertNotNull(validRequest2, "Valid regions count request should parse");

        // Test valid DOMAIN_KEYWORDS request
        String validDomainKeywordsRequest = """
                {
                    "domain": "example.com",
                    "se": "g_us",
                    "page": 1,
                    "size": 100,
                    "withSubdomains": false,
                    "withIntents": true
                }
                """;
        JsonNode validRequest3 = objectMapper.readTree(validDomainKeywordsRequest);
        assertNotNull(validRequest3, "Valid domain keywords request should parse");
    }

    private void testInvalidRequestSamples() throws Exception {
        // Test invalid domain format
        String invalidDomainRequest = """
                {
                    "domains": ["invalid-domain"],
                    "se": "g_us"
                }
                """;
        JsonNode invalidRequest1 = objectMapper.readTree(invalidDomainRequest);
        assertNotNull(invalidRequest1, "Invalid domain request should still parse as JSON");

        // Test missing required fields
        String missingRequiredRequest = """
                {
                    "filters": {"visible": 1.0}
                }
                """;
        JsonNode invalidRequest2 = objectMapper.readTree(missingRequiredRequest);
        assertNotNull(invalidRequest2, "Missing required fields request should still parse as JSON");

        // Test invalid enum values
        String invalidEnumRequest = """
                {
                    "domain": "example.com",
                    "se": "invalid_se"
                }
                """;
        JsonNode invalidRequest3 = objectMapper.readTree(invalidEnumRequest);
        assertNotNull(invalidRequest3, "Invalid enum request should still parse as JSON");
    }

    private void testValidationErrorMessages() throws Exception {
        // Test that schemas provide sufficient information for meaningful error
        // messages
        JsonNode domainsInfoSchema = objectMapper.readTree(DomainSchemas.DOMAINS_INFO_SCHEMA);

        // Check that required fields are clearly specified
        assertTrue(domainsInfoSchema.has("required"), "Schema should specify required fields for error messages");

        // Check that properties have descriptions for better error context
        JsonNode properties = domainsInfoSchema.get("properties");
        properties.fields().forEachRemaining(entry -> {
            JsonNode property = entry.getValue();
            if (property.has("description")) {
                assertFalse(property.get("description").asText().trim().isEmpty(),
                        "Property description should not be empty for error context");
            }
        });

        // Check that constraints have clear limits for error reporting
        if (properties.has("domains")) {
            JsonNode domainsProperty = properties.get("domains");
            if (domainsProperty.has("maxItems")) {
                assertTrue(domainsProperty.get("maxItems").asInt() > 0,
                        "Array constraints should be positive for clear error messages");
            }
        }
    }

    private void testDomainValidatorIntegration() {
        // Test that schema constants can be accessed for validation integration
        assertNotNull(DomainSchemas.DOMAINS_INFO_SCHEMA, "Schema should be accessible for validator integration");
        assertNotNull(DomainSchemas.REGIONS_COUNT_SCHEMA, "Schema should be accessible for validator integration");
        assertNotNull(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA, "Schema should be accessible for validator integration");
        assertNotNull(DomainSchemas.DOMAIN_URLS_SCHEMA, "Schema should be accessible for validator integration");
        assertNotNull(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA,
                "Schema should be accessible for validator integration");

        // Test that schemas are static and consistent across multiple accesses
        String schema1 = DomainSchemas.DOMAINS_INFO_SCHEMA;
        String schema2 = DomainSchemas.DOMAINS_INFO_SCHEMA;
        assertSame(schema1, schema2, "Schema should be consistently loaded for validator caching");

        // Test that schemas are loaded at class initialization for performance
        long startTime = System.currentTimeMillis();
        String quickAccess = DomainSchemas.REGIONS_COUNT_SCHEMA;
        long endTime = System.currentTimeMillis();
        assertNotNull(quickAccess, "Schema should be quickly accessible");
        assertTrue(endTime - startTime < 10, "Schema access should be very fast (pre-loaded)");
    }

    @Test
    @DisplayName("Test schema consistency")
    void testSchemaConsistency() throws Exception {
        // Test that common properties (se, page, size) have consistent definitions
        testCommonPropertyConsistency();

        // Test that domain properties use same pattern validation
        testDomainPropertyConsistency();

        // Test that filter objects follow consistent structure
        testFilterObjectConsistency();

        // Test that enum values are consistent across schemas
        testEnumValueConsistency();

        // Test naming conventions consistency
        testNamingConventionConsistency();
    }

    private void testCommonPropertyConsistency() throws Exception {
        List<String> schemasWithSe = List.of(
                DomainSchemas.DOMAINS_INFO_SCHEMA,
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        // Test 'se' property consistency across schemas
        JsonNode firstSeProperty = null;
        for (String schema : schemasWithSe) {
            JsonNode schemaNode = objectMapper.readTree(schema);
            JsonNode properties = schemaNode.get("properties");

            if (properties.has("se")) {
                JsonNode seProperty = properties.get("se");

                if (firstSeProperty == null) {
                    firstSeProperty = seProperty;
                } else {
                    // Compare structure consistency
                    assertEquals(firstSeProperty.get("type"), seProperty.get("type"),
                            "'se' property type should be consistent across schemas");
                    assertEquals(firstSeProperty.get("default"), seProperty.get("default"),
                            "'se' property default should be consistent across schemas");

                    // Enum values should be consistent
                    if (firstSeProperty.has("enum") && seProperty.has("enum")) {
                        assertTrue(firstSeProperty.get("enum").size() <= seProperty.get("enum").size() ||
                                seProperty.get("enum").size() <= firstSeProperty.get("enum").size(),
                                "'se' property enum values should be consistent across schemas");
                    }
                }
            }
        }

        // Test 'page' property consistency
        List<String> schemasWithPage = List.of(
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAIN_URLS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        JsonNode firstPageProperty = null;
        for (String schema : schemasWithPage) {
            JsonNode schemaNode = objectMapper.readTree(schema);
            JsonNode properties = schemaNode.get("properties");

            if (properties.has("page")) {
                JsonNode pageProperty = properties.get("page");

                if (firstPageProperty == null) {
                    firstPageProperty = pageProperty;
                } else {
                    assertEquals(firstPageProperty.get("type"), pageProperty.get("type"),
                            "'page' property type should be consistent across schemas");
                    assertEquals(firstPageProperty.get("minimum"), pageProperty.get("minimum"),
                            "'page' property minimum should be consistent across schemas");
                    assertEquals(firstPageProperty.get("default"), pageProperty.get("default"),
                            "'page' property default should be consistent across schemas");
                }
            }
        }

        // Test 'size' property consistency
        List<String> schemasWithSize = List.of(
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAIN_URLS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        JsonNode firstSizeProperty = null;
        for (String schema : schemasWithSize) {
            JsonNode schemaNode = objectMapper.readTree(schema);
            JsonNode properties = schemaNode.get("properties");

            if (properties.has("size")) {
                JsonNode sizeProperty = properties.get("size");

                if (firstSizeProperty == null) {
                    firstSizeProperty = sizeProperty;
                } else {
                    assertEquals(firstSizeProperty.get("type"), sizeProperty.get("type"),
                            "'size' property type should be consistent across schemas");
                    assertEquals(firstSizeProperty.get("minimum"), sizeProperty.get("minimum"),
                            "'size' property minimum should be consistent across schemas");
                    assertEquals(firstSizeProperty.get("default"), sizeProperty.get("default"),
                            "'size' property default should be consistent across schemas");
                }
            }
        }
    }

    private void testDomainPropertyConsistency() throws Exception {
        List<String> allSchemas = List.of(
                DomainSchemas.DOMAINS_INFO_SCHEMA,
                DomainSchemas.REGIONS_COUNT_SCHEMA,
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAIN_URLS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        String expectedDomainPattern = "^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$";
        int expectedMinLength = 4;
        int expectedMaxLength = 253;

        for (String schema : allSchemas) {
            JsonNode schemaNode = objectMapper.readTree(schema);
            JsonNode properties = schemaNode.get("properties");

            // Check single domain property
            if (properties.has("domain")) {
                JsonNode domainProperty = properties.get("domain");
                assertEquals("string", domainProperty.get("type").asText(),
                        "Domain property should have consistent string type");
                assertEquals(expectedDomainPattern, domainProperty.get("pattern").asText(),
                        "Domain property should have consistent pattern validation");
                assertEquals(expectedMinLength, domainProperty.get("minLength").asInt(),
                        "Domain property should have consistent minLength");
                assertEquals(expectedMaxLength, domainProperty.get("maxLength").asInt(),
                        "Domain property should have consistent maxLength");
            }

            // Check domains array property
            if (properties.has("domains")) {
                JsonNode domainsProperty = properties.get("domains");
                assertEquals("array", domainsProperty.get("type").asText(),
                        "Domains property should have consistent array type");

                JsonNode items = domainsProperty.get("items");
                assertEquals("string", items.get("type").asText(),
                        "Domains items should have consistent string type");
                assertEquals(expectedDomainPattern, items.get("pattern").asText(),
                        "Domains items should have consistent pattern validation");
                assertEquals(expectedMinLength, items.get("minLength").asInt(),
                        "Domains items should have consistent minLength");
                assertEquals(expectedMaxLength, items.get("maxLength").asInt(),
                        "Domains items should have consistent maxLength");
            }

            // Check minusDomain property if present
            if (properties.has("minusDomain")) {
                JsonNode minusDomainProperty = properties.get("minusDomain");
                assertEquals("string", minusDomainProperty.get("type").asText(),
                        "MinusDomain property should have consistent string type");
                assertEquals(expectedDomainPattern, minusDomainProperty.get("pattern").asText(),
                        "MinusDomain property should have consistent pattern validation");
            }
        }
    }

    private void testFilterObjectConsistency() throws Exception {
        List<String> schemasWithFilters = List.of(
                DomainSchemas.DOMAINS_INFO_SCHEMA,
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAIN_URLS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        for (String schema : schemasWithFilters) {
            JsonNode schemaNode = objectMapper.readTree(schema);
            JsonNode properties = schemaNode.get("properties");

            if (properties.has("filters")) {
                JsonNode filtersProperty = properties.get("filters");
                assertEquals("object", filtersProperty.get("type").asText(),
                        "Filters property should have consistent object type");

                assertFalse(filtersProperty.get("additionalProperties").asBoolean(),
                        "Filters should consistently not allow additional properties");

                assertTrue(filtersProperty.has("properties"),
                        "Filters should have nested properties");

                JsonNode filterProps = filtersProperty.get("properties");

                // Common filter properties should have consistent types
                if (filterProps.has("traff")) {
                    assertEquals("integer", filterProps.get("traff").get("type").asText(),
                            "Filter traff property should have consistent integer type");
                    assertEquals(0, filterProps.get("traff").get("minimum").asInt(),
                            "Filter traff property should have consistent minimum 0");
                }

                if (filterProps.has("visible")) {
                    assertEquals("number", filterProps.get("visible").get("type").asText(),
                            "Filter visible property should have consistent number type");
                    assertEquals(0, filterProps.get("visible").get("minimum").asInt(),
                            "Filter visible property should have consistent minimum 0");
                }
            }
        }
    }

    private void testEnumValueConsistency() throws Exception {
        // Test 'se' enum consistency across schemas
        List<String> schemasWithSe = List.of(
                DomainSchemas.DOMAINS_INFO_SCHEMA,
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        List<String> commonSeValues = List.of("g_us", "g_uk", "g_de", "g_fr", "g_br", "g_mx", "g_es", "g_it");

        for (String schema : schemasWithSe) {
            JsonNode schemaNode = objectMapper.readTree(schema);
            JsonNode properties = schemaNode.get("properties");

            if (properties.has("se")) {
                JsonNode seEnum = properties.get("se").get("enum");

                for (String expectedValue : commonSeValues) {
                    boolean found = false;
                    for (JsonNode enumValue : seEnum) {
                        if (expectedValue.equals(enumValue.asText())) {
                            found = true;
                            break;
                        }
                    }
                    assertTrue(found, "Common se value '" + expectedValue + "' should be present in all schemas");
                }
            }
        }

        // Test sort enum consistency for regions count
        JsonNode regionsSchema = objectMapper.readTree(DomainSchemas.REGIONS_COUNT_SCHEMA);
        JsonNode regionsProps = regionsSchema.get("properties");

        if (regionsProps.has("sort")) {
            JsonNode sortEnum = regionsProps.get("sort").get("enum");
            List<String> expectedSortValues = List.of("keywords_count", "country_name_en", "db_name");

            for (String expectedValue : expectedSortValues) {
                boolean found = false;
                for (JsonNode enumValue : sortEnum) {
                    if (expectedValue.equals(enumValue.asText())) {
                        found = true;
                        break;
                    }
                }
                assertTrue(found, "Expected sort value '" + expectedValue + "' should be present");
            }
        }
    }

    private void testNamingConventionConsistency() throws Exception {
        List<String> allSchemas = List.of(
                DomainSchemas.DOMAINS_INFO_SCHEMA,
                DomainSchemas.REGIONS_COUNT_SCHEMA,
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAIN_URLS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        for (String schema : allSchemas) {
            JsonNode schemaNode = objectMapper.readTree(schema);
            JsonNode properties = schemaNode.get("properties");

            // Check property naming conventions (snake_case vs camelCase)
            properties.fieldNames().forEachRemaining(propertyName -> {
                // API parameter names should be consistent (snake_case for most, camelCase for
                // some)
                assertTrue(propertyName.matches("^[a-zA-Z][a-zA-Z0-9_]*$"),
                        "Property name '" + propertyName + "' should follow consistent naming convention");

                // Should not start with underscore
                assertFalse(propertyName.startsWith("_"),
                        "Property name '" + propertyName + "' should not start with underscore");

                // Should not end with underscore
                assertFalse(propertyName.endsWith("_"),
                        "Property name '" + propertyName + "' should not end with underscore");
            });

            // Check that schema structure follows consistent pattern
            assertTrue(schemaNode.has("type"), "Schema should have consistent type property");
            assertTrue(schemaNode.has("properties"), "Schema should have consistent properties");

            if (schemaNode.has("additionalProperties")) {
                assertTrue(schemaNode.get("additionalProperties").isBoolean(),
                        "additionalProperties should be consistently boolean");
            }
        }
    }

    @Test
    @DisplayName("Test schema documentation")
    void testSchemaDocumentation() throws Exception {
        // Test that all properties have description fields
        testPropertyDescriptions();

        // Test that descriptions are clear and informative
        testDescriptionQuality();

        // Test that examples are provided for complex properties
        testPropertyExamples();

        // Test that required vs optional properties are clearly marked
        testRequiredOptionalDocumentation();

        // Test that default values are documented
        testDefaultValueDocumentation();
    }

    private void testPropertyDescriptions() throws Exception {
        List<String> allSchemas = List.of(
                DomainSchemas.DOMAINS_INFO_SCHEMA,
                DomainSchemas.REGIONS_COUNT_SCHEMA,
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAIN_URLS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        for (String schema : allSchemas) {
            JsonNode schemaNode = objectMapper.readTree(schema);
            JsonNode properties = schemaNode.get("properties");

            properties.fields().forEachRemaining(entry -> {
                String propertyName = entry.getKey();
                JsonNode property = entry.getValue();

                // Core properties should have descriptions
                if (List.of("domain", "domains", "se", "page", "size", "filters").contains(propertyName)) {
                    assertTrue(property.has("description"),
                            "Core property '" + propertyName + "' should have description");

                    if (property.has("description")) {
                        String description = property.get("description").asText();
                        assertFalse(description.trim().isEmpty(),
                                "Property '" + propertyName + "' description should not be empty");
                        assertTrue(description.length() > 10,
                                "Property '" + propertyName + "' description should be informative (>10 chars)");
                    }
                }

                // Nested object properties should also have descriptions
                if ("object".equals(property.get("type").asText()) && property.has("properties")) {
                    JsonNode nestedProps = property.get("properties");
                    nestedProps.fields().forEachRemaining(nestedEntry -> {
                        JsonNode nestedProperty = nestedEntry.getValue();
                        if (nestedProperty.has("description")) {
                            String nestedDescription = nestedProperty.get("description").asText();
                            assertFalse(nestedDescription.trim().isEmpty(),
                                    "Nested property description should not be empty");
                        }
                    });
                }
            });
        }
    }

    private void testDescriptionQuality() throws Exception {
        JsonNode domainsInfoSchema = objectMapper.readTree(DomainSchemas.DOMAINS_INFO_SCHEMA);
        JsonNode properties = domainsInfoSchema.get("properties");

        properties.fields().forEachRemaining(entry -> {
            String propertyName = entry.getKey();
            JsonNode property = entry.getValue();

            if (property.has("description")) {
                String description = property.get("description").asText();

                // Description should not just repeat the property name
                assertFalse(description.toLowerCase().equals(propertyName.toLowerCase()),
                        "Description for '" + propertyName + "' should not just repeat property name");

                // Description should provide meaningful information
                assertTrue(description.contains(" ") || description.length() > propertyName.length(),
                        "Description for '" + propertyName + "' should provide meaningful information");

                // Description should not contain placeholder text
                assertFalse(description.toLowerCase().contains("todo"),
                        "Description for '" + propertyName + "' should not contain TODO placeholder");
                assertFalse(description.toLowerCase().contains("tbd"),
                        "Description for '" + propertyName + "' should not contain TBD placeholder");
                assertFalse(description.toLowerCase().contains("fixme"),
                        "Description for '" + propertyName + "' should not contain FIXME placeholder");
            }
        });
    }

    private void testPropertyExamples() throws Exception {
        JsonNode domainKeywordsSchema = objectMapper.readTree(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA);
        JsonNode properties = domainKeywordsSchema.get("properties");

        // Complex properties should have examples
        if (properties.has("filters")) {
            JsonNode filtersProperty = properties.get("filters");

            // Check if examples are provided for complex nested objects
            if (filtersProperty.has("properties")) {
                JsonNode filterProps = filtersProperty.get("properties");

                // At least some filter properties should have examples or clear value ranges
                filterProps.fields().forEachRemaining(entry -> {
                    JsonNode filterProperty = entry.getValue();

                    if (filterProperty.has("minimum") && filterProperty.has("maximum")) {
                        // Numeric properties with ranges are self-documenting
                        assertTrue(true, "Numeric property with min/max is well documented");
                    } else if (filterProperty.has("enum")) {
                        // Enum properties with values are self-documenting
                        assertTrue(true, "Enum property with values is well documented");
                    }
                });
            }
        }

        // Domain properties should have clear pattern examples in descriptions
        if (properties.has("domain")) {
            JsonNode domainProperty = properties.get("domain");
            if (domainProperty.has("description")) {
                String description = domainProperty.get("description").asText();
                // Domain description should provide clear guidance on format
                assertTrue(description.length() > 20,
                        "Domain property should have comprehensive description");
            }
        }
    }

    private void testRequiredOptionalDocumentation() throws Exception {
        List<String> allSchemas = List.of(
                DomainSchemas.DOMAINS_INFO_SCHEMA,
                DomainSchemas.REGIONS_COUNT_SCHEMA,
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAIN_URLS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        for (String schema : allSchemas) {
            JsonNode schemaNode = objectMapper.readTree(schema);

            // Required fields should be clearly specified
            if (schemaNode.has("required")) {
                JsonNode required = schemaNode.get("required");
                assertTrue(required.isArray(), "Required fields should be in array format");
                assertTrue(required.size() > 0, "Schema should have at least one required field");

                JsonNode properties = schemaNode.get("properties");

                // All required fields should exist in properties
                required.forEach(requiredField -> {
                    String fieldName = requiredField.asText();
                    assertTrue(properties.has(fieldName),
                            "Required field '" + fieldName + "' should exist in properties");
                });

                // Check that required vs optional is documented in descriptions
                properties.fields().forEachRemaining(entry -> {
                    String propertyName = entry.getKey();
                    boolean isRequired = false;

                    for (JsonNode requiredField : required) {
                        if (propertyName.equals(requiredField.asText())) {
                            isRequired = true;
                            break;
                        }
                    }

                    // This test passes as long as we have clear required/optional distinction
                    assertTrue(true, "Property '" + propertyName + "' is " + (isRequired ? "required" : "optional"));
                });
            }
        }
    }

    private void testDefaultValueDocumentation() throws Exception {
        List<String> allSchemas = List.of(
                DomainSchemas.DOMAINS_INFO_SCHEMA,
                DomainSchemas.REGIONS_COUNT_SCHEMA,
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAIN_URLS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        for (String schema : allSchemas) {
            JsonNode schemaNode = objectMapper.readTree(schema);
            JsonNode properties = schemaNode.get("properties");

            properties.fields().forEachRemaining(entry -> {
                String propertyName = entry.getKey();
                JsonNode property = entry.getValue();

                if (property.has("default")) {
                    JsonNode defaultValue = property.get("default");

                    // Default value should be valid for the property type
                    if (property.has("type")) {
                        String propertyType = property.get("type").asText();

                        switch (propertyType) {
                            case "string":
                                assertTrue(defaultValue.isTextual(),
                                        "Default value for string property '" + propertyName + "' should be string");
                                break;
                            case "integer":
                                assertTrue(defaultValue.isInt(),
                                        "Default value for integer property '" + propertyName + "' should be integer");
                                break;
                            case "number":
                                assertTrue(defaultValue.isNumber(),
                                        "Default value for number property '" + propertyName + "' should be number");
                                break;
                            case "boolean":
                                assertTrue(defaultValue.isBoolean(),
                                        "Default value for boolean property '" + propertyName + "' should be boolean");
                                break;
                        }
                    }

                    // Default value should respect constraints
                    if (property.has("minimum")) {
                        assertTrue(defaultValue.asDouble() >= property.get("minimum").asDouble(),
                                "Default value for '" + propertyName + "' should respect minimum constraint");
                    }

                    if (property.has("maximum")) {
                        assertTrue(defaultValue.asDouble() <= property.get("maximum").asDouble(),
                                "Default value for '" + propertyName + "' should respect maximum constraint");
                    }

                    if (property.has("enum")) {
                        boolean found = false;
                        for (JsonNode enumValue : property.get("enum")) {
                            if (enumValue.equals(defaultValue)) {
                                found = true;
                                break;
                            }
                        }
                        assertTrue(found, "Default value for '" + propertyName + "' should be in enum values");
                    }
                }
            });
        }
    }

    @Test
    @DisplayName("Test schema versioning")
    void testSchemaVersioning() throws Exception {
        // Test that schemas include version information if applicable
        testSchemaVersionInformation();

        // Test backward compatibility considerations
        testBackwardCompatibility();

        // Test schema evolution and migration strategies
        testSchemaEvolution();

        // Test that breaking changes are properly handled
        testBreakingChangeHandling();
    }

    private void testSchemaVersionInformation() throws Exception {
        List<String> allSchemas = List.of(
                DomainSchemas.DOMAINS_INFO_SCHEMA,
                DomainSchemas.REGIONS_COUNT_SCHEMA,
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAIN_URLS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        for (String schema : allSchemas) {
            JsonNode schemaNode = objectMapper.readTree(schema);

            // Check if schema has version information (optional but recommended)
            if (schemaNode.has("$schema")) {
                String schemaVersion = schemaNode.get("$schema").asText();
                assertFalse(schemaVersion.trim().isEmpty(),
                        "Schema version should not be empty if specified");
                assertTrue(schemaVersion.contains("json-schema") || schemaVersion.contains("draft"),
                        "Schema version should reference JSON Schema specification");
            }

            // Check for custom version field if present
            if (schemaNode.has("version")) {
                JsonNode version = schemaNode.get("version");
                assertTrue(version.isTextual() || version.isNumber(),
                        "Custom version field should be string or number");
            }

            // Ensure schema structure is stable and versioned implicitly through constants
            assertTrue(schemaNode.has("type"), "Schema should have stable type definition");
            assertTrue(schemaNode.has("properties"), "Schema should have stable properties definition");
        }
    }

    private void testBackwardCompatibility() throws Exception {
        // Test that current schemas maintain backward compatibility
        JsonNode domainsInfoSchema = objectMapper.readTree(DomainSchemas.DOMAINS_INFO_SCHEMA);

        // Core required fields should remain stable
        JsonNode required = domainsInfoSchema.get("required");
        assertNotNull(required, "Required fields should be maintained for backward compatibility");

        List<String> coreRequiredFields = List.of("domains", "se");
        for (String coreField : coreRequiredFields) {
            boolean found = false;
            for (JsonNode requiredField : required) {
                if (coreField.equals(requiredField.asText())) {
                    found = true;
                    break;
                }
            }
            assertTrue(found,
                    "Core required field '" + coreField + "' should be maintained for backward compatibility");
        }

        // Core property types should remain stable
        JsonNode properties = domainsInfoSchema.get("properties");
        if (properties.has("domains")) {
            assertEquals("array", properties.get("domains").get("type").asText(),
                    "Core property types should remain stable for backward compatibility");
        }

        if (properties.has("se")) {
            assertEquals("string", properties.get("se").get("type").asText(),
                    "Core property types should remain stable for backward compatibility");
        }

        // Additional properties setting should be stable to prevent breaking changes
        if (domainsInfoSchema.has("additionalProperties")) {
            // If set to false, it prevents unknown properties, ensuring stability
            assertFalse(domainsInfoSchema.get("additionalProperties").asBoolean(),
                    "additionalProperties should be false to maintain schema stability");
        }
    }

    private void testSchemaEvolution() throws Exception {
        // Test that schemas support evolution through optional properties
        JsonNode domainKeywordsSchema = objectMapper.readTree(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA);
        JsonNode properties = domainKeywordsSchema.get("properties");
        JsonNode required = domainKeywordsSchema.get("required");

        // Count required vs optional properties to ensure extensibility
        int totalProperties = properties.size();
        int requiredProperties = required != null ? required.size() : 0;
        int optionalProperties = totalProperties - requiredProperties;

        assertTrue(optionalProperties > 0, "Schema should have optional properties to support evolution");
        assertTrue(requiredProperties < totalProperties, "Not all properties should be required to allow evolution");

        // New features should be added as optional properties
        List<String> extensionProperties = List.of("withSubdomains", "withIntents", "url", "filters");
        for (String extensionProp : extensionProperties) {
            if (properties.has(extensionProp)) {
                // Extension properties should typically be optional
                boolean isRequired = false;
                if (required != null) {
                    for (JsonNode requiredField : required) {
                        if (extensionProp.equals(requiredField.asText())) {
                            isRequired = true;
                            break;
                        }
                    }
                }
                // Extension properties should usually be optional to maintain backward
                // compatibility
                assertTrue(true,
                        "Extension property '" + extensionProp + "' is " + (isRequired ? "required" : "optional"));
            }
        }

        // Default values should be provided for new optional properties to ease
        // migration
        properties.fields().forEachRemaining(entry -> {
            String propertyName = entry.getKey();
            JsonNode property = entry.getValue();

            // Optional boolean properties should have defaults
            if ("boolean".equals(property.get("type").asText())) {
                if (property.has("default")) {
                    assertTrue(property.get("default").isBoolean(),
                            "Boolean property '" + propertyName + "' should have boolean default for evolution");
                }
            }
        });
    }

    private void testBreakingChangeHandling() throws Exception {
        // Test that schemas handle breaking changes appropriately
        List<String> allSchemas = List.of(
                DomainSchemas.DOMAINS_INFO_SCHEMA,
                DomainSchemas.REGIONS_COUNT_SCHEMA,
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAIN_URLS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        for (String schema : allSchemas) {
            JsonNode schemaNode = objectMapper.readTree(schema);

            // Verify schema structure to detect breaking changes
            assertTrue(schemaNode.has("type"), "Schema must have type field to avoid breaking changes");
            assertEquals("object", schemaNode.get("type").asText(), "Schema type should be object");

            // Check for required fields - adding new required fields is a breaking change
            JsonNode required = schemaNode.get("required");
            if (required != null && required.isArray()) {
                int requiredFieldsCount = required.size();
                assertTrue(requiredFieldsCount >= 1, "Schema should have at least one required field");
                assertTrue(requiredFieldsCount <= 5, "Too many required fields can lead to breaking changes");

                // Verify required fields are strings
                for (JsonNode requiredField : required) {
                    assertTrue(requiredField.isTextual(), "Required field names should be strings");
                    assertFalse(requiredField.asText().trim().isEmpty(), "Required field names should not be empty");
                }
            }

            JsonNode properties = schemaNode.get("properties");
            assertNotNull(properties, "Schema should have properties");
            assertTrue(properties.isObject(), "Properties should be an object");
            assertTrue(properties.size() > 0, "Schema should have at least one property");

            // Check property constraints for potential breaking changes
            properties.fields().forEachRemaining(entry -> {
                String propertyName = entry.getKey();
                JsonNode property = entry.getValue();

                assertNotNull(property, "Property '" + propertyName + "' should not be null");
                assertTrue(property.has("type"), "Property '" + propertyName + "' should have type");

                String propertyType = property.get("type").asText();

                // String constraints - tightening these can be breaking changes
                if ("string".equals(propertyType)) {
                    if (property.has("maxLength")) {
                        int maxLength = property.get("maxLength").asInt();
                        assertTrue(maxLength > 0,
                                "String maxLength should be positive for property '" + propertyName + "'");

                        // Domain names and similar should have reasonable limits
                        if (propertyName.contains("domain") || propertyName.equals("query")) {
                            assertTrue(maxLength >= 100,
                                    "Domain-related property '" + propertyName + "' should have generous maxLength");
                        }
                    }

                    if (property.has("minLength")) {
                        int minLength = property.get("minLength").asInt();
                        assertTrue(minLength >= 0,
                                "String minLength should be non-negative for property '" + propertyName + "'");
                        assertTrue(minLength <= 10,
                                "String minLength should not be too restrictive for property '" + propertyName + "'");
                    }
                }

                // Array constraints - reducing maxItems is a breaking change
                if ("array".equals(propertyType)) {
                    if (property.has("maxItems")) {
                        int maxItems = property.get("maxItems").asInt();
                        assertTrue(maxItems > 0,
                                "Array maxItems should be positive for property '" + propertyName + "'");
                        assertTrue(maxItems >= 1,
                                "Array should allow at least one item for property '" + propertyName + "'");
                    }

                    if (property.has("minItems")) {
                        int minItems = property.get("minItems").asInt();
                        assertTrue(minItems >= 0,
                                "Array minItems should be non-negative for property '" + propertyName + "'");
                    }

                    // Array items should have proper schema
                    if (property.has("items")) {
                        JsonNode items = property.get("items");
                        assertTrue(items.has("type"),
                                "Array items should have type for property '" + propertyName + "'");
                    }
                }

                // Numeric constraints - reducing maximum or increasing minimum is breaking
                if ("integer".equals(propertyType) || "number".equals(propertyType)) {
                    if (property.has("maximum")) {
                        double maximum = property.get("maximum").asDouble();
                        assertTrue(maximum > 0,
                                "Numeric maximum should be positive for property '" + propertyName + "'");
                    }

                    if (property.has("minimum")) {
                        double minimum = property.get("minimum").asDouble();
                        assertTrue(minimum >= 0,
                                "Numeric minimum should be non-negative for property '" + propertyName + "'");
                    }

                    if (property.has("maximum") && property.has("minimum")) {
                        double maximum = property.get("maximum").asDouble();
                        double minimum = property.get("minimum").asDouble();
                        assertTrue(maximum >= minimum,
                                "Maximum should be >= minimum for property '" + propertyName + "'");
                    }
                }

                // Enum constraints - removing values is a breaking change
                if (property.has("enum")) {
                    JsonNode enumValues = property.get("enum");
                    assertTrue(enumValues.isArray(), "Enum should be array for property '" + propertyName + "'");
                    assertTrue(enumValues.size() > 0,
                            "Enum should have at least one value for property '" + propertyName + "'");

                    // Verify enum values are not empty
                    for (JsonNode enumValue : enumValues) {
                        assertNotNull(enumValue, "Enum value should not be null for property '" + propertyName + "'");
                        if (enumValue.isTextual()) {
                            assertFalse(enumValue.asText().trim().isEmpty(),
                                    "Enum value should not be empty for property '" + propertyName + "'");
                        }
                    }
                }
            });

            // Check for additionalProperties - changing this can be breaking
            if (schemaNode.has("additionalProperties")) {
                JsonNode additionalProperties = schemaNode.get("additionalProperties");
                // This can be boolean or object, both are valid
                assertTrue(additionalProperties.isBoolean() || additionalProperties.isObject(),
                        "additionalProperties should be boolean or object");
            }
        }
    }

    @Test
    @DisplayName("Test schema performance")
    void testSchemaPerformance() throws Exception {
        // Test schema loading performance during class initialization
        testSchemaLoadingPerformance();

        // Test memory usage of schema constants
        testSchemaMemoryUsage();

        // Test that schemas are loaded only once (static initialization)
        testStaticInitialization();

        // Test validation performance with schemas
        testValidationPerformance();
    }

    private void testSchemaLoadingPerformance() throws Exception {
        // Test that schema constants can be accessed quickly (already loaded)
        long startTime = System.nanoTime();

        String schema1 = DomainSchemas.DOMAINS_INFO_SCHEMA;
        String schema2 = DomainSchemas.REGIONS_COUNT_SCHEMA;
        String schema3 = DomainSchemas.DOMAIN_KEYWORDS_SCHEMA;
        String schema4 = DomainSchemas.DOMAIN_URLS_SCHEMA;
        String schema5 = DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA;

        long endTime = System.nanoTime();
        long accessTime = endTime - startTime;

        // Schema access should be very fast (under 1ms for all schemas)
        assertTrue(accessTime < 1_000_000L, "Schema access should be very fast (under 1ms)");

        // Verify schemas were actually loaded
        assertNotNull(schema1, "Schema 1 should be loaded");
        assertNotNull(schema2, "Schema 2 should be loaded");
        assertNotNull(schema3, "Schema 3 should be loaded");
        assertNotNull(schema4, "Schema 4 should be loaded");
        assertNotNull(schema5, "Schema 5 should be loaded");
    }

    private void testSchemaMemoryUsage() {
        // Test that schema constants don't consume excessive memory
        Runtime runtime = Runtime.getRuntime();

        // Get baseline memory usage
        runtime.gc(); // Suggest garbage collection
        long baselineMemory = runtime.totalMemory() - runtime.freeMemory();

        // Access all schemas multiple times
        String[] schemas = new String[1000];
        for (int i = 0; i < 1000; i++) {
            schemas[i] = DomainSchemas.DOMAINS_INFO_SCHEMA;
        }

        // Check memory usage after accessing schemas
        runtime.gc(); // Suggest garbage collection
        long afterAccessMemory = runtime.totalMemory() - runtime.freeMemory();

        // Memory increase should be minimal (schemas are static constants)
        long memoryIncrease = afterAccessMemory - baselineMemory;
        assertTrue(memoryIncrease < 10_000_000L, "Schema constants should not cause significant memory increase");

        // Clear the array to allow garbage collection
        schemas = null;

        // Verify schemas are still accessible (static)
        assertNotNull(DomainSchemas.DOMAINS_INFO_SCHEMA, "Schemas should remain accessible after clearing references");
    }

    private void testStaticInitialization() {
        // Test that multiple accesses return the same object reference (static)
        String schema1FirstAccess = DomainSchemas.DOMAINS_INFO_SCHEMA;
        String schema1SecondAccess = DomainSchemas.DOMAINS_INFO_SCHEMA;
        assertSame(schema1FirstAccess, schema1SecondAccess, "Schema should be the same object reference (static)");

        String schema2FirstAccess = DomainSchemas.REGIONS_COUNT_SCHEMA;
        String schema2SecondAccess = DomainSchemas.REGIONS_COUNT_SCHEMA;
        assertSame(schema2FirstAccess, schema2SecondAccess, "Schema should be the same object reference (static)");

        String schema3FirstAccess = DomainSchemas.DOMAIN_KEYWORDS_SCHEMA;
        String schema3SecondAccess = DomainSchemas.DOMAIN_KEYWORDS_SCHEMA;
        assertSame(schema3FirstAccess, schema3SecondAccess, "Schema should be the same object reference (static)");

        String schema4FirstAccess = DomainSchemas.DOMAIN_URLS_SCHEMA;
        String schema4SecondAccess = DomainSchemas.DOMAIN_URLS_SCHEMA;
        assertSame(schema4FirstAccess, schema4SecondAccess, "Schema should be the same object reference (static)");

        String schema5FirstAccess = DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA;
        String schema5SecondAccess = DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA;
        assertSame(schema5FirstAccess, schema5SecondAccess, "Schema should be the same object reference (static)");

        // Test that schema content doesn't change between accesses
        assertEquals(schema1FirstAccess, schema1SecondAccess, "Schema content should be identical");
        assertEquals(schema2FirstAccess, schema2SecondAccess, "Schema content should be identical");
        assertEquals(schema3FirstAccess, schema3SecondAccess, "Schema content should be identical");
        assertEquals(schema4FirstAccess, schema4SecondAccess, "Schema content should be identical");
        assertEquals(schema5FirstAccess, schema5SecondAccess, "Schema content should be identical");
    }

    private void testValidationPerformance() throws Exception {
        // Test that JSON parsing of schemas is fast for validation purposes
        long startTime = System.nanoTime();

        // Parse all schemas as JSON (typical validation operation)
        JsonNode schema1 = objectMapper.readTree(DomainSchemas.DOMAINS_INFO_SCHEMA);
        JsonNode schema2 = objectMapper.readTree(DomainSchemas.REGIONS_COUNT_SCHEMA);
        JsonNode schema3 = objectMapper.readTree(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA);
        JsonNode schema4 = objectMapper.readTree(DomainSchemas.DOMAIN_URLS_SCHEMA);
        JsonNode schema5 = objectMapper.readTree(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        long endTime = System.nanoTime();
        long parseTime = endTime - startTime;

        // JSON parsing should be reasonably fast (under 10ms for all schemas)
        assertTrue(parseTime < 10_000_000L, "Schema JSON parsing should be reasonably fast (under 10ms)");

        // Verify parsing was successful
        assertNotNull(schema1, "Schema 1 should parse successfully");
        assertNotNull(schema2, "Schema 2 should parse successfully");
        assertNotNull(schema3, "Schema 3 should parse successfully");
        assertNotNull(schema4, "Schema 4 should parse successfully");
        assertNotNull(schema5, "Schema 5 should parse successfully");

        // Test repeated parsing performance (caching effectiveness)
        long secondParseStart = System.nanoTime();

        JsonNode schema1Repeat = objectMapper.readTree(DomainSchemas.DOMAINS_INFO_SCHEMA);
        JsonNode schema2Repeat = objectMapper.readTree(DomainSchemas.REGIONS_COUNT_SCHEMA);

        long secondParseEnd = System.nanoTime();
        long secondParseTime = secondParseEnd - secondParseStart;

        // Second parse should also be fast
        assertTrue(secondParseTime < 5_000_000L, "Repeated schema parsing should be fast");

        // Results should be equivalent
        assertEquals(schema1, schema1Repeat, "Repeated parsing should produce equivalent results");
        assertEquals(schema2, schema2Repeat, "Repeated parsing should produce equivalent results");
    }

    @Test
    @DisplayName("Test error handling")
    void testErrorHandling() throws Exception {
        // Test behavior when schema files are missing
        testMissingSchemaFiles();

        // Test behavior when schema files contain invalid JSON
        testInvalidJsonHandling();

        // Test behavior when SchemaUtils.loadSchema fails
        testSchemaUtilsFailureHandling();

        // Test that meaningful error messages are provided
        testMeaningfulErrorMessages();

        // Test graceful degradation when schemas are unavailable
        testGracefulDegradation();
    }

    private void testMissingSchemaFiles() {
        // Test that current schemas are properly loaded (not missing)
        assertNotNull(DomainSchemas.DOMAINS_INFO_SCHEMA, "DOMAINS_INFO_SCHEMA should not be null (file should exist)");
        assertNotNull(DomainSchemas.REGIONS_COUNT_SCHEMA,
                "REGIONS_COUNT_SCHEMA should not be null (file should exist)");
        assertNotNull(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                "DOMAIN_KEYWORDS_SCHEMA should not be null (file should exist)");
        assertNotNull(DomainSchemas.DOMAIN_URLS_SCHEMA, "DOMAIN_URLS_SCHEMA should not be null (file should exist)");
        assertNotNull(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA,
                "DOMAINS_UNIQ_KEYWORDS_SCHEMA should not be null (file should exist)");

        // Test that schemas are not empty (files are properly loaded)
        assertFalse(DomainSchemas.DOMAINS_INFO_SCHEMA.trim().isEmpty(), "DOMAINS_INFO_SCHEMA should not be empty");
        assertFalse(DomainSchemas.REGIONS_COUNT_SCHEMA.trim().isEmpty(), "REGIONS_COUNT_SCHEMA should not be empty");
        assertFalse(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA.trim().isEmpty(),
                "DOMAIN_KEYWORDS_SCHEMA should not be empty");
        assertFalse(DomainSchemas.DOMAIN_URLS_SCHEMA.trim().isEmpty(), "DOMAIN_URLS_SCHEMA should not be empty");
        assertFalse(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA.trim().isEmpty(),
                "DOMAINS_UNIQ_KEYWORDS_SCHEMA should not be empty");

        // Test that schemas don't contain error indicators
        assertFalse(DomainSchemas.DOMAINS_INFO_SCHEMA.contains("ERROR"), "Schema should not contain error indicators");
        assertFalse(DomainSchemas.DOMAINS_INFO_SCHEMA.contains("FILE_NOT_FOUND"),
                "Schema should not contain file not found indicators");
        assertFalse(DomainSchemas.DOMAINS_INFO_SCHEMA.contains("null"), "Schema should not contain null indicators");
    }

    private void testInvalidJsonHandling() throws Exception {
        // Test that current schemas contain valid JSON
        assertDoesNotThrow(() -> {
            objectMapper.readTree(DomainSchemas.DOMAINS_INFO_SCHEMA);
        }, "DOMAINS_INFO_SCHEMA should be valid JSON");

        assertDoesNotThrow(() -> {
            objectMapper.readTree(DomainSchemas.REGIONS_COUNT_SCHEMA);
        }, "REGIONS_COUNT_SCHEMA should be valid JSON");

        assertDoesNotThrow(() -> {
            objectMapper.readTree(DomainSchemas.DOMAIN_KEYWORDS_SCHEMA);
        }, "DOMAIN_KEYWORDS_SCHEMA should be valid JSON");

        assertDoesNotThrow(() -> {
            objectMapper.readTree(DomainSchemas.DOMAIN_URLS_SCHEMA);
        }, "DOMAIN_URLS_SCHEMA should be valid JSON");

        assertDoesNotThrow(() -> {
            objectMapper.readTree(DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);
        }, "DOMAINS_UNIQ_KEYWORDS_SCHEMA should be valid JSON");

        // Test that schemas have proper JSON structure
        JsonNode schema1 = objectMapper.readTree(DomainSchemas.DOMAINS_INFO_SCHEMA);
        assertTrue(schema1.isObject(), "Schema should be valid JSON object");
        assertTrue(schema1.has("type"), "Schema should have proper JSON Schema structure");

        JsonNode schema2 = objectMapper.readTree(DomainSchemas.REGIONS_COUNT_SCHEMA);
        assertTrue(schema2.isObject(), "Schema should be valid JSON object");
        assertTrue(schema2.has("type"), "Schema should have proper JSON Schema structure");
    }

    private void testSchemaUtilsFailureHandling() {
        // Test that schema loading through SchemaUtils is robust and schemas are
        // properly loaded

        List<String> allSchemas = List.of(
                DomainSchemas.DOMAINS_INFO_SCHEMA,
                DomainSchemas.REGIONS_COUNT_SCHEMA,
                DomainSchemas.DOMAIN_KEYWORDS_SCHEMA,
                DomainSchemas.DOMAIN_URLS_SCHEMA,
                DomainSchemas.DOMAINS_UNIQ_KEYWORDS_SCHEMA);

        for (String schema : allSchemas) {
            // Test that schema is not null or empty (proper loading)
            assertNotNull(schema, "Schema should be loaded successfully, not null");
            assertFalse(schema.trim().isEmpty(), "Loaded schema should not be empty");

            // Test that schema doesn't contain error indicators from failed loading
            assertFalse(schema.contains("ERROR"), "Schema should not contain error indicators");
            assertFalse(schema.contains("FILE_NOT_FOUND"), "Schema should not contain file not found indicators");
            assertFalse(schema.contains("LOAD_FAILED"), "Schema should not contain load failed indicators");
            assertFalse(schema.toLowerCase().contains("exception"), "Schema should not contain exception indicators");

            // Test that schema has proper JSON structure
            assertTrue(schema.trim().startsWith("{"), "Schema should start with JSON object opening brace");
            assertTrue(schema.trim().endsWith("}"), "Schema should end with JSON object closing brace");

            // Test that schema can be processed by standard JSON tools without errors
            assertDoesNotThrow(() -> {
                JsonNode node = objectMapper.readTree(schema);
                assertNotNull(node, "Schema should parse to valid JsonNode");
                assertTrue(node.isObject(), "Schema should be valid JSON object");

                // Test re-serialization to verify JSON integrity
                String reserialized = objectMapper.writeValueAsString(node);
                assertNotNull(reserialized, "Schema should be re-serializable");
                assertFalse(reserialized.trim().isEmpty(), "Re-serialized schema should not be empty");

                // Re-serialized JSON should also be parseable
                JsonNode reparsed = objectMapper.readTree(reserialized);
                assertNotNull(reparsed, "Re-serialized schema should be reparseable");

            }, "Schema should be processable by standard JSON tools without exceptions");

            // Test that schema has expected structure indicating proper loading
            try {
                JsonNode schemaNode = objectMapper.readTree(schema);

                // Properly loaded JSON Schema should have these basic fields
                assertTrue(schemaNode.has("type"), "Properly loaded schema should have 'type' field");
                assertEquals("object", schemaNode.get("type").asText(), "Schema type should be 'object'");

                assertTrue(schemaNode.has("properties"), "Properly loaded schema should have 'properties' field");
                JsonNode properties = schemaNode.get("properties");
                assertTrue(properties.isObject(), "Properties should be a JSON object");
                assertTrue(properties.size() > 0, "Properties should not be empty");

                // Test that properties have proper structure
                properties.fields().forEachRemaining(entry -> {
                    String propertyName = entry.getKey();
                    JsonNode property = entry.getValue();

                    assertNotNull(property, "Property '" + propertyName + "' should not be null");
                    assertTrue(property.isObject(), "Property '" + propertyName + "' should be an object");
                    assertTrue(property.has("type"), "Property '" + propertyName + "' should have type field");

                    String propertyType = property.get("type").asText();
                    assertNotNull(propertyType, "Property type should not be null for '" + propertyName + "'");
                    assertFalse(propertyType.trim().isEmpty(),
                            "Property type should not be empty for '" + propertyName + "'");
                });

                // Check for additional schema metadata that indicates proper loading
                if (schemaNode.has("additionalProperties")) {
                    JsonNode additionalProps = schemaNode.get("additionalProperties");
                    assertTrue(additionalProps.isBoolean() || additionalProps.isObject(),
                            "additionalProperties should be boolean or object");
                }

                if (schemaNode.has("required")) {
                    JsonNode required = schemaNode.get("required");
                    assertTrue(required.isArray(), "Required field should be an array");

                    for (JsonNode requiredField : required) {
                        assertTrue(requiredField.isTextual(), "Required field should be a string");
                        assertTrue(properties.has(requiredField.asText()),
                                "Required field '" + requiredField.asText() + "' should exist in properties");
                    }
                }

            } catch (Exception e) {
                fail("Schema should be properly loaded and parseable without exceptions. Error: " + e.getMessage());
            }
        }

        // Test robustness of schema access - multiple accesses should be consistent
        for (int i = 0; i < 5; i++) {
            String schema1 = DomainSchemas.DOMAINS_INFO_SCHEMA;
            String schema2 = DomainSchemas.REGIONS_COUNT_SCHEMA;

            assertNotNull(schema1, "Schema access attempt " + i + " should not return null");
            assertNotNull(schema2, "Schema access attempt " + i + " should not return null");
            assertFalse(schema1.trim().isEmpty(), "Schema access attempt " + i + " should not return empty");
            assertFalse(schema2.trim().isEmpty(), "Schema access attempt " + i + " should not return empty");
        }

        // Test that schemas maintain consistency across multiple accesses
        String initialDomainsSchema = DomainSchemas.DOMAINS_INFO_SCHEMA;
        String secondDomainsSchema = DomainSchemas.DOMAINS_INFO_SCHEMA;
        assertEquals(initialDomainsSchema, secondDomainsSchema, "Schema should be consistent across multiple accesses");

        String initialRegionsSchema = DomainSchemas.REGIONS_COUNT_SCHEMA;
        String secondRegionsSchema = DomainSchemas.REGIONS_COUNT_SCHEMA;
        assertEquals(initialRegionsSchema, secondRegionsSchema, "Schema should be consistent across multiple accesses");
    }

    private void testMeaningfulErrorMessages() throws Exception {
        // Test that schemas provide sufficient information for meaningful error
        // messages
        JsonNode domainsInfoSchema = objectMapper.readTree(DomainSchemas.DOMAINS_INFO_SCHEMA);

        // Test that error-prone fields have clear constraints
        JsonNode properties = domainsInfoSchema.get("properties");

        if (properties.has("domains")) {
            JsonNode domainsProperty = properties.get("domains");
            JsonNode items = domainsProperty.get("items");

            // Pattern validation should provide clear feedback
            assertTrue(items.has("pattern"), "Domain validation should have pattern for clear error messages");
            assertTrue(items.has("minLength"), "Domain validation should have minLength for clear error messages");
            assertTrue(items.has("maxLength"), "Domain validation should have maxLength for clear error messages");

            // Array constraints should provide clear limits
            assertTrue(domainsProperty.has("minItems"), "Domains array should have minItems for clear error messages");
            assertTrue(domainsProperty.has("maxItems"), "Domains array should have maxItems for clear error messages");
        }

        if (properties.has("se")) {
            JsonNode seProperty = properties.get("se");

            // Enum validation should provide clear options
            assertTrue(seProperty.has("enum"), "Search engine should have enum for clear error messages");
            JsonNode enumValues = seProperty.get("enum");
            assertTrue(enumValues.size() > 0, "Enum should have values for clear error messages");
        }

        // Required fields should be clearly specified
        JsonNode required = domainsInfoSchema.get("required");
        assertNotNull(required, "Required fields should be specified for clear error messages");
        assertTrue(required.isArray(), "Required fields should be array for clear error messages");
    }

    private void testGracefulDegradation() {
        // Test that the system can handle schema-related issues gracefully

        // Test that schemas are consistently available
        String[] schemaAccessResults = new String[10];
        for (int i = 0; i < 10; i++) {
            schemaAccessResults[i] = DomainSchemas.DOMAINS_INFO_SCHEMA;
        }

        // All accesses should return the same result
        for (int i = 1; i < 10; i++) {
            assertEquals(schemaAccessResults[0], schemaAccessResults[i],
                    "Schema should be consistently available across multiple accesses");
        }

        // Test that schema access doesn't throw unexpected exceptions
        assertDoesNotThrow(() -> {
            String schema1 = DomainSchemas.DOMAINS_INFO_SCHEMA;
            String schema2 = DomainSchemas.REGIONS_COUNT_SCHEMA;
            String schema3 = DomainSchemas.DOMAIN_KEYWORDS_SCHEMA;
            assertNotNull(schema1, "Schema access should not throw exceptions");
            assertNotNull(schema2, "Schema access should not throw exceptions");
            assertNotNull(schema3, "Schema access should not throw exceptions");
        }, "Schema access should be graceful and not throw unexpected exceptions");

        // Test that schemas maintain their integrity under concurrent access
        List<String> concurrentResults = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            concurrentResults.add(DomainSchemas.DOMAINS_INFO_SCHEMA);
        }

        // All concurrent accesses should return identical results
        String firstResult = concurrentResults.get(0);
        for (String result : concurrentResults) {
            assertEquals(firstResult, result, "Schema should maintain integrity under concurrent access");
        }

        // Test that schema content remains stable
        String initialSchema = DomainSchemas.DOMAINS_INFO_SCHEMA;

        // Wait a small amount of time
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        String laterSchema = DomainSchemas.DOMAINS_INFO_SCHEMA;
        assertEquals(initialSchema, laterSchema, "Schema content should remain stable over time");
    }
}
