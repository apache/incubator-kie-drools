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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.SimpleSetPredicate;
import org.drools.util.StringUtils;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.tuples.KiePMMLReasonCodeAndValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory.STATUS_NULL;
import static org.kie.pmml.models.drools.ast.factories.KiePMMLAbstractModelASTFactory.STATUS_PATTERN;

/**
 * Class used to generate <code>KiePMMLDroolsRule</code> out of a <code>SimpleSetPredicate</code>
 */
public class KiePMMLSimpleSetPredicateASTFactory extends KiePMMLAbstractPredicateASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLSimpleSetPredicateASTFactory.class.getName());

    private KiePMMLSimpleSetPredicateASTFactory(final PredicateASTFactoryData predicateASTFactoryData) {
        super(predicateASTFactoryData);
    }

    public static KiePMMLSimpleSetPredicateASTFactory factory(final PredicateASTFactoryData predicateASTFactoryData) {
        return new KiePMMLSimpleSetPredicateASTFactory(predicateASTFactoryData);
    }

    public void declareRuleFromSimpleSetPredicate(final Number toAccumulate,
                                                  final String statusToSet,
                                                  final KiePMMLReasonCodeAndValue reasonCodeAndValue,
                                                  final boolean isLastCharacteristic) {
        logger.trace("declareRuleFromSimpleSetPredicate {} {} {}", toAccumulate, statusToSet, isLastCharacteristic);
        KiePMMLDroolsRule.Builder builder = getBuilderForSimpleSetPredicate(statusToSet)
                .withAccumulation(toAccumulate);
        if (reasonCodeAndValue != null) {
            builder = builder.withReasonCodeAndValue(reasonCodeAndValue);
        }
        KiePMMLSimpleSetPredicateWithAccumulationASTFactory.declareRuleFromSimpleSetPredicate(builder, predicateASTFactoryData.getRules(), isLastCharacteristic);
    }

    public void declareRuleFromSimpleSetPredicate(final Object result,
                                                  boolean isFinalLeaf) {
        logger.trace("declareRuleFromSimpleSetPredicate {} {}", result, isFinalLeaf);
        String statusToSet = isFinalLeaf ? DONE : predicateASTFactoryData.getCurrentRule();
        KiePMMLDroolsRule.Builder builder = getBuilderForSimpleSetPredicate(statusToSet);
        KiePMMLSimpleSetPredicateWithResultASTFactory.declareRuleFromSimpleSetPredicate(builder, predicateASTFactoryData.getRules(), result, isFinalLeaf);
    }

    private KiePMMLDroolsRule.Builder getBuilderForSimpleSetPredicate(final String statusToSet) {
        logger.trace("declareRuleFromSimpleSetPredicate {}", statusToSet);
        String statusConstraint = StringUtils.isEmpty(predicateASTFactoryData.getParentPath()) ? STATUS_NULL : String.format(STATUS_PATTERN, predicateASTFactoryData.getParentPath());
        SimpleSetPredicate simpleSetPredicate = (SimpleSetPredicate) predicateASTFactoryData.getPredicate();
        String key = predicateASTFactoryData.getFieldTypeMap().get(simpleSetPredicate.getField()).getGeneratedType();
        String stringValue = (String) simpleSetPredicate.getArray().getValue();
        String[] valuesArray = stringValue.split(" ");
        List<Object> value = Arrays.stream(valuesArray).map(rawValue -> {
            String originalType = predicateASTFactoryData.getFieldTypeMap().get(simpleSetPredicate.getField()).getOriginalType();
            switch (originalType) {
                case "string":
                    return "\"" + rawValue + "\"";
                case "double":
                    return Double.valueOf(rawValue).toString();
                default:
                    return rawValue;
            }
        }).collect(Collectors.toList());
        Map<String, List<Object>> constraints = Collections.singletonMap(key, value);
        KiePMMLDroolsRule.Builder toReturn = KiePMMLDroolsRule.builder(predicateASTFactoryData.getCurrentRule(), statusToSet, predicateASTFactoryData.getOutputFields())
                .withStatusConstraint(statusConstraint);
        if (SimpleSetPredicate.BooleanOperator.IS_IN.equals(simpleSetPredicate.getBooleanOperator())) {
            toReturn = toReturn.withInConstraints(constraints);
        } else {
            toReturn = toReturn.withNotInConstraints(constraints);
        }
        return toReturn;
    }
}
