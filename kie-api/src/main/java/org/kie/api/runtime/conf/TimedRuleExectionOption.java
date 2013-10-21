package org.kie.api.runtime.conf;

import org.kie.api.definition.rule.Rule;

public class TimedRuleExectionOption implements SingleValueKieSessionOption {

    private static final long serialVersionUID = 510l;

    public static final String PROPERTY_NAME = "drools.timedRuleExection";

    public static final TimedRuleExectionOption YES = new TimedRuleExectionOption(new TimedRuleExecutionFilter() {
        @Override
        public boolean accept(Rule[] rules) {
            return true;
        }
    });

    public static final TimedRuleExectionOption NO = new TimedRuleExectionOption(null);

    private final TimedRuleExecutionFilter filter;

    private TimedRuleExectionOption( final TimedRuleExecutionFilter filter ) {
        this.filter = filter;
    }

    public String getPropertyName() {
        return PROPERTY_NAME;
    }

    public TimedRuleExecutionFilter getFilter() {
        return filter;
    }

    public static class FILTERED extends TimedRuleExectionOption {
        public FILTERED(TimedRuleExecutionFilter filter) {
            super(filter);
        }
    }

    public static TimedRuleExectionOption resolve(String value) {
        return Boolean.valueOf( value ) ? YES : NO;
    }
}
