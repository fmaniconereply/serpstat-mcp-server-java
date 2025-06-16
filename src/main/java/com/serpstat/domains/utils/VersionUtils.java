package com.serpstat.domains.utils;

import java.io.IOException;
import java.util.Properties;

public class VersionUtils {
    public static String getVersion() {
        Properties properties = new Properties();
        try {
            properties.load(VersionUtils.class.getClassLoader().getResourceAsStream("version.properties"));
            return properties.getProperty("version");
        } catch (IOException e) {
            throw new RuntimeException("Failed to load version.properties", e);
        }
    }
}
