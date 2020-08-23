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
import org.acme.maintenancescheduling.domain.MaintenanceCrew;

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

@Path("/crews")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Transactional
public class MaintenanceCrewResource {

    @GET
    public List<MaintenanceCrew> getAllCrews() {
        return MaintenanceCrew.listAll(Sort.by("crewName").and("id"));
    }

    @POST
    public Response add(MaintenanceCrew crew) {
        MaintenanceCrew.persist(crew);
        return Response.accepted(crew).build();
    }

    @DELETE
    @Path("{crewId}")
    public Response delete(@PathParam("crewId") Long crewId) {
        MaintenanceCrew crew = MaintenanceCrew.findById(crewId);
        if (crew == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        crew.delete();
        return Response.status(Response.Status.OK).build();
    }
}
