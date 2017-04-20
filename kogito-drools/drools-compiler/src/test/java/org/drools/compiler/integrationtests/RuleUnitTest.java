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

package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import org.drools.compiler.Person;
import org.drools.compiler.util.debug.DebugList;
import org.drools.core.impl.InternalRuleUnitExecutor;
import org.drools.core.ruleunit.RuleUnitFactory;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Results;
import org.kie.api.definition.rule.UnitVar;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.rule.DataSource;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.RuleUnit;
import org.kie.api.runtime.rule.RuleUnitExecutor;
import org.kie.internal.builder.conf.PropertySpecificOption;
import org.kie.internal.utils.KieHelper;

import static java.util.Arrays.asList;
import static org.drools.core.ruleunit.RuleUnitUtil.getUnitName;
import static org.drools.core.util.ClassUtils.getCanonicalSimpleName;
import static org.junit.Assert.*;

public class RuleUnitTest {

    @Test
    public void testWithDataSource() throws Exception {
        String drl1 =
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

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Person> persons = executor.newDataSource( "persons",
                                                             new Person( "Mario", 42 ),
                                                             new Person( "Marilena", 44 ),
                                                             new Person( "Sofia", 4 ) );

        // explicitly create unit
        assertEquals(2, executor.run( new AdultUnit(persons) ) );

        // let RuleUnitExecutor internally create and wire the unit instance
        assertEquals(1, executor.run( NotAdultUnit.class ) );
    }

    @Test
    public void testBindDataSource() throws Exception {
        String drl1 =
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

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Person> persons = DataSource.create( new Person( "Mario", 42 ),
                                                        new Person( "Marilena", 44 ),
                                                        new Person( "Sofia", 4 ) );

        executor.bindVariable( "persons", persons );

        // explicitly create unit
        assertEquals(2, executor.run( AdultUnit.class ) );

        // let RuleUnitExecutor internally create and wire the unit instance
        assertEquals(1, executor.run( NotAdultUnit.class ) );
    }

    @Test
    public void testUnboundDataSource() throws Exception {
        // DROOLS-1533
        String drl1 =
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

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Person> persons = DataSource.create( new Person( "Mario", 42 ),
                                                        new Person( "Marilena", 44 ),
                                                        new Person( "Sofia", 4 ) );

        // explicitly create unit
        assertEquals(2, executor.run( new AdultUnit(persons) ) );

        // let RuleUnitExecutor internally create and wire the unit instance
        assertEquals(1, executor.run( new NotAdultUnit(persons) ) );
    }

