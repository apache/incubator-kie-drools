/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.integrationtests;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.common.EventFactHandle;
import org.drools.definition.type.FactType;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ByteArrayResource;
import org.drools.runtime.ClassObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.junit.Test;

/**
 * Test for declared bean Extension
 */
public class ExtendsTest extends CommonTestMethodBase {



    public StatefulKnowledgeSession genSession(String source) {
        return genSession(new String[] {source},0);
    }

     public StatefulKnowledgeSession genSession(String source, int numerrors)  {
        return genSession(new String[] {source},numerrors);
    }

    public StatefulKnowledgeSession genSession(String[] sources, int numerrors)  {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (String source : sources)
            kbuilder.add( ResourceFactory.newClassPathResource(source, getClass()), ResourceType.DRL );
        KnowledgeBuilderErrors errors = kbuilder.getErrors();
        if ( kbuilder.getErrors().size() > 0 ) {
            for ( KnowledgeBuilderError error : kbuilder.getErrors() ) {
                System.err.println( error );
            }
        }
        assertEquals(numerrors, errors.size() );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();

        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return createKnowledgeSession(kbase);

    }

    @Test
    public void testExtends() throws Exception {
        //Test Base Fact Type
        StatefulKnowledgeSession ksession = genSession("test_Extends.drl");

        FactType person = ksession.getKnowledgeBase().getFactType("defaultpkg","Person");
        FactType eqPair = ksession.getKnowledgeBase().getFactType("defaultpkg","EqualityPair");

        Object p = person.newInstance();
        assertNotNull(p);

        ksession.insert("Populate");
        ksession.fireAllRules();

        // A Rule will generate 3 Persons, one with default constructor, two with field constructor
        // and 3 Students, which extends Person. One with default, one with field constructor
        assertEquals(6, ksession.getObjects(new ClassObjectFilter(person.getFactClass())).size());
        assertEquals(1+4+1+4,ksession.getObjects(new ClassObjectFilter(eqPair.getFactClass())).size());
        ksession.dispose();
    }


    @Test
    public void testGeneratedMethods() throws Exception {
        StatefulKnowledgeSession ksession = genSession("test_Extends.drl");

        FactType student = ksession.getKnowledgeBase().getFactType("defaultpkg","Student");

        Constructor constructor = student.getFactClass().getConstructor(String.class,int.class,String.class);
        assertNotNull(constructor);
        Method equals = student.getFactClass().getMethod("equals",Object.class);
        assertNotNull(equals);

        Object s1 = constructor.newInstance("John",18,"Skool");
            assertNotNull(s1);
            assertEquals("Student( name=John, age=18, school=Skool )", s1.toString());



        Object s2 = constructor.newInstance("John",25,"Skool");
            assertNotNull(s2);
            assertEquals("Student( name=John, age=25, school=Skool )", s2.toString());
            assertEquals(s1.hashCode(),s2.hashCode());
            assertTrue((Boolean) equals.invoke(s1,s2));
            assertTrue(s1.equals(s2));


        Object s3 = constructor.newInstance("Mark",18,"Skool");
            assertNotNull(s3);
            assertEquals("Student( name=Mark, age=18, school=Skool )", s3.toString());

            assertNotSame(s1.hashCode(),s3.hashCode());
            assertNotSame(s2.hashCode(),s3.hashCode());
            assertFalse(s1.equals(s3));
            assertFalse(s2.equals(s3));

        Object s4 = constructor.newInstance("John",25,"AnotherSkool");
            assertNotNull(s4);
            assertEquals("Student( name=John, age=25, school=AnotherSkool )", s4.toString());

            assertNotSame(s1.hashCode(),s4.hashCode());
            assertNotSame(s2.hashCode(),s4.hashCode());
            assertNotSame(s3.hashCode(),s4.hashCode());
            assertFalse(s1.equals(s4));
            assertFalse(s2.equals(s4));
            assertFalse(s3.equals(s4));
        ksession.dispose();
    }

    @Test
    public void testDeepExt() throws Exception {
        StatefulKnowledgeSession ksession = genSession("test_Extends.drl");
        FactType LTstudent = ksession.getKnowledgeBase().getFactType("defaultpkg","LongTermStudent");

        Constructor constructor = LTstudent.getFactClass().getConstructor(String.class,int.class,String.class,String.class,int.class);
        assertNotNull(constructor);

        Object ls1 = constructor.newInstance("John",18,"Skool","C1245",4);
        Object ls2 = constructor.newInstance("John",33,"Skool","C1421",4);

        assertEquals(ls1,ls2);

        ksession.dispose();
    }



     @Test
    public void testIllegalExtendsLegacy() throws Exception {
        //Test Base Fact Type
        genSession("test_ExtLegacyIllegal.drl",2);

    }

