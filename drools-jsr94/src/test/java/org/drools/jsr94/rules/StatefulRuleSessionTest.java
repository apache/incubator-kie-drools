package org.drools.jsr94.rules;

/*
 * $Id: StatefulRuleSessionTestCase.java,v 1.7 2005/05/06 19:54:49 dbarnett Exp $
 *
 * Copyright 2002-2004 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a registered trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company.
 * (http://drools.werken.com/).
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.rules.Handle;
import javax.rules.ObjectFilter;
import javax.rules.StatelessRuleSession;

/**
 * Test the <code>StatefulRuleSession</code> implementation.
 *
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 * @author <a href="mailto:michael.frandsen@syngenio.de">Michael Frandsen </a>
 * @see javax.rules.StatefulRuleSession
 */
public class StatefulRuleSessionTest extends RuleEngineTestBase
{
    /**
     * Setup the test case.
     */
    protected void setUp( ) throws Exception
    {
        super.setUp( );
    }

    /**
     * Test containsObject.
     */
    public void testContainsObject( ) throws Exception
    {
        this.statefulSession = engine.getStatefulRuleSession( bindUri );
        Person bob = new Person( "bob" );
        Handle handle = statefulSession.addObject( bob );
        assertTrue( "where is bob", statefulSession.containsObject( handle ) );
    }

    /**
     * Test addObject.
     */
    public void testAddObject( ) throws Exception
    {
        // tested in testContainsObject
    }

    public void testJsr94FactHandleFactoryAvailable( )
        throws ClassNotFoundException
    {
        this.getClass( ).getClassLoader( ).loadClass(
            "org.drools.jsr94.rules.Jsr94FactHandleFactory" );
    }

    /**
     * Test addObjects.
     */
    public void testAddObjects( ) throws Exception
    {
        this.statefulSession = engine.getStatefulRuleSession( bindUri );
        List inObjects = new ArrayList( );

        Person bob = new Person( "bob" );
        inObjects.add( bob );

        Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        List handleList = statefulSession.addObjects( inObjects );
        assertEquals( "incorrect size", 3, handleList.size( ) );
        assertEquals( "where is bob", bob,
            statefulSession.getObject( ( Handle ) handleList.get( 0 ) ) );
        assertEquals( "where is rebecca", rebecca,
            statefulSession.getObject( ( Handle ) handleList.get( 1 ) ) );
        assertEquals( "where is jeannie", jeannie,
            statefulSession.getObject( ( Handle ) handleList.get( 2 ) ) );
    }

    /**
     * Test getObject.
     */
    public void testGetObject( ) throws Exception
    {
        // tested in testAddObjects
    }

    /**
     * Test updateObject.
     */
    public void testUpdateObject( ) throws Exception
    {
        this.statefulSession = engine.getStatefulRuleSession( bindUri );
        Person bob = new Person( "bob" );
        Handle handle = statefulSession.addObject( bob );
        statefulSession.updateObject( handle, bob = new Person( "boby" ) );
        assertEquals(
            "where is boby", bob, statefulSession.getObject( handle ) );
    }

    /**
     * Test removeObject.
     */
    public void testRemoveObject( ) throws Exception
    {
        this.statefulSession = engine.getStatefulRuleSession( bindUri );
        Person bob = new Person( "bob" );
        Handle handle = statefulSession.addObject( bob );
        assertTrue( "where is bob", statefulSession.containsObject( handle ) );

        statefulSession.removeObject( handle );
        assertTrue(
            "bob still there", !statefulSession.containsObject( handle ) );
    }

    /**
     * Test getObjects.
     */
    public void testGetObjects( ) throws Exception
    {
        this.statefulSession = engine.getStatefulRuleSession( bindUri );

        Person bob = new Person( "bob" );
        statefulSession.addObject( bob );

        Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        statefulSession.addObject( rebecca );

        Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        statefulSession.addObject( jeannie );

        // execute the rules
        statefulSession.executeRules( );
        List outList = statefulSession.getObjects( );
        assertEquals( "incorrect size", 5, outList.size( ) );

        assertTrue( "where is bob", outList.contains( bob ) );
        assertTrue( "where is rebecca", outList.contains( rebecca ) );
        assertTrue( "where is jeannie", outList.contains( jeannie ) );

        assertTrue( outList.contains( "rebecca and jeannie are sisters" ) );
        assertTrue( outList.contains( "jeannie and rebecca are sisters" ) );

        statefulSession.release( );
    }

