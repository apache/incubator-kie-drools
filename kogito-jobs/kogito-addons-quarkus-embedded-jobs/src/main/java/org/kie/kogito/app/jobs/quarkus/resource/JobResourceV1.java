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
package org.kie.kogito.app.jobs.quarkus.resource;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.kie.kogito.app.jobs.impl.InVMPayloadData;
import org.kie.kogito.app.jobs.integrations.JobDescriptionHelper;
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

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Tag(name = "Job Service v1", description = "Job Service version 1 API")
@ApplicationScoped
@Path(RestApiConstants.JOBS_PATH)
public class JobResourceV1 {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobResourceV1.class);

    @Inject
    JobsService jobsService;

    @Inject
    JobStore jobStore;

    @Inject
    JobContextFactory jobContextFactory;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "createJob")
    public ScheduledJob create(Job job) {
        LOGGER.debug("REST create {}", job);
        JobDetails jobDetails = jobStore.find(jobContextFactory.newContext(), job.getId());
        if (jobDetails != null) {
            throw new NotFoundException("Job already created " + job.getId());
        }
        JobDetails newJobDetails = ScheduledJobAdapter.to(ScheduledJobBuilder.from(job));
        JobDescription jobDescription = newJobDetails.getRecipient().<InVMPayloadData> getRecipient().getPayload().getJobDescription();
        jobsService.scheduleJob(jobDescription);
        return ScheduledJobAdapter.of(jobDetails);
    }

    @PATCH
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "patchJob")
    public ScheduledJob patch(@PathParam("id") String id, @RequestBody Job job) {
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

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Operation(operationId = "deleteJob")
    public ScheduledJob delete(@PathParam("id") String id) {
        JobDetails jobDetails = jobStore.find(jobContextFactory.newContext(), id);
        if (jobDetails == null) {
            throw new NotFoundException("Job not found id " + id);
        }
        jobsService.cancelJob(id);
        return ScheduledJobAdapter.of(jobDetails);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Operation(operationId = "getJob")
    public ScheduledJob get(@PathParam("id") String id) {
        JobDetails jobDetails = jobStore.find(jobContextFactory.newContext(), id);
        if (jobDetails == null) {
            throw new NotFoundException("Job not found id " + id);
        }

        return ScheduledJobAdapter.of(jobDetails);
    }
}
