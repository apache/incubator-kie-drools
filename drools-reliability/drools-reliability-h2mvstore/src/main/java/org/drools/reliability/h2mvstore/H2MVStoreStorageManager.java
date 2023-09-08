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
package org.drools.reliability.h2mvstore;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.Storage;
import org.drools.reliability.core.TestableStorageManager;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.reliability.core.StorageManager.createStorageId;
import static org.drools.reliability.core.StorageManagerFactory.DELIMITER;
import static org.drools.reliability.core.StorageManagerFactory.SESSION_STORAGE_PREFIX;
import static org.drools.reliability.core.StorageManagerFactory.SHARED_STORAGE_PREFIX;

public class H2MVStoreStorageManager implements TestableStorageManager {

    private static final Logger LOG = LoggerFactory.getLogger(H2MVStoreStorageManager.class);

    static final H2MVStoreStorageManager INSTANCE = new H2MVStoreStorageManager();

    public static final String STORE_FILE_NAME = "h2mvstore.db";

    private MVStore mvStore;

    private H2MVStoreStorageManager() {
    }

    @Override
    public void initStorageManager() {
        LOG.info("Using H2MVStoreStorageManager");
        mvStore = MVStore.open(STORE_FILE_NAME);
    }

    @Override
    public <K, V> Storage<K, V> internalGetOrCreateStorageForSession(ReteEvaluator reteEvaluator, String cacheName) {
        MVMap<K, V> mvMap = mvStore.openMap(createStorageId(reteEvaluator, cacheName));
        return H2MVStoreStorage.fromMVMap(mvMap);
    }

    @Override
    public <K, V> Storage<K, V> getOrCreateSharedStorage(String cacheName) {
        MVMap<K, V> mvMap = mvStore.openMap(SHARED_STORAGE_PREFIX + cacheName);
        return H2MVStoreStorage.fromMVMap(mvMap);
    }

    @Override
    public void close() {
        mvStore.close();
    }

    @Override
    public void removeStorage(String storageName) {
        mvStore.removeMap(storageName);
    }

    @Override
    public void removeStoragesBySessionId(String sessionId) {
        mvStore.getMapNames()
                .stream()
                .filter(mapName -> mapName.startsWith(SESSION_STORAGE_PREFIX + sessionId + DELIMITER))
                .forEach(this::removeStorage);
    }

    @Override
    public void removeAllSessionStorages() {
        mvStore.getMapNames()
                .stream()
                .filter(mapName -> mapName.startsWith(SESSION_STORAGE_PREFIX))
                .forEach(this::removeStorage);
    }

    @Override
    public Set<String> getStorageNames() {
        return mvStore.getMapNames();
    }

    //--- test purpose

    @Override
    public void restart() {
        // JVM crashed
        mvStore.close();
        mvStore = null;

        // Reboot
        initStorageManager();
    }

    @Override
    public void restartWithCleanUp() {
        // JVM crashed
        mvStore.close();
        mvStore = null;

        // remove database file
        cleanUpDatabase();

        // Reboot
        initStorageManager();
    }

    @Override
    public boolean isRemote() {
        return false;
    }

    public static void cleanUpDatabase() {
        // remove database file
        Path path = Paths.get(STORE_FILE_NAME);
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
