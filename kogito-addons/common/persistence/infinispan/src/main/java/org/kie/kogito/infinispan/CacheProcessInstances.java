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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.infinispan.client.hotrod.DefaultTemplate;
import org.infinispan.client.hotrod.MetadataValue;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.commons.util.CloseableIterator;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerService;
import org.kie.kogito.Model;
import org.kie.kogito.internal.utils.ConversionUtils;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceOptimisticLockingException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;

public class CacheProcessInstances<T extends Model> implements MutableProcessInstances<T> {
    private final String EVENT_SEPARATOR = "::";
    private final RemoteCache<String, byte[]> cache;
    private final ProcessInstanceMarshallerService marshaller;
    private final org.kie.kogito.process.Process<?> process;
    private final boolean lock;
    private String eventKey;

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
        this.eventKey = process.id() + "-" + process.version() + ".events";
    }

    @Override
    public Optional<ProcessInstance<T>> findById(String id, ProcessInstanceReadMode mode) {
        return this.lock ? findWithLock(id, mode) : findInternal(id, mode);
    }

    private Optional<ProcessInstance<T>> findInternal(String id, ProcessInstanceReadMode mode) {
        byte[] data = cache.get(id);
        return data == null ? Optional.empty() : Optional.of(unmarshall(data, null, mode));
    }

    private Optional<ProcessInstance<T>> findWithLock(String id, ProcessInstanceReadMode mode) {
        return Optional.ofNullable(cache.getWithMetadata(id)).map(record -> unmarshall(record.getValue(), record.getVersion(), mode));
    }

    private Set<String> getUniqueEvents(ProcessInstance<T> instance) {
        return Stream.of(((AbstractProcessInstance<T>) instance).internalGetProcessInstance().getEventTypes())
                .map(e -> e + EVENT_SEPARATOR + instance.id())
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Stream<ProcessInstance<T>> stream(ProcessInstanceReadMode mode) {
        if (lock) {
            CloseableIterator<Entry<Object, MetadataValue<Object>>> iterator = cache.retrieveEntriesWithMetadata(null, 1000);
            return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                    .filter(v -> !v.getKey().equals(this.eventKey))
                    .map(v -> unmarshall((byte[]) v.getValue().getValue(), v.getValue().getVersion(), mode))
                    .onClose(iterator::close);
        } else {
            return cache.entrySet().stream().filter(v -> !v.getKey().equals(this.eventKey)).map(data -> unmarshall(data.getValue(), null, mode));
        }
    }

    private ProcessInstance<T> unmarshall(byte[] data, Long version, ProcessInstanceReadMode mode) {
        ProcessInstance<T> instance = (ProcessInstance<T>) marshaller.unmarshallProcessInstance(data, process, mode);
        if (version != null) {
            ((AbstractProcessInstance<T>) instance).setVersion(version);
        }
        connectProcessInstance(instance.id(), instance);
        return instance;
    }

    @Override
    public void update(String id, ProcessInstance<T> instance) {
        updateStorage(id, instance, false);
    }

    @Override
    public void remove(String processInstanceId) {
        cache.remove(processInstanceId);
        List<String> events = clearEventTypes(cache.get(this.eventKey), processInstanceId);
        cache.put(this.eventKey, toBytes(events));
    }

    @Override
    public void create(String id, ProcessInstance<T> instance) {
        updateStorage(id, instance, true);
    }

    protected void updateStorage(String id, ProcessInstance<T> instance, boolean checkDuplicates) {
        if (isActive(instance) || instance.status() == ProcessInstance.STATE_PENDING) {
            byte[] data = marshaller.marshallProcessInstance(instance);
            if (checkDuplicates) {
                byte[] existing = cache.putIfAbsent(id, data);
                if (existing != null) {
                    throw new ProcessInstanceDuplicatedException(id);
                } else if (this.lock) {
                    ((AbstractProcessInstance<T>) instance).setVersion(1);
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

            List<String> events = clearEventTypes(cache.get(this.eventKey), id);
            events.addAll(getUniqueEvents(instance));
            cache.put(this.eventKey, toBytes(events));

            connectProcessInstance(id, instance);
        }
    }

    private void connectProcessInstance(String id, ProcessInstance<T> instance) {
        if (this.lock) {
            reloadWithLock(id, instance);
        } else {
            reload(id, instance);
        }
    }

    private void reloadWithLock(String id, ProcessInstance<T> instance) {
        Supplier<byte[]> supplier = () -> {
            MetadataValue<byte[]> versionedCache = cache.getWithMetadata(id);
            ((AbstractProcessInstance<T>) instance).setVersion(versionedCache.getVersion());
            return versionedCache.getValue();
        };
        ((AbstractProcessInstance<?>) instance).internalSetReloadSupplier(marshaller.createdReloadFunction(supplier));
    }

    private void reload(String id, ProcessInstance<T> instance) {
        Supplier<byte[]> supplier = () -> cache.get(id);
        ((AbstractProcessInstance<?>) instance).internalSetReloadSupplier(marshaller.createdReloadFunction(supplier));
    }

    @Override
    public boolean exists(String id) {
        return cache.containsKey(id);
    }

    @Override
    public boolean lock() {
        return this.lock;
    }

    @Override
    public Stream<ProcessInstance<T>> waitingForEventType(String eventType, ProcessInstanceReadMode mode) {
        byte[] eventData = cache.get(this.eventKey);
        if (eventData == null) {
            return Collections.<ProcessInstance<T>> emptyList().stream();
        }
        String list = new String(eventData);
        List<String> processInstancesId = Stream.of(list.split(","))
                .filter(e -> e.startsWith(eventType + EVENT_SEPARATOR))
                .map(e -> e.substring(e.indexOf(EVENT_SEPARATOR) + EVENT_SEPARATOR.length()))
                .toList();

        List<ProcessInstance<T>> waitingInstances = new ArrayList<>();
        for (String processInstanceId : processInstancesId) {
            findById(processInstanceId).ifPresent(waitingInstances::add);
        }
        return waitingInstances.stream();
    }

    private byte[] toBytes(List<String> events) {
        return String.join(",", events).getBytes();
    }

    private List<String> clearEventTypes(byte[] eventData, String processInstanceId) {
        String list = eventData != null ? new String(eventData) : new String();
        return Stream.of(list.split(","))
                .filter(e -> !e.endsWith(EVENT_SEPARATOR + processInstanceId))
                .collect(Collectors.toCollection(ArrayList::new));
    }

}
