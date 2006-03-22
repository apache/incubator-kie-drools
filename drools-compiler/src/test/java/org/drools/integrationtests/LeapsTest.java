package org.drools.integrationtests;

import org.drools.RuleBase;

/** This runs the integration test cases with the leaps implementation */
public class LeapsTest extends IntegrationCases {

    protected RuleBase getRuleBase() throws Exception {
        return new org.drools.leaps.RuleBaseImpl();
    }

    public void testExists() throws Exception {
    }

    public void testQuery() throws Exception {
    }

}
