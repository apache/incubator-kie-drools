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

package org.drools.core.impl;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.DynamicEntryPoint;
import org.drools.core.common.InternalAgenda;
import org.drools.core.datasources.CursoredDataSource;
import org.drools.core.datasources.InternalDataSource;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.rule.EntryPointId;
import org.drools.core.ruleunit.RuleUnitDescr;
import org.drools.core.ruleunit.RuleUnitFactory;
import org.drools.core.ruleunit.RuleUnitGuardSystem;
import org.drools.core.ruleunit.RuleUnitsNodeMemories;
import org.drools.core.spi.Activation;
import org.drools.core.spi.FactHandleFactory;
import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;

public class RuleUnitExecutorSession implements InternalRuleUnitExecutor {

    private final StatefulKnowledgeSessionImpl session;

    private RuleUnitGuardSystem ruleUnitGuardSystem;

    private RuleUnitFactory ruleUnitFactory;
    private RuleUnit currentRuleUnit;

    private AtomicBoolean suspended = new AtomicBoolean( false );

    public RuleUnitExecutorSession() {
        session = new StatefulKnowledgeSessionImpl();
        initSession();
        session.agendaEventSupport = new AgendaEventSupport();
        session.ruleRuntimeEventSupport = new RuleRuntimeEventSupport();
        session.ruleEventListenerSupport = new RuleEventListenerSupport();
    }

    public RuleUnitExecutorSession( final long id,
                                    boolean initInitFactHandle,
                                    final SessionConfiguration config,
                                    final Environment environment ) {
        session = new StatefulKnowledgeSessionImpl(id, null, initInitFactHandle, config, environment);
        initSession();
    }

    public RuleUnitExecutorSession( final long id,
                                    final FactHandleFactory handleFactory,
                                    final long propagationContext,
                                    final SessionConfiguration config,
                                    final InternalAgenda agenda,
                                    final Environment environment ) {
        session = new StatefulKnowledgeSessionImpl(id, null, handleFactory, propagationContext, config, agenda, environment);
        initSession();
    }

    private void initSession() {
        session.init();
        session.ruleUnitExecutor = this;
    }

    @Override
    public RuleUnitExecutor bind( KieBase kiebase ) {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) kiebase;
        if (!kbase.hasUnits()) {
            throw new IllegalStateException( "Cannot create a RuleUnitExecutor against a KieBase without units" );
        }

        session.handleFactory = kbase.newFactHandleFactory();
        session.bindRuleBase( kbase, null, false, false );
        session.nodeMemories = new RuleUnitsNodeMemories(kbase);

        DynamicEntryPoint defaultEp = new DynamicEntryPoint( EntryPointId.DEFAULT, session );
        session.defaultEntryPoint = defaultEp;
        defaultEp.bindRuleBase( kbase );

        this.ruleUnitGuardSystem = new RuleUnitGuardSystem( this );
        return this;
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

    public int run( Class<? extends RuleUnit> ruleUnitClass ) {
        return internalRun( getRuleUnitFactory().getOrCreateRuleUnit( this, ruleUnitClass ) );
    }

    public int run( RuleUnit ruleUnit ) {
        return internalRun( getRuleUnitFactory().injectUnitVariables( this, ruleUnit ) );
    }

    private int internalRun( RuleUnit ruleUnit ) {
        return internalExecuteUnit( ruleUnit ) + ruleUnitGuardSystem.fireActiveUnits(ruleUnit);
    }

    public int internalExecuteUnit( RuleUnit ruleUnit ) {
        RuleUnitDescr ruDescr = bindRuleUnit( ruleUnit );
        try {
            return session.fireAllRules();
        } finally {
            unbindRuleUnit(ruDescr);
        }
    }

    public void runUntilHalt( Class<? extends RuleUnit> ruleUnitClass ) {
        runUntilHalt( getRuleUnitFactory().getOrCreateRuleUnit( this, ruleUnitClass ) );
    }

    public void runUntilHalt( RuleUnit ruleUnit ) {
        bindRuleUnit( ruleUnit );
        session.fireUntilHalt();
    }

    @Override
    public void halt() {
        session.halt();
        unbindRuleUnit(session.kBase.getRuleUnitRegistry().getRuleUnitDescr( currentRuleUnit ));
    }

    @Override
    public RuleUnitDescr switchToRuleUnit( Class<? extends RuleUnit> ruleUnitClass ) {
        return switchToRuleUnit( getRuleUnitFactory().getOrCreateRuleUnit( this, ruleUnitClass ) );
    }

    @Override
    public RuleUnitDescr switchToRuleUnit( RuleUnit ruleUnit ) {
        if (currentRuleUnit != null) {
            currentRuleUnit.onYield( ruleUnit );
        }
        session.getPropagationList().flush();
        return bindRuleUnit( ruleUnit );
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

    private RuleUnitDescr bindRuleUnit( RuleUnit ruleUnit ) {
        suspended.set( false );
        currentRuleUnit = ruleUnit;
        currentRuleUnit.onStart();

        getNodeMemories().bindRuleUnit( session, ruleUnit );

        RuleUnitDescr ruDescr = session.kBase.getRuleUnitRegistry().getRuleUnitDescr( ruleUnit );
        ruDescr.bindDataSources( session, ruleUnit );
        ( (Globals) session.getGlobalResolver() ).setDelegate( new RuleUnitGlobals( ruDescr, ruleUnit ) );
        return ruDescr;
    }

    private void unbindRuleUnit( RuleUnitDescr ruDescr ) {
        getNodeMemories().unbindRuleUnit();

        ruDescr.unbindDataSources( session, currentRuleUnit );
        ( (Globals) session.getGlobalResolver() ).setDelegate( null );
        currentRuleUnit.onEnd();
        currentRuleUnit = null;
        suspended.set( true );
    }

    private RuleUnitsNodeMemories getNodeMemories() {
        return ( (RuleUnitsNodeMemories) session.nodeMemories );
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
        if (value instanceof InternalDataSource) {
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
        private final RuleUnitDescr ruDescr;
        private final RuleUnit ruleUnit;

        private RuleUnitGlobals( RuleUnitDescr ruDescr, RuleUnit ruleUnit ) {
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
