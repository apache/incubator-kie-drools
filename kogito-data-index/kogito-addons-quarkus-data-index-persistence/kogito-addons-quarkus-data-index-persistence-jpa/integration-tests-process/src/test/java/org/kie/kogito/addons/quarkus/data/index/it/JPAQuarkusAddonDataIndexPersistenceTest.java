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
package org.kie.kogito.addons.quarkus.data.index.it;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.index.jpa.storage.ProcessDefinitionEntityStorage;
import org.kie.kogito.index.jpa.storage.ProcessInstanceEntityStorage;
import org.kie.kogito.index.model.ProcessDefinition;
import org.kie.kogito.index.model.ProcessDefinitionKey;
import org.kie.kogito.index.model.ProcessInstance;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.h2.H2DatabaseTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import jakarta.inject.Inject;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

@QuarkusTest
@QuarkusTestResource(H2DatabaseTestResource.class)
class JPAQuarkusAddonDataIndexPersistenceTest {

    @Inject
    ProcessDefinitionEntityStorage processDefinitionEntityStorage;

    @Inject
    ProcessInstanceEntityStorage processInstanceEntityStorage;

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
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
                .isNotBlank();

        Assertions.assertThat(definition.getAddons())
                .hasSize(3)
                .contains("jdbc-persistence", "process-svg", "source-files");

        Assertions.assertThat(definition.getNodes())
                .hasSize(2);

        String processInstanceId = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
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
