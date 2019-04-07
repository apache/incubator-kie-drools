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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.kie.internal.process.CorrelationKey;
import org.kie.api.runtime.process.ProcessInstance;
import org.jbpm.process.instance.ProcessInstanceManager;

public class DefaultProcessInstanceManager implements ProcessInstanceManager {

    private Map<Long, ProcessInstance> processInstances = new ConcurrentHashMap<Long, ProcessInstance>();
    private Map<CorrelationKey, ProcessInstance> processInstancesByCorrelationKey = new ConcurrentHashMap<CorrelationKey, ProcessInstance>();
    private AtomicLong processCounter = new AtomicLong(0);

    public void addProcessInstance(ProcessInstance processInstance, CorrelationKey correlationKey) {
        ((org.jbpm.process.instance.ProcessInstance) processInstance).setId(processCounter.incrementAndGet());
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

    public ProcessInstance getProcessInstance(long id) {
        return (ProcessInstance) processInstances.get(id);
    }

    public ProcessInstance getProcessInstance(long id, boolean readOnly) {
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
    
    public void setProcessCounter(AtomicLong processCounter) {
        this.processCounter = processCounter;
    }
}
