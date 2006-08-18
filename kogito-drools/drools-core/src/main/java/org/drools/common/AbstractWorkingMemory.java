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
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.WorkingMemory;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.GlobalResolver;
import org.drools.spi.PropagationContext;
import org.drools.util.FastMap;
import org.drools.util.PrimitiveLongMap;
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
    protected final Map                       assertMap;

    protected Map                             queryResults                                  = Collections.EMPTY_MAP;

    protected GlobalResolver                  globalResolver;

    protected static final Object             NULL                                          = new Serializable() {
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

    protected final List                      factQueue                                     = new ArrayList();

    protected final ReentrantLock             lock                                          = new ReentrantLock();

    protected final boolean                   discardOnLogicalOverride;

    protected long                            propagationIdCounter;

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
        this.tms = new TruthMaintenanceSystem( this );
        final RuleBaseConfiguration conf = this.ruleBase.getConfiguration();

        if ( RuleBaseConfiguration.WM_BEHAVIOR_IDENTITY.equals( conf.getProperty( RuleBaseConfiguration.PROPERTY_ASSERT_BEHAVIOR ) ) ) {
            this.assertMap = new FastMap().setKeyComparator( new IdentityAssertMapComparator( this.handleFactory.getFactHandleType() ) );
        } else {
            this.assertMap = new FastMap().setKeyComparator( new EqualityAssertMapComparator( this.handleFactory.getFactHandleType() ) );
        }

        // Only takes effect if are using idententity behaviour for assert
        if ( RuleBaseConfiguration.WM_BEHAVIOR_DISCARD.equals( conf.getProperty( RuleBaseConfiguration.PROPERTY_LOGICAL_OVERRIDE_BEHAVIOR ) ) ) {
            this.discardOnLogicalOverride = true;
        } else {
            this.discardOnLogicalOverride = false;
        }

    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    void setRuleBase(InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
    }

    public void addEventListener(final WorkingMemoryEventListener listener) {
        try {
            lock.lock();
            this.workingMemoryEventSupport.addEventListener( listener );
        } finally {
            lock.unlock();
        }
    }

    public void removeEventListener(final WorkingMemoryEventListener listener) {
        try {
            lock.lock();
            this.workingMemoryEventSupport.removeEventListener( listener );
        } finally {
            lock.unlock();
        }
    }

    public List getWorkingMemoryEventListeners() {
        try {
            lock.lock();
            return this.workingMemoryEventSupport.getEventListeners();
        } finally {
            lock.unlock();
        }
    }

    public void addEventListener(final AgendaEventListener listener) {
        try {
            lock.lock();
            this.agendaEventSupport.addEventListener( listener );
        } finally {
            lock.unlock();
        }
    }

    public void removeEventListener(final AgendaEventListener listener) {
        try {
            lock.lock();
            this.agendaEventSupport.removeEventListener( listener );
        } finally {
            lock.unlock();
        }
    }

    public List getAgendaEventListeners() {
        try {
            lock.lock();
            return this.agendaEventSupport.getEventListeners();
        } finally {
            lock.unlock();
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
            lock.lock();
            return this.globals;
        } finally {
            lock.unlock();
        }
    }

    /**
     * @see WorkingMemory
     */
    public void setGlobal(final String name,
                          Object value) {
        // Cannot set null values
        if ( value == null ) {
            return;
        }
        
        try {
            lock.lock();
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
            lock.unlock();
        }
    }
    
    public void setGlobalResolver(GlobalResolver globalResolver) {
        this.globalResolver = globalResolver;
    }

    public long getId() {
        return this.id;
    }

    /**
     * @see WorkingMemory
     */
    public Object getGlobal(final String name) {
        try {
            lock.lock();
            Object object = this.globals.get( name );
            if ( object == null && this.globalResolver != null ) {
                object = this.globalResolver.resolve( name );
            }
            return object;
        } finally {
            lock.unlock();
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
    public void fireAllRules() throws FactException {
        fireAllRules( null );
    }

    public synchronized void fireAllRules(final AgendaFilter agendaFilter) throws FactException {
        // If we're already firing a rule, then it'll pick up
        // the firing for any other assertObject(..) that get
        // nested inside, avoiding concurrent-modification
        // exceptions, depending on code paths of the actions.

        if ( !this.factQueue.isEmpty() ) {
            propagateQueuedActions();
        }

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
    public Object getObject(final FactHandle handle) {
        try {
            lock.lock();
            // you must always take the value from the assertMap, incase the handle
            // is not from this WorkingMemory
            InternalFactHandle factHandle = (InternalFactHandle) this.assertMap.get( handle );
            if ( factHandle != null ) {
                return factHandle.getObject();
            }

            return null;
        } finally {
            lock.unlock();
        }

    }

    /**
     * @see WorkingMemory
     */
    public FactHandle getFactHandle(final Object object) {
        try {
            lock.lock();
            final FactHandle factHandle = (FactHandle) this.assertMap.get( object );

            return factHandle;
        } finally {
            lock.unlock();
        }
    }

    public List getFactHandles() {
        try {
            lock.lock();
            return new ArrayList( this.assertMap.values() );
        } finally {
            lock.unlock();
        }
    }

    /**
     * A helper method used to avoid lookups when iterating over facthandles and
     * objects at once. DO NOT MAKE THIS METHOD PUBLIC UNLESS YOU KNOW WHAT YOU
     * ARE DOING
     * 
     * @return
     */
    public Map getFactHandleMap() {
        return Collections.unmodifiableMap( this.assertMap );
    }

    /**
     * @see WorkingMemory
     */
    public List getObjects() {
        final List list = new ArrayList( this.assertMap.size() );

        for ( final Iterator it = this.assertMap.keySet().iterator(); it.hasNext(); ) {
            list.add( ((InternalFactHandle) it.next()).getObject() );
        }
        return list;
    }

    public List getObjects(final Class objectClass) {
        final List list = new ArrayList();

        for ( final Iterator it = this.assertMap.keySet().iterator(); it.hasNext(); ) {
            final Object object = ((InternalFactHandle) it.next()).getObject();

            if ( objectClass.isInstance( object ) ) {
                list.add( object );
            }
        }

        return list;
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
                                    handle );
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
                        handle.setObject( object );
                        return handle;
                    } else {
                        // override, then instantiate new handle for assertion
                        key.setStatus( EqualityKey.STATED );
                        handle = this.handleFactory.newFactHandle( object );
                        handle.setEqualityKey( key );
                        key.addFactHandle( handle );
                        this.assertMap.put( handle,
                                            handle );
                    }

                } else {
                    handle = this.handleFactory.newFactHandle( object );
                    this.assertMap.put( handle,
                                        handle );
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

            if ( dynamic ) {
                addPropertyChangeListener( object );
            }

            final PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                      PropagationContext.ASSERTION,
                                                                                      rule,
                                                                                      activation );

            // this.ruleBase.assertObject( handle,
            // object,
            // propagationContext,
            // this );

            doAssertObject( handle,
                            object,
                            propagationContext );

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

            final Method mehod = handle.getClass().getMethod( "removePropertyChangeListener",
                                                              AbstractWorkingMemory.ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES );

            mehod.invoke( handle,
                          this.addRemovePropertyChangeListenerArgs );
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

    // /**
    // * Associate an object with its handle.
    // *
    // * @param handle
    // * The handle.
    // * @param object
    // * The object.
    // */
    // public void putObject(InternalFactHandle handle,
    // Object object) {
    // this.assertMap.put( object,
    // handle );
    //
    // handle.setObject( object );
    // }
    //
    // public Object removeObject(InternalFactHandle handle) {
    // Object object = handle.getObject();
    //
    // this.assertMap.remove( object );
    //
    // return object;
    // }

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
                                                                                      activation );

            doRetract( handle,
                       propagationContext );

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

            final Object object = handle.getObject();

            this.workingMemoryEventSupport.fireObjectRetracted( propagationContext,
                                                                handle,
                                                                object );

            this.assertMap.remove( handle );

            this.handleFactory.destroyFactHandle( handle );

            if ( !this.factQueue.isEmpty() ) {
                propagateQueuedActions();
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
     * @see WorkingMemory
     */
    public abstract void modifyObject(FactHandle factHandle,
                                      Object object,
                                      Rule rule,
                                      Activation activation) throws FactException;

    public void propagateQueuedActions() {
        for ( final Iterator it = this.factQueue.iterator(); it.hasNext(); ) {
            final WorkingMemoryAction action = (WorkingMemoryAction) it.next();
            it.remove();
            action.propagate();
        }

    }

    public void queueWorkingMemoryAction(WorkingMemoryAction action) {
        this.factQueue.add( action );
    }

    public void queueRetractAction(final InternalFactHandle factHandle,
                                   final boolean removeLogical,
                                   final boolean updateEqualsMap,
                                   final Rule ruleOrigin,
                                   final Activation activationOrigin) {
        queueWorkingMemoryAction( new WorkingMemoryRetractAction( factHandle,
                                                                  false,
                                                                  true,
                                                                  ruleOrigin,
                                                                  activationOrigin ) );
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

    public void dispose() {
        this.ruleBase.disposeWorkingMemory( this );
    }

    public Lock getLock() {
        return this.lock;
    }

    public interface WorkingMemoryAction {
        public void propagate();
    }

    public class WorkingMemoryRetractAction
        implements
        WorkingMemoryAction {
        private InternalFactHandle factHandle;

        private boolean            removeLogical;

        private boolean            updateEqualsMap;

        private Rule               ruleOrigin;

        private Activation         activationOrigin;

        public WorkingMemoryRetractAction(final InternalFactHandle factHandle,
                                          final boolean removeLogical,
                                          final boolean updateEqualsMap,
                                          final Rule ruleOrigin,
                                          final Activation activationOrigin) {
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

}
