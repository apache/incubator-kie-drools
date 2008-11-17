package org.acme.insurance.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * This is a sample file to launch a rule package from a rule source file.
 */
public class PricingRuleLauncher {

    public static final void main(String[] args) throws Exception {
        PricingRuleLauncher launcher = new PricingRuleLauncher();
        launcher.executeExample();
    }

    public int executeExample() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        DecisionTableConfiguration dtconf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtconf.setInputType( DecisionTableInputType.XLS );

        kbuilder.addResource( new InputStreamReader( getClass().getResourceAsStream( "/data/ExamplePolicyPricing.xls" ),
                                                     "windows-1252" ),
                              KnowledgeType.DTABLE,
                              dtconf );

        if ( kbuilder.hasErrors() ) {
            throw new RuntimeException( kbuilder.getErrors().toString() );
        }

        //BUILD RULEBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        //NEW WORKING MEMORY
        final StatefulKnowledgeSession session = kbase.newStatefulKnowledgeSession();

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        session.insert( driver );
        session.insert( policy );

        session.fireAllRules();

        System.out.println( "BASE PRICE IS: " + policy.getBasePrice() );
        System.out.println( "DISCOUNT IS: " + policy.getDiscountPercent() );

        return policy.getBasePrice();

    }

    /** Build the rule base from the generated DRL */
    private RuleBase buildRuleBase(String drl) throws DroolsParserException,
                                              IOException,
                                              Exception {
        //now we build the rule package and rulebase, as if they are normal rules
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader( drl ) );

        //add the package to a rulebase (deploy the rule package).
        RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );
        return ruleBase;
    }

    private InputStream getSpreadsheetStream() {
        return this.getClass().getResourceAsStream( "/data/ExamplePolicyPricing.xls" );
    }

}
