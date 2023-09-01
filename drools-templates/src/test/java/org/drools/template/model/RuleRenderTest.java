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
 * Tests how the rule parse tree renders itself to a rule XML fragment.
 */
public class RuleRenderTest {

    @Test
    public void testRuleRender() {
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
        cons.setSnippet("cons snippet;");
        rule.addConsequence(cons);
        rule.addConsequence(cons);

        final DRLOutput out = new DRLOutput();
        rule.renderDRL(out);
        final String drl = out.getDRL();
        assertThat(drl).isNotNull();

        assertThat(drl.indexOf("cond snippet") != -1).isTrue();
        assertThat(drl.indexOf("cons snippet") != -1).isTrue();
        assertThat(drl.indexOf("salience 42") != -1).isTrue();
        assertThat(drl.indexOf("salience 42") < drl.indexOf("when")).isTrue();
        assertThat(drl.indexOf("cond snippet") < drl.indexOf("then")).isTrue();
        assertThat(drl.indexOf("cons snippet;") > drl.indexOf("then")).isTrue();
        assertThat(drl.indexOf("rule") != -1).isTrue();
        assertThat(drl.indexOf("end") > drl.indexOf("rule ")).isTrue();
        assertThat(drl.indexOf("//rule comments") > -1).isTrue();

    }

    @Test
    public void testAttributes() throws Exception {
        Rule rule = new Rule("la",
                42,
                2);

        rule.setActivationGroup("foo");
        rule.setNoLoop(true);
        rule.setRuleFlowGroup("ruleflowgroup");
        rule.setDuration(42L);
        DRLOutput out = new DRLOutput();
        rule.renderDRL(out);

        String result = out.toString();

        assertThat(result.indexOf("ruleflow-group \"ruleflowgroup\"") > -1).isTrue();
        assertThat(result.indexOf("no-loop true") > -1).isTrue();
        assertThat(result.indexOf("activation-group \"foo\"") > -1).isTrue();
        assertThat(result.indexOf("duration 42") > -1).isTrue();

    }

    @Test
    public void testMetadata() throws Exception {
        Rule rule = new Rule("la", 42, 2);

        rule.addMetadata("Author( A. U. Thor )");
        rule.addMetadata("Revision( 42 )");
        DRLOutput out = new DRLOutput();
        rule.renderDRL(out);

        String result = out.toString();
        assertThat(result.contains("@Author( A. U. Thor )")).isTrue();
        assertThat(result.contains("@Revision( 42 )")).isTrue();

    }

    @Test
    public void testNotEscapeChars() {
        //bit of a legacy from the olde XML dayes of yesteryeare
        final Condition cond = new Condition();
        cond.setSnippet("a < b");
        final DRLOutput out = new DRLOutput();
        cond.renderDRL(out);

        assertThat(out.toString().indexOf("a < b") != -1).isTrue();

    }

    /**
     * This checks that if the rule has "nil" salience, then
     * no salience value should be put in the rule definition.
     * This allows default salience to work as advertised.
     */
    @Test
    public void testNilSalience() {
        Rule rule = new Rule("MyRule",
                null,
                1);

        DRLOutput out = new DRLOutput();
        rule.renderDRL(out);
        String xml = out.toString();
        int idx = xml.indexOf("salience");
        assertThat(idx).isEqualTo(-1);

        rule = new Rule("MyRule",
                42,
                1);
        out = new DRLOutput();
        rule.renderDRL(out);
        xml = out.toString();
        idx = xml.indexOf("salience");
        assertThat(idx > -1).isTrue();
    }

}
