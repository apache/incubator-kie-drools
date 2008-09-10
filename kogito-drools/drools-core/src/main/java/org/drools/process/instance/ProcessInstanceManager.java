package org.drools.process.instance;

import java.util.Collection;

public interface ProcessInstanceManager {

    ProcessInstance getProcessInstance(long id);
    
    Collection<ProcessInstance> getProcessInstances();

    void addProcessInstance(ProcessInstance processInstance);
    
    void internalAddProcessInstance(ProcessInstance processInstance);

    void removeProcessInstance(ProcessInstance processInstance);

    void internalRemoveProcessInstance(ProcessInstance processInstance);

}
