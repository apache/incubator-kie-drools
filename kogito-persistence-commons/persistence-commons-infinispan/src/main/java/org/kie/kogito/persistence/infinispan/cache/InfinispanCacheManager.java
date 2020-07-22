/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.persistence.infinispan.cache;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.DataFormat;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.exceptions.HotRodClientException;
import org.infinispan.commons.dataconversion.MediaType;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.StorageService;
import org.kie.kogito.persistence.api.factory.StorageQualifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.kogito.persistence.infinispan.Constants.INFINISPAN_STORAGE;

@ApplicationScoped
@StorageQualifier(INFINISPAN_STORAGE)
public class InfinispanCacheManager implements StorageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfinispanCacheManager.class);

    @Inject
    JsonDataFormatMarshaller marshaller;

    DataFormat jsonDataFormat;

    @Inject
    @ConfigProperty(name = "kogito.cache.domain.template", defaultValue = "kogito-template")
    String cacheTemplateName;

    @Inject
    RemoteCacheManager manager;

    @PostConstruct
    public void init() {
        jsonDataFormat = DataFormat.builder().valueType(MediaType.APPLICATION_JSON).valueMarshaller(marshaller).build();
        manager.start();
    }

    @PreDestroy
    public void destroy() {
        manager.stop();
        try {
            manager.close();
        } catch (Exception ex) {
            LOGGER.warn("Error trying to close Infinispan remote cache manager", ex);
        }
    }

    /**
     * Gets the cache if exists, otherwise tries to create one with the given template.
     * If the template does not exist on the server, creates the cache based on a default configuration.
     * @param name the cache manager name
     * @param template the template that must exists on the server
     * @see KogitoCacheDefaultConfiguration
     */
    protected <K, V> RemoteCache<K, V> getOrCreateCache(final String name, final String template) {
        try {
            LOGGER.debug("Trying to get cache {} from the server", name);
            RemoteCache<K, V> remoteCache = manager.getCache(name);
            if (remoteCache == null) {
                LOGGER.debug("Cache {} not found, trying to create a new one based on template {}", name, template);
                return manager.administration().getOrCreateCache(name, template);
            }
            return remoteCache;
        } catch (HotRodClientException e) {
            if (e.isServerError()) {
                LOGGER.info("Creating a cache for '{}' based on the default configuration", name);
                RemoteCache<K, V> cache = manager.administration().getOrCreateCache(name, new KogitoCacheDefaultConfiguration(name));
                LOGGER.debug("Default cache created {}", cache.getName());
                return cache;
            }
            throw e;
        }
    }

    @Override
    public Storage<String, String> getCache(String name) {
        return new StorageImpl<>(manager.administration().getOrCreateCache(name, (String) null), String.class.getName());
    }

    @Override
    public <T> Storage<String, T> getCache(String name, Class<T> type) {
        return new StorageImpl<>(getOrCreateCache(name, cacheTemplateName), type.getName());
    }

    @Override
    public <T> Storage<String, T> getCacheWithDataFormat(String name, Class<T> type, String rootType) {
        return new StorageImpl<>(getOrCreateCache(name, cacheTemplateName).withDataFormat(jsonDataFormat), rootType);
    }
}