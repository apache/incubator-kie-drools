/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.process.definitions;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class ProcessDefinitionsResourceTest {

    @Test
    void testAddDefinition() throws IOException {
        given()
                .contentType(ContentType.TEXT)
                .accept(ContentType.TEXT)
                .body(new String(Thread.currentThread().getContextClassLoader().getResource("helloworld.sw.json").openStream().readAllBytes())).when()
                .post("/helloworld/definition")
                .then()
                .statusCode(200);

        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .when()
                .post("/helloworld")
                .then()
                .statusCode(201)
                .body("workflowdata.result", containsString("Hello World!"));
    }
}
