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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.mvel.compiler.Address;
import org.drools.mvel.compiler.Asset;
import org.drools.mvel.compiler.AssetCard;
import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.IndexedNumber;
import org.drools.mvel.compiler.OuterClass;
import org.drools.mvel.compiler.Person;
import org.drools.mvel.compiler.Target;
import org.drools.mvel.integrationtests.SerializationHelper;
import org.drools.mvel.integrationtests.facts.AFact;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.command.Setter;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.internal.command.CommandFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class UpdateTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public UpdateTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
     // TODO: EM failed with some tests. File JIRAs
        return TestParametersUtil.getKieBaseCloudConfigurations(false);
    }

    @Test
    public void testModifyBlock() throws Exception {
        doModifyTest("test_ModifyBlock.drl");
    }

    @Test
    public void testModifyBlockWithPolymorphism() throws Exception {
        doModifyTest("test_ModifyBlockWithPolymorphism.drl");
    }

    private void doModifyTest(final String drlResource) throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, drlResource);
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Person bob = new Person("Bob");
        bob.setStatus("hungry");

        final Cheese c = new Cheese();

        ksession.insert(bob);
        ksession.insert(c);

        ksession.fireAllRules();

        assertThat(c.getPrice()).isEqualTo(10);
        assertThat(bob.getStatus()).isEqualTo("fine");
    }

    @Test
    public void testModifyBlockWithFrom() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_ModifyBlockWithFrom.drl");
        KieSession ksession = kbase.newKieSession();

        final List results = new ArrayList();
        ksession.setGlobal("results", results);

        final Person bob = new Person("Bob");
        final Address addr = new Address("abc");
        bob.addAddress(addr);

        ksession.insert(bob);
        ksession.insert(addr);

        ksession.fireAllRules();

        // modify worked
        assertThat(addr.getZipCode()).isEqualTo("12345");
        // chaining worked
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo(addr);
    }

    // this test requires mvel 1.2.19. Leaving it commented until mvel is released.
    @Test
    public void testJavaModifyBlock() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_JavaModifyBlock.drl");
        KieSession ksession = kbase.newKieSession();

        final List list = new ArrayList();
        ksession.setGlobal("results", list);

        final Person bob = new Person("Bob", 30);
        bob.setStatus("hungry");
        ksession.insert(bob);
        ksession.insert(new Cheese());
        ksession.insert(new Cheese());
        ksession.insert(new OuterClass.InnerClass(1));

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat(bob.getStatus()).isEqualTo("full");
        assertThat(bob.getAge()).isEqualTo(31);
        assertThat(((OuterClass.InnerClass) list.get(1)).getIntAttr()).isEqualTo(2);
    }

    @Test
    public void testModifyJava() {
        testModifyWithDialect("java");
    }

    @Test
    public void testModifyMVEL() {
        testModifyWithDialect("mvel");
    }

    private void testModifyWithDialect(final String dialect) {
        final String str = "package org.drools.mvel.compiler\n" +
                "import java.util.List\n" +
                "rule \"test\"\n" +
                "    dialect \"" + dialect + "\"\n" +
                "when\n" +
                "    $l : List() from collect ( Person( alive == false ) );\n" +
                "then\n" +
                "    for(Object p : $l ) {\n" +
                "        Person p2 = (Person) p;\n" +
                "        modify(p2) { setAlive(true) }\n" +
                "    }\n" +
                "end";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as(errors.toString()).isTrue();
    }

    @Test
    public void testModifySimple() {
        final String str = "package org.drools.mvel.compiler;\n" +
                "\n" +
                "rule \"test modify block\"\n" +
                "when\n" +
                "    $p: Person( name == \"hungry\" )\n" +
                "then\n" +
                "    modify( $p ) { setName(\"fine\") }\n" +
                "end\n" +
                "\n" +
                "rule \"Log\"\n" +
                "when\n" +
                "    $o: Object()\n" +
                "then\n" +
                "    System.out.println( $o );\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        final Person p = new Person("hungry");
        ksession.insert(p);
        ksession.fireAllRules();
        ksession.dispose();
    }

    @Test
    public void testModifyWithLockOnActive() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_ModifyWithLockOnActive.drl");
        KieSession session = kbase.newKieSession();

        final List results = new ArrayList();
        session.setGlobal("results", results);

        final Person bob = new Person("Bob", 15);
        final Person mark = new Person("Mark", 16);
        final Person michael = new Person("Michael", 14);
        session.insert(bob);
        session.insert(mark);
        session.insert(michael);
        session.getAgenda().getAgendaGroup("feeding").setFocus();
        session.fireAllRules(5);

        assertThat(((List) session.getGlobal("results")).size()).isEqualTo(2);
    }

    @Test
    public void testMissingClosingBraceOnModify() throws Exception {
        // JBRULES-3436
        final String str = "package org.drools.mvel.compiler.test;\n" +
                "import org.drools.compiler.*\n" +
                "rule R1 when\n" +
                "   $p : Person( )" +
                "   $c : Cheese( )" +
                "then\n" +
                "   modify($p) { setCheese($c) ;\n" +
                "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @Test
    public void testInvalidModify1() throws Exception {
        String str = "";
        str += "package org.drools.compiler \n";
        str += "import " + Cheese.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    no-loop \n";
        str += "when \n";
        str += "    $i : Cheese() \n";
        str += "then \n";
        str += "    modify( $i ); ";
        str += "    list.add( $i ); \n";
        str += "end \n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @Test
    public void testInvalidModify2() throws Exception {
        String str = "";
        str += "package org.drools.compiler \n";
        str += "import " + Cheese.class.getName() + "\n";
        str += "global java.util.List list \n";
        str += "rule rule1 \n";
        str += "    no-loop \n";
        str += "when \n";
        str += "    $i : Cheese() \n";
        str += "then \n";
        str += "    modify( $i ) { setType( \"stilton\" ); setType( \"stilton\" );}; ";
        str += "    list.add( $i ); \n";
        str += "end \n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, str);
        List<Message> errors = kieBuilder.getResults().getMessages(Message.Level.ERROR);
        assertThat(errors.isEmpty()).as("Should have an error").isFalse();
    }

    @Test
    public void testJoinNodeModifyObject() throws IOException, ClassNotFoundException {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_JoinNodeModifyObject.drl");
        KieSession ksession = kbase.newKieSession();

        final List orderedFacts = new ArrayList();
        final List errors = new ArrayList();
        ksession.setGlobal("orderedNumbers", orderedFacts);
        ksession.setGlobal("errors", errors);
        final int MAX = 2;
        for (int i = 1; i <= MAX; i++) {
            final IndexedNumber n = new IndexedNumber(i, MAX - i + 1);
            ksession.insert(n);
        }
        ksession.fireAllRules();
        assertThat(errors.isEmpty()).as("Processing generated errors: " + errors.toString()).isTrue();
        for (int i = 1; i <= MAX; i++) {
            final IndexedNumber n = (IndexedNumber) orderedFacts.get(i - 1);
            assertThat(n.getIndex()).as("Fact is out of order").isEqualTo(i);
        }
    }

    @Test
    public void testModifyCommand() {
        final String str =
                "rule \"sample rule\"\n" +
                        "   when\n" +
                        "   then\n" +
                        "       System.out.println(\"\\\"Hello world!\\\"\");\n" +
                        "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        final Person p1 = new Person("John", "nobody", 25);
        ksession.execute(CommandFactory.newInsert(p1));
        final FactHandle fh = ksession.getFactHandle(p1);

        final Person p = new Person("Frank", "nobody", 30);
        final List<Setter> setterList = new ArrayList<Setter>();
        setterList.add(CommandFactory.newSetter("age", String.valueOf(p.getAge())));
        setterList.add(CommandFactory.newSetter("name", p.getName()));
        setterList.add(CommandFactory.newSetter("likes", p.getLikes()));

        ksession.execute(CommandFactory.newModify(fh, setterList));
    }

    @Test
    public void testNotIterativeModifyBug() {
        // JBRULES-2809
        // This bug occurs when a tuple is modified, the remove/add puts it onto the memory end
        // However before this was done it would attempt to find the next tuple, starting from itself
        // This meant it would just re-add itself as the blocker, but then be moved to end of the memory
        // If this tuple was then removed or changed, the blocked was unable to check previous tuples.

        String str = "";
        str += "package org.simple \n";
        str += "import " + AFact.class.getCanonicalName() + "\n";
        str += "global java.util.List list \n";
        str += "rule xxx \n";
        str += "when \n";
        str += "  $f1 : AFact() \n";
        str += "    not AFact(this != $f1,  eval(field2 == $f1.getField2())) \n";
        str += "    eval( !$f1.getField1().equals(\"1\") ) \n";
        str += "then \n";
        str += "  list.add($f1); \n";
        str += "end  \n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();
        final List list = new ArrayList();
        ksession.setGlobal("list", list);

        final AFact a1 = new AFact("2", "2");
        final AFact a2 = new AFact("1", "2");
        final AFact a3 = new AFact("1", "2");

        final FactHandle fa1 = ksession.insert(a1);
        final FactHandle fa2 = ksession.insert(a2);
        final FactHandle fa3 = ksession.insert(a3);
        ksession.fireAllRules();

        // a1 is blocked by a2
        assertThat(list.size()).isEqualTo(0);

        // modify a2, so that a1 is now blocked by a3
        a2.setField2("1"); // Do
        ksession.update(fa2, a2);
        a2.setField2("2"); // Undo
        ksession.update(fa2, a2);

        // modify a3 to cycle, so that it goes on the memory end, but in a previous bug still blocked a1
        ksession.update(fa3, a3);

        a3.setField2("1"); // Do
        ksession.update(fa3, a3);
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(0); // this should still now blocked by a2, but bug from previous update hanging onto blocked

        ksession.dispose();
    }

    @Test
    public void testLLR() throws Exception {
        KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources(getClass(), kieBaseTestConfiguration, "test_JoinNodeModifyTuple.drl");
        KieSession ksession = kbase.newKieSession();
        ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession,true );

        // 1st time
        Target tgt = new Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat(60.26544f);
        tgt.setLon(28.952137f);
        tgt.setCourse(145.0f);
        tgt.setSpeed(12.0f);
        tgt.setTime(1.8666667f);
        ksession.insert( tgt );

        tgt = new Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat(60.236874f);
        tgt.setLon(28.992579f);
        tgt.setCourse(325.0f);
        tgt.setSpeed(8.0f);
        tgt.setTime(1.8666667f);
        ksession.insert( tgt );

        ksession.fireAllRules();

        // 2nd time
        tgt = new Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat(60.265343f);
        tgt.setLon(28.952267f);
        tgt.setCourse(145.0f);
        tgt.setSpeed(12.0f);
        tgt.setTime(1.9f);
        ksession.insert( tgt );

        tgt = new Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat(60.236935f);
        tgt.setLon(28.992493f);
        tgt.setCourse(325.0f);
        tgt.setSpeed(8.0f);
        tgt.setTime(1.9f);
        ksession.insert( tgt );

        ksession.fireAllRules();

        // 3d time
        tgt = new Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat(60.26525f);
        tgt.setLon(28.952396f);
        tgt.setCourse(145.0f);
        tgt.setSpeed(12.0f);
        tgt.setTime(1.9333333f);
        ksession.insert( tgt );

        tgt = new Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat(60.236996f);
        tgt.setLon(28.992405f);
        tgt.setCourse(325.0f);
        tgt.setSpeed(8.0f);
        tgt.setTime(1.9333333f);
        ksession.insert( tgt );

        ksession.fireAllRules();

        // 4th time
        tgt = new Target();
        tgt.setLabel( "Santa-Anna" );
        tgt.setLat(60.265163f);
        tgt.setLon(28.952526f);
        tgt.setCourse(145.0f);
        tgt.setSpeed(12.0f);
        tgt.setTime(1.9666667f);
        ksession.insert( tgt );

        tgt = new Target();
        tgt.setLabel( "Santa-Maria" );
        tgt.setLat(60.237057f);
        tgt.setLon(28.99232f);
        tgt.setCourse(325.0f);
        tgt.setSpeed(8.0f);
        tgt.setTime(1.9666667f);
        ksession.insert( tgt );

        ksession.fireAllRules();
    }

    @Test
    public void noDormantCheckOnModifies() throws Exception {
        // Test case for BZ 862325
        final String str = "package org.drools.mvel.compiler;\n"
                + " rule R1\n"
                + "    salience 10\n"
                + "    when\n"
                + "        $c : Cheese( price == 10 ) \n"
                + "        $p : Person( ) \n"
                + "    then \n"
                + "        modify($c) { setPrice( 5 ) }\n"
                + "        modify($p) { setAge( 20 ) }\n"
                + "end\n"
                + "rule R2\n"
                + "    when\n"
                + "        $p : Person( )"
                + "    then \n"
                + "        // noop\n"
                + "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        final org.kie.api.event.rule.AgendaEventListener ael = mock(org.kie.api.event.rule.AgendaEventListener.class);
        ksession.addEventListener(ael);

        ksession.insert(new Person("Bob", 19));
        ksession.insert(new Cheese("brie", 10));
        ksession.fireAllRules();

        // both rules should fire exactly once
        verify(ael, times(2)).afterMatchFired(any(org.kie.api.event.rule.AfterMatchFiredEvent.class));
        // no cancellations should have happened
        verify(ael, never()).matchCancelled(any(org.kie.api.event.rule.MatchCancelledEvent.class));
    }

    @Test(timeout = 10000)
    public void testSwapChild() {
        // DROOLS-6684
        final String str = "package org.drools.mvel.compiler;\n" +
                           "import " + Person.class.getCanonicalName() + "\n" +
                           "import " + Asset.class.getCanonicalName() + "\n" +
                           "import " + AssetCard.class.getCanonicalName() + "\n" +
                           "\n" +
                           "rule R1\n" +
                           "    no-loop\n" +
                           "when\n" +
                           "    $p : Person(name == \"Mario\") @watch(age)\n" +
                           "    $as : Asset()\n" +
                           "    $ac : AssetCard(parent == $as, groupCode != \"A\") \n" +
                           "then\n" +
                           "    System.out.println(\"Rule \" + drools.getRule().getName() + \"; \" + $ac);\n" +
                           "    modify($p){setAge(10)}\n" +
                           "end\n" +
                           "\n" +
                           "rule R2\n" +
                           "    no-loop\n" +
                           "when\n" +
                           "    $p : Person(name == \"Mario\") @watch(age)\n" +
                           "    $as : Asset()\n" +
                           "    $ac : AssetCard(parent == $as, groupCode == \"A\") \n" +
                           "then\n" +
                           "    System.out.println(\"Rule \" + drools.getRule().getName() + \"; \" + $ac);\n" +
                           "    modify($p){setAge(10)}\n" +
                           "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        Asset asset = new Asset();

        AssetCard assetCard = new AssetCard(1);
        assetCard.setParent(asset);
        assetCard.setGroupCode("A");
        asset.setAssetCard(assetCard);

        Person p = new Person("Mario", 20);

        ksession.insert(asset);
        FactHandle assetCardFh = ksession.insert(assetCard);
        ksession.insert(p);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        //----------------------

        AssetCard assetCard2 = new AssetCard(2);
        assetCard2.setParent(asset);
        assetCard2.setGroupCode("A");
        asset.setAssetCard(assetCard2);

        ksession.delete(assetCardFh);
        ksession.insert(assetCard2);

        assertThat(ksession.fireAllRules()).isEqualTo(1);

        ksession.dispose();
    }

    public static class Firings {
        private final List<String> list = new ArrayList<>();

        public List<String> getList() {
            return list;
        }
    }

    @Test
    public void testPeerUpdate() {
        // DROOLS-6783
        final String str =
                "import " + Firings.class.getCanonicalName() + "\n" +
                "\n" +
                "rule R1 when\n" +
                "  Integer()\n" +
                "  $f : Firings( list not contains \"R1\" )\n" +
                "then\n" +
                "  $f.getList().add(\"R1\");\n" +
                "  update($f);\n" +
                "end\n" +
                "\n" +
                "rule R2 agenda-group \"x\" when\n" +
                "  Integer()\n" +
                "  Firings( $l : list )\n" +
                "  String()\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R3 agenda-group \"x\" when\n" +
                "  Integer()\n" +
                "  Firings( $l : list )\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R4 when\n" +
                "  Integer()\n" +
                "  Firings( $l : list )\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule R5 when\n" +
                "  Integer()\n" +
                "  $f : Firings( list not contains \"R5\" )\n" +
                "then\n" +
                "  $f.getList().add(\"R5\");\n" +
                "  update($f);\n" +
                "end";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, str);
        KieSession ksession = kbase.newKieSession();

        ksession.insert(1);
        ksession.insert(new Firings());

        ksession.getAgenda().getAgendaGroup("x").setFocus();
        assertThat(ksession.fireAllRules()).isEqualTo(5);
    }
}
