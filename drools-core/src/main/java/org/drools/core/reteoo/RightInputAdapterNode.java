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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.UpdateContext;
import org.drools.core.marshalling.impl.PersisterHelper;
import org.drools.core.marshalling.impl.ProtobufInputMarshaller;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.definition.rule.Rule;

/**
 * When joining a subnetwork into the main network again, RightInputAdapterNode adapts the
 * subnetwork's tuple into a fact in order right join it with the tuple being propagated in
 * the main network.
 */
public class RightInputAdapterNode extends ObjectSource
    implements
    LeftTupleSinkNode,
    PathEndNode,
    MemoryFactory<RightInputAdapterNode.RiaNodeMemory> {

    private static final long serialVersionUID = 510l;

    private LeftTupleSource   tupleSource;
    
    private LeftTupleSource   startTupleSource;

    private boolean           tupleMemoryEnabled;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    private LeftTupleNode[] pathNodes;

    private PathEndNode[] pathEndNodes;

    public RightInputAdapterNode() {
    }

    /**
     * Constructor specifying the unique id of the node in the Rete network, the position of the propagating <code>FactHandleImpl</code> in
     * <code>ReteTuple</code> and the source that propagates the receive <code>ReteTuple<code>s.
     *
     * @param id
     *      Unique id
     * @param source
     *      The <code>TupleSource</code> which propagates the received <code>ReteTuple</code>
     */
    public RightInputAdapterNode(final int id,
                                 final LeftTupleSource source,
                                 final LeftTupleSource startTupleSource,
                                 final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getKnowledgeBase().getConfiguration().isMultithreadEvaluation() );
        this.tupleSource = source;
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();
        this.startTupleSource = startTupleSource;

        hashcode = calculateHashCode();
        initMemoryId( context );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        tupleMemoryEnabled = in.readBoolean();
        previousTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        nextTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        startTupleSource = ( LeftTupleSource ) in.readObject();
        pathEndNodes = ( PathEndNode[] ) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeObject( previousTupleSinkNode );
        out.writeObject( nextTupleSinkNode );
        out.writeObject( startTupleSource );
        out.writeObject( pathEndNodes );
    }

    @Override
    public void setPathEndNodes(PathEndNode[] pathEndNodes) {
        this.pathEndNodes = pathEndNodes;
    }

    @Override
    public PathEndNode[] getPathEndNodes() {
        return pathEndNodes;
    }

    public LeftTupleSource getStartTupleSource() {
        return startTupleSource;
    }

    public int getPositionInPath() {
        return tupleSource.getPositionInPath() + 1;
    }

    /**
     * Creates and return the node memory
     */    
    public RiaNodeMemory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        RiaNodeMemory rianMem = new RiaNodeMemory();

        RiaPathMemory pmem = new RiaPathMemory(this, wm);
        AbstractTerminalNode.initPathMemory(pmem, getStartTupleSource(), wm, null);
        rianMem.setRiaPathMemory(pmem);
        
        return rianMem;
    }
    
    public LeftTuple createPeer(LeftTuple original) {
        SubnetworkTuple peer = new SubnetworkTuple();
        peer.initPeer( (BaseLeftTuple) original, this );
        original.setPeer( peer );
        peer.setStagedOnRight( peer.isStagedOnRight() );
        return peer;
    }     

    @SuppressWarnings("unchecked")
    public InternalFactHandle createFactHandle(final LeftTuple leftTuple,
                                                final PropagationContext context,
                                                final InternalWorkingMemory workingMemory) {
        InternalFactHandle handle;
        ProtobufMessages.FactHandle _handle = null;
        if( context.getReaderContext() != null ) {
            Map<ProtobufInputMarshaller.TupleKey, ProtobufMessages.FactHandle> map = (Map<ProtobufInputMarshaller.TupleKey, ProtobufMessages.FactHandle>) context.getReaderContext().nodeMemories.get( getId() );
            if( map != null ) {
                _handle = map.get( PersisterHelper.createTupleKey( leftTuple ) );
            }
        }
        if( _handle != null ) {
            // create a handle with the given id
            handle = workingMemory.getFactHandleFactory().newFactHandle( _handle.getId(),
                                                                         leftTuple,
                                                                         _handle.getRecency(),
                                                                         workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( context.getEntryPoint(),
                                                                                                                                               leftTuple ),
                                                                         workingMemory,
                                                                         null ); // so far, result is not an event
        } else {
            handle = workingMemory.getFactHandleFactory().newFactHandle( leftTuple,
                                                                         workingMemory.getObjectTypeConfigurationRegistry().getObjectTypeConf( context.getEntryPoint(),
                                                                                                                                               leftTuple ),
                                                                         workingMemory,
                                                                         null ); // so far, result is not an event
        }
        return handle;
    }

    public void attach( BuildContext context ) {
        this.tupleSource.addTupleSink( this, context );
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.tupleSource.networkUpdated(updateContext);
    }


    protected boolean doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        if ( !isInUse() ) {
            tupleSource.removeTupleSink(this);
            return true;
        }
        return false;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
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

    public short getType() {
        return NodeTypeEnums.RightInputAdaterNode;
    }

    private int calculateHashCode() {
        return this.tupleSource.hashCode() * 17 + ((this.tupleMemoryEnabled) ? 1234 : 4321);
    }

    @Override
    public boolean equals(Object object) {
        return this == object ||
               ( internalEquals( object ) && this.tupleSource.thisNodeEquals( ((RightInputAdapterNode)object).tupleSource ) );
    }

    @Override
    protected boolean internalEquals( Object object ) {
        return object instanceof RightInputAdapterNode && this.hashCode() == object.hashCode() &&
               this.tupleMemoryEnabled == ( (RightInputAdapterNode) object ).tupleMemoryEnabled;
    }

    @Override
    public String toString() {
        return "RightInputAdapterNode(" + id + ")[ tupleMemoryEnabled=" + tupleMemoryEnabled + ", tupleSource=" + tupleSource + ", source="
               + source + ", associations=" + associations + ", partitionId=" + partitionId + "]";
    }
    
    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new SubnetworkTuple(factHandle, sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return new SubnetworkTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx,
                                     boolean leftTupleMemoryEnabled) {
        return new SubnetworkTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new SubnetworkTuple(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new SubnetworkTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );
    }

    public LeftTupleSource getLeftTupleSource() {
        return this.tupleSource;
    }

    public void setTupleSource( LeftTupleSource tupleSource ) {
        this.tupleSource = tupleSource;
    }

    public ObjectTypeNode.Id getLeftInputOtnId() {
        throw new UnsupportedOperationException();
    }

    public void setLeftInputOtnId(ObjectTypeNode.Id leftInputOtnId) {
        throw new UnsupportedOperationException();
    }      
    
    @Override
    public BitMask calculateDeclaredMask(List<String> settableProperties) {
        throw new UnsupportedOperationException();
    }

    public static class RiaNodeMemory extends AbstractBaseLinkedListNode<Memory> implements Memory {
        private RiaPathMemory pathMemory;

        public RiaNodeMemory() {
        }

        public RiaPathMemory getRiaPathMemory() {
            return pathMemory;
        }

        public void setRiaPathMemory(RiaPathMemory pathMemory) {
            this.pathMemory = pathMemory;
        }

        public SegmentMemory getSegmentMemory() {
            return pathMemory.getSegmentMemory();
        }

        public void setSegmentMemory(SegmentMemory segmentMemory) {
            pathMemory.setSegmentMemory(segmentMemory);
        }

        public short getNodeType() {
            return NodeTypeEnums.RightInputAdaterNode;
        }

        public void reset() {
            pathMemory.reset();
        }
    }

    public BitMask getLeftInferredMask() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateSink(ObjectSink sink, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void assertLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retractLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    public LeftTupleNode[] getPathNodes() {
        if (pathNodes == null) {
            pathNodes = AbstractTerminalNode.getPathNodes( this );
        }
        return pathNodes;
    }

    public boolean hasPathNode(LeftTupleNode node) {
        for (LeftTupleNode pathNode : getPathNodes()) {
            if (node.getId() == pathNode.getId()) {
                return true;
            }
        }
        return false;
    }

    public LeftTupleSinkPropagator getSinkPropagator() {
        return EmptyLeftTupleSinkAdapter.getInstance();
    }

    @Override
    public void addAssociation( BuildContext context, Rule rule ) {
        super.addAssociation(context, rule);
        context.addPathEndNode( this );
    }

    @Override
    public boolean removeAssociation( Rule rule ) {
        boolean result = super.associations.remove(rule);
        if (getAssociationsSize() == 0) {
            // avoid to recalculate the pathEndNodes if this node is going to be removed
            return result;
        }

        List<PathEndNode> remainingPathNodes = new ArrayList<PathEndNode>();
        for (PathEndNode pathEndNode : pathEndNodes) {
            if (pathEndNode.getAssociationsSize() > 0) {
                remainingPathNodes.add(pathEndNode);
            }
        }
        pathEndNodes = remainingPathNodes.toArray( new PathEndNode[remainingPathNodes.size()] );
        return result;
    }
}
