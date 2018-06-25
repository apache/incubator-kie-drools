/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.simulation;

import java.util.List;

import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.Person;
import org.junit.Test;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.ExecutableRunner;
import org.kie.api.runtime.RequestContext;
import org.kie.api.runtime.builder.ExecutableBuilder;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.RuleUnit;

import static org.drools.core.ruleunit.RuleUnitUtil.getUnitName;
import static org.junit.Assert.assertEquals;

public class BatchRunUnitFluentTest extends CommonTestMethodBase {

    String drlUnit =
            "import " + Person.class.getCanonicalName() + "\n" +
                    "import " + AdultUnit.class.getCanonicalName() + "\n" +
                    "import " + NotAdultUnit.class.getCanonicalName() + "\n" +
                    "rule Adult @Unit( AdultUnit.class ) when\n" +
                    "    Person(age >= 18, $name : name) from persons\n" +
                    "then\n" +
                    "    System.out.println($name + \" is adult\");\n" +
                    "end";

    String drlUnit1 =
            "import " + Person.class.getCanonicalName() + "\n" +
                    "import " + AdultUnitDifferentDataSourceName.class.getCanonicalName() + "\n" +
                    "global String oldName;\n" +
                    "rule RealOld @Unit( AdultUnitDifferentDataSourceName.class ) when\n" +
                    "    Person(age >= 90, $name : name) from people\n" +
                    "then\n" +
                    "    System.out.println($name + \" is really old\");\n" +
                    "    kcontext.getKnowledgeRuntime().setGlobal(\"oldName\", $name);" +
                    "end";

    ReleaseId releaseIdUnit = SimulateTestBase.createKJarWithMultipleResources("org.kie.unit2", new String[]{drlUnit, drlUnit1}, new ResourceType[]{ResourceType.DRL, ResourceType.DRL});

    @Test
    public void testUnit() {
        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseIdUnit)

                .newRuleUnitExecutor()

                .createDataSource(Person.class)
                .addBinding("persons")
                .insert(new Person("Mario", 10))
                .insert(new Person("Daniele", 30))
                .insert(new Person("Mark", 40))
                .buildDataSource()

                .run(AdultUnit.class)
                .out("firedRules")
                .dispose();

        RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

        assertEquals(2, requestContext.getOutputs().get("firedRules"));
    }

    @Test
    public void testUnitMultipleBinding() {
        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseIdUnit)

                .newRuleUnitExecutor()

                .createDataSource(Person.class)
                .addBinding("persons").addBinding("people")
                .insert(new Person("Mario", 40))
                .insert(new Person("Daniele", 30))
                .insert(new Person("Mark", 90))
                .buildDataSource()

                .run(AdultUnit.class)
                .out("firedRules1")
                .run(AdultUnitDifferentDataSourceName.class)
                .out("firedRules2")
                .dispose();

        RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

        assertEquals(3, requestContext.getOutputs().get("firedRules1"));
        assertEquals(1, requestContext.getOutputs().get("firedRules2"));
    }

    @Test
    public void testUnitNoBinding() {
        ExecutableBuilder f = ExecutableBuilder.create();

        f.newApplicationContext("app1")
                .getKieContainer(releaseIdUnit)

                .newRuleUnitExecutor()

                .createDataSource(Person.class)
                .insert(new Person("Mario", 10))
                .insert(new Person("Daniele", 30))
                .insert(new Person("Mark", 40))
                .buildDataSource()

                .run(AdultUnit.class)
                .out("firedRulesNoBinding")
                .dispose();

        RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

        assertEquals(0, requestContext.getOutputs().get("firedRulesNoBinding"));
    }

    @Test
    public void testUnitLambdaInitializer() {
        ExecutableBuilder f = ExecutableBuilder.create();

        DataSource<Person> people = DataSource.create(
                new Person("Mario", 10),
                new Person("Daniele", 30),
                new Person("Mark", 90));

        f.newApplicationContext("app1")
                .getKieContainer(releaseIdUnit)

                .newRuleUnitExecutor()
                .bindVariable("people", people)

                .run((() -> new AdultUnitDifferentDataSourceName(people)))
                .out("firedRules")
                .dispose();

        RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

        assertEquals(1, requestContext.getOutputs().get("firedRules"));
    }

    @Test
    public void testUnitLazyVarBinding() {
        ExecutableBuilder f = ExecutableBuilder.create();

        DataSource<Person> people = DataSource.create(
                new Person("Mario", 10),
                new Person("Daniele", 30),
                new Person("Mark", 90));

        f.newApplicationContext("app1")
                .getKieContainer(releaseIdUnit)

                .newRuleUnitExecutor()
                .run((() -> new AdultUnitDifferentDataSourceName(people)))
                .getGlobal("oldName")
                .set("test")
                .bindVariableByExpression("lazyVariable", context -> context.get("test"))
                .get("lazyVariable")
                .out("firedRules")
                .dispose();

        RequestContext requestContext = ExecutableRunner.create().execute(f.getExecutable());

        assertEquals("Mark", requestContext.getOutputs().get("firedRules"));
    }

    public static class AdultUnitDifferentDataSourceName implements RuleUnit {

        private DataSource<Person> people;

        public AdultUnitDifferentDataSourceName() {
        }

        public AdultUnitDifferentDataSourceName(DataSource<Person> people) {
            this.people = people;
        }

        public DataSource<Person> getPeople() {
            return people;
        }
    }

    public static class AdultUnit implements RuleUnit {

        private final int adultAge = 0;
        private DataSource<Person> persons;
        private List<String> log;
        private List<String> results;

        public AdultUnit() {
        }

        public AdultUnit(final DataSource<Person> persons) {
            this.persons = persons;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }

        public int getAdultAge() {
            return adultAge;
        }

        public List<String> getResults() {
            return results;
        }

        @Override
        public void onStart() {
            if (log != null) {
                log.add(getUnitName(this) + " started.");
            } else {
                System.out.println(getUnitName(this) + " started.");
            }
        }

        @Override
        public void onEnd() {
            if (log != null) {
                log.add(getUnitName(this) + " ended.");
            } else {
                System.out.println(getUnitName(this) + " ended.");
            }
        }

        @Override
        public void onYield(final RuleUnit other) {
            if (log != null) {
                log.add(getUnitName(this) + " yielded to " + getUnitName(other));
            } else {
                System.out.println(getUnitName(this) + " yielded to " + getUnitName(other));
            }
        }
    }

    public static class NotAdultUnit implements RuleUnit {

        private DataSource<Person> persons;
        private List<String> log;

        public NotAdultUnit() {
        }

        public NotAdultUnit(final DataSource<Person> persons) {
            this.persons = persons;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }

        @Override
        public void onStart() {
            if (log != null) {
                log.add(getUnitName(this) + " started.");
            } else {
                System.out.println(getUnitName(this) + " started.");
            }
        }

        @Override
        public void onEnd() {
            if (log != null) {
                log.add(getUnitName(this) + " ended.");
            } else {
                System.out.println(getUnitName(this) + " ended.");
            }
        }

        @Override
        public void onYield(final RuleUnit other) {
            if (log != null) {
                log.add(getUnitName(this) + " yielded to " + getUnitName(other));
            } else {
                System.out.println(getUnitName(this) + " yielded to " + getUnitName(other));
            }
        }
    }
}
