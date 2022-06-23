/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.swf.tools.dataindex;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.kie.kogito.swf.tools.dataindex.jobs.JobsResponse;
import org.kie.kogito.swf.tools.dataindex.workflows.WorkflowInstancesResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/dataindex")
public class DataIndexService {

    public static final String ALL_WORKFLOW_INSTANCES_IDS_QUERY = "{ \"operationName\": \"getAllProcessesIds\", \"query\": \"query getAllProcessesIds{  ProcessInstances{ id } }\" }";
    public static final String ALL_JOBS_IDS_QUERY = "{ \"operationName\": \"getAllJobsIds\", \"query\": \"query getAllJobsIds{  Jobs{ id } }\" }";

    private final ObjectMapper mapper;
    private final DataIndexClient dataIndexClient;

    @Inject
    public DataIndexService(ObjectMapper mapper, @RestClient DataIndexClient dataIndexClient) {
        this.mapper = mapper;
        this.dataIndexClient = dataIndexClient;
    }

    @GET
    @Path("/workflowInstances/count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response workflowInstancesCount() {
        try {
            WorkflowInstancesResponse response = doQuery(ALL_WORKFLOW_INSTANCES_IDS_QUERY, WorkflowInstancesResponse.class);
            int workflowInstancesCount = response.getData().getWorkflowInstances().size();
            return Response.ok(workflowInstancesCount).build();
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
}
