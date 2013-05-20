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

import org.drools.core.base.SalienceInteger;
import org.drools.core.base.mvel.MVELEnabledExpression;
import org.drools.core.base.mvel.MVELSalienceExpression;
import org.drools.core.common.AgendaItem;
import org.drools.core.common.EventSupport;
import org.drools.core.common.InternalAgenda;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.PropagationContextImpl;
import org.drools.core.common.ScheduledAgendaItem;
import org.drools.core.common.TruthMaintenanceSystemHelper;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.RuleRemovalContext.CleanupAdapter;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.GroupElement;
import org.drools.core.rule.Rule;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.time.impl.ExpressionIntervalTimer;
import org.kie.api.event.rule.MatchCancelledCause;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

/**
 * Leaf Rete-OO node responsible for enacting <code>Action</code> s on a
 * matched <code>Rule</code>.
 *
 * @see org.kie.rule.Rule
 */
public class RuleTerminalNode extends AbstractTerminalNode {
    private static final long             serialVersionUID = 510l;

    /** The rule to invoke upon match. */
    private Rule                          rule;
    
    /**
     * the subrule reference is needed to resolve declarations
     * because declarations may have different offsets in each subrule
     */
    private GroupElement                  subrule;
    private int                           subruleIndex;
    private Declaration[]                 declarations;
    
    private Declaration[]                 timerDelayDeclarations;
    private Declaration[]                 timerPeriodDeclarations;
    private Declaration[]                 salienceDeclarations;
    private Declaration[]                 enabledDeclarations;

    private LeftTupleSinkNode             previousTupleSinkNode;
    private LeftTupleSinkNode             nextTupleSinkNode;

    private boolean                       fireDirect;

    private transient ObjectTypeNode.Id   leftInputOtnId;

    private String                        consequenceName;

