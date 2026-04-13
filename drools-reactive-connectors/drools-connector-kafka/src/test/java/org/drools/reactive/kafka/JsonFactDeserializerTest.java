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
package org.drools.reactive.kafka;

import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.drools.reactive.api.ConnectorException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonFactDeserializerTest {

    private JsonFactDeserializer<TestEvent> deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new JsonFactDeserializer<>(TestEvent.class);
        deserializer.configure(Collections.emptyMap());
    }

    @Test
    void shouldDeserializeValidJson() {
        byte[] data = "{\"name\":\"test\",\"value\":42}".getBytes(StandardCharsets.UTF_8);

        TestEvent event = deserializer.deserialize("topic", data);

        assertThat(event).isNotNull();
        assertThat(event.getName()).isEqualTo("test");
        assertThat(event.getValue()).isEqualTo(42);
    }

    @Test
    void shouldReturnNullForNullData() {
        assertThat(deserializer.deserialize("topic", null)).isNull();
    }

    @Test
    void shouldReturnNullForEmptyData() {
        assertThat(deserializer.deserialize("topic", new byte[0])).isNull();
    }

    @Test
    void shouldThrowOnInvalidJson() {
        byte[] data = "not json".getBytes(StandardCharsets.UTF_8);

        assertThatThrownBy(() -> deserializer.deserialize("topic", data))
                .isInstanceOf(ConnectorException.class)
                .hasMessageContaining("Failed to deserialize JSON")
                .hasMessageContaining("topic");
    }

    @Test
    void shouldIgnoreUnknownProperties() {
        byte[] data = "{\"name\":\"test\",\"value\":1,\"unknownField\":\"ignored\"}"
                .getBytes(StandardCharsets.UTF_8);

        TestEvent event = deserializer.deserialize("topic", data);

        assertThat(event).isNotNull();
        assertThat(event.getName()).isEqualTo("test");
        assertThat(event.getValue()).isEqualTo(1);
    }
}
