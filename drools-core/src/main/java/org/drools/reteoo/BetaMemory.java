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

package org.drools.reteoo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.drools.RuleBaseConfiguration;
import org.drools.WorkingMemory;
import org.drools.common.BetaNodeConstraints;
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.beta.BetaLeftMemory;
import org.drools.reteoo.beta.BetaMemoryFactory;
import org.drools.reteoo.beta.BetaRightMemory;
import org.drools.spi.Tuple;

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

    /**
     * 
     */
    private static final long serialVersionUID = -5494203883277782421L;

    /** Left-side tuples. */
    private BetaLeftMemory    leftMemory;

    /** Right-side tuples. */
    private BetaRightMemory   rightMemory;

    /** 
     * This map is needed to link FactHandles to ObjectMatches specifically to
     * support random access operations like remove(fact);
     */
    private Map               rightObjectMap;

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Construct a BetaMemory with a LinkedList for the <code>Tuples</code> and a <code>LinkedHashMap</code> for the 
     * <code>FactHandle</code>s
     */
    BetaMemory(final RuleBaseConfiguration config,
               final BetaNodeConstraints binder) {
        this.leftMemory = BetaMemoryFactory.newLeftMemory( config,
                                                           binder );
        this.rightMemory = BetaMemoryFactory.newRightMemory( config,
                                                             binder );
        this.rightObjectMap = new HashMap();
    }

    public BetaLeftMemory getLeftTupleMemory() {
        return this.leftMemory;
    }

    public BetaRightMemory getRightObjectMemory() {
        return this.rightMemory;
    }

    /**
     * @return A tuple iterator with possible matches for the handle
     */
    public Iterator leftTupleIterator(final WorkingMemory wm,
                                      final InternalFactHandle handle) {
        return this.leftMemory.iterator( wm,
                                         handle );
    }

    /**
     * The iterator iterates the propagated FactHandles in assertion order
     * @return The an Iterator for the right memory
     */
    Iterator rightObjectIterator(final WorkingMemory wm,
                                 final Tuple tuple) {
        return this.rightMemory.iterator( wm,
                                          tuple );
    }

    /**
     * Add a <code>ReteTuple</code> to the memory.
     * @param tuple
     */
    void add(final WorkingMemory wm,
             final ReteTuple tuple) {
        this.leftMemory.add( wm,
                             tuple );
    }

    /**
     * Remove the <code>ReteTuple</code> from the left memory.
     * @param tuple
     */
    void remove(final WorkingMemory wm,
                final ReteTuple tuple) {
        this.leftMemory.remove( wm,
                                tuple );
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
    ObjectMatches add(final WorkingMemory wm,
                      final InternalFactHandle handle) {
        final ObjectMatches objectMatches = new ObjectMatches( handle );
        this.rightObjectMap.put( handle,
                                 objectMatches );
        this.rightMemory.add( wm,
                              objectMatches );
        return objectMatches;
    }

    ObjectMatches add(final WorkingMemory wm,
                      final ObjectMatches objectMatches) {
        this.rightObjectMap.put( objectMatches.getFactHandle(),
                                 objectMatches );
        this.rightMemory.add( wm,
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
    ObjectMatches remove(final WorkingMemory wm,
                         final InternalFactHandle handle) {
        final ObjectMatches matches = (ObjectMatches) this.rightObjectMap.remove( handle );
        this.rightMemory.remove( wm,
                                 matches );
        return matches;
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
