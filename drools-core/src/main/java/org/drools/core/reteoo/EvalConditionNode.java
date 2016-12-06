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

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.UpdateContext;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.EvalCondition;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.RuleComponent;
import org.drools.core.util.AbstractBaseLinkedListNode;
import org.kie.api.definition.rule.Rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

public class EvalConditionNode extends LeftTupleSource
    implements
    LeftTupleSinkNode,
    MemoryFactory<EvalConditionNode.EvalMemory> {

    private static final long serialVersionUID = 510l;

    /** The semantic <code>Test</code>. */
    protected EvalCondition     condition;

    protected boolean         tupleMemoryEnabled;

    private LeftTupleSinkNode previousTupleSinkNode;
    private LeftTupleSinkNode nextTupleSinkNode;

    private Map<Rule, RuleComponent> componentsMap = new HashMap<Rule, RuleComponent>();

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------
    public EvalConditionNode() {

    }

    public EvalConditionNode(final int id,
                             final LeftTupleSource tupleSource,
                             final EvalCondition eval,
                             final BuildContext context) {
        super(id, context);
        this.condition = eval;
        setLeftTupleSource(tupleSource);
        this.tupleMemoryEnabled = context.isTupleMemoryEnabled();

        initMasks(context, tupleSource);

        hashcode = calculateHashCode();
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        super.readExternal( in );
        condition = (EvalCondition) in.readObject();
        tupleMemoryEnabled = in.readBoolean();
        componentsMap = (Map<Rule, RuleComponent>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( condition );
        out.writeBoolean( tupleMemoryEnabled );
        out.writeObject( componentsMap );
    }

    public void attach( BuildContext context ) {
        this.leftInput.addTupleSink( this, context );
    }

    public void networkUpdated(UpdateContext updateContext) {
        this.leftInput.networkUpdated(updateContext);
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the <code>Test</code> associated with this node.
     *
     * @return The <code>Test</code>.
     */
    public EvalCondition getCondition() {
        return this.condition;
    }
    
    /**
     * Produce a debug string.
     *
     * @return The debug string.
     */
    public String toString() {
        return "[EvalConditionNode(" + this.id + ")]: cond=" + this.condition + "]";
    }

    private int calculateHashCode() {
        return this.leftInput.hashCode() ^ this.condition.hashCode();
    }

    @Override
    public boolean equals(final Object object) {
        return this == object ||
               ( internalEquals( object ) && this.leftInput.thisNodeEquals( ((EvalConditionNode)object).leftInput ) );
    }

    @Override
    protected boolean internalEquals( Object object ) {
        if ( object == null || !(object instanceof EvalConditionNode) || this.hashCode() != object.hashCode() ) {
            return false;
        }

        return this.condition.equals( ((EvalConditionNode)object).condition );
    }

    public EvalMemory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        return new EvalMemory( this.condition.createContext() );
    }

    @Override
    public LeftTuple createPeer(LeftTuple original) {
        EvalNodeLeftTuple peer = new EvalNodeLeftTuple();
        peer.initPeer( (BaseLeftTuple) original, this );
        original.setPeer( peer );
        return peer;
    }

    public boolean isLeftTupleMemoryEnabled() {
        return tupleMemoryEnabled;
    }

    public void setLeftTupleMemoryEnabled(boolean tupleMemoryEnabled) {
        this.tupleMemoryEnabled = tupleMemoryEnabled;
    }

    /**
     * Returns the next node
     * @return
     *      The next TupleSinkNode
     */
    public LeftTupleSinkNode getNextLeftTupleSinkNode() {
        return this.nextTupleSinkNode;
    }

    /**
     * Sets the next node
     * @param next
     *      The next TupleSinkNode
     */
    public void setNextLeftTupleSinkNode(final LeftTupleSinkNode next) {
        this.nextTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     * @return
     *      The previous TupleSinkNode
     */
    public LeftTupleSinkNode getPreviousLeftTupleSinkNode() {
        return this.previousTupleSinkNode;
    }

    /**
     * Sets the previous node
     * @param previous
     *      The previous TupleSinkNode
     */
    public void setPreviousLeftTupleSinkNode(final LeftTupleSinkNode previous) {
        this.previousTupleSinkNode = previous;
    }

    public short getType() {
        return NodeTypeEnums.EvalConditionNode;
    }



    public LeftTuple createLeftTuple(InternalFactHandle factHandle,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new EvalNodeLeftTuple(factHandle, sink, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(final InternalFactHandle factHandle,
                                     final LeftTuple leftTuple,
                                     final Sink sink) {
        return new EvalNodeLeftTuple(factHandle,leftTuple, sink );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     Sink sink,
                                     PropagationContext pctx, boolean leftTupleMemoryEnabled) {
        return new EvalNodeLeftTuple(leftTuple,sink, pctx, leftTupleMemoryEnabled );
    }

    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     Sink sink) {
        return new EvalNodeLeftTuple(leftTuple, rightTuple, sink );
    }   
    
    public LeftTuple createLeftTuple(LeftTuple leftTuple,
                                     RightTuple rightTuple,
                                     LeftTuple currentLeftChild,
                                     LeftTuple currentRightChild,
                                     Sink sink,
                                     boolean leftTupleMemoryEnabled) {
        return new EvalNodeLeftTuple(leftTuple, rightTuple, currentLeftChild, currentRightChild, sink, leftTupleMemoryEnabled );        
    }        
    
    public static class EvalMemory extends AbstractBaseLinkedListNode<Memory>
        implements
        Externalizable,
        Memory {

        private static final long serialVersionUID = 510l;

        public Object             context;
        
        private SegmentMemory     memory;

        public EvalMemory() {

        }

        public EvalMemory(final Object context) {
            this.context = context;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            context = in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( context );
        }

        public short getNodeType() {
            return NodeTypeEnums.EvalConditionNode;
        }

        public void setSegmentMemory(SegmentMemory smem) {
            this.memory = smem;
        }
        
        public SegmentMemory getSegmentMemory() {
            return this.memory;
        }

        public void reset() { }
    }

    protected ObjectTypeNode getObjectTypeNode() {
        return leftInput.getObjectTypeNode();
    }

    @Override
    public void assertLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void retractLeftTuple(LeftTuple leftTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
    }

    protected boolean doRemove(final RuleRemovalContext context,
                               final ReteooBuilder builder,
                               final InternalWorkingMemory[] workingMemories) {
        if ( !this.isInUse() ) {
            getLeftTupleSource().removeTupleSink( this );
            return true;
        } else {
            // need to re-wire eval expression to the same one from another rule
            // that is sharing this node
            this.condition = (EvalCondition) componentsMap.values().iterator().next();
            return false;
        }
    }

    @Override
    public void addAssociation( BuildContext context, Rule rule ) {
        super.addAssociation(context, rule);
        componentsMap.put(rule, context.peekRuleComponent());
    }

    @Override
    public boolean removeAssociation( Rule rule ) {
        boolean result = super.removeAssociation(rule);
        if (!isAssociatedWith( rule )) {
            componentsMap.remove( rule );
        }
        return result;
    }
}
