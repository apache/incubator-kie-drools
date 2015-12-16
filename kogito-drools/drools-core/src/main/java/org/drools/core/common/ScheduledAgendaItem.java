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

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.rule.GroupElement;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.PropagationContext;
import org.drools.core.spi.Tuple;
import org.drools.core.time.JobHandle;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListNode;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.event.rule.ActivationUnMatchListener;

import java.util.List;

public class ScheduledAgendaItem<T extends ModedAssertion<T>>
            implements
            AgendaItem<T>,
            LinkedListNode<ScheduledAgendaItem<T>> {

    private static final long serialVersionUID = 510l;
    private ScheduledAgendaItem previous;
    private ScheduledAgendaItem next;
    private InternalAgenda      agenda;
    private boolean             enqueued;
    private JobHandle           jobHandle;
    private AgendaItem          agendaItem;

    public ScheduledAgendaItem(AgendaItem agendaItem, InternalAgenda agenda) {
        this.agendaItem = agendaItem;
        this.agenda = agenda;
        this.enqueued = false;
    }

    public PropagationContext getPropagationContext() {
        return agendaItem.getPropagationContext();
    }

    public void setPropagationContext(PropagationContext context) {
        agendaItem.setPropagationContext(context);
    }

    public boolean isMatched() {
        return agendaItem.isMatched();
    }

    public void setMatched(boolean matched) {
        agendaItem.setMatched(matched);
    }

    public boolean isActive() {
        return agendaItem.isActive();
    }

    public void setActive(boolean active) {
        agendaItem.setActive(active);
    }

    public TerminalNode getTerminalNode() {
        return agendaItem.getTerminalNode();
    }

    public int getSalience() {
        return agendaItem.getSalience();
    }

    public void setSalience(int salience) {
        agendaItem.setSalience(salience);
    }

    public String toExternalForm() {
        return agendaItem.toExternalForm();
    }

    public LinkedList<LogicalDependency<SimpleMode>> getBlocked() {
        return agendaItem.getBlocked();
    }

    public void setBlocked(LinkedList<LogicalDependency<SimpleMode>> justified) {
        agendaItem.setBlocked(justified);
    }

    public InternalFactHandle getActivationFactHandle() {
        return agendaItem.getActivationFactHandle();
    }

    public void setActivationFactHandle( InternalFactHandle factHandle ) {
        agendaItem.setActivationFactHandle( factHandle );
    }

    public ActivationGroupNode getActivationGroupNode() {
        return agendaItem.getActivationGroupNode();
    }

    public void setActivationGroupNode(ActivationGroupNode activationNode) {
        agendaItem.setActivationGroupNode(activationNode);
    }

    public Consequence getConsequence() {
        return agendaItem.getConsequence();
    }

    public ActivationUnMatchListener getActivationUnMatchListener() {
        return agendaItem.getActivationUnMatchListener();
    }

    public void setActivationUnMatchListener(ActivationUnMatchListener activationUnMatchListener) {
        agendaItem.setActivationUnMatchListener(activationUnMatchListener);
    }

    public RuleImpl getRule() {
        return agendaItem.getRule();
    }

    public InternalAgendaGroup getAgendaGroup() {
        return agendaItem.getAgendaGroup();
    }

    public long getActivationNumber() {
        return agendaItem.getActivationNumber();
    }

    public void removeBlocked(LogicalDependency dep) {
        agendaItem.removeBlocked(dep);
    }

    public void addBlocked(LogicalDependency dep) {
        agendaItem.addBlocked(dep);
    }

    public void dequeue() {
        agendaItem.dequeue();
    }

    public int getQueueIndex() {
        return agendaItem.getQueueIndex();
    }

    public void setQueueIndex(int index) {
        agendaItem.setQueueIndex(index);
    }

    public boolean isQueued() {
        return agendaItem.isQueued();
    }

    public void setQueued(boolean queued) {
        agendaItem.setQueued(queued);
    }

    public void addLogicalDependency(LogicalDependency<T> node) {
        agendaItem.addLogicalDependency(node);
    }

    public LinkedList<SimpleMode> getBlockers() {
        return agendaItem.getBlockers();
    }

    public Object getDeclarationValue(String variableName) {
        return agendaItem.getDeclarationValue(variableName);
    }

    public RuleAgendaItem getRuleAgendaItem() {
        return agendaItem.getRuleAgendaItem();
    }

    public Tuple getTuple() {
        return agendaItem.getTuple();
    }

    public List<String> getDeclarationIds() {
        return agendaItem.getDeclarationIds();
    }

    public void cancel() {
        agendaItem.cancel();
    }

    public ActivationNode getActivationNode() {
        return agendaItem.getActivationNode();
    }

    public void setActivationNode(ActivationNode activationNode) {
        agendaItem.setActivationNode(activationNode);
    }

    public GroupElement getSubRule() {
        return agendaItem.getSubRule();
    }

    public void removeAllBlockersAndBlocked(InternalAgenda agenda) {
        agendaItem.removeAllBlockersAndBlocked(agenda);
    }

    public boolean isCanceled() {
        return agendaItem.isCanceled();
    }

    public List<Object> getObjects() {
        return agendaItem.getObjects();
    }

    public boolean isRuleAgendaItem() {
        return agendaItem.isRuleAgendaItem();
    }

    public LinkedList<LogicalDependency<T>> getLogicalDependencies() {
        return agendaItem.getLogicalDependencies();
    }

    public void setLogicalDependencies(LinkedList<LogicalDependency<T>> justified) {
        agendaItem.setLogicalDependencies(justified);
    }

    public List<FactHandle> getFactHandles() {
        return agendaItem.getFactHandles();
    }

    public ScheduledAgendaItem getNext() {
        return this.next;
    }

    public void setNext(final ScheduledAgendaItem next) {
        this.next = next;
    }

    public ScheduledAgendaItem getPrevious() {
        return this.previous;
    }

    public void setPrevious(final ScheduledAgendaItem previous) {
        this.previous = previous;
    }

    public void nullPrevNext() {
        previous = null;
        next = null;
    }

    public void remove() {
        this.agenda.removeScheduleItem(this);
    }

    public JobHandle getJobHandle() {
        return this.jobHandle;
    }

    public void setJobHandle(JobHandle jobHandle) {
        this.jobHandle = jobHandle;
    }

    public String toString() {
        return "[ScheduledActivation rule=" + getRule().getName() + ", tuple=" + getTuple() + "]";
    }

    public boolean isEnqueued() {
        return enqueued;
    }

    public void setEnqueued(boolean enqueued) {
        this.enqueued = enqueued;
    }


}
