package com.serpstat.domains.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility class for loading JSON schemas.
 */
public class SchemaUtils {

    /**
     * Loads a JSON schema from the specified path.
     *
     * @param clazz The class to use for resource loading.
     * @param path  The path to the schema file.
     * @return The schema content as a string.
     * @throws RuntimeException If the schema file is not found or cannot be loaded.
     */
    public static String loadSchema(Class<?> clazz, String path) {
        try (InputStream is = clazz.getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("Schema file not found: " + path);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load schema: " + path, e);
        }
    }
}