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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.drools.core.util.StringUtils;
import org.kie.pmml.commons.enums.ResultCode;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory.STATUS_PATTERN;
import static org.kie.pmml.models.drools.utils.KiePMMLASTFactoryUtils.getConstraintEntriesFromAndOrCompoundPredicate;
import static org.kie.pmml.models.drools.utils.KiePMMLASTFactoryUtils.getConstraintEntriesFromXOrCompoundPredicate;

/**
 * Class used to generate <code>KiePMMLDroolsRule</code>s out of a <code>CompoundPredicate</code>
 */
public class KiePMMLCompoundPredicateASTFactory extends KiePMMLAbstractPredicateASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLCompoundPredicateASTFactory.class.getName());
    private final CompoundPredicate compoundPredicate;

    private KiePMMLCompoundPredicateASTFactory(final CompoundPredicate compoundPredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final List<KiePMMLDroolsRule> rules) {
        super(fieldTypeMap, outputFields, rules);
        this.compoundPredicate = compoundPredicate;
    }

    public static KiePMMLCompoundPredicateASTFactory factory(final CompoundPredicate compoundPredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final List<KiePMMLDroolsRule> rules) {
        return new KiePMMLCompoundPredicateASTFactory(compoundPredicate, fieldTypeMap, outputFields, rules);
    }

    public void declareRuleFromCompoundPredicate(final String parentPath,
                                                 final String currentRule,
                                                 final Object result,
                                                 boolean isFinalLeaf) {
        logger.trace("declareIntermediateRuleFromCompoundPredicate {} {} {} {}", compoundPredicate, parentPath, currentRule, result);
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
            default:
                throw new IllegalStateException(String.format("Unknown CompoundPredicate.booleanOperator %st", compoundPredicate.getBooleanOperator()));
        }
    }

    /**
     * Method to be invoked when <b>compoundPredicate.getBooleanOperator()</b> is <code>AND</code>, <code>OR</code> or
     * <XOR>XOR</XOR>. Throws exception otherwise
     * @param parentPath
     * @param currentRule
     * @param result
     * @param isFinalLeaf
     */
    public void declareRuleFromCompoundPredicateAndOrXor(final String parentPath,
                                                         final String currentRule,
                                                         final Object result,
                                                         boolean isFinalLeaf) {
        logger.trace("declareRuleFromCompoundPredicateAndOrXor {} {} {}", compoundPredicate, parentPath, currentRule);
        if (!CompoundPredicate.BooleanOperator.AND.equals(compoundPredicate.getBooleanOperator()) &&
                !CompoundPredicate.BooleanOperator.OR.equals((compoundPredicate.getBooleanOperator())) &&
                !CompoundPredicate.BooleanOperator.XOR.equals((compoundPredicate.getBooleanOperator()))) {
            throw new KiePMMLException(String.format("getConstraintEntriesFromAndOrCompoundPredicate invoked with %s CompoundPredicate", compoundPredicate.getBooleanOperator()));
        }
        String statusConstraint = StringUtils.isEmpty(parentPath) ? KiePMMLAbstractModelASTFactory.STATUS_NULL : String.format(STATUS_PATTERN, parentPath);
        List<KiePMMLFieldOperatorValue> constraints;
        String statusToSet = isFinalLeaf ? DONE : currentRule;
        KiePMMLDroolsRule.Builder builder = KiePMMLDroolsRule.builder(currentRule, statusToSet, outputFields)
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
                throw new IllegalStateException(String.format("CompoundPredicate.booleanOperator should never be %s at this point", compoundPredicate.getBooleanOperator()));
        }
        if (isFinalLeaf) {
            builder = builder.withResult(result)
                    .withResultCode(ResultCode.OK);
        }
        rules.add(builder.build());
    }

    /**
     * Method to be invoked when <b>compoundPredicate.getBooleanOperator()</b> is <code>SURROGATE</code>.
     * Throws exception otherwise
     * @param parentPath
     * @param currentRule
     * @param result
     * @param isFinalLeaf
     */
    public void declareRuleFromCompoundPredicateSurrogate(final String parentPath,
                                                          final String currentRule,
                                                          final Object result,
                                                          boolean isFinalLeaf) {
        logger.trace("declareRuleFromCompoundPredicateSurrogate {} {} {} {}", compoundPredicate, parentPath, currentRule, result);

        if (!CompoundPredicate.BooleanOperator.SURROGATE.equals(compoundPredicate.getBooleanOperator())) {
            throw new KiePMMLException(String.format("declareRuleFromCompoundPredicateSurrogate invoked with %s CompoundPredicate", compoundPredicate.getBooleanOperator()));
        }
        final String agendaActivationGroup = String.format(KiePMMLAbstractModelASTFactory.SURROGATE_GROUP_PATTERN, currentRule);
        KiePMMLDroolsRule.Builder builder = KiePMMLDroolsRule.builder(currentRule, null, outputFields)
                .withStatusConstraint(String.format(STATUS_PATTERN, parentPath))
                .withFocusedAgendaGroup(agendaActivationGroup);
        rules.add(builder.build());
        // Managing only SimplePredicates for the moment being
        final List<Predicate> simplePredicates = compoundPredicate.getPredicates().stream().filter(predicate -> predicate instanceof SimplePredicate).collect(Collectors.toList());
        simplePredicates.forEach(predicate -> {
            SimplePredicate simplePredicate = (SimplePredicate) predicate;
            KiePMMLSimplePredicateASTFactory.factory(simplePredicate, fieldTypeMap, outputFields, rules).declareRuleFromSimplePredicateSurrogate(parentPath, currentRule, agendaActivationGroup, result, isFinalLeaf);
        });
    }
}
