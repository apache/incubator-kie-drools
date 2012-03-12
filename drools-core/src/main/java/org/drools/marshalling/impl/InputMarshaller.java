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
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.drools.RuntimeDroolsException;
import org.drools.SessionConfiguration;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.common.*;
import org.drools.concurrent.ExecutorService;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.ObjectHashSet;
import org.drools.core.util.StringUtils;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.reteoo.AccumulateNode.AccumulateContext;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.FromNode.FromMemory;
import org.drools.reteoo.InitialFactImpl;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleImpl;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.NodeTypeEnums;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.QueryElementNode;
import org.drools.reteoo.ReteooRuleBase;
import org.drools.reteoo.QueryElementNode.UnificationNodeViewChangedEventListener;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTupleSink;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.rule.EntryPoint;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.rule.SlidingLengthWindow;
import org.drools.rule.SlidingLengthWindow.SlidingLengthWindowContext;
import org.drools.rule.SlidingTimeWindow;
import org.drools.rule.SlidingTimeWindow.SlidingTimeWindowContext;
import org.drools.runtime.Environment;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleFlowGroup;
import org.drools.time.Trigger;
import org.drools.time.impl.CronTrigger;
import org.drools.time.impl.IntervalTrigger;
import org.drools.time.impl.PointInTimeTrigger;
import org.drools.time.impl.PseudoClockScheduler;

