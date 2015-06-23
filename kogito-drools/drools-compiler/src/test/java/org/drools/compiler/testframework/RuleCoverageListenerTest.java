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

package org.drools.compiler.testframework;

import org.drools.core.beliefsystem.ModedAssertion;
import org.drools.core.beliefsystem.simple.SimpleMode;
import org.kie.api.runtime.rule.FactHandle;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.core.common.LogicalDependency;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.event.rule.impl.AfterActivationFiredEventImpl;
import org.drools.core.reteoo.LeftTupleImpl;
import org.drools.core.rule.GroupElement;
import org.drools.core.spi.Activation;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.PropagationContext;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListNode;
import org.junit.Test;
import org.kie.internal.runtime.beliefs.Mode;

import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.*;

public class RuleCoverageListenerTest {

    @Test
    public void testCoverage() throws Exception {
        HashSet<String> rules = new HashSet<String>();
        rules.add( "rule1" );
        rules.add( "rule2" );
        rules.add( "rule3" );

        RuleCoverageListener ls = new RuleCoverageListener( rules );
        assertEquals( 3,
                      ls.rules.size() );
        assertEquals( 0,
                      ls.getPercentCovered() );

        ls.afterMatchFired(new AfterActivationFiredEventImpl(new MockActivation("rule1"), null));
        assertEquals( 2,
                      ls.rules.size() );
        assertTrue( ls.rules.contains( "rule2" ) );
        assertTrue( ls.rules.contains( "rule3" ) );
        assertFalse( ls.rules.contains( "rule1" ) );
        assertEquals( 33,
                      ls.getPercentCovered() );

        ls.afterMatchFired(new AfterActivationFiredEventImpl(new MockActivation("rule2"), null));
        assertEquals( 1,
                      ls.rules.size() );
        assertFalse( ls.rules.contains( "rule2" ) );
        assertFalse( ls.rules.contains( "rule1" ) );
        assertTrue( ls.rules.contains( "rule3" ) );

        assertEquals( 66,
                      ls.getPercentCovered() );

        ls.afterMatchFired( new AfterActivationFiredEventImpl( new MockActivation( "rule3" ), null ));
        assertEquals( 0,
                      ls.rules.size() );
        assertFalse( ls.rules.contains( "rule2" ) );
        assertFalse( ls.rules.contains( "rule1" ) );
        assertFalse( ls.rules.contains( "rule3" ) );

        assertEquals( 100,
                      ls.getPercentCovered() );

    }

}

@SuppressWarnings("serial")
class MockActivation<T extends ModedAssertion<T>>
    implements
    Activation<T> {
    private String ruleName;

    public MockActivation(String ruleName) {
        this.ruleName = ruleName;
    }

    public void addLogicalDependency(LogicalDependency<T> node) {
    }

    public ActivationGroupNode getActivationGroupNode() {
        return null;
    }

    public long getActivationNumber() {
        return 0;
    }

    public InternalAgendaGroup getAgendaGroup() {
        return null;
    }

    public InternalRuleFlowGroup getRuleFlowGroup() {
        return null;
    }

    public LinkedList<LogicalDependency<T>> getLogicalDependencies() {
        return null;
    }

    public PropagationContext getPropagationContext() {
        return null;
    }

    public RuleImpl getRule() {
        return new RuleImpl( ruleName );
    }

    public Consequence getConsequence() {
        return getRule().getConsequence();
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

    public boolean isQueued() {
        return false;
    }

    public void remove() {
    }

    public void setQueued(boolean activated) {
    }

    public void setActivationGroupNode(ActivationGroupNode activationGroupNode) {
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
    public void addBlocked(LogicalDependency<SimpleMode> node) {

    }

    @Override
    public LinkedList<LogicalDependency<SimpleMode>> getBlocked() {
        return null;
    }

    @Override
    public void setBlocked(LinkedList<LogicalDependency<SimpleMode>> justified) {

    }

    @Override
    public LinkedList<SimpleMode> getBlockers() {
        return null;
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
