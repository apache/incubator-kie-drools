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
package org.drools.reliability.test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.drools.model.Global;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.Variable;
import org.drools.model.impl.ModelImpl;
import org.drools.model.prototype.PrototypeDSL;
import org.drools.model.prototype.PrototypeVariable;
import org.drools.reliability.test.util.TimeAmount;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.kie.api.conf.EventProcessingOption;
import org.kie.api.prototype.PrototypeEvent;
import org.kie.api.prototype.PrototypeEventInstance;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.conf.PersistedSessionOption;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.accumulate;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.globalOf;
import static org.drools.model.DSL.not;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.prototype.PrototypeDSL.protoPattern;
import static org.drools.model.prototype.PrototypeDSL.variable;
import static org.drools.model.prototype.PrototypeExpression.fixedValue;
import static org.drools.model.prototype.PrototypeExpression.prototypeField;
import static org.drools.reliability.test.util.PrototypeUtils.DEFAULT_PROTOTYPE_NAME;
import static org.drools.reliability.test.util.PrototypeUtils.SYNTHETIC_PROTOTYPE_NAME;
import static org.drools.reliability.test.util.PrototypeUtils.getPrototypeEvent;
import static org.drools.reliability.test.util.PrototypeUtils.processResults;

@ExtendWith(BeforeAllMethodExtension.class)
class ReliabilityCepOnceAfterTest extends ReliabilityTestBasics {

    public static final String KEYWORD = "once_after";
    public static final String RULE_NAME = "R";

    /**
     * These rules are created in the same way as OnceAfterDefinition in drools-ansible-rulebook-integration
     */
    private Model ruleModel() {
        PrototypeEvent controlPrototype = getPrototypeEvent(SYNTHETIC_PROTOTYPE_NAME);
        PrototypeVariable controlVar1 = variable(controlPrototype, "c1");
        PrototypeVariable controlVar2 = variable(controlPrototype, "c2");
        PrototypeVariable controlVar3 = variable(controlPrototype, "c3");
        Variable<List> resultsVar = declarationOf(List.class, "results");
        Global<List> global = globalOf(List.class, "defaultpkg", "results");

        List<Rule> rules = new ArrayList<>();

        // main rule (accumulate events within 10 minutes)
        rules.add(
                  rule(RULE_NAME).metadata(RULE_TYPE_TAG, KEYWORD)
                          .build(
                                  protoPattern(controlVar1).expr("end_once_after", Index.ConstraintType.EQUAL, RULE_NAME),
                                  not(protoPattern(controlVar2).expr("start_once_after", Index.ConstraintType.EQUAL, RULE_NAME)),
                                  accumulate(protoPattern(controlVar3).expr("drools_rule_name", Index.ConstraintType.EQUAL, RULE_NAME),
                                             accFunction(org.drools.core.base.accumulators.CollectListAccumulateFunction::new, controlVar3).as(resultsVar)),
                                  on(controlVar1, resultsVar, global).execute((drools, controlFact, resultFactList, globalResults) -> {
                                      drools.delete(controlFact);
                                      processResults(globalResults, resultFactList);
                                      resultFactList.forEach(drools::delete);
                                  })
                          )
        );

        // control rule (wrapping original event)
        PrototypeVariable originalEventVariable = variable(getPrototypeEvent(DEFAULT_PROTOTYPE_NAME), "m");
        rules.add(
                  rule(RULE_NAME + "_control").metadata(SYNTHETIC_RULE_TAG, true)
                          .build(
                                  guardedPattern(originalEventVariable),
                                  not(duplicateControlPattern(originalEventVariable)),
                                  on(originalEventVariable).execute((drools, event) -> {
                                      PrototypeEventInstance controlEvent = controlPrototype.newInstance();
                                      controlEvent.put("sensu.host", event.get("sensu.host")); // groupByAttributes
                                      controlEvent.put("sensu.process.type", event.get("sensu.process.type")); // groupByAttributes
                                      controlEvent.put("drools_rule_name", RULE_NAME);
                                      controlEvent.put("event", event);
                                      controlEvent.put("once_after_time_window", "10 minutes");
                                      controlEvent.put("events_in_window", 1);
                                      drools.insert(controlEvent);
                                      drools.delete(event);
                                  })
                          )
        );

        // start rule (insert start and end control events. start event expires in 10 minutes, so the main rule will fire)
        TimeAmount timeAmount = TimeAmount.parseTimeAmount("10 minutes");
        rules.add(
                  rule(RULE_NAME + "_start").metadata(SYNTHETIC_RULE_TAG, true)
                          .build(
                                  protoPattern(controlVar1).expr("drools_rule_name", Index.ConstraintType.EQUAL, RULE_NAME),
                                  not(protoPattern(controlVar2).expr("end_once_after", Index.ConstraintType.EQUAL, RULE_NAME)),
                                  on(controlVar1).execute((drools, c1) -> {
                                      PrototypeEventInstance startControlEvent = controlPrototype.newInstance()
                                              .withExpiration(timeAmount.getAmount(), timeAmount.getTimeUnit());
                                      startControlEvent.put("start_once_after", RULE_NAME);
                                      drools.insert(startControlEvent);

                                      PrototypeEventInstance endControlEvent = controlPrototype.newInstance();
                                      endControlEvent.put("end_once_after", RULE_NAME);
                                      drools.insert(endControlEvent);
                                  })
                          )
        );

        // cleanup duplicate events rule
        PrototypeDSL.PrototypePatternDef duplicateControlPattern = duplicateControlPattern(originalEventVariable);
        rules.add(
                  rule(RULE_NAME + "_cleanup_duplicate").metadata(SYNTHETIC_RULE_TAG, true)
                          .build(
                                  guardedPattern(originalEventVariable),
                                  duplicateControlPattern,
                                  on(originalEventVariable, duplicateControlPattern.getFirstVariable()).execute((drools, event, control) -> {
                                      control.put("events_in_window", ((int) control.get("events_in_window")) + 1);
                                      drools.delete(event);
                                  })
                          )
        );

        return new ModelImpl().withRules(rules).addGlobal(global);
    }

