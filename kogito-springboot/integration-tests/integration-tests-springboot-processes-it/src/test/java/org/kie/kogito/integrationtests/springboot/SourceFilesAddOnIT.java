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

package org.kie.kogito.integrationtests.springboot;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.hasEntry;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
public class SourceFilesAddOnIT extends BaseRestTest {

    private static final String GET_SOURCES_PATH = "/management/processes/sources";
    private static final String GET_PROCESS_SOURCES_PATH = "/management/processes/{processId}/sources";
    private static final String GET_PROCESS_SOURCE_PATH = "/management/processes/{processId}/source";

    @LocalServerPort
    int randomServerPort;

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    void setPort() {
        RestAssured.port = randomServerPort;
    }

    public static String readFileContent(String file) throws URISyntaxException, IOException {
        Path path = Paths.get(Thread.currentThread().getContextClassLoader().getResource(file).toURI());
        return Files.readString(path);
    }

    @Test
    void testGetSourceFilesByProcessId() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .get(GET_PROCESS_SOURCES_PATH, "cinema")
                .then()
                .statusCode(200)
                .body("size()", is(1))
                .body("", hasItems(hasEntry("uri", "org/kie/kogito/examples/cinema.bpmn")));
    }

    @Test
    void testGetSourceFileByProcessId() throws Exception {
        given().when()
                .get(GET_PROCESS_SOURCE_PATH, "approvals")
                .then()
                .statusCode(200)
                .body(equalTo(readFileContent("approval.bpmn")));
    }

    @Test
    void testGetSourceFileFromResourcesRoot() {
        given()
                .when()
                .queryParam("uri", "approval.bpmn")
                .get(GET_SOURCES_PATH)
                .then()
                .statusCode(200)
                .header("Content-Length", "24215");
    }

    @Test
    void testGetSourceFileFromInternalDirectory() {
        given()
                .when()
                .queryParam("uri", "org/kie/kogito/examples/timers.bpmn")
                .get(GET_SOURCES_PATH)
                .then()
                .statusCode(200)
                .header("Content-Length", "11583");
    }
}
