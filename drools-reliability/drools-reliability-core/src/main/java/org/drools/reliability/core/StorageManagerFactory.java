package org.drools.reliability.core;

import org.kie.api.internal.utils.KieService;

public interface StorageManagerFactory extends KieService {

    String SESSION_STORAGE_PREFIX = "session_";
    String SHARED_STORAGE_PREFIX = "shared_";
    String DELIMITER = "_";

    String RELIABILITY_STORAGE_PREFIX = "drools.reliability.storage";

    StorageManager getStorageManager();

    class Holder {
        private static final StorageManagerFactory INSTANCE = createInstance();

        static StorageManagerFactory createInstance() {
            StorageManagerFactory factory = KieService.load( StorageManagerFactory.class );
            if (factory == null) {
                throwExceptionForMissingRuntime();
                return null;
            }
            return factory;
        }
    }

    static StorageManagerFactory get() {
        return StorageManagerFactory.Holder.INSTANCE;
    }

    static <T> T throwExceptionForMissingRuntime() {
        throw new RuntimeException("Cannot find any persistence implementation");
    }
}
