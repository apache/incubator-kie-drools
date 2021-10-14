/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.phreak.PhreakAccumulateNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.FastIterator;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.index.TupleList;

/**
 * AccumulateNode
 * A beta node capable of doing accumulate logic.
 *
 * Created: 04/06/2006
 *
 * @version $Id$
 */
public class AccumulateNode extends BetaNode {

    private static final long          serialVersionUID = 510l;

    protected Accumulate                 accumulate;
    protected AlphaNodeFieldConstraint[] resultConstraints;
    protected BetaConstraints            resultBinder;

    public AccumulateNode() {
    }

    public AccumulateNode(final int id,
                          final LeftTupleSource leftInput,
                          final ObjectSource rightInput,
                          final AlphaNodeFieldConstraint[] resultConstraints,
                          final BetaConstraints sourceBinder,
                          final BetaConstraints resultBinder,
                          final Accumulate accumulate,
                          final BuildContext context) {
        super( id,
               leftInput,
               rightInput,
               sourceBinder,
               context );
        this.setObjectCount(leftInput.getObjectCount() + 1); // 'accumulate' node increases the object count
        this.resultBinder = resultBinder;
        this.resultBinder.init( context, getType() );
        this.resultConstraints = resultConstraints;
        this.accumulate = accumulate;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();

        addAccFunctionDeclarationsToLeftMask( context.getKnowledgeBase(), leftInput, accumulate );

        hashcode = this.leftInput.hashCode() ^
                   this.rightInput.hashCode() ^
                   this.accumulate.hashCode() ^
                   this.resultBinder.hashCode() ^
                   Arrays.hashCode( this.resultConstraints );

    }

    private void addAccFunctionDeclarationsToLeftMask( InternalKnowledgeBase kbase, LeftTupleSource leftInput, Accumulate accumulate ) {
        BitMask leftMask = getLeftInferredMask();
        ObjectType leftObjectType = leftInput.getObjectType();
        if (leftObjectType instanceof ClassObjectType ) {
            TypeDeclaration typeDeclaration = kbase.getExactTypeDeclaration( leftObjectType.getClassType() );
            if (typeDeclaration != null && typeDeclaration.isPropertyReactive()) {
                List<String> accessibleProperties = typeDeclaration.getAccessibleProperties();
                for ( Declaration decl : accumulate.getRequiredDeclarations() ) {
                    if ( leftObjectType.equals( decl.getPattern().getObjectType() ) ) {
                        leftMask = leftMask.setAll( decl.getPattern().getPositiveWatchMask(accessibleProperties) );
                    }
                }
            }
        }
        setLeftInferredMask( leftMask );
    }

    @Override
    protected ObjectType getObjectTypeForPropertyReactivity( LeftInputAdapterNode leftInput, Pattern pattern ) {
        return pattern != null && isRightInputIsRiaNode() ?
               pattern.getObjectType() :
               leftInput.getParentObjectSource().getObjectTypeNode().getObjectType();
    }

    public void readExternal( ObjectInput in ) throws IOException,
                                              ClassNotFoundException {
        super.readExternal( in );
        accumulate = (Accumulate) in.readObject();
        resultConstraints = (AlphaNodeFieldConstraint[]) in.readObject();
        resultBinder = (BetaConstraints) in.readObject();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( accumulate );
        out.writeObject( resultConstraints );
        out.writeObject( resultBinder );
    }

    public short getType() {
        return NodeTypeEnums.AccumulateNode;
    }

    public Accumulate getAccumulate() {
        return this.accumulate;
    }       

    public AlphaNodeFieldConstraint[] getResultConstraints() {
        return resultConstraints;
    }

    public BetaConstraints getResultBinder() {
        return resultBinder;
    }

    public InternalFactHandle createResultFactHandle(final PropagationContext context,
                                                     final InternalWorkingMemory workingMemory,
                                                     final LeftTuple leftTuple,
                                                     final Object result) {
        InternalFactHandle handle = null;
        if ( context.getReaderContext() != null ) {
            handle = context.getReaderContext().createAccumulateHandle( context.getEntryPoint(), workingMemory, leftTuple, result, getId() );
        }
        if (handle == null) {
            handle = workingMemory.getFactHandleFactory().newFactHandle( result,
                                                                         null, // no need to retrieve the ObjectTypeConf, acc result is never an event or a trait
                                                                         workingMemory,
                                                                         null );
        }
        return handle;
    }

    @Override
    public void doAttach( BuildContext context ) {
        super.doAttach( context );
    }

    protected int calculateHashCode() {
        return 0;
    }

