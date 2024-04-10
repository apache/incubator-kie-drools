/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.core.common;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.drools.core.impl.InternalRuleBase;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.RuntimeComponentFactory;

public interface AgendaGroupsManager extends Externalizable {

    void reset(boolean clearForRecency);

    void deactivateMainGroupWhenEmpty();

    void clearAndCancel(InternalAgenda agenda);

    void clearAndCancelAgendaGroup(String name, InternalAgenda agenda);

    InternalAgendaGroup[] getAgendaGroups();

    Map<String, InternalAgendaGroup> getAgendaGroupsMap();
    void putOnAgendaGroupsMap(String name, InternalAgendaGroup group);

    void addAgendaGroupOnStack(InternalAgendaGroup agendaGroup);

    boolean setFocus(InternalAgendaGroup agendaGroup);

    Collection<String> getGroupsName();

    String getFocusName();

    RuleAgendaItem peekNextRule();

    InternalAgendaGroup getAgendaGroup(String name);

    InternalAgendaGroup getAgendaGroup(String name, InternalRuleBase kBase);

    InternalAgendaGroup getNextFocus();

    void deactivateRuleFlowGroup(String name);

    boolean removeGroup(InternalAgendaGroup group);

    int agendaSize();

    int sizeOfRuleFlowGroup(String name);

    Collection<RuleAgendaItem> getActivations();

    InternalAgendaGroup getMainAgendaGroup();

    static AgendaGroupsManager create(InternalWorkingMemory workingMemory) {
        InternalRuleBase kBase = workingMemory.getKnowledgeBase();
        return kBase.hasMultipleAgendaGroups() || !kBase.getProcesses().isEmpty() ? new StackedAgendaGroupsManager(workingMemory) : new SimpleAgendaGroupsManager(workingMemory);
    }

    class SimpleAgendaGroupsManager implements AgendaGroupsManager {
        private InternalAgendaGroup mainAgendaGroup;
        private ReteEvaluator reteEvaluator;

        public SimpleAgendaGroupsManager() { }

        public SimpleAgendaGroupsManager(ReteEvaluator reteEvaluator) {
            this.reteEvaluator = reteEvaluator;
            this.mainAgendaGroup = RuntimeComponentFactory.get().getAgendaGroupFactory().createAgendaGroup(InternalAgendaGroup.MAIN, reteEvaluator.getKnowledgeBase());
            this.mainAgendaGroup.setReteEvaluator(reteEvaluator);
        }

        @Override
        public InternalAgendaGroup getMainAgendaGroup() {
            return mainAgendaGroup;
        }

        @Override
        public void reset(boolean clearForRecency) {
            mainAgendaGroup.visited();
            if (clearForRecency) {
                mainAgendaGroup.setClearedForRecency(this.reteEvaluator.getFactHandleFactory().getRecency());
            }
            mainAgendaGroup.reset();
        }

        @Override
        public void deactivateMainGroupWhenEmpty() {
            if (this.mainAgendaGroup.isEmpty()) {
                // the root MAIN agenda group is empty, reset active to false, so it can receive more activations.
                this.mainAgendaGroup.setActive(false);
            }
        }

        @Override
        public void clearAndCancel(InternalAgenda agenda) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void clearAndCancelAgendaGroup(String name, InternalAgenda agenda) {
            throw new UnsupportedOperationException();
        }

        @Override
        public InternalAgendaGroup[] getAgendaGroups() {
            return new InternalAgendaGroup[] { this.mainAgendaGroup };
        }

        @Override
        public Map<String, InternalAgendaGroup> getAgendaGroupsMap() {
            return Collections.singletonMap(InternalAgendaGroup.MAIN, mainAgendaGroup);
        }

        @Override
        public void addAgendaGroupOnStack(InternalAgendaGroup agendaGroup) {
            if (!InternalAgendaGroup.MAIN.equals(agendaGroup.getName())) {
                throw new UnsupportedOperationException();
            }
            this.mainAgendaGroup = agendaGroup;
        }

        @Override
        public boolean setFocus(InternalAgendaGroup agendaGroup) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Collection<String> getGroupsName() {
            return Collections.singleton(InternalAgendaGroup.MAIN);
        }

        @Override
        public void putOnAgendaGroupsMap(String name, InternalAgendaGroup group) {
            if (!InternalAgendaGroup.MAIN.equals(name)) {
                throw new UnsupportedOperationException();
            }
        }

