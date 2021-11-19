/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.QueryResultsImpl;
import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.CalendarsImpl;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.base.NonCloningQueryViewListener;
import org.drools.core.base.QueryRowWithSubruleIndex;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.ConcurrentNodeMemories;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.PhreakPropagationContext;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.GlobalResolver;
import org.drools.core.spi.PropagationContext;
import org.drools.core.time.TimerService;
import org.drools.core.time.TimerServiceFactory;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionClock;

import static org.drools.core.base.ClassObjectType.InitialFact_ObjectType;

public class RuleUnitExecutorImpl implements ReteEvaluator {

    private final RuleRuntimeEventSupport ruleRuntimeEventSupport = new RuleRuntimeEventSupport();
    private final AtomicLong propagationIdCounter = new AtomicLong(1);

    private final InternalKnowledgeBase kBase;
    private final SessionConfiguration sessionConfiguration;
    private final FactHandleFactory handleFactory;

    private final NodeMemories nodeMemories;

    private final ActivationsManager activationsManager;
    private final EntryPointsManager entryPointsManager;

    private final RuleEventListenerSupport ruleEventListenerSupport = new RuleEventListenerSupport();

    private final GlobalResolver globalResolver = new MapGlobalResolver();

    private final TimerService timerService;

    private Calendars calendars;

    public RuleUnitExecutorImpl(InternalKnowledgeBase knowledgeBase) {
        this(knowledgeBase, knowledgeBase.getSessionConfiguration());
    }

    public RuleUnitExecutorImpl(InternalKnowledgeBase knowledgeBase, SessionConfiguration sessionConfiguration) {
        this.kBase = knowledgeBase;
        this.sessionConfiguration = sessionConfiguration;

        this.handleFactory = knowledgeBase.newFactHandleFactory();
        this.nodeMemories = new ConcurrentNodeMemories(kBase);

        this.activationsManager = new ActivationsManagerImpl(this);
        this.entryPointsManager = new EntryPointsManager(this);
        this.timerService = TimerServiceFactory.getTimerService( sessionConfiguration );

        initInitialFact(kBase);
    }

    private void initInitialFact(InternalKnowledgeBase kBase) {
        WorkingMemoryEntryPoint defaultEntryPoint = entryPointsManager.getDefaultEntryPoint();
        InternalFactHandle handle = getFactHandleFactory().newInitialFactHandle(defaultEntryPoint);

        ObjectTypeNode otn = defaultEntryPoint.getEntryPointNode().getObjectTypeNodes().get( InitialFact_ObjectType );
        if (otn != null) {
            PropagationContextFactory ctxFact = RuntimeComponentFactory.get().getPropagationContextFactory();
            PropagationContext pctx = ctxFact.createPropagationContext( 0, PropagationContext.Type.INSERTION, null,
                    null, handle, defaultEntryPoint.getEntryPoint(), null );
            otn.assertInitialFact( handle, pctx, this );
        }
    }

    @Override
    public ActivationsManager getActivationsManager() {
        return activationsManager;
    }

    @Override
    public InternalKnowledgeBase getKnowledgeBase() {
        return kBase;
    }

    @Override
    public Collection<? extends EntryPoint> getEntryPoints() {
        return this.entryPointsManager.getEntryPoints();
    }

    @Override
    public WorkingMemoryEntryPoint getEntryPoint(String name) {
        return entryPointsManager.getEntryPoint(name);
    }

    @Override
    public <T extends Memory> T getNodeMemory(MemoryFactory<T> node) {
        return nodeMemories.getNodeMemory( node, this );
    }

    @Override
    public GlobalResolver getGlobalResolver() {
        return globalResolver;
    }

    @Override
    public FactHandleFactory getFactHandleFactory() {
        return handleFactory;
    }

    @Override
    public InternalFactHandle getFactHandle(Object object) {
        return (InternalFactHandle) this.entryPointsManager.getDefaultEntryPoint().getFactHandle(object);
    }

    @Override
    public TimerService getTimerService() {
        return timerService;
    }

    @Override
    public void addPropagation(PropagationEntry propagationEntry, boolean register) {
        try {
            if (register) {
                startOperation();
            }
            activationsManager.addPropagation( propagationEntry );
        } finally {
            if (register) {
                endOperation();
            }
        }
    }

    @Override
    public long getNextPropagationIdCounter() {
        return this.propagationIdCounter.incrementAndGet();
    }

    @Override
    public SessionConfiguration getSessionConfiguration() {
        return sessionConfiguration;
    }

    @Override
    public RuleEventListenerSupport getRuleEventSupport() {
        return ruleEventListenerSupport;
    }

    @Override
    public RuleRuntimeEventSupport getRuleRuntimeEventSupport() {
        return ruleRuntimeEventSupport;
    }

    @Override
    public Calendars getCalendars() {
        if (this.calendars == null) {
            this.calendars = new CalendarsImpl();
        }
        return this.calendars;
    }

    @Override
    public SessionClock getSessionClock() {
        return (SessionClock) this.timerService;
    }

    @Override
    public int fireAllRules() {
        return fireAllRules( null, -1 );
    }

    @Override
    public int fireAllRules(int fireLimit) {
        return fireAllRules( null, fireLimit );
    }

    @Override
    public int fireAllRules(final AgendaFilter agendaFilter) {
        return fireAllRules( agendaFilter, -1 );
    }

    @Override
    public int fireAllRules(final AgendaFilter agendaFilter, int fireLimit) {
        try {
            startOperation();
            return this.activationsManager.fireAllRules( agendaFilter, fireLimit );
        } finally {
            endOperation();
        }
    }

    @Override
    public FactHandle insert(Object object) {
        return getDefaultEntryPoint().insert(object);
    }

    @Override
    public void dispose() {
        for (WorkingMemoryEntryPoint ep : this.entryPointsManager.getEntryPoints()) {
            ep.dispose();
        }

        this.ruleRuntimeEventSupport.clear();
        this.ruleEventListenerSupport.clear();

        this.timerService.shutdown();
    }

    @Override
    public QueryResults getQueryResults(String queryName, Object... arguments) {
        activationsManager.flushPropagations();

        DroolsQuery queryObject = new DroolsQuery( queryName, arguments, new NonCloningQueryViewListener(), false );

        InternalFactHandle handle = this.handleFactory.newFactHandle( queryObject, null, this, getDefaultEntryPoint() );

        final PropagationContext pCtx = new PhreakPropagationContext(getNextPropagationIdCounter(), PropagationContext.Type.INSERTION, null, null, handle, getDefaultEntryPointId());

        PropagationEntry.ExecuteQuery executeQuery = new PropagationEntry.ExecuteQuery( queryName, queryObject, handle, pCtx, false);
        addPropagation( executeQuery );
        TerminalNode[] terminalNodes = executeQuery.getResult();

        List<Map<String, Declaration>> decls = new ArrayList<>();
        if ( terminalNodes != null ) {
            for ( TerminalNode node : terminalNodes ) {
                decls.add( node.getSubRule().getOuterDeclarations() );
            }
        }

        this.handleFactory.destroyFactHandle( handle);

        return new QueryResultsImpl( (List<QueryRowWithSubruleIndex>) queryObject.getQueryResultCollector().getResults(),
                decls.toArray( new Map[decls.size()] ),
                this,
                ( queryObject.getQuery() != null ) ? queryObject.getQuery().getParameters()  : new Declaration[0] );
    }
}
