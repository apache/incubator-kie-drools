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
package org.kie.kogito.quarkus.workflow.devservices;

import java.util.Collection;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.EventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.predicate.ResponsePredicate;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

public class DataIndexEventPublisher implements EventPublisher {

    public static final String KOGITO_DATA_INDEX = "kogito.data-index.url";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataIndexEventPublisher.class);
    private static final String CLOUD_EVENTS_CONTENT_TYPE = "application/cloudevents+json";
    private static final String CONTENT_TYPE = "content-type";

    @ConfigProperty(name = KOGITO_DATA_INDEX)
    Optional<String> dataIndexUrl;

    @Inject
    Vertx vertx;

    WebClient webClient;

    @PostConstruct
    public void init() {
        webClient = WebClient.create(vertx);
    }

    @Override
    public void publish(DataEvent<?> event) {
        if (dataIndexUrl.isEmpty()) {
            return;
        }

        LOGGER.debug("Sending event to data index: {}", event);
        switch (event.getType()) {
            case "ProcessDefinitionEvent":
                webClient.postAbs(dataIndexUrl.get() + "/definitions")
                        .putHeader(CONTENT_TYPE, CLOUD_EVENTS_CONTENT_TYPE)
                        .expect(ResponsePredicate.SC_ACCEPTED)
                        .sendJson(event, result -> {
                            if (result.failed()) {
                                LOGGER.error("Failed to send message to Data Index", result.cause());
                            } else {
                                LOGGER.debug("Event published to Data Index");
                            }
                        });
                break;
            case "ProcessInstanceErrorDataEvent":
            case "ProcessInstanceNodeDataEvent":
            case "ProcessInstanceSLADataEvent":
            case "ProcessInstanceStateDataEvent":
            case "ProcessInstanceVariableDataEvent":
                webClient.postAbs(dataIndexUrl.get() + "/processes")
                        .putHeader(CONTENT_TYPE, CLOUD_EVENTS_CONTENT_TYPE)
                        .expect(ResponsePredicate.SC_ACCEPTED)
                        .sendJson(event, result -> {
                            if (result.failed()) {
                                LOGGER.error("Failed to send message to Data Index", result.cause());
                            } else {
                                LOGGER.debug("Event published to Data Index");
                            }
                        });
                break;
            case "UserTaskInstanceAssignmentDataEvent":
            case "UserTaskInstanceAttachmentDataEvent":
            case "UserTaskInstanceCommentDataEvent":
            case "UserTaskInstanceDeadlineDataEvent":
            case "UserTaskInstanceStateDataEvent":
            case "UserTaskInstanceVariableDataEvent":
                webClient.postAbs(dataIndexUrl.get() + "/tasks")
                        .putHeader(CONTENT_TYPE, CLOUD_EVENTS_CONTENT_TYPE)
                        .expect(ResponsePredicate.SC_ACCEPTED)
                        .sendJson(event, result -> {
                            if (result.failed()) {
                                LOGGER.error("Failed to send message to Data Index", result.cause());
                            } else {
                                LOGGER.debug("Event published to Data Index");
                            }
                        });
                break;
            default:
                LOGGER.debug("Unknown type of event '{}', ignoring for this publisher", event.getType());
        }
    }

    @Override
    public void publish(Collection<DataEvent<?>> events) {
        events.forEach(this::publish);
    }

}
