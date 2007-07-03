package org.drools.jsr94.rules.admin;

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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.rules.admin.RuleExecutionSet;
import javax.rules.admin.RuleExecutionSetRegisterException;

/**
 * Stores the registered <code>RuleExecutionSet</code> objects.
 * 
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 */
public final class RuleExecutionSetRepository
    implements
    Serializable {
    private static final long serialVersionUID = 400L;

    /** The Singleton instance of the repository. */
    // private static RuleExecutionSetRepository REPOSITORY;
    /** Holds the registered <code>RuleExecutionSet</code> objects. */
    private final Map         map              = new HashMap();

    /** Private constructor; use <code>getInstance</code> instead. */
    public RuleExecutionSetRepository() {
        // Hide the constructor.
    }

    /**
     * Gets the Singleton instance of a <code>RuleExecutionSetRepository</code>.
     * 
     * @return The Singleton instance of the repository.
     */
    // public static synchronized RuleExecutionSetRepository getInstance( )
    // {
    // if ( RuleExecutionSetRepository.REPOSITORY != null )
    // {
    // return RuleExecutionSetRepository.REPOSITORY;
    // }
    // return RuleExecutionSetRepository.REPOSITORY =
    // new RuleExecutionSetRepository( );
    // }
    /**
     * Retrieves a <code>List</code> of the URIs that currently have
     * <code>RuleExecutionSet</code>s associated with them.
     * 
     * An empty list is returned is there are no associations.
     * 
     * @return a <code>List</code> of the URIs that currently have
     *         <code>RuleExecutionSet</code>s associated with them.
     */
    public List getRegistrations() {
        final List list = new ArrayList();
        list.addAll( this.map.keySet() );
        return list;
    }

    /**
     * Get the <code>RuleExecutionSet</code> bound to this URI, or return
     * <code>null</code>.
     * 
     * @param bindUri
     *            the URI associated with the wanted
     *            <code>RuleExecutionSet</code>.
     * 
     * @return the <code>RuleExecutionSet</code> bound to the given URI.
     */
    public RuleExecutionSet getRuleExecutionSet(final String bindUri) {
        return (RuleExecutionSet) this.map.get( bindUri );
    }

    /**
     * Register a <code>RuleExecutionSet</code> under the given URI.
     * 
     * @param bindUri
     *            the URI to associate with the <code>RuleExecutionSet</code>.
     * @param ruleSet
     *            the <code>RuleExecutionSet</code> to associate with the URI
     * 
     * @throws RuleExecutionSetRegisterException
     *             if an error occurred that prevented registration (i.e. if
     *             bindUri or ruleSet are <code>null</code>)
     */
    public void registerRuleExecutionSet(final String bindUri,
                                         final RuleExecutionSet ruleSet) throws RuleExecutionSetRegisterException {
        if ( bindUri == null ) {
            throw new RuleExecutionSetRegisterException( "bindUri cannot be null" );
        }
        if ( ruleSet == null ) {
            throw new RuleExecutionSetRegisterException( "ruleSet cannot be null" );
        }
        this.map.put( bindUri,
                      ruleSet );
    }

    /**
     * Unregister a <code>RuleExecutionSet</code> from the given URI.
     * 
     * @param bindUri
     *            the URI to disassociate with the <code>RuleExecutionSet</code>.
     */
    public void unregisterRuleExecutionSet(final String bindUri) {
        if ( bindUri == null ) {
            throw new NullPointerException( "bindUri cannot be null" );
        }
        this.map.remove( bindUri );
    }
}
