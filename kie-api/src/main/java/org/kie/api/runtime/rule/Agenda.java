package org.kie.api.runtime.rule;

public interface Agenda {
    public void clear();

    AgendaGroup getAgendaGroup(String name);

    ActivationGroup getActivationGroup(String name);

    RuleFlowGroup getRuleFlowGroup(String name);
}
