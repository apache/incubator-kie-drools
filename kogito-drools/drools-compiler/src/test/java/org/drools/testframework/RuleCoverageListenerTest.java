package org.drools.testframework;

import java.util.HashSet;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.drools.FactHandle;
import org.drools.common.ActivationGroupNode;
import org.drools.common.ActivationNode;
import org.drools.common.InternalFactHandle;
import org.drools.common.LogicalDependency;
import org.drools.core.util.LinkedList;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.reteoo.LeftTupleImpl;
import org.drools.rule.GroupElement;
import org.drools.rule.Rule;
import org.drools.spi.Activation;
import org.drools.spi.AgendaGroup;
import org.drools.spi.PropagationContext;
import org.drools.spi.Tuple;

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

    public void setLogicalDependencies(LinkedList justified) {
    }

    public void setActivationNode(ActivationNode ruleFlowGroupNode) {
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

}
