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
