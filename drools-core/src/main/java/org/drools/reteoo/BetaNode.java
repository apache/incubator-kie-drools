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

import static org.drools.core.util.BitMaskUtil.intersect;
import static org.drools.core.util.ClassUtils.areNullSafeEquals;
import static org.drools.reteoo.PropertySpecificUtil.calculateNegativeMask;
import static org.drools.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.reteoo.PropertySpecificUtil.getSettableProperties;
import static org.drools.reteoo.PropertySpecificUtil.isPropertyReactive;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.drools.RuleBaseConfiguration;
import org.drools.base.ClassObjectType;
import org.drools.common.BaseNode;
import org.drools.common.BetaConstraints;
import org.drools.common.DoubleBetaConstraints;
import org.drools.common.DoubleNonIndexSkipBetaConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.Memory;
import org.drools.common.MemoryFactory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.QuadroupleBetaConstraints;
import org.drools.common.QuadroupleNonIndexSkipBetaConstraints;
import org.drools.common.RuleBasePartitionId;
import org.drools.common.SingleBetaConstraints;
import org.drools.common.SingleNonIndexSkipBetaConstraints;
import org.drools.common.TripleBetaConstraints;
import org.drools.common.TripleNonIndexSkipBetaConstraints;
import org.drools.common.UpdateContext;
import org.drools.core.util.FastIterator;
import org.drools.core.util.index.IndexUtil;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.core.util.index.RightTupleList;
import org.drools.phreak.SegmentUtilities;
import org.drools.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.IndexableConstraint;
import org.drools.rule.Pattern;
import org.drools.spi.BetaNodeFieldConstraint;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;
import org.kie.builder.conf.LRUnlinkingOption;

/**
 * <code>BetaNode</code> provides the base abstract class for <code>JoinNode</code> and <code>NotNode</code>. It implements
 * both TupleSink and ObjectSink and as such can receive <code>Tuple</code>s and <code>FactHandle</code>s. BetaNode uses BetaMemory
 * to store the propagated instances.
 *
 * @see org.kie.reteoo.LeftTupleSource
 * @see org.kie.reteoo.LeftTupleSink
 * @see org.kie.reteoo.BetaMemory
 */
