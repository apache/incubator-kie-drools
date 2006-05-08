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

import org.drools.FactHandle;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;
import org.drools.spi.XorGroup;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNode;
import org.drools.util.LinkedListObjectWrapper;

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

    private LinkedListNode           previous;

    private LinkedListNode           next;

    /** The tuple. */
    private final Tuple              tuple;

    /** The rule. */
    private final Rule               rule;

    private final Agenda             agenda;

    private final PropagationContext context;

    private final long               activationNumber;

    private LinkedList               justified;

    private boolean                  activated;
    
    private XorGroupNode             xorGroupNode;    

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
    public ScheduledAgendaItem(long activationNumber,
                               Tuple tuple,
                               Agenda agenda,
                               PropagationContext context,
                               Rule rule) {
        this.tuple = tuple;
        this.context = context;
        this.rule = rule;
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
     * Determine if this tuple depends on the values derrived from a particular
     * root object.
     * 
     * @param handle
     *            The root object handle.
     * 
     * @return <code>true<code> if this agenda item depends
     *          upon the item, otherwise <code>false</code>.
     */
    boolean dependsOn(FactHandle handle) {
        return this.tuple.dependsOn( handle );
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

    public void setNext(LinkedListNode next) {
        this.next = next;
    }

    public LinkedListNode getPrevious() {
        return this.previous;
    }

    public void setPrevious(LinkedListNode previous) {
        this.previous = previous;
    }

    public void remove() {
        agenda.removeScheduleItem( this );
    }

    public String toString() {
        return "[Activation rule=" + this.rule.getName() + ", tuple=" + this.tuple + "]";
    }

    public void addLogicalDependency(LogicalDependency node) {
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

    public void setActivated(boolean activated) {
        this.activated = activated;
    }
    
    public XorGroupNode getXorGroupNode() {
        return this.xorGroupNode;
    }

    public void setXorGroupNode(XorGroupNode xorGroupNode) {
        this.xorGroupNode = xorGroupNode;
    }    

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object object) {
        if ( object == this ) {
            return true;
        }

        if ( (object == null) || !(object instanceof AgendaItem) ) {
            return false;
        }

        AgendaItem otherItem = (AgendaItem) object;

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
}
