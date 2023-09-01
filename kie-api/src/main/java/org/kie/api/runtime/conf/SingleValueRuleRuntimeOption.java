package org.kie.api.runtime.conf;

public interface SingleValueRuleRuntimeOption extends SingleValueKieSessionOption {
    String TYPE = "Rule";
    default String type() {
        return TYPE;
    }
}
