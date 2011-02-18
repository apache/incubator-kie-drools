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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.rules.Handle;
import javax.rules.InvalidHandleException;
import javax.rules.InvalidRuleSessionException;
import javax.rules.ObjectFilter;
import javax.rules.RuleExecutionSetNotFoundException;
import javax.rules.RuleRuntime;
import javax.rules.RuleSessionCreateException;
import javax.rules.StatefulRuleSession;

import org.drools.FactHandle;
import org.drools.SessionConfiguration;
import org.drools.StatefulSession;
import org.drools.jsr94.rules.admin.RuleExecutionSetImpl;
import org.drools.jsr94.rules.repository.RuleExecutionSetRepository;
import org.drools.jsr94.rules.repository.RuleExecutionSetRepositoryException;

/**
 * The Drools implementation of the <code>StatefulRuleSession</code> interface
 * which is a representation of a stateful rules engine session. A stateful
 * rules engine session exposes a stateful rule execution API to an underlying
 * rules engine. The session allows arbitrary objects to be added and removed to
 * and from the rule session state. Additionally, objects currently part of the
 * rule session state may be updated. <p/> There are inherently side-effects to
 * adding objects to the rule session state. The execution of a RuleExecutionSet
 * can add, remove and update objects in the rule session state. The objects in
 * the rule session state are therefore dependent on the rules within the
 * <code>RuleExecutionSet</code> as well as the rule engine vendor's specific
 * rule engine behavior. <p/> <code>Handle</code> instances are used by the
 * rule engine vendor to track <code>Object</code>s added to the rule session
 * state. This allows multiple instances of equivalent <code>Object</code>s
 * to be added to the session state and identified, even after serialization.
 * 
 * @see StatefulRuleSession
 * 
 */
