/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
 */

package org.drools.jsr94.rules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.rules.Handle;
import javax.rules.ObjectFilter;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the <code>StatefulRuleSession</code> implementation.
 *
 * @see javax.rules.StatefulRuleSession
 */
public class StatefulRuleSessionTest extends RuleEngineTestBase {

    /**
     * Test containsObject.
     */
    @Test
    public void testContainsObject() throws Exception {
        this.statefulSession = this.engine.getStatefulRuleSession( this.bindUri );
        final Person bob = new Person( "bob" );
        final Handle handle = this.statefulSession.addObject( bob );
        assertTrue( "where is bob",
                    this.statefulSession.containsObject( handle ) );
    }

    /**
     * Test addObject.
     */
    @Test
    public void testAddObject() throws Exception {
        // tested in testContainsObject
    }

    @Test
    public void testJsr94FactHandleFactoryAvailable() throws ClassNotFoundException {
        this.getClass().getClassLoader().loadClass( "org.drools.jsr94.rules.Jsr94FactHandleFactory" );
    }

    /**
     * Test addObjects.
     */
    @Test
    public void testAddObjects() throws Exception {
        this.statefulSession = this.engine.getStatefulRuleSession( this.bindUri );
        final List inObjects = new ArrayList();

        final Person bob = new Person( "bob" );
        inObjects.add( bob );

        final Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        final Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        final List handleList = this.statefulSession.addObjects( inObjects );
        assertEquals( "incorrect size",
                      3,
                      handleList.size() );
        assertEquals( "where is bob",
                      bob,
                      this.statefulSession.getObject( (Handle) handleList.get( 0 ) ) );
        assertEquals( "where is rebecca",
                      rebecca,
                      this.statefulSession.getObject( (Handle) handleList.get( 1 ) ) );
        assertEquals( "where is jeannie",
                      jeannie,
                      this.statefulSession.getObject( (Handle) handleList.get( 2 ) ) );
    }

    /**
     * Test getObject.
     */
    @Test
    public void testGetObject() throws Exception {
        // tested in testAddObjects
    }

    /**
     * Test updateObject.
     */
    @Test
    public void testUpdateObject() throws Exception {
        this.statefulSession = this.engine.getStatefulRuleSession( this.bindUri );
        Person bob = new Person( "bob" );
        final Handle handle = this.statefulSession.addObject( bob );
        this.statefulSession.updateObject( handle,
                                           bob = new Person( "boby" ) );
        assertEquals( "where is boby",
                      bob,
                      this.statefulSession.getObject( handle ) );
    }

    /**
     * Test removeObject.
     */
    @Test
    public void testRemoveObject() throws Exception {
        this.statefulSession = this.engine.getStatefulRuleSession( this.bindUri );
        final Person bob = new Person( "bob" );
        final Handle handle = this.statefulSession.addObject( bob );
        assertTrue( "where is bob",
                    this.statefulSession.containsObject( handle ) );

        this.statefulSession.removeObject( handle );
        assertTrue( "bob still there",
                    !this.statefulSession.containsObject( handle ) );
    }

    /**
     * Test getObjects.
     */
    @Test
    public void testGetObjects() throws Exception {
        this.statefulSession = this.engine.getStatefulRuleSession( this.bindUri );

        final Person bob = new Person( "bob" );
        this.statefulSession.addObject( bob );

        final Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        this.statefulSession.addObject( rebecca );

        final Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        this.statefulSession.addObject( jeannie );

        // execute the rules
        this.statefulSession.executeRules();
        final List outList = this.statefulSession.getObjects();
        assertEquals( "incorrect size",
                      5,
                      outList.size() );

        assertTrue( "where is bob",
                    outList.contains( bob ) );
        assertTrue( "where is rebecca",
                    outList.contains( rebecca ) );
        assertTrue( "where is jeannie",
                    outList.contains( jeannie ) );

        assertTrue( outList.contains( "rebecca and jeannie are sisters" ) );
        assertTrue( outList.contains( "jeannie and rebecca are sisters" ) );

        this.statefulSession.release();
    }

