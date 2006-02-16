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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.drools.spi.PropagationContext;
import org.drools.util.LinkedList;

/**
 * Memory for left and right inputs of a <code>BetaNode</code>. The LeftMemory is a <code>LinkedList</code> for all incoming 
 * <code>Tuples</code> and the right memory is a LinkedHashMap which stores each incoming <code>FactHandle</code> with 
 * its <code>ObjectMatche</code>. A LinkedHashMap is used as iteration must be in assertion order otherwise you break
 * LIFO based agenda evaluation.
 * 
 * @see ReteTuple
 * @see ObjectMatches
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 */
class BetaMemory
    implements
    Serializable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Left-side tuples. */
    private LinkedList leftMemory;

    /** Right-side tuples. */
    private final Map  rightMemory;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct a BetaMemory with a LinkedList for the <code>Tuples</code> and a <code>LinkedHashMap</code> for the 
     * <code>FactHandle</code>s
     */
    BetaMemory() {
        this.leftMemory = new LinkedList();
        this.rightMemory = new LinkedHashMap();
    }
    
    public LinkedList getLeftTupleMemory() {
    	return this.leftMemory;
    }
    
    public Map getRightFactHandleMemory() {
    	return this.rightMemory;
    }

    
    /**
     * @return The first <code>Tuple</code>
     */
    public ReteTuple getFirstTuple() {
        return (ReteTuple) this.leftMemory.getFirst();
    }

    /**
     * @return The last <code>Tuple</code>
     */
    public ReteTuple getLastTuple() {
        return (ReteTuple) this.leftMemory.getLast();
    }
    
    /**
     * The iterator iterates the propagated FactHandles in assertion order
     * @return The an Iterator for the right memory
     */    
    Iterator rightObjectIterator() {
        return this.rightMemory.values().iterator();
    }


    /**
     * Add a <code>ReteTuple</code> to the end of the LinkedList.
     * @param tuple
     */
    void add(ReteTuple tuple) {
        this.leftMemory.add( tuple );
    }

    /**
     * Remove the <code>ReteTuple</code> from the left memory.
     * @param tuple
     */
    void remove(ReteTuple tuple) {
        this.leftMemory.remove( tuple );  
    }

    
    /**
     * Adds a <code>FactHandleImp</code> to the right memory <code>LinkedHashMap</code>. On being added to
     * The right memory an <code>ObjectMatches</code> class is created and added as the key's value and then returned.
     *  
     *  @see ObjectMatches
     *  
     * @param handle
     * @return
     *      The ObjectMatches to hold the tuples that match with the <code>FactHandleImpl</code>
     */
    ObjectMatches add(FactHandleImpl handle) {
        ObjectMatches objectMatches = new ObjectMatches( handle );
        this.rightMemory.put( handle,
                              objectMatches );
        return objectMatches;
    }

    
    /**
     * Remove the <code>FactHandleImpl<code> from the right memroy <code>LinkedHashMap</code>. The key's ObjectMatches
     * value is returned.
     * 
     * @param handle
     * @return
     *      The ObjectMatches that held the tuples that match with the <code>FactHandleImpl</code>
     */
    ObjectMatches remove(FactHandleImpl handle) {
        return (ObjectMatches) this.rightMemory.remove( handle );
    }

    
    /**
     * @return The boolean value indicating the empty status of the left tuple memory
     */
    public boolean isLeftMemoryEmpty() {
        return this.leftMemory.isEmpty();
    }

    /**
     * @return The boolean value indicating the empty status of the right fact handle memory
     */    
    public boolean isRightMemoryEmpty() {
        return this.rightMemory.isEmpty();
    }        
    /**
     * Produce debug string.
     * 
     * @return The debug string.
     */
    public String toString() {
        return "[JoinMemory]";
        //return "[JoinMemory \n\tleft=" + this.leftMemory + "\n\tright=" + this.rightMemory + "]";
    }

}
