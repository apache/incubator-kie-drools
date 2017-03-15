/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.core.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.stream.Stream;

import org.drools.core.common.BaseNode;
import org.drools.core.common.CompositeDefaultAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.reteoo.CompositeObjectSinkAdapter.FieldIndex;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.ObjectHashMap;

public class CompositePartitionAwareObjectSinkAdapter implements ObjectSinkPropagator {

    private final ObjectSinkPropagator[] partitionedPropagators = new ObjectSinkPropagator[RuleBasePartitionId.PARALLEL_PARTITIONS_NUMBER];

    private boolean hashed = true;
    private CompositeObjectSinkAdapter.FieldIndex fieldIndex;

    private ObjectHashMap hashedSinkMap;

    public CompositePartitionAwareObjectSinkAdapter() {
        Arrays.fill(partitionedPropagators, EmptyObjectSinkAdapter.getInstance());
    }

    public boolean isHashed() {
        return hashed;
    }

    @Override
    public ObjectSinkPropagator addObjectSink( ObjectSink sink, int alphaNodeHashingThreshold ) {
        hashed &= hashSink( sink );
        int partition = sink.getPartitionId().getParallelEvaluationSlot();
        partitionedPropagators[partition] = partitionedPropagators[partition].addObjectSink( sink, alphaNodeHashingThreshold );
        return this;
    }

    private boolean hashSink( ObjectSink sink ) {
        InternalReadAccessor readAccessor = getHashableAccessor( sink );
        if (readAccessor != null) {
            int index = readAccessor.getIndex();
            if ( fieldIndex == null ) {
                this.fieldIndex = new CompositeObjectSinkAdapter.FieldIndex( index, readAccessor );
                this.hashedSinkMap = new ObjectHashMap();
            }
            if (fieldIndex.getIndex() == index) {
                AlphaNode alpha = (AlphaNode)sink;
                this.hashedSinkMap.put( new CompositeObjectSinkAdapter.HashKey( index,
                                                                                ((IndexableConstraint)alpha.getConstraint()).getField(),
                                                                                fieldIndex.getFieldExtractor() ),
                                        alpha,
                                        false );
                return true;
            }
        }
        this.fieldIndex = null;
        this.hashedSinkMap = null;
        return false;
    }

    private InternalReadAccessor getHashableAccessor(ObjectSink sink) {
        if ( sink.getType() == NodeTypeEnums.AlphaNode ) {
            final AlphaNode alphaNode = (AlphaNode) sink;
            return CompositeObjectSinkAdapter.getHashableAccessor( alphaNode );
        }
        return null;
    }

    @Override
    public ObjectSinkPropagator removeObjectSink( ObjectSink sink ) {
        int partition = sink.getPartitionId().getParallelEvaluationSlot();
        partitionedPropagators[partition] = partitionedPropagators[partition].removeObjectSink( sink );
        return this;
    }

    @Override
    public void changeSinkPartition( ObjectSink sink, RuleBasePartitionId oldPartition, RuleBasePartitionId newPartition, int alphaNodeHashingThreshold ) {
        int oldP = oldPartition.getParallelEvaluationSlot();
        partitionedPropagators[oldP] = partitionedPropagators[oldP].removeObjectSink( sink );
        int newP = newPartition.getParallelEvaluationSlot();
        partitionedPropagators[newP] = partitionedPropagators[newP].addObjectSink( sink, alphaNodeHashingThreshold );
    }

    @Override
    public void propagateAssertObject( InternalFactHandle factHandle, PropagationContext context, InternalWorkingMemory workingMemory ) {
        CompositeDefaultAgenda compositeAgenda = (CompositeDefaultAgenda) workingMemory.getAgenda();
        if (hashed) {
            AlphaNode sink = (AlphaNode) this.hashedSinkMap.get( new CompositeObjectSinkAdapter.HashKey( fieldIndex, factHandle.getObject() ) );
            if ( sink != null ) {
                compositeAgenda.getPartitionedAgenda( sink.getPartitionId().getParallelEvaluationSlot() )
                               .addPropagation( new HashedInsert( sink, factHandle, context ) );
            }
        } else {
            // Enqueues this insertion on the propagation queues of each partitioned agenda
            for ( int i = 0; i < partitionedPropagators.length; i++ ) {
                if ( !partitionedPropagators[i].isEmpty() ) {
                    compositeAgenda.getPartitionedAgenda( i ).addPropagation( new Insert( partitionedPropagators[i], factHandle, context ) );
                }
            }
        }
    }

