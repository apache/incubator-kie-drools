/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.models.drools.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataType;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static java.util.stream.Collectors.groupingBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomSimplePredicateOperator;
import static org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils.getRandomValue;

public class KiePMMLASTFactoryUtilsTest {

    private static Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;
    private static Map<String, DataType> simplePredicateNameType;
    private static List<SimplePredicate> simplePredicates;
    private static List<CompoundPredicate> compoundPredicates;

    @BeforeClass
    public static void setup() {
        fieldTypeMap = new HashMap<>();
        simplePredicateNameType = new HashMap<>();
        simplePredicateNameType.put("age", DataType.INTEGER);
        simplePredicateNameType.put("weight", DataType.DOUBLE);
        simplePredicateNameType.put("name", DataType.STRING);
        simplePredicateNameType.put("runner", DataType.BOOLEAN);
        simplePredicates = simplePredicateNameType
                .entrySet()
                .stream()
                .map(entry -> {
                    fieldTypeMap.put(entry.getKey(),
                                     new KiePMMLOriginalTypeGeneratedType(entry.getValue().value(),
                                                                          getSanitizedClassName(entry.getKey().toUpperCase())));
                    return PMMLModelTestUtils.getSimplePredicate(entry.getKey(),
                                                                 getRandomValue(entry.getValue()),
                                                                 getRandomSimplePredicateOperator());
                })
                .collect(Collectors.toList());
        compoundPredicates = IntStream.range(0, 4)
                .mapToObj( index -> PMMLModelTestUtils.getCompoundPredicate(simplePredicates, index))
                .collect(Collectors.toList());
    }

