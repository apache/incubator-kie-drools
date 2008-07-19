package org.drools.examples;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.decisiontable.InputType;
import org.drools.examples.decisiontable.Driver;
import org.drools.examples.decisiontable.Policy;
import org.drools.template.parser.DataListener;
import org.drools.template.parser.TemplateDataListener;

/**
 * This shows off a rule template where the data provider is a spreadsheet.
 * This example uses the same spreadsheet as the Decision table example ({@link PricingRuleDTExample}) 
 * so that you can see the difference between the two.
 * 
 * Note that even though they  use the same spreadsheet, this example is just
 * concerned with the data cells and does not use any of the Decision Table data.
 * @author Steve
 *
 */
public class PricingRuleTemplateExample {
    public static void main(String[] args) throws Exception {
        PricingRuleTemplateExample launcher = new PricingRuleTemplateExample();
        launcher.executeExample();        
    }

    private int executeExample() throws Exception {
        
        //first we compile the decision table into a whole lot of rules.
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();
        final List<DataListener> listeners = new ArrayList<DataListener>();
        TemplateDataListener l1 = new TemplateDataListener(10, 3, getBasePricingRulesStream());
        listeners.add(l1);
        TemplateDataListener l2 = new TemplateDataListener(30, 3, getPromotionalPricingRulesStream());
        listeners.add(l2);
        converter.compile(getSpreadsheetStream(), InputType.XLS, listeners);

        String baseRules = l1.renderDRL();
        //Uncomment to see the base pricing rules
        //System.out.println(baseRules);
        String promotionalRules = l2.renderDRL();
        //Uncomment to see the promotional pricing rules
        //System.out.println(promotionalRules);
        //BUILD RULEBASE
        final RuleBase rb = buildRuleBase(baseRules, promotionalRules);

        WorkingMemory wm = rb.newStatefulSession();
        
        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();
        
        wm.insert(driver);
        wm.insert(policy);
        
        wm.fireAllRules();
        
        System.out.println("BASE PRICE IS: " + policy.getBasePrice());
        System.out.println("DISCOUNT IS: " + policy.getDiscountPercent( ));
        
        return policy.getBasePrice();
    }

    /** Build the rule base from the generated DRL */
    private RuleBase buildRuleBase(String... drls) throws DroolsParserException, IOException, Exception {
        //now we build the rule package and rulebase, as if they are normal rules
        PackageBuilder builder = new PackageBuilder();
        for ( String drl : drls ) {
            builder.addPackageFromDrl( new StringReader( drl ) );
        }
        
        //add the package to a rulebase (deploy the rule package).
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        return ruleBase;
    }

    private InputStream getSpreadsheetStream() {
        return this.getClass().getResourceAsStream("ExamplePolicyPricing.xls");
    }
    
    private InputStream getBasePricingRulesStream() {
        return this.getClass().getResourceAsStream("BasePricing.drt");
    }
    
    private InputStream getPromotionalPricingRulesStream() {
        return this.getClass().getResourceAsStream("PromotionalPricing.drt");
    }


}