    /**
     * Test getObjects with ObjectFilter.
     */
    public void testGetObjectsWithFilter( ) throws Exception
    {
        this.statefulSession = engine.getStatefulRuleSession( bindUri );

        Person bob = new Person( "bob" );
        statefulSession.addObject( bob );

        Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        statefulSession.addObject( rebecca );

        Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        statefulSession.addObject( jeannie );

        // execute the rules
        statefulSession.executeRules( );
        List outList = statefulSession.getObjects( new PersonFilter( ) );
        assertEquals( "incorrect size", 3, outList.size( ) );

        assertTrue( "where is bob", outList.contains( bob ) );
        assertTrue( "where is rebecca", outList.contains( rebecca ) );
        assertTrue( "where is jeannie", outList.contains( jeannie ) );

        statefulSession.release( );
    }

    /**
     * Test executeRules.
     */
    public void testExecuteRules( ) throws Exception
    {
        // tested in testGetObjects, testGetObjectsWithFilter
    }

    /**
     * Test reset.
     */
    public void testReset( ) throws Exception
    {
        this.statefulSession = engine.getStatefulRuleSession( bindUri );
        
        Person bob = new Person( "bob" );
        Handle handle = statefulSession.addObject( bob );
        assertTrue( "where is bob", statefulSession.containsObject( handle ) );

        statefulSession.reset( );
        assertTrue(
            "bob still there", !statefulSession.containsObject( handle ) );
    }
    
    /**
     * Test executeRules with globals.
     */
    public void testExecuteRulesGlobals( ) throws Exception
    {
        java.util.Map map = new HashMap();
        java.util.Vector v = new java.util.Vector( );
        map.put("vector",  v);
        this.statefulSession = engine.getStatefulRuleSession( bindUri_globals, map );
        
        Person bob = new Person( "bob" );
        statefulSession.addObject( bob );

        Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        statefulSession.addObject( rebecca );

        Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        statefulSession.addObject( jeannie );

        // execute the rules
        statefulSession.executeRules( );
        
        List outList = statefulSession.getObjects( );

        assertEquals( "incorrect size", 5, outList.size( ) );

        assertContains( outList, bob );

        assertContains( outList, rebecca );

        assertContains( outList, jeannie );

        assertContains( outList, "rebecca and jeannie are sisters" );

        assertContains( outList, "jeannie and rebecca are sisters" );
        
        v = (java.util.Vector)map.get("vector");
        
        assertNotNull("Global Vector null", v );
        
        assertContains( v, "rebecca and jeannie are sisters" );
        
        assertContains( v, "jeannie and rebecca are sisters" );
        
        assertEquals("Vector v incorrect size", 2, v.size());

        statefulSession.release( );
    }
    
    /**
     * Test executeRules drl with dsl.
     */
    public void testExecuteRules_dsl( ) throws Exception
    {
        this.statefulSession = engine.getStatefulRuleSession( bindUri_drl );

        Person bob = new Person( "bob" );
        statefulSession.addObject( bob );

        Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        statefulSession.addObject( rebecca );

        Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        statefulSession.addObject( jeannie );

        // execute the rules
        statefulSession.executeRules( );
        
        List outList = statefulSession.getObjects( );

        assertEquals( "incorrect size", 5, outList.size( ) );

        assertContains( outList, bob );

        assertContains( outList, rebecca );

        assertContains( outList, jeannie );

        assertContains( outList, "rebecca and jeannie are sisters" );

        assertContains( outList, "jeannie and rebecca are sisters" );

        statefulSession.release( );
    }

    protected void assertContains( List expected, Object object )
    {
        if ( expected.contains( object ) )
        {
            return;
        }

        fail( object + " not in " + expected );
    }
    
    /**
     * Filter accepts only objects of type Person.
     */
    static class PersonFilter implements ObjectFilter
    {
        public Object filter( Object object )
        {
            return ( object instanceof Person ? object : null );
        }

        public void reset( )
        {
            // nothing to reset
        }
    }
    
}
