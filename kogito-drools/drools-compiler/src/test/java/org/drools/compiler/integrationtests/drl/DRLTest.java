/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.drl;

import static org.hamcrest.CoreMatchers.is;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.utils.KieHelper;

public class DRLTest extends CommonTestMethodBase {

    @Test(expected = RuntimeException.class)
    public void testDuplicateRuleName() {
        final String drl = "package org.drools\n" +
                "rule R when\n" +
                "then\n" +
                "end\n" +
                "rule R when\n" +
                "then\n" +
                "end\n";

        new KieHelper().addContent( drl, ResourceType.DRL ).build();
    }

    @Test
    public void testEmptyRule() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_EmptyRule.drl"));
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertTrue(list.contains("fired1"));
        assertTrue(list.contains("fired2"));
    }

    @Test
    public void testRuleMetaAttributes() throws Exception {
        String drl = "";
        drl += "package test\n";
        drl += "rule \"test meta attributes\"\n";
        drl += "    @id(1234 ) @author(  john_doe  ) @text(\"It's an escaped\\\" string\"  )\n";
        drl += "when\n";
        drl += "then\n";
        drl += "    // some comment\n";
        drl += "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(drl);

        final Rule rule = kbase.getRule("test", "test meta attributes");

        assertNotNull(rule);
        assertThat(rule.getMetaData().get("id"), is(1234));
        assertThat(rule.getMetaData().get("author"), is("john_doe"));
        assertThat(rule.getMetaData().get("text"), is("It's an escaped\" string"));
    }

    @Test
    public void testWithInvalidRule() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("invalid_rule.drl", getClass()), ResourceType.DRL);

        assertTrue(kbuilder.hasErrors());

        final String pretty = kbuilder.getErrors().toString();
        assertFalse(pretty.equals(""));
    }

    @Test
    public void testWithInvalidRule2() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("invalid_rule2.drl", getClass()), ResourceType.DRL);

        assertTrue(kbuilder.hasErrors());
    }

    @Test
    public void testDuplicateVariableBinding() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_duplicateVariableBinding.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final Map result = new HashMap();
        ksession.setGlobal("results", result);

        final Cheese stilton = new Cheese("stilton", 20);
        final Cheese brie = new Cheese("brie", 10);

        ksession.insert(stilton);
        ksession.insert(brie);

        ksession.fireAllRules();
        assertEquals(5, result.size());
        assertEquals(stilton.getPrice(), ((Integer) result.get(stilton.getType())).intValue());
        assertEquals(brie.getPrice(), ((Integer) result.get(brie.getType())).intValue());

        assertEquals(stilton.getPrice(), ((Integer) result.get(stilton)).intValue());
        assertEquals(brie.getPrice(), ((Integer) result.get(brie)).intValue());

        assertEquals(stilton.getPrice(), ((Integer) result.get("test3" + stilton.getType())).intValue());

        ksession.insert(new Person("bob", brie.getType()));
        ksession.fireAllRules();

        assertEquals(6, result.size());
        assertEquals(brie.getPrice(), ((Integer) result.get("test3" + brie.getType())).intValue());
    }

    @Test
    public void testDeclarationUsage() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("test_DeclarationUsage.drl", getClass()), ResourceType.DRL);
        assertTrue(kbuilder.hasErrors());
    }

    @Test
    public void testDeclarationNonExistingField() throws Exception {
        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("test_DeclarationOfNonExistingField.drl", getClass()), ResourceType.DRL);
        assertTrue(kbuilder.hasErrors());
    }

    @Test
    public void testDRLWithoutPackageDeclaration() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_NoPackageDeclaration.drl");

        // no package defined, so it is set to the default
        final FactType factType = kbase.getFactType("defaultpkg", "Person");
        assertNotNull(factType);
        final Object bob = factType.newInstance();
        factType.set(bob, "name", "Bob");
        factType.set(bob, "age", Integer.valueOf(30));

        final KieSession session = createKnowledgeSession(kbase);
        final List results = new ArrayList();
        session.setGlobal("results", results);

        session.insert(bob);
        session.fireAllRules();

        assertEquals(1, results.size());
        assertEquals(bob, results.get(0));
    }

    @Test
    public void testEventsInDifferentPackages() {
        final String str = "package org.drools.compiler.test\n" +
                "import org.drools.compiler.*\n" +
                "declare StockTick\n" +
                "    @role( event )\n" +
                "end\n" +
                "rule r1\n" +
                "when\n" +
                "then\n" +
                "    StockTick st = new StockTick();\n" +
                "    st.setCompany(\"RHT\");\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);

        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testPackageNameOfTheBeast() throws Exception {
        // JBRULES-2749 Various rules stop firing when they are in unlucky packagename and there is a function declared

        final String ruleFileContent1 = "package org.drools.integrationtests;\n" +
                "function void myFunction() {\n" +
                "}\n" +
                "declare MyDeclaredType\n" +
                "  someProperty: boolean\n" +
                "end";
        final String ruleFileContent2 = "package de.something;\n" + // FAILS
                //        String ruleFileContent2 = "package de.somethinga;\n" + // PASSES
                //        String ruleFileContent2 = "package de.somethingb;\n" + // PASSES
                //        String ruleFileContent2 = "package de.somethingc;\n" + // PASSES
                //        String ruleFileContent2 = "package de.somethingd;\n" + // PASSES
                //        String ruleFileContent2 = "package de.somethinge;\n" + // FAILS
                //        String ruleFileContent2 = "package de.somethingf;\n" + // FAILS
                //        String ruleFileContent2 = "package de.somethingg;\n" + // FAILS
                "import org.drools.integrationtests.*;\n" +
                "rule \"CheckMyDeclaredType\"\n" +
                "  when\n" +
                "    MyDeclaredType()\n" +
                "  then\n" +
                "    insertLogical(\"THIS-IS-MY-MARKER-STRING\");\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(ruleFileContent1, ruleFileContent2);
        final KieSession knowledgeSession = createKnowledgeSession(kbase);

        final FactType myDeclaredFactType = kbase.getFactType("org.drools.integrationtests", "MyDeclaredType");
        final Object myDeclaredFactInstance = myDeclaredFactType.newInstance();
        knowledgeSession.insert(myDeclaredFactInstance);

        final int rulesFired = knowledgeSession.fireAllRules();
        assertEquals(1, rulesFired);

        knowledgeSession.dispose();
    }
}
