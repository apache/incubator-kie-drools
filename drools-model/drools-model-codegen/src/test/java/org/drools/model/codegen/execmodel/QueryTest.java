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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.core.QueryResultsImpl;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.model.codegen.execmodel.domain.Person;
import org.drools.model.codegen.execmodel.domain.Relationship;
import org.drools.model.codegen.execmodel.domain.Result;
import org.drools.model.codegen.execmodel.oopathdtables.InternationalAddress;
import org.drools.model.codegen.execmodel.util.TrackingAgendaEventListener;
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

import static org.assertj.core.api.Assertions.assertThat;

public class QueryTest extends BaseModelTest {

    public QueryTest( RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test
    public void testQueryZeroArgs() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "global java.lang.Integer ageG;" +
                "query \"older than\"\n" +
                "    $p : Person(age > ageG)\n" +
                "end ";

        KieSession ksession = getKieSession(str);

        ksession.setGlobal("ageG", 40);

        ksession.insert(new Person("Mark", 39));
        ksession.insert(new Person("Mario", 41));

        QueryResults results = ksession.getQueryResults("older than");

        assertThat(results.size()).isEqualTo(1);
        QueryResultsRow res = results.iterator().next();
        Person p = (Person) res.get("$p");
        assertThat(p.getName()).isEqualTo("Mario");

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

        assertThat(results.size()).isEqualTo(1);
        Person p = (Person) results.iterator().next().get( "$p" );
        assertThat(p.getName()).isEqualTo("Mario");
    }

    @Test
    public void testQueryOneArgumentWithoutType() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "query olderThan( $age )\n" +
                "    $p : Person(age > (Integer)$age)\n" +
                "end ";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        QueryResults results = ksession.getQueryResults( "olderThan", 40 );

