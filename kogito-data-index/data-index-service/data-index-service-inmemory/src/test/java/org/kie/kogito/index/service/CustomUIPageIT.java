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
package org.kie.kogito.index.service;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class CustomUIPageIT {
    /**
     * Override the property to point to our test resources' directory.
     * Make sure you have src/test/resources/static-test/index.html and
     * src/test/resources/static-test/styles.css in place.
     */
    @Test
    public void testIndexHtmlServedAtRoot() {
        // Expect the content of src/test/resources/static-test/index.html
        RestAssured.given().when().get("/ui/index.html").then().statusCode(200).body(equalTo("<html><body><h1>Test Index</h1></body></html>"));
        RestAssured.given().when().get("/index.html").then().statusCode(200).body(equalTo("<html><body><h1>Test Index</h1></body></html>"));
        RestAssured.given().when().get("/").then().statusCode(200).body(equalTo("<html><body><h1>Test Index</h1></body></html>"));
    }

    @Test
    public void testCssServed() {
        // Expect the content of src/test/resources/static-test/styles.css
        RestAssured.given().when().get("/ui/styles.css").then().statusCode(200).body(equalTo("body { background: #fff; }"));
    }
}
