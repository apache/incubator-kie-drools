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

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
class DSCoercionTest extends BaseRestTest {

    @Test
    void testWholeModel() {
        given()
                .contentType(ContentType.JSON)
                .when()
                .post("/DScoercion")
                .then()
                .statusCode(200)
                .body("n", is(47))
                .body("s", is("Hello, World"))
                .body("b", is(true))
                .body("d", is("2020-05-18")) // as JSON is not schema aware, here we assert the RAW string
                .body("t", is("12:34:56"))
                .body("dt", is("2020-05-18T12:34:56"))
                .body("ymd", is("P3Y"))
                .body("dtd", is("PT1H"));
    }

    @Test
    void testDSn() {
        Number DSn = given().contentType(ContentType.JSON)
                .when()
                .post("/DScoercion/DSn")
                .getBody().as(Number.class);
        assertThat(DSn, is(47));
    }

    @Test
    void testDSs() {
        String DSs = given().contentType(ContentType.JSON)
                .when()
                .post("/DScoercion/DSs")
                .getBody().asString();
        assertThat(DSs, is("\"Hello, World\"")); // we want to be sure the RAW response is a JSONValue string literal, ref http://ecma-international.org/ecma-262/5.1/#sec-15.12
    }

    @Test
    void testDSb() {
        Boolean DSb = given().contentType(ContentType.JSON)
                .when()
                .post("/DScoercion/DSb")
                .getBody().as(Boolean.class);
        assertThat(DSb, is(true));
    }

    @Test
    void testDSd() {
        LocalDate DSd = given().contentType(ContentType.JSON)
                .when()
                .post("/DScoercion/DSd")
                .getBody().as(LocalDate.class);
        assertThat(DSd, is(LocalDate.of(2020, 5, 18)));
    }

    @Test
    void testDSt() {
        LocalTime DSt = given().contentType(ContentType.JSON)
                .when()
                .post("/DScoercion/DSt")
                .getBody().as(LocalTime.class);
        assertThat(DSt, is(LocalTime.of(12, 34, 56)));
    }

    @Test
    void testDSdt() {
        LocalDateTime DSdt = given().contentType(ContentType.JSON)
                .when()
                .post("/DScoercion/DSdt")
                .getBody().as(LocalDateTime.class);
        assertThat(DSdt, is(LocalDateTime.of(2020, 5, 18, 12, 34, 56)));
    }

    @Test
    void testDSdtd() {
        Duration DSdtd = given().contentType(ContentType.JSON)
                .when()
                .post("/DScoercion/DSdtd")
                .getBody().as(Duration.class);
        assertThat(DSdtd, is(Duration.parse("PT1H")));
    }

    @Test
    void testDSymd() {
        Period DSymd = given().contentType(ContentType.JSON)
                .when()
                .post("/DScoercion/DSymd")
                .getBody().as(Period.class);
        assertThat(DSymd, is(Period.parse("P3Y")));
    }

}
