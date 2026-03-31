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
package org.drools.compiler.integrationtests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class DormantTupleNPETest {

    public static Stream<KieBaseTestConfiguration> parameters() {
        return TestParametersUtil2.getKieBaseCloudConfigurations(true).stream();
    }

    public static class Coupon {
        private Double value;
        private Map<String, String> characteristics = new HashMap<>();

        public Coupon() {}

        public Coupon(Double value, int stage, boolean promoted, boolean stackable) {
            this.value = value;
            this.characteristics.put("stage", String.valueOf(stage));
            this.characteristics.put("promoted", String.valueOf(promoted));
            this.characteristics.put("stackable", String.valueOf(stackable));
        }

        public Double getValue() { return value; }
        public void setValue(Double value) { this.value = value; }
        public Map<String, String> getCharacteristics() { return characteristics; }
        public void setCharacteristics(Map<String, String> characteristics) { this.characteristics = characteristics; }
    }

    public static class StageContext {
        private int currentStage;
        private int nextStage;
        private int finalStage;
        private boolean initialized;

        public StageContext() {}

        public StageContext(int currentStage, int nextStage, int finalStage) {
            this.currentStage = currentStage;
            this.nextStage = nextStage;
            this.finalStage = finalStage;
            this.initialized = true;
        }

        public int getCurrentStage() { return currentStage; }
        public void setCurrentStage(int currentStage) { this.currentStage = currentStage; }
        public int getNextStage() { return nextStage; }
        public void setNextStage(int nextStage) { this.nextStage = nextStage; }
        public int getFinalStage() { return finalStage; }
        public void setFinalStage(int finalStage) { this.finalStage = finalStage; }
        public boolean isInitialized() { return initialized; }
        public void setInitialized(boolean initialized) { this.initialized = initialized; }

        public void advanceStage() {
            this.currentStage = this.nextStage;
            this.nextStage = this.nextStage + 1;
        }
    }

    /**
     * Reproduces the NPE from issue 6422 comment issuecomment-4157177207:
     * collect + insert + update with different salience levels and no-loop causes
     * removeDormantTuple to be called on a tuple not in the dormant list during
     * eager re-evaluation triggered by ruleWithHigherSalienceActivated.
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testCollectWithInsertAndUpdateNoDormantNPE(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import " + Coupon.class.getCanonicalName() + ";\n" +
                "import " + StageContext.class.getCanonicalName() + ";\n" +
                "import " + List.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Combine stackable coupons\"\n" +
                "no-loop true\n" +
                "salience 20\n" +
                "when\n" +
                "  $ctx : StageContext(\n" +
                "    initialized == true,\n" +
                "    $currentStage : currentStage,\n" +
                "    $nextStage : nextStage,\n" +
                "    $finalStage : finalStage,\n" +
                "    currentStage != finalStage\n" +
                "  )\n" +
                "  $coupons : List(size > 0) from collect(\n" +
                "    Coupon(\n" +
                "      value != null,\n" +
                "      Integer.valueOf(characteristics[\"stage\"]) == $nextStage,\n" +
                "      Boolean.valueOf(characteristics[\"promoted\"]) == true,\n" +
                "      Boolean.valueOf(characteristics[\"stackable\"]) == false\n" +
                "    )\n" +
                "  )\n" +
                "then\n" +
                "  results.add(\"combine\");\n" +
                "  Coupon combined = new Coupon();\n" +
                "  combined.setValue(0.0);\n" +
                "  for (Object o : $coupons) {\n" +
                "    combined.setValue(combined.getValue() + ((Coupon) o).getValue());\n" +
                "  }\n" +
                "  insert(combined);\n" +
                "  $ctx.advanceStage();\n" +
                "  update($ctx);\n" +
                "end\n" +
                "\n" +
                "rule \"Process single coupon\"\n" +
                "no-loop true\n" +
                "salience 10\n" +
                "when\n" +
                "  $ctx : StageContext(\n" +
                "    initialized == true,\n" +
                "    $currentStage : currentStage,\n" +
                "    $nextStage : nextStage\n" +
                "  )\n" +
                "  $coupons : List(size > 0) from collect(\n" +
                "    Coupon(\n" +
                "      value != null,\n" +
                "      Integer.valueOf(characteristics[\"stage\"]) == $nextStage,\n" +
                "      Boolean.valueOf(characteristics[\"promoted\"]) == true\n" +
                "    )\n" +
                "  )\n" +
                "then\n" +
                "  results.add(\"process\");\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl(
                "dormant-tuple-npe-test", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();

        try {
            final List<String> results = new ArrayList<>();
            session.setGlobal("results", results);

            session.insert(new StageContext(1, 2, 3));
            session.insert(new Coupon(10.0, 2, true, false));
            session.insert(new Coupon(20.0, 2, true, false));
            session.insert(new Coupon(5.0, 2, true, true));

            // This should not throw NPE in removeDormantTuple
            session.fireAllRules();

            assertThat(results).isNotEmpty();
        } finally {
            session.dispose();
        }
    }

    /**
     * Tests the modifyActiveTuple path: multiple rules with collect where one rule's
     * update invalidates another rule's collected facts, causing modify on a tuple
     * that was removed from active with staged DELETE (skipping dormant addition).
     */
    @ParameterizedTest(name = "KieBase type={0}")
    @MethodSource("parameters")
    public void testMultipleCollectRulesWithStagedUpdate(KieBaseTestConfiguration kieBaseTestConfiguration) {
        final String drl =
                "import " + Coupon.class.getCanonicalName() + ";\n" +
                "import " + StageContext.class.getCanonicalName() + ";\n" +
                "import " + List.class.getCanonicalName() + ";\n" +
                "global java.util.List results;\n" +
                "\n" +
                "rule \"Stage1 - collect and advance\"\n" +
                "no-loop true\n" +
                "salience 30\n" +
                "when\n" +
                "  $ctx : StageContext( initialized == true, currentStage == 1 )\n" +
                "  $coupons : List(size > 0) from collect(\n" +
                "    Coupon( value != null, Integer.valueOf(characteristics[\"stage\"]) == 1 )\n" +
                "  )\n" +
                "then\n" +
                "  results.add(\"stage1\");\n" +
                "  for (Object o : $coupons) {\n" +
                "    Coupon c = (Coupon) o;\n" +
                "    c.getCharacteristics().put(\"stage\", \"2\");\n" +
                "    update(c);\n" +
                "  }\n" +
                "  $ctx.advanceStage();\n" +
                "  update($ctx);\n" +
                "end\n" +
                "\n" +
                "rule \"Stage2 - combine and insert\"\n" +
                "no-loop true\n" +
                "salience 20\n" +
                "when\n" +
                "  $ctx : StageContext( initialized == true, currentStage == 2 )\n" +
                "  $coupons : List(size > 0) from collect(\n" +
                "    Coupon( value != null, Integer.valueOf(characteristics[\"stage\"]) == 2 )\n" +
                "  )\n" +
                "then\n" +
                "  results.add(\"stage2\");\n" +
                "  Coupon combined = new Coupon(0.0, 3, false, false);\n" +
                "  for (Object o : $coupons) {\n" +
                "    combined.setValue(combined.getValue() + ((Coupon) o).getValue());\n" +
                "  }\n" +
                "  insert(combined);\n" +
                "  $ctx.advanceStage();\n" +
                "  update($ctx);\n" +
                "end\n" +
                "\n" +
                "rule \"Stage3 - finalize\"\n" +
                "no-loop true\n" +
                "salience 10\n" +
                "when\n" +
                "  $ctx : StageContext( initialized == true, currentStage == 3 )\n" +
                "  $coupons : List(size > 0) from collect(\n" +
                "    Coupon( value != null, Integer.valueOf(characteristics[\"stage\"]) == 3 )\n" +
                "  )\n" +
                "then\n" +
                "  results.add(\"stage3\");\n" +
                "end\n";

        final KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl(
                "dormant-tuple-npe-test2", kieBaseTestConfiguration, drl);
        final KieSession session = kbase.newKieSession();

        try {
            final List<String> results = new ArrayList<>();
            session.setGlobal("results", results);

            session.insert(new StageContext(1, 2, 3));
            session.insert(new Coupon(10.0, 1, true, false));
            session.insert(new Coupon(20.0, 1, true, false));
            session.insert(new Coupon(5.0, 1, true, true));

            // This should not throw NPE in removeDormantTuple
            session.fireAllRules();

            assertThat(results).contains("stage1");
        } finally {
            session.dispose();
        }
    }
}