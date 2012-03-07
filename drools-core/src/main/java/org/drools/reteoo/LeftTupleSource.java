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
import org.drools.common.MemoryFactory;
import org.drools.common.RuleBasePartitionId;
import org.drools.reteoo.LeftInputAdapterNode.LiaNodeMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Pattern;
import org.drools.spi.ClassWireable;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

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
            if ( this.partitionsEnabled && !this.partitionId.equals( tupleSink.getPartitionId() ) ) {
                // if partitions are enabled and the next node belongs to a different partition,
                // we need to use the asynchronous propagator
                return new AsyncSingleLeftTupleSinkAdapter( this.getPartitionId(), tupleSink );
            }

            // otherwise, we use the lighter synchronous propagator
            return new SingleLeftTupleSinkAdapter( this.getPartitionId(), tupleSink );
        }

        if ( sinkPropagator instanceof SingleLeftTupleSinkAdapter ) {
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

        if ( leftInput.getType() != NodeTypeEnums.LeftInputAdapterNode) {
            // BetaNode's not after LIANode are not relevant for left mask property specific, so don't block anything.
            leftDeclaredMask = Long.MAX_VALUE;
            return;
        }

        Pattern pattern = context.getLastBuiltPatterns()[1]; // left input pattern

        ObjectType objectType;
        if (pattern == null || this.getType() == NodeTypeEnums.AccumulateNode) {
            ObjectSource objectSource = ((LeftInputAdapterNode)leftInput).getParentObjectSource();
            if ( objectSource.getType() != NodeTypeEnums.ObjectTypeNode) {
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
                                         int leftInputOtnId,
                                         long leftInferredMask) {
        LeftTuple leftTuple = modifyPreviousTuples.peekLeftTuple();
        while ( leftTuple != null && leftTuple.getLeftTupleSink().getLeftInputOtnId() < leftInputOtnId ) {
            modifyPreviousTuples.removeLeftTuple();
            leftTuple.setPropagationContext( context );
            
//            // we skipped this node, due to alpha hashing, so retract now
//            if ( leftTuple.getMemory() != null && leftTuple.getMemory().isStagingMemory() ) {  // can be null for RTN, or no unlinking
//                // this is only possible when unlinking is enabled
//                leftTuple.getMemory().remove( leftTuple ); // could be in liam staging or segmentmemory staging, but it shouldn't matter
//                LiaNodeMemory lm = ( LiaNodeMemory ) workingMemory.getNodeMemory( (LeftInputAdapterNode) sink.getLeftTupleSource() );
//                lm.setCounter( lm.getCounter() - 1 ); // we need this to track when we unlink
//                if ( lm.getCounter() == 0 ) {
//                    lm.unlinkNode( workingMemory );
//                }                
//            } else {
//            leftTuple.getLeftTupleSink().retractLeftTuple( leftTuple,
//                                                           context,
//                                                           workingMemory );
//            }
            
            ((LeftInputAdapterNode) leftTuple.getLeftTupleSink().getLeftTupleSource()).retractLeftTuple( leftTuple,
                                                                                                         context,
                                                                                                         workingMemory );
            

            
            leftTuple = modifyPreviousTuples.peekLeftTuple();
        }

        if ( leftTuple != null && leftTuple.getLeftTupleSink().getLeftInputOtnId() == leftInputOtnId ) {
            modifyPreviousTuples.removeLeftTuple();
            leftTuple.reAdd();
            leftTuple.setPropagationContext( context );
            if ( intersect( context.getModificationMask(), leftInferredMask ) ) {                
                // LeftTuple previously existed, so continue as modify, unless it's currently staged
                sink.modifyLeftTuple( leftTuple,
                                      context,
                                      workingMemory );
                
//                if ( leftTuple.getMemory() == null || !leftTuple.getMemory().isStagingMemory() ) { // can be null for RTN, or unlinking is off                    
//                    sink.modifyLeftTuple( leftTuple,
//                                          context,
//                                          workingMemory );
//                } // else LeftTuple is still staged, hasn't propagated yet
            }
        } else {
            if ( intersect( context.getModificationMask(), leftInferredMask ) ) {
                // LeftTuple does not exist, so create and continue as assert
                
                LeftTuple newLeftTuple = sink.createLeftTuple( factHandle,
                                                               sink,
                                                               true );
                newLeftTuple.setPropagationContext( context );
                
//                LeftInputAdapterNode liaNode = ((LeftInputAdapterNode) sink.getLeftTupleSource());
//                if ( liaNode.isUnlinkingEnabled() ) {
//                    // Add it to the lia node for lazy propagation on linking
//                    LiaNodeMemory lm = ( LiaNodeMemory ) workingMemory.getNodeMemory( liaNode );
//                    if ( lm.getSegmentMemory() == null ) {
//                        BetaNode.createNodeSegmentMemory( liaNode, workingMemory ); // initialises for all nodes in segment, including this one
//                    }          
//                    if ( lm.getStagedLeftTupleList().size() == 0 ) {
//                        // link. We do this on staged tuples, instead of entire count, as the lazy agenda might need re-activating
//                        lm.linkNode( workingMemory );
//                    }            
//                    lm.getStagedLeftTupleList().add( newLeftTuple );
//                    lm.setCounter( lm.getCounter() + 1 ); // we need this to track when we unlink
//                } else {
//      
//                }
                sink.assertLeftTuple( newLeftTuple,
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
    
    public boolean isStagedForModifyRight(final RightTuple rightTuple, 
                                          final BetaMemory bm,
                                          final PropagationContext context,
                                          final InternalWorkingMemory wm ) {
        if ( !bm.getSegmentMemory().isActive() ) {
            if ( !rightTuple.getMemory().isStagingMemory() ) {
                // if not already staged, then stage it
                bm.getRightTupleMemory().remove( rightTuple );
               // bm.getSegmentMemory().addModifyRightTuple( rightTuple, wm );
            }                       
            return true;
        } 
        
        return false;
    }
    
    public boolean isStagedForAssertLeft(final LeftTuple leftTuple, 
                                         final BetaMemory bm,
                                         final PropagationContext context,
                                         final InternalWorkingMemory wm ) {
        if ( !bm.getSegmentMemory().isActive() ) {
            bm.getSegmentMemory().addAssertLeftTuple( leftTuple, wm );
            return true;
        }
        
        return false;
    }
    
    
    public boolean isStagedForModifyLeft(final LeftTuple leftTuple, 
                                         final BetaMemory bm,
                                         final PropagationContext context,
                                         final InternalWorkingMemory wm ) {
//        if ( !bm.getSegmentMemory().isActive() ) {
//            if ( leftTuple.getMemory() != null && !leftTuple.getMemory().isStagingMemory() ) {
//                // if not already staged, then stage it
//                bm.getLeftTupleMemory().remove( leftTuple );
//                bm.getSegmentMemory().addModifyLeftTuple( leftTuple, wm );
//            }             
//            return true;
//        }
        
        return false;
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

    public int getLeftInputOtnId() {
        return leftInputOtnId;
    }

    public void setLeftInputOtnId(int leftInputOtnId) {
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
}
