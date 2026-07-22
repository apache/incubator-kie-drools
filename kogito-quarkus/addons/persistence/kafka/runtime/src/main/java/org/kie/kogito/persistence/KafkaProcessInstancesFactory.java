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
package org.kie.kogito.persistence;

import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.kie.kogito.persistence.kafka.KafkaProcessInstances;
import org.kie.kogito.persistence.kafka.KafkaStreamsStateListener;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstancesFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.common.annotation.Identifier;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class KafkaProcessInstancesFactory implements ProcessInstancesFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProcessInstancesFactory.class);

    KafkaStreamsStateListener stateListener;
    KafkaProducer<String, byte[]> producer;

    @Inject
    public void setStateListener(KafkaStreamsStateListener stateListener) {
        this.stateListener = stateListener;
    }

    @Inject
    public void setKafkaConfig(@Identifier("default-kafka-broker") Map<String, Object> kafkaConfig) {
        producer = new KafkaProducer<>(kafkaConfig, new StringSerializer(), new ByteArraySerializer());
    }

    @PreDestroy
    public void stop() {
        if (producer != null) {
            producer.close();
        }
    }

    public KafkaProcessInstances createProcessInstances(Process<?> process) {
        try {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("Creating KafkaProcessInstances for process: {}", process.id());
            }
            KafkaProcessInstances pi = new KafkaProcessInstances(process, producer);
            stateListener.addProcessInstances(pi);
            return pi;
        } catch (Exception ex) {
            LOGGER.error("Error creating KafkaProcessInstances for process: {}", process.id(), ex);
            throw new RuntimeException("Error creating KafkaProcessInstances for process: " + process.id(), ex);
        }
    }

}
