/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.kie.kogito.trusty.service.common;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.IntNode;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.trusty.service.common.CounterfactualParameterValidation.isStructureSubset;
import static org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue.buildStructure;
import static org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue.buildUnit;

public class CounterfactualParameterValidationSubsetTest {

    @Test
    public void testGoals_NullNull() {
        assertTrue(isStructureSubset(
                null,
                null));
    }

    @Test
    public void testGoals_EmptyEmpty() {
        assertTrue(isStructureSubset(
                Collections.emptyList(),
                Collections.emptyList()));
    }

    @Test
    public void testGoals_NullEmpty() {
        assertFalse(isStructureSubset(
                null,
                Collections.emptyList()));
    }

    @Test
    public void testGoals_EmptyNull() {
        assertFalse(isStructureSubset(
                Collections.emptyList(),
                null));
    }

    @Test
    public void testGoals_UnitNull() {
        assertFalse(isStructureSubset(
                List.of(buildUnit("age", "integer", new IntNode(18))),
                null));
    }

    @Test
    public void testGoals_UnitEmpty() {
        assertTrue(isStructureSubset(
                List.of(buildUnit("age", "integer", new IntNode(18))),
                Collections.emptyList()));
    }

    @Test
    public void testGoals_NullUnit() {
        assertFalse(isStructureSubset(
                null,
                List.of(buildUnit("age", "integer", new IntNode(18)))));
    }

    @Test
    public void testGoals_EmptyUnit() {
        assertFalse(isStructureSubset(
                Collections.emptyList(),
                List.of(buildUnit("age", "integer", new IntNode(18)))));
    }

    @Test
    public void testGoals_UnitUnit() {
        assertTrue(isStructureSubset(
                List.of(buildUnit("age", "integer", new IntNode(18))),
                List.of(buildUnit("age", "integer", new IntNode(18)))));
    }

    @Test
    public void testGoals_UnitUnits() {
        assertFalse(isStructureSubset(
                List.of(buildUnit("age", "integer", new IntNode(18))),
                List.of(buildUnit("age", "integer", new IntNode(18)),
                        buildUnit("salary", "integer", new IntNode(5000)))));
    }

    @Test
    public void testGoals_UnitsUnit() {
        assertTrue(isStructureSubset(
                List.of(buildUnit("age", "integer", new IntNode(18)),
                        buildUnit("salary", "integer", new IntNode(10000))),
                List.of(buildUnit("age", "integer", new IntNode(18)))));
    }

    @Test
    public void testGoals_UnitsUnits() {
        assertTrue(isStructureSubset(
                List.of(buildUnit("age", "integer", new IntNode(18)),
                        buildUnit("salary", "integer", new IntNode(10000))),
                List.of(buildUnit("age", "integer", new IntNode(18)),
                        buildUnit("salary", "integer", new IntNode(100000)))));
    }

    @Test
    public void testGoals_UnitsUnits__WithDifferentOrder() {
        assertTrue(isStructureSubset(
                List.of(buildUnit("salary", "integer", new IntNode(10000)),
                        buildUnit("age", "integer", new IntNode(18))),
                List.of(buildUnit("age", "integer", new IntNode(18)),
                        buildUnit("salary", "integer", new IntNode(100000)))));
    }

