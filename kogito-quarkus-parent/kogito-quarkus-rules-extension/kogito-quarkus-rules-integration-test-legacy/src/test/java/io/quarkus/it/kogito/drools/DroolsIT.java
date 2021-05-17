/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package io.quarkus.it.kogito.drools;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;

import static org.hamcrest.Matchers.containsString;

@QuarkusTest
public class DroolsIT {

    @Test
    public void testRuleEvaluation() {
        RestAssured.when().get("/hello").then()
                .body(containsString("Mario is older than Mark"));
    }

    @Test
    public void testTooYoung() {
        RestAssured.when().get("/candrink/Mark/17").then()
                .body(containsString("Mark can NOT drink"));
    }

    @Test
    public void testAdult() {
        RestAssured.when().get("/candrink/Mario/18").then()
                .body(containsString("Mario can drink"));
    }
}
