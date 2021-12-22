/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
package org.drools.tms;

import org.drools.core.common.TruthMaintenanceSystem;
import org.drools.core.common.TruthMaintenanceSystemFactory;
import org.drools.core.reteoo.ObjectTypeConf;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.kiesession.session.StatefulKnowledgeSessionImpl;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    private StatefulKnowledgeSessionImpl ksession;
    private TruthMaintenanceSystem tms;

    @Before
    public void setUp() {
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        ksession = (StatefulKnowledgeSessionImpl)kBase.newKieSession();

        tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(ksession);
    }

    @Test
    public void shouldLazilyAdd() throws Exception {

        final String fact1 = "logical";

        ksession.insert(fact1);

        assertEquals(
                "Shouldn't have anything, since no logical insert was performed.",
                0, tms.getEqualityKeyMap().size());

        final String fact2 = "logical";

        TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(ksession).insert( fact2, null, new TMSMockActivation() );

        assertEquals(
                "Now that a logical insert was done, it should have an element.",
                1, tms.getEqualityKeyMap().size());

        // Make sure the internals are fine.
        ObjectTypeConf typeConf = ksession.getObjectTypeConfigurationRegistry()
                .getOrCreateObjectTypeConf(ksession.getEntryPoint(), fact1);

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

        ObjectTypeConf stringTypeConf = ksession.getObjectTypeConfigurationRegistry()
                .getOrCreateObjectTypeConf(ksession.getEntryPoint(), stringFact1);

        ObjectTypeConf intTypeConf = ksession.getObjectTypeConfigurationRegistry()
                .getOrCreateObjectTypeConf(ksession.getEntryPoint(), intFact1);

        ksession.insert(stringFact1);
        ksession.insert(anotherString);
        ksession.insert(intFact1);
        ksession.insert(doubleFact);

        for (ObjectTypeConf conf : ksession.getObjectTypeConfigurationRegistry()
                .values()) {

            assertFalse(
                    "TMS shouldn't be enabled for any type, since no logical insert was done.",
                    conf.isTMSEnabled());

        }

        TruthMaintenanceSystem tms = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(ksession);
        tms.insert( stringFact2, null, new TMSMockActivation() );

        assertTrue("Should have enabled TMS for Strings.", stringTypeConf
                .isTMSEnabled());

        assertFalse("Shouldn't have enabled TMS for Integers.", intTypeConf
                .isTMSEnabled());

        tms.insert( intFact2, null, new TMSMockActivation() );

        assertTrue("Now it should have enabled TMS for Integers!.", intTypeConf
                .isTMSEnabled());

    }

}
