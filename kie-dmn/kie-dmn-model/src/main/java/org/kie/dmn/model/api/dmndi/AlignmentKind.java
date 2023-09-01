package org.kie.dmn.model.api.dmndi;

public enum AlignmentKind {

    START("start"),
    END("end"),
    CENTER("center");

    private final String value;

    AlignmentKind(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static AlignmentKind fromValue(String v) {
        for (AlignmentKind c: AlignmentKind.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
