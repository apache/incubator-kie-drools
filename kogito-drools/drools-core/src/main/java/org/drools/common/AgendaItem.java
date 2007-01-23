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

import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.PropagationContext;
import org.drools.spi.RuleFlowGroup;
import org.drools.spi.Tuple;
import org.drools.util.LinkedList;
import org.drools.util.Queue;
import org.drools.util.Queueable;

/**
 * Item entry in the <code>Agenda</code>.
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
public class AgendaItem
    implements
    Activation,
    Queueable,
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /**
     * 
     */
    private static final long        serialVersionUID = 320L;

    /** The tuple. */
    private final Tuple              tuple;

    /** The rule. */
    private final Rule               rule;

    /** The subrule */
    private final GroupElement       subrule;

    /** The propagation context */
    private final PropagationContext context;

    /** The activation number */
    private final long               activationNumber;

    /** A reference to the PriorityQeue the item is on */
    private Queue                    queue;

    private int                      index;

    private LinkedList               justified;

    private boolean                  activated;

    private AgendaGroupImpl          agendaGroup;

    private ActivationGroupNode      activationGroupNode;

    private RuleFlowGroupNode        ruleFlowGroupNode;

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
    public AgendaItem(final long activationNumber,
                      final Tuple tuple,
                      final PropagationContext context,
                      final Rule rule,
                      final GroupElement subrule) {
        this.tuple = tuple;
        this.context = context;
        this.rule = rule;
        this.subrule = subrule;
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

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.spi.Activation#getActivationNumber()
     */
    public long getActivationNumber() {
        return this.activationNumber;
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

    public String toString() {
        return "[Activation rule=" + this.rule.getName() + ", tuple=" + this.tuple + "]";
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

        if ( object == null || object.getClass() != AgendaItem.class ) {
            return false;
        }

        final AgendaItem otherItem = (AgendaItem) object;

        return (this.rule.equals( otherItem.getRule() ) && this.tuple.equals( otherItem.getTuple() ));
    }

    /**
     * Return the hashCode of the
     * <code>TupleKey<code> as the hashCode of the AgendaItem
     * @return
     */
    public int hashCode() {
        return this.tuple.hashCode();
    }

    public void enqueued(final Queue queue,
                         final int index) {
        this.queue = queue;
        this.index = index;
    }

    public void dequeue() {
        this.queue.dequeue( this.index );
        this.activated = false;
    }

    public void remove() {
        dequeue();
    }

    public ActivationGroupNode getActivationGroupNode() {
        return this.activationGroupNode;
    }

    public void setActivationGroupNode(final ActivationGroupNode activationNode) {
        this.activationGroupNode = activationNode;
    }

    public AgendaGroup getAgendaGroup() {
        return agendaGroup;
    }

    public void setAgendaGroup(AgendaGroupImpl agendaGroup) {
        this.agendaGroup = agendaGroup;
    }

    public RuleFlowGroupNode getRuleFlowGroupNode() {
        return ruleFlowGroupNode;
    }

    public void setRuleFlowGroupNode(RuleFlowGroupNode ruleFlowGroupNode) {
        this.ruleFlowGroupNode = ruleFlowGroupNode;
    }

    public GroupElement getSubRule() {
        return this.subrule;
    }
}
