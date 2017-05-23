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

package org.drools.compiler.integrationtests.operators.eval;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.Cheese;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.FactA;
import org.drools.compiler.FactB;
import org.drools.compiler.FactC;
import org.drools.compiler.Person;
import org.drools.compiler.TestParam;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.rule.builder.dialect.java.JavaDialectConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

public class EvalTest extends CommonTestMethodBase {

    @Test
    public void testEval() throws Exception {
        final KieBase kbase = loadKnowledgeBase("eval_rule_test.drl");
        KieSession ksession = kbase.newKieSession();

        ksession.setGlobal("five", 5);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Cheese stilton = new Cheese("stilton", 5);
        ksession.insert(stilton);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        assertEquals(stilton, ((List) ksession.getGlobal("list")).get(0));
    }

    @Test
    public void testEvalNoPatterns() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_NoPatterns.drl"));
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertTrue(list.contains("fired1"));
        assertTrue(list.contains("fired3"));
    }

    @Test
    public void testJaninoEval() throws Exception {
        final KnowledgeBuilderConfiguration kbconf = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration();
        kbconf.setProperty(JavaDialectConfiguration.JAVA_COMPILER_PROPERTY, "JANINO");
        KieBase kbase = loadKnowledgeBase(kbconf, "eval_rule_test.drl");

        kbase = SerializationHelper.serializeObject(kbase);
        KieSession ksession = kbase.newKieSession();

        ksession.setGlobal("five", 5);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Cheese stilton = new Cheese("stilton", 5);
        ksession.insert(stilton);
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        assertEquals(stilton, ((List) ksession.getGlobal("list")).get(0));
    }

    @Test
    public void testEvalMore() throws Exception {
        final KieBase kbase = loadKnowledgeBase("eval_rule_test_more.drl");
        KieSession session = kbase.newKieSession();

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final Person foo = new Person("foo");
        session.insert(foo);
        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        session.fireAllRules();

        assertEquals(foo, ((List) session.getGlobal("list")).get(0));
    }

    @Test
    public void testEvalCE() throws Exception {
        final String text = "package org.drools.compiler\n" +
                "rule \"inline eval\"\n" +
                "when\n" +
                "    $str : String()\n" +
                "    $p   : Person()\n" +
                "    eval( $p.getName().startsWith($str) && $p.getName().endsWith($str) )" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(text);
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert("b");

        ksession.insert(new Person("mark", 50));
        int rules = ksession.fireAllRules();
        assertEquals(0, rules);

        ksession.insert(new Person("bob", 18));
        rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testEvalException() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_EvalException.drl"));
        final KieSession ksession = kbase.newKieSession();

        final Cheese brie = new Cheese("brie", 12);
        try {
            ksession.insert(brie);
            ksession.fireAllRules();
            fail("Should throw an Exception from the Eval");
        } catch (final Exception e) {
            assertEquals("this should throw an exception", e.getCause().getMessage());
        }
    }

    @Test
    public void testEvalInline() throws Exception {
        final String text = "package org.drools.compiler\n" +
                "rule \"inline eval\"\n" +
                "when\n" +
                "    $str : String()\n" +
                "    Person( eval( name.startsWith($str) && age == 18) )\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(text);
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.insert("b");

        ksession.insert(new Person("mark", 50));
        int rules = ksession.fireAllRules();
        assertEquals(0, rules);

        ksession.insert(new Person("bob", 18));
        rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    @Test
    public void testEvalWithLineBreaks() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_EvalWithLineBreaks.drl");
        final KieSession session = createKnowledgeSession(kbase);

        final List<Person> results = new ArrayList<Person>();
        session.setGlobal("results", results);

        session.insert(10);
        session.fireAllRules();

        assertEquals(1, results.size());
        assertEquals(10, results.get(0));
    }

    @Test
    public void testEvalWithBigDecimal() throws Exception {
        String str = "";
        str += "package org.drools.compiler \n";
        str += "import java.math.BigDecimal; \n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    dialect \"java\" \n";
        str += "when \n";
        str += "    $bd : BigDecimal() \n";
        str += "    eval( $bd.compareTo( BigDecimal.ZERO ) > 0 ) \n";
        str += "then \n";
        str += "    list.add( $bd ); \n";
        str += "end \n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);
        ksession.insert(new BigDecimal(1.5));

        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(new BigDecimal(1.5), list.get(0));
    }

    @Test
    public void testFieldBiningsAndEvalSharing() throws Exception {
        final String drl = "test_FieldBindingsAndEvalSharing.drl";
        evalSharingTest(drl);
    }

    @Test
    public void testFieldBiningsAndPredicateSharing() throws Exception {
        final String drl = "test_FieldBindingsAndPredicateSharing.drl";
        evalSharingTest(drl);
    }

    private void evalSharingTest(final String drl) throws Exception {
        final KieBase kbase = loadKnowledgeBase(drl);
        KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final TestParam tp1 = new TestParam();
        tp1.setValue2("boo");
        ksession.insert(tp1);

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        assertEquals(1, ((List) ksession.getGlobal("list")).size());
    }

    @Test
    public void testCastingInsideEvals() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_castsInsideEval.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        ksession.setGlobal("value", 20);

        ksession.fireAllRules();
    }

    @Test
    public void testAlphaEvalWithOrCE() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_AlphaEvalWithOrCE.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final FactA a = new FactA();
        a.setField1("a value");

        ksession.insert(a);
        ksession.insert(new FactB());
        ksession.insert(new FactC());

        ksession.fireAllRules();

        assertEquals("should not have fired", 0, list.size());
    }

    @Test
    public void testModifyWithLiaToEval() {
        String str = "";
        str += "package org.simple \n";
        str += "import " + Person.class.getCanonicalName() + "\n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "when \n";
        str += "    $p : Person() \n";
        str += "    eval( $p.getAge() > 30 ) \n";
        str += "then \n";
        str += "  list.add($p); \n";
        str += "end  \n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);

        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Person p1 = new Person("darth", 25);
        final FactHandle fh = ksession.insert(p1);
        ksession.fireAllRules();
        assertEquals(0, list.size());

        p1.setAge(35);
        ksession.update(fh, p1);
        ksession.fireAllRules();
        assertEquals(1, list.size());

        ksession.dispose();
    }

    @Test
    public void testBigDecimalWithFromAndEval() throws Exception {
        String rule = "package org.drools.compiler.test;\n";
        rule += "rule \"Test Rule\"\n";
        rule += "when\n";
        rule += "    $dec : java.math.BigDecimal() from java.math.BigDecimal.TEN;\n";
        rule += "    eval( $dec.compareTo(java.math.BigDecimal.ONE) > 0 )\n";
        rule += "then\n";
        rule += "    System.out.println(\"OK!\");\n";
        rule += "end";

        final KnowledgeBase kbase = SerializationHelper.serializeObject(loadKnowledgeBaseFromString(rule));
        final StatefulKnowledgeSession session = createKnowledgeSession(kbase);

        session.fireAllRules();
    }
}
