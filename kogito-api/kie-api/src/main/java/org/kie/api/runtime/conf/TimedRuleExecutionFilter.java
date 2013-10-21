package org.kie.api.runtime.conf;

import org.kie.api.definition.rule.Rule;

public interface TimedRuleExecutionFilter {
    boolean accept(Rule[] rules);
}
