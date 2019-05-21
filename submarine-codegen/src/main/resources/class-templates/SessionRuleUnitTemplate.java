package org.drools.project.model;

import org.drools.modelcompiler.KieRuntimeBuilder;
import org.drools.modelcompiler.SessionMemory;
import org.drools.modelcompiler.SessionRuleUnitInstance;
import org.kie.submarine.rules.impl.AbstractRuleUnit;

@javax.inject.Singleton
public class SessionRuleUnit extends AbstractRuleUnit<SessionMemory> {

    @javax.inject.Inject
    KieRuntimeBuilder runtimeBuilder;

    public SessionRuleUnit() {
    }

    @Override
    public SessionRuleUnitInstance createInstance( SessionMemory memory ) {
        return new SessionRuleUnitInstance(this, memory, runtimeBuilder.newKieSession());
    }

    public SessionRuleUnitInstance createInstance( SessionMemory memory, String sessionName ) {
        return new SessionRuleUnitInstance(this, memory, runtimeBuilder.newKieSession(sessionName));
    }
}