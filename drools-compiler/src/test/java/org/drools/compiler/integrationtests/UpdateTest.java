/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.Person;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;


public class UpdateTest {

    private static final String DELETE_TEST_DRL = "update_test.drl";

    private KieSession ksession;

    @Before
    public void setUp() {
        final KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();

        kfs.write(KieServices.Factory.get().getResources()
                .newClassPathResource(DELETE_TEST_DRL, DeleteTest.class));

        final KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);

        kbuilder.buildAll();

        final List<Message> res = kbuilder.getResults().getMessages(Message.Level.ERROR);

        assertEquals(res.toString(), 0, res.size());

        final KieBase kbase = KieServices.Factory.get()
                .newKieContainer(kbuilder.getKieModule().getReleaseId())
                .getKieBase();

        ksession = kbase.newKieSession();
    }

    @After
    public void tearDown() {
        ksession.dispose();
    }

    @Test
    public void updateTheOnlyFactTest() {
        final Person person = new Person("George", 18);
        final FactHandle factPerson = ksession.insert(person);
        assertThat(ksession.getObjects()).hasSize(1);
        assertThat(ksession.getObjects().iterator().next()).isInstanceOf(Person.class);

        Person personToBeVerified = (Person) ksession.getObjects().iterator().next();
        verifyPerson(person, personToBeVerified, 18, "George", true);

        ksession.update(factPerson, new Person("Henry", 21));
        verifyFactsPresentInSession(1, Person.class);

        personToBeVerified = (Person) ksession.getObjects().iterator().next();
        verifyPerson(person, personToBeVerified, 21, "Henry", false);
    }

    @Test(expected = NullPointerException.class)
    public void updateWithNullTest() {
        final Person person = new Person("George", 18);
        final FactHandle factPerson = ksession.insert(person);
        verifyFactsPresentInSession(1, Person.class);

        ksession.update(factPerson, null);
    }

    @Test
    public void updateWithDifferentClassGetQueryResultsTest() {
        final Person person = new Person("George", 18);
        final FactHandle fact = ksession.insert(person);

        verifyFactsWithQuery(Person.class, "persons", person);

        final Cheese cheese = new Cheese("Camembert", 2);
        ksession.update(fact, cheese);

        verifyWithQueryNoPersonsPresentInFacts();

        verifyFactsPresentInSession(1, Cheese.class);
        Cheese cheeseToBeVerified = (Cheese) ksession.getObjects().iterator().next();
        verifyCheese(cheeseToBeVerified, 2, "Camembert");

        cheeseToBeVerified = verifyFactPresentInSession(fact, Cheese.class);
        verifyCheese(cheeseToBeVerified, 2, "Camembert");

    }

    @Test
    public void updateWithDifferentClassGetObjectsTest() {
        final Person person = new Person("George", 18);
        final FactHandle factPerson = ksession.insert(person);
        final Person personToBeVerified = verifyFactsPresentInSession(1, Person.class).get(0);
        assertThat(personToBeVerified).isEqualTo(person);

        final Cheese cheese = new Cheese("Camembert", 50);
        ksession.update(factPerson, cheese);
        verifyFactsPresentInSession(1, Cheese.class);

        final Cheese cheeseToBeVerified = (Cheese) ksession.getObjects().iterator().next();
        verifyCheese(cheeseToBeVerified, 50, "Camembert");
    }

    @Test
    public void updateFireRulesTest() {
        final Person george = new Person("George", 17);
        final Person henry = new Person("Henry", 25);
        final FactHandle georgeFact = ksession.insert(george);
        ksession.insert(henry);

        verifyFactsWithQuery(Person.class, "persons", george, henry);

        final List<Person> drivers = new ArrayList<>();
        ksession.setGlobal("drivers", drivers);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        verifyList(drivers, george, henry);

        george.setAge(18);
        ksession.update(georgeFact, george);
        verifyFactsWithQuery(Person.class, "persons", george, henry);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        verifyList(drivers, null, george, henry);
    }

    @Test
    public void updateFactOnRuleFireTest() {
        final Cheese camembert = new Cheese("Camembert", 19);
        final Cheese cheddar = new Cheese("Cheddar", 45);

        ksession.insert(camembert);
        ksession.insert(cheddar);

        verifyFactsWithQuery(Cheese.class, "cheeseTypes", camembert, cheddar);

        final List<Cheese> expensiveCheese = new ArrayList<>();
        ksession.setGlobal("expensiveCheese", expensiveCheese);
        final int firedRules = ksession.fireAllRules();
        assertThat(firedRules).isEqualTo(2);
        verifyList(expensiveCheese, camembert, cheddar);

        verifyFactsWithQuery(Cheese.class, "cheeseTypes", camembert, cheddar);
        assertThat(camembert.getPrice()).isEqualTo(21);
        assertThat(cheddar.getPrice()).isEqualTo(45);
    }

    private <T> void verifyFactsWithQuery(final Class<T> expectedClassOfFacts, final String queryToGetFacts, final T... factsToVerify) {
        final QueryResults results = ksession.getQueryResults(queryToGetFacts);
        assertThat(results).isNotEmpty();
        final QueryResultsRow resultsRow = results.iterator().next();

        assertThat(resultsRow.get("$" + queryToGetFacts)).isInstanceOf(List.class);
        final List<Object> objects = (List<Object>) resultsRow.get("$" + queryToGetFacts);
        assertThat(objects).hasSize(factsToVerify.length);
        assertThat(objects).hasOnlyElementsOfType(expectedClassOfFacts);
        assertThat(objects).containsAll(Arrays.asList(factsToVerify));
    }

    private void verifyWithQueryNoPersonsPresentInFacts() {
        QueryResults results = ksession.getQueryResults("persons");
        assertThat(results).isNotEmpty();

        results = ksession.getQueryResults("persons");
        assertThat(results).isNotEmpty();
        final QueryResultsRow resultsRow = results.iterator().next();
        assertThat(resultsRow.get("$persons")).isInstanceOf(List.class);
        final List<Object> persons = (List<Object>) resultsRow.get("$persons");
        assertThat(persons).isEmpty();
    }

    private <T> List<T> verifyFactsPresentInSession(final int expectedCountOfFacts, final Class<T> expectedClassOfFacts) {
        if (expectedCountOfFacts < 1) {
            assertThat(ksession.getObjects()).isEmpty();
        } else {
            assertThat(ksession.getObjects()).hasSize(expectedCountOfFacts);
            assertThat(ksession.getObjects()).hasOnlyElementsOfType(expectedClassOfFacts);
            return (List<T>) new ArrayList<Object>(ksession.getObjects());
        }
        return null;
    }

    private <T> T verifyFactPresentInSession(final FactHandle factToVerify, final Class<T> expectedClassOfFact) {
        assertThat(ksession.getObject(factToVerify)).isNotNull();
        assertThat(ksession.getObject(factToVerify)).isInstanceOf(expectedClassOfFact);
        return (T) ksession.getObject(factToVerify);
    }

    private void verifyCheese(final Cheese cheeseToBeVerified, final int price, final String type) {
        assertThat(cheeseToBeVerified.getPrice()).isEqualTo(price);
        assertThat(cheeseToBeVerified.getType()).isEqualTo(type);
    }

    private void verifyPerson(final Person original, final Person personToBeVerified, final int age, final String name, final boolean shouldBeEqual) {
        if (original != null) {
            if (shouldBeEqual) {
                assertThat(personToBeVerified).isEqualTo(original);
            } else {
                assertThat(personToBeVerified).isNotEqualTo(original);
            }
        }
        assertThat(personToBeVerified.getAge()).isEqualTo(age);
        assertThat(personToBeVerified.getName()).isEqualTo(name);
    }

    private <T> void verifyList(final List<T> list, final T entryNotToContain, final T... entriesToContain) {
        assertThat(list).isNotEmpty();
        assertThat(list).hasSize(entriesToContain.length);
        assertThat(list).containsAll(Arrays.asList(entriesToContain));
        if (entryNotToContain != null) {
            assertThat(list).doesNotContain(entryNotToContain);
        }
    }

}
