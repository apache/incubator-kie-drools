/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.UpdateContext;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.bitmask.EmptyBitMask;

import static org.drools.core.reteoo.PropertySpecificUtil.calculateNegativeMask;
import static org.drools.core.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.core.reteoo.PropertySpecificUtil.getAccessibleProperties;

public abstract class AbstractTerminalNode extends BaseNode implements TerminalNode, PathEndNode, Externalizable {

    private LeftTupleSource tupleSource;

    private BitMask declaredMask = EmptyBitMask.get();
    private BitMask inferredMask = EmptyBitMask.get();
    private BitMask negativeMask = EmptyBitMask.get();

    private LeftTupleNode[] pathNodes;

    private transient PathEndNode[] pathEndNodes;

    private PathMemSpec pathMemSpec;

    public AbstractTerminalNode() { }

    public AbstractTerminalNode(int id, RuleBasePartitionId partitionId, boolean partitionsEnabled, LeftTupleSource source, final BuildContext context) {
        super(id, partitionId, partitionsEnabled);
        this.tupleSource = source;
        context.addPathEndNode(this);
        initMemoryId( context );
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );
        tupleSource = (LeftTupleSource) in.readObject();
        declaredMask = (BitMask) in.readObject();
        inferredMask = (BitMask) in.readObject();
        negativeMask = (BitMask) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( tupleSource );
        out.writeObject(declaredMask);
        out.writeObject(inferredMask);
        out.writeObject(negativeMask);
    }

    @Override
    public PathMemSpec getPathMemSpec() {
        if (pathMemSpec == null) {
            pathMemSpec = calculatePathMemSpec( null );
        }
        return pathMemSpec;
    }

    @Override
    public void resetPathMemSpec(TerminalNode removingTN) {
        pathMemSpec = removingTN == null ? null : calculatePathMemSpec( null, removingTN );
    }

    @Override
    public void setPathEndNodes(PathEndNode[] pathEndNodes) {
        this.pathEndNodes = pathEndNodes;
    }

    @Override
    public PathEndNode[] getPathEndNodes() {
        return pathEndNodes;
    }

    public int getPositionInPath() {
        return tupleSource.getPositionInPath() + 1;
    }

    public void initDeclaredMask(BuildContext context) {
        if ( !(unwrapTupleSource() instanceof LeftInputAdapterNode)) {
            // RTN's not after LIANode are not relevant for property specific, so don't block anything.
            setDeclaredMask( AllSetBitMask.get() );
            return;
        }

        Pattern pattern = context.getLastBuiltPatterns()[0];
        ObjectType objectType = pattern.getObjectType();

        if ( !(objectType instanceof ClassObjectType) ) {
            // InitialFact has no type declaration and cannot be property specific
            // Only ClassObjectType can use property specific
            setDeclaredMask( AllSetBitMask.get() );
            return;
        }

        Class objectClass = ((ClassObjectType)objectType).getClassType();
        TypeDeclaration typeDeclaration = context.getKnowledgeBase().getTypeDeclaration(objectClass);
        if (  typeDeclaration == null || !typeDeclaration.isPropertyReactive() ) {
            // if property specific is not on, then accept all modification propagations
            setDeclaredMask( AllSetBitMask.get() );
        } else  {
            List<String> settableProperties = getAccessibleProperties( context.getKnowledgeBase(), objectClass );
            Class modifiedClass = (( ClassObjectType ) pattern.getObjectType()).getClassType();
            setDeclaredMask( calculatePositiveMask(modifiedClass, pattern.getListenedProperties(), settableProperties) );
            setNegativeMask( calculateNegativeMask(modifiedClass, pattern.getListenedProperties(), settableProperties) );
        }
    }

    public void initInferredMask() {
        LeftTupleSource leftTupleSource = unwrapTupleSource();
        if ( leftTupleSource instanceof LeftInputAdapterNode && ((LeftInputAdapterNode)leftTupleSource).getParentObjectSource() instanceof AlphaNode ) {
            AlphaNode alphaNode = (AlphaNode) ((LeftInputAdapterNode)leftTupleSource).getParentObjectSource();
            setInferredMask( alphaNode.updateMask( getDeclaredMask() ) );
        } else {
            setInferredMask(  getDeclaredMask() );
        }

        setInferredMask( getInferredMask().resetAll( getNegativeMask() ) );
    }

    public LeftTupleSource unwrapTupleSource() {
        return tupleSource instanceof FromNode ? tupleSource.getLeftTupleSource() : tupleSource;
    }

    public abstract RuleImpl getRule();
    

    public PathMemory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
        return initPathMemory( this, new PathMemory(this, wm) );
    }

    public static PathMemory initPathMemory( PathEndNode pathEndNode, PathMemory pmem ) {
        PathMemSpec pathMemSpec = pathEndNode.getPathMemSpec();
        pmem.setAllLinkedMaskTest(pathMemSpec. allLinkedTestMask );
        pmem.setSegmentMemories( new SegmentMemory[pathMemSpec.smemCount] );
        return pmem;
    }

    public LeftTuple createPeer(LeftTuple original) {
        RuleTerminalNodeLeftTuple peer = new RuleTerminalNodeLeftTuple();
        peer.initPeer( (BaseLeftTuple) original, this );
        original.setPeer( peer );
        return peer;
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder) {
        getLeftTupleSource().removeTupleSink(this);
        this.tupleSource = null;
        return true;
    }

    public LeftTupleSource getLeftTupleSource() {
        return this.tupleSource;
    }

    public BitMask getDeclaredMask() {
        return declaredMask;
    }

    public BitMask getInferredMask() {
        return inferredMask;
    }
    
    public BitMask getLeftInferredMask() {
        return inferredMask;
    }

    public void setDeclaredMask(BitMask mask) {
        declaredMask = mask;
    }

    public void setInferredMask(BitMask mask) {
        inferredMask = mask;
    }

    public BitMask getNegativeMask() {
        return negativeMask;
    }

    public void setNegativeMask(BitMask mask) {
        negativeMask = mask;
    }

    public void networkUpdated(UpdateContext updateContext) {
        getLeftTupleSource().networkUpdated(updateContext);
    }

    public boolean isInUse() {
        return false;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return false;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        // do nothing, this can only ever be false
    }

    public static LeftTupleNode[] getPathNodes(PathEndNode endNode) {
        LeftTupleNode[] pathNodes = new LeftTupleNode[endNode.getPositionInPath()+1];
        for (LeftTupleNode node = endNode; node != null; node = node.getLeftTupleSource()) {
            pathNodes[node.getPositionInPath()] = node;
        }
        return pathNodes;
    }

    public LeftTupleNode[] getPathNodes() {
        if (pathNodes == null) {
            pathNodes = getPathNodes( this );
        }
        return pathNodes;
    }

    public final boolean hasPathNode(LeftTupleNode node) {
        for (LeftTupleNode pathNode : getPathNodes()) {
            if (node.getId() == pathNode.getId()) {
                return true;
            }
        }
        return false;
    }

    public final boolean isTerminalNodeOf(LeftTupleNode node) {
        for (PathEndNode pathEndNode : getPathEndNodes()) {
            if (pathEndNode.hasPathNode( node )) {
                return true;
            }
        }
        return false;
    }

    public LeftTupleSinkPropagator getSinkPropagator() {
        return EmptyLeftTupleSinkAdapter.getInstance();
    }

    @Override
    public final void setPartitionIdWithSinks( RuleBasePartitionId partitionId ) {
        this.partitionId = partitionId;
    }

    @Override
    public ObjectTypeNode getObjectTypeNode() {
        return getLeftTupleSource().getObjectTypeNode();
    }
}