    @Test
    public void testRuleWithoutUnitsIsNotExecutor() throws Exception {
        String drl1 =
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

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();

        try {
            RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );
            fail("It should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testRunUnexistingUnit() throws Exception {
        String drl1 =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "import " + NotAdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    Person(age >= 18, $name : name) from persons\n" +
                "then\n" +
                "    System.out.println($name + \" is adult\");\n" +
                "end";

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        try {
            executor.run( NotAdultUnit.class );
            fail("It should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testDisallowToMixRulesWithAndWithoutUnit() throws Exception {
        String drl1 =
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

        try {
            KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();
            fail("It should throw IllegalStateException");
        } catch (IllegalStateException e) {
            // expected
        }
    }

    @Test
    public void testRuleUnitInvocationFromConsequence() throws Exception {
        String drl1 =
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

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Person> persons = executor.newDataSource( "persons",
                                                             new Person( "Mario", 42 ),
                                                             new Person( "Marilena", 44 ),
                                                             new Person( "Sofia", 4 ) );
        List<String> log = new ArrayList<>();
        executor.bindVariable( "log", log );

        assertEquals(4, executor.run( NotAdultUnit.class ) );

        List<String> expectedLogs = asList("org.drools.compiler.integrationtests.RuleUnitTest.NotAdultUnit started.",
                                           "org.drools.compiler.integrationtests.RuleUnitTest.NotAdultUnit yielded to org.drools.compiler.integrationtests.RuleUnitTest.AdultUnit",
                                           "org.drools.compiler.integrationtests.RuleUnitTest.AdultUnit started.",
                                           "org.drools.compiler.integrationtests.RuleUnitTest.AdultUnit ended.");
        assertEquals( expectedLogs, log );
    }

    @Test
    public void testModifyOnDataSource() throws Exception {
        String drl1 =
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

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Person> persons = executor.newDataSource( "persons",
                                                             new Person( "Mario", 42 ),
                                                             new Person( "Marilena", 44 ),
                                                             new Person( "Sofia", 4 ) );
        List<String> results = new ArrayList<>();
        executor.bindVariable( "results", results );

        assertEquals(4, executor.run( AdultUnit.class ) );
        assertEquals(3, results.size());
        assertTrue(results.containsAll( asList("Mario", "Marilena", "Sofia") ));
    }

    public static class AdultUnit implements RuleUnit {
        private int adultAge = 0;
        private DataSource<Person> persons;
        private List<String> log;
        private List<String> results;

        public AdultUnit( ) { }

        public AdultUnit( DataSource<Person> persons ) {
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
                log.add( getUnitName(this) + " started." );
            } else {
                System.out.println( getUnitName(this) + " started." );
            }
        }

        @Override
        public void onEnd() {
            if (log != null) {
                log.add( getUnitName(this) + " ended." );
            } else {
                System.out.println( getUnitName(this) + " ended." );
            }
        }

        @Override
        public void onYield( RuleUnit other ) {
            if (log != null) {
                log.add( getUnitName(this) + " yielded to " + getUnitName(other) );
            } else {
                System.out.println( getUnitName(this) + " yielded to " + getUnitName(other) );
            }
        }
    }

    public static class NotAdultUnit implements RuleUnit {
        private DataSource<Person> persons;
        private List<String> log;

        public NotAdultUnit( ) { }

        public NotAdultUnit( DataSource<Person> persons ) {
            this.persons = persons;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }

        @Override
        public void onStart() {
            if (log != null) {
                log.add( getUnitName(this) + " started." );
            } else {
                System.out.println( getUnitName(this) + " started." );
            }
        }

        @Override
        public void onEnd() {
            if (log != null) {
                log.add( getUnitName(this) + " ended." );
            } else {
                System.out.println( getUnitName(this) + " ended." );
            }
        }

        @Override
        public void onYield( RuleUnit other ) {
            if (log != null) {
                log.add( getUnitName(this) + " yielded to " + getUnitName(other) );
            } else {
                System.out.println( getUnitName(this) + " yielded to " + getUnitName(other) );
            }
        }
    }

    @Test
    public void testNotExistingDataSource() throws Exception {
        String drl1 =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    Person(age >= 18, $name : name) from adults\n" +
                "then\n" +
                "    System.out.println($name + \" is adult\");\n" +
                "end";

        KieServices ks = KieServices.get();
        KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl1 );
        Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertFalse( results.getMessages().isEmpty() );
    }

    @Test
    public void testReactiveDataSource() throws Exception {
        String drl1 =
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

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Person> persons = executor.newDataSource( "persons", new Person( "Mario", 42 ) );

        ReactiveAdultUnit adultUnit = new ReactiveAdultUnit(persons, null);
        assertEquals(1, executor.run( adultUnit ) );

        ReactiveNotAdultUnit notAdultUnit = new ReactiveNotAdultUnit(persons);
        assertEquals(0, executor.run( notAdultUnit ) );

        persons.insert( new Person( "Sofia", 4 ) );
        assertEquals(0, executor.run( adultUnit ) );
        assertEquals(1, executor.run( notAdultUnit ) );

        persons.insert( new Person( "Marilena", 44 ) );
        assertEquals(1, executor.run( adultUnit ) );
        assertEquals(0, executor.run( notAdultUnit ) );
    }

    public static class ReactiveAdultUnit implements RuleUnit {
        private final DataSource<Person> persons;
        private final List<String> list;

        public ReactiveAdultUnit( DataSource<Person> persons, List<String> list ) {
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

        public ReactiveNotAdultUnit( DataSource<Person> persons ) {
            this.persons = persons;
        }

        public DataSource<Person> getPersons() {
            return persons;
        }
    }

    @Test(timeout = 10000L)
    public void testReactiveDataSourceWithRunUntilHalt() throws Exception {
        String drl1 =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + ReactiveAdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( ReactiveAdultUnit.class ) when\n" +
                "    Person(age >= 18, $name : name) from persons\n" +
                "then\n" +
                "    System.out.println($name + \" is adult\");" +
                "    list.add($name);\n" +
                "end";

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DebugList<String> list = new DebugList<>();
        executor.bindVariable( "list", list );

        DataSource<Person> persons = executor.newDataSource( "persons", new Person( "Mario", 42 ) );
        ReactiveAdultUnit adultUnit = new ReactiveAdultUnit(persons, list);

        Semaphore ready = new Semaphore( 0, true);
        list.onItemAdded = ( l -> ready.release() );

        new Thread( () -> executor.runUntilHalt( adultUnit ) ).start();

        ready.acquire();

        assertEquals( 1, list.size() );
        assertEquals( "Mario", list.get(0) );
        list.clear();

        list.onItemAdded = ( l -> ready.release() );

        persons.insert( new Person( "Sofia", 4 ) );
        persons.insert( new Person( "Marilena", 44 ) );

        ready.acquire();

        assertEquals( 1, list.size() );
        assertEquals( "Marilena", list.get(0) );

        executor.halt();
    }

    @Test
    public void testNamingConventionOnDrlFile() throws Exception {
        String drl1 =
                "package org.kie.test;\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule Adult when\n" +
                "    $p : /persons{age >= 18}\n" +
                "then\n" +
                "    System.out.println($p.getName() + \" is adult\");\n" +
                "end";

        String javaRuleUnit =
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

        String path = "org/kie/test/MyRuleUnit";

        KieServices ks = KieServices.get();
        KieFileSystem kfs = ks.newKieFileSystem();
        kfs.writeKModuleXML(ks.newKieModuleModel().toXML())
           .write("src/main/resources/" + path + ".drl", drl1)
           .write("src/main/java/" + path + ".java", javaRuleUnit);

        ks.newKieBuilder( kfs ).buildAll();
        KieContainer kcontainer = ks.newKieContainer( ks.getRepository().getDefaultReleaseId() );
        KieBase kbase = kcontainer.getKieBase();

        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Person> persons = executor.newDataSource( "persons",
                                                             new Person( "Mario", 42 ) );

        RuleUnit ruleUnit = new RuleUnitFactory().bindVariable( "persons", persons )
                                                 .getOrCreateRuleUnit( ( (InternalRuleUnitExecutor) executor ), "org.kie.test.MyRuleUnit", kcontainer.getClassLoader() );

        assertEquals(1, executor.run( ruleUnit ) );

        persons.insert( new Person( "Sofia", 4 ) );
        assertEquals(0, executor.run( ruleUnit ) );

        persons.insert( new Person( "Marilena", 44 ) );
        assertEquals(1, executor.run( ruleUnit ) );
    }

    @Test
    public void testWithOOPath() throws Exception {
        String drl1 =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    $p : /persons{age >= 18}\n" +
                "then\n" +
                "    System.out.println($p.getName() + \" is adult\");\n" +
                "end";

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Person> persons = executor.newDataSource( "persons",
                                                             new Person( "Mario", 42 ),
                                                             new Person( "Marilena", 44 ),
                                                             new Person( "Sofia", 4 ) );

        RuleUnit adultUnit = new AdultUnit(persons);
        assertEquals(2, executor.run( adultUnit ) );
    }

    @Test
    public void testVarResolution() throws Exception {
        String drl1 =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + AdultUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( AdultUnit.class ) when\n" +
                "    $p : /persons{age >= adultAge}\n" +
                "then\n" +
                "    System.out.println($p.getName() + \" is adult and greater than \" + adultAge);\n" +
                "end";

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Person> persons = executor.newDataSource( "persons",
                                                             new Person( "Mario", 42 ),
                                                             new Person( "Marilena", 44 ),
                                                             new Person( "Sofia", 4 ) );
        executor.bindVariable( "adultAge", 18 );

        assertEquals(2, executor.run( AdultUnit.class ) );
    }

    @Test
    public void testUnitDeclaration() throws Exception {
        String drl1 =
                "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName(AdultUnit.class) + "\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule Adult when\n" +
                "    Person(age >= adultAge, $name : name) from persons\n" +
                "then\n" +
                "    System.out.println($name + \" is adult\");\n" +
                "end";

        String drl2 =
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

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL )
                                       .addContent( drl2, ResourceType.DRL )
                                       .build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Person> persons = executor.newDataSource( "persons",
                                                             new Person( "Mario", 42 ),
                                                             new Person( "Marilena", 44 ),
                                                             new Person( "Sofia", 4 ) );
        List<String> log = new ArrayList<>();
        executor.bindVariable( "log", log )
                .bindVariable( "adultAge", 18 );

        assertEquals(4, executor.run( NotAdultUnit.class ) );

        List<String> expectedLogs = asList("org.drools.compiler.integrationtests.RuleUnitTest.NotAdultUnit started.",
                                           "org.drools.compiler.integrationtests.RuleUnitTest.NotAdultUnit yielded to org.drools.compiler.integrationtests.RuleUnitTest.AdultUnit",
                                           "org.drools.compiler.integrationtests.RuleUnitTest.AdultUnit started.",
                                           "org.drools.compiler.integrationtests.RuleUnitTest.AdultUnit ended.");
        assertEquals( expectedLogs, log );
    }

    @Test
    public void testBindingWithNamedVars() throws Exception {
        String drl1 =
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + NamedVarsUnit.class.getCanonicalName() + "\n" +
                "rule Adult @Unit( NamedVarsUnit.class ) when\n" +
                "    $p : /persons{age >= adultAge}\n" +
                "then\n" +
                "    System.out.println($p.getName() + \" is adult and greater than \" + adultAge);\n" +
                "end";

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL ).build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Person> persons = executor.newDataSource( "data",
                                                             new Person( "Mario", 42 ),
                                                             new Person( "Marilena", 44 ),
                                                             new Person( "Sofia", 4 ) );
        executor.bindVariable( "minAge", 18 );

        assertEquals(2, executor.run( NamedVarsUnit.class ) );
    }

