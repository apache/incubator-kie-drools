package org.drools.runtime.process;

import java.util.Collection;
import java.util.Map;

public interface ProcessRuntime {

    ProcessInstance startProcess(String processId);

    ProcessInstance startProcess(String processId,
                                 Map<String, Object> parameters);

    void signalEvent(String type,
                     Object event);

    Collection<ProcessInstance> getProcessInstances();

    ProcessInstance getProcessInstance(long id);
    
    void abortProcessInstance(long id);

    WorkItemManager getWorkItemManager();

}
