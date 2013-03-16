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

package org.drools.core.util;

import java.util.List;

import org.drools.FactHandle;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.reteoo.LeftTupleImpl;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.PropagationContext;

public class BaseQueueable
    implements
    Queueable {
    private Queue queue;
    private int   index;
    
    public BaseQueueable(Queue queue) {
        this.queue = queue;
    }

    public void enqueued(final int index) {
        this.index = index;
    }

    public void dequeue() {
        this.queue.dequeue( this.index );
    }

    public void addLogicalDependency(LogicalDependency node) {
    }

    public ActivationGroupNode getActivationGroupNode() {
        return null;
    }

    public long getActivationNumber() {
        return 0;
    }

    public AgendaGroup getAgendaGroup() {
        return null;
    }

    public LinkedList getLogicalDependencies() {
        return null;
    }

    public PropagationContext getPropagationContext() {
        return null;
    }

    public Rule getRule() {
        return null;
    }

    public Consequence getConsequence() {
        // TODO Auto-generated method stub
        return null;
    }

    public ActivationNode getActivationNode() {
        return null;
    }

    public int getSalience() {
        return 0;
    }

    public GroupElement getSubRule() {
        return null;
    }

    public LeftTupleImpl getTuple() {
        return null;
    }

    public boolean isActivated() {
        return false;
    }

    public void remove() {
    }

    public void setActivated(boolean activated) {
    }

    public void setActivationGroupNode(ActivationGroupNode activationGroupNode) {
    }

    public void setLogicalDependencies(LinkedList<LogicalDependency> justified) {
    }

    public void setActivationNode(ActivationNode ruleFlowGroupNode) {
    }

    public List<FactHandle> getFactHandles() {
        return null;
    }

    public List<Object> getObjects() {
        return null;
    }

    public Object getDeclarationValue(String variableName) {
        return null;
    }

    public List<String> getDeclarationIds() {
        return null;
    }

    public InternalFactHandle getFactHandle() {
        return null;
    }

    public boolean isAdded() {
        return false;
    }
    
    public boolean isActive() {
        return isActivated();
    }

    public void addBlocked(LogicalDependency node) {
    }

    public LinkedList getBlocked() {
        return null;
    }

    public void setBlocked(LinkedList<LogicalDependency> justified) {
    }

    public void addBlocked(LinkedListNode node) {
    }

    public LinkedList getBlockers() {
        return null;
    }

    public boolean isMatched() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setMatched(boolean matched) {
     
    }
  
    public boolean isRuleNetworkEvaluatorActivation() {
        return false;
    }    
}
