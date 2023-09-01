package org.drools.reliability.infinispan;

import org.drools.reliability.core.StorageManager;
import org.drools.reliability.core.StorageManagerFactory;

import static org.drools.util.Config.getConfig;

public class InfinispanStorageManagerFactory implements StorageManagerFactory {

    static int servicePriorityValue = 0; // package access for test purposes

    public static final String INFINISPAN_STORAGE_PREFIX = RELIABILITY_STORAGE_PREFIX + ".infinispan";
    public static final String INFINISPAN_STORAGE_ALLOWED_PACKAGES = INFINISPAN_STORAGE_PREFIX + ".allowedpackages";
    public static final String INFINISPAN_STORAGE_DIRECTORY = INFINISPAN_STORAGE_PREFIX + ".dir";
    public static final String INFINISPAN_STORAGE_MODE = INFINISPAN_STORAGE_PREFIX + ".mode";
    public static final String INFINISPAN_STORAGE_MARSHALLER = INFINISPAN_STORAGE_PREFIX + ".marshaller";
    public static final String INFINISPAN_STORAGE_SERIALIZATION_CONTEXT_INITIALIZER = INFINISPAN_STORAGE_PREFIX + ".serialization.context.initializer";
    public static final String INFINISPAN_STORAGE_REMOTE_HOST = INFINISPAN_STORAGE_PREFIX + ".remote.host";
    public static final String INFINISPAN_STORAGE_REMOTE_PORT = INFINISPAN_STORAGE_PREFIX + ".remote.port";
    public static final String INFINISPAN_STORAGE_REMOTE_USER = INFINISPAN_STORAGE_PREFIX + ".remote.user";
    public static final String INFINISPAN_STORAGE_REMOTE_PASS = INFINISPAN_STORAGE_PREFIX + ".remote.pass";


    private final StorageManager storageManager;

    public InfinispanStorageManagerFactory() {
        if ("REMOTE".equalsIgnoreCase(getConfig(INFINISPAN_STORAGE_MODE))) {
            storageManager = RemoteStorageManager.INSTANCE;
        } else {
            storageManager = EmbeddedStorageManager.INSTANCE;
        }

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
