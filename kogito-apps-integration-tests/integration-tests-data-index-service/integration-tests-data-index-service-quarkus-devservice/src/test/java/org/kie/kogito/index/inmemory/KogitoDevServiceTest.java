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
package org.kie.kogito.index.inmemory;

import java.time.Duration;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.kie.kogito.test.utils.SocketUtils;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.*;

public class KogitoDevServiceTest {

    private static final Duration TIMEOUT = Duration.ofMinutes(1);
    private static final String DATA_INDEX_EPHEMERAL_IMAGE_TEST_PROPERTY = "data-index-ephemeral.image.test";
    private static final String DATA_INDEX_EPHEMERAL_IMAGE_TEST_DEFAULT_VALUE = "use-default-image";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    final static int httpPort = SocketUtils.findAvailablePort();
    final static int dataIndexHttpPort = SocketUtils.findAvailablePort();

    @RegisterExtension
    final static QuarkusDevModeTest test = new QuarkusDevModeTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addAsResource(new StringAsset(getApplicationPropertiesContent()), "application.properties")
                    .addAsResource("greet.sw.json"));

    @Test
    public void testDataIndexDevService() {
        await()
                .atMost(TIMEOUT)
                .ignoreExceptions()
                .untilAsserted(() -> given()
                        .baseUri("http://localhost:" + dataIndexHttpPort)
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body("{ \"query\" : \"{ ProcessInstances { id } }\"}")
                        .when().post("/graphql")
                        .then()
                        .statusCode(200)
                        .body("data.ProcessInstances.size()", is(0)));

        String processId = given()
                .baseUri("http://localhost:" + httpPort)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{\"workflowdata\" : {\"name\" : \"John\", \"language\":\"English\"}}")
                .when()
                .post("/greet")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .extract()
                .path("id");

        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> given()
                        .baseUri("http://localhost:" + dataIndexHttpPort)
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body("{ \"query\" : \"{ ProcessInstances (where: { id: {equal: \\\"" + processId + "\\\"}}) { id, processId, processName, createdBy } }\"}")
                        .when().post("/graphql")
                        .then()
                        .statusCode(200)
                        .body("data.ProcessInstances[0].id", is(processId))
                        .body("data.ProcessInstances[0].processId", is("greet"))
                        .body("data.ProcessInstances[0].processName", is("Greeting workflow"))
                        .body("data.ProcessInstances[0].createdBy", nullValue()));

        given().contentType(ContentType.JSON)
                .baseUri("http://localhost:" + dataIndexHttpPort)
                .body("{ \"query\" : \"{ProcessDefinitions{ id, version, name } }\" }")
                .when().post("/graphql")
                .then().statusCode(200)
                .body("data.ProcessDefinitions.size()", is(1))
                .body("data.ProcessDefinitions[0].id", is("greet"))
                .body("data.ProcessDefinitions[0].version", is("1.0"))
                .body("data.ProcessDefinitions[0].name", is("Greeting workflow"));
    }

    private static String getApplicationPropertiesContent() {
        return "quarkus.http.port=" + httpPort + "\n"
                + "quarkus.kogito.devservices.port=" + dataIndexHttpPort + "\n"
                + getKogitoDevServicesImageName();
    }

    private static String getKogitoDevServicesImageName() {
        String imageName = System.getProperty(DATA_INDEX_EPHEMERAL_IMAGE_TEST_PROPERTY);
        if (imageName == null) {
            throw new IllegalStateException("Property " + DATA_INDEX_EPHEMERAL_IMAGE_TEST_PROPERTY + " was not passed to tests!");
        }
        if (imageName.isEmpty() || DATA_INDEX_EPHEMERAL_IMAGE_TEST_DEFAULT_VALUE.equals(imageName)) {
            return "";
        }
        return "quarkus.kogito.devservices.image-name=" + imageName;
    }
}
