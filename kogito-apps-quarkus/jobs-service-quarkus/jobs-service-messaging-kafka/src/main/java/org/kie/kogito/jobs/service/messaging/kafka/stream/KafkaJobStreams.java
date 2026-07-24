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
package org.kie.kogito.jobs.service.messaging.kafka.stream;

import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.kie.kogito.jobs.service.events.JobDataEvent;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.stream.AbstractJobStreams;
import org.kie.kogito.jobs.service.stream.AvailableStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.reactive.messaging.kafka.api.OutgoingKafkaRecordMetadata;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class KafkaJobStreams extends AbstractJobStreams {

    public static final String PUBLISH_EVENTS_CONFIG_KEY = "kogito.jobs-service.kafka.job-status-change-events";
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJobStreams.class);

    @Inject
    public KafkaJobStreams(ObjectMapper objectMapper,
            @ConfigProperty(name = PUBLISH_EVENTS_CONFIG_KEY) Optional<Boolean> config,
            @Channel(AvailableStreams.JOB_STATUS_CHANGE_EVENTS_TOPIC) @OnOverflow(value = OnOverflow.Strategy.UNBOUNDED_BUFFER) Emitter<String> emitter,
            @ConfigProperty(name = "kogito.service.url", defaultValue = "http://localhost:8080") String url) {
        super(objectMapper, config.orElse(false), emitter, url);
    }

    @Override
    public void jobStatusChange(JobDetails job) {
        LOGGER.debug("jobStatusChange call received, enabled: {}, job: {}", enabled, job);
        super.jobStatusChange(job);
    }

    @Override
    protected Message<String> decorate(Message<String> message, JobDataEvent event) {
        // regular kafka partitioning.
        return message.addMetadata(OutgoingKafkaRecordMetadata.builder().withKey(event.getData().getId()).build());
    }
}
