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

package org.drools.reteoo.nodes;

import org.drools.core.RuleBaseConfiguration;
import org.drools.core.common.EventFactHandle;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.Memory;
import org.drools.core.common.MemoryFactory;
import org.drools.core.common.PropagationContextFactory;
import org.drools.core.reteoo.ModifyPreviousTuples;
import org.drools.core.reteoo.ObjectSink;
import org.drools.core.reteoo.ObjectSinkNode;
import org.drools.core.reteoo.ObjectSource;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ObjectTypeNode.ObjectTypeNodeMemory;
import org.drools.core.reteoo.RightTuple;
import org.drools.core.reteoo.RightTupleImpl;
import org.drools.core.reteoo.RightTupleSink;
import org.drools.core.reteoo.WindowNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Behavior;
import org.drools.core.rule.SlidingLengthWindow;
import org.drools.core.rule.SlidingTimeWindow;
import org.drools.core.spi.AlphaNodeFieldConstraint;
import org.drools.core.spi.PropagationContext;

import java.util.ArrayList;
import java.util.Iterator;
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
public class ReteWindowNode extends WindowNode
        implements ObjectSinkNode,
                   RightTupleSink,
                   MemoryFactory {

    public ReteWindowNode() {
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
    public ReteWindowNode(final int id,
                          final List<AlphaNodeFieldConstraint> constraints,
                          final List<Behavior> behaviors,
                          final ObjectSource objectSource,
                          final BuildContext context) {
        super(id, constraints, convertBehaviors(behaviors), objectSource, context);
    }

    private static List<Behavior> convertBehaviors(List<Behavior> behaviors) {
        List<Behavior> converted = new ArrayList<Behavior>();
        for (Behavior b : behaviors) {
            if (b instanceof SlidingLengthWindow) {
                converted.add(new ReteSlidingLengthWindow((int) ((SlidingLengthWindow) b).getSize()));
            } else if (b instanceof SlidingTimeWindow) {
                converted.add(new ReteSlidingTimeWindow((int) ((SlidingTimeWindow) b).getSize()));
            } else {
                converted.add(b);
            }
        }
        return converted;
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
        final ReteWindowMemory memory = (ReteWindowMemory) workingMemory.getNodeMemory(this);

        EventFactHandle evFh = (EventFactHandle) factHandle;
        // must guarantee single thread from now on
        memory.gate.lock();
        try {
            int index = 0;
            for (AlphaNodeFieldConstraint constraint : getConstraints()) {
                if (!constraint.isAllowed(evFh, workingMemory)) {
                    return;
                }
            }

            RightTuple rightTuple = new RightTupleImpl(evFh, this);
            rightTuple.setPropagationContext(pctx);

            InternalFactHandle clonedFh = evFh.cloneAndLink();  // this is cloned, as we need to separate the child RightTuple references
            rightTuple.setContextObject( clonedFh);

            // process the behavior
            if (!behavior.assertFact(memory.behaviorContext, clonedFh, pctx, workingMemory)) {
                return;
            }

            this.sink.propagateAssertObject(clonedFh, pctx, workingMemory);
        } finally {
            memory.gate.unlock();
        }
    }

    @Override
    public void retractRightTuple(RightTuple rightTuple, PropagationContext pctx, InternalWorkingMemory wm) {
        final ReteWindowMemory memory = (ReteWindowMemory) wm.getNodeMemory(this);

        memory.gate.lock();
        try {
            behavior.retractFact(memory.behaviorContext, rightTuple.getFactHandle(), pctx, wm);
        } finally {
            memory.gate.unlock();
        }

        InternalFactHandle clonedFh = (InternalFactHandle) rightTuple.getContextObject();
        ObjectTypeNode.doRetractObject(clonedFh, pctx, wm);
    }

    @Override
    public void modifyRightTuple(RightTuple rightTuple, PropagationContext context, InternalWorkingMemory workingMemory) {
        final ReteWindowMemory memory = (ReteWindowMemory) workingMemory.getNodeMemory(this);

        // must guarantee single thread from now on
        memory.gate.lock();

        EventFactHandle originalFactHandle = (EventFactHandle) rightTuple.getFactHandle();
        EventFactHandle cloneFactHandle = (EventFactHandle) rightTuple.getContextObject();
        originalFactHandle.quickCloneUpdate(cloneFactHandle); // make sure all fields are updated

        // behavior modify
        try {
            int index = 0;
            boolean isAllowed = true;
            for (AlphaNodeFieldConstraint constraint : getConstraints()) {
                if (!constraint.isAllowed(cloneFactHandle,
                                          workingMemory)) {
                    isAllowed = false;
                    break;
                }
            }

            if (isAllowed) {
                ModifyPreviousTuples modifyPreviousTuples = new ModifyPreviousTuples(cloneFactHandle.getFirstLeftTuple(), cloneFactHandle.getFirstRightTuple(), epNode);
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

    public void byPassModifyToBetaNode(InternalFactHandle factHandle,
                                       ModifyPreviousTuples modifyPreviousTuples,
                                       PropagationContext context,
                                       InternalWorkingMemory workingMemory) {
        final ReteWindowMemory memory = (ReteWindowMemory) workingMemory.getNodeMemory(this);

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
        final ReteWindowMemory memory = (ReteWindowMemory) wm.getNodeMemory(this);

        // even if the update Sink guarantees the kbase/ksession lock is acquired, we can't
        // have triggers being executed concurrently
        memory.gate.lock();

        try {
            final ObjectTypeNodeMemory omem = (ObjectTypeNodeMemory) wm.getNodeMemory(getObjectTypeNode());
            Iterator<InternalFactHandle> it = omem.iterator();

            while (it.hasNext()) {
                sink.assertObject(it.next(),
                                  context,
                                  wm);
            }
        } finally {
            memory.gate.unlock();
        }
    }

    @Override
    public Memory createMemory(final RuleBaseConfiguration config, InternalWorkingMemory wm) {
        ReteWindowMemory memory = new ReteWindowMemory();
        memory.behaviorContext = this.behavior.createBehaviorContext();
        memory.gate = new ReentrantLock();
        return memory;
    }

    public static class ReteWindowMemory extends WindowMemory {
        public transient ReentrantLock gate;
    }
}
