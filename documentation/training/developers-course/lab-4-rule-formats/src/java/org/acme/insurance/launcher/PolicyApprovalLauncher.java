package org.acme.insurance.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;

/**
 * Sample file for launching rules with a DSL.
 * @author Michael Neale
 */
public class PolicyApprovalLauncher {

	public static void main(String[] args) throws Exception {
    	PolicyApprovalLauncher launcher = new PolicyApprovalLauncher();
    	launcher.executeExample();		
	}

	private void executeExample() throws Exception {

		RuleBase ruleBase = loadRuleBase();
		
		testUnsafeDriver(ruleBase);
		testSafeMature(ruleBase);
		testUnsafeAreaAndPriors(ruleBase);
		
	}

	/**
	 * This shows how rules are loaded up from multiple files.
	 * Some are technical, and some are DSL based.
	 */
	private RuleBase loadRuleBase() throws DroolsParserException, IOException, Exception {
		PackageBuilder builder = new PackageBuilder();
		
		//this loads the DSL business rules
		builder.addPackageFromDrl(getMainRules(), getDSL());
		
		//loads the technical rules
		builder.addPackageFromDrl(getTechnicalRules());
		
		//package it all up
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage(builder.getPackage());
		return ruleBase;
		
		//note there is a utlity class called
		//'RuleBaseLoader' which can do the above in one hit, but only 
		//for 1 file per rulebase.
	}

	
	///////////////////////////////////////////////////
	//  The trial scenarios follow below
	///////////////////////////////////////////////////
	
	
	private void testUnsafeDriver(RuleBase ruleBase) {
		WorkingMemory wm = ruleBase.newWorkingMemory();
		
		Driver driver = new Driver();
		driver.setPriorClaims(new Integer(4));
		Policy policy = new Policy();
		policy.setType("COMPREHENSIVE");
		policy.setApproved(false);
		
		wm.assertObject(driver);
		wm.assertObject(policy);
		
		wm.fireAllRules();
		
		System.out.println("Policy approved: " + policy.isApproved());
	}		

	private void testSafeMature(RuleBase ruleBase) {
		WorkingMemory wm = ruleBase.newWorkingMemory();
		
		Driver driver = new Driver();
		driver.setPriorClaims(new Integer(0));
		driver.setAge(new Integer(45));
		
		Policy policy = new Policy();
		policy.setType("COMPREHENSIVE");
		policy.setApproved(false);
		
		wm.assertObject(driver);
		wm.assertObject(policy);
		
		wm.fireAllRules();
		
		System.out.println("Policy approved: " + policy.isApproved());
	}		
	
	private void testUnsafeAreaAndPriors(RuleBase ruleBase) {
		WorkingMemory wm = ruleBase.newWorkingMemory();
		
		Driver driver = new Driver();
		driver.setPriorClaims(new Integer(2));
		driver.setAge(new Integer(22));
		driver.setLocationRiskProfile("MED");
		
		Policy policy = new Policy();
		policy.setType("COMPREHENSIVE");
		policy.setApproved(false);
		
		wm.assertObject(driver);
		wm.assertObject(policy);
		
		wm.fireAllRules();
		
		System.out.println("Policy approved: " + policy.isApproved());
	}		

	
	////////////////////////////////////////////////////////////
	//	Loading from classpath, for convenience. 
	//  Feel free to change it to the file system if you like.
	////////////////////////////////////////////////////////////
	
	private Reader getMainRules() {
		InputStream stream = this.getClass()
				.getResourceAsStream("/approval/approval.drl");
		return new InputStreamReader(stream);
	}

	private Reader getTechnicalRules() {
		InputStream stream = this.getClass()
				.getResourceAsStream("/approval/technical.drl");
		return new InputStreamReader(stream);
	}
	
	private Reader getDSL() {
		InputStream stream = this.getClass()
				.getResourceAsStream("/approval/acme.dsl");
		return new InputStreamReader(stream);
	}
	
	
}
