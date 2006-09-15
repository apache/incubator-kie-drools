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
import org.drools.common.InternalFactHandle;
import org.drools.reteoo.ReteTuple;
import org.drools.util.MultiLinkedList;
import org.drools.util.MultiLinkedListNodeWrapper;

/**
 * DefaultLeftMemory
 * 
 * A default implementation for Left Memory
 *
 * @author <a href="mailto:tirelli@post.com">Edson Tirelli</a> 
 *
 * Created: 12/02/2006
 */
public class DefaultLeftMemory
    implements
    BetaLeftMemory {

    private static final long serialVersionUID = 5316711294716215716L;
    private final MultiLinkedList memory;

    public DefaultLeftMemory() {
        this.memory = new MultiLinkedList();
    }

    /**
     * @inheritDoc 
     *
     * @see org.drools.reteoo.beta.BetaLeftMemory#add(org.drools.WorkingMemory, org.drools.util.MultiLinkedListNodeWrapper)
     */
    public final void add(final WorkingMemory workingMemory,
                          final MultiLinkedListNodeWrapper tuple) {
        this.memory.add( tuple );
    }

    /**
     * @inheritDoc
     */
    public final void remove(final WorkingMemory workingMemory,
                             final MultiLinkedListNodeWrapper tuple) {
        this.memory.remove( tuple );
    }

    /**
     * @inheritDoc
     */
    public final void add(final WorkingMemory workingMemory,
                          final ReteTuple tuple) {
        this.memory.add( tuple );
    }

    /**
     * @inheritDoc
     */
    public final void remove(final WorkingMemory workingMemory,
                             final ReteTuple tuple) {
        this.memory.remove( tuple );
    }

    /**
     * @inheritDoc
     */
    public final Iterator iterator(final WorkingMemory workingMemory,
                                   final InternalFactHandle handle) {
        return this.memory.iterator();
    }

    /**
     * @inheritDoc
     */
    public final Iterator iterator() {
        return this.memory.iterator();
    }

    /**
     * @inheritDoc
     */
    public final boolean isEmpty() {
        return this.memory.isEmpty();
    }

    /**
     * @inheritDoc
     */
    public final void selectPossibleMatches(final WorkingMemory workingMemory,
                                            final InternalFactHandle handle) {
        // nothing to do. all tuples are possible matches
    }

    /**
     * @inheritDoc
     */
    public final boolean isPossibleMatch(final MultiLinkedListNodeWrapper tuple) {
        return tuple.getOuterList() == this.memory;
    }

    /**
     * @inheritDoc
     */
    public final int size() {
        return this.memory.size();
    }

    /**
     * @inheritDoc
     */
    public BetaLeftMemory getInnerMemory() throws OperationNotSupportedException {
        throw new OperationNotSupportedException( "Default left memory does not support inner memory" );
    }

    /**
     * @inheritDoc
     */
    public void setInnerMemory(final BetaLeftMemory innerMemory) throws OperationNotSupportedException {
        throw new OperationNotSupportedException( "Default left memory does not support inner memory" );
    }

}
