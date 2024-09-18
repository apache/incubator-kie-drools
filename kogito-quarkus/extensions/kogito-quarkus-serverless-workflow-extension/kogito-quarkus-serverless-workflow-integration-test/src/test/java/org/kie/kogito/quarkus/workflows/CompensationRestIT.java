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

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusIntegrationTest
public class CompensationRestIT {

    @Test
    public void testErrorRest1() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"shouldCompensate\" : true, \"number\":2}").when()
                .post("/compensation")
                .then()
                .statusCode(201)
                .body("workflowdata.compensated", is(true));
    }

    @Test
    public void testErrorRest2() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"shouldCompensate\" : true, \"number\":3}").when()
                .post("/compensation")
                .then()
                .statusCode(201)
                .body("workflowdata.compensated", is(true))
                .body("workflowdata.isEven", is(false));

    }

    @Test
    public void testErrorRest3() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"shouldCompensate\" : false}").when()
                .post("/compensation")
                .then()
                .statusCode(201)
                .body("workflowdata.compensated", is(false));
    }
}
