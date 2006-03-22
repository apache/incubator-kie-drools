package org.drools.jsr94.rules;

/*
 * $Id: RuleRuntimeTestCase.java,v 1.7 2005/02/04 02:13:38 mproctor Exp $
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

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;

import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.StatefulRuleSession;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

/**
 * Test the RuleRuntime implementation.
 *
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 */
public class RuleRuntimeTestCase extends RuleEngineTestBase
{
    private LocalRuleExecutionSetProvider ruleSetProvider;

    private RuleAdministrator ruleAdministrator;

    private String RULES_RESOURCE;

    /**
     * Setup the test case.
     */
    protected void setUp( ) throws Exception
    {
        super.setUp( );
        RULES_RESOURCE = bindUri;
        ruleAdministrator = ruleServiceProvider.getRuleAdministrator( );
        ruleSetProvider =
            ruleAdministrator.getLocalRuleExecutionSetProvider( null );
    }

    /**
     * Test createRuleSession.
     */
    public void testCreateRuleStatelessRuleSession( ) throws Exception
    {
        RuleRuntime ruleRuntime = ruleServiceProvider.getRuleRuntime( );
        assertNotNull( "cannot obtain RuleRuntime", ruleRuntime );

        // expect RuleExecutionSetNotFoundException
        try
        {
            ruleRuntime.createRuleSession( "someUri",
                                           null,
                                           RuleRuntime.STATELESS_SESSION_TYPE );
            fail( "RuleExecutionSetNotFoundException expected" );
        }
        catch ( RuleExecutionSetNotFoundException ex )
        {
            // ignore exception
        }

        // read rules and register with administrator
        Reader ruleReader = new InputStreamReader(
            RuleRuntimeTestCase.class.getResourceAsStream( RULES_RESOURCE ) );
        RuleExecutionSet ruleSet =
            ruleSetProvider.createRuleExecutionSet( ruleReader, null );
        ruleAdministrator.registerRuleExecutionSet(
            RULES_RESOURCE, ruleSet, null );

        StatelessRuleSession statelessRuleSession = ( StatelessRuleSession ) ruleRuntime.createRuleSession( RULES_RESOURCE,
                                                                                                            null,
                                                                                                            RuleRuntime.STATELESS_SESSION_TYPE );
        assertNotNull( "cannot obtain StatelessRuleSession",
                       statelessRuleSession );

        ruleAdministrator.deregisterRuleExecutionSet( RULES_RESOURCE, null );
    }

    /**
     * Test createRuleSession.
     */
    public void testCreateRuleStatefulRuleSession( ) throws Exception
    {
        RuleRuntime ruleRuntime = ruleServiceProvider.getRuleRuntime( );
        assertNotNull( "cannot obtain RuleRuntime", ruleRuntime );

        // expect RuleExecutionSetNotFoundException
        try
        {
            ruleRuntime.createRuleSession( "someUri",
                                           null,
                                           RuleRuntime.STATEFUL_SESSION_TYPE );
            fail( "RuleExecutionSetNotFoundException expected" );
        }
        catch ( RuleExecutionSetNotFoundException ex )
        {
            // ignore exception
        }

        // read rules and register with administrator
        Reader ruleReader = new InputStreamReader(
            RuleRuntimeTestCase.class.getResourceAsStream( RULES_RESOURCE ) );
        RuleExecutionSet ruleSet =
            ruleSetProvider.createRuleExecutionSet( ruleReader, null );
        ruleAdministrator.registerRuleExecutionSet(
            RULES_RESOURCE, ruleSet, null );

        StatefulRuleSession statefulRuleSession = ( StatefulRuleSession )
                    ruleRuntime.createRuleSession( RULES_RESOURCE,
                                                   null,
                                                   RuleRuntime.STATEFUL_SESSION_TYPE );
        assertNotNull(
            "cannot obtain StatefulRuleSession", statefulRuleSession );

        ruleAdministrator.deregisterRuleExecutionSet( RULES_RESOURCE, null );
    }

    /**
     * Test getRegistrations.
     */
    public void testGetRegistrations( ) throws Exception
    {
        RuleRuntime ruleRuntime = ruleServiceProvider.getRuleRuntime( );
        assertNotNull( "cannot obtain RuleRuntime", ruleRuntime );

        // read rules and register with administrator
        Reader ruleReader = new InputStreamReader(
            RuleRuntimeTestCase.class.getResourceAsStream( RULES_RESOURCE ) );
        RuleExecutionSet ruleSet =
            ruleSetProvider.createRuleExecutionSet( ruleReader, null );
        ruleAdministrator.registerRuleExecutionSet(
            RULES_RESOURCE, ruleSet, null );

        List list = ruleRuntime.getRegistrations( );
        assertTrue( "no registrations found", list.size( ) > 0 );

        ruleAdministrator.deregisterRuleExecutionSet( RULES_RESOURCE, null );
    }
}
