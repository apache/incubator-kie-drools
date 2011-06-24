package org.drools.integrationtests;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.drools.Address;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.Person;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ByteArrayResource;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.Variable;
import org.junit.Test;

import static org.drools.integrationtests.SerializationHelper.getSerialisedStatefulKnowledgeSession;
import static org.drools.runtime.rule.Variable.v;


public class BackwardChainingTest {
    @Test
    public void testQueryPositional() throws Exception {
        String str = "" +
            "package org.drools.test  \n" +
            "import org.drools.Person \n" +
            "global java.util.List list\n" +
            "query peeps( String $name, String $likes, int $age ) \n" +
            "    Person( $name := name, $likes := likes, $age := age; ) \n"+
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
            "    Person( $name := name, $likes := likes, $age := age ) \n"+
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
            "    Person( $name := name, $likes := likes, $age := age; ) \n"+
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
            "    $p := Person( $name := name, $likes := likes, $age := age; ) \n"+
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
        ksession = getSerialisedStatefulKnowledgeSession( ksession,
                                                          true );           
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
            "   Person( $name := name, $likes := likes, $street := address.street ) \n"+
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
            "   Person( $name := name, $likes := likes, $street := address.street ) \n"+
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
            "    $p := Person( ) from new Person( $name, $likes, $age ) \n"+
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
            "    $p := Person( ) from new Person( $name, $likes, $age ) \n"+
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
            
            "\n" + 
            "query q(int x)\n" + 
            "    Q( x := value )\n" + 
            "end\n" + 
            "\n" + 
            "query r(int x)\n" + 
            "    R( x := value )\n" + 
            "end\n" + 
            "\n" + 
            "query s(int x)\n" + 
            "    S( x := value )    \n" + 
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
            " insert( new Q(5) );\n " +
            " insert( new Q(6) );\n " +
            " insert( new R(1) );\n " +
            " insert( new R(4) );\n " +
            " insert( new R(6) );\n " +
            " insert( new R(2) );\n " +
            " insert( new S(2) );\n " +
            " insert( new S(3) );\n " +
            " insert( new S(6) );\n " +
            "end\n" +              
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
        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list", list );
        
        ksession.fireAllRules();

        QueryResults results = null;
        
        list.clear();
        results = ksession.getQueryResults( "p", new Integer[] { 0 }  );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }   
        assertEquals( 0, list.size() );
        
