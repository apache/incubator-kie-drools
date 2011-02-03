package org.jbpm.persistence;

import java.util.List;

import org.drools.persistence.PersistenceContext;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;

public interface ProcessPersistenceContext
    extends
    PersistenceContext {

    void persist(ProcessInstanceInfo processInstanceInfo);
    
    ProcessInstanceInfo findProcessInstanceInfo(Long processId);
    
    void remove(ProcessInstanceInfo processInstanceInfo);

    List<Long> getProcessInstancesWaitingForEvent(String type);
}
