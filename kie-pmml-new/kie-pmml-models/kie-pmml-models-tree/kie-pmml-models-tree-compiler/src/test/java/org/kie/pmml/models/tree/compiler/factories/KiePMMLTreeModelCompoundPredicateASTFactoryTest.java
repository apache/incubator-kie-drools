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

package org.kie.pmml.models.tree.compiler.factories;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataType;
import org.dmg.pmml.SimplePredicate;
import org.junit.Test;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.kie.pmml.models.drooled.tuples.KiePMMLOperatorValue;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.STATUS_PATTERN;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.SURROGATE_PATTERN;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTTestUtils.getSimplePredicate;

public class KiePMMLTreeModelCompoundPredicateASTFactoryTest {

    @Test
    public void declareRuleFromCompoundPredicateAndOrXorFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<SimplePredicate> predicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate("SIMPLEPREDICATE-" + index, DataType.STRING, "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String result = "RESULT";
        for (CompoundPredicate.BooleanOperator operator : CompoundPredicate.BooleanOperator.values()) {
            if (operator.equals(CompoundPredicate.BooleanOperator.SURROGATE)) {
                continue;
            }
            CompoundPredicate compoundPredicate = new CompoundPredicate();
            compoundPredicate.setBooleanOperator(operator);
            predicates.forEach(compoundPredicate::addPredicates);
            final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
            KiePMMLTreeModelCompoundPredicateASTFactory.factory(compoundPredicate, fieldTypeMap, rules).declareRuleFromCompoundPredicateAndOrXor(parentPath, currentRule, result, true);
            assertEquals(1, rules.size());
            final KiePMMLDrooledRule retrieved = rules.poll();
            assertNotNull(retrieved);
            assertEquals(currentRule, retrieved.getName());
            assertEquals(StatusCode.DONE.getName(), retrieved.getStatusToSet());
            assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
            assertEquals(result, retrieved.getResult());
            assertEquals(StatusCode.OK, retrieved.getResultCode());
            Map<String, List<KiePMMLOperatorValue>> constraints = null;
            switch (compoundPredicate.getBooleanOperator()) {
                case AND:
                    constraints = retrieved.getAndConstraints();
                    break;
                case OR:
                    constraints = retrieved.getOrConstraints();
                    break;
                case XOR:
                    constraints = retrieved.getXorConstraints();
                    break;
                default:
                    continue;
            }
            assertNotNull(constraints);
        }
    }

    @Test
    public void declareRuleFromCompoundPredicateAndOrXorNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<SimplePredicate> predicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate("SIMPLEPREDICATE-" + index, DataType.STRING, "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String result = "RESULT";
        for (CompoundPredicate.BooleanOperator operator : CompoundPredicate.BooleanOperator.values()) {
            if (operator.equals(CompoundPredicate.BooleanOperator.SURROGATE)) {
                continue;
            }
            CompoundPredicate compoundPredicate = new CompoundPredicate();
            compoundPredicate.setBooleanOperator(operator);
            predicates.forEach(compoundPredicate::addPredicates);
            final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
            KiePMMLTreeModelCompoundPredicateASTFactory.factory(compoundPredicate, fieldTypeMap, rules).declareRuleFromCompoundPredicateAndOrXor(parentPath, currentRule, result, false);
            assertEquals(1, rules.size());
            final KiePMMLDrooledRule retrieved = rules.poll();
            assertNotNull(retrieved);
            assertEquals(currentRule, retrieved.getName());
            assertEquals(currentRule, retrieved.getStatusToSet());
            assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
            Map<String, List<KiePMMLOperatorValue>> constraints = null;
            switch (compoundPredicate.getBooleanOperator()) {
                case AND:
                    constraints = retrieved.getAndConstraints();
                    break;
                case OR:
                    constraints = retrieved.getOrConstraints();
                    break;
                case XOR:
                    constraints = retrieved.getXorConstraints();
                    break;
                default:
                    continue;
            }
            assertNotNull(constraints);
        }
    }

    @Test
    public void declareRuleFromCompoundPredicateSurrogateFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<SimplePredicate> predicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate("SIMPLEPREDICATE-" + index, DataType.STRING, "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String result = "RESULT";
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.SURROGATE);
        predicates.forEach(compoundPredicate::addPredicates);
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        KiePMMLTreeModelCompoundPredicateASTFactory.factory(compoundPredicate, fieldTypeMap, rules).declareRuleFromCompoundPredicateSurrogate(parentPath, currentRule, result, true);
        assertEquals(predicates.size(), rules.size());
        for (int i = 0; i < predicates.size(); i++) {
            SimplePredicate simplePredicate = predicates.get(i);
            KiePMMLDrooledRule retrieved = rules.poll();
            assertNotNull(retrieved);
            String expectedRule = String.format(SURROGATE_PATTERN, currentRule, fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType());
            assertEquals(expectedRule, retrieved.getName());
            if (i < predicates.size() - 1) {
                assertEquals(currentRule, retrieved.getStatusToSet());
            } else {
                assertEquals(StatusCode.DONE.getName(), retrieved.getStatusToSet());
            }
            assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
            assertEquals(result, retrieved.getResult());
            assertEquals(StatusCode.OK, retrieved.getResultCode());
        }
    }

    @Test
    public void declareRuleFromCompoundPredicateSurrogateNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        List<SimplePredicate> predicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate("SIMPLEPREDICATE-" + index, DataType.STRING, "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String result = "RESULT";
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.SURROGATE);
        predicates.forEach(compoundPredicate::addPredicates);
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        KiePMMLTreeModelCompoundPredicateASTFactory.factory(compoundPredicate, fieldTypeMap, rules).declareRuleFromCompoundPredicateSurrogate(parentPath, currentRule, result, false);
        assertEquals(predicates.size(), rules.size());
        for (int i = 0; i < predicates.size(); i++) {
            SimplePredicate simplePredicate = predicates.get(i);
            KiePMMLDrooledRule retrieved = rules.poll();
            assertNotNull(retrieved);
            String expectedRule = String.format(SURROGATE_PATTERN, currentRule, fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType());
            assertEquals(expectedRule, retrieved.getName());
            if (i < predicates.size() - 1) {
                assertEquals(currentRule, retrieved.getStatusToSet());
            } else {
                assertEquals(parentPath, retrieved.getStatusToSet());
            }
            assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
            assertEquals(result, retrieved.getResult());
            assertEquals(StatusCode.OK, retrieved.getResultCode());
        }
    }

    @Test
    public void getConstraintEntryFromSimplePredicates() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        String fieldName = "FIELD_NAME";
        List<SimplePredicate> simplePredicates = IntStream.range(0, 2).mapToObj(index -> getSimplePredicate(fieldName, DataType.STRING, "VALUE-" + index, fieldTypeMap)).collect(Collectors.toList());
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.SURROGATE);
        simplePredicates.forEach(compoundPredicate::addPredicates);
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        final Map<String, List<KiePMMLOperatorValue>> retrieved = KiePMMLTreeModelCompoundPredicateASTFactory.factory(compoundPredicate, fieldTypeMap, rules).getConstraintEntryFromSimplePredicates(fieldName, simplePredicates);
        assertTrue(retrieved.containsKey(fieldName));
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = retrieved.get(fieldName);
        assertNotNull(kiePMMLOperatorValues);
    }
}