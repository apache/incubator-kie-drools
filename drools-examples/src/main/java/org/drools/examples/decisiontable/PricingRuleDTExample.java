package org.drools.examples.decisiontable;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

import java.util.Arrays;

/**
 * This shows off a decision table.
 */
public class PricingRuleDTExample {

    public static final void main(String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        System.out.println(kc.verify().getMessages().toString());
        execute( kc );
    }

    public static void execute( KieContainer kc ) {
        StatelessKieSession ksession = kc.newStatelessKieSession( "DecisionTableKS");

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        ksession.execute( Arrays.asList(driver, policy));

        System.out.println( "BASE PRICE IS: " + policy.getBasePrice() );
        System.out.println( "DISCOUNT IS: " + policy.getDiscountPercent() );

        policy.getBasePrice();
    }

}
