/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.index.infinispan.schema;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.configuration.XMLStringConfiguration;
import org.infinispan.query.remote.client.ProtobufMetadataManagerConstants;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.persistence.api.Storage;
import org.kie.kogito.persistence.api.schema.SchemaDescriptor;
import org.kie.kogito.persistence.api.schema.SchemaRegisteredEvent;
import org.kie.kogito.persistence.api.schema.SchemaRegistrationException;
import org.kie.kogito.persistence.infinispan.cache.ProtobufCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.qute.Location;
import io.quarkus.qute.Template;

import static java.util.Collections.emptyList;

@ApplicationScoped
public class ProtoSchemaManager {
    private static final String PROTO_BASE_TYPE_STRING = "string";

    private static final Logger LOGGER = LoggerFactory.getLogger(ProtoSchemaManager.class);

    @Inject
    ProtoSchemaAcceptor schemaAcceptor;

    @Inject
    DataIndexStorageService cacheManager;

    @Inject
    RemoteCacheManager manager;

    @Inject
    ProtobufCacheService protobufCacheService;

    @Location("kogito-cache-default.xml")
    Template cacheTemplate;

    public void onSchemaRegisteredEvent(@Observes SchemaRegisteredEvent event) {
        if (schemaAcceptor.accept(event.getSchemaType())) {
            SchemaDescriptor schemaDescriptor = event.getSchemaDescriptor();
            Storage<String, String> cache = protobufCacheService.getProtobufCache();
            cache.put(schemaDescriptor.getName(),
                    schemaDescriptor.getSchemaContent()
                            .replaceAll("kogito.Date", PROTO_BASE_TYPE_STRING)
                            .replaceAll("kogito.Instant", PROTO_BASE_TYPE_STRING)
                            .replaceAll("kogito.Serializable", PROTO_BASE_TYPE_STRING));
            schemaDescriptor.getProcessDescriptor().ifPresent(processDescriptor -> {
                cacheManager.getProcessIdModelCache().put(processDescriptor.getProcessId(), processDescriptor.getProcessType());
                //Initialize domain cache
                String cacheName = cacheManager.getDomainModelCacheName(processDescriptor.getProcessId());
                String cacheTemplateRendered = getTemplateRendered(schemaDescriptor, cacheName);
                LOGGER.debug("Cache template: \n{}", cacheTemplateRendered);
                manager.administration().getOrCreateCache(cacheName, new XMLStringConfiguration(cacheTemplateRendered));
            });
            List<String> errors = checkSchemaErrors(cache);

            if (!errors.isEmpty()) {
                String message = "Proto Schema contain errors:\n" + String.join("\n", errors);
                throw new SchemaRegistrationException(message);
            }

            if (LOGGER.isDebugEnabled()) {
                logProtoCacheKeys();
            }
        }
    }

    private String getTemplateRendered(SchemaDescriptor schemaDescriptor, String cacheName) {
        return cacheTemplate.data("cache_name", cacheName)
                .data("indexed", schemaDescriptor.getEntityIndexDescriptors().keySet()).render();
    }

    private List<String> checkSchemaErrors(Storage<String, String> metadataCache) {
        if (metadataCache.containsKey(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX)) {
            List<String> errors = new ArrayList<>();
            // The existence of this key indicates there are errors in some files
            String files = metadataCache.get(ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX);
            for (String fname : files.split("\n")) {
                String errorKey = fname + ProtobufMetadataManagerConstants.ERRORS_KEY_SUFFIX;
                final String error = metadataCache.get(errorKey);
                LOGGER.warn("Found errors in Protobuf schema file: {}\n{}\n", fname, error);
                errors.add(String.format("Protobuf schema file: %s%n%s%n", fname, error));
            }
            return errors;
        } else {
            return emptyList();
        }
    }

    private void logProtoCacheKeys() {
        LOGGER.debug(">>>>>>list cache keys start");
        protobufCacheService.getProtobufCache().entries().entrySet().forEach(e -> LOGGER.debug(e.toString()));
        LOGGER.debug(">>>>>>list cache keys end");
    }
}
