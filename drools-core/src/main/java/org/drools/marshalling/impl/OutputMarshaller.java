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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.common.ActivationIterator;
import org.drools.common.AgendaItem;
import org.drools.common.DefaultAgenda;
import org.drools.common.DefaultFactHandle;
import org.drools.common.EqualityKey;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalAgendaGroup;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleBase;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.InternalWorkingMemoryEntryPoint;
import org.drools.common.LogicalDependency;
import org.drools.common.NamedEntryPoint;
import org.drools.common.MemoryFactory;
import org.drools.common.ObjectStore;
import org.drools.common.RuleFlowGroupImpl;
import org.drools.common.WorkingMemoryAction;
import org.drools.core.util.FastIterator;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.ObjectHashSet;
import org.drools.process.instance.WorkItem;
import org.drools.reteoo.AccumulateNode.AccumulateContext;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.BetaNode;
import org.drools.reteoo.FromNode.FromMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.LeftTupleSink;
import org.drools.reteoo.NodeTypeEnums;
import org.drools.reteoo.ObjectTypeNode;
import org.drools.reteoo.QueryElementNode;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.RightTuple;
import org.drools.reteoo.RuleTerminalNode;
import org.drools.reteoo.WindowNode;
import org.drools.reteoo.WindowNode.WindowMemory;
import org.drools.rule.Behavior;
import org.drools.rule.EntryPoint;
import org.drools.rule.Rule;
import org.drools.rule.SlidingLengthWindow;
import org.drools.rule.SlidingLengthWindow.SlidingLengthWindowContext;
import org.drools.rule.SlidingTimeWindow;
import org.drools.rule.SlidingTimeWindow.SlidingTimeWindowContext;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaGroup;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleFlowGroup;
import org.drools.time.JobContext;
import org.drools.time.SelfRemovalJobContext;
import org.drools.time.Trigger;
import org.drools.time.impl.CronTrigger;
import org.drools.time.impl.IntervalTrigger;
import org.drools.time.impl.PointInTimeTrigger;
import org.drools.time.impl.PseudoClockScheduler;
import org.drools.time.impl.TimerJobInstance;
import org.kie.marshalling.ObjectMarshallingStrategy;
import org.kie.marshalling.ObjectMarshallingStrategyStore;
import org.kie.runtime.rule.WorkingMemoryEntryPoint;

public class OutputMarshaller {

    private static ProcessMarshaller processMarshaller = createProcessMarshaller();

    private static ProcessMarshaller createProcessMarshaller() {
        try {
            return ProcessMarshallerFactory.newProcessMarshaller();
        } catch ( IllegalArgumentException e ) {
            return null;
        }
    }

    public static void writeSession(MarshallerWriteContext context) throws IOException {
        //context.out.println( "... write session");
        ReteooWorkingMemory wm = (ReteooWorkingMemory) context.wm;
        wm.getAgenda().unstageActivations();

        context.writeBoolean( false );
        
        long time = 0;
        if ( context.wm.getTimerService() instanceof PseudoClockScheduler ) {
        	time = context.clockTime;
        }
        context.writeLong( time );

        context.writeInt( wm.getFactHandleFactory().getId() );
        context.writeLong( wm.getFactHandleFactory().getRecency() );
        ////context.out.println( "FactHandleFactory int:" + wm.getFactHandleFactory().getId() + " long:" + wm.getFactHandleFactory().getRecency() );

        context.writeLong( wm.getPropagationIdCounter() );
        //context.out.println( "PropagationCounter long:" + wm.getPropagationIdCounter() );        
        
        InternalFactHandle handle = context.wm.getInitialFactHandle();
        context.writeInt( handle.getId() );
        context.writeLong( handle.getRecency() );
        //context.out.println( "InitialFact int:" + handle.getId() + " long:" + handle.getRecency() );

        
        writeAgenda( context );        
        
        
        writeInitialFactHandleRightTuples( context );
        for ( WorkingMemoryEntryPoint wmep : wm.getEntryPoints().values() ) {
            context.stream.writeShort( PersisterEnums.ENTRY_POINT );
            context.stream.writeUTF( wmep.getEntryPointId() );
            writeFactHandles( context, (( NamedEntryPoint )wmep).getObjectStore() );
        }
        context.stream.writeShort( PersisterEnums.END );
        writeInitialFactHandleLeftTuples( context );
        
        writePropagationContexts( context );

        writeActivations( context );        


        writeActionQueue( context );

        writeTruthMaintenanceSystem( context );

        if ( context.marshalProcessInstances && processMarshaller != null ) {
            processMarshaller.writeProcessInstances( context );
        }
        else { 
            context.stream.writeShort( PersisterEnums.END );
        }

        if ( context.marshalWorkItems && processMarshaller != null ) {
            processMarshaller.writeWorkItems( context );
        }     
        else { 
            context.stream.writeShort( PersisterEnums.END );
        }
        
        if ( processMarshaller != null ) {
            // this now just assigns the writer, it will not write out any timer information
            processMarshaller.writeProcessTimers( context );
        }                
        else { 
            context.stream.writeShort( PersisterEnums.END );
        }
        
        // Only works for JpaJDKTimerService
        writeTimers( context.wm.getTimerService().getTimerJobInstances(), context );

        //context.out.println( "--- write session --- END");
    }

