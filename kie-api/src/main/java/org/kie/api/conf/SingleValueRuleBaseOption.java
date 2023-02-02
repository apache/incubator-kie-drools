package org.kie.api.conf;

public interface SingleValueRuleBaseOption extends SingleValueKieBaseOption {
    String TYPE = "Rule";
    default String type() {
        return TYPE;
    }
}
