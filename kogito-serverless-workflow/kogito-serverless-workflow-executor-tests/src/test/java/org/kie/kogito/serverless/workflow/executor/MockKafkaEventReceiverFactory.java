/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.serverless.workflow.executor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;

import io.cloudevents.CloudEvent;

public class MockKafkaEventReceiverFactory extends KafkaEventReceiverFactory {

    public static MockConsumer<byte[], CloudEvent> consumer;

    @Override
    public int ordinal() {
        return 1;
    }

    @Override
    protected Consumer<byte[], CloudEvent> createKafkaConsumer() {
        return consumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST) {
            @Override
            public void subscribe(Collection<String> topics) {
                super.subscribe(topics);
                List<TopicPartition> partitions = topics.stream().map(topic -> new TopicPartition(topic, 0)).collect(Collectors.toList());
                Map<TopicPartition, Long> partitionsBeginningMap = new HashMap<>();
                Map<TopicPartition, Long> partitionsEndMap = new HashMap<>();
                for (TopicPartition partition : partitions) {
                    partitionsBeginningMap.put(partition, 0L);
                    partitionsEndMap.put(partition, 10L);
                }
                rebalance(partitions);
                updateBeginningOffsets(partitionsBeginningMap);
                updateEndOffsets(partitionsEndMap);
            }
        };
    }
}
