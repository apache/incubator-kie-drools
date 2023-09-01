package org.acme.insurance.launcher;

import java.io.InputStream;

import org.acme.insurance.Driver;
import org.acme.insurance.Policy;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

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
        dtconf.setInputType(DecisionTableInputType.XLS);

        kbuilder.add(ResourceFactory.newClassPathResource("/data/ExamplePolicyPricing.drl.xls", getClass()),
                              ResourceType.DTABLE,
                              dtconf);

        if (kbuilder.hasErrors()) {
            throw new RuntimeException(kbuilder.getErrors().toString());
        }

        //BUILD RULEBASE
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        //NEW WORKING MEMORY
        final KieSession session = kbase.newKieSession();

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        session.insert(driver);
        session.insert(policy);

        session.fireAllRules();

        System.out.println("BASE PRICE IS: " + policy.getBasePrice());
        System.out.println("DISCOUNT IS: " + policy.getDiscountPercent());

        return policy.getBasePrice();

    }

    private InputStream getSpreadsheetStream() {
        return this.getClass().getResourceAsStream("/data/ExamplePolicyPricing.drl.xls");
    }

}
