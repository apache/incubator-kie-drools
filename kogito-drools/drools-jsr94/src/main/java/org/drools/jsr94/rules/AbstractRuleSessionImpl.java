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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.rules.InvalidRuleSessionException;
import javax.rules.ObjectFilter;
import javax.rules.RuleExecutionSetMetadata;
import javax.rules.RuleRuntime;
import javax.rules.RuleSession;
import javax.rules.StatefulRuleSession;
import javax.rules.StatelessRuleSession;
import javax.rules.admin.RuleExecutionSet;

import org.drools.WorkingMemory;
import org.drools.jsr94.rules.admin.RuleExecutionSetImpl;
import org.drools.jsr94.rules.admin.RuleExecutionSetRepository;

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
 *
 * @author N. Alex Rupp (n_alex <at>codehaus.org)
 * @author <a href="mailto:thomas.diesler@softcon-itec.de">thomas diesler </a>
 */
abstract class AbstractRuleSessionImpl
    implements
    RuleSession {
    private RuleExecutionSetRepository repository;

    public AbstractRuleSessionImpl(final RuleExecutionSetRepository repository) {
        super();
        this.repository = repository;
    }

    /**
     * The Drools <code>WorkingMemory</code> associated
     * with this <code>RuleSession</code>.
     */
    protected WorkingMemory        workingMemory;

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
     * Initialize this <code>RuleSession</code>
     * with a new <code>WorkingMemory</code>.
     */
    protected void initWorkingMemory(boolean keepReference) {        
        final WorkingMemory newWorkingMemory = this.getRuleExecutionSet().newWorkingMemory(keepReference);

        final Map props = this.getProperties();
        if ( props != null ) {
            for ( final Iterator iterator = props.entrySet().iterator(); iterator.hasNext(); ) {
                final Map.Entry entry = (Map.Entry) iterator.next();
                newWorkingMemory.setGlobal( (String) entry.getKey(),
                                            entry.getValue() );
            }
        }
        
        this.setWorkingMemory( newWorkingMemory );
    }

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
     * Sets the Drools <code>WorkingMemory</code> associated
     * with this <code>RuleSession</code>.
     *
     * @param workingMemory the <code>WorkingMemory</code> to associate
     *        with this <code>RuleSession</code>.
     */
    protected void setWorkingMemory(final WorkingMemory workingMemory) {
        // first dispose any existing working memories
        if ( this.workingMemory != null ) {
            this.workingMemory.dispose();
        }        
        this.workingMemory = workingMemory;
    }

    /**
     * Returns the Drools <code>WorkingMemory</code> associated
     * with this <code>RuleSession</code>.
     *
     * @return the Drools <code>WorkingMemory</code> to associate
     *         with this <code>RuleSession</code>.
     */
    protected WorkingMemory getWorkingMemory() {
        return this.workingMemory;
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

    /**
     * Ensures this <code>RuleSession</code> is not
     * in an illegal rule session state.
     *
     * @throws InvalidRuleSessionException on illegal rule session state.
     */
    protected void checkRuleSessionValidity() throws InvalidRuleSessionException {
        if ( this.workingMemory == null ) {
            throw new InvalidRuleSessionException( "invalid rule session" );
        }
    }
    
    /**
     * Returns a List of all objects in the rule session state of this rule
     * session. The objects should pass the default filter test of the default
     * <code>RuleExecutionSet</code> filter (if present). <p/> This may not
     * neccessarily include all objects added by calls to <code>addObject</code>,
     * and may include <code>Object</code>s created by side-effects. The
     * execution of a <code>RuleExecutionSet</code> can add, remove and update
     * objects as part of the rule session state. Therefore the rule session
     * state is dependent on the rules that are part of the executed
     * <code>RuleExecutionSet</code> as well as the rule vendor's specific
     * rule engine behavior.
     * 
     * @return a <code>List</code> of all objects part of the rule session
     *         state.
     * 
     * @throws InvalidRuleSessionException
     *             on illegal rule session state.
     */
    public List getObjects() throws InvalidRuleSessionException {
        checkRuleSessionValidity();

        return getObjects( getRuleExecutionSet().getObjectFilter() );
    }    
    
    /**
     * Returns a <code>List</code> over the objects in rule session state of
     * this rule session. The objects should pass the filter test on the
     * specified <code>ObjectFilter</code>. <p/> This may not neccessarily
     * include all objects added by calls to <code>addObject</code>, and may
     * include <code>Object</code>s created by side-effects. The execution of
     * a <code>RuleExecutionSet</code> can add, remove and update objects as
     * part of the rule session state. Therefore the rule session state is
     * dependent on the rules that are part of the executed
     * <code>RuleExecutionSet</code> as well as the rule vendor's specific
     * rule engine behavior.
     * 
     * @param filter
     *            the object filter.
     * 
     * @return a <code>List</code> of all the objects in the rule session
     *         state of this rule session based upon the given object filter.
     * 
     * @throws InvalidRuleSessionException
     *             on illegal rule session state.
     */
    public List getObjects(final ObjectFilter filter) throws InvalidRuleSessionException {
        checkRuleSessionValidity();

        final List objects = new ArrayList();

        objects.addAll( getWorkingMemory().getObjects() );

        if ( filter != null ) {
            for ( final Iterator objectIter = objects.iterator(); objectIter.hasNext(); ) {
                final Object object = objectIter.next();
                if ( filter.filter( object ) == null ) {
                    objectIter.remove();
                }
            }
        }

        return objects;
    }    

    // JSR94 interface methods start here -------------------------------------

    /**
     * Returns the meta data for the rule execution set bound to this rule
     * session.
     *
     * @return the RuleExecutionSetMetaData bound to this rule session.
     */
    public RuleExecutionSetMetadata getRuleExecutionSetMetadata() {
        String theBindUri = null;
        for ( final Iterator i = this.repository.getRegistrations().iterator(); i.hasNext(); ) {
            final String aBindUri = (String) i.next();
            final RuleExecutionSet aRuleSet = this.repository.getRuleExecutionSet( aBindUri );
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
        if ( this.workingMemory != null ) {
            this.workingMemory.dispose();
        }
        setProperties( null );
        setWorkingMemory( null );
        setRuleExecutionSet( null );
    }
}
