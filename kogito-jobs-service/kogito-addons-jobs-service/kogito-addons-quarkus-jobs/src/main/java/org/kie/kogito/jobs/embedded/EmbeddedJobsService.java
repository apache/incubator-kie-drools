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
package org.kie.kogito.jobs.embedded;

import java.util.concurrent.ExecutionException;

import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.api.JobCallbackResourceDef;
import org.kie.kogito.jobs.service.adapter.JobDetailsAdapter;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.JobStatus;
import org.kie.kogito.jobs.service.scheduler.ReactiveJobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import static mutiny.zero.flow.adapters.AdaptersToFlow.publisher;

@ApplicationScoped
@Alternative
public class EmbeddedJobsService implements JobsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedJobsService.class);

    @Inject
    ReactiveJobScheduler scheduler;

    public EmbeddedJobsService() {
        LOGGER.info("Starting Embedded Job Service");
    }

    @Override
    public String scheduleJob(JobDescription description) {
        try {
            Job job = Job.builder()
                    .id(description.id())
                    .correlationId(description.id())
                    .recipient(new InVMRecipient(new InVMPayloadData(description)))
                    .schedule(JobCallbackResourceDef.buildSchedule(description))
                    .build();

            JobDetails jobDetails = JobDetailsAdapter.from(job);
            LOGGER.debug("Embedded ScheduleProcessJob: {}", jobDetails);

            String outcome = null;

            JobDetails uni = Uni.createFrom().publisher(publisher(scheduler.schedule(jobDetails))).runSubscriptionOn(Infrastructure.getDefaultWorkerPool()).subscribe().asCompletionStage().get();
            outcome = uni.getId();

            LOGGER.debug("Embedded ScheduleProcessJob: {} scheduled", outcome);
            return outcome;
        } catch (InterruptedException | ExecutionException e) {
            LOGGER.error("interrupted execution", e);
            return null;
        }
    }

    @Override
    public boolean cancelJob(String jobId) {
        try {
            LOGGER.debug("Embedded cancelJob: {}", jobId);
            return JobStatus.CANCELED.equals(scheduler.cancel(jobId).toCompletableFuture().get().getStatus());
        } catch (InterruptedException | ExecutionException e) {
            return false;
        }
    }

    @Override
    public String rescheduleJob(JobDescription description) {
        cancelJob(description.id());
        return scheduleJob(description);
    }

}
