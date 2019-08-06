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

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.Stream;

@Singleton
public class ReactiveMessagingEventPublisher implements EventPublisher {
    private static final String TOPIC_NAME = "kogito-processinstances-events";
    
    private static final Logger logger = LoggerFactory.getLogger(ReactiveMessagingEventPublisher.class);
    private Jsonb jsonb = JsonbBuilder.create();
    
    @Inject
    @Stream(TOPIC_NAME)
    Emitter<String> eventsEmitter;
    
    @Override
    public void publish(DataEvent<?> event) {
        logger.debug("About to publish event {} to topic {}", event, TOPIC_NAME);
        try {
            String eventString = jsonb.toJson(event);
            logger.debug("Event payload '{}'", eventString);

            eventsEmitter.send(eventString);
            logger.debug("Successfully published event {} to topic {}", event, TOPIC_NAME);
        } catch (Exception e) {
            logger.error("Error while publishing event to topic {} for event {}", TOPIC_NAME, event, e);
        }  
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        for (DataEvent<?> event : events) {
            publish(event);
        }
    }

}
