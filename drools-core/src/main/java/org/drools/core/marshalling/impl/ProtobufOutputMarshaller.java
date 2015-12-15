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

package org.drools.core.marshalling.impl;

import com.google.protobuf.ByteString;
import org.drools.core.InitialFact;
import org.drools.core.WorkingMemoryEntryPoint;
import org.drools.core.beliefsystem.BeliefSet;
import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.common.ActivationIterator;
import org.drools.core.common.AgendaGroupQueueImpl;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.BaseNode;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.EqualityKey;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.LeftTupleIterator;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.Memory;
import org.drools.core.common.NamedEntryPoint;
import org.drools.core.common.NodeMemories;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.ObjectTypeConfigurationRegistry;
import org.drools.core.common.QueryElementFactHandle;
import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.drools.core.marshalling.impl.ProtobufMessages.FactHandle;
import org.drools.core.marshalling.impl.ProtobufMessages.ObjectTypeConfiguration;
import org.drools.core.marshalling.impl.ProtobufMessages.ProcessData.Builder;
import org.drools.core.marshalling.impl.ProtobufMessages.Timers;
import org.drools.core.marshalling.impl.ProtobufMessages.Timers.Timer;
import org.drools.core.marshalling.impl.ProtobufMessages.Tuple;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.reteoo.AccumulateNode.AccumulateContext;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.BetaNode;
import org.drools.core.reteoo.FromNode.FromMemory;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.NodeTypeEnums;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.core.reteoo.QueryElementNode.QueryElementNodeMemory;
import org.drools.core.reteoo.RightInputAdapterNode;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.RuleFlowGroup;
import org.drools.core.time.JobContext;
import org.drools.core.time.SelfRemovalJobContext;
import org.drools.core.time.Trigger;
import org.drools.core.time.impl.CompositeMaxDurationTrigger;
import org.drools.core.time.impl.CronTrigger;
import org.drools.core.time.impl.IntervalTrigger;
import org.drools.core.time.impl.PointInTimeTrigger;
import org.drools.core.time.impl.PseudoClockScheduler;
import org.drools.core.time.impl.TimerJobInstance;
import org.drools.core.util.FastIterator;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.ObjectHashMap;
import org.kie.api.marshalling.ObjectMarshallingStrategy;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.rule.EntryPoint;

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
        