        assertThat(results.size()).isEqualTo(1);
        Person p = (Person) results.iterator().next().get( "$p" );
        assertThat(p.getName()).isEqualTo("Mario");
    }

    @Test
    public void testQueryInRule() {
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                "import " + Person.class.getCanonicalName() + ";" +
                "query older_than( Person $p, int $age )\n" +
                "    $p := Person(age > $age)\n" +
                "end\n" +
                "rule R when\n" +
                "    older_than( $p, 40; )\n" +
                "then\n" +
                "    insert(new Result($p.getName()));\n" +
                "end";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList( ksession, Result.class );
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Mario");
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Mario");
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
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.iterator().next().getValue()).isEqualTo("Mario");
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

        assertThat(results.size()).isEqualTo(1);
        Person p = (Person) results.iterator().next().get( "$p" );
        assertThat(p.getName()).isEqualTo("Mario");
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

        assertThat(results.size()).isEqualTo(1);
        Person p = (Person) results.iterator().next().get( "$p" );
        assertThat(p.getName()).isEqualTo("Mario");
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

        assertThat(results.size()).isEqualTo(1);
        Person p = (Person) results.iterator().next().get( "$p" );
        assertThat(p.getName()).isEqualTo("Mario");
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

        assertThat(results.size()).isEqualTo(1);
        String paramName = ((QueryImpl) ksession.getKieBase().getQuery("defaultpkg", "isRelatedTo" )).getParameters()[1].getIdentifier();
        assertThat(results.iterator().next().get(paramName)).isEqualTo("B");

    }

    @Test
    public void testQueryWithOOPath() {
        String str =
                "import " + java.util.List.class.getCanonicalName() + ";" +
                "import " + org.drools.model.codegen.execmodel.oopathdtables.Person.class.getCanonicalName() + ";" +
                "import " + org.drools.model.codegen.execmodel.oopathdtables.Address.class.getCanonicalName() + ";" +
                "import " + org.drools.model.codegen.execmodel.oopathdtables.InternationalAddress.class.getCanonicalName() + ";" +
                "query listSafeCities\n" +
                   "$cities : List() from accumulate (Person ( $city: /address#InternationalAddress[state == \"Safecountry\"]/city), collectList($city))\n" +
                "end";

        KieSession ksession = getKieSession( str );

        org.drools.model.codegen.execmodel.oopathdtables.Person person = new org.drools.model.codegen.execmodel.oopathdtables.Person();
        person.setAddress(new InternationalAddress("", 1, "Milan", "Safecountry"));
        ksession.insert(person);

        org.drools.model.codegen.execmodel.oopathdtables.Person person2 = new org.drools.model.codegen.execmodel.oopathdtables.Person();
        person2.setAddress(new InternationalAddress("", 1, "Rome", "Unsafecountry"));
        ksession.insert(person2);

        QueryResults results = ksession.getQueryResults( "listSafeCities");

        List cities = (List) results.iterator().next().get("$cities");
        assertThat(cities.size()).isEqualTo(1);
        assertThat(cities.get(0)).isEqualTo("Milan");
    }

    @Test
    public void testQueryWithOOPathTransformedToFrom() {
        String str =
                "import " + java.util.List.class.getCanonicalName() + ";" +
                "import " + org.drools.model.codegen.execmodel.oopathdtables.Person.class.getCanonicalName() + ";" +
                "import " + org.drools.model.codegen.execmodel.oopathdtables.Address.class.getCanonicalName() + ";" +
                "import " + org.drools.model.codegen.execmodel.oopathdtables.InternationalAddress.class.getCanonicalName() + ";" +
                "query listSafeCities\n" +
                    "$p  : Person()\n" +
                    "$a  : InternationalAddress(state == \"Safecountry\") from $p.address\n" +
                    "$cities : List() from accumulate ($city : String() from $a.city, collectList($city))\n" +
                "end";

        KieSession ksession = getKieSession( str );

        org.drools.model.codegen.execmodel.oopathdtables.Person person = new org.drools.model.codegen.execmodel.oopathdtables.Person();
        person.setAddress(new InternationalAddress("", 1, "Milan", "Safecountry"));
        ksession.insert(person);

        org.drools.model.codegen.execmodel.oopathdtables.Person person2 = new org.drools.model.codegen.execmodel.oopathdtables.Person();
        person2.setAddress(new InternationalAddress("", 1, "Rome", "Unsafecountry"));
        ksession.insert(person2);

        QueryResults results = ksession.getQueryResults( "listSafeCities");

        List cities = (List) results.iterator().next().get("$cities");
        assertThat(cities.size()).isEqualTo(1);
        assertThat(cities.get(0)).isEqualTo("Milan");
    }

    @Test
    public void testQueryWithOOPathTransformedToFromInsideAcc() {
        String str =
                "import " + java.util.List.class.getCanonicalName() + ";" +
                "import " + org.drools.model.codegen.execmodel.oopathdtables.Person.class.getCanonicalName() + ";" +
                "import " + org.drools.model.codegen.execmodel.oopathdtables.Address.class.getCanonicalName() + ";" +
                "import " + org.drools.model.codegen.execmodel.oopathdtables.InternationalAddress.class.getCanonicalName() + ";" +
                "query listSafeCities\n" +
                    "$cities : List() from accumulate (" +
                    "    $p : Person() and\n" +
                    "    $a : InternationalAddress(state == \"Safecountry\") from $p.address and\n" +
                    "    $city : String() from $a.city, collectList($city))\n" +
                "end";

        KieSession ksession = getKieSession( str );

        org.drools.model.codegen.execmodel.oopathdtables.Person person2 = new org.drools.model.codegen.execmodel.oopathdtables.Person();
        person2.setAddress(new InternationalAddress("", 1, "Rome", "Unsafecountry"));
        ksession.insert(person2);

        org.drools.model.codegen.execmodel.oopathdtables.Person person = new org.drools.model.codegen.execmodel.oopathdtables.Person();
        person.setAddress(new InternationalAddress("", 1, "Milan", "Safecountry"));
        ksession.insert(person);


        QueryResults results = ksession.getQueryResults( "listSafeCities");

        List cities = (List) results.iterator().next().get("$cities");
        assertThat(cities.size()).isEqualTo(1);
        assertThat(cities.get(0)).isEqualTo("Milan");
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

        assertThat(results.size()).isEqualTo(1);
        final QueryResultsRow firstResult = results.iterator().next();

        Object resultDrlx = firstResult.get("z");
        assertThat("B".equals(resultDrlx)).isTrue();
    }

    @Test
    public void testPositionalRecursiveQuery() throws Exception {
        String query =
                "query isContainedIn(String x, String y)\n" +
                "    Location (x, y;)\n" +
                "    or\n" +
                "    ( Location (z, y;) and ?isContainedIn(x, z;))\n" +
                "end\n";

        checkRecursiveQuery( query );
    }

    @Test
    public void testUnificationRecursiveQuery() throws Exception {
        String query =
                "query isContainedIn(String x, String y)\n" +
                "    Location( x := thing, y := location)\n" +
                "    or \n" +
                "    ( Location(z := thing, y := location) and ?isContainedIn( x := x, z := y ) )\n" +
                "end\n";

        checkRecursiveQuery( query );
    }

    private void checkRecursiveQuery( String query ) throws InstantiationException, IllegalAccessException {
        String str =
                "package org.test;\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                query +
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
                "\n"; // +
//                "// rule values at A12, header at A6\n" +
//                "rule \"testPushQueryRule\" when\n" +
//                "    String(this == \"push\")\n" +
//                "    Person($l : likes)\n" +
//                "    isContainedIn($l, \"office\";)\n" +
//                "then\n" +
//                "end";

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
        final FactHandle peterHandle = ksession.insert(peter);
        final FactHandle steakHandle = ksession.insert(steakLocation);
        final FactHandle tableHandle = ksession.insert(tableLocation);
        ksession.insert("pull");
        ksession.fireAllRules();

        assertThat(listener.isRuleFired("testPullQueryRule")).isTrue();
//        assertThat(listener.isRuleFired("testPushQueryRule")).isFalse();
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

        assertThat(listener.isRuleFired("testPullQueryRule")).isFalse();
        assertThat(listener.isRuleFired("testPushQueryRule")).isFalse();
        listener.clear();

        final Person paul = new Person("Paul");
        paul.setLikes("steak");
        ksession.insert(paul);
        ksession.fireAllRules();

        assertThat(listener.isRuleFired("testPullQueryRule")).isTrue();
        assertThat(listener.isRuleFired("testPushQueryRule")).isFalse();
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

        final List<String> l = new ArrayList<String>();
        for (QueryResultsRow r : qResults) {
            l.add((String) r.get("x"));
        }

        // items in the office should be the following
        assertThat(l.size()).isEqualTo(4);
        assertThat(l.contains("desk")).isTrue();
        assertThat(l.contains("flashlight")).isTrue();
        assertThat(l.contains("envelope")).isTrue();
        assertThat(l.contains("key")).isTrue();
    }

    @Test
    public void testQueryUnificationUnset() {
        String str = "package drl;\n" +
                "declare Anon " +
                "    cld : String @key " +
                "    sup : String @key " +
                "end " +

                "rule Init " +
                "when " +
                "then " +
                "    insert( 'aa' ); " +
                "    insert( 'bb' ); " +
                "    insert( 'cc' ); " +
                "    insertLogical( new Anon( 'aa', 'bb' ) ); " +
                "    insertLogical( new Anon( 'cc', 'aa' ) ); " +
                "end " +

                "query unravel( String $g, String $c ) " +
                "    ( " +
                "        ( Anon( $g, $c ; ) and String( $c := this, this.contains( \"b\" ) ) ) " +
                "        or " +
                "        ( Anon( $g, $x ; ) and unravel( $x, $c ; ) ) " +
                "    ) " +
                "end " +

                "rule Check " +
                "when " +
                "    Anon( $e, $par ; ) " +
                "    unravel( $par, $comp ; ) " +
                "    ( Double() or Anon() ) " +
                "then\n" +
                "end\n" +

                "rule Mod " +
                "no-loop " +
                "when\n" +
                "    $a : Anon( ) " +
                "    ( Double() or Anon() ) " +
                "then " +
                "end ";

        KieSession ksession = getKieSession( str );
        ksession.fireAllRules();
    }

    @Test
    public void testQueryCalling2Queries() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "query isPersonOlderThan(Person p, int ageFrom)\n" +
                "    Person(this == p, age > ageFrom)\n" +
                "end\n" +
                "\n" +
                "query isPersonYoungerThan(Person p, int ageTo)\n" +
                "    Person(this == p, age < ageTo)\n" +
                "end\n" +
                "query getPersonsBetween(int ageFrom, int ageTo) \n" +
                "    p : Person()\n" +
                "    isPersonOlderThan(p, ageFrom;) and isPersonYoungerThan(p, ageTo;)\n" +
                "end\n" +
                "\n";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person( "Mark", 39 ) );
        ksession.insert( new Person( "Mario", 41 ) );

        QueryResults results = ksession.getQueryResults( "getPersonsBetween", 40, 50 );

        assertThat(results.size()).isEqualTo(1);
        Person p = (Person) results.iterator().next().get( "p" );
        assertThat(p.getName()).isEqualTo("Mario");
    }

    @Test
    public void testQueriesWithVariableUnification() throws Exception {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                "query peeps( String $name, int $age ) \n" +
                "    $p : Person( $name := name, $age := age ) \n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        Person p1 = new Person( "darth", 100 );
        Person p2 = new Person( "yoda", 300 );
        Person p3 = new Person( "luke", 300 );
        Person p4 = new Person( "bobba", 300 );

        ksession.insert( p1 );
        ksession.insert( p2 );
        ksession.insert( p3 );
        ksession.insert( p4 );

        QueryResultsImpl results = (QueryResultsImpl) ksession.getQueryResults( "peeps", Variable.v, Variable.v );
        assertThat(results.size()).isEqualTo(4);
        List names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertThat(names.size()).isEqualTo(4);
        assertThat(names.contains("luke")).isTrue();
        assertThat(names.contains("yoda")).isTrue();
        assertThat(names.contains("bobba")).isTrue();
        assertThat(names.contains("darth")).isTrue();

        results = (QueryResultsImpl) ksession.getQueryResults( "peeps", Variable.v, 300 );
        assertThat(results.size()).isEqualTo(3);
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertThat(names.size()).isEqualTo(3);
        assertThat(names.contains("luke")).isTrue();
        assertThat(names.contains("yoda")).isTrue();
        assertThat(names.contains("bobba")).isTrue();

        results = (QueryResultsImpl) ksession.getQueryResults( "peeps", "darth", Variable.v );
        assertThat(results.size()).isEqualTo(1);
        names = new ArrayList();
        for ( org.kie.api.runtime.rule.QueryResultsRow row : results ) {
            names.add( ((Person) row.get( "$p" )).getName() );
        }
        assertThat(names.size()).isEqualTo(1);
        assertThat(names.contains("darth")).isTrue();
    }

    @Test
    public void testQueryWithUpdateOnFactHandle() throws Exception {
        String str =
                "global java.util.List list; " +
                "query foo( Integer $i ) " +
                "   $i := Integer( this < 10 ) " +
                "end\n" +
                "\n" +
                "rule r2 when " +
                "   foo( $i; ) " +
                "   Integer( this == 20 ) " +
                "then " +
                "   System.out.println(\"20 \" + $i);" +
                "   list.add( 20 + $i );\n" +
                "end\n" +
                "rule r3 when " +
                "   $i : Integer( this == 1 ) " +
                "then " +
                "   System.out.println($i);" +
                "   update( kcontext.getKieRuntime().getFactHandle( $i ), $i + 1 );" +
                "end\n" +
                "\n";

        KieSession ksession = getKieSession( str );

        final List<Integer> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert(1);
        ksession.insert(20);

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(2);
        assertThat((int) list.get(0)).isEqualTo(21);
        assertThat((int) list.get(1)).isEqualTo(22);
    }

    @Test
    public void testQueryCallWithBindings() {
        String str =
                "package org.drools.compiler.test  \n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule x1\n" +
                "when\n" +
                "    peeps($age1 : $age, $name1 : $name)\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n" +
                "\n" +
                "query peeps( String $name, int $age ) \n" +
                "    Person( $name := name, $age := age; ) \n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert( new Person( "Mario", 44 ) );

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Mario : 44");
    }

    @Test
    public void testQueryCallWithJoinInputAndOutput() {
        String str =
                "package org.drools.compiler.test  \n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "query peeps( String $name, int $age ) \n" +
                "    Person( $name := name, $age := age; ) \n" +
                "end\n" +
                "\n" +
                "rule x1\n" +
                "when\n" +
                "    $name1 : String() from \"Mario\"\n" +
                "    peeps($name1, $age1; )\n" +
                "then\n" +
                "   list.add( $name1 + \" : \" + $age1 );\n" +
                "end \n";

        KieSession ksession = getKieSession( str );

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert( new Person( "Mario", 44 ) );

        ksession.fireAllRules();
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Mario : 44");
    }

    @Test
    public void testQueryWithDyanmicInsert() throws IOException, ClassNotFoundException {
        String str =
                "package org.drools.compiler.test  \n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "query peeps( Person $p, String $name, int $age ) \n" +
                "    $p := Person( ) from new Person( $name, $age ) \n" +
                "end\n" +
                "rule x1\n" +
                "when\n" +
                "    $n1 : String( )\n" +
                "    not Person( name == 'darth' )\n " +
                "    peeps($p; $name : $n1, $age : 100 )\n" +
                "then\n" +
                "   list.add( $p );\n" +
                "end \n";

        KieSession ksession = getKieSession( str );

        try {
            final List<Person> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            final Person p1 = new Person("darth", 100);

            ksession.insert("darth");
            ksession.fireAllRules();
            assertThat(list.size()).isEqualTo(1);
            assertThat(list.get(0)).isEqualTo(p1);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testQuerySameNameBinding() throws IOException, ClassNotFoundException {
        String str =
                "package org.drools.compiler.test  \n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "query peeps( String name ) \n" +
                "    Person( name := name ) \n" +
                "end \n";

        KieSession ksession = getKieSession( str );

        ksession.insert( new Person("Mario", 44) );
        ksession.insert( new Person("Mark", 40) );
        ksession.insert( new Person("Edson", 37) );

        List<String> list = new ArrayList<>();
        QueryResults results = ksession.getQueryResults( "peeps", Variable.v );
        for (final QueryResultsRow result : results) {
            list.add((String) result.get("name"));
        }
        assertThat(list.size()).isEqualTo(3);
        assertThat(list.containsAll(Arrays.asList("Mario", "Edson", "Mark"))).isTrue();

        list.clear();
        results = ksession.getQueryResults( "peeps", "Mario" );
        for (final QueryResultsRow result : results) {
            list.add((String) result.get("name"));
        }
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Mario");
    }

    @Test
    public void testQuery10Args() throws IOException, ClassNotFoundException {
        String str =
                "package org.drools.compiler.test  \n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "global java.util.List list\n" +
                "query peeps( String name, int age, long ageLong, int id, String likes, String arg6, String arg7, String arg8, String arg9, String arg10) \n" +
                "    Person( name := name, age := age, ageLong := ageLong, id := id, likes := likes ) \n" +
                "end \n";

        KieSession ksession = getKieSession( str );

        Person mario = new Person("Mario", 44);
        mario.setAgeLong(44L);
        mario.setId(1);
        mario.setLikes("cheese");
        ksession.insert( mario );
        Person mark = new Person("Mark", 40);
        mark.setAgeLong(40L);
        mark.setId(2);
        mark.setLikes("beer");
        ksession.insert( mark );

        List<String> list = new ArrayList<>();
        QueryResults results = ksession.getQueryResults( "peeps", "Mario", 44, 44L, 1, "cheese"
                , "these"
                , "arguments"
                , "are"
                , "ignored"
                , "it's just for compilation"
        );
        for (final QueryResultsRow result : results) {
            list.add((String) result.get("name"));
        }
        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("Mario");
    }

    @Test
    public void testPositionalQueryWithAccumulate() {
        // DROOLS-6128
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "declare Person\n" +
                        "    name : String \n" +
                        "    age : int \n" +
                        "end" +
                        "\n" +
                        "rule Init when\n" +
                        "then\n" +
                        "  insert(new Person(\"Mark\", 37));\n" +
                        "  insert(new Person(\"Edson\", 35));\n" +
                        "  insert(new Person(\"Mario\", 40));\n" +
                        "end\n" +
                        "query accAge(String arg)\n" +
                        "  accumulate ( Person ( arg, $age; ); \n" +
                        "                $sum : sum($age)  \n" +
                        "              )                          \n" +
                        "end";

        KieSession ksession = getKieSession( str );
        ksession.fireAllRules();

        QueryResults results = ksession.getQueryResults( "accAge", "Mark" );

        assertThat(results.size()).isEqualTo(1);
        final QueryResultsRow firstResult = results.iterator().next();

        Object resultDrlx = firstResult.get("$sum");
        assertThat(resultDrlx).isEqualTo(37);
    }

    @Test
    public void testPositionalQueryWithAmbigousName() {
        // DROOLS-6128
        String str =
                "import " + Result.class.getCanonicalName() + ";" +
                        "declare Person\n" +
                        "    name : String \n" +
                        "    age : int \n" +
                        "end" +
                        "\n" +
                        "rule Init when\n" +
                        "then\n" +
                        "  insert(new Person(\"Mark\", 37));\n" +
                        "  insert(new Person(\"Edson\", 35));\n" +
                        "  insert(new Person(\"Mario\", 40));\n" +
                        "end\n" +
                        "query accAge(String arg)\n" +
                        "  accumulate ( Person ( arg, age; ); \n" +
                        "                $sum : sum(age)  \n" +
                        "              )                          \n" +
                        "end";

        KieSession ksession = getKieSession( str );
        ksession.fireAllRules();

        QueryResults results = ksession.getQueryResults( "accAge", "Mark" );

        assertThat(results.size()).isEqualTo(1);
        final QueryResultsRow firstResult = results.iterator().next();

        Object resultDrlx = firstResult.get("$sum");
        assertThat(resultDrlx).isEqualTo(37);
    }

    @Test
    public void testQueryWithAccumulateAndUnification() {
        // DROOLS-6105
        String str =
                "import " + Result.class.getCanonicalName() + ";\n" +
                "global java.util.List result;\n" +
                "declare Person\n" +
                "    name : String \n" +
                "    age : int \n" +
                "end" +
                "\n" +
                "rule Init when\n" +
                "then\n" +
                "  insert(new Person(\"Mark\", 37));\n" +
                "  insert(new Person(\"Edson\", 35));\n" +
                "  insert(new Person(\"Mario\", 40));\n" +
                "end\n" +
                "query accAge(String arg, int $sum)\n" +
                "  $sum := Number() from accumulate ( Person ( arg, $age; ); \n" +
                "                sum($age)  \n" +
                "              )                          \n" +
                "end\n" +
                "rule callAcc when\n" +
                "    $s: String()\n" +
                "    accAge($s, $sum;)\n" +
                "then\n" +
                "    result.add($sum);" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<Integer> result = new ArrayList<>();
        ksession.setGlobal( "result", result );

        ksession.insert( "Mark" );
        ksession.fireAllRules();

        assertThat(result.size()).isEqualTo(1);
        assertThat((int) result.get(0)).isEqualTo(37);
    }

    @Test
    public void testQueryWithAccumulateInvokingQuery() {
        // DROOLS-6105
        String str =
                "import " + Result.class.getCanonicalName() + ";\n" +
                "global java.util.List result;\n" +
                "declare Person\n" +
                "    name : String \n" +
                "    age : int \n" +
                "end" +
                "\n" +
                "rule Init when\n" +
                "then\n" +
                "  insert(new Person(\"Mark\", 37));\n" +
                "  insert(new Person(\"Edson\", 35));\n" +
                "  insert(new Person(\"Mario\", 40));\n" +
                "end\n" +
                "query findPerson(String $name, int $age)\n" +
                "  Person ( $name, $age; )\n" +
                "end\n" +
                "query accAge(String arg, int $sum)\n" +
                "  $sum := Number() from accumulate ( findPerson( arg, $age; ); \n" +
                "                sum($age)  \n" +
                "              )                          \n" +
                "end\n" +
                "rule callAcc when\n" +
                "    $s: String()\n" +
                "    accAge($s, $sum;)\n" +
                "then\n" +
                "    result.add($sum);" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<Integer> result = new ArrayList<>();
        ksession.setGlobal( "result", result );

        ksession.insert( "Mark" );
        ksession.fireAllRules();

        assertThat(result.size()).isEqualTo(1);
        assertThat((int) result.get(0)).isEqualTo(37);
    }

    @Test
    public void testQueryDoubleUnification() {
        // DROOLS-6105
        final String str = "" +
                "package org.drools.compiler.test  \n" +
                "declare Location\n" +
                "    thing : String \n" +
                "    location : String \n" +
                "end" +
                "\n" +
                "declare Edible\n" +
                "   thing : String\n" +
                "end" +
                "\n" +
                "query whereFood( String x, String y ) \n" +
                "    Location(x, y;) \n" +
                "    Edible(x;) \n" +
                "end\n" +
                "\n" +
                "rule init when\n" +
                "then\n" +
                "        insert( new Location(\"crackers\", \"kitchen\") );\n" +
                "        insert( new Edible(\"crackers\") );\n" +
                "end\n" +
                "";

        KieSession ksession = getKieSession( str );
        ksession.fireAllRules();

        QueryResults results = ksession.getQueryResults("whereFood", Variable.v, "kitchen");
        assertThat(results.size()).isEqualTo(1);
        QueryResultsRow row = results.iterator().next();
        assertThat(row.get("x")).isEqualTo("crackers");
    }

    @Test
    public void testQueryWithInheritance() {
        // DROOLS-6105
        final String str = "" +
                "global java.util.List list;\n" +
                "declare Thing \n" +
                "    thing : String \n" +
                "end \n" +
                "declare Edible extends Thing \n" +
                "end \n" +
                "\n" +
                "query look(List food)  \n" +
                "    food := List() from accumulate( Edible(x;), \n" +
                "                                    collectList( x ) ) \n" +
                "end\n" +
                "\n" +
                "rule init when\n" +
                "then\n" +
                "        insert( new Edible( 'peach' ) ); \n" +
                "end\n" +
                "\n" +
                "rule reactiveLook \n" +
                "when \n" +
                "    look($food;) \n" +
                "then \n" +
                "    list.addAll( $food ); \n" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        ksession.fireAllRules();

        assertThat(list.size()).isEqualTo(1);
        assertThat(list.get(0)).isEqualTo("peach");
    }
}
