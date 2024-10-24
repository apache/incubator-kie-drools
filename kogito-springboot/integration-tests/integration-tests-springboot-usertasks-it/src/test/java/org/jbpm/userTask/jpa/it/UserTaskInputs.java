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

import org.acme.travels.Address;
import org.acme.travels.Traveller;
import org.junit.jupiter.api.Test;
import org.kie.kogito.it.KogitoSpringbootApplication;
import org.kie.kogito.testcontainers.springboot.PostgreSqlSpringBootTestResource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers = PostgreSqlSpringBootTestResource.class)
public class UserTaskInputs extends BaseUserTaskIT {
    public static final String USER_TASKS_INSTANCE_INPUTS_ENDPOINT = USER_TASKS_INSTANCE_ENDPOINT + "/inputs";

    @Test
    public void testUserTaskInputs() {
        Traveller traveller = new Traveller("John", "Doe", "john.doe@example.com", "American", new Address("main street", "Boston", "10005", "US"));

        String pid = startProcessInstance(traveller);

        String taskId = given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_ENDPOINT)
                .then()
                .statusCode(200)
                .body("$.size()", is(1))
                .extract()
                .path("[0].id");

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("id", equalTo(taskId))
                .body("status.name", equalTo("Reserved"))
                .body("taskName", equalTo("firstLineApproval"))
                .body("potentialUsers", hasItem("manager"))
                .body("potentialGroups", hasItem("managers"))
                .body("inputs.traveller.firstName", equalTo(traveller.getFirstName()))
                .body("inputs.traveller.lastName", equalTo(traveller.getLastName()))
                .body("inputs.traveller.email", equalTo(traveller.getEmail()))
                .body("inputs.traveller.nationality", equalTo(traveller.getNationality()))
                .body("metadata.ProcessType", equalTo("BPMN"))
                .body("metadata.ProcessVersion", equalTo("1.0"))
                .body("metadata.ProcessId", equalTo(PROCESS_ID))
                .body("metadata.ProcessInstanceId", equalTo(pid))
                .body("metadata.ProcessInstanceState", equalTo(1));

        Traveller newTraveller = new Traveller("Ned", "Stark", "n.stark@winterfell.com", "Northern", new Address("main street", "Winterfell", "10005", "WF"));

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .body(Map.of("traveller", newTraveller))
                .put(USER_TASKS_INSTANCE_INPUTS_ENDPOINT, taskId)
                .then()
                .statusCode(200);

        given().contentType(ContentType.JSON)
                .when()
                .queryParam("user", "manager")
                .queryParam("group", "department-managers")
                .get(USER_TASKS_INSTANCE_ENDPOINT, taskId)
                .then()
                .statusCode(200)
                .body("inputs.traveller.firstName", equalTo(newTraveller.getFirstName()))
                .body("inputs.traveller.lastName", equalTo(newTraveller.getLastName()))
                .body("inputs.traveller.email", equalTo(newTraveller.getEmail()))
                .body("inputs.traveller.nationality", equalTo(newTraveller.getNationality()))
                .body("inputs.traveller.address.street", equalTo(newTraveller.getAddress().getStreet()))
                .body("inputs.traveller.address.city", equalTo(newTraveller.getAddress().getCity()))
                .body("inputs.traveller.address.zipCode", equalTo(newTraveller.getAddress().getZipCode()))
                .body("inputs.traveller.address.country", equalTo(newTraveller.getAddress().getCountry()));

        abortProcessInstance(pid);
    }
}
