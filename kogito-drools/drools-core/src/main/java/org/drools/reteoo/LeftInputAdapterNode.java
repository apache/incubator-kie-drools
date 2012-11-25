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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Map;

import org.drools.RuleBaseConfiguration;
import org.drools.base.ClassObjectType;
import org.drools.base.DroolsQuery;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.Memory;
import org.drools.common.MemoryFactory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.RuleBasePartitionId;
import org.drools.common.StagedLeftTuples;
import org.drools.common.UpdateContext;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.index.LeftTupleList;
import org.drools.phreak.SegmentUtilities;
import org.drools.reteoo.builder.BuildContext;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleComponent;
import org.kie.definition.rule.Rule;

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
        
        this.unlinkingEnabled = context.getRuleBase().getConfiguration().isUnlinkingEnabled();      
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
        if (context == null) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            this.objectSource.updateSink( this,
                                          propagationContext,
                                          workingMemory );
        }
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
        if ( unlinkingEnabled ) {
            LiaNodeMemory lm = ( LiaNodeMemory ) workingMemory.getNodeMemory( this );
            if ( lm.getSegmentMemory() == null ) {
                SegmentUtilities.createSegmentMemory( this, workingMemory );
            }
            LeftTupleSink sink = getSinkPropagator().getFirstLeftTupleSink();
            LeftTuple leftTuple = sink.createLeftTuple( factHandle, sink, leftTupleMemoryEnabled );
            leftTuple.setPropagationContext( context );
            if ( lm.getSegmentMemory().getStagedLeftTuples().insertSize() == 0 ) {
                lm.linkNode( workingMemory );
            }
            lm.setCounter( lm.getCounter() + 1 );
            lm.getSegmentMemory().getStagedLeftTuples().addInsert( leftTuple );
            return;
        } 
        
        boolean useLeftMemory = true;
        if ( !this.leftTupleMemoryEnabled ) {
            // This is a hack, to not add closed DroolsQuery objects
            Object object = ((InternalFactHandle)context.getFactHandle()).getObject();
            if ( object instanceof DroolsQuery &&  !((DroolsQuery)object).isOpen() ) {
                useLeftMemory = false;
            }
        }        
        
        if ( !workingMemory.isSequential() ) {
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
    
    public static LeftTuple propagateLeftTuples(LeftInputAdapterNode liaNode, LeftTupleList list, int length, InternalWorkingMemory wm) {
        LeftTuple leftTuple = list.getFirst();
        for ( int i = 0; i < length; i++ ) {   
            LeftTuple next =   ( LeftTuple ) leftTuple.getNext();                    
            
            leftTuple.setPrevious( null );
            leftTuple.setNext( null );
            leftTuple.setMemory( null );
            
            leftTuple.getLeftTupleSink().assertLeftTuple( leftTuple,  leftTuple.getPropagationContext(), wm );
            leftTuple.getPropagationContext().evaluateActionQueue( wm );
            leftTuple = next;                      
        }       
        return leftTuple;
    }    

    public void retractLeftTuple(LeftTuple leftTuple,
                                 PropagationContext context,
                                 InternalWorkingMemory workingMemory) {
        if ( isUnlinkingEnabled() ) {
            LiaNodeMemory lm = ( LiaNodeMemory ) workingMemory.getNodeMemory( this );
            SegmentMemory smem = lm.getSegmentMemory();
            
            StagedLeftTuples leftTuples = smem.getStagedLeftTuples();
            switch ( leftTuple.getStagedType() ) {
                // handle clash with already staged entries
                case LeftTuple.INSERT:
                    leftTuples.removeInsert( leftTuple );
                    break;
                case LeftTuple.UPDATE:
                    leftTuples.removeUpdate( leftTuple );
                    break;
            }                        
            
            lm.setCounter( lm.getCounter() - 1 ); // we need this to track when we unlink
            if ( lm.getCounter() == 0 ) {
                lm.unlinkNode( workingMemory );
            }    
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
        this.sink.propagateModifyObject( factHandle,
                                         modifyPreviousTuples,
                                         context,
                                         workingMemory );
    }
    
    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory) {
        this.sink.byPassModifyToBetaNode( factHandle,
                                          modifyPreviousTuples,
                                          context,
                                          workingMemory );        
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
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        if ( !node.isInUse() ) {
            removeTupleSink( (LeftTupleSink) node );
        }
        
        handleUnlinking(context);
        
        this.objectSource.remove( context,
                                  builder,
                                  this,
                                  workingMemories );
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

        return this.objectSource.equals( other.objectSource );
    }

    /**
     * Used with the updateSink method, so that the parent ObjectSource
     * can  update the  TupleSink
     */
    private static class RightTupleSinkAdapter
        implements
        ObjectSink {
        private LeftTupleSink sink;
        private boolean       leftTupleMemoryEnabled;

        public RightTupleSinkAdapter(final LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
            this.sink = sink;
            this.leftTupleMemoryEnabled = leftTupleMemoryEnabled;
        }

        public void assertObject(final InternalFactHandle factHandle,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
            final LeftTuple tuple = this.sink.createLeftTuple( factHandle,
                                                               this.sink,
                                                               this.leftTupleMemoryEnabled );
            this.sink.assertLeftTuple( tuple,
                                       context,
                                       workingMemory );
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

    public Memory createMemory(RuleBaseConfiguration config) {
        return new LiaNodeMemory();
    }    
    
    public static class LiaNodeMemory extends AbstractBaseLinkedListNode<Memory> implements Memory { 
        private int                 counter;
        
        private SegmentMemory        segmentMemory;

        private long                nodePosMaskBit;     
        
        public LiaNodeMemory() {
        }
        
        
        public int getCounter() {
            return counter;
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
        
        public void linkNode(InternalWorkingMemory wm) {
            segmentMemory.linkNode( nodePosMaskBit, wm );        
        }
        
        public void unlinkNode(InternalWorkingMemory wm) {
            segmentMemory.unlinkNode( nodePosMaskBit, wm );        
        }
        

        public short getNodeType() {           
            return NodeTypeEnums.LeftInputAdapterNode;
        }  

    }

}
