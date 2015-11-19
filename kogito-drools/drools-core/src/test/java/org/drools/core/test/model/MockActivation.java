/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.test.model;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.LogicalDependency;
import org.drools.core.common.PhreakPropagationContext;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.rule.GroupElement;
import org.drools.core.spi.Activation;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListNode;
import org.kie.api.runtime.rule.FactHandle;

import java.util.List;

public class MockActivation<T extends ModedAssertion<T>>
        implements
        Activation<T> {

    private RuleImpl rule;

    public MockActivation( ) {
        this.rule = new RuleImpl();
    }

    public RuleImpl getRule() {
        return this.rule;
    }

    public Consequence getConsequence() {
        return getRule().getConsequence();
    }

    public int getSalience() {
        return 0;
    }

    public LeftTupleImpl getTuple() {
        return null;
    }

    public PropagationContext getPropagationContext() {
        return new PhreakPropagationContext();
    }

    public long getActivationNumber() {
        return 0;
    }

    public void remove() {
    }

    public void addLogicalDependency( final LogicalDependency<T> node ) {
    }

    public LinkedList<LogicalDependency<T>> getLogicalDependencies() {
        return null;
    }

    public boolean isQueued() {
        return false;
    }

    public void setQueued(final boolean activated) {
    }

    public ActivationGroupNode getActivationGroupNode() {
        return null;
    }

    public void setActivationGroupNode( final ActivationGroupNode activationGroupNode ) {
    }

    public GroupElement getSubRule() {
        return null;
    }

    public InternalAgendaGroup getAgendaGroup() {
        return null;
    }

    public InternalRuleFlowGroup getRuleFlowGroup() {
        return null;
    }

    public ActivationNode getActivationNode() {
        return null;
    }

    public void setActivationNode( final ActivationNode ruleFlowGroupNode ) {
    }
    public List<FactHandle> getFactHandles() {
        return null;
    }

    public List<Object> getObjects() {
        return null;
    }

    public Object getDeclarationValue( String variableName ) {
        return null;
    }

    public List<String> getDeclarationIds() {
        return null;
    }

    public InternalFactHandle getActivationFactHandle() {
        return null;
    }

    public boolean isAdded() {
        return false;
    }

    public void addBlocked(LogicalDependency node) {
    }

    public LinkedList getBlocked() {
        return null;
    }

    public void addBlocked(LinkedListNode node) {
    }

    public LinkedList getBlockers() {
        return null;
    }

    public boolean isMatched() {
        return false;
    }

    public void setMatched(boolean matched) { }

    public boolean isActive() {
        return false;
    }

    public void setActive(boolean active) { }

    public boolean isRuleAgendaItem() {
        return false;
    }

    @Override
    public void setBlocked(LinkedList<LogicalDependency<SimpleMode>> justified) {

    }

    @Override
    public void setLogicalDependencies(LinkedList<LogicalDependency<T>> justified) {

    }

    @Override
    public void setQueueIndex(int index) {
    }

    @Override
    public int getQueueIndex() {
        return 0;
    }

    @Override
    public void dequeue() {
    }
}
