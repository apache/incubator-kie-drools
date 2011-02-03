package org.jbpm.persistence;

import java.util.List;

import org.drools.persistence.map.KnowledgeSessionStorage;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;

public interface ProcessStorage
    extends
    KnowledgeSessionStorage {
    
    ProcessInstanceInfo findProcessInstanceInfo(Long processInstanceId);
    
    void saveOrUpdate(ProcessInstanceInfo processInstanceInfo);

    long getNextProcessInstanceId();

    void removeProcessInstanceInfo(Long id);

    List<Long> getProcessInstancesWaitingForEvent(String type);

}
