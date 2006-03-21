package org.drools.integrationtests;

import org.drools.RuleBase;
import org.drools.reteoo.RuleBaseImpl;

/** Run all the tests with the ReteOO engine implementation */
public class ReteTest extends IntegrationCases {

    protected RuleBase getRuleBase() throws Exception {
        
        return new RuleBaseImpl();
    }

}
