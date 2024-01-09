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
import org.kie.api.runtime.rule.RuleContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.model.DSL.accFunction;
import static org.drools.model.DSL.accumulate;
import static org.drools.model.DSL.after;
import static org.drools.model.DSL.declarationOf;
import static org.drools.model.DSL.globalOf;
import static org.drools.model.DSL.not;
import static org.drools.model.DSL.on;
import static org.drools.model.PatternDSL.pattern;
import static org.drools.model.PatternDSL.rule;
import static org.drools.model.prototype.PrototypeDSL.protoPattern;
import static org.drools.model.prototype.PrototypeDSL.variable;
import static org.drools.model.prototype.PrototypeExpression.prototypeField;
import static org.drools.model.prototype.PrototypeExpression.thisPrototype;
import static org.drools.reliability.test.util.PrototypeUtils.DEFAULT_PROTOTYPE_NAME;
import static org.drools.reliability.test.util.PrototypeUtils.SYNTHETIC_PROTOTYPE_NAME;
import static org.drools.reliability.test.util.PrototypeUtils.getPrototypeEvent;

@ExtendWith(BeforeAllMethodExtension.class)
class ReliabilityCepTimedOutTest extends ReliabilityTestBasics {

    public static final String KEYWORD = "timed_out";
    public static final String RULE_NAME = "R";

