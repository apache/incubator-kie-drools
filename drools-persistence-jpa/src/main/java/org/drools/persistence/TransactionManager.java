package org.drools.persistence;


public interface TransactionManager {
    /** Completion status in case of proper commit */
    int STATUS_COMMITTED      = 0;

    /** Completion status in case of proper rollback */
    int STATUS_ROLLEDBACK     = 1;

    /** Completion status in case of heuristic mixed completion or system errors */
    int STATUS_UNKNOWN        = 2;

    /** No existing transaction is associated with this threat */
    int STATUS_NO_TRANSACTION = 3;

    /** Transaction is Active */
    int STATUS_ACTIVE         = 4;

    int getStatus();

    void begin();

    void commit();

    void rollback();

    void registerTransactionSynchronization(TransactionSynchronization ts);

}
