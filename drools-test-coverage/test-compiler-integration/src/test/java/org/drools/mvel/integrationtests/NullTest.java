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

package org.drools.mvel.integrationtests;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.mvel.compiler.Attribute;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Message;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.PersonInterface;
import org.drools.mvel.compiler.Primitives;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.conf.EqualityBehaviorOption;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class NullTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public NullTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testNullValuesIndexing() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_NullValuesIndexing.drl");
        KieSession ksession = kbase.newKieSession();

        // Adding person with null name and likes attributes
        final PersonInterface bob = new Person(null, null);
        bob.setStatus("P1");
        final PersonInterface pete = new Person(null, null);
        bob.setStatus("P2");
        ksession.insert(bob);
        ksession.insert(pete);

        ksession.fireAllRules();

        assertEquals("Indexing with null values is not working correctly.", "OK", bob.getStatus());
        assertEquals("Indexing with null values is not working correctly.", "OK", pete.getStatus());
    }

    @Test
    public void testNullBehaviour() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "null_behaviour.drl");
        KieSession session = kbase.newKieSession();

        final PersonInterface p1 = new Person("michael", "food", 40);
        final PersonInterface p2 = new Person(null, "drink", 30);
        session.insert(p1);
        session.insert(p2);

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        session.fireAllRules();
    }

    @Test
    public void testNullConstraint() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "null_constraint.drl");
        KieSession session = kbase.newKieSession();

        final List foo = new ArrayList();
        session.setGlobal("messages", foo);

        final PersonInterface p1 = new Person(null, "food", 40);
        final Primitives p2 = new Primitives();
        p2.setArrayAttribute(null);

        session.insert(p1);
        session.insert(p2);

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        session.fireAllRules();
        assertEquals(2, ((List) session.getGlobal("messages")).size());
    }

    @Test
    public void testNullBinding() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_nullBindings.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        ksession.insert(new Person("bob"));
        ksession.insert(new Person(null));

        ksession.fireAllRules();

        assertEquals(1, list.size());

        assertEquals("OK", list.get(0));
    }

    @Test
    public void testNullConstantLeft() {
        // JBRULES-3627
        final String str = "import org.drools.mvel.compiler.*;\n" +
                "rule R1 when\n" +
                "   $p : Person( null == name )\n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new Person(null));
        ksession.insert(new Person("Mark"));

        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testNullFieldOnCompositeSink() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_NullFieldOnCompositeSink.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        ksession.insert(new Attribute());
        ksession.insert(new Message());
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        assertEquals(1, ((List) ksession.getGlobal("list")).size());
        assertEquals("X", ((List) ksession.getGlobal("list")).get(0));
    }

    @Test
    public void testNullHandling() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_NullHandling.drl");
        KieSession session = kbase.newKieSession();

        final List list = new ArrayList();
        session.setGlobal("list", list);
        final Cheese nullCheese = new Cheese(null, 2);
        session.insert(nullCheese);

        final Person notNullPerson = new Person("shoes butt back");
        notNullPerson.setBigDecimal(new BigDecimal("42.42"));

        session.insert(notNullPerson);

        Person nullPerson = new Person("whee");
        nullPerson.setBigDecimal(null);

        session.insert(nullPerson);

        session = SerializationHelper.getSerialisedStatefulKnowledgeSession(session, true);
        session.fireAllRules();
        //System.out.println(((List) session.getGlobal("list")).get(0));
        assertEquals(3, ((List) session.getGlobal("list")).size());

        nullPerson = new Person(null);

        session.insert(nullPerson);
        session.fireAllRules();
        assertEquals(4, ((List) session.getGlobal("list")).size());
    }

    @Test
    public void testNullHashing() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_NullHashing.drl");
        KieSession ksession = kbase.newKieSession();

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        ksession.insert(new Cheese("stilton", 15));
        ksession.insert(new Cheese("", 10));
        ksession.insert(new Cheese(null, 8));

        ksession.fireAllRules();

        assertEquals(3, results.size());
    }

    @Test
    public void testBindingToNullFieldWithEquality() {
        // JBRULES-3396
        final String str = "package org.drools.mvel.compiler.test; \n" +
                "\n" +
                "global java.util.List list;" +
                "\n" +
                "declare Bean\n" +
                "  id    : String @key\n" +
                "  field : String\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when  \n" +
                "then\n" +
                "  insert( new Bean( \"x\" ) );\n" +
                "end\n" +
                "\n" +
                "rule \"Check\"\n" +
                "when\n" +
                "  $b : Bean( $fld : field )\n" +
                "then\n" +
                "  System.out.println( $fld );\n" +
                "  list.add( \"OK\" ); \n" +
                "end";

        KieBaseTestConfiguration equalityConfig = TestParametersUtil.getEqualityInstanceOf(kieBaseTestConfiguration);
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", equalityConfig, str);
        KieSession ksession = kbase.newKieSession();

        final java.util.List list = new java.util.ArrayList();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertTrue(list.contains("OK"));

        ksession.dispose();
    }

    @Test
    public void testArithmeticExpressionWithNull() {
        // JBRULES-3568
        final String str = "import " + PrimitiveBean.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "   PrimitiveBean(primitive/typed > 0.7)\n" +
                "then\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new PrimitiveBean(0.9, 1.1));
        ksession.insert(new PrimitiveBean(0.9, null));
        assertEquals(1, ksession.fireAllRules());
        ksession.dispose();
    }

    public static class PrimitiveBean {

        public final double primitive;
        public final Double typed;

        public PrimitiveBean(final double primitive, final Double typed) {
            this.primitive = primitive;
            this.typed = typed;
        }

        public double getPrimitive() {
            return primitive;
        }

        public Double getTyped() {
            return typed;
        }
    }
}
