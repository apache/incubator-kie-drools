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
package org.kie.kogito.app;

import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.kie.kogito.addon.cloudevents.quarkus.QuarkusCloudEventPublisher;
import org.kie.kogito.addon.cloudevents.quarkus.http.Responses;
import org.kie.kogito.cloudevents.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;

@Path("/")
public class CloudEventListenerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger("CloudEventListenerResource");
    private Map<String, Emitter<String>> emitters;

    @javax.inject.Inject
    ObjectMapper objectMapper;

    @javax.inject.Inject()
    QuarkusCloudEventPublisher publisher;

    @javax.annotation.PostConstruct
    void setup() {
        objectMapper.registerModule(JsonFormat.getCloudEventJacksonModule());
    }

    @POST()
    @Consumes({MediaType.APPLICATION_JSON, JsonFormat.CONTENT_TYPE})
    @Produces(MediaType.APPLICATION_JSON)
    public javax.ws.rs.core.Response cloudEventListener(CloudEvent event) {
        try {
            LOGGER.debug("CloudEvent received: {}", Printer.beautify(event));
            // convert CloudEvent to JSON and send to internal channels
            publisher.produce(objectMapper.writeValueAsString(event));
            return javax.ws.rs.core.Response.ok().build();
        } catch (Exception ex) {
            return Responses.errorProcessingCloudEvent(ex);
        }
    }

}