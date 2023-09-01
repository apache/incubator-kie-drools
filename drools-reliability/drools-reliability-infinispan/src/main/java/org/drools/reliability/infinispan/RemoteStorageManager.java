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
package org.drools.reliability.infinispan;

import java.util.Optional;
import java.util.Set;

import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.Storage;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.marshall.JavaSerializationMarshaller;
import org.infinispan.commons.marshall.ProtoStreamMarshaller;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.protostream.SerializationContext;
import org.infinispan.protostream.SerializationContextInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.reliability.core.StorageManager.createStorageId;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.DELIMITER;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.SESSION_STORAGE_PREFIX;
import static org.drools.reliability.infinispan.InfinispanStorageManagerFactory.SHARED_STORAGE_PREFIX;
import static org.drools.util.Config.getConfig;

public class RemoteStorageManager implements InfinispanStorageManager {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteStorageManager.class);

    static final RemoteStorageManager INSTANCE = new RemoteStorageManager();

    private org.infinispan.client.hotrod.RemoteCacheManager remoteCacheManager;

    private MarshallerType marshallerType;
    private Optional<ProtoStreamMarshaller> protoMarshaller;

    private RemoteStorageManager() {
    }

    @Override
    public void initStorageManager() {
        // Create a RemoteCacheManager with provided properties
        LOG.info("Using Remote Cache Manager");
        String host = getConfig(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_REMOTE_HOST);
        String port = getConfig(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_REMOTE_PORT);
        String user = getConfig(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_REMOTE_USER);
        String pass = getConfig(InfinispanStorageManagerFactory.INFINISPAN_STORAGE_REMOTE_PASS);

        marshallerType = InfinispanStorageManager.getMarshallerType();

        if (host == null || port == null) {
            LOG.info("Remote Cache Manager host '{}' and port '{}' not set. So not creating a default RemoteCacheManager." +
                             " You will need to set a RemoteCacheManager with setRemoteCacheManager() method.", host, port);
            return;
        }

        //--- Create a RemoteCacheManager with provided properties

        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.addServer().host(host).port(Integer.parseInt(port));
        if (user != null && pass != null) {
            builder.security().authentication().username(user).password(pass);
        }

        configureMarshaller(builder);

        remoteCacheManager = new org.infinispan.client.hotrod.RemoteCacheManager(builder.build());
    }

    private void configureMarshaller(ConfigurationBuilder builder) {
        if (marshallerType == MarshallerType.JAVA) {
            builder.marshaller(new JavaSerializationMarshaller())
                    .addJavaSerialAllowList(InfinispanStorageManager.getAllowedPackages());
        } else if (marshallerType == MarshallerType.PROTOSTREAM) {
            protoMarshaller = Optional.of(new ProtoStreamMarshaller());
            Optional<SerializationContextInitializer> serializationContextInitializer = findSerializationContextInitializer();
            if (serializationContextInitializer.isEmpty()) {
                throw new IllegalStateException("ProtoStream serialization context initializer not found");
            }
            builder.marshaller(protoMarshaller.get()).addContextInitializer(serializationContextInitializer.get());
        } else {
            throw new IllegalStateException("Unknown MarshallerType: " + marshallerType);
        }
    }

    @Override
    public <K, V> Storage<K, V> internalGetOrCreateStorageForSession(ReteEvaluator reteEvaluator, String cacheName) {
        RemoteCache<K, V> cache = remoteCacheManager.administration().getOrCreateCache(createStorageId(reteEvaluator, cacheName), (String) null);
        return InfinispanStorage.fromCache(cache);
    }

    @Override
    public <K, V> Storage<K, V> getOrCreateSharedStorage(String cacheName) {
        RemoteCache<K, V> cache = remoteCacheManager.administration().getOrCreateCache(SHARED_STORAGE_PREFIX + cacheName, (String) null);
        return InfinispanStorage.fromCache(cache);
    }

    @Override
    public void close() {
        remoteCacheManager.close();
    }

    @Override
    public void removeStorage(String storageName) {
        if (remoteCacheManager.getCache(storageName) != null) {
            remoteCacheManager.administration().removeCache(storageName);
        }
    }

    @Override
    public void removeStoragesBySessionId(String sessionId) {
        remoteCacheManager.getCacheNames()
                .stream()
                .filter(cacheName -> cacheName.startsWith(SESSION_STORAGE_PREFIX + sessionId + DELIMITER))
                .forEach(this::removeStorage);
    }

    @Override
    public void removeAllSessionStorages() {
        remoteCacheManager.getCacheNames()
                .stream()
                .filter(cacheName -> cacheName.startsWith(SESSION_STORAGE_PREFIX))
                .forEach(this::removeStorage);
    }

    @Override
    public Set<String> getStorageNames() {
        return remoteCacheManager.getCacheNames();
    }

    @Override
    public void setRemoteCacheManager(org.infinispan.client.hotrod.RemoteCacheManager remoteCacheManager) {
        this.remoteCacheManager = remoteCacheManager;
    }

    @Override
    public boolean isProtoStream() {
        return marshallerType == MarshallerType.PROTOSTREAM;
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
                .forEach(this::removeStorage);
    }

    @Override
    public void setEmbeddedCacheManager(DefaultCacheManager cacheManager) {
        throw new UnsupportedOperationException("setEmbeddedCacheManager is not supported in " + this.getClass());
    }

    @Override
    public ConfigurationBuilder provideAdditionalRemoteConfigurationBuilder() {
        // Used for externally provided RemoteCacheManager, especially for InfinispanContainer tests
        ConfigurationBuilder builder = new ConfigurationBuilder();
        configureMarshaller(builder);
        return builder;
    }

    @Override
    public SerializationContext getSerializationContext() {
        return protoMarshaller.get().getSerializationContext();
    }

    @Override
    public boolean isRemote() {
        return true;
    }
}
