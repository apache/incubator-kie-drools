package org.kie.pmml.pmml_4_2;

import org.kie.api.pmml.PMMLRuleExecutor;
import org.kie.api.pmml.PMMLRuleUnit;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;


public class DefaultRuleExecutor implements PMMLRuleExecutor {

    @Override
    public int executeRules(RuleUnitExecutor executor, PMMLRuleUnit ruleUnit) {
        RuleUnit ru = (RuleUnit) ruleUnit;
        return executor.run(ru.getClass());
    }

}
