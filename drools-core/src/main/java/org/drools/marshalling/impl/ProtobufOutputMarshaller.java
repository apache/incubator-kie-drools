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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.InitialFact;
import org.drools.common.ActivationIterator;
import org.drools.common.AgendaItem;
import org.drools.common.DefaultAgenda;
import org.drools.common.EqualityKey;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.LeftTupleIterator;
import org.drools.common.LogicalDependency;
import org.drools.common.Memory;
import org.drools.common.NamedEntryPoint;
import org.drools.common.NodeMemories;
import org.drools.common.ObjectStore;
import org.drools.common.QueryElementFactHandle;
import org.drools.common.RuleFlowGroupImpl;
import org.drools.common.WorkingMemoryAction;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.ObjectHashMap;
import org.drools.core.util.ObjectHashMap.ObjectEntry;
import org.drools.marshalling.ObjectMarshallingStrategy;
import org.drools.marshalling.ObjectMarshallingStrategyStore;
import org.drools.marshalling.impl.ProtobufMessages.FactHandle;
import org.drools.marshalling.impl.ProtobufMessages.ProcessData.Builder;
import org.drools.marshalling.impl.ProtobufMessages.Timers;
import org.drools.marshalling.impl.ProtobufMessages.Timers.Timer;
import org.drools.reteoo.AccumulateNode.AccumulateContext;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.FromNode.FromMemory;
import org.drools.reteoo.LeftTuple;
import org.drools.reteoo.NodeTypeEnums;
import org.drools.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.reteoo.RightInputAdapterNode.RIAMemory;
import org.drools.reteoo.RightTuple;
import org.drools.rule.Rule;
import org.drools.runtime.rule.WorkingMemoryEntryPoint;
import org.drools.spi.AgendaGroup;
import org.drools.spi.RuleFlowGroup;
import org.drools.time.JobContext;
import org.drools.time.SelfRemovalJobContext;
import org.drools.time.Trigger;
import org.drools.time.impl.CronTrigger;
import org.drools.time.impl.IntervalTrigger;
import org.drools.time.impl.PointInTimeTrigger;
import org.drools.time.impl.PseudoClockScheduler;
import org.drools.time.impl.TimerJobInstance;

import com.google.protobuf.ByteString;

/**
 * An output marshaller that uses ProtoBuf as the marshalling framework
 * in order to provide backward compatibility with marshalled sessions
 * 
 * @author etirelli
 */
public class ProtobufOutputMarshaller {
    
    private static ProcessMarshaller processMarshaller = createProcessMarshaller();

    private static ProcessMarshaller createProcessMarshaller() {
        try {
            return ProcessMarshallerFactory.newProcessMarshaller();
        } catch ( IllegalArgumentException e ) {
            return null;
        }
    }

    public static void writeSession(MarshallerWriteContext context) throws IOException {
        
        ProtobufMessages.KnowledgeSession _session = serializeSession( context );
        
        PersisterHelper.writeToStreamWithHeader( context, 
                                                 _session );
    }

