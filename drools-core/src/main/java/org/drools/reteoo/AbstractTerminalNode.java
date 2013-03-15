package org.drools.reteoo;

import org.drools.RuleBaseConfiguration;
import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.phreak.SegmentUtilities;
import org.drools.reteoo.builder.BuildContext;
import org.drools.reteoo.RightInputAdapterNode.RiaNodeMemory;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import static org.drools.reteoo.PropertySpecificUtil.calculateNegativeMask;
import static org.drools.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.reteoo.PropertySpecificUtil.getSettableProperties;

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
            setDeclaredMask( Long.MAX_VALUE );
            return;
        }

        Pattern pattern = context.getLastBuiltPatterns()[0];
        ObjectType objectType = pattern.getObjectType();

        if ( !(objectType instanceof ClassObjectType) ) {
            // InitialFact has no type declaration and cannot be property specific
            // Only ClassObjectType can use property specific
            setDeclaredMask( Long.MAX_VALUE );
            return;
        }

        Class objectClass = ((ClassObjectType)objectType).getClassType();
        TypeDeclaration typeDeclaration = context.getRuleBase().getTypeDeclaration(objectClass);
        if (  typeDeclaration == null || !typeDeclaration.isPropertyReactive() ) {
            // if property specific is not on, then accept all modification propagations
            setDeclaredMask( Long.MAX_VALUE );
        } else  {
            List<String> settableProperties = getSettableProperties(context.getRuleBase(), objectClass);
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

        setInferredMask( getInferredMask() & (Long.MAX_VALUE - getNegativeMask() ) );
    }

    public LeftTupleSource unwrapTupleSource() {
        return tupleSource instanceof FromNode ? ((FromNode)tupleSource).getLeftTupleSource() : tupleSource;
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTupleSource.doModifyLeftTuple( factHandle, modifyPreviousTuples, context, workingMemory,
                                           this, getLeftInputOtnId(), inferredMask);
    }
    
    public abstract Rule getRule();
    

    public Memory createMemory(RuleBaseConfiguration config, InternalWorkingMemory wm) {
        PathMemory rmem = new PathMemory(this);
        initPathMemory(rmem, getLeftTupleSource(), null, wm );
        return rmem;
    }

    /**
     * Creates and return the node memory
     */
    public static void initPathMemory(PathMemory pmem, LeftTupleSource tupleSource, LeftTupleSource startTupleSource, InternalWorkingMemory wm) {
            int counter = 0;
            long allLinkedTestMask = 0;

            if ( tupleSource.getSinkPropagator().size() > 1 ) {
                // it's shared, RTN is in it's own segment, so increase segmentCount
                counter++;
            }
            boolean updateBitInNewSegment = true; // avoids more than one isBetaNode check per segment
            boolean updateAllLinkedTest = true;
            while (  tupleSource.getType() != NodeTypeEnums.LeftInputAdapterNode ) {
                if ( updateAllLinkedTest && updateBitInNewSegment && NodeTypeEnums.isBetaNode( tupleSource )) {
                    updateBitInNewSegment = false;
                    BetaNode bn = ( BetaNode) tupleSource;
                    if ( bn.isRightInputIsRiaNode() ) {
                        // only ria's without reactive subnetworks can be disabled and thus need checking
                        // The getNodeMemory will call this method recursive for sub networks it reaches
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

                if ( !SegmentUtilities.parentInSameSegment( tupleSource ) ) {
                    updateBitInNewSegment = true;
                    allLinkedTestMask = allLinkedTestMask << 1;
                    counter++;
                }

                tupleSource = tupleSource.getLeftTupleSource();
                if ( tupleSource == startTupleSource ) {
                    // stop tracking if we move outside of a subnetwork boundary (if one is set)
                    updateAllLinkedTest = false;
                }
            }

            if ( updateAllLinkedTest && tupleSource.getSinkPropagator().size() > 1 ) {
                // tupleSource == LeftInputAdapterNode
                allLinkedTestMask = allLinkedTestMask | 1;
            }
            pmem.setAllLinkedMaskTest( allLinkedTestMask );
            pmem.setSegmentMemories( new SegmentMemory[counter + 1] ); // +1 as arras are zero based.
    }

    public LeftTuple createPeer(LeftTuple original) {
        RuleTerminalNodeLeftTuple peer = new RuleTerminalNodeLeftTuple();
        peer.initPeer( (BaseLeftTuple) original, this );
        original.setPeer( peer );
        return peer;
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
