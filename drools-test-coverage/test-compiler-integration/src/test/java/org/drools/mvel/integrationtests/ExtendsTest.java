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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.drools.core.common.DefaultEventHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.drl.ast.dsl.DescrFactory;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.mvel.DrlDumper;
import org.drools.mvel.compiler.Cheese;
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
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ClassObjectFilter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 * Test for declared bean Extension
 */
@RunWith(Parameterized.class)
public class ExtendsTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public ExtendsTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testExtends() throws Exception {
        //Test Base Fact Type
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_Extends.drl");
        KieSession ksession = kbase.newKieSession();

        FactType person = ksession.getKieBase().getFactType("defaultpkg","Person");
        FactType eqPair = ksession.getKieBase().getFactType("defaultpkg","EqualityPair");

        Object p = person.newInstance();
        assertThat(p).isNotNull();

        ksession.insert("Populate");
        ksession.fireAllRules();

        // A Rule will generate 3 Persons, one with default constructor, two with field constructor
        // and 3 Students, which extends Person. One with default, one with field constructor
        assertThat(ksession.getObjects(new ClassObjectFilter(person.getFactClass())).size()).isEqualTo(6);
        assertThat(ksession.getObjects(new ClassObjectFilter(eqPair.getFactClass())).size()).isEqualTo(1 + 4 + 1 + 4);
        ksession.dispose();
    }


    @Test
    public void testGeneratedMethods() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_Extends.drl");
        KieSession ksession = kbase.newKieSession();

        FactType student = ksession.getKieBase().getFactType("defaultpkg","Student");

        Constructor constructor = student.getFactClass().getConstructor(String.class,int.class,String.class);
        assertThat(constructor).isNotNull();
        Method equals = student.getFactClass().getMethod("equals",Object.class);
        assertThat(equals).isNotNull();

        Object s1 = constructor.newInstance("John",18,"Skool");
            assertThat(s1).isNotNull();
        assertThat(s1.toString()).isEqualTo("Student( name=John, age=18, school=Skool )");



        Object s2 = constructor.newInstance("John",25,"Skool");
            assertThat(s2).isNotNull();
        assertThat(s2.toString()).isEqualTo("Student( name=John, age=25, school=Skool )");
        assertThat(s2.hashCode()).isEqualTo(s1.hashCode());
        assertThat((Boolean) equals.invoke(s1, s2)).isTrue();
        assertThat(s1.equals(s2)).isTrue();


        Object s3 = constructor.newInstance("Mark",18,"Skool");
            assertThat(s3).isNotNull();
        assertThat(s3.toString()).isEqualTo("Student( name=Mark, age=18, school=Skool )");

            assertThat(s3.hashCode()).isNotEqualTo(s1.hashCode());
            assertThat(s3.hashCode()).isNotEqualTo(s2.hashCode());
        assertThat(s1.equals(s3)).isFalse();
        assertThat(s2.equals(s3)).isFalse();

        Object s4 = constructor.newInstance("John",25,"AnotherSkool");
            assertThat(s4).isNotNull();
        assertThat(s4.toString()).isEqualTo("Student( name=John, age=25, school=AnotherSkool )");

            assertThat(s4.hashCode()).isNotEqualTo(s1.hashCode());
            assertThat(s4.hashCode()).isNotEqualTo(s2.hashCode());
            assertThat(s4.hashCode()).isNotEqualTo(s3.hashCode());
        assertThat(s1.equals(s4)).isFalse();
        assertThat(s2.equals(s4)).isFalse();
        assertThat(s3.equals(s4)).isFalse();
        ksession.dispose();
    }

    @Test
    public void testDeepExt() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_Extends.drl");
        KieSession ksession = kbase.newKieSession();
        
        FactType LTstudent = ksession.getKieBase().getFactType("defaultpkg","LongTermStudent");

        Constructor constructor = LTstudent.getFactClass().getConstructor(String.class,int.class,String.class,String.class,int.class);
        assertThat(constructor).isNotNull();

        Object ls1 = constructor.newInstance("John",18,"Skool","C1245",4);
        Object ls2 = constructor.newInstance("John",33,"Skool","C1421",4);

        assertThat(ls2).isEqualTo(ls1);

        ksession.dispose();
    }



     @Test
    public void testIllegalExtendsLegacy() throws Exception {
        //Test Base Fact Type
        KieBuilder kieBuilder = KieUtil.getKieBuilderFromClasspathResources(kieBaseTestConfiguration, getClass(), false, "test_ExtLegacyIllegal.drl");
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
         assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @Test
    public void testExtendsLegacy() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_ExtLegacy.drl");
        KieSession ksession = kbase.newKieSession();

        FactType leg = ksession.getKieBase().getFactType("org.drools.compiler","BetterLegacy");
        assertThat(leg).isNotNull();

        Object b = leg.newInstance();

        assertThat(leg.get(b, "doubleField")).isEqualTo(3.3);
        assertThat(leg.get(b, "objField")).isNull();
        assertThat(leg.get(b, "intField")).isEqualTo(245);
        assertThat(leg.get(b, "strField")).isEqualTo("XX");
        assertThat(leg.get(b, "prop")).isEqualTo(true);
        assertThat(leg.get(b, "oneMoreField")).isEqualTo("Hello");

        System.out.println(b);

    }

    @Test
     public void testExtendsAcrossFiles() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_Ext1.drl", "test_Ext2.drl", "test_Ext3.drl", "test_Ext4.drl");
        KieSession ksession = kbase.newKieSession();

        FactType person = ksession.getKieBase().getFactType("org.drools.mvel.compiler.ext.test","Person");
            assertThat(person).isNotNull();
        FactType student = ksession.getKieBase().getFactType("org.drools.mvel.compiler.ext.test","Student");
            assertThat(student).isNotNull();

        FactType worker = ksession.getKieBase().getFactType("org.drools.mvel.compiler.anothertest","Worker");
            assertThat(worker).isNotNull();

        FactType ltss = ksession.getKieBase().getFactType("defaultpkg","SubLTStudent");
            assertThat(ltss).isNotNull();

        Constructor ctor = worker.getFactClass().getConstructor(String.class,int.class,String.class, double.class, int.class);
            assertThat(ctor).isNotNull();

        Object w = ctor.newInstance("Adam",20,"Carpenter",150.0,40);
        System.out.println(w);
        assertThat(worker.get(w, "name")).isEqualTo("Adam");

        ksession.fireAllRules();
    }




    @Test
     public void testFieldInit() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_ExtFieldInit.drl");
        KieSession ksession = kbase.newKieSession();

        FactType test = ksession.getKieBase().getFactType("org.drools.compiler", "MyBean3");

        Object x = test.newInstance();
        assertThat(x).isNotNull();

        assertThat(test.get(x, "fieldint")).isEqualTo(12);
        assertThat(test.get(x, "fieldstr")).isEqualTo("xyz");
        assertThat(test.get(x, "fielddbl")).isEqualTo(3.23);
        assertThat(test.get(x, "field0")).isEqualTo(0);
        assertThat(test.get(x, "field1")).isEqualTo(0.0f);
        assertThat(test.get(x, "fieldflt")).isEqualTo(1.2f);
        short sht = 2;
        assertThat(test.get(x, "fieldsht")).isEqualTo(sht);
        assertThat(test.get(x, "field2")).isEqualTo(0);
        byte byt = 1;
        assertThat(test.get(x, "fieldbyt")).isEqualTo(byt);
        assertThat(test.get(x, "fieldbln")).isEqualTo(true);
        assertThat(test.get(x, "fieldchr")).isEqualTo('x');
        assertThat(test.get(x, "fieldlng")).isEqualTo(9999L);


        System.out.println(x);

    }


    @Test
    public void testBoxedFieldInit() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_ExtFieldInit.drl");
        KieSession ksession = kbase.newKieSession();

        FactType test = ksession.getKieBase().getFactType("org.drools.compiler","MyBoxBean");

        Object x = test.newInstance();
        assertThat(x).isNotNull();

        assertThat(test.get(x, "fieldint")).isEqualTo(12);
        assertThat(test.get(x, "fielddbl")).isEqualTo(3.23);
        assertThat(test.get(x, "fieldflt")).isEqualTo(1.2f);
        short sht = 2;
        assertThat(test.get(x, "fieldsht")).isEqualTo(sht);
        byte byt = 1;
        assertThat(test.get(x, "fieldbyt")).isEqualTo(byt);
        assertThat(test.get(x, "fieldbln")).isEqualTo(true);
        assertThat(test.get(x, "fieldchr")).isEqualTo('x');
        assertThat(test.get(x, "fieldlng")).isEqualTo(9999L);

        System.out.println(x);

    }


    @Test
    public void testExpressionFieldInit() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_ExtFieldInit.drl");
        KieSession ksession = kbase.newKieSession();
        FactType test = ksession.getKieBase().getFactType("org.drools.compiler","MyBoxExpressionBean");

        Object x = test.newInstance();
        assertThat(x).isNotNull();

        assertThat(test.get(x, "f0")).isEqualTo("foobar");
        assertThat(test.get(x, "fieldint")).isEqualTo(-32);
        assertThat(test.get(x, "fielddbl")).isEqualTo(4.0);
        assertThat(test.get(x, "fieldflt")).isEqualTo(0.5f);
        short sht = 6;
        assertThat(test.get(x, "fieldsht")).isEqualTo(sht);
        byte byt = 2;
        assertThat(test.get(x, "fieldbyt")).isEqualTo(byt);
        assertThat(test.get(x, "fieldbln")).isEqualTo(true);
        assertThat(test.get(x, "fieldchr")).isEqualTo('x');
        assertThat(test.get(x, "fieldlng")).isEqualTo(9999L);

        System.out.println(x);



        FactType test2 = ksession.getKieBase().getFactType("org.drools.compiler","MySimpleExpressionBean");

        x = test2.newInstance();
        assertThat(x).isNotNull();

        assertThat(test2.get(x, "f0")).isEqualTo("foobar");
        assertThat(test2.get(x, "fieldint")).isEqualTo(-32);
        assertThat(test2.get(x, "fielddbl")).isEqualTo(4.0);
        assertThat(test2.get(x, "fieldflt")).isEqualTo(0.5f);
        sht = 6;
        assertThat(test2.get(x, "fieldsht")).isEqualTo(sht);
        byt = 2;
        assertThat(test2.get(x, "fieldbyt")).isEqualTo(byt);
        assertThat(test2.get(x, "fieldbln")).isEqualTo(true);
        assertThat(test2.get(x, "fieldchr")).isEqualTo('x');
        assertThat(test2.get(x, "fieldlng")).isEqualTo(9999L);

        System.out.println(x);
    }

    @Test
    public void testHierarchy() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_ExtHierarchy.drl");
        KieSession ksession = kbase.newKieSession();
        ksession.setGlobal("list",new LinkedList());

        ksession.fireAllRules();

        assertThat(((List) ksession.getGlobal("list")).size()).isEqualTo(1);
    }

    @Test
    public void testExtendOverride() {

        String drl = "package test.beans;\n" +
                "\n" +
                "import java.util.List;\n" +
                "import java.util.ArrayList;\n" +
                "\n" +
                "global List ans;" +
                "\n" +
                "\n" +
                "declare ArrayList\n" +
                "end\n" +
                "" +
                "declare Bean extends ArrayList\n" +
                "  fld : int = 4 \n" +
                "  myField : String = \"xxx\" \n" +
                "end\n" +
                "\n" +
                "declare Bean2 extends Bean\n" +
                "  moref : double\n" +
                "  myField : String\n" +
                "end\n" +

                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  Bean b = new Bean2();\n" +
                "  ans.add(b);" +
                "  System.out.println(b);\t\n" +
                "end\n"
                ;

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        List out = new ArrayList();
        ksession.setGlobal("ans",out);

        ksession.fireAllRules();
        Object x = out.get(0);

        FactType type = kbase.getFactType("test.beans","Bean2");
        assertThat(type.get(x, "fld")).isEqualTo(4);
        assertThat(type.get(x, "myField")).isEqualTo("xxx");
        assertThat(type.get(x, "moref")).isEqualTo(0.0);

        assertThat(x instanceof ArrayList).isTrue();
    }

    @Test
    public void testRedefineDefaults() throws Exception {
        //Test Base Fact Type
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_Extends.drl");
        KieSession ksession = kbase.newKieSession();

        FactType person = ksession.getKieBase().getFactType("defaultpkg","Person");
        FactType student = ksession.getKieBase().getFactType("defaultpkg","Student");

        Object p = person.newInstance();
        Object s = student.newInstance();
        assertThat(p).isNotNull();
        assertThat(s).isNotNull();

        assertThat(person.get(p, "age")).isEqualTo(99);
        assertThat(person.get(s, "age")).isEqualTo(18);

        ksession.dispose();
    }

    @Test
    public void testExtendFromOtherPackage() throws Exception {

        String s1 = "package org.drools.mvel.compiler.test.pack1;\n" +
                "\n" +
                "declare Base\n" +
                "  id    : int\n" +
                "end\n" +
                "\n" +
                "declare Sub extends Base\n" +
                "  field : int\n" +
                "end\n";

        String s2 = "package org.drools.mvel.compiler.test.pack2;\n" +
                "\n" +
                "import org.drools.mvel.compiler.test.pack1.Base;\n" +
                "import org.drools.mvel.compiler.test.pack1.Sub;\n" +
                "\n" +
                "declare Sub end\n" +
                "\n" +
                "declare SubChild extends Sub\n" +
                "  field2 : int\n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new SubChild( 1, 2, 3 ) );\n" +
                "end\n" +
                "\n" +
                "rule \"Touch Base\"\n" +
                "when\n" +
                "  $b : Base()\n" +
                "then\n" +
                "  System.out.println( $b );\n" +
                "end \n" +
                "rule \"Touch Base 2\"\n" +
                "when\n" +
                "  $c : Sub()\n" +
                "then\n" +
                "  System.out.println( $c );\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, s1, s2);
        KieSession kSession = kbase.newKieSession();

        assertThat(kSession.fireAllRules()).isEqualTo(3);
    }

    @Test
    public void testInheritAnnotationsInOtherPackage() throws Exception {

        String s1 = "package org.drools.mvel.compiler.test.pack1;\n" +
                "global java.util.List list;" +
                "\n" +
                "declare Event\n" +
                "@role(event)" +
                "  id    : int\n" +
                "end\n" +
                "\n" +
                "rule \"X\"\n" +
                "when\n" +
                "  $s1 : Event()\n" +
                "then\n" +
                "  System.out.println( $s1 );\n" +
                "  list.add( $s1.getId() );\n " +
                "end";


        String s2 = "package org.drools.mvel.compiler.test.pack2;\n" +
                "\n" +
                "import org.drools.mvel.compiler.test.pack1.Event;\n" +
                "global java.util.List list;" +
                "\n" +
                "declare Event end\n" +
                "\n" +
                "declare SubEvent extends Event\n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  list.add( 0 );\n" +
                "  insert( new SubEvent( 1 ) );\n" +
                "  insert( new SubEvent( 2 ) );\n" +
                "end\n" +
                "\n" +
                "rule \"Seq\"\n" +
                "when\n" +
                "  $s1 : SubEvent( id == 1 )\n" +
                "  $s2 : SubEvent( id == 2, this after[0,10s] $s1 )\n" +
                "then\n" +
                "  list.add( 3 );\n" +
                "  System.out.println( $s1 + \" after \" + $s1 );\n" +
                "end \n" +
                "\n" +
                "rule \"Seq 2 \"\n" +
                "when\n" +
                "  $s1 : Event( id == 1 )\n" +
                "  $s2 : Event( id == 2, this after[0,10s] $s1 )\n" +
                "then\n" +
                "  list.add( 4 );\n" +
                "  System.out.println( $s1 + \" after II \" + $s1 );\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, s1, s2);
        KieSession kSession = kbase.newKieSession();

        List list = new ArrayList();
        kSession.setGlobal( "list", list );

        kSession.fireAllRules();

        for ( Object o : kSession.getObjects() ) {
            FactHandle h = kSession.getFactHandle( o );
            assertThat(h instanceof DefaultEventHandle).isTrue();
        }

        System.out.println( list );
        assertThat(list.size()).isEqualTo(5);
        assertThat(list.contains(0)).isTrue();
        assertThat(list.contains(1)).isTrue();
        assertThat(list.contains(2)).isTrue();
        assertThat(list.contains(3)).isTrue();
        assertThat(list.contains(4)).isTrue();

    }

    @Test
    public void testInheritFromClassWithDefaults() throws Exception {

        // car is a java class with attributes
        // brand (default ferrari)
        // expensive (default true)
        String s1 = "package org.drools.mvel.compiler;\n" +
                "global java.util.List list;\n" +
                "declare Car end\n" +
                "\n" +
                "declare MyCar extends Car \n" +
                " miles : int\n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  MyCar c = new MyCar();\n" +
                "  c.setMiles( 100 );" +
                "  insert( c );\n" +
                "end\n" +
                "rule \"Match\"\n" +
                "when\n" +
                "  MyCar( brand == \"ferrari\", expensive == true, $miles : miles ) " +
                "then\n" +
                "  list.add( $miles );\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, s1);
        KieSession kSession = kbase.newKieSession();

        List list = new ArrayList();
        kSession.setGlobal( "list", list );

        kSession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.contains(100)).isTrue();

    }


    @Test
    public void testExtendSelf() throws Exception {
        String s1 = "package org.drools;\n" +
                    "global java.util.List list;\n" +
                    "\n" +
                    "declare Bean extends Bean \n" +
                    " foo : int @key\n" +
                    "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, s1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @Test
    public void testExtendCircular() throws Exception {
        String s1 = "package org.drools;\n" +
                    "global java.util.List list;\n" +
                    "\n" +
                    "declare Bean1 extends Bean2 \n" +
                    " foo : int @key\n" +
                    "end\n" +
                    "" +
                    "declare Bean2 extends Bean3 \n" +
                    " foo : int @key\n" +
                    "end\n" +
                    "" +
                    "declare Bean3 extends Bean1 \n" +
                    " foo : int @key\n" +
                    "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, s1);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();

        System.out.println( errors );
        assertThat(errors.toString().contains("circular")).isTrue();
    }

    @Test
    public void testExtendsDump() {
        PackageDescrBuilder pkgd = DescrFactory.newPackage();
        pkgd.name( "org.test" )
            .newDeclare().type().name( "Foo" )
            .newField( "id" ).type( "int" ).end()
            .end()
            .newDeclare().type().name( "Bar" ).superType( "Foo" )
            .newField( "val" ).type( "int" ).initialValue( "42" ).end()
            .end();
        String drl = new DrlDumper().dump( pkgd.getDescr() );

        KieBase kb = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

        FactType bar = kb.getFactType( "org.test", "Bar" );
        try {
            Object x = bar.newInstance();
            assertThat(bar.get(x, "val")).isEqualTo(42);
            bar.set( x, "id", 1 );
            assertThat(bar.get(x, "id")).isEqualTo(1);
        } catch ( Exception e ) {
            fail( e.getMessage() );
        }
    }

    public static interface A {}

    public static interface B extends A {}

    public static interface C extends B {}

    public static class X implements C {
        private int x = 1;
        public int getX() { return x; }
    }


    @Test
    public void testDeclareInheritance() throws Exception {
        String s1 = "package org.drools;\n" +
                    "import org.drools.mvel.integrationtests.ExtendsTest.*;\n" +
                    "\n" +
                    "declare A \n" +
                    " @role( event )" +
                    " @typesafe( false )\n" +
                    "end\n" +
                    "" +
                    "declare C @role( event ) @typesafe( false ) end \n" +
                    "" +
                    "rule R \n" +
                    "when " +
                    "   $x : C( this.x == 1 ) \n" +
                    "then\n" +
                    "   System.out.println( $x ); \n" +
                    "end\n" +
                    "";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, s1);
        KieSession knowledgeSession = kbase.newKieSession();
        
        FactHandle h = knowledgeSession.insert( new X() );

        assertThat(((InternalFactHandle) h).isEvent()).isTrue();

    }

    @Test
    public void testDeclareExtendsJavaParent() {
        String drl = "package org.drools.test; \n" +
                     "import org.drools.mvel.compiler.Person; \n" +
                     "declare Student extends Person end \n" +
                     "";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
    }

    @Test
    public void testDeclareExtendsJavaParentOuterPackaga() {
        String drl = "package org.drools.test; \n" +
                     "import org.drools.mvel.integrationtests.ExtendsTest.X; \n" +
                     "declare Student extends X end \n" +
                     "";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
    }

    @Test
    public void testDeclareExtendsMissingJavaParent() {
        String drl = "package org.drools.test; \n" +
                     "import org.drools.mvel.integrationtests.ExtendsTest.Y; \n" +
                     "declare Student extends Y end \n" +
                     "";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @Test
    public void testDeclareExtendsWithFullyQualifiedName() {
        String drl = "package org.drools.extends.test; \n" +
                     "" +
                     "declare org.drools.extends.test.Foo end \n" +
                     "declare org.drools.extends.test.Bar extends org.drools.extends.test.Foo end \n" +
                     "";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();

    }

    @Test
    public void testExtendsBasic() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "extend_rule_test.drl");
        KieSession session = kbase.newKieSession();

        //Test 2 levels of inheritance, and basic rule
        List list = new ArrayList();
        session.setGlobal("list", list);
        final Cheese mycheese = new Cheese("cheddar", 4);
        final FactHandle handle = session.insert(mycheese);
        session.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(list.contains("rule 4")).isTrue();
        assertThat(list.contains("rule 2b")).isTrue();

        //Test 2nd level (parent) to make sure rule honors the extend rule
        list = new ArrayList();
        session.setGlobal("list", list);
        session.delete(handle);
        final Cheese mycheese2 = new Cheese("notcheddar", 4);
        final FactHandle handle2 = session.insert(mycheese2);
        session.fireAllRules();

        assertThat(list.get(0)).isEqualTo("rule 4");
        assertThat(list.size()).isEqualTo(1);

        //Test 3 levels of inheritance, all levels
        list = new ArrayList();
        session.setGlobal("list", list);
        session.delete(handle2);
        final Cheese mycheese3 = new Cheese("stilton", 6);
        final FactHandle handle3 = session.insert(mycheese3);
        session.fireAllRules();
        //System.out.println(list.toString());
        assertThat(list.get(0)).isEqualTo("rule 3");
        assertThat(list.size()).isEqualTo(1);

        //Test 3 levels of inheritance, third only
        list = new ArrayList();
        session.setGlobal("list", list);
        session.delete(handle3);
        final Cheese mycheese4 = new Cheese("notstilton", 6);
        final FactHandle handle4 = session.insert(mycheese4);
        session.fireAllRules();
        //System.out.println(((List) session.getGlobal( "list" )).toString());
        assertThat(((List) session.getGlobal("list")).size() == 0).isTrue();

        //Test 3 levels of inheritance, 2nd only
        list = new ArrayList();
        session.setGlobal("list", list);
        session.delete(handle4);
        final Cheese mycheese5 = new Cheese("stilton", 7);
        session.insert(mycheese5);
        session.fireAllRules();
        assertThat(list.size()).isEqualTo(0);
    }

    @Test
    public void testExtendsBasic2() {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_RuleExtend.drl");
        KieSession ksession = kbase.newKieSession();

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        final Cheese stilton = new Cheese("stilton", 5);
        final Cheese cheddar = new Cheese("cheddar", 7);
        final Cheese brie = new Cheese("brie", 5);

        ksession.insert(stilton);
        ksession.insert(cheddar);
        ksession.insert(brie);

        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.get(0)).isEqualTo("stilton");
        assertThat(results.get(1)).isEqualTo("brie");
    }

}

