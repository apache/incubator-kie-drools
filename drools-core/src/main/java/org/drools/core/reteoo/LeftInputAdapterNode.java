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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.TupleSets;
import org.drools.core.common.UpdateContext;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.reteoo.ObjectTypeNode.Id;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.Activation;
import org.drools.core.spi.ClassWireable;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.definition.rule.Rule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.drools.core.phreak.AddRemoveRule.flushLeftTupleIfNecessary;
import static org.drools.core.reteoo.PropertySpecificUtil.*;

/**
 * All asserting Facts must propagated into the right <code>ObjectSink</code> side of a BetaNode, if this is the first Pattern
 * then there are no BetaNodes to propagate to. <code>LeftInputAdapter</code> is used to adapt an ObjectSink propagation into a
 * <code>TupleSource</code> which propagates a <code>ReteTuple</code> suitable fot the right <code>ReteTuple</code> side
 * of a <code>BetaNode</code>.
 */
public class LeftInputAdapterNode extends LeftTupleSource
        implements
        ObjectSinkNode,
        MemoryFactory<LeftInputAdapterNode.LiaNodeMemory> {

    protected static final transient Logger log = LoggerFactory.getLogger(LeftInputAdapterNode.class);

    private static final long serialVersionUID = 510l;
    private ObjectSource objectSource;

    private ObjectSinkNode previousRightTupleSinkNode;
    private ObjectSinkNode nextRightTupleSinkNode;

    private boolean leftTupleMemoryEnabled;

    private BitMask sinkMask;

    public LeftInputAdapterNode() {

    }

    /**
     * Constructus a LeftInputAdapterNode with a unique id that receives <code>FactHandle</code> from a
     * parent <code>ObjectSource</code> and adds it to a given pattern in the resulting Tuples.
     *
     * @param id
     *      The unique id of this node in the current Rete network
     * @param source
     *      The parent node, where Facts are propagated from
     */
    public LeftInputAdapterNode(final int id,
                                final ObjectSource source,
                                final BuildContext context) {
        super(id, context);
        this.objectSource = source;
        this.leftTupleMemoryEnabled = context.isTupleMemoryEnabled();
        ObjectSource current = source;
        while (!(current.getType() == NodeTypeEnums.ObjectTypeNode)) {
            current = current.getParentObjectSource();
        }

        setStreamMode( context.isStreamMode() && context.getRootObjectTypeNode().getObjectType().isEvent() );
        sinkMask = calculateSinkMask(context);

        hashcode = calculateHashCode();
    }

    private BitMask calculateSinkMask(BuildContext context) {
        Pattern pattern = context.getLastBuiltPatterns() != null ? context.getLastBuiltPatterns()[0] : null;
        if (pattern == null) {
            return AllSetBitMask.get();
        }
        ObjectType objectType = pattern.getObjectType();
        if ( !(objectType instanceof ClassObjectType) ) {
            // Only ClassObjectType can use property specific
            return AllSetBitMask.get();
        }

        Class objectClass = ((ClassWireable) objectType).getClassType();
        return isPropertyReactive( context, objectClass ) ?
               calculatePositiveMask( pattern.getListenedProperties(),
                                      getAccessibleProperties( context.getKnowledgeBase(), objectClass ) ) :
               AllSetBitMask.get();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                                    ClassNotFoundException {
        super.readExternal(in);
        objectSource = (ObjectSource) in.readObject();
        leftTupleMemoryEnabled = in.readBoolean();
        sinkMask = (BitMask) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(objectSource);
        out.writeBoolean(leftTupleMemoryEnabled);
        out.writeObject(sinkMask);
    }

    public ObjectSource getObjectSource() {
        return this.objectSource;
    }

    public short getType() {
        return NodeTypeEnums.LeftInputAdapterNode;
    }

    @Override
    public boolean isLeftTupleMemoryEnabled() {
        return leftTupleMemoryEnabled;
    }

    public ObjectSource getParentObjectSource() {
        return this.objectSource;
    }

    public void attach( BuildContext context ) {
        this.objectSource.addObjectSink( this );
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.objectSource.networkUpdated(updateContext);
    }

    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        LiaNodeMemory lm = workingMemory.getNodeMemory( this );
        if ( lm.getSegmentMemory() == null ) {
            SegmentUtilities.createSegmentMemory(this, workingMemory);
        }

        doInsertObject( factHandle, context, this, workingMemory,
                        lm, true, // queries are handled directly, and not through here
                        true );
    }

    public static void doInsertObject(final InternalFactHandle factHandle,
                                      final PropagationContext context,
                                      final LeftInputAdapterNode liaNode,
                                      final InternalWorkingMemory wm,
                                      final LiaNodeMemory lm,
                                      boolean linkOrNotify,
                                      boolean useLeftMemory) {
        SegmentMemory sm = lm.getSegmentMemory();
        if ( sm.getTipNode() == liaNode) {
            // liaNode in it's own segment and child segments not yet created
            if ( sm.isEmpty() ) {
                SegmentUtilities.createChildSegments( wm,
                                                      sm,
                                                      liaNode.getSinkPropagator() );
            }
            sm = sm.getFirst(); // repoint to the child sm
        }

        int counter = lm.getAndIncreaseCounter();
        // node is not linked, so notify will happen when we link the node
        boolean notifySegment = linkOrNotify && counter != 0;

        if ( counter == 0) {
            // if there is no left mempry, then there is no linking or notification
            if ( linkOrNotify ) {
                // link and notify
                lm.linkNode( wm );
            } else {
                // link without notify, when driven by a query, as we don't want it, placed on the agenda
                lm.linkNodeWithoutRuleNotify();
            }
        }

        LeftTupleSink sink = liaNode.getSinkPropagator().getFirstLeftTupleSink();
        LeftTuple leftTuple = sink.createLeftTuple( factHandle, sink, useLeftMemory );
        leftTuple.setPropagationContext( context );
        doInsertSegmentMemory( wm, notifySegment, lm, sm, leftTuple, liaNode.isStreamMode() );

        if ( sm.getRootNode() != liaNode ) {
            // sm points to lia child sm, so iterate for all remaining children

            for ( sm = sm.getNext(); sm != null; sm = sm.getNext() ) {
                sink =  sm.getSinkFactory();
                leftTuple = sink.createPeer( leftTuple ); // pctx is set during peer cloning
                doInsertSegmentMemory( wm, notifySegment, lm, sm, leftTuple, liaNode.isStreamMode() );
            }
        }
    }

    public static void doInsertSegmentMemory( InternalWorkingMemory wm, boolean linkOrNotify, final LiaNodeMemory lm,
                                               SegmentMemory sm, LeftTuple leftTuple, boolean streamMode ) {
        if ( flushLeftTupleIfNecessary( wm, sm, leftTuple, streamMode, Tuple.INSERT ) ) {
            if ( linkOrNotify ) {
                lm.setNodeDirty( wm );
            }
            return;
        }

        // mask check is necessary if insert is a result of a modify
        boolean stagedInsertWasEmpty = sm.getStagedLeftTuples().addInsert( leftTuple );

        if ( stagedInsertWasEmpty && linkOrNotify  ) {
            // staged is empty, so notify rule, to force re-evaluation.
            lm.setNodeDirty(wm);
        }
    }

    public static void doDeleteObject(LeftTuple leftTuple,
                                      PropagationContext context,
                                      SegmentMemory sm,
                                      final InternalWorkingMemory wm,
                                      final LeftInputAdapterNode liaNode,
                                      final boolean linkOrNotify,
                                      final LiaNodeMemory lm) {
        if ( sm.getTipNode() == liaNode ) {
            // liaNode in it's own segment and child segments not yet created
            if ( sm.isEmpty() ) {
                SegmentUtilities.createChildSegments( wm,
                                                      sm,
                                                      liaNode.getSinkPropagator() );
            }
            sm = sm.getFirst(); // repoint to the child sm
        }

        doDeleteSegmentMemory(leftTuple, context, lm, sm, wm, linkOrNotify, liaNode.isStreamMode());

        if ( sm.getNext() != null) {
            // sm points to lia child sm, so iterate for all remaining children

            for ( sm = sm.getNext(); sm != null; sm = sm.getNext() ) {
                // iterate for peers segment memory
                leftTuple = leftTuple.getPeer();
                if (leftTuple == null) {
                    break;
                }
                doDeleteSegmentMemory(leftTuple, context, lm, sm, wm, linkOrNotify, liaNode.isStreamMode());
            }
        }

        if ( lm.getAndDecreaseCounter() == 1 ) {
            if ( linkOrNotify ) {
                lm.unlinkNode( wm );
            } else {
                lm.unlinkNodeWithoutRuleNotify();
            }
        }
    }

    private static void doDeleteSegmentMemory(LeftTuple leftTuple, PropagationContext pctx, final LiaNodeMemory lm,
                                              SegmentMemory sm, InternalWorkingMemory wm, boolean linkOrNotify, boolean streamMode) {
        leftTuple.setPropagationContext( pctx );
        if ( flushLeftTupleIfNecessary( wm, sm, leftTuple, streamMode, Tuple.DELETE ) ) {
            if ( linkOrNotify ) {
                lm.setNodeDirty( wm );
            }
            return;
        }

        TupleSets<LeftTuple> leftTuples = sm.getStagedLeftTuples();
        boolean stagedDeleteWasEmpty = leftTuples.addDelete(leftTuple);

        if (  stagedDeleteWasEmpty && linkOrNotify ) {
            // staged is empty, so notify rule, to force re-evaluation
            lm.setNodeDirty(wm);
        }
    }

    public static void doUpdateObject(LeftTuple leftTuple,
                                      PropagationContext context,
                                      final InternalWorkingMemory wm,
                                      final LeftInputAdapterNode liaNode,
                                      final boolean linkOrNotify,
                                      final LiaNodeMemory lm,
                                      SegmentMemory sm) {
        if ( sm.getTipNode() == liaNode) {
            // liaNode in it's own segment and child segments not yet created
            if ( sm.isEmpty() ) {
                SegmentUtilities.createChildSegments( wm,
                                                      sm,
                                                      liaNode.getSinkPropagator() );
            }
            sm = sm.getFirst(); // repoint to the child sm
        }

        TupleSets<LeftTuple> leftTuples = sm.getStagedLeftTuples();

        doUpdateSegmentMemory(leftTuple, context, wm, linkOrNotify, lm, leftTuples, sm, liaNode.isStreamMode() );

        if (  sm.getNext() != null ) {
            // sm points to lia child sm, so iterate for all remaining children
            for ( sm = sm.getNext(); sm != null; sm = sm.getNext() ) {
                // iterate for peers segment memory
                leftTuple = leftTuple.getPeer();
                leftTuples = sm.getStagedLeftTuples();

                doUpdateSegmentMemory(leftTuple, context, wm, linkOrNotify, lm, leftTuples, sm, liaNode.isStreamMode() );
            }
        }
    }

    private static void doUpdateSegmentMemory( LeftTuple leftTuple, PropagationContext pctx, InternalWorkingMemory wm, boolean linkOrNotify,
                                               final LiaNodeMemory lm, TupleSets<LeftTuple> leftTuples, SegmentMemory sm, boolean streamMode ) {
        leftTuple.setPropagationContext( pctx );
        if ( leftTuple.getStagedType() == LeftTuple.NONE ) {
            if ( flushLeftTupleIfNecessary( wm, sm, leftTuple, streamMode, Tuple.UPDATE ) ) {
                if ( linkOrNotify ) {
                    lm.setNodeDirty( wm );
                }
                return;
            }

            // if LeftTuple is already staged, leave it there
            boolean stagedUpdateWasEmpty = leftTuples.addUpdate(leftTuple);

            if ( stagedUpdateWasEmpty  && linkOrNotify ) {
                // staged is empty, so notify rule, to force re-evaluation
                lm.setNodeDirty(wm);
            }
        }
    }

    public void retractLeftTuple(LeftTuple leftTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        LiaNodeMemory lm = workingMemory.getNodeMemory( this );
        SegmentMemory smem = lm.getSegmentMemory();
        if ( smem.getTipNode() == this ) {
            // segment with only a single LiaNode in it, skip to next segment
            // as a liaNode only segment has no staging
            smem = smem.getFirst();
        }

        doDeleteObject( leftTuple, context, smem, workingMemory,
                        this, true, lm );
    }

    public void modifyObject(InternalFactHandle factHandle,
                             final ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        ObjectTypeNode.Id otnId = this.sink.getFirstLeftTupleSink().getLeftInputOtnId();

        LeftTuple leftTuple = processDeletesFromModify(modifyPreviousTuples, context, workingMemory, otnId);

        LiaNodeMemory lm = workingMemory.getNodeMemory( this );
        if ( lm.getSegmentMemory() == null ) {
            SegmentUtilities.createSegmentMemory( this, workingMemory );
        }

        if ( leftTuple != null && leftTuple.getInputOtnId().equals( otnId ) ) {
            modifyPreviousTuples.removeLeftTuple();
            leftTuple.reAdd();
            LeftTupleSink sink = getSinkPropagator().getFirstLeftTupleSink();
            BitMask mask = sink.getLeftInferredMask();
            if ( context.getModificationMask().intersects( mask) ) {
                doUpdateObject( leftTuple, context, workingMemory, (LeftInputAdapterNode) leftTuple.getTupleSource(), true, lm, lm.getSegmentMemory() );
                if (leftTuple instanceof Activation) {
                    ((Activation)leftTuple).setActive(true);
                }
            }
        } else {
            LeftTupleSink sink = getSinkPropagator().getFirstLeftTupleSink();
            BitMask mask = sink.getLeftInferredMask();
            if ( context.getModificationMask().intersects( mask) ) {
                doInsertObject(factHandle, context, this,
                               workingMemory,
                               lm, true, true);
            }

        }
    }

    private static LeftTuple processDeletesFromModify(ModifyPreviousTuples modifyPreviousTuples, PropagationContext context, InternalWorkingMemory workingMemory, Id otnId) {
        LeftTuple leftTuple = modifyPreviousTuples.peekLeftTuple();
        while ( leftTuple != null && leftTuple.getInputOtnId().before( otnId ) ) {
            modifyPreviousTuples.removeLeftTuple();

            LeftInputAdapterNode prevLiaNode = (LeftInputAdapterNode) leftTuple.getTupleSource();
            LiaNodeMemory prevLm = workingMemory.getNodeMemory( prevLiaNode );
            SegmentMemory prevSm = prevLm.getSegmentMemory();
            doDeleteObject( leftTuple, context, prevSm, workingMemory, prevLiaNode, true, prevLm );

            leftTuple = modifyPreviousTuples.peekLeftTuple();
        }
        return leftTuple;
    }

    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory) {
        modifyObject(factHandle, modifyPreviousTuples, context, workingMemory );
    }




    protected boolean doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        if (!isInUse()) {
            objectSource.removeObjectSink(this);
            return true;
        }
        return false;
    }


    public LeftTuple createPeer(LeftTuple original) {
        return null;
    }

    /**
     * Returns the next node
     * @return
     *      The next ObjectSinkNode
     */
    public ObjectSinkNode getNextObjectSinkNode() {
        return this.nextRightTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next ObjectSinkNode
     */
    public void setNextObjectSinkNode(final ObjectSinkNode next) {
        this.nextRightTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous ObjectSinkNode
     */
    public ObjectSinkNode getPreviousObjectSinkNode() {
        return this.previousRightTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous ObjectSinkNode
     */
    public void setPreviousObjectSinkNode(final ObjectSinkNode previous) {
        this.previousRightTupleSinkNode = previous;
    }

    private int calculateHashCode() {
        return 31 * this.objectSource.hashCode() + 37 * sinkMask.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return this == object ||
               ( internalEquals(object) &&
                 this.objectSource.equals(((LeftInputAdapterNode)object).objectSource) );
    }

    @Override
    protected boolean internalEquals( Object object ) {
        if ( object == null || !(object instanceof LeftInputAdapterNode) || this.hashCode() != object.hashCode() ) {
            return false;
        }
        return this.sinkMask.equals( ((LeftInputAdapterNode) object).sinkMask );
    }

    protected ObjectTypeNode getObjectTypeNode() {
        ObjectSource source = this.objectSource;
        while ( source != null ) {
            if ( source instanceof ObjectTypeNode ) {
                return (ObjectTypeNode) source;
            }
            source = source.source;
        }
        return null;
    }

    public LiaNodeMemory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
        return new LiaNodeMemory();
    }

    public static class LiaNodeMemory extends AbstractBaseLinkedListNode<Memory> implements SegmentNodeMemory {
        private int               counter;

        private SegmentMemory     segmentMemory;

        private long              nodePosMaskBit;

        public LiaNodeMemory() {
        }


        public int getCounter() {
            return counter;
        }

        public int getAndIncreaseCounter() {
            return this.counter++;
        }

        public int getAndDecreaseCounter() {
            return this.counter--;
        }

        public void setCounter(int counter) {
            this.counter = counter;
        }

        public SegmentMemory getSegmentMemory() {
            return segmentMemory;
        }

        public void setSegmentMemory(SegmentMemory segmentNodes) {
            this.segmentMemory = segmentNodes;
        }

        public long getNodePosMaskBit() {
            return nodePosMaskBit;
        }

        public void setNodePosMaskBit(long nodePosMask) {
            nodePosMaskBit = nodePosMask;
        }

        public void setNodeDirtyWithoutNotify() { }

        public void setNodeCleanWithoutNotify() { }

        public void linkNodeWithoutRuleNotify() {
            segmentMemory.linkNodeWithoutRuleNotify(nodePosMaskBit);
        }

        public void linkNode(InternalWorkingMemory wm) {
            segmentMemory.linkNode(nodePosMaskBit, wm);
        }

        public boolean unlinkNode(InternalWorkingMemory wm) {
            return segmentMemory.unlinkNode(nodePosMaskBit, wm);
        }

        public void unlinkNodeWithoutRuleNotify() {
            segmentMemory.unlinkNodeWithoutRuleNotify(nodePosMaskBit);
        }

        public short getNodeType() {
            return NodeTypeEnums.LeftInputAdapterNode;
        }

        public void setNodeDirty(InternalWorkingMemory wm) {
            segmentMemory.notifyRuleLinkSegment(wm, nodePosMaskBit);
        }

        public void reset() {
            counter = 0;
        }
    }

    /**
     * Used with the updateSink method, so that the parent ObjectSource
     * can  update the  TupleSink
     */
    public static class RightTupleSinkAdapter
            implements
            ObjectSink {
        private LeftTupleSink sink;
        private boolean       leftTupleMemoryEnabled;
        private LeftInputAdapterNode liaNode;

        public RightTupleSinkAdapter(LeftInputAdapterNode liaNode) {
            this.liaNode = liaNode;
        }

        public RightTupleSinkAdapter(final LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
            this.sink = sink;
            this.leftTupleMemoryEnabled = leftTupleMemoryEnabled;
        }

        public void assertObject(final InternalFactHandle factHandle,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
            if ( liaNode != null ) {
                // phreak
                liaNode.assertObject(factHandle, context, workingMemory);
            } else {
                final LeftTuple tuple = this.sink.createLeftTuple( factHandle,
                                                                   this.sink,
                                                                   this.leftTupleMemoryEnabled );
                this.sink.assertLeftTuple( tuple,
                                           context,
                                           workingMemory );
            }
        }

        public void modifyObject(InternalFactHandle factHandle,
                                 ModifyPreviousTuples modifyPreviousTuples,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
            throw new UnsupportedOperationException( "ObjectSinkAdapter onlys supports assertObject method calls" );
        }

        public int getId() {
            return 0;
        }

        public RuleBasePartitionId getPartitionId() {
            return sink.getPartitionId();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // this is a short living adapter class used only during an update operation, and
            // as so, no need for serialization code
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                        ClassNotFoundException {
            // this is a short living adapter class used only during an update operation, and
            // as so, no need for serialization code
        }

        public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                           ModifyPreviousTuples modifyPreviousTuples,
                                           PropagationContext context,
                                           InternalWorkingMemory workingMemory) {
            throw new UnsupportedOperationException();
        }

        public short getType() {
            return NodeTypeEnums.LeftInputAdapterNode;
        }

        public int getAssociationsSize() {
            return sink.getAssociationsSize();
        }

        public int getAssociatedRuleSize() {
            return sink.getAssociatedRuleSize();
        }

        public int getAssociationsSize(Rule rule) {
            return sink.getAssociationsSize(rule);
        }

        public boolean isAssociatedWith( Rule rule ) {
            return sink.isAssociatedWith( rule );
        }

        public boolean thisNodeEquals(final Object object) {
            return false;
        }

        public int nodeHashCode() {return this.hashCode();}
    }

    @Override
    public void setSourcePartitionId(BuildContext context, RuleBasePartitionId partitionId) {
        setSourcePartitionId(objectSource, context, partitionId);
    }

    @Override
    public void setPartitionId(BuildContext context, RuleBasePartitionId partitionId) {
        if (this.partitionId != null && this.partitionId != partitionId) {
            objectSource.sink.changeSinkPartition( (ObjectSink)this, this.partitionId, partitionId, objectSource.alphaNodeHashingThreshold );
        }
        this.partitionId = partitionId;
    }
}