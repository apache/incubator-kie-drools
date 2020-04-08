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

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.drools.core.util.StringUtils;
import org.kie.pmml.commons.enums.ResultCode;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.kie.pmml.models.drooled.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLASTFactoryUtils.getConstraintEntriesFromAndOrCompoundPredicate;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLASTFactoryUtils.getConstraintEntriesFromXOrCompoundPredicate;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.STATUS_NULL;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.STATUS_PATTERN;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.SURROGATE_GROUP_PATTERN;

/**
 * Class used to generate <code>KiePMMLDrooledRule</code>s out of a <code>CompoundPredicate</code>
 */
public class KiePMMLTreeModelCompoundPredicateASTFactory extends KiePMMLTreeModeAbstractPredicateASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelCompoundPredicateASTFactory.class.getName());
    private final CompoundPredicate compoundPredicate;

    private KiePMMLTreeModelCompoundPredicateASTFactory(final CompoundPredicate compoundPredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final Queue<KiePMMLDrooledRule> rules) {
        super(fieldTypeMap, outputFields, rules);
        this.compoundPredicate = compoundPredicate;
    }

    public static KiePMMLTreeModelCompoundPredicateASTFactory factory(final CompoundPredicate compoundPredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final Queue<KiePMMLDrooledRule> rules) {
        return new KiePMMLTreeModelCompoundPredicateASTFactory(compoundPredicate, fieldTypeMap, outputFields, rules);
    }

    public void declareRuleFromCompoundPredicate(final String parentPath,
                                                 final String currentRule,
                                                 final Object result,
                                                 boolean isFinalLeaf) {
        logger.debug("declareIntermediateRuleFromCompoundPredicate {} {} {} {}", compoundPredicate, parentPath, currentRule, result);
        switch (compoundPredicate.getBooleanOperator()) {
            case SURROGATE:
                declareRuleFromCompoundPredicateSurrogate(parentPath, currentRule, result, isFinalLeaf);
                break;
            case AND:
                declareRuleFromCompoundPredicateAndOrXor(parentPath, currentRule, result, isFinalLeaf);
                break;
            case OR:
                declareRuleFromCompoundPredicateAndOrXor(parentPath, currentRule, result, isFinalLeaf);
                break;
            case XOR:
                declareRuleFromCompoundPredicateAndOrXor(parentPath, currentRule, result, isFinalLeaf);
                break;
        }
    }

    public void declareRuleFromCompoundPredicateAndOrXor(final String parentPath,
                                                         final String currentRule,
                                                         final Object result,
                                                         boolean isFinalLeaf) {
        logger.debug("declareIntermediateRuleFromCompoundPredicateAndOrXor {} {} {}", compoundPredicate, parentPath, currentRule);
        String statusConstraint = StringUtils.isEmpty(parentPath) ? STATUS_NULL : String.format(STATUS_PATTERN, parentPath);
        List<KiePMMLFieldOperatorValue> constraints;
        String statusToSet = isFinalLeaf ? DONE : currentRule;
        KiePMMLDrooledRule.Builder builder = KiePMMLDrooledRule.builder(currentRule, statusToSet, outputFields)
                .withStatusConstraint(statusConstraint);
        switch (compoundPredicate.getBooleanOperator()) {
            case AND:
                constraints = getConstraintEntriesFromAndOrCompoundPredicate(compoundPredicate, fieldTypeMap);
                builder = builder.withAndConstraints(constraints);
                break;
            case OR:
                constraints = getConstraintEntriesFromAndOrCompoundPredicate(compoundPredicate, fieldTypeMap);
                builder = builder.withOrConstraints(constraints);
                break;
            case XOR:
                constraints = getConstraintEntriesFromXOrCompoundPredicate(compoundPredicate, fieldTypeMap);
                builder = builder.withXorConstraints(constraints);
                break;
            default:
                break;
        }
        if (isFinalLeaf) {
            builder = builder.withResult(result)
                    .withResultCode(ResultCode.OK);
        }
        rules.add(builder.build());
    }

    public void declareRuleFromCompoundPredicateSurrogate(final String parentPath,
                                                          final String currentRule,
                                                          final Object result,
                                                          boolean isFinalLeaf) {
        logger.debug("declareRuleFromCompoundPredicateSurrogate {} {} {} {}", compoundPredicate, parentPath, currentRule, result);
        final String agendaActivationGroup = String.format(SURROGATE_GROUP_PATTERN, currentRule);
        KiePMMLDrooledRule.Builder builder = KiePMMLDrooledRule.builder(currentRule, null, outputFields)
                .withStatusConstraint(String.format(STATUS_PATTERN, parentPath))
                .withFocusedAgendaGroup(agendaActivationGroup);
        rules.add(builder.build());
        // Managing only SimplePredicates for the moment being
        final List<Predicate> simplePredicates = compoundPredicate.getPredicates().stream().filter(predicate -> predicate instanceof SimplePredicate).collect(Collectors.toList());
        simplePredicates.forEach(predicate -> {
            SimplePredicate simplePredicate = (SimplePredicate) predicate;
            KiePMMLTreeModelSimplePredicateASTFactory.factory(simplePredicate, fieldTypeMap, outputFields, rules).declareRuleFromSimplePredicateSurrogate(parentPath, currentRule, agendaActivationGroup, result, isFinalLeaf);
        });
    }
}
