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
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.drools.RuntimeDroolsException;
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
import org.drools.core.util.KeyStoreHelper;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.impl.ProtobufMessages.Agenda.RuleFlowGroup.NodeInstance;
import org.drools.marshalling.impl.ProtobufMessages.FactHandle;
import org.drools.marshalling.impl.ProtobufMessages.Header;
import org.drools.marshalling.impl.ProtobufMessages.KnowledgeSession;
import org.drools.marshalling.impl.ProtobufMessages.Timers.Timer;
import org.drools.reteoo.InitialFactImpl;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.ObjectTypeConf;
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

/**
 * An input marshaller that uses protobuf. 
 * 
 * @author etirelli
 */
public class ProtobufInputMarshaller {
    // NOTE: all variables prefixed with _ (underscore) are protobuf structs

    //    private static ProcessMarshaller processMarshaller = createProcessMarshaller();
    //
    //    private static ProcessMarshaller createProcessMarshaller() {
    //        try {
    //            return ProcessMarshallerFactory.newProcessMarshaller();
    //        } catch (IllegalArgumentException e) {
    //            return null;
    //        }
    //    }

    /**
     * Stream the data into an existing session
     * 
     * @param session
     * @param context
     * @param id
     * @param executor
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
        session.reset( _session.getLastId(),
                       _session.getLastRecency(),
                       0 );
        DefaultAgenda agenda = (DefaultAgenda) session.getAgenda();

        readAgenda( context,
                    _session,
                    agenda );
        return agenda;
    }

    private static ReteooStatefulSession createAndInitializeSession(MarshallerReaderContext context,
                                                                    int id,
                                                                    ExecutorService executor,
                                                                    Environment environment,
                                                                    SessionConfiguration config,
                                                                    ProtobufMessages.KnowledgeSession _session) throws IOException {
        FactHandleFactory handleFactory = context.ruleBase.newFactHandleFactory( _session.getLastId(),
                                                                                 _session.getLastRecency() );

        InternalFactHandle initialFactHandle = new DefaultFactHandle( _session.getInitialFact().getId(),
                                                                      InitialFactImpl.getInstance(),
                                                                      _session.getInitialFact().getRecency(),
                                                                      null );
        context.handles.put( initialFactHandle.getId(),
                             initialFactHandle );

        DefaultAgenda agenda = new DefaultAgenda( context.ruleBase,
                                                  false );
        readAgenda( context,
                    _session,
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

    private static ProtobufMessages.KnowledgeSession loadAndParseSession(MarshallerReaderContext context) throws IOException {
        ProtobufMessages.Header _header = ProtobufMessages.Header.parseFrom( context.stream );

        loadStrategiesIndex( context, _header );

        byte[] sessionbuff = _header.getKsession().toByteArray();

        // should we check version as well here?
        checkSignature( _header, sessionbuff );

        return ProtobufMessages.KnowledgeSession.parseFrom( sessionbuff );
    }

    private static void loadStrategiesIndex(MarshallerReaderContext context,
                                            ProtobufMessages.Header _header) {
        for ( ProtobufMessages.Header.StrategyIndex _entry : _header.getStrategyList() ) {
            ObjectMarshallingStrategy strategyObject = context.resolverStrategyFactory.getStrategyObject( _entry.getName() );
            if ( strategyObject == null ) {
                throw new IllegalStateException( "No strategy of type " + _entry.getName() + " available." );
            }
            context.usedStrategies.put( _entry.getId(), strategyObject );
        }
    }

    private static void checkSignature(Header _header,
                                       byte[] sessionbuff) {
        KeyStoreHelper helper = new KeyStoreHelper();
        boolean signed = _header.hasSignature();
        if ( helper.isSigned() != signed ) {
            throw new RuntimeDroolsException( "This environment is configured to work with " +
                                              (helper.isSigned() ? "signed" : "unsigned") +
                                              " serialized objects, but the given object is " +
                                              (signed ? "signed" : "unsigned") + ". Deserialization aborted." );
        }
        if ( signed ) {
            if ( helper.getPubKeyStore() == null ) {
                throw new RuntimeDroolsException( "The session was serialized with a signature. Please configure a public keystore with the public key to check the signature. Deserialization aborted." );
            }
            try {
                if ( !helper.checkDataWithPublicKey( _header.getKeyAlias(),
                                                     sessionbuff,
                                                     _header.getSignature().toByteArray() ) ) {
                    throw new RuntimeDroolsException(
                                                      "Signature does not match serialized package. This is a security violation. Deserialisation aborted." );
                }
            } catch ( InvalidKeyException e ) {
                throw new RuntimeDroolsException( "Invalid key checking signature: " + e.getMessage(),
                                                  e );
            } catch ( KeyStoreException e ) {
                throw new RuntimeDroolsException( "Error accessing Key Store: " + e.getMessage(),
                                                  e );
            } catch ( NoSuchAlgorithmException e ) {
                throw new RuntimeDroolsException( "No algorithm available: " + e.getMessage(),
                                                  e );
            } catch ( SignatureException e ) {
                throw new RuntimeDroolsException( "Signature Exception: " + e.getMessage(),
                                                  e );
            }
        }
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
                          _session );

        readInitialFactHandle( context, 
                               _session );
        
        for ( ProtobufMessages.EntryPoint _ep : _session.getEntryPointList() ) {
            WorkingMemoryEntryPoint wmep = context.wm.getEntryPoints().get( _ep.getEntryPointId() );
            readFactHandles( context,
                             _ep,
                             ((NamedEntryPoint) wmep).getObjectStore() );
        }

        readActionQueue( context,
                         _session );

        //        InternalFactHandle handle = context.wm.getInitialFactHandle();
        //        while (context.stream.readShort() == PersisterEnums.LEFT_TUPLE) {
        //            LeftTupleSink sink = (LeftTupleSink) context.sinks.get( context.stream.readInt() );
        //            LeftTuple leftTuple = sink.createLeftTuple( handle,
        //                                                        sink,
        //                                                        true );
        //            readLeftTuple( leftTuple,
        //                           context );
        //        }
        //
        //        readPropagationContexts( context );
        //
        //
        readTruthMaintenanceSystem( context,
                                    _session );
        
        //
        //        if (processMarshaller != null) {
        //            processMarshaller.readProcessInstances( context );
        //        }
        //        else {
        //            short type = context.stream.readShort();
        //            if (PersisterEnums.END != type) {
        //                throw new IllegalStateException( "No process marshaller, unable to unmarshall type: " + type );
        //            }
        //        }
        //
        //        if (processMarshaller != null) {
        //            processMarshaller.readWorkItems( context );
        //        }
        //        else {
        //            short type = context.stream.readShort();
        //            if (PersisterEnums.END != type) {
        //                throw new IllegalStateException( "No process marshaller, unable to unmarshall type: " + type );
        //            }
        //        }
        //
        //        if (processMarshaller != null) {
        //            // This actually does ALL timers, due to backwards compatability issues
        //            // It will read in old JBPM binaries, but always write to the new binary format.
        //            processMarshaller.readProcessTimers( context );
        //        } else {
        //            short type = context.stream.readShort();
        //            if (PersisterEnums.END != type) {
        //                throw new IllegalStateException( "No process marshaller, unable to unmarshall type: " + type );
        //            }
        //        }
        //
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
                                         KnowledgeSession _session) {
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
                                              KnowledgeSession _session ) {
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
                                  KnowledgeSession _session,
                                  DefaultAgenda agenda) {
        org.drools.marshalling.impl.ProtobufMessages.Agenda _agenda = _session.getAgenda();
        //agenda.setDormantActivations( _agenda.getDormantActivations() );
        //agenda.setActiveActivations( _agenda.getActiveActivations() );

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
            
            readActivations( context, 
                             _ruleFlowGroup.getActivationList() );
            
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
                                       KnowledgeSession _session) throws IOException,
                                                                 ClassNotFoundException {
        ReteooWorkingMemory wm = (ReteooWorkingMemory) context.wm;
        Queue<WorkingMemoryAction> actionQueue = wm.getActionQueue();
        for ( ProtobufMessages.ActionQueue.Action _action : _session.getActionQueue().getActionList() ) {
            actionQueue.offer( PersisterHelper.deserializeWorkingMemoryAction( context,
                                                                               _action ) );
        }
    }

    //
    //    public static void readTruthMaintenanceSystem( MarshallerReaderContext context ) throws IOException {
    //        ObjectInputStream stream = context.stream;
    //
    //        TruthMaintenanceSystem tms = context.wm.getTruthMaintenanceSystem();
    //        while (stream.readShort() == PersisterEnums.EQUALITY_KEY) {
    //            int status = stream.readInt();
    //            int factHandleId = stream.readInt();
    //            InternalFactHandle handle = (InternalFactHandle) context.handles.get( factHandleId );
    //
    //            // ObjectTypeConf state is not marshalled, so it needs to be re-determined
    //            ObjectTypeConf typeConf = context.wm.getObjectTypeConfigurationRegistry().getObjectTypeConf( context.wm.getEntryPoint(),
    //                                                                                                         handle.getObject() );
    //            if (!typeConf.isTMSEnabled()) {
    //                typeConf.enableTMS();
    //            }
    //
    //            EqualityKey key = new EqualityKey( handle,
    //                                               status );
    //            handle.setEqualityKey( key );
    //            while (stream.readShort() == PersisterEnums.FACT_HANDLE) {
    //                factHandleId = stream.readInt();
    //                handle = (InternalFactHandle) context.handles.get( factHandleId );
    //                key.addFactHandle( handle );
    //                handle.setEqualityKey( key );
    //            }
    //            tms.put( key );
    //        }
    //    }
    //
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
            // we probably need to use the proper classloader for the following
            object = strategy.unmarshal( _handle.getObject().toByteArray(), (context.ruleBase == null)?null:context.ruleBase.getRootClassLoader() );
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
                                                   KnowledgeSession _session ) throws IOException {

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

    //
    //    public static void readBehaviors( WindowNode windowNode,
    //                                      WindowMemory memory,
    //                                      MarshallerReaderContext inCtx ) throws IOException {
    //        short token = -1;
    //        while (( token = inCtx.readShort() ) != PersisterEnums.END) {
    //            int i = inCtx.readInt();
    //            Object object = ( (Object[]) memory.behaviorContext )[i];
    //            switch (token) {
    //                case PersisterEnums.SLIDING_TIME_WIN: {
    //                    readSlidingTimeWindowBehaviour( windowNode,
    //                                                    memory,
    //                                                    (SlidingTimeWindow) windowNode.getBehaviors()[i],
    //                                                    (SlidingTimeWindowContext) object,
    //                                                    inCtx );
    //                    break;
    //                }
    //                case PersisterEnums.SLIDING_LENGTH_WIN: {
    //                    readSlidingLengthWindowBehaviour( windowNode,
    //                                                      memory,
    //                                                      (SlidingLengthWindow) windowNode.getBehaviors()[i],
    //                                                      (SlidingLengthWindowContext) object,
    //                                                      inCtx );
    //                    break;
    //                }
    //            }
    //
    //        }
    //    }
    //
    //    public static void readSlidingTimeWindowBehaviour( WindowNode windowNode,
    //            WindowMemory memory,
    //            SlidingTimeWindow stw,
    //            SlidingTimeWindowContext stwCtx,
    //            MarshallerReaderContext inCtx ) throws IOException {
    //
    //        if (inCtx.readBoolean()) {
    //            int sinkId = inCtx.readInt();
    //            int factId = inCtx.readInt();
    //
    //            RightTupleSink sink = (RightTupleSink) inCtx.sinks.get( sinkId );
    //            RightTupleKey key = new RightTupleKey( factId,
    //                                                   sink );
    //            RightTuple rightTuple = inCtx.rightTuples.get( key );
    //
    //            //FIXME: stwCtx.expiringTuple = rightTuple;
    //        }
    //
    //        if (inCtx.readBoolean()) {
    //            int size = inCtx.readInt();
    //            for (int i = 0; i < size; i++) {
    //                int sinkId = inCtx.readInt();
    //                int factId = inCtx.readInt();
    //
    //                RightTupleSink sink = (RightTupleSink) inCtx.sinks.get( sinkId );
    //                RightTupleKey key = new RightTupleKey( factId,
    //                                                       sink );
    //                RightTuple rightTuple = inCtx.rightTuples.get( key );
    //
    //                //FIXME: stwCtx.queue.add( rightTuple );
    //            }
    //        }
    //    }
    //
    //    public static void readSlidingLengthWindowBehaviour( WindowNode windowNode,
    //            WindowMemory memory,
    //            SlidingLengthWindow slw,
    //            SlidingLengthWindowContext slwCtx,
    //            MarshallerReaderContext inCtx ) throws IOException {
    //        int pos = inCtx.readInt();
    //        int length = inCtx.readInt();
    //
    //        slwCtx.pos = pos;
    //        //FIXME: slwCtx.rightTuples = new RightTuple[length];
    //        for (int i = 0; i < length; i++) {
    //            int factId = inCtx.readInt();
    //
    //            if (factId >= 0) {
    //                int sinkId = inCtx.readInt();
    //
    //                RightTupleSink sink = (RightTupleSink) inCtx.sinks.get( sinkId );
    //                RightTupleKey key = new RightTupleKey( factId,
    //                                                       sink );
    //                RightTuple rightTuple = inCtx.rightTuples.get( key );
    //
    //                //FIXME: slwCtx.rightTuples[i] = rightTuple;
    //            }
    //
    //        }
    //    }
    //
    //    private static void addToLeftMemory( LeftTuple parentLeftTuple,
    //            BetaMemory memory ) {
    //        memory.getLeftTupleMemory().add( parentLeftTuple );
    //        memory.linkRight();
    //    }
    //
    private static void readActivations(MarshallerReaderContext context,
                                        List<ProtobufMessages.Activation> _list) {

        for ( ProtobufMessages.Activation _activation : _list ) {
            context.filter.getActivationsMap().put( PersisterHelper.createActivationKey( _activation.getPackageName(),
                                                                                         _activation.getRuleName(),
                                                                                         _activation.getTuple() ),
                                                    _activation );
        }
    }

    //
    //    public static void readPropagationContexts( MarshallerReaderContext context ) throws IOException {
    //        ObjectInputStream stream = context.stream;
    //
    //        while (stream.readShort() == PersisterEnums.PROPAGATION_CONTEXT) {
    //            readPropagationContext( context );
    //        }
    //
    //    }
    //
    //    public static void readPropagationContext( MarshallerReaderContext context ) throws IOException {
    //        ObjectInputStream stream = context.stream;
    //        InternalRuleBase ruleBase = context.ruleBase;
    //
    //        int type = stream.readInt();
    //
    //        Rule rule = null;
    //        if (stream.readBoolean()) {
    //            String pkgName = stream.readUTF();
    //            String ruleName = stream.readUTF();
    //            Package pkg = ruleBase.getPackage( pkgName );
    //            rule = pkg.getRule( ruleName );
    //        }
    //
    //        LeftTuple leftTuple = null;
    //        if (stream.readBoolean()) {
    //            int tuplePos = stream.readInt();
    //            leftTuple = context.terminalTupleMap.get( tuplePos );
    //        }
    //
    //        long propagationNumber = stream.readLong();
    //
    //        int factHandleId = stream.readInt();
    //        InternalFactHandle factHandle = context.handles.get( factHandleId );
    //
    //        int activeActivations = stream.readInt();
    //        int dormantActivations = stream.readInt();
    //        String entryPointId = stream.readUTF();
    //
    //        EntryPoint entryPoint = context.entryPoints.get( entryPointId );
    //        if (entryPoint == null) {
    //            entryPoint = new EntryPoint( entryPointId );
    //            context.entryPoints.put( entryPointId,
    //                                     entryPoint );
    //        }
    //
    //        PropagationContext pc = new PropagationContextImpl( propagationNumber,
    //                                                            type,
    //                                                            rule,
    //                                                            leftTuple,
    //                                                            factHandle,
    //                                                            activeActivations,
    //                                                            dormantActivations,
    //                                                            entryPoint );
    //        context.propagationContexts.put( propagationNumber,
    //                                         pc );
    //    }
    //
    //    public static WorkItem readWorkItem( MarshallerReaderContext context ) throws IOException {
    //        ObjectInputStream stream = context.stream;
    //
    //        WorkItemImpl workItem = new WorkItemImpl();
    //        workItem.setId( stream.readLong() );
    //        workItem.setProcessInstanceId( stream.readLong() );
    //        workItem.setName( stream.readUTF() );
    //        workItem.setState( stream.readInt() );
    //
    //        //WorkItem Paramaters
    //        int nbVariables = stream.readInt();
    //        if (nbVariables > 0) {
    //
    //            for (int i = 0; i < nbVariables; i++) {
    //                String name = stream.readUTF();
    //                try {
    //                    int index = stream.readInt();
    //                    ObjectMarshallingStrategy strategy = null;
    //                    // Old way of retrieving strategy objects
    //                    if (index >= 0) {
    //                        strategy = context.resolverStrategyFactory.getStrategy( index );
    //                        if (strategy == null) {
    //                            throw new IllegalStateException( "No strategy of with index " + index + " available." );
    //                        }
    //                    }
    //                    // New way 
    //                    else if (index == -2) {
    //                        String strategyClassName = stream.readUTF();
    //                        strategy = context.resolverStrategyFactory.getStrategyObject( strategyClassName );
    //                        if (strategy == null) {
    //                            throw new IllegalStateException( "No strategy of type " + strategyClassName + " available." );
    //                        }
    //                    }
    //
    //                    Object value = strategy.read( stream );
    //                    workItem.setParameter( name,
    //                                           value );
    //                } catch (ClassNotFoundException e) {
    //                    throw new IllegalArgumentException(
    //                                                        "Could not reload variable " + name );
    //                }
    //            }
    //        }
    //
    //        return workItem;
    //    }
    //
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
        private Map<ActivationKey, ProtobufMessages.Activation> activeActivations;
        private Map<ActivationKey, LeftTuple>                   tuplesCache;

        public PBActivationsFilter() {
            this.activeActivations = new HashMap<ProtobufInputMarshaller.ActivationKey, ProtobufMessages.Activation>();
            this.tuplesCache = new HashMap<ProtobufInputMarshaller.ActivationKey, LeftTuple>();
        }

        public Map<ActivationKey, ProtobufMessages.Activation> getActivationsMap() {
            return this.activeActivations;
        }

        public boolean accept(Activation activation,
                              PropagationContext context,
                              InternalWorkingMemory workingMemory,
                              RuleTerminalNode rtn) {
            ActivationKey key = PersisterHelper.createActivationKey( rtn.getRule().getPackageName(), rtn.getRule().getName(), activation.getTuple() );
            // add the tuple to the cache for correlation
            this.tuplesCache.put( key, activation.getTuple() );
            // check if there was an active activation for it
            return this.activeActivations.containsKey( key );
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
