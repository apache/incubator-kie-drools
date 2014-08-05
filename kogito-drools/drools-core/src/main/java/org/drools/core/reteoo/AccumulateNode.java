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

package org.drools.core.reteoo;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller.TupleKey;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.marshalling.impl.ProtobufMessages.FactHandle;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Accumulate;
import org.drools.core.rule.ContextEntry;
import org.drools.core.spi.Accumulator;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.AbstractBaseLinkedListNode;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Map;

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

    protected boolean                    unwrapRightObject;
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
                          final boolean unwrapRightObject,
                          final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getKnowledgeBase().getConfiguration().isMultithreadEvaluation(),
               leftInput,
               rightInput,
               sourceBinder,
               context );
        this.resultBinder = resultBinder;
        this.resultConstraints = resultConstraints;
        this.accumulate = accumulate;
        this.unwrapRightObject = unwrapRightObject;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
    }

    public void readExternal( ObjectInput in ) throws IOException,
                                              ClassNotFoundException {
        super.readExternal( in );
        unwrapRightObject = in.readBoolean();
        accumulate = (Accumulate) in.readObject();
        resultConstraints = (AlphaNodeFieldConstraint[]) in.readObject();
        resultBinder = (BetaConstraints) in.readObject();
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeBoolean( unwrapRightObject );
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

    public boolean isUnwrapRightObject() {
        return unwrapRightObject;
    }

    public InternalFactHandle createResultFactHandle(final PropagationContext context,
                                                     final InternalWorkingMemory workingMemory,
                                                     final LeftTuple leftTuple,
                                                     final Object result) {
        InternalFactHandle handle = null;
        ProtobufMessages.FactHandle _handle = null;
        if( context.getReaderContext() != null ) {
            Map<TupleKey, FactHandle> map = (Map<ProtobufInputMarshaller.TupleKey, ProtobufMessages.FactHandle>) context.getReaderContext().nodeMemories.get( getId() );
            if( map != null ) {
                _handle = map.get( PersisterHelper.createTupleKey(leftTuple) );
            }
        }
        if( _handle != null ) {
            // create a handle with the given id
            handle = workingMemory.getFactHandleFactory().newFactHandle( _handle.getId(),
                                                                         result,
                                                                         _handle.getRecency(),
                                                                         workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( context.getEntryPoint(),
                                                                                                                                               result ),
                                                                         workingMemory,
                                                                         null ); // so far, result is not an event
        } else {
            handle = workingMemory.getFactHandleFactory().newFactHandle( result,
                                                                         workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( context.getEntryPoint(),
                                                                                                                                               result ),
                                                                         workingMemory,
                                                                         null ); // so far, result is not an event
        }
        return handle;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.BaseNode#hashCode()
     */
    public int hashCode() {
        return this.leftInput.hashCode() ^ this.rightInput.hashCode() ^ this.accumulate.hashCode() ^ this.resultBinder.hashCode() ^ Arrays.hashCode( this.resultConstraints );
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( final Object object ) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof AccumulateNode) ) {
            return false;
        }

        final AccumulateNode other = (AccumulateNode) object;

        if ( this.getClass() != other.getClass() || (!this.leftInput.equals( other.leftInput )) || (!this.rightInput.equals( other.rightInput )) || (!this.constraints.equals( other.constraints )) ) {
            return false;
        }

        return this.accumulate.equals( other.accumulate ) && resultBinder.equals( other.resultBinder ) && Arrays.equals( this.resultConstraints,
                                                                                                                         other.resultConstraints );
    }

    /**
     * Creates a BetaMemory for the BetaNode's memory.
     */
    public Memory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        AccumulateMemory memory = this.accumulate.isMultiFunction() ?
                                  new MultiAccumulateMemory(this.accumulate.getAccumulators()) :
                                  new SingleAccumulateMemory(this.accumulate.getAccumulators()[0]);
        memory.betaMemory = this.constraints.createBetaMemory(config,
                                                              NodeTypeEnums.AccumulateNode);
        memory.workingMemoryContext = this.accumulate.createWorkingMemoryContext();
        memory.resultsContext = this.resultBinder.createContext();
        memory.alphaContexts = new ContextEntry[this.resultConstraints.length];
        for ( int i = 0; i < this.resultConstraints.length; i++ ) {
            memory.alphaContexts[i] = this.resultConstraints[i].createContextEntry();
        }
        return memory;
    }

    public void doRemove(InternalWorkingMemory workingMemory, AccumulateMemory object) {

    }

    public static abstract class AccumulateMemory extends AbstractBaseLinkedListNode<Memory>
        implements
        Memory {

        public Object             workingMemoryContext;
        public BetaMemory         betaMemory;
        public ContextEntry[]     resultsContext;
        public ContextEntry[]     alphaContexts;
        
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
    }

    public static class SingleAccumulateMemory extends AccumulateMemory {

        private final Accumulator accumulator;

        public SingleAccumulateMemory(Accumulator accumulator) {
            this.accumulator = accumulator;
        }

        public void reset() {
            betaMemory.reset();
            workingMemoryContext = this.accumulator.createWorkingMemoryContext();
        }
    }

    public static class MultiAccumulateMemory extends AccumulateMemory {

        private final Accumulator[] accumulators;

        public MultiAccumulateMemory(Accumulator[] accumulators) {
            this.accumulators = accumulators;
        }

        public void reset() {
            betaMemory.reset();
            workingMemoryContext = new Object[ this.accumulators.length ];
            for( int i = 0; i < this.accumulators.length; i++ ) {
                ((Object[])workingMemoryContext)[i] = this.accumulators[i].createWorkingMemoryContext();
            }
        }
    }

    public static class AccumulateContext
        implements
        Externalizable {
        public  Object              context;
        public  RightTuple          result;
        public  InternalFactHandle  resultFactHandle;
        public  LeftTuple           resultLeftTuple;
        public  boolean             propagated;
        private WorkingMemoryAction action; // is transiant

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            context = in.readObject();
            result = (RightTuple) in.readObject();
            propagated = in.readBoolean();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(context);
            out.writeObject(result);
            out.writeBoolean(propagated);
        }

        public WorkingMemoryAction getAction() {
            return action;
        }

        public void setAction(WorkingMemoryAction action) {
            this.action = action;
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
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled);
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final LeftTupleSink sink) {
        return new FromNodeLeftTuple(factHandle, leftTuple, sink);
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     PropagationContext pctx,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(leftTuple, sink, pctx, leftTupleMemoryEnabled);
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTupleSink sink) {
        return new FromNodeLeftTuple(leftTuple, rightTuple, sink);
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled);
    }


    public LeftTuple createPeer(LeftTuple original) {
        FromNodeLeftTuple peer = new FromNodeLeftTuple();
        peer.initPeer((BaseLeftTuple) original, this);
        original.setPeer(peer);
        return peer;
    }

    public static enum ActivitySource {
        LEFT, RIGHT
    }


    @Override
    public void assertRightTuple(RightTuple rightTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
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
    public void assertLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retractLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyLeftTuple(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void modifyLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void updateSink(LeftTupleSink sink, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }


    @Override
    public void doRemove(RuleRemovalContext context, ReteooBuilder builder, InternalWorkingMemory[] workingMemories) {
        if ( !isInUse() ) {
            getLeftTupleSource().removeTupleSink( this );
            getRightInput().removeObjectSink( this );
        }
    }
}
