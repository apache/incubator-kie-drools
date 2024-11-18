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
package org.kie.kogito.jobs.service.json;

import org.kie.kogito.jobs.DurationExpirationTime;
import org.kie.kogito.jobs.ExactExpirationTime;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.service.api.serlialization.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.cloudevents.jackson.JsonFormat;
import io.quarkus.jackson.ObjectMapperCustomizer;

import jakarta.enterprise.inject.Produces;
import jakarta.inject.Singleton;

public class JacksonConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonConfiguration.class);

    @Singleton
    @Produces
    public ObjectMapperCustomizer customizer() {
        return objectMapper -> {
            LOGGER.debug("Jackson customization initialized.");
            SimpleModule kogitoCustomModule = new SimpleModule();
            kogitoCustomModule.addSerializer(JobDescription.class, new JobDescriptionSerializer());
            kogitoCustomModule.addDeserializer(JobDescription.class, new JobDescriptionDeserializer());
            kogitoCustomModule.addSerializer(DurationExpirationTime.class, new DurationExpirationTimeSerializer());
            kogitoCustomModule.addDeserializer(DurationExpirationTime.class, new DurationExpirationTimeDeserializer());
            kogitoCustomModule.addSerializer(ExactExpirationTime.class, new ExactExpirationTimeSerializer());
            kogitoCustomModule.addDeserializer(ExactExpirationTime.class, new ExactExpirationTimeDeserializer());
            objectMapper
                    .registerModule(new JavaTimeModule())
                    .registerModule(kogitoCustomModule)
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                    .registerModule(JsonFormat.getCloudEventJacksonModule());
            SerializationUtils.registerDescriptors(objectMapper);
        };
    }
}
