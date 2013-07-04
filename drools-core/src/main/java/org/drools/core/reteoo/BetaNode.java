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

import static org.drools.core.reteoo.PropertySpecificUtil.calculateNegativeMask;
import static org.drools.core.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.core.reteoo.PropertySpecificUtil.getSettableProperties;
import static org.drools.core.reteoo.PropertySpecificUtil.isPropertyReactive;
import static org.drools.core.util.BitMaskUtil.intersect;
import static org.drools.core.util.ClassUtils.areNullSafeEquals;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BetaConstraints;
import org.drools.core.common.DoubleBetaConstraints;
import org.drools.core.common.DoubleNonIndexSkipBetaConstraints;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.PropagationContextImpl;
import org.drools.core.common.QuadroupleBetaConstraints;
import org.drools.core.common.QuadroupleNonIndexSkipBetaConstraints;
import org.drools.core.common.RightTupleSets;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.SingleBetaConstraints;
import org.drools.core.common.SingleNonIndexSkipBetaConstraints;
import org.drools.core.common.SynchronizedRightTupleSets;
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
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.BetaNodeFieldConstraint;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.FastIterator;
import org.drools.core.util.index.IndexUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.core.reteoo.PropertySpecificUtil.getSettableProperties;
import static org.drools.core.reteoo.PropertySpecificUtil.isPropertyReactive;

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

    protected static transient Logger log = LoggerFactory.getLogger(BetaNode.class);

    protected ObjectSource rightInput;

    protected BetaConstraints constraints;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    private ObjectSinkNode previousObjectSinkNode;
    private ObjectSinkNode nextObjectSinkNode;

    protected boolean objectMemory = true; // hard coded to true
    protected boolean tupleMemoryEnabled;
    protected boolean concurrentRightTupleMemory = false;


    protected boolean indexedUnificationJoin;

    private long rightDeclaredMask;
    private long rightInferredMask;
    private long rightNegativeMask;

    private List<String> leftListenedProperties;
    private List<String> rightListenedProperties;

    private transient ObjectTypeNode.Id rightInputOtnId = ObjectTypeNode.DEFAULT_ID;

    private boolean rightInputIsRiaNode;

    private transient ObjectTypeNode objectTypeNode;

    private boolean unlinkingEnabled;

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
        super(id,
              partitionId,
              partitionsEnabled);
        setLeftTupleSource(leftInput);
        this.rightInput = rightInput;

        if (NodeTypeEnums.RightInputAdaterNode == rightInput.getType()) {
            rightInputIsRiaNode = true;
        } else {
            rightInputIsRiaNode = false;
        }

        setConstraints(constraints);

        if (this.constraints == null) {
            throw new RuntimeException("cannot have null constraints, must at least be an instance of EmptyBetaConstraints");
        }

        initMasks(context, leftInput);

        this.unlinkingEnabled = context.getRuleBase().getConfiguration().isPhreakEnabled();

        ObjectTypeNode node = null;

        if (context.isStreamMode() && getObjectTypeNode().getObjectType().isEvent()) {
            streamMode = true;
        } else {
            streamMode = false;
        }
    }

    public boolean isUnlinkingEnabled() {
        return unlinkingEnabled;
    }

    public void setUnlinkingEnabled(boolean unlinkingEnabled) {
        this.unlinkingEnabled = unlinkingEnabled;
    }

    @Override
    protected void initDeclaredMask(BuildContext context,
                                    LeftTupleSource leftInput) {
        if (context == null || context.getLastBuiltPatterns() == null) {
            // only happens during unit tests
            rightDeclaredMask = Long.MAX_VALUE;
            super.initDeclaredMask(context, leftInput);
            return;
        }

        if (!isRightInputIsRiaNode()) {
            Pattern pattern = context.getLastBuiltPatterns()[0]; // right input pattern
            ObjectType objectType = pattern.getObjectType();

            if (objectType instanceof ClassObjectType) {
                Class objectClass = ((ClassObjectType) objectType).getClassType();
                if (isPropertyReactive(context, objectClass)) {
                    rightListenedProperties = pattern.getListenedProperties();
                    List<String> settableProperties = getSettableProperties(context.getRuleBase(), objectClass);
                    rightDeclaredMask = calculatePositiveMask(rightListenedProperties, settableProperties);
                    rightDeclaredMask |= constraints.getListenedPropertyMask(settableProperties);
                    rightNegativeMask = calculateNegativeMask(rightListenedProperties, settableProperties);
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
                setConstraints( this.constraints.getOriginalConstraint() );
            }
        }

        out.writeObject( constraints );
        out.writeObject( rightInput );
        out.writeBoolean( objectMemory );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeBoolean( concurrentRightTupleMemory );
        out.writeBoolean( unlinkingEnabled );
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

        RightTuple rightTuple = createRightTuple( factHandle,
                                                  this,
                                                  pctx );

        if ( isUnlinkingEnabled() ) {
            boolean stagedInsertWasEmpty = false;
            if ( streamMode ) {
                stagedInsertWasEmpty = memory.getSegmentMemory().getTupleQueue().isEmpty();
                memory.getSegmentMemory().getTupleQueue().add(new RightTupleEntry(rightTuple, pctx, memory ));
                if ( log.isTraceEnabled() ) {
                    log.trace( "JoinNode insert queue={} size={} pctx={} lt={}", System.identityHashCode( memory.getSegmentMemory().getTupleQueue() ), memory.getSegmentMemory().getTupleQueue().size(), PropagationContextImpl.intEnumToString( pctx ), rightTuple );
                }
            }  else {
                stagedInsertWasEmpty = memory.getStagedRightTuples().addInsert( rightTuple );
            }
            if ( log.isTraceEnabled() ) {
                log.trace("BetaNode insert={} stagedInsertWasEmpty={}",  memory.getStagedRightTuples().insertSize(), stagedInsertWasEmpty );
            }
            if ( memory.getAndIncCounter() == 0 ) {
                memory.linkNode( wm );
            } else if ( stagedInsertWasEmpty ) {
                memory.getSegmentMemory().notifyRuleLinkSegment( wm );
            }

            if( pctx.getReaderContext() != null ) {
                // we are deserializing a session, so we might need to evaluate
                // rule activations immediately
                MarshallerReaderContext mrc = (MarshallerReaderContext) pctx.getReaderContext();
                mrc.filter.fireRNEAs( wm );
            }

            return;
        }

        assertRightTuple(rightTuple, pctx, wm );

    }

    public abstract void assertRightTuple( final RightTuple rightTuple,
                                           final PropagationContext context,
                                           final InternalWorkingMemory workingMemory );


    public void doDeleteRightTuple(final RightTuple rightTuple,
                                   final InternalWorkingMemory wm,
                                   final BetaMemory memory) {
        RightTupleSets stagedRightTuples = memory.getStagedRightTuples();

        boolean stagedDeleteWasEmpty = false;
        if ( isStreamMode() ) {
            stagedDeleteWasEmpty = memory.getSegmentMemory().getTupleQueue().isEmpty();
            memory.getSegmentMemory().getTupleQueue().add(new RightTupleEntry(rightTuple, rightTuple.getPropagationContext(), memory ));
            if ( log.isTraceEnabled() ) {
                log.trace( "JoinNode delete queue={} size={} pctx={} lt={}", System.identityHashCode( memory.getSegmentMemory().getTupleQueue() ), memory.getSegmentMemory().getTupleQueue().size(), PropagationContextImpl.intEnumToString( rightTuple.getPropagationContext() ), rightTuple );
            }
        } else {
            stagedDeleteWasEmpty = stagedRightTuples.addDelete( rightTuple );
        }

        if ( memory.getAndDecCounter() == 1 ) {
            memory.unlinkNode( wm );
        } else if ( stagedDeleteWasEmpty ) {
            // nothing staged before, notify rule, so it can evaluate network
            memory.getSegmentMemory().notifyRuleLinkSegment( wm );
        };
    }

    public void doUpdateRightTuple(final RightTuple rightTuple,
                                    final InternalWorkingMemory wm,
                                    final BetaMemory memory) {
        RightTupleSets stagedRightTuples = memory.getStagedRightTuples();


        boolean stagedUpdateWasEmpty = false;
        if ( streamMode ) {
            stagedUpdateWasEmpty = memory.getSegmentMemory().getTupleQueue().isEmpty();
            memory.getSegmentMemory().getTupleQueue().add(new RightTupleEntry(rightTuple, rightTuple.getPropagationContext(), memory ));
        } else {
            stagedUpdateWasEmpty = stagedRightTuples.addUpdate( rightTuple );
        }

        if ( stagedUpdateWasEmpty  ) {
            memory.getSegmentMemory().notifyRuleLinkSegment( wm );
        }
    }

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
            return memory.getFirst( leftTuple, factHandle, it );
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

        if (context == null || context.getRuleBase().getConfiguration().isPhreakEnabled() ) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            final PropagationContext propagationContext = new PropagationContextImpl(workingMemory.getNextPropagationIdCounter(),
                    PropagationContext.RULE_ADDITION,
                    null,
                    null,
                    null);

            this.rightInput.updateSink(this,
                                       propagationContext,
                                       workingMemory);

            this.leftInput.updateSink(this,
                                      propagationContext,
                                      workingMemory);
        }

    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        //context.getRuleBase().getConfiguration().isPhreakEnabled()

        //context.get
        if ( !context.getRuleBase().getConfiguration().isPhreakEnabled() && (!this.isInUse() || context.getCleanupAdapter() != null ) ) {
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

        if ( !isInUse() ) {
            leftInput.removeTupleSink( this );
            rightInput.removeObjectSink( this );
        }
    }
        
    protected void doCollectAncestors(NodeSet nodeSet) {
        this.leftInput.collectAncestors(nodeSet);
        this.rightInput.collectAncestors(nodeSet);
    }

    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory wm) {
        RightTuple rightTuple = modifyPreviousTuples.peekRightTuple();

        // if the peek is for a different OTN we assume that it is after the current one and then this is an assert
        while ( rightTuple != null &&
                (( BetaNode ) rightTuple.getRightTupleSink()).getRightInputOtnId().before( getRightInputOtnId() ) ) {
            modifyPreviousTuples.removeRightTuple();
                        
            // we skipped this node, due to alpha hashing, so retract now
            rightTuple.setPropagationContext( context );
            if ( isUnlinkingEnabled() ) {
                BetaMemory bm  = getBetaMemory( (BetaNode) rightTuple.getRightTupleSink(), wm );
                (( BetaNode ) rightTuple.getRightTupleSink()).doDeleteRightTuple( rightTuple, wm, bm );
            }  else {
                rightTuple.getRightTupleSink().retractRightTuple( rightTuple,
                                                                  context,
                                                                  wm );
            }
            rightTuple = modifyPreviousTuples.peekRightTuple();
        }

        if ( rightTuple != null && (( BetaNode ) rightTuple.getRightTupleSink()).getRightInputOtnId().equals(getRightInputOtnId()) ) {
            modifyPreviousTuples.removeRightTuple();
            rightTuple.reAdd();
            if ( rightTuple.getStagedType() != LeftTuple.INSERT ) {
                // things staged as inserts, are left as inserts and use the pctx associated from the time of insertion
                rightTuple.setPropagationContext( context );
            }
            if ( intersect( context.getModificationMask(), rightInferredMask ) ) {
                // RightTuple previously existed, so continue as modify     
                if ( isUnlinkingEnabled() ) {
                    BetaMemory bm = getBetaMemory( this, wm );
                    rightTuple.setPropagationContext( context );
                    doUpdateRightTuple(rightTuple, wm, bm);                        
                } else {
                    modifyRightTuple( rightTuple,
                                      context,
                                      wm ); 
                }
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

    public static BetaMemory getBetaMemory(BetaNode node, InternalWorkingMemory wm) {
        BetaMemory bm;
        if ( node.getType() == NodeTypeEnums.AccumulateNode ) {
            bm = ((AccumulateMemory)wm.getNodeMemory(node)).getBetaMemory();
        } else {
            bm = ((BetaMemory)wm.getNodeMemory(  node ));
        }
        return bm;
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
//        RightTuple rightTuple = null;
//        if ( context.getActiveWindowTupleList() == null ) {
//            rightTuple = new RightTuple( handle,
//                                         sink );
//        } else {
//            rightTuple = new WindowTuple( handle,
//                                          sink,
//                                          context.getActiveWindowTupleList() );
//        }
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
        
        
        if ( betaNode.isUnlinkingEnabled() && memory.getSegmentMemory() == null ) {
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
