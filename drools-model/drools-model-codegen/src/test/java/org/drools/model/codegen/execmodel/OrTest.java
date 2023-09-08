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

import org.drools.model.codegen.execmodel.domain.Address;
import org.drools.model.codegen.execmodel.domain.Employee;
import org.drools.model.codegen.execmodel.domain.Person;
import org.junit.Test;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
        assertThat(ksession.fireAllRules()).isEqualTo(1);
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
        assertThat(ksession.fireAllRules()).isEqualTo(2);
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
        assertThat(ksession.fireAllRules()).isEqualTo(1);
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
        assertThat(ksession.fireAllRules()).isEqualTo(1);
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
                "  Employee( $address: address, ( address.city == 'Big City' || address.city == 'Small City' ) )\n" +
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

        assertThat(results.size()).isEqualTo(4);
        assertThat(results.contains("Mark is 37")).isTrue();
        assertThat(results.contains("Mark has 37 years")).isTrue();
        assertThat(results.contains("Mario is 40")).isTrue();
        assertThat(results.contains("Mario has 40 years")).isTrue();
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
        assertThat(results.getMessages().isEmpty()).isFalse();
    }

    private Results getCompilationResults( String drl ) {
        return createKieBuilder( drl ).getResults();
    }

    @Test
    public void testMultipleFiringWithOr() {
        // DROOLS-7466
        final String str =
                "rule R when\n" +
                "    (or\n" +
                "        $val: String() from \"foo\"\n" +
                "        $val: String() from \"bar\")\n" +
                "then\n" +
                "end \n";

        KieSession ksession = getKieSession( str );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }
}
