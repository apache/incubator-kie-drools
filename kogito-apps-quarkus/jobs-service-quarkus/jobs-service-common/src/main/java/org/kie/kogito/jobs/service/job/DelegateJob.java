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
package org.kie.kogito.jobs.service.job;

import java.util.concurrent.atomic.AtomicReference;

import org.kie.kogito.jobs.service.exception.JobExecutionException;
import org.kie.kogito.jobs.service.executor.JobExecutor;
import org.kie.kogito.jobs.service.executor.JobExecutorResolver;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobDetailsContext;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.scheduler.ReactiveJobScheduler;
import org.kie.kogito.jobs.service.utils.ErrorHandling;
import org.kie.kogito.timer.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;

import static java.util.Objects.requireNonNull;
import static mutiny.zero.flow.adapters.AdaptersToFlow.publisher;

/**
 * The job that delegates the execution to the {@link JobExecutorResolver} with the {@link JobDetailsContext}.
 */
public class DelegateJob implements Job<JobDetailsContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelegateJob.class);

    private final JobExecutorResolver jobExecutorResolver;

    ReactiveJobScheduler scheduler;

    public DelegateJob(JobExecutorResolver executorResolver, ReactiveJobScheduler scheduler) {
        this.jobExecutorResolver = executorResolver;
        this.scheduler = scheduler;
    }

    @Override
    public void execute(JobDetailsContext ctx) {
        final AtomicReference<JobExecutionResponse> executionResponse = new AtomicReference<>();
        final JobDetails jobDetails = requireNonNull(ctx.getJobDetails(), () -> String.format("JobDetails cannot be null for context: %s", ctx));
        final JobExecutor executor = requireNonNull(jobExecutorResolver.get(jobDetails), () -> String.format("No JobExecutor was found for jobDetails: %s", jobDetails));
        LOGGER.info("Executing job for context: {}", jobDetails);
        executor.execute(jobDetails)
                .flatMap(response -> {
                    executionResponse.set(response);
                    return handleJobExecutionSuccess(response);
                })
                .onFailure(JobExecutionException.class).recoverWithUni(ex -> {
                    String jobId = ((JobExecutionException) ex).getJobId();
                    executionResponse.set(JobExecutionResponse.builder()
                            .message(ex.getMessage())
                            .now()
                            .jobId(jobId)
                            .build());
                    return handleJobExecutionError(executionResponse.get());
                })
                // avoid blocking IO pool from the event-loop since alternative EmbeddedJobExecutor is blocking.
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .subscribe().with(ignore -> LOGGER.info("Job execution response processing has finished: {}", executionResponse.get()));
    }

    public Uni<JobDetails> handleJobExecutionSuccess(JobExecutionResponse response) {
        LOGGER.debug("Job execution success response received: {}", response);
        return Uni.createFrom().publisher(publisher(ErrorHandling.skipErrorPublisherBuilder(scheduler::handleJobExecutionSuccess, response).buildRs()));
    }

    public Uni<JobDetails> handleJobExecutionError(JobExecutionResponse response) {
        LOGGER.error("Job execution error response received: {}", response);
        return Uni.createFrom().publisher(publisher(ErrorHandling.skipErrorPublisherBuilder(scheduler::handleJobExecutionError, response).buildRs()));
    }
}
