package org.drools.core.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.core.SessionConfigurationImpl;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.spi.Activation;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleUnit;

import static org.drools.core.ruleunit.RuleUnitUtil.RULE_UNIT_ENTRY_POINT;

public interface UnitSession {

    RuleUnit unit();

    void halt();

    void fireUntilHalt();

    int fireAllRules();

    void yield(RuleUnit unit);

    void dispose();

    boolean isActive();

    void registerGuard(GuardedRuleUnitSession session, Activation activation);

    void unregisterGuard(Activation activation);

    Collection<GuardedRuleUnitSession> getGuards();

    class EntryPoint {

        private final StatefulKnowledgeSessionImpl session;
        private final Map<Class<?>, FactHandle> registry = new HashMap<>();

        public EntryPoint(StatefulKnowledgeSessionImpl session) {
            this.session = session;
        }

        public void bind(RuleUnit unit) {
            registry.computeIfAbsent(unit.getClass(),
                                     c -> session.getEntryPoint(RULE_UNIT_ENTRY_POINT).insert(unit));
        }
    }

    class Registry {

        private final Map<RuleUnit.Identity, UnitSession> registry = new HashMap<>();

        <T extends UnitSession> T register(T session) {
            if (registry.containsKey(session.unit().getUnitIdentity())) {
                return (T) registry.get(session.unit().getUnitIdentity());
            } else {
                registry.put(session.unit().getUnitIdentity(), session);
                return session;
            }
        }

        public UnitSession get(RuleUnit unit) {
            return registry.get(unit.getUnitIdentity());
        }
    }

    class Factory {

        private final StatefulKnowledgeSessionImpl session;
        private final EntryPoint entryPoint;
        private final Registry registry;
        private InternalKnowledgeBase kiebase;

        public Factory(UnitExecutor executor, StatefulKnowledgeSessionImpl session) {
            this.session = session;
            this.kiebase = (InternalKnowledgeBase) session.getKieBase();
            this.entryPoint = new EntryPoint(session);
            this.registry = new Registry();

            this.session.init(
                    new SessionConfigurationImpl(),
                    EnvironmentFactory.newEnvironment());

            this.session.ruleUnitExecutor = new InternalUnitExecutorDelegate(executor);
            this.session.agendaEventSupport = new AgendaEventSupport();
            this.session.ruleRuntimeEventSupport = new RuleRuntimeEventSupport();
            this.session.ruleEventListenerSupport = new RuleEventListenerSupport();
        }

        public static Factory of(UnitExecutor executor, StatefulKnowledgeSessionImpl session) {
            return new Factory(executor, session);
        }

        public RuleUnitSession create(RuleUnit unit) {
            return registry.register(new RuleUnitSession(
                    unit,
                    session,
                    entryPoint));
        }

        public GuardedRuleUnitSession createGuard(RuleUnit unit) {
            return registry.register(new GuardedRuleUnitSession(
                    unit,
                    session,
                    entryPoint));
        }

        public void bind(InternalKnowledgeBase kiebase) {
            this.kiebase = kiebase;
            this.session.handleFactory = kiebase.newFactHandleFactory();
            this.session.bindRuleBase(kiebase, null, false);
        }
    }
}
