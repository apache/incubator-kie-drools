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

import org.drools.spi.Activation;
import org.drools.util.LinkedList;
import org.drools.util.LinkedListNode;

/**
 * A LIFO <code>Activation</code> queue based on a <code>LinkedList</code>. Each salience rule value 
 * is assigned an <code>ActivationQueue</code>, the salience number is used to prioritise the 
 * <code>ActivationQueue</code> within the <code>PriorityQueue<code>. Each <code>Activation</code> in the queue 
 * must implement the <code>LinkedListNode</code> interface. Each added <code>Activation</code>
 * is placed at the end of the queue; however <code>Activations</code> can be removed from the end or 
 * from any point in queue. 
 * <p>
 * <code>ActivationQueue</code> implements <code>Comparable</code>, based on the salience value, this 
 * allows the queue to be correctly prioritised by salience value on the <code>PriorityQueue</code>.
 * <p>
 * When an <code>ActivationQueue</code> is placed onto a <code>PriorityQueue</code> it is considered active, 
 * when it is removed it is inactivate. This ensures that an active <code>ActivationQueue</code> placed 
 * onto a <code>PriorityQueue</code> cannot be re-added.
 * 
 * @see LinkedList
 * @see PriorityQueue
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
public class ActivationQueue
    implements
    Comparable {
    private LinkedList list;
    
    private final int  salience;

    private boolean    active;

    /**
     * Constructs a new <code>ActivationQueue</code> for a given saliene value using a
     *  <code>LinkedList</code> to maintain the queue of <code>Activation</code>s.
     *  
     * @param salience
     *      The salience queue that the queue is for.
     */
    public ActivationQueue(int salience) {
        this.salience = salience;
        this.list = new LinkedList();
    }

    /**
     * Returns the salience.
     * @return
     *      The salience queue that the queue is for.
     */
    public int getSalience() {
        return this.salience;
    }

    /**
     * Has the queue been placed onto a <code>PriorityQueue</code>, if so this returns true until it 
     * is removed. 
     * @return
     *      boolean
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Specifies whether the queue has been placed onto a <code>PriorityQueue</code>. If it has 
     *  it is set to true, else it is set to false. 

     * @param active
     *      boolena value that sets the activate state. 
     */
    public void setActivated(boolean active) {
        this.active = active;
    }


    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(T)
     */
    public int compareTo(Object object) {
        ActivationQueue other = (ActivationQueue) object;
        return this.salience - other.salience;
    }    
    
    /**
     * Add an <code>Activation</code> to the end of the queue
     * @param activation
     *      the <code>Activation</code> to be placed onto the queue
     */
    public void add(Activation activation) {        
        this.list.add( (LinkedListNode ) activation );
    }
    
    
    /**
     * Remove the <code>Activaton</code> at the end of the queue.
     * @return
     *      The last <code>Activation</code>.
     */
    public Activation remove() {
        AgendaItem item = (AgendaItem) this.list.getLast();
        item.remove();
        return item;
    }
    
    /**
     * Remove the given <code>Activation<code> from its place in the queue. This results in the 
     * previous <code>Activation</code> being linked to the next <code>activation</code>.
     * @param activation
     *      The <code>Activation</code> to be removed.
     */
    public void remove(Activation activation) {
        this.list.remove( (LinkedListNode) activation );
    }
    
    /**
     * Returns true if there are no <code>Activations</code> on the queue.
     * @return
     *      boolean value indicating the empty stae of the queue,
     */
    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public int size() {
        return this.list.size();
    }
}
