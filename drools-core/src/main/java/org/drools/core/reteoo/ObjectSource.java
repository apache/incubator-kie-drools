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

package org.drools.core.reteoo;

import org.drools.core.base.ClassObjectType;
import org.drools.core.common.BaseNode;
import org.drools.core.common.DefaultFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Pattern;
import org.drools.core.rule.TypeDeclaration;
import org.drools.core.spi.ObjectType;
import org.drools.core.spi.PropagationContext;

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

    private int                    alphaNodeHashingThreshold;


    protected long declaredMask;
    protected long inferredMask;
    
    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public ObjectSource() {

    }

    /**
     * Single parameter constructor that specifies the unique id of the node.
     *
     * @param id
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
     *
     * @param id
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
    
    public void initDeclaredMask(BuildContext context) {
        if ( context == null || context.getLastBuiltPatterns() == null ) {
            // only happens during unit tests
            declaredMask = -1L;
            return;
        }
        
        Pattern pattern = context.getLastBuiltPatterns()[0];
        ObjectType objectType = pattern.getObjectType();
        
        if ( !(objectType instanceof ClassObjectType)) {
            // Only ClassObjectType can use property specific
            declaredMask = -1L;
            return;
        }
        
        Class objectClass = ((ClassObjectType)objectType).getClassType();        
        TypeDeclaration typeDeclaration = context.getKnowledgeBase().getTypeDeclaration(objectClass);
        if ( typeDeclaration == null || !typeDeclaration.isPropertyReactive() ) {
            // if property specific is not on, then accept all modification propagations
            declaredMask = -1L;
        } else {
            List<String> settableProperties = getSettableProperties(context.getKnowledgeBase(), objectClass);
            declaredMask = calculateDeclaredMask(settableProperties);
        }
    }
    
    public abstract long calculateDeclaredMask(List<String> settableProperties);
    
    public void resetInferredMask() {
        this.inferredMask = 0;
    }
    
    public long updateMask(long mask) {
        long returnMask;
        if ( source.getType() != NodeTypeEnums.ObjectTypeNode ) {
            returnMask = source.updateMask( declaredMask | mask );
        } else { // else ObjectTypeNode
            returnMask = declaredMask | mask;
        }
        inferredMask = inferredMask | returnMask;
        return returnMask;
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
        if ( this.sink instanceof EmptyObjectSinkAdapter ) {
            this.sink = new SingleObjectSinkAdapter( this.getPartitionId(), objectSink );
        } else if ( this.sink instanceof SingleObjectSinkAdapter ) {
            final CompositeObjectSinkAdapter sinkAdapter;
            sinkAdapter = new CompositeObjectSinkAdapter( this.getPartitionId(), this.alphaNodeHashingThreshold );
            sinkAdapter.addObjectSink( this.sink.getSinks()[0] );
            sinkAdapter.addObjectSink( objectSink );
            this.sink = sinkAdapter;
        } else {
            ((CompositeObjectSinkAdapter) this.sink).addObjectSink( objectSink );
        }
    }

    /**
     * Removes the <code>ObjectSink</code>
     *
     * @param objectSink
     *            The <code>ObjectSink</code> to remove
     */
    public void removeObjectSink(final ObjectSink objectSink) {
        if ( this.sink instanceof EmptyObjectSinkAdapter ) {
            throw new IllegalArgumentException( "Cannot remove a sink, when the list of sinks is null" );
        }

        if ( this.sink instanceof SingleObjectSinkAdapter ) {
            this.sink = EmptyObjectSinkAdapter.getInstance();
        } else {
            final CompositeObjectSinkAdapter sinkAdapter = (CompositeObjectSinkAdapter) this.sink;
            sinkAdapter.removeObjectSink( objectSink );
            if ( sinkAdapter.size() == 1 ) {
                this.sink = new SingleObjectSinkAdapter( this.getPartitionId(), sinkAdapter.getSinks()[0] );
            }
        }
    }

    public abstract void updateSink(ObjectSink sink,
                                    PropagationContext context,
                                    InternalWorkingMemory workingMemory);

    public void networkUpdated(UpdateContext updateContext) {
        this.source.networkUpdated(updateContext);
    }

    public ObjectSinkPropagator getSinkPropagator() {
        return this.sink;
    }

    public boolean isInUse() {
        return this.sink.size() > 0;
    }
    
    protected void doRemove(final RuleRemovalContext context,
                            final ReteooBuilder builder,
                            final InternalWorkingMemory[] workingMemories) {
        if ( !context.getKnowledgeBase().getConfiguration().isPhreakEnabled()  && !this.isInUse() && this instanceof MemoryFactory ) {
            for( InternalWorkingMemory workingMemory : workingMemories ) {
                workingMemory.clearNodeMemory( (MemoryFactory) this );
            }
        }
        if ( !isInUse() && this instanceof ObjectSink ) {
            this.source.removeObjectSink((ObjectSink) this);
        }
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

    public long getDeclaredMask() {
        return 0L;
    }
}
