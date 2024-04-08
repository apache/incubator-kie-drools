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
import java.util.Optional;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.jbpm.flow.serialization.ProcessInstanceMarshallerService;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksIterator;

public class RocksDBProcessInstances<T> implements MutableProcessInstances<T> {

    private final Process<T> process;
    private final ProcessInstanceMarshallerService marshaller;
    private final RocksDB db;

    public RocksDBProcessInstances(Process<T> process, RocksDB db) {
        this.process = process;
        marshaller = ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().withDefaultListeners().build();
        this.db = db;
    }

    private class RockSplitIterator extends AbstractSpliterator<ProcessInstance<T>> implements Closeable {

        private final RocksIterator iterator;

        protected RockSplitIterator(RocksIterator iterator) {
            super(Integer.MAX_VALUE, 0);
            this.iterator = iterator;
            iterator.seekToFirst();
        }

        @Override
        public boolean tryAdvance(Consumer<? super ProcessInstance<T>> action) {
            boolean hasNext = iterator.isValid();
            if (hasNext) {
                action.accept(unmarshall(iterator.value()));
                iterator.next();
                hasNext = iterator.isValid();
            }
            return hasNext;
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
            return data == null ? Optional.empty() : Optional.of(unmarshall(data));
        } catch (RocksDBException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public Stream<ProcessInstance<T>> stream(ProcessInstanceReadMode mode) {
        RocksDBProcessInstances<T>.RockSplitIterator iterator = new RockSplitIterator(db.newIterator());
        return StreamSupport.stream(iterator, false).onClose(iterator::close);
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
    public void update(String id, ProcessInstance<T> instance) {
        try {
            db.put(id.getBytes(), marshaller.marshallProcessInstance(instance));
        } catch (RocksDBException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void remove(String id) {
        try {
            db.delete(id.getBytes());
        } catch (RocksDBException ex) {
            throw new IllegalStateException(ex);
        }
    }

    @SuppressWarnings("unchecked")
    private ProcessInstance<T> unmarshall(byte[] data) {
        return (ProcessInstance<T>) marshaller.unmarshallProcessInstance(data, process);
    }
}
