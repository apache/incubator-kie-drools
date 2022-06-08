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

import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DataIndexServiceTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String ERROR_RESPONSE = "something wrong happened!";
    public static final String WORKFLOW_INSTANCE_RESPONSE =
            "{\"data\":{\"ProcessInstances\":[{\"id\":\"2aa76968-5087-433a-a61f-9d3ca2feb270\"},{\"id\":\"2652e27e-127f-4b2a-961b-718a50d97212\"},{\"id\":\"5da599ad-ae7c-492f-b2c1-4a4a559dd64c\"},{\"id\":\"12ee3d38-2467-4cc0-89e6-1e6547f95cd8\"},{\"id\":\"edd5d47a-2fd1-4f8b-87a0-a8ab3f938993\"}]}}";
    public static final String EMPTY_WORKFLOW_INSTANCE_RESPONSE = "{\"data\":{\"ProcessInstances\":[]}}";
    public static final String JOBS_RESPONSE =
            "{\"data\":{\"Jobs\":[{\"id\":\"a859c055-3301-487a-a252-95bc35849d5c\"},{\"id\":\"cfe73332-6942-44f0-b00b-7f63349c7465\"},{\"id\":\"3ebb40a3-9ed6-4be9-bae7-5cb036870327\"},{\"id\":\"04e200d7-c191-4b29-9f6f-09c556ff5a97\"},{\"id\":\"4ab9ddff-6edb-44fa-9ed4-2b4fc797c23d\"}]}}";
    public static final String EMPTY_JOBS_RESPONSE = "{\"data\":{\"Jobs\":[]}}";

    @Mock
    private static DataIndexClient dataIndexClient;

    private DataIndexService dataIndexService;

    @BeforeEach
    public void init() {
        dataIndexService = new DataIndexService(MAPPER, dataIndexClient);
    }

    @Test
    public void testWorkflowInstancesCount() {
        when(dataIndexClient.query(DataIndexService.ALL_WORKFLOW_INSTANCES_IDS_QUERY)).thenReturn(WORKFLOW_INSTANCE_RESPONSE);

        Response response = dataIndexService.workflowInstancesCount();
        assertEquals(200, response.getStatus());
        assertEquals(5, response.getEntity());
    }

    @Test
    public void testEmptyWorkflowInstancesCount() {
        when(dataIndexClient.query(DataIndexService.ALL_WORKFLOW_INSTANCES_IDS_QUERY)).thenReturn(EMPTY_WORKFLOW_INSTANCE_RESPONSE);

        Response response = dataIndexService.workflowInstancesCount();
        assertEquals(200, response.getStatus());
        assertEquals(0, response.getEntity());
    }

    @Test
    public void testWorkflowInstancestCountError() {
        when(dataIndexClient.query(DataIndexService.ALL_WORKFLOW_INSTANCES_IDS_QUERY)).thenReturn(ERROR_RESPONSE);

        Response response = dataIndexService.workflowInstancesCount();
        assertEquals(500, response.getStatus());
    }

    @Test
    public void testJobsCount() {
        when(dataIndexClient.query(DataIndexService.ALL_JOBS_IDS_QUERY)).thenReturn(JOBS_RESPONSE);

        Response response = dataIndexService.jobsCount();
        assertEquals(200, response.getStatus());
        assertEquals(5, response.getEntity());
    }

    @Test
    public void testEmptyJobsCount() {
        when(dataIndexClient.query(DataIndexService.ALL_JOBS_IDS_QUERY)).thenReturn(EMPTY_JOBS_RESPONSE);

        Response response = dataIndexService.jobsCount();
        assertEquals(200, response.getStatus());
        assertEquals(0, response.getEntity());
    }

    @Test
    public void testJobsCountError() {
        when(dataIndexClient.query(DataIndexService.ALL_JOBS_IDS_QUERY)).thenReturn(ERROR_RESPONSE);

        Response response = dataIndexService.jobsCount();
        assertEquals(500, response.getStatus());
    }
}