        @Override
        public String getFocusName() {
            return InternalAgendaGroup.MAIN;
        }

        @Override
        public RuleAgendaItem peekNextRule() {
            return this.mainAgendaGroup.peek();
        }

        @Override
        public InternalAgendaGroup getAgendaGroup(String name) {
            return InternalAgendaGroup.MAIN.equals(name) ? this.mainAgendaGroup : null;
        }

        @Override
        public InternalAgendaGroup getAgendaGroup(String name, InternalRuleBase kBase) {
            return InternalAgendaGroup.MAIN.equals(name) ? this.mainAgendaGroup : null;
        }

        @Override
        public InternalAgendaGroup getNextFocus() {
            if (mainAgendaGroup.isEmpty()) {
                return null;
            }
            if ( !mainAgendaGroup.isActive() ) {
                // only update recency, if not already active. It may be active already if the use called setFocus
                mainAgendaGroup.setActivatedForRecency( this.reteEvaluator.getFactHandleFactory().getRecency() );
                mainAgendaGroup.setActive( true );
            }
            return mainAgendaGroup;
        }

        @Override
        public void deactivateRuleFlowGroup(String name) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeGroup(InternalAgendaGroup group) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int agendaSize() {
            return mainAgendaGroup.size();
        }

        @Override
        public int sizeOfRuleFlowGroup(String name) {
            if (!InternalAgendaGroup.MAIN.equals(name)) {
                return 0;
            }
            int count = 0;
            for ( RuleAgendaItem item : mainAgendaGroup.getActivations() ) {
                if (!(item.getRuleExecutor().getActiveMatches().isEmpty())) {
                    count = count + item.getRuleExecutor().getActiveMatches().size();
                }
            }
            return count;
        }

        @Override
        public Collection<RuleAgendaItem> getActivations() {
            return this.mainAgendaGroup.getActivations();
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( mainAgendaGroup );
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            mainAgendaGroup = (InternalAgendaGroup) in.readObject();
        }
    }

    class StackedAgendaGroupsManager implements AgendaGroupsManager {

        private AgendaGroupFactory agendaGroupFactory;
        private Map<String, InternalAgendaGroup> agendaGroups = new HashMap<>();
        private Deque<InternalAgendaGroup> focusStack = new ArrayDeque<>();
        private InternalAgendaGroup mainAgendaGroup;
        private InternalWorkingMemory workingMemory;

        public StackedAgendaGroupsManager() { }

        public StackedAgendaGroupsManager(InternalWorkingMemory workingMemory) {
            this.agendaGroupFactory = RuntimeComponentFactory.get().getAgendaGroupFactory();
            // stacked agenda groups are supported only for InternalWorkingMemory
            this.workingMemory = workingMemory;
            if (this.mainAgendaGroup == null) {
                initMainAgendaGroup(workingMemory.getKnowledgeBase());
            }
            this.mainAgendaGroup.setReteEvaluator( workingMemory );
        }

        @Override
        public InternalAgendaGroup getMainAgendaGroup() {
            return mainAgendaGroup;
        }

        private void initMainAgendaGroup(InternalRuleBase kBase) {
            this.mainAgendaGroup = agendaGroupFactory.createAgendaGroup( InternalAgendaGroup.MAIN, kBase);
            this.agendaGroups.put( InternalAgendaGroup.MAIN, this.mainAgendaGroup );
            this.focusStack.add( this.mainAgendaGroup );
        }

        private boolean isEmpty() {
            return focusStack.isEmpty();
        }

        private void addAgendaGroup(InternalAgendaGroup agendaGroup) {
            this.agendaGroups.put( agendaGroup.getName(), agendaGroup );
        }

        @Override
        public void reset(boolean clearForRecency) {
            for ( InternalAgendaGroup group : focusStack ) {
                group.visited();
            }
            this.focusStack.clear();
            this.focusStack.add( this.mainAgendaGroup );

            for ( InternalAgendaGroup group : this.agendaGroups.values() ) {
                // preserve lazy items.
                if (clearForRecency) {
                    group.setClearedForRecency(this.workingMemory.getFactHandleFactory().getRecency());
                }
                group.reset();
            }
        }

        @Override
        public void deactivateMainGroupWhenEmpty() {
            if (this.focusStack.size() == 1 && this.mainAgendaGroup.isEmpty()) {
                // the root MAIN agenda group is empty, reset active to false, so it can receive more activations.
                this.mainAgendaGroup.setActive(false);
            }
        }

