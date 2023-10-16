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
package org.kie.kogito.jitexecutor.dmn.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.util.IoUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNResult;
import org.kie.kogito.jitexecutor.common.requests.MultipleResourcesPayload;
import org.kie.kogito.jitexecutor.common.requests.ResourceWithURI;
import org.kie.kogito.jitexecutor.dmn.requests.JITDMNPayload;
import org.kie.kogito.jitexecutor.dmn.responses.JITDMNDecisionResult;
import org.kie.kogito.jitexecutor.dmn.responses.JITDMNMessage;
import org.kie.kogito.jitexecutor.dmn.responses.JITDMNResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class MultipleModelsTest {
    private static final Logger LOG = LoggerFactory.getLogger(MultipleModelsTest.class);

    private static final String URI1 = "/multiple/importing.dmn";
    private static final String URI2 = "/multiple/stdlib.dmn";
    private static ResourceWithURI model1;
    private static ResourceWithURI model2;

    private static final String XAIURI1 = "/test.dmn";
    private static ResourceWithURI xaimodel1;

    private static final String CH11URI1 = "/multiple/Chapter 11 Example.dmn";
    private static final String CH11URI2 = "/multiple/Financial.dmn";
    private static ResourceWithURI ch11model1;
    private static ResourceWithURI ch11model2;

    private static final ObjectMapper MAPPER;
    static {
        final var jitModule = new SimpleModule().addAbstractTypeMapping(DMNResult.class, JITDMNResult.class)
                .addAbstractTypeMapping(DMNDecisionResult.class, JITDMNDecisionResult.class)
                .addAbstractTypeMapping(DMNMessage.class, JITDMNMessage.class);

        MAPPER = new ObjectMapper()
                .registerModule(new Jdk8Module())
                .registerModule(jitModule);
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    private static final CollectionType LIST_OF_MSGS = MAPPER.getTypeFactory()
            .constructCollectionType(List.class,
                    JITDMNMessage.class);

    @BeforeAll
    public static void setup() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        model1 = new ResourceWithURI(URI1, new String(IoUtils.readBytesFromInputStream(MultipleModelsTest.class.getResourceAsStream(URI1))));
        model2 = new ResourceWithURI(URI2, new String(IoUtils.readBytesFromInputStream(MultipleModelsTest.class.getResourceAsStream(URI2))));
        xaimodel1 = new ResourceWithURI(XAIURI1, new String(IoUtils.readBytesFromInputStream(MultipleModelsTest.class.getResourceAsStream(XAIURI1))));
        ch11model1 = new ResourceWithURI(CH11URI1, new String(IoUtils.readBytesFromInputStream(MultipleModelsTest.class.getResourceAsStream(CH11URI1))));
        ch11model2 = new ResourceWithURI(CH11URI2, new String(IoUtils.readBytesFromInputStream(MultipleModelsTest.class.getResourceAsStream(CH11URI2))));
    }

    @Test
    public void testForm() {
        given()
                .contentType(ContentType.JSON)
                .body(new MultipleResourcesPayload(URI1, List.of(model1, model2)))
                .when().post("/jitdmn/schema/form")
                .then()
                .statusCode(200)
                .body(containsString("InputSet"), containsString("x-dmn-type"), containsString("tPerson"));
    }

    @Test
    public void testSchema() {
        given()
                .contentType(ContentType.JSON)
                .body(new MultipleResourcesPayload(URI1, List.of(model1, model2)))
                .when().post("/jitdmn/schema")
                .then()
                .statusCode(200)
                .body(containsString("InputSet"), containsString("x-dmn-type"), containsString("tPerson"), containsString("mainURI"), containsString("URI"));
    }

    @Test
    public void testjitEndpoint() {
        JITDMNPayload jitdmnpayload = new JITDMNPayload(URI1, List.of(model1, model2), buildContext());
        given()
                .contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn")
                .then()
                .statusCode(200)
                .body("'my decision'", is("Ciao, John Doe (age:47)."));
    }

    private Map<String, Object> buildContext() {
        return Map.of("a person", Map.of("full name", "John Doe", "age", 47));
    }

    @Test
    public void testjitdmnResultEndpoint() {
        JITDMNPayload jitdmnpayload = new JITDMNPayload(URI1, List.of(model1, model2), buildContext());
        given()
                .contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn/dmnresult")
                .then()
                .statusCode(200)
                .body("dmnContext.'my decision'", is("Ciao, John Doe (age:47)."));
    }

    @Test
    public void testValidation() throws IOException {
        String response = given()
                .contentType(ContentType.JSON)
                .body(new MultipleResourcesPayload(URI1, List.of(model1, model2)))
                .when()
                .post("/jitdmn/validate")
                .then()
                .statusCode(200)
                .body(containsString("Variable named 'my decision' is missing its type reference"))
                .extract()
                .asString();
        LOG.info("Validate response: {}", response);
        List<JITDMNMessage> messages = MAPPER.readValue(response, LIST_OF_MSGS);
        assertEquals(1, messages.size());
        assertThat(messages.get(0)).hasFieldOrPropertyWithValue("path", URI1);
    }

    @Test
    public void testjitExplainabilityEndpoint() {
        JITDMNPayload jitdmnpayload = new JITDMNPayload(XAIURI1, List.of(xaimodel1, model1, model2), Map.of("FICO Score", 800, "DTI Ratio", .1, "PITI Ratio", .1));
        given()
                .contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn/evaluateAndExplain")
                .then()
                .statusCode(200)
                .body(containsString("dmnResult"), containsString("saliencies"), containsString("xls2dmn"), containsString("featureName"));
    }

    @Test
    public void testjitEndpointCH11() {
        JITDMNPayload jitdmnpayload = new JITDMNPayload(CH11URI1, List.of(ch11model1, ch11model2), buildCH11Context());
        given()
                .contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn")
                .then()
                .statusCode(200)
                .body("Strategy", is("THROUGH"));
    }

    @Test
    public void testjitdmnResultEndpointCH11() {
        JITDMNPayload jitdmnpayload = new JITDMNPayload(CH11URI1, List.of(ch11model1, ch11model2), buildCH11Context());
        given()
                .contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn/dmnresult")
                .then()
                .statusCode(200)
                .body("dmnContext.Strategy", is("THROUGH"));
    }

    private Map<String, Object> buildCH11Context() {
        Map<String, Object> context = new HashMap<>();
        context.put("Applicant data", Map.of("Age", 51,
                "MartitalStatus", "M", // typo is present in DMNv1.3
                "EmploymentStatus", "EMPLOYED",
                "ExistingCustomer", false,
                "Monthly", Map.of("Income", 100_000,
                        "Repayments", 2_500,
                        "Expenses", 10_000)));
        context.put("Bureau data", Map.of("Bankrupt", false,
                "CreditScore", 600));
        context.put("Requested product", Map.of("ProductType", "STANDARD LOAN",
                "Rate", 0.08d,
                "Term", 36,
                "Amount", 100_00));
        context.put("Supporting documents", null);
        return context;
    }

    @Test
    public void testjitdmnResultEndpointCH11_withErrors() throws Exception {
        Map<String, Object> context = new HashMap<>(); // will omit `Applicant data` intentionally.
        context.put("Bureau data", Map.of("Bankrupt", false,
                "CreditScore", 600));
        context.put("Requested product", Map.of("ProductType", "STANDARD LOAN",
                "Rate", 0.08d,
                "Term", 36,
                "Amount", 100_00));
        JITDMNPayload jitdmnpayload = new JITDMNPayload(CH11URI1, List.of(ch11model1, ch11model2), context);
        String response = given()
                .contentType(ContentType.JSON)
                .body(jitdmnpayload)
                .when().post("/jitdmn/dmnresult")
                .then()
                .statusCode(200)
                .body("dmnContext.'Required monthly installment'", notNullValue())
                .extract()
                .asString();
        JITDMNResult result = MAPPER.readValue(response, JITDMNResult.class);
        assertThat(result.getMessages()).isNotEmpty().allMatch(m -> m.getPath().equals(CH11URI1));
    }

}
