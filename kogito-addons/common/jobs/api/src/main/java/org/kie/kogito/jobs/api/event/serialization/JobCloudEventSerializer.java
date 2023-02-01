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
package org.kie.kogito.jobs.api.event.serialization;

import org.kie.kogito.jobs.api.event.JobCloudEvent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.jackson.JsonFormat;

@Deprecated
public class JobCloudEventSerializer {

    /**
     * Shared default object mapper with the minimal required setups that can be used by the JobCloudEventDeserializer
     * and JobCloudEventSerializer classes. Interested parties that doesn't want to use this object mapper can use
     * their own provided mappers by using the proper constructors.
     */
    public static final ObjectMapper DEFAULT_OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .registerModule(JsonFormat.getCloudEventJacksonModule())
            .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final ObjectMapper objectMapper;

    public JobCloudEventSerializer() {
        this.objectMapper = DEFAULT_OBJECT_MAPPER;
    }

    public JobCloudEventSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String serialize(JobCloudEvent<?> jobCloudEvent) {
        try {
            return objectMapper.writeValueAsString(jobCloudEvent);
        } catch (JsonProcessingException e) {
            throw new SerializationException("An error was produced during a JobCloudEvent serialization: " + e.getMessage(), e);
        }
    }
}
