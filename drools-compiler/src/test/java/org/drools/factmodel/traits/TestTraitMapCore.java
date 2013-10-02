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


    @Test
    public void testDrools216(){

        String drl = "" +
                "\n" +
                "/*\n" +
                "  Map accessors and custom evaluators do not work together\n" +
                "  Map( this[ \"x\" ] custOp value ) fails.\n" +
                "  The custom evaluator is passed the map itself, rather than the value\n" +
                "  corresponding to the key.\n" +
                "*/\n" +
                "\n" +
                "package openehr.test;\n" +
                "\n" +
                "import java.util.*;\n" +
                "import org.drools.factmodel.traits.Alias\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "\n" +
                "declare org.drools.factmodel.MapCore  //what is this for\n" +
                "end\n" +
                "\n" +
                "\n" +
                "declare trait Citizen\n" +
                "@traitable\n" +
                "    citizenship : String = \"Unknown\"\n" +
                "end\n" +
                "\n" +
                "declare trait Student extends Citizen\n" +
                "@propertyReactive\n" +
                "   ID : String = \"412314\" @Alias(\"personID\")\n" +
                "   GPA : Double = 3.99\n" +
                "end\n" +
                "\n" +
                "declare Person\n" +
                "@Traitable\n" +
                "    personID : String\n" +
                "    isStudent : boolean\n" +
                "end\n" +
                "\n" +
                "declare trait Worker\n" +
                "@propertyReactive\n" +
                "    hasBenefits : Boolean = true\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"1\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "then\n" +
                "    Person p = new Person(\"1020\",true);\n" +
                "    Map map = new HashMap();\n" +
                "    map.put(\"isEmpty\",true);\n" +
                "    insert(p);\n" +
                "    insert(map);\n" +
                "    list.add(\"initialized\");\n" +
                "end\n" +
                "\n" +
                "rule \"2\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $stu : Person(isStudent == true)\n" +
                "    $map : Map(this[\"isEmpty\"] == true)\n" +
                "then\n" +
                "    Student s = don( $stu , Student.class );\n" +
                "    $map.put(\"worker\" , s);\n" +
                "    $map.put(\"isEmpty\" , false);\n" +
                "    update($map);\n" +
                "    System.out.println(\"don: Person -> Student \");\n" +
                "    list.add(\"student is donned\");\n" +
                "end\n" +
                "\n" +
                "rule \"3\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $map : Map($stu : this[\"worker\"] isA Student.class)\n" +
                "then\n" +
                "    Object obj = don( $map , Worker.class );\n" +
                "    System.out.println(\"don: Map -> Worker : \"+obj);\n" +
                "    list.add(\"worker is donned\");\n" +
                "end\n";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();

        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertTrue(list.contains("initialized"));
        assertTrue(list.contains("student is donned"));
        assertTrue(list.contains("worker is donned"));

    }

    @Test
    public void testDrools217(){

        String drl = "" +
                "\n" +
                "/*\n" +
                "MVEL throws an exception when using bound variables and map accessors\n" +
                "Map( $c : this[ \"x\" ], $c op value )\n" +
                "throws an exception as MVEL tries to compile the constraint\n" +
                "*/\n" +
                "\n" +
                "package openehr.test;\n" +
                "\n" +
                "import java.util.*;\n" +
                "import org.drools.factmodel.traits.Alias\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "\n" +
                "declare org.drools.factmodel.MapCore  //what is this for\n" +
                "end\n" +
                "\n" +
                "\n" +
                "declare trait Citizen\n" +
                "@traitable\n" +
                "    citizenship : String = \"Unknown\"\n" +
                "end\n" +
                "\n" +
                "declare trait Student extends Citizen\n" +
                "@propertyReactive\n" +
                "   ID : String = \"412314\" @Alias(\"personID\")\n" +
                "   GPA : Double = 3.99\n" +
                "end\n" +
                "\n" +
                "declare Person\n" +
                "@Traitable\n" +
                "    personID : String\n" +
                "    isStudent : boolean\n" +
                "end\n" +
                "\n" +
                "declare trait Worker\n" +
                "@propertyReactive\n" +
                "    hasBenefits : Boolean = true\n" +
                "end\n" +
                "\n" +
                "\n" +
                "rule \"1\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "then\n" +
                "    Person p = new Person(\"1020\",true);\n" +
                "    Map map = new HashMap();\n" +
                "    map.put(\"isEmpty\",true);\n" +
                "    insert(p);\n" +
                "    insert(map);\n" +
                "    list.add(\"initialized\");\n" +
                "end\n" +
                "\n" +
                "rule \"2\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $stu : Person(isStudent == true)\n" +
                "    $map : Map(this[\"isEmpty\"] == true)\n" +
                "then\n" +
                "    Student s = don( $stu , Student.class );\n" +
                "    $map.put(\"worker\" , s);\n" +
                "    $map.put(\"isEmpty\" , false);\n" +
                "    update($map);\n" +
                "    System.out.println(\"don: Person -> Student \");\n" +
                "    list.add(\"student is donned\");\n" +
                "end\n" +
                "\n" +
                "rule \"3\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $map : Map($stu : this[\"worker\"], $stu isA Student.class)\n" +
                "then\n" +
                "    Object obj = don( $map , Worker.class );\n" +
                "    System.out.println(\"don: Map -> Worker : \"+obj);\n" +
                "    list.add(\"worker is donned\");\n" +
                "end\n";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();

        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        List list = new ArrayList();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();

        assertTrue(list.contains("initialized"));
        assertTrue(list.contains("student is donned"));
        assertTrue(list.contains("worker is donned"));
    }

    @Test
    public void testDrools218(){

        String drl = "" +
                "\n" +
                "/*\n" +
                "trait field @aliasing does not work when Maps are traited\n" +
                "*/\n" +
                "package openehr.test;\n" +
                "\n" +
                "import java.util.*;\n" +
                "import org.drools.factmodel.traits.Alias\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare org.drools.factmodel.MapCore  //what is this for\n" +
                "end\n" +
                "\n" +
                "\n" +
                "declare trait Citizen\n" +
                "@traitable\n" +
                "    citizenship : String = \"Unknown\"\n" +
                "end\n" +
                "\n" +
                "declare trait Student extends Citizen\n" +
                "@propertyReactive\n" +
                "   ID : String = \"412314\" @Alias(\"personID\")\n" +
                "   GPA : Double = 3.99\n" +
                "end\n" +
                "\n" +
                "declare Person\n" +
                "@Traitable\n" +
                "    personID : String\n" +
                "    isStudent : boolean\n" +
                "end\n" +
                "\n" +
                "declare trait Worker\n" +
                "@propertyReactive\n" +
                "    //customer : Citizen\n" +
                "    hasBenefits : Boolean = true\n" +
                "end\n" +
                "\n" +
                "declare trait StudentWorker extends Worker\n" +
                "@propertyReactive\n" +
                "    //currentStudent : Citizen @Alias(\"customer\")\n" +
                "    tuitionWaiver : Boolean @Alias(\"hasBenefits\")\n" +
                "end\n" +
                "\n" +
                "rule \"1\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "then\n" +
                "    Person p = new Person(\"1020\",true);\n" +
                "    Map map = new HashMap();\n" +
                "    map.put(\"isEmpty\",true);\n" +
                "    insert(p);\n" +
                "    insert(map);\n" +
                "    list.add(\"initialized\");\n" +
                "end\n" +
                "\n" +
                "rule \"2\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $stu : Person(isStudent == true)\n" +
                "    $map : Map(this[\"isEmpty\"] == true)\n" +
                "then\n" +
                "    Student s = don( $stu , Student.class );\n" +
                "    $map.put(\"worker\" , s);\n" +
                "    $map.put(\"isEmpty\" , false);\n" +
                "    $map.put(\"hasBenefits\",null);\n" +
                "    update($map);\n" +
                "    System.out.println(\"don: Person -> Student \");\n" +
                "    list.add(\"student is donned\");\n" +
                "end\n" +
                "\n" +
                "rule \"3\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $map : Map($stu : this[\"worker\"])\n" +
                "    Map($stu isA Student.class, this == $map)\n" +
                "then\n" +
                "    Object obj = don( $map , Worker.class );\n" +
                "    System.out.println(\"don: Map -> Worker : \"+obj);\n" +
                "    list.add(\"worker is donned\");\n" +
                "end\n" +
                "\n" +
                "rule \"4\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $stu : Student()\n" +
                "then\n" +
                "    Object obj = don( $stu , StudentWorker.class );\n" +
                "    System.out.println(\"don: Map -> StudentWorker : \"+obj);\n" +
                "    list.add(\"studentworker is donned\");\n" +
                "end\n" +
                "\n" +
                "rule \"5\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    StudentWorker(tuitionWaiver == true)\n" +
                "then\n" +
                "    System.out.println(\"tuitionWaiver == true\");\n" +
                "    list.add(\"tuitionWaiver is true\");\n" +
                "end\n" +
                "\n";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();

        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertTrue(list.contains("initialized"));
        assertTrue(list.contains("student is donned"));
        assertTrue(list.contains("worker is donned"));
        assertTrue(list.contains("studentworker is donned"));
        assertTrue(list.contains("tuitionWaiver is true"));
    }

    @Test
    public void testDrools219(){

        String drl = "" +
                "\n" +
                "/*\n" +
                "@aliasing soft fields may fail silently or throw exceptions\n" +
                "the behavior of @alias is undefined for soft fields and may be erratic at best.\n" +
                "*/\n" +
                "\n" +
                "package openehr.test;\n" +
                "\n" +
                "import java.util.*;\n" +
                "import org.drools.factmodel.traits.Alias\n" +
                "\n" +
                "global java.util.List list;\n" +
                "\n" +
                "declare org.drools.factmodel.MapCore  //what is this for\n" +
                "end\n" +
                "\n" +
                "\n" +
                "declare trait Citizen\n" +
                "    citizenship : String = \"Unknown\"\n" +
                "    socialSecurity : String = \"0\"\n" +
                "end\n" +
                "\n" +
                "declare trait Student extends Citizen\n" +
                "@propertyReactive\n" +
                "   ID : String = \"412314\" @Alias(\"personID\") //notice: by removing this Alias rule \"4\" would be ok\n" +
                "   GPA : Double = 3.99\n" +
                "   SSN : String = \"888111155555\" @Alias(\"socialSecurity\")\n" +
                "end\n" +
                "\n" +
                "declare Person\n" +
                "@Traitable(logical=true)\n" +
                "    personID : String\n" +
                "    isStudent : boolean\n" +
                "end\n" +
                "\n" +
                "rule \"1\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "then\n" +
                "    Person p = new Person(\"1020\",true);\n" +
                "    insert(p);\n" +
                "    list.add(\"initialized\");\n" +
                "end\n" +
                "\n" +
                "rule \"2\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $stu : Person(isStudent == true)\n" +
                "then\n" +
                "    Student s = don( $stu , Student.class );\n" +
                "    System.out.println(\"don: Person -> Student \" + s);\n" +
                "    list.add(\"student is donned\");\n" +
                "end\n" +
                "\n" +
                "rule \"3\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $stu : Student(ID == \"412314\", SSN == \"888111155555\")\n" +
                "then\n" +
                "    list.add(\"student has ID and SSN\");\n" +
                "end\n" +
                "\n" +
                "rule \"4\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    Student(fields[\"personID\"] == \"412314\", fields[\"socialSecurity\"] == \"888111155555\")\n" +
                "    //$stu : Student(personID == \"412314\", socialSecurity == \"888111155555\")//notice: compile error\n" +
                "then\n" +
                "    list.add(\"student has personID and socialSecurity\");\n" +
                "end\n" +
                "\n" +
                "rule \"5\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $ctz : Citizen(socialSecurity == \"888111155555\")\n" +
                "then\n" +
                "    list.add(\"citizen has socialSecurity\");\n" +
                "end\n" +
                "\n" +
                "rule \"6\"\n" +
                "salience 1\n" +
                "no-loop\n" +
                "when\n" +
                "    $p : Person(personID == \"412314\")\n" +
                "then\n" +
                "    list.add(\"person has personID\");\n" +
                "end\n";

        StatefulKnowledgeSession ksession = loadKnowledgeBaseFromString(drl).newStatefulKnowledgeSession();

        TraitFactory.setMode(TraitFactory.VirtualPropertyMode.MAP, ksession.getKnowledgeBase());
        List list = new ArrayList();
        ksession.setGlobal( "list", list );
        ksession.fireAllRules();

        assertTrue(list.contains("initialized"));
        assertTrue(list.contains("student is donned"));
        assertTrue(list.contains("student has ID and SSN"));
        assertTrue(list.contains("student has personID and socialSecurity"));
        assertTrue(list.contains("citizen has socialSecurity"));
        assertTrue(list.contains("person has personID"));
    }



}
