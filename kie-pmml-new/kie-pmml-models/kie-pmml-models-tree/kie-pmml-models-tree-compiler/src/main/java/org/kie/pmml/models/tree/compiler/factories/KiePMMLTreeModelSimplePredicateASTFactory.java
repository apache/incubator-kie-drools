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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.dmg.pmml.SimplePredicate;
import org.drools.core.util.StringUtils;
import org.kie.pmml.commons.enums.StatusCode;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.kie.pmml.models.drooled.tuples.KiePMMLOperatorValue;
import org.kie.pmml.models.drooled.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.pmml.models.tree.model.enums.OPERATOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.STATUS_NULL;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.STATUS_PATTERN;
import static org.kie.pmml.models.tree.compiler.factories.KiePMMLTreeModelASTFactory.SURROGATE_PATTERN;

/**
 * Class used to generate <code>KiePMMLDrooledRule</code> out of a <code>SimplePredicate</code>
 */
public class KiePMMLTreeModelSimplePredicateASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelSimplePredicateASTFactory.class.getName());

    private final SimplePredicate simplePredicate;
    private final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap;
    private final Queue<KiePMMLDrooledRule> rules;

    private KiePMMLTreeModelSimplePredicateASTFactory(final SimplePredicate simplePredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final Queue<KiePMMLDrooledRule> rules) {
        this.simplePredicate = simplePredicate;
        this.fieldTypeMap = fieldTypeMap;
        this.rules = rules;
    }

    public static KiePMMLTreeModelSimplePredicateASTFactory factory(final SimplePredicate simplePredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final Queue<KiePMMLDrooledRule> rules) {
        return new KiePMMLTreeModelSimplePredicateASTFactory(simplePredicate, fieldTypeMap, rules);
    }

    public void declareRuleFromSimplePredicateSurrogate(final String parentPath,
                                                        final String currentRule,
                                                        final String statusToSet,
                                                        final Object result) {
        logger.info("declareRuleFromSimplePredicateSurrogate {} {} {} {} {}", simplePredicate, parentPath, currentRule, statusToSet, result);
        String statusConstraint = StringUtils.isEmpty(parentPath) ? STATUS_NULL : String.format(STATUS_PATTERN, parentPath);
        String ifBreakField = fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType();
        String ifBreakOperator = OPERATOR.byName(simplePredicate.getOperator().value()).getOperator();
        Object ifBreakValue = simplePredicate.getValue();
        String surrogateCurrentRule = String.format(SURROGATE_PATTERN, currentRule, ifBreakField);
        KiePMMLDrooledRule.Builder builder = KiePMMLDrooledRule.builder(surrogateCurrentRule, statusToSet)
                .withStatusConstraint(statusConstraint)
                .withIfBreak(ifBreakField, ifBreakOperator, ifBreakValue)
                .withResult(result)
                .withResultCode(StatusCode.OK);
        rules.add(builder.build());
    }

    public void declareRuleFromSimplePredicate(final String parentPath,
                                               final String currentRule,
                                               final Object result,
                                               boolean isFinalLeaf) {
        logger.info("declareRuleFromSimplePredicate {} {} {}", simplePredicate, parentPath, currentRule);
        String statusConstraint = StringUtils.isEmpty(parentPath) ? STATUS_NULL : String.format(STATUS_PATTERN, parentPath);
        String key = fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType();
        String operator = OPERATOR.byName(simplePredicate.getOperator().value()).getOperator();
        Object value = simplePredicate.getValue();
        if (fieldTypeMap.get(simplePredicate.getField().getValue()).getOriginalType().equals("string")) {
            value = "\"" + value + "\"";
        }
        String statusToSet = isFinalLeaf ? StatusCode.DONE.getName() : currentRule;
        Map<String, List<KiePMMLOperatorValue>> andConstraints = Collections.singletonMap(key, Collections.singletonList(new KiePMMLOperatorValue(operator, value)));
        KiePMMLDrooledRule.Builder builder = KiePMMLDrooledRule.builder(currentRule, statusToSet)
                .withStatusConstraint(statusConstraint)
                .withAndConstraints(andConstraints);
        if (isFinalLeaf) {
            builder = builder.withResult(result)
                    .withResultCode(StatusCode.OK);
        }
        rules.add(builder.build());
    }
}
