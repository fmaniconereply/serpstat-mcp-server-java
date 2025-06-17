package com.serpstat.domains.keywords;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class KeywordSchemas {

    public static final String GET_KEYWORDS_SCHEMA = loadSchema("get_keywords.json");
    public static final String GET_RELATED_KEYWORDS_SCHEMA = loadSchema("get_related_keywords.json");

    private static String loadSchema(String filename) {
        try (InputStream is = KeywordSchemas.class.getResourceAsStream("/schemas/keywords/" + filename)) {
            if (is == null) {
                throw new RuntimeException("Schema file not found: " + filename);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load schema: " + filename, e);
        }
    }
}
