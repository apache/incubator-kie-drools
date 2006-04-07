package org.drools.integrationtests.waltz;

import org.drools.RuleBase;
import org.drools.reteoo.RuleBaseImpl;

public class ReteOOWaltzTest extends Waltz {
    protected RuleBase getRuleBase() throws Exception {
        
        return new RuleBaseImpl();
    }

}
