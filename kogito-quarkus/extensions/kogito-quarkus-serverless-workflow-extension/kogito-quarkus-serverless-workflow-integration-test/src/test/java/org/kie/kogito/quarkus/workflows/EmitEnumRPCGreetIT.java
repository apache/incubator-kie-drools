/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.quarkus.workflows;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.quarkus.test.junit.TestProfile;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTestResource(GrpcServerPortResource.class)
@QuarkusIntegrationTest
@TestProfile(EmitEnumProfile.class)
class EmitEnumRPCGreetIT {

    @Test
    void testStateIsUnknown() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\" : {\"name\" : \"Javierito\", \"language\":\"Spanish\", \"unknown\": true}}").when()
                .post("/rpc-greet")
                .then()
                .statusCode(201)
                .body("workflowdata.state", is("UNKNOWN"))
                .body("workflowdata.innerMessage.state", is("UNKNOWN"))
                .body("workflowdata.minority[0].state", is("UNKNOWN"))
                .body("workflowdata.minority[0].innerMessage.state", is("UNKNOWN"))
                .body("workflowdata.minority[1].state", is("UNKNOWN"))
                .body("workflowdata.minority[1].innerMessage.state", is("UNKNOWN"));
    }
}
