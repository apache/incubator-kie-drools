package org.drools.runtime.process;

import java.util.Collection;
import java.util.Map;

import org.drools.event.process.ProcessEventManager;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.WorkItemManager;

public interface ProcessRuntime extends ProcessEventManager {

    ProcessInstance startProcess(String processId);
    
    ProcessInstance startProcess(String processId, Map<String, Object> parameters);
    
    void signalEvent(String type, Object event);
    
    Collection<ProcessInstance> getProcessInstances();

    ProcessInstance getProcessInstance(long id);

    WorkItemManager getWorkItemManager();
    
}
