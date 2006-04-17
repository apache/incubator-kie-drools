package org.drools;

import junit.framework.TestCase;

public class RuleBaseFactoryTest extends TestCase {

    public void testReteOO() {
        RuleBase rb = RuleBaseFactory.getInstance().newRuleBase();
        assertTrue( rb instanceof org.drools.reteoo.RuleBaseImpl );

        RuleBase rb2 = RuleBaseFactory.getInstance().newRuleBase( RuleBase.RETEOO );

        assertTrue( rb2 instanceof org.drools.reteoo.RuleBaseImpl );
        assertNotSame( rb2,
                       rb );
    }

    public void testLeaps() {
        RuleBase rb = RuleBaseFactory.getInstance().newRuleBase( RuleBase.LEAPS );
        assertTrue( rb instanceof org.drools.leaps.RuleBaseImpl );
        RuleBase rb2 = RuleBaseFactory.getInstance().newRuleBase( RuleBase.LEAPS );
        assertTrue( rb2 instanceof org.drools.leaps.RuleBaseImpl );
        assertNotSame( rb2,
                       rb );
    }

}
