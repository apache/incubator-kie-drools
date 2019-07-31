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

package org.jbpm.process.instance.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.jbpm.process.instance.ProcessInstanceManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.process.CorrelationKey;

public class DefaultProcessInstanceManager implements ProcessInstanceManager {

    private Map<String, ProcessInstance> processInstances = new ConcurrentHashMap<String, ProcessInstance>();
    private Map<CorrelationKey, ProcessInstance> processInstancesByCorrelationKey = new ConcurrentHashMap<CorrelationKey, ProcessInstance>();
    

    public void addProcessInstance(ProcessInstance processInstance, CorrelationKey correlationKey) {
        ((org.jbpm.process.instance.ProcessInstance) processInstance).setId(UUID.randomUUID().toString());
        internalAddProcessInstance(processInstance);
 
        if (correlationKey != null) {  
            if (processInstancesByCorrelationKey.containsKey(correlationKey)) {
                throw new RuntimeException(correlationKey + " already exists");
            }
            processInstancesByCorrelationKey.put(correlationKey, processInstance);
        }
    }
    
    public void internalAddProcessInstance(ProcessInstance processInstance) {
    	processInstances.put(((ProcessInstance)processInstance).getId(), processInstance);
    }

    public Collection<ProcessInstance> getProcessInstances() {
        return Collections.unmodifiableCollection(processInstances.values());
    }

    public ProcessInstance getProcessInstance(String id) {
        return (ProcessInstance) processInstances.get(id);
    }

    public ProcessInstance getProcessInstance(String id, boolean readOnly) {
        return (ProcessInstance) processInstances.get(id);
    }

    public void removeProcessInstance(ProcessInstance processInstance) {
        internalRemoveProcessInstance(processInstance);
    }

    public void internalRemoveProcessInstance(ProcessInstance processInstance) {
        processInstances.remove(((ProcessInstance)processInstance).getId());
        for (Entry<CorrelationKey, ProcessInstance> entry : processInstancesByCorrelationKey.entrySet()) {
            if (entry.getValue().getId() == processInstance.getId()) {
                processInstancesByCorrelationKey.remove(entry.getKey());
            }
        }
    }
    
    public void clearProcessInstances() {
    	processInstances.clear();
    }

    public void clearProcessInstancesState() {
        
    }

    @Override
    public ProcessInstance getProcessInstance(CorrelationKey correlationKey) {
        return processInstancesByCorrelationKey.get(correlationKey);
    }
}
