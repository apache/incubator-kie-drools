/*
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

package org.kie.kogito.tracing.decision.event;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CloudEventUtilsTest {

    private static final String TEST_DATA = "this-is-some-test-data";
    private static final Class<String> TEST_DATA_CLASS = String.class;
    private static final String TEST_ID = "acacfa12-5520-419a-91c4-05a57676dd8b";
    private static final String TEST_URI_STRING = "http://test-host/test-endpoint";
    private static final URI TEST_URI = URI.create(TEST_URI_STRING);

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

    @Test
    void testBuildSuccess() {
        assertTrue(CloudEventUtils.build(TEST_ID, TEST_URI, TEST_DATA, TEST_DATA_CLASS).isPresent());
    }

    @Test
    void testBuildFailure() throws Exception {
        runWithMockedCloudEventUtilsMapper(() ->
                assertFalse(CloudEventUtils.build(TEST_ID, TEST_URI, TEST_DATA, TEST_DATA_CLASS).isPresent())
        );
    }

    @Test
    void testEncodeSuccess() {
        assertTrue(CloudEventUtils.encode(TEST_CLOUDEVENT).isPresent());
        System.out.println(CloudEventUtils.encode(TEST_CLOUDEVENT).get());
    }

    @Test
    void testEncodeFailure() throws Exception {
        runWithMockedCloudEventUtilsMapper(() ->
                assertFalse(CloudEventUtils.encode(TEST_CLOUDEVENT).isPresent())
        );
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
        try (MockedStatic<URLEncoder> mockedStaticURLEncoder = Mockito.mockStatic(URLEncoder.class)) {
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
        try (MockedStatic<URI> mockedStaticURLEncoder = Mockito.mockStatic(URI.class)) {
            mockedStaticURLEncoder.when(() -> URI.create(any(String.class))).thenThrow(new IllegalArgumentException());
            assertFalse(CloudEventUtils.urlEncodedURIFrom(TEST_URI_STRING).isPresent());
        }
    }

    private static ObjectMapper getFailingMockedObjectMapper() throws Exception {
        ObjectMapper mockedMapper = mock(ObjectMapper.class);
        when(mockedMapper.writeValueAsBytes(any())).thenThrow(new JsonProcessingException(TEST_EXCEPTION_MESSAGE){});
        when(mockedMapper.writeValueAsString(any())).thenThrow(new JsonProcessingException(TEST_EXCEPTION_MESSAGE){});
        return mockedMapper;
    }

    private static void runWithMockedCloudEventUtilsMapper(Runnable runnable) throws Exception {
        try (MockedStatic<CloudEventUtils.Mapper> mockedStaticMapper = Mockito.mockStatic(CloudEventUtils.Mapper.class)) {
            ObjectMapper mockedMapper = getFailingMockedObjectMapper();
            mockedStaticMapper.when(CloudEventUtils.Mapper::mapper).thenReturn(mockedMapper);
            runnable.run();
        }
    }
}
