package org.kie.api.runtime.rule;

import org.kie.api.definition.rule.Rule;

public interface TimedRuleExecutionFilter {
    boolean accept(Rule[] rules);
}
