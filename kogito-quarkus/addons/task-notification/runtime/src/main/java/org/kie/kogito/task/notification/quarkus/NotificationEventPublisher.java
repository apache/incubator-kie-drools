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
package org.kie.kogito.task.notification.quarkus;

import java.util.Collection;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class NotificationEventPublisher implements EventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(NotificationEventPublisher.class.getName());
    private static final String CHANNEL_NAME = "kogito-deadline-events";

    @Inject
    @Channel(CHANNEL_NAME)
    Emitter<DataEvent<?>> emitter;

    @Override
    public void publish(DataEvent<?> event) {
        if (event.getType().startsWith("UserTaskDeadline")) {
            logger.debug("About to publish event {} to topic {}", event, CHANNEL_NAME);
            try {
                emitter.send(event);
                logger.debug("Successfully published event {} to topic {}", event, CHANNEL_NAME);
            } catch (Exception e) {
                logger.error("Error while publishing event to topic {} for event {}", CHANNEL_NAME, event, e);
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
