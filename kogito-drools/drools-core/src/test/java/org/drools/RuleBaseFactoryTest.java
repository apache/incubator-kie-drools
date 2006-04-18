package org.drools;
/*
 * Copyright 2005 JBoss Inc
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