    public static class NamedVarsUnit implements RuleUnit {
        @UnitVar("minAge") private int adultAge = 0;
        @UnitVar("data") private DataSource<Person> persons;

        public NamedVarsUnit( ) { }

        public DataSource<Person> getPersons() {
            return persons;
        }

        public int getAdultAge() {
            return adultAge;
        }
    }

    @Test
    public void testGuardedUnit() throws Exception {
        String drl1 =
                "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName( BoxOfficeUnit.class ) + ";\n" +
                "import " + BoxOffice.class.getCanonicalName() + "\n" +
                "import " + TicketIssuerUnit.class.getCanonicalName() + "\n" +
                "\n" +
                "rule BoxOfficeIsOpen when\n" +
                "    $box: /boxOffices{ open }\n" +
                "then\n" +
                "    drools.guard( TicketIssuerUnit.class );" +
                "end";

        String drl2 =
                "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName( TicketIssuerUnit.class ) + ";\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "import " + AdultTicket.class.getCanonicalName() + "\n" +
                "rule IssueAdultTicket when\n" +
                "    $p: /persons{ age >= 18 }\n" +
                "then\n" +
                "    tickets.insert(new AdultTicket($p));\n" +
                "end\n" +
                "rule RegisterAdultTicket when\n" +
                "    $t: /tickets\n" +
                "then\n" +
                "    results.add( $t.getPerson().getName() );\n" +
                "end";

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL )
                                       .addContent( drl2, ResourceType.DRL )
                                       .build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Person> persons = executor.newDataSource( "persons" );
        DataSource<BoxOffice> boxOffices = executor.newDataSource( "boxOffices" );
        DataSource<AdultTicket> tickets = executor.newDataSource( "tickets" );

