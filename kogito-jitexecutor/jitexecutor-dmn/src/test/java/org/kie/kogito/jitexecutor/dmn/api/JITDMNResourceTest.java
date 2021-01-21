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

package org.kie.kogito.jitexecutor.dmn.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.drools.core.util.IoUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jitexecutor.dmn.requests.JITDMNPayload;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class JITDMNResourceTest {

    private static String model;

    @BeforeAll
    public static void setup() throws IOException {
        model = new String(IoUtils.readBytesFromInputStream(JITDMNResourceTest.class.getResourceAsStream("/test.dmn")));
    }

    @Test
    public void testjitEndpoint() {
        JITDMNPayload jitdmnpayload = new JITDMNPayload(model, buildContext());
        given()
                .contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn")
                .then()
                .statusCode(200)
                .body(containsString("Loan Approval"), containsString("Approved"));
    }

    @Test
    public void testjitdmnResultEndpoint() {
        JITDMNPayload jitdmnpayload = new JITDMNPayload(model, buildContext());
        given()
                .contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn/dmnresult")
                .then()
                .statusCode(200)
                .body(containsString("Loan Approval"), containsString("Approved"), containsString("xls2dmn"));
    }

    private Map<String, Object> buildContext() {
        Map<String, Object> context = new HashMap<>();
        context.put("FICO Score", 800);
        context.put("DTI Ratio", .1);
        context.put("PITI Ratio", .1);
        return context;
    }
}
