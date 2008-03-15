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

import junit.framework.Assert;
import junit.framework.TestCase;
import org.drools.ClockType;
import org.drools.WorkingMemoryEntryPoint;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.StockTick;
import org.drools.WorkingMemory;
import org.drools.common.InternalFactHandle;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }

    protected RuleBase getRuleBase(final RuleBaseConfiguration config) throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            config );
    }

    private RuleBase loadRuleBase(final Reader reader) throws IOException,
                                                      DroolsParserException,
                                                      Exception {
        final DrlParser parser = new DrlParser();
        final PackageDescr packageDescr = parser.parse( reader );
        if ( parser.hasErrors() ) {
            System.out.println( parser.getErrors() );
            Assert.fail( "Error messages in parser, need to sort this our (or else collect error messages)" );
        }
        // pre build the package
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackage( packageDescr );
        final Package pkg = builder.getPackage();

        // add the package to a rulebase
        final RuleBase ruleBase = getRuleBase();
        ruleBase.addPackage( pkg );
        // load up the rulebase
        return SerializationHelper.serializeObject(ruleBase);
    }

    public void testEventAssertion() throws Exception {
        // read in the source
        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "test_EntryPoint.drl" ) );
        final RuleBase ruleBase = loadRuleBase( reader );

        final WorkingMemory wm = ruleBase.newTemporalSession( ClockType.PSEUDO_CLOCK );
        final List results = new ArrayList();

        wm.setGlobal( "results",
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

        InternalFactHandle handle1 = (InternalFactHandle) wm.insert( tick1 );
        InternalFactHandle handle2 = (InternalFactHandle) wm.insert( tick2 );
        InternalFactHandle handle3 = (InternalFactHandle) wm.insert( tick3 );
        InternalFactHandle handle4 = (InternalFactHandle) wm.insert( tick4 );

        assertNotNull( handle1 );
        assertNotNull( handle2 );
        assertNotNull( handle3 );
        assertNotNull( handle4 );

// TODO need to fix those tests
//       assertTrue( handle1.isEvent() );
//        assertTrue( handle2.isEvent() );
//        assertTrue( handle3.isEvent() );
//        assertTrue( handle4.isEvent() );

        wm.fireAllRules();

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

        WorkingMemoryEntryPoint entry = wm.getWorkingMemoryEntryPoint( "StockStream" );

        InternalFactHandle handle5 = (InternalFactHandle) entry.insert( tick5 );
        InternalFactHandle handle6 = (InternalFactHandle) entry.insert( tick6 );
        InternalFactHandle handle7 = (InternalFactHandle) entry.insert( tick7 );
        InternalFactHandle handle8 = (InternalFactHandle) entry.insert( tick8 );

        assertNotNull( handle5 );
        assertNotNull( handle6 );
        assertNotNull( handle7 );
        assertNotNull( handle8 );

// TODO need to fix those tests
//        assertTrue( handle5.isEvent() );
//        assertTrue( handle6.isEvent() );
//        assertTrue( handle7.isEvent() );
//        assertTrue( handle8.isEvent() );

        wm.fireAllRules();

        assertEquals( 1,
                      results.size() );
        assertSame( tick7,
                    results.get( 0 ) );

    }

}
