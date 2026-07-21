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

package org.jbpm.userTask.jpa.it;

import java.util.Map;

import org.acme.travels.Traveller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

public abstract class BaseUserTaskIT {
    public static final String PROCESS_ID = "approvals";
    public static final String USER_TASKS_ENDPOINT = "/usertasks/instance";
    public static final String USER_TASKS_INSTANCE_ENDPOINT = USER_TASKS_ENDPOINT + "/{taskId}";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public String startProcessInstance(Traveller traveller) {
        final String pid = given().contentType(ContentType.JSON)
                .when()
                .body(Map.of("traveller", traveller))
                .post("/{processId}", PROCESS_ID)
                .then()
                .statusCode(201)
                .header("Location", not(emptyOrNullString()))
                .body("id", not(emptyOrNullString()))
                .extract()
                .path("id");

        given()
                .accept(ContentType.JSON)
                .when()
                .get("/{processId}/{id}", PROCESS_ID, pid)
                .then()
                .statusCode(200)
                .body("id", equalTo(pid))
                .body("traveller.firstName", equalTo(traveller.getFirstName()))
                .body("traveller.lastName", equalTo(traveller.getLastName()))
                .body("traveller.email", equalTo(traveller.getEmail()))
                .body("traveller.nationality", equalTo(traveller.getNationality()));

        return pid;
    }
}
