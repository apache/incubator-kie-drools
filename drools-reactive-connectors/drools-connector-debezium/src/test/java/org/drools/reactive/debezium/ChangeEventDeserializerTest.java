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
package org.drools.reactive.debezium;

import java.util.Collections;

import org.drools.reactive.api.ConnectorException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ChangeEventDeserializerTest {

    private ChangeEventDeserializer<TestRow> deserializer;

    @BeforeEach
    void setUp() {
        deserializer = new ChangeEventDeserializer<>(TestRow.class);
        deserializer.configure(Collections.emptyMap());
    }

    @Test
    void shouldDeserializeCreateEvent() {
        String json = "{\"payload\":{\"op\":\"c\",\"ts_ms\":1700000000," +
                "\"source\":{\"name\":\"mydb\",\"table\":\"orders\"}," +
                "\"after\":{\"id\":1,\"name\":\"order1\",\"amount\":99.5}}}";

        ChangeEvent<TestRow> event = deserializer.deserialize(json);

        assertThat(event).isNotNull();
        assertThat(event.getOperation()).isEqualTo(ChangeEventOperation.CREATE);
        assertThat(event.getSource()).isEqualTo("mydb");
        assertThat(event.getTable()).isEqualTo("orders");
        assertThat(event.getTimestamp()).isEqualTo(1700000000L);
        assertThat(event.getValue().getId()).isEqualTo(1);
        assertThat(event.getValue().getName()).isEqualTo("order1");
        assertThat(event.getValue().getAmount()).isEqualTo(99.5);
    }

    @Test
    void shouldDeserializeUpdateEvent() {
        String json = "{\"payload\":{\"op\":\"u\",\"ts_ms\":1700000001," +
                "\"source\":{\"name\":\"mydb\",\"table\":\"orders\"}," +
                "\"before\":{\"id\":1,\"name\":\"order1\",\"amount\":50.0}," +
                "\"after\":{\"id\":1,\"name\":\"order1\",\"amount\":100.0}}}";

        ChangeEvent<TestRow> event = deserializer.deserialize(json);

        assertThat(event.getOperation()).isEqualTo(ChangeEventOperation.UPDATE);
        assertThat(event.getValue().getAmount()).isEqualTo(100.0);
    }

    @Test
    void shouldDeserializeDeleteEvent() {
        String json = "{\"payload\":{\"op\":\"d\",\"ts_ms\":1700000002," +
                "\"source\":{\"name\":\"mydb\",\"table\":\"orders\"}," +
                "\"before\":{\"id\":1,\"name\":\"order1\",\"amount\":99.5}}}";

        ChangeEvent<TestRow> event = deserializer.deserialize(json);

        assertThat(event.getOperation()).isEqualTo(ChangeEventOperation.DELETE);
        assertThat(event.getValue().getId()).isEqualTo(1);
    }

    @Test
    void shouldHandlePayloadWithoutEnvelope() {
        String json = "{\"op\":\"c\",\"ts_ms\":123," +
                "\"source\":{\"name\":\"db\",\"table\":\"t\"}," +
                "\"after\":{\"id\":2,\"name\":\"x\",\"amount\":0.0}}";

        ChangeEvent<TestRow> event = deserializer.deserialize(json);

        assertThat(event.getOperation()).isEqualTo(ChangeEventOperation.CREATE);
        assertThat(event.getValue().getId()).isEqualTo(2);
    }

    @Test
    void shouldReturnNullForNullOrEmpty() {
        assertThat(deserializer.deserialize(null)).isNull();
        assertThat(deserializer.deserialize("")).isNull();
    }

    @Test
    void shouldParseReadOperation() {
        String json = "{\"op\":\"r\",\"ts_ms\":100," +
                "\"source\":{\"name\":\"db\",\"table\":\"t\"}," +
                "\"after\":{\"id\":3,\"name\":\"snap\",\"amount\":1.0}}";

        ChangeEvent<TestRow> event = deserializer.deserialize(json);
        assertThat(event.getOperation()).isEqualTo(ChangeEventOperation.READ);
    }

    @Test
    void shouldHandleMissingSourceGracefully() {
        String json = "{\"op\":\"c\",\"ts_ms\":100," +
                "\"after\":{\"id\":4,\"name\":\"noSource\",\"amount\":0.0}}";

        ChangeEvent<TestRow> event = deserializer.deserialize(json);

        assertThat(event.getOperation()).isEqualTo(ChangeEventOperation.CREATE);
        assertThat(event.getSource()).isNull();
        assertThat(event.getTable()).isNull();
    }

    @Test
    void shouldThrowOnMalformedJson() {
        assertThatThrownBy(() -> deserializer.deserialize("not-json"))
                .isInstanceOf(ConnectorException.class)
                .hasMessageContaining("Failed to deserialize");
    }
}
