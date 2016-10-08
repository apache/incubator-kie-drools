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
import java.util.Comparator;
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
import static org.junit.Assert.*;


public class UpdateTest {

    private static final String DELETE_TEST_DRL = "update_test.drl";

    private KieSession ksession;

    /* ************** */
    /* PUBLIC METHODS */
    /* ************** */

    @Before
    public void setUp() {
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();

        kfs.write(KieServices.Factory.get().getResources()
                .newClassPathResource(DELETE_TEST_DRL, DeleteTest.class));

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);

        kbuilder.buildAll();

        List<Message> res = kbuilder.getResults().getMessages(Message.Level.ERROR);

        assertEquals(res.toString(), 0, res.size());

        KieBase kbase = KieServices.Factory.get()
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
        Person person = new Person("George", 18);
        FactHandle factPerson = ksession.insert(person);
        assertThat(ksession.getObjects()).hasSize(1);
        assertThat(ksession.getObjects().iterator().next()).isInstanceOf(Person.class);

        Person personToBeVerified = (Person) ksession.getObjects().iterator().next();
        assertThat(personToBeVerified.getName()).isEqualTo("George");
        assertThat(personToBeVerified.getAge()).isEqualTo(18);

        ksession.update(factPerson, new Person("Henry", 21));
        assertThat(ksession.getObjects()).hasSize(1);
        assertThat(ksession.getObjects().iterator().next()).isInstanceOf(Person.class);

