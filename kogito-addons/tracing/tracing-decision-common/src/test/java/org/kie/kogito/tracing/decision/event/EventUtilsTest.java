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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.junit.jupiter.api.Test;
import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.kogito.tracing.decision.event.message.InternalMessageType;
import org.kie.kogito.tracing.typedvalue.TypedValue;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EventUtilsTest {

    @Test
    void testDoesNotThrowOnNullValues() {
        assertDoesNotThrow(() -> EventUtils.<Integer,String>map(null, null));
        assertDoesNotThrow(() -> EventUtils.messageFrom((DMNMessage) null));
        assertDoesNotThrow(() -> EventUtils.messageFrom((InternalMessageType) null));
        assertDoesNotThrow(() -> EventUtils.messageFrom(null, null));
        assertDoesNotThrow(() -> EventUtils.messageExceptionFieldFrom(null));
        assertDoesNotThrow(() -> EventUtils.messageFEELEventFrom(null));
        assertDoesNotThrow(() -> EventUtils.messageFEELEventSeverityFrom(null));
        assertDoesNotThrow(() -> EventUtils.messageLevelFrom(null));
        assertDoesNotThrow(() -> EventUtils.traceResourceIdFrom(null, null));
        assertDoesNotThrow(() -> EventUtils.typedValueFrom(null));
        assertDoesNotThrow(() -> EventUtils.typedValueFrom(null, null));
    }

    @Test
    void testTypedVariableFromJsonNode() throws JsonProcessingException {
        ObjectReader reader = new ObjectMapper().reader();

        TypedValue value = EventUtils.typedValueFromJsonNode(null, null);
        assertNotNull(value);
        assertSame(TypedValue.Kind.UNIT, value.getKind());
        assertEquals(BuiltInType.UNKNOWN.getName(), value.getType());

        value = EventUtils.typedValueFromJsonNode(reader.readTree("true"), null);
        assertNotNull(value);
        assertSame(TypedValue.Kind.UNIT, value.getKind());
        assertEquals(BuiltInType.BOOLEAN.getName(), value.getType());

        value = EventUtils.typedValueFromJsonNode(reader.readTree("12"), null);
        assertNotNull(value);
        assertSame(TypedValue.Kind.UNIT, value.getKind());
        assertEquals(BuiltInType.NUMBER.getName(), value.getType());

        value = EventUtils.typedValueFromJsonNode(reader.readTree("\"test\""), null);
        assertNotNull(value);
        assertSame(TypedValue.Kind.UNIT, value.getKind());
        assertEquals(BuiltInType.STRING.getName(), value.getType());

        value = EventUtils.typedValueFromJsonNode(reader.readTree("[1,2,3]"), null);
        assertNotNull(value);
        assertSame(TypedValue.Kind.COLLECTION, value.getKind());
        assertEquals(BuiltInType.LIST.getName(), value.getType());

        value = EventUtils.typedValueFromJsonNode(reader.readTree("{\"name\": \"John\", \"age\": 45, \"married\": true}"), null);
        assertNotNull(value);
        assertSame(TypedValue.Kind.STRUCTURE, value.getKind());
        assertEquals(BuiltInType.UNKNOWN.getName(), value.getType());
    }

    @Test
    void testTypedVariableFromJsonNodeWithDMNType() throws JsonProcessingException {
        ObjectReader reader = new ObjectMapper().reader();

        TypedValue value = EventUtils.typedValueFromJsonNode(mockDMNType("Any"),null, null);
        assertNotNull(value);
        assertSame(TypedValue.Kind.UNIT, value.getKind());
        assertEquals(BuiltInType.UNKNOWN.getName(), value.getType());

        value = EventUtils.typedValueFromJsonNode(mockDMNType("boolean"), reader.readTree("true"), null);
        assertNotNull(value);
        assertSame(TypedValue.Kind.UNIT, value.getKind());
        assertEquals(BuiltInType.BOOLEAN.getName(), value.getType());

        value = EventUtils.typedValueFromJsonNode(mockDMNType("number"), reader.readTree("12"), null);
        assertNotNull(value);
        assertSame(TypedValue.Kind.UNIT, value.getKind());
        assertEquals(BuiltInType.NUMBER.getName(), value.getType());

        value = EventUtils.typedValueFromJsonNode(mockDMNType("string"), reader.readTree("\"test\""), null);
        assertNotNull(value);
        assertSame(TypedValue.Kind.UNIT, value.getKind());
        assertEquals(BuiltInType.STRING.getName(), value.getType());

        value = EventUtils.typedValueFromJsonNode(mockDMNType("number"), reader.readTree("[1,2,3]"), null);
        assertNotNull(value);
        assertSame(TypedValue.Kind.COLLECTION, value.getKind());
        assertEquals(BuiltInType.NUMBER.getName(), value.getType());

        value = EventUtils.typedValueFromJsonNode(mockDMNType("Person"), reader.readTree("{\"name\": \"John\", \"age\": 45, \"married\": true}"), null);
        assertNotNull(value);
        assertSame(TypedValue.Kind.STRUCTURE, value.getKind());
        assertEquals("Person", value.getType());
    }

    private DMNType mockDMNType(String name) {
        DMNType mock = mock(DMNType.class);
        when(mock.getName()).thenReturn(name);
        return mock;
    }

}
