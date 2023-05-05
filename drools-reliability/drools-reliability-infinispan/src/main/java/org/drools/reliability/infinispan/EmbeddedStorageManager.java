/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.reliability.infinispan;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.Storage;
import org.drools.util.FileUtils;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.globalstate.ConfigurationStorage;
import org.infinispan.manager.DefaultCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Paths;
import java.util.Set;

import static org.drools.reliability.core.StorageManager.createStorageId;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.*;
import static org.drools.util.Config.getConfig;

public class EmbeddedStorageManager implements InfinispanStorageManager {

    private static final Logger LOG = LoggerFactory.getLogger(EmbeddedStorageManager.class);

    public static final String GLOBAL_STATE_DIR = getConfig(INFINISPAN_STORAGE_DIRECTORY, "global/state");

    static final EmbeddedStorageManager INSTANCE = new EmbeddedStorageManager();

    private DefaultCacheManager embeddedCacheManager;
    private Configuration cacheConfiguration;

    public static final String CACHE_DIR = "cache";

    private EmbeddedStorageManager() {}

    @Override
    public void initStorageManager() {
        LOG.info("Using Embedded Cache Manager");

        // Set up a clustered Cache Manager.
        GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
        global.serialization()
              .marshaller(new JavaSerializationMarshaller())
              .allowList()
              .addRegexps(InfinispanStorageManager.getAllowedPackages());
        global.globalState()
              .enable()
              .persistentLocation(GLOBAL_STATE_DIR)
              .configurationStorage(ConfigurationStorage.OVERLAY);
        global.metrics().gauges(false);

        // Initialize the default Cache Manager.
        embeddedCacheManager = new DefaultCacheManager(global.build());

        // Create a distributed cache with synchronous replication.
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.persistence().passivation(false)
               .addSoftIndexFileStore()
               .shared(false)
               .dataLocation(CACHE_DIR + "/data")
               .indexLocation(CACHE_DIR + "/index");
        builder.clustering()
               .cacheMode(CacheMode.LOCAL);

        builder.unsafe().unreliableReturnValues(true);

        cacheConfiguration = builder.build();
    }

    @Override
    public <k, V> Storage<k, V> getOrCreateStorageForSession(ReteEvaluator reteEvaluator, String cacheName) {
        return InfinispanStorage.fromCache(embeddedCacheManager.administration().getOrCreateCache(createStorageId(reteEvaluator, cacheName), cacheConfiguration));
    }

    @Override
    public <k, V> Storage<k, V> getOrCreateSharedStorage(String cacheName) {
        return InfinispanStorage.fromCache(embeddedCacheManager.administration().getOrCreateCache(SHARED_STORAGE_PREFIX + cacheName, cacheConfiguration));
    }

    @Override
    public void close() {
        embeddedCacheManager.stop();
    }

    @Override
    public void removeStorage(String storageName) {
        if (embeddedCacheManager.cacheExists(storageName)) {
            embeddedCacheManager.removeCache(storageName);
        }
    }

    @Override
    public void removeStoragesBySessionId(String sessionId) {
        embeddedCacheManager.getCacheNames()
                            .stream()
                            .filter(cacheName -> cacheName.startsWith(SESSION_STORAGE_PREFIX + sessionId + DELIMITER))
                            .forEach(this::removeStorage);
    }

    @Override
    public void removeAllSessionStorages() {
        embeddedCacheManager.getCacheNames()
                            .stream()
                            .filter(cacheName -> cacheName.startsWith(SESSION_STORAGE_PREFIX))
                            .forEach(this::removeStorage);
    }

    @Override
    public Set<String> getStorageNames() {
        return embeddedCacheManager.getCacheNames();
    }

    @Override
    public void setRemoteCacheManager(RemoteCacheManager remoteCacheManager) {
        throw new UnsupportedOperationException("setRemoteCacheManager is not supported in " + this.getClass());
    }

    //--- test purpose

    @Override
    public void restart() {
        // JVM crashed
        embeddedCacheManager.stop();
        embeddedCacheManager = null;
        cacheConfiguration = null;

        // Reboot
        initStorageManager();
    }

    @Override
    public void restartWithCleanUp() {
        // JVM down
        embeddedCacheManager.stop();
        embeddedCacheManager = null;
        cacheConfiguration = null;

        // Remove GlobalState and FileStore
        cleanUpGlobalStateAndFileStore();

        // Reboot
        initStorageManager();
    }

    @Override
    public void setEmbeddedCacheManager(DefaultCacheManager embeddedCacheManager) {
        if (this.embeddedCacheManager != null) {
            this.embeddedCacheManager.stop();
        }
        this.embeddedCacheManager = embeddedCacheManager;
    }

    // test purpose to remove GlobalState and FileStore
    private static void cleanUpGlobalStateAndFileStore() {
        FileUtils.deleteDirectory(Paths.get(GLOBAL_STATE_DIR));
    }

    @Override
    public org.infinispan.client.hotrod.configuration.ConfigurationBuilder provideAdditionalRemoteConfigurationBuilder() {
        throw new UnsupportedOperationException("provideRemoteConfigurationBuilder is not supported in " + this.getClass());
    }

    @Override
    public boolean isRemote() {
        return false;
    }
}