    public static class Insert extends PropagationEntry.AbstractPropagationEntry {

        private final ObjectSinkPropagator propagator;
        private final InternalFactHandle factHandle;
        private final PropagationContext context;

        public Insert( ObjectSinkPropagator propagator, InternalFactHandle factHandle, PropagationContext context ) {
            this.propagator = propagator;
            this.factHandle = factHandle;
            this.context = context;
        }

        @Override
        public void execute( InternalWorkingMemory wm ) {
            propagator.propagateAssertObject( factHandle, context, wm );
        }

        @Override
        public String toString() {
            return "Insert of " + factHandle.getObject();
        }
    }

    public static class HashedInsert extends PropagationEntry.AbstractPropagationEntry {

        private final AlphaNode sink;
        private final InternalFactHandle factHandle;
        private final PropagationContext context;

        public HashedInsert( AlphaNode sink, InternalFactHandle factHandle, PropagationContext context ) {
            this.sink = sink;
            this.factHandle = factHandle;
            this.context = context;
        }

        @Override
        public void execute( InternalWorkingMemory wm ) {
            sink.getObjectSinkPropagator().propagateAssertObject( factHandle, context, wm );
        }

        @Override
        public String toString() {
            return "Hashed insert of " + factHandle.getObject();
        }
    }

    @Override
    public BaseNode getMatchingNode( BaseNode candidate ) {
        return Stream.of( partitionedPropagators )
                     .map( p -> p.getMatchingNode( candidate ) )
                     .filter( node -> node != null )
                     .findFirst()
                     .orElse( null );
    }

    @Override
    public ObjectSink[] getSinks() {
        return Stream.of( partitionedPropagators )
                     .flatMap( p -> Stream.of( p.getSinks() ) )
                     .toArray(ObjectSink[]::new);
    }

    @Override
    public int size() {
        return Stream.of( partitionedPropagators )
                     .mapToInt( ObjectSinkPropagator::size )
                     .sum();
    }

    public boolean isEmpty() {
        return false;
    }

    public ObjectSinkPropagator[] getPartitionedPropagators() {
        return partitionedPropagators;
    }

    @Override
    public void propagateModifyObject( InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory ) {
        throw new UnsupportedOperationException("propagateModifyObject has to be executed by partitions");
    }

    public void propagateModifyObjectForPartition( InternalFactHandle handle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory wm, int partition ) {
        partitionedPropagators[partition].propagateModifyObject(handle, modifyPreviousTuples, context, wm);
    }

    @Override
    public void byPassModifyToBetaNode( InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory ) {
        throw new UnsupportedOperationException("This sink is only used for OTNs, it cannot be the sink for a beta");
    }

    @Override
    public void doLinkRiaNode( InternalWorkingMemory wm ) {
        throw new UnsupportedOperationException("This sink is only used for OTNs, it cannot be the sink for a RIA");

    }

    @Override
    public void doUnlinkRiaNode( InternalWorkingMemory wm ) {
        throw new UnsupportedOperationException("This sink is only used for OTNs, it cannot be the sink for a RIA");

    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeBoolean( hashed );
        out.writeObject( fieldIndex );
        out.writeObject( hashedSinkMap );
        for ( ObjectSinkPropagator partitionedPropagator : partitionedPropagators ) {
            out.writeObject( partitionedPropagator );
        }
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        hashed = in.readBoolean();
        fieldIndex = (FieldIndex) in.readObject();
        hashedSinkMap = (ObjectHashMap) in.readObject();
        for (int i = 0; i < partitionedPropagators.length; i++) {
            partitionedPropagators[i] = (ObjectSinkPropagator) in.readObject();
        }
    }

    public ObjectSinkPropagator asNonPartitionedSinkPropagator(int alphaNodeHashingThreshold) {
        ObjectSinkPropagator sinkPropagator = new EmptyObjectSinkAdapter();
        for ( int i = 0; i < partitionedPropagators.length; i++ ) {
            for (ObjectSink sink : partitionedPropagators[i].getSinks()) {
                sinkPropagator = sinkPropagator.addObjectSink( sink, alphaNodeHashingThreshold );
            }
        }
        return sinkPropagator;
    }

    public int getUsedPartitionsCount() {
        int partitions = 0;
        for ( int i = 0; i < partitionedPropagators.length; i++ ) {
            if (partitionedPropagators[i].size() > 0) {
                partitions++;
            }
        }
        return partitions;
    }

}
