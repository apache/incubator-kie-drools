package org.drools.agent;

import org.drools.RuleBaseConfiguration;

public class MockRuleAgent extends RuleAgent {

    
    public boolean refreshCalled = false;
    
    public MockRuleAgent() {
        super( new RuleBaseConfiguration() );
    }

    public void refreshRuleBase() {
        refreshCalled = true;
        
    }

    
    
}
