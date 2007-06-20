package org.drools.agent;

import java.util.Properties;

import org.drools.RuleBase;

public class RuleBaseAgentCache {

    
    private static final RuleBaseAgentCache INSTANCE = new RuleBaseAgentCache();
    
    private RuleBaseAgentCache() {
    }
    
    public static RuleBaseAgentCache instance() {
        return INSTANCE;
    }
    
    /**
     * Return a rulebase by name.
     * This name may be the name of a pre configured rulebase, 
     * or the name of a config properties file to be found
     * on the classpath.
     */
    public RuleBase getRuleBase(String name) {
        throw new UnsupportedOperationException("Not done yet !");
    }

    
    /** 
     * Pass in a pre populated properties file.
     * It will then map this config to the given name for future use.
     * @return A RuleBase ready to go. 
     */
    public RuleBase configureRuleBase(String name, Properties props) {
        return null;
    }
    
    
    
}
