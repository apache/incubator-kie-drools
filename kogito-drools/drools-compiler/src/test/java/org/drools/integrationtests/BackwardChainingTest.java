package org.drools.integrationtests;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.drools.Address;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.rule.Variable;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.junit.Test;

import static org.drools.integrationtests.SerializationHelper.getSerialisedStatefulKnowledgeSession;
import static org.drools.rule.Variable.variable;


public class BackwardChainingTest {
     
    @Test
    public void testQueryPositional() throws Exception {
        String str = "" +
            "package org.drools.test  \n" +
            "import org.drools.Person \n" +
            "global java.util.List list\n" +
            "query peeps( String $name, String $likes, int $age ) \n" +
            "    Person( $name : name, $likes : likes, $age : age; ) \n"+
            "end\n";

        str += "rule x1\n" + 
            "when\n" + 
            "    String( this == \"go1\" )\n" +
            //         output, output ,output
            "    ?peeps($name1, $likes1, $age1; )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";        
        
        str += "rule x2\n" + 
            "when\n" + 
            "    String( this == \"go2\" )\n" +
            //         output, input      ,output
            "    ?peeps($name1, \"stilton\", $age1; )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";

        str += "rule x3\n" + 
            "when\n" + 
            "    String( this == \"go3\" )\n" +
            "    $name1 : String() from \"darth\";\n "+
            //         input , input      ,output        
            "    ?peeps($name1, \"stilton\", $age1; )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";        
        
        str += "rule x4\n" + 
            "when\n" + 
            "    String( this == \"go4\" )\n" +
            "    $name1 : String() from \"darth\"\n "+
            "    $age1 : Integer() from 200;\n "+        
            //         input , input      ,input        
            "    ?peeps($name1, \"stilton\", $age1; )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";         

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        
        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        Person p2 = new Person( "darth",
                                "stilton",
                                200 );        
        Person p3 = new Person( "yoda",
                                "stilton",
                                300 );
        Person p4 = new Person( "luke",
                                "brie",
                                300 );
        Person p5 = new Person( "bobba",
                                "cheddar",
                                300 );

        ksession.insert( p1 );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.insert( p2 );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.insert( p3 );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.insert( p4 );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.insert( p5 );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );   
        
        ksession.insert( "go1" );
        
