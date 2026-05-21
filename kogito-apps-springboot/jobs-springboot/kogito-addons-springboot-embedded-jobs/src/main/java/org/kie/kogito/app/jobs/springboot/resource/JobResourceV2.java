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
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.kie.kogito.app.jobs.impl.InVMRecipient;
import org.kie.kogito.app.jobs.spi.JobContextFactory;
import org.kie.kogito.app.jobs.spi.JobStore;
import org.kie.kogito.jobs.JobDescription;
import org.kie.kogito.jobs.JobsService;
import org.kie.kogito.jobs.service.adapter.JobDetailsAdapter;
import org.kie.kogito.jobs.service.api.Job;
import org.kie.kogito.jobs.service.model.JobDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.ws.rs.NotFoundException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Tag(name = "Job Service v2", description = "Job Service version 2 API")
@RestController
@RequestMapping(RestApiConstants.V2 + RestApiConstants.JOBS_PATH)
public class JobResourceV2 {

    @Autowired
    JobsService jobsService;

    @Autowired
    JobStore jobStore;

    @Autowired
    JobContextFactory jobContextFactory;

    @Operation(operationId = "createJobV2")
    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Job create(Job job) {
        JobDescription jobDescription = ((InVMRecipient) job.getRecipient()).getPayload().getJobDescription();
        jobsService.scheduleJob(jobDescription);
        return job;
    }

    @Operation(operationId = "deleteJobV2")
    @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Job delete(@PathVariable("id") String id) {
        JobDetails jobDetails = jobStore.find(jobContextFactory.newContext(), id);
        if (jobDetails == null) {
            throw new NotFoundException("Job not found id " + id);
        }
        jobsService.cancelJob(jobDetails.getId());
        return JobDetailsAdapter.toJob(jobDetails);

    }

    @Operation(operationId = "getJobV2")
    @GetMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Job get(@PathVariable("id") String id) {
        JobDetails jobDetails = jobStore.find(jobContextFactory.newContext(), id);
        if (jobDetails == null) {
            throw new NotFoundException("Job not found id " + id);
        }
        return JobDetailsAdapter.toJob(jobDetails);
    }
}