    private static ProtobufMessages.KnowledgeSession serializeSession(MarshallerWriteContext context) throws IOException {
        ReteooWorkingMemory wm = (ReteooWorkingMemory) context.wm;
        wm.getAgenda().unstageActivations();
        
        ProtobufMessages.RuleData.Builder _ruleData = ProtobufMessages.RuleData.newBuilder();

        final boolean multithread = wm.isPartitionManagersActive();
        if ( multithread ) {
            wm.stopPartitionManagers();
        }

        long time = 0;
        if ( context.wm.getTimerService() instanceof PseudoClockScheduler ) {
            time = context.clockTime;
        }
        _ruleData.setLastId( wm.getFactHandleFactory().getId() );
        _ruleData.setLastRecency( wm.getFactHandleFactory().getRecency() );

        InternalFactHandle handle = context.wm.getInitialFactHandle();
        ProtobufMessages.FactHandle _ifh = ProtobufMessages.FactHandle.newBuilder()
                .setType( ProtobufMessages.FactHandle.HandleType.INITIAL_FACT )
                .setId( handle.getId() )
                .setRecency( handle.getRecency() )
                .build();
        _ruleData.setInitialFact( _ifh );

        writeAgenda( context, _ruleData );
        
        writeNodeMemories( context, _ruleData );

        for ( WorkingMemoryEntryPoint wmep : wm.getEntryPoints().values() ) {
            org.drools.marshalling.impl.ProtobufMessages.EntryPoint.Builder _epb = ProtobufMessages.EntryPoint.newBuilder();
            _epb.setEntryPointId( wmep.getEntryPointId() );
            writeFactHandles( context,
                              _epb,
                              ((NamedEntryPoint) wmep).getObjectStore() );
            _ruleData.addEntryPoint( _epb.build() );
        }


        writeActionQueue( context,
                          _ruleData );

        writeTruthMaintenanceSystem( context,
                                     _ruleData );

        ProtobufMessages.KnowledgeSession.Builder _session = ProtobufMessages.KnowledgeSession.newBuilder()
                .setMultithread( multithread )
                .setTime( time )
                .setRuleData( _ruleData.build() );
        
        if( processMarshaller != null ) {
            Builder _pdata = ProtobufMessages.ProcessData.newBuilder();
            if ( context.marshalProcessInstances ) {
                context.parameterObject = _pdata;
                processMarshaller.writeProcessInstances( context );
            }

            if ( context.marshalWorkItems ) {
                context.parameterObject = _pdata;
                processMarshaller.writeWorkItems( context );
            }     

            // this now just assigns the writer, it will not write out any timer information
            context.parameterObject = _pdata;
            processMarshaller.writeProcessTimers( context );
            
            _session.setProcessData( _pdata.build() );
        }
        
        Timers _timers = writeTimers( context.wm.getTimerService().getTimerJobInstances(), 
                                      context );
        if( _timers != null ) {
            _session.setTimers( _timers );
        }

        if ( multithread ) {
            wm.startPartitionManagers();
        }
        
        return _session.build();
    }

    private static void writeAgenda(MarshallerWriteContext context,
                                    ProtobufMessages.RuleData.Builder _ksb) throws IOException {
        InternalWorkingMemory wm = context.wm;
        DefaultAgenda agenda = (DefaultAgenda) wm.getAgenda();

        org.drools.marshalling.impl.ProtobufMessages.Agenda.Builder _ab = ProtobufMessages.Agenda.newBuilder();

        AgendaGroup[] agendaGroups = (AgendaGroup[]) agenda.getAgendaGroupsMap().values().toArray( new AgendaGroup[agenda.getAgendaGroupsMap().size()] );
        Arrays.sort( agendaGroups,
                     AgendaGroupSorter.instance );
        for ( AgendaGroup group : agendaGroups ) {
            org.drools.marshalling.impl.ProtobufMessages.Agenda.AgendaGroup.Builder _agb = ProtobufMessages.Agenda.AgendaGroup.newBuilder();
            _agb.setName( group.getName() );
            _agb.setIsActive( group.isActive() );
            _ab.addAgendaGroup( _agb.build() );
            
        }

        org.drools.marshalling.impl.ProtobufMessages.Agenda.FocusStack.Builder _fsb = ProtobufMessages.Agenda.FocusStack.newBuilder();
        LinkedList<AgendaGroup> focusStack = agenda.getStackList();
        for ( Iterator<AgendaGroup> it = focusStack.iterator(); it.hasNext(); ) {
            AgendaGroup group = it.next();
            _fsb.addGroupName( group.getName() );
        }
        _ab.setFocusStack( _fsb.build() );

        RuleFlowGroupImpl[] ruleFlowGroups = (RuleFlowGroupImpl[]) agenda.getRuleFlowGroupsMap().values().toArray( new RuleFlowGroupImpl[agenda.getRuleFlowGroupsMap().size()] );
        Arrays.sort( ruleFlowGroups,
                     RuleFlowGroupSorter.instance );
        for ( RuleFlowGroupImpl group : ruleFlowGroups ) {
            org.drools.marshalling.impl.ProtobufMessages.Agenda.RuleFlowGroup.Builder _rfgb = ProtobufMessages.Agenda.RuleFlowGroup.newBuilder();
            _rfgb.setName( group.getName() );
            _rfgb.setIsActive( group.isActive() );
            _rfgb.setIsAutoDeactivate( group.isAutoDeactivate() );
            
            Map<Long, String> nodeInstances = group.getNodeInstances();
            for ( Map.Entry<Long, String> entry : nodeInstances.entrySet() ) {
                org.drools.marshalling.impl.ProtobufMessages.Agenda.RuleFlowGroup.NodeInstance.Builder _nib = ProtobufMessages.Agenda.RuleFlowGroup.NodeInstance.newBuilder();
                _nib.setProcessInstanceId( entry.getKey() );
                _nib.setNodeInstanceId( entry.getValue() );
                _rfgb.addNodeInstance( _nib.build() );
            }
            _ab.addRuleFlowGroup( _rfgb.build() );
        }
        
        // serialize all dormant activations
        ActivationIterator it = ActivationIterator.iterator( wm );
        List<org.drools.spi.Activation> dormant = new ArrayList<org.drools.spi.Activation>();
        for ( org.drools.spi.Activation item = (org.drools.spi.Activation) it.next(); item != null; item = (org.drools.spi.Activation) it.next() ) {
            if( ! item.isActive() ) {
                dormant.add( item );
            }
        }
        Collections.sort( dormant, ActivationsSorter.INSTANCE );
        for( org.drools.spi.Activation activation : dormant ) {
            _ab.addActivation( writeActivation( context, (AgendaItem) activation ) );
        }
        
        _ksb.setAgenda( _ab.build() );
    }

