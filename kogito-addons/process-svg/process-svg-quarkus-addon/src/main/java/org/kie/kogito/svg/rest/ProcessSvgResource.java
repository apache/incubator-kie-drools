/*
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.svg.rest;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.kie.kogito.svg.ProcessSvgService;

@ApplicationScoped
@Path("/svg")
public class ProcessSvgResource {

    @Inject
    ProcessSvgService service;

    @GET
    @Path("processes/{processId}")
    @Produces("image/svg+xml")
    public Response getProcessSvg(@PathParam("processId") String processId) {
        Optional<String> processSvg = service.getProcessSvg(processId);
        if (processSvg.isPresent()) {
            return Response.ok(processSvg.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @GET
    @Path("processes/{processId}/instances/{processInstanceId}")
    @Produces("image/svg+xml")
    public Response getExecutionPathByProcessInstanceId(@PathParam("processId") String processId,
                                                        @PathParam("processInstanceId") String processInstanceId) {
        Optional<String> processInstanceSvg = service.getProcessInstanceSvg(processId, processInstanceId);
        if (processInstanceSvg.isPresent()) {
            return Response.ok(processInstanceSvg.get()).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Inject
    protected void setProcessSvgService(ProcessSvgService service) {
        this.service = service;
    }
}

