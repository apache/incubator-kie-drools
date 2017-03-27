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

package org.drools.compiler.oopath;

import static org.drools.compiler.oopath.model.BodyMeasurement.*;
import static org.junit.Assert.*;

import java.util.*;

import org.assertj.core.api.Assertions;
import org.drools.compiler.integrationtests.SerializationHelper;
import org.drools.compiler.oopath.model.*;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.reteoo.BetaMemory;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.LeftInputAdapterNode;
import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.ReactiveFromNode;
import org.drools.core.reteoo.TupleMemory;
import org.drools.core.util.Iterator;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.builder.Results;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

/**
 * Created by tzimanyi on 24.2.17.
 */
public class OOPathReactiveTests {

    @Test
    public void testReactiveOnLia() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children{age > 10}/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

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

        Assertions.assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        debbie.setAge( 11 );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("doll");
    }

    @Test
    public void testReactiveDeleteOnLia() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children{age > 10}/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        final KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        final KieSession ksession = kbase.newKieSession();

        final EntryPointNode epn = ( (InternalKnowledgeBase) ksession.getKieBase() ).getRete().getEntryPointNodes().values().iterator().next();
        final ObjectTypeNode otn = epn.getObjectTypeNodes().values().iterator().next();
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

        Assertions.assertThat(list).containsExactlyInAnyOrder("car", "ball", "doll");

        final TupleMemory tupleMemory = betaMemory.getLeftTupleMemory();
        Assertions.assertThat(betaMemory.getLeftTupleMemory().size()).isEqualTo(2);
        Iterator<LeftTuple> it = tupleMemory.iterator();
        for ( LeftTuple next = it.next(); next != null; next = it.next() ) {
            final Object obj = next.getFactHandle().getObject();
            Assertions.assertThat(obj == charlie || obj == debbie).isTrue();
        }

        list.clear();
        debbie.setAge( 10 );
        ksession.fireAllRules();

        Assertions.assertThat(list).hasSize(0);;
        Assertions.assertThat(betaMemory.getLeftTupleMemory().size()).isEqualTo(1);
        it = tupleMemory.iterator();
        for ( LeftTuple next = it.next(); next != null; next = it.next() ) {
            final Object obj = next.getFactHandle().getObject();
            Assertions.assertThat(obj == charlie).isTrue();
        }
    }

    @Test
    public void testRemoveFromReactiveListBasic() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "\n" +
                        "rule R2 when\n" +
                        "  School( $child: /children{age >= 13 && age < 20} )\n" +
                        "then\n" +
                        "  System.out.println( $child );\n" +
                        "  insertLogical( $child );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

        final Child charlie = new Child( "Charles", 15 );
        final Child debbie = new Child( "Debbie", 19 );
        final School school = new School( "Da Vinci" );
        school.addChild( charlie );

        ksession.insert( school );
        ksession.fireAllRules();
        assertTrue(ksession.getObjects().contains(charlie));
        assertFalse(ksession.getObjects().contains(debbie));

        school.addChild( debbie );
        ksession.fireAllRules();
        assertTrue(ksession.getObjects().contains(charlie));
        assertTrue(ksession.getObjects().contains(debbie));

        school.getChildren().remove( debbie );
        ksession.fireAllRules();
        assertTrue(ksession.getObjects().contains(charlie));
        assertFalse(ksession.getObjects().contains(debbie));

        school.addChild( debbie );
        ksession.fireAllRules();
        assertTrue(ksession.getObjects().contains(charlie));
        assertTrue(ksession.getObjects().contains(debbie));

        debbie.setAge( 20 );
        ksession.fireAllRules();
        assertTrue(ksession.getObjects().contains(charlie));
        assertFalse(ksession.getObjects().contains(debbie));
    }

    @Test
    public void testRemoveFromReactiveListExtended() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "\n" +
                        "rule R2 when\n" +
                        "  Group( $id: name, $p: /members{age >= 20} )\n" +
                        "then\n" +
                        "  System.out.println( $id + \".\" + $p.getName() );\n" +
                        "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

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
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));

        ada.setAge( 20 );
        ksession.fireAllRules();
        ksession.getObjects().forEach(System.out::println);
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));

        y.removePerson(bea);
        bea.setAge( 20 );
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertTrue  (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
    }

    @Test
    public void testRemoveFromAndAddToReactiveSet() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "\n" +
                        "rule R when\n" +
                        "  School( $disease: /children/diseases )\n" +
                        "then\n" +
                        "  insertLogical( $disease );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                .build()
                .newKieSession();

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
        assertTrue(ksession.getObjects().contains(flu));
        assertTrue(ksession.getObjects().contains(asthma));
        assertTrue(ksession.getObjects().contains(diabetes));

        charlie.getDiseases().remove(flu);
        ksession.fireAllRules();
        assertFalse(ksession.getObjects().contains(flu));
        assertTrue(ksession.getObjects().contains(asthma));
        assertTrue(ksession.getObjects().contains(diabetes));

        charlie.getDiseases().remove(asthma);
        ksession.fireAllRules();
        assertFalse(ksession.getObjects().contains(flu));
        assertFalse(ksession.getObjects().contains(asthma));
        assertTrue(ksession.getObjects().contains(diabetes));

        debbie.getDiseases().remove(diabetes);
        ksession.fireAllRules();
        assertFalse(ksession.getObjects().contains(flu));
        assertFalse(ksession.getObjects().contains(asthma));
        assertFalse(ksession.getObjects().contains(diabetes));

        charlie.addDisease(flu);
        ksession.fireAllRules();
        assertTrue(ksession.getObjects().contains(flu));
        assertFalse(ksession.getObjects().contains(asthma));
        assertFalse(ksession.getObjects().contains(diabetes));

        charlie.addDisease(asthma);
        debbie.addDisease(diabetes);
        ksession.fireAllRules();
        assertTrue(ksession.getObjects().contains(flu));
        assertTrue(ksession.getObjects().contains(asthma));
        assertTrue(ksession.getObjects().contains(diabetes));
    }

    /**
     * Same test as above but with serialization.
     */
    @Test
    public void testRemoveFromReactiveListExtendedWithSerialization() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "\n" +
                        "rule R2 when\n" +
                        "  Group( $id: name, $p: /members{age >= 20} )\n" +
                        "then\n" +
                        "  System.out.println( $id + \".\" + $p.getName() );\n" +
                        "  insertLogical(      $id + \".\" + $p.getName() );\n" +
                        "end\n";

        KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

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
        assertFalse (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertFalse (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));

        ada.setAge( 20 );
        ksession.fireAllRules();
        ksession.getObjects().forEach(System.out::println);
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertFalse (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));

        y.removePerson(bea);
        bea.setAge( 20 );
        ksession.fireAllRules();
        assertTrue  (factsCollection(ksession).contains("X.Ada"));
        assertTrue  (factsCollection(ksession).contains("X.Bea"));
        assertTrue  (factsCollection(ksession).contains("Y.Ada"));
        assertFalse (factsCollection(ksession).contains("Y.Bea"));
    }

    @Test
    public void testReactiveOnBeta() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  $i : Integer()\n" +
                        "  Man( $toy: /wife/children{age > $i}?/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

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

        Assertions.assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        debbie.setAge( 11 );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("doll");
    }

    @Test
    public void testReactive2Rules() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List toyList\n" +
                        "global java.util.List teenagers\n" +
                        "\n" +
                        "rule R1 when\n" +
                        "  $i : Integer()\n" +
                        "  Man( $toy: /wife/children{age >= $i}/toys )\n" +
                        "then\n" +
                        "  toyList.add( $toy.getName() );\n" +
                        "end\n" +
                        "rule R2 when\n" +
                        "  School( $child: /children{age >= 13} )\n" +
                        "then\n" +
                        "  teenagers.add( $child.getName() );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

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

        Assertions.assertThat(toyList).containsExactlyInAnyOrder("car", "ball");
        Assertions.assertThat(teenagers).containsExactlyInAnyOrder("Charles");

        toyList.clear();
        debbie.setAge( 13 );
        ksession.fireAllRules();

        Assertions.assertThat(toyList).containsExactlyInAnyOrder("doll");
        Assertions.assertThat(teenagers).containsExactlyInAnyOrder("Charles", "Debbie");
    }

    @Test
    public void testReactiveList() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children{age > 10}/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

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

        Assertions.assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        charlie.addToy( new Toy( "gun" ) );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("gun");
    }

    @Test
    public void testReactiveArray() {
        // RHBRMS-2768
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Company( $employee: /employees )\n" +
                        "then\n" +
                        "  list.add( $employee.getName() );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Company robotics = new Company("Robotics");
        final Employee mark = new Employee("Mark", 35);
        final Employee thomas = new Employee("Thomas", 40);
        robotics.setEmployees(new Employee[] {mark, thomas});

        ksession.insert(robotics);
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Mark", "Thomas");

        list.clear();
        final Employee arnold = new Employee("Arnold", 50);
        robotics.setEmployees(new Employee[] {mark, thomas, arnold});
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Mark", "Thomas", "Arnold");
    }

    @Test
    public void testReactiveSet() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $disease: /wife/children{age > 10}/diseases )\n" +
                        "then\n" +
                        "  list.add( $disease.getName() );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

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
        Assertions.assertThat(list).containsExactlyInAnyOrder("flu", "asthma");

        list.clear();
        charlie.addDisease(new Disease("epilepsy"));
        ksession.fireAllRules();
        Assertions.assertThat(list).containsExactlyInAnyOrder("epilepsy");
    }

    @Test
    public void testReactiveMap() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $bodyMeasurement: /wife/bodyMeasurementsMap/entrySet )\n" +
                        "then\n" +
                        "  list.add( $bodyMeasurement.getValue() );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

        final List<Integer> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Man bob = new Man("Bob", 40);
        final Woman alice = new Woman("Alice", 38);
        alice.putBodyMeasurement(CHEST, 80);
        bob.setWife(alice);

        ksession.insert(bob);
        ksession.fireAllRules();
        Assertions.assertThat(list).containsExactlyInAnyOrder(80);

        list.clear();
        alice.putBodyMeasurement(RIGHT_FOREARM, 38);
        ksession.fireAllRules();
        Assertions.assertThat(list).containsExactlyInAnyOrder(38, 80);
    }

    @Test
    public void testNonReactivePart() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children{age > 10}?/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

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

        Assertions.assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        charlie.addToy( new Toy( "robot" ) );
        ksession.fireAllRules();

        Assertions.assertThat(list).isEmpty();
    }

    @Test
    public void testAllNonReactiveAfterNonReactivePart() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: ?/wife/children{age > 10}/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent( drl, ResourceType.DRL )
                .build()
                .newKieSession();

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

        Assertions.assertThat(list).containsExactlyInAnyOrder("car", "ball");

        list.clear();
        charlie.addToy( new Toy( "robot" ) );
        ksession.fireAllRules();

        Assertions.assertThat(list).isEmpty();
    }

    @Test
    public void testInvalidDoubleNonReactivePart() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife?/children{age > 10}?/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        final KieServices ks = KieServices.Factory.get();
        final KieFileSystem kfs = ks.newKieFileSystem().write( "src/main/resources/r1.drl", drl );
        final Results results = ks.newKieBuilder( kfs ).buildAll().getResults();
        assertTrue( results.hasMessages( Message.Level.ERROR ) );
    }

    @Test
    public void testSingleFireOnReactiveChange() {
        // DROOLS-1302
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children{age > 10}/toys )\n" +
                        "then\n" +
                        "  list.add( $toy );\n" +
                        "end\n";

        final KieBase kbase = new KieHelper().addContent( drl, ResourceType.DRL ).build();
        final KieSession ksession = kbase.newKieSession();

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

        Assertions.assertThat(list).hasSize(1);

        list.clear();

        toy.setName( "eleonor toy 2" );
        ksession.fireAllRules();
        Assertions.assertThat(list).hasSize(1);
    }

    @Test
    public void testReactivitySettingAttributeInDrl() {
        final String drl =
                "import org.drools.compiler.oopath.model.*;\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $child: /wife/children{age >= 10} )\n" +
                        "then\n" +
                        "end\n" +
                        "rule R2 when\n" +
                        "  Man( $child: /wife/children{age < 10} )\n" +
                        "then\n" +
                        "$child.setAge(12);" +
                        "end\n";

        final KieSession ksession = new KieHelper().addContent(drl, ResourceType.DRL)
                .build()
                .newKieSession();

        final Man bob = new Man("Bob", 40);

        final Woman alice = new Woman("Alice", 38);
        final Child charlie = new Child("Charles", 9);
        final Child debbie = new Child("Debbie", 8);
        bob.setWife(alice);
        alice.addChild(charlie);
        alice.addChild(debbie);

        ksession.insert(bob);

        Assertions.assertThat(ksession.fireAllRules()).isEqualTo(4);
    }

    private List<?> factsCollection(KieSession ksession) {
        final List<Object> res = new ArrayList<>();
        res.addAll(ksession.getObjects());
        return res;
    }
}
