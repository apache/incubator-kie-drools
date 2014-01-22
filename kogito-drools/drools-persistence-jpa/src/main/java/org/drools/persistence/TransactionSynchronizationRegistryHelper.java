package org.drools.persistence;

import javax.transaction.TransactionSynchronizationRegistry;

import org.drools.persistence.jta.JtaTransactionSynchronizationAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionSynchronizationRegistryHelper {

    private static final Logger logger = LoggerFactory.getLogger(TransactionSynchronizationRegistryHelper.class);
    public static void registerTransactionSynchronization(final Object tsro, final TransactionSynchronization ts) {
        TransactionSynchronizationRegistry tsr = ( TransactionSynchronizationRegistry ) tsro;
        tsr.registerInterposedSynchronization( new JtaTransactionSynchronizationAdapter( ts ) );
    }

    public static void putResource(final Object tsro, final Object key, final Object resource) {
        TransactionSynchronizationRegistry tsr = ( TransactionSynchronizationRegistry ) tsro;
        try {
            tsr.putResource(key, resource);
        } catch (IllegalStateException e) {
            // IllegalStateException is thrown when no transaction is active
            // so report only when there is actually resource to be inserted
            if (resource != null) {
                logger.warn("Unable to put resource {} value {} due to {}", key, resource, e.getMessage());
            }
        } catch (Exception e) {
            logger.warn("Unable to put resource {} value {} due to {}", key, resource, e.getMessage());
        }
    }

    public static Object getResource(final Object tsro, final Object key) {
        TransactionSynchronizationRegistry tsr = ( TransactionSynchronizationRegistry ) tsro;
        try {
            return tsr.getResource(key);
        } catch (Exception e) {
            return null;
        }
    }
}
