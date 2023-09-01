package org.kie.internal.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class KieMeta {

    private final static boolean productized;

    static {
        Properties properties = new Properties();
        try {
            InputStream in = KieMeta.class.getResourceAsStream("kieMeta.properties");
            properties.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("The classpath file kieMeta.properties is missing.");
        }
        productized = Boolean.valueOf(properties.getProperty("productized"));
    }

    public static boolean isProductized() {
        return productized;
    }

}
