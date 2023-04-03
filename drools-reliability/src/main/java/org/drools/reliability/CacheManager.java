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

import java.util.Set;

import org.drools.core.common.ReteEvaluator;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.manager.DefaultCacheManager;

public enum CacheManager implements AutoCloseable {

    INSTANCE;

    public static final String SESSION_CACHE_PREFIX = "session_";
    public static final String DELIMITER = "_";

    public static final String CACHE_MANAGER_MODE_PROPERTY = "drools.reliability.cache.manager.mode";
    public static final String CACHE_MANAGER_REMOTE_HOST = "drools.reliability.cache.manager.remote.host";
    public static final String CACHE_MANAGER_REMOTE_PORT = "drools.reliability.cache.manager.remote.port";
    public static final String CACHE_MANAGER_REMOTE_USER = "drools.reliability.cache.manager.remote.user";
    public static final String CACHE_MANAGER_REMOTE_PASS = "drools.reliability.cache.manager.remote.pass";

    private final CacheManagerDelegate delegate;

    CacheManager() {
        String modeValue = System.getProperty(CACHE_MANAGER_MODE_PROPERTY, "EMBEDDED");
        if (modeValue.equalsIgnoreCase("REMOTE")) {
            delegate = RemoteCacheManagerDelegate.INSTANCE;
        } else {
            delegate = EmbeddedCacheManagerDelegate.INSTANCE;
        }

        initCacheManager();
    }

    private void initCacheManager() {
        delegate.initCacheManager();
    }

    public <k, V> BasicCache<k, V> getOrCreateCacheForSession(ReteEvaluator reteEvaluator, String cacheName) {
        return delegate.getOrCreateCacheForSession(reteEvaluator, cacheName);
    }

    @Override
    public void close() {
        delegate.close();
    }

    public boolean isRemote() {
        return delegate instanceof RemoteCacheManagerDelegate;
    }

    public void setRemoteCacheManager(RemoteCacheManager remoteCacheManager) {
        delegate.setRemoteCacheManager(remoteCacheManager);
    }

    // test purpose to simulate fail-over
    void restart() {
        delegate.restart();
    }

    // test purpose to clean up environment
    void restartWithCleanUp() {
        delegate.restartWithCleanUp();
    }

    // test purpose to inject fake cacheManager
    void setEmbeddedCacheManager(DefaultCacheManager cacheManager) {
        delegate.setEmbeddedCacheManager(cacheManager);
    }

    // test purpose
    org.infinispan.client.hotrod.configuration.ConfigurationBuilder provideAdditionalRemoteConfigurationBuilder() {
        return delegate.provideAdditionalRemoteConfigurationBuilder();
    }

    public void removeCache(String cacheName) {
        delegate.removeCache(cacheName);
    }

    public void removeCachesBySessionId(String sessionId) {
        delegate.removeCachesBySessionId(sessionId);
    }

    public void removeAllSessionCaches() {
        delegate.removeAllSessionCaches();
    }

    public Set<String> getCacheNames() {
        return delegate.getCacheNames();
    }
}
