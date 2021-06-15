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

import com.fasterxml.jackson.databind.node.IntNode;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.kie.kogito.trusty.service.common.CounterfactualParameterValidation.isStructureIdentical;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain.buildSearchDomainUnit;
import static org.kie.kogito.trusty.storage.api.model.CounterfactualSearchDomain.buildStructure;
import static org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue.buildStructure;
import static org.kie.kogito.trusty.storage.api.model.TypedVariableWithValue.buildUnit;

public class CounterfactualParameterValidationIdenticalTest {

    @Test
    public void testSearchDomains_NullNull() {
        assertTrue(isStructureIdentical(
                null,
                null));
    }

    @Test
    public void testSearchDomains_EmptyEmpty() {
        assertTrue(isStructureIdentical(
                Collections.emptyList(),
                Collections.emptyList()));
    }

    @Test
    public void testSearchDomains_NullEmpty() {
        assertFalse(isStructureIdentical(
                null,
                Collections.emptyList()));
    }

    @Test
    public void testSearchDomains_EmptyNull() {
        assertFalse(isStructureIdentical(
                Collections.emptyList(),
                null));
    }

    @Test
    public void testSearchDomains_UnitNull() {
        assertFalse(isStructureIdentical(
                List.of(buildUnit("age", "integer", new IntNode(18))),
                null));
    }

    @Test
    public void testSearchDomains_UnitEmpty() {
        assertFalse(isStructureIdentical(
                List.of(buildUnit("age", "integer", new IntNode(18))),
                Collections.emptyList()));
    }

