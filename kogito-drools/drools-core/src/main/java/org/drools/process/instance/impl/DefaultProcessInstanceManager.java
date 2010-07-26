/**
 * Copyright 2010 JBoss Inc
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

package org.drools.process.instance.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.ProcessInstanceManager;

public class DefaultProcessInstanceManager implements ProcessInstanceManager {

    private Map<Long, ProcessInstance> processInstances = new HashMap<Long, ProcessInstance>();
    private int processCounter = 0;

    public void addProcessInstance(ProcessInstance processInstance) {
        ((ProcessInstance)processInstance).setId(++processCounter);
        internalAddProcessInstance(processInstance);
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

    public void removeProcessInstance(ProcessInstance processInstance) {
        internalRemoveProcessInstance(processInstance);
    }

    public void internalRemoveProcessInstance(ProcessInstance processInstance) {
        processInstances.remove(((ProcessInstance)processInstance).getId());
    }
}
