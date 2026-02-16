/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.task.notification.spring;

import java.util.Collection;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class SpringNotificationEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(SpringNotificationEventPublisher.class);

    @Autowired
    private KafkaTemplate<String, DataEvent<?>> emitter;

    @Value("${kogito.events.deadline.topic:kogito-deadline-events}")
    private String topic;

    @Override
    public void publish(DataEvent<?> event) {
        if (event.getType().startsWith("UserTaskInstanceDeadline")) {
            logger.debug("About to publish event {} to Kafka topic {}", event, topic);
            try {
                emitter.send(topic, event);
                logger.debug("Successfully published event {} to topic {}", event, topic);
            } catch (Exception e) {
                logger.error("Error while publishing event to Kafka topic {} for event {}", topic, event, e);
            }
        } else {
            logger.debug("Unknown type of event '{}', ignoring", event.getType());
        }
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        for (DataEvent<?> event : events) {
            publish(event);
        }
    }
}
