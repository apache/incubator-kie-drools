/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.hacep.core.infra.utils;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.kie.remote.RemoteFactHandle;
import org.kie.hacep.Config;
import org.kie.hacep.EnvConfig;
import org.kie.remote.message.ControlMessage;
import org.kie.remote.message.FactCountMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.remote.util.SerializationUtil.deserialize;

public class ConsumerUtils {

    private static Logger logger = LoggerFactory.getLogger(ConsumerUtils.class);

    public static ControlMessage getLastEvent( String topic, Integer pollTimeout) {
        return getLastEvent(topic, Config.getConsumerConfig("LastEventConsumer"), pollTimeout);
    }

    public Map<TopicPartition, Long> getOffsets(String topic) {
        KafkaConsumer consumer = new KafkaConsumer(Config.getConsumerConfig("OffsetConsumer"));
        consumer.subscribe(Arrays.asList(topic));
        List<PartitionInfo> infos = consumer.partitionsFor(topic);
        List<TopicPartition> tps = new ArrayList<>();
        for (PartitionInfo info : infos) {
            tps.add(new TopicPartition(topic, info.partition()));
        }
        Map<TopicPartition, Long> offsets = consumer.endOffsets(tps);
        consumer.close();
        return offsets;
    }

    public static ControlMessage getLastEvent( String topic, Properties properties, Integer pollTimeout) {
        KafkaConsumer consumer = new KafkaConsumer(properties);
        List<PartitionInfo> infos = consumer.partitionsFor(topic);
        List<TopicPartition> partitions = new ArrayList<>();
        if (infos != null) {
            for (PartitionInfo partition : infos) {
                partitions.add(new TopicPartition(topic, partition.partition()));
            }
        }
        consumer.assign(partitions);

        Map<TopicPartition, Long> offsets = consumer.endOffsets(partitions);
        Long lastOffset = 0l;
        for (Map.Entry<TopicPartition, Long> entry : offsets.entrySet()) {
            lastOffset = entry.getValue();
        }
        if(lastOffset == 0){
            lastOffset = 1l;// this is to start the seek with offset -1 on empty topic
        }
        Set<TopicPartition> assignments = consumer.assignment();
        for (TopicPartition part : assignments) {
            consumer.seek(part, lastOffset - 1);
        }

        ControlMessage lastMessage = new ControlMessage();
        try {
            ConsumerRecords records = consumer.poll(Duration.of(pollTimeout, ChronoUnit.MILLIS));
            for (Object item : records) {
                ConsumerRecord<String, byte[]> record = (ConsumerRecord<String, byte[]>) item;
                lastMessage = deserialize(record.value());
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(),
                         ex);
        } finally {
            consumer.close();
        }
        return lastMessage;
    }

    public static FactCountMessage getFactCount(RemoteFactHandle factHandle, EnvConfig config, Properties properties) {
        KafkaConsumer consumer = new KafkaConsumer(properties);
        List<PartitionInfo> infos = consumer.partitionsFor(config.getKieSessionInfosTopicName());
        List<TopicPartition> partitions = new ArrayList<>();
        if (infos != null) {
            for (PartitionInfo partition : infos) {
                partitions.add(new TopicPartition(config.getKieSessionInfosTopicName(), partition.partition()));
            }
        }
        consumer.assign(partitions);

        Map<TopicPartition, Long> offsets = consumer.endOffsets(partitions);
        Long lastOffset = 0l;
        for (Map.Entry<TopicPartition, Long> entry : offsets.entrySet()) {
            lastOffset = entry.getValue();
        }
        if(lastOffset == 0){
            lastOffset = 1l;// this is to start the seek with offset -1 on empty topic
        }
        Set<TopicPartition> assignments = consumer.assignment();
        for (TopicPartition part : assignments) {
            consumer.seek(part, lastOffset - 1);
        }

        FactCountMessage lastMessage = new FactCountMessage();
        try {
            ConsumerRecords records = consumer.poll(Duration.of(Config.DEFAULT_POLL_TIMEOUT, ChronoUnit.MILLIS));
            for (Object item : records) {
                ConsumerRecord<String, byte[]> record = (ConsumerRecord<String, byte[]>) item;
                if(record.key().equals(factHandle.getId())) {
                    lastMessage = deserialize(record.value());
                    break;
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        } finally {
            consumer.close();
        }
        return lastMessage;
    }

}
