/*
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
package org.kie.kogito.infinispan;

import java.util.Map.Entry;
import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.infinispan.client.hotrod.DefaultTemplate;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.util.CloseableIterator;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerService;
import org.kie.kogito.internal.utils.ConversionUtils;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceOptimisticLockingException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;

@SuppressWarnings({ "rawtypes" })
public class CacheProcessInstances implements MutableProcessInstances {

    private final RemoteCache<String, byte[]> cache;
    private final ProcessInstanceMarshallerService marshaller;
    private final org.kie.kogito.process.Process<?> process;
    private final boolean lock;

    public CacheProcessInstances(Process<?> process, RemoteCacheManager cacheManager, String templateName, boolean lock) {
        this.process = process;
        String cacheName = process.id() + "_store";
        if (ConversionUtils.isEmpty(templateName)) {
            this.cache = cacheManager.administration().getOrCreateCache(cacheName, DefaultTemplate.LOCAL);
        } else {
            this.cache = cacheManager.administration().getOrCreateCache(cacheName, templateName);
        }
        this.marshaller = ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().build();
        this.lock = lock;
    }

    @Override
    public Optional<? extends ProcessInstance> findById(String id, ProcessInstanceReadMode mode) {
        return this.lock ? findWithLock(id, mode) : findInternal(id, mode);
    }

    private Optional<? extends ProcessInstance> findInternal(String id, ProcessInstanceReadMode mode) {
        byte[] data = cache.get(id);
        return data == null ? Optional.empty() : Optional.of(marshaller.unmarshallProcessInstance(data, process, mode));
    }

    private Optional<? extends ProcessInstance> findWithLock(String id, ProcessInstanceReadMode mode) {
        return Optional.ofNullable(cache.getWithMetadata(id)).map(record -> unmarshall(record, mode));
    }

    @Override
    public Stream<? extends ProcessInstance> stream(ProcessInstanceReadMode mode) {
        if (lock) {
            CloseableIterator<Entry<Object, MetadataValue<Object>>> iterator = cache.retrieveEntriesWithMetadata(null, 1000);
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false).map(v -> unmarshall(v.getValue(), mode)).onClose(iterator::close);
        } else {
            return cache.values().parallelStream().map(marshaller.createUnmarshallFunction(process, mode));
        }
    }

    private <T> ProcessInstance<?> unmarshall(MetadataValue<T> versionedCache, ProcessInstanceReadMode mode) {
        ProcessInstance<?> instance = marshaller.unmarshallProcessInstance((byte[]) versionedCache.getValue(), process, mode);
        ((AbstractProcessInstance) instance).setVersion(versionedCache.getVersion());
        return instance;
    }

    @Override
    public void update(String id, ProcessInstance instance) {
        try {
            updateStorage(id, instance, false);
        } finally {
            disconnect(id, instance);
        }
    }

    @Override
    public void remove(String id) {
        cache.remove(id);
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
                } else if (this.lock) {
                    ((AbstractProcessInstance) instance).setVersion(1);
                }
            } else {
                if (this.lock) {
                    boolean success = cache.replaceWithVersion(id, data, instance.version());
                    if (!success) {
                        throw new ProcessInstanceOptimisticLockingException(id);
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
        ((AbstractProcessInstance<?>) instance).internalSetReloadSupplier(marshaller.createdReloadFunction(supplier));
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance();
    }

    private void reload(String id, ProcessInstance instance) {
        Supplier<byte[]> supplier = () -> cache.get(id);
        ((AbstractProcessInstance<?>) instance).internalSetReloadSupplier(marshaller.createdReloadFunction(supplier));
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance();
    }

    @Override
    public boolean exists(String id) {
        return cache.containsKey(id);
    }

    @Override
    public boolean lock() {
        return this.lock;
    }

}
