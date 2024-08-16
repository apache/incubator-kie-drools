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
package org.kie.kogito.integrationtests.springboot;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.emptyOrNullString;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
public class ParentSubProcessTest extends BaseRestTest {

    @Test
    public void testParentSubProcessRest() {
        String name = RandomStringUtils.randomAlphanumeric(10);
        String review = RandomStringUtils.randomAlphanumeric(10);
        String addDealPayload = "{\"name\" : \"" + name + "\"}";
        String parentId = given().contentType(ContentType.JSON).accept(ContentType.JSON).body(addDealPayload)
                .when().post("/parent")
                .then().statusCode(201)
                .body("id", notNullValue())
                .extract().path("id");

        given().accept(ContentType.JSON)
                .when().get("/parent")
                .then().statusCode(200)
                .body("$.size()", is(1))
                .body("[0].id", is(parentId))
                .body("[0].name", is(name))
                .body("[0].review", nullValue());

        String subProcessId = given().accept(ContentType.JSON)
                .when().get("/subprocess")
                .then().statusCode(200)
                .body("$.size()", is(1))
                .body("[0].id", notNullValue())
                .body("[0].name", is(name))
                .body("[0].review", nullValue())
                .extract().path("[0].id");

        // get task
        String taskId = given().accept(ContentType.JSON)
                .when().get("/subprocess/{uuid}/tasks?user=john", subProcessId)
                .then().statusCode(200)
                .body("$.size()", is(1))
                .body("[0].parameters.param_name", is(name))
                .extract().path("[0].id");

        // complete review task
        given().contentType(ContentType.JSON).accept(ContentType.JSON).body("{\"review\" : \"" + review + "\"}")
                .when().post("/subprocess/{uuid}/review/{tuuid}?user=john", subProcessId, taskId)
                .then().statusCode(200)
                .body("id", is(subProcessId))
                .body("review", is(review))
                .body("name", is(name));

        //verify no subprocess
        given().accept(ContentType.JSON)
                .when().get("/subprocess")
                .then().statusCode(200).body("$.size()", is(0));

        given().contentType(ContentType.JSON)
                .when().post("/parent/{pid}/end", parentId)
                .then().statusCode(200)
                .body("id", not(emptyOrNullString()))
                .body("review", is(review))
                .body("name", is(name));

        //verify no parent process
        given().accept(ContentType.JSON)
                .when().get("/parent")
                .then().statusCode(200).body("$.size()", is(0));
    }
}
