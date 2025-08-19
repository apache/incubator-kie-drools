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

import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

public class JITDMNPayloadTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    void deserialize() throws JsonProcessingException {
        String json = "{\n" +
                "  \"model\": \"<dmn>...</dmn>\",\n" +
                "  \"context\": {\n" +
                "    \"ApplicantAge\": 35,\n" +
                "    \"Income\": 75000\n" +
                "  }\n" +
                "}";

        JITDMNPayload payload = MAPPER.readValue(json, JITDMNPayload.class);
        assertThat(payload).isNotNull();
        assertThat(payload.getModel()).isEqualTo("<dmn>...</dmn>");
        assertThat(payload.getContext()).containsEntry("ApplicantAge", 35).containsEntry("Income", 75000);
        assertThat(payload.isStrictMode()).isFalse();
    }

    @Test
    void deserializeWithStrictMode() throws JsonProcessingException {
        String json = "{\n" +
                "  \"model\": \"<dmn>...</dmn>\",\n" +
                "  \"context\": {\n" +
                "    \"ApplicantAge\": 35,\n" +
                "    \"Income\": 75000\n" +
                "  },\n" +
                "  \"isStrictMode\": true\n" +
                "}";

        JITDMNPayload payload = MAPPER.readValue(json, JITDMNPayload.class);
        assertThat(payload).isNotNull();
        assertThat(payload.getModel()).isEqualTo("<dmn>...</dmn>");
        assertThat(payload.getContext()).containsEntry("ApplicantAge", 35).containsEntry("Income", 75000);
        assertThat(payload.isStrictMode()).isTrue();
    }

    @Test
    void roundTripSerialization() throws JsonProcessingException {
        JITDMNPayload original = new JITDMNPayload(
                "<dmn>...</dmn>",
                Map.of("ApplicantAge", 35, "Income", 75000));

        String json = MAPPER.writeValueAsString(original);
        JITDMNPayload deserialized = MAPPER.readValue(json, JITDMNPayload.class);

        assertThat(deserialized).isNotNull();
        assertThat(deserialized.getModel()).isEqualTo(original.getModel());
        assertThat(deserialized.getContext()).isEqualTo(original.getContext());
    }
}
