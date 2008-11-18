package org.drools.examples;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

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

        DecisionTableConfiguration conf = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        conf.setInputType( DecisionTableInputType.XLS );

        KnowledgeBuilder builder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        builder.addResource( new InputStreamReader( getSpreadsheetStream(),
                                                    "windows-1252" ),
                             KnowledgeType.DTABLE,
                             conf );

        KnowledgeBase knowledgeBase = KnowledgeBaseFactory.newKnowledgeBase();
        knowledgeBase.addKnowledgePackages( builder.getKnowledgePackages() );

        // typical decision tables are used statelessly
        StatelessKnowledgeSession session = knowledgeBase.newStatelessKnowledgeSession();

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        session.executeObject( new Object[]{driver, policy} );

        System.out.println( "BASE PRICE IS: " + policy.getBasePrice() );
        System.out.println( "DISCOUNT IS: " + policy.getDiscountPercent() );

        return policy.getBasePrice();

    }

    private InputStream getSpreadsheetStream() {
        return this.getClass().getResourceAsStream( "ExamplePolicyPricing.xls" );
    }

}
