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
package org.kie.pmml.models.drools.ast.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.DataType;
import org.dmg.pmml.SimplePredicate;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getPredicateASTFactoryData;

public class KiePMMLSimplePredicateASTFactoryTest {

    @Test
    void declareRuleFromSimplePredicateSurrogateFinalLeaf() {
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
        assertThat(rules).hasSize(2);
        // This is the "TRUE" matching rule
        KiePMMLDroolsRule retrieved = rules.get(0);
        assertThat(retrieved).isNotNull();
        String baseExpectedRule = String.format(KiePMMLAbstractModelASTFactory.SURROGATE_RULENAME_PATTERN,
                currentRule,
                fieldTypeMap.get(simplePredicate.getField()).getGeneratedType());
        String expectedRule = baseExpectedRule + "_TRUE";
        assertThat(retrieved.getName()).isEqualTo(expectedRule);
        assertThat(retrieved.getStatusToSet()).isEqualTo(DONE);
        assertThat(retrieved.getStatusConstraint()).isNull();
        assertThat(retrieved.getAgendaGroup()).isEqualTo(agendaActivationGroup);
        assertThat(retrieved.getActivationGroup()).isEqualTo(agendaActivationGroup);
        assertThat(retrieved.getIfBreakField()).isNull();
        assertThat(retrieved.getIfBreakOperator()).isNull();
        assertThat(retrieved.getIfBreakValue()).isNull();
        assertThat(retrieved.getNotConstraints()).isNull();
        assertThat(retrieved.getAndConstraints()).isNotNull();
        assertThat(retrieved.getAndConstraints()).hasSize(1);
        KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = retrieved.getAndConstraints().get(0);
        assertThat(kiePMMLFieldOperatorValue.getName()).isEqualTo("OUTLOOK");
        assertThat(kiePMMLFieldOperatorValue.getOperator()).isEqualTo(BOOLEAN_OPERATOR.SURROGATE);
        assertThat(kiePMMLFieldOperatorValue.getConstraintsAsString()).isEqualTo("value < \"VALUE\"");
        assertThat(retrieved.getResult()).isEqualTo(result);
        assertThat(retrieved.getResultCode()).isEqualTo(ResultCode.OK);
        // This is the "FALSE" matching rule
        retrieved = rules.get(1);
        assertThat(retrieved).isNotNull();
        expectedRule = baseExpectedRule + "_FALSE";
        assertThat(retrieved.getName()).isEqualTo(expectedRule);
        assertThat(retrieved.getStatusToSet()).isEqualTo(parentPath);
        assertThat(retrieved.getStatusConstraint()).isNull();
        assertThat(retrieved.getAgendaGroup()).isEqualTo(agendaActivationGroup);
        assertThat(retrieved.getActivationGroup()).isEqualTo(agendaActivationGroup);
        assertThat(retrieved.getIfBreakField()).isNull();
        assertThat(retrieved.getIfBreakOperator()).isNull();
        assertThat(retrieved.getIfBreakValue()).isNull();
        assertThat(retrieved.getAndConstraints()).isNull();
        assertThat(retrieved.getNotConstraints()).isNotNull();
        assertThat(retrieved.getNotConstraints()).hasSize(1);
        kiePMMLFieldOperatorValue = retrieved.getNotConstraints().get(0);
        assertThat(kiePMMLFieldOperatorValue.getName()).isEqualTo("OUTLOOK");
        assertThat(kiePMMLFieldOperatorValue.getOperator()).isEqualTo(BOOLEAN_OPERATOR.SURROGATE);
        assertThat(kiePMMLFieldOperatorValue.getConstraintsAsString()).isEqualTo("value < \"VALUE\"");
        assertThat(retrieved.getResult()).isNull();
        assertThat(retrieved.getResultCode()).isNull();
    }

