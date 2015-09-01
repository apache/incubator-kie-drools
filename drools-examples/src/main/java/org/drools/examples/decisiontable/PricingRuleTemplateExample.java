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

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * This shows off a rule template where the data provider is a spreadsheet.
 * This example uses the same spreadsheet as the Decision table example ({@link PricingRuleDTExample})
 * so that you can see the difference between the two.
 * 
 * Note that even though they  use the same spreadsheet, this example is just
 * concerned with the data cells and does not use any of the Decision Table data.
 */
public class PricingRuleTemplateExample {

    public static void main(String[] args) {
        KieContainer kc = KieServices.Factory.get().getKieClasspathContainer();
        execute( kc );
    }

    public static void execute( KieContainer kc ) {
        KieSession ksession = kc.newKieSession( "DTableWithTemplateKS" );

        //now create some test data
        Driver driver = new Driver();
        Policy policy = new Policy();

        ksession.insert(driver);
        ksession.insert(policy);

        ksession.fireAllRules();

        System.out.println("BASE PRICE IS: " + policy.getBasePrice());
        System.out.println("DISCOUNT IS: " + policy.getDiscountPercent());

        ksession.dispose();

        policy.getBasePrice();
    }
}
