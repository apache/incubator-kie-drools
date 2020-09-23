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

package org.kie.kogito.trusty.service.responses;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kie.kogito.trusty.service.TrustyServiceTestUtils;
import org.kie.kogito.trusty.storage.api.model.TypedVariable;

class TypedVariableResponseTest {

    private static final String FIELD_COMPONENTS = "components";
    private static final String FIELD_NAME = "name";
    private static final String FIELD_TYPE_REF = "typeRef";
    private static final String FIELD_VALUE = "value";
    private static final String TYPE_REF_NUMBER = "number";
    private static final String TYPE_REF_STRING = "string";
    private static final String TYPE_REF_STRUCT = "StructType";
    private static final String VAR_NAME_COLLECTION = "tstCollection";
    private static final String VAR_NAME_STRUCT = "tstStruct";
    private static final String VAR_NAME_UNIT = "tstUnit";
    private static final String VALUE_AGE = "age";
    private static final String VALUE_COUNT_ONE = "One";
    private static final String VALUE_COUNT_TWO = "Two";
    private static final String VALUE_COUNT_THREE = "Three";
    private static final String VALUE_HELLO_THIS_IS_A_TEST = "hello this is a test";
    private static final String VALUE_MILANO = "Milano";
    private static final String VALUE_MONZA = "Monza";
    private static final String VALUE_TAVULLIA = "Tavullia";
    private static final String VALUE_TOWN = "town";
    private static final int VALUE_34 = 34;
    private static final int VALUE_41 = 41;
    private static final int VALUE_50 = 50;

    @Test
    void testCollection() throws JsonProcessingException {
        TypedVariableResponse response = TypedVariableResponse.from(buildTestCollection());

        Assertions.assertEquals(VAR_NAME_COLLECTION, response.getName());
        Assertions.assertEquals(TYPE_REF_STRING, response.getTypeRef());
        Assertions.assertNotNull(response.getValue());
        Assertions.assertNull(response.getComponents());

        Assertions.assertTrue(response.getValue().isArray());
        Assertions.assertSame(3, response.getValue().size());
        Assertions.assertTrue(response.getValue().get(0).isTextual());
        Assertions.assertEquals(VALUE_COUNT_ONE, response.getValue().get(0).textValue());
        Assertions.assertTrue(response.getValue().get(1).isTextual());
        Assertions.assertEquals(VALUE_COUNT_TWO, response.getValue().get(1).textValue());
        Assertions.assertTrue(response.getValue().get(2).isTextual());
        Assertions.assertEquals(VALUE_COUNT_THREE, response.getValue().get(2).textValue());
    }

