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

package org.drools.common;

import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListEntry;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.PriorityQueue;

/**
 * <code>AgendaGroup</code> implementation that uses a <code>PriorityQueue</code> to prioritise the evaluation of added
 * <code>ActivationQueue</code>s. The <code>AgendaGroup</code> also maintains a <code>Map</code> of <code>ActivationQueues</code>
 * for requested salience values.
 *
 * @see PriorityQueue
 * @see ActivationQueue
 */
public class ArrayAgendaGroup
    implements
    InternalAgendaGroup {

    private static final long serialVersionUID = 510l;

    private String      name;

    /** Items in the agenda. */
    private LinkedList[] array;

    private boolean           active;

    private int               size;

    private int               index;

    private int               lastIndex;
    
    private PropagationContext autoFocusActivator;

    public ArrayAgendaGroup() {

    }
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

        this.index = this.array.length-1;
        this.lastIndex = 0;
    }
    

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        name    = (String)in.readObject();
        array   = (LinkedList[])in.readObject();
        active  = in.readBoolean();
        size    = in.readInt();
        index   = in.readInt();
        lastIndex   = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(name);
        out.writeObject(array);
        out.writeBoolean(active);
        out.writeInt(size);
        out.writeInt(index);
        out.writeInt(lastIndex);
    }

    /* (non-Javadoc)
     * @see org.drools.spi.AgendaGroup#getName()
     */
    public String getName() {
        return this.name;
    }

    public void clear() {
        this.lastIndex = 0;
        this.array = new LinkedList[ this.array.length ];
    }

    /* (non-Javadoc)
     * @see org.drools.spi.AgendaGroup#size()
     */
    public int size() {
        return this.size;
    }
    
    public Activation[] getAndClear() {
        Activation[] queue = getActivations();
        clear();
        return queue;
    }    

    public void add(final Activation activation) {
        AgendaItem item = (AgendaItem) activation;
        this.size++;
        int seq = item.getSequenence();

        if ( seq < this.index ) {
            this.index = seq;
        }

        if ( seq > this.lastIndex ) {
            this.lastIndex = seq;
        }

        LinkedList<LinkedListEntry<Activation>> list = this.array[seq];
        if ( list == null ) {
            list = new LinkedList<LinkedListEntry<Activation>>();
            this.array[item.getSequenence()] = list;
        }

        list.add( new LinkedListEntry<Activation>( activation ) );
    }

    public Activation getNext() {
        Activation activation = null;
        while ( this.index <= lastIndex ) {
            LinkedList<LinkedListEntry<Activation>> list = this.array[this.index];
            if ( list != null ) {
                activation = list.removeFirst().getObject();
                if ( list.isEmpty()) {
                    this.array[this.index++] = null;
                }
                this.size--;
                break;
            }
            this.index++;
        }
        return activation;
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
        for ( LinkedList<LinkedListEntry<Activation>> list : array ) {
            if ( list != null ) {
                
                FastIterator it = list.fastIterator();
                for ( LinkedListEntry<Activation> entry =  list.getFirst(); entry != null; entry = (LinkedListEntry<Activation>) it.next( entry ) ) {
                    if ( entry.getObject() != null ) {
                        activations[j++] = entry.getObject();
                    }
                }
            }

        }
        return activations;
    }    

    public String toString() {
        return "AgendaGroup '" + this.name + "'";
    }

    public boolean equals(final Object object) {
        if ( !(object instanceof ArrayAgendaGroup) ) {
            return false;
        }

        return ((ArrayAgendaGroup) object).name.equals(this.name);
    }

    public int hashCode() {
        return this.name.hashCode();
    }
    
    public void setFocus() {
        throw new UnsupportedOperationException();
    }
    
    public void remove(AgendaItem agendaItem) {
        throw new UnsupportedOperationException();
    }
    
    public void setAutoFocusActivator(PropagationContext ctx) {
        this.autoFocusActivator = ctx;
    }

    public PropagationContext getAutoFocusActivator() {
        return autoFocusActivator;
    }
}
