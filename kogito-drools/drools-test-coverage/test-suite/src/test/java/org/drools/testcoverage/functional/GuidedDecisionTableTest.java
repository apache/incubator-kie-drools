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
    private Person elizabeth35Years;
    private Person william25Years;
    private Person oldPeter;
    private KieSession kSession;
    private TrackingAgendaEventListener rulesFired;

    @Before
    public void setUp() throws Exception {
        barcelonaCityCenter = new Address("City Center",
                                          1,
                                          "Barcelona");
        johnFromBarcelona = new Person("John",
                                       18);
        oldPeter = new Person("Peter",
                              70);
        johnFromBarcelona.setAddress(barcelonaCityCenter);
        elizabeth35Years = new Person("Elizabeth",
                                      35);
        william25Years = new Person("William",
                                    25);
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

    @Test
    public void testFirstHitPolicyMatchAll() throws Exception {
        initKieSession("firstHitPolicy.gdst");
        kSession.insert(elizabeth35Years);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 1 firstHitPolicy")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testFirstHitPolicyMatchTwoOfThree() throws Exception {
        initKieSession("firstHitPolicy.gdst");
        kSession.insert(william25Years);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 2 firstHitPolicy")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testResolvedHitPolicy() throws Exception {
        initKieSession("resolvedHitPolicy.gdst");
        kSession.insert(oldPeter);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 2 resolvedHitPolicy")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testResolvedHitPolicyPossibleMatchOnTwoRows() throws Exception {
        initKieSession("resolvedHitPolicyPossibleMatchOnTwoRows.gdst");
        kSession.insert(oldPeter);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);

        Assertions.assertThat(rulesFired.isRuleFired("Row 6 resolvedHitPolicyPossibleMatchOnTwoRows")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testResolvedHitPolicyPrioritiesOverSameRow() throws Exception {
        initKieSession("resolvedHitPolicyPrioritiesOverSameRow.gdst");
        kSession.insert(oldPeter);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 3 resolvedHitPolicyPrioritiesOverSameRow")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testResolvedHitPolicyPrioritiesOverSameRowMatchTwoRows() throws Exception {
        initKieSession("resolvedHitPolicyPrioritiesOverSameRowMatchTwoRows.gdst");
        kSession.insert(oldPeter);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 5 resolvedHitPolicyPrioritiesOverSameRowMatchTwoRows")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testResolvedHitPolicyTransitivePriorities() throws Exception {
        initKieSession("resolvedHitPolicyTransitivePriorities.gdst");
        kSession.insert(oldPeter);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 4 resolvedHitPolicyTransitivePriorities")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testResolvedHitPolicyTransitivePrioritiesMatchTwoRows() throws Exception {
        initKieSession("resolvedHitPolicyTransitivePrioritiesMatchTwoRows.gdst");
        kSession.insert(oldPeter);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 3 resolvedHitPolicyTransitivePrioritiesMatchTwoRows")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testRuleOrderHitPolicy() throws Exception {
        initKieSession("ruleOrderHitPolicy.gdst");

        kSession.insert(oldPeter);

        kSession.fireAllRules();
        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(4);
        Assertions.assertThat(rulesFired.getRulesFiredOrder()).containsSequence("Row 1 ruleOrderHitPolicy",
                                                                                "Row 2 ruleOrderHitPolicy",
                                                                                "Row 3 ruleOrderHitPolicy",
                                                                                "Row 4 ruleOrderHitPolicy");
        kSession.dispose();
    }

    @Test
    public void testRuleOrderHitPolicyTwoOfFour() throws Exception {
        initKieSession("ruleOrderHitPolicy.gdst");
        kSession.insert(johnFromBarcelona);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(2);
        Assertions.assertThat(rulesFired.getRulesFiredOrder()).containsSequence("Row 1 ruleOrderHitPolicy",
                                                                                "Row 3 ruleOrderHitPolicy");
        kSession.dispose();
    }

    @Test
    public void testRuleOrderHitPolicyActivationGroupBeginning() throws Exception {
        initKieSession("ruleOrderHitPolicyActivationGroupBeginning.gdst");
        kSession.insert(oldPeter);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(3);
        Assertions.assertThat(rulesFired.getRulesFiredOrder()).containsSequence("Row 1 ruleOrderHitPolicyActivationGroupBeginning",
                                                                                "Row 3 ruleOrderHitPolicyActivationGroupBeginning",
                                                                                "Row 4 ruleOrderHitPolicyActivationGroupBeginning");
        kSession.dispose();
    }

    @Test
    public void testRuleOrderHitPolicyActivationGroupEnd() throws Exception {
        initKieSession("ruleOrderHitPolicyActivationGroupEnd.gdst");
        kSession.insert(oldPeter);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(3);
        Assertions.assertThat(rulesFired.getRulesFiredOrder()).containsSequence("Row 1 ruleOrderHitPolicyActivationGroupEnd",
                                                                                "Row 2 ruleOrderHitPolicyActivationGroupEnd",
                                                                                "Row 3 ruleOrderHitPolicyActivationGroupEnd");
        kSession.dispose();
    }

    @Test
    public void testRuleOrderHitPolicyTwoActivationGroups() throws Exception {
        initKieSession("ruleOrderHitPolicyTwoActivationGroups.gdst");
        kSession.insert(oldPeter);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(4);
        Assertions.assertThat(rulesFired.getRulesFiredOrder()).containsSequence("Row 1 ruleOrderHitPolicyTwoActivationGroups",
                                                                                "Row 3 ruleOrderHitPolicyTwoActivationGroups",
                                                                                "Row 4 ruleOrderHitPolicyTwoActivationGroups",
                                                                                "Row 5 ruleOrderHitPolicyTwoActivationGroups");
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