        // Make sure we can serialise query state
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );        
        
        ksession.fireAllRules();
        assertEquals( 5, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        assertTrue( list.contains( "yoda : 300" ));
        assertTrue( list.contains( "luke : 300" ));
        assertTrue( list.contains( "bobba : 300" ));
        
        list.clear();        
        ksession.insert( "go2" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();
        assertEquals( 3, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        assertTrue( list.contains( "yoda : 300" ));
        
        list.clear();
        ksession.insert( "go3" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();   
        assertEquals( 2, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        
        list.clear();        
        ksession.insert( "go4" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();           
        assertEquals( 1, list.size());
        assertTrue( list.contains( "darth : 200" ));        
    }      
    
    @Test
    public void testQueryNamed() throws Exception {
        String str = "" +
            "package org.drools.test  \n" +
            "import org.drools.Person \n" +
            "global java.util.List list\n" +
            "query peeps( String $name, String $likes, int $age ) \n" +
            "    Person( $name : name, $likes : likes, $age : age ) \n"+
            "end\n";

        str += "rule x1\n" + 
            "when\n" + 
            "    String( this == \"go1\" )\n" +
            //         output        ,output          ,output
            "    ?peeps($name1 : $name, $likes1 : $likes, $age1 : $age )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";        
        
        str += "rule x2\n" + 
            "when\n" + 
            "    String( this == \"go2\" )\n" +
            //         output        ,output                ,output
            "    ?peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";

        str += "rule x3\n" + 
            "when\n" + 
            "    String( this == \"go3\" )\n" +
            "    $name1 : String() from \"darth\";\n "+            
            //         input         ,input                ,output
            "    ?peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n" +              
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";        
        
        str += "rule x4\n" + 
            "when\n" + 
            "    String( this == \"go4\" )\n" +
            "    $name1 : String() from \"darth\";\n "+
            "    $age1 : Integer() from 200;\n "+            
            //         input         ,input                ,input
            "    ?peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n" +
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";         

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        
        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        Person p2 = new Person( "darth",
                                "stilton",
                                200 );        
        Person p3 = new Person( "yoda",
                                "stilton",
                                300 );
        Person p4 = new Person( "luke",
                                "brie",
                                300 );
        Person p5 = new Person( "bobba",
                                "cheddar",
                                300 );

        ksession.insert( p1 );
        ksession.insert( p2 );
        ksession.insert( p3 );
        ksession.insert( p4 );
        ksession.insert( p5 );
        
        ksession.insert( "go1" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();
        assertEquals( 5, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        assertTrue( list.contains( "yoda : 300" ));
        assertTrue( list.contains( "luke : 300" ));
        assertTrue( list.contains( "bobba : 300" ));
        
        list.clear();        
        ksession.insert( "go2" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();
        assertEquals( 3, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        assertTrue( list.contains( "yoda : 300" ));
        
        list.clear();
        ksession.insert( "go3" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();   
        assertEquals( 2, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        
        list.clear();        
        ksession.insert( "go4" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();           
        assertEquals( 1, list.size());
        assertTrue( list.contains( "darth : 200" ));        
    }
    
    @Test
    public void testQueryMixed() throws Exception {
        String str = "" +
            "package org.drools.test  \n" +
            "import org.drools.Person \n" +
            "global java.util.List list\n" +
            "query peeps( String $name, String $likes, int $age ) \n" +
            "    Person( $name : name, $likes : likes, $age : age; ) \n"+
            "end\n";

        str += "rule x1\n" + 
            "when\n" + 
            "    String( this == \"go1\" )\n" +
            //         output        ,output          ,output
            "    ?peeps($name1; $likes1 : $likes, $age1 : $age )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";        
        
        str += "rule x2\n" + 
            "when\n" + 
            "    String( this == \"go2\" )\n" +
            //         output        ,output                ,output
            "    ?peeps($name1, \"stilton\"; $age1 : $age )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";

        str += "rule x3\n" + 
            "when\n" + 
            "    String( this == \"go3\" )\n" +
            "    $name1 : String() from \"darth\";\n "+            
            //         input         ,input                ,output
            "    ?peeps($name1, \"stilton\"; $age1 : $age )\n" +              
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";        
        
        str += "rule x4\n" + 
            "when\n" + 
            "    String( this == \"go4\" )\n" +
            "    $name1 : String() from \"darth\"\n "+
            "    $age1 : Integer() from 200;\n "+            
            //         input         ,input                ,input
            "    ?peeps($name1; $likes : \"stilton\", $age1 : $age )\n" +
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";         

        System.out.println( str );
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        
        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        Person p2 = new Person( "darth",
                                "stilton",
                                200 );        
        Person p3 = new Person( "yoda",
                                "stilton",
                                300 );
        Person p4 = new Person( "luke",
                                "brie",
                                300 );
        Person p5 = new Person( "bobba",
                                "cheddar",
                                300 );

        ksession.insert( p1 );
        ksession.insert( p2 );
        ksession.insert( p3 );
        ksession.insert( p4 );
        ksession.insert( p5 );
        
        ksession.insert( "go1" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();
        assertEquals( 5, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        assertTrue( list.contains( "yoda : 300" ));
        assertTrue( list.contains( "luke : 300" ));
        assertTrue( list.contains( "bobba : 300" ));
        
        list.clear();        
        ksession.insert( "go2" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();
        assertEquals( 3, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        assertTrue( list.contains( "yoda : 300" ));
        
        list.clear();
        ksession.insert( "go3" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();   
        assertEquals( 2, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        
        list.clear();        
        ksession.insert( "go4" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();           
        assertEquals( 1, list.size());
        assertTrue( list.contains( "darth : 200" ));        
    }     
    
    @Test
    public void testQueryPatternBindingAsResult() throws Exception {
        String str = "" +
            "package org.drools.test  \n" +
            "import org.drools.Person \n" +
            "global java.util.List list\n" +
            "query peeps( Person $p, String $name, String $likes, int $age ) \n" +
            "    $p : Person( $name : name, $likes : likes, $age : age; ) \n"+
            "end\n";

        str += "rule x1\n" + 
            "when\n" + 
            "    String( this == \"go1\" )\n" +
            //     output, output, output          ,output
            "    ?peeps($p, $name1; $likes1 : $likes, $age1 : $age )\n" + 
            "then\n" +
            "   list.add( $p );\n" +             
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";               

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        
        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        Person p2 = new Person( "darth",
                                "stilton",
                                200 );        
        Person p3 = new Person( "yoda",
                                "stilton",
                                300 );
        Person p4 = new Person( "luke",
                                "brie",
                                300 );
        Person p5 = new Person( "bobba",
                                "cheddar",
                                300 );

        ksession.insert( p1 );
        ksession.insert( p2 );
        ksession.insert( p3 );
        ksession.insert( p4 );
        ksession.insert( p5 );
        
        ksession.insert( "go1" );
//        ksession = getSerialisedStatefulKnowledgeSession( ksession,
//                                                          true );           
        ksession.fireAllRules();
        assertEquals( 10, list.size());
        assertEquals( p1, list.get( list.indexOf( "darth : 100" ) - 1) );
        assertTrue( list.contains( "darth : 100" ));
        assertEquals( p2, list.get( list.indexOf( "darth : 200" ) - 1) );
        assertTrue( list.contains( "darth : 200" ));
        assertEquals( p3, list.get( list.indexOf( "yoda : 300" ) - 1) );
        assertTrue( list.contains( "yoda : 300" ));
        assertEquals( p4, list.get( list.indexOf( "luke : 300" ) - 1) );        
        assertTrue( list.contains( "luke : 300" ));
        assertEquals( p5, list.get( list.indexOf( "bobba : 300" ) - 1) );
        assertTrue( list.contains( "bobba : 300" ));
 
    }         
    
    @Test
    public void testQueriesWithNestedAcecssorsAllOutputs() throws Exception {
        String str = "" +
            "package org.drools.test  \n" +
            "import org.drools.Person \n" +
            "global java.util.List list\n" +
            "query peeps( String $name, String $likes, String $street ) \n" +
            "   Person( $name : name, $likes : likes, $street : address.street ) \n"+
            "end\n";

        str += "rule x1\n" + 
            "when\n" + 
            "    String( this == \"go1\" )\n" +
            //         output, output,         ,output
            "    ?peeps($name1; $likes1 : $likes, $street1 : $street )\n" + 
            "then\n" +           
            "   list.add( $name1 + \" : \" + $likes1 + \" : \" +  $street1 );\n" + 
            "end \n";            
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        
        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        p1.setAddress( new Address("s1") );
        
        Person p2 = new Person( "yoda",
                                "stilton",
                                300 );
        p2.setAddress( new Address("s2") );

        ksession.insert( p1 );
        ksession.insert( p2 );

        ksession.insert( "go1" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();
        assertEquals( 2, list.size());
        assertTrue( list.contains( "darth : stilton : s1" ));
        assertTrue( list.contains( "yoda : stilton : s2" ));
    }    
    
    @Test
    public void testQueriesWithNestedAcecssorsMixedArgs() throws Exception {
        String str = "" +
            "package org.drools.test  \n" +
            "import org.drools.Person \n" +
            "global java.util.List list\n" +
            "query peeps( String $name, String $likes, String $street ) \n" +
            "   Person( $name : name, $likes : likes, $street : address.street ) \n"+
            "end\n";

        str += "rule x1\n" + 
            "when\n" + 
            "    $s : String()\n" +
            //         output, output,         ,input
            "    ?peeps($name1; $likes1 : $likes, $street : $s )\n" + 
            "then\n" +           
            "   list.add( $name1 + \" : \" + $likes1 + \" : \" +  $s );\n" + 
            "end \n";            
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        
        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        p1.setAddress( new Address("s1") );
        
        Person p2 = new Person( "yoda",
                                "stilton",
                                300 );
        p2.setAddress( new Address("s2") );

        ksession.insert( p1 );
        ksession.insert( p2 );

        ksession.insert( "s1" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();
        assertEquals( 1, list.size());
        assertTrue( list.contains( "darth : stilton : s1" ));
        
        list.clear();
        ksession.insert( "s2" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();
        assertEquals( 1, list.size());
        assertTrue( list.contains( "yoda : stilton : s2" ));        
    }        
    
    @Test
    public void testQueryWithDynamicData() throws Exception {
        String str = "" +
            "package org.drools.test  \n" +
            "import org.drools.Person \n" +
            "global java.util.List list\n" +
            "query peeps( Person $p, String $name, String $likes, int $age ) \n" +
            "    $p : Person( ) from new Person( $name, $likes, $age ) \n"+
            "end\n";

        str += "rule x1\n" + 
            "when\n" + 
            "    $n1 : String( )\n" +
            //     output, input     ,input                 ,input
            "    ?peeps($p; $name : $n1, $likes : \"stilton\", $age : 100 )\n" + 
            "then\n" + 
            "   list.add( $p );\n" + 
            "end \n";        
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        
        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        
        Person p2 = new Person( "yoda",
                                "stilton",
                                100 );        
        
        ksession.insert( "darth" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();
        assertEquals( 1, list.size());
        assertEquals( p1, list.get(0));     
        
        list.clear();
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.insert( "yoda" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();
        assertEquals( 1, list.size());
        assertEquals( p2, list.get(0));          
    }         
    
    @Test
    public void testQueryWithDyanmicInsert() throws Exception {
        String str = "" +
            "package org.drools.test  \n" +
            "import org.drools.Person \n" +
            "global java.util.List list\n" +
            "query peeps( Person $p, String $name, String $likes, int $age ) \n" +
            "    $p : Person( ) from new Person( $name, $likes, $age ) \n"+
            "end\n";

        str += "rule x1\n" + 
            "when\n" + 
            "    $n1 : String( )\n" +
            "    not Person( name == 'darth' )\n "+
            //     output, input     ,input                 ,input
            "    ?peeps($p; $name : $n1, $likes : \"stilton\", $age : 100 )\n" + 
            "then\n" + 
            "   insert( $p );\n" + 
            "end \n"; 
        
        str += "rule x2\n" + 
            "when\n" + 
            "    $p : Person( )\n" + 
            "then\n" + 
            "   list.add( $p );\n" + 
            "end \n";        
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        
        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        
        Person p2 = new Person( "yoda",
                                "stilton",
                                100 );        
        
        ksession.insert( "darth" );
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();        
        ksession.insert( "yoda" ); // darth exists, so yoda won't get created 
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
        ksession.fireAllRules();
        assertEquals( 1, list.size());
        assertEquals( p1, list.get(0));          
    }  
    
    @Test
    public void testQueryWithOr() throws Exception {
        String str = "" +
            "package org.drools.test  \n" +
            
            "import java.util.List\n" +
            "import java.util.ArrayList\n" +
            
            "global List list\n" +
            
            "dialect \"mvel\"\n" +            
            "\n" + 
            
            "import org.drools.integrationtests.BackwardChainingTest.Q\n" +
            "import org.drools.integrationtests.BackwardChainingTest.R\n" +
            "import org.drools.integrationtests.BackwardChainingTest.S\n" +                      
            
//            "declare Q\n" + 
//            "    value : int\n" + 
//            "end \n" + 
//            "\n" + 
//            "declare R\n" + 
//            "    value : int\n" + 
//            "end \n" + 
//            "\n" + 
//            "declare S\n" + 
//            "    value : int\n" + 
//            "end \n" + 
            "\n" + 
            "query q(int x)\n" + 
            "    Q( x : value; )\n" + 
            "end\n" + 
            "\n" + 
            "query r(int x)\n" + 
            "    R( x : value; )\n" + 
            "end\n" + 
            "\n" + 
            "query s(int x)\n" + 
            "    S( x : value; )    \n" + 
            "end\n" + 
            "\n" + 
            
            "query p(int x)\n" + 
            "    (?q(x;) and ?r(x;) ) \n" + 
            "    or\n" + 
            "    ?s(x;)\n" + 
            "end\n" + 
            
            "rule init when\n" +
            "then\n" +
            " insert( new Q(1) );\n " +
            " insert( new R(1) );\n " +
            " insert( new R(2) );\n " +
            " insert( new S(2) );\n " +
            " insert( new S(3) );\n " +
            "end\n" +  
            
//            "rule show when\n" +
//            "    o : Object()\n" +
//            "then\n" +
//            " System.out.println( o );\n " +
//            "end\n" +             
            "";        
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );
        
        ksession.fireAllRules();

        QueryResults results = null;
        results = ksession.getQueryResults( "p", new Integer[] { 1 }  );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "result( " + result.get( "x" ) + " )" );
        } 
        
        System.out.println( );
        
        results = ksession.getQueryResults( "p", new Integer[] { 2 }  );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "result( " + result.get( "x" ) + " )" );
        } 
        
        System.out.println( );
        
        results = ksession.getQueryResults( "p", new Integer[] { 3 }  );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "result( " + result.get( "x" ) + " )" );
        }                 
    }      
    
    @Test
    public void testGeneology() throws Exception {
        // from http://kti.mff.cuni.cz/~bartak/prolog/genealogy.html
            
        String str = "" +
            "package org.drools.test  \n" +
            "global java.util.List list\n" +
            "dialect \"mvel\"\n" +                   
                                    
            "query man( String name ) \n" +
            "   org.drools.integrationtests.BackwardChainingTest.Man( name : name ) \n"+
            "end\n" +
        
            "query woman( String name ) \n" +
            "   org.drools.integrationtests.BackwardChainingTest.Woman( name : name ) \n"+
            "end\n" +    
            
            "query parent( String parent, String child ) \n" +
            "   org.drools.integrationtests.BackwardChainingTest.Parent( parent : parent, child : child ) \n"+
            "end\n" +              
            
            "query father( String father, String child ) \n" +
            "   ?man( father; ) \n"+
            "   ?parent( father, child; ) \n"+
            "end\n" + 
            
            "query mother( String mother, String child ) \n" +
            "   ?woman( mother; ) \n"+
            "   ?parent( mother, child; ) \n"+
            "end\n" +             
        
            "query son( String son, String parent ) \n" +
            "   ?man( son; ) \n"+
            "   ?parent( parent, son; ) \n"+
            "end\n" +
        
            "query daughter( String daughter, String parent ) \n" +
            "   ?woman( daughter; ) \n"+
            "   ?parent( parent, daughter; ) \n"+
            "end\n" +
            
            "query siblings( String c1, String c2 ) \n" +
            "   ?parent( $p, c1; ) \n" +
            "   ?parent( $p, c2; ) \n"+
            "   eval( !c1.equals( c2 ) )\n"+
            "end\n"+        
        
            "query fullSiblings( String c1, String c2 )\n" +
            "   ?parent( $p1, c1; ) ?parent( $p1, c2; )\n" +
            "   ?parent( $p2, c1; ) ?parent( $p2, c2; )\n" +
            "   eval( !c1.equals( c2 ) && !$p1.equals( $p2 )  )\n"+
            "end\n" +
            
            "query fullSiblings2( String c1, String c2 )\n" +
            "   ?father( $p1, c1; ) ?father( $p1, c2; )\n" +
            "   ?mother( $p2, c1; ) ?mother( $p2, c2; )\n" +
            "   eval( !c1.equals( c2 ) )\n"+
            "end\n" +    
    
            "query uncle( String uncle, String n )\n" +
            "   ?man( uncle; ) ?siblings( uncle, parent; )\n" +
            "   ?parent( parent, n; )\n " +
            "end\n" +   
        
            "query aunt( String aunt, String n )\n" +
            "   ?woman( aunt; ) ?siblings( aunt, parent; )\n" +
            "   ?parent( parent, n; )\n " +
            "end\n" +
            
            "query grantParents( String gp, String gc )\n" +
            "   ?parent( gp, p; ) ?parent( p, gc; )\n" +
            "end\n";            
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );
    
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
    
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
    
        kbase = SerializationHelper.serializeObject( kbase );
    
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );     

        // grand parents
        ksession.insert( new Man("john") );
        ksession.insert( new Woman("janet") );
        
        // parent
        ksession.insert( new Man("adam") );
        ksession.insert(  new Parent( "john", "adam") );
        ksession.insert(  new Parent( "janet", "adam") );
        
        ksession.insert( new Man("stan") );
        ksession.insert(  new Parent( "john", "stan") );
        ksession.insert(  new Parent( "janet", "stan") );        
        
        // grant parents
        ksession.insert( new Man("carl") );
        ksession.insert( new Woman("tina") );        
 
        // parent         
        ksession.insert( new Woman("eve") );        
        ksession.insert(  new Parent( "carl", "eve") );
        ksession.insert(  new Parent( "tina", "eve") ); 

        
        // parent         
        ksession.insert( new Woman("mary") );  
        ksession.insert(  new Parent( "carl", "mary") );
        ksession.insert(  new Parent( "tina", "mary") );         
        
        
        ksession.insert( new Man("peter") );
        ksession.insert( new Parent( "adam", "peter" ) );        
        ksession.insert( new Parent( "eve", "peter" ) );
        
        
        ksession.insert( new Man("paul") );
        ksession.insert( new Parent( "adam", "paul" ) );
        ksession.insert( new Parent( "mary", "paul" ) );
                

        ksession.insert( new Woman("jill") );
        ksession.insert( new Parent( "adam", "jill" ) );        
        ksession.insert( new Parent( "eve", "jill" ) );
        
        QueryResults results = null;
        
        System.out.println("woman");         
        results = ksession.getQueryResults( "woman", new Object[] { variable } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + result.get( "name" ) );
        } 
        
        System.out.println("\nman");        
        results = ksession.getQueryResults( "man", new Object[] { variable } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + result.get( "name" ) );
        }   
        
        System.out.println("\nfather");
        results = ksession.getQueryResults( "father", new Object[] {variable,  variable  } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "father( " + result.get( "father" ) + ", " + result.get( "child" ) + " )" );
        }       
        
        System.out.println("\nmother");
        results = ksession.getQueryResults( "mother", new Object[] { variable,  variable } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "mother( " + result.get( "mother" ) + ", " + result.get( "child" ) + " )" );
        }    
        
        System.out.println("\nson");
        results = ksession.getQueryResults( "son", new Object[] { variable,  variable } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "son( " + result.get( "son" ) + ", " + result.get( "parent" ) + " )" );
        }     
        
        System.out.println("\ndaughter");
        results = ksession.getQueryResults( "daughter", new Object[] { variable,  variable } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "daughter( " + result.get( "daughter" ) + ", " + result.get( "parent" ) + " )" );
        }         
        
        System.out.println("\nsiblings");
        results = ksession.getQueryResults( "siblings", new Object[] { variable,  variable } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "sibling( " + result.get( "c1" ) + ", " + result.get( "c2" ) + " )" );
        }     
        
        System.out.println("\nfullSiblings");
        results = ksession.getQueryResults( "fullSiblings", new Object[] { variable,  variable } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "fullSiblings( " + result.get( "c1" ) + ", " + result.get( "c2" ) + " )" );
        }        

        System.out.println("\nfullSiblings2");
        results = ksession.getQueryResults( "fullSiblings", new Object[] { variable,  variable } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "fullSiblings2( " + result.get( "c1" ) + ", " + result.get( "c2" ) + " )" );
        }  
        
        System.out.println("\nuncle");
        results = ksession.getQueryResults( "uncle", new Object[] { variable,  variable } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "uncle( " + result.get( "uncle" ) + ", " + result.get( "n" ) + " )" );
        }        
        
        System.out.println("\naunt");
        results = ksession.getQueryResults( "aunt", new Object[] { variable,  variable } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "aunt( " + result.get( "aunt" ) + ", " + result.get( "n" ) + " )" );
        }
        
        System.out.println("\ngrantParents");
        results = ksession.getQueryResults( "grantParents", new Object[] { variable,  variable } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + "grantParents( " + result.get( "gp" ) + ", " + result.get( "gc" ) + " )" );
        }          
    }
    
    @Test
    public void testNaniSearch() throws Exception {
        // http://www.amzi.com/AdventureInProlog/advtop.php
            
        String str = "" +
            "package org.drools.test  \n" +
            
            "import java.util.List\n" +
            "import java.util.ArrayList\n" +
            
            "global List list\n" +
            
            "dialect \"mvel\"\n" +   
            
            "declare Room" +
            "    name : String\n" +
            "end\n" +
            "\n" +
            "declare Location\n" +
            "    thing : String \n" +
            "    location : String \n" +
            "end" +
            "\n" +
            "declare Door\n" +
            "   fromLocation : String\n" +
            "   toLocation : String\n" +
            "end" +
            "\n" +
            "declare Edible\n" +
            "   thing : String\n" +
            "end" +     
            "\n" +
            "declare TastesYucky\n" +
            "   thing : String\n" +
            "end" +        
            "\n" +
            "declare TastesYucky\n" +
            "   thing : String\n" +
            "end\n" +
            "\n" +  
            "declare Here\n" +
            "   place : String \n" +
            "end\n" +
            "\n" +               
        
            "query whereFood( String thing, String location ) \n" +
            "    ( Location(thing, location;) and\n"+
            "      Edible(thing;) )\n " +
            "    or \n"+
            "    ( Location(thing, location;) and\n"+
            "      TastesYucky(thing;) ) \n"+            
            "end\n" +
            "\n" +   
        
            "query connect( String x, String y ) \n" +
            "    Door(x, y;)\n"+
            "    or \n"+
            "    Door(y, x;)\n"+          
            "end\n" + 
            "\n" +     
            "\n" +
            "query isContainedIn( String x, String y ) \n" +
            "    Location(x, y;)\n"+
            "    or \n"+
            "    ( Location(z, y;) and ?isContainedIn(x, z;) )\n"+          
            "end\n" +            
            "\n" +              
            "query look(String place, List things, List food, List exits) \n" +
            "    Here(place;)\n"+            
            "    things : List() from accumulate( Location(thing, place;),\n" +
            "                                    collectList( thing ) )\n" +   
            "    food : List() from accumulate( ?whereFood(thing, place;) ," +
            "                                    collectList( thing ) )\n" +                
            
            "    exits : List() from accumulate( ?connect(place, exit;),\n" +
            "                                    collectList( exit ) )\n" +        
            "end\n" +
            "\n" +
            "rule reactiveLook when\n" +
            "    Here( place : place) \n"+
            "    ?look(place, things, food, exits;)\n"+
            "then\n" +
            "    System.out.println( \"You are in the \" + place);\n" +
            "    System.out.println( \"  You can see \" + things );\n" +
            "    System.out.println( \"  You can eat \" + food );\n" +             
            "    System.out.println( \"  You can go to \" + exits );\n" +
            "end\n" +
            "\n" +
            "rule init when\n" +
            "then\n" +
            "        insert( new Room(\"kitchen\") );\n" + 
            "        insert( new Room(\"office\") );\n" + 
            "        insert( new Room(\"hall\") );\n" + 
            "        insert( new Room(\"dining room\") );\n" + 
            "        insert( new Room(\"cellar\") );\n" + 
            "        \n" + 
            "        insert( new Location(\"apple\", \"kitchen\") );\n" +
            
            "        insert( new Location(\"desk\", \"office\") );\n" +             
            "        insert( new Location(\"flashlight\", \"desk\") );\n" +
            "        insert( new Location(\"envelope\", \"desk\") );\n" +
            "        insert( new Location(\"key\", \"envelope\") );\n" +
            
            
            "        insert( new Location(\"washing machine\", \"cellar\") );\n" + 
            "        insert( new Location(\"nani\", \"washing machine\") );\n" + 
            "        insert( new Location(\"broccoli\", \"kitchen\") );\n" + 
            "        insert( new Location(\"crackers\", \"kitchen\") );\n" + 
            "        insert( new Location(\"compuer\", \"office\") );\n" + 
            "        \n" + 
            "        insert( new Door(\"office\", \"hall\") );\n" + 
            "        insert( new Door(\"kitchen\", \"office\") );\n" + 
            "        insert( new Door(\"hall\", \"dining room\") );\n" + 
            "        insert( new Door(\"kitchen\", \"cellar\") );\n" + 
            "        insert( new Door(\"dining room\", \"kitchen\") );\n" + 
            "        \n" +  
            "        insert( new Edible(\"apple\") );\n" + 
            "        insert( new Edible(\"crackers\") );\n" + 
            "        \n" + 
            "        insert( new TastesYucky(\"broccoli\") );  " +
            "end\n" +
            "" +
            "rule go1 when\n" +
            "   String( this == 'go1' )\n" +
            "then\n" +
            "   insert( new Here(\"kitchen\") );\n" + 
            "end\n" +
            "\n" +
            "rule go2 when\n" +
            "   String( this == 'go2' )\n" +
            "   $h : Here( place == \"kitchen\")" +
            "then\n" +
            "   modify( $h ) { place = \"office\" };\n" +
            "end\n"            
            ;            
            
        System.out.println( str );
        
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );
    
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }        
    
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
    
        kbase = SerializationHelper.serializeObject( kbase );
    
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );           
        
        QueryResults results = null;
        ksession.fireAllRules();
        
        ksession.insert( "go1" );
        ksession.fireAllRules();    
        
        ksession.insert( "go2" );
        ksession.fireAllRules();       
        
        System.out.println("isContainedIn key in office");         
        results = ksession.getQueryResults( "isContainedIn", new Object[] { "key", "office" } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + result.get( "x" )+ ":"+ result.get( "y" ) );
        } 
        
        System.out.println("isContainedIn apple in office");         
        results = ksession.getQueryResults( "isContainedIn", new Object[] { "apple", "office" } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "  " + result.get( "x" )+ ":"+ result.get( "y" ) );
        }         
                
    }    
    
    public static class Man {
        private String name;      

        public Man(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }      
    }
    
    public static class Woman {
        private String name;      

        public Woman(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }      
    }  
    
    public static class Parent {
        private String parent;
        private String child;
        
        public Parent(String parent,
                      String child) {
            this.parent = parent;
            this.child = child;
        }
        
        public String getParent() {
            return parent;
        }
        
        public void setParent(String parent) {
            this.parent = parent;
        }
        
        public String getChild() {
            return child;
        }
        
        public void setChild(String child) {
            this.child = child;
        }
    }
    
