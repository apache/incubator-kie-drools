/*
 * Copyright 2010 JBoss Inc
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

package org.drools.core.impl;

import org.drools.core.QueryResultsImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.SessionConfiguration;
import org.drools.core.SessionConfigurationImpl;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.CalendarsImpl;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.InternalViewChangedEventListener;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.base.NonCloningQueryViewListener;
import org.drools.core.base.QueryRowWithSubruleIndex;
import org.drools.core.base.StandardQueryViewChangedEventListener;
import org.drools.core.command.impl.FixedKnowledgeCommandContext;
import org.drools.core.command.impl.GenericCommand;
import org.drools.core.command.impl.KnowledgeCommandContext;
import org.drools.core.command.runtime.BatchExecutionCommandImpl;
import org.drools.core.common.BaseNode;
import org.drools.core.common.ConcurrentNodeMemories;
import org.drools.core.common.DefaultAgenda;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EndOperationListener;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.EventSupport;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryActions;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.ProcessEventSupport;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.RuleRuntimeEventSupport;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.phreak.PropagationList;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.phreak.SynchronizedBypassPropagationList;
import org.drools.core.phreak.SynchronizedPropagationList;
import org.drools.core.reteoo.ClassObjectTypeConf;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.InitialFactImpl;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EntryPointId;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.drools.core.runtime.rule.impl.LiveQueryImpl;
import org.drools.core.runtime.rule.impl.OpenQueryViewChangedEventListenerAdapter;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AsyncExceptionHandler;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.GlobalResolver;
import org.drools.core.spi.PropagationContext;
import org.drools.core.time.TimerService;
import org.drools.core.time.TimerServiceFactory;
import org.drools.core.type.DateFormats;
import org.drools.core.type.DateFormatsImpl;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.command.Command;
import org.kie.api.event.KieRuntimeEventManager;
import org.kie.api.event.kiebase.KieBaseEventListener;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessEventManager;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.api.runtime.rule.EntryPoint;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.command.Context;
import org.kie.internal.event.rule.RuleEventListener;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.KieRuntimeService;
import org.kie.internal.runtime.KieRuntimes;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.internal.runtime.beliefs.Mode;
import org.kie.internal.utils.ServiceRegistryImpl;

import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;

public class StatefulKnowledgeSessionImpl extends AbstractRuntime
        implements
        StatefulKnowledgeSession,
        InternalWorkingMemoryEntryPoint,
        InternalKnowledgeRuntime,
        KieSession,
        KieRuntimeEventManager,
        InternalWorkingMemoryActions,
        EventSupport,
        ProcessEventManager,
        CorrelationAwareProcessRuntime,
        Externalizable {

    private static final String ERRORMSG = "Illegal method call. This session was previously disposed.";

    private static final long serialVersionUID = 510l;
    public    byte[] bytes;
    protected Long    id;

    /** The actual memory for the <code>JoinNode</code>s. */
    private NodeMemories nodeMemories;

    protected NamedEntryPoint defaultEntryPoint;

    /** Global values which are associated with this memory. */
    protected GlobalResolver globalResolver;

    protected Calendars   calendars;
    protected DateFormats dateFormats;

    /** The eventSupport */
    protected RuleRuntimeEventSupport ruleRuntimeEventSupport;

    protected RuleEventListenerSupport ruleEventListenerSupport;

    protected AgendaEventSupport agendaEventSupport;

    protected List<KieBaseEventListener> kieBaseEventListeners;

    /** The <code>RuleBase</code> with which this memory is associated. */
    protected transient InternalKnowledgeBase kBase;

    protected FactHandleFactory handleFactory;

    /** Rule-firing agenda. */
    protected InternalAgenda agenda;

    protected ReentrantLock lock;

    /**
     * This must be thread safe as it is incremented and read via different
     * EntryPoints
     */
    protected AtomicLong propagationIdCounter;

    private boolean sequential;

    /** Flag to determine if a rule is currently being fired. */
    protected volatile AtomicBoolean firing;

    private WorkItemManager workItemManager;

    private TimerService timerService;

    protected Map<String, WorkingMemoryEntryPoint> entryPoints;

    protected volatile InternalFactHandle initialFactHandle;

    protected PropagationContextFactory pctxFactory;

    protected SessionConfiguration config;

    private Map<String, Channel> channels;

    private Environment environment;

    private ExecutionResults batchExecutionResult;

    // this is a counter of concurrent operations happening. When this counter is zero,
    // the engine is idle.
    private AtomicLong opCounter;
    // this is the timestamp of the end of the last operation, based on the session clock,
    // or -1 if there are operation being executed at this moment
    private AtomicLong lastIdleTimestamp;

    private InternalProcessRuntime processRuntime;

    private Map<String, Object> runtimeServices;

    private boolean alive = true;

    protected PropagationList propagationList;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    public StatefulKnowledgeSessionImpl() {

    }


    public StatefulKnowledgeSessionImpl(final long id,
                                        final InternalKnowledgeBase kBase) {
        this(id,
             kBase,
             true,
             SessionConfigurationImpl.getDefaultInstance(),
             EnvironmentFactory.newEnvironment());
    }

    public StatefulKnowledgeSessionImpl(final long id,
                                        final InternalKnowledgeBase kBase,
                                        boolean initInitFactHandle,
                                        final SessionConfiguration config,
                                        final Environment environment) {
        this(id,
             kBase,
             kBase.newFactHandleFactory(),
             initInitFactHandle,
             1,
             config,
             environment,
             new RuleRuntimeEventSupport(),
             new AgendaEventSupport(),
             new RuleEventListenerSupport(),
             null);
    }

    public StatefulKnowledgeSessionImpl(final long id,
                                        final InternalKnowledgeBase kBase,
                                        final FactHandleFactory handleFactory,
                                        final long propagationContext,
                                        final SessionConfiguration config,
                                        final InternalAgenda agenda,
                                        final Environment environment) {
        this(id,
             kBase,
             handleFactory,
             false,
             propagationContext,
             config,
             environment,
             new RuleRuntimeEventSupport(),
             new AgendaEventSupport(),
             new RuleEventListenerSupport(),
             agenda);
    }


    public StatefulKnowledgeSessionImpl(final long id,
                                        final InternalKnowledgeBase kBase,
                                        final FactHandleFactory handleFactory,
                                        final boolean initInitFactHandle,
                                        final long propagationContext,
                                        final SessionConfiguration config,
                                        final Environment environment,
                                        final RuleRuntimeEventSupport workingMemoryEventSupport,
                                        final AgendaEventSupport agendaEventSupport,
                                        final RuleEventListenerSupport ruleEventListenerSupport,
                                        final InternalAgenda agenda) {
        this.id = id;
        this.config = config;
        this.kBase = kBase;
        this.handleFactory = handleFactory;
        this.pctxFactory = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        this.environment = environment;
        this.ruleRuntimeEventSupport = workingMemoryEventSupport;
        this.agendaEventSupport = agendaEventSupport;
        this.ruleEventListenerSupport = ruleEventListenerSupport;

        init();

        this.propagationIdCounter = new AtomicLong(propagationContext);

        if (agenda == null) {
            this.agenda = kBase.getConfiguration().getComponentFactory().getAgendaFactory().createAgenda(kBase);
        } else {
            this.agenda = agenda;
        }
        this.agenda.setWorkingMemory(this);


        nodeMemories = new ConcurrentNodeMemories(this.kBase);

        Globals globals = (Globals) this.environment.get(EnvironmentName.GLOBALS);
        if (globals != null) {
            if (!(globals instanceof GlobalResolver)) {
                this.globalResolver = new GlobalsAdapter(globals);
            } else {
                this.globalResolver = (GlobalResolver) globals;
            }
        } else {
            this.globalResolver = new MapGlobalResolver();
        }

        final RuleBaseConfiguration conf = kBase.getConfiguration();

        this.sequential = conf.isSequential();

        this.kieBaseEventListeners = new LinkedList<KieBaseEventListener>();
        this.lock = new ReentrantLock();

        timerService = TimerServiceFactory.getTimerService(this.config);

        this.firing = new AtomicBoolean(false);

        initTransient();

        this.opCounter = new AtomicLong(0);
        this.lastIdleTimestamp = new AtomicLong(-1);

        initManagementBeans();

        if (initInitFactHandle) {
            initInitialFact(kBase, null);
        }
    }

    protected void init() {
        if (config.hasForceEagerActivationFilter()) {
            this.propagationList = new SynchronizedBypassPropagationList(this);
        } else {
            this.propagationList = new SynchronizedPropagationList(this);
        }
    }

    @Override
    public TruthMaintenanceSystem getTruthMaintenanceSystem() {
        return defaultEntryPoint.getTruthMaintenanceSystem();
    }

    @Override
    public FactHandleFactory getHandleFactory() {
        return handleFactory;
    }

    public <T> T getKieRuntime(Class<T> cls) {
        //  Only ever one KieRuntimeManager is created, using the two-tone pattern.

        T runtime;
        if (runtimeServices == null) {
            runtime = createRuntimeService(cls);
        } else {
            runtime = (T) runtimeServices.get(cls.getName());
            if (runtime == null) {
                runtime = createRuntimeService(cls);
            }
        }

        return runtime;
    }

    public synchronized <T> T createRuntimeService(Class<T> cls) {
        // This is sychronized to ensure that only ever one is created, using the two-tone pattern.
        if (runtimeServices == null) {
            runtimeServices = new HashMap<String, Object>();
        }

        T runtime = (T) runtimeServices.get(cls.getName());
        if (runtime == null) {
            KieRuntimes runtimes = ServiceRegistryImpl.getInstance().get(KieRuntimes.class);

            KieRuntimeService service = (KieRuntimeService) runtimes.getRuntimes().get(cls.getName());
            runtime  = (T) service.newKieRuntime(this);
        }

        return runtime;
    }

    public EntryPoint getEntryPoint(String name) {
        return getWorkingMemoryEntryPoint(name);
    }

    public Collection<? extends org.kie.api.runtime.rule.EntryPoint> getEntryPoints() {
        return this.entryPoints.values();
    }

    public Map<String, WorkingMemoryEntryPoint> getEntryPointMap() {
        return this.entryPoints;
    }

    public Collection<RuleRuntimeEventListener> getRuleRuntimeEventListeners() {
        return Collections.unmodifiableCollection(ruleRuntimeEventSupport.getEventListeners());
    }

    public Collection<AgendaEventListener> getAgendaEventListeners() {
        return Collections.unmodifiableCollection(this.agendaEventSupport.getEventListeners());
    }

    private InternalProcessRuntime createProcessRuntime() {
        InternalProcessRuntime processRuntime = ProcessRuntimeFactory.newProcessRuntime(this);
        if (processRuntime == null) {
            processRuntime = DUMMY_PROCESS_RUNTIME;
            throw new RuntimeException( "There is no ProcessRuntime available: are jBPM libraries missing on classpath?" );
        }
        return processRuntime;
    }

    public InternalProcessRuntime getProcessRuntime() {
        if (processRuntime == null) {
            synchronized(this) {
                if (processRuntime == null) {
                    this.processRuntime = createProcessRuntime();
                }
            }
        }
        return this.processRuntime;
    }

    public InternalProcessRuntime internalGetProcessRuntime() {
        return this.processRuntime;
    }

    public void addEventListener(ProcessEventListener listener) {
        getProcessRuntime().addEventListener(listener);
    }

    public Collection<ProcessEventListener> getProcessEventListeners() {
        return getProcessRuntime().getProcessEventListeners();
    }

    public void removeEventListener(ProcessEventListener listener) {
        getProcessRuntime().removeEventListener(listener);
    }

    public KnowledgeBase getKieBase() {
        return this.kBase;
    }

    public void dispose() {
        if (logger != null) {
            try {
                logger.close();
            } catch (Exception e) { /* the logger was already closed, swallow */ }
        }

        for (WorkingMemoryEntryPoint ep : this.entryPoints.values()) {
            ep.dispose();
        }
        this.ruleRuntimeEventSupport.reset();
        this.agendaEventSupport.reset();
        for (KieBaseEventListener listener : kieBaseEventListeners) {
            this.kBase.removeEventListener(listener);
        }
        if (processRuntime != null) {
            this.processRuntime.dispose();
        }
        if (timerService != null) {
            this.timerService.shutdown();
        }

        if (this.workItemManager != null) {
            ((org.drools.core.process.instance.WorkItemManager)this.workItemManager).dispose();
        }

        alive = false;

        this.kBase.disposeStatefulSession( this );
        if (this.kBase.getConfiguration().isMBeansEnabled()) {
            DroolsManagementAgent.getInstance().unregisterKnowledgeSession(this);
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void destroy() {
        dispose();
    }

    public void update(FactHandle factHandle) {
        this.update(factHandle,
                    ((InternalFactHandle) factHandle).getObject());
    }

    public void abortProcessInstance(long id) {
        this.getProcessRuntime().abortProcessInstance(id);
    }

    public void signalEvent(String type,
                            Object event) {
        this.getProcessRuntime().signalEvent( type, event );
    }

    public void signalEvent(String type,
                            Object event,
                            long processInstanceId) {
        this.getProcessRuntime().signalEvent( type, event, processInstanceId );
    }

    public Globals getGlobals() {
        return (Globals) this.getGlobalResolver();
    }

    public <T extends FactHandle> Collection<T> getFactHandles() {
        return new ObjectStoreWrapper( getObjectStore(),
                                       null,
                                       ObjectStoreWrapper.FACT_HANDLE );
    }

    public <T extends FactHandle> Collection<T> getFactHandles(org.kie.api.runtime.ObjectFilter filter) {
        return new ObjectStoreWrapper( getObjectStore(),
                                       filter,
                                       ObjectStoreWrapper.FACT_HANDLE );
    }

    public Collection<?> getObjects() {
        return new ObjectStoreWrapper( getObjectStore(),
                                       null,
                                       ObjectStoreWrapper.OBJECT );
    }

    public Collection<?> getObjects(org.kie.api.runtime.ObjectFilter filter) {
        return new ObjectStoreWrapper( getObjectStore(),
                                       filter,
                                       ObjectStoreWrapper.OBJECT );
    }

    public static abstract class AbstractImmutableCollection
            implements
            Collection {

        public boolean add(Object o) {
            throw new UnsupportedOperationException( "This is an immmutable Collection" );
        }

        public boolean addAll(Collection c) {
            throw new UnsupportedOperationException( "This is an immmutable Collection" );
        }

        public void clear() {
            throw new UnsupportedOperationException( "This is an immmutable Collection" );
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException( "This is an immmutable Collection" );
        }

        public boolean removeAll(Collection c) {
            throw new UnsupportedOperationException( "This is an immmutable Collection" );
        }

        public boolean retainAll(Collection c) {
            throw new UnsupportedOperationException( "This is an immmutable Collection" );
        }
    }

    public static class ObjectStoreWrapper extends AbstractImmutableCollection {
        public ObjectStore                     store;
        public org.kie.api.runtime.ObjectFilter filter;
        public int                             type;           // 0 == object, 1 == facthandle
        public static final int                OBJECT      = 0;
        public static final int                FACT_HANDLE = 1;

        public ObjectStoreWrapper(ObjectStore store,
                                  org.kie.api.runtime.ObjectFilter filter,
                                  int type) {
            this.store = store;
            this.filter = filter;
            this.type = type;
        }

        public boolean contains(Object object) {
            if ( object instanceof FactHandle ) {
                return this.store.getObjectForHandle( (InternalFactHandle) object ) != null;
            } else {
                return this.store.getHandleForObject( object ) != null;
            }
        }

        public boolean containsAll(Collection c) {
            for ( Object object : c ) {
                if ( !contains( object ) ) {
                    return false;
                }
            }
            return true;
        }

        public boolean isEmpty() {
            if ( this.filter == null ) {
                return this.store.isEmpty();
            }

            return size() == 0;
        }

        public int size() {
            if ( this.filter == null ) {
                return this.store.size();
            }

            int i = 0;
            for (Object o : this) {
                i++;
            }

            return i;
        }

        public Iterator< ? > iterator() {
            Iterator it;
            if ( type == OBJECT ) {
                if ( filter != null ) {
                    it = store.iterateObjects( filter );
                } else {
                    it = store.iterateObjects();
                }
            } else {
                if ( filter != null ) {
                    it = store.iterateFactHandles( filter );
                } else {
                    it = store.iterateFactHandles();
                }
            }
            return it;
        }

        public Object[] toArray() {
            if ( type == FACT_HANDLE ) {
                return toArray( new InternalFactHandle[size()] );
            } else {
                return toArray( new Object[size()] );
            }

        }

        public Object[] toArray(Object[] array) {
            if ( array == null || array.length != size() ) {
                if ( type == FACT_HANDLE ) {
                    array = new InternalFactHandle[size()];
                } else {
                    array = new Object[size()];
                }
            }

            int i = 0;
            for (Object o : this) {
                array[i++] = o;
            }

            return array;
        }
    }

    public <T> T execute(Command<T> command) {
        return execute( null,
                        command );
    }

    public <T> T execute(Context context,
                         Command<T> command) {

        ExecutionResultImpl results = null;
        if ( context != null ) {
            results = (ExecutionResultImpl) ((KnowledgeCommandContext) context).getExecutionResults();
        }

        if ( results == null ) {
            results = new ExecutionResultImpl();
        }

        if ( !(command instanceof BatchExecutionCommandImpl) ) {
            return (T) ((GenericCommand) command).execute( new FixedKnowledgeCommandContext( context,
                                                                                             null,
                                                                                             this.kBase,
                                                                                             this,
                                                                                             results ) );
        }

        try {
            startBatchExecution( results );
            ((GenericCommand) command).execute( new FixedKnowledgeCommandContext( context,
                                                                                  null,
                                                                                  this.kBase,
                                                                                  this,
                                                                                  results ) );
            ExecutionResults result = getExecutionResult();
            return (T) result;
        } finally {
            endBatchExecution();
            if (kBase.flushModifications()) {
                fireAllRules();
            }
        }
    }

    public void initInitialFact(InternalKnowledgeBase kBase, MarshallerReaderContext context) {
        this.initialFactHandle = new DefaultFactHandle(0, InitialFactImpl.getInstance(), 0,  defaultEntryPoint );

        ClassObjectTypeConf otc = (ClassObjectTypeConf) defaultEntryPoint.getObjectTypeConfigurationRegistry()
                                                                         .getObjectTypeConf(defaultEntryPoint.getEntryPoint(),
                                                                                            initialFactHandle.getObject());
        PropagationContextFactory ctxFact = kBase.getConfiguration().getComponentFactory().getPropagationContextFactory();


        final PropagationContext pctx = ctxFact.createPropagationContext(0, PropagationContext.INSERTION, null,
                                                                         null , initialFactHandle, defaultEntryPoint.getEntryPoint(),
                                                                         context );

        otc.getConcreteObjectTypeNode().assertInitialFact(this.initialFactHandle, pctx, this);
    }

    private void initManagementBeans() {
        if ( this.kBase.getConfiguration().isMBeansEnabled() ) {
            DroolsManagementAgent.getInstance().registerKnowledgeSession( this );
        }
    }

    public String getEntryPointId() {
        return EntryPointId.DEFAULT.getEntryPointId();
    }

    public QueryResultsImpl getQueryResults(String queryName, Object... arguments) {

        try {
            startOperation();

            this.lock.lock();
            this.kBase.readLock();

            this.kBase.executeQueuedActions();
            flushPropagations();

            DroolsQuery queryObject = new DroolsQuery( queryName,
                                                       arguments,
                                                       getQueryListenerInstance(),
                                                       false ,
                                                       null,
                                                       null,
                                                       null,
                                                       null,
                                                       null );

            InternalFactHandle handle = this.handleFactory.newFactHandle( queryObject,
                                                                          null,
                                                                          this,
                                                                          this );

            final PropagationContext pCtx = pctxFactory.createPropagationContext(getNextPropagationIdCounter(), PropagationContext.INSERTION,
                                                                                 null, null, handle, getEntryPoint());


            BaseNode[] tnodes = evalQuery(queryName, queryObject, handle, pCtx);

            List<Map<String, Declaration>> decls = new ArrayList<Map<String, Declaration>>();
            if ( tnodes != null ) {
                for ( BaseNode node : tnodes ) {
                    decls.add( ((QueryTerminalNode) node).getSubRule().getOuterDeclarations() );
                }
            }

            this.handleFactory.destroyFactHandle( handle);

            return new QueryResultsImpl( (List<QueryRowWithSubruleIndex>) queryObject.getQueryResultCollector().getResults(),
                                         decls.toArray( new Map[decls.size()] ),
                                         this,
                                         ( queryObject.getQuery() != null ) ? queryObject.getQuery().getParameters()  : new Declaration[0] );
        } finally {
            this.kBase.readUnlock();
            this.lock.unlock();
            endOperation();
        }
    }

    private InternalViewChangedEventListener getQueryListenerInstance() {
        switch ( this.config.getQueryListenerOption() ) {
            case STANDARD :
                return new StandardQueryViewChangedEventListener();
            case LIGHTWEIGHT :
                return new NonCloningQueryViewListener();
        }
        return null;
    }

    public LiveQuery openLiveQuery(final String query,
                                   final Object[] arguments,
                                   final ViewChangedEventListener listener) {

        try {
            startOperation();
            this.kBase.readLock();
            this.lock.lock();

            this.kBase.executeQueuedActions();
            flushPropagations();

            DroolsQuery queryObject = new DroolsQuery( query,
                                                       arguments,
                                                       new OpenQueryViewChangedEventListenerAdapter( listener ),
                                                       true,
                                                       null,
                                                       null,
                                                       null,
                                                       null,
                                                       null);
            InternalFactHandle handle = this.handleFactory.newFactHandle(queryObject,
                                                                         null,
                                                                         this,
                                                                         this);

            final PropagationContext pCtx = pctxFactory.createPropagationContext(getNextPropagationIdCounter(), PropagationContext.INSERTION,
                                                                                 null, null, handle, getEntryPoint());

            evalQuery( queryObject.getName(), queryObject, handle, pCtx );

            return new LiveQueryImpl( this,
                                      handle );
        } finally {
            this.lock.unlock();
            this.kBase.readUnlock();
            endOperation();
        }
    }

    protected BaseNode[] evalQuery(String queryName, DroolsQuery queryObject, InternalFactHandle handle, PropagationContext pCtx) {
        BaseNode[] tnodes = kBase.getReteooBuilder().getTerminalNodes(queryName);
        if ( tnodes == null ) {
            throw new RuntimeException( "Query '" + queryName + "' does not exist");
        }

        QueryTerminalNode tnode = ( QueryTerminalNode )  tnodes[0];
        LeftTupleSource lts = tnode.getLeftTupleSource();
        while ( lts.getType() != NodeTypeEnums.LeftInputAdapterNode ) {
            lts = lts.getLeftTupleSource();
        }
        LeftInputAdapterNode lian = ( LeftInputAdapterNode ) lts;
        LeftInputAdapterNode.LiaNodeMemory lmem = (LeftInputAdapterNode.LiaNodeMemory) getNodeMemory( (MemoryFactory) lts);
        if ( lmem.getSegmentMemory() == null) {
            SegmentUtilities.createSegmentMemory(lts, this);
        }

        LeftInputAdapterNode.doInsertObject( handle, pCtx, lian, this, lmem, false, queryObject.isOpen() );

        for ( PathMemory rm : lmem.getSegmentMemory().getPathMemories() ) {
            RuleAgendaItem evaluator = agenda.createRuleAgendaItem( Integer.MAX_VALUE, rm, (TerminalNode) rm.getNetworkNode() );
            evaluator.getRuleExecutor().setDirty( true );
            evaluator.getRuleExecutor().evaluateNetworkAndFire( this, null, 0, -1 );
        }

        return tnodes;
    }

    public void closeLiveQuery(final InternalFactHandle factHandle) {

        try {
            startOperation();
            this.kBase.readLock();
            this.lock.lock();

            LeftInputAdapterNode lian = ( LeftInputAdapterNode ) factHandle.getFirstLeftTuple().getLeftTupleSink().getLeftTupleSource();
            LeftInputAdapterNode.LiaNodeMemory lmem = (LeftInputAdapterNode.LiaNodeMemory) getNodeMemory(lian);
            SegmentMemory lsmem = lmem.getSegmentMemory();

            LeftTuple childLeftTuple = factHandle.getFirstLeftTuple(); // there is only one, all other LTs are peers
            LeftInputAdapterNode.doDeleteObject( childLeftTuple, childLeftTuple.getPropagationContext(),  lsmem, this, lian, false, lmem );

            for ( PathMemory rm : lmem.getSegmentMemory().getPathMemories() ) {
                RuleAgendaItem evaluator = agenda.createRuleAgendaItem( Integer.MAX_VALUE, rm, (TerminalNode) rm.getNetworkNode() );
                evaluator.getRuleExecutor().setDirty( true );
                evaluator.getRuleExecutor().evaluateNetworkAndFire( this, null, 0, -1 );
            }

            getFactHandleFactory().destroyFactHandle( factHandle );

        } finally {
            this.lock.unlock();
            this.kBase.readUnlock();
            endOperation();
        }
    }

    public EntryPointId getEntryPoint() {
        return this.defaultEntryPoint.getEntryPoint();
    }

    public InternalWorkingMemory getInternalWorkingMemory() {
        return this;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        // all we do is create marshall to a byte[] and write to the stream
        StatefulKnowledgeSession ksession = (StatefulKnowledgeSession) getKnowledgeRuntime();

        Marshaller marshaller = MarshallerFactory.newMarshaller(ksession.getKieBase(), new ObjectMarshallingStrategy[]{MarshallerFactory.newSerializeMarshallingStrategy()});

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        marshaller.marshall( stream, (StatefulKnowledgeSession) getKnowledgeRuntime() );
        stream.close();

        byte[] bytes = stream.toByteArray();
        out.writeInt( bytes.length );
        out.write(bytes);
    }

    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        bytes = new byte[ in.readInt() ];
        in.readFully( bytes );
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

        public void removeGlobal(String identifier) {
            ((GlobalResolver)globals).removeGlobal( identifier );
        }

        @Override
        public void clear() {
            if (globals instanceof GlobalResolver) {
                ((GlobalResolver)globals).clear();
            }
        }
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    public void updateEntryPointsCache() {
        if (kBase.getAddedEntryNodeCache() != null) {
            for (EntryPointNode addedNode : kBase.getAddedEntryNodeCache()) {
                EntryPointId id = addedNode.getEntryPoint();
                if (EntryPointId.DEFAULT.equals(id)) continue;
                WorkingMemoryEntryPoint wmEntryPoint = new NamedEntryPoint(id, addedNode, this);
                entryPoints.put(id.getEntryPointId(), wmEntryPoint);
            }
        }

        if (kBase.getRemovedEntryNodeCache() != null) {
            for (EntryPointNode removedNode : kBase.getRemovedEntryNodeCache()) {
                entryPoints.remove(removedNode.getEntryPoint().getEntryPointId());
            }
        }
    }

    private void initTransient() {
        EntryPointNode epn = this.kBase.getRete().getEntryPointNode( EntryPointId.DEFAULT );

        this.defaultEntryPoint = new NamedEntryPoint( EntryPointId.DEFAULT,
                                                      epn,
                                                      this );

        this.entryPoints = new ConcurrentHashMap<String, WorkingMemoryEntryPoint>();

        this.entryPoints.put("DEFAULT",
                             this.defaultEntryPoint);

        updateEntryPointsCache();
    }

    public SessionConfiguration getSessionConfiguration() {
        return this.config;
    }

    public void reset() {
        propagationList.reset();

        if (nodeMemories != null) {
            nodeMemories.resetAllMemories( this );
        }

        ((DefaultAgenda)this.agenda).reset();

        this.globalResolver.clear();
        this.kieBaseEventListeners.clear();
        this.handleFactory.clear( 0, 0 );
        this.propagationIdCounter.set(0);
        this.opCounter.set(0);
        this.lastIdleTimestamp.set( -1 );

        initTransient();

        timerService = TimerServiceFactory.getTimerService(this.config);

        this.processRuntime = null;

        initInitialFact(kBase, null);

        alive = true;
    }

    public void reset(int handleId,
                      long handleCounter,
                      long propagationCounter) {
        propagationList.reset();

        if (nodeMemories != null) {
            nodeMemories.clear();
        }
        this.agenda.clear();

        for ( WorkingMemoryEntryPoint ep : this.entryPoints.values() ) {
            // clear the state for each entry point
            InternalWorkingMemoryEntryPoint iep = (InternalWorkingMemoryEntryPoint) ep;
            iep.reset();
        }

        this.handleFactory.clear( handleId,
                                  handleCounter);

        this.propagationIdCounter = new AtomicLong( propagationCounter );
        this.opCounter.set( 0 );
        this.lastIdleTimestamp.set(-1);

        // TODO should these be cleared?
        // we probably neeed to do CEP and Flow timers too
        // this.processInstanceManager.clear()
        // this.workItemManager.clear();
    }

    public void setRuleRuntimeEventSupport(RuleRuntimeEventSupport ruleRuntimeEventSupport) {
        this.ruleRuntimeEventSupport = ruleRuntimeEventSupport;
    }

    public void setAgendaEventSupport(AgendaEventSupport agendaEventSupport) {
        this.agendaEventSupport = agendaEventSupport;
    }

    public boolean isSequential() {
        return this.sequential;
    }

    public void addEventListener(final RuleRuntimeEventListener listener) {
        this.ruleRuntimeEventSupport.addEventListener( listener );
    }

    public void removeEventListener(final RuleRuntimeEventListener listener) {
        this.ruleRuntimeEventSupport.removeEventListener( listener );
    }

    public void addEventListener(final AgendaEventListener listener) {
        this.agendaEventSupport.addEventListener( listener );
    }

    public void removeEventListener(final AgendaEventListener listener) {
        this.agendaEventSupport.removeEventListener( listener );
    }

    public void addEventListener(KieBaseEventListener listener) {
        this.kBase.addEventListener(listener);
        this.kieBaseEventListeners.add(listener);
    }

    public Collection<KieBaseEventListener> getKieBaseEventListeners() {
        return Collections.unmodifiableCollection( kieBaseEventListeners );
    }

    public void removeEventListener(KieBaseEventListener listener) {
        this.kBase.removeEventListener( listener);
        this.kieBaseEventListeners.remove( listener );
    }

    ///RuleEventListenerSupport
    public void addEventListener(final RuleEventListener listener) {
        this.ruleEventListenerSupport.addEventListener( listener );
    }

    public void removeEventListener(final RuleEventListener listener) {
        this.ruleEventListenerSupport.removeEventListener(listener);
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
            this.kBase.readLock();
            startOperation();
            // Make sure the global has been declared in the RuleBase
            final Map globalDefintions = this.kBase.getGlobals();
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
            this.kBase.readUnlock();
        }
    }

    public void removeGlobal(String identifier) {
        this.globalResolver.removeGlobal( identifier );
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
        if (this.calendars == null) {
            this.calendars = new CalendarsImpl();
        }
        return this.calendars;
    }

    public DateFormats getDateFormats() {
        if (this.dateFormats == null) {
            this.dateFormats = (DateFormats) this.environment.get( EnvironmentName.DATE_FORMATS );
            if ( this.dateFormats == null ) {
                this.dateFormats = new DateFormatsImpl();
                this.environment.set( EnvironmentName.DATE_FORMATS,
                                      this.dateFormats );
            }
        }
        return this.dateFormats;
    }

    public int getId() {
        checkAlive();
        return this.id.intValue();
    }

    public long getIdentifier() {
        checkAlive();
        return this.id;
    }

    public void setId(Long id) {
        checkAlive();
        this.id = id;
    }

    protected void checkAlive() {
        if (!alive) {
            throw new IllegalStateException( ERRORMSG );
        }
    }

    public Object getGlobal(final String identifier) {
        return this.globalResolver.resolveGlobal( identifier );
    }

    public Environment getEnvironment() {
        return this.environment;
    }

    public InternalAgenda getAgenda() {
        return this.agenda;
    }

    public void clearAgenda() {
        this.agenda.clearAndCancel();
    }

    public void clearAgendaGroup(final String group) {
        this.agenda.clearAndCancelAgendaGroup(group);
    }

    public void clearActivationGroup(final String group) {
        this.agenda.clearAndCancelActivationGroup(group);
    }

    public void clearRuleFlowGroup(final String group) {
        this.agenda.clearAndCancelRuleFlowGroup(group);
    }

    public InternalKnowledgeBase getKnowledgeBase() {
        return this.kBase;
    }

    public void halt() {
        this.agenda.halt();
        notifyHalt();
    }

    public int fireAllRules() {
        return fireAllRules( null,
                             -1 );
    }

    public int fireAllRules(int fireLimit) {
        return fireAllRules( null,
                             fireLimit );
    }

    public int fireAllRules(final AgendaFilter agendaFilter) {
        return fireAllRules( agendaFilter,
                             -1 );
    }

    public int fireAllRules(final AgendaFilter agendaFilter,
                            int fireLimit) {
        checkAlive();
        if ( this.firing.compareAndSet( false,
                                        true ) ) {
            try {
                startOperation();
                return internalFireAllRules(agendaFilter, fireLimit);
            } finally {
                endOperation();
                this.firing.set( false );
            }
        }
        return 0;
    }

    private int internalFireAllRules(AgendaFilter agendaFilter, int fireLimit) {
        int fireCount = 0;
        try {
            kBase.readLock();
            fireCount = this.agenda.fireAllRules( agendaFilter, fireLimit );
        } finally {
            kBase.readUnlock();
            if (kBase.flushModifications()) {
                fireCount += internalFireAllRules(agendaFilter, fireLimit);
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

        if ( this.firing.compareAndSet( false, true ) ) {
            try {
                startOperation();
                agenda.fireUntilHalt( agendaFilter );
            } finally {
                endOperation();
                this.firing.set( false );
            }
        }
    }

    /**
     * Returns the fact Object for the given <code>FactHandle</code>. It
     * actually attempts to return the value from the handle, before retrieving
     * it from objects map.
     *
     * @see org.drools.core.WorkingMemory
     *
     * @param handle
     *            The <code>FactHandle</code> reference for the
     *            <code>Object</code> lookup
     */
    public Object getObject(FactHandle handle) {
        // the handle might have been disconnected, so reconnect if it has
        if ( ((InternalFactHandle)handle).isDisconnected() ) {
            handle = this.defaultEntryPoint.getObjectStore().reconnect( (InternalFactHandle)handle );
        }
        return this.defaultEntryPoint.getObject(handle);
    }

    public ObjectStore getObjectStore() {
        return this.defaultEntryPoint.getObjectStore();
    }

    /**
     * @see org.drools.core.WorkingMemory
     */
    public FactHandle getFactHandle(final Object object) {
        return this.defaultEntryPoint.getFactHandle(object);
    }

    /**
     * @see org.drools.core.WorkingMemory
     */
    public FactHandle getFactHandleByIdentity(final Object object) {
        return getObjectStore().getHandleForObjectIdentity(object);
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
    public Iterator iterateObjects(org.kie.api.runtime.ObjectFilter filter) {
        return getObjectStore().iterateObjects(filter);
    }

    /**
     * This class is not thread safe, changes to the working memory during
     * iteration may give unexpected results
     */
    public Iterator<InternalFactHandle> iterateFactHandles() {
        return getObjectStore().iterateFactHandles();
    }

    /**
     * This class is not thread safe, changes to the working memory during
     * iteration may give unexpected results
     */
    public Iterator<InternalFactHandle> iterateFactHandles(org.kie.api.runtime.ObjectFilter filter) {
        return getObjectStore().iterateFactHandles(filter);
    }

    public void setFocus(final String focus) {
        this.agenda.setFocus( focus );
    }

    /**
     * @see org.drools.core.WorkingMemory
     */
    public FactHandle insert(final Object object) {
        return insert( object, /* Not-Dynamic */
                       null,
                       false,
                       false,
                       null,
                       null );
    }

    public FactHandle insert(final Object object,
                                             final boolean dynamic) {
        return insert( object,
                       null,
                       dynamic,
                       false,
                       null,
                       null );
    }

    @Override
    public void updateTraits( InternalFactHandle h, BitMask mask, Class<?> modifiedClass, Activation activation ) {
        this.defaultEntryPoint.getTraitHelper().updateTraits(h, mask, modifiedClass, activation );
    }

    @Override
    public <T, K, X extends TraitableBean> Thing<K> shed( Activation activation, TraitableBean<K, X> core, Class<T> trait ) {
        return this.defaultEntryPoint.getTraitHelper().shed(core, trait, activation);
    }

    @Override
    public <T, K> T don( Activation activation, K core, Collection<Class<? extends Thing>> traits, boolean b, Mode[] modes ) {
        return this.defaultEntryPoint.getTraitHelper().don(activation, core, traits, b, modes);
    }

    @Override
    public <T, K> T don( Activation activation, K core, Class<T> trait, boolean b, Mode[] modes ) {
        return this.defaultEntryPoint.getTraitHelper().don(activation, core, trait, b, modes);
    }

    public FactHandle insert(final Object object,
                                             final Object tmsValue,
                             final boolean dynamic,
                                             boolean logical,
                                             final RuleImpl rule,
                                             final Activation activation) {
        checkAlive();
        return this.defaultEntryPoint.insert(object,
                                             dynamic,
                                             rule,
                                             activation);
    }

    public void insert(final InternalFactHandle handle,
                       final Object object,
                       final RuleImpl rule,
                       final Activation activation,
                       ObjectTypeConf typeConf) {
        this.defaultEntryPoint.insert(handle,
                                      object,
                                      rule,
                                      activation,
                                      typeConf,
                                      null);
    }

    public void retract(final FactHandle handle) {
        delete(handle,
               null,
               null);
    }

    public void delete(final FactHandle handle) {
        delete(handle,
               null,
               null);
    }

    public void delete(final FactHandle factHandle,
                       final RuleImpl rule,
                       final Activation activation) {
        checkAlive();
        this.defaultEntryPoint.delete(factHandle,
                                      rule,
                                      activation);
    }

    public EntryPointNode getEntryPointNode() {
        return this.defaultEntryPoint.getEntryPointNode();
    }

    public void update(final FactHandle handle,
                       final Object object) {
        update(handle,
               object,
               allSetButTraitBitMask(),
               Object.class,
               null);
    }

    /**
     * modify is implemented as half way retract / assert due to the truth
     * maintenance issues.
     *
     * @see org.drools.core.WorkingMemory
     */
    public void update(FactHandle factHandle,
                       final Object object,
                       final BitMask mask,
                       Class<?> modifiedClass,
                       final Activation activation) {
        checkAlive();
        this.defaultEntryPoint.update(factHandle,
                                      object,
                                      mask,
                                      modifiedClass,
                                      activation);
    }

    public void executeQueuedActionsForRete() {
        // NO-OP: this is necessary only for rete
    }

    public void executeQueuedActions() {
        flushPropagations();
    }

    public void queueWorkingMemoryAction(final WorkingMemoryAction action) {
        try {
            startOperation();
            addPropagation(action);
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
        return nodeMemories.getNodeMemory( node, this );
    }

    public void clearNodeMemory(final MemoryFactory node) {
        if (nodeMemories != null) nodeMemories.clearNodeMemory( node );
    }

    public NodeMemories getNodeMemories() {
        return nodeMemories;
    }

    public RuleRuntimeEventSupport getRuleRuntimeEventSupport() {
        return this.ruleRuntimeEventSupport;
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

    public static class WorkingMemoryReteAssertAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {
        private final InternalFactHandle factHandle;

        private final boolean            removeLogical;

        private final boolean            updateEqualsMap;

        private RuleImpl               ruleOrigin;

        private LeftTuple          leftTuple;

        public WorkingMemoryReteAssertAction(MarshallerReaderContext context) throws IOException {
            this.factHandle = context.handles.get( context.readInt() );
            this.removeLogical = context.readBoolean();
            this.updateEqualsMap = context.readBoolean();

            if ( context.readBoolean() ) {
                String pkgName = context.readUTF();
                String ruleName = context.readUTF();
                InternalKnowledgePackage pkg = context.kBase.getPackage( pkgName );
                this.ruleOrigin = pkg.getRule( ruleName );
            }
            if ( context.readBoolean() ) {
                this.leftTuple = context.terminalTupleMap.get( context.readInt() );
            }
        }

        public WorkingMemoryReteAssertAction(MarshallerReaderContext context,
                                             ProtobufMessages.ActionQueue.Action _action) {
            ProtobufMessages.ActionQueue.Assert _assert = _action.getAssert();
            this.factHandle = context.handles.get( _assert.getHandleId() );
            this.removeLogical = _assert.getRemoveLogical();
            this.updateEqualsMap = _assert.getUpdateEqualsMap();

            if ( _assert.hasTuple() ) {
                String pkgName = _assert.getOriginPkgName();
                String ruleName = _assert.getOriginRuleName();
                InternalKnowledgePackage pkg = context.kBase.getPackage( pkgName );
                this.ruleOrigin = pkg.getRule( ruleName );
                this.leftTuple = context.filter.getTuplesCache().get( PersisterHelper.createActivationKey(pkgName, ruleName, _assert.getTuple()) );
            }
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) {
            ProtobufMessages.ActionQueue.Assert.Builder _assert = ProtobufMessages.ActionQueue.Assert.newBuilder();
            _assert.setHandleId( this.factHandle.getId() )
                   .setRemoveLogical( this.removeLogical )
                   .setUpdateEqualsMap( this.updateEqualsMap );

            if ( this.leftTuple != null ) {
                ProtobufMessages.Tuple.Builder _tuple = ProtobufMessages.Tuple.newBuilder();
                for( LeftTuple entry = this.leftTuple; entry != null; entry = entry.getParent() ) {
                    if ( entry.getLastHandle() != null ) {
                        // can be null for eval, not and exists that have no right input
                        _tuple.addHandleId( entry.getLastHandle().getId() );
                    }
                }
                _assert.setOriginPkgName( ruleOrigin.getPackageName() )
                       .setOriginRuleName( ruleOrigin.getName() )
                       .setTuple(_tuple.build());
            }
            return ProtobufMessages.ActionQueue.Action.newBuilder()
                                               .setType( ProtobufMessages.ActionQueue.ActionType.ASSERT )
                                               .setAssert( _assert.build() )
                                               .build();
        }

        public void execute(InternalWorkingMemory workingMemory) {
            PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();

            final PropagationContext context = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.INSERTION,
                                                                                    this.ruleOrigin, this.leftTuple, this.factHandle);
            workingMemory.getKnowledgeBase().assertObject(this.factHandle,
                                                          this.factHandle.getObject(),
                                                          context,
                                                          workingMemory);
            context.evaluateActionQueue( workingMemory );
        }
    }

    public static class WorkingMemoryReteExpireAction
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {

        private EventFactHandle factHandle;
        private ObjectTypeNode node;

        public WorkingMemoryReteExpireAction(final EventFactHandle factHandle) {
            this.factHandle = factHandle;
        }

        public WorkingMemoryReteExpireAction(final EventFactHandle factHandle,
                                             final ObjectTypeNode node) {
            this.factHandle = factHandle;
            this.node = node;
        }

        public EventFactHandle getFactHandle() {
            return factHandle;
        }

        public void setFactHandle(EventFactHandle factHandle) {
            this.factHandle = factHandle;
        }

        public ObjectTypeNode getNode() {
            return node;
        }

        public void setNode(ObjectTypeNode node) {
            this.node = node;
        }

        public WorkingMemoryReteExpireAction(MarshallerReaderContext context) throws IOException {
            this.factHandle = (EventFactHandle)context.handles.get(context.readInt());
            final int nodeId = context.readInt();
            this.node = (ObjectTypeNode) context.sinks.get(nodeId);
        }

        public WorkingMemoryReteExpireAction(MarshallerReaderContext context,
                                             ProtobufMessages.ActionQueue.Action _action) {
            this.factHandle = (EventFactHandle)context.handles.get(_action.getExpire().getHandleId());
            if (_action.getExpire().getNodeId() > 0) {
                this.node = (ObjectTypeNode) context.sinks.get(_action.getExpire().getNodeId());
            }
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) {
            return ProtobufMessages.ActionQueue.Action.newBuilder()
                                                      .setType(ProtobufMessages.ActionQueue.ActionType.EXPIRE)
                                                      .setExpire(ProtobufMessages.ActionQueue.Expire.newBuilder()
                                                                                                    .setHandleId(this.factHandle.getId())
                                                                                                    .setNodeId(this.node != null ? this.node.getId() : -1)
                                                                                                    .build())
                                                      .build();
        }

        public void execute(InternalWorkingMemory workingMemory) {
            if (this.factHandle.isValid()) {
                PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();

                // if the fact is still in the working memory (since it may have been previously retracted already
                final PropagationContext context = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.EXPIRATION,
                                                                                        null, null, this.factHandle);
                factHandle.setExpired(true);
                if ( factHandle.getEqualityKey() == null || factHandle.getEqualityKey().getLogicalFactHandle() != factHandle ) {
                    if (this.node != null) {
                        this.node.retractObject(factHandle, context, workingMemory);
                    } else {
                        workingMemory.getEntryPoint(factHandle.getEntryPoint().getEntryPointId()).delete(factHandle);
                    }

                    context.evaluateActionQueue(workingMemory);
                    // if no activations for this expired event
                    if (factHandle.getActivationsCount() == 0) {
                        // remove it from the object store and clean up resources
                        factHandle.getEntryPoint().delete(factHandle);
                    }
                } else {
                    ((NamedEntryPoint) factHandle.getEntryPoint()).getTruthMaintenanceSystem().delete( factHandle );
                }

                context.evaluateActionQueue(workingMemory);
            }
        }

        @Override
        public boolean isMarshallable() {
            return true;
        }
    }

    public ProcessInstance startProcess(final String processId) {
        return getProcessRuntime().startProcess( processId );
    }

    public ProcessInstance startProcess(String processId,
                                        Map<String, Object> parameters) {
        return getProcessRuntime().startProcess( processId,
                                                 parameters );
    }

    public ProcessInstance createProcessInstance(String processId,
                                                 Map<String, Object> parameters) {
        return getProcessRuntime().createProcessInstance( processId, parameters );
    }

    public ProcessInstance startProcessInstance(long processInstanceId) {
        return getProcessRuntime().startProcessInstance( processInstanceId );
    }

    public Collection<ProcessInstance> getProcessInstances() {
        return getProcessRuntime().getProcessInstances();
    }

    public ProcessInstance getProcessInstance(long processInstanceId) {
        return getProcessRuntime().getProcessInstance( processInstanceId );
    }

    @Override
    public ProcessInstance startProcess(String processId,
                                        CorrelationKey correlationKey, Map<String, Object> parameters) {

        return getProcessRuntime().startProcess( processId, correlationKey, parameters );
    }

    @Override
    public ProcessInstance createProcessInstance(String processId,
                                                 CorrelationKey correlationKey, Map<String, Object> parameters) {

        return getProcessRuntime().createProcessInstance( processId, correlationKey, parameters );
    }

    @Override
    public ProcessInstance getProcessInstance(CorrelationKey correlationKey) {
        return getProcessRuntime().getProcessInstance( correlationKey );
    }

    public ProcessInstance getProcessInstance(long processInstanceId, boolean readOnly) {
        return getProcessRuntime().getProcessInstance( processInstanceId, readOnly);
    }

    public WorkItemManager getWorkItemManager() {
        if (workItemManager == null) {
            workItemManager = config.getWorkItemManagerFactory().createWorkItemManager(this.getKnowledgeRuntime());
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("ksession", this.getKnowledgeRuntime());
            Map<String, WorkItemHandler> workItemHandlers = config.getWorkItemHandlers(params);
            if (workItemHandlers != null) {
                for (Map.Entry<String, WorkItemHandler> entry : workItemHandlers.entrySet()) {
                    workItemManager.registerWorkItemHandler(entry.getKey(),
                                                            entry.getValue());
                }
            }
        }
        return workItemManager;
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        return this.entryPoints.get(name);
    }

    public Map<String, WorkingMemoryEntryPoint> getWorkingMemoryEntryPoints() {
        return this.entryPoints;
    }

    public ObjectTypeConfigurationRegistry getObjectTypeConfigurationRegistry() {
        return this.defaultEntryPoint.getObjectTypeConfigurationRegistry();
    }

    public InternalFactHandle getInitialFactHandle() {
        return this.initialFactHandle;
    }

    public TimerService getTimerService() {
        return this.timerService;
    }

    public SessionClock getSessionClock() {
        return (SessionClock) this.timerService;
    }

    public void startBatchExecution(ExecutionResultImpl results) {
        this.kBase.readLock();
        this.lock.lock();
        this.batchExecutionResult = results;
    }

    public ExecutionResultImpl getExecutionResult() {
        return (ExecutionResultImpl) this.batchExecutionResult;
    }

    public void endBatchExecution() {
        this.batchExecutionResult = null;
        this.lock.unlock();
        this.kBase.readUnlock();
    }

    public InternalKnowledgeRuntime getKnowledgeRuntime() {
        return this;
    }

    public void registerChannel(String name,
                                Channel channel) {
        getChannels().put(name, channel);
    }

    public void unregisterChannel(String name) {
        if (channels != null) channels.remove(name);
    }

    public Map<String, Channel> getChannels() {
        if (channels == null) channels = new ConcurrentHashMap<String, Channel>();
        return channels;
    }

    public long getFactCount() {
        return getObjectStore().size();
    }

    public long getTotalFactCount() {
        long result = 0;
        for (WorkingMemoryEntryPoint ep : this.entryPoints.values()) {
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
        if (this.opCounter.getAndIncrement() == 0) {
            // means the engine was idle, reset the timestamp
            this.lastIdleTimestamp.set(-1);
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
        if (this.opCounter.decrementAndGet() == 0) {
            // means the engine is idle, so, set the timestamp
            this.lastIdleTimestamp.set(this.timerService.getCurrentTime());
            if (this.endOperationListener != null) {
                this.endOperationListener.endOperation(this.getKnowledgeRuntime());
            }
        }
    }

    /**
     * Returns the number of time units (usually ms) that the engine is idle
     * according to the session clock or -1 if it is not idle.
     *
     * This method is not synchronised and might return an approximate value.
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

    public void addPropagation(PropagationEntry propagationEntry) {
        propagationList.addEntry( propagationEntry );
    }

    public void flushPropagations() {
        propagationList.flush();
        executeQueuedActionsForRete();
    }

    public void flushPropagationsOnFireUntilHalt(boolean fired) {
        propagationList.flushOnFireUntilHalt( fired );
        executeQueuedActionsForRete();
    }

    public void flushPropagationsOnFireUntilHalt( boolean fired, PropagationEntry propagationEntry ) {
        propagationList.flushOnFireUntilHalt( fired, propagationEntry );
        executeQueuedActionsForRete();
    }

    public PropagationEntry takeAllPropagations() {
        return propagationList.takeAll();
    }

    public void notifyHalt() {
        propagationList.notifyHalt();
    }

    public void flushNonMarshallablePropagations() {
        propagationList.flushNonMarshallable();
        executeQueuedActionsForRete();
    }

    public void notifyEngineInactive() {
        propagationList.onEngineInactive();
    }

    public boolean hasPendingPropagations() {
        return !propagationList.isEmpty();
    }

    public Iterator<? extends PropagationEntry> getActionsIterator() {
        return propagationList.iterator();
    }

    public static final DummyInternalProcessRuntime DUMMY_PROCESS_RUNTIME = new DummyInternalProcessRuntime();

    public static class DummyInternalProcessRuntime implements InternalProcessRuntime {
        @Override
        public void dispose() { }

        @Override
        public void setProcessEventSupport( ProcessEventSupport processEventSupport ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void clearProcessInstances() {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void clearProcessInstancesState() {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance startProcess( String processId, CorrelationKey correlationKey, Map<String, Object> parameters ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance createProcessInstance( String processId, CorrelationKey correlationKey, Map<String, Object> parameters ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance getProcessInstance( CorrelationKey correlationKey ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void addEventListener( ProcessEventListener listener ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void removeEventListener( ProcessEventListener listener ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public Collection<ProcessEventListener> getProcessEventListeners() {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance startProcess( String processId ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance startProcess( String processId, Map<String, Object> parameters ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance createProcessInstance( String processId, Map<String, Object> parameters ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance startProcessInstance( long processInstanceId ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void signalEvent( String type, Object event ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void signalEvent( String type, Object event, long processInstanceId ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public Collection<ProcessInstance> getProcessInstances() {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance getProcessInstance( long processInstanceId ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public ProcessInstance getProcessInstance( long processInstanceId, boolean readonly ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public void abortProcessInstance( long processInstanceId ) {
            throw new UnsupportedOperationException( );
        }

        @Override
        public WorkItemManager getWorkItemManager() {
            throw new UnsupportedOperationException( );
        }
    }

}