    @Test
    public void testExtendsLegacy() throws Exception {
        StatefulKnowledgeSession ksession = genSession("test_ExtLegacy.drl",0);

        FactType leg = ksession.getKnowledgeBase().getFactType("org.drools","BetterLegacy");
        assertNotNull(leg);

        Object b = leg.newInstance();

        assertEquals(3.3,leg.get(b,"doubleField"));
        assertNull(leg.get(b,"objField"));
        assertEquals(245,leg.get(b,"intField"));
        assertEquals("XX",leg.get(b,"strField"));
        assertEquals(true,leg.get(b,"prop"));
        assertEquals("Hello",leg.get(b,"oneMoreField"));

        System.out.println(b);

    }





     public void testExtendsAcrossFiles() throws Exception {
        StatefulKnowledgeSession ksession = genSession(new String[] {"test_Ext1.drl","test_Ext2.drl","test_Ext3.drl","test_Ext4.drl"} ,0);

        FactType person = ksession.getKnowledgeBase().getFactType("org.drools.compiler.test","Person");
            assertNotNull(person);
        FactType student = ksession.getKnowledgeBase().getFactType("org.drools.compiler.test","Student");
            assertNotNull(student);

        FactType worker = ksession.getKnowledgeBase().getFactType("org.drools.compiler.anothertest","Worker");
            assertNotNull(worker);

        FactType ltss = ksession.getKnowledgeBase().getFactType("defaultpkg","SubLTStudent");
            assertNotNull(ltss);

        Constructor ctor = worker.getFactClass().getConstructor(String.class,int.class,String.class, double.class, int.class);
            assertNotNull(ctor);

        Object w = ctor.newInstance("Adam",20,"Carpenter",150.0,40);
        System.out.println(w);
            assertEquals("Adam",worker.get(w,"name"));

        ksession.fireAllRules();
    }





     public void testFieldInit() throws Exception {
        StatefulKnowledgeSession ksession = genSession("test_ExtFieldInit.drl");
        FactType test = ksession.getKnowledgeBase().getFactType("org.drools.compiler", "MyBean3");

        Object x = test.newInstance();
        assertNotNull(x);

        assertEquals(12,test.get(x,"fieldint"));
        assertEquals("xyz",test.get(x,"fieldstr"));
        assertEquals(3.23,test.get(x,"fielddbl"));
        assertEquals(0,test.get(x,"field0"));
        assertEquals(0.0f,test.get(x,"field1"));
        assertEquals(1.2f,test.get(x,"fieldflt"));
        short sht = 2;
        assertEquals(sht,test.get(x,"fieldsht"));
        assertEquals(0,test.get(x,"field2"));
        byte byt = 1;
        assertEquals(byt,test.get(x,"fieldbyt"));
        assertEquals(true,test.get(x,"fieldbln"));
        assertEquals('x',test.get(x,"fieldchr"));
        assertEquals(9999L,test.get(x,"fieldlng"));


        System.out.println(x);

    }



    public void testBoxedFieldInit() throws Exception {
        StatefulKnowledgeSession ksession = genSession("test_ExtFieldInit.drl");
        FactType test = ksession.getKnowledgeBase().getFactType("org.drools.compiler","MyBoxBean");

        Object x = test.newInstance();
        assertNotNull(x);

        assertEquals(12,test.get(x,"fieldint"));
        assertEquals(3.23,test.get(x,"fielddbl"));
        assertEquals(1.2f,test.get(x,"fieldflt"));
        short sht = 2;
        assertEquals(sht,test.get(x,"fieldsht"));
        byte byt = 1;
        assertEquals(byt,test.get(x,"fieldbyt"));
        assertEquals(true,test.get(x,"fieldbln"));
        assertEquals('x',test.get(x,"fieldchr"));
        assertEquals(9999L,test.get(x,"fieldlng"));

        System.out.println(x);

    }



    public void testExpressionFieldInit() throws Exception {
        StatefulKnowledgeSession ksession = genSession("test_ExtFieldInit.drl");
        FactType test = ksession.getKnowledgeBase().getFactType("org.drools.compiler","MyBoxExpressionBean");

        Object x = test.newInstance();
        assertNotNull(x);

        assertEquals("foobar",test.get(x,"f0"));
        assertEquals(-32,test.get(x,"fieldint"));
        assertEquals(4.0,test.get(x,"fielddbl"));
        assertEquals(0.5f,test.get(x,"fieldflt"));
        short sht = 6;
        assertEquals(sht,test.get(x,"fieldsht"));
        byte byt = 2;
        assertEquals(byt,test.get(x,"fieldbyt"));
        assertEquals(true,test.get(x,"fieldbln"));
        assertEquals('x',test.get(x,"fieldchr"));
        assertEquals(9999L,test.get(x,"fieldlng"));

        System.out.println(x);



        FactType test2 = ksession.getKnowledgeBase().getFactType("org.drools.compiler","MySimpleExpressionBean");

        x = test2.newInstance();
        assertNotNull(x);

        assertEquals("foobar",test2.get(x,"f0"));
        assertEquals(-32,test2.get(x,"fieldint"));
        assertEquals(4.0,test2.get(x,"fielddbl"));
        assertEquals(0.5f,test2.get(x,"fieldflt"));
        sht = 6;
        assertEquals(sht,test2.get(x,"fieldsht"));
        byt = 2;
        assertEquals(byt,test2.get(x,"fieldbyt"));
        assertEquals(true,test2.get(x,"fieldbln"));
        assertEquals('x',test2.get(x,"fieldchr"));
        assertEquals(9999L,test2.get(x,"fieldlng"));

        System.out.println(x);

    }


