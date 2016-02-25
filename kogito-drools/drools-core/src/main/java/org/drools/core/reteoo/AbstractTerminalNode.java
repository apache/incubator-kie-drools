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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.UpdateContext;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.bitmask.EmptyBitMask;
import org.kie.api.definition.rule.Rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import static org.drools.core.reteoo.PropertySpecificUtil.*;

public abstract class AbstractTerminalNode extends BaseNode implements TerminalNode, PathEndNode, Externalizable {

    private LeftTupleSource tupleSource;

    private BitMask declaredMask = EmptyBitMask.get();
    private BitMask inferredMask = EmptyBitMask.get();
    private BitMask negativeMask = EmptyBitMask.get();

    private LeftTupleNode[] pathNodes;

    private transient PathEndNode[] pathEndNodes;

    public AbstractTerminalNode() { }


    public AbstractTerminalNode(int id, RuleBasePartitionId partitionId, boolean partitionsEnabled, LeftTupleSource source, final BuildContext context) {
        super(id, partitionId, partitionsEnabled);
        this.tupleSource = source;
        context.getPathEndNodes().add(this);
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
            List<String> settableProperties = getSettableProperties(context.getKnowledgeBase(), objectClass);
            setDeclaredMask( calculatePositiveMask(pattern.getListenedProperties(), settableProperties) );
            setNegativeMask( calculateNegativeMask(pattern.getListenedProperties(), settableProperties) );
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

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTupleSourceUtils.doModifyLeftTuple(factHandle, modifyPreviousTuples, context, workingMemory,
                                               this, getLeftInputOtnId(), inferredMask);
    }
    
    public abstract RuleImpl getRule();
    

    public PathMemory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
        PathMemory pmem = new PathMemory(this);
        initPathMemory(pmem, this, null, wm, null );
        return pmem;
    }

    /**
     * Creates and return the node memory
     */
    public static void initPathMemory(PathMemory pmem, LeftTupleNode endNode, LeftTupleSource startTupleSource, InternalWorkingMemory wm, Rule removingRule) {
        int counter = 1;
        long allLinkedTestMask = 0;

        LeftTupleSource tupleSource = endNode.getLeftTupleSource();
        if ( SegmentUtilities.isRootNode(endNode, removingRule)) {
            counter++;
        }

        ConditionalBranchNode cen = getConditionalBranchNode(tupleSource); // segments after a branch CE can notify, but they cannot impact linking
        // @TODO optimization would be to split path's into two, to avoid wasted rule evaluation for segments after the first branch CE

        boolean updateBitInNewSegment = true; // Avoids more than one isBetaNode check per segment
        boolean updateAllLinkedTest = cen == null; // if there is a CEN, do not set bit until it's reached
        boolean subnetworkBoundaryCrossed = false;
        while (  tupleSource.getType() != NodeTypeEnums.LeftInputAdapterNode ) {
            if ( !subnetworkBoundaryCrossed &&  tupleSource.getType() == NodeTypeEnums.ConditionalBranchNode ) {
                // start recording now we are after the BranchCE, but only if we are not outside the target
                // subnetwork
                updateAllLinkedTest = true;
            }

            if ( updateAllLinkedTest && updateBitInNewSegment &&
                 NodeTypeEnums.isBetaNode( tupleSource ) &&
                 NodeTypeEnums.AccumulateNode != tupleSource.getType()) { // accumulates can never be disabled
                BetaNode bn = ( BetaNode) tupleSource;
                if ( bn.isRightInputIsRiaNode() ) {
                    updateBitInNewSegment = false;
                    // only ria's without reactive subnetworks can be disabled and thus need checking
                    // The getNodeMemory will call this method recursive for sub networks it reaches
                    RiaNodeMemory rnmem = ( RiaNodeMemory ) wm.getNodeMemory((MemoryFactory) bn.getRightInput());
                    if ( rnmem.getRiaPathMemory().getAllLinkedMaskTest() != 0 ) {
                        allLinkedTestMask = allLinkedTestMask | 1;
                    }
                } else if ( NodeTypeEnums.NotNode != bn.getType() || ((NotNode)bn).isEmptyBetaConstraints()) {
                    updateBitInNewSegment = false;
                    // non empty not nodes can never be disabled and thus don't need checking
                    allLinkedTestMask = allLinkedTestMask | 1;
                }
            }

            if ( SegmentUtilities.isRootNode( tupleSource, removingRule ) ) {
                updateBitInNewSegment = true; // allow bit to be set for segment
                allLinkedTestMask = allLinkedTestMask << 1;
                counter++;
            }

            tupleSource = tupleSource.getLeftTupleSource();
            if ( tupleSource == startTupleSource ) {
                // stop tracking if we move outside of a subnetwork boundary (if one is set)
                subnetworkBoundaryCrossed = true;
                updateAllLinkedTest = false;
            }
        }

        if ( !subnetworkBoundaryCrossed ) {
            allLinkedTestMask = allLinkedTestMask | 1;
        }

        pmem.setAllLinkedMaskTest( allLinkedTestMask );
        pmem.setSegmentMemories( new SegmentMemory[counter] );
    }

    private static ConditionalBranchNode getConditionalBranchNode(LeftTupleSource tupleSource) {
        ConditionalBranchNode cen = null;
        while (  tupleSource.getType() != NodeTypeEnums.LeftInputAdapterNode ) {
            // find the first ConditionalBranch, if one exists
            if ( tupleSource.getType() == NodeTypeEnums.ConditionalBranchNode ) {
                cen =  ( ConditionalBranchNode ) tupleSource;
            }
            tupleSource = tupleSource.getLeftTupleSource();
        }
        return cen;
    }

    public LeftTuple createPeer(LeftTuple original) {
        RuleTerminalNodeLeftTuple peer = new RuleTerminalNodeLeftTuple();
        peer.initPeer( (BaseLeftTuple) original, this );
        original.setPeer( peer );
        return peer;
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder,
                               final InternalWorkingMemory[] workingMemories) {
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

    public LeftTupleSinkPropagator getSinkPropagator() {
        return EmptyLeftTupleSinkAdapter.getInstance();
    }
}
