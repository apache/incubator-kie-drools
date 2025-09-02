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
package org.drools.model.codegen.execmodel;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivationGroupDualFactMatchTest extends BaseModelTest {

    public static class FactWrapper {

        private final String id;
        private boolean flag;

        public FactWrapper(String id) {
            this.id = id;
            this.flag = false;
        }

        public String getId() {
            return id;
        }

        public boolean isFlag() {
            return flag;
        }

        public void setFlag(boolean flag) {
            this.flag = flag;
        }

        @Override
        public String toString() {
            return "FactWrapper{id='" + id + "', flag=" + flag + '}';
        }
    }

    /**
     * Drools Rule Language (DRL) definition containing three interconnected rules
     * that demonstrate activation group behavior with agenda group transitions.
     * <p>
     * Rule Definitions:
     * <p>
     * "Setup Rule - GroupA":
     * - Triggers on dynamic facts with flag=false
     * - Modifies the fact to set flag=true
     * - Switches agenda focus to GroupB
     * - Not in any activation group (can fire multiple times)
     * <p>
     * "Rule A - GroupA":
     * - Requires both static fact (flag=true) AND dynamic fact (flag=true)
     * - In activation-group "TestGroup"
     * - Should NEVER fire due to activation group mutual exclusion
     * - Also in GroupA agenda group
     * <p>
     * "Rule B - GroupB":
     * - Requires only dynamic fact (flag=true)
     * - In activation-group "TestGroup" (same as Rule A)
     * - Fires first, preventing Rule A from executing
     * - Executes in GroupB agenda group
     * <p>
     * Execution Flow:
     * 1. Setup Rule fires, modifies dynamic fact, switches to GroupB
     * 2. Rule B fires in GroupB (satisfies activation group)
     * 3. Rule A cannot fire because activation group is already satisfied
     */
    private static final String DRL = """
            package com.example
            
            import %s.FactWrapper;
            
            global org.kie.api.runtime.KieSession ksession;
            
            rule "Setup Rule - GroupA"
                agenda-group "GroupA"
                when
                    $d: FactWrapper(id matches "dynamic-\\\\d+", flag == false)
                then
                    modify($d) { setFlag(true) };
                    ksession.getAgenda().getAgendaGroup("GroupB").setFocus();
                end
            
            // This rule should never fire, but fires twice. Switching the order of the conditions
            // (putting StaticFact first) will make the test pass.
            rule "Rule A - GroupA"
                agenda-group "GroupA"
                activation-group "TestGroup"
                when
                    DynamicFact: FactWrapper(id matches "dynamic-.*", flag == true)
                    StaticFact: FactWrapper(id == "static", flag == true)
                then
                end
            
            rule "Rule B - GroupB"
                agenda-group "GroupB"
                activation-group "TestGroup"
                when
                    DynamicFact: FactWrapper(id matches "dynamic-.*", flag == true)
                then
                end
            """.formatted(ActivationGroupDualFactMatchTest.class.getName());

    @ParameterizedTest
    @MethodSource("parameters")
    void testRuleAMatchingTwoFactsButNeverFires(RUN_TYPE runType) {
        // === PHASE 1: Rule Compilation and Knowledge Base Setup ===

        final KieSession kSession = getKieSession(runType, DRL);

        // === PHASE 2: Event Listener Setup for Rule Execution Tracking ===

        // Track which rules fire during execution for validation
        final List<String> firedRules = new ArrayList<>();

        // Add event listener to capture rule firing events
        kSession.addEventListener(new DefaultAgendaEventListener() {
            @Override
            public void afterMatchFired(final AfterMatchFiredEvent event) {
                // Record the name of each rule that fires
                firedRules.add(event.getMatch().getRule().getName());
            }
        });

        // Set the global variable that rules use to manipulate agenda focus
        kSession.setGlobal("ksession", kSession);

        // === PHASE 3: Fact Insertion - Setting Up the Test Scenario ===

        // Insert the static fact with flag=true (required for Rule A to match)
        // This fact will remain constant throughout the test
        final FactWrapper staticFact = new FactWrapper("static");
        staticFact.setFlag(true);  // Pre-set to true so Rule A can potentially match
        kSession.insert(staticFact);

        // Insert multiple dynamic facts with flag=false (triggers for Setup Rule)
        // These facts will be modified by the Setup Rule to flag=true
        for (int i = 1; i <= 3; i++) {
            // Each dynamic fact starts with flag=false, matching Setup Rule conditions
            kSession.insert(new FactWrapper("dynamic-%d".formatted(i)));
        }

        // === PHASE 4: Rule Execution - The Core Test Logic ===

        // Set initial agenda focus to GroupA where Setup Rule and Rule A reside
        kSession.getAgenda().getAgendaGroup("GroupA").setFocus();

        // Execute all rules - this is where the complex interaction happens:
        // 1. Setup Rule fires for each dynamic fact (3 times)
        // 2. Each Setup Rule execution modifies a dynamic fact and switches to GroupB
        // 3. Rule B fires in GroupB for each modified dynamic fact (3 times)
        // 4. Rule A never fires despite having matching conditions (activation group prevents it)
        kSession.fireAllRules();

        // === PHASE 5: Validation - Verify Expected Behavior ===

        final boolean ruleAFired = firedRules.contains("Rule A - GroupA");
        final long ruleBFires = firedRules.stream().filter(r -> r.equals("Rule B - GroupB")).count();
        final long setupFires = firedRules.stream().filter(r -> r.equals("Setup Rule - GroupA")).count();

        // Validate the expected execution pattern
        assertThat(setupFires).as("Setup rule should fire 3 times (once per dynamic fact).").isEqualTo(3);
        assertThat(ruleBFires).as("Rule B should fire 3 times (once per setup cycle).").isEqualTo(3);
        assertThat(ruleAFired).as("Rule A should NOT have fired due to activation group mutual exclusion.").isFalse();

        // Clean up resources
        kSession.dispose();
    }
}