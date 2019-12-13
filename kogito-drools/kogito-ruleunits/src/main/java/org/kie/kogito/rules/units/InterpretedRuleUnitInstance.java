package org.kie.kogito.rules.units;

import org.kie.api.runtime.KieSession;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitData;
import org.kie.kogito.rules.units.AbstractRuleUnitInstance;

public class InterpretedRuleUnitInstance<T extends RuleUnitData> extends AbstractRuleUnitInstance<T> {

    InterpretedRuleUnitInstance(RuleUnit<T> unit, T workingMemory, KieSession ksession) {
        super(unit, workingMemory, ksession);
    }
}
