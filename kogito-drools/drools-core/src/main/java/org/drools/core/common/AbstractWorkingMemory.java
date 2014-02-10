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

package org.drools.core.common;

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
import java.util.Map.Entry;
import java.util.Queue;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.core.Agenda;
import org.drools.core.FactException;
import org.drools.core.FactHandle;
import org.drools.core.QueryResults;
import org.drools.core.RuleBase;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.RuntimeDroolsException;
import org.drools.core.SessionConfiguration;
import org.drools.core.StatefulSession;
import org.drools.core.WorkingMemory;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.base.CalendarsImpl;
import org.drools.core.base.DroolsQuery;
import org.drools.core.base.InternalViewChangedEventListener;
import org.drools.core.base.MapGlobalResolver;
import org.drools.core.base.NonCloningQueryViewListener;
import org.drools.core.base.QueryRowWithSubruleIndex;
import org.drools.core.base.StandardQueryViewChangedEventListener;
import org.drools.core.event.AgendaEventListener;
import org.drools.core.event.AgendaEventSupport;
import org.drools.core.event.RuleBaseEventListener;
import org.drools.core.event.RuleEventListenerSupport;
import org.drools.core.event.WorkingMemoryEventListener;
import org.drools.core.event.WorkingMemoryEventSupport;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.management.DroolsManagementAgent;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ObjectMarshallingStrategyStoreImpl;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ProtobufMessages.ActionQueue.Action;
import org.drools.core.marshalling.impl.ProtobufMessages.ActionQueue.Assert;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.phreak.RuleExecutor;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.phreak.StackEntry;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.InitialFactImpl;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.PathMemory;
import org.drools.core.reteoo.QueryTerminalNode;
import org.drools.core.reteoo.ReteooRuleBase;
import org.drools.core.reteoo.ReteooWorkingMemoryInterface;
import org.drools.core.reteoo.RuleTerminalNode;
import org.drools.core.reteoo.SegmentMemory;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.Package;
import org.drools.core.rule.Rule;
import org.drools.core.runtime.impl.ExecutionResultImpl;
import org.drools.core.runtime.process.InternalProcessRuntime;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.drools.core.runtime.rule.impl.LiveQueryImpl;
import org.drools.core.runtime.rule.impl.OpenQueryViewChangedEventListenerAdapter;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaFilter;
import org.drools.core.spi.AsyncExceptionHandler;
import org.drools.core.spi.FactHandleFactory;
import org.drools.core.spi.GlobalResolver;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleBaseUpdateListener;
import org.drools.core.spi.RuleBaseUpdateListenerFactory;
import org.drools.core.time.AcceptsTimerJobFactoryManager;
import org.drools.core.time.TimerService;
import org.drools.core.time.TimerServiceFactory;
import org.drools.core.type.DateFormats;
import org.drools.core.type.DateFormatsImpl;
import org.drools.core.util.index.LeftTupleList;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.process.ProcessEventManager;
import org.kie.api.marshalling.Marshaller;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Calendars;
import org.kie.api.runtime.Channel;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.Globals;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.api.runtime.rule.LiveQuery;
import org.kie.api.runtime.rule.ViewChangedEventListener;
import org.kie.api.time.SessionClock;
import org.kie.internal.event.rule.RuleEventListener;
import org.kie.internal.marshalling.MarshallerFactory;
import org.kie.internal.process.CorrelationAwareProcessRuntime;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.StatefulKnowledgeSession;

/**
 * Implementation of <code>WorkingMemory</code>.
 */
