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
package org.kie.kogito.infinispan;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.kie.kogito.serialization.process.ProcessInstanceMarshallerService;

import static org.kie.kogito.process.ProcessInstanceReadMode.MUTABLE;

@SuppressWarnings({ "rawtypes" })
public class CacheProcessInstances implements MutableProcessInstances {

    private final RemoteCache<String, byte[]> cache;
    private ProcessInstanceMarshallerService marshaller;
    private org.kie.kogito.process.Process<?> process;
    private final boolean lock;

    public CacheProcessInstances(Process<?> process, RemoteCacheManager cacheManager, String templateName, boolean lock) {
        this.process = process;
        this.cache = cacheManager.administration().getOrCreateCache(process.id() + "_store", ignoreNullOrEmpty(templateName));
        this.marshaller = ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().build();
        this.lock = lock;
    }

    @Override
    public Integer size() {
        return cache.size();
    }

    @Override
    public Optional<? extends ProcessInstance> findById(String id, ProcessInstanceReadMode mode) {
        return this.lock ? findWithLock(id, mode) : findInternal(id, mode);
    }

    private Optional<? extends ProcessInstance> findInternal(String id, ProcessInstanceReadMode mode) {
        byte[] data = cache.get(id);
        if (data == null) {
            return Optional.empty();
        }
        return Optional.of(mode == MUTABLE ? marshaller.unmarshallProcessInstance(data, process) : marshaller.unmarshallReadOnlyProcessInstance(data, process));
    }

    private Optional<? extends ProcessInstance> findWithLock(String id, ProcessInstanceReadMode mode) {
        MetadataValue<byte[]> versionedCache = cache.getWithMetadata(id);
        if (versionedCache != null) {
            ProcessInstance<?> instance =
                    mode == MUTABLE ? marshaller.unmarshallProcessInstance(versionedCache.getValue(), process) : marshaller.unmarshallReadOnlyProcessInstance(versionedCache.getValue(), process);
            ((AbstractProcessInstance) instance).setVersion(versionedCache.getVersion());
            return Optional.of(instance);
        }
        return Optional.empty();
    }

    @Override
    public Collection<? extends ProcessInstance> values(ProcessInstanceReadMode mode) {
        return cache.values()
                .parallelStream()
                .map(data -> mode == MUTABLE ? marshaller.unmarshallProcessInstance(data, process) : marshaller.unmarshallReadOnlyProcessInstance(data, process))
                .collect(Collectors.toList());
    }

    @Override
    public void update(String id, ProcessInstance instance) {
        updateStorage(id, instance, false);
        disconnect(id, instance);
    }

    @Override
    public void remove(String id) {
        if (this.lock) {
            MetadataValue<byte[]> versionedCache = cache.getWithMetadata(id);
            boolean success = cache.removeWithVersion(id, versionedCache.getVersion());
            if (!success) {
                throw uncheckedException(null, "The document with ID: %s was deleted by other request.", id);
            }
        } else {
            cache.remove(id);
        }
    }

    protected String ignoreNullOrEmpty(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        return value;
    }

    @Override
    public void create(String id, ProcessInstance instance) {
        updateStorage(id, instance, true);
    }

    @SuppressWarnings("unchecked")
    protected void updateStorage(String id, ProcessInstance instance, boolean checkDuplicates) {
        if (isActive(instance)) {
            byte[] data = marshaller.marshallProcessInstance(instance);

            if (checkDuplicates) {
                byte[] existing = cache.putIfAbsent(id, data);
                if (existing != null) {
                    throw new ProcessInstanceDuplicatedException(id);
                }
            } else {
                if (this.lock) {
                    boolean success = cache.replaceWithVersion(id, data, instance.version());
                    if (!success) {
                        throw uncheckedException(null, "The document with ID: %s was updated or deleted by other request.", id);
                    }
                } else {
                    cache.put(id, data);
                }
            }
        }
    }

    private void disconnect(String id, ProcessInstance instance) {
        if (this.lock) {
            reloadWithLock(id, instance);
        } else {
            reload(id, instance);
        }
    }

    private void reloadWithLock(String id, ProcessInstance instance) {
        Supplier<byte[]> supplier = () -> {
            MetadataValue<byte[]> versionedCache = cache.getWithMetadata(id);
            ((AbstractProcessInstance) instance).setVersion(versionedCache.getVersion());
            return versionedCache.getValue();
        };
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance(marshaller.createdReloadFunction(supplier));
    }

    private void reload(String id, ProcessInstance instance) {
        Supplier<byte[]> supplier = () -> cache.get(id);
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance(marshaller.createdReloadFunction(supplier));
    }

    @Override
    public boolean exists(String id) {
        return cache.containsKey(id);
    }

    @Override
    public boolean lock() {
        return this.lock;
    }

    private RuntimeException uncheckedException(Exception ex, String message, Object... param) {
        return new RuntimeException(String.format(message, param), ex);
    }
}