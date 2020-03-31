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

import org.dmg.pmml.DataType;
import org.dmg.pmml.SimplePredicate;
import org.junit.Test;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.kie.pmml.models.drooled.tuples.KiePMMLOperatorValue;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.pmml.models.tree.model.enums.OPERATOR;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.STATUS_PATTERN;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.SURROGATE_PATTERN;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTTestUtils.getSimplePredicate;

public class KiePMMLTreeModelSimplePredicateASTFactoryTest {

    @Test
    public void declareRuleFromSimplePredicateSurrogate() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate simplePredicate = getSimplePredicate("outlook", DataType.STRING, "VALUE", fieldTypeMap);
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String result = "RESULT";
        String declaredType = fieldTypeMap.get("outlook").getGeneratedType();
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        String statusToSet = StatusCode.DONE.getName();
        KiePMMLTreeModelSimplePredicateASTFactory.factory(simplePredicate, fieldTypeMap, rules).declareRuleFromSimplePredicateSurrogate(parentPath, currentRule, statusToSet, result);
        assertEquals(1, rules.size());
        final KiePMMLDrooledRule retrieved = rules.poll();
        assertNotNull(retrieved);
        String expectedRule = String.format(SURROGATE_PATTERN, currentRule, fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType());
        assertEquals(expectedRule, retrieved.getName());
        assertEquals(statusToSet, retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertEquals(declaredType, retrieved.getIfBreakField());
        assertEquals(OPERATOR.byName(simplePredicate.getOperator().value()).getOperator(), retrieved.getIfBreakOperator());
        assertEquals(simplePredicate.getValue(), retrieved.getIfBreakValue());
        assertNull(retrieved.getAndConstraints());
        assertEquals(result, retrieved.getResult());
        assertEquals(StatusCode.OK, retrieved.getResultCode());
    }

    @Test
    public void declareRuleFromSimplePredicateFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate simplePredicate = getSimplePredicate("outlook", DataType.STRING, "VALUE", fieldTypeMap);
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String declaredType = fieldTypeMap.get("outlook").getGeneratedType();
        String result = "RESULT";
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        KiePMMLTreeModelSimplePredicateASTFactory.factory(simplePredicate, fieldTypeMap, rules).declareRuleFromSimplePredicate(parentPath, currentRule, result, true);
        assertEquals(1, rules.size());
        final KiePMMLDrooledRule retrieved = rules.poll();
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(StatusCode.DONE.getName(), retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertEquals(StatusCode.OK, retrieved.getResultCode());
        assertEquals(result, retrieved.getResult());
        final Map<String, List<KiePMMLOperatorValue>> andConstraints = retrieved.getAndConstraints();
        assertNotNull(andConstraints);
        assertEquals(1, andConstraints.size());
        assertTrue(andConstraints.containsKey(declaredType));
        List<KiePMMLOperatorValue> operatorValues = andConstraints.get(declaredType);
        assertNotNull(operatorValues);
        assertEquals(1, operatorValues.size());
        KiePMMLOperatorValue operatorValue = operatorValues.get(0);
        assertEquals(OPERATOR.byName(simplePredicate.getOperator().value()).getOperator(), operatorValue.getOperator());
        Object expectedValue = simplePredicate.getValue();
        if (fieldTypeMap.get("outlook").getOriginalType().equals("string")) {
            expectedValue = "\"" + expectedValue + "\"";
        }
        assertEquals(expectedValue, operatorValue.getValue());
    }

    @Test
    public void declareIntermediateRuleFromSimplePredicateNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate simplePredicate = getSimplePredicate("outlook", DataType.STRING, "VALUE", fieldTypeMap);
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String declaredType = fieldTypeMap.get("outlook").getGeneratedType();
        String result = "RESULT";
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        KiePMMLTreeModelSimplePredicateASTFactory.factory(simplePredicate, fieldTypeMap, rules).declareRuleFromSimplePredicate(parentPath, currentRule, result, false);
        assertEquals(1, rules.size());
        final KiePMMLDrooledRule retrieved = rules.poll();
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(currentRule, retrieved.getStatusToSet());
        assertEquals(String.format(STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertEquals(currentRule, retrieved.getStatusToSet());
        final Map<String, List<KiePMMLOperatorValue>> andConstraints = retrieved.getAndConstraints();
        assertNotNull(andConstraints);
        assertEquals(1, andConstraints.size());
        assertTrue(andConstraints.containsKey(declaredType));
        List<KiePMMLOperatorValue> operatorValues = andConstraints.get(declaredType);
        assertNotNull(operatorValues);
        assertEquals(1, operatorValues.size());
        KiePMMLOperatorValue operatorValue = operatorValues.get(0);
        assertEquals(OPERATOR.byName(simplePredicate.getOperator().value()).getOperator(), operatorValue.getOperator());
        Object expectedValue = simplePredicate.getValue();
        if (fieldTypeMap.get("outlook").getOriginalType().equals("string")) {
            expectedValue = "\"" + expectedValue + "\"";
        }
        assertEquals(expectedValue, operatorValue.getValue());
    }
}