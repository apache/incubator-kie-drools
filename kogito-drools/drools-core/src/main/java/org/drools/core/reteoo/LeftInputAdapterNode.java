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

import static org.drools.core.util.BitMaskUtil.intersect;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Deque;
import java.util.Map;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.base.DroolsQuery;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.LeftTupleSets;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.SynchronizedLeftTupleSets;
import org.drools.core.common.UpdateContext;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleComponent;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.kie.api.definition.rule.Rule;

/**
 * All asserting Facts must propagated into the right <code>ObjectSink</code> side of a BetaNode, if this is the first Pattern
 * then there are no BetaNodes to propagate to. <code>LeftInputAdapter</code> is used to adapt an ObjectSink propagation into a
 * <code>TupleSource</code> which propagates a <code>ReteTuple</code> suitable fot the right <code>ReteTuple</code> side
 * of a <code>BetaNode</code>.
 */
public class LeftInputAdapterNode extends LeftTupleSource
    implements
    ObjectSinkNode,
    MemoryFactory {

    private static final long serialVersionUID = 510l;
    private ObjectSource      objectSource;

    private ObjectSinkNode    previousRightTupleSinkNode;
    private ObjectSinkNode    nextRightTupleSinkNode;

    private boolean           leftTupleMemoryEnabled;
    
    protected boolean         rootQueryNode;
    
    protected boolean         unlinkingEnabled;
    private int               unlinkedDisabledCount;
    private int               segmentMemoryIndex;    
    
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
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.objectSource = source;
        this.leftTupleMemoryEnabled = context.isTupleMemoryEnabled();
        ObjectSource current = source;
        while ( !(current.getType() == NodeTypeEnums.ObjectTypeNode) ) {
               current = current.getParentObjectSource();
        }
        ObjectTypeNode otn = ( ObjectTypeNode ) current;
        rootQueryNode = ClassObjectType.DroolsQuery_ObjectType.isAssignableFrom( otn.getObjectType() );
        
        this.unlinkingEnabled = context.getRuleBase().getConfiguration().isPhreakEnabled();
    }    

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        objectSource = (ObjectSource) in.readObject();
        leftTupleMemoryEnabled = in.readBoolean();
        rootQueryNode = in.readBoolean();
        unlinkingEnabled = in.readBoolean();
        unlinkedDisabledCount = in.readInt();        
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( objectSource );
        out.writeBoolean( leftTupleMemoryEnabled );
        out.writeBoolean(  rootQueryNode );
        out.writeBoolean( unlinkingEnabled );
        out.writeInt( unlinkedDisabledCount );        
    }
    
    public ObjectSource getObjectSource() {
        return this.objectSource;
    }
    
    public int getSegmentMemoryIndex() {
        return segmentMemoryIndex;
    }

    public void setSegmentMemoryIndex(int segmentMemoryIndex) {
        this.segmentMemoryIndex = segmentMemoryIndex;
    }
    
    public short getType() {
        return NodeTypeEnums.LeftInputAdapterNode;
    }
    
    public boolean isRootQueryNode() {
        return this.rootQueryNode;
    }
    
    public boolean isUnlinkingEnabled() {
        return unlinkingEnabled;
    }

    @Override
    public boolean isLeftTupleMemoryEnabled() {
        return leftTupleMemoryEnabled;
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
    
    public ObjectSource getParentObjectSource() {
        return this.objectSource;
    }       
    
    public void attach( BuildContext context ) {
        this.objectSource.addObjectSink( this );
        if (context == null || context.getRuleBase().getConfiguration().isPhreakEnabled()) {
            return;
        }

        // we don't call updateSink here yet, as the sink is not yet attached
//        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
//            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
//                                                                                      PropagationContext.RULE_ADDITION,
//                                                                                      null,
//                                                                                      null,
//                                                                                      null );
//            this.objectSource.updateSink( this,
//                                          propagationContext,
//                                          workingMemory );
//        }
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.objectSource.networkUpdated(updateContext);
    }

    /**
     * Takes the asserted <code>FactHandleImpl</code> received from the <code>ObjectSource</code> and puts it
     * in a new <code>ReteTuple</code> before propagating to the <code>TupleSinks</code>
     *
     * @param factHandle
     *            The asserted <code>FactHandle/code>.
     * @param context
     *             The <code>PropagationContext</code> of the <code>WorkingMemory<code> action.
     * @param workingMemory
     *            the <code>WorkingMemory</code> session.
     */
    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        boolean useLeftMemory = true;

        if ( unlinkingEnabled ) {
            LiaNodeMemory lm = ( LiaNodeMemory ) workingMemory.getNodeMemory( this );
            if ( lm.getSegmentMemory() == null ) {
                SegmentUtilities.createSegmentMemory( this, workingMemory );
            }
            
            doInsertObject( factHandle,
                            context,
                            this,
                            workingMemory,
                            lm,
                            true, // queries are handled directly, and not through here
                            useLeftMemory );          
            return;
        } 
            
        
        if ( !workingMemory.isSequential() ) {
            if ( !this.leftTupleMemoryEnabled ) {
                // This is a hack, to not add closed DroolsQuery objects
                Object object = factHandle.getObject();
                if ( object instanceof DroolsQuery ) {
                    if ( !((DroolsQuery)object).isOpen() ) {
                        useLeftMemory = false;
                    }
                }
            }

            this.sink.createAndPropagateAssertLeftTuple( factHandle,
                                                         context,
                                                         workingMemory,
                                                         useLeftMemory, 
                                                         this );
        } else {
            workingMemory.addLIANodePropagation( new LIANodePropagation( this,
                                                                         factHandle,
                                                                         context ) );
        }
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
            if ( sm.isEmpty() ) {
                // liaNode in it's own segment and child segments not yet created
                synchronized ( sm ) {
                    if ( sm.isEmpty() ) {
                        SegmentUtilities.createChildSegments( wm,
                                                              sm,
                                                              liaNode.getSinkPropagator() );
                    }
                }
            }
            sm = sm.getFirst(); // repoint to the child sm
        }

        if ( useLeftMemory && lm.getAndIncreaseCounter() == 0) {
            // if there is no left mempry, then there is no linking or notification
            if ( linkOrNotify ) {
                // link and notify
                lm.linkNode( wm );
            } else {
                // link without notify, when driven by a query, as we don't want it, placed on the agenda
                lm.linkNodeWithoutRuleNotify();
            }
            linkOrNotify = false; // we don't need to do any more notifcations
        }

        LeftTupleSink sink = liaNode.getSinkPropagator().getFirstLeftTupleSink();
        LeftTuple leftTuple = sink.createLeftTuple( factHandle, sink, useLeftMemory );
        leftTuple.setPropagationContext( context );
        long mask = sink.getLeftInferredMask();
        doInsertSegmentMemory(context, wm, linkOrNotify, sm, leftTuple, mask);

        if ( sm.getRootNode() != liaNode ) {
            // sm points to lia child sm, so iterate for all remaining children 
            
            for ( sm = sm.getNext(); sm != null; sm = sm.getNext() ) {
                sink =  sm.getSinkFactory();                
                leftTuple = sink.createPeer( leftTuple ); // pctx is set during peer cloning
                mask = ((LeftTupleSink)sm.getRootNode()).getLeftInferredMask();
                doInsertSegmentMemory(context, wm, linkOrNotify, sm, leftTuple, mask);
            }              
        }
        if( context.getReaderContext() != null && sm != null ) {
            // we are deserializing a session, so we might need to evaluate
            // rule activations immediately
            MarshallerReaderContext mrc = (MarshallerReaderContext) context.getReaderContext();
            mrc.filter.fireRNEAs( wm );
        }
    }

    private static void doInsertSegmentMemory(PropagationContext context, InternalWorkingMemory wm, boolean linkOrNotify, SegmentMemory sm, LeftTuple leftTuple, long mask) {
        if ( context.getType() == PropagationContext.INSERTION ||
                mask == Long.MAX_VALUE ||
                intersect( context.getModificationMask(),  mask) ) {
                // mask check is necessary if insert is a result of a modify

            if ( sm.getStagedLeftTuples().addInsert( leftTuple ) && linkOrNotify  ) {
                // staged is empty, so notify rule, to force re-evaluation.
                sm.notifyRuleLinkSegment(wm);
            }
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
            if ( sm.isEmpty() ) {
                // liaNode in it's own segment and child segments not yet created
                synchronized ( sm ) {
                    if ( sm.isEmpty() ) {
                        SegmentUtilities.createChildSegments( wm,
                                                              sm,
                                                              liaNode.getSinkPropagator() );
                    }
                }
            }
            sm = sm.getFirst(); // repoint to the child sm
        }

        doDeleteSegmentMemory(leftTuple, context, sm, wm, linkOrNotify);

        if ( sm.getNext() != null) {
            // sm points to lia child sm, so iterate for all remaining children

            for ( sm = sm.getNext(); sm != null; sm = sm.getNext() ) {
                // iterate for peers segment memory
                leftTuple = leftTuple.getPeer();
                doDeleteSegmentMemory(leftTuple, context, sm, wm, linkOrNotify);
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

    private static void doDeleteSegmentMemory(LeftTuple leftTuple, PropagationContext context, SegmentMemory sm, InternalWorkingMemory wm, boolean linkOrNotify) {
        LeftTupleSets leftTuples = sm.getStagedLeftTuples();
        if ( leftTuples.addDelete(leftTuple) && linkOrNotify ) {
            // staged is empty, so notify rule, to force re-evaluation
            sm.notifyRuleLinkSegment( wm );
        }
        leftTuple.setPropagationContext( context );

    }

    public static void doUpdateObject(LeftTuple leftTuple,
                                      PropagationContext context,
                                      final InternalWorkingMemory wm,
                                      final LeftInputAdapterNode liaNode,
                                      final boolean linkOrNotify,
                                      SegmentMemory sm) {
        if ( sm.getTipNode() == liaNode) {
            if ( sm.isEmpty() ) {
                // liaNode in it's own segment and child segments not yet created
                synchronized ( sm ) {
                    if ( sm.isEmpty() ) {
                        SegmentUtilities.createChildSegments( wm,
                                                              sm,
                                                              liaNode.getSinkPropagator() );
                    }
                }
            }
            sm = sm.getFirst(); // repoint to the child sm
        }
        
        LeftTupleSets leftTuples = sm.getStagedLeftTuples();
                       
        LeftTupleSink sink = liaNode.getSinkPropagator().getFirstLeftTupleSink() ;

        doUpdateSegmentMemory(leftTuple, context, wm, linkOrNotify, sm, leftTuples, sink);

        if (  sm.getNext() != null ) {
            // sm points to lia child sm, so iterate for all remaining children
            for ( sm = sm.getNext(); sm != null; sm = sm.getNext() ) {
                // iterate for peers segment memory
                leftTuple = leftTuple.getPeer();
                leftTuples = sm.getStagedLeftTuples();

                doUpdateSegmentMemory(leftTuple, context, wm, linkOrNotify, sm, leftTuples, ((LeftTupleSink) sm.getRootNode()));
            }
        }
    }

    private static void doUpdateSegmentMemory(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory wm, boolean linkOrNotify, SegmentMemory sm, LeftTupleSets leftTuples, LeftTupleSink sink) {
        if ( leftTuple.getStagedType() != LeftTuple.INSERT ) {
            // things staged as inserts, are left as inserts and use the pctx associated from the time of insertion
            leftTuple.setPropagationContext( context );
        }
        synchronized ( ((SynchronizedLeftTupleSets)leftTuples).getLock() ) {
            // @TODO I synchronized this, as I'm not 100% of the thread interactions here, it might be possible to remove this later.
            if ( leftTuple.getStagedType() == LeftTuple.NONE ) {
                // if LeftTuple is already staged, leave it there
                long mask = sink.getLeftInferredMask();

                if ( mask == Long.MAX_VALUE ||
                     intersect( context.getModificationMask(),  mask) ) {
                    // only add to staging if masks match

                    if ( leftTuples.addUpdate( leftTuple )  && linkOrNotify ) {
                        // staged is empty, so notify rule, to force re-evaluation
                        sm.notifyRuleLinkSegment(wm);
                    }
                }
            }
        }
    }

    public void retractLeftTuple(LeftTuple leftTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        if ( isUnlinkingEnabled() ) {
            LiaNodeMemory lm = ( LiaNodeMemory ) workingMemory.getNodeMemory( this );
            SegmentMemory smem = lm.getSegmentMemory();
            if ( smem.getTipNode() == this ) { 
                // segment with only a single LiaNode in it, skip to next segment
                // as a liaNode only segment has no staging
                smem = smem.getFirst();
            }
           
            doDeleteObject( leftTuple, context,
                            smem,      
                            workingMemory,
                            this,
                            true,
                            lm );
               
            return;
        }            
        
        leftTuple.getLeftTupleSink().retractLeftTuple( leftTuple,
                                                       context,
                                                       workingMemory );
        
    }

    public void modifyObject(InternalFactHandle factHandle,
                             final ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory workingMemory) {
        if ( unlinkingEnabled ) {                              
            LeftTuple leftTuple = modifyPreviousTuples.peekLeftTuple();
            
            ObjectTypeNode.Id otnId = this.sink.getFirstLeftTupleSink().getLeftInputOtnId();
            while ( leftTuple != null && leftTuple.getLeftTupleSink().getLeftInputOtnId().before( otnId ) ) {
                modifyPreviousTuples.removeLeftTuple();
                
                LeftInputAdapterNode prevLiaNode = (LeftInputAdapterNode) leftTuple.getLeftTupleSink().getLeftTupleSource();
                LiaNodeMemory prevLm = ( LiaNodeMemory ) workingMemory.getNodeMemory( prevLiaNode );
                SegmentMemory prevSm = (SegmentMemory ) prevLm.getSegmentMemory();                
                doDeleteObject( leftTuple, context, prevSm, workingMemory, prevLiaNode, true, prevLm );
                
                leftTuple = modifyPreviousTuples.peekLeftTuple();
            }

            LiaNodeMemory lm = ( LiaNodeMemory ) workingMemory.getNodeMemory( this );
            if ( lm.getSegmentMemory() == null ) {
                SegmentUtilities.createSegmentMemory( this, workingMemory );
            }
            
            if ( leftTuple != null && leftTuple.getLeftTupleSink().getLeftInputOtnId().equals( otnId ) ) {
                modifyPreviousTuples.removeLeftTuple();
                leftTuple.reAdd();
                doUpdateObject( leftTuple, context, workingMemory, (LeftInputAdapterNode) leftTuple.getLeftTupleSink().getLeftTupleSource(), true, lm.getSegmentMemory() );
                
            } else {
                doInsertObject( factHandle, context, this,
                                workingMemory, 
                                lm, true, true);
            }
        } else {
            this.sink.propagateModifyObject( factHandle,
                                             modifyPreviousTuples,
                                             context,
                                             workingMemory );
        }
    }
    
    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory) {
        if ( unlinkingEnabled ) {
            modifyObject(factHandle, modifyPreviousTuples, context, workingMemory);
        } else {
            this.sink.byPassModifyToBetaNode(factHandle,
                                             modifyPreviousTuples,
                                             context,
                                             workingMemory);
        }
    }

    public void updateSink(final LeftTupleSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final RightTupleSinkAdapter adapter = new RightTupleSinkAdapter( sink,
                                                                         true );
        this.objectSource.updateSink( adapter,
                                      context,
                                      workingMemory );
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        if (!isInUse()) {
            objectSource.removeObjectSink(this);
            if ( !context.getRuleBase().getConfiguration().isPhreakEnabled() ) {
                for ( InternalWorkingMemory wm : workingMemories ) {
                    wm.clearNodeMemory( (MemoryFactory) this);
                }
            }
        }

        handleUnlinking(context);
    }

    protected void doCollectAncestors(NodeSet nodeSet) {
        this.objectSource.collectAncestors(nodeSet);
    }
    

    public LeftTuple createPeer(LeftTuple original) {
        return null;
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

    public int hashCode() {
        return this.objectSource.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || !(object instanceof LeftInputAdapterNode) ) {
            return false;
        }

        final LeftInputAdapterNode other = (LeftInputAdapterNode) object;

        return this.objectSource.equals(other.objectSource);
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
        
        public Map<Rule, RuleComponent> getAssociations() {
            return sink.getAssociations();
        }        
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

    public Memory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
        return new LiaNodeMemory();
    }    
    
    public static class LiaNodeMemory extends AbstractBaseLinkedListNode<Memory> implements Memory {
        private int               counter;

        private SegmentMemory     segmentMemory;

        private long              nodePosMaskBit;

        private Deque<RightTuple> dequeu;

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

        public void linkNodeWithoutRuleNotify() {
            segmentMemory.linkNodeWithoutRuleNotify(nodePosMaskBit);
        }

        public void linkNode(InternalWorkingMemory wm) {
            segmentMemory.linkNode(nodePosMaskBit, wm);
        }

        public void unlinkNode(InternalWorkingMemory wm) {
            segmentMemory.unlinkNode(nodePosMaskBit, wm);
        }

        public void unlinkNodeWithoutRuleNotify() {
            segmentMemory.unlinkNodeWithoutRuleNotify(nodePosMaskBit);
        }

        public short getNodeType() {
            return NodeTypeEnums.LeftInputAdapterNode;
        }

    }

}
