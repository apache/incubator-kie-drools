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
package org.kie.kogito.jobs.service.stream;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.jobs.service.adapter.ScheduledJobAdapter;
import org.kie.kogito.jobs.service.events.JobDataEvent;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.resource.RestApiConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.smallrye.reactive.messaging.providers.locals.ContextAwareMessage;

public abstract class AbstractJobStreams implements JobStreams {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJobStreams.class);

    protected ObjectMapper objectMapper;

    protected boolean enabled;

    protected Emitter<String> emitter;

    protected String url;

    protected AbstractJobStreams() {
    }

    protected AbstractJobStreams(ObjectMapper objectMapper, boolean enabled, Emitter<String> emitter, String url) {
        this.objectMapper = objectMapper;
        this.enabled = enabled;
        this.emitter = emitter;
        this.url = url;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void jobStatusChange(JobDetails job) {
        if (isEnabled()) {
            try {
                JobDataEvent event = buildEvent(job);
                LOGGER.debug("emit jobStatusChange, hasRequests: {}, eventId: {}, jobDetails: {}", emitter.hasRequests(), event.getId(), job);
                String json = objectMapper.writeValueAsString(event);
                emitter.send(decorate(ContextAwareMessage.of(json)
                        .withAck(() -> onAck(event.getId(), job))
                        .withNack(reason -> onNack(reason, job)), event));
            } catch (Exception e) {
                String msg = String.format("An unexpected error was produced while processing a Job status change for the job: %s", job);
                LOGGER.error(msg, e);
            }
        }
    }

    protected JobDataEvent buildEvent(JobDetails job) {
        return JobDataEvent
                .builder()
                .source(url + RestApiConstants.JOBS_PATH)
                .data(ScheduledJobAdapter.of(job))//this should support jobs crated with V1 and V2
                .build();
    }

    protected CompletionStage<Void> onAck(String eventId, JobDetails job) {
        LOGGER.debug("Job Status change emitted successfully, eventId: {}, jobDetails: {}", eventId, job);
        return CompletableFuture.completedFuture(null);
    }

    protected CompletionStage<Void> onNack(Throwable reason, JobDetails job) {
        String msg = String.format("An error was produced while emitting a Job status change for the job: %s", job);
        LOGGER.error(msg, reason);
        return CompletableFuture.completedFuture(null);
    }

    protected Message<String> decorate(Message<String> message, JobDataEvent event) {
        return message;
    }
}
