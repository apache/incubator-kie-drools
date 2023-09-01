package org.drools.reliability.core;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.Storage;
import org.kie.api.runtime.conf.PersistedSessionOption;

import java.util.Set;

public interface StorageManager {

    void initStorageManager();

    default <K, V> Storage<K, V> getOrCreateStorageForSession(ReteEvaluator reteEvaluator, String storageName) {
        return getOrCreateStorageForSession(reteEvaluator, reteEvaluator.getSessionConfiguration().getPersistedSessionOption().getSafepointStrategy(), storageName);
    }

    default <K, V> Storage<K, V> getOrCreateStorageForSession(ReteEvaluator reteEvaluator, PersistedSessionOption.SafepointStrategy safepointStrategy, String storageName) {
        Storage<K, V> storage = internalGetOrCreateStorageForSession(reteEvaluator, storageName);
        if (safepointStrategy.useSafepoints()) {
            storage = new BatchingStorageDecorator<>(storage);
        }
        return storage;
    }

    <K, V> Storage<K, V> internalGetOrCreateStorageForSession(ReteEvaluator reteEvaluator, String storageName);

    <K, V> Storage<K, V> getOrCreateSharedStorage(String storageName);

    void close();

    void removeStorage(String storageName);

    void removeStoragesBySessionId(String sessionId);

    void removeAllSessionStorages();

    Set<String> getStorageNames();

    static String createStorageId(ReteEvaluator reteEvaluator, String storageName) {
        return StorageManagerFactory.SESSION_STORAGE_PREFIX + getSessionIdentifier(reteEvaluator) + StorageManagerFactory.DELIMITER + storageName;
    }

    public static long getSessionIdentifier(ReteEvaluator reteEvaluator) {
        PersistedSessionOption persistedSessionOption = reteEvaluator.getSessionConfiguration().getPersistedSessionOption();
        if (persistedSessionOption != null) {
            return persistedSessionOption.isNewSession() ? reteEvaluator.getIdentifier() : persistedSessionOption.getSessionId();
        } else {
            throw new ReliabilityConfigurationException("PersistedSessionOption has to be configured when drools-reliability is used");
        }
    }
}
