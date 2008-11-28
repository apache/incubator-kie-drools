/*
 * Copyright 2007 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Created on Dec 14, 2007
 */
package org.drools.integrationtests;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.ClockType;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.SessionConfiguration;
import org.drools.StockTick;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.DroolsParserException;
import org.drools.io.ResourceFactory;
import org.drools.runtime.KnowledgeSessionConfiguration;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;

/**
 * Tests related to the stream support features
 * 
 * @author etirelli
 */
public class StreamsTest extends TestCase {

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    private KnowledgeBase loadKnowledgeBase(final String fileName) throws IOException,
                                                                  DroolsParserException,
                                                                  Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newClassPathResource( fileName,
                                                                    getClass() ),
                              KnowledgeType.DRL );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        return SerializationHelper.serializeObject( kbase );
    }

    public void testEventAssertion() throws Exception {
        // read in the source
        KnowledgeBase kbase = loadKnowledgeBase( "test_EntryPoint.drl" );
        //final RuleBase ruleBase = loadRuleBase( reader );

        KnowledgeSessionConfiguration conf = new SessionConfiguration();
        ((SessionConfiguration) conf).setClockType( ClockType.PSEUDO_CLOCK );
        StatefulKnowledgeSession session = kbase.newStatefulSession( conf );

        final List results = new ArrayList();

        session.setGlobal( "results",
                           results );

        StockTick tick1 = new StockTick( 1,
                                         "DROO",
                                         50,
                                         System.currentTimeMillis() );
        StockTick tick2 = new StockTick( 2,
                                         "ACME",
                                         10,
                                         System.currentTimeMillis() );
        StockTick tick3 = new StockTick( 3,
                                         "ACME",
                                         10,
                                         System.currentTimeMillis() );
        StockTick tick4 = new StockTick( 4,
                                         "DROO",
                                         50,
                                         System.currentTimeMillis() );

        InternalFactHandle handle1 = (InternalFactHandle) session.insert( tick1 );
        InternalFactHandle handle2 = (InternalFactHandle) session.insert( tick2 );
        InternalFactHandle handle3 = (InternalFactHandle) session.insert( tick3 );
        InternalFactHandle handle4 = (InternalFactHandle) session.insert( tick4 );

        assertNotNull( handle1 );
        assertNotNull( handle2 );
        assertNotNull( handle3 );
        assertNotNull( handle4 );

        assertTrue( handle1.isEvent() );
        assertTrue( handle2.isEvent() );
        assertTrue( handle3.isEvent() );
        assertTrue( handle4.isEvent() );

        session.fireAllRules();

        assertEquals( 0,
                      results.size() );

        StockTick tick5 = new StockTick( 5,
                                         "DROO",
                                         50,
                                         System.currentTimeMillis() );
        StockTick tick6 = new StockTick( 6,
                                         "ACME",
                                         10,
                                         System.currentTimeMillis() );
        StockTick tick7 = new StockTick( 7,
                                         "ACME",
                                         15,
                                         System.currentTimeMillis() );
        StockTick tick8 = new StockTick( 8,
                                         "DROO",
                                         50,
                                         System.currentTimeMillis() );

        WorkingMemoryEntryPoint entry = session.getWorkingMemoryEntryPoint( "StockStream" );

        InternalFactHandle handle5 = (InternalFactHandle) entry.insert( tick5 );
        InternalFactHandle handle6 = (InternalFactHandle) entry.insert( tick6 );
        InternalFactHandle handle7 = (InternalFactHandle) entry.insert( tick7 );
        InternalFactHandle handle8 = (InternalFactHandle) entry.insert( tick8 );

        assertNotNull( handle5 );
        assertNotNull( handle6 );
        assertNotNull( handle7 );
        assertNotNull( handle8 );

        assertTrue( handle5.isEvent() );
        assertTrue( handle6.isEvent() );
        assertTrue( handle7.isEvent() );
        assertTrue( handle8.isEvent() );

        session.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertSame( tick7,
                    results.get( 0 ) );

    }

}
