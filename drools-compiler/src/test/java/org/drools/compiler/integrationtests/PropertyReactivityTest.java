package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.junit.Test;
import org.kie.api.definition.type.PropertyReactive;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PropertyReactivityTest extends CommonTestMethodBase {

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


    @Test
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
                "import org.drools.compiler.integrationtests.PropertyReactivityTest.Intf1;\n" +
                "import org.drools.compiler.integrationtests.PropertyReactivityTest.Intf2;\n" +
                "import org.drools.compiler.integrationtests.PropertyReactivityTest.Klass;\n" +
                "import org.drools.compiler.integrationtests.PropertyReactivityTest.Klass2;\n" +
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
}
