/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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
package org.drools.compiler.integrationtests;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.integrationtests.facts.TestEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Test suite for processing of facts imported using a "star" import and
 * declared in DRL at the same time.
 * BZ-973264
 */
public class StarImportTest extends CommonTestMethodBase {

    private static final String DRL_FILE = "/org/drools/compiler/integrationtests/star_import.drl";

    private KieSession ksession;

    @Before
    public void setup() {
        KieBase kbase = new KieHelper().addFromClassPath(DRL_FILE).build();
        this.ksession = kbase.newKieSession();
    }

    @After
    public void cleanup() {
        if (this.ksession != null) {
            this.ksession.dispose();
        }
    }

    /**
     * Tests that rule fires if given a fact that is imported using 
     * "star" import, while it is also declared in DRL.
     */
    @Test
    public void starImportedFactAlsoDeclaredInDRL() throws Exception {
        AgendaEventListener ael = mock(AgendaEventListener.class);
        ksession.addEventListener(ael);
        ksession.insert(new TestEvent("event 1"));
        ksession.fireAllRules();

        // the rule should have fired exactly once
        verify(ael, times(1)).afterMatchFired(any(AfterMatchFiredEvent.class));
    }

}
