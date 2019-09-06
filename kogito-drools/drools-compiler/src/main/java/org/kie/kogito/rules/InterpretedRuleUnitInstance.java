package org.kie.kogito.rules;

import org.kie.api.runtime.KieSession;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitMemory;

public class InterpretedRuleUnitInstance<T extends RuleUnitMemory> extends org.drools.core.ruleunit.impl.AbstractRuleUnitInstance<T> {

    InterpretedRuleUnitInstance(RuleUnit<T> unit, T workingMemory, KieSession ksession) {
        super(unit, workingMemory, ksession);
    }
}
