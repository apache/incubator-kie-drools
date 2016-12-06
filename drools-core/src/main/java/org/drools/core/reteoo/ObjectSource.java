/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.UpdateContext;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.BitMask;
import org.drools.core.util.bitmask.EmptyBitMask;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import static org.drools.core.reteoo.PropertySpecificUtil.getSettableProperties;

/**
 * A source of <code>FactHandle</code>s for an <code>ObjectSink</code>.
 *
 * <p>
 * Nodes that propagate <code>FactHandleImpl</code> extend this class.
 * </p>
 *
 * @see ObjectSource
 * @see DefaultFactHandle
 */
public abstract class ObjectSource extends BaseNode
    implements
    Externalizable {
    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** The destination for <code>FactHandleImpl</code>. */
    protected ObjectSinkPropagator sink;

    protected ObjectSource         source;

    protected int                  alphaNodeHashingThreshold;


    protected BitMask declaredMask = EmptyBitMask.get();
    protected BitMask inferredMask = EmptyBitMask.get();
    
    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public ObjectSource() {

    }

    /**
     * Single parameter constructor that specifies the unique id of the node.
     */
    ObjectSource(final int id,
                 final RuleBasePartitionId partitionId,
                 final boolean partitionsEnabled) {
        this( id,
              partitionId,
              partitionsEnabled,
              null,
              3 );
    }

    /**
     * Single parameter constructor that specifies the unique id of the node.
     */
    ObjectSource(final int id,
                 final RuleBasePartitionId partitionId,
                 final boolean partitionsEnabled,
                 final ObjectSource objectSource,
                 final int alphaNodeHashingThreshold) {
        super(id, partitionId, partitionsEnabled);
        this.source = objectSource;
        this.alphaNodeHashingThreshold = alphaNodeHashingThreshold;
        this.sink = EmptyObjectSinkAdapter.getInstance();
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------
    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        sink = (ObjectSinkPropagator) in.readObject();
        source = (ObjectSource) in.readObject();
        alphaNodeHashingThreshold = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( sink );
        out.writeObject( source );
        out.writeInt( alphaNodeHashingThreshold );
    }
    
    public ObjectSource getParentObjectSource() {
        return this.source;
    }

    public InternalKnowledgeBase getKnowledgeBase() {
        return source.getKnowledgeBase();
    }

    public void initDeclaredMask(BuildContext context) {
        if ( context == null || context.getLastBuiltPatterns() == null ) {
            // only happens during unit tests
            declaredMask = AllSetBitMask.get();
            return;
        }
        
        Pattern pattern = context.getLastBuiltPatterns()[0];
        ObjectType objectType = pattern.getObjectType();
        
        if ( !(objectType instanceof ClassObjectType)) {
            // Only ClassObjectType can use property specific
            declaredMask = AllSetBitMask.get();
            return;
        }
        
        Class objectClass = ((ClassObjectType)objectType).getClassType();        
        TypeDeclaration typeDeclaration = context.getKnowledgeBase().getTypeDeclaration(objectClass);
        if ( typeDeclaration == null || !typeDeclaration.isPropertyReactive() ) {
            // if property specific is not on, then accept all modification propagations
            declaredMask = AllSetBitMask.get();
        } else {
            List<String> settableProperties = getSettableProperties(context.getKnowledgeBase(), objectClass);
            declaredMask = calculateDeclaredMask(settableProperties);
        }
    }
    
    public abstract BitMask calculateDeclaredMask(List<String> settableProperties);
    
    public void resetInferredMask() {
        this.inferredMask = EmptyBitMask.get();
    }
    
    public BitMask updateMask(BitMask mask) {
        BitMask returnMask;
        if ( source.getType() != NodeTypeEnums.ObjectTypeNode ) {
            returnMask = source.updateMask( declaredMask.clone().setAll( mask ) );
        } else { // else ObjectTypeNode
            returnMask = declaredMask.clone().setAll( mask );
        }
        inferredMask = inferredMask.setAll( returnMask );
        return returnMask;
    }

    @Override
    public void setPartitionId(BuildContext context, RuleBasePartitionId partitionId) {
        if (this.partitionId != null && this.partitionId != partitionId) {
            source.sink.changeSinkPartition( (ObjectSink)this, this.partitionId, partitionId, source.alphaNodeHashingThreshold );
        }
        this.partitionId = partitionId;
    }

    public final RuleBasePartitionId setSourcePartitionId(RuleBasePartitionId partitionId) {
        if (this.partitionId == null || this.partitionId == partitionId) {
            this.partitionId = partitionId;
            return partitionId;
        }
        this.partitionId = partitionId;
        if (source.getPartitionId() == RuleBasePartitionId.MAIN_PARTITION) {
            setPartitionIdWithSinks( partitionId );
        } else {
            source.setSourcePartitionId( partitionId );
        }
        return partitionId;
    }

    public final void setPartitionIdWithSinks( RuleBasePartitionId partitionId ) {
        this.partitionId = partitionId;
        for (ObjectSink sink : getObjectSinkPropagator().getSinks()) {
            ( (ObjectSinkNode) sink ).setPartitionIdWithSinks( partitionId );
        }
    }

    /**
     * Adds the <code>ObjectSink</code> so that it may receive
     * <code>FactHandleImpl</code> propagated from this
     * <code>ObjectSource</code>.
     *
     * @param objectSink
     *            The <code>ObjectSink</code> to receive propagated
     *            <code>FactHandleImpl</code>.
     */
    public void addObjectSink(final ObjectSink objectSink) {
        this.sink = this.sink.addObjectSink( objectSink, this.alphaNodeHashingThreshold );
    }

    /**
     * Removes the <code>ObjectSink</code>
     *
     * @param objectSink
     *            The <code>ObjectSink</code> to remove
     */
    public void removeObjectSink(final ObjectSink objectSink) {
        this.sink = this.sink.removeObjectSink( objectSink );
    }

    public abstract void updateSink(ObjectSink sink,
                                    PropagationContext context,
                                    InternalWorkingMemory workingMemory);

    public void networkUpdated(UpdateContext updateContext) {
        this.source.networkUpdated(updateContext);
    }

    public ObjectSinkPropagator getObjectSinkPropagator() {
        return this.sink;
    }

    public void setObjectSinkPropagator(ObjectSinkPropagator sink) {
        this.sink = sink;
    }

    public boolean isInUse() {
        return this.sink.size() > 0;
    }
    
    protected boolean doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        if ( !isInUse() && this instanceof ObjectSink ) {
            this.source.removeObjectSink((ObjectSink) this);
            return true;
        }
        return false;
    }

    protected ObjectTypeNode getObjectTypeNode() {
        ObjectSource source = this;
        while (source != null) {
            if (source.getType() ==  NodeTypeEnums.ObjectTypeNode) {
                return (ObjectTypeNode)source;
            }
            source = source.source;
        }
        return null;
    }

    public BitMask getDeclaredMask() {
        return EmptyBitMask.get();
    }
}
