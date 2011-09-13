package org.jbpm.persistence;

import org.drools.persistence.map.ManualTransactionManager;
import org.jbpm.persistence.processinstance.ProcessInstanceInfo;

public class ManualProcessTransactionManager extends ManualTransactionManager {

    private ProcessStorage storage;
    private NonTransactionalProcessPersistentSession session;

    public ManualProcessTransactionManager(NonTransactionalProcessPersistentSession session,
                                           ProcessStorage storage) {
        super( session,
               storage );
        this.storage = storage;
        this.session = session;
    }
    
    @Override
    public void commit(boolean transactionOwner) {
        for ( ProcessInstanceInfo processInstanceInfo : session.getStoredProcessInstances() ) {
            storage.saveOrUpdate( processInstanceInfo );
        }
        session.clearStoredProcessInstances();
        super.commit(transactionOwner);
    }
}
