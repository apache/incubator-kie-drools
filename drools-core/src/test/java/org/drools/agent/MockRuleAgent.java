package org.drools.agent;

import java.util.Properties;

public class MockRuleAgent extends RuleAgent {

    
    public boolean refreshCalled = false;
    


    public void refreshRuleBase() {
        refreshCalled = true;
        
    }

    
    
}
