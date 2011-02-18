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

import java.util.Map;

import javax.rules.admin.LocalRuleExecutionSetProvider;
import javax.rules.admin.RuleAdministrator;
import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleExecutionSetProvider;
import javax.rules.admin.RuleExecutionSetRegisterException;

import org.drools.jsr94.rules.repository.RuleExecutionSetRepository;
import org.drools.jsr94.rules.repository.RuleExecutionSetRepositoryException;

/**
 * The Drools implementation of the <code>RuleAdministrator</code> interface
 * which is used by rule execution set administrators to load rule execution
 * sets from external sources and create a <code>RuleExecutionSet</code>
 * runtime object. <p/> The <code>RuleAdministrator</code> should be accessed
 * by calling: <p/> <code>
 * RuleServiceProvider ruleServiceProvider =
 *     RuleServiceProvider.newInstance();<br/>
 * RuleAdministrator ruleAdministration =
 *     ruleServiceProvider.getRuleAdministrator();
 * </code>
 * <p/> In an additional step the administrator may also choose to bind the
 * <code>RuleExecutionSet</code> instance to a URI so that it is globally
 * accessible and <code>RuleSession</code>s can be created for the
 * <code>RuleExecutionSet</code> through the RuleRuntime.
 * 
 * @see RuleAdministrator
 */
public class RuleAdministratorImpl
    implements
    RuleAdministrator, java.io.Serializable {
    private RuleExecutionSetRepository repository;

    /** Default constructor. */
    public RuleAdministratorImpl(final RuleExecutionSetRepository repository) {
        super();
        this.repository = repository;
    }

    /**
     * Returns a <code>RuleExecutionSetProvider</code> implementation.
     * 
     * @param properties
     *            additional properties
     * 
     * @return The created <code>RuleExecutionSetProvider</code>.
     */
    public RuleExecutionSetProvider getRuleExecutionSetProvider(final Map properties) {
        return new RuleExecutionSetProviderImpl();
    }

    /**
     * Returns a <code>LocalRuleExecutionSetProvider</code> implementation.
     * 
     * Returns a <code>LocalRuleExecutionSetProvider</code> implementation or
     * null if this implementation does not support creating a
     * <code>RuleExecutionSet</code> from non-serializable resources.
     * 
     * @param properties
     *            additional properties
     * 
     * @return The created <code>LocalRuleExecutionSetProvider</code>.
     */
    public LocalRuleExecutionSetProvider getLocalRuleExecutionSetProvider(final Map properties) {
        return new LocalRuleExecutionSetProviderImpl();
    }

    /**
     * Registers a <code>RuleExecutionSet</code> and associates it with a
     * given URI. Once a <code>RuleExecutionSet</code> has been registered it
     * is accessible to runtime clients through the <code>RuleRuntime</code>.
     * If a <code>RuleExecutionSet</code> has already been associated with the
     * URI it should be deregistered (as if
     * <code>deregisterRuleExecutionSet/</code> had been called) and the URI
     * should be associated with the new <code>RuleExecutionSet</code>.
     * 
     * @param bindUri
     *            the URI to associate with the <code>RuleExecutionSet</code>.
     * @param set
     *            the <code>RuleExecutionSet</code> to associate with the URI
     * @param properties
     *            additional properties used to perform the registration
     * 
     * @throws RuleExecutionSetRegisterException
     *             if an error occurred that prevented registration
     */
    public void registerRuleExecutionSet(final String bindUri,
                                         final RuleExecutionSet set,
                                         final Map properties)
    throws RuleExecutionSetRegisterException {

        // Note: an existing RuleExecutionSet is simply replaced
        repository.registerRuleExecutionSet(bindUri, set, properties);
    }

    /**
     * Unregisters a previously registered <code>RuleExecutionSet</code> from
     * a URI.
     * 
     * @param bindUri
     *            the URI to disassociate with the <code>RuleExecutionSet</code>.
     * @param properties
     *            additional properties used to perform the deregistration
     * 
     * @throws RuleExecutionSetDeregistrationException
     *             if an error occurred that prevented unregistration
     */
    public void deregisterRuleExecutionSet(final String bindUri,
                                           final Map properties)
    throws RuleExecutionSetDeregistrationException {

        try {
            if ( this.repository.getRuleExecutionSet(bindUri, properties) == null ) {
                throw new RuleExecutionSetDeregistrationException( "no execution set bound to: " + bindUri );
            }
        } catch (RuleExecutionSetRepositoryException e) {
            String s = "Error while retrieving rule execution set bound to: " + bindUri;
            throw new RuleExecutionSetDeregistrationException(s, e);
        }

        repository.unregisterRuleExecutionSet(bindUri, properties);
    }
}