    @Test
    public void testGoals_StructureUnit() {
        assertFalse(isStructureSubset(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("age", "integer", new IntNode(18)),
                                buildUnit("salary", "integer", new IntNode(10000))))),
                List.of(buildUnit("age", "integer", new IntNode(18)))));
    }

    @Test
    public void testGoals_UnitStructure() {
        assertFalse(isStructureSubset(
                List.of(buildUnit("age", "integer", new IntNode(18))),
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("age", "integer", new IntNode(18)),
                                buildUnit("salary", "integer", new IntNode(100000)))))));
    }

    @Test
    public void testGoals_StructureStructure() {
        assertTrue(isStructureSubset(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("age", "integer", new IntNode(18)),
                                buildUnit("salary", "integer", new IntNode(10000))))),
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("age", "integer", new IntNode(18)),
                                buildUnit("salary", "integer", new IntNode(100000)))))));
    }

    @Test
    public void testGoals_StructureStructureSubset() {
        assertTrue(isStructureSubset(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("age", "integer", new IntNode(18)),
                                buildUnit("salary", "integer", new IntNode(10000))))),
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("age", "integer", new IntNode(18)))))));
    }

    @Test
    public void testGoals_StructureStructure__WithDifferentOrder() {
        assertTrue(isStructureSubset(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("salary", "integer", new IntNode(10000)),
                                buildUnit("age", "integer", new IntNode(18))))),
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("age", "integer", new IntNode(18)),
                                buildUnit("salary", "integer", new IntNode(100000)))))));
    }

    @Test
    public void testGoals_StructureWithStructureStructureWithStructure() {
        assertTrue(isStructureSubset(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("age", "integer", new IntNode(18)),
                                buildStructure("income", "tIncome",
                                        List.of(buildUnit("salary", "integer", new IntNode(10000)),
                                                buildUnit("bonuses", "integer", new IntNode(50000))))))),
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("age", "integer", new IntNode(18)),
                                buildStructure("income", "tIncome",
                                        List.of(buildUnit("salary", "integer", new IntNode(100000)),
                                                buildUnit("bonuses", "integer", new IntNode(500000)))))))));
    }

    @Test
    public void testGoals_ComplexComplex() {
        assertTrue(isStructureSubset(
                List.of(buildUnit("hatSize", "integer", new IntNode(16)),
                        buildStructure("person", "tPerson",
                                List.of(buildUnit("age", "integer", new IntNode(18)),
                                        buildStructure("income", "tIncome",
                                                List.of(buildUnit("salary", "integer", new IntNode(10000)),
                                                        buildUnit("bonuses", "integer", new IntNode(50000))))))),
                List.of(buildUnit("hatSize", "integer", new IntNode(12)),
                        buildStructure("person", "tPerson",
                                List.of(buildUnit("age", "integer", new IntNode(18)),
                                        buildStructure("income", "tIncome",
                                                List.of(buildUnit("salary", "integer", new IntNode(100000)),
                                                        buildUnit("bonuses", "integer", new IntNode(500000)))))))));
    }

    @Test
    public void testGoals_ComplexComplex__WithDifferentOrder() {
        assertTrue(isStructureSubset(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildStructure("income", "tIncome",
                                List.of(buildUnit("salary", "integer", new IntNode(10000)),
                                        buildUnit("bonuses", "integer", new IntNode(50000)))),
                                buildUnit("age", "integer", new IntNode(18)))),
                        buildUnit("hatSize", "integer", new IntNode(16))),
                List.of(buildUnit("hatSize", "integer", new IntNode(12)),
                        buildStructure("person", "tPerson",
                                List.of(buildUnit("age", "integer", new IntNode(18)),
                                        buildStructure("income", "tIncome",
                                                List.of(buildUnit("salary", "integer", new IntNode(100000)),
                                                        buildUnit("bonuses", "integer", new IntNode(500000)))))))));
    }

    @Test
    public void testGoals_ComplexComplex__WithDifferentOrder_WithDifference() {
        assertFalse(isStructureSubset(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildStructure("income", "tIncome",
                                List.of(buildUnit("salary", "integer", new IntNode(10000)))),
                                buildUnit("age", "integer", new IntNode(18)))),
                        buildUnit("hatSize", "integer", new IntNode(16))),
                List.of(buildUnit("hatSize", "integer", new IntNode(12)),
                        buildStructure("person", "tPerson",
                                List.of(buildUnit("age", "integer", new IntNode(18)),
                                        buildStructure("income", "tIncome",
                                                List.of(buildUnit("salary", "integer", new IntNode(100000)),
                                                        buildUnit("bonuses", "integer", new IntNode(500000)))))))));
    }

}
