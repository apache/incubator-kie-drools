/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.tracing.decision.event;

import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.kogito.tracing.decision.message.InternalMessageType;
import org.kie.kogito.tracing.typedvalue.TypedValue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventUtilsTest {

    @Test
    void testDoesNotThrowOnNullValues() {
        assertThatNoException().isThrownBy(() -> EventUtils.<Integer, String> map(null, null));
        assertThatNoException().isThrownBy(() -> EventUtils.messageFrom((DMNMessage) null));
        assertThatNoException().isThrownBy(() -> EventUtils.messageFrom((InternalMessageType) null));
        assertThatNoException().isThrownBy(() -> EventUtils.messageFrom(null, null));
        assertThatNoException().isThrownBy(() -> EventUtils.messageExceptionFieldFrom(null));
        assertThatNoException().isThrownBy(() -> EventUtils.messageFEELEventFrom(null));
        assertThatNoException().isThrownBy(() -> EventUtils.messageFEELEventSeverityFrom(null));
        assertThatNoException().isThrownBy(() -> EventUtils.messageLevelFrom(null));
        assertThatNoException().isThrownBy(() -> EventUtils.traceResourceIdFrom(null, null));
        assertThatNoException().isThrownBy(() -> EventUtils.typedValueFrom(null));
        assertThatNoException().isThrownBy(() -> EventUtils.typedValueFrom(null, null));
    }

    @Test
    void testTypedVariableFromJsonNode() throws JsonProcessingException {
        ObjectReader reader = new ObjectMapper().reader();

        TypedValue value = EventUtils.typedValueFromJsonNode(null, null);
        assertThat(value).isNotNull();
        assertThat(value.getKind()).isSameAs(TypedValue.Kind.UNIT);
        assertThat(value.getType()).isEqualTo(BuiltInType.UNKNOWN.getName());

        value = EventUtils.typedValueFromJsonNode(reader.readTree("true"), null);
        assertThat(value).isNotNull();
        assertThat(value.getKind()).isSameAs(TypedValue.Kind.UNIT);
        assertThat(value.getType()).isEqualTo(BuiltInType.BOOLEAN.getName());

        value = EventUtils.typedValueFromJsonNode(reader.readTree("12"), null);
        assertThat(value).isNotNull();
        assertThat(value.getKind()).isSameAs(TypedValue.Kind.UNIT);
        assertThat(value.getType()).isEqualTo(BuiltInType.NUMBER.getName());

        value = EventUtils.typedValueFromJsonNode(reader.readTree("\"test\""), null);
        assertThat(value).isNotNull();
        assertThat(value.getKind()).isSameAs(TypedValue.Kind.UNIT);
        assertThat(value.getType()).isEqualTo(BuiltInType.STRING.getName());

        value = EventUtils.typedValueFromJsonNode(reader.readTree("[1,2,3]"), null);
        assertThat(value).isNotNull();
        assertThat(value.getKind()).isSameAs(TypedValue.Kind.COLLECTION);
        assertThat(value.getType()).isEqualTo(BuiltInType.LIST.getName());

        value = EventUtils.typedValueFromJsonNode(reader.readTree("{\"name\": \"John\", \"age\": 45, \"married\": true}"), null);
        assertThat(value).isNotNull();
        assertThat(value.getKind()).isSameAs(TypedValue.Kind.STRUCTURE);
        assertThat(value.getType()).isEqualTo(BuiltInType.UNKNOWN.getName());
    }

    @Test
    void testTypedVariableFromJsonNodeWithDMNType() throws JsonProcessingException {
        ObjectReader reader = new ObjectMapper().reader();

        TypedValue value = EventUtils.typedValueFromJsonNode(mockDMNType("Any"), null, null);
        assertThat(value).isNotNull();
        assertThat(value.getKind()).isSameAs(TypedValue.Kind.UNIT);
        assertThat(value.getType()).isEqualTo(BuiltInType.UNKNOWN.getName());

        value = EventUtils.typedValueFromJsonNode(mockDMNType("boolean"), reader.readTree("true"), null);
        assertThat(value).isNotNull();
        assertThat(value.getKind()).isSameAs(TypedValue.Kind.UNIT);
        assertThat(value.getType()).isEqualTo(BuiltInType.BOOLEAN.getName());

        value = EventUtils.typedValueFromJsonNode(mockDMNType("number"), reader.readTree("12"), null);
        assertThat(value).isNotNull();
        assertThat(value.getKind()).isSameAs(TypedValue.Kind.UNIT);
        assertThat(value.getType()).isEqualTo(BuiltInType.NUMBER.getName());

        value = EventUtils.typedValueFromJsonNode(mockDMNType("string"), reader.readTree("\"test\""), null);
        assertThat(value).isNotNull();
        assertThat(value.getKind()).isSameAs(TypedValue.Kind.UNIT);
        assertThat(value.getType()).isEqualTo(BuiltInType.STRING.getName());

        value = EventUtils.typedValueFromJsonNode(mockDMNType("number"), reader.readTree("[1,2,3]"), null);
        assertThat(value).isNotNull();
        assertThat(value.getKind()).isSameAs(TypedValue.Kind.COLLECTION);
        assertThat(value.getType()).isEqualTo(BuiltInType.NUMBER.getName());

        value = EventUtils.typedValueFromJsonNode(mockDMNType("Person"), reader.readTree("{\"name\": \"John\", \"age\": 45, \"married\": true}"), null);
        assertThat(value).isNotNull();
        assertThat(value.getKind()).isSameAs(TypedValue.Kind.STRUCTURE);
        assertThat(value.getType()).isEqualTo("Person");
    }

    private DMNType mockDMNType(String name) {
        DMNType mock = mock(DMNType.class);
        when(mock.getName()).thenReturn(name);
        return mock;
    }

}
