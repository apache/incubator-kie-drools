/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.reteoo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.drools.base.base.ObjectType;
import org.drools.base.common.NetworkNode;
import org.drools.base.common.RuleBasePartitionId;
import org.drools.base.reteoo.BaseTerminalNode;
import org.drools.base.reteoo.NodeTypeEnums;
import org.drools.base.rule.IndexableConstraint;
import org.drools.base.rule.Pattern;
import org.drools.base.util.index.IndexUtil;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.core.common.DoubleNonIndexSkipBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.PropagationContext;
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.core.common.QuadroupleNonIndexSkipBetaConstraints;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.common.SingleNonIndexSkipBetaConstraints;
import org.drools.core.common.TripleBetaConstraints;
import org.drools.core.common.TripleNonIndexSkipBetaConstraints;
import org.drools.core.common.TupleSets;
import org.drools.core.common.UpdateContext;
import org.drools.core.phreak.DetachedTuple;
import org.drools.core.reteoo.AccumulateNode.AccumulateMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.rule.constraint.BetaConstraint;
import org.drools.core.util.FastIterator;
import org.drools.util.bitmask.AllSetBitMask;
import org.drools.util.bitmask.BitMask;
import org.drools.util.bitmask.EmptyBitMask;
import org.kie.api.definition.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.base.reteoo.PropertySpecificUtil.isPropertyReactive;
import static org.drools.core.phreak.RuleNetworkEvaluator.doUpdatesReorderChildLeftTuple;
import static org.drools.core.phreak.TupleEvaluationUtil.flushLeftTupleIfNecessary;

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

    private ObjectTypeNodeId rightInputOtnId = ObjectTypeNodeId.DEFAULT_ID;

    protected boolean objectMemory = true; // hard coded to true

    protected boolean tupleMemoryEnabled;

    protected boolean indexedUnificationJoin;

    private BitMask rightDeclaredMask = EmptyBitMask.get();
    private BitMask rightInferredMask = EmptyBitMask.get();
    private BitMask rightNegativeMask = EmptyBitMask.get();

    private Collection<String> leftListenedProperties;
    private Collection<String> rightListenedProperties;

    protected boolean rightInputIsRiaNode;

    private transient ObjectTypeNode objectTypeNode;

    private boolean rightInputIsPassive;

    private boolean indexable;

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
    protected BetaNode(final int id,
             final LeftTupleSource leftInput,
             final ObjectSource rightInput,
             final BetaConstraints constraints,
             final BuildContext context) {
        super(id, context);
        setLeftTupleSource(leftInput);
        this.rightInput = rightInput;

        rightInputIsRiaNode = NodeTypeEnums.RightInputAdapterNode == rightInput.getType();

        setConstraints(constraints);

        if (this.constraints == null) {
            throw new RuntimeException("cannot have null constraints, must at least be an instance of EmptyBetaConstraints");
        }

        this.constraints.init(context, getType());
        this.constraints.registerEvaluationContext(context);

        initMasks(context, leftInput);

        setStreamMode( context.isStreamMode() && getObjectTypeNode(context).getObjectType().isEvent() );

        this.hashcode = calculateHashCode();

        this.indexable = this.constraints.getConstraints().length > 0 && IndexUtil.isIndexable(this.constraints.getConstraints()[0], getType(), context.getRuleBase().getConfiguration());
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
            rightDeclaredMask = AllSetBitMask.get();
            super.initDeclaredMask(context, leftInput);
            return;
        }

        Pattern pattern = context.getLastBuiltPatterns()[0]; // right input pattern
        rightInputIsPassive = pattern.isPassive();

        if (!isRightInputIsRiaNode()) {
            ObjectType objectType = pattern.getObjectType();

            if (isPropertyReactive(context.getRuleBase(), objectType)) {
                rightListenedProperties = pattern.getListenedProperties();
                List<String> accessibleProperties = pattern.getAccessibleProperties( context.getRuleBase() );
                rightDeclaredMask = pattern.getPositiveWatchMask(accessibleProperties);
                rightDeclaredMask = rightDeclaredMask.setAll(constraints.getListenedPropertyMask(pattern, objectType, accessibleProperties));
                rightNegativeMask = pattern.getNegativeWatchMask(accessibleProperties);
            } else {
                // if property reactive is not on, then accept all modification propagations
                rightDeclaredMask = AllSetBitMask.get();
            }
        } else {
            rightDeclaredMask = AllSetBitMask.get();
            // There would have been no right input pattern, so swap current to first, so leftInput can still work
            context.setLastBuiltPattern( context.getLastBuiltPatterns()[0] );
        }

        super.initDeclaredMask(context, leftInput);
    }

    @Override
    public void setPartitionId(BuildContext context, RuleBasePartitionId partitionId ) {
        if (rightInput.getPartitionId() != RuleBasePartitionId.MAIN_PARTITION && !rightInput.getPartitionId().equals( partitionId )) {
            this.partitionId = rightInput.getPartitionId();
            context.setPartitionId( this.partitionId );
            leftInput.setSourcePartitionId( context, this.partitionId );
        } else {
            this.partitionId = partitionId;
        }
    }

    @Override
    protected void setLeftListenedProperties(Collection<String> leftListenedProperties) {
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
        rightInferredMask = rightInferredMask.resetAll(rightNegativeMask);
    }

    public ObjectSource unwrapRightInput() {
        return rightInput;
    }

    private void setUnificationJoin() {
        // If this join uses a indexed, ==, constraint on a query parameter then set indexedUnificationJoin to true
        // This ensure we get the correct iterator
        BetaConstraint[] betaCconstraints = this.constraints.getConstraints();
        if ( betaCconstraints.length > 0 ) {
            BetaConstraint c = betaCconstraints[0];
            if ( indexable && ((IndexableConstraint) c).isUnification() ) {
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

    @Override
    public void assertObject( InternalFactHandle factHandle, PropagationContext pctx, ReteEvaluator reteEvaluator ) {
        final BetaMemory memory = (BetaMemory) getBetaMemoryFromRightInput(this, reteEvaluator);

        RightTuple rightTuple = createRightTuple(factHandle, this, pctx);

        boolean stagedInsertWasEmpty = memory.getStagedRightTuples().addInsert(rightTuple);
        if ( isLogTraceEnabled ) {
            log.trace("BetaNode stagedInsertWasEmpty={}", stagedInsertWasEmpty );
        }

        boolean shouldFlush = isStreamMode();
        if ( memory.getAndIncCounter() == 0 ) {
            if ( stagedInsertWasEmpty ) {
                memory.setNodeDirtyWithoutNotify();
            }
            shouldFlush = memory.linkNode( this, reteEvaluator, !rightInputIsPassive ) | shouldFlush;
        } else if ( stagedInsertWasEmpty ) {
            shouldFlush = memory.setNodeDirty( this, reteEvaluator, !rightInputIsPassive ) | shouldFlush;
        }

        if (shouldFlush) {
            flushLeftTupleIfNecessary( reteEvaluator, memory.getOrCreateSegmentMemory( this, reteEvaluator ), isStreamMode() );
        }
    }

    public void modifyObject(InternalFactHandle factHandle, ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, ReteEvaluator reteEvaluator) {
        TupleImpl rightTuple = modifyPreviousTuples.peekRightTuple(partitionId);

        // if the peek is for a different OTN we assume that it is after the current one and then this is an assert
        while ( rightTuple != null && rightTuple.getInputOtnId().before(getRightInputOtnId()) ) {
            modifyPreviousTuples.removeRightTuple(partitionId);

            // we skipped this node, due to alpha hashing, so retract now
            rightTuple.setPropagationContext( context );
            BetaMemory bm = getBetaMemory(rightTuple.getSink(), reteEvaluator);
            (( BetaNode ) rightTuple.getSink()).doDeleteRightTuple(rightTuple, reteEvaluator, bm);
            rightTuple = modifyPreviousTuples.peekRightTuple(partitionId);
        }

        if ( rightTuple != null && rightTuple.getInputOtnId().equals(getRightInputOtnId()) ) {
            modifyPreviousTuples.removeRightTuple(partitionId);
            rightTuple.reAdd();
            if ( context.getModificationMask().intersects(getRightInferredMask()) ) {
                // RightTuple previously existed, so continue as modify
                rightTuple.setPropagationContext( context );  // only update, if the mask intersects

                BetaMemory bm = getBetaMemory(this, reteEvaluator);
                rightTuple.setPropagationContext( context );
                doUpdateRightTuple(rightTuple, reteEvaluator, bm);
            } else if (rightTuple.getMemory() != null) {
                reorderRightTuple(reteEvaluator, rightTuple);
            }
        } else {
            if ( context.getModificationMask().intersects(getRightInferredMask()) ) {
                // RightTuple does not exist for this node, so create and continue as assert
                assertObject( factHandle, context, reteEvaluator );
            }
        }
    }

    protected void reorderRightTuple(ReteEvaluator reteEvaluator, TupleImpl rightTuple) {
        getBetaMemory(this, reteEvaluator).getRightTupleMemory().removeAdd(rightTuple);
        doUpdatesReorderChildLeftTuple(rightTuple);
    }

    public void doDeleteRightTuple(final TupleImpl rightTuple,
                                   final ReteEvaluator reteEvaluator,
                                   final BetaMemory memory) {
        TupleSets stagedRightTuples = memory.getStagedRightTuples();

        boolean stagedDeleteWasEmpty = stagedRightTuples.addDelete(rightTuple);

        boolean shouldFlush = isStreamMode();
        if ( memory.getAndDecCounter() == 1 ) {
            if ( stagedDeleteWasEmpty ) {
                memory.setNodeDirtyWithoutNotify();
            }
            shouldFlush = memory.unlinkNode(reteEvaluator) | shouldFlush;
        } else if ( stagedDeleteWasEmpty ) {
            // nothing staged before, notify rule, so it can evaluate network
            shouldFlush = memory.setNodeDirty( this, reteEvaluator ) | shouldFlush;
        }

        if (shouldFlush) {
            flushLeftTupleIfNecessary( reteEvaluator, memory.getOrCreateSegmentMemory( this, reteEvaluator ), isStreamMode() );
        }
    }

    public void doUpdateRightTuple(final TupleImpl rightTuple,
                                    final ReteEvaluator reteEvaluator,
                                    final BetaMemory memory) {
        TupleSets stagedRightTuples = memory.getStagedRightTuples();

        boolean stagedUpdateWasEmpty = stagedRightTuples.addUpdate( rightTuple );

        boolean shouldFlush = isStreamMode();
        if ( stagedUpdateWasEmpty  ) {
            shouldFlush = memory.setNodeDirty( this, reteEvaluator ) | shouldFlush;
        }

        if (shouldFlush) {
            flushLeftTupleIfNecessary( reteEvaluator, memory.getOrCreateSegmentMemory( this, reteEvaluator ), isStreamMode() );
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

    public void setRightInput( ObjectSource rightInput ) {
        this.rightInput = rightInput;
        rightInputIsRiaNode = NodeTypeEnums.RightInputAdapterNode == rightInput.getType();
    }

    public FastIterator<TupleImpl> getRightIterator(TupleMemory memory) {
        if ( this.indexedUnificationJoin ) {
            return memory.fullFastIterator();
        } else {
            return memory.fastIterator();
        }
    }

    public RightTuple getFirstRightTuple(final TupleImpl leftTuple,
                                         final TupleMemory memory,
                                         final FastIterator<TupleImpl> it) {
        if ( this.indexedUnificationJoin ) {
            return (RightTuple) it.next(null);
        } else {
            return (RightTuple) memory.getFirst(leftTuple);
        }
    }

    public FastIterator<TupleImpl> getLeftIterator(TupleMemory memory) {
        if (rightInputIsRiaNode) {
            return FastIterator.NullFastIterator.INSTANCE;
        } else {
            if ( this.indexedUnificationJoin ) {
                return memory.fullFastIterator();
            } else {
                return memory.fastIterator();
            }
        }
    }

    public TupleImpl getFirstLeftTuple(final TupleImpl rightTuple,
                                       final TupleMemory memory,
                                       final FastIterator<TupleImpl> it) {
        if (rightInputIsRiaNode) {
            return getStartTuple(rightTuple);
        } else {
            if ( this.indexedUnificationJoin ) {
                return it.next(null );
            } else {
                return memory.getFirst(rightTuple);
            }
        }
    }

    public TupleImpl getStartTuple(TupleImpl lt) {
        LeftTupleSource startTupleSource = (( RightInputAdapterNode ) getRightInput()).getStartTupleSource();

        // Iterate find start
        while (lt.getIndex() != startTupleSource.getPathIndex()-1) { // -1 as it needs the split node, not the start of the branch
            lt = lt.getLeftParent();
        }

        // Now iterate to find peer. It is not guaranteed that the next node is the correct one, see testSubnetworkSharingWith2Sinks
        while (lt.getSink() != this) {
            lt = lt.getPeer();
        }

        return lt;
    }

    public static TupleImpl getFirstTuple(TupleMemory memory, FastIterator<TupleImpl> it) {
        if ( !memory.isIndexed() ) {
            return memory.getFirst( null );
        } else {
            return it.next( null );
        }
    }

    public boolean isIndexedUnificationJoin() {
        return indexedUnificationJoin;
    }

    public BetaConstraint[] getConstraints() {
        return constraints.getConstraints();
    }

    public BetaConstraints getRawConstraints() {
        return this.constraints;
    }
    
    private void setConstraints(BetaConstraints constraints) {
        this.constraints = (BetaConstraints) constraints.cloneIfInUse();
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
        final List<String> list = new ArrayList<>();

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

    @Override
    public ObjectTypeNode getObjectTypeNode() {
        if (objectTypeNode == null) {
            ObjectSource source = this.rightInput;
            while ( source != null ) {
                if ( NodeTypeEnums.ObjectTypeNode == source.getType()) {
                    objectTypeNode = (ObjectTypeNode) source;
                    break;
                }
                source = source.source;
            }
        }
        return objectTypeNode;
    }

    public void doAttach(BuildContext context) {
        super.doAttach(context);
        setUnificationJoin();

        this.rightInput.addObjectSink(this);
        this.leftInput.addTupleSink( this, context );
    }

    public void byPassModifyToBetaNode (final InternalFactHandle factHandle,
                                        final ModifyPreviousTuples modifyPreviousTuples,
                                        final PropagationContext context,
                                        final ReteEvaluator reteEvaluator) {
        modifyObject( factHandle, modifyPreviousTuples, context, reteEvaluator );
    }


    public static BetaMemory getBetaMemory(NetworkNode node, ReteEvaluator reteEvaluator) {
        BetaMemory bm;
        if ( node.getType() == NodeTypeEnums.AccumulateNode ) {
            bm = ((AccumulateMemory)reteEvaluator.getNodeMemory((AccumulateNode)node)).getBetaMemory();
        } else {
            bm = ((BetaMemory)reteEvaluator.getNodeMemory((BetaNode)node));
        }
        return bm;
    }
    

    public boolean isObjectMemoryEnabled() {
        return objectMemory;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public Memory createMemory(RuleBaseConfiguration config, ReteEvaluator reteEvaluator) {
        return (Memory) constraints.createBetaMemory(config, getType());
    }

    public String toString() {
        return "[ " + this.getClass().getSimpleName() + "(" + this.id + ") ]";
    }

    protected int calculateHashCode() {
        int hash = ( 23 * leftInput.hashCode() ) + ( 29 * rightInput.hashCode() ) + ( 31 * constraints.hashCode() );
        if (leftListenedProperties != null) {
            hash += 37 * leftListenedProperties.hashCode();
        }
        if (rightListenedProperties != null) {
            hash += 41 * rightListenedProperties.hashCode();
        }
        return hash + (rightInputIsPassive ? 43 : 0);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }

        if (!NodeTypeEnums.isBetaNode((NetworkNode)object) || this.hashCode() != object.hashCode()) {
            return false;
        }

        BetaNode other = (BetaNode) object;
        return this.getClass() == other.getClass() &&
               this.constraints.equals( other.constraints ) &&
               this.rightInputIsPassive == other.rightInputIsPassive &&
               Objects.equals(this.leftListenedProperties, other.leftListenedProperties) &&
               Objects.equals(this.rightListenedProperties, other.rightListenedProperties) &&
               this.leftInput.getId() == other.leftInput.getId() &&
               this.rightInput.getId() == other.rightInput.getId();
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
        RightTuple rightTuple = new RightTuple(handle, sink );
        rightTuple.setPropagationContext( context );
        return rightTuple;
    }
    
    public static BetaMemory getBetaMemoryFromRightInput(BetaNode betaNode, ReteEvaluator reteEvaluator) {
        return NodeTypeEnums.AccumulateNode == betaNode.getType() ?
                            ((AccumulateMemory)reteEvaluator.getNodeMemory( betaNode )).getBetaMemory() :
                            (BetaMemory) reteEvaluator.getNodeMemory( betaNode );
    }
    
    public BitMask getRightDeclaredMask() {
        return rightDeclaredMask;
    }

    public BitMask getRightInferredMask() {
        return rightInferredMask;
    }

    void disablePropertyReactivity() {
        rightInferredMask = AllSetBitMask.get();
        if (NodeTypeEnums.isBetaNode(leftInput)) {
            ((BetaNode)leftInput).disablePropertyReactivity();
        }
    }

    public BitMask getRightNegativeMask() {
        return rightNegativeMask;
    }

    public ObjectTypeNodeId getRightInputOtnId() {
        return rightInputOtnId;
    }

    public void setRightInputOtnId(ObjectTypeNodeId rightInputOtnId) {
        this.rightInputOtnId = rightInputOtnId;
    }

    /**
     * Used with the updateSink method, so that the parent ObjectSource
     * can  update the  TupleSink
     */
    public static class RightTupleSinkAdapter
            implements
            ObjectSink {
        private BetaNode bnNode;

        private List<DetachedTuple> detachedTuples;

        public RightTupleSinkAdapter(BetaNode bnNode, List<DetachedTuple> detachedTuples) {
            this.bnNode = bnNode;
            this.detachedTuples = detachedTuples;
        }

        /**
         * Do not use this constructor. It should be used just by deserialization.
         */
        public RightTupleSinkAdapter() {
        }

        public void assertObject(final InternalFactHandle factHandle,
                                 final PropagationContext context,
                                 final ReteEvaluator reteEvaluator) {
            ObjectTypeNodeId otnId = bnNode.getRightInputOtnId();
            TupleImpl detached = factHandle.getLinkedTuples().detachRightTupleAfter(getPartitionId(), otnId);
            if (detached != null) {
                detachedTuples.add(new DetachedTuple((DefaultFactHandle) factHandle, detached));
            }

            bnNode.assertObject(factHandle, context, reteEvaluator);
        }

        public void modifyObject(InternalFactHandle factHandle,
                                 ModifyPreviousTuples modifyPreviousTuples,
                                 PropagationContext context,
                                 ReteEvaluator reteEvaluator) {
            throw new UnsupportedOperationException( "ObjectSinkAdapter onlys supports assertObject method calls" );
        }

        public int getId() {
            return 0;
        }

        public RuleBasePartitionId getPartitionId() {
            return bnNode.getPartitionId();
        }

        public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                           ModifyPreviousTuples modifyPreviousTuples,
                                           PropagationContext context,
                                           ReteEvaluator reteEvaluator) {
            throw new UnsupportedOperationException();
        }

        public int getType() {
            return NodeTypeEnums.LeftInputAdapterNode;
        }

        @Override public Rule[] getAssociatedRules() {
            return bnNode.getAssociatedRules();
        }

        public boolean isAssociatedWith(Rule rule) {
            return bnNode.isAssociatedWith( rule );
        }

        @Override
        public NetworkNode[] getSinks() {
            return new NetworkNode[0];
        }

        @Override
        public void addAssociatedTerminal(BaseTerminalNode terminalNode) {
            bnNode.addAssociatedTerminal(terminalNode);
        }

        @Override
        public void removeAssociatedTerminal(BaseTerminalNode terminalNode) {
            bnNode.removeAssociatedTerminal(terminalNode);
        }

        @Override
        public int getAssociatedTerminalsSize() {
            return bnNode.getAssociatedTerminalsSize();
        }

        @Override
        public boolean hasAssociatedTerminal(BaseTerminalNode terminalNode) {
            return bnNode.hasAssociatedTerminal(terminalNode);
        }
    }
}
