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

import java.util.Collection;
import org.assertj.core.api.Assertions;
import org.drools.testcoverage.common.listener.TrackingAgendaEventListener;
import org.drools.testcoverage.common.model.Address;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

@RunWith(Parameterized.class)
public class GuidedDecisionTableTest {

    private Address barcelonaCityCenter;
    private Address cottageInMountains;
    private Address cottageInDesert;
    private Person johnFromBarcelona;
    private Person elizabeth35Years;
    private Person william25Years;
    private Person peter70Years;
    private Person mary33Years;
    private KieSession kSession;
    private TrackingAgendaEventListener rulesFired;

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public GuidedDecisionTableTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseConfigurations();
    }

    @Before
    public void setUp() throws Exception {
        barcelonaCityCenter = new Address("City Center",
                                          1,
                                          "Barcelona");
        cottageInMountains = new Address("Mountains",
                                         999,
                                         "Small Village");
        cottageInDesert = new Address("Sand Street",
                                      1,
                                      "Desert Town");
        johnFromBarcelona = new Person("John",
                                       18);
        peter70Years = new Person("Peter",
                                  70);
        mary33Years = new Person("Mary",
                                 33);
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
        kSession.insert(peter70Years);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 2 resolvedHitPolicy")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testResolvedHitPolicyPossibleMatchOnTwoRows() throws Exception {
        initKieSession("resolvedHitPolicyPossibleMatchOnTwoRows.gdst");
        kSession.insert(peter70Years);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);

        Assertions.assertThat(rulesFired.isRuleFired("Row 6 resolvedHitPolicyPossibleMatchOnTwoRows")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testResolvedHitPolicyPrioritiesOverSameRow() throws Exception {
        initKieSession("resolvedHitPolicyPrioritiesOverSameRow.gdst");
        kSession.insert(peter70Years);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 3 resolvedHitPolicyPrioritiesOverSameRow")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testResolvedHitPolicyPrioritiesOverSameRowMatchTwoRows() throws Exception {
        initKieSession("resolvedHitPolicyPrioritiesOverSameRowMatchTwoRows.gdst");
        kSession.insert(peter70Years);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 5 resolvedHitPolicyPrioritiesOverSameRowMatchTwoRows")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testResolvedHitPolicyTransitivePriorities() throws Exception {
        initKieSession("resolvedHitPolicyTransitivePriorities.gdst");
        kSession.insert(peter70Years);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 4 resolvedHitPolicyTransitivePriorities")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testResolvedHitPolicyTransitivePrioritiesMatchTwoRows() throws Exception {
        initKieSession("resolvedHitPolicyTransitivePrioritiesMatchTwoRows.gdst");
        kSession.insert(peter70Years);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(1);
        Assertions.assertThat(rulesFired.isRuleFired("Row 3 resolvedHitPolicyTransitivePrioritiesMatchTwoRows")).isTrue();

        kSession.dispose();
    }

    @Test
    public void testRuleOrderHitPolicy() throws Exception {
        initKieSession("ruleOrderHitPolicy.gdst");

        kSession.insert(peter70Years);

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
        kSession.insert(peter70Years);

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
        kSession.insert(peter70Years);

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
        kSession.insert(peter70Years);

        kSession.fireAllRules();

        Assertions.assertThat(rulesFired.getFiredRules().size()).isEqualTo(4);
        Assertions.assertThat(rulesFired.getRulesFiredOrder()).containsSequence("Row 1 ruleOrderHitPolicyTwoActivationGroups",
                                                                                "Row 3 ruleOrderHitPolicyTwoActivationGroups",
                                                                                "Row 4 ruleOrderHitPolicyTwoActivationGroups",
                                                                                "Row 5 ruleOrderHitPolicyTwoActivationGroups");
        kSession.dispose();
    }

    @Test
    public void testOptimizeAddressesRemoveUnusedAddresses() throws Exception {
        initKieSession("optimizeAddresses.gdst");
        final FactHandle cottage = kSession.insert(cottageInMountains);
        final FactHandle barcelona = kSession.insert(barcelonaCityCenter);
        final FactHandle john = kSession.insert(johnFromBarcelona);

        kSession.fireAllRules();

        Assertions.assertThat(kSession.getObject(cottage)).isNull();
        Assertions.assertThat(kSession.getObject(barcelona)).isNotNull();
        Assertions.assertThat(kSession.getObject(john)).isNotNull();

        kSession.dispose();
    }

    @Test
    public void testOptimizeAddressesMovePeopleToUnusedAddressOrBrno() throws Exception {
        initKieSession("optimizeAddresses.gdst");
        final FactHandle mountainsCottage = kSession.insert(cottageInMountains);
        final FactHandle elizabeth = kSession.insert(elizabeth35Years);
        final FactHandle william = kSession.insert(william25Years);
        final FactHandle john = kSession.insert(johnFromBarcelona);

        kSession.fireAllRules();

        Assertions.assertThat(kSession.getObject(mountainsCottage)).isNotNull();
        Assertions.assertThat(kSession.getObject(elizabeth)).isNotNull();
        Assertions.assertThat(kSession.getObject(william)).isNotNull();
        Assertions.assertThat(((Person) kSession.getObject(elizabeth)).getAddress()).isEqualTo(cottageInMountains);
        Assertions.assertThat(((Person) kSession.getObject(william)).getAddress().getCity()).isEqualTo("Brno");
        Assertions.assertThat(kSession.getObject(john)).isNotNull();

        kSession.dispose();
    }

    @Test
    public void testOptimizeAddressesMovePeopleAndRemoveUnusedAddresses() throws Exception {
        initKieSession("optimizeAddresses.gdst");
        final FactHandle desertCottage = kSession.insert(cottageInDesert);
        final FactHandle mountainsCottage = kSession.insert(cottageInMountains);
        final FactHandle elizabeth = kSession.insert(elizabeth35Years);

        kSession.fireAllRules();

        Assertions.assertThat(kSession.getObject(mountainsCottage)).isNotNull();
        Assertions.assertThat(kSession.getObject(elizabeth)).isNotNull();
        Assertions.assertThat(((Person) kSession.getObject(elizabeth)).getAddress()).isEqualTo(cottageInMountains);
        Assertions.assertThat(kSession.getObject(desertCottage)).isNull();

        kSession.dispose();
    }

    @Test
    public void testDetectWhatPersonLikesAndMoveConditionsMet() throws Exception {
        initKieSession("detectWhatPersonLikesAndMove.gdst");
        william25Years.setLikes("wine");
        final FactHandle williamLikesWine = kSession.insert(william25Years);
        elizabeth35Years.setLikes("movies");
        final FactHandle elizabetLikesMovies = kSession.insert(elizabeth35Years);
        mary33Years.setLikes("pc games");
        final FactHandle maryLikesGames = kSession.insert(mary33Years);

        kSession.fireAllRules();

        Assertions.assertThat(((Person) kSession.getObject(williamLikesWine)).getAddress().getCity()).isEqualTo("Paris");
        Assertions.assertThat(((Person) kSession.getObject(elizabetLikesMovies)).getAddress().getCity()).isEqualTo("New York");
        Assertions.assertThat(((Person) kSession.getObject(maryLikesGames)).getAddress().getCity()).isEqualTo("Berlin");

        kSession.dispose();
    }

    @Test
    public void testDetectWhatPersonLikesAndMoveConditionsNotMet() throws Exception {
        initKieSession("detectWhatPersonLikesAndMove.gdst");
        william25Years.setLikes("money");
        final FactHandle williamLikesMoney = kSession.insert(william25Years);
        mary33Years.setLikes("cheese");
        final FactHandle maryLikesCheese = kSession.insert(mary33Years);
        peter70Years.setLikes("movies");
        final FactHandle peterLikesMovies = kSession.insert(peter70Years);

        kSession.fireAllRules();

        Assertions.assertThat(((Person) kSession.getObject(williamLikesMoney)).getAddress()).isNull();
        Assertions.assertThat(((Person) kSession.getObject(maryLikesCheese)).getAddress()).isNull();
        Assertions.assertThat(((Person) kSession.getObject(peterLikesMovies)).getAddress()).isNull();

        kSession.dispose();
    }

    /**
     * The decision table assumes 3 groups of cities according to people living there
     * The test assert that people move to bigger city, if they like big cities
     */
    @Test
    public void testMoveToBiggerCities() throws Exception {
        initKieSession("moveToBiggerCities.gdst");
        final Address brno = producePeopleInCity("Brno", 7000);
        final Address prague = producePeopleInCity("Prague", 30000);
        final Address london = producePeopleInCity("London", 60000);

        final Address smallCity = new Address();
        smallCity.setCity("city with just one person");

        peter70Years.setAddress(smallCity);
        peter70Years.setLikes("big city");

        william25Years.setAddress(brno);
        william25Years.setLikes("big city");

        mary33Years.setAddress(prague);
        mary33Years.setLikes("big city");

        elizabeth35Years.setAddress(london);
        elizabeth35Years.setLikes("big city");

        kSession.insert(smallCity);
        final FactHandle peter = kSession.insert(peter70Years);
        final FactHandle wiliam = kSession.insert(william25Years);
        final FactHandle mary = kSession.insert(mary33Years);
        final FactHandle elizabeth = kSession.insert(elizabeth35Years);

        Assertions.assertThat(kSession.fireAllRules()).isEqualTo(3);
        Assertions.assertThat(((Person) kSession.getObject(peter)).getAddress().getCity()).isEqualTo("Brno");
        Assertions.assertThat(((Person) kSession.getObject(wiliam)).getAddress().getCity()).isEqualTo("Prague");
        Assertions.assertThat(((Person) kSession.getObject(mary)).getAddress().getCity()).isEqualTo("London");
        // No other bigger city
        Assertions.assertThat(((Person) kSession.getObject(elizabeth)).getAddress().getCity()).isEqualTo("London");

        kSession.dispose();
    }

    @Test
    public void testMoveToBiggerCitiesPeopleNotLikeBigCities() throws Exception {
        initKieSession("moveToBiggerCities.gdst");
        final Address brno = producePeopleInCity("Brno", 7000);
        final Address prague = producePeopleInCity("Prague", 30000);
        final Address london = producePeopleInCity("London", 60000);

        final Address smallCity = new Address();
        smallCity.setCity("city with just one person");

        peter70Years.setAddress(smallCity);
        william25Years.setAddress(brno);
        mary33Years.setAddress(prague);
        elizabeth35Years.setAddress(london);

        kSession.insert(smallCity);
        kSession.insert(peter70Years);
        kSession.insert(william25Years);
        kSession.insert(mary33Years);
        kSession.insert(elizabeth35Years);

        Assertions.assertThat(kSession.fireAllRules()).isEqualTo(0);

        kSession.dispose();
    }

    @Test
    public void testMoveToBiggerCitiesTooBigGapBetweenCitySizes() throws Exception {
        initKieSession("moveToBiggerCities.gdst");
        final Address brno = producePeopleInCity("Brno", 7000);
        producePeopleInCity("London", 60000);

        william25Years.setAddress(brno);
        william25Years.setLikes("big city");

        kSession.insert(william25Years);

        Assertions.assertThat(kSession.fireAllRules()).isEqualTo(0);

        kSession.dispose();
    }


    private Address producePeopleInCity(final String city, final int countOfPeople) {
        final Address address = new Address();
        address.setCity(city);
        kSession.insert(address);

        for (int i = 0; i < countOfPeople; i++) {
            final Person person = new Person();
            person.setName("Inhabitant " + i);
            person.setAddress(address);

            kSession.insert(person);
        }

        return address;
    }

    private void initKieSession(String gdstName) {
        final Resource resource = KieServices.Factory.get().getResources().newClassPathResource(gdstName,
                                                                                                GuidedDecisionTableTest.class);
        final KieBase kBase = KieBaseUtil.getKieBaseFromResources(kieBaseTestConfiguration, true,
                                                                  resource);

        kSession = kBase.newKieSession();

        rulesFired = new TrackingAgendaEventListener();
        kSession.addEventListener(rulesFired);
        rulesFired.clear();
    }
}
