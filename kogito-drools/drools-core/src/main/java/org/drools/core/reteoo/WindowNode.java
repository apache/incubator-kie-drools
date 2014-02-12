/*
 * Copyright 2011 JBoss Inc
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
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.BehaviorManager;
import org.drools.core.rule.ContextEntry;
import org.drools.core.rule.EntryPointId;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashSet.ObjectEntry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

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
                   MemoryFactory {

    private static final long serialVersionUID = 540l;
    private List<AlphaNodeFieldConstraint> constraints;
    private BehaviorManager                behavior;
    private EntryPointId                     entryPoint;
    private ObjectSinkNode                 previousRightTupleSinkNode;
    private ObjectSinkNode                 nextRightTupleSinkNode;
    private EntryPointNode                 epNode;
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
        epNode = (EntryPointNode) getObjectTypeNode().getParentObjectSource();
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        constraints = (List<AlphaNodeFieldConstraint>) in.readObject();
        behavior = (BehaviorManager) in.readObject();
        entryPoint = (EntryPointId) in.readObject();
        epNode = (EntryPointNode) getObjectTypeNode().getParentObjectSource();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(constraints);
        out.writeObject(behavior);
        out.writeObject(entryPoint);
        epNode = (EntryPointNode) getObjectTypeNode().getParentObjectSource();
    }

    public short getType() {
        return NodeTypeEnums.WindowNode;
    }

    @Override
    public void assertRightTuple(RightTuple rightTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        throw new UnsupportedOperationException();
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
     *
     * @return
     */
    public Behavior[] getBehaviors() {
        return behavior.getBehaviors();
    }

    public void attach(BuildContext context) {
        this.source.addObjectSink(this);
        if (context == null || context.getKnowledgeBase().getConfiguration().isPhreakEnabled()) {
            return;
        }

        for (InternalWorkingMemory workingMemory : context.getWorkingMemories()) {
            PropagationContextFactory pctxFactory = workingMemory.getKnowledgeBase().getConfiguration().getComponentFactory().getPropagationContextFactory();
            final PropagationContext propagationContext = pctxFactory.createPropagationContext(workingMemory.getNextPropagationIdCounter(), PropagationContext.RULE_ADDITION, null, null, null);
            this.source.updateSink(this,
                                   propagationContext,
                                   workingMemory);
        }
    }

    public void assertObject(final InternalFactHandle factHandle,
                             final PropagationContext pctx,
                             final InternalWorkingMemory workingMemory) {
        final WindowMemory memory = (WindowMemory) workingMemory.getNodeMemory(this);

        EventFactHandle evFh = ( EventFactHandle ) factHandle;
        // must guarantee single thread from now on
        memory.gate.lock();
        try {
            int index = 0;
            for (AlphaNodeFieldConstraint constraint : constraints) {
                if (!constraint.isAllowed(evFh, workingMemory, memory.context[index++])) {
                    return;
                }
            }

            RightTuple rightTuple = new RightTuple( evFh, this );
            rightTuple.setPropagationContext( pctx );

            InternalFactHandle clonedFh = evFh.cloneAndLink();  // this is cloned, as we need to separate the child RightTuple references
            rightTuple.setObject( clonedFh );

            // process the behavior
            if (!behavior.assertFact(memory, clonedFh, pctx, workingMemory)) {
                return;
            }

            this.sink.propagateAssertObject(clonedFh, pctx, workingMemory);
        } finally {
            memory.gate.unlock();
        }
    }

    @Override
    public void retractRightTuple(RightTuple rightTuple, PropagationContext pctx, InternalWorkingMemory wm) {
        final WindowMemory memory = (WindowMemory) wm.getNodeMemory(this);

        memory.gate.lock();
        try {
            behavior.retractFact(memory, rightTuple.getFactHandle(), pctx, wm);
        } finally {
            memory.gate.unlock();
        }

        InternalFactHandle clonedFh = ( InternalFactHandle ) rightTuple.getObject();
        ObjectTypeNode.doRetractObject(clonedFh, pctx, wm);
    }

    @Override
    public void modifyRightTuple(RightTuple rightTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        final WindowMemory memory = (WindowMemory) workingMemory.getNodeMemory(this);

        // must guarantee single thread from now on
        memory.gate.lock();

        EventFactHandle originalFactHandle = ( EventFactHandle ) rightTuple.getFactHandle();
        EventFactHandle cloneFactHandle  = ( EventFactHandle ) rightTuple.getObject();
        originalFactHandle.quickCloneUpdate( cloneFactHandle ); // make sure all fields are updated

        // behavior modify
        try {
            int index = 0;
            boolean isAllowed = true;
            for (AlphaNodeFieldConstraint constraint : constraints) {
                if (!constraint.isAllowed(cloneFactHandle,
                                          workingMemory,
                                          memory.context[index++])) {
                    isAllowed = false;
                    break;
                }
            }

            if  ( isAllowed ) {
                ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(cloneFactHandle.getFirstLeftTuple(), cloneFactHandle.getFirstRightTuple(), epNode );
                cloneFactHandle.clearLeftTuples();
                cloneFactHandle.clearRightTuples();

                this.sink.propagateModifyObject(cloneFactHandle,
                                                modifyPreviousTuples,
                                                context,
                                                workingMemory);
                modifyPreviousTuples.retractTuples(context, workingMemory);
            } else {
                ObjectTypeNode.doRetractObject(cloneFactHandle, context, workingMemory);
            }
        } finally {
            memory.gate.unlock();
        }
    }


    public void modifyObject(InternalFactHandle factHandle,
                             ModifyPreviousTuples modifyPreviousTuples,
                             PropagationContext context,
                             InternalWorkingMemory wm) {
        RightTuple rightTuple = modifyPreviousTuples.peekRightTuple();

        // if the peek is for a different OTN we assume that it is after the current one and then this is an assert
        while ( rightTuple != null &&
                rightTuple.getRightTupleSink().getRightInputOtnId().before( getRightInputOtnId() ) ) {
            modifyPreviousTuples.removeRightTuple();

            // we skipped this node, due to alpha hashing, so retract now
            rightTuple.setPropagationContext( context );
            retractRightTuple( rightTuple,
                               context,
                               wm );
            rightTuple = modifyPreviousTuples.peekRightTuple();
        }

        if ( rightTuple != null && rightTuple.getRightTupleSink().getRightInputOtnId().equals(getRightInputOtnId()) ) {
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
        final WindowMemory memory = (WindowMemory) workingMemory.getNodeMemory(this);

        // must guarantee single thread from now on
        memory.gate.lock();

        try {
            sink.byPassModifyToBetaNode(factHandle, modifyPreviousTuples, context, workingMemory);
        } finally {
            memory.gate.unlock();
        }
    }

    public void updateSink(final ObjectSink sink,
                           final PropagationContext context,
                           final InternalWorkingMemory wm) {
        final WindowMemory memory = (WindowMemory) wm.getNodeMemory(this);

        // even if the update Sink guarantees the kbase/ksession lock is acquired, we can't
        // have triggers being executed concurrently
        memory.gate.lock();

        try {
            final ObjectTypeNodeMemory omem = (ObjectTypeNodeMemory) wm.getNodeMemory( getObjectTypeNode());
            Iterator it = omem.getObjectHashSet().iterator();

            for (ObjectEntry entry = (ObjectEntry) it.next(); entry != null; entry = (ObjectEntry) it.next()) {
                InternalFactHandle fh = (InternalFactHandle) entry.getValue();
                sink.assertObject(fh,
                                  context,
                                  wm);
            }
        } finally {
            memory.gate.unlock();
        }
    }

    /**
     * Creates the WindowNode's memory.
     */
    public Memory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        WindowMemory memory = new WindowMemory();
        memory.context = new ContextEntry[this.constraints.size()];
        int index = 0;
        for (AlphaNodeFieldConstraint alpha : constraints) {
            memory.context[index++] = alpha.createContextEntry();
        }
        memory.behaviorContext = this.behavior.createBehaviorContext();
        memory.gate = new ReentrantLock();
        return memory;
    }

    public String toString() {
        return "[WindowNode(" + this.id + ") constraints=" + this.constraints + "]";
    }

    public int hashCode() {
        return this.source.hashCode() * 17 + ((this.constraints != null) ? this.constraints.hashCode() : 0);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        }

        if (object == null || !(object instanceof WindowNode)) {
            return false;
        }

        final WindowNode other = (WindowNode) object;

        return this.source.equals(other.source) && this.constraints.equals(other.constraints) && behavior.equals(other.behavior);
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
    public long calculateDeclaredMask(List<String> settableProperties) {
        throw new UnsupportedOperationException();
    }

    public ObjectTypeNode.Id getRightInputOtnId() {
        return rightInputOtnId;
    }

    public void setRightInputOtnId(ObjectTypeNode.Id rightInputOtnId) {
        this.rightInputOtnId = rightInputOtnId;
    }

    public static class WindowMemory implements Memory {
        public           ContextEntry[] context;
        public           Object         behaviorContext;
        public transient ReentrantLock  gate;

        public short getNodeType() {
            return NodeTypeEnums.WindowNode;
        }

        public SegmentMemory getSegmentMemory() {
            throw new UnsupportedOperationException();
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

        public Memory getNext() {
            throw new UnsupportedOperationException();
        }

        public void setNext(Memory next) {
            throw new UnsupportedOperationException();
        }

        public void nullPrevNext() {
            throw new UnsupportedOperationException();
        }
    }
}