public class AbstractWorkingMemory
    implements
    InternalWorkingMemoryActions,
    EventSupport,
    ProcessEventManager, 
    CorrelationAwareProcessRuntime, ReteooWorkingMemoryInterface, StatefulSession, Externalizable {

    private static final long serialVersionUID = 510l;
    public    byte[] bytes;
    protected int    id;

    /** The actual memory for the <code>JoinNode</code>s. */
    private NodeMemories nodeMemories;

    protected NamedEntryPoint defaultEntryPoint;

    /** Global values which are associated with this memory. */
    protected GlobalResolver globalResolver;

    protected Calendars   calendars;
    protected DateFormats dateFormats;

    /** The eventSupport */
    protected WorkingMemoryEventSupport workingMemoryEventSupport;

    protected RuleEventListenerSupport ruleEventListenerSupport;

    protected AgendaEventSupport agendaEventSupport;

    protected List __ruleBaseEventListeners;

    /** The <code>RuleBase</code> with which this memory is associated. */
    protected transient InternalRuleBase ruleBase;

    protected FactHandleFactory handleFactory;

    /** Rule-firing agenda. */
    protected InternalAgenda agenda;

    private Queue<WorkingMemoryAction> actionQueue;

    protected AtomicBoolean evaluatingActionQueue;

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

    private InternalKnowledgeRuntime kruntime;

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

    private transient ObjectMarshallingStrategyStore marshallingStore;
    private transient List                           ruleBaseListeners;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public AbstractWorkingMemory() {

    }


    public AbstractWorkingMemory(final int id,
                                 final InternalRuleBase ruleBase) {
        this(id,
             ruleBase,
             true,
             SessionConfiguration.getDefaultInstance(),
             EnvironmentFactory.newEnvironment());
    }

    /**
     * Construct.
     *
     * @param ruleBase
     *            The backing rule-base.
     */
    public AbstractWorkingMemory(final int id,
                                 final InternalRuleBase ruleBase,
                                 boolean initInitFactHandle,
                                 final SessionConfiguration config,
                                 final Environment environment) {
        this(id,
             ruleBase,
             ruleBase.newFactHandleFactory(),
             initInitFactHandle,
             1,
             config,
             environment,
             new WorkingMemoryEventSupport(),
             new AgendaEventSupport(),
             new RuleEventListenerSupport(),
             null );
    }

    public AbstractWorkingMemory(final int id,
                                 final InternalRuleBase ruleBase,
                                 final FactHandleFactory handleFactory,
                                 final InternalFactHandle initialFactHandle,
                                 final long propagationContext,
                                 final SessionConfiguration config,
                                 final InternalAgenda agenda,
                                 final Environment environment) {
        this(id,
             ruleBase,
             handleFactory,
             false,
             propagationContext,
             config,
             environment,
             new WorkingMemoryEventSupport(),
             new AgendaEventSupport(),
             new RuleEventListenerSupport(),
             agenda);
    }


    public AbstractWorkingMemory(final int id,
                                 final InternalRuleBase ruleBase,
                                 final FactHandleFactory handleFactory,
                                 final boolean initInitFactHandle,
                                 final long propagationContext,
                                 final SessionConfiguration config,
                                 final Environment environment,
                                 final WorkingMemoryEventSupport workingMemoryEventSupport,
                                 final AgendaEventSupport agendaEventSupport,
                                 final RuleEventListenerSupport ruleEventListenerSupport,
                                 final InternalAgenda agenda) {
        this.id = id;
        this.config = config;
        this.ruleBase = ruleBase;
        this.handleFactory = handleFactory;
        this.pctxFactory = ruleBase.getConfiguration().getComponentFactory().getPropagationContextFactory();
        this.environment = environment;

        nodeMemories = new ConcurrentNodeMemories(this.ruleBase);
        actionQueue = new ConcurrentLinkedQueue<WorkingMemoryAction>();

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

        this.calendars = new CalendarsImpl();

        this.dateFormats = (DateFormats) this.environment.get(EnvironmentName.DATE_FORMATS);
        if (this.dateFormats == null) {
            this.dateFormats = new DateFormatsImpl();
            this.environment.set( EnvironmentName.DATE_FORMATS,
                                  this.dateFormats );
        }

        final RuleBaseConfiguration conf = this.ruleBase.getConfiguration();

        this.sequential = conf.isSequential();

//        if ( initialFactHandle == null ) {
//            this.initialFactHandle = handleFactory.newFactHandle( InitialFactImpl.getInstance(),
//                                                                  null,
//                                                                  this,
//                                                                  this );
//        } else {
//            this.initialFactHandle = initialFactHandle;
//        }

        this.evaluatingActionQueue = new AtomicBoolean( false );

        this.workingMemoryEventSupport = workingMemoryEventSupport;
        this.agendaEventSupport = agendaEventSupport;
        this.ruleEventListenerSupport = ruleEventListenerSupport;
        this.__ruleBaseEventListeners = new LinkedList();
        this.lock = new ReentrantLock();

        timerService = TimerServiceFactory.getTimerService( this.config );     
        ((AcceptsTimerJobFactoryManager) timerService).setTimerJobFactoryManager( config.getTimerJobFactoryManager() );

        this.propagationIdCounter = new AtomicLong( propagationContext );

        this.firing = new AtomicBoolean( false );

        initTransient();

        this.opCounter = new AtomicLong( 0 );
        this.lastIdleTimestamp = new AtomicLong( -1 );

        if ( agenda == null ) {
            this.agenda = ruleBase.getConfiguration().getComponentFactory().getAgendaFactory().createAgenda(ruleBase);
        }  else {
            this.agenda = agenda;
        }
        this.agenda.setWorkingMemory(this);

        if ( initInitFactHandle ) {
            initInitialFact(ruleBase, null);
        }
    }

    public void initInitialFact(InternalRuleBase ruleBase, MarshallerReaderContext context) {
        this.initialFactHandle = new DefaultFactHandle(0, InitialFactImpl.getInstance(), 0,  defaultEntryPoint );

        ObjectTypeConf otc = this.defaultEntryPoint.getObjectTypeConfigurationRegistry()
                                                   .getObjectTypeConf(this.defaultEntryPoint.entryPoint, this.initialFactHandle.getObject());
        PropagationContextFactory ctxFact = ruleBase.getConfiguration().getComponentFactory().getPropagationContextFactory();


        final PropagationContext pctx = ctxFact.createPropagationContext(0, PropagationContext.INSERTION, null,
                                                                         null , initialFactHandle, defaultEntryPoint.getEntryPoint(),
                                                                         context );

        otc.getConcreteObjectTypeNode().assertObject(this.initialFactHandle, pctx, this );
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
        return EntryPointId.DEFAULT.getEntryPointId();
    }

    public QueryResults getQueryResults(final String queryName,
                                        final Object[] arguments) {

        try {
            startOperation();

            this.lock.lock();
            this.ruleBase.readLock();

            this.ruleBase.executeQueuedActions();
            executeQueuedActions();

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

            executeQueuedActions();

            this.handleFactory.destroyFactHandle( handle );

            return new QueryResults( (List<QueryRowWithSubruleIndex>) queryObject.getQueryResultCollector().getResults(),
                                     decls.toArray( new Map[decls.size()] ),
                                     this,
                                     ( queryObject.getQuery() != null ) ? queryObject.getQuery().getParameters()  : new Declaration[0] );
        } finally {
            this.ruleBase.readUnlock();
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
            this.ruleBase.readLock();
            this.lock.lock();

            this.ruleBase.executeQueuedActions();
            executeQueuedActions();

            DroolsQuery queryObject = new DroolsQuery( query,
                                                       arguments,
                                                       new OpenQueryViewChangedEventListenerAdapter( listener ),
                                                       true,
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

            BaseNode[] tnodes = evalQuery(queryObject.getName(), queryObject, handle, pCtx);

            executeQueuedActions();

            return new LiveQueryImpl( this,
                                      handle );
        } finally {
            this.lock.unlock();
            this.ruleBase.readUnlock();
            endOperation();
        }
    }

    protected BaseNode[] evalQuery(String queryName, DroolsQuery queryObject, InternalFactHandle handle, PropagationContext pCtx) {
        BaseNode[] tnodes = ( BaseNode[] ) ruleBase.getReteooBuilder().getTerminalNodes(queryName);
        if ( tnodes == null ) {
            throw new RuntimeException( "Query '" + queryName + "' does not exist");
        }

        QueryTerminalNode tnode = ( QueryTerminalNode )  tnodes[0];
        LeftTupleSource lts = tnode.getLeftTupleSource();
        while ( lts.getType() != NodeTypeEnums.LeftInputAdapterNode ) {
            lts = lts.getLeftTupleSource();
        }
        LeftInputAdapterNode lian = ( LeftInputAdapterNode ) lts;
        LiaNodeMemory lmem = (LiaNodeMemory) getNodeMemory( (MemoryFactory) lts);
        SegmentMemory lsmem = lmem.getSegmentMemory();
        if ( lsmem == null ) {
            lsmem = SegmentUtilities.createSegmentMemory(lts, this);
        }

        LeftInputAdapterNode.doInsertObject( handle, pCtx, lian, this, lmem, false, queryObject.isOpen() );

        RuleBaseConfiguration conf = this.ruleBase.getConfiguration();
        if( conf.isPhreakEnabled() && lmem.getSegmentMemory().getTupleQueue() != null ) {
            RuleExecutor.flushTupleQueue( lmem.getSegmentMemory().getTupleQueue() );
        }

        List<PathMemory> pmems =  lmem.getSegmentMemory().getPathMemories();
        for ( int i = 0, length = pmems.size(); i < length; i++ ) {
            PathMemory rm = pmems.get( i );
            RuleAgendaItem evaluator = agenda.createRuleAgendaItem(Integer.MAX_VALUE, rm, (TerminalNode) rm.getNetworkNode());
            evaluator.getRuleExecutor().setDirty(true);
            evaluator.getRuleExecutor().evaluateNetworkAndFire(this, null, 0, -1);
        }

        return tnodes;
    }

    public void closeLiveQuery(final InternalFactHandle factHandle) {

        try {
            startOperation();
            this.ruleBase.readLock();
            this.lock.lock();

            final PropagationContext pCtx = pctxFactory.createPropagationContext(getNextPropagationIdCounter(), PropagationContext.INSERTION,
                                                                                 null, null, factHandle, getEntryPoint());

            LeftInputAdapterNode lian = ( LeftInputAdapterNode ) factHandle.getFirstLeftTuple().getLeftTupleSink().getLeftTupleSource();
            LiaNodeMemory lmem = (LiaNodeMemory) getNodeMemory( (MemoryFactory) lian);
            SegmentMemory lsmem = lmem.getSegmentMemory();

            LeftTuple childLeftTuple = factHandle.getFirstLeftTuple(); // there is only one, all other LTs are peers
            LeftInputAdapterNode.doDeleteObject( childLeftTuple, childLeftTuple.getPropagationContext(),  lsmem, this, lian, false, lmem );

            List<PathMemory> pmems =  lmem.getSegmentMemory().getPathMemories();
            for ( int i = 0, length = pmems.size(); i < length; i++ ) {
                PathMemory rm = pmems.get( i );

                RuleAgendaItem evaluator = agenda.createRuleAgendaItem(Integer.MAX_VALUE, rm, (TerminalNode) rm.getNetworkNode());
                evaluator.getRuleExecutor().setDirty(true);
                evaluator.getRuleExecutor().evaluateNetworkAndFire(this, null, 0, -1);
            }

            getFactHandleFactory().destroyFactHandle( factHandle );

        } finally {
            this.lock.unlock();
            this.ruleBase.readUnlock();
            endOperation();
        }
    }

    public EntryPointId getEntryPoint() {
        return this.defaultEntryPoint.getEntryPoint();
    }

    public InternalWorkingMemory getInternalWorkingMemory() {
        return this;
    }

    public <T extends org.kie.api.runtime.rule.FactHandle> Collection<T> getFactHandles() {
        List list = new ArrayList();

        for ( Iterator it = iterateFactHandles(); it.hasNext(); ) {
            FactHandle fh = ( FactHandle) it.next();
            list.add(  fh );
        }

        return list;
    }

    public <T extends org.kie.api.runtime.rule.FactHandle> Collection<T> getFactHandles(ObjectFilter filter) {
        throw new UnsupportedOperationException( "this is implementedby StatefulKnowledgeImpl" );
    }

    public Collection<? extends Object> getObjects() {
        throw new UnsupportedOperationException( "this is implementedby StatefulKnowledgeImpl" );
    }

    public Collection<? extends Object> getObjects(ObjectFilter filter) {
        throw new UnsupportedOperationException( "this is implementedby StatefulKnowledgeImpl" );
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
        out.write( bytes );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        bytes = new byte[ in.readInt() ];
        in.readFully( bytes );
    }

    public List getRuleBaseUpdateListeners() {
        if ( this.ruleBaseListeners == null || this.ruleBaseListeners == Collections.EMPTY_LIST ) {
            String listenerName = this.ruleBase.getConfiguration().getRuleBaseUpdateHandler();
            if ( listenerName != null && listenerName.length() > 0 ) {
                RuleBaseUpdateListener listener = RuleBaseUpdateListenerFactory.createListener(listenerName,
                                                                                               this);
                this.ruleBaseListeners = Collections.singletonList( listener );
            } else {
                this.ruleBaseListeners = Collections.EMPTY_LIST;
            }
        }
        return this.ruleBaseListeners;
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
                EntryPointId id = addedNode.getEntryPoint();
                if (EntryPointId.DEFAULT.equals(id)) continue;
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
        EntryPointNode epn = this.ruleBase.getRete().getEntryPointNode( EntryPointId.DEFAULT );

        this.defaultEntryPoint = new NamedEntryPoint( EntryPointId.DEFAULT,
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

    ///RuleEventListenerSupport
    public void addEventListener(final RuleEventListener listener) {
        this.ruleEventListenerSupport.addEventListener( listener );
    }

    public void removeEventListener(final RuleEventListener listener) {
        this.ruleEventListenerSupport.removeEventListener( listener );
    }

    public List getRuleEventListeners() {
        return this.ruleEventListenerSupport.getEventListeners();
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
        return this.globalResolver.resolveGlobal( identifier );
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
     * @see org.drools.core.WorkingMemory
     * 
     * @param handle
     *            The <code>FactHandle</code> reference for the
     *            <code>Object</code> lookup
     */
    public Object getObject(org.kie.api.runtime.rule.FactHandle handle) {
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
     * @see org.drools.core.WorkingMemory
     */
    public FactHandle getFactHandle(final Object object) {
        return this.defaultEntryPoint.getFactHandle( object );
    }

    /**
     * @see org.drools.core.WorkingMemory
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
    public Iterator iterateObjects(org.kie.api.runtime.ObjectFilter filter) {
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
    public Iterator iterateFactHandles(org.kie.api.runtime.ObjectFilter filter) {
        return getObjectStore().iterateFactHandles( filter );
    }

    public QueryResults getQueryResults(final String query) {
        return getQueryResults( query,
                                null );
    }

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
     * @see org.drools.core.WorkingMemory
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
                                       typeConf,
                                       null );
    }

    public void retract(final org.kie.api.runtime.rule.FactHandle handle) throws FactException {
        delete( (FactHandle) handle,
                 null,
                 null );
    }

    public void delete(final org.kie.api.runtime.rule.FactHandle handle) throws FactException {
        delete( (FactHandle) handle,
                 null,
                 null );
    }

    public void delete(final FactHandle factHandle,
                        final Rule rule,
                        final Activation activation) throws FactException {
        this.defaultEntryPoint.delete( factHandle,
                                        rule,
                                        activation );
    }

    public EntryPointNode getEntryPointNode() {
        return ((InternalWorkingMemoryEntryPoint) this.defaultEntryPoint).getEntryPointNode();
    }

    public void update(final org.kie.api.runtime.rule.FactHandle handle,
                       final Object object) throws FactException {
        update( (FactHandle) handle,
                object,
                Long.MAX_VALUE,
                Object.class,
                null );
    }

    public void update(final org.kie.api.runtime.rule.FactHandle factHandle,
                       final Object object,
                       final long mask,
                       Class<?> modifiedClass,
                       final Activation activation) throws FactException {

        update( (FactHandle) factHandle,
                object,
                mask,
                modifiedClass,
                activation );
    }

    /**
     * modify is implemented as half way retract / assert due to the truth
     * maintenance issues.
     * 
     * @see org.drools.core.WorkingMemory
     */
    public void update(FactHandle factHandle,
                       final Object object,
                       final long mask,
                       Class<?> modifiedClass,
                       final Activation activation) throws FactException {
        this.defaultEntryPoint.update( factHandle,
                                       object,
                                       mask,
                                       modifiedClass,
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
        return nodeMemories.getNodeMemory( node, this );
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

    public static class WorkingMemoryReteAssertAction
        implements
        WorkingMemoryAction {
        private InternalFactHandle factHandle;

        private boolean            removeLogical;

        private boolean            updateEqualsMap;

        private Rule               ruleOrigin;

        private LeftTuple          leftTuple;

        public WorkingMemoryReteAssertAction(final InternalFactHandle factHandle,
                                             final boolean removeLogical,
                                             final boolean updateEqualsMap,
                                             final Rule ruleOrigin,
                                             final LeftTuple leftTuple) {
            this.factHandle = factHandle;
            this.removeLogical = removeLogical;
            this.updateEqualsMap = updateEqualsMap;
            this.ruleOrigin = ruleOrigin;
            this.leftTuple = leftTuple;
        }

        public WorkingMemoryReteAssertAction(MarshallerReaderContext context) throws IOException {
            this.factHandle = context.handles.get( context.readInt() );
            this.removeLogical = context.readBoolean();
            this.updateEqualsMap = context.readBoolean();

            if ( context.readBoolean() ) {
                String pkgName = context.readUTF();
                String ruleName = context.readUTF();
                org.drools.core.rule.Package pkg = context.ruleBase.getPackage( pkgName );
                this.ruleOrigin = pkg.getRule( ruleName );
            }
            if ( context.readBoolean() ) {
                this.leftTuple = context.terminalTupleMap.get( context.readInt() );
            }
        }

        public WorkingMemoryReteAssertAction(MarshallerReaderContext context,
                                             Action _action) {
            Assert _assert = _action.getAssert();
            this.factHandle = context.handles.get( _assert.getHandleId() );
            this.removeLogical = _assert.getRemoveLogical();
            this.updateEqualsMap = _assert.getUpdateEqualsMap();

            if ( _assert.hasTuple() ) {
                String pkgName = _assert.getOriginPkgName();
                String ruleName = _assert.getOriginRuleName();
                Package pkg = context.ruleBase.getPackage( pkgName );
                this.ruleOrigin = pkg.getRule( ruleName );
                this.leftTuple = context.filter.getTuplesCache().get( PersisterHelper.createActivationKey(pkgName, ruleName, _assert.getTuple()) );
            }
        }

        public void write(MarshallerWriteContext context) throws IOException {
            context.writeShort( WorkingMemoryAction.WorkingMemoryReteAssertAction );

            context.writeInt( this.factHandle.getId() );
            context.writeBoolean( this.removeLogical );
            context.writeBoolean( this.updateEqualsMap );

            if ( this.ruleOrigin != null ) {
                context.writeBoolean( true );
                context.writeUTF( ruleOrigin.getPackage() );
                context.writeUTF( ruleOrigin.getName() );
            } else {
                context.writeBoolean( false );
            }

            if ( this.leftTuple != null ) {
                context.writeBoolean( true );
                context.writeInt( context.terminalTupleMap.get( this.leftTuple ) );
            } else {
                context.writeBoolean( false );
            }

        }

        public Action serialize(MarshallerWriteContext context) {
            Assert.Builder _assert = Assert.newBuilder();
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
                       .setTuple( _tuple.build() );
            }
            return Action.newBuilder()
                    .setType( ProtobufMessages.ActionQueue.ActionType.ASSERT )
                    .setAssert( _assert.build() )
                    .build();
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            factHandle = (InternalFactHandle) in.readObject();
            removeLogical = in.readBoolean();
            updateEqualsMap = in.readBoolean();
            ruleOrigin = (Rule) in.readObject();
            leftTuple = (LeftTuple) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( factHandle );
            out.writeBoolean( removeLogical );
            out.writeBoolean( updateEqualsMap );
            out.writeObject( ruleOrigin );
            out.writeObject( leftTuple );
        }

        public void execute(InternalWorkingMemory workingMemory) {
            PropagationContextFactory pctxFactory = ((InternalRuleBase) workingMemory.getRuleBase()).getConfiguration().getComponentFactory().getPropagationContextFactory();

            final PropagationContext context = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.INSERTION,
                                                                                    this.ruleOrigin, this.leftTuple, this.factHandle);
            ReteooRuleBase ruleBase = (ReteooRuleBase) workingMemory.getRuleBase();
            ruleBase.assertObject( this.factHandle,
                                   this.factHandle.getObject(),
                                   context,
                                   workingMemory );
            context.evaluateActionQueue( workingMemory );
        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute( ((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory() );
        }

    }

    public static class WorkingMemoryReteExpireAction
        implements
        WorkingMemoryAction {

        private InternalFactHandle factHandle;
        private ObjectTypeNode     node;

        public WorkingMemoryReteExpireAction(final InternalFactHandle factHandle,
                                             final ObjectTypeNode node) {
            this.factHandle = factHandle;
            this.node = node;
        }

        public InternalFactHandle getFactHandle() {
            return factHandle;
        }

        public void setFactHandle(InternalFactHandle factHandle) {
            this.factHandle = factHandle;
        }

        public ObjectTypeNode getNode() {
            return node;
        }

        public void setNode(ObjectTypeNode node) {
            this.node = node;
        }

        public WorkingMemoryReteExpireAction(MarshallerReaderContext context) throws IOException {
            this.factHandle = context.handles.get(context.readInt());
            final int nodeId = context.readInt();
            this.node = (ObjectTypeNode) context.sinks.get(Integer.valueOf(nodeId));
        }

        public WorkingMemoryReteExpireAction(MarshallerReaderContext context,
                                             Action _action) {
            this.factHandle = context.handles.get(_action.getExpire().getHandleId());
            this.node = (ObjectTypeNode) context.sinks.get(Integer.valueOf(_action.getExpire().getNodeId()));
        }

        public void write(MarshallerWriteContext context) throws IOException {
            context.writeShort(WorkingMemoryAction.WorkingMemoryReteExpireAction);
            context.writeInt(this.factHandle.getId());
            context.writeInt(this.node.getId());
        }

        public Action serialize(MarshallerWriteContext context) {
            return Action.newBuilder()
                         .setType(ProtobufMessages.ActionQueue.ActionType.EXPIRE)
                         .setExpire(ProtobufMessages.ActionQueue.Expire.newBuilder()
                                                                .setHandleId(this.factHandle.getId())
                                                                .setNodeId(this.node.getId())
                                                                .build())
                         .build();
        }

        public void execute(InternalWorkingMemory workingMemory) {
            if (this.factHandle.isValid()) {
                PropagationContextFactory pctxFactory = ((InternalRuleBase) workingMemory.getRuleBase()).getConfiguration().getComponentFactory().getPropagationContextFactory();

                // if the fact is still in the working memory (since it may have been previously retracted already
                final PropagationContext context = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.EXPIRATION,
                                                                                        null, null, this.factHandle);
                ((EventFactHandle) factHandle).setExpired(true);
                this.node.retractObject(factHandle,
                                        context,
                                        workingMemory);

                context.evaluateActionQueue(workingMemory);
                // if no activations for this expired event
                if (((EventFactHandle) factHandle).getActivationsCount() == 0) {
                    // remove it from the object store and clean up resources
                    ((EventFactHandle) factHandle).getEntryPoint().retract(factHandle);
                }
                context.evaluateActionQueue(workingMemory);
            }

        }

        public void execute(InternalKnowledgeRuntime kruntime) {
            execute(((StatefulKnowledgeSessionImpl) kruntime).getInternalWorkingMemory());
        }

        public void writeExternal(ObjectOutput out) throws IOException {
        }

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
        }
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
        return processRuntime.startProcess(processId,
                                           parameters);
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
        return processRuntime.getProcessInstance(processInstanceId);
    }

    @Override
    public ProcessInstance startProcess(String processId,
                                        CorrelationKey correlationKey, Map<String, Object> parameters) {

        return processRuntime.startProcess(processId, correlationKey, parameters);
    }

    @Override
    public ProcessInstance createProcessInstance(String processId,
                                                 CorrelationKey correlationKey, Map<String, Object> parameters) {

        return processRuntime.createProcessInstance(processId, correlationKey, parameters);
    }

    @Override
    public ProcessInstance getProcessInstance(CorrelationKey correlationKey) {
        return processRuntime.getProcessInstance(correlationKey);
    }

    public ProcessInstance getProcessInstance(long processInstanceId, boolean readOnly) {
        return processRuntime.getProcessInstance(processInstanceId, readOnly);
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

    public List iterateObjectsToList() {
        List result = new ArrayList();
        Iterator iterator = iterateObjects();
        for (; iterator.hasNext(); ) {
            result.add(iterator.next());
        }
        return result;
    }

    public List iterateNonDefaultEntryPointObjectsToList() {
        List result = new ArrayList();
        for (Map.Entry<String, WorkingMemoryEntryPoint> entry : getEntryPoints().entrySet()) {
            WorkingMemoryEntryPoint entryPoint = entry.getValue();
            if (entryPoint instanceof NamedEntryPoint) {
                result.add(new EntryPointObjects(entry.getKey(),
                                                 new ArrayList(entry.getValue().getObjects())));
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
        for (int i = 0; i < activations.length; i++) {
            if (activations[i].getActivationNumber() == activationId) {
                Map params = getActivationParameters(activations[i]);
                return (Entry[]) params.entrySet().toArray(new Entry[params.size()]);
            }
        }
        return new Entry[0];
    }

    /**
     * Helper method
     */
    public Map getActivationParameters(Activation activation) {
        if (activation instanceof RuleAgendaItem) {
            RuleAgendaItem ruleAgendaItem = (RuleAgendaItem)activation;
            LeftTupleList tupleList = ruleAgendaItem.getRuleExecutor().getLeftTupleList();
            Map result = new TreeMap();
            int i = 0;
            for (LeftTuple tuple = tupleList.getFirst(); tuple != null; tuple = (LeftTuple) tuple.getNext()) {
                Map params = getActivationParameters(tuple);
                result.put("Parameters set [" + i++ + "]", (Entry[]) params.entrySet().toArray(new Entry[params.size()]));
            }
            return result;
        } else {
            return getActivationParameters(activation.getTuple());
        }
    }

    private Map getActivationParameters(LeftTuple tuple) {
        Map result = new HashMap();
        Declaration[] declarations = ((RuleTerminalNode) tuple.getLeftTupleSink()).getDeclarations();

        for (int i = 0; i < declarations.length; i++) {
            FactHandle handle = tuple.get(declarations[i]);
            if (handle instanceof InternalFactHandle) {
                result.put(declarations[i].getIdentifier(),
                           declarations[i].getValue(this,
                                                    ((InternalFactHandle) handle).getObject()));
            }
        }
        return result;
    }

    public WorkingMemoryEntryPoint getWorkingMemoryEntryPoint(String name) {
        WorkingMemoryEntryPoint wmEntryPoint = this.entryPoints.get(name);
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
        this.ruleBase.disposeStatefulSession( this );

        if (this.ruleBase.getConfiguration().isMBeansEnabled()) {
            DroolsManagementAgent.getInstance().unregisterKnowledgeSession(this);
        }
        for (WorkingMemoryEntryPoint ep : this.entryPoints.values()) {
            ep.dispose();
        }
        this.workingMemoryEventSupport.reset();
        this.agendaEventSupport.reset();
        for (Iterator it = this.__ruleBaseEventListeners.iterator(); it.hasNext(); ) {
            this.ruleBase.removeEventListener((RuleBaseEventListener) it.next());
        }
        if (processRuntime != null) {
            this.processRuntime.dispose();
        }
        if (timerService != null) {
            this.timerService.shutdown();
        }
    }

    public void setKnowledgeRuntime(InternalKnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
        this.processRuntime = createProcessRuntime();
        initManagementBeans();
    }

    public InternalKnowledgeRuntime getKnowledgeRuntime() {
        return this.kruntime;
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

    public Map<String, WorkingMemoryEntryPoint> getEntryPoints() {
        return this.entryPoints;
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
        if (this.marshallingStore == null) {
            this.marshallingStore = new ObjectMarshallingStrategyStoreImpl((ObjectMarshallingStrategy[]) this.environment.get(EnvironmentName.OBJECT_MARSHALLING_STRATEGIES));
        }
        return this.marshallingStore;
    }
}
