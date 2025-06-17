package com.serpstat.domains.keywords;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * JSON schemas for keyword competitors tools
 */
public class KeywordCompetitorsSchemas {

    /**
     * JSON schema for keyword competitors method
     */
    public static final String KEYWORD_COMPETITORS_SCHEMA = loadSchema("keyword_competitors.json");

    private static String loadSchema(String filename) {
        try (InputStream is = KeywordCompetitorsSchemas.class.getResourceAsStream("/schemas/keywords/" + filename)) {
            if (is == null) {
                throw new RuntimeException("Schema file not found: " + filename);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load schema: " + filename, e);
        }
    }
}