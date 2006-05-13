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
import org.drools.common.Agenda;
import org.drools.common.AgendaGroupImpl;
import org.drools.common.AgendaItem;
import org.drools.common.EventSupport;
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
class WorkingMemoryImpl extends AbstractWorkingMemory
    implements EventSupport,
        PropertyChangeListener {
    private static final long serialVersionUID       = -2524904474925421759L;

    protected final Agenda    agenda;

    private final Map         queryResults;

    private final IdentityMap leapsRulesToHandlesMap = new IdentityMap( );

    private final IdentityMap rulesActivationsMap = new IdentityMap( );

    /**
     * Construct.
     * 
     * @param ruleBase
     *            The backing rule-base.
     */
    public WorkingMemoryImpl(RuleBaseImpl ruleBase) {
        super( ruleBase, ruleBase.newFactHandleFactory( ) );
        this.agenda = new LeapsAgenda( this );
        this.agenda.setFocus( AgendaGroup.MAIN );
        //
        this.queryResults = new HashMap( );
    }

    /**
     * Create a new <code>FactHandle</code>.
     * 
     * @return The new fact handle.
     */
    FactHandle newFactHandle(Object object) {
        return ((HandleFactory) this.handleFactory).newFactHandle( object );
    }

    /**
     * @see WorkingMemory
     */
    public void setGlobal(String name,
                          Object value) {
        // Make sure the application data has been declared in the RuleBase
        Map applicationDataDefintions = ((RuleBaseImpl) this.ruleBase).getGlobals();
        Class type = (Class) applicationDataDefintions.get( name );
        if ( (type == null) ) {
            throw new RuntimeException( "Unexpected global [" + name + "]" );
        } else if ( !type.isInstance( value ) ) {
            throw new RuntimeException( "Illegal class for global. " + "Expected [" + type.getName() + "], " + "found [" + value.getClass().getName() + "]." );

        } else {
            this.getGlobals().put( name,
                                   value );
        }
    }

    public void clearAgenda() {
        this.agenda.clearAgenda();
    }
    
    public void clearAgendaGroup(String group) {
        this.agenda.clearAgendaGroup( group );
    }    

    /**
     * Returns the fact Object for the given <code>FactHandle</code>. It
     * actually returns the value from the handle, before retrieving it from
     * objects map.
     * 
     * @see WorkingMemory
     * 
     * @param handle
     *            The <code>FactHandle</code> reference for the
     *            <code>Object</code> lookup
     * 
     */
    public Object getObject(FactHandle handle) {
        return ((FactHandleImpl) handle).getObject();
    }

    public List getObjects(Class objectClass) {
        List list = new LinkedList();
        for ( Iterator it = this.getFactTable( objectClass ).iterator(); it.hasNext(); ) {
            list.add( ((FactHandleImpl)it.next()).getObject() );
        }

        return list;
    }

    /**
     * @see WorkingMemory
     */
    public boolean containsObject(FactHandle handle) {
        // return this.objects.containsKey( ((FactHandleImpl) handle).getId() );
        return ((FactHandleImpl) handle).getObject() != null;
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle assertObject(Object object) throws FactException {
        return assertObject( object, /* Not-Dynamic */
                             false,
                             false,
                             null,
                             null );
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle assertLogicalObject(Object object) throws FactException {
        return assertObject( object, /* Not-Dynamic */
                             false,
                             true,
                             null,
                             null );
    }

    public FactHandle assertObject(Object object,
                                   boolean dynamic) throws FactException {
        return assertObject( object,
                             dynamic,
                             false,
                             null,
                             null );
    }

    public FactHandle assertLogicalObject(Object object,
                                          boolean dynamic) throws FactException {
        return assertObject( object,
                             dynamic,
                             true,
                             null,
                             null );
    }

    /**
     * 
     * @param object
     * @param dynamic
     * @param logical
     * @param rule
     * @param agendaItem
     * @return
     * @throws FactException
     * 
     * @see WorkingMemory
     */
    public FactHandle assertObject( Object object,
                                   boolean dynamic,
                                   boolean logical,
                                   Rule rule,
                                   Activation activation ) throws FactException {
        FactHandleImpl handle ;
        this.getLock().lock( );
        try {
            // check if the object already exists in the WM
            handle = (FactHandleImpl) this.identityMap.get( object );

            // lets see if the object is already logical asserted
            FactStatus logicalState = (FactStatus) this.equalsMap.get( object );
            if (logicalState == null) {
                logicalState = STATUS_NEW;
            }

            // This object is already STATED, we cannot make it justifieable
            if (( logical ) && ( logicalState.getStatus( ) == WorkingMemoryImpl.STATED )) {
                return null;
            }

            // return if there is already a logical handle
            if (( logical ) && ( logicalState.getStatus( ) == WorkingMemoryImpl.JUSTIFIED )) {
                addLogicalDependency( logicalState.getHandle( ),
                                      activation,
                                      activation.getPropagationContext( ),
                                      rule );
                
                return logicalState.getHandle( );
            }

            // if we have a handle and this STATED fact was previously STATED
            if (( handle != null ) && ( !logical )
                    && ( logicalState.getStatus( ) == WorkingMemoryImpl.STATED )) {
                return handle;
            }

            if (!logical) {
                // If this stated assertion already has justifications then we
                // need
                // to cancel them
                if (logicalState.getStatus( ) == WorkingMemoryImpl.JUSTIFIED) {
                    handle = (FactHandleImpl) logicalState.getHandle( );

                    removeLogicalDependencies( handle );
                }
                else {
                    handle = (FactHandleImpl) newFactHandle( object );
                }

                putObject( handle, object );

                if (logicalState != WorkingMemoryImpl.STATUS_NEW) {
                    // make sure status is stated
                    logicalState.setStatus( WorkingMemoryImpl.STATED );
                    logicalState.incCounter( );
                }
                else {
                    this.equalsMap.put( object,
                                        new FactStatus( WorkingMemoryImpl.STATED, 1 ) );
                }

                if (dynamic) {
                    addPropertyChangeListener( object );
                }

            }
            else {
                handle = (FactHandleImpl) logicalState.getHandle( );
                // we create a lookup handle for the first asserted equals
                // object
                // all future equals objects will use that handle
                if (handle == null) {
                    handle = (FactHandleImpl) newFactHandle( object );

                    putObject( handle, object );

                    this.equalsMap.put( object,
                                        new FactStatus( WorkingMemoryImpl.JUSTIFIED, handle ) );
                }

                addLogicalDependency( handle,
                                      activation,
                                      activation.getPropagationContext( ),
                                      rule );
            }

            // new leaps stack token
            PropagationContextImpl context = new PropagationContextImpl( nextPropagationIdCounter( ),
                                                                         PropagationContext.ASSERTION,
                                                                         rule,
                                                                         activation );

            this.pushTokenOnStack( handle, new Token( this, handle, context ) );

            this.workingMemoryEventSupport.fireObjectAsserted( context, handle, object );

            // determine what classes it belongs to put it into the "table" on
            // class name key
            Class objectClass = object.getClass( );
            for (Iterator tables = this.getFactTablesList( objectClass ).iterator( ); tables.hasNext( );) {
                FactTable factTable = (FactTable) tables.next( );
                // adding fact to container
                factTable.add( handle );
                // inspect all tuples for exists and not conditions and activate
                // /
                // deactivate agenda items
                for (Iterator tuples = factTable.getTuplesIterator( ); tuples.hasNext( );) {
                    LeapsTuple tuple = (LeapsTuple) tuples.next( );
                    boolean tupleWasReadyForActivation = tuple.isReadyForActivation( );
                    if (!tuple.isActivationNull( )) {
                        // check not constraints only on activated tuples to see
                        // if
                        // we need to deactivate
                        ColumnConstraints[] not = tuple.getLeapsRule( )
                                                       .getNotColumnConstraints( );
                        for (int i = 0, length = not.length; i < length; i++) {
                            ColumnConstraints constraint = not[i];
                            if (!tuple.isBlockingNotFactHandle( i )
                                    && constraint.getClassType( )
                                                 .isAssignableFrom( objectClass )
                                    && constraint.isAllowed( handle, tuple, this )) {
                                tuple.setBlockingNotFactHandle( handle, i );
                                handle.addNotTuple( tuple, i );
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
                        ColumnConstraints[] exists = tuple.getLeapsRule( )
                                                          .getExistsColumnConstraints( );
                        for (int i = 0, length = exists.length; i < length; i++) {
                            ColumnConstraints constraint = exists[i];
                            if (!tuple.isExistsFactHandle( i )
                                    && constraint.getClassType( )
                                                 .isAssignableFrom( objectClass )
                                    && constraint.isAllowed( handle, tuple, this )) {
                                tuple.setExistsFactHandle( handle, i );
                                handle.addExistsTuple( tuple, i );
                            }
                        }
                        // check and see if we need activate
                        // activate only if tuple was not ready for it before
                        if (tuple.isReadyForActivation( ) && !tupleWasReadyForActivation) {
                            // ready to activate
                            tuple.setContext( new PropagationContextImpl( nextPropagationIdCounter( ),
                                                                          PropagationContext.ASSERTION,
                                                                          tuple.getLeapsRule( )
                                                                               .getRule( ),
                                                                          null ) );

                            this.assertTuple( tuple );
                        }
                    }
                }
            }
            propagateQueuedActions( );
        }
        finally {
            this.getLock( ).unlock( );
        }

        return handle;
    }

    /**
     * Associate an object with its handle.
     * 
     * @param handle
     *            The handle.
     * @param object
     *            The object.
     */
    void putObject(FactHandle handle,
                     Object object) {

        this.identityMap.put( object,
                              handle );
    }

    Object removeObject(FactHandle handle) {

        this.identityMap.remove( ((FactHandleImpl) handle).getObject() );

        return ((FactHandleImpl) handle).getObject();
    }

    /**
     * copies reteoo behaviour in regards to logical assertion 
     * and does checking on available tuples to see if any needs
     * invalidation / activation as a result of this retraction
     * 
     * @see WorkingMemory
     */
    public void retractObject(FactHandle handle,
                              boolean removeLogical,
                              boolean updateEqualsMap,
                              Rule rule,
                              Activation activation ) throws FactException {
        this.getLock().lock( );
        try {
            removePropertyChangeListener( handle );
            //
            // end leaps specific actions
            //
            Object oldObject = removeObject( handle );

            /* check to see if this was a logical asserted object */
            if (removeLogical) {
                removeLogicalDependencies( handle );
            }

            if (removeLogical || updateEqualsMap) {
                FactStatus status = (FactStatus) this.equalsMap.get( oldObject );
                if (status != null) {
                    status.decCounter( );
                    if (status.getCounter( ) <= 0) {
                        this.equalsMap.remove( oldObject );
                    }
                }
            }

            /*
             * leaps specific actions
             */
            // remove fact from all relevant fact tables container
            for (Iterator it = this.getFactTablesList( ( (FactHandleImpl) handle ).getObject( )
                                                                                  .getClass( ) )
                                   .iterator( ); it.hasNext( );) {
                ( (FactTable) it.next( ) ).remove( handle );
            }

            // 0. remove activated tuples
            Iterator tuples = ( (FactHandleImpl) handle ).getActivatedTuples( );
            for (; tuples != null && tuples.hasNext( );) {
                LeapsTuple tuple = (LeapsTuple) tuples.next( );
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

            // 1. remove fact for nots and exists tuples
            IdentityMap tuplesNotReadyForActivation = new IdentityMap( );
            FactHandleTupleAssembly assembly;
            LeapsTuple tuple;
            Iterator it;
            it = ( (FactHandleImpl) handle ).getNotTupleAssemblies( );
            if (it != null) {
                for (; it.hasNext( );) {
                    assembly = (FactHandleTupleAssembly) it.next( );
                    tuple = assembly.getTuple( );
                    if (!tuple.isReadyForActivation( )) {
                        tuplesNotReadyForActivation.put( tuple, tuple );
                    }
                    tuple.removeBlockingNotFactHandle( assembly.getIndex( ) );
                    TokenEvaluator.evaluateNotCondition( new FactHandleImpl( ( (FactHandleImpl) handle ).getId( ) + 1,
                                                                             null ),
                                                         assembly.getIndex( ),
                                                         tuple,
                                                         this );
                }
            }
            it = ( (FactHandleImpl) handle ).getExistsTupleAssemblies( );
            if (it != null) {
                for (; it.hasNext( );) {
                    assembly = (FactHandleTupleAssembly) it.next( );
                    tuple = assembly.getTuple( );
                    if (!tuple.isReadyForActivation( )) {
                        tuplesNotReadyForActivation.put( tuple, tuple );
                    }
                    tuple.removeExistsFactHandle( assembly.getIndex( ) );
                    TokenEvaluator.evaluateExistsCondition( new FactHandleImpl( ( (FactHandleImpl) handle ).getId( ) + 1,
                                                                                null ),
                                                            assembly.getIndex( ),
                                                            tuple,
                                                            this );
                }
            }
            // 2. assert all tuples that are ready for activation or cancel ones
            // that are no longer
            IteratorChain chain = new IteratorChain( );
            it = ( (FactHandleImpl) handle ).getNotTupleAssemblies( );
            if (it != null) {
                chain.addIterator( it );
            }
            it = ( (FactHandleImpl) handle ).getExistsTupleAssemblies( );
            if (it != null) {
                chain.addIterator( it );
            }
            for (; chain.hasNext( );) {
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
            this.removeTokenFromStack( (FactHandleImpl) handle );

            // even support
            PropagationContextImpl context = new PropagationContextImpl( nextPropagationIdCounter( ),
                                                                         PropagationContext.RETRACTION,
                                                                         rule,
                                                                         activation );

            this.workingMemoryEventSupport.fireObjectRetracted( context, handle, oldObject );
            
            propagateQueuedActions();
        }
        finally {
            this.getLock().unlock( );
        }
    }

    /**
     * used when assertion / retraction adds invalidating conditions that 
     * make tuple ineligible for firing
     * 
     * @param tuple
     */
    private final void invalidateActivation( LeapsTuple tuple ) {
        Activation activation = tuple.getActivation( );
        if (!tuple.isReadyForActivation( ) && !tuple.isActivationNull( )) {
            // invalidate agenda agendaItem
            if (activation.isActivated( )) {
                activation.remove( );
                getAgendaEventSupport( ).fireActivationCancelled( activation );
            }
            //
            tuple.setActivation( null );
        }
        // remove logical dependency
        if (activation != null) {
            this.removeLogicalDependencies( activation,
                                            tuple.getContext( ),
                                            tuple.getLeapsRule( ).getRule( ) );
        }
    }

    
    
    public void addLogicalDependency( FactHandle handle,
                                     Activation activation,
                                     PropagationContext context,
                                     Rule rule ) throws FactException {
        super.addLogicalDependency( handle, activation, context, rule );

        LinkedList activations = (LinkedList) this.rulesActivationsMap.get( rule );
        if (activations == null) {
            activations = new LinkedList( );
            this.rulesActivationsMap.put( rule, activations );
        }
        activations.add( activation );
    }

    
    public void removeLogicalDependencies( Activation activation,
                                          PropagationContext context,
                                          Rule rule ) throws FactException {
        super.removeLogicalDependencies( activation, context, rule );
    }

    /**
     * @see WorkingMemory
     */
    public void modifyObject( FactHandle handle,
                             Object object,
                             Rule rule,
                             Activation activation ) throws FactException {
        this.getLock( ).lock( );
        try {

            this.retractObject( handle );

            Object originalObject = removeObject( handle );

            if (originalObject == null) {
                throw new NoSuchFactObjectException( handle );
            }

            /* check to see if this is a logically asserted object */
            FactHandleImpl handleImpl = (FactHandleImpl) this.assertObject( object,
                                                                            false,
                                                                            false,
                                                                            rule,
                                                                            activation );

            if (this.justified.get( handleImpl.getId( ) ) != null) {
                this.equalsMap.remove( originalObject );
                this.equalsMap.put( object, new FactStatus( WorkingMemoryImpl.JUSTIFIED,
                                                            handleImpl ) );
            }

            this.workingMemoryEventSupport.fireObjectModified( new PropagationContextImpl( nextPropagationIdCounter( ),
                                                                                           PropagationContext.MODIFICATION,
                                                                                           rule,
                                                                                           activation ),
                                                               handle,
                                                               ( (FactHandleImpl) handle ).getObject( ),
                                                               object );
            propagateQueuedActions( );
        }
        finally {
            this.getLock( ).unlock( );
        }
    }

    /**
     * ************* leaps section *********************
     */
    private long            idLastFireAllAt = -1;

    /**
     * algorithm stack.
     */
    private final TokenStack           mainStack           = new TokenStack();

    /**
     * generates or just return List of internal factTables that correspond a
     * class can be used to generate factTables
     * 
     * @return
     */
    protected final List getFactTablesList(Class c) {
        ArrayList list = new ArrayList();
        // interfaces
        Class[] interfaces = c.getInterfaces();
        for ( int i = 0; i < interfaces.length; i++ ) {
            list.add( this.getFactTable( interfaces[i] ) );
        }
        // classes
        Class bufClass = c;
        while ( bufClass != null ) {
            //
            list.add( this.getFactTable( bufClass ) );
            // and get the next class on the list
            bufClass = bufClass.getSuperclass();
        }
        return list;
    }

    /**
     * adds new leaps token on main stack
     * 
     * @param fact handle
     * @param token
     */
    protected final void pushTokenOnStack(FactHandleImpl factHandle, Token token) {
        this.mainStack.push( token );
    }

    /**
     * removes leaps token on main stack
     * 
     * @param fact handle
     */
    protected final void removeTokenFromStack(FactHandleImpl factHandle) {
        this.mainStack.remove( factHandle.getId() );
    }

    /**
     * gets leaps token from top of stack
     * 
     * @param fact handle
     */
    protected final Token peekTokenOnTop(){
        return (Token)this.mainStack.peek();
    }
    /**
     * get leaps fact table of specific type (class)
     * 
     * @param type
     *            of objects
     * @return fact table of requested class type
     */
    protected FactTable getFactTable(Class c) {
        FactTable table;
        if ( this.factTables.containsKey( c ) ) {
            table = (FactTable) this.factTables.get( c );
        } else {
            table = new FactTable( DefaultConflictResolver.getInstance( ) );
            this.factTables.put( c, table );
            // review existing rules and assign to the fact table if needed
            for (Iterator iter = this.leapsRulesToHandlesMap.keySet( ).iterator( ); iter.hasNext( );) {
                LeapsRule leapsRule = (LeapsRule) iter.next( );
                if (leapsRule.getNumberOfColumns( ) > 0) {
                    List rulesHandles = (List) this.leapsRulesToHandlesMap.get( leapsRule );
                    for (Iterator handles = rulesHandles.iterator( ); handles.hasNext( );) {
                        RuleHandle handle = (RuleHandle) handles.next( );
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
    protected void addLeapsRules( List rules ) {
        this.getLock( ).lock( );
        try {
            ArrayList ruleHandlesList;
            LeapsRule rule;
            RuleHandle ruleHandle;
            for (Iterator it = rules.iterator( ); it.hasNext( );) {
                rule = (LeapsRule) it.next( );
                // some times rules do not have "normal" constraints and only
                // not and exists
                if (rule.getNumberOfColumns( ) > 0) {
                    ruleHandlesList = new ArrayList( );
                    for (int i = 0; i < rule.getNumberOfColumns( ); i++) {
                        ruleHandle = new RuleHandle( ( (HandleFactory) this.handleFactory ).getNextId( ),
                                                     rule,
                                                     i );
                        // 
                        this.getFactTable( rule.getColumnClassObjectTypeAtPosition( i ) )
                            .addRule( this, ruleHandle );
                        //
                        ruleHandlesList.add( ruleHandle );
                    }
                    this.leapsRulesToHandlesMap.put( rule, ruleHandlesList );
                }
                else {
                    // to pick up rules that do not require columns, only not
                    // and exists
                    PropagationContextImpl context = new PropagationContextImpl( nextPropagationIdCounter( ),
                                                                                 PropagationContext.ASSERTION,
                                                                                 null,
                                                                                 null );

                    TokenEvaluator.processAfterAllPositiveConstraintOk( new LeapsTuple( new FactHandleImpl[0],
                                                                                        rule,
                                                                                        context ),
                                                                        rule,
                                                                        this );
                }
            }
        }
        finally {
            this.getLock( ).unlock( );
        }
    }

    protected void removeRule( List rules ) {
        this.getLock( ).lock( );
        try {
            ArrayList ruleHandlesList;
            LeapsRule leapsRule;
            RuleHandle ruleHandle;
            for (Iterator it = rules.iterator( ); it.hasNext( );) {
                leapsRule = (LeapsRule) it.next( );
                // some times rules do not have "normal" constraints and only
                // not and exists
                if (leapsRule.getNumberOfColumns( ) > 0) {
                    ruleHandlesList = (ArrayList) this.leapsRulesToHandlesMap.remove( leapsRule );
                    for (int i = 0; i < ruleHandlesList.size( ); i++) {
                        ruleHandle = (RuleHandle) ruleHandlesList.get( i );
                        // 
                        this.getFactTable( leapsRule.getColumnClassObjectTypeAtPosition( i ) )
                            .removeRule( ruleHandle );
                    }
                }
                //
            }
            Rule rule = ((LeapsRule)rules.get(0)).getRule( );
            List activations = (List) this.rulesActivationsMap.remove( rule );
            if (activations != null) {
                for (Iterator activationsIt = activations.iterator( ); activationsIt.hasNext( );) {
                    Activation activation = (Activation) activationsIt.next( );
                    ((LeapsTuple)activation.getTuple()).setActivation(null);
                    this.removeLogicalDependencies( activation,
                                                    activation.getPropagationContext( ),
                                                    rule );
                }
            }
            
            propagateQueuedActions();
        }
        finally {
            this.getLock( ).unlock( );
        }
    }

    /**
     * main loop
     * 
     */
    public final synchronized void fireAllRules( AgendaFilter agendaFilter )
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
                    // normal rules with required columns
                    while (!this.mainStack.empty( )) {
                        Token token = (Token) this.peekTokenOnTop( );
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
                                    this.removeTokenFromStack( token.getDominantFactHandle( ) );
                                    done = true;
                                }
                            }
                            if (!done) {
                                try {
                                    // ok. now we have tuple, dominant fact and
                                    // rules and ready to seek to checks if any
                                    // agendaItem
                                    // matches on current rule
                                    TokenEvaluator.evaluate( token );
                                    // something was found so set marks for
                                    // resume processing
                                    if (token.getDominantFactHandle( ) != null) {
                                        token.setResume( true );
                                        done = true;
                                    }
                                }
                                catch (NoMatchesFoundException ex) {
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
                this.idLastFireAllAt = ( (HandleFactory) this.handleFactory ).getNextId( );
                // set all factTables to be reseeded
                for (Iterator it = this.factTables.values( ).iterator(); it.hasNext( );) {
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
        for ( Iterator it = this.factTables.keySet().iterator(); it.hasNext(); ) {
            key = it.next();
            ret = ret + "\n" + "******************   " + key;
            ret = ret + ((FactTable) this.factTables.get( key )).toString();
        }
        ret = ret + "\n" + "Stack:";
        for (Iterator it = this.mainStack.iterator( ); it.hasNext( );) {
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
    public final void assertTuple(LeapsTuple tuple) {
        PropagationContext context = tuple.getContext();
        Rule rule = tuple.getLeapsRule().getRule();
        // if the current Rule is no-loop and the origin rule is the same then
        // return
        if ( rule.getNoLoop() && rule.equals( context.getRuleOrigin() ) ) {
            return;
        }
        //
        Duration dur = rule.getDuration();

        Activation agendaItem;
        if ( dur != null && dur.getDuration( tuple ) > 0 ) {
            agendaItem = new ScheduledAgendaItem( context.getPropagationNumber(),
                                                  tuple,
                                                  this.agenda,
                                                  context,
                                                  rule );
            this.agenda.scheduleItem( (ScheduledAgendaItem) agendaItem );
            tuple.setActivation( agendaItem );
            agendaItem.setActivated( true );
            this.getAgendaEventSupport().fireActivationCreated( agendaItem );
        } else {
            LeapsRule leapsRule = tuple.getLeapsRule();
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
                    this.getAgenda().addAgendaGroup( agendaGroup );
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

            // retract support
            FactHandleImpl[] factHandles = (FactHandleImpl[]) tuple.getFactHandles();
            for ( int i = 0; i < factHandles.length; i++ ) {
                factHandles[i].addActivatedTuple( tuple );
            }
        }
    }

    protected long nextPropagationIdCounter() {
        return ++this.propagationIdCounter;
    }

    public void dispose() {
        ((RuleBaseImpl) this.ruleBase).disposeWorkingMemory( this );
    }
    
    public QueryResults getQueryResults(String queryName) {
        IdentityMap map = ( IdentityMap ) this.queryResults.get( queryName );
        if ( map == null ) {
            return null;
        }

        LinkedList list = new LinkedList();
        for(Iterator it = map.keySet().iterator(); it.hasNext();) {
            list.add(it.next());
        }
        if(!list.isEmpty()) {
            Query queryRule = (Query)((LeapsTuple) list.get(0)).getLeapsRule().getRule();
            return new LeapsQueryResults( list, queryRule, this );
        }
        else {
            return null;
        }
        
        // 
    }

    void addToQueryResults(String query, Tuple tuple) {
        IdentityMap map = (IdentityMap) this.queryResults.get( query );
        if (map == null) {
            map = new IdentityMap( );
            this.queryResults.put( query, map );
        }
        map.put( tuple, tuple );
    }
    
    void removeFromQueryResults(String query, Tuple tuple) {
        IdentityMap map = (IdentityMap) this.queryResults.get( query );
        if (map != null) {
            map.remove(tuple);
        }
    }

    public AgendaGroup getFocus() {
        return this.agenda.getFocus();
    }

    public void setFocus(String focus) {
        this.agenda.setFocus( focus );
    }

    public void setFocus(AgendaGroup focus) {
        this.agenda.setFocus( focus );
    }

    public Agenda getAgenda() {
        return this.agenda;
    }

    /**
     * to store facts to cursor over it
     */
    private final Map factTables = new Map( ) {
                                     private LinkedList tables = new LinkedList( );

                                     private HashMap    map    = new HashMap( );
                                     public int size() {
                                         return tables.size();
                                     }
                                     public void clear() {
                                         tables.clear();
                                         map.clear();
                                     }
                                     public boolean isEmpty() {
                                         return tables.isEmpty();
                                     }

                                     public boolean containsKey( Object key ) {
                                         return map.containsKey(key);
                                     }

                                     public boolean containsValue( Object value ) {
                                         return map.containsValue(value);
                                     }

                                     public Collection values() {
                                         return tables;
                                     }

                                     public void putAll( Map t ) {
                                         tables.addAll(t.values());
                                         map.putAll(t);
                                     }

                                     public Set entrySet() {
                                         return map.entrySet();
                                     }

                                     public Set keySet() {
                                         return map.keySet();
                                     }

                                     public Object get( Object key ) {
                                         return map.get(key);
                                         
                                     }

                                     public Object remove( Object key ) {
                                         Object ret = map.remove(key);
                                         tables.remove(ret);
                                         return ret;
                                     }

                                     public Object put( Object key, Object value ) {
                                         tables.add(value);
                                         map.put(key, value);
                                         return value;
                                     }
                                 };

}
