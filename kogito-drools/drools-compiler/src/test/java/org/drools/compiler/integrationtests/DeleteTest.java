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
import org.kie.api.builder.Message.Level;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteTest {

    private static final String DELETE_TEST_DRL = "delete_test.drl";

    private KieSession ksession;

    @Before
    public void setUp() {
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write(KieServices.Factory.get().getResources()
                .newClassPathResource(DELETE_TEST_DRL, DeleteTest.class));

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();

        List<Message> res = kbuilder.getResults().getMessages(Level.ERROR);
        assertThat(res).isEmpty();

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
    public void deleteFactTest() {
        ksession.insert(new Person("Petr", 25));

        FactHandle george = ksession.insert(new Person("George", 19));
        QueryResults results = ksession.getQueryResults("informationAboutPersons");
        assertThat(results).isNotEmpty();
        assertThat(results.iterator().next().get("$countOfPerson")).isEqualTo(2L);

        ksession.delete(george);
        results = ksession.getQueryResults("informationAboutPersons");
        assertThat(results).isNotEmpty();
        assertThat(results.iterator().next().get("$countOfPerson")).isEqualTo(1L);
    }

    @Test
    public void deleteFactTwiceTest() {
        FactHandle george = ksession.insert(new Person("George", 19));
        QueryResults results = ksession.getQueryResults("countPerson");
        assertThat(results).isNotEmpty();
        assertThat(results.iterator().next().get("$personCount")).isEqualTo(1L);

        ksession.delete(george);
        results = ksession.getQueryResults("countPerson");
        assertThat(results).isNotEmpty();
        assertThat(results.iterator().next().get("$personCount")).isEqualTo(0L);

        ksession.delete(george);
        assertThat(results).isNotEmpty();
        assertThat(results.iterator().next().get("$personCount")).isEqualTo(0L);
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNullFactTest() {
        ksession.delete(null);
    }

    @Test
    public void deleteUpdatedFactTest() {
        FactHandle person = ksession.insert(new Person("George", 18));

        ksession.update(person, new Person("John", 21));

        QueryResults results = ksession.getQueryResults("countPerson");
        assertThat(results).isNotEmpty();
        assertThat(results.iterator().next().get("$personCount")).isEqualTo(1L);

        ksession.delete(person);
        results = ksession.getQueryResults("countPerson");
        assertThat(results).isNotEmpty();
        assertThat(results.iterator().next().get("$personCount")).isEqualTo(0L);
    }

    @Test
    public void deleteUpdatedFactDifferentClassTest() {
        FactHandle fact = ksession.insert(new Person("George", 18));

        assertThat(ksession.getObjects()).hasSize(1);
        assertThat(ksession.getObjects().iterator().next()).isInstanceOf(Person.class);

        ksession.update(fact, new Cheese("Cheddar", 50));

        assertThat(ksession.getObjects()).hasSize(1);
        assertThat(ksession.getObjects().iterator().next()).isInstanceOf(Cheese.class);

        ksession.delete(fact);

        assertThat(ksession.getObjects()).isEmpty();

    }
}
