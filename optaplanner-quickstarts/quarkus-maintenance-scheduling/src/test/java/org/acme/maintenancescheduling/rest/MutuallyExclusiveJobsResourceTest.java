/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.acme.maintenancescheduling.rest;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.acme.maintenancescheduling.domain.MutuallyExclusiveJobs;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@QuarkusTest
public class MutuallyExclusiveJobsResourceTest {

    @Test
    public void getAll() {
        List<MutuallyExclusiveJobs> mutuallyExclusiveJobsList = given()
                .when().get("/mutuallyExclusiveJobs")
                .then()
                .statusCode(200)
                .extract().body().jsonPath().getList(".", MutuallyExclusiveJobs.class);
        assertFalse(mutuallyExclusiveJobsList.isEmpty());
        MutuallyExclusiveJobs firstMutuallyExclusiveJobs = mutuallyExclusiveJobsList.get(0);
        assertEquals(3, firstMutuallyExclusiveJobs.getMutexJobs().size());
    }

    @Test
    public void addAndRemove() {
        MutuallyExclusiveJobs mutuallyExclusiveJobs = given()
                .when()
                .contentType(ContentType.JSON)
                .body(new MutuallyExclusiveJobs())
                .post("/mutuallyExclusiveJobs")
                .then()
                .statusCode(202)
                .extract().as(MutuallyExclusiveJobs.class);

        given()
                .when()
                .delete("/mutuallyExclusiveJobs/{id}", mutuallyExclusiveJobs.getId())
                .then()
                .statusCode(200);
    }
}
