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

package org.kie.kogito.tracing.typedvalue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;
import org.junit.jupiter.api.Test;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypedValueTest {

    @Test
    void test() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        List<TypedValue> variables = Arrays.asList(
                new UnitValue("number", "baseType", new DoubleNode(1d)),
                new CollectionValue("list", singleton(new UnitValue("string"))),
                new StructureValue("tStruct", singletonMap("key", new UnitValue("value")))
        );

        String serializedJson = mapper.writeValueAsString(variables);

        List<TypedValue> serializedVariables = mapper.readValue(serializedJson, new TypeReference<List<TypedValue>>() {
        });

        assertNotNull(serializedVariables);
        assertSame(3, serializedVariables.size());

        // test unit
        TypedValue serializedVariable1 = serializedVariables.get(0);
        assertSame(TypedValue.Kind.UNIT, serializedVariable1.getKind());
        assertTrue(serializedVariable1 instanceof UnitValue);
        assertTrue(serializedVariable1.isUnit());
        assertFalse(serializedVariable1.isCollection());
        assertFalse(serializedVariable1.isStructure());
        assertThrows(IllegalStateException.class, serializedVariable1::toCollection);

        UnitValue unitValue = serializedVariable1.toUnit();
        assertNotNull(unitValue);
        assertEquals("baseType", unitValue.getBaseType());
        assertTrue(unitValue.getValue() instanceof DoubleNode);
        assertEquals(1d, unitValue.getValue().doubleValue(), 0.1);

        // test collection
        TypedValue serializableVariable2 = serializedVariables.get(1);
        assertSame(TypedValue.Kind.COLLECTION, serializableVariable2.getKind());
        assertTrue(serializableVariable2 instanceof CollectionValue);
        assertFalse(serializableVariable2.isUnit());
        assertTrue(serializableVariable2.isCollection());
        assertFalse(serializableVariable2.isStructure());
        assertNotNull(serializableVariable2.toCollection());
        assertThrows(IllegalStateException.class, serializableVariable2::toStructure);

        CollectionValue collectionValue = serializableVariable2.toCollection();
        assertNotNull(collectionValue);
        assertEquals(1, collectionValue.getValue().size());
        TypedValue value = collectionValue.getValue().iterator().next();
        assertTrue(value.isUnit());
        assertEquals("string", value.toUnit().getType());

        // test structure
        TypedValue serializableVariable3 = serializedVariables.get(2);
        assertSame(TypedValue.Kind.STRUCTURE, serializableVariable3.getKind());
        assertTrue(serializableVariable3 instanceof StructureValue);
        assertFalse(serializableVariable3.isUnit());
        assertFalse(serializableVariable3.isCollection());
        assertTrue(serializableVariable3.isStructure());
        assertNotNull(serializableVariable3.toStructure());
        assertThrows(IllegalStateException.class, serializableVariable3::toUnit);

        StructureValue structureValue = serializableVariable3.toStructure();
        assertNotNull(structureValue);
        assertEquals(1, structureValue.getValue().size());
        Map<String, TypedValue> valueMap = structureValue.getValue();
        assertTrue(valueMap.containsKey("key"));
        assertEquals("value", valueMap.get("key").getType());
    }
}
