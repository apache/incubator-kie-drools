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

import static org.drools.reteoo.PropertySpecificUtil.calculateNegativeMask;
import static org.drools.reteoo.PropertySpecificUtil.calculatePositiveMask;
import static org.drools.reteoo.PropertySpecificUtil.getSettableProperties;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.drools.base.ClassObjectType;
import org.drools.common.AgendaItem;
import org.drools.common.BaseNode;
import org.drools.common.EventSupport;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.ScheduledAgendaItem;
import org.drools.common.UpdateContext;
import org.drools.event.rule.ActivationCancelledCause;
import org.drools.reteoo.RuleRemovalContext.CleanupAdapter;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Pattern;
import org.drools.rule.Rule;
import org.drools.rule.TypeDeclaration;
import org.drools.spi.Activation;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

/**
 * Leaf Rete-OO node responsible for enacting <code>Action</code> s on a
 * matched <code>Rule</code>.
 *
 * @see org.drools.rule.Rule
 */
public class RuleTerminalNode extends BaseNode
        implements
        TerminalNode,
        Externalizable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private int                           sequence         = -1;  // -1 means not set

    private static final long             serialVersionUID = 510l;

    /** The rule to invoke upon match. */
    private Rule                          rule;
    /**
     * the subrule reference is needed to resolve declarations
     * because declarations may have different offsets in each subrule
     */
    private GroupElement                  subrule;
    private int                           subruleIndex;
    private LeftTupleSource               tupleSource;
    private Declaration[]                 declarations;

    private LeftTupleSinkNode             previousTupleSinkNode;
    private LeftTupleSinkNode             nextTupleSinkNode;

    private boolean                       fireDirect;

    private long             declaredMask;
    private long             inferredMask;
    private long             negativeMask;
    
    private int              leftInputOtnId;    

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public RuleTerminalNode() {

    }

    /**
     *
     * @param id
     * @param source
     * @param rule
     * @param subrule
     * @param subruleIndex
     * @param context
     */
    public RuleTerminalNode(final int id,
                            final LeftTupleSource source,
                            final Rule rule,
                            final GroupElement subrule,
                            final int subruleIndex,
                            final BuildContext context) {
        super(id,
                context.getPartitionId(),
                context.getRuleBase().getConfiguration().isMultithreadEvaluation());
        this.rule = rule;
        this.tupleSource = source;
        this.subrule = subrule;
        this.subruleIndex = subruleIndex;

        Map<String, Declaration> decls = this.subrule.getOuterDeclarations();
        this.declarations = new Declaration[rule.getRequiredDeclarations().length];
        int i = 0;
        for ( String str : rule.getRequiredDeclarations() ) {
            this.declarations[i++] = decls.get( str );
        }
        Arrays.sort( this.declarations, SortDeclarations.instance );
        setFireDirect( rule.getActivationListener().equals( "direct" ) );

        initDeclaredMask(context);        
        initInferredMask();
    }

    public void initDeclaredMask(BuildContext context) {  
        doInitDeclaredMask(this, context);
    }
    public static void doInitDeclaredMask(TerminalNode tn, BuildContext context) {        
        if ( !(tn.unwrapTupleSource() instanceof LeftInputAdapterNode)) {
            // RTN's not after LIANode are not relevant for property specific, so don't block anything.
            tn.setDeclaredMask( Long.MAX_VALUE );
            return;            
        }
        
        Pattern pattern = context.getLastBuiltPatterns()[0];
        ObjectType objectType = pattern.getObjectType();
        
        if ( !(objectType instanceof ClassObjectType) ) {
            // InitialFact has no type declaration and cannot be property specific
            // Only ClassObjectType can use property specific
            tn.setDeclaredMask( Long.MAX_VALUE );
            return;
        }
        
        Class objectClass = ((ClassObjectType)objectType).getClassType();        
        TypeDeclaration typeDeclaration = context.getRuleBase().getTypeDeclaration(objectClass);
        if (  typeDeclaration == null || !typeDeclaration.isPropertySpecific() ) {
            // if property specific is not on, then accept all modification propagations
            tn.setDeclaredMask( Long.MAX_VALUE );            
        } else  {
            List<String> settableProperties = getSettableProperties(context.getRuleBase(), objectClass);
            tn.setDeclaredMask( calculatePositiveMask(pattern.getListenedProperties(), settableProperties) );
            tn.setNegativeMask( calculateNegativeMask(pattern.getListenedProperties(), settableProperties) );
        }
    }
    
    public void initInferredMask() {
        doInitInferredMask(this);
    }
    
    public static void doInitInferredMask(TerminalNode tn) {
        LeftTupleSource leftTupleSource = tn.unwrapTupleSource();
        if ( leftTupleSource instanceof LeftInputAdapterNode && ((LeftInputAdapterNode)leftTupleSource).getParentObjectSource() instanceof AlphaNode ) {
            AlphaNode alphaNode = (AlphaNode) ((LeftInputAdapterNode)leftTupleSource).getParentObjectSource();
            tn.setInferredMask( alphaNode.updateMask( tn.getDeclaredMask() ) );
        } else {
            tn.setInferredMask(  tn.getDeclaredMask() );
        }
        
        tn.setInferredMask(   tn.getInferredMask() & (Long.MAX_VALUE - tn.getNegativeMask() ) );
    }

    public LeftTupleSource unwrapTupleSource() {
        return tupleSource instanceof FromNode ? ((FromNode)tupleSource).getLeftTupleSource() : tupleSource;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        sequence = in.readInt();
        rule = (Rule) in.readObject();
        subrule = (GroupElement) in.readObject();
        subruleIndex = in.readInt();
        tupleSource = (LeftTupleSource) in.readObject();
        previousTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        nextTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        declarations = ( Declaration[]) in.readObject();
        fireDirect = rule.getActivationListener().equals( "direct" );
        
        declaredMask = in.readLong();
        inferredMask = in.readLong();        
        negativeMask = in.readLong();
        leftInputOtnId = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeInt( sequence );
        out.writeObject( rule );
        out.writeObject( subrule );
        out.writeInt( subruleIndex );
        out.writeObject( tupleSource );
        out.writeObject( previousTupleSinkNode );
        out.writeObject( nextTupleSinkNode );
        out.writeObject( declarations );
        out.writeLong(declaredMask);
        out.writeLong(inferredMask);        
        out.writeLong(negativeMask);
        out.writeLong(leftInputOtnId);
    }

    /**
     * Retrieve the <code>Action</code> associated with this node.
     *
     * @return The <code>Action</code> associated with this node.
     */
    public Rule getRule() {
        return this.rule;
    }

    public GroupElement getSubRule() {
        return this.subrule;
    }

    public void setSequence(int seq) {
        this.sequence = seq;
    }

    public int getSequence() {
        return this.sequence;
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

    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        //check if the rule is not effective or
        // if the current Rule is no-loop and the origin rule is the same then return
        if ( (!this.rule.isEffective( leftTuple,
                                      workingMemory )) ||
             (this.rule.isNoLoop() && this.rule.equals( context.getRuleOrigin() )) ) {
            return;
        }

        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

        boolean fire = agenda.createActivation( leftTuple, 
                                                context, 
                                                workingMemory, 
                                                this, 
                                                false );
        if( fire && !fireDirect ) {
            agenda.addActivation( (AgendaItem) leftTuple.getObject() );            
        }
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        // we need the inserted facthandle so we can update the network with new Activation
        AgendaItem match = (AgendaItem) leftTuple.getObject();

        InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();
        if ( match != null && match.isActivated() ) {
            // already activated, do nothing
            // although we need to notify the inserted Activation, as it's declarations may have changed.
            agenda.modifyActivation( match, true );
            return;
        }

        // if the current Rule is no-loop and the origin rule is the same then return
        if ( this.rule.isNoLoop() && this.rule.equals( context.getRuleOrigin() ) ) {
            agenda.increaseDormantActivations();
            return;
        }

        boolean fire = agenda.createActivation( leftTuple, context, workingMemory, this, true );
        if ( fire && !isFireDirect() ) {
            // This activation is currently dormant and about to reactivated, so decrease the dormant count.
            agenda.decreaseDormantActivations();

            agenda.modifyActivation( (AgendaItem) leftTuple.getObject(), false );
        }
    }

    public void modifyLeftTuple(InternalFactHandle factHandle,
                                ModifyPreviousTuples modifyPreviousTuples,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        LeftTupleSource.doModifyLeftTuple( factHandle, modifyPreviousTuples, context, workingMemory,
                                           this, getLeftInputOtnId(), inferredMask);
        
//        LeftTuple leftTuple = modifyPreviousTuples.removeLeftTuple( this );
//
//        if ( intersect(context.getModificationMask(), inferredMask)) {
//            if ( leftTuple != null ) {
//                leftTuple.reAdd(); //
//                // LeftTuple previously existed, so continue as modify
//                modifyLeftTuple( leftTuple,
//                                 context,
//                                 workingMemory );
//            } else {
//                // LeftTuple does not exist, so create and continue as assert
//                assertLeftTuple( createLeftTuple( factHandle,
//                                                    this,
//                                                    true ),
//                                 context,
//                                 workingMemory );
//            }
//        }
    }    
    
    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        final Activation activation = (Activation) leftTuple.getObject();

        // activation can be null if the LeftTuple previous propagated into a no-loop
        if ( activation == null ) {
            return;
        }

        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

        agenda.cancelActivation( leftTuple, 
                                 context, 
                                 workingMemory, 
                                 activation, 
                                 this );
    }


    public String toString() {
        return "[RuleTerminalNode(" + this.getId() + "): rule=" + this.rule.getName() + "]";
    }

    public void attach() {
        this.tupleSource.addTupleSink( this );
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            this.tupleSource.updateSink( this,
                                         propagationContext,
                                         workingMemory );
        }
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.tupleSource.networkUpdated(updateContext);
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final BaseNode node,
                            final InternalWorkingMemory[] workingMemories) {
        CleanupAdapter adapter = context.getCleanupAdapter();
        context.setCleanupAdapter( new RTNCleanupAdapter( this ) );
        this.tupleSource.remove( context,
                                 builder,
                                 this,
                                 workingMemories );
        for ( InternalWorkingMemory workingMemory : workingMemories ) {
            workingMemory.executeQueuedActions();
        }
        context.setCleanupAdapter(adapter);
    }

    public boolean isInUse() {
        return false;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return false;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {

    }

    public Declaration[] getDeclarations() {
        return this.declarations;
    }

    public static class SortDeclarations
            implements
            Comparator<Declaration> {
        public final static SortDeclarations instance = new SortDeclarations();

        public int compare(Declaration d1,
                           Declaration d2) {
            return (d1.getIdentifier().compareTo( d2.getIdentifier() ));
        }
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

    public int hashCode() {
        return this.rule.hashCode();
    }

    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( object == null || !(object instanceof RuleTerminalNode) ) {
            return false;
        }

        final RuleTerminalNode other = (RuleTerminalNode) object;
        return this.rule.equals(other.rule);
    }

    public short getType() {
        return NodeTypeEnums.RuleTerminalNode;
    }

    public static class RTNCleanupAdapter
            implements
            CleanupAdapter {
        private RuleTerminalNode node;

        public RTNCleanupAdapter(RuleTerminalNode node) {
            this.node = node;
        }

        public void cleanUp(final LeftTuple leftTuple,
                            final InternalWorkingMemory workingMemory) {
            if ( leftTuple.getLeftTupleSink() != node ) {
                return;
            }

            final Activation activation = (Activation) leftTuple.getObject();

            // this is to catch a race condition as activations are activated and unactivated on timers
            if ( activation instanceof ScheduledAgendaItem ) {
                ScheduledAgendaItem scheduled = (ScheduledAgendaItem) activation;
                workingMemory.getTimerService().removeJob( scheduled.getJobHandle() );
                scheduled.getJobHandle().setCancel( true );
            }

            if ( activation.isActivated() ) {
                activation.remove();
                ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCancelled( activation,
                                                                                                workingMemory,
                                                                                                ActivationCancelledCause.CLEAR );
            }

            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_REMOVAL,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            workingMemory.getTruthMaintenanceSystem().removeLogicalDependencies( activation,
                                                                                 propagationContext,
                                                                                 node.getRule() );
            leftTuple.unlinkFromLeftParent();
            leftTuple.unlinkFromRightParent();
        }
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple( factHandle, sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple( leftTuple, sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTupleSink sink) {
        return new RuleTerminalNodeLeftTuple( leftTuple, rightTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }      
    
    public int getLeftInputOtnId() {
        return leftInputOtnId;
    }

    public void setLeftInputOtnId(int leftInputOtnId) {
        this.leftInputOtnId = leftInputOtnId;
    }  

    public boolean isFireDirect() {
        return fireDirect;
    }

    public void setFireDirect(boolean fireDirect) {
        this.fireDirect = fireDirect;
    }

    protected ObjectTypeNode getObjectTypeNode() {
        return tupleSource.getObjectTypeNode();
    }
}
