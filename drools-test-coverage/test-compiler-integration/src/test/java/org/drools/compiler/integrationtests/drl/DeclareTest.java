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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.drools.compiler.Address;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.compiler.integrationtests.facts.ClassB;
import org.drools.compiler.integrationtests.facts.InterfaceB;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DeclareTest extends CommonTestMethodBase {

    @Test
    public void testDeclaredTypesDefaultHashCode() {
        // JBRULES-3481
        final String str = "package com.sample\n" +
                "\n" +
                "global java.util.List list; \n" +
                "" +
                "declare Bean\n" +
                " id : int \n" +
                "end\n" +
                "\n" +
                "declare KeyedBean\n" +
                " id : int @key \n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule Create\n" +
                "when\n" +
                "then\n" +
                " list.add( new Bean(1) ); \n" +
                " list.add( new Bean(2) ); \n" +
                " list.add( new KeyedBean(1) ); \n" +
                " list.add( new KeyedBean(1) ); \n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();

        ksession.setGlobal("list", list);
        ksession.fireAllRules();

        ksession.dispose();

        assertFalse(list.get(0).hashCode() == 34);
        assertFalse(list.get(1).hashCode() == 34);
        assertFalse(list.get(0).hashCode() == list.get(1).hashCode());
        assertNotSame(list.get(0), list.get(1));
        assertFalse(list.get(0).equals(list.get(1)));

        assertTrue(list.get(2).hashCode() == 32);
        assertTrue(list.get(3).hashCode() == 32);
        assertNotSame(list.get(2), list.get(3));
        assertTrue(list.get(2).equals(list.get(3)));
    }

    @Test
    public void testDeclareAndFrom() throws Exception {
        final KieBase kbase = loadKnowledgeBase("test_DeclareWithFrom.drl");
        final FactType profileType = kbase.getFactType("org.drools.compiler", "Profile");

        final KieSession ksession = createKnowledgeSession(kbase);
        final Object profile = profileType.newInstance();
        final Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("internet", Integer.valueOf(2));
        profileType.set(profile, "pageFreq", map);

        ksession.insert(profile);
        ksession.fireAllRules();
        ksession.dispose();
    }

    @Test
    public void testDeclaredFactAndFunction() throws Exception {
        String rule = "package com.jboss.qa;\n";
        rule += "global java.util.List list\n";
        rule += "declare Address\n";
        rule += "    street: String\n";
        rule += "end\n";
        rule += "function void myFunction() {\n";
        rule += "}\n";
        rule += "rule \"r1\"\n";
        rule += "    dialect \"mvel\"\n";
        rule += "when\n";
        rule += "    Address()\n";
        rule += "then\n";
        rule += "    list.add(\"r1\");\n";
        rule += "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(rule);
        final KieSession session = createKnowledgeSession(kbase);

        List list = new ArrayList();
        session.setGlobal("list", list);

        final FactType addressFact = kbase.getFactType("com.jboss.qa", "Address");
        final Object address = addressFact.newInstance();
        session.insert(address);
        session.fireAllRules();

        list = (List) session.getGlobal("list");
        assertEquals(1, list.size());

        assertEquals("r1", list.get(0));
    }

    @Test
    public void testTypeDeclarationOnSeparateResource() throws Exception {
        final String file1 = "package a.b.c\n" +
                "declare SomePerson\n" +
                "    weight : double\n" +
                "    height : double\n" +
                "end\n";
        final String file2 = "package a.b.c\n" +
                "import org.drools.compiler.*\n" +
                "declare Holder\n" +
                "    person : Person\n" +
                "end\n" +
                "rule \"create holder\"\n" +
                "    when\n" +
                "        person : Person( )\n" +
                "        not (\n" +
                "            Holder( person; )\n" +
                "        )\n" +
                "    then\n" +
                "        insert(new Holder(person));\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(file1, file2);
        final KieSession ksession = kbase.newKieSession();

        assertEquals(0, ksession.fireAllRules());
        ksession.insert(new Person("Bob"));
        assertEquals(1, ksession.fireAllRules());
        assertEquals(0, ksession.fireAllRules());
    }

    @Test
    public void testDeclaredTypeAsFieldForAnotherDeclaredType() {
        // JBRULES-3468
        final String str = "package com.sample\n" +
                "\n" +
                "import com.sample.*;\n" +
                "\n" +
                "declare Item\n" +
                "        id : int;\n" +
                "end\n" +
                "\n" +
                "declare Priority\n" +
                "        name : String;\n" +
                "        priority : int;\n" +
                "end\n" +
                "\n" +
                "declare Cap\n" +
                "        item : Item;\n" +
                "        name : String\n" +
                "end\n" +
                "\n" +
                "rule \"split cart into items\"\n" +
                "when\n" +
                "then\n" +
                "        insert(new Item(1));\n" +
                "        insert(new Item(2));\n" +
                "        insert(new Item(3));\n" +
                "end\n" +
                "\n" +
                "rule \"Priorities\"\n" +
                "when\n" +
                "then\n" +
                "        insert(new Priority(\"A\", 3));\n" +
                "        insert(new Priority(\"B\", 2));\n" +
                "        insert(new Priority(\"C\", 5));\n" +
                "end\n" +
                "\n" +
                "rule \"Caps\"\n" +
                "when\n" +
                "        $i : Item()\n" +
                "        $p : Priority($name : name)\n" +
                "then\n" +
                "        insert(new Cap($i, $name));\n" +
                "end\n" +
                "\n" +
                "rule \"test\"\n" +
                "when\n" +
                "        $i : Item()\n" +
                "        Cap(item.id == $i.id)\n" +
                "then\n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        assertEquals(20, ksession.fireAllRules());
        ksession.dispose();
    }

    @Test
    public void testDeclaredTypeWithHundredsProps() {
        // JBRULES-3621
        final StringBuilder sb = new StringBuilder("declare MyType\n");
        for (int i = 0; i < 300; i++) {
            sb.append("i").append(i).append(" : int\n");
        }
        sb.append("end");

        final KieBase kbase = loadKnowledgeBaseFromString(sb.toString());
        kbase.newKieSession();
    }

    @Test
    public void testDeclaresWithArrayFields() throws Exception {
        final String rule = "package org.drools.compiler.test; \n" +
                "import " + org.drools.compiler.test.Person.class.getName() + ";\n" +
                "import " + org.drools.compiler.test.Man.class.getName() + ";\n" +
                "\n" +
                "global java.util.List list;" +
                "\n" +
                "declare Cheese\n" +
                "   name : String = \"ched\" \n" +
                "end \n" +
                "" +
                "declare X\n" +
                "    fld \t: String   = \"xx\"                                      @key \n" +
                "    achz\t: Cheese[] \n" +
                "    astr\t: String[] " + " = new String[] {\"x\", \"y11\" } \n" +
                "    aint\t: int[] \n" +
                "    sint\t: short[] \n" +
                "    bint\t: byte[] \n" +
                "    lint\t: long[] \n" +
                "    dint\t: double[] \n" +
                "    fint\t: float[] \n" +
                "    zint\t: Integer[] " + " = new Integer[] {2,3}                   @key \n" +
                "    aaaa\t: String[][] \n" +
                "    bbbb\t: int[][] \n" +
                "    aprs\t: Person[] " + " = new Person[] { new Man() } \n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "\n" +
                "then\n" +
                "    X x = new X( \"xx\", \n" +
                "                 new Cheese[0], \n" +
                "                 new String[] { \"x\", \"y22\" }, \n" +
                "                 new int[] { 7, 9 }, \n" +
                "                 new short[] { 3, 4 }, \n" +
                "                 new byte[] { 1, 2 }, \n" +
                "                 new long[] { 100L, 200L }, \n" +
                "                 new double[] { 3.2, 4.4 }, \n" +
                "                 new float[] { 3.2f, 4.4f }, \n" +
                "                 new Integer[] { 2, 3 }, \n" +
                "                 new String[2][3], \n" +
                "                 new int[5][3], \n" +
                "                 null \n" +
                "    ); \n" +
                "   insert( x );\n" +
                "   " +
                "   X x2 = new X(); \n" +
                "   x2.setAint( new int[2] ); \n " +
                "   x2.getAint()[0] = 7; \n" +
                "   insert( x2 );\n" +
                "   " +
                "   if ( x.hashCode() == x2.hashCode() ) list.add( \"hash\" );  \n" +
                "   " +
                "   if( x.equals( x2 ) ) list.add( \"equals\" );  \n" +
                "   " +
                "   list.add( x.getAint(  )[0] );  \n" +
                "end \n" +
                "\n" +
                "rule \"Check\"\n" +
                "when\n" +
                "    X( astr.length > 0,            \n" +
                "       astr[0] == \"x\",           \n" +
                "       $x : astr[1],               \n" +
                "       aint[0] == 7  )             \n" +
                "then\n" +
                "    list.add( $x );\n" +
                "end \n" +
                "";

        final KieBase kbase = loadKnowledgeBaseFromString( rule );
        final KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertTrue( list.contains( "hash" ) );
        assertTrue( list.contains( "equals" ) );
        assertTrue( list.contains( 7 ) );
        assertTrue( list.contains( "y11" ) );
        assertTrue( list.contains( "y22" ) );
    }

    @Test
    public void testMvelFunctionWithDeclaredTypeArg() {
        // JBRULES-3562
        final String rule = "package test; \n" +
                "dialect \"mvel\"\n" +
                "global java.lang.StringBuilder value;\n" +
                "function String getFieldValue(Bean bean) {" +
                "   return bean.getField();" +
                "}" +
                "declare Bean \n" +
                "   field : String \n" +
                "end \n" +
                "\n" +
                "rule R1 \n" +
                "when \n" +
                "then \n" +
                "   insert( new Bean( \"mario\" ) ); \n" +
                "end \n" +
                "\n" +
                "rule R2 \n" +
                "when \n" +
                "   $bean : Bean( ) \n" +
                "then \n" +
                "   value.append( getFieldValue($bean) ); \n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString( rule );
        final KieSession ksession = kbase.newKieSession();

        final StringBuilder sb = new StringBuilder();
        ksession.setGlobal( "value", sb );
        ksession.fireAllRules();

        assertEquals( "mario", sb.toString() );
        ksession.dispose();
    }

    @Test
    public void testMvelFunctionWithDeclaredTypeArgForGuvnor() throws Exception {
        // JBRULES-3562
        final String function = "function String getFieldValue(Bean bean) {" +
                " return bean.getField();" +
                "}\n";
        final String declaredFactType = "declare Bean \n" +
                " field : String \n" +
                "end \n";
        final String rule = "rule R2 \n" +
                "dialect 'mvel'\n" +
                "when \n" +
                " $bean : Bean( ) \n" +
                "then \n" +
                " System.out.println( getFieldValue($bean) ); \n" +
                "end\n";

        final KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( declaredFactType.getBytes() ), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( function.getBytes() ), ResourceType.DRL );
        kbuilder.add( ResourceFactory.newByteArrayResource( rule.getBytes() ), ResourceType.DRL );

        for ( final KnowledgeBuilderError error : kbuilder.getErrors() ) {
            System.out.println( "ERROR:" );
            System.out.println( error.getMessage() );
        }
        assertFalse( kbuilder.hasErrors() );
    }

    @Test
    public void testNoneTypeSafeDeclarations() {
        // same namespace
        String str = "package org.drools.compiler\n" +
                "global java.util.List list\n" +
                "declare Person\n" +
                "    @typesafe(false)\n" +
                "end\n" +
                "rule testTypeSafe\n dialect \"mvel\" when\n" +
                "   $p : Person( object.street == 's1' )\n" +
                "then\n" +
                "   list.add( $p );\n" +
                "end\n";

        executeTypeSafeDeclarations( str,
                true );

        // different namespace with import
        str = "package org.drools.compiler.test\n" +
                "import org.drools.compiler.Person\n" +
                "global java.util.List list\n" +
                "declare Person\n" +
                "    @typesafe(false)\n" +
                "end\n" +
                "rule testTypeSafe\n dialect \"mvel\" when\n" +
                "   $p : Person( object.street == 's1' )\n" +
                "then\n" +
                "   list.add( $p );\n" +
                "end\n";
        executeTypeSafeDeclarations( str,
                true );

        // different namespace without import using qualified name
        str = "package org.drools.compiler.test\n" +
                "global java.util.List list\n" +
                "declare org.drools.compiler.Person\n" +
                "    @typesafe(false)\n" +
                "end\n" +
                "rule testTypeSafe\n dialect \"mvel\" when\n" +
                "   $p : org.drools.compiler.Person( object.street == 's1' )\n" +
                "then\n" +
                "   list.add( $p );\n" +
                "end\n";
        executeTypeSafeDeclarations( str,
                true );

        // this should fail as it's not declared non typesafe
        str = "package org.drools.compiler.test\n" +
                "global java.util.List list\n" +
                "declare org.drools.compiler.Person\n" +
                "    @typesafe(true)\n" +
                "end\n" +
                "rule testTypeSafe\n dialect \"mvel\" when\n" +
                "   $p : org.drools.compiler.Person( object.street == 's1' )\n" +
                "then\n" +
                "   list.add( $p );\n" +
                "end\n";
        executeTypeSafeDeclarations( str,
                false );
    }

    private void executeTypeSafeDeclarations(final String str, final boolean mustSucceed) {
        final KieBase kbase;
        try {
            kbase = loadKnowledgeBaseFromString(str);
            if (!mustSucceed) {
                fail("Compilation Should fail");
            }
        } catch (final Throwable e) {
            if (mustSucceed) {
                fail("Compilation Should succeed");
            }
            return;
        }

        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final Address a = new Address("s1");
        final Person p = new Person("yoda");
        p.setObject(a);

        ksession.insert(p);
        ksession.fireAllRules();
        assertEquals(p, list.get(0));
    }

    @Test
    public void testTypeUnsafe() throws Exception {
        final String str = "import " + DeclareTest.class.getName() + ".*\n" +
                "declare\n" +
                "   Parent @typesafe(false)\n" +
                "end\n" +
                "rule R1\n" +
                "when\n" +
                "   $a : Parent( x == 1 )\n" +
                "then\n" +
                "end\n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        for (int i = 0; i < 20; i++) {
            ksession.insert(new ChildA(i % 10));
            ksession.insert(new ChildB(i % 10));
        }

        assertEquals(4, ksession.fireAllRules());

        // give time to async jitting to complete
        Thread.sleep(100);

        ksession.insert(new ChildA(1));
        ksession.insert(new ChildB(1));
        assertEquals(2, ksession.fireAllRules());
    }

    public static class Parent {
    }

    public static class ChildA extends Parent {
        private final int x;

        public ChildA(final int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }
    }

    public static class ChildB extends Parent {
        private final int x;

        public ChildB(final int x) {
            this.x = x;
        }

        public int getX() {
            return x;
        }
    }

    @Test
    public void testConstructorWithOtherDefaults() {
        final String str = "" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare Bean\n" +
                "   kField : String     @key\n" +
                "   sField : String     = \"a\"\n" +
                "   iField : int        = 10\n" +
                "   dField : double     = 4.32\n" +
                "   aField : Long[]     = new Long[] { 100L, 1000L }\n" +
                "end" +
                "\n" +
                "rule \"Trig\"\n" +
                "when\n" +
                "    Bean( kField == \"key\", sField == \"a\", iField == 10, dField == 4.32, aField[1] == 1000L ) \n" +
                "then\n" +
                "    list.add( \"OK\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Exec\"\n" +
                "when\n" +
                "then\n" +
                "    insert( new Bean( \"key\") ); \n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final java.util.List list = new java.util.ArrayList();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertTrue(list.contains("OK"));

        ksession.dispose();
    }

    @Test
    public void testKeyedInterfaceField() {
        //JBRULES-3441
        final String str = "package org.drools.compiler.integrationtest; \n" +
                "\n" +
                "import " + ClassB.class.getCanonicalName() + "; \n" +
                "import " + InterfaceB.class.getCanonicalName() + "; \n" +
                "" +
                "global java.util.List list;" +
                "" +
                "declare Bean\n" +
                "  id    : InterfaceB @key\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when  \n" +
                "then\n" +
                "  insert( new Bean( new ClassB() ) );\n" +
                "end\n" +
                "\n" +
                "rule \"Check\"\n" +
                "when\n" +
                "  $b : Bean( )\n" +
                "then\n" +
                "  list.add( $b.hashCode() ); \n" +
                "  list.add( $b.equals( new Bean( new ClassB() ) ) ); \n" +
                "end";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        final java.util.List list = new java.util.ArrayList();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertTrue(list.contains(31 + 123));
        assertTrue(list.contains(true));

        ksession.dispose();
    }
}
