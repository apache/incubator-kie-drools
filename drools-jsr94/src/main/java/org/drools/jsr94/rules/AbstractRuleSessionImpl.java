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

import org.drools.InitialFact;
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
abstract class AbstractRuleSessionImpl implements RuleSession
{
	private RuleExecutionSetRepository repository;
	
	public AbstractRuleSessionImpl(RuleExecutionSetRepository repository) {
		super();
		this.repository = repository;
	}
	
    /**
     * The Drools <code>WorkingMemory</code> associated
     * with this <code>RuleSession</code>.
     */
    private WorkingMemory workingMemory;

    /**
     * The Drools <code>RuleExecutionSet</code> associated
     * with this <code>RuleSession</code>.
     */
    private RuleExecutionSetImpl ruleExecutionSet;

    /**
     * A <code>Map</code> of <code>String</code>/<code>Object</code> pairs
     * passed as application data to the Drools <code>WorkingMemory</code>.
     */
    private Map properties;

    /**
     * Initialize this <code>RuleSession</code>
     * with a new <code>WorkingMemory</code>.
     *
     * @see #newWorkingMemory()
     */
    protected void initWorkingMemory( )
    {
        this.setWorkingMemory( this.newWorkingMemory( ) );
    }

    /**
     * Creates a new <code>WorkingMemory</code> for this
     * <code>RuleSession</code>. All properties set prior to calling this method
     * are added as application data to the new <code>WorkingMemory</code>.
     * The created <code>WorkingMemory</code> uses the default conflict
     * resolution strategy.
     *
     * @return the new <code>WorkingMemory</code>.
     *
     * @see #setProperties(Map)
     * @see WorkingMemory#setApplicationData(String, Object)
     */
    protected WorkingMemory newWorkingMemory( )
    {
        WorkingMemory newWorkingMemory =
            this.getRuleExecutionSet( ).newWorkingMemory( );

        Map props = this.getProperties( );
        if ( props != null )
        {
            for ( Iterator iterator = props.entrySet( ).iterator( );
                  iterator.hasNext( ); )
            {
                Map.Entry entry = ( Map.Entry ) iterator.next( );
                newWorkingMemory.setGlobal(
                    ( String ) entry.getKey( ), entry.getValue( ) );
            }
        }

        return newWorkingMemory;
    }

    /**
     * Sets additional properties used to create this <code>RuleSession</code>.
     *
     * @param properties additional properties used to create the
     *        <code>RuleSession</code> implementation.
     */
    protected void setProperties( Map properties )
    {
        this.properties = properties;
    }

    /**
     * Returns the additional properties used to create this
     * <code>RuleSession</code>.
     *
     * @return the additional properties used to create this
     *         <code>RuleSession</code>.
     */
    protected Map getProperties( )
    {
        return this.properties;
    }

    /**
     * Sets the Drools <code>WorkingMemory</code> associated
     * with this <code>RuleSession</code>.
     *
     * @param workingMemory the <code>WorkingMemory</code> to associate
     *        with this <code>RuleSession</code>.
     */
    protected void setWorkingMemory( WorkingMemory workingMemory )
    {
        this.workingMemory = workingMemory;
    }

    /**
     * Returns the Drools <code>WorkingMemory</code> associated
     * with this <code>RuleSession</code>.
     *
     * @return the Drools <code>WorkingMemory</code> to associate
     *         with this <code>RuleSession</code>.
     */
    protected WorkingMemory getWorkingMemory( )
    {
        return this.workingMemory;
    }

    /**
     * Sets the Drools <code>RuleExecutionSet</code> associated
     * with this <code>RuleSession</code>.
     *
     * @param ruleExecutionSet the Drools <code>RuleExecutionSet</code> to associate
     *        with this <code>RuleSession</code>.
     */
    protected void setRuleExecutionSet( RuleExecutionSetImpl ruleExecutionSet )
    {
        this.ruleExecutionSet = ruleExecutionSet;
    }

