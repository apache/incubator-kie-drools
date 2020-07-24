/*
 * Copyright 2020 JBoss Inc
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
import org.drools.core.ClockType;
import org.drools.modelcompiler.builder.RuleWriter;
import org.drools.modelcompiler.domain.Child;
import org.drools.modelcompiler.domain.Man;
import org.drools.modelcompiler.domain.Person;
import org.drools.modelcompiler.domain.Result;
import org.drools.modelcompiler.domain.StockTick;
import org.drools.modelcompiler.domain.Woman;
import org.drools.modelcompiler.util.lambdareplace.NonExternalisedLambdaFoundException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.model.KieModuleModel;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.time.SessionPseudoClock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * This test class enables CheckNonExternalisedLambda option
 * so it will throw NonExternalisedLambdaFoundException if non-externalized lambda is found.
 *
 * Note that other test classes don't throw NonExternalisedLambdaFoundException and proceed with non-externalized lambdas
 */
public class ExternalisedLambdaTest extends BaseModelTest {

    public ExternalisedLambdaTest(RUN_TYPE testRunType) {
        super(testRunType);
    }

    @Before
    public void init() {
        RuleWriter.setCheckNonExternalisedLambda(true);
    }

    @After
    public void clear() {
        RuleWriter.setCheckNonExternalisedLambda(false);
    }

    @Test
    public void testConsequenceNoVariable() throws Exception {
        // DROOLS-4924
        String str =
                "package defaultpkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "rule R when\n" +
                     "  $p : Person(name == \"Mario\")\n" +
                     "then\n" +
                     "  System.out.println(\"Hello\");\n" + // don't remove this line
                     "end";

        KieSession ksession = null;
        try {
            ksession = getKieSession(str);
        } catch (NonExternalisedLambdaFoundException e) {
            fail(e.getMessage());
        }

        Person me = new Person("Mario", 40);
        ksession.insert(me);

        assertEquals(1, ksession.fireAllRules());
    }

    @Test
    public void testExternalizeBindingVariableLambda() throws Exception {
        String str =
                "package defaultpkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "global java.util.List list;\n" +
                     "rule R when\n" +
                     "  $p : Person($n : name == \"Mario\")\n" +
                     "then\n" +
                     "  list.add($n);\n" +
                     "end";

        KieSession ksession = null;
        try {
            ksession = getKieSession(str);
        } catch (NonExternalisedLambdaFoundException e) {
            fail(e.getMessage());
        }

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Person me = new Person("Mario", 40);
        ksession.insert(me);
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Mario");
    }

    @Test
    public void testExternalizeLambdaPredicate() throws Exception {
        String str =
                "package defaultpkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";" +
                     "global java.util.List list;\n" +
                     "rule R when\n" +
                     "  $p : Person(name == \"Mario\")\n" +
                     "then\n" +
                     "  list.add($p.getName());\n" +
                     "end";

        KieSession ksession = null;
        try {
            ksession = getKieSession(str);
        } catch (NonExternalisedLambdaFoundException e) {
            fail(e.getMessage());
        }

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        Person me = new Person("Mario", 40);
        ksession.insert(me);
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Mario");
    }

    @Test
    public void testExternalizeLambdaUsingVariable() throws Exception {
        String str =
                "package defaultpkg;\n" +
                     "import " + Person.class.getCanonicalName() + ";\n" +
                     "global java.util.List list;\n" +
                     "rule R when\n" +
                     "  Integer( $i : intValue )\n" +
                     "  Person( age > $i )\n" +
                     "then\n" +
                     "  list.add($i);\n" +
                     "end";

        KieSession ksession = null;
        try {
            ksession = getKieSession(str);
        } catch (NonExternalisedLambdaFoundException e) {
            fail(e.getMessage());
        }

        final List<Integer> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        ksession.insert(43);
        ksession.insert(new Person("John", 44));
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder(43);
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

        KieSession ksession = null;
        try {
            ksession = getKieSession(str);
        } catch (NonExternalisedLambdaFoundException e) {
            fail(e.getMessage());
        }

        ksession.insert(new Person("Mario", 40));
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals("Mario", results.iterator().next().getValue());
    }

