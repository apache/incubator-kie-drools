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

package org.drools.reliability;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;

import org.drools.core.common.ReteEvaluator;
import org.infinispan.Cache;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.globalstate.ConfigurationStorage;
import org.infinispan.manager.DefaultCacheManager;
import org.kie.api.runtime.conf.PersistedSessionOption;

public enum CacheManager implements AutoCloseable {

    INSTANCE;

    public static final String SESSION_CACHE_PREFIX = "session_";
    public static final String DELIMITER = "_";

    private DefaultCacheManager cacheManager;
    private Configuration cacheConfiguration;

    public static final String GLOBAL_STATE_DIR = "global/state";
    public static final String CACHE_DIR = "cache";

    CacheManager() {
        initCacheManager();
    }

    private void initCacheManager() {
        // Set up a clustered Cache Manager.
        GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
        global.serialization()
                .marshaller(new JavaSerializationMarshaller())
                .allowList()
                .addRegexps("org.kie.*") // TODO: need to be configurable
                .addRegexps("org.drools.*") // TODO: need to be configurable
                .addRegexps("java.*"); // TODO: why is this necessary?
        global.globalState()
                .enable()
                .persistentLocation(GLOBAL_STATE_DIR)
                .configurationStorage(ConfigurationStorage.OVERLAY);

        // Initialize the default Cache Manager.
        cacheManager = new DefaultCacheManager(global.build());

        // Create a distributed cache with synchronous replication.
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.persistence().passivation(false)
                .addSoftIndexFileStore()
                .shared(false)
                .dataLocation(CACHE_DIR + "/data")
                .indexLocation(CACHE_DIR + "/index");
        builder.clustering()
                .cacheMode(CacheMode.LOCAL)
                .hash().numOwners(1);
        cacheConfiguration = builder.build();
    }

    public <k, V> Cache<k, V> getOrCreateCacheForSession(ReteEvaluator reteEvaluator, String cacheName) {
        String cacheId = SESSION_CACHE_PREFIX + getSessionIdentifier(reteEvaluator) + DELIMITER + cacheName;
        return cacheManager.administration().getOrCreateCache(cacheId, cacheConfiguration);
    }

    private long getSessionIdentifier(ReteEvaluator reteEvaluator) {
        PersistedSessionOption persistedSessionOption = reteEvaluator.getSessionConfiguration().getPersistedSessionOption();
        if (persistedSessionOption != null) {
            return persistedSessionOption.isNewSession() ? reteEvaluator.getIdentifier() : persistedSessionOption.getSessionId();
        } else {
            throw new ReliabilityConfigurationException("PersistedSessionOption has to be configured when drools-reliability is used");
        }
    }

    @Override
    public void close() {
        cacheManager.stop();
    }

    // test purpose to simulate fail-over
    void restart() {
        // JVM crashed
        cacheManager.stop();
        cacheManager = null;
        cacheConfiguration = null;

        // Reboot
        initCacheManager();
    }

    // test purpose to clean up environment
    void restartWithRemovingGlobalStateAndFileStore() {
        // JVM down
        cacheManager.stop();
        cacheManager = null;
        cacheConfiguration = null;

        // Remove GlobalState and FileStore
        cleanUpGlobalStateAndFileStore();

        // Reboot
        initCacheManager();
    }

    // test purpose to remove GlobalState and FileStore
    static void cleanUpGlobalStateAndFileStore() {
        try {
            Path path = Paths.get(GLOBAL_STATE_DIR);
            if (Files.exists(path)) {
                try (Stream<Path> walk = Files.walk(path)) {
                    walk.sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    // test purpose to inject fake cacheManager
    void setCacheManager(DefaultCacheManager cacheManager) {
        if (this.cacheManager != null) {
            this.cacheManager.stop();
        }
        this.cacheManager = cacheManager;
    }

    public void removeCache(String cacheName) {
        if (cacheManager.cacheExists(cacheName)) {
            cacheManager.removeCache(cacheName);
        }
    }

    public void removeCachesBySessionId(String sessionId) {
        cacheManager.getCacheNames()
                .stream()
                .filter(cacheName -> cacheName.startsWith(SESSION_CACHE_PREFIX + sessionId + DELIMITER))
                .forEach(this::removeCache);
    }

    public void removeAllSessionCaches() {
        cacheManager.getCacheNames()
                .stream()
                .filter(cacheName -> cacheName.startsWith(SESSION_CACHE_PREFIX))
                .forEach(this::removeCache);
    }

    public Set<String> getCacheNames() {
        return cacheManager.getCacheNames();
    }
}
