/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.quarkus.dmn;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
public class CustomEndpointIT {

    @Test
    public void testHelloEndpoint() {
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(DMNIT.ADULT_PAYLOAD)
                .post("/custom")
                .then()
                .statusCode(200)
                .body("data.d.Hello", Matchers.is("Certidão Hello Luca"));
    }

}
