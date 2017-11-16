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

import java.util.ArrayList;
import java.util.List;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.PolymorphicFact;
import org.drools.compiler.Primitives;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.junit.Assert.assertEquals;

public class TypeCoercionTest extends CommonTestMethodBase {

    @Test
    public void testRuntimeTypeCoercion() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_RuntimeTypeCoercion.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final PolymorphicFact fact = new PolymorphicFact(10);
        final FactHandle handle = ksession.insert(fact);

        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals(fact.getData(), list.get(0));

        fact.setData("10");
        ksession.update(handle, fact);
        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertEquals(fact.getData(), list.get(1));

        fact.setData(Boolean.TRUE);
        ksession.update(handle, fact);

        assertEquals(2, list.size());
    }

    @Test
    public void testRuntimeTypeCoercion2() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_RuntimeTypeCoercion2.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Primitives fact = new Primitives();
        fact.setBooleanPrimitive(true);
        fact.setBooleanWrapper(Boolean.TRUE);
        fact.setObject(Boolean.TRUE);
        fact.setCharPrimitive('X');
        final FactHandle handle = ksession.insert(fact);

        ksession.fireAllRules();

        int index = 0;
        assertEquals(list.toString(), 4, list.size());
        assertEquals("boolean", list.get(index++));
        assertEquals("boolean wrapper", list.get(index++));
        assertEquals("boolean object", list.get(index++));
        assertEquals("char", list.get(index++));

        fact.setBooleanPrimitive(false);
        fact.setBooleanWrapper(null);
        fact.setCharPrimitive('\0');
        fact.setObject('X');
        ksession.update(handle, fact);
        ksession.fireAllRules();
        assertEquals(5, list.size());
        assertEquals("char object", list.get(index++));

        fact.setObject(null);
        ksession.update(handle, fact);
        ksession.fireAllRules();
        assertEquals(6, list.size());
        assertEquals("null object", list.get(index));
    }

    @Test
    public void testUnwantedCoersion() throws Exception {
        final String rule = "package org.drools.compiler\n" +
                "import " + InnerBean.class.getCanonicalName() + ";\n" +
                "import " + OuterBean.class.getCanonicalName() + ";\n" +
                "rule \"Test.Code One\"\n" +
                "when\n" +
                "   OuterBean($code : inner.code in (\"1.50\", \"2.50\"))\n" +
                "then\n" +
                "   System.out.println(\"Code compared values: 1.50, 2.50 - actual code value: \" + $code);\n" +
                "end\n" +
                "rule \"Test.Code Two\"\n" +
                "when\n" +
                "   OuterBean($code : inner.code in (\"1.5\", \"2.5\"))\n" +
                "then\n" +
                "   System.out.println(\"Code compared values: 1.5, 2.5 - actual code value: \" + $code);\n" +
                "end\n" +
                "rule \"Big Test ID One\"\n" +
                "when\n" +
                "   OuterBean($id : id in (\"3.5\", \"4.5\"))\n" +
                "then\n" +
                "   System.out.println(\"ID compared values: 3.5, 4.5 - actual ID value: \" + $id);\n" +
                "end\n" +
                "rule \"Big Test ID Two\"\n" +
                "when\n" +
                "   OuterBean($id : id in ( \"3.0\", \"4.0\"))\n" +
                "then\n" +
                "   System.out.println(\"ID compared values: 3.0, 4.0 - actual ID value: \" + $id);\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession ksession = kbase.newKieSession();

        final InnerBean innerTest = new InnerBean();
        innerTest.setCode("1.500");
        ksession.insert(innerTest);

        final OuterBean outerTest = new OuterBean();
        outerTest.setId("3");
        outerTest.setInner(innerTest);
        ksession.insert(outerTest);

        final OuterBean outerTest2 = new OuterBean();
        outerTest2.setId("3.0");
        outerTest2.setInner(innerTest);
        ksession.insert(outerTest2);

        final int rules = ksession.fireAllRules();
        assertEquals(1, rules);
    }

    public static class InnerBean {
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(final String code) {
            this.code = code;
        }
    }

    public static class OuterBean {
        private InnerBean inner;
        private String    id;

        public InnerBean getInner() {
            return inner;
        }

        public void setInner(final InnerBean inner) {
            this.inner = inner;
        }

        public String getId() {
            return id;
        }

        public void setId(final String id) {
            this.id = id;
        }
    }

    @Test
    public void testCoercionOfStringValueWithoutQuotes() throws Exception {
        // JBRULES-3080
        final String str = "package org.drools.compiler.test; \n" +
                "declare A\n" +
                "   field : String\n" +
                "end\n" +
                "rule R when\n" +
                "   A( field == 12 )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final FactType typeA = kbase.getFactType("org.drools.compiler.test", "A");
        final Object a = typeA.newInstance();
        typeA.set(a, "field", "12");
        ksession.insert(a);

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testPrimitiveToBoxedCoercionInMethodArgument() throws Exception {
        final String str = "package org.drools.compiler.test;\n" +
                "import " + TypeCoercionTest.class.getName() + "\n" +
                "import org.drools.compiler.*\n" +
                "rule R1 when\n" +
                "   Person( $ag1 : age )" +
                "   $p2 : Person( name == TypeCoercionTest.integer2String($ag1) )" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final Person p = new Person("42", 42);
        ksession.insert(p);
        assertEquals(1, ksession.fireAllRules());
    }

    public static String integer2String(final Integer value) {
        return "" + value;
    }

    @Test
    public void testStringCoercion() {
        // DROOLS-1688
        final String drl = "package org.drools.compiler.test;\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           " rule R1 when\n" +
                           "     Person(name == \"12\")\n" +
                           " then end\n" +
                           " rule R2 when\n" +
                           "     Person(name == 11)\n " +
                           " then\n end\n" +
                           " rule R3 when\n" +
                           "    Person(name == \"11\")\n" +
                           " then end\n";

        KieBase kieBase = loadKnowledgeBaseFromString(drl);
        KieSession kieSession = kieBase.newKieSession();

        kieSession.insert(new Person("11"));
        assertEquals(2, kieSession.fireAllRules());
    }

    @Test
    public void testIntCoercion() {
        // DROOLS-1688
        final String drl = "package org.drools.compiler.test;\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           " rule R1 when\n" +
                           "     Person(age == 12)\n" +
                           " then end\n" +
                           " rule R2 when\n" +
                           "     Person(age == \"11\")\n " +
                           " then\n end\n" +
                           " rule R3 when\n" +
                           "    Person(age == 11)\n" +
                           " then end\n";

        KieBase kieBase = loadKnowledgeBaseFromString(drl);
        KieSession kieSession = kieBase.newKieSession();

        kieSession.insert(new Person("Mario", 11));
        assertEquals(2, kieSession.fireAllRules());
    }
}
