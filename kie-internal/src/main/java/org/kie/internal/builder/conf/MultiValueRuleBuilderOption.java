package org.kie.internal.builder.conf;

public interface MultiValueRuleBuilderOption extends MultiValueKieBuilderOption {
    String TYPE = SingleValueRuleBuilderOption.TYPE;
    default String type() {
        return TYPE;
    }
}
