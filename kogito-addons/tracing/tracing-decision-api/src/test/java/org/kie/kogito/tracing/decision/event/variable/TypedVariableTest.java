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

package org.kie.kogito.tracing.decision.event.variable;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypedVariableTest {

    @Test
    void test() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        List<TypedVariable> variables = Arrays.asList(
                new UnitVariable("number"),
                new CollectionVariable("list"),
                new StructureVariable("tStruct")
        );

        String serializedJson = mapper.writeValueAsString(variables);

        List<TypedVariable> serializedVariables = mapper.readValue(serializedJson, new TypeReference<List<TypedVariable>>() {
        });

        assertNotNull(serializedVariables);
        assertSame(3, serializedVariables.size());

        assertSame(TypedVariable.Kind.UNIT, serializedVariables.get(0).getKind());
        assertTrue(serializedVariables.get(0) instanceof UnitVariable);
        assertTrue(serializedVariables.get(0).isUnit());
        assertNotNull(serializedVariables.get(0).toUnit());

        assertSame(TypedVariable.Kind.COLLECTION, serializedVariables.get(1).getKind());
        assertTrue(serializedVariables.get(1) instanceof CollectionVariable);
        assertTrue(serializedVariables.get(1).isCollection());
        assertNotNull(serializedVariables.get(1).toCollection());

        assertSame(TypedVariable.Kind.STRUCTURE, serializedVariables.get(2).getKind());
        assertTrue(serializedVariables.get(2) instanceof StructureVariable);
        assertTrue(serializedVariables.get(2).isStructure());
        assertNotNull(serializedVariables.get(2).toStructure());
    }

}
