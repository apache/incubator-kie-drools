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
package org.drools.template.model;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test rendering and running a whole sample ruleset, from the model classes
 * down.
 */
public class PackageRenderTest {

    public Rule buildRule() {
        final Rule rule = new Rule("myrule",
                42,
                1);
        rule.setComment("rule comments");

        final Condition cond = new Condition();
        cond.setComment("cond comment");
        cond.setSnippet("cond snippet");
        rule.addCondition(cond);

        final Consequence cons = new Consequence();
        cons.setComment("cons comment");
        cons.setSnippet("cons snippet");
        rule.addConsequence(cons);

        return rule;
    }

    @Test
    public void testRulesetRender() {
        final Package ruleSet = new Package("my ruleset");
        ruleSet.addFunctions("my functions");
        ruleSet.addRule(buildRule());

        final Rule rule = buildRule();
        rule.setName("other rule");
        ruleSet.addRule(rule);

        final Import imp = new Import();
        imp.setClassName("clazz name");
        imp.setComment("import comment");
        ruleSet.addImport(imp);

        final DRLOutput out = new DRLOutput();
        ruleSet.renderDRL(out);

        final String drl = out.getDRL();
        assertThat(drl).isNotNull();
        System.out.println(drl);
        assertThat(drl.indexOf("rule \"myrule\"") > -1).isTrue();
        assertThat(drl.indexOf("salience 42") > -1).isTrue();
        assertThat(drl.indexOf("//rule comments") > -1).isTrue();
        assertThat(drl.indexOf("my functions") > -1).isTrue();
        assertThat(drl.indexOf("package my_ruleset;") > -1).isTrue();
        assertThat(drl.indexOf("rule \"other rule\"") > drl.indexOf("rule \"myrule\"")).isTrue();
    }

    @Test
    public void testRulesetAttribute() {
        final Package ruleSet = new Package("my ruleset");
        ruleSet.setAgendaGroup("agroup");
        ruleSet.setNoLoop(true);
        ruleSet.setSalience(100);
        final DRLOutput out = new DRLOutput();
        ruleSet.renderDRL(out);

        final String drl = out.getDRL();
        assertThat(drl.contains("agenda-group \"agroup\"")).isTrue();
        assertThat(drl.contains("no-loop true")).isTrue();
        assertThat(drl.contains("salience 100")).isTrue();
    }
}
