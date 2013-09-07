package org.drools.integrationtests;

import org.drools.CommonTestMethodBase;
import org.drools.KnowledgeBase;
import org.drools.definition.type.PropertyReactive;
import org.drools.factmodel.traits.Traitable;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropertyReactivityTest extends CommonTestMethodBase {

    @Test
    public void testComposedConstraint() {
        String str =
                "package org.drools.test;\n" +
                "\n" +
                "import org.drools.integrationtests.PropertyReactivityTest.Klass2;\n" +
                "\n" +
                "rule R when \n" +
                "    $k2 : Klass2(b == 0 || c == 0)\n" +
                "then" +
                "    modify($k2) { setD(1) }\n" +
                "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Klass2 k2 = new Klass2(0, 0, 0, 0);
        ksession.insert(k2);
        assertEquals(1, ksession.fireAllRules());
        assertEquals(1, k2.getD());
    }

    @Test
    public void testScrambleProperties() {
        // DROOLS-91
        String str =
                "package org.drools.test\n" +
                "    global java.util.List list" +
                "\n" +
                "    declare Parent\n" +
                "    @propertyReactive\n" +
                "    a : int\n" +
                "    k : int\n" +
                "    z : int\n" +
                "            end\n" +
                "\n" +
                "    declare Child extends Parent\n" +
                "    @propertyReactive\n" +
                "    p : int\n" +
                "            end\n" +
                "\n" +
                "\n" +
                "    rule Init\n" +
                "    when\n" +
                "            then\n" +
                "    insert( new Child( 1, 3, 5, 7 ) );\n" +
                "    end\n" +
                "\n" +
                "    rule Mod\n" +
                "    when\n" +
                "    $p : Parent()\n" +
                "    then\n" +
                "    modify( $p ) { setZ( 99 ); }\n" +
                "    end\n" +
                "\n" +
                "    rule React2\n" +
                "    when\n" +
                "    Child( p == 7 )\n" +
                "    then\n" +
                "    list.add( \"React2\" );\n" +
                "    end\n" +
                "\n" +
                "    rule React\n" +
                "    when\n" +
                "    Child( z == 99 )\n" +
                "    then\n" +
                "    list.add( \"React\" );\n" +
                "    end";

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
        public void setId( String id );
    }

    @PropertyReactive
    public static interface Intf2 {
        public int getD();
        public void setD(int d);

        public int getE();
        public void setE(int e);

        public String getId();
        public void setId( String id );
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
        public void setId( String id ) { this.id = id; };
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
        public void setId( String id ) { this.id = id; }
    }


    @Test
    public void testScrambleWithInterfaces() {
    /*
    * K1 a b c d e f   1000
    * I1     c d   f   10
    * I2       d e     1
    * K2   b c d e     100
    */

        // DROOLS-91
        String str =
                "package org.drools.test;\n" +
                "\n" +
                "import org.drools.integrationtests.PropertyReactivityTest.Intf1;\n" +
                "import org.drools.integrationtests.PropertyReactivityTest.Intf2;\n" +
                "import org.drools.integrationtests.PropertyReactivityTest.Klass;\n" +
                "import org.drools.integrationtests.PropertyReactivityTest.Klass2;\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                " insert( new Klass( 1, 2, 3, 4, 5, 6 ) );\n" +
                " insert( new Klass2( 2, 3, 4, 5 ) );\n" +
                "end\n" +
                "\n" +
                "rule \"On1\"\n" +
                "when\n" +
                " $x : Intf1( )\n" +
                "then\n" +
                " System.out.println( \"Modify by interface \" );\n" +
                " modify ( $x ) { setD( 200 ) }\n" +
                "end\n" +
                "rule \"On2\"\n" +
                "when\n" +
                " $x : Klass2( )\n" +
                "then\n" +
                " System.out.println( \"Modify by class \" );\n" +
                " modify ( $x ) { setD( 200 ) }\n" +
                "end\n" +
                "\n" +
                "rule \"Log1\"\n" +
                "when\n" +
                " Klass( d == 200, $id : id ) \n" +
                "then\n" +
                " System.out.println( \"Log1 - As K1 \" + $id );\n" +
                " list.add( $id + \"@K1\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Log2\"\n" +
                "when\n" +
                " Klass2( d == 200, $id : id ) \n" +
                "then\n" +
                " System.out.println( \"Log2 - As K2 \" + $id );\n" +
                " list.add( $id + \"@K2\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Log3\"\n" +
                "when\n" +
                " Intf1( d == 200, $id : id ) \n" +
                "then\n" +
                " System.out.println( \"Log3 - As I1 \" + $id );\n" +
                " list.add( $id + \"@I1\" );\n" +
                "end\n" +
                "\n" +
                "rule \"Log4\"\n" +
                "when\n" +
                " Intf2( d == 200, $id : id ) \n" +
                "then\n" +
                " System.out.println( \"Log4 - As K2 \" + $id );\n" +
                " list.add( $id + \"@I2\" );\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.fireAllRules();

        System.out.println( list );

        assertTrue( list.containsAll( Arrays.asList( "k1@K1", "k1@I1", "k1@I2" ) ) );
        assertTrue( list.containsAll( Arrays.asList("k2@K2", "k2@I2") ) );
        assertEquals( 5, list.size() );
    }

    @Test
    public void testScrambleWithInterfacesAndObject() {
        // DROOLS-91
        String str =
                "package org.drools.test;\n" +
                "\n" +
                "import org.drools.integrationtests.PropertyReactivityTest.Intf2;\n" +
                "import org.drools.integrationtests.PropertyReactivityTest.Klass2;\n" +
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
        assertEquals( Arrays.asList( "Klass2", "Klass2" ), list );
    }

    @Test
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

    @Test
    public void testWithBeanAndTraitInDifferentPackages() {
        // DROOLS-91
        String str1 =
                "package org.drools.integrationtests;\n" +
                "declare trait Trait " +
                " @propertyReactive\n" +
                " a : int\n" +
                "end";

        String str2 =
                "package org.drools.test;\n" +
                "import org.drools.integrationtests.Trait;\n" +
                "import org.drools.integrationtests.PropertyReactivityTest.Bean;\n" +
                "rule Init\n" +
                "when\n" +
                "then\n" +
                " insert(new Bean(1, 2));\n" +
                "end\n" +
                "rule R\n" +
                "when\n" +
                " $b : Bean( b == 2)" +
                "then\n" +
                " Trait t = don( $b, Trait.class, true );\n" +
                " modify(t) { setA(2) };\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str1, str2);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        ksession.fireAllRules();
    }


    @Test
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
                        "    rule \"calculate y\"\n" +
                        "    dialect \"mvel\"\n" +
                        "    when\n" +
                        "    $bean : SampleBean(id == 1L);\n" +
                        "    then\n" +
                        "    modify($bean){\n" +
                        "        y =5B;\n" +
                        "    }\n" +
                        "    list.add( $bean.y ); \n" +
                        "    end\n" +
                        "\n" +
                        "    rule \"calculate x\"\n" +
                        "    dialect \"mvel\"\n" +
                        "    when\n" +
                        "    $bean : SampleBean(id == 1L);\n" +
                        "    then\n" +
                        "    modify($bean){\n" +
                        "        x =10B;\n" +
                        "    }\n" +
                        "    list.add( $bean.x ); \n" +
                        "    end\n" +
                        "" +
                        "   rule Init \n" +
                        "   when\n" +
                        "   then\n" +
                        "       insert( new SampleBean( 1L ) ); \n" +
                        "   end";


        KnowledgeBase kbase = loadKnowledgeBaseFromString( str1 );
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertEquals( 2, list.size() );
        assertTrue( list.contains( new BigDecimal( 10 ) ) );
        assertTrue( list.contains( new BigDecimal( 5 ) ) );
    }


    @Test
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


    @Test
    public void testPRWithPositionalUnification() {
        String str1 =
                "package org.test;\n" +
                "global java.util.List list; \n" +
                "" +
                "declare Man \n" +
                "@propertyReactive \n" +
                "  name : String \n" +
                "end \n" +
                "" +
                "declare Animal \n" +
                "@propertyReactive \n" +
                "   id : int \n" +
                "   owner : String \n" +
                "   age : int \n" +
                "end \n" +
                "" +
                "rule Init \n" +
                "when \n" +
                "then \n" +
                "   insert( new Man( \"alan\" ) ); \n" +
                "   insert( new Animal( 1, \"bob\", 7 ) ); \n" +
                "end \n" +
                "" +
                "rule \"Mod Man\" \n" +
                "when \n" +
                "   $m :Man() \n" +
                "then \n" +
                "   modify ( $m ) { setName( \"bob\" ); } \n" +
                "end \n" +
                "" +
                "rule \"Mod Per\" \n" +
                "when \n" +
                "   $m :Animal() \n" +
                "then \n" +
                "   modify ( $m ) { " +
                "       setId( 1 ); \n " +
                "   } \n" +
                "end \n" +
                "" +
                "rule Join_1\n" +
                "when\n" +
                "   Man( $name ; )  \n" +
                "   Animal( $name := owner ) \n" +
                "then\n" +
                "   list.add( 1 ); \n" +
                "end\n" +
                "" +
                "rule Join_3\n" +
                "when\n" +
                "   Man( $name ; ) \n" +
                "   Animal( $id, $name; ) \n" +
                "   Integer( this == $id ) \n" +
                "then\n" +
                "   list.add( 2 ); \n" +
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

}
