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
package org.kie.kogito.integrationtests.quarkus.source.files;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasEntry;

@QuarkusIntegrationTest
class SourceFilesAddOnIT {

    private static final String GET_PROCESS_SOURCES_PATH = "/management/processes/%s/sources";
    private static final String GET_PROCESS_SOURCE_PATH = "/management/processes/%s/source";

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public static String readFileContent(String file) throws URISyntaxException, IOException {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource(file).toURI());
        return Files.readString(path);
    }

    @Test
    void testGetSourceFilesByProcessId() {
        given()
                .header("Authorization", "Basic c2NvdHQ6amIwc3M=")
                .contentType(ContentType.JSON)
                .when()
                .get(String.format(GET_PROCESS_SOURCES_PATH, "ymlgreet"))
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("", hasItems(hasEntry("uri", "org/kie/kogito/examples/ymlgreet.sw.yml")));
    }

    @Test
    void testGetSourceFilesByProcessIdNonAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(String.format(GET_PROCESS_SOURCES_PATH, "ymlgreet"))
                .then()
                .statusCode(401);
    }

    @Test
    void testGetSourceFileByProcessId() throws Exception {
        given().header("Authorization", "Basic c2NvdHQ6amIwc3M=")
                .when()
                .get(String.format(GET_PROCESS_SOURCE_PATH, "ymlgreet"))
                .then()
                .statusCode(200)
                .body(equalTo(readFileContent("ymlgreet-expected.sw.yml")));
    }

    @Test
    void testGetSourceFileByProcessIdNonAuthenticated() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(String.format(GET_PROCESS_SOURCES_PATH, "ymlgreet"))
                .then()
                .statusCode(401);
    }

    @Test
    void testGetSourceFileFromResourcesRoot() {
        given()
                .header("Authorization", "Basic c2NvdHQ6amIwc3M=")
                .when()
                .get("/management/processes/sources?uri=petstore_root.sw.json")
                .then()
                .statusCode(200)
                .header("Content-Length", "556");
    }

    @Test
    void testGetSourceFileFromInternalDirectory() {
        given()
                .header("Authorization", "Basic c2NvdHQ6amIwc3M=")
                .when()
                .get("/management/processes/sources?uri=org/kie/kogito/examples/ymlgreet.sw.yml")
                .then()
                .statusCode(200)
                .header("Content-Length", "1802");
    }

    @Test
    void testGetSourceFileNonAuthenticated() {
        given()
                .when()
                .get("/management/processes/sources?uri=petstore.json")
                .then()
                .statusCode(401);
    }
}
