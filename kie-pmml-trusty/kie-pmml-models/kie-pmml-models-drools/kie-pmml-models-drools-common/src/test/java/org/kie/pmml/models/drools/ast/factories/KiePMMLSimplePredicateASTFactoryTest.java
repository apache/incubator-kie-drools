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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.DataType;
import org.dmg.pmml.SimplePredicate;
import org.junit.Test;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.compiler.commons.testutils.PMMLModelTestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getPredicateASTFactoryData;

public class KiePMMLSimplePredicateASTFactoryTest {

    @Test
    public void declareRuleFromSimplePredicateSurrogateFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate simplePredicate = getSimplePredicate("outlook",
                                                             "VALUE",
                                                             SimplePredicate.Operator.LESS_THAN,
                                                             fieldTypeMap);
        String currentRule = "_will play_will play";
        String agendaActivationGroup = "_will play_will play Group";
        String result = "RESULT";
        String parentPath = "parentPath";
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(simplePredicate,
                                                                                     Collections.emptyList(),
                                                                                     rules,
                                                                                     parentPath,
                                                                                     currentRule,
                                                                                     fieldTypeMap);
        KiePMMLSimplePredicateASTFactory.factory(predicateASTFactoryData)
                .declareRuleFromSimplePredicateSurrogate(agendaActivationGroup, result, true);
        assertEquals(2, rules.size());
        // This is the "TRUE" matching rule
        KiePMMLDroolsRule retrieved = rules.get(0);
        assertNotNull(retrieved);
        String baseExpectedRule = String.format(KiePMMLAbstractModelASTFactory.SURROGATE_RULENAME_PATTERN,
                                                currentRule,
                                                fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType());
        String expectedRule = baseExpectedRule + "_TRUE";
        assertEquals(expectedRule, retrieved.getName());
        assertEquals(DONE, retrieved.getStatusToSet());
        assertNull(retrieved.getStatusConstraint());
        assertEquals(agendaActivationGroup, retrieved.getAgendaGroup());
        assertEquals(agendaActivationGroup, retrieved.getActivationGroup());
        assertNull(retrieved.getIfBreakField());
        assertNull(retrieved.getIfBreakOperator());
        assertNull(retrieved.getIfBreakValue());
        assertNull(retrieved.getNotConstraints());
        assertNotNull(retrieved.getAndConstraints());
        assertEquals(1, retrieved.getAndConstraints().size());
        KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = retrieved.getAndConstraints().get(0);
        assertEquals("OUTLOOK", kiePMMLFieldOperatorValue.getName());
        assertEquals(BOOLEAN_OPERATOR.SURROGATE, kiePMMLFieldOperatorValue.getOperator());
        assertEquals("value < \"VALUE\"", kiePMMLFieldOperatorValue.getConstraintsAsString());
        assertEquals(result, retrieved.getResult());
        assertEquals(ResultCode.OK, retrieved.getResultCode());
        // This is the "FALSE" matching rule
        retrieved = rules.get(1);
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
        kiePMMLFieldOperatorValue = retrieved.getNotConstraints().get(0);
        assertEquals("OUTLOOK", kiePMMLFieldOperatorValue.getName());
        assertEquals(BOOLEAN_OPERATOR.SURROGATE, kiePMMLFieldOperatorValue.getOperator());
        assertEquals("value < \"VALUE\"", kiePMMLFieldOperatorValue.getConstraintsAsString());
        assertNull(retrieved.getResult());
        assertNull(retrieved.getResultCode());
    }

    @Test
    public void declareRuleFromSimplePredicateSurrogateNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate simplePredicate = getSimplePredicate("outlook",
                                                             "VALUE",
                                                             SimplePredicate.Operator.LESS_THAN,
                                                             fieldTypeMap);
        String currentRule = "_will play_will play";
        String agendaActivationGroup = "_will play_will play Group";
        String result = "RESULT";
        String parentPath = "parentPath";
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(simplePredicate,
                                                                                     Collections.emptyList(),
                                                                                     rules,
                                                                                     parentPath,
                                                                                     currentRule,
                                                                                     fieldTypeMap);
        KiePMMLSimplePredicateASTFactory.factory(predicateASTFactoryData)
                .declareRuleFromSimplePredicateSurrogate(agendaActivationGroup, result, false);
        assertEquals(2, rules.size());
        // This is the "TRUE" matching rule
        KiePMMLDroolsRule retrieved = rules.get(0);
        assertNotNull(retrieved);
        String baseExpectedRule = String.format(KiePMMLAbstractModelASTFactory.SURROGATE_RULENAME_PATTERN,
                                                currentRule,
                                                fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType());
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
        KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = retrieved.getAndConstraints().get(0);
        assertEquals("OUTLOOK", kiePMMLFieldOperatorValue.getName());
        assertEquals(BOOLEAN_OPERATOR.SURROGATE, kiePMMLFieldOperatorValue.getOperator());
        assertEquals("value < \"VALUE\"", kiePMMLFieldOperatorValue.getConstraintsAsString());
        assertNull(retrieved.getResult());
        assertNull(retrieved.getResultCode());
        // This is the "FALSE" matching rule
        retrieved = rules.get(1);
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
        kiePMMLFieldOperatorValue = retrieved.getNotConstraints().get(0);
        assertEquals("OUTLOOK", kiePMMLFieldOperatorValue.getName());
        assertEquals(BOOLEAN_OPERATOR.SURROGATE, kiePMMLFieldOperatorValue.getOperator());
        assertEquals("value < \"VALUE\"", kiePMMLFieldOperatorValue.getConstraintsAsString());
        assertNull(retrieved.getResult());
        assertNull(retrieved.getResultCode());
    }

    @Test
    public void declareRuleFromSimplePredicateFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate simplePredicate = getSimplePredicate("outlook",
                                                             "VALUE",
                                                             SimplePredicate.Operator.LESS_THAN,
                                                             fieldTypeMap);
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String declaredType = fieldTypeMap.get("outlook").getGeneratedType();
        String result = "RESULT";
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(simplePredicate,
                                                                                     Collections.emptyList(),
                                                                                     rules,
                                                                                     parentPath,
                                                                                     currentRule,
                                                                                     fieldTypeMap);
        KiePMMLSimplePredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromSimplePredicate(result, true);
        assertEquals(1, rules.size());
        final KiePMMLDroolsRule retrieved = rules.get(0);
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(DONE, retrieved.getStatusToSet());
        assertEquals(String.format(KiePMMLAbstractModelASTFactory.STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertEquals(ResultCode.OK, retrieved.getResultCode());
        assertEquals(result, retrieved.getResult());
        final List<KiePMMLFieldOperatorValue> andConstraints = retrieved.getAndConstraints();
        assertNotNull(andConstraints);
        assertEquals(1, andConstraints.size());
        KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = retrieved.getAndConstraints().get(0);
        assertEquals(declaredType, kiePMMLFieldOperatorValue.getName());
        assertEquals(BOOLEAN_OPERATOR.AND, kiePMMLFieldOperatorValue.getOperator());
        String expectedValue = "value < \"" + simplePredicate.getValue() + "\"";
        assertEquals(expectedValue, kiePMMLFieldOperatorValue.getConstraintsAsString());
    }

    @Test
    public void declareIntermediateRuleFromSimplePredicateNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        SimplePredicate simplePredicate = getSimplePredicate("outlook",
                                                             "VALUE",
                                                             SimplePredicate.Operator.LESS_THAN,
                                                             fieldTypeMap);
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String declaredType = fieldTypeMap.get("outlook").getGeneratedType();
        String result = "RESULT";
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(simplePredicate,
                                                                                     Collections.emptyList(),
                                                                                     rules,
                                                                                     parentPath,
                                                                                     currentRule,
                                                                                     fieldTypeMap);
        KiePMMLSimplePredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromSimplePredicate(result, false);
        assertEquals(1, rules.size());
        final KiePMMLDroolsRule retrieved = rules.get(0);
        assertNotNull(retrieved);
        assertEquals(currentRule, retrieved.getName());
        assertEquals(currentRule, retrieved.getStatusToSet());
        assertEquals(String.format(KiePMMLAbstractModelASTFactory.STATUS_PATTERN, parentPath), retrieved.getStatusConstraint());
        assertEquals(currentRule, retrieved.getStatusToSet());
        final List<KiePMMLFieldOperatorValue> andConstraints = retrieved.getAndConstraints();
        assertNotNull(andConstraints);
        assertEquals(1, andConstraints.size());
        KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = retrieved.getAndConstraints().get(0);
        assertEquals(declaredType, kiePMMLFieldOperatorValue.getName());
        assertEquals(BOOLEAN_OPERATOR.AND, kiePMMLFieldOperatorValue.getOperator());
        String expectedValue = "value < \"" + simplePredicate.getValue() + "\"";
        assertEquals(expectedValue, kiePMMLFieldOperatorValue.getConstraintsAsString());
    }

    private SimplePredicate getSimplePredicate(final String predicateName,
                                               final Object value,
                                                     final SimplePredicate.Operator operator,
                                                     final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        fieldTypeMap.put(predicateName,
                         new KiePMMLOriginalTypeGeneratedType(DataType.STRING.value(),
                                                              getSanitizedClassName(predicateName.toUpperCase())));
        return PMMLModelTestUtils.getSimplePredicate(predicateName,
                                                     value,
                                                     operator);
    }
}