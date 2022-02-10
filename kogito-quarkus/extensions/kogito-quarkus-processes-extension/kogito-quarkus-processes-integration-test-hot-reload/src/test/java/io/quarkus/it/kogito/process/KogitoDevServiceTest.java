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

package io.quarkus.it.kogito.process;

import java.time.Duration;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import io.quarkus.test.QuarkusDevModeTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.quarkus.it.kogito.process.HotReloadTest.HTTP_TEST_PORT;
import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;

public class KogitoDevServiceTest {

    private static Duration TIMEOUT = Duration.ofMinutes(1);

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @RegisterExtension
    final static QuarkusDevModeTest test = new QuarkusDevModeTest()
            .setArchiveProducer(() -> ShrinkWrap.create(JavaArchive.class)
                    .addClass(CalculationService.class)
                    .addClass(Order.class)
                    .addAsResource("orderItems.bpmn")
                    .addAsResource("orders.bpmn"));

    @Test
    public void testDataIndexDevService() {
        await()
                .atMost(TIMEOUT)
                .ignoreExceptions()
                .untilAsserted(() -> given()
                        .baseUri("http://localhost:8180")
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body("{ \"query\" : \"{ ProcessInstances { id } }\"}")
                        .when().post("/graphql")
                        .then()
                        .statusCode(200)
                        .body("data.ProcessInstances.size()", is(0)));

        String addOrderPayload = "{\"approver\" : \"john\", \"order\" : {\"orderNumber\" : \"12345\", \"shipped\" : false}}";
        String processId = given()
                .baseUri("http://localhost:" + HTTP_TEST_PORT)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(addOrderPayload)
                .when()
                .post("/orders")
                .then()
                .statusCode(201)
                .header("Location", notNullValue())
                .body("id", notNullValue())
                .extract()
                .path("id");

        await()
                .atMost(TIMEOUT)
                .untilAsserted(() -> given()
                        .baseUri("http://localhost:8180")
                        .contentType(ContentType.JSON)
                        .accept(ContentType.JSON)
                        .body("{ \"query\" : \"{ ProcessInstances (where: { id: {equal: \\\"" + processId + "\\\"}}) { id, processId, processName } }\"}")
                        .when().post("/graphql")
                        .then()
                        .statusCode(200)
                        .body("data.ProcessInstances[0].id", is(processId))
                        .body("data.ProcessInstances[0].processId", is("demo.orders"))
                        .body("data.ProcessInstances[0].processName", is("orders")));
    }
}
