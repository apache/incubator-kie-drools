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

import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.SessionConfiguration;
import org.drools.core.common.AgendaFactory;
import org.drools.core.common.EntryPointFactory;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.RuleBase;
import org.drools.kiesession.factory.RuntimeComponentFactoryImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.kie.api.runtime.Environment;

public class ReliableRuntimeComponentFactoryImpl extends RuntimeComponentFactoryImpl {

    public static final ReliableRuntimeComponentFactoryImpl DEFAULT = new ReliableRuntimeComponentFactoryImpl();

    private static final AtomicLong RELIABLE_SESSIONS_COUNTER = new AtomicLong(0);

    private final AgendaFactory agendaFactory = ReliableAgendaFactory.getInstance();

    @Override
    public EntryPointFactory getEntryPointFactory() {
        return new ReliableNamedEntryPointFactory();
    }

    @Override
    public InternalWorkingMemory createStatefulSession(RuleBase ruleBase, Environment environment, SessionConfiguration sessionConfig, boolean fromPool) {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) ruleBase;
        if (fromPool || kbase.getSessionPool() == null) {
            StatefulKnowledgeSessionImpl session = (StatefulKnowledgeSessionImpl) getWorkingMemoryFactory()
                    .createWorkingMemory(RELIABLE_SESSIONS_COUNTER.getAndIncrement(), kbase, sessionConfig, environment);
            return internalInitSession(kbase, sessionConfig, session);
        }
        return (InternalWorkingMemory) kbase.getSessionPool().newKieSession(sessionConfig);
    }

    private StatefulKnowledgeSessionImpl internalInitSession(InternalKnowledgeBase kbase, SessionConfiguration sessionConfig, StatefulKnowledgeSessionImpl session) {
        if (sessionConfig.isKeepReference()) {
            kbase.addStatefulSession(session);
        }

        // re-propagate objects from the cache to the new session
//        updateFactsWithCache(session);
        return session;
    }

    private void updateFactsWithCache(StatefulKnowledgeSessionImpl session) {
        session.getFactHandles().forEach(factHandle -> {
            session.update(factHandle, session.getObject(factHandle));
        });
    }

    public AgendaFactory getAgendaFactory() {
        return agendaFactory;
    }

    @Override
    public int servicePriority() {
        return 1;
    }
}
