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

package org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.jobs.JobsResponse;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.processes.ProcessInstancesResponse;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.tasks.TasksResponse;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.FormsStorage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/dataindex")
public class DataIndexService {

    public static final String ALL_TASKS_IDS_QUERY = "{ \"operationName\": \"getAllTasksIds\", \"query\": \"query getAllTasksIds{  UserTaskInstances{ id } }\" }";
    public static final String ALL_PROCESS_INSTANCES_IDS_QUERY = "{ \"operationName\": \"getAllProcessesIds\", \"query\": \"query getAllProcessesIds{  ProcessInstances{ id } }\" }";
    public static final String ALL_JOBS_IDS_QUERY = "{ \"operationName\": \"getAllJobsIds\", \"query\": \"query getAllJobsIds{  Jobs{ id } }\" }";

    private final ObjectMapper mapper;
    private final DataIndexClient dataIndexClient;
    private final FormsStorage formsStorage;

    @Inject
    public DataIndexService(ObjectMapper mapper, @RestClient DataIndexClient dataIndexClient, FormsStorage formsStorage) {
        this.mapper = mapper;
        this.dataIndexClient = dataIndexClient;
        this.formsStorage = formsStorage;
    }

    @GET
    @Path("/tasks/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response tasksCount() {
        try {
            TasksResponse tasksResponse = doQuery(ALL_TASKS_IDS_QUERY, TasksResponse.class);
            int tasksCount = tasksResponse.getData().getTasks().size();
            return Response.ok(tasksCount).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/processInstances/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response processInstancesCount() {
        try {
            ProcessInstancesResponse response = doQuery(ALL_PROCESS_INSTANCES_IDS_QUERY, ProcessInstancesResponse.class);
            int processInstancesCount = response.getData().getProcessInstances().size();
            return Response.ok(processInstancesCount).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/jobs/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response jobsCount() {
        try {
            JobsResponse jobsResponse = doQuery(ALL_JOBS_IDS_QUERY, JobsResponse.class);
            int jobsCount = jobsResponse.getData().getJobs().size();
            return Response.ok(jobsCount).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    private <T> T doQuery(final String query, Class<T> type) throws JsonProcessingException {
        final String response = dataIndexClient.query(query);
        return mapper.readValue(response, type);
    }

    @GET
    @Path("/forms/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response formsCount() {
        try {
            int formsCount = formsStorage.getFormsCount();
            return Response.ok(formsCount).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }
}
