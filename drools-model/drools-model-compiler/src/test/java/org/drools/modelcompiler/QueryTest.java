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

import org.assertj.core.api.Assertions;
import org.drools.core.rule.QueryImpl;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Relationship;
import org.drools.modelcompiler.domain.Result;
import org.drools.modelcompiler.util.TrackingAgendaEventListener;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.kie.api.runtime.rule.Variable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
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

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
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

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertEquals( 1, results.size() );
        assertEquals( "Mario", results.iterator().next().getValue() );
    }

    @Test
    public void testNonPositionalQuery() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "query findPerson( String $n, int $a )\n" +
                "    $p : Person(name == $n, age == $a)\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        QueryResults results = ksession.getQueryResults( "findPerson", "Mario", 41 );

        assertEquals( 1, results.size() );
        Person p = (Person) results.iterator().next().get( "$p" );
        assertEquals( "Mario", p.getName() );
    }

    @Test
    public void testPositionalQuery() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "query findPerson( String $n, int $a )\n" +
                "    $p : Person($n, $a;)\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        QueryResults results = ksession.getQueryResults( "findPerson", "Mario", 41 );

        assertEquals( 1, results.size() );
        Person p = (Person) results.iterator().next().get( "$p" );
        assertEquals( "Mario", p.getName() );
    }

    @Test
    public void testUnificationParameterInPattern() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "query personsAges(int ages)\n" +
                        "$p : Person(ages := age)\n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        QueryResults results = ksession.getQueryResults( "personsAges",  41 );

        assertEquals( 1, results.size() );
        Person p = (Person) results.iterator().next().get( "$p" );
        assertEquals( "Mario", p.getName() );
    }

    @Test
    public void testQueryCallingQuery() {
        String str =
                "import " + Relationship.class.getCanonicalName() + ";" +
                "query isRelatedTo(String x, String y)\n" +
                "    isRelatedTo2(x, y;)\n" +
                "end\n" +
                "query isRelatedTo2(String x, String y)\n" +
                "    Relationship(x, y;)\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Relationship( "A", "B" ) );
        ksession.insert( new Relationship( "B", "C" ) );

        QueryResults results = ksession.getQueryResults( "isRelatedTo", "A", "B" );

        assertEquals( 1, results.size() );
        String paramName = ((QueryImpl) ksession.getKieBase().getQuery("defaultpkg", "isRelatedTo" )).getParameters()[1].getIdentifier();
        assertEquals("B", results.iterator().next().get(paramName));

    }

    @Test
    public void testPositionalRecursiveQueryWithUnification() {
        String str =
                "import " + Relationship.class.getCanonicalName() + ";" +
                "query isRelatedTo(String x, String y)\n" +
                "    Relationship (x, y;)\n" +
                "    or\n" +
                "    ( Relationship (z, y;) and ?isRelatedTo(x, z;))\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Relationship( "A", "B" ) );
        ksession.insert( new Relationship( "B", "C" ) );

        QueryResults results = ksession.getQueryResults( "isRelatedTo", "A", "C" );

        assertEquals( 1, results.size() );
        final QueryResultsRow firstResult = results.iterator().next();

        Object resultDrlx = firstResult.get("z");
        assertTrue("B".equals(resultDrlx));
    }

    @Test
    public void testRecursiveQuery() throws Exception {
        String str =
                "package org.test;\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "query isContainedIn(String x, String y)\n" +
                "    Location (x, y;)\n" +
                "    or\n" +
                "    ( Location (z, y;) and ?isContainedIn(x, z;))\n" +
                "end\n" +
                "declare Location\n" +
                "    thing : String\n" +
                "    location : String\n" +
                "end\n" +
                "// rule values at A11, header at A6\n" +
                "rule \"testPullQueryRule\" when\n" +
                "    String(this == \"pull\")\n" +
                "    Person($l : likes)\n" +
                "    ?isContainedIn($l, \"office\";)\n" +
                "then\n" +
                "end\n" +
                "\n" +
                "// rule values at A12, header at A6\n" +
                "rule \"testPushQueryRule\" when\n" +
                "    String(this == \"push\")\n" +
                "    Person($l : likes)\n" +
                "    isContainedIn($l, \"office\";)\n" +
                "then\n" +
                "end";

        KieSession ksession = getKieSession( str );

        FactType locationType = ksession.getKieBase().getFactType("org.test", "Location");

        final TrackingAgendaEventListener listener = new TrackingAgendaEventListener();
        ksession.addEventListener(listener);

        final Person peter = new Person("Peter");
        peter.setLikes("steak");
        final Object steakLocation = locationType.newInstance();
        locationType.set(steakLocation, "thing", "steak");
        locationType.set(steakLocation, "location", "table");
        final Object tableLocation = locationType.newInstance();
        locationType.set(tableLocation, "thing", "table");
        locationType.set(tableLocation, "location", "office");
        ksession.insert(peter);
        final FactHandle steakHandle = ksession.insert(steakLocation);
        final FactHandle tableHandle = ksession.insert(tableLocation);
        ksession.insert("pull");
        ksession.fireAllRules();

        Assertions.assertThat(listener.isRuleFired("testPullQueryRule")).isTrue();
        Assertions.assertThat(listener.isRuleFired("testPushQueryRule")).isFalse();
        listener.clear();

        // when location is changed of what Peter likes, pull query should
        // ignore it
        final Object steakLocation2 = locationType.newInstance();
        locationType.set(steakLocation2, "thing", "steak");
        locationType.set(steakLocation2, "location", "desk");
        final Object deskLocation = locationType.newInstance();
        locationType.set(deskLocation, "thing", "desk");
        locationType.set(deskLocation, "location", "office");
        ksession.insert(steakLocation2);
        ksession.insert(deskLocation);
        ksession.delete(steakHandle);
        ksession.delete(tableHandle);
        ksession.fireAllRules();

        Assertions.assertThat(listener.isRuleFired("testPullQueryRule")).isFalse();
        Assertions.assertThat(listener.isRuleFired("testPushQueryRule")).isFalse();
        listener.clear();

        final Person paul = new Person("Paul");
        paul.setLikes("steak");
        ksession.insert(paul);
        ksession.fireAllRules();

        Assertions.assertThat(listener.isRuleFired("testPullQueryRule")).isTrue();
        Assertions.assertThat(listener.isRuleFired("testPushQueryRule")).isFalse();
    }

    @Test
    public void testRecursiveQueryWithBatchCommand() throws Exception {
        String str =
                "package org.test;\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "query isContainedIn(String x, String y)\n" +
                        "    Location (x, y;)\n" +
                        "    or\n" +
                        "    ( Location (z, y;) and ?isContainedIn(x, z;))\n" +
                        "end\n" +
                        "declare Location\n" +
                        "    thing : String\n" +
                        "    location : String\n" +
                        "end";

        KieServices kieServices = KieServices.Factory.get();
        KieSession ksession = getKieSession( str );

        FactType locationType = ksession.getKieBase().getFactType("org.test", "Location");

        // a pear is in the kitchen
        final Object pear = locationType.newInstance();
        locationType.set(pear, "thing", "pear");
        locationType.set(pear, "location", "kitchen");

        // a desk is in the office
        final Object desk = locationType.newInstance();
        locationType.set(desk, "thing", "desk");
        locationType.set(desk, "location", "office");

        // a flashlight is on the desk
        final Object flashlight = locationType.newInstance();
        locationType.set(flashlight, "thing", "flashlight");
        locationType.set(flashlight, "location", "desk");

        // an envelope is on the desk
        final Object envelope = locationType.newInstance();
        locationType.set(envelope, "thing", "envelope");
        locationType.set(envelope, "location", "desk");

        // a key is in the envelope
        final Object key = locationType.newInstance();
        locationType.set(key, "thing", "key");
        locationType.set(key, "location", "envelope");

        // create working memory objects
        final List<Command<?>> commands = new ArrayList<Command<?>>();

        // Location instances
        commands.add(kieServices.getCommands().newInsert(pear));
        commands.add(kieServices.getCommands().newInsert(desk));
        commands.add(kieServices.getCommands().newInsert(flashlight));
        commands.add(kieServices.getCommands().newInsert(envelope));
        commands.add(kieServices.getCommands().newInsert(key));

        // fire all rules
        final String queryAlias = "myQuery";
        commands.add(kieServices.getCommands().newQuery(queryAlias, "isContainedIn", new Object[] { Variable.v, "office" }));

        final ExecutionResults results = ksession.execute(kieServices.getCommands().newBatchExecution(commands, null));
        final QueryResults qResults = (QueryResults) results.getValue(queryAlias);

        String paramName = ((QueryImpl ) ksession.getKieBase().getQuery("org.test", "isContainedIn" )).getParameters()[0].getIdentifier();
        final List<String> l = new ArrayList<String>();
        for (QueryResultsRow r : qResults) {
            l.add((String) r.get(paramName));
        }

        // items in the office should be the following
        Assertions.assertThat(l.size()).isEqualTo(4);
        Assertions.assertThat(l.contains("desk")).isTrue();
        Assertions.assertThat(l.contains("flashlight")).isTrue();
        Assertions.assertThat(l.contains("envelope")).isTrue();
        Assertions.assertThat(l.contains("key")).isTrue();
    }
}
