package org.jbpm.persistence;

import java.util.List;

import org.drools.persistence.map.NonTransactionalPersistentSession;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;

public interface NonTransactionalProcessPersistentSession
    extends
    NonTransactionalPersistentSession {

    List<ProcessInstanceInfo> getStoredProcessInstances();

    void clearStoredProcessInstances();
    
}
