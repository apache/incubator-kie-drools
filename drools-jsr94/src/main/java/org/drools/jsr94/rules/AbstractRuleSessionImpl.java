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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.rules.InvalidRuleSessionException;
import javax.rules.RuleExecutionSetMetadata;
import javax.rules.RuleSession;
import javax.rules.admin.RuleExecutionSet;

import org.drools.jsr94.rules.admin.RuleExecutionSetImpl;
import org.drools.jsr94.rules.repository.RuleExecutionSetRepository;
import org.drools.jsr94.rules.repository.RuleExecutionSetRepositoryException;

/**
 * The Drools implementation of the <code>RuleSession</code> interface which is
 * a representation of a client session with a rules engine. A rules engine
 * session serves as an entry point into an underlying rules engine. The
 * <code>RuleSession</code> is bound to a rules engine instance and exposes a
 * vendor-neutral rule processing API for executing <code>Rule</code>s within a
 * bound <code>RuleExecutionSet</code>.
 * <p/>
 * Note: the <code>release</code> method must be called to clean up all
 * resources used by the <code>RuleSession</code>. Calling <code>release</code>
 * may make the <code>RuleSession</code> eligible to be returned to a
 * <code>RuleSession</code> pool.
 *
 * @see RuleSession
 */
abstract class AbstractRuleSessionImpl
    implements
    RuleSession {
    private RuleExecutionSetRepository repository;

    public AbstractRuleSessionImpl(final RuleExecutionSetRepository repository) {
        this.repository = repository;
    }
    
    /**
     * The Drools <code>RuleExecutionSet</code> associated
     * with this <code>RuleSession</code>.
     */
    private RuleExecutionSetImpl ruleExecutionSet;

    /**
     * A <code>Map</code> of <code>String</code>/<code>Object</code> pairs
     * passed as application data to the Drools <code>WorkingMemory</code>.
     */
    private Map                  properties;

    /**
     * Sets additional properties used to create this <code>RuleSession</code>.
     *
     * @param properties additional properties used to create the
     *        <code>RuleSession</code> implementation.
     */
    protected void setProperties(final Map properties) {
        this.properties = properties;
    }

    /**
     * Returns the additional properties used to create this
     * <code>RuleSession</code>.
     *
     * @return the additional properties used to create this
     *         <code>RuleSession</code>.
     */
    protected Map getProperties() {
        return this.properties;
    }

    /**
     * Sets the Drools <code>RuleExecutionSet</code> associated
     * with this <code>RuleSession</code>.
     *
     * @param ruleExecutionSet the Drools <code>RuleExecutionSet</code> to associate
     *        with this <code>RuleSession</code>.
     */
    protected void setRuleExecutionSet(final RuleExecutionSetImpl ruleExecutionSet) {
        this.ruleExecutionSet = ruleExecutionSet;
    }

    /**
     * Returns the Drools <code>RuleExecutionSet</code> associated
     * with this <code>RuleSession</code>.
     *
     * @return the Drools <code>RuleExecutionSet</code> associated
     * with this <code>RuleSession</code>.
     */
    protected RuleExecutionSetImpl getRuleExecutionSet() {
        return this.ruleExecutionSet;
    }
    
    protected abstract void checkRuleSessionValidity() throws InvalidRuleSessionException;
    

    // JSR94 interface methods start here -------------------------------------

    /**
     * Returns the meta data for the rule execution set bound to this rule
     * session.
     *
     * @return the RuleExecutionSetMetaData bound to this rule session.
     */
    public RuleExecutionSetMetadata getRuleExecutionSetMetadata() {
        String theBindUri = null;
        List registrations = null;
        
        try {
            registrations = this.repository.getRegistrations();
        } catch (RuleExecutionSetRepositoryException e) {
            String s = "Error while retrieving rule execution set registrations";
            throw new RuntimeException(s, e);
        }
        
        for ( final Iterator i = registrations.iterator(); i.hasNext(); ) {
            final String aBindUri = (String) i.next();
            // FIXME: provide the correct properties
            RuleExecutionSet aRuleSet = null;
            
            try {
                aRuleSet = this.repository.getRuleExecutionSet( aBindUri, null );
            } catch (RuleExecutionSetRepositoryException e) {
                String s = "Error while retrieving rule execution set bound to: " + aBindUri;
                throw new RuntimeException(s, e);
            }

            if ( aRuleSet == this.ruleExecutionSet ) {
                theBindUri = aBindUri;
                break;
            }
        }

        return new RuleExecutionSetMetadataImpl( theBindUri,
                                                 this.ruleExecutionSet.getName(),
                                                 this.ruleExecutionSet.getDescription() );
    }

    /**
     * Releases all resources used by this rule session.
     * This method renders this rule session unusable until
     * it is reacquired through the <code>RuleRuntime</code>.
     */
    public void release() {
        setProperties( null );
        setRuleExecutionSet( null );
    }
}
