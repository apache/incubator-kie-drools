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

package org.drools.core.reteoo;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.core.common.DoubleNonIndexSkipBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.PhreakPropagationContext;
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.core.common.QuadroupleNonIndexSkipBetaConstraints;
import org.drools.core.common.RightTupleSets;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.common.SingleNonIndexSkipBetaConstraints;
import org.drools.core.common.TripleBetaConstraints;
import org.drools.core.common.TripleNonIndexSkipBetaConstraints;
import org.drools.core.common.UpdateContext;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.phreak.RightTupleEntry;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.IndexableConstraint;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.FastIterator;
import org.drools.core.util.index.IndexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import static org.drools.core.phreak.AddRemoveRule.forceFlushLeftTuple;
import static org.drools.core.reteoo.PropertySpecificUtil.*;
import static org.drools.core.util.BitMaskUtil.intersect;
import static org.drools.core.util.ClassUtils.areNullSafeEquals;

public abstract class BetaNode extends LeftTupleSource
        implements
        LeftTupleSinkNode,
        ObjectSinkNode,
        RightTupleSink,
        MemoryFactory {

    protected static final Logger log = LoggerFactory.getLogger(BetaNode.class);
    protected static final boolean isLogTraceEnabled = log.isTraceEnabled();

    protected ObjectSource rightInput;

    protected BetaConstraints constraints;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    private ObjectSinkNode previousObjectSinkNode;
    private ObjectSinkNode nextObjectSinkNode;

    protected boolean objectMemory = true; // hard coded to true
    protected boolean tupleMemoryEnabled;

    protected boolean indexedUnificationJoin;

    private long rightDeclaredMask;
    private long rightInferredMask;
    private long rightNegativeMask;

    private List<String> leftListenedProperties;
    private List<String> rightListenedProperties;

    private transient ObjectTypeNode.Id rightInputOtnId = ObjectTypeNode.DEFAULT_ID;

    private boolean rightInputIsRiaNode;

    private transient ObjectTypeNode objectTypeNode;

    private boolean rightInputIsPassive;

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
             final LeftTupleSource leftInput,
             final ObjectSource rightInput,
             final BetaConstraints constraints,
             final BuildContext context) {
        super(id, context);
        setLeftTupleSource(leftInput);
        this.rightInput = rightInput;

        rightInputIsRiaNode = NodeTypeEnums.RightInputAdaterNode == rightInput.getType();

        setConstraints(constraints);

        if (this.constraints == null) {
            throw new RuntimeException("cannot have null constraints, must at least be an instance of EmptyBetaConstraints");
        }

        initMasks(context, leftInput);

        streamMode = context.isStreamMode() && getObjectTypeNode(context).getObjectType().isEvent();
    }

    private ObjectTypeNode getObjectTypeNode(BuildContext context) {
        ObjectTypeNode otn = getObjectTypeNode();
        // getObjectTypeNode() can return null if the BetaNode is in a subnetwork
        return otn != null ? otn : context.getRootObjectTypeNode();
    }

    @Override
    protected void initDeclaredMask(BuildContext context,
                                    LeftTupleSource leftInput) {
        if (context == null || context.getLastBuiltPatterns() == null) {
            // only happens during unit tests
            rightDeclaredMask = -1L;
            super.initDeclaredMask(context, leftInput);
            return;
        }

        if (!isRightInputIsRiaNode()) {
            Pattern pattern = context.getLastBuiltPatterns()[0]; // right input pattern
            rightInputIsPassive = pattern.isPassive();
            ObjectType objectType = pattern.getObjectType();

            if (objectType instanceof ClassObjectType) {
                Class objectClass = ((ClassObjectType) objectType).getClassType();
                if (isPropertyReactive(context, objectClass)) {
                    rightListenedProperties = pattern.getListenedProperties();
                    List<String> settableProperties = getSettableProperties(context.getKnowledgeBase(), objectClass);
                    rightDeclaredMask = calculatePositiveMask(rightListenedProperties, settableProperties);
                    rightDeclaredMask |= constraints.getListenedPropertyMask(settableProperties);
                    rightNegativeMask = calculateNegativeMask(rightListenedProperties, settableProperties);
                } else {
                    // if property reactive is not on, then accept all modification propagations
                    rightDeclaredMask = -1L;
                }
            } else {
                // InitialFact has no type declaration and cannot be property specific
                // Only ClassObjectType can use property specific
                rightDeclaredMask = -1L;
            }
        } else {
            rightDeclaredMask = -1L;
            // There would have been no right input pattern, so swap current to first, so leftInput can still work
            context.setLastBuiltPattern( context.getLastBuiltPatterns()[0] );
        }

        super.initDeclaredMask(context, leftInput);
    }

    protected void setLeftListenedProperties(List<String> leftListenedProperties) {
        this.leftListenedProperties = leftListenedProperties;
    }

    public void initInferredMask() {
        initInferredMask(leftInput);
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
        rightInferredMask &= (-1L - rightNegativeMask);
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
        rightDeclaredMask = in.readLong();
        rightInferredMask = in.readLong();
        rightNegativeMask = in.readLong();
        leftListenedProperties = (List) in.readObject();
        rightListenedProperties = (List) in.readObject();
        rightInputIsPassive = in.readBoolean();
        setUnificationJoin();
        super.readExternal( in );
        rightInputIsRiaNode = NodeTypeEnums.RightInputAdaterNode == rightInput.getType();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        BetaNodeFieldConstraint[] betaCconstraints = this.constraints.getConstraints();
        if ( betaCconstraints.length > 0 ) {
            BetaNodeFieldConstraint c = betaCconstraints[0];
            if ( IndexUtil.isIndexable(c, getType()) && ((IndexableConstraint) c).isUnification() ) {
                setConstraints( this.constraints.getOriginalConstraint() );
            }
        }

        out.writeObject( constraints );
        out.writeObject(rightInput);
        out.writeBoolean( objectMemory );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeLong( rightDeclaredMask );
        out.writeLong( rightInferredMask );
        out.writeLong( rightNegativeMask );
        out.writeObject(leftListenedProperties);
        out.writeObject(rightListenedProperties);
        out.writeBoolean(rightInputIsPassive);
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
                    setConstraints( new SingleNonIndexSkipBetaConstraints( (SingleBetaConstraints) this.constraints ) );
                } else if ( this.constraints instanceof DoubleBetaConstraints ) {
                    setConstraints( new DoubleNonIndexSkipBetaConstraints( (DoubleBetaConstraints) this.constraints ) );
                } else if ( this.constraints instanceof TripleBetaConstraints ) {
                    setConstraints( new TripleNonIndexSkipBetaConstraints( (TripleBetaConstraints) this.constraints ) );
                } else if ( this.constraints instanceof QuadroupleBetaConstraints ) {
                    setConstraints( new QuadroupleNonIndexSkipBetaConstraints( (QuadroupleBetaConstraints) this.constraints ) );
                }

                this.indexedUnificationJoin = true;
            }
        }
    }

    public void assertObject( final InternalFactHandle factHandle,
                              final PropagationContext pctx,
                              final InternalWorkingMemory wm ) {
        final BetaMemory memory = (BetaMemory) getBetaMemoryFromRightInput(this, wm);

        RightTuple rightTuple = createRightTuple( factHandle, this, pctx );

        boolean stagedInsertWasEmpty = false;
        if ( streamMode ) {
            int propagationType = pctx.getType() == PropagationContext.MODIFICATION ? PropagationContext.INSERTION : pctx.getType();
            stagedInsertWasEmpty = memory.getSegmentMemory().getStreamQueue().addInsert(new RightTupleEntry(rightTuple, pctx, memory, propagationType));
            if ( isLogTraceEnabled ) {
                log.trace( "JoinNode insert queue={} size={} pctx={} lt={}", System.identityHashCode( memory.getSegmentMemory().getStreamQueue() ), memory.getSegmentMemory().getStreamQueue().size(), PhreakPropagationContext.intEnumToString(pctx), rightTuple );
            }
        }  else {
            stagedInsertWasEmpty = memory.getStagedRightTuples().addInsert(rightTuple);
        }
        if ( isLogTraceEnabled ) {
            log.trace("BetaNode insert={} stagedInsertWasEmpty={}",  memory.getStagedRightTuples().insertSize(), stagedInsertWasEmpty );
        }
        if ( memory.getAndIncCounter() == 0 ) {
            memory.linkNode(wm, !rightInputIsPassive);
        } else if ( stagedInsertWasEmpty ) {
            memory.setNodeDirty( wm, !rightInputIsPassive );
        }

        PathMemory pmem = memory.getSegmentMemory().getFirstDataDrivenPathMemory();
        if (pmem != null) {
            forceFlushLeftTuple(pmem, memory.getSegmentMemory(), wm, null);
            return;
        }

        if( pctx.getReaderContext() != null ) {
            // we are deserializing a session, so we might need to evaluate
            // rule activations immediately
            MarshallerReaderContext mrc = pctx.getReaderContext();
            mrc.filter.fireRNEAs( wm );
        }
    }

    public void modifyObject(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory wm) {
        RightTuple rightTuple = modifyPreviousTuples.peekRightTuple();

        // if the peek is for a different OTN we assume that it is after the current one and then this is an assert
        while ( rightTuple != null &&
                rightTuple.getRightTupleSink().getRightInputOtnId().before(getRightInputOtnId()) ) {
            modifyPreviousTuples.removeRightTuple();

            // we skipped this node, due to alpha hashing, so retract now
            rightTuple.setPropagationContext( context );
            BetaMemory bm  = getBetaMemory( (BetaNode) rightTuple.getRightTupleSink(), wm );
            (( BetaNode ) rightTuple.getRightTupleSink()).doDeleteRightTuple( rightTuple, wm, bm );
            rightTuple = modifyPreviousTuples.peekRightTuple();
        }

        if ( rightTuple != null && rightTuple.getRightTupleSink().getRightInputOtnId().equals(getRightInputOtnId()) ) {
            modifyPreviousTuples.removeRightTuple();
            rightTuple.reAdd();
            if ( intersect( context.getModificationMask(), getRightInferredMask() ) ) {
                // RightTuple previously existed, so continue as modify
                rightTuple.setPropagationContext( context );  // only update, if the mask intersects

                BetaMemory bm = getBetaMemory( this, wm );
                rightTuple.setPropagationContext( context );
                doUpdateRightTuple(rightTuple, wm, bm);
            } else if (rightTuple.getMemory() != null) {
                getBetaMemory( this, wm ).getRightTupleMemory().removeAdd(rightTuple);
            }
        } else {
            if ( intersect( context.getModificationMask(), getRightInferredMask() ) ) {
                // RightTuple does not exist for this node, so create and continue as assert
                assertObject( factHandle,
                              context,
                              wm );
            }
        }
    }

    public void doDeleteRightTuple(final RightTuple rightTuple,
                                   final InternalWorkingMemory wm,
                                   final BetaMemory memory) {
        RightTupleSets stagedRightTuples = memory.getStagedRightTuples();

        boolean stagedDeleteWasEmpty = false;
        if ( isStreamMode() ) {
            PropagationContext pctx = rightTuple.getPropagationContext();
            int propagationType = pctx.getType() == PropagationContext.MODIFICATION ? PropagationContext.DELETION : pctx.getType();
            stagedDeleteWasEmpty = memory.getSegmentMemory().getStreamQueue().addDelete(new RightTupleEntry(rightTuple, pctx, memory, propagationType));
            if ( isLogTraceEnabled ) {
                log.trace( "{} delete queue={} size={} pctx={} lt={}", getClass().getSimpleName(), System.identityHashCode( memory.getSegmentMemory().getStreamQueue() ), memory.getSegmentMemory().getStreamQueue().size(), PhreakPropagationContext.intEnumToString(rightTuple.getPropagationContext()), rightTuple );
            }

            registerUnlinkedPaths(wm, memory.getSegmentMemory(), stagedDeleteWasEmpty);
        } else {
            stagedDeleteWasEmpty = stagedRightTuples.addDelete(rightTuple);
        }

        if ( memory.getAndDecCounter() == 1 ) {
            memory.unlinkNode(wm);
        } else if ( stagedDeleteWasEmpty ) {
            // nothing staged before, notify rule, so it can evaluate network
            memory.setNodeDirty( wm );
        }
    }

    public void doUpdateRightTuple(final RightTuple rightTuple,
                                    final InternalWorkingMemory wm,
                                    final BetaMemory memory) {
        RightTupleSets stagedRightTuples = memory.getStagedRightTuples();


        boolean stagedUpdateWasEmpty = false;
        if ( streamMode ) {
            PropagationContext pctx = rightTuple.getPropagationContext();
            stagedUpdateWasEmpty = memory.getSegmentMemory().getStreamQueue().addUpdate(new RightTupleEntry(rightTuple, pctx, memory, pctx.getType()));
        } else {
            stagedUpdateWasEmpty = stagedRightTuples.addUpdate( rightTuple );
        }

        if ( stagedUpdateWasEmpty  ) {
            memory.setNodeDirty( wm );
        }
    }

    public boolean isRightInputIsRiaNode() {
        return rightInputIsRiaNode;
    }

    public boolean isRightInputPassive() {
        return rightInputIsPassive;
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

    public FastIterator getRightIterator(RightTupleMemory memory, RightTuple rightTuple) {
        if ( !this.indexedUnificationJoin ) {
            return memory.fastIterator();
        } else {
            return memory.fullFastIterator(rightTuple);
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
                                         final InternalFactHandle factHandle,
                                         final FastIterator it) {
        if ( !this.indexedUnificationJoin ) {
            return memory.getFirst(leftTuple, factHandle, it);
        } else {
            return (RightTuple) it.next( null );
        }
    }

    public LeftTuple getFirstLeftTuple(final RightTuple rightTuple,
                                       final LeftTupleMemory memory,
                                       final PropagationContext context,
                                       final FastIterator it) {
        if ( !this.indexedUnificationJoin ) {
            return memory.getFirst(rightTuple);
        } else {
            return (LeftTuple) it.next( null );
        }
    }

    public static RightTuple getFirstRightTuple(final RightTupleMemory memory,
                                                final FastIterator it) {
        if ( !memory.isIndexed() ) {
            return memory.getFirst(null, null, it);
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

    public boolean isIndexedUnificationJoin() {
        return indexedUnificationJoin;
    }

    public BetaNodeFieldConstraint[] getConstraints() {
        return constraints.getConstraints();
    }

    public BetaConstraints getRawConstraints() {
        return this.constraints;
    }
    
    public void setConstraints(BetaConstraints constraints) {
        this.constraints = constraints.cloneIfInUse();
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
        for (LeftTupleSink sink1 : sinks) {
            if (sink1.getType() == NodeTypeEnums.RuleTerminalNode) {
                list.add(((RuleTerminalNode) sink1).getRule().getName());
            } else if (NodeTypeEnums.isBetaNode(sink1)) {
                list.addAll(((BetaNode) sink1).getRules());
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

        this.rightInput.addObjectSink(this);
        this.leftInput.addTupleSink( this, context );
    }

    public void byPassModifyToBetaNode (final InternalFactHandle factHandle,
                                        final ModifyPreviousTuples modifyPreviousTuples,
                                        final PropagationContext context,
                                        final InternalWorkingMemory workingMemory) {
        modifyObject( factHandle, modifyPreviousTuples, context, workingMemory );
    }


    public static BetaMemory getBetaMemory(BetaNode node, InternalWorkingMemory wm) {
        BetaMemory bm;
        if ( node.getType() == NodeTypeEnums.AccumulateNode ) {
            bm = ((AccumulateMemory)wm.getNodeMemory(node)).getBetaMemory();
        } else {
            bm = ((BetaMemory)wm.getNodeMemory(  node ));
        }
        return bm;
    }
    

    public boolean isObjectMemoryEnabled() {
        return objectMemory;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    public Memory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
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
        return hash + (rightInputIsPassive ? 43 : 0);
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
                this.rightInputIsPassive == other.rightInputIsPassive &&
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
        RightTuple rightTuple = new RightTuple( handle, sink );
        rightTuple.setPropagationContext( context );
        return rightTuple;
    }
    
    public static Object getBetaMemoryFromRightInput( final BetaNode betaNode, final InternalWorkingMemory workingMemory ) {        
        BetaMemory memory;
        if ( NodeTypeEnums.AccumulateNode == betaNode.getType()) {
            memory = ((AccumulateMemory)workingMemory.getNodeMemory( betaNode )).getBetaMemory();
        } else {
            memory = (BetaMemory) workingMemory.getNodeMemory( betaNode );
        }
        
        
        if ( memory.getSegmentMemory() == null ) {
            SegmentUtilities.createSegmentMemory( betaNode, workingMemory ); // initialises for all nodes in segment, including this one
        }
        return memory;
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

    public ObjectTypeNode.Id getRightInputOtnId() {
        return rightInputOtnId;
    }

    public void setRightInputOtnId(ObjectTypeNode.Id rightInputOtnId) {
        this.rightInputOtnId = rightInputOtnId;
    }
}
