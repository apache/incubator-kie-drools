/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.quarkus.it.kogito.jbpm;

import java.util.Map;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HotReloadTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private static final String PACKAGE = "io.quarkus.it.kogito.jbpm";
    private static final String PACKAGE_FOLDER = PACKAGE.replace('.', '/');
    private static final String RESOURCE_FILE = PACKAGE_FOLDER + "/text-process.bpmn";
    private static final String HTTP_TEST_PORT = "65535";

    private static final String PROCESS_NAME = "text_process";

    @RegisterExtension
    final static QuarkusDevModeTest test = new QuarkusDevModeTest()
        .setArchiveProducer(
            () -> ShrinkWrap
                .create(JavaArchive.class)
                .addClass(CalculationService.class)
                .addClass(Order.class)
                .addAsResource("text-process.txt", RESOURCE_FILE)
                .addClass(JbpmHotReloadTestHelper.class));

    @Test
    @SuppressWarnings("unchecked")
    public void testServletChange() {

        String payload = "{\"mytext\": \"HeLlO\"}";

        String id =
            given()
                    .baseUri("http://localhost:" + HTTP_TEST_PORT)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(payload)
                .when()
                    .post("/" + PROCESS_NAME)
                .then()
                    .statusCode(201)
                    .header("Location", notNullValue())
                    .body("id", notNullValue())
                .extract()
                    .path("id");

        Map<String, String> result = given()
                    .baseUri("http://localhost:" + HTTP_TEST_PORT)
                    .accept(ContentType.JSON)
                .when()
                    .get("/" + PROCESS_NAME + "/{id}", id)
                .then()
                    .statusCode(200)
                .extract()
                    .as(Map.class);

        assertEquals(2, result.size());
        assertEquals("HELLO", result.get("mytext"));
        
        test.modifySourceFile(JbpmHotReloadTestHelper.class, s -> s.replace("toUpperCase", "toLowerCase"));

        id = given()
                    .baseUri("http://localhost:" + HTTP_TEST_PORT)
                    .contentType(ContentType.JSON)
                    .accept(ContentType.JSON)
                    .body(payload)
                .when()
                    .post("/" + PROCESS_NAME)
                .then()
                    .statusCode(201)
                    .body("id", notNullValue())
                    .header("Location", notNullValue())
                .extract()
                    .path("id");

        result = given()
                    .baseUri("http://localhost:" + HTTP_TEST_PORT)
                    .accept(ContentType.JSON)
                .when()
                    .get("/" + PROCESS_NAME + "/{id}", id)
                .then()
                    .statusCode(200)
                .extract()
                    .as(Map.class);

        assertEquals(2, result.size());
        assertEquals("hello", result.get("mytext"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testRenameProcess() {

        String payload = "{\"mytext\": \"HeLlO\"}";

        String id =
                given()
                        .baseUri("http://localhost:" + HTTP_TEST_PORT)
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body(payload)
                        .when()
                        .post("/" + PROCESS_NAME)
                        .then()
                        .statusCode(201)
                        .header("Location", notNullValue())
                        .body("id", notNullValue())
                        .extract()
                        .path("id");

        Map<String, String> result = given()
                .baseUri("http://localhost:" + HTTP_TEST_PORT)
                .accept(ContentType.JSON)
                .when()
                .get("/" + PROCESS_NAME + "/{id}", id)
                .then()
                .statusCode(200)
                .extract()
                .as(Map.class);

        assertEquals(2, result.size());
        assertEquals("HELLO", result.get("mytext"));

        test.modifyResourceFile(RESOURCE_FILE, s -> s.replaceAll(PROCESS_NAME, "new_" + PROCESS_NAME));

        id = given()
                .baseUri("http://localhost:" + HTTP_TEST_PORT)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(payload)
                .when()
                .post("/new_" + PROCESS_NAME)
                .then()
                .statusCode(201)
                .body("id", notNullValue())
                .header("Location", notNullValue())
                .extract()
                .path("id");

        result = given()
                .baseUri("http://localhost:" + HTTP_TEST_PORT)
                .accept(ContentType.JSON)
                .when()
                .get("/new_" + PROCESS_NAME + "/{id}", id)
                .then()
                .statusCode(200)
                .extract()
                .as(Map.class);

        assertEquals(2, result.size());
        assertEquals("HELLO", result.get("mytext"));
    }

}
