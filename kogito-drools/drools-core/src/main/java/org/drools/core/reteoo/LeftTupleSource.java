/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collection;
import java.util.List;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.ClassWireable;
import org.drools.core.spi.ObjectType;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.bitmask.EmptyBitMask;

import static org.drools.core.reteoo.PropertySpecificUtil.*;

/**
 * A source of <code>ReteTuple</code> s for a <code>TupleSink</code>.
 *
 * <p>
 * Nodes that propagate <code>Tuples</code> extend this class.
 * </p>
 *
 * @see LeftTupleSource
 * @see LeftTuple
 */
public abstract class LeftTupleSource extends BaseNode
        implements
        LeftTupleNode,
        Externalizable {

    private BitMask                   leftDeclaredMask = EmptyBitMask.get();
    private BitMask                   leftInferredMask = EmptyBitMask.get();
    private BitMask                   leftNegativeMask = EmptyBitMask.get();


    /** The left input <code>TupleSource</code>. */
    protected LeftTupleSource         leftInput;


    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The destination for <code>Tuples</code>. */
    protected LeftTupleSinkPropagator sink;

    private transient ObjectTypeNode.Id leftInputOtnId;

    private int positionInPath;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public LeftTupleSource() {

    }

    /**
     * Single parameter constructor that specifies the unique id of the node.
     *
     * @param id
     */
    protected LeftTupleSource(int id, BuildContext context) {
        super(id,
              context != null ? context.getPartitionId() : RuleBasePartitionId.MAIN_PARTITION,
              context != null && context.getKnowledgeBase().getConfiguration().isMultithreadEvaluation());
        this.sink = EmptyLeftTupleSinkAdapter.getInstance();

        initMemoryId( context );
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        sink = (LeftTupleSinkPropagator) in.readObject();
        leftDeclaredMask = (BitMask) in.readObject();
        leftInferredMask = (BitMask) in.readObject();
        leftNegativeMask = (BitMask) in.readObject();
        positionInPath = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( sink );
        out.writeObject(leftDeclaredMask);
        out.writeObject(leftInferredMask);
        out.writeObject(leftNegativeMask);
        out.writeInt( positionInPath );
    }

    public int getPositionInPath() {
        return positionInPath;
    }

    public abstract short getType();

    public abstract LeftTuple createPeer(LeftTuple original);

    public void addTupleSink(final LeftTupleSink tupleSink) {
        addTupleSink(tupleSink, null);
    }

    public LeftTupleSource getLeftTupleSource() {
        return leftInput;
    }

    public final void setLeftTupleSource(LeftTupleSource leftInput) {
        this.leftInput = leftInput;
        positionInPath = leftInput.getPositionInPath() + 1;
    }

	/**
     * Adds the <code>TupleSink</code> so that it may receive
     * <code>Tuples</code> propagated from this <code>TupleSource</code>.
     *
     * @param tupleSink
     *            The <code>TupleSink</code> to receive propagated
     *            <code>Tuples</code>.
     */
    public void addTupleSink(final LeftTupleSink tupleSink, final BuildContext context) {
        this.sink = addTupleSink(this.sink, tupleSink, context);
    }

    protected LeftTupleSinkPropagator addTupleSink(final LeftTupleSinkPropagator sinkPropagator, final LeftTupleSink tupleSink, final BuildContext context) {
        if ( sinkPropagator instanceof EmptyLeftTupleSinkAdapter ) {
            // otherwise, we use the lighter synchronous propagator
            return new SingleLeftTupleSinkAdapter( this.getPartitionId(), tupleSink );
        }

        if ( sinkPropagator instanceof SingleLeftTupleSinkAdapter ) {
            CompositeLeftTupleSinkAdapter sinkAdapter = new CompositeLeftTupleSinkAdapter( this.getPartitionId() );
            sinkAdapter.addTupleSink( sinkPropagator.getSinks()[0] );
            sinkAdapter.addTupleSink( tupleSink );
            return sinkAdapter;
        }

        ((CompositeLeftTupleSinkAdapter) sinkPropagator).addTupleSink( tupleSink );
        return sinkPropagator;
    }

    /**
     * Removes the <code>TupleSink</code>
     *
     * @param tupleSink
     *            The <code>TupleSink</code> to remove
     */
    public void removeTupleSink(final LeftTupleSink tupleSink) {
        if ( this.sink instanceof EmptyLeftTupleSinkAdapter ) {
            throw new IllegalArgumentException( "Cannot remove a sink, when the list of sinks is null" );
        }

        if ( this.sink instanceof SingleLeftTupleSinkAdapter ) {
            this.sink = EmptyLeftTupleSinkAdapter.getInstance();
        } else {
            final CompositeLeftTupleSinkAdapter sinkAdapter = (CompositeLeftTupleSinkAdapter) this.sink;
            sinkAdapter.removeTupleSink( tupleSink );
            if ( sinkAdapter.size() == 1 ) {
                this.sink = new SingleLeftTupleSinkAdapter( this.getPartitionId(), sinkAdapter.getSinks()[0] );
            }
        }
    }

    public LeftTupleSinkPropagator getSinkPropagator() {
        return this.sink;
    }

    public void setSourcePartitionId(BuildContext context, RuleBasePartitionId partitionId) {
        setSourcePartitionId(leftInput, context, partitionId);
    }

    protected void setSourcePartitionId(BaseNode source, BuildContext context, RuleBasePartitionId partitionId) {
        if (this.partitionId == partitionId) {
            return;
        }
        this.partitionId = partitionId;
        if (source.getPartitionId() == RuleBasePartitionId.MAIN_PARTITION) {
            setPartitionIdWithSinks( partitionId );
        } else {
            source.setPartitionId( context, partitionId );
        }
    }

    public final void setPartitionIdWithSinks( RuleBasePartitionId partitionId ) {
        this.partitionId = partitionId;
        for (LeftTupleSink sink : getSinkPropagator().getSinks()) {
            sink.setPartitionIdWithSinks( partitionId );
        }
    }

    public boolean isInUse() {
        return this.sink.size() > 0;
    }

    protected final void initMasks(BuildContext context,
                                   LeftTupleSource leftInput) {
        initDeclaredMask( context, leftInput );
        initInferredMask( leftInput );
    }

    protected void initDeclaredMask(BuildContext context,
                                    LeftTupleSource leftInput) {
        if ( context == null || context.getLastBuiltPatterns() == null ) {
            // only happens during unit tests
            leftDeclaredMask = AllSetBitMask.get();
            return;
        }

        if ( leftInput.getType() != NodeTypeEnums.LeftInputAdapterNode) {
            // BetaNode's not after LIANode are not relevant for left mask property specific, so don't block anything.
            leftDeclaredMask = AllSetBitMask.get();
            return;
        }

        Pattern pattern = context.getLastBuiltPatterns()[1]; // left input pattern

        ObjectType objectType = pattern == null || this.getType() == NodeTypeEnums.AccumulateNode ?
            ((LeftInputAdapterNode)leftInput).getParentObjectSource().getObjectTypeNode().getObjectType() :
            pattern.getObjectType();

        if ( !(objectType instanceof ClassObjectType) ) {
            // Only ClassObjectType can use property specific
            leftDeclaredMask = AllSetBitMask.get();
            return;
        }

        Class objectClass = ((ClassWireable) objectType).getClassType();
        // if pattern is null (e.g. for eval or query nodes) we cannot calculate the mask, so we set it all
        if ( pattern != null && isPropertyReactive(context, objectClass) ) {
            Collection<String> leftListenedProperties = pattern.getListenedProperties();
            List<String> settableProperties = getAccessibleProperties( context.getKnowledgeBase(), objectClass );
            leftDeclaredMask = calculatePositiveMask( leftListenedProperties, settableProperties );
            leftNegativeMask = calculateNegativeMask( leftListenedProperties, settableProperties );
            setLeftListenedProperties(leftListenedProperties);
        } else {
            // if property specific is not on, then accept all modification propagations
            leftDeclaredMask = AllSetBitMask.get();
        }
    }

    protected void setLeftListenedProperties(Collection<String> leftListenedProperties) { }

    protected void initInferredMask(LeftTupleSource leftInput) {
        LeftTupleSource unwrappedLeft = unwrapLeftInput(leftInput);
        if ( unwrappedLeft.getType() == NodeTypeEnums.LeftInputAdapterNode && ((LeftInputAdapterNode)unwrappedLeft).getParentObjectSource().getType() == NodeTypeEnums.AlphaNode ) {
            AlphaNode alphaNode = (AlphaNode) ((LeftInputAdapterNode)unwrappedLeft).getParentObjectSource();
            leftInferredMask = alphaNode.updateMask( leftDeclaredMask );
        } else {
            leftInferredMask = leftDeclaredMask;
        }
        leftInferredMask = leftInferredMask.resetAll(leftNegativeMask);
    }

    private LeftTupleSource unwrapLeftInput(LeftTupleSource leftInput) {
        if (leftInput.getType() == NodeTypeEnums.FromNode || leftInput.getType() == NodeTypeEnums.ReactiveFromNode) {
            return leftInput.getLeftTupleSource();
        }
        return leftInput;
    }

    public BitMask getLeftDeclaredMask() {
        return leftDeclaredMask;
    }

    public BitMask getLeftInferredMask() {
        return leftInferredMask;
    }

    protected void setLeftInferredMask(BitMask leftInferredMask) {
        this.leftInferredMask = leftInferredMask;
    }

    public BitMask getLeftNegativeMask() {
        return leftNegativeMask;
    }

    public ObjectTypeNode.Id getLeftInputOtnId() {
        return leftInputOtnId;
    }

    public void setLeftInputOtnId(ObjectTypeNode.Id leftInputOtnId) {
        this.leftInputOtnId = leftInputOtnId;
    }

    public ObjectType getObjectType() {
        ObjectTypeNode objectTypeNode = getObjectTypeNode();
        return objectTypeNode != null ? objectTypeNode.getObjectType() : null;
    }

    public abstract boolean isLeftTupleMemoryEnabled();
}
