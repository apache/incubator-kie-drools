package org.drools.agent;

import java.util.Properties;

import org.drools.RuleBase;

public class RuleBaseAgent {

    
    private RuleBaseAgent() {
        
    }
    
    public static RuleBaseAgent instance() {
        return null;
    }
    
    /**
     * Return a rulebase by name to its config file.
     */
    public RuleBase getRuleBase(String name) {
        throw new UnsupportedOperationException("Not done yet !");
    }

    
    /** Pass in a pre populated properties file */
    public RuleBase getRuleBase(Properties props) {
        return null;
    }
    
    
    
}
