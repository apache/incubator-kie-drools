package org.drools.persistence.session;

import javax.transaction.Status;
import javax.transaction.Synchronization;

public class JtaTransactionSynchronizationAdapter
    implements
    Synchronization {
    TransactionSynchronization ts;

    public JtaTransactionSynchronizationAdapter(TransactionSynchronization ts) {
        super();
        this.ts = ts;
    }

    public void afterCompletion(int status) {
        switch ( status ) {
            case Status.STATUS_COMMITTED :
                this.ts.afterCompletion( TransactionManager.STATUS_COMMITTED );
                break;
            case Status.STATUS_ROLLEDBACK :
                this.ts.afterCompletion( TransactionManager.STATUS_ROLLEDBACK );
                break;
            case Status.STATUS_NO_TRANSACTION :
                this.ts.afterCompletion(  TransactionManager.STATUS_NO_TRANSACTION );
                break;
            case Status.STATUS_ACTIVE :
                this.afterCompletion( TransactionManager.STATUS_ACTIVE );
                break;
            default :
                this.ts.afterCompletion( TransactionManager.STATUS_UNKNOWN );
        }
    }

    public void beforeCompletion() {
        this.ts.beforeCompletion();
    }
}
