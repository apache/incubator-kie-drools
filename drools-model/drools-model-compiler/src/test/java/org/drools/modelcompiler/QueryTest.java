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

import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class QueryTest extends BaseModelTest {

    public QueryTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testQueryZeroArgs() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "global java.lang.Integer ageG;" +
                        "query olderThan\n" +
                        "    $p : Person(age > ageG)\n" +
                        "end ";

        KieSession ksession = getKieSession(str);

        ksession.setGlobal("ageG", 40);

        ksession.insert(new Person("Mark", 39));
        ksession.insert(new Person("Mario", 41));

        QueryResults results = ksession.getQueryResults("olderThan", 40);

        assertEquals(1, results.size());
        QueryResultsRow res = results.iterator().next();
        Person p = (Person) res.get("$p");
        assertEquals("Mario", p.getName());

    }

    @Test
    public void testQueryOneArgument() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                        "query olderThan( int $age )\n" +
                        "    $p : Person(age > $age)\n" +
                        "end ";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        QueryResults results = ksession.getQueryResults( "olderThan", 40 );

        assertEquals( 1, results.size() );
        Person p = (Person) results.iterator().next().get( "$p" );
        assertEquals( "Mario", p.getName() );
    }

    @Test
    public void testQueryInRule() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "query olderThan( Person $p, int $age )\n" +
                        "    $p := Person(age > $age)\n" +
                        "end\n" +
                        "rule R when\n" +
                        "    olderThan( $p, 40; )\n" +
                        "then\n" +
                        "    insert(new Result($p.getName()));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        ksession.fireAllRules();

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testQueryInRuleWithDeclaration() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "query olderThan( Person $p, int $age )\n" +
                        "    $p := Person(age > $age)\n" +
                        "end\n" +
                        "rule R when\n" +
                        "    $p : Person( name.startsWith(\"M\") )\n" +
                        "    olderThan( $p, 40; )\n" +
                        "then\n" +
                        "    insert(new Result($p.getName()));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );
        ksession.insert( new Person( "Edson", 41 ) );

        ksession.fireAllRules();

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }


    @Test
    public void testQueryInvokedWithGlobal() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "global Integer ageG;" +
                        "query olderThan( Person $p, int $age )\n" +
                        "    $p := Person(age > $age)\n" +
                        "end\n" +
                        "rule R when\n" +
                        "    $p : Person( name.startsWith(\"M\") )\n" +
                        "    olderThan( $p, ageG; )\n" +
                        "then\n" +
                        "    insert(new Result($p.getName()));\n" +
                        "end";

        KieSession ksession = getKieSession( str );

        ksession.setGlobal("ageG", 40);

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );
        ksession.insert( new Person( "Edson", 41 ) );

        ksession.fireAllRules();

        Collection<Result> results = getObjects( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }
}
