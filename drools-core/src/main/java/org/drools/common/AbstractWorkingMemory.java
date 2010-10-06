/**
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

package org.drools.common;

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
import java.util.concurrent.ConcurrentLinkedQueue;
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
import org.drools.base.CalendarsImpl;
import org.drools.base.ClassObjectType;
import org.drools.base.MapGlobalResolver;
import org.drools.concurrent.ExecutorService;
import org.drools.concurrent.ExternalExecutorService;
import org.drools.core.util.ObjectHashSet;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.process.ProcessEventManager;
import org.drools.management.DroolsManagementAgent;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.InitialFactImpl;
import org.drools.reteoo.LIANodePropagation;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.PartitionManager;
import org.drools.reteoo.PartitionTaskManager;
import org.drools.reteoo.Rete;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.rule.TimeMachine;
import org.drools.runtime.Calendars;
import org.drools.runtime.Channel;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.ExitPoint;
import org.drools.runtime.Globals;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.process.InternalProcessRuntime;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.GlobalResolver;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.time.SessionClock;
import org.drools.time.TimerService;
import org.drools.time.TimerServiceFactory;
import org.drools.type.DateFormats;
import org.drools.type.DateFormatsImpl;

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
    ProcessEventManager,
    PropertyChangeListener {
    // ------------------------------------------------------------
    // Constants
    // ------------------------------------------------------------
    protected static final Class[]                               ADD_REMOVE_PROPERTY_CHANGE_LISTENER_ARG_TYPES = new Class[]{PropertyChangeListener.class};
    private static final int                                     NODE_MEMORIES_ARRAY_GROWTH                    = 32;

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------
    protected int                                                id;

    /** The arguments used when adding/removing a property change listener. */
    protected Object[]                                           addRemovePropertyChangeListenerArgs;

    /** The actual memory for the <code>JoinNode</code>s. */
    protected NodeMemories                                       nodeMemories;

    protected ObjectStore                                        objectStore;

    /** Global values which are associated with this memory. */
    protected GlobalResolver                                     globalResolver;

    protected Calendars                                          calendars;
    protected DateFormats                                        dateFormats;

    /** The eventSupport */
    protected WorkingMemoryEventSupport                          workingMemoryEventSupport;

    protected AgendaEventSupport                                 agendaEventSupport;

    protected List                                               __ruleBaseEventListeners;

    /** The <code>RuleBase</code> with which this memory is associated. */
    protected transient InternalRuleBase                         ruleBase;

    protected FactHandleFactory                                  handleFactory;

    protected TruthMaintenanceSystem                             tms;

    /** Rule-firing agenda. */
    protected InternalAgenda                                     agenda;

    protected Queue<WorkingMemoryAction>                         actionQueue;

    protected AtomicBoolean                                      evaluatingActionQueue;

    protected ReentrantLock                                      lock;

    protected boolean                                            discardOnLogicalOverride;

    /**
     * This must be thread safe as it is incremented and read via different
     * EntryPoints
     */
    protected AtomicLong                                         propagationIdCounter;

    private boolean                                              sequential;

    private List                                                 liaPropagations;

    /** Flag to determine if a rule is currently being fired. */
    protected volatile AtomicBoolean                             firing;

    private WorkItemManager                                      workItemManager;

    private TimeMachine                                          timeMachine;
    
    private TimerService                                         timerService;

    protected transient ObjectTypeConfigurationRegistry          typeConfReg;

    protected EntryPoint                                         entryPoint;
    protected transient EntryPointNode                           entryPointNode;

    protected Map<String, WorkingMemoryEntryPoint>               entryPoints;

    protected InternalFactHandle                                 initialFactHandle;

    protected SessionConfiguration                               config;

    protected PartitionManager                                   partitionManager;

    protected transient AtomicReference<ExternalExecutorService> threadPool                                    = new AtomicReference<ExternalExecutorService>();

    private Map<InternalFactHandle, PropagationContext>          modifyContexts;

    private InternalKnowledgeRuntime                             kruntime;

    private Map<String, ExitPoint>                               exitPoints;

    private Map<String, Channel>                                 channels;

    private Environment                                          environment;

    private ExecutionResults                                     batchExecutionResult;

    // this is a counter of concurrent operations happening. When this counter is zero, 
    // the engine is idle.
    private AtomicLong                                           opCounter;
    // this is the timestamp of the end of the last operation, based on the session clock,
    // or -1 if there are operation being executed at this moment
    private AtomicLong                                           lastIdleTimestamp;
    
    private InternalProcessRuntime                               processRuntime;

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
                                 final SessionConfiguration config,
                                 final Environment environment,
                                 final WorkingMemoryEventSupport workingMemoryEventSupport,
                                 final AgendaEventSupport agendaEventSupport) {
        this( id,
              ruleBase,
              handleFactory,
              null,
              0,
              config,
              environment,
              workingMemoryEventSupport,
              agendaEventSupport);
    }

    public AbstractWorkingMemory(final int id,
                                 final InternalRuleBase ruleBase,
                                 final FactHandleFactory handleFactory,
                                 final InternalFactHandle initialFactHandle,
                                 final long propagationContext,
                                 final SessionConfiguration config,
                                 final Environment environment) {
        this( id,
              ruleBase,
              handleFactory,
              initialFactHandle,
              propagationContext,
              config,
              environment,
              new WorkingMemoryEventSupport(),
              new AgendaEventSupport());
    }

    public AbstractWorkingMemory(final int id,
                                 final InternalRuleBase ruleBase,
                                 final FactHandleFactory handleFactory,
                                 final InternalFactHandle initialFactHandle,
                                 final long propagationContext,
                                 final SessionConfiguration config,
                                 final Environment environment,
                                 final WorkingMemoryEventSupport workingMemoryEventSupport,
                                 final AgendaEventSupport agendaEventSupport) {
        this.id = id;
        this.config = config;
        this.ruleBase = ruleBase;
        this.handleFactory = handleFactory;
        this.environment = environment;

        Globals globals = (Globals) this.environment.get( EnvironmentName.GLOBALS );
        if ( globals != null ) {
            if ( !(globals instanceof GlobalResolver) ) {
                this.globalResolver = new GlobalsAdapter( globals );
            } else {
                this.globalResolver = (GlobalResolver) globals;
            }
        } else {
            this.globalResolver = new MapGlobalResolver();
        }

        this.calendars = new CalendarsImpl();

        this.dateFormats = (DateFormats) this.environment.get( EnvironmentName.DATE_FORMATS );
        if ( this.dateFormats == null ) {
            this.dateFormats = new DateFormatsImpl();
            this.environment.set( EnvironmentName.DATE_FORMATS,
                                  this.dateFormats );
        }

        final RuleBaseConfiguration conf = this.ruleBase.getConfiguration();

        this.sequential = conf.isSequential();


        if ( initialFactHandle == null ) {
            this.initialFactHandle = handleFactory.newFactHandle( InitialFactImpl.getInstance(),
                                                                  null,
                                                                  this,
                                                                  this);
        } else {
            this.initialFactHandle = initialFactHandle;
        }

        this.actionQueue = new ConcurrentLinkedQueue<WorkingMemoryAction>();
        this.evaluatingActionQueue = new AtomicBoolean( false );

        this.addRemovePropertyChangeListenerArgs = new Object[]{this};
        this.workingMemoryEventSupport = workingMemoryEventSupport;
        this.agendaEventSupport = agendaEventSupport;
        this.__ruleBaseEventListeners = new LinkedList();
        this.lock = new ReentrantLock();
        this.liaPropagations = Collections.EMPTY_LIST;
        this.timeMachine = new TimeMachine();

        timerService = TimerServiceFactory.getTimerService( this.config );

        this.nodeMemories = new ConcurrentNodeMemories( this.ruleBase );
        
        this.tms = new TruthMaintenanceSystem(this);

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
        this.channels = new ConcurrentHashMap<String, Channel>();
        initPartitionManagers();
        initTransient();

        this.opCounter = new AtomicLong( 0 );
        this.lastIdleTimestamp = new AtomicLong( -1 );

        initManagementBeans();        
    }

    private void initManagementBeans() {
        if ( this.ruleBase.getConfiguration().isMBeansEnabled() ) {
            DroolsManagementAgent.getInstance().registerKnowledgeSession( this );
        }
    }
    
    private InternalProcessRuntime createProcessRuntime() {
		try {
			return ProcessRuntimeFactory.newProcessRuntime(this);
		} catch (IllegalArgumentException e) {
			return null;
		}
    }

    public String getEntryPointId() {
        return EntryPoint.DEFAULT.getEntryPointId();
    }

    public static class GlobalsAdapter
        implements
        GlobalResolver {
        private Globals globals;

        public GlobalsAdapter(Globals globals) {
            this.globals = globals;
        }

        public Object resolveGlobal(String identifier) {
            return this.globals.get( identifier );
        }

        public void setGlobal(String identifier,
                              Object value) {
            this.globals.set( identifier,
                              value );
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

        updateEntryPointsCache();
    }

    public void updateEntryPointsCache() {
        Map<EntryPoint, EntryPointNode> reteEPs = this.ruleBase.getRete().getEntryPointNodes();

        // first create a temporary cache to find which entry points were removed from the network
        Map<String, WorkingMemoryEntryPoint> cache = new HashMap<String, WorkingMemoryEntryPoint>( this.entryPoints );

        // now, add any entry point that was added to the knowledge base
        for ( EntryPointNode entryPointNode : reteEPs.values() ) {
            EntryPoint id = entryPointNode.getEntryPoint();
            cache.remove( id.getEntryPointId() );
            if ( !EntryPoint.DEFAULT.equals( id ) && !this.entryPoints.containsKey( id ) ) {
                WorkingMemoryEntryPoint wmEntryPoint = new NamedEntryPoint( id,
                                                                            entryPointNode,
                                                                            this );
                this.entryPoints.put( entryPointNode.getEntryPoint().getEntryPointId(),
                                      wmEntryPoint );
            }
        }

        // now, if there is any element left in the cache, remove them as they were removed from the network
        this.entryPoints.keySet().removeAll( cache.keySet() );
    }

    /**
     * Creates the actual partition managers and their tasks for multi-thread
     * processing
     */
    private void initPartitionManagers() {
        if ( this.ruleBase.getConfiguration().isMultithreadEvaluation() ) {
            this.partitionManager = new PartitionManager( this );

            for ( RuleBasePartitionId partitionId : this.ruleBase.getPartitionIds() ) {
                this.partitionManager.manage( partitionId );
            }
        }
    }

    /**
     * This method is called to start the multiple partition threads when
     * running in multi-thread mode
     */
    public void startPartitionManagers() {
        startOperation();
        try {
            if ( this.ruleBase.getConfiguration().isMultithreadEvaluation() ) {
                int maxThreads = (this.ruleBase.getConfiguration().getMaxThreads() > 0) ? this.ruleBase.getConfiguration().getMaxThreads() : this.ruleBase.getPartitionIds().size();
                if ( this.threadPool.compareAndSet( null,
                                                    createExecutorService( maxThreads ) ) ) {
                    this.partitionManager.setPool( this.threadPool.get() );
                }
            }
        } finally {
            endOperation();
        }
    }

    private ExternalExecutorService createExecutorService(final int maxThreads) {
        return new ExternalExecutorService( Executors.newFixedThreadPool( maxThreads ) );
    }

    public void stopPartitionManagers() {
        startOperation();
        try {
            if ( this.ruleBase.getConfiguration().isMultithreadEvaluation() ) {
                ExternalExecutorService service = this.threadPool.get();
                if ( this.threadPool.compareAndSet( service,
                                                    null ) ) {
                    service.shutdown();
                    partitionManager.shutdown();
                }
            }
        } finally {
            endOperation();
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
        this.opCounter.set( 0 );
        this.lastIdleTimestamp.set( -1 );

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

    public void addEventListener(ProcessEventListener listener) {
        ((ProcessEventManager) this.processRuntime).addEventListener( listener );
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        return ((ProcessEventManager) this.processRuntime).getProcessEventListeners();
    }

    public void removeEventListener(ProcessEventListener listener) {
    	((ProcessEventManager) this.processRuntime).removeEventListener(listener);
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
            this.ruleBase.readLock();
            this.lock.lock();
            startOperation();
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
            endOperation();
            this.lock.unlock();
            this.ruleBase.readUnlock();
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

    public Calendars getCalendars() {
        return this.calendars;
    }

    public DateFormats getDateFormats() {
        return this.dateFormats;
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

    public int fireAllRules() throws FactException {
        return fireAllRules( null,
                             -1 );
    }

    public int fireAllRules(int fireLimit) throws FactException {
        return fireAllRules( null,
                             fireLimit );
    }

    public int fireAllRules(final AgendaFilter agendaFilter) throws FactException {
        return fireAllRules( agendaFilter,
                             -1 );
    }

    public int fireAllRules(final AgendaFilter agendaFilter,
                            int fireLimit) throws FactException {
        if ( this.firing.compareAndSet( false,
                                        true ) ) {
            try {
                synchronized ( this ) {
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
                    fireCount = this.agenda.fireAllRules( agendaFilter,
                                                          fireLimit );
                    return fireCount;
                }
            } finally {
                this.firing.set( false );
            }
        }
        return 0;
    }

    /**
     * Keeps firing activations until a halt is called. If in a given moment,
     * there is no activation to fire, it will wait for an activation to be
     * added to an active agenda group or rule flow group.
     * 
     * @throws IllegalStateException
     *             if this method is called when running in sequential mode
     */
    public void fireUntilHalt() {
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
    public void fireUntilHalt(final AgendaFilter agendaFilter) {
        if ( isSequential() ) {
            throw new IllegalStateException( "fireUntilHalt() can not be called in sequential mode." );
        }

        if ( this.firing.compareAndSet( false,
                                        true ) ) {
            try {
                synchronized ( this ) {
                    executeQueuedActions();
                    this.agenda.fireUntilHalt( agendaFilter );
                }
            } finally {
                this.firing.set( false );
            }
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

    public FactHandle insert(final Object object,
                             final boolean dynamic,
                             boolean logical,
                             final Rule rule,
                             final Activation activation) throws FactException {
        if ( object == null ) {
            // you cannot assert a null object
            return null;
        }

        try {
            startOperation();

            ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                                                                          object );
            if ( logical && !typeConf.isTMSEnabled()) {
                enableTMS(object, typeConf);
            }

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
                this.ruleBase.readLock();
                this.lock.lock();
                // check if the object already exists in the WM
                handle = (InternalFactHandle) this.objectStore.getHandleForObject( object );

                if ( typeConf.isTMSEnabled() ) {
                  
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
                            // this object was previously justified, so we have to override it to stated
                            key.setStatus( EqualityKey.STATED );
                            this.tms.removeLogicalDependencies( handle );
                        } else {
                            // this was object is already justified, so just add new logical dependency
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

                        key = createEqualityKey(handle);
                        
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
                            // Its previous justified, so switch to stated and remove logical dependencies
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
                            // only add as logical dependency if this wasn't previously stated
                            this.tms.addLogicalDependency( key.getFactHandle(),
                                                           activation,
                                                           activation.getPropagationContext(),
                                                           rule );
                            return key.getFactHandle();
                        } else {
                            // You cannot justify a previously stated equality equal object, so return null
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

                // if the dynamic parameter is true or if the user declared the fact type with the meta tag:
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
                this.ruleBase.readUnlock();
            }
            return handle;
        } finally {
            endOperation();
        }

    }

    /** Side-effects, will add the created key to the handle. */
    private EqualityKey createEqualityKey(InternalFactHandle handle) {
      EqualityKey key = new EqualityKey( handle );
      handle.setEqualityKey( key );
      return key;
    }

    /**
     * TMS will be automatically enabled when the first logical insert happens. 
     * 
     * We will take all the already asserted objects of the same type and initialize
     * the equality map.
     *  
     * @param object the logically inserted object.
     * @param conf the type's configuration.
     */
    private void enableTMS(Object object, ObjectTypeConf conf) {

        
        final Rete source = this.ruleBase.getRete();
        final ClassObjectType cot = new ClassObjectType( object.getClass() );
        final Map<ObjectType, ObjectTypeNode> map = source.getObjectTypeNodes( EntryPoint.DEFAULT );
        final ObjectTypeNode node = map.get( cot );
        final ObjectHashSet memory = (ObjectHashSet) this.getNodeMemory( node );
      
        // All objects of this type that are already there were certainly stated,
        // since this method call happens at the first logical insert, for any given type.
        org.drools.core.util.Iterator it = memory.iterator();

        for ( Object obj = it.next(); obj != null; obj = it.next() ) {
          
            org.drools.core.util.ObjectHashSet.ObjectEntry holder = (org.drools.core.util.ObjectHashSet.ObjectEntry) obj; 
    
            InternalFactHandle handle = (InternalFactHandle) holder.getValue();
            
            if ( handle != null) {
                EqualityKey key = createEqualityKey(handle);
                key.setStatus(EqualityKey.STATED);
                this.tms.put(key);
            }
        }
      
        // Enable TMS for this type.
        conf.enableTMS();
      
    }

    private InternalFactHandle createHandle(final Object object,
                                            ObjectTypeConf typeConf) {
        InternalFactHandle handle;
        handle = this.handleFactory.newFactHandle( object,
                                                   typeConf,
                                                   this,
                                                   this );
        this.objectStore.addHandle( handle,
                                    object );
        return handle;
    }

    public void insert(final InternalFactHandle handle,
                       final Object object,
                       final Rule rule,
                       final Activation activation,
                       ObjectTypeConf typeConf) {
        this.ruleBase.executeQueuedActions();

        executeQueuedActions();

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
            this.ruleBase.readLock();
            this.lock.lock();
            startOperation();
            this.ruleBase.executeQueuedActions();

            InternalFactHandle handle = (InternalFactHandle) factHandle;
            if ( handle.getId() == -1 ) {
                // can't retract an already retracted handle
                return;
            }

            // the handle might have been disconnected, so reconnect if it has
            if ( handle.isDisconnected() ) {
                handle = this.objectStore.reconnect( handle );
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
            
            final ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                object );

            this.entryPointNode.retractObject( handle,
                                               propagationContext,
                                               typeConf,
                                               this );

            if ( typeConf.isTMSEnabled() ) {

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
            endOperation();
            this.lock.unlock();
            this.ruleBase.readUnlock();
        }
    }

    public EntryPointNode getEntryPointNode() {
        return this.entryPointNode;
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

        update( (org.drools.FactHandle) factHandle,
                object,
                rule,
                activation );
    }

    /**
     * modify is implemented as half way retract / assert due to the truth
     * maintenance issues.
     * 
     * @see WorkingMemory
     */
    public void update(org.drools.FactHandle factHandle,
                       final Object object,
                       final Rule rule,
                       final Activation activation) throws FactException {
        try {
            this.ruleBase.readLock();
            this.lock.lock();
            startOperation();
            this.ruleBase.executeQueuedActions();

            // the handle might have been disconnected, so reconnect if it has
            if ( ((InternalFactHandle)factHandle).isDisconnected() ) {
                factHandle = this.objectStore.reconnect( factHandle );
            }
            
            final ObjectTypeConf typeConf = this.typeConfReg.getObjectTypeConf( this.entryPoint,
                object );

            // only needed if we maintain tms, but either way we must get it before we do the retract
            int status = -1;
            if ( typeConf.isTMSEnabled() ) {
                status = ((InternalFactHandle) factHandle).getEqualityKey().getStatus();
            }
            final InternalFactHandle handle = (InternalFactHandle) factHandle;
            final Object originalObject = handle.getObject();

            if ( handle.getId() == -1 || object == null || (handle.isEvent() && ((EventFactHandle) handle).isExpired()) ) {
                // the handle is invalid, most likely already retracted, so return and we cannot assert a null object
                return;
            }

            if ( activation != null ) {
                // release resources so that they can be GC'ed
                activation.getPropagationContext().releaseResources();
            }

            if ( originalObject != object || !AssertBehaviour.IDENTITY.equals( this.ruleBase.getConfiguration().getAssertBehaviour() ) ) {
                this.objectStore.removeHandle( handle );

                // set anyway, so that it updates the hashCodes
                handle.setObject( object );
                this.objectStore.addHandle( handle,
                                            object );
            }

            if ( typeConf.isTMSEnabled() ) {
            
                // the hashCode and equality has changed, so we must update the
                // EqualityKey
                EqualityKey key = handle.getEqualityKey();
                key.removeFactHandle( handle );
            

                // If the equality key is now empty, then remove it
                if ( key.isEmpty() ) {
                    this.tms.remove( key );
                }
    
                // now use an existing EqualityKey, if it exists, else create a new one
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

            final PropagationContext propagationContext = new PropagationContextImpl( getNextPropagationIdCounter(),
                                                                                      PropagationContext.MODIFICATION,
                                                                                      rule,
                                                                                      (activation == null) ? null : (LeftTuple) activation.getTuple(),
                                                                                      handle,
                                                                                      this.agenda.getActiveActivations(),
                                                                                      this.agenda.getDormantActivations(),
                                                                                      entryPoint );

            this.entryPointNode.modifyObject( handle,
                                              propagationContext,
                                              typeConf,
                                              this );

            this.workingMemoryEventSupport.fireObjectUpdated( propagationContext,
                                                              (org.drools.FactHandle) factHandle,
                                                              originalObject,
                                                              object,
                                                              this );

            executeQueuedActions();
        } finally {
            endOperation();
            this.lock.unlock();
            this.ruleBase.readUnlock();
        }
    }

    public void executeQueuedActions() {
        try {
            startOperation();
            if( evaluatingActionQueue.compareAndSet( false, true ) ) {
                try {
                    if ( !this.actionQueue.isEmpty() ) {
                        WorkingMemoryAction action = null;

                        while ( (action = actionQueue.poll()) != null ) {
                            try {
                                action.execute( this );
                            } catch ( Exception e ) {
                                throw new RuntimeDroolsException( "Unexpected exception executing action " + action.toString(),
                                                                  e );
                            }
                        }
                    }
                } finally {
                    evaluatingActionQueue.compareAndSet( true, false );
                }
            }
        } finally {
            endOperation();
        }
    }

    public Queue<WorkingMemoryAction> getActionQueue() {
        return this.actionQueue;
    }

    public void queueWorkingMemoryAction(final WorkingMemoryAction action) {
        try {
            startOperation();
            this.actionQueue.add( action );
            this.agenda.notifyHalt();
        } finally {
            endOperation();
        }
    }

    public void removeLogicalDependencies(final Activation activation,
                                          final PropagationContext context,
                                          final Rule rule) throws FactException {
      
        this.tms.removeLogicalDependencies( activation,
                                            context,
                                            rule );

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
    
    public InternalProcessRuntime getProcessRuntime() {
    	return processRuntime;
    }

    public ProcessInstance startProcess(final String processId) {
    	return processRuntime.startProcess(processId);
    }

    public ProcessInstance startProcess(String processId,
                                        Map<String, Object> parameters) {
        return processRuntime.startProcess(processId, parameters);
    }

    public Collection<ProcessInstance> getProcessInstances() {
        return processRuntime.getProcessInstances();
    }

    public ProcessInstance getProcessInstance(long processInstanceId) {
        return processRuntime.getProcessInstance(processInstanceId);
    }

    public WorkItemManager getWorkItemManager() {
        if ( workItemManager == null ) {
            workItemManager = config.getWorkItemManagerFactory().createWorkItemManager( this.getKnowledgeRuntime() );
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

    public List iterateObjectsToList() {
        List result = new ArrayList();
        Iterator iterator = iterateObjects();
        for ( ; iterator.hasNext(); ) {
            result.add( iterator.next() );
        }
        return result;
    }

    public List iterateNonDefaultEntryPointObjectsToList() {
        List result = new ArrayList();
        for ( Map.Entry<String, WorkingMemoryEntryPoint> entry : getEntryPoints().entrySet() ) {
            WorkingMemoryEntryPoint entryPoint = entry.getValue();
            if ( entryPoint instanceof NamedEntryPoint ) {
                result.add( new EntryPointObjects( entry.getKey(),
                                                   new ArrayList( entry.getValue().getObjects() ) ) );
            }
        }
        return result;
    }

    private class EntryPointObjects {
        private String name;
        private List   objects;

        public EntryPointObjects(String name,
                                 List objects) {
            this.name = name;
            this.objects = objects;
        }
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
        return this.timerService;
    }

    public SessionClock getSessionClock() {
        return (SessionClock) this.timerService;
    }

    public PartitionTaskManager getPartitionTaskManager(final RuleBasePartitionId partitionId) {
        return partitionManager.getPartitionTaskManager( partitionId );
    }

    public void startBatchExecution(ExecutionResultImpl results) {
        this.ruleBase.readLock();
        this.lock.lock();
        this.batchExecutionResult = results;
    }

    public ExecutionResultImpl getExecutionResult() {
        return (ExecutionResultImpl) this.batchExecutionResult;
    }

    public void endBatchExecution() {
        this.batchExecutionResult = null;
        this.lock.unlock();
        this.ruleBase.readUnlock();
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
        if ( this.ruleBase.getConfiguration().isMBeansEnabled() ) {
            DroolsManagementAgent.getInstance().unregisterKnowledgeSession( this );
        }
        this.workingMemoryEventSupport.reset();
        this.agendaEventSupport.reset();
        for ( Iterator it = this.__ruleBaseEventListeners.iterator(); it.hasNext(); ) {
            this.ruleBase.removeEventListener( (RuleBaseEventListener) it.next() );
        }
        this.stopPartitionManagers();
        if (processRuntime != null) {
        	this.processRuntime.dispose();
        }
    }

    public void setKnowledgeRuntime(InternalKnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
        this.processRuntime = createProcessRuntime();
    }

    public InternalKnowledgeRuntime getKnowledgeRuntime() {
        return this.kruntime;
    }

    /**
     * @deprecated Use {@link #registerChannel(String, Channel)} instead.
     */
    @Deprecated
    public void registerExitPoint(String name,
                                  ExitPoint exitPoint) {
        this.exitPoints.put( name,
                             exitPoint );
    }

    /**
     * @deprecated Use {@link #unregisterChannel(String)} instead.
     */
    @Deprecated
    public void unregisterExitPoint(String name) {
        this.exitPoints.remove( name );
    }

    /**
     * @deprecated Use {@link #getChannels()} instead.
     */
    @Deprecated
    public Map<String, ExitPoint> getExitPoints() {
        return this.exitPoints;
    }

    public void registerChannel(String name,
                                Channel channel) {
        this.channels.put( name,
                           channel );
    }
    
    public void unregisterChannel(String name) {
        this.channels.remove( name );
    }

    public Map<String, Channel> getChannels() {
        return this.channels;
    }

    public Map<String, WorkingMemoryEntryPoint> getEntryPoints() {
        return this.entryPoints;
    }

    public long getFactCount() {
        return this.objectStore.size();
    }

    public long getTotalFactCount() {
        long result = 0;
        for ( WorkingMemoryEntryPoint ep : this.entryPoints.values() ) {
            result += ep.getFactCount();
        }
        return result;
    }

    /**
     * This method must be called before starting any new work in the engine,
     * like inserting a new fact or firing a new rule. It will reset the engine
     * idle time counter.
     * 
     * This method must be extremely light to avoid contentions when called by 
     * multiple threads/entry-points
     */
    public void startOperation() {
        if ( this.opCounter.getAndIncrement() == 0 ) {
            // means the engine was idle, reset the timestamp
            this.lastIdleTimestamp.set( -1 );
        }
    }

    private EndOperationListener endOperationListener;

    public void setEndOperationListener(EndOperationListener listener) {
        this.endOperationListener = listener;
    }

    /**
     * This method must be called after finishing any work in the engine,
     * like inserting a new fact or firing a new rule. It will reset the engine
     * idle time counter.
     * 
     * This method must be extremely light to avoid contentions when called by 
     * multiple threads/entry-points
     */
    public void endOperation() {
        if ( this.opCounter.decrementAndGet() == 0 ) {
            // means the engine is idle, so, set the timestamp
            this.lastIdleTimestamp.set( this.timerService.getCurrentTime() );
            if ( this.endOperationListener != null ) {
                this.endOperationListener.endOperation( this.getKnowledgeRuntime() );
            }
        }
    }

    /**
     * Returns the number of time units (usually ms) that the engine is idle
     * according to the session clock or -1 if it is not idle.
     * 
     * This method is not synchronised and might return an approximate value.
     *  
     * @return
     */
    public long getIdleTime() {
        long lastIdle = this.lastIdleTimestamp.get();
        return lastIdle > -1 ? timerService.getCurrentTime() - lastIdle : -1;
    }

    public long getLastIdleTimestamp() {
        return this.lastIdleTimestamp.get();
    }

    /**
     * Returns the number of time units (usually ms) to
     * the next scheduled job
     * 
     * @return the number of time units until the next scheduled job or -1 if
     *         there is no job scheduled
     */
    public long getTimeToNextJob() {
        return this.timerService.getTimeToNextJob();
    }

    public void prepareToFireActivation() {
        if ( this.partitionManager != null ) {
            this.partitionManager.holdTasks();
            this.partitionManager.waitForPendingTasks();
        }
    }

    public void activationFired() {
        if ( this.partitionManager != null ) {
            this.partitionManager.releaseTasks();
        }
    }

}
