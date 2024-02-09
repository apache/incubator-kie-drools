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
package org.drools.mvel.compiler.testframework;

import java.util.HashSet;
import java.util.List;

import org.drools.compiler.testframework.RuleCoverageListener;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.InternalAgendaGroup;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalRuleFlowGroup;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.event.rule.impl.AfterActivationFiredEventImpl;
import org.drools.core.phreak.RuleAgendaItem;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.base.rule.consequence.Consequence;
import org.drools.core.common.PropagationContext;
import org.junit.Test;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleCoverageListenerTest {

    @Test
    public void testCoverage() throws Exception {
        HashSet<String> rules = new HashSet<String>();
        rules.add( "rule1" );
        rules.add( "rule2" );
        rules.add( "rule3" );

        RuleCoverageListener ls = new RuleCoverageListener( rules );
        assertThat(ls.getRules().size()).isEqualTo(3);
        assertThat(ls.getPercentCovered()).isEqualTo(0);

        ls.afterMatchFired(new AfterActivationFiredEventImpl(new MockInternalMatch("rule1"), null, null));
        assertThat(ls.getRules().size()).isEqualTo(2);
        assertThat(ls.getRules().contains("rule2")).isTrue();
        assertThat(ls.getRules().contains("rule3")).isTrue();
        assertThat(ls.getRules().contains("rule1")).isFalse();
        assertThat(ls.getPercentCovered()).isEqualTo(33);

        ls.afterMatchFired(new AfterActivationFiredEventImpl(new MockInternalMatch("rule2"), null, null));
        assertThat(ls.getRules().size()).isEqualTo(1);
        assertThat(ls.getRules().contains("rule2")).isFalse();
        assertThat(ls.getRules().contains("rule1")).isFalse();
        assertThat(ls.getRules().contains("rule3")).isTrue();

        assertThat(ls.getPercentCovered()).isEqualTo(66);

        ls.afterMatchFired( new AfterActivationFiredEventImpl(new MockInternalMatch("rule3" ), null , null));
        assertThat(ls.getRules().size()).isEqualTo(0);
        assertThat(ls.getRules().contains("rule2")).isFalse();
        assertThat(ls.getRules().contains("rule1")).isFalse();
        assertThat(ls.getRules().contains("rule3")).isFalse();

        assertThat(ls.getPercentCovered()).isEqualTo(100);

    }

}

@SuppressWarnings("serial")
class MockInternalMatch implements InternalMatch {
    private String ruleName;

    public MockInternalMatch(String ruleName) {
        this.ruleName = ruleName;
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

    public TupleImpl getTuple() {
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

    @Override
    public Runnable getCallback() {
        return null;
    }

    @Override
    public void setCallback(Runnable callback) {

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

    public InternalFactHandle getActivationFactHandle() {
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

    @Override
    public void setActivationFactHandle(InternalFactHandle factHandle) {

    }

    public boolean isRuleAgendaItem() {
        return false;
    }

    @Override
    public void dequeue() {
    }

    @Override
    public int getQueueIndex() {
        return 0;
    }

    @Override
    public void setQueueIndex(int index) {

    }

    @Override
    public RuleAgendaItem getRuleAgendaItem() {
        return null;
    }

    @Override
    public TerminalNode getTerminalNode() {
        return null;
    }

    @Override
    public String toExternalForm() {
        return null;
    }
}
