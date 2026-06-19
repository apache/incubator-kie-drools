/*
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
package org.drools.mvel.integrationtests;

import java.util.stream.Stream;

import org.drools.mvel.compiler.Cheese;
import org.drools.mvel.compiler.Person;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;

/**
 * Reproducers for NPE in RuleExecutor.removeDormantTuple (issue #6422, PR #6707).
 *
 * Root cause: doLeftTupleInsert can return early (no-loop or lock-on-active guard)
 * without adding the tuple to any RuleExecutor list. The tuple remains linked as
 * a child at the join node. A subsequent fact modification triggers an UPDATE for
 * the orphan, and modifyActiveTuple calls removeDormantTuple on a tuple that was
 * never in dormantMatches — NPE in LinkedList.remove().
 */
public class RemoveDormantTupleNPETest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    /**
     * Orphan via no-loop, triggered by external ksession.update().
     *
     * R1 (no-loop) fires → inserts new Cheese → new match blocked by no-loop → orphan.
     * External update of Person → UPDATE on orphan → NPE.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNoLoopInsertThenExternalUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "package org.drools.reproducer\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "\n" +
                "rule R1\n" +
                "    no-loop true\n" +
                "when\n" +
                "    $c : Cheese(price > 0)\n" +
                "    $p : Person(age > 0)\n" +
                "then\n" +
                "    insert(new Cheese(\"inserted\", $c.getPrice() + 100));\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        try {
            Cheese cheese = new Cheese("original", 1);
            Person person = new Person("john", 1);

            ksession.insert(cheese);
            FactHandle personHandle = ksession.insert(person);

            // R1 fires → inserts Cheese("inserted",101) → new match blocked by no-loop → orphan
            ksession.fireAllRules();

            // Modify Person externally → right-side UPDATE at R1's join node hits the orphan
            person.setAge(2);
            ksession.update(personHandle, person);

            // doLeftTupleUpdate → modifyActiveTuple → removeDormantTuple → NPE
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    /**
     * Orphan via no-loop, triggered by a second rule's modify — single fireAllRules.
     *
     * R1 (no-loop, higher salience) fires first → inserts new Cheese → orphan.
     * R2 fires second → modifies Person → UPDATE on orphan → NPE.
     * All within one fireAllRules() call.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNoLoopCrossRuleModify(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "package org.drools.reproducer\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "\n" +
                "rule R1\n" +
                "    no-loop true\n" +
                "    salience 10\n" +
                "when\n" +
                "    $c : Cheese(price > 0)\n" +
                "    $p : Person(age > 0)\n" +
                "then\n" +
                "    insert(new Cheese(\"inserted\", $c.getPrice() + 100));\n" +
                "end\n" +
                "\n" +
                "rule R2\n" +
                "    no-loop true\n" +
                "    salience 5\n" +
                "when\n" +
                "    $c : Cheese(type == \"original\")\n" +
                "    $p : Person(age > 0)\n" +
                "then\n" +
                "    modify($p) { setAge($p.getAge() + 1) };\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        try {
            ksession.insert(new Cheese("original", 1));
            ksession.insert(new Person("john", 1));

            // R1 fires (salience 10): inserts Cheese("inserted",101) → orphan at R1's terminal.
            // R2 fires (salience 5): modifies Person → right-side UPDATE at R1's join node.
            // R1 re-evaluates: doLeftTupleUpdate on orphan → modifyActiveTuple → NPE.
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    /**
     * Orphan via lock-on-active, triggered by external ksession.update().
     *
     * R1 (lock-on-active, agenda-group) fires → inserts new Cheese → new match
     * blocked by lock-on-active → orphan.
     * After the group deactivates, external update of Person + re-focus → NPE.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testLockOnActiveInsertThenExternalUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "package org.drools.reproducer\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "\n" +
                "rule R1\n" +
                "    agenda-group \"group1\"\n" +
                "    lock-on-active true\n" +
                "when\n" +
                "    $c : Cheese(price > 0)\n" +
                "    $p : Person(age > 0)\n" +
                "then\n" +
                "    insert(new Cheese(\"inserted\", $c.getPrice() + 100));\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        try {
            Cheese cheese = new Cheese("original", 1);
            Person person = new Person("john", 1);

            ksession.insert(cheese);
            FactHandle personHandle = ksession.insert(person);

            // Activate group and fire. R1 fires → inserts Cheese("inserted",101).
            // New match blocked by lock-on-active → orphan.
            ksession.getAgenda().getAgendaGroup("group1").setFocus();
            ksession.fireAllRules();

            // Modify Person while group is inactive (update recency < activation recency
            // when group is re-focused, so lock-on-active won't block the update).
            person.setAge(2);
            ksession.update(personHandle, person);

            // Re-focus and fire. doLeftTupleUpdate processes the UPDATE on the orphan.
            // lock-on-active check passes (fact was modified before group re-activation).
            // modifyActiveTuple → removeDormantTuple → NPE.
            ksession.getAgenda().getAgendaGroup("group1").setFocus();
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }

    /**
     * Orphan via no-loop, triggered by DELETE (retract) — single fireAllRules,
     * no external update, no modify.
     *
     * R1 (no-loop) fires → inserts a new fact → new match blocked by no-loop → orphan.
     * R2 retracts the inserted fact → LEFT DELETE propagates to orphan →
     * doLeftDelete → removeDormantTuple → NPE.
     *
     * This is the most realistic scenario: rules only use insert/retract (no modify),
     * and the NPE occurs within a single fireAllRules() call.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testNoLoopInsertRetractNoExternalUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        // R1: inserts a new Cheese("big", price+100) when it finds a cheap Cheese with a Person.
        //     The new Cheese re-matches R1 with the same Person → blocked by no-loop → orphan.
        //
        // R2: retracts any Cheese with price > 50. This covers the inserted Cheese("big", 101).
        //     Retracting it propagates a LEFT DELETE through R1's join node, hitting the orphan.
        final String drl =
                "package org.drools.reproducer\n" +
                "import " + Cheese.class.getCanonicalName() + "\n" +
                "import " + Person.class.getCanonicalName() + "\n" +
                "\n" +
                "rule R1\n" +
                "    no-loop true\n" +
                "    salience 10\n" +
                "when\n" +
                "    $c : Cheese(price > 0)\n" +
                "    $p : Person(age > 0)\n" +
                "then\n" +
                "    insert(new Cheese(\"big\", $c.getPrice() + 100));\n" +
                "end\n" +
                "\n" +
                "rule R2\n" +
                "    salience 1\n" +
                "when\n" +
                "    $c : Cheese(price > 50)\n" +
                "then\n" +
                "    retract($c);\n" +
                "end\n";

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);
        KieSession ksession = kbase.newKieSession();

        try {
            ksession.insert(new Cheese("original", 1));
            ksession.insert(new Person("john", 1));

            // R1 fires (salience 10): inserts Cheese("big",101) → orphan at R1's terminal.
            // R2 fires (salience 1): retracts Cheese("big",101) → LEFT DELETE on orphan.
            // doLeftDelete → removeDormantTuple on orphan → NPE.
            ksession.fireAllRules();
        } finally {
            ksession.dispose();
        }
    }
}
