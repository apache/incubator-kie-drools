/**
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

package org.drools.jsr94.rules.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetDeregistrationException;
import javax.rules.admin.RuleExecutionSetRegisterException;

/**
 * Stores the registered <code>RuleExecutionSet</code> objects.
 * 
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 */
public interface RuleExecutionSetRepository extends Serializable
{
    /**
     * Retrieves a <code>List</code> of the URIs that currently have
     * <code>RuleExecutionSet</code>s associated with them.
     * 
     * An empty list is returned if there are no associations.
     * 
     * @return a <code>List</code> of the URIs that currently have
     *         <code>RuleExecutionSet</code>s associated with them.
     * @throws RuleExecutionSetRepositoryException
     */
    List getRegistrations() throws RuleExecutionSetRepositoryException;

    /**
     * Get the <code>RuleExecutionSet</code> bound to this URI, or return
     * <code>null</code>.
     * 
     * @param bindUri
     *            the URI associated with the wanted
     *            <code>RuleExecutionSet</code>.
     * @param properties
     * 
     * @return the <code>RuleExecutionSet</code> bound to the given URI.
     * @throws RuleExecutionSetRepositoryException
     */
    RuleExecutionSet getRuleExecutionSet(
    		String bindUri,
    		Map properties)
    throws RuleExecutionSetRepositoryException;

    /**
     * Register a <code>RuleExecutionSet</code> under the given URI.
     * 
     * @param bindUri the URI to associate with the <code>RuleExecutionSet</code>.
     * @param ruleSet the <code>RuleExecutionSet</code> to associate with the URI
     * @param properties
     * 
     * @throws RuleExecutionSetRegisterException
     *             if an error occurred that prevented registration (i.e. if
     *             bindUri or ruleSet are <code>null</code>)
     */
	void registerRuleExecutionSet(
    		String bindUri,
    		RuleExecutionSet ruleSet,
    		Map properties)
    throws RuleExecutionSetRegisterException;

    /**
     * Unregister a <code>RuleExecutionSet</code> from the given URI.
     * 
     * @param bindUri the URI to disassociate with the <code>RuleExecutionSet</code>.
     * @param properties
     * @throws RuleExecutionSetDeregistrationException
     *             if an error occurred that prevented deregistration
     */
	void unregisterRuleExecutionSet(
    		String bindUri,
    		Map properties)
    throws RuleExecutionSetDeregistrationException;
}