    @Test
    void declareRuleFromSimplePredicateSurrogateNotFinalLeaf() {
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
        assertThat(rules).hasSize(2);
        // This is the "TRUE" matching rule
        KiePMMLDroolsRule retrieved = rules.get(0);
        assertThat(retrieved).isNotNull();
        String baseExpectedRule = String.format(KiePMMLAbstractModelASTFactory.SURROGATE_RULENAME_PATTERN,
                currentRule,
                fieldTypeMap.get(simplePredicate.getField()).getGeneratedType());
        String expectedRule = baseExpectedRule + "_TRUE";
        assertThat(retrieved.getName()).isEqualTo(expectedRule);
        assertThat(retrieved.getStatusToSet()).isEqualTo(currentRule);
        assertThat(retrieved.getStatusConstraint()).isNull();
        assertThat(retrieved.getAgendaGroup()).isEqualTo(agendaActivationGroup);
        assertThat(retrieved.getActivationGroup()).isEqualTo(agendaActivationGroup);
        assertThat(retrieved.getIfBreakField()).isNull();
        assertThat(retrieved.getIfBreakOperator()).isNull();
        assertThat(retrieved.getIfBreakValue()).isNull();
        assertThat(retrieved.getAndConstraints()).isNotNull();
        assertThat(retrieved.getAndConstraints()).hasSize(1);
        KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = retrieved.getAndConstraints().get(0);
        assertThat(kiePMMLFieldOperatorValue.getName()).isEqualTo("OUTLOOK");
        assertThat(kiePMMLFieldOperatorValue.getOperator()).isEqualTo(BOOLEAN_OPERATOR.SURROGATE);
        assertThat(kiePMMLFieldOperatorValue.getConstraintsAsString()).isEqualTo("value < \"VALUE\"");
        assertThat(retrieved.getResult()).isNull();
        assertThat(retrieved.getResultCode()).isNull();
        // This is the "FALSE" matching rule
        retrieved = rules.get(1);
        assertThat(retrieved).isNotNull();
        expectedRule = baseExpectedRule + "_FALSE";
        assertThat(retrieved.getName()).isEqualTo(expectedRule);
        assertThat(retrieved.getStatusToSet()).isEqualTo(parentPath);
        assertThat(retrieved.getStatusConstraint()).isNull();
        assertThat(retrieved.getAgendaGroup()).isEqualTo(agendaActivationGroup);
        assertThat(retrieved.getActivationGroup()).isEqualTo(agendaActivationGroup);
        assertThat(retrieved.getIfBreakField()).isNull();
        assertThat(retrieved.getIfBreakOperator()).isNull();
        assertThat(retrieved.getIfBreakValue()).isNull();
        assertThat(retrieved.getAndConstraints()).isNull();
        assertThat(retrieved.getNotConstraints()).isNotNull();
        assertThat(retrieved.getNotConstraints()).hasSize(1);
        kiePMMLFieldOperatorValue = retrieved.getNotConstraints().get(0);
        assertThat(kiePMMLFieldOperatorValue.getName()).isEqualTo("OUTLOOK");
        assertThat(kiePMMLFieldOperatorValue.getOperator()).isEqualTo(BOOLEAN_OPERATOR.SURROGATE);
        assertThat(kiePMMLFieldOperatorValue.getConstraintsAsString()).isEqualTo("value < \"VALUE\"");
        assertThat(retrieved.getResult()).isNull();
        assertThat(retrieved.getResultCode()).isNull();
    }

    @Test
    void declareRuleFromSimplePredicateFinalLeaf() {
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
        assertThat(rules).hasSize(1);
        final KiePMMLDroolsRule retrieved = rules.get(0);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo(currentRule);
        assertThat(retrieved.getStatusToSet()).isEqualTo(DONE);
        assertThat(retrieved.getStatusConstraint()).isEqualTo(String.format(KiePMMLAbstractModelASTFactory.STATUS_PATTERN, parentPath));
        assertThat(retrieved.getResultCode()).isEqualTo(ResultCode.OK);
        assertThat(retrieved.getResult()).isEqualTo(result);
        final List<KiePMMLFieldOperatorValue> andConstraints = retrieved.getAndConstraints();
        assertThat(andConstraints).isNotNull();
        assertThat(andConstraints).hasSize(1);
        KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = retrieved.getAndConstraints().get(0);
        assertThat(kiePMMLFieldOperatorValue.getName()).isEqualTo(declaredType);
        assertThat(kiePMMLFieldOperatorValue.getOperator()).isEqualTo(BOOLEAN_OPERATOR.AND);
        String expectedValue = "value < \"" + simplePredicate.getValue() + "\"";
        assertThat(kiePMMLFieldOperatorValue.getConstraintsAsString()).isEqualTo(expectedValue);
    }

    @Test
    void declareIntermediateRuleFromSimplePredicateNotFinalLeaf() {
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
        assertThat(rules).hasSize(1);
        final KiePMMLDroolsRule retrieved = rules.get(0);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getName()).isEqualTo(currentRule);
        assertThat(retrieved.getStatusToSet()).isEqualTo(currentRule);
        assertThat(retrieved.getStatusConstraint()).isEqualTo(String.format(KiePMMLAbstractModelASTFactory.STATUS_PATTERN, parentPath));
        assertThat(retrieved.getStatusToSet()).isEqualTo(currentRule);
        final List<KiePMMLFieldOperatorValue> andConstraints = retrieved.getAndConstraints();
        assertThat(andConstraints).isNotNull();
        assertThat(andConstraints).hasSize(1);
        KiePMMLFieldOperatorValue kiePMMLFieldOperatorValue = retrieved.getAndConstraints().get(0);
        assertThat(kiePMMLFieldOperatorValue.getName()).isEqualTo(declaredType);
        assertThat(kiePMMLFieldOperatorValue.getOperator()).isEqualTo(BOOLEAN_OPERATOR.AND);
        String expectedValue = "value < \"" + simplePredicate.getValue() + "\"";
        assertThat(kiePMMLFieldOperatorValue.getConstraintsAsString()).isEqualTo(expectedValue);
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