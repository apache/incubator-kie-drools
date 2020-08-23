/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acme.maintenancescheduling.rest;

import io.quarkus.panache.common.Sort;
import org.acme.maintenancescheduling.domain.MaintenanceJob;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/jobs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class MaintenanceJobResource {

    @GET
    public List<MaintenanceJob> getAllJobs() {
        return MaintenanceJob.listAll(Sort.by("maintainableUnit").and("startingTimeGrain").and("readyGrainIndex")
                .and("deadlineGrainIndex").and("id"));
    }

    @POST
    public Response add(MaintenanceJob job) {
        MaintenanceJob.persist(job);
        return Response.accepted(job).build();
    }

    @DELETE
    @Path("{jobId}")
    public Response delete(@PathParam("jobId") Long jobId) {
        MaintenanceJob job = MaintenanceJob.findById(jobId);
        if (job == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        job.delete();
        return Response.status(Response.Status.OK).build();
    }
}
