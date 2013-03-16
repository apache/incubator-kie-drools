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

package org.drools.core.common;

import java.util.PriorityQueue;

import org.drools.core.util.BinaryHeapQueue;
import org.drools.core.util.Queueable;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

/**
 * <code>AgendaGroup</code> implementation that uses a <code>PriorityQueue</code> to prioritise the evaluation of added
 * <code>ActivationQueue</code>s. The <code>AgendaGroup</code> also maintains a <code>Map</code> of <code>ActivationQueues</code>
 * for requested salience values.
 *
 * @see PriorityQueue
 * @see ActivationQueue
 */
public class BinaryHeapQueueAgendaGroup
    implements
    InternalAgendaGroup {

    private static final long serialVersionUID = 510l;

    private String            name;

    /** Items in the agenda. */
    private BinaryHeapQueue   queue;

    private boolean           active;

    private PropagationContext autoFocusActivator;
    
    private long activatedForRecency;

    private long clearedForRecency;
    /**
     * Construct an <code>AgendaGroup</code> with the given name.
     *
     * @param name
     *      The <AgendaGroup> name.
     */
    public BinaryHeapQueueAgendaGroup() {

    }

    public BinaryHeapQueueAgendaGroup(final String name,
                                      final InternalRuleBase ruleBase) {
        this.name = name;
        this.queue = new BinaryHeapQueue( ruleBase.getConfiguration().getConflictResolver() );
        this.clearedForRecency = -1;
    }

    /* (non-Javadoc)
     * @see org.kie.spi.AgendaGroup#getName()
     */
    public String getName() {
        return this.name;
    }

    public void clear() {
        this.queue.clear();
    }

    public Activation[] getAndClear() {
        return this.queue.getAndClear();
    }

    /* (non-Javadoc)
     * @see org.kie.spi.AgendaGroup#size()
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
    
    public Activation  peekNext() {
        return this.queue.peek();
    }

    public boolean isActive() {
        return this.active;
    }

    public void setActive(final boolean activate) {
        this.active = activate;
    }

    public void setAutoFocusActivator(PropagationContext autoFocusActivator) {
        this.autoFocusActivator = autoFocusActivator;
    }
    
    public PropagationContext getAutoFocusActivator() {
        return this.autoFocusActivator;
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

    public String toString() {
        return "AgendaGroup '" + this.name + "'";
    }

    public boolean equals(final Object object) {
        if ( (object == null) || !(object instanceof BinaryHeapQueueAgendaGroup) ) {
            return false;
        }

        if ( ((BinaryHeapQueueAgendaGroup) object).name.equals( this.name ) ) {
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

    public void remove(AgendaItem agendaItem) {
        this.queue.dequeue( agendaItem.getIndex() );
    }

    public void setActivatedForRecency(long recency) {
        this.activatedForRecency = recency;
    }

    public long getActivatedForRecency() {
        return this.activatedForRecency;
    }
    
    public void setClearedForRecency(long recency) {
        this.clearedForRecency = recency;
    }

    public long getClearedForRecency() {
        return this.clearedForRecency;
    }      
}
