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
package org.kie.pmml.models.drools.commons.factories;

import java.util.List;

import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.RuleDescrBuilder;
import org.drools.drl.ast.descr.AttributeDescr;
import org.kie.pmml.models.drools.ast.KiePMMLDroolsRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class used to generate <b>Rules</b> (descr) out of a <b>List&lt;KiePMMLDroolsRule&gt;</b>
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

    public void declareRules(final List<KiePMMLDroolsRule> rules) {
        logger.trace("declareRules {}", rules);
        rules.forEach(this::declareRule);
    }

    protected void declareRule(final KiePMMLDroolsRule rule) {
        logger.trace("declareRule {}", rule);
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
