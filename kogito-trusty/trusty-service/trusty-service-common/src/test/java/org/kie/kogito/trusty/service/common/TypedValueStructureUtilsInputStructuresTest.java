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
import org.kie.kogito.trusty.storage.api.model.CounterfactualDomainRange;
import org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain;
import org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue;

import com.fasterxml.jackson.databind.node.IntNode;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypedValueStructureUtilsInputStructuresTest {

    @Test
    public void testInputs_NullNull() {
        assertTrue(TypedValueStructureUtils.isStructureIdentical(null, null));
    }

    @Test
    public void testInputs_EmptyEmpty() {
        assertTrue(TypedValueStructureUtils.isStructureIdentical(Collections.<TypedVariableWithValue> emptyList(), Collections.<CounterfactualSearchDomain> emptyList()));
    }

    @Test
    public void testInputs_NullEmpty() {
        assertFalse(TypedValueStructureUtils.isStructureIdentical(null, Collections.<CounterfactualSearchDomain> emptyList()));
    }

    @Test
    public void testInputs_EmptyNull() {
        assertFalse(TypedValueStructureUtils.isStructureIdentical(Collections.<TypedVariableWithValue> emptyList(), null));
    }

    @Test
    public void testInputs_UnitNull() {
        assertFalse(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18))), null));
    }

    @Test
    public void testInputs_UnitEmpty() {
        assertFalse(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18))),
                Collections.<CounterfactualSearchDomain> emptyList()));
    }

    @Test
    public void testInputs_NullUnit() {
        assertFalse(TypedValueStructureUtils.isStructureIdentical(null,
                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))))));
    }

    @Test
    public void testInputs_EmptyUnit() {
        assertFalse(TypedValueStructureUtils.isStructureIdentical(Collections.<TypedVariableWithValue> emptyList(),
                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))))));
    }

    @Test
    public void testInputs_UnitUnit() {
        assertTrue(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18))),
                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))))));
    }

    @Test
    public void testInputs_UnitUnits() {
        assertFalse(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18))),
                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                        CounterfactualSearchDomain.buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))))));
    }

    @Test
    public void testInputs_UnitsUnit() {
        assertFalse(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18)),
                TypedVariableWithValue.buildUnit("salary", "integer", new IntNode(10000))),
                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))))));
    }

    @Test
    public void testInputs_UnitsUnits() {
        assertTrue(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18)),
                TypedVariableWithValue.buildUnit("salary", "integer", new IntNode(10000))),
                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                        CounterfactualSearchDomain.buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))))));
    }

    @Test
    public void testInputs_UnitsUnits__WithDifferentOrder() {
        assertTrue(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildUnit("salary", "integer", new IntNode(10000)),
                TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18))),
                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                        CounterfactualSearchDomain.buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))))));
    }

    @Test
    public void testInputs_StructureUnit() {
        assertFalse(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildStructure("person", "tPerson",
                List.of(TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18)),
                        TypedVariableWithValue.buildUnit("salary", "integer", new IntNode(10000))))),
                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))))));
    }

    @Test
    public void testInputs_UnitStructure() {
        assertFalse(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18))),
                List.of(CounterfactualSearchDomain.buildStructure("person", "tPerson",
                        List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                CounterfactualSearchDomain.buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))))))));
    }

    @Test
    public void testInputs_StructureStructure() {
        assertTrue(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildStructure("person", "tPerson",
                List.of(TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18)),
                        TypedVariableWithValue.buildUnit("salary", "integer", new IntNode(10000))))),
                List.of(CounterfactualSearchDomain.buildStructure("person", "tPerson",
                        List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                CounterfactualSearchDomain.buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))))))));
    }

    @Test
    public void testInputs_StructureStructure__WithDifferentOrder() {
        assertTrue(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildStructure("person", "tPerson",
                List.of(TypedVariableWithValue.buildUnit("salary", "integer", new IntNode(10000)),
                        TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18))))),
                List.of(CounterfactualSearchDomain.buildStructure("person", "tPerson",
                        List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                CounterfactualSearchDomain.buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))))))));
    }

    @Test
    public void testInputs_StructureWithStructureStructureWithStructure() {
        assertTrue(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildStructure("person", "tPerson",
                List.of(TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18)),
                        TypedVariableWithValue.buildStructure("income", "tIncome",
                                List.of(TypedVariableWithValue.buildUnit("salary", "integer", new IntNode(10000)),
                                        TypedVariableWithValue.buildUnit("bonuses", "integer", new IntNode(50000))))))),
                List.of(CounterfactualSearchDomain.buildStructure("person", "tPerson",
                        List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                CounterfactualSearchDomain.buildStructure("income", "tIncome",
                                        List.of(CounterfactualSearchDomain.buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))),
                                                CounterfactualSearchDomain.buildSearchDomainUnit("bonuses", "integer", new CounterfactualDomainRange(new IntNode(0), new IntNode(500000))))))))));
    }

    @Test
    public void testInputs_ComplexComplex() {
        assertTrue(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildUnit("hatSize", "integer", new IntNode(16)),
                TypedVariableWithValue.buildStructure("person", "tPerson",
                        List.of(TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18)),
                                TypedVariableWithValue.buildStructure("income", "tIncome",
                                        List.of(TypedVariableWithValue.buildUnit("salary", "integer", new IntNode(10000)),
                                                TypedVariableWithValue.buildUnit("bonuses", "integer", new IntNode(50000))))))),
                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("hatSize", "integer", new CounterfactualDomainRange(new IntNode(12), new IntNode(22))),
                        CounterfactualSearchDomain.buildStructure("person", "tPerson",
                                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                        CounterfactualSearchDomain.buildStructure("income", "tIncome",
                                                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))),
                                                        CounterfactualSearchDomain.buildSearchDomainUnit("bonuses", "integer",
                                                                new CounterfactualDomainRange(new IntNode(0), new IntNode(500000))))))))));
    }

    @Test
    public void testInputs_ComplexComplex__WithDifferentOrder() {
        assertTrue(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildStructure("person", "tPerson",
                List.of(TypedVariableWithValue.buildStructure("income", "tIncome",
                        List.of(TypedVariableWithValue.buildUnit("salary", "integer", new IntNode(10000)),
                                TypedVariableWithValue.buildUnit("bonuses", "integer", new IntNode(50000)))),
                        TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18)))),
                TypedVariableWithValue.buildUnit("hatSize", "integer", new IntNode(16))),
                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("hatSize", "integer", new CounterfactualDomainRange(new IntNode(12), new IntNode(22))),
                        CounterfactualSearchDomain.buildStructure("person", "tPerson",
                                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                        CounterfactualSearchDomain.buildStructure("income", "tIncome",
                                                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))),
                                                        CounterfactualSearchDomain.buildSearchDomainUnit("bonuses", "integer",
                                                                new CounterfactualDomainRange(new IntNode(0), new IntNode(500000))))))))));
    }

    @Test
    public void testInputs_ComplexComplex__WithDifferentOrder_WithDifference() {
        assertFalse(TypedValueStructureUtils.isStructureIdentical(List.of(TypedVariableWithValue.buildStructure("person", "tPerson",
                List.of(TypedVariableWithValue.buildStructure("income", "tIncome",
                        List.of(TypedVariableWithValue.buildUnit("salary", "integer", new IntNode(10000)))),
                        TypedVariableWithValue.buildUnit("age", "integer", new IntNode(18)))),
                TypedVariableWithValue.buildUnit("hatSize", "integer", new IntNode(16))),
                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("hatSize", "integer", new CounterfactualDomainRange(new IntNode(12), new IntNode(22))),
                        CounterfactualSearchDomain.buildStructure("person", "tPerson",
                                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                        CounterfactualSearchDomain.buildStructure("income", "tIncome",
                                                List.of(CounterfactualSearchDomain.buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))),
                                                        CounterfactualSearchDomain.buildSearchDomainUnit("bonuses", "integer",
                                                                new CounterfactualDomainRange(new IntNode(0), new IntNode(500000))))))))));
    }

}
