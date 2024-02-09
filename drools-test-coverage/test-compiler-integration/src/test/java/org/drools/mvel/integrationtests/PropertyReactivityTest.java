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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.State;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.definition.type.Modifies;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(Parameterized.class)
public class PropertyReactivityTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public PropertyReactivityTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test(timeout=10000)
    public void testComposedConstraint() {
        final String str =
                "package org.drools.test;\n" +
                "\n" +
                "import " + Klass2.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when \n" +
                " $k2 : Klass2(b == 0 || c == 0)\n" +
                "then" +
                " modify($k2) { setD(1) }\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final Klass2 k2 = new Klass2(0, 0, 0, 0);
        ksession.insert(k2);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(k2.getD()).isEqualTo(1);
    }

    @Test(timeout=10000)
    public void testScrambleProperties() {
        // DROOLS-91
        final String str =
                "package org.drools.test\n" +
                " global java.util.List list" +
                "\n" +
                " declare Parent\n" +
                " @propertyReactive\n" +
                " a : int\n" +
                " k : int\n" +
                " z : int\n" +
                " end\n" +
                "\n" +
                " declare Child extends Parent\n" +
                " @propertyReactive\n" +
                " p : int\n" +
                " end\n" +
                "\n" +
                "\n" +
                " rule Init\n" +
                " when\n" +
                " then\n" +
                " insert( new Child( 1, 3, 5, 7 ) );\n" +
                " end\n" +
                "\n" +
                " rule Mod\n" +
                " when\n" +
                " $p : Parent()\n" +
                " then\n" +
                " modify( $p ) { setZ( 99 ); }\n" +
                " end\n" +
                "\n" +
                " rule React2\n" +
                " when\n" +
                " Child( p == 7 )\n" +
                " then\n" +
                " list.add( \"React2\" );\n" +
                " end\n" +
                "\n" +
                " rule React\n" +
                " when\n" +
                " Child( z == 99 )\n" +
                " then\n" +
                " list.add( \"React\" );\n" +
                " end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.contains("React")).isTrue();
        assertThat(list.contains("React2")).isTrue();
    }

    @PropertyReactive
    public static interface Intf1 {
        public int getC();
        public void setC(int c);

        public int getD();
        public void setD(int d);

        public int getF();
        public void setF(int f);

        public String getId();
    }

    @PropertyReactive
    public static interface Intf2 {
        public int getD();
        public void setD(int d);

        public int getE();
        public void setE(int e);

        public String getId();
    }

    @PropertyReactive
    public static class Klass implements Intf1, Intf2 {
        private String id = "k1";
        private int a;
        private int b;
        private int c;
        private int d;
        private int e;
        private int f;

        public Klass(final int a, final int b, final int c, final int d, final int e, final int f) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
            this.f = f;
        }

        public int getA() { return a; }
        public void setA(final int a) { this.a = a; }

        public int getB() { return b; }
        public void setB(final int b) { this.b = b; }

        public int getC() { return c; }
        public void setC(final int c) { this.c = c; }

        public int getD() { return d; }
        public void setD(final int d) { this.d = d; }

        public int getE() { return e; }
        public void setE(final int e) { this.e = e; }

        public int getF() { return f; }
        public void setF(final int f) { this.f = f; }

        public String getId() { return id; }
    }

    @PropertyReactive
    public static class Klass2 implements Intf2 {
        private String id = "k2";
        private int b;
        private int c;
        private int d;
        private int e;

        public Klass2( final int b, final int c, final int d, final int e ) {
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
        }

        public int getB() { return b; }
        public void setB(final int b) { this.b = b; }

        public int getC() { return c; }
        public void setC(final int c) { this.c = c; }

        public int getD() { return d; }
        public void setD(final int d) { this.d = d; }

        public int getE() { return e; }
        public void setE(final int e) { this.e = e; }

        public String getId() { return id; }
    }


    @Test(timeout=10000)
    public void testScrambleWithInterfaces() {
    /*
     *       K1 a b c d e f    1000
     *       I1     c d   f    10
     *       I2       d e      1
     *       K2   b c d e      100
     */

        // DROOLS-91
        final String str =
                "package org.drools.test;\n" +
                "\n" +
                "import " + Intf1.class.getCanonicalName() + ";\n" +
                "import " + Intf2.class.getCanonicalName() + ";\n" +
                "import " + Klass.class.getCanonicalName() + ";\n" +
                "import " + Klass2.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Klass( 1, 2, 3, 4, 5, 6 ) );\n" +
                "  insert( new Klass2( 2, 3, 4, 5 ) );\n" +
                "end\n" +
                "\n" +
                "rule \"On1\"\n" +
                "when\n" +
                "  $x : Intf1( )\n" +
                "then\n" +
                "  System.out.println( \"Modify by interface \" );\n" +
                "  modify ( $x ) { setD( 200 ) }\n" +
                "end\n" +
                "rule \"On2\"\n" +
                "when\n" +
                "  $x : Klass2( )\n" +
                "then\n" +
                "  System.out.println( \"Modify by class \" );\n" +
                "  modify ( $x ) { setD( 200 ) }\n" +
                "end\n" +
                "\n" +
                "rule \"Log1\"\n" +
                "when\n" +
                "  Klass( d == 200, $id : id ) \n" +
                "then\n" +
                "  System.out.println( \"Log1 - As K1 \" + $id );\n" +
                "  list.add( $id + \"@K1\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Log2\"\n" +
                "when\n" +
                "  Klass2( d == 200, $id : id ) \n" +
                "then\n" +
                "  System.out.println( \"Log2 - As K2 \" + $id );\n" +
                "  list.add( $id + \"@K2\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Log3\"\n" +
                "when\n" +
                "  Intf1( d == 200, $id : id ) \n" +
                "then\n" +
                "  System.out.println( \"Log3 - As I1 \" + $id );\n" +
                "  list.add( $id + \"@I1\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Log4\"\n" +
                "when\n" +
                "  Intf2( d == 200, $id : id ) \n" +
                "then\n" +
                "  System.out.println( \"Log4 - As K2 \" + $id );\n" +
                "  list.add( $id + \"@I2\" );\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        System.out.println( list );

        assertThat(list.containsAll(Arrays.asList("k1@K1", "k1@I1", "k1@I2"))).isTrue();
        assertThat(list.containsAll(Arrays.asList("k2@K2", "k2@I2"))).isTrue();
        assertThat(list.size()).isEqualTo(5);
    }

    @Test(timeout=10000)
    public void testScrambleWithInterfacesAndObject() {
        // DROOLS-91
        final String str =
                "package org.drools.test;\n" +
                "\n" +
                "import " + Intf2.class.getCanonicalName() + ";\n" +
                "import " + Klass2.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Klass2( 2, 3, 4, 5 ) );\n" +
                "end\n" +
                "rule \"Mod\"\n" +
                "when\n" +
                "  $x : Intf2( )\n" +
                "then\n" +
                "  modify ( $x ) { setD( 200 ) }\n" +
                "end\n" +
                "\n" +
                "rule \"Log\"\n" +
                "when\n" +
                "  Klass2( d == 200, $id : id ) \n" +
                "then\n" +
                "  list.add( \"Klass2\" );\n" +
                "end\n" +
                "\n" +
                "rule \"LogObject\" salience -1\n" +
                "when\n" +
                "  $o : Object( ) \n" +
                "then\n" +
                "  list.add( $o.getClass().getSimpleName() );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.get(0)).isEqualTo("Klass2");
        assertThat(list.get(1)).isEqualTo("Klass2");
    }

    @Test(timeout=10000)
    public void testRepeatedPatternWithPR() {
        // JBRULES-3705
        final String str1 =
                "package org.test;\n" +
                "global java.util.List list; \n" +
                "" +
                "declare SampleBean \n" +
                "@propertyReactive \n" +
                "x : java.math.BigDecimal \n" +
                "y : java.math.BigDecimal \n" +
                "id : Long @key\n" +
                "end \n" +
                "" +
                " rule \"calculate y\"\n" +
                " dialect \"mvel\"\n" +
                " when\n" +
                " $bean : SampleBean(id == 1L);\n" +
                " then\n" +
                " modify($bean){\n" +
                " y =5B;\n" +
                " }\n" +
                " list.add( $bean.y ); \n" +
                " end\n" +
                "\n" +
                " rule \"calculate x\"\n" +
                " dialect \"mvel\"\n" +
                " when\n" +
                " $bean : SampleBean(id == 1L);\n" +
                " then\n" +
                " modify($bean){\n" +
                " x =10B;\n" +
                " }\n" +
                " list.add( $bean.x ); \n" +
                " end\n" +
                "" +
                " rule Init \n" +
                " when\n" +
                " then\n" +
                " insert( new SampleBean( 1L ) ); \n" +
                " end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1);
        final KieSession ksession = kbase.newKieSession();
        final ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.contains(new BigDecimal( 10 ))).isTrue();
        assertThat(list.contains(new BigDecimal(5))).isTrue();
    }

    @Test(timeout=10000)
    public void testPRWithCollections() {
        // DROOLS-135
        final String str1 = "package org.test;\n" +
                      "import java.util.*\n" +
                      "\n" +
                      "global List list;\n" +
                      "" +
                      "declare java.util.ArrayList end \n" +
                      "" +
                      "declare MyList extends java.util.ArrayList \n" +
                      "end\n" +
                      "\n" +
                      "declare Bean\n" +
                      "@propertyReactive\n" +
                      " id : int\n" +
                      " num : int\n" +
                      " values : MyList \n" +
                      " checks : Map \n" +
                      " str : String\n" +
                      "end\n" +
                      "\n" +
                      "\n" +
                      "rule Init\n" +
                      "when\n" +
                      "then\n" +
                      " insert( new Bean( 42, 0, new MyList(), new HashMap(), \"\" ) );\n" +
                      "end\n" +
                      "\n" +
                      "rule M1\n" +
                      "when\n" +
                      " $b : Bean( id == 42 )\n" +
                      "then\n" +
                      " System.out.println( 1 ); \n" +
                      " list.add( 1 ); \n" +
                      " modify ( $b ) { setNum( 1 ); }\n" +
                      "end\n" +
                      "\n" +
                      "rule M2\n" +
                      "when\n" +
                      " $b : Bean( num == 1 )\n" +
                      "then\n" +
                      " System.out.println( 2 ); \n" +
                      " list.add( 2 ); \n" +
                      " modify ( $b ) { getValues().add( \"foo\" ); }\n" +
                      "end\n" +
                      "\n" +
                      "rule M3\n" +
                      "when\n" +
                      " $b : Bean( values contains \"foo\" )\n" +
                      "then\n" +
                      " System.out.println( 3 ); \n" +
                      " list.add( 3 ); \n" +
                      " modify ( $b ) { setStr( \"x\" ); }\n" +
                      "end\n" +
                      " \n " +
                      "rule M4\n" +
                      "when\n" +
                      " $b : Bean( str == \"x\" )\n" +
                      "then\n" +
                      " System.out.println( 4 ); \n" +
                      " list.add( 4 ); \n" +
                      " modify ( $b ) { getChecks().put( \"x\", 13 ); }\n" +
                      "end\n" +
                      "\n" +
                      "rule M5\n" +
                      "when\n" +
                      " $b : Bean( checks[ \"x\" ] > 10 )\n" +
                      "then\n" +
                      " System.out.println( 5 ); \n" +
                      " list.add( 5 ); \n" +
                      " modify ( $b ) { getChecks().clear(); }\n" +
                      "end\n" +
                      "\n" +
                      "rule Log\n" +
                      "salience 1\n" +
                      "when\n" +
                      " $b : Bean() @watch( values, checks ) \n" +
                      "then\n" +
                      " System.out.println( \"Log >> \" + $b );\n" +
                      " list.add( 0 );\n" +
                      "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1);
        final KieSession ksession = kbase.newKieSession();
        final ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertThat(list).isEqualTo(Arrays.asList(0, 1, 2, 0, 3, 4, 0, 5, 0));

    }

    @Test(timeout=10000)
    public void testPRWithPositionalUnification() {
        // DROOLS-247
        final String str1 =
                "package org.test;\n" +
                "global java.util.List list; \n" +
                "" +
                "declare Man \n" +
                "@propertyReactive \n" +
                " name : String \n" +
                "end \n" +
                "" +
                "declare Animal \n" +
                "@propertyReactive \n" +
                " id : int \n" +
                " owner : String \n" +
                " age : int \n" +
                "end \n" +
                "" +
                "rule Init \n" +
                "when \n" +
                "then \n" +
                " insert( new Man( \"alan\" ) ); \n" +
                " insert( new Animal( 1, \"bob\", 7 ) ); \n" +
                "end \n" +
                "" +
                "rule \"Mod Man\" \n" +
                "when \n" +
                " $m :Man() \n" +
                "then \n" +
                " modify ( $m ) { setName( \"bob\" ); } \n" +
                "end \n" +
                "" +
                "rule \"Mod Per\" \n" +
                "when \n" +
                " $m :Animal() \n" +
                "then \n" +
                " modify ( $m ) { " +
                " setId( 1 ); \n " +
                " } \n" +
                "end \n" +
                "" +
                "rule Join_1\n" +
                "when\n" +
                " Man( $name ; ) \n" +
                " Animal( $name == owner ) \n" +
                "then\n" +
                " list.add( 1 ); \n" +
                "end\n" +
                "" +
                "rule Join_3\n" +
                "when\n" +
                " Man( $name ; ) \n" +
                " Animal( $id, $name; ) \n" +
                " Integer( this == $id ) \n" +
                "then\n" +
                " list.add( 2 ); \n" +
                "end\n" +
                "";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1);
        final KieSession ksession = kbase.newKieSession();
        final ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( 1 );

        ksession.fireAllRules();

        assertThat(list.contains(1)).isTrue();
        assertThat(list.contains(2)).isTrue();
        assertThat(list.size()).isEqualTo(2);

    }

    /**
     * Tests the use of PR on constraints involving 'virtual' properties
     * of a POJO: calculated properties without a setter.
     * Because getFullName doesn't have a setter, and we are not using
     * @watch in the rule nor @Modified in setName() or in setLastName(), there
     * are no way that rule 'Find Heisenberg' gets activated because a modification
     * or a Klass3 object.
     */
    @Test(timeout=10000)
    public void testPRConstraintOnAttributesWithoutSetter(){
        final String str =
                "package org.drools.test;\n" +
                "\n" +
                "import " + Klass3.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Klass3( \"XXX\", \"White\" ) );\n" +
                "end\n" +

                "rule \"Find Heisenberg\"\n" +
                "when\n" +
                "  $x : Klass3( fullName == 'Walter White' )\n" +
                "then\n" +
                "  list.add( drools.getRule().getName() );\n" +
                "end\n" +

                "rule \"XXX -> Walter\"\n" +
                "when\n" +
                "  $x : Klass3( name == 'XXX' )\n" +
                "then\n" +
                "  modify($x){ setName('Walter') };\n" +
                "  list.add( drools.getRule().getName() );\n" +
                "end\n" +
                "\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list).isEqualTo(List.of("XXX -> Walter"));
    }

    /**
     * Tests the use of PR on constraints involving 'virtual' properties
     * of a POJO: calculated properties without a setter.
     * getFullName doesn't have a setter and Klass3 doesn't state that setName()
     * nor setLastName() @Modifies fullName. We are explicitly using @watches
     * in the rule involving fullName to be aware of modifications in the name
     * and/or lastName of a Klass3 object.
     */
    @Test(timeout=10000)
    public void testPRConstraintOnAttributesWithoutSetterUsingWatches(){
        final String str =
                "package org.drools.test;\n" +
                "\n" +
                "import " + Klass3.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Klass3( \"XXX\", \"White\" ) );\n" +
                "end\n" +

                "rule \"Find Heisenberg\"\n" +
                "when\n" +
                "  $x : Klass3( fullName == 'Walter White' ) @watch( name, lastName)\n" +
                "then\n" +
                "  list.add( drools.getRule().getName() );\n" +
                "end\n" +

                "rule \"XXX -> Walter\"\n" +
                "when\n" +
                "  $x : Klass3( name == 'XXX' )\n" +
                "then\n" +
                "  modify($x){ setName('Walter') };\n" +
                "  list.add( drools.getRule().getName() );\n" +
                "end\n" +
                "\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list).isEqualTo(Arrays.asList("XXX -> Walter", "Find Heisenberg"));
    }

    /**
     * Tests the use of PR on constraints involving 'virtual' properties
     * of a POJO: calculated properties without a setter.
     * getFullName doesn't have a setter but Klass4 states that setName()
     * and setLastName() both @Modifies fullName.
     */
    @Test(timeout=10000)
    public void testPRConstraintOnAttributesWithoutSetterUsingModifies(){
        final String str =
                "package org.drools.test;\n" +
                "\n" +
                "import " + Klass4.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Klass4( \"XXX\", \"White\" ) );\n" +
                "end\n" +

                "rule \"Find Heisenberg\"\n" +
                "when\n" +
                "  $x : Klass4( fullName == 'Walter White' )\n" +
                "then\n" +
                "  list.add( drools.getRule().getName() );\n" +
                "end\n" +

                "rule \"XXX -> Walter\"\n" +
                "when\n" +
                "  $x : Klass4( name == 'XXX' )\n" +
                "then\n" +
                "  modify($x){ setName('Walter') };\n" +
                "  list.add( drools.getRule().getName() );\n" +
                "end\n" +
                "\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list).isEqualTo(Arrays.asList("XXX -> Walter", "Find Heisenberg"));
    }

    /**
     * Tests the use of PR on bindings involving 'virtual' properties
     * of a POJO: calculated properties without a setter.
     * Because getFullName doesn't have a setter, and we are not using
     * @watch in the rule nor @Modified in setName() or in setLastName(), there
     * are no way that rule 'Get Person name' gets activated because a modification
     * or a Klass3 object.
     */
    @Test(timeout=10000)
    public void testPRBindingOnAttributesWithoutSetter(){
        final String str =
                "package org.drools.test;\n" +
                "\n" +
                "import " + Klass3.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Klass3( \"XXX\", \"White\" ) );\n" +
                "end\n" +

                "rule \"Get Person name\"\n" +
                "salience 1\n" +
                "when\n" +
                "  $x : Klass3( $fullName: fullName )\n" +
                "then\n" +
                "  list.add( $fullName );\n" +
                "end\n" +

                "rule \"XXX -> Walter\"\n" +
                "when\n" +
                "  $x : Klass3( name == 'XXX' )\n" +
                "then\n" +
                "  modify($x){ setName('Walter') };\n" +
                "end\n" +
                "\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list).isEqualTo(List.of("XXX White"));
    }

    /**
     * Tests the use of PR on bindings involving 'virtual' properties
     * of a POJO: calculated properties without a setter.
     * getFullName doesn't have a setter but we are explicitly using @watches
     * annotation in 'Get Person name' rule. After the name of Kalss3 instance is
     * modified, rule 'Get Person name' must be re-activated.
     */
    @Test(timeout=10000)
    public void testPRBindingOnAttributesWithoutSetterUsingWatches(){
        final String str =
                "package org.drools.test;\n" +
                "\n" +
                "import " + Klass3.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Klass3( \"XXX\", \"White\" ) );\n" +
                "end\n" +

                "rule \"Get Person name\"\n" +
                "salience 1\n" +
                "when\n" +
                "  $x : Klass3( $fullName: fullName ) @watch( name, lastName)\n" +
                "then\n" +
                "  list.add( $fullName );\n" +
                "end\n" +

                "rule \"XXX -> Walter\"\n" +
                "when\n" +
                "  $x : Klass3( name == 'XXX' )\n" +
                "then\n" +
                "  modify($x){ setName('Walter') };\n" +
                "end\n" +
                "\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list).isEqualTo(Arrays.asList("XXX White", "Walter White"  ));
    }

    /**
     * Tests the use of PR on bindings involving 'virtual' properties
     * of a POJO: calculated properties without a setter.
     * getFullName doesn't have a setter but we are explicitly using @Modifies
     * in Klass4's setName() and setLastName(). After the name of Kalss4
     * instance is modified, rule 'Get Person name' must be re-activated.
     */
    @Test(timeout=10000)
    public void testPRBindingOnAttributesWithoutSetterUsingModifies(){
        final String str =
                "package org.drools.test;\n" +
                "\n" +
                "import " + Klass4.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new Klass4( \"XXX\", \"White\" ) );\n" +
                "end\n" +

                "rule \"Get Person name\"\n" +
                "salience 1\n" +
                "when\n" +
                "  $x : Klass4( $fullName: fullName )\n" +
                "then\n" +
                "  list.add( $fullName );\n" +
                "end\n" +

                "rule \"XXX -> Walter\"\n" +
                "when\n" +
                "  $x : Klass4( name == 'XXX' )\n" +
                "then\n" +
                "  modify($x){ setName('Walter') };\n" +
                "end\n" +
                "\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list).isEqualTo(Arrays.asList("XXX White", "Walter White"  ));
    }


    @Test(timeout=10000)
    public void testPRBindingOnNonexistingAttributes(){
        final String str =
                "package org.drools.test;\n" +
                "\n" +
                "import " + Klass4.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +

                "rule \"Get Person name\"\n" +
                "salience 1\n" +
                "when\n" +
                "  $x : Klass4( $name: nonexistingName )\n" +
                "then\n" +
                "  list.add( $fullName );\n" +
                "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @Test(timeout=10000)
    public void testPRBindingOnNonexistingWatchedAttribute(){
        final String str =
                "package org.drools.test;\n" +
                "\n" +
                "import org.drools.mvel.integrationtests.PropertyReactivityTest.Klass4;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +

                "rule \"Get Person name\"\n" +
                "salience 1\n" +
                "when\n" +
                "  $x : Klass4( ) @watch( nmae )\n" +
                "then\n" +
                "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @Test(timeout=10000)
    public void testModifyAfterInsertWithPropertyReactive() {
        final String rule1 =
                "\n" +
                "package com.sample;\n" +
                "import " + MyClass.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule r0\n" +
                "then insert( new MyClass() );\n" +
                "end\n" +
                "rule r1 salience 1\n" +
                "when " +
                "  MyClass(value == null)\n" +
                "then " +
                "  list.add( 1 );\n" +
                "end\n" +
                "\n" +
                "rule r2 salience 2\n" +
                "when " +
                "  m : MyClass(value == null)\n" +
                "then " +
                "  modify(m) { setData(\"test\") }\n" +
                "  list.add( 2 );\n" +
                "end\n" +
                "\n" +
                "rule r3 salience 3\n" +
                "when " +
                "  MyClass(value == null)\n" +
                "then " +
                "  list.add( 3 );\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule1);
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        assertThat(ksession.fireAllRules()).isEqualTo(4);

        assertThat(list.size()).isEqualTo(3);

        assertThat(list.get(0)).isEqualTo(3);
        assertThat(list.get(1)).isEqualTo(2);
        assertThat(list.get(2)).isEqualTo(1);
    }

    @PropertyReactive
    public static class Klass3 {
        private String name;
        private String lastName;

        public Klass3(final String name, final String lastName) {
            this.name = name;
            this.lastName = lastName;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(final String lastName) {
            this.lastName = lastName;
        }

        public String getFullName(){
            return this.name + " "+ this.lastName;
        }
    }

    @PropertyReactive
    public static class MyClass {
        private String value;
        private String data;

        public String getValue() {
            return value;
        }

        public void setValue(final String value) {
            this.value = value;
        }

        public String getData() {
            return data;
        }

        public void setData(final String data) {
            this.data = data;
        }
    }

    /**
     * Same as Klass3 but using @Modifies in setName() and setLastName()
     */
    @PropertyReactive
    public static class Klass4 {
        private String name;
        private String lastName;

        public Klass4(final String name, final String lastName) {
            this.name = name;
            this.lastName = lastName;
        }

        public String getName() {
            return name;
        }

        @Modifies("fullName")
        public void setName(final String name) {
            this.name = name;
        }

        public String getLastName() {
            return lastName;
        }

        @Modifies("fullName")
        public void setLastName(final String lastName) {
            this.lastName = lastName;
        }

        public String getFullName(){
            return this.name + " "+ this.lastName;
        }
    }

    @Test(timeout=10000)
    public void testIndexedNotWatchedProperty() {
        // DROOLS-569
        final String rule1 =
                "package com.sample;\n" +
                "import " + MyClass.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R1 when\n" +
                "    $s : String()\n" +
                "    $m : MyClass( data != null, value == $s ) @watch( !* )\n" +
                "then \n" +
                "    list.add($s);\n" +
                "    modify( $m ) { setValue(\"2\") };\n" +
                "end\n" +
                "\n" +
                "rule R2 when\n" +
                "    $i : Integer()\n" +
                "    $m : MyClass( value == $i.toString(), data == \"x\" ) @watch( !value )\n" +
                "then \n" +
                "    modify( $m ) { setValue(\"3\"), setData(\"y\") };\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule1);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        final MyClass myClass = new MyClass();
        myClass.setValue("1");
        myClass.setData("x");
        ksession.insert(myClass);
        ksession.insert("1");
        ksession.insert(2);
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("1");
        list.clear();

        ksession.insert("3");
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("3");
    }

    @Test
    public void testModifyWithGetter() {
        final String rule1 =
                "package foo.bar\n" +
                "import " + Person.class.getName() + "\n" +
                "declare Person @propertyReactive end\n" +
                "rule x\n" +
                "    when\n" +
                "       $p : Person( address != null ) @watch(!address) \n" +
                "    then\n" +
                "       modify($p){getAddress().setStreet(\"foo\");}\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, rule1);
        KieSession ksession = kbase.newKieSession();

        final Person p = new Person();
        p.setAddress(new Address());
        ksession.insert(p);

        final int fired = ksession.fireAllRules(10);

        assertThat(fired).isEqualTo(1);
        assertThat(p.getAddress().getStreet()).isEqualTo("foo");
    }

    @Test(timeout = 10000L)
    public void testMoreThan64Fields() {
        final StringBuilder fields = new StringBuilder();
        for (int i = 10; i < 100; i++) {
            fields.append("  a").append(i).append(" : int\n");
        }
        final String str =
                "package org.drools.test\n" +
                "global java.util.List list;\n" +
                "declare BigType @propertyReactive\n" +
                fields +
                "end\n" +
                "rule Init when\n" +
                "then\n" +
                "  insert( new BigType() );" +
                "end\n" +
                "rule R when\n" +
                "  $b : BigType( a11 == 0, a98 == 0 )" +
                "then\n" +
                "  modify($b) { setA12(1), setA99(1) };\n" +
                "  list.add(1);\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        final List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
    }

    @Test(timeout = 10000L)
    public void testMoreThan64FieldsMultipleFirings() {
        final StringBuilder fields = new StringBuilder();
        for (int i = 10; i < 100; i++) {
            fields.append("  a").append(i).append(" : int\n");
        }
        final String str =
                "package org.drools.test\n" +
                "global java.util.List list;\n" +
                "declare BigType @propertyReactive\n" +
                fields +
                "end\n" +
                "rule Init when\n" +
                "then\n" +
                "  insert( new BigType() );" +
                "end\n" +
                "rule R when\n" +
                "  $b : BigType( a11 == 0, a12 < 10, a98 == 0 )" +
                "then\n" +
                "  modify($b) { setA12($b.getA12()+1), setA99(1) };\n" +
                "  list.add(1);\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        final List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(10);
    }

    @Test(timeout = 10000L)
    public void testMoreThan64FieldsWithWatch() {
        final StringBuilder fields = new StringBuilder();
        for (int i = 10; i < 100; i++) {
            fields.append("  a").append(i).append(" : int\n");
        }
        final String str =
                "package org.drools.test\n" +
                "global java.util.List list;\n" +
                "declare BigType @propertyReactive\n" +
                fields +
                "end\n" +
                "rule Init when\n" +
                "then\n" +
                "  insert( new BigType() );" +
                "end\n" +
                "rule R when\n" +
                "  $b : BigType( a11 == 0, a99 < 10 ) @watch(!a99)" +
                "then\n" +
                "  modify($b) { setA12(1), setA99(1) };\n" +
                "  list.add(1);\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        final List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testPropReactiveAnnotationOnDifferentDrl() {
        // DROOLS-800
        final String str1 =
                "package org.jboss.ddoyle.drools.propertyreactive;\n" +
                "\n" +
                "import " + Event1.class.getCanonicalName() + ";\n" +
                "\n" +
                "declare Event1\n" +
                "    @role( event )\n" +
                "    @timestamp( timestamp )\n" +
                "    @expires( 2d )\n" +
                "    @propertyReactive\n" +
                "end\n";

        final String str2 =
                "package org.jboss.ddoyle.drools.propertyreactive;\n" +
                "\n" +
                "import " + Event1.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule \"rule_1\"\n" +
                "    when\n" +
                "       Event1() @watch(*, !code)\n" +
                "    then\n" +
                "       System.out.println(\"Rule fired.\");\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1, str2);

    }

    public static class Event1 {

        private int id;
        private String code;

        private long timestamp;

        public Event1( final String code, final int id ) {
            this.code = code;
            this.id = id;
        }

        public String getCode() {
            return code;
        }

        public int getId() {
            return id;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public void setTimestamp( final long timestamp ) {
            this.timestamp = timestamp;
        }

        public void setId( final int id ) {
            this.id = id;
        }

        public void setCode( final String code ) {
            this.code = code;
        }
    }

    public static class ParentDummyBean {
        private String id;

        public ParentDummyBean(final String id) { this.id = id; }

        public String getId() { return id; }
        public void setId(final String id) { this.id = id; }
    }

    @PropertyReactive
    public interface DummyBeanInterface {
        boolean isActive();
        void setActive(boolean active);
    }

    @PropertyReactive
    public static class DummyBean extends ParentDummyBean implements DummyBeanInterface {
        private boolean active;

        public DummyBean(final String id) {
            super(id);
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(final boolean active) {
            this.active = active;
        }

        @Override
        public String toString() {
            return "DummyEvent{" + "id='" + getId() + '\'' + ", active=" + active + '}';
        }
    }

    @Test
    public void testPropReactiveWithParentClassNotImplementingChildInterface() {
        // DROOLS-1090
        final String str1 =
                "import " + DummyBeanInterface.class.getCanonicalName() + "\n" +
                "import " + DummyBean.class.getCanonicalName() + "\n" +
                "rule \"RG_TEST_1\"\n" +
                "    when\n" +
                "       $event: DummyBean (!active)\n" +
                "    then\n" +
                "        modify($event){\n" +
                "            setActive(true)\n" +
                "        }\n" +
                "        System.out.println(\"RG_TEST_1 fired\");\n" +
                "end\n" +
                "\n" +
                "rule \"RG_TEST_2\"\n" +
                "    when\n" +
                "       $event: DummyBeanInterface (!active)\n" +
                "    then\n" +
                "        System.out.println(\"RG_TEST_2 fired, with event \" + $event);\n" +
                "        throw new IllegalStateException(\"Should not happen since the event is active\");\n" +
                "end\n" +
                "\n" +
                "rule \"RG_TEST_3\"\n" +
                "    when\n" +
                "       $event: DummyBean ()\n" +
                "    then\n" +
                "        retract($event);\n" +
                "        System.out.println(\"RG_TEST_3 fired\");\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(new DummyBean("1"));
        ksession.fireAllRules();

        ksession.insert(new DummyBean("2"));
        ksession.fireAllRules();
    }

    @Test
    public void testPropReactiveUpdate() {
        // DROOLS-1275
        final String str1 =
                "import " + Klass.class.getCanonicalName() + "\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  Klass( b == 2 )\n" +
                "then\n" +
                "  list.add(\"fired\");\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );

        final Klass bean = new Klass( 1, 2, 3, 4, 5, 6 );
        final FactHandle fh = ksession.insert( bean );
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);

        ksession.update(fh, bean, "a", "d");
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);

        ksession.update(fh, bean, "c", "b");
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(2);
    }

    @Test
    public void testSetterInConcreteClass() {
        // DROOLS-1368
        final String drl = "import " + BaseFact.class.getCanonicalName() + ";\n"
                     + "import " + MyFact.class.getCanonicalName() + ";\n"
                     + "rule R when\n"
                     + "    $b : BaseFact( $n : name, name != null )\n"
                     + "then end\n";
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final MyFact f = new MyFact();
        final FactHandle fh = ksession.insert(f);
        assertThat(ksession.fireAllRules()).isEqualTo(0);
        f.setName("hello");
        ksession.update(fh, f, "name");
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public abstract class BaseFact {
        public abstract String getName();
    }

    public class MyFact extends BaseFact {

        private String name;

        @Override
        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

    @PropertyReactive
    public interface StopOrHub {

        boolean isVisitedByCoach();
    }

    @PropertyReactive
    public static class BusStop implements StopOrHub {

        private boolean visitedByCoach;

        @Override
        public boolean isVisitedByCoach() {
            return visitedByCoach;
        }

        public BusStop setVisitedByCoach(final boolean visitedByCoach) {
            this.visitedByCoach = visitedByCoach;
            return this;
        }
    }

    @PropertyReactive
    public static class Shuttle {

        private StopOrHub destination;

        public StopOrHub getDestination() {
            return destination;
        }

        public Shuttle setDestination(final StopOrHub destination) {
            this.destination = destination;
            return this;
        }

    }

    @Test
    public void testSetterInConcreteClass2() {
        // DROOLS-1369
        final String drl = "package org.test;\n"
                     + "import " + BusStop.class.getCanonicalName() + ";\n"
                     + "import " + Shuttle.class.getCanonicalName() + ";\n"
                     + "import " + StopOrHub.class.getCanonicalName() + ";\n"
                     + "rule shuttleDestinationIsCoachOrHub\n"
                     + "    when\n"
                     + "        $destination : StopOrHub(visitedByCoach == false)\n"
                     + "        Shuttle(destination == $destination)\n"
                     + "    then\n"
                     + "end";
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession kieSession = kbase.newKieSession();

        final BusStop busStop = new BusStop();
        final Shuttle shuttle = new Shuttle();
        busStop.setVisitedByCoach(true);

        final FactHandle fhShuttle = kieSession.insert(shuttle);
        final FactHandle fhBusStop = kieSession.insert(busStop);

        kieSession.update(fhShuttle, shuttle.setDestination(busStop));
        assertThat(kieSession.fireAllRules()).isEqualTo(0);

        // removing the property name from this update will make the test pass
        kieSession.update(fhBusStop, busStop.setVisitedByCoach(false), "visitedByCoach");
        // LHS is now satisfied, the rule should fire but it doesn't
        assertThat(shuttle.getDestination()).isEqualTo(busStop);
        assertThat(busStop.isVisitedByCoach()).isFalse();
        assertThat(kieSession.fireAllRules()).isEqualTo(1); // change the expected value to 0 to get further

        // after this update (without any real fact modification) the rule will fire as expected
        kieSession.update(fhBusStop, busStop);
        assertThat(kieSession.fireAllRules()).isEqualTo(1);
    }

    @Test
    public void testWatchFieldInExternalPattern() {
        // DROOLS-1445
        final String drl =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    $p1 : Person( name == \"Mario\" )\n" +
                "    $p2 : Person( age > $p1.age ) \n" +
                "then\n" +
                "    list.add(\"t0\");\n" +
                "end\n" +
                "rule Z when\n" +
                "    $p1 : Person( name == \"Mario\" ) \n" +
                "then\n" +
                "    modify($p1) { setAge(35); } \n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl); // default PropertySpecificOption.ALWAYS
        KieSession ksession = kbase.newKieSession();
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        final Person mario = new Person("Mario", 40);
        final Person mark = new Person("Mark", 37);
        final FactHandle fh_mario = ksession.insert(mario);
        ksession.insert(mark);
        final int x = ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("t0");
    }

    @Test
    public void testWatchFieldInExternalPatternWithNoConstraints() {
        // DROOLS-2333
        final String drl =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    $p1 : Person()\n" +
                "    $p2 : Person( name == \"Mark\", age > $p1.age ) \n" +
                "then\n" +
                "    list.add(\"t0\");\n" +
                "end\n" +
                "rule Z when\n" +
                "    $p1 : Person( name == \"Mario\" ) \n" +
                "then\n" +
                "    modify($p1) { setAge(35); } \n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl); // default PropertySpecificOption.ALWAYS
        KieSession ksession = kbase.newKieSession();
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        final Person mario = new Person("Mario", 40);
        final Person mark = new Person("Mark", 37);
        final FactHandle fh_mario = ksession.insert(mario);
        ksession.insert(mark);
        final int x = ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("t0");
    }

    @Test
    public void testWatchFieldInExternalNotPattern() {
        // DROOLS-1445
        final String drl =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "    $p1 : Person( name == \"Mario\" )\n" +
                "    not( Person( age < $p1.age ) )\n" +
                "then\n" +
                "    list.add(\"t0\");\n" +
                "end\n" +
                "rule Z when\n" +
                "    $p1 : Person( name == \"Mario\" ) \n" +
                "then\n" +
                "    modify($p1) { setAge(35); } \n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl); // default PropertySpecificOption.ALWAYS
        KieSession ksession = kbase.newKieSession();
        final List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        final Person mario = new Person("Mario", 40);
        final Person mark = new Person("Mark", 37);
        final FactHandle fh_mario = ksession.insert(mario);
        ksession.insert(mark);
        final int x = ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("t0");
    }

    @Test
    public void testPropertyChangeSupportNewAPI() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_PropertyChangeTypeDecl.drl");
        KieSession session = kbase.newKieSession();

        final List list = new ArrayList();
        session.setGlobal("list", list);

        final State state = new State("initial");
        session.insert(state);
        session.fireAllRules();

        assertThat(((List) session.getGlobal("list")).size()).isEqualTo(1);

        state.setFlag(true);
        assertThat(((List) session.getGlobal("list")).size()).isEqualTo(1);

        session.fireAllRules();
        // due to property reactivity the rule shouldn't fire again
        assertThat(((List) session.getGlobal("list")).size()).isEqualTo(1);

        state.setState("finished");

        session.dispose();

        // checks that the session removed itself from the bean listeners list
        assertThat(state.getPropertyChangeListeners().length).isEqualTo(0);
    }

    @Test
    public void testAccumulateWithPR() {

        String drl = "declare A end\n" +
                     " " +
                     "declare B\n" +
                     " value : String\n" +
                     "end\n" +
                     " " +
                     "rule Foo when\n" +
                     "    $s : A()\n" +
                     "    accumulate( String()\n" +
                     "                and\n" +
                     "                B( $val : value ),\n" +
                     "                $set : collectSet( $val )\n" +
                     "              )\n" +
                     "then end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
    }

    @Test
    public void testUpdateOnNonExistingProperty() {
        // DROOLS-2170
        final String str1 =
                "import " + Klass.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "  Klass( b == 2 )\n" +
                "then\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1);
        KieSession ksession = kbase.newKieSession();

        final Klass bean = new Klass( 1, 2, 3, 4, 5, 6 );
        final FactHandle fh = ksession.insert( bean );
        ksession.fireAllRules();

        try {
            ksession.update( fh, bean, "z" );
            fail("Trying to update not existing property must fail");
        } catch (Exception e) {
            assertThat(e.getMessage().contains(Klass.class.getName())).isTrue();
        }
    }

    @Test(timeout = 5000L)
    public void testPRAfterAccumulate() {
        // DROOLS-2427
        final String str1 =
                "import " + Order.class.getCanonicalName() + "\n" +
                "import " + OrderLine.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "        $o: Order($lines: orderLines)\n" +
                "        Number(intValue >= 15) from accumulate(\n" +
                "            OrderLine($q: quantity) from $lines\n" +
                "            , sum($q)\n" +
                "        )\n" +
                "    then\n" +
                "        modify($o) { setPrice(10) }\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1);
        KieSession ksession = kbase.newKieSession();

        Order order = new Order( Arrays.asList(new OrderLine( 9 ), new OrderLine( 8 )), 12 );
        ksession.insert( order );
        ksession.fireAllRules();

        assertThat(order.getPrice()).isEqualTo(10);
    }

    public static class Order {
        private final List<OrderLine> orderLines;

        private int price;

        public Order( List<OrderLine> orderLines, int price ) {
            this.orderLines = orderLines;
            this.price = price;
        }

        public List<OrderLine> getOrderLines() {
            return orderLines;
        }

        public int getPrice() {
            return price;
        }

        public void setPrice( int price ) {
            this.price = price;
        }
    }

    public static class OrderLine {
        private final int quantity;

        public OrderLine( int quantity ) {
            this.quantity = quantity;
        }

        public int getQuantity() {
            return quantity;
        }
    }

    public static class StrangeGetter {
        private int value;
        private int another;

        public int getValue() {
            return value;
        }

        public void setValue( int value ) {
            this.value = value;
        }

        public int getValue(int ignored) {
            return value;
        }

        public int getAnother() {
            return another;
        }

        public void setAnother( int another ) {
            this.another = another;
        }
    }

    @Test
    public void testGetterWithoutArgShouldBePropReactve() {
        // DROOLS-4288
        final String str1 =
                "import " + StrangeGetter.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "        $s: StrangeGetter(getValue() == 1)\n" +
                "    then\n" +
                "        modify($s) { setAnother(10) }\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1);
        KieSession ksession = kbase.newKieSession();

        StrangeGetter bean = new StrangeGetter();
        bean.setValue( 1 );
        ksession.insert( bean );

        assertThat(ksession.fireAllRules(3)).isEqualTo(1);
    }

    @Test
    public void testGetterWithArgShouldNotBePropReactve() {
        // DROOLS-4288
        final String str1 =
                "import " + StrangeGetter.class.getCanonicalName() + "\n" +
                "rule R when\n" +
                "        $s: StrangeGetter(getValue(1) == 1)\n" +
                "    then\n" +
                "        modify($s) { setAnother(10) }\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1);
        KieSession ksession = kbase.newKieSession();

        StrangeGetter bean = new StrangeGetter();
        bean.setValue( 1 );
        ksession.insert( bean );

        assertThat(ksession.fireAllRules(3)).isEqualTo(3);
    }

    @Test
    public void testComment() {
        // DROOLS-3583
        final String str1 =
                "package com.example\n" +
                "\n" +
                "declare Counter\n" +
                "    value: int\n" +
                "end\n" +
                "\n" +
                "rule \"Init\" when\n" +
                "    not Counter()\n" +
                "then\n" +
                "    drools.insert(new Counter(0));\n" +
                "end\n" +
                "\n" +
                "rule \"Loop\"\n" +
                "when\n" +
                "    $c: Counter()\n" +
                "then\n" +
                "// removing this comment line removes the loop\n" +
                "    $c.setValue(1);\n" +
                "    update($c);\n" +
                "end\n\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str1);
        KieSession ksession = kbase.newKieSession();

        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }
}
