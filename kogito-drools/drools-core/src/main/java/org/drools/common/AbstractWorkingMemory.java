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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
import org.drools.RuleBase;
import org.drools.WorkingMemory;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
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
 * @author <a href="mailto:bagerman@gmail.com">Alexander Bagerman </a>
 */
abstract public class AbstractWorkingMemory implements WorkingMemory,
        InternalWorkingMemoryActions, EventSupport, PropertyChangeListener {
    // ------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------
    private static final Class[]              ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[] { PropertyChangeListener.class };

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The arguments used when adding/removing a property change listener. */
    protected final Object[]                  addRemovePropertyChangeListenerArgs           = new Object[] { this };

    /** Global values which are associated with this memory. */
    private final Map                         globals                                       = new HashMap( );

    /** Object-to-handle mapping. */
    protected final Map                       identityMap                                   = new IdentityMap( );

    protected final Map                       equalsMap                                     = new HashMap( );

    protected final PrimitiveLongMap          justified                                     = new PrimitiveLongMap( 8,
                                                                                                                    32 );

    private final PrimitiveLongStack          factHandlePool                                = new PrimitiveLongStack( );

    protected static final String             STATED                                        = "STATED";

    protected static final String             JUSTIFIED                                     = "JUSTIFIED";

    protected static final String             NEW                                           = "NEW";

    protected static final FactStatus         STATUS_NEW                                    = new FactStatus( NEW,
                                                                                                              0 );

    /** The eventSupport */
    protected final WorkingMemoryEventSupport workingMemoryEventSupport                     = new WorkingMemoryEventSupport( this );

    protected final AgendaEventSupport        agendaEventSupport                            = new AgendaEventSupport( this );

    /** The <code>RuleBase</code> with which this memory is associated. */
    protected final RuleBase                  ruleBase;

    protected final FactHandleFactory         handleFactory;

    /** Rule-firing agenda. */
    // protected final Agenda agenda;
    /** Flag to determine if a rule is currently being fired. */
    protected boolean                         firing;

    protected long                            propagationIdCounter;

    private ReentrantLock                     lock                                          = new ReentrantLock( );

    private List                              factQueue                                     = new ArrayList( );

    public AbstractWorkingMemory(RuleBase ruleBase,
                                 FactHandleFactory handleFactory) {
        this.ruleBase = ruleBase;
        //		this.agenda = new Agenda(this);
        this.handleFactory = handleFactory;
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
    public Object getGlobal(String name) {
        return this.globals.get( name );
    }

    /**
     * Clear the Agenda
     */
    abstract public void clearAgenda();

    /**
     * @see WorkingMemory
     */
    public RuleBase getRuleBase() {
        return this.ruleBase;
    }

    abstract public void fireAllRules(AgendaFilter agendaFilter) throws FactException;

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
    abstract public Object getObject(FactHandle handle);

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

    /**
     * @see WorkingMemory
     */
    abstract public boolean containsObject(FactHandle handle);

    protected void addPropertyChangeListener(Object object) {
        try {
            Method method = object.getClass().getMethod( "addPropertyChangeListener",
                                                         AbstractWorkingMemory.ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES );

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

    protected void removePropertyChangeListener(FactHandle handle) throws NoSuchFactObjectException {
        Object object = null;
        try {
            object = getObject( handle );

            Method mehod = handle.getClass().getMethod( "removePropertyChangeListener",
                                                        AbstractWorkingMemory.ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES );

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
    abstract public void retractObject(FactHandle handle,
                                       boolean removeLogical,
                                       boolean updateEqualsMap,
                                       Rule rule,
                                       Activation activation) throws FactException;

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
    abstract public void modifyObject(FactHandle handle,
                                      Object object,
                                      Rule rule,
                                      Activation activation) throws FactException;

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

    public PrimitiveLongMap getJustified() {
        return this.justified;
    }

    public long getNextPropagationIdCounter() {
        return this.propagationIdCounter++;
    }

    abstract public void dispose();

    public void removeLogicalDependencies(Activation activation,
                                          PropagationContext context,
                                          Rule rule) throws FactException {
        org.drools.util.LinkedList list = activation.getLogicalDependencies();
        if ( list == null || list.isEmpty() ) {
            return;
        }
        for ( LogicalDependency node = (LogicalDependency) list.getFirst(); node != null; node = (LogicalDependency) node.getNext() ) {
            InternalFactHandle handle = (InternalFactHandle) node.getFactHandle();
            Set set = (Set) this.justified.get( handle.getId( ) );
            // check set for null because in some weird cases on logical assertion  
            // it comes back with the same activation/handle and tries on
            // already cleaned this.justified. only happens on removal of rule 
            // from the working memory
            if (set != null) {
                set.remove( node );
                if (set.isEmpty( )) {
                    this.justified.remove( handle.getId( ) );
                    // this needs to be scheduled so we don't upset the current
                    // working memory operation
                    this.factQueue.add( new WorkingMemoryRetractAction( handle,
                                                                        false,
                                                                        true,
                                                                        context.getRuleOrigin( ),
                                                                        context.getActivationOrigin( ) ) );
                }
            }
        }
    }

    public void removeLogicalDependencies(FactHandle handle) throws FactException {
        Set set = (Set) this.justified.remove( ((InternalFactHandle) handle).getId() );
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
        Set set = (Set) this.justified.get( ((InternalFactHandle) handle).getId() );
        if ( set == null ) {
            set = new HashSet();
            this.justified.put( ((InternalFactHandle) handle).getId(),
                                set );
        }
        set.add( node );
    }

    protected void propagateQueuedActions() {
        if (!this.factQueue.isEmpty( )) {
            for (Iterator it = this.factQueue.iterator( ); it.hasNext( );) {
                WorkingMemoryAction action = (WorkingMemoryAction) it.next( );
                it.remove( );
                action.propagate( );
            }
        }
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

    protected static class FactStatus {
        private int            counter;
        private String         status;
        private InternalFactHandle handle;

        public FactStatus() {
            this( AbstractWorkingMemory.STATED,
                  1 );
        }

        public FactStatus(String status) {
            this( status,
                  1 );
        }

        public FactStatus(String status,
                          InternalFactHandle handle) {
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
        public InternalFactHandle getHandle() {
            return handle;
        }

        /**
         * @param handle the handle to set
         */
        public void setHandle(InternalFactHandle handle) {
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

