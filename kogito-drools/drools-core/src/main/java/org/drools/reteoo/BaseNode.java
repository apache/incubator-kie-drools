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

import org.drools.spi.PropagationContext;
import org.drools.spi.ReteooNode;

/**
 * The base class for all Rete nodes.
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
abstract class BaseNode
    implements
    ReteooNode {
    protected final int id;

    protected boolean   attachingNewNode = false;

    protected boolean   hasMemory        = false;

    protected int       sharedCount      = 0;

    /**
     * All nodes have a unique id, set in the constructor.
     * 
     * @param id
     *      The unique id
     */
    public BaseNode(int id) {
        super();
        this.id = id;
    }

    /* (non-Javadoc)
     * @see org.drools.spi.ReteooNode#getId()
     */
    public int getId() {
        return this.id;
    }

    /**
     * Specifies with the node has any memory.
     * 
     * @param hasMemory
     */
    protected void setHasMemory(boolean hasMemory) {
        this.hasMemory = hasMemory;
    }

    /**
     * 
     * Indicates whether the node has any memory.
     * @return
     */
    public boolean hasMemory() {
        return this.hasMemory;
    }

    /**
     * Attaches the node into the network. Usually to the parent <code>ObjectSource</code> or <code>TupleSource</code>
     */
    public abstract void attach();

    public abstract void attach(WorkingMemoryImpl[] workingMemories);

    /**
     * Removes the node from teh network. Usually from the parent <code>ObjectSource</code> or <code>TupleSource</code>
     *
     */
    public abstract void remove(BaseNode node,
                                WorkingMemoryImpl[] workingMemories);

    /**
     * When nodes are added to the network that already has data. that existing data must be repropagated to the new node.
     * This new propagation may result in one or more assertions, so a PropagationContext and the workingMemory for the facts
     * must be provided.
     * 
     * @param workingMemory
     *      The WorkingMemory
     * @param context
     *      The PropagationContext
     *      
     */
    public abstract void updateNewNode(WorkingMemoryImpl workingMemory,
                                       PropagationContext context);

    /**
     * Each time a node is shared a counter is increased.
     *
     */
    public void addShare() {
        ++this.sharedCount;
    }

    /**
     * Each time a node is unshared a counter is decreased.
     *
     */
    public void removeShare() {
        --this.sharedCount;
    }

    /**
     * Indicates whether the node is shared.
     * @return
     */
    public boolean isShared() {
        return (this.sharedCount == 0) ? false : true;
    }

    /**
     * Returns the number of times the node is shared
     * @return
     *      int value indicating the share count.
     */
    public int getSharedCount() {
        return this.sharedCount;
    }

    /** 
     * The hashCode return is simply the unique id of the node. It is expected that base classes will also implement equals(Object object). 
     */
    public int hashCode() {
        return this.id;
    }

}
