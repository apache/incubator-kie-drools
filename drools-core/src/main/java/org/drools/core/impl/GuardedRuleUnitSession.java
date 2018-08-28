package org.drools.core.impl;

import java.util.HashSet;
import java.util.Set;

import org.drools.core.spi.Activation;
import org.kie.api.runtime.rule.RuleUnit;

public class GuardedRuleUnitSession extends RuleUnitSession {

    private Set<Activation> activations = new HashSet<>();

    public GuardedRuleUnitSession(
            RuleUnit unit,
            StatefulKnowledgeSessionImpl session,
            EntryPoint entryPoint) {
        super(unit, session, entryPoint);
    }

    public void addActivation(Activation activation) {
        activations.add(activation);
    }

    public void removeActivation(Activation activation) {
        activations.remove(activation);
    }

    public boolean isActive() {
        return !activations.isEmpty();
    }

    @Override
    public String toString() {
        return "GS:" + unit();
    }
}
