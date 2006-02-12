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

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.spi.ConflictResolver;
import org.drools.spi.AgendaGroup;
import org.drools.util.PriorityQueue;

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
    private final String        name;
    
    private Map                 activationQueues = Collections.EMPTY_MAP;

    /** Items in the agenda. */
    private final PriorityQueue priorityQueue;

    /**
     * Construct an <code>AgendaGroup</code> with the given name.
     * 
     * @param name
     *      The <AgendaGroup> name.
     */
    public AgendaGroupImpl(String name) {
        this.name = name;
        this.priorityQueue = new PriorityQueue( );
    }

    /* (non-Javadoc)
     * @see org.drools.spi.AgendaGroup#getName()
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the <code>PriorityQueue</code> for the <code>AgendaGroup</code>.
     * @return the <code>PriotyQueue</code>
     */
    public PriorityQueue getPriorityQueue() {
        return this.priorityQueue;
    }
    
    /** 
     * Returns the Lifo <code>ActivationQueue</code> for the given salience. A Map is used to maintain
     * the salience/ActivationQueue lookup. This lookup is lazy with ActivationQueues created on first
     * get.     
     * 
     * @param salience
     * @return
     *      ActivationQueue
     */
    public ActivationQueue getActivationQueue(int salience) {
        if ( this.activationQueues == Collections.EMPTY_MAP ) {
            this.activationQueues = new HashMap(1);
        }
        
        ActivationQueue queue = (ActivationQueue) this.activationQueues.get( new Integer( salience ) );
        if ( queue == null ) {
            queue = new ActivationQueue( salience );
            this.activationQueues.put( new Integer( salience ), queue );
        }
        
        return queue;
    }

    /**
     * Adds an ActivationQueue to the PriorityQueue. Only <code>ActivationQueue</code>s not already added, 
     * considered inactive, can be added.
     * 
     * @param queue 
     *      the <code>ActivationQueue</code>
     */
    public void addToAgenda(ActivationQueue queue) {
        if ( !queue.isActive() ) {
            queue.setActivated( true );
            this.priorityQueue.add( queue );
        }
    }    
    
    /* (non-Javadoc)
     * @see org.drools.spi.AgendaGroup#size()
     */
    public int size() {
        // Iterates the PriorityQueue getting the size of each ActivationQueue. Empty ActivationQueues are removed
        int size = 0;
        for (Iterator it = this.priorityQueue.iterator(); it.hasNext(); ) {
            ActivationQueue queue = (ActivationQueue) it.next();
            if ( !queue.isEmpty() ) {
                size += queue.size();                
            } else {
                queue.setActivated( false );
                it.remove();
            }
        }
        return size;
    }
    
    /**
     * Iterates a PriorityQueue removing empty entries until it finds a populated entry and return true,
     * otherwise it returns false;
     * 
     * @param priorityQueue
     * @return
     */
    public boolean isEmpty() {
        // Iterate removing empty queues until we either find a populated queue or there are no more queues
        boolean empty = this.priorityQueue.isEmpty();
        while ( ! empty ) {
            ActivationQueue queue = (ActivationQueue) this.priorityQueue.get();
            if ( queue.isEmpty() ) {
                queue.setActivated( false );
                this.priorityQueue.remove();
            }  else {
                break;
            }
            empty = this.priorityQueue.isEmpty();
        }        
        
        return empty;
    }    

    public String toString() {
        return "AgendaGroup '" + this.name + "'";
    }


    public boolean equal(Object object) {
        if ( (object == null) || !(object instanceof AgendaGroupImpl) ) {
            return false;
        }

        if ( ((AgendaGroupImpl) object).name.equals( this.name ) ) {
            return true;
        }

        return false;
    }

    public int hashcode() {
        return this.name.hashCode();
    }    
}
