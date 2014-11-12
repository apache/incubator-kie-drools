package org.drools.persistence;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionSynchronizationContainer implements TransactionSynchronization {

    private static Logger logger = LoggerFactory.getLogger( TransactionSynchronizationContainer.class );
    public static final String RESOURCE_KEY = "org.drools.persistence.txsync.container";

    private Set<TransactionSynchronization> synchronizations = new TreeSet<TransactionSynchronization>();

    @Override
    public void beforeCompletion() {
        TransactionSynchronization[] txSyncArray = synchronizations.toArray(new TransactionSynchronization[synchronizations.size()]);

        for (TransactionSynchronization txSync : txSyncArray) {

            txSync.beforeCompletion();
        }
    }

    @Override
    public void afterCompletion(int status) {
        TransactionSynchronization[] txSyncArray = synchronizations.toArray(new TransactionSynchronization[synchronizations.size()]);

        for (TransactionSynchronization txSync : txSyncArray) {

            txSync.afterCompletion(status);
        }
    }

    public void addTransactionSynchronization(TransactionSynchronization txSync) {

        this.synchronizations.add(txSync);
        logger.debug("Adding sync {} total syncs ", txSync, synchronizations.size());
    }
}
