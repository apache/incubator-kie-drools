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
package org.kie.kogito.quarkus.workflows;

import java.util.Map;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusIntegrationTest
class ParallelStateIT {

    @Test
    void testAllParallelRest() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\":{\"numCompleted\": 3}}").when()
                .post("/parallel")
                .then()
                .statusCode(201)
                .body("workflowdata.firstPart", is("A"))
                .body("workflowdata.secondPart", is("B"))
                .body("workflowdata.thirdPart", is("C"));
    }

    @Test
    void testPartialParallelRest() {
        Map<String, Object> result = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\":{\"numCompleted\": 2}}").when()
                .post("/parallel")
                .then()
                .statusCode(201)
                .extract().body().as(new TypeRef<Map<String, Object>>() {
                });
        assertThat(result).containsKey("workflowdata");
        Map<String, Object> workflowData = (Map<String, Object>) result.get("workflowdata");
        assertThat(workflowData).hasSize(3);
    }
}
