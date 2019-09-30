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

package org.kie.kogito.events.rm;

import java.util.Collection;
import java.util.TimeZone;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.StdDateFormat;

import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;

@Singleton
public class ReactiveMessagingEventPublisher implements EventPublisher {
    private static final String PI_TOPIC_NAME = "kogito-processinstances-events";
    private static final String UI_TOPIC_NAME = "kogito-usertaskinstances-events";
    
    private static final Logger logger = LoggerFactory.getLogger(ReactiveMessagingEventPublisher.class);
    private ObjectMapper json = new ObjectMapper();
    
    @Inject
    @Stream(PI_TOPIC_NAME)
    Emitter<String> processInstancesEventsEmitter;
    
    @Inject
    @Stream(UI_TOPIC_NAME)
    Emitter<String> userTasksEventsEmitter;
    
    @Inject
    @ConfigProperty(name = "kogito.events.processinstances.enabled", defaultValue = "true")
    Boolean processInstancesEvents;
    
    @Inject
    @ConfigProperty(name = "kogito.events.usertasks.enabled", defaultValue = "true")
    Boolean userTasksEvents;
    
    @PostConstruct
    public void configure() {
        json.setDateFormat(new StdDateFormat().withColonInTimeZone(true).withTimeZone(TimeZone.getDefault()));
    }
    
    @Override
    public void publish(DataEvent<?> event) {
        if (event.getType().equals("ProcessInstanceEvent") && processInstancesEvents) {
            
            publishToTopic(event, processInstancesEventsEmitter, PI_TOPIC_NAME);
        } else if (event.getType().equals("UserTaskInstanceEvent") && userTasksEvents) {
            
            publishToTopic(event, userTasksEventsEmitter, UI_TOPIC_NAME);
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

    
    protected void publishToTopic(DataEvent<?> event, Emitter<String> emitter, String topic) {
        if (emitter.isRequested()) {
            logger.debug("Emitter {} is not ready to send messages", topic);
        }
        
        logger.debug("About to publish event {} to topic {}", event, topic);
        try {
            String eventString = json.writeValueAsString(event);
            logger.debug("Event payload '{}'", eventString);

            emitter.send(eventString);
            logger.debug("Successfully published event {} to topic {}", event, topic);
        } catch (Exception e) {
            logger.error("Error while publishing event to topic {} for event {}", topic, event, e);
        }  
    }
}
