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

package org.drools.jsr94.rules;

import java.util.List;
import java.util.Map;

import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleSession;
import javax.rules.RuleSessionCreateException;
import javax.rules.RuleSessionTypeUnsupportedException;

import org.drools.jsr94.rules.repository.RuleExecutionSetRepository;
import org.drools.jsr94.rules.repository.RuleExecutionSetRepositoryException;

/**
 * The Drools implementation of the <code>RuleRuntime</code> interface which
 * is the access point for runtime execution of <code>RuleExecutionSet</code>s.
 * It provides methods to create <code>RuleSession</code> implementation as
 * well as methods to retrieve <code>RuleExecutionSet</code>s that have been
 * previously registered using the <code>RuleAdministrator</code>. <p/> The
 * <code>RuleRuntime</code> should be accessed through the
 * <code>RuleServiceProvider</code>. An instance of the
 * <code>RuleRuntime</code> can be retrieved by calling: <p/> <code>
 * RuleServiceProvider ruleServiceProvider =
 *     RuleServiceProvider.newInstance();<br/>
 * RuleRuntime ruleRuntime = ruleServiceProvider.getRuleRuntime();
 * </code>
 * <p/> Note: the release method must be called on the <code>RuleSession</code>
 * to clean up all resources used by the <code>RuleSession</code>.
 * 
 * @see RuleRuntime
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 */
public class RuleRuntimeImpl
    implements
    RuleRuntime {
    private static final long          serialVersionUID = 510l;

    private RuleExecutionSetRepository repository;

    /**
     * Create a new <code>RuleRuntimeImpl</code>.
     */
    public RuleRuntimeImpl(final RuleExecutionSetRepository repository) {
        this.repository = repository;
        // no special initialization required
    }

    /**
     * Creates a <code>RuleSession</code> implementation using the supplied
     * Drools-specific rule execution set registration URI.
     * 
     * @param uri
     *            the URI for the <code>RuleExecutionSet</code>
     * @param properties
     *            additional properties used to create the
     *            <code>RuleSession</code> implementation.
     * @param ruleSessionType
     *            the type of rule session to create.
     * 
     * @throws RuleSessionTypeUnsupportedException
     *             if the ruleSessionType is not supported by Drools or the
     *             RuleExecutionSet
     * @throws RuleExecutionSetNotFoundException
     *             if the URI could not be resolved into a
     *             <code>RuleExecutionSet</code>
     * 
     * @return The created <code>RuleSession</code>.
     */
    public RuleSession createRuleSession(final String uri,
                                         final Map properties,
                                         final int ruleSessionType)
    throws RuleSessionTypeUnsupportedException,
           RuleSessionCreateException,
           RuleExecutionSetNotFoundException {

        if ( ruleSessionType == RuleRuntime.STATELESS_SESSION_TYPE ) {
            final StatelessRuleSessionImpl session = new StatelessRuleSessionImpl( uri,
                                                                                   properties,
                                                                                   this.repository );
            return session;
        } else if ( ruleSessionType == RuleRuntime.STATEFUL_SESSION_TYPE ) {
            final StatefulRuleSessionImpl session = new StatefulRuleSessionImpl( uri,
                                                                                 properties,
                                                                                 this.repository );
            return session;
        }

        throw new RuleSessionTypeUnsupportedException( "invalid session type: " + ruleSessionType );
    }

    /**
     * Retrieves a <code>List</code> of the URIs that currently have
     * <code>RuleExecutionSet</code>s associated with them. An empty list is
     * returned is there are no associations.
     * 
     * @return a <code>List</code> of <code>String</code>s (URIs)
     */
    public List getRegistrations() {
        try {
			return this.repository.getRegistrations();
		} catch (RuleExecutionSetRepositoryException e) {
			String s = "Error while retrieving list of registrations";
			throw new RuntimeException(s, e);
		}
    }
}
