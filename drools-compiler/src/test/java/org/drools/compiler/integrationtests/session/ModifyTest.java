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

package org.drools.compiler.integrationtests.session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Address;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.IndexedNumber;
import org.drools.compiler.OuterClass;
import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

public class ModifyTest extends CommonTestMethodBase {

    @Test
    public void testModifyBlock() throws Exception {
        doModifyTest("test_ModifyBlock.drl");
    }

    @Test
    public void testModifyBlockWithPolymorphism() throws Exception {
        doModifyTest("test_ModifyBlockWithPolymorphism.drl");
    }

    private void doModifyTest(final String drlResource) throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase(drlResource));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Person bob = new Person("Bob");
        bob.setStatus("hungry");

        final Cheese c = new Cheese();

        ksession.insert(bob);
        ksession.insert(c);

        ksession.fireAllRules();

        assertEquals(10, c.getPrice());
        assertEquals("fine", bob.getStatus());
    }

    @Test
    public void testModifyBlockWithFrom() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_ModifyBlockWithFrom.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        final Person bob = new Person("Bob");
        final Address addr = new Address("abc");
        bob.addAddress(addr);

        ksession.insert(bob);
        ksession.insert(addr);

        ksession.fireAllRules();

        // modify worked
        assertEquals("12345", addr.getZipCode());
        // chaining worked
        assertEquals(1, results.size());
        assertEquals(addr, results.get(0));
    }

    // this test requires mvel 1.2.19. Leaving it commented until mvel is released.
    @Test
    public void testJavaModifyBlock() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_JavaModifyBlock.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Person bob = new Person("Bob", 30);
        bob.setStatus("hungry");
        ksession.insert(bob);
        ksession.insert(new Cheese());
        ksession.insert(new Cheese());
        ksession.insert(new OuterClass.InnerClass(1));

        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertEquals("full", bob.getStatus());
        assertEquals(31, bob.getAge());
        assertEquals(2, ((OuterClass.InnerClass) list.get(1)).getIntAttr());
    }

    @Test
    public void testModifyJava() {
        testModifyWithDialect("java");
    }

    @Test
    public void testModifyMVEL() {
        testModifyWithDialect("mvel");
    }

    private void testModifyWithDialect(final String dialect) {
        final String str = "package org.drools.compiler\n" +
                "import java.util.List\n" +
                "rule \"test\"\n" +
                "    dialect \"" + dialect + "\"\n" +
                "when\n" +
                "    $l : List() from collect ( Person( alive == false ) );\n" +
                "then\n" +
                "    for(Object p : $l ) {\n" +
                "        Person p2 = (Person) p;\n" +
                "        modify(p2) { setAlive(true) }\n" +
                "    }\n" +
                "end";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);
        Assert.assertFalse(kbuilder.getErrors().toString(), kbuilder.hasErrors());
    }

    @Test
    public void testModifySimple() {
        final String str = "package org.drools.compiler;\n" +
                "\n" +
                "rule \"test modify block\"\n" +
                "when\n" +
                "    $p: Person( name == \"hungry\" )\n" +
                "then\n" +
                "    modify( $p ) { setName(\"fine\") }\n" +
                "end\n" +
                "\n" +
                "rule \"Log\"\n" +
                "when\n" +
                "    $o: Object()\n" +
                "then\n" +
                "    System.out.println( $o );\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final Person p = new Person("hungry");
        ksession.insert(p);
        ksession.fireAllRules();
        ksession.dispose();
    }

    @Test
    public void testModifyWithLockOnActive() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_ModifyWithLockOnActive.drl"));
        final KieSession session = createKnowledgeSession(kbase);

        final List results = new ArrayList();
        session.setGlobal("results", results);

        final Person bob = new Person("Bob", 15);
        final Person mark = new Person("Mark", 16);
        final Person michael = new Person("Michael", 14);
        session.insert(bob);
        session.insert(mark);
        session.insert(michael);
        session.getAgenda().getAgendaGroup("feeding").setFocus();
        session.fireAllRules(5);

        assertEquals(2, ((List) session.getGlobal("results")).size());
    }

    @Test
    public void testMissingClosingBraceOnModify() throws Exception {
        // JBRULES-3436
        final String str = "package org.drools.compiler.test;\n" +
                "import org.drools.compiler.*\n" +
                "rule R1 when\n" +
                "   $p : Person( )" +
                "   $c : Cheese( )" +
                "then\n" +
                "   modify($p) { setCheese($c) ;\n" +
                "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes()), ResourceType.DRL);

        assertTrue(kbuilder.hasErrors());
    }

    @Test
    public void testInvalidModify1() throws Exception {
        String str = "";
        str += "package org.drools.compiler \n";
        str += "import " + Cheese.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    no-loop \n";
        str += "when \n";
        str += "    $i : Cheese() \n";
        str += "then \n";
        str += "    modify( $i ); ";
        str += "    list.add( $i ); \n";
        str += "end \n";

        testInvalidDrl(str);
    }

    @Test
    public void testInvalidModify2() throws Exception {
        String str = "";
        str += "package org.drools.compiler \n";
        str += "import " + Cheese.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    no-loop \n";
        str += "when \n";
        str += "    $i : Cheese() \n";
        str += "then \n";
        str += "    modify( $i ) { setType( \"stilton\" ); setType( \"stilton\" );}; ";
        str += "    list.add( $i ); \n";
        str += "end \n";

        testInvalidDrl(str);
    }

    @Test
    public void testJoinNodeModifyObject() throws IOException, ClassNotFoundException {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_JoinNodeModifyObject.drl"));
        final KieSession ksession = kbase.newKieSession();

        try {
            final List orderedFacts = new ArrayList();
            final List errors = new ArrayList();
            ksession.setGlobal("orderedNumbers", orderedFacts);
            ksession.setGlobal("errors", errors);
            final int MAX = 2;
            for (int i = 1; i <= MAX; i++) {
                final IndexedNumber n = new IndexedNumber(i, MAX - i + 1);
                ksession.insert(n);
            }
            ksession.fireAllRules();
            assertTrue("Processing generated errors: " + errors.toString(), errors.isEmpty());
            for (int i = 1; i <= MAX; i++) {
                final IndexedNumber n = (IndexedNumber) orderedFacts.get(i - 1);
                assertEquals("Fact is out of order", i, n.getIndex());
            }
        } finally {
        }
    }
}
