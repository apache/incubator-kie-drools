package org.acme.insurance.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.DecisionTableConfiguration;
import org.kie.builder.DecisionTableInputType;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.io.ResourceFactory;
import org.kie.io.ResourceType;
import org.kie.runtime.StatefulKnowledgeSession;

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

        kbuilder.add( ResourceFactory.newClassPathResource( "/data/ExamplePolicyPricing.xls", getClass() ),
                              ResourceType.DTABLE,
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
