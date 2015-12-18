/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import org.drools.compiler.Address;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.drools.core.InitialFact;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.reteoo.AccumulateNode;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.ExistsNode;
import org.drools.core.reteoo.FromNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.NotNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.QueryElementNode;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Row;
import org.kie.api.runtime.rule.Variable;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.RuleEngineOption;
import org.kie.internal.definition.KnowledgePackage;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.utils.KieHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.drools.compiler.integrationtests.SerializationHelper.getSerialisedStatefulKnowledgeSession;
import static org.kie.api.runtime.rule.Variable.v;

public class BackwardChainingTest extends CommonTestMethodBase {
    
    private static Logger logger = LoggerFactory.getLogger(BackwardChainingTest.class);

    @Test(timeout = 10000)
    public void testQueryPositional() throws Exception {
        String str = "" +
                     "package org.drools.compiler.test  \n" +
                     "import org.drools.compiler.Person \n" +
                     "global java.util.List list\n" +
                     "query peeps( String $name, String $likes, int $age ) \n" +
                     "    Person( $name := name, $likes := likes, $age := age; ) \n" +
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
               "    $name1 : String() from \"darth\";\n " +
               //         input , input      ,output        
               "    ?peeps($name1, \"stilton\", $age1; )\n" +
               "then\n" +
               "   list.add( $name1 + \" : \" + $age1 );\n" +
               "end \n";

        str += "rule x4\n" +
               "when\n" +
               "    String( this == \"go4\" )\n" +
               "    $name1 : String() from \"darth\"\n " +
               "    $age1 : Integer() from 200;\n " +
               //         input , input      ,input        
               "    ?peeps($name1, \"stilton\", $age1; )\n" +
               "then\n" +
               "   list.add( $name1 + \" : \" + $age1 );\n" +
               "end \n";
        
        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );

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
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.insert( p2 );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.insert( p3 );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.insert( p4 );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.insert( p5 );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);

        ksession.insert( "go1" );

        // Make sure we can serialise query state
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);

        ksession.fireAllRules();
        assertEquals( 5,
                      list.size() );
        assertTrue( list.contains( "darth : 100" ) );
        assertTrue( list.contains( "darth : 200" ) );
        assertTrue( list.contains( "yoda : 300" ) );
        assertTrue( list.contains( "luke : 300" ) );
        assertTrue( list.contains( "bobba : 300" ) );

        list.clear();
        ksession.insert( "go2" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "darth : 100" ) );
        assertTrue( list.contains( "darth : 200" ) );
        assertTrue( list.contains( "yoda : 300" ) );

        list.clear();
        ksession.insert( "go3" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "darth : 100" ) );
        assertTrue( list.contains( "darth : 200" ) );

        list.clear();
        ksession.insert( "go4" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "darth : 200" ) );
    }

    @Test(timeout = 10000)
    public void testQueryNamed() throws Exception {
        String str = "" +
                     "package org.drools.compiler.test  \n" +
                     "import org.drools.compiler.Person \n" +
                     "global java.util.List list\n" +
                     "query peeps( String $name, String $likes, int $age ) \n" +
                     "    Person( $name := name, $likes := likes, $age := age ) \n" +
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
               "    $name1 : String() from \"darth\";\n " +
               //         input         ,input                ,output
               "    ?peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n" +
               "then\n" +
               "   list.add( $name1 + \" : \" + $age1 );\n" +
               "end \n";

        str += "rule x4\n" +
               "when\n" +
               "    String( this == \"go4\" )\n" +
               "    $name1 : String() from \"darth\";\n " +
               "    $age1 : Integer() from 200;\n " +
               //         input         ,input                ,input
               "    ?peeps($name1 : $name, $likes : \"stilton\", $age1 : $age )\n" +
               "then\n" +
               "   list.add( $name1 + \" : \" + $age1 );\n" +
               "end \n";

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );

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
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 5,
                      list.size() );
        assertTrue( list.contains( "darth : 100" ) );
        assertTrue( list.contains( "darth : 200" ) );
        assertTrue( list.contains( "yoda : 300" ) );
        assertTrue( list.contains( "luke : 300" ) );
        assertTrue( list.contains( "bobba : 300" ) );

        list.clear();
        ksession.insert( "go2" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "darth : 100" ) );
        assertTrue( list.contains( "darth : 200" ) );
        assertTrue( list.contains( "yoda : 300" ) );

        list.clear();
        ksession.insert( "go3" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "darth : 100" ) );
        assertTrue( list.contains( "darth : 200" ) );

        list.clear();
        ksession.insert( "go4" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "darth : 200" ) );
    }

    @Test(timeout = 10000)
    public void testQueryMixed() throws Exception {
        String str = "" +
                     "package org.drools.compiler.test  \n" +
                     "import org.drools.compiler.Person \n" +
                     "global java.util.List list\n" +
                     "query peeps( String $name, String $likes, int $age ) \n" +
                     "    Person( $name := name, $likes := likes, $age := age; ) \n" +
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
               "    $name1 : String() from \"darth\";\n " +
               //         input         ,input                ,output
               "    ?peeps($name1, \"stilton\"; $age1 : $age )\n" +
               "then\n" +
               "   list.add( $name1 + \" : \" + $age1 );\n" +
               "end \n";

        str += "rule x4\n" +
               "when\n" +
               "    String( this == \"go4\" )\n" +
               "    $name1 : String() from \"darth\"\n " +
               "    $age1 : Integer() from 200;\n " +
               //         input         ,input                ,input
               "    ?peeps($name1; $likes : \"stilton\", $age1 : $age )\n" +
               "then\n" +
               "   list.add( $name1 + \" : \" + $age1 );\n" +
               "end \n";

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );

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
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 5,
                      list.size() );
        assertTrue( list.contains( "darth : 100" ) );
        assertTrue( list.contains( "darth : 200" ) );
        assertTrue( list.contains( "yoda : 300" ) );
        assertTrue( list.contains( "luke : 300" ) );
        assertTrue( list.contains( "bobba : 300" ) );

        list.clear();
        ksession.insert( "go2" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 3,
                      list.size() );
        assertTrue( list.contains( "darth : 100" ) );
        assertTrue( list.contains( "darth : 200" ) );
        assertTrue( list.contains( "yoda : 300" ) );

        list.clear();
        ksession.insert( "go3" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "darth : 100" ) );
        assertTrue( list.contains( "darth : 200" ) );

        list.clear();
        ksession.insert( "go4" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "darth : 200" ) );
    }

    @Test(timeout = 10000)
    public void testQueryPatternBindingAsResult() throws Exception {
        String str = "" +
                     "package org.drools.compiler.test  \n" +
                     "import org.drools.compiler.Person \n" +
                     "global java.util.List list\n" +
                     "query peeps( Person $p, String $name, String $likes, int $age ) \n" +
                     "    $p := Person( $name := name, $likes := likes, $age := age; ) \n" +
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

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );

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
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 10,
                      list.size() );
        assertEquals( p1,
                      list.get( list.indexOf( "darth : 100" ) - 1 ) );
        assertTrue( list.contains( "darth : 100" ) );
        assertEquals( p2,
                      list.get( list.indexOf( "darth : 200" ) - 1 ) );
        assertTrue( list.contains( "darth : 200" ) );
        assertEquals( p3,
                      list.get( list.indexOf( "yoda : 300" ) - 1 ) );
        assertTrue( list.contains( "yoda : 300" ) );
        assertEquals( p4,
                      list.get( list.indexOf( "luke : 300" ) - 1 ) );
        assertTrue( list.contains( "luke : 300" ) );
        assertEquals( p5,
                      list.get( list.indexOf( "bobba : 300" ) - 1 ) );
        assertTrue( list.contains( "bobba : 300" ) );

    }

    @Test(timeout = 10000)
    public void testQueriesWithNestedAcecssorsAllOutputs() throws Exception {
        String str = "" +
                     "package org.drools.compiler.test  \n" +
                     "import org.drools.compiler.Person \n" +
                     "global java.util.List list\n" +
                     "query peeps( String $name, String $likes, String $street ) \n" +
                     "   Person( $name := name, $likes := likes, $street := address.street ) \n" +
                     "end\n";

        str += "rule x1\n" +
               "when\n" +
               "    String( this == \"go1\" )\n" +
               //         output, output,         ,output
               "    ?peeps($name1; $likes1 : $likes, $street1 : $street )\n" +
               "then\n" +
               "   list.add( $name1 + \" : \" + $likes1 + \" : \" +  $street1 );\n" +
               "end \n";

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );

        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        p1.setAddress( new Address( "s1" ) );

        Person p2 = new Person( "yoda",
                                "stilton",
                                300 );
        p2.setAddress( new Address( "s2" ) );

        ksession.insert( p1 );
        ksession.insert( p2 );

        ksession.insert( "go1" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 2,
                      list.size() );
        assertTrue( list.contains( "darth : stilton : s1" ) );
        assertTrue( list.contains( "yoda : stilton : s2" ) );
    }

    @Test(timeout = 10000)
    public void testQueriesWithNestedAcecssorsMixedArgs() throws Exception {
        String str = "" +
                     "package org.drools.compiler.test  \n" +
                     "import org.drools.compiler.Person \n" +
                     "global java.util.List list\n" +
                     "query peeps( String $name, String $likes, String $street ) \n" +
                     "   Person( $name := name, $likes := likes, $street := address.street ) \n" +
                     "end\n";

        str += "rule x1\n" +
               "when\n" +
               "    $s : String()\n" +
               //         output, output,         ,input
               "    ?peeps($name1; $likes1 : $likes, $street : $s )\n" +
               "then\n" +
               "   list.add( $name1 + \" : \" + $likes1 + \" : \" +  $s );\n" +
               "end \n";

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );

        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        p1.setAddress( new Address( "s1" ) );

        Person p2 = new Person( "yoda",
                                "stilton",
                                300 );
        p2.setAddress( new Address( "s2" ) );

        ksession.insert( p1 );
        ksession.insert( p2 );

        ksession.insert( "s1" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "darth : stilton : s1" ) );

        list.clear();
        ksession.insert( "s2" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertTrue( list.contains( "yoda : stilton : s2" ) );
    }

    @Test//(timeout = 10000)
    public void testQueryWithDynamicData() throws Exception {
        String str = "" +
                     "package org.drools.compiler.test  \n" +
                     "import org.drools.compiler.Person \n" +
                     "global java.util.List list\n" +
                     "query peeps( Person $p, String $name, String $likes, int $age ) \n" +
                     "    $p := Person( ) from new Person( $name, $likes, $age ) \n" +
                     "end\n";

        str += "rule x1\n" +
               "when\n" +
               "    $n1 : String( )\n" +
               //     output, input     ,input                 ,input
               "    ?peeps($p; $name : $n1, $likes : \"stilton\", $age : 100 )\n" +
               "then\n" +
               "   list.add( $p );\n" +
               "end \n";

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );

        Person p1 = new Person( "darth",
                                "stilton",
                                100 );

        Person p2 = new Person( "yoda",
                                "stilton",
                                100 );

        ksession.insert( "darth" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true,
                false);
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertEquals( p1,
                      list.get( 0 ) );

        list.clear();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true,
                false);
        ksession.insert( "yoda" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true,
                false);
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertEquals( p2,
                      list.get( 0 ) );
    }

    @Test(timeout = 10000)
    public void testQueryWithDyanmicInsert() throws Exception {
        String str = "" +
                     "package org.drools.compiler.test  \n" +
                     "import org.drools.compiler.Person \n" +
                     "global java.util.List list\n" +
                     "query peeps( Person $p, String $name, String $likes, int $age ) \n" +
                     "    $p := Person( ) from new Person( $name, $likes, $age ) \n" +
                     "end\n";

        str += "rule x1\n" +
               "when\n" +
               "    $n1 : String( )\n" +
               "    not Person( name == 'darth' )\n " +
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

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );

        Person p1 = new Person( "darth",
                                "stilton",
                                100 );

        ksession.insert( "darth" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true,
                false);
        ksession.fireAllRules();
        ksession.insert( "yoda" ); // darth exists, so yoda won't get created 
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true,
                false);
        ksession.fireAllRules();
        assertEquals( 1,
                      list.size() );
        assertEquals( p1,
                      list.get( 0 ) );
    }

    @Test (timeout = 10000)
    public void testQueryWithOr() throws Exception {
        String str = "" +
                     "package org.drools.compiler.test  \n" +

                     "import java.util.List\n" +
                     "import java.util.ArrayList\n" +

                     "global List list\n" +

                     "dialect \"mvel\"\n" +
                     "\n" +

                     "import " + BackwardChainingTest.class.getName() + ".Q\n" +
                     "import " + BackwardChainingTest.class.getName() + ".R\n" +
                     "import " + BackwardChainingTest.class.getName() + ".S\n" +

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

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list",
                            list );

        ksession.fireAllRules();

        QueryResults results = null;

        list.clear();
        results = ksession.getQueryResults( "p",
                                            new Integer[]{0} );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }
        assertEquals( 0,
                      list.size() );

        list.clear();
        results = ksession.getQueryResults( "p",
                                            new Integer[]{1} );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }

        assertEquals( 1,
                      list.size() );
        assertEquals( 1,
                      list.get( 0 ).intValue() );

        list.clear();
        results = ksession.getQueryResults( "p",
                                            new Integer[]{2} );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }
        assertEquals( 1,
                      list.size() );
        assertEquals( 2,
                      list.get( 0 ).intValue() );

        list.clear();
        results = ksession.getQueryResults( "p",
                                            new Integer[]{3} );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }
        assertEquals( 1,
                      list.size() );
        assertEquals( 3,
                      list.get( 0 ).intValue() );

        list.clear();
        results = ksession.getQueryResults( "p",
                                            new Integer[]{4} );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }
        assertEquals( 0,
                      list.size() );

        list.clear();
        results = ksession.getQueryResults( "p",
                                            new Integer[]{5} );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }
        assertEquals( 0,
                      list.size() );

        list.clear();
        results = ksession.getQueryResults( "p",
                                            new Integer[]{6} );
        for ( QueryResultsRow result : results ) {
            list.add( (Integer) result.get( "x" ) );
        }
        assertEquals( 2,
                      list.size() );
        assertEquals( 6,
                      list.get( 0 ).intValue() );
        assertEquals( 6,
                      list.get( 1 ).intValue() );
    }

    @Test(timeout = 10000)
    public void testGeneology() throws Exception {
        // from http://kti.mff.cuni.cz/~bartak/prolog/genealogy.html

        String str = "" +
                     "package org.drools.compiler.test2  \n" +
                     "global java.util.List list\n" +
                     "dialect \"mvel\"\n" +

                     "query man( String name ) \n" +
                     "   " + BackwardChainingTest.class.getName() + ".Man( name := name ) \n" +
                     "end\n" +

                     "query woman( String name ) \n" +
                     "   " + BackwardChainingTest.class.getName() + ".Woman( name := name ) \n" +
                     "end\n" +

                     "query parent( String parent, String child ) \n" +
                     "   " + BackwardChainingTest.class.getName() + ".Parent( parent := parent, child := child ) \n" +
                     "end\n" +

                     "query father( String father, String child ) \n" +
                     "   ?man( father; ) \n" +
                     "   ?parent( father, child; ) \n" +
                     "end\n" +

                     "query mother( String mother, String child ) \n" +
                     "   ?woman( mother; ) \n" +
                     "   ?parent( mother, child; ) \n" +
                     "end\n" +

                     "query son( String son, String parent ) \n" +
                     "   ?man( son; ) \n" +
                     "   ?parent( parent, son; ) \n" +
                     "end\n" +

                     "query daughter( String daughter, String parent ) \n" +
                     "   ?woman( daughter; ) \n" +
                     "   ?parent( parent, daughter; ) \n" +
                     "end\n" +

                     "query siblings( String c1, String c2 ) \n" +
                     "   ?parent( $p, c1; ) \n" +
                     "   ?parent( $p, c2; ) \n" +
                     "   eval( !c1.equals( c2 ) )\n" +
                     "end\n" +

                     "query fullSiblings( String c1, String c2 )\n" +
                     "   ?parent( $p1, c1; ) ?parent( $p1, c2; )\n" +
                     "   ?parent( $p2, c1; ) ?parent( $p2, c2; )\n" +
                     "   eval( !c1.equals( c2 ) && !$p1.equals( $p2 )  )\n" +
                     "end\n" +

                     "query fullSiblings2( String c1, String c2 )\n" +
                     "   ?father( $p1, c1; ) ?father( $p1, c2; )\n" +
                     "   ?mother( $p2, c1; ) ?mother( $p2, c2; )\n" +
                     "   eval( !c1.equals( c2 ) )\n" +
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

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );

        // grand parents
        ksession.insert( new Man( "john" ) );
        ksession.insert( new Woman( "janet" ) );

        // parent
        ksession.insert( new Man( "adam" ) );
        ksession.insert( new Parent( "john",
                                     "adam" ) );
        ksession.insert( new Parent( "janet",
                                     "adam" ) );

        ksession.insert( new Man( "stan" ) );
        ksession.insert( new Parent( "john",
                                     "stan" ) );
        ksession.insert( new Parent( "janet",
                                     "stan" ) );

        // grand parents
        ksession.insert( new Man( "carl" ) );
        ksession.insert( new Woman( "tina" ) );
        // 
        // parent         
        ksession.insert( new Woman( "eve" ) );
        ksession.insert( new Parent( "carl",
                                     "eve" ) );
        ksession.insert( new Parent( "tina",
                                     "eve" ) );
        //
        // parent         
        ksession.insert( new Woman( "mary" ) );
        ksession.insert( new Parent( "carl",
                                     "mary" ) );
        ksession.insert( new Parent( "tina",
                                     "mary" ) );

        ksession.insert( new Man( "peter" ) );
        ksession.insert( new Parent( "adam",
                                     "peter" ) );
        ksession.insert( new Parent( "eve",
                                     "peter" ) );

        ksession.insert( new Man( "paul" ) );
        ksession.insert( new Parent( "adam",
                                     "paul" ) );
        ksession.insert( new Parent( "mary",
                                     "paul" ) );

        ksession.insert( new Woman( "jill" ) );
        ksession.insert( new Parent( "adam",
                                     "jill" ) );
        ksession.insert( new Parent( "eve",
                                     "jill" ) );

        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);

        QueryResults results = null;

        //System.out.println("woman");         
        list.clear();
        results = ksession.getQueryResults( "woman",
                                            new Object[]{v} );
        for ( QueryResultsRow result : results ) {
            list.add( (String) result.get( "name" ) );
        }
        assertEquals( 5,
                      list.size() );
        assertContains( new String[]{"janet", "mary", "tina", "eve", "jill"},
                        list );

        list.clear();
        //System.out.println("\nman");        
        results = ksession.getQueryResults( "man",
                                            new Object[]{v} );
        for ( QueryResultsRow result : results ) {
            list.add( (String) result.get( "name" ) );
        }
        assertEquals( 6,
                      list.size() );
        assertContains( new String[]{"stan", "john", "peter", "carl", "adam", "paul"},
                        list );

        list.clear();
        //System.out.println("\nfather");
        results = ksession.getQueryResults( "father",
                                            new Object[]{v, v} );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "father" ) + ", " + result.get( "child" ) );
        }
        assertEquals( 7,
                      list.size() );
        assertContains( new String[]{"john, adam", "john, stan",
                                "carl, eve", "carl, mary",
                                "adam, peter", "adam, paul",
                                "adam, jill"},
                        list );

        list.clear();
        //System.out.println("\nmother");
        results = ksession.getQueryResults( "mother",
                                            new Object[]{v, v} );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "mother" ) + ", " + result.get( "child" ) );
        }
        assertEquals( 7,
                      list.size() );
        assertContains( new String[]{"janet, adam", "janet, stan",
                                "mary, paul", "tina, eve",
                                "tina, mary", "eve, peter",
                                "eve, jill"},
                        list );

        list.clear();
        //System.out.println("\nson");
        results = ksession.getQueryResults( "son",
                                            new Object[]{v, v} );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "son" ) + ", " + result.get( "parent" ) );
        }
        assertEquals( 8,
                      list.size() );
        assertContains( new String[]{"stan, john", "stan, janet",
                                "peter, adam", "peter, eve",
                                "adam, john", "adam, janet",
                                "paul, mary", "paul, adam"},
                        list );

        list.clear();
        //System.out.println("\ndaughter");
        results = ksession.getQueryResults( "daughter",
                                            new Object[]{v, v} );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "daughter" ) + ", " + result.get( "parent" ) );
        }
        assertEquals( 6,
                      list.size() );
        assertContains( new String[]{"mary, carl", "mary, tina",
                                "eve, carl", "eve, tina",
                                "jill, adam", "jill, eve"},
                        list );

        list.clear();
        //System.out.println("\nsiblings");
        results = ksession.getQueryResults( "siblings",
                                            new Object[]{v, v} );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "c1" ) + ", " + result.get( "c2" ) );
        }
        assertEquals( 16,
                      list.size() );
        assertContains( new String[]{"eve, mary", "mary, eve",
                                "adam, stan", "stan, adam",
                                "adam, stan", "stan, adam",
                                "peter, paul", "peter, jill",
                                "paul, peter", "paul, jill",
                                "jill, peter", "jill, paul",
                                "peter, jill", "jill, peter",
                                "eve, mary", "mary, eve"},
                        list );

        list.clear();
        //System.out.println("\nfullSiblings");
        results = ksession.getQueryResults( "fullSiblings",
                                            new Object[]{v, v} );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "c1" ) + ", " + result.get( "c2" ) );
        }
        assertEquals( 12,
                      list.size() );
        assertContains( new String[]{"eve, mary", "mary, eve",
                                "adam, stan", "stan, adam",
                                "adam, stan", "stan, adam",
                                "peter, jill", "jill, peter",
                                "peter, jill", "jill, peter",
                                "eve, mary", "mary, eve"},
                        list );

        list.clear();
        //System.out.println("\nfullSiblings2");
        results = ksession.getQueryResults( "fullSiblings",
                                            new Object[]{v, v} );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "c1" ) + ", " + result.get( "c2" ) );
        }
        assertEquals( 12,
                      list.size() );
        assertContains( new String[]{"eve, mary", "mary, eve",
                                "adam, stan", "stan, adam",
                                "adam, stan", "stan, adam",
                                "peter, jill", "jill, peter",
                                "peter, jill", "jill, peter",
                                "eve, mary", "mary, eve"},
                        list );

        list.clear();
        //System.out.println("\nuncle");
        results = ksession.getQueryResults( "uncle",
                                            new Object[]{v, v} );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "uncle" ) + ", " + result.get( "n" ) );
        }
        assertEquals( 6,
                      list.size() );
        assertContains( new String[]{"stan, peter",
                                "stan, paul",
                                "stan, jill",
                                "stan, peter",
                                "stan, paul",
                                "stan, jill"},
                        list );

        list.clear();
        //System.out.println("\naunt");
        results = ksession.getQueryResults( "aunt",
                                            new Object[]{v, v} );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "aunt" ) + ", " + result.get( "n" ) );
        }
        assertEquals( 6,
                      list.size() );
        assertContains( new String[]{"mary, peter",
                                "mary, jill",
                                "mary, peter",
                                "mary, jill",
                                "eve, paul",
                                "eve, paul"},
                        list );

        list.clear();
        //System.out.println("\ngrantParents");
        results = ksession.getQueryResults( "grantParents",
                                            new Object[]{v, v} );
        for ( QueryResultsRow result : results ) {
            list.add( result.get( "gp" ) + ", " + result.get( "gc" ) );
        }
        assertEquals( 12,
                      list.size() );
        assertContains( new String[]{"carl, peter",
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
                                "tina, paul",},
                        list );
    }

    @Test //(timeout = 10000)
    public void testNaniSearchs() throws Exception {
        // http://www.amzi.com/AdventureInProlog/advtop.php

        String str = "" +
                     "package org.drools.compiler.test  \n" +

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
                     "end\n" +
                     "\n" +
                     "declare Here\n" +
                     "   place : String \n" +
                     "end\n" +
                     "\n" +

                     "query whereFood( String x, String y ) \n" +
                     "    ( Location(x, y;) and\n" +
                     "      Edible(x;) ) " +
                     "     or \n " +
                     "    ( Location(z, y;) and ?whereFood(x, z;) )\n" +
                     "end\n" +

                     "query connect( String x, String y ) \n" +
                     "    Door(x, y;)\n" +
                     "    or \n" +
                     "    Door(y, x;)\n" +
                     "end\n" +
                     "\n" +
                     "query isContainedIn( String x, String y ) \n" +
                     "    Location(x, y;)\n" +
                     "    or \n" +
                     "    ( Location(z, y;) and ?isContainedIn(x, z;) )\n" +
                     "end\n" +
                     "\n" +
                     "query look(String place, List things, List food, List exits ) \n" +
                     "    Here(place;)\n" +
                     "    things := List() from accumulate( Location(thing, place;),\n" +
                     "                                      collectList( thing ) )\n" +
                     "    food := List() from accumulate( ?whereFood(thing, place;) ," +
                     "                                    collectList( thing ) )\n" +
                     "    exits := List() from accumulate( ?connect(place, exit;),\n" +
                     "                                    collectList( exit ) )\n" +
                     "end\n" +
                     "\n" +
                     "rule reactiveLook when\n" +
                     "    Here( place : place) \n" +
                     "    ?look(place, things, food, exits;)\n" +
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
                     "";

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ksession.setGlobal( "list",
                            list );

        QueryResults results = null;
        ksession.fireAllRules();

        ksession.insert( "go1" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true);
        ksession.fireAllRules();

        Map<String, Object> map = (Map) list.get( 0 );
        assertEquals( "kitchen",
                      map.get( "place" ) );
        List<String> items = (List<String>) map.get( "things" );
        assertEquals( 3,
                      items.size() );
        assertContains( new String[]{"apple", "broccoli", "crackers"},
                        items );

        items = (List<String>) map.get( "food" );
        assertEquals( 2,
                      items.size() );
        assertContains( new String[]{"apple", "crackers"},
                        items );

        items = (List<String>) map.get( "exits" );
        assertEquals( 3,
                      items.size() );
        assertContains( new String[]{"office", "cellar", "dining room"},
                        items );

        ksession.insert( "go2" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession,
                true,
                false);
        ksession.fireAllRules();

        map = (Map) list.get( 1 );
        assertEquals( "office",
                      map.get( "place" ) );
        items = (List<String>) map.get( "things" );
        assertEquals( 2,
                      items.size() );
        assertContains( new String[]{"computer", "desk",},
                        items );

        items = (List<String>) map.get( "food" );
        assertEquals( 1,
                      items.size() );
        assertContains( new String[]{"apple"},
                        items ); // notice the apple is on the desk in the office

        items = (List<String>) map.get( "exits" );
        assertEquals( 2,
                      items.size() );
        assertContains( new String[]{"hall", "kitchen"},
                        items );

        results = ksession.getQueryResults( "isContainedIn",
                                            new Object[]{"key", "office"} );
        assertEquals( 1,
                      results.size() );
        QueryResultsRow result = results.iterator().next();
        assertEquals( "key",
                      result.get( "x" ) );
        assertEquals( "office",
                      result.get( "y" ) );

        results = ksession.getQueryResults( "isContainedIn",
                                            new Object[]{"key", Variable.v} );
        List<List<String>> l = new ArrayList<List<String>>();
        for ( QueryResultsRow r : results ) {
            l.add( Arrays.asList( new String[]{(String) r.get( "x" ), (String) r.get( "y" )} ) );
        }
        assertEquals( 3,
                      results.size() );
        assertContains( Arrays.asList( new String[]{"key", "desk"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"key", "office"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"key", "envelope"} ),
                        l );

        results = ksession.getQueryResults( "isContainedIn",
                                            new Object[]{Variable.v, "office"} );
        l = new ArrayList<List<String>>();
        for ( QueryResultsRow r : results ) {
            l.add( Arrays.asList( new String[]{(String) r.get( "x" ), (String) r.get( "y" )} ) );
        }

        assertEquals( 6,
                      results.size() );
        assertContains( Arrays.asList( new String[]{"desk", "office"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"computer", "office"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"apple", "office"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"envelope", "office"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"flashlight", "office"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"key", "office"} ),
                        l );

        results = ksession.getQueryResults( "isContainedIn",
                                            new Object[]{Variable.v, Variable.v} );
        l = new ArrayList<List<String>>();
        for ( QueryResultsRow r : results ) {
            l.add( Arrays.asList( new String[]{(String) r.get( "x" ), (String) r.get( "y" )} ) );
        }
        assertEquals( 17,
                      results.size() );
        assertContains( Arrays.asList( new String[]{"apple", "kitchen"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"apple", "desk"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"envelope", "desk"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"desk", "office"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"computer", "office"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"washing machine", "cellar"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"key", "envelope"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"broccoli", "kitchen"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"nani", "washing machine"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"crackers", "kitchen"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"flashlight", "desk"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"nani", "cellar"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"apple", "office"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"envelope", "office"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"flashlight", "office"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"key", "office"} ),
                        l );
        assertContains( Arrays.asList( new String[]{"key", "desk"} ),
                        l );
    }

    @Test(timeout = 10000)
    public void testSubNetworksAndQueries() throws Exception {
        if( CommonTestMethodBase.phreak == RuleEngineOption.RETEOO ) {
            return;  //Disbaled due to phreak, as tests is order specific
        }
        String str = "" +
                     "package org.drools.compiler.test  \n" +

                     "import java.util.List\n" +
                     "import java.util.ArrayList\n" +

                     "import java.util.Map\n" +
                     "import java.util.HashMap\n" +

                     "global List list\n" +

                     "dialect \"mvel\"\n" +
                     "\n" +
                     "declare Location\n" +
                     "    thing : String \n" +
                     "    location : String \n" +
                     "end" +
                     "\n" +
                     "declare Edible\n" +
                     "   thing : String\n" +
                     "end" +
                     "\n" +
                     "query whereFood( String x, String y ) \n" +
                     "    Location(x, y;) Edible(x;) \n" +
                     "end\n" +
                     "\n" +
                     "query look(String place, List food ) \n" +
                     "    $s : String() // just here to give a OTN lookup point\n" +
                     "    food := List() from accumulate( whereFood(thing, place;) ," +
                     "                                    collectList( thing ) )\n" +
                     "    exists( whereFood(thing, place;) )\n" +
                     "    not( whereFood(thing, place;) and\n " +
                     "         String( this == $s ) from thing )\n" +
                     "end\n" +
                     "\n" +
                     "rule init when\n" +
                     "then\n" +
                     "        \n" +
                     "        insert( new Location(\"apple\", \"kitchen\") );\n" +
                     "        insert( new Location(\"crackers\", \"kitchen\") );\n" +
                     "        insert( new Location(\"broccoli\", \"kitchen\") );\n" +
                     "        insert( new Location(\"computer\", \"office\") );\n" +

                     "        insert( new Edible(\"apple\") );\n" +
                     "        insert( new Edible(\"crackers\") );\n" +
                     "end\n" +
                     "";

        logger.debug( str );

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        

        // Get the accumulate node, so we can test it's memory later
        // now check beta memory was correctly cleared
        List<ObjectTypeNode> nodes = ((KnowledgeBaseImpl) kbase).getRete().getObjectTypeNodes();
        ObjectTypeNode node = null;
        for ( ObjectTypeNode n : nodes ) {
            if ( ((ClassObjectType) n.getObjectType()).getClassType() == String.class ) {
                node = n;
                break;
            }
        }

        BetaNode stringBetaNode = (BetaNode) node.getSinkPropagator().getSinks()[0];
        QueryElementNode queryElementNode1 = (QueryElementNode) stringBetaNode.getSinkPropagator().getSinks()[0];
        RightInputAdapterNode riaNode1 = (RightInputAdapterNode) queryElementNode1.getSinkPropagator().getSinks()[0];
        AccumulateNode accNode = (AccumulateNode) riaNode1.getSinkPropagator().getSinks()[0];

        QueryElementNode queryElementNode2 = (QueryElementNode) accNode.getSinkPropagator().getSinks()[0];
        RightInputAdapterNode riaNode2 = (RightInputAdapterNode) queryElementNode2.getSinkPropagator().getSinks()[0];
        ExistsNode existsNode = (ExistsNode) riaNode2.getSinkPropagator().getSinks()[0];

        QueryElementNode queryElementNode3 = (QueryElementNode) existsNode.getSinkPropagator().getSinks()[0];
        FromNode fromNode = (FromNode) queryElementNode3.getSinkPropagator().getSinks()[0];
        RightInputAdapterNode riaNode3 = (RightInputAdapterNode) fromNode.getSinkPropagator().getSinks()[0];
        NotNode notNode = (NotNode) riaNode3.getSinkPropagator().getSinks()[0];

        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        InternalWorkingMemory wm = ((StatefulKnowledgeSessionImpl) ksession);
        AccumulateMemory accMemory = (AccumulateMemory) wm.getNodeMemory( accNode );
        BetaMemory existsMemory = (BetaMemory) wm.getNodeMemory( existsNode );
        FromMemory fromMemory = (FromMemory) wm.getNodeMemory( fromNode );
        BetaMemory notMemory = (BetaMemory) wm.getNodeMemory( notNode );

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ksession.setGlobal( "list",
                            list );
        FactHandle fh = ksession.insert( "bread" );

        ksession.fireAllRules();

        final List food = new ArrayList();

        QueryResults results = null;

        // Execute normal query and check no subnetwork tuples are left behind
        results = ksession.getQueryResults( "look",
                                            new Object[]{"kitchen", Variable.v} );
        assertEquals( 1,
                      results.size() );

        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            food.addAll( (Collection) row.get( "food" ) );
            logger.debug( row.get( "food" ).toString() );
        }
        assertEquals( 2,
                      food.size() );
        assertContains( new String[]{"crackers", "apple"},
                        food );

        assertEquals( 0,
                      accMemory.getBetaMemory().getRightTupleMemory().size() );
        assertEquals( 0,
                      existsMemory.getRightTupleMemory().size() );
        assertEquals( 0,
                      fromMemory.getBetaMemory().getLeftTupleMemory().size() );
        assertEquals( 0,
                      notMemory.getRightTupleMemory().size() );

        // Now execute an open query and ensure the memory is left populated
        food.clear();
        final List foodUpdated = new ArrayList();
        LiveQuery query = ksession.openLiveQuery( "look",
                                                  new Object[]{"kitchen", Variable.v},
                                                  new ViewChangedEventListener() {

                                                      public void rowUpdated(Row row) {
                                                          foodUpdated.addAll( (Collection) row.get( "food" ) );
                                                      }

                                                      public void rowDeleted(Row row) {
                                                      }

                                                      public void rowInserted(Row row) {
                                                          food.addAll( (Collection) row.get( "food" ) );
                                                      }
                                                  } );
        assertEquals( 2,
                      food.size() );
        assertContains( new String[]{"crackers", "apple"},
                        food );

        assertEquals( 2,
                      accMemory.getBetaMemory().getRightTupleMemory().size() );
        assertEquals( 2,
                      existsMemory.getRightTupleMemory().size() );
        assertEquals( 2,
                      fromMemory.getBetaMemory().getLeftTupleMemory().size() );
        assertEquals( 0,
                      notMemory.getRightTupleMemory().size() );

        food.clear();
        // Now try again, make sure it only delete's it's own tuples
        results = ksession.getQueryResults( "look",
                                            new Object[]{"kitchen", Variable.v} );
        assertEquals( 1,
                      results.size() );

        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            food.addAll( (Collection) row.get( "food" ) );
            logger.debug( row.get( "food" ).toString() );
        }
        assertEquals( 2,
                      food.size() );
        assertContains( new String[]{"crackers", "apple"},
                        food );

        assertEquals( 2,
                      accMemory.getBetaMemory().getRightTupleMemory().size() );
        assertEquals( 2,
                      existsMemory.getRightTupleMemory().size() );
        assertEquals( 2,
                      fromMemory.getBetaMemory().getLeftTupleMemory().size() );
        assertEquals( 0,
                      notMemory.getRightTupleMemory().size() );
        food.clear();

        // do an update and check it's  still memory size 2
        // however this time the food should be empty, as 'crackers' now blocks the not.
        ksession.update( fh,
                         "crackers" );
        ksession.fireAllRules();

        assertEquals( 2,
                      accMemory.getBetaMemory().getRightTupleMemory().size() );
        assertEquals( 2,
                      existsMemory.getRightTupleMemory().size() );
        assertEquals( 2,
                      fromMemory.getBetaMemory().getLeftTupleMemory().size() );
        assertEquals( 1,
                      notMemory.getRightTupleMemory().size() );

        assertEquals( 0, foodUpdated.size() );

        // do an update and check it's  still memory size 2
        // this time
        ksession.update( fh,
                         "oranges" );
        ksession.fireAllRules();

        assertEquals( 2,
                      accMemory.getBetaMemory().getRightTupleMemory().size() );
        assertEquals( 2,
                      existsMemory.getRightTupleMemory().size() );
        assertEquals( 2,
                      fromMemory.getBetaMemory().getLeftTupleMemory().size() );
        assertEquals( 0,
                      notMemory.getRightTupleMemory().size() );

        assertEquals( 2,
                      food.size() );
        assertContains( new String[]{"crackers", "apple"},
                        food );

        // Close the open
        query.close();
        assertEquals( 0,
                      accMemory.getBetaMemory().getRightTupleMemory().size() );
        assertEquals( 0,
                      existsMemory.getRightTupleMemory().size() );
        assertEquals( 0,
                      fromMemory.getBetaMemory().getLeftTupleMemory().size() );
        assertEquals( 0,
                      notMemory.getRightTupleMemory().size() );
    }

    @Test(timeout = 10000)
    public void testDynamicRulesWithSharing() throws IOException,
                                             ClassNotFoundException {
        String str = "" +
                     "package org.drools.compiler.test1  \n" +
                     "\n" +
                     "declare Location\n" +
                     "    thing : String \n" +
                     "    location : String \n" +
                     "end" +
                     "\n" +
                     "declare Edible\n" +
                     "   thing : String\n" +
                     "end" +
                     "\n" +
                     "query whereFood( String x, String y ) \n" +
                     "    Location(x, y;) Edible(x;) \n" +
                     "end\n" +
                     "\n" +         
                     "rule init when\n" +
                     "then\n" +
                     "        \n" +
                     "        insert( new Location(\"apple\", \"kitchen\") );\n" +
                     "        insert( new Location(\"crackers\", \"kitchen\") );\n" +
                     "        insert( new Location(\"broccoli\", \"kitchen\") );\n" +
                     "        insert( new Location(\"computer\", \"office\") );\n" +

                     "        insert( new Edible(\"apple\") );\n" +
                     "        insert( new Edible(\"crackers\") );\n" +
                     "end\n" +
                     "";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }           

        str = "" +
                "package org.drools.compiler.test2  \n" +
                
                "import org.drools.compiler.test1.*\n" +
                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "import java.util.Map\n" +
                "import java.util.HashMap\n" +

                "global List list\n" +
                "\n" +
                "rule look2 when\n" +
                "     $place : String() // just here to give a OTN lookup point\n" +
                "     whereFood(thing, $place;)\n" +
                "then\n" +
                "      list.add( \"2:\" + thing );\n" +
                "end\n";  
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );     
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }        
        
        str = "" +
                "package org.drools.compiler.test3  \n" +

                "import org.drools.compiler.test1.*\n" +
                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "import java.util.Map\n" +
                "import java.util.HashMap\n" +

                "global List list\n" +
                "\n" +
                "rule look3 when\n" +
                "     $place : String() // just here to give a OTN lookup point\n" +
                "     whereFood(thing, $place;)\n" +
                "then\n" +
                "      list.add( \"3:\" + thing );\n" +
                "end\n";
        
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );        
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        
        str = "" +
                "package org.drools.compiler.test4  \n" +

                "import org.drools.compiler.test1.*\n" +
                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "import java.util.Map\n" +
                "import java.util.HashMap\n" +

                "global List list\n" +
                "\n" +
                "rule look4 when\n" +
                "     $place : String() // just here to give a OTN lookup point\n" +
                "     whereFood(thing, $place;)\n" +
                "then\n" +
                "      list.add( \"4:\" + thing );\n" +
                "end\n";
        
        kbuilder.add( ResourceFactory.newByteArrayResource( str.getBytes() ),
                      ResourceType.DRL );        
        
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }        

        Map<String, KnowledgePackage> pkgs = new HashMap<String, KnowledgePackage>();
        for ( KnowledgePackage pkg : kbuilder.getKnowledgePackages() ) {
            pkgs.put(  pkg.getName(), pkg );
        }
        
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(  Arrays.asList( new KnowledgePackage[] { pkgs.get( "org.drools.compiler.test1" ), pkgs.get( "org.drools.compiler.test2" ) } ) );

        kbase = SerializationHelper.serializeObject( kbase );
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);

        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        ksession.setGlobal( "list",
                            list );
        FactHandle fh = ksession.insert( "kitchen" );

        ksession.fireAllRules();
        
        assertEquals( 2, list.size() );
        assertContains( new String[]{"2:crackers", "2:apple"},
                        list ); 
        
        list.clear();
        kbase.addKnowledgePackages(  Arrays.asList( new KnowledgePackage[] { pkgs.get( "org.drools.compiler.test3" ) } ) );
        
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        assertContains( new String[]{"3:crackers", "3:apple"},
                        list );

        list.clear();
        kbase.addKnowledgePackages(  Arrays.asList( new KnowledgePackage[] { pkgs.get( "org.drools.compiler.test4" ) } ) );
        
        ksession.fireAllRules();
        assertEquals( 2, list.size() );
        assertContains( new String[]{"4:crackers", "4:apple"},
                        list );        
    }

    @Test //(timeout = 10000)
    public void testOpenBackwardChain() throws Exception {
        // http://www.amzi.com/AdventureInProlog/advtop.php

        String str = "" +
                     "package org.drools.compiler.test  \n" +

                     "import java.util.List\n" +
                     "import java.util.ArrayList\n" +
                     "import org.drools.compiler.Person\n" +

                     "global List list\n" +

                     "dialect \"mvel\"\n" +

                     "declare Location\n" +
                     "    thing : String \n" +
                     "    location : String \n" +
                     "end" +
                     "\n" +
                     "query isContainedIn( String x, String y ) \n" +
                     "    Location(x, y;)\n" +
                     "    or \n" +
                     "    ( Location(z, y;) and isContainedIn(x, z;) )\n" +
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
                     //"        insert( new Location(\"apple\", \"kitchen\") );\n" +
                     "        insert( new Location(\"desk\", \"office\") );\n" +
                     //"        insert( new Location(\"flashlight\", \"desk\") );\n" +
                     "        insert( new Location(\"envelope\", \"desk\") );\n" +
                     "        insert( new Location(\"key\", \"envelope\") );\n" +
                     //"        insert( new Location(\"washing machine\", \"cellar\") );\n" +
                     //"        insert( new Location(\"nani\", \"washing machine\") );\n" +
                     //"        insert( new Location(\"broccoli\", \"kitchen\") );\n" +
                     //"        insert( new Location(\"crackers\", \"kitchen\") );\n" +
                     //"        insert( new Location(\"computer\", \"office\") );\n" +
                     "end\n" +
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
                     "\n" +
                     "rule go7 when \n" +
                     "    String( this == 'go7') \n" +
                     "    $p : Person( likes == 'lamp' ) \n" +
                     "then\n" +
                     "    list.add( rule.getName() ); \n" +
                     "    modify( $p ) { likes = 'key' };\n" +
                     "end\n" +
                     "\n";

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );

        List<String> list = new ArrayList<String>();
        ksession.setGlobal( "list",
                            list );

        QueryResults results = null;

        Person p = new Person();
        p.setLikes( "lamp" );
        FactHandle handle = ksession.insert( p );
        //ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();
        assertEquals( "not blah",
                      list.get( 0 ) );

        list.clear();

        InternalFactHandle fh = (InternalFactHandle) ksession.insert( "go1" );
        //ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        fh = getFactHandle( fh,
                            ksession );
        ksession.retract( fh );
        assertEquals( "go1",
                      list.get( 0 ) );
        assertEquals( "exists blah",
                      list.get( 1 ) );

        fh = (InternalFactHandle) ksession.insert( "go2" );
        //ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        fh = getFactHandle( fh,
                            ksession );
        ksession.retract( fh );
        assertEquals( "go2",
                      list.get( 2 ) );
        assertEquals( "not blah",
                      list.get( 3 ) );

        fh = (InternalFactHandle) ksession.insert( "go3" );
        logger.trace( "--------------" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        fh = getFactHandle( fh,
                            ksession );
        ksession.retract( fh );
        assertEquals( "go3",
                      list.get( 4 ) );
        assertEquals( "exists blah",
                      list.get( 5 ) );

        fh = (InternalFactHandle) ksession.insert( "go4" );
        ksession.fireAllRules();

        fh = getFactHandle( fh,
                            ksession );
        ksession.retract( fh );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        assertEquals( "go4",
                      list.get( 6 ) );
        assertEquals( "not blah",
                      list.get( 7 ) );

        fh = (InternalFactHandle) ksession.insert( "go5" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        fh = getFactHandle( fh,
                            ksession );
        ksession.retract( fh );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        assertEquals( "go5",
                      list.get( 8 ) );
        assertEquals( "exists blah",
                      list.get( 9 ) );

        // This simulates a modify of the root DroolsQuery object, but first we break it
        fh = (InternalFactHandle) ksession.insert( "go6" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        fh = getFactHandle( fh,
                            ksession );
        ksession.retract( fh );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        assertEquals( "go6",
                      list.get( 10 ) );
        assertEquals( "not blah",
                      list.get( 11 ) );

        // now fix it
        fh = (InternalFactHandle) ksession.insert( "go7" );
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
        ksession.fireAllRules();

        fh = getFactHandle( fh,
                            ksession );
        ksession.retract( fh );
        ksession = getSerialisedStatefulKnowledgeSession( ksession, true );
        assertEquals( "go7",
                      list.get( 12 ) );
        assertEquals( "exists blah",
                      list.get( 13 ) );
    }

    @Test(timeout = 10000)
    public void testCompile() throws IOException, ClassNotFoundException {
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

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( drl ) );
    }

    @Test(timeout = 10000)
    public void testInsertionOrderTwo() throws Exception {
        String str = "" +
                "package org.drools.compiler.test \n" +
                "import java.util.List \n" +
                "global List list \n" +
                "declare Thing \n" +
                "    thing : String @key \n" +
                "end \n" +
                "declare Edible extends Thing \n" +
                "end \n" +
                "declare Location extends Thing \n" +
                "    location : String  @key \n" +
                "end \n" +
                "declare Here \n" +
                "    place : String \n" +
                "end \n" +
                "rule kickOff \n" +
                "when \n" +
                "    Integer( $i: intValue ) \n" +
                "then \n" +
                "    switch( $i ){ \n";

        String[] facts = new String[]{"new Edible( 'peach' )", "new Location( 'peach', 'table' )", "new Here( 'table' )"};
        int f = 0;
        for ( int i = 0; i < facts.length; i++ ) {
            for ( int j = 0; j < facts.length; j++ ) {
                for ( int k = 0; k < facts.length; k++ ) {
                    // use a Set to make sure we only include 3 unique values
                    Set<String> set = new HashSet<String>();
                    set.add( facts[i] );
                    set.add( facts[j] );
                    set.add( facts[k] );
                    if ( set.size() == 3 ) {
                        str +=
                                "    case " + f++ + ": \n" +
                                        //"        System.out.println( \"s) \"+" + (f-1) + ");\n" +
                                        "        insert( " + facts[i] + " ); \n" +
                                        "        insert( " + facts[j] + " ); \n" +
                                        "        insert( " + facts[k] + " ); \n" +
                                        "        break; \n";
                    }
                }
            }
        }

        facts = new String[]{"new Edible( 'peach' )", "new Location( 'table', 'office' )", "new Location( 'peach', 'table' )", "new Here( 'office' )"};
        int h = f;
        for ( int i = 0; i < facts.length; i++ ) {
            for ( int j = 0; j < facts.length; j++ ) {
                for ( int k = 0; k < facts.length; k++ ) {
                    for ( int l = 0; l < facts.length; l++ ) {
                        // use a Set to make sure we only include 3 unique values
                        Set<String> set = new HashSet<String>();
                        set.add( facts[i] );
                        set.add( facts[j] );
                        set.add( facts[k] );
                        set.add( facts[l] );
                        if ( set.size() == 4 ) {
                            str +=
                                    "    case " + h++ + ": \n" +
                                            //"        System.out.println( \"s) \"+" + (h-1) + ");\n" +
                                            "        insert( " + facts[i] + " ); \n" +
                                            "        insert( " + facts[j] + " ); \n" +
                                            "        insert( " + facts[k] + " ); \n" +
                                            "        insert( " + facts[l] + " ); \n" +
                                            "        break; \n";
                        }
                    }
                }
            }
        }

        str +=
                "    } \n" +
                        "end \n" +
                        "\n" +
                        "query whereFood( String x, String y ) \n" +
                        "    ( Location(x, y;) and \n" +
                        "    Edible(x;) ) \n " +
                        "    or  \n" +
                        "    ( Location(z, y;) and whereFood(x, z;) ) \n" +
                        "end " +
                        "query look(String place, List things, List food)  \n" +
                        "    Here(place;) \n" +
                        "    things := List() from accumulate( Location(thing, place;), \n" +
                        "                                      collectList( thing ) ) \n" +
                        "    food := List() from accumulate( whereFood(thing, place;), \n" +
                        "                                    collectList( thing ) ) \n" +
                        "end \n" +
                        "rule reactiveLook \n" +
                        "when \n" +
                        "    Here( $place : place)  \n" +
                        "    look($place, $things; $food := food) \n" +
                        "then \n" +
                        "    list.addAll( $things ); \n" +
                        "    list.addAll( $food   ); \n" +
                        //"    System.out.println( $things + \":\" + $food ); \n" +
                        "end \n" +
                        "";

        // System.out.println( str );
        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );

        for ( int i = 0; i < f; i++ ) {
            StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
            List<String> list = new ArrayList<String>();
            ksession.setGlobal( "list",
                                list );
            //ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();
            list.clear();
            InternalFactHandle fh = (InternalFactHandle) ksession.insert( Integer.valueOf( i ) );
            //ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);
            ksession.fireAllRules();

            //ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);

            assertEquals( 2,
                          list.size() );
            assertEquals( "peach",
                          list.get( 0 ) );
            assertEquals( "peach",
                          list.get( 1 ) );
            list.clear();

            InternalFactHandle[] handles = ksession.getFactHandles().toArray( new InternalFactHandle[0] );
            for ( int j = 0; j < handles.length; j++ ) {
                if ( handles[j].getObject() instanceof InitialFact || handles[j].getObject() instanceof Integer ) {
                    continue;
                }

                //ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);

                handles[j] = getFactHandle( handles[j],
                                            ksession );
                Object o = handles[j].getObject();

                // first retract + assert
                ksession.retract( handles[j] );
                //ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);

                handles[j] = (InternalFactHandle) ksession.insert( o );
                //ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);

                ksession.fireAllRules();
                assertEquals( 2,
                              list.size() );
                assertEquals( "peach",
                              list.get( 0 ) );
                assertEquals( "peach",
                              list.get( 1 ) );
                list.clear();

                // now try update
                // session was serialised so need to get factHandle
                handles[j] = getFactHandle( handles[j],
                                            ksession );
                ksession.update( handles[j],
                                 handles[j].getObject() );
                //ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);

                ksession.fireAllRules();
                assertEquals( 2,
                              list.size() );
                assertEquals( "peach",
                              list.get( 0 ) );
                assertEquals( "peach",
                              list.get( 1 ) );
                list.clear();
            }

            fh = getFactHandle( fh,
                                ksession );
            ksession.retract( fh );
            ksession.dispose();
        }

        for ( int i = f; i < h; i++ ) { //h; i++) {
            StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
            List<String> list = new ArrayList<String>();
            ksession.setGlobal( "list",
                                list );
            ksession.fireAllRules();
            list.clear();

            // ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true);

            InternalFactHandle fh = (InternalFactHandle) ksession.insert( Integer.valueOf( i ) );
            ksession.fireAllRules();
            assertEquals( 2,
                          list.size() );
            assertEquals( "table",
                          list.get( 0 ) );
            assertEquals( "peach",
                          list.get( 1 ) );
            list.clear();

            // ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);

            InternalFactHandle[] handles = ksession.getFactHandles().toArray( new InternalFactHandle[0] );
            for ( int j = 0; j < handles.length; j++ ) {
                if ( handles[j].getObject() instanceof InitialFact || handles[j].getObject() instanceof Integer ) {
                    continue;
                }

                //                if ( !handles[j].getObject().toString().equals( "Location( thing=peach, location=table )" )) {
                //                    continue;
                //                }     

                // ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);

                handles[j] = getFactHandle( handles[j],
                                            ksession );
                Object o = handles[j].getObject();

                // first retract + assert
                ksession.retract( handles[j] );
                // ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);

                handles[j] = (InternalFactHandle) ksession.insert( o );

                // ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);

                ksession.fireAllRules();
                assertEquals( 2,
                              list.size() );
                assertEquals( "table",
                              list.get( 0 ) );
                assertEquals( "peach",
                              list.get( 1 ) );
                list.clear();

                // ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);

                // now try update
                handles[j] = getFactHandle( handles[j],
                                            ksession );
                ksession.update( handles[j],
                                 handles[j].getObject() );

                //ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);

                ksession.fireAllRules();
                assertEquals( 2,
                              list.size() );
                assertEquals( "table",
                              list.get( 0 ) );
                assertEquals( "peach",
                              list.get( 1 ) );
                list.clear();
            }
            //ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession(ksession, true, false);

            fh = getFactHandle( fh,
                                ksession );
            ksession.retract( fh );
            ksession.dispose();
        }
    }

    @Test (timeout = 10000)
    public void testInsertionOrder() throws Exception {
        String str = "" +
                     "package org.drools.compiler.integrationtests  \n" +

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
                     "    ( Location(z, y;) and hasFood(x, z;) )\n" +
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
                     "end\n" +
                     "rule go2 when\n" +
                     "    String( this == 'go2') \n" +
                     "then\n" +
                     "        insert( new Person('zool', 'peach') );\n" +
                     "        insert( new Location(\"peach\", \"table\") );\n" +
                     "        insert( new Location(\"table\", \"kitchen\") );\n" +
                     "end\n" +
                     "\n" +
                     "rule go3 when\n" +
                     "    String( this == 'go3') \n" +
                     "then\n" +
                     "        insert( new Location(\"table\", \"kitchen\") );\n" +
                     "        insert( new Location(\"peach\", \"table\") );\n" +
                     "        insert( new Person('zool', 'peach') );\n" +
                     "end\n" +
                     "\n" +
                     "rule go4 when\n" +
                     "    String( this == 'go4') \n" +
                     "then\n" +
                     "        insert( new Location(\"peach\", \"table\") );\n" +
                     "        insert( new Location(\"table\", \"kitchen\") );\n" +
                     "        insert( new Person('zool', 'peach') );\n" +
                     "end\n" +
                     "rule go5 when\n" +
                     "    String( this == 'go5') \n" +
                     "then\n" +
                     "        insert( new Location(\"peach\", \"table\") );\n" +
                     "        insert( new Person('zool', 'peach') );\n" +
                     "        insert( new Location(\"table\", \"kitchen\") );\n" +
                     "end\n" +
                     "rule go6 when\n" +
                     "    String( this == 'go6') \n" +
                     "then\n" +
                     "        insert( new Location(\"table\", \"kitchen\") );\n" +
                     "        insert( new Person('zool', 'peach') );\n" +
                     "        insert( new Location(\"peach\", \"table\") );\n" +
                     "end\n" +
                     "\n" +
                     "\n";

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        

        for ( int i = 1; i <= 6; i++ ) {
            StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
            List<String> list = new ArrayList<String>();
            ksession.setGlobal( "list",
                                list );
            ksession.fireAllRules();
            list.clear();
            FactHandle fh = ksession.insert( "go" + i );
            ksession.fireAllRules();
            ksession.retract( fh );
            assertEquals( 1,
                          list.size() );
            assertEquals( "kitchen has peach",
                          list.get( 0 ) );
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testQueryFindAll() throws Exception {
        Object[] objects = new Object[]{Integer.valueOf( 42 ), "a String", Integer.valueOf( 100 )};
        int oCount = objects.length;

        List<Object> queryList = new ArrayList<Object>();
        List<Object> ruleList = new ArrayList<Object>();
        // expect all inserted objects + InitialFact
        runTestQueryFindAll( 0,
                             queryList,
                             ruleList,
                             objects );

        assertEquals( oCount,
                      queryList.size() );
        assertContains( objects,
                        queryList );

        // expect inserted objects + InitialFact
        queryList.clear();
        ruleList.clear();
        runTestQueryFindAll( 1, queryList, ruleList, objects );
        assertEquals( oCount*oCount, queryList.size() );

        queryList.clear();
        ruleList.clear();
        runTestQueryFindAll( 2, queryList, ruleList, objects );
        assertEquals( oCount*oCount, queryList.size() );
    }

    private void runTestQueryFindAll(int iCase,
                                     List<Object> queryList,
                                     List<Object> ruleList,
                                     Object[] objects) throws Exception,
                                                      Exception {
        String str = "" +
                     "package org.drools.compiler.test \n" +
                     "global java.util.List queryList \n" +
                     "global java.util.List ruleList \n" +
                     "query object( Object o ) \n" +
                     "    o := Object( ) \n" +
                     "end \n" +
                     "rule findObjectByQuery \n" +
                     "when \n";
        switch ( iCase ) {
            case 0 :
                // omit Object()
                str += "    object( $a ; ) \n";
                break;
            case 1 :
                str += "    Object() ";
                str += "    object( $a ; ) \n";
                break;
            case 2 :
                str += "    object( $a ; ) \n";
                str += "    Object() ";
                break;
        }
        str +=
                "then \n" +
                        "//   System.out.println( \"Object by query: \" + $a );\n" +
                        "    queryList.add( $a ); \n" +
                        "end \n" +
                        "rule findObject \n" +
                        "salience 10 \n" +
                        "when \n" +
                        "    $o: Object() \n" +
                        "then " +
                        "//   System.out.println( \"Object: \" + $o );\n" +
                        "    ruleList.add( $o ); \n" +
                        "end \n" +
                        "";

        logger.debug( str );

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        ksession.setGlobal( "queryList",
                            queryList );
        ksession.setGlobal( "ruleList",
                            ruleList );
        for ( Object o : objects ) {
            ksession.insert( o );
        }
        ksession.fireAllRules();
        ksession.dispose();
    }

    @Test (timeout = 10000)
    public void testQueryWithObject() throws Exception {
        String str = "" +
                     "package org.drools.compiler.test  \n" +

                     "import java.util.List\n" +
                     "import java.util.ArrayList\n" +

                     "global List list\n" +

                     "dialect \"mvel\"\n" +
                     "\n" +

                     "import " + BackwardChainingTest.class.getName() + ".Q\n" +
                     "import " + BackwardChainingTest.class.getName() + ".R\n" +
                     "import " + BackwardChainingTest.class.getName() + ".S\n" +

                     "query object(Object o)\n" +
                     "    o := Object() \n" +
                     "end\n" +

                     "rule collectObjects when\n" +
                     "   String( this == 'go1' )\n" +
                     "   object( o; )\n" +
                     "then\n" +
                     "   list.add( o );\n" +
                     "end\n" +

                     "rule init when\n" +
                     "   String( this == 'init' )\n" +
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

        KnowledgeBase kbase = SerializationHelper.serializeObject( loadKnowledgeBaseFromString( str ) );
        StatefulKnowledgeSession ksession = createKnowledgeSession( kbase );
        
        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list",
                            list );
        
        ksession.insert( "init" );
        ksession.fireAllRules();  
        
        ksession.insert( "go1" );
        ksession.fireAllRules();

        System.out.println( list );
        
        assertEquals( 12,
                      list.size() );
        assertContains( new Object[]{ "go1", "init",
                                      new Q( 6 ), new R( 6 ), new S( 3 ), new R( 2 ), new R( 1 ), new R( 4 ), new S( 2 ), new S( 6 ), new Q( 1 ), new Q( 5 )},
                                      list );

        ksession.dispose();
        // now reverse the go1 and init order
        ksession = createKnowledgeSession(kbase);
        list = new ArrayList<Integer>();
        ksession.setGlobal( "list",
                            list );

        ksession.insert( "go1" );
        ksession.fireAllRules();

        ksession.insert( "init" );
        ksession.fireAllRules();

        System.out.println( list );
        assertEquals( 12,
                      list.size() );
        assertContains( new Object[]{"go1", "init",
                                new Q( 6 ), new R( 6 ), new S( 3 ), new R( 2 ), new R( 1 ), new R( 4 ), new S( 2 ), new S( 6 ), new Q( 1 ), new Q( 5 )},
                        list );
    }

    public void assertContains(Object[] objects,
                               List list) {
        for ( Object object : objects ) {
            if ( !list.contains( object ) ) {
                fail( "does not contain:" + object );
            }
        }
    }

    public void assertContains(List objects,
                               List list) {
        if ( !list.contains( objects ) ) {
            fail( "does not contain:" + objects );
        }
    }

    public InternalFactHandle getFactHandle(FactHandle factHandle,
                                            StatefulKnowledgeSession ksession) {
        Map<Integer, FactHandle> handles = new HashMap<Integer, FactHandle>();
        for ( FactHandle fh : ksession.getFactHandles() ) {
            handles.put( ((InternalFactHandle) fh).getId(),
                         fh );
        }
        return (InternalFactHandle) handles.get( ((InternalFactHandle) factHandle).getId() );
    }

    public static class Man
        implements
        Serializable {
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

        @Override
        public String toString() {
            return "Man [name=" + name + "]";
        }                
    }

    public static class Woman
        implements
        Serializable {
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

        @Override
        public String toString() {
            return "Woman [name=" + name + "]";
        }
    }

    public static class Parent
        implements
        Serializable {
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

        @Override
        public String toString() {
            return "Parent [parent=" + parent + ", child=" + child + "]";
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + value;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            Q other = (Q) obj;
            if ( value != other.value ) return false;
            return true;
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + value;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            R other = (R) obj;
            if ( value != other.value ) return false;
            return true;
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

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + value;
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            S other = (S) obj;
            if ( value != other.value ) return false;
            return true;
        }

    }

    @Test(timeout = 10000)
    public void testQueryWithClassLiterals() throws Exception {
        String str = "" +
                "package org.drools.test \n" +

                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "global List list\n" +

                "declare Foo end \n" +

                "query klass( Class $c )\n" +
                " Object( this.getClass() == $c ) \n" +
                "end\n" +

                "rule R when\n" +
                " o : String( this == 'go1' )\n" +
                " klass( String.class ; )\n" +
                "then\n" +
                " list.add( o );\n" +
                " insert( new Foo() ); \n" +
                "end\n" +

                "rule S when\n" +
                " o : Foo()\n" +
                " klass( Foo.class ; )\n" +
                "then\n" +
                " list.add( o );\n" +
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

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List list = new ArrayList();
        ksession.setGlobal( "list", list );


        ksession.insert( "go1" );
        ksession.fireAllRules();

        System.out.println( list );

        assertEquals( 2, list.size() );
        assertEquals( "go1", list.get( 0 ) );
        assertEquals( "org.drools.test.Foo", list.get( 1 ).getClass().getName() );
    }

    @Test(timeout = 10000)
    public void testQueryIndexingWithUnification() throws Exception {
        String str = "" +
                "package org.drools.test \n" +

                "import java.util.List\n" +
                "import java.util.ArrayList\n" +

                "global List list\n" +

                "declare Foo id : int end \n" +
                "declare Bar " +
                " name : String " +
                " val : int " +
                "end \n" +

                "query fooffa( String $name, Foo $f )\n" +
                " Bar( name == $name, $id : val )\n" +
                " $f := Foo( id == $id ) \n" +
                "end\n" +

                "rule R when\n" +
                " o : String( this == 'go' )\n" +
                " fooffa( \"x\", $f ; )\n" +
                "then\n" +
                " list.add( $f );\n" +
                "end\n" +

                "rule S when\n" +
                "then\n" +
                " insert( new Foo( 1 ) );\n" +
                " insert( new Bar( \"x\", 1 ) );\n" +
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

        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal( "list",
                list );

        ksession.fireAllRules();

        ksession.insert( "go" );
        ksession.fireAllRules();

        System.out.println( list );

        assertEquals( 1, list.size() );
    }

    @Test
    public void testQueryWithEvalAndTypeBoxingUnboxing() {
        String drl = "package org.drools.test;\n" +
                     "\n" +
                     "global java.util.List list \n;" +
                     "\n" +
                     "query primitiveInt( int $a )\n" +
                     " Integer( intValue == $a )\n" +
                     " eval( $a == 178 )\n" +
                     "end\n" +
                     "\n" +
                     "query boxedInteger( Integer $a )\n" +
                     " Integer( this == $a )\n" +
                     " eval( $a == 178 )\n" +
                     "end\n" +
                     "\n" +
                     "query boxInteger( int $a )\n" +
                     " Integer( this == $a )\n" +
                     " eval( $a == 178 )\n" +
                     "end\n" +
                     "\n" +
                     "query unboxInteger( Integer $a )\n" +
                     " Integer( intValue == $a )\n" +
                     " eval( $a == 178 )\n" +
                     "end\n" +
                     "\n" +
                     "query cast( int $a )\n" +
                     " Integer( longValue == $a )\n" +
                     " eval( $a == 178 )\n" +
                     "end\n" +
                     "" +
                     "query cast2( long $a )\n" +
                     " Integer( intValue == $a )\n" +
                     " eval( $a == 178 )\n" +
                     "end\n" +
                     "\n" +
                     "rule Init when then insert( 178 ); end\n" +
                     "\n" +
                     "rule Check\n" +
                     "when\n" +
                     " String()\n" +
                     " ?primitiveInt( 178 ; )\n" +
                     " ?boxedInteger( $x ; )\n" +
                     " ?boxInteger( $x ; )\n" +
                     " ?unboxInteger( $y ; )\n" +
                     " ?cast( $z ; )\n" +
                     " ?cast2( $z ; )\n" +
                     "then\n" +
                     " list.add( $x ); \n" +
                     " list.add( $y ); \n" +
                     " list.add( $z ); \n" +
                     "end";

        KnowledgeBase knowledgeBase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();
        assertTrue( list.isEmpty() );

        knowledgeSession.insert( "go" );
        knowledgeSession.fireAllRules();

        assertEquals( Arrays.asList( 178, 178, 178 ), list );

    }


    @Test
    public void testQueryWithEvents() {
        String drl = "global java.util.List list; " +
                     "" +
                     "declare Inner\n" +
                     "  @role(event)\n" +
                     "end\n" +

                     "rule \"Input\"\n" +
                     "when\n" +
                     "then\n" +
                     "    insert( \"X\" );\n" +
                     "    insert( new Inner( ) );\n" +
                     "end\n" +
                     "\n" +
                     "query myAgg(  )\n" +
                     "    Inner(  )\n" +
                     "end\n" +
                     "\n" +
                     "rule \"React\"\n" +
                     "when\n" +
                     "    String()\n" +
                     "    myAgg(  )\n" +
                     "then\n" +
                     "    list.add( 42 );\n" +
                     "end";

        KnowledgeBase knowledgeBase = loadKnowledgeBaseFromString( drl );
        StatefulKnowledgeSession knowledgeSession = knowledgeBase.newStatefulKnowledgeSession();
        ArrayList list = new ArrayList();
        knowledgeSession.setGlobal( "list", list );

        knowledgeSession.fireAllRules();

        assertEquals( Arrays.asList( 42 ), list );

    }

    @Test
    public void testNpeOnQuery() {
        String drl =
                "global java.util.List list; " +
                "query foo( Integer $i ) " +
                "   $i := Integer( this < 10 ) " +
                "end\n" +
                "\n" +

                "rule r1 when " +
                "   foo( $i ; ) " +
                "   Integer( this == 10 ) " +
                "then " +
                "   System.out.println(\"10 \" + $i);" +
                "   list.add( 10 );\n" +
                "end\n" +
                "\n" +

                "rule r2 when " +
                "   foo( $i; ) " +
                "   Integer( this == 20 ) " +
                "then " +
                "   System.out.println(\"20 \" + $i);" +
                "   list.add( 20 );\n" +
                "end\n" +

                "rule r3 when " +
                "   $i : Integer( this == 1 ) " +
                "then " +
                "   System.out.println($i);" +
                "   update( kcontext.getKieRuntime().getFactHandle( $i ), $i + 1 );" +
                "end\n" +
                "\n";

        KieHelper helper = new KieHelper();
        helper.addContent( drl, ResourceType.DRL );
        KieSession kieSession = helper.build().newKieSession();

        List<Integer> list = new ArrayList<Integer>();
        kieSession.setGlobal( "list", list );

        kieSession.insert( 1 );
        kieSession.insert( 20 );

        kieSession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( 20, (int)list.get(0) );
    }
}