    @Override
    public boolean equals( final Object object ) {
        if (this == object) {
            return true;
        }

        if ( object == null || !(object instanceof AccumulateNode ) || this.hashCode() != object.hashCode() ) {
            return false;
        }

        AccumulateNode other = (AccumulateNode) object;
        return this.leftInput.getId() == other.leftInput.getId() && this.rightInput.getId() == other.rightInput.getId() &&
               this.constraints.equals( other.constraints ) &&
               this.accumulate.equals( other.accumulate ) &&
               resultBinder.equals( other.resultBinder ) &&
               Arrays.equals( this.resultConstraints, other.resultConstraints );
    }

    /**
     * Creates a BetaMemory for the BetaNode's memory.
     */
    public Memory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        BetaMemory betaMemory = this.constraints.createBetaMemory(config,
                                                                  NodeTypeEnums.AccumulateNode);
        AccumulateMemory memory = this.accumulate.isMultiFunction() ?
                                  new MultiAccumulateMemory(betaMemory, this.accumulate.getAccumulators()) :
                                  new SingleAccumulateMemory(betaMemory, this.accumulate.getAccumulators()[0]);

        memory.workingMemoryContext = this.accumulate.createWorkingMemoryContext();
        memory.resultsContext = this.resultBinder.createContext();
        return memory;
    }

    public static abstract class AccumulateMemory extends AbstractBaseLinkedListNode<Memory>
        implements
        SegmentNodeMemory {

        public Object             workingMemoryContext;
        private final BetaMemory  betaMemory;
        public ContextEntry[]     resultsContext;

        protected AccumulateMemory( BetaMemory betaMemory ) {
            this.betaMemory = betaMemory;
        }

        public BetaMemory getBetaMemory() {
            return this.betaMemory;
        }

        public short getNodeType() {
            return NodeTypeEnums.AccumulateNode;
        }

        public SegmentMemory getSegmentMemory() {
            return betaMemory.getSegmentMemory();
        }

        public void setSegmentMemory(SegmentMemory segmentMemory) {
            betaMemory.setSegmentMemory(segmentMemory);
        }

        public abstract void reset();

        @Override
        public long getNodePosMaskBit() {
            return betaMemory.getNodePosMaskBit();
        }

        @Override
        public void setNodePosMaskBit( long segmentPos ) {
            betaMemory.setNodePosMaskBit( segmentPos );
        }

        @Override
        public void setNodeDirtyWithoutNotify() {
            betaMemory.setNodeDirtyWithoutNotify();
        }

        @Override
        public void setNodeCleanWithoutNotify() {
            betaMemory.setNodeCleanWithoutNotify();
        }
    }

    public static class SingleAccumulateMemory extends AccumulateMemory {

        private final Accumulator accumulator;

        public SingleAccumulateMemory(BetaMemory betaMemory, Accumulator accumulator) {
            super( betaMemory );
            this.accumulator = accumulator;
        }

        public void reset() {
            getBetaMemory().reset();
            workingMemoryContext = this.accumulator.createWorkingMemoryContext();
        }
    }

    public static class MultiAccumulateMemory extends AccumulateMemory {

        private final Accumulator[] accumulators;

        public MultiAccumulateMemory(BetaMemory betaMemory, Accumulator[] accumulators) {
            super( betaMemory );
            this.accumulators = accumulators;
        }

        public void reset() {
            getBetaMemory().reset();
            workingMemoryContext = new Object[ this.accumulators.length ];
            for( int i = 0; i < this.accumulators.length; i++ ) {
                ((Object[])workingMemoryContext)[i] = this.accumulators[i].createWorkingMemoryContext();
            }
        }
    }

    public interface BaseAccumulation {
        PropagationContext getPropagationContext();

        void setPropagationContext(PropagationContext propagationContext);
    }


    public static class AccumulateContextEntry {
        private Object             key;
        private InternalFactHandle resultFactHandle;
        private LeftTuple          resultLeftTuple;
        private boolean            propagated;
        private Object             functionContext;
        private boolean            toPropagate;
        private boolean            empty = true;

        public AccumulateContextEntry(Object key) {
            this.key = key;
        }

        public InternalFactHandle getResultFactHandle() {
            return resultFactHandle;
        }

        public void setResultFactHandle(InternalFactHandle resultFactHandle) {
            this.resultFactHandle = resultFactHandle;
        }

        public LeftTuple getResultLeftTuple() {
            return resultLeftTuple;
        }

        public void setResultLeftTuple(LeftTuple resultLeftTuple) {
            this.resultLeftTuple = resultLeftTuple;
        }

        public boolean isPropagated() {
            return propagated;
        }

        public void setPropagated( boolean propagated ) {
            this.propagated = propagated;
        }

        public boolean isToPropagate() {
            return toPropagate;
        }

        public void setToPropagate(boolean toPropagate) {
            this.toPropagate = toPropagate;
        }

        public Object getFunctionContext() {
            return functionContext;
        }

        public void setFunctionContext(Object context) {
            this.functionContext = context;
        }

        public Object getKey() {
            return this.key;
        }

        public boolean isEmpty() {
            return empty;
        }

        public void setEmpty( boolean empty ) {
            this.empty = empty;
        }
    }

    public static class AccumulateContext extends AccumulateContextEntry implements BaseAccumulation {
        private PropagationContext  propagationContext;

        public AccumulateContext() {
            super(null);
        }

        public PropagationContext getPropagationContext() {
            return propagationContext;
        }

        public void setPropagationContext(PropagationContext propagationContext) {
            this.propagationContext = propagationContext;
        }
    }

    public static class GroupByContext implements BaseAccumulation {
        private PropagationContext                              propagationContext;
        private Map<Object, TupleList<AccumulateContextEntry> > groupsMap = new HashMap<>();
        private TupleList<AccumulateContextEntry>               lastTupleList;
        private TupleList<AccumulateContextEntry>               toPropagateList;

        public PropagationContext getPropagationContext() {
            return propagationContext;
        }

        public void setPropagationContext(PropagationContext propagationContext) {
            this.propagationContext = propagationContext;
        }

        public Map<Object, TupleList<AccumulateContextEntry>> getGroups() {
            return groupsMap;
        }

        public TupleList<AccumulateContextEntry> getGroup(Object workingMemoryContext, Accumulate accumulate, Tuple leftTuple,
                                                          Object key, WorkingMemory wm) {
            return groupsMap.computeIfAbsent(key, k -> {
                AccumulateContextEntry entry = new AccumulateContextEntry(key);
                entry.setFunctionContext( accumulate.init(workingMemoryContext, entry, accumulate.createFunctionContext(), leftTuple, wm) );
                PhreakAccumulateNode.initContext(workingMemoryContext, (InternalWorkingMemory) wm, accumulate, leftTuple, entry);
                return new TupleList<>(entry);
            });
        }

        public void removeGroup(Object key) {
            groupsMap.remove(key);
        }

        public void moveToPropagateTupleList(TupleList<AccumulateContextEntry> list) {
            this.lastTupleList = list;
            if ( list.getContext().isToPropagate()) {
                return;
            }

            // add list to head
            if (toPropagateList != null) {
                toPropagateList.setPrevious(list);
            }
            list.setNext(toPropagateList);
            list.setPrevious(null);
            toPropagateList = list;

            list.getContext().setToPropagate(true);
        }

        public TupleList<AccumulateContextEntry> takeToPropagateList() {
            TupleList<AccumulateContextEntry> list = toPropagateList;
            toPropagateList = null;
            return list;
        }

        public TupleList<AccumulateContextEntry> getLastTupleList() {
            return lastTupleList;
        }

        public void addMatchOnLastTupleList(LeftTuple match) {
            lastTupleList.add(match);
            lastTupleList.getContext().setEmpty( false );
        }

        public void clear() {
            for (TupleList<AccumulateContextEntry> list : groupsMap.values()) {
                for ( Tuple tuple = list.getFirst(); list.getFirst() != null; tuple = list.getFirst()) {
                    list.remove(tuple);
                    tuple.setContextObject(null);
                }
            }
            groupsMap.clear();
            toPropagateList = null;
            lastTupleList = null;
        }
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(factHandle, this, leftTupleMemoryEnabled);
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return new FromNodeLeftTuple(factHandle, leftTuple, sink);
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(leftTuple, sink, pctx, leftTupleMemoryEnabled);
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new FromNodeLeftTuple(leftTuple, rightTuple, sink);
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled);
    }


    public LeftTuple createPeer(LeftTuple original) {
        FromNodeLeftTuple peer = new FromNodeLeftTuple();
        peer.initPeer((BaseLeftTuple) original, this);
        original.setPeer(peer);
        return peer;
    }

    /**
     *  @inheritDoc
     *
     *  If an object is retract, call modify tuple for each
     *  tuple match.
     */
    public void retractRightTuple( final RightTuple rightTuple,
                                   final PropagationContext pctx,
                                   final InternalWorkingMemory workingMemory ) {
        final AccumulateMemory memory = (AccumulateMemory) workingMemory.getNodeMemory( this );

        BetaMemory bm = memory.getBetaMemory();
        rightTuple.setPropagationContext( pctx );
        doDeleteRightTuple( rightTuple, workingMemory, bm );
    }

    @Override
    public void modifyRightTuple(RightTuple rightTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean doRemove(RuleRemovalContext context, ReteooBuilder builder) {
        if ( !isInUse() ) {
            getLeftTupleSource().removeTupleSink( this );
            getRightInput().removeObjectSink( this );
            return true;
        }
        return false;
    }
}
