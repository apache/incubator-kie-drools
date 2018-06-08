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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.testcoverage.common.model.ClassB;
import org.drools.testcoverage.common.model.InterfaceB;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class DeclareTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DeclareTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testDeclaredTypesDefaultHashCode() {
        // JBRULES-3481
        final String str = "package org.drools.compiler.integrationtests.drl;\n" +
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

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();

            ksession.setGlobal("list", list);
            ksession.fireAllRules();

            ksession.dispose();

            assertNotEquals(34, list.get(0).hashCode());
            assertNotEquals(34, list.get(1).hashCode());
            assertNotEquals(list.get(0).hashCode(), list.get(1).hashCode());
            assertNotSame(list.get(0), list.get(1));
            assertNotEquals(list.get(0), list.get(1));

            assertNotSame(list.get(2), list.get(3));
            assertEquals(list.get(2), list.get(3));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testDeclareAndFrom() throws Exception {
        final String drl = "package org.drools.compiler.integrationtests.drl\n" +
                "\n" +
                "declare Profile\n" +
                "    pageFreq : java.util.Map\n" +
                "end\n" +
                "\n" +
                "rule \"Testing out UdayCompare Custom Operator\"\n" +
                "    ruleflow-group \"udaytesting\"\n" +
                "when\n" +
                "    $profile : Profile( $pg : pageFreq )\n" +
                "    Integer( this > 1 ) from $profile.pageFreq[\"internet\"]\n" +
                "then\n" +
                "    System.out.println(\"Yippie it works!!\");\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final FactType profileType = kbase.getFactType("org.drools.compiler.integrationtests.drl", "Profile");
            final Object profile = profileType.newInstance();
            final Map<String, Integer> map = new HashMap<>();
            map.put("internet", 2);
            profileType.set(profile, "pageFreq", map);

            ksession.insert(profile);
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testDeclaredFactAndFunction() throws Exception {
        final String drl = "package org.drools.compiler.integrationtests.drl;\n" +
            "global java.util.List list\n" +
            "declare Address\n" +
            "    street: String\n" +
            "end\n" +
            "function void myFunction() {\n" +
            "}\n" +
            "rule \"r1\"\n" +
            "    dialect \"mvel\"\n" +
            "when\n" +
            "    Address()\n" +
            "then\n" +
            "    list.add(\"r1\");\n" +
            "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();
        try {
            List list = new ArrayList();
            session.setGlobal("list", list);

            final FactType addressFact = kbase.getFactType("org.drools.compiler.integrationtests.drl", "Address");
            final Object address = addressFact.newInstance();
            session.insert(address);
            session.fireAllRules();

            list = (List) session.getGlobal("list");
            assertEquals(1, list.size());

            assertEquals("r1", list.get(0));
        } finally {
            session.dispose();
        }
    }

    @Test
    public void testTypeDeclarationOnSeparateResource() {
        final String drl1 = "package a.b.c\n" +
                "declare SomePerson\n" +
                "    weight : double\n" +
                "    height : double\n" +
                "end\n";
        final String drl2 = "package a.b.c\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
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

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test", kieBaseTestConfiguration, drl1, drl2);
        final KieSession ksession = kbase.newKieSession();
        try {
            assertEquals(0, ksession.fireAllRules());
            ksession.insert(new Person("Bob"));
            assertEquals(1, ksession.fireAllRules());
            assertEquals(0, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testDeclaredTypeAsFieldForAnotherDeclaredType() {
        // JBRULES-3468
        final String drl = "package com.sample\n" +
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

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            assertEquals(20, ksession.fireAllRules());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testDeclaredTypeWithHundredsProps() {
        // JBRULES-3621
        final StringBuilder sb = new StringBuilder("declare MyType\n");
        for (int i = 0; i < 300; i++) {
            sb.append("i").append(i).append(" : int\n");
        }
        sb.append("end");

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test", kieBaseTestConfiguration, sb.toString());
        final KieSession ksession = kbase.newKieSession();
        ksession.dispose();
    }

    @Test
    public void testDeclaresWithArrayFields() {
        final String drl = "package org.drools.compiler.integrationtests.drl; \n" +
                "import " + Person.class.getName() + ";\n" +
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
                "    aprs\t: Person[] " + " = new Person[] { } \n" +
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
                "    X( astr != null,               \n" +
                "       astr.length > 0,            \n" +
                "       astr[0] == \"x\",           \n" +
                "       $x : astr[1],               \n" +
                "       aint[0] == 7  )             \n" +
                "then\n" +
                " System.out.println(\"Fired!!!!!!!!!!!!!!\" + $x); \n" +
                "    list.add( $x );\n" +
                "end \n" +
                "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal( "list", list );

            ksession.fireAllRules();
            System.out.println(list);
            assertTrue( list.contains( "hash" ) );
            assertTrue( list.contains( "equals" ) );
            assertTrue( list.contains( 7 ) );
            // The X instances are considered equal so when using EQUALITY, the second insert doesn't insert anything,
            // therefore, the fire produced by the second insert should be checked just with IDENTITY.
            if (kieBaseTestConfiguration.isIdentity()) {
                assertTrue( list.contains( "y11" ) );
            }
            assertTrue( list.contains( "y22" ) );
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMvelFunctionWithDeclaredTypeArg() {
        // JBRULES-3562
        final String drl = "package org.drools.compiler.integrationtests.drl; \n" +
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

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final StringBuilder sb = new StringBuilder();
            ksession.setGlobal( "value", sb );
            ksession.fireAllRules();

            assertEquals( "mario", sb.toString() );
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMvelFunctionWithDeclaredTypeArgForGuvnor() {
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

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test",
                                                                         kieBaseTestConfiguration,
                                                                         declaredFactType,
                                                                         function,
                                                                         rule);
        final KieSession ksession = kbase.newKieSession();
        ksession.dispose();
    }

    @Test
    public void testConstructorWithOtherDefaults() {
        final String drl =
                "global java.util.List list;\n" +
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

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final java.util.List list = new java.util.ArrayList();
            ksession.setGlobal("list", list);

            ksession.fireAllRules();
            assertTrue(list.contains("OK"));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testKeyedInterfaceField() {
        //JBRULES-3441
        final String drl = "package org.drools.compiler.integrationtests.drl; \n" +
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
                "  list.add( $b.equals( new Bean( new ClassB() ) ) ); \n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("declare-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final java.util.List list = new java.util.ArrayList();
            ksession.setGlobal("list", list);

            ksession.fireAllRules();
            assertTrue(list.contains(true));
        } finally {
            ksession.dispose();
        }
    }
}
