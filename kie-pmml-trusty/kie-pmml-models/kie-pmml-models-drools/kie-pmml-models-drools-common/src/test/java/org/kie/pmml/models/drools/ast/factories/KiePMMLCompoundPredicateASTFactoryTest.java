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
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.DataType;
import org.dmg.pmml.SimplePredicate;
import org.junit.jupiter.api.Test;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.compiler.api.testutils.PMMLModelTestUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory.STATUS_PATTERN;
import static org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory.SURROGATE_GROUP_PATTERN;
import static org.kie.pmml.models.drools.utils.KiePMMLASTTestUtils.getPredicateASTFactoryData;


public class KiePMMLCompoundPredicateASTFactoryTest {

    @Test
    void declareRuleFromCompoundPredicateAndOrXorFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        final List<SimplePredicate> predicates = getSimplePredicates(fieldTypeMap);
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
            final List<KiePMMLDroolsRule> rules = new ArrayList<>();
            PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(compoundPredicate,
                    Collections.emptyList(),
                    rules,
                    parentPath,
                    currentRule,
                    fieldTypeMap);
            KiePMMLCompoundPredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromCompoundPredicate(result, true);
            assertThat(rules).hasSize(1);
            final KiePMMLDroolsRule retrieved = rules.get(0);
            assertThat(retrieved).isNotNull();
            assertThat(retrieved.getName()).isEqualTo(currentRule);
            assertThat(retrieved.getStatusToSet()).isEqualTo(DONE);
            assertThat(retrieved.getStatusConstraint()).isEqualTo(String.format(STATUS_PATTERN, parentPath));
            assertThat(retrieved.getResult()).isEqualTo(result);
            assertThat(retrieved.getResultCode()).isEqualTo(ResultCode.OK);
            switch (compoundPredicate.getBooleanOperator()) {
                case AND:
                    assertThat(retrieved.getAndConstraints()).isNotNull();
                    break;
                case OR:
                    assertThat(retrieved.getOrConstraints()).isNotNull();
                    break;
                case XOR:
                    assertThat(retrieved.getXorConstraints()).isNotNull();
                    break;
                default:
                // no op
            }
        }
    }

    @Test
    void declareRuleFromCompoundPredicateAndOrXorNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        final List<SimplePredicate> predicates = getSimplePredicates(fieldTypeMap);
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
            final List<KiePMMLDroolsRule> rules = new ArrayList<>();
            PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(compoundPredicate,
                    Collections.emptyList(),
                    rules,
                    parentPath,
                    currentRule,
                    fieldTypeMap);
            KiePMMLCompoundPredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromCompoundPredicate(result, false);
            assertThat(rules).hasSize(1);
            final KiePMMLDroolsRule retrieved = rules.get(0);
            assertThat(retrieved).isNotNull();
            assertThat(retrieved.getName()).isEqualTo(currentRule);
            assertThat(retrieved.getStatusToSet()).isEqualTo(currentRule);
            assertThat(retrieved.getStatusConstraint()).isEqualTo(String.format(STATUS_PATTERN, parentPath));
            switch (compoundPredicate.getBooleanOperator()) {
                case AND:
                    assertThat(retrieved.getAndConstraints()).isNotNull();
                    break;
                case OR:
                    assertThat(retrieved.getOrConstraints()).isNotNull();
                    break;
                case XOR:
                    assertThat(retrieved.getXorConstraints()).isNotNull();
                    break;
                default:
                // no op
            }
        }
    }

    @Test
    void declareRuleFromCompoundPredicateSurrogateFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        final List<SimplePredicate> predicates = getSimplePredicates(fieldTypeMap);
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String result = "RESULT";
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.SURROGATE);
        predicates.forEach(compoundPredicate::addPredicates);
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(compoundPredicate,
                Collections.emptyList(),
                rules,
                parentPath,
                currentRule,
                fieldTypeMap);
        KiePMMLCompoundPredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromCompoundPredicate(result, true);
        int expectedRules = (predicates.size() * 2) + 1; // For each "surrogate" predicate two rules -"TRUE" and "FALSE" - are generated; one more rule is generated for the Compound predicate itself
        assertThat(rules).hasSize(expectedRules);
        String agendaActivationGroup = String.format(SURROGATE_GROUP_PATTERN, currentRule);
        for (KiePMMLDroolsRule retrieved : rules) {
            String ruleName = retrieved.getName();
            if (ruleName.contains("_surrogate_")) {
                String[] ruleNameParts = ruleName.split("_surrogate_");
                String generatedTypePart = ruleNameParts[1];
                boolean isTrueRule = generatedTypePart.endsWith("_TRUE");
                final String generatedType = generatedTypePart.replace("_TRUE", "").replace("_FALSE", "");
                final Optional<String> fieldName = fieldTypeMap
                        .entrySet()
                        .stream()
                        .filter(entry -> generatedType.equals(entry.getValue().getGeneratedType()))
                        .map(Map.Entry::getKey)
                        .findFirst();
                if (fieldName.isPresent()) {
                    SimplePredicate mappedPredicate = predicates.stream()
                            .filter(pred -> fieldName.get().equals(pred.getField()))
                            .findFirst()
                            .orElse(null);
                    assertThat(mappedPredicate).isNotNull();
                    assertThat(retrieved.getStatusConstraint()).isNull();
                    assertThat(retrieved.getActivationGroup()).isEqualTo(agendaActivationGroup);
                    assertThat(retrieved.getAgendaGroup()).isEqualTo(agendaActivationGroup);
                    // Those are in a final leaf node
                    if (isTrueRule) {
                        assertThat(retrieved.getStatusToSet()).isEqualTo(DONE);
                        assertThat(retrieved.getResult()).isEqualTo(result);
                        assertThat(retrieved.getResultCode()).isEqualTo(ResultCode.OK);
                    } else {
                        assertThat(retrieved.getStatusToSet()).isEqualTo(parentPath);
                        assertThat(retrieved.getResult()).isNull();
                        assertThat(retrieved.getResultCode()).isNull();
                    }
                }
            } else {
                assertThat(retrieved.getStatusConstraint()).isNotNull();
                assertThat(retrieved.getStatusConstraint()).isEqualTo(String.format(STATUS_PATTERN, parentPath));
                assertThat(retrieved.getFocusedAgendaGroup()).isEqualTo(agendaActivationGroup);
                assertThat(retrieved.getStatusToSet()).isNull();
                assertThat(retrieved.getResult()).isNull();
                assertThat(retrieved.getResultCode()).isNull();
            }
        }
    }

    @Test
    void declareRuleFromCompoundPredicateSurrogateNotFinalLeaf() {
        final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap = new HashMap<>();
        final List<SimplePredicate> predicates = getSimplePredicates(fieldTypeMap);
        String parentPath = "_will play";
        String currentRule = "_will play_will play";
        String result = "RESULT";
        CompoundPredicate compoundPredicate = new CompoundPredicate();
        compoundPredicate.setBooleanOperator(CompoundPredicate.BooleanOperator.SURROGATE);
        predicates.forEach(compoundPredicate::addPredicates);
        final List<KiePMMLDroolsRule> rules = new ArrayList<>();
        PredicateASTFactoryData predicateASTFactoryData = getPredicateASTFactoryData(compoundPredicate,
                Collections.emptyList(),
                rules,
                parentPath,
                currentRule,
                fieldTypeMap);
        KiePMMLCompoundPredicateASTFactory.factory(predicateASTFactoryData).declareRuleFromCompoundPredicate(result, false);
        int expectedRules = (predicates.size() * 2) + 1; // For each "surrogate" predicate two rules -"TRUE" and "FALSE" - are generated; one more rule is generated for the Compound predicate itself
        assertThat(rules).hasSize(expectedRules);
        String agendaActivationGroup = String.format(SURROGATE_GROUP_PATTERN, currentRule);
        for (KiePMMLDroolsRule retrieved : rules) {
            String ruleName = retrieved.getName();
            if (ruleName.contains("_surrogate_")) {
                String[] ruleNameParts = ruleName.split("_surrogate_");
                String generatedTypePart = ruleNameParts[1];
                boolean isTrueRule = generatedTypePart.endsWith("_TRUE");
                final String generatedType = generatedTypePart.replace("_TRUE", "").replace("_FALSE", "");
                final Optional<String> fieldName = fieldTypeMap
                        .entrySet()
                        .stream()
                        .filter(entry -> generatedType.equals(entry.getValue().getGeneratedType()))
                        .map(Map.Entry::getKey)
                        .findFirst();
                if (fieldName.isPresent()) {
                    SimplePredicate mappedPredicate = predicates.stream()
                            .filter(pred -> fieldName.get().equals(pred.getField())).findFirst().orElse(null);
                    assertThat(mappedPredicate).isNotNull();
                    assertThat(retrieved.getStatusConstraint()).isNull();
                    assertThat(retrieved.getActivationGroup()).isEqualTo(agendaActivationGroup);
                    assertThat(retrieved.getAgendaGroup()).isEqualTo(agendaActivationGroup);
                    // Those are not in a final leaf node
                    if (isTrueRule) {
                        assertThat(retrieved.getStatusToSet()).isEqualTo(currentRule);
                        assertThat(retrieved.getResult()).isNull();
                        assertThat(retrieved.getResultCode()).isNull();
                    } else {
                        assertThat(retrieved.getStatusToSet()).isEqualTo(parentPath);
                        assertThat(retrieved.getResult()).isNull();
                        assertThat(retrieved.getResultCode()).isNull();
                    }
                }
            } else {
                assertThat(retrieved.getStatusConstraint()).isNotNull();
                assertThat(retrieved.getStatusConstraint()).isEqualTo(String.format(STATUS_PATTERN, parentPath));
                assertThat(retrieved.getFocusedAgendaGroup()).isEqualTo(agendaActivationGroup);
                assertThat(retrieved.getStatusToSet()).isNull();
                assertThat(retrieved.getResult()).isNull();
                assertThat(retrieved.getResultCode()).isNull();
            }
        }
    }

    private List<SimplePredicate> getSimplePredicates(final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap) {
        return IntStream.range(0, 2)
                .mapToObj(index -> {
                    String predicateName = "SIMPLEPREDICATE-" + index;
                    fieldTypeMap.put(predicateName,
                                     new KiePMMLOriginalTypeGeneratedType(DataType.STRING.value(),
                                                                          getSanitizedClassName(predicateName.toUpperCase())));
                    return PMMLModelTestUtils.getSimplePredicate(predicateName,
                                                                 "VALUE-" + index,
                                                                 SimplePredicate.Operator.LESS_THAN);
                })
                .collect(Collectors.toList());

    }
}