/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.ruleunit.executor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.core.SessionConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.spi.Activation;
import org.drools.core.spi.FactHandleFactory;
import org.drools.ruleunit.impl.RuleUnitFactory;
import org.drools.ruleunit.impl.RuleUnitGuardSystem;
import org.drools.ruleunit.datasources.CursoredDataSource;
import org.drools.ruleunit.datasources.InternalDataSource;
import org.drools.ruleunit.impl.RuleUnitDescriptionImpl;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.logger.KieRuntimeLogger;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.ruleunit.DataSource;
import org.drools.ruleunit.RuleUnit;
import org.drools.ruleunit.RuleUnitExecutor;

import static org.kie.internal.ruleunit.RuleUnitUtil.RULE_UNIT_ENTRY_POINT;

public class RuleUnitExecutorSession implements InternalRuleUnitExecutor {

    private final RuleUnitSessionImpl session;

    private final Map<Class<?>, FactHandle> factHandlesMap = new HashMap<>();

    private RuleUnitGuardSystem ruleUnitGuardSystem;

    private RuleUnitFactory ruleUnitFactory;
    private RuleUnit currentRuleUnit;
    private RuleUnitDescriptionImpl currentRuDescr;

    private AtomicBoolean suspended = new AtomicBoolean( false );

    private LinkedList<RuleUnit> unitsStack = new LinkedList<>();

    public RuleUnitExecutorSession() {
        session = new RuleUnitSessionImpl( this, new StatefulKnowledgeSessionImpl() );
        initSession(new SessionConfigurationImpl(), EnvironmentFactory.newEnvironment());
        session.initEventSupports();
    }

    public RuleUnitExecutorSession(KieBase kiebase) {
        session = new RuleUnitSessionImpl( this, (StatefulKnowledgeSessionImpl) kiebase.newKieSession() );
        session.ruleUnitExecutor = this;
        this.ruleUnitGuardSystem = new RuleUnitGuardSystem( this );
    }

    public RuleUnitExecutorSession(KieSession session) {
        this.session = new RuleUnitSessionImpl( this, (( StatefulKnowledgeSessionImpl ) session) );
        this.session.ruleUnitExecutor = this;
        bind( session.getKieBase() );
    }

    public RuleUnitExecutorSession( final long id,
                                    boolean initInitFactHandle,
                                    final SessionConfiguration config,
                                    final Environment environment ) {
        session = new RuleUnitSessionImpl( this, new StatefulKnowledgeSessionImpl(id, null, initInitFactHandle, config, environment) );
        initSession(config, environment);
    }

    public RuleUnitExecutorSession( final long id,
                                    final FactHandleFactory handleFactory,
                                    final long propagationContext,
                                    final SessionConfiguration config,
                                    final InternalAgenda agenda,
                                    final Environment environment ) {
        session = new RuleUnitSessionImpl( this, new StatefulKnowledgeSessionImpl(id, null, handleFactory, propagationContext, config, agenda, environment) );
        initSession(config, environment);
    }

    private void initSession(SessionConfiguration config, Environment environment) {
        session.init(config, environment);
        session.ruleUnitExecutor = this;
    }

    @Override
    public RuleUnitExecutor bind( KieBase kiebase ) {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) kiebase;
        if (!kbase.hasUnits()) {
            throw new IllegalStateException( "Cannot create a RuleUnitExecutor against a KieBase without units" );
        }

        session.setHandleFactory( kbase.newFactHandleFactory() );
        session.bindRuleBase( kbase, null, false );

