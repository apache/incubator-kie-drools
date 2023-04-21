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
import org.infinispan.commons.api.BasicCache;
import org.infinispan.configuration.cache.CacheMode;
import org.infinispan.configuration.cache.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.reliability.core.CacheManager.createCacheId;
import static org.drools.reliability.infinispan.EmbeddedCacheManager.CACHE_DIR;
import static org.drools.reliability.infinispan.InfinispanCacheManagerFactory.SHARED_CACHE_PREFIX;

public class RemoteFileStoreCacheManager extends RemoteCacheManager {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteFileStoreCacheManager.class);

    static final RemoteFileStoreCacheManager INSTANCE = new RemoteFileStoreCacheManager();

    private Configuration cacheConfiguration;

    private RemoteFileStoreCacheManager() {
        super();
    }

    @Override
    public void initCacheManager() {
        super.initCacheManager();
        LOG.info("with File Store");

        // Create a remote cache configuration with file store.
        org.infinispan.configuration.cache.ConfigurationBuilder builder = new org.infinispan.configuration.cache.ConfigurationBuilder();
        builder.persistence().passivation(false)
                .addSoftIndexFileStore()
                .shared(false)
                .dataLocation(CACHE_DIR + "/data")
                .indexLocation(CACHE_DIR + "/index");
        builder.clustering()
                .cacheMode(CacheMode.LOCAL);
        cacheConfiguration = builder.build();
    }

    @Override
    public <k, V> BasicCache<k, V> getOrCreateCacheForSession(ReteEvaluator reteEvaluator, String cacheName) {
        return remoteCacheManager.administration().getOrCreateCache(createCacheId(reteEvaluator, cacheName), cacheConfiguration);
    }

    @Override
    public <k, V> BasicCache<k, V> getOrCreateSharedCache(String cacheName) {
        return remoteCacheManager.administration().getOrCreateCache(SHARED_CACHE_PREFIX + cacheName, cacheConfiguration);
    }
}
