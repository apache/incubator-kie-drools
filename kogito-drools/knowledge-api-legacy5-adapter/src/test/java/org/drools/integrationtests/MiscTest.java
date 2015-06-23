/*
 * Copyright 2015 JBoss Inc
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

package org.drools.integrationtests;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseConfiguration;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderConfiguration;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.model.Person;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.conf.ClockTypeOption;
import org.drools.runtime.rule.FactHandle;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.drools.runtime.rule.Variable;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.time.SessionPseudoClock;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;

public class MiscTest {

    protected KnowledgeBase loadKnowledgeBaseFromString(String... drlContentStrings) {
        return loadKnowledgeBaseFromString( null, null, drlContentStrings );
    }

    protected KnowledgeBase loadKnowledgeBaseFromString(KnowledgeBuilderConfiguration config,
                                                        String... drlContentStrings) {
        return loadKnowledgeBaseFromString( config, null, drlContentStrings );
    }

    protected KnowledgeBase loadKnowledgeBaseFromString(KnowledgeBaseConfiguration kBaseConfig,
                                                        String... drlContentStrings) {
        return loadKnowledgeBaseFromString( null, kBaseConfig, drlContentStrings );
    }

    protected KnowledgeBase loadKnowledgeBaseFromString(KnowledgeBuilderConfiguration config,
                                                        KnowledgeBaseConfiguration kBaseConfig,
                                                        String... drlContentStrings) {
        KnowledgeBuilder kbuilder = config == null ? KnowledgeBuilderFactory.newKnowledgeBuilder() : KnowledgeBuilderFactory.newKnowledgeBuilder( config );
        for ( String drlContentString : drlContentStrings ) {
            kbuilder.add( ResourceFactory.newByteArrayResource(drlContentString.getBytes()),
                          ResourceType.DRL );
        }

        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        if ( kBaseConfig == null ) {
            kBaseConfig = KnowledgeBaseFactory.newKnowledgeBaseConfiguration();
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( kBaseConfig );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );
        return kbase;
    }

    @Test
    public void testNullValueInFrom() {
        // DROOLS-71
        String str =
                "global java.util.List list\n" +
                "\n" +
                "rule R\n" +
                "when\n" +
                "    $i : Integer( ) from list\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        List<Integer> list = new ArrayList<Integer>();
        ksession.setGlobal("list", list);

        list.add(1);
        list.add(null);
        list.add(2);

        ksession.fireAllRules();
    }

    @Test
    public void testErrorReporting() {
        String str =
                "global java.util.List list\n" +
                "\n" +
                "rule R\n" +
                "when\n" +
                "    $i : Intege( ) from list\n" +
                "then\n" +
                "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(str.getBytes()),
                      ResourceType.DRL );

        assertTrue(kbuilder.hasErrors());
        assertTrue(kbuilder.getErrors().toString().contains("Intege"));
    }

    @Test
    public void testUsingSessionClock() {
        //BZ-1058687
        String str =
                "global java.util.List list\n" +
                "\n" +
                "rule R\n" +
                "when\n" +
                "    $i : Integer( ) from list\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        KnowledgeSessionConfiguration ksconfig = KnowledgeBaseFactory.newKnowledgeSessionConfiguration();
        ksconfig.setOption(ClockTypeOption.get("pseudo"));
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(ksconfig, null);

        // compilation fails on Java 6
        SessionPseudoClock clock = ksession.getSessionClock();
    }

    @Test
    public void testUsingNullConfiguration() {
        String str =
                "global java.util.List list\n" +
                "\n" +
                "rule R\n" +
                "when\n" +
                "    $i : Integer( ) from list\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession(null, null);

    }
    
    @Test
    public void testConsequenceException() {
        // BZ-1077834
        String str =
                "package foo.bar\n" +
                "rule R\n" +
                "when\n" +
                "then\n" +
                "    throw new RuntimeException(\"foo\");" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();
        
        try {
            ksession.fireAllRules();
        } catch( org.drools.runtime.rule.ConsequenceException e ) {
            // this is correct, succeeds
        } catch( Exception other) {
            fail("Wrong exception raised = "+other.getClass().getCanonicalName());
        }
    }

    @Test
    public void testConsequenceException2() {
        // BZ-1077834
        String str
                = "package foo.bar\n"
                  + "rule R\n"
                  + "when\n"
                  + "then\n"
                  + "    throw new RuntimeException(\"foo\");"
                  + "end";

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newByteArrayResource(str.getBytes(Charset.forName("UTF-8"))), ResourceType.DRL);

        if (kbuilder.hasErrors()) {
            throw new RuntimeException("Drools compile errors: " + kbuilder.getErrors().toString());
        }

        KnowledgeBase kbase = kbuilder.newKnowledgeBase();
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        try {
            ksession.fireAllRules();
        } catch (org.drools.runtime.rule.ConsequenceException e) {
            // this is correct, succeeds
        } catch (Exception other) {
            fail("Wrong exception raised = " + other.getClass().getCanonicalName());
        }
    }

    @Test
    public void testNotExistingEntryPoint() {
        // BZ-1099767
        String str =
                "package foo.bar\n" +
                "rule R\n" +
                "when\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        WorkingMemoryEntryPoint entryPoint = ksession.getWorkingMemoryEntryPoint("x");
        assertNull(entryPoint);
    }

    @Test
    public void testQueriesWithVariableUnification() throws Exception {
        // BZ-1100820
        String str = "";
        str += "package org.drools.compiler.test  \n";
        str += "import org.drools.model.Person \n";
        str += "query peeps( String $name, String $likes, int $age ) \n";
        str += "    $p : Person( $name := name, $likes := likes, $age := age ) \n";
        str += "end\n";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        Person p1 = new Person( "darth",
                                "stilton",
                                100 );
        Person p2 = new Person( "yoda",
                                "stilton",
                                300 );
        Person p3 = new Person( "luke",
                                "brie",
                                300 );
        Person p4 = new Person( "bobba",
                                "cheddar",
                                300 );

        ksession.insert( p1 );
        ksession.insert( p2 );
        ksession.insert( p3 );
        ksession.insert( p4 );

        QueryResults results = ksession.getQueryResults( "peeps", new Object[]{Variable.v, Variable.v, Variable.v} );
        assertEquals( 4,
                      results.size() );
        List names = new ArrayList();
        for ( QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 4,
                      names.size() );
        assertTrue( names.contains( "luke" ) );
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "bobba" ) );
        assertTrue( names.contains( "darth" ) );

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{Variable.v, Variable.v, 300} );
        assertEquals( 3,
                      results.size() );
        names = new ArrayList();
        for ( QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 3,
                      names.size() );
        assertTrue( names.contains( "luke" ) );
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "bobba" ) );

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{Variable.v, "stilton", 300} );
        assertEquals( 1,
                      results.size() );
        names = new ArrayList();
        for ( QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 1,
                      names.size() );
        assertTrue( names.contains( "yoda" ) );

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{Variable.v, "stilton", Variable.v} );
        assertEquals( 2,
                      results.size() );
        names = new ArrayList();
        for ( QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 2,
                      names.size() );
        assertTrue( names.contains( "yoda" ) );
        assertTrue( names.contains( "darth" ) );

        results = ksession.getQueryResults( "peeps",
                                            new Object[]{"darth", Variable.v, Variable.v} );
        assertEquals( 1,
                      results.size() );
        names = new ArrayList();
        for ( QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertEquals( 1,
                      names.size() );
        assertTrue( names.contains( "darth" ) );
    }

    @Test
    public void testNotExistingFactHandle() {
        // BZ-1119731
        String str =
                "package foo.bar\n" +
                "rule R\n" +
                "when\n" +
                "then\n" +
                "end";

        KnowledgeBase kbase = loadKnowledgeBaseFromString(str);
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        FactHandle handle = ksession.getFactHandle("fact");
        assertNull(handle);
    }

}
