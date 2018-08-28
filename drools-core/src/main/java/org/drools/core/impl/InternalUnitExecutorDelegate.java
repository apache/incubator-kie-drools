package org.drools.core.impl;

import java.util.Collection;

import org.drools.core.datasources.InternalDataSource;
import org.drools.core.spi.Activation;
import org.kie.api.KieBase;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;

public class InternalUnitExecutorDelegate implements InternalRuleUnitExecutor {

    private final UnitExecutor unitExecutor;

    @Override
    public int run(Class<? extends RuleUnit> ruleUnitClass) {
        return unitExecutor.run(ruleUnitClass);
    }

    @Override
    public int run(RuleUnit ruleUnit) {
        return unitExecutor.run(ruleUnit);
    }

    @Override
    public RuleUnitExecutor bind(KieBase kiebase) {
        return unitExecutor.bind(kiebase);
    }

    @Override
    public KieSession getKieSession() {
        return unitExecutor.getKieSession();
    }

    @Override
    public void runUntilHalt(Class<? extends RuleUnit> ruleUnitClass) {
        unitExecutor.runUntilHalt(ruleUnitClass);
    }

    @Override
    public void runUntilHalt(RuleUnit ruleUnit) {
        unitExecutor.runUntilHalt(ruleUnit);
    }

    @Override
    public void halt() {
        unitExecutor.halt();
    }

    @Override
    public <T> DataSource<T> newDataSource(String name, T... items) {
        return unitExecutor.newDataSource(name, items);
    }

    @Override
    public RuleUnitExecutor bindVariable(String name, Object value) {
        return unitExecutor.bindVariable(name, value);
    }

    @Override
    public void dispose() {
        unitExecutor.dispose();
    }

    public InternalUnitExecutorDelegate(UnitExecutor unitExecutor) {
        this.unitExecutor = unitExecutor;
    }

    @Override
    public void onSuspend() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void switchToRuleUnit(Class<? extends RuleUnit> ruleUnitClass, Activation activation) {
        switchToRuleUnit( unitExecutor.ruleUnitFactory.getOrCreateRuleUnit( this, ruleUnitClass ), activation );
    }

    @Override
    public void switchToRuleUnit(RuleUnit ruleUnit, Activation activation) {
        unitExecutor.scheduler.schedule(
                unitExecutor.sessionFactory.create(ruleUnit), activation);
    }

    @Override
    public void guardRuleUnit(Class<? extends RuleUnit> ruleUnitClass, Activation activation) {
        guardRuleUnit( unitExecutor.ruleUnitFactory.getOrCreateRuleUnit( this, ruleUnitClass ), activation );
    }

    @Override
    public void guardRuleUnit(RuleUnit ruleUnit, Activation activation) {
        //System.out.println("REGISTERING GUARD: "+ruleUnit);
        unitExecutor.scheduler.registerGuard(
                unitExecutor.sessionFactory.createGuard(
                        unitExecutor.ruleUnitFactory.registerUnit(this, ruleUnit)),
                activation);
    }


    @Override
    public void cancelActivation(Activation activation) {
        unitExecutor.scheduler.unregisterGuard(activation);
    }

    @Override
    public RuleUnit getCurrentRuleUnit() {
        UnitSession current = unitExecutor.scheduler.current();
        return current == null? null: current.unit();
    }

    @Override
    public KieRuntimeLogger addConsoleLogger() {
        return null;
    }

    @Override
    public KieRuntimeLogger addFileLogger(String fileName) {
        return null;
    }

    @Override
    public KieRuntimeLogger addFileLogger(String fileName, int maxEventsInMemory) {
        return null;
    }

    @Override
    public KieRuntimeLogger addThreadedFileLogger(String fileName, int interval) {
        return null;
    }

    @Override
    public Collection<?> getSessionObjects() {
        return null;
    }

    @Override
    public Collection<?> getSessionObjects(ObjectFilter filter) {
        return null;
    }

    @Override
    public void bindDataSource(InternalDataSource dataSource) {
        unitExecutor.bindDataSource(dataSource);
    }
}
