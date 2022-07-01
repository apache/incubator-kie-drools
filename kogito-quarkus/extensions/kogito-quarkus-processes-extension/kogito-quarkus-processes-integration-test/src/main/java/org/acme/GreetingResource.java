/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.acme;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kie.kogito.Model;
import org.kie.kogito.incubation.application.AppRoot;
import org.kie.kogito.incubation.common.DataContext;
import org.kie.kogito.incubation.common.MapDataContext;
import org.kie.kogito.incubation.processes.ProcessIds;
import org.kie.kogito.incubation.processes.services.StraightThroughProcessService;
import org.kie.kogito.process.Process;

@Path("/hello")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GreetingResource {

    @Inject
    AppRoot appRoot;
    @Inject
    StraightThroughProcessService svc;

    @Inject
    @Named("approvals")
    Process<? extends Model> approvalProcess;

    @POST
    public DataContext hello(Map<String, Object> payload) {
        // path: /processes/scripts
        System.out.println(payload);

        var id = appRoot.get(ProcessIds.class).get("scripts");
        return svc.evaluate(id, MapDataContext.from(payload));
    }

    @GET
    @Path("/version")
    public Response version() {
        return Response.ok(Map.of("version", approvalProcess.version())).build();
    }

}