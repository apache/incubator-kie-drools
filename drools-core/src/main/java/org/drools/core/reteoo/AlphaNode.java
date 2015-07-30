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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.base.evaluators.IsAEvaluatorDefinition;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.RuleBasePartitionId;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.constraint.EvaluatorConstraint;
import org.drools.core.rule.constraint.MvelConstraint;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleComponent;
import org.drools.core.util.bitmask.AllSetBitMask;
import org.drools.core.util.bitmask.BitMask;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.rule.Operator;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.Map;

import static org.drools.core.reteoo.PropertySpecificUtil.allSetButTraitBitMask;

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
    MemoryFactory {

    private static final long        serialVersionUID = 510l;

    /** The <code>FieldConstraint</code> */
    private AlphaNodeFieldConstraint constraint;

    private ObjectSinkNode           previousRightTupleSinkNode;
    private ObjectSinkNode           nextRightTupleSinkNode;

    private int                      hashcode;

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
               context.getKnowledgeBase().getConfiguration().isMultithreadEvaluation(),
               objectSource,
               context.getKnowledgeBase().getConfiguration().getAlphaNodeHashingThreshold() );

        this.constraint = constraint.cloneIfInUse();
        if (this.constraint instanceof MvelConstraint) {
            ((MvelConstraint) this.constraint).registerEvaluationContext(context);
        }

        initDeclaredMask(context);
        hashcode = calculateHashCode();
    }


    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        constraint = (AlphaNodeFieldConstraint) in.readObject();
        declaredMask = (BitMask) in.readObject();
        inferredMask = (BitMask) in.readObject();
        hashcode = in.readInt();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(constraint);
        out.writeObject(declaredMask);
        out.writeObject(inferredMask);
        out.writeInt(hashcode);
    }

    /**
     * Retruns the <code>FieldConstraint</code>
     *
     * @return <code>FieldConstraint</code>
     */
    public AlphaNodeFieldConstraint getConstraint() {
        return this.constraint;
    }
    
    public short getType() {
        return NodeTypeEnums.AlphaNode;
    }

    public void attach(BuildContext context) {
        this.source.addObjectSink( this );
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
        if ( context.getModificationMask().intersects( inferredMask ) ) {

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
            byPassModifyToBetaNode(factHandle, modifyPreviousTuples,context,workingMemory);
        }
    }

    public  void byPassModifyToBetaNode (final InternalFactHandle factHandle,
                                         final ModifyPreviousTuples modifyPreviousTuples,
                                         final PropagationContext context,
                                         final InternalWorkingMemory workingMemory) {
        sink.byPassModifyToBetaNode( factHandle, modifyPreviousTuples, context, workingMemory );
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
    public Memory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        AlphaMemory memory = new AlphaMemory();
        memory.context = this.constraint.createContextEntry();
        return memory;
    }

    public String toString() {
        return "[AlphaNode(" + this.id + ") constraint=" + this.constraint + "]";
    }

    public int hashCode() {
        return hashcode;
    }

    public int calculateHashCode() {
        return (this.source != null ? this.source.hashCode() : 0) * 37 + (this.constraint != null ? this.constraint.hashCode() : 0) * 31;
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

        if ( !(object instanceof AlphaNode) || hashCode() != object.hashCode() ) {
            return false;
        }

        final AlphaNode other = (AlphaNode) object;

        return this.source.equals( other.source ) &&
               constraint instanceof MvelConstraint ?
               ((MvelConstraint)constraint).equals( other.constraint, getKnowledgeBase() ) :
               constraint.equals( other.constraint );
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
        Memory {
        private static final long serialVersionUID = 510l;

        public ContextEntry       context;
        
        public short getNodeType() {
            return NodeTypeEnums.AlphaNode;
        }

        public SegmentMemory getSegmentMemory() {
            return null;
        }

        public void setSegmentMemory(SegmentMemory segmentMemory) {
            throw new UnsupportedOperationException();
        }

        public Memory getPrevious() {
            throw new UnsupportedOperationException();
        }

        public void setPrevious(Memory previous) {
            throw new UnsupportedOperationException();
        }

        public void setNext(Memory next) {
            throw new UnsupportedOperationException();
        }

        public Memory getNext() {
            throw new UnsupportedOperationException();
        }

        public void nullPrevNext() {
            throw new UnsupportedOperationException();
        }

        public void reset() { }
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

        public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                           ModifyPreviousTuples modifyPreviousTuples,
                                           PropagationContext context,
                                           InternalWorkingMemory workingMemory) {
        }

        public short getType() {
            return NodeTypeEnums.AlphaNode;
        }

        public Map<Rule, RuleComponent> getAssociations() {
            return sink.getAssociations();
        }

    }

    public BitMask calculateDeclaredMask(List<String> settableProperties) {
        boolean typeBit = false;
        if ( constraint instanceof EvaluatorConstraint && ( (EvaluatorConstraint) constraint ).isSelf() ) {
            Operator op = ((EvaluatorConstraint) constraint).getEvaluator().getOperator();
            if ( op == IsAEvaluatorDefinition.ISA || op == IsAEvaluatorDefinition.NOT_ISA ) {
                typeBit = true;
            }
        }
        if (settableProperties == null || !(constraint instanceof MvelConstraint)) {
            return typeBit ? AllSetBitMask.get() : allSetButTraitBitMask();
        }
        BitMask mask = ((MvelConstraint)constraint).getListenedPropertyMask(settableProperties);
        return typeBit ? mask.set(PropertySpecificUtil.TRAITABLE_BIT) : mask;
    }

    @Override
    public BitMask getDeclaredMask() {
        return declaredMask;
    }  

    public BitMask getInferredMask() {
        return inferredMask;
    }

    @Override
    public void addObjectSink(final ObjectSink objectSink) {
        super.addObjectSink(objectSink);
    }
}
