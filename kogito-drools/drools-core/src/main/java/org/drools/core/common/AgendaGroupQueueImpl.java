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

package org.drools.core.common;

import org.drools.core.conflict.PhreakConflictResolver;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.BinaryHeapQueue;

import java.io.IOException;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <code>AgendaGroup</code> implementation that uses a <code>PriorityQueue</code> to prioritise the evaluation of added
 * <code>ActivationQueue</code>s. The <code>AgendaGroup</code> also maintains a <code>Map</code> of <code>ActivationQueues</code>
 * for requested salience values.
 *
 * @see PriorityQueue
 */
public class AgendaGroupQueueImpl
        implements
        InternalAgendaGroup,
        InternalRuleFlowGroup {
    private static final long serialVersionUID = 510l;
    private final    String             name;
    /**
     * Items in the agenda.
     */
    protected final  BinaryHeapQueue    priorityQueue;
    private volatile boolean            active;
    private          PropagationContext autoFocusActivator;
    private          long               activatedForRecency;
    private          long               clearedForRecency;

    private InternalWorkingMemory workingMemory;
    private boolean               autoDeactivate = true;
    private Map<Long, String>     nodeInstances  = new ConcurrentHashMap<Long, String>();

    private volatile              boolean hasRuleFlowLister;

    private Activation            lastRemoved;
    private boolean               sequential;

    private static final Activation visited = new VisitedAgendaGroup();

    public AgendaGroupQueueImpl(final String name,
                                final InternalKnowledgeBase kBase) {
        this.name = name;
        this.sequential = kBase.getConfiguration().isSequential();

        this.priorityQueue = initPriorityQueue( kBase );

        this.clearedForRecency = -1;
    }

    protected BinaryHeapQueue initPriorityQueue( InternalKnowledgeBase kBase ) {
        return new BinaryHeapQueue(new PhreakConflictResolver());
    }

    @Override
    public void visited() {
        if (sequential) {
            lastRemoved = visited;
        }
    }

    /* (non-Javadoc)
     * @see org.kie.spi.AgendaGroup#getName()
     */
    public String getName() {
        return this.name;
    }

    public void setWorkingMemory(InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public InternalWorkingMemory getWorkingMemory() {
        return workingMemory;
    }

    @Override
    public void hasRuleFlowListener(boolean hasRuleFlowLister) {
        this.hasRuleFlowLister = hasRuleFlowLister;
    }

    @Override
    public boolean isRuleFlowListener() {
        return hasRuleFlowLister;
    }

    public void clear() {
        workingMemory.addPropagation( new ClearAction( this.name ) );
    }

    public class ClearAction extends PropagationEntry.AbstractPropagationEntry {

        private final String name;

        public ClearAction( String name ) {
            this.name = name;
        }

        @Override
        public void execute( InternalWorkingMemory wm ) {
            wm.getAgenda().clearAndCancelAgendaGroup(this.name);
        }
    }

    public void setFocus() {
        workingMemory.addPropagation( new SetFocusAction( this.name ) );
    }

    public class SetFocusAction extends PropagationEntry.AbstractPropagationEntry {

        private final String name;

        public SetFocusAction( String name ) {
            this.name = name;
        }

        @Override
        public void execute( InternalWorkingMemory wm ) {
            wm.getAgenda().setFocus(this.name);
        }
    }

    public void reset() {
        this.priorityQueue.clear();
    }

    public Activation[] getAndClear() {
        return this.priorityQueue.getAndClear();
    }

    /* (non-Javadoc)
     * @see org.kie.spi.AgendaGroup#size()
     */
    public int size() {
        return this.priorityQueue.size();
    }

    public void add(final Activation activation) {
        if ( lastRemoved != null ) {
            // this will only be set if sequential. Do not add Match's that are higher in salience + load order than the lastRemoved (fired)
            if ( lastRemoved == activation || lastRemoved  == visited || PhreakConflictResolver.doCompare( lastRemoved, activation ) < 0 ) {
                return;
            }
        }
        this.priorityQueue.enqueue( activation);
    }

    public Activation remove() {
        Activation match = this.priorityQueue.dequeue();
        if ( sequential ) {
            lastRemoved = match;
        }
        return match;
    }

    public Activation peek() {
        return this.priorityQueue.peek();
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isAutoDeactivate() {
        return autoDeactivate;
    }

    public void setAutoDeactivate(boolean autoDeactivate) {
        this.autoDeactivate = autoDeactivate;
    }

    public void addNodeInstance(Long processInstanceId,
                                String nodeInstanceId) {
        nodeInstances.put( processInstanceId,
                           nodeInstanceId );
    }

    public void removeNodeInstance(Long processInstanceId,
                                   String nodeInstanceId) {
        nodeInstances.put( processInstanceId,
                           nodeInstanceId );
    }

    public void setActive(final boolean activate) {
        this.active = activate;
    }

    public PropagationContext getAutoFocusActivator() {
        return this.autoFocusActivator;
    }

    public void setAutoFocusActivator(PropagationContext autoFocusActivator) {
        this.autoFocusActivator = autoFocusActivator;
    }

    public boolean isEmpty() {
        return this.priorityQueue.isEmpty();
    }

    public Activation[] getActivations() {
        return (Activation[]) this.priorityQueue.toArray(new AgendaItem[this.priorityQueue.size()]);
    }

    @Override
    public Map<Long, String> getNodeInstances() {
        return nodeInstances;
    }

    public String toString() {
        return "AgendaGroup '" + this.name + "'";
    }

    public boolean equals(final Object object) {
        if ((object == null) || !(object instanceof AgendaGroupQueueImpl)) {
            return false;
        }

        return ((AgendaGroupQueueImpl) object).name.equals( this.name );
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    public void remove(final Activation activation) {
        this.priorityQueue.dequeue(activation);
    }

    public long getActivatedForRecency() {
        return this.activatedForRecency;
    }

    public void setActivatedForRecency(long recency) {
        this.activatedForRecency = recency;
    }

    public long getClearedForRecency() {
        return this.clearedForRecency;
    }

    public void setClearedForRecency(long recency) {
        this.clearedForRecency = recency;
    }

    public static class DeactivateCallback
            extends PropagationEntry.AbstractPropagationEntry
            implements WorkingMemoryAction {

        private static final long     serialVersionUID = 510l;

        private InternalRuleFlowGroup ruleFlowGroup;

        public DeactivateCallback(InternalRuleFlowGroup ruleFlowGroup) {
            this.ruleFlowGroup = ruleFlowGroup;
        }

        public DeactivateCallback(MarshallerReaderContext context) throws IOException {
            this.ruleFlowGroup = (InternalRuleFlowGroup) context.wm.getAgenda().getRuleFlowGroup( context.readUTF() );
        }

        public DeactivateCallback(MarshallerReaderContext context,
                                  ProtobufMessages.ActionQueue.Action _action) {
            this.ruleFlowGroup = (InternalRuleFlowGroup) context.wm.getAgenda().getRuleFlowGroup( _action.getDeactivateCallback().getRuleflowGroup() );
        }

        public ProtobufMessages.ActionQueue.Action serialize(MarshallerWriteContext context) {
            return ProtobufMessages.ActionQueue.Action.newBuilder()
                                               .setType( ProtobufMessages.ActionQueue.ActionType.DEACTIVATE_CALLBACK )
                                               .setDeactivateCallback( ProtobufMessages.ActionQueue.DeactivateCallback.newBuilder()
                                                                                                   .setRuleflowGroup( ruleFlowGroup.getName() )
                                                                                                   .build() )
                                               .build();
        }

        public void execute(InternalWorkingMemory workingMemory) {
            // check whether ruleflow group is still empty first
            if ( this.ruleFlowGroup.isEmpty() ) {
                // deactivate ruleflow group
                this.ruleFlowGroup.setActive( false );
            }
        }
    }

    public boolean isSequential() {
        return sequential;
    }

}
