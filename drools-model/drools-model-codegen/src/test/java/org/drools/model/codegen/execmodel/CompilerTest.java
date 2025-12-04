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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.drools.model.codegen.execmodel.domain.Address;
import org.drools.model.codegen.execmodel.domain.Adult;
import org.drools.model.codegen.execmodel.domain.Child;
import org.drools.model.codegen.execmodel.domain.Man;
import org.drools.model.codegen.execmodel.domain.Overloaded;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Pet;
import org.drools.model.codegen.execmodel.domain.Result;
import org.drools.model.codegen.execmodel.domain.StockTick;
import org.drools.model.codegen.execmodel.domain.Toy;
import org.drools.model.codegen.execmodel.domain.Woman;
import org.drools.modelcompiler.util.EvaluationUtil;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessContext;
import org.kie.api.runtime.rule.FactHandle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class CompilerTest extends BaseModelTest {

    @ParameterizedTest
    @MethodSource("parameters")
    @Timeout(5000)
    public void testPropertyReactivity(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { setAge($p.getAge()+1) }\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person me = new Person( "Mario", 40 );
        ksession.insert( me );
        ksession.fireAllRules();

        assertThat(me.getAge()).isEqualTo(41);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testPropertyReactivityWithArguments(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R \n" +
                "when\n" +
                "    $p: Person()\n" +
                "then\n" +
                "    modify($p) { setLikes( String.valueOf(($p.getAddress().getStreet() + $p.getAddress().getCity()))) };\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person me = new Person( "Mario", 40 );
        me.setAddress(new Address("street1", 2, "city1"));
        ksession.insert( me );
        ksession.fireAllRules();

        assertThat(me.getLikes()).isEqualTo("street1city1");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testPropertyReactvityOnFinalField(RUN_TYPE runType) throws Exception {
        String str =
                "rule R when\n" +
                "    $a : String( length > 3 )\n" +
                "then\n" +
                "  System.out.println($a);\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( "abcd" );
        ksession.insert( "xy" );

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testEqualityCheckOnNull(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  Person($name : name == \"Mario\")\n" +
                "then\n" +
                "  insert(new Result($name));\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        final Person mario = new Person("Mario", 40);
        final Person luca = new Person(null, 33);

        ksession.insert(mario);
        ksession.insert(luca);

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Mario");
    }

    @DisabledIfSystemProperty(named = "drools.drl.antlr4.parser.enabled", matches = "true")
    @ParameterizedTest
    @MethodSource("parameters")
    public void testOrWithFixedLeftOperand(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person(name == \"Mario\" || == \"Luca\" || == \"Edoardo\")\n" +
                "then\n" +
                "  insert(new Result($p));\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        final Person mario = new Person("Mario", 40);
        final Person luca = new Person("Luca", 33);
        final Person edoardo = new Person("Edoardo", 31);
        final Person matteo = new Person("Matteo", 36);

        ksession.insert(mario);
        ksession.insert(luca);
        ksession.insert(edoardo);
        ksession.insert(matteo);

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(3);
        assertThat(results.stream().map(r -> r.getValue())).containsExactlyInAnyOrder(mario, luca, edoardo);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testCapitalLetterProperty(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( ParentP!.name == \"Luca\")\n" +
                "then\n" +
                "  insert(new Result($p));\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        final Person luca = new Person("Luca", 35);
        final Person leonardo = new Person("Leonardo", 2).setParentP(luca);
        final Person edoardo = new Person("Edoardo", 31);

        ksession.insert(leonardo);
        ksession.insert(luca);
        ksession.insert(edoardo);

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.stream().map(Result::getValue)).containsExactlyInAnyOrder(leonardo);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBeta(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $markV : Person(name == \"Mark\")\n" +
                "  $olderV : Person(name != \"Mark\", age > $markV.age)\n" +
                "then\n" +
                "  $r.setValue($olderV.getName() + \" is older than \" + $markV.getName());\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Mario is older than Mark");

        result.setValue( null );
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        mark.setAge( 34 );
        ksession.update( markFH, mark, "age" );

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Edson is older than Mark");
    }


    @ParameterizedTest
    @MethodSource("parameters")
    public void testBetaMap(RUN_TYPE runType) {

        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Map.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $markV : Map(this['name'] == 'Mark')\n" +
                "  $olderV : Map(this['name'] != 'Mark', this['age'] > $markV['age'])\n" +
                "then\n" +
                "  $r.setValue($olderV.get(\"name\") + \" is older than \" + $markV.get(\"name\"));\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );


        Map<String, Object> mark = mapPerson("Mark", 37);
        Map<String, Object> edson = mapPerson("Edson", 35);
        Map<String, Object> mario = mapPerson("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Mario is older than Mark");

        result.setValue( null );
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        mark.put("age", 34 );
        ksession.update( markFH, mark );

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Edson is older than Mark");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBetaMapComparisonWithLiteral(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Map.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $olderV : Map(this['name'] != 'Mark', this['age'] > 37)\n" +
                "then\n" +
                "  $r.setValue($olderV.get(\"name\") + \" is older than Mark\"\n);" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );


        Map<String, Object> mark = mapPerson("Mark", 37);
        Map<String, Object> edson = mapPerson("Edson", 35);
        Map<String, Object> mario = mapPerson("Mario", 40);

        ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Mario is older than Mark");

        result.setValue( null );
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();
    }

    private Map<String, Object> mapPerson(String name, int age) {
        HashMap<String, Object> person = new HashMap<>();
        person.put("name", name);
        person.put("age", age);
        return person;
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testRuleExtends(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "  $r : Result()\n" +
                "then\n" +
                "end\n" +
                "rule R2 extends R1 when\n" +
                "  $p1 : Person(name == \"Mark\")\n" +
                "then\n" +
                "end\n" +
                "rule R3 extends R2 when\n" +
                "  $p2 : Person(name != \"Mark\", age > $p1.age)\n" +
                "then\n" +
                "  $r.setValue($p2.getName() + \" is older than \" + $p1.getName());\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        assertThat(ksession.fireAllRules()).isEqualTo(0);

        ksession.insert(new Person("Mark", 37));
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        assertThat(result.getValue()).isEqualTo("Mario is older than Mark");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBetaWithDeclaration(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $p1 : Person(name == \"Mark\", $markAge : age)\n" +
                "  $p2 : Person(name != \"Mark\", age > $markAge)\n" +
                "then\n" +
                "  $r.setValue($p2.getName() + \" is older than \" + $p1.getName());\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Mario is older than Mark");

        result.setValue( null );
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertThat(result.getValue()).isNull();

        mark.setAge( 34 );
        ksession.update( markFH, mark, "age" );

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Edson is older than Mark");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void test3Patterns(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $mark : Person(name == \"Mark\")\n" +
                "  $p : Person(age > $mark.age)\n" +
                "  $s: String(this == $p.name)\n" +
                "then\n" +
                "  System.out.println(\"Found: \" + $s);\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( "Mario" );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testSimpleInsert(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( name.length == 4 )\n" +
                "then\n" +
                "  Result r = new Result($p.getName());" +
                "  insert(r);\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Mark");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testSimpleInsertWithProperties(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( address.city.startsWith(\"M\"))\n" +
                "then\n" +
                "  Result r = new Result($p.getName());" +
                "  insert(r);\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( new Person( "Mark", 37, new Address("London")) );
        ksession.insert( new Person( "Luca", 32 , new Address("Milan")) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Luca");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testSimpleDelete(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( name.length == 4 )\n" +
                "then\n" +
                "  Result r = new Result($p.getName());" +
                "  insert(r);\n" +
                "  delete($p);\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Mark");
        assertThat(getObjectsIntoList(ksession, Person.class).size()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testSimpleInsertDeleteExplicitScope(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( name.length == 4 )\n" +
                "then\n" +
                "  Result r = new Result($p.getName());" +
                "  drools.insert(r);\n" +
                "  drools.delete($p);\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Mark");
        assertThat(getObjectsIntoList(ksession, Person.class).size()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testSimpleUpdate(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( name.length == 4 )\n" +
                "then\n" +
                "  $p.setAge($p.getAge()+1);" +
                "  update($p);\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person mark = new Person( "Mark", 37 );
        Person mario = new Person( "Mario", 40 );

        ksession.insert( mark );
        ksession.insert( mario );
        ksession.fireAllRules();

        assertThat(mark.getAge()).isEqualTo(38);
        assertThat(mario.getAge()).isEqualTo(40);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testSimpleModify(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( name.length == 4 )\n" +
                "then\n" +
                "  modify($p) { setAge($p.getAge()+1) }\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person mark = new Person( "Mark", 37 );
        Person mario = new Person( "Mario", 40 );

        ksession.insert( mark );
        ksession.insert( mario );
        ksession.fireAllRules();

        assertThat(mark.getAge()).isEqualTo(38);
        assertThat(mario.getAge()).isEqualTo(40);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testEmptyPattern(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  Person() \n" +
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
    public void testEmptyPatternWithBinding(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person() \n" +
                "then\n" +
                "  insert(new Result($p.getName()));\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person mario = new Person( "Mario", 40 );

        ksession.insert( mario );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Mario");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testFrom(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";\n" +
                "import " + Adult.class.getCanonicalName() + ";\n" +
                "import " + Child.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $a : Adult()\n" +
                "  $c : Child( age > 8 ) from $a.children\n" +
                "then\n" +
                "  $r.setValue($c.getName());\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );

        Adult dad = new Adult( "dad", 40 );
        dad.addChild( new Child( "Alan", 10 ) );
        dad.addChild( new Child( "Betty", 7 ) );
        ksession.insert( dad );
        ksession.fireAllRules();

        assertThat(result.getValue()).isEqualTo("Alan");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testConcatenatedFrom(RUN_TYPE runType) {
        checkConcatenatedFrom(runType, true);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testConcatenatedFromWithCondition(RUN_TYPE runType) {
        checkConcatenatedFrom(runType, false);
    }

    private void checkConcatenatedFrom(RUN_TYPE runType, boolean withCondition) {
        String str =
                "import " + Result.class.getCanonicalName() + ";\n" +
                "import " + Man.class.getCanonicalName() + ";\n" +
                "import " + Woman.class.getCanonicalName() + ";\n" +
                "import " + Child.class.getCanonicalName() + ";\n" +
                "import " + Toy.class.getCanonicalName() + ";\n" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  $m : Man(" + (withCondition ? "age > 0" : "") + ")\n" +
                "  $w : Woman() from $m.wife\n" +
                "  $c : Child( age > 10 ) from $w.children\n" +
                "  $t : Toy() from $c.toys\n" +
                "then\n" +
                "  list.add($t.getName());\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );

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
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testAgeWithSum(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( personAge : age )\n" +
                "  $plusTwo : Person(age == personAge + 2 )\n" +
                "then\n" +
                "  insert(new Result($plusTwo.getName()));\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Mark");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testAgeWithSumUsing2DeclarationInBeta(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( personAge : age )\n" +
                     "  $plusTwo : Person(age == personAge + 2 + $p.age - $p.age )\n" +
                     "then\n" +
                     "  insert(new Result($plusTwo.getName()));\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new Person("Mario", 40));
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        System.out.println(results);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Mark");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testFunction3(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "function Boolean isFortyYearsOld(Person p, Boolean booleanParameter) {\n" +
                "    return p.getAge() == 40; \n"+
                "}" +
                "rule R when\n" +
                "  $p : Person(isFortyYearsOld(this, true))\n" +
                "then\n" +
                "  insert(new Result($p.getName()));\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Mario");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testInsertLogical(RUN_TYPE runType) {
        String str = "rule R when\n" +
                     "  Integer()" +
                     "then\n" +
                     "  insertLogical(\"Hello World\");\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        FactHandle fh_47 = ksession.insert(47);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.contains("Hello World")).isTrue();

        ksession.delete(fh_47);
        ksession.fireAllRules();

        results = getObjectsIntoList(ksession, String.class);
        assertThat(results.contains("Hello World")).isFalse();
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testModifyRewriteAvoidTwiceThePreceeding(RUN_TYPE runType) {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "global java.util.List globalA \n" +
                     "global java.util.List globalB \n" +
                     "rule R \n" +
                     "when\n" +
                     "  $p : Person()" +
                     "then\n" +
                     "  globalA.add(\"A\");\n" +
                     "  modify( $p ) { setAge(47); }\n" +
                     "  globalB.add(\"B\");\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        List globalA = new ArrayList<>();
        List globalB = new ArrayList<>();
        ksession.setGlobal("globalA", globalA);
        ksession.setGlobal("globalB", globalB);

        ksession.insert(new Person("person1"));
        ksession.fireAllRules();

        assertThat(globalA.size()).isEqualTo(1);
        assertThat(globalB.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testEmptyModifyRewrite(RUN_TYPE runType) {
        String str = "rule R \n" +
                     "no-loop \n" +
                     "when\n" +
                     "  $s : String()" +
                     "then\n" +
                     "  System.out.println(\"intentional empty modify on $s\" + $s);" +
                     "  modify( $s ) { }\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert("Hello World");
        int fired = ksession.fireAllRules();

        assertThat(fired).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testModifyRewriteWithComments(RUN_TYPE runType) {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "global java.util.List globalA \n" +
                     "global java.util.List globalB \n" +
                     "rule R \n" +
                     "when\n" +
                     "  $p : Person()" +
                     "then\n" +
                     "  globalA.add(\"A\");\n" +
                     "  modify( $p ) {\n" +
                     "    // modify ; something\n" +
                     "    /* modify ; something */\n" +
                     "    setAge(47)\n" +
                     "  }\n" +
                     "  globalB.add(\"B\");\n" +
                     "  // modify ; something\n" +
                     "  /* modify ; something */\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        List globalA = new ArrayList<>();
        List globalB = new ArrayList<>();
        ksession.setGlobal("globalA", globalA);
        ksession.setGlobal("globalB", globalB);

        Person person1 = new Person("person1");
        ksession.insert(person1);
        ksession.fireAllRules();

        assertThat(globalA.size()).isEqualTo(1);
        assertThat(globalB.size()).isEqualTo(1);
        assertThat(person1.getAge()).isEqualTo(47);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    @Disabled("fails for exec model, is not recognizing properly start/ends of modify block")
    public void testModifyRewriteWithCommentsAbsurd(RUN_TYPE runType) {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "global java.util.List globalA \n" +
                     "global java.util.List globalB \n" +
                     "rule R \n" +
                     "when\n" +
                     "  $p : Person()" +
                     "then\n" +
                     "  globalA.add(\"A\");\n" +
                     "  modify( $p ) {\n" +
                     "    // modify( $p ) { setAge(1) } \n" +
                     "    /* modify( $p ) { setAge(2) } */\n" +
                     "    setAge(47)\n" +
                     "  }\n" +
                     "  globalB.add(\"B\");\n" +
                     "  // modify( $p ) { setAge(1) }\n" +
                     "  /* modify( $p ) { setAge(2) } */\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        List globalA = new ArrayList<>();
        List globalB = new ArrayList<>();
        ksession.setGlobal("globalA", globalA);
        ksession.setGlobal("globalB", globalB);

        Person person1 = new Person("person1");
        ksession.insert(person1);
        ksession.fireAllRules();

        assertThat(globalA.size()).isEqualTo(1);
        assertThat(globalB.size()).isEqualTo(1);
        assertThat(person1.getAge()).isEqualTo(47);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testConstraintContainingAMethodCallWithParams(RUN_TYPE runType) {
        String str = "import " + Overloaded.class.getCanonicalName() + ";" +
                     "rule OverloadedMethods\n" +
                     "when\n" +
                     "  o : Overloaded( method(5, 9, \"x\") == 15 )\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new Overloaded());
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testSimpleModifyUsingNameRefFollowedByMethodCall(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( name.length() == 4 )\n" +
                     "then\n" +
                     "  modify($p) { setAge($p.getAge()+1) }\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        Person mark = new Person("Mark", 37);
        Person mario = new Person("Mario", 40);

        ksession.insert(mark);
        ksession.insert(mario);
        ksession.fireAllRules();

        assertThat(mark.getAge()).isEqualTo(38);
        assertThat(mario.getAge()).isEqualTo(40);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testChainOfMethodCallInConstraint(RUN_TYPE runType) {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( getAddress().getCity().length() == 5 )\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        Person john = new Person("John", 47);
        Address a = new Address("Italy");
        john.setAddress(a);

        ksession.insert(john);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testChainOfMethodCallInConstraintSub(RUN_TYPE runType) {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( address.(city.startsWith(\"I\") &&  city.length() == 5  ) )\n" + // DRL feature "Grouped accessors for nested objects" is addressed by the RuleDescr directly.
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        Person john = new Person("John", 47);
        Address a = new Address("Italy");
        john.setAddress(a);

        ksession.insert(john);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testChainFieldAccessorsAndMethodCall(RUN_TYPE runType) {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( address.getCity().length == 5 )\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        Person john = new Person("John", 47);
        Address a = new Address("Italy");
        john.setAddress(a);

        ksession.insert(john);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertThat(results.size()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testInnerBindingWithOr(RUN_TYPE runType) {
        String str =
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " s: String( s.toString() == \"x\" || s.toString() == \"y\" )\n" +
                "then\n" +
                " list.add(s);\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.insert("y");
        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("y");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testRHS(RUN_TYPE runType) {
        String str =
                "rule R when\n" +
                "        //conditions\n" +
                "    then\n" +
                "        drools.halt();\n" +
                "        drools.getWorkingMemory();\n" +
                "        drools.setFocus(\"agenda-group\");\n" +
                "        drools.getRule();\n" +
                "        drools.getTuple();\n" +
                "        System.out.println(kcontext);\n" +
                "        kcontext.getKnowledgeRuntime();\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBindWith2Arguments(RUN_TYPE runType) {
        String str =
                "import " + Adult.class.getCanonicalName() + ";\n" +
                        "import " + Child.class.getCanonicalName() + ";\n" +
                        "import " + Result.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "  $y : Adult( $sum : (name.length + age) )\n" +
                        "then\n" +
                        "  insert(new Result($sum));\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Adult a = new Adult( "Mario", 43 );
        ksession.insert( a );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(((Number) results.iterator().next().getValue()).intValue()).isEqualTo(48);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testLockOnActiveWithModify(RUN_TYPE runType) {
        String str =
                "package org.drools.test; \n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"Rule1\" \n" +
                "@Propagation(EAGER) \n" +
                "salience 1 \n" +
                "lock-on-active true\n" +
                "when\n" +
                "  $p: Person()\n" +
                "then\n" +
                "  System.out.println( \"Rule1\" ); \n" +
                "  modify( $p ) { setAge( 44 ); }\n" +
                "end;\n" +
                "\n" +
                "rule \"Rule2\"\n" +
                "@Propagation(EAGER) \n" +
                "lock-on-active true\n" +
                "when\n" +
                "  $p: Person() \n" +
                "  String() from $p.getName() \n" +
                "then\n" +
                "  System.out.println( \"Rule2\" + $p ); " +
                "  modify ( $p ) { setName( \"john\" ); } \n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.fireAllRules();

        Person p = new Person( "mark", 76 );
        ksession.insert( p );
        ksession.fireAllRules();

        assertThat(p.getAge()).isEqualTo(44);
        assertThat(p.getName()).isEqualTo("john");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testAlphaConstraintOn2Properties(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( age > name.length )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person me = new Person( "Mario", 40 );
        ksession.insert( me );
        ksession.fireAllRules();
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testAlphaNull(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule R1 when\n" +
                        "  $p : Person( name == null)\n" +
                        "then\n" +
                        "  insert(new Result($p.getName()));\n" +

                        "end\n" +
                        "rule R2 when\n" +
                        "  $p : Person(  name == \"Luca\")\n" +
                        "then\n" +
                        "  insert(new Result($p.getName()));\n" +
                        "end\n" +
                        "rule R3 when\n" +
                        "  $p : Person(  name == \"Pippo\")\n" +
                        "then\n" +
                        "  insert(new Result($p.getName()));\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Person first = new Person(null, 40);
        Person second = new Person("Luca", 40);
        Person third = new Person("Mario", 40);
        ksession.insert(first);
        ksession.insert(second);
        ksession.insert(third);
        ksession.fireAllRules();

        List<Object> results = getObjectsIntoList(ksession, Result.class)
                .stream().map(Result::getValue).collect(Collectors.toList());
        assertThat(results.size()).isEqualTo(2);

        assertThat(results).containsExactlyInAnyOrder("Luca", null);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testAlphaNullBoolean(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule R1 when\n" +
                        "  $p : Person( employed == true)\n" +
                        "then\n" +
                        "  insert(new Result($p.getName()));\n" +
                        "end\n";

        KieSession ksession = getKieSession(runType, str);

        Person first = new Person("First", 40);
        first.setEmployed(null);
        Person second = new Person("Second", 40);
        second.setEmployed(true);
        ksession.insert(first);
        ksession.insert(second);
        ksession.fireAllRules();

        List<Object> results = getObjectsIntoList(ksession, Result.class)
                .stream().map(Result::getValue).collect(Collectors.toList());
        assertThat(results.size()).isEqualTo(1);

        assertThat(results).containsExactlyInAnyOrder("Second");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testStringValueOf(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  Integer( $i : intValue )\n" +
                "  Person( name == (String.valueOf($i)) )\n" +
                "then\n" +
                "  insert(new Result($i));\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( 44 );
        ksession.insert( new Person( "44", 44 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(((Number) results.iterator().next().getValue()).intValue()).isEqualTo(44);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigDecimalBigIntegerCoercion(RUN_TYPE runType) {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + BigInteger.class.getCanonicalName() + ";\n" +
                "rule \"rule1\"\n" +
                "when\n" +
                "    Person( money == new BigInteger( \"1\" ) )\n" +
                "then\n" +
                "end\n" +
                "rule \"rule2\"\n" +
                "when\n" +
                "    Person( money == new BigInteger( \"2\" ) )\n" +
                "then\n" +
                "end\n" +
                "rule \"rule3\"\n" +
                "when\n" +
                "    Person( money == new BigInteger( \"3\" ) )\n" +
                "then\n" +
                "end\n";


        KieSession ksession1 = getKieSession(runType, str);

        Person p1 = new Person();
        p1.setMoney( new BigDecimal(1 ) );
        ksession1.insert( p1 );
        assertThat(ksession1.fireAllRules()).isEqualTo(1);

    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigDecimalOperationsInConstraint(RUN_TYPE runType) {
        String str = "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "rule \"rule1\"\n" +
                "when\n" +
                "    Person( $moneyDoubled : (money + money) )\n" +
                "then\n" +
                "    results.add($moneyDoubled);\n" +
                "end\n";

        KieSession ksession1 = getKieSession(runType, str);

        ArrayList<BigDecimal> results = new ArrayList<>();
        ksession1.setGlobal("results", results);

        Person p1 = new Person();
        p1.setMoney( new BigDecimal(1 ));
        ksession1.insert( p1 );
        assertThat(ksession1.fireAllRules()).isEqualTo(1);

        assertThat(results).containsExactly(BigDecimal.valueOf(2));

    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testSingleQuoteString(RUN_TYPE runType) {
        String str =
                "rule R1 when\n" +
                "  String( this == 'x' )\n" +
                "then\n" +
                "end\n" +
                "rule R2 when\n" +
                "  String( this == 'xx' )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( "x" );
        ksession.insert( "xx" );
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testIntToLongComparison(RUN_TYPE runType) {
        String str =
                "rule R when\n" +
                "    $i : Integer()\n" +
                "    $l : Long( this > $i )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( 1 );
        ksession.insert( 2L );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }


    @ParameterizedTest
    @MethodSource("parameters")
    public void testUseGlobalInLHS(RUN_TYPE runType) {
        // DROOLS-1025
        final String drl1 =
                "import " + Result.class.getCanonicalName() + ";\n" +
                        "global java.util.concurrent.atomic.AtomicInteger globalInt\n" +
                        "rule R1 when\n" +
                        "	 exists Integer() from globalInt.get()\n" +
                        "then\n" +
                        "  insert(new Result(\"match\"));\n" +
                        "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        ksession.setGlobal("globalInt", new AtomicInteger(0));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.iterator().next().getValue().toString()).isEqualTo("match");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMapAccess(RUN_TYPE runType) {
        final String drl1 =
                "import java.util.Map;\n" +
                "rule R1 when\n" +
                "	 Map(this['type'] == 'Goods' )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        final Map<String, Object> map = new HashMap<>();
        map.put("type", "Goods");

        ksession.insert( map );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMapAccessBindingConstant(RUN_TYPE runType) {
        final String drl1 =
                "import java.util.Map;\n" +
                "rule R1 when\n" +
                "	 Map($type: \"type\", this[$type] == 'Goods' )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        final Map<String, Object> map = new HashMap<>();
        map.put("type", "Goods");

        ksession.insert( map );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMapAccessBindingConstantJoin(RUN_TYPE runType) {
        final String drl1 =
                "import java.util.Map;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + List.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "rule R1 when\n" +
                "$p : Person($name: \"Andrea\", " +
                        "parentP.childrenMap[$name] != null," +
                        "parentP.childrenMap[$name].name != null )\n" +
                "then\n" +
                "  results.add($p.getName());\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        ArrayList<String> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Person luca = new Person("Luca", 37);
        luca.setParentP(luca); // avoid NPE
        Person andrea = new Person("Andrea", 0);
        andrea.setParentP(luca);

        luca.getChildrenMap().put("Andrea", andrea);

        ksession.insert( luca );
        ksession.insert( andrea );
        assertThat(ksession.fireAllRules()).isEqualTo(2);

        assertThat(results).containsExactlyInAnyOrder("Andrea", "Luca");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMapAccessBinding(RUN_TYPE runType) {
        final String drl1 =
                "import java.util.Map;\n" +
                        "rule R1 when\n" +
                        "    $s: String() \n" +
                        "	 Map(this[$s] == 'Goods' )\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        final Map<String, Object> map = new HashMap<>();
        map.put("type", "Goods");

        ksession.insert( map );
        ksession.insert("type");
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testStringBinding(RUN_TYPE runType) {
        final String drl1 =
                "import " + Result.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "	 String($t: \"type\")\n" +
                "then\n" +
                "  insert(new Result($t));\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        ksession.insert( "whatever" );
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertThat(results.iterator().next().getValue().toString()).isEqualTo("type");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMapAccessProperty(RUN_TYPE runType) {
        final String drl1 =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import java.util.Map;\n" +
                "rule R1 when\n" +
                "   Person( items[1] == 2000 )" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        final Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 2000);
        map.put(2, 2000);

        final Person luca = new Person("Luca");
        luca.setItems(map);
        ksession.insert(luca);

        final Person mario = new Person("Mario");
        ksession.insert(mario);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMapInitialization(RUN_TYPE runType) {
        final String drl1 =
                "import " + Person.class.getCanonicalName() + ";\n" +
                        "import java.util.Map;\n" +
                        "import static " + Person.class.getName() + ".countItems;\n" +
                        "rule R1 when\n" +
                        "   Person( numberOfItems == countItems([123 : 456, 789 : name]))" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        final Person luca = new Person("Luca");
        luca.setNumberOfItems(2);
        ksession.insert(luca);

        final Person mario = new Person("Mario");
        ksession.insert(mario);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }


    @ParameterizedTest
    @MethodSource("parameters")
    public void testErrorTwoPatterns(RUN_TYPE runType) {
        // DROOLS-3850
        final String drl1 =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import java.util.Map;\n" +
                "import static " + Person.class.getName() + ".countItems;\n" +
                "import static " + Person.class.getName() + ".evaluate;\n" +
                "rule R1 when\n" +
                "   Person( evaluate([123 : 456, 789 : name]), numberOfItems == countItems([123 : 456, 789 : name]))" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        final Person luca = new Person("Luca");
        luca.setNumberOfItems(2);
        ksession.insert(luca);

        final Person mario = new Person("Mario");
        ksession.insert(mario);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }


    @ParameterizedTest
    @MethodSource("parameters")
    public void testMapWithBinding(RUN_TYPE runType) {
        // DROOLS-3558
        final String drl1 = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Address.class.getCanonicalName() + ";\n" +
                "rule R1\n" +
                "    when\n" +
                "        $p : Person()\n" +
                "        $a : Address( number == $p.items[1] )\n" +
                "    then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        final Person john = new Person("John");
        HashMap<Integer, Integer> items = new HashMap<Integer, Integer>();
        items.put(1, 20);
        john.setItems(items);

        items.values().iterator().next();
        ksession.insert(john);

        final Address address = new Address("Tasman", 20, "Nelson");
        ksession.insert(address);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMapAccessPropertyWithCast(RUN_TYPE runType) {
        final String drl1 =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import java.util.Map;\n" +
                "rule R1 when\n" +
                "   Person( items[(Integer) 1] == 2000 )" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        final Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 2000);
        map.put(2, 2000);

        final Person luca = new Person("Luca");
        luca.setItems(map);
        ksession.insert(luca);

        final Person mario = new Person("Mario");
        ksession.insert(mario);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testArrayAccess(RUN_TYPE runType) {
        final String drl =
                "package org.drools.compiler.test\n" +
                        "import " + Person.class.getCanonicalName() + "\n" +
                        "global java.util.List list\n" +
                        "rule test1\n" +
                        "when\n" +
                        "   $p1  : Person($name : name )\n" +
                        "   Person(addresses[0].street == $p1.name)\n" +  // indexed
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(runType, drl);

        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testInOperators(RUN_TYPE runType) {
        final String drl1 = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"eval rewrite with 'in'\"\n" +
                "    when\n" +
                "        $p : Person( age in ( 1, (1 + 1) ))\n" +
                "    then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        final Person luca = new Person("Luca");
        luca.setAge(2);
        ksession.insert(luca);

        final Person mario = new Person("Mario");
        mario.setAge(12);
        ksession.insert(mario);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static class TestFact {
        private String aBcde;

        public TestFact(String aBcde) {
            this.aBcde = aBcde;
        }

        public String getaBcde() {
            return aBcde;
        }

        public void setaBcde(String aBcde) {
            this.aBcde = aBcde;
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testGetterSetterCase(RUN_TYPE runType) {
        // DROOLS-2724
        final String drl =
                "import " + TestFact.class.getCanonicalName() + ";\n" +
                "import java.util.List;\n" +
                "rule R1\n" +
                "when \n" +
                "   TestFact(aBcde == \"test\")\n" +
                "then end";
        KieSession kieSession = getKieSession(runType, drl);
        kieSession.insert(new TestFact("test"));
        assertThat(kieSession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testCommaInModify(RUN_TYPE runType) {
        // DROOLS-3505
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                "dialect \"java\"\n" +

                "rule R1 when\n" +
                "   $p : Person( name == \"John\" )\n" +
                "then\n" +
                "   modify($p) { setAge(1), setLikes(\"bread\"); }\n" +
                "end\n";
        KieSession kieSession = getKieSession(runType, drl);
        Person john = new Person("John", 24);
        kieSession.insert(john);
        assertThat(kieSession.fireAllRules()).isEqualTo(1);

        assertThat(john.getAge()).isEqualTo(1);
        assertThat(john.getLikes()).isEqualTo("bread");
    }

    public static class Message {
        public static final int HELLO = 0;
        public static final int GOODBYE = 1;

        private String message;

        private int status;

        public String getMessage() {
            return this.message;
        }

        public void setMessage( String message ) {
            this.message = message;
        }

        public int getStatus() {
            return this.status;
        }

        public void setStatus( int status ) {
            this.status = status;
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testStaticFieldClashingWithClassName(RUN_TYPE runType) {
        // DROOLS-3560
        final String drl =
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule \"Hello World\"\n" +
                "    when\n" +
                "        m : Message( status == Message.HELLO, myMessage : message ) \n" +
                "    then\n" +
                "        System.out.println( myMessage );\n" +
                "        m.setMessage( \"Goodbye cruel world\" ); \n" +
                "        m.setStatus( Message.GOODBYE ); \n" +
                "        update( m );\n" +
                "end\n" +
                "\n" +
                "rule \"GoodBye\"\n" +
                "    when\n" +
                "        Message( status == Message.GOODBYE, myMessage : message ) \n" +
                "    then\n" +
                "        System.out.println( myMessage );\n" +
                "end\n";

        KieSession kieSession = getKieSession(runType, drl);
        Message message = new Message();
        message.setMessage( "Hi" );
        message.setStatus( Message.HELLO );
        kieSession.insert(message);
        assertThat(kieSession.fireAllRules()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testDoubleModify(RUN_TYPE runType) {
        // DROOLS-3560
        final String drl =
                "import " + Message.class.getCanonicalName() + "\n" +
                "rule \"Hello World\"\n" +
                "    when\n" +
                "        m : Message( status == Message.HELLO, myMessage : message ) \n" +
                "    then\n" +
                "        System.out.println( myMessage );\n" +
                "        m.setMessage( \"Goodbye cruel world\" );" +
                "        update(m); \n" +
                "        m.setStatus( Message.GOODBYE ); \n" +
                "        update( m );\n" +
                "end\n" +
                "\n" +
                "rule \"GoodBye\"\n" +
                "    when\n" +
                "        Message( status == Message.GOODBYE, myMessage : message ) \n" +
                "    then\n" +
                "        System.out.println( myMessage );\n" +
                "end\n";

        KieSession kieSession = getKieSession(runType, drl);
        Message message = new Message();
        message.setMessage( "Hi" );
        message.setStatus( Message.HELLO );
        kieSession.insert(message);
        assertThat(kieSession.fireAllRules()).isEqualTo(2);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testPrettyPrinterCrashing(RUN_TYPE runType) {
        final String drl = "" +
                "package org.drools.compiler.test  \n" +

                "import java.util.List\n" +
                "import java.util.ArrayList\n" +
                "import " + Person.class.getCanonicalName() + "\n" +

                "global List list\n" +

                "dialect \"mvel\"\n" +

                "declare Location\n" +
                "    thing : String \n" +
                "    location : String \n" +
                "end" +
                "\n" +
                "query isContainedIn( String x, String y ) \n" +
                "    Location(x, y;)\n" +
                "    or \n" +
                "    ( Location(z, y;) and isContainedIn(x, z;) )\n" +
                "end\n" +
                "\n" +
                "rule look when \n" +
                "    Person( $l : likes ) \n" +
                "    isContainedIn( $l, 'office'; )\n" +
                "then\n" +
                "   insertLogical( 'blah' );" +
                "end\n" +
                "\n" +
                "rule go1 when \n" +
                "    String( this == 'go1') \n" +
                "then\n" +
                "        list.add( drools.getRule().getName() ); \n" +
                "        insert( new Location('lamp', 'desk') );\n" +
                "end\n" +
                "\n";

        KieSession ksession = getKieSession(runType, drl);
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            final Person p = new Person();
            p.setLikes("lamp");
            ksession.insert(p);
            ksession.fireAllRules();

        } finally {
            ksession.dispose();
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBetaJoinBigInteger(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $r : Result()\n" +
                        "  $markV : Person(name == \"Mark\", $markAgeInSeconds : ageInSeconds)\n" +
                        "  $olderV : Person(name != \"Mark\", ageInSeconds > $markAgeInSeconds)\n" +
                        "then\n" +
                        "  $r.setValue($olderV.getName() + \" is older than \" + $markV.getName());\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );

        Person mark = new Person("Mark", 37).setAgeInSeconds(BigInteger.valueOf(12341234));
        Person edson = new Person("Edson", 35).setAgeInSeconds(BigInteger.valueOf(1234));
        Person mario = new Person("Mario", 40).setAgeInSeconds(BigInteger.valueOf(123412341234L));

        ksession.insert(mark);
        ksession.insert(edson);
        ksession.insert(mario);

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Mario is older than Mark");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBetaJoinBigDecimal(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $r : Result()\n" +
                        "  $markV : Person(name == \"Mark\", $markMoney : money)\n" +
                        "  $richerV : Person(name != \"Mark\", money > $markMoney)\n" +
                        "then\n" +
                        "  $r.setValue($richerV.getName() + \" is richer than \" + $markV.getName());\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );

        Person mark = new Person("Mark", 37).setMoney(BigDecimal.valueOf(1_000_000));
        Person edson = new Person("Edson", 35).setMoney(BigDecimal.valueOf(1_000));
        Person mario = new Person("Mario", 40).setMoney(BigDecimal.valueOf(1_000_000_000));

        ksession.insert(mark);
        ksession.insert(edson);
        ksession.insert(mario);

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Mario is richer than Mark");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBetaCast(RUN_TYPE runType) {
        String str =
                    "import " + Result.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Address.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $r : Result()\n" +
                        "  $p : Person($intField : age)\n" +
                        "  $a : Address(shortNumber == (short)$intField)\n" +
                        "then\n" +
                        "  $r.setValue($a.getCity() + \" number has the same value of \" + $p.getName() + \" age\");\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        Address a = new Address("Milan");
        a.setShortNumber((short)37);

        Address b = new Address("Rome");
        b.setShortNumber((short)1);

        ksession.insert(mark);
        ksession.insert(edson);
        ksession.insert(mario);

        ksession.insert(a);
        ksession.insert(b);

        ksession.fireAllRules();
        assertThat(result.getValue()).isEqualTo("Milan number has the same value of Mark age");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testNumericLimitsLiteral(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $r : Result()\n" +
                        "  $p : Person(ageLong > 9223372036854775806L)\n" + // MAX_LONG - 1
                        "then\n" +
                        "  $r.setValue($p.getName() + \" is very old\");\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );

        Person mark = new Person("Mark").setAgeLong(37);
        Person edson = new Person("Edson").setAgeLong(35);
        Person mario = new Person("Mario").setAgeLong(Long.MAX_VALUE);

        ksession.insert(mark);
        ksession.insert(edson);
        ksession.insert(mario);

        ksession.fireAllRules();

        assertThat(result.getValue()).isEqualTo("Mario is very old");

    }



    @ParameterizedTest
    @MethodSource("parameters")
    public void testBetaCastGreaterThan(RUN_TYPE runType) {
        String str =
                    "import " + Result.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Address.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $r : Result()\n" +
                        "  $p : Person($intField : age)\n" +
                        "  $a : Address(shortNumber > (short)$intField)\n" +
                        "then\n" +
                            "  $r.setValue($a.getCity() + \" number is greater than \" + $p.getName() + \" age\");\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        Address a = new Address("Milan");
        a.setShortNumber((short)37);

        Address b = new Address("Naples");
        b.setShortNumber((short)1);

        Address c = new Address("Rome");
        c.setShortNumber((short)38);

        ksession.insert(mark);
        ksession.insert(edson);
        ksession.insert(mario);

        ksession.insert(a);
        ksession.insert(b);
        ksession.insert(c);

        ksession.fireAllRules();

        assertThat(result.getValue()).isEqualTo("Rome number is greater than Mark age");

    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testNumericLimits(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $r : Result()\n" +
                        "  $p : Person(ageLong > 9223372036854775806)\n" + // MAX_LONG - 1
                        "then\n" +
                        "  $r.setValue($p.getName() + \" is very old\");\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );

        Person mark = new Person("Mark").setAgeLong(37);
        Person edson = new Person("Edson").setAgeLong(35);
        Person mario = new Person("Mario").setAgeLong(Long.MAX_VALUE);

        ksession.insert(mark);
        ksession.insert(edson);
        ksession.insert(mario);

        ksession.fireAllRules();

        assertThat(result.getValue()).isEqualTo("Mario is very old");

    }

    @DisabledIfSystemProperty(named = "drools.drl.antlr4.parser.enabled", matches = "true")
    @ParameterizedTest
    @MethodSource("parameters")
    public void testMapAbbreviatedComparison(RUN_TYPE runType) {
        final String drl1 =
                "import java.util.Map;\n" +
                "rule R1 when\n" +
                "    Map(this['money'] >= 65 && <= 75)\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        final Map<String, Object> map = new HashMap<>();
        map.put("money", new BigDecimal(70));

        ksession.insert( map );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMapPrimitiveComparison(RUN_TYPE runType) {
        final String drl1 =
                "import java.util.Map;\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R1 when\n" +
                "    $m : Map()\n" +
                "    Person(age == $m['age'] )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        final Map<String, Object> map = new HashMap<>();
        map.put("age", 20);
        Person john = new Person("John", 20);

        ksession.insert( map );
        ksession.insert( john );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    public static final int CONSTANT = 1;

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMapCheckForExistence(RUN_TYPE runType) {
        final String drl1 =
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "    $p : Person(getItems().get( " + CompilerTest.class.getCanonicalName() + ".CONSTANT) == null )\n" +
                "then\n" +
                "  insert(new Result($p.getName()));\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, drl1);

        final Map<Integer, Integer> items = new HashMap<>();
        items.put(CONSTANT, 2000);

        final Person luca = new Person("Luca");
        luca.setItems(items);

        final Person mario = new Person("Mario");

        ksession.insert( luca );
        ksession.insert( mario );
        assertThat(ksession.fireAllRules()).isEqualTo(1);

        Collection<Result> results = getObjectsIntoList(ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Mario");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBigDecimalIntCoercion(RUN_TYPE runType) {
        String str = "import " + Result.class.getCanonicalName() + ";\n" +
                "rule \"rule1\"\n" +
                "when\n" +
                "    $r : Result( value <= 20 )\n" +
                "then\n" +
                "end\n";

        KieSession ksession1 = getKieSession(runType, str);

        Result fact = new Result();
        fact.setValue( new BigDecimal(10) );
        ksession1.insert( fact );
        assertThat(ksession1.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testBooleanCoercion(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "import " + Result.class.getCanonicalName() + ";" +
                        "rule R1 when\n" +
                        "  $p : Person(employed == \"true\")\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(runType, str);

        Person first = new Person("First", 40);
        first.setEmployed(true);
        ksession.insert(first);
        assertThat(ksession.fireAllRules()).isEqualTo(1);;
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testUseMatch(RUN_TYPE runType) {
        // DROOLS-4579
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "    $p: Person()\n" +
                "then\n" +
                "    if ($p != drools.getMatch().getObjects().get(0)) throw new RuntimeException();\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person me = new Person( "Mario", 40 );
        ksession.insert( me );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMultilinePattern(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule R1 when\n" +
                        "  $p : Person(age == 30\n" +
                        "    || employed == true)\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(runType, str);

        Person first = new Person("John", 40);
        first.setEmployed(true);
        ksession.insert(first);
        assertThat(ksession.fireAllRules()).isEqualTo(1);;
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testAccumulateWithMax(RUN_TYPE runType) {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                        "import " + StockTick.class.getCanonicalName() + ";" +
                        "rule AccumulateMaxDate when\n" +
                        "  $max1 : Number() from accumulate(\n" +
                        "    StockTick($time : getTimeFieldAsDate());\n" +
                        "    max($time.getTime()))\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(runType, str);

        StockTick st = new StockTick("RHT");
        st.setTimeField(new Date().getTime());
        ksession.insert(st);
        assertThat(ksession.fireAllRules()).isEqualTo(1);;
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMultipleModify(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p1 : Person(name == \"John\")\n" +
                "  $p2 : Person(name == \"Paul\")\n" +
                "then\n" +
                "  modify($p1) { setAge($p1.getAge()+1) }\n" +
                "  modify($p2) { setAge($p2.getAge()+5) }\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person p1 = new Person( "John", 40 );
        Person p2 = new Person( "Paul", 38 );

        ksession.insert( p1 );
        ksession.insert( p2 );
        ksession.fireAllRules();

        assertThat(p1.getAge()).isEqualTo(41);
        assertThat(p2.getAge()).isEqualTo(43);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMultipleModifyWithDifferentFacts(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Pet.class.getCanonicalName() + ";" +
                "rule R1 when\n" +
                "  $person : Person(name == \"John\")\n" +
                "  $pet : Pet(owner == $person)\n" +
                "then\n" +
                "  modify($person) { setName(\"George\") }\n" +
                "  modify($pet) { setAge($pet.getAge()+1) }\n" +
                "end\n" +
                "rule R2 when\n" +
                "  $person : Person(name == \"George\")\n" +
                "  $pet : Pet(owner == $person)\n" +
                "then\n" +
                "  modify($pet) { setAge($pet.getAge()+1) }\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person person = new Person( "John", 40 );
        Pet pet = new Pet( Pet.PetType.dog, 3 );
        pet.setOwner(person);

        ksession.insert( person );
        ksession.insert( pet );
        ksession.fireAllRules();

        assertThat(person.getName()).isEqualTo("George");
        assertThat(pet.getAge()).isEqualTo(5);
    }

    public static class IntegerToShort {

        private Boolean testBoolean;
        private int testInt;
        private Short testShort;
        private double testDouble;

        public IntegerToShort(Boolean testBoolean, int testInt, Short testShort, Double testDouble) {
            this.testBoolean = testBoolean;
            this.testInt = testInt;
            this.testShort = testShort;
            this.testDouble = testDouble;
        }

        public IntegerToShort(Boolean testBoolean, int testInt, Short testShort) {
            this.testBoolean = testBoolean;
            this.testInt = testInt;
            this.testShort = testShort;
            this.testDouble = 0d;
        }

        public void setTestBoolean(Boolean testBoolean) {
            this.testBoolean = testBoolean;
        }

        public void setTestInt(int testInt) {
            this.testInt = testInt;
        }

        public void setTestShort(Short testShort) {
            this.testShort = testShort;
        }

        public Boolean getTestBoolean() {
            return testBoolean;
        }

        public int getTestInt() {
            return testInt;
        }

        public Short getTestShort() {
            return testShort;
        }

        public Double getTestDouble() {
            return testDouble;
        }

        public void setTestDouble(Double testShort) {
            this.testDouble = testDouble;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            IntegerToShort that = (IntegerToShort) o;
            return testInt == that.testInt && Objects.equals(testBoolean, that.testBoolean) && Objects.equals(testShort, that.testShort) && Objects.equals(testDouble, that.testDouble);
        }

        @Override
        public int hashCode() {
            return Objects.hash(testBoolean, testInt, testShort, testDouble);
        }

        @Override
        public String toString() {
            return "IntegerToShort{" +
                    "testBoolean=" + testBoolean +
                    ", testInt=" + testInt +
                    ", testShort=" + testShort +
                    ", testDouble=" + testDouble +
                    '}';
        }
    }

    public static class GlobalFunctions {
        public Object getObject() {
            return new IntegerToShort(true, 1, (short)0);
        }

        public String getValue(Object o) {
            if(o instanceof IntegerToShort) {
                return "" + ((IntegerToShort)o).getTestInt();
            }
            return "0";
        }
    }

    // DROOLS-5007
    @ParameterizedTest
    @MethodSource("parameters")
    public void testIntToShortCast(RUN_TYPE runType) {
        String str = "import " + Address.class.getCanonicalName() + ";\n" +
                "rule \"rule1\"\n" +
                "when\n" +
                "    $address : Address( shortNumber == null ||  shortNumber == 0, \n" +
                "                           $interimVar : number)\n" +
                "then\n" +
                "    $address.setShortNumber((short)$interimVar);\n" +
                "    update($address);\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, str);

        Address address = new Address();
        address.setNumber(1);
        ksession.insert( address );
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }


    // DROOLS-5709 // DROOLS-5768
    @ParameterizedTest
    @MethodSource("parameters")
    public void testCastingIntegerToShort(RUN_TYPE runType) {
        String str =
                "import " + IntegerToShort.class.getCanonicalName() + ";\n " +
                        "rule \"test_rule\"\n" +
                        "dialect \"java\"\n" +
                        "when\n" +
                        "   $integerToShort : IntegerToShort( " +
                        "           $testInt : testInt, " +
                        "           testBoolean != null, " +
                        "           testBoolean == false" +
                        ") \n" +
                        "then\n" +
                        "   $integerToShort.setTestShort((short)(12)); \n" +
                        "   $integerToShort.setTestShort((short)($testInt)); \n" +
                        "   $integerToShort.setTestBoolean(true);\n" +
                        "   update($integerToShort);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        IntegerToShort integerToShort = new IntegerToShort(false, Short.MAX_VALUE, (short)0, (double)0);

        ksession.insert(integerToShort);
        int rulesFired = ksession.fireAllRules();

        assertThat(rulesFired).isEqualTo(1);
        assertThat(integerToShort).isEqualTo(new IntegerToShort(true, Short.MAX_VALUE, Short.MAX_VALUE, (double)0));
    }

    // DROOLS-5998
    @ParameterizedTest
    @MethodSource("parameters")
    public void testCastingIntegerToShortWithNegativeNumbers(RUN_TYPE runType) {
        String str =
                "import " + IntegerToShort.class.getCanonicalName() + ";\n " +
                        "rule \"test_rule\"\n" +
                        "dialect \"java\"\n" +
                        "when\n" +
                        "   $integerToShort : IntegerToShort( " +
                        "           $testInt : testInt, " +
                        "           testBoolean != null, " +
                        "           testBoolean == false" +
                        ") \n" +
                        "then\n" +
                        "   $integerToShort.setTestShort((short)(-12)); \n" +
                        "   $integerToShort.setTestBoolean(true);\n" +
                        "   update($integerToShort);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        IntegerToShort integerToShort = new IntegerToShort(false, Short.MAX_VALUE, (short)0, (double)0);

        ksession.insert(integerToShort);
        int rulesFired = ksession.fireAllRules();

        assertThat(rulesFired).isEqualTo(1);
        assertThat(integerToShort).isEqualTo(new IntegerToShort(true, Short.MAX_VALUE, (short)-12, (double)0));
    }

    // RHDM-1644 // DROOLS-6196
    @ParameterizedTest
    @MethodSource("parameters")
    public void testCastingIntegerToShortWithDoubleVar(RUN_TYPE runType) {
        String str =
                "import " + IntegerToShort.class.getCanonicalName() + ";\n " +
                        "rule \"test_rule\"\n" +
                        "dialect \"java\"\n" +
                        "when\n" +
                        "   $integerToShort : IntegerToShort( " +
                        "           $testDouble : testDouble, " +
                        "           $testInt : testInt, " +
                        "           testBoolean != null, " +
                        "           testBoolean == false" +
                        ") \n" +
                        "then\n" +
                        "   $integerToShort.setTestShort((short)((16 + $testDouble))); \n" +
                        "   $integerToShort.setTestBoolean(true);\n" +
                        "   update($integerToShort);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        IntegerToShort integerToShort = new IntegerToShort(false, Short.MAX_VALUE, (short)0, (double)1);

        ksession.insert(integerToShort);
        int rulesFired = ksession.fireAllRules();

        assertThat(rulesFired).isEqualTo(1);
        assertThat(integerToShort).isEqualTo(new IntegerToShort(true, Short.MAX_VALUE, (short)17, (double)1));
    }

    // RHDM-1644 // DROOLS-6196
    @ParameterizedTest
    @MethodSource("parameters")
    public void testUseOfVarCreatedAsInputArgInGlobalFuntionAsA_Var(RUN_TYPE runType) {
        String str =
                "import " + IntegerToShort.class.getCanonicalName() + ";\n " +
                "global " + GlobalFunctions.class.getCanonicalName() + " functions;\n " +
                        "rule \"test_rule\"\n" +
                        "dialect \"java\"\n" +
                        "when\n" +
                        "   $integerToShort : IntegerToShort( " +
                        "           $testInt : testInt, " +
                        "           testBoolean != null, " +
                        "           testBoolean == false" +
                        ") \n" +
                        "then\n" +
                        "   Object co = functions.getObject(); \n" +
                        "   $integerToShort.setTestInt((int)Integer.valueOf(functions.getValue(co)));\n" +
                        "   $integerToShort.setTestBoolean(true);\n" +
                        "   update($integerToShort);\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);
        IntegerToShort integerToShort = new IntegerToShort(false, Short.MAX_VALUE, (short)0);

        ksession.insert(integerToShort);
        ksession.setGlobal("functions", new GlobalFunctions());
        int rulesFired = ksession.fireAllRules();

        assertThat(rulesFired).isEqualTo(1);
        assertThat(integerToShort).isEqualTo(new IntegerToShort(true, 1, (short)0));
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testConsequenceGetContext(RUN_TYPE runType) throws Exception {
        String str =
                "import " + ProcessContext.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Object()\n" +
                "then\n" +
                "  ProcessContext clazz = drools.getContext(ProcessContext.class);\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);
        assertThat(ksession).isNotNull();
    }

    // DROOLS-6034
    @ParameterizedTest
    @MethodSource("parameters")
    public void testConsequenceInsertThenUpdate(RUN_TYPE runType) throws Exception {
        String str =
                "global java.util.List children;\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule \"a new baby is born\" when\n" +
                        "  " + // No pattern
                        "then\n" +
                        "  Person andrea = new Person();\n" +
                        "  insert(andrea);\n" +
                        "  andrea.setName(\"Andrea\");\n" +
                        "  update(andrea);\n" +
                        "  children.add(andrea.getName());\n" +
                        "end";

        KieSession kSession = getKieSession(runType, str);

        ArrayList<String> children = new ArrayList<>();
        kSession.setGlobal("children", children);

        Person luca = new Person( "Luca", 36 );

        kSession.insert( luca );
        assertThat(kSession.fireAllRules()).isEqualTo(1);

        assertThat(children).containsOnly("Andrea");
    }

    // DROOLS-6034
    @ParameterizedTest
    @MethodSource("parameters")
    public void testConsequenceInsertThenModify(RUN_TYPE runType) throws Exception {
        String str =
                "global java.util.List children;\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "dialect \"mvel\"" +
                        "rule \"a new baby is born\" when\n" +
                        "  " + // No pattern
                        "then\n" +
                        "  Person andrea = new Person();\n" +
                        "  insert(andrea);\n" +
                        "  modify(andrea) {" +
                        "       name = \"Andrea\" " +
                        "  }" +
                        "  children.add(andrea.getName());\n" +
                        "end";

        KieSession kSession = getKieSession(runType, str);

        ArrayList<String> children = new ArrayList<>();
        kSession.setGlobal("children", children);

        Person luca = new Person( "Luca", 36 );

        kSession.insert( luca );
        assertThat(kSession.fireAllRules()).isEqualTo(1);

        assertThat(children).containsOnly("Andrea");
    }

    // DROOLS-6034
    @ParameterizedTest
    @MethodSource("parameters")
    public void testConsequenceInsertThenUpdateWithPatternInitializer(RUN_TYPE runType) throws Exception {
        String str =
                "global java.util.List result;\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import static " + Person.class.getName() + ".identityFunction;\n" +
                "no-loop true\n" +
                "rule R1 when\n" +
                "  $p : Person($name : name)\n" +
                "then\n" +
                "  Person fromMethodCall = identityFunction($p);" +
                "  Person fromNameExpr = fromMethodCall;" +
                "  Person fromNameExprTwice = fromNameExpr;\n" +
                "  insert(fromNameExprTwice);\n" +
                "  update(fromNameExprTwice);\n" +
                "  result.add(fromNameExprTwice.getName());\n" +
                "end";

        KieSession kSession = getKieSession(runType, str);

        ArrayList<String> children = new ArrayList<>();
        kSession.setGlobal("result", children);

        Person luca = new Person( "Luca", 36 );

        kSession.insert( luca );
        assertThat(kSession.fireAllRules()).isEqualTo(1);

        assertThat(children).containsOnly("Luca");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testExtraParenthes(RUN_TYPE runType) throws Exception {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person((age > 30))\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Person person = new Person( "John", 20 );

        ksession.insert( person );
        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testNegateBigDecimal(RUN_TYPE runType) throws Exception {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "global java.util.List list;\n" +
                     "rule R when\n" +
                     "  $p : Person(!(money > 20))\n" +
                     "then\n" +
                     "  list.add($p.getName());" +
                     "end";

        KieSession ksession = getKieSession(runType, str);
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Person p1 = new Person("John");
        p1.setMoney(new BigDecimal("10.0"));
        Person p2 = new Person("Paul");
        p2.setMoney(new BigDecimal("30.0"));

        ksession.insert(p1);
        ksession.insert(p2);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
        assertThat(list).containsExactly("John");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testNegateJoin(RUN_TYPE runType) throws Exception {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Address.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $a : Address()\n" +
                     "  $p : Person(!(address == $a))\n" +
                     "then\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        Address a = new Address("Milan");
        Person p = new Person("Toshiya");
        p.setAddress(new Address("Tokyo"));

        ksession.insert(a);
        ksession.insert(p);

        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testNegateComplex(RUN_TYPE runType) throws Exception {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "global java.util.List list;\n" +
                     "rule R when\n" +
                     "  $p : Person(!(money > 20 && money < 40))\n" +
                     "then\n" +
                     "  list.add($p.getName());" +
                     "end";

        KieSession ksession = getKieSession(runType, str);
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Person p1 = new Person("John");
        p1.setMoney(new BigDecimal("10.0"));
        Person p2 = new Person("Paul");
        p2.setMoney(new BigDecimal("30.0"));
        Person p3 = new Person("George");
        p3.setMoney(new BigDecimal("50.0"));

        ksession.insert(p1);
        ksession.insert(p2);
        ksession.insert(p3);

        assertThat(ksession.fireAllRules()).isEqualTo(2);
        assertThat(list).containsExactlyInAnyOrder("John", "George");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMapStringProp(RUN_TYPE runType) throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Person.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           "  Person(\"XXX\" == itemsString[\"AAA\"])\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(runType, str);

        final Person p = new Person("Toshiya");
        p.getItemsString().put("AAA", "XXX");

        ksession.insert(p);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMapString(RUN_TYPE runType) throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + Map.class.getCanonicalName() + ";\n" +
                           "rule R1 when \n" +
                           "  Map(\"XXX\" == this[\"AAA\"])\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(runType, str);

        Map<String, String> map = new HashMap<>();
        map.put("AAA", "XXX");

        ksession.insert(map);
        assertThat(ksession.fireAllRules()).isEqualTo(1);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testHashSet(RUN_TYPE runType) throws Exception {
        final String str =
                "package org.drools.test;\n" +
                           "import " + HashSet.class.getCanonicalName() + ";\n" +
                           "import " + Set.class.getCanonicalName() + ";\n" +
                           "declare Application\n" +
                           "    categories : Set = new HashSet()" +
                           "end\n" +
                           "rule R1\n" +
                           "no-loop true\n" +
                           "when \n" +
                           "  $a : Application()\n" +
                           "then\n" +
                           "  modify ($a) { getCategories().add(\"hello\") };\n" +
                           "end\n" +
                           "rule R2\n" +
                           "when \n" +
                           "  $a : Application(categories contains \"hello\")\n" +
                           "then\n" +
                           "end";

        final KieSession ksession = getKieSession(runType, str);

        FactType appType = ksession.getKieBase().getFactType("org.drools.test", "Application");
        Object appObj = appType.newInstance();
        Set<String> categories = new HashSet<>();
        appType.set(appObj, "categories", categories);

        ksession.insert(appObj);
        assertThat(ksession.fireAllRules()).isEqualTo(2);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testRhsOrderWithModify(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  $p1 : Person(name == \"John\")\n" +
                "  $p2 : Person(name == \"Paul\")\n" +
                "then\n" +
                "  list.add($p1.getAge());\n" +
                "  list.add($p2.getAge());\n" +
                "  modify($p1) { setAge($p1.getAge()+1) }\n" +
                "  list.add($p1.getAge());\n" +
                "  list.add($p2.getAge());\n" +
                "  modify($p2) { setAge($p2.getAge()+5) }\n" +
                "  list.add($p1.getAge());\n" +
                "  list.add($p2.getAge());\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);
        final List<Integer> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Person p1 = new Person( "John", 40 );
        Person p2 = new Person( "Paul", 38 );

        ksession.insert( p1 );
        ksession.insert( p2 );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder(40, 38, 41, 38, 41, 43);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testStringRelationalComparison(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.util.List list;\n" +
                "rule R when\n" +
                "  $p : Person(name > \"Bob\" && name < \"Ken\")\n" +
                "then\n" +
                "  list.add($p.getName());" +
                "end";

        KieSession ksession = getKieSession(runType, str);
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert(new Person("John"));
        ksession.insert(new Person("Ann"));
        ksession.fireAllRules();

        assertThat(list).containsExactly("John");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testNPEOnConstraint(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $p : Person(money < salary * 20 )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Person me = new Person( "Luca");
        me.setMoney(null);
        ksession.insert( me );
        assertThatExceptionOfType(RuntimeException.class)
    		.isThrownBy(() -> ksession.fireAllRules())
    		.withMessage("Error evaluating constraint 'money < salary * 20' in [Rule \"R\" in r0.drl]");    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testSharedPredicateInformation(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person(money < salary * 20 )\n" +
                     "then\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(money < salary * 20 )\n" +
                     "then\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        Person me = new Person("Luca");
        me.setSalary(null);
        me.setMoney(null);
        ksession.insert(me);
        
        assertThatExceptionOfType(RuntimeException.class)
        	.isThrownBy(() -> ksession.fireAllRules())
        	.withMessage("Error evaluating constraint 'money < salary * 20' in [Rule \"R1\", \"R2\" in r0.drl]");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testSharedPredicateInformationWithNonSharedRule(RUN_TYPE runType) {

        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person(money < salary * 20 )\n" +
                     "then\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person()\n" +
                     "then\n" +
                     "end\n" +
                     "rule R3 when\n" +
                     "  $p : Person(money < salary * 20 )\n" +
                     "then\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        Person me = new Person("Luca");
        me.setSalary(null);
        me.setMoney(null);
        ksession.insert(me);
        
        assertThatExceptionOfType(RuntimeException.class)
    	.isThrownBy(() -> ksession.fireAllRules())
    	.withMessage("Error evaluating constraint 'money < salary * 20' in [Rule \"R1\", \"R3\" in r0.drl]");
    	
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testSharedPredicateInformationWithMultipleFiles(RUN_TYPE runType) {

        String str1 =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $p : Person(money < salary * 20 )\n" +
                     "then\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $p : Person(money < salary * 20 )\n" +
                     "then\n" +
                     "end";
        String str2 =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R3 when\n" +
                     "  $p : Person(money < salary * 20 )\n" +
                     "then\n" +
                     "end\n" +
                     "rule R4 when\n" +
                     "  $p : Person(money < salary * 20 )\n" +
                     "then\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str1, str2);

        Person me = new Person("Luca");
        me.setSalary(null);
        me.setMoney(null);
        ksession.insert(me);
        
        assertThatExceptionOfType(RuntimeException.class)
    		.isThrownBy(() -> ksession.fireAllRules())
    		.withMessage("Error evaluating constraint 'money < salary * 20' in [Rule \"R1\", \"R2\" in r0.drl] [Rule \"R3\", \"R4\" in r1.drl]");   
        
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testSharedBetaPredicateInformationWithMultipleFiles(RUN_TYPE runType) {
        String str1 =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R1 when\n" +
                     "  $i : Integer()\n" +
                     "  $p : Person($i < salary * 20 )\n" +
                     "then\n" +
                     "end\n" +
                     "rule R2 when\n" +
                     "  $i : Integer()\n" +
                     "  $p : Person($i < salary * 20 )\n" +
                     "then\n" +
                     "end";
        String str2 =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R3 when\n" +
                     "  $i : Integer()\n" +
                     "  $p : Person($i < salary * 20 )\n" +
                     "then\n" +
                     "end\n" +
                     "rule R4 when\n" +
                     "  $i : Integer()\n" +
                     "  $p : Person($i < salary * 20 )\n" +
                     "then\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str1, str2);

        Person me = new Person("Luca");
        me.setSalary(null);
        me.setMoney(null);
        ksession.insert(Integer.valueOf(10));
        ksession.insert(me);

        assertThatExceptionOfType(RuntimeException.class)
    		.isThrownBy(() -> ksession.fireAllRules())
    		.withMessage("Error evaluating constraint '$i < salary * 20' in [Rule \"R1\", \"R2\" in r0.drl] [Rule \"R3\", \"R4\" in r1.drl]");   
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testSharedPredicateInformationExceedMaxRuleDefs(RUN_TYPE runType) {
        // shared by 11 rules
        String str1 =
                "import " + Person.class.getCanonicalName() + ";" +
                      "rule R1 when\n" +
                      "  $i : Integer()\n" +
                      "  $p : Person($i < salary * 20 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule R2 when\n" +
                      "  $i : Integer()\n" +
                      "  $p : Person($i < salary * 20 )\n" +
                      "then\n" +
                      "end";
        String str2 =
                "import " + Person.class.getCanonicalName() + ";" +
                      "rule R3 when\n" +
                      "  $i : Integer()\n" +
                      "  $p : Person($i < salary * 20 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule R4 when\n" +
                      "  $i : Integer()\n" +
                      "  $p : Person($i < salary * 20 )\n" +
                      "then\n" +
                      "end";
        String str3 =
                "import " + Person.class.getCanonicalName() + ";" +
                      "rule R5 when\n" +
                      "  $i : Integer()\n" +
                      "  $p : Person($i < salary * 20 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule R6 when\n" +
                      "  $i : Integer()\n" +
                      "  $p : Person($i < salary * 20 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule R7 when\n" +
                      "  $i : Integer()\n" +
                      "  $p : Person($i < salary * 20 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule R8 when\n" +
                      "  $i : Integer()\n" +
                      "  $p : Person($i < salary * 20 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule R9 when\n" +
                      "  $i : Integer()\n" +
                      "  $p : Person($i < salary * 20 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule R10 when\n" +
                      "  $i : Integer()\n" +
                      "  $p : Person($i < salary * 20 )\n" +
                      "then\n" +
                      "end\n" +
                      "rule R11 when\n" +
                      "  $i : Integer()\n" +
                      "  $p : Person($i < salary * 20 )\n" +
                      "then\n" +
                      "end";

        KieSession ksession = getKieSession(runType, str1, str2, str3);

        Person me = new Person("Luca");
        me.setSalary(null);
        me.setMoney(null);
        ksession.insert(Integer.valueOf(10));
        ksession.insert(me);
        
        assertThatExceptionOfType(RuntimeException.class)
    		.isThrownBy(() -> ksession.fireAllRules())
    		.withMessageContaining("Error evaluating constraint '$i < salary * 20' in ")
    		.withMessageContaining(" and in more rules");       
        
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testWithQuotedStringConcatenationOnConstraint(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "  $p : Person(name == \"Luca\" + \" II\"  )\n" +
                        "then\n" +
                        "end";

        KieSession ksession = getKieSession(runType, str);

        Person me = new Person( "Luca II");
        me.setMoney(null);
        ksession.insert( me );
        int rulesFired = ksession.fireAllRules();
        assertThat(rulesFired).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testNegatedConstraint(RUN_TYPE runType) {
        // DROOLS-5791
        String str =
                "rule R when\n" +
                "  $i : Integer()\n" +
                "  String( !($i.intValue > length) )\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert( 5 );
        ksession.insert( "test" );
        assertThat(ksession.fireAllRules()).isEqualTo(0);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testMethodCallWithClass(RUN_TYPE runType) {
        final String str = "package org.drools.mvel.compiler\n" +
                "import " + FactWithMethod.class.getCanonicalName() + ";" +
                "rule r1\n" +
                "when\n" +
                "    FactWithMethod( checkClass( java.lang.String.class ) )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession(runType, str);
        final FactWithMethod fact = new FactWithMethod();
        ksession.insert(fact);
        final int rules = ksession.fireAllRules();
        assertThat(rules).isEqualTo(1);
    }

    public static class FactWithMethod {

        public FactWithMethod() {}

        public boolean checkClass(Class<?> clazz) {
            return clazz.equals(String.class);
        }
    }

    public interface MyInterface {
        default String getDefaultString() {
            return "DEFAULT";
        }
    }

    public static class MyClass implements MyInterface { }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testUseDefaultMethod(RUN_TYPE runType) {
        // DROOLS-6358
        final String str =
                "package org.drools.mvel.compiler\n" +
                "global java.util.List list;\n" +
                "import " + MyClass.class.getCanonicalName() + ";" +
                "rule r1\n" +
                "when\n" +
                "    MyClass( val: defaultString )\n" +
                "then\n" +
                "    list.add(val);" +
                "end\n";

        KieSession ksession = getKieSession(runType, str);
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final FactWithMethod fact = new FactWithMethod();
        ksession.insert(new MyClass());
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("DEFAULT");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void testSharedConstraintWithExtraParenthesis(RUN_TYPE runType) {
        // DROOLS-6548
        final String str =
                "package org.drools.mvel.compiler\n" +
                "global java.util.List list;\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule r1 when\n" +
                "    Person( ( name == \"A\" ) )\n" +
                "then\n" +
                "    list.add(\"r1\");" +
                "end\n" +
                "rule r2 when\n" +
                "    Person( name == \"B\" )\n" +
                "    Person( name == \"A\" )\n" +
                "then\n" +
                "    list.add(\"r2\");" +
                "end\n";

        KieSession ksession = getKieSession(runType, str);
        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert(new Person("A"));
        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("r1");
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void orWithMethodCall(RUN_TYPE runType) {
        final String str =
                "package org.example\n" +
                        "import " + MyFact.class.getCanonicalName() + ";" +
                        "rule r1 when\n" +
                        "    MyFact( value == 10 || someMethod() == 4 )\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new MyFact(5));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void orWithMethodCallWithArg(RUN_TYPE runType) {
        final String str =
                "package org.example\n" +
                        "import " + MyFact.class.getCanonicalName() + ";" +
                        "rule r1 when\n" +
                        "    MyFact( value == 10 || someMethod(2) == 4 )\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new MyFact(5));
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }

    public static class MyFact {
        private int value;

        public MyFact(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public int someMethod(int input) {
            return input * 2;
        }

        public int someMethod() {
            return 4;
        }
    }

    @ParameterizedTest
    @MethodSource("parameters")
    public void octalDigit(RUN_TYPE runType) {
        final String str =
                "package org.example\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule r1 when\n" +
                        "    Person( age == 013 )\n" +
                        "then\n" +
                        "end\n";

        KieSession ksession = getKieSession(runType, str);

        ksession.insert(new Person("John", 11)); // Octal 013 = Decimal 11
        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(1);
    }
}
