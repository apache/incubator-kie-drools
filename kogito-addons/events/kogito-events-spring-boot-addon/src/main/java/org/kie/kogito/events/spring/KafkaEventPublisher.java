/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.events.spring;

import java.util.Collection;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class KafkaEventPublisher implements EventPublisher {
    
    private static final String TOPIC_NAME = "kogito-processinstances-events";
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private ObjectMapper json = new ObjectMapper();
    @Autowired
    private KafkaTemplate<String, String> eventsEmitter;
    
    @Override
    public void publish(DataEvent<?> event) {
        logger.debug("About to publish event {} to Kafka topic {}", event, TOPIC_NAME);
        try {
            String eventString = json.writeValueAsString(event);
            logger.debug("Event payload '{}'", eventString);
            
            eventsEmitter.send(TOPIC_NAME, eventString);
            logger.debug("Successfully published event {} to topic {}", event, TOPIC_NAME);
        } catch (Exception e) {
            logger.error("Error while publishing event to Kafka topic {} for event {}", TOPIC_NAME, event, e);
        }        
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        for (DataEvent<?> event : events) {
            publish(event);
        }
    }
}
