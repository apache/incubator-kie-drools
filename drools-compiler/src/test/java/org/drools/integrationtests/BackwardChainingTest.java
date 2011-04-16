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
            "    peeps($name1, $likes1, $age1; )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";        
        
        str += "rule x2\n" + 
            "when\n" + 
            "    String( this == \"go2\" )\n" +
            //         output, input      ,output
            "    peeps($name1, \"stilton\", $age1; )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";

        str += "rule x3\n" + 
            "when\n" + 
            "    String( this == \"go3\" )\n" +
            "    $name1 : String() from \"darth\"\n "+
            //         input , input      ,output        
            "    peeps($name1, \"stilton\", $age1; )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";        
        
        str += "rule x4\n" + 
            "when\n" + 
            "    String( this == \"go4\" )\n" +
            "    $name1 : String() from \"darth\"\n "+
            "    $age1 : Integer() from 200\n "+        
            //         input , input      ,input        
            "    peeps($name1, \"stilton\", $age1; )\n" + 
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

        //kbase = SerializationHelper.serializeObject( kbase );

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
        ksession.fireAllRules();
        assertEquals( 5, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        assertTrue( list.contains( "yoda : 300" ));
        assertTrue( list.contains( "luke : 300" ));
        assertTrue( list.contains( "bobba : 300" ));
        
        list.clear();        
        ksession.insert( "go2" );
        ksession.fireAllRules();
        assertEquals( 3, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        assertTrue( list.contains( "yoda : 300" ));
        
        list.clear();
        ksession.insert( "go3" );
        ksession.fireAllRules();   
        assertEquals( 2, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        
        list.clear();        
        ksession.insert( "go4" );
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
            "    peeps($name1 : $name, $likes1 : $likes, $age1 : $age )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";        
        
        str += "rule x2\n" + 
            "when\n" + 
            "    String( this == \"go2\" )\n" +
            //         output        ,output                ,output
            "    peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";

        str += "rule x3\n" + 
            "when\n" + 
            "    String( this == \"go3\" )\n" +
            "    $name1 : String() from \"darth\"\n "+            
            //         input         ,input                ,output
            "    peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n" +              
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";        
        
        str += "rule x4\n" + 
            "when\n" + 
            "    String( this == \"go4\" )\n" +
            "    $name1 : String() from \"darth\"\n "+
            "    $age1 : Integer() from 200\n "+            
            //         input         ,input                ,input
            "    peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n" +
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

        //kbase = SerializationHelper.serializeObject( kbase );

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
        ksession.fireAllRules();
        assertEquals( 5, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        assertTrue( list.contains( "yoda : 300" ));
        assertTrue( list.contains( "luke : 300" ));
        assertTrue( list.contains( "bobba : 300" ));
        
        list.clear();        
        ksession.insert( "go2" );
        ksession.fireAllRules();
        assertEquals( 3, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        assertTrue( list.contains( "yoda : 300" ));
        
        list.clear();
        ksession.insert( "go3" );
        ksession.fireAllRules();   
        assertEquals( 2, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        
        list.clear();        
        ksession.insert( "go4" );
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
            "    peeps($name1; $likes1 : $likes, $age1 : $age )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";        
        
        str += "rule x2\n" + 
            "when\n" + 
            "    String( this == \"go2\" )\n" +
            //         output        ,output                ,output
            "    peeps($name1, \"stilton\"; $age1 : $age )\n" + 
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";

        str += "rule x3\n" + 
            "when\n" + 
            "    String( this == \"go3\" )\n" +
            "    $name1 : String() from \"darth\"\n "+            
            //         input         ,input                ,output
            "    peeps($name1, \"stilton\"; $age1 : $age )\n" +              
            "then\n" + 
            "   list.add( $name1 + \" : \" + $age1 );\n" + 
            "end \n";        
        
        str += "rule x4\n" + 
            "when\n" + 
            "    String( this == \"go4\" )\n" +
            "    $name1 : String() from \"darth\"\n "+
            "    $age1 : Integer() from 200\n "+            
            //         input         ,input                ,input
            "    peeps($name1; $likes : \"stilton\", $age1 : $age )\n" +
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

        //kbase = SerializationHelper.serializeObject( kbase );

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
        ksession.fireAllRules();
        assertEquals( 5, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        assertTrue( list.contains( "yoda : 300" ));
        assertTrue( list.contains( "luke : 300" ));
        assertTrue( list.contains( "bobba : 300" ));
        
        list.clear();        
        ksession.insert( "go2" );
        ksession.fireAllRules();
        assertEquals( 3, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        assertTrue( list.contains( "yoda : 300" ));
        
        list.clear();
        ksession.insert( "go3" );
        ksession.fireAllRules();   
        assertEquals( 2, list.size());
        assertTrue( list.contains( "darth : 100" ));
        assertTrue( list.contains( "darth : 200" ));
        
        list.clear();        
        ksession.insert( "go4" );
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
            "    peeps($p, $name1; $likes1 : $likes, $age1 : $age )\n" + 
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

        //kbase = SerializationHelper.serializeObject( kbase );

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
        ksession.fireAllRules();
        assertEquals( 10, list.size());
        assertSame( p1, list.get( list.indexOf( "darth : 100" ) - 1) );
        assertTrue( list.contains( "darth : 100" ));
        assertSame( p2, list.get( list.indexOf( "darth : 200" ) - 1) );
        assertTrue( list.contains( "darth : 200" ));
        assertSame( p3, list.get( list.indexOf( "yoda : 300" ) - 1) );
        assertTrue( list.contains( "yoda : 300" ));
        assertSame( p4, list.get( list.indexOf( "luke : 300" ) - 1) );        
        assertTrue( list.contains( "luke : 300" ));
        assertSame( p5, list.get( list.indexOf( "bobba : 300" ) - 1) );
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
            "    peeps($name1; $likes1 : $likes, $street1 : $street )\n" + 
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

        //kbase = SerializationHelper.serializeObject( kbase );

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
            "    peeps($name1; $likes1 : $likes, $street : $s )\n" + 
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

        //kbase = SerializationHelper.serializeObject( kbase );

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
        ksession.fireAllRules();
        assertEquals( 1, list.size());
        assertTrue( list.contains( "darth : stilton : s1" ));
        
        list.clear();
        ksession.insert( "s2" );
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
            "    peeps($p; $name : $n1, $likes : \"stilton\", $age : 100 )\n" + 
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

        //kbase = SerializationHelper.serializeObject( kbase );

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
        ksession.fireAllRules();
        assertEquals( 1, list.size());
        assertEquals( p1, list.get(0));     
        
        list.clear();
        ksession.insert( "yoda" );
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
            "    peeps($p; $name : $n1, $likes : \"stilton\", $age : 100 )\n" + 
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

        //kbase = SerializationHelper.serializeObject( kbase );

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
        ksession.fireAllRules();        
        ksession.insert( "yoda" ); // darth exists, so yoda won't get created 
        ksession.fireAllRules();
        assertEquals( 1, list.size());
        assertEquals( p1, list.get(0));          
    }  
    
    @Test
    public void testGeneology() {
        String str = "" +
            "package org.drools.test  \n" +
            "import org.drools.Person \n" +
            "global java.util.List list\n" +
            
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
            "   man( father; ) \n"+
            "   parent( father, child ) \n"+
            "end\n" + 
            
            "query mother( String mother, String child ) \n" +
            "   woman( mother; ) \n"+
            "   parent( mother, child ) \n"+
            "end\n" +             
        
            "query son( String son, String parent ) \n" +
            "   man( son ) \n"+
            "   parent( parent, son ) \n"+
            "end\n" +
        
            "query daughter( String daughter, String parent ) \n" +
            "   woman( daughter ) \n"+
            "   parent( parent, daughter ) \n"+
            "end\n" +
            
            "query siblings( String c1, String c2 ) \n" +
            "   parent( $p, c1 ) \n" +
            "   parent( $p, c2 ) \n"+
            "   eval( c1 != c2 )\n"+
            "end\n";            
    
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                          ResourceType.DRL );
    
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
    
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
    
        //kbase = SerializationHelper.serializeObject( kbase );
    
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list", list );     
        
        ksession.insert( new Man("adam") );
        ksession.insert( new Man("peter") );
        ksession.insert( new Man("paul") );

        ksession.insert( new Woman("eve") );
        ksession.insert( new Woman("mary") );        

        ksession.insert( new Parent( "adam", "peter" ) );
        ksession.insert( new Parent( "eve", "peter" ) );
        ksession.insert( new Parent( "adam", "paul" ) );
        ksession.insert( new Parent( "mary", "paul" ) );

        
        QueryResults results = null;
        
        
        results = ksession.getQueryResults( "woman", new Object[] { new Variable() } );
        for ( QueryResultsRow result : results ) {
            System.out.println( result.get( "name" ) );
        } 
        
        results = ksession.getQueryResults( "man", new Object[] { new Variable() } );
        for ( QueryResultsRow result : results ) {
            System.out.println( result.get( "name" ) );
        }         
        
        results = ksession.getQueryResults( "father", new Object[] { new Variable(),  new Variable()  } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "father( " + result.get( "father" ) + ", " + result.get( "child" ) + " )" );
        }       
        
        System.out.println( );        
        
        results = ksession.getQueryResults( "mother", new Object[] { new Variable(),  new Variable()  } );
        for ( QueryResultsRow result : results ) {
            System.out.println( "mother( " + result.get( "mother" ) + ", " + result.get( "child" ) + " )" );
        }      
        
        System.out.println( );         
        
//        results = ksession.getQueryResults( "siblings", new Object[] { new Variable(),  new Variable()  } );
//        for ( QueryResultsRow result : results ) {
//            System.out.println( "sibling( " + result.get( "c1" ) + ", " + result.get( "c2" ) + " )" );
//        }             
               
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
    
}
