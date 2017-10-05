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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.drools.core.reteoo.AlphaNode;
import org.drools.modelcompiler.domain.Address;
import org.drools.modelcompiler.domain.Adult;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
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
                "  modify($p) { setAge($p.getAge()+1) };\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person me = new Person( "Mario", 40 );
        ksession.insert( me );
        ksession.fireAllRules();

        assertEquals( 41, me.getAge() );
    }

    @Test
    public void testBeta() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $p1 : Person(name == \"Mark\")\n" +
                "  $p2 : Person(name != \"Mark\", age > $p1.age)\n" +
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
    public void testGlobalInConsequence() {
        String str =
                "package org.mypkg;" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "global Result globalResult;" +
                "rule X when\n" +
                "  $p1 : Person(name == \"Mark\")\n" +
                "then\n" +
                " globalResult.setValue($p1.getName() + \" is \" + $p1.getAge());\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.setGlobal("globalResult", result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        assertEquals( "Mark is 37", result.getValue() );

    }

    @Test
    public void testGlobalInConstraint() {
        String str =
                "package org.mypkg;" +
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "global java.lang.String nameG;" +
                "global Result resultG;" +
                "rule X when\n" +
                "  $p1 : Person(nameG == name)\n" +
                "then\n" +
                " resultG.setValue($p1.getName() + \" is \" + $p1.getAge());\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.setGlobal("nameG", "Mark");

        Result result = new Result();
        ksession.setGlobal("resultG", result);

        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        assertEquals( "Mark is 37", result.getValue() );

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
        ksession.fireAllRules();
    }

    @Test
    public void testInlineCast() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $r : Result()\n" +
                "  $o : Object( this#Person.name == \"Mark\" )\n" +
                "then\n" +
                "  $r.setValue(\"Found: \" + $o);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Result result = new Result();
        ksession.insert( result );

        ksession.insert( "Mark" );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Mario", 40 ) );
        ksession.fireAllRules();

        assertEquals( "Found: Mark", result.getValue() );
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

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mark", results.iterator().next().getValue() );
    }

    @Test
    public void testSimpleInsertWithProperties() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person( address.addressName.startsWith(\"M\"))\n" +
                "then\n" +
                "  Result r = new Result($p.getName());" +
                "  insert(r);\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 37, new Address("London")) );
        ksession.insert( new Person( "Luca", 32 , new Address("Milan")) );
        ksession.fireAllRules();

        Collection<Result> results = getObjects( ksession, Result.class );
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

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mark", results.iterator().next().getValue() );
        assertEquals( 1, getObjects( ksession, Person.class ).size() );
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

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mark", results.iterator().next().getValue() );
        assertEquals( 1, getObjects( ksession, Person.class ).size() );
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
    public void testNot() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  not( Person( name.length == 4 ) )\n" +
                "then\n" +
                "  insert(new Result(\"ok\"));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person mario = new Person( "Mario", 40 );

        ksession.insert( mario );
        ksession.fireAllRules();

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "ok", results.iterator().next().getValue() );
    }

    @Test
    public void testNotEmptyPredicate() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  not( Person( ) )\n" +
                "then\n" +
                "  insert(new Result(\"ok\"));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ReteDumper.dumpRete( ksession );

        Person mario = new Person( "Mario", 40 );

        ksession.insert( mario );
        ksession.fireAllRules();

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 0, results.size() );
    }

    @Test
    public void testExists() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  exists Person( name.length == 5 )\n" +
                "then\n" +
                "  insert(new Result(\"ok\"));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person mario = new Person( "Mario", 40 );

        ksession.insert( mario );
        ksession.fireAllRules();

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "ok", results.iterator().next().getValue() );
    }

    @Test
    public void testForall() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  forall( $p : Person( name.length == 5 ) " +
                        "       Person( this == $p, age > 40 ) )\n" +
                "then\n" +
                "  insert(new Result(\"ok\"));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 41 ) );
        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Edson", 42 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "ok", results.iterator().next().getValue() );
    }

    @Test
    public void testExistsEmptyPredicate() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "import " + Result.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  exists( Person() )\n" +
                "then\n" +
                "  insert(new Result(\"ok\"));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person mark = new Person( "Mark", 37 );
        Person mario = new Person( "Mario", 40 );

        ksession.insert( mark );
        ksession.insert( mario );
        ksession.fireAllRules();

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "ok", results.iterator().next().getValue() );
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

        Collection<Result> results = getObjects( ksession, Result.class );
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

        Collection<Result> results = getObjects( ksession, Result.class );
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

    @Test(timeout = 5000)
    public void testNoLoop() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R no-loop when\n" +
                "  $p : Person(age > 18)\n" +
                "then\n" +
                "  modify($p) { setAge($p.getAge()+1) };\n" +
                "end";

        KieSession ksession = getKieSession( str );

        Person me = new Person( "Mario", 40 );
        ksession.insert( me );
        ksession.fireAllRules();

        assertEquals( 41, me.getAge() );
    }

    @Test
    public void testEval() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "  $p : Person()\n" +
                "  eval( $p.getAge() == 40 )\n" +
                "then\n" +
                "  insert(new Result($p.getName()));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }


    @Test
    public void testFunction() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "function String hello(String name) {\n" +
                "    return \"Hello \"+name+\"!\";\n" +
                "}" +
                "rule R when\n" +
                "  $p : Person()\n" +
                "  eval( $p.getAge() == 40 )\n" +
                "then\n" +
                "  insert(new Result(hello($p.getName())));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 40 ) );
        ksession.insert( new Person( "Mark", 37 ) );
        ksession.insert( new Person( "Edson", 35 ) );
        ksession.fireAllRules();

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Hello Mario!", results.iterator().next().getValue() );
    }
}