/*
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

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.math3.util.Pair;
import org.assertj.core.api.Assertions;
import org.drools.model.codegen.execmodel.domain.Address;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.internal.event.rule.RuleEventListener;
import org.kie.internal.event.rule.RuleEventManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.codegen.execmodel.CepTest.getCepKieModuleModel;

public class ExisistentialTest extends BaseModelTest {

    @ParameterizedTest
	@MethodSource("parameters")
    public void testNot(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  not( Person( name.length == 4 ) )\n" +
                "then\n" +
                "  insert(new Result(\"ok\"));\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person mario = new Person( "Mario", 40 );

        ksession.insert( mario );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("ok");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testNotEmptyPredicate(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  not( Person( ) )\n" +
                "then\n" +
                "  insert(new Result(\"ok\"));\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person mario = new Person( "Mario", 40 );

        ksession.insert( mario );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(0);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testExists(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  exists Person( name.length == 5 )\n" +
                        "then\n" +
                        "  insert(new Result(\"ok\"));\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Person mario = new Person( "Mario", 40 );

        ksession.insert( mario );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("ok");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testForall(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  forall( $p : Person( name.length == 5 ) " +
                "       Person( this == $p, age > 40 ) )\n" +
                "then\n" +
                "  insert(new Result(\"ok\"));\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( new Person( "Mario", 41 ) );
        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Edson", 42 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("ok");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testForallInQuery(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "query ifAllPersonsAreOlderReturnThem (int pAge)\n" +
                "    forall ( Person(age > pAge) )\n" +
                "    $person : Person()\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( new Person( "Mario", 41 ) );
        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Edson", 42 ) );
        ksession.fireAllRules();

        QueryResults results = ksession.getQueryResults( "ifAllPersonsAreOlderReturnThem", 30 );

        assertThat(results.size()).isEqualTo(3);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testForallSingleConstraint(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Result.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  forall( Person( name.length == 5 ) )\n" +
                     "then\n" +
                     "  insert(new Result(\"ok\"));\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new Person("Mario"));
        ksession.insert(new Person("Edson"));
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("ok");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testForallEmptyConstraint(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Result.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  forall( Person() )\n" +
                     "then\n" +
                     "  insert(new Result(\"ok\"));\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new Person("Mario"));
        ksession.insert(new Person("Mark"));
        ksession.insert(new Person("Edson"));
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("ok");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testExistsEmptyPredicate(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  exists( Person() )\n" +
                "then\n" +
                "  insert(new Result(\"ok\"));\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person mark = new Person( "Mark", 37 );
        Person mario = new Person( "Mario", 40 );

        ksession.insert( mark );
        ksession.insert( mario );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("ok");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testComplexNots(RUN_TYPE runType) throws Exception {
        String str =
                "package org.drools.testcoverage.regression;\n" +
                "\n" +
                "declare BaseEvent\n" +
                "  @role(event)\n" +
                "end\n" +
                "\n" +
                "declare Event extends BaseEvent\n" +
                "  @role(event)\n" +
                "  property : String\n" +
                "end\n" +
                "\n" +
                "declare NotEvent extends BaseEvent\n" +
                "  @role(event)\n" +
                "  property : String\n" +
                "end\n" +
                "\n" +
                "rule Init when then drools.getEntryPoint(\"entryPoint\").insert(new NotEvent(\"value\")); end\n" +
                "\n" +
                "rule \"not equal\" when\n" +
                "    not (\n" +
                "      ( and\n" +
                "          $e : BaseEvent( ) over window:length(3) from entry-point entryPoint\n" +
                "          NotEvent( this == $e, property == \"value\" ) from entry-point entryPoint\n" +
                "      )\n" +
                "    )\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule \"not equal 2\" when\n" +
                "    not (\n" +
                "      $e : NotEvent( ) over window:length(3) and\n" +
                "      NotEvent( this == $e, property == \"value\" )\n" +
                "    )\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "rule \"different\" when\n" +
                "    NotEvent( property != \"value\" ) over window:length(3) from entry-point entryPoint\n" +
                "then\n" +
                "end\n" +
                "";

        KieSession ksession = getKieSession(runType, getCepKieModuleModel(), str );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }


    @ParameterizedTest
	@MethodSource("parameters")
    public void testDuplicateBindingNameInDifferentScope(RUN_TYPE runType) {
        final String drl1 =
                "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    exists( $fact : String( length == 4 ) and String( this == $fact ) )\n" +
                "    exists( $fact : Person( age == 18 ) and Person( this == $fact ) )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        ksession.insert( "test" );
        ksession.insert( new Person("test", 18) );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testNotWithDereferencingConstraint(RUN_TYPE runType) {
        final String drl1 =
                "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  Person( $name : name )\n" +
                "  not Person( name.length == $name.length )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        ksession.insert( new Person("test", 18) );
        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void test2NotsWithAnd(RUN_TYPE runType) {
        final String drl1 =
                "package org.drools.compiler\n" +
                "rule R when\n" +
                "  (not (and Integer( $i : intValue )\n" +
                "            String( length > $i ) \n" +
                "       )\n" +
                "  )\n" +
                "  (not (and Integer( $i : intValue )\n" +
                "            String( length > $i ) \n" +
                "       )\n" +
                "  )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }


    @ParameterizedTest
	@MethodSource("parameters")
    public void testExistsWithAJoin(RUN_TYPE runType) {
        // DROOLS-7065
        final String drl1 =
                "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Pair.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  Pair($right: value)\n" +
                "  exists Person(name != null, this == $right)\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        AtomicInteger matchCount = new AtomicInteger(0); // Atomic only so that I get an effectively-final int.
        ((RuleEventManager) ksession).addEventListener(new RuleEventListener() {
            @Override
            public void onAfterMatchFire(Match match) {
                System.out.println("Fired " + match.getObjects());
                matchCount.incrementAndGet();
            }

            @Override
            public void onDeleteMatch(Match match) {
                System.out.println("Deleted " + match.getObjects());
                matchCount.decrementAndGet();
            }

        });

        Person lukas = new Person( "Lukas", 37 );
        Person mario = new Person( "Mario", 40 );
        Person mark = new Person( "Mark", 37 );
        Pair<Person, Person> pair = Pair.create(mario, lukas);
        Pair<Person, Person> pair2 = Pair.create(mark, lukas);

        ksession.insert(pair);
        ksession.insert(pair2);
        FactHandle lukasFh = ksession.insert(lukas);
        FactHandle marioFh = ksession.insert(mario);
        ksession.insert(mark);
        ksession.fireAllRules();
        Assertions.assertThat(matchCount).hasValue(2);
        // Now we have two matches Mario+Lukas, Mark+Lukas.

        mario.setName("Also Mario");
        ksession.update(marioFh, mario); // If we don't make this update, the test passes.
        lukas.setName(null);
        ksession.update(lukasFh, lukas);
        ksession.fireAllRules();
        // We have set Lukas' name to null,
        // therefore "exists Person(getName() != null)" no longer matches,
        // therefore both matches should have been removed.
        Assertions.assertThat(matchCount).hasValue(0);
        // Yet only one was removed.
    }

    @ParameterizedTest
    @MethodSource("parameters")
    void joinFromExistsNot(RUN_TYPE runType) {
        final String drl =
                "import " + Person.class.getCanonicalName() + ";\n" +
                        "import " + Address.class.getCanonicalName() + ";\n" +
                        """
                                rule R1
                                when
                                    String()
                                    $person: Person()
                                    exists Address(number > 18) from $person.addresses
                                    not Integer()
                                then
                                end
                                """;

        KieSession ksession = getKieSession(runType, drl);

        ksession.insert("test");
        Person person = new Person();
        Address address = new Address("ABC st.", 20, "London");
        person.getAddresses().add(address);
        ksession.insert(person);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }
}
