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
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.InitialFact;
import org.drools.base.ClassObjectType;
import org.drools.common.AgendaItem;
import org.drools.common.DefaultAgenda;
import org.drools.common.EqualityKey;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.InternalWorkingMemoryEntryPoint;
import org.drools.common.LogicalDependency;
import org.drools.common.NodeMemory;
import org.drools.common.ObjectStore;
import org.drools.common.RuleFlowGroupImpl;
import org.drools.common.WorkingMemoryAction;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.ObjectHashSet;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.process.instance.WorkItem;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.NodeTypeEnums;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.AccumulateNode.AccumulateContext;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.FromNode.FromMemory;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaGroup;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleFlowGroup;

public class OutputMarshaller {

    private static ProcessMarshaller processMarshaller = createProcessMarshaller();

    private static ProcessMarshaller createProcessMarshaller() {
        try {
            return ProcessMarshallerFactory.newProcessMarshaller();
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static void writeSession(MarshallerWriteContext context) throws IOException {
        ReteooWorkingMemory wm = (ReteooWorkingMemory) context.wm;
        
        final boolean multithread = wm.isPartitionManagersActive();
        // is multi-thread active?
        if( multithread ) {
            context.writeBoolean( true );
            wm.stopPartitionManagers();
        } else {
            context.writeBoolean( false );
        }

        context.writeInt( wm.getFactHandleFactory().getId() );
        context.writeLong( wm.getFactHandleFactory().getRecency() );
        ////context.out.println( "FactHandleFactory int:" + wm.getFactHandleFactory().getId() + " long:" + wm.getFactHandleFactory().getRecency() );

        InternalFactHandle handle = context.wm.getInitialFactHandle();
        context.writeInt( handle.getId() );
        context.writeLong( handle.getRecency() );
        //context.out.println( "InitialFact int:" + handle.getId() + " long:" + handle.getRecency() );

        context.writeLong( wm.getPropagationIdCounter() );
        //context.out.println( "PropagationCounter long:" + wm.getPropagationIdCounter() );

        writeAgenda( context );

        writeFactHandles( context );

        writeActionQueue( context );
        
        writeTruthMaintenanceSystem( context );

        if ( context.marshalProcessInstances && processMarshaller != null ) {
            processMarshaller.writeProcessInstances( context );
        }

        if ( context.marshalWorkItems  && processMarshaller != null ) {
            processMarshaller.writeWorkItems( context );
        }

        if ( processMarshaller != null ) {
            processMarshaller.writeProcessTimers( context );
        }
        
        if( multithread ) {
            wm.startPartitionManagers();
        }
    }

    public static void writeAgenda(MarshallerWriteContext context) throws IOException {
        InternalWorkingMemory wm = context.wm;
        DefaultAgenda agenda = (DefaultAgenda) wm.getAgenda();

        Map<String, ActivationGroup> activationGroups = agenda.getActivationGroupsMap();

        AgendaGroup[] agendaGroups = (AgendaGroup[]) agenda.getAgendaGroupsMap().values().toArray( new AgendaGroup[agenda.getAgendaGroupsMap().size()] );
        Arrays.sort( agendaGroups,
                     AgendaGroupSorter.instance );

        for ( AgendaGroup group : agendaGroups ) {
            context.writeShort( PersisterEnums.AGENDA_GROUP );
            context.writeUTF( group.getName() );
            context.writeBoolean( group.isActive() );
        }
        context.writeShort( PersisterEnums.END );

        LinkedList<AgendaGroup> focusStack = agenda.getStackList();
        for ( Iterator<AgendaGroup> it = focusStack.iterator(); it.hasNext(); ) {
            AgendaGroup group = it.next();
            context.writeShort( PersisterEnums.AGENDA_GROUP );
            context.writeUTF( group.getName() );
        }
        context.writeShort( PersisterEnums.END );

        RuleFlowGroupImpl[] ruleFlowGroups = (RuleFlowGroupImpl[]) agenda.getRuleFlowGroupsMap().values().toArray( new RuleFlowGroupImpl[agenda.getRuleFlowGroupsMap().size()] );
        Arrays.sort( ruleFlowGroups,
                     RuleFlowGroupSorter.instance );

        for ( RuleFlowGroupImpl group : ruleFlowGroups ) {
            context.writeShort( PersisterEnums.RULE_FLOW_GROUP );
            //group.write( context );
            context.writeUTF( group.getName() );
            context.writeBoolean( group.isActive() );
            context.writeBoolean( group.isAutoDeactivate() );
            Map<Long, String> nodeInstances = group.getNodeInstances();
            context.writeInt( nodeInstances.size() );
            for (Map.Entry<Long, String> entry: nodeInstances.entrySet()) {
                context.writeLong( entry.getKey() );
                context.writeUTF( entry.getValue() );
            }
        }
        context.writeShort( PersisterEnums.END );
    }

    public static class AgendaGroupSorter
        implements
        Comparator<AgendaGroup> {
        public static final AgendaGroupSorter instance = new AgendaGroupSorter();

        public int compare(AgendaGroup group1,
                           AgendaGroup group2) {
            return group1.getName().compareTo( group2.getName() );
        }
    }

    public static class RuleFlowGroupSorter
        implements
        Comparator<RuleFlowGroup> {
        public static final RuleFlowGroupSorter instance = new RuleFlowGroupSorter();

        public int compare(RuleFlowGroup group1,
                           RuleFlowGroup group2) {
            return group1.getName().compareTo( group2.getName() );
        }
    }

    public static void writeActionQueue(MarshallerWriteContext context) throws IOException {
        ReteooWorkingMemory wm = (ReteooWorkingMemory) context.wm;

        WorkingMemoryAction[] queue = wm.getActionQueue().toArray( new WorkingMemoryAction[wm.getActionQueue().size()] );
        for ( int i = queue.length - 1; i >= 0; i-- ) {
            context.writeShort( PersisterEnums.WORKING_MEMORY_ACTION );
            queue[i].write( context );
        }
        context.writeShort( PersisterEnums.END );
    }

    public static void writeTruthMaintenanceSystem(MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;

        ObjectHashMap assertMap = context.wm.getTruthMaintenanceSystem().getAssertMap();

        EqualityKey[] keys = new EqualityKey[assertMap.size()];
        org.drools.core.util.Iterator it = assertMap.iterator();
        int i = 0;
        for ( org.drools.core.util.ObjectHashMap.ObjectEntry entry = (org.drools.core.util.ObjectHashMap.ObjectEntry) it.next(); entry != null; entry = (org.drools.core.util.ObjectHashMap.ObjectEntry) it.next() ) {
            EqualityKey key = (EqualityKey) entry.getKey();
            keys[i++] = key;
        }

        Arrays.sort( keys,
                     EqualityKeySorter.instance );

        // write the assert map of Equality keys
        for ( EqualityKey key : keys ) {
            stream.writeShort( PersisterEnums.EQUALITY_KEY );
            stream.writeInt( key.getStatus() );
            InternalFactHandle handle = key.getFactHandle();
            stream.writeInt( handle.getId() );
            //context.out.println( "EqualityKey int:" + key.getStatus() + " int:" + handle.getId() );
            if ( key.getOtherFactHandle() != null && !key.getOtherFactHandle().isEmpty() ) {
                for ( InternalFactHandle handle2 : key.getOtherFactHandle() ) {
                    stream.writeShort( PersisterEnums.FACT_HANDLE );
                    stream.writeInt( handle2.getId() );
                    //context.out.println( "OtherHandle int:" + handle2.getId() );
                }
            }
            stream.writeShort( PersisterEnums.END );
        }
        stream.writeShort( PersisterEnums.END );
    }

    public static class EqualityKeySorter
        implements
        Comparator<EqualityKey> {
        public static final EqualityKeySorter instance = new EqualityKeySorter();

        public int compare(EqualityKey key1,
                           EqualityKey key2) {
            return key1.getFactHandle().getId() - key2.getFactHandle().getId();
        }
    }

    public static void writeFactHandles(MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalWorkingMemory wm = context.wm;
        ObjectMarshallingStrategyStore objectMarshallingStrategyStore = context.objectMarshallingStrategyStore;

        writeInitialFactHandleRightTuples( context );

        stream.writeInt( wm.getObjectStore().size() );

        // Write out FactHandles
        for ( InternalFactHandle handle : orderFacts( wm.getObjectStore() ) ) {
            //stream.writeShort( PersisterEnums.FACT_HANDLE );
            //InternalFactHandle handle = (InternalFactHandle) it.next();
            writeFactHandle( context,
                             stream,
                             objectMarshallingStrategyStore,
                             handle );

            writeRightTuples( handle,
                              context );
        }

        writeInitialFactHandleLeftTuples( context );

        writeLeftTuples( context );

        writePropagationContexts( context );

        writeActivations( context );
    }

    private static void writeFactHandle(MarshallerWriteContext context,
                                        ObjectOutputStream stream,
                                        ObjectMarshallingStrategyStore objectMarshallingStrategyStore,
                                        InternalFactHandle handle) throws IOException {
        stream.writeInt( handle.getId() );
        stream.writeLong( handle.getRecency() );

        //context.out.println( "Object : int:" + handle.getId() + " long:" + handle.getRecency() );
        //context.out.println( handle.getObject() );

        Object object = handle.getObject();

        int index = objectMarshallingStrategyStore.getStrategy( object );
        
        ObjectMarshallingStrategy strategy = objectMarshallingStrategyStore.getStrategy( index );

        stream.writeInt( index );

        strategy.write( stream,
                        object );
        if( handle.getEntryPoint() instanceof InternalWorkingMemoryEntryPoint ){
            String entryPoint = ((InternalWorkingMemoryEntryPoint)handle.getEntryPoint()).getEntryPoint().getEntryPointId();
            if(entryPoint!=null && !entryPoint.equals("")){
                stream.writeBoolean(true);
                stream.writeUTF(entryPoint);
            }
            else{
                stream.writeBoolean(false);
            }
        }else{
            stream.writeBoolean(false);
        }

    }

    public static InternalFactHandle[] orderFacts(ObjectStore objectStore) {
        // this method is just needed for testing purposes, to allow round tripping
        int size = objectStore.size();
        InternalFactHandle[] handles = new InternalFactHandle[size];
        int i = 0;
        for ( Iterator it = objectStore.iterateFactHandles(); it.hasNext(); ) {
            handles[i++] = (InternalFactHandle) it.next();
        }

        Arrays.sort( handles,
                     new HandleSorter() );

        return handles;
    }

    public static class HandleSorter
        implements
        Comparator<InternalFactHandle> {
        public int compare(InternalFactHandle h1,
                           InternalFactHandle h2) {
            return h1.getId() - h2.getId();
        }
    }

    public static void writeInitialFactHandleRightTuples(MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;

        ObjectTypeNode initialFactNode = ruleBase.getRete().getEntryPointNode( EntryPoint.DEFAULT ).getObjectTypeNodes().get( new ClassObjectType( InitialFact.class ) );

        // do we write the fact to the objecttypenode memory
        if ( initialFactNode != null ) {
            ObjectHashSet initialFactMemory = (ObjectHashSet) context.wm.getNodeMemory( initialFactNode );
            if ( initialFactMemory != null && !initialFactMemory.isEmpty() ) {
                //context.out.println( "InitialFactMemory true int:" + initialFactNode.getId() );
                stream.writeBoolean( true );
                stream.writeInt( initialFactNode.getId() );

                //context.out.println( "InitialFact RightTuples" );
                writeRightTuples( context.wm.getInitialFactHandle(),
                                  context );
            } else {
                //context.out.println( "InitialFactMemory false " );
                stream.writeBoolean( false );
            }
        } else {
            //context.out.println( "InitialFactMemory false " );
            stream.writeBoolean( false );
        }
    }

    public static void writeInitialFactHandleLeftTuples(MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;

        //context.out.println( "InitialFact LeftTuples Start" );
        InternalFactHandle handle = context.wm.getInitialFactHandle();
        for ( LeftTuple leftTuple = handle.getFirstLeftTuple(); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
            stream.writeShort( PersisterEnums.LEFT_TUPLE );

            stream.writeInt( leftTuple.getLeftTupleSink().getId() );
            //context.out.println( "LeftTuple sinkId:" + leftTuple.getLeftTupleSink().getId() );
            writeLeftTuple( leftTuple,
                            context,
                            true );
        }
        stream.writeShort( PersisterEnums.END );
        //context.out.println( "InitialFact LeftTuples End" );
    }

    public static void writeRightTuples(InternalFactHandle handle,
                                        MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        //context.out.println( "RightTuples Start" );
        
        for (RightTuple rightTuple = handle.getFirstRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
            stream.writeShort( PersisterEnums.RIGHT_TUPLE );
            writeRightTuple( rightTuple,
                             context );
        }
        stream.writeShort( PersisterEnums.END );
        //context.out.println( "RightTuples END" );
    }

    public static void writeRightTuple(RightTuple rightTuple,
                                       MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalWorkingMemory wm = context.wm;
        // right tuples created in a "FromNode" have no sink, so we need to handle that appropriatelly
        int id = rightTuple.getRightTupleSink() != null ? rightTuple.getRightTupleSink().getId() : -1;
        stream.writeInt( id );
        //context.out.println( "RightTuple sinkId:" + (rightTuple.getRightTupleSink() != null ? rightTuple.getRightTupleSink().getId() : -1) );
    }

    public static void writeLeftTuples(MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalWorkingMemory wm = context.wm;

        // Write out LeftTuples
        //context.out.println( "LeftTuples Start" );
        for ( InternalFactHandle handle : orderFacts( wm.getObjectStore() ) ) {
            //InternalFactHandle handle = (InternalFactHandle) it.next();

            for ( LeftTuple leftTuple = handle.getFirstLeftTuple(); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
                stream.writeShort( PersisterEnums.LEFT_TUPLE );

                stream.writeInt( leftTuple.getLeftTupleSink().getId() );
                stream.writeInt( handle.getId() );

                //context.out.println( "LeftTuple sinkId:" + leftTuple.getLeftTupleSink().getId() + " handleId:" + handle.getId() );
                writeLeftTuple( leftTuple,
                                context,
                                true );
            }
        }
        stream.writeShort( PersisterEnums.END );
        //context.out.println( "LeftTuples End" );
    }

    public static void writeLeftTuple(LeftTuple leftTuple,
                                      MarshallerWriteContext context,
                                      boolean recurse) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalRuleBase ruleBase = context.ruleBase;
        InternalWorkingMemory wm = context.wm;

        LeftTupleSink sink = leftTuple.getLeftTupleSink();

        switch ( sink.getType() ) {
            case NodeTypeEnums.JoinNode : {
                //context.out.println( "JoinNode" );
                for ( LeftTuple childLeftTuple = leftTuple.firstChild; childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
                    stream.writeShort( PersisterEnums.RIGHT_TUPLE );
                    stream.writeInt( childLeftTuple.getLeftTupleSink().getId() );
                    stream.writeInt( childLeftTuple.getRightParent().getFactHandle().getId() );
                    //context.out.println( "RightTuple int:" + childLeftTuple.getLeftTupleSink().getId() + " int:" + childLeftTuple.getRightParent().getFactHandle().getId() );
                    writeLeftTuple( childLeftTuple,
                                    context,
                                    recurse );
                }
                stream.writeShort( PersisterEnums.END );
                //context.out.println( "JoinNode   ---   END" );
                break;
            }
            case NodeTypeEnums.EvalConditionNode : {
                //context.out.println( ".... EvalConditionNode" );
                for ( LeftTuple childLeftTuple = leftTuple.firstChild; childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
                    stream.writeShort( PersisterEnums.LEFT_TUPLE );
                    stream.writeInt( childLeftTuple.getLeftTupleSink().getId() );
                    writeLeftTuple( childLeftTuple,
                                    context,
                                    recurse );
                }
                stream.writeShort( PersisterEnums.END );
                //context.out.println( "---- EvalConditionNode   ---   END" );
                break;
            }
            case NodeTypeEnums.NotNode : 
            case NodeTypeEnums.ForallNotNode : {
                if ( leftTuple.getBlocker() == null ) {
                    // is not blocked so has children
                    stream.writeShort( PersisterEnums.LEFT_TUPLE_NOT_BLOCKED );

                    for ( LeftTuple childLeftTuple = leftTuple.firstChild; childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
                        stream.writeShort( PersisterEnums.LEFT_TUPLE );
                        stream.writeInt( childLeftTuple.getLeftTupleSink().getId() );
                        writeLeftTuple( childLeftTuple,
                                        context,
                                        recurse );
                    }
                    stream.writeShort( PersisterEnums.END );

                } else {
                    stream.writeShort( PersisterEnums.LEFT_TUPLE_BLOCKED );
                    stream.writeInt( leftTuple.getBlocker().getFactHandle().getId() );
                }
                break;
            }
            case NodeTypeEnums.ExistsNode : {
                if ( leftTuple.getBlocker() == null ) {
                    // is blocked so has children
                    stream.writeShort( PersisterEnums.LEFT_TUPLE_NOT_BLOCKED );
                } else {
                    stream.writeShort( PersisterEnums.LEFT_TUPLE_BLOCKED );
                    stream.writeInt( leftTuple.getBlocker().getFactHandle().getId() );

                    for ( LeftTuple childLeftTuple = leftTuple.firstChild; childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
                        stream.writeShort( PersisterEnums.LEFT_TUPLE );
                        stream.writeInt( childLeftTuple.getLeftTupleSink().getId() );
                        writeLeftTuple( childLeftTuple,
                                        context,
                                        recurse );
                    }
                    stream.writeShort( PersisterEnums.END );
                }
                break;
            }
            case NodeTypeEnums.AccumulateNode : {
                //context.out.println( ".... AccumulateNode" );
                // accumulate nodes generate new facts on-demand and need special procedures when serializing to persistent storage
                AccumulateMemory memory = (AccumulateMemory) context.wm.getNodeMemory( (BetaNode) sink );
                AccumulateContext accctx = (AccumulateContext) memory.betaMemory.getCreatedHandles().get( leftTuple );
                // first we serialize the generated fact handle
                writeFactHandle( context,
                                 stream,
                                 context.objectMarshallingStrategyStore,
                                 accctx.result.getFactHandle() );
                // then we serialize the associated accumulation context
                stream.writeObject( accctx.context );
                // then we serialize the boolean propagated flag
                stream.writeBoolean( accctx.propagated );

                // then we serialize all the propagated tuples
                for ( LeftTuple childLeftTuple = leftTuple.firstChild; childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
                    if( leftTuple.getLeftTupleSink().getId() == childLeftTuple.getLeftTupleSink().getId()) {
                        // this is a matching record, so, associate the right tuples
                        //context.out.println( "RightTuple(match) int:" + childLeftTuple.getLeftTupleSink().getId() + " int:" + childLeftTuple.getRightParent().getFactHandle().getId() );
                        stream.writeShort( PersisterEnums.RIGHT_TUPLE );
                        stream.writeInt( childLeftTuple.getRightParent().getFactHandle().getId() );
                    } else {
                        // this is a propagation record
                        //context.out.println( "RightTuple(propagation) int:" + childLeftTuple.getLeftTupleSink().getId() + " int:" + childLeftTuple.getRightParent().getFactHandle().getId() );
                        stream.writeShort( PersisterEnums.LEFT_TUPLE );
                        stream.writeInt( childLeftTuple.getLeftTupleSink().getId() );
                        writeLeftTuple( childLeftTuple,
                                        context,
                                        recurse );
                    }
                }
                stream.writeShort( PersisterEnums.END );
                //context.out.println( "---- AccumulateNode   ---   END" );
                break;
            }
            case NodeTypeEnums.RightInputAdaterNode : {
                //context.out.println( ".... RightInputAdapterNode" );
                // RIANs generate new fact handles on-demand to wrap tuples and need special procedures when serializing to persistent storage
                ObjectHashMap memory = (ObjectHashMap) context.wm.getNodeMemory( (NodeMemory) sink );
                InternalFactHandle ifh = (InternalFactHandle) memory.get( leftTuple );
                // first we serialize the generated fact handle ID
                //context.out.println( "FactHandle id:"+ifh.getId() );
                stream.writeInt( ifh.getId() );
                stream.writeLong( ifh.getRecency() );
                
                writeRightTuples( ifh, context );

                stream.writeShort( PersisterEnums.END );
                //context.out.println( "---- RightInputAdapterNode   ---   END" );
                break;
            }
            case NodeTypeEnums.FromNode: {
              //context.out.println( ".... FromNode" );
              // FNs generate new fact handles on-demand to wrap objects and need special procedures when serializing to persistent storage
              FromMemory memory = (FromMemory) context.wm.getNodeMemory( (NodeMemory) sink );

              Map<Object, RightTuple> matches = (Map<Object, RightTuple>) memory.betaMemory.getCreatedHandles().get( leftTuple );
              for ( RightTuple rightTuples : matches.values() ) {
                  // first we serialize the generated fact handle ID
                  stream.writeShort( PersisterEnums.FACT_HANDLE );
                  writeFactHandle( context,
                                   stream,
                                   context.objectMarshallingStrategyStore,
                                   rightTuples.getFactHandle() );
                  writeRightTuples( rightTuples.getFactHandle(), 
                                    context );
              }
              stream.writeShort( PersisterEnums.END );
              for ( LeftTuple childLeftTuple = leftTuple.firstChild; childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
                  stream.writeShort( PersisterEnums.RIGHT_TUPLE );
                  stream.writeInt( childLeftTuple.getLeftTupleSink().getId() );
                  stream.writeInt( childLeftTuple.getRightParent().getFactHandle().getId() );
                  //context.out.println( "RightTuple int:" + childLeftTuple.getLeftTupleSink().getId() + " int:" + childLeftTuple.getRightParent().getFactHandle().getId() );
                  writeLeftTuple( childLeftTuple,
                                  context,
                                  recurse );
              }
              stream.writeShort( PersisterEnums.END );
              //context.out.println( "---- FromNode   ---   END" );
              break;
          }
            case NodeTypeEnums.RuleTerminalNode : {
                //context.out.println( "RuleTerminalNode" );
                int pos = context.terminalTupleMap.size();
                context.terminalTupleMap.put( leftTuple,
                                              pos );
                break;
            }
        }
    }

    public static void writeActivations(MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;

        Entry<LeftTuple, Integer>[] entries = context.terminalTupleMap.entrySet().toArray( new Entry[context.terminalTupleMap.size()] );
        Arrays.sort( entries,
                     TupleSorter.instance );

        //Map<LeftTuple, Integer> tuples = context.terminalTupleMap;
        if ( entries.length != 0 ) {
            for ( Entry<LeftTuple, Integer> entry : entries ) {
                if (entry.getKey().getObject() != null) {
                    LeftTuple leftTuple = entry.getKey();
                    stream.writeShort(PersisterEnums.ACTIVATION);
                    writeActivation(context, leftTuple, (AgendaItem) leftTuple
                            .getObject(), (RuleTerminalNode) leftTuple
                            .getLeftTupleSink());
                }
            }
        }
        stream.writeShort( PersisterEnums.END );
    }

    public static class TupleSorter
        implements
        Comparator<Entry<LeftTuple, Integer>> {
        public static final TupleSorter instance = new TupleSorter();

        public int compare(Entry<LeftTuple, Integer> e1,
                           Entry<LeftTuple, Integer> e2) {
            return e1.getValue() - e2.getValue();
        }
    }

    public static void writeActivation(MarshallerWriteContext context,
                                       LeftTuple leftTuple,
                                       AgendaItem agendaItem,
                                       RuleTerminalNode ruleTerminalNode) throws IOException {
        ObjectOutputStream stream = context.stream;

        stream.writeLong( agendaItem.getActivationNumber() );

        stream.writeInt( context.terminalTupleMap.get( leftTuple ) );

        stream.writeInt( agendaItem.getSalience() );

        Rule rule = agendaItem.getRule();
        stream.writeUTF( rule.getPackage() );
        stream.writeUTF( rule.getName() );

        //context.out.println( "Rule " + rule.getPackage() + "." + rule.getName() );

        //context.out.println( "AgendaItem long:" + agendaItem.getPropagationContext().getPropagationNumber() );
        stream.writeLong( agendaItem.getPropagationContext().getPropagationNumber() );

        if ( agendaItem.getActivationGroupNode() != null ) {
            stream.writeBoolean( true );
            //context.out.println( "ActivationGroup bool:" + true );
            stream.writeUTF( agendaItem.getActivationGroupNode().getActivationGroup().getName() );
            //context.out.println( "ActivationGroup string:" + agendaItem.getActivationGroupNode().getActivationGroup().getName() );
        } else {
            stream.writeBoolean( false );
            //context.out.println( "ActivationGroup bool:" + false );
        }

        stream.writeBoolean( agendaItem.isActivated() );
        //context.out.println( "AgendaItem bool:" + agendaItem.isActivated() );

        org.drools.core.util.LinkedList list = agendaItem.getLogicalDependencies();
        if ( list != null && !list.isEmpty() ) {
            for ( LogicalDependency node = (LogicalDependency) list.getFirst(); node != null; node = (LogicalDependency) node.getNext() ) {
                stream.writeShort( PersisterEnums.LOGICAL_DEPENDENCY );
                stream.writeInt( ((InternalFactHandle) node.getFactHandle()).getId() );
                //context.out.println( "Logical Depenency : int " + ((InternalFactHandle) node.getFactHandle()).getId() );
            }
        }
        stream.writeShort( PersisterEnums.END );
    }

    public static void writePropagationContexts(MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;

        Entry<LeftTuple, Integer>[] entries = context.terminalTupleMap.entrySet().toArray( new Entry[context.terminalTupleMap.size()] );
        Arrays.sort( entries,
                     TupleSorter.instance );

        //Map<LeftTuple, Integer> tuples = context.terminalTupleMap;
        if ( entries.length != 0 ) {
            Map<Long, PropagationContext> pcMap = new HashMap<Long, PropagationContext>();
            for ( Entry<LeftTuple, Integer> entry : entries ) {
                LeftTuple leftTuple = entry.getKey();
                if (leftTuple.getObject() != null) {
                    PropagationContext pc = ((Activation)leftTuple.getObject())
                            .getPropagationContext();
                    if (!pcMap.containsKey(pc.getPropagationNumber())) {
                        stream.writeShort(PersisterEnums.PROPAGATION_CONTEXT);
                        writePropagationContext(context, pc);
                        pcMap.put(pc.getPropagationNumber(), pc);
                    }
                }
            }
        }

        stream.writeShort( PersisterEnums.END );
    }

    public static void writePropagationContext(MarshallerWriteContext context,
                                               PropagationContext pc) throws IOException {
        ObjectOutputStream stream = context.stream;
        Map<LeftTuple, Integer> tuples = context.terminalTupleMap;

        stream.writeInt( pc.getType() );

        Rule ruleOrigin = pc.getRuleOrigin();
        if ( ruleOrigin != null ) {
            stream.writeBoolean( true );
            stream.writeUTF( ruleOrigin.getPackage() );
            stream.writeUTF( ruleOrigin.getName() );
        } else {
            stream.writeBoolean( false );
        }

        LeftTuple tupleOrigin = pc.getLeftTupleOrigin();
        if ( tupleOrigin != null && tuples.containsKey( tupleOrigin )) {
            stream.writeBoolean( true );
            stream.writeInt( tuples.get( tupleOrigin ) );
        } else {
            stream.writeBoolean( false );
        }

        stream.writeLong( pc.getPropagationNumber() );
        if ( pc.getFactHandleOrigin() != null ) {
            stream.writeInt( ((InternalFactHandle)pc.getFactHandleOrigin()).getId() );
        } else {
            stream.writeInt( -1 );
        }

        stream.writeInt( pc.getActiveActivations() );
        stream.writeInt( pc.getDormantActivations() );

        stream.writeUTF( pc.getEntryPoint().getEntryPointId() );
    }

    
    public static void writeWorkItem(MarshallerWriteContext context,
            WorkItem workItem) throws IOException {
        ObjectOutputStream stream = context.stream;
        stream.writeLong(workItem.getId());
        stream.writeLong(workItem.getProcessInstanceId());
        stream.writeUTF(workItem.getName());
        stream.writeInt(workItem.getState());

        //Work Item Parameters
                Map<String, Object> parameters = workItem.getParameters();
                 Collection<Object> notNullValues = new ArrayList<Object>();
                for(Object value: parameters.values()){
                    if(value != null){
                        notNullValues.add(value);
                    }
                }
                
                stream.writeInt(notNullValues.size());
                for (String key : parameters.keySet()) {
                    Object object = parameters.get(key);
                    if(object != null){
                        stream.writeUTF(key);
                        int index = context.objectMarshallingStrategyStore.getStrategy(object);
                        stream.writeInt(index);
                        ObjectMarshallingStrategy strategy = context.objectMarshallingStrategyStore.getStrategy(index);
                        if(strategy.accept(object)){
                            strategy.write(stream, object);
                        }
                    }

                }
             
    }

}
