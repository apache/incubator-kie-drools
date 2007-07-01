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

import org.drools.conflict.DepthConflictResolver;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.ConflictResolver;
import org.drools.util.BinaryHeapQueue;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListEntry;
import org.drools.util.PrimitiveLongMap;
import org.drools.util.Queueable;
import org.drools.util.LinkedList.LinkedListIterator;

/**
 * <code>AgendaGroup</code> implementation that uses a <code>PriorityQueue</code> to prioritise the evaluation of added
 * <code>ActivationQueue</code>s. The <code>AgendaGroup</code> also maintains a <code>Map</code> of <code>ActivationQueues</code> 
 * for requested salience values.
 * 
 * @see PriorityQueue
 * @see ActivationQueue
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public class ArrayAgendaGroup
    implements
    InternalAgendaGroup {

    private static final long serialVersionUID = 320L;

    private final String      name;

    /** Items in the agenda. */
    private LinkedList[]      array;

    private boolean           active;

    private int               size;

    private int               index;

    /**
     * Construct an <code>AgendaGroup</code> with the given name.
     * 
     * @param name
     *      The <AgendaGroup> name.
     */

    public ArrayAgendaGroup(final String name,
                            final InternalRuleBase ruleBase) {
        this.name = name;
        Integer integer = (Integer) ruleBase.getAgendaGroupRuleTotals().get( name );
        if ( integer == null ) {
            this.array = new LinkedList[0];
        } else {
            this.array = new LinkedList[integer.intValue()];    
        }
        
    }

    /* (non-Javadoc)
     * @see org.drools.spi.AgendaGroup#getName()
     */
    public String getName() {
        return this.name;
    }

    public void clear() {
        this.array = new LinkedList[this.array.length];
    }

    /* (non-Javadoc)
     * @see org.drools.spi.AgendaGroup#size()
     */
    public int size() {
        return this.size;
    }

    public void add(final Activation activation) {
        AgendaItem item = (AgendaItem) activation;
        this.size++;

        LinkedList list = this.array[item.getSequenence()];
        if ( list == null ) {
            list = new LinkedList();
            this.array[item.getSequenence()] = list;
        }
        
        list.add( new LinkedListEntry( activation ) );
    }

    public Activation getNext() {
        Activation activation = null;
        int length = this.array.length;
        while ( this.index < length ) {
            LinkedList list = this.array[this.index];            
            if ( list != null ) {
                activation = (Activation) ((LinkedListEntry)list.removeFirst()).getObject();
                if ( list.isEmpty()) {
                    this.array[this.index++] = null;
                }
                this.size--;
                break;
            }
            this.index++;
        }
        return (Activation) activation;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean activate) {
        this.active = activate;
    }

    /**
     * Iterates a PriorityQueue removing empty entries until it finds a populated entry and return true,
     * otherwise it returns false;
     * 
     * @param priorityQueue
     * @return
     */
    public boolean isEmpty() {
        return this.size == 0;
    }

    public Activation[] getActivations() {
        Activation[] activations = new Activation[this.size];
        int j = 0;
        for ( int i = 0; i < this.array.length; i++ ) {;
            LinkedList list = this.array[i];
            if ( list != null ) {
                LinkedListIterator it = list.iterator();
                Activation activation = ( Activation ) ((LinkedListEntry)it.next()).getObject();
                while ( activation != null) {
                    activations[j++] = activation;
                    activation = ( Activation ) it.next();
                }
            }
            
        }
        return activations;
    }

    public Activation[] getQueue() {
        return getActivations();
    }

    public String toString() {
        return "AgendaGroup '" + this.name + "'";
    }

    public boolean equal(final Object object) {
        if ( (object == null) || !(object instanceof ArrayAgendaGroup) ) {
            return false;
        }

        if ( ((ArrayAgendaGroup) object).name.equals( this.name ) ) {
            return true;
        }

        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}
