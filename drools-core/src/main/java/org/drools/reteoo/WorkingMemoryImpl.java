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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.NoSuchFactHandleException;
import org.drools.NoSuchFactObjectException;
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.common.Agenda;
import org.drools.common.AgendaItem;
import org.drools.common.EventSupport;
import org.drools.common.LogicalDependency;
import org.drools.common.PropagationContextImpl;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.ReteooNodeEventListener;
import org.drools.event.ReteooNodeEventSupport;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.drools.util.IdentityMap;
import org.drools.util.PrimitiveLongMap;
import org.drools.util.PrimitiveLongStack;

/**
 * Implementation of <code>WorkingMemory</code>.
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris </a>
 */
class WorkingMemoryImpl
    implements
    WorkingMemory,
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
    private final Map                       globalValues                                  = new HashMap();

    /** Handle-to-object mapping. */
    private final PrimitiveLongMap          objects                                       = new PrimitiveLongMap( 32,
                                                                                                                  8 );

    /** Object-to-handle mapping. */
    private final Map                       identityMap                                   = new IdentityMap();
    private final Map                       equalsMap                                     = new HashMap();

    private final PrimitiveLongMap          justified                                     = new PrimitiveLongMap( 8,
                                                                                                                  32 );
    private final PrimitiveLongStack        factHandlePool                                = new PrimitiveLongStack();

    private static final String             STATED                                        = "STATED";

    /** The eventSupport */
    private final WorkingMemoryEventSupport workingMemoryEventSupport                     = new WorkingMemoryEventSupport( this );
    private final AgendaEventSupport        agendaEventSupport                            = new AgendaEventSupport( this );
    private final ReteooNodeEventSupport    reteooNodeEventSupport                        = new ReteooNodeEventSupport( this );

    /** The <code>RuleBase</code> with which this memory is associated. */
    private final RuleBaseImpl              ruleBase;

    private final FactHandleFactory         handleFactory;

    /** Rule-firing agenda. */
    private final Agenda                    agenda;

    /** Flag to determine if a rule is currently being fired. */
    private boolean                         firing;

    private long                            propagationIdCounter;

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

    public void addEventListener(ReteooNodeEventListener listener) {
        this.reteooNodeEventSupport.addEventListener( listener );
    }

    public void removeEventListener(ReteooNodeEventListener listener) {
        this.reteooNodeEventSupport.removeEventListener( listener );
    }

    public List getReteooNodeEventListeners() {
        return this.reteooNodeEventSupport.getEventListeners();
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
        return this.globalValues;
    }

    /**
     * @see WorkingMemory
     */
    public void setGlobal(String name,
                          Object value) {
        // Make sure the application data has been declared in the RuleBase
        Map applicationDataDefintions = this.ruleBase.getGlobalDeclarations();
        Class type = (Class) applicationDataDefintions.get( name );
        if ( (type == null) ) {
            throw new RuntimeException( "Unexpected application data [" + name + "]" );
        } else if ( !type.isInstance( value ) ) {
            throw new RuntimeException( "Illegal class for application data. " + "Expected [" + type.getName() + "], " + "found [" + value.getClass().getName() + "]." );

        } else {
            this.globalValues.put( name,
                                      value );
        }
    }

    /**
     * @see WorkingMemory
     */
    public Object getGlobal(String name) {
        return this.globalValues.get( name );
    }

    /**
     * Retrieve the rule-firing <code>Agenda</code> for this
     * <code>WorkingMemory</code>.
     * 
     * @return The <code>Agenda</code>.
     */
    protected Agenda getAgenda() {
        return this.agenda;
    }

    /**
     * Clear the Agenda
     */
    public void clearAgenda() {
        this.agenda.clearAgenda();
    }

    /**
     * @see WorkingMemory
     */
    public RuleBase getRuleBase() {
        return this.ruleBase;
    }

    public void fireAllRules(AgendaFilter agendaFilter) throws FactException {
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
     * @see WorkingMemory
     */
    public void fireAllRules() throws FactException {
        fireAllRules( null );
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
        FactHandleImpl handleImpl = (FactHandleImpl) handle;
        if ( handleImpl.getObject() == null ) {
            Object object = this.objects.get( handleImpl.getId() );
            if ( object == null ) {
                throw new NoSuchFactObjectException( handle );
            }
            handleImpl.setObject( object );
        }

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
     * @see WorkingMemory
     */
    public List getObjects() {
        return new ArrayList( this.objects.values() );
    }

    public List getObjects(Class objectClass) {
        List matching = new LinkedList();
        for ( Iterator objIter = this.objects.values().iterator(); objIter.hasNext(); ) {
            Object obj = objIter.next();

            if ( objectClass.isInstance( obj ) ) {
                matching.add( obj );
            }
        }

        return matching;
    }

    /**
     * @see WorkingMemory
     */
    public boolean containsObject(FactHandle handle) {
        return this.objects.containsKey( ((FactHandleImpl) handle).getId() );
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

    FactHandle assertObject(Object object,
                            boolean dynamic,
                            boolean logical,
                            Rule rule,
                            Activation activation) throws FactException {
        // check if the object already exists in the WM
        FactHandleImpl handle = (FactHandleImpl) this.identityMap.get( object );

        // return if the handle exists and this is a logical assertion
        if ( (handle != null) && (logical) ) {
            return handle;
        }

        // lets see if the object is already logical asserted
        Object logicalState = this.equalsMap.get( object );

        // if we have a handle and this STATED fact was previously STATED
        if ( (handle != null) && (!logical) && logicalState == WorkingMemoryImpl.STATED ) {
            return handle;
        }

        if ( !logical ) {
            // If this stated assertion already has justifications then we need
            // to cancel them
            if ( logicalState instanceof FactHandleImpl ) {
                handle = (FactHandleImpl) logicalState;
                removeLogicalDependencies( handle );
            } else {
                handle = (FactHandleImpl) newFactHandle();
            }

            putObject( handle,
                       object );

            this.equalsMap.put( object,
                                WorkingMemoryImpl.STATED );

            if ( dynamic ) {
                addPropertyChangeListener( object );
            }
        } else {
            // This object is already STATED, we cannot make it justifieable
            if ( logicalState == WorkingMemoryImpl.STATED ) {
                return null;
            }

            handle = (FactHandleImpl) logicalState;
            // we create a lookup handle for the first asserted equals object
            // all future equals objects will use that handle
            if ( handle == null ) {
                handle = (FactHandleImpl) newFactHandle();

                putObject( handle,
                           object );

                this.equalsMap.put( object,
                                    handle );
            }
            addLogicalDependency( handle, activation, activation.getPropagationContext(), rule );
        }

        handle.setObject( object );

        PropagationContext propagationContext = new PropagationContextImpl( ++this.propagationIdCounter,
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
    Object putObject(FactHandle handle,
                     Object object) {
        Object oldValue = this.objects.put( ((FactHandleImpl) handle).getId(),
                                            object );

        this.identityMap.put( object,
                              handle );

        ((FactHandleImpl) handle).setObject( object );
        return oldValue;
    }

    Object removeObject(FactHandle handle) {
        Object object = this.objects.remove( ((FactHandleImpl) handle).getId() );

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
        removePropertyChangeListener( handle );

        PropagationContext propagationContext = new PropagationContextImpl( ++this.propagationIdCounter,
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
            this.equalsMap.remove( oldObject );
        }                    

        if ( updateEqualsMap ) {
            this.equalsMap.remove( oldObject );
        }

        this.factHandlePool.push( ((FactHandleImpl) handle).getId() );

        this.workingMemoryEventSupport.fireObjectRetracted( propagationContext,
                                                            handle,
                                                            oldObject );

        ((FactHandleImpl) handle).invalidate();
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
                                handle );
        }

        PropagationContext propagationContext = new PropagationContextImpl( ++this.propagationIdCounter,
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
            memory = node.createMemory();

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

    public ReteooNodeEventSupport getReteooNodeEventSupport() {
        return this.reteooNodeEventSupport;
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
                retractObject( handle,
                               false,
                               true,
                               context.getRuleOrigin(),
                               context.getActivationOrigin() );                
            }            
        }
    }

    public void removeLogicalDependencies(FactHandle handle) throws FactException {
        Set set = (Set) this.justified.remove( ((FactHandleImpl) handle).getId() );
        if (!set.isEmpty()) {
            for( Iterator it = set.iterator(); it.hasNext(); ) {
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

    public void dispose() {
        this.ruleBase.disposeWorkingMemory( this );
    }
}
