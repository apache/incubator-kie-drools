/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import java.util.Objects;

import org.eclipse.microprofile.reactive.messaging.Message;
import org.kie.kogito.jobs.service.exception.JobServiceException;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.impl.TimerDelegateJobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.cloudevents.CloudEvent;
import io.smallrye.mutiny.Uni;

public abstract class ReactiveMessagingEventConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveMessagingEventConsumer.class);

    private final TimerDelegateJobScheduler scheduler;
    private final ReactiveJobRepository jobRepository;
    private final String createJobEventType;
    private final String cancelJobEventType;

    protected ReactiveMessagingEventConsumer() {
        this(null, null, null, null);
    }

    protected ReactiveMessagingEventConsumer(TimerDelegateJobScheduler scheduler,
            ReactiveJobRepository jobRepository,
            String createJobEventType,
            String cancelJobEventType) {
        this.scheduler = scheduler;
        this.jobRepository = jobRepository;
        this.createJobEventType = createJobEventType;
        this.cancelJobEventType = cancelJobEventType;
    }

    public Uni<Void> onKogitoServiceRequest(Message<CloudEvent> message) {
        CloudEvent cloudEvent = message.getPayload();
        final String eventType = cloudEvent.getType();
        if (Objects.equals(createJobEventType, eventType)) {
            return handleCreateEvent(message, getJobDetails(cloudEvent));
        }
        if (Objects.equals(cancelJobEventType, eventType)) {
            return handleCancelEvent(message, getJobId(cloudEvent));
        }

        LOGGER.error("Unexpected job request type: {}, for the cloud event: {}", eventType, cloudEvent);
        return Uni.createFrom().completionStage(message.nack(new JobServiceException("Unexpected job request type: " + eventType)));
    }

    public abstract JobDetails getJobDetails(CloudEvent createEvent);

    public abstract String getJobId(CloudEvent createEvent);

    protected Uni<Void> handleCreateEvent(Message<?> message, JobDetails job) {
        return Uni.createFrom().completionStage(jobRepository.get(job.getId()))
                .flatMap(existingJob -> {
                    if (existingJob == null || existingJob.getStatus() == JobStatus.SCHEDULED) {
                        return Uni.createFrom().publisher(scheduler.schedule(job));
                    } else {
                        LOGGER.info("A Job in status: {} already exists for the job id: {}, no processing will be done fot the event: {}.",
                                existingJob.getStatus(),
                                existingJob.getId(),
                                message.getPayload());
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
                    String msg = String.format("An error was produced during Job scheduling for the event: %s", message.getPayload());
                    LOGGER.error(msg, throwable);
                    return Uni.createFrom().completionStage(message.nack(new JobServiceException("An error was produced during Job scheduling: " + throwable.getMessage(), throwable)));
                });
    }

    protected Uni<Void> handleCancelEvent(Message<?> message, String id) {
        return Uni.createFrom().completionStage(scheduler.cancel(id))
                .onItemOrFailure().transformToUni((cancelledJob, throwable) -> {
                    if (throwable != null) {
                        String msg = String.format("An error was produced during Job cancelling for the event: %s", message.getPayload());
                        LOGGER.error(msg, throwable);
                        return Uni.createFrom().completionStage(message.nack(new JobServiceException("An error was produced during Job cancelling: " + throwable.getMessage(), throwable)));
                    } else {
                        if (cancelledJob == null) {
                            LOGGER.info("No Job exists for the job id: {} or it was already cancelled", id);
                        }
                        return Uni.createFrom().completionStage(message.ack());
                    }
                });
    }

    public String getCreateJobEventType() {
        return createJobEventType;
    }

    public String getCancelJobEventType() {
        return cancelJobEventType;
    }
}
