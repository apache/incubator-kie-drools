/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.integrationtests.session;

import java.util.ArrayList;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.drools.compiler.CommonTestMethodBase;
import org.drools.compiler.PersonInterface;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.integrationtests.facts.ClassA;
import org.drools.compiler.integrationtests.facts.ClassB;
import org.drools.compiler.integrationtests.facts.InterfaceA;
import org.drools.compiler.integrationtests.facts.InterfaceB;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeleteTest extends CommonTestMethodBase {

    private static Logger logger = LoggerFactory.getLogger(DeleteTest.class);

    private static final String DELETE_TEST_DRL = "org/drools/compiler/integrationtests/session/delete_test.drl";

    private KieSession ksession;

    @Before
    public void setUp() {
        KieFileSystem kfs = KieServices.Factory.get().newKieFileSystem();
        kfs.write(KieServices.Factory.get().getResources()
                .newClassPathResource(DELETE_TEST_DRL, DeleteTest.class));

        KieBuilder kbuilder = KieServices.Factory.get().newKieBuilder(kfs);
        kbuilder.buildAll();

        List<Message> res = kbuilder.getResults().getMessages(Level.ERROR);
        Assertions.assertThat(res).isEmpty();

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
        Assertions.assertThat(results).isNotEmpty();
        Assertions.assertThat(results.iterator().next().get("$countOfPerson")).isEqualTo(2L);

        ksession.delete(george);
        results = ksession.getQueryResults("informationAboutPersons");
        Assertions.assertThat(results).isNotEmpty();
        Assertions.assertThat(results.iterator().next().get("$countOfPerson")).isEqualTo(1L);
    }

    @Test
    public void deleteFactTwiceTest() {
        FactHandle george = ksession.insert(new Person("George", 19));
        QueryResults results = ksession.getQueryResults("countPerson");
        Assertions.assertThat(results).isNotEmpty();
        Assertions.assertThat(results.iterator().next().get("$personCount")).isEqualTo(1L);

        ksession.delete(george);
        results = ksession.getQueryResults("countPerson");
        Assertions.assertThat(results).isNotEmpty();
        Assertions.assertThat(results.iterator().next().get("$personCount")).isEqualTo(0L);

        ksession.delete(george);
        Assertions.assertThat(results).isNotEmpty();
        Assertions.assertThat(results.iterator().next().get("$personCount")).isEqualTo(0L);
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
        Assertions.assertThat(results).isNotEmpty();
        Assertions.assertThat(results.iterator().next().get("$personCount")).isEqualTo(1L);

        ksession.delete(person);
        results = ksession.getQueryResults("countPerson");
        Assertions.assertThat(results).isNotEmpty();
        Assertions.assertThat(results.iterator().next().get("$personCount")).isEqualTo(0L);
    }

    @Test
    public void deleteUpdatedFactDifferentClassTest() {
        FactHandle fact = ksession.insert(new Person("George", 18));

        Assertions.assertThat(ksession.getObjects()).hasSize(1);
        Assertions.assertThat(ksession.getObjects().iterator().next()).isInstanceOf(Person.class);

        ksession.update(fact, new Cheese("Cheddar", 50));

        Assertions.assertThat(ksession.getObjects()).hasSize(1);
        Assertions.assertThat(ksession.getObjects().iterator().next()).isInstanceOf(Cheese.class);

        ksession.delete(fact);

        Assertions.assertThat(ksession.getObjects()).isEmpty();
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

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = kbase.newKieSession();

        ksession.insert(new ClassA());
        ksession.insert(new ClassB());
        assertEquals(3, ksession.fireAllRules());
    }

    @Test
    public void testAssertRetract() throws Exception {
        // postponed while I sort out KnowledgeHelperFixer
        final KieBase kbase = loadKnowledgeBase("assert_retract.drl");
        final KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final PersonInterface person = new org.drools.compiler.Person("michael", "cheese");
        person.setStatus("start");
        ksession.insert(person);

        ksession.fireAllRules();

        final List<String> results = (List<String>) ksession.getGlobal("list");
        for (final String result : results) {
            logger.info(result);
        }
        assertEquals(5, results.size());
        assertTrue(results.contains("first"));
        assertTrue(results.contains("second"));
        assertTrue(results.contains("third"));
        assertTrue(results.contains("fourth"));
        assertTrue(results.contains("fifth"));
    }

    @Test
    public void testEmptyAfterRetractInIndexedMemory() {
        String str = "";
        str += "package org.simple \n";
        str += "import org.drools.compiler.Person\n";
        str += "global java.util.List list \n";
        str += "rule xxx dialect 'mvel' \n";
        str += "when \n";
        str += "  Person( $name : name ) \n";
        str += "  $s : String( this == $name) \n";
        str += "then \n";
        str += "  list.add($s); \n";
        str += "end  \n";

        final KieBase kbase = loadKnowledgeBaseFromString(str);
        final KieSession ksession = createKnowledgeSession(kbase);
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final org.drools.compiler.Person p = new org.drools.compiler.Person("ackbar");
        ksession.insert(p);
        ksession.insert("ackbar");
        ksession.fireAllRules();
        ksession.dispose();

        assertEquals(1, list.size());
        assertEquals("ackbar", list.get(0));
    }

    @Test
    public void testModifyRetractAndModifyInsert() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject( loadKnowledgeBase( "test_ModifyRetractInsert.drl" ) );
        final KieSession ksession = createKnowledgeSession( kbase );

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final org.drools.compiler.Person bob = new org.drools.compiler.Person("Bob");
        bob.setStatus("hungry");
        ksession.insert(bob);
        ksession.insert(new org.drools.compiler.Cheese());
        ksession.insert(new org.drools.compiler.Cheese());

        ksession.fireAllRules(2);

        assertEquals("should have fired only once", 1, list.size());
    }

    @Test
    public void testModifyRetractWithFunction() throws Exception {
        final KieBase kbase = SerializationHelper.serializeObject(loadKnowledgeBase("test_RetractModifyWithFunction.drl"));
        final KieSession ksession = createKnowledgeSession(kbase);

        final org.drools.compiler.Cheese stilton = new org.drools.compiler.Cheese("stilton", 7);
        final org.drools.compiler.Cheese muzzarella = new org.drools.compiler.Cheese("muzzarella", 9);
        final int sum = stilton.getPrice() + muzzarella.getPrice();
        final FactHandle stiltonHandle = ksession.insert(stilton);
        final FactHandle muzzarellaHandle = ksession.insert(muzzarella);

        ksession.fireAllRules();

        assertEquals(sum, stilton.getPrice());
        assertEquals(1, ksession.getFactCount());
        assertNotNull(ksession.getObject(stiltonHandle));
        assertNotNull(ksession.getFactHandle(stilton));

        assertNull(ksession.getObject(muzzarellaHandle));
        assertNull(ksession.getFactHandle(muzzarella));
    }
}
