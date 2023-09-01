package org.kie.api.runtime.conf;

import org.kie.api.conf.OptionKey;
import org.kie.api.definition.rule.Rule;

public class TimedRuleExecutionOption implements SingleValueRuleRuntimeOption {

    private static final long serialVersionUID = 510l;

    public static final String PROPERTY_NAME = "drools.timedRuleExecution";

    public static OptionKey<TimedRuleExecutionOption> KEY = new OptionKey<>(TYPE, PROPERTY_NAME);

    public static final TimedRuleExecutionOption YES = new TimedRuleExecutionOption(rules -> true);

    public static final TimedRuleExecutionOption NO = new TimedRuleExecutionOption(null);

    private final TimedRuleExecutionFilter filter;

    private TimedRuleExecutionOption( final TimedRuleExecutionFilter filter ) {
        this.filter = filter;
    }

    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public TimedRuleExecutionFilter getFilter() {
        return filter;
    }

    public static class FILTERED extends TimedRuleExecutionOption {
        public FILTERED(TimedRuleExecutionFilter filter) {
            super(filter);
        }
    }

    public static TimedRuleExecutionOption resolve(String value) {
        return Boolean.valueOf( value ) ? YES : NO;
    }
}
