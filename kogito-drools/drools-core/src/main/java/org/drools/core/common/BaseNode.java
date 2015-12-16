/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.common;

import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.spi.RuleComponent;
import org.drools.core.util.Bag;
import org.kie.api.definition.rule.Rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * The base class for all Rete nodes.
 */
public abstract class BaseNode
    implements
    NetworkNode {

    protected int                      id;
    protected RuleBasePartitionId      partitionId;
    protected boolean                  partitionsEnabled;
    protected Bag<Rule>                associations;
    private   boolean                  streamMode;

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
        this.associations = new Bag<Rule>();
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        id = in.readInt();
        partitionId = (RuleBasePartitionId) in.readObject();
        partitionsEnabled = in.readBoolean();
        associations = (Bag<Rule>) in.readObject();
        streamMode = in.readBoolean();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt( id );
        out.writeObject( partitionId );
        out.writeBoolean( partitionsEnabled );
        out.writeObject( associations );
        out.writeBoolean( streamMode );
    }

    /* (non-Javadoc)
     * @see org.kie.spi.ReteooNode#getId()
     */
    public int getId() {
        return this.id;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public boolean isStreamMode() {
        return this.streamMode;
    }

    protected void setStreamMode(boolean streamMode) {
        this.streamMode = streamMode;
    }

    /**
     * Attaches the node into the network. Usually to the parent <code>ObjectSource</code> or <code>TupleSource</code>
     */
    public abstract void attach(BuildContext context);


    /**
     * A method that is called for all nodes whose network below them
     * changed, after the change is complete, providing them with an oportunity
     * for state update
     */
    public abstract void networkUpdated(UpdateContext updateContext);

    public boolean remove(RuleRemovalContext context,
                       ReteooBuilder builder,
                       InternalWorkingMemory[] workingMemories) {
        this.removeAssociation( context.getRule() );
        boolean removed = doRemove( context, builder, workingMemories );
        if ( !this.isInUse() && !(this instanceof EntryPointNode) ) {
            builder.getIdGenerator().releaseId( this.getId() );
        }
        return removed;
    }

    /**
     * Removes the node from teh network. Usually from the parent <code>ObjectSource</code> or <code>TupleSource</code>
     */
    protected abstract boolean doRemove(RuleRemovalContext context,
                                        ReteooBuilder builder,
                                        InternalWorkingMemory[] workingMemories);

    /**
     * Returns true in case the current node is in use (is referenced by any other node)
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
     */
    public RuleBasePartitionId getPartitionId() {
        return this.partitionId;
    }

    /**
     * Sets the partition this node belongs to
     */
    public void setPartitionId(final RuleBasePartitionId partitionId) {
        this.partitionId = partitionId;
    }

    /**
     * Associates this node with the give rule
     */
    public void addAssociation( Rule rule ) {
        this.associations.add( rule );
    }

    public void addAssociation( Rule rule, RuleComponent ruleComponent ) {
        addAssociation( rule );
    }

    /**
     * Removes the association to the given rule from the
     * associations map.
     */
    public void removeAssociation( Rule rule ) {
        this.associations.remove(rule);
    }

    public int getAssociationsSize() {
        return this.associations.size();
    }

    public int getAssociationsSize(Rule rule) {
        return this.associations.sizeFor(rule);
    }

    public boolean isAssociatedWith( Rule rule ) {
        return this.associations.contains( rule );
    }
}
