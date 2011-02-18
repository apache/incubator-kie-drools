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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.drools.core.util.AbstractBaseLinkedListNode;
import org.drools.core.util.BinaryHeapQueue;
import org.drools.core.util.LinkedList;
import org.drools.core.util.Queueable;
import org.drools.spi.Activation;

/**
 * <code>AgendaGroup</code> implementation that uses a
 * <code>ActivationQueue</code>s. The <code>AgendaGroup</code> also maintains a
 * <code>Map</code> of <code>ActivationQueues</code> for requested salience
 * values.
 * 
 * @see PriorityQueue
 * @see ActivationQueue
 */
public class SimpleAgendaGroup
    implements
    InternalAgendaGroup {

    private static final long serialVersionUID = 510l;

    private String            name;

    /** Items in the agenda. */
    private LinkedList        salienceGroups;

    private int               size;

    private boolean           active;

    public static class SalienceGroup extends AbstractBaseLinkedListNode {
        private int        salience;
        private LinkedList list;

        public SalienceGroup(int salience) {
            this.salience = salience;
            this.list = new LinkedList();
        }

        public int getSalience() {
            return salience;
        }

        public LinkedList getList() {
            return this.list;
        }
    }

    /**
     * Construct an <code>AgendaGroup</code> with the given name.
     * 
     * @param name
     *            The <AgendaGroup> name.
     */
    public SimpleAgendaGroup() {

    }

    public SimpleAgendaGroup(final String name,
                             final InternalRuleBase ruleBase) {
        this.name = name;
        this.salienceGroups = new LinkedList();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        name = (String) in.readObject();
        //queue = (BinaryHeapQueue) in.readObject();
        active = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( name );
        //out.writeObject(queue);
        out.writeBoolean( active );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.spi.AgendaGroup#getName()
     */
    public String getName() {
        return this.name;
    }

    public void clear() {
        this.salienceGroups.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.drools.spi.AgendaGroup#size()
     */
    public int size() {
        return this.size;
    }

    public void add(final Activation activation) {
        int salience = activation.getSalience();
        SalienceGroup group = null;

        if ( !this.salienceGroups.isEmpty() ) {
            SalienceGroup lastGroup = (SalienceGroup) this.salienceGroups.getLast();
            
            // optimisation to find out it's the lowest, and thus add get or add it.
            if ( salience  <= lastGroup.getSalience() ) {
                if ( salience == lastGroup.getSalience() ) {
                    // get
                    group = (SalienceGroup) lastGroup;
                } else {
                    // create and add
                    SalienceGroup newGroup = new SalienceGroup( salience );
                    this.salienceGroups.insertAfter( lastGroup, newGroup );
                    group = newGroup;
                }
                //add or get to end
            }
            
            
            if ( group == null ) {
                // we know this won't iterate to the end returning null, as we checked the end already.
                for ( group = (SalienceGroup) this.salienceGroups.getFirst(); group != null && salience < group.getSalience(); group = (SalienceGroup) group.getNext() ) {
                }
                
                if ( salience  == group.getSalience() ) {
                    // get
                    group = (SalienceGroup) group;
                } else {
                    // create and add before, as must be larger
                    SalienceGroup newGroup = new SalienceGroup( salience );
                    this.salienceGroups.insertAfter( group.getPrevious(), newGroup );
                    group = newGroup;
                }
            }
            
        } else {
            //no groups so add
            SalienceGroup newGroup = new SalienceGroup( salience );
            this.salienceGroups.add( newGroup );
            group = newGroup;
        }

        group.getList().add( new ActivationNode( activation,
                                                 this ) );

    }

    public void remove(AgendaItem agendaItem) {
        int salience = agendaItem.getSalience();

        SalienceGroup group = null;
        
        SalienceGroup lastGroup = (SalienceGroup) this.salienceGroups.getLast();
        
        // optimisation to find out it's the lowest, and thus add get or add it.
        if ( salience  == lastGroup.getSalience() ) {
            group = lastGroup;
        } else {
            // don't check for !
            for ( group = (SalienceGroup) this.salienceGroups.getFirst(); group != null &&  group.getSalience() != salience; group = (SalienceGroup) group.getNext() ) {
            }
        }

        if ( group == null ) {
            throw new RuntimeException( "SalienceGroup does not exist, This should not be possible." );
        }

        group.getList().remove( agendaItem.getActivationNode() );
    }

    public Activation getNext() {
        SalienceGroup group = (SalienceGroup) this.salienceGroups.getFirst();
        
        while ( !this.salienceGroups.isEmpty() && ( group == null || ( group != null && group.getList().isEmpty() ) ) ) {
            this.salienceGroups.removeFirst();
            group = (SalienceGroup) this.salienceGroups.getFirst();
        }

        if ( group != null ) {
            ActivationNode node =  (ActivationNode) group.getList().removeFirst();
                if ( group.getList().isEmpty() ) {
                    this.salienceGroups.removeFirst();
                }
                
                return node.getActivation();

        }

        return null;
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean activate) {
        this.active = activate;
    }

    /**
     * Iterates a PriorityQueue removing empty entries until it finds a
     * populated entry and return true, otherwise it returns false;
     * 
     * @param priorityQueue
     * @return
     */
    public boolean isEmpty() {
        return this.salienceGroups.isEmpty();
    }

    public Activation[] getActivations() {
        return null;
        // return (Activation[]) this.queue.toArray(new AgendaItem[this.queue.size()]);
    }

    public Activation[] getQueue() {
        return null;
        //return this.queue.getQueueable();
    }

    public String toString() {
        return "AgendaGroup '" + this.name + "'";
    }

    public boolean equal(final Object object) {
        if ( (object == null) || !(object instanceof SimpleAgendaGroup) ) {
            return false;
        }

        if ( ((SimpleAgendaGroup) object).name.equals( this.name ) ) {
            return true;
        }

        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public void setFocus() {
        throw new UnsupportedOperationException();
    }

}
