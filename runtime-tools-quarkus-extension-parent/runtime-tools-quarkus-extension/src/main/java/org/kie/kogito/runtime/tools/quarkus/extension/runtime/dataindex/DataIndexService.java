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

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.jobs.JobsInstancesAdapter;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.jobs.JobsResponse;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.processes.ProcessInstancesAdapter;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.processes.ProcessInstancesResponse;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.tasks.TasksResponse;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.dataindex.tasks.UserTaskInstancesAdapter;
import org.kie.kogito.runtime.tools.quarkus.extension.runtime.forms.FormsStorage;

import io.quarkus.arc.Arc;

@Path("/dataindex")
public class DataIndexService {

    public static final String ALL_TASKS_IDS_QUERY = "{ \"operationName\": \"getAllTasksIds\", \"query\": \"query getAllTasksIds{  UserTaskInstances{ id } }\" }";
    public static final String ALL_PROCESS_INSTANCES_IDS_QUERY = "{ \"operationName\": \"getAllProcessesIds\", \"query\": \"query getAllProcessesIds{  ProcessInstances{ id } }\" }";
    public static final String ALL_JOBS_IDS_QUERY = "{ \"operationName\": \"getAllJobsIds\", \"query\": \"query getAllJobsIds{  Jobs{ id } }\" }";

    @GET
    @Path("/tasks/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response tasksCount() {
        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withAdapters(new UserTaskInstancesAdapter()))) {
            final String tasksQueryResponse = getDataIndexClient().query(ALL_TASKS_IDS_QUERY);

            TasksResponse tasksResponse = jsonb.fromJson(tasksQueryResponse,
                    TasksResponse.class);

            int tasksCount = tasksResponse.getData().getUserTaskInstances().size();

            return Response.ok(tasksCount).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/processInstances/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response processInstancesCount() {
        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withAdapters(new ProcessInstancesAdapter()))) {
            final String processInstancesQueryResponse = getDataIndexClient().query(ALL_PROCESS_INSTANCES_IDS_QUERY);

            ProcessInstancesResponse processInstancesResponse = jsonb.fromJson(processInstancesQueryResponse,
                    ProcessInstancesResponse.class);

            int processInstancesCount = processInstancesResponse.getData().getProcessInstances().size();

            return Response.ok(processInstancesCount).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/jobs/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response jobsCount() {
        try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withAdapters(new JobsInstancesAdapter()))) {
            final String jobsQueryResponse = getDataIndexClient().query(ALL_JOBS_IDS_QUERY);

            JobsResponse jobsResponse = jsonb.fromJson(jobsQueryResponse,
                    JobsResponse.class);

            int jobsCount = jobsResponse.getData().getJobs().size();

            return Response.ok(jobsCount).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/forms/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response formsCount() {
        try {
            int formsCount = getFormsStorage().getFormsCount();
            return Response.ok(formsCount).build();
        } catch (Exception e) {
            return Response.serverError().build();
        }
    }

    private DataIndexClient getDataIndexClient() {
        return Arc.container().instance(DataIndexClient.class,
                RestClient.LITERAL).get();
    }

    private FormsStorage getFormsStorage() {
        return Arc.container().instance(FormsStorage.class).get();
    }
}
