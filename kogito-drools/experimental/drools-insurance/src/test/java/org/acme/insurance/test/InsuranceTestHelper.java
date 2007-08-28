package org.acme.insurance.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.agent.RuleAgent;
import org.drools.compiler.PackageBuilder;

public class InsuranceTestHelper {
	private RuleBase rulebase;
	private StatefulSession session;

	public StatefulSession getSession() {
		try { 
			//rulebase = loadRuleBaseFromRuleAgent();
            rulebase = loadRuleBaseFromDRL();
            session = rulebase.newStatefulSession();
			
            session.startProcess( "insuranceProcess" );
			
			return session;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public RuleBase loadRuleBaseFromRuleAgent() {
		RuleAgent agent = RuleAgent
				.newRuleAgent("/brmsdeployedrules.properties");
		RuleBase rulebase = agent.getRuleBase();
		return rulebase;
	}
	

	private RuleBase loadRuleBaseFromDRL() throws Exception {

		PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl(getTechnicalRules("/approval/insurancefactor.drl"));
        builder.addPackageFromDrl(getTechnicalRules("/approval/approval.drl"));
        builder.addPackageFromDrl(getTechnicalRules("/approval/calculateInsurance.drl"));
        builder.addPackageFromDrl(getTechnicalRules("/approval/marginalage.dslr"));
        builder.addRuleFlow( getTechnicalRules( "/approval/insurance-process.rfm" ) );
        
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(builder.getPackage());
		return ruleBase;
	}

	private Reader getTechnicalRules(String name) {

		InputStream stream = this.getClass().getResourceAsStream(
				name);
		
		return new InputStreamReader(stream);

	}

}
