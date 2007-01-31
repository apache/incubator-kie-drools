package org.drools.common;

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
import java.util.TimerTask;

import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNode;

/**
 * Item entry in the <code>Agenda</code>.
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
public class ScheduledAgendaItem extends TimerTask
    implements
    Activation,
    Serializable,
    LinkedListNode {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long        serialVersionUID = 320L;

    private LinkedListNode           previous;

    private LinkedListNode           next;

    /** The tuple. */
    private final Tuple              tuple;

    /** The rule. */
    private final Rule               rule;

    /** The subrule */
    private final GroupElement       subrule;

    private final InternalAgenda     agenda;

    private final PropagationContext context;

    private final long               activationNumber;

    private LinkedList               justified;

    private boolean                  activated;

    private ActivationGroupNode      activationGroupNode;       

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct.
     * 
     * @param tuple
     *            The tuple.
     * @param rule
     *            The rule.
     */
    public ScheduledAgendaItem(final long activationNumber,
                               final Tuple tuple,
                               final InternalAgenda agenda,
                               final PropagationContext context,
                               final Rule rule,
                               final GroupElement subrule ) {
        this.tuple = tuple;
        this.context = context;
        this.rule = rule;
        this.subrule = subrule;
        this.agenda = agenda;
        this.activationNumber = activationNumber;
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public PropagationContext getPropagationContext() {
        return this.context;
    }

    /**
     * Retrieve the rule.
     * 
     * @return The rule.
     */
    public Rule getRule() {
        return this.rule;
    }

    /**
     * Retrieve the tuple.
     * 
     * @return The tuple.
     */
    public Tuple getTuple() {
        return this.tuple;
    }

    /**
     * Handle the firing of an alarm.
     */
    public void run() {
        this.agenda.fireActivation( this );
        this.agenda.getWorkingMemory().fireAllRules();
    }

    public long getActivationNumber() {
        return this.activationNumber;
    }

    public LinkedListNode getNext() {
        return this.next;
    }

    public void setNext(final LinkedListNode next) {
        this.next = next;
    }

    public LinkedListNode getPrevious() {
        return this.previous;
    }

    public void setPrevious(final LinkedListNode previous) {
        this.previous = previous;
    }

    public void remove() {
        this.agenda.removeScheduleItem( this );
    }

    public String toString() {
        return "[Activation rule=" + this.rule.getName() + ", tuple=" + this.tuple + "]";
    }

    public void addLogicalDependency(final LogicalDependency node) {
        if ( this.justified == null ) {
            this.justified = new LinkedList();
        }

        this.justified.add( node );
    }

    public LinkedList getLogicalDependencies() {
        return this.justified;
    }

    public boolean isActivated() {
        return this.activated;
    }

    public void setActivated(final boolean activated) {
        this.activated = activated;
    }

    public ActivationGroupNode getActivationGroupNode() {
        return this.activationGroupNode;
    }

    public void setActivationGroupNode(final ActivationGroupNode activationGroupNode) {
        this.activationGroupNode = activationGroupNode;
    }
    
    public RuleFlowGroupNode getRuleFlowGroupNode() {
        throw null;
    }

    public void setRuleFlowGroupNode(RuleFlowGroupNode ruleFlowGroupNode) {
        throw new UnsupportedOperationException( "Scheduled activations cannot be in a Rule Flow Group" );
    }    

    public AgendaGroup getAgendaGroup() {
        return null;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if ( object == this ) {
            return true;
        }

        if ( (object == null) || !(object instanceof AgendaItem) ) {
            return false;
        }

        final AgendaItem otherItem = (AgendaItem) object;

        return (this.rule.equals( otherItem.getRule() ) && this.tuple.equals( otherItem.getTuple() ));
    }

    /**
     * Return the hashode of the
     * <code>TupleKey<code> as the hashCode of the AgendaItem
     * @return
     */
    public int hashCode() {
        return this.tuple.hashCode();
    }

    public GroupElement getSubRule() {
        return this.subrule;
    }
}
