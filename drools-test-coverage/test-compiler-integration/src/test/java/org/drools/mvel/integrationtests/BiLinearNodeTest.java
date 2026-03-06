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

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.mvel.integrationtests.phreak.A;
import org.drools.mvel.integrationtests.phreak.B;
import org.drools.mvel.integrationtests.phreak.C;
import org.drools.mvel.integrationtests.phreak.D;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for BiLinear optimization where BiLinearJoinNodes ARE created.
 * All tests have BiLinear enabled and verify that optimization is applied.
 */
public class BiLinearNodeTest extends BiLinearTestBase {

    @Test
    public void testBiLinear() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        assertEquals(2, firedRules);

        session.dispose();
    }

    @Test
    public void testBiLinearSwapOrder() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        assertEquals(2, firedRules);

        session.dispose();
    }

    @Test
    public void testBiLinear3RuleSetup() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule3\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        assertEquals(3, firedRules);

        session.dispose();
    }

    @Test
    public void testBiLinear3RuleSetupShort() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule3\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        assertEquals(3, firedRules);

        session.dispose();
    }

    @Test
    public void testBiLinear3RuleSetup2BiLinearNodes() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule3\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 2);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        assertEquals(3, firedRules);

        session.dispose();
    }

    @Test
    public void testBiLinearChain() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Rule3\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule4\"\n" +
                        "when\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 2);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        assertEquals(4, firedRules);

        session.dispose();
    }

    @Test
    public void testBiLinearWithSameVariableNames() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c1 : C()\n" +
                        "    $d1 : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $c2 : C()\n" +
                        "    $d2 : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(1));
        session.insert(new B(2));
        session.insert(new C(3));
        session.insert(new D(4));

        int firedRules = session.fireAllRules();
        assertEquals(2, firedRules);

        session.dispose();
    }

    @Test
    public void testBiLinearWithSamePropertyBindings() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A($obj : object)\n" +
                        "    $b : B()\n" +
                        "    $c : C($cObj : object)\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $c : C($cObj : object)\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(10));
        session.insert(new B(20));
        session.insert(new C(30));
        session.insert(new D(40));

        int firedRules = session.fireAllRules();
        assertEquals(2, firedRules);

        session.dispose();
    }

    @Test
    public void testBiLinearWithMatchingAlphaConstraints() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A(object > 10)\n" +
                        "    $c : C(object == 5)\n" +
                        "    $d : D(object < 100)\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $c : C(object == 5)\n" +
                        "    $d : D(object < 100)\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(15));
        session.insert(new C(5));
        session.insert(new D(50));

        int fired = session.fireAllRules();
        assertEquals(2, fired);
        session.dispose();
    }

    @Test
    public void testBiLinearWithMatchingAlphaConstraintsNoMatch() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A(object > 10)\n" +
                        "    $c : C(object == 5)\n" +
                        "    $d : D(object < 100)\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $c : C(object == 5)\n" +
                        "    $d : D(object < 100)\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new C(10));
        session.insert(new D(150));

        int fired = session.fireAllRules();
        assertEquals(0, fired);
        session.dispose();
    }

    @Test
    public void testBiLinearWithIdenticalBetaConstraints() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C($cVal : object)\n" +
                        "    $d : D(object > $cVal)\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $c : C($cVal : object)\n" +
                        "    $d : D(object > $cVal)\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session1 = kieBase.newKieSession();
        session1.insert(new A(1));
        session1.insert(new B(1));
        session1.insert(new C(10));
        session1.insert(new D(20));

        int firedRules1 = session1.fireAllRules();
        assertEquals(2, firedRules1);
        session1.dispose();

        KieSession session2 = kieBase.newKieSession();
        session2.insert(new A(1));
        session2.insert(new B(1));
        session2.insert(new C(10));
        session2.insert(new D(5));

        int firedRules2 = session2.fireAllRules();
        assertEquals(0, firedRules2);
        session2.dispose();
    }

    @Test
    public void testRemoveProviderRuleAfterBiLinearFormed() {
        String drl =
                "package org.drools.test\n" +
                        "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"ConsumerRule\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"ProviderRule\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) kieBase;

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session1 = kieBase.newKieSession();
        session1.insert(new A(1));
        session1.insert(new B(2));
        session1.insert(new C(3));
        session1.insert(new D(4));
        int firedBefore = session1.fireAllRules();
        assertEquals(2, firedBefore);
        session1.dispose();

        kbase.removeRule("org.drools.test", "ProviderRule");

        assertEquals(1, kieBase.getKiePackage("org.drools.test").getRules().size());

        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session2 = kieBase.newKieSession();
        session2.insert(new A(1));
        session2.insert(new B(2));
        session2.insert(new C(3));
        session2.insert(new D(4));

        session2.fireAllRules();
        session2.dispose();
    }

    @Test
    public void testRemoveConsumerRuleAfterBiLinearFormed() {
        String drl =
                "package org.drools.test\n" +
                        "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"ConsumerRule\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"ProviderRule\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) kieBase;

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session1 = kieBase.newKieSession();
        session1.insert(new A(1));
        session1.insert(new B(2));
        session1.insert(new C(3));
        session1.insert(new D(4));
        int firedBefore = session1.fireAllRules();
        assertEquals(2, firedBefore);
        session1.dispose();

        kbase.removeRule("org.drools.test", "ConsumerRule");

        assertEquals(1, kieBase.getKiePackage("org.drools.test").getRules().size());

        assertNoBiLinearNodes(visitor, kieBase);
    }

    @Test
    public void testRemoveBothRulesAfterBiLinearFormed() {
        String drl =
                "package org.drools.test\n" +
                        "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"ConsumerRule\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"ProviderRule\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);
        InternalKnowledgeBase kbase = (InternalKnowledgeBase) kieBase;

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        kbase.removeRule("org.drools.test", "ProviderRule");

        assertBiLinearNodeCount(visitor, kieBase, 1);

        kbase.removeRule("org.drools.test", "ConsumerRule");

        assertNoBiLinearNodes(visitor, kieBase);

        // Verify no rules fire after both removed
        KieSession session = kieBase.newKieSession();
        session.insert(new A(1));
        session.insert(new B(2));
        session.insert(new C(3));
        session.insert(new D(4));
        int firedAfter = session.fireAllRules();
        assertEquals(0, firedAfter);
        session.dispose();
    }

    @Test
    public void testBiLinearLeftDelete() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"ConsumerRule\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"ProviderRule\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();

        A a = new A(1);
        B b = new B(2);
        C c = new C(3);
        D d = new D(4);

        session.insert(a);
        session.insert(b);
        session.insert(c);
        session.insert(d);

        int firedFirst = session.fireAllRules();
        assertEquals(2, firedFirst, "Both rules should fire initially");

        session.delete(session.getFactHandle(a));

        session.insert(new C(30));
        session.insert(new D(40));

        int firedSecond = session.fireAllRules();
        assertEquals(3, firedSecond, "ProviderRule should fire for new C+D combinations");

        session.dispose();
    }

    @Test
    public void testBiLinearRightDelete() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"ConsumerRule\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"ProviderRule\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();

        A a = new A(1);
        B b = new B(2);
        C c = new C(3);
        D d = new D(4);

        session.insert(a);
        session.insert(b);
        session.insert(c);
        session.insert(d);

        int firedFirst = session.fireAllRules();
        assertEquals(2, firedFirst, "Both rules should fire initially");

        session.delete(session.getFactHandle(c));

        int firedSecond = session.fireAllRules();
        assertEquals(0, firedSecond, "No rules should fire after C is deleted");

        session.insert(new A(10));

        int firedThird = session.fireAllRules();
        assertEquals(0, firedThird, "No rules should fire without C");

        session.dispose();
    }

    @Test
    public void testBiLinearLeftUpdate() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"ConsumerRule\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"ProviderRule\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();

        A a = new A(1);
        B b = new B(2);
        C c = new C(3);
        D d = new D(4);

        session.insert(a);
        session.insert(b);
        session.insert(c);
        session.insert(d);

        int firedFirst = session.fireAllRules();
        assertEquals(2, firedFirst, "Both rules should fire initially");

        a.setObject(10);
        session.update(session.getFactHandle(a), a);

        int firedSecond = session.fireAllRules();
        assertEquals(1, firedSecond, "ConsumerRule should re-fire after updating A");

        session.dispose();
    }

    @Test
    public void testBiLinearRightUpdate() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"ConsumerRule\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"ProviderRule\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();

        A a = new A(1);
        B b = new B(2);
        C c = new C(3);
        D d = new D(4);

        session.insert(a);
        session.insert(b);
        session.insert(c);
        session.insert(d);

        int firedFirst = session.fireAllRules();
        assertEquals(2, firedFirst, "Both rules should fire initially");

        c.setObject(10);
        session.update(session.getFactHandle(c), c);

        int firedSecond = session.fireAllRules();
        assertEquals(2, firedSecond, "Both rules should re-fire after updating C");

        session.dispose();
    }

    /**
     * Tests that left-side updates still work correctly after right-side updates have occurred.
     * This is an edge case where the BiLinear right memory already has staged tuples.
     */
    @Test
    public void testBiLinearLeftUpdateAfterRightUpdate() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"ConsumerRule\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"ProviderRule\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);
        assertBiLinearNodeCount(visitor, kieBase, 1);

        KieSession session = kieBase.newKieSession();

        A a = new A(1);
        B b = new B(2);
        C c = new C(3);
        D d = new D(4);

        session.insert(a);
        session.insert(b);
        session.insert(c);
        session.insert(d);

        int firedFirst = session.fireAllRules();
        assertEquals(2, firedFirst, "Both rules should fire initially");

        // First update C (right side) to populate BiLinear right memory with updates
        c.setObject(30);
        session.update(session.getFactHandle(c), c);
        int firedAfterC = session.fireAllRules();
        assertEquals(2, firedAfterC, "Both rules should re-fire after updating C");

        // Now update A (left side) - this should still work after right-side updates
        a.setObject(10);
        session.update(session.getFactHandle(a), a);

        int firedAfterA = session.fireAllRules();
        assertEquals(1, firedAfterA, "ConsumerRule should re-fire after updating A");

        session.dispose();
    }
}