    /**
     * Test getObjects with ObjectFilter.
     */
    @Test
    public void testGetObjectsWithFilter() throws Exception {
        this.statefulSession = this.engine.getStatefulRuleSession( this.bindUri );

        final Person bob = new Person( "bob" );
        this.statefulSession.addObject( bob );

        final Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        this.statefulSession.addObject( rebecca );

        final Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        this.statefulSession.addObject( jeannie );

        // execute the rules
        this.statefulSession.executeRules();
        final List outList = this.statefulSession.getObjects( new PersonFilter() );
        assertEquals( "incorrect size",
                      3,
                      outList.size() );

        assertTrue( "where is bob",
                    outList.contains( bob ) );
        assertTrue( "where is rebecca",
                    outList.contains( rebecca ) );
        assertTrue( "where is jeannie",
                    outList.contains( jeannie ) );

        this.statefulSession.release();
    }

    /**
     * Test executeRules.
     */
    @Test
    public void testExecuteRules() throws Exception {
        // tested in testGetObjects, testGetObjectsWithFilter
    }

    /**
     * Test reset.
     */
    @Test
    public void testReset() throws Exception {
        this.statefulSession = this.engine.getStatefulRuleSession( this.bindUri );

        final Person bob = new Person( "bob" );
        final Handle handle = this.statefulSession.addObject( bob );
        assertTrue( "where is bob",
                    this.statefulSession.containsObject( handle ) );

        this.statefulSession.reset();
        assertTrue( "bob still there",
                    !this.statefulSession.containsObject( handle ) );
    }

    /**
     * Test executeRules with globals.
     */
    @Test
    public void testExecuteRulesGlobals() throws Exception {
        final java.util.Map map = new HashMap();
        java.util.Vector v = new java.util.Vector();
        map.put( "vector",
                 v );
        this.statefulSession = this.engine.getStatefulRuleSession( this.bindUri_globals,
                                                                   map );

        final Person bob = new Person( "bob" );
        this.statefulSession.addObject( bob );

        final Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        this.statefulSession.addObject( rebecca );

        final Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        this.statefulSession.addObject( jeannie );

        // execute the rules
        this.statefulSession.executeRules();

        final List outList = this.statefulSession.getObjects();

        assertEquals( "incorrect size",
                      5,
                      outList.size() );

        assertContains( outList,
                        bob );

        assertContains( outList,
                        rebecca );

        assertContains( outList,
                        jeannie );

        assertContains( outList,
                        "rebecca and jeannie are sisters" );

        assertContains( outList,
                        "jeannie and rebecca are sisters" );

        v = (java.util.Vector) map.get( "vector" );

        assertNotNull( "Global Vector null",
                       v );

        assertContains( v,
                        "rebecca and jeannie are sisters" );

        assertContains( v,
                        "jeannie and rebecca are sisters" );

        assertEquals( "Vector v incorrect size",
                      2,
                      v.size() );

        this.statefulSession.release();
    }

    /**
     * Test executeRules drl with dsl.
     */
    @Test
    public void testExecuteRules_dsl() throws Exception {
        this.statefulSession = this.engine.getStatefulRuleSession( this.bindUri_drl );

        final Person bob = new Person( "bob" );
        this.statefulSession.addObject( bob );

        final Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        this.statefulSession.addObject( rebecca );

        final Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        this.statefulSession.addObject( jeannie );

        // execute the rules
        this.statefulSession.executeRules();

        final List outList = this.statefulSession.getObjects();

        assertEquals( "incorrect size",
                      5,
                      outList.size() );

        assertContains( outList,
                        bob );

        assertContains( outList,
                        rebecca );

        assertContains( outList,
                        jeannie );

        assertContains( outList,
                        "rebecca and jeannie are sisters" );

        assertContains( outList,
                        "jeannie and rebecca are sisters" );

        this.statefulSession.release();
    }

    protected void assertContains(final List expected,
                                  final Object object) {
        if ( expected.contains( object ) ) {
            return;
        }

        fail( object + " not in " + expected );
    }

    /**
     * Filter accepts only objects of type Person.
     */
    static class PersonFilter
        implements
        ObjectFilter {
        public Object filter(final Object object) {
            return (object instanceof Person ? object : null);
        }

        public void reset() {
            // nothing to reset
        }
    }

}
