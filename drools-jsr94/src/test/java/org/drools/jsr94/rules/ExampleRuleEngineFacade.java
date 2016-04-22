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
import java.io.InputStreamReader;
import java.io.Reader;

import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.RuleServiceProviderManager;
import javax.rules.StatefulRuleSession;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;

/**
 * Builds up the JSR94 object structure. It'll simplify the task of building a
 * <code>RuleExecutionSet</code> and associated <code>RuntimeSession</code>
 * objects from a given <code>InputStream</code>.
 */
public class ExampleRuleEngineFacade {
    public static final String            RULE_SERVICE_PROVIDER = "http://drools.org/";

    private RuleAdministrator             ruleAdministrator;

    private RuleServiceProvider           ruleServiceProvider;

    private LocalRuleExecutionSetProvider ruleSetProvider;

    private RuleRuntime                   ruleRuntime;

    // configuration parameters
    String                                ruleFilesDirectory;

    String                                ruleFilesIncludes;

    public ExampleRuleEngineFacade() throws Exception {
        RuleServiceProviderManager.registerRuleServiceProvider( ExampleRuleEngineFacade.RULE_SERVICE_PROVIDER,
                                                                RuleServiceProviderImpl.class );

        this.ruleServiceProvider = RuleServiceProviderManager.getRuleServiceProvider( ExampleRuleEngineFacade.RULE_SERVICE_PROVIDER );

        this.ruleAdministrator = this.ruleServiceProvider.getRuleAdministrator();

        this.ruleSetProvider = this.ruleAdministrator.getLocalRuleExecutionSetProvider( null );
    }

    public void addRuleExecutionSet(final String bindUri,
                                    final InputStream resourceAsStream) throws Exception {
        final Reader ruleReader = new InputStreamReader( resourceAsStream );

        final RuleExecutionSet ruleExecutionSet = this.ruleSetProvider.createRuleExecutionSet( ruleReader,
                                                                                               null );

        this.ruleAdministrator.registerRuleExecutionSet( bindUri,
                                                         ruleExecutionSet,
                                                         null );
    }

    public void addRuleExecutionSet(final String bindUri,
                                    final InputStream resourceAsStream,
                                    final java.util.Map properties) throws Exception {
        final Reader ruleReader = new InputStreamReader( resourceAsStream );

        final RuleExecutionSet ruleExecutionSet = this.ruleSetProvider.createRuleExecutionSet( ruleReader,
                                                                                               properties );

        this.ruleAdministrator.registerRuleExecutionSet( bindUri,
                                                         ruleExecutionSet,
                                                         properties );
    }

    /**
     * Returns a named <code>StatelessRuleSession</code>.
     *
     * 
     * @return StatelessRuleSession
     * @throws Exception
     */
    public StatelessRuleSession getStatelessRuleSession(final String key,
                                                        final java.util.Map properties) throws Exception {
        this.ruleRuntime = this.ruleServiceProvider.getRuleRuntime();

        return (StatelessRuleSession) this.ruleRuntime.createRuleSession( key,
                                                                          properties,
                                                                          RuleRuntime.STATELESS_SESSION_TYPE );
    }

    /**
     * Returns a named <code>StatelessRuleSession</code>.
     *
     * @param key
     * @return StatelessRuleSession
     * @throws Exception
     */
    public StatelessRuleSession getStatelessRuleSession(final String key) throws Exception {
        return this.getStatelessRuleSession( key,
                                             null );
    }

    public StatefulRuleSession getStatefulRuleSession(final String key) throws Exception {
        return this.getStatefulRuleSession( key,
                                            null );
    }

    public StatefulRuleSession getStatefulRuleSession(final String key,
                                                      final java.util.Map properties) throws Exception {
        this.ruleRuntime = this.ruleServiceProvider.getRuleRuntime();

        return (StatefulRuleSession) this.ruleRuntime.createRuleSession( key,
                                                                         properties,
                                                                         RuleRuntime.STATEFUL_SESSION_TYPE );
    }

    public RuleServiceProvider getRuleServiceProvider() {
        return this.ruleServiceProvider;
    }
}
