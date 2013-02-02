package org.drools.reteoo;

import org.drools.RuleBaseConfiguration;
import org.drools.base.ClassObjectType;
import org.drools.common.BaseNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.Memory;
import org.drools.common.MemoryFactory;
import org.drools.common.RuleBasePartitionId;
import org.drools.phreak.SegmentUtilities;
import org.drools.reteoo.builder.BuildContext;
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
    

    public Memory createMemory(RuleBaseConfiguration config) {
        int segmentCount = 1; // always atleast one segment
        
        if ( getLeftTupleSource().getSinkPropagator().size() > 1 ) {
            // it's shared, RTN is in it's own segment, so increase segmentCount
            segmentCount++;
        }
        
        int segmentPosMask = 1;
        long allLinkedTestMask = 1;        
        PathMemory rmem = new PathMemory(this);
        LeftTupleSource tupleSource = getLeftTupleSource();
        boolean updateBitInNewSegment = false; // this is so we can handle segments that don't have betanode's, as their bit will never be set
        while ( tupleSource.getLeftTupleSource() != null ) {            
            if ( !SegmentUtilities.parentInSameSegment( tupleSource ) ) {
                updateBitInNewSegment = true;
                segmentPosMask = segmentPosMask << 1;  
                segmentCount++;
            }
            
            if ( updateBitInNewSegment && NodeTypeEnums.isBetaNode( tupleSource )) {
                updateBitInNewSegment = false;
                allLinkedTestMask = allLinkedTestMask | segmentPosMask;
            }
            
            tupleSource = tupleSource.getLeftTupleSource();            
        }        
        rmem.setAllLinkedMaskTest( allLinkedTestMask );
        rmem.setSegmentMemories( new SegmentMemory[segmentCount] );
        return rmem;
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
