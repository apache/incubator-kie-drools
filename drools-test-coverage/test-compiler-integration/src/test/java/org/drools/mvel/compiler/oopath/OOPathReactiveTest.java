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
package org.drools.mvel.compiler.oopath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.base.base.ClassObjectType;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.Tuple;
import org.drools.core.reteoo.TupleImpl;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.util.FastIterator;
import org.drools.core.util.Iterator;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.mvel.compiler.oopath.model.Adult;
import org.drools.mvel.compiler.oopath.model.Child;
import org.drools.mvel.compiler.oopath.model.Disease;
import org.drools.mvel.compiler.oopath.model.Group;
import org.drools.mvel.compiler.oopath.model.Man;
import org.drools.mvel.compiler.oopath.model.School;
import org.drools.mvel.compiler.oopath.model.Toy;
import org.drools.mvel.compiler.oopath.model.Woman;
import org.drools.mvel.integrationtests.SerializationHelper;
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
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.drools.mvel.compiler.oopath.model.BodyMeasurement.CHEST;
import static org.drools.mvel.compiler.oopath.model.BodyMeasurement.RIGHT_FOREARM;

@RunWith(Parameterized.class)
public class OOPathReactiveTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public OOPathReactiveTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testReactiveOnLia() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children[age > 10]/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        debbie.setAge( 11 );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("doll");
    }

    @Test
    public void testReactiveDeleteOnLia() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children[age > 10]/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final EntryPointNode epn = ( (InternalKnowledgeBase) ksession.getKieBase() ).getRete().getEntryPointNodes().values().iterator().next();
        final ObjectTypeNode otn = epn.getObjectTypeNodes().get( new ClassObjectType(Man.class) );
        final LeftInputAdapterNode lian = (LeftInputAdapterNode)otn.getObjectSinkPropagator().getSinks()[0];
        final ReactiveFromNode from1 = (ReactiveFromNode)lian.getSinkPropagator().getSinks()[0];
        final ReactiveFromNode from2 = (ReactiveFromNode)from1.getSinkPropagator().getSinks()[0];
        final ReactiveFromNode from3 = (ReactiveFromNode)from2.getSinkPropagator().getSinks()[0];

        final BetaMemory betaMemory = ( (InternalWorkingMemory) ksession ).getNodeMemory(from3).getBetaMemory();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 11 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("car", "ball", "doll");

        final TupleMemory tupleMemory = betaMemory.getLeftTupleMemory();
        assertThat(betaMemory.getLeftTupleMemory().size()).isEqualTo(2);
        FastIterator<TupleImpl> it = tupleMemory.fastIterator();
        for ( TupleImpl next = tupleMemory.getFirst(null); next != null; next = it.next(next) ) {
            final Object obj = next.getFactHandle().getObject();
            assertThat(obj == charlie || obj == debbie).isTrue();
        }

        list.clear();
        debbie.setAge( 10 );
        ksession.fireAllRules();

        assertThat(list).hasSize(0);;
        assertThat(betaMemory.getLeftTupleMemory().size()).isEqualTo(1);
        it = tupleMemory.fastIterator();
        for ( TupleImpl next = tupleMemory.getFirst(null); next != null; next = it.next(next) ) {
            final Object obj = next.getFactHandle().getObject();
            assertThat(obj == charlie).isTrue();
        }
    }

    @Test
    public void testRemoveFromReactiveListBasic() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "\n" +
                        "rule R2 when\n" +
                        "  School( $child: /children[age >= 13 && age < 20] )\n" +
                        "then\n" +
                        "  System.out.println( $child );\n" +
                        "  insertLogical( $child );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final Child charlie = new Child( "Charles", 15 );
        final Child debbie = new Child( "Debbie", 19 );
        final School school = new School( "Da Vinci" );
        school.addChild( charlie );

        ksession.insert( school );
        ksession.fireAllRules();
        assertThat(ksession.getObjects().contains(charlie)).isTrue();
        assertThat(ksession.getObjects().contains(debbie)).isFalse();

        school.addChild( debbie );
        ksession.fireAllRules();
        assertThat(ksession.getObjects().contains(charlie)).isTrue();
        assertThat(ksession.getObjects().contains(debbie)).isTrue();

        school.getChildren().remove( debbie );
        ksession.fireAllRules();
        assertThat(ksession.getObjects().contains(charlie)).isTrue();
        assertThat(ksession.getObjects().contains(debbie)).isFalse();

        school.addChild( debbie );
        ksession.fireAllRules();
        assertThat(ksession.getObjects().contains(charlie)).isTrue();
        assertThat(ksession.getObjects().contains(debbie)).isTrue();

        debbie.setAge( 20 );
        ksession.fireAllRules();
        assertThat(ksession.getObjects().contains(charlie)).isTrue();
        assertThat(ksession.getObjects().contains(debbie)).isFalse();
    }

    @Test
    public void testRemoveFromReactiveListExtended() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "\n" +
                        "rule R2 when\n" +
                        "  Group( $id: name, $p: /members[age >= 20] )\n" +
                        "then\n" +
                        "  System.out.println( $id + \".\" + $p.getName() );\n" +
                        "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final Adult ada = new Adult("Ada", 19);
        final Adult bea = new Adult("Bea", 19);
        final Group x = new Group("X");
        final Group y = new Group("Y");
        x.addPerson(ada);
        x.addPerson(bea);
        y.addPerson(ada);
        y.addPerson(bea);
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("X.Bea")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();

        ada.setAge( 20 );
        ksession.fireAllRules();
        ksession.getObjects().forEach(System.out::println);
        assertThat(factsCollection(ksession).contains("X.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("X.Bea")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();

        y.removePerson(bea);
        bea.setAge( 20 );
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("X.Bea")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();
    }

    @Test
    public void testRemoveFromAndAddToReactiveSet() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "\n" +
                        "rule R when\n" +
                        "  School( $disease: /children/diseases )\n" +
                        "then\n" +
                        "  insertLogical( $disease );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final Disease flu = new Disease("flu");
        final Disease asthma = new Disease("asthma");
        final Disease diabetes = new Disease("diabetes");

        final Child charlie = new Child("Charles", 15);
        charlie.addDisease(flu);
        charlie.addDisease(asthma);

        final Child debbie = new Child("Debbie", 19);
        debbie.addDisease(diabetes);

        final School school = new School("Da Vinci");
        school.addChild(charlie);
        school.addChild(debbie);

        ksession.insert(school);
        ksession.fireAllRules();
        assertThat(ksession.getObjects().contains(flu)).isTrue();
        assertThat(ksession.getObjects().contains(asthma)).isTrue();
        assertThat(ksession.getObjects().contains(diabetes)).isTrue();

        charlie.getDiseases().remove(flu);
        ksession.fireAllRules();
        assertThat(ksession.getObjects().contains(flu)).isFalse();
        assertThat(ksession.getObjects().contains(asthma)).isTrue();
        assertThat(ksession.getObjects().contains(diabetes)).isTrue();

        charlie.getDiseases().remove(asthma);
        ksession.fireAllRules();
        assertThat(ksession.getObjects().contains(flu)).isFalse();
        assertThat(ksession.getObjects().contains(asthma)).isFalse();
        assertThat(ksession.getObjects().contains(diabetes)).isTrue();

        debbie.getDiseases().remove(diabetes);
        ksession.fireAllRules();
        assertThat(ksession.getObjects().contains(flu)).isFalse();
        assertThat(ksession.getObjects().contains(asthma)).isFalse();
        assertThat(ksession.getObjects().contains(diabetes)).isFalse();

        charlie.addDisease(flu);
        ksession.fireAllRules();
        assertThat(ksession.getObjects().contains(flu)).isTrue();
        assertThat(ksession.getObjects().contains(asthma)).isFalse();
        assertThat(ksession.getObjects().contains(diabetes)).isFalse();

        charlie.addDisease(asthma);
        debbie.addDisease(diabetes);
        ksession.fireAllRules();
        assertThat(ksession.getObjects().contains(flu)).isTrue();
        assertThat(ksession.getObjects().contains(asthma)).isTrue();
        assertThat(ksession.getObjects().contains(diabetes)).isTrue();
    }

    /**
     * Same test as above but with serialization.
     */
    @Test
    public void testRemoveFromReactiveListExtendedWithSerialization() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "\n" +
                        "rule R2 when\n" +
                        "  Group( $id: name, $p: /members[age >= 20] )\n" +
                        "then\n" +
                        "  System.out.println( $id + \".\" + $p.getName() );\n" +
                        "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        try {
            ksession = SerializationHelper.getSerialisedStatefulKnowledgeSession( ksession, true, false );
        } catch ( Exception e ) {
            e.printStackTrace();
            fail( e.getMessage() );
        }

        final Adult ada = new Adult("Ada", 19);
        final Adult bea = new Adult("Bea", 19);
        final Group x = new Group("X");
        final Group y = new Group("Y");
        x.addPerson(ada);
        x.addPerson(bea);
        y.addPerson(ada);
        y.addPerson(bea);
        ksession.insert( x );
        ksession.insert( y );
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("X.Bea")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();

        ada.setAge( 20 );
        ksession.fireAllRules();
        ksession.getObjects().forEach(System.out::println);
        assertThat(factsCollection(ksession).contains("X.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("X.Bea")).isFalse();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();

        y.removePerson(bea);
        bea.setAge( 20 );
        ksession.fireAllRules();
        assertThat(factsCollection(ksession).contains("X.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("X.Bea")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Ada")).isTrue();
        assertThat(factsCollection(ksession).contains("Y.Bea")).isFalse();
    }

    @Test
    public void testReactiveOnBeta() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  $i : Integer()\n" +
                        "  Man( $toy: /wife/children[age > $i]?/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( 10 );
        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        debbie.setAge( 11 );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("doll");
    }

    @Test
    public void testReactive2Rules() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List toyList\n" +
                        "global java.util.List teenagers\n" +
                        "\n" +
                        "rule R1 when\n" +
                        "  $i : Integer()\n" +
                        "  Man( $toy: /wife/children[age >= $i]/toys )\n" +
                        "then\n" +
                        "  toyList.add( $toy.getName() );\n" +
                        "end\n" +
                        "rule R2 when\n" +
                        "  School( $child: /children[age >= 13] )\n" +
                        "then\n" +
                        "  teenagers.add( $child.getName() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<String> toyList = new ArrayList<>();
        ksession.setGlobal( "toyList", toyList );
        final List<String> teenagers = new ArrayList<>();
        ksession.setGlobal( "teenagers", teenagers );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 15 );
        final Child debbie = new Child( "Debbie", 12 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        final School school = new School( "Da Vinci" );
        school.addChild( charlie );
        school.addChild( debbie );

        ksession.insert( 13 );
        ksession.insert( bob );
        ksession.insert( school );
        ksession.fireAllRules();

        assertThat(toyList).containsExactlyInAnyOrder("car", "ball");
        assertThat(teenagers).containsExactlyInAnyOrder("Charles");

        toyList.clear();
        debbie.setAge( 13 );
        ksession.fireAllRules();

        assertThat(toyList).containsExactlyInAnyOrder("doll");
        assertThat(teenagers).containsExactlyInAnyOrder("Charles", "Debbie");
    }

    @Test
    public void testReactiveList() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children[age > 10]/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        charlie.addToy( new Toy( "gun" ) );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("gun");
    }

    @Test
    public void testReactiveSet() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $disease: /wife/children[age > 10]/diseases )\n" +
                        "then\n" +
                        "  list.add( $disease.getName() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Woman alice = new Woman("Alice", 38);
        final Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        final Child charlie = new Child("Charles", 12);
        final Child debbie = new Child("Debbie", 10);
        alice.addChild(charlie);
        alice.addChild(debbie);

        charlie.addDisease(new Disease("flu"));
        charlie.addDisease(new Disease("asthma"));
        debbie.addDisease(new Disease("diabetes"));

        ksession.insert(bob);
        ksession.fireAllRules();
        assertThat(list).containsExactlyInAnyOrder("flu", "asthma");

        list.clear();
        charlie.addDisease(new Disease("epilepsy"));
        ksession.fireAllRules();
        assertThat(list).containsExactlyInAnyOrder("epilepsy");
    }

    @Test
    public void testReactiveMap() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $bodyMeasurement: /wife/bodyMeasurementsMap/entrySet )\n" +
                        "then\n" +
                        "  list.add( $bodyMeasurement.getValue() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<Integer> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Man bob = new Man("Bob", 40);
        final Woman alice = new Woman("Alice", 38);
        alice.putBodyMeasurement(CHEST, 80);
        bob.setWife(alice);

        ksession.insert(bob);
        ksession.fireAllRules();
        assertThat(list).containsExactlyInAnyOrder(80);

        list.clear();
        alice.putBodyMeasurement(RIGHT_FOREARM, 38);
        ksession.fireAllRules();
        assertThat(list).containsExactlyInAnyOrder(38, 80);
    }

    @Test
    public void testNonReactivePart() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children[age > 10]?/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        charlie.addToy( new Toy( "robot" ) );
        ksession.fireAllRules();

        assertThat(list).isEmpty();
    }

    @Test
    public void testAllNonReactiveAfterNonReactivePart() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: ?/wife/children[age > 10]/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        charlie.addToy( new Toy( "robot" ) );
        ksession.fireAllRules();

        assertThat(list).isEmpty();
    }

    @Test
    public void testInvalidDoubleNonReactivePart() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife?/children[age > 10]?/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        assertThat(kieBuilder.getResults().hasMessages(Message.Level.ERROR)).isTrue();
    }

    @Test
    public void testSingleFireOnReactiveChange() {
        // DROOLS-1302
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children[age > 10]/toys )\n" +
                        "then\n" +
                        "  list.add( $toy );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        ksession.insert( bob );
        ksession.fireAllRules();

        list.clear();
        final Child eleonor = new Child( "Eleonor", 10 );
        alice.addChild( eleonor );
        final Toy toy = new Toy( "eleonor toy 1" );
        eleonor.addToy( toy );
        eleonor.setAge(11);

        ksession.fireAllRules();

        assertThat(list).hasSize(1);

        list.clear();

        toy.setName( "eleonor toy 2" );
        ksession.fireAllRules();
        assertThat(list).hasSize(1);
    }

    @Test
    public void testReactivitySettingAttributeInDrl() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $child: /wife/children[age >= 10] )\n" +
                        "then\n" +
                        "end\n" +
                        "rule R2 when\n" +
                        "  Man( $child: /wife/children[age < 10] )\n" +
                        "then\n" +
                        "$child.setAge(12);" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final Man bob = new Man("Bob", 40);

        final Woman alice = new Woman("Alice", 38);
        final Child charlie = new Child("Charles", 9);
        final Child debbie = new Child("Debbie", 8);
        bob.setWife(alice);
        alice.addChild(charlie);
        alice.addChild(debbie);

        ksession.insert(bob);

        assertThat(ksession.fireAllRules()).isEqualTo(4);
    }

    private List<?> factsCollection(KieSession ksession) {
        final List<Object> res = new ArrayList<>();
        res.addAll(ksession.getObjects());
        return res;
    }
}
