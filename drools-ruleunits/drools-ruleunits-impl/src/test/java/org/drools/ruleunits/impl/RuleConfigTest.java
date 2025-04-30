/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.ruleunits.impl;

import java.util.concurrent.TimeUnit;

import org.drools.ruleunits.api.RuleUnitInstance;
import org.drools.ruleunits.api.RuleUnitProvider;
import org.drools.ruleunits.api.conf.ClockType;
import org.drools.ruleunits.api.conf.RuleConfig;
import org.drools.ruleunits.impl.listener.TestAgendaEventListener;
import org.drools.ruleunits.impl.listener.TestRuleEventListener;
import org.drools.ruleunits.impl.listener.TestRuleRuntimeEventListener;
import org.junit.jupiter.api.Test;
import org.kie.api.time.SessionClock;
import org.kie.api.time.SessionPseudoClock;

import static org.assertj.core.api.Assertions.assertThat;

class RuleConfigTest {

    @Test
    void addEventListeners() {
        TestAgendaEventListener testAgendaEventListener = new TestAgendaEventListener();
        TestRuleRuntimeEventListener testRuleRuntimeEventListener = new TestRuleRuntimeEventListener();
        TestRuleEventListener testRuleEventListener = new TestRuleEventListener();

        RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();
        ruleConfig.getAgendaEventListeners().add(testAgendaEventListener);
        ruleConfig.getRuleRuntimeListeners().add(testRuleRuntimeEventListener);
        ruleConfig.getRuleEventListeners().add(testRuleEventListener);

        HelloWorldUnit unit = new HelloWorldUnit();
        unit.getStrings().add("Hello World");

        try (RuleUnitInstance<HelloWorldUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit, ruleConfig)) {
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
            assertThat(testAgendaEventListener.getResults()).containsExactly("matchCreated : HelloWorld", "beforeMatchFired : HelloWorld", "afterMatchFired : HelloWorld");
            assertThat(testRuleRuntimeEventListener.getResults()).containsExactly("objectInserted : Hello World");
            assertThat(testRuleEventListener.getResults()).containsExactly("onBeforeMatchFire : HelloWorld", "onAfterMatchFire : HelloWorld");
        }
    }

    @Test
    void clockType() {
        // TimerUnit doesn't have @Clock(ClockType.PSEUDO) so REALTIME clock by default
        TimerUnit unit = new TimerUnit();
        unit.getStrings().add("Hello Timer");

        RuleConfig ruleConfig = RuleUnitProvider.get().newRuleConfig();
        ruleConfig.setClockType(ClockType.PSEUDO);
        try (RuleUnitInstance<TimerUnit> unitInstance = RuleUnitProvider.get().createRuleUnitInstance(unit, ruleConfig)) {
            SessionClock clock = unitInstance.getClock();
            assertThat(clock).isInstanceOf(SessionPseudoClock.class);
            SessionPseudoClock pseudoClock = (SessionPseudoClock) clock;

            assertThat(unitInstance.fire()).isZero();
            assertThat(unit.getResults()).isEmpty();

            pseudoClock.advanceTime(40L, TimeUnit.MINUTES);
            assertThat(unitInstance.fire()).isEqualTo(1);
            assertThat(unit.getResults()).containsExactly("it worked!");
        }
    }
}
