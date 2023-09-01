package org.kie.dmn.model.v1_4;

public enum TDecisionTableOrientation {

    RULE_AS_ROW("Rule-as-Row"),
    RULE_AS_COLUMN("Rule-as-Column"),
    CROSS_TABLE("CrossTable");
    private final String value;

    TDecisionTableOrientation(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static TDecisionTableOrientation fromValue(String v) {
        for (TDecisionTableOrientation c: TDecisionTableOrientation.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
