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
package org.drools.mvel.integrationtests;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.TestParametersUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.KieBase;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Ignore
@RunWith(Parameterized.class)
public class WorkingMemoryActionsSerializationTest {

    private final KieBaseTestConfiguration kieBaseTestConfiguration;

    public WorkingMemoryActionsSerializationTest(final KieBaseTestConfiguration kieBaseTestConfiguration) {
        this.kieBaseTestConfiguration = kieBaseTestConfiguration;
    }

    @Parameterized.Parameters(name = "KieBase type={0}")
    public static Collection<Object[]> getParameters() {
        return TestParametersUtil.getKieBaseCloudConfigurations(true);
    }
    private static final List<String> RULES = Arrays.asList("enableRecording", "saveRecord", "processEvent", "ignoreEvent"); //rules expected to be executed
    private KieSession ksession;
    private KieBase kbase;

    private final Map<String, Integer> ruleCalls = new HashMap<String, Integer>();

    //private final String accessor = "getEventName()";
    private final String accessor = "eventName";

    private final String drl =
               "package apackage\n" +
               " \n" +
               "import " + KnowledgeHelper.class.getCanonicalName() + "\n" +
               "import " + AnEvent.class.getCanonicalName() + "\n" +
               " \n" +
               "declare DoRecord end\n" +
               "\n" +
               "rule \"enableRecording\"\n" +
               "  salience 100\n" +
               "when\n" +
               "  AnEvent() from entry-point \"game stream\"\n" +
               "then\n" +
               "  drools.getEntryPoint(\"internal stream\").insert(new DoRecord());\n" +
               "end\n" +
               "\n" +
               "rule \"saveRecord\"\n" +
               "  salience -100\n" +
               "when\n" +
               "  $event : DoRecord() from entry-point \"internal stream\"\n" +
               "then\n" +
               "  retract($event);\n" +
               "  //save record\n" +
               "end\n" +
               " \n" +
               "rule \"ignoreEvent\"\n" +
               "  salience 40\n" +
               "when\n" +
               "  $discardCardEvent2 : AnEvent(" + accessor + " == \"discardCardIrr\") from entry-point \"game stream\"\n" +
               "then\n" +
               "  retract($discardCardEvent2);\n" +
               "  //This rule is intended to remove the event and ignore it\n" +
               "  //ignore this message\n" +
               "end\n" +
               "\n" +
               "rule \"processEvent\"\n" +
               "when\n" +
               "  $discardCardEvent : AnEvent(" + accessor + " == \"discardCard\") from entry-point \"game stream\"\n" +
               "then\n" +
               "  retract($discardCardEvent);\n" +
               "  //side effects go here\n" +
               "end";

    @Before
    public void before() {
        ruleCalls.clear();

        KieBase kbase = KieBaseUtil.getKieBaseFromKieModuleFromDrl("test", kieBaseTestConfiguration, drl);

        ksession = kbase.newKieSession();

        ksession.addEventListener(new DefaultAgendaEventListener() {
            @Override
            public void afterMatchFired(final AfterMatchFiredEvent event) {
                final String name = event.getMatch().getRule().getName();
                System.out.println(name + " fired!");
                synchronized (ruleCalls) {
                    Integer calls = ruleCalls.get(name);
                    if (calls == null) {
                        calls = 1;
                    } else {
                        calls++;
                    }
                    ruleCalls.put(name, calls);
                }
            }
        });

        // Using fire until halt. If firaAllRules is called it works. But for other reasons, I need to run fireUntilHalt

        new Thread(ksession::fireUntilHalt).start();

    }

    @After
    public void after() {
        ksession.halt();
        ksession.dispose();
    }

    @Test
    public void testMultipleFires() {

        playAnEvent("discardCard");
        checkExecutions(RULES, Arrays.asList(1, 1, 1, 0));
        System.out.println("first played");

        playAnEvent("discardCard");
        checkExecutions(RULES, Arrays.asList(2, 2, 2, 0));
        System.out.println("second played");

        playAnEvent("discardCardIrr");
        checkExecutions(RULES, Arrays.asList(3, 3, 2, 1));
        System.out.println("third played");

        playAnEvent("discardCardIrr");
        checkExecutions(RULES, Arrays.asList(4, 4, 2, 2));
        System.out.println("fourth played");

    }

    /**
     * Checks that the rule names passed in are called the number of times passed in.
     */
    private void checkExecutions(final List<String> rules, final List<Integer> expected) {
        assertThat(expected.size()).as("Wrong config passed. Rules doesn't match times").isEqualTo(rules.size());
        synchronized (ruleCalls) {
            for (int i = 0; i < rules.size(); i++) {
                final String ruleName = rules.get(i);
                Integer actualTimes = ruleCalls.get(ruleName);
                if (actualTimes == null) {
                    actualTimes = 0;
                }
                assertThat(actualTimes).as("Ruled " + ruleName + " is not called as often as expected.").isEqualTo(expected.get(i));
            }
        }
    }


    private void playAnEvent(final String key) {
        final AnEvent discardCardIrr = new AnEvent(key);
        ksession.getEntryPoint("game stream").insert(discardCardIrr);
        giveTheRuleThreadSomeTimeToFinishComputation(300); // give some time the other thread where rules run finished with the execution
    }

    private void giveTheRuleThreadSomeTimeToFinishComputation(final long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            fail("Unexpected error");
        }
    }

    public static class AnEvent implements Serializable {

        private String eventName;

        public AnEvent() {
        }

        public AnEvent(String eventName) {
            this.eventName = eventName;
        }

        public String getEventName() {
            return eventName;
        }

        public void setEventName(String eventName) {
            this.eventName = eventName;
        }
    }
}
