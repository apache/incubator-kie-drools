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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.drools.model.codegen.execmodel.domain.Cheese;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Result;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

public class NamedConsequencesTest extends BaseModelTest {

    @ParameterizedTest
	@MethodSource("parameters")
    public void testNamedConsequence(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $p1 : Person(name == \"Mark\")\n" +
                "  do[FoundMark]\n" +
                "  $p2 : Person(name != \"Mark\", age > $p1.age)\n" +
                "then\n" +
                "  $r.addValue($p2.getName() + \" is older than \" + $p1.getName());\n" +
                "then[FoundMark]\n" +
                "  $r.addValue(\"Found \" + $p1.getName());\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);
        Result result = new Result();
        ksession.insert( result );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection results = (Collection)result.getValue();
        assertThat(results.size()).isEqualTo(2);

        assertThat(results.containsAll(asList("Found Mark", "Mario is older than Mark"))).isTrue();
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testBreakingNamedConsequence(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $p1 : Person(name == \"Mark\")\n" +
                "  if ( age < 30 ) break[FoundYoungMark]" +
                "  else if ( age > 50) break[FoundOldMark]\n" +
                "  else break[FoundMark]\n" +
                "  $p2 : Person(name != \"Mark\", age > $p1.age)\n" +
                "then\n" +
                "  $r.addValue($p2.getName() + \" is older than \" + $p1.getName());\n" +
                "then[FoundYoungMark]\n" +
                "  $r.addValue(\"Found young \" + $p1.getName());\n" +
                "then[FoundOldMark]\n" +
                "  $r.addValue(\"Found old \" + $p1.getName());\n" +
                "then[FoundMark]\n" +
                "  $r.addValue(\"Found \" + $p1.getName());\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection results = (Collection)result.getValue();
        assertThat(results.size()).isEqualTo(1);

        assertThat(results.iterator().next()).isEqualTo("Found Mark");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testNonBreakingNamedConsequence(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $p1 : Person(name == \"Mark\")\n" +
                "  if ( age < 30 ) break[FoundYoungMark]" +
                "  else if ( age > 50) break[FoundOldMark]\n" +
                "  else do[FoundMark]\n" +
                "  $p2 : Person(name != \"Mark\", age > $p1.age)\n" +
                "then\n" +
                "  $r.addValue($p2.getName() + \" is older than \" + $p1.getName());\n" +
                "then[FoundYoungMark]\n" +
                "  $r.addValue(\"Found young \" + $p1.getName());\n" +
                "then[FoundOldMark]\n" +
                "  $r.addValue(\"Found old \" + $p1.getName());\n" +
                "then[FoundMark]\n" +
                "  $r.addValue(\"Found \" + $p1.getName());\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert( result );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection results = (Collection)result.getValue();
        assertThat(results.size()).isEqualTo(2);

        assertThat(results.containsAll(asList("Found Mark", "Mario is older than Mark"))).isTrue();
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testIfAfterAccumulate(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n" +
                "                $sum : sum($p.getAge())  \n" +
                "              )                          \n" +
                "  if ($sum > 70) do[greater]\n" +
                "  String()\n" +
                "then\n" +
                "  $r.addValue(\"default\");\n" +
                "then[greater]\n" +
                "  $r.addValue(\"greater\");\n" +
                "end";

        KieSession ksession = getKieSession(runType, str);
        Result result = new Result();
        ksession.insert( result );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        List results = ( List )result.getValue();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0)).isEqualTo("greater");
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testNonCompilingIFAfterOR(RUN_TYPE runType) {
        String str = "import " + Cheese.class.getCanonicalName() + ";\n " +
                     "global java.util.List results;\n" +
                     "\n" +
                     "rule R1 when\n" +
                     "    ( $a: Cheese ( type == \"stilton\" )\n" +
                     "    or\n" +
                     "    $a: Cheese ( type == \"gorgonzola\" ) )\n" +
                     "    if ( price > 10 ) do[t1]\n" +
                     "    $b: Cheese ( type == \"cheddar\" )\n" +
                     "then\n" +
                     "    results.add( $b.getType() );\n" +
                     "then[t1]\n" +
                     "    results.add( $a.getType() );\n" +
                     "end\n";

        Results results = createKieBuilder(runType, str).getResults();
        assertThat(results.hasMessages(Level.ERROR)).isTrue();
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testIfElseWithMvelAccessor(RUN_TYPE runType) {
        String str = "import " + Cheese.class.getCanonicalName() + ";\n " +
                     "global java.util.List results;\n" +
                     "\n" +
                     "rule R1 dialect \"mvel\" when\n" +
                     "    $a: Cheese ( type == \"stilton\" )\n" +
                     "    if ( $a.price > Cheese.BASE_PRICE ) do[t1] else do[t2]\n" +
                     "    $b: Cheese ( type == \"cheddar\" )\n" +
                     "then\n" +
                     "    results.add( $b.getType() );\n" +
                     "then[t1]\n" +
                     "    results.add( $a.getType() );\n" +
                     "then[t2]\n" +
                     "    results.add( $a.getType().toUpperCase() );\n" +
                     "end\n";

        KieSession ksession = getKieSession(runType, str);
        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);
        Cheese brie = new Cheese("brie", 5);

        ksession.insert(stilton);
        ksession.insert(cheddar);
        ksession.insert(brie);

        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("STILTON")).isTrue();
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testWrongConsequenceName(RUN_TYPE runType) {
        String str = "import " + Cheese.class.getCanonicalName() + ";\n " +
                     "global java.util.List results;\n" +
                     "\n" +
                     "rule R1 dialect \"mvel\" when\n" +
                     "    $a: Cheese ( type == \"stilton\" )\n" +
                     "    $b: Cheese ( type == \"cheddar\" )\n" +
                     "    if ( 200 < 400 ) break[t2]\n" +
                     "then\n" +
                     "    results.add( $b.getType() );\n" +
                     "then[t1]\n" +
                     "    results.add( $a.getType().toUpperCase() );\n" +
                     "end\n";

        Results results = createKieBuilder(runType, str).getResults();
        assertThat(results.hasMessages(Level.ERROR)).isTrue();
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMvelInsertWithNamedConsequence(RUN_TYPE runType) {
        String drl =
                "package org.drools.compiler\n" +
                     "global java.util.concurrent.atomic.AtomicInteger counter\n" +
                     "declare Output\n" +
                     "    feedback: String\n" +
                     "end\n" +
                     "rule \"Move to next\" dialect \"mvel\"\n" +
                     "   when\n" +
                     "          $i: Integer()\n" +
                     "          if ($i == 1) break[nextStep1]\n" +
                     "   then\n" +
                     "           insert(new Output(\"defualt\"));\n" +
                     "   then[nextStep1]\n" +
                     "           insert(new Output(\"step 1\"));\n" +
                     "end\n" +
                     "\n" +
                     "rule \"Produce output\"\n" +
                     "    when\n" +
                     "        $output: Output()\n" +
                     "    then\n" +
                     "        System.out.println($output);\n" +
                     "        retract($output);" +
                     "        counter.incrementAndGet();\n" +
                     "end\n";

        KieSession kSession = getKieSession(runType, drl);

        AtomicInteger counter = new AtomicInteger(0);
        kSession.setGlobal("counter", counter);

        FactHandle messageHandle = kSession.insert(1);
        kSession.fireAllRules();

        kSession.delete(messageHandle);
        kSession.insert(2);
        kSession.fireAllRules();

        assertThat(counter.get()).isEqualTo(2);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMVELBreak(RUN_TYPE runType) {
        String str = "import " + Cheese.class.getCanonicalName() + ";\n " +
                     "global java.util.List results;\n" +
                     "\n" +
                     "rule R1 dialect \"mvel\" when\n" +
                     "    $a: Cheese ( type == \"stilton\" )\n" +
                     "    $b: Cheese ( type == \"cheddar\" )\n" +
                     "    if ( 200 < 400 ) break[t1]\n" +
                     "then\n" +
                     "    results.add( $b.type );\n" +
                     "then[t1]\n" +
                     "    results.add( $a.type.toUpperCase() );\n" +
                     "end\n";

        KieSession ksession = getKieSession(runType, str);
        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);
        Cheese brie = new Cheese("brie", 5);

        ksession.insert(stilton);
        ksession.insert(cheddar);
        ksession.insert(brie);

        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("STILTON")).isTrue();
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMVELNoBreak(RUN_TYPE runType) {
        String str = "import " + Cheese.class.getCanonicalName() + ";\n " +
                     "global java.util.List results;\n" +
                     "\n" +
                     "rule R1 dialect \"mvel\" when\n" +
                     "    $a: Cheese ( type == \"stilton\" )\n" +
                     "    $b: Cheese ( type == \"cheddar\" )\n" +
                     "    if ( 200 > 400 ) break[t1]\n" +
                     "then\n" +
                     "    results.add( $b.type );\n" +
                     "then[t1]\n" +
                     "    results.add( $a.type.toUpperCase() );\n" +
                     "end\n";

        KieSession ksession = getKieSession(runType, str);
        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);
        Cheese brie = new Cheese("brie", 5);

        ksession.insert(stilton);
        ksession.insert(cheddar);
        ksession.insert(brie);

        ksession.fireAllRules();

        System.out.println(results);
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("cheddar")).isTrue();
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMultipleIfElseInARow(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                     "global java.util.List result;\n" +
                     "rule R when\n" +
                     "  $p1 : Person()\n" +
                     "  if (name == \"Mark\") do[Mark]\n" +
                     "  else if (name == \"Edson\") do[Edson]\n" +
                     "  if (age == 35) do[Age35]\n" +
                     "  else if (age == 37) do[Age37]\n" +
                     "  if (age == 90) do[Age90]\n" +
                     "  else if (age == 100) do[Age100]\n" +
                     "then\n" +
                     "  result.add(\"Default\");\n" +
                     "then[Mark]\n" +
                     "  result.add(\"Mark\");\n" +
                     "then[Edson]\n" +
                     "  result.add(\"Edson\");\n" +
                     "then[Age35]\n" +
                     "  result.add(\"Age35\");\n" +
                     "then[Age37]\n" +
                     "  result.add(\"Age37\");\n" +
                     "then[Age90]\n" +
                     "  result.add(\"Age90\");\n" +
                     "then[Age100]\n" +
                     "  result.add(\"Age100\");\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        assertThat(result.size()).isEqualTo(7);

        assertThat(result.containsAll(asList("Default", "Mark", "Edson", "Age35", "Age37"))).isTrue();
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMultipleIfElseInARowWithJoin(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "rule R when\n" +
                     "  $r : Result()\n" +
                     "  $p1 : Person()\n" +
                     "  if (name == \"Mark\") do[Mark]\n" +
                     "  else if (name == \"Edson\") do[Edson]\n" +
                     "  if (age == 35) do[Age35]\n" +
                     "  else if (age == 37) do[Age37]\n" +
                     "then\n" +
                     "  $r.addValue(\"Default\");\n" +
                     "then[Mark]\n" +
                     "  $r.addValue(\"Mark\");\n" +
                     "then[Edson]\n" +
                     "  $r.addValue(\"Edson\");\n" +
                     "then[Age35]\n" +
                     "  $r.addValue(\"Age35\");\n" +
                     "then[Age37]\n" +
                     "  $r.addValue(\"Age37\");\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert(result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        Collection results = (Collection) result.getValue();
        assertThat(results.size()).isEqualTo(7);

        assertThat(results.containsAll(asList("Default", "Mark", "Edson", "Age35", "Age37"))).isTrue();
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMultipleIfElseInARowWithJoin2(RUN_TYPE runType) {
        String str =
                "import " + Result.class.getCanonicalName() + ";\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "rule R when\n" +
                     "  $r : Result()\n" +
                     "  $p1 : Person()\n" +
                     "  if (name == \"Mark\") do[Mark]\n" +
                     "  else if (name == \"Edson\") do[Edson]\n" +
                     "  if (age == 35) do[Age35]\n" +
                     "  else if (age == 37) do[Age37]\n" +
                     "  $i : Integer(this == $p1.age)" +
                     "then\n" +
                     "  $r.addValue(\"Default\" + $p1.getName());\n" +
                     "then[Mark]\n" +
                     "  $r.addValue(\"Mark\");\n" +
                     "then[Edson]\n" +
                     "  $r.addValue(\"Edson\");\n" +
                     "then[Age35]\n" +
                     "  $r.addValue(\"Age35\");\n" +
                     "then[Age37]\n" +
                     "  $r.addValue(\"Age37\");\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);

        Result result = new Result();
        ksession.insert(result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.insert(Integer.valueOf(40));
        ksession.fireAllRules();

        Collection results = (Collection) result.getValue();
        assertThat(results.size()).isEqualTo(5);

        assertThat(results.containsAll(asList("DefaultMario", "Mark", "Edson", "Age35", "Age37"))).isTrue();
    }

    public void testModifyInNamedConsequence(RUN_TYPE runType) {
        String str = "import " + Cheese.class.getCanonicalName() + ";\n " +
                     "global java.util.List results;\n" +
                     "\n" +
                     "rule R1 when\n" +
                     "    $a: Cheese ( type == \"stilton\" )\n" +
                     "    if ( price < 10 ) break[t1]\n" +
                     "    $b: Cheese ( type == \"cheddar\" )\n" +
                     "then\n" +
                     "    results.add( $a.getType() );\n" +
                     "then[t1]\n" +
                     "    modify( $a ) { setPrice(15) };\n" +
                     "end\n";

        KieSession ksession = getKieSession(runType, str);
        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);
        Cheese brie = new Cheese("brie", 5);

        ksession.insert(stilton);
        ksession.insert(cheddar);
        ksession.insert(brie);

        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(1);
        assertThat(results.contains("stilton")).isTrue();
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void test2ModifyBlocksInNamedConsequences(RUN_TYPE runType) {
        String str = "import " + Cheese.class.getCanonicalName() + ";\n " +
                     "global java.util.List results;\n" +
                     "\n" +
                     "rule R1 when\n" +
                     "    $a: Cheese (price < 10)\n" +
                     "    if ( type == \"stilton\" ) break[t1]\n" +
                     "then\n" +
                     "    modify( $a ) { setPrice(10) };\n" +
                     "    results.add( $a.getPrice() );\n" +
                     "then[t1]\n" +
                     "    modify( $a ) { setPrice(15) };\n" +
                     "    results.add( $a.getPrice() );\n" +
                     "end\n";

        KieSession ksession = getKieSession(runType, str);
        List<Integer> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);

        ksession.insert(stilton);
        ksession.insert(cheddar);

        int fired = ksession.fireAllRules();
        assertThat(fired).isEqualTo(2);

        assertThat(results).containsExactlyInAnyOrder(10, 15);
    }

    @ParameterizedTest
	@MethodSource("parameters")
    public void testMultipleIfAfterEval(RUN_TYPE runType) {
        String str = "import " + Cheese.class.getCanonicalName() + ";\n " +
                     "global java.util.List results;\n" +
                     "\n" +
                     "rule R1 when\n" +
                     "    $a: Cheese ( )\n" +
                     "    eval( $a.getType().equals(\"stilton\") )\n" +
                     "    if ( $a.getPrice() > 10 ) do[t1]\n" +
                     "    if ( $a.getPrice() < 10 ) do[t2]\n" +
                     "    $b: Cheese ( type == \"cheddar\" )\n" +
                     "then\n" +
                     "    results.add( $b.getType() );\n" +
                     "then[t1]\n" +
                     "    results.add( $a.getType().toUpperCase() );\n" +
                     "then[t2]\n" +
                     "    results.add( $a.getType() );\n" +
                     "end\n";

        KieSession ksession = getKieSession(runType, str);
        List<Integer> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);

        ksession.insert(stilton);
        ksession.insert(cheddar);

        ksession.fireAllRules();

        assertThat(results.size()).isEqualTo(2);
        assertThat(results.contains("cheddar")).isTrue();
        assertThat(results.contains("stilton")).isTrue();
    }

    public void testIfTrue(RUN_TYPE runType) {
        String str =
                "import " + Person.class.getCanonicalName() + ";\n" +
                     "global java.util.List result;\n" +
                     "rule R when\n" +
                     "  $p : Person()\n" +
                     "  if (true) do[t1]\n" +
                     "then\n" +
                     "  result.add(\"main\");\n" +
                     "then[t1]\n" +
                     "  result.add(\"t1\");\n" +
                     "end";

        KieSession ksession = getKieSession(runType, str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Person("John", 37));
        ksession.fireAllRules();

        assertThat(result.size()).isEqualTo(2);
        assertThat(result.containsAll(asList("main", "t1"))).isTrue();
    }

    @ParameterizedTest
    @MethodSource("parametersStandardOnly") // fails to build with exec model. Filed as incubator-kie-drools/issues/6459
    void testConditionalBreak_defaultBreakDefault(RUN_TYPE runType) {
        String str = """
                package com.example.reproducer;
                
                import org.drools.model.codegen.execmodel.domain.Person;
                
                global java.util.List result;
                
                rule R1
                  when
                    Person( $age : age > 10 )
                    if( $age == 21 ) break [ Do2 ]
                  then
                    result.add("R1 Default Consequence: $age = " + $age);
                  then[ Do2 ]
                    result.add("R1 Do2 Consequence: $age = " + $age);
                end
                
                rule R2
                  when
                    $p : Person( age == 20 )
                  then
                    result.add("R2");
                    $p.setAge(21);
                    update($p);
                end
                
                rule R3
                  when
                    $p : Person( age == 21 )
                  then
                    result.add("R3");
                    $p.setAge(22);
                    update($p);
                end
        """;

        // incubator-kie-issues/issues/2105
        // The expected flow is:
        //   1. R1 fires, goes to default consequence
        //   2. R2 fires, sets age to 21
        //   3. R1 fires, goes to Do2 consequence
        //   4. R3 fires, sets age to 22
        //   5. R1 fires, goes to default consequence
        // The original issue is that the step 3 executes both Do2 and default consequences.
        // If we fix PhreakBranchNode.doLeftUpdates to call `trgLeftTuples.addDelete(branchTuples.mainLeftTuple);` even if branchTuples.mainLeftTuple.getSink() is TerminalNode,
        // then next issue arises. The step 5 fails with NPE because the deletion breaks dormantMatches LinkedList.
        // The issue reproduces when conditional branch results in "default", "break", "default" order

        KieSession ksession = getKieSession(runType, str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal( "result", result );

        ksession.insert(new Person("John", 20));
        ksession.fireAllRules();

        assertThat(result).containsExactly("R1 Default Consequence: $age = 20",
                                           "R2",
                                           "R1 Do2 Consequence: $age = 21",
                                           "R3",
                                           "R1 Default Consequence: $age = 22");
    }
}
