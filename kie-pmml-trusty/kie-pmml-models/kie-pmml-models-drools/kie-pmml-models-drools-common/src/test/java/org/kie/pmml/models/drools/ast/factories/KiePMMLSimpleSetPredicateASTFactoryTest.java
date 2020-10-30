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

package org.kie.pmml.models.drools.ast.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.Array;
import org.dmg.pmml.SimpleSetPredicate;
import org.junit.Test;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory.STATUS_PATTERN;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getPredicateASTFactoryData;

public class KiePMMLSimpleSetPredicateASTFactoryTest {

    @Test
    public void declareRuleFromSimpleSetPredicateIsInFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<String> values = Arrays.asList("-5", "0.5", "1", "10");
        final SimpleSetPredicate simpleSetPredicate = getSimpleSetPredicate("input1",
                                                                            values,
                                                                            SimpleSetPredicate.BooleanOperator.IS_IN,
                                                                            fieldTypeMap);
        String parentPath = "_classA";
        String currentRule = "_classA_classB";
        String result = "classB";
        String declaredType = fieldTypeMap.get("input1").getGeneratedType();
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(simpleSetPredicate,
                                                                                     Collections.emptyList(),
                                                                                     rules,
                                                                                     parentPath,
                                                                                     currentRule,
                                                                                     fieldTypeMap);
        KiePMMLSimpleSetPredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromSimpleSetPredicate(result, true);
        assertEquals(1, rules.size());
        final KiePMMLDroolsRule retrieved = rules.get(0);
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(DONE, retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertEquals(ResultCode.OK, retrieved.getResultCode());
        assertEquals(result, retrieved.getResult());
        assertNotNull(retrieved.getInConstraints());
        final Map<String, List<Object>> inConstraints = retrieved.getInConstraints();
        assertEquals(1, inConstraints.size());
        assertTrue(inConstraints.containsKey(declaredType));
        final List<Object> retrievedValues = inConstraints.get(declaredType);
        List<String> originalPredicateValues = Arrays.asList(((String) simpleSetPredicate.getArray().getValue()).split(" "));
        assertEquals(originalPredicateValues.size(), retrievedValues.size());
        retrievedValues.forEach(retrievedValue -> {
            assertTrue(originalPredicateValues.contains(retrievedValue));
        });
    }

    @Test
    public void declareRuleFromSimpleSetPredicateIsInNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<String> values = Arrays.asList("-5", "0.5", "1", "10");
        final SimpleSetPredicate simpleSetPredicate = getSimpleSetPredicate("input1",
                                                                            values,
                                                                            SimpleSetPredicate.BooleanOperator.IS_IN,
                                                                            fieldTypeMap);
        String parentPath = "_classA";
        String currentRule = "_classA_classB";
        String result = "classB";
        String declaredType = fieldTypeMap.get("input1").getGeneratedType();
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(simpleSetPredicate,
                                                                                     Collections.emptyList(),
                                                                                     rules,
                                                                                     parentPath,
                                                                                     currentRule,
                                                                                     fieldTypeMap);
        KiePMMLSimpleSetPredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromSimpleSetPredicate(result, false);
        assertEquals(1, rules.size());
        final KiePMMLDroolsRule retrieved = rules.get(0);
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(currentRule, retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertNull(retrieved.getResultCode());
        assertNull(retrieved.getResult());
        assertNotNull(retrieved.getInConstraints());
        final Map<String, List<Object>> inConstraints = retrieved.getInConstraints();
        assertEquals(1, inConstraints.size());
        assertTrue(inConstraints.containsKey(declaredType));
        final List<Object> retrievedValues = inConstraints.get(declaredType);
        List<String> originalPredicateValues = Arrays.asList(((String) simpleSetPredicate.getArray().getValue()).split(" "));
        assertEquals(originalPredicateValues.size(), retrievedValues.size());
        retrievedValues.forEach(retrievedValue -> {
            assertTrue(originalPredicateValues.contains(retrievedValue));
        });
    }

