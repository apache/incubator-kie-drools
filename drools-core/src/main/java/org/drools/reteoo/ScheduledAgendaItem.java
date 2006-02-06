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
import java.util.TimerTask;

import org.drools.FactHandle;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.ConsequenceException;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

/**
 * Item entry in the <code>Agenda</code>.
 * 
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter </a>
 */
class ScheduledAgendaItem  extends  TimerTask
    implements
    Activation,
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    private ScheduledAgendaItem               previous;

    private ScheduledAgendaItem               next;

    /** The tuple. */
    private final ReteTuple          tuple;

    /** The rule. */
    private final  Rule              rule;
    
    private final WorkingMemoryImpl  workingMemory;

    private final PropagationContext context;

    private final long               activationNumber;        

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
    ScheduledAgendaItem(long activationNumber,
                        ReteTuple tuple,
                        WorkingMemoryImpl workingMemory,
                        PropagationContext context,
                        Rule rule) {
        this.tuple = tuple;
        this.context = context;
        this.rule = rule;
        this.workingMemory = workingMemory;
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
     * Retrieve the <code>TupleKey</code>.
     * 
     * @return The key to the tuple in this item.
     */
    TupleKey getKey() {
        return this.tuple.getKey();
    }

    /**
     * Handle the firing of an alarm.
     */
    public void run() {
        this.workingMemory.getAgenda().fireActivation( this );
    }

    public long getActivationNumber() {
        return this.activationNumber;
    }

    public String toString() {
        return "[Activation rule=" + this.rule.getName() + ", tuple=" + this.tuple + "]";
    }

    public boolean equals(Object object) {
        if ( object == this ) {
            return true;
        }

        if ( (object == null) || !(object instanceof ScheduledAgendaItem) ) {
            return false;
        }

        ScheduledAgendaItem otherItem = (ScheduledAgendaItem) object;

        return (this.rule.equals( otherItem.getRule() ) && this.tuple.getKey().equals( otherItem.getKey() ));
    }

    public int hashcode() {
        return this.getKey().hashCode();
    }

    public ScheduledAgendaItem getNext() {
        return this.next;
    }

    public void setNext(ScheduledAgendaItem next) {
        this.next = next;
    }

    public ScheduledAgendaItem getPrevious() {
        return this.previous;
    }

    public void setPrevious(ScheduledAgendaItem previous) {
        this.previous = previous;
    }

    public void remove() {
        this.workingMemory.getAgenda().removeScheduleItem( this );        
    }    
}
