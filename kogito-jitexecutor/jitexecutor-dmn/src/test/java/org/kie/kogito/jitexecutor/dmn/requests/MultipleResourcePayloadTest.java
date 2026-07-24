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
package org.kie.kogito.jitexecutor.dmn.requests;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.kie.kogito.jitexecutor.common.requests.MultipleResourcesPayload;
import org.kie.kogito.jitexecutor.common.requests.ResourceWithURI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

public class MultipleResourcePayloadTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    void deserialize() throws JsonProcessingException {
        String json = "{\n" +
                "  \"mainURI\": \"main.dmn\",\n" +
                "  \"resources\": [\n" +
                "    {\n" +
                "      \"uri\": \"resource1.dmn\",\n" +
                "      \"content\": \"<dmn>...</dmn>\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"uri\": \"resource2.dmn\",\n" +
                "      \"content\": \"<dmn>...</dmn>\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"strictMode\": true\n" +
                "}";

        MultipleResourcesPayload payload = MAPPER.readValue(json, MultipleResourcesPayload.class);
        assertThat(payload).isNotNull();
        assertThat(payload.getMainURI()).isEqualTo("main.dmn");
        assertThat(payload.getResources()).hasSize(2);
        assertThat(payload.isStrictMode()).isTrue();
    }

    @Test
    void roundTripSerialization() throws JsonProcessingException {
        ResourceWithURI resource1 = new ResourceWithURI("resource1.dmn", "<dmn>resource1</dmn>");
        ResourceWithURI resource2 = new ResourceWithURI("resource2.dmn", "<dmn>resource2</dmn>");

        MultipleResourcesPayload original = new MultipleResourcesPayload(
                "main.dmn",
                List.of(resource1, resource2),
                true);

        String json = MAPPER.writeValueAsString(original);

        MultipleResourcesPayload deserialized = MAPPER.readValue(json, MultipleResourcesPayload.class);

        assertThat(deserialized).isNotNull();
        assertThat(deserialized.getMainURI()).isEqualTo(original.getMainURI());
        assertThat(deserialized.getResources()).hasSize(2);
        assertThat(deserialized.getResources().get(0).getURI()).isEqualTo("resource1.dmn");
        assertThat(deserialized.isStrictMode()).isTrue();
    }
}
