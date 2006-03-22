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

import javax.rules.RuleRuntime;
import javax.rules.RuleServiceProvider;
import javax.rules.admin.RuleAdministrator;

import org.drools.jsr94.rules.admin.RuleAdministratorImpl;
import org.drools.jsr94.rules.admin.RuleExecutionSetRepository;

/**
 * This class provides access to the <code>RuleRuntime</code> and
 * <code>RuleAdministrator</code> implementation supplied by Drools when
 * running under J2SE. <p/> This class should be used in environments without a
 * JNDI provider - typically when writing standalone J2SE clients. Within the
 * J2EE environment the <code>RuleServiceProvider</code> implementation class
 * provided by Drools should be retrieved using a JNDI lookup. <p/> This class
 * should be constructed using the
 * <code>RuleServiceProviderManager.getRuleServiceProvider</code> method.
 * 
 * @see RuleRuntimeImpl
 * @see RuleAdministratorImpl
 * @see RuleServiceProvider
 * @see javax.rules.RuleServiceProviderManager#getRuleServiceProvider(String)
 * 
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 */
public class RuleServiceProviderImpl extends RuleServiceProvider {
    /** An instance of <code>RuleRuntimeImpl</code>. */
    private RuleRuntime                ruleRuntime;

    /** An instance of <code>RuleAdministratorImpl</code>. */
    private RuleAdministrator          ruleAdministrator;

    private RuleExecutionSetRepository repository;

    /**
     * Create a new <code>RuleServiceProviderImpl</code>.
     */
    public RuleServiceProviderImpl() {
        // no special initialization required
    }

    /**
     * @return
     */
    public synchronized RuleExecutionSetRepository getRepository() {
        if ( this.repository != null ) {
            return this.repository;
        }
        return this.repository = new RuleExecutionSetRepository();
    }

    /**
     * Returns a class instance of <code>RuleRuntime</code>. Specifically an
     * instance of the Drools <code>RuleRuntimeImpl</code> is returned.
     * 
     * @return an instance of <code>RuleRuntime</code>
     */
    public synchronized RuleRuntime getRuleRuntime() {
        if ( this.ruleRuntime != null ) {
            return this.ruleRuntime;
        }
        return this.ruleRuntime = new RuleRuntimeImpl( getRepository() );
    }

    /**
     * Returns a class instance of <code>RuleAdministrator</code>.
     * Specifically an instance of the Drools <code>RuleAdministratorImpl</code>
     * is returned.
     * 
     * @return an instance of <code>RuleAdministrator</code>
     */
    public synchronized RuleAdministrator getRuleAdministrator() {
        if ( this.ruleAdministrator != null ) {
            return this.ruleAdministrator;
        }
        return this.ruleAdministrator = new RuleAdministratorImpl( getRepository() );
    }
}
