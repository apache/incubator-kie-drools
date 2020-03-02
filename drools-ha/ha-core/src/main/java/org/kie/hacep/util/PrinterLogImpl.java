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
package org.kie.hacep.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.kie.hacep.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrinterLogImpl implements Printer {

    private static Logger logger = LoggerFactory.getLogger(PrinterLogImpl.class);

    @Override
    public void prettyPrinter(String caller, ConsumerRecord consumerRecord,
                              boolean processed) {
        if (consumerRecord != null) {
            logger.info("Caller:{} - Processed:{} - Topic: {} - Partition: {} - Offset: {} - Value: {}\n",
                        caller,
                        processed,
                        consumerRecord.topic(),
                        consumerRecord.partition(),
                        consumerRecord.offset(),
                        !(consumerRecord.value() instanceof byte[]) ? consumerRecord.value() : "bytes[]");
        }

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

}
