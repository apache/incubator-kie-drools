/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.jobs.service.stream;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.reactive.messaging.annotations.Channel;
import io.smallrye.reactive.messaging.annotations.Emitter;
import io.smallrye.reactive.messaging.annotations.OnOverflow;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kie.kogito.jobs.service.events.JobDataEvent;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.utils.DateUtil;
import org.kie.kogito.jobs.service.utils.FunctionsUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class KafkaJobStreams {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaJobStreams.class);
    private static final String PUBLISH_EVENTS_CONFIG_KEY = "kogito.jobs-service.events-support";

    private ObjectMapper objectMapper;

    private Emitter kafkaEmitter;

    private Optional<Boolean> enabled;

    @Inject
    public KafkaJobStreams(ObjectMapper objectMapper,
                           @ConfigProperty(name = PUBLISH_EVENTS_CONFIG_KEY)
                                   Optional<String> config,
                           @Channel(AvailableStreams.JOB_STATUS_CHANGE_EVENTS_TOPIC)
                           @OnOverflow(value = OnOverflow.Strategy.LATEST)
                                   Emitter emitter) {
        this.objectMapper = objectMapper;
        this.enabled = config.map(Boolean::valueOf).filter(Boolean.TRUE::equals);
        this.kafkaEmitter = emitter;
    }

    @Incoming(AvailableStreams.JOB_STATUS_CHANGE_EVENTS)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public void jobStatusChangeKafkaPublisher(ScheduledJob job) {
        enabled
                .map(e -> kafkaEmitter)
                .map(emitter -> {
                    JobDataEvent event = JobDataEvent
                            .builder()
                            .time(DateUtil.now())
                            .id(job.getId())
                            .source("JobService")
                            .data(job)
                            .build();
                    return emitter.send(FunctionsUtil.unchecked(objectMapper::writeValueAsString).apply(event));
                })
                .ifPresent(emitter -> LOGGER.debug("Job Status change published to kafka {}", job));
    }
}
