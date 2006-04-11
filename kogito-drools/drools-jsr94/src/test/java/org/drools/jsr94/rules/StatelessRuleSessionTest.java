package org.drools.jsr94.rules;

/*
 * Copyright 2005 JBoss Inc
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.rules.ObjectFilter;
import javax.rules.StatelessRuleSession;

import junit.framework.TestCase;

/**
 * Test the <code>StatelessRuleSession</code> implementation.
 *
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 * @author <a href="mailto:michael.frandsen@syngenio.de">Michael Frandsen </a>
 * @see StatelessRuleSession
 */
public class StatelessRuleSessionTest extends TestCase
{

    private ExampleRuleEngineFacade sessionBuilder;

    private String bindUri = "sisters.drl";
    private String bindUri_drl = "sisters_expander.drl";
    private String bindUri_dsl = "sisters_expander.dsl";
    private String bindUri_globals = "sisters_globals.drl";

    /**
     * Setup the test case.
     * normal drl, drl with dsl, drl with global
     */
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        sessionBuilder = new ExampleRuleEngineFacade( );
        sessionBuilder.addRuleExecutionSet( bindUri,
            StatelessRuleSessionTest.class.getResourceAsStream( bindUri ) );
        
        
        Map map = new HashMap(); 
        Reader reader = new InputStreamReader(StatelessRuleSessionTest.class.getResourceAsStream( bindUri_dsl ) );
        
        
        map.put("dsl",this.getDSLText(reader).toString());
        sessionBuilder.addRuleExecutionSet( bindUri_drl,
                StatelessRuleSessionTest.class.getResourceAsStream( bindUri_drl ), map );
        
        sessionBuilder.addRuleExecutionSet( bindUri_globals,
                StatelessRuleSessionTest.class.getResourceAsStream( bindUri_globals ) );
          
        
    }
    /*
     * Taken from DRLParser
     */
    private StringBuffer getDSLText(Reader reader) throws IOException {
        StringBuffer text = new StringBuffer();

	        char[] buf = new char[1024];
	        int len = 0;

	        while ( (len = reader.read( buf )) >= 0 ) {
	            text.append( buf,
	                         0,
	                         len );
	        }
        return text;
    }

    /**
     * Test executeRules with globals.
     */
    public void testExecuteRulesGlobals( ) throws Exception
    {
    	java.util.Map map = new HashMap();
    	java.util.Vector v = new java.util.Vector( );
    	map.put("vector",  v);
    	StatelessRuleSession statelessSession =
            sessionBuilder.getStatelessRuleSession( bindUri_globals, map );
    	
        List inObjects = new ArrayList( );

        Person bob = new Person( "bob" );
        inObjects.add( bob );

        Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        // execute the rules
        List outList = statelessSession.executeRules( inObjects );

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

        statelessSession.release( );
    }
    /**
     * Test executeRules with normal drl.
     */
    public void testExecuteRules( ) throws Exception
    {
    	StatelessRuleSession statelessSession =
            sessionBuilder.getStatelessRuleSession( bindUri );
    	
        List inObjects = new ArrayList( );

        Person bob = new Person( "bob" );
        inObjects.add( bob );

        Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        // execute the rules
        List outList = statelessSession.executeRules( inObjects );

        assertEquals( "incorrect size", 5, outList.size( ) );

        assertContains( outList, bob );

        assertContains( outList, rebecca );

        assertContains( outList, jeannie );

        assertContains( outList, "rebecca and jeannie are sisters" );

        assertContains( outList, "jeannie and rebecca are sisters" );

        statelessSession.release( );
    }
    /**
     * Test executeRules drl with dsl.
     */
    public void testExecuteRules_dsl( ) throws Exception
    {
    	StatelessRuleSession statelessSession =
            sessionBuilder.getStatelessRuleSession( bindUri_drl );
    	
        List inObjects = new ArrayList( );

        Person bob = new Person( "bob" );
        inObjects.add( bob );

        Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        // execute the rules
        List outList = statelessSession.executeRules( inObjects );

        assertEquals( "incorrect size", 5, outList.size( ) );

        assertContains( outList, bob );

        assertContains( outList, rebecca );

        assertContains( outList, jeannie );

        assertContains( outList, "rebecca and jeannie are sisters" );

        assertContains( outList, "jeannie and rebecca are sisters" );

        statelessSession.release( );
    }

    /**
     * Test executeRules with ObjectFilter.
     */
    public void testExecuteRulesWithFilter( ) throws Exception
    {
    	StatelessRuleSession statelessSession =
            sessionBuilder.getStatelessRuleSession( bindUri );
    	
        List inObjects = new ArrayList( );

        Person bob = new Person( "bob" );
        inObjects.add( bob );

        Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        // execute the rules
        List outList = statelessSession.executeRules(
            inObjects, new PersonFilter( ) );
        assertEquals( "incorrect size", 3, outList.size( ) );

        assertTrue( "where is bob", outList.contains( bob ) );
        
        assertTrue( "where is rebecca", outList.contains( rebecca ) );
        
        assertTrue( "where is jeannie", outList.contains( jeannie ) );
    }
    
    /**
     * Test executeRules with ObjectFilter drl with dsl.
     */
    public void testExecuteRulesWithFilter_dsl( ) throws Exception
    {
    	StatelessRuleSession statelessSession =
            sessionBuilder.getStatelessRuleSession( bindUri_drl );
    	
        List inObjects = new ArrayList( );

        Person bob = new Person( "bob" );
        inObjects.add( bob );

        Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        // execute the rules
        List outList = statelessSession.executeRules(
            inObjects, new PersonFilter( ) );
        assertEquals( "incorrect size", 3, outList.size( ) );

        assertTrue( "where is bob", outList.contains( bob ) );
        
        assertTrue( "where is rebecca", outList.contains( rebecca ) );
        
        assertTrue( "where is jeannie", outList.contains( jeannie ) );
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

    protected void assertContains( List expected, Object object )
    {
        if ( expected.contains( object ) )
        {
            return;
        }

        fail( object + " not in " + expected );
    }
}
