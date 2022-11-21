package org.drools.ruleunits.impl.conf;

import java.util.ArrayList;
import java.util.List;

import org.drools.ruleunits.api.conf.RuleConfig;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.internal.event.rule.RuleEventListener;


public class RuleConfigImpl implements RuleConfig {

    private final List<AgendaEventListener> agendaEventListeners;
    private final List<RuleRuntimeEventListener> ruleRuntimeEventListeners;
    private final List<RuleEventListener> ruleEventListeners;

    public RuleConfigImpl() {
        agendaEventListeners = new ArrayList<>();
        ruleRuntimeEventListeners = new ArrayList<>();
        ruleEventListeners = new ArrayList<>();
    }

    @Override
    public List<AgendaEventListener> getAgendaEventListeners() {
        return agendaEventListeners;
    }

    @Override
    public List<RuleRuntimeEventListener> getRuleRuntimeListeners() {
        return ruleRuntimeEventListeners;
    }

    @Override
    public List<RuleEventListener> getRuleEventListeners() {
        return ruleEventListeners;
    }

}
