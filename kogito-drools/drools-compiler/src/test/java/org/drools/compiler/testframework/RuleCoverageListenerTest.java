package org.drools.compiler.testframework;

import java.util.HashSet;
import java.util.List;

import org.drools.core.spi.Consequence;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.FactHandle;
import org.drools.core.common.ActivationGroupNode;
import org.drools.core.common.ActivationNode;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.LogicalDependency;
import org.drools.core.util.LinkedList;
import org.drools.core.util.LinkedListNode;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.reteoo.LeftTupleImpl;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.core.spi.Activation;
import org.drools.core.spi.AgendaGroup;
import org.drools.core.spi.PropagationContext;

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

        ls.afterActivationFired( new AfterActivationFiredEvent( new MockActivation( "rule1" ) ),
                                 null );
        assertEquals( 2,
                      ls.rules.size() );
        assertTrue( ls.rules.contains( "rule2" ) );
        assertTrue( ls.rules.contains( "rule3" ) );
        assertFalse( ls.rules.contains( "rule1" ) );
        assertEquals( 33,
                      ls.getPercentCovered() );

        ls.afterActivationFired( new AfterActivationFiredEvent( new MockActivation( "rule2" ) ),
                                 null );
        assertEquals( 1,
                      ls.rules.size() );
        assertFalse( ls.rules.contains( "rule2" ) );
        assertFalse( ls.rules.contains( "rule1" ) );
        assertTrue( ls.rules.contains( "rule3" ) );

        assertEquals( 66,
                      ls.getPercentCovered() );

        ls.afterActivationFired( new AfterActivationFiredEvent( new MockActivation( "rule3" ) ),
                                 null );
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
class MockActivation
    implements
    Activation {
    private String ruleName;

    public MockActivation(String ruleName) {
        this.ruleName = ruleName;
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
        return new Rule( ruleName );
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
        // TODO Auto-generated method stub
        
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
        // TODO Auto-generated method stub
    }

    public boolean isRuleNetworkEvaluatorActivation() {
        return false;
    }        

}
