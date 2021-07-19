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
package org.kie.kogito.task.management;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.kie.kogito.process.ProcessConfig;
import org.kie.kogito.process.Processes;
import org.kie.kogito.process.workitem.Policies;
import org.kie.kogito.task.management.service.TaskInfo;
import org.kie.kogito.task.management.service.TaskManagementOperations;
import org.kie.kogito.task.management.service.TaskManagementService;

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
