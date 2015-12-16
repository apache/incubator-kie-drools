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

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * A test for independent repository instances for different runtimes.
 */
public class MultipleRepositoryTest {

    /**
     * Do the test.
     *
     * @throws Exception
     */
    @Test
    public void testMultipleInstances() throws Exception {
        // create 2 different runtimes with different rulesets
        final RuleRuntime ruleRuntime1 = getServiceProvider( "engine1",
                                                             "multiple-engine1.drl" ).getRuleRuntime();
        final RuleRuntime ruleRuntime2 = getServiceProvider( "engine2",
                                                             "multiple-engine2.drl" ).getRuleRuntime();

        // there should be only 1
        System.out.println( ruleRuntime1.getRegistrations().size() );
        assertTrue( ruleRuntime1.getRegistrations().size() == 1 );

        // there should be only 1
        System.out.println( ruleRuntime2.getRegistrations().size() );
        assertTrue( ruleRuntime2.getRegistrations().size() == 1 );

        // execute them both for good measure...
        execute( ruleRuntime1,
                 "Engine1",
                 new Object[]{"value1"} );
        execute( ruleRuntime2,
                 "Engine2",
                 new Object[]{"value2"} );

    }

    /**
     * Create a Provider.
     *
     * @param url
     * @param rulesets
     * @return
     * @throws Exception
     */
    public RuleServiceProvider getServiceProvider(final String url,
                                                  final String ruleset) throws Exception {
        // create the provider
        final Class clazz = this.getClass().getClassLoader().loadClass( "org.drools.jsr94.rules.RuleServiceProviderImpl" );
        RuleServiceProviderManager.registerRuleServiceProvider( url,
                                                                clazz );
        final RuleServiceProvider serviceProvider = RuleServiceProviderManager.getRuleServiceProvider( url );
        final RuleAdministrator ruleAdministrator = serviceProvider.getRuleAdministrator();

        // register the ruleset
        final InputStream inStream = this.getClass().getResourceAsStream( ruleset );
        final RuleExecutionSet res1 = ruleAdministrator.getLocalRuleExecutionSetProvider( null ).createRuleExecutionSet( inStream,
                                                                                                                         null );

        inStream.close();
        final String uri = res1.getName();
        System.out.println( uri );
        ruleAdministrator.registerRuleExecutionSet( uri,
                                                    res1,
                                                    null );
        return serviceProvider;
    }

    /**
     * Execute a ruleset for the input.
     *
     * @param rt
     * @param ruleset
     * @param input
     * @throws Exception
     */
    public void execute(final RuleRuntime rt,
                        final String ruleset,
                        final Object[] input) throws Exception {
        final StatelessRuleSession srs = (StatelessRuleSession) rt.createRuleSession( ruleset,
                                                                                      null,
                                                                                      RuleRuntime.STATELESS_SESSION_TYPE );
        final List output = srs.executeRules( Arrays.asList( input ) );
        System.out.println( output );
    }
}
