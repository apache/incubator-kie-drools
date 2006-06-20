package org.drools.leaps;

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

import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.NoSuchFactObjectException;
import org.drools.QueryResults;
import org.drools.WorkingMemory;
import org.drools.common.AbstractWorkingMemory;
import org.drools.common.AgendaGroupImpl;
import org.drools.common.AgendaItem;
import org.drools.common.EqualityKey;
import org.drools.common.EventSupport;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.PropagationContextImpl;
import org.drools.common.ScheduledAgendaItem;
import org.drools.leaps.conflict.DefaultConflictResolver;
import org.drools.leaps.util.TokenStack;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.Duration;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.drools.util.FastMap;
import org.drools.util.IdentityMap;
import org.drools.util.IteratorChain;

/**
 * Followed RETEOO implementation for interfaces.
 * 
 * This class is a repository for leaps specific containers (fact factTables).
 * 
 * @author Alexander Bagerman
 * 
 * @see org.drools.WorkingMemory
 * @see java.beans.PropertyChangeListener
 * @see java.io.Serializable
 * 
 */
class LeapsWorkingMemory extends AbstractWorkingMemory implements EventSupport,
        PropertyChangeListener {
    private static final long serialVersionUID       = -2524904474925421759L;

    private final Map         queryResults;

    private final IdentityMap leapsRulesToHandlesMap = new IdentityMap( );

    private final IdentityMap rulesActivationsMap    = new IdentityMap( );

    /**
     * Construct.
     * 
     * @param ruleBase
     *            The backing rule-base.
     */
    public LeapsWorkingMemory(final InternalRuleBase ruleBase) {
        super( ruleBase, ruleBase.newFactHandleFactory( ) );
        this.queryResults = new HashMap( );
        this.agenda = new LeapsAgenda( this );
    }

    public void doAssertObject( final InternalFactHandle factHandle,
                                final Object object,
                                final PropagationContext propagationContext )
            throws FactException {
        
        this.pushTokenOnStack( factHandle, new Token( this, factHandle, propagationContext ) );

        // determine what classes it belongs to put it into the "table" on
        // class name key
        final Class objectClass = object.getClass();
        for ( final Iterator tables = this.getFactTablesList( objectClass ).iterator(); tables.hasNext(); ) {
            final FactTable factTable = (FactTable) tables.next();
            // adding fact to container
            factTable.add( factHandle );
            // inspect all tuples for exists and not conditions and activate
            // /
            // deactivate agenda items
            for (final Iterator tuples = factTable.getTuplesIterator( ); tuples.hasNext( );) {
                final LeapsTuple tuple = (LeapsTuple) tuples.next( );
                boolean tupleWasReadyForActivation = tuple.isReadyForActivation( );
                if (!tuple.isActivationNull( )) {
                    // check not constraints only on activated tuples to see
                    // if
                    // we need to deactivate
                    final ColumnConstraints[] not = tuple.getLeapsRule( )
                                                         .getNotColumnConstraints( );
                    for (int i = 0, length = not.length; i < length; i++) {
                        final ColumnConstraints constraint = not[i];
                        if (!tuple.isBlockingNotFactHandle( i )
                                && constraint.getClassType( )
                                             .isAssignableFrom( objectClass )
                                && constraint.isAllowed( factHandle, tuple, this )) {
                            tuple.setBlockingNotFactHandle( (LeapsFactHandle) factHandle, i );
                            ( (LeapsFactHandle) factHandle ).addNotTuple( tuple, i );
                        }
                    }
                    // check and see if we need de-activate
                    if (!tuple.isReadyForActivation( )) {
                        if (tuple.getLeapsRule( ).getRule( ) instanceof Query) {
                            // put query results to the working memory
                            // location
                            removeFromQueryResults( tuple.getLeapsRule( )
                                                         .getRule( )
                                                         .getName( ), tuple );
                        }
                        else {
                            // time to pull from agenda
                            invalidateActivation( tuple );
                        }
                    }
                }
                else {
                    // check exists constraints and activate constraints
                    final ColumnConstraints[] exists = tuple.getLeapsRule( )
                                                            .getExistsColumnConstraints( );
                    for (int i = 0, length = exists.length; i < length; i++) {
                        final ColumnConstraints constraint = exists[i];
                        if (!tuple.isExistsFactHandle( i )
                                && constraint.getClassType( )
                                             .isAssignableFrom( objectClass )
                                && constraint.isAllowed( factHandle, tuple, this )) {
                            tuple.setExistsFactHandle( (LeapsFactHandle) factHandle, i );
                            ( (LeapsFactHandle) factHandle ).addExistsTuple( tuple, i );
                        }
                    }
                    // check and see if we need activate
                    // activate only if tuple was not ready for it before
                    if (!tupleWasReadyForActivation && tuple.isReadyForActivation( )) {
                        // ready to activate
                        tuple.setContext( new PropagationContextImpl( nextPropagationIdCounter( ),
                                                                      PropagationContext.ASSERTION,
                                                                      tuple.getLeapsRule( ).getRule( ),
                                                                      null ) );

                        this.assertTuple( tuple );
                    }
                }
            }
        }
    }

    /**
     * copies reteoo behaviour in regards to logical assertion 
     * and does checking on available tuples to see if any needs
     * invalidation / activation as a result of this retraction
     * 
     * @see WorkingMemory
     */
    public void doRetract( final InternalFactHandle factHandle,
                           final PropagationContext propagationContext ) {

        /*
         * leaps specific actions
         */
        // remove fact from all relevant fact tables container
        for (final Iterator it = this.getFactTablesList( factHandle.getObject( ).getClass( ) )
                                     .iterator( ); it.hasNext( );) {
            ( (FactTable) it.next( ) ).remove( factHandle );
        }

        // 0. remove activated tuples
        final Iterator tuples = ( (LeapsFactHandle) factHandle ).getActivatedTuples( );
        for (; tuples != null && tuples.hasNext( );) {
            final LeapsTuple tuple = (LeapsTuple) tuples.next( );
            if (tuple.getLeapsRule( ).getRule( ) instanceof Query) {
                // put query results to the working memory location
                removeFromQueryResults( tuple.getLeapsRule( ).getRule( ).getName( ), tuple );
            }
            else {
                // time to pull from agenda
                invalidateActivation( tuple );
            }
        }

        // 1. remove fact for nots and exists tuples
        final IdentityMap tuplesNotReadyForActivation = new IdentityMap( );
        FactHandleTupleAssembly assembly;
        LeapsTuple tuple;
        Iterator it;
        it = ( (LeapsFactHandle) factHandle ).getNotTupleAssemblies( );
        if (it != null) {
            for (; it.hasNext( );) {
                assembly = (FactHandleTupleAssembly) it.next( );
                tuple = assembly.getTuple( );
                if (!tuple.isReadyForActivation( )) {
                    tuplesNotReadyForActivation.put( tuple, tuple );
                }
                tuple.removeBlockingNotFactHandle( assembly.getIndex( ) );

                TokenEvaluator.evaluateNotCondition( (LeapsFactHandle) factHandle,
//                                                                          TokenEvaluator.evaluateNotCondition( new LeapsFactHandle( factHandle.getRecency( ) + 1,
//                                                                                                                                    new Object( ) ),
                                                     assembly.getIndex( ),
                                                     tuple,
                                                     this );
            }
        }
        it = ((LeapsFactHandle) factHandle).getExistsTupleAssemblies();
        if ( it != null ) {
            for ( ; it.hasNext(); ) {
                assembly = (FactHandleTupleAssembly) it.next();
                tuple = assembly.getTuple();
                if ( !tuple.isReadyForActivation() ) {
                    tuplesNotReadyForActivation.put( tuple,
                                                     tuple );
                }
                tuple.removeExistsFactHandle( assembly.getIndex( ) );
                TokenEvaluator.evaluateExistsCondition( (LeapsFactHandle)factHandle,
//                                                                             TokenEvaluator.evaluateExistsCondition( new LeapsFactHandle( factHandle.getRecency( ) + 1,
//                                                                                                                                          null ),
                                                        assembly.getIndex( ),
                                                        tuple,
                                                        this );
            }
        }
        // 2. assert all tuples that are ready for activation or cancel ones
        // that are no longer
        final IteratorChain chain = new IteratorChain();
        it = ((LeapsFactHandle) factHandle).getNotTupleAssemblies();
        if ( it != null ) {
            chain.addIterator( it );
        }
        it = ((LeapsFactHandle) factHandle).getExistsTupleAssemblies();
        if ( it != null ) {
            chain.addIterator( it );
        }
        for ( ; chain.hasNext(); ) {
            tuple = ( (FactHandleTupleAssembly) chain.next( ) ).getTuple( );
            // can assert only tuples that were not eligible for activation
            // before retraction
            if (tuple.isReadyForActivation( ) && tuple.isActivationNull( )
                    && tuplesNotReadyForActivation.containsKey( tuple )) {
                // ready to activate
                tuple.setContext( new PropagationContextImpl( nextPropagationIdCounter( ),
                                                              PropagationContext.ASSERTION,
                                                              tuple.getLeapsRule( )
                                                                   .getRule( ),
                                                              null ) );
                this.assertTuple( tuple );
            }
            else {
                if (tuple.getLeapsRule( ).getRule( ) instanceof Query) {
                    // put query results to the working memory location
                    removeFromQueryResults( tuple.getLeapsRule( ).getRule( ).getName( ),
                                            tuple );
                }
                else {
                    // time to pull from agenda
                    invalidateActivation( tuple );
                }
            }
        }

        // remove it from stack
        this.removeTokenFromStack( (LeapsFactHandle) factHandle );
    }

    /**
     * used when assertion / retraction adds invalidating conditions that 
     * make tuple ineligible for firing
     * 
     * @param tuple
     */
    private final void invalidateActivation( final LeapsTuple tuple ) {
        final Activation activation = tuple.getActivation( );
        if (!tuple.isReadyForActivation( ) && !tuple.isActivationNull( )) {
            // invalidate agenda agendaItem
            if (activation.isActivated( )) {
                activation.remove( );
                getAgendaEventSupport( ).fireActivationCancelled( activation );
            }
            //
            tuple.setActivation( null );
        }
        if (activation != null) {
            // remove logical dependency
            this.tms.removeLogicalDependencies( activation,
                                                tuple.getContext( ),
                                                tuple.getLeapsRule( ).getRule( ) );

            // remove from rule / activaitons map
            FastMap activations = (FastMap) this.rulesActivationsMap.get( activation.getRule( ) );
            if (activations != null) {
                activations.remove( activation );
            }
        }
    }

    /**
     * modify is implemented as half way retract / assert due to the truth maintenance issues.
     * 
     * @see WorkingMemory
     */
    public void modifyObject( final FactHandle factHandle,
                              final Object object,
                              final Rule rule,
                              final Activation activation ) throws FactException {
        this.getLock( ).lock( );
        try {
            final PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                      PropagationContext.MODIFICATION,
                                                                                      rule,
                                                                                      activation );

            final int status = ( (InternalFactHandle) factHandle ).getEqualityKey( )
                                                                  .getStatus( );

            final Object originalObject = this.assertMap.remove( factHandle );
            if (originalObject == null) {
                throw new NoSuchFactObjectException( factHandle );
            }
            // 
            // do subset of retractObject( )
            //
            final InternalFactHandle handle = (InternalFactHandle) factHandle;
            if (handle.getId( ) == -1) {
                // can't retract an already retracted handle
                return;
            }
            removePropertyChangeListener( handle );

            doRetract( handle, propagationContext );

            // Update the equality key, which maintains a list of stated
            // FactHandles
            final EqualityKey key = handle.getEqualityKey( );

            key.removeFactHandle( handle );
            handle.setEqualityKey( null );

            // If the equality key is now empty, then remove it
            if (key.isEmpty( )) {
                this.tms.remove( key );
            }
            // produces NPE otherwise
            this.handleFactory.destroyFactHandle( handle );

            // 
            // and now assert
            //
            /* check to see if this is a logically asserted object */
            this.assertObject( object, false, ( status == EqualityKey.STATED ) ? false
                    : true, rule, activation );

            this.workingMemoryEventSupport.fireObjectModified( propagationContext,
                                                               handle,
                                                               handle.getObject( ),
                                                               object );

            if (!this.factQueue.isEmpty( )) {
                propagateQueuedActions( );
            }
        }
        finally {
            this.getLock( ).unlock( );
        }
    }

    /**
     * ************* leaps section *********************
     */
    private long             idLastFireAllAt = -1;

    /**
     * algorithm stack.
     */
    private final TokenStack mainStack       = new TokenStack( );

    /**
     * generates or just return List of internal factTables that correspond a
     * class can be used to generate factTables
     * 
     * @return
     */
    protected final List getFactTablesList( final Class c ) {
        final ArrayList list = new ArrayList( );
        // interfaces
        final Class[] interfaces = c.getInterfaces( );
        for (int i = 0; i < interfaces.length; i++) {
            list.add( this.getFactTable( interfaces[i] ) );
        }
        // classes
        Class bufClass = c;
        while (bufClass != null) {
            //
            list.add( this.getFactTable( bufClass ) );
            // and get the next class on the list
            bufClass = bufClass.getSuperclass( );
        }
        return list;
    }

    /**
     * adds new leaps token on main stack
     * 
     * @param fact handle
     * @param token
     */
    protected final void pushTokenOnStack(final InternalFactHandle factHandle,
                                          final Token token) {
        this.mainStack.push( token );
    }

    /**
     * removes leaps token on main stack
     * 
     * @param fact handle
     */
    protected final void removeTokenFromStack( final LeapsFactHandle factHandle ) {
        this.mainStack.remove( factHandle.getId( ) );
    }

    /**
     * gets leaps token from top of stack
     * 
     * @param fact handle
     */
    protected final Token peekTokenOnTop() {
        return (Token) this.mainStack.peek( );
    }

    /**
     * get leaps fact table of specific type (class)
     * 
     * @param type
     *            of objects
     * @return fact table of requested class type
     */
    protected FactTable getFactTable( final Class c ) {
        FactTable table;
        if (this.factTables.containsKey( c )) {
            table = (FactTable) this.factTables.get( c );
        }
        else {
            table = new FactTable( DefaultConflictResolver.getInstance( ) );
            this.factTables.put( c, table );
            // review existing rules and assign to the fact table if needed
            for (final Iterator iter = this.leapsRulesToHandlesMap.keySet( ).iterator( ); iter.hasNext( );) {
                final LeapsRule leapsRule = (LeapsRule) iter.next( );
                if (leapsRule.getNumberOfColumns( ) > 0) {
                    final List rulesHandles = (List) this.leapsRulesToHandlesMap.get( leapsRule );
                    for (final Iterator handles = rulesHandles.iterator( ); handles.hasNext( );) {
                        final LeapsRuleHandle handle = (LeapsRuleHandle) handles.next( );
                        if (leapsRule.getColumnClassObjectTypeAtPosition( handle.getDominantPosition( ) )
                                     .isAssignableFrom( c )) {
                            table.addRule( this, handle );
                        }
                    }
                }
            }
        }

        return table;
    }

    /**
     * Add Leaps wrapped rules into the working memory
     * 
     * @param rules
     */
    protected void addLeapsRules(final List rules) {
        this.getLock().lock();
        try {
            ArrayList ruleHandlesList;
            LeapsRule rule;
            LeapsRuleHandle ruleHandle;
            for ( final Iterator it = rules.iterator(); it.hasNext(); ) {
                rule = (LeapsRule) it.next();
                // some times rules do not have "normal" constraints and only
                // not and exists
                if ( rule.getNumberOfColumns() > 0 ) {
                    ruleHandlesList = new ArrayList( );
                    for (int i = 0; i < rule.getNumberOfColumns( ); i++) {
                        ruleHandle = new LeapsRuleHandle( ( (LeapsFactHandleFactory) this.handleFactory ).getNextId( ),
                                                          rule,
                                                          i );
                        // 
                        this.getFactTable( rule.getColumnClassObjectTypeAtPosition( i ) )
                            .addRule( this, ruleHandle );
                        //
                        ruleHandlesList.add( ruleHandle );
                    }
                    this.leapsRulesToHandlesMap.put( rule, ruleHandlesList );
                } else {
                    // to pick up rules that do not require columns, only not
                    // and exists
                    final PropagationContextImpl context = new PropagationContextImpl( nextPropagationIdCounter( ),
                                                                                       PropagationContext.ASSERTION,
                                                                                       null,
                                                                                       null );

                    TokenEvaluator.processAfterAllPositiveConstraintOk( new LeapsTuple( new LeapsFactHandle[0],
                                                                                        rule,
                                                                                        context ),
                                                                        rule,
                                                                        this );
                }
            }
        } finally {
            this.getLock().unlock();
        }
    }

    protected void removeRule(final List rules) {
        this.getLock().lock();
        try {
            ArrayList ruleHandlesList;
            LeapsRule leapsRule;
            LeapsRuleHandle ruleHandle;
            for ( final Iterator it = rules.iterator(); it.hasNext(); ) {
                leapsRule = (LeapsRule) it.next();
                // some times rules do not have "normal" constraints and only
                // not and exists
                if ( leapsRule.getNumberOfColumns() > 0 ) {
                    ruleHandlesList = (ArrayList) this.leapsRulesToHandlesMap.remove( leapsRule );
                    for ( int i = 0; i < ruleHandlesList.size(); i++ ) {
                        ruleHandle = (LeapsRuleHandle) ruleHandlesList.get( i );
                        // 
                        this.getFactTable( leapsRule.getColumnClassObjectTypeAtPosition( i ) ).removeRule( this, ruleHandle );
                    }
                }
                //
            }
            final Rule rule = ((LeapsRule) rules.get( 0 )).getRule();
            final FastMap activations = (FastMap) this.rulesActivationsMap.remove( rule );
            if ( activations != null ) {
                for ( final Iterator activationsIt = activations.keySet( ).iterator(); activationsIt.hasNext(); ) {
                    final Activation activation = (Activation) activationsIt.next();
                    ((LeapsTuple) activation.getTuple()).setActivation( null );
                    this.tms.removeLogicalDependencies( activation,
                                                        activation.getPropagationContext(),
                                                        rule );
                }
            }

            propagateQueuedActions();
        } finally {
            this.getLock().unlock();
        }
    }

    /**
     * main loop
     * 
     */
    public final synchronized void fireAllRules(final AgendaFilter agendaFilter) throws FactException {
        // If we're already firing a rule, then it'll pick up
        // the firing for any other assertObject(..) that get
        // nested inside, avoiding concurrent-modification
        // exceptions, depending on code paths of the actions.

        if ( !this.firing ) {
            try {
                this.firing = true;
                boolean nothingToProcess = false;
                while ( !nothingToProcess ) {
                    // normal rules with required columns
                    while ( !this.mainStack.empty() ) {
                        final Token token = this.peekTokenOnTop();
                        boolean done = false;
                        while ( !done ) {
                            if ( !token.isResume() ) {
                                if ( token.hasNextRuleHandle() ) {
                                    token.nextRuleHandle();
                                } else {
                                    // we do not pop because something might get
                                    // asserted
                                    // and placed on hte top of the stack during
                                    // firing
                                    this.removeTokenFromStack( (LeapsFactHandle) token.getDominantFactHandle() );
                                    done = true;
                                }
                            }
                            if ( !done ) {
                                try {
                                    // ok. now we have tuple, dominant fact and
                                    // rules and ready to seek to checks if any
                                    // agendaItem
                                    // matches on current rule
                                    TokenEvaluator.evaluate( token );
                                    // something was found so set marks for
                                    // resume processing
                                    if ( token.getDominantFactHandle() != null ) {
                                        token.setResume( true );
                                        done = true;
                                    }
                                } catch ( final NoMatchesFoundException ex ) {
                                    token.setResume( false );
                                }
                            }
                            // we put everything on agenda
                            // and if there is no modules or anything like it
                            // it would fire just activated rule
                            while ( this.agenda.fireNextItem( agendaFilter ) ) {
                                ;
                            }
                        }
                    }
                    // pick activations generated by retraction or assert
                    // can generate activations off exists and not pending
                    // tuples
                    while ( this.agenda.fireNextItem( agendaFilter ) ) {
                        ;
                    }
                    if ( this.mainStack.empty() ) {
                        nothingToProcess = true;
                    }
                }
                // mark when method was called last time
                this.idLastFireAllAt = ( (LeapsFactHandleFactory) this.handleFactory ).getNextId( );
                // set all factTables to be reseeded
                for (final Iterator it = this.factTables.values( ).iterator( ); it.hasNext( );) {
                    ( (FactTable) it.next( ) ).setReseededStack( true );
                }
            }
            finally {
                this.firing = false;
            }
        }
    }

    protected final long getIdLastFireAllAt() {
        return this.idLastFireAllAt;
    }

    public String toString() {
        String ret = "";
        Object key;
        ret = ret + "\n" + "Working memory";
        ret = ret + "\n" + "Fact Tables by types:";
        for ( final Iterator it = this.factTables.keySet().iterator(); it.hasNext(); ) {
            key = it.next();
            ret = ret + "\n" + "******************   " + key;
            ret = ret + ((FactTable) this.factTables.get( key )).toString();
        }
        ret = ret + "\n" + "Stack:";
        for ( final Iterator it = this.mainStack.iterator(); it.hasNext(); ) {
            ret = ret + "\n" + "\t" + it.next();
        }
        return ret;
    }

    /**
     * Assert a new <code>Tuple</code>.
     * 
     * @param tuple
     *            The <code>Tuple</code> being asserted.
     * @param workingMemory
     *            The working memory seesion.
     * @throws AssertionException
     *             If an error occurs while asserting.
     */
    public final void assertTuple( final LeapsTuple tuple ) {
        final PropagationContext context = tuple.getContext( );
        final Rule rule = tuple.getLeapsRule( ).getRule( );
        // if the current Rule is no-loop and the origin rule is the same then
        // return
        if (rule.getNoLoop( ) && rule.equals( context.getRuleOrigin( ) )) {
            return;
        }
        //
        final Duration dur = rule.getDuration( );

        Activation agendaItem;
        if (dur != null && dur.getDuration( tuple ) > 0) {
            agendaItem = new ScheduledAgendaItem( context.getPropagationNumber( ),
                                                  tuple,
                                                  this.agenda,
                                                  context,
                                                  rule );
            this.agenda.scheduleItem( (ScheduledAgendaItem) agendaItem );
            tuple.setActivation( agendaItem );
            agendaItem.setActivated( true );
            this.getAgendaEventSupport().fireActivationCreated( agendaItem );
        } else {
            final LeapsRule leapsRule = tuple.getLeapsRule();
            AgendaGroupImpl agendaGroup = leapsRule.getAgendaGroup();
            if ( agendaGroup == null ) {
                if ( rule.getAgendaGroup() == null || rule.getAgendaGroup().equals( "" ) || rule.getAgendaGroup().equals( AgendaGroup.MAIN ) ) {
                    // Is the Rule AgendaGroup undefined? If it is use MAIN,
                    // which is added to the Agenda by default
                    agendaGroup = (AgendaGroupImpl) this.agenda.getAgendaGroup( AgendaGroup.MAIN );
                } else {
                    // AgendaGroup is defined, so try and get the AgendaGroup
                    // from the Agenda
                    agendaGroup = (AgendaGroupImpl) this.agenda.getAgendaGroup( rule.getAgendaGroup() );
                }

                if ( agendaGroup == null ) {
                    // The AgendaGroup is defined but not yet added to the
                    // Agenda, so create the AgendaGroup and add to the Agenda.
                    agendaGroup = new AgendaGroupImpl( rule.getAgendaGroup() );
                    this.agenda.addAgendaGroup( agendaGroup );
                }

                leapsRule.setAgendaGroup( agendaGroup );
            }

            // set the focus if rule autoFocus is true
            if ( rule.getAutoFocus() ) {
                this.agenda.setFocus( agendaGroup );
            }

            agendaItem = new AgendaItem( context.getPropagationNumber(),
                                         tuple,
                                         context,
                                         rule );

            agendaGroup.add( agendaItem );

            tuple.setActivation( agendaItem );
            agendaItem.setActivated( true );
            this.getAgendaEventSupport().fireActivationCreated( agendaItem );
        }

        // retract support
        final LeapsFactHandle[] factHandles = (LeapsFactHandle[]) tuple.getFactHandles();
        for ( int i = 0; i < factHandles.length; i++ ) {
            factHandles[i].addActivatedTuple( tuple );
        }
        
        // rules remove support
        FastMap activations = (FastMap) this.rulesActivationsMap.get( rule );
        if (activations == null) {
            activations = new FastMap( );
            this.rulesActivationsMap.put( rule, activations );
        }
        activations.put( agendaItem, agendaItem );
    }

    List getActivations() {
        List ret = new ArrayList( );
        for (final Iterator it = this.rulesActivationsMap.values( ).iterator( ); it.hasNext( );) {
            ret.addAll( ( (FastMap) it.next( ) ).values( ) );
        }

        return ret;
    }

    protected long nextPropagationIdCounter() {
        return ++this.propagationIdCounter;
    }

    public QueryResults getQueryResults( final String queryName ) {
        final IdentityMap map = (IdentityMap) this.queryResults.get( queryName );
        if (map == null) {
            return null;
        }

        final LinkedList list = new LinkedList( );
        for (final Iterator it = map.keySet( ).iterator( ); it.hasNext( );) {
            list.add( it.next( ) );
        }
        if (!list.isEmpty( )) {
            final Query queryRule = (Query) ( (LeapsTuple) list.get( 0 ) ).getLeapsRule( )
                                                                          .getRule( );
            return new LeapsQueryResults( list, queryRule, this );
        }
        else {
            return null;
        }
    }

    void addToQueryResults( final String query, final Tuple tuple ) {
        IdentityMap map = (IdentityMap) this.queryResults.get( query );
        if (map == null) {
            map = new IdentityMap( );
            this.queryResults.put( query, map );
        }
        map.put( tuple, tuple );
    }

    void removeFromQueryResults( final String query, final Tuple tuple ) {
        final IdentityMap map = (IdentityMap) this.queryResults.get( query );
        if (map != null) {
            map.remove( tuple );
        }
    }

    /**
     * to store facts to cursor over it
     */
    private final Map factTables = new FactTables(); 
    
    class FactTables implements Map, Serializable {
        private LinkedList tables = new LinkedList( );

        private HashMap    map    = new HashMap( );

        public int size() {
            return this.tables.size( );
        }

        public void clear() {
            this.tables.clear( );
            this.map.clear( );
        }

        public boolean isEmpty() {
            return this.tables.isEmpty( );
        }

        public boolean containsKey( Object key ) {
            return this.map.containsKey( key );
        }

        public boolean containsValue( Object value ) {
            return this.map.containsValue( value );
        }

        public Collection values() {
            return this.tables;
        }

        public void putAll( Map t ) {
            this.tables.addAll( t.values( ) );
            this.map.putAll( t );
        }

        public Set entrySet() {
            return this.map.entrySet( );
        }

        public Set keySet() {
            return this.map.keySet( );
        }

        public Object get( Object key ) {
            return this.map.get( key );

        }

        public Object remove( Object key ) {
            Object ret = this.map.remove( key );
            this.tables.remove( ret );
            return ret;
        }

        public Object put( Object key, Object value ) {
            this.tables.add( value );
            this.map.put( key, value );
            return value;
        }        
    }
}
