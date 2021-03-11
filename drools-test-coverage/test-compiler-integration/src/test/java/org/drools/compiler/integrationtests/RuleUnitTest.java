/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Semaphore;

import org.assertj.core.api.Assertions;
import org.drools.ruleunit.impl.RuleUnitFactory;
import org.drools.ruleunit.executor.InternalRuleUnitExecutor;
import org.drools.testcoverage.common.model.LongAddress;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.util.DebugList;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionTestConfiguration;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.ReleaseId;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.time.SessionPseudoClock;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.utils.KieHelper;
import org.drools.core.util.index.IndexTestUtil;
import org.drools.ruleunit.DataSource;
import org.drools.ruleunit.RuleUnit;
import org.drools.ruleunit.RuleUnitExecutor;
import org.drools.ruleunit.UnitVar;

import static java.util.Arrays.asList;

import static org.drools.core.util.ClassUtils.getCanonicalSimpleName;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class RuleUnitTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public RuleUnitTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    private static String getUnitName(Object ruleUnit) {
        return ruleUnit.getClass().getName();
    }

    @Test
    public void testWithDataSource() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "import " + NotAdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    Person(age >= 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "end\n" +
                        "rule NotAdult @Unit( NotAdultUnit.class ) when\n" +
                        "    Person(age < 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final DataSource<Person> persons = executor.newDataSource("persons",
                                                                      new Person("Mario", 42),
                                                                      new Person("Marilena", 44),
                                                                      new Person("Sofia", 4));
            // explicitly create unit
            assertEquals(2, executor.run(new AdultUnit(persons)));
            // let RuleUnitExecutor internally create and wire the unit instance
            assertEquals(1, executor.run(NotAdultUnit.class));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testBindDataSource() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "import " + NotAdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    Person(age >= 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "end\n" +
                        "rule NotAdult @Unit( NotAdultUnit.class ) when\n" +
                        "    Person(age < 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final DataSource<Person> persons = DataSource.create(new Person("Mario", 42),
                                                                 new Person("Marilena", 44),
                                                                 new Person("Sofia", 4));
            executor.bindVariable("persons", persons);
            // explicitly create unit
            assertEquals(2, executor.run(AdultUnit.class));
            // let RuleUnitExecutor internally create and wire the unit instance
            assertEquals(1, executor.run(NotAdultUnit.class));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testUnboundDataSource() {
        // DROOLS-1533
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "import " + NotAdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    Person(age >= 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "end\n" +
                        "rule NotAdult @Unit( NotAdultUnit.class ) when\n" +
                        "    Person(age < 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final DataSource<Person> persons = DataSource.create(new Person("Mario", 42),
                                                                 new Person("Marilena", 44),
                                                                 new Person("Sofia", 4));
            // explicitly create unit
            assertEquals(2, executor.run(new AdultUnit(persons)));
            // let RuleUnitExecutor internally create and wire the unit instance
            assertEquals(1, executor.run(new NotAdultUnit(persons)));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testRuleWithoutUnitsIsNotExecutor() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "import " + NotAdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult when\n" +
                        "    Person(age >= 18, $name : name)\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "end\n" +
                        "rule NotAdult when\n" +
                        "    Person(age < 18, $name : name)\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        Assertions.assertThatThrownBy(() -> RuleUnitExecutor.create().bind(kbase)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testRunUnexistingUnit() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "import " + NotAdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    Person(age >= 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            Assertions.assertThatThrownBy(() -> executor.run(NotAdultUnit.class)).isInstanceOf(IllegalStateException.class);
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testDisallowToMixRulesWithAndWithoutUnit() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "import " + NotAdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    Person(age >= 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "end\n" +
                        "rule NotAdult when\n" +
                        "    Person(age < 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "end";

        Assertions.assertThatThrownBy(() -> KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void testRuleUnitInvocationFromConsequence() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "import " + NotAdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    Person(age >= 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "end\n" +
                        "rule NotAdult @Unit( NotAdultUnit.class ) when\n" +
                        "    $p : Person(age < 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "    modify($p) { setAge(18); }\n" +
                        "    drools.run( AdultUnit.class );" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            executor.newDataSource("persons",
                                   new Person("Mario", 42),
                                   new Person("Marilena", 44),
                                   new Person("Sofia", 4));
            final List<String> log = new ArrayList<>();
            executor.bindVariable("log", log);

            assertEquals(4, executor.run(NotAdultUnit.class));

            final List<String> expectedLogs = asList(RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit started.",
                                                     RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit yielded to org.drools.compiler.integrationtests.RuleUnitTest$AdultUnit",
                                                     RuleUnitTest.class.getCanonicalName() + "$AdultUnit started.",
                                                     RuleUnitTest.class.getCanonicalName() + "$AdultUnit ended.",
                                                     RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit started.",
                                                     RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit ended.");
            assertEquals(expectedLogs, log);
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testStackedRuleUnitInvocationFromConsequence() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "import " + NotAdultUnit.class.getCanonicalName() + "\n" +
                        "rule NotAdult @Unit( NotAdultUnit.class ) when\n" +
                        "    $p : Person(age < 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "    modify($p) { setAge(18); }\n" +
                        "    drools.run( AdultUnit.class );" +
                        "end\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    Person(age >= 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            executor.newDataSource("persons",
                                   new Person("Mario", 2),
                                   new Person("Sofia", 6));
            final List<String> log = new ArrayList<>();
            executor.bindVariable("log", log);

            assertEquals(4, executor.run(NotAdultUnit.class));

            final List<String> expectedLogs = asList(RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit started.",
                                                     RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit yielded to org.drools.compiler.integrationtests.RuleUnitTest$AdultUnit",
                                                     RuleUnitTest.class.getCanonicalName() + "$AdultUnit started.",
                                                     RuleUnitTest.class.getCanonicalName() + "$AdultUnit ended.",
                                                     RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit started.",
                                                     RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit yielded to org.drools.compiler.integrationtests.RuleUnitTest$AdultUnit",
                                                     RuleUnitTest.class.getCanonicalName() + "$AdultUnit started.",
                                                     RuleUnitTest.class.getCanonicalName() + "$AdultUnit ended.",
                                                     RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit started.",
                                                     RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit ended.");
            assertEquals(expectedLogs, log);
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testModifyOnDataSource() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    Person(age >= 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "    results.add($name);\n" +
                        "end\n" +
                        "rule NotAdult @Unit( AdultUnit.class ) when\n" +
                        "    $p : Person(age < 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "    modify($p) { setAge(18); }\n" +
                        "    drools.run( AdultUnit.class );" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            executor.newDataSource("persons",
                                   new Person("Mario", 42),
                                   new Person("Marilena", 44),
                                   new Person("Sofia", 4));
            final List<String> results = new ArrayList<>();
            executor.bindVariable("results", results);

            assertEquals(4, executor.run(AdultUnit.class));
            assertEquals(3, results.size());
            assertTrue(results.containsAll(asList("Mario", "Marilena", "Sofia")));
        } finally {
            executor.dispose();
        }
    }

    public static class AdultUnit implements RuleUnit {

        private int adultAge = 0;
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

    @Test
    public void testNotExistingDataSource() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    Person(age >= 18, $name : name) from adults\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration,
                                                                    false,
                                                                    drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @Test
    public void testReactiveDataSource() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + ReactiveAdultUnit.class.getCanonicalName() + "\n" +
                        "import " + ReactiveNotAdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( ReactiveAdultUnit.class) when\n" +
                        "    Person(age >= 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "end\n" +
                        "rule NotAdult @Unit( ReactiveNotAdultUnit.class ) when\n" +
                        "    Person(age < 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final DataSource<Person> persons = executor.newDataSource("persons", new Person("Mario", 42));

            final ReactiveAdultUnit adultUnit = new ReactiveAdultUnit(persons, null);
            assertEquals(1, executor.run(adultUnit));

            final ReactiveNotAdultUnit notAdultUnit = new ReactiveNotAdultUnit(persons);
            assertEquals(0, executor.run(notAdultUnit));

            persons.insert(new Person("Sofia", 4));
            assertEquals(0, executor.run(adultUnit));
            assertEquals(1, executor.run(notAdultUnit));

            persons.insert(new Person("Marilena", 44));
            assertEquals(1, executor.run(adultUnit));
            assertEquals(0, executor.run(notAdultUnit));
        } finally {
            executor.dispose();
        }
    }

    public static class ReactiveAdultUnit implements RuleUnit {

        private final DataSource<Person> persons;
        private final List<String> list;

        public ReactiveAdultUnit(final DataSource<Person> persons, final List<String> list) {
            this.persons = persons;
            this.list = list;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }

        public List<String> getList() {
            return list;
        }

        @Override
        public void onStart() {
            System.out.println(getUnitName(this) + " started.");
        }

        @Override
        public void onEnd() {
            System.out.println(getUnitName(this) + " ended.");
        }

        @Override
        public void onSuspend() {
            System.out.println(getUnitName(this) + " suspended.");
        }

        @Override
        public void onResume() {
            System.out.println(getUnitName(this) + " resumed.");
        }
    }

    public static class ReactiveNotAdultUnit implements RuleUnit {

        private final DataSource<Person> persons;

        public ReactiveNotAdultUnit(final DataSource<Person> persons) {
            this.persons = persons;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }
    }

    @Test(timeout = 10000L)
    public void testReactiveDataSourceWithRunUntilHalt() throws Exception {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + ReactiveAdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( ReactiveAdultUnit.class ) when\n" +
                        "    Person(age >= 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");" +
                        "    list.add($name);\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final DebugList<String> list = new DebugList<>();
            executor.bindVariable("list", list);

            final DataSource<Person> persons = executor.newDataSource("persons", new Person("Mario", 42));
            final ReactiveAdultUnit adultUnit = new ReactiveAdultUnit(persons, list);

            final Semaphore ready = new Semaphore(0, true);
            list.onItemAdded = (l -> ready.release());

            new Thread(() -> executor.runUntilHalt(adultUnit)).start();

            ready.acquire();

            Assert.assertEquals(1, list.size());
            Assert.assertEquals("Mario", list.get(0));
            list.clear();

            list.onItemAdded = (l -> ready.release());

            persons.insert(new Person("Sofia", 4));
            persons.insert(new Person("Marilena", 44));

            ready.acquire();

            Assert.assertEquals(1, list.size());
            Assert.assertEquals("Marilena", list.get(0));
        } finally {
            executor.halt();
            executor.dispose();
        }
    }

    @Test
    public void testNamingConventionOnDrlFile() {
        final String drl1 =
                "package org.kie.test;\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule Adult when\n" +
                        "    $p : /persons[age >= 18]\n" +
                        "then\n" +
                        "    System.out.println($p.getName() + \" is adult\");\n" +
                        "end";

        final String javaRuleUnit =
                "package org.kie.test;\n" +
                        "\n" +
                        "import " + Person.class.getCanonicalName() + ";\n" +
                        "import " + RuleUnit.class.getCanonicalName() + ";\n" +
                        "import " + DataSource.class.getCanonicalName() + ";\n" +
                        "\n" +
                        "public class MyRuleUnit implements RuleUnit {\n" +
                        "    private DataSource<Person> persons;\n" +
                        "\n" +
                        "    public DataSource<Person> getPersons() {\n" +
                        "        return persons;\n" +
                        "    }\n" +
                        "}\n";

        final KieModuleModel kieModuleModel = KieUtil.getKieModuleModel(kieBaseTestConfiguration,
                                                                        KieSessionTestConfiguration.STATEFUL_REALTIME,
                                                                        new HashMap<>());
        final KieServices ks = KieServices.get();
        final ReleaseId releaseId = ks.newReleaseId(UUID.randomUUID().toString(), "test-artifact", "1.0");
        final String path = "org/kie/test/MyRuleUnit";
        final KieFileSystem kfs = ks.newKieFileSystem();
        kfs.generateAndWritePomXML(releaseId);
        kfs.writeKModuleXML(kieModuleModel.toXML())
                .write("src/main/resources/" + path + ".drl", drl1)
                .write("src/main/java/" + path + ".java", javaRuleUnit);

        KieUtil.buildAndInstallKieModuleIntoRepo(kieBaseTestConfiguration, kfs);
        final KieContainer kcontainer = ks.newKieContainer(releaseId);
        final KieBase kbase = kcontainer.getKieBase();
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final DataSource<Person> persons = executor.newDataSource("persons",
                                                                      new Person("Mario", 42));

            final RuleUnit ruleUnit = new RuleUnitFactory().bindVariable("persons", persons)
                    .getOrCreateRuleUnit((( InternalRuleUnitExecutor ) executor), "org.kie.test.MyRuleUnit", kcontainer.getClassLoader());

            assertEquals(1, executor.run(ruleUnit));

            persons.insert(new Person("Sofia", 4));
            assertEquals(0, executor.run(ruleUnit));

            persons.insert(new Person("Marilena", 44));
            assertEquals(1, executor.run(ruleUnit));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testWithOOPath() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    $p : /persons[age >= 18]\n" +
                        "then\n" +
                        "    System.out.println($p.getName() + \" is adult\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final DataSource<Person> persons = executor.newDataSource("persons",
                                                                      new Person("Mario", 42),
                                                                      new Person("Marilena", 44),
                                                                      new Person("Sofia", 4));

            final RuleUnit adultUnit = new AdultUnit(persons);
            assertEquals(2, executor.run(adultUnit));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testWithOOPathAndNot() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    not /persons[age >= 18]\n" +
                        "then\n" +
                        "    System.out.println(\"No adults\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final DataSource<Person> persons = executor.newDataSource("persons",
                                                                      new Person("Mario", 4),
                                                                      new Person("Marilena", 17),
                                                                      new Person("Sofia", 4));

            final RuleUnit adultUnit = new AdultUnit(persons);
            assertEquals(1, executor.run(adultUnit));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testWithOOPathAndNotNoMatch() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    not /persons[age >= 18]\n" +
                        "then\n" +
                        "    System.out.println(\"No adults\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final DataSource<Person> persons = executor.newDataSource("persons",
                                                                      new Person("Mario", 44),
                                                                      new Person("Marilena", 170),
                                                                      new Person("Sofia", 18));

            final RuleUnit adultUnit = new AdultUnit(persons);
            assertEquals(0, executor.run(adultUnit));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testVarResolution() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    $p : /persons[age >= adultAge]\n" +
                        "then\n" +
                        "    System.out.println($p.getName() + \" is adult and greater than \" + adultAge);\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            executor.newDataSource("persons",
                                   new Person("Mario", 42),
                                   new Person("Marilena", 44),
                                   new Person("Sofia", 4));
            executor = executor.bindVariable("adultAge", 18);
            assertEquals(2, executor.run(AdultUnit.class));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testUnitDeclaration() {
        final String drl1 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(AdultUnit.class) + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule Adult when\n" +
                        "    Person(age >= adultAge, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "end";

        final String drl2 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(NotAdultUnit.class) + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule NotAdult when\n" +
                        "    $p : Person(age < 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "    modify($p) { setAge(18); }\n" +
                        "    drools.run( AdultUnit.class );\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl1, drl2);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            executor.newDataSource("persons",
                                   new Person("Mario", 42),
                                   new Person("Marilena", 44),
                                   new Person("Sofia", 4));
            final List<String> log = new ArrayList<>();
            executor.bindVariable("log", log)
                    .bindVariable("adultAge", 18);

            assertEquals(4, executor.run(NotAdultUnit.class));

            final List<String> expectedLogs = asList(RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit started.",
                                                     RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit yielded to org.drools.compiler.integrationtests.RuleUnitTest$AdultUnit",
                                                     RuleUnitTest.class.getCanonicalName() + "$AdultUnit started.",
                                                     RuleUnitTest.class.getCanonicalName() + "$AdultUnit ended.",
                                                     RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit started.",
                                                     RuleUnitTest.class.getCanonicalName() + "$NotAdultUnit ended.");
            assertEquals(expectedLogs, log);
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testBindingWithNamedVars() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + NamedVarsUnit.class.getCanonicalName() + "\n" +
                        "rule Adult @Unit( NamedVarsUnit.class ) when\n" +
                        "    $p : /persons[age >= adultAge]\n" +
                        "then\n" +
                        "    System.out.println($p.getName() + \" is adult and greater than \" + adultAge);\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            executor.newDataSource("data",
                                   new Person("Mario", 42),
                                   new Person("Marilena", 44),
                                   new Person("Sofia", 4));
            executor.bindVariable("minAge", 18);

            assertEquals(2, executor.run(NamedVarsUnit.class));
        } finally {
            executor.dispose();
        }
    }

    public static class NamedVarsUnit implements RuleUnit {

        @UnitVar("minAge")
        private int adultAge = 0;
        @UnitVar("data")
        private DataSource<Person> persons;

        public NamedVarsUnit() {
        }

        public DataSource<Person> getPersons() {
            return persons;
        }

        public int getAdultAge() {
            return adultAge;
        }
    }

    @Test
    public void testGuardedUnit() {
        final String drl1 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(BoxOfficeUnit.class) + ";\n" +
                        "import " + BoxOffice.class.getCanonicalName() + "\n" +
                        "import " + TicketIssuerUnit.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule BoxOfficeIsOpen when\n" +
                        "    $box: /boxOffices[ open ]\n" +
                        "then\n" +
                        "    drools.guard( TicketIssuerUnit.class );" +
                        "end";

        final String drl2 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(TicketIssuerUnit.class) + ";\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultTicket.class.getCanonicalName() + "\n" +
                        "rule IssueAdultTicket when\n" +
                        "    $p: /persons[ age >= 18 ]\n" +
                        "then\n" +
                        "    tickets.insert(new AdultTicket($p));\n" +
                        "end\n" +
                        "rule RegisterAdultTicket when\n" +
                        "    $t: /tickets\n" +
                        "then\n" +
                        "    results.add( $t.getPerson().getName() );\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl1, drl2);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final DataSource<Person> persons = executor.newDataSource("persons");
            final DataSource<BoxOffice> boxOffices = executor.newDataSource("boxOffices");
            executor.newDataSource("tickets");

            final List<String> list = new ArrayList<>();
            executor.bindVariable("results", list);

            // two open box offices
            final BoxOffice office1 = new BoxOffice(true);
            final FactHandle officeFH1 = boxOffices.insert(office1);
            final BoxOffice office2 = new BoxOffice(true);
            final FactHandle officeFH2 = boxOffices.insert(office2);

            persons.insert(new Person("Mario", 40));
            executor.run(BoxOfficeUnit.class); // fire BoxOfficeIsOpen -> run TicketIssuerUnit -> fire RegisterAdultTicket

            assertEquals(1, list.size());
            assertEquals("Mario", list.get(0));
            list.clear();

            persons.insert(new Person("Matteo", 30));
            executor.run(BoxOfficeUnit.class); // fire RegisterAdultTicket

            assertEquals(1, list.size());
            assertEquals("Matteo", list.get(0));
            list.clear();

            // close one box office, the other is still open
            office1.setOpen(false);
            boxOffices.update(officeFH1, office1);
            persons.insert(new Person("Mark", 35));
            executor.run(BoxOfficeUnit.class);

            assertEquals(1, list.size());
            assertEquals("Mark", list.get(0));
            list.clear();

            // all box offices, are now closed
            office2.setOpen(false);
            boxOffices.update(officeFH2, office2); // guarding rule no longer true
            persons.insert(new Person("Edson", 35));
            executor.run(BoxOfficeUnit.class); // no fire

            assertEquals(0, list.size());
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testComplexData() {
        final String drl =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(ComplexDataUnit.class) + "\n" +
                        "import " + RequestData.class.getCanonicalName() + "\n" +
                        "import " + ParameterHolder.class.getCanonicalName() + "\n" +
                        "import " + InternalData.class.getCanonicalName() + "\n" +
                        "import " + InternalDataFactory.class.getCanonicalName() + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "\n" +
                        "\n" +
                        "rule 'Extract Named Parameter out of Request - Name'\n" +
                        "when\n" +
                        "   $request: RequestData( $id: requestId != null, modelId != null, $parameters: parameters != null ) from requestData\n" +
                        "   $param: ParameterHolder( capitalizedName == \"Name\" ) from $parameters\n" +
                        "then\n" +
                        "   System.out.printf(\"Parameter found: %s with value of %s%n\",$param.getCapitalizedName(),$param.getValue());\n" +
                        "   InternalData d = InternalDataFactory.get().createInternalData($param);\n" +
                        "   insert(d); \n" +
                        "end\n" +
                        "\n" +
                        "\n" +
                        "rule 'Extract Named Parameter out of Request - LikesBeets'\n" +
                        "when\n" +
                        "   $request: RequestData( $id: requestId != null, modelId != null, $parameters: parameters != null ) from requestData\n" +
                        "   $param: ParameterHolder( capitalizedName == \"LikesBeets\" ) from $parameters\n" +
                        "then\n" +
                        "   System.out.printf(\"Parameter found: %s with value of %s%n\",$param.getCapitalizedName(),$param.getValue());\n" +
                        "   InternalData d = InternalDataFactory.get().createInternalData($param);\n" +
                        "   insert(d); \n" +
                        "end\n" +
                        "\n" +
                        "\n" +
                        "rule 'Extract Named Parameter out of Request - HairColor'\n" +
                        "when\n" +
                        "   $request: RequestData( $id: requestId != null, modelId != null, $parameters: parameters != null ) from requestData\n" +
                        "   $param: ParameterHolder( capitalizedName == \"HairColor\" ) from $parameters\n" +
                        "then\n" +
                        "   System.out.printf(\"Parameter found: %s with value of %s%n\",$param.getCapitalizedName(),$param.getValue());\n" +
                        "   InternalData d = InternalDataFactory.get().createInternalData($param);\n" +
                        "   insert(d); \n" +
                        "end\n" +
                        "\n" +
                        "rule 'Extract Named Parameter out of Request - Age'\n" +
                        "when\n" +
                        "   $request: RequestData( $id: requestId != null, modelId != null, $parameters: parameters != null ) from requestData\n" +
                        "   $param: ParameterHolder( capitalizedName == \"Age\" ) from $parameters\n" +
                        "then\n" +
                        "   System.out.printf(\"Parameter found: %s with value of %f%n\",$param.getCapitalizedName(),$param.getValue());\n" +
                        "   InternalData d = InternalDataFactory.get().createInternalData($param);\n" +
                        "   insert(d); \n" +
                        "end\n" +
                        "\n" +
                        "rule 'Check InternalData is inserted - Age' \n" +
                        "when\n" +
                        "   $i: InternalData( capitalizedName == \"Age\" )\n" +
                        "then\n" +
                        "   System.out.printf(\"Actual Age: %f   Factored Age: %f%n\",$i.getValue(),$i.getTransformed()); \n" +
                        "end\n" +
                        "";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final RequestData request = new RequestData("123", "simple");
            request.addParameter("name", "Lance", String.class);
            request.addParameter("age", 54.5, Double.class);
            request.addParameter("hairColor", "bald", String.class);
            request.addParameter("likesBeets", false, Boolean.class);

            executor.newDataSource("requestData", request);
            assertEquals(5, executor.run(ComplexDataUnit.class));
        } finally {
            executor.dispose();
        }
    }

    public static class InternalData<T> {

        private String requestId;
        private String name;
        private T value;
        private Class<T> type;

        protected InternalData(final String requestId, final String name, final T value, final Class<T> type) {
            this.requestId = requestId;
            this.name = name;
            this.value = value;
            this.type = type;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(final String requestId) {
            this.requestId = requestId;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public T getValue() {
            return value;
        }

        public void setValue(final T value) {
            this.value = value;
        }

        public Class<T> getType() {
            return type;
        }

        public void setType(final Class<T> type) {
            this.type = type;
        }

        public String getCapitalizedName() {
            return name.substring(0, 1).toUpperCase() + name.substring(1);
        }

        public T getTransformed() {
            return null;
        }
    }

    public static class InternalDataFactory {

        private static final InternalDataFactory instance = new InternalDataFactory();

        public static InternalDataFactory get() {
            return instance;
        }

        public InternalData createInternalData(final ParameterHolder ph) {
            if (String.class.isAssignableFrom(ph.getType())) {
                return new StringData(ph.getRequestId(), ph.getParmName(), (String) ph.getValue());
            }
            if (Double.class.isAssignableFrom(ph.getType())) {
                return new DoubleData(ph.getRequestId(), ph.getParmName(), (Double) ph.getValue());
            }
            if (Boolean.class.isAssignableFrom(ph.getType())) {
                return new BooleanData(ph.getRequestId(), ph.getParmName(), (Boolean) ph.getValue());
            }
            return null;
        }
    }

    public static class StringData extends InternalData<String> {

        public StringData(final String requestId, final String name, final String value) {
            super(requestId, name, value, String.class);
        }

        @Override
        public String getTransformed() {
            return getValue().toUpperCase();
        }
    }

    public static class DoubleData extends InternalData<Double> {

        private Double factor = 1.5;

        public DoubleData(final String requestId, final String name, final Double value) {
            super(requestId, name, value, Double.class);
        }

        public Double getFactor() {
            return factor;
        }

        public void setFactor(final Double factor) {
            this.factor = factor;
        }

        @Override
        public Double getTransformed() {
            return getValue() * factor;
        }
    }

    public static class BooleanData extends InternalData<Boolean> {

        public BooleanData(final String requestId, final String name, final Boolean value) {
            super(requestId, name, value, Boolean.class);
        }

        @Override
        public Boolean getTransformed() {
            return !getValue();
        }
    }

    public static class ParameterHolder<T> {

        private String requestId;
        private String parmName;
        private T value;
        private Class<T> type;

        public ParameterHolder(final String requestId, final String parmName, final T value, final Class<T> type) {
            this.requestId = requestId;
            this.parmName = parmName;
            this.value = value;
            this.type = type;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(final String requestId) {
            this.requestId = requestId;
        }

        public String getParmName() {
            return parmName;
        }

        public void setParmName(final String parmName) {
            this.parmName = parmName;
        }

        public T getValue() {
            return value;
        }

        public void setValue(final T value) {
            this.value = value;
        }

        public Class<T> getType() {
            return type;
        }

        public void setType(final Class<T> type) {
            this.type = type;
        }

        public String getCapitalizedName() {
            return parmName.substring(0, 1).toUpperCase() + parmName.substring(1);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((parmName == null) ? 0 : parmName.hashCode());
            result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final ParameterHolder other = (ParameterHolder) obj;
            if (parmName == null) {
                if (other.parmName != null) {
                    return false;
                }
            } else if (!parmName.equals(other.parmName)) {
                return false;
            }
            if (requestId == null) {
                return other.requestId == null;
            } else {
                return requestId.equals(other.requestId);
            }
        }
    }

    public static class RequestData {

        private String requestId;
        private String modelId;
        private final List<ParameterHolder<?>> parameters;

        public RequestData(final String requestId, final String modelId) {
            super();
            this.requestId = requestId;
            this.modelId = modelId;
            this.parameters = new ArrayList<>();
        }

        public String getRequestId() {
            return requestId;
        }

        public void setRequestId(final String requestId) {
            this.requestId = requestId;
        }

        public String getModelId() {
            return modelId;
        }

        public void setModelId(final String modelId) {
            this.modelId = modelId;
        }

        public List<ParameterHolder<?>> getParameters() {
            return parameters;
        }

        public <T> boolean addParameter(final ParameterHolder<T> parameter) {
            return parameters.add(parameter);
        }

        public <T> boolean addParameter(final String parameterName, final T value, final Class<T> type) {
            final ParameterHolder<T> parameter = new ParameterHolder<>(this.requestId, parameterName, value, type);
            return addParameter(parameter);
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((modelId == null) ? 0 : modelId.hashCode());
            result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
            return result;
        }

        @Override
        public boolean equals(final Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final RequestData other = (RequestData) obj;
            if (modelId == null) {
                if (other.modelId != null) {
                    return false;
                }
            } else if (!modelId.equals(other.modelId)) {
                return false;
            }
            if (requestId == null) {
                return other.requestId == null;
            } else {
                return requestId.equals(other.requestId);
            }
        }
    }

    public static class ComplexDataUnit implements RuleUnit {

        private DataSource<RequestData> requestData;

        public ComplexDataUnit() {
        }

        public ComplexDataUnit(final DataSource<RequestData> requestData) {
            this.requestData = requestData;
        }

        public DataSource<RequestData> getRequestData() {
            return requestData;
        }
    }

    public static class BoxOffice {

        private boolean open;

        public BoxOffice(final boolean open) {
            this.open = open;
        }

        public boolean isOpen() {
            return open;
        }

        public void setOpen(final boolean open) {
            this.open = open;
        }
    }

    public static class AdultTicket {

        private final Person person;

        public AdultTicket(final Person person) {
            this.person = person;
        }

        public Person getPerson() {
            return person;
        }
    }

    public static class BoxOfficeUnit implements RuleUnit {

        private DataSource<BoxOffice> boxOffices;

        public DataSource<BoxOffice> getBoxOffices() {
            return boxOffices;
        }
    }

    public static class TicketIssuerUnit implements RuleUnit {

        private DataSource<Person> persons;
        private DataSource<AdultTicket> tickets;

        private List<String> results;

        public TicketIssuerUnit() {
        }

        public TicketIssuerUnit(final DataSource<Person> persons, final DataSource<AdultTicket> tickets) {
            this.persons = persons;
            this.tickets = tickets;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }

        public DataSource<AdultTicket> getTickets() {
            return tickets;
        }

        public List<String> getResults() {
            return results;
        }
    }

    @Test
    public void testMultiLevelGuards() {
        final String drl1 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(Unit0.class) + "\n" +
                        "import " + UnitA.class.getCanonicalName() + "\n" +
                        "rule X when\n" +
                        "    $b: /ds#Boolean\n" +
                        "then\n" +
                        "    Boolean b = $b;\n" +
                        "    drools.guard( UnitA.class );\n" +
                        "end";

        final String drl2 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(UnitA.class) + "\n" +
                        "import " + UnitB.class.getCanonicalName() + "\n" +
                        "rule A when\n" +
                        "    $s: /ds#String\n" +
                        "then\n" +
                        "    drools.guard( UnitB.class );" +
                        "end";

        final String drl3 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(UnitB.class) + "\n" +
                        "import " + UnitB.class.getCanonicalName() + "\n" +
                        "rule B when\n" +
                        "    $i: /ds#Integer\n" +
                        "then\n" +
                        "    list.add($i);" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl1, drl2, drl3);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final DataSource<Object> ds = executor.newDataSource("ds");

            final List<Integer> list = new ArrayList<>();
            executor.bindVariable("list", list);

            ds.insert(1);
            executor.run(Unit0.class);
            assertEquals(0, list.size()); // all units are inactive

            final FactHandle guardA = ds.insert(true);
            executor.run(Unit0.class);
            assertEquals(0, list.size()); // UnitB still inactive

            ds.insert("test");
            executor.run(Unit0.class);
            assertEquals(1, list.size()); // all units are active
            assertEquals(1, (int) list.get(0)); // all units are active
            list.clear();

            ds.insert(2);
            executor.run(Unit0.class);
            assertEquals(1, list.size()); // all units are inactive
            assertEquals(2, (int) list.get(0)); // all units are active
            list.clear();

            ds.delete(guardA); // retracting guard A deactivate unitA and in cascade unit B
            ds.insert(3);
            executor.run(Unit0.class);
            assertEquals(0, list.size()); // all units are inactive

            ds.insert(true); // activating guard A reactivate unitA and in cascade unit B
            executor.run(Unit0.class);
            assertEquals(1, list.size()); // all units are active
            list.clear();
        } finally {
            executor.dispose();
        }
    }

    public static class Unit0 implements RuleUnit {

        private DataSource<Object> ds;

        public DataSource<Object> getDs() {
            return ds;
        }
    }

    public static class UnitA implements RuleUnit {

        private DataSource<Object> ds;

        public DataSource<Object> getDs() {
            return ds;
        }
    }

    public static class UnitB implements RuleUnit {

        private DataSource<Object> ds;
        private List<Integer> list;

        public DataSource<Object> getDs() {
            return ds;
        }

        public List<Integer> getList() {
            return list;
        }
    }

    @Test
    public void testRuleUnitIdentity() {
        final String drl1 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(Unit0.class) + "\n" +
                        "import " + AgeCheckUnit.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule R1 when\n" +
                        "    $i: /ds#Integer\n" +
                        "then\n" +
                        "    drools.guard( new AgeCheckUnit($i) );" +
                        "end\n" +
                        "rule RegisterAdultTicket when\n" +
                        "    $s: /ds#String\n" +
                        "then\n" +
                        "    drools.guard( new AgeCheckUnit($s.length()) );" +
                        "end";

        final String drl2 =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(AgeCheckUnit.class) + ";\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule CheckAge when\n" +
                        "    $p : /persons[ age > minAge ]\n" +
                        "then\n" +
                        "    list.add($p.getName() + \">\" + minAge);\n" +
                        "end";

        try {
            IndexTestUtil.disableRangeIndexForJoin(); // See comment in DROOLS-5910

            final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl1, drl2);
            final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
            try {
                final DataSource<Object> ds = executor.newDataSource("ds");
                executor.newDataSource("persons",
                                       new Person("Mario", 42),
                                       new Person("Sofia", 4));

                final List<String> list = new ArrayList<>();
                executor.bindVariable("list", list);

                ds.insert("test");
                ds.insert(3);
                ds.insert(4);
                executor.run(Unit0.class);

                assertEquals(3, list.size());
                assertTrue(list.containsAll(asList("Mario>4", "Mario>3", "Sofia>3")));

                list.clear();
                ds.insert("xxx");
                ds.insert("yyyy");
                executor.run(Unit0.class);
                assertEquals(0, list.size());
            } finally {
                executor.dispose();
            }
        } finally {
            IndexTestUtil.enableRangeIndexForJoin();
        }
    }

    public static class AgeCheckUnit implements RuleUnit {

        private final int minAge;
        private DataSource<Person> persons;
        private List<String> list;

        public AgeCheckUnit(final int minAge) {
            this.minAge = minAge;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }

        public int getMinAge() {
            return minAge;
        }

        public List<String> getList() {
            return list;
        }

        @Override
        public Identity getUnitIdentity() {
            return new Identity(getClass(), minAge);
        }

        @Override
        public String toString() {
            return "AgeCheckUnit(" + minAge + ")";
        }
    }

    @Test(timeout = 10000L)
    public void testPropertyReactiveModify() {
        final String drl =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(AdultUnit.class) + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule Adult when\n" +
                        "    $p: /persons[ age < 18 ]\n" +
                        "then\n" +
                        "    System.out.println($p.getName() + \" is NOT adult\");\n" +
                        "    modify($p) { setHappy(true); }\n" +
                        "end";

        final KieServices ks = KieServices.Factory.get();
        final Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put(PropertySpecificOption.PROPERTY_NAME, PropertySpecificOption.ALWAYS.toString());
        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-property-reactive-modify", "1");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_REALTIME,
                                     kieModuleConfigurationProperties, drl);
        final KieContainer container = ks.newKieContainer(releaseId1);
        final KieBase kbase = container.getKieBase();
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final Person mario = new Person("Mario", 42);
            final Person sofia = new Person("Sofia", 4);

            final DataSource<Person> persons = executor.newDataSource("persons");
            persons.insert(mario);
            final FactHandle sofiaFh = persons.insert(sofia);

            executor.run(AdultUnit.class);

            assertTrue(sofia.isHappy());
            assertFalse(mario.isHappy());

            sofia.setAge(5);
            persons.update(sofiaFh, sofia, "age");
            assertEquals(1, executor.run(AdultUnit.class));

            sofia.setHair("Brown");
            persons.update(sofiaFh, sofia, "hair");
            assertEquals(0, executor.run(AdultUnit.class));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testTwoPartsOOPath() {
        // DROOLS-1539
        final String drl =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(AdultUnit.class) + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + LongAddress.class.getCanonicalName() + "\n" +
                        "rule Adult when\n" +
                        "    $a: /persons[ age > 18 ]/addresses#LongAddress[ country == \"it\" ]\n" +
                        "then\n" +
                        "    System.out.println($a.getCountry());\n" +
                        "end";

        final KieServices ks = KieServices.Factory.get();
        final Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put(PropertySpecificOption.PROPERTY_NAME, PropertySpecificOption.ALWAYS.toString());
        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-two-parts-oopath", "1");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_REALTIME,
                                     kieModuleConfigurationProperties, drl);
        final KieContainer container = ks.newKieContainer(releaseId1);
        final KieBase kbase = container.getKieBase();
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final Person mario = new Person("Mario", 43);
            mario.setAddresses(Collections.singletonList(new LongAddress("street", "suburb", "zipCode", "it")));
            final Person mark = new Person("Mark", 40);
            mark.setAddresses(Collections.singletonList(new LongAddress("street", "suburb", "zipCode", "uk")));
            executor.newDataSource("persons", mario, mark);

            assertEquals(1, executor.run(AdultUnit.class));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testNestedOOPath() {
        // DROOLS-1539
        final String drl =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(AdultUnit.class) + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + LongAddress.class.getCanonicalName() + "\n" +
                        "rule Adult when\n" +
                        "    $p: /persons[ age > 18, $a: /addresses#LongAddress[ country == \"it\" ] ]\n" +
                        "then\n" +
                        "    System.out.println($p.getName() + \" is in \" + $a.getCountry());\n" +
                        "end";

        final KieServices ks = KieServices.Factory.get();
        final Map<String, String> kieModuleConfigurationProperties = new HashMap<>();
        kieModuleConfigurationProperties.put(PropertySpecificOption.PROPERTY_NAME, PropertySpecificOption.ALWAYS.toString());
        final ReleaseId releaseId1 = ks.newReleaseId("org.kie", "test-nested-oopath", "1");
        KieUtil.getKieModuleFromDrls(releaseId1, kieBaseTestConfiguration, KieSessionTestConfiguration.STATEFUL_REALTIME,
                                     kieModuleConfigurationProperties, drl);
        final KieContainer container = ks.newKieContainer(releaseId1);
        final KieBase kbase = container.getKieBase();
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final Person mario = new Person("Mario", 43);
            mario.setAddresses(Collections.singletonList(new LongAddress("street", "suburb", "zipCode", "it")));
            final Person mark = new Person("Mark", 40);
            mark.setAddresses(Collections.singletonList(new LongAddress("street", "suburb", "zipCode", "uk")));

            executor.newDataSource("persons", mario, mark);

            assertEquals(1, executor.run(AdultUnit.class));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testWithOOPathOnList() {
        // DROOLS-1646
        final String drl =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(AdultUnitWithList.class) + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule Adult when\n" +
                        "    $p : /persons[age >= adultAge]\n" +
                        "then\n" +
                        "    System.out.println($p.getName() + \" is adult\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final List<Person> persons = asList(new Person("Mario", 42),
                                                new Person("Marilena", 44),
                                                new Person("Sofia", 4));

            final RuleUnit adultUnit = new AdultUnitWithList(persons);
            assertEquals(2, executor.run(adultUnit));
        } finally {
            executor.dispose();
        }
    }

    public static class AdultUnitWithList implements RuleUnit {

        private final int adultAge = 18;
        private List<Person> persons;

        public AdultUnitWithList() {
        }

        public AdultUnitWithList(final List<Person> persons) {
            this.persons = persons;
        }

        public List<Person> getPersons() {
            return persons;
        }

        public int getAdultAge() {
            return adultAge;
        }
    }

    @Test
    public void testWithOOPathOnArray() {
        // DROOLS-1646
        final String drl =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(AdultUnitWithArray.class) + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule Adult when\n" +
                        "    $p : /persons[age >= adultAge]\n" +
                        "then\n" +
                        "    System.out.println($p.getName() + \" is adult\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final RuleUnit adultUnit = new AdultUnitWithArray(new Person("Mario", 42),
                                                              new Person("Marilena", 44),
                                                              new Person("Sofia", 4));
            assertEquals(2, executor.run(adultUnit));
        } finally {
            executor.dispose();
        }
    }

    public static class AdultUnitWithArray implements RuleUnit {

        private final int adultAge = 18;
        private Person[] persons;

        public AdultUnitWithArray() {
        }

        public AdultUnitWithArray(final Person... persons) {
            this.persons = persons;
        }

        public Person[] getPersons() {
            return persons;
        }

        public int getAdultAge() {
            return adultAge;
        }
    }

    @Test
    public void testWithOOPathOnSingleItem() {
        // DROOLS-1646
        final String drl =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(AdultUnitWithSingleItem.class) + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule Adult when\n" +
                        "    $p : /person[age >= adultAge]\n" +
                        "then\n" +
                        "    System.out.println($p.getName() + \" is adult\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final RuleUnit adultUnit = new AdultUnitWithSingleItem(new Person("Mario", 42));
            assertEquals(1, executor.run(adultUnit));
        } finally {
            executor.dispose();
        }
    }

    public static class AdultUnitWithSingleItem implements RuleUnit {

        private final int adultAge = 18;
        private Person person;

        public AdultUnitWithSingleItem() {
        }

        public AdultUnitWithSingleItem(final Person person) {
            this.person = person;
        }

        public Person getPerson() {
            return person;
        }

        public int getAdultAge() {
            return adultAge;
        }
    }

    @Test(timeout = 10000L)
    public void testReactiveOnUnitCreatingDataSource() throws Exception {
        // DROOLS-1647
        final String drl =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(AdultUnitCreatingDataSource.class) + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule Adult when\n" +
                        "    Person(age >= 18, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");" +
                        "    list.add($name);\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final DebugList<String> list = new DebugList<>();
            executor.bindVariable("list", list);

            final AdultUnitCreatingDataSource adultUnit = new AdultUnitCreatingDataSource(list);
            adultUnit.insertPerson(new Person("Mario", 42));

            final Semaphore ready = new Semaphore(0, true);
            list.onItemAdded = (l -> ready.release());

            new Thread(() -> executor.runUntilHalt(adultUnit)).start();

            ready.acquire();

            Assert.assertEquals(1, list.size());
            Assert.assertEquals("Mario", list.get(0));
            list.clear();

            list.onItemAdded = (l -> ready.release());

            adultUnit.insertPerson(new Person("Sofia", 4));
            adultUnit.insertPerson(new Person("Marilena", 44));

            ready.acquire();

            Assert.assertEquals(1, list.size());
            Assert.assertEquals("Marilena", list.get(0));
        } finally {
            executor.halt();
            executor.dispose();
        }
    }

    public static class AdultUnitCreatingDataSource implements RuleUnit {

        private final DataSource<Person> persons;
        private final List<String> list;

        public AdultUnitCreatingDataSource(final List<String> list) {
            this.persons = DataSource.create();
            this.list = list;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }

        public List<String> getList() {
            return list;
        }

        public void insertPerson(final Person person) {
            persons.insert(person);
        }
    }

    @Test
    public void testRuleUnitFromKieContainer() {
        final String drl =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(AdultUnitWithSingleItem.class) + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule Adult when\n" +
                        "    $p : /person[age >= adultAge]\n" +
                        "then\n" +
                        "    System.out.println($p.getName() + \" is adult\");\n" +
                        "end";

        final KieContainer kieContainer = KieUtil.getKieContainerFromDrls(kieBaseTestConfiguration,
                                                                          KieSessionTestConfiguration.STATEFUL_PSEUDO,
                                                                          drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.newRuleUnitExecutor( kieContainer );
        try {
            assertTrue(executor.getKieSession().getSessionClock() instanceof SessionPseudoClock);
            final RuleUnit adultUnit = new AdultUnitWithSingleItem(new Person("Mario", 42));
            assertEquals(1, executor.run(adultUnit));
        } finally {
            executor.dispose();
        }
    }

    public static class FlowUnit implements RuleUnit {

    }

    @Test
    public void testRunOrder() {
        // DROOLS-2199
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + FlowUnit.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "import " + NotAdultUnit.class.getCanonicalName() + "\n" +
                        "global java.util.List list;\n" +
                        "rule Flow @Unit( FlowUnit.class ) when\n" +
                        "then\n" +
                        "    drools.run( NotAdultUnit.class );\n" +
                        "    drools.run( AdultUnit.class );\n" +
                        "end\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    Person(age >= 18, $name : name) from persons\n" +
                        "then\n" +
                        "    list.add($name + \" is adult\");\n" +
                        "end\n" +
                        "rule NotAdult @Unit( NotAdultUnit.class ) when\n" +
                        "    Person(age < 18, $name : name) from persons\n" +
                        "then\n" +
                        "    list.add($name + \" is NOT adult\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final List<String> list = new ArrayList<>();
            executor.getKieSession().setGlobal("list", list);
            executor.newDataSource("persons",
                                   new Person("Mario", 42),
                                   new Person("Sofia", 4));
            assertEquals(3, executor.run(FlowUnit.class));
            assertEquals(list, asList("Sofia is NOT adult", "Mario is adult"));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testRuleUnitWithAccumulate() {
        // DROOLS-2209
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "global java.util.List list;\n" +
                        "rule AccumulateAdults @Unit( AdultUnit.class ) when\n" +
                        "   accumulate( $p: Person( $age: age >= 18 ) from persons, \n" +
                        "               $sum : sum( $age ) )\n" +
                        "then\n" +
                        "   list.add($sum); \n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            final List<Integer> list = new ArrayList<>();
            executor.getKieSession().setGlobal("list", list);
            executor.newDataSource("persons",
                                   new Person("Mario", 42),
                                   new Person("Marilena", 44),
                                   new Person("Sofia", 4));

            assertEquals(1, executor.run(AdultUnit.class));
            assertEquals(1, list.size());
            assertEquals(86, (int) list.get(0));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testModifyGlobalFact() {
        final String drl =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(AdultUnit.class) + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule Adult when\n" +
                        "    $p : Person(age < 18, $name : name)\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "    modify($p) { setAge(18) }\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            executor.getKieSession().insert(new Person("Sofia", 4));
            assertEquals(1, executor.run(AdultUnit.class));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testModifyGlobalFactWithMvelDialect() {
        final String drl =
                "package org.drools.compiler.integrationtests\n" +
                        "unit " + getCanonicalSimpleName(AdultUnit.class) + "\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "rule Adult dialect \"mvel\" when\n" +
                        "    $p : Person(age < 18, $name : name)\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "    modify($p) { setAge(18) }\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            executor.getKieSession().insert(new Person("Sofia", 4));
            assertEquals(1, executor.run(AdultUnit.class));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testMixingGlobalDataAndDataSource() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + FlowUnit.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "import " + NotAdultUnit.class.getCanonicalName() + "\n" +
                        "rule Flow @Unit( FlowUnit.class ) when\n" +
                        "then\n" +
                        "    insert(18);\n" +
                        "    drools.run( NotAdultUnit.class );\n" +
                        "    drools.run( AdultUnit.class );\n" +
                        "end\n" +
                        "rule Adult @Unit( AdultUnit.class ) when\n" +
                        "    $i : Integer()\n" +
                        "    Person(age >= $i, $name : name) from persons\n" +
                        "then\n" +
                        "    System.out.println($name + \" is adult\");\n" +
                        "end\n" +
                        "rule NotAdult @Unit( NotAdultUnit.class ) when\n" +
                        "    Person($age : age < 18, $name : name) from persons\n" +
                        "    $i : Integer(this >= $age)\n" +
                        "then\n" +
                        "    System.out.println($name + \" is NOT adult\");\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            executor.newDataSource("persons",
                                   new Person("Mario", 42),
                                   new Person("Marilena", 44),
                                   new Person("Sofia", 4));

            assertEquals(4, executor.run(FlowUnit.class));
        } finally {
            executor.dispose();
        }
    }

    @Test
    public void testDeleteFromDataSource() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "import " + FlowUnit.class.getCanonicalName() + "\n" +
                        "import " + AdultUnit.class.getCanonicalName() + "\n" +
                        "import " + FilterUnit.class.getCanonicalName() + "\n" +
                        "rule Flow @Unit( FlowUnit.class ) when\n" +
                        "then\n" +
                        "    drools.run( FilterUnit.class );\n" +
                        "    drools.run( AdultUnit.class );\n" +
                        "end\n" +
                        "rule filter @Unit( FilterUnit.class ) when\n" +
                        "    $p:Person(name str[startsWith] \"D\") from persons\n" +
                        "then\n" +
                        "    System.out.println(\"Deleting person: \" + $p.getName() + \". Sorry man, your name starts with a 'D' ....\");\n" +
                        "    persons.delete( $p );\n" +
                        "end\n" +
                        "rule AdultUnit @Unit( AdultUnit.class ) when\n" +
                        "    Person($age : age > 18, $name : name) from persons\n" +
                        "then\n" +
                        "    results.add($name);\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl);
        final RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);
        try {
            executor.newDataSource("persons",
                                   new Person("Mario", 43),
                                   new Person("Duncan", 34),
                                   new Person("Sofia", 6));

            final List<String> results = new ArrayList<>();
            executor.bindVariable("results", results);

            assertEquals(3, executor.run(FlowUnit.class));
            assertEquals(1, results.size());
            assertEquals("Mario", results.get(0));
        } finally {
            executor.dispose();
        }
    }

    public static class FilterUnit implements RuleUnit {

        private DataSource<Person> persons;

        public DataSource<Person> getPersons() {
            return persons;
        }

        public void setPersons(final DataSource<Person> persons) {
            this.persons = persons;
        }
    }

    public static class MainHouseUnit implements RuleUnit {

        private DataSource<Date> now;
        private DataSource<String> part;
        private DataSource<Boolean> switch1;

        public MainHouseUnit() {
            super();
        }

        public DataSource<Date> getNow() {
            return now;
        }

        public DataSource<String> getPart() {
            return part;
        }

        public DataSource<Boolean> getSwitch1() {
            return switch1;
        }

    }

    public static class DayPartUnit implements RuleUnit {

        private DataSource<Date> now;
        private DataSource<Date> aScopedDS;
        private DataSource<String> part;

        public DayPartUnit() {
            super();
        }

        public DataSource<Date> getNow() {
            return now;
        }

        public DataSource<String> getPart() {
            return part;
        }

        public DataSource<Date> getaScopedDS() {
            return aScopedDS;
        }

    }

    public static class SwitchUnit implements RuleUnit {

        private DataSource<String> part;
        private DataSource<Boolean> switch1;

        public SwitchUnit() {
            super();
        }

        public DataSource<String> getPart() {
            return part;
        }

        public DataSource<Boolean> getSwitch1() {
            return switch1;
        }

    }

    private KieBase kieBaseMainGuardSubunitRunBackToMain(boolean currentStyle) {
        // use "hammer" approach with external multiple call to fire, or "drools.run()" approach in rules.
        System.out.println("Running with style: " + currentStyle);
        String drl1 = "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName(MainHouseUnit.class) + "\n" +
                "import " + DayPartUnit.class.getCanonicalName() + "\n" +
                "import " + SwitchUnit.class.getCanonicalName() + "\n" +
                "rule GuardDayPartUnit when\n" +
                "    Object() from now \n" +
                "    not( String() from part ) \n" +
                "then\n" +
                "    System.out.println(\"Guarding DayPartUnit\");\n" +
                "    drools.guard(DayPartUnit.class);\n" +
                "end\n" +
                "rule GuardSwitchUnit when\n" +
                "    String() from part \n" +
                "    not( Boolean() from switch1 ) \n" +
                "then\n" +
                "    System.out.println(\"Guarding SwitchUnit\");\n" +
                "    drools.guard(SwitchUnit.class);\n" +
                "end\n";

        String drl2 = "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName(DayPartUnit.class) + "\n" +
                "import " + MainHouseUnit.class.getCanonicalName() + "\n" +
                "rule doDayPartUnit when\n" +
                "    $n : Object() from now \n" +
                "then\n" +
                "    System.out.println(\"Inside DayPartUnit: \"+$n);\n" +
                "    part.insert(\"Morning\");\n" +
                (currentStyle ? "//" : "") + " drools.run(MainHouseUnit.class);\n" +
                "end\n";

        String drl3 = "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName(SwitchUnit.class) + "\n" +
                "import " + MainHouseUnit.class.getCanonicalName() + "\n" +
                "rule doSwitchUnit when\n" +
                "    $n : String() from part \n" +
                "then\n" +
                "    System.out.println(\"Inside SwitchUnit: \"+$n);\n" +
                "    switch1.insert(true);\n" +
                (currentStyle ? "//" : "") + " drools.run(MainHouseUnit.class);\n" +
                "end\n";

        return KieBaseUtil.getKieBaseFromKieModuleFromDrl("rule-unit-test", kieBaseTestConfiguration, drl1, drl2, drl3);
    }

    public static class EmptyUnit implements RuleUnit {

        public EmptyUnit() {
            // no-args constructor.
        }
    }

    public static class StringDSUnit implements RuleUnit {

        private DataSource<String> strings;

        public StringDSUnit() {
            // no-args constructor.
        }

        public DataSource<String> getStrings() {
            return strings;
        }

    }

    @Test
    public void testGuardAndRunBack() {
        String drl1 = "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName(EmptyUnit.class) + "\n" +
                "import " + StringDSUnit.class.getCanonicalName() + "\n" +
                "rule RGuard when\n" +
                "then\n" +
                "    System.out.println(\"Guarding StringDSUnit\");\n" +
                "    drools.guard(StringDSUnit.class);\n" +
                "end\n";

        String drl2 = "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName(StringDSUnit.class) + "\n" +
                "import " + EmptyUnit.class.getCanonicalName() + "\n" +
                "rule RGoBack when\n" +
                "then\n" +
                "    System.out.println(\"Inside StringDSUnit: \");\n" +
                "    drools.run(EmptyUnit.class);\n" +
                "end\n";

        KieBase kbase = new KieHelper().addContent(drl1, ResourceType.DRL)
                .addContent(drl2, ResourceType.DRL)
                .build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind(kbase);

        executor.newDataSource("strings", "abc", "xyz");

        RuleUnit emptyUnit = new EmptyUnit();
        executor.run(emptyUnit);
    }
}