        @Override
        public void clearAndCancel(InternalAgenda agenda) {
            // Cancel all items and fire a Cancelled event for each Activation
            for (InternalAgendaGroup internalAgendaGroup : this.agendaGroups.values()) {
                clearAndCancelAgendaGroup(internalAgendaGroup, agenda);
            }
        }

        @Override
        public void clearAndCancelAgendaGroup(String name, InternalAgenda agenda) {
            InternalAgendaGroup agendaGroup = this.agendaGroups.get( name );
            if ( agendaGroup != null ) {
                clearAndCancelAgendaGroup( agendaGroup, agenda );
            }
        }

        private void clearAndCancelAgendaGroup(InternalAgendaGroup agendaGroup, InternalAgenda agenda) {
            // enforce materialization of all activations of this group before removing them
            for (RuleAgendaItem activation : agendaGroup.getActivations()) {
                activation.getRuleExecutor().evaluateNetworkIfDirty( agenda );
            }

            final EventSupport eventsupport = this.workingMemory;

            agendaGroup.setClearedForRecency( this.workingMemory.getFactHandleFactory().getRecency() );

            // this is thread safe for BinaryHeapQueue
            // Binary Heap locks while it returns the array and reset's it's own internal array. Lock is released afer getAndClear()
            List<RuleAgendaItem> lazyItems = new ArrayList<>();
            for (RuleAgendaItem item : agendaGroup.getActivations() ) {
                lazyItems.add(item );
                item.getRuleExecutor().cancel(workingMemory, eventsupport);
            }
            agendaGroup.reset();

            // restore lazy items
            for ( RuleAgendaItem lazyItem : lazyItems ) {
                agendaGroup.add( lazyItem );
            }
        }

        @Override
        public InternalAgendaGroup[] getAgendaGroups() {
            return this.agendaGroups.values().toArray( new InternalAgendaGroup[this.agendaGroups.size()] );
        }

        @Override
        public Map<String, InternalAgendaGroup> getAgendaGroupsMap() {
            return this.agendaGroups;
        }

        @Override
        public void putOnAgendaGroupsMap(String name, InternalAgendaGroup group){
            this.agendaGroups.put(name, group);
        }

        @Override
        public void addAgendaGroupOnStack(InternalAgendaGroup agendaGroup) {
            if ( focusStack.isEmpty() || focusStack.getLast() != agendaGroup ) {
                focusStack.add( agendaGroup );
            }
        }

        @Override
        public boolean setFocus(final InternalAgendaGroup agendaGroup) {
            // Set the focus to the agendaGroup if it doesn't already have the focus
            if ( this.focusStack.getLast() != agendaGroup ) {
                this.focusStack.getLast().setActive( false );
                InternalAgendaGroup internalGroup = agendaGroup;
                this.focusStack.add( internalGroup );
                internalGroup.setActive( true );
                internalGroup.setActivatedForRecency( this.workingMemory.getFactHandleFactory().getRecency() );
                final EventSupport eventsupport = this.workingMemory;
                eventsupport.getAgendaEventSupport().fireAgendaGroupPushed( agendaGroup, this.workingMemory );
                return true;
            }
            return false;
        }

        @Override
        public Collection<String> getGroupsName() {
            return this.focusStack.stream().map(InternalAgendaGroup::getName).collect(Collectors.toList());
        }

        @Override
        public String getFocusName() {
            return this.focusStack.getLast().getName();
        }

        @Override
        public RuleAgendaItem peekNextRule() {
            return this.focusStack.peekLast().peek();
        }

        @Override
        public InternalAgendaGroup getAgendaGroup(final String name) {
            return getAgendaGroup( name, workingMemory == null ? null : workingMemory.getKnowledgeBase() );
        }

        @Override
        public InternalAgendaGroup getAgendaGroup(final String name, InternalRuleBase kBase) {
            String groupName = (name == null || name.length() == 0) ? InternalAgendaGroup.MAIN : name;

            InternalAgendaGroup agendaGroup = this.agendaGroups.get( groupName );
            if ( agendaGroup == null ) {
                // The AgendaGroup is defined but not yet added to the
                // Agenda, so create the AgendaGroup and add to the Agenda.
                agendaGroup = agendaGroupFactory.createAgendaGroup( name, kBase );
                addAgendaGroup( agendaGroup );

                agendaGroup.setReteEvaluator( workingMemory );
            }

            return agendaGroup;
        }

