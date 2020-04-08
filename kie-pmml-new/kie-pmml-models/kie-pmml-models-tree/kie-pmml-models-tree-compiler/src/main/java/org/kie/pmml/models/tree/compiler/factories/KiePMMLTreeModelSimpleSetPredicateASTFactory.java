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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.stream.Collectors;

import org.dmg.pmml.SimpleSetPredicate;
import org.drools.core.util.StringUtils;
import org.kie.pmml.commons.enums.ResultCode;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.STATUS_NULL;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.STATUS_PATTERN;

/**
 * Class used to generate <code>KiePMMLDrooledRule</code> out of a <code>SimpleSetPredicate</code>
 */
public class KiePMMLTreeModelSimpleSetPredicateASTFactory extends KiePMMLTreeModeAbstractPredicateASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelSimpleSetPredicateASTFactory.class.getName());

    private final SimpleSetPredicate simpleSetPredicate;

    private KiePMMLTreeModelSimpleSetPredicateASTFactory(final SimpleSetPredicate simpleSetPredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final Queue<KiePMMLDrooledRule> rules) {
        super(fieldTypeMap, outputFields, rules);
        this.simpleSetPredicate = simpleSetPredicate;
    }

    public static KiePMMLTreeModelSimpleSetPredicateASTFactory factory(final SimpleSetPredicate simpleSetPredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final Queue<KiePMMLDrooledRule> rules) {
        return new KiePMMLTreeModelSimpleSetPredicateASTFactory(simpleSetPredicate, fieldTypeMap, outputFields, rules);
    }

    public void declareRuleFromSimpleSetPredicate(final String parentPath,
                                                  final String currentRule,
                                                  final Object result,
                                                  boolean isFinalLeaf) {
        logger.debug("declareRuleFromSimpleSetPredicate {} {} {}", simpleSetPredicate, parentPath, currentRule);
        String statusConstraint = StringUtils.isEmpty(parentPath) ? STATUS_NULL : String.format(STATUS_PATTERN, parentPath);
        String key = fieldTypeMap.get(simpleSetPredicate.getField().getValue()).getGeneratedType();
        String stringValue = (String) simpleSetPredicate.getArray().getValue();
        String[] valuesArray = stringValue.split(" ");
        List<Object> value = Arrays.asList(valuesArray).stream().map(rawValue -> {
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
        KiePMMLDrooledRule.Builder builder = KiePMMLDrooledRule.builder(currentRule, statusToSet, outputFields)
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
