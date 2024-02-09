/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.integrationtests.session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.core.test.model.Cheese;
import org.drools.core.test.model.Person;
import org.drools.mvel.compiler.PersonInterface;
import org.drools.mvel.integrationtests.facts.ClassA;
import org.drools.mvel.integrationtests.facts.ClassB;
import org.drools.mvel.integrationtests.facts.InterfaceA;
import org.drools.mvel.integrationtests.facts.InterfaceB;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class DeleteTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public DeleteTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    private static Logger logger = LoggerFactory.getLogger(DeleteTest.class);

    private static final String DELETE_TEST_DRL = "org/drools/mvel/integrationtests/session/delete_test.drl";

    private KieSession ksession;

    @Before
    public void setUp() {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, DELETE_TEST_DRL);
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

    @Test
    public void testRetractLeftTuple() throws Exception {
        // JBRULES-3420
        final String str =
                "import " + ClassA.class.getCanonicalName() + ";\n" +
                "import " + ClassB.class.getCanonicalName() + ";\n" +
                "import " + InterfaceA.class.getCanonicalName() + ";\n" +
                "import " + InterfaceB.class.getCanonicalName() + ";\n" +
                "rule R1 salience 3\n" +
                "when\n" +
                "   $b : InterfaceB( )\n" +
                "   $a : ClassA( b == null )\n" +
                "then\n" +
                "   $a.setB( $b );\n" +
                "   update( $a );\n" +
                "end\n" +
                "rule R2 salience 2\n" +
                "when\n" +
                "   $b : ClassB( id == \"123\" )\n" +
                "   $a : ClassA( b != null && b.id == $b.id )\n" +
                "then\n" +
                "   $b.setId( \"456\" );\n" +
                "   update( $b );\n" +
                "end\n" +
                "rule R3 salience 1\n" +
                "when\n" +
                "   InterfaceA( $b : b )\n" +
                "then\n" +
                "   delete( $b );\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new ClassA());
        ksession.insert(new ClassB());
        assertThat(ksession.fireAllRules()).isEqualTo(3);
    }

    @Test
    public void testAssertRetract() throws Exception {
        // postponed while I sort out KnowledgeHelperFixer
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "assert_retract.drl");
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final PersonInterface person = new org.drools.mvel.compiler.Person("michael", "cheese");
        person.setStatus("start");
        ksession.insert(person);

        ksession.fireAllRules();

        final List<String> results = (List<String>) ksession.getGlobal("list");
        for (final String result : results) {
            logger.info(result);
        }
        assertThat(results.size()).isEqualTo(5);
        assertThat(results.contains("first")).isTrue();
        assertThat(results.contains("second")).isTrue();
        assertThat(results.contains("third")).isTrue();
        assertThat(results.contains("fourth")).isTrue();
        assertThat(results.contains("fifth")).isTrue();
    }

    @Test
    public void testEmptyAfterRetractInIndexedMemory() {
        String str = "";
        str += "package org.simple \n";
        str += "import org.drools.mvel.compiler.Person\n";
        str += "global java.util.List list \n";
        str += "rule xxx dialect 'mvel' \n";
        str += "when \n";
        str += "  Person( $name : name ) \n";
        str += "  $s : String( this == $name) \n";
        str += "then \n";
        str += "  list.add($s); \n";
        str += "end  \n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final org.drools.mvel.compiler.Person p = new org.drools.mvel.compiler.Person("ackbar");
        ksession.insert(p);
        ksession.insert("ackbar");
        ksession.fireAllRules();
        ksession.dispose();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("ackbar");
    }

    @Test
    public void testModifyRetractAndModifyInsert() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_ModifyRetractInsert.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final org.drools.mvel.compiler.Person bob = new org.drools.mvel.compiler.Person("Bob");
        bob.setStatus("hungry");
        ksession.insert(bob);
        ksession.insert(new org.drools.mvel.compiler.Cheese());
        ksession.insert(new org.drools.mvel.compiler.Cheese());

        ksession.fireAllRules(2);

        assertThat(list.size()).as("should have fired only once").isEqualTo(1);
    }

    @Test
    public void testModifyRetractWithFunction() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_RetractModifyWithFunction.drl");
        KieSession ksession = kbase.newKieSession();

        final org.drools.mvel.compiler.Cheese stilton = new org.drools.mvel.compiler.Cheese("stilton", 7);
        final org.drools.mvel.compiler.Cheese muzzarella = new org.drools.mvel.compiler.Cheese("muzzarella", 9);
        final int sum = stilton.getPrice() + muzzarella.getPrice();
        final FactHandle stiltonHandle = ksession.insert(stilton);
        final FactHandle muzzarellaHandle = ksession.insert(muzzarella);

        ksession.fireAllRules();

        assertThat(stilton.getPrice()).isEqualTo(sum);
        assertThat(ksession.getFactCount()).isEqualTo(1);
        assertThat(ksession.getObject(stiltonHandle)).isNotNull();
        assertThat(ksession.getFactHandle(stilton)).isNotNull();

        assertThat(ksession.getObject(muzzarellaHandle)).isNull();
        assertThat(ksession.getFactHandle(muzzarella)).isNull();
    }
}
