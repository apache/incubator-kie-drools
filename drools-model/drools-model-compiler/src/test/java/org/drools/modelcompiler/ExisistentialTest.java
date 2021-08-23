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

import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;

import static org.drools.modelcompiler.CepTest.getCepKieModuleModel;
import static org.junit.Assert.assertEquals;

public class ExisistentialTest extends BaseModelTest {

    public ExisistentialTest( RUN_TYPE testRunType ) {
        super( testRunType );
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

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
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

        Person mario = new Person( "Mario", 40 );

        ksession.insert( mario );
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
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

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
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

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "ok", results.iterator().next().getValue() );
    }

    @Test
    public void testForallInQuery() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "query ifAllPersonsAreOlderReturnThem (int pAge)\n" +
                "    forall ( Person(age > pAge) )\n" +
                "    $person : Person()\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mario", 41 ) );
        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Edson", 42 ) );
        ksession.fireAllRules();

        QueryResults results = ksession.getQueryResults( "ifAllPersonsAreOlderReturnThem", 30 );

        assertEquals( 3, results.size() );
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

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "ok", results.iterator().next().getValue() );
    }

    @Test
    public void testComplexNots() throws Exception {
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

        KieSession ksession = getKieSession( getCepKieModuleModel(), str );
        assertEquals( 2, ksession.fireAllRules() );
    }


    @Test
    public void testDuplicateBindingNameInDifferentScope() {
        final String drl1 =
                "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "    exists( $fact : String( length == 4 ) and String( this == $fact ) )\n" +
                "    exists( $fact : Person( age == 18 ) and Person( this == $fact ) )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert( "test" );
        ksession.insert( new Person("test", 18) );
        assertEquals( 1, ksession.fireAllRules() );
    }

    @Test
    public void testNotWithDereferencingConstraint() {
        final String drl1 =
                "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule R when\n" +
                "  Person( $name : name )\n" +
                "  not Person( name.length == $name.length )\n" +
                "then\n" +
                "end\n";

        KieSession ksession = getKieSession( drl1 );

        ksession.insert( new Person("test", 18) );
        assertEquals( 0, ksession.fireAllRules() );
    }

    @Test
    public void test2NotsWithAnd() {
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

        KieSession ksession = getKieSession( drl1 );
        assertEquals( 1, ksession.fireAllRules() );
    }
}
