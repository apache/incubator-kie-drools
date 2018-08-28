package org.drools.core.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.core.common.AgendaGroupQueueImpl;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.ruleunit.RuleUnitDescr;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaGroup;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.rule.RuleUnit;

public class RuleUnitSession implements UnitSession {


    private boolean yielded = false;

    @Override
    public RuleUnit unit() {
        return ruleUnit;
    }

    @Override
    public void halt() {
        unbindRuleUnit();
    }

    @Override
    public void fireUntilHalt() {
        bindRuleUnit();
        try {
            session.fireUntilHalt();
        } finally {
            unbindRuleUnit();
        }
    }

    private final StatefulKnowledgeSessionImpl session;
    private final Agenda agenda;
    private final EntryPoint entryPoint;
    private RuleUnit ruleUnit;
    private RuleUnitDescr ruleUnitDescr;
    private final Set<GuardedRuleUnitSession> guards = new HashSet<>();

    private AtomicBoolean suspended = new AtomicBoolean(false);

    public RuleUnitSession(
            RuleUnit unit,
            StatefulKnowledgeSessionImpl session,
            EntryPoint entryPoint) {
        this.ruleUnit = unit;
        this.session = session;
        this.agenda = new Agenda(session);
        this.entryPoint = entryPoint;
    }

    @Override
    public int fireAllRules() {
        bindRuleUnit();
        try {
            return session.fireAllRules();
        } finally {
            unbindRuleUnit();
        }
    }

    public void bindRuleUnit() {
        suspended.set(false);
        ruleUnit.onStart();

        entryPoint.bind(ruleUnit);

        this.ruleUnitDescr = session.kBase.getRuleUnitRegistry().getRuleUnitDescr(ruleUnit);
        getGlobalResolver().setDelegate(new RuleUnitGlobals(ruleUnitDescr, ruleUnit));
        ruleUnitDescr.bindDataSources(session, ruleUnit);

        agenda.focus(ruleUnit);
    }

    private void unbindRuleUnit() {
        if (yielded) {
            yielded = false;
            return;
        }
        ruleUnitDescr.unbindDataSources(session, ruleUnit);
        (getGlobalResolver()).setDelegate(null);
        ruleUnit.onEnd();
        suspended.set(true);
    }

    @Override
    public void yield(RuleUnit unit) {
        yielded = true;
        ruleUnit.onYield(unit);
        session.getPropagationList().flush();
        agenda.unfocus(ruleUnit);
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void registerGuard(GuardedRuleUnitSession session, Activation activation) {
        guards.add(session);
        session.addActivation(activation);
    }

    @Override
    public void unregisterGuard(Activation activation) {
        guards.forEach(g -> g.removeActivation(activation));
    }

    @Override
    public Collection<GuardedRuleUnitSession> getGuards() {
        return Collections.unmodifiableCollection(guards);
    }

    private Globals getGlobalResolver() {
        return (Globals) session.getGlobalResolver();
    }

    private static class RuleUnitGlobals implements Globals {

        private final RuleUnitDescr ruDescr;
        private final RuleUnit ruleUnit;

        private RuleUnitGlobals(RuleUnitDescr ruDescr, RuleUnit ruleUnit) {
            this.ruDescr = ruDescr;
            this.ruleUnit = ruleUnit;
        }

        @Override
        public Object get(String identifier) {
            return ruDescr.getValue(ruleUnit, identifier);
        }

        @Override
        public void set(String identifier, Object value) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDelegate(Globals delegate) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<String> getGlobalKeys() {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public String toString() {
        return "S:" + unit().getClass().getSimpleName();
    }

    private static class Agenda {
        private static final String EMPTY_AGENDA = "$$$EMPTY_AGENDA$$";
        final StatefulKnowledgeSessionImpl session;

        Agenda(StatefulKnowledgeSessionImpl session) {
            this.session = session;
        }

        void focus(RuleUnit unit) {
            InternalAgenda agenda = session.getAgenda();
            InternalAgendaGroup unitGroup = (InternalAgendaGroup) agenda.getAgendaGroup(unit.getClass().getName());
            unitGroup.setAutoDeactivate(false);
            unitGroup.setFocus();
        }
        void unfocus(RuleUnit unit) {
            InternalAgenda agenda = session.getAgenda();
            AgendaGroup agendaGroup = agenda.getAgendaGroup(unit.getClass().getName());
            agenda.getStackList().remove(agendaGroup);

            AgendaGroupQueueImpl unitGroup =
                    (AgendaGroupQueueImpl) session.getAgenda().getAgendaGroup(EMPTY_AGENDA);
            unitGroup.setAutoDeactivate(false);
            unitGroup.setKeepWhenEmpty(true);
            unitGroup.setFocus();

        }
    }
}
