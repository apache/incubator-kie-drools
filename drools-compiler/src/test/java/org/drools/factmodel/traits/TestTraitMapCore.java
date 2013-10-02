package org.drools.factmodel.traits;

import org.drools.CommonTestMethodBase;
import org.drools.runtime.StatefulKnowledgeSession;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestTraitMapCore extends CommonTestMethodBase {



    @Test
    public void testMapCoreManyTraits(  ) {
        String source = "package org.drools.test;\n" +
                        "\n" +
                        "import java.util.*;\n" +
                        "import org.drools.factmodel.traits.Traitable;\n" +
                        "" +
                        "global List list;\n " +
                        "\n" +
                        "declare HashMap @Traitable end \n" +
                        "" +
                        "declare org.drools.factmodel.MapCore \n" +
                        "end\n" +
                        "\n" +
                        "global List list; \n" +
                        "\n" +
                        "declare trait PersonMap\n" +
                        "@propertyReactive  \n" +
                        "   name : String  \n" +
                        "   age  : int  \n" +
                        "   height : Double  \n" +
                        "end\n" +
                        "\n" +
                        "declare trait StudentMap\n" +
                        "@propertyReactive\n" +
                        "   ID : String\n" +
                        "   GPA : Double = 3.0\n" +
                        "end\n" +
                        "\n" +
                        "rule Don  \n" +
                        "no-loop \n" +
                        "when  \n" +
                        "  $m : Map( this[ \"age\"] == 18 )\n" +
                        "then  \n" +
                        "   Object obj1 = don( $m, PersonMap.class );\n" +
                        "   Object obj2 = don( obj1, StudentMap.class );\n" +
                        "   System.out.println( \"done: PersonMap\" );\n" +
                        "\n" +
                        "end\n" +
                        "\n";

        StatefulKnowledgeSession ks = loadKnowledgeBaseFromString( source ).newStatefulKnowledgeSession();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        Map<String,Object> map = new HashMap<String, Object>(  );
        map.put( "name", "john" );
        map.put( "age", 18 );
        ks.insert( map );

        ks.fireAllRules();

        for ( Object o : ks.getObjects() ) {
            System.err.println( o );
        }

        assertEquals( 3.0, map.get( "GPA" ) );
    }












    @Test
    public void donMapTest() {
        String source = "package org.drools.traits.test; \n" +
                        "import java.util.*\n;" +
                        "import org.drools.factmodel.traits.Traitable;\n" +
                        "" +
                        "declare org.drools.factmodel.MapCore end \n" +
                        "" +
                        "global List list; \n" +
                        "" +
                        "declare HashMap @Traitable end \n" +
                        "" +
                        "declare trait PersonMap" +
                        "@propertyReactive \n" +
                        "   name : String \n" +
                        "   age  : int \n" +
                        "   height : Double \n" +
                        "end\n" +
                        "" +
                        "" +
                        "rule Don \n" +
                        "when \n" +
                        "  $m : Map( this[ \"age\"] == 18 ) " +
                        "then \n" +
                        "   don( $m, PersonMap.class );\n" +
                        "end \n" +
                        "" +
                        "rule Log \n" +
                        "when \n" +
                        "   $p : PersonMap( name == \"john\", age > 10 ) \n" +
                        "then \n" +
                        "   System.out.println( $p ); \n" +
                        "   modify ( $p ) { \n" +
                        "       setHeight( 184.0 ); \n" +
                        "   }" +
                        "   System.out.println( $p ); " +
                        "end \n";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString( source ).newStatefulKnowledgeSession();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase() );

        List list = new ArrayList();
        ksession.setGlobal( "list", list );

        Map map = new HashMap();
        map.put( "name", "john" );
        map.put( "age", 18 );

        ksession.insert( map );
        ksession.fireAllRules();

        assertTrue( map.containsKey( "height" ) );
        assertEquals( map.get( "height"), 184.0 );

    }



    @Test
    public void testMapCore2(  ) {
        String source = "package org.drools.factmodel.traits.test;\n" +
                        "\n" +
                        "import java.util.*;\n" +
                        "import org.drools.factmodel.traits.Traitable;\n" +
                        "" +
                        "global List list;\n " +
                        "" +
                        "declare HashMap @Traitable end \n" +
                        "\n" +
                        "declare org.drools.factmodel.MapCore \n" +
                        "end\n" +
                        "\n" +
                        "global List list; \n" +
                        "\n" +
                        "declare trait PersonMap\n" +
                        "@propertyReactive  \n" +
                        "   name : String  \n" +
                        "   age  : int  \n" +
                        "   height : Double  \n" +
                        "end\n" +
                        "\n" +
                        "declare trait StudentMap\n" +
                        "@propertyReactive\n" +
                        "   ID : String\n" +
                        "   GPA : Double = 3.0\n" +
                        "end\n" +
                        "\n" +
                        "rule Don  \n" +
                        "when  \n" +
                        "  $m : Map( this[ \"age\"] == 18, this[ \"ID\" ] != \"100\" )\n" +
                        "then  \n" +
                        "   don( $m, PersonMap.class );\n" +
                        "   System.out.println( \"done: PersonMap\" );\n" +
                        "\n" +
                        "end\n" +
                        "\n" +
                        "rule Log  \n" +
                        "when  \n" +
                        "   $p : PersonMap( name == \"john\", age > 10 )\n" +
                        "then  \n" +
                        "   modify ( $p ) {  \n" +
                        "       setHeight( 184.0 );  \n" +
                        "   }\n" +
                        "   System.out.println(\"Log: \" +  $p );\n" +
                        "end\n" +
                        "" +
                        "" +
                        "rule Don2\n" +
                        "salience -1\n" +
                        "when\n" +
                        "   $m : Map( this[ \"age\"] == 18, this[ \"ID\" ] != \"100\" ) " +
                        "then\n" +
                        "   don( $m, StudentMap.class );\n" +
                        "   System.out.println( \"done2: StudentMap\" );\n" +
                        "end\n" +
                        "" +
                        "" +
                        "rule Log2\n" +
                        "salience -2\n" +
                        "no-loop\n" +
                        "when\n" +
                        "   $p : StudentMap( $h : fields[ \"height\" ], GPA >= 3.0 ) " +
                        "then\n" +
                        "   modify ( $p ) {\n" +
                        "       setGPA( 4.0 ),\n" +
                        "       setID( \"100\" );\n" +
                        "   }\n" +
                        "   System.out.println(\"Log2: \" + $p );\n" +
                        "end\n" +
                        "" +
                        "" +
                        "\n" +
                        "rule Shed1\n" +
                        "salience -5// it seams that the order of shed must be the same as applying don\n" +
                        "when\n" +
                        "    $m : PersonMap()\n" +
                        "then\n" +
                        "   shed( $m, PersonMap.class );\n" +
                        "   System.out.println( \"shed: PersonMap\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule Shed2\n" +
                        "salience -9\n" +
                        "when\n" +
                        "    $m : StudentMap()\n" +
                        "then\n" +
                        "   shed( $m, StudentMap.class );\n" +
                        "   System.out.println( \"shed: StudentMap\" );\n" +
                        "end\n" +
                        "" +
                        "rule Last  \n" +
                        "salience -99 \n" +
                        "when  \n" +
                        "  $m : Map( this not isA StudentMap.class )\n" +
                        "then  \n" +
                        "   System.out.println( \"Final\" );\n" +
                        "   $m.put( \"final\", true );" +
                        "\n" +
                        "end\n" +
                        "\n" +
                        "\n";

        StatefulKnowledgeSession ks = loadKnowledgeBaseFromString( source ).newStatefulKnowledgeSession();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        Map<String,Object> map = new HashMap<String, Object>(  );
        map.put( "name", "john" );
        map.put( "age", 18 );
        ks.insert( map );

        ks.fireAllRules();


        for ( Object o : ks.getObjects() ) {
            System.err.println( o );
        }

        assertEquals( "100", map.get( "ID" ) );
        assertEquals( 184.0, map.get( "height" ) );
        assertEquals( 4.0, map.get( "GPA" ) );
        assertEquals( true, map.get( "final" ) );

    }




    @Test
    public void testMapCoreAliasing(  ) {
        String source = "package org.drools.factmodel.traits.test;\n" +
                        "\n" +
                        "import java.util.*;\n" +
                        "import org.drools.factmodel.traits.*;\n" +
                        "" +
                        "global List list;\n " +
                        "" +
                        "declare HashMap @Traitable(logical=true) end \n" +
                        "\n" +
                        "declare org.drools.factmodel.MapCore \n" +
                        "end\n" +
                        "\n" +
                        "global List list; \n" +
                        "\n" +
                        "declare trait PersonMap\n" +
                        "@propertyReactive  \n" +
                        "   name : String  \n" +
                        "   age  : Integer  @Alias( \"years\" ) \n" +
                        "   eta  : Integer  @Alias( \"years\" ) \n" +
                        "   height : Double  @Alias( \"tall\" ) \n" +
                        "   sen : String @Alias(\"years\") \n " +
                        "end\n" +
                        "\n" +
                        "rule Don  \n" +
                        "when  \n" +
                        "  $m : Map()\n" +
                        "then  \n" +
                        "   don( $m, PersonMap.class );\n" +
                        "\n" +
                        "end\n" +
                        "\n" +
                        "rule Log  \n" +
                        "when  \n" +
                        "   $p : PersonMap( name == \"john\", age > 10 && < 35 )\n" +
                        "then  \n" +
                        "   modify ( $p ) {  \n" +
                        "       setHeight( 184.0 ), \n" +
                        "       setEta( 42 );  \n" +
                        "   }\n" +
                        "   System.out.println(\"Log: \" +  $p );\n" +
                        "end\n" +
                        "" +
                        "\n";

        StatefulKnowledgeSession ks = loadKnowledgeBaseFromString( source ).newStatefulKnowledgeSession();
        TraitFactory.setMode( TraitFactory.VirtualPropertyMode.MAP, ks.getKnowledgeBase() );

        List list = new ArrayList();
        ks.setGlobal( "list", list );

        Map<String,Object> map = new HashMap<String, Object>(  );
        map.put( "name", "john" );
        map.put( "years", new Integer( 18 ) );
        ks.insert( map );

        ks.fireAllRules();


        for ( Object o : ks.getObjects() ) {
            System.err.println( o );
        }

        assertEquals( 42, map.get( "years" ) );
        assertEquals( 184.0, map.get( "tall" ) );

    }




}
