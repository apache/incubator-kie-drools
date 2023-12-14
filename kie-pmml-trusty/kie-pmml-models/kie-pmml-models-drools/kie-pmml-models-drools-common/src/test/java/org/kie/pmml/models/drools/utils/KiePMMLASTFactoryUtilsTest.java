/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static java.util.stream.Collectors.groupingBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomObject;
import static org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils.getRandomSimplePredicateOperator;

public class KiePMMLASTFactoryUtilsTest {

    private static Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;
    private static Map<String, DataType> simplePredicateNameType;
    private static List<SimplePredicate> simplePredicates;
    private static List<CompoundPredicate> compoundPredicates;

    @BeforeAll
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
                                                                 getRandomObject(entry.getValue()),
                                                                 getRandomSimplePredicateOperator());
                })
                .collect(Collectors.toList());
        compoundPredicates = IntStream.range(0, 4)
                .mapToObj( index -> PMMLModelTestUtils.getCompoundPredicate(simplePredicates, index))
                .collect(Collectors.toList());
    }

    @Test
    void getConstraintEntriesFromXOrCompoundPredicate() {
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.XOR);
        List<Predicate> predicates =
                IntStream.range(0, 2).mapToObj(index -> simplePredicates.get(index)).collect(Collectors.toList());
        compoundPredicate.getPredicates().addAll(predicates);
        List<KiePMMLFieldOperatorValue> retrieved = KiePMMLASTFactoryUtils
                .getConstraintEntriesFromXOrCompoundPredicate(compoundPredicate, fieldTypeMap);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(predicates);
        commonVerifyKiePMMLFieldOperatorValueList(retrieved, null);
    }

    @Test
    void getConstraintEntriesFromXOrCompoundPredicateWrongSize() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            CompoundPredicate compoundPredicate = new CompoundPredicate();
            compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.XOR);
            compoundPredicate.getPredicates().addAll(simplePredicates);
            KiePMMLASTFactoryUtils.getConstraintEntriesFromXOrCompoundPredicate(compoundPredicate, fieldTypeMap);
        });
    }

    @Test
    void getConstraintEntriesFromXOrCompoundPredicateWrongOperator() {
        assertThatExceptionOfType(KiePMMLException.class).isThrownBy(() -> {
            CompoundPredicate compoundPredicate = new CompoundPredicate();
            compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.AND);
            KiePMMLASTFactoryUtils.getConstraintEntriesFromXOrCompoundPredicate(compoundPredicate, fieldTypeMap);
        });
    }

    @Test
    void getConstraintEntryFromSimplePredicates() {
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
        assertThat(retrieved.getName()).isEqualTo(fieldName);
        assertThat(retrieved.getConstraintsAsString()).isNotNull();
        String expected = "value < \"VALUE-0\" || value < \"VALUE-1\"";
        assertThat(retrieved.getConstraintsAsString()).isEqualTo(expected);
    }

    @Test
    void getXORConstraintEntryFromSimplePredicates() {
        List<Predicate> predicates = new ArrayList<>(simplePredicates);
        List<KiePMMLFieldOperatorValue> retrieved = KiePMMLASTFactoryUtils
                .getXORConstraintEntryFromSimplePredicates(predicates, fieldTypeMap);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved).hasSameSizeAs(simplePredicates);
        commonVerifyKiePMMLFieldOperatorValueList(retrieved, null);
    }

    @Test
    void getCorrectlyFormattedObject() {
        simplePredicates.forEach(simplePredicate -> {
            Object retrieved = KiePMMLASTFactoryUtils.getCorrectlyFormattedObject(simplePredicate, fieldTypeMap);
            Object expected = simplePredicate.getValue();
            if (expected instanceof String) {
                expected = String.format("\"%s\"", expected);
            }
            assertThat(retrieved).isEqualTo(expected);
        });
    }

    @Test
    void populateKiePMMLFieldOperatorValueListWithSimplePredicatesWithAnd() {
        commonPopulateKiePMMLFieldOperatorValueListWithSimplePredicates(CompoundPredicate.BooleanOperator.AND,
                BOOLEAN_OPERATOR.AND);
    }

    @Test
    void populateKiePMMLFieldOperatorValueListWithSimplePredicatesWithOr() {
        commonPopulateKiePMMLFieldOperatorValueListWithSimplePredicates(CompoundPredicate.BooleanOperator.OR,
                BOOLEAN_OPERATOR.OR);
    }

    @Test
    void populateKiePMMLFieldOperatorValueListWithCompoundPredicates() {
        final List<KiePMMLFieldOperatorValue> toPopulate = new ArrayList<>();
        KiePMMLASTFactoryUtils.populateKiePMMLFieldOperatorValueListWithCompoundPredicates(toPopulate,
                compoundPredicates,
                fieldTypeMap);
        assertThat(toPopulate).isNotEmpty();
        assertThat(toPopulate).hasSize(2); // one entry is for "AND" compounds and the other is for "OR" ones
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
            assertThat(nestedKiePMMLFieldOperatorValues).hasSameSizeAs(nestedPredicates);
            nestedKiePMMLFieldOperatorValues.forEach(new Consumer<KiePMMLFieldOperatorValue>() {
                @Override
                public void accept(KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue) {
                    assertThat(kiePMMLFieldOperatorValue.getKiePMMLOperatorValues()).hasSize(1);
                    final KiePMMLOperatorValue kiePMMLOperatorValue =
                            kiePMMLFieldOperatorValue.getKiePMMLOperatorValues().get(0);
                    SimplePredicate simplePredicate = nestedPredicates.stream()
                            .map(predicate -> (SimplePredicate) predicate)
                            .filter(predicate -> predicate.getField().equals(getOriginalPredicateName(kiePMMLFieldOperatorValue.getName())))
                            .findFirst()
                            .orElseThrow(() -> new RuntimeException("Failed to find SimplePredicate for " + kiePMMLFieldOperatorValue.getName()));
                    commonVerifyKiePMMLOperatorValue(kiePMMLOperatorValue, simplePredicate);
                    nestedPredicates.remove(simplePredicate);
                }
            });
            assertThat(nestedPredicates).isEmpty();
        });
    }

    private void commonPopulateKiePMMLFieldOperatorValueListWithSimplePredicates(CompoundPredicate.BooleanOperator compoundBooleanOperator, BOOLEAN_OPERATOR booleanOperator) {
        final Map<String, List<SimplePredicate>> predicatesByField = simplePredicates.stream()
                .collect(groupingBy(child -> fieldTypeMap.get(child.getField()).getGeneratedType()));
        final List<KiePMMLFieldOperatorValue> toPopulate = new ArrayList<>();
        KiePMMLASTFactoryUtils.populateKiePMMLFieldOperatorValueListWithSimplePredicates(toPopulate,
                                                                                         compoundBooleanOperator,
                                                                                         predicatesByField,
                                                                                         fieldTypeMap);
        assertThat(toPopulate).hasSameSizeAs(simplePredicates);
        commonVerifyKiePMMLFieldOperatorValueList(toPopulate, booleanOperator);
    }

    private void commonVerifyKiePMMLFieldOperatorValueList(List<KiePMMLFieldOperatorValue> toVerify,
                                                           BOOLEAN_OPERATOR booleanOperator) {
        toVerify.forEach(kiePMMLFieldOperatorValue -> {
            assertThat(kiePMMLFieldOperatorValue.getOperator()).isEqualTo(booleanOperator);
            commonVerifyKiePMMLFieldOperatorValue(kiePMMLFieldOperatorValue);
        });
    }

    private void commonVerifyKiePMMLFieldOperatorValue(final KiePMMLFieldOperatorValue toVerify) {
        final SimplePredicate simplePredicate = getSimplePredicate(toVerify.getName());
        commonVerifyKiePMMLFieldOperatorValue(toVerify, simplePredicate);
    }

    private void commonVerifyKiePMMLFieldOperatorValue(final KiePMMLFieldOperatorValue toVerify,
                                                       final SimplePredicate simplePredicate) {
        assertThat(toVerify.getKiePMMLOperatorValues()).hasSize(1);
        final KiePMMLOperatorValue kiePMMLOperatorValue = toVerify.getKiePMMLOperatorValues().get(0);
        commonVerifyKiePMMLOperatorValue(kiePMMLOperatorValue, simplePredicate);
        Object expectedValue = simplePredicate.getValue();
        if (expectedValue instanceof String) {
            expectedValue = String.format("\"%s\"", expectedValue);
        }
        String expectedOperator = OPERATOR.byName(simplePredicate.getOperator().value()).getOperator();
        String expected = String.format("value %s %s", expectedOperator, expectedValue);
        assertThat(toVerify.getConstraintsAsString()).isEqualTo(expected);
    }

    private void commonVerifyKiePMMLOperatorValue(final KiePMMLOperatorValue toVerify,
                                                  final SimplePredicate simplePredicate) {
        assertThat(toVerify.getOperator()).isNotNull();
        Object expectedValue = simplePredicate.getValue();
        if (expectedValue instanceof String) {
            expectedValue = String.format("\"%s\"", expectedValue);
        }
        assertThat(toVerify.getValue()).isEqualTo(expectedValue);
    }

    private SimplePredicate getSimplePredicate(String generatedType) {
        String predicateName = getOriginalPredicateName(generatedType);
        return simplePredicates.stream()
                .filter(simplePredicate -> simplePredicate.getField().equals(predicateName))
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