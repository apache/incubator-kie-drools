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
package org.drools.model.codegen.execmodel;

import java.util.ArrayList;
import java.util.List;

import org.drools.model.codegen.execmodel.domain.Address;
import org.drools.model.codegen.execmodel.domain.Child;
import org.drools.model.codegen.execmodel.domain.Employee;
import org.drools.model.codegen.execmodel.domain.InternationalAddress;
import org.drools.model.codegen.execmodel.domain.Man;
import org.drools.model.codegen.execmodel.domain.Toy;
import org.drools.model.codegen.execmodel.domain.Woman;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class OOPathTest extends BaseModelTest {

    public OOPathTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testOOPath() {
        final String str =
                "import org.drools.model.codegen.execmodel.domain.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " $man: Man( /wife/children[age > 10] )\n" +
                "then\n" +
                "  list.add( $man.getName() );\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        final Man carl = new Man( "Carl", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        ksession.insert( bob );
        ksession.insert( carl );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("Bob");
    }

    @Test
    public void testOOPathBinding() {
        final String str =
                "import org.drools.model.codegen.execmodel.domain.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " Man( /wife[$age : age] )\n" +
                "then\n" +
                "  list.add( $age );\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        final List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        final Man carl = new Man( "Carl", 40 );
        bob.setWife( alice );

        ksession.insert( bob );
        ksession.insert( carl );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder(38);
    }

    @Test
    public void testReactiveOOPath() {
        final String str =
                "import org.drools.model.codegen.execmodel.domain.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toy: /wife/children[age > 10]/toys )\n" +
                        "then\n" +
                        "  list.add( $toy.getName() );\n" +
                        "end\n";

        KieSession ksession = getKieSession( str );

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
    public void testBackReferenceConstraint() {
        final String str =
                "import org.drools.model.codegen.execmodel.domain.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Man( $toy: /wife/children/toys[ name.length == ../name.length ] )\n" +
                "then\n" +
                "  list.add( $toy.getName() );\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Carl", 12 );
        final Child debbie = new Child( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );
        debbie.addToy( new Toy( "guitar" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("ball", "guitar");
    }

    @Test
    public void testSimpleOOPathCast1() {
        final String str = "import org.drools.model.codegen.execmodel.domain.*;\n" +
                           "global java.util.List list\n" +
                           "\n" +
                           "rule R when\n" +
                           "  $man : Man( $italy: /address#InternationalAddress[ state == \"Italy\" ] )\n" +
                           "then\n" +
                           "  list.add( $man.getName() );\n" +
                           "end\n";

        KieSession ksession = getKieSession(str);

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Man bob = new Man("Bob", 40);
        bob.setAddress(new InternationalAddress("Via Verdi", "Italy"));
        ksession.insert(bob);
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("Bob");
    }

    @Test
    public void testSimpleOOPathCast2() {
        final String str = "import org.drools.model.codegen.execmodel.domain.*;\n" +
                           "global java.util.List list\n" +
                           "\n" +
                           "rule R when\n" +
                           "  Man( $name : name, $italy: /address#InternationalAddress[ state == \"Italy\" ] )\n" +
                           "then\n" +
                           "  list.add( $name );\n" +
                           "end\n";

        KieSession ksession = getKieSession(str);

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Man bob = new Man("Bob", 40);
        bob.setAddress(new InternationalAddress("Via Verdi", "Italy"));
        ksession.insert(bob);
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("Bob");
    }

    @Test
    public void testSimpleOOPathCast3() {
        final String str = "import org.drools.model.codegen.execmodel.domain.*;\n" +
                           "global java.util.List list\n" +
                           "\n" +
                           "rule R when\n" +
                           "  Man( $italy: /address#InternationalAddress[ state == \"Italy\" ], $name : name != null )\n" +
                           "then\n" +
                           "  list.add( $name );\n" +
                           "end\n";

        KieSession ksession = getKieSession(str);

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Man bob = new Man("Bob", 40);
        bob.setAddress(new InternationalAddress("Via Verdi", "Italy"));
        ksession.insert(bob);
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("Bob");
    }

    @Test
    public void testOOPathMultipleConditions() {
        final String drl =
                "import " + Employee.class.getCanonicalName() + ";" +
                "import " + Address.class.getCanonicalName() + ";" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Employee( $address: /address[ street == 'Elm', city == 'Big City' ] )\n" +
                "then\n" +
                "  list.add( $address.getCity() );\n" +
                "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = Employee.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = Employee.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        assertThat(results).containsExactlyInAnyOrder("Big City");
    }

    @Test
    public void testOOPathMultipleConditionsWithBinding() {
        final String drl =
                "import " + Employee.class.getCanonicalName() + ";" +
                "import " + Address.class.getCanonicalName() + ";" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " $employee: (\n" +
                "  Employee( /address[ street == 'Elm', city == 'Big City' ] )\n" +
                " )\n" +
                "then\n" +
                "  list.add( $employee.getName() );\n" +
                "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = Employee.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = Employee.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        assertThat(results).containsExactlyInAnyOrder("Alice");
    }

    @Test
    public void testOrConditionalElementNoBinding() {
        final String drl =
                "import " + Employee.class.getCanonicalName() + ";" +
                "import " + Address.class.getCanonicalName() + ";" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " $employee: (\n" +
                "  Employee( /address[ city == 'Big City' ] )\n" +
                " or " +
                "  Employee( /address[ city == 'Small City' ] )\n" +
                " )\n" +
                "then\n" +
                "  list.add( $employee.getName() );\n" +
                "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = Employee.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = Employee.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        assertThat(results).containsExactlyInAnyOrder("Bruno", "Alice");
    }

    @Test
    public void testOrConditionalElement() {
        final String drl =
                "import " + Employee.class.getCanonicalName() + ";" +
                "import " + Address.class.getCanonicalName() + ";" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Employee( $address: /address[ city == 'Big City' ] )\n" +
                " or " +
                "  Employee( $address: /address[ city == 'Small City' ] )\n" +
                "then\n" +
                "  list.add( $address.getCity() );\n" +
                "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = Employee.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = Employee.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        assertThat(results).containsExactlyInAnyOrder("Big City", "Small City");
    }

    @Test
    public void testOrConstraintNoBinding() {
        final String drl =
                "import " + Employee.class.getCanonicalName() + ";" +
                        "import " + Address.class.getCanonicalName() + ";" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  $emp: Employee( /address[ street == 'Elm' || city == 'Big City' ] )\n" +
                        "        Employee( this != $emp, /address[ street == 'Elm' || city == 'Big City' ] )\n" +
                        "then\n" +
                        "  list.add( $emp.getName() );\n" +
                        "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = Employee.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = Employee.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        assertThat(results).containsExactlyInAnyOrder("Bruno", "Alice");
    }

    @Test
    public void testOrConstraintWithJoin() {
        final String drl =
                "import " + Employee.class.getCanonicalName() + ";" +
                        "import " + Address.class.getCanonicalName() + ";" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  $emp: Employee( $address: /address[ street == 'Elm' || city == 'Big City' ] )\n" +
                        "        Employee( this != $emp, /address[ street == $address.street || city == 'Big City' ] )\n" +
                        "then\n" +
                        "  list.add( $address.getCity() );\n" +
                        "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = Employee.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = Employee.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        assertThat(results).containsExactlyInAnyOrder("Big City", "Small City");
    }

    @Test
    public void testOrConstraint() {
        final String drl =
                "import " + Employee.class.getCanonicalName() + ";" +
                        "import " + Address.class.getCanonicalName() + ";" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  $emp: Employee( $address: /address[ street == 'Elm' || city == 'Big City' ] )\n" +
                        "        Employee( this != $emp, /address[ street == 'Elm' || city == 'Big City' ] )\n" +
                        "then\n" +
                        "  list.add( $address.getCity() );\n" +
                        "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = Employee.createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = Employee.createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();
        assertThat(results).containsExactlyInAnyOrder("Big City", "Small City");
    }

}
