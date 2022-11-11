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
package org.kie.kogito.tracing.typedvalue;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.within;

class TypedValueTest {

    @Test
    void test() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        List<TypedValue> variables = Arrays.asList(
                new UnitValue("number", "baseType", new DoubleNode(1d)),
                new CollectionValue("list", singleton(new UnitValue("string"))),
                new StructureValue("tStruct", singletonMap("key", new UnitValue("value"))));

        String serializedJson = mapper.writeValueAsString(variables);

        List<TypedValue> serializedVariables = mapper.readValue(serializedJson, new TypeReference<List<TypedValue>>() {
        });

        assertThat(serializedVariables).isNotNull();
        assertThat(serializedVariables.size()).isSameAs(3);

        // test unit
        TypedValue serializedVariable1 = serializedVariables.get(0);
        assertThat(serializedVariable1.getKind()).isSameAs(TypedValue.Kind.UNIT);
        assertThat(serializedVariable1).isInstanceOf(UnitValue.class);
        assertThat(serializedVariable1.isUnit()).isTrue();
        assertThat(serializedVariable1.isCollection()).isFalse();
        assertThat(serializedVariable1.isStructure()).isFalse();
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(serializedVariable1::toCollection);

        UnitValue unitValue = serializedVariable1.toUnit();
        assertThat(unitValue).isNotNull();
        assertThat(unitValue.getBaseType()).isEqualTo("baseType");
        assertThat(unitValue.getValue()).isInstanceOf(DoubleNode.class);
        assertThat(unitValue.getValue().doubleValue()).isCloseTo(1d, within(0.1));

        // test collection
        TypedValue serializableVariable2 = serializedVariables.get(1);
        assertThat(serializableVariable2.getKind()).isSameAs(TypedValue.Kind.COLLECTION);
        assertThat(serializableVariable2).isInstanceOf(CollectionValue.class);
        assertThat(serializableVariable2.isUnit()).isFalse();
        assertThat(serializableVariable2.isCollection()).isTrue();
        assertThat(serializableVariable2.isStructure()).isFalse();
        assertThat(serializableVariable2.toCollection()).isNotNull();
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(serializableVariable2::toStructure);

        CollectionValue collectionValue = serializableVariable2.toCollection();
        assertThat(collectionValue).isNotNull();
        assertThat(collectionValue.getValue()).hasSize(1);
        TypedValue value = collectionValue.getValue().iterator().next();
        assertThat(value.isUnit()).isTrue();
        assertThat(value.toUnit().getType()).isEqualTo("string");

        // test structure
        TypedValue serializableVariable3 = serializedVariables.get(2);
        assertThat(serializableVariable3.getKind()).isSameAs(TypedValue.Kind.STRUCTURE);
        assertThat(serializableVariable3).isInstanceOf(StructureValue.class);
        assertThat(serializableVariable3.isUnit()).isFalse();
        assertThat(serializableVariable3.isCollection()).isFalse();
        assertThat(serializableVariable3.isStructure()).isTrue();
        assertThat(serializableVariable3.toStructure()).isNotNull();
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(serializableVariable3::toUnit);

        StructureValue structureValue = serializableVariable3.toStructure();
        assertThat(structureValue).isNotNull();
        assertThat(structureValue.getValue()).hasSize(1);
        Map<String, TypedValue> valueMap = structureValue.getValue();
        assertThat(valueMap).containsKey("key");
        assertThat(valueMap.get("key").getType()).isEqualTo("value");
    }
}
