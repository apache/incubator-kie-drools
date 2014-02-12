package org.drools.core.reteoo;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.phreak.SegmentUtilities;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import static org.drools.core.reteoo.PropertySpecificUtil.calculateNegativeMask;
import static org.drools.core.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.core.reteoo.PropertySpecificUtil.getSettableProperties;

public abstract class AbstractTerminalNode extends BaseNode implements TerminalNode, MemoryFactory, Externalizable {

    private LeftTupleSource tupleSource;

    private long declaredMask;
    private long inferredMask;
    private long negativeMask;

    public AbstractTerminalNode() { }

    public AbstractTerminalNode(int id, RuleBasePartitionId partitionId, boolean partitionsEnabled, LeftTupleSource source) {
        super(id, partitionId, partitionsEnabled);
        this.tupleSource = source;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );
        tupleSource = (LeftTupleSource) in.readObject();
        declaredMask = in.readLong();
        inferredMask = in.readLong();
        negativeMask = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( tupleSource );
        out.writeLong(declaredMask);
        out.writeLong(inferredMask);
        out.writeLong(negativeMask);
    }

    public void initDeclaredMask(BuildContext context) {
        if ( !(unwrapTupleSource() instanceof LeftInputAdapterNode)) {
            // RTN's not after LIANode are not relevant for property specific, so don't block anything.
            setDeclaredMask( -1L );
            return;
        }

        Pattern pattern = context.getLastBuiltPatterns()[0];
        ObjectType objectType = pattern.getObjectType();

        if ( !(objectType instanceof ClassObjectType) ) {
            // InitialFact has no type declaration and cannot be property specific
            // Only ClassObjectType can use property specific
            setDeclaredMask( -1L );
            return;
        }

        Class objectClass = ((ClassObjectType)objectType).getClassType();
        TypeDeclaration typeDeclaration = context.getKnowledgeBase().getTypeDeclaration(objectClass);
        if (  typeDeclaration == null || !typeDeclaration.isPropertyReactive() ) {
            // if property specific is not on, then accept all modification propagations
            setDeclaredMask( -1L );
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

        setInferredMask( getInferredMask() & ( -1L - getNegativeMask() ) );
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
    

    public Memory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
        PathMemory pmem = new PathMemory(this);
        initPathMemory(pmem, getLeftTupleSource(), null, wm, null );
        return pmem;
    }

    /**
     * Creates and return the node memory
     */
    public static void initPathMemory(PathMemory pmem, LeftTupleSource tupleSource, LeftTupleSource startTupleSource, InternalWorkingMemory wm, RuleImpl removingRule) {
        int counter = 0;
        long allLinkedTestMask = 0;


        int size = tupleSource.getSinkPropagator().size();
        if ( size > 2 ) {
            counter++;
        } else if ( size == 2 && ( removingRule == null || !tupleSource.getAssociations().containsKey( removingRule )  ) ) {
            counter++;
        }

        ConditionalBranchNode cen = getConditionalBranchNode(tupleSource); // segments after a branch CE can notify, but they cannot impact linking
        // @TODO optimization would be to split path's into two, to avoid wasted rule evaluation for segments after the first branch CE

        boolean updateBitInNewSegment = true; // Avoids more than one isBetaNode check per segment
        boolean updateAllLinkedTest = ( cen == null ) ? true : false; // if there is a CEN, do not set bit until it's reached
        boolean subnetworkBoundaryCrossed = false;
        while (  tupleSource.getType() != NodeTypeEnums.LeftInputAdapterNode ) {
            if ( !subnetworkBoundaryCrossed &&  tupleSource.getType() == NodeTypeEnums.ConditionalBranchNode ) {
                // start recording now we are after the BranchCE, but only if we are not outside the target
                // subnetwork
                updateAllLinkedTest = true;
            }

            if ( updateAllLinkedTest && updateBitInNewSegment && NodeTypeEnums.isBetaNode( tupleSource )) {
                updateBitInNewSegment = false;
                BetaNode bn = ( BetaNode) tupleSource;
                if ( bn.isRightInputIsRiaNode() ) {
                    // only ria's without reactive subnetworks can be disabled and thus need checking
                    // The getNodeMemory will7 call this method recursive for sub networks it reaches
                    RiaNodeMemory rnmem = ( RiaNodeMemory ) wm.getNodeMemory((MemoryFactory) bn.getRightInput());
                    if ( rnmem.getRiaPathMemory().getAllLinkedMaskTest() != 0 ) {
                        allLinkedTestMask = allLinkedTestMask | 1;
                    }
                } else if ( ( !(NodeTypeEnums.NotNode == bn.getType() && !((NotNode)bn).isEmptyBetaConstraints()) &&
                              NodeTypeEnums.AccumulateNode != bn.getType()) )  {
                    // non empty not nodes and accumulates can never be disabled and thus don't need checking
                    allLinkedTestMask = allLinkedTestMask | 1;
                }
            }

            if ( !SegmentUtilities.parentInSameSegment( tupleSource, removingRule ) ) {
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
        pmem.setlinkedSegmentMask(0);
        pmem.setSegmentMemories( new SegmentMemory[counter + 1] ); // +1 as arras are zero based.
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

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        getLeftTupleSource().removeTupleSink(this);
        this.tupleSource = null;
    }

    public LeftTupleSource getLeftTupleSource() {
        return this.tupleSource;
    }

    public long getDeclaredMask() {
        return declaredMask;
    }

    public long getInferredMask() {
        return inferredMask;
    }
    
    public long getLeftInferredMask() {
        return inferredMask;
    }

    public void setDeclaredMask(long mask) {
        declaredMask = mask;
    }

    public void setInferredMask(long mask) {
        inferredMask = mask;
    }

    public long getNegativeMask() {
        return negativeMask;
    }

    public void setNegativeMask(long mask) {
        negativeMask = mask;
    }
}
