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

package org.drools.jsr94.rules;

/*
 * $Id: ExampleRuleEngineFacade.java,v 1.5 2004/11/17 03:09:50 dbarnett Exp $
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
 *
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
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
