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
package org.drools.compiler.integrationtests;

import java.util.Collection;
import java.util.HashSet;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;

import static org.assertj.core.api.Assertions.assertThat;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

// DROOLS-1010
@RunWith(Parameterized.class)
public class AlphaNodeSharingWithDiffPackageNameTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public AlphaNodeSharingWithDiffPackageNameTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }

    public static class TypeA {

        private final int parentId = 2;
        private final int id = 3;

        public int getParentId() {
            return parentId;
        }

        public int getId() {
            return id;
        }

        private String alphaNode;
        private HashSet<String> firings = new HashSet<>();

        public HashSet<String> getFirings() {
            if (firings == null) {
                firings = new HashSet<>();
            }
            return firings;
        }

        public void setFirings(final HashSet<String> x) {
            firings = x;
        }

        private final String data = "AlphaNodeHashingThreshold Data";

        public String getData() {
            return data;
        }

        public String getAlphaNode() {
            return alphaNode;
        }

        public void setAlphaNode(final String alphaNode) {
            this.alphaNode = alphaNode;
        }
    }

    public static class TypeB {

        private final int parentId = 1;
        private final int id = 2;

        public int getParentId() {
            return parentId;
        }

        public int getId() {
            return id;
        }
    }

    public static class TypeC {

        private final int parentId = 0;
        private final int id = 1;

        public int getParentId() {
            return parentId;
        }

        public int getId() {
            return id;
        }
    }

    public static class TypeD {

    }

    public static class TypeE {

    }

    private static final String rule1 = "package com.test.rule1;\r\n" +
            "\r\n" +
            "import " + TypeA.class.getCanonicalName() + ";\r\n" +
            "import " + TypeB.class.getCanonicalName() + ";\r\n" +
            "import " + TypeC.class.getCanonicalName() + ";\r\n" +
            "           \r\n" +
            "rule R1\r\n" +
            "when\r\n" +
            "   $c : TypeC()\r\n" +
            "   $b : TypeB(parentId == $c.Id)\r\n" +
            "   $a : TypeA( parentId == $b.Id, firings not contains \"R1 Fired\")\r\n" +
            "then\r\n" +
            "   $a.setAlphaNode(\"value contains TypeD TypeE data type\");\r\n" +
            "   $a.getFirings().add(\"R1 Fired\");\r\n" +
            "   update($a);\r\n" +
            "end";

    private static final String rule2 = "package com.test.rule2;\r\n" +
            "\r\n" +
            "import " + TypeA.class.getCanonicalName() + ";\r\n" +
            "import " + TypeB.class.getCanonicalName() + ";\r\n" +
            "import " + TypeC.class.getCanonicalName() + ";\r\n" +
            "\r\n" +
            "rule R2 \r\n" +
            "when\r\n" +
            "   $c : TypeC()\r\n" +
            "   $b : TypeB(parentId == $c.Id)\r\n" +
            "   $a : TypeA(parentId == $b.Id, \r\n" +
            "               alphaNode==\"value contains TypeD TypeE data type\", \r\n" +
            "               firings not contains \"R2 Fired\")\r\n" +
            "then\r\n" +
            "       $a.getFirings().add(\"R2 Fired\");\r\n" +
            "       update($a);\r\n" +
            "end";

    private static final String rule3 = "package com.test.rule3;\r\n" +
            "\r\n" +
            "import " + TypeA.class.getCanonicalName() + ";\r\n" +
            "import " + TypeB.class.getCanonicalName() + ";\r\n" +
            "import " + TypeC.class.getCanonicalName() + ";\r\n" +
            "import " + TypeD.class.getCanonicalName() + ";\r\n" +
            "\r\n" +
            "rule R3 \r\n" +
            "when\r\n" +
            "   $d : TypeD()\r\n" +
            "   $c : TypeC()\r\n" +
            "   $b : TypeB(parentId == $c.Id)\r\n" +
            "   $a : TypeA( parentId == $b.Id,\r\n" +
            "               alphaNode==\"value contains TypeD TypeE data type\", \r\n" +
            "               firings not contains \"R3 Fired\")\r\n" +
            "then\r\n" +
            "   $a.getFirings().add(\"R3 Fired\");\r\n" +
            "   update($a);\r\n" +
            "end;";

    private static final String rule4 = "package com.test.rule4;\r\n" +
            "\r\n" +
            "import " + TypeA.class.getCanonicalName() + ";\r\n" +
            "import " + TypeB.class.getCanonicalName() + ";\r\n" +
            "import " + TypeC.class.getCanonicalName() + ";\r\n" +
            "import " + TypeE.class.getCanonicalName() + ";\r\n" +
            "\r\n" +
            "rule R4 \r\n" +
            "when\r\n" +
            "   $e : TypeE()\r\n" +
            "   $c : TypeC()\r\n" +
            "   $b : TypeB(parentId == $c.Id)\r\n" +
            "   $a : TypeA( parentId == $b.Id,\r\n" +
            "               alphaNode==\"value contains TypeD TypeE data type\", \r\n" +
            "               firings not contains \"R4 Fired\")\r\n" +
            "then\r\n" +
            "   $a.getFirings().add(\"R4 Fired\");\r\n" +
            "   update($a);\r\n" +
            "end;";

    @Test
    public void testAlphaNode() {

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("alpha-node-sharing-test", kieBaseTestConfiguration, rule1, rule2, rule3, rule4);
        final KieSession ksession = kbase.newKieSession();
        try {
            final TypeC c = new TypeC();
            final TypeB b = new TypeB();
            final TypeA a = new TypeA();
            final TypeD d = new TypeD();
            final TypeE e = new TypeE();

            ksession.insert(a);
            ksession.insert(b);
            ksession.insert(c);
            ksession.insert(d);
            ksession.insert(e);

            ksession.fireAllRules();

            assertThat(a.getFirings().contains("R1 Fired")).isTrue();
            assertThat(a.getFirings().contains("R2 Fired")).isTrue();
            assertThat(a.getFirings().contains("R3 Fired")).isTrue();
            assertThat(a.getFirings().contains("R4 Fired")).isTrue();
        } finally {
            ksession.dispose();
        }
    }
}