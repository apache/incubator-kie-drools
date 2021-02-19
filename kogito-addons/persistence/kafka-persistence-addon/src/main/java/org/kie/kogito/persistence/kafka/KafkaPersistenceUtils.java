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

import java.util.List;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.String.format;

public class KafkaPersistenceUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaPersistenceUtils.class);
    private static final String TOPIC = "kogito.process.%s";
    private static final String STORE = "kogito-%s-store";

    private KafkaPersistenceUtils() {
    }

    public static String topicName(String processId) {
        return format(TOPIC, processId);
    }
    public static String storeName(String processId) {
        return format(STORE, processId);
    }
    
    public static Topology createTopologyForProcesses(List<String> processes){
        StreamsBuilder builder = new StreamsBuilder();
        processes.forEach(p -> {
            builder.globalTable(topicName(p), Materialized.<String, byte[], KeyValueStore<Bytes, byte[]>>as(storeName(p))
                    .withKeySerde(Serdes.String())
                    .withValueSerde(Serdes.ByteArray()));
            LOGGER.info("Created Kafka Stream GlobalTable for process {}", p);
        });
        return builder.build();
    }
}
