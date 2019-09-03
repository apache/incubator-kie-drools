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

package org.kie.kogito.index.infinispan.cache;

import java.io.IOException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.json.JsonObject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.infinispan.client.hotrod.DataFormat;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.exceptions.HotRodClientException;
import org.infinispan.commons.dataconversion.MediaType;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.kie.kogito.index.cache.CacheService;
import org.kie.kogito.index.model.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class InfinispanCacheManager implements CacheService {

    private static final Logger LOGGER = LoggerFactory.getLogger(InfinispanCacheManager.class);
    private static final String PROCESS_INSTANCES_CACHE = "processinstances";

    private DataFormat jsonDataFormat = DataFormat.builder().valueType(MediaType.APPLICATION_JSON).valueMarshaller(new JsonDataFormatMarshaller()).build();

    @Inject
    @ConfigProperty(name = "kogito.cache.domain.template", defaultValue = "kogito-template")
    String domainCacheTemplate;

    @Inject
    RemoteCacheManager manager;

    @PostConstruct
    public void init() {
        manager.start();
    }

    @PreDestroy
    public void destroy() {
        manager.stop();
        try {
            manager.close();
        } catch (IOException ex) {
            LOGGER.warn("Error trying to close Infinispan remote cache manager", ex);
        }
    }

    /**
     * Gets the cache if exists, otherwise tries to create one with the given template.
     * If the template does not exist on the server, creates the cache based on a default configuration.
     * 
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
                return manager.administration().createCache(name, template);
            }
            return remoteCache;
        } catch (HotRodClientException e) {
            if (e.isServerError()) {
                LOGGER.info("Creating a cache for '{}' based on the default configuration", name);
                RemoteCache<K, V> cache = manager.administration().createCache(name, new KogitoCacheDefaultConfiguration(name));
                LOGGER.debug("Default cache created {}", cache.getName());
                return cache;
            }
            throw e;
        }
    }

    @Override
    public Map<String, ProcessInstance> getProcessInstancesCache() {
        return this.getOrCreateCache(PROCESS_INSTANCES_CACHE, domainCacheTemplate);
    }

    @Override
    public Map<String, JsonObject> getProcessInstancesCacheAsJson() {
        return this.getOrCreateCache(PROCESS_INSTANCES_CACHE, domainCacheTemplate).withDataFormat(jsonDataFormat);
    }

    @Override
    public Map<String, String> getProtobufCache() {
        return manager.getCache(ProtobufMetadataManagerConstants.PROTOBUF_METADATA_CACHE_NAME);
    }

    @Override
    public Map<String, String> getProcessIdModelCache() {
        return manager.administration().getOrCreateCache("processidmodel", (String) null);
    }

    @Override
    public Map<String, JsonObject> getDomainModelCache(String processId) {
        return this.getOrCreateCache(processId + "_domain", domainCacheTemplate).withDataFormat(jsonDataFormat);
    }

}
