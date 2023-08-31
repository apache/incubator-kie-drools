/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.index.service.messaging;

import java.time.Duration;
import java.util.Collection;

import javax.inject.Inject;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.storage.DataIndexStorageService;
import org.kie.kogito.persistence.protobuf.ProtobufService;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.isA;

public abstract class AbstractDomainMessagingConsumerIT {

    Duration timeout = Duration.ofSeconds(30);

    @Inject
    public ProtobufService protobufService;

    @Inject
    public DataIndexStorageService cacheService;

    @BeforeEach
    void setup() throws Exception {
        protobufService.registerProtoBufferType(getTestProtobufFileContent());
        cacheService.getProcessDefinitionsCache().clear();
        cacheService.getProcessInstancesCache().clear();
        cacheService.getUserTaskInstancesCache().clear();
        if (cacheService.getDomainModelCache("travels") != null) {
            cacheService.getDomainModelCache("travels").clear();
        }
    }

    @AfterEach
    void close() {
        cacheService.getProcessDefinitionsCache().clear();
        cacheService.getProcessInstancesCache().clear();
        cacheService.getUserTaskInstancesCache().clear();
        if (cacheService.getDomainModelCache("travels") != null) {
            cacheService.getDomainModelCache("travels").clear();
        }
    }

    @Test
    void testProcessInstanceEvent() throws Exception {
        sendProcessInstanceEvent();

        String processInstanceId = "2308e23d-9998-47e9-a772-a078cf5b891b";

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels { id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels", isA(Collection.class));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON)
                        .body("{ \"query\" : \"{ Travels { id, metadata { processInstances { id } } } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.Travels[0].id", is("2308e23d-9998-47e9-a772-a078cf5b891b"))
                        .body("data.Travels[0].metadata.processInstances[0].id", is(processInstanceId)));
    }

    @Test
    void testUserTaskInstanceEvent() throws Exception {
        sendUserTaskInstanceEvent();

        String taskId = "45fae435-b098-4f27-97cf-a0c107072e8b";

        given().contentType(ContentType.JSON).body("{ \"query\" : \"{ Travels { id } }\" }")
                .when().post("/graphql")
                .then().log().ifValidationFails().statusCode(200)
                .body("data.Travels", isA(Collection.class));

        await()
                .atMost(timeout)
                .untilAsserted(() -> given().contentType(ContentType.JSON)
                        .body("{ \"query\" : \"{ Travels { id, metadata { userTasks { id } } } }\" }")
                        .when().post("/graphql")
                        .then().log().ifValidationFails().statusCode(200)
                        .body("data.Travels[0].id", is("2308e23d-9998-47e9-a772-a078cf5b891b"))
                        .body("data.Travels[0].metadata.userTasks[0].id", is(taskId)));
    }

    protected abstract void sendUserTaskInstanceEvent() throws Exception;

    protected abstract void sendProcessInstanceEvent() throws Exception;

    protected abstract String getTestProtobufFileContent() throws Exception;
}
