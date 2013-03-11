package org.drools.persistence;

import javax.transaction.TransactionSynchronizationRegistry;

import org.drools.persistence.jta.JtaTransactionSynchronizationAdapter;

public class TransactionSynchronizationRegistryHelper {
    public static void registerTransactionSynchronization(final Object tsro, final TransactionSynchronization ts) {
        TransactionSynchronizationRegistry tsr = ( TransactionSynchronizationRegistry ) tsro;
        tsr.registerInterposedSynchronization( new JtaTransactionSynchronizationAdapter( ts ) );
    }
}
