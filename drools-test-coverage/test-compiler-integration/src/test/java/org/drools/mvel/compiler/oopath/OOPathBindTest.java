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
package org.drools.mvel.compiler.oopath;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.drools.mvel.compiler.oopath.model.Child;
import org.drools.mvel.compiler.oopath.model.Man;
import org.drools.mvel.compiler.oopath.model.Toy;
import org.drools.mvel.compiler.oopath.model.Woman;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class OOPathBindTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public OOPathBindTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test
    public void testBindIntegerFireAllRules() throws InterruptedException, ExecutionException {
        testBindInteger(false);
    }

    @Test(timeout = 1000)
    public void testBindIntegerFireUntilHalt() throws InterruptedException, ExecutionException {
        testBindInteger(true);
    }

    public void testBindInteger(final boolean fireUntilHalt) throws InterruptedException, ExecutionException {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Adult( $age: /age )\n" +
                        "then\n" +
                        "  list.add( $age );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        Future fireUntilHaltFuture = null;
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        try {
            if (fireUntilHalt) {
                fireUntilHaltFuture = executorService.submit((Runnable) ksession::fireUntilHalt);
            }

            final List<Integer> list = new ArrayList<>();
            ksession.setGlobal( "list", list );

            final Man bob = new Man( "Bob", 40 );

            ksession.insert(bob);
            if (fireUntilHalt) {
                waitForResultAndStopFireUntilHalt(list, ksession, fireUntilHaltFuture);
            } else {
                ksession.fireAllRules();
                assertThat(list).hasSize(1);
            }
            assertThat(list).contains(40);
        } finally {
            executorService.shutdownNow();
            ksession.dispose();
        }
    }

    private void waitForResultAndStopFireUntilHalt(final List<Integer> resultList, final KieSession kieSession,
            final Future fireUntilHaltFuture) throws InterruptedException, ExecutionException {
        try {
            while (resultList.size() < 1) {
                Thread.sleep(100);
            }
        } finally {
            kieSession.halt();
            fireUntilHaltFuture.get();
        }
    }

    @Test
    public void testBindString() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Adult( $name: /name )\n" +
                        "then\n" +
                        "  list.add( $name );\n" +
                        "end\n";

        testScenarioBindString(drl, "Bob", "Alice");
    }

    @Test
    public void testBindStringWithConstraint() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Adult( $name: /name[this == \"Bob\"] )\n" +
                        "then\n" +
                        "  list.add( $name );\n" +
                        "end\n";

        testScenarioBindString(drl, "Bob");
    }

    @Test
    public void testBindStringWithAlphaConstraint() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Adult( $name: /name[this.length == 3, this != \"George\"] )\n" +
                        "then\n" +
                        "  list.add( $name );\n" +
                        "end\n";

        testScenarioBindString(drl, "Bob");
    }

    @Test
    public void testBindStringWithBetaConstraint() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  $alice: Adult(name == \"Alice\") \n" +
                        "  Adult( $name: /name[this.length == 3, this != $alice.name] )\n" +
                        "then\n" +
                        "  list.add( $name );\n" +
                        "end\n";

        testScenarioBindString(drl, "Bob");
    }

    private void testScenarioBindString(final String drl, final String... expectedResults) {
        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Man bob = new Man( "Bob", 40 );
        final Woman alice = new Woman( "Alice", 38 );

        ksession.insert(bob);
        ksession.insert(alice);
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder(expectedResults);
    }

    @Test
    public void testBindObjectFromList() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Adult( $child: /children )\n" +
                        "then\n" +
                        "  list.add( $child.getName() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<String> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Man bob = new Man( "Bob", 40 );
        bob.addChild( new Child( "Charles", 12 ) );
        bob.addChild( new Child( "Debbie", 8 ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder("Charles", "Debbie");
    }

    @Test
    public void testBindList() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toys: /wife/children.toys )\n" +
                        "then\n" +
                        "  list.add( $toys.size() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder(1, 2);
    }

    @Test
    public void testBindListWithConstraint() {
        final String drl =
                "import org.drools.mvel.compiler.oopath.model.*;\n" +
                        "global java.util.List list\n" +
                        "\n" +
                        "rule R when\n" +
                        "  Man( $toys: /wife/children[age > 10].toys )\n" +
                        "then\n" +
                        "  list.add( $toys.size() );\n" +
                        "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        final List<Integer> list = new ArrayList<>();
        ksession.setGlobal( "list", list );

        final Woman alice = new Woman( "Alice", 38 );
        final Man bob = new Man( "Bob", 40 );
        bob.setWife( alice );

        final Child charlie = new Child( "Charles", 12 );
        final Child debbie = new Child( "Debbie", 8 );
        alice.addChild( charlie );
        alice.addChild( debbie );

        charlie.addToy( new Toy( "car" ) );
        charlie.addToy( new Toy( "ball" ) );
        debbie.addToy( new Toy( "doll" ) );

        ksession.insert( bob );
        ksession.fireAllRules();

        assertThat(list).containsExactlyInAnyOrder(2);
    }
}
