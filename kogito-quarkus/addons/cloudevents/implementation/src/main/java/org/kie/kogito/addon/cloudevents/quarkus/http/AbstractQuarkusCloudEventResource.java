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
package org.kie.kogito.addon.cloudevents.quarkus.http;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kie.kogito.addon.cloudevents.quarkus.QuarkusCloudEventReceiver;
import org.kie.kogito.cloudevents.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonFormat;

/**
 * Base class for Resource classes that consumes CloudEvents through HTTP.
 * It can process binary or structured CloudEvent, convert it's data to JSON format and publish to the internal Channel
 */
public abstract class AbstractQuarkusCloudEventResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractQuarkusCloudEventResource.class);

    @Inject
    ObjectMapper objectMapper;
    @Inject
    QuarkusCloudEventReceiver publisher;

    @PostConstruct
    public void setup() {
        objectMapper.registerModule(JsonFormat.getCloudEventJacksonModule());
    }

    @POST
    @Consumes(MediaType.WILDCARD)
    @Produces(MediaType.APPLICATION_JSON)
    public CompletionStage<Response> cloudEventListener(CloudEvent event) {
        return CompletableFuture.completedFuture(serialize(event))
                .thenCompose(publisher::produce)
                .thenApply(r -> Response.ok().type(MediaType.APPLICATION_JSON).build());
    }

    protected String serialize(CloudEvent event) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("CloudEvent to publish: {}", Printer.beautify(event));
        }
        try {
            if (!isSupportedContentType(event)) {
                LOGGER.warn(
                        "Content-Type of the received CloudEvent '{}' is not supported. Content-type is {}. Assuming application/json.",
                        event.getType(), event.getDataContentType());
                // We rebuild the CloudEvent with a supported format, so we can serialize correctly our content.
                // We assume the data is in JSON format.
                // This happens when a client sends the data in an arbitrary format without informing the Content-Type, instead of rejecting the request, we try to serialize it using JSON.
                // If it's not a valid JSON, we throw the exception to be correctly handled by the controller (4xx error)
                objectMapper.readTree(event.getData());
                final CloudEvent newEvent = CloudEventBuilder.v1(event).withDataContentType(MediaType.APPLICATION_JSON).build();
                final String newDecodedEvent = objectMapper.writeValueAsString(newEvent);
                LOGGER.debug("Decoded event {}", newDecodedEvent);
                return newDecodedEvent;
            } else {
                return objectMapper.writeValueAsString(event);
            }
        } catch (IOException | IllegalArgumentException ex) {
            throw new CloudEventResourceException(event, ex);
        }
    }

    /**
     * Sometimes clients might send empty Content-type headers.
     *
     * @param event the given CloudEvent to check
     * @return true if the given content-type is not supported
     */
    private boolean isSupportedContentType(CloudEvent event) {
        return MediaType.APPLICATION_JSON_TYPE.isCompatible(MediaType.valueOf(event.getDataContentType()));
    }

}
