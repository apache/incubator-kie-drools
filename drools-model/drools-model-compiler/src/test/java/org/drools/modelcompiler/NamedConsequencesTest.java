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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.drools.modelcompiler.domain.Cheese;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.builder.Message.Level;
import org.kie.api.builder.Results;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class NamedConsequencesTest extends BaseModelTest {

    public NamedConsequencesTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testNamedConsequence() {
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

        KieSession ksession = getKieSession( str );
        Result result = new Result();
        ksession.insert( result );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection results = (Collection)result.getValue();
        assertEquals(2, results.size());

        assertTrue( results.containsAll( asList("Found Mark", "Mario is older than Mark") ) );
    }

    @Test
    public void testBreakingNamedConsequence() {
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

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.insert( result );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection results = (Collection)result.getValue();
        assertEquals(1, results.size());

        assertEquals( "Found Mark", results.iterator().next() );
    }

    @Test
    public void testNonBreakingNamedConsequence() {
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

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.insert( result );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection results = (Collection)result.getValue();
        assertEquals(2, results.size());

        assertTrue( results.containsAll( asList("Found Mark", "Mario is older than Mark") ) );
    }

    @Test
    public void testIfAfterAccumulate() {
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

        KieSession ksession = getKieSession( str );
        Result result = new Result();
        ksession.insert( result );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        List results = ( List )result.getValue();
        assertEquals(1, results.size());
        assertEquals("greater", results.get(0));
    }

    @Test
    public void testNonCompilingIFAfterOR() {
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

        Results results = createKieBuilder(str).getResults();
        assertTrue(results.hasMessages(Level.ERROR));
    }

    @Test
    public void testIfElseWithMvelAccessor() {
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

        KieSession ksession = getKieSession(str);
        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);
        Cheese brie = new Cheese("brie", 5);

        ksession.insert(stilton);
        ksession.insert(cheddar);
        ksession.insert(brie);

        ksession.fireAllRules();

        assertEquals(2, results.size());
        assertTrue(results.contains("cheddar"));
        assertTrue(results.contains("STILTON"));
    }

    @Test
    public void testWrongConsequenceName() {
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

        Results results = createKieBuilder(str).getResults();
        assertTrue(results.hasMessages(Level.ERROR));
    }

    @Test
    public void testMvelInsertWithNamedConsequence() {
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

        KieSession kSession = getKieSession(drl);

        AtomicInteger counter = new AtomicInteger(0);
        kSession.setGlobal("counter", counter);

        FactHandle messageHandle = kSession.insert(1);
        kSession.fireAllRules();

        kSession.delete(messageHandle);
        kSession.insert(2);
        kSession.fireAllRules();

        assertEquals(2, counter.get());
    }

    @Test
    public void testMVELBreak() {
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

        KieSession ksession = getKieSession(str);
        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);
        Cheese brie = new Cheese("brie", 5);

        ksession.insert(stilton);
        ksession.insert(cheddar);
        ksession.insert(brie);

        ksession.fireAllRules();

        assertEquals(1, results.size());
        assertTrue(results.contains("STILTON"));
    }

    @Test
    public void testMVELNoBreak() {
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

        KieSession ksession = getKieSession(str);
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
        assertEquals(1, results.size());
        assertTrue(results.contains("cheddar"));
    }

    @Test
    public void testMultipleIfElseInARow() {
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

        KieSession ksession = getKieSession(str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        assertEquals(7, result.size());

        assertTrue(result.containsAll(asList("Default", "Mark", "Edson", "Age35", "Age37")));
    }

    @Test
    public void testMultipleIfElseInARowWithJoin() {
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

        KieSession ksession = getKieSession(str);

        Result result = new Result();
        ksession.insert(result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.fireAllRules();

        Collection results = (Collection) result.getValue();
        assertEquals(7, results.size());

        assertTrue(results.containsAll(asList("Default", "Mark", "Edson", "Age35", "Age37")));
    }

    @Test
    public void testMultipleIfElseInARowWithJoin2() {
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

        KieSession ksession = getKieSession(str);

        Result result = new Result();
        ksession.insert(result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        ksession.insert(Integer.valueOf(40));
        ksession.fireAllRules();

        Collection results = (Collection) result.getValue();
        assertEquals(5, results.size());

        assertTrue(results.containsAll(asList("DefaultMario", "Mark", "Edson", "Age35", "Age37")));
    }

    public void testModifyInNamedConsequence() {
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

        KieSession ksession = getKieSession(str);
        List<String> results = new ArrayList<String>();
        ksession.setGlobal("results", results);

        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);
        Cheese brie = new Cheese("brie", 5);

        ksession.insert(stilton);
        ksession.insert(cheddar);
        ksession.insert(brie);

        ksession.fireAllRules();

        assertEquals(1, results.size());
        assertTrue(results.contains("stilton"));
    }

    @Test
    public void test2ModifyBlocksInNamedConsequences() {
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

        KieSession ksession = getKieSession(str);
        List<Integer> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);

        ksession.insert(stilton);
        ksession.insert(cheddar);

        int fired = ksession.fireAllRules();
        assertEquals(2, fired);

        Assertions.assertThat(results).containsExactlyInAnyOrder(10, 15);
    }

    @Test
    public void testMultipleIfAfterEval() {
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

        KieSession ksession = getKieSession(str);
        List<Integer> results = new ArrayList<>();
        ksession.setGlobal("results", results);

        Cheese stilton = new Cheese("stilton", 5);
        Cheese cheddar = new Cheese("cheddar", 7);

        ksession.insert(stilton);
        ksession.insert(cheddar);

        ksession.fireAllRules();

        assertEquals(2, results.size());
        assertTrue(results.contains("cheddar"));
        assertTrue(results.contains("stilton"));
    }

    public void testIfTrue() {
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

        KieSession ksession = getKieSession(str);
        List<String> result = new ArrayList<>();
        ksession.setGlobal("result", result);

        ksession.insert(new Person("John", 37));
        ksession.fireAllRules();

        assertEquals(2, result.size());
        assertTrue(result.containsAll(asList("main", "t1")));
    }
}
