package org.drools.jsr94.benchmark.drools;

/*
 * $Id: DroolsBenchmarkTestCase.java,v 1.8 2005/11/25 02:11:34 mproctor Exp $
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

import java.io.InputStream;
import java.util.List;

import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSet;

import org.drools.jsr94.benchmark.BenchmarkTestBase;

/**
 * Uses the RuleServiceProviderImpl for Drools to solve the Miss Manners
 * problem.
 *
 * Miss Manners is a program which handles the problem of finding an acceptable
 * seating arrangement for guests at a dinner party. It will attempt to match
 * people with the same hobbies, and to seat everyone next to a member of the
 * opposite sex. Manners is a small program, which has only few rules, and
 * employs a depth-first search approach to the problem.
 *
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 */
public class DroolsBenchmarkTestCase extends BenchmarkTestBase
{
    /** Drools <code>RuleServiceProvider</code> URI. */
    public static final String RULE_SERVICE_PROVIDER = "http://drools.org/";

    /** manners URI */
    public static final String RULE_URI              = "manners.drl";

    /**
     * Setup the test case.
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        // obtain the RuleServiceProvider
        RuleServiceProviderManager.registerRuleServiceProvider( RULE_SERVICE_PROVIDER,
                                                                org.drools.jsr94.rules.RuleServiceProviderImpl.class );
        ruleServiceProvider = RuleServiceProviderManager.getRuleServiceProvider( RULE_SERVICE_PROVIDER );
        ruleAdministrator = ruleServiceProvider.getRuleAdministrator();

        // load the rules and register them
        LocalRuleExecutionSetProvider ruleSetProvider = ruleAdministrator.getLocalRuleExecutionSetProvider( null );
        InputStream rules = DroolsBenchmarkTestCase.class.getResourceAsStream( RULE_URI );
        RuleExecutionSet ruleExecutionSet = ruleSetProvider.createRuleExecutionSet( rules,
                                                                                    null );
        ruleAdministrator.registerRuleExecutionSet( RULE_URI,
                                                    ruleExecutionSet,
                                                    null );

        RuleRuntime ruleRuntime = ruleServiceProvider.getRuleRuntime();
        statelessRuleSession = (StatelessRuleSession) ruleRuntime.createRuleSession( RULE_URI,
                                                                                     null,
                                                                                     RuleRuntime.STATELESS_SESSION_TYPE );
    }

    /**
     * Tear down the test case
     */
    protected void tearDown() throws Exception
    {
        statelessRuleSession.release();
        ruleAdministrator.deregisterRuleExecutionSet( RULE_URI,
                                                      null );
        super.tearDown();
    }

    public void testMissManners16() throws Exception
    {
        List inList = getInputObjects( BenchmarkTestBase.class.getResourceAsStream( "manners16.dat" ) );
        List outList = statelessRuleSession.executeRules( inList );
        assertEquals( "seated guests",
                      16,
                      validateResults( inList,
                                       outList ) );
    }

    public void testMissManners32() throws Exception
    {
        List inList = getInputObjects( BenchmarkTestBase.class.getResourceAsStream( "manners32.dat" ) );
        List outList = statelessRuleSession.executeRules( inList );
        assertEquals( "seated guests",
                      32,
                      validateResults( inList,
                                       outList ) );
    }

    public void testMissManners64() throws Exception
    {
        List inList = getInputObjects( BenchmarkTestBase.class.getResourceAsStream( "manners64.dat" ) );
        List outList = statelessRuleSession.executeRules( inList );
        assertEquals( "seated guests",
                      64,
                      validateResults( inList,
                                       outList ) );
    }

    public void testMissManners128() throws Exception
    {
        List inList = getInputObjects( BenchmarkTestBase.class.getResourceAsStream( "manners128.dat" ) );
        List outList = statelessRuleSession.executeRules( inList );
        assertEquals( "seated guests",
                      128,
                      validateResults( inList,
                                       outList ) );
    }
}
