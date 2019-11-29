package org.drools.project.model;

import org.kie.kogito.rules.KieRuntimeBuilder;
import org.kie.kogito.rules.impl.SessionData;
import org.kie.kogito.rules.impl.SessionUnit;
import org.drools.core.ruleunit.impl.SessionRuleUnitInstance;

public class SessionRuleUnit extends SessionUnit {
    
    KieRuntimeBuilder runtimeBuilder;

    @Override
    public SessionRuleUnitInstance createInstance( SessionData memory, String name ) {
        return new SessionRuleUnitInstance(this, memory, runtimeBuilder.newKieSession("$SessionName$"));
    }
}