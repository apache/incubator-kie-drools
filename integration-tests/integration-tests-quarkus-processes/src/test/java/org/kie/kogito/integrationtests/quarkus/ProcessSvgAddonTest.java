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

package org.kie.kogito.integrationtests.quarkus;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.kie.kogito.integrationtests.quarkus.utils.DataIndexWiremock;
import org.kie.kogito.testcontainers.quarkus.InfinispanQuarkusTestResource;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

@QuarkusTest
@QuarkusTestResource(InfinispanQuarkusTestResource.Conditional.class)
@QuarkusTestResource(DataIndexWiremock.class)
public class ProcessSvgAddonTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public static String readFileContent(String file) throws URISyntaxException, IOException {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource(file).toURI());
        return new String(Files.readAllBytes(path));
    }

    @Test
    void testInvalidSvgProcessInstances() {
        given().when()
                .get("/svg/processes/{processId}", "aprocess")
                .then()
                .statusCode(404);

        given().when()
                .get("/svg/processes/{processId}/instances/{processInstanceId}", "aprocess", "id")
                .then()
                .statusCode(404);
    }

    @Test
    void testGetSvgProcessInstances() throws Exception {
        given().when()
                .get("/svg/processes/{processId}", "approvals")
                .then()
                .statusCode(200)
                .body(equalTo(readFileContent("META-INF/processSVG/approvals.svg")));

        String pId = given()
                .contentType(ContentType.JSON)
                .when()
                .post("/approvals")
                .then()
                .statusCode(201)
                .extract().body().path("id");

        given().when()
                .get("/svg/processes/{processId}/instances/{processInstanceId}", "approvals", pId)
                .then()
                .statusCode(200)
                .body(equalTo(readFileContent("META-INF/processSVG/approvals-expected.svg")));

        given()
                .contentType(ContentType.JSON)
                .when()
                .delete("/approvals/{pId}", pId)
                .then()
                .statusCode(200);

        given().when()
                .get("/svg/processes/{processId}/instances/{processInstanceId}", "approvals", pId)
                .then()
                .statusCode(200)
                .body(equalTo(readFileContent("META-INF/processSVG/approvals-expected.svg")));
    }
}
