/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.mvel.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.builder.KieBuilder;
import org.kie.api.definition.KiePackage;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class DynamicRuleRemovalTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DynamicRuleRemovalTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testDynamicRuleRemoval() throws Exception {
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        addRule(kbase, "rule1");
        addRule(kbase, "rule2");
        addRule(kbase, "rule3");

        final KieSession ksession = kbase.newKieSession();
        List<String> rulesList = new ArrayList<String>();
        ksession.setGlobal("list", rulesList);

        ksession.insert("2");
        ksession.fireAllRules();
        assertEquals(3, rulesList.size());
        assertTrue(rulesList.contains("rule1"));
        assertTrue(rulesList.contains("rule2"));
        assertTrue(rulesList.contains("rule3"));

        removeRule(kbase, "rule1");

        rulesList.clear();
        ksession.insert("3");
        ksession.fireAllRules();
        assertEquals(2, rulesList.size());
        assertFalse(rulesList.contains("rule1"));
        assertTrue(rulesList.contains("rule2"));
        assertTrue(rulesList.contains("rule3"));
    }

    private void addRule(InternalKnowledgeBase kbase, String ruleName) {
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
