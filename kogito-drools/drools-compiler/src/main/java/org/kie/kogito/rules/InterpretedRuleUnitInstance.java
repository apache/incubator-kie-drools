package org.kie.kogito.rules;

import org.kie.api.runtime.KieSession;

public class InterpretedRuleUnitInstance<T extends RuleUnitData> extends org.drools.core.ruleunit.impl.AbstractRuleUnitInstance<T> {

    InterpretedRuleUnitInstance(RuleUnit<T> unit, T workingMemory, KieSession ksession) {
        super(unit, workingMemory, ksession);
    }
}
