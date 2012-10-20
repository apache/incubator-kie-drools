/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.drools.RuleBaseFactory;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.rule.EntryPoint;
import org.junit.Before;
import org.junit.Test;

/**
 * <p>
 * Verify that TMS will be lazily enabled after the first logical insert is
 * processed.
 * </p>
 * 
 * <p>
 * This is based on the ideas <a
 * href="http://blog.athico.com/2010/09/lazily-enabled-truth-maintenace.html">
 * published here.</a>
 * </p>
 */
public class LazyTMSEnablingTest {

    private ReteooWorkingMemory wm;
    private TruthMaintenanceSystem tms;

    @Before
    public void setUp() {

        wm = (ReteooWorkingMemory) RuleBaseFactory.newRuleBase()
                .newStatefulSession();

        tms = ((NamedEntryPoint)wm.getWorkingMemoryEntryPoint( EntryPoint.DEFAULT.getEntryPointId() ) ).getTruthMaintenanceSystem();

    }

    @Test
    public void shouldLazilyAdd() throws Exception {

        final String fact1 = "logical";

        wm.insert(fact1);

        assertEquals(
                "Shouldn't have anything, since no logical insert was performed.",
                0, tms.getEqualityKeyMap().size());

        final String fact2 = "logical";

        wm.insertLogical(fact2);

        assertEquals(
                "Now that a logical insert was done, it should have an element.",
                1, tms.getEqualityKeyMap().size());

        // Make sure the internals are fine.
        ObjectTypeConf typeConf = wm.getObjectTypeConfigurationRegistry()
                .getObjectTypeConf(wm.getEntryPoint(), fact1);

        assertTrue("Should have enabled TMS", typeConf.isTMSEnabled());

    }

    @Test
    public void shouldEnableTMSForSpecificType() throws Exception {

        final String stringFact1 = "toto";
        final String stringFact2 = "toto";
        final String anotherString = "tata";

        final Integer intFact1 = 99;
        final Integer intFact2 = 99;

        final Double doubleFact = 77.8;

        ObjectTypeConf stringTypeConf = wm.getObjectTypeConfigurationRegistry()
                .getObjectTypeConf(wm.getEntryPoint(), stringFact1);

        ObjectTypeConf intTypeConf = wm.getObjectTypeConfigurationRegistry()
                .getObjectTypeConf(wm.getEntryPoint(), intFact1);

        wm.insert(stringFact1);
        wm.insert(anotherString);
        wm.insert(intFact1);
        wm.insert(doubleFact);

        for (ObjectTypeConf conf : wm.getObjectTypeConfigurationRegistry()
                .values()) {

            assertFalse(
                    "TMS shouldn't be enabled for any type, since no logical insert was done.",
                    conf.isTMSEnabled());

        }

        wm.insertLogical(stringFact2);

        assertTrue("Should have enabled TMS for Strings.", stringTypeConf
                .isTMSEnabled());

        assertFalse("Shouldn't have enabled TMS for Integers.", intTypeConf
                .isTMSEnabled());

        wm.insertLogical(intFact2);

        assertTrue("Now it should have enabled TMS for Integers!.", intTypeConf
                .isTMSEnabled());

    }

}
