package org.drools.integrationtests.waltz;

import org.drools.RuleBase;

public class LeapsWaltzTest extends Waltz {
    protected RuleBase getRuleBase() throws Exception {
        return new org.drools.leaps.RuleBaseImpl();
    }

}
