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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.Agenda;
import org.drools.EntryPointInterface;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.ObjectFilter;
import org.drools.Otherwise;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuntimeDroolsException;
import org.drools.WorkingMemory;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.RuleBaseConfiguration.LogicalOverride;
import org.drools.base.MapGlobalResolver;
import org.drools.base.ShadowProxy;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.RuleFlowEventSupport;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.facttemplates.Fact;
import org.drools.process.core.Process;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.ProcessInstanceFactory;
import org.drools.process.instance.WorkItemManager;
import org.drools.reteoo.ClassObjectTypeConf;
import org.drools.reteoo.FactTemplateTypeConf;
import org.drools.reteoo.LIANodePropagation;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.rule.TimeMachine;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.ruleflow.instance.RuleFlowProcessInstance;
import org.drools.ruleflow.instance.RuleFlowProcessInstanceFactory;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AgendaGroup;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.GlobalResolver;
import org.drools.spi.PropagationContext;
import org.drools.temporal.SessionClock;

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
    protected static final Class[]                       ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[]{PropertyChangeListener.class};
    private static final int                             NODE_MEMORIES_ARRAY_GROWTH                    = 32;

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------
    protected long                                       id;

    /** The arguments used when adding/removing a property change listener. */
    protected final Object[]                             addRemovePropertyChangeListenerArgs           = new Object[]{this};

    /** The actual memory for the <code>JoinNode</code>s. */
    protected final NodeMemories                         nodeMemories;

    protected final ObjectStore                          objectStore;

    protected Map                                        queryResults                                  = Collections.EMPTY_MAP;

    /** Global values which are associated with this memory. */
    protected GlobalResolver                             globalResolver;

    protected static final Object                        NULL                                          = new Serializable() {
                                                                                                           private static final long serialVersionUID = 400L;
                                                                                                       };

    /** The eventSupport */
    protected WorkingMemoryEventSupport                  workingMemoryEventSupport                     = new WorkingMemoryEventSupport();

    protected AgendaEventSupport                         agendaEventSupport                            = new AgendaEventSupport();

    protected RuleFlowEventSupport                       workflowEventSupport                          = new RuleFlowEventSupport();

    protected List                                       __ruleBaseEventListeners                      = new LinkedList();

    /** The <code>RuleBase</code> with which this memory is associated. */
    protected transient InternalRuleBase                 ruleBase;

    protected final FactHandleFactory                    handleFactory;

    protected final TruthMaintenanceSystem               tms;

    /** Rule-firing agenda. */
    protected DefaultAgenda                              agenda;

    protected final List                                 actionQueue                                   = new ArrayList();

    protected final ReentrantLock                        lock                                          = new ReentrantLock();

    protected final boolean                              discardOnLogicalOverride;

    protected long                                       propagationIdCounter;

    private final boolean                                maintainTms;

    private final boolean                                sequential;

    private List                                         liaPropagations                               = Collections.EMPTY_LIST;

    /** Flag to determine if a rule is currently being fired. */
    protected boolean                                    firing;

    protected boolean                                    halt;

    private Map                                          processInstances                              = new HashMap();

    private int                                          processCounter;

    private WorkItemManager                              workItemManager;

    private Map<String, ProcessInstanceFactory>          processInstanceFactories                      = new HashMap();

    private TimeMachine                                  timeMachine                                   = new TimeMachine();

    private Map<EntryPoint, Map<Object, ObjectTypeConf>> typeConfMap;

    private SessionClock                                 sessionClock;

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
        this.globalResolver = new MapGlobalResolver();
        this.maintainTms = this.ruleBase.getConfiguration().isMaintainTms();
        this.sequential = this.ruleBase.getConfiguration().isSequential();

        this.nodeMemories = new ConcurrentNodeMemories( this.ruleBase );

        if ( this.maintainTms ) {
            this.tms = new TruthMaintenanceSystem( this );
        } else {
            this.tms = null;
        }

        final RuleBaseConfiguration conf = this.ruleBase.getConfiguration();

        this.objectStore = new SingleThreadedObjectStore( conf,
                                                          this.lock );

        // Only takes effect if are using idententity behaviour for assert
        if ( conf.getLogicalOverride() == LogicalOverride.DISCARD ) {
            this.discardOnLogicalOverride = true;
        } else {
            this.discardOnLogicalOverride = false;
        }

        this.workItemManager = new WorkItemManager( this );
        this.processInstanceFactories.put( RuleFlowProcess.RULEFLOW_TYPE,
                                           new RuleFlowProcessInstanceFactory() );

        this.typeConfMap = new ConcurrentHashMap<EntryPoint, Map<Object, ObjectTypeConf>>();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    public void setRuleBase(final InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.nodeMemories.setRuleBaseReference( this.ruleBase );
    }

    public void setWorkingMemoryEventSupport(WorkingMemoryEventSupport workingMemoryEventSupport) {
        this.workingMemoryEventSupport = workingMemoryEventSupport;
    }

    public void setAgendaEventSupport(AgendaEventSupport agendaEventSupport) {
        this.agendaEventSupport = agendaEventSupport;
    }

    public void setRuleFlowEventSupport(RuleFlowEventSupport ruleFlowEventSupport) {
        this.workflowEventSupport = ruleFlowEventSupport;
    }

    public boolean isSequential() {
        return this.sequential;
    }

    public void addLIANodePropagation(LIANodePropagation liaNodePropagation) {
        if ( this.liaPropagations == Collections.EMPTY_LIST ) {
            this.liaPropagations = new ArrayList();
        }
        this.liaPropagations.add( liaNodePropagation );
    }

    public void addEventListener(final WorkingMemoryEventListener listener) {
        this.workingMemoryEventSupport.addEventListener( listener );
    }

    public void removeEventListener(final WorkingMemoryEventListener listener) {
        this.workingMemoryEventSupport.removeEventListener( listener );
    }

    public List getWorkingMemoryEventListeners() {
        return this.workingMemoryEventSupport.getEventListeners();
    }

    public void addEventListener(final AgendaEventListener listener) {
        this.agendaEventSupport.addEventListener( listener );
    }

    public void removeEventListener(final AgendaEventListener listener) {
        this.agendaEventSupport.removeEventListener( listener );
    }

    public List getAgendaEventListeners() {
        return this.agendaEventSupport.getEventListeners();
    }

    public void addEventListener(final RuleFlowEventListener listener) {
        this.workflowEventSupport.addEventListener( listener );
    }

    public void removeEventListener(final RuleFlowEventListener listener) {
        this.workflowEventSupport.removeEventListener( listener );
    }

    public List getRuleFlowEventListeners() {
        return this.workflowEventSupport.getEventListeners();
    }

    public void addEventListener(RuleBaseEventListener listener) {
        this.ruleBase.addEventListener( listener );
        this.__ruleBaseEventListeners.add( listener );
    }

    public List getRuleBaseEventListeners() {
        return Collections.unmodifiableList( this.__ruleBaseEventListeners );
    }

    public void removeEventListener(RuleBaseEventListener listener) {
        this.ruleBase.removeEventListener( listener );
        this.__ruleBaseEventListeners.remove( listener );
    }

    public FactHandleFactory getFactHandleFactory() {
        return this.handleFactory;
    }

    public void setGlobal(final String identifier,
                          final Object value) {
        // Cannot set null values
        if ( value == null ) {
            return;
        }

        try {
            this.lock.lock();
            // Make sure the global has been declared in the RuleBase
            final Map globalDefintions = this.ruleBase.getGlobals();
            final Class type = (Class) globalDefintions.get( identifier );
            if ( (type == null) ) {
                throw new RuntimeException( "Unexpected global [" + identifier + "]" );
            } else if ( !type.isInstance( value ) ) {
                throw new RuntimeException( "Illegal class for global. " + "Expected [" + type.getName() + "], " + "found [" + value.getClass().getName() + "]." );

            } else {
                this.globalResolver.setGlobal( identifier,
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

    public GlobalResolver getGlobalResolver() {
        return this.globalResolver;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Object getGlobal(final String identifier) {
        try {
            this.lock.lock();
            return this.globalResolver.resolveGlobal( identifier );
        } finally {
            this.lock.unlock();
        }
    }

    public Agenda getAgenda() {
        return this.agenda;
    }

    public void clearAgenda() {
        this.agenda.clearAgenda();
    }

    public void clearAgendaGroup(final String group) {
        this.agenda.clearAgendaGroup( group );
    }

    public void clearActivationGroup(final String group) {
        this.agenda.clearActivationGroup( group );
    }

    public void clearRuleFlowGroup(final String group) {
        this.agenda.clearRuleFlowGroup( group );
    }

    public RuleBase getRuleBase() {
        return this.ruleBase;
    }

    public void halt() {
        this.halt = true;
    }

    public synchronized void fireAllRules() throws FactException {
        fireAllRules( null,
                      -1 );
    }

    public synchronized void fireAllRules(int fireLimit) throws FactException {
        fireAllRules( null,
                      fireLimit );
    }

    public synchronized void fireAllRules(final AgendaFilter agendaFilter) throws FactException {
        fireAllRules( agendaFilter,
                      -1 );
    }

    public synchronized void fireAllRules(final AgendaFilter agendaFilter,
                                          int fireLimit) throws FactException {
        // If we're already firing a rule, then it'll pick up
        // the firing for any other assertObject(..) that get
        // nested inside, avoiding concurrent-modification
        // exceptions, depending on code paths of the actions.
        this.halt = false;

        if ( isSequential() ) {
            for ( Iterator it = this.liaPropagations.iterator(); it.hasNext(); ) {
                ((LIANodePropagation) it.next()).doPropagation( this );
            }
        }

        if ( !this.actionQueue.isEmpty() ) {
            executeQueuedActions();
        }

        boolean noneFired = true;

        if ( !this.firing ) {
            try {
                this.firing = true;

                while ( continueFiring( fireLimit ) && this.agenda.fireNextItem( agendaFilter ) ) {
                    fireLimit = updateFireLimit( fireLimit );
                    noneFired = false;
                    if ( !this.actionQueue.isEmpty() ) {
                        executeQueuedActions();
                    }
                }
            } finally {
                this.firing = false;
                // @todo (mproctor) disabling Otherwise management for now, not
                // happy with the current implementation
                // if ( noneFired ) {
                // doOtherwise( agendaFilter,
                // fireLimit );
                // }

            }
        }
    }

    private final boolean continueFiring(final int fireLimit) {
        return (!halt) && (fireLimit != 0);
    }

    private final int updateFireLimit(final int fireLimit) {
        return fireLimit > 0 ? fireLimit - 1 : fireLimit;
    }

    /**
     * This does the "otherwise" phase of processing. If no items are fired,
     * then it will assert a temporary "Otherwise" fact and allow any rules to
     * fire to handle "otherwise" cases.
     */
    private void doOtherwise(final AgendaFilter agendaFilter,
                             int fireLimit) {
        final FactHandle handle = this.insert( new Otherwise() );
        if ( !this.actionQueue.isEmpty() ) {
            executeQueuedActions();
        }

        while ( continueFiring( fireLimit ) && this.agenda.fireNextItem( agendaFilter ) ) {
            fireLimit = updateFireLimit( fireLimit );
        }

        this.retract( handle );
    }

    //
    // MN: The following is the traditional fireAllRules (without otherwise).
    // Purely kept here as this implementation of otherwise is still
    // experimental.
    //
    // public synchronized void fireAllRules(final AgendaFilter agendaFilter)
    // throws FactException {
    // // If we're already firing a rule, then it'll pick up
    // // the firing for any other assertObject(..) that get
    // // nested inside, avoiding concurrent-modification
    // // exceptions, depending on code paths of the actions.
    //
    // if ( !this.factQueue.isEmpty() ) {
    // propagateQueuedActions();
    // }
    //
    // if ( !this.firing ) {
    // try {
    // this.firing = true;
    //
    // while ( this.agenda.fireNextItem( agendaFilter ) ) {
    // ;
    // }
    // } finally {
    // this.firing = false;
    // }
    // }
    // }

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
        return this.objectStore.getObjectForHandle( (InternalFactHandle) handle );
    }

    public ObjectStore getObjectStore() {
        return this.objectStore;
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle getFactHandle(final Object object) {
        return this.objectStore.getHandleForObject( object );
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle getFactHandleByIdentity(final Object object) {
        return this.objectStore.getHandleForObjectIdentity( object );
    }

    /**
     * This class is not thread safe, changes to the working memory during
     * iteration may give unexpected results
     */
    public Iterator iterateObjects() {
        return this.objectStore.iterateObjects();
    }

    /**
     * This class is not thread safe, changes to the working memory during
     * iteration may give unexpected results
     */
    public Iterator iterateObjects(ObjectFilter filter) {
        return this.objectStore.iterateObjects( filter );
    }

    /**
     * This class is not thread safe, changes to the working memory during
     * iteration may give unexpected results
     */
    public Iterator iterateFactHandles() {
        return this.objectStore.iterateFactHandles();
    }

    /**
     * This class is not thread safe, changes to the working memory during
     * iteration may give unexpected results
     */
    public Iterator iterateFactHandles(ObjectFilter filter) {
        return this.objectStore.iterateFactHandles( filter );
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
    public FactHandle insert(final Object object) throws FactException {
        return insert( object, /* Not-Dynamic */
                       0,
                       false,
                       false,
                       null,
                       null );
    }

    public FactHandle insert(final Object object,
                             final long duration) throws FactException {
        return insert( object, /* Not-Dynamic */
                       duration,
                       false,
                       false,
                       null,
                       null );
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle insertLogical(final Object object) throws FactException {
        return insert( object, //Not-Dynamic 
                       0,
                       false,
                       true,
                       null,
                       null );
    }

    public FactHandle insertLogical(final Object object,
                                    final long duration) throws FactException {
        return insert( object, /* Not-Dynamic */
                       duration,
                       false,
                       true,
                       null,
                       null );
    }

    public FactHandle insert(final Object object,
                             final boolean dynamic) throws FactException {
        return insert( object,
                       0,
                       dynamic,
                       false,
                       null,
                       null );
    }

    public FactHandle insert(final Object object,
                             final long duration,
                             final boolean dynamic) throws FactException {
        return insert( object,
                       duration,
                       dynamic,
                       false,
                       null,
                       null );
    }

    public FactHandle insertLogical(final Object object,
                                    final boolean dynamic) throws FactException {
        return insert( object,
                       0,
                       dynamic,
                       true,
                       null,
                       null );
    }

    public FactHandle insertLogical(final Object object,
                                    final long duration,
                                    final boolean dynamic) throws FactException {
        return insert( object,
                       duration,
                       dynamic,
                       true,
                       null,
                       null );
    }

    public FactHandle insert(final Object object,
                             final boolean dynamic,
                             boolean logical,
                             final Rule rule,
                             final Activation activation) throws FactException {
        return this.insert( EntryPoint.DEFAULT,
                            object,
                            0,
                            dynamic,
                            logical,
                            rule,
                            activation );

    }

    public FactHandle insert(final Object object,
                             final long duration,
                             final boolean dynamic,
                             boolean logical,
                             final Rule rule,
                             final Activation activation) throws FactException {
        return this.insert( EntryPoint.DEFAULT,
                            object,
                            duration,
                            dynamic,
                            logical,
                            rule,
                            activation );
    }

    protected FactHandle insert(final EntryPoint entryPoint,
                                final Object object,
                                final boolean dynamic,
                                boolean logical,
                                final Rule rule,
                                final Activation activation) throws FactException {
        return this.insert( entryPoint,
                            object,
                            0,
                            dynamic,
                            logical,
                            rule,
                            activation );
    }

    protected FactHandle insert(final EntryPoint entryPoint,
                                final Object object,
                                final long duration,
                                final boolean dynamic,
                                boolean logical,
                                final Rule rule,
                                final Activation activation) throws FactException {
        if ( object == null ) {
            // you cannot assert a null object
            return null;
        }

        ObjectTypeConf typeConf = getObjectTypeConf( entryPoint,
                                                     object );

        InternalFactHandle handle = null;

        if ( isSequential() ) {
            handle = this.handleFactory.newFactHandle( object,
                                                       typeConf.isEvent(),
                                                       duration,
                                                       this );
            this.objectStore.addHandle( handle,
                                        object );
            insert( entryPoint,
                    handle,
                    object,
                    rule,
                    activation );
            return handle;
        }

        try {
            this.lock.lock();
            // check if the object already exists in the WM
            handle = (InternalFactHandle) this.objectStore.getHandleForObject( object );

            if ( this.maintainTms ) {

                EqualityKey key = null;

                if ( handle == null ) {
                    // lets see if the object is already logical asserted
                    key = this.tms.get( object );
                } else {
                    // Object is already asserted, so check and possibly correct
                    // its
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
                    // key is also null, so treat as a totally new
                    // stated/logical
                    // assert
                    handle = this.handleFactory.newFactHandle( object,
                                                               typeConf.isEvent(),
                                                               duration,
                                                               this );
                    this.objectStore.addHandle( handle,
                                                object );

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
                        // Its previous justified, so switch to stated and
                        // remove
                        // logical dependencies
                        final InternalFactHandle justifiedHandle = key.getFactHandle();
                        this.tms.removeLogicalDependencies( justifiedHandle );

                        if ( this.discardOnLogicalOverride ) {
                            // override, setting to new instance, and return
                            // existing handle
                            key.setStatus( EqualityKey.STATED );
                            handle = key.getFactHandle();

                            if ( this.ruleBase.getConfiguration().getAssertBehaviour() == AssertBehaviour.IDENTITY ) {
                                // as assertMap may be using an "identity"
                                // equality comparator,
                                // we need to remove the handle from the map,
                                // before replacing the object
                                // and then re-add the handle. Otherwise we may
                                // end up with a leak.
                                this.objectStore.updateHandle( handle,
                                                               object );
                            } else {
                                Object oldObject = handle.getObject();
                                if ( oldObject instanceof ShadowProxy ) {
                                    ((ShadowProxy) oldObject).setShadowedObject( object );
                                } else {
                                    handle.setObject( object );
                                }
                            }
                            return handle;
                        } else {
                            // override, then instantiate new handle for
                            // assertion
                            key.setStatus( EqualityKey.STATED );
                            handle = this.handleFactory.newFactHandle( object,
                                                                       typeConf.isEvent(),
                                                                       duration,
                                                                       this );
                            handle.setEqualityKey( key );
                            key.addFactHandle( handle );
                            this.objectStore.addHandle( handle,
                                                        object );

                        }

                    } else {
                        handle = this.handleFactory.newFactHandle( object,
                                                                   typeConf.isEvent(),
                                                                   duration,
                                                                   this );
                        this.objectStore.addHandle( handle,
                                                    object );
                        key.addFactHandle( handle );
                        handle.setEqualityKey( key );

                    }

                } else {
                    if ( key.getStatus() == EqualityKey.JUSTIFIED ) {
                        // only add as logical dependency if this wasn't
                        // previously
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
                handle = this.handleFactory.newFactHandle( object,
                                                           typeConf.isEvent(),
                                                           duration,
                                                           this );
                this.objectStore.addHandle( handle,
                                            object );

            }

            if ( dynamic ) {
                addPropertyChangeListener( object );
            }

            insert( entryPoint,
                    handle,
                    object,
                    rule,
                    activation );

        } finally {
            this.lock.unlock();
        }
        return handle;
    }

    protected void insert(final EntryPoint entryPoint,
                          final InternalFactHandle handle,
                          final Object object,
                          final Rule rule,
                          final Activation activation) {
        this.ruleBase.executeQueuedActions();

        if ( activation != null ) {
            // release resources so that they can be GC'ed
            activation.getPropagationContext().releaseResources();
        }
        final PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                  PropagationContext.ASSERTION,
                                                                                  rule,
                                                                                  activation,
                                                                                  this.agenda.getActiveActivations(),
                                                                                  this.agenda.getDormantActivations(),
                                                                                  entryPoint );

        doInsert( handle,
                  object,
                  propagationContext );

        if ( !this.actionQueue.isEmpty() ) {
            executeQueuedActions();
        }

        this.workingMemoryEventSupport.fireObjectInserted( propagationContext,
                                                           handle,
                                                           object,
                                                           this );
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

    public abstract void doInsert(InternalFactHandle factHandle,
                                  Object object,
                                  PropagationContext propagationContext) throws FactException;

    protected void removePropertyChangeListener(final FactHandle handle) {
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
            throw new RuntimeDroolsException( "Warning: The removePropertyChangeListener method on the class " + object.getClass() + " does not take a simple PropertyChangeListener argument so Drools will be unable to stop processing JavaBean"
                                              + " PropertyChangeEvents on the retracted Object" );
        } catch ( final IllegalAccessException e ) {
            throw new RuntimeDroolsException( "Warning: The removePropertyChangeListener method on the class " + object.getClass() + " is not public so Drools will be unable to stop processing JavaBean PropertyChangeEvents on the retracted Object" );
        } catch ( final InvocationTargetException e ) {
            throw new RuntimeDroolsException( "Warning: The removePropertyChangeL istener method on the class " + object.getClass() + " threw an InvocationTargetException so Drools will be unable to stop processing JavaBean"
                                              + " PropertyChangeEvents on the retracted Object: " + e.getMessage() );
        } catch ( final SecurityException e ) {
            throw new RuntimeDroolsException( "Warning: The SecurityManager controlling the class " + object.getClass() + " did not allow the lookup of a removePropertyChangeListener method so Drools will be unable to stop processing JavaBean"
                                              + " PropertyChangeEvents on the retracted Object: " + e.getMessage() );
        }
    }

    public void retract(final FactHandle handle) throws FactException {
        retract( handle,
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
    public void retract(final FactHandle factHandle,
                        final boolean removeLogical,
                        final boolean updateEqualsMap,
                        final Rule rule,
                        final Activation activation) throws FactException {
        this.retract( EntryPoint.DEFAULT,
                      factHandle,
                      removeLogical,
                      updateEqualsMap,
                      rule,
                      activation );

    }

    protected void retract(final EntryPoint entryPoint,
                           final FactHandle factHandle,
                           final boolean removeLogical,
                           final boolean updateEqualsMap,
                           final Rule rule,
                           final Activation activation) throws FactException {
        try {
            this.lock.lock();
            this.ruleBase.executeQueuedActions();

            final InternalFactHandle handle = (InternalFactHandle) factHandle;
            if ( handle.getId() == -1 ) {
                // can't retract an already retracted handle
                return;
            }
            removePropertyChangeListener( handle );

            if ( activation != null ) {
                // release resources so that they can be GC'ed
                activation.getPropagationContext().releaseResources();
            }
            final PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                      PropagationContext.RETRACTION,
                                                                                      rule,
                                                                                      activation,
                                                                                      this.agenda.getActiveActivations(),
                                                                                      this.agenda.getDormantActivations(),
                                                                                      entryPoint );

            doRetract( handle,
                       propagationContext );

            if ( this.maintainTms ) {
                // Update the equality key, which maintains a list of stated
                // FactHandles
                final EqualityKey key = handle.getEqualityKey();

                // Its justified so attempt to remove any logical dependencies
                // for
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
                                                                object,
                                                                this );

            this.objectStore.removeHandle( handle );

            this.handleFactory.destroyFactHandle( handle );

            if ( !this.actionQueue.isEmpty() ) {
                executeQueuedActions();
            }
        } finally {
            this.lock.unlock();
        }
    }

    //    private void addHandleToMaps(InternalFactHandle handle) {
    //        this.assertMap.put( handle,
    //                            handle,
    //                            false );
    //        if ( this.ruleBase.getConfiguration().getAssertBehaviour() == AssertBehaviour.EQUALITY ) {
    //            this.identityMap.put( handle,
    //                                  handle,
    //                                  false );
    //        }
    //    }
    //
    //    private void removeHandleFromMaps(final InternalFactHandle handle) {
    //        this.assertMap.remove( handle );
    //        if ( this.ruleBase.getConfiguration().getAssertBehaviour() == AssertBehaviour.EQUALITY ) {
    //            this.identityMap.remove( handle );
    //        }
    //    }

    public void modifyRetract(final FactHandle factHandle) {
        modifyRetract( factHandle,
                       null,
                       null );
    }

    public void modifyRetract(final FactHandle factHandle,
                              final Rule rule,
                              final Activation activation) {
        this.modifyRetract( EntryPoint.DEFAULT,
                            factHandle,
                            rule,
                            activation );
    }

    protected void modifyRetract(final EntryPoint entryPoint,
                                 final FactHandle factHandle,
                                 final Rule rule,
                                 final Activation activation) {
        try {
            this.lock.lock();
            this.ruleBase.executeQueuedActions();

            // only needed if we maintain tms, but either way we must get it
            // before we do the retract
            int status = -1;
            if ( this.maintainTms ) {
                status = ((InternalFactHandle) factHandle).getEqualityKey().getStatus();
            }
            final InternalFactHandle handle = (InternalFactHandle) factHandle;
            // final Object originalObject = (handle.isShadowFact()) ?
            // ((ShadowProxy) handle.getObject()).getShadowedObject() :
            // handle.getObject();

            if ( handle.getId() == -1 ) {
                // the handle is invalid, most likely already retracted, so
                // return
                return;
            }

            if ( activation != null ) {
                // release resources so that they can be GC'ed
                activation.getPropagationContext().releaseResources();
            }
            // Nowretract any trace of the original fact
            final PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                      PropagationContext.MODIFICATION,
                                                                                      rule,
                                                                                      activation,
                                                                                      this.agenda.getActiveActivations(),
                                                                                      this.agenda.getDormantActivations(),
                                                                                      entryPoint );
            doRetract( handle,
                       propagationContext );

            if ( this.maintainTms ) {

                // the hashCode and equality has changed, so we must update the
                // EqualityKey
                EqualityKey key = handle.getEqualityKey();
                key.removeFactHandle( handle );

                // If the equality key is now empty, then remove it
                if ( key.isEmpty() ) {
                    this.tms.remove( key );
                }
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void modifyInsert(final FactHandle factHandle,
                             final Object object) {
        modifyInsert( factHandle,
                      object,
                      null,
                      null );
    }

    public void modifyInsert(final FactHandle factHandle,
                             final Object object,
                             final Rule rule,
                             final Activation activation) {
        this.modifyInsert( EntryPoint.DEFAULT,
                           factHandle,
                           object,
                           rule,
                           activation );
    }

    protected void modifyInsert(final EntryPoint entryPoint,
                                final FactHandle factHandle,
                                final Object object,
                                final Rule rule,
                                final Activation activation) {
        try {
            this.lock.lock();
            this.ruleBase.executeQueuedActions();

            final InternalFactHandle handle = (InternalFactHandle) factHandle;
            final Object originalObject = (handle.isShadowFact()) ? ((ShadowProxy) handle.getObject()).getShadowedObject() : handle.getObject();

            if ( this.maintainTms ) {
                EqualityKey key = handle.getEqualityKey();

                // now use an existing EqualityKey, if it exists, else create a
                // new one
                key = this.tms.get( object );
                if ( key == null ) {
                    key = new EqualityKey( handle,
                                           0 );
                    this.tms.put( key );
                } else {
                    key.addFactHandle( handle );
                }

                handle.setEqualityKey( key );
            }

            this.handleFactory.increaseFactHandleRecency( handle );

            if ( activation != null ) {
                // release resources so that they can be GC'ed
                activation.getPropagationContext().releaseResources();
            }
            // Nowretract any trace of the original fact
            final PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                      PropagationContext.MODIFICATION,
                                                                                      rule,
                                                                                      activation,
                                                                                      this.agenda.getActiveActivations(),
                                                                                      this.agenda.getDormantActivations(),
                                                                                      entryPoint );

            doInsert( handle,
                      object,
                      propagationContext );

            this.workingMemoryEventSupport.fireObjectUpdated( propagationContext,
                                                              factHandle,
                                                              originalObject,
                                                              object,
                                                              this );

            propagationContext.clearRetractedTuples();

            if ( !this.actionQueue.isEmpty() ) {
                executeQueuedActions();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void update(final FactHandle handle,
                       final Object object) throws FactException {
        update( handle,
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
    public void update(final FactHandle factHandle,
                       final Object object,
                       final Rule rule,
                       final Activation activation) throws FactException {
        this.update( EntryPoint.DEFAULT,
                     factHandle,
                     object,
                     rule,
                     activation );

    }

    protected void update(final EntryPoint entryPoint,
                          final FactHandle factHandle,
                          final Object object,
                          final Rule rule,
                          final Activation activation) throws FactException {
        try {
            this.lock.lock();
            this.ruleBase.executeQueuedActions();

            // only needed if we maintain tms, but either way we must get it
            // before we do the retract
            int status = -1;
            if ( this.maintainTms ) {
                status = ((InternalFactHandle) factHandle).getEqualityKey().getStatus();
            }
            final InternalFactHandle handle = (InternalFactHandle) factHandle;
            final Object originalObject = (handle.isShadowFact()) ? ((ShadowProxy) handle.getObject()).getShadowedObject() : handle.getObject();

            if ( handle.getId() == -1 || object == null ) {
                // the handle is invalid, most likely already retracted, so
                // return
                // and we cannot assert a null object
                return;
            }

            if ( activation != null ) {
                // release resources so that they can be GC'ed
                activation.getPropagationContext().releaseResources();
            }
            // Nowretract any trace of the original fact
            final PropagationContext propagationContext = new PropagationContextImpl( this.propagationIdCounter++,
                                                                                      PropagationContext.MODIFICATION,
                                                                                      rule,
                                                                                      activation,
                                                                                      this.agenda.getActiveActivations(),
                                                                                      this.agenda.getDormantActivations(),
                                                                                      entryPoint );
            doRetract( handle,
                       propagationContext );

            if ( (originalObject != object) || (this.ruleBase.getConfiguration().getAssertBehaviour() != AssertBehaviour.IDENTITY) ) {
                this.objectStore.removeHandle( handle );

                // set anyway, so that it updates the hashCodes
                handle.setObject( object );
                this.objectStore.addHandle( handle,
                                            object );
            }

            if ( this.maintainTms ) {

                // the hashCode and equality has changed, so we must update the
                // EqualityKey
                EqualityKey key = handle.getEqualityKey();
                key.removeFactHandle( handle );

                // If the equality key is now empty, then remove it
                if ( key.isEmpty() ) {
                    this.tms.remove( key );
                }

                // now use an existing EqualityKey, if it exists, else create a
                // new one
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

            doInsert( handle,
                      object,
                      propagationContext );

            this.workingMemoryEventSupport.fireObjectUpdated( propagationContext,
                                                              factHandle,
                                                              originalObject,
                                                              object,
                                                              this );

            propagationContext.clearRetractedTuples();

            if ( !this.actionQueue.isEmpty() ) {
                executeQueuedActions();
            }
        } finally {
            this.lock.unlock();
        }
    }

    public void executeQueuedActions() {
        while ( !actionQueue.isEmpty() ) {
            final WorkingMemoryAction action = (WorkingMemoryAction) actionQueue.get( 0 );
            actionQueue.remove( 0 );
            action.execute( this );
        }
        // for ( final Iterator it = this.actionQueue.iterator(); it.hasNext();
        // ) {
        // final WorkingMemoryAction action = (WorkingMemoryAction) it.next();
        // it.remove();
        // action.execute( this );
        // }
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
        return this.nodeMemories.getNodeMemory( node );
    }

    public void clearNodeMemory(final NodeMemory node) {
        this.nodeMemories.clearNodeMemory( node );
    }

    public WorkingMemoryEventSupport getWorkingMemoryEventSupport() {
        return this.workingMemoryEventSupport;
    }

    public AgendaEventSupport getAgendaEventSupport() {
        return this.agendaEventSupport;
    }

    public RuleFlowEventSupport getRuleFlowEventSupport() {
        return this.workflowEventSupport;
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
            FactHandle handle = getFactHandle( object );
            if ( handle == null ) {
                throw new FactException( "Update error: handle not found for object: " + object + ". Is it in the working memory?" );
            }
            update( handle,
                    object );
        } catch ( final FactException e ) {
            throw new RuntimeDroolsException( e.getMessage() );
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

    public ProcessInstance startProcess(final String processId) {
        return startProcess( processId,
                             null );
    }

    public ProcessInstance startProcess(String processId,
                                        Map<String, Object> parameters) {
        final Process process = ((InternalRuleBase) getRuleBase()).getProcess( processId );
        if ( process == null ) {
            throw new IllegalArgumentException( "Unknown process ID: " + processId );
        }
        ProcessInstanceFactory factory = processInstanceFactories.get( process.getType() );
        if ( factory == null ) {
            throw new IllegalArgumentException( "Could not create process instance for type: " + process.getType() );
        }
        ProcessInstance processInstance = factory.createProcessInstance();
        processInstance.setWorkingMemory( this );
        processInstance.setProcess( process );
        processInstance.setId( ++processCounter );
        processInstances.put( new Long( processInstance.getId() ),
                              processInstance );
        if ( parameters != null ) {
            for ( Map.Entry<String, Object> entry : parameters.entrySet() ) {
                processInstance.setVariable( entry.getKey(),
                                             entry.getValue() );
            }
        }
        getRuleFlowEventSupport().fireBeforeRuleFlowProcessStarted( processInstance,
                                                                    this );
        processInstance.start();
        getRuleFlowEventSupport().fireAfterRuleFlowProcessStarted( processInstance,
                                                                   this );

        return processInstance;
    }

    public Collection getProcessInstances() {
        return Collections.unmodifiableCollection( processInstances.values() );
    }

    public ProcessInstance getProcessInstance(long id) {
        return (ProcessInstance) processInstances.get( new Long( id ) );
    }

    public void removeProcessInstance(ProcessInstance processInstance) {
        processInstances.remove( processInstance );
    }

    public void registerProcessInstanceFactory(String type,
                                               ProcessInstanceFactory processInstanceFactory) {
        processInstanceFactories.put( type,
                                      processInstanceFactory );
    }

    public WorkItemManager getWorkItemManager() {
        return workItemManager;
    }

    public List iterateObjectsToList() {
        List result = new ArrayList();
        Iterator iterator = iterateObjects();
        for ( ; iterator.hasNext(); ) {
            result.add( iterator.next() );
        }
        return result;
    }

    public Entry[] getActivationParameters(long activationId) {
        Activation[] activations = getAgenda().getActivations();
        for ( int i = 0; i < activations.length; i++ ) {
            if ( activations[i].getActivationNumber() == activationId ) {
                Map params = getActivationParameters( activations[i] );
                return (Entry[]) params.entrySet().toArray( new Entry[params.size()] );
            }
        }
        return new Entry[0];
    }

    /**
     * Helper method
     */
    public Map getActivationParameters(Activation activation) {
        Map result = new HashMap();
        Declaration[] declarations = activation.getRule().getDeclarations();
        for ( int i = 0; i < declarations.length; i++ ) {
            FactHandle handle = activation.getTuple().get( declarations[i] );
            if ( handle instanceof InternalFactHandle ) {
                result.put( declarations[i].getIdentifier(),
                            declarations[i].getValue( this,
                                                      ((InternalFactHandle) handle).getObject() ) );
            }
        }
        return result;
    }

    /**
     * The time machine tells you what time it is.
     */
    public TimeMachine getTimeMachine() {
        return timeMachine;
    }

    /**
     * The time machine defaults to returning the current time when asked.
     * However, you can use tell it to go back in time.
     * 
     * @param timeMachine
     */
    public void setTimeMachine(TimeMachine timeMachine) {
        this.timeMachine = timeMachine;
    }

    /**
     * Returns the ObjectTypeConfiguration object for the given object or
     * creates a new one if none is found in the cache
     * 
     * @param object
     * @return
     */
    public ObjectTypeConf getObjectTypeConf(EntryPoint entrypoint,
                                            Object object) {
        Map<Object, ObjectTypeConf> map = this.typeConfMap.get( entrypoint );
        if ( map == null ) {
            map = new ConcurrentHashMap<Object, ObjectTypeConf>();
            this.typeConfMap.put( entrypoint,
                                  map );
        }
        ObjectTypeConf objectTypeConf;

        if ( object instanceof Fact ) {
            String key = ((Fact) object).getFactTemplate().getName();
            objectTypeConf = map.get( key );
            if ( objectTypeConf == null ) {
                objectTypeConf = new FactTemplateTypeConf( entrypoint,
                                                           ((Fact) object).getFactTemplate(),
                                                           this.ruleBase );
                this.addObjectTypeConf( entrypoint,
                                        key,
                                        objectTypeConf );
            }
            object = key;
        } else {
            Class cls = null;
            if ( object instanceof ShadowProxy ) {
                cls = ((ShadowProxy) object).getShadowedObject().getClass();
            } else {
                cls = object.getClass();
            }

            objectTypeConf = map.get( cls );
            if ( objectTypeConf == null ) {

                final boolean isEvent = this.ruleBase.isEvent( cls );
                objectTypeConf = new ClassObjectTypeConf( entrypoint,
                                                          cls,
                                                          isEvent,
                                                          this.ruleBase );
                this.addObjectTypeConf( entrypoint,
                                        cls,
                                        objectTypeConf );
            }

        }
        return objectTypeConf;
    }

    public Map<Object, ObjectTypeConf> getObjectTypeConfMap(EntryPoint entryPoint) {
        Map<Object, ObjectTypeConf> map = this.typeConfMap.get( entryPoint );
        if ( map == null ) {
            map = Collections.emptyMap();
        }
        return map;
    }

    private void addObjectTypeConf(EntryPoint entryPoint,
                                   Object key,
                                   ObjectTypeConf conf) {
        Map<Object, ObjectTypeConf> map = this.typeConfMap.get( entryPoint );
        if ( map == null ) {
            map = new ConcurrentHashMap<Object, ObjectTypeConf>();
            this.typeConfMap.put( entryPoint,
                                  map );
        }
        map.put( key,
                 conf );
    }

    public EntryPointInterface getEntryPoint(String id) {
        EntryPoint ep = new EntryPoint( id );
        return new EntryPointInterfaceImpl( ep,
                                            this );
    }

    protected static class EntryPointInterfaceImpl
        implements
        EntryPointInterface {

        private static final long           serialVersionUID = 2917871170743358801L;

        private final EntryPoint            entryPoint;
        private final AbstractWorkingMemory wm;

        public EntryPointInterfaceImpl(EntryPoint entryPoint,
                                       AbstractWorkingMemory wm) {
            this.entryPoint = entryPoint;
            this.wm = wm;
        }

        public FactHandle insert(Object object) throws FactException {
            return wm.insert( this.entryPoint,
                              object, /* Not-Dynamic */
                              false,
                              false,
                              null,
                              null );
        }

        public FactHandle insert(Object object,
                                 boolean dynamic) throws FactException {
            return wm.insert( this.entryPoint,
                              object, /* Not-Dynamic */
                              dynamic,
                              false,
                              null,
                              null );
        }

        public void modifyInsert(FactHandle factHandle,
                                 Object object) {
            wm.modifyInsert( this.entryPoint,
                             factHandle,
                             object,
                             null,
                             null );
        }

        public void modifyRetract(FactHandle factHandle) {
            wm.modifyRetract( this.entryPoint,
                              factHandle,
                              null,
                              null );
        }

        public void retract(FactHandle handle) throws FactException {
            wm.retract( this.entryPoint,
                        handle,
                        true,
                        true,
                        null,
                        null );
        }

        public void update(FactHandle handle,
                           Object object) throws FactException {
            wm.update( this.entryPoint,
                       handle,
                       object,
                       null,
                       null );
        }

    }

}
