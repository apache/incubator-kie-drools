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
package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.builder.KieBuilder;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class DynamicRuleRemovalTest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testDynamicRuleRemoval(KieBaseTestConfiguration kieBaseTestConfiguration) throws Exception {
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();;
        addRule(kieBaseTestConfiguration, kbase, "rule1");
        addRule(kieBaseTestConfiguration, kbase, "rule2");
        addRule(kieBaseTestConfiguration, kbase, "rule3");

        final KieSession ksession = kbase.newKieSession();
        List<String> rulesList = new ArrayList<String>();
        ksession.setGlobal("list", rulesList);

        ksession.insert("2");
        ksession.fireAllRules();
        assertThat(rulesList.size()).isEqualTo(3);
        assertThat(rulesList.contains("rule1")).isTrue();
        assertThat(rulesList.contains("rule2")).isTrue();
        assertThat(rulesList.contains("rule3")).isTrue();

        removeRule(kbase, "rule1");

        rulesList.clear();
        ksession.insert("3");
        ksession.fireAllRules();
        assertThat(rulesList.size()).isEqualTo(2);
        assertThat(rulesList.contains("rule1")).isFalse();
        assertThat(rulesList.contains("rule2")).isTrue();
        assertThat(rulesList.contains("rule3")).isTrue();
    }

    private void addRule(KieBaseTestConfiguration kieBaseTestConfiguration, InternalKnowledgeBase kbase, String ruleName) {
        String rule = createDRL(ruleName);
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, true, rule);
        Collection<KiePackage> pkgs = KieBaseUtil.getDefaultKieBaseFromKieBuilder(kieBuilder).getKiePackages();
        kbase.addPackages(pkgs);
    }

    private void removeRule(InternalKnowledgeBase kbase, String ruleName) {
        kbase.removeRule("org.kie.test", ruleName);
    }

    private String createDRL(String ruleName) {
        return "package org.kie.test\n" +
               "global java.util.List list\n" +
               "rule " + ruleName + "\n" +
               "when\n" +
               "   $s: String()\n" +
               "then\n" +
               "list.add( drools.getRule().getName() );\n" +
               "end\n";
    }
}
