/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.testcoverage.functional;

import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.listener.TrackingAgendaEventListener;
import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;

public class GuidedDecisionTableTest {

    private Address barcelonaCityCenter;
    private Person johnFromBarcelona;
    private KieSession kSession;
    private TrackingAgendaEventListener rulesFired;

    @Before
    public void setUp() throws Exception {
        barcelonaCityCenter = new Address("City Center",
                                          1,
                                          "Barcelona");
        johnFromBarcelona = new Person("John",
                                       18);
        johnFromBarcelona.setAddress(barcelonaCityCenter);
    }

    /**
     * Even when there are multiple matching rules, only one is fired.
     */
    @Test
    public void testUniqueHitPolicy() throws Exception {
        initKieSession("uniqueHitPolicy.gdst");
        kSession.insert(johnFromBarcelona);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 2 uniqueHitPolicy")).isTrue();

        kSession.dispose();
    }

    /**
     * Even when there are multiple matching rules, only one is fired.
     * However not the first one, but the one with the highest salience.
     */
    @Test
    public void testUniqueHitPolicyWithSalience() throws Exception {
        initKieSession("uniqueHitPolicyWithSalience.gdst");
        kSession.insert(johnFromBarcelona);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 4 uniqueHitPolicyWithSalience")).isTrue();

        kSession.dispose();
    }

    private void initKieSession(String gdstName) {
        final Resource resource = KieServices.Factory.get().getResources().newClassPathResource(gdstName,
                                                                                                GuidedDecisionTableTest.class);
        final KieBase kBase = KieBaseUtil.getKieBaseFromResources(true,
                                                                  resource);

        kSession = kBase.newKieSession();

        rulesFired = new TrackingAgendaEventListener();
        kSession.addEventListener(rulesFired);
        rulesFired.clear();
    }
}