    @Test
    public void getConstraintEntriesFromXOrCompoundPredicate() {
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.XOR);
        List<Predicate> predicates =
                IntStream.range(0, 2).mapToObj(index -> simplePredicates.get(index)).collect(Collectors.toList());
        compoundPredicate.getPredicates().addAll(predicates);
        List<KiePMMLFieldOperatorValue> retrieved = KiePMMLASTFactoryUtils
                .getConstraintEntriesFromXOrCompoundPredicate(compoundPredicate, fieldTypeMap);
        assertNotNull(retrieved);
        assertEquals(predicates.size(), retrieved.size());
        commonVerifyKiePMMLFieldOperatorValueList(retrieved, null);
    }

    @Test(expected = KiePMMLException.class)
    public void getConstraintEntriesFromXOrCompoundPredicateWrongSize() {
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.XOR);
        compoundPredicate.getPredicates().addAll(simplePredicates);
        KiePMMLASTFactoryUtils.getConstraintEntriesFromXOrCompoundPredicate(compoundPredicate, fieldTypeMap);
    }

    @Test(expected = KiePMMLException.class)
    public void getConstraintEntriesFromXOrCompoundPredicateWrongOperator() {
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.AND);
        KiePMMLASTFactoryUtils.getConstraintEntriesFromXOrCompoundPredicate(compoundPredicate, fieldTypeMap);
    }

    @Test
    public void getConstraintEntryFromSimplePredicates() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        String fieldName = "FIELD_NAME";
        List<SimplePredicate> simplePredicates = IntStream.range(0, 2)
                .mapToObj(index -> {
                    fieldTypeMap.put(fieldName,
                                     new KiePMMLOriginalTypeGeneratedType(DataType.STRING.value(),
                                                                          getSanitizedClassName(fieldName.toUpperCase())));
                    return PMMLModelTestUtils
                            .getSimplePredicate(fieldName, "VALUE-" + index, SimplePredicate.Operator.LESS_THAN);
                })
                .collect(Collectors.toList());
        final KiePMMLFieldOperatorValue retrieved = KiePMMLASTFactoryUtils
                .getConstraintEntryFromSimplePredicates(fieldName, BOOLEAN_OPERATOR.OR, simplePredicates, fieldTypeMap);
        assertEquals(fieldName, retrieved.getName());
        assertNotNull(retrieved.getConstraintsAsString());
        String expected = "value < \"VALUE-0\" || value < \"VALUE-1\"";
        assertEquals(expected, retrieved.getConstraintsAsString());
    }

    @Test
    public void getXORConstraintEntryFromSimplePredicates() {
        List<Predicate> predicates = new ArrayList<>(simplePredicates);
        List<KiePMMLFieldOperatorValue> retrieved = KiePMMLASTFactoryUtils
                .getXORConstraintEntryFromSimplePredicates(predicates, fieldTypeMap);
        assertNotNull(retrieved);
        assertEquals(simplePredicates.size(), retrieved.size());
        commonVerifyKiePMMLFieldOperatorValueList(retrieved, null);
    }

    @Test
    public void getCorrectlyFormattedObject() {
        simplePredicates.forEach(simplePredicate -> {
            Object retrieved = KiePMMLASTFactoryUtils.getCorrectlyFormattedObject(simplePredicate, fieldTypeMap);
            Object expected = simplePredicate.getValue();
            if (expected instanceof String) {
                expected = String.format("\"%s\"", expected);
            }
            assertEquals(expected, retrieved);
        });
    }

    @Test
    public void populateKiePMMLFieldOperatorValueListWithSimplePredicatesWithAnd() {
        commonPopulateKiePMMLFieldOperatorValueListWithSimplePredicates(CompoundPredicate.BooleanOperator.AND,
                                                                        BOOLEAN_OPERATOR.AND);
    }

    @Test
    public void populateKiePMMLFieldOperatorValueListWithSimplePredicatesWithOr() {
        commonPopulateKiePMMLFieldOperatorValueListWithSimplePredicates(CompoundPredicate.BooleanOperator.OR,
                                                                        BOOLEAN_OPERATOR.OR);
    }

    @Test
    public void populateKiePMMLFieldOperatorValueListWithCompoundPredicates() {
        final List<KiePMMLFieldOperatorValue> toPopulate = new ArrayList<>();
        KiePMMLASTFactoryUtils.populateKiePMMLFieldOperatorValueListWithCompoundPredicates(toPopulate,
                                                                                           compoundPredicates,
                                                                                           fieldTypeMap);
        assertFalse(toPopulate.isEmpty());
        assertEquals(2, toPopulate.size()); // one entry is for "AND" compounds and the other is for "OR" ones
        final Map<CompoundPredicate.BooleanOperator, List<CompoundPredicate>> partitionedCompoundPredicates =
                compoundPredicates.stream()
                .collect(Collectors.groupingBy(CompoundPredicate::getBooleanOperator));
        partitionedCompoundPredicates.forEach((booleanOperator, compoundPredicates) -> {
            final KiePMMLFieldOperatorValue operatorValue = toPopulate.stream()
                    .filter(kiePMMLFieldOperatorValue -> kiePMMLFieldOperatorValue.getOperator().equals(BOOLEAN_OPERATOR.byName(booleanOperator.value())))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Failed toRetrieve KiePMMLFieldOperatorValue for " +
                                                                    "BooleanOperator " + booleanOperator));
            final List<KiePMMLFieldOperatorValue> nestedKiePMMLFieldOperatorValues =
                    operatorValue.getNestedKiePMMLFieldOperatorValues();
            final List<Predicate> nestedPredicates =
                    compoundPredicates.stream().flatMap(compoundPredicate -> compoundPredicate.getPredicates().stream()).collect(Collectors.toList());
            assertEquals(nestedPredicates.size(), nestedKiePMMLFieldOperatorValues.size());
            nestedKiePMMLFieldOperatorValues.forEach(new Consumer<KiePMMLFieldOperatorValue>() {
                @Override
                public void accept(KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue) {
                    assertEquals(1, kiePMMLFieldOperatorValue.getKiePMMLOperatorValues().size());
                    final KiePMMLOperatorValue kiePMMLOperatorValue =
                            kiePMMLFieldOperatorValue.getKiePMMLOperatorValues().get(0);
                    SimplePredicate simplePredicate = nestedPredicates.stream()
                            .map(predicate -> (SimplePredicate) predicate)
                            .filter(predicate -> predicate.getField().getValue().equals(getOriginalPredicateName(kiePMMLFieldOperatorValue.getName())))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Failed to find SimplePredicate for " + kiePMMLFieldOperatorValue.getName()));
                    commonVerifyKiePMMLOperatorValue(kiePMMLOperatorValue, simplePredicate);
                    nestedPredicates.remove(simplePredicate);
                }
            });
            assertTrue(nestedPredicates.isEmpty());
        });
    }

    private void commonPopulateKiePMMLFieldOperatorValueListWithSimplePredicates(CompoundPredicate.BooleanOperator compoundBooleanOperator, BOOLEAN_OPERATOR booleanOperator) {
        final Map<String, List<SimplePredicate>> predicatesByField = simplePredicates.stream()
                .collect(groupingBy(child -> fieldTypeMap.get(child.getField().getValue()).getGeneratedType()));
        final List<KiePMMLFieldOperatorValue> toPopulate = new ArrayList<>();
        KiePMMLASTFactoryUtils.populateKiePMMLFieldOperatorValueListWithSimplePredicates(toPopulate,
                                                                                         compoundBooleanOperator,
                                                                                         predicatesByField,
                                                                                         fieldTypeMap);
        assertEquals(simplePredicates.size(), toPopulate.size());
        commonVerifyKiePMMLFieldOperatorValueList(toPopulate, booleanOperator);
    }

    private void commonVerifyKiePMMLFieldOperatorValueList(List<KiePMMLFieldOperatorValue> toVerify,
                                                           BOOLEAN_OPERATOR booleanOperator) {
        toVerify.forEach(kiePMMLFieldOperatorValue -> {
            assertEquals(booleanOperator, kiePMMLFieldOperatorValue.getOperator());
            commonVerifyKiePMMLFieldOperatorValue(kiePMMLFieldOperatorValue);
        });
    }

    private void commonVerifyKiePMMLFieldOperatorValue(final KiePMMLFieldOperatorValue toVerify) {
        final SimplePredicate simplePredicate = getSimplePredicate(toVerify.getName());
        commonVerifyKiePMMLFieldOperatorValue(toVerify, simplePredicate);
    }

    private void commonVerifyKiePMMLFieldOperatorValue(final KiePMMLFieldOperatorValue toVerify,
                                                       final SimplePredicate simplePredicate) {
        assertEquals(1, toVerify.getKiePMMLOperatorValues().size());
        final KiePMMLOperatorValue kiePMMLOperatorValue = toVerify.getKiePMMLOperatorValues().get(0);
        commonVerifyKiePMMLOperatorValue(kiePMMLOperatorValue, simplePredicate);
        Object expectedValue = simplePredicate.getValue();
        if (expectedValue instanceof String) {
            expectedValue = String.format("\"%s\"", expectedValue);
        }
        String expectedOperator = OPERATOR.byName(simplePredicate.getOperator().value()).getOperator();
        String expected = String.format("value %s %s", expectedOperator, expectedValue);
        assertEquals(expected, toVerify.getConstraintsAsString());
    }

    private void commonVerifyKiePMMLOperatorValue(final KiePMMLOperatorValue toVerify,
                                                  final SimplePredicate simplePredicate) {
        assertNotNull(toVerify.getOperator());
        Object expectedValue = simplePredicate.getValue();
        if (expectedValue instanceof String) {
            expectedValue = String.format("\"%s\"", expectedValue);
        }
        assertEquals(expectedValue, toVerify.getValue());
    }

    private SimplePredicate getSimplePredicate(String generatedType) {
        String predicateName = getOriginalPredicateName(generatedType);
        return simplePredicates.stream()
                .filter(simplePredicate -> simplePredicate.getField().getValue().equals(predicateName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Failed to find SimplePredicate for " + predicateName));
    }

    private String getOriginalPredicateName(String generatedType) {
        return fieldTypeMap.entrySet().stream()
                .filter(entry -> entry.getValue().getGeneratedType().equals(generatedType))
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(() -> new RuntimeException("Failed to find original type for " + generatedType));
    }
}