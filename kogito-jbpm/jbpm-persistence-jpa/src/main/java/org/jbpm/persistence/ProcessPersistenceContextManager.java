package org.jbpm.persistence;

import org.drools.persistence.PersistenceContextManager;

public interface ProcessPersistenceContextManager
    extends
    PersistenceContextManager {

    ProcessPersistenceContext getProcessPersistenceContext();
    
}
