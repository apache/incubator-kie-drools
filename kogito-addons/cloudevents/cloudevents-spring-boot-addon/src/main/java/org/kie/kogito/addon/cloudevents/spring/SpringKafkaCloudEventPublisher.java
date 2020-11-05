/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package org.kie.kogito.addon.cloudevents.spring;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.kie.kogito.event.KogitoEventStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;

@Component
public class SpringKafkaCloudEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(SpringKafkaCloudEventPublisher.class.getName());

    private final ReceiverOptions<Integer, String> receiverOptions;
    private final SimpleDateFormat dateFormat;

    private final String topic;

    public SpringKafkaCloudEventPublisher(
            @Value(value = "${spring.kafka.bootstrap-servers}") String kafkaBootstrapAddress,
            @Value(value = "${spring.kafka.consumer.group-id}") String groupId,
            @Value(value = "${kogito.addon.cloudevents.kafka." + KogitoEventStreams.INCOMING + ":" + KogitoEventStreams.INCOMING + "}") String kafkaTopicName) {
        this.topic = kafkaTopicName;

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBootstrapAddress);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        receiverOptions = ReceiverOptions.create(props);
        dateFormat = new SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy");
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
    @Qualifier(KogitoEventStreams.PUBLISHER)
    public Flux<String> makeConsumer() {
        ReceiverOptions<Integer, String> options = receiverOptions.subscription(Collections.singleton(topic))
                .addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));

        ConnectableFlux<String> broadcast = KafkaReceiver.create(options).receive().map(record -> {
            ReceiverOffset offset = record.receiverOffset();
            log.info("Received message: topic-partition={} offset={} timestamp={} key={} value={}\n",
                      offset.topicPartition(),
                      offset.offset(),
                      dateFormat.format(new Date(record.timestamp())),
                      record.key(),
                      record.value());
            offset.acknowledge();

            return record.value();
        }).publish();

        return broadcast.autoConnect();
    }
}
