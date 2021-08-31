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
package org.kie.kogito.test.springboot.kafka;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Kafka client for Kogito Example tests.
 */
@Component
@ConditionalOnProperty(name = "spring.kafka.bootstrap-servers")
public class KafkaTestClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaTestClient.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaBootstrapServers;

    private KafkaTemplate<String, String> producer;

    private KafkaMessageListenerContainer<String, String> container;

    @PostConstruct
    public void setup() {
        producer = new KafkaTemplate<>(producerFactory());
    }

    public KafkaTestClient() {
    }

    public KafkaTestClient(KafkaTemplate<String, String> producer) {
        this.producer = producer;
    }

    private ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        config.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "100");
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, KafkaTestClient.class.getName() + "Consumer");
        return new DefaultKafkaConsumerFactory<>(config);
    }

    private ProducerFactory<String, String> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapServers);
        config.put(ProducerConfig.ACKS_CONFIG, "1");
        config.put(ProducerConfig.CLIENT_ID_CONFIG, KafkaTestClient.class.getName() + "Producer");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        return new DefaultKafkaProducerFactory<>(config);
    }

    public void consume(Collection<String> topics, Consumer<String> callback) {
        if (container == null) {
            ContainerProperties containerProperties = new ContainerProperties(topics.toArray(new String[] {}));
            container = new KafkaMessageListenerContainer(consumerFactory(), containerProperties);
            container.setupMessageListener(new MessageListener<String, String>() {

                @Override
                public void onMessage(ConsumerRecord<String, String> record) {
                    callback.accept(record.value());
                }
            });
            container.setBeanName("kafka-test-client");
            container.start();
            try {
                // Needs to wait for Consumer to get started. The only way to get a callback is using an SB application
                // event, which we're avoiding to register the container as a bean to not interfere with the SB application
                // context from the app under test.
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            container.stop();
            container = null;
            consume(topics, callback);
        }
    }

    public void consume(String topic, Consumer<String> callback) {
        consume(Collections.singletonList(topic), callback);
    }

    public void produce(String data, String topic) {
        LOGGER.info("Publishing event with data {} for topic {}", data, topic);
        producer.send(topic, data).addCallback(produceCallback());
        producer.flush();
    }

    public ListenableFutureCallback<SendResult<String, String>> produceCallback() {
        return new ListenableFutureCallback<>() {

            @Override
            public void onFailure(Throwable throwable) {
                LOGGER.error("Event publishing failed", throwable);
            }

            @Override
            public void onSuccess(SendResult<String, String> result) {
                LOGGER.info("Event published {}", result.getRecordMetadata());
            }
        };
    }

    @PreDestroy
    public void shutdown() {
        if (producer != null) {
            producer.destroy();
        }
        if (container != null) {
            container.stop();
        }
    }
}