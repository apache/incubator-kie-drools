/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.integrationtests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.io.ResourceType;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;

@Ignore
public class WorkingMemoryActionsSerializationTest {
    private static final List<String> RULES = Arrays.asList("enableRecording", "saveRecord", "processEvent", "ignoreEvent"); //rules expected to be executed
    private StatefulKnowledgeSession ksession;
    private KnowledgeBase kbase;

    private final Map<String, Integer> ruleCalls = new HashMap<String, Integer>();

    //private final String accessor = "getEventName()";
    private final String accessor = "eventName";

    private final String drl =
               "package apackage\n" +
               " \n" +
               "import org.drools.core.spi.KnowledgeHelper\n" +
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

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add( ResourceFactory.newByteArrayResource(drl.getBytes()), ResourceType.DRL);
        if ( kbuilder.hasErrors() ) {
            fail( kbuilder.getErrors().toString() );
        }
        KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase( );
        kbase.addKnowledgePackages( kbuilder.getKnowledgePackages() );

        ksession = kbase.newStatefulKnowledgeSession();

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                ksession.fireUntilHalt();
            }
        }).start();

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
        Assert.assertEquals("Wrong config passed. Rules doesn't match times", rules.size(), expected.size());
        synchronized (ruleCalls) {
            for (int i = 0; i < rules.size(); i++) {
                final String ruleName = rules.get(i);
                Integer actualTimes = ruleCalls.get(ruleName);
                if (actualTimes == null) {
                    actualTimes = 0;
                }
                Assert.assertEquals(
                        "Ruled " + ruleName + " is not called as often as expected.", expected.get(i), actualTimes);
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
            fail();
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
