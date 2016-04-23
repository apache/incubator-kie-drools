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

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatefulRuleSession;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

import org.drools.core.RuleBaseConfiguration;
import org.drools.jsr94.rules.decisiontables.SpreadsheetIntegrationExampleTest;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test the RuleRuntime implementation.
 */
public class RuleRuntimeTest extends RuleEngineTestBase {
    private LocalRuleExecutionSetProvider ruleSetProvider;

    private RuleAdministrator             ruleAdministrator;

    private String                        RULES_RESOURCE;

    /**
     * Setup the test case.
     */
    @Before
    public void setUp() throws Exception {
        super.setUp();
        this.RULES_RESOURCE = this.bindUri;
        this.ruleAdministrator = this.ruleServiceProvider.getRuleAdministrator();
        this.ruleSetProvider = this.ruleAdministrator.getLocalRuleExecutionSetProvider( null );
    }

    /**
     * Test createRuleSession.
     */
    @Test
    public void testCreateRuleStatelessRuleSession() throws Exception {
        final RuleRuntime ruleRuntime = this.ruleServiceProvider.getRuleRuntime();
        assertNotNull( "cannot obtain RuleRuntime",
                       ruleRuntime );

        // expect RuleExecutionSetNotFoundException
        try {
            ruleRuntime.createRuleSession( "someUri",
                                           null,
                                           RuleRuntime.STATELESS_SESSION_TYPE );
            fail( "RuleExecutionSetNotFoundException expected" );
        } catch ( final RuleExecutionSetNotFoundException ex ) {
            // ignore exception
        }

        // read rules and register with administrator
        final Reader ruleReader = new InputStreamReader( RuleRuntimeTest.class.getResourceAsStream( this.RULES_RESOURCE ) );
        final RuleExecutionSet ruleSet = this.ruleSetProvider.createRuleExecutionSet( ruleReader,
                                                                                      null );
        this.ruleAdministrator.registerRuleExecutionSet( this.RULES_RESOURCE,
                                                         ruleSet,
                                                         null );

        final StatelessRuleSession statelessRuleSession = (StatelessRuleSession) ruleRuntime.createRuleSession( this.RULES_RESOURCE,
                                                                                                                null,
                                                                                                                RuleRuntime.STATELESS_SESSION_TYPE );
        assertNotNull( "cannot obtain StatelessRuleSession",
                       statelessRuleSession );

        this.ruleAdministrator.deregisterRuleExecutionSet( this.RULES_RESOURCE,
                                                           null );
    }

    /**
     * Test createRuleSession.
     */
    @Test
    public void testCreateRuleStatefulRuleSession() throws Exception {
        final RuleRuntime ruleRuntime = this.ruleServiceProvider.getRuleRuntime();
        assertNotNull( "cannot obtain RuleRuntime",
                       ruleRuntime );

        // expect RuleExecutionSetNotFoundException
        try {
            ruleRuntime.createRuleSession( "someUri",
                                           null,
                                           RuleRuntime.STATEFUL_SESSION_TYPE );
            fail( "RuleExecutionSetNotFoundException expected" );
        } catch ( final RuleExecutionSetNotFoundException ex ) {
            // ignore exception
        }

        // read rules and register with administrator
        final Reader ruleReader = new InputStreamReader( RuleRuntimeTest.class.getResourceAsStream( this.RULES_RESOURCE ) );
        final RuleExecutionSet ruleSet = this.ruleSetProvider.createRuleExecutionSet( ruleReader,
                                                                                      null );
        this.ruleAdministrator.registerRuleExecutionSet( this.RULES_RESOURCE,
                                                         ruleSet,
                                                         null );

        final StatefulRuleSession statefulRuleSession = (StatefulRuleSession) ruleRuntime.createRuleSession( this.RULES_RESOURCE,
                                                                                                             null,
                                                                                                             RuleRuntime.STATEFUL_SESSION_TYPE );
        assertNotNull( "cannot obtain StatefulRuleSession",
                       statefulRuleSession );

        this.ruleAdministrator.deregisterRuleExecutionSet( this.RULES_RESOURCE,
                                                           null );
    }

    /**
     * Test getRegistrations.
     */
    @Test
    public void testGetRegistrations() throws Exception {
        final RuleRuntime ruleRuntime = this.ruleServiceProvider.getRuleRuntime();
        assertNotNull( "cannot obtain RuleRuntime",
                       ruleRuntime );

        // read rules and register with administrator
        final Reader ruleReader = new InputStreamReader( RuleRuntimeTest.class.getResourceAsStream( this.RULES_RESOURCE ) );
        final RuleExecutionSet ruleSet = this.ruleSetProvider.createRuleExecutionSet( ruleReader,
                                                                                      null );
        this.ruleAdministrator.registerRuleExecutionSet( this.RULES_RESOURCE,
                                                         ruleSet,
                                                         null );

        final List list = ruleRuntime.getRegistrations();
        assertTrue( "no registrations found",
                    list.size() > 0 );

        this.ruleAdministrator.deregisterRuleExecutionSet( this.RULES_RESOURCE,
                                                           null );
    }

    @Test
    public void testRuleBaseConfigurationConstant() throws Exception {
        // JBRULES-1061
        
        Map properties = new HashMap();
        properties.put( Constants.RES_SOURCE,
                        Constants.RES_SOURCE_TYPE_DECISION_TABLE );

        properties.put( Constants.RES_RULEBASE_CONFIG,
                        new RuleBaseConfiguration() );

        RuleServiceProviderManager.registerRuleServiceProvider( ExampleRuleEngineFacade.RULE_SERVICE_PROVIDER,
                                                                RuleServiceProviderImpl.class );

        RuleServiceProvider ruleServiceProvider = RuleServiceProviderManager.getRuleServiceProvider( ExampleRuleEngineFacade.RULE_SERVICE_PROVIDER );
        RuleAdministrator ruleAdministrator = ruleServiceProvider.getRuleAdministrator();
        LocalRuleExecutionSetProvider ruleSetProvider = ruleAdministrator.getLocalRuleExecutionSetProvider( null );

        try {
            RuleExecutionSet ruleExecutionSet = ruleSetProvider.createRuleExecutionSet( SpreadsheetIntegrationExampleTest.class.getResourceAsStream( "IntegrationExampleTest.xls" ),
                                                                                        properties );
        } catch (Exception e) {
            // fail should not throw an Excetpion
        }
    }
}
