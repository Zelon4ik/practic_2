package com.mysuperproject.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {

    private static final Properties PROPERTIES = new Properties();

    static {
        loadProperties();
    }

    private PropertiesUtil() {}

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static void set(String key, String value) {
        PROPERTIES.setProperty(key, value);
    }

    private static void loadProperties() {
        try (InputStream inputStream =
                PropertiesUtil.class
                        .getClassLoader()
                        .getResourceAsStream("application.properties")) {
            if (inputStream != null) {
                PROPERTIES.load(inputStream);
            } else {
                System.err.println("Warning: application.properties not found in classpath.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load application.properties", e);
        }
    }
}
