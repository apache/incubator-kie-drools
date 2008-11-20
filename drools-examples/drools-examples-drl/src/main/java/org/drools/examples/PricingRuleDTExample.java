package org.drools.examples;

import java.net.MalformedURLException;
import java.net.URL;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.DecisionTableConfiguration;
import org.drools.builder.DecisionTableInputType;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.KnowledgeType;
import org.drools.examples.decisiontable.Driver;
import org.drools.examples.decisiontable.Policy;
import org.drools.runtime.StatelessKnowledgeSession;

/**
 * This shows off a decision table.
 */
public class PricingRuleDTExample {

    public static final void main(String[] args) throws Exception {
        PricingRuleDTExample launcher = new PricingRuleDTExample();
        launcher.executeExample();
    }

    public int executeExample() throws Exception {

        DecisionTableConfiguration dtableconfiguration = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dtableconfiguration.setInputType( DecisionTableInputType.XLS );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();

        kbuilder.addResource( getSpreadsheetURL(),
                              KnowledgeType.DTABLE,
                              dtableconfiguration );

        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        // typical decision tables are used statelessly
        StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        ksession.executeObject( new Object[]{driver, policy} );

        System.out.println( "BASE PRICE IS: " + policy.getBasePrice() );
        System.out.println( "DISCOUNT IS: " + policy.getDiscountPercent() );

        return policy.getBasePrice();

    }

    private URL getSpreadsheetURL() throws MalformedURLException {
        return getClass().getResource( "ExamplePolicyPricing.xls" );
    }

}