public class StatefulRuleSessionImpl extends AbstractRuleSessionImpl
    implements
    StatefulRuleSession {
    // ----------------------------------------------------------------------
    // Constructors
    // ----------------------------------------------------------------------

    private static final long serialVersionUID = 510l;
    
    private StatefulSession session;

    /**
     * Gets the <code>RuleExecutionSet</code> for this URI and associates it
     * with a RuleBase.
     * 
     * @param bindUri
     *            the URI the <code>RuleExecutionSet</code> has been bound to
     * @param properties
     *            additional properties used to create the
     *            <code>RuleSession</code> implementation.
     * 
     * @throws RuleExecutionSetNotFoundException
     *             if there is no rule set under the given URI
     * @throws RuleSessionCreateException 
     */
    StatefulRuleSessionImpl(final String bindUri,
                            final Map properties,
                            final RuleExecutionSetRepository repository)
    throws RuleExecutionSetNotFoundException, RuleSessionCreateException {

        super( repository );
        setProperties( properties );

        RuleExecutionSetImpl ruleSet = null;
        
        try {
            ruleSet = (RuleExecutionSetImpl)
            repository.getRuleExecutionSet(bindUri, properties);
        } catch (RuleExecutionSetRepositoryException e) {
            String s = "Error while retrieving rule execution set bound to: " + bindUri;
            throw new RuleSessionCreateException(s, e);
        }

        if ( ruleSet == null ) {
            throw new RuleExecutionSetNotFoundException( "no execution set bound to: " + bindUri );
        }

        this.setRuleExecutionSet( ruleSet );

        SessionConfiguration conf = new SessionConfiguration();
        conf.setKeepReference( true );
        initSession( conf );
    }
    
    /**
     * Initialize this <code>RuleSession</code>
     * with a new <code>WorkingMemory</code>.
     */
    protected void initSession(SessionConfiguration conf) {
        this.session = this.getRuleExecutionSet().newStatefulSession( conf );

        final Map props = this.getProperties();
        if ( props != null ) {
            for ( final Iterator iterator = props.entrySet().iterator(); iterator.hasNext(); ) {
                final Map.Entry entry = (Map.Entry) iterator.next();
                this.session.setGlobal( (String) entry.getKey(),
                                            entry.getValue() );
            }
        }
    }

    // ----------------------------------------------------------------------
    // Instance methods
    // ----------------------------------------------------------------------

    /**
     * Returns <code>true</code> if the given object is contained within
     * rulesession state of this rule session.
     * 
     * @param objectHandle
     *            the handle to the target object.
     * 
     * @return <code>true</code> if the given object is contained within the
     *         rule session state of this rule session.
     */
    public boolean containsObject(final Handle objectHandle) {
        if ( objectHandle instanceof FactHandle ) {
            return this.session.getObject( (FactHandle) objectHandle ) != null;
        }

        return false;
    }

    /**
     * Adds a given object to the rule session state of this rule session. The
     * argument to this method is Object because in the non-managed env. not all
     * objects should have to implement Serializable. If the
     * <code>RuleSession</code> is <code>Serializable</code> and it contains
     * non-serializable fields a runtime exception will be thrown.
     * 
     * @param object
     *            the object to be added.
     * 
     * @return the Handle for the newly added Object
     * 
     * @throws InvalidRuleSessionException
     *             on illegal rule session state.
     */
    public Handle addObject(final Object object) throws InvalidRuleSessionException {
        checkRuleSessionValidity();
        return (Handle) this.session.insert( object );
    }

    /**
     * Adds a <code>List</code> of <code>Object</code>s to the rule session
     * state of this rule session.
     * 
     * @param objList
     *            the objects to be added.
     * 
     * @return a <code>List</code> of <code>Handle</code>s, one for each
     *         added <code>Object</code>. The <code>List</code> must be
     *         ordered in the same order as the input <code>objList</code>.
     * 
     * @throws InvalidRuleSessionException
     *             on illegal rule session state.
     */
    public List addObjects(final List objList) throws InvalidRuleSessionException {
        checkRuleSessionValidity();

        final List handles = new ArrayList();

        for ( final Iterator objectIter = objList.iterator(); objectIter.hasNext(); ) {
            handles.add( addObject( objectIter.next() ) );
        }
        return handles;
    }

    /**
     * Notifies the rules engine that a given object in the rule session state
     * has changed. <p/> The semantics of this call are equivalent to calling
     * <code>removeObject</code> followed by <code>addObject</code>. The
     * original <code>Handle</code> is rebound to the new value for the
     * <code>Object</code> however.
     * 
     * @param objectHandle
     *            the handle to the original object.
     * @param newObject
     *            the new object to bind to the handle.
     * 
     * @throws InvalidRuleSessionException
     *             on illegal rule session state.
     * @throws InvalidHandleException
     *             if the input <code>Handle</code> is no longer valid
     */
    public void updateObject(final Handle objectHandle,
                             final Object newObject) throws InvalidRuleSessionException,
                                                    InvalidHandleException {
        checkRuleSessionValidity();

        if ( objectHandle instanceof FactHandle ) {
            this.session.update( (FactHandle) objectHandle,
                                             newObject );
        } else {
            throw new InvalidHandleException( "invalid handle" );

        }
    }

    /**
     * Removes a given object from the rule session state of this rule session.
     * 
     * @param handleObject
     *            the handle to the object to be removed from the rule session
     *            state.
     * 
     * @throws InvalidRuleSessionException
     *             on illegal rule session state.
     * @throws InvalidHandleException
     *             if the input <code>Handle</code> is no longer valid
     */
    public void removeObject(final Handle handleObject) throws InvalidRuleSessionException,
                                                       InvalidHandleException {
        checkRuleSessionValidity();

        if ( handleObject instanceof FactHandle ) {
            this.session.retract( (FactHandle) handleObject );
        } else {
            throw new InvalidHandleException( "invalid handle" );
        }
    }

    /**
     * Executes the rules in the bound rule execution set using the objects
     * present in the rule session state. This will typically modify the rule
     * session state - and may add, remove or update <code>Object</code>s
     * bound to <code>Handle</code>s.
     * 
     * @throws InvalidRuleSessionException
     *             on illegal rule session state.
     */
    public void executeRules() throws InvalidRuleSessionException {
        checkRuleSessionValidity();
        this.session.fireAllRules();
    }

    /**
     * @see StatefulRuleSessionImpl
     */
    public Object getObject(final Handle handle) throws InvalidRuleSessionException,
                                                InvalidHandleException {
       checkRuleSessionValidity();

        if ( handle instanceof FactHandle ) {
            return this.session.getObject( (FactHandle) handle );
        } else {
            throw new InvalidHandleException( "invalid handle" );
        }
    }

    /**
     * Returns a <code>List</code> of the <code>Handle</code>s being used
     * for object identity.
     * 
     * @return a <code>List</code> of <code>Handle</code>s present in the
     *         currect state of the rule session.
     */
    public List getHandles() {
        return IteratorToList.convert( this.session.iterateFactHandles() );
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
        
        return IteratorToList.convert( this.session.iterateObjects( new ObjectFilterAdapter( filter ) ) );
    }

    /**
     * Resets this rule session. Calling this method will bring the rule session
     * state to its initial state for this rule session and will reset any other
     * state associated with this rule session.
     * <p/>
     * A reset will not reset the state on the default object filter for a
     * <code>RuleExecutionSet</code>.
     */
    public void reset() {
        // stateful rule sessions should not be high load, thus safe to keep references
        initSession( new SessionConfiguration() );
    }

    public int getType() throws InvalidRuleSessionException {
        return RuleRuntime.STATEFUL_SESSION_TYPE;
    }
    
    /**
     * Releases all resources used by this rule session.
     * This method renders this rule session unusable until
     * it is reacquired through the <code>RuleRuntime</code>.
     */
    public void release() {
        if ( this.session != null ) {
            this.session.dispose();
        }
        this.session = null;
        super.release();
    }
    
    /**
     * Ensures this <code>RuleSession</code> is not
     * in an illegal rule session state.
     *
     * @throws InvalidRuleSessionException on illegal rule session state.
     */
    protected void checkRuleSessionValidity() throws InvalidRuleSessionException {
        if ( this.session == null ) {
            throw new InvalidRuleSessionException( "invalid rule session" );
        }
    }
}
