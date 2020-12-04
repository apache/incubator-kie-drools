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
import java.util.List;

import org.drools.modelcompiler.domain.Person;
import org.junit.Test;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// DROOLS-5852
public class BetaConditionTest extends BaseModelTest {

    public BetaConditionTest(RUN_TYPE testRunType ) {
        super( testRunType );
    }

    @Test // why 1 fired?
    public void betaCheckTwoConditionsExplicit() {
        final String str =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person(this != $p1, employed == (employed || $p1.employed) )" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<Person> results = new ArrayList<>();
        ksession.setGlobal("list", results);

        Person mark = new Person("Mark", 37).setEmployed(false);
        Person edson = new Person("Edson", 35).setEmployed(true);

        ksession.insert(mark);
        ksession.insert(edson);
        int rulesFired = ksession.fireAllRules();

        assertEquals(2, results.size());
        assertEquals(2, rulesFired);
        assertTrue(results.contains(mark));
        assertTrue(results.contains(edson));
    }

    @Test // this should have the same result as betaCheckTwoConditionsExplicit ( 2 fired)
    public void betaCheckTwoConditionsImplicit() {
        final String str =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person(this != $p1, employed || $p1.employed)" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<Person> results = new ArrayList<>();
        ksession.setGlobal("list", results);

        Person mark = new Person("Mark", 37).setEmployed(false);
        Person edson = new Person("Edson", 35).setEmployed(true);

        ksession.insert(mark);
        ksession.insert(edson);
        int rulesFired = ksession.fireAllRules();

        assertEquals(2, results.size());
        assertEquals(2, rulesFired);
        assertTrue(results.contains(mark));
        assertTrue(results.contains(edson));
    }

    @Test // this works in three cases but fires 3 times
    public void betaCheckORExplicit() {
        final String str =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person(employed == (employed || $p1.employed) )" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<Person> results = new ArrayList<>();
        ksession.setGlobal("list", results);

        Person mark = new Person("Mark", 37).setEmployed(false);
        Person edson = new Person("Edson", 35).setEmployed(true);

        ksession.insert(mark);
        ksession.insert(edson);
        int rulesFired = ksession.fireAllRules();

        assertEquals(3, results.size());
        assertEquals(3, rulesFired);
        assertTrue(results.contains(mark));
        assertTrue(results.contains(edson));
    }

    @Test // does not work in exec model but has same result than betaCheckORExplicit
    public void betaCheckORImplicit() {
        final String str =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person(employed || $p1.employed)" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<Person> results = new ArrayList<>();
        ksession.setGlobal("list", results);

        Person mark = new Person("Mark", 37).setEmployed(false);
        Person edson = new Person("Edson", 35).setEmployed(true);

        ksession.insert(mark);
        ksession.insert(edson);
        int rulesFired = ksession.fireAllRules();

        assertEquals(3, results.size());
        assertEquals(3, rulesFired);
        assertTrue(results.contains(mark));
        assertTrue(results.contains(edson));
    }

    @Test // works in three cases, fire 2 times
    public void betaCheckExplicit() {
        final String str =
                "global java.util.List list\n" +
                        "import " + Person.class.getCanonicalName() + ";" +
                        "rule R when\n" +
                        "$p1 : Person()" +
                        "$p2 : Person(employed == $p1.employed)" +
                        "then\n" +
                        "   list.add($p2);" +
                        "end\n";

        KieSession ksession = getKieSession( str );

        List<Person> results = new ArrayList<>();
        ksession.setGlobal("list", results);

        Person mark = new Person("Mark", 37).setEmployed(false);
        Person edson = new Person("Edson", 35).setEmployed(true);

        ksession.insert(mark);
        ksession.insert(edson);
        int rulesFired = ksession.fireAllRules();

        assertEquals(2, results.size());
        assertEquals(2, rulesFired);
        assertTrue(results.contains(mark));
        assertTrue(results.contains(edson));
    }


    @Test // should have same result as betaCheckExplicit, does not work with exec model
    public void betaCheckImplicit() {
        final String str =
                "global java.util.List list\n" +
                "import " + Person.class.getCanonicalName() + ";" +
                "rule R when\n" +
                    "$p1 : Person()" +
                    "$p2 : Person($p1.employed)" +
                "then\n" +
                "   list.add($p2);" +
                "end\n";

        KieSession ksession = getKieSession( str );

        List<Person> results = new ArrayList<>();
        ksession.setGlobal("list", results);

        Person mark = new Person("Mark", 37).setEmployed(false);
        Person edson = new Person("Edson", 35).setEmployed(true);

        ksession.insert(mark);
        ksession.insert(edson);
        int rulesFired = ksession.fireAllRules();

        assertEquals(2, results.size());
        assertEquals(2, rulesFired);
        assertTrue(results.contains(mark));
        assertTrue(results.contains(edson));
    }


}
