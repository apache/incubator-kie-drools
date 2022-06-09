package org.optaplanner.benchmark.impl.report;

public class ReportHelper {

    /**
     * Escape illegal HTML element id characters, such as a dot.
     * <p>
     * This escape function guarantees that 2 distinct strings will result into 2 distinct escape strings
     * (presuming that both have been escaped by this method).
     *
     * @param rawHtmlId never null
     * @return never null
     */
    public static String escapeHtmlId(String rawHtmlId) {
        // Uses unicode numbers to escape, see http://unicode-table.com
        // Uses '-' as the escape character
        return rawHtmlId
                .replaceAll(" ", "-0020")
                .replaceAll("!", "-0021")
                .replaceAll("#", "-0023")
                .replaceAll("\\$", "-0024")
                .replaceAll(",", "-002C")
                .replaceAll("-", "-002D")
                .replaceAll("\\.", "-002E")
                .replaceAll("\\(", "-0028")
                .replaceAll("\\)", "-0029")
                .replaceAll(":", "-003A")
                .replaceAll(";", "-003B")
                .replaceAll("\\?", "-003F");
    }

}
