package org.drools.persistence;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class TransactionManagerHelper {

    private static final String APP_UPDETEABLE_RESOURCE = "app-updateable-resource";
    private static final String CMD_UPDETEABLE_RESOURCE = "cmd-updateable-resource";

    public static void registerTransactionSyncInContainer(TransactionManager txm, OrderedTransactionSynchronization synchronization) {
        TransactionSynchronizationContainer container = (TransactionSynchronizationContainer)txm.getResource(TransactionSynchronizationContainer.RESOURCE_KEY);
        if (container == null) {
            container = new TransactionSynchronizationContainer();
            txm.registerTransactionSynchronization( container );
            txm.putResource(TransactionSynchronizationContainer.RESOURCE_KEY, container);
        }
        container.addTransactionSynchronization(synchronization);
    }

    public static void addToUpdatableSet(TransactionManager txm, Transformable transformable) {
        if (transformable == null) {
            return;
        }
        Set<Transformable> toBeUpdated = (Set<Transformable>) txm.getResource(APP_UPDETEABLE_RESOURCE);
        if (toBeUpdated == null) {
            toBeUpdated = new LinkedHashSet<Transformable>();
            txm.putResource(APP_UPDETEABLE_RESOURCE, toBeUpdated);
        }
        toBeUpdated.add(transformable);
    }

    public static void removeFromUpdatableSet(TransactionManager txm, Transformable transformable) {
        Set<Transformable> toBeUpdated = (Set<Transformable>) txm.getResource(APP_UPDETEABLE_RESOURCE);
        if (toBeUpdated == null) {
            return;
        }
        toBeUpdated.remove(transformable);
    }

    public static Set<Transformable> getUpdateableSet(TransactionManager txm) {
        Set<Transformable> toBeUpdated = (Set<Transformable>) txm.getResource(APP_UPDETEABLE_RESOURCE);
        if (toBeUpdated == null) {
            return Collections.emptySet();
        }

        return new LinkedHashSet<Transformable>(toBeUpdated);
    }
}
