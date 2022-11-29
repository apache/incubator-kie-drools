/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jitexecutor.bpmn.api;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.drools.util.IoUtils;
import org.junit.jupiter.api.Test;
import org.kie.kogito.jitexecutor.bpmn.JITBPMNService;
import org.kie.kogito.jitexecutor.common.requests.MultipleResourcesPayload;
import org.kie.kogito.jitexecutor.common.requests.ResourceWithURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.kie.kogito.jitexecutor.bpmn.TestingUtils.MULTIPLE_BPMN2_FILE;
import static org.kie.kogito.jitexecutor.bpmn.TestingUtils.MULTIPLE_INVALID_BPMN2_FILE;
import static org.kie.kogito.jitexecutor.bpmn.TestingUtils.SINGLE_BPMN2_FILE;
import static org.kie.kogito.jitexecutor.bpmn.TestingUtils.SINGLE_INVALID_BPMN2_FILE;
import static org.kie.kogito.jitexecutor.bpmn.TestingUtils.SINGLE_UNPARSABLE_BPMN2_FILE;

@QuarkusTest
public class BPMNValidatorResourceTest {

    private static final Logger LOG = LoggerFactory.getLogger(BPMNValidatorResourceTest.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final CollectionType LIST_OF_MSGS = MAPPER.getTypeFactory()
            .constructCollectionType(List.class,
                    String.class);

    @Test
    void test_SingleValidBPMN2() throws IOException {
        String toValidate =
                new String(IoUtils.readBytesFromInputStream(Objects.requireNonNull(JITBPMNService.class.getResourceAsStream(SINGLE_BPMN2_FILE))));
        String uri = "uri";
        String response = given()
                .contentType(ContentType.JSON)
                .body(getMultipleResourcePayload(toValidate, uri))
                .when()
                .post("/jitbpmn/validate")
                .then()
                .statusCode(200)
                .body(containsString("[]"))
                .extract()
                .asString();

        LOG.info("Validate response: {}", response);
        List<String> messages = MAPPER.readValue(response, LIST_OF_MSGS);
        assertThat(messages).isEmpty();
    }

    @Test
    void validateModel_MultipleValidBPMN2() throws IOException {
        String toValidate =
                new String(IoUtils.readBytesFromInputStream(Objects.requireNonNull(JITBPMNService.class.getResourceAsStream(MULTIPLE_BPMN2_FILE))));
        String uri = "uri";
        String response = given()
                .contentType(ContentType.JSON)
                .body(getMultipleResourcePayload(toValidate, uri))
                .when()
                .post("/jitbpmn/validate")
                .then()
                .statusCode(200)
                .body(containsString("[]"))
                .extract()
                .asString();

        LOG.info("Validate response: {}", response);
        List<String> messages = MAPPER.readValue(response, LIST_OF_MSGS);
        assertThat(messages).isEmpty();
    }

    @Test
    void validateModel_SingleInvalidBPMN2() throws IOException {
        String toValidate =
                new String(IoUtils.readBytesFromInputStream(Objects.requireNonNull(JITBPMNService.class.getResourceAsStream(SINGLE_INVALID_BPMN2_FILE))));
        String uri = "uri";
        String response = given()
                .contentType(ContentType.JSON)
                .body(getMultipleResourcePayload(toValidate, uri))
                .when()
                .post("/jitbpmn/validate")
                .then()
                .statusCode(200)
                .body(containsString("[\"Uri: uri - Process id: invalid - name : invalid-process-id - error : Process has no " +
                        "start node.\",\"Uri: uri - Process id: invalid - name : invalid-process-id - error" +
                        " : Process has no end node.\"]"))
                .extract()
                .asString();

        LOG.info("Validate response: {}", response);
        List<String> messages = MAPPER.readValue(response, LIST_OF_MSGS);
        assertThat(messages).hasSize(2);
    }

    @Test
    void validateModel_MultipleInvalidBPMN2() throws IOException {
        String toValidate =
                new String(IoUtils.readBytesFromInputStream(Objects.requireNonNull(JITBPMNService.class.getResourceAsStream(MULTIPLE_INVALID_BPMN2_FILE))));
        String uri = "uri";
        String response = given()
                .contentType(ContentType.JSON)
                .body(getMultipleResourcePayload(toValidate, uri))
                .when()
                .post("/jitbpmn/validate")
                .then()
                .statusCode(200)
                .body(containsString("[\"Uri: uri - Process id: invalid1 - name : invalid1-process-id - error : Process has no " +
                        "start node.\",\"Uri: uri - Process id: invalid1 - name : invalid1-process-id - " +
                        "error : Process has no end node.\",\"Uri: uri - Process id: invalid2 - name : " +
                        "invalid2-process-id - error : Process has no start node.\",\"Uri: uri - Process " +
                        "id: invalid2 - name : invalid2-process-id - error : Process has no end " +
                        "node.\"]"))
                .extract()
                .asString();

        LOG.info("Validate response: {}", response);
        List<String> messages = MAPPER.readValue(response, LIST_OF_MSGS);
        assertThat(messages).hasSize(4);
    }

    @Test
    void validateModel_MultipleBPMN2() throws IOException {
        String validModel =
                new String(IoUtils.readBytesFromInputStream(Objects.requireNonNull(JITBPMNService.class.getResourceAsStream(SINGLE_BPMN2_FILE))));
        String invalidModel =
                new String(IoUtils.readBytesFromInputStream(Objects.requireNonNull(JITBPMNService.class.getResourceAsStream(SINGLE_INVALID_BPMN2_FILE))));
        ResourceWithURI validResource = new ResourceWithURI("UriValid", validModel);
        ResourceWithURI invalidResource = new ResourceWithURI("UriInvalid", invalidModel);
        String response = given()
                .contentType(ContentType.JSON)
                .body(new MultipleResourcesPayload("mainUri", Arrays.asList(validResource, invalidResource)))
                .when()
                .post("/jitbpmn/validate")
                .then()
                .statusCode(200)
                .body(containsString("[\"Uri: UriInvalid - Process id: invalid - name : invalid-process-id - error : Process has no " +
                        "start node.\",\"Uri: UriInvalid - Process id: invalid - name : invalid-process-id - error" +
                        " : Process has no end node.\"]"))
                .extract()
                .asString();

        LOG.info("Validate response: {}", response);
        List<String> messages = MAPPER.readValue(response, LIST_OF_MSGS);
        assertThat(messages).hasSize(2);
    }

    @Test
    void validateModel_SingleUnparsablePMN2() throws IOException {
        String toValidate =
                new String(IoUtils.readBytesFromInputStream(Objects.requireNonNull(JITBPMNService.class.getResourceAsStream(SINGLE_UNPARSABLE_BPMN2_FILE))));
        String uri = "uri";
        String response = given()
                .contentType(ContentType.JSON)
                .body(getMultipleResourcePayload(toValidate, uri))
                .when()
                .post("/jitbpmn/validate")
                .then()
                .statusCode(200)
                .body(containsString("[\"Could not find message _T6T0kEcTEDuygKsUt0on2Q____\"]"))
                .extract()
                .asString();

        LOG.info("Validate response: {}", response);
        List<String> messages = MAPPER.readValue(response, LIST_OF_MSGS);
        assertThat(messages).hasSize(1);
    }

    private MultipleResourcesPayload getMultipleResourcePayload(String content, String uri) {
        return new MultipleResourcesPayload(uri, Collections.singletonList(new ResourceWithURI(uri, content)));
    }

}
