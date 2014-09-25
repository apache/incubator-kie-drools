package org.drools.compiler.integrationtests;

import org.drools.compiler.Address;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.io.impl.ByteArrayResource;
import org.junit.Test;
import org.kie.api.definition.type.Modifies;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropertyReactivityTest extends CommonTestMethodBase {

    @Test(timeout=10000)
    public void testComposedConstraint() {
        String str =
                "package org.drools.test;\n" +
                "\n" +
                "import " + Klass2.class.getCanonicalName() + ";\n" +
                "\n" +
                "rule R when \n" +
                " $k2 : Klass2(b == 0 || c == 0)\n" +
                "then" +
                " modify($k2) { setD(1) }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Klass2 k2 = new Klass2(0, 0, 0, 0);
        ksession.insert(k2);
        assertEquals(1, ksession.fireAllRules());
        assertEquals(1, k2.getD());
    }

    @Test(timeout=10000)
    public void testScrambleProperties() {
        // DROOLS-91
        String str =
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals(2, list.size());
        assertTrue(list.contains("React"));
        assertTrue(list.contains("React2"));
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

        public Klass(int a, int b, int c, int d, int e, int f) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
            this.f = f;
        }

        public int getA() { return a; }
        public void setA(int a) { this.a = a; }

        public int getB() { return b; }
        public void setB(int b) { this.b = b; }

        public int getC() { return c; }
        public void setC(int c) { this.c = c; }

        public int getD() { return d; }
        public void setD(int d) { this.d = d; }

        public int getE() { return e; }
        public void setE(int e) { this.e = e; }

        public int getF() { return f; }
        public void setF(int f) { this.f = f; }

        public String getId() { return id; }
    }

    @PropertyReactive
    public static class Klass2 implements Intf2 {
        private String id = "k2";
        private int b;
        private int c;
        private int d;
        private int e;

        public Klass2( int b, int c, int d, int e ) {
            this.b = b;
            this.c = c;
            this.d = d;
            this.e = e;
        }

        public int getB() { return b; }
        public void setB(int b) { this.b = b; }

        public int getC() { return c; }
        public void setC(int c) { this.c = c; }

        public int getD() { return d; }
        public void setD(int d) { this.d = d; }

        public int getE() { return e; }
        public void setE(int e) { this.e = e; }

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
        String str =
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        System.out.println( list );

        assertTrue( list.containsAll( Arrays.asList( "k1@K1", "k1@I1", "k1@I2" ) ) );
        assertTrue( list.containsAll( Arrays.asList( "k2@K2", "k2@I2" ) ) );
        assertEquals( 5, list.size() );
    }

    @Test(timeout=10000)
    public void testScrambleWithInterfacesAndObject() {
        // DROOLS-91
        String str =
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( "Klass2", list.get(0) );
        assertEquals( "Klass2", list.get(1) );
    }

    @Test(timeout=10000)
    public void testWithDeclaredTypeAndTraitInDifferentPackages() {
        // DROOLS-91
        String str1 =
                "package org.pkg1;\n" +
                "declare trait Trait " +
                "    @propertyReactive\n" +
                "    a : int\n" +
                "end";

        String str2 =
                "package org.pkg2;\n" +
                "declare Bean " +
                "    @propertyReactive\n" +
                "    @Traitable\n" +
                "    a : int\n" +
                "    b : int\n" +
                "end";

        String str3 =
                "package org.pkg3;\n" +
                "import org.pkg1.Trait;\n" +
                "import org.pkg2.Bean;\n" +
                "rule Init\n" +
                "when\n" +
                "then\n" +
                "    insert(new Bean(1, 2));\n" +
                "end\n" +
                "rule R\n" +
                "when\n" +
                "   $b : Bean( b == 2)" +
                "then\n" +
                "   Trait t = don( $b, Trait.class, true );\n" +
                "   modify(t) { setA(2) };\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str1, str2, str3);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.fireAllRules();
    }

    @PropertyReactive @Traitable
    public static class Bean {
        private int a;
        private int b;

        public Bean() { }

        public Bean(int a, int b) {
            this.a = a;
            this.b = b;
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }
    }

    @Test(timeout=10000)
    public void testWithBeanAndTraitInDifferentPackages() {
        // DROOLS-91
        String str1 =
                "package org.drools.compiler.integrationtests;\n" +
                "declare trait Trait " +
                "    @propertyReactive\n" +
                "    a : int\n" +
                "end";

        String str2 =
                "package org.drools.test;\n" +
                "import org.drools.compiler.integrationtests.Trait;\n" +
                "import " + Bean.class.getCanonicalName() + ";\n" +
                "rule Init\n" +
                "when\n" +
                "then\n" +
                "    insert(new Bean(1, 2));\n" +
                "end\n" +
                "rule R\n" +
                "when\n" +
                "   $b : Bean( b == 2)" +
                "then\n" +
                "   Trait t = don( $b, Trait.class, true );\n" +
                "   modify(t) { setA(2) };\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str1, str2);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.fireAllRules();
    }

    @Test(timeout=10000)
    public void testRepeatedPatternWithPR() {
        // JBRULES-3705
        String str1 =
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


        KnowledgeBase kbase = loadKnowledgeBaseFromString( str1 );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( new BigDecimal( 10 ) ) );
        assertTrue(list.contains(new BigDecimal(5)));
    }

    @Test(timeout=10000)
    public void testPRWithCollections() {
        // DROOLS-135
        String str1 = "package org.test;\n" +
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


        KnowledgeBase kbase = loadKnowledgeBaseFromString( str1 );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertEquals( Arrays.asList( 0, 1, 2, 0, 3, 4, 0, 5, 0 ), list );

    }

    @Test(timeout=10000)
    public void testPRWithPositionalUnification() {
        // DROOLS-247
        String str1 =
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
                " Animal( $name := owner ) \n" +
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


        KnowledgeBase kbase = loadKnowledgeBaseFromString( str1 );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );

        ksession.insert( 1 );

        ksession.fireAllRules();

        assertTrue( list.contains( 1 ) );
        assertTrue( list.contains( 2 ) );
        assertEquals( 2, list.size() );

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
        String str =
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( Arrays.asList( "XXX -> Walter" ), list );
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
        String str =
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( Arrays.asList( "XXX -> Walter", "Find Heisenberg" ), list );
    }

    /**
     * Tests the use of PR on constraints involving 'virtual' properties
     * of a POJO: calculated properties without a setter.
     * getFullName doesn't have a setter but Klass4 states that setName()
     * and setLastName() both @Modifies fullName.
     */
    @Test(timeout=10000)
    public void testPRConstraintOnAttributesWithoutSetterUsingModifies(){
        String str =
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( Arrays.asList( "XXX -> Walter", "Find Heisenberg" ), list );
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
        String str =
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( Arrays.asList( "XXX White" ), list );
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
        String str =
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( Arrays.asList( "XXX White", "Walter White"  ), list );
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
        String str =
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

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertEquals( Arrays.asList( "XXX White", "Walter White"  ), list );
    }


    @Test(timeout=10000)
    public void testPRBindingOnNonexistingAttributes(){
        String str =
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

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( new ByteArrayResource( str.getBytes() ), ResourceType.DRL );

        System.out.println( knowledgeBuilder.getErrors() );
        assertTrue( knowledgeBuilder.hasErrors() );
    }

    @Test(timeout=10000)
    public void testPRBindingOnNonexistingWatchedAttribute(){
        String str =
                "package org.drools.test;\n" +
                "\n" +
                "import org.drools.compiler.integrationtests.PropertyReactivityTest.Klass4;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +

                "rule \"Get Person name\"\n" +
                "salience 1\n" +
                "when\n" +
                "  $x : Klass4( ) @watch( nmae )\n" +
                "then\n" +
                "end\n";

        KnowledgeBuilder knowledgeBuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        knowledgeBuilder.add( new ByteArrayResource( str.getBytes() ), ResourceType.DRL );

        System.out.println( knowledgeBuilder.getErrors() );
        assertTrue( knowledgeBuilder.hasErrors() );
    }

    @Test(timeout=10000)
    public void testModifyAfterInsertWithPropertyReactive() {
        String rule1 =
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

        KieHelper helper = new KieHelper();
        helper.addContent(rule1, ResourceType.DRL);
        KieSession ksession = helper.build().newKieSession();

        List list = new ArrayList();
        ksession.setGlobal("list", list);

        assertEquals(4, ksession.fireAllRules());

        assertEquals(3, list.size());

        assertEquals(3, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(1, list.get(2));
    }

    @PropertyReactive
    public static class Klass3 {
        private String name;
        private String lastName;

        public Klass3(String name, String lastName) {
            this.name = name;
            this.lastName = lastName;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
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

        public void setValue(String value) {
            this.value = value;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
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

        public Klass4(String name, String lastName) {
            this.name = name;
            this.lastName = lastName;
        }

        public String getName() {
            return name;
        }

        @Modifies("fullName")
        public void setName(String name) {
            this.name = name;
        }

        public String getLastName() {
            return lastName;
        }

        @Modifies("fullName")
        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getFullName(){
            return this.name + " "+ this.lastName;
        }
    }

    @Test(timeout=10000)
    public void testIndexedNotWatchedProperty() {
        // DROOLS-569
        String rule1 =
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

        KieHelper helper = new KieHelper();
        helper.addContent(rule1, ResourceType.DRL);
        KieSession ksession = helper.build().newKieSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        MyClass myClass = new MyClass();
        myClass.setValue("1");
        myClass.setData("x");
        ksession.insert(myClass);
        ksession.insert("1");
        ksession.insert(2);
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("1", list.get(0));
        list.clear();

        ksession.insert("3");
        ksession.fireAllRules();

        assertEquals(1, list.size());
        assertEquals("3", list.get(0));
    }

    @Test
    public void testModifyWithGetter() {
        String rule1 =
                "package foo.bar\n" +
                "import " + Person.class.getName() + "\n" +
                "declare Person @propertyReactive end\n" +
                "rule x\n" +
                "    when\n" +
                "       $p : Person( address != null ) @watch(!address) \n" +
                "    then\n" +
                "       modify($p){getAddress().setStreet(\"foo\");}\n" +
                "end";

        KieHelper helper = new KieHelper();
        helper.addContent(rule1, ResourceType.DRL);
        KieSession ksession = helper.build().newKieSession();

        Person p = new Person();
        p.setAddress(new Address());
        ksession.insert(p);

        int fired = ksession.fireAllRules(10);

        assertEquals(1, fired);
        assertEquals("foo", p.getAddress().getStreet());
    }

    @Test(timeout = 10000L)
    public void testMoreThan64Fields() {
        StringBuilder fields = new StringBuilder();
        for (int i = 10; i < 100; i++) {
            fields.append("  a").append(i).append(" : int\n");
        }
        String str =
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

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertEquals(1, list.size());
    }

    @Test(timeout = 10000L)
    public void testMoreThan64FieldsMultipleFirings() {
        StringBuilder fields = new StringBuilder();
        for (int i = 10; i < 100; i++) {
            fields.append("  a").append(i).append(" : int\n");
        }
        String str =
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

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertEquals(10, list.size());
    }

    @Test(timeout = 10000L)
    public void testMoreThan64FieldsWithWatch() {
        StringBuilder fields = new StringBuilder();
        for (int i = 10; i < 100; i++) {
            fields.append("  a").append(i).append(" : int\n");
        }
        String str =
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

        KieSession ksession = new KieHelper().addContent(str, ResourceType.DRL)
                                             .build()
                                             .newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();
        assertEquals(1, list.size());
    }
}
