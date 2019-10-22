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

package org.kie.kogito.jobs.service.resource;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.reactive.streams.operators.ReactiveStreams;
import org.kie.kogito.jobs.api.Job;
import org.kie.kogito.jobs.service.model.ScheduledJob;
import org.kie.kogito.jobs.service.repository.ReactiveJobRepository;
import org.kie.kogito.jobs.service.scheduler.impl.VertxJobScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/job")
public class JobResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobResource.class);

    @Inject
    private VertxJobScheduler scheduler;

    @Inject
    private ReactiveJobRepository reactiveJobRepository;

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public CompletionStage<Job> create(Job job) {
        LOGGER.debug("REST create {}", job);
        final CompletionStage<Job> response = ReactiveStreams.fromPublisher(scheduler.schedule(job))
                .map(ScheduledJob::getJob)
                .findFirst()
                .run()
                .thenApply(j -> j.orElseThrow(() -> new RuntimeException("Failed to schedule job " + job.getId())))
                .whenCompleteAsync((r, t) -> Optional
                        .ofNullable(t)
                        .ifPresent(ex -> LOGGER.error("Error Scheduling Job: {}. Details: {}", job, t)));
        return response;
    }

    @DELETE
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public CompletionStage<Job> delete(@PathParam("id") String id) {
        LOGGER.debug("REST delete id {}", id);
        return  scheduler
                .cancel(id)
                .thenApply(result -> Optional
                        .ofNullable(result)
                        .map(ScheduledJob::getJob)
                        .orElseThrow(() -> new RuntimeException("Failed to cancel job scheduling")));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/{id}")
    public CompletionStage<Job> get(@PathParam("id") String id) {
        LOGGER.debug("REST get {}", id);
        return reactiveJobRepository
                .get(id)
                .thenApply(ScheduledJob::getJob);
    }
}