package org.kie.dmn.backend.marshalling.v1_2.xstream;


public class FormatUtils {

    public static String manageDouble(Double d) {
        if (d == (long) (double) d) {
            return String.format("%d", (long) (double) d);
        } else {
            return String.format("%s", d);
        }
    }
}
