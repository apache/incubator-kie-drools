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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.dmg.pmml.SimpleSetPredicate;
import org.drools.core.util.StringUtils;
import org.kie.pmml.commons.enums.ResultCode;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
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

    private final SimpleSetPredicate simpleSetPredicate;

    private KiePMMLSimpleSetPredicateASTFactory(final SimpleSetPredicate simpleSetPredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final List<KiePMMLDroolsRule> rules) {
        super(fieldTypeMap, outputFields, rules);
        this.simpleSetPredicate = simpleSetPredicate;
    }

    public static KiePMMLSimpleSetPredicateASTFactory factory(final SimpleSetPredicate simpleSetPredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final List<KiePMMLDroolsRule> rules) {
        return new KiePMMLSimpleSetPredicateASTFactory(simpleSetPredicate, fieldTypeMap, outputFields, rules);
    }

    public void declareRuleFromSimpleSetPredicate(final String parentPath,
                                                  final String currentRule,
                                                  final Object result,
                                                  boolean isFinalLeaf) {
        logger.trace("declareRuleFromSimpleSetPredicate {} {} {}", simpleSetPredicate, parentPath, currentRule);
        String statusConstraint = StringUtils.isEmpty(parentPath) ? STATUS_NULL : String.format(STATUS_PATTERN, parentPath);
        String key = fieldTypeMap.get(simpleSetPredicate.getField().getValue()).getGeneratedType();
        String stringValue = (String) simpleSetPredicate.getArray().getValue();
        String[] valuesArray = stringValue.split(" ");
        List<Object> value = Arrays.stream(valuesArray).map(rawValue -> {
            String originalType = fieldTypeMap.get(simpleSetPredicate.getField().getValue()).getOriginalType();
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
        String statusToSet = isFinalLeaf ? DONE : currentRule;
        KiePMMLDroolsRule.Builder builder = KiePMMLDroolsRule.builder(currentRule, statusToSet, outputFields)
                .withStatusConstraint(statusConstraint);
        if (SimpleSetPredicate.BooleanOperator.IS_IN.equals(simpleSetPredicate.getBooleanOperator())) {
            builder = builder.withInConstraints(constraints);
        } else {
            builder = builder.withNotInConstraints(constraints);
        }
        if (isFinalLeaf) {
            builder = builder.withResult(result)
                    .withResultCode(ResultCode.OK);
        }
        rules.add(builder.build());
    }
}
