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

import java.io.Serializable;
import java.util.List;

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

    public class Person implements Serializable {

        private static final long serialVersionUID = -6208475520104308723L;

        private int id = 0;
        private String name = "";
        private int age = 0;

        public Person() {
        }

        public Person(String name, int age) {
            super();
            this.name = name;
            this.age = age;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return String.format("%s[id='%s', name='%s']",
                    getClass().getName(), id, name);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + id;
            result = prime * result + ((name == null) ? 0 : name.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            Person other = (Person) obj;
            if (id != other.id) {
                return false;
            }
            if (name == null) {
                if (other.name != null) {
                    return false;
                }
            } else if (!name.equals(other.name)) {
                return false;
            }
            return true;
        }
    }
}
