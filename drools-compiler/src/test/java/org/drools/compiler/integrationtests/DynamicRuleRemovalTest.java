package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.core.impl.InternalKnowledgeBase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBaseFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class DynamicRuleRemovalTest extends CommonTestMethodBase {

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
        kbase.addKnowledgePackages(loadKnowledgePackagesFromString(rule));
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
