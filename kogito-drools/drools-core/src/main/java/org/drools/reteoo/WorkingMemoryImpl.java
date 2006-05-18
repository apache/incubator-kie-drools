package org.drools.reteoo;

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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.NoSuchFactHandleException;
import org.drools.NoSuchFactObjectException;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.base.DroolsQuery;
import org.drools.common.Agenda;
import org.drools.common.EventSupport;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemoryActions;
import org.drools.common.LogicalDependency;
import org.drools.common.PropagationContextImpl;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;
import org.drools.util.IdentityMap;
import org.drools.util.PrimitiveLongMap;
import org.drools.util.PrimitiveLongStack;
import org.drools.util.concurrent.locks.Lock;
import org.drools.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of <code>WorkingMemory</code>.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 */
public class WorkingMemoryImpl
    implements
    WorkingMemory,
    InternalWorkingMemoryActions,
    EventSupport,
    PropertyChangeListener {
    // ------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------
    private static final Class[]            ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[]{PropertyChangeListener.class};

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The arguments used when adding/removing a property change listener. */
    private final Object[]                  addRemovePropertyChangeListenerArgs           = new Object[]{this};

    /** The actual memory for the <code>JoinNode</code>s. */
    private final PrimitiveLongMap          nodeMemories                                  = new PrimitiveLongMap( 32,
                                                                                                                  8 );

    /** Global values which are associated with this memory. */
    private final Map                       globals                                       = new HashMap();

    //    /** Handle-to-object mapping. */
    //    private final PrimitiveLongMap          objects                                       = new PrimitiveLongMap( 32,
    //                                                                                                                  8 );

    /** Object-to-handle mapping. */
    private final Map                       identityMap                                   = new IdentityMap();
    private final Map                       equalsMap                                     = new HashMap();

    private final PrimitiveLongMap          justified                                     = new PrimitiveLongMap( 8,
                                                                                                                  32 );
    private final PrimitiveLongStack        factHandlePool                                = new PrimitiveLongStack();

    private Map                             queryResults                                  = Collections.EMPTY_MAP;

    /** Support for logical assertions */
    private static final String             STATED                                        = "STATED";
    private static final String             JUSTIFIED                                     = "JUSTIFIED";
    private static final String             NEW                                           = "NEW";
    private static final FactStatus         STATUS_NEW                                    = new FactStatus( NEW,
                                                                                                            0 );

    /** The eventSupport */
    private final WorkingMemoryEventSupport workingMemoryEventSupport                     = new WorkingMemoryEventSupport( this );
    private final AgendaEventSupport        agendaEventSupport                            = new AgendaEventSupport( this );

    private ReentrantLock                   lock                                          = new ReentrantLock();

    /** The <code>RuleBase</code> with which this memory is associated. */
    private final RuleBaseImpl              ruleBase;

    private final FactHandleFactory         handleFactory;

    /** Rule-firing agenda. */
    private final Agenda                    agenda;

    /** Flag to determine if a rule is currently being fired. */
    private boolean                         firing;

    private long                            propagationIdCounter;
    
    private List                            factQueue                                     = new ArrayList();

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param ruleBase
     *            The backing rule-base.
     */
    public WorkingMemoryImpl(RuleBaseImpl ruleBase) {
        this.ruleBase = ruleBase;
        this.agenda = new Agenda( this );
        this.handleFactory = this.ruleBase.newFactHandleFactory();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    public void addEventListener(WorkingMemoryEventListener listener) {
        this.workingMemoryEventSupport.addEventListener( listener );
    }

    public void removeEventListener(WorkingMemoryEventListener listener) {
        this.workingMemoryEventSupport.removeEventListener( listener );
    }

    public List getWorkingMemoryEventListeners() {
        return this.workingMemoryEventSupport.getEventListeners();
    }

    public void addEventListener(AgendaEventListener listener) {
        this.agendaEventSupport.addEventListener( listener );
    }

    public void removeEventListener(AgendaEventListener listener) {
        this.agendaEventSupport.removeEventListener( listener );
    }

    public List getAgendaEventListeners() {
        return this.agendaEventSupport.getEventListeners();
    }

    /**
     * Create a new <code>FactHandle</code>.
     * 
     * @return The new fact handle.
     */
    FactHandle newFactHandle() {
        if ( !this.factHandlePool.isEmpty() ) {
            return this.handleFactory.newFactHandle( this.factHandlePool.pop() );
        }

        return this.handleFactory.newFactHandle();
    }

    /**
     * @see WorkingMemory
     */
    public Map getGlobals() {
        return this.globals;
    }

    /**
     * @see WorkingMemory
     */
    public void setGlobal(String name,
                          Object value) {  
        // Make sure the global has been declared in the RuleBase        
        Map globalDefintions = this.ruleBase.getGlobals();
        Class type = (Class) globalDefintions.get( name );
        if ( (type == null) ) {
            throw new RuntimeException( "Unexpected global [" + name + "]" );
        } else if ( !type.isInstance( value ) ) {
            throw new RuntimeException( "Illegal class for global. " + "Expected [" + type.getName() + "], " + "found [" + value.getClass().getName() + "]." );

        } else {
            this.globals.put( name,
                              value );
        }
    }

    /**
     * @see WorkingMemory
     */
    public Object getGlobal(String name) {
        Object object = this.globals.get( name );
        return object;
    }

    /**
     * Retrieve the rule-firing <code>Agenda</code> for this
     * <code>WorkingMemory</code>.
     * 
     * @return The <code>Agenda</code>.
     */
    public Agenda getAgenda() {
        return this.agenda;
    }

    /**
     * Clear the Agenda
     */
    public void clearAgenda() {
        this.agenda.clearAgenda();
    }
    
    /**
     * Clear the Agenda Group
     */
    public void clearAgendaGroup(String group) {
        this.agenda.clearAgendaGroup(group);
    }    

    /**
     * @see WorkingMemory
     */
    public RuleBase getRuleBase() {
        return this.ruleBase;
    }

    /**
     * @see WorkingMemory
     */
    public void fireAllRules() throws FactException {
        fireAllRules( null );
    }

    public synchronized void fireAllRules(AgendaFilter agendaFilter) throws FactException {
        // If we're already firing a rule, then it'll pick up
        // the firing for any other assertObject(..) that get
        // nested inside, avoiding concurrent-modification
        // exceptions, depending on code paths of the actions.

        if ( !this.firing ) {
            try {
                this.firing = true;

                while ( this.agenda.fireNextItem( agendaFilter ) ) {
                    ;
                }
            } finally {
                this.firing = false;
            }
        }
    }

    /**
     * Returns the fact Object for the given <code>FactHandle</code>. It
     * actually attemps to return the value from the handle, before retrieving
     * it from objects map.
     * 
     * @see WorkingMemory
     * 
     * @param handle
     *            The <code>FactHandle</code> reference for the
     *            <code>Object</code> lookup
     * 
     */
    public Object getObject(FactHandle handle) {
        InternalFactHandle handleImpl = (InternalFactHandle) handle;
        return handleImpl.getObject();
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle getFactHandle(Object object) {
        FactHandle factHandle = (FactHandle) this.identityMap.get( object );

        if ( factHandle == null ) {
            throw new NoSuchFactHandleException( object );
        }

        return factHandle;
    }

    public List getFactHandles() {
        return new ArrayList( this.identityMap.values() );
    }

    /**
     * A helper method used to avoid lookups when iterating over facthandles and 
     * objects at once. 
     * DO NOT MAKE THIS METHOD PUBLIC UNLESS YOU KNOW WHAT YOU ARE DOING
     * 
     * @return
     */
    Map getFactHandleMap() {
        return Collections.unmodifiableMap( this.identityMap );
    }

    /**
     * @see WorkingMemory
     */
    public List getObjects() {
        return new ArrayList( this.identityMap.keySet() );
    }

    public List getObjects(Class objectClass) {
        List matching = new java.util.LinkedList();
        for ( Iterator objIter = this.identityMap.keySet().iterator(); objIter.hasNext(); ) {
            Object obj = objIter.next();

            if ( objectClass.isInstance( obj ) ) {
                matching.add( obj );
            }
        }

        return matching;
    }

    public QueryResults getQueryResults(String query) {
        FactHandle handle = assertObject( new DroolsQuery( query ) );
        QueryTerminalNode node = (QueryTerminalNode) this.queryResults.remove( query );
        if ( node == null ) {
            retractObject( handle );
            return null;
        }

        List list = (List) this.nodeMemories.remove( node.getId() );

        retractObject( handle );
        if ( list == null ) {
            return null;
        }
        return new QueryResults( list,
                                 (Query) node.getRule(),
                                 this );
    }

    void setQueryResults(String query,
                         QueryTerminalNode node) {
        if ( this.queryResults == Collections.EMPTY_MAP ) {
            this.queryResults = new HashMap();
        }
        this.queryResults.put( query,
                               node );
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

    /**
     * @see WorkingMemory
     */
    public boolean containsObject(FactHandle handle) {
        return this.identityMap.containsKey( getObject( handle ) );
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

    public FactHandle assertObject(Object object,
                                   boolean dynamic,
                                   boolean logical,
                                   Rule rule,
                                   Activation activation) throws FactException {
        FactHandleImpl handle = null;
        this.lock.lock();
        try {

            // check if the object already exists in the WM
            handle = (FactHandleImpl) this.identityMap.get( object );

            // lets see if the object is already logical asserted
            FactStatus logicalState = (FactStatus) this.equalsMap.get( object );
            if ( logicalState == null ) {
                logicalState = STATUS_NEW;
            }

            // This object is already STATED, we cannot make it justifieable
            if ( (logical) && (logicalState.getStatus() == WorkingMemoryImpl.STATED) ) {
                return null;
            }

            // return if there is already a logical handle
            if ( (logical) && (logicalState.getStatus() == WorkingMemoryImpl.JUSTIFIED) ) {
                addLogicalDependency( logicalState.getHandle(),
                                      activation,
                                      activation.getPropagationContext(),
                                      rule );
                return logicalState.getHandle();
            }

            // if we have a handle and this STATED fact was previously STATED
            if ( (handle != null) && (!logical) && (logicalState.getStatus() == WorkingMemoryImpl.STATED) ) {
                return handle;
            }

            if ( !logical ) {
                // If this stated assertion already has justifications then we need
                // to cancel them
                if ( logicalState.getStatus() == WorkingMemoryImpl.JUSTIFIED ) {
                    handle = logicalState.getHandle();
                    removeLogicalDependencies( handle );
                } else {
                    handle = (FactHandleImpl) newFactHandle();
                }

                putObject( handle,
                           object );

                if ( logicalState != WorkingMemoryImpl.STATUS_NEW ) {
                    // make sure status is stated
                    logicalState.setStatus( WorkingMemoryImpl.STATED );
                    logicalState.incCounter();
                } else {
                    this.equalsMap.put( object,
                                        new FactStatus( WorkingMemoryImpl.STATED,
                                                        1 ) );
                }

                if ( dynamic ) {
                    addPropertyChangeListener( object );
                }
            } else {

                handle = logicalState.getHandle();
                // we create a lookup handle for the first asserted equals object
                // all future equals objects will use that handle
                if ( handle == null ) {
                    handle = (FactHandleImpl) newFactHandle();

                    putObject( handle,
                               object );

                    this.equalsMap.put( object,
                                        new FactStatus( WorkingMemoryImpl.JUSTIFIED,
                                                        handle ) );
                }
                addLogicalDependency( handle,
                                      activation,
                                      activation.getPropagationContext(),
                                      rule );
            }

            handle.setObject( object );

            PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                PropagationContext.ASSERTION,
                                                                                rule,
                                                                                activation );

            this.ruleBase.assertObject( handle,
                                        object,
                                        propagationContext,
                                        this );

            this.workingMemoryEventSupport.fireObjectAsserted( propagationContext,
                                                               handle,
                                                               object );
            
            if ( !this.factQueue.isEmpty() ) {
                propagateQueuedActions();
            }            
        } finally {
            this.lock.unlock();
        }
        return handle;
    }

    private void addPropertyChangeListener(Object object) {
        try {
            Method method = object.getClass().getMethod( "addPropertyChangeListener",
                                                         WorkingMemoryImpl.ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES );

            method.invoke( object,
                           this.addRemovePropertyChangeListenerArgs );
        } catch ( NoSuchMethodException e ) {
            System.err.println( "Warning: Method addPropertyChangeListener not found" + " on the class " + object.getClass() + " so Drools will be unable to process JavaBean" + " PropertyChangeEvents on the asserted Object" );
        } catch ( IllegalArgumentException e ) {
            System.err.println( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " does not take" + " a simple PropertyChangeListener argument" + " so Drools will be unable to process JavaBean"
                                + " PropertyChangeEvents on the asserted Object" );
        } catch ( IllegalAccessException e ) {
            System.err.println( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " is not public" + " so Drools will be unable to process JavaBean" + " PropertyChangeEvents on the asserted Object" );
        } catch ( InvocationTargetException e ) {
            System.err.println( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " threw an InvocationTargetException" + " so Drools will be unable to process JavaBean"
                                + " PropertyChangeEvents on the asserted Object: " + e.getMessage() );
        } catch ( SecurityException e ) {
            System.err.println( "Warning: The SecurityManager controlling the class " + object.getClass() + " did not allow the lookup of a" + " addPropertyChangeListener method" + " so Drools will be unable to process JavaBean"
                                + " PropertyChangeEvents on the asserted Object: " + e.getMessage() );
        }
    }

    private void removePropertyChangeListener(FactHandle handle) throws NoSuchFactObjectException {
        Object object = null;
        try {
            object = getObject( handle );

            Method mehod = handle.getClass().getMethod( "removePropertyChangeListener",
                                                        WorkingMemoryImpl.ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES );

            mehod.invoke( handle,
                          this.addRemovePropertyChangeListenerArgs );
        } catch ( NoSuchMethodException e ) {
            // The removePropertyChangeListener method on the class
            // was not found so Drools will be unable to
            // stop processing JavaBean PropertyChangeEvents
            // on the retracted Object
        } catch ( IllegalArgumentException e ) {
            System.err.println( "Warning: The removePropertyChangeListener method" + " on the class " + object.getClass() + " does not take" + " a simple PropertyChangeListener argument" + " so Drools will be unable to stop processing JavaBean"
                                + " PropertyChangeEvents on the retracted Object" );
        } catch ( IllegalAccessException e ) {
            System.err.println( "Warning: The removePropertyChangeListener method" + " on the class " + object.getClass() + " is not public" + " so Drools will be unable to stop processing JavaBean" + " PropertyChangeEvents on the retracted Object" );
        } catch ( InvocationTargetException e ) {
            System.err.println( "Warning: The removePropertyChangeL istener method" + " on the class " + object.getClass() + " threw an InvocationTargetException" + " so Drools will be unable to stop processing JavaBean"
                                + " PropertyChangeEvents on the retracted Object: " + e.getMessage() );
        } catch ( SecurityException e ) {
            System.err.println( "Warning: The SecurityManager controlling the class " + object.getClass() + " did not allow the lookup of a" + " removePropertyChangeListener method" + " so Drools will be unable to stop processing JavaBean"
                                + " PropertyChangeEvents on the retracted Object: " + e.getMessage() );
        }
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
        //        Object oldValue = this.objects.put( ((FactHandleImpl) handle).getId(),
        //                                            object );

        this.identityMap.put( object,
                              handle );

        ((FactHandleImpl) handle).setObject( object );
        //return oldValue;
    }

    Object removeObject(FactHandle handle) {
        //Object object = this.objects.remove( ((FactHandleImpl) handle).getId() );

        Object object = getObject( handle );
        this.identityMap.remove( object );

        return object;
    }

    public void retractObject(FactHandle handle) throws FactException {
        retractObject( handle,
                       true,
                       true,
                       null,
                       null );
    }

    /**
     * @see WorkingMemory
     */
    public void retractObject(FactHandle handle,
                              boolean removeLogical,
                              boolean updateEqualsMap,
                              Rule rule,
                              Activation activation) throws FactException {
        this.lock.lock();
        try {
            removePropertyChangeListener( handle );

            PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                PropagationContext.RETRACTION,
                                                                                rule,
                                                                                activation );

            this.ruleBase.retractObject( handle,
                                         propagationContext,
                                         this );

            Object oldObject = removeObject( handle );

            /* check to see if this was a logical asserted object */
            if ( removeLogical ) {
                removeLogicalDependencies( handle );
                //this.equalsMap.remove( oldObject );
            }

            if ( removeLogical || updateEqualsMap ) {
                FactStatus status = (FactStatus) this.equalsMap.get( oldObject );
                if ( status != null ) {
                    status.decCounter();
                    if ( status.getCounter() <= 0 ) {
                        this.equalsMap.remove( oldObject );
                    }
                }
            }

            this.factHandlePool.push( ((FactHandleImpl) handle).getId() );

            this.workingMemoryEventSupport.fireObjectRetracted( propagationContext,
                                                                handle,
                                                                oldObject );

            ((FactHandleImpl) handle).invalidate();
            
            if ( !this.factQueue.isEmpty() ) {
                propagateQueuedActions();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void modifyObject(FactHandle handle,
                             Object object) throws FactException {
        modifyObject( handle,
                      object,
                      null,
                      null );
    }

    /**
     * @see WorkingMemory
     */
    public void modifyObject(FactHandle handle,
                             Object object,
                             Rule rule,
                             Activation activation) throws FactException {
        this.lock.lock();
        try {
            Object originalObject = removeObject( handle );

            if ( originalObject == null ) {
                throw new NoSuchFactObjectException( handle );
            }

            this.handleFactory.increaseFactHandleRecency( handle );

            putObject( handle,
                       object );

            /* check to see if this is a logically asserted object */
            FactHandleImpl handleImpl = (FactHandleImpl) handle;
            if ( this.justified.get( handleImpl.getId() ) != null ) {
                this.equalsMap.remove( originalObject );
                this.equalsMap.put( object,
                                    new FactStatus( WorkingMemoryImpl.JUSTIFIED,
                                                    handleImpl ) );
            }

            PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                PropagationContext.MODIFICATION,
                                                                                rule,
                                                                                activation );

            this.ruleBase.modifyObject( handle,
                                        propagationContext,
                                        this );
            // this.ruleBase.retractObject( handle,
            // propagationContext,
            // this );
            //
            // this.ruleBase.assertObject( handle,
            // object,
            // propagationContext,
            // this );

            /*
             * this.ruleBase.modifyObject( handle, object, this );
             */
            this.workingMemoryEventSupport.fireObjectModified( propagationContext,
                                                               handle,
                                                               originalObject,
                                                               object );
            
            if ( !this.factQueue.isEmpty() ) {
                propagateQueuedActions();
            }
        } finally {
            this.lock.unlock();
        }
    }
    
    void propagateQueuedActions() {
        for ( Iterator it = this.factQueue.iterator(); it.hasNext(); ) {
            WorkingMemoryAction action = (WorkingMemoryAction) it.next();
            it.remove();
            action.propagate();
        }
        
        
    }

    /**
     * Retrieve the <code>JoinMemory</code> for a particular
     * <code>JoinNode</code>.
     * 
     * @param node
     *            The <code>JoinNode</code> key.
     * 
     * @return The node's memory.
     */
    public Object getNodeMemory(NodeMemory node) {
        Object memory = this.nodeMemories.get( node.getId() );

        if ( memory == null ) {
            memory = node.createMemory( this.ruleBase.getConfiguration() );

            this.nodeMemories.put( node.getId(),
                                   memory );
        }

        return memory;
    }

    public void clearNodeMemory(NodeMemory node) {
        this.nodeMemories.remove( node.getId() );
    }

    public WorkingMemoryEventSupport getWorkingMemoryEventSupport() {
        return this.workingMemoryEventSupport;
    }

    public AgendaEventSupport getAgendaEventSupport() {
        return this.agendaEventSupport;
    }

    /**
     * Sets the AsyncExceptionHandler to handle exceptions thrown by the Agenda
     * Scheduler used for duration rules.
     * 
     * @param handler
     */
    public void setAsyncExceptionHandler(AsyncExceptionHandler handler) {
        // this.agenda.setAsyncExceptionHandler( handler );
    }

    /*
     * public void dumpMemory() { Iterator it = this.joinMemories.keySet(
     * ).iterator( ); while ( it.hasNext( ) ) { ((JoinMemory)
     * this.joinMemories.get( it.next( ) )).dump( ); } }
     */

    public void propertyChange(PropertyChangeEvent event) {
        Object object = event.getSource();

        try {
            modifyObject( getFactHandle( object ),
                          object );
        } catch ( NoSuchFactHandleException e ) {
            // Not a fact so unable to process the chnage event
        } catch ( FactException e ) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    public void removeLogicalDependencies(Activation activation,
                                          PropagationContext context,
                                          Rule rule) throws FactException {
        org.drools.util.LinkedList list = activation.getLogicalDependencies();
        if ( list == null || list.isEmpty() ) {
            return;
        }
        for ( LogicalDependency node = (LogicalDependency) list.getFirst(); node != null; node = (LogicalDependency) node.getNext() ) {
            FactHandleImpl handle = (FactHandleImpl) node.getFactHandle();
            Set set = (Set) this.justified.get( handle.getId() );
            set.remove( node );
            if ( set.isEmpty() ) {
                this.justified.remove( handle.getId() );
                // this needs to be scheduled so we don't upset the current working memory operation
                this.factQueue.add( new WorkingMemoryRetractAction( handle,
                                                                    false,
                                                                    true,
                                                                    context.getRuleOrigin(),
                                                                    context.getActivationOrigin() ) );
            }
        }
    }

    public void removeLogicalDependencies(FactHandle handle) throws FactException {
        Set set = (Set) this.justified.remove( ((FactHandleImpl) handle).getId() );
        if ( set != null && !set.isEmpty() ) {
            for ( Iterator it = set.iterator(); it.hasNext(); ) {
                LogicalDependency node = (LogicalDependency) it.next();
                node.getJustifier().getLogicalDependencies().remove( node );
            }
        }
    }

    public void addLogicalDependency(FactHandle handle,
                                     Activation activation,
                                     PropagationContext context,
                                     Rule rule) throws FactException {
        LogicalDependency node = new LogicalDependency( activation,
                                                        handle );
        activation.addLogicalDependency( node );
        Set set = (Set) this.justified.get( ((FactHandleImpl) handle).getId() );
        if ( set == null ) {
            set = new HashSet();
            this.justified.put( ((FactHandleImpl) handle).getId(),
                                set );
        }
        set.add( node );
    }

    public PrimitiveLongMap getJustified() {
        return this.justified;
    }

    public long getNextPropagationIdCounter() {
        return this.propagationIdCounter++;
    }

    public void dispose() {
        this.ruleBase.disposeWorkingMemory( this );
    }

    public Lock getLock() {
        return this.lock;
    }
    
    private interface WorkingMemoryAction {
        public void propagate();
    }
    
    private class WorkingMemoryRetractAction implements WorkingMemoryAction {
        private InternalFactHandle factHandle;
        private boolean removeLogical;
        private boolean updateEqualsMap;
        private Rule ruleOrigin;
        private Activation activationOrigin;
        
        
        
        public WorkingMemoryRetractAction(InternalFactHandle factHandle,
                                          boolean removeLogical,
                                          boolean updateEqualsMap,
                                          Rule ruleOrigin,
                                          Activation activationOrigin) {
            super();
            this.factHandle = factHandle;
            this.removeLogical = removeLogical;
            this.updateEqualsMap = updateEqualsMap;
            this.ruleOrigin = ruleOrigin;
            this.activationOrigin = activationOrigin;
        }

        public void propagate() {
            retractObject( this.factHandle,
                           this.removeLogical,
                           this.updateEqualsMap,
                           this.ruleOrigin,
                           this.activationOrigin );
        }
    }

    private static class FactStatus {
        private int            counter;
        private String         status;
        private FactHandleImpl handle;

        public FactStatus() {
            this( WorkingMemoryImpl.STATED,
                  1 );
        }

        public FactStatus(String status) {
            this( status,
                  1 );
        }

        public FactStatus(String status,
                          FactHandleImpl handle) {
            this.status = status;
            this.handle = handle;
        }

        public FactStatus(String status,
                          int counter) {
            this.status = status;
            this.counter = counter;
        }

        /**
         * @return the counter
         */
        public int getCounter() {
            return counter;
        }

        /**
         * @param counter the counter to set
         */
        public void setCounter(int counter) {
            this.counter = counter;
        }

        public int incCounter() {
            return ++counter;
        }

        public int decCounter() {
            return --counter;
        }

        /**
         * @return the handle
         */
        public FactHandleImpl getHandle() {
            return handle;
        }

        /**
         * @param handle the handle to set
         */
        public void setHandle(FactHandleImpl handle) {
            this.handle = handle;
        }

        /**
         * @return the status
         */
        public String getStatus() {
            return status;
        }

        /**
         * @param status the status to set
         */
        public void setStatus(String status) {
            this.status = status;
        }

        public String toString() {
            return "FactStatus( " + this.status + ", handle=" + this.handle + ", counter=" + this.counter + ")";
        }

    }
}
