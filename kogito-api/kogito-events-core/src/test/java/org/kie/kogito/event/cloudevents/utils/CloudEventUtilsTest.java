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
package org.kie.kogito.event.cloudevents.utils;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.cloudevents.extension.KogitoExtension;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.provider.ExtensionProvider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class CloudEventUtilsTest {

    private static final String TEST_DATA = "this-is-some-test-data";
    private static final Class<String> TEST_DATA_CLASS = String.class;
    private static final String TEST_ID = "acacfa12-5520-419a-91c4-05a57676dd8b";
    private static final String TEST_SERVICE_URL = "http://test-host";
    private static final String TEST_URI_STRING = TEST_SERVICE_URL + "/test-endpoint";
    private static final URI TEST_URI = URI.create(TEST_URI_STRING);
    private static final String TEST_SUBJECT = "test-subject";

    private static final String TEST_DECISION_MODEL_NAME = "testDecisionModel";
    private static final String TEST_DECISION_MODEL_NAMESPACE = "http://test-decision-model-namespace";
    private static final String TEST_DECISION_SERVICE_NAME = "testDecisionService";
    private static final URI TEST_DECISION_URI_UNKNOWN = URI.create(CloudEventUtils.UNKNOWN_SOURCE_URI_STRING);
    private static final URI TEST_DECISION_URI_ONLY_SERVICE = URI.create(TEST_SERVICE_URL);
    private static final URI TEST_DECISION_URI_SERVICE_AND_MODEL = URI.create(TEST_SERVICE_URL + "/" + TEST_DECISION_MODEL_NAME);
    private static final URI TEST_DECISION_URI_FULL = URI.create(TEST_SERVICE_URL + "/" + TEST_DECISION_MODEL_NAME + "/" + TEST_DECISION_SERVICE_NAME);

    private static final CloudEvent TEST_CLOUDEVENT = CloudEventBuilder.v1()
            .withType(TEST_DATA_CLASS.getName())
            .withId(TEST_ID)
            .withSource(TEST_URI)
            .withData(String.format("\"%s\"", TEST_DATA).getBytes())
            .build();

    private static final String TEST_CORRECT_JSON = "{" +
            "  \"specversion\":\"1.0\"," +
            "  \"id\":\"" + TEST_ID + "\"," +
            "  \"source\":\"" + TEST_URI_STRING + "\"," +
            "  \"type\":\"" + TEST_DATA_CLASS.getName() + "\"," +
            "  \"data\":\"" + TEST_DATA + "\"" +
            "}";

    private static final String TEST_MALFORMED_JSON = "not-a-json-serialized-cloudevent";

    private static final String TEST_EXCEPTION_MESSAGE = "Mocked parse error";

    private static final KogitoExtension TEST_EXTENSION = new KogitoExtension();

    @BeforeAll
    static void setupExtension() {
        KogitoExtension.register();
        TEST_EXTENSION.setDmnModelName(TEST_DECISION_MODEL_NAME);
        TEST_EXTENSION.setDmnModelNamespace(TEST_DECISION_MODEL_NAMESPACE);
        TEST_EXTENSION.setDmnEvaluateDecision(TEST_DECISION_SERVICE_NAME);
    }

    @Test
    void testBuildDecisionSource() {
        assertEquals(TEST_DECISION_URI_UNKNOWN, CloudEventUtils.buildDecisionSource(null));
        assertEquals(TEST_DECISION_URI_UNKNOWN, CloudEventUtils.buildDecisionSource(""));
        assertEquals(TEST_DECISION_URI_UNKNOWN, CloudEventUtils.buildDecisionSource(null, null));
        assertEquals(TEST_DECISION_URI_UNKNOWN, CloudEventUtils.buildDecisionSource("", ""));
        assertEquals(TEST_DECISION_URI_UNKNOWN, CloudEventUtils.buildDecisionSource(null, null, null));
        assertEquals(TEST_DECISION_URI_UNKNOWN, CloudEventUtils.buildDecisionSource("", "", ""));

        assertEquals(TEST_DECISION_URI_ONLY_SERVICE, CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL));
        assertEquals(TEST_DECISION_URI_ONLY_SERVICE, CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, null));
        assertEquals(TEST_DECISION_URI_ONLY_SERVICE, CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, ""));
        assertEquals(TEST_DECISION_URI_ONLY_SERVICE, CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, null, null));
        assertEquals(TEST_DECISION_URI_ONLY_SERVICE, CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, "", ""));

        assertEquals(TEST_DECISION_URI_SERVICE_AND_MODEL, CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, TEST_DECISION_MODEL_NAME));
        assertEquals(TEST_DECISION_URI_SERVICE_AND_MODEL, CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, TEST_DECISION_MODEL_NAME, null));
        assertEquals(TEST_DECISION_URI_SERVICE_AND_MODEL, CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, TEST_DECISION_MODEL_NAME, ""));

        assertEquals(TEST_DECISION_URI_FULL, CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, TEST_DECISION_MODEL_NAME, TEST_DECISION_SERVICE_NAME));
    }

    @Test
    void testBuildSuccess() {
        assertTrue(CloudEventUtils.build(TEST_ID, TEST_URI, TEST_DATA, TEST_DATA_CLASS).isPresent());
    }

    @Test
    void testBuildFailure() throws Exception {
        runWithMockedCloudEventUtilsMapper(() -> assertFalse(CloudEventUtils.build(TEST_ID, TEST_URI, TEST_DATA, TEST_DATA_CLASS).isPresent()));
    }

    @Test
    void testBuildWithExtensionSuccess() {
        Optional<CloudEvent> optCE = CloudEventUtils.build(TEST_ID, TEST_URI, TEST_DATA_CLASS.getSimpleName(), TEST_SUBJECT, TEST_DATA, TEST_EXTENSION);
        assertTrue(optCE.isPresent());
        assertEquals(TEST_EXTENSION, ExtensionProvider.getInstance().parseExtension(KogitoExtension.class, optCE.get()));
    }

    @Test
    void testBuildWithExtensionFailure() throws Exception {
        runWithMockedCloudEventUtilsMapper(() -> assertFalse(CloudEventUtils.build(TEST_ID, TEST_URI, TEST_DATA_CLASS.getSimpleName(), TEST_SUBJECT, TEST_DATA, TEST_EXTENSION).isPresent()));
    }

    @Test
    void testDecodeDataSuccess() {
        Optional<String> optData = CloudEventUtils.decodeData(TEST_CLOUDEVENT, String.class);
        assertTrue(optData.isPresent());
        assertEquals(TEST_DATA, optData.get());
    }

    @Test
    void testDecodeDataFailure() {
        Optional<Integer> optData = CloudEventUtils.decodeData(TEST_CLOUDEVENT, Integer.class);
        assertFalse(optData.isPresent());
    }

    @Test
    void testEncodeSuccess() {
        assertTrue(CloudEventUtils.encode(TEST_CLOUDEVENT).isPresent());
        System.out.println(CloudEventUtils.encode(TEST_CLOUDEVENT).get());
    }

    @Test
    void testEncodeFailure() throws Exception {
        runWithMockedCloudEventUtilsMapper(() -> assertFalse(CloudEventUtils.encode(TEST_CLOUDEVENT).isPresent()));
    }

    @Test
    void testDecodeSuccess() {
        assertTrue(CloudEventUtils.decode(TEST_CORRECT_JSON).isPresent());
    }

    @Test
    void testDecodeFailure() {
        assertFalse(CloudEventUtils.decode(TEST_MALFORMED_JSON).isPresent());
    }

    @Test
    void testUrlEncodedStringFromSuccess() {
        assertTrue(CloudEventUtils.urlEncodedStringFrom(TEST_URI_STRING).isPresent());
    }

    @Test
    void testUrlEncodedStringFromFailure() throws Exception {
        try (MockedStatic<URLEncoder> mockedStaticURLEncoder = mockStatic(URLEncoder.class)) {
            mockedStaticURLEncoder.when(() -> URLEncoder.encode(any(String.class), any(String.class))).thenThrow(new UnsupportedEncodingException());
            assertFalse(CloudEventUtils.urlEncodedStringFrom(TEST_URI_STRING).isPresent());
        }
    }

    @Test
    void testUrlEncodedURIFromSuccess() {
        assertTrue(CloudEventUtils.urlEncodedURIFrom(TEST_URI_STRING).isPresent());
    }

    @Test
    void testUrlEncodedURIFromFailure() {
        try (MockedStatic<URI> mockedStaticURLEncoder = mockStatic(URI.class)) {
            mockedStaticURLEncoder.when(() -> URI.create(any(String.class))).thenThrow(new IllegalArgumentException());
            assertFalse(CloudEventUtils.urlEncodedURIFrom(TEST_URI_STRING).isPresent());
        }
    }

    @Test
    void testLocalDate() throws JsonProcessingException {
        LocalDate localDate = LocalDate.of(2021, 12, 21);
        String marshalled = CloudEventUtils.Mapper.mapper().writeValueAsString(localDate);
        LocalDate unmarshalled = CloudEventUtils.Mapper.mapper().readValue(marshalled, LocalDate.class);
        assertEquals(localDate, unmarshalled);
    }

    private static ObjectMapper getFailingMockedObjectMapper() throws Exception {
        ObjectMapper mockedMapper = mock(ObjectMapper.class);
        when(mockedMapper.writeValueAsBytes(any())).thenThrow(new JsonProcessingException(TEST_EXCEPTION_MESSAGE) {
        });
        when(mockedMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException(TEST_EXCEPTION_MESSAGE) {
        });
        return mockedMapper;
    }

    private static void runWithMockedCloudEventUtilsMapper(Runnable runnable) throws Exception {
        try (MockedStatic<CloudEventUtils.Mapper> mockedStaticMapper = mockStatic(CloudEventUtils.Mapper.class)) {
            ObjectMapper mockedMapper = getFailingMockedObjectMapper();
            mockedStaticMapper.when(CloudEventUtils.Mapper::mapper).thenReturn(mockedMapper);
            runnable.run();
        }
    }
}
