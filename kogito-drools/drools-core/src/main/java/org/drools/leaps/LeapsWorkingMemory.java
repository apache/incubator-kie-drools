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
    private static final long serialVersionUID       = 320;

    private final Map         queryResults;

    private final IdentityMap leapsRulesToHandlesMap = new IdentityMap( );

    private final IdentityMap rulesActivationsMap    = new IdentityMap( );

    private final LinkedList  noPositiveColumnsRules = new LinkedList( );

    /**
     * Construct.
     * 
     * @param ruleBase
     *            The backing rule-base.
     */
    public LeapsWorkingMemory(final int id, final InternalRuleBase ruleBase) {
        super( id, ruleBase, ruleBase.newFactHandleFactory( ) );
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
        List tuplesToAssert = new LinkedList( );
        final Object objectClass = LeapsBuilder.getLeapsClassType( object );
        for (final Iterator tables = this.getFactTablesList( objectClass ).iterator( ); tables.hasNext( );) {
            tuplesToAssert.clear( );
            final FactTable factTable = (FactTable) tables.next( );
            // adding fact to container
            factTable.add( factHandle );
            // iterate through unsatisfied exists
            for (final Iterator tuples = factTable.getTuplesIterator( ); tuples.hasNext( );) {
                final LeapsTuple tuple = (LeapsTuple) tuples.next( );

                TokenEvaluator.evaluateExistsConditions( tuple, tuple.getLeapsRule( ), this );
                // check and see if we need activate
                // activate only if tuple was not ready for it before
                if (tuple.isReadyForActivation( )) {
                    // ready to activate
                    tuplesToAssert.add( tuple );
                }
            }
            for (final Iterator it = tuplesToAssert.iterator( ); it.hasNext( );) {
                // ready to activate
                final LeapsTuple tuple = (LeapsTuple) it.next( );
                factTable.removeTuple( tuple );
                tuple.setContext( new PropagationContextImpl( nextPropagationIdCounter( ),
                                                              PropagationContext.ASSERTION,
                                                              tuple.getLeapsRule( )
                                                                   .getRule( ),
                                                              null ) );
                this.assertTuple( tuple );
            }
        }
        // inspect all tuples for not conditions and activate
        // deactivate agenda items
        Activation[] activations = this.agenda.getActivations( );
        for (int k = 0; k < activations.length; k++) {
            boolean deActivate = false;
            LeapsTuple tuple = (LeapsTuple) activations[k].getTuple( );
            final ColumnConstraints[] not = tuple.getLeapsRule( ).getNotColumnConstraints( );
            for (int i = 0, length = not.length; !deActivate && i < length; i++) {
                final ColumnConstraints constraint = not[i];
                final Object columnClassObject = constraint.getClassType( );
                if (( ( objectClass.getClass( ) == Class.class
                        && columnClassObject.getClass( ) == Class.class && ( (Class) columnClassObject ).isAssignableFrom( (Class) objectClass ) ) || ( objectClass.getClass( ) != Class.class
                        && columnClassObject.getClass( ) != Class.class && columnClassObject.equals( objectClass ) ) )
                        && constraint.isAllowed( factHandle, tuple, this )) {
                    tuple.setBlockingNotFactHandle( (LeapsFactHandle) factHandle, i );
                    ( (LeapsFactHandle) factHandle ).addNotTuple( tuple, i );
                    deActivate = true;
                }
            }
            // check and see if we need de-activate
            if (deActivate) {
                tuple.setContext( new PropagationContextImpl( nextPropagationIdCounter( ),
                                                              PropagationContext.ASSERTION,
                                                              tuple.getLeapsRule( )
                                                                   .getRule( ),
                                                              null ) );
                if (tuple.getLeapsRule( ).getRule( ) instanceof Query) {
                    // put query results to the working memory
                    // location
                    removeFromQueryResults( tuple.getLeapsRule( ).getRule( ).getName( ),
                                            tuple );
                }
                else {
                    // time to pull from agenda
                    invalidateActivation( tuple );
                }
            }
        }
    }

    /**
     * copies reteoo behaviour in regards to logical assertion and does checking
     * on available tuples to see if any needs invalidation / activation as a
     * result of this retraction
     * 
     * @see WorkingMemory
     */
    public void doRetract( final InternalFactHandle factHandle,
                           final PropagationContext propagationContext ) {

        /*
         * leaps specific actions
         */
        // remove fact from all relevant fact tables container
        final Object objectClass = LeapsBuilder.getLeapsClassType( factHandle.getObject( ) );
        for (final Iterator it = this.getFactTablesList( objectClass ).iterator( ); it.hasNext( );) {
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
        ( (LeapsFactHandle) factHandle ).clearActivatedTuples( );
        // assert all tuples that are ready for activation or cancel ones
        // that are no longer
        Iterator it;
        final IteratorChain chain = new IteratorChain( );
        it = ( (LeapsFactHandle) factHandle ).getNotTupleAssemblies( );
        if (it != null) {
            chain.addIterator( it );
        }
        it = ( (LeapsFactHandle) factHandle ).getExistsTupleAssemblies( );
        if (it != null) {
            chain.addIterator( it );
        }
        for (; chain.hasNext( );) {
            FactHandleTupleAssembly tupleAssembly = ( (FactHandleTupleAssembly) chain.next( ) );
            final LeapsTuple tuple = tupleAssembly.getTuple( );
            if (tupleAssembly.getType( ) == FactHandleTupleAssembly.NOT) {
                tuple.removeBlockingNotFactHandle( tupleAssembly.getIndex( ) );
            }
            else {
                tuple.removeExistsFactHandle( tupleAssembly.getIndex( ) );
            }
            // can assert only tuples that were not eligible for activation
            // before retraction
            if (!TokenEvaluator.processAfterAllPositiveConstraintOk( tuple,
                                                                     tuple.getLeapsRule( ),
                                                                     this )) {
                // deactivate tuple that was activated inside of
                // processAfterAllPositive
                // bad design, need to rethink it
                invalidateActivation( tuple );
            }
            else {
                this.assertTuple( tuple );
            }
        }
        ( (LeapsFactHandle) factHandle ).clearExistsTuples( );
        ( (LeapsFactHandle) factHandle ).clearNotTuples( );
        // remove it from stack
        this.removeTokenFromStack( (LeapsFactHandle) factHandle );
    }

    /**
     * used when assertion / retraction adds invalidating conditions that make
     * tuple ineligible for firing
     * 
     * @param tuple
     */
    private final void invalidateActivation( final LeapsTuple tuple ) {
        final Activation activation = tuple.getActivation( );
        // tuple can already loose activation if another fact or exists fact was retracted
        // or not fact added
        if (activation != null) {
            if (activation.isActivated( )) {
                activation.remove( );
                this.getAgendaEventSupport( ).fireActivationCancelled( activation );
            }

            this.getTruthMaintenanceSystem( )
                .removeLogicalDependencies( activation,
                                            tuple.getContext( ),
                                            tuple.getLeapsRule( ).getRule( ) );
            //
            tuple.setActivation( null );
            // remove from rule / activaitons map
            FastMap activations = (FastMap) this.rulesActivationsMap.get( activation.getRule( ) );
            if (activations != null) {
                activations.remove( activation );
            }
        }
    }

    /**
     * ************* leaps section *********************
     */
    private long             idLastFireAllAt = -1;
    
    private boolean rulesAddedSinceLastFireAll = false;

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
    protected final List getFactTablesList( final Object objectClass ) {
        final ArrayList list = new ArrayList( );
        if (objectClass.getClass( ) == Class.class) {
            // interfaces
            final Class[] interfaces = ( (Class) objectClass ).getInterfaces( );
            for (int i = 0; i < interfaces.length; i++) {
                list.add( this.getFactTable( interfaces[i] ) );
            }
            // classes
            Class bufClass = (Class) objectClass;
            while (bufClass != null) {
                //
                list.add( this.getFactTable( bufClass ) );
                // and get the next class on the list
                bufClass = bufClass.getSuperclass( );
            }
        }
        else {
            list.add( this.getFactTable( objectClass ) );
        }
        return list;
    }

    /**
     * adds new leaps token on main stack
     * 
     * @param fact
     *            handle
     * @param token
     */
    protected final void pushTokenOnStack( final InternalFactHandle factHandle,
                                           final Token token ) {
        this.mainStack.push( token );
    }

    /**
     * removes leaps token on main stack
     * 
     * @param fact
     *            handle
     */
    protected final void removeTokenFromStack( final LeapsFactHandle factHandle ) {
        this.mainStack.remove( factHandle.getId( ) );
    }

    /**
     * gets leaps token from top of stack
     * 
     * @param fact
     *            handle
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
    protected FactTable getFactTable( final Object objectClass ) {
        FactTable table;
        if (this.factTables.containsKey( objectClass )) {
            table = (FactTable) this.factTables.get( objectClass );
        }
        else {
            table = new FactTable( DefaultConflictResolver.getInstance( ) );
            this.factTables.put( objectClass, table );
            // review existing rules and assign to the fact table if needed
            for (final Iterator iter = this.leapsRulesToHandlesMap.keySet( ).iterator( ); iter.hasNext( );) {
                final LeapsRule leapsRule = (LeapsRule) iter.next( );
                if (leapsRule.getNumberOfColumns( ) > 0) {
                    final List rulesHandles = (List) this.leapsRulesToHandlesMap.get( leapsRule );
                    for (final Iterator handles = rulesHandles.iterator( ); handles.hasNext( );) {
                        final LeapsRuleHandle handle = (LeapsRuleHandle) handles.next( );
                        final Object columnClassObject = leapsRule.getColumnClassObjectTypeAtPosition( handle.getDominantPosition( ) );
                        if (( objectClass.getClass( ) == Class.class
                                && columnClassObject.getClass( ) == Class.class && ( (Class) columnClassObject ).isAssignableFrom( (Class) objectClass ) )
                                // on template name
                                || ( objectClass.getClass( ) != Class.class
                                        && columnClassObject.getClass( ) != Class.class && columnClassObject.equals( objectClass ) )) {
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
    protected void addLeapsRules( final List rules ) {
        this.getLock( ).lock( );
        try {
            this.rulesAddedSinceLastFireAll = true;
            
            ArrayList ruleHandlesList;
            LeapsRule rule;
            LeapsRuleHandle ruleHandle;
            for (final Iterator it = rules.iterator( ); it.hasNext( );) {
                rule = (LeapsRule) it.next( );
                // create hashed entries for not and exists
                // check for NOT and EXISTS and create new hashed entry
                ColumnConstraints constraint;
                for (int i = 0; i < rule.getNumberOfNotColumns( ); i++) {
                    constraint = rule.getNotColumnConstraints( )[i];
                    this.getFactTable( constraint.getClassType( ) )
                        .createHashedSubTable( constraint );
                }
                for (int i = 0; i < rule.getNumberOfExistsColumns( ); i++) {
                    constraint = rule.getExistsColumnConstraints( )[i];
                    this.getFactTable( constraint.getClassType( ) )
                        .createHashedSubTable( constraint );
                }
                // some times rules do not have "normal" constraints and only
                // not and exists
                if (rule.getNumberOfColumns( ) > 0) {
                    ruleHandlesList = new ArrayList( );
                    for (int i = 0; i < rule.getNumberOfColumns( ); i++) {
                        ruleHandle = new LeapsRuleHandle( ( (LeapsFactHandleFactory) this.handleFactory ).getNextId( ),
                                                          rule,
                                                          i );
                        // 
                        if (rule.getColumnConstraintsAtPosition( i ).getClass( ) != FromConstraint.class) {
                            this.getFactTable( rule.getColumnClassObjectTypeAtPosition( i ) )
                                .addRule( this, ruleHandle );
                            //
                        }
                        else {
                            FactTable table = this.getFactTable( FromConstraintFactDriver.class );
                            table.addRule( this, ruleHandle );
                            if (table.isEmpty( )) {
                                this.assertObject( new FromConstraintFactDriver( ) );
                            }
                        }
                        ruleHandlesList.add( ruleHandle );
                    }
                    this.leapsRulesToHandlesMap.put( rule, ruleHandlesList );
                }
                else {
                    this.noPositiveColumnsRules.add( new LeapsRuleHandle( ( (LeapsFactHandleFactory) this.handleFactory ).getNextId( ),
                                                                          rule,
                                                                          0 ) );
                }
            }
        }
        finally {
            this.getLock( ).unlock( );
        }
    }

    protected void removeRule( final List rules ) {
        this.getLock( ).lock( );
        try {
            ArrayList ruleHandlesList;
            LeapsRule leapsRule;
            LeapsRuleHandle ruleHandle;
            for (final Iterator it = rules.iterator( ); it.hasNext( );) {
                leapsRule = (LeapsRule) it.next( );
                // some times rules do not have "normal" constraints and only
                // not and exists
                if (leapsRule.getNumberOfColumns( ) > 0) {
                    ruleHandlesList = (ArrayList) this.leapsRulesToHandlesMap.remove( leapsRule );
                    for (int i = 0; i < ruleHandlesList.size( ); i++) {
                        ruleHandle = (LeapsRuleHandle) ruleHandlesList.get( i );
                        // 
                        this.getFactTable( leapsRule.getColumnClassObjectTypeAtPosition( i ) )
                            .removeRule( this, ruleHandle );
                    }
                }
                //
            }
            final Rule rule = ( (LeapsRule) rules.get( 0 ) ).getRule( );
            final FastMap activations = (FastMap) this.rulesActivationsMap.remove( rule );
            if (activations != null) {
                for (final Iterator activationsIt = activations.keySet( ).iterator( ); activationsIt.hasNext( );) {
                    final Activation activation = (Activation) activationsIt.next( );
                    ( (LeapsTuple) activation.getTuple( ) ).setActivation( null );
                    this.tms.removeLogicalDependencies( activation,
                                                        activation.getPropagationContext( ),
                                                        rule );
                }
            }

            propagateQueuedActions( );
        }
        finally {
            this.getLock( ).unlock( );
        }
    }

    /**
     * main loop
     * 
     */
    public final synchronized void fireAllRules( final AgendaFilter agendaFilter )
            throws FactException {
        // If we're already firing a rule, then it'll pick up
        // the firing for any other assertObject(..) that get
        // nested inside, avoiding concurrent-modification
        // exceptions, depending on code paths of the actions.

        if (!this.firing) {
            try {
                this.firing = true;
                
                boolean nothingToProcess = false;
                while (!nothingToProcess) {
                    // check for the initial fact
                    for (Iterator rulesIt = this.noPositiveColumnsRules.iterator( ); rulesIt.hasNext( );) {
                        LeapsRule rule = ( (LeapsRuleHandle) rulesIt.next( ) ).getLeapsRule( );
                        final PropagationContextImpl context = new PropagationContextImpl( nextPropagationIdCounter( ),
                                                                                           PropagationContext.ASSERTION,
                                                                                           null, //rule.getRule( ),
                                                                                           null );
                        final LeapsTuple tuple = new LeapsTuple( new LeapsFactHandle[0],
                                                                 rule,
                                                                 context );
                        if (TokenEvaluator.processAfterAllPositiveConstraintOk( tuple,
                                                                                rule,
                                                                                this )) {
                            this.assertTuple( tuple );
                        }

                    }
                    this.noPositiveColumnsRules.clear( );
                    // normal rules with required columns
                    while (!this.mainStack.empty( )) {
                        final Token token = this.peekTokenOnTop( );
                        boolean done = false;
                        while (!done) {
                            if (!token.isResume( )) {
                                if (token.hasNextRuleHandle( )) {
                                    token.nextRuleHandle( );
                                }
                                else {
                                    // we do not pop because something might get
                                    // asserted
                                    // and placed on hte top of the stack during
                                    // firing
                                    this.removeTokenFromStack( (LeapsFactHandle) token.getDominantFactHandle( ) );
                                    done = true;
                                }
                            }
                            if (!done) {
                                try {
                                    // ok. now we have tuple, dominant fact
                                    // and
                                    // rules and ready to seek to checks if
                                    // any
                                    // agendaItem
                                    // matches on current rule
                                    TokenEvaluator.evaluate( token );
                                    // something was found so set marks for
                                    // resume processing
                                    if (token.getDominantFactHandle( ) != null) {
                                        if (token.getDominantFactHandle( )
                                                 .getObject( )
                                                 .getClass( ) != FromConstraintFactDriver.class) {
                                            token.setResume( true );
                                        }
                                        done = true;
                                    }
                                }
                                catch (final NoMatchesFoundException ex) {
                                    token.setResume( false );
                                }
                            }
                            // we put everything on agenda
                            // and if there is no modules or anything like it
                            // it would fire just activated rule
                            while (this.agenda.fireNextItem( agendaFilter )) {
                                ;
                            }
                        }
                    }
                    // pick activations generated by retraction or assert
                    // can generate activations off exists and not pending
                    // tuples
                    while (this.agenda.fireNextItem( agendaFilter )) {
                        ;
                    }
                    if (this.mainStack.empty( )) {
                        nothingToProcess = true;
                    }
                }
                // mark when method was called last time
                this.rulesAddedSinceLastFireAll = false;
                this.idLastFireAllAt = ( (LeapsFactHandleFactory) this.handleFactory ).getNextId( );
                // set all factTables to be reseeded
                for (final Iterator it = this.factTables.values( ).iterator( ); it.hasNext( );) {
                    ( (FactTable) it.next( ) ).setReseededStack( true );
                }
                // clear table that is used to trigger From constraints
                this.getFactTable( FromConstraintFactDriver.class ).clear( );
            }
            finally {
                this.firing = false;
            }
        }
    }

    protected final boolean isRulesAddedSinceLastFireAll() {
        return this.rulesAddedSinceLastFireAll;
    }
    
    protected final long getIdLastFireAllAt() {
        return this.idLastFireAllAt;
    }

    public String toString() {
        String ret = "";
        Object key;
        ret = ret + "\n" + "Working memory";
        ret = ret + "\n" + "Fact Tables by types:";
        for (final Iterator it = this.factTables.keySet( ).iterator( ); it.hasNext( );) {
            key = it.next( );
            ret = ret + "\n" + "******************   " + key;
            ret = ret + ( (FactTable) this.factTables.get( key ) ).toString( );
        }
        ret = ret + "\n" + "Stack:";
        for (final Iterator it = this.mainStack.iterator( ); it.hasNext( );) {
            ret = ret + "\n" + "\t" + it.next( );
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
            this.getAgendaEventSupport( ).fireActivationCreated( agendaItem );
        }
        else {
            final LeapsRule leapsRule = tuple.getLeapsRule( );
            AgendaGroupImpl agendaGroup = leapsRule.getAgendaGroup( );
            if (agendaGroup == null) {
                if (rule.getAgendaGroup( ) == null || rule.getAgendaGroup( ).equals( "" )
                        || rule.getAgendaGroup( ).equals( AgendaGroup.MAIN )) {
                    // Is the Rule AgendaGroup undefined? If it is use MAIN,
                    // which is added to the Agenda by default
                    agendaGroup = (AgendaGroupImpl) this.agenda.getAgendaGroup( AgendaGroup.MAIN );
                }
                else {
                    // AgendaGroup is defined, so try and get the AgendaGroup
                    // from the Agenda
                    agendaGroup = (AgendaGroupImpl) this.agenda.getAgendaGroup( rule.getAgendaGroup( ) );
                }

                if (agendaGroup == null) {
                    // The AgendaGroup is defined but not yet added to the
                    // Agenda, so create the AgendaGroup and add to the Agenda.
                    agendaGroup = new AgendaGroupImpl( rule.getAgendaGroup( ) );
                    this.agenda.addAgendaGroup( agendaGroup );
                }

                leapsRule.setAgendaGroup( agendaGroup );
            }

            // set the focus if rule autoFocus is true
            if (rule.getAutoFocus( )) {
                this.agenda.setFocus( agendaGroup );
            }

            agendaItem = new AgendaItem( context.getPropagationNumber( ),
                                         tuple,
                                         context,
                                         rule );

            agendaGroup.add( agendaItem );

            tuple.setActivation( agendaItem );
            agendaItem.setActivated( true );
            this.getAgendaEventSupport( ).fireActivationCreated( agendaItem );
        }

        // retract support
        final LeapsFactHandle[] factHandles = (LeapsFactHandle[]) tuple.getFactHandles( );
        for (int i = 0; i < factHandles.length; i++) {
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
    private final Map factTables = new FactTables( );

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

    private class FromConstraintFactDriver implements Serializable {
    }
}
