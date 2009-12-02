package org.drools.reteoo;

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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import org.drools.RuleBaseConfiguration;
import org.drools.common.AgendaItem;
import org.drools.common.BaseNode;
import org.drools.common.EventFactHandle;
import org.drools.common.EventSupport;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalRuleFlowGroup;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.ScheduledAgendaItem;
import org.drools.event.rule.ActivationCancelledCause;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.Duration;
import org.drools.spi.PropagationContext;
import org.drools.time.impl.Timer;
import org.drools.util.Iterator;
import org.drools.util.LeftTupleList;

/**
 * Leaf Rete-OO node responsible for enacting <code>Action</code> s on a
 * matched <code>Rule</code>.
 *
 * @see org.drools.rule.Rule
 *
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
public final class RuleTerminalNode extends BaseNode
    implements
    LeftTupleSinkNode,
    NodeMemory,
    TerminalNode,
    Externalizable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private int               sequence         = -1;  // -1 means not set

    /**
     *
     */
    private static final long serialVersionUID = 400L;

    /** The rule to invoke upon match. */
    private Rule              rule;
    /**
     * the subrule reference is needed to resolve declarations
     * because declarations may have different offsets in each subrule
     */
    private GroupElement      subrule;
    private LeftTupleSource   tupleSource;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    protected boolean         tupleMemoryEnabled;

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
     */
    public RuleTerminalNode(final int id,
                            final LeftTupleSource source,
                            final Rule rule,
                            final GroupElement subrule,
                            final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation() );
        this.rule = rule;
        this.tupleSource = source;
        this.subrule = subrule;
        this.tupleMemoryEnabled = context.isTerminalNodeMemoryEnabled();
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
        tupleSource = (LeftTupleSource) in.readObject();
        previousTupleSinkNode = (LeftTupleSinkNode) in.readObject();
        nextTupleSinkNode = (LeftTupleSinkNode) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeInt( sequence );
        out.writeObject( rule );
        out.writeObject( subrule );
        out.writeObject( tupleSource );
        out.writeObject( previousTupleSinkNode );
        out.writeObject( nextTupleSinkNode );
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

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.drools.impl.TupleSink
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    public void assertLeftTuple(final LeftTuple tuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory) {
        assertLeftTuple( tuple,
                         context,
                         workingMemory,
                         true );

    }

    /**
     * Assert a new <code>Tuple</code>.
     *
     * @param tuple
     *            The <code>Tuple</code> being asserted.
     * @param workingMemory
     *            The working memory session.
     * @throws AssertionException
     *             If an error occurs while asserting.
     */
    public void assertLeftTuple(final LeftTuple tuple,
                                final PropagationContext context,
                                final InternalWorkingMemory workingMemory,
                                final boolean fireActivationCreated) {
        //check if the rule is effective
        if ( !this.rule.isEffective( workingMemory.getTimeMachine(),
                                     tuple,
                                     workingMemory ) ) {
            return;
        }

        // if the current Rule is no-loop and the origin rule is the same and its the same set of facts (tuple) then return
        if ( context.getType() == PropagationContext.MODIFICATION ) {
            if ( this.rule.isNoLoop() && this.rule.equals( context.getRuleOrigin() ) && context.getLeftTupleOrigin().equals( tuple ) ) {
                return;
            }
        } else if ( this.rule.isNoLoop() && this.rule.equals( context.getRuleOrigin() ) ) {
            return;
        }

        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();
        
        final Timer timer = this.rule.getTimer();

        if ( timer != null ) {
            final ScheduledAgendaItem item = agenda.createScheduledAgendaItem( tuple,
                                                                               context,
                                                                               this.rule,
                                                                               this.subrule );
            final TerminalNodeMemory memory = (TerminalNodeMemory) workingMemory.getNodeMemory( this );

            agenda.scheduleItem( item );
            tuple.setActivation( item );

            if ( this.tupleMemoryEnabled ) {
                memory.getTupleMemory().add( tuple );
            }

            item.setActivated( true );
            ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCreated( item,
                                                                                          workingMemory );
        } else {
            // -----------------
            // Lazy instantiation and addition to the Agenda of AgendGroup
            // implementations
            // ----------------
            final TerminalNodeMemory memory = (TerminalNodeMemory) workingMemory.getNodeMemory( this );

            final AgendaItem item = agenda.createAgendaItem( tuple,
                                                             rule.getSalience().getValue( tuple,
                                                                                          workingMemory ),
                                                             context,
                                                             this.rule,
                                                             this.subrule );

            item.setSequenence( this.sequence );

            tuple.setActivation( item );
            memory.getTupleMemory().add( tuple );

            boolean added = agenda.addActivation( item );

            item.setActivated( added );

            // We only want to fire an event on a truly new Activation and not on an Activation as a result of a modify
            if ( added && fireActivationCreated ) {
                ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCreated( item,
                                                                                              workingMemory );
            }
        }

        agenda.increaseActiveActivations();
    }

    public void retractLeftTuple(final LeftTuple leftTuple,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
        final TerminalNodeMemory memory = (TerminalNodeMemory) workingMemory.getNodeMemory( this );
        memory.getTupleMemory().remove( leftTuple );

        final Activation activation = leftTuple.getActivation();

        // activation can be null if the LeftTuple previous propagated into a no-loop
        if ( activation == null ) {
            return;
        }

        if ( activation.getLogicalDependencies() != null && !activation.getLogicalDependencies().isEmpty() ) {
            context.addRetractedTuple( this.rule,
                                       activation );
        }

        if ( activation.isActivated() ) {
            if ( context.getType() == PropagationContext.MODIFICATION ) {
                // during a modify if we have either isLockOnActive or the activation has logical dependencies
                // then we need to track retractions, so we know which are exising activations and which are truly new
                if ( this.rule.isLockOnActive() ) {
                    context.addRetractedTuple( this.rule,
                                               activation );
                }
            }

            // on fact expiration, we don't remove the activation, but let it fire
            if ( context.getType() == PropagationContext.EXPIRATION && context.getFactHandleOrigin() != null ) {
                EventFactHandle efh = (EventFactHandle) context.getFactHandleOrigin();
                efh.increaseActivationsCount();
            } else {
                activation.remove();

                if ( activation.getActivationGroupNode() != null ) {
                    activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
                }

                if ( activation.getActivationNode() != null ) {
                    final InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) activation.getActivationNode().getParentContainer();
                    ruleFlowGroup.removeActivation( activation );
                }

                ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCancelled( activation,
                                                                                                workingMemory,
                                                                                                ActivationCancelledCause.WME_MODIFY );
                ((InternalAgenda) workingMemory.getAgenda()).decreaseActiveActivations();
            }
        } else {
            ((InternalAgenda) workingMemory.getAgenda()).decreaseDormantActivations();
        }

        workingMemory.removeLogicalDependencies( activation,
                                                 context,
                                                 this.rule );
    }

    public String toString() {
        return "[RuleTerminalNode: rule=" + this.rule.getName() + "]";
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
        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];

            final TerminalNodeMemory memory = (TerminalNodeMemory) workingMemory.getNodeMemory( this );
            final Iterator it = memory.getTupleMemory().iterator();
            for ( LeftTuple leftTuple = (LeftTuple) it.next(); leftTuple != null; leftTuple = (LeftTuple) it.next() ) {
                final Activation activation = leftTuple.getActivation();

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
                                                                                     this.rule );
                leftTuple.unlinkFromLeftParent();
                leftTuple.unlinkFromRightParent();
            }

            workingMemory.executeQueuedActions();
            workingMemory.clearNodeMemory( this );
        }

        if ( !context.alreadyVisited( this.tupleSource ) ) {
            this.tupleSource.remove( context,
                                     builder,
                                     this,
                                     workingMemories );
        }
    }

    public boolean isInUse() {
        return false;
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        return new TerminalNodeMemory();
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

    public static class TerminalNodeMemory
        implements
        Externalizable {
        private static final long serialVersionUID = 400L;

        //        private InternalAgendaGroup agendaGroup;
        //
        //        private ActivationGroup     activationGroup;
        //
        //        private RuleFlowGroup       ruleFlowGroup;

        private LeftTupleList     tupleMemory;

        public TerminalNodeMemory() {
            this.tupleMemory = new LeftTupleList();
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            //            agendaGroup = (InternalAgendaGroup) in.readObject();
            //            activationGroup = (ActivationGroup) in.readObject();
            //            ruleFlowGroup = (RuleFlowGroup) in.readObject();
            tupleMemory = (LeftTupleList) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            //            out.writeObject( agendaGroup );
            //            out.writeObject( activationGroup );
            //            out.writeObject( ruleFlowGroup );
            out.writeObject( tupleMemory );
        }

        //        public InternalAgendaGroup getAgendaGroup() {
        //            return this.agendaGroup;
        //        }
        //
        //        public void setAgendaGroup(final InternalAgendaGroup agendaGroup) {
        //            this.agendaGroup = agendaGroup;
        //        }
        //
        //        public ActivationGroup getActivationGroup() {
        //            return this.activationGroup;
        //        }
        //
        //        public void setActivationGroup(final ActivationGroup activationGroup) {
        //            this.activationGroup = activationGroup;
        //        }

        public LeftTupleList getTupleMemory() {
            return this.tupleMemory;
        }

        //        public RuleFlowGroup getRuleFlowGroup() {
        //            return this.ruleFlowGroup;
        //        }
        //
        //        public void setRuleFlowGroup(final RuleFlowGroup ruleFlowGroup) {
        //            this.ruleFlowGroup = ruleFlowGroup;
        //        }
    }
}
