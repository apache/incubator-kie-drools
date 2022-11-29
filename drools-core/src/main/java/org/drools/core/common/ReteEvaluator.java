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

package org.drools.core.common;

import java.util.Collection;
import java.util.Collections;

import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.impl.RuleBase;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.RuntimeComponentFactory;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.core.rule.accessor.GlobalResolver;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.core.time.TimerService;
import org.drools.core.time.impl.TimerJobInstance;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.time.SessionClock;

public interface ReteEvaluator {

    ActivationsManager getActivationsManager();

    RuleBase getKnowledgeBase();

    Collection<? extends EntryPoint> getEntryPoints();

    WorkingMemoryEntryPoint getEntryPoint(String name);
    default EntryPointId getDefaultEntryPointId() {
        return EntryPointId.DEFAULT;
    }
    default WorkingMemoryEntryPoint getDefaultEntryPoint() {
        return getEntryPoint(getDefaultEntryPointId().getEntryPointId());
    }

    <T extends Memory> T getNodeMemory(MemoryFactory<T> node);

    NodeMemories getNodeMemories();

    GlobalResolver getGlobalResolver();
    default Object getGlobal(String identifier) {
        return getGlobalResolver().resolveGlobal( identifier );
    }
    default void setGlobal(String identifier, Object value) {
        getGlobalResolver().setGlobal(identifier, value);
    }

    default InternalFactHandle createFactHandle(Object object, ObjectTypeConf conf, WorkingMemoryEntryPoint wmEntryPoint ) {
        return getFactHandleFactory().newFactHandle( object, conf, this, wmEntryPoint );
    }

    FactHandleFactory getFactHandleFactory();

    InternalFactHandle getFactHandle(Object object);

    TimerService getTimerService();

    default Collection<TimerJobInstance> getTimerJobInstances(long id) {
        TimerService timerService = getTimerService();
        return timerService != null ? timerService.getTimerJobInstances(id) : Collections.emptyList();
    }

    default void addPropagation(PropagationEntry propagationEntry) {
        addPropagation(propagationEntry, false);
    }

    void addPropagation(PropagationEntry propagationEntry, boolean register);

    long getNextPropagationIdCounter();

    default boolean isThreadSafe() {
        return true;
    }

    default FactHandleClassStore getStoreForClass(Class<?> clazz) {
        return getDefaultEntryPoint().getObjectStore().getStoreForClass(clazz);
    }

    SessionConfiguration getSessionConfiguration();

    AgendaEventSupport getAgendaEventSupport();

    RuleEventListenerSupport getRuleEventSupport();

    RuleRuntimeEventSupport getRuleRuntimeEventSupport();

    Calendars getCalendars();

    SessionClock getSessionClock();

    default boolean isSequential() {
        return getKnowledgeBase().getConfiguration().isSequential();
    }

    default void startOperation() { }
    default void endOperation() { }

    default KnowledgeHelper createKnowledgeHelper() {
        return RuntimeComponentFactory.get().createKnowledgeHelper(this);
    }

    FactHandle insert(Object object);

    QueryResults getQueryResults(String queryName, Object... arguments);

    void dispose();

    int fireAllRules();
    int fireAllRules(int max);
    int fireAllRules(AgendaFilter agendaFilter);
    int fireAllRules(AgendaFilter agendaFilter, int max);
}
