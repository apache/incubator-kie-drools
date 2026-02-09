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

import org.drools.base.common.NetworkNode;
import org.drools.mvel.integrationtests.phreak.A;
import org.drools.mvel.integrationtests.phreak.B;
import org.drools.mvel.integrationtests.phreak.C;
import org.drools.mvel.integrationtests.phreak.D;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.utils.KieHelper;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BiLinearTest {

    @BeforeEach
    public void setUp() {
        System.setProperty("drools.bilinear.enabled", "true");
    }

    @AfterEach
    public void cleanup() {
        System.clearProperty("drools.bilinear.enabled");
    }

    @Test
    public void testBiLinear() {
        System.out.println("\n🔍 Testing BiLinear functionality...");

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
                        "    System.out.println(\"Rule1 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule2 fired\" );\n" +
                        "end\n";

        System.out.println("🔧 Building KieBase with BiLinear enabled...");
        KieBase kieBase = buildKieBase(drl);

        System.out.println("📊 Network structure:");
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);

        System.out.println("\n🚀 Testing execution...");
        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        System.out.println("📈 Rules fired: " + firedRules);

        assertEquals(2, firedRules);

        assertBiLinearNodeCount(visitor, kieBase, 1);

        session.dispose();
        System.out.println("✅ BiLinear test completed");
    }


    @Test
    public void testBiLinearSwapOrder() {
        System.out.println("\n🔍 Testing BiLinear functionality...");

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
                        "    System.out.println(\"Rule1 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule2 fired\" );\n" +
                        "end\n";

        System.out.println("🔧 Building KieBase with BiLinear enabled...");
        KieBase kieBase = buildKieBase(drl);

        System.out.println("📊 Network structure:");
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);

        System.out.println("\n🚀 Testing execution...");
        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        System.out.println("📈 Rules fired: " + firedRules);

        assertEquals(2, firedRules);

        assertBiLinearNodeCount(visitor, kieBase, 1);

        session.dispose();
        System.out.println("✅ BiLinear test completed");
    }



    @Test
    public void testBiLinear3RuleSetup() {
        System.out.println("\n🔍 Testing BiLinear functionality...");

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
                        "    System.out.println(\"Rule1 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule2 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule3\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule3 fired\" );\n" +
                        "end\n";

        System.out.println("🔧 Building KieBase with BiLinear enabled...");
        KieBase kieBase = buildKieBase(drl);

        System.out.println("📊 Network structure:");
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);

        System.out.println("\n🚀 Testing execution...");
        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        System.out.println("📈 Rules fired: " + firedRules);

        assertEquals(3, firedRules);

        assertBiLinearNodeCount(visitor, kieBase, 1);

        session.dispose();
        System.out.println("✅ BiLinear test completed");
    }


    @Test
    public void testBiLinear3RuleSetupShort() {
        System.out.println("\n🔍 Testing BiLinear functionality...");

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
                        "    System.out.println(\"Rule1 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule2 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule3\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule3 fired\" );\n" +
                        "end\n";

        System.out.println("🔧 Building KieBase with BiLinear enabled...");
        KieBase kieBase = buildKieBase(drl);

        System.out.println("📊 Network structure:");
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);

        System.out.println("\n🚀 Testing execution...");
        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        System.out.println("📈 Rules fired: " + firedRules);

        assertEquals(3, firedRules);

        assertBiLinearNodeCount(visitor, kieBase, 1);

        session.dispose();
        System.out.println("✅ BiLinear test completed");
    }

    @Test
    public void testBiLinear3RuleSetup2() {
        System.out.println("\n🔍 Testing BiLinear functionality...");

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
                        "    System.out.println(\"Rule1 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule2 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule3\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule3 fired\" );\n" +
                        "end\n";

        System.out.println("🔧 Building KieBase with BiLinear enabled...");
        KieBase kieBase = buildKieBase(drl);

        System.out.println("📊 Network structure:");
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);

        System.out.println("\n🚀 Testing execution...");
        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        System.out.println("📈 Rules fired: " + firedRules);

        assertEquals(3, firedRules);

        assertBiLinearNodeCount(visitor, kieBase, 2);

        session.dispose();
        System.out.println("✅ BiLinear test completed");
    }

    @Test
    public void testBiLinearSayAAAAAAAAA() {
        System.out.println("\n🔍 Testing BiLinear functionality...");

        System.setProperty("drools.bilinear.enabled", "false");

        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : A()\n" +
                        "    $d : A()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule1 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : A()\n" +
                        "    $c : A()\n" +
                        "    $d : A()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule2 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule3\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $b : A()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule3 fired\" );\n" +
                        "end\n";

        System.out.println("🔧 Building KieBase with BiLinear enabled...");
        KieBase kieBase = buildKieBase(drl);

        System.out.println("📊 Network structure:");
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);

        System.out.println("\n🚀 Testing execution...");
        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));

        int firedRules = session.fireAllRules();
        System.out.println("📈 Rules fired: " + firedRules);

        assertNoBiLinearNodes(visitor, kieBase);

        assertEquals(3, firedRules);

        session.dispose();
        System.out.println("✅ BiLinear test completed");
    }

    @Test
    public void testBiLinearShortNoBiLinear() {
        System.out.println("\n🔍 Testing BiLinear functionality...");

        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $b : B()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule1 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $c : B()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule2 fired\" );\n" +
                        "end\n";

        System.out.println("🔧 Building KieBase with BiLinear enabled...");
        KieBase kieBase = buildKieBase(drl);

        System.out.println("📊 Network structure:");
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);

        System.out.println("\n🚀 Testing execution...");
        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        System.out.println("📈 Rules fired: " + firedRules);

        assertNoBiLinearNodes(visitor, kieBase);

        assertEquals(2, firedRules);

        session.dispose();
        System.out.println("✅ BiLinear test completed");
    }


    @Test
    public void testBiLinearEqualRulesNoBiLinear() {
        System.out.println("\n🔍 Testing BiLinear functionality...");

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
                        "then\n" +
                        "    System.out.println(\"Rule1 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $a : A()\n" +
                        "    $c : B()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule2 fired\" );\n" +
                        "end\n";

        System.out.println("🔧 Building KieBase with BiLinear enabled...");
        KieBase kieBase = buildKieBase(drl);

        System.out.println("📊 Network structure:");
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);

        System.out.println("\n🚀 Testing execution...");
        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        System.out.println("📈 Rules fired: " + firedRules);

        assertNoBiLinearNodes(visitor, kieBase);

        assertEquals(2, firedRules);

        session.dispose();
        System.out.println("✅ BiLinear test completed");
    }


    @Test
    public void testBiLinearChain() {
        System.out.println("\n🔍 Testing BiLinear functionality...");

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
                        "    System.out.println(\"Rule1 fired\" );\n" +
                        "end\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $b : B()\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule2 fired\" );\n" +
                        "end\n" +
                        "rule \"Rule3\"\n" +
                        "when\n" +
                        "    $c : C()\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule3 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule4\"\n" +
                        "when\n" +
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule4 fired\" );\n" +
                        "end\n";

        System.out.println("🔧 Building KieBase with BiLinear enabled...");
        KieBase kieBase = buildKieBase(drl);

        System.out.println("📊 Network structure:");
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);

        System.out.println("\n🚀 Testing execution...");
        KieSession session = kieBase.newKieSession();
        session.insert(new A(5));
        session.insert(new B(10));
        session.insert(new C(10));
        session.insert(new D(10));

        int firedRules = session.fireAllRules();
        System.out.println("📈 Rules fired: " + firedRules);

        assertEquals(4, firedRules);

        assertBiLinearNodeCount(visitor, kieBase, 2);

        session.dispose();
        System.out.println("✅ BiLinear test completed");
    }


    @Test
    public void testBiLinearNoNetworkShouldBeMade() {
        System.out.println("\n🔍 Testing BiLinear functionality...");

        String drl =
                "import " + A.class.getCanonicalName() + "\n" +
                        "import " + B.class.getCanonicalName() + "\n" +
                        "import " + C.class.getCanonicalName() + "\n" +
                        "import " + D.class.getCanonicalName() + "\n" +
                        "\n" +
                        "rule \"Rule1\"\n" +
                        "when\n" +
                        "    $s1 : String()\n" +
                        "    $s2 : String()\n" +
                        "    eval( 1 == 1 )\n" +
                        "then\n" +
                        "    System.out.println(\"Rule1 fired\" );\n" +
                        "end\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $s1 : String()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule2 fired\" );\n" +
                        "end\n" +
                        "\n" +
                        "\n" +
                        "rule \"Rule3\"\n" +
                        "when\n" +
                        "    $s1 : String()\n" +
                        "    $s2 : String()\n" +
                        "    $s3 : String()\n" +
                        "    eval( 1 == 1 )\n" +
                        "then\n" +
                        "    System.out.println(\"Rule3 fired\" );\n" +
                        "end\n";

        System.out.println("🔧 Building KieBase with BiLinear enabled...");
        KieBase kieBase = buildKieBase(drl);

        System.out.println("📊 Network structure:");
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);

        System.out.println("\n🚀 Testing execution...");
        KieSession session = kieBase.newKieSession();
        session.insert("test");

        int firedRules = session.fireAllRules();
        System.out.println("📈 Rules fired: " + firedRules);

        assertNoBiLinearNodes(visitor, kieBase);

        assertEquals(3, firedRules);

        session.dispose();
        System.out.println("✅ BiLinear test completed");
    }

    private KieBase buildKieBase(String drl) {
        // Note: BiLinear property should be set by the calling test before calling this method
        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);
        return kieHelper.build();
    }

    @Test
    public void testBiLinearJoinNodesPresentInNetwork() {
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
        assertBiLinearNodeCount(visitor, kieBase, 1);

        System.out.println("BiLinear network structure:");
        visitor.debugNetworkStructure(kieBase);
    }

    private void assertBiLinearNodeCount(NetworkVisitor visitor, KieBase kieBase, int bilinearNodeCount) {

        List<NetworkNode> biLinearNodes = visitor.findBiLinearJoinNodes(kieBase);

        assertThat(biLinearNodes)
                .as("BiLinear optimization should create BiLinearJoinNode(s)")
                .isNotEmpty();

        assertThat(biLinearNodes).hasSize(bilinearNodeCount);
    }

    @Test
    public void testNoBiLinearJoinNodesWhenDisabled() {
        System.setProperty("drools.bilinear.enabled", "false");

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

        System.out.println("Non-BiLinear network structure:");
        visitor.debugNetworkStructure(kieBase);
    }

    private void assertNoBiLinearNodes(NetworkVisitor visitor, KieBase kieBase) {
        List<NetworkNode> biLinearNodes = visitor.findBiLinearJoinNodes(kieBase);

        assertThat(biLinearNodes)
                .as("No BiLinearJoinNodes when bilinear is disabled")
                .isEmpty();

    }

    @Test
    public void testBiLinearWithSameVariableNames() {
        System.out.println("\n Testing BiLinear with same variable names (naming conflict)...");

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
                        "    System.out.println(\"Rule1 fired with $c1=\" + $c1 + \", $d1=\" + $d1);\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $c2 : C()\n" +
                        "    $d2 : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule2 fired with $c2=\" + $c2 + \", $d2=\" + $d2);\n" +
                        "end\n";

        System.out.println("Building KieBase with potential naming conflicts...");
        KieBase kieBase = buildKieBase(drl);

        System.out.println("Network structure:");
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);

        System.out.println("\nTesting execution...");
        KieSession session = kieBase.newKieSession();
        session.insert(new A(1));
        session.insert(new B(2));
        session.insert(new C(3));
        session.insert(new D(4));

        int firedRules = session.fireAllRules();
        System.out.println("Rules fired: " + firedRules);

        assertEquals(2, firedRules);

        assertBiLinearNodeCount(visitor, kieBase, 1);

        session.dispose();
        System.out.println("BiLinear naming conflict test completed");
    }

    @Test
    public void testBiLinearWithSamePropertyBindings() {
        System.out.println("\n Testing BiLinear with same property binding names...");

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
                        "    $c : C($cObj : object)\n" +  // Binding name $cObj
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule1: obj=\" + $obj + \", cObj=\" + $cObj);\n" +
                        "end\n" +
                        "\n" +
                        "rule \"Rule2\"\n" +
                        "when\n" +
                        "    $c : C($cObj : object)\n" +  // Same binding name $cObj as Rule1
                        "    $d : D()\n" +
                        "then\n" +
                        "    System.out.println(\"Rule2: cObj=\" + $cObj);\n" +
                        "end\n";

        System.out.println("Building KieBase...");
        KieBase kieBase = buildKieBase(drl);

        System.out.println("Network structure:");
        NetworkVisitor visitor = new NetworkVisitor();
        visitor.debugNetworkStructure(kieBase);

        System.out.println("\nTesting execution...");
        KieSession session = kieBase.newKieSession();
        session.insert(new A(10));
        session.insert(new B(20));
        session.insert(new C(30));
        session.insert(new D(40));

        int firedRules = session.fireAllRules();
        System.out.println("Rules fired: " + firedRules);

        assertEquals(2, firedRules);

        assertBiLinearNodeCount(visitor, kieBase, 1);

        session.dispose();
        System.out.println("BiLinear property bindings test completed");
    }
}