        this.ruleUnitGuardSystem = new RuleUnitGuardSystem( this );
        return this;
    }

    @Override
    public KieSession getKieSession() {
        return session;
    }

    @Override
    public <T> DataSource<T> newDataSource( String name, T... items ) {
        DataSource<T> dataSource = new CursoredDataSource( session );
        for (T item : items) {
            dataSource.insert( item );
        }
        getRuleUnitFactory().bindVariable( name, dataSource );
        return dataSource;
    }
    
    @Override
    public Collection<?> getSessionObjects() {
    	if (session != null) {
    		return session.getObjects();
    	}
    	return Collections.emptyList();
    }
    
    @Override
    public Collection<?> getSessionObjects(ObjectFilter filter) {
    	if (session != null) {
    		return session.getObjects(filter);
    	}
    	return Collections.emptyList();
    }

    @Override
    public KieRuntimeLogger addConsoleLogger() {
        if (this.session != null) {
            return KieServices.Factory.get().getLoggers().newConsoleLogger(session);
        } else {
            throw new IllegalStateException("Cannot add logger to the rule unit when the session is not available");
        }
    }

    @Override
    public KieRuntimeLogger addFileLogger(String fileName) {
        if (this.session != null) {
            return KieServices.Factory.get().getLoggers().newFileLogger(session, fileName);
        } else {
            throw new IllegalStateException("Cannot add logger to the rule unit when the session is not available");
        }
    }

    @Override
    public KieRuntimeLogger addFileLogger(String fileName, int maxEventsInMemory) {
        if (this.session != null) {
            return KieServices.Factory.get().getLoggers().newFileLogger(session, fileName, maxEventsInMemory);
        } else {
            throw new IllegalStateException("Cannot add logger to the rule unit when the session is not available");
        }
    }

    @Override
    public KieRuntimeLogger addThreadedFileLogger(String fileName, int interval) {
        if (this.session != null) {
            return KieServices.Factory.get().getLoggers().newThreadedFileLogger(session, fileName, interval);
        } else {
            throw new IllegalStateException("Cannot add logger to the rule unit when the session is not available");
        }
    }

    public int run( Class<? extends RuleUnit> ruleUnitClass ) {
        return internalRun( getRuleUnitFactory().getOrCreateRuleUnit( this, ruleUnitClass ) );
    }

    public int run( RuleUnit ruleUnit ) {
        return internalRun( getRuleUnitFactory().injectUnitVariables( this, ruleUnit ) );
    }

    private int internalRun( RuleUnit ruleUnit ) {
        int fired = 0;
        for (RuleUnit evaluatedUnit = ruleUnit; evaluatedUnit != null; evaluatedUnit = unitsStack.poll()) {
            fired += internalExecuteUnit( evaluatedUnit ) + ruleUnitGuardSystem.fireActiveUnits( evaluatedUnit );
        }
        return fired;
    }

    public int internalExecuteUnit( RuleUnit ruleUnit ) {
        currentRuDescr = bindRuleUnit( ruleUnit );
        try {
            return session.fireAllRules();
        } finally {
            unbindCurrentRuleUnit();
        }
    }

    public void runUntilHalt( Class<? extends RuleUnit> ruleUnitClass ) {
        runUntilHalt( getRuleUnitFactory().getOrCreateRuleUnit( this, ruleUnitClass ) );
    }

    public void runUntilHalt( RuleUnit ruleUnit ) {
        currentRuDescr = bindRuleUnit( ruleUnit );
        session.fireUntilHalt();
    }

    @Override
    public void halt() {
        session.halt();
        unbindCurrentRuleUnit();
    }

    @Override
    public void switchToRuleUnit( Class<? extends RuleUnit> ruleUnitClass, Activation activation ) {
        switchToRuleUnit( getRuleUnitFactory().getOrCreateRuleUnit( this, ruleUnitClass ), activation );
    }

    @Override
    public void switchToRuleUnit( RuleUnit ruleUnit, Activation activation ) {
        String activateUnitName = activation.getRule().getRuleUnitClassName();
        boolean isActiveUnitCurrent = currentRuleUnit != null && currentRuleUnit.getClass().getName().equals( activateUnitName );

        if ( isActiveUnitCurrent ) {
            currentRuleUnit.onYield( ruleUnit );
            session.getPropagationList().flush();
            InternalAgenda agenda = session.getAgenda();
            agenda.removeAgendaGroup(currentRuleUnit.getClass().getName());

            unitsStack.push( currentRuleUnit );
            currentRuDescr = bindRuleUnit( ruleUnit );
        } else {
            for (int i = 0; i < unitsStack.size(); i++) {
                if (unitsStack.get(i).getClass().getName().equals( activateUnitName )) {
                    unitsStack.add( i, ruleUnit );
                    break;
                }
            }
        }
    }

    @Override
    public void guardRuleUnit( Class<? extends RuleUnit> ruleUnitClass, Activation activation) {
        ruleUnitGuardSystem.registerGuard( getRuleUnitFactory().getOrCreateRuleUnit( this, ruleUnitClass ), activation );
    }

    @Override
    public void guardRuleUnit( RuleUnit ruleUnit, Activation activation ) {
        ruleUnitGuardSystem.registerGuard( getRuleUnitFactory().registerUnit( this, ruleUnit ), activation );
    }

    @Override
    public void cancelActivation( Activation activation ) {
        ruleUnitGuardSystem.removeActivation( activation );
    }

    private RuleUnitDescriptionImpl bindRuleUnit(RuleUnit ruleUnit ) {
        suspended.set( false );
        currentRuleUnit = ruleUnit;
        currentRuleUnit.onStart();

        factHandlesMap.computeIfAbsent( ruleUnit.getClass(), x -> session.getEntryPoint( RULE_UNIT_ENTRY_POINT ).insert( ruleUnit ) );

        RuleUnitDescriptionImpl ruDescr = (RuleUnitDescriptionImpl) session.getKnowledgeBase().getRuleUnitDescriptionRegistry().getDescription(ruleUnit );
        ( (Globals) session.getGlobalResolver() ).setDelegate( new RuleUnitGlobals( ruDescr, ruleUnit ) );
        ruDescr.bindDataSources( session, ruleUnit );

        InternalAgendaGroup unitGroup = (InternalAgendaGroup)session.getAgenda().getAgendaGroup(ruleUnit.getClass().getName());
        unitGroup.setAutoDeactivate( false );
        unitGroup.setFocus();

        return ruDescr;
    }

    private void unbindCurrentRuleUnit() {
        currentRuDescr.unbindDataSources( session, currentRuleUnit );
        ( (Globals) session.getGlobalResolver() ).setDelegate( null );
        currentRuleUnit.onEnd();
        currentRuleUnit = null;
        currentRuDescr = null;
        suspended.set( true );
    }

    public RuleUnit getCurrentRuleUnit() {
        return currentRuleUnit;
    }

    public RuleUnitFactory getRuleUnitFactory() {
        if (ruleUnitFactory == null) {
            ruleUnitFactory = new RuleUnitFactory();
        }
        return ruleUnitFactory;
    }

    @Override
    public RuleUnitExecutor bindVariable( String name, Object value ) {
        getRuleUnitFactory().bindVariable( name, value );
        if (value instanceof InternalDataSource ) {
            bindDataSource( (InternalDataSource) value );
        }
        return this;
    }

    @Override
    public void bindDataSource(InternalDataSource dataSource) {
        dataSource.setWorkingMemory( session );
    }

    @Override
    public void onSuspend() {
        if (!suspended.getAndSet( true )) {
            if ( currentRuleUnit != null ) {
                currentRuleUnit.onSuspend();
            }
        }
    }

    @Override
    public void onResume() {
        if (suspended.getAndSet( false )) {
            if ( currentRuleUnit != null ) {
                currentRuleUnit.onResume();
            }
        }
    }

    @Override
    public void dispose() {
        session.dispose();
        ruleUnitGuardSystem = null;
        ruleUnitFactory = null;
        currentRuleUnit = null;
    }

    public static class RuleUnitGlobals implements Globals {
        private final RuleUnitDescriptionImpl ruDescr;
        private final RuleUnit ruleUnit;

        private RuleUnitGlobals(RuleUnitDescriptionImpl ruDescr, RuleUnit ruleUnit ) {
            this.ruDescr = ruDescr;
            this.ruleUnit = ruleUnit;
        }

        @Override
        public Object get( String identifier ) {
            return ruDescr.getValue( ruleUnit, identifier );
        }

        @Override
        public void set( String identifier, Object value ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDelegate( Globals delegate ) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<String> getGlobalKeys() {
            throw new UnsupportedOperationException();
        }
    }
}
