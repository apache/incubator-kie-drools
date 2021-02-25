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
package org.kie.kogito.explainability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.kie.kogito.tracing.typedvalue.CollectionValue;
import org.kie.kogito.tracing.typedvalue.StructureValue;
import org.kie.kogito.tracing.typedvalue.TypedValue;
import org.kie.kogito.tracing.typedvalue.UnitValue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import io.vertx.core.json.JsonObject;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConversionUtilsTest {

    @Test
    void toFeatureObject() {
        Feature name = ConversionUtils.toFeature("name", 10d);
        assertNotNull(name);
        assertEquals("name", name.getName());
        assertEquals(Type.NUMBER, name.getType());
        assertEquals(10d, name.getValue().getUnderlyingObject());

        JsonObject jsonObject = new JsonObject(singletonMap("key", 10d));
        Feature output = ConversionUtils.toFeature("output", jsonObject);
        assertNotNull(output);
        assertEquals("output", output.getName());
        assertEquals(Type.COMPOSITE, output.getType());
        assertEquals(10d, name.getValue().getUnderlyingObject());
    }

    @Test
    void toFeatureTypedValue() {
        Feature name = ConversionUtils.toFeature("name", new UnitValue("number", new DoubleNode(10d)));
        assertNotNull(name);
        assertEquals("name", name.getName());
        assertEquals(Type.NUMBER, name.getType());
        assertEquals(10d, name.getValue().getUnderlyingObject());

        Feature name1 = ConversionUtils.toFeature("name1",
                new StructureValue("complex", singletonMap(
                        "key",
                        new UnitValue("string1", new TextNode("stringValue")))));
        assertNotNull(name1);
        assertEquals("name1", name1.getName());
        assertEquals(Type.COMPOSITE, name1.getType());
        assertTrue(name1.getValue().getUnderlyingObject() instanceof List);
        @SuppressWarnings("unchecked")
        List<Feature> features = (List<Feature>) name1.getValue().getUnderlyingObject();
        assertEquals(1, features.size());
        assertEquals(Type.TEXT, features.get(0).getType());
        assertEquals("stringValue", features.get(0).getValue().getUnderlyingObject());

        List<TypedValue> values = List.of(new UnitValue("number", new DoubleNode(0d)),
                new UnitValue("number", new DoubleNode(1d)));
        Feature collectionFeature = ConversionUtils.toFeature("name", new CollectionValue("list", values));
        assertNotNull(collectionFeature);
        assertEquals("name", collectionFeature.getName());
        assertEquals(Type.COMPOSITE, collectionFeature.getType());
        assertTrue(collectionFeature.getValue().getUnderlyingObject() instanceof List);
        @SuppressWarnings("unchecked")
        List<Feature> objects = (List<Feature>) collectionFeature.getValue().getUnderlyingObject();
        assertEquals(2, objects.size());
        for (Feature f : objects) {
            assertNotNull(f);
            assertNotNull(f.getName());
            assertNotNull(f.getType());
            assertEquals(Type.NUMBER, f.getType());
            assertNotNull(f.getValue());
        }
    }

    @Test
    void testNestedCollection() {
        Collection<TypedValue> depthTwoOne = new ArrayList<>(2);
        depthTwoOne.add(new StructureValue("complex", singletonMap(
                "key",
                new UnitValue("string1", new TextNode("value one")))));
        depthTwoOne.add(new StructureValue("complex", singletonMap(
                "key",
                new UnitValue("string1", new TextNode("value two")))));

        Collection<TypedValue> depthTwoTwo = new ArrayList<>(2);
        depthTwoTwo.add(new StructureValue("complex", singletonMap(
                "key",
                new UnitValue("string1", new TextNode("value three")))));
        depthTwoTwo.add(new StructureValue("complex", singletonMap(
                "key",
                new UnitValue("string1", new TextNode("value four")))));

        CollectionValue depthOneLeft = new CollectionValue("list", depthTwoOne);
        CollectionValue depthOneRight = new CollectionValue("list", depthTwoTwo);
        Collection<TypedValue> depthOne = new ArrayList<>(2);
        depthOne.add(depthOneLeft);
        depthOne.add(depthOneRight);
        CollectionValue value = new CollectionValue("list", depthOne);
        Feature collectionFeature = ConversionUtils.toFeature("name", value);
        assertNotNull(collectionFeature);
        assertEquals("name", collectionFeature.getName());
        assertEquals(Type.COMPOSITE, collectionFeature.getType());
        assertTrue(collectionFeature.getValue().getUnderlyingObject() instanceof List);
        @SuppressWarnings("unchecked")
        List<Feature> deepFeatures = (List<Feature>) collectionFeature.getValue().getUnderlyingObject();
        assertEquals(2, deepFeatures.size());
        for (Feature f : deepFeatures) {
            assertNotNull(f);
            assertNotNull(f.getName());
            assertNotNull(f.getType());
            assertEquals(Type.COMPOSITE, f.getType());
            assertNotNull(f.getValue());
            List<Feature> nestedOneValues = (List<Feature>) f.getValue().getUnderlyingObject();
            for (Feature nestedOneValue : nestedOneValues) {
                assertNotNull(nestedOneValue);
                assertNotNull(nestedOneValue.getName());
                assertNotNull(nestedOneValue.getType());
                assertEquals(Type.COMPOSITE, nestedOneValue.getType());
                assertNotNull(nestedOneValue.getValue());
                List<Feature> nestedTwoValues = (List<Feature>) nestedOneValue.getValue().getUnderlyingObject();
                for (Feature nestedTwoValue : nestedTwoValues) {
                    assertNotNull(nestedTwoValue);
                    assertNotNull(nestedTwoValue.getName());
                    assertNotNull(nestedTwoValue.getType());
                    assertEquals(Type.TEXT, nestedTwoValue.getType());
                    assertNotNull(nestedTwoValue.getValue());
                    assertTrue(nestedTwoValue.getValue().asString().contains("value"));
                }
            }
        }
    }

    @Test
    void testToList() {
        assertEquals(emptyList(), ConversionUtils.toList((JsonObject) null, (a, b) -> a));
        assertEquals(emptyList(), ConversionUtils.toList((Map<String, TypedValue>) null, (a, b) -> a));
    }

    @Test
    void toOutputObject() {
        Output name = ConversionUtils.toOutput("name", 10d);
        assertNotNull(name);
        assertEquals("name", name.getName());
        assertEquals(Type.NUMBER, name.getType());
        assertEquals(10d, name.getValue().getUnderlyingObject());

        JsonObject jsonObject = new JsonObject(singletonMap("key", 10d));
        Output output = ConversionUtils.toOutput("output", jsonObject);
        assertNotNull(output);
        assertEquals("output", output.getName());
        assertEquals(Type.COMPOSITE, output.getType());
        assertEquals(10d, name.getValue().getUnderlyingObject());
    }

    @Test
    void toOutputTypedValue() {
        Output name = ConversionUtils.toOutput("name", new UnitValue("number", new DoubleNode(10d)));
        assertNotNull(name);
        assertEquals("name", name.getName());
        assertEquals(Type.NUMBER, name.getType());
        assertEquals(10d, name.getValue().getUnderlyingObject());

        Output name1 = ConversionUtils.toOutput("name1",
                new StructureValue("complex", singletonMap(
                        "key",
                        new UnitValue("string1", new TextNode("stringValue")))));
        assertNotNull(name1);
        assertEquals("name1", name1.getName());
        assertEquals(Type.COMPOSITE, name1.getType());
        assertTrue(name1.getValue().getUnderlyingObject() instanceof List);
        @SuppressWarnings("unchecked")
        List<Feature> features = (List<Feature>) name1.getValue().getUnderlyingObject();
        assertEquals(1, features.size());
        assertEquals(Type.TEXT, features.get(0).getType());
        assertEquals("stringValue", features.get(0).getValue().getUnderlyingObject());

        List<TypedValue> values = List.of(new UnitValue("number", new DoubleNode(0d)),
                new UnitValue("number", new DoubleNode(1d)));
        assertNotNull(ConversionUtils.toOutput("name", new CollectionValue("list", values)));
    }

    @Test
    void testToTypeValuePairObject() {
        commonTypeValuePairObject("string", Type.TEXT, "string");
        commonTypeValuePairObject(10d, Type.NUMBER, 10d);
        commonTypeValuePairObject(true, Type.BOOLEAN, true);
        assertFalse(ConversionUtils.toTypeValuePair(new Object()).isPresent());
    }

    private void commonTypeValuePairObject(Object input, Type type, Object value) {
        Optional<Pair<Type, Value<Object>>> result = ConversionUtils.toTypeValuePair(input);
        assertTrue(result.isPresent());
        assertEquals(type, result.get().getKey());
        assertEquals(value, result.get().getValue().getUnderlyingObject());
    }

    @Test
    void testToTypeValuePairJson() {
        commonTypeValuePairJson(new TextNode("string"), Type.TEXT, "string");
        commonTypeValuePairJson(new DoubleNode(10d), Type.NUMBER, 10d);
        commonTypeValuePairJson(BooleanNode.TRUE, Type.BOOLEAN, true);
        assertFalse(ConversionUtils.toTypeValuePair(new ObjectNode(JsonNodeFactory.instance)).isPresent());
    }

    private void commonTypeValuePairJson(JsonNode input, Type type, Object value) {
        Optional<Pair<Type, Value<Object>>> result = ConversionUtils.toTypeValuePair(input);
        assertTrue(result.isPresent());
        assertEquals(type, result.get().getKey());
        assertEquals(value, result.get().getValue().getUnderlyingObject());
    }
}
