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
import org.drools.util.Queueable;

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
public class AgendaGroupImpl
    implements
    AgendaGroup {

    private static final long     serialVersionUID = 320L;

    private final String          name;

    /** Items in the agenda. */
    private final BinaryHeapQueue queue;

    private boolean               active;

    /**
     * Construct an <code>AgendaGroup</code> with the given name.
     * 
     * @param name
     *      The <AgendaGroup> name.
     */
    
    
    public AgendaGroupImpl(final String name, final ConflictResolver conflictResolver) {
        this.name = name;
        this.queue = new BinaryHeapQueue( conflictResolver );
    }    

    /* (non-Javadoc)
     * @see org.drools.spi.AgendaGroup#getName()
     */
    public String getName() {
        return this.name;
    }

    public void clear() {
        this.queue.clear();
    }

    /* (non-Javadoc)
     * @see org.drools.spi.AgendaGroup#size()
     */
    public int size() {
        return this.queue.size();
    }

    public void add(final Activation activation) {
        this.queue.enqueue( (Queueable) activation );
    }

    public Activation getNext() {
        return (Activation) this.queue.dequeue();
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
        return this.queue.isEmpty();
    }

    public Activation[] getActivations() {
        return (Activation[]) this.queue.toArray( new AgendaItem[this.queue.size()] );
    }

    public Queueable[] getQueueable() {
        return this.queue.getQueueable();
    }

    public String toString() {
        return "AgendaGroup '" + this.name + "'";
    }

    public boolean equal(final Object object) {
        if ( (object == null) || !(object instanceof AgendaGroupImpl) ) {
            return false;
        }

        if ( ((AgendaGroupImpl) object).name.equals( this.name ) ) {
            return true;
        }

        return false;
    }

    public int hashCode() {
        return this.name.hashCode();
    }
}
