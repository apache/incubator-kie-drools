package org.optaplanner.benchmark.impl.report;

public class ReportHelper {

    /**
     * Escape illegal HTML element id characters, such as a dot.
     * <p/>
     * This escape function guarantees that 2 distinct strings will result into 2 distinct escape strings
     * (presuming that both have been escaped by this method).
     * @param rawHtmlId never null
     * @return never null
     */
    public static String escapeHtmlId(String rawHtmlId) {
        return rawHtmlId
                .replaceAll("-", "--") // make '-' the escape character
                .replaceAll("\\.", "-d")
                .replaceAll(":", "-c")
                .replaceAll(";", "-s")
                .replaceAll("\\$", "-m");
    }

}
