/*
 * Copyright 2005 JBoss Inc
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

package org.drools.reteoo;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.reteoo.builder.BuildContext;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.ClassWireable;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import static org.drools.core.util.BitMaskUtil.intersect;
import static org.drools.reteoo.PropertySpecificUtil.calculateNegativeMask;
import static org.drools.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.reteoo.PropertySpecificUtil.getSettableProperties;
import static org.drools.reteoo.PropertySpecificUtil.isPropertyReactive;

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
        Externalizable {

    private long                      leftDeclaredMask;
    private long                      leftInferredMask;
    private long                      leftNegativeMask;


    /** The left input <code>TupleSource</code>. */
    protected LeftTupleSource         leftInput;

    
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The destination for <code>Tuples</code>. */
    protected LeftTupleSinkPropagator sink;

    
    private transient ObjectTypeNode.Id leftInputOtnId;

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
    protected LeftTupleSource(final int id,
                              final RuleBasePartitionId partitionId,
                              final boolean partitionsEnabled) {
        super( id, partitionId, partitionsEnabled );
        this.sink = EmptyLeftTupleSinkAdapter.getInstance();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        sink = (LeftTupleSinkPropagator) in.readObject();
        leftInput = (LeftTupleSource) in.readObject();        
        leftDeclaredMask = in.readLong();
        leftInferredMask = in.readLong();
        leftNegativeMask = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( sink );
        out.writeObject( leftInput );        
        out.writeLong( leftDeclaredMask );
        out.writeLong( leftInferredMask );
        out.writeLong( leftNegativeMask );
    }

    public abstract short getType();
    
    public abstract LeftTuple createPeer(LeftTuple original);
    
    public void addTupleSink(final LeftTupleSink tupleSink) {
        addTupleSink(tupleSink, null);
    }
    
    public LeftTupleSource getLeftTupleSource() {
        return leftInput;
    }

    public void setLeftTupleSource(LeftTupleSource leftInput) {
        this.leftInput = leftInput;
    }

    /**
    public LeftTupleSource getLeftTupleSource() {
		return leftInput;
	}

	public void setLeftTupleSource(LeftTupleSource leftInput) {
		this.leftInput = leftInput;
	}

	/**
     * Adds the <code>TupleSink</code> so that it may receive
     * <code>Tuples</code> propagated from this <code>TupleSource</code>.
     *
     * @param tupleSink
     *            The <code>TupleSink</code> to receive propagated
     *            <code>Tuples</code>.
     */
    protected void addTupleSink(final LeftTupleSink tupleSink, final BuildContext context) {
        this.sink = addTupleSink(this.sink, tupleSink, context);
    }

    protected LeftTupleSinkPropagator addTupleSink(final LeftTupleSinkPropagator sinkPropagator, final LeftTupleSink tupleSink, final BuildContext context) {
        if ( sinkPropagator instanceof EmptyLeftTupleSinkAdapter ) {
            // otherwise, we use the lighter synchronous propagator
            return new SingleLeftTupleSinkAdapter( this.getPartitionId(), tupleSink );
        }

        if ( sinkPropagator instanceof SingleLeftTupleSinkAdapter ) {
            final CompositeLeftTupleSinkAdapter sinkAdapter;
            sinkAdapter = new CompositeLeftTupleSinkAdapter( this.getPartitionId() );
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
    protected void removeTupleSink(final LeftTupleSink tupleSink) {
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

    public abstract void updateSink(LeftTupleSink sink,
                                    PropagationContext context,
                                    InternalWorkingMemory workingMemory);

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
            leftDeclaredMask = Long.MAX_VALUE;
            return;
        }

        if ( leftInput.getType() != NodeTypeEnums.LeftInputAdapterNode) {
            // BetaNode's not after LIANode are not relevant for left mask property specific, so don't block anything.
            leftDeclaredMask = Long.MAX_VALUE;
            return;
        }

        Pattern pattern = context.getLastBuiltPatterns()[1]; // left input pattern

        ObjectType objectType = pattern == null || this.getType() == NodeTypeEnums.AccumulateNode ?
            ((LeftInputAdapterNode)leftInput).getParentObjectSource().getObjectTypeNode().getObjectType() :
            pattern.getObjectType();

        if ( !(objectType instanceof ClassObjectType) ) {
            // Only ClassObjectType can use property specific
            leftDeclaredMask = Long.MAX_VALUE;
            return;
        }

        Class objectClass = ((ClassWireable) objectType).getClassType();
        if ( isPropertyReactive(context, objectClass) ) {
            // TODO: at the moment if pattern is null (e.g. for eval node) we cannot calculate the mask, so we leave it to 0
            if ( pattern != null ) {
                List<String> leftListenedProperties = pattern.getListenedProperties();
                List<String> settableProperties = getSettableProperties( context.getRuleBase(), objectClass );
                leftDeclaredMask = calculatePositiveMask( leftListenedProperties, settableProperties );
                leftNegativeMask = calculateNegativeMask( leftListenedProperties, settableProperties );
                setLeftListenedProperties(leftListenedProperties);
            }
        } else {
            // if property specific is not on, then accept all modification propagations
            leftDeclaredMask = Long.MAX_VALUE;
        }
    }

    protected void setLeftListenedProperties(List<String> leftListenedProperties) { }

    protected void initInferredMask(LeftTupleSource leftInput) {
        LeftTupleSource unwrappedLeft = unwrapLeftInput(leftInput);
        if ( unwrappedLeft.getType() == NodeTypeEnums.LeftInputAdapterNode && ((LeftInputAdapterNode)unwrappedLeft).getParentObjectSource().getType() == NodeTypeEnums.AlphaNode ) {
            AlphaNode alphaNode = (AlphaNode) ((LeftInputAdapterNode)unwrappedLeft).getParentObjectSource();
            leftInferredMask = alphaNode.updateMask( leftDeclaredMask );
        } else {
            leftInferredMask = leftDeclaredMask;
        }
        leftInferredMask &= (Long.MAX_VALUE - leftNegativeMask);
    }

    private LeftTupleSource unwrapLeftInput(LeftTupleSource leftInput) {
        if (leftInput.getType() == NodeTypeEnums.FromNode) {
            return ((FromNode)leftInput).getLeftTupleSource();
        }
        return leftInput;
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        doModifyLeftTuple( factHandle, modifyPreviousTuples, context, workingMemory,
                           (LeftTupleSink) this, getLeftInputOtnId(), getLeftInferredMask());
    }

    public static void doModifyLeftTuple(InternalFactHandle factHandle,
                                         ModifyPreviousTuples modifyPreviousTuples,
                                         PropagationContext context,
                                         InternalWorkingMemory workingMemory,
                                         LeftTupleSink sink,
                                         ObjectTypeNode.Id leftInputOtnId,
                                         long leftInferredMask) {
        LeftTuple leftTuple = modifyPreviousTuples.peekLeftTuple();
        while ( leftTuple != null && leftTuple.getLeftTupleSink().getLeftInputOtnId() != null &&
                leftTuple.getLeftTupleSink().getLeftInputOtnId().before( leftInputOtnId ) ) {
            modifyPreviousTuples.removeLeftTuple();

            // we skipped this node, due to alpha hashing, so retract now
            ((LeftInputAdapterNode) leftTuple.getLeftTupleSink().getLeftTupleSource()).retractLeftTuple( leftTuple,
                                                                                                         context,
                                                                                                         workingMemory );

            leftTuple = modifyPreviousTuples.peekLeftTuple();
        }

        if ( leftTuple != null && leftTuple.getLeftTupleSink().getLeftInputOtnId() != null &&
             leftTuple.getLeftTupleSink().getLeftInputOtnId().equals( leftInputOtnId ) ) {
            modifyPreviousTuples.removeLeftTuple();
            leftTuple.reAdd();
            if ( intersect( context.getModificationMask(), leftInferredMask ) ) {
                // LeftTuple previously existed, so continue as modify, unless it's currently staged
                sink.modifyLeftTuple( leftTuple,
                                      context,
                                      workingMemory );
            }
        } else {
            if ( intersect( context.getModificationMask(), leftInferredMask ) ) {
                // LeftTuple does not exist, so create and continue as assert
                LeftTuple newLeftTuple = sink.createLeftTuple( factHandle,
                                                               sink,
                                                               true );

                sink.assertLeftTuple( newLeftTuple,
                                      context,
                                      workingMemory );
            }
        }
    }
    
    public long getLeftDeclaredMask() {
        return leftDeclaredMask;
    }

    public long getLeftInferredMask() {
        return leftInferredMask;
    }

    protected void setLeftInferredMask(long leftInferredMask) {
        this.leftInferredMask = leftInferredMask;
    }

    public long getLeftNegativeMask() {
        return leftNegativeMask;
    }

    public ObjectTypeNode.Id getLeftInputOtnId() {
        return leftInputOtnId;
    }

    public void setLeftInputOtnId(ObjectTypeNode.Id leftInputOtnId) {
        this.leftInputOtnId = leftInputOtnId;
    }

    protected abstract ObjectTypeNode getObjectTypeNode();

    public ObjectType getObjectType() {
        ObjectTypeNode objectTypeNode = getObjectTypeNode();
        return objectTypeNode != null ? objectTypeNode.getObjectType() : null;
    }
    
    public boolean isUnlinkingEnabled() {
        return false;
    }

    public abstract boolean isLeftTupleMemoryEnabled();
}
