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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.drools.common.AgendaItem;
import org.drools.common.BaseNode;
import org.drools.common.DefaultAgenda;
import org.drools.common.EventFactHandle;
import org.drools.common.EventSupport;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalAgendaGroup;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleFlowGroup;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.ScheduledAgendaItem;
import org.drools.event.rule.ActivationCancelledCause;
import org.drools.reteoo.RuleRemovalContext.CleanupAdapter;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Declaration;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;
import org.drools.time.impl.Timer;

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

    private int               sequence         = -1;  // -1 means not set

    private static final long serialVersionUID = 510l;

    /** The rule to invoke upon match. */
    private Rule              rule;
    /**
     * the subrule reference is needed to resolve declarations
     * because declarations may have different offsets in each subrule
     */
    private GroupElement      subrule;
    private int               subruleIndex;
    private LeftTupleSource   tupleSource;
    private Declaration[]     declarations;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;
    
    private boolean           fireDirect;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public RuleTerminalNode() {

    }

    /**
     * Construct.
     *
     * @param inputSource
     *            The parent tuple source.
     * @param rule
     *            The rule.
     * @param subruleIndex 
     */
    public RuleTerminalNode(final int id,
                            final LeftTupleSource source,
                            final Rule rule,
                            final GroupElement subrule,
                            final int subruleIndex, 
                            final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.rule = rule;
        this.tupleSource = source;
        this.subrule = subrule;
        this.subruleIndex = subruleIndex;
        
        Map<String, Declaration> decls = this.subrule.getOuterDeclarations();
        this.declarations = new Declaration[ rule.getRequiredDeclarations().length ];
        int i = 0;
        for (String str : rule.getRequiredDeclarations() ) {
            this.declarations[i++] = decls.get( str );
        }
        Arrays.sort( this.declarations, SortDeclarations.instance  );
        fireDirect = rule.getActivationListener().equals( "direct" );
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
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

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.drools.impl.TupleSink
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public void assertLeftTuple(final LeftTuple leftTuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        //check if the rule is not effective or
        // if the current Rule is no-loop and the origin rule is the same then return
        if ( (!this.rule.isEffective( leftTuple,
                                      workingMemory )) ||
             (this.rule.isNoLoop() && this.rule.equals( context.getRuleOrigin() )) ||
             ( context.getReaderContext() == null && leftTuple.isExpired()) ) {
            return;
        }

        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();
       
        boolean fire = ((DefaultAgenda)agenda).createActivation( leftTuple, 
                                                                 context, 
                                                                 workingMemory, 
                                                                 this, 
                                                                 false );
        
        // Can be null if no Activation was created, only add it to the agenda if it's not a control rule.
        if ( fire && !fireDirect) {
            agenda.addActivation( (AgendaItem) leftTuple.getObject() );            
        }
    }
    
    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {        
    	InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();
    	
        // we need the inserted facthandle so we can update the network with new Activation
    	Object o = leftTuple.getObject();
    	if ( o != Boolean.TRUE) {  // would be true due to lock-on-active blocking activation creation
    		AgendaItem match = (AgendaItem) o;       
	        if ( match != null && match.isActivated() ) {
	            // already activated, do nothing
	            // although we need to notify the inserted Activation, as it's declarations may have changed.
	            agenda.modifyActivation( match, true );
	            return;
	        }
    	}

        // if the current Rule is no-loop and the origin rule is the same then return
        if ( this.rule.isNoLoop() && this.rule.equals( context.getRuleOrigin() ) ) {
            agenda.increaseDormantActivations();
            return;
        }

        boolean reuseActivation = true;
        if ( o  == Boolean.TRUE ) {
        	// set to Boolean.TRUE when lock-on-active stops an Activation being created
        	// We set this instead of doing a null check, as it's a little safer due to intent.
        	reuseActivation = false;
        	leftTuple.setObject( null );
        }
        boolean fire = ((DefaultAgenda)agenda).createActivation( leftTuple, context, workingMemory, this, reuseActivation );
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
        LeftTuple leftTuple = modifyPreviousTuples.removeLeftTuple( this );
        if ( leftTuple != null ) {
            leftTuple.reAdd(); //
            // LeftTuple previously existed, so continue as modify
            modifyLeftTuple( leftTuple,
                             context,
                             workingMemory );
        } else {
            // LeftTuple does not exist, so create and continue as assert
            assertLeftTuple( createLeftTuple( factHandle,
                                                this,
                                                true ),
                             context,
                             workingMemory );
        }
    }    
    

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        final Activation activation = (Activation) leftTuple.getObject();

        // activation can be null if the LeftTuple previous propagated into a no-loop
        if ( activation == null ) {
            return;
        }
        
        final DefaultAgenda agenda = (DefaultAgenda) workingMemory.getAgenda();
        
        AgendaItem item = ( AgendaItem ) activation;
        item.removeAllBlockersAndBlocked(agenda);
        
        if ( agenda.isDeclarativeAgenda() && activation.getFactHandle() == null ) {
            // This a control rule activation, nothing to do except update counters. As control rules are not in agenda-groups etc.
            agenda.decreaseDormantActivations(); // because we know ControlRules fire straight away and then become dormant
            return; 
        } else {
            // we are retracting an actual Activation, so also remove it and it's handle from the WM. 
            agenda.removeActivation( (AgendaItem) activation ); 
        }

        if (  activation.isActivated() ) {
            // on fact expiration, we don't remove the activation, but let it fire
            if ( context.getType() == PropagationContext.EXPIRATION && context.getFactHandleOrigin() != null ) {
            } else {
                activation.remove();

                if ( activation.getActivationGroupNode() != null ) {
                    activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
                }

                if ( activation.getActivationNode() != null ) {
                    final InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) activation.getActivationNode().getParentContainer();
                    ruleFlowGroup.removeActivation( activation );
                }
                leftTuple.decreaseActivationCountForEvents();

                ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCancelled( activation,
                                                                                                workingMemory,
                                                                                                ActivationCancelledCause.WME_MODIFY );
                agenda.decreaseActiveActivations();
            }
        } else {
            agenda.decreaseDormantActivations();
        }
        
        workingMemory.getTruthMaintenanceSystem().removeLogicalDependencies( activation,
                                                                             context,
                                                                             this.rule );        
    }
    

    public String toString() {
        return "[RuleTerminalNode(" + this.getId() + "): rule=" + this.rule.getName() + "]";
    }

    public void ruleAttached() {
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

    public void networkUpdated() {
        this.tupleSource.networkUpdated();
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
        context.setCleanupAdapter( adapter );
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
    
    public static class SortDeclarations implements Comparator<Declaration> {
        public final static SortDeclarations instance = new SortDeclarations();
        public int compare(Declaration d1,
                           Declaration d2) {
            return ( d1.getIdentifier().compareTo( d2.getIdentifier() ) );
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
        return this.rule.equals( other.rule );
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
                ScheduledAgendaItem scheduled = ( ScheduledAgendaItem ) activation;
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
        return new RuleTerminalNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }    
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple(leftTuple,sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTupleSink sink) {
        return new RuleTerminalNodeLeftTuple(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }
    
    public boolean isFireDirect() {
        return this.fireDirect;
    }    
}
