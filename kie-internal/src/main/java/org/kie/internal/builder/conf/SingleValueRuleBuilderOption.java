package org.kie.internal.builder.conf;

public interface SingleValueRuleBuilderOption extends SingleValueKieBuilderOption {
    String TYPE = "Rule";
    default String type() {
        return TYPE;
    }
}
