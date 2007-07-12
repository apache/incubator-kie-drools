package org.drools.examples;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.StatelessSession;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.examples.decisiontable.Driver;
import org.drools.examples.decisiontable.Policy;

/**
 * This shows off a decision table.
 */
public class PricingRuleDTExample {

    public static final void main(String[] args) throws Exception {    	
    	PricingRuleDTExample launcher = new PricingRuleDTExample();
    	launcher.executeExample();
    }
    
    public int executeExample() throws Exception {
    	
    	//first we compile the decision table into a whole lot of rules.
    	SpreadsheetCompiler compiler = new SpreadsheetCompiler();
    	String drl = compiler.compile(getSpreadsheetStream(), InputType.XLS);

    	//UNCOMMENT ME TO SEE THE DRL THAT IS GENERATED
    	//System.out.println(drl);

    	RuleBase ruleBase = buildRuleBase(drl);
    	
        // typical decision tables are used statelessly
		StatelessSession session = ruleBase.newStatelessSession();
		
		//now create some test data
		Driver driver = new Driver();
		Policy policy = new Policy();
		
        session.execute( new Object[] { driver, policy } );
		
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
    	return this.getClass().getResourceAsStream("ExamplePolicyPricing.xls");
	}



    
}
