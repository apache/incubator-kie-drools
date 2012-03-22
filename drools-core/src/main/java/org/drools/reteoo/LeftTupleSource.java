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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.drools.base.ClassObjectType;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.RuleBasePartitionId;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Pattern;
import org.drools.rule.TypeDeclaration;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

import static org.drools.core.util.BitMaskUtil.intersect;
import static org.drools.reteoo.PropertySpecificUtil.calculateNegativeMask;
import static org.drools.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.reteoo.PropertySpecificUtil.getSettableProperties;

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

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The destination for <code>Tuples</code>. */
    protected LeftTupleSinkPropagator sink;

    
    private transient int              leftInputOtnId;

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
    LeftTupleSource(final int id,
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
        leftDeclaredMask = in.readLong();
        leftInferredMask = in.readLong();
        leftNegativeMask = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( sink );
        out.writeLong( leftDeclaredMask );
        out.writeLong( leftInferredMask );
        out.writeLong( leftNegativeMask );
    }

    /**
     * Adds the <code>TupleSink</code> so that it may receive
     * <code>Tuples</code> propagated from this <code>TupleSource</code>.
     *
     * @param tupleSink
     *            The <code>TupleSink</code> to receive propagated
     *            <code>Tuples</code>.
     */
    public void addTupleSink(final LeftTupleSink tupleSink) {
        if ( this.sink instanceof EmptyLeftTupleSinkAdapter ) {
            if ( this.partitionsEnabled && !this.partitionId.equals( tupleSink.getPartitionId() ) ) {
                // if partitions are enabled and the next node belongs to a different partition,
                // we need to use the asynchronous propagator
                this.sink = new AsyncSingleLeftTupleSinkAdapter( this.getPartitionId(), tupleSink );
            } else {
                // otherwise, we use the lighter synchronous propagator
                this.sink = new SingleLeftTupleSinkAdapter( this.getPartitionId(), tupleSink );
            }
        } else if ( this.sink instanceof SingleLeftTupleSinkAdapter ) {
            final CompositeLeftTupleSinkAdapter sinkAdapter;
            if ( this.partitionsEnabled ) {
                // a composite propagator may propagate to both nodes in the same partition
                // as well as in a different partition, so, if partitions are enabled, we
                // must use the asynchronous version
                sinkAdapter = new AsyncCompositeLeftTupleSinkAdapter( this.getPartitionId() );
            } else {
                // if partitions are disabled, then it is safe to use the lighter synchronous propagator
                sinkAdapter = new CompositeLeftTupleSinkAdapter( this.getPartitionId() );
            }
            sinkAdapter.addTupleSink( this.sink.getSinks()[0] );
            sinkAdapter.addTupleSink( tupleSink );
            this.sink = sinkAdapter;
        } else {
            ((CompositeLeftTupleSinkAdapter) this.sink).addTupleSink( tupleSink );
        }
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
                if ( this.partitionsEnabled && !this.partitionId.equals( tupleSink.getPartitionId() ) ) {
                    // if partitions are enabled and the next node belongs to a different partition,
                    // we need to use the asynchronous propagator
                    this.sink = new AsyncSingleLeftTupleSinkAdapter( this.getPartitionId(), sinkAdapter.getSinks()[0] );
                } else {
                    // otherwise, we use the lighter synchronous propagator
                    this.sink = new SingleLeftTupleSinkAdapter( this.getPartitionId(), sinkAdapter.getSinks()[0] );
                }
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

        if ( !(leftInput instanceof LeftInputAdapterNode) ) {
            // BetaNode's not after LIANode are not relevant for left mask property specific, so don't block anything.
            leftDeclaredMask = Long.MAX_VALUE;
            return;
        }

        Pattern pattern = context.getLastBuiltPatterns()[1]; // left input pattern

        ObjectType objectType;
        if ( pattern == null || this instanceof AccumulateNode ) {
            ObjectSource objectSource = ((LeftInputAdapterNode) leftInput).getParentObjectSource();
            if ( !(objectSource instanceof ObjectTypeNode) ) {
                leftDeclaredMask = Long.MAX_VALUE;
                return;
            }
            objectType = ((ObjectTypeNode) objectSource).getObjectType();
        } else {
            objectType = pattern.getObjectType();
        }

        if ( !(objectType instanceof ClassObjectType) ) {
            // Only ClassObjectType can use property specific
            leftDeclaredMask = Long.MAX_VALUE;
            return;
        }

        Class objectClass = ((ClassObjectType) objectType).getClassType();
        TypeDeclaration typeDeclaration = context.getRuleBase().getTypeDeclaration( objectClass );
        if ( typeDeclaration == null || !typeDeclaration.isPropertySpecific() ) {
            // if property specific is not on, then accept all modification propagations
            leftDeclaredMask = Long.MAX_VALUE;
        } else {
            // TODO: at the moment if pattern is null (e.g. for eval node) we cannot calculate the mask, so we leave it to 0
            if ( pattern != null ) {
                List<String> settableProperties = getSettableProperties( context.getRuleBase(), objectClass );
                leftDeclaredMask = calculatePositiveMask( pattern.getListenedProperties(), settableProperties );
                leftNegativeMask = calculateNegativeMask( pattern.getListenedProperties(), settableProperties );
            }
        }
    }

    protected void initInferredMask(LeftTupleSource leftInput) {
        LeftTupleSource unwrappedLeft = unwrapLeftInput( leftInput );
        if ( unwrappedLeft instanceof LeftInputAdapterNode && ((LeftInputAdapterNode) unwrappedLeft).getParentObjectSource() instanceof AlphaNode ) {
            AlphaNode alphaNode = (AlphaNode) ((LeftInputAdapterNode) unwrappedLeft).getParentObjectSource();
            leftInferredMask = alphaNode.updateMask( leftDeclaredMask );
        } else {
            leftInferredMask = leftDeclaredMask;
        }
        leftInferredMask &= (Long.MAX_VALUE - leftNegativeMask);
    }

    private LeftTupleSource unwrapLeftInput(LeftTupleSource leftInput) {
        if ( leftInput instanceof FromNode ) {
            return ((FromNode) leftInput).getLeftTupleSource();
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
                                         int leftInputOtnId,
                                         long leftInferredMask) {
        LeftTuple leftTuple = modifyPreviousTuples.peekLeftTuple();
        while ( leftTuple != null && ((LeftTupleSink) leftTuple.getLeftTupleSink()).getLeftInputOtnId() < leftInputOtnId ) {
            modifyPreviousTuples.removeLeftTuple();
            // we skipped this node, due to alpha hashing, so retract now
            leftTuple.getLeftTupleSink().retractLeftTuple( leftTuple,
                                                           context,
                                                           workingMemory );
            leftTuple = modifyPreviousTuples.peekLeftTuple();
        }

        if ( leftTuple != null && ((LeftTupleSink) leftTuple.getLeftTupleSink()).getLeftInputOtnId() == leftInputOtnId ) {
            modifyPreviousTuples.removeLeftTuple();
            leftTuple.reAdd();
            if ( intersect( context.getModificationMask(), leftInferredMask ) ) {
                // LeftTuple previously existed, so continue as modify
                sink.modifyLeftTuple( leftTuple,
                                      context,
                                      workingMemory );
            }
        } else {
            if ( intersect( context.getModificationMask(), leftInferredMask ) ) {
                // LeftTuple does not exist, so create and continue as assert
                sink.assertLeftTuple( sink.createLeftTuple( factHandle,
                                                            sink,
                                                            true ),
                                      context,
                                      workingMemory );
            }
        }

        //        // cast is safe, as LIANode never has this method called
        //        LeftTupleSink sink = (LeftTupleSink)this;
        //        LeftTuple leftTuple = modifyPreviousTuples.removeLeftTuple(sink);
        //        if ( leftTuple != null ) {
        //            leftTuple.reAdd();
        //        }
        //
        //        if ( intersect(context.getModificationMask(), leftInferredMask) ) {
        //            if ( leftTuple != null ) {
        //                // LeftTuple previously existed, so continue as modify
        //                sink.modifyLeftTuple( leftTuple,
        //                        context,
        //                        workingMemory );
        //            } else {
        //                // LeftTuple does not exist, so create and continue as assert
        //                sink.assertLeftTuple( sink.createLeftTuple( factHandle,
        //                                                            sink,
        //                                                            true ),
        //                                      context,
        //                                      workingMemory );
        //            }
        //        }
    }

    public long getLeftDeclaredMask() {
        return leftDeclaredMask;
    }

    public long getLeftInferredMask() {
        return leftInferredMask;
    }

    public long getLeftNegativeMask() {
        return leftNegativeMask;
    }

    public int getLeftInputOtnId() {
        return leftInputOtnId;
    }

    public void setLeftInputOtnId(int leftInputOtnId) {
        this.leftInputOtnId = leftInputOtnId;
    }

    protected abstract ObjectTypeNode getObjectTypeNode();
}
