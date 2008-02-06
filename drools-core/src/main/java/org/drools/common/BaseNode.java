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

package org.drools.common;

import org.drools.reteoo.ReteooBuilder;
import org.drools.reteoo.RuleRemovalContext;

/**
 * The base class for all Rete nodes.
 * 
 * @author <a href="mailto:mark.proctor@jboss.com">Mark Proctor</a>
 * @author <a href="mailto:bob@werken.com">Bob McWhirter</a>
 *
 */
public abstract class BaseNode
    implements
    NetworkNode {
    protected final int id;

    /**
     * All nodes have a unique id, set in the constructor.
     * 
     * @param id
     *      The unique id
     */
    public BaseNode(final int id) {
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
     * Attaches the node into the network. Usually to the parent <code>ObjectSource</code> or <code>TupleSource</code>
     */
    public abstract void attach();

    public abstract void attach(InternalWorkingMemory[] workingMemories);

    public void remove(RuleRemovalContext context,
                       ReteooBuilder builder,
                       BaseNode node,
                       InternalWorkingMemory[] workingMemories) {
        doRemove( context,
                  builder,
                  node,
                  workingMemories );
        if ( !isInUse() ) {
            builder.getIdGenerator().releaseId( this.getId() );
        }
    }

    /**
     * Removes the node from teh network. Usually from the parent <code>ObjectSource</code> or <code>TupleSource</code>
     * @param builder TODO
     *
     */
    protected abstract void doRemove(RuleRemovalContext context,
                                     ReteooBuilder builder,
                                     BaseNode node,
                                     InternalWorkingMemory[] workingMemories);

    /**
     * Returns true in case the current node is in use (is referenced by any other node)
     * @return
     */
    public abstract boolean isInUse();

    /** 
     * The hashCode return is simply the unique id of the node. It is expected that base classes will also implement equals(Object object). 
     */
    public int hashCode() {
        return this.id;
    }

    public String toString() {
        return "[" + this.getClass().getName() + "(" + this.id + ")]";
    }
}
