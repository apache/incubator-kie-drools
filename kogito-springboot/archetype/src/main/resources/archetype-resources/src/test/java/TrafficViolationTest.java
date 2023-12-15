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
package ${package};

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = KogitoApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // reset spring context after each test method
public class TrafficViolationTest {

    @LocalServerPort
    private int port;

    @Test
    public void testEvaluateTrafficViolation() {
        RestAssured.port = port;
        given()
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
               .post("/TrafficViolation")
          .then()
             .statusCode(200)
               .body("'Should the driver be suspended?'", is("No"));
    }
}