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

import java.util.Collections;
import java.util.List;

import org.dmg.pmml.SimplePredicate;
import org.drools.util.StringUtils;
import org.kie.pmml.api.enums.BOOLEAN_OPERATOR;
import org.kie.pmml.api.enums.OPERATOR;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLReasonCodeAndValue;
import org.kie.pmml.models.drools.utils.KiePMMLASTFactoryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory.STATUS_PATTERN;

/**
 * Class used to generate <code>KiePMMLDroolsRule</code> out of a <code>SimplePredicate</code>
 */
public class KiePMMLSimplePredicateASTFactory extends KiePMMLAbstractPredicateASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSimplePredicateASTFactory.class.getName());

    private KiePMMLSimplePredicateASTFactory(final PredicateASTFactoryData predicateASTFactoryData) {
        super(predicateASTFactoryData);
    }

    public static KiePMMLSimplePredicateASTFactory factory(final PredicateASTFactoryData predicateASTFactoryData) {
        return new KiePMMLSimplePredicateASTFactory(predicateASTFactoryData);
    }

    public void declareRuleFromSimplePredicateSurrogate(
            final String agendaActivationGroup,
            final Number toAccumulate,
            final String statusToSet,
            final KiePMMLReasonCodeAndValue reasonCodeAndValue,
            final boolean isLastCharacteristic) {
        logger.trace("declareRuleFromSimplePredicateSurrogate {} {} {} {}", agendaActivationGroup, toAccumulate, statusToSet, isLastCharacteristic);
        String fieldName = predicateASTFactoryData.getFieldTypeMap().get(((SimplePredicate) predicateASTFactoryData.getPredicate()).getField()).getGeneratedType();
        String surrogateCurrentRule = String.format(KiePMMLAbstractModelASTFactory.SURROGATE_RULENAME_PATTERN, predicateASTFactoryData.getCurrentRule(), fieldName);
        final List<KiePMMLFieldOperatorValue> constraints = Collections.singletonList(KiePMMLASTFactoryUtils.getConstraintEntryFromSimplePredicates(fieldName, BOOLEAN_OPERATOR.SURROGATE, Collections.singletonList((SimplePredicate) predicateASTFactoryData.getPredicate()), predicateASTFactoryData.getFieldTypeMap()));
        // Create "TRUE" matcher
        KiePMMLDroolsRule.Builder builder = getBuilderForSimplePredicateSurrogateTrueMatcher(agendaActivationGroup, surrogateCurrentRule, constraints, statusToSet)
                .withAccumulation(toAccumulate);
        if (reasonCodeAndValue != null) {
            builder = builder.withReasonCodeAndValue(reasonCodeAndValue);
        }
        KiePMMLSimplePredicateWithAccumulationASTFactory.declareRuleFromSimplePredicateSurrogateTrueMatcher(builder, predicateASTFactoryData.getRules(), isLastCharacteristic);
        // Create "FALSE" matcher
        builder = getBuilderForSimplePredicateSurrogateFalseMatcher(agendaActivationGroup, surrogateCurrentRule, constraints, statusToSet)
                .withAccumulation(toAccumulate);
        if (reasonCodeAndValue != null) {
            builder = builder.withReasonCodeAndValue(reasonCodeAndValue);
        }
        KiePMMLSimplePredicateWithAccumulationASTFactory.declareRuleFromSimplePredicateSurrogateFalseMatcher(builder, predicateASTFactoryData.getRules());
    }

    public void declareRuleFromSimplePredicateSurrogate(
            final String agendaActivationGroup,
            final Object result,
            boolean isFinalLeaf) {
        logger.trace("declareRuleFromSimplePredicateSurrogate {} {} {}", agendaActivationGroup, result, isFinalLeaf);
        String fieldName = predicateASTFactoryData.getFieldTypeMap().get(((SimplePredicate) predicateASTFactoryData.getPredicate()).getField()).getGeneratedType();
        String surrogateCurrentRule = String.format(KiePMMLAbstractModelASTFactory.SURROGATE_RULENAME_PATTERN, predicateASTFactoryData.getCurrentRule(), fieldName);
        final List<KiePMMLFieldOperatorValue> constraints = Collections.singletonList(KiePMMLASTFactoryUtils.getConstraintEntryFromSimplePredicates(fieldName, BOOLEAN_OPERATOR.SURROGATE, Collections.singletonList((SimplePredicate) predicateASTFactoryData.getPredicate()), predicateASTFactoryData.getFieldTypeMap()));
        String statusToSet = isFinalLeaf ? DONE : predicateASTFactoryData.getCurrentRule();
        // Create "TRUE" matcher
        KiePMMLDroolsRule.Builder builder = getBuilderForSimplePredicateSurrogateTrueMatcher(agendaActivationGroup, surrogateCurrentRule, constraints, statusToSet);
        KiePMMLSimplePredicateWithResultASTFactory.declareRuleFromSimplePredicateSurrogateTrueMatcher(builder, predicateASTFactoryData.getRules(), result, isFinalLeaf);
        // Create "FALSE" matcher
        builder = getBuilderForSimplePredicateSurrogateFalseMatcher(agendaActivationGroup, surrogateCurrentRule, constraints, statusToSet);
        KiePMMLSimplePredicateWithResultASTFactory.declareRuleFromSimplePredicateSurrogateFalseMatcher(builder, predicateASTFactoryData.getRules());
    }

    /**
     * This method will create a <b>rule</b> that, in the RHS,
     * 1) update the status (used for flowing between rules)
     * 2) add <i>outputfields</i> to result variables
     * 3) eventually set the value to accumulate
     * <p>
     * rule "_ResidenceStateScore_1"
     * when
     * $statusHolder : KiePMMLStatusHolder( status == "_ResidenceStateScore" )
     * <p>
     * RESIDENCESTATE( value == "KN" )
     * then
     * <p>
     * $statusHolder.setStatus("_ResidenceStateScore_1");
     * $statusHolder.accumulate("10.0");
     * update($statusHolder);
     * $outputFieldsMap.put("rank-" + $outputFieldsMap.size(), "_reasonCode_");
     *
     * <p>
     * end
     * <p>
     * end
     *
     * @param toAccumulate
     * @param statusToSet
     * @param reasonCodeAndValue
     * @param isLastCharacteristic
     */
    public void declareRuleFromSimplePredicate(final Number toAccumulate,
                                               final String statusToSet,
                                               final KiePMMLReasonCodeAndValue reasonCodeAndValue,
                                               final boolean isLastCharacteristic) {
        logger.trace("declareRuleFromSimplePredicate {} {} {}", toAccumulate, statusToSet, isLastCharacteristic);
        KiePMMLDroolsRule.Builder builder = getBuilderForSimplePredicate(statusToSet)
                .withAccumulation(toAccumulate);
        if (reasonCodeAndValue != null) {
            builder = builder.withReasonCodeAndValue(reasonCodeAndValue);
        }
        KiePMMLSimplePredicateWithAccumulationASTFactory.declareRuleFromSimplePredicate(builder, predicateASTFactoryData.getRules(), isLastCharacteristic);
    }

    /**
     * This method will create a <b>rule</b> that, in the RHS,
     * 1) update the status (used for flowing between rules)
     * 2) add <i>outputfields</i> to result variables
     * 3) eventually set the value to accumulate
     * <p>
     * rule "_ResidenceStateScore_1"
     * when
     * $statusHolder : KiePMMLStatusHolder( status == "_ResidenceStateScore" )
     * <p>
     * RESIDENCESTATE( value == "KN" )
     * then
     * <p>
     * $statusHolder.setStatus("_ResidenceStateScore_1");
     * $statusHolder.accumulate("10.0");
     * update($statusHolder);
     * <p>
     * end
     * <p>
     * end
     * @param result
     * @param isFinalLeaf
     */
    public void declareRuleFromSimplePredicate(final Object result,
                                               final boolean isFinalLeaf) {
        logger.trace("declareRuleFromSimplePredicate {} {}", result, isFinalLeaf);
        String statusToSet = isFinalLeaf ? DONE : predicateASTFactoryData.getCurrentRule();
        KiePMMLDroolsRule.Builder builder = getBuilderForSimplePredicate(statusToSet);
        KiePMMLSimplePredicateWithResultASTFactory.declareRuleFromSimplePredicate(builder, predicateASTFactoryData.getRules(), result, isFinalLeaf);
    }

    private KiePMMLDroolsRule.Builder getBuilderForSimplePredicateSurrogateTrueMatcher(
            final String agendaActivationGroup,
            final String surrogateCurrentRule,
            final List<KiePMMLFieldOperatorValue> constraints,
            final String statusToSet) {
        logger.trace("getBuilderForSimplePredicateSurrogateTrueMatcher {} {} {} {}", agendaActivationGroup, surrogateCurrentRule, constraints, statusToSet);
        // Create "TRUE" matcher
        return KiePMMLDroolsRule.builder(surrogateCurrentRule + "_TRUE", statusToSet, predicateASTFactoryData.getOutputFields())
                .withAgendaGroup(agendaActivationGroup)
                .withActivationGroup(agendaActivationGroup)
                .withAndConstraints(constraints);
    }

    private KiePMMLDroolsRule.Builder getBuilderForSimplePredicateSurrogateFalseMatcher(
            final String agendaActivationGroup,
            final String surrogateCurrentRule,
            final List<KiePMMLFieldOperatorValue> constraints,
            final String statusToSet) {
        logger.trace("getBuilderForSimplePredicateSurrogateFalseMatcher {} {} {} {}", agendaActivationGroup, surrogateCurrentRule, constraints, statusToSet);
        // Create "FALSE" matcher
        return KiePMMLDroolsRule.builder(surrogateCurrentRule + "_FALSE", predicateASTFactoryData.getParentPath(), predicateASTFactoryData.getOutputFields())
                .withAgendaGroup(agendaActivationGroup)
                .withActivationGroup(agendaActivationGroup)
                .withNotConstraints(constraints);
    }

    /**
     * This method will create a <b>rule</b> that, in the RHS,
     * 1) update the status (used for flowing between rules)
     * 2) add <i>outputfields</i> to result variables
     * 3) eventually set the value to accumulate
     * <p>
     * rule "_ResidenceStateScore_1"
     * when
     * $statusHolder : KiePMMLStatusHolder( status == "_ResidenceStateScore" )
     * <p>
     * RESIDENCESTATE( value == "KN" )
     * then
     * <p>
     * $statusHolder.setStatus("_ResidenceStateScore_1");
     * $statusHolder.accumulate("10.0");
     * update($statusHolder);
     * <p>
     * end
     * <p>
     * end
     * @param statusToSet
     * @return
     */
    protected KiePMMLDroolsRule.Builder getBuilderForSimplePredicate(final String statusToSet) {
        logger.trace("getBuilderForSimplePredicate {}", statusToSet);
        String statusConstraint = StringUtils.isEmpty(predicateASTFactoryData.getParentPath()) ? KiePMMLAbstractModelASTFactory.STATUS_NULL : String.format(STATUS_PATTERN, predicateASTFactoryData.getParentPath());
        String key = predicateASTFactoryData.getFieldTypeMap().get(((SimplePredicate) predicateASTFactoryData.getPredicate()).getField()).getGeneratedType();
        OPERATOR operator = OPERATOR.byName(((SimplePredicate) predicateASTFactoryData.getPredicate()).getOperator().value());
        Object value = KiePMMLASTFactoryUtils.getCorrectlyFormattedObject(((SimplePredicate) predicateASTFactoryData.getPredicate()), predicateASTFactoryData.getFieldTypeMap());
        List<KiePMMLFieldOperatorValue> andConstraints = Collections.singletonList(new KiePMMLFieldOperatorValue(key, BOOLEAN_OPERATOR.AND, Collections.singletonList(new KiePMMLOperatorValue(operator, value)), null));
        return KiePMMLDroolsRule.builder(predicateASTFactoryData.getCurrentRule(), statusToSet, predicateASTFactoryData.getOutputFields())
                .withStatusConstraint(statusConstraint)
                .withAndConstraints(andConstraints);
    }
}