//        System.out.println("=============================================================================");
//        System.out.println(_session);

        PersisterHelper.writeToStreamWithHeader( context,
                                                 _session );
    }

    private static ProtobufMessages.KnowledgeSession serializeSession(MarshallerWriteContext context) throws IOException {
        StatefulKnowledgeSessionImpl wm = (StatefulKnowledgeSessionImpl) context.wm;

        try {
            wm.getLock().lock();
            for (WorkingMemoryEntryPoint ep : wm.getWorkingMemoryEntryPoints().values()) {
                if (ep instanceof NamedEntryPoint) {
                    ((NamedEntryPoint)ep).lock();
                }
            }

            wm.getAgenda().unstageActivations();

            evaluateRuleActivations( wm );

            ProtobufMessages.RuleData.Builder _ruleData = ProtobufMessages.RuleData.newBuilder();

            long time = 0;
            if ( context.wm.getTimerService() instanceof PseudoClockScheduler ) {
                time = context.clockTime;
            }
            _ruleData.setLastId( wm.getFactHandleFactory().getId() );
            _ruleData.setLastRecency( wm.getFactHandleFactory().getRecency() );

            InternalFactHandle handle = context.wm.getInitialFactHandle();
            if ( handle != null ) {
                // can be null for RETE, if fireAllRules has not yet been called
                ProtobufMessages.FactHandle _ifh = ProtobufMessages.FactHandle.newBuilder()
                        .setType( ProtobufMessages.FactHandle.HandleType.INITIAL_FACT )
                        .setId( handle.getId() )
                        .setRecency( handle.getRecency() )
                        .build();
                _ruleData.setInitialFact( _ifh );
            }

            writeAgenda( context, _ruleData );

            writeNodeMemories( context, _ruleData );

            for ( EntryPoint wmep : wm.getWorkingMemoryEntryPoints().values() ) {
                org.drools.core.marshalling.impl.ProtobufMessages.EntryPoint.Builder _epb = ProtobufMessages.EntryPoint.newBuilder();
                _epb.setEntryPointId( wmep.getEntryPointId() );

                writeObjectTypeConfiguration( context,
                                              ((InternalWorkingMemoryEntryPoint)wmep).getObjectTypeConfigurationRegistry(),
                                              _epb );

                writeFactHandles( context,
                                  _epb,
                                  ((NamedEntryPoint) wmep).getObjectStore() );

                writeTruthMaintenanceSystem( context,
                                             wmep,
                                             _epb );

                _ruleData.addEntryPoint( _epb.build() );
            }

            writeActionQueue( context,
                              _ruleData );

            ProtobufMessages.KnowledgeSession.Builder _session = ProtobufMessages.KnowledgeSession.newBuilder()
                    .setMultithread( false )
                    .setTime( time )
                    .setRuleData( _ruleData.build() );

            if ( processMarshaller != null ) {
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

            Timers _timers = writeTimers( context.wm.getTimerService().getTimerJobInstances( context.wm.getId() ),
                                          context );
            if ( _timers != null ) {
                _session.setTimers( _timers );
            }

            return _session.build();
        } finally {
            for (WorkingMemoryEntryPoint ep : wm.getWorkingMemoryEntryPoints().values()) {
                if (ep instanceof NamedEntryPoint) {
                    ((NamedEntryPoint)ep).unlock();
                }
            }
            wm.getLock().unlock();
        }
    }

    private static void writeObjectTypeConfiguration( MarshallerWriteContext context, 
    		                                          ObjectTypeConfigurationRegistry otcr,
    		                                          org.drools.core.marshalling.impl.ProtobufMessages.EntryPoint.Builder _epb) {
        
        Collection<ObjectTypeConf> values = otcr.values();
    	ObjectTypeConf[] otcs = values.toArray( new ObjectTypeConf[ values.size() ] );
    	Arrays.sort( otcs,
    	        new Comparator<ObjectTypeConf>() {
                    @Override
                    public int compare(ObjectTypeConf o1, ObjectTypeConf o2) {
                        return o1.getTypeName().compareTo(o2.getTypeName());
                    }
    	});
        for( ObjectTypeConf otc : otcs ) {
            ObjectTypeNode objectTypeNode = otc.getConcreteObjectTypeNode();
            if (objectTypeNode != null) {
                final ObjectTypeNodeMemory memory = context.wm.getNodeMemory(objectTypeNode);
                if (memory != null) {
                    ObjectTypeConfiguration _otc = ObjectTypeConfiguration.newBuilder()
                                                                          .setType(otc.getTypeName())
                                                                          .setTmsEnabled(otc.isTMSEnabled())
                                                                          .build();
                    _epb.addOtc(_otc);
                }
            }
    	}
	}

	private static void evaluateRuleActivations(StatefulKnowledgeSessionImpl wm) {
        // ET: NOTE: initially we were only resolving partially evaluated rules
        // but some tests fail because of that. Have to resolve all rule agenda items
        // in order to fix the tests
        
        // find all partially evaluated rule activations
//        ActivationIterator it = ActivationIterator.iterator( wm );
//        Set<String> evaluated = new HashSet<String>();
//        for ( org.drools.core.spi.Activation item = (org.drools.core.spi.Activation) it.next(); item != null; item = (org.drools.core.spi.Activation) it.next() ) {
//            if ( !item.isRuleAgendaItem() ) {
//                evaluated.add( item.getRule().getPackageName()+"."+item.getRule().getName() );
//            }
//        }
        // need to evaluate all lazy partially evaluated activations before serializing
        boolean dirty = true;
        while ( dirty) {
            for ( Activation activation : wm.getAgenda().getActivations() ) {
                if ( activation.isRuleAgendaItem() /*&& evaluated.contains( activation.getRule().getPackageName()+"."+activation.getRule().getName() )*/ ) {
                    // evaluate it
                    ((RuleAgendaItem)activation).getRuleExecutor().reEvaluateNetwork( wm );
                    ((RuleAgendaItem)activation).getRuleExecutor().removeRuleAgendaItemWhenEmpty( wm );
                }
            }
            dirty = false;
            if ( wm.getKnowledgeBase().getConfiguration().isPhreakEnabled() ) {
                // network evaluation with phreak and TMS may make previous processed rules dirty again, so need to reprocess until all is flushed.
                for ( Activation activation : wm.getAgenda().getActivations() ) {
                    if ( activation.isRuleAgendaItem() && ((RuleAgendaItem)activation).getRuleExecutor().isDirty() ) {
                        dirty = true;
                        break;
                    }
                }
            }
            wm.flushNonMarshallablePropagations();
        }
    }

    private static void writeAgenda(MarshallerWriteContext context,
                                    ProtobufMessages.RuleData.Builder _ksb) throws IOException {
        InternalWorkingMemory wm = context.wm;
        InternalAgenda agenda = wm.getAgenda();

        org.drools.core.marshalling.impl.ProtobufMessages.Agenda.Builder _ab = ProtobufMessages.Agenda.newBuilder();

        AgendaGroup[] agendaGroups = agenda.getAgendaGroupsMap().values().toArray( new AgendaGroup[agenda.getAgendaGroupsMap().size()] );
        Arrays.sort( agendaGroups,
                     AgendaGroupSorter.instance );
        for ( AgendaGroup ag : agendaGroups ) {
            AgendaGroupQueueImpl group = (AgendaGroupQueueImpl) ag;
            org.drools.core.marshalling.impl.ProtobufMessages.Agenda.AgendaGroup.Builder _agb = ProtobufMessages.Agenda.AgendaGroup.newBuilder();
            _agb.setName( group.getName() )
                    .setIsActive( group.isActive() )
                    .setIsAutoDeactivate( group.isAutoDeactivate() )
                    .setClearedForRecency( group.getClearedForRecency() )
                    .setHasRuleFlowLister( group.isRuleFlowListener() )
                    .setActivatedForRecency( group.getActivatedForRecency() );

            Map<Long, String> nodeInstances = group.getNodeInstances();
            for ( Map.Entry<Long, String> entry : nodeInstances.entrySet() ) {
                org.drools.core.marshalling.impl.ProtobufMessages.Agenda.AgendaGroup.NodeInstance.Builder _nib = ProtobufMessages.Agenda.AgendaGroup.NodeInstance.newBuilder();
                _nib.setProcessInstanceId( entry.getKey() );
                _nib.setNodeInstanceId( entry.getValue() );
                _agb.addNodeInstance( _nib.build() );
            }

            _ab.addAgendaGroup( _agb.build() );

        }

        org.drools.core.marshalling.impl.ProtobufMessages.Agenda.FocusStack.Builder _fsb = ProtobufMessages.Agenda.FocusStack.newBuilder();
        LinkedList<AgendaGroup> focusStack = agenda.getStackList();
        for ( AgendaGroup group : focusStack ) {
            _fsb.addGroupName( group.getName() );
        }
        _ab.setFocusStack( _fsb.build() );

        // serialize all dormant activations
        org.drools.core.util.Iterator it = ActivationIterator.iterator( wm );
        List<org.drools.core.spi.Activation> dormant = new ArrayList<org.drools.core.spi.Activation>();
        for ( org.drools.core.spi.Activation item = (org.drools.core.spi.Activation) it.next(); item != null; item = (org.drools.core.spi.Activation) it.next() ) {
            if ( !item.isQueued() ) {
                dormant.add( item );
            }
        }

        Collections.sort( dormant, ActivationsSorter.INSTANCE );
        for ( org.drools.core.spi.Activation activation : dormant ) {
            _ab.addMatch( writeActivation( context, (AgendaItem) activation ) );
        }

        // serialize all network evaluator activations
        for ( Activation activation : agenda.getActivations() ) {
            if ( activation.isRuleAgendaItem() ) {
                // serialize it
                _ab.addRuleActivation( writeActivation( context, (AgendaItem) activation ) );
            }
        }

        _ksb.setAgenda( _ab.build() );
    }

    private static void writeNodeMemories(MarshallerWriteContext context,
                                          ProtobufMessages.RuleData.Builder _ksb) throws IOException {
        InternalWorkingMemory wm = context.wm;
        NodeMemories memories = wm.getNodeMemories();
        // only some of the node memories require special serialization handling
        // so we iterate over all of them and process only those that require it
        for ( int i = 0; i < memories.length(); i++ ) {
            Memory memory = memories.peekNodeMemory( i );
            // some nodes have no memory, so we need to check for nulls
            if ( memory != null ) {
                ProtobufMessages.NodeMemory _node = null;
                switch ( memory.getNodeType() ) {
                    case NodeTypeEnums.AccumulateNode : {
                        _node = writeAccumulateNodeMemory( i, memory );
                        break;
                    }
                    case NodeTypeEnums.RightInputAdaterNode : {

                        _node = writeRIANodeMemory( i, context.sinks.get(i), memories );
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
                if ( _node != null ) {
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
        if ( accmem.getBetaMemory().getLeftTupleMemory().size() > 0 ) {
            ProtobufMessages.NodeMemory.AccumulateNodeMemory.Builder _accumulate = ProtobufMessages.NodeMemory.AccumulateNodeMemory.newBuilder();

            final org.drools.core.util.Iterator<LeftTuple> tupleIter = accmem.getBetaMemory().getLeftTupleMemory().iterator();
            for ( LeftTuple leftTuple = tupleIter.next(); leftTuple != null; leftTuple = tupleIter.next() ) {
                AccumulateContext accctx = (AccumulateContext) leftTuple.getContextObject();
                if ( accctx.getResultFactHandle() != null ) {
                    FactHandle _handle = ProtobufMessages.FactHandle.newBuilder()
                            .setId( accctx.getResultFactHandle().getId() )
                            .setRecency( accctx.getResultFactHandle().getRecency() )
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
                                                                  final BaseNode node,
                                                                  final NodeMemories memories) {
        RightInputAdapterNode riaNode = (RightInputAdapterNode) node;

        ObjectSink[] sinks = riaNode.getSinkPropagator().getSinks();
        BetaNode betaNode = (BetaNode) sinks[0];

        Memory betaMemory = memories.peekNodeMemory( betaNode.getId() );
        if ( betaMemory == null ) {
            return null;
        }
        BetaMemory bm;
        if ( betaNode.getType() == NodeTypeEnums.AccumulateNode ) {
            bm =  ((AccumulateMemory) betaMemory).getBetaMemory();
        } else {
            bm =  (BetaMemory) betaMemory;
        }

        // for RIA nodes, we need to store the ID of the created handles
        bm.getRightTupleMemory().iterator();
        if ( bm.getRightTupleMemory().size() > 0 ) {
            ProtobufMessages.NodeMemory.RIANodeMemory.Builder _ria = ProtobufMessages.NodeMemory.RIANodeMemory.newBuilder();
            final org.drools.core.util.Iterator it = bm.getRightTupleMemory().iterator();

            // iterates over all propagated handles and assert them to the new sink
            for ( RightTuple entry = (RightTuple) it.next(); entry != null; entry = (RightTuple) it.next() ) {
                LeftTuple leftTuple = (LeftTuple) entry.getFactHandle().getObject();
                InternalFactHandle handle = (InternalFactHandle) leftTuple.getContextObject();
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

        if ( fromMemory.getBetaMemory().getLeftTupleMemory().size() > 0 ) {
            ProtobufMessages.NodeMemory.FromNodeMemory.Builder _from = ProtobufMessages.NodeMemory.FromNodeMemory.newBuilder();

            final org.drools.core.util.Iterator<LeftTuple> tupleIter = fromMemory.getBetaMemory().getLeftTupleMemory().iterator();
            for ( LeftTuple leftTuple = tupleIter.next(); leftTuple != null; leftTuple = tupleIter.next() ) {
                Map<Object, RightTuple> matches = (Map<Object, RightTuple>) leftTuple.getContextObject();
                ProtobufMessages.NodeMemory.FromNodeMemory.FromContext.Builder _context = ProtobufMessages.NodeMemory.FromNodeMemory.FromContext.newBuilder()
                        .setTuple( PersisterHelper.createTuple( leftTuple ) );
                for ( RightTuple rightTuple : matches.values() ) {
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
        org.drools.core.util.Iterator<LeftTuple> it = LeftTupleIterator.iterator( wm, ((QueryElementNodeMemory) memory).getNode() );

        ProtobufMessages.NodeMemory.QueryElementNodeMemory.Builder _query = ProtobufMessages.NodeMemory.QueryElementNodeMemory.newBuilder();
        for ( LeftTuple leftTuple = it.next(); leftTuple != null; leftTuple = it.next() ) {
            InternalFactHandle handle = (InternalFactHandle) leftTuple.getContextObject();
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
                    childLeftTuple = childLeftTuple.getHandleNext();
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

        if ( context.wm.hasPendingPropagations() ) {
            ProtobufMessages.ActionQueue.Builder _queue = ProtobufMessages.ActionQueue.newBuilder();

            Iterator<? extends PropagationEntry> i = context.wm.getActionsIterator();
            while ( i.hasNext() ) {
                PropagationEntry entry = i.next();
                if (entry instanceof WorkingMemoryAction) {
                    _queue.addAction(((WorkingMemoryAction) entry).serialize(context));
                }
            }
            _session.setActionQueue( _queue.build() );
        }
    }

    public static void writeTruthMaintenanceSystem(MarshallerWriteContext context,
                                                   EntryPoint wmep,
                                                   ProtobufMessages.EntryPoint.Builder _epb) throws IOException {
        TruthMaintenanceSystem tms = ((NamedEntryPoint) wmep).getTruthMaintenanceSystem();
        ObjectHashMap justifiedMap = tms.getEqualityKeyMap();

        if ( !justifiedMap.isEmpty() ) {
            EqualityKey[] keys = new EqualityKey[justifiedMap.size()];
            org.drools.core.util.Iterator it = justifiedMap.iterator();
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

                if ( key.size() > 1 ) {
                    // add all the other key's if they exist
                    FastIterator keyIter = key.fastIterator();
                    for ( DefaultFactHandle handle = key.getFirst().getNext(); handle != null; handle = (DefaultFactHandle) keyIter.next( handle ) ) {
                        _key.addOtherHandle( handle.getId() );
                    }
                }

                if ( key.getBeliefSet() != null ) {
                    writeBeliefSet( context, key.getBeliefSet(), _key );
                }

                _tms.addKey( _key.build() );
            }

            _epb.setTms( _tms.build() );
        }
    }

    private static void writeBeliefSet(MarshallerWriteContext context,
                                       BeliefSet beliefSet,
                                       org.drools.core.marshalling.impl.ProtobufMessages.EqualityKey.Builder _key) throws IOException {

        ProtobufMessages.BeliefSet.Builder _beliefSet = ProtobufMessages.BeliefSet.newBuilder();
        _beliefSet.setHandleId( beliefSet.getFactHandle().getId() );

        ObjectMarshallingStrategyStore objectMarshallingStrategyStore = context.objectMarshallingStrategyStore;

        // for ( LinkedListEntry node = (LinkedListEntry) beliefSet.getFirst(); node != null; node = (LinkedListEntry) node.getNext() ) {
        FastIterator it =  beliefSet.iterator();
        for ( LinkedListEntry node = (LinkedListEntry) beliefSet.getFirst(); node != null; node = (LinkedListEntry) it.next(node) ) {
            LogicalDependency belief = (LogicalDependency) node.getObject();
            ProtobufMessages.LogicalDependency.Builder _logicalDependency = ProtobufMessages.LogicalDependency.newBuilder();
            //_belief.setActivation( value )

            LogicalDependency dependency = (LogicalDependency) node.getObject();
            org.drools.core.spi.Activation activation = dependency.getJustifier();
            ProtobufMessages.Activation _activation = ProtobufMessages.Activation.newBuilder()
                    .setPackageName( activation.getRule().getPackage() )
                    .setRuleName( activation.getRule().getName() )
                    .setTuple( PersisterHelper.createTuple( activation.getTuple() ) )
                    .build();
            _logicalDependency.setActivation( _activation );

            if ( belief.getObject() != null ) {
                ObjectMarshallingStrategy strategy = objectMarshallingStrategyStore.getStrategyObject( belief.getObject() );

                Integer index = context.getStrategyIndex( strategy );
                _logicalDependency.setObjectStrategyIndex( index );
                _logicalDependency.setObject( ByteString.copyFrom( strategy.marshal( context.strategyContext.get( strategy ),
                                                                                     context,
                                                                                     belief.getObject() ) ) );
            }

            if ( belief.getMode() != null ) {
                ObjectMarshallingStrategy strategy = objectMarshallingStrategyStore.getStrategyObject( belief.getMode() );

                Integer index = context.getStrategyIndex( strategy );
                _logicalDependency.setValueStrategyIndex( index );
                _logicalDependency.setValue( ByteString.copyFrom( strategy.marshal( context.strategyContext.get( strategy ),
                                                                                    context,
                                                                                    belief.getMode() ) ) );
            }
            _beliefSet.addLogicalDependency( _logicalDependency.build() );
        }
        _key.setBeliefSet( _beliefSet );
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

    private static void writeFactHandles(MarshallerWriteContext context,
                                         org.drools.core.marshalling.impl.ProtobufMessages.EntryPoint.Builder _epb,
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

        if ( handle.getEqualityKey() != null &&
             handle.getEqualityKey().getStatus() == EqualityKey.JUSTIFIED ) {
            _handle.setIsJustified( true );
        } else {
            _handle.setIsJustified( false );
        }

        Object object = handle.getObject();

        if ( object != null ) {
            ObjectMarshallingStrategy strategy = objectMarshallingStrategyStore.getStrategyObject( object );

            Integer index = context.getStrategyIndex( strategy );
            _handle.setStrategyIndex( index );
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
        for ( Iterator<InternalFactHandle> it = objectStore.iterateFactHandles(); it.hasNext(); ) {
            handles[i++] = it.next();
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
            implements
            Comparator<org.drools.core.spi.Activation> {
        public static final ActivationsSorter INSTANCE = new ActivationsSorter();

        public int compare(org.drools.core.spi.Activation o1,
                           org.drools.core.spi.Activation o2) {
            int result = o1.getRule().getName().compareTo( o2.getRule().getName() );
            if ( result == 0 ) {
                org.drools.core.spi.Tuple t1 = o1.getTuple();
                org.drools.core.spi.Tuple t2 = o2.getTuple();
                while ( result == 0 && t1 != null && t2 != null ) {
                    if ( t1.getFactHandle() != null && t2.getFactHandle() != null ) {
                        // can be null for eval, not and exists that have no right input
                        result = t1.getFactHandle().getId() - t2.getFactHandle().getId();
                    }
                    t1 = t1.getParent();
                    t2 = t2.getParent();
                }
            }
            return result;
        }
    }

    public static <M extends ModedAssertion<M>> ProtobufMessages.Activation writeActivation(MarshallerWriteContext context,
                                                                                            AgendaItem<M> agendaItem) {
        ProtobufMessages.Activation.Builder _activation = ProtobufMessages.Activation.newBuilder();

        RuleImpl rule = agendaItem.getRule();
        _activation.setPackageName( rule.getPackage() );
        _activation.setRuleName( rule.getName() );
        _activation.setTuple( writeTuple( agendaItem.getTuple() ) );
        _activation.setSalience( agendaItem.getSalience() );
        _activation.setIsActivated( agendaItem.isQueued() );
        _activation.setEvaluated( agendaItem.isRuleAgendaItem() );

        if ( agendaItem.getActivationGroupNode() != null ) {
            _activation.setActivationGroup( agendaItem.getActivationGroupNode().getActivationGroup().getName() );
        }

        if ( agendaItem.getActivationFactHandle() != null ) {
            _activation.setHandleId( agendaItem.getActivationFactHandle().getId() );
        }

        org.drools.core.util.LinkedList<LogicalDependency<M>> list = agendaItem.getLogicalDependencies();
        if ( list != null && !list.isEmpty() ) {
            for ( LogicalDependency<?> node = list.getFirst(); node != null; node = node.getNext() ) {
                _activation.addLogicalDependency( ((BeliefSet) node.getJustified()).getFactHandle().getId() );
            }
        }
        return _activation.build();
    }

    public static Tuple writeTuple(org.drools.core.spi.Tuple tuple) {
        ProtobufMessages.Tuple.Builder _tb = ProtobufMessages.Tuple.newBuilder();
        for ( org.drools.core.spi.Tuple entry = tuple; entry != null; entry = entry.getParent() ) {
            InternalFactHandle handle = entry.getFactHandle();
            if ( handle != null ) {
                 // can be null for eval, not and exists that have no right input
                _tb.addHandleId( handle.getId() );
            }
        }
        return _tb.build();
    }

    private static ProtobufMessages.Timers writeTimers(Collection<TimerJobInstance> timers,
                                                       MarshallerWriteContext outCtx) {
        if ( !timers.isEmpty() ) {
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
                if (jctx instanceof ObjectTypeNode.ExpireJobContext &&
                    !((ObjectTypeNode.ExpireJobContext) jctx).getExpireAction().getFactHandle().isValid()) {
                    continue;
                }
                TimersOutputMarshaller writer = outCtx.writersByClass.get( jctx.getClass() );
                Timer _timer = writer.serialize( jctx, outCtx );
                if ( _timer != null ) {
                    _timers.addTimer( _timer );
                }
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
            if ( cronTrigger.getCalendarNames() != null ) {
                for ( String calendarName : cronTrigger.getCalendarNames() ) {
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
            if ( intTrigger.getCalendarNames() != null ) {
                for ( String calendarName : intTrigger.getCalendarNames() ) {
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
        } else if ( trigger instanceof CompositeMaxDurationTrigger ) {
            CompositeMaxDurationTrigger cmdTrigger = (CompositeMaxDurationTrigger) trigger;
            ProtobufMessages.Trigger.CompositeMaxDurationTrigger.Builder _cmdt = ProtobufMessages.Trigger.CompositeMaxDurationTrigger.newBuilder();
            if ( cmdTrigger.getMaxDurationTimestamp() != null ) {
                _cmdt.setMaxDurationTimestamp( cmdTrigger.getMaxDurationTimestamp().getTime() );
            }
            if ( cmdTrigger.getTimerCurrentDate() != null ) {
                _cmdt.setTimerCurrentDate( cmdTrigger.getTimerCurrentDate().getTime() );
            }
            if ( cmdTrigger.getTimerTrigger() != null ) {
                _cmdt.setTimerTrigger( writeTrigger(cmdTrigger.getTimerTrigger(), outCtx) );
            }
            return ProtobufMessages.Trigger.newBuilder()
                                           .setType( ProtobufMessages.Trigger.TriggerType.COMPOSITE_MAX_DURATION )
                                           .setCmdt( _cmdt.build() )
                                           .build();
        }
        throw new RuntimeException( "Unable to serialize Trigger for type: " + trigger.getClass() );
    }

    public static void writeWorkItem( MarshallerWriteContext context, WorkItem workItem ) {
        processMarshaller.writeWorkItem( context, workItem );
    }

}
