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

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Employee;
import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieSession;

import static org.drools.modelcompiler.domain.Employee.createEmployee;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class OrTest extends BaseModelTest {

    public OrTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testOr() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $p : Person(name == \"Mark\") or\n" +
                        "  ( $mark : Person(name == \"Mark\")\n" +
                        "    and\n" +
                        "    $p : Person(age > $mark.age) )\n" +
                        "  $s: String(this == $p.name)\n" +
                        "then\n" +
                        "  System.out.println(\"Found: \" + $s);\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( "Mario" );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testOrWhenStringFirst() {
        String str =
              "import " + Person.class.getCanonicalName() + ";" +
              "import " + Address.class.getCanonicalName() + ";" +
              "rule R when\n" +
              "  $s : String(this == \"Go\")\n" +
              "  ( Person(name == \"Mark\") or \n" +
              "     (\n" +
              "     Person(name == \"Mario\") and\n" +
              "     Address(city == \"London\") ) )\n" +
              "then\n" +
              "   System.out.println(\"Found: \" + $s.getClass());\n" +
              "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( "Go" );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Mario", 100 ) );
        ksession.insert( new Address( "London" ) );
        assertEquals(2, ksession.fireAllRules());
    }


    @Test
    public void testOrWithBetaIndex() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person(name == \"Mark\") or\n" +
                     "  ( $mark : Person(name == \"Mark\")\n" +
                     "    and\n" +
                     "    $p : Person(age == $mark.age) )\n" +
                     "  $s: String(this == $p.name)\n" +
                     "then\n" +
                     "  System.out.println(\"Found: \" + $s);\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("Mario");
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 37));
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testOrWithBetaIndexOffset() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $e : Person(name == \"Edson\")\n" +
                     "  $p : Person(name == \"Mark\") or\n" +
                     "  ( $mark : Person(name == \"Mark\")\n" +
                     "    and\n" +
                     "    $p : Person(age == $mark.age) )\n" +
                     "  $s: String(this == $p.name)\n" +
                     "then\n" +
                     "  System.out.println(\"Found: \" + $s);\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("Mario");
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 37));
        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testOrConditional() {
        final String drl =
                "import " + Employee.class.getCanonicalName() + ";" +
                "import " + Address.class.getCanonicalName() + ";" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Employee( $address: address, address.city == 'Big City' )\n" +
                " or " +
                "  Employee( $address: address, address.city == 'Small City' )\n" +
                "then\n" +
                "  list.add( $address.getCity() );\n" +
                "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        Assertions.assertThat(results).containsExactlyInAnyOrder("Big City", "Small City");
    }

    @Test
    public void testOrConstraint() {
        final String drl =
                "import " + Employee.class.getCanonicalName() + ";" +
                "import " + Address.class.getCanonicalName() + ";" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                "  Employee( $address: address, ( address.city == 'Big City' || address.city == 'Small City' ) )\n" +
                "then\n" +
                "  list.add( $address.getCity() );\n" +
                "end\n";

        KieSession kieSession = getKieSession(drl);

        List<String> results = new ArrayList<>();
        kieSession.setGlobal("list", results);

        final Employee bruno = createEmployee("Bruno", new Address("Elm", 10, "Small City"));
        kieSession.insert(bruno);

        final Employee alice = createEmployee("Alice", new Address("Elm", 10, "Big City"));
        kieSession.insert(alice);

        kieSession.fireAllRules();

        Assertions.assertThat(results).containsExactlyInAnyOrder("Big City", "Small City");
    }

    @Test
    public void testOrWithDuplicatedVariables() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List list\n" +
                "\n" +
                "rule R1 when\n" +
                "   Person( $name: name == \"Mark\", $age: age ) or\n" +
                "   Person( $name: name == \"Mario\", $age : age )\n" +
                "then\n" +
                "  list.add( $name + \" is \" + $age);\n" +
                "end\n" +
                "rule R2 when\n" +
                "   $p: Person( name == \"Mark\", $age: age ) or\n" +
                "   $p: Person( name == \"Mario\", $age : age )\n" +
                "then\n" +
                "  list.add( $p + \" has \" + $age + \" years\");\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<String> results = new ArrayList<>();
        ksession.setGlobal("list", results);

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        assertEquals(4, results.size());
        assertTrue(results.contains("Mark is 37"));
        assertTrue(results.contains("Mark has 37 years"));
        assertTrue(results.contains("Mario is 40"));
        assertTrue(results.contains("Mario has 40 years"));
    }

    @Test
    public void generateErrorForEveryFieldInRHSNotDefinedInLHS() {
        // JBRULES-3390
        final String drl1 = "package org.drools.compiler.integrationtests.operators; \n" +
                "declare B\n" +
                "   field : int\n" +
                "end\n" +
                "declare C\n" +
                "   field : int\n" +
                "end\n" +
                "rule R when\n" +
                "( " +
                "   ( B( $bField : field ) or C( $cField : field ) ) " +
                ")\n" +
                "then\n" +
                "    System.out.println($bField);\n" +
                "end\n";

        Results results = getCompilationResults(drl1);
        assertFalse(results.getMessages().isEmpty());
    }

    private Results getCompilationResults( String drl ) {
        return createKieBuilder( drl ).getResults();
    }
}
