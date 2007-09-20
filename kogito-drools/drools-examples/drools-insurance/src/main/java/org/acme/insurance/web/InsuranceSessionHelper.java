package org.acme.insurance.web;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.agent.RuleAgent;

public class InsuranceSessionHelper {
    private static RuleBase        rulebase;
    private static StatefulSession session;
    
    private InsuranceSessionHelper(){
    }

    public static StatefulSession getSession() {
    	
    	if ( rulebase == null ) { 
    		rulebase = loadRuleBaseFromRuleAgent();
    	}
    	
        session = rulebase.newStatefulSession();
        session.startProcess( "insuranceProcess" );
        return session;
    }

    private static RuleBase loadRuleBaseFromRuleAgent() {
        RuleAgent agent = RuleAgent.newRuleAgent( "/brmsdeployedrules.properties" );
        RuleBase rulebase = agent.getRuleBase();
        return rulebase;
    }
}
