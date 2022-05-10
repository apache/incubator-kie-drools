package org.drools.parser;

/**
 * will be merged in drools-util
 */
public class StringUtils {

    private StringUtils() {
    }

    public static String safeStripStringDelimiters(String value) {
        if (value != null) {
            value = value.trim();
            if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
                value = value.substring(1, value.length() - 1);
            }
        }
        return value;
    }
}
