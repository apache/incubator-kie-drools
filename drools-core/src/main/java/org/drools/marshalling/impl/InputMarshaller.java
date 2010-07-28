/**
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

import org.drools.RuntimeDroolsException;
import org.drools.SessionConfiguration;
import org.drools.base.ClassObjectType;
import org.drools.common.AgendaItem;
import org.drools.common.BaseNode;
import org.drools.common.BinaryHeapQueueAgendaGroup;
import org.drools.common.DefaultAgenda;
import org.drools.common.DefaultFactHandle;
import org.drools.common.DisconnectedWorkingMemoryEntryPoint;
import org.drools.common.EqualityKey;
import org.drools.common.InternalAgendaGroup;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalRuleFlowGroup;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.RuleFlowGroupImpl;
import org.drools.common.TruthMaintenanceSystem;
import org.drools.concurrent.ExecutorService;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.ObjectHashSet;
import org.drools.impl.EnvironmentFactory;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.process.instance.timer.TimerInstance;
import org.drools.process.instance.timer.TimerManager;
import org.drools.reteoo.BetaMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.InitialFactImpl;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.NodeTypeEnums;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RightTupleSink;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.AccumulateNode.AccumulateContext;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.FromNode.FromMemory;
import org.drools.rule.EntryPoint;
import org.drools.rule.GroupElement;
import org.drools.rule.Package;
import org.drools.rule.Rule;
import org.drools.runtime.Environment;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.FactHandleFactory;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleFlowGroup;

public class InputMarshaller {
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
        boolean multithread = context.readBoolean();
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

        context.wm = session;
        
        context.handles.put( context.wm.getInitialFactHandle().getId(),  context.wm.getInitialFactHandle() );

        readFactHandles( context );       

        readActionQueue( context );

        if ( context.readBoolean() ) {
            readTruthMaintenanceSystem( context );
        }

        if ( context.marshalProcessInstances ) {
            readProcessInstances( context );
        }

        if ( context.marshalWorkItems ) {
            readWorkItems( context );
        }

        readTimers( context );
        
        if( multithread ) {
            session.startPartitionManagers();
        }

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
        return readSession( context, id, executor, EnvironmentFactory.newEnvironment(), new SessionConfiguration() );
    }
    
    public static ReteooStatefulSession readSession(MarshallerReaderContext context,
                                                    int id,
                                                    ExecutorService executor,
                                                    Environment environment,
                                                    SessionConfiguration config) throws IOException,
                                                                             ClassNotFoundException {

        boolean multithread = context.readBoolean();
        
        FactHandleFactory handleFactory = context.ruleBase.newFactHandleFactory( context.readInt(),
                                                                                 context.readLong() );

        InternalFactHandle initialFactHandle = new DefaultFactHandle( context.readInt(), //id
                                                                      InitialFactImpl.getInstance(),
                                                                      context.readLong(),
                                                                      null);
        
        context.handles.put( initialFactHandle.getId(),
                             initialFactHandle );

        long propagationCounter = context.readLong();

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
        initialFactHandle.setEntryPoint( session.getEntryPoints().get( EntryPoint.DEFAULT.getEntryPointId() ) );

        // RuleFlowGroups need to reference the session
        for ( RuleFlowGroup group : agenda.getRuleFlowGroupsMap().values() ) {
            ((RuleFlowGroupImpl) group).setWorkingMemory( session );
        }
        context.wm = session;

        readFactHandles( context );

        readActionQueue( context );

        if ( context.readBoolean() ) {
            readTruthMaintenanceSystem( context );
        }

        if ( context.marshalProcessInstances ) {
            readProcessInstances( context );
        }

        if ( context.marshalWorkItems ) {
            readWorkItems( context );
        }

        readTimers( context );

        if( multithread ) {
            session.startPartitionManagers();
        }

        return session;
    }    

    public static void readAgenda(MarshallerReaderContext context,
                                  DefaultAgenda agenda) throws IOException {
        ObjectInputStream stream = context.stream;
        while ( stream.readShort() == PersisterEnums.AGENDA_GROUP ) {
            BinaryHeapQueueAgendaGroup group = new BinaryHeapQueueAgendaGroup( stream.readUTF(),
                                                                               context.ruleBase );
            group.setActive( stream.readBoolean() );
            agenda.getAgendaGroupsMap().put( group.getName(),
                                             group );
        }

        while ( stream.readShort() == PersisterEnums.AGENDA_GROUP ) {
            String agendaGroupName = stream.readUTF();
            agenda.getStackList().add( agenda.getAgendaGroup( agendaGroupName ) );
        }

        while ( stream.readShort() == PersisterEnums.RULE_FLOW_GROUP ) {
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
            	rfg.addNodeInstance(processInstanceId, nodeInstanceId);
            }
        }

    }

    public static void readActionQueue(MarshallerReaderContext context) throws IOException, ClassNotFoundException {
        ReteooWorkingMemory wm = (ReteooWorkingMemory) context.wm;
        Queue actionQueue = wm.getActionQueue();
        while ( context.readShort() == PersisterEnums.WORKING_MEMORY_ACTION ) {
            actionQueue.offer( PersisterHelper.readWorkingMemoryAction( context ) );
        }
    }

    public static void readTruthMaintenanceSystem(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;

        TruthMaintenanceSystem tms = context.wm.getTruthMaintenanceSystem();
        while ( stream.readShort() == PersisterEnums.EQUALITY_KEY ) {
            int status = stream.readInt();
            int factHandleId = stream.readInt();
            InternalFactHandle handle = (InternalFactHandle) context.handles.get( factHandleId );
            EqualityKey key = new EqualityKey( handle,
                                               status );
            handle.setEqualityKey( key );
            while ( stream.readShort() == PersisterEnums.FACT_HANDLE ) {
                factHandleId = stream.readInt();
                handle = (InternalFactHandle) context.handles.get( factHandleId );
                key.addFactHandle( handle );
                handle.setEqualityKey( key );
            }
            tms.put( key );
        }
    }

    public static void readFactHandles(MarshallerReaderContext context) throws IOException,
                                                                       ClassNotFoundException {
        ObjectInputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;
        ObjectMarshallingStrategyStore resolverStrategyFactory = context.resolverStrategyFactory;
        InternalWorkingMemory wm = context.wm;

        if ( stream.readBoolean() ) {
            InternalFactHandle initialFactHandle = wm.getInitialFactHandle();
            int sinkId = stream.readInt();
            ObjectTypeNode initialFactNode = (ObjectTypeNode) context.sinks.get( sinkId );
            ObjectHashSet initialFactMemory = (ObjectHashSet) context.wm.getNodeMemory( initialFactNode );

            initialFactMemory.add( initialFactHandle );
            readRightTuples( initialFactHandle,
                             context );
        }

        int size = stream.readInt();

        // load the handles
        InternalFactHandle[] handles = new InternalFactHandle[size];
        for ( int i = 0; i < size; i++ ) {
            InternalFactHandle handle = readFactHandle( context );

            context.handles.put( handle.getId(),
                                 handle );
            handles[i] = handle;

            context.wm.getObjectStore().addHandle( handle,
                                                   handle.getObject() );

            readRightTuples( handle,
                             context );
        }

        EntryPointNode node = ruleBase.getRete().getEntryPointNode( EntryPoint.DEFAULT );
        Map<ObjectType, ObjectTypeNode> objectTypeNodes = node.getObjectTypeNodes();

        // add handles to object type nodes
        for ( InternalFactHandle handle : handles ) {
            Object object = handle.getObject();
            ClassObjectType objectType = new ClassObjectType( object.getClass() );
            ObjectTypeNode objectTypeNode = objectTypeNodes.get( objectType );
            if (objectTypeNode != null) {
	            ObjectHashSet set = (ObjectHashSet) context.wm.getNodeMemory( objectTypeNode );
	            set.add( handle,
	                     false );
            }
        }

        InternalFactHandle handle = wm.getInitialFactHandle();
        while ( stream.readShort() == PersisterEnums.LEFT_TUPLE ) {
            LeftTupleSink sink = (LeftTupleSink) context.sinks.get( stream.readInt() );
            LeftTuple leftTuple = new LeftTuple( handle,
                                                 sink,
                                                 true );
            readLeftTuple( leftTuple,
                           context );
        }

        readLeftTuples( context );
 
        readPropagationContexts( context );

        readActivations( context );
    }

    public static InternalFactHandle readFactHandle(MarshallerReaderContext context) throws IOException,
                                                                                    ClassNotFoundException {
        int id = context.stream.readInt();
        long recency = context.stream.readLong();

        int strategyIndex = context.stream.readInt();
        ObjectMarshallingStrategy strategy = context.resolverStrategyFactory.getStrategy( strategyIndex );
        Object object = strategy.read( context.stream );
        
        WorkingMemoryEntryPoint entryPoint = null;
        if(context.readBoolean()){
            String entryPointId = context.readUTF();
            if(entryPointId != null && !entryPointId.equals("")){
                entryPoint = context.wm.getEntryPoints().get(entryPointId);
            } 
        }        

        InternalFactHandle handle = new DefaultFactHandle( id,
                                                           object,
                                                           recency,
                                                           entryPoint );

        return handle;
    }

    public static void readRightTuples(InternalFactHandle factHandle,
                                       MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;
        while ( stream.readShort() == PersisterEnums.RIGHT_TUPLE ) {
            readRightTuple( context,
                            factHandle );
        }
    }

    public static void readRightTuple(MarshallerReaderContext context,
                                      InternalFactHandle factHandle) throws IOException {
        ObjectInputStream stream = context.stream;

        int sinkId = stream.readInt();
        RightTupleSink sink = (sinkId >= 0) ? (RightTupleSink) context.sinks.get( sinkId ) : null;

        RightTuple rightTuple = new RightTuple( factHandle,
                                                sink );
        context.rightTuples.put( new RightTupleKey( factHandle.getId(),
                                                    sink ),
                                 rightTuple );

        if( sink != null ) {
            BetaMemory memory = null;
            switch ( sink.getType() ) {
                case NodeTypeEnums.AccumulateNode : {
                    memory = ((AccumulateMemory) context.wm.getNodeMemory( (BetaNode) sink )).betaMemory;
                    break;
                }
                default : {
                    memory = (BetaMemory) context.wm.getNodeMemory( (BetaNode) sink );
                    break;
                }
            }
            memory.getRightTupleMemory().add( rightTuple );
        }
    }

    public static void readLeftTuples(MarshallerReaderContext context) throws IOException,
                                                                      ClassNotFoundException {
        ObjectInputStream stream = context.stream;

        while ( stream.readShort() == PersisterEnums.LEFT_TUPLE ) {
            int nodeId = stream.readInt();
            LeftTupleSink sink = (LeftTupleSink) context.sinks.get( nodeId );
            int factHandleId = stream.readInt();
            LeftTuple leftTuple = new LeftTuple( context.handles.get( factHandleId ),
                                                 sink,
                                                 true );
            readLeftTuple( leftTuple,
                           context );
        }
    }

    public static void readLeftTuple(LeftTuple parentLeftTuple,
                                     MarshallerReaderContext context) throws IOException,
                                                                     ClassNotFoundException {
        ObjectInputStream stream = context.stream;
        InternalWorkingMemory wm = context.wm;
        Map<Integer, BaseNode> sinks = context.sinks;

        LeftTupleSink sink = parentLeftTuple.getLeftTupleSink();

        switch ( sink.getType() ) {
            case NodeTypeEnums.JoinNode : {
                BetaMemory memory = (BetaMemory) context.wm.getNodeMemory( (BetaNode) sink );
                memory.getLeftTupleMemory().add( parentLeftTuple );

                while ( stream.readShort() == PersisterEnums.RIGHT_TUPLE ) {
                    LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                    int factHandleId = stream.readInt();
                    RightTupleKey key = new RightTupleKey( factHandleId,
                                                           sink );
                    RightTuple rightTuple = context.rightTuples.get( key );
                    LeftTuple childLeftTuple = new LeftTuple( parentLeftTuple,
                                                              rightTuple,
                                                              childSink,
                                                              true );
                    readLeftTuple( childLeftTuple,
                                   context );
                }
                break;

            }
            case NodeTypeEnums.EvalConditionNode : {
                while ( stream.readShort() == PersisterEnums.LEFT_TUPLE ) {
                    LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                    LeftTuple childLeftTuple = new LeftTuple( parentLeftTuple,
                                                              childSink,
                                                              true );
                    readLeftTuple( childLeftTuple,
                                   context );
                }
                break;
            }
            case NodeTypeEnums.NotNode : 
            case NodeTypeEnums.ForallNotNode : {
                BetaMemory memory = (BetaMemory) context.wm.getNodeMemory( (BetaNode) sink );
                int type = stream.readShort();
                if ( type == PersisterEnums.LEFT_TUPLE_NOT_BLOCKED ) {
                    memory.getLeftTupleMemory().add( parentLeftTuple );

                    while ( stream.readShort() == PersisterEnums.LEFT_TUPLE ) {
                        LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                        LeftTuple childLeftTuple = new LeftTuple( parentLeftTuple,
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
            case NodeTypeEnums.ExistsNode : {
                BetaMemory memory = (BetaMemory) context.wm.getNodeMemory( (BetaNode) sink );
                int type = stream.readShort();
                if ( type == PersisterEnums.LEFT_TUPLE_NOT_BLOCKED ) {
                    memory.getLeftTupleMemory().add( parentLeftTuple );
                } else {
                    int factHandleId = stream.readInt();
                    RightTupleKey key = new RightTupleKey( factHandleId,
                                                           sink );
                    RightTuple rightTuple = context.rightTuples.get( key );

                    parentLeftTuple.setBlocker( rightTuple );
                    rightTuple.addBlocked( parentLeftTuple );

                    while ( stream.readShort() == PersisterEnums.LEFT_TUPLE ) {
                        LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                        LeftTuple childLeftTuple = new LeftTuple( parentLeftTuple,
                                                                  childSink,
                                                                  true );
                        readLeftTuple( childLeftTuple,
                                       context );
                    }
                }
                break;
            }
            case NodeTypeEnums.AccumulateNode : {
                // accumulate nodes generate new facts on-demand and need special procedures when de-serializing from persistent storage
                AccumulateMemory memory = (AccumulateMemory) context.wm.getNodeMemory( (BetaNode) sink );
                memory.betaMemory.getLeftTupleMemory().add( parentLeftTuple );

                AccumulateContext accctx = new AccumulateContext();
                memory.betaMemory.getCreatedHandles().put( parentLeftTuple,
                                                           accctx,
                                                           false );
                // first we de-serialize the generated fact handle
                InternalFactHandle handle = readFactHandle( context );
                accctx.result = new RightTuple( handle,
                                                (RightTupleSink) sink );

                // then we de-serialize the associated accumulation context
                accctx.context = (Serializable) stream.readObject();
                // then we de-serialize the boolean propagated flag
                accctx.propagated = stream.readBoolean();

                // then we de-serialize all the propagated tuples
                short head = -1;
                while ( (head = stream.readShort()) != PersisterEnums.END ) {
                    switch ( head ) {
                        case PersisterEnums.RIGHT_TUPLE : {
                            int factHandleId = stream.readInt();
                            RightTupleKey key = new RightTupleKey( factHandleId,
                                                                   sink );
                            RightTuple rightTuple = context.rightTuples.get( key );
                            // just wiring up the match record
                            new LeftTuple( parentLeftTuple,
                                           rightTuple,
                                           sink,
                                           true );
                            break;
                        }
                        case PersisterEnums.LEFT_TUPLE : {
                            LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                            LeftTuple childLeftTuple = new LeftTuple( parentLeftTuple,
                                                                      accctx.result,
                                                                      childSink,
                                                                      true );
                            readLeftTuple( childLeftTuple,
                                           context );
                            break;
                        }
                        default : {
                            throw new RuntimeDroolsException( "Marshalling error. This is a bug. Please contact the development team." );
                        }
                    }
                }
                break;
            }
            case NodeTypeEnums.RightInputAdaterNode : {
                // RIANs generate new fact handles on-demand to wrap tuples and need special procedures when de-serializing from persistent storage
                ObjectHashMap memory = (ObjectHashMap) context.wm.getNodeMemory( (NodeMemory) sink );
                // create fact handle
                int id = stream.readInt();
                long recency = stream.readLong();
                InternalFactHandle handle = new DefaultFactHandle( id,
                                                                   parentLeftTuple,
                                                                   recency,
                                                                   context.wm.getEntryPoints().get( EntryPoint.DEFAULT.getEntryPointId() ) );
                memory.put( parentLeftTuple, handle );
                
                readRightTuples( handle, context );
                
                stream.readShort(); // Persistence.END
                break;
            }
            case NodeTypeEnums.FromNode: {
//              context.out.println( "FromNode" );
                // FNs generate new fact handles on-demand to wrap objects and need special procedures when serializing to persistent storage
                FromMemory memory = (FromMemory) context.wm.getNodeMemory( (NodeMemory) sink );
                
                memory.betaMemory.getLeftTupleMemory().add( parentLeftTuple );                
                Map<Object, RightTuple> matches =  new LinkedHashMap<Object, RightTuple>();
                memory.betaMemory.getCreatedHandles().put( parentLeftTuple, matches );
                
                while( stream.readShort() == PersisterEnums.FACT_HANDLE ) {
                    // we de-serialize the generated fact handle ID
                    InternalFactHandle handle = readFactHandle( context );
                    context.handles.put( handle.getId(),
                                         handle );
                    readRightTuples( handle, 
                                     context );
                    matches.put( handle.getObject(), handle.getFirstRightTuple() );
                }
                while( stream.readShort() == PersisterEnums.RIGHT_TUPLE ) {
                    LeftTupleSink childSink = (LeftTupleSink) sinks.get( stream.readInt() );
                    int factHandleId = stream.readInt();
                    RightTupleKey key = new RightTupleKey( factHandleId,
                                                           null ); // created tuples in from node always use null sink
                    RightTuple rightTuple = context.rightTuples.get( key );
                    LeftTuple childLeftTuple = new LeftTuple( parentLeftTuple,
                                                              rightTuple,
                                                              childSink,
                                                              true );
                    readLeftTuple( childLeftTuple,
                                   context );
                }
//                context.out.println( "FromNode   ---   END" );
                break;
            }
            case NodeTypeEnums.RuleTerminalNode : {
                int pos = context.terminalTupleMap.size();
                context.terminalTupleMap.put( pos,
                                              parentLeftTuple );
                break;
            }
        }
    }

    public static void readActivations(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;

        while ( stream.readShort() == PersisterEnums.ACTIVATION ) {
            readActivation( context );
        }
    }

    public static Activation readActivation(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;
        InternalWorkingMemory wm = context.wm;

        long activationNumber = stream.readLong();

        int pos = stream.readInt();
        LeftTuple leftTuple = context.terminalTupleMap.get( pos );

        int salience = stream.readInt();

        //PropagationContext context,
        String pkgName = stream.readUTF();
        String ruleName = stream.readUTF();
        Package pkg = ruleBase.getPackage( pkgName );
        Rule rule = pkg.getRule( ruleName );

        RuleTerminalNode ruleTerminalNode = (RuleTerminalNode) leftTuple.getLeftTupleSink();
        GroupElement subRule = ruleTerminalNode.getSubRule();

        PropagationContext pc = context.propagationContexts.get( stream.readLong() );

        AgendaItem activation = new AgendaItem( activationNumber,
                                                leftTuple,
                                                salience,
                                                pc,
                                                rule,
                                                subRule );

        leftTuple.setObject( activation );

        if ( stream.readBoolean() ) {
            String activationGroupName = stream.readUTF();
            ((DefaultAgenda) wm.getAgenda()).getActivationGroup( activationGroupName ).addActivation( activation );
        }

        boolean activated = stream.readBoolean();
        activation.setActivated( activated );

        InternalAgendaGroup agendaGroup;
        if ( rule.getAgendaGroup() == null || rule.getAgendaGroup().equals( "" ) || rule.getAgendaGroup().equals( AgendaGroup.MAIN ) ) {
            // Is the Rule AgendaGroup undefined? If it is use MAIN,
            // which is added to the Agenda by default
            agendaGroup = (InternalAgendaGroup) ((DefaultAgenda) wm.getAgenda()).getAgendaGroup( AgendaGroup.MAIN );
        } else {
            // AgendaGroup is defined, so try and get the AgendaGroup
            // from the Agenda
            agendaGroup = (InternalAgendaGroup) ((DefaultAgenda) wm.getAgenda()).getAgendaGroup( rule.getAgendaGroup() );
        }

        activation.setAgendaGroup( agendaGroup );

        if ( activated ) {
            if ( rule.getRuleFlowGroup() == null ) {
                agendaGroup.add( activation );
            } else {
                InternalRuleFlowGroup rfg = (InternalRuleFlowGroup) ((DefaultAgenda) wm.getAgenda()).getRuleFlowGroup( rule.getRuleFlowGroup() );
                rfg.addActivation( activation );
            }
        }

        TruthMaintenanceSystem tms = context.wm.getTruthMaintenanceSystem();
        while ( stream.readShort() == PersisterEnums.LOGICAL_DEPENDENCY ) {
            int factHandleId = stream.readInt();
            InternalFactHandle handle = (InternalFactHandle) context.handles.get( factHandleId );
            tms.addLogicalDependency( handle,
                                      activation,
                                      pc,
                                      rule );
        }

        return activation;
    }

    public static void readPropagationContexts(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;

        while ( stream.readShort() == PersisterEnums.PROPAGATION_CONTEXT ) {
            readPropagationContext( context );
        }

    }

    public static void readPropagationContext(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;

        int type = stream.readInt();

        Rule rule = null;
        if ( stream.readBoolean() ) {
            String pkgName = stream.readUTF();
            String ruleName = stream.readUTF();
            Package pkg = ruleBase.getPackage( pkgName );
            rule = pkg.getRule( ruleName );
        }

        LeftTuple leftTuple = null;
        if ( stream.readBoolean() ) {
            int tuplePos = stream.readInt();
            leftTuple = (LeftTuple) context.terminalTupleMap.get( tuplePos );
        }

        long propagationNumber = stream.readLong();

        int factHandleId = stream.readInt();
        InternalFactHandle factHandle = context.handles.get( factHandleId );

        int activeActivations = stream.readInt();
        int dormantActivations = stream.readInt();
        String entryPointId = stream.readUTF();

        EntryPoint entryPoint = context.entryPoints.get( entryPointId );
        if ( entryPoint == null ) {
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

    public static void readProcessInstances(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;
        while ( stream.readShort() == PersisterEnums.PROCESS_INSTANCE ) {
        	String processType = stream.readUTF();
        	ProcessMarshallerRegistry.INSTANCE.getMarshaller(processType).readProcessInstance(context);
        }
    }

    public static void readWorkItems(MarshallerReaderContext context) throws IOException {
        InternalWorkingMemory wm = context.wm;
        ObjectInputStream stream = context.stream;
        while ( stream.readShort() == PersisterEnums.WORK_ITEM ) {
            WorkItem workItem = readWorkItem( context );
            ((WorkItemManager) wm.getWorkItemManager()).internalAddWorkItem( workItem );
        }
    }

    public static WorkItem readWorkItem(MarshallerReaderContext context) throws IOException {
       return readWorkItem(context, true);
    }

    public static WorkItem readWorkItem(MarshallerReaderContext context, boolean includeVariables) throws IOException {
        ObjectInputStream stream = context.stream;

        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setId( stream.readLong() );
        workItem.setProcessInstanceId( stream.readLong() );
        workItem.setName( stream.readUTF() );
        workItem.setState( stream.readInt() );

        if(includeVariables){
        int nbParameters = stream.readInt();

        for ( int i = 0; i < nbParameters; i++ ) {
            String name = stream.readUTF();
            try {
                Object value = stream.readObject();
                workItem.setParameter( name,
                                       value );
            } catch ( ClassNotFoundException e ) {
                throw new IllegalArgumentException( "Could not reload parameter " + name );
            }
        }
        }

        return workItem;
    }

    public static void readTimers(MarshallerReaderContext context) throws IOException, ClassNotFoundException {
        InternalWorkingMemory wm = context.wm;
        ObjectInputStream stream = context.stream;

        TimerManager timerManager = wm.getTimerManager();
        timerManager.internalSetTimerId( stream.readLong() );
        
        // still need to think on how to fix this.
//        TimerService service = (TimerService) stream.readObject();
//        timerManager.setTimerService( service );

        while ( stream.readShort() == PersisterEnums.TIMER ) {
            TimerInstance timer = readTimer( context );
            timerManager.internalAddTimer( timer );
        }
    }

    public static TimerInstance readTimer(MarshallerReaderContext context) throws IOException {
        ObjectInputStream stream = context.stream;

        TimerInstance timer = new TimerInstance();
        timer.setId( stream.readLong() );
        timer.setTimerId( stream.readLong() );
        timer.setDelay( stream.readLong() );
        timer.setPeriod( stream.readLong() );
        timer.setProcessInstanceId( stream.readLong() );
        timer.setActivated( new Date( stream.readLong() ) );
        if ( stream.readBoolean() ) {
            timer.setLastTriggered( new Date( stream.readLong() ) );
        }
        return timer;
    }

}
