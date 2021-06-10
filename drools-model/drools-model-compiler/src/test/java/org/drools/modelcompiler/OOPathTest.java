/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler;


import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Employee;
import org.drools.modelcompiler.domain.InternationalAddress;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Toy;
import org.drools.modelcompiler.domain.Woman;
import org.junit.Test;


import static org.drools.modelcompiler.domain.Employee.createEmployee;

public class OOPathTest extends BaseModelTest {

    public OOPathTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testOOPath() {
        withRule("import org.drools.modelcompiler.domain.*;",
                "global java.util.List list", 
                "rule R", 
                "when",
                " $man: Man( /wife/children[age > 10] )",
                "then",
                "  list.add( $man.getName() );",
                "end");
        withSessionAndGlobals();

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        final Man carl = new Man( "Carl", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );
        
        withFacts(bob, carl);
        whenWeFireAllRules();
        
        resultContainsInAnyOrder("Bob");

    }

    @Test
    public void testOOPathBinding() {
        withRule("import org.drools.modelcompiler.domain.*;",
                "global java.util.List list",
                "rule R",
                "when",
                " Man( /wife[$age : age] )",
                "then",
                "  list.add( $age );",
                "end");

        withSessionAndGlobals();

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        final Man carl = new Man( "Carl", 40 );
        bob.setWife( alice );

        withFacts(bob, carl);
        whenWeFireAllRules();
        resultContainsInAnyOrder(38);
    }

    @Test
    public void testReactiveOOPath() {
        withRule("import org.drools.modelcompiler.domain.*;",
        		"global java.util.List list",
                "rule R",
                "when",
                "  Man( $toy: /wife/children[age > 10]/toys )",
                "then",
                "  list.add( $toy.getName() );",
                "end");
        withSessionAndGlobals();
        
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

        withFacts(bob);
        whenWeFireAllRules();

        resultContainsInAnyOrder("car", "ball");

        list.clear();
        debbie.setAge( 11 );
        whenWeFireAllRules();

        resultContainsInAnyOrder("doll");
    }


    @Test
    public void testBackReferenceConstraint() {
        withRule("import org.drools.modelcompiler.domain.*;",
                "global java.util.List list",
                "rule R",
                "when",
                "  Man( $toy: /wife/children/toys[ name.length == ../name.length ] )",
                "then",
                "  list.add( $toy.getName() );",
                "end");
        withSessionAndGlobals();

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

        withFacts(bob);
        whenWeFireAllRules();
        resultContainsInAnyOrder("ball", "guitar");
    }

    @Test
    public void testSimpleOOPathCast1() {
        withRule("import org.drools.modelcompiler.domain.*;",
        		"global java.util.List list",
        		"rule R",
        		" when",
                "  $man : Man( $italy: /address#InternationalAddress[ state == \"Italy\" ] )",
                "then",
                "  list.add( $man.getName() );",
                "end");
        withSessionAndGlobals();
        withFacts(man("Bob", "Italy"));
        whenWeFireAllRules();
        resultContainsInAnyOrder("Bob");
    }

    @Test
    public void testSimpleOOPathCast2() {
        withRule("import org.drools.modelcompiler.domain.*;",
        		"global java.util.List list",
        		"rule R",
        		"when",
        		"  Man( $name : name, $italy: /address#InternationalAddress[ state == \"Italy\" ] )",
        		"then",
        		"  list.add( $name );",
        		"end");

        withSessionAndGlobals();
        withFacts(man("Bob", "Italy"));
        whenWeFireAllRules();
        resultContainsInAnyOrder("Bob");
    }

    @Test
    public void testSimpleOOPathCast3() {
    	withRule(
    			"import org.drools.modelcompiler.domain.*;", 
    			"global java.util.List list",
    			"rule R",
    			"when",
    			"  Man( $italy: /address#InternationalAddress[ state == \"Italy\" ], $name : name != null )",
    			"then",
    			"  list.add( $name );",
    			"end");
    	withSessionAndGlobals();
        withFacts(man("Bob", "Italy"));
    	whenWeFireAllRules();
    	resultContainsInAnyOrder("Bob");
    }

    @Test
    public void testOOPathMultipleConditions() {
        withRule(
                "import " + Employee.class.getCanonicalName() + ";",
                "import " + Address.class.getCanonicalName() + ";",
                "global java.util.List list",
                "rule R ",
                "when",
                "  Employee( $address: /address[ street == 'Elm', city == 'Big City' ] )",
                "then",
                "  list.add( $address.getCity() );",
                "end");

        withSessionAndGlobals();
        withFacts(createEmployee("Bruno", new Address("Elm", 10, "Small City")), 
        		createEmployee("Alice", new Address("Elm", 10, "Big City")));
        whenWeFireAllRules();
        resultContainsInAnyOrder("Big City");
    }

