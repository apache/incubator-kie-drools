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
package org.kie.kogito.persistence.kafka;

import java.util.Optional;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.jbpm.flow.serialization.ProcessInstanceMarshallerService;
import org.kie.kogito.process.MutableProcessInstances;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceDuplicatedException;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.impl.AbstractProcessInstance;

import static java.lang.String.format;
import static org.kie.kogito.persistence.kafka.KafkaPersistenceUtils.topicName;

public class KafkaProcessInstances implements MutableProcessInstances {

    private Process<?> process;
    private KafkaProducer<String, byte[]> producer;
    private String topic;
    private ReadOnlyKeyValueStore<String, byte[]> store;
    private ProcessInstanceMarshallerService marshaller;
    private CountDownLatch latch = new CountDownLatch(1);

    public KafkaProcessInstances(Process<?> process, KafkaProducer<String, byte[]> producer) {
        this.process = process;
        this.topic = topicName();
        this.producer = producer;
        setMarshaller(ProcessInstanceMarshallerService.newBuilder().withDefaultObjectMarshallerStrategies().build());
    }

    protected Process<?> getProcess() {
        return process;
    }

    protected ReadOnlyKeyValueStore<String, byte[]> getStore() {
        if (store != null) {
            return store;
        }

        return getStoreAwait();
    }

    protected void setStore(ReadOnlyKeyValueStore<String, byte[]> store) {
        this.store = store;
        this.latch.countDown();
    }

    private ReadOnlyKeyValueStore<String, byte[]> getStoreAwait() {
        try {
            if (latch.await(1, TimeUnit.MINUTES)) {
                if (store == null) {
                    throw new RuntimeException("Failed to obtain Kafka Store for process: " + process.id());
                } else {
                    return store;
                }
            } else {
                throw new RuntimeException("Timeout waiting to obtain Kafka Store for process: " + process.id());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to obtain Kafka Store for process: " + process.id(), e);
        }
    }

    protected void setMarshaller(ProcessInstanceMarshallerService marshaller) {
        this.marshaller = marshaller;
    }

    @Override
    public boolean exists(String id) {
        return getProcessInstanceById(id).isPresent();
    }

    protected Optional<byte[]> getProcessInstanceById(String id) {
        return Optional.ofNullable(getStore().get(getKeyForProcessInstance(id)));
    }

    protected String getKeyForProcessInstance(String id) {
        return format("%s-%s", getProcess().id(), id);
    }

    protected void sendKafkaRecord(String id, byte[] data) throws ExecutionException, InterruptedException {
        producer.send(new ProducerRecord<>(topic, getKeyForProcessInstance(id), data)).get();
    }

    @Override
    public void create(String id, ProcessInstance instance) {
        if (isActive(instance)) {
            if (getProcessInstanceById(id).isPresent()) {
                throw new ProcessInstanceDuplicatedException(id);
            }
            try {
                sendKafkaRecord(id, marshaller.marshallProcessInstance(instance));
            } catch (Exception e) {
                throw new RuntimeException("Unable to persist process instance id: " + id, e);
            }
        }
    }

    @Override
    public void update(String id, ProcessInstance instance) {
        if (isActive(instance)) {
            byte[] data = marshaller.marshallProcessInstance(instance);
            try {
                sendKafkaRecord(id, data);
                disconnect(instance);
            } catch (Exception e) {
                throw new RuntimeException("Unable to update process instance id: " + id, e);
            }
        }
    }

    @Override
    public void remove(String id) {
        try {
            sendKafkaRecord(id, null);
        } catch (Exception e) {
            throw new RuntimeException("Unable to remove process instance id: " + id, e);
        }
    }

    @Override
    public Optional<ProcessInstance<?>> findById(String id, ProcessInstanceReadMode mode) {
        return getProcessInstanceById(id).map(marshaller.createUnmarshallFunction(process, mode));
    }

    @Override
    public Stream<ProcessInstance<?>> stream(ProcessInstanceReadMode mode) {
        KeyValueIterator<String, byte[]> iterator = getStore().prefixScan(getProcess().id(), Serdes.String().serializer());
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false)
                .map(k -> k.value)
                .map(marshaller.createUnmarshallFunction(process, mode)).onClose(iterator::close);
    }

    protected void disconnect(ProcessInstance<?> instance) {
        ((AbstractProcessInstance<?>) instance).internalRemoveProcessInstance(marshaller.createdReloadFunction(() -> getProcessInstanceById(instance.id()).orElseThrow()));
    }
}
