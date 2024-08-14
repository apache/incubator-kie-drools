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
package org.kie.kogito.process.dynamic;

import java.io.IOException;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.kie.kogito.event.CloudEventMarshaller;
import org.kie.kogito.event.cloudevents.CloudEventExtensionConstants;
import org.kie.kogito.event.impl.StringCloudEventMarshaller;
import org.kie.kogito.jackson.utils.ObjectMapperFactory;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path("/example")
public class ResumeProcessResource {

    private CloudEventMarshaller<String> marshaller;
    @Inject
    @Channel("resume-out")
    Emitter<String> resumeEmitter;

    @PostConstruct
    void init() {
        marshaller = new StringCloudEventMarshaller(ObjectMapperFactory.get());
    }

    @POST
    @Path("/{processInstanceId}")
    public Map<String, Object> resumeProcess(@PathParam("processInstanceId") String processInstanceId, Map<String, Object> data) throws IOException {
        resumeEmitter.send(marshaller.marshall(buildCloudEvent(processInstanceId, data)));
        return Map.of("message", "this is a test call to resume the workflow", "tobefilterout", "This a parameter we would like to remove");
    }

    private CloudEvent buildCloudEvent(String processInstanceId, Map<String, Object> data) {
        return CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create(""))
                .withType("resume")
                .withTime(OffsetDateTime.now())
                .withData(marshaller.cloudEventDataFactory().apply(data)).withExtension(CloudEventExtensionConstants.PROCESS_REFERENCE_ID, processInstanceId).build();
    }
}
