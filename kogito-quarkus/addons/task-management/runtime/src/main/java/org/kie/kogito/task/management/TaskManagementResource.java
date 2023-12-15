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
package org.kie.kogito.task.management;

import java.util.List;

import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.workitem.Policies;
import org.kie.kogito.task.management.service.TaskInfo;
import org.kie.kogito.task.management.service.TaskManagementOperations;
import org.kie.kogito.task.management.service.TaskManagementService;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/management/processes")
public class TaskManagementResource {

    private TaskManagementOperations taskService;

    @Inject
    private Processes processes;

    @Inject
    private ProcessConfig processConfig;

    @PostConstruct
    private void init() {
        taskService = new TaskManagementService(processes, processConfig);
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{processId}/instances/{processInstanceId}/tasks/{taskId}")
    public Response updateTask(@PathParam("processId") String processId,
            @PathParam("processInstanceId") String processInstanceId,
            @PathParam("taskId") String taskId,
            @QueryParam("user") final String user,
            @QueryParam("group") final List<String> groups,
            TaskInfo taskInfo) {
        taskService.updateTask(processId, processInstanceId, taskId, taskInfo, true, Policies.of(user, groups));
        return Response.ok().build();
    }

    @PATCH
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{processId}/instances/{processInstanceId}/tasks/{taskId}")
    public TaskInfo partialUpdateTask(@PathParam("processId") String processId,
            @PathParam("processInstanceId") String processInstanceId,
            @PathParam("taskId") String taskId,
            @QueryParam("user") final String user,
            @QueryParam("group") final List<String> groups,
            TaskInfo taskInfo) {
        return taskService.updateTask(processId, processInstanceId, taskId, taskInfo, false, Policies.of(user, groups));
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("{processId}/instances/{processInstanceId}/tasks/{taskId}")
    public TaskInfo getTask(@PathParam("processId") String processId,
            @PathParam("processInstanceId") String processInstanceId,
            @PathParam("taskId") String taskId,
            @QueryParam("user") final String user,
            @QueryParam("group") final List<String> groups) {
        return taskService.getTask(processId, processInstanceId, taskId, Policies.of(user, groups));
    }
}
