/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.persistence.map.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.persistence.api.PersistentSession;
import org.drools.persistence.api.PersistentWorkItem;
import org.drools.persistence.map.EnvironmentBuilder;
import org.jbpm.persistence.ProcessStorage;
import org.jbpm.persistence.ProcessStorageEnvironmentBuilder;
import org.jbpm.persistence.api.PersistentProcessInstance;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;
import org.junit.Before;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.internal.persistence.jpa.JPAKnowledgeService;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class MapBasedPersistenceTest extends MapPersistenceTest {
    
    private SimpleProcessStorage storage;
    
    @Before
    public void createStorage(){
        storage = new SimpleProcessStorage();
    }
    
    @Override
    protected StatefulKnowledgeSession createSession(KieBase kbase) {
        
        EnvironmentBuilder envBuilder = new ProcessStorageEnvironmentBuilder( storage );
        Environment env = KieServices.get().newEnvironment();
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 envBuilder.getTransactionManager() );
        env.set( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER,
                 envBuilder.getPersistenceContextManager() );

        return JPAKnowledgeService.newStatefulKnowledgeSession( kbase,
                                                                null,
                                                                env );
    }
    
    @Override
    protected StatefulKnowledgeSession disposeAndReloadSession(StatefulKnowledgeSession ksession, long ksessionId,
                                                             KieBase kbase) {
        ksession.dispose();
        EnvironmentBuilder envBuilder = new ProcessStorageEnvironmentBuilder( storage );
        Environment env = KieServices.get().newEnvironment();
        env.set( EnvironmentName.TRANSACTION_MANAGER,
                 envBuilder.getTransactionManager() );
        env.set( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER,
                 envBuilder.getPersistenceContextManager() );
        
        return JPAKnowledgeService.loadStatefulKnowledgeSession( ksessionId, kbase, null, env );
    }
    
    @Override
    protected int getProcessInstancesCount() {
        return storage.processes.size();
    }

    @Override
    protected int getKnowledgeSessionsCount() {
        return storage.ksessions.size();
    }
    
    private static class SimpleProcessStorage
        implements
        ProcessStorage {
        private Map<Long, PersistentSession>      ksessions = new HashMap<Long, PersistentSession>();
        private Map<Long, PersistentProcessInstance> processes = new HashMap<Long, PersistentProcessInstance>();
        private Map<Long, PersistentWorkItem>        workItems = new HashMap<Long, PersistentWorkItem>();

        public void saveOrUpdate(PersistentSession ksessionInfo) {
            ksessionInfo.transform();
            ksessions.put( ksessionInfo.getId(),
                           ksessionInfo );
        }

        public PersistentSession findSessionInfo(Long id) {
            return ksessions.get( id );
        }

        public PersistentProcessInstance findProcessInstanceInfo(Long processInstanceId) {
        	PersistentProcessInstance processInstanceInfo = processes.get( processInstanceId );
            if(processInstanceInfo != null) {
                //FIXME need a way to clone a processInstance before saving
                ((ProcessInstanceInfo)processInstanceInfo).clearProcessInstance();
            }
            return processInstanceInfo;
        }

        public void saveOrUpdate(PersistentProcessInstance processInstanceInfo) {
            processInstanceInfo.transform();
            processes.put( processInstanceInfo.getId(),
                           processInstanceInfo );
        }

        public long getNextProcessInstanceId() {
            return processes.size() + 1;
        }

        public void removeProcessInstanceInfo(Long id) {
            processes.remove( id );
        }

        public List<Long> getProcessInstancesWaitingForEvent(String type) {
            List<Long> processInstancesWaitingForEvent = new ArrayList<Long>();
            for ( PersistentProcessInstance processInstanceInfo : processes.values() ) {
                if ( ((ProcessInstanceInfo) processInstanceInfo).getEventTypes().contains( type ) ) 
                	processInstancesWaitingForEvent.add( processInstanceInfo.getId() );
            }
            return processInstancesWaitingForEvent;
        }

        public void saveOrUpdate(PersistentWorkItem workItem) {
            workItems.put( workItem.getId(),
                           workItem );
        }

        public Long getNextWorkItemId() {
            return new Long( workItems.size() + 1 );
        }

        public PersistentWorkItem findWorkItemInfo(Long id) {
            return workItems.get( id );
        }

        public void remove(PersistentWorkItem workItem) {
            workItems.remove( workItem.getId() );
        }

        public Long getNextStatefulKnowledgeSessionId() {
            return  new Long(ksessions.size() + 1) ;
        }

        @Override
        public void lock(PersistentSession session) {
            throw new UnsupportedOperationException("Map based persistence does not support locking.");
        }

        @Override
        public void lock(PersistentWorkItem workItem) {
            throw new UnsupportedOperationException("Map based persistence does not support locking.");
        }
    }
}
