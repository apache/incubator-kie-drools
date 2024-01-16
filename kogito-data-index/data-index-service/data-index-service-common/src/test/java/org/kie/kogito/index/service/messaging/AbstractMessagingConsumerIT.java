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
package org.kie.kogito.index.service.messaging;

import java.time.Duration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.kie.kogito.index.storage.DataIndexStorageService;

import io.restassured.http.ContentType;

import jakarta.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;

@Timeout(10000)
public abstract class AbstractMessagingConsumerIT {

    Duration timeout = Duration.ofSeconds(30);

    @Inject
    public DataIndexStorageService cacheService;

    @BeforeEach
    void setup() {
        cacheService.getJobsStorage().clear();
        cacheService.getProcessDefinitionStorage().clear();
        cacheService.getProcessInstanceStorage().clear();
        cacheService.getUserTaskInstanceStorage().clear();
    }

    @AfterEach
    void close() {
        cacheService.getJobsStorage().clear();
        cacheService.getProcessDefinitionStorage().clear();
        cacheService.getProcessInstanceStorage().clear();
        cacheService.getUserTaskInstanceStorage().clear();
    }

    @Test
    void testProcessInstanceEvent() throws Exception {
        sendProcessInstanceEvent();

        String processInstanceId = "2308e23d-9998-47e9-a772-a078cf5b891b";

        await()
                .atMost(timeout)
                .untilAsserted(
                        () -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{ ProcessInstances { id } }\" }")
                                .when().post("/graphql")
                                .then().log().ifValidationFails().statusCode(200)
                                .body("data.ProcessInstances.size()", is(1))
                                .body("data.ProcessInstances[0].id", is(processInstanceId)));
    }

    @Test
    void testProcessDefinitionEvent() throws Exception {
        sendProcessDefinitionEvent();

        String id = "jsongreet";

        await()
                .atMost(timeout)
                .untilAsserted(
                        () -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{ ProcessDefinitions { id } }\" }")
                                .when().post("/graphql")
                                .then().log().ifValidationFails().statusCode(200)
                                .body("data.ProcessDefinitions.size()", is(1))
                                .body("data.ProcessDefinitions[0].id", is(id)));
    }

    @Test
    void testUserTaskInstanceEvent() throws Exception {
        sendUserTaskInstanceEvent();

        String taskId = "45fae435-b098-4f27-97cf-a0c107072e8b";

        await()
                .atMost(timeout)
                .untilAsserted(
                        () -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{ UserTaskInstances { id } }\" }")
                                .when().post("/graphql")
                                .then().log().ifValidationFails().statusCode(200)
                                .body("data.UserTaskInstances.size()", is(1))
                                .body("data.UserTaskInstances[0].id", is(taskId)));
    }

    @Test
    void testJobEvent() throws Exception {
        sendJobEvent();

        String jobId = "8350b8b6-c5d9-432d-a339-a9fc85f642d4_0";

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Jobs { id } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.Jobs.size()", is(1))
                        .body("data.Jobs[0].id", is(jobId)));
    }

    protected abstract void sendUserTaskInstanceEvent() throws Exception;

    protected abstract void sendProcessInstanceEvent() throws Exception;

    protected abstract void sendProcessDefinitionEvent() throws Exception;

    protected abstract void sendJobEvent() throws Exception;

}
