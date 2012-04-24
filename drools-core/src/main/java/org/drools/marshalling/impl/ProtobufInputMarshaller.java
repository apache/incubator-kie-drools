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

package org.drools.marshalling.impl;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.drools.SessionConfiguration;
import org.drools.common.ActivationsFilter;
import org.drools.common.BinaryHeapQueueAgendaGroup;
import org.drools.common.DefaultAgenda;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EqualityKey;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.InternalWorkingMemoryEntryPoint;
import org.drools.common.NamedEntryPoint;
import org.drools.common.ObjectStore;
import org.drools.common.PropagationContextImpl;
import org.drools.common.QueryElementFactHandle;
import org.drools.common.RuleFlowGroupImpl;
import org.drools.common.TruthMaintenanceSystem;
import org.drools.common.WorkingMemoryAction;
import org.drools.concurrent.ExecutorService;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.ProtobufMessages.Agenda.RuleFlowGroup.NodeInstance;
import org.drools.marshalling.impl.ProtobufMessages.FactHandle;
import org.drools.marshalling.impl.ProtobufMessages.RuleData;
import org.drools.marshalling.impl.ProtobufMessages.Timers.Timer;
import org.drools.reteoo.InitialFactImpl;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.ReteooComponentFactory;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.EntryPoint;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.Activation;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.GlobalResolver;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleFlowGroup;
import org.drools.time.Trigger;
import org.drools.time.impl.CronTrigger;
import org.drools.time.impl.IntervalTrigger;
import org.drools.time.impl.PointInTimeTrigger;
import org.drools.time.impl.PseudoClockScheduler;

import com.google.protobuf.ExtensionRegistry;

/**
 * An input marshaller that uses protobuf. 
 * 
 * @author etirelli
 */
public class ProtobufInputMarshaller {
    // NOTE: all variables prefixed with _ (underscore) are protobuf structs

    private static ProcessMarshaller processMarshaller = createProcessMarshaller();

