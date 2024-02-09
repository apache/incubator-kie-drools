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

import java.util.List;

import org.dmg.pmml.CompoundPredicate;
import org.drools.util.StringUtils;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLReasonCodeAndValue;
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

    private KiePMMLCompoundPredicateASTFactory(final PredicateASTFactoryData predicateASTFactoryData) {
        super(predicateASTFactoryData);
    }

    public static KiePMMLCompoundPredicateASTFactory factory(final PredicateASTFactoryData predicateASTFactoryData) {
        return new KiePMMLCompoundPredicateASTFactory(predicateASTFactoryData);
    }

    /**
     * @param toAccumulate
     * @param statusToSet
     * @param reasonCodeAndValue
     * @param isLastCharacteristic
     */
    public void declareRuleFromCompoundPredicate(final Number toAccumulate,
                                                 final String statusToSet,
                                                 final KiePMMLReasonCodeAndValue reasonCodeAndValue,
                                                 boolean isLastCharacteristic) {
        logger.trace("declareRuleFromCompoundPredicate {} {} {}", toAccumulate, statusToSet, isLastCharacteristic);
        CompoundPredicate compoundPredicate = (CompoundPredicate) predicateASTFactoryData.getPredicate();
        switch (compoundPredicate.getBooleanOperator()) {
            case SURROGATE:
                final String agendaActivationGroup = String.format(KiePMMLAbstractModelASTFactory.SURROGATE_GROUP_PATTERN, predicateASTFactoryData.getCurrentRule());
                declareRuleFromCompoundPredicateSurrogate(agendaActivationGroup, statusToSet);
                KiePMMLCompoundPredicateWithAccumulationASTFactory.declareRuleFromCompoundPredicateSurrogate(predicateASTFactoryData, agendaActivationGroup, toAccumulate, statusToSet, reasonCodeAndValue, isLastCharacteristic);
                break;
            case AND:
            case OR:
            case XOR:
                declareRuleFromCompoundPredicateAndOrXor(toAccumulate, statusToSet, reasonCodeAndValue, isLastCharacteristic);
                break;
            default:
                throw new IllegalStateException(String.format("Unknown CompoundPredicate.booleanOperator %st", compoundPredicate.getBooleanOperator()));
        }
    }

    /**
     * @param result
     * @param isFinalLeaf
     */
    public void declareRuleFromCompoundPredicate(final Object result,
                                                 final boolean isFinalLeaf) {
        logger.trace("declareRuleFromCompoundPredicate {} {}", result, isFinalLeaf);
        CompoundPredicate compoundPredicate = (CompoundPredicate) predicateASTFactoryData.getPredicate();
        switch (compoundPredicate.getBooleanOperator()) {
            case SURROGATE:
                final String agendaActivationGroup = String.format(KiePMMLAbstractModelASTFactory.SURROGATE_GROUP_PATTERN, predicateASTFactoryData.getCurrentRule());
                declareRuleFromCompoundPredicateSurrogate(agendaActivationGroup, null);
                KiePMMLCompoundPredicateWithResultASTFactory.declareRuleFromCompoundPredicateSurrogate(predicateASTFactoryData, agendaActivationGroup, result, isFinalLeaf);
                break;
            case AND:
            case OR:
            case XOR:
                declareRuleFromCompoundPredicateAndOrXor(result, isFinalLeaf);
                break;
            default:
                throw new IllegalStateException(String.format("Unknown CompoundPredicate.booleanOperator %st", compoundPredicate.getBooleanOperator()));
        }
    }

    /**
     * Method to be invoked when <b>compoundPredicate.getBooleanOperator()</b> is <code>AND</code>, <code>OR</code> or
     * <XOR>XOR</XOR>. Throws exception otherwise
     * @param toAccumulate
     * @param statusToSet
     * @param reasonCodeAndValue
     * @param isLastCharacteristic
     */
    private void declareRuleFromCompoundPredicateAndOrXor(final Number toAccumulate,
                                                          final String statusToSet,
                                                          final KiePMMLReasonCodeAndValue reasonCodeAndValue,
                                                          final boolean isLastCharacteristic) {
        logger.trace("declareRuleFromCompoundPredicateAndOrXor {} {} {}", toAccumulate, statusToSet, isLastCharacteristic);
        KiePMMLDroolsRule.Builder builder = getBuilderForCompoundPredicateAndOrXor(statusToSet)
                .withAccumulation(toAccumulate);
        KiePMMLCompoundPredicateWithAccumulationASTFactory.declareRuleFromCompoundPredicateAndOrXor(builder, predicateASTFactoryData.getRules(), reasonCodeAndValue, isLastCharacteristic);
    }

    /**
     * Method to be invoked when <b>compoundPredicate.getBooleanOperator()</b> is <code>AND</code>, <code>OR</code> or
     * <XOR>XOR</XOR>. Throws exception otherwise
     * @param result
     * @param isFinalLeaf
     */
    private void declareRuleFromCompoundPredicateAndOrXor(final Object result,
                                                          final boolean isFinalLeaf) {
        logger.trace("declareRuleFromCompoundPredicateAndOrXor {} {}", result, isFinalLeaf);
        String statusToSet = isFinalLeaf ? DONE : predicateASTFactoryData.getCurrentRule();
        KiePMMLDroolsRule.Builder builder = getBuilderForCompoundPredicateAndOrXor(statusToSet);
        KiePMMLCompoundPredicateWithResultASTFactory.declareRuleFromCompoundPredicateAndOrXor(builder, predicateASTFactoryData.getRules(), result, isFinalLeaf);
    }

    /**
     * Method to be invoked when <b>compoundPredicate.getBooleanOperator()</b> is <code>AND</code>, <code>OR</code> or
     * <XOR>XOR</XOR>. Throws exception otherwise
     * @param statusToSet
     */
    private KiePMMLDroolsRule.Builder getBuilderForCompoundPredicateAndOrXor(final String statusToSet) {
        logger.trace("getBuilderForCompoundPredicateAndOrXor {}", statusToSet);
        CompoundPredicate compoundPredicate = (CompoundPredicate) predicateASTFactoryData.getPredicate();
        if (!CompoundPredicate.BooleanOperator.AND.equals(compoundPredicate.getBooleanOperator()) &&
                !CompoundPredicate.BooleanOperator.OR.equals((compoundPredicate.getBooleanOperator())) &&
                !CompoundPredicate.BooleanOperator.XOR.equals((compoundPredicate.getBooleanOperator()))) {
            throw new KiePMMLException(String.format("getBuilderForCompoundPredicateAndOrXor invoked with %s CompoundPredicate", compoundPredicate.getBooleanOperator()));
        }
        String statusConstraint = StringUtils.isEmpty(predicateASTFactoryData.getParentPath()) ? KiePMMLAbstractModelASTFactory.STATUS_NULL : String.format(STATUS_PATTERN, predicateASTFactoryData.getParentPath());
        List<KiePMMLFieldOperatorValue> constraints;
        KiePMMLDroolsRule.Builder toReturn = KiePMMLDroolsRule.builder(predicateASTFactoryData.getCurrentRule(), statusToSet, predicateASTFactoryData.getOutputFields())
                .withStatusConstraint(statusConstraint);
        switch (compoundPredicate.getBooleanOperator()) {
            case AND:
                constraints = getConstraintEntriesFromAndOrCompoundPredicate(compoundPredicate, predicateASTFactoryData.getFieldTypeMap());
                toReturn = toReturn.withAndConstraints(constraints);
                break;
            case OR:
                constraints = getConstraintEntriesFromAndOrCompoundPredicate(compoundPredicate, predicateASTFactoryData.getFieldTypeMap());
                toReturn = toReturn.withOrConstraints(constraints);
                break;
            case XOR:
                constraints = getConstraintEntriesFromXOrCompoundPredicate(compoundPredicate, predicateASTFactoryData.getFieldTypeMap());
                toReturn = toReturn.withXorConstraints(constraints);
                break;
            default:
                throw new IllegalStateException(String.format("CompoundPredicate.booleanOperator should never be %s at this point", compoundPredicate.getBooleanOperator()));
        }
        return toReturn;
    }

    /**
     * Method to be invoked when <b>compoundPredicate.getBooleanOperator()</b> is <code>SURROGATE</code>.
     * Throws exception otherwise
     * @param agendaActivationGroup
     * @param statusToSet
     */
    private void declareRuleFromCompoundPredicateSurrogate(final String agendaActivationGroup,
                                                           final String statusToSet) {
        logger.trace("declareRuleFromCompoundPredicateSurrogate {} {}", agendaActivationGroup, statusToSet);
        CompoundPredicate compoundPredicate = (CompoundPredicate) predicateASTFactoryData.getPredicate();
        if (!CompoundPredicate.BooleanOperator.SURROGATE.equals(compoundPredicate.getBooleanOperator())) {
            throw new KiePMMLException(String.format("declareRuleFromCompoundPredicateSurrogate invoked with %s CompoundPredicate", compoundPredicate.getBooleanOperator()));
        }
        KiePMMLDroolsRule.Builder builder = KiePMMLDroolsRule.builder(predicateASTFactoryData.getCurrentRule(), null, predicateASTFactoryData.getOutputFields())
                .withStatusConstraint(String.format(STATUS_PATTERN, predicateASTFactoryData.getParentPath()))
                .withFocusedAgendaGroup(agendaActivationGroup);
        predicateASTFactoryData.getRules().add(builder.build());
    }
}