        @Override
        public InternalAgendaGroup getNextFocus() {
            if (isEmpty()) {
                return null;
            }

            InternalAgendaGroup agendaGroup;
            // Iterate until we find a populate AgendaModule or we reach the MAIN,
            // default, AgendaGroup
            while ( true ) {
                agendaGroup = this.focusStack.getLast();

                if ( !agendaGroup.isAutoDeactivate() ) {
                    // does not automatically pop, when empty, so always return, even if empty
                    break;
                }

                // No populated queues found so pop the focusStack and repeat
                if ( agendaGroup.isEmpty() && (this.focusStack.size() > 1) ) {
                    agendaGroup.setActive( false );
                    removeLast();

                    if ( agendaGroup.isAutoDeactivate() && !agendaGroup.getNodeInstances().isEmpty() ) {
                        InternalRuleFlowGroup ruleFlowGroup = (InternalRuleFlowGroup) agendaGroup;
                        this.workingMemory.getAgendaEventSupport().fireBeforeRuleFlowGroupDeactivated( ruleFlowGroup, this.workingMemory );
                        innerDeactiveRuleFlowGroup(ruleFlowGroup);
                        this.workingMemory.getAgendaEventSupport().fireAfterRuleFlowGroupDeactivated( ruleFlowGroup, this.workingMemory );
                    }
                    final EventSupport eventsupport = this.workingMemory;
                    eventsupport.getAgendaEventSupport().fireAgendaGroupPopped( agendaGroup, this.workingMemory );
                } else {
                    agendaGroup = agendaGroup.isEmpty() ? null : agendaGroup;
                    break;
                }
            }

            if ( agendaGroup != null &&  !agendaGroup.isActive() ) {
                // only update recency, if not already active. It may be active already if the use called setFocus
                agendaGroup.setActivatedForRecency( this.workingMemory.getFactHandleFactory().getRecency() );
                agendaGroup.setActive( true );
            }
            return agendaGroup;
        }

        @Override
        public void deactivateRuleFlowGroup(final String name) {
            InternalRuleFlowGroup group = (InternalRuleFlowGroup) getAgendaGroup(name);
            if ( !group.isRuleFlowListener() ) {
                return;
            }
            this.workingMemory.getAgendaEventSupport().fireBeforeRuleFlowGroupDeactivated( group, this.workingMemory );
            while ( removeGroup(group) ) {}; // keep removing while group is on the stack
            group.setActive( false );
            innerDeactiveRuleFlowGroup( group );
            this.workingMemory.getAgendaEventSupport().fireAfterRuleFlowGroupDeactivated( group, this.workingMemory );
        }

        private void innerDeactiveRuleFlowGroup(InternalRuleFlowGroup group) {
            group.hasRuleFlowListener( false );
            group.getNodeInstances().clear();
        }

        private void removeLast() {
            this.focusStack.removeLast().visited();
        }

        @Override
        public boolean removeGroup(InternalAgendaGroup group) {
            boolean existed = this.focusStack.remove( group );
            group.visited();
            return existed;
        }

        @Override
        public int agendaSize() {
            int size = 0;
            for ( InternalAgendaGroup internalAgendaGroup : this.agendaGroups.values() ) {
                size += internalAgendaGroup.size();
            }
            return size;
        }

        @Override
        public int sizeOfRuleFlowGroup(String name) {
            InternalAgendaGroup group = agendaGroups.get( name );
            if (group == null) {
                return 0;
            }

            int count = 0;
            for ( RuleAgendaItem item : group.getActivations() ) {
                if (!item.getRuleExecutor().getActiveMatches().isEmpty()) {
                    count = count + item.getRuleExecutor().getActiveMatches().size();
                }
            }
            return count;
        }

        @Override
        public Collection<RuleAgendaItem> getActivations() {
            final List<RuleAgendaItem> list = new ArrayList<>();
            for (InternalAgendaGroup group : this.agendaGroups.values()) {
                list.addAll(group.getActivations());
            }
            return list;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( agendaGroups );
            out.writeObject( focusStack );
            out.writeObject( mainAgendaGroup );
            out.writeObject( agendaGroupFactory );
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            agendaGroups = (Map) in.readObject();
            focusStack = (Deque) in.readObject();
            mainAgendaGroup = (InternalAgendaGroup) in.readObject();
            agendaGroupFactory = (AgendaGroupFactory) in.readObject();
        }
    }
}
