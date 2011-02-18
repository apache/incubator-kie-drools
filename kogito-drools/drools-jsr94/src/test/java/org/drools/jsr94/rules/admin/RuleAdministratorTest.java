/*
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

package org.drools.jsr94.rules.admin;

import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSetRegisterException;

import org.drools.jsr94.rules.RuleEngineTestBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the RuleRuntime implementation.
 *
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 */
public class RuleAdministratorTest extends RuleEngineTestBase {
    private RuleAdministrator ruleAdministrator;

    /**
     * Obtain an instance of <code>RuleAdministrator</code>.
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.ruleAdministrator = this.ruleServiceProvider.getRuleAdministrator();
    }

    /**
     * Test getRuleExecutionSetProvider.
     */
    @Test
    public void testRuleExecutionSetProvider() throws Exception {
        final RuleExecutionSetProvider ruleExecutionSetProvider = this.ruleAdministrator.getRuleExecutionSetProvider( null );
        assertNotNull( "cannot obtain RuleExecutionSetProvider",
                       ruleExecutionSetProvider );
    }

    /**
     * Test getLocalRuleExecutionSetProvider.
     */
    @Test
    public void testLocalRuleExecutionSetProvider() throws Exception {
        final LocalRuleExecutionSetProvider localRuleExecutionSetProvider = this.ruleAdministrator.getLocalRuleExecutionSetProvider( null );
        assertNotNull( "cannot obtain LocalRuleExecutionSetProvider",
                       localRuleExecutionSetProvider );
    }

    /**
     * Test registerRuleExecutionSet.
     */
    @Test
    public void testRegisterRuleExecutionSet() throws Exception {
        try {
            // that it works is tested elsewhere
            this.ruleAdministrator.registerRuleExecutionSet( "test URI",
                                                             null,
                                                             null );
            fail( "RuleExecutionSetRegisterException expected" );
        } catch ( final RuleExecutionSetRegisterException ex ) {
            // ignore exception
        }
    }

    /**
     * Test deregisterRuleExecutionSet.
     */
    @Test
    public void testDeregisterRuleExecutionSet() throws Exception {
        try {
            // that it works is tested else where
            this.ruleAdministrator.deregisterRuleExecutionSet( "test URI",
                                                               null );
            fail( "RuleExecutionSetUnregisterException expected" );
        } catch ( final RuleExecutionSetDeregistrationException ex ) {
            // ignore exception
        }
    }
}
