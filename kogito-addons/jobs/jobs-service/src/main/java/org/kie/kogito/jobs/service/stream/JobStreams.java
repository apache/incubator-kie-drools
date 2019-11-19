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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.kie.kogito.jobs.service.model.JobExecutionResponse;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.scheduler.ReactiveJobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class that configure the Consumers for Job Streams,like Job Executed, Job Error... and execute the actions for each
 * received item.
 */
@ApplicationScoped
public class JobStreams {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobStreams.class);

    private ReactiveJobScheduler<ScheduledJob> jobScheduler;

    @Inject
    public JobStreams(ReactiveJobScheduler<ScheduledJob> jobScheduler) {
        this.jobScheduler = jobScheduler;
    }

    @Incoming(AvailableStreams.JOB_ERROR)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public void jobErrorProcessor(JobExecutionResponse error) {
        LOGGER.warn("Error received {}", error);
        jobScheduler.handleJobExecutionError(error)
                .findFirst()
                .run()
                .thenAccept(job -> LOGGER.info("Rescheduled {}", job.orElse(null)));
    }

    @Incoming(AvailableStreams.JOB_SUCCESS)
    @Acknowledgment(Acknowledgment.Strategy.PRE_PROCESSING)
    public void jobSuccessProcessor(JobExecutionResponse response) {
        LOGGER.warn("Success received {}", response);
        jobScheduler.handleJobExecutionSuccess(response)
                .findFirst()
                .run();
    }
}
