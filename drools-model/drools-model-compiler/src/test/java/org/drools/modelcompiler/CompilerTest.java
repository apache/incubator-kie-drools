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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.drools.core.reteoo.AlphaNode;
import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Adult;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Overloaded;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.drools.modelcompiler.domain.Toy;
import org.drools.modelcompiler.domain.Woman;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class CompilerTest extends BaseModelTest {

    public CompilerTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test(timeout = 5000)
    public void testPropertyReactvity() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person(name == \"Mario\")\n" +
                "then\n" +
                "  modify($p) { setAge($p.getAge()+1) }\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person me = new Person( "Mario", 40 );
        ksession.insert( me );
        ksession.fireAllRules();

        assertEquals( 41, me.getAge() );
    }

    @Test
    public void testPropertyReactvityOnFinalField() throws Exception {
        String str =
                "rule R when\n" +
                "    $a : String( length > 3 )\n" +
                "then\n" +
                "  System.out.println($a);\n" +
                "end\n";

        KieSession ksession = getKieSession(str);

        ksession.insert( "abcd" );
        ksession.insert( "xy" );

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testEqualityCheckOnNull() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  Person($name : name == \"Mario\")\n" +
                "then\n" +
                "  insert(new Result($name));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        final Person mario = new Person("Mario", 40);
        final Person luca = new Person(null, 33);

        ksession.insert(mario);
        ksession.insert(luca);

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testOrWithFixedLeftOperand() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person(name == \"Mario\" || == \"Luca\" || == \"Edoardo\")\n" +
                "then\n" +
                "  insert(new Result($p));\n" +
                "end";

        KieSession ksession = getKieSession( str );

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
        assertEquals( 3, results.size() );
        Assertions.assertThat(results.stream().map(r -> r.getValue())).containsExactlyInAnyOrder(mario, luca, edoardo);
    }

    @Test
    public void testBeta() {
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

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.insert( result );

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mario is older than Mark", result.getValue());

        result.setValue( null );
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertNull(result.getValue());

        mark.setAge( 34 );
        ksession.update( markFH, mark, "age" );

        ksession.fireAllRules();
        assertEquals("Edson is older than Mark", result.getValue());
    }

    @Test
    public void testRuleExtends() {
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

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.insert( result );
        assertEquals( 1, ksession.fireAllRules() );

        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));
        assertEquals( 0, ksession.fireAllRules() );

        ksession.insert(new Person("Mark", 37));
        assertEquals( 2, ksession.fireAllRules() );

        assertEquals("Mario is older than Mark", result.getValue());
    }

    @Test
    public void testBetaWithDeclaration() {
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

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.insert( result );

        Person mark = new Person("Mark", 37);
        Person edson = new Person("Edson", 35);
        Person mario = new Person("Mario", 40);

        FactHandle markFH = ksession.insert(mark);
        FactHandle edsonFH = ksession.insert(edson);
        FactHandle marioFH = ksession.insert(mario);

        ksession.fireAllRules();
        assertEquals("Mario is older than Mark", result.getValue());

        result.setValue( null );
        ksession.delete( marioFH );
        ksession.fireAllRules();
        assertNull(result.getValue());

        mark.setAge( 34 );
        ksession.update( markFH, mark, "age" );

        ksession.fireAllRules();
        assertEquals("Edson is older than Mark", result.getValue());
    }

    @Test
    public void testShareAlpha() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : java.util.Set()\n" +
                "  $p1 : Person(name == \"Edson\")\n" +
                "  $p2 : Person(name != \"Edson\", age > $p1.age)\n" +
                "  $p3 : Person(name != \"Edson\", age > $p1.age, this != $p2)\n" +
                "then\n" +
                "  $r.add($p2);\n" +
                "  $r.add($p3);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Set result = new HashSet<>();
        ksession.insert( result );

        Person mark = new Person( "Mark", 37 );
        Person edson = new Person( "Edson", 35 );
        Person mario = new Person( "Mario", 40 );

        ksession.insert( mark );
        ksession.insert( edson );
        ksession.insert( mario );
        ksession.fireAllRules();

        assertTrue( result.contains( mark ) );
        assertTrue( result.contains( mario ) );

        // Alpha node "name != Edson" should be shared between 3rd and 4th pattern.
        // therefore alpha nodes should be a total of 2: name == Edson, name != Edson
        assertEquals( 2, ReteDumper.dumpRete( ksession ).stream().filter( AlphaNode.class::isInstance ).count() );
    }

    @Test
    public void test3Patterns() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $mark : Person(name == \"Mark\")\n" +
                "  $p : Person(age > $mark.age)\n" +
                "  $s: String(this == $p.name)\n" +
                "then\n" +
                "  System.out.println(\"Found: \" + $s);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( "Mario" );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();
    }

    @Test
    public void testNullSafeDereferncing() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $p : Person( name!.length == 4 )\n" +
                "then\n" +
                "  $r.setValue(\"Found: \" + $p);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.insert( result );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( null, 40 ) );
        ksession.fireAllRules();

        assertEquals( "Found: Mark", result.getValue() );
    }

    @Test
    public void testSimpleInsert() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( name.length == 4 )\n" +
                "then\n" +
                "  Result r = new Result($p.getName());" +
                "  insert(r);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mark", results.iterator().next().getValue() );
    }

    @Test
    public void testSimpleInsertWithProperties() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( address.city.startsWith(\"M\"))\n" +
                "then\n" +
                "  Result r = new Result($p.getName());" +
                "  insert(r);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 37, new Address("London")) );
        ksession.insert( new Person( "Luca", 32 , new Address("Milan")) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Luca", results.iterator().next().getValue() );
    }

    @Test
    public void testSimpleDelete() {
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

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mark", results.iterator().next().getValue() );
        assertEquals( 1, getObjectsIntoList( ksession, Person.class ).size() );
    }

    @Test
    public void testSimpleInsertDeleteExplicitScope() {
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

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mark", results.iterator().next().getValue() );
        assertEquals( 1, getObjectsIntoList( ksession, Person.class ).size() );
    }

    @Test
    public void testSimpleUpdate() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( name.length == 4 )\n" +
                "then\n" +
                "  $p.setAge($p.getAge()+1);" +
                "  update($p);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person mark = new Person( "Mark", 37 );
        Person mario = new Person( "Mario", 40 );

        ksession.insert( mark );
        ksession.insert( mario );
        ksession.fireAllRules();

        assertEquals( 38, mark.getAge() );
        assertEquals( 40, mario.getAge() );
    }

    @Test
    public void testSimpleModify() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( name.length == 4 )\n" +
                "then\n" +
                "  modify($p) { setAge($p.getAge()+1) }\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person mark = new Person( "Mark", 37 );
        Person mario = new Person( "Mario", 40 );

        ksession.insert( mark );
        ksession.insert( mario );
        ksession.fireAllRules();

        assertEquals( 38, mark.getAge() );
        assertEquals( 40, mario.getAge() );
    }

    @Test
    public void testEmptyPattern() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  Person() \n" +
                "then\n" +
                "  insert(new Result(\"ok\"));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person mario = new Person( "Mario", 40 );

        ksession.insert( mario );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "ok", results.iterator().next().getValue() );
    }

    @Test
    public void testEmptyPatternWithBinding() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person() \n" +
                "then\n" +
                "  insert(new Result($p.getName()));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person mario = new Person( "Mario", 40 );

        ksession.insert( mario );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
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
    public void testFrom() {
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

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.insert( result );

        Adult dad = new Adult( "dad", 40 );
        dad.addChild( new Child( "Alan", 10 ) );
        dad.addChild( new Child( "Betty", 7 ) );
        ksession.insert( dad );
        ksession.fireAllRules();

        assertEquals("Alan", result.getValue());
    }

    @Test
    public void testConcatenatedFrom() {
        checkConcatenatedFrom(true);
    }

    @Test
    public void testConcatenatedFromWithCondition() {
        checkConcatenatedFrom(false);
    }

    private void checkConcatenatedFrom(boolean withCondition) {
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

        KieSession ksession = getKieSession( str );

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

        Assertions.assertThat(list).containsExactlyInAnyOrder("car", "ball");
    }

    @Test
    public void testAgeWithSum() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( personAge : age )\n" +
                "  $plusTwo : Person(age == personAge + 2 )\n" +
                "then\n" +
                "  insert(new Result($plusTwo.getName()));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mark", results.iterator().next().getValue() );
    }

    @Test
    public void testAgeWithSumUsing2DeclarationInBeta() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( personAge : age )\n" +
                     "  $plusTwo : Person(age == personAge + 2 + $p.age - $p.age )\n" +
                     "then\n" +
                     "  insert(new Result($plusTwo.getName()));\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("Mario", 40));
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        System.out.println(results);
        assertEquals(1, results.size());
        assertEquals("Mark", results.iterator().next().getValue());
    }

    @Test
    public void testFunction3() {
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

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testInsertLogical() {
        String str = "rule R when\n" +
                     "  Integer()" +
                     "then\n" +
                     "  insertLogical(\"Hello World\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        FactHandle fh_47 = ksession.insert(47);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertTrue(results.contains("Hello World"));

        ksession.delete(fh_47);
        ksession.fireAllRules();

        results = getObjectsIntoList(ksession, String.class);
        assertFalse(results.contains("Hello World"));
    }

    @Test
    public void testModifyRewriteAvoidTwiceThePreceeding() {
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

        KieSession ksession = getKieSession(str);

        List globalA = new ArrayList<>();
        List globalB = new ArrayList<>();
        ksession.setGlobal("globalA", globalA);
        ksession.setGlobal("globalB", globalB);

        ksession.insert(new Person("person1"));
        ksession.fireAllRules();

        assertEquals(1, globalA.size());
        assertEquals(1, globalB.size());
    }

    @Test
    public void testEmptyModifyRewrite() {
        String str = "rule R \n" +
                     "no-loop \n" +
                     "when\n" +
                     "  $s : String()" +
                     "then\n" +
                     "  System.out.println(\"intentional empty modify on $s\" + $s);" +
                     "  modify( $s ) { }\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert("Hello World");
        int fired = ksession.fireAllRules();

        assertEquals(1, fired);
    }

    @Test()
    public void testModifyRewriteWithComments() {
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

        KieSession ksession = getKieSession(str);

        List globalA = new ArrayList<>();
        List globalB = new ArrayList<>();
        ksession.setGlobal("globalA", globalA);
        ksession.setGlobal("globalB", globalB);

        Person person1 = new Person("person1");
        ksession.insert(person1);
        ksession.fireAllRules();

        assertEquals(1, globalA.size());
        assertEquals(1, globalB.size());
        assertEquals(47, person1.getAge());
    }

    @Test()
    @Ignore("fails for exec model, is not recognizing properly start/ends of modify block")
    public void testModifyRewriteWithCommentsAbsurd() {
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

        KieSession ksession = getKieSession(str);

        List globalA = new ArrayList<>();
        List globalB = new ArrayList<>();
        ksession.setGlobal("globalA", globalA);
        ksession.setGlobal("globalB", globalB);

        Person person1 = new Person("person1");
        ksession.insert(person1);
        ksession.fireAllRules();

        assertEquals(1, globalA.size());
        assertEquals(1, globalB.size());
        assertEquals(47, person1.getAge());
    }

    @Test
    public void testConstraintContainingAMethodCallWithParams() {
        String str = "import " + Overloaded.class.getCanonicalName() + ";" +
                     "rule OverloadedMethods\n" +
                     "when\n" +
                     "  o : Overloaded( method(5, 9, \"x\") == 15 )\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Overloaded());
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertEquals(1, results.size());
    }

    @Test
    public void testSimpleModifyUsingNameRefFollowedByMethodCall() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( name.length() == 4 )\n" +
                     "then\n" +
                     "  modify($p) { setAge($p.getAge()+1) }\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        Person mark = new Person("Mark", 37);
        Person mario = new Person("Mario", 40);

        ksession.insert(mark);
        ksession.insert(mario);
        ksession.fireAllRules();

        assertEquals(38, mark.getAge());
        assertEquals(40, mario.getAge());
    }

    @Test
    public void testChainOfMethodCallInConstraint() {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( getAddress().getCity().length() == 5 )\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        Person john = new Person("John", 47);
        Address a = new Address("Italy");
        john.setAddress(a);

        ksession.insert(john);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertEquals(1, results.size());
    }

    @Test
    public void testChainOfMethodCallInConstraintSub() {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( address.( city.length() == 5 && city.startsWith(\"I\") ) )\n" + // DRL feature "Grouped accessors for nested objects" is addressed by the RuleDescr directly.
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        Person john = new Person("John", 47);
        Address a = new Address("Italy");
        john.setAddress(a);

        ksession.insert(john);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertEquals(1, results.size());
    }

    @Test
    public void testChainFieldAccessorsAndMethodCall() {
        String str = "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( address.getCity().length == 5 )\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        Person john = new Person("John", 47);
        Address a = new Address("Italy");
        john.setAddress(a);

        ksession.insert(john);
        ksession.fireAllRules();

        Collection<String> results = getObjectsIntoList(ksession, String.class);
        assertEquals(1, results.size());
    }

    public static class NullUnsafeA {

        private NullUnsafeB someB;

        public NullUnsafeB getSomeB() {
            return someB;
        }

        public void setSomeB(NullUnsafeB someB) {
            this.someB = someB;
        }
    }

    public static class NullUnsafeB {

        private NullUnsafeC someC;

        public NullUnsafeC getSomeC() {
            return someC;
        }

        public void setSomeC(NullUnsafeC someC) {
            this.someC = someC;
        }
    }

    public static class NullUnsafeC {

        private NullUnsafeD someD;

        public NullUnsafeD getSomeD() {
            return someD;
        }

        public void setSomeD(NullUnsafeD someD) {
            this.someD = someD;
        }

    }

    public static class NullUnsafeD {

        private String something;

        public String getSomething() {
            return something;
        }

        public void setSomething(String something) {
            this.something = something;
        }
    }

    @Test
    public void testNullSafeMultiple() {
        String str = "import " + NullUnsafeA.class.getCanonicalName() + ";" +
                     "import " + NullUnsafeB.class.getCanonicalName() + ";" +
                     "import " + NullUnsafeD.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $a : NullUnsafeA( someB!.someC!.someD!.something == \"Hello\" )\n" +
                     "then\n" +
                     "  insert(\"matched\");\n" +
                     "end";

        for (int i = 0; i <= 4; i++) {
            KieSession ksession = getKieSession(str);

            NullUnsafeA a = new NullUnsafeA();
            NullUnsafeB b = new NullUnsafeB();
            NullUnsafeC x = new NullUnsafeC();
            NullUnsafeD c = new NullUnsafeD();
            // trap #0
            if (i != 0) {
                c.setSomething("Hello");
            }
            // trap #1
            if (i != 1) {
                b.setSomeC(x);
            }
            // trap #2
            if (i != 2) {
                x.setSomeD(c);
            }
            // trap #3
            if (i != 3) {
                a.setSomeB(b);
            }
            ksession.insert(a);
            ksession.fireAllRules();

            Collection<String> results = getObjectsIntoList(ksession, String.class);
            if (i < 4) {
                assertEquals(0, results.size());
            } else if (i == 4) {
                // iteration #3 has no null-traps
                assertEquals(1, results.size());
            }
        }
    }

    @Test
    @Ignore("the codegen does not support it yet")
    public void testNullSafeDereferncing2() {
        String str = "import " + Result.class.getCanonicalName() + ";" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person( address!.city.startsWith(\"M\") )\n" +
                     "then\n" +
                     "  Result r = new Result($p.getName());" +
                     "  insert(r);\n" +
                     "end";

        KieSession ksession = getKieSession(str);

        ksession.insert(new Person("John1", 41, null));
        ksession.insert(new Person("John2", 42, new Address("Milan")));
        ksession.fireAllRules();

        List<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals("John2", results.get(0).getValue());
    }

    @Test
    public void testInnerBindingWithOr() {
        String str =
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " s: String( s.toString() == \"x\" || s.toString() == \"y\" )\n" +
                "then\n" +
                " list.add(s);\n" +
                "end";

        KieSession ksession = getKieSession(str);

        List<String> list = new ArrayList<String>();
        ksession.setGlobal("list", list);

        ksession.insert("y");
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals( "y", list.get(0) );
    }

    @Test
    public void testRHS() {
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

        KieSession ksession = getKieSession(str);
    }


    @Test
    public void testDroolsContext() {
        String str =
                "global java.util.List list\n" +
                "global java.util.List list2\n" +
                "\n" +
                "rule R when\n" +
                "then\n" +
                " list.add(list2.add(kcontext));\n" +
                "end";

        KieSession ksession = getKieSession(str);

        List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        List<Object> list2 = new ArrayList<>();
        ksession.setGlobal("list2", list2);

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testDroolsContextInString() {
        String str =
                "global java.util.List list\n" +
                        "global java.util.List list2\n" +
                        "\n" +
                        "rule R when\n" +
                        "then\n" +
                        " list.add(list2.add(\"something\" + kcontext));\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        List<Object> list2 = new ArrayList<>();
        ksession.setGlobal("list2", list2);

        ksession.fireAllRules();

        assertEquals( 1, list.size() );
    }

    @Test
    public void testDroolsContextWithoutReplacingStrings() {
        String str =
                "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "then\n" +
                        " list.add(\"this kcontext shoudln't be replaced\");\n" +
                        "end";

        KieSession ksession = getKieSession(str);

        List<Object> list = new ArrayList<>();
        ksession.setGlobal("list", list);
        ksession.fireAllRules();

        assertEquals( 1, list.size() );
        assertEquals("this kcontext shoudln't be replaced", list.iterator().next());
    }

    @Test
    public void testBindWith2Arguments() {
        String str =
                "import " + Adult.class.getCanonicalName() + ";\n" +
                        "import " + Child.class.getCanonicalName() + ";\n" +
                        "import " + Result.class.getCanonicalName() + ";\n" +
                        "rule R when\n" +
                        "  $y : Adult( $sum : (name.length + age) )\n" +
                        "then\n" +
                        "  insert(new Result($sum));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        Adult a = new Adult( "Mario", 43 );
        ksession.insert( a );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(((Number)results.iterator().next().getValue()).intValue(), 48);
    }
}