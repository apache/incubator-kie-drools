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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.kogito.event.DataEvent;
import org.kie.kogito.event.DataEventFactory;
import org.kie.kogito.event.cloudevents.extension.KogitoExtension;
import org.kie.kogito.event.cloudevents.utils.CloudEventUtils.Mapper;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.core.provider.ExtensionProvider;
import io.cloudevents.jackson.JsonCloudEventData;

import static org.assertj.core.api.Assertions.assertThat;
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
        assertThat(CloudEventUtils.buildDecisionSource(null)).isEqualTo(TEST_DECISION_URI_UNKNOWN);
        assertThat(CloudEventUtils.buildDecisionSource("")).isEqualTo(TEST_DECISION_URI_UNKNOWN);
        assertThat(CloudEventUtils.buildDecisionSource(null, null)).isEqualTo(TEST_DECISION_URI_UNKNOWN);
        assertThat(CloudEventUtils.buildDecisionSource("", "")).isEqualTo(TEST_DECISION_URI_UNKNOWN);
        assertThat(CloudEventUtils.buildDecisionSource(null, null, null)).isEqualTo(TEST_DECISION_URI_UNKNOWN);
        assertThat(CloudEventUtils.buildDecisionSource("", "", "")).isEqualTo(TEST_DECISION_URI_UNKNOWN);

        assertThat(CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL)).isEqualTo(TEST_DECISION_URI_ONLY_SERVICE);
        assertThat(CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, null)).isEqualTo(TEST_DECISION_URI_ONLY_SERVICE);
        assertThat(CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, "")).isEqualTo(TEST_DECISION_URI_ONLY_SERVICE);
        assertThat(CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, null, null)).isEqualTo(TEST_DECISION_URI_ONLY_SERVICE);
        assertThat(CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, "", "")).isEqualTo(TEST_DECISION_URI_ONLY_SERVICE);

        assertThat(CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, TEST_DECISION_MODEL_NAME)).isEqualTo(TEST_DECISION_URI_SERVICE_AND_MODEL);
        assertThat(CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, TEST_DECISION_MODEL_NAME, null)).isEqualTo(TEST_DECISION_URI_SERVICE_AND_MODEL);
        assertThat(CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, TEST_DECISION_MODEL_NAME, "")).isEqualTo(TEST_DECISION_URI_SERVICE_AND_MODEL);

        assertThat(CloudEventUtils.buildDecisionSource(TEST_SERVICE_URL, TEST_DECISION_MODEL_NAME, TEST_DECISION_SERVICE_NAME)).isEqualTo(TEST_DECISION_URI_FULL);
    }

    @Test
    void testBuildSuccess() {
        assertThat(CloudEventUtils.build(TEST_ID, TEST_URI, TEST_DATA, TEST_DATA_CLASS)).isPresent();
    }

    @Test
    void testBuildFailure() throws Exception {
        runWithMockedCloudEventUtilsMapper(() -> assertThat(CloudEventUtils.build(TEST_ID, TEST_URI, TEST_DATA, TEST_DATA_CLASS)).isNotPresent());
    }

    @Test
    void testBuildWithExtensionSuccess() {
        Optional<CloudEvent> optCE = CloudEventUtils.build(TEST_ID, TEST_URI, TEST_DATA_CLASS.getSimpleName(), TEST_SUBJECT, TEST_DATA, TEST_EXTENSION);
        assertThat(optCE).isPresent();
        assertThat(ExtensionProvider.getInstance().parseExtension(KogitoExtension.class, optCE.get())).isEqualTo(TEST_EXTENSION);
    }

    @Test
    void testBuildWithExtensionFailure() throws Exception {
        runWithMockedCloudEventUtilsMapper(() -> assertThat(CloudEventUtils.build(TEST_ID, TEST_URI, TEST_DATA_CLASS.getSimpleName(), TEST_SUBJECT, TEST_DATA, TEST_EXTENSION)).isNotPresent());
    }

    @Test
    void testDecodeDataSuccess() {
        Optional<String> optData = CloudEventUtils.decodeData(TEST_CLOUDEVENT, String.class);
        assertThat(optData).contains(TEST_DATA);
    }

    @Test
    void testDecodeDataFailure() {
        Optional<Integer> optData = CloudEventUtils.decodeData(TEST_CLOUDEVENT, Integer.class);
        assertThat(optData).isNotPresent();
    }

    @Test
    void testEncodeSuccess() {
        assertThat(CloudEventUtils.encode(TEST_CLOUDEVENT)).isPresent();
        System.out.println(CloudEventUtils.encode(TEST_CLOUDEVENT).get());
    }

    @Test
    void testEncodeFailure() throws Exception {
        runWithMockedCloudEventUtilsMapper(() -> assertThat(CloudEventUtils.encode(TEST_CLOUDEVENT)).isNotPresent());
    }

    @Test
    void testDecodeSuccess() {
        assertThat(CloudEventUtils.decode(TEST_CORRECT_JSON)).isPresent();
    }

    @Test
    void testDecodeFailure() {
        assertThat(CloudEventUtils.decode(TEST_MALFORMED_JSON)).isNotPresent();
    }

    @Test
    void testUrlEncodedStringFromSuccess() {
        assertThat(CloudEventUtils.urlEncodedStringFrom(TEST_URI_STRING)).isPresent();
    }

    @Test
    void testUrlEncodedStringFromFailure() throws Exception {
        try (MockedStatic<URLEncoder> mockedStaticURLEncoder = mockStatic(URLEncoder.class)) {
            mockedStaticURLEncoder.when(() -> URLEncoder.encode(any(String.class), any(String.class))).thenThrow(new UnsupportedEncodingException());
            assertThat(CloudEventUtils.urlEncodedStringFrom(TEST_URI_STRING)).isNotPresent();
        }
    }

    @Test
    void testUrlEncodedURIFromSuccess() {
        assertThat(CloudEventUtils.urlEncodedURIFrom(TEST_URI_STRING)).isPresent();
    }

    @Test
    void testUrlEncodedURIFromFailure() {
        try (MockedStatic<URI> mockedStaticURLEncoder = mockStatic(URI.class)) {
            mockedStaticURLEncoder.when(() -> URI.create(any(String.class))).thenThrow(new IllegalArgumentException());
            assertThat(CloudEventUtils.urlEncodedURIFrom(TEST_URI_STRING)).isNotPresent();
        }
    }

    @Test
    void testLocalDate() throws JsonProcessingException {
        LocalDate localDate = LocalDate.of(2021, 12, 21);
        String marshalled = CloudEventUtils.Mapper.mapper().writeValueAsString(localDate);
        LocalDate unmarshalled = CloudEventUtils.Mapper.mapper().readValue(marshalled, LocalDate.class);
        assertThat(unmarshalled).isEqualTo(localDate);
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

    @Test
    void testFromValue() throws IOException {
        ObjectMapper objectMapper = Mapper.mapper();
        CloudEventBuilder builder =
                CloudEventBuilder.v1().withId("1").withType("type").withSource(URI.create("/pepe/pepa")).withData(JsonCloudEventData.wrap(objectMapper.createObjectNode().put("name", "Javierito")))
                        .withExtension("pepe", "pepa");
        DataEvent<JsonNode> dataEvent = DataEventFactory.from(builder.build(), ced -> objectMapper.readTree(ced.toBytes()));
        JsonNode deserialized = CloudEventUtils.fromValue(dataEvent);
        System.out.println(deserialized);
        JsonNode data = deserialized.get("data");
        assertThat(data).isNotNull();
        assertThat(data.get("name").asText()).isEqualTo("Javierito");
        assertThat(deserialized.get("type").asText()).isEqualTo("type");
        assertThat(deserialized.get("pepe").asText()).isEqualTo("pepa");
    }
}
