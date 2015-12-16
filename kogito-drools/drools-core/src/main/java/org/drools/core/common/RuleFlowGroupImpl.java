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

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.marshalling.impl.MarshallerReaderContext;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.drools.core.marshalling.impl.ProtobufMessages;
import org.drools.core.phreak.PropagationEntry;
import org.drools.core.spi.Activation;
import org.drools.core.spi.PropagationContext;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of a <code>RuleFlowGroup</code> that collects activations
 * of rules of this ruleflow-group.
 * If this group is activated, all its activations are added to the agenda.
 * As long as this group is active, its activations are added to the agenda.
 * Deactivating the group removes all its activations from the agenda and
 * collects them until it is activated again.
 * By default, <code>RuleFlowGroups</code> are automatically deactivated when there are no more
 * activations in the <code>RuleFlowGroup</code>.  However, this can be configured.
 */
public class RuleFlowGroupImpl
    implements
    InternalRuleFlowGroup,
    InternalAgendaGroup {

    private static final long           serialVersionUID = 510l;

    private InternalWorkingMemory       workingMemory;
//    private String                      name;
//    private boolean                     active           = false;
    private boolean                     autoDeactivate   = true;
//    private LinkedList<ActivationNode>  list;
    private List<RuleFlowGroupListener> listeners;
    private Map<Long, String>           nodeInstances    = new HashMap<Long, String>();

//    private long activatedForRecency;
//    private long clearedForRecency;
    private  final InternalAgendaGroup agendaGroup;

    public RuleFlowGroupImpl() {
        agendaGroup = null;
    }

    /**
     * Construct a <code>RuleFlowGroupImpl</code> with the given name.
     *
     * @param name
     *      The RuleFlowGroup name.
     */
    public RuleFlowGroupImpl(final String name, InternalKnowledgeBase kBase) {
        //this.name = name;
        //this.list = new LinkedList();
        agendaGroup = new AgendaGroupQueueImpl(name, kBase);
    }

    public RuleFlowGroupImpl(final String name,
                             final boolean active,
                             final boolean autoDeactivate) {
        agendaGroup = null;
//        this.name = name;
//        this.active = active;
//        this.autoDeactivate = autoDeactivate;
//        this.list = new LinkedList();
//        this.clearedForRecency = -1;
    }

    public String getName() {
        return agendaGroup.getName();
    }

    public void setWorkingMemory(InternalWorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public InternalWorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    @Override
    public void hasRuleFlowListener(boolean hasRuleFlowLister) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isRuleFlowListener() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Activation remove() {
        return agendaGroup.remove();
    }

    public Activation peek() {
        return agendaGroup.peek();
    }

    public void setActive(final boolean active) {
        this.agendaGroup.setActive( active );
//        if ( this.active == active ) {
//            return;
//        }
//        this.active = active;
//        synchronized ( list ) {
//            if ( active ) {
//                setActivatedForRecency( this.workingMemory.getFactHandleFactory().getRecency() );
//                ((EventSupport) this.workingMemory).getAgendaEventSupport().fireBeforeRuleFlowGroupActivated( this,
//                                                                                                                this.workingMemory );
//                if ( this.list.isEmpty() ) {
//                    if ( this.autoDeactivate ) {
//                        // if the list of activations is empty and
//                        // auto-deactivate is on, deactivate this group
//                        WorkingMemoryAction action = new DeactivateCallback( this );
//                        this.workingMemory.queueWorkingMemoryAction( action );
//                    }
//                } else {
//                    triggerActivations();
//                }
//                ((EventSupport) this.workingMemory).getAgendaEventSupport().fireAfterRuleFlowGroupActivated( this,
//                                                                                                             this.workingMemory );
//            } else {
//                ((EventSupport) this.workingMemory).getAgendaEventSupport().fireBeforeRuleFlowGroupDeactivated( this,
//                                                                                                                this.workingMemory );
//
//                FastIterator it = list.fastIterator();
//                for ( ActivationNode entry =  list.getFirst(); entry != null; entry = (ActivationNode) it.next( entry ) ) {
//                    final Activation activation = entry.getActivation();
//                    activation.remove();
//                    if ( activation.getActivationGroupNode() != null ) {
//                        activation.getActivationGroupNode().getActivationGroup().removeActivation( activation );
//                    }
//                }
//
//                nodeInstances.clear();
//                notifyRuleFlowGroupListeners();
//                ((EventSupport) this.workingMemory).getAgendaEventSupport().fireAfterRuleFlowGroupDeactivated( this,
//                                                                                                                 this.workingMemory );
//            }
//        }
    }

    public boolean isActive() {
        return agendaGroup.isActive();
    }

    @Override
    public void setAutoFocusActivator(PropagationContext ctx) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public PropagationContext getAutoFocusActivator() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isAutoDeactivate() {
        return this.autoDeactivate;
    }

    public void setAutoDeactivate(final boolean autoDeactivate) {
        this.autoDeactivate = autoDeactivate;
        synchronized ( agendaGroup ) {
            if ( autoDeactivate && agendaGroup.isActive() && agendaGroup.isEmpty() ) {
                this.agendaGroup.setActive( false );
            }
        }
    }

//    private void triggerActivations() {
//
//        // iterate all activations adding them to their AgendaGroups
//        synchronized ( this.list ) {
//            FastIterator it = list.fastIterator();
//            for ( ActivationNode entry =  list.getFirst(); entry != null; entry = (ActivationNode) it.next( entry ) ) {
//                final Activation activation = entry.getActivation();
//                ((InternalAgendaGroup) activation.getAgendaGroup()).add( activation );
//            }
//        }
//
//        // making sure we re-evaluate agenda in case we are waiting for activations
//        ((InternalAgenda) workingMemory.getAgenda()).notifyWaitOnRest();
//    }

    public void clear() {
        synchronized ( agendaGroup ) {
            agendaGroup.clear();
        }
    }

    public void reset() {
        synchronized ( agendaGroup ) {
            agendaGroup.reset();
        }
    }

    @Override
    public void setFocus() {
        //agendaGroup.setFocus();
    }

    @Override
    public Activation[] getAndClear() {
        return new Activation[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void add(Activation activation) {
        addActivation( activation );
    }


    public void addActivation(final Activation activation) {
        synchronized ( agendaGroup ) {
            agendaGroup.add(activation);
        }
//        assert activation.getActivationNode() == null;
//        final ActivationNode node = new ActivationNode( activation,
//                                                        this );
//        activation.setActivationNode( node );
//        synchronized ( this.list ) {
//            this.list.add( node );
//        }
//
//        if ( this.active ) {
//            ((InternalAgendaGroup) activation.getAgendaGroup()).add( activation );
//        }
    }

    @Override
    public void remove(Activation activation) {
        removeActivation( activation );
    }

    public void removeActivation(final Activation activation) {
        synchronized ( agendaGroup ) {
            agendaGroup.remove(activation);
        }
//        synchronized ( this.list ) {
//            final ActivationNode node = activation.getActivationNode();
//            this.list.remove( node );
//            activation.setActivationNode( null );
//        }
    }

    /**
     * Checks if this ruleflow group is active and should automatically deactivate.
     * If the queue is empty, it deactivates the group.
     */
    public void deactivateIfEmpty() {
//        synchronized ( this.list ) {
//            if ( this.active && this.autoDeactivate && this.list.isEmpty() ) {
//                // deactivate callback
//                WorkingMemoryAction action = new DeactivateCallback( this );
//                this.workingMemory.queueWorkingMemoryAction( action );
//            }
//        }
    }

    public void addRuleFlowGroupListener(RuleFlowGroupListener listener) {
        if ( listeners == null ) {
            listeners = new CopyOnWriteArrayList<RuleFlowGroupListener>();
        }
        listeners.add( listener );
    }

    public void removeRuleFlowGroupListener(RuleFlowGroupListener listener) {
        if ( listeners != null ) {
            listeners.remove( listener );
        }
    }

    public void notifyRuleFlowGroupListeners() {
        if ( listeners != null ) {
            for ( java.util.Iterator<RuleFlowGroupListener> iterator = listeners.iterator(); iterator.hasNext(); ) {
                iterator.next().ruleFlowGroupDeactivated();
            }
        }
    }

    public boolean isEmpty() {
        synchronized ( agendaGroup ) {
            return agendaGroup.isEmpty();
        }
    }
    
    public Activation[] getActivations() {
        synchronized ( agendaGroup ) {
            //return agendaGroup.getActivations();
            return null;
        }
    }

    public java.util.Iterator iterator() {
        //return agendaGroup.it
        return null;
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

    public Map<Long, String> getNodeInstances() {
        return nodeInstances;
    }
    
    public void setActivatedForRecency(long recency) {
        agendaGroup.setActivatedForRecency( recency );
    }

    public long getActivatedForRecency() {
        return agendaGroup.getActivatedForRecency();
    }       
    
    public void setClearedForRecency(long recency) {
        agendaGroup.setClearedForRecency( recency );
    }

    public long getClearedForRecency() {
        return agendaGroup.getClearedForRecency();
    }

    public String toString() {
        return "RuleFlowGroup '" + this.agendaGroup.remove() + "'";
    }

    @Override
    public void visited() {
        agendaGroup.visited();
    }

    public int size() {
        synchronized ( agendaGroup ) {
            return agendaGroup.size();
        }
    }

    public boolean equals(final Object object) {
        if ( (object == null) || !(object instanceof RuleFlowGroupImpl) ) {
            return false;
        }

        if ( ((RuleFlowGroupImpl) object).getName().equals( getName() ) ) {
            return true;
        }

        return false;
    }

    public int hashCode() {
        return getName().hashCode();
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

    @Override
    public boolean isSequential() {
        return agendaGroup.isSequential();
    }
}
