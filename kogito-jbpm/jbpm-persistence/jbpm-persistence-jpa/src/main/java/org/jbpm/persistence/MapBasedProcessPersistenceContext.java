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

package org.jbpm.persistence;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.persistence.map.MapBasedPersistenceContext;
import org.jbpm.persistence.api.PersistentCorrelationKey;
import org.jbpm.persistence.api.PersistentProcessInstance;
import org.jbpm.persistence.api.ProcessPersistenceContext;
import org.kie.internal.process.CorrelationKey;

public class MapBasedProcessPersistenceContext extends MapBasedPersistenceContext
    implements
    ProcessPersistenceContext,
    NonTransactionalProcessPersistentSession{
    
    private ProcessStorage storage;
    private Map<Long, PersistentProcessInstance> processes;
    private Map<PersistentCorrelationKey, PersistentProcessInstance> processInstancesByBusinessKey;

    public MapBasedProcessPersistenceContext(ProcessStorage storage) {
        super( storage );
        this.storage = storage;
        this.processes = new HashMap<Long, PersistentProcessInstance>();
        this.processInstancesByBusinessKey = new HashMap<PersistentCorrelationKey, PersistentProcessInstance>();
    }

    public PersistentProcessInstance persist(PersistentProcessInstance processInstanceInfo) {
        if( processInstanceInfo.getId() == null ) {
            processInstanceInfo.setId( storage.getNextProcessInstanceId() );
        }
        processes.put( processInstanceInfo.getId(), processInstanceInfo );
        return processInstanceInfo;
    }

    public PersistentProcessInstance findProcessInstanceInfo(Long processId) {
    	PersistentProcessInstance processInstanceInfo = processes.get( processId );
        if( processInstanceInfo == null){
            processInstanceInfo = storage.findProcessInstanceInfo( processId );
        }
        return processInstanceInfo;
    }

    public List<PersistentProcessInstance> getStoredProcessInstances() {
        return Collections.unmodifiableList( new ArrayList<PersistentProcessInstance>(processes.values()));
    }

    @Override
    public void close() {
        super.close();
        clearStoredProcessInstances();
    }

    public void remove(PersistentProcessInstance processInstanceInfo) {
        storage.removeProcessInstanceInfo( processInstanceInfo.getId() );
        
    }

    public List<Long> getProcessInstancesWaitingForEvent(String type) {
        return storage.getProcessInstancesWaitingForEvent( type );
    }

    public void clearStoredProcessInstances() {
        processes.clear();
    }

    @Override
    public PersistentCorrelationKey persist(PersistentCorrelationKey correlationKeyInfo) {
        PersistentProcessInstance piInfo = this.processes.get(correlationKeyInfo.getProcessInstanceId());
        if (piInfo != null) {
            this.processInstancesByBusinessKey.put(correlationKeyInfo, piInfo);
        }
        return correlationKeyInfo;
    }

    @Override
    public Long getProcessInstanceByCorrelationKey(CorrelationKey correlationKey) {
    	PersistentProcessInstance piInfo = this.processInstancesByBusinessKey.get(correlationKey);
        return piInfo.getId();
    }
}