        personToBeVerified = (Person) ksession.getObjects().iterator().next();
        assertThat(personToBeVerified).isNotEqualTo(person);
        assertThat(personToBeVerified.getAge()).isEqualTo(21);
        assertThat(personToBeVerified.getName()).isEqualTo("Henry");
    }

    @Test(expected = NullPointerException.class)
    public void updateWithNullTest() {
        Person person = new Person("George", 18);
        FactHandle factPerson = ksession.insert(person);
        assertThat(ksession.getObjects()).hasSize(1);

        ksession.update(factPerson, null);
    }

    @Test
    public void updateWithDifferentClassGetQueryResultsTest() {
        Person person = new Person("George", 18);
        FactHandle fact = ksession.insert(person);

        QueryResults results = ksession.getQueryResults("persons");
        assertThat(results).isNotEmpty();
        QueryResultsRow resultsRow = results.iterator().next();

        /* checking original object */
        assertThat(resultsRow.get("$persons")).isInstanceOf(List.class);
        List<Object> persons = (List<Object>) resultsRow.get("$persons");
        assertThat(persons).hasSize(1);
        assertThat(persons).hasOnlyElementsOfType(Person.class);
        assertThat(persons.get(0)).isEqualTo(person);

        Cheese cheese = new Cheese("Camembert", 2);
        ksession.update(fact, cheese);

        /* getting updated object via getQueryResults(String s) */
        results = ksession.getQueryResults("persons");
        assertThat(results).isNotEmpty();
        resultsRow = results.iterator().next();
        assertThat(resultsRow.get("$persons")).isInstanceOf(List.class);
        persons = (List<Object>) resultsRow.get("$persons");
        assertThat(persons).isEmpty();

        /* getting updated object via getObjects() */
        assertThat(ksession.getObjects()).hasSize(1);
        assertThat(ksession.getObjects().iterator().next()).isInstanceOf(Cheese.class);
        Cheese cheeseToBeVerified = (Cheese) ksession.getObjects().iterator().next();
        assertThat(cheeseToBeVerified.getPrice()).isEqualTo(2);
        assertThat(cheeseToBeVerified.getType()).isEqualTo("Camembert");

        /* getting updated object via getObject(FactHandle f) */
        assertThat(ksession.getObject(fact)).isNotNull();
        assertThat(ksession.getObject(fact)).isInstanceOf(Cheese.class);
        cheeseToBeVerified = (Cheese) ksession.getObject(fact);
        assertThat(cheeseToBeVerified.getPrice()).isEqualTo(2);
        assertThat(cheeseToBeVerified.getType()).isEqualTo("Camembert");

    }

    @Test
    public void updateWithDifferentClassGetObjectsTest() {
        Person person = new Person("George", 18);
        FactHandle factPerson = ksession.insert(person);
        assertThat(ksession.getObjects()).hasSize(1);
        assertThat(ksession.getObjects().iterator().next()).isInstanceOf(Person.class);
        Person personToBeVerified = (Person) ksession.getObjects().iterator().next();
        assertThat(personToBeVerified).isEqualTo(person);

        Cheese cheese = new Cheese("Camembert", 50);
        ksession.update(factPerson, cheese);
        assertThat(ksession.getObjects()).hasSize(1);
        assertThat(ksession.getObjects().iterator().next()).isInstanceOf(Cheese.class);

        Cheese cheeseToBeVerified = (Cheese) ksession.getObjects().iterator().next();
        assertThat(cheeseToBeVerified.getPrice()).isEqualTo(50);
        assertThat(cheeseToBeVerified.getType()).isEqualTo("Camembert");

    }

    @Test
    public void updateFireRulesTest() {
        Person george = new Person("George", 17);
        Person henry = new Person("Henry", 25);
        FactHandle georgeFact = ksession.insert(george);
        ksession.insert(henry);

        QueryResults results = ksession.getQueryResults("persons");
        assertThat(results).isNotEmpty();
        QueryResultsRow resultsRow = results.iterator().next();
        assertThat(resultsRow.get("$persons")).isInstanceOf(List.class);
        assertThat((List<Object>) resultsRow.get("$persons")).hasOnlyElementsOfType(Person.class);
        List<Person> persons = new ArrayList<>((List<Person>) resultsRow.get("$persons"));
        assertThat(persons).hasSize(2);
        persons.sort(new PersonAlphabeticalComparator());
        assertThat(persons.get(0).getName()).isEqualTo("George");
        assertThat(persons.get(0).getAge()).isEqualTo(17);
        assertThat(persons.get(1).getName()).isEqualTo("Henry");
        assertThat(persons.get(1).getAge()).isEqualTo(25);

        final List<Person> drivers = new ArrayList<>();
        ksession.setGlobal("drivers", drivers);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(drivers).isNotEmpty();
        assertThat(drivers).hasSize(1);
        assertThat(drivers).contains(henry);
        assertThat(drivers).doesNotContain(george);

        george.setAge(18);
        ksession.update(georgeFact, george);
        results = ksession.getQueryResults("persons");
        assertThat(results).isNotEmpty();
        resultsRow = results.iterator().next();
        assertThat(resultsRow.get("$persons")).isInstanceOf(List.class);
        assertThat((List<Object>) resultsRow.get("$persons")).hasOnlyElementsOfType(Person.class);
        persons = new ArrayList<>((List<Person>) resultsRow.get("$persons"));
        assertThat(persons).hasSize(2);
        persons.sort(new PersonAlphabeticalComparator());
        assertThat(persons.get(0).getName()).isEqualTo("George");
        assertThat(persons.get(0).getAge()).isEqualTo(18);
        assertThat(persons.get(1).getName()).isEqualTo("Henry");
        assertThat(persons.get(1).getAge()).isEqualTo(25);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        assertThat(drivers).isNotEmpty();
        assertThat(drivers).hasSize(2);
        assertThat(drivers).contains(henry);
        assertThat(drivers).contains(george);
    }

    @Test
    public void updateFactOnRuleFireTest() {
        Cheese camembert = new Cheese("Camembert", 19);
        Cheese cheddar = new Cheese("Cheddar", 45);

        ksession.insert(camembert);
        ksession.insert(cheddar);

        QueryResults results = ksession.getQueryResults("cheeseTypes");
        assertThat(results).isNotEmpty();
        QueryResultsRow resultsRow = results.iterator().next();
        assertThat(resultsRow.get("$cheeseTypes")).isInstanceOf(List.class);
        assertThat((List<Object>) resultsRow.get("$cheeseTypes")).hasOnlyElementsOfType(Cheese.class);
        List<Cheese> cheese = new ArrayList<>((List<Cheese>) resultsRow.get("$cheeseTypes"));
        assertThat(cheese).hasSize(2);
        cheese.sort(new CheeseTypeAlphabeticalComparator());
        assertThat(cheese.get(0).getType()).isEqualTo("Camembert");
        assertThat(cheese.get(0).getPrice()).isEqualTo(19);
        assertThat(cheese.get(1).getType()).isEqualTo("Cheddar");
        assertThat(cheese.get(1).getPrice()).isEqualTo(45);

        final List<Cheese> expensiveCheese = new ArrayList<>();
        ksession.setGlobal("expensiveCheese", expensiveCheese);
        int firedRules = ksession.fireAllRules();
        assertThat(firedRules).isEqualTo(2);
        assertThat(expensiveCheese).hasSize(1);
        assertThat(expensiveCheese).contains(cheddar);
        assertThat(expensiveCheese).doesNotContain(camembert);


        results = ksession.getQueryResults("cheeseTypes");
        assertThat(results).isNotEmpty();
        resultsRow = results.iterator().next();
        assertThat(resultsRow.get("$cheeseTypes")).isInstanceOf(List.class);
        assertThat((List<Object>) resultsRow.get("$cheeseTypes")).hasOnlyElementsOfType(Cheese.class);
        cheese = new ArrayList<>((List<Cheese>) resultsRow.get("$cheeseTypes"));
        assertThat(cheese).hasSize(2);
        cheese.sort(new CheeseTypeAlphabeticalComparator());
        assertThat(cheese.get(0).getType()).isEqualTo("Camembert");
        assertThat(cheese.get(0).getPrice()).isEqualTo(21);
        assertThat(cheese.get(1).getType()).isEqualTo("Cheddar");
        assertThat(cheese.get(1).getPrice()).isEqualTo(45);
    }

    /* ************** */
    /* PRIVATE SECTOR */
    /* ************** */
    private static class PersonAlphabeticalComparator implements Comparator<Person> {

        @Override
        public int compare(Person o1, Person o2) {
            return o1.getName().compareToIgnoreCase(o2.getName());
        }
    }

    private static class CheeseTypeAlphabeticalComparator implements Comparator<Cheese> {

        @Override
        public int compare(Cheese c1, Cheese c2) {
            return c1.getType().compareToIgnoreCase(c2.getType());
        }
    }

}
