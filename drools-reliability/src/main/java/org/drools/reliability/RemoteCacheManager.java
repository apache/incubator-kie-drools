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
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.api.BasicCache;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.infinispan.manager.DefaultCacheManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.reliability.CacheManagerFactory.DELIMITER;
import static org.drools.reliability.CacheManagerFactory.SESSION_CACHE_PREFIX;
import static org.drools.reliability.CacheManager.*;

class RemoteCacheManager implements CacheManager {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteCacheManager.class);

    static final RemoteCacheManager INSTANCE = new RemoteCacheManager();

    private org.infinispan.client.hotrod.RemoteCacheManager remoteCacheManager;

    private RemoteCacheManager() {
    }

    @Override
    public void initCacheManager() {
        // Create a RemoteCacheManager with provided properties
        LOG.info("Using Remote Cache Manager");
        String host = System.getProperty(CacheManagerFactory.RELIABILITY_CACHE_REMOTE_HOST);
        String port = System.getProperty(CacheManagerFactory.RELIABILITY_CACHE_REMOTE_PORT);
        String user = System.getProperty(CacheManagerFactory.RELIABILITY_CACHE_REMOTE_USER);
        String pass = System.getProperty(CacheManagerFactory.RELIABILITY_CACHE_REMOTE_PASS);
        if (host == null || port == null) {
            LOG.info("Remote Cache Manager host '{}' and port '{}' not set. So not creating a default RemoteCacheManager." +
                             " You will need to set a RemoteCacheManager with setRemoteCacheManager() method.", host, port);
            return;
        }
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer().host(host).port(Integer.parseInt(port));
        if (user != null && pass != null) {
            builder.security().authentication().username(user).password(pass);
        }
        builder.marshaller(new JavaSerializationMarshaller())
                .addJavaSerialAllowList("org.kie.*")
                .addJavaSerialAllowList("org.drools.*")
                .addJavaSerialAllowList("java.*");
        remoteCacheManager = new org.infinispan.client.hotrod.RemoteCacheManager(builder.build());
    }

    @Override
    public <k, V> BasicCache<k, V> getOrCreateCacheForSession(ReteEvaluator reteEvaluator, String cacheName) {
        return remoteCacheManager.administration().getOrCreateCache(createCacheId(reteEvaluator, cacheName), (String) null);
    }

    @Override
    public void close() {
        remoteCacheManager.close();
    }

    @Override
    public void removeCache(String cacheName) {
        if (remoteCacheManager.getCache(cacheName) != null) {
            remoteCacheManager.administration().removeCache(cacheName);
        }
    }

    @Override
    public void removeCachesBySessionId(String sessionId) {
        remoteCacheManager.getCacheNames()
                .stream()
                .filter(cacheName -> cacheName.startsWith(SESSION_CACHE_PREFIX + sessionId + DELIMITER))
                .forEach(this::removeCache);
    }

    @Override
    public void removeAllSessionCaches() {
        remoteCacheManager.getCacheNames()
                .stream()
                .filter(cacheName -> cacheName.startsWith(SESSION_CACHE_PREFIX))
                .forEach(this::removeCache);
    }

    @Override
    public Set<String> getCacheNames() {
        return remoteCacheManager.getCacheNames();
    }

    @Override
    public void setRemoteCacheManager(org.infinispan.client.hotrod.RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
    }

    //--- test purpose

    @Override
    public void restart() {
        if (remoteCacheManager != null) {
            remoteCacheManager.stop();
        }

        // Not setting a remoteCacheManager, expecting the test to set it.
    }

    @Override
    public void restartWithCleanUp() {
        if (remoteCacheManager != null) {
            removeAllCache();
            remoteCacheManager.stop();
        }

        // Not setting a remoteCacheManager, expecting the test to set it.
    }

    private void removeAllCache() {
        remoteCacheManager.getCacheNames()
                .forEach(this::removeCache);
    }

    @Override
    public void setEmbeddedCacheManager(DefaultCacheManager cacheManager) {
        throw new UnsupportedOperationException("setEmbeddedCacheManager is not supported in " + this.getClass());
    }

    @Override
    public ConfigurationBuilder provideAdditionalRemoteConfigurationBuilder() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.marshaller(new JavaSerializationMarshaller())
                .addJavaSerialAllowList("org.kie.*")
                .addJavaSerialAllowList("org.drools.*")
                .addJavaSerialAllowList("java.*");
        return builder;
    }

    @Override
    public boolean isRemote() {
        return true;
    }
}
