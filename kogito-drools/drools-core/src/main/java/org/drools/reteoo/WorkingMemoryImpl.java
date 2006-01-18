package org.drools.reteoo;

/*
 * Copyright 2001-2004 (C) The Werken Company. All Rights Reserved.
 *
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "drools" must not be used to endorse or promote products derived
 * from this Software without prior written permission of The Werken Company.
 * For written permission, please contact bob@werken.com.
 *
 * 4. Products derived from this Software may not be called "drools" nor may
 * "drools" appear in their names without prior written permission of The Werken
 * Company. "drools" is a trademark of The Werken Company.
 *
 * 5. Due credit should be given to The Werken Company. (http://werken.com/)
 *
 * THIS SOFTWARE IS PROVIDED BY THE WERKEN COMPANY AND CONTRIBUTORS ``AS IS''
 * AND ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE WERKEN COMPANY OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
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
import org.drools.spi.PropagationContext;
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

    /** Application data which is associated with this memory. */
    private final Map                       applicationData                               = new HashMap();

    /** Handle-to-object mapping. */
    private final PrimitiveLongMap          objects                                       = new PrimitiveLongMap( 32,
                                                                                                                  8 );

    /** Object-to-handle mapping. */
    private final Map                       identityMap                                   = new IdentityMap();
    private final Map                       equalsMap                                     = new HashMap();
    private final Map                       justifiers                                    = new HashMap();
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
    
    private final FactHandleFactory               handleFactory;

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
        this.agenda = new Agenda( this,
                                  ruleBase.getConflictResolver() );
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
    public Map getApplicationDataMap() {
        return this.applicationData;
    }

    /**
     * @see WorkingMemory
     */
    public void setApplicationData(String name,
                                   Object value) {
        // Make sure the application data has been declared in the RuleBase
        Map applicationDataDefintions = this.ruleBase.getApplicationData();
        Class type = (Class) applicationDataDefintions.get( name );
        if ( (type == null) ) {
            throw new RuntimeException( "Unexpected application data [" + name + "]" );
        } else if ( !type.isInstance( value ) ) {
            throw new RuntimeException( "Illegal class for application data. " + "Expected [" + type.getName() + "], " + "found [" + value.getClass().getName() + "]." );

        } else {
            this.applicationData.put( name,
                                      value );
        }
    }

    /**
     * @see WorkingMemory
     */
    public Object getApplicationData(String name) {
        return this.applicationData.get( name );
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
     * @see WorkingMemory
     */
    public Object getObject(FactHandle handle) throws NoSuchFactObjectException {
        Object object = this.objects.get( ((FactHandleImpl) handle).getId() );

        if ( object == null ) {
            throw new NoSuchFactObjectException( handle );
        }

        return object;
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle getFactHandle(Object object) throws NoSuchFactHandleException {
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
        /* check if the object already exists in the WM */
        FactHandle handle = (FactHandle) this.identityMap.get( object );

        /* only return if the handle exists and this is a logical assertion */
        if ( (handle != null) && (logical) ) {
            return handle;
        }

        /* lets see if the object is already logical asserted */
        Object logicalState = this.equalsMap.get( object );

        /* if we have a handle and this STATED fact was previously STATED */
        if ( (handle != null) && (!logical) && logicalState == WorkingMemoryImpl.STATED ) {
            return handle;
        }

        if ( !logical ) {
            /*
             * If this stated assertion already has justifications then we need
             * to cancel them
             */
            if ( logicalState instanceof FactHandleImpl ) {
                handle = (FactHandleImpl) logicalState;
                /*
                 * remove handle from the justified Map and then iterate each of
                 * each Activations. For each Activation remove the handle. If
                 * the Set is empty then remove the activation from justiers.
                 */
                Set activationList = (Set) this.justified.remove( ((FactHandleImpl) handle).getId() );
                for ( Iterator it = activationList.iterator(); it.hasNext(); ) {
                    Activation eachActivation = (Activation) it.next();
                    Set handles = (Set) this.justifiers.get( eachActivation );
                    handles.remove( handle );
                    // if an activation has no justified assertions then remove
                    // it
                    if ( handles.isEmpty() ) {
                        this.justifiers.remove( eachActivation );
                    }
                }
            } else {
                handle = newFactHandle();
            }

            putObject( handle,
                       object );

            this.equalsMap.put( object,
                                WorkingMemoryImpl.STATED );

            if ( dynamic ) {
                addPropertyChangeListener( object );
            }
        } else {
            /* This object is already STATED, we cannot make it justifieable */
            if ( logicalState == WorkingMemoryImpl.STATED ) {
                return null;
            }

            handle = (FactHandleImpl) logicalState;
            /*
             * we create a lookup handle for the first asserted equals object
             * all future equals objects will use that handle
             */
            if ( handle == null ) {
                handle = (FactHandleImpl) newFactHandle();

                putObject( handle,
                           object );

                this.equalsMap.put( object,
                                    handle );
            }
            Set activationList = (Set) this.justified.get( ((FactHandleImpl) handle).getId() );
            if ( activationList == null ) {
                activationList = new HashSet();
                this.justified.put( ((FactHandleImpl) handle).getId(),
                                    activationList );
            }
            activationList.add( activation );

            Set handles = (Set) this.justifiers.get( activation );
            if ( handles == null ) {
                handles = new HashSet();
                this.justifiers.put( activation,
                                     handles );
            }
            handles.add( handle );
        }        

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

        PropagationContext propagationContext = 
            new PropagationContextImpl( ++this.propagationIdCounter,
                                        PropagationContext.RETRACTION,
                                        rule,
                                        activation );
        
        this.ruleBase.retractObject( handle,
                                     propagationContext,
                                     this );

        Object oldObject = removeObject( handle );

        /* check to see if this was a logical asserted object */
        if ( removeLogical ) {
            FactHandleImpl handleImpl = (FactHandleImpl) handle;
            Set activations = (Set) this.justified.remove( handleImpl.getId() );
            if ( activations != null ) {
                for ( Iterator it = activations.iterator(); it.hasNext(); ) {
                    this.justifiers.remove( it.next() );
                }
            }
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

        this.ruleBase.retractObject( handle,
                                     propagationContext,
                                     this );

        this.ruleBase.assertObject( handle,
                                    object,
                                    propagationContext,
                                    this );

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
        this.agenda.setAsyncExceptionHandler( handler );
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

    /*
     * public PrimitiveLongMap getJustified() { return this.justified; }
     * 
     * public Map getJustifiers() { return this.justifiers; }
     */
    public void removeLogicalAssertions(TupleKey key,
                                        PropagationContext context,
                                        Rule rule) throws FactException {
        for ( Iterator it = this.justifiers.keySet().iterator(); it.hasNext(); ) {
            AgendaItem item = (AgendaItem) it.next();

            if ( item.getRule() == rule && item.getKey().containsAll( key ) ) {
                removeLogicalAssertions( item,
                                         context,
                                         rule );
            }
        }

    }

    public void removeLogicalAssertions(Activation activation,
                                        PropagationContext context,
                                        Rule rule) throws FactException {
        Set handles = (Set) this.justifiers.remove( activation );
        /* no justified facts for this activation */
        if ( handles == null ) {
            return;
        }
        for ( Iterator it = handles.iterator(); it.hasNext(); ) {
            FactHandleImpl handle = (FactHandleImpl) it.next();
            Set activations = (Set) this.justified.get( handle.getId() );
            activations.remove( activation );
            if ( activations.isEmpty() ) {
                this.justified.remove( handle.getId() );
                retractObject( handle,
                               false,
                               true,
                               context.getRuleOrigin(),
                               context.getActivationOrigin() );
            }

        }
    }

    public PrimitiveLongMap getJustified() {
        return this.justified;
    }

    public Map getJustifiers() {
        return this.justifiers;
    }

    public void dispose() {
        this.ruleBase.disposeWorkingMemory( this );
    }
}