    @Test
    public void testOOPathMultipleConditionsWithBinding() {
    	withRule("import " + Employee.class.getCanonicalName() + ";",
                "import " + Address.class.getCanonicalName() + ";",
                "global java.util.List list",
                "rule R", 
                "when",
                " $employee: ( Employee( /address[ street == 'Elm', city == 'Big City' ] ) )",
                "then",
                "  list.add( $employee.getName() );",
                "end");
    	withSessionAndGlobals();
        withFacts(createEmployee("Bruno", new Address("Elm", 10, "Small City")),
        		createEmployee("Alice", new Address("Elm", 10, "Big City")));
        whenWeFireAllRules();
        resultContainsInAnyOrder("Alice");
    }

    @Test
    public void testOrConditionalElementNoBinding() {
    	withRule("import " + Employee.class.getCanonicalName() + ";",
                "import " + Address.class.getCanonicalName() + ";",
                "global java.util.List list",
                "rule R",
                "when",
                " $employee: (",
                "  Employee( /address[ city == 'Big City' ] )",
                " or ",
                "  Employee( /address[ city == 'Small City' ] )",
                " )",
                "then",
                "  list.add( $employee.getName() );",
                "end");
    	withSessionAndGlobals();
    	withFacts(createEmployee("Bruno", new Address("Elm", 10, "Small City")),
    			createEmployee("Alice", new Address("Elm", 10, "Big City")));
    	whenWeFireAllRules();
    	resultContainsInAnyOrder("Bruno", "Alice");
    }

    @Test
    public void testOrConditionalElement() {
        withRule("import " + Employee.class.getCanonicalName() + ";",
                "import " + Address.class.getCanonicalName() + ";",
                "global java.util.List list",
                "rule R",
                "when",
                "  Employee( $address: /address[ city == 'Big City' ] )",
                " or ",
                "  Employee( $address: /address[ city == 'Small City' ] )",
                "then",
                "  list.add( $address.getCity() );",
                "end");

        withSessionAndGlobals();
        withFacts(createEmployee("Bruno", new Address("Elm", 10, "Small City")),
        		createEmployee("Alice", new Address("Elm", 10, "Big City")));
        whenWeFireAllRules();
        resultContainsInAnyOrder("Big City", "Small City");
    }

    @Test
    public void testOrConstraintNoBinding() {
        withRule("import " + Employee.class.getCanonicalName() + ";",
        		"import " + Address.class.getCanonicalName() + ";",
        		"global java.util.List list",
        		"rule R",
        		"when",
        		"  $emp: Employee( /address[ street == 'Elm' || city == 'Big City' ] )",
                "        Employee( this != $emp, /address[ street == 'Elm' || city == 'Big City' ] )",
                "then",
                "  list.add( $emp.getName() );",
                "end");
        withSessionAndGlobals();
        withFacts(createEmployee("Bruno", new Address("Elm", 10, "Small City")),
        		createEmployee("Alice", new Address("Elm", 10, "Big City")));
        whenWeFireAllRules();
        resultContainsInAnyOrder("Bruno", "Alice");
    }

    @Test
    public void testOrConstraintWithJoin() {
        withRule("import " + Employee.class.getCanonicalName() + ";",
        		"import " + Address.class.getCanonicalName() + ";",
        		"global java.util.List list",
        		"rule R",
        		"when",
        		"  $emp: Employee( $address: /address[ street == 'Elm' || city == 'Big City' ] )",
        		"        Employee( this != $emp, /address[ street == $address.street || city == 'Big City' ] )",
        		"then",
        		"  list.add( $address.getCity() );",
        		"end");

        withSessionAndGlobals();
        withFacts(createEmployee("Bruno", new Address("Elm", 10, "Small City")), 
        		createEmployee("Alice", new Address("Elm", 10, "Big City")));
        whenWeFireAllRules();
        resultContainsInAnyOrder("Big City", "Small City");
    }

    @Test
    public void testOrConstraint() {
        withRule("import " + Employee.class.getCanonicalName() + ";",
        		"import " + Address.class.getCanonicalName() + ";",
        		"global java.util.List list",
        		"rule R",
        		"when",
        		"  $emp: Employee( $address: /address[ street == 'Elm' || city == 'Big City' ] )",
        		"        Employee( this != $emp, /address[ street == 'Elm' || city == 'Big City' ] )",
        		"then",
        		"  list.add( $address.getCity() );",
        		"end");
        withSessionAndGlobals();
        withFacts(createEmployee("Bruno", new Address("Elm", 10, "Small City")),
        		createEmployee("Alice", new Address("Elm", 10, "Big City")));
        whenWeFireAllRules();
        resultContainsInAnyOrder("Big City", "Small City");
    }
    
	private Man man(String name, String country) {
		final Man bob = new Man(name, 40);
        bob.setAddress(new InternationalAddress("Via Verdi", country));
		return bob;
	}



}
