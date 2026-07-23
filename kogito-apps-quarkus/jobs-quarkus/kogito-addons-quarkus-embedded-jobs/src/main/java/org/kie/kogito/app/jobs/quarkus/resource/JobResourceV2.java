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
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.kie.kogito.app.jobs.impl.InVMRecipient;
import org.kie.kogito.app.jobs.spi.JobContextFactory;
import org.kie.kogito.app.jobs.spi.JobStore;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.service.adapter.JobDetailsAdapter;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.model.JobDetails;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Tag(name = "Job Service v2", description = "Job Service version 2 API")
@ApplicationScoped
@Path(RestApiConstants.V2 + RestApiConstants.JOBS_PATH)
public class JobResourceV2 {

    @Inject
    JobsService jobsService;

    @Inject
    JobStore jobStore;

    @Inject
    JobContextFactory jobContextFactory;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Operation(operationId = "createJobV2")
    public Job create(Job job) {
        JobDescription jobDescription = ((InVMRecipient) job.getRecipient()).getPayload().getJobDescription();
        jobsService.scheduleJob(jobDescription);
        return job;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Operation(operationId = "deleteJobV2")
    public Job delete(@PathParam("id") String id) {
        JobDetails jobDetails = jobStore.find(jobContextFactory.newContext(), id);
        if (jobDetails == null) {
            throw new NotFoundException("Job not found id " + id);
        }
        jobsService.cancelJob(jobDetails.getId());
        return JobDetailsAdapter.toJob(jobDetails);

    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    @Operation(operationId = "getJobV2")
    public Job get(@PathParam("id") String id) {
        JobDetails jobDetails = jobStore.find(jobContextFactory.newContext(), id);
        if (jobDetails == null) {
            throw new NotFoundException("Job not found id " + id);
        }
        return JobDetailsAdapter.toJob(jobDetails);
    }
}
