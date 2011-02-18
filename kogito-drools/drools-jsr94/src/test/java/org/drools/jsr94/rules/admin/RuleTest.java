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

import java.io.InputStream;
import java.util.List;

import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.Rule;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

import org.drools.jsr94.rules.RuleEngineTestBase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the LocalRuleExecutionSetProvider implementation.
 */
public class RuleTest extends RuleEngineTestBase {
    private RuleAdministrator             ruleAdministrator;

    private LocalRuleExecutionSetProvider ruleSetProvider;

    /**
     * Setup the test case.
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.ruleAdministrator = this.ruleServiceProvider.getRuleAdministrator();
        this.ruleSetProvider = this.ruleAdministrator.getLocalRuleExecutionSetProvider( null );
    }

    /**
     * Test rule name and description.
     */
    @Test
    public void testRule() throws Exception {
        final InputStream in = RuleEngineTestBase.class.getResourceAsStream( this.bindUri );
        final RuleExecutionSet ruleExecutionSet = this.ruleSetProvider.createRuleExecutionSet( in,
                                                                                               null );
        final List rules = ruleExecutionSet.getRules();
        assertEquals( "number of rules",
                      1,
                      rules.size() );

        final Rule rule01 = (Rule) ruleExecutionSet.getRules().get( 0 );
        assertEquals( "rule name",
                      "FindSisters",
                      rule01.getName() );
        assertEquals( "rule description",
                      "FindSisters",
                      rule01.getDescription() );
    }
}
