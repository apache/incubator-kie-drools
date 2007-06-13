package org.acme.insurance.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class PricingRuleLauncher {

    public static final void main(String[] args) throws Exception {    	
    	PricingRuleLauncher launcher = new PricingRuleLauncher();
    	launcher.executeExample();
    }
    
    public int executeExample() throws Exception {
    	
    	//first we compile the decision table into a whole lot of rules.
    	SpreadsheetCompiler compiler = new SpreadsheetCompiler();
    	String drl = compiler.compile(getSpreadsheetStream(), InputType.XLS);

    	//UNCOMMENT ME TO SEE THE DRL THAT IS GENERATED
    	//System.out.println(drl);

    	RuleBase ruleBase = buildRuleBase(drl);
    	
		WorkingMemory wm = ruleBase.newStatefulSession();
		
		//now create some test data
		Driver driver = new Driver();
		Policy policy = new Policy();
		
		wm.insert(driver);
		wm.insert(policy);
		
		wm.fireAllRules();
		
		System.out.println("BASE PRICE IS: " + policy.getBasePrice());
		System.out.println("DISCOUNT IS: " + policy.getDiscountPercent());
		
        return policy.getBasePrice();
    	
    }


    /** Build the rule base from the generated DRL */
	private RuleBase buildRuleBase(String drl) throws DroolsParserException, IOException, Exception {
		//now we build the rule package and rulebase, as if they are normal rules
		PackageBuilder builder = new PackageBuilder();
		builder.addPackageFromDrl( new StringReader(drl) );
		
		//add the package to a rulebase (deploy the rule package).
		RuleBase ruleBase = RuleBaseFactory.newRuleBase();
		ruleBase.addPackage( builder.getPackage() );
		return ruleBase;
	}
    
    


    private InputStream getSpreadsheetStream() {
    	return this.getClass().getResourceAsStream("/data/ExamplePolicyPricing.xls");
	}



    
}
