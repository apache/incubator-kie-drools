package org.drools.persistence.session;

import javax.transaction.TransactionSynchronizationRegistry;

public class TransactionSynchronizationRegistryHelper {
    public static void registerTransactionSynchronization(final Object tsro, final TransactionSynchronization ts) {
        TransactionSynchronizationRegistry tsr = ( TransactionSynchronizationRegistry ) tsro;
        tsr.registerInterposedSynchronization( new JtaTransactionSynchronizationAdapter( ts ) );
    }
}