    @Test
    public void declareRuleFromSimpleSetPredicateIsNotInFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<String> values = Arrays.asList("3", "8.5");
        final SimpleSetPredicate simpleSetPredicate = getSimpleSetPredicate("input2",
                                                                            values,
                                                                            SimpleSetPredicate.BooleanOperator.IS_NOT_IN,
                                                                            fieldTypeMap);
        String parentPath = "_classA";
        String currentRule = "_classA_classC";
        String result = "classC";
        String declaredType = fieldTypeMap.get("input2").getGeneratedType();
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        String statusToSet = DONE;
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(simpleSetPredicate,
                                                                                     Collections.emptyList(),
                                                                                     rules,
                                                                                     parentPath,
                                                                                     currentRule,
                                                                                     fieldTypeMap);
        KiePMMLSimpleSetPredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromSimpleSetPredicate(result, true);
        assertEquals(1, rules.size());
        final KiePMMLDroolsRule retrieved = rules.get(0);
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(statusToSet, retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertEquals(ResultCode.OK, retrieved.getResultCode());
        assertEquals(result, retrieved.getResult());
        assertNotNull(retrieved.getNotInConstraints());
        final Map<String, List<Object>> notInConstraints = retrieved.getNotInConstraints();
        assertEquals(1, notInConstraints.size());
        assertTrue(notInConstraints.containsKey(declaredType));
        final List<Object> retrievedValues = notInConstraints.get(declaredType);
        List<String> originalPredicateValues = Arrays.asList(((String) simpleSetPredicate.getArray().getValue()).split(" "));
        assertEquals(originalPredicateValues.size(), retrievedValues.size());
        retrievedValues.forEach(retrievedValue -> {
            assertTrue(originalPredicateValues.contains(retrievedValue));
        });
    }

    @Test
    public void declareRuleFromSimpleSetPredicateIsNotInNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<String> values = Arrays.asList("3", "8.5");
        final SimpleSetPredicate simpleSetPredicate = getSimpleSetPredicate("input2",
                                                                            values,
                                                                            SimpleSetPredicate.BooleanOperator.IS_NOT_IN,
                                                                            fieldTypeMap);
        String parentPath = "_classA";
        String currentRule = "_classA_classC";
        String result = "classC";
        String declaredType = fieldTypeMap.get("input2").getGeneratedType();
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(simpleSetPredicate,
                                                                                     Collections.emptyList(),
                                                                                     rules,
                                                                                     parentPath,
                                                                                     currentRule,
                                                                                     fieldTypeMap);
        KiePMMLSimpleSetPredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromSimpleSetPredicate(result, false);
        assertEquals(1, rules.size());
        final KiePMMLDroolsRule retrieved = rules.get(0);
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(currentRule, retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertNull(retrieved.getResultCode());
        assertNull(retrieved.getResult());
        assertNotNull(retrieved.getNotInConstraints());
        final Map<String, List<Object>> notInConstraints = retrieved.getNotInConstraints();
        assertEquals(1, notInConstraints.size());
        assertTrue(notInConstraints.containsKey(declaredType));
        final List<Object> retrievedValues = notInConstraints.get(declaredType);
        List<String> originalPredicateValues = Arrays.asList(((String) simpleSetPredicate.getArray().getValue()).split(" "));
        assertEquals(originalPredicateValues.size(), retrievedValues.size());
        retrievedValues.forEach(retrievedValue -> {
            assertTrue(originalPredicateValues.contains(retrievedValue));
        });
    }

    private SimpleSetPredicate getSimpleSetPredicate(final String predicateName,
                                                     final List<String> values,
                                                     final SimpleSetPredicate.BooleanOperator booleanOperator,
                                                     final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        fieldTypeMap.put(predicateName,
                         new KiePMMLOriginalTypeGeneratedType(Array.Type.REAL.value(),
                                                              getSanitizedClassName(predicateName.toUpperCase())));
        return PMMLModelTestUtils.getSimpleSetPredicate(predicateName,
                                                        Array.Type.REAL,
                                                        values,
                                                        booleanOperator);
    }
}