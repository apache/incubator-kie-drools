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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.Agenda;
import org.drools.FactException;
import org.drools.FactHandle;
import org.drools.QueryResults;
import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuntimeDroolsException;
import org.drools.SessionConfiguration;
import org.drools.WorkingMemory;
import org.drools.WorkingMemoryEntryPoint;
import org.drools.RuleBaseConfiguration.AssertBehaviour;
import org.drools.RuleBaseConfiguration.LogicalOverride;
import org.drools.base.MapGlobalResolver;
import org.drools.concurrent.ExecutorService;
import org.drools.definition.process.Process;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.RuleFlowEventSupport;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.process.core.ContextContainer;
import org.drools.process.core.context.variable.VariableScope;
import org.drools.process.core.event.EventFilter;
import org.drools.process.core.event.EventTypeFilter;
import org.drools.process.instance.ProcessInstance;
import org.drools.process.instance.ProcessInstanceFactory;
import org.drools.process.instance.ProcessInstanceFactoryRegistry;
import org.drools.process.instance.ProcessInstanceManager;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.context.variable.VariableScopeInstance;
import org.drools.process.instance.event.SignalManager;
import org.drools.process.instance.timer.TimerManager;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.InitialFactHandle;
import org.drools.reteoo.InitialFactHandleDummyObject;
import org.drools.reteoo.LIANodePropagation;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.PartitionTaskManager;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.rule.TimeMachine;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.Globals;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.impl.BatchExecutionResultImpl;
import org.drools.runtime.process.EventListener;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.GlobalResolver;
import org.drools.spi.PropagationContext;
import org.drools.time.SessionClock;
import org.drools.time.TimerService;
import org.drools.time.TimerServiceFactory;
import org.drools.workflow.core.node.EventTrigger;
import org.drools.workflow.core.node.StartNode;
import org.drools.workflow.core.node.Trigger;

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
    protected static final Class[]                                            ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[]{PropertyChangeListener.class};
    private static final int                                                  NODE_MEMORIES_ARRAY_GROWTH                    = 32;

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------
    protected int                                                            id;

    /** The arguments used when adding/removing a property change listener. */
    protected Object[]                                                        addRemovePropertyChangeListenerArgs;

    /** The actual memory for the <code>JoinNode</code>s. */
    protected NodeMemories                                                    nodeMemories;

    protected ObjectStore                                                     objectStore;

    protected Map                                                             queryResults;

    /** Global values which are associated with this memory. */
    protected GlobalResolver                                                  globalResolver;

    /** The eventSupport */
    protected WorkingMemoryEventSupport                                       workingMemoryEventSupport;

    protected AgendaEventSupport                                              agendaEventSupport;

    protected RuleFlowEventSupport                                            workflowEventSupport;

    protected List                                                            __ruleBaseEventListeners;

    /** The <code>RuleBase</code> with which this memory is associated. */
    protected transient InternalRuleBase                                      ruleBase;

    protected FactHandleFactory                                               handleFactory;

    protected TruthMaintenanceSystem                                          tms;

    /** Rule-firing agenda. */
    protected InternalAgenda                                                  agenda;

    protected Queue<WorkingMemoryAction>                                      actionQueue;

    protected volatile boolean                                                evaluatingActionQueue;

    protected ReentrantLock                                                   lock;

    protected boolean                                                         discardOnLogicalOverride;

    /**
     * This must be thread safe as it is incremented and read via different
     * EntryPoints
     */
    protected AtomicLong                                                      propagationIdCounter;

    private boolean                                                           maintainTms;
    private boolean                                                           sequential;

    private List                                                              liaPropagations;

    /** Flag to determine if a rule is currently being fired. */
    protected volatile AtomicBoolean                                          firing;

    private ProcessInstanceManager                                            processInstanceManager;

    private WorkItemManager                                                   workItemManager;

    private TimerManager                                                      timerManager;

    private SignalManager                                                     signalManager;

    private TimeMachine                                                       timeMachine;

    protected transient ObjectTypeConfigurationRegistry                       typeConfReg;

    protected EntryPoint                                                      entryPoint;
    protected transient EntryPointNode                                        entryPointNode;

    protected Map<String, WorkingMemoryEntryPoint>                            entryPoints;

    protected InternalFactHandle                                              initialFactHandle;

    protected SessionConfiguration                                            config;

    protected Map<RuleBasePartitionId, PartitionTaskManager>                  partitionManagers;

    protected transient AtomicReference<java.util.concurrent.ExecutorService> threadPool                                    = new AtomicReference<java.util.concurrent.ExecutorService>();

    private Map<InternalFactHandle, PropagationContext>                       modifyContexts;

    private KnowledgeRuntime                                                  kruntime;

    private Map<String, ExitPoint>                                            exitPoints;

    private Environment                                                       environment;
    
    private ExecutionResults                                              batchExecutionResult;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public AbstractWorkingMemory() {

    }

    /**
     * Construct.
     * 
     * @param ruleBase
     *            The backing rule-base.
     */
    public AbstractWorkingMemory(final int id,
                                 final InternalRuleBase ruleBase,
                                 final FactHandleFactory handleFactory,
                                 final SessionConfiguration config,
                                 final Environment environment) {
        this( id,
              ruleBase,
              handleFactory,
              null,
              0,
              config,
              environment );
    }

    public AbstractWorkingMemory(final int id,
                                 final InternalRuleBase ruleBase,
                                 final FactHandleFactory handleFactory,
                                 final InitialFactHandle initialFactHandle,
                                 final long propagationContext,
                                 final SessionConfiguration config,
                                 final Environment environment) {
        this.id = id;
        this.config = config;
        this.ruleBase = ruleBase;
        this.handleFactory = handleFactory;
        this.environment = environment;
        
        Globals globals =  ( Globals ) this.environment.get( EnvironmentName.GLOBALS );
        if ( globals != null ) {
            if ( !(globals instanceof GlobalResolver )) {
                this.globalResolver = new GlobalsAdapter( globals );   
            } else {
                this.globalResolver = ( GlobalResolver ) globals;
            }
        } else {
            this.globalResolver = new MapGlobalResolver();
        }

        final RuleBaseConfiguration conf = this.ruleBase.getConfiguration();

        this.maintainTms = conf.isMaintainTms();
        this.sequential = conf.isSequential();

        if ( initialFactHandle == null ) {
            this.initialFactHandle = new InitialFactHandle( handleFactory.newFactHandle( new InitialFactHandleDummyObject(),
                                                                                         null,
                                                                                         this ) );
        } else {
            this.initialFactHandle = initialFactHandle;
        }

        this.actionQueue = new LinkedList<WorkingMemoryAction>();

        this.addRemovePropertyChangeListenerArgs = new Object[]{this};
        this.queryResults = Collections.EMPTY_MAP;
        this.workingMemoryEventSupport = new WorkingMemoryEventSupport();
        this.agendaEventSupport = new AgendaEventSupport();
        this.workflowEventSupport = new RuleFlowEventSupport();
        this.__ruleBaseEventListeners = new LinkedList();
        this.lock = new ReentrantLock();
        this.liaPropagations = Collections.EMPTY_LIST;
        this.processInstanceManager = config.getProcessInstanceManagerFactory().createProcessInstanceManager( this );
        this.timeMachine = new TimeMachine();

        TimerService timerService = TimerServiceFactory.getTimerService( this.config.getClockType() );
        this.timerManager = new TimerManager( this,
                                              timerService );
        this.signalManager = config.getSignalManagerFactory().createSignalManager( this );

        this.nodeMemories = new ConcurrentNodeMemories( this.ruleBase );

        if ( this.maintainTms ) {
            this.tms = new TruthMaintenanceSystem( this );
        } else {
            this.tms = null;
        }

        this.propagationIdCounter = new AtomicLong( propagationContext );

        this.objectStore = new SingleThreadedObjectStore( conf,
                                                          this.lock );

        // Only takes effect if are using idententity behaviour for assert
        if ( LogicalOverride.DISCARD.equals( conf.getLogicalOverride() ) ) {
            this.discardOnLogicalOverride = true;
        } else {
            this.discardOnLogicalOverride = false;
        }

        initEntryPointsMap();
        this.firing = new AtomicBoolean( false );
        this.modifyContexts = new HashMap<InternalFactHandle, PropagationContext>();
        this.exitPoints = new ConcurrentHashMap<String, ExitPoint>();
        //setGlobal( "exitPoints", Collections.unmodifiableMap( this.exitPoints ) );
        initProcessEventListeners();
        initPartitionManagers();
        initTransient();
    }
    
    public static class GlobalsAdapter implements GlobalResolver {
        private Globals globals;
        
        public GlobalsAdapter(Globals globals) {
            this.globals = globals;
        }
        
        
        public Object resolveGlobal(String identifier) {
            return this.globals.get( identifier );
        }

        public void setGlobal(String identifier,
                              Object value) {
            this.globals.set( identifier, value );
        }
        
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    public void setRuleBase(final InternalRuleBase ruleBase) {
        this.ruleBase = ruleBase;
        this.nodeMemories.setRuleBaseReference( this.ruleBase );
        initTransient();
    }

    private void initEntryPointsMap() {
        this.entryPoints = new ConcurrentHashMap<String, WorkingMemoryEntryPoint>();
        this.entryPoints.put( "DEFAULT",
                              this );
        this.entryPoint = EntryPoint.DEFAULT;

        for ( EntryPointNode entryPointNode : this.ruleBase.getRete().getEntryPointNodes().values() ) {
            EntryPoint id = entryPointNode.getEntryPoint();
            if( !  EntryPoint.DEFAULT.equals( id ) ) {
                WorkingMemoryEntryPoint wmEntryPoint = new NamedEntryPoint( id,
                                                                            entryPointNode,
                                                                            this );
                this.entryPoints.put( entryPointNode.getEntryPoint().getEntryPointId(),
                                      wmEntryPoint );
            }
        }
    }

    /**
     * Creates the actual partition managers and their tasks for multi-thread
     * processing
     */
    private void initPartitionManagers() {
        if ( this.ruleBase.getConfiguration().isMultithreadEvaluation() ) {

            // the Map MUST be thread safe
            this.partitionManagers = new ConcurrentHashMap<RuleBasePartitionId, PartitionTaskManager>();

            for ( RuleBasePartitionId partitionId : this.ruleBase.getPartitionIds() ) {
                this.partitionManagers.put( partitionId,
                                            new PartitionTaskManager( this ) );
            }
        }
    }

    /**
     * This method is called to start the multiple partition threads when
     * running in multi-thread mode
     */
    public void startPartitionManagers() {
        if ( this.ruleBase.getConfiguration().isMultithreadEvaluation() ) {
            int maxThreads = (this.ruleBase.getConfiguration().getMaxThreads() > 0) ? this.ruleBase.getConfiguration().getMaxThreads() : this.ruleBase.getPartitionIds().size();
            if ( this.threadPool.compareAndSet( null,
                                                Executors.newFixedThreadPool( maxThreads ) ) ) {
                for ( PartitionTaskManager task : this.partitionManagers.values() ) {
                    task.setPool( this.threadPool.get() );
                }
            }
        }
    }

    public void stopPartitionManagers() {
        if ( this.ruleBase.getConfiguration().isMultithreadEvaluation() ) {
            java.util.concurrent.ExecutorService service = this.threadPool.get();
            if ( this.threadPool.compareAndSet( service,
                                                null ) ) {
                service.shutdown();
                for ( PartitionTaskManager task : this.partitionManagers.values() ) {
                    task.setPool( null );
                }
            }
        }
    }

    public boolean isPartitionManagersActive() {
        return this.threadPool.get() != null;
    }

    private void initTransient() {
        this.entryPointNode = this.ruleBase.getRete().getEntryPointNode( this.entryPoint );
        this.typeConfReg = new ObjectTypeConfigurationRegistry( this.ruleBase );
    }

    public SessionConfiguration getSessionConfiguration() {
        return this.config;
    }

    public void reset(int handleId,
                      long handleCounter,
                      long propagationCounter) {
        this.nodeMemories.clear();
        this.agenda.clear();
        this.objectStore.clear();
        this.handleFactory.clear( handleId,
                                  handleCounter );
        this.tms.clear();
        this.liaPropagations.clear();
        this.actionQueue.clear();

        this.propagationIdCounter = new AtomicLong( propagationCounter );

        // TODO should these be cleared?
        // we probably neeed to do CEP and Flow timers too
        // this.processInstanceManager.clear()
        // this.workItemManager.clear();
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

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
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

    public Environment getEnvironment() {
        return this.environment;
    }

    public Agenda getAgenda() {
        return this.agenda;
    }

    public void clearAgenda() {
        this.agenda.clearAndCancel();
    }

    public void clearAgendaGroup(final String group) {
        this.agenda.clearAndCancelAgendaGroup( group );
    }

    public void clearActivationGroup(final String group) {
        this.agenda.clearAndCancelActivationGroup( group );
    }

    public void clearRuleFlowGroup(final String group) {
        this.agenda.clearAndCancelRuleFlowGroup( group );
    }

    public RuleBase getRuleBase() {
        return this.ruleBase;
    }

    public void halt() {
        this.agenda.halt();
    }

    // /**
    // * This is a synchronous call that will keep the engine running
    // * until halt() is called. If no more activations exist, the engine
    // * will wait until either halt is called or new activations are
    // * created. In the later case, it will fire them.
    // */
    // public void runUntilHalt() {
    // do {
    // fireAllRules();
    // synchronized( this.agenda ) {
    // if( !halt && this.agenda.agendaSize() == 0 ) {
    // try {
    // this.agenda.wait();
    // } catch (InterruptedException e) {
    // // set status and continue
    // Thread.currentThread().interrupted();
    // break;
    // }
    // }
    // }
    // } while( !halt );
    //    	
    // }

    public synchronized int fireAllRules() throws FactException {
        return fireAllRules( null,
                             -1 );
    }

    public synchronized int fireAllRules(int fireLimit) throws FactException {
        return fireAllRules( null,
                             fireLimit );
    }

    public synchronized int fireAllRules(final AgendaFilter agendaFilter) throws FactException {
        return fireAllRules( agendaFilter,
                             -1 );
    }

    public synchronized int fireAllRules(final AgendaFilter agendaFilter,
                                         int fireLimit) throws FactException {
        // If we're already firing a rule, then it'll pick up
        // the firing for any other assertObject(..) that get
        // nested inside, avoiding concurrent-modification
        // exceptions, depending on code paths of the actions.
        if ( isSequential() ) {
            for ( Iterator it = this.liaPropagations.iterator(); it.hasNext(); ) {
                ((LIANodePropagation) it.next()).doPropagation( this );
            }
        }

        // do we need to call this in advance?
        executeQueuedActions();

        int fireCount = 0;
        if ( this.firing.compareAndSet( false,
                                        true ) ) {
            try {
                fireCount = this.agenda.fireAllRules( agendaFilter,
                                                      fireLimit );
            } finally {
                this.firing.set( false );
            }
        }
        return fireCount;
    }

    /**
     * Keeps firing activations until a halt is called. If in a given moment,
     * there is no activation to fire, it will wait for an activation to be
     * added to an active agenda group or rule flow group.
     * 
     * @throws IllegalStateException
     *             if this method is called when running in sequential mode
     */
    public synchronized void fireUntilHalt() {
        fireUntilHalt( null );
    }

    /**
     * Keeps firing activations until a halt is called. If in a given moment,
     * there is no activation to fire, it will wait for an activation to be
     * added to an active agenda group or rule flow group.
     * 
     * @param agendaFilter
     *            filters the activations that may fire
     * 
     * @throws IllegalStateException
     *             if this method is called when running in sequential mode
     */
    public synchronized void fireUntilHalt(final AgendaFilter agendaFilter) {
        if ( isSequential() ) {
            throw new IllegalStateException( "fireUntilHalt() can not be called in sequential mode." );
        }

        executeQueuedActions();
        try {
            if ( this.firing.compareAndSet( false,
                                            true ) ) {
                this.agenda.fireUntilHalt( agendaFilter );
            }
        } finally {
            this.firing.set( false );
        }
    }

    /**
     * Returns the fact Object for the given <code>FactHandle</code>. It
     * actually attempts to return the value from the handle, before retrieving
     * it from objects map.
     * 
     * @see WorkingMemory
     * 
     * @param handle
     *            The <code>FactHandle</code> reference for the
     *            <code>Object</code> lookup
     * 
     */
    public Object getObject(final org.drools.runtime.rule.FactHandle handle) {
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
    public Iterator iterateObjects(org.drools.runtime.ObjectFilter filter) {
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
    public Iterator iterateFactHandles(org.drools.runtime.ObjectFilter filter) {
        return this.objectStore.iterateFactHandles( filter );
    }

    public abstract QueryResults getQueryResults(String query);

    public void setFocus(final String focus) {
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
                       false,
                       false,
                       null,
                       null );
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle insertLogical(final Object object) throws FactException {
        return insert( object, // Not-Dynamic
                       false,
                       true,
                       null,
                       null );
    }

    public FactHandle insert(final Object object,
                             final boolean dynamic) throws FactException {
        return insert( object,
                       dynamic,
                       false,
                       null,
                       null );
    }

    public FactHandle insertLogical(final Object object,
                                    final boolean dynamic) throws FactException {
        return insert( object,
                       dynamic,
                       true,
                       null,
                       null );
    }

    // protected FactHandle insert(final EntryPoint entryPoint,
    // final Object object,
    // final boolean dynamic,
    // boolean logical,
    // final Rule rule,
    // final Activation activation) throws FactException {
    // return this.insert( entryPoint,
    // object,
    // 0,
    // dynamic,
    // logical,
    // rule,
    // activation );
    // }

    public FactHandle insert(final Object object,
                             final boolean dynamic,
                             boolean logical,
                             final Rule rule,
                             final Activation activation) throws FactException {
        if ( object == null ) {
            // you cannot assert a null object
            return null;
        }

        ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                                                      object );

        InternalFactHandle handle = null;

        if ( isSequential() ) {
            handle = createHandle( object,
                                   typeConf );
            insert( handle,
                    object,
                    rule,
                    activation,
                    typeConf );
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
                    handle = createHandle( object,
                                           typeConf );

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

                            if ( AssertBehaviour.IDENTITY.equals( this.ruleBase.getConfiguration().getAssertBehaviour() ) ) {
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
                            }
                            return handle;
                        } else {
                            // override, then instantiate new handle for
                            // assertion
                            key.setStatus( EqualityKey.STATED );
                            handle = createHandle( object,
                                                   typeConf );
                            handle.setEqualityKey( key );
                            key.addFactHandle( handle );
                        }

                    } else {
                        handle = createHandle( object,
                                               typeConf );
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
                handle = createHandle( object,
                                       typeConf );

            }

            // if the dynamic parameter is true or if the
            // user declared the fact type with the meta tag:
            // @propertyChangeSupport
            if ( dynamic || typeConf.isDynamic() ) {
                addPropertyChangeListener( object );
            }

            insert( handle,
                    object,
                    rule,
                    activation,
                    typeConf );

        } finally {
            this.lock.unlock();
        }
        return handle;
    }

    private InternalFactHandle createHandle(final Object object,
                                            ObjectTypeConf typeConf) {
        InternalFactHandle handle;
        handle = this.handleFactory.newFactHandle( object,
                                                   typeConf,
                                                   this );
        handle.setEntryPoint( this );
        this.objectStore.addHandle( handle,
                                    object );
        return handle;
    }

    protected void insert(final InternalFactHandle handle,
                          final Object object,
                          final Rule rule,
                          final Activation activation,
                          ObjectTypeConf typeConf) {
        this.ruleBase.executeQueuedActions();

        if ( activation != null ) {
            // release resources so that they can be GC'ed
            activation.getPropagationContext().releaseResources();
        }
        final PropagationContext propagationContext = new PropagationContextImpl( getNextPropagationIdCounter(),
                                                                                  PropagationContext.ASSERTION,
                                                                                  rule,
                                                                                  (activation == null) ? null : (LeftTuple) activation.getTuple(),
                                                                                  handle,
                                                                                  this.agenda.getActiveActivations(),
                                                                                  this.agenda.getDormantActivations(),
                                                                                  entryPoint );

        this.entryPointNode.assertObject( handle,
                                          propagationContext,
                                          typeConf,
                                          this );

        executeQueuedActions();

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

    protected void removePropertyChangeListener(final FactHandle handle) {
        Object object = null;
        try {
            object = ((InternalFactHandle) handle).getObject();

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

    public void retract(final org.drools.runtime.rule.FactHandle handle) throws FactException {
        retract( (org.drools.FactHandle) handle,
                 true,
                 true,
                 null,
                 null );
    }

    public void retract(final org.drools.FactHandle factHandle,
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
            final PropagationContext propagationContext = new PropagationContextImpl( getNextPropagationIdCounter(),
                                                                                      PropagationContext.RETRACTION,
                                                                                      rule,
                                                                                      (activation == null) ? null : (LeftTuple) activation.getTuple(),
                                                                                      handle,
                                                                                      this.agenda.getActiveActivations(),
                                                                                      this.agenda.getDormantActivations(),
                                                                                      this.entryPoint );

            final Object object = handle.getObject();

            this.entryPointNode.retractObject( handle,
                                               propagationContext,
                                               this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                                                                   object ),
                                               this );

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

            this.workingMemoryEventSupport.fireObjectRetracted( propagationContext,
                                                                handle,
                                                                object,
                                                                this );

            this.objectStore.removeHandle( handle );

            this.handleFactory.destroyFactHandle( handle );

            executeQueuedActions();
        } finally {
            this.lock.unlock();
        }
    }

    public void modifyRetract(final FactHandle factHandle) {
        modifyRetract( factHandle,
                       null,
                       null );
    }

    public void modifyRetract(final FactHandle factHandle,
                              final Rule rule,
                              final Activation activation) {
        try {
            this.lock.lock();
            this.ruleBase.executeQueuedActions();

            final InternalFactHandle handle = (InternalFactHandle) factHandle;

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
            final PropagationContext propagationContext = new PropagationContextImpl( getNextPropagationIdCounter(),
                                                                                      PropagationContext.MODIFICATION,
                                                                                      rule,
                                                                                      (activation == null) ? null : (LeftTuple) activation.getTuple(),
                                                                                      handle,
                                                                                      this.agenda.getActiveActivations(),
                                                                                      this.agenda.getDormantActivations(),
                                                                                      entryPoint );

            modifyContexts.put( handle,
                                propagationContext );

            this.entryPointNode.retractObject( handle,
                                               propagationContext,
                                               this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                                                                   handle.getObject() ),
                                               this );

            if ( this.maintainTms ) {

                // the hashCode and equality has changed, so we must update the
                // EqualityKey
                EqualityKey key = handle.getEqualityKey();
                if(key != null){
                key.removeFactHandle( handle );

                // If the equality key is now empty, then remove it
                if ( key.isEmpty() ) {
                    this.tms.remove( key );
                }
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
        try {
            this.lock.lock();
            this.ruleBase.executeQueuedActions();

            final InternalFactHandle handle = (InternalFactHandle) factHandle;
            final Object originalObject = handle.getObject();

            if ( this.maintainTms ) {
                if(handle.getEqualityKey() != null ){
                int status = handle.getEqualityKey().getStatus();

                // now use an existing EqualityKey, if it exists, else create a
                // new one
                EqualityKey key = this.tms.get( object );
                if ( key == null ) {
                    key = new EqualityKey( handle,
                                           status );
                    this.tms.put( key );
                } else {
                    key.addFactHandle( handle );
                }

                handle.setEqualityKey( key );
            }
            }

            this.handleFactory.increaseFactHandleRecency( handle );

            if ( activation != null ) {
                // release resources so that they can be GC'ed
                activation.getPropagationContext().releaseResources();
            }
            // Now retract any trace of the original fact
            final PropagationContext propagationContext = this.modifyContexts.remove( handle );

            this.entryPointNode.assertObject( handle,
                                              propagationContext,
                                              this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                                                                  object ),
                                              this );

            this.workingMemoryEventSupport.fireObjectUpdated( propagationContext,
                                                              factHandle,
                                                              originalObject,
                                                              object,
                                                              this );

            propagationContext.clearRetractedTuples();

            executeQueuedActions();

        } finally {
            this.lock.unlock();
        }
    }

    public void update(final org.drools.runtime.rule.FactHandle handle,
                       final Object object) throws FactException {
        update( (org.drools.FactHandle) handle,
                object,
                null,
                null );
    }
   public void update(final org.drools.runtime.rule.FactHandle factHandle,
                       final Object object,
                       final Rule rule,
                       final Activation activation) throws FactException {

       update((org.drools.FactHandle)factHandle, object, rule, activation);
   }

    /**
     * modify is implemented as half way retract / assert due to the truth
     * maintenance issues.
     * 
     * @see WorkingMemory
     */
    public void update(final org.drools.FactHandle factHandle,
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
            final Object originalObject = handle.getObject();

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
            final PropagationContext propagationContext = new PropagationContextImpl( getNextPropagationIdCounter(),
                                                                                      PropagationContext.MODIFICATION,
                                                                                      rule,
                                                                                      (activation == null) ? null : (LeftTuple) activation.getTuple(),
                                                                                      handle,
                                                                                      this.agenda.getActiveActivations(),
                                                                                      this.agenda.getDormantActivations(),
                                                                                      entryPoint );

            ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                                                          object );

            this.entryPointNode.retractObject( handle,
                                               propagationContext,
                                               typeConf,
                                               this );

            if ( originalObject != object || !AssertBehaviour.IDENTITY.equals( this.ruleBase.getConfiguration().getAssertBehaviour() ) ) {
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

            this.entryPointNode.assertObject( handle,
                                              propagationContext,
                                              typeConf,
                                              this );

            this.workingMemoryEventSupport.fireObjectUpdated( propagationContext,
                                                              (org.drools.FactHandle) factHandle,
                                                              originalObject,
                                                              object,
                                                              this );

            propagationContext.clearRetractedTuples();

            executeQueuedActions();
        } finally {
            this.lock.unlock();
        }
    }

    public void executeQueuedActions() {
        synchronized ( this.actionQueue ) {
            if ( !this.actionQueue.isEmpty() && !evaluatingActionQueue ) {
                evaluatingActionQueue = true;
                WorkingMemoryAction action = null;

                while ( (action = actionQueue.poll()) != null ) {
                    try {
                        action.execute( this );
                    } catch ( Exception e ) {
                        System.err.println("************************************************");
                        System.err.println("Exception caught while executing action: "+action.toString());
                        e.printStackTrace();
                    }
                }
                evaluatingActionQueue = false;
            }
        }
    }

    public Queue<WorkingMemoryAction> getActionQueue() {
        return this.actionQueue;
    }

    public void queueWorkingMemoryAction(final WorkingMemoryAction action) {
        synchronized ( this.actionQueue ) {
            this.actionQueue.add( action );
            this.agenda.notifyHalt();
        }
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
        return this.propagationIdCounter.incrementAndGet();
    }

    public long getPropagationIdCounter() {
        return this.propagationIdCounter.get();
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
        if ( !this.actionQueue.isEmpty() ) {
            executeQueuedActions();
        }
        final Process process = ((InternalRuleBase) getRuleBase()).getProcess( processId );
        if ( process == null ) {
            throw new IllegalArgumentException( "Unknown process ID: " + processId );
        }
        ProcessInstance processInstance = (ProcessInstance) getProcessInstance( process );
        processInstance.setWorkingMemory( this );
        processInstance.setProcess( process );
        processInstanceManager.addProcessInstance( processInstance );
        // set variable default values
        // TODO: should be part of processInstanceImpl?
        VariableScope variableScope = (VariableScope) ((ContextContainer) process).getDefaultContext( VariableScope.VARIABLE_SCOPE );
        VariableScopeInstance variableScopeInstance = (VariableScopeInstance) processInstance.getContextInstance( VariableScope.VARIABLE_SCOPE );
        // set input parameters
        if ( parameters != null ) {
            if ( variableScope != null ) {
                for ( Map.Entry<String, Object> entry : parameters.entrySet() ) {
                    variableScopeInstance.setVariable( entry.getKey(),
                                                       entry.getValue() );
                }
            } else {
                throw new IllegalArgumentException( "This process does not support parameters!" );
            }
        }
        // start
        getRuleFlowEventSupport().fireBeforeRuleFlowProcessStarted( processInstance,
                                                                    this );
        processInstance.start();
        getRuleFlowEventSupport().fireAfterRuleFlowProcessStarted( processInstance,
                                                                   this );

        return processInstance;
    }

    public ProcessInstance getProcessInstance(final Process process) {
        ProcessInstanceFactoryRegistry processRegistry = ((InternalRuleBase) getRuleBase()).getConfiguration().getProcessInstanceFactoryRegistry();
        ProcessInstanceFactory conf = processRegistry.getProcessInstanceFactory( process );
        if ( conf == null ) {
            throw new IllegalArgumentException( "Illegal process type: " + process.getClass() );
        }
        ProcessInstance processInstance = conf.createProcessInstance();
        if ( processInstance == null ) {
            throw new IllegalArgumentException( "Illegal process type: " + process.getClass() );
        }
        return processInstance;
    }

    public ProcessInstanceManager getProcessInstanceManager() {
        return processInstanceManager;
    }

    public Collection<ProcessInstance> getProcessInstances() {
        return (Collection<ProcessInstance>) processInstanceManager.getProcessInstances();
    }

    public ProcessInstance getProcessInstance(long id) {
        return processInstanceManager.getProcessInstance( id );
    }

    public void removeProcessInstance(ProcessInstance processInstance) {
        processInstanceManager.removeProcessInstance( processInstance );
    }

    public WorkItemManager getWorkItemManager() {
        if ( workItemManager == null ) {
            workItemManager = config.getWorkItemManagerFactory().createWorkItemManager( this );
            Map<String, WorkItemHandler> workItemHandlers = config.getWorkItemHandlers();
            if ( workItemHandlers != null ) {
                for ( Map.Entry<String, WorkItemHandler> entry : workItemHandlers.entrySet() ) {
                    workItemManager.registerWorkItemHandler( entry.getKey(),
                                                             entry.getValue() );
                }
            }
        }
        return workItemManager;
    }

    public SignalManager getSignalManager() {
        return signalManager;
    }

    private void initProcessEventListeners() {
        for ( Process process : ruleBase.getProcesses() ) {
            if ( process instanceof RuleFlowProcess ) {
                StartNode startNode = ((RuleFlowProcess) process).getStart();
                List<Trigger> triggers = startNode.getTriggers();
                if ( triggers != null ) {
                    for ( Trigger trigger : triggers ) {
                        if ( trigger instanceof EventTrigger ) {
                            final List<EventFilter> filters = ((EventTrigger) trigger).getEventFilters();
                            String type = null;
                            for ( EventFilter filter : filters ) {
                                if ( filter instanceof EventTypeFilter ) {
                                    type = ((EventTypeFilter) filter).getType();
                                }
                            }
                            getSignalManager().addEventListener( type,
                                                                 new StartProcessEventListener( process.getId(),
                                                                                                filters,
                                                                                                trigger.getInMappings() ) );
                        }
                    }
                }
            }
        }
    }

    private class StartProcessEventListener
        implements
        EventListener {
        private String              processId;
        private List<EventFilter>   eventFilters;
        private Map<String, String> inMappings;

        public StartProcessEventListener(String processId,
                                         List<EventFilter> eventFilters,
                                         Map<String, String> inMappings) {
            this.processId = processId;
            this.eventFilters = eventFilters;
            this.inMappings = inMappings;
        }

        public String[] getEventTypes() {
            return null;
        }

        public void signalEvent(String type,
                                Object event) {
            for ( EventFilter filter : eventFilters ) {
                if ( !filter.acceptsEvent( type,
                                           event ) ) {
                    return;
                }
            }
            Map<String, Object> params = null;
            if ( inMappings != null && !inMappings.isEmpty() ) {
                params = new HashMap<String, Object>();
                for ( Map.Entry<String, String> entry : inMappings.entrySet() ) {
                    if ( "event".equals( entry.getValue() ) ) {
                        params.put( entry.getKey(),
                                    event );
                    } else {
                        params.put( entry.getKey(),
                                    entry.getValue() );
                    }
                }
            }
            startProcess( processId,
                          params );
        }
    }

    public TimerManager getTimerManager() {
        return timerManager;
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

    public ExecutorService getExecutorService() {
        return null; // no executor service
    }

    public void setExecutorService(ExecutorService executor) {
        // no executor service, so nothing to set
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        WorkingMemoryEntryPoint wmEntryPoint = this.entryPoints.get( name );
        return wmEntryPoint;
    }
    
    public Collection<WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints() {
        return this.entryPoints.values();
    }
    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        return this.typeConfReg;
    }

    public InternalFactHandle getInitialFactHandle() {
        return this.initialFactHandle;
    }

    public void setInitialFactHandle(InternalFactHandle initialFactHandle) {
        this.initialFactHandle = initialFactHandle;
    }

    public TimerService getTimerService() {
        return this.getTimerManager().getTimerService();
    }

    public SessionClock getSessionClock() {
        return (SessionClock) this.getTimerManager().getTimerService();
    }

    public PartitionTaskManager getPartitionManager(final RuleBasePartitionId partitionId) {
        return partitionManagers.get( partitionId );
    }
    
    public void startBatchExecution() {
        this.lock.lock();
        this.batchExecutionResult = new BatchExecutionResultImpl();
    }
    
    public BatchExecutionResultImpl getExecutionResult() {
        return ( BatchExecutionResultImpl ) this.batchExecutionResult;
    }
    
    public void endBatchExecution() {
        this.batchExecutionResult = null;
        this.lock.unlock();
    }    

    // public static class FactHandleInvalidation implements WorkingMemoryAction
    // {
    // private final InternalFactHandle handle;
    //        
    // public FactHandleInvalidation(InternalFactHandle handle) {
    // this.handle = handle;
    // }
    //
    // public void execute(InternalWorkingMemory workingMemory) {
    // workingMemory.getFactHandleFactory().destroyFactHandle( handle );
    // }
    //
    // public void write(WMSerialisationOutContext context) throws IOException {
    // context.writeInt( handle.getId() );
    // }
    //
    // public void readExternal(ObjectInput in) throws IOException,
    // ClassNotFoundException {
    // // TODO Auto-generated method stub
    //            
    // }
    //
    // public void writeExternal(ObjectOutput out) throws IOException {
    // // TODO Auto-generated method stub
    //            
    // }
    //        
    // }

    public void dispose() {
        this.workingMemoryEventSupport.reset();
        this.agendaEventSupport.reset();
        this.workflowEventSupport.reset();
        for ( Iterator it = this.__ruleBaseEventListeners.iterator(); it.hasNext(); ) {
            this.ruleBase.removeEventListener( (RuleBaseEventListener) it.next() );
        }
        this.stopPartitionManagers();
        this.timerManager.dispose();
    }

    public void setKnowledgeRuntime(KnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
    }

    public KnowledgeRuntime getKnowledgeRuntime() {
        return this.kruntime;
    }

    public void registerExitPoint(String name,
                                  ExitPoint exitPoint) {
        this.exitPoints.put( name,
                             exitPoint );
    }

    public void unregisterExitPoint(String name) {
        this.exitPoints.remove( name );
    }

    public Map<String, ExitPoint> getExitPoints() {
        return this.exitPoints;
    }

    public Map<String, WorkingMemoryEntryPoint> getEntryPoints() {
        return this.entryPoints;
    }

}
