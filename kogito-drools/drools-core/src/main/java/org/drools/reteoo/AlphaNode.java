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

package org.drools.reteoo;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;

import org.drools.RuleBaseConfiguration;
import org.drools.base.ClassObjectType;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.common.RuleBasePartitionId;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.ContextEntry;
import org.drools.rule.Pattern;
import org.drools.rule.TypeDeclaration;
import org.drools.rule.constraint.MvelConstraint;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.ObjectType;
import org.drools.spi.PropagationContext;

import static org.drools.core.util.BitMaskUtil.intersect;
import static org.drools.reteoo.PropertySpecificUtil.getSettableProperties;

/**
 * <code>AlphaNodes</code> are nodes in the <code>Rete</code> network used
 * to apply <code>FieldConstraint<.code>s on asserted fact
 * objects where the <code>FieldConstraint</code>s have no dependencies on any other of the facts in the current <code>Rule</code>.
 *
 *  @see AlphaNodeFieldConstraint
 */
public class AlphaNode extends ObjectSource
    implements
    ObjectSinkNode,
    NodeMemory {

    private static final long        serialVersionUID = 510l;

    /** The <code>FieldConstraint</code> */
    private AlphaNodeFieldConstraint constraint;

    private ObjectSinkNode           previousRightTupleSinkNode;
    private ObjectSinkNode           nextRightTupleSinkNode;

    private long declaredMask;
    private long inferredMask;

    public AlphaNode() {

    }

    /**
     * Construct an <code>AlphaNode</code> with a unique id using the provided
     * <code>FieldConstraint</code> and the given <code>ObjectSource</code>.
     * Set the boolean flag to true if the node is supposed to have local
     * memory, or false otherwise. Memory is optional for <code>AlphaNode</code>s
     * and is only of benefic when adding additional <code>Rule</code>s at runtime.
     *
     * @param id Node's ID
     * @param constraint Node's constraints
     * @param objectSource Node's object source
     */
    public AlphaNode(final int id,
                     final AlphaNodeFieldConstraint constraint,
                     final ObjectSource objectSource,
                     final BuildContext context) {
        super( id,
               context.getPartitionId(),
               context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
               objectSource,
               context.getRuleBase().getConfiguration().getAlphaNodeHashingThreshold() );
        this.constraint = constraint;

        initDeclaredMask(context);
    }

    public void initDeclaredMask(BuildContext context) {
        if ( context == null || context.getLastBuiltPatterns() == null ) {
            // only happens during unit tests
            declaredMask = Long.MAX_VALUE;
            return;
        }
        
        Pattern pattern = context.getLastBuiltPatterns()[0];
        ObjectType objectType = pattern.getObjectType();
        
        if ( !(objectType instanceof ClassObjectType)) {
            // Only ClassObjectType can use property specific
            declaredMask = Long.MAX_VALUE;
            return;
        }
        
        Class objectClass = ((ClassObjectType)objectType).getClassType();        
        TypeDeclaration typeDeclaration = context.getRuleBase().getTypeDeclaration(objectClass);
        if ( typeDeclaration == null || !typeDeclaration.isPropertySpecific() ) {
            // if property specific is not on, then accept all modification propagations
            declaredMask = Long.MAX_VALUE;             
        } else {
            List<String> settableProperties = getSettableProperties(context.getRuleBase(), objectClass);
            declaredMask = calculateDeclaredMask(settableProperties);
        }
    }
    
    public void resetInferredMask() {
        this.inferredMask = 0;
    }
    
    public long updateMask(long mask) {
        long returnMask;
        if (source instanceof AlphaNode) {
            returnMask = ((AlphaNode) source).updateMask( declaredMask | mask );
        } else { // else ObjectTypeNode
            returnMask = declaredMask | mask;
        }
        inferredMask = inferredMask | returnMask;
        return returnMask;
        
    }     

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        constraint = (AlphaNodeFieldConstraint) in.readObject();
        declaredMask = in.readLong();
        inferredMask = in.readLong();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(constraint);
        out.writeLong(declaredMask);
        out.writeLong(inferredMask);
    }

    /**
     * Retruns the <code>FieldConstraint</code>
     *
     * @return <code>FieldConstraint</code>
     */
    public AlphaNodeFieldConstraint getConstraint() {
        return this.constraint;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.drools.reteoo.BaseNode#attach()
     */
    public void attach() {
        this.source.addObjectSink( this );     
    }

    public void attach(final InternalWorkingMemory[] workingMemories) {
        attach();

        for ( int i = 0, length = workingMemories.length; i < length; i++ ) {
            final InternalWorkingMemory workingMemory = workingMemories[i];
            final PropagationContext propagationContext = new PropagationContextImpl( workingMemory.getNextPropagationIdCounter(),
                                                                                      PropagationContext.RULE_ADDITION,
                                                                                      null,
                                                                                      null,
                                                                                      null );
            this.source.updateSink( this,
                                    propagationContext,
                                    workingMemory );
        }
    }   
    
    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        final AlphaMemory memory = (AlphaMemory) workingMemory.getNodeMemory( this );
        if ( this.constraint.isAllowed( factHandle,
                                        workingMemory,
                                        memory.context ) ) {

            this.sink.propagateAssertObject( factHandle,
                                             context,
                                             workingMemory );
        }
    }

    public void modifyObject(final InternalFactHandle factHandle,
                             final ModifyPreviousTuples modifyPreviousTuples,
                             final PropagationContext context,
                             final InternalWorkingMemory workingMemory) {
        if ( intersect(context.getModificationMask(), inferredMask ) ) {

            final AlphaMemory memory = (AlphaMemory) workingMemory.getNodeMemory( this );
            if ( this.constraint.isAllowed( factHandle,
                    workingMemory,
                    memory.context ) ) {
                this.sink.propagateModifyObject( factHandle,
                        modifyPreviousTuples,
                        context,
                        workingMemory );
            }
        } else {
            byPassModifyToBetaNode(modifyPreviousTuples);
        }
    }

    private void byPassModifyToBetaNode (ModifyPreviousTuples modifyPreviousTuples) {
        for (ObjectSink objectSink : sink.getSinks()) {
            if (objectSink instanceof BetaNode) {
                RightTuple rightTuple = modifyPreviousTuples.removeRightTuple( (BetaNode) objectSink );
                if ( rightTuple != null ) rightTuple.reAdd();
            } else if (objectSink instanceof AlphaNode) {
                ((AlphaNode)objectSink).byPassModifyToBetaNode( modifyPreviousTuples );
            }
        }
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory workingMemory) {
        final AlphaMemory memory = (AlphaMemory) workingMemory.getNodeMemory( this );

        // get the objects from the parent
        ObjectSinkUpdateAdapter adapter = new ObjectSinkUpdateAdapter( sink,
                                                                       this.constraint,
                                                                       memory.context );
        this.source.updateSink( adapter,
                                context,
                                workingMemory );
    }

    /**
     * Creates a HashSet for the AlphaNode's memory.
     */
    public Object createMemory(final RuleBaseConfiguration config) {
        AlphaMemory memory = new AlphaMemory();
        memory.context = this.constraint.createContextEntry();
        return memory;
    }

    public String toString() {
        return "[AlphaNode(" + this.id + ") constraint=" + this.constraint + "]";
    }

    public int hashCode() {
        return this.source.hashCode() * 17 + ((this.constraint != null) ? this.constraint.hashCode() : 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if ( this == object ) {
            return true;
        }

        if ( object == null || !(object instanceof AlphaNode) ) {
            return false;
        }

        final AlphaNode other = (AlphaNode) object;

        return this.source.equals( other.source ) && this.constraint.equals( other.constraint );
    }

    /**
     * Returns the next node
     * @return
     *      The next ObjectSinkNode
     */
    public ObjectSinkNode getNextObjectSinkNode() {
        return this.nextRightTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next ObjectSinkNode
     */
    public void setNextObjectSinkNode(final ObjectSinkNode next) {
        this.nextRightTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous ObjectSinkNode
     */
    public ObjectSinkNode getPreviousObjectSinkNode() {
        return this.previousRightTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous ObjectSinkNode
     */
    public void setPreviousObjectSinkNode(final ObjectSinkNode previous) {
        this.previousRightTupleSinkNode = previous;
    }

    public static class AlphaMemory
        implements
        Externalizable {
        private static final long serialVersionUID = 510l;

        public ContextEntry       context;

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            context = (ContextEntry) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( context );
        }
    }

    /**
     * Used with the updateSink method, so that the parent ObjectSource
     * can  update the  TupleSink
     */
    private static class ObjectSinkUpdateAdapter
        implements
        ObjectSink {
        private final ObjectSink               sink;
        private final AlphaNodeFieldConstraint constraint;
        private final ContextEntry             contextEntry;

        public ObjectSinkUpdateAdapter(final ObjectSink sink,
                                       final AlphaNodeFieldConstraint constraint,
                                       final ContextEntry contextEntry) {
            this.sink = sink;
            this.constraint = constraint;
            this.contextEntry = contextEntry;
        }

        public void assertObject(final InternalFactHandle handle,
                                 final PropagationContext propagationContext,
                                 final InternalWorkingMemory workingMemory) {

            if ( this.constraint.isAllowed( handle,
                                            workingMemory,
                                            this.contextEntry ) ) {
                this.sink.assertObject( handle,
                                        propagationContext,
                                        workingMemory );
            }
        }

        public int getId() {
            return 0;
        }

        public RuleBasePartitionId getPartitionId() {
            return this.sink.getPartitionId();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            // this is a short living adapter class, so no need for serialization
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            // this is a short living adapter class, so no need for serialization
        }

        public void modifyObject(final InternalFactHandle factHandle,
                                 final ModifyPreviousTuples modifyPreviousTuples,
                                 final PropagationContext context,
                                 final InternalWorkingMemory workingMemory) {
            throw new UnsupportedOperationException( "This method should NEVER EVER be called" );
        }

    }

    private long calculateDeclaredMask(List<String> settableProperties) {
        if (settableProperties == null || !(constraint instanceof MvelConstraint)) {
            return Long.MAX_VALUE;
        }
        return ((MvelConstraint)constraint).getListenedPropertyMask(settableProperties);
    }

    @Override
    public long getDeclaredMask() {
        return declaredMask;
    }  

    public long getInferredMask() {
        return inferredMask;
    }

    @Override
    public void addObjectSink(final ObjectSink objectSink) {
        super.addObjectSink(objectSink);
    }
}
