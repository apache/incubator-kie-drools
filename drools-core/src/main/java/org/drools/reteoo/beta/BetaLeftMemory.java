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
import org.drools.reteoo.FactHandleImpl;
import org.drools.reteoo.ReteTuple;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * BetaLeftMemory
 * 
 * The <code>BetaLeftMemory</code> is the interface for all classes
 * implementing the left memory of a <code>BetaMemory</code> instance. 
 * 
 * The left memory is responsible for indexing and storing references
 * to all input tuples in the ReteOO network.
 * 
 * Specialized implementations for this interface are capable of indexing 
 * and partitioning the tuples with the objective of improve query time.
 * Although, the implementations are all required to keep the order of
 * objects, and return then in order when iterating over.
 *
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a> 
 *
 * Created: 12/02/2006
 */
public interface BetaLeftMemory {
    
    /**
     * Adds the given tuple to the memory
     * 
     * @param workingMemory the working memory reference
     * @param tuple the tuple to add
     */
    public void add(WorkingMemory workingMemory, MultiLinkedListNodeWrapper tuple);
    
    /**
     * Remove the given tuple from the memory
     * 
     * @param workingMemory the working memory reference
     * @param tuple the tuple to remove
     */
    public void remove(WorkingMemory workingMemory, MultiLinkedListNodeWrapper tuple);
    
    /**
     * Adds the given tuple to the memory
     * 
     * @param workingMemory the working memory reference
     * @param tuple the tuple to add
     */
    public void add(WorkingMemory workingMemory, ReteTuple tuple);
    
    /**
     * Remove the given tuple from the memory
     * 
     * @param tuple the tuple to remove
     */
    public void remove(WorkingMemory workingMemory, ReteTuple tuple);
    
    /**
     * Returns an iterator to iterate over tuples that attend the binder/handle
     * constraints 
     *
     * @param workingMemory the working memory reference
     * @param handle the handle whose tuples will try to join
     * 
     * @return an Iterator over the tuples
     */
    public Iterator iterator(WorkingMemory workingMemory, FactHandleImpl handle);

    /**
     * Returns the number of tuples currently stored in the left memory
     * 
     * @return the number of tuples currently stored in the left memory
     */
    public int size();
    
    /**
     * Checks if this memory is empty
     * 
     * @return true if the memory is empty, false otherwise
     */
    public boolean isEmpty();
    
    /**
     * Prepares the left memory for subsequent calls of isPossibleMatch()
     * based on the constraints applied to the handle.
     * 
     * Also, iterator() usually calls this method to select tuples to iterate 
     * over.
     * 
     * @param workingMemory the working memory reference
     * @param handle the handle whose tuples shall match
     */
    public void selectPossibleMatches(WorkingMemory workingMemory, FactHandleImpl handle);
    
    /**
     * Returns true if the tuple is a possible match to the handle
     * passed to the previous selectPossibleMatches() call
     * 
     * @param tuple 
     * @return
     */
    public boolean isPossibleMatch(MultiLinkedListNodeWrapper tuple);
}

