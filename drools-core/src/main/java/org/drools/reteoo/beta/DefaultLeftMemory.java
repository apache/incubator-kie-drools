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
import org.drools.util.MultiLinkedList;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * DefaultLeftMemory
 * 
 * A default implementation for Left Memory
 *
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a> 
 *
 * Created: 12/02/2006
 */
public class DefaultLeftMemory
    implements
    BetaLeftMemory {

    private final MultiLinkedList memory;

    public DefaultLeftMemory() {
        this.memory = new MultiLinkedList();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#add(org.drools.WorkingMemory, org.drools.util.MultiLinkedListNodeWrapper)
     */
    public void add(WorkingMemory workingMemory,
                    MultiLinkedListNodeWrapper tuple) {
        this.memory.add( tuple );
    }

    /**
     * @inheritDoc
     */
    public void remove(WorkingMemory workingMemory,
                       MultiLinkedListNodeWrapper tuple) {
        this.memory.remove( tuple );
    }

    /**
     * @inheritDoc
     */
    public void add(WorkingMemory workingMemory,
                    ReteTuple tuple) {
        this.memory.add( tuple );
    }

    /**
     * @inheritDoc
     */
    public void remove(WorkingMemory workingMemory,
                       ReteTuple tuple) {
        this.memory.remove( tuple );
    }

    /**
     * @inheritDoc
     */
    public Iterator iterator(WorkingMemory workingMemory,
                             FactHandleImpl handle) {
        return this.memory.iterator();
    }

    /**
     * @inheritDoc
     */
    public Iterator iterator() {
        return this.memory.iterator();
    }

    /**
     * @inheritDoc
     */
    public boolean isEmpty() {
        return this.memory.isEmpty();
    }

    /**
     * @inheritDoc
     */
    public void selectPossibleMatches(WorkingMemory workingMemory,
                                      FactHandleImpl handle) {
        // nothing to do. all tuples are possible matches
    }

    /**
     * @inheritDoc
     */
    public boolean isPossibleMatch(MultiLinkedListNodeWrapper tuple) {
        return tuple.getLinkedList() == this.memory;
    }

    /**
     * @inheritDoc
     */
    public int size() {
        return this.memory.size();
    }

}
