package org.drools.core.impl;

import org.drools.core.datasources.CursoredDataSource;
import org.drools.core.datasources.InternalDataSource;
import org.drools.core.ruleunit.RuleUnitFactory;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;

public final class UnitExecutor implements RuleUnitExecutor {

    public final UnitScheduler scheduler;
    public final RuleUnitFactory ruleUnitFactory;
    public final UnitSession.Factory sessionFactory;
    private StatefulKnowledgeSessionImpl session;

    public UnitExecutor() {
        this.session = new StatefulKnowledgeSessionImpl();
        this.sessionFactory = UnitSession.Factory.of(
                this, this.session);
        this.scheduler = new UnitScheduler();
        this.ruleUnitFactory = new RuleUnitFactory();
    }

    public UnitExecutor(KieSession session) {
        this.session = (StatefulKnowledgeSessionImpl) session;
        this.sessionFactory = UnitSession.Factory.of(
                this, this.session);
        this.scheduler = new UnitScheduler();
        this.ruleUnitFactory = new RuleUnitFactory();
    }

    @Override
    public int run(Class<? extends RuleUnit> ruleUnitClass) {
        RuleUnit unit = ruleUnitFactory.getOrCreateRuleUnit(
                new InternalUnitExecutorDelegate(this), ruleUnitClass);
        UnitSession session = sessionFactory.create(unit);
        return runSession(session);
    }

    @Override
    public int run(RuleUnit ruleUnit) {
        RuleUnit unit = ruleUnitFactory.injectUnitVariables(
                new InternalUnitExecutorDelegate(this), ruleUnit);
        UnitSession session = sessionFactory.create(unit);
        return runSession(session);
    }

    private int runSession(UnitSession session) {
        int fired = 0;
        scheduler.schedule(session);
        session = scheduler.next();
        while (session != null) {
            fired += session.fireAllRules();
            session = scheduler.next();
        }
        return fired;
    }

    @Override
    public RuleUnitExecutor bind(KieBase kiebase) {
        InternalKnowledgeBase kb = (InternalKnowledgeBase) kiebase;
        if (!kb.hasUnits()) {
            throw new IllegalStateException(
                    "Cannot create a RuleUnitExecutor against a KieBase without units");
        }
        sessionFactory.bind(kb);
        return this;
    }

    @Override
    public KieSession getKieSession() {
        return session;
    }

    @Override
    public void runUntilHalt(Class<? extends RuleUnit> ruleUnitClass) {
        runUntilHalt(ruleUnitFactory.getOrCreateRuleUnit(
                new InternalUnitExecutorDelegate(this), ruleUnitClass));
    }

    @Override
    public void runUntilHalt(RuleUnit ruleUnit) {
        UnitSession session = sessionFactory.create(ruleUnit);
        scheduler.schedule(session);
        scheduler.next();
        session.fireUntilHalt();
    }

    @Override
    public void halt() {
        scheduler.halt();
    }

    @Override
    public <T> DataSource<T> newDataSource(String name, T... items) {
        DataSource<T> dataSource = new CursoredDataSource(session);
        for (T item : items) {
            dataSource.insert(item);
        }
        ruleUnitFactory.bindVariable(name, dataSource);
        return dataSource;
    }

    @Override
    public RuleUnitExecutor bindVariable(String name, Object value) {
        ruleUnitFactory.bindVariable(name, value);
        if (value instanceof InternalDataSource) {
            bindDataSource((InternalDataSource) value);
        }
        return this;
    }

    public void bindDataSource(InternalDataSource dataSource) {
        dataSource.setWorkingMemory(session);
    }

    @Override
    public void dispose() {

    }

    public static UnitExecutor create() {
        return new UnitExecutor();
    }
}
