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
package org.kie.pmml.commons.factories;

import java.util.Queue;

import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.kie.pmml.models.drooled.ast.KiePMMLDrooledRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate <b>Rules</b> (descr) out of a <b>Queue&lt;KiePMMLDrooledRule&gt;</b>
 */
public class KiePMMLDescrRulesFactory {

    public static final String STATUS_HOLDER = "$statusHolder";
    public static final String BREAK_LABEL = "match";

    private static final Logger logger = LoggerFactory.getLogger(KiePMMLDescrRulesFactory.class.getName());

    final PackageDescrBuilder builder;

    private KiePMMLDescrRulesFactory(final PackageDescrBuilder builder) {
        this.builder = builder;
    }

    public static KiePMMLDescrRulesFactory factory(final PackageDescrBuilder builder) {
        return new KiePMMLDescrRulesFactory(builder);
    }

    public void declareRules(final Queue<KiePMMLDrooledRule> rules) {
        logger.info("declareRules {}", rules);
        rules.forEach(this::declareRule);
    }

    protected void declareRule(final KiePMMLDrooledRule rule) {
        logger.info("declareRule {}", rule);
        final RuleDescrBuilder ruleBuilder = builder.newRule().name(rule.getName());
        if (rule.getAgendaGroup() != null) {
            declareAgendaGroup(ruleBuilder, rule.getAgendaGroup());
        }
        if (rule.getActivationGroup() != null) {
            declareActivationGroup(ruleBuilder, rule.getActivationGroup());
        }
        KiePMMLDescrLhsFactory.factory(ruleBuilder.lhs()).declareLhs(rule);
        KiePMMLDescrRhsFactory.factory(ruleBuilder).declareRhs(rule);
    }

    protected void declareAgendaGroup(final RuleDescrBuilder ruleBuilder, final String agendaGroup) {
        ruleBuilder.attribute("agenda-group").type(AttributeDescr.Type.STRING).value(agendaGroup);
    }

    protected void declareActivationGroup(final RuleDescrBuilder ruleBuilder, final String activationGroup) {
        ruleBuilder.attribute("activation-group").type(AttributeDescr.Type.STRING).value(activationGroup);
    }
}
