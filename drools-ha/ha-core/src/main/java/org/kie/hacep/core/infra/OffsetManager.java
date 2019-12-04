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
package org.kie.hacep.core.infra;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Used when is required to store the current offset between operation in the cluster
 */
public class OffsetManager {

    private static Logger logger = LoggerFactory.getLogger(OffsetManager.class);
    private static Map<TopicPartition, Long> controlTopicOffset = Collections.emptyMap();
    private static Map<TopicPartition, Long> eventsTopicOffset = Collections.emptyMap();

    public static void saveControlTopic(Map<TopicPartition, Long> newOffset) {
        controlTopicOffset = newOffset;
    }

    public static void saveEventsTopic(Map<TopicPartition, Long> newOffset) {
        eventsTopicOffset = newOffset;
    }

    public static Map<TopicPartition, Long> getControlTopicOffset() {
        return controlTopicOffset;
    }

    public static Map<TopicPartition, Long> getEventsTopicOffset() {
        return eventsTopicOffset;
    }

    public static Properties load() {
        Properties prop = null;
        InputStream input = null;
        try {
            //todo store on infinispan/etcd
            input = new FileInputStream("/tmp/offsets.properties");
            prop = new Properties();
            prop.load(input);
        } catch (IOException ex) {
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),
                                 e);
                }
            }
        }
        return prop;
    }

    public static void store(Map<TopicPartition, OffsetAndMetadata> offsetAndMetadataMap) {
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            //todo store on infinispan/etcd
            output = new FileOutputStream("/tmp/offsets.properties");
            for (Map.Entry<TopicPartition, OffsetAndMetadata> entry : offsetAndMetadataMap.entrySet()) {
                prop.setProperty(entry.getKey().topic() + "-" + entry.getKey().partition(),
                                 String.valueOf(entry.getValue().offset()));
            }

            prop.store(output,
                       null);
        } catch (IOException io) {
            logger.error(io.getMessage(),
                         io);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(),
                                 e);
                }
            }
        }
    }
}
