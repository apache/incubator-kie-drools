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

import java.util.HashSet;
import java.util.Set;
import java.util.TimerTask;

import org.drools.FactException;
import org.drools.common.ActivationQueue;
import org.drools.common.Agenda;
import org.drools.common.AgendaGroupImpl;
import org.drools.common.AgendaItem;
import org.drools.common.ScheduledAgendaItem;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.Duration;
import org.drools.spi.AgendaGroup;
import org.drools.spi.PropagationContext;

/**
 * Leaf Rete-OO node responsible for enacting <code>Action</code> s on a
 * matched <code>Rule</code>.
 * 
 * @see org.drools.rule.Rule
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
final class TerminalNode extends BaseNode
    implements
    TupleSink,
    NodeMemory {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The rule to invoke upon match. */
    private final Rule        rule;
    private final TupleSource tupleSource;

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
    TerminalNode(int id,
                 TupleSource source,
                 Rule rule) {
        super( id );
        this.rule = rule;
        this.tupleSource = source;
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

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    // org.drools.impl.TupleSink
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

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
    public void assertTuple(ReteTuple tuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) {
        // if the current Rule is no-loop and the origin rule is the same then
        // return
        if ( rule.getNoLoop() && rule.equals( context.getRuleOrigin() ) ) {
            return;
        }
        Agenda agenda = workingMemory.getAgenda();

        Duration dur = rule.getDuration();

        if ( dur != null && dur.getDuration( tuple ) > 0 ) {
            ScheduledAgendaItem item = new ScheduledAgendaItem( context.getPropagationNumber(),
                                                                tuple,
                                                                workingMemory.getAgenda(),
                                                                context,
                                                                rule );
            agenda.scheduleItem( item );
            tuple.setActivation( item );
            item.setActivated( true );
            workingMemory.getAgendaEventSupport().fireActivationCreated( item );
        } else {
            // -----------------
            // Lazy instantiation and addition to the Agenda of AgendGroup
            // implementations
            // ----------------
            TerminalNodeMemory memory = (TerminalNodeMemory) workingMemory.getNodeMemory( this );
            AgendaGroupImpl agendaGroup = memory.getAgendaGroup();
            if ( agendaGroup == null ) {
                if ( rule.getAgendaGroup() == null || rule.getAgendaGroup().equals( "" ) || rule.getAgendaGroup().equals( AgendaGroup.MAIN ) ) {
                    // Is the Rule AgendaGroup undefined? If it is use MAIN,
                    // which is added to the Agenda by default
                    agendaGroup = (AgendaGroupImpl) agenda.getAgendaGroup( AgendaGroup.MAIN );
                } else {
                    // AgendaGroup is defined, so try and get the AgendaGroup
                    // from the Agenda
                    agendaGroup = (AgendaGroupImpl) agenda.getAgendaGroup( rule.getAgendaGroup() );
                }

                if ( agendaGroup == null ) {
                    // The AgendaGroup is defined but not yet added to the
                    // Agenda, so create the AgendaGroup and add to the Agenda.
                    agendaGroup = new AgendaGroupImpl( rule.getAgendaGroup() );
                    workingMemory.getAgenda().addAgendaGroup( agendaGroup );
                }

                memory.setAgendaGroup( agendaGroup );
            }

            // set the focus if rule autoFocus is true
            if ( rule.getAutoFocus() ) {
                agenda.setFocus( agendaGroup );
            }

            // Lazy assignment of the AgendaGroup's Activation Lifo Queue
            if ( memory.getLifo() == null ) {
                memory.setLifo( agendaGroup.getActivationQueue( this.rule.getSalience() ) );
            }

            ActivationQueue queue = memory.getLifo();

            AgendaItem item = new AgendaItem( context.getPropagationNumber(),
                                              tuple,
                                              context,
                                              rule,
                                              queue );

            queue.add( item );

            // Makes sure the Lifo is added to the AgendaGroup priority queue
            // If the AgendaGroup is already in the priority queue it just
            // returns.
            agendaGroup.addToAgenda( memory.getLifo() );
            tuple.setActivation( item );
            item.setActivated( true );
            workingMemory.getAgendaEventSupport().fireActivationCreated( item );
        }
    }

    public void retractTuple(ReteTuple tuple,
                             PropagationContext context,
                             WorkingMemoryImpl workingMemory) {
        Activation activation = tuple.getActivation();
        if ( activation.isActivated() ) {
            activation.remove();
            workingMemory.getAgendaEventSupport().fireActivationCancelled( activation );
        }

        workingMemory.removeLogicalDependencies( activation,
                                                 context,
                                                 this.rule );
    }

    public void modifyTuple(ReteTuple tuple,
                            PropagationContext context,
                            WorkingMemoryImpl workingMemory) {
        if ( tuple.getActivation().isActivated() ) {
            tuple.getActivation().remove();
        }
        assertTuple( tuple,
                     context,
                     workingMemory );

    }

    public String toString() {
        return "[TerminalNode: rule=" + this.rule.getName() + "]";
    }

    public void ruleAttached() {
        // TODO Auto-generated method stub

    }

    public void attach() {
        tupleSource.addTupleSink( this );
    }

    public void remove(BaseNode node,
                       WorkingMemoryImpl workingMemory,
                       PropagationContext context) {
        workingMemory.clearNodeMemory( this );
        tupleSource.remove( this,
                            workingMemory,
                            context );
    }

    public void updateNewNode(WorkingMemoryImpl workingMemory,
                              PropagationContext context) {
        // There are no child nodes to update, do nothing.
    }

    public Object createMemory() {
        return new TerminalNodeMemory();
    }

    class TerminalNodeMemory {
        private AgendaGroupImpl agendaGroup;
        private ActivationQueue lifo;

        public ActivationQueue getLifo() {
            return this.lifo;
        }

        public void setLifo(ActivationQueue lifo) {
            this.lifo = lifo;
        }

        public AgendaGroupImpl getAgendaGroup() {
            return this.agendaGroup;
        }

        public void setAgendaGroup(AgendaGroupImpl agendaGroup) {
            this.agendaGroup = agendaGroup;
        }
    }
}