    private static void writeNodeMemories(MarshallerWriteContext context,
                                          ProtobufMessages.RuleData.Builder _ksb) throws IOException {
        InternalWorkingMemory wm = context.wm;
        NodeMemories memories = wm.getNodeMemories();
        // only some of the node memories require special serialization handling
        // so we iterate over all of them and process only those that require it
        for( int i = 0; i < memories.length(); i++ ) {
            Memory memory = memories.peekNodeMemory( i );
            // some nodes have no memory, so we need to check for nulls
            if( memory != null ) {
                ProtobufMessages.NodeMemory _node = null;
                switch( memory.getNodeType() ) {
                    case NodeTypeEnums.AccumulateNode : {
                        _node = writeAccumulateNodeMemory( i, memory );
                        break;
                    }
                    case NodeTypeEnums.RightInputAdaterNode : {
                        _node = writeRIANodeMemory( i, memory );
                        break;
                    }
                    case NodeTypeEnums.FromNode : {
                        _node = writeFromNodeMemory( i, memory );
                        break;
                    }
                    case NodeTypeEnums.QueryElementNode : {
                        _node = writeQueryElementNodeMemory( i, memory, wm );
                        break;
                    }
                }
                if( _node != null ) {
                    // not all node memories require serialization
                    _ksb.addNodeMemory( _node );
                }
            }
        }

    }

