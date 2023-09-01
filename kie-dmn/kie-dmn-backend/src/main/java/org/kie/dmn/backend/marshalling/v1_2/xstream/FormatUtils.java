package org.kie.dmn.backend.marshalling.v1_2.xstream;

import java.util.Objects;

public class FormatUtils {

    public static String manageDouble(Double d) {
        Objects.requireNonNull(d);
        long longValue = d.longValue();
        if (d == longValue) {
            return String.format("%d", longValue);
        } else {
            return String.format("%s", d);
        }
    }

    private FormatUtils() {
        // no constructor for utils class.
    }
}
