package org.drools.project.model;

import org.kie.kogito.rules.KieRuntimeBuilder;
import org.kie.kogito.rules.units.SessionData;
import org.kie.kogito.rules.units.SessionRuleUnitInstance;
import org.kie.kogito.rules.units.SessionUnit;

public class SessionRuleUnit extends SessionUnit {
    
    KieRuntimeBuilder runtimeBuilder;

    @Override
    public String id() {
        return "$SessionName$";
    }

    @Override
    public SessionRuleUnitInstance createInstance( SessionData memory, String name ) {
        return new SessionRuleUnitInstance(this, memory, runtimeBuilder.newKieSession("$SessionName$"));
    }
}