/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.persistence.kafka;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.drools.core.io.impl.ClassPathResource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.auth.IdentityProviders;
import org.kie.kogito.auth.SecurityPolicy;
import org.kie.kogito.persistence.KogitoProcessInstancesFactory;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.ProcessInstanceReadMode;
import org.kie.kogito.process.ProcessInstances;
import org.kie.kogito.process.WorkItem;
import org.kie.kogito.process.bpmn2.BpmnProcess;
import org.kie.kogito.process.bpmn2.BpmnVariables;
import org.kie.kogito.testcontainers.KogitoKafkaContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.entry;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE;
import static org.kie.kogito.persistence.kafka.KafkaPersistenceUtils.createTopologyForProcesses;
import static org.kie.kogito.process.ProcessInstance.STATE_COMPLETED;
import static org.kie.kogito.process.ProcessInstance.STATE_ERROR;

@Testcontainers
public class KafkaProcessInstancesIT {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProcessInstancesIT.class);

    @Container
    KogitoKafkaContainer kafka = new KogitoKafkaContainer();

    KafkaProcessInstancesFactory factory;

    KafkaStreamsStateListener listener = new KafkaStreamsStateListener();

    @BeforeEach
    void start() {
        factory = new KafkaProcessInstancesFactory();
        factory.setKafkaConfig(singletonMap(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers()));
        factory.setStateListener(listener);
    }

    @AfterEach
    void stop() {
        if (factory != null) {
            factory.stop();
        }
        if (listener.getKafkaStreams() != null) {
            listener.getKafkaStreams().close();
            listener.getKafkaStreams().cleanUp();
        }
    }

    @Test
    void testFindByIdReadMode() {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask-Script.bpmn2")).get(0);

        listener.setKafkaStreams(createStreams(process));
        process.setProcessInstancesFactory(factory);
        process.configure();
        listener.getKafkaStreams().start();

        ProcessInstances<BpmnVariables> instances = process.instances();
        assertThat(instances.size()).isZero();

        ProcessInstance<BpmnVariables> mutablePi = process.createInstance(BpmnVariables.create(singletonMap("var", "value")));

        mutablePi.start();
        assertThat(mutablePi.status()).isEqualTo(STATE_ERROR);
        assertThat(mutablePi.error()).hasValueSatisfying(error -> {
            assertThat(error.errorMessage()).contains("java.lang.NullPointerException");
            assertThat(error.failedNodeId()).isEqualTo("ScriptTask_1");
        });
        assertThat(mutablePi.variables().toMap()).containsExactly(entry("var", "value"));

        await().until(() -> instances.values().size() == 1);

        ProcessInstance<BpmnVariables> pi = instances.findById(mutablePi.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> pi.abort());

        ProcessInstance<BpmnVariables> readOnlyPi = instances.findById(mutablePi.id(), ProcessInstanceReadMode.READ_ONLY).get();
        assertThat(readOnlyPi.status()).isEqualTo(STATE_ERROR);
        assertThat(readOnlyPi.error()).hasValueSatisfying(error -> {
            assertThat(error.errorMessage()).contains("java.lang.NullPointerException");
            assertThat(error.failedNodeId()).isEqualTo("ScriptTask_1");
        });
        assertThat(readOnlyPi.variables().toMap()).containsExactly(entry("var", "value"));
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> readOnlyPi.abort());

        instances.findById(mutablePi.id()).get().abort();
        assertThat(instances.size()).isZero();
    }

    @Test
    void testValuesReadMode() {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        listener.setKafkaStreams(createStreams(process));
        process.setProcessInstancesFactory(factory);
        process.configure();
        listener.getKafkaStreams().start();

        ProcessInstances<BpmnVariables> instances = process.instances();
        assertThat(instances.size()).isZero();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(singletonMap("test", "test")));

        processInstance.start();

        await().until(() -> instances.values().size() == 1);

        ProcessInstance<BpmnVariables> pi = instances.values().stream().findFirst().get();
        assertThatExceptionOfType(UnsupportedOperationException.class).isThrownBy(() -> pi.abort());
        instances.values(ProcessInstanceReadMode.MUTABLE).stream().findFirst().get().abort();
        assertThat(instances.size()).isZero();
    }

    @Test
    void testBasicFlow() {
        BpmnProcess process = BpmnProcess.from(new ClassPathResource("BPMN2-UserTask.bpmn2")).get(0);
        listener.setKafkaStreams(createStreams(process));
        process.setProcessInstancesFactory(factory);
        process.configure();
        listener.getKafkaStreams().start();

        ProcessInstances<BpmnVariables> instances = process.instances();
        assertThat(instances.size()).isZero();

        ProcessInstance<BpmnVariables> processInstance = process.createInstance(BpmnVariables.create(singletonMap("test", "test")));

        processInstance.start();
        assertEquals(STATE_ACTIVE, processInstance.status());

        await().until(() -> instances.values().size() == 1);

        SecurityPolicy asJohn = SecurityPolicy.of(IdentityProviders.of("john"));

        assertThat(instances.values().iterator().next().workItems(asJohn)).hasSize(1);

        List<WorkItem> workItems = processInstance.workItems(asJohn);
        assertThat(workItems).hasSize(1);
        WorkItem workItem = workItems.get(0);
        assertEquals("john", workItem.getParameters().get("ActorId"));
        processInstance.completeWorkItem(workItem.getId(), null, asJohn);
        assertEquals(STATE_COMPLETED, processInstance.status());
        assertThat(instances.size()).isZero();
    }

    KafkaStreams createStreams(Process process) {
        Topology topology = createTopologyForProcesses(Arrays.asList(process.id()));
        KafkaStreams streams = new KafkaStreams(topology, getStreamsConfig());
        streams.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> LOGGER.error("Kafka persistence error: " + throwable.getMessage(), throwable));
        streams.cleanUp();
        return streams;
    }

    Properties getStreamsConfig() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, "kogito");
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        return properties;
    }

    private class KafkaProcessInstancesFactory extends KogitoProcessInstancesFactory {

    }
}
