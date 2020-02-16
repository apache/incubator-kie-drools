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
import java.util.TimeZone;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;

@Component
public class KafkaEventPublisher implements EventPublisher {
    
    private static final String PI_TOPIC_NAME = "kogito-processinstances-events";
    private static final String UI_TOPIC_NAME = "kogito-usertaskinstances-events";
    private static final String VI_TOPIC_NAME = "kogito-variables-events";
    
    private static final Logger logger = LoggerFactory.getLogger(KafkaEventPublisher.class);

    private ObjectMapper json = new ObjectMapper();
    
    @Autowired
    private KafkaTemplate<String, String> eventsEmitter;
    
    @Value("${kogito.events.processinstances.enabled:true}")
    private boolean processInstancesEvents;
    
    @Value("${kogito.events.usertasks.enabled:true}")
    private boolean userTasksEvents;
    
    @Value("${kogito.events.variables.enabled:true}")
    private boolean variablesEvents;
        
    public KafkaEventPublisher() {
        json.setDateFormat(new StdDateFormat().withColonInTimeZone(true).withTimeZone(TimeZone.getDefault()));
    }
    
    @Override
    public void publish(DataEvent<?> event) {
        if (event.getType().equals("ProcessInstanceEvent") && processInstancesEvents) {
            
            publishToTopic(event, eventsEmitter, PI_TOPIC_NAME);
        } else if (event.getType().equals("UserTaskInstanceEvent") && userTasksEvents) {
            
            publishToTopic(event, eventsEmitter, UI_TOPIC_NAME);
        } else if (event.getType().equals("VariableInstanceEvent") && variablesEvents) {
            
            publishToTopic(event, eventsEmitter, VI_TOPIC_NAME);
        } else {
            logger.warn("Unknown type of event '{}', ignoring", event.getType());
        }       
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        for (DataEvent<?> event : events) {
            publish(event);
        }
    }
    
    protected void publishToTopic(DataEvent<?> event, KafkaTemplate<String, String> emitter, String topic) {
        logger.debug("About to publish event {} to Kafka topic {}", event, topic);
        try {
            String eventString = json.writeValueAsString(event);
            logger.debug("Event payload '{}'", eventString);
            
            eventsEmitter.send(topic, eventString);
            logger.debug("Successfully published event {} to topic {}", event, topic);
        } catch (Exception e) {
            logger.error("Error while publishing event to Kafka topic {} for event {}", topic, event, e);
        }        
    }
}
