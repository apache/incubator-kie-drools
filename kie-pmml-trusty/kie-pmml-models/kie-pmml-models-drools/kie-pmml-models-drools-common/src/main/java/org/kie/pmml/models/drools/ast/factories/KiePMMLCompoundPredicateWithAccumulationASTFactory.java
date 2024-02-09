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
import java.util.stream.Collectors;

import org.dmg.pmml.CompoundPredicate;
import org.dmg.pmml.Predicate;
import org.dmg.pmml.SimplePredicate;
import org.kie.pmml.api.enums.ResultCode;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.tuples.KiePMMLReasonCodeAndValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate <code>KiePMMLDroolsRule</code>s out of a <code>CompoundPredicate</code>
 */
public class KiePMMLCompoundPredicateWithAccumulationASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLCompoundPredicateWithAccumulationASTFactory.class.getName());

    /**
     * Method to be invoked when <b>compoundPredicate.getBooleanOperator()</b> is <code>SURROGATE</code>.
     * Throws exception otherwise
     * @param predicateASTFactoryData
     * @param agendaActivationGroup
     * @param toAccumulate
     * @param statusToSet
     * @param reasonCodeAndValue
     * @param isLastCharacteristic
     */
    public static void declareRuleFromCompoundPredicateSurrogate(final PredicateASTFactoryData predicateASTFactoryData,
                                                                 final String agendaActivationGroup,
                                                                 final Number toAccumulate,
                                                                 final String statusToSet,
                                                                 final KiePMMLReasonCodeAndValue reasonCodeAndValue,
                                                                 final boolean isLastCharacteristic) {
        logger.trace("declareRuleFromCompoundPredicateSurrogate {} {} {} {} {}", predicateASTFactoryData, agendaActivationGroup, toAccumulate, statusToSet, isLastCharacteristic);
        // Managing only SimplePredicates for the moment being
        CompoundPredicate compoundPredicate = (CompoundPredicate) predicateASTFactoryData.getPredicate();
        final List<Predicate> simplePredicates = compoundPredicate.getPredicates().stream().filter(predicate -> predicate instanceof SimplePredicate).collect(Collectors.toList());
        simplePredicates.forEach(predicate -> {
            SimplePredicate simplePredicate = (SimplePredicate) predicate;
            PredicateASTFactoryData newPredicateASTFactoryData = predicateASTFactoryData.cloneWithPredicate(simplePredicate);
            KiePMMLSimplePredicateASTFactory.factory(newPredicateASTFactoryData).declareRuleFromSimplePredicateSurrogate(agendaActivationGroup, toAccumulate, statusToSet, reasonCodeAndValue, isLastCharacteristic);
        });
    }

    /**
     * Method to be invoked when <b>compoundPredicate.getBooleanOperator()</b> is <code>AND</code>, <code>OR</code> or
     * <XOR>XOR</XOR>. Throws exception otherwise
     * @param builder
     * @param rules
     * @param reasonCodeAndValue
     * @param isLastCharacteristic
     */
    public static void declareRuleFromCompoundPredicateAndOrXor(KiePMMLDroolsRule.Builder builder,
                                                                final List<KiePMMLDroolsRule> rules,
                                                                final KiePMMLReasonCodeAndValue reasonCodeAndValue,
                                                                boolean isLastCharacteristic) {
        logger.trace("declareRuleFromCompoundPredicateAndOrXor {} {} {}", builder, rules, isLastCharacteristic);
        if (isLastCharacteristic) {
            builder = builder.withAccumulationResult(true)
                    .withResultCode(ResultCode.OK);
        }
        if (reasonCodeAndValue != null) {
            builder = builder.withReasonCodeAndValue(reasonCodeAndValue);
        }
        rules.add(builder.build());
    }
}
