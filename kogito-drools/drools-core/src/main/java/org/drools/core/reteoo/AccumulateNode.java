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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Map;

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
               leftInput,
               rightInput,
               sourceBinder,
               context );
        this.resultBinder = resultBinder;
        this.resultConstraints = resultConstraints;
        this.accumulate = accumulate;
        this.unwrapRightObject = unwrapRightObject;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();

        hashcode = this.leftInput.hashCode() ^
                   this.rightInput.hashCode() ^
                   this.accumulate.hashCode() ^
                   this.resultBinder.hashCode() ^
                   Arrays.hashCode( this.resultConstraints );
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
        InternalFactHandle handle;
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

    @Override
    public void attach( BuildContext context ) {
        this.resultBinder.init( context, getType() );
        super.attach( context );
    }

    protected int calculateHashCode() {
        return 0;
    }

    @Override
    public boolean equals( final Object object ) {
        return this == object ||
               ( internalEquals( object ) &&
               this.leftInput.thisNodeEquals( ((AccumulateNode) object).leftInput ) &&
               this.rightInput.thisNodeEquals( ((AccumulateNode) object).rightInput ) );
    }

    @Override
    protected boolean internalEquals( Object object ) {
        if ( object == null || !(object instanceof AccumulateNode ) || this.hashCode() != object.hashCode() ) {
            return false;
        }

        AccumulateNode other = (AccumulateNode) object;
        return this.constraints.equals( other.constraints ) &&
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

    public static class AccumulateContext
        implements
        Externalizable {
        public  Object              context;
        public  RightTuple          result;
        public  InternalFactHandle  resultFactHandle;
        public  LeftTuple           resultLeftTuple;
        public  boolean             propagated;
        private WorkingMemoryAction action; // is transiant
        private PropagationContext  propagationContext;

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            context = in.readObject();
            result = (RightTuple) in.readObject();
            propagated = in.readBoolean();
            propagationContext = (PropagationContext) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(context);
            out.writeObject(result);
            out.writeBoolean(propagated);
            out.writeObject(propagationContext);
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

        public PropagationContext getPropagationContext() {
            return propagationContext;
        }

        public void setPropagationContext(PropagationContext propagationContext) {
            this.propagationContext = propagationContext;
        }
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new FromNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled);
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

    public enum ActivitySource {
        LEFT, RIGHT
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
    public boolean doRemove(RuleRemovalContext context, ReteooBuilder builder, InternalWorkingMemory[] workingMemories) {
        if ( !isInUse() ) {
            getLeftTupleSource().removeTupleSink( this );
            getRightInput().removeObjectSink( this );
            return true;
        }
        return false;
    }
}
