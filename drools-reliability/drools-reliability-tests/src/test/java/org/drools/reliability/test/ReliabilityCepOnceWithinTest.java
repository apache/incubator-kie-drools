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

import org.drools.model.DroolsEntryPoint;
import org.drools.model.Global;
import org.drools.model.Index;
import org.drools.model.Model;
import org.drools.model.Rule;
import org.drools.model.impl.ModelImpl;
import org.drools.model.prototype.PrototypeDSL;
import org.drools.model.prototype.PrototypeVariable;
import org.drools.model.prototype.impl.HashMapEventImpl;
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
import static org.assertj.core.groups.Tuple.tuple;
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

@ExtendWith(BeforeAllMethodExtension.class)
class ReliabilityCepOnceWithinTest extends ReliabilityTestBasics {

    public static final String KEYWORD = "once_within";
    public static final String RULE_NAME = "R";

    /**
     * These rules are created in the same way as OnceWithinDefinition in drools-ansible-rulebook-integration
     */
    private Model ruleModel() {
        PrototypeEvent controlPrototype = getPrototypeEvent(SYNTHETIC_PROTOTYPE_NAME);
        Global<List> global = globalOf(List.class, "defaultpkg", "results");

        List<Rule> rules = new ArrayList<>();

        // main rule (match only once for grouped events within 10 minutes)
        TimeAmount timeAmount = TimeAmount.parseTimeAmount("10 minutes");
        PrototypeVariable originalEventVariable = variable(getPrototypeEvent(DEFAULT_PROTOTYPE_NAME), "m");
        rules.add(rule(RULE_NAME).metadata(RULE_TYPE_TAG, KEYWORD)
                          .build(
                                  guardedPattern(originalEventVariable),
                                  not(duplicateControlPattern(originalEventVariable)),
                                  on(originalEventVariable, global).execute((drools, event, globalResults) -> {
                                      PrototypeEventInstance controlEvent = controlPrototype.newInstance()
                                              .withExpiration(timeAmount.getAmount(), timeAmount.getTimeUnit());
                                      controlEvent.put("sensu.host", event.get("sensu.host")); // groupByAttributes
                                      controlEvent.put("sensu.process.type", event.get("sensu.process.type")); // groupByAttributes
                                      controlEvent.put("drools_rule_name", RULE_NAME);
                                      drools.insert(controlEvent);
                                      globalResults.add(event);
                                      drools.delete(event);
                                  })
                          )
        );

        // cleanup duplicate events rule
        rules.add(rule( "cleanup_" + RULE_NAME ).metadata(SYNTHETIC_RULE_TAG, true)
                          .build(
                                  guardedPattern(originalEventVariable),
                                  duplicateControlPattern(originalEventVariable),
                                  on(originalEventVariable).execute(DroolsEntryPoint::delete)
                          )
        );

        return new ModelImpl().withRules(rules).addGlobal(global);
    }

    // This is the pattern which we want to match
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
    void insertFailoverExpireFire_shouldCollectEventAfterWindow(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        insertMatchingSensuEvent("host1", "alert");
        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertNonMatchingSensuEvent("host1", "info");
        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertMatchingSensuEvent("host2", "alert");
        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertMatchingSensuEvent("host1", "alert"); // duplicate event
        advanceTimeAndFire(1, TimeUnit.MINUTES);

        assertThat(getResults()).as("once_within is 10 minutes window. 2 events should be collected")
                                .hasSize(2);

        failover();
        restoreSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        insertMatchingSensuEvent("host1", "alert"); // duplicate event
        advanceTimeAndFire(1, TimeUnit.MINUTES);

        assertThat(getResults()).as("once_within is 10 minutes window. 2 events should be collected")
                .hasSize(2);

        advanceTimeAndFire(10, TimeUnit.MINUTES); // controlEvent should be expired

        insertMatchingSensuEvent("host1", "alert"); // duplicate event, but 10 minutes window is over, so it should match
        fireAllRules();

        assertThat(getResults()).as("after 10 minutes window. 3 events should be collected")
                                .hasSize(3)
                                .allMatch(event -> event instanceof HashMapEventImpl)
                                .extracting(event -> ((HashMapEventImpl) event).get("sensu.host"), event -> ((HashMapEventImpl) event).get("sensu.process.type"))
                                .containsExactly(tuple("host1", "alert"),
                                                 tuple("host2", "alert"),
                                                 tuple("host1", "alert"));
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertExpireFailoverFire_shouldCollectEventAfterWindow(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        insertMatchingSensuEvent("host1", "alert");

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertNonMatchingSensuEvent("host1", "info");

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertMatchingSensuEvent("host2", "alert");

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertMatchingSensuEvent("host1", "alert"); // duplicate event

        advanceTimeAndFire(1, TimeUnit.MINUTES);

        assertThat(getResults()).as("once_within is 10 minutes window. 2 events should be collected")
                                .hasSize(2);

        advanceTime(10, TimeUnit.MINUTES); // controlEvent expire job should be triggered, but the action is still in propagationList. Will be lost by server crash

        failover();
        restoreSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO); // expire job is recreated and triggered

        insertMatchingSensuEvent("host1", "alert"); // duplicate event, but 10 minutes window is over, so it should match
        fireAllRules();

        assertThat(getResults()).as("after 10 minutes window. 3 events should be collected")
                                .hasSize(3)
                                .allMatch(event -> event instanceof HashMapEventImpl)
                                .extracting(event -> ((HashMapEventImpl) event).get("sensu.host"), event -> ((HashMapEventImpl) event).get("sensu.process.type"))
                                .containsExactly(tuple("host1", "alert"),
                                                 tuple("host2", "alert"),
                                                 tuple("host1", "alert"));
    }
}