        list.clear();
        results = ksession.getQueryResults( "p", new Integer[] { 1 }  );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }

        assertEquals( 1, list.size() );
        assertEquals( 1, list.get(0).intValue());
        
        System.out.println( );
        
        list.clear();
        results = ksession.getQueryResults( "p", new Integer[] { 2 }  );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        } 
        assertEquals( 1, list.size() );
        assertEquals( 2, list.get(0).intValue());
        
        list.clear();
        results = ksession.getQueryResults( "p", new Integer[] { 3 }  );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }   
        assertEquals( 1, list.size() );
        assertEquals( 3, list.get(0).intValue());        
        
        list.clear();
        results = ksession.getQueryResults( "p", new Integer[] { 4 }  );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }
        assertEquals( 0, list.size() );
        
        list.clear();
        results = ksession.getQueryResults( "p", new Integer[] { 5 }  );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }
        assertEquals( 0, list.size() );
        
        list.clear();
        results = ksession.getQueryResults( "p", new Integer[] { 6 }  );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }                
        assertEquals( 2, list.size() );
        assertEquals( 6, list.get(0).intValue());
        assertEquals( 6, list.get(1).intValue());
    }      
    
    @Test
    public void testGeneology() throws Exception {
        // from http://kti.mff.cuni.cz/~bartak/prolog/genealogy.html
            
        String str = "" +
            "package org.drools.test2  \n" +
            "global java.util.List list\n" +
            "dialect \"mvel\"\n" +                   
                                    
            "query man( String name ) \n" +
            "   org.drools.integrationtests.BackwardChainingTest.Man( name := name ) \n"+
            "end\n" +
        
            "query woman( String name ) \n" +
            "   org.drools.integrationtests.BackwardChainingTest.Woman( name := name ) \n"+
            "end\n" +    
            
            "query parent( String parent, String child ) \n" +
            "   org.drools.integrationtests.BackwardChainingTest.Parent( parent := parent, child := child ) \n"+
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
        
//        // grand parents
        ksession.insert( new Man("carl") );
        ksession.insert( new Woman("tina") );        
// 
//        // parent         
        ksession.insert( new Woman("eve") );        
        ksession.insert(  new Parent( "carl", "eve") );
        ksession.insert(  new Parent( "tina", "eve") ); 
//
//        // parent         
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
        
        //System.out.println("woman");         
        list.clear();
        results = ksession.getQueryResults( "woman", new Object[] { v } );
        for ( QueryResultsRow result : results ) {
            list.add( (String) result.get( "name" ) );
        } 
        assertEquals( 5, list.size());
        assertContains( new String[] { "janet", "mary", "tina", "eve", "jill"}, list);
        
        list.clear();
        //System.out.println("\nman");        
        results = ksession.getQueryResults( "man", new Object[] { v } );
        for ( QueryResultsRow result : results ) {
            list.add( (String) result.get( "name" ) );
        }   
        assertEquals( 6, list.size());
        assertContains( new String[] { "stan", "john", "peter", "carl", "adam", "paul"}, list);
        
        list.clear();
        //System.out.println("\nfather");
        results = ksession.getQueryResults( "father", new Object[] {v,  v  } );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "father" ) + ", " + result.get( "child" ) );
        }       
        assertEquals( 7, list.size());
        assertContains( new String[] { "john, adam", "john, stan", 
                                       "carl, eve", "carl, mary", 
                                       "adam, peter", "adam, paul",
                                       "adam, jill"}, list);
        
        list.clear();
        //System.out.println("\nmother");
        results = ksession.getQueryResults( "mother", new Object[] { v,  v } );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "mother" ) + ", " + result.get( "child" ) );
        }    
        assertEquals( 7, list.size());
        assertContains( new String[] { "janet, adam", "janet, stan", 
                                       "mary, paul", "tina, eve", 
                                       "tina, mary", "eve, peter",
                                       "eve, jill"}, list);        
        
        
        list.clear();
        //System.out.println("\nson");
        results = ksession.getQueryResults( "son", new Object[] { v,  v } );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "son" ) + ", " + result.get( "parent" ) );
        }  
        assertEquals( 8, list.size());
        assertContains( new String[] { "stan, john", "stan, janet", 
                                       "peter, adam", "peter, eve", 
                                       "adam, john", "adam, janet",
                                       "paul, mary", "paul, adam"}, list); 
        
        list.clear();
        //System.out.println("\ndaughter");
        results = ksession.getQueryResults( "daughter", new Object[] { v,  v } );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "daughter" ) + ", " + result.get( "parent" ) );
        }         
        assertEquals( 6, list.size());
        assertContains( new String[] { "mary, carl", "mary, tina", 
                                       "eve, carl", "eve, tina", 
                                       "jill, adam", "jill, eve"}, list);        
        
        list.clear();
        //System.out.println("\nsiblings");
        results = ksession.getQueryResults( "siblings", new Object[] { v,  v } );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "c1" ) + ", " + result.get( "c2" ) );
        } 
        assertEquals( 16, list.size());
        assertContains( new String[] { "eve, mary",  "mary, eve", 
                                       "adam, stan", "stan, adam", 
                                       "adam, stan", "stan, adam", 
                                       "peter, paul", "peter, jill", 
                                       "paul, peter", "paul, jill", 
                                       "jill, peter", "jill, paul", 
                                       "peter, jill", "jill, peter", 
                                       "eve, mary",  "mary, eve"}, list);             
        
        list.clear();
        //System.out.println("\nfullSiblings");
        results = ksession.getQueryResults( "fullSiblings", new Object[] { v,  v } );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "c1" ) + ", " + result.get( "c2" ) );
        }        
        assertEquals( 12, list.size());
        assertContains( new String[] { "eve, mary", "mary, eve", 
                                       "adam, stan", "stan, adam", 
                                       "adam, stan", "stan, adam", 
                                       "peter, jill", "jill, peter", 
                                       "peter, jill", "jill, peter", 
                                       "eve, mary", "mary, eve" }, list);         

        list.clear();
        //System.out.println("\nfullSiblings2");
        results = ksession.getQueryResults( "fullSiblings", new Object[] { v,  v } );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "c1" ) + ", " + result.get( "c2" ) );
        }  
        assertEquals( 12, list.size());
        assertContains( new String[] { "eve, mary", "mary, eve", 
                                       "adam, stan", "stan, adam", 
                                       "adam, stan", "stan, adam", 
                                       "peter, jill", "jill, peter", 
                                       "peter, jill", "jill, peter", 
                                       "eve, mary", "mary, eve" }, list);          
        
        list.clear();
        //System.out.println("\nuncle");
        results = ksession.getQueryResults( "uncle", new Object[] { v,  v } );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "uncle" ) + ", " + result.get( "n" ) );            
        }    
        assertEquals( 6, list.size());
        assertContains( new String[] { "stan, peter", 
                                       "stan, paul", 
                                       "stan, jill", 
                                       "stan, peter", 
                                       "stan, paul", 
                                       "stan, jill" }, list);           
        
        list.clear();
        //System.out.println("\naunt");
        results = ksession.getQueryResults( "aunt", new Object[] { v,  v } );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "aunt" ) + ", " + result.get( "n" ) );
        }
        assertEquals( 6, list.size());
        assertContains( new String[] { "mary, peter", 
                                       "mary, jill", 
                                       "mary, peter", 
                                       "mary, jill", 
                                       "eve, paul", 
                                       "eve, paul" }, list);          
        
        list.clear();
        //System.out.println("\ngrantParents");
        results = ksession.getQueryResults( "grantParents", new Object[] { v,  v } );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "gp" ) + ", " + result.get( "gc" ) );            
        }      
        assertEquals( 12, list.size());
        assertContains( new String[] { "carl, peter", 
                                       "carl, jill", 
                                       "carl, paul", 
                                       "john, peter", 
                                       "john, paul", 
                                       "john, jill", 
                                       "janet, peter", 
                                       "janet, paul", 
                                       "janet, jill", 
                                       "tina, peter", 
                                       "tina, jill", 
                                       "tina, paul",  }, list);         
    }
    
    @Test
    public void testNaniSearchs() throws Exception {
        // http://www.amzi.com/AdventureInProlog/advtop.php
            
        String str = "" +
            "package org.drools.test  \n" +
            
            "import java.util.List\n" +
            "import java.util.ArrayList\n" +

            "import java.util.Map\n" +
            "import java.util.HashMap\n" +            
            
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

            "query whereFood( String x, String y ) \n" +
            "    ( Location(x, y;) and\n"+
            "      Edible(x;) ) " +
            "     or \n " +
            "    ( Location(z, y;) and ?whereFood(x, z;) )\n"+                      
            "end\n" +            
            
            "query connect( String x, String y ) \n" +
            "    Door(x, y;)\n"+
            "    or \n"+
            "    Door(y, x;)\n"+          
            "end\n" + 
            "\n" +     
            "query isContainedIn( String x, String y ) \n" +
            "    Location(x, y;)\n"+            
            "    or \n"+
            "    ( Location(z, y;) and ?isContainedIn(x, z;) )\n"+          
            "end\n" +            
            "\n" +                            
            "query look(String place, List things, List food, List exits ) \n" + 
            "    Here(place;)\n"+            
            "    things := List() from accumulate( Location(thing, place;),\n" +
            "                                      collectList( thing ) )\n" +   
            "    food := List() from accumulate( ?whereFood(thing, place;) ," +
            "                                    collectList( thing ) )\n" +                            
            "    exits := List() from accumulate( ?connect(place, exit;),\n" +
            "                                    collectList( exit ) )\n" +        
            "end\n" +
            "\n" +            
            "rule reactiveLook when\n" +
            "    Here( place : place) \n"+
            "    ?look(place, things, food, exits;)\n"+
            "then\n" +
            "    Map map = new HashMap();" +
            "    list.add(map);" +
            "    map.put( 'place', place); " +
            "    map.put( 'things', things); " +
            "    map.put( 'food', food); " +
            "    map.put( 'exits', exits); " +
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
            "        insert( new Location(\"apple\", \"desk\") );\n" +            
            "        insert( new Location(\"flashlight\", \"desk\") );\n" +
            "        insert( new Location(\"envelope\", \"desk\") );\n" +
            "        insert( new Location(\"key\", \"envelope\") );\n" +
            
            
            "        insert( new Location(\"washing machine\", \"cellar\") );\n" + 
            "        insert( new Location(\"nani\", \"washing machine\") );\n" + 
            "        insert( new Location(\"broccoli\", \"kitchen\") );\n" + 
            "        insert( new Location(\"crackers\", \"kitchen\") );\n" + 
            "        insert( new Location(\"computer\", \"office\") );\n" + 
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
            "end\n" +   
            ""
            ;            
        
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
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ksession.setGlobal( "list", list );           
        
        QueryResults results = null;
        ksession.fireAllRules();
        
        ksession.insert( "go1" );
        ksession.fireAllRules();  
        
        Map<String, Object> map = ( Map ) list.get(0);
        assertEquals( "kitchen", map.get( "place" ) );
        List<String> items = ( List<String> ) map.get( "things" );      
        assertEquals( 3, items.size() );
        assertContains( new String[] { "apple", "broccoli", "crackers" }, items);

        items = ( List<String> ) map.get( "food" );      
        assertEquals( 2, items.size() );
        assertContains( new String[] { "apple",  "crackers" }, items);        

        items = ( List<String> ) map.get( "exits" );      
        assertEquals( 3, items.size() );
        assertContains( new String[] { "office",  "cellar", "dining room" }, items);
        
        
        ksession.insert( "go2" );
        ksession.fireAllRules();       
        
        map = ( Map ) list.get(1);
        assertEquals( "office", map.get( "place" ) );
        items = ( List<String> ) map.get( "things" );      
        assertEquals( 2, items.size() );
        assertContains( new String[] { "computer", "desk", }, items);

        items = ( List<String> ) map.get( "food" );      
        assertEquals( 1, items.size() );
        assertContains( new String[] { "apple" }, items); // notice the apple is on the desk in the office        
                

        items = ( List<String> ) map.get( "exits" );      
        assertEquals( 2, items.size() );
        assertContains( new String[] { "hall",  "kitchen" }, items);        
         
        results = ksession.getQueryResults( "isContainedIn", new Object[] { "key", "office" } );
        assertEquals( 1, results.size() );
        QueryResultsRow result = results.iterator().next();
        assertEquals( "key",  result.get( "x" ) );
        assertEquals( "office",  result.get( "y" ) );
        
        results = ksession.getQueryResults( "isContainedIn", new Object[] { "key", Variable.v } );
        List<List<String>> l = new ArrayList<List<String>>();
        for ( QueryResultsRow r : results ) {
            l.add( Arrays.asList( new String[] { (String) r.get( "x" ), (String) r.get( "y" ) } ) );
        }  
        assertEquals( 3, results.size() );
        assertContains( Arrays.asList( new String[] { "key", "desk" } ), l);
        assertContains( Arrays.asList( new String[] { "key", "office" } ), l);
        assertContains( Arrays.asList( new String[] { "key", "envelope" } ), l);
        
        results = ksession.getQueryResults( "isContainedIn", new Object[] {  Variable.v, "office"} );
        l = new ArrayList<List<String>>();
        for ( QueryResultsRow r : results ) {
            l.add( Arrays.asList( new String[] { (String) r.get( "x" ), (String) r.get( "y" ) } ) );
        }  
        
        assertEquals( 6, results.size() );
        assertContains( Arrays.asList( new String[] { "desk", "office" } ), l);
        assertContains( Arrays.asList( new String[] { "computer", "office" } ), l);
        assertContains( Arrays.asList( new String[] { "apple", "office" } ), l);
        assertContains( Arrays.asList( new String[] { "envelope", "office" } ), l);
        assertContains( Arrays.asList( new String[] { "flashlight", "office" } ), l);
        assertContains( Arrays.asList( new String[] { "key", "office" } ), l);
        
        results = ksession.getQueryResults( "isContainedIn", new Object[] {  Variable.v, Variable.v} );
        l = new ArrayList<List<String>>();
        for ( QueryResultsRow r : results ) {
            l.add( Arrays.asList( new String[] { (String) r.get( "x" ), (String) r.get( "y" ) } ) );
        }  
        assertEquals( 17, results.size() );
        assertContains( Arrays.asList( new String[] { "apple", "kitchen"} ), l);
        assertContains( Arrays.asList( new String[] { "apple", "desk"} ), l);
        assertContains( Arrays.asList( new String[] { "envelope", "desk"} ), l);
        assertContains( Arrays.asList( new String[] { "desk", "office"} ), l);
        assertContains( Arrays.asList( new String[] { "computer", "office"} ), l);
        assertContains( Arrays.asList( new String[] { "washing machine", "cellar"} ), l);
        assertContains( Arrays.asList( new String[] { "key", "envelope"} ), l);
        assertContains( Arrays.asList( new String[] { "broccoli", "kitchen"} ), l);
        assertContains( Arrays.asList( new String[] { "nani", "washing machine"} ), l);
        assertContains( Arrays.asList( new String[] { "crackers", "kitchen"} ), l);
        assertContains( Arrays.asList( new String[] { "flashlight", "desk"} ), l);
        assertContains( Arrays.asList( new String[] { "nani", "cellar"} ), l);
        assertContains( Arrays.asList( new String[] { "apple", "office"} ), l);
        assertContains( Arrays.asList( new String[] { "envelope", "office"} ), l);
        assertContains( Arrays.asList( new String[] { "flashlight", "office"} ), l);
        assertContains( Arrays.asList( new String[] { "key", "office"} ), l);
        assertContains( Arrays.asList( new String[] { "key", "desk"} ), l);                            
    }   
    
    @Test
    public void testOpenBackwardChain() throws Exception {
        // http://www.amzi.com/AdventureInProlog/advtop.php
            
        String str = "" +
            "package org.drools.test  \n" +
            
            "import java.util.List\n" +
            "import java.util.ArrayList\n" +
            "import org.drools.Person\n" +
            
            "global List list\n" +
            
            "dialect \"mvel\"\n" +   
           
            "declare Location\n" +
            "    thing : String \n" +
            "    location : String \n" +
            "end" +  
            "\n" +
            "query isContainedIn( String x, String y ) \n" +
            "    Location(x, y;)\n"+
            "    or \n"+
            "    ( Location(z, y;) and isContainedIn(x, z;) )\n"+          
            "end\n" +            
            "\n" +
            "rule look when \n" +
            "    Person( $l : likes ) \n" +
            "    isContainedIn( $l, 'office'; )\n" +
            "then\n" +
            "   insertLogical( 'blah' );" +
            "end\n" +  
            "rule existsBlah when \n" +
            "    exists String( this == 'blah') \n" +
            "then\n" +
            "   list.add( 'exists blah' );" +
            "end\n" +             
            "\n" +
            "rule notBlah when \n" +
            "    not String( this == 'blah') \n" +
            "then\n" +
            "   list.add( 'not blah' );" +
            "end\n" +             
            "\n" +            
            "rule init when\n" +
            "then\n" +
            "        insert( new Location(\"apple\", \"kitchen\") );\n" +           
            "        insert( new Location(\"desk\", \"office\") );\n" +             
            "        insert( new Location(\"flashlight\", \"desk\") );\n" +
            "        insert( new Location(\"envelope\", \"desk\") );\n" +
            "        insert( new Location(\"key\", \"envelope\") );\n" +                        
            "        insert( new Location(\"washing machine\", \"cellar\") );\n" + 
            "        insert( new Location(\"nani\", \"washing machine\") );\n" + 
            "        insert( new Location(\"broccoli\", \"kitchen\") );\n" + 
            "        insert( new Location(\"crackers\", \"kitchen\") );\n" + 
            "        insert( new Location(\"computer\", \"office\") );\n" + 
            "end\n"  + 
            "\n" +            
            "rule go1 when \n" +
            "    String( this == 'go1') \n" +            
            "then\n" +
            "        list.add( rule.getName() ); \n" +
            "        insert( new Location('lamp', 'desk') );\n" +
            "end\n" +              
            "\n" +
            "rule go2 when \n" +
            "    String( this == 'go2') \n" +
            "    $l : Location('lamp', 'desk'; )\n" +            
            "then\n" +      
            "    list.add( rule.getName() ); \n" +            
            "    retract( $l );\n" +
            "end\n" +              
            "\n" +     
            "rule go3 when \n" +
            "    String( this == 'go3') \n" +            
            "then\n" +
            "        list.add( rule.getName() ); \n" +               
            "        insert( new Location('lamp', 'desk') );\n" +
            "end\n" +  
            "\n" +            
            "rule go4 when \n" +
            "    String( this == 'go4') \n" +
            "    $l : Location('lamp', 'desk'; )\n" +            
            "then\n" +
            "        list.add( rule.getName() ); \n" +      
            "    modify( $l ) { thing = 'book' };\n" +
            "end\n" + 
            "\n" +            
            "rule go5 when \n" +
            "    String( this == 'go5') \n" +
            "    $l : Location('book', 'desk'; )\n" +            
            "then\n" +
            "    list.add( rule.getName() ); \n" +      
            "    modify( $l ) { thing = 'lamp' };\n" + 
            "end\n" +             
            "\n" +
            "rule go6 when \n" +
            "    String( this == 'go6') \n" +            
            "    $l : Location( 'lamp', 'desk'; )\n" +            
            "then\n" +
            "    list.add( rule.getName() ); \n" +  
            "    modify( $l ) { thing = 'book' };\n" +                       
            "end\n" +             
            "\n"  +
            "rule go7 when \n" +
            "    String( this == 'go7') \n" +            
            "    $p : Person( likes == 'lamp' ) \n" +               
            "then\n" +
            "    list.add( rule.getName() ); \n" +  
            "    modify( $p ) { likes = 'key' };\n" +             
            "end\n" +             
            "\n"            
            ;   
        
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
        
        Person p = new Person();
        p.setLikes( "lamp" );
        FactHandle handle = ksession.insert(  p  );
        ksession.fireAllRules();
        
        list.clear();

        FactHandle fh = ksession.insert( "go1" );
        ksession.fireAllRules();
        ksession.retract( fh );        
        assertEquals( "go1", list.get(0));
        assertEquals( "exists blah", list.get(1));

        fh = ksession.insert( "go2" );
        ksession.fireAllRules();
        ksession.retract( fh );
        assertEquals( "go2", list.get(2));
        assertEquals( "not blah", list.get(3));
        
        fh = ksession.insert( "go3" );
        ksession.fireAllRules();
        ksession.retract( fh );
        assertEquals( "go3", list.get(4));
        assertEquals( "exists blah", list.get(5));        
        
        fh = ksession.insert( "go4" );
        ksession.fireAllRules();
        ksession.retract( fh );
        assertEquals( "go4", list.get(6));
        assertEquals( "not blah", list.get(7));          
        
        fh = ksession.insert( "go5" );
        ksession.fireAllRules();
        ksession.retract( fh );
        assertEquals( "go5", list.get(8));
        assertEquals( "exists blah", list.get(9));
        
        // This simulates a modify of the root DroolsQuery object, but first we break it
        fh = ksession.insert( "go6" );
        ksession.fireAllRules();
        ksession.retract( fh );
        assertEquals( "go6", list.get(10));
        assertEquals( "not blah", list.get(11));  
        
        // now fix it
        fh = ksession.insert( "go7" );
        ksession.fireAllRules();
        ksession.retract( fh );
        assertEquals( "go7", list.get(12));
        assertEquals( "exists blah", list.get(13));                          
    }
    
    @Test
    public void testCompile() {
        String drl = "";
        
        drl = "declare Location\n"
              + "thing : String\n"
              + "location : String\n"
              + "end\n\n";
 
        drl = drl + "query isContainedIn( String x, String y )\n"
              + "Location( x := thing, y := location)\n"
              + "or \n"
              + "( Location(z := thing, y := location) and ?isContainedIn( x := x, z := y ) )\n"
              + "end\n"; 
 
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( new ByteArrayResource( drl.getBytes() ),
                      ResourceType.DRL );
 
        if ( kbuilder.hasErrors() ) {
           fail( kbuilder.getErrors().toString() );
        }      
    }
    
    @Test
    public void testInsertionOrder() throws Exception {
        // http://www.amzi.com/AdventureInProlog/advtop.php

        String str = "" +
            "package org.test  \n" +

            "import java.util.List\n" +
            "import java.util.ArrayList\n" +

            "global List list\n" +

            "dialect \"mvel\"\n" +

            "declare Person\n" +
            "   name : String\n" +
            "   likes : String\n" +
            "end\n" +
            "\n" +
            "declare Location\n" +
            "    thing : String \n" +
            "    location : String \n" +
            "end\n" +
            "\n" +
            "declare Edible\n" +
            "   thing : String\n" +
            "end\n" +
            "\n" +
            "\n" +
            "query hasFood( String x, String y ) \n" +
            "    Location(x, y;) " +
            "     or \n " +
            "    ( Location(z, y;) and hasFood(x, z;) )\n"+
            "end\n" +
            "\n" +
            "rule look when \n" +
            "    Person( $l : likes ) \n" +
            "    hasFood( $l, 'kitchen'; )\n" +
            "then\n" +
            "   list.add( 'kitchen has ' + $l );" +
            "end\n" +
            "rule go1 when\n" +
            "    String( this == 'go1') \n" +
            "then\n" +
            "        insert( new Person('zool', 'peach') );\n" +            
            "        insert( new Location(\"table\", \"kitchen\") );\n" +
            "        insert( new Location(\"peach\", \"table\") );\n" +
            "end\n"  +
            "rule go2 when\n" +
            "    String( this == 'go2') \n" +
            "then\n" +
            "        insert( new Person('zool', 'peach') );\n" +         
            "        insert( new Location(\"peach\", \"table\") );\n" +            
            "        insert( new Location(\"table\", \"kitchen\") );\n" +            
            "end\n"  +            
            "\n" +
            "rule go3 when\n" +
            "    String( this == 'go3') \n" +
            "then\n" +
            "        insert( new Location(\"table\", \"kitchen\") );\n" +
            "        insert( new Location(\"peach\", \"table\") );\n" +
            "        insert( new Person('zool', 'peach') );\n" +            
            "end\n"  +
            "\n" +
            "rule go4 when\n" +
            "    String( this == 'go4') \n" +
            "then\n" +
            "        insert( new Location(\"peach\", \"table\") );\n" +            
            "        insert( new Location(\"table\", \"kitchen\") );\n" +
            "        insert( new Person('zool', 'peach') );\n" +            
            "end\n"  +
            "rule go5 when\n" +
            "    String( this == 'go5') \n" +
            "then\n" +
            "        insert( new Location(\"peach\", \"table\") );\n" +  
            "        insert( new Person('zool', 'peach') );\n" +            
            "        insert( new Location(\"table\", \"kitchen\") );\n" +            
            "end\n"  +
            "rule go6 when\n" +
            "    String( this == 'go6') \n" +
            "then\n" +
            "        insert( new Location(\"table\", \"kitchen\") );\n" +
            "        insert( new Person('zool', 'peach') );\n" +            
            "        insert( new Location(\"peach\", \"table\") );\n" +                          
            "end\n"  +                        
            "\n" +            
            "\n";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        kbase = SerializationHelper.serializeObject( kbase );

        for ( int i = 1; i <= 6; i++) {
            StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
            List<String> list = new ArrayList<String>();
            ksession.setGlobal( "list", list );            
            ksession.fireAllRules();
            list.clear();
            FactHandle fh = ksession.insert( "go" + i );
            ksession.fireAllRules();
            ksession.retract( fh );
            assertEquals( 1, list.size() );
            assertEquals( "kitchen has peach", list.get( 0  ) );
            ksession.dispose();
        }
    }    
    
    public void assertContains( Object[] objects, List list) {
        for ( Object object : objects ) {
            if ( !list.contains( object ) ) {
                fail("does not contain:" + object);
            }
        }
    }
    
    public void assertContains( List objects, List list) {
            if ( !list.contains( objects ) ) {
                fail("does not contain:" + objects);
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
