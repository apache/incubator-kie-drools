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
import org.drools.core.reteoo.LeftTupleSource;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ReteooBuilder;
import org.drools.core.reteoo.RuleRemovalContext;
import org.drools.core.reteoo.Sink;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.util.Bag;
import org.kie.api.definition.rule.Rule;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;

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

    protected int hashcode;

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
        hashcode = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt( id );
        out.writeObject( partitionId );
        out.writeBoolean( partitionsEnabled );
        out.writeObject( associations );
        out.writeBoolean( streamMode );
        out.writeInt(hashcode);
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
     * changed, after the change is complete, providing them with an opportunity
     * for state update
     */
    public abstract void networkUpdated(UpdateContext updateContext);

    public boolean remove(RuleRemovalContext context,
                       ReteooBuilder builder,
                       InternalWorkingMemory[] workingMemories) {
        boolean removed = doRemove( context, builder, workingMemories );
        if ( !this.isInUse() && !(this instanceof EntryPointNode) ) {
            builder.getIdGenerator().releaseId( this.getId() );
        }
        return removed;
    }

    /**
     * Removes the node from the network. Usually from the parent <code>ObjectSource</code> or <code>TupleSource</code>
     */
    protected abstract boolean doRemove(RuleRemovalContext context,
                                        ReteooBuilder builder,
                                        InternalWorkingMemory[] workingMemories);

    /**
     * Returns true in case the current node is in use (is referenced by any other node)
     */
    public abstract boolean isInUse();

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
    public void setPartitionId(BuildContext context, RuleBasePartitionId partitionId) {
        this.partitionId = partitionId;
    }

    /**
     * Associates this node with the give rule
     */
    public void addAssociation( Rule rule ) {
        this.associations.add( rule );
    }

    public void addAssociation( BuildContext context, Rule rule ) {
        addAssociation( rule );
    }

    /**
     * Removes the association to the given rule from the
     * associations map.
     */
    public boolean removeAssociation( Rule rule ) {
        return this.associations.remove(rule);
    }

    public int getAssociationsSize() {
        return this.associations.size();
    }

    public int getAssociatedRuleSize() {
        return this.associations.getKeySize();
    }

    public int getAssociationsSize(Rule rule) {
        return this.associations.sizeFor(rule);
    }

    public boolean isAssociatedWith( Rule rule ) {
        return this.associations.contains( rule );
    }

    public boolean thisNodeEquals(final Object object) {
        return this == object || internalEquals( object );
    }

    protected abstract boolean internalEquals( Object object );

    @Override
    public final int hashCode() {
        return hashcode;
    }

    public Sink[] getSinks() {
        Sink[] sinks = null;
        if (this instanceof EntryPointNode ) {
            EntryPointNode source = (EntryPointNode) this;
            Collection<ObjectTypeNode> otns = source.getObjectTypeNodes().values();
            sinks = otns.toArray(new Sink[otns.size()]);
        } else if (this instanceof ObjectSource ) {
            ObjectSource source = (ObjectSource) this;
            sinks = source.getObjectSinkPropagator().getSinks();
        } else if (this instanceof LeftTupleSource ) {
            LeftTupleSource source = (LeftTupleSource) this;
            sinks = source.getSinkPropagator().getSinks();
        }
        return sinks;
    }
}
