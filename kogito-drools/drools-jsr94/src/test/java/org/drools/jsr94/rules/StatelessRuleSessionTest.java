/**
 * Copyright 2010 JBoss Inc
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.rules.ConfigurationException;
import javax.rules.ObjectFilter;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetCreateException;
import javax.rules.admin.RuleExecutionSetRegisterException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Test the <code>StatelessRuleSession</code> implementation.
 *
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 * @author <a href="mailto:michael.frandsen@syngenio.de">Michael Frandsen </a>
 * @see StatelessRuleSession
 */
public class StatelessRuleSessionTest {

    private ExampleRuleEngineFacade sessionBuilder;

    private final String            bindUri         = "sisters.drl";
    private final String            bindUri_drl     = "sisters_expander.drl";
    private final String            bindUri_dsl     = "sisters_expander.dsl";
    private final String            bindUri_xml     = "sisters.xml";
    private final String            bindUri_globals = "sisters_globals.drl";

    /**
     * Setup the test case.
     * normal drl, drl with dsl, drl with global
     */
    @Before
    public void setUp() throws Exception {
        this.sessionBuilder = new ExampleRuleEngineFacade();
        this.sessionBuilder.addRuleExecutionSet( this.bindUri,
                                                 StatelessRuleSessionTest.class.getResourceAsStream( this.bindUri ) );

        final Map map = new HashMap();
        final Reader reader = new InputStreamReader( StatelessRuleSessionTest.class.getResourceAsStream( this.bindUri_dsl ) );

        map.put( "dsl",
                 this.getDSLText( reader ).toString() );
        this.sessionBuilder.addRuleExecutionSet( this.bindUri_drl,
                                                 StatelessRuleSessionTest.class.getResourceAsStream( this.bindUri_drl ),
                                                 map );

        final Map map_xml = new HashMap();
        map_xml.put( "source",
                     "xml" );
        this.sessionBuilder.addRuleExecutionSet( this.bindUri_xml,
                                                 StatelessRuleSessionTest.class.getResourceAsStream( this.bindUri_xml ),
                                                 map_xml );

        this.sessionBuilder.addRuleExecutionSet( this.bindUri_globals,
                                                 StatelessRuleSessionTest.class.getResourceAsStream( this.bindUri_globals ) );

    }

    /*
     * Taken from DRLParser
     */
    private StringBuffer getDSLText(final Reader reader) throws IOException {
        final StringBuffer text = new StringBuffer();

        final char[] buf = new char[1024];
        int len = 0;

        while ( (len = reader.read( buf )) >= 0 ) {
            text.append( buf,
                         0,
                         len );
        }
        return text;
    }

    @Test
    public void testCreateRuleExecutionSetFromStreamWithXml() {

        try {
            final Map map = new HashMap();
            map.put( "source",
                     "xml" );

            RuleServiceProvider ruleServiceProvider;
            RuleServiceProviderManager.registerRuleServiceProvider( "http://drools.org/",
                                                                    RuleServiceProviderImpl.class );

            ruleServiceProvider = RuleServiceProviderManager.getRuleServiceProvider( "http://drools.org/" );

            LocalRuleExecutionSetProvider ruleSetProvider = ruleServiceProvider.getRuleAdministrator().getLocalRuleExecutionSetProvider( null );
            final RuleExecutionSet ruleExecutionSet = ruleSetProvider.createRuleExecutionSet( StatelessRuleSessionTest.class.getResourceAsStream( this.bindUri_xml ),
                                                                                              map );
            assertNotNull( ruleExecutionSet );
        } catch ( RemoteException e ) {
            fail();
        } catch ( ConfigurationException e ) {
            fail();
        } catch ( RuleExecutionSetCreateException e ) {
            fail();
        } catch ( IOException e ) {
            fail();
        }
    }

