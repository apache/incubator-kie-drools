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

package org.drools.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
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
import org.drools.base.CalendarsImpl;
import org.drools.base.MapGlobalResolver;
import org.drools.event.AgendaEventListener;
import org.drools.event.AgendaEventSupport;
import org.drools.event.RuleBaseEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.event.WorkingMemoryEventSupport;
import org.drools.management.DroolsManagementAgent;
import org.drools.marshalling.impl.ObjectMarshallingStrategyStoreImpl;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.InitialFactImpl;
import org.drools.reteoo.LIANodePropagation;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.process.InternalProcessRuntime;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.drools.spi.Activation;
import org.drools.spi.AgendaFilter;
import org.drools.spi.AsyncExceptionHandler;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.GlobalResolver;
import org.drools.time.AcceptsTimerJobFactoryManager;
import org.drools.time.TimerService;
import org.drools.time.TimerServiceFactory;
import org.drools.type.DateFormats;
import org.drools.type.DateFormatsImpl;
import org.kie.event.process.ProcessEventListener;
import org.kie.event.process.ProcessEventManager;
import org.kie.marshalling.ObjectMarshallingStrategy;
import org.kie.marshalling.ObjectMarshallingStrategyStore;
import org.kie.runtime.Calendars;
import org.kie.runtime.Channel;
import org.kie.runtime.Environment;
import org.kie.runtime.EnvironmentName;
import org.kie.runtime.ExecutionResults;
import org.kie.runtime.Globals;
import org.kie.runtime.process.ProcessInstance;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;
import org.kie.time.SessionClock;

/**
 * Implementation of <code>WorkingMemory</code>.
 */
