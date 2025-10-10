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

import org.drools.core.reteoo.builder.BiLinearDetector;
import org.drools.mvel.integrationtests.phreak.A;
import org.drools.mvel.integrationtests.phreak.B;
import org.drools.mvel.integrationtests.phreak.C;
import org.drools.mvel.integrationtests.phreak.D;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for scenarios where BiLinearJoinNodes are NOT created.
 */
public class BiLinearNodeNotFormedTest extends BiLinearTestBase {

    @Test
    public void testSameTypePatternChainNoBiLinear() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : A()\n" +
                        "    $d : A()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : A()\n" +
                        "    $c : A()\n" +
                        "    $d : A()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule3\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : A()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        assertNoBiLinearNodes(visitor, kieBase);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));

        int firedRules = session.fireAllRules();
        assertEquals(3, firedRules);

        session.dispose();
    }

    @Test
    public void testShortPatternsNoBiLinear() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $b : B()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $c : B()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        assertNoBiLinearNodes(visitor, kieBase);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));

        int firedRules = session.fireAllRules();
        assertEquals(2, firedRules);

        session.dispose();
    }

    @Test
    public void testEqualLengthRulesNoBiLinear() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $c : B()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        assertNoBiLinearNodes(visitor, kieBase);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));

        int firedRules = session.fireAllRules();
        assertEquals(2, firedRules);

        session.dispose();
    }

    @Test
    public void testEvalConditionNoBiLinear() {
        // Rules with eval conditions don't participate in BiLinear
        String drl =
                "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $s1 : String()\n" +
                        "    $s2 : String()\n" +
                        "    eval( 1 == 1 )\n" +
                        "then\n" +
                        "end\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $s1 : String()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule3\"\n" +
                        "when\n" +
                        "    $s1 : String()\n" +
                        "    $s2 : String()\n" +
                        "    $s3 : String()\n" +
                        "    eval( 1 == 1 )\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        assertNoBiLinearNodes(visitor, kieBase);

        KieSession session = kieBase.newKieSession();
        session.insert("test");

        int firedRules = session.fireAllRules();
        assertEquals(3, firedRules);

        session.dispose();
    }

    @Test
    public void testBiLinearDisabled() {
        BiLinearDetector.setBiLinearEnabled(false);

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
        assertNoBiLinearNodes(visitor, kieBase);
    }

    @Test
    public void testBiLinearNotFormedWithDifferentBetaConstraintOperators() {
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
                        "    $d : D(object < $cVal)\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        assertNoBiLinearNodes(visitor, kieBase);

        // Verify constraint evaluation still works correctly
        KieSession session1 = kieBase.newKieSession();
        session1.insert(new A(1));
        session1.insert(new B(1));
        session1.insert(new C(10));
        session1.insert(new D(20));
        session1.insert(new D(5));

        int firedRules1 = session1.fireAllRules();
        assertEquals(2, firedRules1);
        session1.dispose();
    }

    @Test
    public void testBiLinearNotFormedWithDifferentBetaConstraintEqualsVsGreater() {
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
                        "    $d : D(object == $cVal)\n" +
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
        assertNoBiLinearNodes(visitor, kieBase);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(1));
        session.insert(new B(1));
        session.insert(new C(10));
        session.insert(new D(10));
        session.insert(new D(20));

        int firedRules = session.fireAllRules();
        assertEquals(2, firedRules);
        session.dispose();
    }

    @Test
    public void testBiLinearNotFormedWithDifferentAlphaConstraints() {
        String drl =
                "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $c : C(object == 5)\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $c : C(object == 10)\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        assertNoBiLinearNodes(visitor, kieBase);

        KieSession session = kieBase.newKieSession();
        session.insert(new C(5));
        session.insert(new C(10));
        session.insert(new D(1));

        int fired = session.fireAllRules();
        assertEquals(2, fired);
        session.dispose();
    }

    @Test
    public void testBiLinearNotFormedWithDifferentAlphaOperators() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $c : C(object > 10)\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $c : C(object < 10)\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "end\n";

        KieBase kieBase = buildKieBase(drl);

        NetworkVisitor visitor = new NetworkVisitor();
        assertNoBiLinearNodes(visitor, kieBase);

        KieSession session = kieBase.newKieSession();
        session.insert(new A(1));
        session.insert(new C(5));
        session.insert(new C(15));  // Matches C(object > 10
        session.insert(new D(1));

        int fired = session.fireAllRules();
        assertEquals(2, fired);
        session.dispose();
    }

    @Test
    public void testBiLinearNotFormedWithExtraAlphaConstraint() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $c : C(object > 0)\n" +
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
        assertNoBiLinearNodes(visitor, kieBase);

        // Verify both rules work correctly
        KieSession session = kieBase.newKieSession();
        session.insert(new A(1));
        session.insert(new C(5));
        session.insert(new D(1));

        int fired = session.fireAllRules();
        assertEquals(2, fired);
        session.dispose();
    }

    @Test
    public void testBetaConstraintCrossPatternReference() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A($aVal : object)\n" +
                        "    $b : B()\n" +
                        "    $c : C(object > $aVal)\n" +
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
        assertNoBiLinearNodes(visitor, kieBase);

        KieSession session1 = kieBase.newKieSession();
        session1.insert(new A(5));
        session1.insert(new B(1));
        session1.insert(new C(10));
        session1.insert(new D(1));

        int firedRules1 = session1.fireAllRules();
        assertEquals(2, firedRules1);
        session1.dispose();

        KieSession session2 = kieBase.newKieSession();
        session2.insert(new A(5));
        session2.insert(new B(1));
        session2.insert(new C(3));
        session2.insert(new D(1));

        int firedRules2 = session2.fireAllRules();
        assertEquals(1, firedRules2);
        session2.dispose();
    }

    @Test
    public void testBiLinearNotAppliedWithCrossNetworkConstraint() {
        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A($aVal : object)\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D(object > $aVal)\n" +  // Cross-network: $aVal from first network, D in second
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

        // BiLinear should NOT be applied because of cross-network constraint
        assertNoBiLinearNodes(visitor, kieBase);

        // Rules should still work correctly using standard joins
        KieSession session1 = kieBase.newKieSession();
        session1.insert(new A(10));
        session1.insert(new B(1));
        session1.insert(new C(1));
        session1.insert(new D(20));  // 20 > 10, matches

        int firedRules1 = session1.fireAllRules();
        assertEquals(2, firedRules1, "Both rules should fire with standard joins");
        session1.dispose();

        KieSession session2 = kieBase.newKieSession();
        session2.insert(new A(10));
        session2.insert(new B(1));
        session2.insert(new C(1));
        session2.insert(new D(5));   // 5 NOT > 10, doesn't match Rule1

        int firedRules2 = session2.fireAllRules();
        assertEquals(1, firedRules2, "Only Rule2 should fire");
        session2.dispose();
    }
}