    public void testHierarchy() throws Exception {
        StatefulKnowledgeSession ksession = genSession("test_ExtHierarchy.drl");
        ksession.setGlobal("list",new LinkedList());

        ksession.fireAllRules();

        assertEquals(1, ((List) ksession.getGlobal("list")).size());


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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List out = new ArrayList();
        ksession.setGlobal("ans",out);

        ksession.fireAllRules();
        Object x = out.get(0);

        FactType type = kbase.getFactType("test.beans","Bean2");
        assertEquals(4, type.get( x, "fld") );
        assertEquals("xxx", type.get( x, "myField") );
        assertEquals(0.0, type.get( x, "moref") );


        assertTrue( x instanceof ArrayList );
    }




    @Test
    public void testRedefineDefaults() throws Exception {
        //Test Base Fact Type
        StatefulKnowledgeSession ksession = genSession("test_Extends.drl");

        FactType person = ksession.getKnowledgeBase().getFactType("defaultpkg","Person");
        FactType student = ksession.getKnowledgeBase().getFactType("defaultpkg","Student");

        Object p = person.newInstance();
        Object s = student.newInstance();
        assertNotNull(p);
        assertNotNull(s);

        assertEquals( 99, person.get(p,"age") );
        assertEquals( 18, person.get(s,"age") );

        ksession.dispose();
    }



    @Test
    public void testExtendFromOtherPackage() throws Exception {

        String s1 = "package org.drools.test.pack1;\n" +
                "\n" +
                "declare Base\n" +
                "  id    : int\n" +
                "end\n" +
                "\n" +
                "declare Sub extends Base\n" +
                "  field : int\n" +
                "end\n";

        String s2 = "package org.drools.test.pack2;\n" +
                "\n" +
                "import org.drools.test.pack1.Base;\n" +
                "import org.drools.test.pack1.Sub;\n" +
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

        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();


        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kBuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );

        if ( kBuilder.hasErrors() ) {
            System.err.println( kBuilder.getErrors().toString() );
            fail();
        }
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );


        KnowledgeBuilder kBuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder( kBase );
        kBuilder2.add( new ByteArrayResource( s2.getBytes() ), ResourceType.DRL );

        if ( kBuilder2.hasErrors() ) {
            System.err.println( kBuilder2.getErrors().toString() );
            fail();
        }
        kBase.addKnowledgePackages( kBuilder2.getKnowledgePackages() );

        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();

        assertEquals( 3, kSession.fireAllRules() );
    }




    @Test
    public void testInheritAnnotationsInOtherPackage() throws Exception {

        String s1 = "package org.drools.test.pack1;\n" +
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


        String s2 = "package org.drools.test.pack2;\n" +
                "\n" +
                "import org.drools.test.pack1.Event;\n" +
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



        KnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();


        KnowledgeBuilder kBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder(  );
        kBuilder.add( new ByteArrayResource( s1.getBytes() ), ResourceType.DRL );

        if ( kBuilder.hasErrors() ) {
            fail( kBuilder.getErrors().toString() );
        }
        kBase.addKnowledgePackages( kBuilder.getKnowledgePackages() );


        KnowledgeBuilder kBuilder2 = KnowledgeBuilderFactory.newKnowledgeBuilder( kBase );
        kBuilder2.add( new ByteArrayResource( s2.getBytes() ), ResourceType.DRL );

        if ( kBuilder2.hasErrors() ) {
            fail( kBuilder2.getErrors().toString() );
        }
        kBase.addKnowledgePackages( kBuilder2.getKnowledgePackages() );

        StatefulKnowledgeSession kSession = kBase.newStatefulKnowledgeSession();
        List list = new ArrayList();
        kSession.setGlobal( "list", list );

        kSession.fireAllRules();

        for ( Object o : kSession.getObjects() ) {
            FactHandle h = kSession.getFactHandle( o );
            assertTrue( h instanceof EventFactHandle );
        }

        System.out.println( list );
        assertEquals( 5, list.size() );
        assertTrue( list.contains( 0 ) );
        assertTrue( list.contains( 1 ) );
        assertTrue( list.contains( 2 ) );
        assertTrue( list.contains( 3 ) );
        assertTrue( list.contains( 4 ) );

    }




}