    @Test
    void testCollectionOfStructures() throws JsonProcessingException {
        TypedVariableResponse response = TypedVariableResponse.from(buildTestCollectionOfStructures());

        Assertions.assertEquals(VAR_NAME_COLLECTION, response.getName());
        Assertions.assertEquals(TYPE_REF_STRUCT, response.getTypeRef());
        Assertions.assertNull(response.getValue());
        Assertions.assertNotNull(response.getComponents());

        Assertions.assertSame(2, response.getComponents().size());

        JsonNode firstComponent = response.getComponents().get(0);
        Assertions.assertTrue(firstComponent.isArray());
        Assertions.assertSame(2, firstComponent.size());
        Assertions.assertTrue(firstComponent.get(0).isObject());
        Assertions.assertTrue(firstComponent.get(0).get(FIELD_NAME).isTextual());
        Assertions.assertEquals(VALUE_AGE, firstComponent.get(0).get(FIELD_NAME).textValue());
        Assertions.assertTrue(firstComponent.get(0).get(FIELD_TYPE_REF).isTextual());
        Assertions.assertEquals(TYPE_REF_NUMBER, firstComponent.get(0).get(FIELD_TYPE_REF).textValue());
        Assertions.assertTrue(firstComponent.get(0).get(FIELD_VALUE).isInt());
        Assertions.assertSame(VALUE_34, firstComponent.get(0).get(FIELD_VALUE).intValue());
        Assertions.assertTrue(firstComponent.get(0).get(FIELD_COMPONENTS).isNull());
        Assertions.assertTrue(firstComponent.get(1).isObject());
        Assertions.assertTrue(firstComponent.get(1).get(FIELD_NAME).isTextual());
        Assertions.assertEquals(VALUE_TOWN, firstComponent.get(1).get(FIELD_NAME).textValue());
        Assertions.assertTrue(firstComponent.get(1).get(FIELD_TYPE_REF).isTextual());
        Assertions.assertEquals(TYPE_REF_STRING, firstComponent.get(1).get(FIELD_TYPE_REF).textValue());
        Assertions.assertTrue(firstComponent.get(1).get(FIELD_VALUE).isTextual());
        Assertions.assertEquals(VALUE_MONZA, firstComponent.get(1).get(FIELD_VALUE).textValue());
        Assertions.assertTrue(firstComponent.get(1).get(FIELD_COMPONENTS).isNull());

        JsonNode secondComponent = response.getComponents().get(1);
        Assertions.assertTrue(secondComponent.isArray());
        Assertions.assertSame(2, secondComponent.size());
        Assertions.assertTrue(secondComponent.get(0).isObject());
        Assertions.assertTrue(secondComponent.get(0).get(FIELD_NAME).isTextual());
        Assertions.assertEquals(VALUE_AGE, secondComponent.get(0).get(FIELD_NAME).textValue());
        Assertions.assertTrue(secondComponent.get(0).get(FIELD_TYPE_REF).isTextual());
        Assertions.assertEquals(TYPE_REF_NUMBER, secondComponent.get(0).get(FIELD_TYPE_REF).textValue());
        Assertions.assertTrue(secondComponent.get(0).get(FIELD_VALUE).isInt());
        Assertions.assertSame(VALUE_41, secondComponent.get(0).get(FIELD_VALUE).intValue());
        Assertions.assertTrue(secondComponent.get(0).get(FIELD_COMPONENTS).isNull());
        Assertions.assertTrue(secondComponent.get(1).isObject());
        Assertions.assertTrue(secondComponent.get(1).get(FIELD_NAME).isTextual());
        Assertions.assertEquals(VALUE_TOWN, secondComponent.get(1).get(FIELD_NAME).textValue());
        Assertions.assertTrue(secondComponent.get(1).get(FIELD_TYPE_REF).isTextual());
        Assertions.assertEquals(TYPE_REF_STRING, secondComponent.get(1).get(FIELD_TYPE_REF).textValue());
        Assertions.assertTrue(secondComponent.get(1).get(FIELD_VALUE).isTextual());
        Assertions.assertEquals(VALUE_TAVULLIA, secondComponent.get(1).get(FIELD_VALUE).textValue());
        Assertions.assertTrue(secondComponent.get(1).get(FIELD_COMPONENTS).isNull());
    }

    @Test
    void testStructure() throws JsonProcessingException {
        TypedVariableResponse response = TypedVariableResponse.from(buildTestStructure());

        Assertions.assertEquals(VAR_NAME_STRUCT, response.getName());
        Assertions.assertEquals(TYPE_REF_STRUCT, response.getTypeRef());
        Assertions.assertNull(response.getValue());
        Assertions.assertNotNull(response.getComponents());

        Assertions.assertSame(2, response.getComponents().size());

        JsonNode firstComponent = response.getComponents().get(0);
        Assertions.assertTrue(firstComponent.isObject());
        Assertions.assertTrue(firstComponent.get(FIELD_NAME).isTextual());
        Assertions.assertEquals(VALUE_AGE, firstComponent.get(FIELD_NAME).textValue());
        Assertions.assertTrue(firstComponent.get(FIELD_TYPE_REF).isTextual());
        Assertions.assertEquals(TYPE_REF_NUMBER, firstComponent.get(FIELD_TYPE_REF).textValue());
        Assertions.assertTrue(firstComponent.get(FIELD_VALUE).isInt());
        Assertions.assertSame(VALUE_50, firstComponent.get(FIELD_VALUE).intValue());
        Assertions.assertTrue(firstComponent.get(FIELD_COMPONENTS).isNull());

        JsonNode secondComponent = response.getComponents().get(1);
        Assertions.assertTrue(secondComponent.isObject());
        Assertions.assertTrue(secondComponent.get(FIELD_NAME).isTextual());
        Assertions.assertEquals(VALUE_TOWN, secondComponent.get(FIELD_NAME).textValue());
        Assertions.assertTrue(secondComponent.get(FIELD_TYPE_REF).isTextual());
        Assertions.assertEquals(TYPE_REF_STRING, secondComponent.get(FIELD_TYPE_REF).textValue());
        Assertions.assertTrue(secondComponent.get(FIELD_VALUE).isTextual());
        Assertions.assertEquals(VALUE_MILANO, secondComponent.get(FIELD_VALUE).textValue());
        Assertions.assertTrue(secondComponent.get(FIELD_COMPONENTS).isNull());
    }

