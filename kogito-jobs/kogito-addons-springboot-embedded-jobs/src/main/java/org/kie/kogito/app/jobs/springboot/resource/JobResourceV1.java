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
package org.kie.kogito.app.jobs.springboot.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.kie.kogito.app.jobs.impl.InVMPayloadData;
import org.kie.kogito.app.jobs.impl.JobDescriptionHelper;
import org.kie.kogito.app.jobs.spi.JobContextFactory;
import org.kie.kogito.app.jobs.spi.JobStore;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.service.adapter.ScheduledJobAdapter;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.model.ScheduledJob.ScheduledJobBuilder;
import org.kie.kogito.timer.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.ws.rs.NotFoundException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Job Service v1", description = "Job Service version 1 API")
@RestController
@RequestMapping(RestApiConstants.JOBS_PATH)
public class JobResourceV1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobResourceV1.class);

    @Autowired
    JobsService jobsService;

    @Autowired
    JobStore jobStore;

    @Autowired
    JobContextFactory jobContextFactory;

    @Operation(operationId = "createJob")
    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ScheduledJob create(Job job) {
        LOGGER.debug("REST create {}", job);
        JobDetails jobDetails = jobStore.find(jobContextFactory.newContext(), job.getId());
        if (jobDetails != null) {
            throw new NotFoundException("Job already created " + job.getId());
        }
        JobDetails newJobDetails = ScheduledJobAdapter.to(ScheduledJobBuilder.from(job));
        JobDescription jobDescription = newJobDetails.getRecipient().<InVMPayloadData> getRecipient().getPayload()
                .getJobDescription();
        jobsService.scheduleJob(jobDescription);
        return ScheduledJobAdapter.of(jobDetails);
    }

    @Operation(operationId = "patchJob")
    @PatchMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ScheduledJob patch(@PathVariable("id") String id, @RequestBody Job job) {
        LOGGER.debug("REST patch update {}", job);
        // validating allowed patch attributes
        JobDetails jobDetails = jobStore.find(jobContextFactory.newContext(), id);
        if (jobDetails == null) {
            throw new NotFoundException("Job not found id " + id);
        }
        JobDetails jobToBeMerged = ScheduledJobAdapter.to(ScheduledJobBuilder.from(job));
        Trigger trigger = jobToBeMerged.getTrigger();
        JobDescription jobDescription = jobDetails.getRecipient().<InVMPayloadData> getRecipient().getPayload().getJobDescription();
        jobsService.rescheduleJob(JobDescriptionHelper.newJobDescription(jobDescription, trigger));
        return ScheduledJobAdapter.of(jobDetails);
    }

    @Operation(operationId = "deleteJob")
    @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ScheduledJob delete(@PathVariable("id") String id) {
        JobDetails jobDetails = jobStore.find(jobContextFactory.newContext(), id);
        if (jobDetails == null) {
            throw new NotFoundException("Job not found id " + id);
        }
        jobsService.cancelJob(id);
        return ScheduledJobAdapter.of(jobDetails);
    }

    @Operation(operationId = "getJob")
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ScheduledJob get(@PathVariable("id") String id) {
        JobDetails jobDetails = jobStore.find(jobContextFactory.newContext(), id);
        if (jobDetails == null) {
            throw new NotFoundException("Job not found id " + id);
        }

        return ScheduledJobAdapter.of(jobDetails);
    }
}
