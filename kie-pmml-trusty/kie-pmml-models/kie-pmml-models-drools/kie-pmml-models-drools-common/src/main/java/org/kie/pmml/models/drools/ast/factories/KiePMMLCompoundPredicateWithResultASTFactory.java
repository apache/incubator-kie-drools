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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate <code>KiePMMLDroolsRule</code>s out of a <code>CompoundPredicate</code>
 */
public class KiePMMLCompoundPredicateWithResultASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLCompoundPredicateWithResultASTFactory.class.getName());

    /**
     * Method to be invoked when <b>compoundPredicate.getBooleanOperator()</b> is <code>SURROGATE</code>.
     * Throws exception otherwise
     * @param predicateASTFactoryData
     * @param agendaActivationGroup
     * @param result
     * @param isFinalLeaf
     */
    public static void declareRuleFromCompoundPredicateSurrogate(final PredicateASTFactoryData predicateASTFactoryData,
                                                                 final String agendaActivationGroup,
                                                                 final Object result,
                                                                 boolean isFinalLeaf) {
        logger.trace("declareRuleFromCompoundPredicateSurrogate {} {} {} {}", predicateASTFactoryData, agendaActivationGroup, result, isFinalLeaf);
        // Managing only SimplePredicates for the moment being
        CompoundPredicate compoundPredicate = (CompoundPredicate) predicateASTFactoryData.getPredicate();
        final List<Predicate> simplePredicates = compoundPredicate.getPredicates().stream().filter(predicate -> predicate instanceof SimplePredicate).collect(Collectors.toList());
        simplePredicates.forEach(predicate -> {
            SimplePredicate simplePredicate = (SimplePredicate) predicate;
            PredicateASTFactoryData newPredicateASTFactoryData = predicateASTFactoryData.cloneWithPredicate(simplePredicate);
            KiePMMLSimplePredicateASTFactory.factory(newPredicateASTFactoryData).declareRuleFromSimplePredicateSurrogate(agendaActivationGroup, result, isFinalLeaf);
        });
    }

    /**
     * Method to be invoked when <b>compoundPredicate.getBooleanOperator()</b> is <code>AND</code>, <code>OR</code> or
     * <XOR>XOR</XOR>. Throws exception otherwise
     * @param builder
     * @param rules
     * @param result
     * @param isFinalLeaf
     */
    public static void declareRuleFromCompoundPredicateAndOrXor(KiePMMLDroolsRule.Builder builder,
                                                                final List<KiePMMLDroolsRule> rules,
                                                                final Object result,
                                                                boolean isFinalLeaf) {
        logger.trace("declareRuleFromCompoundPredicateAndOrXor {} {} {} {}", builder, rules, result, isFinalLeaf);
        if (isFinalLeaf) {
            builder = builder.withResult(result)
                    .withResultCode(ResultCode.OK);
        }
        rules.add(builder.build());
    }
}
