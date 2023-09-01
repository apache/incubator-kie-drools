package org.kie.dmn.model.api;

public enum FunctionKind {

    FEEL("FEEL"),
    JAVA("Java"),
    PMML("PMML");
    private final String value;

    FunctionKind(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static FunctionKind fromValue(String v) {
        for (FunctionKind c: FunctionKind.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        if ("Java".equalsIgnoreCase(v)) {
            return FunctionKind.JAVA;
        }
        throw new IllegalArgumentException(v);
    }

}
