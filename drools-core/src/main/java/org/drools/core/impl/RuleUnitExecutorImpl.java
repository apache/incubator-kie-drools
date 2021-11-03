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

import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.ActivationsManager;
import org.drools.core.common.ConcurrentNodeMemories;
import org.drools.core.common.FactHandleClassStore;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.GlobalResolver;
import org.drools.core.time.TimerService;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionClock;

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

    public RuleUnitExecutorImpl(InternalKnowledgeBase knowledgeBase) {
        this.kBase = knowledgeBase;

        this.sessionConfiguration = knowledgeBase.getSessionConfiguration();
        this.handleFactory = knowledgeBase.newFactHandleFactory();
        this.nodeMemories = new ConcurrentNodeMemories(kBase);

        this.activationsManager = new ActivationsManagerImpl(this);
        this.entryPointsManager = new EntryPointsManager(this);
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
        throw new UnsupportedOperationException();
    }

    @Override
    public FactHandleFactory getFactHandleFactory() {
        return handleFactory;
    }

    @Override
    public InternalFactHandle getFactHandle(Object object) {
        throw new UnsupportedOperationException();
    }

    @Override
    public TimerService getTimerService() {
        throw new UnsupportedOperationException();
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
    public FactHandleClassStore getStoreForClass(Class<?> classType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String entryPointId) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    @Override
    public SessionClock getSessionClock() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        throw new UnsupportedOperationException();
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
    public void fireUntilHalt() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void fireUntilHalt(AgendaFilter agendaFilter) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void halt() {
        throw new UnsupportedOperationException();
    }

    @Override
    public FactHandle insert(Object object) {
        return getDefaultEntryPoint().insert(object);
    }
}
