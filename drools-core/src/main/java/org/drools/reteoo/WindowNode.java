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

package org.drools.reteoo;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.drools.RuleBaseConfiguration;
import org.drools.common.EventFactHandle;
import org.drools.common.InternalFactHandle;
import org.drools.common.InternalWorkingMemory;
import org.drools.common.Memory;
import org.drools.common.NodeMemory;
import org.drools.common.PropagationContextImpl;
import org.drools.core.util.Iterator;
import org.drools.core.util.ObjectHashMap;
import org.drools.reteoo.builder.BuildContext;
import org.drools.rule.Behavior;
import org.drools.rule.BehaviorManager;
import org.drools.rule.ContextEntry;
import org.drools.rule.EntryPoint;
import org.drools.rule.SlidingTimeWindow;
import org.drools.spi.AlphaNodeFieldConstraint;
import org.drools.spi.PropagationContext;

/**
 * <code>WindowNodes</code> are nodes in the <code>Rete</code> network used
 * to manage windows. They support multiple types of windows, like 
 * sliding windows, tumbling windows, etc.
 * 
 * This class must act as a lock-gate for all working memory actions on it
 * and propagated down the network in this branch, as there can be concurrent
 * threads propagating events and expiring events working on this node at the
 * same time. It requires it to be thread safe.
 *
 */
