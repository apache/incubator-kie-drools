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
package org.kie.kogito.index.sprinboot.addon.it;

import java.util.Map;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.KogitoSpringBootApplication;
import org.kie.kogito.index.jpa.storage.ProcessDefinitionEntityStorage;
import org.kie.kogito.index.jpa.storage.ProcessInstanceEntityStorage;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.index.model.ProcessInstance;
import org.kie.kogito.testcontainers.springboot.PostgreSqlSpringBootTestResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.enableLoggingOfRequestAndResponseIfValidationFails;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringBootApplication.class)
@ContextConfiguration(initializers = PostgreSqlSpringBootTestResource.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class JPASpringBootAddonDataIndexPersistenceTest {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    ProcessDefinitionEntityStorage processDefinitionEntityStorage;

    @Autowired
    ProcessInstanceEntityStorage processInstanceEntityStorage;

    static {
        enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void setPort() {
        RestAssured.port = randomServerPort;
    }

    @Test
    void testDataIndexAddon() {

        String processDefId = "hello";
        String version = "1.0";

        ProcessDefinition definition = processDefinitionEntityStorage.get(new ProcessDefinitionKey(processDefId, version));

        Assertions.assertThat(definition)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", processDefId)
                .hasFieldOrPropertyWithValue("version", version)
                .hasFieldOrPropertyWithValue("name", processDefId)
                .hasFieldOrPropertyWithValue("type", "BPMN")
                .hasFieldOrProperty("description")
                .hasFieldOrProperty("source")
                .hasFieldOrProperty("addons")
                .hasFieldOrProperty("roles")
                .hasFieldOrProperty("endpoint")
                .hasFieldOrProperty("nodes");

        Assertions.assertThat(definition.getSource())
                .isBlank();

        Assertions.assertThat(definition.getAddons())
                .hasSize(2)
                .contains("jdbc-persistence", "process-svg");

        Assertions.assertThat(definition.getNodes())
                .hasSize(2);

        String processInstanceId = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(Map.of())
                .post("/hello")
                .then()
                .statusCode(201)
                .body("id", is(notNullValue()))
                .extract().path("id");

        Assertions.assertThat(processInstanceId)
                .isNotBlank();

        ProcessInstance instance = processInstanceEntityStorage.get(processInstanceId);

        Assertions.assertThat(instance)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", processInstanceId)
                .hasFieldOrPropertyWithValue("processId", processDefId)
                .hasFieldOrPropertyWithValue("processName", processDefId)
                .hasFieldOrPropertyWithValue("version", version)
                .hasFieldOrPropertyWithValue("state", 2)
                .hasFieldOrProperty("nodes")
                .hasFieldOrProperty("start")
                .hasFieldOrProperty("end")
                .hasFieldOrProperty("lastUpdate");
    }

}
