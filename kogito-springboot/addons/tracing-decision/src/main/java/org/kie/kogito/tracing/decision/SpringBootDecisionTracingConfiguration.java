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
package org.kie.kogito.tracing.decision;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.kie.kogito.Application;
import org.kie.kogito.config.ConfigBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class SpringBootDecisionTracingConfiguration {

    private final String kafkaBootstrapAddress;
    private final String kafkaTopicName;
    private final int kafkaTopicPartitions;
    private final short kafkaTopicReplicationFactor;

    public SpringBootDecisionTracingConfiguration(
            @Value(value = "${kogito.addon.tracing.decision.kafka.bootstrapAddress}") String kafkaBootstrapAddress,
            @Value(value = "${kogito.addon.tracing.decision.kafka.topic.name:kogito-tracing-decision}") String kafkaTopicName,
            @Value(value = "${kogito.addon.tracing.decision.kafka.topic.partitions:1}") int kafkaTopicPartitions,
            @Value(value = "${kogito.addon.tracing.decision.kafka.topic.replicationFactor:1}") short kafkaTopicReplicationFactor) {
        this.kafkaBootstrapAddress = kafkaBootstrapAddress;
        this.kafkaTopicName = kafkaTopicName;
        this.kafkaTopicPartitions = kafkaTopicPartitions;
        this.kafkaTopicReplicationFactor = kafkaTopicReplicationFactor;
    }

    @Bean
    public SpringBootDecisionTracingCollector collector(
            final SpringBootTraceEventEmitter eventEmitter,
            final ConfigBean configBean,
            final Application application,
            @Value(value = "${kogito.addon.tracing.decision.asyncEnabled:true}") final boolean asyncEnabled) {
        if (asyncEnabled) {
            return new SpringBootDecisionTracingCollectorAsync(eventEmitter, configBean, application);
        } else {
            return new SpringBootDecisionTracingCollector(eventEmitter, configBean, application);
        }
    }

    /**
     * Defining a {@link KafkaAdmin} bean allows to automatically add topic to the broker via {@link NewTopic} beans
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        return new KafkaAdmin(configs);
    }

    /**
     * {@link NewTopic} bean to create the addon Kafka topic (if the topic already exists this is ignored)
     */
    @Bean
    public NewTopic newTopic() {
        return new NewTopic(kafkaTopicName, kafkaTopicPartitions, kafkaTopicReplicationFactor);
    }

    /**
     * Configure producers
     */
    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Configure {@link KafkaTemplate} object used by producers to send messages
     */
    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean(name = "kogitoTracingDecisionAddonTaskExecutor")
    @ConditionalOnProperty(value = "kogito.addon.tracing.decision.asyncEnabled", havingValue = "true", matchIfMissing = true)
    public Executor threadPoolTaskExecutor() {
        return Executors.newSingleThreadExecutor(r -> new Thread(r, "kogito-tracing"));
    }
}