    @Test
    public void testSearchDomains_NullUnit() {
        assertFalse(isStructureIdentical(
                null,
                List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))))));
    }

    @Test
    public void testSearchDomains_EmptyUnit() {
        assertFalse(isStructureIdentical(
                Collections.emptyList(),
                List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))))));
    }

    @Test
    public void testSearchDomains_UnitUnit() {
        assertTrue(isStructureIdentical(
                List.of(buildUnit("age", "integer", new IntNode(18))),
                List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))))));
    }

    @Test
    public void testSearchDomains_UnitUnits() {
        assertFalse(isStructureIdentical(
                List.of(buildUnit("age", "integer", new IntNode(18))),
                List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                        buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))))));
    }

    @Test
    public void testSearchDomains_UnitsUnit() {
        assertFalse(isStructureIdentical(
                List.of(buildUnit("age", "integer", new IntNode(18)),
                        buildUnit("salary", "integer", new IntNode(10000))),
                List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))))));
    }

    @Test
    public void testSearchDomains_UnitsUnits() {
        assertTrue(isStructureIdentical(
                List.of(buildUnit("age", "integer", new IntNode(18)),
                        buildUnit("salary", "integer", new IntNode(10000))),
                List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                        buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))))));
    }

    @Test
    public void testSearchDomains_UnitsUnits__WithDifferentOrder() {
        assertTrue(isStructureIdentical(
                List.of(buildUnit("salary", "integer", new IntNode(10000)),
                        buildUnit("age", "integer", new IntNode(18))),
                List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                        buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))))));
    }

    @Test
    public void testSearchDomains_StructureUnit() {
        assertFalse(isStructureIdentical(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("age", "integer", new IntNode(18)),
                                buildUnit("salary", "integer", new IntNode(10000))))),
                List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))))));
    }

    @Test
    public void testSearchDomains_UnitStructure() {
        assertFalse(isStructureIdentical(
                List.of(buildUnit("age", "integer", new IntNode(18))),
                List.of(buildStructure("person", "tPerson",
                        List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))))))));
    }

    @Test
    public void testSearchDomains_StructureStructure() {
        assertTrue(isStructureIdentical(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("age", "integer", new IntNode(18)),
                                buildUnit("salary", "integer", new IntNode(10000))))),
                List.of(buildStructure("person", "tPerson",
                        List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))))))));
    }

    @Test
    public void testSearchDomains_StructureStructure__WithDifferentOrder() {
        assertTrue(isStructureIdentical(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("salary", "integer", new IntNode(10000)),
                                buildUnit("age", "integer", new IntNode(18))))),
                List.of(buildStructure("person", "tPerson",
                        List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))))))));
    }

    @Test
    public void testSearchDomains_StructureWithStructureStructureWithStructure() {
        assertTrue(isStructureIdentical(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildUnit("age", "integer", new IntNode(18)),
                                buildStructure("income", "tIncome",
                                        List.of(buildUnit("salary", "integer", new IntNode(10000)),
                                                buildUnit("bonuses", "integer", new IntNode(50000))))))),
                List.of(buildStructure("person", "tPerson",
                        List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                buildStructure("income", "tIncome",
                                        List.of(buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))),
                                                buildSearchDomainUnit("bonuses", "integer", new CounterfactualDomainRange(new IntNode(0), new IntNode(500000))))))))));
    }

    @Test
    public void testSearchDomains_ComplexComplex() {
        assertTrue(isStructureIdentical(
                List.of(buildUnit("hatSize", "integer", new IntNode(16)),
                        buildStructure("person", "tPerson",
                                List.of(buildUnit("age", "integer", new IntNode(18)),
                                        buildStructure("income", "tIncome",
                                                List.of(buildUnit("salary", "integer", new IntNode(10000)),
                                                        buildUnit("bonuses", "integer", new IntNode(50000))))))),
                List.of(buildSearchDomainUnit("hatSize", "integer", new CounterfactualDomainRange(new IntNode(12), new IntNode(22))),
                        buildStructure("person", "tPerson",
                                List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                        buildStructure("income", "tIncome",
                                                List.of(buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))),
                                                        buildSearchDomainUnit("bonuses", "integer",
                                                                new CounterfactualDomainRange(new IntNode(0), new IntNode(500000))))))))));
    }

    @Test
    public void testSearchDomains_ComplexComplex__WithDifferentOrder() {
        assertTrue(isStructureIdentical(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildStructure("income", "tIncome",
                                List.of(buildUnit("salary", "integer", new IntNode(10000)),
                                        buildUnit("bonuses", "integer", new IntNode(50000)))),
                                buildUnit("age", "integer", new IntNode(18)))),
                        buildUnit("hatSize", "integer", new IntNode(16))),
                List.of(buildSearchDomainUnit("hatSize", "integer", new CounterfactualDomainRange(new IntNode(12), new IntNode(22))),
                        buildStructure("person", "tPerson",
                                List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                        buildStructure("income", "tIncome",
                                                List.of(buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))),
                                                        buildSearchDomainUnit("bonuses", "integer",
                                                                new CounterfactualDomainRange(new IntNode(0), new IntNode(500000))))))))));
    }

    @Test
    public void testSearchDomains_ComplexComplex__WithDifferentOrder_WithDifference() {
        assertFalse(isStructureIdentical(
                List.of(buildStructure("person", "tPerson",
                        List.of(buildStructure("income", "tIncome",
                                List.of(buildUnit("salary", "integer", new IntNode(10000)))),
                                buildUnit("age", "integer", new IntNode(18)))),
                        buildUnit("hatSize", "integer", new IntNode(16))),
                List.of(buildSearchDomainUnit("hatSize", "integer", new CounterfactualDomainRange(new IntNode(12), new IntNode(22))),
                        buildStructure("person", "tPerson",
                                List.of(buildSearchDomainUnit("age", "integer", new CounterfactualDomainRange(new IntNode(18), new IntNode(65))),
                                        buildStructure("income", "tIncome",
                                                List.of(buildSearchDomainUnit("salary", "integer", new CounterfactualDomainRange(new IntNode(5000), new IntNode(100000))),
                                                        buildSearchDomainUnit("bonuses", "integer",
                                                                new CounterfactualDomainRange(new IntNode(0), new IntNode(500000))))))))));
    }

}
