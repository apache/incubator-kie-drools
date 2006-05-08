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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.common.Agenda;
import org.drools.common.AgendaGroupImpl;
import org.drools.common.AgendaItem;
import org.drools.common.PropagationContextImpl;
import org.drools.common.ScheduledAgendaItem;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.Duration;
import org.drools.spi.PropagationContext;
import org.drools.spi.XorGroup;
import org.drools.util.LinkedListObjectWrapper;
import org.drools.util.Queueable;

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
    private XorGroup          xorGroup;

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
    TerminalNode(final int id,
                 final TupleSource source,
                 final Rule rule) {
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

    public void assertTuple(final ReteTuple tuple,
                            final PropagationContext context,
                            final WorkingMemoryImpl workingMemory) {
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
                            final WorkingMemoryImpl workingMemory,
                            final boolean fireActivationCreated) {
        // if the current Rule is no-loop and the origin rule is the same then
        // return
        if ( this.rule.getNoLoop() && this.rule.equals( context.getRuleOrigin() ) ) {
            return;
        }
        final Agenda agenda = workingMemory.getAgenda();

        final Duration dur = this.rule.getDuration();                

        if ( dur != null && dur.getDuration( tuple ) > 0 ) {
            final ScheduledAgendaItem item = new ScheduledAgendaItem( context.getPropagationNumber(),
                                                                tuple,
                                                                workingMemory.getAgenda(),
                                                                context,
                                                                this.rule );

            if ( this.rule.getXorGroup() != null ) {
                // Lazy cache xorGroup
                if ( this.xorGroup == null ) {
                    this.xorGroup = workingMemory.getAgenda().getXorGroup( this.rule.getXorGroup() );
                }
                this.xorGroup.addActivation( item );
            }            
            
            agenda.scheduleItem( item );
            tuple.setActivation( item );
            item.setActivated( true );
            workingMemory.getAgendaEventSupport().fireActivationCreated( item );
        } else {
            // -----------------
            // Lazy instantiation and addition to the Agenda of AgendGroup
            // implementations
            // ----------------
            final TerminalNodeMemory memory = (TerminalNodeMemory) workingMemory.getNodeMemory( this );
            AgendaGroupImpl agendaGroup = memory.getAgendaGroup();
            if ( agendaGroup == null ) {
                if ( this.rule.getAgendaGroup() == null || this.rule.getAgendaGroup().equals( "" ) || this.rule.getAgendaGroup().equals( AgendaGroup.MAIN ) ) {
                    // Is the Rule AgendaGroup undefined? If it is use MAIN,
                    // which is added to the Agenda by default
                    agendaGroup = (AgendaGroupImpl) agenda.getAgendaGroup( AgendaGroup.MAIN );
                } else {
                    // AgendaGroup is defined, so try and get the AgendaGroup
                    // from the Agenda
                    agendaGroup = (AgendaGroupImpl) agenda.getAgendaGroup( this.rule.getAgendaGroup() );
                }

                if ( agendaGroup == null ) {
                    // The AgendaGroup is defined but not yet added to the
                    // Agenda, so create the AgendaGroup and add to the Agenda.
                    agendaGroup = new AgendaGroupImpl( this.rule.getAgendaGroup() );
                    workingMemory.getAgenda().addAgendaGroup( agendaGroup );
                }

                memory.setAgendaGroup( agendaGroup );
            }

            // set the focus if rule autoFocus is true
            if ( this.rule.getAutoFocus() ) {
                agenda.setFocus( agendaGroup );
            }

            final AgendaItem item = new AgendaItem( context.getPropagationNumber(),
                                              tuple,
                                              context,
                                              this.rule );
            
            if ( this.rule.getXorGroup() != null ) {
                // Lazy cache xorGroup
                if ( this.xorGroup == null ) {
                    this.xorGroup = workingMemory.getAgenda().getXorGroup( this.rule.getXorGroup() );
                }
                this.xorGroup.addActivation( item );
            }               

            // Makes sure the Lifo is added to the AgendaGroup priority queue
            // If the AgendaGroup is already in the priority queue it just
            // returns.
            agendaGroup.add( item );
            tuple.setActivation( item );
            item.setActivated( true );

            // We only want to fire an event on a truly new Activation and not on an Activation as a result of a modify
            if ( fireActivationCreated ) {
                workingMemory.getAgendaEventSupport().fireActivationCreated( item );
            }
        }
    }

    public void retractTuple(final ReteTuple tuple,
                             final PropagationContext context,
                             final WorkingMemoryImpl workingMemory) {
        final Activation activation = tuple.getActivation();
        if ( activation.isActivated() ) {
            activation.remove();
            workingMemory.getAgendaEventSupport().fireActivationCancelled( activation );
        }

        workingMemory.removeLogicalDependencies( activation,
                                                 context,
                                                 this.rule );
    }

    public void modifyTuple(final ReteTuple tuple,
                            final PropagationContext context,
                            final WorkingMemoryImpl workingMemory) {
        // We have to remove and assert the new tuple as it has modified facts and thus its tuple is newer
        if ( tuple.getActivation().isActivated() ) {
            tuple.getActivation().remove();
        }
        assertTuple( tuple,
                     context,
                     workingMemory,
                     false );

    }

    public String toString() {
        return "[TerminalNode: rule=" + this.rule.getName() + "]";
    }

    public void ruleAttached() {
        // TODO Auto-generated method stub

    }

    public void attach() {
        this.tupleSource.addTupleSink( this );
    }

    public void attach(final WorkingMemoryImpl[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final WorkingMemoryImpl workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                PropagationContext.RULE_ADDITION,
                                                                                null,
                                                                                null );
            this.tupleSource.updateNewNode( workingMemory,
                                            propagationContext );
        }
    }

    public void remove(final BaseNode node,
                       final WorkingMemoryImpl[] workingMemories) {
        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final WorkingMemoryImpl workingMemory = workingMemories[i];

            final TerminalNodeMemory memory = (TerminalNodeMemory) workingMemory.getNodeMemory( this );

            final AgendaGroupImpl group = memory.getAgendaGroup();
            final Queueable[] elements = group.getQueueable();
            final List list = new ArrayList();
            //start at 1 as BinaryHeapQueue starts at 1
            for ( int j = 1, size = group.size() + 1; j < size; j++ ) {
                final AgendaItem item = (AgendaItem) elements[j];
                if ( item.getRule() == this.rule ) {
                    list.add( item );
                }
            }
            for ( final Iterator it = list.iterator(); it.hasNext(); ) {
                final AgendaItem item = (AgendaItem) it.next();
                if ( item.isActivated() ) {
                    item.remove();
                    workingMemory.getAgendaEventSupport().fireActivationCancelled( item );
                }

                final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                    PropagationContext.RULE_REMOVAL,
                                                                                    null,
                                                                                    null );
                workingMemory.removeLogicalDependencies( item,
                                                         propagationContext,
                                                         this.rule );
            }
        }

        this.tupleSource.remove( this,
                            workingMemories );
    }

    public void updateNewNode(final WorkingMemoryImpl workingMemory,
                              final PropagationContext context) {
        // There are no child nodes to update, do nothing.
    }

    public Object createMemory() {
        return new TerminalNodeMemory();
    }
    
    public int hashCode() {
        return this.rule.hashCode();
    }
    
    public boolean equals(Object object) {
        if ( object == this ){
            return true;
        }
        
        if ( object == null || object.getClass() != TerminalNode.class ) {
            return false;
        }
        
        TerminalNode other = ( TerminalNode ) object;
        return this.rule.equals( other.rule );
    }       

    class TerminalNodeMemory {
        private AgendaGroupImpl agendaGroup;

        public AgendaGroupImpl getAgendaGroup() {
            return this.agendaGroup;
        }

        public void setAgendaGroup(final AgendaGroupImpl agendaGroup) {
            this.agendaGroup = agendaGroup;
        }
    }
}
