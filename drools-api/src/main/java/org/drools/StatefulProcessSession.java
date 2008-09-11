package org.drools;

import java.util.Collection;
import java.util.Map;

import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.WorkItemManager;

public interface StatefulProcessSession {
	
    ProcessInstance startProcess(String processId);
    
    ProcessInstance startProcess(String processId, Map<String, Object> parameters);
    
    Collection<ProcessInstance> getProcessInstances();

    ProcessInstance getProcessInstance(long id);

    WorkItemManager getWorkItemManager();
    
}
