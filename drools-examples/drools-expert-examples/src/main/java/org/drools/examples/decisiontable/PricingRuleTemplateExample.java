/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.examples.decisiontable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;

import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.decisiontable.ExternalSpreadsheetCompiler;
import org.drools.io.ResourceFactory;
import org.drools.io.impl.ByteArrayResource;
import org.drools.runtime.StatefulKnowledgeSession;

/**
 * This shows off a rule template where the data provider is a spreadsheet.
 * This example uses the same spreadsheet as the Decision table example ({@link PricingRuleDTExample})
 * so that you can see the difference between the two.
 * 
 * Note that even though they  use the same spreadsheet, this example is just
 * concerned with the data cells and does not use any of the Decision Table data.
 */
public class PricingRuleTemplateExample {

    public static void main(String[] args) throws Exception {
        PricingRuleTemplateExample launcher = new PricingRuleTemplateExample();
        launcher.executeExample();
    }

    private int executeExample() throws Exception {

        //BUILD THE KBASE
        KnowledgeBase kbase = this.buildKBase();

        //GET A KSESSION
        StatefulKnowledgeSession ksession = kbase.newStatefulKnowledgeSession();

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        ksession.insert(driver);
        ksession.insert(policy);

        ksession.fireAllRules();

        System.out.println("BASE PRICE IS: " + policy.getBasePrice());
        System.out.println("DISCOUNT IS: " + policy.getDiscountPercent());


        ksession.dispose();

        return policy.getBasePrice();

    }

    /**
     * Creates a new kbase containing the rules generated from the xls file and
     * the templates.
     * @return
     * @throws IOException
     */
    private KnowledgeBase buildKBase() throws IOException {
        //first we compile the decision table into a whole lot of rules.
        final ExternalSpreadsheetCompiler converter = new ExternalSpreadsheetCompiler();

        //the data we are interested in starts at row 10, column 3
        String basePricingDRL = converter.compile(getSpreadsheetStream(), getBasePricingRulesStream(), 10, 3);
        //the data we are interested in starts at row 30, column 3
        String promotionalPricingDRL = converter.compile(getSpreadsheetStream(), getPromotionalPricingRulesStream(), 30, 3);

        //compile the drls
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(new ByteArrayResource(basePricingDRL.getBytes()), ResourceType.DRL);
        kbuilder.add(new ByteArrayResource(promotionalPricingDRL.getBytes()), ResourceType.DRL);

        //compilation errors?
        if (kbuilder.hasErrors()) {
            System.out.println("Error compiling resources:");
            Iterator<KnowledgeBuilderError> errors = kbuilder.getErrors().iterator();
            while (errors.hasNext()) {
                System.out.println("\t" + errors.next().getMessage());
            }
            throw new IllegalStateException("Error compiling resources");
        }


        //Uncomment to see the base pricing rules
        //System.out.println(basePricingDRL);
        //Uncomment to see the promotional pricing rules
        //System.out.println(promotionalPricingDRL);

        //BUILD KBASE
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());

        return kbase;

    }

    private InputStream getSpreadsheetStream() throws IOException {
        return ResourceFactory.newClassPathResource("org/drools/examples/decisiontable/ExamplePolicyPricing.xls").getInputStream();
    }

    private InputStream getBasePricingRulesStream() throws IOException {
        return ResourceFactory.newClassPathResource("org/drools/examples/decisiontable/BasePricing.drt").getInputStream();
    }

    private InputStream getPromotionalPricingRulesStream() throws IOException {
        return ResourceFactory.newClassPathResource("org/drools/examples/decisiontable/PromotionalPricing.drt").getInputStream();
    }
}
