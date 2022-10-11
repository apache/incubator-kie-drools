package org.optaplanner.core.impl.testutil;

// TODO remove this when Bavet is productized
public final class DisabledInProductizationCheck {

    /**
     * @see DisabledInProductization
     * @return true if a "productized" system property is set to "" or "true".
     */
    public static boolean isProductized() {
        String property = System.getProperty("productized", "false")
                .trim();
        return property.equals("true") || property.equals("");

    }

}
