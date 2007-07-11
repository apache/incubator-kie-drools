package org.acme.insurance;

import java.io.IOException;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.agent.RuleAgent;
import org.drools.concurrent.Future;

public class InsuranceBusiness {
	
	public void executeExample() throws Exception {
		RuleBase ruleBase = loadRuleBase();

//		testUnsafeDriver(ruleBase);
		testSafeMature(ruleBase);
//		testUnsafeAreaAndPriors(ruleBase);
	}

	/**
	 * This shows how rules are loaded up from a deployed package.
	 */
	private RuleBase loadRuleBase() throws IOException, Exception {

		RuleAgent agent = RuleAgent.newRuleAgent("/brmsdeployedrules.properties");
		RuleBase rb = agent.getRuleBase(); 

		return rb;
	}

	private void testUnsafeDriver(RuleBase ruleBase) {
		StatefulSession session = ruleBase.newStatefulSession();
		
		Driver driver = new Driver();
		driver.setPriorClaims(new Integer(4));
		Policy policy = new Policy();
		policy.setType("COMPREHENSIVE");
		policy.setApproved(false);
		
		
        Future futureAssertDrv = session.asyncInsert( driver );
        Future futureAssertPol = session.asyncInsert( policy );
        Future futureFireAllRules = session.asyncFireAllRules();

        System.out.println("Policy approved: " + policy.isApproved());
	}		

	private void testSafeMature(RuleBase ruleBase) {
		StatefulSession session = ruleBase.newStatefulSession();
		
		Driver driver = new Driver();
		driver.setPriorClaims(new Integer(0));
		driver.setAge(new Integer(45));
		
		Policy policy = new Policy();
		policy.setType("COMPREHENSIVE");
		policy.setApproved(false);
		
        Future futureAssertDrv = session.asyncInsert( driver );
        Future futureAssertPol = session.asyncInsert( policy );
        Future futureFireAllRules = session.asyncFireAllRules();
	}		
	
	private void testUnsafeAreaAndPriors(RuleBase ruleBase) {
		StatefulSession session = ruleBase.newStatefulSession();
		
		Driver driver = new Driver();
		driver.setPriorClaims(new Integer(2));
		driver.setAge(new Integer(55));
		driver.setLocationRiskProfile("LOW");
		
		Policy policy = new Policy();
		policy.setType("COMPREHENSIVE");
		policy.setApproved(false);
		
        Future futureAssertDrv = session.asyncInsert( driver );
        Future futureAssertPol = session.asyncInsert( policy );
        Future futureFireAllRules = session.asyncFireAllRules();
	}
	
}