    @Test
    public void testCreateRuleExecutionSetFromStreamReaderWithXml() {
        try {
            final Map map = new HashMap();
            map.put( "source",
                     "xml" );

            RuleServiceProvider ruleServiceProvider;
            RuleServiceProviderManager.registerRuleServiceProvider( "http://drools.org/",
                                                                    RuleServiceProviderImpl.class );

            ruleServiceProvider = RuleServiceProviderManager.getRuleServiceProvider( "http://drools.org/" );

            LocalRuleExecutionSetProvider ruleSetProvider = ruleServiceProvider.getRuleAdministrator().getLocalRuleExecutionSetProvider( null );
            final Reader ruleReader = new InputStreamReader( StatelessRuleSessionTest.class.getResourceAsStream( this.bindUri_xml ) );
            final RuleExecutionSet ruleExecutionSet = ruleSetProvider.createRuleExecutionSet( ruleReader,
                                                                                              map );
            assertNotNull( ruleExecutionSet );

        } catch ( RemoteException e ) {
            fail();
        } catch ( ConfigurationException e ) {
            fail();
        } catch ( RuleExecutionSetCreateException e ) {
            fail();
        } catch ( IOException e ) {
            fail();
        }
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
        final StatelessRuleSession statelessSession = this.sessionBuilder.getStatelessRuleSession( this.bindUri_globals,
                                                                                                   map );

        final List inObjects = new ArrayList();

        final Person bob = new Person( "bob" );
        inObjects.add( bob );

        final Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        final Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        // execute the rules
        final List outList = statelessSession.executeRules( inObjects );

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

        statelessSession.release();
    }

    /**
     * Test executeRules with normal drl.
     */
    @Test
    public void testExecuteRules() throws Exception {
        final StatelessRuleSession statelessSession = this.sessionBuilder.getStatelessRuleSession( this.bindUri );

        final List inObjects = new ArrayList();

        final Person bob = new Person( "bob" );
        inObjects.add( bob );

        final Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        final Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        // execute the rules
        final List outList = statelessSession.executeRules( inObjects );

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

        statelessSession.release();
    }

    /**
     * Test executeRules with normal drl.
     */
    @Test
    public void testExecuteRulesWithXml() throws Exception {
        final StatelessRuleSession statelessSession = this.sessionBuilder.getStatelessRuleSession( this.bindUri_xml );

        final List inObjects = new ArrayList();

        final Person bob = new Person( "bob" );
        inObjects.add( bob );

        final Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        final Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        // execute the rules
        final List outList = statelessSession.executeRules( inObjects );

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

        statelessSession.release();
    }

    /**
     * Test executeRules drl with dsl.
     */
    public void xxxtestExecuteRules_dsl() throws Exception {
        // @FIXME
        final StatelessRuleSession statelessSession = this.sessionBuilder.getStatelessRuleSession( this.bindUri_drl );

        final List inObjects = new ArrayList();

        final Person bob = new Person( "bob" );
        inObjects.add( bob );

        final Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        final Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        // execute the rules
        final List outList = statelessSession.executeRules( inObjects );

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

        statelessSession.release();
    }

    /**
     * Test executeRules with ObjectFilter.
     */
    @Test
    public void testExecuteRulesWithFilter() throws Exception {
        final StatelessRuleSession statelessSession = this.sessionBuilder.getStatelessRuleSession( this.bindUri );

        final List inObjects = new ArrayList();

        final Person bob = new Person( "bob" );
        inObjects.add( bob );

        final Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        final Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        // execute the rules
        final List outList = statelessSession.executeRules( inObjects,
                                                            new PersonFilter() );
        assertEquals( "incorrect size",
                      3,
                      outList.size() );

        assertTrue( "where is bob",
                    outList.contains( bob ) );

        assertTrue( "where is rebecca",
                    outList.contains( rebecca ) );

        assertTrue( "where is jeannie",
                    outList.contains( jeannie ) );
    }

    /**
     * Test executeRules with ObjectFilter drl with dsl.
     */
    @Test
    public void testExecuteRulesWithFilter_dsl() throws Exception {
        final StatelessRuleSession statelessSession = this.sessionBuilder.getStatelessRuleSession( this.bindUri_drl );

        final List inObjects = new ArrayList();

        final Person bob = new Person( "bob" );
        inObjects.add( bob );

        final Person rebecca = new Person( "rebecca" );
        rebecca.addSister( "jeannie" );
        inObjects.add( rebecca );

        final Person jeannie = new Person( "jeannie" );
        jeannie.addSister( "rebecca" );
        inObjects.add( jeannie );

        // execute the rules
        final List outList = statelessSession.executeRules( inObjects,
                                                            new PersonFilter() );
        assertEquals( "incorrect size",
                      3,
                      outList.size() );

        assertTrue( "where is bob",
                    outList.contains( bob ) );

        assertTrue( "where is rebecca",
                    outList.contains( rebecca ) );

        assertTrue( "where is jeannie",
                    outList.contains( jeannie ) );
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

    protected void assertContains(final List expected,
                                  final Object object) {
        if ( expected.contains( object ) ) {
            return;
        }

        fail( object + " not in " + expected );
    }
}
