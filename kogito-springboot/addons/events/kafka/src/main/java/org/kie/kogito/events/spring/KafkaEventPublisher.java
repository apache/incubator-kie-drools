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
package org.kie.kogito.events.spring;

import java.util.Collection;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafkaEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);
    private static final String TOPIC_FORMAT = "kogito.addon.events.process.kafka.%s.%s";
    private static final String ENABLED_FORMAT = "kogito.events.%s.enabled";

    @Autowired
    private ObjectMapper json;

    @Autowired
    private Environment env;

    @Autowired
    private KafkaTemplate<String, String> eventsEmitter;

    @Override
    public void publish(DataEvent<?> event) {

        switch (event.getType()) {
            case "ProcessInstanceErrorDataEvent":
            case "ProcessInstanceNodeDataEvent":
            case "ProcessInstanceSLADataEvent":
            case "ProcessInstanceStateDataEvent":
            case "ProcessInstanceVariableDataEvent":
                publishToTopic(event, PROCESS_INSTANCES_TOPIC_NAME, "processinstances");
                break;
            case "UserTaskInstanceAssignmentDataEvent":
            case "UserTaskInstanceAttachmentDataEvent":
            case "UserTaskInstanceCommentDataEvent":
            case "UserTaskInstanceDeadlineDataEvent":
            case "UserTaskInstanceStateDataEvent":
            case "UserTaskInstanceVariableDataEvent":
                publishToTopic(event, USER_TASK_INSTANCES_TOPIC_NAME, "usertasks");
                break;
            case "ProcessDefinitionEvent":
                publishToTopic(event, PROCESS_DEFINITIONS_TOPIC_NAME, "processdefinitions");
                break;
            default:
                logger.debug("Unknown type of event '{}', ignoring for this publisher", event.getType());
        }
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        for (DataEvent<?> event : events) {
            publish(event);
        }
    }

    protected void publishToTopic(DataEvent<?> event, String topic, String enabled) {
        if (env.getProperty(String.format(ENABLED_FORMAT, enabled), Boolean.class, true)) {
            logger.debug("About to publish event {} to Kafka topic {}", event, topic);
            try {
                String eventString = json.writeValueAsString(event);
                logger.debug("Event payload '{}'", eventString);
                eventsEmitter.send(env.getProperty(String.format(TOPIC_FORMAT, topic, "topic"), topic), eventString);
                logger.debug("Successfully published event {} to topic {}", event, topic);
            } catch (Exception e) {
                logger.error("Error while publishing event to Kafka topic {} for event {}", topic, event, e);
            }
        } else {
            logger.debug("Event {} won't be published because topic {} is disabled", event, topic);
        }
    }
}