    private boolean                       unlinkingEnabled;

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
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
               source );
        this.rule = rule;
        this.subrule = subrule;
        this.subruleIndex = subruleIndex;
        
        this.unlinkingEnabled = context.getRuleBase().getConfiguration().isPhreakEnabled();

        setFireDirect( rule.getActivationListener().equals( "direct" ) );
        if ( isFireDirect() ) {
            rule.setSalience( new SalienceInteger(Integer.MAX_VALUE) );
        }

        setDeclarations( this.subrule.getOuterDeclarations() );

        initDeclaredMask(context);        
        initInferredMask();
    }
    
    public void setDeclarations(Map<String, Declaration> decls) {
        if ( rule.getSalience() instanceof MVELSalienceExpression ) {
            MVELSalienceExpression expr = ( MVELSalienceExpression ) rule.getSalience();
            Declaration[] declrs = expr.getMVELCompilationUnit().getPreviousDeclarations();
            
            this.salienceDeclarations = new Declaration[declrs.length];
            int i = 0;
            for ( Declaration declr : declrs ) {
                this.salienceDeclarations[i++] = decls.get( declr.getIdentifier() );
            }
            Arrays.sort( this.salienceDeclarations, SortDeclarations.instance );            
        }
        
        if ( rule.getEnabled() instanceof MVELEnabledExpression ) {
            MVELEnabledExpression expr = ( MVELEnabledExpression ) rule.getEnabled();
            Declaration[] declrs = expr.getMVELCompilationUnit().getPreviousDeclarations();
            
            this.enabledDeclarations = new Declaration[declrs.length];
            int i = 0;
            for ( Declaration declr : declrs ) {
                this.enabledDeclarations[i++] = decls.get( declr.getIdentifier() );
            }
            Arrays.sort( this.enabledDeclarations, SortDeclarations.instance );              
        }        
        
        if ( rule.getTimer() instanceof ExpressionIntervalTimer ) {
            ExpressionIntervalTimer expr = ( ExpressionIntervalTimer ) rule.getTimer();
            
            Declaration[] declrs = expr.getDelayMVELCompilationUnit().getPreviousDeclarations();            
            this.timerDelayDeclarations = new Declaration[declrs.length];
            int i = 0;
            for ( Declaration declr : declrs ) {
                this.timerDelayDeclarations[i++] = decls.get( declr.getIdentifier() );
            }
            Arrays.sort( this.timerDelayDeclarations, SortDeclarations.instance );      
            
            declrs = expr.getPeriodMVELCompilationUnit().getPreviousDeclarations();            
            this.timerPeriodDeclarations = new Declaration[declrs.length];
            i = 0;
            for ( Declaration declr : declrs ) {
                this.timerPeriodDeclarations[i++] = decls.get( declr.getIdentifier() );
            }
            Arrays.sort( this.timerPeriodDeclarations, SortDeclarations.instance );             
        }
    }
    
    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal(in);
        rule = (Rule) in.readObject();
        subrule = (GroupElement) in.readObject();
        subruleIndex = in.readInt();
        previousTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        nextTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        declarations = ( Declaration[]) in.readObject();

        timerDelayDeclarations = ( Declaration[]) in.readObject();
        timerPeriodDeclarations = ( Declaration[]) in.readObject();
        salienceDeclarations = ( Declaration[]) in.readObject();
        enabledDeclarations = ( Declaration[]) in.readObject();
        consequenceName = (String) in.readObject();

        fireDirect = rule.getActivationListener().equals( "direct" );
        unlinkingEnabled = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( rule );
        out.writeObject( subrule );
        out.writeInt( subruleIndex );
        out.writeObject( previousTupleSinkNode );
        out.writeObject( nextTupleSinkNode );
        out.writeObject( declarations );
        
        out.writeObject( timerDelayDeclarations );
        out.writeObject( timerPeriodDeclarations );
        out.writeObject( salienceDeclarations );
        out.writeObject( enabledDeclarations );
        out.writeObject( consequenceName );
        out.writeBoolean(unlinkingEnabled);
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

    public void assertLeftTuple(final LeftTuple leftTuple,
                                PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        if( unlinkingEnabled ) {
            context = findMostRecentPropagationContext( leftTuple,
                                                        context );
        }
        
        //check if the rule is not effective or
        // if the current Rule is no-loop and the origin rule is the same then return
        if ( (!this.rule.isEffective( leftTuple,
                                      this,
                                      workingMemory )) ||
             (this.rule.isNoLoop() && this.rule.equals( context.getRuleOrigin() )) ) {
            leftTuple.setObject( Boolean.TRUE );
            return;
        }

        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();        

        boolean fire = agenda.createActivation( leftTuple, 
                                                context, 
                                                workingMemory, 
                                                this );
        if( fire && !fireDirect ) {
            agenda.addActivation( (AgendaItem) leftTuple.getObject() );
        }
    }

    public static PropagationContext findMostRecentPropagationContext(final LeftTuple leftTuple,
                                                                PropagationContext context) {
        // Find the most recent PropagationContext, as this caused this rule to elegible for firing
        LeftTuple lt = leftTuple;
        while ( lt != null ) {
            if ( lt.getPropagationContext() != null && lt.getPropagationContext().getPropagationNumber() > context.getPropagationNumber() ) {
                context = lt.getPropagationContext();
            }
            lt = lt.getParent();
        }
        return context;
    }

    public void modifyLeftTuple(LeftTuple leftTuple,
                                PropagationContext context,
                                InternalWorkingMemory workingMemory) {
        if( unlinkingEnabled ) {
            context = findMostRecentPropagationContext( leftTuple,
                                                        context );
        }
        
    	InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();
    	
        // we need the inserted facthandle so we can update the network with new Activation
    	Object o = leftTuple.getObject();
    	if ( o != Boolean.TRUE) {  // would be true due to lock-on-active blocking activation creation
    		AgendaItem match = (AgendaItem) o;       
	        if ( match != null && match.isQueued() ) {
	            // already activated, do nothing
	            // although we need to notify the inserted Activation, as it's declarations may have changed.
	            agenda.modifyActivation( match, true );
	            return;
	        }
    	}

        // if the current Rule is no-loop and the origin rule is the same then return
        if ( (!this.rule.isEffective( leftTuple,
                                      this,
                                      workingMemory )) ||
             (this.rule.isNoLoop() && this.rule.equals( context.getRuleOrigin() )) ) {
            return;
        }

        // o (AgendaItem) could be null, if this was staged as an insert but not processed, then pushed as a update
        if ( o == null || o  == Boolean.TRUE ) {
        	// set to Boolean.TRUE when lock-on-active stops an Activation being created
        	leftTuple.setObject( null );
        }
        boolean fire = agenda.createActivation( leftTuple, context, workingMemory, this );
        if ( fire && !isFireDirect() ) {
            agenda.modifyActivation( (AgendaItem) leftTuple.getObject(), false );
        }
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        Object obj = leftTuple.getObject();


        // activation can be null if the LeftTuple previous propagated into a no-loop
        // or could be true due to lock-on-active blocking activation creation
        if ( obj == null || obj == Boolean.TRUE) {
            return;
        }

        Activation activation = (Activation) obj;
        activation.setMatched( false );
        
        InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

        agenda.cancelActivation( leftTuple, 
                                 context, 
                                 workingMemory, 
                                 activation, 
                                 this );

        ((RuleTerminalNodeLeftTuple)leftTuple).setActivationUnMatchListener(null);
    }


    public String toString() {
        return "[RuleTerminalNode(" + this.getId() + "): rule=" + this.rule.getName() + "]";
    }

    public void attach( BuildContext context ) {
        getLeftTupleSource().addTupleSink(this, context);
        if (context == null || context.getRuleBase().getConfiguration().isPhreakEnabled() ) {
            return;
        }

        for ( InternalWorkingMemory workingMemory : context.getWorkingMemories() ) {
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            getLeftTupleSource().updateSink(this, propagationContext, workingMemory);
        }
    }

    public void networkUpdated(UpdateContext updateContext) {
        getLeftTupleSource().networkUpdated(updateContext);
    }

    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        getLeftTupleSource().removeTupleSink(this);
    }

    protected void doCollectAncestors(NodeSet nodeSet) {
        getLeftTupleSource().collectAncestors(nodeSet);
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
        if ( this.declarations == null ) {
            Map<String, Declaration> decls = this.subrule.getOuterDeclarations();
            String[] requiredDeclarations = rule.getRequiredDeclarationsForConsequence(getConsequenceName());
            this.declarations = new Declaration[requiredDeclarations.length];
            int i = 0;
            for ( String str : requiredDeclarations ) {
                declarations[i++] = decls.get( str );
            }
            Arrays.sort( this.declarations, SortDeclarations.instance );
        }
        return this.declarations;
    }
    
    public Declaration[] getTimerDelayDeclarations() {
        return timerDelayDeclarations;
    }

    public void setTimerDelayDeclarations(Declaration[] timerDelayDeclarations) {
        this.timerDelayDeclarations = timerDelayDeclarations;
    }

    public Declaration[] getTimerPeriodDeclarations() {
        return timerPeriodDeclarations;
    }

    public void setTimerPeriodDeclarations(Declaration[] timerPeriodDeclarations) {
        this.timerPeriodDeclarations = timerPeriodDeclarations;
    }

    public Declaration[] getSalienceDeclarations() {
        return salienceDeclarations;
    }

    public void setSalienceDeclarations(Declaration[] salienceDeclarations) {
        this.salienceDeclarations = salienceDeclarations;
    }

    public Declaration[] getEnabledDeclarations() {
        return enabledDeclarations;
    }

    public void setEnabledDeclarations(Declaration[] enabledDeclarations) {
        this.enabledDeclarations = enabledDeclarations;
    }

    public String getConsequenceName() {
        return consequenceName == null ? Rule.DEFAULT_CONSEQUENCE_NAME : consequenceName;
    }

    public void setConsequenceName(String consequenceName) {
        this.consequenceName = consequenceName;
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

        if ( !(object instanceof RuleTerminalNode) ) {
            return false;
        }

        final RuleTerminalNode other = (RuleTerminalNode) object;
        return rule.equals(other.rule) && (consequenceName == null ? other.consequenceName == null : consequenceName.equals(other.consequenceName));
    }

    public short getType() {
        return NodeTypeEnums.RuleTerminalNode;
    }

    public static class RTNCleanupAdapter
            implements
            CleanupAdapter {
        private final RuleTerminalNode node;

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

            if ( activation.isQueued() ) {
                activation.remove();
                ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCancelled( activation,
                                                                                                workingMemory,
                                                                                                MatchCancelledCause.CLEAR );
            }

            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_REMOVAL,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            TruthMaintenanceSystemHelper.removeLogicalDependencies( activation,
                                                                    propagationContext,
                                                                    node.getRule() );
            leftTuple.unlinkFromLeftParent();
            leftTuple.unlinkFromRightParent();
        }
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final LeftTupleSink sink) {
        return new RuleTerminalNodeLeftTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     LeftTupleSink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple( factHandle, sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     LeftTupleSink sink,
                                     PropagationContext pctx,
                                     boolean leftTupleMemoryEnabled) {
        return new RuleTerminalNodeLeftTuple( leftTuple, sink, pctx, leftTupleMemoryEnabled );
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
    
    public ObjectTypeNode.Id getLeftInputOtnId() {
        return leftInputOtnId;
    }

    public void setLeftInputOtnId(ObjectTypeNode.Id leftInputOtnId) {
        this.leftInputOtnId = leftInputOtnId;
    }  

    public boolean isFireDirect() {
        return fireDirect;
    }

    public void setFireDirect(boolean fireDirect) {
        this.fireDirect = fireDirect;
    }

    protected ObjectTypeNode getObjectTypeNode() {
        return getLeftTupleSource().getObjectTypeNode();
    }         

}
