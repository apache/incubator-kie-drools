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

import java.io.Serializable;

import org.drools.common.EventSupport;
import org.drools.RuleBaseConfiguration;
import org.drools.common.BinaryHeapQueueAgendaGroup;
import org.drools.common.AgendaItem;
import org.drools.common.BaseNode;
import org.drools.common.InternalAgenda;
import org.drools.common.InternalAgendaGroup;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalRuleFlowGroup;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.LogicalDependency;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.ScheduledAgendaItem;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.ActivationGroup;
import org.drools.spi.AgendaGroup;
import org.drools.spi.Duration;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleFlowGroup;
import org.drools.util.Iterator;
import org.drools.util.LinkedList;
import org.drools.util.TupleHashTable;

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
    TupleSinkNode,
    NodeMemory,
    TerminalNode {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private int sequence;

    /**
     *
     */
    private static final long  serialVersionUID = 400L;
    /** The rule to invoke upon match. */
    private final Rule         rule;
    /**
     * the subrule reference is needed to resolve declarations
     * because declarations may have different offsets in each subrule
     */
    private final GroupElement subrule;
    private final TupleSource  tupleSource;

    private TupleSinkNode      previousTupleSinkNode;
    private TupleSinkNode      nextTupleSinkNode;

    protected boolean          tupleMemoryEnabled;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     *
     * @param inputSource
     *            The parent tuple source.
     * @param rule
     *            The rule.
     */
    public RuleTerminalNode(final int id,
                            final TupleSource source,
                            final Rule rule,
                            final GroupElement subrule,
                            final BuildContext buildContext) {
        super( id );
        this.rule = rule;
        this.tupleSource = source;
        this.subrule = subrule;
        this.tupleMemoryEnabled = buildContext.isTerminalNodeMemoryEnabled();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the <code>Action</code> associated with this node.
     *
     * @return The <code>Action</code> associated with this node.
     */
    public Rule getRule() {
        return this.rule;
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

    public void assertTuple(final ReteTuple tuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory) {
        assertTuple( tuple,
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
     *            The working memory seesion.
     * @throws AssertionException
     *             If an error occurs while asserting.
     */
    public void assertTuple(final ReteTuple tuple,
                            final PropagationContext context,
                            final InternalWorkingMemory workingMemory,
                            final boolean fireActivationCreated) {

        //check if the rule is effective
        if ( !this.rule.isEffective(workingMemory.getTimeMachine()) ) {
            return;
        }

        // if the current Rule is no-loop and the origin rule is the same and its the same set of facts (tuple) then return
        if ( context.getType() == PropagationContext.MODIFICATION ) {
            if ( this.rule.isNoLoop() && this.rule.equals( context.getRuleOrigin() ) && context.getActivationOrigin().getTuple().equals( tuple ) ) {
                return;
            }
        } else if ( this.rule.isNoLoop() && this.rule.equals( context.getRuleOrigin() ) ) {
            return;
        }

        //we only have to clone the head fact to make sure the graph is not affected during consequence reads after a modify
        final ReteTuple cloned = new ReteTuple( tuple );

        final InternalAgenda agenda = (InternalAgenda) workingMemory.getAgenda();

        final Duration dur = this.rule.getDuration();

        if ( dur != null && dur.getDuration( tuple ) > 0 ) {
            final ScheduledAgendaItem item = new ScheduledAgendaItem( context.getPropagationNumber(),
                                                                      cloned,
                                                                      agenda,
                                                                      context,
                                                                      this.rule,
                                                                      this.subrule );
            final TerminalNodeMemory memory = (TerminalNodeMemory) workingMemory.getNodeMemory( this );
            if ( this.rule.getActivationGroup() != null ) {
                // Lazy cache activationGroup
                if ( memory.getActivationGroup() == null ) {
                    memory.setActivationGroup( workingMemory.getAgenda().getActivationGroup( this.rule.getActivationGroup() ) );
                }
                memory.getActivationGroup().addActivation( item );
            }

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
            InternalAgendaGroup agendaGroup = memory.getAgendaGroup();
            if ( agendaGroup == null ) {
                // @todo: this logic really should be encapsulated inside the Agenda
                if ( this.rule.getAgendaGroup() == null || this.rule.getAgendaGroup().equals( "" ) || this.rule.getAgendaGroup().equals( AgendaGroup.MAIN ) ) {
                    // Is the Rule AgendaGroup undefined? If it is use MAIN,
                    // which is added to the Agenda by default
                    agendaGroup = (InternalAgendaGroup) agenda.getAgendaGroup( AgendaGroup.MAIN );
                } else {
                    // AgendaGroup is defined, so try and get the AgendaGroup
                    // from the Agenda
                    agendaGroup = (InternalAgendaGroup) agenda.getAgendaGroup( this.rule.getAgendaGroup() );
                }

                memory.setAgendaGroup( agendaGroup );
            }

            // set the focus if rule autoFocus is true
            if ( this.rule.getAutoFocus() ) {
                agenda.setFocus( agendaGroup );
            }

            final AgendaItem item = new AgendaItem( context.getPropagationNumber(),
                                                    cloned,
                                                    rule.getSalience().getValue( tuple, workingMemory ),
                                                    context,
                                                    this.rule,
                                                    this.subrule );

            if ( this.tupleMemoryEnabled ) {
                item.setSequenence( this.sequence );
            }

            if ( this.rule.getActivationGroup() != null ) {
                // Lazy cache activationGroup
                if ( memory.getActivationGroup() == null ) {
                    memory.setActivationGroup( workingMemory.getAgenda().getActivationGroup( this.rule.getActivationGroup() ) );
                }
                memory.getActivationGroup().addActivation( item );
            }

            item.setAgendaGroup( agendaGroup );
            if ( this.rule.getRuleFlowGroup() == null ) {
                // No RuleFlowNode so add  it directly to  the Agenda

                // do not add the activation if the rule is "lock-on-active" and the AgendaGroup is active
                // we must check the context to determine if its a new tuple or an exist re-activated tuple as part of the retract
                if ( context.getType() == PropagationContext.MODIFICATION ) {
                    if ( this.rule.isLockOnActive() && agendaGroup.isActive() ) {
                        Activation justifier = context.removeRetractedTuple( this.rule,
                                                                             tuple );
                        if ( justifier == null ) {
                            // This rule is locked and active, do not allow new tuples to activate
                            return;
                        } else if ( this.rule.hasLogicalDependency() ) {
                            copyLogicalDependencies( context,
                                                     workingMemory,
                                                     item,
                                                     justifier );
                        }
                    } else if ( this.rule.hasLogicalDependency() ) {
                        Activation justifier = context.removeRetractedTuple( this.rule,
                                                                             tuple );
                        copyLogicalDependencies( context,
                                                 workingMemory,
                                                 item,
                                                 justifier );
                    }
                } else if ( this.rule.isLockOnActive() && agendaGroup.isActive() ) {
                    return;
                }

                agendaGroup.add( item );
            } else {
                //There is  a RuleFlowNode so add it there, instead  of the Agenda
                RuleFlowGroup rfg = memory.getRuleFlowGroup();
                // Lazy cache ruleFlowGroup
                if ( rfg == null ) {
                    rfg = workingMemory.getAgenda().getRuleFlowGroup( this.rule.getRuleFlowGroup() );
                    memory.setRuleFlowGroup( rfg );
                }

                // do not add the activation if the rule is "lock-on-active" and the RuleFlowGroup is active
                // we must check the context to determine if its a new tuple or an exist re-activated tuple as part of the retract
                if ( context.getType() == PropagationContext.MODIFICATION ) {
                    if ( this.rule.isLockOnActive() && rfg.isActive() ) {
                        Activation justifier = context.removeRetractedTuple( this.rule,
                                                                             tuple );
                        if ( justifier == null ) {
                            // This rule is locked and active, do not allow new tuples to activate
                            return;
                        } else if ( this.rule.hasLogicalDependency() ) {
                            copyLogicalDependencies( context,
                                                     workingMemory,
                                                     item,
                                                     justifier );
                        }
                    } else if ( this.rule.hasLogicalDependency() ) {
                        Activation justifier = context.removeRetractedTuple( this.rule,
                                                                             tuple );
                        copyLogicalDependencies( context,
                                                 workingMemory,
                                                 item,
                                                 justifier );
                    }
                } else if ( this.rule.isLockOnActive() && rfg.isActive() ) {
                    return;
                }

                ((InternalRuleFlowGroup) memory.getRuleFlowGroup()).addActivation( item );

            }

            tuple.setActivation( item );
            memory.getTupleMemory().add( tuple );

            item.setActivated( true );

            // We only want to fire an event on a truly new Activation and not on an Activation as a result of a modify
            if ( fireActivationCreated ) {
            	((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCreated( item,
                                                                             workingMemory );
            }
        }

        agenda.increaseActiveActivations();
    }

    private void copyLogicalDependencies(final PropagationContext context,
                                         final InternalWorkingMemory workingMemory,
                                         final AgendaItem item,
                                         Activation justifier) {
        if ( justifier != null ) {
            final LinkedList list = justifier.getLogicalDependencies();
            if ( list != null && !list.isEmpty() ) {
                for ( LogicalDependency node = (LogicalDependency) list.getFirst(); node != null; node = (LogicalDependency) node.getNext() ) {
                    final InternalFactHandle handle = (InternalFactHandle) node.getFactHandle();
                    workingMemory.getTruthMaintenanceSystem().addLogicalDependency( handle,
                                                                                    item,
                                                                                    context,
                                                                                    this.rule );
                }
            }
        }
    }

    public void retractTuple(final ReteTuple leftTuple,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final TerminalNodeMemory memory = (TerminalNodeMemory) workingMemory.getNodeMemory( this );
        final ReteTuple tuple = memory.getTupleMemory().remove( leftTuple );
        if ( tuple == null ) {
            // tuple should only be null if it was asserted and reached a no-loop causing it to exit early
            // before being added to the node memory and an activation created and attached
            return;
        }

        final Activation activation = tuple.getActivation();
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

            activation.remove();

            if ( activation.getActivationGroupNode() != null ) {
                activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
            }

            if ( activation.getRuleFlowGroupNode() != null ) {
                final InternalRuleFlowGroup ruleFlowGroup = activation.getRuleFlowGroupNode().getRuleFlowGroup();
                ruleFlowGroup.removeActivation( activation );
            }

            ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCancelled( activation,
                                                                           workingMemory );
            ((InternalAgenda) workingMemory.getAgenda()).decreaseActiveActivations();
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
        // TODO Auto-generated method stub

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
                                                                                      null );
            this.tupleSource.updateSink( this,
                                         propagationContext,
                                         workingMemory );
        }
    }

    public void remove(ReteooBuilder builder,
                       final BaseNode node, final InternalWorkingMemory[] workingMemories) {
        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];

            final TerminalNodeMemory memory = (TerminalNodeMemory) workingMemory.getNodeMemory( this );
            final Iterator it = memory.getTupleMemory().iterator();
            for ( ReteTuple tuple = (ReteTuple) it.next(); tuple != null; tuple = (ReteTuple) it.next() ) {
                final Activation activation = tuple.getActivation();

                if ( activation.isActivated() ) {
                    activation.remove();
                    ((EventSupport) workingMemory).getAgendaEventSupport().fireActivationCancelled( activation,
                                                                                   workingMemory );
                }

                final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                          PropagationContext.RULE_REMOVAL,
                                                                                          null,
                                                                                          null );
                workingMemory.getTruthMaintenanceSystem().removeLogicalDependencies( activation,
                                                                                     propagationContext,
                                                                                     this.rule );
            }

            workingMemory.executeQueuedActions();
            workingMemory.clearNodeMemory( this );
        }

        removeShare(builder);

        this.tupleSource.remove( builder,
                                 this, workingMemories );
    }

    public Object createMemory(final RuleBaseConfiguration config) {
        return new TerminalNodeMemory();
    }

    public boolean isTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public TupleSinkNode getNextTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextTupleSinkNode(final TupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public TupleSinkNode getPreviousTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousTupleSinkNode(final TupleSinkNode previous) {
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

    class TerminalNodeMemory
        implements
        Serializable {
        private static final long serialVersionUID = 400L;

        private InternalAgendaGroup   agendaGroup;

        private ActivationGroup   activationGroup;

        private RuleFlowGroup     ruleFlowGroup;

        private TupleHashTable    tupleMemory;

        public TerminalNodeMemory() {
            this.tupleMemory = new TupleHashTable();
        }

        public InternalAgendaGroup getAgendaGroup() {
            return this.agendaGroup;
        }

        public void setAgendaGroup(final InternalAgendaGroup agendaGroup) {
            this.agendaGroup = agendaGroup;
        }

        public ActivationGroup getActivationGroup() {
            return this.activationGroup;
        }

        public void setActivationGroup(final ActivationGroup activationGroup) {
            this.activationGroup = activationGroup;
        }

        public TupleHashTable getTupleMemory() {
            return this.tupleMemory;
        }

        public RuleFlowGroup getRuleFlowGroup() {
            return this.ruleFlowGroup;
        }

        public void setRuleFlowGroup(final RuleFlowGroup ruleFlowGroup) {
            this.ruleFlowGroup = ruleFlowGroup;
        }
    }
}
