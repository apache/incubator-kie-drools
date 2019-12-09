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
package org.kie.remote.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerUtils {

  private static Logger logger = LoggerFactory.getLogger(ConsumerUtils.class);

  private ConsumerUtils() { }

  public static KafkaConsumer getConsumer(String topic, Properties properties) {
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
    if (lastOffset == 0) {
      lastOffset = 1l;// this is to start the seek with offset -1 on empty topic
    }
    Set<TopicPartition> assignments = consumer.assignment();
    for (TopicPartition part : assignments) {
      consumer.seek(part, lastOffset - 1);
    }
    return consumer;
  }

}