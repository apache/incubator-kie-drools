/**
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
package org.drools.quarkus.test;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static io.restassured.RestAssured.given;

@QuarkusIntegrationTest
public class TestableIT {
    static {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    @Test
    public void testCepEvaluation() {
        given().when().get("/test/testCepEvaluation").then().statusCode(200);
    }

    @Test
    @Timeout(value = 10, unit = TimeUnit.SECONDS)
    public void testFireUntiHalt() {
        given().when().get("/test/testFireUntiHalt").then().statusCode(200);
    }

    @Test
    public void testAllPkgsKBase() {
        given().when().get("/test/testAllPkgsKBase").then().statusCode(200);
    }

    @Test
    public void testTms() {
        given().when().get("/test/testTms").then().statusCode(200);
    }
}
