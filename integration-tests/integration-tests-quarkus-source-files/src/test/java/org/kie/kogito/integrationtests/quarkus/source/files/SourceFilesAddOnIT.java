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
package org.kie.kogito.integrationtests.quarkus.source.files;

import org.junit.jupiter.api.Test;
import org.kie.kogito.addon.source.files.SourceFile;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;

@QuarkusIntegrationTest
class SourceFilesAddOnIT {

    private static final String PATH = "/management/process/";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    void testGetSourceFilesByProcessId() {
        given()
                .header("Authorization", "Basic c2NvdHQ6amIwc3M=")
                .contentType(ContentType.JSON)
                .when()
                .get(PATH + "ymlgreet/sources")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("", hasItems(hasEntry("uri", SourceFile.SOURCES_HTTP_PATH + "org/kie/kogito/examples/ymlgreet.sw.yml")));
    }

    @Test
    void testGetSourceFilesByProcessIdNonAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(PATH + "ymlgreet/sources")
                .then()
                .statusCode(401);
    }

    @Test
    void testGetSourceFileFromResourcesRoot() {
        given()
                .header("Authorization", "Basic c2NvdHQ6amIwc3M=")
                .when()
                .get("/sources/petstore.json")
                .then()
                .statusCode(200)
                .header("Content-Length", "5189");
    }

    @Test
    void testGetSourceFileFromInternalDirectory() {
        given()
                .header("Authorization", "Basic c2NvdHQ6amIwc3M=")
                .when()
                .get("/sources/org/kie/kogito/examples/ymlgreet.sw.yml")
                .then()
                .statusCode(200)
                .header("Content-Length", "1012");
    }

    @Test
    void testGetSourceFileNonAuthenticated() {
        given()
                .when()
                .get("/sources/petstore.json")
                .then()
                .statusCode(401);
    }
}
