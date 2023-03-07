/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reliability;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.SessionConfiguration;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.common.AgendaFactory;
import org.drools.core.common.EntryPointFactory;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ObjectStore;
import org.drools.core.impl.RuleBase;
import org.drools.core.phreak.PropagationEntry;
import org.drools.kiesession.factory.RuntimeComponentFactoryImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.conf.PersistedSessionOption;
import org.kie.api.runtime.rule.EntryPoint;

public class ReliableRuntimeComponentFactoryImpl extends RuntimeComponentFactoryImpl {

    private static final AtomicLong RELIABLE_SESSIONS_COUNTER = new AtomicLong(0);

    private final AgendaFactory agendaFactory = ReliableAgendaFactory.getInstance();

    @Override
    public EntryPointFactory getEntryPointFactory() {
        return new ReliableNamedEntryPointFactory();
    }

    @Override
    public InternalWorkingMemory createStatefulSession(RuleBase ruleBase, Environment environment, SessionConfiguration sessionConfig, boolean fromPool) {
        if (!sessionConfig.hasPersistedSessionOption()) {
            return super.createStatefulSession(ruleBase, environment, sessionConfig, fromPool);
        }

        InternalKnowledgeBase kbase = (InternalKnowledgeBase) ruleBase;
        if (fromPool || kbase.getSessionPool() == null) {
            InternalWorkingMemory session = getWorkingMemoryFactory().createWorkingMemory(RELIABLE_SESSIONS_COUNTER.getAndIncrement(), kbase, sessionConfig, environment);
            return internalInitSession(kbase, sessionConfig, session);
        }
        return (InternalWorkingMemory) kbase.getSessionPool().newKieSession(sessionConfig);
    }

    private InternalWorkingMemory internalInitSession(InternalKnowledgeBase kbase, SessionConfiguration sessionConfig, InternalWorkingMemory session) {
        if (sessionConfig.isKeepReference()) {
            kbase.addStatefulSession(session);
        }

        if (sessionConfig.getPersistedSessionOption().getStrategy() == PersistedSessionOption.Strategy.STORES_ONLY) {
            session.setWorkingMemoryActionListener(entry -> onWorkingMemoryAction(session, entry));

            // re-propagate objects from the cache to the new session
            populateSessionFromCache(session);
        }
        return session;
    }

    private void onWorkingMemoryAction(InternalWorkingMemory session, PropagationEntry entry) {
        if (entry instanceof PropagationEntry.Insert) {
            InternalFactHandle fh = ((PropagationEntry.Insert) entry).getHandle();
            WorkingMemoryEntryPoint ep = fh.getEntryPoint(session);
            ep.getObjectStore().updateHandle(fh, fh.getObject());
        }
    }

    private void populateSessionFromCache(InternalWorkingMemory session) {
        session.getEntryPoints().forEach( ep -> populateEntryPointFromCache(session, ep));
    }

    private void populateEntryPointFromCache(InternalWorkingMemory session, EntryPoint ep) {
        ObjectStore objectStore = ((WorkingMemoryEntryPoint) ep).getObjectStore();
        Map<Boolean, List<Object>> map = ((SimpleReliableObjectStore) objectStore).takeObjectsGroupedByPropagation();

        // fact handles with a match have been already propagated in the original session, so they shouldn't fire
        map.get(true).forEach(ep::insert);
        session.fireAllRules(match -> false);

        // fact handles without any match have never been propagated in the original session, so they should fire
        map.get(false).forEach(ep::insert);
    }

    public AgendaFactory getAgendaFactory(SessionConfiguration sessionConfig) {
        if (!sessionConfig.hasPersistedSessionOption() || sessionConfig.getPersistedSessionOption().getStrategy() == PersistedSessionOption.Strategy.STORES_ONLY) {
            return super.getAgendaFactory(sessionConfig);
        }
        return agendaFactory;
    }

    // test purpose to simulate fail-over
    static void resetCounter() {
        RELIABLE_SESSIONS_COUNTER.set(0);
    }

    @Override
    public int servicePriority() {
        return 1;
    }
}
