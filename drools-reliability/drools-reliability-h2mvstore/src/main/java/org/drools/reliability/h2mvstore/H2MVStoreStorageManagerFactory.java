package org.drools.reliability.h2mvstore;

import org.drools.reliability.core.StorageManager;
import org.drools.reliability.core.StorageManagerFactory;

public class H2MVStoreStorageManagerFactory implements StorageManagerFactory {

    static int servicePriorityValue = 0; // package access for test purposes

    private final StorageManager storageManager;

    public H2MVStoreStorageManagerFactory() {
        storageManager = H2MVStoreStorageManager.INSTANCE;

        // initStorageManager() is called by StorageManagerFactory.Holder.createInstance()
    }

    @Override
    public StorageManager getStorageManager() {
        return storageManager;
    }

    @Override
    public int servicePriority() {
        return servicePriorityValue;
    }
}
