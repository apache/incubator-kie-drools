package org.drools.common;

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
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.Agenda;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.NoSuchFactHandleException;
import org.drools.NoSuchFactObjectException;
import org.drools.ObjectFilter;
import org.drools.Otherwise;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.WorkingMemory;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.RuleBaseConfiguration.LogicalOverride;
import org.drools.base.ShadowProxy;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.rule.Rule;
import org.drools.ruleflow.common.core.IProcess;
import org.drools.ruleflow.common.instance.IProcessInstance;
import org.drools.ruleflow.core.IRuleFlowProcess;
import org.drools.ruleflow.instance.IRuleFlowProcessInstance;
import org.drools.ruleflow.instance.impl.RuleFlowProcessInstance;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.GlobalResolver;
import org.drools.spi.PropagationContext;
import org.drools.util.JavaIteratorAdapter;
import org.drools.util.ObjectHashMap;
import org.drools.util.PrimitiveLongMap;
import org.drools.util.AbstractHashTable.HashTableIterator;
import org.drools.util.concurrent.locks.Lock;
import org.drools.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of <code>WorkingMemory</code>.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 */
public abstract class AbstractWorkingMemory
    implements
    InternalWorkingMemoryActions,
    EventSupport,
    PropertyChangeListener {
    // ------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------
    protected static final Class[]            ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[]{PropertyChangeListener.class};

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------
    protected final long                      id;

    /** The arguments used when adding/removing a property change listener. */
    protected final Object[]                  addRemovePropertyChangeListenerArgs           = new Object[]{this};

    /** The actual memory for the <code>JoinNode</code>s. */
    protected final PrimitiveLongMap          nodeMemories                                  = new PrimitiveLongMap( 32,
                                                                                                                    8 );

    /** Global values which are associated with this memory. */
    protected final Map                       globals                                       = new HashMap();

    /** Object-to-handle mapping. */
    private final ObjectHashMap               assertMap;

    protected Map                             queryResults                                  = Collections.EMPTY_MAP;

    protected GlobalResolver                  globalResolver;

    protected static final Object             NULL                                          = new Serializable() {
                                                                                                private static final long serialVersionUID = 320L;
                                                                                            };

    /** The eventSupport */
    protected final WorkingMemoryEventSupport workingMemoryEventSupport                     = new WorkingMemoryEventSupport( this );

    protected final AgendaEventSupport        agendaEventSupport                            = new AgendaEventSupport( this );

    /** The <code>RuleBase</code> with which this memory is associated. */
    protected transient InternalRuleBase      ruleBase;

    protected final FactHandleFactory         handleFactory;

    protected final TruthMaintenanceSystem    tms;

    /** Rule-firing agenda. */
    protected DefaultAgenda                   agenda;

    protected final List                      actionQueue                                     = new ArrayList();

    protected final ReentrantLock             lock                                          = new ReentrantLock();

    protected final boolean                   discardOnLogicalOverride;

    protected long                            propagationIdCounter;

    private final boolean                     maintainTms;

    /** Flag to determine if a rule is currently being fired. */
    protected boolean                         firing;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param ruleBase
     *            The backing rule-base.
     */
    public AbstractWorkingMemory(final int id,
                                 final InternalRuleBase ruleBase,
                                 final FactHandleFactory handleFactory) {
        this.id = id;
        this.ruleBase = ruleBase;
        this.handleFactory = handleFactory;
        this.maintainTms = this.ruleBase.getConfiguration().getMaintainTms();

        if ( this.maintainTms ) {
            this.tms = new TruthMaintenanceSystem( this );
        } else {
            this.tms = null;
        }

        this.assertMap = new ObjectHashMap();
        final RuleBaseConfiguration conf = this.ruleBase.getConfiguration();

        if ( conf.getAssertBehaviour() == AssertBehaviour.IDENTITY ) {
            this.assertMap.setComparator( new IdentityAssertMapComparator() );
        } else {
            this.assertMap.setComparator( new EqualityAssertMapComparator() );
        }

        // Only takes effect if are using idententity behaviour for assert        
        if ( conf.getLogicalOverride() == LogicalOverride.DISCARD ) {
            this.discardOnLogicalOverride = true;
        } else {
            this.discardOnLogicalOverride = false;
        }

    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    void setRuleBase(final InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    public void addEventListener(final WorkingMemoryEventListener listener) {
        try {
            this.lock.lock();
            this.workingMemoryEventSupport.addEventListener( listener );
        } finally {
            this.lock.unlock();
        }
    }

    public void removeEventListener(final WorkingMemoryEventListener listener) {
        try {
            this.lock.lock();
            this.workingMemoryEventSupport.removeEventListener( listener );
        } finally {
            this.lock.unlock();
        }
    }

    public List getWorkingMemoryEventListeners() {
        try {
            this.lock.lock();
            return this.workingMemoryEventSupport.getEventListeners();
        } finally {
            this.lock.unlock();
        }
    }

    public void addEventListener(final AgendaEventListener listener) {
        try {
            this.lock.lock();
            this.agendaEventSupport.addEventListener( listener );
        } finally {
            this.lock.unlock();
        }
    }

    public void removeEventListener(final AgendaEventListener listener) {
        try {
            this.lock.lock();
            this.agendaEventSupport.removeEventListener( listener );
        } finally {
            this.lock.unlock();
        }
    }

    public List getAgendaEventListeners() {
        try {
            this.lock.lock();
            return this.agendaEventSupport.getEventListeners();
        } finally {
            this.lock.unlock();
        }
    }

    public FactHandleFactory getFactHandleFactory() {
        return this.handleFactory;
    }

    /**
     * @see WorkingMemory
     */
    public Map getGlobals() {
        try {
            this.lock.lock();
            return this.globals;
        } finally {
            this.lock.unlock();
        }
    }

    /**
     * @see WorkingMemory
     */
    public void setGlobal(final String name,
                          final Object value) {
        // Cannot set null values
        if ( value == null ) {
            return;
        }

        try {
            this.lock.lock();
            // Make sure the global has been declared in the RuleBase
            final Map globalDefintions = this.ruleBase.getGlobals();
            final Class type = (Class) globalDefintions.get( name );
            if ( (type == null) ) {
                throw new RuntimeException( "Unexpected global [" + name + "]" );
            } else if ( !type.isInstance( value ) ) {
                throw new RuntimeException( "Illegal class for global. " + "Expected [" + type.getName() + "], " + "found [" + value.getClass().getName() + "]." );

            } else {
                this.globals.put( name,
                                  value );
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void setGlobalResolver(final GlobalResolver globalResolver) {
        try {
            this.lock.lock();        
            this.globalResolver = globalResolver;
        } finally {
            this.lock.unlock();
        }            
    }

    public long getId() {
        return this.id;
    }

    /**
     * @see WorkingMemory
     */
    public Object getGlobal(final String name) {
        try {
            this.lock.lock();
            Object object = this.globals.get( name );
            if ( object == null && this.globalResolver != null ) {
                object = this.globalResolver.resolve( name );
            }
            return object;
        } finally {
            this.lock.unlock();
        }
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
    public void clearAgendaGroup(final String group) {
        this.agenda.clearAgendaGroup( group );
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
    public synchronized void fireAllRules() throws FactException {
        fireAllRules( null );
    }

    public synchronized void fireAllRules(final AgendaFilter agendaFilter) throws FactException {
        // If we're already firing a rule, then it'll pick up
        // the firing for any other assertObject(..) that get
        // nested inside, avoiding concurrent-modification
        // exceptions, depending on code paths of the actions.

        if ( !this.actionQueue.isEmpty() ) {
            executeQueuedActions();
        }

        boolean noneFired = true;

        if ( !this.firing ) {
            try {
                this.firing = true;

                while ( this.agenda.fireNextItem( agendaFilter ) ) {
                    noneFired = false;
                    if ( !this.actionQueue.isEmpty() ) {
                        executeQueuedActions();
                    }
                }
            } finally {
                this.firing = false;
                if ( noneFired ) {
                    doOtherwise( agendaFilter );
                }

            }
        }
    }    

    /**
     * This does the "otherwise" phase of processing.
     * If no items are fired, then it will assert a temporary "Otherwise"
     * fact and allow any rules to fire to handle "otherwise" cases.
     */
    private void doOtherwise(final AgendaFilter agendaFilter) {
        final FactHandle handle = this.assertObject( new Otherwise() );
        if ( !this.actionQueue.isEmpty() ) {
            executeQueuedActions();
        }

        while ( this.agenda.fireNextItem( agendaFilter ) ) {
            ;
        }

        this.retractObject( handle );
    }

    //
    //        MN: The following is the traditional fireAllRules (without otherwise).
    //            Purely kept here as this implementation of otherwise is still experimental.    
    //    
    //    public synchronized void fireAllRules(final AgendaFilter agendaFilter) throws FactException {
    //        // If we're already firing a rule, then it'll pick up
    //        // the firing for any other assertObject(..) that get
    //        // nested inside, avoiding concurrent-modification
    //        // exceptions, depending on code paths of the actions.
    //
    //        if ( !this.factQueue.isEmpty() ) {
    //            propagateQueuedActions();
    //        }
    //
    //        if ( !this.firing ) {
    //            try {
    //                this.firing = true;
    //
    //                while ( this.agenda.fireNextItem( agendaFilter ) ) {
    //                    ;
    //                }
    //            } finally {
    //                this.firing = false;
    //            }
    //        }
    //    }    

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
    public Object getObject(final FactHandle handle) {
        try {
            this.lock.lock();

            // Make sure the FactHandle is from this WorkingMemory
            final InternalFactHandle internalHandle = (InternalFactHandle) this.assertMap.get( handle );
            if ( internalHandle == null ) {
                return null;
            }

            Object object = internalHandle.getObject();

            if ( object != null && internalHandle.isShadowFact() ) {
                object = ((ShadowProxy) object).getShadowedObject();
            }

            return object;
        } finally {
            this.lock.unlock();
        }

    }

    /**
     * @see WorkingMemory
     */
    public FactHandle getFactHandle(final Object object) {
        try {
            this.lock.lock();
            final FactHandle factHandle = (FactHandle) this.assertMap.get( object );

            return factHandle;
        } finally {
            this.lock.unlock();
        }
    }
    
    /** 
     * This is an internal method, used to avoid java.util.Iterator adaptors
     */
    public ObjectHashMap getFactHandleMap() {
        return this.assertMap;
    }

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public Iterator iterateObjects() {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator, JavaIteratorAdapter.OBJECT );
    }
    
    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */  
    public Iterator iterateObjects(ObjectFilter filter) {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator, JavaIteratorAdapter.OBJECT, filter );
    }

    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */
    public Iterator iterateFactHandles() {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator, JavaIteratorAdapter.FACT_HANDLE );
    }
    
    /**
     * This class is not thread safe, changes to the working memory during iteration may give unexpected results
     */  
    public Iterator iterateFactHandles(ObjectFilter filter) {
        HashTableIterator iterator = new HashTableIterator( this.assertMap );
        iterator.reset();
        return new JavaIteratorAdapter( iterator, JavaIteratorAdapter.FACT_HANDLE, filter );
    }       

    public abstract QueryResults getQueryResults(String query);

    public AgendaGroup getFocus() {
        return this.agenda.getFocus();
    }

    public void setFocus(final String focus) {
        this.agenda.setFocus( focus );
    }

    public void setFocus(final AgendaGroup focus) {
        this.agenda.setFocus( focus );
    }

    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return this.tms;
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle assertObject(final Object object) throws FactException {
        return assertObject( object, /* Not-Dynamic */
                             false,
                             false,
                             null,
                             null );
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle assertLogicalObject(final Object object) throws FactException {
        return assertObject( object, /* Not-Dynamic */
                             false,
                             true,
                             null,
                             null );
    }

    public FactHandle assertObject(final Object object,
                                   final boolean dynamic) throws FactException {
        return assertObject( object,
                             dynamic,
                             false,
                             null,
                             null );
    }

    public FactHandle assertLogicalObject(final Object object,
                                          final boolean dynamic) throws FactException {
        return assertObject( object,
                             dynamic,
                             true,
                             null,
                             null );
    }

    public FactHandle assertObject(final Object object,
                                   final boolean dynamic,
                                   boolean logical,
                                   final Rule rule,
                                   final Activation activation) throws FactException {
        if ( object == null ) {
            // you cannot assert a null object
            return null;
        }
        InternalFactHandle handle = null;
        try {
            this.lock.lock();
            // check if the object already exists in the WM
            handle = (InternalFactHandle) this.assertMap.get( object );

            if ( this.maintainTms ) {

                EqualityKey key = null;

                if ( handle == null ) {
                    // lets see if the object is already logical asserted
                    key = this.tms.get( object );
                } else {
                    // Object is already asserted, so check and possibly correct its
                    // status and then return the handle
                    key = handle.getEqualityKey();

                    if ( key.getStatus() == EqualityKey.STATED ) {
                        // return null as you cannot justify a stated object.
                        return handle;
                    }

                    if ( !logical ) {
                        // this object was previously justified, so we have to
                        // override it to stated
                        key.setStatus( EqualityKey.STATED );
                        this.tms.removeLogicalDependencies( handle );
                    } else {
                        // this was object is already justified, so just add new
                        // logical dependency
                        this.tms.addLogicalDependency( handle,
                                                       activation,
                                                       activation.getPropagationContext(),
                                                       rule );
                    }

                    return handle;
                }

                // At this point we know the handle is null
                if ( key == null ) {
                    // key is also null, so treat as a totally new stated/logical
                    // assert
                    handle = this.handleFactory.newFactHandle( object );
                    this.assertMap.put( handle,
                                        handle,
                                        false );
                    key = new EqualityKey( handle );
                    handle.setEqualityKey( key );
                    this.tms.put( key );
                    if ( !logical ) {
                        key.setStatus( EqualityKey.STATED );
                    } else {
                        key.setStatus( EqualityKey.JUSTIFIED );
                        this.tms.addLogicalDependency( handle,
                                                       activation,
                                                       activation.getPropagationContext(),
                                                       rule );
                    }
                } else if ( !logical ) {
                    if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                        // Its previous justified, so switch to stated and remove
                        // logical dependencies
                        final InternalFactHandle justifiedHandle = key.getFactHandle();
                        this.tms.removeLogicalDependencies( justifiedHandle );

                        if ( this.discardOnLogicalOverride ) {
                            // override, setting to new instance, and return
                            // existing handle
                            key.setStatus( EqualityKey.STATED );
                            handle = key.getFactHandle();
                            
                            if ( this.ruleBase.getConfiguration().getAssertBehaviour() == AssertBehaviour.IDENTITY ) {
                                // as assertMap may be using an "identity" equality comparator,
                                // we need to remove the handle from the map, before replacing the object
                                // and then re-add the handle. Otherwise we may end up with a leak.
                                this.assertMap.remove( handle );
                                handle.setObject( object );
                                this.assertMap.put( handle, 
                                                    handle, 
                                                    false );
                            } else {
                                handle.setObject( object );
                            }
                            return handle;
                        } else {
                            // override, then instantiate new handle for assertion
                            key.setStatus( EqualityKey.STATED );
                            handle = this.handleFactory.newFactHandle( object );
                            handle.setEqualityKey( key );
                            key.addFactHandle( handle );
                            this.assertMap.put( handle,
                                                handle,
                                                false );
                        }

                    } else {
                        handle = this.handleFactory.newFactHandle( object );
                        this.assertMap.put( handle,
                                            handle,
                                            false );
                        key.addFactHandle( handle );
                        handle.setEqualityKey( key );
                    }

                } else {
                    if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                        // only add as logical dependency if this wasn't previously
                        // stated
                        this.tms.addLogicalDependency( key.getFactHandle(),
                                                       activation,
                                                       activation.getPropagationContext(),
                                                       rule );
                        return key.getFactHandle();
                    } else {
                        // You cannot justify a previously stated equality equal
                        // object, so return null
                        return null;
                    }
                }

            } else {
                if ( handle != null ) {
                    return handle;
                }
                handle = this.handleFactory.newFactHandle( object );
                this.assertMap.put( handle,
                                    handle,
                                    false );
            }

            if ( dynamic ) {
                addPropertyChangeListener( object );
            }

            final PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                      PropagationContext.ASSERTION,
                                                                                      rule,
                                                                                      activation,
                                                                                      this.agenda.getActiveActivations(),
                                                                                      this.agenda.getDormantActivations() );

            doAssertObject( handle,
                            object,
                            propagationContext );

            this.workingMemoryEventSupport.fireObjectAsserted( propagationContext,
                                                               handle,
                                                               object );

            if ( !this.actionQueue.isEmpty() ) {
                executeQueuedActions();
            }
        } finally {
            this.lock.unlock();
        }
        return handle;
    }

    protected void addPropertyChangeListener(final Object object) {
        try {
            final Method method = object.getClass().getMethod( "addPropertyChangeListener",
                                                               AbstractWorkingMemory.ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES );

            method.invoke( object,
                           this.addRemovePropertyChangeListenerArgs );
        } catch ( final NoSuchMethodException e ) {
            System.err.println( "Warning: Method addPropertyChangeListener not found" + " on the class " + object.getClass() + " so Drools will be unable to process JavaBean" + " PropertyChangeEvents on the asserted Object" );
        } catch ( final IllegalArgumentException e ) {
            System.err.println( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " does not take" + " a simple PropertyChangeListener argument" + " so Drools will be unable to process JavaBean"
                                + " PropertyChangeEvents on the asserted Object" );
        } catch ( final IllegalAccessException e ) {
            System.err.println( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " is not public" + " so Drools will be unable to process JavaBean" + " PropertyChangeEvents on the asserted Object" );
        } catch ( final InvocationTargetException e ) {
            System.err.println( "Warning: The addPropertyChangeListener method" + " on the class " + object.getClass() + " threw an InvocationTargetException" + " so Drools will be unable to process JavaBean"
                                + " PropertyChangeEvents on the asserted Object: " + e.getMessage() );
        } catch ( final SecurityException e ) {
            System.err.println( "Warning: The SecurityManager controlling the class " + object.getClass() + " did not allow the lookup of a" + " addPropertyChangeListener method" + " so Drools will be unable to process JavaBean"
                                + " PropertyChangeEvents on the asserted Object: " + e.getMessage() );
        }
    }

    public abstract void doAssertObject(InternalFactHandle factHandle,
                                        Object object,
                                        PropagationContext propagationContext) throws FactException;

    protected void removePropertyChangeListener(final FactHandle handle) throws NoSuchFactObjectException {
        Object object = null;
        try {
            object = getObject( handle );

            if ( object != null ) {
                final Method mehod = object.getClass().getMethod( "removePropertyChangeListener",
                                                                  AbstractWorkingMemory.ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES );

                mehod.invoke( object,
                              this.addRemovePropertyChangeListenerArgs );
            }
        } catch ( final NoSuchMethodException e ) {
            // The removePropertyChangeListener method on the class
            // was not found so Drools will be unable to
            // stop processing JavaBean PropertyChangeEvents
            // on the retracted Object
        } catch ( final IllegalArgumentException e ) {
            System.err.println( "Warning: The removePropertyChangeListener method" + " on the class " + object.getClass() + " does not take" + " a simple PropertyChangeListener argument" + " so Drools will be unable to stop processing JavaBean"
                                + " PropertyChangeEvents on the retracted Object" );
        } catch ( final IllegalAccessException e ) {
            System.err.println( "Warning: The removePropertyChangeListener method" + " on the class " + object.getClass() + " is not public" + " so Drools will be unable to stop processing JavaBean" + " PropertyChangeEvents on the retracted Object" );
        } catch ( final InvocationTargetException e ) {
            System.err.println( "Warning: The removePropertyChangeL istener method" + " on the class " + object.getClass() + " threw an InvocationTargetException" + " so Drools will be unable to stop processing JavaBean"
                                + " PropertyChangeEvents on the retracted Object: " + e.getMessage() );
        } catch ( final SecurityException e ) {
            System.err.println( "Warning: The SecurityManager controlling the class " + object.getClass() + " did not allow the lookup of a" + " removePropertyChangeListener method" + " so Drools will be unable to stop processing JavaBean"
                                + " PropertyChangeEvents on the retracted Object: " + e.getMessage() );
        }
    }


    public void retractObject(final FactHandle handle) throws FactException {
        retractObject( handle,
                       true,
                       true,
                       null,
                       null );
    }

    public abstract void doRetract(InternalFactHandle factHandle,
                                   PropagationContext propagationContext);

    /**
     * @see WorkingMemory
     */
    public void retractObject(final FactHandle factHandle,
                              final boolean removeLogical,
                              final boolean updateEqualsMap,
                              final Rule rule,
                              final Activation activation) throws FactException {
        try {
            this.lock.lock();
            final InternalFactHandle handle = (InternalFactHandle) factHandle;
            if ( handle.getId() == -1 ) {
                // can't retract an already retracted handle
                return;
            }
            removePropertyChangeListener( handle );

            final PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                      PropagationContext.RETRACTION,
                                                                                      rule,
                                                                                      activation,
                                                                                      this.agenda.getActiveActivations(),
                                                                                      this.agenda.getDormantActivations() );

            doRetract( handle,
                       propagationContext );

            if ( this.maintainTms ) {
                // Update the equality key, which maintains a list of stated
                // FactHandles
                final EqualityKey key = handle.getEqualityKey();

                // Its justified so attempt to remove any logical dependencies for
                // the handle
                if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                    this.tms.removeLogicalDependencies( handle );
                }

                key.removeFactHandle( handle );
                handle.setEqualityKey( null );

                // If the equality key is now empty, then remove it
                if ( key.isEmpty() ) {
                    this.tms.remove( key );
                }
            }

            final Object object = handle.getObject();

            this.workingMemoryEventSupport.fireObjectRetracted( propagationContext,
                                                                handle,
                                                                object );

            this.assertMap.remove( handle );

            this.handleFactory.destroyFactHandle( handle );

            if ( !this.actionQueue.isEmpty() ) {
                executeQueuedActions();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void modifyObject(final FactHandle handle,
                             final Object object) throws FactException {
        modifyObject( handle,
                      object,
                      null,
                      null );
    }

    /**
     * modify is implemented as half way retract / assert due to the truth
     * maintenance issues.
     * 
     * @see WorkingMemory
     */
    public void modifyObject(final FactHandle factHandle,
                             final Object object,
                             final Rule rule,
                             final Activation activation) throws FactException {
        try {
            this.lock.lock();
            // only needed if we maintain tms, but either way we must get it before we do the retract
            int status = -1;
            if ( this.maintainTms ) {
                status = ((InternalFactHandle) factHandle).getEqualityKey().getStatus();
            }
            final InternalFactHandle handle = (InternalFactHandle) factHandle;
            final Object originalObject = (handle.isShadowFact()) ? ((ShadowProxy) handle.getObject()).getShadowedObject() : handle.getObject();

            if ( handle.getId() == -1 || object == null ) {
                // the handle is invalid, most likely already  retracted, so return
                // and we cannot assert a null object
                return;
            }

            // Nowretract any trace  of the original fact
            final PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                      PropagationContext.MODIFICATION,
                                                                                      rule,
                                                                                      activation,
                                                                                      this.agenda.getActiveActivations(),
                                                                                      this.agenda.getDormantActivations() );
            doRetract( handle,
                       propagationContext );

            // set anyway, so that it updates the hashCodes
            handle.setObject( object );

            if ( this.maintainTms ) {
                // We only need to put objects, if its a new object
                if ( originalObject != object ) {
                    this.assertMap.put( handle,
                                        handle );
                }

                // the hashCode and equality has changed, so we must update the EqualityKey
                EqualityKey key = handle.getEqualityKey();
                key.removeFactHandle( handle );

                // If the equality key is now empty, then remove it
                if ( key.isEmpty() ) {
                    this.tms.remove( key );
                }

                // now use an  existing  EqualityKey, if it exists, else create a new one
                key = this.tms.get( object );
                if ( key == null ) {
                    key = new EqualityKey( handle,
                                           status );
                    this.tms.put( key );
                } else {
                    key.addFactHandle( handle );
                }

                handle.setEqualityKey( key );
            }

            this.handleFactory.increaseFactHandleRecency( handle );

            doAssertObject( handle,
                            object,
                            propagationContext );

            this.workingMemoryEventSupport.fireObjectModified( propagationContext,
                                                               factHandle,
                                                               originalObject,
                                                               object );

            propagationContext.clearRetractedTuples();

            if ( !this.actionQueue.isEmpty() ) {
                executeQueuedActions();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void executeQueuedActions() {
        for ( final Iterator it = this.actionQueue.iterator(); it.hasNext(); ) {
            final WorkingMemoryAction action = (WorkingMemoryAction) it.next();
            it.remove();
            action.execute( this );
        }

    }

    public void queueWorkingMemoryAction(final WorkingMemoryAction action) {
        this.actionQueue.add( action );
    }

    public void removeLogicalDependencies(final Activation activation,
                                          final PropagationContext context,
                                          final Rule rule) throws FactException {
        if ( this.maintainTms ) {
            this.tms.removeLogicalDependencies( activation,
                                                context,
                                                rule );
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
    public Object getNodeMemory(final NodeMemory node) {
        Object memory = this.nodeMemories.get( node.getId() );

        if ( memory == null ) {
            memory = node.createMemory( this.ruleBase.getConfiguration() );

            this.nodeMemories.put( node.getId(),
                                   memory );
        }

        return memory;
    }

    public void clearNodeMemory(final NodeMemory node) {
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
    public void setAsyncExceptionHandler(final AsyncExceptionHandler handler) {
        // this.agenda.setAsyncExceptionHandler( handler );
    }

    /*
     * public void dumpMemory() { Iterator it = this.joinMemories.keySet(
     * ).iterator( ); while ( it.hasNext( ) ) { ((JoinMemory)
     * this.joinMemories.get( it.next( ) )).dump( ); } }
     */

    public void propertyChange(final PropertyChangeEvent event) {
        final Object object = event.getSource();

        try {
            modifyObject( getFactHandle( object ),
                          object );
        } catch ( final NoSuchFactHandleException e ) {
            // Not a fact so unable to process the chnage event
        } catch ( final FactException e ) {
            throw new RuntimeException( e.getMessage() );
        }
    }

    public long getNextPropagationIdCounter() {
        return this.propagationIdCounter++;
    }

    public Lock getLock() {
        return this.lock;
    }

    public class RuleFlowDeactivateEvent {

        public void propagate() {

        }
    }

    public IProcessInstance startProcess(final String processId) {
        final IProcess process = getRuleBase().getProcess( processId );
        if ( process == null ) {
            throw new IllegalArgumentException( "Unknown process ID: " + processId );
        }
        if ( process instanceof IRuleFlowProcess ) {
            final IRuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
            processInstance.setAgenda( this.agenda );
            processInstance.setProcess( process );
            processInstance.start();
            return processInstance;
        } else {
            throw new IllegalArgumentException( "Unknown process type: " + process.getClass() );
        }
    }
    
    public List iterateObjectsToList() {
    	List result = new ArrayList();
    	Iterator iterator = iterateObjects();
    	for ( ; iterator.hasNext(); ) {
    		result.add(iterator.next());
    	}
    	return result;
    }

}