    private static ProcessMarshaller createProcessMarshaller() {
        try {
            return ProcessMarshallerFactory.newProcessMarshaller();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Stream the data into an existing session
     * 
     * @param session
     * @param context
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static ReteooStatefulSession readSession(ReteooStatefulSession session,
                                                    MarshallerReaderContext context) throws IOException,
                                                                                    ClassNotFoundException {

        ProtobufMessages.KnowledgeSession _session = loadAndParseSession( context );

        DefaultAgenda agenda = resetSession( session,
                                             context,
                                             _session );

        readSession( _session,
                     session,
                     agenda,
                     context );

        return session;
    }

    /**
     * Create a new session into which to read the stream data
     * @param context
     * @param id
     * @param executor
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static ReteooStatefulSession readSession(MarshallerReaderContext context,
                                                    int id,
                                                    ExecutorService executor) throws IOException,
                                                                             ClassNotFoundException {
        ReteooStatefulSession session = readSession( context,
                                                     id,
                                                     executor,
                                                     EnvironmentFactory.newEnvironment(),
                                                     SessionConfiguration.getDefaultInstance() );
        return session;
    }

    public static ReteooStatefulSession readSession(MarshallerReaderContext context,
                                                    int id,
                                                    ExecutorService executor,
                                                    Environment environment,
                                                    SessionConfiguration config) throws IOException,
                                                                                ClassNotFoundException {

        ProtobufMessages.KnowledgeSession _session = loadAndParseSession( context );

        ReteooStatefulSession session = createAndInitializeSession( context,
                                                                    id,
                                                                    executor,
                                                                    environment,
                                                                    config,
                                                                    _session );

        return readSession( _session,
                            session,
                            (DefaultAgenda) session.getAgenda(),
                            context );
    }

    private static DefaultAgenda resetSession(ReteooStatefulSession session,
                                              MarshallerReaderContext context,
                                              ProtobufMessages.KnowledgeSession _session) {
        session.reset( _session.getRuleData().getLastId(),
                       _session.getRuleData().getLastRecency(),
                       0 );
        DefaultAgenda agenda = (DefaultAgenda) session.getAgenda();

        readAgenda( context,
                    _session.getRuleData(),
                    agenda );
        return agenda;
    }

    private static ReteooStatefulSession createAndInitializeSession(MarshallerReaderContext context,
                                                                    int id,
                                                                    ExecutorService executor,
                                                                    Environment environment,
                                                                    SessionConfiguration config,
                                                                    ProtobufMessages.KnowledgeSession _session) throws IOException {
        FactHandleFactory handleFactory = context.ruleBase.newFactHandleFactory( _session.getRuleData().getLastId(),
                                                                                 _session.getRuleData().getLastRecency() );

        InternalFactHandle initialFactHandle = new DefaultFactHandle( _session.getRuleData().getInitialFact().getId(),
                                                                      InitialFactImpl.getInstance(),
                                                                      _session.getRuleData().getInitialFact().getRecency(),
                                                                      null );
        context.handles.put( initialFactHandle.getId(),
                             initialFactHandle );

        DefaultAgenda agenda = ReteooComponentFactory.getAgendaFactory().createAgenda( context.ruleBase, false );
        readAgenda( context,
                    _session.getRuleData(),
                    agenda );

        ReteooStatefulSession session = new ReteooStatefulSession( id,
                                                                   context.ruleBase,
                                                                   executor,
                                                                   handleFactory,
                                                                   initialFactHandle,
                                                                   0,
                                                                   config,
                                                                   agenda,
                                                                   environment );
        new StatefulKnowledgeSessionImpl( session );

        initialFactHandle.setEntryPoint( session.getEntryPoints().get( EntryPoint.DEFAULT.getEntryPointId() ) );
        return session;
    }

    private static ProtobufMessages.KnowledgeSession loadAndParseSession(MarshallerReaderContext context) throws IOException, ClassNotFoundException {
        ExtensionRegistry registry = PersisterHelper.buildRegistry( context, processMarshaller );

        ProtobufMessages.Header _header = PersisterHelper.readFromStreamWithHeader( context, registry );
        
        return ProtobufMessages.KnowledgeSession.parseFrom( _header.getPayload(), registry );
    }

    public static ReteooStatefulSession readSession(ProtobufMessages.KnowledgeSession _session,
                                                    ReteooStatefulSession session,
                                                    DefaultAgenda agenda,
                                                    MarshallerReaderContext context) throws IOException,
                                                                                    ClassNotFoundException {
        GlobalResolver globalResolver = (GlobalResolver) context.env.get( EnvironmentName.GLOBALS );
        if( globalResolver != null ) {
            session.setGlobalResolver( globalResolver );
        }

        if ( session.getTimerService() instanceof PseudoClockScheduler ) {
            PseudoClockScheduler clock = (PseudoClockScheduler) session.getTimerService();
            clock.advanceTime( _session.getTime(),
                               TimeUnit.MILLISECONDS );
        }

        // RuleFlowGroups need to reference the session
        for ( RuleFlowGroup group : agenda.getRuleFlowGroupsMap().values() ) {
            ((RuleFlowGroupImpl) group).setWorkingMemory( session );
        }

        context.wm = session;
        
        // need to read node memories before reading the fact handles
        // because this data is required during fact propagation 
        readNodeMemories( context,
                          _session.getRuleData() );

        readInitialFactHandle( context, 
                               _session.getRuleData() );
        
        for ( ProtobufMessages.EntryPoint _ep : _session.getRuleData().getEntryPointList() ) {
            WorkingMemoryEntryPoint wmep = context.wm.getEntryPoints().get( _ep.getEntryPointId() );
            readFactHandles( context,
                             _ep,
                             ((NamedEntryPoint) wmep).getObjectStore() );
        }

        readActionQueue( context,
                         _session.getRuleData() );

        readTruthMaintenanceSystem( context,
                                    _session.getRuleData() );
        

        if (processMarshaller != null) {
            if( _session.hasProcessData() ) {
                context.parameterObject = _session.getProcessData();
                processMarshaller.readProcessInstances( context );
                
                context.parameterObject = _session.getProcessData();
                processMarshaller.readWorkItems( context );
                
                // This actually does ALL timers, due to backwards compatability issues
                // It will read in old JBPM binaries, but always write to the new binary format.
                context.parameterObject = _session.getProcessData();
                processMarshaller.readProcessTimers( context );
            }
        } else {
            if ( _session.hasProcessData() ) {
                throw new IllegalStateException( "No process marshaller, unable to unmarshall process data." );
            }
        }

        if( _session.hasTimers() ) { 
            for( ProtobufMessages.Timers.Timer _timer : _session.getTimers().getTimerList() ) {
                readTimer( context,
                           _timer );
            }
        }

        // remove the activations filter
        agenda.setActivationsFilter( null );

        if ( _session.getMultithread() ) {
            session.startPartitionManagers();
        }

        return session;
    }
    
    private static void readNodeMemories(MarshallerReaderContext context,
                                         RuleData _session) {
        for( ProtobufMessages.NodeMemory _node : _session.getNodeMemoryList() ) {
            Object memory = null;
            switch( _node.getNodeType() ) {
                case ACCUMULATE: {
                    Map<TupleKey, ProtobufMessages.FactHandle> map = new HashMap<TupleKey, ProtobufMessages.FactHandle>();
                    for( ProtobufMessages.NodeMemory.AccumulateNodeMemory.AccumulateContext _ctx : _node.getAccumulate().getContextList() ) {
                        map.put( PersisterHelper.createTupleKey( _ctx.getTuple() ), _ctx.getResultHandle() );
                    }
                    memory = map;
                    break;
                }
                case RIA: {
                    Map<TupleKey, ProtobufMessages.FactHandle> map = new HashMap<TupleKey, ProtobufMessages.FactHandle>();
                    for( ProtobufMessages.NodeMemory.RIANodeMemory.RIAContext _ctx : _node.getRia().getContextList() ) {
                        map.put( PersisterHelper.createTupleKey( _ctx.getTuple() ), _ctx.getResultHandle() );
                    }
                    memory = map;
                    break;
                }
                case FROM: {
                    Map<TupleKey, List<ProtobufMessages.FactHandle>> map = new HashMap<TupleKey, List<ProtobufMessages.FactHandle>>();
                    for( ProtobufMessages.NodeMemory.FromNodeMemory.FromContext _ctx : _node.getFrom().getContextList() ) {
                        // have to instantiate a modifiable list
                        map.put( PersisterHelper.createTupleKey( _ctx.getTuple() ), new LinkedList<ProtobufMessages.FactHandle>( _ctx.getHandleList() ) );
                    }
                    memory = map;
                    break;
                }
                case QUERY_ELEMENT: {
                    Map<TupleKey, QueryElementContext> map = new HashMap<TupleKey, QueryElementContext>();
                    for( ProtobufMessages.NodeMemory.QueryElementNodeMemory.QueryContext _ctx : _node.getQueryElement().getContextList() ) {
                        // we have to use a "cloned" query element context as we need to write on it during deserialization process and the 
                        // protobuf one is read-only
                        map.put( PersisterHelper.createTupleKey( _ctx.getTuple() ), new QueryElementContext( _ctx ) );
                    }
                    memory = map;
                    break;
                }
                default: {
                    throw new IllegalArgumentException( "Unknown node type "+_node.getNodeType()+" while deserializing session." );
                }
            }
            context.nodeMemories.put( _node.getNodeId(), memory );
        }
    }

    public static class QueryElementContext {
        public final ProtobufMessages.FactHandle handle;
        public final LinkedList<ProtobufMessages.FactHandle> results;
        
        public QueryElementContext( ProtobufMessages.NodeMemory.QueryElementNodeMemory.QueryContext _ctx ) {
            this.handle = _ctx.getHandle();
            this.results = new LinkedList<ProtobufMessages.FactHandle>( _ctx.getResultList() );
        }
    }

    private static void readInitialFactHandle(MarshallerReaderContext context,
                                              RuleData _session ) {
        int ifhId = context.wm.getInitialFactHandle().getId();
        context.handles.put( ifhId,
                             context.wm.getInitialFactHandle() );

        // special case we have to handle for the initial fact
        boolean initialFactPropagated = true;
        for ( ProtobufMessages.ActionQueue.Action _action : _session.getActionQueue().getActionList() ) {
            if( _action.getType() == ProtobufMessages.ActionQueue.ActionType.ASSERT ) {
                if( _action.getAssert().getHandleId() == ifhId ) {
                    initialFactPropagated = false;
                    break;
                }
            }
        }
        if( initialFactPropagated ) {
            assertHandleIntoOTN( context, 
                                 context.wm, 
                                 context.wm.getInitialFactHandle() );
        }
    }

    public static void readAgenda(MarshallerReaderContext context,
                                  RuleData _ruleData,
                                  DefaultAgenda agenda) {
        ProtobufMessages.Agenda _agenda = _ruleData.getAgenda();

        for ( org.drools.marshalling.impl.ProtobufMessages.Agenda.AgendaGroup _agendaGroup : _agenda.getAgendaGroupList() ) {
            BinaryHeapQueueAgendaGroup group = new BinaryHeapQueueAgendaGroup( _agendaGroup.getName(),
                                                                               context.ruleBase );
            group.setActive( _agendaGroup.getIsActive() );
            agenda.getAgendaGroupsMap().put( group.getName(),
                                             group );
        }

        for ( String _groupName : _agenda.getFocusStack().getGroupNameList() ) {
            agenda.getStackList().add( agenda.getAgendaGroup( _groupName ) );
        }

        for ( ProtobufMessages.Agenda.RuleFlowGroup _ruleFlowGroup : _agenda.getRuleFlowGroupList() ) {
            RuleFlowGroupImpl rfgi = new RuleFlowGroupImpl( _ruleFlowGroup.getName(),
                                                            _ruleFlowGroup.getIsActive(),
                                                            _ruleFlowGroup.getIsAutoDeactivate() );
            agenda.getRuleFlowGroupsMap().put( _ruleFlowGroup.getName(),
                                               rfgi );
            
//            readActivations( context, 
//                             _ruleFlowGroup.getActivationList() );
            
            for ( NodeInstance _nodeInstance : _ruleFlowGroup.getNodeInstanceList() ) {
                rfgi.addNodeInstance( _nodeInstance.getProcessInstanceId(),
                                      _nodeInstance.getNodeInstanceId() );
            }
        }

        readActivations( context,
                         _agenda.getActivationList() );
        agenda.setActivationsFilter( context.filter );
    }

    public static void readActionQueue(MarshallerReaderContext context,
                                       RuleData _session) throws IOException,
                                                                 ClassNotFoundException {
        ReteooWorkingMemory wm = (ReteooWorkingMemory) context.wm;
        Queue<WorkingMemoryAction> actionQueue = wm.getActionQueue();
        for ( ProtobufMessages.ActionQueue.Action _action : _session.getActionQueue().getActionList() ) {
            actionQueue.offer( PersisterHelper.deserializeWorkingMemoryAction( context,
                                                                               _action ) );
        }
    }

    public static void readFactHandles(MarshallerReaderContext context,
                                       org.drools.marshalling.impl.ProtobufMessages.EntryPoint _ep,
                                       ObjectStore objectStore) throws IOException,
                                                               ClassNotFoundException {
        InternalWorkingMemory wm = context.wm;

        WorkingMemoryEntryPoint entryPoint = context.wm.getEntryPoints().get( _ep.getEntryPointId() );
        // load the handles
        for ( ProtobufMessages.FactHandle _handle : _ep.getHandleList() ) {
            InternalFactHandle handle = readFactHandle( context,
                                                        entryPoint,
                                                        _handle );

            context.handles.put( handle.getId(),
                                 handle );

            if ( handle.getObject() != null ) {
                objectStore.addHandle( handle,
                                       handle.getObject() );
            }

            // add handle to object type node
            assertHandleIntoOTN( context, 
                                 wm, 
                                 handle );
        }

    }
    
    private static void assertHandleIntoOTN(MarshallerReaderContext context,
                                            InternalWorkingMemory wm,
                                            InternalFactHandle handle) {
        Object object = handle.getObject();
        InternalWorkingMemoryEntryPoint ep = (InternalWorkingMemoryEntryPoint) handle.getEntryPoint();
        ObjectTypeConf typeConf = ((InternalWorkingMemoryEntryPoint) handle.getEntryPoint()).getObjectTypeConfigurationRegistry().getObjectTypeConf( ep.getEntryPoint(),
                                                                                                                                                     object );
        final PropagationContext propagationContext = new PropagationContextImpl( wm.getNextPropagationIdCounter(),
                                                                                  PropagationContext.ASSERTION,
                                                                                  null,
                                                                                  null,
                                                                                  handle,
                                                                                  ((DefaultAgenda) wm.getAgenda()).getActiveActivations(),
                                                                                  ((DefaultAgenda) wm.getAgenda()).getDormantActivations(),
                                                                                  ep.getEntryPoint(),
                                                                                  context);

        ep.getEntryPointNode().assertObject( handle,
                                             propagationContext,
                                             typeConf,
                                             wm );

        propagationContext.evaluateActionQueue( wm );
        wm.executeQueuedActions();
    }

    public static InternalFactHandle readFactHandle(MarshallerReaderContext context,
                                                    WorkingMemoryEntryPoint entryPoint,
                                                    FactHandle _handle) throws IOException,
                                                                       ClassNotFoundException {
        Object object = null;
        ObjectMarshallingStrategy strategy = null;
        if ( _handle.hasStrategyIndex() ) {
            strategy = context.usedStrategies.get( _handle.getStrategyIndex() );
            object = strategy.unmarshal( context.strategyContexts.get( strategy ),
                                         context,
                                         _handle.getObject().toByteArray(), 
                                         (context.ruleBase == null)?null:context.ruleBase.getRootClassLoader() );
        }

        InternalFactHandle handle = null;
        switch ( _handle.getType() ) {
            case FACT : {
                handle = new DefaultFactHandle( _handle.getId(),
                                                object,
                                                _handle.getRecency(),
                                                entryPoint );
                break;
            }
            case QUERY : {
                handle = new QueryElementFactHandle( object,
                                                     _handle.getId(),
                                                     _handle.getRecency() );
                break;
            }
            case EVENT : {
                handle = new EventFactHandle( _handle.getId(),
                                              object,
                                              _handle.getRecency(),
                                              _handle.getTimestamp(),
                                              _handle.getDuration(),
                                              entryPoint );
                ((EventFactHandle) handle).setExpired( _handle.getIsExpired() );
                // the event is re-propagated through the network, so the activations counter will be recalculated
                //((EventFactHandle) handle).setActivationsCount( _handle.getActivationsCount() );
                break;
            }
            default : {
                throw new IllegalStateException( "Unable to marshal FactHandle, as type does not exist:" + _handle.getType() );
            }
        }
        return handle;
    }

    public static void readTruthMaintenanceSystem( MarshallerReaderContext context, 
                                                   RuleData _session ) throws IOException {

        TruthMaintenanceSystem tms = context.wm.getTruthMaintenanceSystem();
        ProtobufMessages.TruthMaintenanceSystem _tms = _session.getTms();
        
        for( ProtobufMessages.EqualityKey _key : _tms.getKeyList() ) {
            InternalFactHandle handle = (InternalFactHandle) context.handles.get( _key.getHandleId() );

            // ObjectTypeConf state is not marshalled, so it needs to be re-determined
            ObjectTypeConf typeConf = context.wm.getObjectTypeConfigurationRegistry().getObjectTypeConf( ((NamedEntryPoint)handle.getEntryPoint()).getEntryPoint(),
                                                                                                         handle.getObject() );
            if (!typeConf.isTMSEnabled()) {
                typeConf.enableTMS();
            }

            EqualityKey key = new EqualityKey( handle,
                                               _key.getStatus() );
            handle.setEqualityKey( key );
            for( Integer factHandleId : _key.getOtherHandleList() ) {
                handle = (InternalFactHandle) context.handles.get( factHandleId.intValue() );
                key.addFactHandle( handle );
                handle.setEqualityKey( key );
            }
            tms.put( key );
        }
        
        for( ProtobufMessages.Justification _justification : _tms.getJustificationList() ) {
            InternalFactHandle handle = (InternalFactHandle) context.handles.get( _justification.getHandleId() );
            
            for( ProtobufMessages.Activation _activation : _justification.getActivationList() ) {
                Activation activation = (Activation) context.filter.getTuplesCache().get( 
                              PersisterHelper.createActivationKey( _activation.getPackageName(), 
                                                                   _activation.getRuleName(), 
                                                                   _activation.getTuple() ) ).getObject();
                PropagationContext pc = activation.getPropagationContext();
                tms.addLogicalDependency( handle, 
                                          activation, 
                                          pc, 
                                          activation.getRule() );
            }
        }
    }

    private static void readActivations(MarshallerReaderContext context,
                                        List<ProtobufMessages.Activation> _list) {

        for ( ProtobufMessages.Activation _activation : _list ) {
            context.filter.getDormantActivationsMap().put( PersisterHelper.createActivationKey( _activation.getPackageName(),
                                                                                                _activation.getRuleName(),
                                                                                                _activation.getTuple() ),
                                                           _activation );
        }
    }

    public static void readTimer( MarshallerReaderContext inCtx, Timer _timer ) throws IOException, ClassNotFoundException {
        TimersInputMarshaller reader = inCtx.readersByInt.get( _timer.getType().getNumber() );
        reader.deserialize( inCtx, _timer );
    }

    public static Trigger readTrigger( MarshallerReaderContext inCtx,
                                       ProtobufMessages.Trigger _trigger ) {
        switch (_trigger.getType() ) {
            case CRON: {
                ProtobufMessages.Trigger.CronTrigger _cron = _trigger.getCron();
                CronTrigger trigger = new CronTrigger();
                trigger.setStartTime( new Date( _cron.getStartTime() ) );
                if (_cron.hasEndTime()) {
                    trigger.setEndTime( new Date( _cron.getEndTime() ) );
                }
                trigger.setRepeatLimit( _cron.getRepeatLimit() );
                trigger.setRepeatCount( _cron.getRepeatCount() );
                trigger.setCronExpression( _cron.getCronExpression() );
                if (_cron.hasNextFireTime()) {
                    trigger.setNextFireTime( new Date( _cron.getNextFireTime() ) );
                }
                String[] calendarNames = new String[_cron.getCalendarNameCount()];
                for( int i = 0; i < calendarNames.length; i++ ) {
                    calendarNames[i] = _cron.getCalendarName( i );
                }
                trigger.setCalendarNames( calendarNames );
                return trigger;
            }
            case INTERVAL: {
                ProtobufMessages.Trigger.IntervalTrigger _interval = _trigger.getInterval();
                IntervalTrigger trigger = new IntervalTrigger();
                trigger.setStartTime( new Date( _interval.getStartTime() ) );
                if (_interval.hasEndTime()) {
                    trigger.setEndTime( new Date( _interval.getEndTime() ) );
                }
                trigger.setRepeatLimit( _interval.getRepeatLimit() );
                trigger.setRepeatCount( _interval.getRepeatCount() );
                if (_interval.hasNextFireTime()) {
                    trigger.setNextFireTime( new Date( _interval.getNextFireTime() ) );
                }
                trigger.setPeriod( _interval.getPeriod() );
                String[] calendarNames = new String[_interval.getCalendarNameCount()];
                for( int i = 0; i < calendarNames.length; i++ ) {
                    calendarNames[i] = _interval.getCalendarName( i );
                }
                trigger.setCalendarNames( calendarNames );
                return trigger;
            }
            case POINT_IN_TIME: {
                PointInTimeTrigger trigger = new PointInTimeTrigger( _trigger.getPit().getNextFireTime(), null, null );
                return trigger;
            }
        }
        throw new RuntimeException( "Unable to deserialize Trigger for type: " + _trigger.getType() );

    }

    public static class PBActivationsFilter
            implements
            ActivationsFilter {
        private Map<ActivationKey, ProtobufMessages.Activation> dormantActivations;
        private Map<ActivationKey, LeftTuple>                   tuplesCache;

        public PBActivationsFilter() {
            this.dormantActivations = new HashMap<ProtobufInputMarshaller.ActivationKey, ProtobufMessages.Activation>();
            this.tuplesCache = new HashMap<ProtobufInputMarshaller.ActivationKey, LeftTuple>();
        }

        public Map<ActivationKey, ProtobufMessages.Activation> getDormantActivationsMap() {
            return this.dormantActivations;
        }

        public boolean accept(Activation activation,
                              PropagationContext context,
                              InternalWorkingMemory workingMemory,
                              RuleTerminalNode rtn) {
            ActivationKey key = PersisterHelper.createActivationKey( rtn.getRule().getPackageName(), rtn.getRule().getName(), activation.getTuple() );
            // add the tuple to the cache for correlation
            this.tuplesCache.put( key, activation.getTuple() );
            // check if there was an active activation for it
            return !this.dormantActivations.containsKey( key );
        }

        public Map<ActivationKey, LeftTuple> getTuplesCache() {
            return tuplesCache;
        }
    }

    public static class ActivationKey {

        private final String pkgName;
        private final String ruleName;
        private final int[]  tuple;

        public ActivationKey(String pkgName,
                             String ruleName,
                             int[] tuple) {
            this.pkgName = pkgName;
            this.ruleName = ruleName;
            this.tuple = tuple;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((pkgName == null) ? 0 : pkgName.hashCode());
            result = prime * result + ((ruleName == null) ? 0 : ruleName.hashCode());
            result = prime * result + Arrays.hashCode( tuple );
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            ActivationKey other = (ActivationKey) obj;
            if ( pkgName == null ) {
                if ( other.pkgName != null ) return false;
            } else if ( !pkgName.equals( other.pkgName ) ) return false;
            if ( ruleName == null ) {
                if ( other.ruleName != null ) return false;
            } else if ( !ruleName.equals( other.ruleName ) ) return false;
            if ( !Arrays.equals( tuple, other.tuple ) ) return false;
            return true;
        }
    }
    
    public static class TupleKey {
        private final int[] tuple;

        public TupleKey(int[] tuple) {
            super();
            this.tuple = tuple;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + Arrays.hashCode( tuple );
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            TupleKey other = (TupleKey) obj;
            if ( !Arrays.equals( tuple, other.tuple ) ) return false;
            return true;
        }
    }
}
