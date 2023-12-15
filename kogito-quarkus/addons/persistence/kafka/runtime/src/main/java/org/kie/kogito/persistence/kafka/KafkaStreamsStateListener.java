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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import static org.kie.kogito.persistence.kafka.KafkaPersistenceUtils.storeName;

@ApplicationScoped
public class KafkaStreamsStateListener implements KafkaStreams.StateListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsStateListener.class);

    private Map<String, KafkaProcessInstances> instances = new ConcurrentHashMap<>();

    private KafkaStreams streams;

    protected KafkaStreams getKafkaStreams() {
        return streams;
    }

    @Inject
    public void setKafkaStreams(KafkaStreams streams) {
        this.streams = streams;
        this.streams.setStateListener(this);
    }

    @PreDestroy
    public void close() {
        instances.clear();
    }

    protected Collection<KafkaProcessInstances> getInstances() {
        return instances.values();
    }

    @Override
    public void onChange(KafkaStreams.State newState, KafkaStreams.State oldState) {
        LOGGER.debug("Received change from KafkaStreams to new state: {}", newState);
        if (newState == KafkaStreams.State.RUNNING) {
            instances.forEach((id, pi) -> {
                LOGGER.info("Creating store for process: {}", id);
                setStore(pi);
            });
        }
    }

    private void setStore(KafkaProcessInstances pi) {
        pi.setStore(streams.store(StoreQueryParameters.fromNameAndType(storeName(), QueryableStoreTypes.keyValueStore())));
    }

    public void addProcessInstances(KafkaProcessInstances pi) {
        LOGGER.debug("Adding process instance into listener for process: {}", pi.getProcess().id());
        if (streams.state() == KafkaStreams.State.RUNNING) {
            setStore(pi);
        }
        instances.put(pi.getProcess().id(), pi);
    }
}
