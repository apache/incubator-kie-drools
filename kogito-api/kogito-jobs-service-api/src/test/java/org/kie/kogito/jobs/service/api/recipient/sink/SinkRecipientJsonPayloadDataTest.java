/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.jobs.service.api.recipient.sink;

import java.util.Objects;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static org.assertj.core.api.Assertions.assertThat;

class SinkRecipientJsonPayloadDataTest {

    public static final String PROPERTY_NAME = "PROPERTY_NAME";
    public static final String PROPERTY_VALUE = "PROPERTY_VALUE";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getData() {
        ObjectNode json = objectMapper.createObjectNode().put(PROPERTY_NAME, PROPERTY_VALUE);
        SinkRecipientJsonPayloadData payloadData = SinkRecipientJsonPayloadData.from(json);
        assertThat(payloadData.getData()).isEqualTo(json);
    }

    @Test
    void equalsMethod() {
        ObjectNode json1 = objectMapper.createObjectNode().put(PROPERTY_NAME, PROPERTY_VALUE);
        ObjectNode json2 = objectMapper.createObjectNode().put(PROPERTY_NAME, PROPERTY_VALUE);
        SinkRecipientJsonPayloadData payloadData1 = SinkRecipientJsonPayloadData.from(json1);
        SinkRecipientJsonPayloadData payloadData2 = SinkRecipientJsonPayloadData.from(json2);
        assertThat(Objects.equals(payloadData1, payloadData2)).isTrue();
        json2.remove(PROPERTY_NAME);
        assertThat(Objects.equals(payloadData1, payloadData2)).isFalse();
    }

    @Test
    void hashCodeMethod() {
        ObjectNode json = objectMapper.createObjectNode().put(PROPERTY_NAME, PROPERTY_VALUE);
        SinkRecipientJsonPayloadData payloadData = SinkRecipientJsonPayloadData.from(json);
        assertThat(payloadData.hashCode()).isEqualTo(Objects.hash(json));
    }
}
