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
import java.util.List;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.WorkingMemoryAction;
import org.drools.core.impl.InternalKnowledgeBase;
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
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.bitmask.BitMask;

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
        this.resultBinder.init( context, getType() );
        this.resultConstraints = resultConstraints;
        this.accumulate = accumulate;
        this.unwrapRightObject = unwrapRightObject;
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
            TypeDeclaration typeDeclaration = kbase.getExactTypeDeclaration( ((ClassObjectType) leftObjectType).getClassType() );
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
        if ( context.getReaderContext() != null ) {
            handle = context.getReaderContext().createAccumulateHandle( context.getEntryPoint(), workingMemory, leftTuple, result, getId() );
        }
        if (handle == null) {
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
        super.attach( context );
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

    public static class AccumulateContext
        implements
        ContextOwner, Externalizable {
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

        public <T> T getContext(Class<T> contextClass) {
            if (contextClass.isInstance( context )) {
                return (T) context;
            }
            return null;
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
    public boolean doRemove(RuleRemovalContext context, ReteooBuilder builder) {
        if ( !isInUse() ) {
            getLeftTupleSource().removeTupleSink( this );
            getRightInput().removeObjectSink( this );
            return true;
        }
        return false;
    }
}
