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
package org.kie.kogito.jobs.service.resource.v2;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.kie.kogito.jobs.service.adapter.JobDetailsAdapter;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.resource.RestApiConstants;
import org.kie.kogito.jobs.service.scheduler.impl.TimerDelegateJobScheduler;
import org.kie.kogito.jobs.service.validation.JobValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.smallrye.mutiny.Uni;

@ApplicationScoped
@Path(RestApiConstants.V2 + RestApiConstants.JOBS_PATH)
public class JobResourceV2 {
    private static final Logger LOGGER = LoggerFactory.getLogger(JobResourceV2.class);
    @SuppressWarnings("squid:S1075")

    @Inject
    TimerDelegateJobScheduler scheduler;

    @Inject
    ReactiveJobRepository jobRepository;

    @Inject
    JobValidator jobValidator;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "createJobV2")
    public Uni<Job> create(Job job) {
        LOGGER.debug("REST create {}", job);
        jobValidator.validateToCreate(job);
        JobDetails jobDetails = JobDetailsAdapter.from(job);
        return Uni.createFrom().publisher(scheduler.schedule(jobDetails))
                .onItem().ifNull().failWith(new RuntimeException("Failed to schedule job " + job))
                .onItem().transform(JobDetailsAdapter::toJob);
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Operation(operationId = "deleteJobV2")
    public Uni<Job> delete(@PathParam("id") String id) {
        return Uni.createFrom().completionStage(scheduler.cancel(id))
                .onItem().ifNull().failWith(new NotFoundException("Failed to cancel job scheduling for jobId " + id))
                .onItem().transform(JobDetailsAdapter::toJob);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Operation(operationId = "getJobV2")
    public Uni<Job> get(@PathParam("id") String id) {
        return Uni.createFrom().completionStage(jobRepository.get(id))
                .onItem().ifNull().failWith(new NotFoundException("Job not found id " + id))
                .onItem().transform(JobDetailsAdapter::toJob);
    }
}
