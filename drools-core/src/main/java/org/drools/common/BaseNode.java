/*
 * Copyright 2010 JBoss Inc
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import org.drools.definition.rule.Rule;
import org.drools.reteoo.EntryPointNode;
import org.drools.reteoo.ReteooBuilder;
import org.drools.reteoo.RuleRemovalContext;
import org.drools.spi.RuleComponent;

/**
 * The base class for all Rete nodes.
 */
public abstract class BaseNode
    implements
    NetworkNode {

    protected int                      id;
    protected RuleBasePartitionId      partitionId;
    protected boolean                  partitionsEnabled;
    protected Map<Rule, RuleComponent> associations;

    public BaseNode() {

    }

    /**
     * All nodes have a unique id, set in the constructor.
     *
     * @param id
     *      The unique id
     */
    public BaseNode(final int id,
                    final RuleBasePartitionId partitionId,
                    final boolean partitionsEnabled) {
        super();
        this.id = id;
        this.partitionId = partitionId;
        this.partitionsEnabled = partitionsEnabled;
        this.associations = new HashMap<Rule, RuleComponent>();
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        id = in.readInt();
        partitionId = (RuleBasePartitionId) in.readObject();
        partitionsEnabled = in.readBoolean();
        associations = (Map<Rule, RuleComponent>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt( id );
        out.writeObject( partitionId );
        out.writeBoolean( partitionsEnabled );
        out.writeObject( associations );
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

    /**
     * A method that is called for all nodes whose network bellow them 
     * changed, after the change is complete, providing them with an oportunity
     * for state update
     */
    public abstract void networkUpdated();

    public void remove(RuleRemovalContext context,
                       ReteooBuilder builder,
                       BaseNode node,
                       InternalWorkingMemory[] workingMemories) {
        this.removeAssociation( context.getRule() );
        doRemove( context,
                  builder,
                  node,
                  workingMemories );
        if ( !this.isInUse() && !(this instanceof EntryPointNode) ) {
            builder.getIdGenerator().releaseId( this.getId() );
        }
    }

    /**
     * Removes the node from teh network. Usually from the parent <code>ObjectSource</code> or <code>TupleSource</code>
     * @param builder 
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
        return "[" + this.getClass().getSimpleName() + "(" + this.id + ")]";
    }

    /**
     * Returns the partition ID for which this node belongs to
     * 
     * @return
     */
    public RuleBasePartitionId getPartitionId() {
        return this.partitionId;
    }

    /**
     * Sets the partition this node belongs to
     * 
     * @param partitionId
     */
    public void setPartitionId(final RuleBasePartitionId partitionId) {
        this.partitionId = partitionId;
    }

    /**
     * Creates an association between this node and the rule + rule component
     * that caused the creation of this node. Since nodes might be shared,
     * there might be more than one source for each node.
     * 
     * @param rule The rule source
     * @param component
     */
    public void addAssociation( Rule rule, RuleComponent component ) {
        this.associations.put( rule, component );
    }
    
    /**
     * Returns the map of associations for this node
     * 
     * @return
     */
    public Map<Rule, RuleComponent> getAssociations() {
        return this.associations;
    }
    
    /**
     * Removes the association to the given rule from the
     * associations map.
     *  
     * @param rule
     */
    public void removeAssociation( Rule rule ) {
        this.associations.remove( rule );
    }
}
