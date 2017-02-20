/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.core.reteoo.WindowNode.WindowMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.BehaviorManager;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.bitmask.BitMask;

/**
 * <code>WindowNodes</code> are nodes in the <code>Rete</code> network used
 * to manage windows. They support multiple types of windows, like
 * sliding windows, tumbling windows, etc.
 * <p/>
 * This class must act as a lock-gate for all working memory actions on it
 * and propagated down the network in this branch, as there can be concurrent
 * threads propagating events and expiring events working on this node at the
 * same time. It requires it to be thread safe.
 */
public class WindowNode extends ObjectSource
        implements ObjectSinkNode,
                   RightTupleSink,
                   MemoryFactory<WindowMemory> {

    private static final long serialVersionUID = 540l;
    private List<AlphaNodeFieldConstraint> constraints;
    protected BehaviorManager              behavior;
    private EntryPointId                   entryPoint;
    private ObjectSinkNode                 previousRightTupleSinkNode;
    private ObjectSinkNode                 nextRightTupleSinkNode;
    private transient ObjectTypeNode.Id rightInputOtnId = ObjectTypeNode.DEFAULT_ID;

    public WindowNode() {
    }

    /**
     * Construct a <code>WindowNode</code> with a unique id using the provided
     * list of <code>AlphaNodeFieldConstraint</code> and the given <code>ObjectSource</code>.
     *
     * @param id           Node's ID
     * @param constraints  Node's constraints
     * @param behaviors    list of behaviors for this window node
     * @param objectSource Node's object source
     */
    public WindowNode(final int id,
                      final List<AlphaNodeFieldConstraint> constraints,
                      final List<Behavior> behaviors,
                      final ObjectSource objectSource,
                      final BuildContext context) {
        super(id,
              context.getPartitionId(),
              context.getKnowledgeBase().getConfiguration().isMultithreadEvaluation(),
              objectSource,
              context.getKnowledgeBase().getConfiguration().getAlphaNodeHashingThreshold());
        // needs to be cloned as the list is managed externally
        this.constraints = new ArrayList<AlphaNodeFieldConstraint>(constraints);
        this.behavior = new BehaviorManager(behaviors);
        this.entryPoint = context.getCurrentEntryPoint();
        for ( Behavior b :  behaviors ) {
            if ( b instanceof SlidingTimeWindow ) {
                ((SlidingTimeWindow)b).setWindowNode( this );
            }
        }
        hashcode = calculateHashCode();
        initMemoryId( context );
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        constraints = (List<AlphaNodeFieldConstraint>) in.readObject();
        behavior = (BehaviorManager) in.readObject();
        entryPoint = (EntryPointId) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(constraints);
        out.writeObject(behavior);
        out.writeObject(entryPoint);
    }

    public short getType() {
        return NodeTypeEnums.WindowNode;
    }

    /**
     * Returns the <code>FieldConstraints</code>
     *
     * @return <code>FieldConstraints</code>
     */
    public List<AlphaNodeFieldConstraint> getConstraints() {
        return this.constraints;
    }

    /**
     * Returns the list of behaviors for this window node
     */
    public Behavior[] getBehaviors() {
        return behavior.getBehaviors();
    }

    public void attach(BuildContext context) {
        this.source.addObjectSink(this);
    }

    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext pctx,
                             final InternalWorkingMemory workingMemory) {
        EventFactHandle evFh = ( EventFactHandle ) factHandle;
        for (AlphaNodeFieldConstraint constraint : constraints) {
            if (!constraint.isAllowed(evFh, workingMemory)) {
                return;
            }
        }

        RightTuple rightTuple = new RightTupleImpl( evFh, this );
        rightTuple.setPropagationContext( pctx );

        InternalFactHandle clonedFh = evFh.cloneAndLink();  // this is cloned, as we need to separate the child RightTuple references
        rightTuple.setContextObject( clonedFh );

        // process the behavior
        final WindowMemory memory = workingMemory.getNodeMemory(this);
        if (!behavior.assertFact(memory.behaviorContext, clonedFh, pctx, workingMemory)) {
            return;
        }

        this.sink.propagateAssertObject(clonedFh, pctx, workingMemory);
    }

    @Override
    public void retractRightTuple(RightTuple rightTuple, PropagationContext pctx, InternalWorkingMemory wm) {
        if (isInUse()) {
            // This retraction could be the effect of an event expiration, but this node could be no
            // longer in use since an incremental update could have concurrently removed it
            WindowMemory memory = wm.getNodeMemory( this );
            behavior.retractFact( memory.behaviorContext, rightTuple.getFactHandle(), pctx, wm );
        }

        InternalFactHandle clonedFh = ( InternalFactHandle ) rightTuple.getContextObject();
        ObjectTypeNode.doRetractObject(clonedFh, pctx, wm);
    }

    @Override
    public void modifyRightTuple(RightTuple rightTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        EventFactHandle originalFactHandle = ( EventFactHandle ) rightTuple.getFactHandle();
        EventFactHandle cloneFactHandle  = ( EventFactHandle ) rightTuple.getContextObject();
        originalFactHandle.quickCloneUpdate( cloneFactHandle ); // make sure all fields are updated

        // behavior modify
        boolean isAllowed = true;
        for (AlphaNodeFieldConstraint constraint : constraints) {
            if (!constraint.isAllowed(cloneFactHandle,
                                      workingMemory)) {
                isAllowed = false;
                break;
            }
        }

        if  ( isAllowed ) {
            ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(cloneFactHandle.detachLinkedTuples() );

            this.sink.propagateModifyObject(cloneFactHandle,
                                            modifyPreviousTuples,
                                            context,
                                            workingMemory);
            modifyPreviousTuples.retractTuples(context, workingMemory);
        } else {
            ObjectTypeNode.doRetractObject(cloneFactHandle, context, workingMemory);
        }
    }

    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory wm) {
        RightTuple rightTuple = modifyPreviousTuples.peekRightTuple();

        // if the peek is for a different OTN we assume that it is after the current one and then this is an assert
        while ( rightTuple != null && rightTuple.getInputOtnId().before( getRightInputOtnId() ) ) {
            modifyPreviousTuples.removeRightTuple();

            // we skipped this node, due to alpha hashing, so retract now
            rightTuple.setPropagationContext( context );
            rightTuple.retractTuple( context, wm );
            rightTuple = modifyPreviousTuples.peekRightTuple();
        }

        if ( rightTuple != null && rightTuple.getInputOtnId().equals( getRightInputOtnId()) ) {
            modifyPreviousTuples.removeRightTuple();
            rightTuple.reAdd();
            modifyRightTuple( rightTuple, context, wm );
        } else {
            // RightTuple does not exist for this node, so create and continue as assert
            assertObject( factHandle, context, wm );
        }
    }

    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory) {
        sink.byPassModifyToBetaNode(factHandle, modifyPreviousTuples, context, workingMemory);
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory wm) {
        final ObjectTypeNodeMemory omem = wm.getNodeMemory( getObjectTypeNode());
        Iterator<InternalFactHandle> it = omem.iterator();

        while (it.hasNext()) {
            assertObject( it.next(), context, wm );
        }
    }

    /**
     * Creates the WindowNode's memory.
     */
    public WindowMemory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        WindowMemory memory = new WindowMemory();
        memory.behaviorContext = this.behavior.createBehaviorContext();
        return memory;
    }

    public String toString() {
        return "[WindowNode(" + this.id + ") constraints=" + this.constraints + "]";
    }

    private int calculateHashCode() {
        return this.source.hashCode() * 17 + ((this.constraints != null) ? this.constraints.hashCode() : 0);
    }

    @Override
    public boolean equals(final Object object) {
        return this == object ||
               ( internalEquals( object ) && this.source.thisNodeEquals(((WindowNode) object).source) );
    }

    @Override
    protected boolean internalEquals( Object object ) {
        if ( object == null || !(object instanceof WindowNode) || this.hashCode() != object.hashCode() ) {
            return false;
        }
        WindowNode other = (WindowNode) object;
        return this.constraints.equals(other.constraints) && behavior.equals(other.behavior);
    }

    /**
     * Returns the next node
     *
     * @return The next ObjectSinkNode
     */
    public ObjectSinkNode getNextObjectSinkNode() {
        return this.nextRightTupleSinkNode;
    }

    /**
     * Sets the next node
     *
     * @param next The next ObjectSinkNode
     */
    public void setNextObjectSinkNode(final ObjectSinkNode next) {
        this.nextRightTupleSinkNode = next;
    }

    /**
     * Returns the previous node
     *
     * @return The previous ObjectSinkNode
     */
    public ObjectSinkNode getPreviousObjectSinkNode() {
        return this.previousRightTupleSinkNode;
    }

    /**
     * Sets the previous node
     *
     * @param previous The previous ObjectSinkNode
     */
    public void setPreviousObjectSinkNode(final ObjectSinkNode previous) {
        this.previousRightTupleSinkNode = previous;
    }

    public EntryPointId getEntryPoint() {
        return entryPoint;
    }

    @Override
    public BitMask calculateDeclaredMask(List<String> settableProperties) {
        throw new UnsupportedOperationException();
    }

    public ObjectTypeNode.Id getRightInputOtnId() {
        return rightInputOtnId;
    }

    public void setRightInputOtnId(ObjectTypeNode.Id rightInputOtnId) {
        this.rightInputOtnId = rightInputOtnId;
    }

    public static class WindowMemory implements Memory {
        public Behavior.Context[] behaviorContext;

        public short getNodeType() {
            return NodeTypeEnums.WindowNode;
        }

        public SegmentMemory getSegmentMemory() {
            return null;
        }

        public void setSegmentMemory( SegmentMemory segmentMemory ) {
            throw new UnsupportedOperationException();
        }

        public Memory getPrevious() {
            throw new UnsupportedOperationException();
        }

        public void setPrevious( Memory previous ) {
            throw new UnsupportedOperationException();
        }

        public Memory getNext() {
            throw new UnsupportedOperationException();
        }

        public void setNext( Memory next ) {
            throw new UnsupportedOperationException();
        }

        public void nullPrevNext() {
            throw new UnsupportedOperationException();
        }

        public void reset() {
        }

        public Collection<EventFactHandle> getFactHandles() {
            List<EventFactHandle> eventFactHandles = new ArrayList<EventFactHandle>(  );
            for (Behavior.Context ctx : behaviorContext) {
                for (EventFactHandle efh : ctx.getFactHandles()) {
                    eventFactHandles.add(efh.getLinkedFactHandle());
                }
            }
            return eventFactHandles;
        }
    }
}