    @Test
    public void testFromExpression() {
        final String str =
                "import org.drools.modelcompiler.domain.*;\n" +
                           "global java.util.List list\n" +
                           "\n" +
                           "rule R when\n" +
                           " Man( $wife : wife )\n" +
                           " $child: Child( age > 10 ) from $wife.children\n" +
                           "then\n" +
                           "  list.add( $child.getName() );\n" +
                           "end\n";
        KieSession ksession = null;
        try {
            ksession = getKieSession(str);
        } catch (NonExternalisedLambdaFoundException e) {
            fail(e.getMessage());
        }

        final List<String> list = new ArrayList<>();
        ksession.setGlobal("list", list);

        final Woman alice = new Woman("Alice", 38);
        final Man bob = new Man("Bob", 40);
        bob.setWife(alice);

        final Child charlie = new Child("Charles", 12);
        final Child debbie = new Child("Debbie", 10);
        alice.addChild(charlie);
        alice.addChild(debbie);

        ksession.insert(bob);
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Charles");
    }

    @Test
    public void testAccumulateWithBinaryExpr() {
        String str =
                "import " + Person.class.getCanonicalName() + ";" +
                     "import " + Result.class.getCanonicalName() + ";" +
                     "rule X when\n" +
                     "  String( $l : length )" +
                     "  accumulate ( $p: Person ( getName().startsWith(\"M\")); \n" +
                     "                $sum : sum($p.getAge() * $l)  \n" +
                     "              )                          \n" +
                     "then\n" +
                     "  insert(new Result($sum));\n" +
                     "end";

        KieSession ksession = null;
        try {
            ksession = getKieSession(str);
        } catch (NonExternalisedLambdaFoundException e) {
            fail(e.getMessage());
        }

        ksession.insert("x");
        ksession.insert(new Person("Mark", 37));
        ksession.insert(new Person("Edson", 35));
        ksession.insert(new Person("Mario", 40));

        ksession.fireAllRules();

        Collection<Result> results = getObjectsIntoList(ksession, Result.class);
        assertEquals(1, results.size());
        assertEquals(77, ((Number) results.iterator().next().getValue()).intValue());
    }
  
    @Test
    public void testOOPath() {
        final String str =
                "import org.drools.modelcompiler.domain.*;\n" +
                "global java.util.List list\n" +
                "\n" +
                "rule R when\n" +
                " $man: Man( /wife/children[age > 10] )\n" +
                "then\n" +
                "  list.add( $man.getName() );\n" +
                "end\n";

        KieSession ksession = null;
        try {
            ksession = getKieSession(str);
        } catch (NonExternalisedLambdaFoundException e) {
            fail(e.getMessage());
        }

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        final Man carl = new Man( "Carl", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 10 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        ksession.insert( bob );
        ksession.insert( carl );
        ksession.fireAllRules();

        Assertions.assertThat(list).containsExactlyInAnyOrder("Bob");
    }

    @Test
    public void testCep() throws Exception {
        String str =
                "import " + StockTick.class.getCanonicalName() + ";" +
                "rule R when\n" +
                "    $a : StockTick( company == \"DROO\" )\n" +
                "    $b : StockTick( company == \"ACME\", timeFieldAsLong after[5,8] $a.timeFieldAsLong )\n" +
                "then\n" +
                "  System.out.println(\"fired\");\n" +
                "end\n";

        KieModuleModel kmodel = KieServices.get().newKieModuleModel();
        kmodel.newKieBaseModel( "kb" )
                .setDefault( true )
                .setEventProcessingMode( EventProcessingOption.STREAM )
                .newKieSessionModel( "ks" )
                .setDefault( true ).setClockType( ClockTypeOption.get( ClockType.PSEUDO_CLOCK.getId() ) );
        KieSession ksession = null;
        try {
            ksession = getKieSession(kmodel, str);
        } catch (NonExternalisedLambdaFoundException e) {
            fail(e.getMessage());
        }
        SessionPseudoClock clock = ksession.getSessionClock();

        ksession.insert( new StockTick( "DROO" ).setTimeField( 0 ) );
        ksession.insert( new StockTick( "ACME" ).setTimeField( 6 ) );

        assertEquals( 1, ksession.fireAllRules() );

        ksession.insert( new StockTick( "ACME" ).setTimeField( 10 ) );

        assertEquals( 0, ksession.fireAllRules() );
    }
}
