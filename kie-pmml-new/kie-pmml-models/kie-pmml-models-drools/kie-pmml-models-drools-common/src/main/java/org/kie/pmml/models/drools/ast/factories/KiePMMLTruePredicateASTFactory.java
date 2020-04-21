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

import java.util.Collections;
import java.util.List;

import org.dmg.pmml.True;
import org.drools.core.util.StringUtils;
import org.kie.pmml.commons.enums.ResultCode;
import org.kie.pmml.commons.model.KiePMMLOutputField;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.Constants.DONE;

/**
 * Class used to generate a <code>KiePMMLDroolsRule</code> out of a <code>True</code> predicate
 */
public class KiePMMLTruePredicateASTFactory extends KiePMMLAbstractPredicateASTFactory {

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLTruePredicateASTFactory.class.getName());

    private final True truePredicate;

    private KiePMMLTruePredicateASTFactory(final True truePredicate, final List<KiePMMLOutputField> outputFields, final List<KiePMMLDroolsRule> rules) {
        super(Collections.emptyMap(), outputFields, rules);
        this.truePredicate = truePredicate;
    }

    public static KiePMMLTruePredicateASTFactory factory(final True truePredicate, final List<KiePMMLOutputField> outputFields, final List<KiePMMLDroolsRule> rules) {
        return new KiePMMLTruePredicateASTFactory(truePredicate, outputFields, rules);
    }

    public void declareRuleFromTruePredicate(final String parentPath,
                                             final String currentRule,
                                             final Object result,
                                             boolean isFinalLeaf) {
        logger.trace("declareRuleFromTruePredicate {} {} {}", truePredicate, parentPath, currentRule);
        String statusConstraint = StringUtils.isEmpty(parentPath) ? KiePMMLAbstractModelASTFactory.STATUS_NULL : String.format(KiePMMLAbstractModelASTFactory.STATUS_PATTERN, parentPath);
        String statusToSet = isFinalLeaf ? DONE : currentRule;
        KiePMMLDroolsRule.Builder builder = KiePMMLDroolsRule.builder(currentRule, statusToSet, outputFields)
                .withStatusConstraint(statusConstraint);
        if (isFinalLeaf) {
            builder = builder.withResult(result)
                    .withResultCode(ResultCode.OK);
        }
        rules.add(builder.build());
    }
}