    private static ProtobufMessages.NodeMemory writeAccumulateNodeMemory(final int nodeId,
                                                                         final Memory memory) {
        // for accumulate nodes, we need to store the ID of created (result) handles
        AccumulateMemory accmem = (AccumulateMemory) memory;
        if( accmem.betaMemory.getLeftTupleMemory().size() > 0 ) {
            ProtobufMessages.NodeMemory.AccumulateNodeMemory.Builder _accumulate = ProtobufMessages.NodeMemory.AccumulateNodeMemory.newBuilder();
            
            final org.drools.core.util.Iterator tupleIter = accmem.betaMemory.getLeftTupleMemory().iterator();
            for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
                AccumulateContext accctx = (AccumulateContext) leftTuple.getObject();
                if( accctx.result != null ) {
                    FactHandle _handle = ProtobufMessages.FactHandle.newBuilder()
                            .setId( accctx.result.getFactHandle().getId() )
                            .setRecency( accctx.result.getFactHandle().getRecency() )
                            .build();
                    _accumulate.addContext( 
                            ProtobufMessages.NodeMemory.AccumulateNodeMemory.AccumulateContext.newBuilder()
                            .setTuple( PersisterHelper.createTuple( leftTuple ) )
                            .setResultHandle( _handle )
                            .build() );
                }
            }

            return ProtobufMessages.NodeMemory.newBuilder()
                    .setNodeId( nodeId )
                    .setNodeType( ProtobufMessages.NodeMemory.NodeType.ACCUMULATE )
                    .setAccumulate( _accumulate.build() )
                    .build();
        }
        return null;
    }

    private static ProtobufMessages.NodeMemory writeRIANodeMemory(final int nodeId,
                                                                  final Memory memory) {
        // for RIA nodes, we need to store the ID of the created handles
        RIAMemory mem = (RIAMemory) memory;
        if( ! mem.memory.isEmpty() ) {
            ProtobufMessages.NodeMemory.RIANodeMemory.Builder _ria = ProtobufMessages.NodeMemory.RIANodeMemory.newBuilder();
            
            final org.drools.core.util.Iterator it = mem.memory.iterator();
            // iterates over all propagated handles and assert them to the new sink
            for ( ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next() ) {
                LeftTuple leftTuple = (LeftTuple) entry.getKey();
                InternalFactHandle handle = (InternalFactHandle) entry.getValue();
                FactHandle _handle = ProtobufMessages.FactHandle.newBuilder()
                        .setId( handle.getId() )
                        .setRecency( handle.getRecency() )
                        .build();
                _ria.addContext( ProtobufMessages.NodeMemory.RIANodeMemory.RIAContext.newBuilder()
                                 .setTuple( PersisterHelper.createTuple( leftTuple ) )
                                 .setResultHandle( _handle )
                                 .build() );
            }

            return ProtobufMessages.NodeMemory.newBuilder()
                    .setNodeId( nodeId )
                    .setNodeType( ProtobufMessages.NodeMemory.NodeType.RIA )
                    .setRia( _ria.build() )
                    .build();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static ProtobufMessages.NodeMemory writeFromNodeMemory(final int nodeId,
                                                                   final Memory memory) {
        FromMemory fromMemory = (FromMemory) memory;
        
        if( fromMemory.betaMemory.getLeftTupleMemory().size() > 0 ) {
            ProtobufMessages.NodeMemory.FromNodeMemory.Builder _from = ProtobufMessages.NodeMemory.FromNodeMemory.newBuilder();
            
            final org.drools.core.util.Iterator tupleIter = fromMemory.betaMemory.getLeftTupleMemory().iterator();
            for ( LeftTuple leftTuple = (LeftTuple) tupleIter.next(); leftTuple != null; leftTuple = (LeftTuple) tupleIter.next() ) {
                Map<Object,RightTuple> matches = (Map<Object,RightTuple>) leftTuple.getObject();
                ProtobufMessages.NodeMemory.FromNodeMemory.FromContext.Builder _context = ProtobufMessages.NodeMemory.FromNodeMemory.FromContext.newBuilder()
                        .setTuple( PersisterHelper.createTuple( leftTuple ) );
                for( RightTuple rightTuple : matches.values() ) {
                    FactHandle _handle = ProtobufMessages.FactHandle.newBuilder()
                            .setId( rightTuple.getFactHandle().getId() )
                            .setRecency( rightTuple.getFactHandle().getRecency() )
                            .build();
                    _context.addHandle( _handle );
                }
                _from.addContext( _context.build() );
            }

            return ProtobufMessages.NodeMemory.newBuilder()
                    .setNodeId( nodeId )
                    .setNodeType( ProtobufMessages.NodeMemory.NodeType.FROM )
                    .setFrom( _from.build() )
                    .build();
        }
        return null;
    }

    private static ProtobufMessages.NodeMemory writeQueryElementNodeMemory(final int nodeId,
                                                                           final Memory memory,
                                                                           final InternalWorkingMemory wm) {
        LeftTupleIterator it = LeftTupleIterator.iterator( wm, ((QueryElementNodeMemory)memory).node );
        
        ProtobufMessages.NodeMemory.QueryElementNodeMemory.Builder _query = ProtobufMessages.NodeMemory.QueryElementNodeMemory.newBuilder();
        for ( LeftTuple leftTuple =  ( LeftTuple ) it.next(); leftTuple != null; leftTuple =  ( LeftTuple ) it.next() ) {
            InternalFactHandle handle = (InternalFactHandle) leftTuple.getObject();
            FactHandle _handle = ProtobufMessages.FactHandle.newBuilder()
                    .setId( handle.getId() )
                    .setRecency( handle.getRecency() )
                    .build();
            
            ProtobufMessages.NodeMemory.QueryElementNodeMemory.QueryContext.Builder _context = ProtobufMessages.NodeMemory.QueryElementNodeMemory.QueryContext.newBuilder()
                    .setTuple( PersisterHelper.createTuple( leftTuple ) )
                    .setHandle( _handle );
            
            LeftTuple childLeftTuple = leftTuple.getFirstChild();
            while ( childLeftTuple != null ) {
                RightTuple rightParent = childLeftTuple.getRightParent();
                _context.addResult( ProtobufMessages.FactHandle.newBuilder()
                    .setId( rightParent.getFactHandle().getId() )
                    .setRecency( rightParent.getFactHandle().getRecency() )
                    .build() );
                while ( childLeftTuple != null && childLeftTuple.getRightParent() == rightParent ) {
                    // skip to the next child that has a different right parent
                    childLeftTuple = childLeftTuple.getLeftParentNext();
                }
            }
            _query.addContext( _context.build() ); 
        }
        
        return _query.getContextCount() > 0 ?
            ProtobufMessages.NodeMemory.newBuilder()
                .setNodeId( nodeId )
                .setNodeType( ProtobufMessages.NodeMemory.NodeType.QUERY_ELEMENT )
                .setQueryElement( _query.build() )
                .build()
            : null;
    }

    private static class AgendaGroupSorter
            implements
            Comparator<AgendaGroup> {
        public static final AgendaGroupSorter instance = new AgendaGroupSorter();

        public int compare(AgendaGroup group1,
                           AgendaGroup group2) {
            return group1.getName().compareTo( group2.getName() );
        }
    }

    private static class RuleFlowGroupSorter
            implements
            Comparator<RuleFlowGroup> {
        public static final RuleFlowGroupSorter instance = new RuleFlowGroupSorter();

        public int compare(RuleFlowGroup group1,
                           RuleFlowGroup group2) {
            return group1.getName().compareTo( group2.getName() );
        }
    }

    public static void writeActionQueue(MarshallerWriteContext context, 
                                        ProtobufMessages.RuleData.Builder _session) throws IOException {
        
        ReteooWorkingMemory wm = (ReteooWorkingMemory) context.wm;
        if( ! wm.getActionQueue().isEmpty() ) {
            ProtobufMessages.ActionQueue.Builder _queue = ProtobufMessages.ActionQueue.newBuilder();

            WorkingMemoryAction[] queue = wm.getActionQueue().toArray( new WorkingMemoryAction[wm.getActionQueue().size()] );
            for ( int i = queue.length - 1; i >= 0; i-- ) {
                _queue.addAction( queue[i].serialize( context ) );
            }
            _session.setActionQueue( _queue.build() );
        }
    }
    

    public static void writeTruthMaintenanceSystem(MarshallerWriteContext context, 
                                                   ProtobufMessages.RuleData.Builder _session) throws IOException {
        ObjectHashMap assertMap = context.wm.getTruthMaintenanceSystem().getAssertMap();
        ObjectHashMap justifiedMap = context.wm.getTruthMaintenanceSystem().getJustifiedMap();
        
        if( !assertMap.isEmpty() || !justifiedMap.isEmpty() ) {
            EqualityKey[] keys = new EqualityKey[assertMap.size()];
            org.drools.core.util.Iterator it = assertMap.iterator();
            int i = 0;
            for ( org.drools.core.util.ObjectHashMap.ObjectEntry entry = (org.drools.core.util.ObjectHashMap.ObjectEntry) it.next(); entry != null; entry = (org.drools.core.util.ObjectHashMap.ObjectEntry) it.next() ) {
                EqualityKey key = (EqualityKey) entry.getKey();
                keys[i++] = key;
            }

            Arrays.sort( keys,
                         EqualityKeySorter.instance );
            
            ProtobufMessages.TruthMaintenanceSystem.Builder _tms = ProtobufMessages.TruthMaintenanceSystem.newBuilder();

            // write the assert map of Equality keys
            for ( EqualityKey key : keys ) {
                ProtobufMessages.EqualityKey.Builder _key = ProtobufMessages.EqualityKey.newBuilder();
                _key.setStatus( key.getStatus() );
                _key.setHandleId( key.getFactHandle().getId() );
                if ( key.getOtherFactHandle() != null && !key.getOtherFactHandle().isEmpty() ) {
                    for ( InternalFactHandle handle : key.getOtherFactHandle() ) {
                        _key.addOtherHandle( handle.getId() );
                    }
                }
                _tms.addKey( _key.build() );
            }
            
            it = justifiedMap.iterator();
            i = 0;
            for ( org.drools.core.util.ObjectHashMap.ObjectEntry entry = (org.drools.core.util.ObjectHashMap.ObjectEntry) it.next(); entry != null; entry = (org.drools.core.util.ObjectHashMap.ObjectEntry) it.next() ) {
                ProtobufMessages.Justification.Builder _justification = ProtobufMessages.Justification.newBuilder();
                _justification.setHandleId( ((Integer) entry.getKey()).intValue() );
                
                org.drools.core.util.LinkedList list = (org.drools.core.util.LinkedList) entry.getValue();
                for ( LinkedListEntry node = (LinkedListEntry) list.getFirst(); node != null; node =  (LinkedListEntry) node.getNext() ) {
                    LogicalDependency dependency = (LogicalDependency) node.getObject();
                    org.drools.spi.Activation activation = dependency.getJustifier();
                    ProtobufMessages.Activation _activation = ProtobufMessages.Activation.newBuilder()
                            .setPackageName( activation.getRule().getPackage() )
                            .setRuleName( activation.getRule().getName() )
                            .setTuple( PersisterHelper.createTuple( activation.getTuple() ) )
                            .build();
                    _justification.addActivation( _activation );
                }
                _tms.addJustification( _justification.build() );
            }
            _session.setTms( _tms.build() );
        }
    }

    public static class EqualityKeySorter
                        implements Comparator<EqualityKey> {
        public static final EqualityKeySorter instance = new EqualityKeySorter();

        public int compare(EqualityKey key1,
                           EqualityKey key2) {
            return key1.getFactHandle().getId() - key2.getFactHandle().getId();
        }
    }

    private static void writeFactHandles(MarshallerWriteContext context,
                                         org.drools.marshalling.impl.ProtobufMessages.EntryPoint.Builder _epb,
                                         ObjectStore objectStore) throws IOException {
        ObjectMarshallingStrategyStore objectMarshallingStrategyStore = context.objectMarshallingStrategyStore;

        // Write out FactHandles
        for ( InternalFactHandle handle : orderFacts( objectStore ) ) {
            ProtobufMessages.FactHandle _handle = writeFactHandle( context,
                                                                   objectMarshallingStrategyStore,
                                                                   handle );

            _epb.addHandle( _handle );
        }
    }

    private static ProtobufMessages.FactHandle writeFactHandle(MarshallerWriteContext context,
                                                               ObjectMarshallingStrategyStore objectMarshallingStrategyStore,
                                                               InternalFactHandle handle) throws IOException {
        ProtobufMessages.FactHandle.Builder _handle = ProtobufMessages.FactHandle.newBuilder();

        _handle.setType( getHandleType( handle ) );
        _handle.setId( handle.getId() );
        _handle.setRecency( handle.getRecency() );

        if ( _handle.getType() == ProtobufMessages.FactHandle.HandleType.EVENT ) {
            // is event
            EventFactHandle efh = (EventFactHandle) handle;
            _handle.setTimestamp( efh.getStartTimestamp() );
            _handle.setDuration( efh.getDuration() );
            _handle.setIsExpired( efh.isExpired() );
            _handle.setActivationsCount( efh.getActivationsCount() );
        }

        Object object = handle.getObject();

        if ( object != null ) {
            ObjectMarshallingStrategy strategy = objectMarshallingStrategyStore.getStrategyObject( object );

            Integer index = context.getStrategyIndex( strategy );
            _handle.setStrategyIndex( index.intValue() );
            _handle.setObject( ByteString.copyFrom( strategy.marshal( context.strategyContext.get( strategy ),
                                                                      context,
                                                                      object ) ) );
        }

        return _handle.build();
    }

    private static ProtobufMessages.FactHandle.HandleType getHandleType(InternalFactHandle handle) {
        if ( handle instanceof EventFactHandle ) {
            return ProtobufMessages.FactHandle.HandleType.EVENT;
        } else if ( handle instanceof QueryElementFactHandle ) {
            return ProtobufMessages.FactHandle.HandleType.QUERY;
        } else if ( handle.getObject() instanceof InitialFact ) {
            return ProtobufMessages.FactHandle.HandleType.INITIAL_FACT;
        }
        return ProtobufMessages.FactHandle.HandleType.FACT;
    }

    public static InternalFactHandle[] orderFacts(ObjectStore objectStore) {
        // this method is just needed for testing purposes, to allow round tripping
        int size = objectStore.size();
        InternalFactHandle[] handles = new InternalFactHandle[size];
        int i = 0;
        for ( Iterator< ? > it = objectStore.iterateFactHandles(); it.hasNext(); ) {
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

    public static class ActivationsSorter
            implements Comparator<org.drools.spi.Activation> {
        public static final ActivationsSorter INSTANCE = new ActivationsSorter();

        public int compare(org.drools.spi.Activation o1,
                           org.drools.spi.Activation o2) {
            int result = o1.getRule().getName().compareTo( o2.getRule().getName() );
            if( result == 0 ) {
                LeftTuple t1 = o1.getTuple();
                LeftTuple t2 = o2.getTuple();
                while( result == 0 && t1 != null && t2 != null ) {
                    result = t1.getLastHandle().getId() - t2.getLastHandle().getId();
                    t1 = t1.getParent();
                    t2 = t2.getParent();
                }
            }
            return result;
        }
    }

    public static ProtobufMessages.Activation writeActivation(MarshallerWriteContext context,
                                                              AgendaItem agendaItem ) {
        ProtobufMessages.Activation.Builder _activation = ProtobufMessages.Activation.newBuilder();
        
        Rule rule = agendaItem.getRule();
        _activation.setPackageName( rule.getPackage() );
        _activation.setRuleName( rule.getName() );

        ProtobufMessages.Tuple.Builder _tb = ProtobufMessages.Tuple.newBuilder();
        for( LeftTuple entry = agendaItem.getTuple(); entry != null; entry = entry.getParent() ) {
            InternalFactHandle handle = entry.getLastHandle();
            _tb.addHandleId( handle.getId() );
        }
        _activation.setTuple( _tb.build() );
        
        _activation.setSalience( agendaItem.getSalience() );
        _activation.setIsActivated( agendaItem.isActivated() );

        if ( agendaItem.getActivationGroupNode() != null ) {
            _activation.setActivationGroup( agendaItem.getActivationGroupNode().getActivationGroup().getName() );
        }

        if ( agendaItem.getFactHandle() != null ) {
            _activation.setHandleId( agendaItem.getFactHandle().getId() );
        }

        org.drools.core.util.LinkedList<LogicalDependency> list = agendaItem.getLogicalDependencies();
        if ( list != null && !list.isEmpty() ) {
            for ( LogicalDependency node = list.getFirst(); node != null; node = node.getNext() ) {
                _activation.addLogicalDependency( ((InternalFactHandle) node.getJustified()).getId() );
            }
        }
        return _activation.build();
    }

    private static ProtobufMessages.Timers writeTimers(Collection<TimerJobInstance> timers,
                                                       MarshallerWriteContext outCtx) {
        if( ! timers.isEmpty() ) {
            List<TimerJobInstance> sortedTimers = new ArrayList<TimerJobInstance>( timers );
            Collections.sort( sortedTimers,
                              new Comparator<TimerJobInstance>() {
                public int compare(TimerJobInstance o1,
                                   TimerJobInstance o2) {
                    return (int) (o1.getJobHandle().getId() - o2.getJobHandle().getId());
                }
            } );
            
            ProtobufMessages.Timers.Builder _timers = ProtobufMessages.Timers.newBuilder();
            for ( TimerJobInstance timer : sortedTimers ) {
                JobContext jctx = ((SelfRemovalJobContext) timer.getJobContext()).getJobContext();
                TimersOutputMarshaller writer = outCtx.writersByClass.get( jctx.getClass() );
                Timer _timer = writer.serialize( jctx, outCtx );
                _timers.addTimer( _timer );
            }
            return _timers.build();
        }
        return null;
    }

    public static ProtobufMessages.Trigger writeTrigger(Trigger trigger,
                                                        MarshallerWriteContext outCtx) {
        if ( trigger instanceof CronTrigger ) {
            CronTrigger cronTrigger = (CronTrigger) trigger;
            ProtobufMessages.Trigger.CronTrigger.Builder _cron = ProtobufMessages.Trigger.CronTrigger.newBuilder() 
                      .setStartTime( cronTrigger.getStartTime().getTime() )
                      .setRepeatLimit( cronTrigger.getRepeatLimit() )
                      .setRepeatCount( cronTrigger.getRepeatCount() )
                      .setCronExpression( cronTrigger.getCronEx().getCronExpression() );
            if ( cronTrigger.getEndTime() != null ) {
                _cron.setEndTime( cronTrigger.getEndTime().getTime() );
            }
            if ( cronTrigger.getNextFireTime() != null ) {
                _cron.setNextFireTime( cronTrigger.getNextFireTime().getTime() );
            }
            if( cronTrigger.getCalendarNames() != null ) {
                for( String calendarName : cronTrigger.getCalendarNames() ) {
                    _cron.addCalendarName( calendarName );
                }
            }
            return ProtobufMessages.Trigger.newBuilder()
                    .setType( ProtobufMessages.Trigger.TriggerType.CRON )
                    .setCron( _cron.build() )
                    .build(); 
        } else if ( trigger instanceof IntervalTrigger ) {
            IntervalTrigger intTrigger = (IntervalTrigger) trigger;
            ProtobufMessages.Trigger.IntervalTrigger.Builder _interval = ProtobufMessages.Trigger.IntervalTrigger.newBuilder() 
                    .setStartTime( intTrigger.getStartTime().getTime() )
                    .setRepeatLimit( intTrigger.getRepeatLimit() )
                    .setRepeatCount( intTrigger.getRepeatCount() )
                    .setPeriod( intTrigger.getPeriod() );
            if ( intTrigger.getEndTime() != null ) {
                _interval.setEndTime( intTrigger.getEndTime().getTime() );
            }
            if ( intTrigger.getNextFireTime() != null ) {
                _interval.setNextFireTime( intTrigger.getNextFireTime().getTime() );
            }
            if( intTrigger.getCalendarNames() != null ) {
                for( String calendarName : intTrigger.getCalendarNames() ) {
                    _interval.addCalendarName( calendarName );
                }
            }
            return ProtobufMessages.Trigger.newBuilder()
                    .setType( ProtobufMessages.Trigger.TriggerType.INTERVAL )
                    .setInterval( _interval.build() )
                    .build(); 
        } else if ( trigger instanceof PointInTimeTrigger ) {
            PointInTimeTrigger pinTrigger = (PointInTimeTrigger) trigger;
            return ProtobufMessages.Trigger.newBuilder()
                    .setType( ProtobufMessages.Trigger.TriggerType.POINT_IN_TIME )
                    .setPit( ProtobufMessages.Trigger.PointInTimeTrigger.newBuilder()
                             .setNextFireTime( pinTrigger.hasNextFireTime().getTime() )
                             .build() )
                    .build(); 
        }
        throw new RuntimeException( "Unable to serialize Trigger for type: " + trigger.getClass() );
    }

}
