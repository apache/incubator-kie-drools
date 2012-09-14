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

package org.drools.util;

import java.util.List;

import org.drools.FactHandle;
import org.drools.common.ActivationGroupNode;
import org.drools.common.ActivationNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.LogicalDependency;
import org.drools.common.LogicalDependency;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListNode;
import org.drools.core.util.Queue;
import org.drools.core.util.Queueable;
import org.drools.reteoo.LeftTupleImpl;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.AgendaGroup;
import org.drools.spi.Consequence;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

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
        // TODO Auto-generated method stub
        
    }

    public ActivationGroupNode getActivationGroupNode() {
        // TODO Auto-generated method stub
        return null;
    }

    public long getActivationNumber() {
        // TODO Auto-generated method stub
        return 0;
    }

    public AgendaGroup getAgendaGroup() {
        // TODO Auto-generated method stub
        return null;
    }

    public LinkedList getLogicalDependencies() {
        // TODO Auto-generated method stub
        return null;
    }

    public PropagationContext getPropagationContext() {
        // TODO Auto-generated method stub
        return null;
    }

    public Rule getRule() {
        // TODO Auto-generated method stub
        return null;
    }

    public Consequence getConsequence() {
        // TODO Auto-generated method stub
        return null;
    }

    public ActivationNode getActivationNode() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getSalience() {
        // TODO Auto-generated method stub
        return 0;
    }

    public GroupElement getSubRule() {
        // TODO Auto-generated method stub
        return null;
    }

    public LeftTupleImpl getTuple() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isActivated() {
        // TODO Auto-generated method stub
        return false;
    }

    public void remove() {
        // TODO Auto-generated method stub
        
    }

    public void setActivated(boolean activated) {
        // TODO Auto-generated method stub
        
    }

    public void setActivationGroupNode(ActivationGroupNode activationGroupNode) {
        // TODO Auto-generated method stub
        
    }

    public void setLogicalDependencies(LinkedList<LogicalDependency> justified) {
        // TODO Auto-generated method stub
        
    }

    public void setActivationNode(ActivationNode ruleFlowGroupNode) {
        // TODO Auto-generated method stub
        
    }

    public List<FactHandle> getFactHandles() {
        // TODO Auto-generated method stub
        return null;
    }

    public List<Object> getObjects() {
        // TODO Auto-generated method stub
        return null;
    }

    public Object getDeclarationValue(String variableName) {
        // TODO Auto-generated method stub
        return null;
    }

    public List<String> getDeclarationIDs() {
        // TODO Auto-generated method stub
        return null;
    }

    public InternalFactHandle getFactHandle() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isAdded() {
        // TODO Auto-generated method stub
        return false;
    }
    
    public boolean isActive() {
        return isActivated();
    }

    public void addBlocked(LogicalDependency node) {
        // TODO Auto-generated method stub
        
    }

    public LinkedList getBlocked() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setBlocked(LinkedList<LogicalDependency> justified) {
        // TODO Auto-generated method stub
        
    }

    public void addBlocked(LinkedListNode node) {
        // TODO Auto-generated method stub
        
    }

    public LinkedList getBlockers() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean isMatched() {
        // TODO Auto-generated method stub
        return false;
    }

    public void setMatched(boolean matched) {
        // TODO Auto-generated method stub
        
    }    
}
