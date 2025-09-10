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
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.BeforeMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class FiringOrderTest extends BaseModelTest {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(FiringOrderTest.class);

    public static class State {

        private int value;
        private boolean aFired;

        public State(int value, boolean aFired) {
            this.value = value;
            this.aFired = aFired;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public boolean isAFired() {
            return aFired;
        }

        public void setAFired(boolean aFired) {
            this.aFired = aFired;
        }
    }

    private static final String DRL_INSERT_FACT = """
                                      package com.example.drools
                                      
                                      import %s.State;
                                      
                                      global java.util.List firingOrder;
                                      
                                      // Rule A: in ruleflow-group XGroup. It modifies State so that B and C become eligible.
                                      rule "A"
                                          ruleflow-group "XGroup"
                                      when
                                          $s : State(aFired == false, value == 0)
                                      then
                                          // Mark A as fired and set value to 1 so that B condition is met
                                          modify($s) { setAFired(true), setValue(1) };
                                      
                                          // Insert new fact to make C eligible (modification of existing fact does not necessarily trigger auto-focus)
                                          insert(new State(2, false));
                                      end
                                      
                                      // Rule B: in ruleflow-group XGroup (same as A).
                                      rule "B"
                                          ruleflow-group "XGroup"
                                      when
                                          State(value == 1, aFired == true)
                                      then
                                      end
                                      
                                      // Rule C: in ruleflow-group YGroup with auto-focus true.
                                      // When this rule becomes active, it will push its ruleflow group to the focus stack.
                                      rule "C"
                                          ruleflow-group "YGroup"
                                          auto-focus true
                                      when
                                          State(value == 2, aFired == false)
                                      then
                                      end
                                      """.formatted(FiringOrderTest.class.getName());

    @ParameterizedTest
    @MethodSource("parameters")
    void ruleCActivatesBeforeRuleBInsertTest(RUN_TYPE runType) {
        final KieSession kieSession = getKieSession(runType, DRL_INSERT_FACT);

        try {
            final List<String> firingOrder = new ArrayList<>();

            // Listener to capture firing order for B and C
            final AgendaEventListener listener = new DefaultAgendaEventListener() {
                @Override
                public void beforeMatchFired(BeforeMatchFiredEvent event) {
                    firingOrder.add(event.getMatch().getRule().getName());
                }
            };
            kieSession.addEventListener(listener);

            // Global for firing order from the DRL RHS for additional verification
            kieSession.setGlobal("firingOrder", firingOrder);

            // Initial fact to trigger A
            State state = new State(0, false);
            kieSession.insert(state);

            // Ensure we start in ruleflow-group X so A fires first
            kieSession.getAgenda().getAgendaGroup("XGroup").setFocus();

            final int fired = kieSession.fireAllRules();

            // We expect all three to have fired
            assertThat(fired).as("Expected three rules to fire: A, C, B").isEqualTo(3);

            // Verify firing order: A will first, then C (due to auto-focus to ruleflow-group Y), then B
            assertThat(firingOrder).as("Firing order should be [A, C, B]").containsExactly("A", "C", "B");
        } finally {
            kieSession.dispose();
        }
    }
}
