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
package org.kie.kogito.jobs.service.api.event.serialization;

import java.io.IOException;
import java.util.Objects;

import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.api.JobLookupId;
import org.kie.kogito.jobs.service.api.event.CreateJobEvent;
import org.kie.kogito.jobs.service.api.event.DeleteJobEvent;
import org.kie.kogito.jobs.service.api.event.JobCloudEvent;
import org.kie.kogito.jobs.service.api.serialization.DeserializationException;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventData;

import static org.kie.kogito.jobs.service.api.serialization.SerializationUtils.DEFAULT_OBJECT_MAPPER;
import static org.kie.kogito.jobs.service.api.serialization.SerializationUtils.registerDescriptors;

public class JobCloudEventDeserializer {

    private final ObjectMapper objectMapper;

    public JobCloudEventDeserializer() {
        this.objectMapper = DEFAULT_OBJECT_MAPPER;
    }

    public JobCloudEventDeserializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        registerDescriptors(objectMapper);
    }

    public JobCloudEvent<?> deserialize(byte[] data) {
        try {
            CloudEvent cloudEvent = objectMapper.readValue(data, CloudEvent.class);
            return deserialize(cloudEvent);
        } catch (IOException e) {
            throw new DeserializationException("An error was produced during a JobCloudEvent deserialization: " + e.getMessage(), e);
        }
    }

    public JobCloudEvent<?> deserialize(CloudEvent cloudEvent) {
        try {
            if (cloudEvent.getSpecVersion() != JobCloudEvent.SPEC_VERSION) {
                throw new DeserializationException("JobCloudEvents are expected to be in the CloudEvent specversion: " + JobCloudEvent.SPEC_VERSION);
            }
            CloudEventData cloudEventData = Objects.requireNonNull(cloudEvent.getData(), "JobCloudEvent data field must not be null");
            if (cloudEvent.getType().equals(CreateJobEvent.TYPE)) {
                Job job = objectMapper.readValue(cloudEventData.toBytes(), Job.class);
                return CreateJobEvent.builder()
                        .withValuesFrom(cloudEvent)
                        .job(job)
                        .build();
            } else if (cloudEvent.getType().equals(DeleteJobEvent.TYPE)) {
                JobLookupId lookupId = objectMapper.readValue(cloudEventData.toBytes(), JobLookupId.class);
                return DeleteJobEvent.builder()
                        .withValuesFrom(cloudEvent)
                        .lookupId(lookupId)
                        .build();
            }
            throw new DeserializationException("Unknown JobCloudEvent event type: " + cloudEvent.getType());
        } catch (IOException e) {
            throw new DeserializationException("An error was produced during a JobCloudEvent deserialization: " + e.getMessage(), e);
        }
    }
}
