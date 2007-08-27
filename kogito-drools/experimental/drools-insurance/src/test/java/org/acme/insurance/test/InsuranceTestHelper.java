package org.acme.insurance.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.audit.WorkingMemoryFileLogger;
import org.drools.audit.WorkingMemoryLogger;
import org.drools.compiler.PackageBuilder;

public class InsuranceTestHelper {
	private RuleBase rulebase;
	private StatefulSession session;

	public StatefulSession getSession() {
		try { 
			rulebase = loadRuleBase();
			session = rulebase.newStatefulSession();
			
			session.setFocus("risk assessment");
			
			return session;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private RuleBase loadRuleBase() throws Exception {

		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl(getTechnicalRules("/approval/raw.drl"));
		builder.addPackageFromDrl(getTechnicalRules("/approval/approval.drl"));
		builder.addPackageFromDrl(getTechnicalRules("/approval/calculateInsurance.drl"));
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
