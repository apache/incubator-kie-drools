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
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.SURROGATE_RULENAME_PATTERN;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTTestUtils.getSimplePredicate;

public class KiePMMLTreeModelSimplePredicateASTFactoryTest {

    @Test
    public void declareRuleFromSimplePredicateSurrogateFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate simplePredicate = getSimplePredicate("outlook", DataType.STRING, "VALUE", fieldTypeMap);
        String currentRule = "_will play_will play";
        String agendaActivationGroup = "_will play_will play Group";
        String result = "RESULT";
        String parentPath = "parentPath";
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        KiePMMLTreeModelSimplePredicateASTFactory.factory(simplePredicate, fieldTypeMap, rules).declareRuleFromSimplePredicateSurrogate(parentPath, currentRule, agendaActivationGroup, result, true);
        assertEquals(2, rules.size());
        // This is the "TRUE" matching rule
        KiePMMLDrooledRule retrieved = rules.poll();
        assertNotNull(retrieved);
        String baseExpectedRule = String.format(SURROGATE_RULENAME_PATTERN, currentRule, fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType());
        String expectedRule = baseExpectedRule + "_TRUE";
        assertEquals(expectedRule, retrieved.getName());
        assertEquals(StatusCode.DONE.getName(), retrieved.getStatusToSet());
        assertNull(retrieved.getStatusConstraint());
        assertEquals(agendaActivationGroup, retrieved.getAgendaGroup());
        assertEquals(agendaActivationGroup, retrieved.getActivationGroup());
        assertNull(retrieved.getIfBreakField());
        assertNull(retrieved.getIfBreakOperator());
        assertNull(retrieved.getIfBreakValue());
        assertNull(retrieved.getNotConstraints());
        assertNotNull(retrieved.getAndConstraints());
        assertEquals(1, retrieved.getAndConstraints().size());
        assertTrue(retrieved.getAndConstraints().containsKey("OUTLOOK"));
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = retrieved.getAndConstraints().get("OUTLOOK");
        assertEquals(1, kiePMMLOperatorValues.size());
        assertEquals("<", kiePMMLOperatorValues.get(0).getOperator());
        assertEquals("\"VALUE\"", kiePMMLOperatorValues.get(0).getValue());
        assertEquals(result, retrieved.getResult());
        assertEquals(StatusCode.OK, retrieved.getResultCode());
        // This is the "FALSE" matching rule
        retrieved = rules.poll();
        assertNotNull(retrieved);
        expectedRule = baseExpectedRule + "_FALSE";
        assertEquals(expectedRule, retrieved.getName());
        assertEquals(parentPath, retrieved.getStatusToSet());
        assertNull(retrieved.getStatusConstraint());
        assertEquals(agendaActivationGroup, retrieved.getAgendaGroup());
        assertEquals(agendaActivationGroup, retrieved.getActivationGroup());
        assertNull(retrieved.getIfBreakField());
        assertNull(retrieved.getIfBreakOperator());
        assertNull(retrieved.getIfBreakValue());
        assertNull(retrieved.getAndConstraints());
        assertNotNull(retrieved.getNotConstraints());
        assertEquals(1, retrieved.getNotConstraints().size());
        assertTrue(retrieved.getNotConstraints().containsKey("OUTLOOK"));
        kiePMMLOperatorValues = retrieved.getNotConstraints().get("OUTLOOK");
        assertEquals(1, kiePMMLOperatorValues.size());
        assertEquals("<", kiePMMLOperatorValues.get(0).getOperator());
        assertEquals("\"VALUE\"", kiePMMLOperatorValues.get(0).getValue());
        assertNull(retrieved.getResult());
        assertNull(retrieved.getResultCode());
    }

    @Test
    public void declareRuleFromSimplePredicateSurrogateNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate simplePredicate = getSimplePredicate("outlook", DataType.STRING, "VALUE", fieldTypeMap);
        String currentRule = "_will play_will play";
        String agendaActivationGroup = "_will play_will play Group";
        String result = "RESULT";
        String parentPath = "parentPath";
        final Queue<KiePMMLDrooledRule> rules = new LinkedList<>();
        KiePMMLTreeModelSimplePredicateASTFactory.factory(simplePredicate, fieldTypeMap, rules).declareRuleFromSimplePredicateSurrogate(parentPath, currentRule, agendaActivationGroup, result, false);
        assertEquals(2, rules.size());
        // This is the "TRUE" matching rule
        KiePMMLDrooledRule retrieved = rules.poll();
        assertNotNull(retrieved);
        String baseExpectedRule = String.format(SURROGATE_RULENAME_PATTERN, currentRule, fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType());
        String expectedRule = baseExpectedRule + "_TRUE";
        assertEquals(expectedRule, retrieved.getName());
        assertEquals(currentRule, retrieved.getStatusToSet());
        assertNull(retrieved.getStatusConstraint());
        assertEquals(agendaActivationGroup, retrieved.getAgendaGroup());
        assertEquals(agendaActivationGroup, retrieved.getActivationGroup());
        assertNull(retrieved.getIfBreakField());
        assertNull(retrieved.getIfBreakOperator());
        assertNull(retrieved.getIfBreakValue());
        assertNotNull(retrieved.getAndConstraints());
        assertEquals(1, retrieved.getAndConstraints().size());
        assertTrue(retrieved.getAndConstraints().containsKey("OUTLOOK"));
        List<KiePMMLOperatorValue> kiePMMLOperatorValues = retrieved.getAndConstraints().get("OUTLOOK");
        assertEquals(1, kiePMMLOperatorValues.size());
        assertEquals("<", kiePMMLOperatorValues.get(0).getOperator());
        assertEquals("\"VALUE\"", kiePMMLOperatorValues.get(0).getValue());
        assertNull(retrieved.getResult());
        assertNull(retrieved.getResultCode());
        // This is the "FALSE" matching rule
        retrieved = rules.poll();
        assertNotNull(retrieved);
        expectedRule = baseExpectedRule + "_FALSE";
        assertEquals(expectedRule, retrieved.getName());
        assertEquals(parentPath, retrieved.getStatusToSet());
        assertNull(retrieved.getStatusConstraint());
        assertEquals(agendaActivationGroup, retrieved.getAgendaGroup());
        assertEquals(agendaActivationGroup, retrieved.getActivationGroup());
        assertNull(retrieved.getIfBreakField());
        assertNull(retrieved.getIfBreakOperator());
        assertNull(retrieved.getIfBreakValue());
        assertNull(retrieved.getAndConstraints());
        assertNotNull(retrieved.getNotConstraints());
        assertEquals(1, retrieved.getNotConstraints().size());
        assertTrue(retrieved.getNotConstraints().containsKey("OUTLOOK"));
        kiePMMLOperatorValues = retrieved.getNotConstraints().get("OUTLOOK");
        assertEquals(1, kiePMMLOperatorValues.size());
        assertEquals("<", kiePMMLOperatorValues.get(0).getOperator());
        assertEquals("\"VALUE\"", kiePMMLOperatorValues.get(0).getValue());
        assertNull(retrieved.getResult());
        assertNull(retrieved.getResultCode());
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