    /**
     * Returns the Drools <code>RuleExecutionSet</code> associated
     * with this <code>RuleSession</code>.
     *
     * @return the Drools <code>RuleExecutionSet</code> associated
     * with this <code>RuleSession</code>.
     */
    protected RuleExecutionSetImpl getRuleExecutionSet( )
    {
        return this.ruleExecutionSet;
    }

    /**
     * Ensures this <code>RuleSession</code> is not
     * in an illegal rule session state.
     *
     * @throws InvalidRuleSessionException on illegal rule session state.
     */
    protected void checkRuleSessionValidity( )
        throws InvalidRuleSessionException
    {
        if ( this.workingMemory == null )
        {
            throw new InvalidRuleSessionException( "invalid rule session" );
        }
    }

    /**
     * Applies the given <code>ObjectFilter</code> to the <code>List</code> of
     * <code>Object</code>s, removing all <code>Object</code>s from the given
     * <code>List</code> that do not pass the filter.
     *
     * @param objects <code>List</code> of <code>Object</code>s to be filtered
     * @param objectFilter the <code>ObjectFilter</code> to be applied
     */
    protected void applyFilter( List objects, ObjectFilter objectFilter )
    {
        if ( objectFilter != null )
        {
            for ( Iterator objectIter = objects.iterator( );
                  objectIter.hasNext( ); )
            {
                Object object = objectIter.next( );
                if ( objectFilter.filter( object ) == null )
                {
                    objectIter.remove( );
                } else if ( object.getClass() == InitialFact.class ) {
                    objectIter.remove( );
                }
            }
        }
    }

    // JSR94 interface methods start here -------------------------------------

    /**
     * Returns the meta data for the rule execution set bound to this rule
     * session.
     *
     * @return the RuleExecutionSetMetaData bound to this rule session.
     */
    public RuleExecutionSetMetadata getRuleExecutionSetMetadata( )
    {
        String theBindUri = null;
        for ( Iterator i = repository.getRegistrations( ).iterator( );
              i.hasNext( ); )
        {
            String aBindUri = ( String ) i.next( );
            RuleExecutionSet aRuleSet =
                repository.getRuleExecutionSet( aBindUri );
            if ( aRuleSet == this.ruleExecutionSet )
            {
                theBindUri = aBindUri;
                break;
            }
        }

        return new RuleExecutionSetMetadataImpl(
            theBindUri,
            this.ruleExecutionSet.getName( ),
            this.ruleExecutionSet.getDescription( ) );
    }

    /**
     * Returns the type identifier for this <code>RuleSession</code>. The
     * type identifiers are defined in the <code>RuleRuntime</code> interface.
     *
     * @return the type identifier for this <code>RuleSession</code>
     *
     * @throws InvalidRuleSessionException on illegal rule session state.
     *
     * @see RuleRuntime#STATEFUL_SESSION_TYPE
     * @see RuleRuntime#STATELESS_SESSION_TYPE
     */
    public int getType( ) throws InvalidRuleSessionException
    {
        if ( this instanceof StatelessRuleSession )
        {
            return RuleRuntime.STATELESS_SESSION_TYPE;
        }

        if ( this instanceof StatefulRuleSession )
        {
            return RuleRuntime.STATEFUL_SESSION_TYPE;
        }

        throw new InvalidRuleSessionException( "unknown type" );
    }

    /**
     * Releases all resources used by this rule session.
     * This method renders this rule session unusable until
     * it is reacquired through the <code>RuleRuntime</code>.
     */
    public void release( )
    {
        this.setProperties( null );
        this.setWorkingMemory( null );
        this.setRuleExecutionSet( null );
    }

    /**
     * Resets this rule session. Calling this method will bring the rule session
     * state to its initial state for this rule session and will reset any other
     * state associated with this rule session.
     * <p/>
     * A reset will not reset the state on the default object filter for a
     * <code>RuleExecutionSet</code>.
     */
    public void reset( )
    {
        this.initWorkingMemory( );
    }
}