public abstract class AbstractWorkingMemory
    implements
    InternalWorkingMemoryActions,
    EventSupport,
    ProcessEventManager {

    protected int                                                id;

    /** The actual memory for the <code>JoinNode</code>s. */
    private   NodeMemories                                       nodeMemories;

    protected NamedEntryPoint                                    defaultEntryPoint;

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

    private   TruthMaintenanceSystem                             tms;

    /** Rule-firing agenda. */
    protected InternalAgenda                                     agenda;

    private   Queue<WorkingMemoryAction>                         actionQueue;

    protected AtomicBoolean                                      evaluatingActionQueue;

    protected ReentrantLock                                      lock;

    /**
     * This must be thread safe as it is incremented and read via different
     * EntryPoints
     */
    protected AtomicLong                                         propagationIdCounter;

    private boolean                                              sequential;

    private List<LIANodePropagation>                             liaPropagations;

    /** Flag to determine if a rule is currently being fired. */
    protected volatile AtomicBoolean                             firing;

    private WorkItemManager                                      workItemManager;

    private TimerService                                         timerService;

    protected Map<String, WorkingMemoryEntryPoint>               entryPoints;

    protected InternalFactHandle                                 initialFactHandle;

    protected SessionConfiguration                               config;

    private InternalKnowledgeRuntime                             kruntime;

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

    private transient ObjectMarshallingStrategyStore             marshallingStore;

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
              agendaEventSupport );
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
              new AgendaEventSupport() );
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

        nodeMemories = new ConcurrentNodeMemories( this.ruleBase );
        actionQueue = new ConcurrentLinkedQueue<WorkingMemoryAction>();

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
                                                                  this );
        } else {
            this.initialFactHandle = initialFactHandle;
        }

        this.evaluatingActionQueue = new AtomicBoolean( false );

        this.workingMemoryEventSupport = workingMemoryEventSupport;
        this.agendaEventSupport = agendaEventSupport;
        this.__ruleBaseEventListeners = new LinkedList();
        this.lock = new ReentrantLock();

        timerService = TimerServiceFactory.getTimerService( this.config );        
        ((AcceptsTimerJobFactoryManager) timerService).setTimerJobFactoryManager( config.getTimerJobFactoryManager() );

        this.propagationIdCounter = new AtomicLong( propagationContext );

        this.firing = new AtomicBoolean( false );

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
            return ProcessRuntimeFactory.newProcessRuntime( this );
        } catch ( IllegalArgumentException e ) {
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

    public void updateEntryPointsCache() {
        if (ruleBase.getAddedEntryNodeCache() != null) {
            for (EntryPointNode addedNode : ruleBase.getAddedEntryNodeCache()) {
                EntryPoint id = addedNode.getEntryPoint();
                if (EntryPoint.DEFAULT.equals(id)) continue;
                WorkingMemoryEntryPoint wmEntryPoint = new NamedEntryPoint(id, addedNode, this);
                entryPoints.put(id.getEntryPointId(), wmEntryPoint);
            }
        }

        if (ruleBase.getRemovedEntryNodeCache() != null) {
            for (EntryPointNode removedNode : ruleBase.getRemovedEntryNodeCache()) {
                entryPoints.remove(removedNode.getEntryPoint().getEntryPointId());
            }
        }
    }

    private void initTransient() {
        EntryPointNode epn = this.ruleBase.getRete().getEntryPointNode( EntryPoint.DEFAULT );

        this.defaultEntryPoint = new NamedEntryPoint( EntryPoint.DEFAULT,
                                                      epn,
                                                      this );

        this.entryPoints = new ConcurrentHashMap<String, WorkingMemoryEntryPoint>();

        this.entryPoints.put( "DEFAULT",
                              this.defaultEntryPoint );

        updateEntryPointsCache();
    }

    public SessionConfiguration getSessionConfiguration() {
        return this.config;
    }

    public void reset() {
        throw new UnsupportedOperationException( "This should not be called" );
    }

    public void reset(int handleId,
                      long handleCounter,
                      long propagationCounter) {
        if (nodeMemories != null) nodeMemories.clear();
        this.agenda.clear();

        for ( WorkingMemoryEntryPoint ep : this.entryPoints.values() ) {
            // clear the state for each entry point
            InternalWorkingMemoryEntryPoint iep = (InternalWorkingMemoryEntryPoint) ep;
            iep.reset();
        }

        this.handleFactory.clear( handleId,
                                  handleCounter );
        if (tms != null) tms.clear();
        if (liaPropagations != null) liaPropagations.clear();
        if (actionQueue != null) actionQueue.clear();

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
        if (liaPropagations == null) liaPropagations = new ArrayList();
        liaPropagations.add( liaNodePropagation );
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
        ((ProcessEventManager) this.processRuntime).removeEventListener( listener );
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
                startOperation();
                ruleBase.readLock();
                
                // If we're already firing a rule, then it'll pick up
                // the firing for any other assertObject(..) that get
                // nested inside, avoiding concurrent-modification
                // exceptions, depending on code paths of the actions.
                if ( liaPropagations != null && isSequential() ) {
                    for ( Iterator it = liaPropagations.iterator(); it.hasNext(); ) {
                        ((LIANodePropagation) it.next()).doPropagation( this );
                    }
                }

                // do we need to call this in advance?
                executeQueuedActions();

                int fireCount = 0;
                fireCount = this.agenda.fireAllRules( agendaFilter,
                                                      fireLimit );
                return fireCount;
            } finally {
                ruleBase.readUnlock();
                endOperation();
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
     */
    public Object getObject(org.kie.runtime.rule.FactHandle handle) {
        // the handle might have been disconnected, so reconnect if it has
        if ( ((InternalFactHandle)handle).isDisconnected() ) {
            handle = this.defaultEntryPoint.getObjectStore().reconnect( handle );
        }        
        return this.defaultEntryPoint.getObject( handle );
    }

    public ObjectStore getObjectStore() {
        return ((InternalWorkingMemoryEntryPoint) this.defaultEntryPoint).getObjectStore();
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle getFactHandle(final Object object) {
        return this.defaultEntryPoint.getFactHandle( object );
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle getFactHandleByIdentity(final Object object) {
        return getObjectStore().getHandleForObjectIdentity( object );
    }

    /**
     * This class is not thread safe, changes to the working memory during
     * iteration may give unexpected results
     */
    public Iterator iterateObjects() {
        return getObjectStore().iterateObjects();
    }

    /**
     * This class is not thread safe, changes to the working memory during
     * iteration may give unexpected results
     */
    public Iterator iterateObjects(org.kie.runtime.ObjectFilter filter) {
        return getObjectStore().iterateObjects( filter );
    }

    /**
     * This class is not thread safe, changes to the working memory during
     * iteration may give unexpected results
     */
    public Iterator iterateFactHandles() {
        return getObjectStore().iterateFactHandles();
    }

    /**
     * This class is not thread safe, changes to the working memory during
     * iteration may give unexpected results
     */
    public Iterator iterateFactHandles(org.kie.runtime.ObjectFilter filter) {
        return getObjectStore().iterateFactHandles( filter );
    }

    public abstract QueryResults getQueryResults(String query);

    public void setFocus(final String focus) {
        this.agenda.setFocus( focus );
    }

    /**
     * @see WorkingMemory
     */
    public FactHandle insert(final Object object) throws FactException {
        return insert( object, /* Not-Dynamic */
                       null,
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
                       null,
                       false,
                       true,
                       null,
                       null );
    }

    public FactHandle insert(final Object object,
                             final boolean dynamic) throws FactException {
        return insert( object,
                       null,
                       dynamic,
                       false,
                       null,
                       null );
    }

    public FactHandle insertLogical(final Object object,
                                    final boolean dynamic) throws FactException {
        return insert( object,
                       null,
                       dynamic,
                       true,
                       null,
                       null );
    }

    public FactHandle insertLogical(final Object object,
                                    final Object value) throws FactException {
        return insert( object,
                       value,
                       false,
                       true,
                       null,
                       null );
    }

    public FactHandle insert(final Object object,
                             final Object tmsValue,
                             final boolean dynamic,
                             boolean logical,
                             final Rule rule,
                             final Activation activation) throws FactException {
        return this.defaultEntryPoint.insert( object,
                                              tmsValue,
                                              dynamic,
                                              logical,
                                              rule,
                                              activation );
    }

    public void insert(final InternalFactHandle handle,
                       final Object object,
                       final Rule rule,
                       final Activation activation,
                       ObjectTypeConf typeConf) {
        this.defaultEntryPoint.insert( handle,
                                       object,
                                       rule,
                                       activation,
                                       typeConf );
    }

    public void retract(final org.kie.runtime.rule.FactHandle handle) throws FactException {
        delete( (org.drools.FactHandle) handle,
                 null,
                 null );
    }

    public void delete(final org.kie.runtime.rule.FactHandle handle) throws FactException {
        delete( (org.drools.FactHandle) handle,
                 null,
                 null );
    }

    public void delete(final org.drools.FactHandle factHandle,
                        final Rule rule,
                        final Activation activation) throws FactException {
        this.defaultEntryPoint.delete( factHandle,
                                        rule,
                                        activation );
    }

    public EntryPointNode getEntryPointNode() {
        return ((InternalWorkingMemoryEntryPoint) this.defaultEntryPoint).getEntryPointNode();
    }

    public void update(final org.kie.runtime.rule.FactHandle handle,
                       final Object object) throws FactException {
        update( (org.drools.FactHandle) handle,
                object,
                Long.MAX_VALUE,
                null );
    }

    public void update(final org.kie.runtime.rule.FactHandle factHandle,
                       final Object object,
                       final long mask,
                       final Activation activation) throws FactException {

        update( (org.drools.FactHandle) factHandle,
                object,
                mask,
                activation );
    }

    /**
     * modify is implemented as half way retract / assert due to the truth
     * maintenance issues.
     * 
     * @see WorkingMemory
     */
    public void update(FactHandle factHandle,
                       final Object object,
                       final long mask,
                       final Activation activation) throws FactException {
        this.defaultEntryPoint.update( factHandle,
                                       object,
                                       mask,
                                       activation );
    }

    public void executeQueuedActions() {
        try {
            startOperation();
            if ( evaluatingActionQueue.compareAndSet( false,
                                                      true ) ) {
                try {
                    if ( actionQueue!= null && !actionQueue.isEmpty() ) {
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
                    evaluatingActionQueue.compareAndSet( true,
                                                         false );
                }
            }
        } finally {
            endOperation();
        }
    }

    public Queue<WorkingMemoryAction> getActionQueue() {
        return actionQueue;
    }

    public void queueWorkingMemoryAction(final WorkingMemoryAction action) {
        try {
            startOperation();
            getActionQueue().add( action );
            this.agenda.notifyHalt();
        } finally {
            endOperation();
        }
    }  

    /**
     * Retrieve the <code>JoinMemory</code> for a particular
     * <code>JoinNode</code>.
     * @param node
     *            The <code>JoinNode</code> key.
     * @return The node's memory.
     */
    public Memory getNodeMemory(final MemoryFactory node) {
        return nodeMemories.getNodeMemory( node );
    }

    public void clearNodeMemory(final MemoryFactory node) {
        if (nodeMemories != null) nodeMemories.clearNodeMemory( node );
    }
    
    public NodeMemories getNodeMemories() {
        return nodeMemories;
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
        return processRuntime.startProcess( processId );
    }

    public ProcessInstance startProcess(String processId,
                                        Map<String, Object> parameters) {
        return processRuntime.startProcess( processId,
                                            parameters );
    }

	public ProcessInstance createProcessInstance(String processId,
			                                     Map<String, Object> parameters) {
		return processRuntime.createProcessInstance(processId, parameters);
	}

	public ProcessInstance startProcessInstance(long processInstanceId) {
		return processRuntime.startProcessInstance(processInstanceId);
	}

    public Collection<ProcessInstance> getProcessInstances() {
        return processRuntime.getProcessInstances();
    }

    public ProcessInstance getProcessInstance(long processInstanceId) {
        return processRuntime.getProcessInstance( processInstanceId );
    }

    public WorkItemManager getWorkItemManager() {
        if ( workItemManager == null ) {
            workItemManager = config.getWorkItemManagerFactory().createWorkItemManager( this.getKnowledgeRuntime() );
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ksession", this.getKnowledgeRuntime());
            Map<String, WorkItemHandler> workItemHandlers = config.getWorkItemHandlers(params);
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
        Declaration[] declarations = ((RuleTerminalNode)((LeftTuple) activation.getTuple()).getLeftTupleSink()).getDeclarations();
        
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

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        WorkingMemoryEntryPoint wmEntryPoint = this.entryPoints.get( name );
        return wmEntryPoint;
    }

    public Collection<WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints() {
        return this.entryPoints.values();
    }

    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        return this.defaultEntryPoint.getObjectTypeConfigurationRegistry();
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

    public void dispose() {
        if ( this.ruleBase.getConfiguration().isMBeansEnabled() ) {
            DroolsManagementAgent.getInstance().unregisterKnowledgeSession( this );
        }
        for( WorkingMemoryEntryPoint ep : this.entryPoints.values() ) {
            ep.dispose();
        }
        this.workingMemoryEventSupport.reset();
        this.agendaEventSupport.reset();
        for ( Iterator it = this.__ruleBaseEventListeners.iterator(); it.hasNext(); ) {
            this.ruleBase.removeEventListener( (RuleBaseEventListener) it.next() );
        }
        if ( processRuntime != null ) {
            this.processRuntime.dispose();
        }
        if ( timerService != null ) {
        	this.timerService.shutdown();
        }
    }

    public void setKnowledgeRuntime(InternalKnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
        this.processRuntime = createProcessRuntime();
    }

    public InternalKnowledgeRuntime getKnowledgeRuntime() {
        return this.kruntime;
    }

    public void registerChannel(String name,
                                Channel channel) {
        getChannels().put(name, channel);
    }

    public void unregisterChannel(String name) {
        if (channels != null) channels.remove( name );
    }

    public Map<String, Channel> getChannels() {
        if (channels == null) channels = new ConcurrentHashMap<String, Channel>();
        return channels;
    }

    public Map<String, WorkingMemoryEntryPoint> getEntryPoints() {
        return this.entryPoints;
    }

    public long getFactCount() {
        return getObjectStore().size();
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

    public void prepareToFireActivation() {
    }

    public void activationFired() {
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

    public ObjectMarshallingStrategyStore getObjectMarshallingStrategyStore() {
        if ( this.marshallingStore == null ) {
            this.marshallingStore = new ObjectMarshallingStrategyStoreImpl( (ObjectMarshallingStrategy[]) this.environment.get( EnvironmentName.OBJECT_MARSHALLING_STRATEGIES ) );
        }
        return this.marshallingStore;
    }

}
