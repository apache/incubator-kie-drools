/*
 * Copyright 2011 Red Hat Inc.
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
package org.drools.persistence.map.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.persistence.info.SessionInfo;
import org.drools.persistence.info.WorkItemInfo;
import org.drools.persistence.map.EnvironmentBuilder;
import org.drools.persistence.map.KnowledgeSessionStorage;
import org.drools.persistence.map.KnowledgeSessionStorageEnvironmentBuilder;
import org.junit.Before;
import org.kie.api.KieServices;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class MapBasedPersistenceTest extends MapPersistenceTest{
    
    private SimpleKnowledgeSessionStorage storage;
    
    @Before
    public void createStorage(){
        storage = new SimpleKnowledgeSessionStorage();
    }
    
    @Override
    protected StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
        
        EnvironmentBuilder envBuilder = new KnowledgeSessionStorageEnvironmentBuilder( storage );
        Environment env = KieServices.Factory.get().newEnvironment();
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 envBuilder.getTransactionManager() );
        env.set( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER,
                 envBuilder.getPersistenceContextManager() );

        return JPAKnowledgeService.newStatefulKnowledgeSession( kbase,
                                                                null,
                                                                env );
    }
    
    @Override
    protected StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession ksession,
                                                             KnowledgeBase kbase) {
        long sessionId = ksession.getIdentifier();
        ksession.dispose();
        EnvironmentBuilder envBuilder = new KnowledgeSessionStorageEnvironmentBuilder( storage );
        Environment env = KieServices.Factory.get().newEnvironment();
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 envBuilder.getTransactionManager() );
        env.set( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER,
                 envBuilder.getPersistenceContextManager() );
        
        return JPAKnowledgeService.loadStatefulKnowledgeSession( sessionId, kbase, null, env );
    }
    
    @Override
    protected long getSavedSessionsCount() {
        return storage.ksessions.size();
    }
    
    private static class SimpleKnowledgeSessionStorage
        implements
        KnowledgeSessionStorage {

        public Map<Long, SessionInfo>  ksessions = new HashMap<Long, SessionInfo>();
        public Map<Long, WorkItemInfo> workItems = new HashMap<Long, WorkItemInfo>();

        public SessionInfo findSessionInfo(Long id) {
            return ksessions.get( id );
        }

        public void saveOrUpdate(SessionInfo storedObject) {
            ksessions.put( storedObject.getId(),
                           storedObject );
        }

        public void saveOrUpdate(WorkItemInfo workItemInfo) {
            workItems.put( workItemInfo.getId(),
                           workItemInfo );
        }

        public Long getNextWorkItemId() {
            return new Long( workItems.size() + 1 );
        }

        public WorkItemInfo findWorkItemInfo(Long id) {
            return workItems.get( id );
        }

        public void remove(WorkItemInfo workItemInfo) {
            workItems.remove( workItemInfo.getId() );
        }

        public Long getNextStatefulKnowledgeSessionId() {
            return new Long(ksessions.size() + 1 );
        }

        public void lock(SessionInfo sessionInfo) {
            throw new UnsupportedOperationException("Map based persistence does not support locking.");
        }

        @Override
        public void lock(WorkItemInfo workItemInfo) {
            throw new UnsupportedOperationException("Map based persistence does not support locking.");
        }
    }
}
