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

package io.quarkus.it.kogito.decision;

import java.util.function.Supplier;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class SimpleModifyHotReloadTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private static final String PACKAGE = "io.quarkus.it.kogito.decision";
    private static final String RESOURCE_FILE_PATH = PACKAGE.replace( '.', '/' );
    private static final String DMN_RESOURCE_FILE = RESOURCE_FILE_PATH + "/TrafficViolation.dmn";

    private static final String HTTP_TEST_PORT = "65535";

    @RegisterExtension
    final static QuarkusDevModeTest test = new QuarkusDevModeTest().setArchiveProducer(
            new Supplier<JavaArchive>() {

                @Override
                public JavaArchive get() {
                    JavaArchive ja = ShrinkWrap.create(JavaArchive.class)
                            .addAsResource("application.properties.stronglytyped", "/application.properties")
                            .addAsResource("TrafficViolation.txt", DMN_RESOURCE_FILE);
                    return ja;
                }
            });

    @Test
    void simpleHotReloadTest() throws InterruptedException {
        executeTest("No");

        test.modifyResourceFile(DMN_RESOURCE_FILE, s -> s.replaceAll("if Total Points >= 20 then \"Yes\" else \"No\"",
                                                                     "if Total Points >= 2 then \"Yes\" else \"No\""));

        executeTest("Yes");
    }

    private void executeTest(String result) {
        ValidatableResponse response = given()
                .baseUri("http://localhost:" + HTTP_TEST_PORT)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\n" +
                        "    \"Driver\": {\n" +
                        "        \"Points\": 2\n" +
                        "    },\n" +
                        "    \"Violation\": {\n" +
                        "        \"Type\": \"speed\",\n" +
                        "        \"Actual Speed\": 120,\n" +
                        "        \"Speed Limit\": 100\n" +
                        "    }\n" +
                        "}")
                .contentType(ContentType.JSON)
                .when()
        .post("/Traffic Violation")
                .then();

        response.statusCode(200)
                .body("'Should the driver be suspended?'", is(result));
    }
}
