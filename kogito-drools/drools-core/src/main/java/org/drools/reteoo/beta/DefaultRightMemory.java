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

import javax.naming.OperationNotSupportedException;

import org.drools.WorkingMemory;
import org.drools.reteoo.ObjectMatches;
import org.drools.reteoo.ReteTuple;
import org.drools.util.MultiLinkedList;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * DefaultRightMemory
 * 
 * A default implementation for BetaRightMemory.
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a>
 *
 * Created: 12/02/2006
 */
public class DefaultRightMemory
    implements
    BetaRightMemory {

    private final MultiLinkedList memory;

    public DefaultRightMemory() {
        this.memory = new MultiLinkedList();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.reteoo.ObjectMatches)
     */
    public final void add(WorkingMemory workingMemory,
                    ObjectMatches matches) {
        this.memory.add( matches );
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#remove(org.drools.WorkingMemory, org.drools.reteoo.ObjectMatches)
     */
    public final void remove(WorkingMemory workingMemory,
                       ObjectMatches matches) {
        matches.getLinkedList().remove( matches );
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#add(org.drools.WorkingMemory, org.drools.util.MultiLinkedListNodeWrapper)
     */
    public final void add(WorkingMemory workingMemory,
                    MultiLinkedListNodeWrapper wrapper) {
        this.memory.add( wrapper );
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#remove(org.drools.WorkingMemory, org.drools.util.MultiLinkedListNodeWrapper)
     */
    public final void remove(WorkingMemory workingMemory,
                       MultiLinkedListNodeWrapper wrapper) {
        wrapper.getLinkedList().remove( wrapper );
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#iterator(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public final Iterator iterator(final WorkingMemory workingMemory,
                             final ReteTuple tuple) {
        return this.memory.iterator();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#isEmpty()
     */
    public final boolean isEmpty() {
        return this.memory.isEmpty();
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#selectPossibleMatches(org.drools.WorkingMemory, org.drools.reteoo.ReteTuple)
     */
    public final void selectPossibleMatches(WorkingMemory workingMemory,
                                      ReteTuple tuple) {
        // nothing to do
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#isPossibleMatch(org.drools.util.MultiLinkedListNodeWrapper)
     */
    public final boolean isPossibleMatch(MultiLinkedListNodeWrapper matches) {
        return matches.getLinkedList() == this.memory;
    }

    /**
     * 
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#size()
     */
    public final int size() {
        return this.memory.size();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaRightMemory#iterator()
     */
    public final Iterator iterator() {
        return memory.iterator();
    }

    /**
     * @inheritDoc
     */
    public BetaRightMemory getInnerMemory() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Default right memory does not support inner memory");
    }

    /**
     * @inheritDoc
     */
    public void setInnerMemory(BetaRightMemory innerMemory) throws OperationNotSupportedException  {
        throw new OperationNotSupportedException("Default right memory does not support inner memory");
    }

}