    // This is the pattern which we want to accumulate
    private static PrototypeDSL.PrototypePatternDef guardedPattern(PrototypeVariable originalEventVariable) {
        return protoPattern(originalEventVariable).expr(prototypeField("sensu.process.type"), Index.ConstraintType.EQUAL, fixedValue("alert"));
    }

    // We group-by sensu.host and sensu.process.type
    private static PrototypeDSL.PrototypePatternDef duplicateControlPattern(PrototypeVariable originalEventVariable) {
        return protoPattern(variable(getPrototypeEvent(SYNTHETIC_PROTOTYPE_NAME)))
                .expr(prototypeField("sensu.host"), Index.ConstraintType.EQUAL, originalEventVariable, prototypeField("sensu.host")) // groupByAttributes
                .expr(prototypeField("sensu.process.type"), Index.ConstraintType.EQUAL, originalEventVariable, prototypeField("sensu.process.type")) // groupByAttributes
                .expr(prototypeField("drools_rule_name"), Index.ConstraintType.EQUAL, fixedValue(RULE_NAME));
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertFailoverAdvanceFire_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        insertMatchingSensuEvent("host1", "alert");

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertNonMatchingSensuEvent("host1", "info");

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertMatchingSensuEvent("host2", "alert");

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertMatchingSensuEvent("host1", "alert"); // duplicate event

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        assertThat(getResults()).as("once_after is 10 minutes window. The main rule should not be fired yet")
                .isEmpty();

        failover();
        restoreSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        advanceTimeAndFire(7, TimeUnit.MINUTES);

        assertThat(getResults()).as("10 minutes window is over. The result should be collected")
                .hasSize(2);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertAdvanceFailoverFire_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        insertMatchingSensuEvent("host1", "alert");

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertNonMatchingSensuEvent("host1", "info");

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertMatchingSensuEvent("host2", "alert");

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertMatchingSensuEvent("host1", "alert"); // duplicate event

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        assertThat(getResults()).as("once_after is 10 minutes window. The main rule should not be fired yet")
                .isEmpty();

        advanceTime(7, TimeUnit.MINUTES); // controlEvent expire job should be triggered, but the action is still in propagationList. Will be lost by server crash

        // advanceTime, then server crash before fireAllRules
        // Generally this doesn't happen in drools-ansible because AutomaticPseudoClock does advanceTime + fireAllRules atomically, but simulating a crash in the middle.

        failover();
        restoreSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO); // expire job is recreated and triggered

        fireAllRules();

        assertThat(getResults()).as("10 minutes window is over. The result should be collected")
                .hasSize(2);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertAdvanceInsertFailoverFire_shouldRecoverFromFailover(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        insertMatchingSensuEvent("host1", "alert");

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertNonMatchingSensuEvent("host1", "info");

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertMatchingSensuEvent("host2", "alert");

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        assertThat(getResults()).as("once_after is 10 minutes window. The main rule should not be fired yet")
                .isEmpty();

        advanceTime(8, TimeUnit.MINUTES);

        // advanceTime, insert events, then server crashes before fireAllRules
        // This doesn't happen in drools-ansible because AutomaticPseudoClock does advanceTime + fireAllRules atomically, but testing to confirm the restore logic.

        // These events are not fired before a crash, so will be listed in "notPropagated", so wouldn't bother "nextFireTime" calculation of "start_once_after" expiration
        insertMatchingSensuEvent("host3", "alert");
        insertMatchingSensuEvent("host4", "alert");
        insertMatchingSensuEvent("host5", "alert");
        insertMatchingSensuEvent("host6", "alert");

        failover();
        restoreSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        fireAllRules();

        assertThat(getResults()).as("2 is the normally expected result size, but in this edge case, 6 events are collected")
                .hasSize(6);
    }
}
