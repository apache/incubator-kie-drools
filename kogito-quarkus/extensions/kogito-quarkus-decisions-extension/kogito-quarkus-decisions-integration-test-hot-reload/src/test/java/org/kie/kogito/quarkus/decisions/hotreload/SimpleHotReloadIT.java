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
package org.kie.kogito.quarkus.decisions.hotreload;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

public class SimpleHotReloadIT {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    private static final String PACKAGE = "org.kie.kogito.quarkus.decisions.hotreload";
    private static final String RESOURCE_FILE_PATH = PACKAGE.replace('.', '/');
    private static final String DMN_RESOURCE_FILE = RESOURCE_FILE_PATH + "/TrafficViolation.dmn";

    @RegisterExtension
    final static QuarkusDevModeTest test = new QuarkusDevModeTest().setArchiveProducer(
            () -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource("TrafficViolation.txt", DMN_RESOURCE_FILE));

    @Test
    void simpleHotReloadTest() {
        executeTest("Traffic Violation");

        test.modifyResourceFile(DMN_RESOURCE_FILE, s -> s.replaceAll("Traffic", "NewTraffic"));

        executeTest("NewTraffic Violation");
    }

    private void executeTest(String path) {
        String httpPort = ConfigProvider.getConfig().getValue("quarkus.http.port", String.class);
        ValidatableResponse response = given()
                .baseUri("http://localhost:" + httpPort)
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
                .post("/" + path)
                .then();

        response.statusCode(200)
                .body("'Should the driver be suspended?'", is("No"));
    }
}
