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

package org.drools.reteoo.beta;

import java.util.Iterator;

import org.drools.WorkingMemory;
import org.drools.reteoo.ObjectMatches;
import org.drools.reteoo.ReteTuple;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * BetaRightMemory
 * 
 * The <code>BetaRightMemory</code> is the interface for all classes
 * implementing the right memory of a <code>BetaMemory</code> instance. 
 * 
 * The right memory is responsible for indexing and storing references
 * to all input fact handles in the ReteOO network.
 * 
 * Specialized implementations for this interface are capable of indexing 
 * and partitioning the fact handles with the objective of improve query time.
 * Although, the implementations are all required to keep the order of
 * objects, and iterate then in order with the <code>iterator()</code> method. 
 *
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a> 
 *
 * Created: 12/02/2006
 */
public interface BetaRightMemory {

    /**
     * Adds an object to the right memory
     * 
     * @param workingMemory the working memory reference
     * @param matches the matches object (with the reference to the fact implementation)
     */
    public void add(WorkingMemory workingMemory,
                    ObjectMatches matches);

    /**
     * Removes the given match from memory
     * 
     * @param workingMemory the working memory reference
     * @param matches
     */
    public void remove(WorkingMemory workingMemory,
                       ObjectMatches matches);

    /**
     * Adds an object to the right memory
     * 
     * @param workingMemory the working memory reference
     * @param matches the matches object (with the reference to the fact implementation)
     */
    public void add(WorkingMemory workingMemory,
                    MultiLinkedListNodeWrapper matches);

    /**
     * Removes the given match from memory
     * 
     * @param workingMemory the working memory reference
     * @param matches
     */
    public void remove(WorkingMemory workingMemory,
                       MultiLinkedListNodeWrapper matches);

    /**
     * Returns an iterator that allows to iterate over the ObjectMatches (and
     * referenced FactHandles) that are possible matches to the given tuple.
     * 
     * IMPORTANT: this iterator should NOT implement/support the remove() method
     * 
     * @param workingMemory the working memory reference
     * @param tuple the tuple that will possibly join with the returned matches
     * @return
     */
    public Iterator iterator(WorkingMemory workingMemory,
                             ReteTuple tuple);

    /**
     * Returns an iterator that allows to iterate over all the ObjectMatches
     * in the beta right memory.
     * This might not be much efficient for indexed memories, so this shall 
     * be used only when changing RETE network layoud (adding/removing rules).
     * 
     * @return
     */
    public Iterator iterator();

    /**
     * Returns the number of objects currently stored in the right memory
     * 
     * @return the number of objects currently stored in the right memory
     */
    public int size();

    /**
     * Returns true if the memory is empty
     * @return
     */
    public boolean isEmpty();

    /**
     * Prepares the right memory for subsequent calls of isPossibleMatch()
     * based on the constraints applied to the tuples
     * 
     * @param handle
     */
    public void selectPossibleMatches(WorkingMemory workingMemory,
                                      ReteTuple tuple);

    /**
     * Returns true if the matches is a possible match to the tuple
     * given to the previous selectPossibleMatches() call
     * 
     * @param matches
     * @return
     */
    public boolean isPossibleMatch(MultiLinkedListNodeWrapper matches);

}
