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
package org.kie.kogito.jobs.service.api.serialization;

import org.kie.kogito.jobs.service.api.RecipientDescriptor;
import org.kie.kogito.jobs.service.api.RecipientDescriptorRegistry;
import org.kie.kogito.jobs.service.api.ScheduleDescriptor;
import org.kie.kogito.jobs.service.api.ScheduleDescriptorRegistry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.jackson.JsonFormat;

public class SerializationUtils {

    /**
     * Shared default object mapper with the minimal required setups that can be used by the JobCloudEventDeserializer
     * and JobCloudEventSerializer classes. Interested parties that doesn't want to use this object mapper can use
     * their own provided mappers by using the proper constructors.
     */
    public static final ObjectMapper DEFAULT_OBJECT_MAPPER = buildObjectMapper();

    private SerializationUtils() {
    }

    public static void registerDescriptors(ObjectMapper objectMapper) {
        for (RecipientDescriptor<?> descriptor : RecipientDescriptorRegistry.getInstance().getDescriptors()) {
            objectMapper.registerSubtypes(new NamedType(descriptor.getType(), descriptor.getName()));
        }
        for (ScheduleDescriptor<?> descriptor : ScheduleDescriptorRegistry.getInstance().getDescriptors()) {
            objectMapper.registerSubtypes(new NamedType(descriptor.getType(), descriptor.getName()));
        }
    }

    private static ObjectMapper buildObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(JsonFormat.getCloudEventJacksonModule())
                .disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(com.fasterxml.jackson.databind.DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        registerDescriptors(objectMapper);
        return objectMapper;
    }
}