        List<String> list = new ArrayList<>();
        executor.bindVariable( "results", list );

        // two open box offices
        BoxOffice office1 = new BoxOffice(true);
        FactHandle officeFH1 = boxOffices.insert( office1 );
        BoxOffice office2 = new BoxOffice(true);
        FactHandle officeFH2 = boxOffices.insert( office2 );

        persons.insert(new Person("Mario", 40));
        executor.run(BoxOfficeUnit.class); // fire BoxOfficeIsOpen -> run TicketIssuerUnit -> fire RegisterAdultTicket

        assertEquals( 1, list.size() );
        assertEquals( "Mario", list.get(0) );
        list.clear();

        persons.insert(new Person("Matteo", 30));
        executor.run(BoxOfficeUnit.class); // fire RegisterAdultTicket

        assertEquals( 1, list.size() );
        assertEquals( "Matteo", list.get(0) );
        list.clear();

        // close one box office, the other is still open
        office1.setOpen(false);
        boxOffices.update(officeFH1, office1);
        persons.insert(new Person("Mark", 35));
        executor.run(BoxOfficeUnit.class);

        assertEquals( 1, list.size() );
        assertEquals( "Mark", list.get(0) );
        list.clear();

        // all box offices, are now closed
        office2.setOpen(false);
        boxOffices.update(officeFH2, office2); // guarding rule no longer true
        persons.insert(new Person("Edson", 35));
        executor.run(BoxOfficeUnit.class); // no fire

