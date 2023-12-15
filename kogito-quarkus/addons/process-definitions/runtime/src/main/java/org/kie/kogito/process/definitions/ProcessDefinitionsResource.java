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
package org.kie.kogito.process.definitions;

import java.io.IOException;
import java.io.StringReader;

import org.kie.kogito.process.ProcessInstancesFactory;
import org.kie.kogito.serverless.workflow.executor.StaticWorkflowApplication;
import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.kie.kogito.serverless.workflow.models.JsonNodeModelInput;
import org.kie.kogito.serverless.workflow.utils.ServerlessWorkflowUtils;
import org.kie.kogito.serverless.workflow.utils.WorkflowFormat;

import com.fasterxml.jackson.databind.node.NullNode;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/")
public class ProcessDefinitionsResource {

    private StaticWorkflowApplication application;

    @Inject
    Instance<ProcessInstancesFactory> factories;

    @PostConstruct
    void init() {
        application = StaticWorkflowApplication.create();
        factories.stream().findFirst().ifPresent(application::processInstancesFactory);
    }

    @PreDestroy
    void cleanup() {
        application.close();
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{id}")
    public Response executeProcess(@PathParam("id") String processId, JsonNodeModelInput input) {
        return Response.status(201).entity(
                application.execute(application.findProcessById(processId).orElseThrow(() -> new IllegalArgumentException("Cannot find process id " + processId)),
                        input != null ? input.toModel() : new JsonNodeModel(NullNode.instance)))
                .build();
    }

    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    @Path("{id}/definition")
    public Response uploadProcess(@PathParam("id") String processId, String content) throws IOException {
        application.process(ServerlessWorkflowUtils.getWorkflow(new StringReader(content), content.startsWith("{") ? WorkflowFormat.JSON : WorkflowFormat.YAML));
        return Response.ok().build();
    }
}
