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

import org.infinispan.Cache;
import org.infinispan.commons.api.CacheContainerAdmin;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.transaction.TransactionMode;

public enum CacheManager implements AutoCloseable {

    INSTANCE;

    private final DefaultCacheManager cacheManager;
    private final Configuration cacheConfiguration;

    public static final String CACHE_DIR = "tmp/cache";

    CacheManager() {
        // Set up a clustered Cache Manager.
        GlobalConfigurationBuilder global = new GlobalConfigurationBuilder();
        global.serialization()
                .marshaller(new JavaSerializationMarshaller())
                .allowList()
                .addRegexps("org.drools.");
        global.transport().defaultTransport();

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
        builder.clustering().transaction().transactionMode(TransactionMode.TRANSACTIONAL);
        cacheConfiguration = builder.build();
    }

    public <k, V> Cache<k, V> getOrCreateCache(String cacheName) {
        // Obtain a volatile cache.
        return cacheManager.administration().withFlags(CacheContainerAdmin.AdminFlag.VOLATILE).getOrCreateCache(cacheName, cacheConfiguration);
    }

    @Override
    public void close() {
        cacheManager.stop();
    }

    public void removeCache(String cacheName){
        if (cacheManager.cacheExists(cacheName)) {
            cacheManager.removeCache(cacheName);
        }
    }

}
