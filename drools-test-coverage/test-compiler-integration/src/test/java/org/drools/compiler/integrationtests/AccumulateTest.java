/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests;

import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.assertj.core.api.Assertions;
import org.drools.compiler.integrationtests.incrementalcompilation.TestUtil;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.core.command.runtime.rule.InsertElementsCommand;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.testcoverage.common.model.Cheese;
import org.drools.testcoverage.common.model.Cheesery;
import org.drools.testcoverage.common.model.Order;
import org.drools.testcoverage.common.model.OrderItem;
import org.drools.testcoverage.common.model.OuterClass;
import org.drools.testcoverage.common.model.Person;
import org.drools.testcoverage.common.model.StockTick;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.Message;
import org.kie.api.builder.ReleaseId;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AccumulateFunction;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.Match;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.Variable;
import org.kie.internal.utils.KieHelper;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import static java.util.Arrays.asList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(Parameterized.class)
public class AccumulateTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AccumulateTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    @Test(timeout = 10000)
    public void testAccumulateModify() {

        final String drl = "package org.drools.compiler.integrationtests;\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Constraints everywhere\" salience 80\n" +
                "    when\n" +
                "        $person      : Person( $likes : likes )\n" +
                "        $cheesery    : Cheesery( totalAmount > 20 )\n" +
                "                               from accumulate( $cheese : Cheese( type == $likes ),\n" +
                "                                                init( Cheesery cheesery = new Cheesery(); ),\n" +
                "                                                action( cheesery.addCheese( $cheese ); ),\n" +
                "                                                result( cheesery ) );\n" +
                "    then\n" +
                "        results.add( $cheesery );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();
            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 10),
                    new Cheese("stilton", 2),
                    new Cheese("stilton", 5),
                    new Cheese("brie", 15),
                    new Cheese("brie", 16),
                    new Cheese("provolone", 8)};

            final Person bob = new Person("Bob", "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }
            final org.kie.api.runtime.rule.FactHandle bobHandle = wm.insert(bob);

            // ---------------- 1st scenario
            wm.fireAllRules();
            // no fire, as per rule constraints
            assertEquals(0, results.size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            wm.update(cheeseHandles[index], cheese[index]);
            wm.fireAllRules();

            // 1 fire
            assertEquals(1, results.size());
            assertEquals(24, ((Cheesery) results.get(results.size() - 1)).getTotalAmount());

            // ---------------- 3rd scenario
            bob.setLikes("brie");
            wm.update(bobHandle, bob);
            wm.fireAllRules();

            // 2 fires
            assertEquals(2, results.size());
            assertEquals(31, ((Cheesery) results.get(results.size() - 1)).getTotalAmount());

            // ---------------- 4th scenario
            wm.delete(cheeseHandles[3]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(2, results.size());
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulate() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           "org/drools/compiler/integrationtests/test_Accumulate.drl");
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();
            wm.setGlobal("results",
                         results);

            wm.insert(new Person("Bob",
                                 "stilton",
                                 20));
            wm.insert(new Person("Mark",
                                 "provolone"));
            wm.insert(new Cheese("stilton",
                                 10));
            wm.insert(new Cheese("brie",
                                 5));
            wm.insert(new Cheese("provolone",
                                 150));

            wm.fireAllRules();

            System.out.println(results);

            assertEquals(5,
                         results.size());

            assertEquals(165, results.get(0));
            assertEquals(10, results.get(1));
            assertEquals(150, results.get(2));
            assertEquals(10, results.get(3));
            assertEquals(210, results.get(4));
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateModifyMVEL() {

        final String drl = "package org.drools.compiler.test;\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Constraints everywhere\" salience 80\n" +
                "    dialect \"mvel\"\n" +
                "    when\n" +
                "        $person      : Person( $likes : likes )\n" +
                "        $cheesery    : Cheesery( totalAmount > 20 )\n" +
                "                               from accumulate( $cheese : Cheese( type == $likes ),\n" +
                "                                                init( cheesery = new Cheesery(); ),\n" +
                "                                                action( cheesery.addCheese( $cheese ); ),\n" +
                "                                                result( cheesery ) );\n" +
                "    then\n" +
                "        results.add( $cheesery );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();
            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 10), new Cheese("stilton", 2),
                    new Cheese("stilton", 5),
                    new Cheese("brie", 15),
                    new Cheese("brie", 16),
                    new Cheese("provolone", 8)};
            final Person bob = new Person("Bob", "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }
            final org.kie.api.runtime.rule.FactHandle bobHandle = wm.insert(bob);

            // ---------------- 1st scenario
            wm.fireAllRules();
            // no fire, as per rule constraints
            assertEquals(0,
                         results.size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            wm.update(cheeseHandles[index],
                      cheese[index]);
            wm.fireAllRules();

            // 1 fire
            assertEquals(1,
                         results.size());
            assertEquals(24,
                         ((Cheesery) results.get(results.size() - 1)).getTotalAmount());

            // ---------------- 3rd scenario
            bob.setLikes("brie");
            wm.update(bobHandle,
                      bob);
            wm.fireAllRules();

            // 2 fires
            assertEquals(2,
                         results.size());
            assertEquals(31,
                         ((Cheesery) results.get(results.size() - 1)).getTotalAmount());

            // ---------------- 4th scenario
            wm.delete(cheeseHandles[3]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(2,
                         results.size());
        } finally {
            wm.dispose();
        }

    }

    @Test(timeout = 10000)
    public void testAccumulateReverseModify() {

        final String drl = "package org.drools.compiler.test;\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Constraints everywhere\" salience 80\n" +
                "    when\n" +
                "        $person      : Person( $likes : likes )\n" +
                "        $cheesery    : Cheesery( totalAmount > 20 )\n" +
                "                               from accumulate( $cheese : Cheese( type == $likes ),\n" +
                "                                                init( Cheesery cheesery = new Cheesery(); ),\n" +
                "                                                action( cheesery.addCheese( $cheese ); ),\n" +
                "                                                reverse( cheesery.removeCheese( $cheese ); ),\n" +
                "                                                result( cheesery ) );\n" +
                "    then\n" +
                "        //System.out.println($person.getName() +\" is spending a lot buying cheese ( US$ \"+$cheesery.getTotalAmount()+\" )!\");\n" +
                "        results.add( $cheesery );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();
            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 10),
                    new Cheese("stilton", 2),
                    new Cheese("stilton", 5),
                    new Cheese("brie", 15),
                    new Cheese("brie", 16),
                    new Cheese("provolone", 8)};
            final Person bob = new Person("Bob",
                                          "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }
            final org.kie.api.runtime.rule.FactHandle bobHandle = wm.insert(bob);

            // ---------------- 1st scenario
            wm.fireAllRules();
            // no fire, as per rule constraints
            assertEquals(0,
                         results.size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            wm.update(cheeseHandles[index],
                      cheese[index]);
            wm.fireAllRules();

            // 1 fire
            assertEquals(1,
                         results.size());
            assertEquals(24,
                         ((Cheesery) results.get(results.size() - 1)).getTotalAmount());

            // ---------------- 3rd scenario
            bob.setLikes("brie");
            wm.update(bobHandle,
                      bob);
            cheese[3].setPrice(20);
            wm.update(cheeseHandles[3],
                      cheese[3]);
            wm.fireAllRules();

            // 2 fires
            assertEquals(2,
                         results.size());
            assertEquals(36,
                         ((Cheesery) results.get(results.size() - 1)).getTotalAmount());

            // ---------------- 4th scenario
            wm.delete(cheeseHandles[3]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(2,
                         results.size());
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateReverseModify2() {

        final String drl = "package org.drools.compiler.test;\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Constraints everywhere\" salience 80\n" +
                "    when\n" +
                "        $person      : Person( $likes : likes )\n" +
                "        $total       : Number( intValue > 20 )\n" +
                "                               from accumulate( Cheese( type == $likes, $p : price ),\n" +
                "                                                init( int total = 0; ),\n" +
                "                                                action( total += $p; ),\n" +
                "                                                reverse( total -= $p; ),\n" +
                "                                                result( new Integer( total ) ) )\n" +
                "    then\n" +
                "        results.add( $total );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 10),
                    new Cheese("stilton", 2),
                    new Cheese("stilton", 5),
                    new Cheese("brie", 15),
                    new Cheese("brie", 16),
                    new Cheese("provolone", 8)};
            final Person bob = new Person("Bob",
                                          "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }
            final FactHandle bobHandle = wm.insert(bob);

            // ---------------- 1st scenario
            wm.fireAllRules();
            // no fire, as per rule constraints
            assertEquals(0,
                         results.size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            wm.update(cheeseHandles[index],
                      cheese[index]);
            wm.fireAllRules();

            // 1 fire
            assertEquals(1,
                         results.size());
            assertEquals(24,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 3rd scenario
            bob.setLikes("brie");
            wm.update(bobHandle,
                      bob);
            cheese[3].setPrice(20);
            wm.update(cheeseHandles[3],
                      cheese[3]);
            wm.fireAllRules();

            // 2 fires
            assertEquals(2,
                         results.size());
            assertEquals(36,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 4th scenario
            wm.delete(cheeseHandles[3]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(2,
                         results.size());
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateReverseModifyInsertLogical2() {

        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           "org/drools/compiler/integrationtests/test_AccumulateReverseModifyInsertLogical2.drl");
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 10),
                    new Cheese("stilton", 2),
                    new Cheese("stilton", 5),
                    new Cheese("brie", 15),
                    new Cheese("brie", 16),
                    new Cheese("provolone", 8)
            };
            final Person alice = new Person("Alice", "brie");
            final Person bob = new Person("Bob", "stilton");
            final Person doug = new Person("Doug", "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }
            wm.insert(alice);
            wm.insert(bob);
            wm.insert(doug); // should be ignored

            // alice = 31, bob = 17, doug = 17
            // !alice = 34, !bob = 31, !doug = 31
            wm.fireAllRules();
            assertEquals(31, ((Number) results.get(results.size() - 1)).intValue());

            // delete stilton=2 ==> bob = 15, doug = 15, !alice = 30
            wm.delete(cheeseHandles[1]);
            wm.fireAllRules();
            assertEquals(30, ((Number) results.get(results.size() - 1)).intValue());
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateReverseModifyMVEL() {

        final String drl = "package org.drools.compiler.test;\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Constraints everywhere\" salience 80\n" +
                "    dialect \"mvel\"\n" +
                "    when\n" +
                "        $person      : Person( $likes : likes )\n" +
                "        $cheesery    : Cheesery( totalAmount > 20 )\n" +
                "                               from accumulate( $cheese : Cheese( type == $likes ),\n" +
                "                                                init( cheesery = new Cheesery(); ),\n" +
                "                                                action( cheesery.addCheese( $cheese ); ),\n" +
                "                                                reverse( cheesery.removeCheese( $cheese ); ),\n" +
                "                                                result( cheesery ) );\n" +
                "    then\n" +
                "        results.add( $cheesery );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 10),
                    new Cheese("stilton", 2),
                    new Cheese("stilton", 5),
                    new Cheese("brie", 15),
                    new Cheese("brie", 16),
                    new Cheese("provolone", 8)};
            final Person bob = new Person("Bob",
                                          "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }
            final FactHandle bobHandle = wm.insert(bob);

            // ---------------- 1st scenario
            wm.fireAllRules();
            // no fire, as per rule constraints
            assertEquals(0,
                         results.size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            wm.update(cheeseHandles[index],
                      cheese[index]);
            wm.fireAllRules();

            // 1 fire
            assertEquals(1,
                         results.size());
            assertEquals(24,
                         ((Cheesery) results.get(results.size() - 1)).getTotalAmount());

            // ---------------- 3rd scenario
            bob.setLikes("brie");
            wm.update(bobHandle,
                      bob);
            wm.fireAllRules();

            // 2 fires
            assertEquals(2,
                         results.size());
            assertEquals(31,
                         ((Cheesery) results.get(results.size() - 1)).getTotalAmount());

            // ---------------- 4th scenario
            wm.delete(cheeseHandles[3]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(2,
                         results.size());
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateReverseModifyMVEL2() {

        final String drl = "package org.drools.compiler.test;\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Constraints everywhere\" salience 80\n" +
                "    dialect \"mvel\"\n" +
                "    when\n" +
                "        $person      : Person( $likes : likes )\n" +
                "        $total       : Number( intValue > 20 )\n" +
                "                               from accumulate( $cheese : Cheese( type == $likes, $p : price ),\n" +
                "                                                init( int total = 0; ),\n" +
                "                                                action( total += $p; ),\n" +
                "                                                reverse( total -= $p; ),\n" +
                "                                                result( new Integer( total ) ) )\n" +
                "    then\n" +
                "        results.add( $total );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 10),
                    new Cheese("stilton", 2),
                    new Cheese("stilton", 5),
                    new Cheese("brie", 15),
                    new Cheese("brie", 16),
                    new Cheese("provolone", 8)};
            final Person bob = new Person("Bob",
                                          "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }
            final FactHandle bobHandle = wm.insert(bob);

            // ---------------- 1st scenario
            wm.fireAllRules();
            // no fire, as per rule constraints
            assertEquals(0,
                         results.size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            wm.update(cheeseHandles[index],
                      cheese[index]);
            wm.fireAllRules();

            // 1 fire
            assertEquals(1,
                         results.size());
            assertEquals(24,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 3rd scenario
            bob.setLikes("brie");
            wm.update(bobHandle,
                      bob);
            wm.fireAllRules();

            // 2 fires
            assertEquals(2,
                         results.size());
            assertEquals(31,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 4th scenario
            wm.delete(cheeseHandles[3]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(2,
                         results.size());
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateWithFromChaining() {

        final String drl = "package org.drools.compiler\n" +
                "\n" +
                "import java.util.List;\n" +
                "import java.util.ArrayList;\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "import " + Cheesery.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Accumulate with From Chaining\" salience 80\n" +
                "    when\n" +
                "        $cheesery : Cheesery()\n" +
                "        $person   : Person( $likes : likes )\n" +
                "        $list     : List( size > 2 )\n" +
                "                               from accumulate( $cheese : Cheese( type == $likes  ) from $cheesery.getCheeses(),\n" +
                "                                                init( List l = new ArrayList(); ),\n" +
                "                                                action( l.add( $cheese ); )\n" +
                "                                                result( l ) )\n" +
                "    then\n" +
                "        results.add( $list );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 8),
                    new Cheese("stilton", 10),
                    new Cheese("stilton", 9),
                    new Cheese("brie", 4),
                    new Cheese("brie", 1),
                    new Cheese("provolone", 8)};

            final Cheesery cheesery = new Cheesery();

            for (final Cheese aCheese : cheese) {
                cheesery.addCheese(aCheese);
            }

            final FactHandle cheeseryHandle = wm.insert(cheesery);

            final Person bob = new Person("Bob",
                                          "stilton");

            final FactHandle bobHandle = wm.insert(bob);

            // ---------------- 1st scenario
            wm.fireAllRules();
            // one fire, as per rule constraints
            assertEquals(1,
                         results.size());
            assertEquals(3,
                         ((List) results.get(results.size() - 1)).size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setType("brie");
            wm.update(cheeseryHandle,
                      cheesery);
            wm.fireAllRules();

            // no fire
            assertEquals(1,
                         results.size());
            System.out.println(results);

            // ---------------- 3rd scenario
            bob.setLikes("brie");
            wm.update(bobHandle,
                      bob);
            wm.fireAllRules();

            // 2 fires
            assertEquals(2,
                         results.size());
            assertEquals(3,
                         ((List) results.get(results.size() - 1)).size());

            // ---------------- 4th scenario
            cheesery.getCheeses().remove(cheese[3]);
            wm.update(cheeseryHandle,
                      cheesery);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(2,
                         results.size());
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateInnerClass() {

        final String drl = "package org.drools.compiler\n" +
                "\n" +
                "import " + OuterClass.InnerClass.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Accumulate Inner Class\" \n" +
                "    when\n" +
                "        $totalAmount : Number() from accumulate( $inner : InnerClass( $val : intAttr ),\n" +
                "                                                  init( int total = 0; ),\n" +
                "                                                  action( total += $val; ),\n" +
                "                                                  result( new Integer( total ) ) );\n" +
                "    then\n" +
                "        //System.out.println(\"Total amount = US$ \"+$totalAmount );\n" +
                "        results.add($totalAmount);\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            wm.insert(new OuterClass.InnerClass(10));
            wm.insert(new OuterClass.InnerClass(5));

            wm.fireAllRules();

            assertEquals(15, results.get(0));
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateReturningNull() {

        final String drl = "package org.drools.compiler\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Accumulate Returning Null\" salience 100\n" +
                "    when\n" +
                "        // emulating a null return value for accumulate\n" +
                "        $totalAmount : Number() from accumulate( Cheese( $price : price ),\n" +
                "                                                 init( ),\n" +
                "                                                 action( ),\n" +
                "                                                 result( null ) );\n" +
                "    then\n" +
                "        //System.out.println(\"Total amount = US$ \"+$totalAmount );\n" +
                "        results.add($totalAmount);\n" +
                "end ";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();
            wm.setGlobal("results", results);
            wm.insert(new Cheese("stilton", 10));
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateSumJava() {
        execTestAccumulateSum("org/drools/compiler/integrationtests/test_AccumulateSum.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateSumMVEL() {
        execTestAccumulateSum("org/drools/compiler/integrationtests/test_AccumulateSumMVEL.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateMultiPatternWithFunctionJava() {
        execTestAccumulateSum("org/drools/compiler/integrationtests/test_AccumulateMultiPatternFunctionJava.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateMultiPatternWithFunctionMVEL() {
        execTestAccumulateSum("org/drools/compiler/integrationtests/test_AccumulateMultiPatternFunctionMVEL.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateCountJava() {
        execTestAccumulateCount("org/drools/compiler/integrationtests/test_AccumulateCount.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateCountMVEL() {
        execTestAccumulateCount("org/drools/compiler/integrationtests/test_AccumulateCountMVEL.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateAverageJava() {
        execTestAccumulateAverage("org/drools/compiler/integrationtests/test_AccumulateAverage.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateAverageMVEL() {
        execTestAccumulateAverage("org/drools/compiler/integrationtests/test_AccumulateAverageMVEL.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateMinJava() {
        execTestAccumulateMin("org/drools/compiler/integrationtests/test_AccumulateMin.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateMinMVEL() {
        execTestAccumulateMin("org/drools/compiler/integrationtests/test_AccumulateMinMVEL.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateMaxJava() {
        execTestAccumulateMax("org/drools/compiler/integrationtests/test_AccumulateMax.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateMaxMVEL() {
        execTestAccumulateMax("org/drools/compiler/integrationtests/test_AccumulateMaxMVEL.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateMultiPatternJava() {
        execTestAccumulateReverseModifyMultiPattern("org/drools/compiler/integrationtests/test_AccumulateMultiPattern.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateMultiPatternMVEL() {
        execTestAccumulateReverseModifyMultiPattern("org/drools/compiler/integrationtests/test_AccumulateMultiPatternMVEL.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateCollectListJava() {
        execTestAccumulateCollectList("org/drools/compiler/integrationtests/test_AccumulateCollectList.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateCollectListMVEL() {
        execTestAccumulateCollectList("org/drools/compiler/integrationtests/test_AccumulateCollectListMVEL.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateCollectSetJava() {
        execTestAccumulateCollectSet("org/drools/compiler/integrationtests/test_AccumulateCollectSet.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateCollectSetMVEL() {
        execTestAccumulateCollectSet("org/drools/compiler/integrationtests/test_AccumulateCollectSetMVEL.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateMultipleFunctionsJava() {
        execTestAccumulateMultipleFunctions("org/drools/compiler/integrationtests/test_AccumulateMultipleFunctions.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateMultipleFunctionsMVEL() {
        execTestAccumulateMultipleFunctions("org/drools/compiler/integrationtests/test_AccumulateMultipleFunctionsMVEL.drl");
    }

    @Test(timeout = 10000)
    public void testAccumulateMultipleFunctionsConstraint() {
        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           "org/drools/compiler/integrationtests/test_AccumulateMultipleFunctionsConstraint.drl");
        final KieSession ksession = kbase.newKieSession();
        try {
            final AgendaEventListener ael = mock(AgendaEventListener.class);
            ksession.addEventListener(ael);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 10),
                    new Cheese("stilton", 3),
                    new Cheese("stilton", 5),
                    new Cheese("brie", 3),
                    new Cheese("brie", 17),
                    new Cheese("provolone", 8)};
            final Person bob = new Person("Bob",
                                          "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = ksession.insert(cheese[i]);
            }
            final FactHandle bobHandle = ksession.insert(bob);

            // ---------------- 1st scenario
            ksession.fireAllRules();

            final ArgumentCaptor<AfterMatchFiredEvent> cap = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
            Mockito.verify(ael).afterMatchFired(cap.capture());

            Match activation = cap.getValue().getMatch();
            assertThat(((Number) activation.getDeclarationValue("$sum")).intValue(),
                       is(18));
            assertThat(((Number) activation.getDeclarationValue("$min")).intValue(),
                       is(3));
            assertThat(((Number) activation.getDeclarationValue("$avg")).intValue(),
                       is(6));

            Mockito.reset(ael);
            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            ksession.update(cheeseHandles[index],
                            cheese[index]);
            ksession.fireAllRules();

            Mockito.verify(ael, Mockito.never()).afterMatchFired(Mockito.any(AfterMatchFiredEvent.class));

            Mockito.reset(ael);
            // ---------------- 3rd scenario
            bob.setLikes("brie");
            ksession.update(bobHandle,
                            bob);
            ksession.fireAllRules();

            Mockito.verify(ael).afterMatchFired(cap.capture());

            activation = cap.getValue().getMatch();
            assertThat(((Number) activation.getDeclarationValue("$sum")).intValue(),
                       is(20));
            assertThat(((Number) activation.getDeclarationValue("$min")).intValue(),
                       is(3));
            assertThat(((Number) activation.getDeclarationValue("$avg")).intValue(),
                       is(10));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateWithAndOrCombinations() {
        // JBRULES-3482
        // once this compils, update it to actually assert on correct outputs.

        final String drl = "package org.drools.compiler.test;\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"Class cast causer\"\n" +
                "    when\n" +
                "        $person      : Person( $likes : likes )\n" +
                "        $total       : Number() from accumulate( $p : Person(likes != $likes, $l : likes) and $c : Cheese( type == $l ),\n" +
                "                                                min($c.getPrice()) )\n" +
                "        ($p2 : Person(name == 'nobody') or $p2 : Person(name == 'Doug'))\n" +
                "    then\n" +
                "        System.out.println($p2.getName());\n" +
                "end\n";
        // read in the source

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration, drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.insert(new Cheese("stilton", 10));
            ksession.insert(new Person("Alice", "brie"));
            ksession.insert(new Person("Bob", "stilton"));
        } finally {
            ksession.dispose();
        }
    }

    private void execTestAccumulateSum(final String fileName) {

        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           fileName);
        final KieSession session = kbase.newKieSession();
        try {
            final DataSet data = new DataSet();
            data.results = new ArrayList<>();

            session.setGlobal("results", data.results);

            data.cheese = new Cheese[]{
                    new Cheese("stilton", 8, 0),
                    new Cheese("stilton", 10, 1),
                    new Cheese("stilton", 9, 2),
                    new Cheese("brie", 11, 3),
                    new Cheese("brie", 4, 4),
                    new Cheese("provolone", 8, 5)};
            data.bob = new Person("Bob",
                                  "stilton");

            data.cheeseHandles = new FactHandle[data.cheese.length];
            for (int i = 0; i < data.cheese.length; i++) {
                data.cheeseHandles[i] = session.insert(data.cheese[i]);
            }
            data.bobHandle = session.insert(data.bob);

            // ---------------- 1st scenario
            session.fireAllRules();
            assertEquals(1,
                         data.results.size());
            assertEquals(27,
                         ((Number) data.results.get(data.results.size() - 1)).intValue());

            updateReferences(session,
                             data);

            // ---------------- 2nd scenario
            final int index = 1;
            data.cheese[index].setPrice(3);
            session.update(data.cheeseHandles[index],
                           data.cheese[index]);
            final int count = session.fireAllRules();
            assertEquals(1, count);

            assertEquals(2,
                         data.results.size());
            assertEquals(20,
                         ((Number) data.results.get(data.results.size() - 1)).intValue());

            // ---------------- 3rd scenario
            data.bob.setLikes("brie");
            session.update(data.bobHandle,
                           data.bob);
            session.fireAllRules();

            assertEquals(3,
                         data.results.size());
            assertEquals(15,
                         ((Number) data.results.get(data.results.size() - 1)).intValue());

            // ---------------- 4th scenario
            session.delete(data.cheeseHandles[3]);
            session.fireAllRules();

            // should not have fired as per constraint
            assertEquals(3,
                         data.results.size());
        } finally {
            session.dispose();
        }
    }

    private void updateReferences(final KieSession session,
                                  final DataSet data) {
        data.results = (List<?>) session.getGlobal("results");
        for (final Object next : session.getObjects()) {
            if (next instanceof Cheese) {
                final Cheese c = (Cheese) next;
                data.cheese[c.getOldPrice()] = c;
                data.cheeseHandles[c.getOldPrice()] = session.getFactHandle(c);
                assertNotNull(data.cheeseHandles[c.getOldPrice()]);
            } else if (next instanceof Person) {
                data.bob = (Person) next;
                data.bobHandle = session.getFactHandle(data.bob);
            }
        }
    }

    private void execTestAccumulateCount(final String fileName) {

        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           fileName);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 8),
                    new Cheese("stilton", 10),
                    new Cheese("stilton", 9),
                    new Cheese("brie", 4),
                    new Cheese("brie", 1),
                    new Cheese("provolone", 8)};
            final Person bob = new Person("Bob",
                                          "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }
            final FactHandle bobHandle = wm.insert(bob);

            // ---------------- 1st scenario
            wm.fireAllRules();
            // no fire, as per rule constraints
            assertEquals(1,
                         results.size());
            assertEquals(3,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(3);
            wm.update(cheeseHandles[index],
                      cheese[index]);
            wm.fireAllRules();

            // 1 fire
            assertEquals(2,
                         results.size());
            assertEquals(3,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 3rd scenario
            bob.setLikes("brie");
            wm.update(bobHandle,
                      bob);
            wm.fireAllRules();

            // 2 fires
            assertEquals(3,
                         results.size());
            assertEquals(2,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 4th scenario
            wm.delete(cheeseHandles[3]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(3,
                         results.size());
        } finally {
            wm.dispose();
        }
    }

    private void execTestAccumulateAverage(final String fileName) {

        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           fileName);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 10),
                    new Cheese("stilton", 2),
                    new Cheese("stilton", 11),
                    new Cheese("brie", 15),
                    new Cheese("brie", 17),
                    new Cheese("provolone", 8)};
            final Person bob = new Person("Bob",
                                          "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }
            final FactHandle bobHandle = wm.insert(bob);

            // ---------------- 1st scenario
            wm.fireAllRules();
            // no fire, as per rule constraints
            assertEquals(0,
                         results.size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            wm.update(cheeseHandles[index],
                      cheese[index]);
            wm.fireAllRules();

            // 1 fire
            assertEquals(1,
                         results.size());
            assertEquals(10,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 3rd scenario
            bob.setLikes("brie");
            wm.update(bobHandle,
                      bob);
            wm.fireAllRules();

            // 2 fires
            assertEquals(2,
                         results.size());
            assertEquals(16,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 4th scenario
            wm.delete(cheeseHandles[3]);
            wm.delete(cheeseHandles[4]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(2,
                         results.size());
        } finally {
            wm.dispose();
        }
    }

    private void execTestAccumulateMin(final String fileName) {

        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           fileName);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 8),
                    new Cheese("stilton", 10),
                    new Cheese("stilton", 9),
                    new Cheese("brie", 4),
                    new Cheese("brie", 1),
                    new Cheese("provolone", 8)};
            final Person bob = new Person("Bob",
                                          "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }
            final FactHandle bobHandle = wm.insert(bob);

            // ---------------- 1st scenario
            wm.fireAllRules();
            // no fire, as per rule constraints
            assertEquals(0,
                         results.size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(3);
            wm.update(cheeseHandles[index],
                      cheese[index]);
            wm.fireAllRules();

            // 1 fire
            assertEquals(1,
                         results.size());
            assertEquals(3,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 3rd scenario
            bob.setLikes("brie");
            wm.update(bobHandle,
                      bob);
            wm.fireAllRules();

            // 2 fires
            assertEquals(2,
                         results.size());
            assertEquals(1,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 4th scenario
            wm.delete(cheeseHandles[3]);
            wm.delete(cheeseHandles[4]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(2,
                         results.size());
        } finally {
            wm.dispose();
        }
    }

    private void execTestAccumulateMax(final String fileName) {

        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           fileName);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 4),
                    new Cheese("stilton", 2),
                    new Cheese("stilton", 3),
                    new Cheese("brie", 15),
                    new Cheese("brie", 17),
                    new Cheese("provolone", 8)};
            final Person bob = new Person("Bob",
                                          "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }
            final FactHandle bobHandle = wm.insert(bob);

            // ---------------- 1st scenario
            wm.fireAllRules();
            // no fire, as per rule constraints
            assertEquals(0,
                         results.size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            wm.update(cheeseHandles[index],
                      cheese[index]);
            wm.fireAllRules();

            // 1 fire
            assertEquals(1,
                         results.size());
            assertEquals(9,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 3rd scenario
            bob.setLikes("brie");
            wm.update(bobHandle,
                      bob);
            wm.fireAllRules();

            // 2 fires
            assertEquals(2,
                         results.size());
            assertEquals(17,
                         ((Number) results.get(results.size() - 1)).intValue());

            // ---------------- 4th scenario
            wm.delete(cheeseHandles[3]);
            wm.delete(cheeseHandles[4]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(2,
                         results.size());
        } finally {
            wm.dispose();
        }
    }

    private void execTestAccumulateCollectList(final String fileName) {

        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           fileName);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 4),
                    new Cheese("stilton", 2),
                    new Cheese("stilton", 3),
                    new Cheese("brie", 15),
                    new Cheese("brie", 17),
                    new Cheese("provolone", 8)};
            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }

            // ---------------- 1st scenario
            wm.fireAllRules();
            assertEquals(1,
                         results.size());
            assertEquals(6,
                         ((List) results.get(results.size() - 1)).size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            wm.update(cheeseHandles[index],
                      cheese[index]);
            wm.fireAllRules();

            // fire again
            assertEquals(2,
                         results.size());
            assertEquals(6,
                         ((List) results.get(results.size() - 1)).size());

            // ---------------- 3rd scenario
            wm.delete(cheeseHandles[3]);
            wm.delete(cheeseHandles[4]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(2,
                         results.size());
        } finally {
            wm.dispose();
        }
    }

    private void execTestAccumulateCollectSet(final String fileName) {

        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           fileName);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 4),
                    new Cheese("stilton", 2),
                    new Cheese("stilton", 3),
                    new Cheese("brie", 15),
                    new Cheese("brie", 17),
                    new Cheese("provolone", 8)};
            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }

            // ---------------- 1st scenario
            wm.fireAllRules();
            assertEquals(1,
                         results.size());
            assertEquals(3,
                         ((Set) results.get(results.size() - 1)).size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            wm.update(cheeseHandles[index],
                      cheese[index]);
            wm.fireAllRules();

            // fire again
            assertEquals(2,
                         results.size());
            assertEquals(3,
                         ((Set) results.get(results.size() - 1)).size());

            // ---------------- 3rd scenario
            wm.delete(cheeseHandles[3]);
            wm.fireAllRules();
            // fire again
            assertEquals(3,
                         results.size());
            assertEquals(3,
                         ((Set) results.get(results.size() - 1)).size());

            // ---------------- 4rd scenario
            wm.delete(cheeseHandles[4]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(3,
                         results.size());
        } finally {
            wm.dispose();
        }
    }

    private void execTestAccumulateReverseModifyMultiPattern(final String fileName) {

        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           fileName);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            final Cheese[] cheese = new Cheese[]{new Cheese("stilton", 10),
                    new Cheese("stilton", 2),
                    new Cheese("stilton", 5),
                    new Cheese("brie", 15),
                    new Cheese("brie", 16),
                    new Cheese("provolone", 8)};

            final Person bob = new Person("Bob",
                                          "stilton");
            final Person mark = new Person("Mark",
                                           "provolone");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = wm.insert(cheese[i]);
            }
            final FactHandle bobHandle = wm.insert(bob);
            wm.insert(mark);

            // ---------------- 1st scenario
            wm.fireAllRules();
            // no fire, as per rule constraints
            assertEquals(0,
                         results.size());

            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            wm.update(cheeseHandles[index],
                      cheese[index]);
            wm.fireAllRules();

            // 1 fire
            assertEquals(1,
                         results.size());
            assertEquals(32,
                         ((Cheesery) results.get(results.size() - 1)).getTotalAmount());

            // ---------------- 3rd scenario
            bob.setLikes("brie");
            wm.update(bobHandle,
                      bob);
            wm.fireAllRules();

            // 2 fires
            assertEquals(2,
                         results.size());
            assertEquals(39,
                         ((Cheesery) results.get(results.size() - 1)).getTotalAmount());

            // ---------------- 4th scenario
            wm.delete(cheeseHandles[3]);
            wm.fireAllRules();

            // should not have fired as per constraint
            assertEquals(2,
                         results.size());
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateWithPreviouslyBoundVariables() {

        final String drl = "package org.drools.compiler\n" +
                "\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Accumulate with bound var \" salience 100\n" +
                "    when\n" +
                "        Cheese( type == \"stilton\", $price : price )\n" +
                "        $totalAmount : Number() from accumulate(  $c : Cheese( type == \"brie\" ),\n" +
                "                                                  init( int total = 0; ),\n" +
                "                                                  action( total += $c.getPrice() + $price; ),\n" +
                "                                                  reverse( total -= $c.getPrice() + $price; ),\n" +
                "                                                  result( new Integer( total ) ) );\n" +
                "    then\n" +
                "        //System.out.println(\"Total amount = US$ \"+$totalAmount );\n" +
                "        results.add($totalAmount);\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);

            wm.insert(new Cheese("stilton",
                                 10));
            wm.insert(new Cheese("brie",
                                 5));
            wm.insert(new Cheese("provolone",
                                 150));
            wm.insert(new Cheese("brie",
                                 20));

            wm.fireAllRules();

            assertEquals(1,
                         results.size());
            assertEquals(45,
                         results.get(0));
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateMVELWithModify() {

        final String drl = "package org.drools.compiler\n" +
                "\n" +
                "import " + Order.class.getCanonicalName() + ";\n" +
                "import " + OrderItem.class.getCanonicalName() + ";\n" +
                "\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Accumulate with modify\" \n" +
                "        dialect \"mvel\"\n" +
                "    when\n" +
                "        $o : Order( total == 0 )\n" +
                "        Number( $total : doubleValue ) from accumulate(\n" +
                "              OrderItem( order == $o, $p : price ),\n" +
                "              sum( $p ) )\n" +
                "    then\n" +
                "        modify( $o ) { total = $total }\n" +
                "        results.add( $total );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<Number> results = new ArrayList<>();
            wm.setGlobal("results",
                         results);

            final Order order = new Order(1,
                                          "Bob");
            final OrderItem item1 = new OrderItem(order,
                                                  1,
                                                  "maquilage",
                                                  1,
                                                  10);
            final OrderItem item2 = new OrderItem(order,
                                                  2,
                                                  "perfume",
                                                  1,
                                                  5);
            order.addItem(item1);
            order.addItem(item2);

            wm.insert(order);
            wm.insert(item1);
            wm.insert(item2);
            wm.fireAllRules();

            assertEquals(1,
                         results.size());
            assertEquals(15,
                         results.get(0).intValue());
            assertEquals(15.0,
                         order.getTotal(),
                         0.0);
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateGlobals() {
        final String drl = "package org.drools.compiler\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "global Integer globalValue;\n" +
                "\n" +
                "rule \"Accumulate globals\"\n" +
                "    when\n" +
                "        $totalAmount : Number() from accumulate(  Cheese( type == \"brie\" ),\n" +
                "                                                  init( int total = 0; ),\n" +
                "                                                  action( total += globalValue.intValue(); ),\n" +
                "                                                  reverse( total -= globalValue.intValue(); ),\n" +
                "                                                  result( new Integer( total ) ) );\n" +
                "    then\n" +
                "        results.add($totalAmount);\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession wm = kbase.newKieSession();
        try {
            final List<?> results = new ArrayList<>();

            wm.setGlobal("results",
                         results);
            wm.setGlobal("globalValue",
                         50);

            wm.insert(new Cheese("stilton",
                                 10));
            wm.insert(new Cheese("brie",
                                 5));
            wm.insert(new Cheese("provolone",
                                 150));
            wm.insert(new Cheese("brie",
                                 20));

            wm.fireAllRules();

            assertEquals(1,
                         results.size());
            assertEquals(100,
                         results.get(0));
        } finally {
            wm.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateNonExistingFunction() {

        final String drl = "package org.drools.compiler\n" +
                "import " + StockTick.class.getCanonicalName() + ";\n" +
                "rule \"Accumulate non existing function - Java\"\n" +
                "    dialect \"java\"\n" +
                "    when\n" +
                "        $val : Number() from accumulate( $st : StockTick(),\n" +
                "                                         nonExistingFunction( 1 ) );\n" +
                "    then\n" +
                "        // no-op\n" +
                "end  \n" +
                "\n" +
                "rule \"Accumulate non existing function - MVEL\"\n" +
                "    dialect \"mvel\"\n" +
                "    when\n" +
                "        $val : Number() from accumulate( $st : StockTick(),\n" +
                "                                         nonExistingFunction( 1 ) );\n" +
                "    then\n" +
                "        // no-op\n" +
                "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration,
                                                                    false,
                                                                    drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        Assertions.assertThat(kieBuilder.getResults().getMessages()).extracting(Message::getText)
                .anySatisfy(text -> Assertions.assertThat(text).contains("Unknown accumulate function: 'nonExistingFunction' on rule 'Accumulate non existing function - Java'."));
        Assertions.assertThat(kieBuilder.getResults().getMessages()).extracting(Message::getText)
                .anySatisfy(text -> Assertions.assertThat(text).contains("Unknown accumulate function: 'nonExistingFunction' on rule 'Accumulate non existing function - MVEL'."));
    }

    @Test(timeout = 10000)
    public void testAccumulateZeroParams() {
        final String drl = "global java.util.List list;\n" +
                "rule fromIt\n" +
                "when\n" +
                "    Number( $c: intValue ) from accumulate( Integer(), count( ) )\n" +
                "then\n" +
                "    list.add( $c );\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            ksession.insert(1);
            ksession.insert(2);
            ksession.insert(3);

            ksession.fireAllRules();

            assertEquals(1, list.size());
            assertEquals(3, list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    private void execTestAccumulateMultipleFunctions(final String fileName) {

        final KieBase kbase = KieBaseUtil.getKieBaseFromClasspathResources("accumulate-test", kieBaseTestConfiguration,
                                                                           fileName);
        final KieSession ksession = kbase.newKieSession();
        try {
            final AgendaEventListener ael = mock(AgendaEventListener.class);
            ksession.addEventListener(ael);

            final Cheese[] cheese = new Cheese[]{
                    new Cheese("stilton", 10),
                    new Cheese("stilton",3),
                    new Cheese("stilton",5),
                    new Cheese("brie",15),
                    new Cheese("brie",17),
                    new Cheese("provolone",8)};
            final Person bob = new Person("Bob",
                                          "stilton");

            final FactHandle[] cheeseHandles = new FactHandle[cheese.length];
            for (int i = 0; i < cheese.length; i++) {
                cheeseHandles[i] = ksession.insert(cheese[i]);
            }
            final FactHandle bobHandle = ksession.insert(bob);

            // ---------------- 1st scenario
            ksession.fireAllRules();

            final ArgumentCaptor<AfterMatchFiredEvent> cap = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
            Mockito.verify(ael).afterMatchFired(cap.capture());

            Match activation = cap.getValue().getMatch();
            assertThat(((Number) activation.getDeclarationValue("$sum")).intValue(),
                       is(18));
            assertThat(((Number) activation.getDeclarationValue("$min")).intValue(),
                       is(3));
            assertThat(((Number) activation.getDeclarationValue("$avg")).intValue(),
                       is(6));

            Mockito.reset(ael);
            // ---------------- 2nd scenario
            final int index = 1;
            cheese[index].setPrice(9);
            ksession.update(cheeseHandles[index],
                            cheese[index]);
            ksession.fireAllRules();

            Mockito.verify(ael).afterMatchFired(cap.capture());

            activation = cap.getValue().getMatch();
            assertThat(((Number) activation.getDeclarationValue("$sum")).intValue(),
                       is(24));
            assertThat(((Number) activation.getDeclarationValue("$min")).intValue(),
                       is(5));
            assertThat(((Number) activation.getDeclarationValue("$avg")).intValue(),
                       is(8));

            Mockito.reset(ael);
            // ---------------- 3rd scenario
            bob.setLikes("brie");
            ksession.update(bobHandle,
                            bob);
            ksession.fireAllRules();

            Mockito.verify(ael).afterMatchFired(cap.capture());

            activation = cap.getValue().getMatch();
            assertThat(((Number) activation.getDeclarationValue("$sum")).intValue(),
                       is(32));
            assertThat(((Number) activation.getDeclarationValue("$min")).intValue(),
                       is(15));
            assertThat(((Number) activation.getDeclarationValue("$avg")).intValue(),
                       is(16));

            Mockito.reset(ael);
            // ---------------- 4th scenario
            ksession.delete(cheeseHandles[3]);
            ksession.fireAllRules();

            Mockito.verify(ael).afterMatchFired(cap.capture());

            activation = cap.getValue().getMatch();
            assertThat(((Number) activation.getDeclarationValue("$sum")).intValue(),
                       is(17));
            assertThat(((Number) activation.getDeclarationValue("$min")).intValue(),
                       is(17));
            assertThat(((Number) activation.getDeclarationValue("$avg")).intValue(),
                       is(17));
        } finally {
            ksession.dispose();
        }
    }

    public static class DataSet {

        public Cheese[] cheese;
        public FactHandle[] cheeseHandles;
        public Person bob;
        public FactHandle bobHandle;
        public List<?> results;
    }

    @Test(timeout = 10000)
    public void testAccumulateMinMax() {
        final String drl = "package org.drools.compiler.test \n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List results \n " +
                "rule minMax \n" +
                "when \n" +
                "    accumulate( Cheese( $p: price ), $min: min($p), $max: max($p) ) \n" +
                "then \n" +
                "    results.add($min); results.add($max); \n" +
                "end \n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Number> results = new ArrayList<>();
            ksession.setGlobal("results", results);
            final Cheese[] cheese = new Cheese[]{
                    new Cheese("Emmentaler", 4),
                    new Cheese("Appenzeller", 6),
                    new Cheese("Greyerzer", 2),
                    new Cheese("Raclette", 3),
                    new Cheese("Olmützer Quargel", 15),
                    new Cheese("Brie", 17),
                    new Cheese("Dolcelatte", 8)};

            for (final Cheese aCheese : cheese) {
                ksession.insert(aCheese);
            }

            ksession.fireAllRules();
            assertEquals(2, results.size());
            assertEquals(results.get(0).intValue(), 2);
            assertEquals(results.get(1).intValue(), 17);
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateCE() {
        final String drl = "package org.drools.compiler\n" +
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                "global java.util.List results\n" +
                "rule \"ocount\"\n" +
                "when\n" +
                "    accumulate( Cheese(), $c: count(1) )\n" +
                "then\n" +
                "    results.add( $c + \" facts\" );\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<String> results = new ArrayList<>();
            ksession.setGlobal("results", results);
            final Cheese[] cheese = new Cheese[]{
                    new Cheese("Emmentaler", 4),
                    new Cheese("Appenzeller", 6),
                    new Cheese("Greyerzer", 2),
                    new Cheese("Raclette", 3),
                    new Cheese("Olmützer Quargel", 15),
                    new Cheese("Brie", 17),
                    new Cheese("Dolcelatte", 8)};

            for (final Cheese aCheese : cheese) {
                ksession.insert(aCheese);
            }

            ksession.fireAllRules();
            assertEquals(1, results.size());
            assertEquals("7 facts", results.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateAndRetract() {
        final String drl = "package org.drools.compiler;\n" +
                "\n" +
                "import java.util.ArrayList;\n" +
                "\n" +
                "global ArrayList list;\n" +
                "\n" +
                "declare Holder\n" +
                "    list : ArrayList\n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "    $l : ArrayList()\n" +
                "then\n" +
                "    insert( new Holder($l) );\n" +
                "end\n" +
                "\n" +
                "rule \"axx\"\n" +
                "when\n" +
                "    $h : Holder( $l : list )\n" +
                "    $n : Long() from accumulate (\n" +
                "                    $b : String( ) from $l;\n" +
                "                    count($b))\n" +
                "then\n" +
                "    System.out.println($n);\n" +
                "    list.add($n);\n" +
                "end\n" +
                "\n" +
                "rule \"clean\"\n" +
                "salience -10\n" +
                "when\n" +
                "    $h : Holder()\n" +
                "then\n" +
                "    retract($h);\n" +
                "end" +
                "\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ks = kbase.newKieSession();
        try {
            final ArrayList resList = new ArrayList();
            ks.setGlobal("list", resList);

            final ArrayList<String> list = new ArrayList<>();
            list.add("x");
            list.add("y");
            list.add("z");

            ks.insert(list);
            ks.fireAllRules();

            assertEquals(3L, resList.get(0));
        } finally {
            ks.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateWithNull() {
        final String drl = "rule foo\n" +
                "when\n" +
                "Object() from accumulate( Object(),\n" +
                "init( Object res = null; )\n" +
                "action( res = null; )\n" +
                "result( res ) )\n" +
                "then\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    public static class MyObj {

        public static class NestedObj {

            public long value;

            public NestedObj(final long value) {
                this.value = value;
            }
        }

        private final NestedObj nestedObj;

        public MyObj(final long value) {
            nestedObj = new NestedObj(value);
        }

        public NestedObj getNestedObj() {
            return nestedObj;
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateWithBoundExpression() {
        final String drl = "package org.drools.compiler;\n" +
                "import " + MyObj.class.getCanonicalName() + ";\n" +
                "global java.util.List results\n" +
                "rule init\n" +
                "   when\n" +
                "   then\n" +
                "       insert( new MyObj(5) );\n" +
                "       insert( new MyObj(4) );\n" +
                "end\n" +
                "rule foo\n" +
                "   salience -10\n" +
                "   when\n" +
                "       $n : Number() from accumulate( MyObj( $val : nestedObj.value ),\n" +
                "                                      sum( $val ) )\n" +
                "   then\n" +
                "       results.add($n);\n" +
                "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Number> results = new ArrayList<>();
            ksession.setGlobal("results",
                               results);
            ksession.fireAllRules();
            ksession.dispose();
            assertEquals(1,
                         results.size());
            assertEquals(9L,
                         results.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testInfiniteLoopAddingPkgAfterSession() {
        // JBRULES-3488
        final String drl = "package org.drools.compiler.test;\n" +
                "import " + Triple.class.getCanonicalName() + ";\n" +
                "rule \"accumulate 2 times\"\n" +
                "when\n" +
                "  $LIST : java.util.List( )" +
                "  from accumulate( $Triple_1 : Triple( $CN : subject," +
                "    predicate == \"<http://deductions.sf.net/samples/princing.n3p.n3#number>\", $N : object )," +
                "      collectList( $N ) )\n" +
                "  $NUMBER : Number() from accumulate(" +
                "    $NUMBER_STRING_ : String() from $LIST , sum( Double.parseDouble( $NUMBER_STRING_)) )\n" +
                "then\n" +
                "  System.out.println(\"ok\");\n" +
                "end\n";

        final InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration);
        final KieSession ksession = kbase.newKieSession();
        try {
            // To reproduce, Need to have 3 object asserted (not less) :
            ksession.insert(new Triple("<http://deductions.sf.net/samples/princing.n3p.n3#CN1>", "<http://deductions.sf.net/samples/princing.n3p.n3#number>", "200"));
            ksession.insert(new Triple("<http://deductions.sf.net/samples/princing.n3p.n3#CN2>", "<http://deductions.sf.net/samples/princing.n3p.n3#number>", "100"));
            ksession.insert(new Triple("<http://deductions.sf.net/samples/princing.n3p.n3#CN3>", "<http://deductions.sf.net/samples/princing.n3p.n3#number>", "100"));

            kbase.addPackages(TestUtil.createKnowledgeBuilder(null, drl).getKnowledgePackages());
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    public static class Triple {

        private String subject;
        private String predicate;
        private String object;

        /**
         * for javabeans
         */
        public Triple() {
        }

        public Triple(final String subject, final String predicate, final String object) {
            this.subject = subject;
            this.predicate = predicate;
            this.object = object;
        }

        public String getSubject() {
            return subject;
        }

        public String getPredicate() {
            return predicate;
        }

        public String getObject() {
            return object;
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateWithVarsOutOfHashOrder() {
        // JBRULES-3494
        final String drl = "package com.sample;\n" +
                "\n" +
                "import java.util.List;\n" +
                "\n" +
                "declare MessageHolder\n" +
                "  id : String\n" +
                "  msg: String\n" +
                "end\n" +
                "\n" +
                "query getResults( String $mId, List $holders )\n" +
                "  accumulate(  \n" +
                "    $holder  : MessageHolder( id == $mId, $ans : msg ),\n" +
                "    $holders := collectList( $holder )\n" +
                "  ) \n" +
                "end\n" +
                "\n" +
                "rule \"Init\"\n" +
                "when\n" +
                "then\n" +
                "  insert( new MessageHolder( \"1\", \"x\" ) );\n" +
                "end\n";


        final InternalKnowledgeBase kbase = (InternalKnowledgeBase) KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration);
        final KieSession ksession = kbase.newKieSession();
        try {
            kbase.addPackages(TestUtil.createKnowledgeBuilder(null, drl).getKnowledgePackages());
            ksession.fireAllRules();

            final QueryResults res = ksession.getQueryResults("getResults", "1", Variable.v);
            assertEquals(1, res.size());

            final Object o = res.iterator().next().get("$holders");
            assertTrue(o instanceof List);
            assertEquals(1, ((List) o).size());
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void testAccumulateWithWindow() {
        final String drl = "global java.util.Map map;\n" +
                " \n" +
                "declare Double\n" +
                "@role(event)\n" +
                "end\n" +
                " \n" +
                "declare window Streem\n" +
                "    Double() over window:length( 10 )\n" +
                "end\n" +
                " \n" +
                "rule \"See\"\n" +
                "when\n" +
                "    $a : Double() from accumulate (\n" +
                "        $d: Double()\n" +
                "            from window Streem,\n" +
                "        sum( $d )\n" +
                "    )\n" +
                "then\n" +
                "    System.out.println( \"We have a sum \" + $a );\n" +
                "end\n";

        testAccumulateEntryPointWindow(drl, null);
    }

    @Test(timeout = 10000)
    public void testAccumulateWithEntryPoint() {
        final String drl = "global java.util.Map map;\n" +
                " \n" +
                "declare Double\n" +
                "@role(event)\n" +
                "end\n" +
                " \n" +
                "rule \"See\"\n" +
                "when\n" +
                "    $a : Double() from accumulate (\n" +
                "        $d: Double()\n" +
                "            from entry-point data,\n" +
                "        sum( $d )\n" +
                "    )\n" +
                "then\n" +
                "    System.out.println( \"We have a sum \" + $a );\n" +
                "end\n";

        testAccumulateEntryPointWindow(drl, "data");
    }

    @Test(timeout = 10000)
    public void testAccumulateWithWindowAndEntryPoint() {
        final String drl = "global java.util.Map map;\n" +
                " \n" +
                "declare Double\n" +
                "@role(event)\n" +
                "end\n" +
                " \n" +
                "declare window Streem\n" +
                "    Double() over window:length( 10 ) from entry-point data\n" +
                "end\n" +
                " \n" +
                "rule \"See\"\n" +
                "when\n" +
                "    $a : Double() from accumulate (\n" +
                "        $d: Double()\n" +
                "            from window Streem,\n" +
                "        sum( $d )\n" +
                "    )\n" +
                "then\n" +
                "    System.out.println( \"We have a sum \" + $a );\n" +
                "end\n";

        testAccumulateEntryPointWindow(drl, "data");
    }

    private void testAccumulateEntryPointWindow(final String drl, final String entryPointName) {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Map res = new HashMap();
            ksession.setGlobal("map", res);
            ksession.fireAllRules();

            for (int j = 0; j < 33; j++) {
                if (entryPointName != null && !"".equals(entryPointName)) {
                    ksession.getEntryPoint("data").insert(1.0 * j);
                } else {
                    ksession.insert(1.0 * j);
                }
                ksession.fireAllRules();
            }
        } finally {
            ksession.dispose();
        }
    }

    @Test(timeout = 10000)
    public void test2AccumulatesWithOr() {
        // JBRULES-3538
        final String drl =
                "import java.util.*;\n" +
                        "import " + MyPerson.class.getCanonicalName() + ";\n" +
                        "global java.util.Map map;\n" +
                        "dialect \"mvel\"\n" +
                        "\n" +
                        "rule \"Test\"\n" +
                        "    when\n" +
                        "        $total : Number()\n" +
                        "             from accumulate( MyPerson( $age: age ),\n" +
                        "                              sum( $age ) )\n" +
                        "\n" +
                        "        $p: MyPerson();\n" +
                        "        $k: List( size > 0 ) from accumulate( MyPerson($kids: kids) from $p.kids,\n" +
                        "            init( ArrayList myList = new ArrayList(); ),\n" +
                        "            action( myList.addAll($kids); ),\n" +
                        "            reverse( myList.removeAll($kids); ),\n" +
                        "            result( myList )\n" +
                        "        )\n" +
                        "\n" +
                        "        $r : MyPerson(name == \"Jos Jr Jr\")\n" +

                        "        or\n" +
                        "        $r : MyPerson(name == \"Jos\")\n" +

                        "    then\n" +
                        "        Map pMap = map.get( $r.getName() );\n" +
                        "        pMap.put( 'total', $total );\n" +
                        "        pMap.put( 'p', $p );\n" +
                        "        pMap.put( 'k', $k );\n" +
                        "        pMap.put( 'r', $r );\n" +
                        "        map.put('count', ((Integer)map.get('count')) + 1 );\n " +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final Map<String, Object> map = new HashMap<>();
            ksession.setGlobal("map", map);
            map.put("Jos Jr Jr", new HashMap());
            map.put("Jos", new HashMap());
            map.put("count", 0);

            final MyPerson josJr = new MyPerson("Jos Jr Jr", 20,
                                                asList(new MyPerson("John Jr 1st", 10,
                                                                    Collections.singletonList(new MyPerson("John Jr Jrx", 4, Collections.emptyList()))),
                                                       new MyPerson("John Jr 2nd", 8, Collections.emptyList())));

            final MyPerson jos = new MyPerson("Jos", 30,
                                              asList(new MyPerson("Jeff Jr 1st", 10, Collections.emptyList()),
                                                     new MyPerson("Jeff Jr 2nd", 8, Collections.emptyList())));

            ksession.execute(new InsertElementsCommand(asList(new Object[]{josJr, jos})));

            ksession.fireAllRules();

            assertEquals(2, map.get("count"));
            Map pMap = (Map) map.get("Jos Jr Jr");
            assertEquals(50.0, ((Number)pMap.get("total")).doubleValue(), 1);
            List kids = (List) pMap.get("k");
            assertEquals(1, kids.size());
            assertEquals("John Jr Jrx", ((MyPerson) kids.get(0)).getName());
            assertEquals(josJr, pMap.get("p"));
            assertEquals(josJr, pMap.get("r"));

            pMap = (Map) map.get("Jos");
            assertEquals(50.0, ((Number)pMap.get("total")).doubleValue(), 1);
            kids = (List) pMap.get("k");
            assertEquals(1, kids.size());
            assertEquals("John Jr Jrx", ((MyPerson) kids.get(0)).getName());
            assertEquals(josJr, pMap.get("p"));
            assertEquals(jos, pMap.get("r"));
        } finally {
            ksession.dispose();
        }
    }

    public static class MyPerson {

        public MyPerson(final String name, final Integer age, final Collection<MyPerson> kids) {
            this.name = name;
            this.age = age;
            this.kids = kids;
        }

        private String name;

        private Integer age;

        private Collection<MyPerson> kids;

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(final Integer age) {
            this.age = age;
        }

        public Collection<MyPerson> getKids() {
            return kids;
        }

        public void setKids(final Collection<MyPerson> kids) {
            this.kids = kids;
        }
    }

    public static class Course {

        private int minWorkingDaySize;

        public Course(final int minWorkingDaySize) {
            this.minWorkingDaySize = minWorkingDaySize;
        }

        public int getMinWorkingDaySize() {
            return minWorkingDaySize;
        }

        public void setMinWorkingDaySize(final int minWorkingDaySize) {
            this.minWorkingDaySize = minWorkingDaySize;
        }
    }

    public static class Lecture {

        private Course course;
        private int day;

        public Lecture(final Course course, final int day) {
            this.course = course;
            this.day = day;
        }

        public Course getCourse() {
            return course;
        }

        public void setCourse(final Course course) {
            this.course = course;
        }

        public int getDay() {
            return day;
        }

        public void setDay(final int day) {
            this.day = day;
        }
    }

    @Test
    public void testAccumulateWithExists() {
        final String drl =
                "import " + Course.class.getCanonicalName() + "\n" +
                        "import " + Lecture.class.getCanonicalName() + "\n" +
                        "global java.util.List list; \n" +
                        "rule \"minimumWorkingDays\"\n" +
                        "    when\n" +
                        "        $course : Course($minWorkingDaySize : minWorkingDaySize)\n" +
                        "        $dayCount : Number(intValue <= $minWorkingDaySize) from accumulate(\n" +
                        "            $day : Integer()\n" +
                        "            and exists Lecture(course == $course, day == $day),\n" +
                        "            count($day)\n" +
                        "        )\n" +
                        "        // An uninitialized schedule should have no constraints broken\n" +
                        "        exists Lecture(course == $course)\n" +
                        "    then\n" +
                        "       list.add( $course );\n" +
                        "       list.add( $dayCount );\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List list = new ArrayList();
            ksession.setGlobal("list", list);

            final Integer day1 = 1;
            final Integer day2 = 2;
            final Integer day3 = 3;

            final Course c = new Course(2);

            final Lecture l1 = new Lecture(c, day1);
            final Lecture l2 = new Lecture(c, day2);

            ksession.insert(day1);
            ksession.insert(day2);
            ksession.insert(day3);
            ksession.insert(c);
            ksession.insert(l1);
            ksession.insert(l2);

            assertEquals(1, ksession.fireAllRules());

            assertEquals(2, list.size());
            assertEquals(c, list.get(0));
            assertEquals(2L, list.get(1));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testImportAccumulateFunction() {
        final String drl = "package org.foo.bar\n"
                + "import accumulate " + TestFunction.class.getCanonicalName() + " f\n"
                + "rule X when\n"
                + "    accumulate( $s : String(),\n"
                + "                $v : f( $s ) )\n"
                + "then\n"
                + "end\n";
        testImportAccumulateFunction(drl);
    }

    @Test
    public void testImportAccumulateFunctionWithDeclaration() {
        // DROOLS-750
        final String drl = "package org.foo.bar\n"
                + "import accumulate " + TestFunction.class.getCanonicalName() + " f;\n"
                + "import " + Person.class.getCanonicalName() + ";\n"
                + "declare Person \n"
                + "  @propertyReactive\n"
                + "end\n"
                + "rule X when\n"
                + "    accumulate( $s : String(),\n"
                + "                $v : f( $s ) )\n"
                + "then\n"
                + "end\n";

        testImportAccumulateFunction(drl);
    }

    private void testImportAccumulateFunction(final String drl) {
        final ReleaseId releaseId = new ReleaseIdImpl("foo", "bar", "1.0");
        KieUtil.getKieModuleFromDrls(releaseId, kieBaseTestConfiguration, drl);
        final KieContainer kc = KieServices.get().newKieContainer(releaseId);
        final KieSession ksession = kc.newKieSession();
        try {
            final AgendaEventListener ael = mock(AgendaEventListener.class);
            ksession.addEventListener(ael);

            ksession.insert("x");
            ksession.fireAllRules();

            final ArgumentCaptor<AfterMatchFiredEvent> ac = ArgumentCaptor.forClass(AfterMatchFiredEvent.class);
            verify(ael).afterMatchFired(ac.capture());

            assertThat(ac.getValue().getMatch().getDeclarationValue("$v"), is(1));
        } finally {
            ksession.dispose();
        }
    }

    public static class TestFunction implements AccumulateFunction<Serializable> {

        @Override
        public void writeExternal(final ObjectOutput out) {
        }

        @Override
        public void readExternal(final ObjectInput in) {
        }

        @Override
        public Serializable createContext() {
            return null;
        }

        @Override
        public void init(final Serializable context) {
        }

        @Override
        public void accumulate(final Serializable context, final Object value) {
        }

        @Override
        public void reverse(final Serializable context, final Object value) {
        }

        @Override
        public Object getResult(final Serializable context) {
            return 1;
        }

        @Override
        public boolean supportsReverse() {
            return true;
        }

        @Override
        public Class<?> getResultType() {
            return Number.class;
        }
    }

    @Test
    public void testAccumulateWithSharedNode() {
        // DROOLS-594
        final String drl =
                "rule A when" +
                        "   Double() " +
                        "then " +
                        "end " +
                        "rule B  " +
                        "when " +
                        "   Double() " +
                        "   String() " +
                        "   $list : java.util.List(  this not contains \"XX\" ) " +
                        "   $sum  : Integer( ) from accumulate ( $i : Integer(), " +
                        "                                        sum( $i ) ) " +
                        "then " +
                        "    $list.add( \"XX\" );\n" +
                        "    update( $list );\n" +
                        "end ";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            ksession.insert(list);

            ksession.insert(42.0);
            ksession.insert(9000);
            ksession.insert("a");
            ksession.insert("b");
            ksession.fireAllRules();

            assertEquals(1, list.size());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEmptyAccumulateInSubnetwork() {
        // DROOLS-598
        final String drl =
                "global java.util.List list;\n" +
                        "rule R when\n" +
                        "    $count : Number( ) from accumulate (\n" +
                        "        Integer() and\n" +
                        "        $s: String();\n" +
                        "        count($s)\n" +
                        "    )\n" +
                        "then\n" +
                        "    list.add($count);\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Long> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.insert(1);
            ksession.fireAllRules();

            assertEquals(1, list.size());
            assertEquals(0, (long) list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testEmptyAccumulateInSubnetworkFollwedByPattern() {
        // DROOLS-627
        final String drl =
                "global java.util.List list;\n" +
                        "rule R when\n" +
                        "    $count : Number( ) from accumulate (\n" +
                        "        Integer() and\n" +
                        "        $s: String();\n" +
                        "        count($s)\n" +
                        "    )\n" +
                        "    Long()\n" +
                        "then\n" +
                        "    list.add($count);\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Long> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.insert(1);
            ksession.insert(1L);
            ksession.fireAllRules();

            assertEquals(1, list.size());
            assertEquals(0, (long) list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testAccumulateWithoutSeparator() {
        // DROOLS-602
        final String drl = "package org.drools.compiler\n" +
                "import " + Person.class.getCanonicalName() + ";\n" +
                "rule \"Constraints everywhere\" \n" +
                "    when\n" +
                "        $person : Person( $likes : likes )\n" +
                "        accumulate( Cheese( type == $likes, $price : price )\n" +
                "                    $sum : sum( $price ),\n" +
                "                    $avg : average( $price ),\n" +
                "                    $min : min( $price );\n" +
                "                    $min == 3,\n" +
                "                    $sum > 10 )\n" +
                "    then\n" +
                "        // do something\n" +
                "end  ";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @Test
    public void testFromAccumulateWithoutSeparator() {
        // DROOLS-602
        final String drl = "rule R when\n" +
                "    $count : Number( ) from accumulate (\n" +
                "        $s: String()\n" +
                "        count($s)\n" +
                "    )\n" +
                "then\n" +
                "    System.out.println($count);\n" +
                "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    public static class ExpectedMessage {

        String type;

        public ExpectedMessage(final String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

    public static class ExpectedMessageToRegister {

        String type;
        boolean registered = false;
        List<ExpectedMessage> msgs = new ArrayList<>();

        public ExpectedMessageToRegister(final String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public List<ExpectedMessage> getExpectedMessages() {
            return msgs;
        }

        public boolean isRegistered() {
            return registered;
        }

        public void setRegistered(final boolean registered) {
            this.registered = registered;
        }
    }

    @Test
    public void testReaccumulateForLeftTuple() {

        final String drl =
                "import " + ExpectedMessage.class.getCanonicalName() + ";\n"
                        + "import " + List.class.getCanonicalName() + ";\n"
                        + "import " + ExpectedMessageToRegister.class.getCanonicalName() + ";\n"
                        + "\n\n"

                        + "rule \"Modify\"\n"
                        + " when\n"
                        + " $etr: ExpectedMessageToRegister(registered == false)"
                        + " then\n"
                        + " modify( $etr ) { setRegistered( true ) }"
                        + " end\n"

                        + "rule \"Collect\"\n"
                        + " salience 200 \n"
                        + " when\n"
                        + " etr: ExpectedMessageToRegister($type: type)"
                        + " $l : List( ) from collect( ExpectedMessage( type == $type ) from etr.expectedMessages )"
                        + " then\n"
                        + " java.lang.System.out.println( $l.size() );"
                        + " end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final ExpectedMessage psExpMsg1 = new ExpectedMessage("Index");

            final ExpectedMessageToRegister etr1 = new ExpectedMessageToRegister("Index");
            etr1.msgs.add(psExpMsg1);

            ksession.insert(etr1);
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testNoLoopAccumulate() {
        // DROOLS-694
        final String drl =
                "import " + AtomicInteger.class.getCanonicalName() + ";\n" +
                        "rule NoLoopAccumulate\n" +
                        "no-loop\n" +
                        "when\n" +
                        "    accumulate( $s : String() ; $val : count($s) )\n" +
                        "    $a : AtomicInteger( )\n" +
                        "then\n" +
                        "    modify($a) { set($val.intValue()) }\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final AtomicInteger counter = new AtomicInteger(0);
            ksession.insert(counter);

            ksession.insert("1");
            ksession.fireAllRules();

            assertEquals(1, counter.get());

            ksession.insert("2");
            ksession.fireAllRules();

            assertEquals(2, counter.get());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testAccumulateWithOr() {
        // DROOLS-839
        final String drl =
                "import " + Converter.class.getCanonicalName() + ";\n" +
                        "global java.util.List list;\n" +
                        "rule R when\n" +
                        "  (or\n" +
                        "    Integer (this == 1)\n" +
                        "    Integer (this == 2)\n" +
                        "  )\n" +
                        "String( $length : length )\n" +
                        "accumulate ( $c : Converter(), $result : sum( $c.convert($length) ) )\n" +
                        "then\n" +
                        "    list.add($result);\n" +
                        "end";

        testAccumulateWithOr(drl);
    }

    @Test
    public void testMvelAccumulateWithOr() {
        // DROOLS-839
        final String drl =
                "import " + Converter.class.getCanonicalName() + ";\n" +
                        "global java.util.List list;\n" +
                        "rule R dialect \"mvel\" when\n" +
                        "  (or\n" +
                        "    Integer (this == 1)\n" +
                        "    Integer (this == 2)\n" +
                        "  )\n" +
                        "String( $length : length )\n" +
                        "accumulate ( $c : Converter(), $result : sum( $c.convert($length) ) )\n" +
                        "then\n" +
                        "    list.add($result);\n" +
                        "end";

        testAccumulateWithOr(drl);
    }

    private void testAccumulateWithOr(final String drl) {
        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Number> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.insert(1);
            ksession.insert("hello");
            ksession.insert(new Converter());
            ksession.fireAllRules();

            assertEquals(1, list.size());
            assertEquals(5, list.get(0).intValue());
        } finally {
            ksession.dispose();
        }
    }

    public static class Converter {

        public static int convert(final int i) {
            return i;
        }
    }

    @Test
    public void testNormalizeStagedTuplesInAccumulate() {
        // DROOLS-998
        final String drl =
                "global java.util.List list;\n" +
                        "rule R when\n" +
                        "    not( String() )\n" +
                        "    accumulate(\n" +
                        "        $l: Long();\n" +
                        "        count($l)\n" +
                        "    )\n" +
                        "    ( Boolean() or not( Float() ) )\n" +
                        "then\n" +
                        "    list.add( \"fired\" ); \n" +
                        "    insert(new String());\n" +
                        "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<String> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.fireAllRules();
            assertEquals(1, list.size());
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testIncompatibleTypeOnAccumulateFunction() {
        // DROOLS-1243
        final String drl =
                "import " + MyPerson.class.getCanonicalName() + ";\n" +
                        "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                        "global java.util.List list;\n" +
                        "rule R when\n" +
                        "  $theFrom : BigDecimal() from accumulate(MyPerson( $val : age ); \n" +
                        "                                          sum( $val ) )\n" +
                        "then\n" +
                        "  list.add($theFrom);\n" +
                        "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @Test
    public void testIncompatibleListOnAccumulateFunction() {
        // DROOLS-1243
        final String drl =
                "import " + MyPerson.class.getCanonicalName() + ";\n" +
                        "import " + BigDecimal.class.getCanonicalName() + ";\n" +
                        "global java.util.List list;\n" +
                        "rule R when\n" +
                        "  $theFrom : String() from accumulate(MyPerson( $val : age ); \n" +
                        "                                          collectList( $val ) )\n" +
                        "then\n" +
                        "  list.add($theFrom);\n" +
                        "end\n";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @Test
    public void testTypedSumOnAccumulate() {
        // DROOLS-1175
        final String drl =
                "global java.util.List list;\n" +
                        "rule R when\n" +
                        "  $i : Integer()\n" +
                        "  accumulate ( $s : String(), $result : sum( $s.length() ) )\n" +
                        "then\n" +
                        "  list.add($result);\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Integer> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.insert(1);
            ksession.insert("hello");
            ksession.insert("hi");
            ksession.fireAllRules();

            assertEquals(1, list.size());
            assertEquals("hello".length() + "hi".length(), (int) list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testSumAccumulateOnNullValue() {
        // DROOLS-1242
        final String drl =
                "import " + PersonWithBoxedAge.class.getCanonicalName() + ";\n" +
                        "global java.util.List list;\n" +
                        "rule R when\n" +
                        "  accumulate ( $p : PersonWithBoxedAge(), $result : sum( $p.getAge() ) )\n" +
                        "then\n" +
                        "  list.add($result);\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Integer> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.insert(new PersonWithBoxedAge("me", 30));
            ksession.insert(new PersonWithBoxedAge("you", 40));
            ksession.insert(new PersonWithBoxedAge("she", null));
            ksession.fireAllRules();

            assertEquals(1, list.size());
            assertEquals(70, (int) list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testMinAccumulateOnComparable() {
        testMinMaxAccumulateOnComparable("min", "she");
    }

    @Test
    public void testMaxAccumulateOnComparable() {
        testMinMaxAccumulateOnComparable("max", "you");
    }

    private void testMinMaxAccumulateOnComparable(final String minMaxFunction, final String expectedResult) {

        final String drl =
                "import " + PersonWithBoxedAge.class.getCanonicalName() + ";\n" +
                        "global java.util.List list;\n" +
                        "rule R when\n" +
                        "  accumulate ( $p : PersonWithBoxedAge(), $result : " + minMaxFunction + "( $p ) )\n" +
                        "then\n" +
                        "  list.add($result);\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<PersonWithBoxedAge> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.insert(new PersonWithBoxedAge("me", 30));
            ksession.insert(new PersonWithBoxedAge("you", 40));
            ksession.insert(new PersonWithBoxedAge("she", 25));
            ksession.fireAllRules();

            assertEquals(1, list.size());
            assertEquals(expectedResult, list.get(0).getName());
        } finally {
            ksession.dispose();
        }
    }

    public static class PersonWithBoxedAge implements Comparable<PersonWithBoxedAge> {

        private final String name;
        private final Integer age;

        public PersonWithBoxedAge(final String name, final Integer age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public Integer getAge() {
            return age;
        }

        @Override
        public int compareTo(final PersonWithBoxedAge other) {
            return age.compareTo(other.getAge());
        }
    }

    @Test
    public void testTypedMaxOnAccumulate() {
        // DROOLS-1175
        final String drl =
                "global java.util.List list;\n" +
                        "rule R when\n" +
                        "  $i : Integer()\n" +
                        "  $result : Integer() from accumulate ( $s : String(), max( $s.length() ) )\n" +
                        "then\n" +
                        "  list.add($result);\n" +
                        "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession ksession = kbase.newKieSession();
        try {
            final List<Integer> list = new ArrayList<>();
            ksession.setGlobal("list", list);

            ksession.insert(1);
            ksession.insert("hello");
            ksession.insert("hi");
            ksession.fireAllRules();

            assertEquals(1, list.size());
            assertEquals("hello".length(), (int) list.get(0));
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testVarianceDouble() {
        final String drl =
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                        "global java.util.List list;\n" +
                        "rule R when\n" +
                        "  accumulate(\n" +
                        "    Cheese($price : price);\n" +
                        "    $result : variance($price)\n" +
                        "  )\n" +
                        "then\n" +
                        "  list.add($result);\n" +
                        "end";



        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);

        assertEquals(0.00, cheeseInsertsFunction(kieBase, 3, 3, 3, 3, 3), 0.01);
        assertEquals(0.80, cheeseInsertsFunction(kieBase, 4, 4, 3, 2, 2), 0.01);
        assertEquals(1.20, cheeseInsertsFunction(kieBase, 5, 3, 3, 2, 2), 0.01);
        assertEquals(2.80, cheeseInsertsFunction(kieBase, 5, 5, 2, 2, 1), 0.01);
        assertEquals(2.80, cheeseInsertsFunction(kieBase, 6, 3, 3, 2, 1), 0.01);
        assertEquals(4.40, cheeseInsertsFunction(kieBase, 6, 5, 2, 1, 1), 0.01);
        assertEquals(16.00, cheeseInsertsFunction(kieBase, 11, 1, 1, 1, 1), 0.01);
        assertEquals(36.00, cheeseInsertsFunction(kieBase, 15, 0, 0, 0, 0), 0.01);
    }

    @Test
    public void testStandardDeviationDouble() {
        final String drl =
                "import " + Cheese.class.getCanonicalName() + ";\n" +
                        "global java.util.List list;\n" +
                        "rule R when\n" +
                        "  accumulate(\n" +
                        "    Cheese($price : price);\n" +
                        "    $result : standardDeviation($price)\n" +
                        "  )\n" +
                        "then\n" +
                        "  list.add($result);\n" +
                        "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);

        assertEquals(0.00, cheeseInsertsFunction(kieBase, 3, 3, 3, 3, 3), 0.01);
        assertEquals(0.89, cheeseInsertsFunction(kieBase, 4, 4, 3, 2, 2), 0.01);
        assertEquals(1.10, cheeseInsertsFunction(kieBase, 5, 3, 3, 2, 2), 0.01);
        assertEquals(1.67, cheeseInsertsFunction(kieBase, 5, 5, 2, 2, 1), 0.01);
        assertEquals(1.67, cheeseInsertsFunction(kieBase, 6, 3, 3, 2, 1), 0.01);
        assertEquals(2.10, cheeseInsertsFunction(kieBase, 6, 5, 2, 1, 1), 0.01);
        assertEquals(4.00, cheeseInsertsFunction(kieBase, 11, 1, 1, 1, 1), 0.01);
        assertEquals(6.00, cheeseInsertsFunction(kieBase, 15, 0, 0, 0, 0), 0.01);
    }

    private double cheeseInsertsFunction(final KieBase kieBase, final int... prices) {
        final KieSession ksession = kieBase.newKieSession();
        try {
            final List<Double> list = new ArrayList<>();
            ksession.setGlobal("list", list);
            for (final int price : prices) {
                ksession.insert(new Cheese("stilton", price));
            }
            ksession.fireAllRules();
            assertEquals(1, list.size());
            final double result = list.get(0);
            final FactHandle triggerReverseHandle = ksession.insert(new Cheese("triggerReverse", 7));
            ksession.fireAllRules();
            ksession.delete(triggerReverseHandle);
            list.clear();
            ksession.fireAllRules();
            assertEquals(1, list.size());
            // Check that the reserse() does the opposite of the accumulate()
            assertEquals(result, list.get(0), 0.001);
            return list.get(0);
        } finally {
            ksession.dispose();
        }
    }

    @Test
    public void testConcurrentLeftAndRightUpdate() {
        // DROOLS-1517
        final String drl = "package P;\n"
                + "import " + Visit.class.getCanonicalName() + ";\n"
                + "global java.util.List list\n"
                + "rule OvercommittedMechanic\n"
                + "when\n"
                + "  Visit($bucket : bucket)\n"
                + "  $weeklyCommitment : Number() from accumulate(\n"
                + "	     Visit($duration : duration, bucket == $bucket),\n"
                + "	          sum($duration)\n"
                + "      )\n"
                + "then\n"
                + "  list.add($weeklyCommitment);"
                + "end";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                         drl);
        final KieSession kieSession = kbase.newKieSession();
        try {
            final List<Number> list = new ArrayList<>();
            kieSession.setGlobal("list", list);

            final Visit visit1 = new Visit(1.0);
            final Visit visit2 = new Visit(2.0);
            final Visit visit3 = new Visit(3.0);
            final Visit visit4 = new Visit(4.0);
            final int bucketA = 1;
            final int bucketB = 2;
            visit1.setBucket(bucketA);
            visit2.setBucket(bucketB);
            visit3.setBucket(bucketB);
            visit4.setBucket(bucketB);

            final FactHandle fhVisit1 = kieSession.insert(visit1);
            kieSession.insert(visit2);
            final FactHandle fhVisit3 = kieSession.insert(visit3);
            final FactHandle fhVisit4 = kieSession.insert(visit4);

            kieSession.fireAllRules();
            assertTrue(containsExactlyAndClear(list, 9.0, 9.0, 9.0, 1.0));

            kieSession.update(fhVisit4, visit4);
            kieSession.update(fhVisit3, visit3.setBucket(bucketA));
            kieSession.update(fhVisit1, visit1.setBucket(bucketB));

            kieSession.fireAllRules();
            assertTrue(containsExactlyAndClear(list, 7.0, 7.0, 3.0, 7.0));

            kieSession.update(fhVisit1, visit1.setBucket(bucketA));

            kieSession.fireAllRules();
            assertTrue(list.containsAll(asList(6.0, 4.0, 6.0, 4.0)));
        } finally {
            kieSession.dispose();
        }
    }

    public static class Visit {

        private static int TAG = 1;

        private final double duration;
        private int bucket;

        private final int tag;

        public Visit(final double duration) {
            this.duration = duration;
            this.tag = TAG++;
        }

        public int getBucket() {
            return bucket;
        }

        public Visit setBucket(final int bucket) {
            this.bucket = bucket;
            return this;
        }

        public double getDuration() {
            return duration;
        }

        @Override
        public String toString() {
            return "Visit[" + tag + "]";
        }
    }

    private <T> boolean containsExactlyAndClear(final List<T> list, final T... values) {
        if (list.size() != values.length) {
            return false;
        }
        for (final T value : values) {
            if (!list.remove(value)) {
                System.err.println(value + " not present");
                return false;
            }
        }
        return list.isEmpty();
    }

    @Test
    public void testDoubleAccumulate() {
        // DROOLS-1530
        final String drl = "package P;"
                + "import " + BusStop.class.getCanonicalName() + ";\n"
                + "import " + Coach.class.getCanonicalName() + ";\n"
                + "import " + Shuttle.class.getCanonicalName() + ";\n"
                + "\n"
                + "global java.util.List result;\n"
                + "\n"
                + "rule coachCapacity\n"
                + "    when\n"
                + "        $coach : Coach()\n"
                + "        accumulate(\n"
                + "            BusStop(bus == $coach);\n"
                + "            count()\n"
                + "        )\n"
                + "\n"
                + "        $shuttle : Shuttle()\n"
                + "        accumulate(\n"
                + "            BusStop(bus == $coach)\n"
                + "            and BusStop(bus == $shuttle);\n"
                + "            $result : count()\n"
                + "        )\n"
                + "    then\n"
                + "        result.add($result);\n"
                + "end";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        KieSession kieSession = kieBase.newKieSession();
        try {
            final ArrayList<Integer> result = new ArrayList<>();
            kieSession.setGlobal("result", result);

            int id = 1;
            final Coach coach1 = new Coach(id++);
            final Coach coach2 = new Coach(id++);
            final Shuttle shuttle = new Shuttle(id++);
            final BusStop stop1 = new BusStop(id++);
            final BusStop stop2 = new BusStop(id);

            stop2.setBus(coach2);

            kieSession.insert(coach1);
            kieSession.insert(coach2);
            final FactHandle fhShuttle = kieSession.insert(shuttle);
            final FactHandle fhStop1 = kieSession.insert(stop1);
            final FactHandle fhStop2 = kieSession.insert(stop2);

            kieSession.fireAllRules();
            result.clear();

            kieSession.update(fhShuttle, shuttle);
            kieSession.update(fhStop2, stop2);

            kieSession.fireAllRules();
            result.clear();

            kieSession.update(fhShuttle, shuttle);
            stop1.setBus(shuttle);
            kieSession.update(fhStop1, stop1);

            kieSession.fireAllRules();
            final ArrayList<Integer> actual = new ArrayList<>(result);
            Collections.sort(actual);
            result.clear();
            kieSession.dispose();

            kieSession = kieBase.newKieSession();
            kieSession.setGlobal("result", result);

            // Insert everything into a fresh session to see the uncorrupted score
            kieSession.insert(coach1);
            kieSession.insert(coach2);
            kieSession.insert(shuttle);
            kieSession.insert(stop1);
            kieSession.insert(stop2);

            kieSession.fireAllRules();
            final ArrayList<Integer> expected = new ArrayList<>(result);
            Collections.sort(expected);
            assertEquals(expected, actual);
        } finally {
            kieSession.dispose();
        }
    }

    public interface Bus {

    }

    public static class Coach implements Bus {

        private final int id;

        public Coach(final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Coach[" + id + "]";
        }
    }

    public static class Shuttle implements Bus {

        private final int id;

        public Shuttle(final int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "Shuttle[" + id + "]";
        }
    }

    public static class BusStop {

        private final int id;

        private Bus bus;

        public BusStop(final int id) {
            this.id = id;
        }

        public Bus getBus() {
            return bus;
        }

        public void setBus(final Bus bus) {
            this.bus = bus;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "BusStop[" + id + "]";
        }
    }

    @Test
    public void testCompileFailureOnMissingImport() {
        // DROOLS-1714
        final String drl = "import " + BusStop.class.getCanonicalName() + ";\n" +
                "rule \"sample rule\"\n" +
                "when\n" +
                "\n" +
                "    $bus: Bus()\n" +
                "    \n" +
                "    accumulate(\n" +
                "         $sample: BusStop(bus == $bus);\n" +
                "    $count: count()\n" +
                "    )\n" +
                "then\n" +
                "    System.out.println(\"wierd error: \" + $count );\n" +
                "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
    }

    @Test
    public void testAccumulateWithFrom() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "global java.util.List persons;\n" +
                        "global java.util.List list;\n" +
                        "rule AccumulateAdults when\n" +
                        "   accumulate( $p: Person( $age: age >= 18 ) from persons, \n" +
                        "               $sum : sum( $age ) )\n" +
                        "then\n" +
                        "   list.add($sum); \n" +
                        "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession kieSession = kieBase.newKieSession();
        try {
            final List<Integer> list = new ArrayList<>();
            kieSession.setGlobal("list", list);

            final List<Person> persons = Arrays.asList(new Person("Mario", 42), new Person("Marilena", 44), new Person("Sofia", 4));
            kieSession.setGlobal("persons", persons);

            assertEquals(1, kieSession.fireAllRules());
            assertEquals(1, list.size());
            assertEquals(86, (int) list.get(0));
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testAccumulateWith2EntryPoints() {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "global java.util.List list;\n" +
                        "rule AccumulateAdults when\n" +
                        "   String() from entry-point strings" +
                        "   accumulate( $p: Person( $age: age >= 18 ) from entry-point persons, \n" +
                        "               $sum : sum( $age ) )\n" +
                        "then\n" +
                        "   list.add($sum); \n" +
                        "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl);
        final KieSession kieSession = kieBase.newKieSession();
        try {
            final List<Integer> list = new ArrayList<>();
            kieSession.setGlobal("list", list);

            kieSession.getEntryPoint("strings").insert("test");
            kieSession.getEntryPoint("persons").insert(new Person("Mario", 42));
            kieSession.getEntryPoint("persons").insert(new Person("Marilena", 44));
            kieSession.getEntryPoint("persons").insert(new Person("Sofia", 4));

            assertEquals(1, kieSession.fireAllRules());
            assertEquals(1, list.size());
            assertEquals(86, (int) list.get(0));
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testNumericMax() {
        // DROOLS-2519
        assertEquals(44, (int) testMax("age"));
    }

    @Test
    public void testComparableMax() {
        // DROOLS-2519
        assertEquals("Sofia", testMax("name"));
    }

    private Object testMax(final String fieldToUse) {
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "global java.util.List list;\n" +
                        "rule AccumulateAdults when\n" +
                        "   accumulate( $p: Person( ${fieldToUse} : {fieldToUse} ) , \n" +
                        "               $max : max( ${fieldToUse} ) )\n" +
                        "then\n" +
                        "   list.add($max); \n" +
                        "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("accumulate-test", kieBaseTestConfiguration,
                                                                           drl.replace("{fieldToUse}", fieldToUse));
        final KieSession kieSession = kieBase.newKieSession();
        try {
            final List<Integer> list = new ArrayList<>();
            kieSession.setGlobal("list", list);

            kieSession.insert(new Person("Mario", 42));
            kieSession.insert(new Person("Marilena", 44));
            kieSession.insert(new Person("Sofia", 4));

            assertEquals(1, kieSession.fireAllRules());
            assertEquals(1, list.size());
            return list.get(0);
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testAccumlateResultCannotBeUsedInFunctions() {
        final String drl =
                "import java.util.*;" +
                        "rule \"Rule X\" when\n" +
                        "    openAlarms: Collection( ) from accumulate (\n" +
                        "            $s : String(),\n" +
                        "            init( Map map = new HashMap(); ),\n" +
                        "            action( map.put($s, openAlarms); ),\n" +
                        "            result( map.values() ) )\n" +
                        "then\n" +
                        "end";

        final KieBuilder kieBuilder = KieUtil.getKieBuilderFromDrls(kieBaseTestConfiguration, false, drl);
        Assertions.assertThat(kieBuilder.getResults().getMessages()).isNotEmpty();
        Assertions.assertThat(kieBuilder.getResults().getMessages()).extracting(Message::getText)
                .anySatisfy(text -> Assertions.assertThat(text).contains("openAlarms"));
    }

    @Test
    public void testAverageWithNoFacts() throws Exception {
        // DROOLS-2595
        final String drl =
                "import " + Person.class.getCanonicalName() + "\n" +
                        "global java.util.List list;\n" +
                        "rule R when\n" +
                        "   accumulate( String( $l : length ) , \n" +
                        "               $avg : average( $l ) )\n" +
                        "then\n" +
                        "   list.add($avg); \n" +
                        "end\n";

        final KieBase kieBase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        final KieSession kieSession = kieBase.newKieSession();

        final List<Integer> list = new ArrayList<>();
        kieSession.setGlobal( "list", list );

        final FactHandle fh = kieSession.insert("test" );

        assertEquals(1, kieSession.fireAllRules() );
        assertEquals(1, list.size() );
        assertEquals(4, ((Number)list.get(0)).intValue());

        list.clear();

        kieSession.delete( fh );
        assertEquals(0, kieSession.fireAllRules() );
        assertEquals(0, list.size() );
    }

    @Test
    public void testAverageFunctionRounding() {
        final String drl =
                "import java.math.BigDecimal; \n" +
                "import java.util.List; \n" +
                "global List<BigDecimal> resultList; \n" +
                "rule \"accumulateTest\"\n" +
                "when\n" +
                " accumulate(\n" +
                "    $bd: java.math.BigDecimal();\n" +
                "    $ave: average( $bd ))\n" +
                "then\n " +
                "    resultList.add($ave);\n" +
                "end";

        final KieBase kieBase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        final KieSession kieSession = kieBase.newKieSession();
        final List<BigDecimal> resultList = new ArrayList<>();
        kieSession.setGlobal("resultList", resultList);
        try {
            kieSession.insert(new BigDecimal(0));
            kieSession.insert(new BigDecimal(0));
            kieSession.insert(new BigDecimal(1));

            assertEquals(1, kieSession.fireAllRules());
            assertEquals(1, resultList.size());
            assertEquals(BigDecimal.ZERO, resultList.get(0));
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testNestedAccumulateWithPrefixAnd() {
        final String drl =
                "rule R when\n" +
                "    String($l: length)\n" +
                "    accumulate(\n" +
                "        (and\n" +
                "            Integer(this == $l) \n" +
                "            accumulate(\n" +
                "                Long() \n" +
                "                ;$counter: count(1);$counter <= 4)\n" +
                "         )\n" +
                "        ;$mainCounter: count(1);$mainCounter <= 2\n" +
                "    )\n" +
                "then\n " +
                "end";

        final KieBase kieBase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        final KieSession kieSession = kieBase.newKieSession();

        try {
            kieSession.insert("test");
            kieSession.insert(4);
            assertEquals(1, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }

    @Test
    public void testNestedAccumulateWithInfixAnd() {
        final String drl =
                "rule R when\n" +
                "    String($l: length)\n" +
                "    accumulate(\n" +
                "        (\n" +
                "            Integer(this == $l) and\n" +
                "            accumulate(\n" +
                "                Long() \n" +
                "                ;$counter: count(1);$counter <= 4)\n" +
                "         )\n" +
                "        ;$mainCounter: count(1);$mainCounter <= 2\n" +
                "    )\n" +
                "then\n " +
                "end";

        final KieBase kieBase = new KieHelper().addContent(drl, ResourceType.DRL).build();
        final KieSession kieSession = kieBase.newKieSession();

        try {
            kieSession.insert("test");
            kieSession.insert(4);
            assertEquals(1, kieSession.fireAllRules());
        } finally {
            kieSession.dispose();
        }
    }
}
