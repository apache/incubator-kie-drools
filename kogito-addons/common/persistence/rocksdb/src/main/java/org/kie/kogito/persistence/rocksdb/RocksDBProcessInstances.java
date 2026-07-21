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
package org.kie.kogito.persistence.rocksdb;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jbpm.flow.serialization.MarshallerContextName;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerService;
import org.kie.kogito.Model;
import org.kie.kogito.internal.process.runtime.HeadersPersistentConfig;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

import static java.util.stream.Collectors.toCollection;

public class RocksDBProcessInstances<T extends Model> implements MutableProcessInstances<T> {
    private final String EVENT_SEPARATOR = "::";
    private final Process<T> process;
    private final ProcessInstanceMarshallerService marshaller;
    private final RocksDB db;
    private String eventKey;

    public RocksDBProcessInstances(Process<T> process, RocksDB db) {
        this(process, db, null);
    }

    public RocksDBProcessInstances(Process<T> process, RocksDB db, HeadersPersistentConfig headersConfig) {
        this.process = process;
        marshaller = ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().withDefaultListeners()
                .withContextEntry(MarshallerContextName.MARSHALLER_HEADERS_CONFIG, headersConfig).build();
        this.db = db;
        this.eventKey = process.id() + "-" + process.version() + ".events";
    }

    private class RockSplitIterator extends AbstractSpliterator<ProcessInstance<T>> implements Closeable {

        private RocksIterator iterator;
        private ProcessInstanceReadMode mode;

        protected RockSplitIterator(RocksIterator iterator, ProcessInstanceReadMode mode) {
            super(Integer.MAX_VALUE, 0);
            this.iterator = iterator;
            this.mode = mode;
            iterator.seekToFirst();
        }

        @Override
        public boolean tryAdvance(Consumer<? super ProcessInstance<T>> action) {
            boolean hasNext = iterator.isValid();
            if (!hasNext) {
                iterator.close();
                return false;
            }

            while (iterator.isValid()) {
                if (eventKey.equals(new String(iterator.key()))) {
                    iterator.next();
                    continue;
                }
                action.accept(unmarshall(iterator.value(), mode));
                iterator.next();
                return true;
            }
            return false;
        }

        @Override
        public void close() {
            iterator.close();
        }
    }

    @Override
    public Optional<ProcessInstance<T>> findById(String id, ProcessInstanceReadMode mode) {
        try {
            byte[] data = db.get(id.getBytes());
            return data == null ? Optional.empty() : Optional.of(unmarshall(data, mode));
        } catch (RocksDBException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Stream<ProcessInstance<T>> stream(ProcessInstanceReadMode mode) {
        RocksDBProcessInstances<T>.RockSplitIterator iterator = new RockSplitIterator(db.newIterator(), mode);
        return StreamSupport.stream(iterator, false).onClose(iterator::close);
    }

    @Override
    public Stream<ProcessInstance<T>> waitingForEventType(String eventType, ProcessInstanceReadMode mode) {
        try {
            byte[] eventData = db.get(this.eventKey.getBytes());
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
                byte[] processData = db.get(processInstanceId.getBytes());
                waitingInstances.add(unmarshall(processData, mode));
            }
            return waitingInstances.stream();
        } catch (RocksDBException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public boolean exists(String id) {
        try {
            return db.get(id.getBytes()) != null;
        } catch (RocksDBException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void create(String id, ProcessInstance<T> instance) {
        update(id, instance);
    }

    @Override
    public synchronized void update(String id, ProcessInstance<T> instance) {
        try {
            db.put(id.getBytes(), marshaller.marshallProcessInstance(instance));
            connectProcessInstance(instance);
            Set<String> events = clearEventTypes(instance.id());
            events.addAll(getUniqueEvents(instance));
            db.put(this.eventKey.getBytes(), toBytes(events));
        } catch (RocksDBException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private byte[] toBytes(Set<String> events) {
        return String.join(",", events).getBytes();
    }

    private Set<String> clearEventTypes(String processInstanceId) throws RocksDBException {
        byte[] eventData = db.get(this.eventKey.getBytes());
        String list = eventData != null ? new String(eventData) : new String();
        return Stream.of(list.split(",")).filter(e -> !e.endsWith(EVENT_SEPARATOR + processInstanceId)).collect(toCollection(HashSet::new));
    }

    @Override
    public synchronized void remove(String id) {
        try {
            db.delete(id.getBytes());
            Set<String> events = clearEventTypes(id);
            db.put(this.eventKey.getBytes(), toBytes(events));
        } catch (RocksDBException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private Set<String> getUniqueEvents(ProcessInstance<T> instance) {
        return Stream.of(((AbstractProcessInstance<T>) instance).internalGetProcessInstance().getEventTypes())
                .map(e -> e + EVENT_SEPARATOR + instance.id())
                .collect(Collectors.toCollection(HashSet::new));
    }

    private ProcessInstance<T> unmarshall(byte[] data, ProcessInstanceReadMode mode) {
        AbstractProcessInstance<?> pi = (AbstractProcessInstance<?>) marshaller.unmarshallProcessInstance(data, process, mode);
        connectProcessInstance(pi);
        return (ProcessInstance<T>) pi;
    }

    private void connectProcessInstance(ProcessInstance<?> pi) {
        ((AbstractProcessInstance<?>) pi).internalSetReloadSupplier(marshaller.createdReloadFunction(() -> {
            try {
                return db.get(pi.id().getBytes());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
    }

}