    @Test
    void testUnit() throws JsonProcessingException {
        TypedVariableResponse response = TypedVariableResponse.from(buildTestUnitVariable());

        Assertions.assertEquals(VAR_NAME_UNIT, response.getName());
        Assertions.assertEquals(TYPE_REF_STRING, response.getTypeRef());
        Assertions.assertNotNull(response.getValue());
        Assertions.assertNull(response.getComponents());

        Assertions.assertTrue(response.getValue().isTextual());
        Assertions.assertEquals(VALUE_HELLO_THIS_IS_A_TEST, response.getValue().textValue());
    }

    private static TypedVariable buildTestCollection() throws JsonProcessingException {
        return TypedVariable.buildCollection(VAR_NAME_COLLECTION, TYPE_REF_STRING, List.of(
                buildTestUnitVariable(null, TYPE_REF_STRING, "\"" + VALUE_COUNT_ONE + "\""),
                buildTestUnitVariable(null, TYPE_REF_STRING, "\"" + VALUE_COUNT_TWO + "\""),
                buildTestUnitVariable(null, TYPE_REF_STRING, "\"" + VALUE_COUNT_THREE + "\"")
        ));
    }

    private static TypedVariable buildTestCollectionOfStructures() throws JsonProcessingException {
        return TypedVariable.buildCollection(VAR_NAME_COLLECTION, TYPE_REF_STRUCT, List.of(
                TypedVariable.buildStructure(null, TYPE_REF_STRUCT, List.of(
                        buildTestUnitVariable(VALUE_AGE, TYPE_REF_NUMBER, "" + VALUE_34),
                        buildTestUnitVariable(VALUE_TOWN, TYPE_REF_STRING, "\"" + VALUE_MONZA + "\"")
                )),
                TypedVariable.buildStructure(null, TYPE_REF_STRUCT, List.of(
                        buildTestUnitVariable(VALUE_AGE, TYPE_REF_NUMBER, "" + VALUE_41),
                        buildTestUnitVariable(VALUE_TOWN, TYPE_REF_STRING, "\"" + VALUE_TAVULLIA + "\"")
                ))
        ));
    }

    private static TypedVariable buildTestStructure() throws JsonProcessingException {
        return TypedVariable.buildStructure(VAR_NAME_STRUCT, TYPE_REF_STRUCT, List.of(
                buildTestUnitVariable(VALUE_AGE, TYPE_REF_NUMBER, "" + VALUE_50),
                buildTestUnitVariable(VALUE_TOWN, TYPE_REF_STRING, "\"" + VALUE_MILANO + "\"")
        ));
    }

    private static TypedVariable buildTestUnitVariable() throws JsonProcessingException {
        return buildTestUnitVariable(VAR_NAME_UNIT, TYPE_REF_STRING, "\"" + VALUE_HELLO_THIS_IS_A_TEST + "\"");
    }

    private static TypedVariable buildTestUnitVariable(String name, String typeRef, String jsonValue) throws JsonProcessingException {
        return TypedVariable.buildUnit(name, typeRef, TrustyServiceTestUtils.MAPPER.readTree(jsonValue));
    }
}
