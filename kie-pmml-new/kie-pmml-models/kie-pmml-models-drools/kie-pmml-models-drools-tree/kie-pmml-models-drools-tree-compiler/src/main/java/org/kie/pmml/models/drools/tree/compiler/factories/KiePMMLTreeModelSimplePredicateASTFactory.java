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
package org.kie.pmml.models.drools.tree.compiler.factories;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.dmg.pmml.SimplePredicate;
import org.drools.core.util.StringUtils;
import org.kie.pmml.commons.enums.ResultCode;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.kie.pmml.models.drools.ast.KiePMMLFieldOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOperatorValue;
import org.kie.pmml.models.drools.tuples.KiePMMLOriginalTypeGeneratedType;
import org.kie.pmml.models.drools.tree.model.enums.OPERATOR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.DONE;
import static org.kie.pmml.models.drools.tree.compiler.factories.KiePMMLTreeModelASTFactory.STATUS_NULL;
import static org.kie.pmml.models.drools.tree.compiler.factories.KiePMMLTreeModelASTFactory.STATUS_PATTERN;
import static org.kie.pmml.models.drools.tree.compiler.factories.KiePMMLTreeModelASTFactory.SURROGATE_RULENAME_PATTERN;

/**
 * Class used to generate <code>KiePMMLDroolsRule</code> out of a <code>SimplePredicate</code>
 */
public class KiePMMLTreeModelSimplePredicateASTFactory extends KiePMMLTreeModelAbstractPredicateASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTreeModelSimplePredicateASTFactory.class.getName());

    private final SimplePredicate simplePredicate;

    private KiePMMLTreeModelSimplePredicateASTFactory(final SimplePredicate simplePredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final List<KiePMMLDroolsRule> rules) {
        super(fieldTypeMap, outputFields, rules);
        this.simplePredicate = simplePredicate;
    }

    public static KiePMMLTreeModelSimplePredicateASTFactory factory(final SimplePredicate simplePredicate, final Map<String, KiePMMLOriginalTypeGeneratedType> fieldTypeMap, final List<KiePMMLOutputField> outputFields, final List<KiePMMLDroolsRule> rules) {
        return new KiePMMLTreeModelSimplePredicateASTFactory(simplePredicate, fieldTypeMap, outputFields, rules);
    }

    public void declareRuleFromSimplePredicateSurrogate(
            final String parentPath,
            final String currentRule,
            final String agendaActivationGroup,
            final Object result,
            boolean isFinalLeaf) {
        logger.trace("declareRuleFromSimplePredicateSurrogate {} {} {} {}", simplePredicate, currentRule, agendaActivationGroup, result);
        String fieldName = fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType();
        String surrogateCurrentRule = String.format(SURROGATE_RULENAME_PATTERN, currentRule, fieldName);
        final List<KiePMMLFieldOperatorValue> constraints = Collections.singletonList(KiePMMLASTFactoryUtils.getConstraintEntryFromSimplePredicates(fieldName, "surrogate", Collections.singletonList(simplePredicate), fieldTypeMap));
        String statusToSet = isFinalLeaf ? DONE : currentRule;
        // Create "TRUE" matcher
        KiePMMLDroolsRule.Builder builder = KiePMMLDroolsRule.builder(surrogateCurrentRule + "_TRUE", statusToSet, outputFields)
                .withAgendaGroup(agendaActivationGroup)
                .withActivationGroup(agendaActivationGroup)
                .withAndConstraints(constraints);
        if (isFinalLeaf) {
            builder = builder.withResult(result)
                    .withResultCode(ResultCode.OK);
        }
        rules.add(builder.build());
        // Create "FALSE" matcher
        builder = KiePMMLDroolsRule.builder(surrogateCurrentRule + "_FALSE", parentPath, outputFields)
                .withAgendaGroup(agendaActivationGroup)
                .withActivationGroup(agendaActivationGroup)
                .withNotConstraints(constraints);
        rules.add(builder.build());
    }

    public void declareRuleFromSimplePredicate(final String parentPath,
                                               final String currentRule,
                                               final Object result,
                                               boolean isFinalLeaf) {
        logger.trace("declareRuleFromSimplePredicate {} {} {}", simplePredicate, parentPath, currentRule);
        String statusConstraint = StringUtils.isEmpty(parentPath) ? STATUS_NULL : String.format(STATUS_PATTERN, parentPath);
        String key = fieldTypeMap.get(simplePredicate.getField().getValue()).getGeneratedType();
        String operator = OPERATOR.byName(simplePredicate.getOperator().value()).getOperator();
        Object value = KiePMMLASTFactoryUtils.getCorrectlyFormattedObject(simplePredicate, fieldTypeMap);
        String statusToSet = isFinalLeaf ? DONE : currentRule;
        List<KiePMMLFieldOperatorValue> andConstraints = Collections.singletonList(new KiePMMLFieldOperatorValue(key, "and", Collections.singletonList(new KiePMMLOperatorValue(operator, value)), null));
        KiePMMLDroolsRule.Builder builder = KiePMMLDroolsRule.builder(currentRule, statusToSet, outputFields)
                .withStatusConstraint(statusConstraint)
                .withAndConstraints(andConstraints);
        if (isFinalLeaf) {
            builder = builder.withResult(result)
                    .withResultCode(ResultCode.OK);
        }
        rules.add(builder.build());
    }
}