    public static void writeAgenda(MarshallerWriteContext context) throws IOException {
        InternalWorkingMemory wm = context.wm;
        DefaultAgenda agenda = (DefaultAgenda) wm.getAgenda();
        
        context.writeInt( agenda.getDormantActivations() );
        context.writeInt( agenda.getActiveActivations() );

        Map<String, ActivationGroup> activationGroups = agenda.getActivationGroupsMap();

        InternalAgendaGroup[] agendaGroups = (InternalAgendaGroup[]) agenda.getAgendaGroupsMap().values().toArray( new InternalAgendaGroup[agenda.getAgendaGroupsMap().size()] );
        Arrays.sort( agendaGroups,
                     AgendaGroupSorter.instance );

        for ( InternalAgendaGroup group : agendaGroups ) {
            context.writeShort( PersisterEnums.AGENDA_GROUP );
            context.writeUTF( group.getName() );
            context.writeBoolean( group.isActive() );
            context.writeLong( group.getActivatedForRecency() );
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
            for ( Map.Entry<Long, String> entry : nodeInstances.entrySet() ) {
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

        throw new UnsupportedOperationException(); // MDP need to update as we now have a TMS per EntryPoint
//        ObjectHashMap assertMap = context.wm.getTruthMaintenanceSystem().getAssertMap();
//
//        EqualityKey[] keys = new EqualityKey[assertMap.size()];
//        org.kie.core.util.Iterator it = assertMap.iterator();
//        int i = 0;
//        for ( org.kie.core.util.ObjectHashMap.ObjectEntry entry = (org.kie.core.util.ObjectHashMap.ObjectEntry) it.next(); entry != null; entry = (org.kie.core.util.ObjectHashMap.ObjectEntry) it.next() ) {
//            EqualityKey key = (EqualityKey) entry.getKey();
//            keys[i++] = key;
//        }
//
//        Arrays.sort( keys,
//                     EqualityKeySorter.instance );
//
//        // write the assert map of Equality keys
//        for ( EqualityKey key : keys ) {
//            stream.writeShort( PersisterEnums.EQUALITY_KEY );
//            stream.writeInt( key.getStatus() );
//            InternalFactHandle handle = key.getFactHandle();
//            stream.writeInt( handle.getId() );
//            //context.out.println( "EqualityKey int:" + key.getStatus() + " int:" + handle.getId() );
//            if ( key.size() > 1) {
//                FastIterator keyIter = key.fastIterator();
//                for ( DefaultFactHandle handle2 = key.getFirst().getNext(); handle2 != null; handle2 = ( DefaultFactHandle  ) keyIter.next( handle2 )) {
//                    stream.writeShort( PersisterEnums.FACT_HANDLE );
//                    stream.writeInt( handle2.getId() );
//                    //context.out.println( "OtherHandle int:" + handle2.getId() );                    
//                }
//            }
//            stream.writeShort( PersisterEnums.END );
//        }
//        stream.writeShort( PersisterEnums.END );
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

    public static void writeFactHandles(MarshallerWriteContext context, ObjectStore objectStore) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalWorkingMemory wm = context.wm;
        ObjectMarshallingStrategyStore objectMarshallingStrategyStore = context.objectMarshallingStrategyStore;

        List<InternalFactHandle> matchFactHandles = null;
        
        if ( ((InternalAgenda) wm.getAgenda()).isDeclarativeAgenda() ) {
            ActivationIterator it = ActivationIterator.iterator( wm );
            matchFactHandles = new ArrayList<InternalFactHandle>( 100 );
            for ( Activation item = (Activation) it.next(); item != null; item = (Activation) it.next() ) {
                matchFactHandles.add( item.getFactHandle() );
            }
        }

        stream.writeInt( objectStore.size() + ((matchFactHandles == null) ? 0 : matchFactHandles.size()) );

        // Write out FactHandles
        for ( InternalFactHandle handle : orderFacts( objectStore ) ) {
            //stream.writeShort( PersisterEnums.FACT_HANDLE );
            //InternalFactHandle handle = (InternalFactHandle) it.next();
            writeFactHandle( context,
                             stream,
                             objectMarshallingStrategyStore,
                             handle );

            writeRightTuples( handle,
                              context );
        }

        if ( matchFactHandles != null ) {
            for ( InternalFactHandle handle : orderFacts( matchFactHandles ) ) {
                Object object = handle.getObject();
                handle.setObject( null ); // we must set it to null as we don't want to write out the Activation
                writeFactHandle( context,
                                 stream,
                                 objectMarshallingStrategyStore,
                                 handle );
                handle.setObject( object ); // restore object
                writeRightTuples( handle,
                                  context );
            }
        }

        //writeLeftTuples( context );
        writeLeftTuples( context,
                         orderFacts( objectStore ) );
        
        if ( matchFactHandles != null ) {
            stream.writeBoolean( true );
            writeLeftTuples( context,
                             orderFacts( matchFactHandles ) );
        } else {
            stream.writeBoolean( false );
        }
    }

    private static void writeFactHandle(MarshallerWriteContext context,
                                        ObjectOutputStream stream,
                                        ObjectMarshallingStrategyStore objectMarshallingStrategyStore,
                                        int type,
                                        InternalFactHandle handle) throws IOException {
        stream.writeInt( type );
        stream.writeInt( handle.getId() );
        stream.writeLong( handle.getRecency() );
        
        if ( type == 2) {
            // is event
            EventFactHandle efh = ( EventFactHandle ) handle;
            stream.writeLong( efh.getStartTimestamp() );
            stream.writeLong( efh.getDuration() );
            stream.writeBoolean( efh.isExpired() );
            stream.writeLong( efh.getActivationsCount() );
        }

        //context.out.println( "Object : int:" + handle.getId() + " long:" + handle.getRecency() );
        //context.out.println( handle.getObject() );

        Object object = handle.getObject();

        // Old versions wrote -1 and tested >= 0 to see if there was a strategy available
        // Now, we write -2 to indicate that we write the strategy class name to the stream
        stream.writeInt(-2);
        if ( object != null ) {
            ObjectMarshallingStrategy strategy = objectMarshallingStrategyStore.getStrategyObject( object );

            String strategyClassName = strategy.getClass().getName();
            stream.writeUTF(strategyClassName);

            strategy.write( stream,
                            object );
        } else {
            stream.writeUTF("");
        }

        if ( handle.getEntryPoint() instanceof InternalWorkingMemoryEntryPoint ) {
            String entryPoint = ((InternalWorkingMemoryEntryPoint) handle.getEntryPoint()).getEntryPoint().getEntryPointId();
            if ( entryPoint != null && !entryPoint.equals( "" ) ) {
                stream.writeBoolean( true );
                stream.writeUTF( entryPoint );
            }
            else {
                stream.writeBoolean( false );
            }
        } else {
            stream.writeBoolean( false );
        }
    }

    private static void writeFactHandle(MarshallerWriteContext context,
                                        ObjectOutputStream stream,
                                        ObjectMarshallingStrategyStore objectMarshallingStrategyStore,
                                        InternalFactHandle handle) throws IOException {
        writeFactHandle( context,
                         stream,
                         objectMarshallingStrategyStore,
                         ( handle instanceof EventFactHandle ) ? 2 : 0,
                         handle );

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

    public static InternalFactHandle[] orderFacts(List<InternalFactHandle> handlesList) {
        // this method is just needed for testing purposes, to allow round tripping
        int size = handlesList.size();
        InternalFactHandle[] handles = handlesList.toArray( new InternalFactHandle[size] );
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

        ObjectTypeNode initialFactNode = ruleBase.getRete().getEntryPointNode( EntryPoint.DEFAULT ).getObjectTypeNodes().get( ClassObjectType.InitialFact_ObjectType );

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

        for ( RightTuple rightTuple = handle.getFirstRightTuple(); rightTuple != null; rightTuple = (RightTuple) rightTuple.getHandleNext() ) {
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

    public static void writeLeftTuples(MarshallerWriteContext context,
                                       InternalFactHandle[] factHandles) throws IOException {
        ObjectOutputStream stream = context.stream;
        InternalWorkingMemory wm = context.wm;

        // Write out LeftTuples
        //context.out.println( "LeftTuples Start" );
        for ( InternalFactHandle handle : factHandles ) {
            //InternalFactHandle handle = (InternalFactHandle) it.next();

            for ( LeftTuple leftTuple = handle.getFirstLeftTuple(); leftTuple != null; leftTuple = (LeftTuple) leftTuple.getLeftParentNext() ) {
                stream.writeShort( PersisterEnums.LEFT_TUPLE );
                int sinkId = leftTuple.getLeftTupleSink().getId() ;
                stream.writeInt( sinkId );
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
                for ( LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
                    stream.writeShort( PersisterEnums.RIGHT_TUPLE );
                    int childSinkId = childLeftTuple.getLeftTupleSink().getId();
                    stream.writeInt( childSinkId );
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
            case NodeTypeEnums.QueryRiaFixerNode : 
            case NodeTypeEnums.EvalConditionNode : {
                //context.out.println( ".... EvalConditionNode" );
                for ( LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
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

                    for ( LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
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

                    for ( LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
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
                AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
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
                for ( LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
                    if ( leftTuple.getLeftTupleSink().getId() == childLeftTuple.getLeftTupleSink().getId() ) {
                        // this is a matching record, so, associate the right tuples
                        //context.out.println( "RightTuple(match) int:" + childLeftTuple.getLeftTupleSink().getId() + " int:" + childLeftTuple.getRightParent().getFactHandle().getId() );
                        stream.writeShort( PersisterEnums.RIGHT_TUPLE );
                        stream.writeInt( childLeftTuple.getRightParent().getFactHandle().getId() );
                    } else {
                        // this is a propagation record
                        //context.out.println( "RightTuple(propagation) int:" + childLeftTuple.getLeftTupleSink().getId() + " int:" + childLeftTuple.getRightParent().getFactHandle().getId() );
                        stream.writeShort( PersisterEnums.LEFT_TUPLE );
                        int sinkId = childLeftTuple.getLeftTupleSink().getId();
                        stream.writeInt( sinkId );
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
                ObjectHashMap memory = (ObjectHashMap) context.wm.getNodeMemory( (MemoryFactory) sink );
                InternalFactHandle ifh = (InternalFactHandle) memory.get( leftTuple );
                // first we serialize the generated fact handle ID
                //context.out.println( "FactHandle id:"+ifh.getId() );
                stream.writeInt( ifh.getId() );
                stream.writeLong( ifh.getRecency() );

                writeRightTuples( ifh,
                                  context );

                stream.writeShort( PersisterEnums.END );
                //context.out.println( "---- RightInputAdapterNode   ---   END" );
                break;
            }
            case NodeTypeEnums.FromNode : {
                //context.out.println( ".... FromNode" );
                // FNs generate new fact handles on-demand to wrap objects and need special procedures when serializing to persistent storage
                FromMemory memory = (FromMemory) context.wm.getNodeMemory( (MemoryFactory) sink );

                Map<Object, RightTuple> matches = (Map<Object, RightTuple>) leftTuple.getObject();
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
                for ( LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
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
            case NodeTypeEnums.UnificationNode : {
                //context.out.println( ".... UnificationNode" );

                QueryElementNode node = (QueryElementNode) sink;
                boolean isOpen = node.isOpenQuery();

                context.writeBoolean( isOpen );
                if ( isOpen ) {
                    InternalFactHandle factHandle = (InternalFactHandle) leftTuple.getObject();
                    DroolsQuery query = (DroolsQuery) factHandle.getObject();
                    
                    //context.out.println( "factHandle:" +  factHandle );
                    
                    factHandle.setObject( null );
                    writeFactHandle( context,
                                     stream,
                                     context.objectMarshallingStrategyStore,
                                     0,
                                     factHandle );
                    factHandle.setObject( query );
                    writeLeftTuples( context,
                                     new InternalFactHandle[]{factHandle} );                    
                } else {
                    for ( LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
                        stream.writeShort( PersisterEnums.LEFT_TUPLE );
                        stream.writeInt( childLeftTuple.getLeftTupleSink().getId() );
                        InternalFactHandle factHandle = childLeftTuple.getLastHandle();
                        writeFactHandle( context,
                                         stream,
                                         context.objectMarshallingStrategyStore,
                                         1,
                                         factHandle );
                        writeLeftTuple( childLeftTuple,
                                        context,
                                        recurse );
                    }
                    stream.writeShort( PersisterEnums.END );
                }                
                //context.out.println( "---- EvalConditionNode   ---   END" );
                break;
            }
            case NodeTypeEnums.RuleTerminalNode : {
                //context.out.println( "RuleTerminalNode" );
                int pos = context.terminalTupleMap.size();
                context.terminalTupleMap.put( leftTuple,
                                              pos );
                break;
            }
            case NodeTypeEnums.QueryTerminalNode : {
                //context.out.println( ".... QueryTerminalNode" );                
                //                LeftTuple entry = leftTuple;
                //
                //                // find the DroolsQuery object
                //                while ( entry.getParent() != null ) {
                //                    entry = entry.getParent();
                //                }
                //
                //                // Now output all the child tuples in the caller network
                //                DroolsQuery query = (DroolsQuery) entry.getLastHandle().getObject();
                //                if ( query.getQueryResultCollector() instanceof UnificationNodeViewChangedEventListener ) {
                //                    context.writeBoolean( true );
                //                    UnificationNodeViewChangedEventListener collector = (UnificationNodeViewChangedEventListener) query.getQueryResultCollector();
                //                    leftTuple = collector.getLeftTuple();
                //        
                context.writeBoolean( true );
                RightTuple rightTuple = (RightTuple) leftTuple.getObject();
                //context.out.println( "rightTuple:" +  rightTuple.getFactHandle() );
                writeFactHandle( context,
                                 stream,
                                 context.objectMarshallingStrategyStore,
                                 1,
                                 rightTuple.getFactHandle() );
                
                for ( LeftTuple childLeftTuple = rightTuple.firstChild; childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getRightParentNext() ) {
                    stream.writeShort( PersisterEnums.LEFT_TUPLE );
                    stream.writeInt( childLeftTuple.getLeftTupleSink().getId() );
                    writeLeftTuple( childLeftTuple,
                                    context,
                                    recurse );
                }

                //                    for ( LeftTuple childLeftTuple = leftTuple.getFirstChild(); childLeftTuple != null; childLeftTuple = (LeftTuple) childLeftTuple.getLeftParentNext() ) {
                //                        stream.writeShort( PersisterEnums.LEFT_TUPLE );
                //                        stream.writeInt( childLeftTuple.getLeftTupleSink().getId() );
                //                        writeFactHandle( context,
                //                                         stream,
                //                                         context.objectMarshallingStrategyStore,
                //                                         1,
                //                                         childLeftTuple.getLastHandle() );
                //                        writeLeftTuple( childLeftTuple,
                //                                        context,
                //                                        recurse );
                //                    }                      
                //                } else {
                //                    context.writeBoolean( false );
                //                }
                stream.writeShort( PersisterEnums.END );
                //context.out.println( "---- QueryTerminalNode   ---   END" );
                break;
            }
        }
    }
    
    public static void writeBehaviours(WindowNode windowNode,
                                       MarshallerWriteContext outCtx) throws IOException {
        Behavior[] behaviors = windowNode.getBehaviors();
        
        WindowMemory memory = (WindowMemory) outCtx.wm.getNodeMemory( windowNode );       
        
        Object[] behaviorContexts = ( Object[] ) memory.behaviorContext;
        
        for ( int i = 0; i < behaviors.length; i++ ) {
            if ( windowNode.getBehaviors()[i] instanceof SlidingTimeWindow) {
                outCtx.writeShort( PersisterEnums.SLIDING_TIME_WIN );
                outCtx.writeInt( i );
                writeSlidingTimeWindowBehaviour( ( SlidingTimeWindow) windowNode.getBehaviors()[i], 
                                                 ( SlidingTimeWindowContext ) behaviorContexts[i], 
                                                 outCtx);
            } else if ( windowNode.getBehaviors()[i] instanceof SlidingLengthWindow) {
                outCtx.writeShort( PersisterEnums.SLIDING_LENGTH_WIN );
                outCtx.writeInt( i );
                writeSlidingLengthWindowBehaviour( ( SlidingLengthWindow) windowNode.getBehaviors()[i], 
                                                 ( SlidingLengthWindowContext ) behaviorContexts[i], 
                                                 outCtx);                
            }
        } 
        outCtx.writeShort( PersisterEnums.END );
    }
    
    public static void writeSlidingTimeWindowBehaviour(SlidingTimeWindow stw,
                                                       SlidingTimeWindowContext slCtx,
                                                       MarshallerWriteContext outputCtx) throws IOException {
        // It's timers are restored by writeTimers
        // FIXME
//        if ( slCtx.expiringTuple != null ) {
//            outputCtx.writeBoolean( true );
//
//            outputCtx.writeInt( slCtx.expiringTuple.getRightTupleSink().getId() );
//            outputCtx.writeInt( slCtx.expiringTuple.getFactHandle().getId() );
//        } else {
//            outputCtx.writeBoolean( false );
//        }
//
//        if ( slCtx.getQueue() != null ) {
//            outputCtx.writeBoolean( true );
//            outputCtx.writeInt( slCtx.getQueue().size() );
//            for ( RightTuple rightTuple : slCtx.getQueue() ) {
//                outputCtx.writeInt( rightTuple.getRightTupleSink().getId() );
//                outputCtx.writeInt( rightTuple.getFactHandle().getId() );
//            }
//        } else {
//            outputCtx.writeBoolean( false );
//        }
    }

    public static void writeSlidingLengthWindowBehaviour(SlidingLengthWindow stw,
                                                         SlidingLengthWindowContext slCtx,
                                                         MarshallerWriteContext outputCtx) throws IOException {
        // FIXME
//        outputCtx.writeInt( slCtx.pos );
//        
//        outputCtx.writeInt( slCtx.rightTuples.length );
//        
//        for ( RightTuple rightTuple : slCtx.rightTuples ) {
//            if ( rightTuple == null ) {
//                outputCtx.writeInt( -1 );
//            } else {
//                outputCtx.writeInt( rightTuple.getFactHandle().getId() );
//                outputCtx.writeInt( rightTuple.getRightTupleSink().getId() );                
//            }
//        }
    }
    
    public static void writeActivations(MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;

        Entry<LeftTuple, Integer>[] entries = context.terminalTupleMap.entrySet().toArray( new Entry[context.terminalTupleMap.size()] );
        Arrays.sort( entries,
                     TupleSorter.instance );

        //Map<LeftTuple, Integer> tuples = context.terminalTupleMap;
        if ( entries.length != 0 ) {
            for ( Entry<LeftTuple, Integer> entry : entries ) {
                if ( entry.getKey().getObject() != null ) {
                    LeftTuple leftTuple = entry.getKey();
                    stream.writeShort( PersisterEnums.ACTIVATION );
                    writeActivation( context,
                                     leftTuple,
                                     (AgendaItem) leftTuple
                                             .getObject(),
                                     (RuleTerminalNode) leftTuple
                                             .getLeftTupleSink() );
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

        if ( agendaItem.getFactHandle() != null ) {
            stream.writeBoolean( true );
            stream.writeInt( agendaItem.getFactHandle().getId() );
        } else {
            stream.writeBoolean( false );
        }

        org.drools.core.util.LinkedList<LogicalDependency> list = agendaItem.getLogicalDependencies();
        if ( list != null && !list.isEmpty() ) {
            for (LogicalDependency node = list.getFirst(); node != null; node = node.getNext() ) {
                stream.writeShort( PersisterEnums.LOGICAL_DEPENDENCY );
                stream.writeInt( ((InternalFactHandle) node.getJustified()).getId() );
                //context.out.println( "Logical Depenency : int " + ((InternalFactHandle) node.getFactHandle()).getId() );
            }
        }
        stream.writeShort( PersisterEnums.END );
    }

    public static void writePropagationContexts(MarshallerWriteContext context) throws IOException {
        ObjectOutputStream stream = context.stream;
        
        if ( context.terminalTupleMap != null && context.terminalTupleMap.size() > 0  ) {
            Entry<LeftTuple, Integer>[] entries = context.terminalTupleMap.entrySet().toArray( new Entry[context.terminalTupleMap.size()] );
            Arrays.sort( entries,
                         TupleSorter.instance );
    
            //Map<LeftTuple, Integer> tuples = context.terminalTupleMap;
            if ( entries.length != 0 ) {
                Map<Long, PropagationContext> pcMap = new HashMap<Long, PropagationContext>();
                for ( Entry<LeftTuple, Integer> entry : entries ) {
                    LeftTuple leftTuple = entry.getKey();
                    if ( leftTuple.getObject() != null ) {
                        PropagationContext pc = ((Activation) leftTuple.getObject()).getPropagationContext();
                        if ( !pcMap.containsKey( pc.getPropagationNumber() ) ) {
                            stream.writeShort( PersisterEnums.PROPAGATION_CONTEXT );
                            writePropagationContext( context,
                                                     pc );
                            pcMap.put( pc.getPropagationNumber(),
                                       pc );
                        }
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
        if ( tupleOrigin != null && tuples.containsKey( tupleOrigin ) ) {
            stream.writeBoolean( true );
            stream.writeInt( tuples.get( tupleOrigin ) );
        } else {
            stream.writeBoolean( false );
        }

        stream.writeLong( pc.getPropagationNumber() );
        if ( pc.getFactHandleOrigin() != null ) {
            stream.writeInt( ((InternalFactHandle) pc.getFactHandleOrigin()).getId() );
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
        stream.writeLong( workItem.getId() );
        stream.writeLong( workItem.getProcessInstanceId() );
        stream.writeUTF( workItem.getName() );
        stream.writeInt( workItem.getState() );

        //Work Item Parameters
        Map<String, Object> parameters = workItem.getParameters();
        Collection<Object> notNullValues = new ArrayList<Object>();
        for ( Object value : parameters.values() ) {
            if ( value != null ) {
                notNullValues.add( value );
            }
        }

        stream.writeInt( notNullValues.size() );
        for ( String key : parameters.keySet() ) {
            Object object = parameters.get( key );
            if ( object != null ) {
                stream.writeUTF( key );
                
                ObjectMarshallingStrategy strategy = context.objectMarshallingStrategyStore.getStrategyObject( object );
                String strategyClassName = strategy.getClass().getName();
                stream.writeInt(-2); // backwards compatibility
                stream.writeUTF(strategyClassName);
                if ( strategy.accept( object ) ) {
                    strategy.write( stream,
                                    object );
                }
            }

        }

    }         
    
    public static void writeTimers(Collection<TimerJobInstance> timers, MarshallerWriteContext outCtx) throws IOException {
        List<TimerJobInstance> sortedTimers = new ArrayList<TimerJobInstance>( timers );
        Collections.sort( sortedTimers,
                          new Comparator<TimerJobInstance>() {
                              public int compare(TimerJobInstance o1,
                                                 TimerJobInstance o2) {
                                  return (int) (o1.getJobHandle().getId() - o2.getJobHandle().getId());
                              }
                          } );
        
        for ( TimerJobInstance timer : sortedTimers ) {
            outCtx.writeShort( PersisterEnums.DEFAULT_TIMER );
            JobContext jctx = ((SelfRemovalJobContext)timer.getJobContext()).getJobContext();
            TimersOutputMarshaller writer =  outCtx.writersByClass.get( jctx.getClass() );
            writer.write( jctx, outCtx );              
        }
        outCtx.writeShort( PersisterEnums.END );
    }
    
    public static void writeTrigger(Trigger trigger, MarshallerWriteContext outCtx) throws IOException {
        if ( trigger instanceof CronTrigger ) {
            outCtx.writeShort( PersisterEnums.CRON_TRIGGER );           
            
            CronTrigger cronTrigger = ( CronTrigger ) trigger;
            outCtx.writeLong( cronTrigger.getStartTime().getTime() );
            if ( cronTrigger.getEndTime() != null ) {
                outCtx.writeBoolean( true );
                outCtx.writeLong( cronTrigger.getEndTime().getTime() );
            } else {
                outCtx.writeBoolean( false );
            }
            outCtx.writeInt( cronTrigger.getRepeatLimit() );  
            outCtx.writeInt( cronTrigger.getRepeatCount() );
            outCtx.writeUTF( cronTrigger.getCronEx().getCronExpression() );            
            if ( cronTrigger.getNextFireTime() != null ) {
                outCtx.writeBoolean( true );
                outCtx.writeLong( cronTrigger.getNextFireTime().getTime() );
            } else {
                outCtx.writeBoolean( false );
            }             
            outCtx.writeObject( cronTrigger.getCalendarNames() );
        } else if ( trigger instanceof IntervalTrigger ) {
            outCtx.writeShort( PersisterEnums.INT_TRIGGER );
            
            IntervalTrigger intTrigger = ( IntervalTrigger ) trigger;
            outCtx.writeLong( intTrigger.getStartTime().getTime() );
            if ( intTrigger.getEndTime() != null ) {
                outCtx.writeBoolean( true );
                outCtx.writeLong( intTrigger.getEndTime().getTime() );
            } else {
                outCtx.writeBoolean( false );
            }
            outCtx.writeInt( intTrigger.getRepeatLimit() );
            outCtx.writeInt( intTrigger.getRepeatCount() );
            if ( intTrigger.getNextFireTime() != null ) {
                outCtx.writeBoolean( true );
                outCtx.writeLong( intTrigger.getNextFireTime().getTime() );
            } else {
                outCtx.writeBoolean( false );
            }            
            outCtx.writeLong( intTrigger.getPeriod() );
            outCtx.writeObject( intTrigger.getCalendarNames() );
        } else if ( trigger instanceof PointInTimeTrigger ) {
            outCtx.writeShort( PersisterEnums.POINT_IN_TIME_TRIGGER );
            
            PointInTimeTrigger pinTrigger = ( PointInTimeTrigger ) trigger;
            
            outCtx.writeLong( pinTrigger.hasNextFireTime().getTime() );
        }
    }

}