public class InputMarshaller {

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
     * @param id
     * @param executor
     * @return
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public static ReteooStatefulSession readSession( ReteooStatefulSession session,
            MarshallerReaderContext context ) throws IOException,
            ClassNotFoundException {
        boolean multithread = context.readBoolean();
        long time = context.readLong();
        int handleId = context.readInt();
        long handleCounter = context.readLong();
        long propagationCounter = context.readLong();

        // these are for the InitialFactHandle, on a reset we just ignore
        context.readInt();
        context.readLong();

        session.reset( handleId,
                       handleCounter,
                       propagationCounter );
        DefaultAgenda agenda = (DefaultAgenda) session.getAgenda();

        readAgenda( context,
                    agenda );

        return readSession( session,
                            agenda,
                            time,
                            multithread,
                            context );
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
    public static ReteooStatefulSession readSession( MarshallerReaderContext context,
            int id,
            ExecutorService executor ) throws IOException,
            ClassNotFoundException {
        return readSession( context,
                            id,
                            executor,
                            EnvironmentFactory.newEnvironment(),
                            SessionConfiguration.getDefaultInstance() );
    }

    public static ReteooStatefulSession readSession( MarshallerReaderContext context,
            int id,
            ExecutorService executor,
            Environment environment,
            SessionConfiguration config ) throws IOException,
            ClassNotFoundException {

        boolean multithread = context.readBoolean();

        long time = context.readLong();

        FactHandleFactory handleFactory = context.ruleBase.newFactHandleFactory( context.readInt(),
                                                                                 context.readLong() );

        long propagationCounter = context.readLong();

        InternalFactHandle initialFactHandle = new DefaultFactHandle( context.readInt(), //id
                                                                      InitialFactImpl.getInstance(),
                                                                      context.readLong(),
                                                                      null );

        context.handles.put( initialFactHandle.getId(),
                             initialFactHandle );

        DefaultAgenda agenda = new DefaultAgenda( context.ruleBase,
                                                  false );
        readAgenda( context,
                    agenda );
        ReteooStatefulSession session = new ReteooStatefulSession( id,
                                                                   context.ruleBase,
                                                                   executor,
                                                                   handleFactory,
                                                                   initialFactHandle,
                                                                   propagationCounter,
                                                                   config,
                                                                   agenda,
                                                                   environment );
        new StatefulKnowledgeSessionImpl( session );

        initialFactHandle.setEntryPoint( session.getEntryPoints().get( EntryPoint.DEFAULT.getEntryPointId() ) );

        return readSession( session,
                            agenda,
                            time,
                            multithread,
                            context );
    }

    public static ReteooStatefulSession readSession( ReteooStatefulSession session,
            DefaultAgenda agenda,
            long time,
            boolean multithread,
            MarshallerReaderContext context ) throws IOException, ClassNotFoundException {
        if (session.getTimerService() instanceof PseudoClockScheduler) {
            PseudoClockScheduler clock = (PseudoClockScheduler) session.getTimerService();
            clock.advanceTime( time,
                               TimeUnit.MILLISECONDS );
        }

        // RuleFlowGroups need to reference the session
        for (RuleFlowGroup group : agenda.getRuleFlowGroupsMap().values()) {
            ( (RuleFlowGroupImpl) group ).setWorkingMemory( session );
        }

        context.wm = session;

        context.handles.put( context.wm.getInitialFactHandle().getId(),
                             context.wm.getInitialFactHandle() );

        if (context.stream.readBoolean()) {
            InternalFactHandle initialFactHandle = context.wm.getInitialFactHandle();
            int sinkId = context.stream.readInt();
            ObjectTypeNode initialFactNode = (ObjectTypeNode) context.sinks.get( sinkId );
            if (initialFactNode == null) {
                // ------ START RANT ------
                // The following code is as bad as it looks, but since I was so far 
                // unable to convince Mark that creating OTNs on demand is really bad,
                // I have to continue doing it :)
                EntryPointNode defaultEPNode = context.ruleBase.getRete().getEntryPointNode( EntryPoint.DEFAULT );
                BuildContext buildContext = new BuildContext( context.ruleBase,
                                                              context.ruleBase.getReteooBuilder().getIdGenerator() );
                buildContext.setPartitionId(RuleBasePartitionId.MAIN_PARTITION);
                buildContext.setObjectTypeNodeMemoryEnabled( true );
                initialFactNode = new ObjectTypeNode( sinkId, 
                                                      defaultEPNode, 
                                                      ClassObjectType.InitialFact_ObjectType,
                                                      buildContext );
                // isn't contention something everybody loves?
                context.ruleBase.lock();
                try {
                    // Yeah, I know, because one session is being deserialized, we go and lock all of them...
                    InternalWorkingMemory[] wms = buildContext.getWorkingMemories();
                    if ( wms.length > 0 ) {
                        initialFactNode.attach( wms );
                    } else {
                        initialFactNode.attach();
                    }
                } finally {
                    context.ruleBase.unlock();
                }
                // ------- END RANT -----
            }
            ObjectHashSet initialFactMemory = (ObjectHashSet) context.wm.getNodeMemory( initialFactNode );

            initialFactMemory.add( initialFactHandle );
            readRightTuples( initialFactHandle,
                             context );
        }
        while ( context.readShort() == PersisterEnums.ENTRY_POINT) {
            String entryPointId = context.stream.readUTF();
            WorkingMemoryEntryPoint wmep = context.wm.getEntryPoints().get( entryPointId );
            readFactHandles( context,
                             ( (NamedEntryPoint) wmep ).getObjectStore() );
        }
        InternalFactHandle handle = context.wm.getInitialFactHandle();
        while (context.stream.readShort() == PersisterEnums.LEFT_TUPLE) {
            LeftTupleSink sink = (LeftTupleSink) context.sinks.get( context.stream.readInt() );
            LeftTuple leftTuple = sink.createLeftTuple( handle,
                                                        sink,
                                                        true );
            readLeftTuple( leftTuple,
                           context );
        }

        readPropagationContexts( context );

        readActivations( context );

        readActionQueue( context );

        readTruthMaintenanceSystem( context );

        if (processMarshaller != null) {
            processMarshaller.readProcessInstances( context );
        }
        else {
            short type = context.stream.readShort();
            if (PersisterEnums.END != type) {
                throw new IllegalStateException( "No process marshaller, unable to unmarshall type: " + type );
            }
        }

        if (processMarshaller != null) {
            processMarshaller.readWorkItems( context );
        }
        else {
            short type = context.stream.readShort();
            if (PersisterEnums.END != type) {
                throw new IllegalStateException( "No process marshaller, unable to unmarshall type: " + type );
            }
        }

        if (processMarshaller != null) {
            // This actually does ALL timers, due to backwards compatability issues
            // It will read in old JBPM binaries, but always write to the new binary format.
            processMarshaller.readProcessTimers( context );
        } else {
            short type = context.stream.readShort();
            if (PersisterEnums.END != type) {
                throw new IllegalStateException( "No process marshaller, unable to unmarshall type: " + type );
            }
        }

        // no legacy jBPM timers, so handle locally
        while ( context.readShort() == PersisterEnums.DEFAULT_TIMER) {
            InputMarshaller.readTimer( context );
        }

        if (multithread) {
            session.startPartitionManagers();
        }

        return session;
    }

    public static void readAgenda( MarshallerReaderContext context,
            DefaultAgenda agenda ) throws IOException {
        ObjectInputStream stream = context.stream;

        agenda.setDormantActivations( stream.readInt() );
        agenda.setActiveActivations( stream.readInt() );

        while (stream.readShort() == PersisterEnums.AGENDA_GROUP) {
            BinaryHeapQueueAgendaGroup group = new BinaryHeapQueueAgendaGroup( stream.readUTF(),
                                                                               context.ruleBase );
            group.setActive( stream.readBoolean() );
            agenda.getAgendaGroupsMap().put( group.getName(),
                                             group );
        }

        while (stream.readShort() == PersisterEnums.AGENDA_GROUP) {
            String agendaGroupName = stream.readUTF();
            agenda.getStackList().add( agenda.getAgendaGroup( agendaGroupName ) );
        }

        while (stream.readShort() == PersisterEnums.RULE_FLOW_GROUP) {
            String rfgName = stream.readUTF();
            boolean active = stream.readBoolean();
            boolean autoDeactivate = stream.readBoolean();
            RuleFlowGroupImpl rfg = new RuleFlowGroupImpl( rfgName,
                                                           active,
                                                           autoDeactivate );
            agenda.getRuleFlowGroupsMap().put( rfgName,
                                               rfg );
            int nbNodeInstances = stream.readInt();
            for (int i = 0; i < nbNodeInstances; i++) {
                Long processInstanceId = stream.readLong();
                String nodeInstanceId = stream.readUTF();
                rfg.addNodeInstance( processInstanceId,
                                     nodeInstanceId );
            }
        }

    }

    public static void readActionQueue( MarshallerReaderContext context ) throws IOException, ClassNotFoundException {
        ReteooWorkingMemory wm = (ReteooWorkingMemory) context.wm;
        Queue<WorkingMemoryAction> actionQueue = wm.getActionQueue();
        while (context.readShort() == PersisterEnums.WORKING_MEMORY_ACTION) {
            actionQueue.offer( PersisterHelper.readWorkingMemoryAction( context ) );
        }
    }

    public static void readTruthMaintenanceSystem( MarshallerReaderContext context ) throws IOException {
        ObjectInputStream stream = context.stream;

        TruthMaintenanceSystem tms = context.wm.getTruthMaintenanceSystem();
        while (stream.readShort() == PersisterEnums.EQUALITY_KEY) {
            int status = stream.readInt();
            int factHandleId = stream.readInt();
            InternalFactHandle handle = (InternalFactHandle) context.handles.get( factHandleId );

            // ObjectTypeConf state is not marshalled, so it needs to be re-determined
            ObjectTypeConf typeConf = context.wm.getObjectTypeConfigurationRegistry().getObjectTypeConf( context.wm.getEntryPoint(),
                                                                                                         handle.getObject() );
            if (!typeConf.isTMSEnabled()) {
                typeConf.enableTMS();
            }

            EqualityKey key = new EqualityKey( handle,
                                               status );
            handle.setEqualityKey( key );
            while (stream.readShort() == PersisterEnums.FACT_HANDLE) {
                factHandleId = stream.readInt();
                handle = (InternalFactHandle) context.handles.get( factHandleId );
                key.addFactHandle( handle );
                handle.setEqualityKey( key );
            }
            tms.put( key );
        }
    }

    public static void readFactHandles( MarshallerReaderContext context,
            ObjectStore objectStore ) throws IOException,
            ClassNotFoundException {
        ObjectInputStream stream = context.stream;
        InternalWorkingMemory wm = context.wm;

        int size = stream.readInt();

        // load the handles
        InternalFactHandle[] handles = new InternalFactHandle[size];
        for (int i = 0; i < size; i++) {
            InternalFactHandle handle = readFactHandle( context );

            context.handles.put( handle.getId(),
                                 handle );
            handles[i] = handle;

            if (handle.getObject() != null) {
                objectStore.addHandle( handle,
                                       handle.getObject() );
            }

            readRightTuples( handle,
                             context );
        }

        readLeftTuples( context ); // object store

        if (stream.readBoolean()) {
            readLeftTuples( context ); // activation fact handles
        }

        // add handles to object type nodes
        for (InternalFactHandle factHandle : handles) {
            Object object = factHandle.getObject();

            EntryPoint ep = ( (InternalWorkingMemoryEntryPoint) factHandle.getEntryPoint() ).getEntryPoint();

            ObjectTypeConf typeConf = ( (InternalWorkingMemoryEntryPoint) factHandle.getEntryPoint() ).getObjectTypeConfigurationRegistry().getObjectTypeConf( ep,
                                                                                                                                                               object );
            ObjectTypeNode[] cachedNodes = typeConf.getObjectTypeNodes();
            for (int i = 0, length = cachedNodes.length; i < length; i++) {
                ObjectHashSet set = (ObjectHashSet) wm.getNodeMemory( cachedNodes[i] );
                set.add( factHandle,
                         false );
            }
        }
    }

    public static InternalFactHandle readFactHandle( MarshallerReaderContext context ) throws IOException,
            ClassNotFoundException {
        int type = context.stream.readInt();
        int id = context.stream.readInt();
        long recency = context.stream.readLong();

        long startTimeStamp = 0;
        long duration = 0;
        boolean expired = false;
        long activationsCount = 0;
        if (type == 2) {
            startTimeStamp = context.stream.readLong();
            duration = context.stream.readLong();
            expired = context.stream.readBoolean();
            activationsCount = context.stream.readLong();
        }

        int strategyIndex = context.stream.readInt();
        Object object = null;
        ObjectMarshallingStrategy strategy = null;
        // This is the old way of de/serializing strategy objects
        if (strategyIndex >= 0) {
            strategy = context.resolverStrategyFactory.getStrategy( strategyIndex );
        }
        // This is the new way 
        else if (strategyIndex == -2) {
            String strategyClassName = context.stream.readUTF();
            if (!StringUtils.isEmpty( strategyClassName )) {
                strategy = context.resolverStrategyFactory.getStrategyObject( strategyClassName );
                if (strategy == null) {
                    throw new IllegalStateException( "No strategy of type " + strategyClassName + " available." );
                }
            }
        }

        // If either way retrieves a strategy, use it
        if (strategy != null) {
            object = strategy.read( context.stream );
        }

        WorkingMemoryEntryPoint entryPoint = null;
        if (context.readBoolean()) {
            String entryPointId = context.readUTF();
            if (entryPointId != null && !entryPointId.equals( "" )) {
                entryPoint = context.wm.getEntryPoints().get( entryPointId );
            }
        }
        InternalFactHandle handle = null;
        switch (type) {
            case 0: {

                handle = new DefaultFactHandle( id,
                                                object,
                                                recency,
                                                entryPoint );
                break;

            }
            case 1: {
                handle = new QueryElementFactHandle( object,
                                                     id,
                                                     recency );
                break;
            }
            case 2: {
                handle = new EventFactHandle( id, object, recency, startTimeStamp, duration, entryPoint );
                ( (EventFactHandle) handle ).setExpired( expired );
                ( (EventFactHandle) handle ).setActivationsCount( activationsCount );
                break;
            }
            default: {
                throw new IllegalStateException( "Unable to marshal FactHandle, as type does not exist:" + type );
            }
        }

        return handle;
    }

    public static void readRightTuples( InternalFactHandle factHandle,
            MarshallerReaderContext context ) throws IOException {
        ObjectInputStream stream = context.stream;
        while (stream.readShort() == PersisterEnums.RIGHT_TUPLE) {
            readRightTuple( context,
                            factHandle );
        }
    }

    public static void readRightTuple( MarshallerReaderContext context,
            InternalFactHandle factHandle ) throws IOException {
        ObjectInputStream stream = context.stream;

        int sinkId = stream.readInt();
        RightTupleSink sink = ( sinkId >= 0 ) ? (RightTupleSink) context.sinks.get( sinkId ) : null;

        RightTuple rightTuple = new RightTuple( factHandle,
                                                sink );
        context.rightTuples.put( new RightTupleKey( factHandle.getId(),
                                                    sink ),
                                 rightTuple );

        if (sink != null) {
            BetaMemory memory = null;
            switch (sink.getType()) {
                case NodeTypeEnums.AccumulateNode: {
                    memory = ( (AccumulateMemory) context.wm.getNodeMemory( (BetaNode) sink ) ).betaMemory;
                    break;
                }
                default: {
                    memory = (BetaMemory) context.wm.getNodeMemory( (BetaNode) sink );
                    break;
                }
            }
            memory.getRightTupleMemory().add( rightTuple );
            memory.linkLeft();
        }
    }

    public static void readLeftTuples( MarshallerReaderContext context ) throws IOException,
            ClassNotFoundException {
        ObjectInputStream stream = context.stream;

        while (stream.readShort() == PersisterEnums.LEFT_TUPLE) {
            int nodeId = stream.readInt();
            LeftTupleSink sink = (LeftTupleSink) context.sinks.get( nodeId );
            int factHandleId = stream.readInt();
            LeftTuple leftTuple = sink.createLeftTuple( context.handles.get( factHandleId ),
                                                        sink,
                                                        true );
            readLeftTuple( leftTuple,
                           context );
        }
    }

    public static void readLeftTuple( LeftTuple parentLeftTuple,
            MarshallerReaderContext context ) throws IOException,
            ClassNotFoundException {
        ObjectInputStream stream = context.stream;
        Map<Integer, BaseNode> sinks = context.sinks;

        LeftTupleSink sink = parentLeftTuple.getLeftTupleSink();

        switch (sink.getType()) {
            case NodeTypeEnums.JoinNode: {
                BetaMemory memory = (BetaMemory) context.wm.getNodeMemory( (BetaNode) sink );
                readBehaviors( (BetaNode) sink,
                               memory,
                               context );
                addToLeftMemory( parentLeftTuple,
                                 memory );

                while (stream.readShort() == PersisterEnums.RIGHT_TUPLE) {
                    int childSinkId = stream.readInt();
                    LeftTupleSink childSink = (LeftTupleSink) sinks.get( childSinkId );
                    int factHandleId = stream.readInt();
                    RightTupleKey key = new RightTupleKey( factHandleId,
                                                           sink );
                    RightTuple rightTuple = context.rightTuples.get( key );
                    LeftTuple childLeftTuple = childSink.createLeftTuple( parentLeftTuple,
                                                                          rightTuple,
                                                                          null,
                                                                          null,
                                                                          childSink,
                                                                          true );
                    readLeftTuple( childLeftTuple,
                                   context );
                }
                break;

            }
            case NodeTypeEnums.EvalConditionNode: {
                while (stream.readShort() == PersisterEnums.LEFT_TUPLE) {
                    LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                    LeftTuple childLeftTuple = childSink.createLeftTuple( parentLeftTuple,
                                                                          childSink,
                                                                          true );
                    readLeftTuple( childLeftTuple,
                                   context );
                }
                break;
            }
            case NodeTypeEnums.NotNode:
            case NodeTypeEnums.ForallNotNode: {
                BetaMemory memory = (BetaMemory) context.wm.getNodeMemory( (BetaNode) sink );
                readBehaviors( (BetaNode) sink,
                               memory,
                               context );
                int type = stream.readShort();
                if (type == PersisterEnums.LEFT_TUPLE_NOT_BLOCKED) {
                    addToLeftMemory( parentLeftTuple,
                                     memory );

                    while (stream.readShort() == PersisterEnums.LEFT_TUPLE) {
                        LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                        LeftTuple childLeftTuple = childSink.createLeftTuple( parentLeftTuple,
                                                                              childSink,
                                                                              true );
                        readLeftTuple( childLeftTuple,
                                       context );
                    }

                } else {
                    int factHandleId = stream.readInt();
                    RightTupleKey key = new RightTupleKey( factHandleId,
                                                           sink );
                    RightTuple rightTuple = context.rightTuples.get( key );

                    parentLeftTuple.setBlocker( rightTuple );
                    rightTuple.addBlocked( parentLeftTuple );
                }
                break;
            }
            case NodeTypeEnums.ExistsNode: {
                BetaMemory memory = (BetaMemory) context.wm.getNodeMemory( (BetaNode) sink );
                readBehaviors( (BetaNode) sink,
                               memory,
                               context );
                int type = stream.readShort();
                if (type == PersisterEnums.LEFT_TUPLE_NOT_BLOCKED) {
                    addToLeftMemory( parentLeftTuple,
                                     memory );
                } else {
                    int factHandleId = stream.readInt();
                    RightTupleKey key = new RightTupleKey( factHandleId,
                                                           sink );
                    RightTuple rightTuple = context.rightTuples.get( key );

                    parentLeftTuple.setBlocker( rightTuple );
                    rightTuple.addBlocked( parentLeftTuple );

                    while (stream.readShort() == PersisterEnums.LEFT_TUPLE) {
                        LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                        LeftTuple childLeftTuple = childSink.createLeftTuple( parentLeftTuple,
                                                                              childSink,
                                                                              true );
                        readLeftTuple( childLeftTuple,
                                       context );
                    }
                }
                break;
            }
            case NodeTypeEnums.AccumulateNode: {
                // accumulate nodes generate new facts on-demand and need special procedures when de-serializing from persistent storage
                AccumulateMemory memory = (AccumulateMemory) context.wm.getNodeMemory( (BetaNode) sink );
                memory.betaMemory.getLeftTupleMemory().add( parentLeftTuple );

                readBehaviors( (BetaNode) sink,
                               memory.betaMemory,
                               context );

                AccumulateContext accctx = new AccumulateContext();
                parentLeftTuple.setObject( accctx );

                // first we de-serialize the generated fact handle
                InternalFactHandle handle = readFactHandle( context );
                accctx.result = new RightTuple( handle,
                                                (RightTupleSink) sink );

                // then we de-serialize the associated accumulation context
                accctx.context = (Serializable[]) stream.readObject();
                // then we de-serialize the boolean propagated flag
                accctx.propagated = stream.readBoolean();

                // then we de-serialize all the propagated tuples
                short head = -1;
                while (( head = stream.readShort() ) != PersisterEnums.END) {
                    switch (head) {
                        case PersisterEnums.RIGHT_TUPLE: {
                            int factHandleId = stream.readInt();
                            RightTupleKey key = new RightTupleKey( factHandleId,
                                                                   sink );
                            RightTuple rightTuple = context.rightTuples.get( key );
                            // just wiring up the match record
                            sink.createLeftTuple( parentLeftTuple,
                                                  rightTuple,
                                                  null,
                                                  null,
                                                  sink,
                                                  true );
                            break;
                        }
                        case PersisterEnums.LEFT_TUPLE: {
                            int sinkId = stream.readInt();
                            LeftTupleSink childSink = (LeftTupleSink) sinks.get( sinkId );
                            LeftTuple childLeftTuple = new LeftTupleImpl( parentLeftTuple,
                                                                          accctx.result,
                                                                          childSink,
                                                                          true );
                            readLeftTuple( childLeftTuple,
                                           context );
                            break;
                        }
                        default: {
                            throw new RuntimeDroolsException(
                                                              "Marshalling error. This is a bug. Please contact the development team." );
                        }
                    }
                }
                break;
            }
            case NodeTypeEnums.RightInputAdaterNode: {
                // RIANs generate new fact handles on-demand to wrap tuples and need special procedures when de-serializing from persistent storage
                ObjectHashMap memory = (ObjectHashMap) context.wm.getNodeMemory( (NodeMemory) sink );
                // create fact handle
                int id = stream.readInt();
                long recency = stream.readLong();
                InternalFactHandle handle = new DefaultFactHandle(
                                                                   id,
                                                                   parentLeftTuple,
                                                                   recency,
                                                                   context.wm.getEntryPoints().get( EntryPoint.DEFAULT.getEntryPointId() ) );
                memory.put( parentLeftTuple,
                            handle );

                readRightTuples( handle,
                                 context );

                stream.readShort(); // Persistence.END
                break;
            }
            case NodeTypeEnums.FromNode: {
                //              context.out.println( "FromNode" );
                // FNs generate new fact handles on-demand to wrap objects and need special procedures when serializing to persistent storage
                FromMemory memory = (FromMemory) context.wm.getNodeMemory( (NodeMemory) sink );

                memory.betaMemory.getLeftTupleMemory().add( parentLeftTuple );
                Map<Object, RightTuple> matches = new LinkedHashMap<Object, RightTuple>();
                parentLeftTuple.setObject( matches );

                while (stream.readShort() == PersisterEnums.FACT_HANDLE) {
                    // we de-serialize the generated fact handle ID
                    InternalFactHandle handle = readFactHandle( context );
                    context.handles.put( handle.getId(),
                                         handle );
                    readRightTuples( handle,
                                     context );
                    matches.put( handle.getObject(),
                                 handle.getFirstRightTuple() );
                }
                while (stream.readShort() == PersisterEnums.RIGHT_TUPLE) {
                    LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                    int factHandleId = stream.readInt();
                    RightTupleKey key = new RightTupleKey( factHandleId,
                                                           null ); // created tuples in from node always use null sink
                    RightTuple rightTuple = context.rightTuples.get( key );
                    LeftTuple childLeftTuple = new LeftTupleImpl( parentLeftTuple,
                                                                  rightTuple,
                                                                  childSink,
                                                                  true );
                    readLeftTuple( childLeftTuple,
                                   context );
                }
                //                context.out.println( "FromNode   ---   END" );
                break;
            }
            case NodeTypeEnums.UnificationNode: {
                boolean isOpen = context.readBoolean();

                if (isOpen) {
                    QueryElementNode node = (QueryElementNode) sink;
                    InternalFactHandle handle = readFactHandle( context );
                    context.handles.put( handle.getId(),
                                         handle );
                    node.createDroolsQuery( parentLeftTuple,
                                            handle,
                                            context.wm );
                    readLeftTuples( context );
                } else {
                    while (stream.readShort() == PersisterEnums.LEFT_TUPLE) {
                        LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                        // we de-serialize the generated fact handle ID
                        InternalFactHandle handle = readFactHandle( context );
                        context.handles.put( handle.getId(),
                                             handle );
                        RightTuple rightTuple = new RightTuple( handle );
                        // @TODO check if open query
                        LeftTuple childLeftTuple = new LeftTupleImpl( parentLeftTuple,
                                                                      rightTuple,
                                                                      childSink,
                                                                      true );
                        readLeftTuple( childLeftTuple,
                                       context );
                    }
                }
                break;
            }
            case NodeTypeEnums.RuleTerminalNode: {
                int pos = context.terminalTupleMap.size();
                context.terminalTupleMap.put( pos,
                                              parentLeftTuple );
                break;
            }
            case NodeTypeEnums.QueryTerminalNode: {
                boolean unificationNode = context.readBoolean();
                if (unificationNode) {
                    // we de-serialize the generated fact handle ID
                    InternalFactHandle handle = readFactHandle( context );
                    context.handles.put( handle.getId(),
                                         handle );
                    RightTuple rightTuple = new RightTuple( handle );
                    parentLeftTuple.setObject( rightTuple );

                    LeftTuple entry = parentLeftTuple;

                    // find the DroolsQuery object
                    while (entry.getParent() != null) {
                        entry = entry.getParent();
                    }
                    DroolsQuery query = (DroolsQuery) entry.getLastHandle().getObject();
                    LeftTuple leftTuple = ( (UnificationNodeViewChangedEventListener) query.getQueryResultCollector() ).getLeftTuple();

                    while (stream.readShort() == PersisterEnums.LEFT_TUPLE) {
                        LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                        // @TODO check if open query!!!
                        LeftTuple childLeftTuple = childSink.createLeftTuple( leftTuple,
                                                                              rightTuple,
                                                                              childSink );
                        readLeftTuple( childLeftTuple,
                                       context );
                    }
                }
                break;
            }
        }
    }

    public static void readBehaviors( BetaNode betaNode,
            BetaMemory betaMemory,
            MarshallerReaderContext inCtx ) throws IOException {
        short token = -1;
        while (( token = inCtx.readShort() ) != PersisterEnums.END) {
            int i = inCtx.readInt();
            Object object = ( (Object[]) betaMemory.getBehaviorContext() )[i];
            switch (token) {
                case PersisterEnums.SLIDING_TIME_WIN: {
                    readSlidingTimeWindowBehaviour( betaNode,
                                                    betaMemory,
                                                    (SlidingTimeWindow) betaNode.getBehaviors()[i],
                                                    (SlidingTimeWindowContext) object,
                                                    inCtx );
                    break;
                }
                case PersisterEnums.SLIDING_LENGTH_WIN: {
                    readSlidingLengthWindowBehaviour( betaNode,
                                                      betaMemory,
                                                      (SlidingLengthWindow) betaNode.getBehaviors()[i],
                                                      (SlidingLengthWindowContext) object,
                                                      inCtx );
                    break;
                }
            }

        }
    }

    public static void readSlidingTimeWindowBehaviour( BetaNode betaNode,
            BetaMemory betaMemory,
            SlidingTimeWindow stw,
            SlidingTimeWindowContext stwCtx,
            MarshallerReaderContext inCtx ) throws IOException {

        if (inCtx.readBoolean()) {
            int sinkId = inCtx.readInt();
            int factId = inCtx.readInt();

            RightTupleSink sink = (RightTupleSink) inCtx.sinks.get( sinkId );
            RightTupleKey key = new RightTupleKey( factId,
                                                   sink );
            RightTuple rightTuple = inCtx.rightTuples.get( key );

            stwCtx.expiringTuple = rightTuple;
        }

        if (inCtx.readBoolean()) {
            int size = inCtx.readInt();
            for (int i = 0; i < size; i++) {
                int sinkId = inCtx.readInt();
                int factId = inCtx.readInt();

                RightTupleSink sink = (RightTupleSink) inCtx.sinks.get( sinkId );
                RightTupleKey key = new RightTupleKey( factId,
                                                       sink );
                RightTuple rightTuple = inCtx.rightTuples.get( key );

                stwCtx.queue.add( rightTuple );
            }
        }
    }

    public static void readSlidingLengthWindowBehaviour( BetaNode betaNode,
            BetaMemory betaMemory,
            SlidingLengthWindow slw,
            SlidingLengthWindowContext slwCtx,
            MarshallerReaderContext inCtx ) throws IOException {
        int pos = inCtx.readInt();
        int length = inCtx.readInt();

        slwCtx.pos = pos;
        slwCtx.rightTuples = new RightTuple[length];
        for (int i = 0; i < length; i++) {
            int factId = inCtx.readInt();

            if (factId >= 0) {
                int sinkId = inCtx.readInt();

                RightTupleSink sink = (RightTupleSink) inCtx.sinks.get( sinkId );
                RightTupleKey key = new RightTupleKey( factId,
                                                       sink );
                RightTuple rightTuple = inCtx.rightTuples.get( key );

                slwCtx.rightTuples[i] = rightTuple;
            }

        }
    }

    private static void addToLeftMemory( LeftTuple parentLeftTuple,
            BetaMemory memory ) {
        memory.getLeftTupleMemory().add( parentLeftTuple );
        memory.linkRight();
    }

    public static void readActivations( MarshallerReaderContext context ) throws IOException {
        ObjectInputStream stream = context.stream;

        while (stream.readShort() == PersisterEnums.ACTIVATION) {
            readActivation( context );
        }
    }

    public static Activation readActivation( MarshallerReaderContext context ) throws IOException {
        ObjectInputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;
        InternalWorkingMemory wm = context.wm;

        long activationNumber = stream.readLong();

        int pos = stream.readInt();
        LeftTuple leftTuple = context.terminalTupleMap.get( pos );

        int salience = stream.readInt();

        String pkgName = stream.readUTF();
        String ruleName = stream.readUTF();
        Package pkg = ruleBase.getPackage( pkgName );
        Rule rule = pkg.getRule( ruleName );

        RuleTerminalNode ruleTerminalNode = (RuleTerminalNode) leftTuple.getLeftTupleSink();

        PropagationContext pc = context.propagationContexts.get( stream.readLong() );

        AgendaItem activation;

        boolean scheduled = false;
        if (rule.getTimer() != null) {
            activation = new ScheduledAgendaItem( activationNumber,
                                                  leftTuple,
                                                  (InternalAgenda) wm.getAgenda(),
                                                  pc,
                                                  ruleTerminalNode );
            scheduled = true;
        } else {
            activation = new AgendaItem( activationNumber,
                                         leftTuple,
                                         salience,
                                         pc,
                                         ruleTerminalNode );
        }
        leftTuple.setObject( activation );

        if (stream.readBoolean()) {
            String activationGroupName = stream.readUTF();
            ( (DefaultAgenda) wm.getAgenda() ).getActivationGroup( activationGroupName ).addActivation( activation );
        }

        boolean activated = stream.readBoolean();
        activation.setActivated( activated );

        if (stream.readBoolean()) {
            InternalFactHandle handle = context.handles.get( stream.readInt() );
            activation.setFactHandle( handle );
            handle.setObject( activation );
        }

        InternalAgendaGroup agendaGroup;
        if (rule.getAgendaGroup() == null || rule.getAgendaGroup().equals( "" ) ||
            rule.getAgendaGroup().equals( AgendaGroup.MAIN )) {
            // Is the Rule AgendaGroup undefined? If it is use MAIN,
            // which is added to the Agenda by default
            agendaGroup = (InternalAgendaGroup) ( (DefaultAgenda) wm.getAgenda() ).getAgendaGroup( AgendaGroup.MAIN );
        } else {
            // AgendaGroup is defined, so try and get the AgendaGroup
            // from the Agenda
            agendaGroup = (InternalAgendaGroup) ( (DefaultAgenda) wm.getAgenda() ).getAgendaGroup( rule.getAgendaGroup() );
        }

        activation.setAgendaGroup( agendaGroup );

        if (!scheduled && activated) {
            if (rule.getRuleFlowGroup() == null) {
                agendaGroup.add( activation );
            } else {
                InternalRuleFlowGroup rfg = (InternalRuleFlowGroup) ( (DefaultAgenda) wm.getAgenda() ).getRuleFlowGroup( rule.getRuleFlowGroup() );
                rfg.addActivation( activation );
            }
        }

        TruthMaintenanceSystem tms = context.wm.getTruthMaintenanceSystem();
        while (stream.readShort() == PersisterEnums.LOGICAL_DEPENDENCY) {
            int factHandleId = stream.readInt();
            InternalFactHandle handle = (InternalFactHandle) context.handles.get( factHandleId );
            tms.addLogicalDependency( handle,
                                      activation,
                                      pc,
                                      rule );
        }

        return activation;
    }

    public static void readPropagationContexts( MarshallerReaderContext context ) throws IOException {
        ObjectInputStream stream = context.stream;

        while (stream.readShort() == PersisterEnums.PROPAGATION_CONTEXT) {
            readPropagationContext( context );
        }

    }

    public static void readPropagationContext( MarshallerReaderContext context ) throws IOException {
        ObjectInputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;

        int type = stream.readInt();

        Rule rule = null;
        if (stream.readBoolean()) {
            String pkgName = stream.readUTF();
            String ruleName = stream.readUTF();
            Package pkg = ruleBase.getPackage( pkgName );
            rule = pkg.getRule( ruleName );
        }

        LeftTuple leftTuple = null;
        if (stream.readBoolean()) {
            int tuplePos = stream.readInt();
            leftTuple = context.terminalTupleMap.get( tuplePos );
        }

        long propagationNumber = stream.readLong();

        int factHandleId = stream.readInt();
        InternalFactHandle factHandle = context.handles.get( factHandleId );

        int activeActivations = stream.readInt();
        int dormantActivations = stream.readInt();
        String entryPointId = stream.readUTF();

        EntryPoint entryPoint = context.entryPoints.get( entryPointId );
        if (entryPoint == null) {
            entryPoint = new EntryPoint( entryPointId );
            context.entryPoints.put( entryPointId,
                                     entryPoint );
        }

        PropagationContext pc = new PropagationContextImpl( propagationNumber,
                                                            type,
                                                            rule,
                                                            leftTuple,
                                                            factHandle,
                                                            activeActivations,
                                                            dormantActivations,
                                                            entryPoint );
        context.propagationContexts.put( propagationNumber,
                                         pc );
    }

    public static WorkItem readWorkItem( MarshallerReaderContext context ) throws IOException {
        ObjectInputStream stream = context.stream;

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setId( stream.readLong() );
        workItem.setProcessInstanceId( stream.readLong() );
        workItem.setName( stream.readUTF() );
        workItem.setState( stream.readInt() );

        //WorkItem Paramaters
        int nbVariables = stream.readInt();
        if (nbVariables > 0) {

            for (int i = 0; i < nbVariables; i++) {
                String name = stream.readUTF();
                try {
                    int index = stream.readInt();
                    ObjectMarshallingStrategy strategy = null;
                    // Old way of retrieving strategy objects
                    if (index >= 0) {
                        strategy = context.resolverStrategyFactory.getStrategy( index );
                        if (strategy == null) {
                            throw new IllegalStateException( "No strategy of with index " + index + " available." );
                        }
                    }
                    // New way 
                    else if (index == -2) {
                        String strategyClassName = stream.readUTF();
                        strategy = context.resolverStrategyFactory.getStrategyObject( strategyClassName );
                        if (strategy == null) {
                            throw new IllegalStateException( "No strategy of type " + strategyClassName + " available." );
                        }
                    }

                    Object value = strategy.read( stream );
                    workItem.setParameter( name,
                                           value );
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException(
                                                        "Could not reload variable " + name );
                }
            }
        }

        return workItem;
    }

    public static void readTimer( MarshallerReaderContext inCtx ) throws IOException, ClassNotFoundException {
        short timerType = inCtx.readShort();
        TimersInputMarshaller reader = inCtx.readersByInt.get( timerType );
        reader.read( inCtx );
    }

    public static Trigger readTrigger( MarshallerReaderContext inCtx ) throws IOException, ClassNotFoundException {
        short triggerInt = inCtx.readShort();

        switch (triggerInt) {
            case PersisterEnums.CRON_TRIGGER: {
                long startTime = inCtx.readLong();

                CronTrigger trigger = new CronTrigger();
                trigger.setStartTime( new Date( startTime ) );
                if (inCtx.readBoolean()) {
                    long endTime = inCtx.readLong();
                    trigger.setEndTime( new Date( endTime ) );
                }

                int repeatLimit = inCtx.readInt();
                trigger.setRepeatLimit( repeatLimit );

                int repeatCount = inCtx.readInt();
                trigger.setRepeatCount( repeatCount );

                String expr = inCtx.readUTF();
                trigger.setCronExpression( expr );
                if (inCtx.readBoolean()) {
                    long nextFireTime = inCtx.readLong();
                    trigger.setNextFireTime( new Date( nextFireTime ) );
                }

                String[] calendarNames = (String[]) inCtx.readObject();
                trigger.setCalendarNames( calendarNames );
                return trigger;
            }
            case PersisterEnums.INT_TRIGGER: {
                IntervalTrigger trigger = new IntervalTrigger();
                long startTime = inCtx.readLong();
                trigger.setStartTime( new Date( startTime ) );
                if (inCtx.readBoolean()) {
                    long endTime = inCtx.readLong();
                    trigger.setEndTime( new Date( endTime ) );
                }
                int repeatLimit = inCtx.readInt();
                trigger.setRepeatLimit( repeatLimit );
                int repeatCount = inCtx.readInt();
                trigger.setRepeatCount( repeatCount );
                if (inCtx.readBoolean()) {
                    long nextFireTime = inCtx.readLong();
                    trigger.setNextFireTime( new Date( nextFireTime ) );
                }
                long period = inCtx.readLong();
                trigger.setPeriod( period );
                String[] calendarNames = (String[]) inCtx.readObject();
                trigger.setCalendarNames( calendarNames );
                return trigger;
            }
            case PersisterEnums.POINT_IN_TIME_TRIGGER: {
                long startTime = inCtx.readLong();

                PointInTimeTrigger trigger = new PointInTimeTrigger( startTime, null, null );
                return trigger;
            }
        }
        throw new RuntimeException( "Unable to persist Trigger for type: " + triggerInt );

    }

}
