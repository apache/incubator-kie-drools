/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
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