        assertEquals( 0, list.size() );
    }

    public static class BoxOffice {
        private boolean open;

        public BoxOffice( boolean open ) {
            this.open = open;
        }

        public boolean isOpen() {
            return open;
        }

        public void setOpen( boolean open ) {
            this.open = open;
        }
    }

    public static class AdultTicket {
        private final Person person;

        public AdultTicket( Person person ) {
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

        public TicketIssuerUnit() { }

        public TicketIssuerUnit( DataSource<Person> persons, DataSource<AdultTicket> tickets ) {
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
    public void testMultiLevelGuards() throws Exception {
        String drl1 =
                "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName( Unit0.class ) + "\n" +
                "import " + UnitA.class.getCanonicalName() + "\n" +
                "rule X when\n" +
                "    $b: /ds{ #Boolean }\n" +
                "then\n" +
                "    Boolean b = $b;\n" +
                "    drools.guard( UnitA.class );\n" +
                "end";

        String drl2 =
                "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName( UnitA.class ) + "\n" +
                "import " + UnitB.class.getCanonicalName() + "\n" +
                "rule A when\n" +
                "    $s: /ds{ #String }\n" +
                "then\n" +
                "    drools.guard( UnitB.class );" +
                "end";

        String drl3 =
                "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName( UnitB.class ) + "\n" +
                "import " + UnitB.class.getCanonicalName() + "\n" +
                "rule B when\n" +
                "    $i: /ds{ #Integer }\n" +
                "then\n" +
                "    list.add($i);" +
                "end";

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL )
                                       .addContent( drl2, ResourceType.DRL )
                                       .addContent( drl3, ResourceType.DRL )
                                       .build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Object> ds = executor.newDataSource( "ds" );

        List<Integer> list = new ArrayList<>();
        executor.bindVariable( "list", list );

        ds.insert( 1 );
        executor.run(Unit0.class);
        assertEquals( 0, list.size() ); // all units are inactive

        FactHandle guardA = ds.insert( true );
        executor.run(Unit0.class);
        assertEquals( 0, list.size() ); // UnitB still inactive

        FactHandle guardB = ds.insert( "test" );
        executor.run(Unit0.class);
        assertEquals( 1, list.size() ); // all units are active
        assertEquals( 1, (int)list.get(0) ); // all units are active
        list.clear();

        ds.insert( 2 );
        executor.run(Unit0.class);
        assertEquals( 1, list.size() ); // all units are inactive
        assertEquals( 2, (int)list.get(0) ); // all units are active
        list.clear();

        ds.delete( guardA ); // retracting guard A deactivate unitA and in cascade unit B
        ds.insert( 3 );
        executor.run(Unit0.class);
        assertEquals( 0, list.size() ); // all units are inactive

        guardA = ds.insert( true ); // activating guard A reactivate unitA and in cascade unit B
        executor.run(Unit0.class);
        assertEquals( 1, list.size() ); // all units are active
        list.clear();
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
    public void testRuleUnitIdentity() throws Exception {
        String drl1 =
                "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName( Unit0.class ) + "\n" +
                "import " + AgeCheckUnit.class.getCanonicalName() + "\n" +
                "\n" +
                "rule R1 when\n" +
                "    $i: /ds{ #Integer }\n" +
                "then\n" +
                "    drools.guard( new AgeCheckUnit($i) );" +
                "end\n" +
                "rule RegisterAdultTicket when\n" +
                "    $s: /ds{ #String }\n" +
                "then\n" +
                "    drools.guard( new AgeCheckUnit($s.length()) );" +
                "end";

        String drl2 =
                "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName( AgeCheckUnit.class ) + ";\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule CheckAge when\n" +
                "    $p : /persons{ age > minAge }\n" +
                "then\n" +
                "    list.add($p.getName() + \">\" + minAge);\n" +
                "end";

        KieBase kbase = new KieHelper().addContent( drl1, ResourceType.DRL )
                                       .addContent( drl2, ResourceType.DRL )
                                       .build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        DataSource<Object> ds = executor.newDataSource( "ds" );
        DataSource<Person> persons = executor.newDataSource( "persons",
                                                             new Person( "Mario", 42 ),
                                                             new Person( "Sofia", 4 ) );

        List<String> list = new ArrayList<>();
        executor.bindVariable( "list", list );

        ds.insert("test");
        ds.insert(3);
        ds.insert(4);
        executor.run(Unit0.class);

        System.out.println(list);
        assertEquals(3, list.size());
        assertTrue( list.containsAll( asList("Mario>4", "Mario>3", "Sofia>3") ) );

        list.clear();
        ds.insert("xxx");
        ds.insert("yyyy");
        executor.run(Unit0.class);
        assertEquals(0, list.size());
    }

    public static class AgeCheckUnit implements RuleUnit {
        private final int minAge;
        private DataSource<Person> persons;
        private List<String> list;

        public AgeCheckUnit( int minAge ) {
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
    public void testPropertyReactiveModify() throws Exception {
        String drl1 =
                "package org.drools.compiler.integrationtests\n" +
                "unit " + getCanonicalSimpleName( AdultUnit.class ) + "\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "rule Adult when\n" +
                "    $p: /persons{ age < 18 }\n" +
                "then\n" +
                "    System.out.println($p.getName() + \" is NOT adult\");\n" +
                "    modify($p) { setHappy(true); }\n" +
                "end";

        KieBase kbase = new KieHelper( PropertySpecificOption.ALWAYS ).addContent( drl1, ResourceType.DRL ).build();
        RuleUnitExecutor executor = RuleUnitExecutor.create().bind( kbase );

        Person mario = new Person( "Mario", 42 );
        Person sofia = new Person( "Sofia", 4 );

        DataSource<Person> persons = executor.newDataSource( "persons" );
        FactHandle marioFh = persons.insert( mario );
        FactHandle sofiaFh = persons.insert( sofia );

        executor.run( AdultUnit.class );

        assertTrue( sofia.isHappy() );
        assertFalse( mario.isHappy() );

        sofia.setAge( 5 );
        persons.update( sofiaFh, sofia, "age" );
        assertEquals( 1, executor.run( AdultUnit.class ) );

        sofia.setSex( 'F' );
        persons.update( sofiaFh, sofia, "sex" );
        assertEquals( 0, executor.run( AdultUnit.class ) );
    }
}
