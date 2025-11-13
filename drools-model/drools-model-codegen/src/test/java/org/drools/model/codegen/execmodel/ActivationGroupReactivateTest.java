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
import org.drools.core.event.TrackingAgendaEventListener;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class ActivationGroupReactivateTest extends BaseModelTest {

	public static class StringFact {
		private String value;
		private int count = 0;

		public StringFact(String value) {
			this.value = value;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @param value the value to set
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * @return the count
		 */
		public int getCount() {
			return count;
		}

		/**
		 * @param count the count to set
		 */
		public void setCount(int count) {
			this.count = count;
		}

		public void incrementCount() {
			this.count++;
		}

		@Override
		public String toString() {
			return "StringFact: value=" + value + ", count=" + count;
		}
	}

    /**
     * Drools Rule Language (DRL) definition containing three interconnected rules
     * that demonstrate activation group behavior on reactivation due to fact change.
     * 
     * <p>
     * Rule Definitions:
     * <p>
     * "First rule in first activation group":
     * - Fires for pre-asserted StringFact
     * - In activation-group "first-group"
     * - salience 10: Should fire first
     * <p>
     * "Second rule in first activation group":
     * - Activated by pre-asserted StringFact
     * - In activation-group "first-group"
     * - salience 5: Should activate after first rule
     * - Should NEVER fire due to activation group mutual exclusion
     * <p>
     * "Rule without activation group":
     * - salience 2: fires last
     * - No activation group
     * - Fires second, triggering a StringFact update
     *   Activates first and second rules a second time
     * <p>
     * Execution Flow:
     * 1. First rule fires, cancels second rule activation
     * 2. Third rule fires, triggers activation for first 2 rules
     * 3. First rule fires, cancels second rule activation
     */
    private static final String DRL = """
            package com.example
            
            import %s.StringFact;
            
			rule "First rule in first activation group"
			    activation-group "first-group"
				salience 10
			when
				$strf : StringFact(value == "force", count < 5)
			then
			end

			rule "Second rule in first activation group"
			    activation-group "first-group"
				salience 5
			when
				$strf : StringFact(value == "force", count < 5)
			then
				modify($strf) { setValue("fail") }
			end

			rule "Rule without activation group"
				salience 2
			when
				$strf : StringFact(value == "force", count < 1)
			then
				modify($strf) { incrementCount() }
			end
            """.formatted(ActivationGroupReactivateTest.class.getName());

    @ParameterizedTest
    @MethodSource("parameters")
    void testReactivatedRulesInSingleActivationGroup(RUN_TYPE runType) {
        // === PHASE 1: Rule Compilation and Knowledge Base Setup ===

        final KieSession kSession = getKieSession(runType, DRL);

        // === PHASE 2: Event Listener Setup for Rule Execution Tracking ===

        // Track which rules fire during execution for validation
        TrackingAgendaEventListener eventListener = new TrackingAgendaEventListener();

        // Add event listener to capture rule firing events
        kSession.addEventListener(eventListener);

        // === PHASE 3: Fact Insertion - Setting Up the Test Scenario ===

		kSession.insert(new StringFact("force"));

        // === PHASE 4: Rule Execution - The Core Test Logic ===

        // Execute all rules
        kSession.fireAllRules();

        // === PHASE 5: Validation - Verify Expected Behavior ===

        final long firstRuleFireCount = eventListener.getAfterMatchFired().stream().filter(r -> r.equals("First rule in first activation group")).count();
		final boolean secondRuleFired = eventListener.getAfterMatchFired().contains("Second rule in first activation group");
        final long thirdRuleFireCount = eventListener.getAfterMatchFired().stream().filter(r -> r.equals("Rule without activation group")).count();

        // Validate the expected execution pattern
        assertThat(firstRuleFireCount).as("First rule should fire twice").isEqualTo(2);
        assertThat(secondRuleFired).as("Second rule should not fire").isFalse();
        assertThat(thirdRuleFireCount).as("Third rule should fire once").isEqualTo(1);

        // Clean up resources
        kSession.dispose();
    }
}
