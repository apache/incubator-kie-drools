package org.drools.ruleunits.impl;

import org.drools.ruleunits.api.RuleUnit;
import org.drools.ruleunits.api.RuleUnitInstance;

public abstract class SessionUnit implements RuleUnit<SessionData> {

    public void evaluate(SessionData data) {
        try (final RuleUnitInstance ruleUnitInstance = createInstance(data)) {
            ruleUnitInstance.fire();
        }
    }
}
