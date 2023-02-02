package org.kie.api.conf;

public interface MultiValueRuleBaseOption extends MultiValueKieBaseOption {
    static String TYPE = SingleValueRuleBaseOption.TYPE;
    
    default String type() {
        return TYPE;
    }
}