public abstract class BetaNode extends LeftTupleSource
        implements
        LeftTupleSinkNode,
        ObjectSinkNode,
        RightTupleSink,
        MemoryFactory {

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------
    /** The right input <code>TupleSource</code>. */
    protected ObjectSource    rightInput;

    protected BetaConstraints constraints;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    private ObjectSinkNode    previousObjectSinkNode;
    private ObjectSinkNode    nextObjectSinkNode;

    protected boolean         objectMemory               = true; // hard coded to true
    protected boolean         tupleMemoryEnabled;
    protected boolean         concurrentRightTupleMemory = false;


    private boolean           indexedUnificationJoin;

    private long              rightDeclaredMask;
    private long              rightInferredMask;
    private long              rightNegativeMask;

    private List<String>      leftListenedProperties;
    private List<String>      rightListenedProperties;

    private transient int     rightInputOtnId;
    
    private boolean           rightInputIsRiaNode;

    private transient ObjectTypeNode objectTypeNode;

    private boolean                  unlinkingEnabled;

    private int                      unlinkedDisabledCount;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public BetaNode() {

    }

    /**
     * Constructs a <code>BetaNode</code> using the specified <code>BetaNodeBinder</code>.
     *
     * @param leftInput
     *            The left input <code>TupleSource</code>.
     * @param rightInput
     *            The right input <code>ObjectSource</code>.
     */
    BetaNode(final int id,
             final RuleBasePartitionId partitionId,
             final boolean partitionsEnabled,
             final LeftTupleSource leftInput,
             final ObjectSource rightInput,
             final BetaConstraints constraints,
             final BuildContext context) {
        super( id,
                partitionId,
                partitionsEnabled );
        setLeftTupleSource(leftInput);
        this.rightInput = rightInput;
        
        if ( NodeTypeEnums.RightInputAdaterNode == rightInput.getType() ) {
            rightInputIsRiaNode = true;
        } else {
            rightInputIsRiaNode = false;
        }
        
        this.constraints = constraints;

        if ( this.constraints == null ) {
            throw new RuntimeException( "cannot have null constraints, must at least be an instance of EmptyBetaConstraints" );
        }

        initMasks( context, leftInput );        
        
        this.unlinkingEnabled = context.getRuleBase().getConfiguration().isUnlinkingEnabled();      
        this.unlinkedDisabledCount = 0;
    }
    
    public boolean isUnlinkingEnabled() {
        return unlinkingEnabled;
    }

    public void setUnlinkingEnabled(boolean unlinkingEnabled) {
        this.unlinkingEnabled = unlinkingEnabled;
    }
    
    public int getUnlinkedDisabledCount() {
        return unlinkedDisabledCount;
    }

    public void setUnlinkedDisabledCount(int unlinkedDisabledCount) {
        this.unlinkedDisabledCount = unlinkedDisabledCount;
    }

    @Override
    protected void initDeclaredMask(BuildContext context,
                                    LeftTupleSource leftInput) {
        if ( context == null || context.getLastBuiltPatterns() == null ) {
            // only happens during unit tests
            rightDeclaredMask = Long.MAX_VALUE;
            super.initDeclaredMask( context, leftInput );
            return;
        }

        if ( !isRightInputIsRiaNode() ) {
            Pattern pattern = context.getLastBuiltPatterns()[0]; // right input pattern
            ObjectType objectType = pattern.getObjectType();

            if ( objectType instanceof ClassObjectType ) {
                Class objectClass = ((ClassObjectType) objectType).getClassType();
                if ( isPropertyReactive(context, objectClass) ) {
                    rightListenedProperties = pattern.getListenedProperties();
                    List<String> settableProperties = getSettableProperties( context.getRuleBase(), objectClass );
                    rightDeclaredMask = calculatePositiveMask(rightListenedProperties, settableProperties );
                    rightDeclaredMask |= constraints.getListenedPropertyMask( settableProperties );
                    rightNegativeMask = calculateNegativeMask(rightListenedProperties, settableProperties );
                } else {
                    // if property reactive is not on, then accept all modification propagations
                    rightDeclaredMask = Long.MAX_VALUE;
                }
            } else {
                // InitialFact has no type declaration and cannot be property specific
                // Only ClassObjectType can use property specific
                rightDeclaredMask = Long.MAX_VALUE;
            }
        } else {
            rightDeclaredMask = Long.MAX_VALUE;
            // There would have been no right input pattern, so swap current to first, so leftInput can still work
            context.setLastBuiltPattern( context.getLastBuiltPatterns()[0] );
        }

        super.initDeclaredMask(context, leftInput);
    }

    protected void setLeftListenedProperties(List<String> leftListenedProperties) {
        this.leftListenedProperties = leftListenedProperties;
    }

    public void initInferredMask() {
        initInferredMask( leftInput );
    }

    @Override
    protected void initInferredMask(LeftTupleSource leftInput) {
        super.initInferredMask( leftInput );

        ObjectSource unwrappedRight = unwrapRightInput();
        if ( unwrappedRight.getType() == NodeTypeEnums.AlphaNode ) {
            AlphaNode alphaNode = (AlphaNode) unwrappedRight;
            rightInferredMask = alphaNode.updateMask( rightDeclaredMask );
        } else {
            rightInferredMask = rightDeclaredMask;
        }
        rightInferredMask &= (Long.MAX_VALUE - rightNegativeMask);
    }

    public ObjectSource unwrapRightInput() {   
        return rightInput.getType() == NodeTypeEnums.PropagationQueuingNode ? rightInput.getParentObjectSource() : rightInput;
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        constraints = (BetaConstraints) in.readObject();
        rightInput = (ObjectSource) in.readObject();
        objectMemory = in.readBoolean();
        tupleMemoryEnabled = in.readBoolean();
        concurrentRightTupleMemory = in.readBoolean();
        unlinkingEnabled = in.readBoolean();
        unlinkedDisabledCount = in.readInt();
        rightDeclaredMask = in.readLong();
        rightInferredMask = in.readLong();
        rightNegativeMask = in.readLong();
        leftListenedProperties = (List) in.readObject();
        rightListenedProperties = (List) in.readObject();
        setUnificationJoin();
        super.readExternal( in );
        if ( NodeTypeEnums.RightInputAdaterNode == rightInput.getType() ) {
            rightInputIsRiaNode = true;
        } else {
            rightInputIsRiaNode = false;
        }        
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        BetaNodeFieldConstraint[] betaCconstraints = this.constraints.getConstraints();
        if ( betaCconstraints.length > 0 ) {
            BetaNodeFieldConstraint c = betaCconstraints[0];
            if ( IndexUtil.isIndexable(c, getType()) && ((IndexableConstraint) c).isUnification() ) {
                this.constraints = this.constraints.getOriginalConstraint();
            }
        }

        out.writeObject( constraints );
        out.writeObject( rightInput );
        out.writeBoolean( objectMemory );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeBoolean( concurrentRightTupleMemory );
        out.writeBoolean( unlinkingEnabled );
        out.writeInt( unlinkedDisabledCount );
        out.writeLong( rightDeclaredMask );
        out.writeLong( rightInferredMask );
        out.writeLong( rightNegativeMask );
        out.writeObject( leftListenedProperties );
        out.writeObject( rightListenedProperties );
        super.writeExternal( out );
    }
    
    public void setUnificationJoin() {
        // If this join uses a indexed, ==, constraint on a query parameter then set indexedUnificationJoin to true
        // This ensure we get the correct iterator
        BetaNodeFieldConstraint[] betaCconstraints = this.constraints.getConstraints();
        if ( betaCconstraints.length > 0 ) {
            BetaNodeFieldConstraint c = betaCconstraints[0];
            if ( IndexUtil.isIndexable(c, getType()) && ((IndexableConstraint) c).isUnification() ) {
                if ( this.constraints instanceof SingleBetaConstraints ) {
                    this.constraints = new SingleNonIndexSkipBetaConstraints( (SingleBetaConstraints) this.constraints );
                } else if ( this.constraints instanceof DoubleBetaConstraints ) {
                    this.constraints = new DoubleNonIndexSkipBetaConstraints( (DoubleBetaConstraints) this.constraints );
                } else if ( this.constraints instanceof TripleBetaConstraints ) {
                    this.constraints = new TripleNonIndexSkipBetaConstraints( (TripleBetaConstraints) this.constraints );
                } else if ( this.constraints instanceof QuadroupleBetaConstraints ) {
                    this.constraints = new QuadroupleNonIndexSkipBetaConstraints( (QuadroupleBetaConstraints) this.constraints );
                }

                this.indexedUnificationJoin = true;
            }
        }
    }    

    public void assertObject( final InternalFactHandle factHandle,
                              final PropagationContext context,
                              final InternalWorkingMemory wm ) {
        final BetaMemory memory = (BetaMemory) getBetaMemoryFromRightInput(this, wm);

        RightTuple rightTuple = createRightTuple( factHandle,
                                                  this,
                                                  context );
        rightTuple.setPropagationContext( context );
        
        if ( isUnlinkingEnabled() ) {            
            if (  memory.getStagedRightTuples().insertSize() == 0 && !isRightInputIsRiaNode() ) {
                // link node. Ignore right input adapters, as these will link the betanode via the RiaRuleSegments
                // Even if rule is already linked, still call this in case the lazy agenda item needs re-activating
                memory.linkNode( wm );
            }
                
            memory.getAndIncCounter();
            memory.getStagedRightTuples().addInsert( rightTuple );  
            return;
        }
        
        assertRightTuple(rightTuple, context, wm );
        
    }    
    
    public abstract void assertRightTuple( final RightTuple rightTuple,
                                           final PropagationContext context,
                                           final InternalWorkingMemory workingMemory );    

    public static void flushModifyStagedRightTuples(RightTupleList list, InternalWorkingMemory wm) {        
        // propagateRightTuples((BetaNode) list.getFirst().getRightTupleSink(), list, list.size(), wm);
        
        RightTuple rightTuple = list.getFirst();
        BetaNode bnode = (BetaNode) list.getFirst().getRightTupleSink();
        for ( int i = 0, length = list.size(); i < length; i++ ) {  
            RightTuple next =   ( RightTuple ) rightTuple.getNext();
            
            rightTuple.setPrevious( null );
            rightTuple.setNext( null );

            bnode.modifyRightTuple( rightTuple, rightTuple.getPropagationContext(), wm );
            //betaNode.assertRightTuple( rightTuple, rightTuple.getPropagationContext(), wm );
            rightTuple.getPropagationContext().evaluateActionQueue( wm );
            rightTuple = next;
        }                
    }
    
    public static RightTuple propagateAssertRightTuples(BetaNode betaNode, RightTupleList list, int length, InternalWorkingMemory wm) {
        RightTuple rightTuple = list.getFirst();
        for ( int i = 0; i < length; i++ ) {  
            RightTuple next =   ( RightTuple ) rightTuple.getNext();
            
            rightTuple.setPrevious( null );
            rightTuple.setNext( null );
            
            betaNode.assertRightTuple( rightTuple, rightTuple.getPropagationContext(), wm );
            rightTuple.getPropagationContext().evaluateActionQueue( wm );
            rightTuple = next;
        }        
        
        return rightTuple;
    }   
    
//    public static RightTuple propagateRetractRightTuples(BetaNode betaNode, RightTupleList list, InternalWorkingMemory wm) {
//        RightTuple rightTuple = list.getFirst();
//        for ( int i = 0; i < length; i++ ) {  
//            RightTuple next =   ( RightTuple ) rightTuple.getNext();
//            
//            rightTuple.setPrevious( null );
//            rightTuple.setNext( null );
//            
//            betaNode.assertRightTuple( rightTuple, rightTuple.getPropagationContext(), wm );
//            rightTuple.getPropagationContext().evaluateActionQueue( wm );
//            rightTuple = next;
//        }        
//        
//        return rightTuple;
//    }     
    


    public boolean isRightInputIsRiaNode() {
        return rightInputIsRiaNode;
    }

    public ObjectSource getRightInput() {
        return this.rightInput;
    }
    
    public FastIterator getRightIterator(RightTupleMemory memory) {
        if ( !this.indexedUnificationJoin ) {
            return memory.fastIterator();
        } else {
            return memory.fullFastIterator();
        }
    }

    public FastIterator getLeftIterator(LeftTupleMemory memory) {
        if ( !this.indexedUnificationJoin ) {
            return memory.fastIterator();
        } else {
            return memory.fullFastIterator();
        }
    }

    public RightTuple getFirstRightTuple(final LeftTuple leftTuple,
                                         final RightTupleMemory memory,
                                         final PropagationContext context,
                                         final FastIterator it) {
        if ( !this.indexedUnificationJoin ) {
            return memory.getFirst( leftTuple, (InternalFactHandle) context.getFactHandle(), it );
        } else {
            return (RightTuple) it.next( null );
        }
    }

    public LeftTuple getFirstLeftTuple(final RightTuple rightTuple,
                                       final LeftTupleMemory memory,
                                       final PropagationContext context,
                                       final FastIterator it) {
        if ( !this.indexedUnificationJoin ) {
            return memory.getFirst( rightTuple );
        } else {
            return (LeftTuple) it.next( null );
        }
    }

    public static RightTuple getFirstRightTuple(final RightTupleMemory memory,
                                                final FastIterator it) {
        if ( !memory.isIndexed() ) {
            return memory.getFirst( null, null, it );
        } else {
            return (RightTuple) it.next( null );
        }
    }

    public static LeftTuple getFirstLeftTuple(final LeftTupleMemory memory,
                                              final FastIterator it) {
        if ( !memory.isIndexed() ) {
            return memory.getFirst( null );
        } else {
            return (LeftTuple) it.next( null );
        }
    }

    public BetaNodeFieldConstraint[] getConstraints() {
        return constraints.getConstraints();
    }

    public BetaConstraints getRawConstraints() {
        return this.constraints;
    }
    
    public void setConstraints(BetaConstraints constraints) {
        this.constraints = constraints;
    }
    

    public void networkUpdated(UpdateContext updateContext) {
        updateContext.startVisitNode( leftInput );
        rightInput.networkUpdated( updateContext );
        updateContext.endVisit();
        if ( !updateContext.isVisiting( leftInput ) ) {
            leftInput.networkUpdated( updateContext );
        }
    }

    public List<String> getRules() {
        final List<String> list = new ArrayList<String>();

        final LeftTupleSink[] sinks = this.sink.getSinks();
        for ( int i = 0, length = sinks.length; i < length; i++ ) {
            if ( sinks[i].getType() ==  NodeTypeEnums.RuleTerminalNode ) {
                list.add( ((RuleTerminalNode) sinks[i]).getRule().getName() );
            } else if ( NodeTypeEnums.isBetaNode( sinks[i] ) ) {
                list.addAll( ((BetaNode) sinks[i]).getRules() );
            }
        }
        return list;
    }

    protected ObjectTypeNode getObjectTypeNode() {
        if (objectTypeNode == null) {
            ObjectSource source = this.rightInput;
            while ( source != null ) {
                if ( source instanceof ObjectTypeNode ) {
                    objectTypeNode = (ObjectTypeNode) source;
                    break;
                }
                source = source.source;
            }
        }
        return objectTypeNode;
    }

    public void attach(BuildContext context) {
        constraints.init(context, getType());
        setUnificationJoin();

        this.rightInput.addObjectSink( this );
        this.leftInput.addTupleSink( this, context );

        if (context == null) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            final PropagationContext propagationContext = new PropagationContextImpl(workingMemory.getNextPropagationIdCounter(),
                    PropagationContext.RULE_ADDITION,
                    null,
                    null,
                    null);

            /* FIXME: This should be generalized at BetaNode level and the
             * instanceof should be removed!
             *
             * When L&R Unlinking is enabled, we only need to update the side
             * that is initially linked. If there are tuples to be propagated,
             * they will trigger the update (thus, population) of the other side.
             * */
            if (!unlinkingEnabled || !(this.getType() == NodeTypeEnums.JoinNode) ) {
                this.rightInput.updateSink(this,
                        propagationContext,
                        workingMemory);
            }

            this.leftInput.updateSink(this,
                    propagationContext,
                    workingMemory);
        }

    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        if ( !node.isInUse() ) {
            removeTupleSink( (LeftTupleSink) node );
        }
        if ( !this.isInUse() || context.getCleanupAdapter() != null ) {
            for (InternalWorkingMemory workingMemory : workingMemories) {
                BetaMemory memory;
                Object object = workingMemory.getNodeMemory(this);

                // handle special cases for Accumulate to make sure they tidy up their specific data
                // like destroying the local FactHandles
                if (object instanceof AccumulateMemory) {
                    memory = ((AccumulateMemory) object).betaMemory;
                } else {
                    memory = (BetaMemory) object;
                }

                FastIterator it = memory.getLeftTupleMemory().fullFastIterator();
                for (LeftTuple leftTuple = getFirstLeftTuple(memory.getLeftTupleMemory(), it); leftTuple != null; ) {
                    LeftTuple tmp = (LeftTuple) it.next(leftTuple);
                    if (context.getCleanupAdapter() != null) {
                        LeftTuple child;
                        while ( (child = leftTuple.getFirstChild()) != null ) {
                            if (child.getLeftTupleSink() == this) {
                                // this is a match tuple on collect and accumulate nodes, so just unlink it
                                child.unlinkFromLeftParent();
                                child.unlinkFromRightParent();
                            } else {
                                // the cleanupAdapter will take care of the unlinking
                                context.getCleanupAdapter().cleanUp(child, workingMemory);
                            }
                        }
                    }
                    memory.getLeftTupleMemory().remove(leftTuple);
                    leftTuple.unlinkFromLeftParent();
                    leftTuple.unlinkFromRightParent();
                    leftTuple = tmp;
                }

                // handle special cases for Accumulate to make sure they tidy up their specific data
                // like destroying the local FactHandles
                if (object instanceof AccumulateMemory) {
                    ((AccumulateNode) this).doRemove(workingMemory, (AccumulateMemory) object);
                }

                if (!this.isInUse()) {
                    it = memory.getRightTupleMemory().fullFastIterator();
                    for (RightTuple rightTuple = getFirstRightTuple(memory.getRightTupleMemory(), it); rightTuple != null; ) {
                        RightTuple tmp = (RightTuple) it.next(rightTuple);
                        if (rightTuple.getBlocked() != null) {
                            // special case for a not, so unlink left tuple from here, as they aren't in the left memory
                            for (LeftTuple leftTuple = rightTuple.getBlocked(); leftTuple != null; ) {
                                LeftTuple temp = leftTuple.getBlockedNext();

                                leftTuple.setBlocker(null);
                                leftTuple.setBlockedPrevious(null);
                                leftTuple.setBlockedNext(null);
                                leftTuple.unlinkFromLeftParent();
                                leftTuple = temp;
                            }
                        }
                        memory.getRightTupleMemory().remove(rightTuple);
                        rightTuple.unlinkFromRightParent();
                        rightTuple = tmp;
                    }
                    workingMemory.clearNodeMemory(this);
                }
            }
            context.setCleanupAdapter( null );
        }

        handleUnlinking(context);

        this.rightInput.remove( context,
                                builder,
                                this,
                                workingMemories );

this.leftInput.remove( context,
                       builder,
                       this,
                       workingMemories );
    }
    
    public void handleUnlinking(final RuleRemovalContext context) {
        if ( !context.isUnlinkEnabled( )  && unlinkedDisabledCount == 0) {
            // if unlinkedDisabledCount is 0, then we know that unlinking is disabled globally
            return;
        }
        
        if ( context.isUnlinkEnabled( ) ) {
            unlinkedDisabledCount--;
            if ( unlinkedDisabledCount == 0 ) {
                unlinkingEnabled = true;
            }
        }
        
    }

    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory wm) {
        RightTuple rightTuple = modifyPreviousTuples.peekRightTuple();
        


        // if the peek is for a different OTN we assume that it is after the current one and then this is an assert
        while ( rightTuple != null &&
                ((BetaNode) rightTuple.getRightTupleSink()).getRightInputOtnId() < getRightInputOtnId() ) {
            modifyPreviousTuples.removeRightTuple();
            rightTuple.setPropagationContext( context );
            // we skipped this node, due to alpha hashing, so retract now

            rightTuple.getRightTupleSink().retractRightTuple( rightTuple,
                                                              context,
                                                              wm );
            rightTuple = modifyPreviousTuples.peekRightTuple();
        }

        if ( rightTuple != null && ((BetaNode) rightTuple.getRightTupleSink()).getRightInputOtnId() == getRightInputOtnId() ) {
            modifyPreviousTuples.removeRightTuple();
            rightTuple.reAdd();
            rightTuple.setPropagationContext( context );
            if ( intersect( context.getModificationMask(), rightInferredMask ) ) {
                // RightTuple previously existed, so continue as modify                
                modifyRightTuple( rightTuple,
                                  context,
                                  wm );     
                
//                if ( rightTuple.getMemory() != null && rightTuple.getMemory().isStagingMemory() ) { // can be null for if unlinking is off
//                    // RightTuple is still staged, hasn't propagated yet, just up date PropagationContext
//                    rightTuple.setPropagationContext( context );                    
//                } else {
//                    if ( isUnlinkingEnabled() ) {
//                        SegmentMemory sm;
//                        if ( getType() == NodeTypeEnums.AccumulateNode ) {
//                            sm = ((AccumulateMemory)wm.getNodeMemory( this )).getBetaMemory().getSegmentMemory();
//                        } else {
//                            sm = ((BetaMemory)wm.getNodeMemory( this )).getSegmentMemory();
//                        }
//                        //remove from main memory and stage
//                        rightTuple.getMemory().remove( rightTuple );
//                        sm.addModifyRightTuple( rightTuple, wm );
//                    } else {
//                        modifyRightTuple( rightTuple,
//                                          context,
//                                          wm );                        
//                    }
//                }
            }
        } else {
            if ( intersect( context.getModificationMask(), rightInferredMask ) ) {
                // RightTuple does not exist for this node, so create and continue as assert
                assertObject( factHandle,
                              context,
                              wm );
            }
        }
    }
    
    public void byPassModifyToBetaNode (final InternalFactHandle factHandle,
                                        final ModifyPreviousTuples modifyPreviousTuples,
                                        final PropagationContext context,
                                        final InternalWorkingMemory workingMemory) {
        modifyObject( factHandle, modifyPreviousTuples, context, workingMemory );
    }

    public boolean isObjectMemoryEnabled() {
        return objectMemory;
    }

    public void setObjectMemoryEnabled(boolean objectMemory) {
        this.objectMemory = objectMemory;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    public boolean isConcurrentRightTupleMemory() {
        return concurrentRightTupleMemory;
    }

    public void setConcurrentRightTupleMemory(boolean concurrentRightTupleMemory) {
        this.concurrentRightTupleMemory = concurrentRightTupleMemory;
    }
    
    public Memory createMemory(RuleBaseConfiguration config) {
        return constraints.createBetaMemory(config, getType());
    }

    public String toString() {
        return "[ " + this.getClass().getSimpleName() + "(" + this.id + ") ]";
    }

    public void dumpMemory(final InternalWorkingMemory workingMemory) {
        final MemoryVisitor visitor = new MemoryVisitor( workingMemory );
        visitor.visit( this );
    }

    public LeftTupleSource getLeftTupleSource() {
        return this.leftInput;
    }

    /* (non-Javadoc)
     * @see org.kie.reteoo.BaseNode#hashCode()
     */
    public int hashCode() {
        int hash = ( 23 * leftInput.hashCode() ) + ( 29 * rightInput.hashCode() ) + ( 31 * constraints.hashCode() );
        if (leftListenedProperties != null) {
            hash += 37 * leftListenedProperties.hashCode();
        }
        if (rightListenedProperties != null) {
            hash += 41 * rightListenedProperties.hashCode();
        }
        return hash;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof BetaNode) ) {
            return false;
        }

        final BetaNode other = (BetaNode) object;

        return this.getClass() == other.getClass() &&
                this.leftInput.equals( other.leftInput ) &&
                this.rightInput.equals( other.rightInput ) &&
                this.constraints.equals( other.constraints ) &&
                areNullSafeEquals(this.leftListenedProperties, other.leftListenedProperties) &&
                areNullSafeEquals(this.rightListenedProperties, other.rightListenedProperties);
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode(final LeftTupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    /**
     * Returns the next node
     * @return
     *      The next ObjectSinkNode
     */
    public ObjectSinkNode getNextObjectSinkNode() {
        return this.nextObjectSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next ObjectSinkNode
     */
    public void setNextObjectSinkNode(final ObjectSinkNode next) {
        this.nextObjectSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous ObjectSinkNode
     */
    public ObjectSinkNode getPreviousObjectSinkNode() {
        return this.previousObjectSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous ObjectSinkNode
     */
    public void setPreviousObjectSinkNode(final ObjectSinkNode previous) {
        this.previousObjectSinkNode = previous;
    }

    public RightTuple createRightTuple(InternalFactHandle handle,
                                       RightTupleSink sink,
                                       PropagationContext context) {
        if ( context.getActiveWindowTupleList() == null ) {
            return new RightTuple( handle,
                                   sink );
        } else {
            return new WindowTuple( handle,
                                    sink,
                                    context.getActiveWindowTupleList() );
        }
    }
    
    public static Object getBetaMemoryFromRightInput( final BetaNode betaNode, final InternalWorkingMemory workingMemory ) {        
        BetaMemory memory;
        if ( NodeTypeEnums.AccumulateNode == betaNode.getType()) {
            memory = ((AccumulateMemory)workingMemory.getNodeMemory( betaNode )).getBetaMemory();
        } else {
            memory = (BetaMemory) workingMemory.getNodeMemory( betaNode );
        }
        
        
        if ( betaNode.isUnlinkingEnabled() && memory.getSegmentMemory() == null ) {
            SegmentUtilities.createSegmentMemory( betaNode, workingMemory ); // initialises for all nodes in segment, including this one
        }
        return memory;
    }
    
    public static boolean parentInSameSegment(LeftTupleSource lt) {
        LeftTupleSource parent = lt.getLeftTupleSource();        
        if ( parent != null && ( parent.getSinkPropagator().size() == 1 || 
               // same segment, if it's a subnetwork split and we are on the non subnetwork side of the split
             ( parent.getSinkPropagator().size() == 2 && 
               NodeTypeEnums.isBetaNode( lt ) &&
               ((BetaNode)lt).isRightInputIsRiaNode() ) ) ) {
            return true;
        } else {        
            return false;
        }
    }
    
    public long getRightDeclaredMask() {
        return rightDeclaredMask;
    }

    public void setRightDeclaredMask(long rightDeclaredMask) {
        this.rightDeclaredMask = rightDeclaredMask;
    }

    public long getRightInferredMask() {
        return rightInferredMask;
    }

    public long getRightNegativeMask() {
        return rightNegativeMask;
    }

    public int getRightInputOtnId() {
        return rightInputOtnId;
    }

    public void setRightInputOtnId(int rightInputOtnId) {
        this.rightInputOtnId = rightInputOtnId;
    }
}