public class WindowNode extends ObjectSource
        implements ObjectSinkNode,
        NodeMemory {

    private static final long              serialVersionUID = 540l;

    private List<AlphaNodeFieldConstraint> constraints;
    private BehaviorManager                behavior;
    private EntryPoint                     entryPoint;

    private ObjectSinkNode                 previousRightTupleSinkNode;
    private ObjectSinkNode                 nextRightTupleSinkNode;

    public WindowNode() {
    }

    /**
     * Construct a <code>WindowNode</code> with a unique id using the provided
     * list of <code>AlphaNodeFieldConstraint</code> and the given <code>ObjectSource</code>.
     * 
     * @param id Node's ID
     * @param constraints Node's constraints
     * @param behaviors list of behaviors for this window node
     * @param objectSource Node's object source
     */
    public WindowNode(final int id,
            final List<AlphaNodeFieldConstraint> constraints,
            final List<Behavior> behaviors,
            final ObjectSource objectSource,
            final BuildContext context) {
        super(id,
                context.getPartitionId(),
                context.getRuleBase().getConfiguration().isMultithreadEvaluation(),
                objectSource,
                context.getRuleBase().getConfiguration().getAlphaNodeHashingThreshold());
        // needs to be cloned as the list is managed externally
        this.constraints = new ArrayList<AlphaNodeFieldConstraint>(constraints);
        this.behavior = new BehaviorManager(behaviors);
        this.entryPoint = context.getCurrentEntryPoint();
        for ( Behavior b : behaviors ) {
            if ( b instanceof SlidingTimeWindow ) {
                ((SlidingTimeWindow)b).setWindowNode( this );
            }
        }

    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException,
            ClassNotFoundException {
        super.readExternal(in);
        constraints = (List<AlphaNodeFieldConstraint>) in.readObject();
        behavior = (BehaviorManager) in.readObject();
        entryPoint = (EntryPoint) in.readObject();
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
     * @return
     */
    public Behavior[] getBehaviors() {
        return behavior.getBehaviors();
    }

    public void attach(BuildContext context) {
        this.source.addObjectSink(this);
    }

    public void updateSinkOnAttach( BuildContext context, PropagationContext propagationContext, InternalWorkingMemory workingMemory ) {
        // do nothing, this node's updateSink will be called from the beta network
    }


    public void assertObject(final InternalFactHandle factHandle,
            final PropagationContext context,
            final InternalWorkingMemory workingMemory) {

        final WindowMemory memory = (WindowMemory) workingMemory.getNodeMemory(this);
        
        // must guarantee single thread from now on
        memory.gate.lock();
        try {
            int index = 0;
            for (AlphaNodeFieldConstraint constraint : constraints) {
                if (!constraint.isAllowed(factHandle,
                        workingMemory,
                        memory.context[index++])) {
                    return;
                }
            }
            // process the behavior
            if (!behavior.assertFact(memory,
                    factHandle,
                    workingMemory)) {
                return;
            }

            // propagate
            WindowTupleList list = new WindowTupleList((EventFactHandle) factHandle, this);
            context.setActiveWindowTupleList(list);
            memory.events.put(factHandle,
                              list);
            this.sink.propagateAssertObject(factHandle,
                                            context,
                                            workingMemory);
            // execute queued assertions before nullifying the window tuple list
            workingMemory.executeQueuedActions();
            context.setActiveWindowTupleList(null);
        } finally {
            memory.gate.unlock();
        }
    }

    public void modifyObject(final InternalFactHandle factHandle,
            final ModifyPreviousTuples modifyPreviousTuples,
            final PropagationContext context,
            final InternalWorkingMemory workingMemory) {
        final WindowMemory memory = (WindowMemory) workingMemory.getNodeMemory(this);

        // must guarantee single thread from now on
        memory.gate.lock();

        // behavior modify
        try {
            int index = 0;
            boolean isAllowed = true;
            for (AlphaNodeFieldConstraint constraint : constraints) {
                if (!constraint.isAllowed(factHandle,
                        workingMemory,
                        memory.context[index++])) {
                    isAllowed = false;
                    break;
                }
            }
            // propagate
            WindowTupleList list = (WindowTupleList) memory.events.get(factHandle);
            if (isAllowed) {
                if (list != null) {
                    context.setActiveWindowTupleList(list);
                    this.sink.propagateModifyObject(factHandle,
                            modifyPreviousTuples,
                            context,
                            workingMemory);
                    context.setActiveWindowTupleList(null);
                } else {
                    list = new WindowTupleList((EventFactHandle) factHandle, this);
                    context.setActiveWindowTupleList(list);
                    memory.events.put(factHandle,
                            list);
                    this.sink.propagateAssertObject(factHandle,
                            context,
                            workingMemory);
                    context.setActiveWindowTupleList(null);
                }
            } else {
                memory.events.remove(factHandle);
                // no need to propagate retract if it is no longer allowed
                // because the algorithm will automatically retract facts
                // based on the ModifyPreviousTuples parameters 
            }
        } finally {
            memory.gate.unlock();
        }
    }

    /**
     * Retract the <code>FactHandle</code> from the <code>WindowNode</code>.
     * This method is for the node benefit only as the node itself will not 
     * propagate retracts down the network, relying on the standard Rete
     * retract algorithm implemented by the ObjectTypeNode to do it.
     *
     * @param factHandle    The fact handle.
     * @param context       The propagation context
     * @param workingMemory The working memory session.
     */
    public void retractObject(final InternalFactHandle factHandle,
            final PropagationContext context,
            final InternalWorkingMemory workingMemory) {
        final WindowMemory memory = (WindowMemory) workingMemory.getNodeMemory(this);

        // must guarantee single thread from now on
        memory.gate.lock();
        
        try {
            // behavior retract
            behavior.retractFact(memory,
                    factHandle,
                    workingMemory);

            // memory retract
            memory.events.remove(factHandle);

            // as noted in the javadoc, this node will not propagate retracts, relying
            // on the standard algorithm to do it instead.
        } finally {
            memory.gate.unlock();
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
            final InternalWorkingMemory workingMemory) {
        final WindowMemory memory = (WindowMemory) workingMemory.getNodeMemory(this);

        // even if the update Sink guarantees the kbase/ksession lock is acquired, we can't
        // have triggers being executed concurrently
        memory.gate.lock();
        
        try {
            Iterator it = memory.events.iterator();
            try {
                for (ObjectHashMap.ObjectEntry entry = (ObjectHashMap.ObjectEntry) it.next(); entry != null; entry = (ObjectHashMap.ObjectEntry) it.next()) {
                    sink.assertObject((InternalFactHandle) entry.getValue(),
                            context,
                            workingMemory);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } finally {
            memory.gate.unlock();
        }
    }

    /**
     * Creates the WindowNode's memory.
     */
    public Memory createMemory(final RuleBaseConfiguration config) {
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

    public EntryPoint getEntryPoint() {
        return entryPoint;
    }

    public static class WindowMemory implements Externalizable, Memory {

        private static final long      serialVersionUID = 540l;

        public ObjectHashMap           events           = new ObjectHashMap();
        public ContextEntry[]          context;
        public Object                  behaviorContext;

        public transient ReentrantLock gate;

        public void readExternal(ObjectInput in) throws IOException,
                ClassNotFoundException {
            context = (ContextEntry[]) in.readObject();
            behaviorContext = (Object) in.readObject();
            events = (ObjectHashMap) in.readObject();
            gate = new ReentrantLock();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject(context);
            out.writeObject(behaviorContext);
            out.writeObject(events);
        }

        public short getNodeType() {
            return NodeTypeEnums.WindowNode;
        }
    }

    @Override
    public long calculateDeclaredMask(List<String> settableProperties) {
        throw new UnsupportedOperationException();
    }
}
