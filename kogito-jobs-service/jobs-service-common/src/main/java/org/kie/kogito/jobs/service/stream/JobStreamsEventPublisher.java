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

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.scheduler.ReactiveJobScheduler;
import org.kie.kogito.jobs.service.utils.ErrorHandling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.reactive.messaging.annotations.Broadcast;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

/**
 * Class that configure the Consumers for Job Streams,like Job Executed, Job Error... and execute the actions for each
 * received item.
 */
@ApplicationScoped
public class JobStreamsEventPublisher implements JobEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobStreamsEventPublisher.class);

    @Inject
    ReactiveJobScheduler scheduler;

    /**
     * Publish on Stream of Job Error events
     */
    @Inject
    @Channel(AvailableStreams.JOB_ERROR)
    @OnOverflow(value = OnOverflow.Strategy.NONE)
    Emitter<JobExecutionResponse> jobErrorEmitter;

    /**
     * Publish on Stream of Job Success events
     */
    @Inject
    @Channel(AvailableStreams.JOB_SUCCESS)
    @OnOverflow(value = OnOverflow.Strategy.NONE)
    Emitter<JobExecutionResponse> jobSuccessEmitter;

    /**
     * Publish on Stream of Job Success events
     */
    @Inject
    @Channel(AvailableStreams.JOB_STATUS_CHANGE)
    @OnOverflow(value = OnOverflow.Strategy.NONE)
    Emitter<JobDetails> jobStatusChangeEmitter;

    public JobExecutionResponse publishJobError(JobExecutionResponse response) {
        jobErrorEmitter.send(response);
        return response;
    }

    public JobExecutionResponse publishJobSuccess(JobExecutionResponse response) {
        jobSuccessEmitter.send(response);
        return response;
    }

    public JobDetails publishJobStatusChange(JobDetails scheduledJob) {
        jobStatusChangeEmitter.send(scheduledJob);
        return scheduledJob;
    }

    //Stream Processors
    @Incoming(AvailableStreams.JOB_ERROR_EVENTS)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public CompletionStage<Boolean> jobErrorProcessor(JobExecutionResponse response) {
        LOGGER.warn("Error received {}", response);
        return ErrorHandling.skipErrorPublisherBuilder(scheduler::handleJobExecutionError, response)
                .findFirst()
                .run()
                .thenApply(Optional::isPresent)
                .exceptionally(e -> {
                    LOGGER.error("Error handling error {}", response, e);
                    return false;
                });
    }

    @Incoming(AvailableStreams.JOB_SUCCESS_EVENTS)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public CompletionStage<Boolean> jobSuccessProcessor(JobExecutionResponse response) {
        LOGGER.debug("Success received to be processed {}", response);
        return ErrorHandling.skipErrorPublisherBuilder(scheduler::handleJobExecutionSuccess, response)
                .findFirst()
                .run()
                .thenApply(Optional::isPresent)
                .exceptionally(e -> {
                    LOGGER.error("Error handling error {}", response, e);
                    return false;
                });
    }

    // Broadcast Events from Emitter to Streams
    @Incoming(AvailableStreams.JOB_ERROR)
    @Outgoing(AvailableStreams.JOB_ERROR_EVENTS)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public JobExecutionResponse jobErrorBroadcast(JobExecutionResponse response) {
        LOGGER.debug("Error broadcast published {}", response);
        return response;
    }

    @Incoming(AvailableStreams.JOB_SUCCESS)
    @Outgoing(AvailableStreams.JOB_SUCCESS_EVENTS)
    @Broadcast
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public JobExecutionResponse jobSuccessBroadcast(JobExecutionResponse response) {
        LOGGER.debug("Success broadcast published {}", response);
        return response;
    }

    @Incoming(AvailableStreams.JOB_STATUS_CHANGE)
    @Outgoing(AvailableStreams.JOB_STATUS_CHANGE_EVENTS)
    @Broadcast
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public JobDetails jobStatusChangeBroadcast(JobDetails job) {
        LOGGER.debug("Status change broadcast for Job {}", job);
        return job;
    }
}
