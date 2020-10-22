/**
 *  Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.kie.kogito.integrationtests.springboot;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;

import io.restassured.http.ContentType;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.kie.kogito.testcontainers.springboot.InfinispanSpringBootTestResource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.ContextConfiguration;


import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoSpringbootApplication.class)
@ContextConfiguration(initializers = InfinispanSpringBootTestResource.Conditional.class)
class OneOfEachTypeTest extends BaseRestTest {

    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Test
    public void allTypes() {
        final String JSON = "{\n" +
                            "    \"InputBoolean\": true,\n" +
                            "    \"InputDate\": \"2020-04-02\",\n" +
                            "    \"InputDTDuration\": \"P1D\",\n" +
                            "    \"InputDateAndTime\": \"2020-04-02T09:00:00\",\n" +
                            "    \"InputNumber\": 1,\n" +
                            "    \"InputString\": \"John Doe\",\n" +
                            "    \"InputTime\": \"09:00\",\n" +
                            "    \"InputYMDuration\": \"P1M\"\n" +
                            "}";

        RestAssured.given()
                   .contentType(ContentType.JSON)
                   .accept(ContentType.JSON)
                   .body(JSON)
                   .post("/OneOfEachType")
                   .then()
                   .statusCode(200)
                   .body("DecisionBoolean", is(Boolean.FALSE))
                   .body("DecisionDate", is("2020-04-03")) // as JSON is not schema aware, here we assert the RAW string
                   .body("DecisionDTDuration", is("PT48H"))
                   .body("DecisionDateAndTime", is("2020-04-02T10:00:00"))
                   .body("DecisionNumber", is(2))
                   .body("DecisionString", is("Hello, John Doe"))
                   .body("DecisionTime", is("10:00:00"))
                   .body("DecisionYMDuration", is("P2M"))
        ;
    }
}
