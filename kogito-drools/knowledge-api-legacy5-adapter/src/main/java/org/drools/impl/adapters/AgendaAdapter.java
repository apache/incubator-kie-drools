package org.drools.impl.adapters;

import org.drools.runtime.rule.ActivationGroup;
import org.drools.runtime.rule.Agenda;
import org.drools.runtime.rule.AgendaGroup;
import org.drools.runtime.rule.RuleFlowGroup;

public class AgendaAdapter implements Agenda {

    private final org.kie.api.runtime.rule.Agenda delegate;

    public AgendaAdapter(org.kie.api.runtime.rule.Agenda delegate) {
        this.delegate = delegate;
    }

    public void clear() {
        delegate.clear();
    }

    public AgendaGroup getAgendaGroup(String name) {
        return new AgendaGroupAdapter(delegate.getAgendaGroup(name));
    }

    public ActivationGroup getActivationGroup(String name) {
        return new ActivationGroupAdapter(delegate.getActivationGroup(name));
    }

    public RuleFlowGroup getRuleFlowGroup(String name) {
        return new RuleFlowGroupAdapter(delegate.getRuleFlowGroup(name));
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AgendaAdapter && delegate.equals(((AgendaAdapter)obj).delegate);
    }
}
