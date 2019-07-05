package org.drools.project.model;

import org.kie.kogito.rules.KieRuntimeBuilder;
import org.kie.kogito.rules.impl.SessionMemory;
import org.drools.modelcompiler.SessionRuleUnitInstance;
import org.kie.kogito.rules.RuleUnit;

public class SessionRuleUnit implements RuleUnit<SessionMemory> {
    
    KieRuntimeBuilder runtimeBuilder;

    @Override
    public SessionRuleUnitInstance createInstance( SessionMemory memory ) {
        return new SessionRuleUnitInstance(this, memory, runtimeBuilder.newKieSession("$SessionName$"));
    }
}