    /**
     * These rules are created in the same way as TimedOutDefinition in drools-ansible-rulebook-integration
     */
    private Model ruleModel() {
        PrototypeEvent controlPrototype = getPrototypeEvent(SYNTHETIC_PROTOTYPE_NAME);
        PrototypeVariable controlVar1 = variable(controlPrototype, "c1");
        PrototypeVariable controlVar2 = variable(controlPrototype, "c2");
        PrototypeVariable controlVar3 = variable(controlPrototype, "c3");
        PrototypeVariable eventVar = variable(getPrototypeEvent(DEFAULT_PROTOTYPE_NAME), "e");
        Variable<Long> resultCount = declarationOf( Long.class, "count" );
        Global<List> global = globalOf(List.class, "defaultpkg", "results");

        List<Rule> rules = new ArrayList<>();

        // main rule (match only after 5 minutes, but not all sub-rules are matched)
        String startTag = "start_" + RULE_NAME;
        String endTag = "end_" + RULE_NAME;
        TimeAmount timeAmount = TimeAmount.parseTimeAmount("5 minutes");

        rules.add(
                  rule( RULE_NAME ).metadata(RULE_TYPE_TAG, KEYWORD)
                    .build(
                            protoPattern(controlVar1).expr( "rulename", Index.ConstraintType.EQUAL, startTag ),
                            not( protoPattern(controlVar2)
                                    .expr( "rulename", Index.ConstraintType.EQUAL, endTag )
                                    .expr( after(0, timeAmount.getTimeUnit(), timeAmount.getAmount(), timeAmount.getTimeUnit()), controlVar1 ) ),
                            on(controlVar1, global).execute((drools, controlFact, globalResults) -> {
                                globalResults.add(RULE_NAME);
                                drools.delete(controlFact);
                            })
            )
        );

        String rulePrefix = RULE_NAME + "_";

        // sub-rule 0
        String subRuleName0 = rulePrefix + "0";
        PrototypeVariable patternVariable0 = variable(getPrototypeEvent(DEFAULT_PROTOTYPE_NAME), "m_0");
        rules.add(
                  rule( subRuleName0 ).metadata(SYNTHETIC_RULE_TAG, true)
                     .build(
                             protoPattern(patternVariable0)
                                     .expr("ping.timeout", Index.ConstraintType.EQUAL, true),
                             not( protoPattern(controlVar1)
                                     .expr( "rulename", Index.ConstraintType.EQUAL, subRuleName0 ) ),
                             on(patternVariable0).execute((drools, t1) -> {
                                 PrototypeEventInstance controlEvent = controlPrototype.newInstance()
                                         .withExpiration(timeAmount.getAmount(), timeAmount.getTimeUnit());
                                 controlEvent.put("rulename", subRuleName0 );
                                 controlEvent.put("event", t1 );
                                 controlEvent.put("binding", ((RuleContext) drools).getMatch().getDeclarationIds().get(0) );
                                 drools.insert(controlEvent);
                             })
                     )
        );

        // sub-rule 1
        String subRuleName1 = rulePrefix + "1";
        PrototypeVariable patternVariable1 = variable(getPrototypeEvent(DEFAULT_PROTOTYPE_NAME), "m_1");
        rules.add(
                  rule( subRuleName1 ).metadata(SYNTHETIC_RULE_TAG, true)
                     .build(
                             protoPattern(patternVariable1)
                                     .expr("sensu.process.status", Index.ConstraintType.EQUAL, "stopped"),
                             not( protoPattern(controlVar1)
                                     .expr( "rulename", Index.ConstraintType.EQUAL, subRuleName1 ) ),
                             on(patternVariable1).execute((drools, t1) -> {
                                 PrototypeEventInstance controlEvent = controlPrototype.newInstance()
                                         .withExpiration(timeAmount.getAmount(), timeAmount.getTimeUnit());
                                 controlEvent.put("rulename", subRuleName1 );
                                 controlEvent.put("event", t1 );
                                 controlEvent.put("binding", ((RuleContext) drools).getMatch().getDeclarationIds().get(0) );
                                 drools.insert(controlEvent);
                             })
                     )
        );

        // sub-rule 2
        String subRuleName2 = rulePrefix + "2";
        PrototypeVariable patternVariable2 = variable(getPrototypeEvent(DEFAULT_PROTOTYPE_NAME), "m_2");
        rules.add(
                  rule( subRuleName2 ).metadata(SYNTHETIC_RULE_TAG, true)
                     .build(
                             protoPattern(patternVariable2)
                                     .expr("sensu.storage.percent", Index.ConstraintType.GREATER_THAN, 95),
                             not( protoPattern(controlVar1)
                                     .expr( "rulename", Index.ConstraintType.EQUAL, subRuleName2 ) ),
                             on(patternVariable2).execute((drools, t1) -> {
                                 PrototypeEventInstance controlEvent = controlPrototype.newInstance()
                                         .withExpiration(timeAmount.getAmount(), timeAmount.getTimeUnit());
                                 controlEvent.put("rulename", subRuleName2 );
                                 controlEvent.put("event", t1 );
                                 controlEvent.put("binding", ((RuleContext) drools).getMatch().getDeclarationIds().get(0) );
                                 drools.insert(controlEvent);
                             })
                      )
        );

        // start rule
        rules.add(
                  rule(startTag).metadata(SYNTHETIC_RULE_TAG, true)
                      .build(
                              not( protoPattern(controlVar1).expr( "rulename", Index.ConstraintType.EQUAL, startTag ) ),
                              protoPattern(controlVar2).expr( p -> ((String)p.get("rulename")).startsWith(rulePrefix) ),
                              on(controlVar2).execute((drools, firstEvent) -> {
                                  PrototypeEventInstance controlEvent = controlPrototype.newInstance()
                                          .withExpiration(timeAmount.getAmount(), timeAmount.getTimeUnit());
                                  controlEvent.put("rulename", startTag );
                                  controlEvent.put("event", firstEvent.get("event") );
                                  controlEvent.put("binding", firstEvent.get("binding") );
                                  drools.insert(controlEvent);
                              })
                      )
        );

        // end rule
        rules.add(
                  rule(endTag).metadata(SYNTHETIC_RULE_TAG, true)
                      .build(
                              protoPattern(controlVar1).expr( "rulename", Index.ConstraintType.EQUAL, startTag ),
                              accumulate( protoPattern(controlVar2).expr(p -> ((String)p.get("rulename")).startsWith(rulePrefix)),
                                      accFunction(org.drools.core.base.accumulators.CountAccumulateFunction::new).as(resultCount)),
                              pattern(resultCount).expr(count -> count == 3),
                              on(resultCount).execute((drools, count) -> {
                                  PrototypeEventInstance controlEvent = controlPrototype.newInstance()
                                          .withExpiration(timeAmount.getAmount(), timeAmount.getTimeUnit());
                                  controlEvent.put("rulename", endTag );
                                  drools.insert(controlEvent);
                              })
                      )
        );

        // cleanup rule 1
        rules.add(
                  rule( rulePrefix + "cleanupEvents" ).metadata(SYNTHETIC_RULE_TAG, true)
                      .build(
                              protoPattern(controlVar1).expr( "rulename", Index.ConstraintType.EQUAL, endTag ),
                              protoPattern(controlVar2).expr(p -> ((String)p.get("rulename")).startsWith(rulePrefix)),
                              protoPattern(eventVar).expr( thisPrototype(), Index.ConstraintType.EQUAL, controlVar2, prototypeField("event") ),
                              on(controlVar2, eventVar).execute((drools, c, e) -> {
                                  drools.delete(e);
                                  drools.delete(c);
                              })
                      )
        );

        // cleanup rule 2
        rules.add(
                  rule( rulePrefix + "cleanupEvents2" ).metadata(SYNTHETIC_RULE_TAG, true)
                      .build(
                              protoPattern(controlVar1).expr( "rulename", Index.ConstraintType.EQUAL, startTag ),
                              not( protoPattern(controlVar2)
                                      .expr( "rulename", Index.ConstraintType.EQUAL, endTag )
                                      .expr( after(0, timeAmount.getTimeUnit(), timeAmount.getAmount(), timeAmount.getTimeUnit()), controlVar1 ) ),
                              protoPattern(controlVar3).expr(p -> ((String)p.get("rulename")).startsWith(rulePrefix)),
                              protoPattern(eventVar).expr( thisPrototype(), Index.ConstraintType.EQUAL, controlVar3, prototypeField("event") ),
                              on(controlVar1, controlVar3, eventVar).execute((drools, c1, c3, e) -> {
                                  drools.delete(e);
                                  drools.delete(c3);
                                  drools.delete(c1);
                              })
                      )
        );

        // cleanup rule 3
        rules.add(
                  rule( rulePrefix + "cleanupTerminal" ).metadata(SYNTHETIC_RULE_TAG, true)
                      .build(
                              protoPattern(controlVar1).expr( "rulename", Index.ConstraintType.EQUAL, startTag ),
                              protoPattern(controlVar2).expr( "rulename", Index.ConstraintType.EQUAL, endTag ),
                              on(controlVar1, controlVar2).execute((drools, c1, c2) -> {
                                  drools.delete(c1);
                                  drools.delete(c2);
                              })
                      )
        );

        return new ModelImpl().withRules(rules).addGlobal(global);
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertFailoverExpireFire_shouldFire(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        insertMatchingSensuProcessStatusEvent("stopped");
        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertMatchingPingTimeoutEvent(true);
        advanceTimeAndFire(1, TimeUnit.MINUTES);

        assertThat(getResults()).as("timeout is 5 minutes window. Not yet fired")
                                .isEmpty();

        failover();
        restoreSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        advanceTimeAndFire(5, TimeUnit.MINUTES); // main rule's temporal constraint is evaluated

        assertThat(getResults()).as("after 5 minutes window. the main rule is fired")
                                .hasSize(1);

        assertThat(getFactHandles()).as("All events should be cleaned up")
                                    .isEmpty();
    }

    @ParameterizedTest
    @MethodSource("strategyProviderStoresOnlyWithExplicitSafepoints")
    void insertExpireFailoverFire_shouldFire(PersistedSessionOption.PersistenceStrategy persistenceStrategy, PersistedSessionOption.SafepointStrategy safepointStrategy) {

        createSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO);

        insertMatchingSensuProcessStatusEvent("stopped");
        advanceTimeAndFire(1, TimeUnit.MINUTES);

        insertMatchingPingTimeoutEvent(true);
        advanceTimeAndFire(1, TimeUnit.MINUTES);

        advanceTime(5, TimeUnit.MINUTES); // TimerNodeJob is triggered, but the action is still in propagationList. Will be lost by server crash

        assertThat(getResults()).as("5 minutes timeout is over. But not yet fired")
                                .isEmpty();

        failover();
        restoreSession(ruleModel(), persistenceStrategy, safepointStrategy, EventProcessingOption.STREAM, ClockTypeOption.PSEUDO); // TimerNodeJob is recreated and triggered

        fireAllRules();

        assertThat(getResults()).as("after 5 minutes window. the main rule is fired")
                                .hasSize(1);

        assertThat(getFactHandles()).as("All events should be cleaned up")
                                    .isEmpty();
    }
}
