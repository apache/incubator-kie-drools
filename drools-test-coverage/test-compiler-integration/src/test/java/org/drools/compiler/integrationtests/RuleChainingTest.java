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
package org.drools.compiler.integrationtests;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.ObjectDeletedEvent;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.KieSession;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class RuleChainingTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RuleChainingTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testRuleChainingWithLogicalInserts() {
        final String drl = "package com.sample\n" +
                " \n" +
                "declare Some\n" +
                "    field : int\n" +
                "end\n" +
                " \n" +
                "rule \"init\"\n" +
                "salience 9999\n" +
                "    dialect \"mvel\"\n" +
                "    when\n" +
                "    then\n" +
                "        Some s0 = new Some();\n" +
                "        s0.field = 0;\n" +
                "        insertLogical(s0);\n" +
                "        Some s1 = new Some();\n" +
                "        s1.field = 1;\n" +
                "        insertLogical(s1);\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"r1\"\n" +
                "salience 100\n" +
                "when\n" +
                "    Some( field == 0 )\n" +
                "    Some( $f : field == 1)\n" +
                "then\n" +
                "    // noop\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"r2\"\n" +
                "salience 10\n" +
                "when\n" +
                "     $s : Some( $f : field == 0 )\n" +
                "then\n" +
                "    // noop\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("subnetwork-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            // create working memory mock listener
            final RuleRuntimeEventListener wml = Mockito.mock(RuleRuntimeEventListener.class);
            final AgendaEventListener ael = Mockito.mock(AgendaEventListener.class);

            ksession.addEventListener(wml);
            ksession.addEventListener(ael);

            final int fired = ksession.fireAllRules();
            assertThat(fired).isEqualTo(3);

            // capture the arguments and check that the rules fired in the proper sequence
            final ArgumentCaptor<AfterMatchFiredEvent> actvs = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
            verify(ael, times(3)).afterMatchFired(actvs.capture());
            final List<AfterMatchFiredEvent> values = actvs.getAllValues();
            assertThat(values.get(0).getMatch().getRule().getName()).isEqualTo("init");
            assertThat(values.get(1).getMatch().getRule().getName()).isEqualTo("r1");
            assertThat(values.get(2).getMatch().getRule().getName()).isEqualTo("r2");

            verify(ael, never()).matchCancelled(any(org.kie.api.event.rule.MatchCancelledEvent.class));
            verify(wml, times(2)).objectInserted(any(ObjectInsertedEvent.class));
            verify(wml, never()).objectDeleted(any(ObjectDeletedEvent.class));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testDoubleInsertLogical() {
        // DROOLS-7525
        final String drl =
                "package org.test;\n" +
                        "\n" +
                        "declare Fact\n" +
                        "    value : Integer\n" +
                        "end\n" +
                        "\n" +
                        "declare Logical\n" +
                        "    value : Integer\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Init\"\n" +
                        "  when\n" +
                        "  then\n" +
                        "    insert(new Fact(1));\n" +
                        "    insert(new Fact(2));\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Eliminate all\"\n" +
                        "  when\n" +
                        "    $fact : Fact($val : value)\n" +
                        "    not( Fact(value < $val) )\n" +
                        "    Logical(value == $val)\n" +
                        "  then\n" +
                        "    System.out.println(\"delete\" + $fact);\n" +
                        "    delete($fact);\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Logical\"\n" +
                        "  when\n" +
                        "    Fact(value==1)\n" +
                        "  then\n" +
                        "    insertLogical(new Logical(1));\n" +
                        "    insertLogical(new Logical(2));\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("logical-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();

        ksession.fireAllRules();

        // The retraction of Fact(1) should also cause the immediate deletion of both Logical(1) and Logical(2)
        // thus preventing rule "Eliminate all" to fire a second time and leaving Fact(2) in the working memory
        assertThat(ksession.getObjects()).hasSize(1);
        assertThat(ksession.getObjects().iterator().next().toString()).isEqualTo("Fact( value=2 )");
    }
}
