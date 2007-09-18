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
    	
    	if (rulebase == null ) { 
	        try {
	        	rulebase = loadRuleBaseFromRuleAgent();
	        } catch ( Exception e ) {
	            e.printStackTrace();
	        }
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


// Use the following methods to load the rulebase from the drl files    
    
//    private RuleBase loadRuleBaseFromDRL() throws Exception {
//
//        PackageBuilder builder = new PackageBuilder();
//        builder.addPackageFromDrl( getTechnicalRules( "/approval/insurancefactor.drl" ) );
//        builder.addPackageFromDrl( getTechnicalRules( "/approval/approval.drl" ) );
//        builder.addPackageFromDrl( getTechnicalRules( "/approval/calculateInsurance.drl" ) );
//        builder.addPackageFromDrl( getTechnicalRules( "/approval/marginalage.dslr" ),
//                                   getTechnicalRules( "/approval/acme.dsl" ) );
//        builder.addRuleFlow( getTechnicalRules( "/approval/insurance-process.rfm" ) );
//
//        
//
//        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
//        ruleBase.addPackage( builder.getPackage() );
//        return ruleBase;
//    }

//    private Reader getTechnicalRules(String name) {
//
//        InputStream stream = this.getClass().getResourceAsStream( name );
//
//        return new InputStreamReader( stream );
//
//    }

}