//    public static class Room {
//        private String name;
//
//        public Room(String name) {
//            this.name = name;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//        
//    }
//    
//    public static class Location {
//        private String thing;
//        private String location;
//        
//        public Location(String thing,
//                        String location) {
//            this.thing = thing;
//            this.location = location;
//        }
//
//        public String getThing() {
//            return thing;
//        }
//
//        public void setThing(String thing) {
//            this.thing = thing;
//        }
//
//        public String getLocation() {
//            return location;
//        }
//
//        public void setLocation(String location) {
//            this.location = location;
//        }
//    }
//    
//    public static class Door {
//        private String fromLocation;
//        private String toLocation;
//        
//        public Door(String fromLocation,
//                    String toLocation) {
//            this.fromLocation = fromLocation;
//            this.toLocation = toLocation;
//        }
//        public String getFromLocation() {
//            return fromLocation;
//        }
//        public void setFromLocation(String fromLocation) {
//            this.fromLocation = fromLocation;
//        }
//        public String getToLocation() {
//            return toLocation;
//        }
//        public void setToLocation(String toLocation) {
//            this.toLocation = toLocation;
//        }   
//    }
//    
//    public static class Edible {
//        private String thing;
//
//        public Edible(String thing) {
//            this.thing = thing;
//        }
//
//        public String getThing() {
//            return thing;
//        }
//
//        public void setThing(String thing) {
//            this.thing = thing;
//        }        
//    }
//    
//    public static class TastesYucky {
//        private String thing;
//
//        public TastesYucky(String thing) {
//            this.thing = thing;
//        }
//
//        public String getThing() {
//            return thing;
//        }
//
//        public void setThing(String thing) {
//            this.thing = thing;
//        }        
//    }    
//    
//    public static class TurnedOff {
//        private String thing;
//
//        public TurnedOff(String thing) {
//            this.thing = thing;
//        }
//
//        public String getThing() {
//            return thing;
//        }
//
//        public void setThing(String thing) {
//            this.thing = thing;
//        }
//    }
//    
//    public static class Here {
//        private String location;
//
//        public Here(String location) {
//            this.location = location;
//        }
//
//        public String getLocation() {
//            return location;
//        }
//
//        public void setLocation(String location) {
//            this.location = location;
//        }
//    }
    
    public static class Q {
        int value;

        public Q(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
        public String toString() {
            return "Q" + value;
        }        
    }
    
    public static class R {
        int value;

        public R(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
        public String toString() {
            return "R" + value;
        }
    }    
    
    public static class S {
        int value;

        public S(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }
        public String toString() {
            return "S" + value;
        }
    }    
    
    
}
