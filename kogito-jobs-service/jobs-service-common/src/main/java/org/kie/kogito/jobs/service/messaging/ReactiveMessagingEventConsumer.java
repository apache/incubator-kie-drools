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
package org.kie.kogito.jobs.service.messaging;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.jobs.api.event.CancelJobRequestEvent;
import org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent;
import org.kie.kogito.jobs.api.event.JobCloudEvent;
import org.kie.kogito.jobs.api.event.serialization.JobCloudEventDeserializer;
import org.kie.kogito.jobs.service.exception.JobServiceException;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.model.job.ScheduledJobAdapter;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.impl.TimerDelegateJobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.smallrye.mutiny.Uni;

import static org.kie.kogito.jobs.api.event.CancelJobRequestEvent.CANCEL_JOB_REQUEST;
import static org.kie.kogito.jobs.api.event.CreateProcessInstanceJobRequestEvent.CREATE_PROCESS_INSTANCE_JOB_REQUEST;

public abstract class ReactiveMessagingEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveMessagingEventConsumer.class);

    protected TimerDelegateJobScheduler scheduler;

    protected ReactiveJobRepository jobRepository;

    protected ObjectMapper objectMapper;

    protected JobCloudEventDeserializer deserializer;

    protected ReactiveMessagingEventConsumer() {
        this.scheduler = null;
        this.jobRepository = null;
        this.objectMapper = null;
        this.deserializer = null;
    }

    protected ReactiveMessagingEventConsumer(TimerDelegateJobScheduler scheduler,
            ReactiveJobRepository jobRepository,
            ObjectMapper objectMapper) {
        this.scheduler = scheduler;
        this.jobRepository = jobRepository;
        this.objectMapper = objectMapper;
        this.deserializer = new JobCloudEventDeserializer(objectMapper);
    }

    protected Uni<Void> onKogitoServiceRequest(Message<CloudEvent> message) {
        final JobCloudEvent<?> jobCloudEvent = deserializer.deserialize(message.getPayload());
        switch (jobCloudEvent.getType()) {
            case CREATE_PROCESS_INSTANCE_JOB_REQUEST:
                return handleEvent(message, (CreateProcessInstanceJobRequestEvent) jobCloudEvent);
            case CANCEL_JOB_REQUEST:
                return handleEvent(message, (CancelJobRequestEvent) jobCloudEvent);
            default:
                LOGGER.error("Unexpected job request type: {}, for the cloud event: {}", jobCloudEvent.getType(), jobCloudEvent);
                return Uni.createFrom().completionStage(message.nack(new JobServiceException("Unexpected job request type: " + jobCloudEvent.getType())));
        }
    }

    protected Uni<Void> handleEvent(Message<?> message, CreateProcessInstanceJobRequestEvent event) {
        return Uni.createFrom().completionStage(jobRepository.get(event.getData().getId()))
                .flatMap(existingJob -> {
                    if (existingJob == null || existingJob.getStatus() == JobStatus.SCHEDULED) {
                        return Uni.createFrom().publisher(scheduler.schedule(ScheduledJobAdapter.to(ScheduledJob.builder().job(event.getData()).build())));
                    } else {
                        LOGGER.info("A Job in status: {} already exists for the job id: {}, no processing will be done fot the event: {}.",
                                existingJob.getStatus(),
                                existingJob.getId(),
                                event);
                        return Uni.createFrom().item(existingJob);
                    }
                })
                .onItem().transformToUni(createdJob -> {
                    if (createdJob == null) {
                        // The scheduler halted the stream processing by emitting no values, an error was produced.
                        return Uni.createFrom().failure(new JobServiceException("An internal scheduler error was produced during Job scheduling"));
                    } else {
                        return Uni.createFrom().completionStage(message.ack());
                    }
                }).onFailure().recoverWithUni(throwable -> {
                    String msg = String.format("An error was produced during Job scheduling for the event: %s", event);
                    LOGGER.error(msg, throwable);
                    return Uni.createFrom().completionStage(message.nack(new JobServiceException("An error was produced during Job scheduling: " + throwable.getMessage(), throwable)));
                });
    }

    protected Uni<Void> handleEvent(Message<?> message, CancelJobRequestEvent event) {
        return Uni.createFrom().completionStage(scheduler.cancel(event.getData().getId()))
                .onItemOrFailure().transformToUni((cancelledJob, throwable) -> {
                    if (throwable != null) {
                        String msg = String.format("An error was produced during Job cancelling for the event: %s", event);
                        LOGGER.error(msg, throwable);
                        return Uni.createFrom().completionStage(message.nack(new JobServiceException("An error was produced during Job cancelling: " + throwable.getMessage(), throwable)));
                    } else {
                        if (cancelledJob == null) {
                            LOGGER.info("No Job exists for the job id: {} or it was already cancelled", event.getData().getId());
                        }
                        return Uni.createFrom().completionStage(message.ack());
                    }
                });
    }
}
