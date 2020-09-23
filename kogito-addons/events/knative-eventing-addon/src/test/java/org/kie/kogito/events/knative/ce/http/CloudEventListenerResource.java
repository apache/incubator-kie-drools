/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.events.knative.ce.http;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonFormat;
import org.kie.kogito.events.knative.ce.Printer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/")
public class CloudEventListenerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(CloudEventListenerResource.class);

    @POST
    @Consumes({MediaType.APPLICATION_JSON, JsonFormat.CONTENT_TYPE})
    @Produces(MediaType.APPLICATION_JSON)
    public Response cloudEventListener(CloudEvent event) {
        try {
            LOGGER.info("CloudEvent processed: {}", Printer.beautify(event));
            return Response.ok(event).build();
        } catch (Exception ex) {
            LOGGER.error("Fail to process CloudEvent: ", ex);
            return Responses.errorProcessingCloudEvent(ex);
        }
    }
}
