/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.testcoverage.functional.oopath;

import org.assertj.core.api.Assertions;
import org.drools.core.time.SessionPseudoClock;
import org.drools.testcoverage.common.model.Message;
import org.drools.testcoverage.common.model.MessageEvent;
import org.drools.testcoverage.common.util.KieBaseTestConfiguration;
import org.drools.testcoverage.common.util.KieBaseUtil;
import org.drools.testcoverage.common.util.KieSessionUtil;
import org.junit.After;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.conf.ClockTypeOption;
import org.kie.api.runtime.rule.EntryPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Tests usage of OOPath expressions with CEP (events, event windows, event streams).
 */
public class OOPathCepTest {

    private static final String MODULE_GROUP_ID = "oopath-cep-test";
    private static final String ENTRY_POINT_NAME = "test-entry-point";

    private KieSession kieSession;
    private List<MessageEvent> events;
    private List<Message> messages;

    @After
    public void disposeKieSession() {
        if (this.kieSession != null) {
            this.kieSession.dispose();
            this.kieSession = null;

            this.events = null;
            this.messages = null;
        }
    }

    @Test
    public void testEventWithOOPath() {
        final String drl =
                "import org.drools.testcoverage.common.model.Message;\n" +
                "import org.drools.testcoverage.common.model.MessageEvent;\n" +
                "global java.util.List events\n" +
                "global java.util.List messages\n" +
                "\n" +
                "declare org.drools.testcoverage.common.model.MessageEvent\n" +
                "  @role( event )\n" +
                "end\n" +
                "rule R when\n" +
                "  MessageEvent( $message: /msg{ message == 'Hello' } )\n" +
                "then\n" +
                "  messages.add( $message );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(MODULE_GROUP_ID, KieBaseTestConfiguration.STREAM_EQUALITY, drl);
        this.initKieSession(kieBase);
        this.populateAndVerifyEventCase(this.kieSession);
    }

    @Test
    public void testEntryPointWithOOPath() {
        final String drl =
                "import org.drools.testcoverage.common.model.Message;\n" +
                "import org.drools.testcoverage.common.model.MessageEvent;\n" +
                "global java.util.List events\n" +
                "global java.util.List messages\n" +
                "\n" +
                "declare org.drools.testcoverage.common.model.MessageEvent\n" +
                "  @role( event )\n" +
                "end\n" +
                "rule R when\n" +
                String.format("  MessageEvent( $message: /msg{ message == 'Hello' } ) from entry-point \"%s\"\n", ENTRY_POINT_NAME) +
                "then\n" +
                "  messages.add( $message );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(MODULE_GROUP_ID, KieBaseTestConfiguration.STREAM_EQUALITY, drl);
        this.initKieSession(kieBase);
        this.populateAndVerifyEventCase(this.kieSession.getEntryPoint(ENTRY_POINT_NAME));
    }

    private void populateAndVerifyEventCase(final EntryPoint entryPoint) {
        final Message helloMessage = new Message("Hello");
        final MessageEvent helloEvent = new MessageEvent(MessageEvent.Type.sent, helloMessage);
        entryPoint.insert(helloEvent);

        final MessageEvent anotherEvent = new MessageEvent(MessageEvent.Type.sent, new Message("Not a hello"));
        entryPoint.insert(anotherEvent);

        this.kieSession.fireAllRules();
        Assertions.assertThat(this.messages).containsExactlyInAnyOrder(helloMessage);
    }

    @Test
    public void testTemporalOperatorWithOOPath() {
        final String drl =
                "import org.drools.testcoverage.common.model.Message;\n" +
                "import org.drools.testcoverage.common.model.MessageEvent;\n" +
                "global java.util.List events\n" +
                "global java.util.List messages\n" +
                "\n" +
                "declare org.drools.testcoverage.common.model.MessageEvent\n" +
                "  @role( event )\n" +
                "end\n" +
                "rule R when\n" +
                "  ev1: MessageEvent( /msg{ message == 'Ping' } )\n" +
                "  ev2: MessageEvent( $message: /msg{ message == 'Pong' }, this after ev1 )\n" +
                "then\n" +
                "  messages.add( $message );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(MODULE_GROUP_ID, KieBaseTestConfiguration.STREAM_EQUALITY, drl);
        this.initKieSession(kieBase);

        final MessageEvent pongEvent = new MessageEvent(MessageEvent.Type.sent, new Message("Pong"));
        this.kieSession.insert(pongEvent);

        final MessageEvent pingEvent = new MessageEvent(MessageEvent.Type.sent, new Message("Ping"));
        this.kieSession.insert(pingEvent);

        this.kieSession.fireAllRules();
        Assertions.assertThat(this.messages).as("Pong event before Ping event should NOT make the rule fire").isEmpty();

        final Message pongMessage = new Message("Pong");
        final MessageEvent secondPongEvent = new MessageEvent(MessageEvent.Type.sent, pongMessage);
        this.kieSession.insert(secondPongEvent);

        this.kieSession.fireAllRules();
        Assertions.assertThat(this.messages).as("Pong event after Ping event should make the rule fire").containsExactlyInAnyOrder(pongMessage);
    }

    @Test
    public void testLengthWindowWithOOPath() {
        final String drl =
                "import org.drools.testcoverage.common.model.Message;\n" +
                "import org.drools.testcoverage.common.model.MessageEvent;\n" +
                "global java.util.List events\n" +
                "global java.util.List messages\n" +
                "\n" +
                "declare org.drools.testcoverage.common.model.MessageEvent\n" +
                "  @role( event )\n" +
                "end\n" +
                "rule R when\n" +
                "  $messageEvent: MessageEvent( /msg{ message == 'Ping' } ) over window:length( 2 )\n" +
                "then\n" +
                "  events.add( $messageEvent );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(MODULE_GROUP_ID, KieBaseTestConfiguration.STREAM_EQUALITY, drl);
        this.populateAndVerifyLengthWindowCase(kieBase);
    }

    @Test
    public void testDeclaredLengthWindowWithOOPathInRule() {
        final String drl =
                "import org.drools.testcoverage.common.model.Message;\n" +
                "import org.drools.testcoverage.common.model.MessageEvent;\n" +
                "global java.util.List events\n" +
                "global java.util.List messages\n" +
                "\n" +
                "declare org.drools.testcoverage.common.model.MessageEvent\n" +
                "  @role( event )\n" +
                "end\n" +
                "\n" +
                "declare window Pings\n" +
                "  MessageEvent() over window:length( 2 )\n" +
                "end\n" +
                "rule R when\n" +
                "  $messageEvent: MessageEvent( /msg{ message == 'Ping' } ) from window Pings\n" +
                "then\n" +
                "  events.add( $messageEvent );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(MODULE_GROUP_ID, KieBaseTestConfiguration.STREAM_EQUALITY, drl);
        this.populateAndVerifyLengthWindowCase(kieBase);
    }

    @Test
    public void testDeclaredLengthWindowWithOOPathInWindow() {
        final String drl =
                "import org.drools.testcoverage.common.model.Message;\n" +
                "import org.drools.testcoverage.common.model.MessageEvent;\n" +
                "global java.util.List events\n" +
                "global java.util.List messages\n" +
                "\n" +
                "declare org.drools.testcoverage.common.model.MessageEvent\n" +
                "  @role( event )\n" +
                "end\n" +
                "\n" +
                "declare window Pings\n" +
                "  MessageEvent( /msg{ message == 'Ping' } ) over window:length( 2 )\n" +
                "end\n" +
                "rule R when\n" +
                "  $messageEvent: MessageEvent() from window Pings\n" +
                "then\n" +
                "  events.add( $messageEvent );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(MODULE_GROUP_ID, KieBaseTestConfiguration.STREAM_EQUALITY, drl);
        this.populateAndVerifyLengthWindowCase(kieBase);
    }

    @Test
    public void testDeclaredLengthWindowWithOOPathInRuleAndWindow() {
        final String drl =
                "import org.drools.testcoverage.common.model.Message;\n" +
                "import org.drools.testcoverage.common.model.MessageEvent;\n" +
                "global java.util.List events\n" +
                "global java.util.List messages\n" +
                "\n" +
                "declare org.drools.testcoverage.common.model.MessageEvent\n" +
                "  @role( event )\n" +
                "end\n" +
                "\n" +
                "declare window Pings\n" +
                "  MessageEvent( /msg{ message != 'Pong' } ) over window:length( 2 )\n" +
                "end\n" +
                "rule R when\n" +
                "  $messageEvent: MessageEvent( /msg{ message == 'Ping' } ) from window Pings\n" +
                "then\n" +
                "  events.add( $messageEvent );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(MODULE_GROUP_ID, KieBaseTestConfiguration.STREAM_EQUALITY, drl);
        this.populateAndVerifyLengthWindowCase(kieBase);
    }

    private void populateAndVerifyLengthWindowCase(final KieBase kieBase) {
        this.initKieSession(kieBase);

        final MessageEvent pingEvent = new MessageEvent(MessageEvent.Type.sent, new Message("Ping"));
        this.kieSession.insert(pingEvent);

        final MessageEvent ping2Event = new MessageEvent(MessageEvent.Type.received, new Message("Ping"));
        this.kieSession.insert(ping2Event);

        final MessageEvent ping3Event = new MessageEvent(MessageEvent.Type.received, new Message("Ping"));
        this.kieSession.insert(ping3Event);

        this.kieSession.fireAllRules();
        Assertions.assertThat(this.events).as("The rule should have fired for 2 events").containsExactly(ping2Event, ping3Event);
        this.events.clear();

        final MessageEvent pongEvent = new MessageEvent(MessageEvent.Type.sent, new Message("Pong"));
        this.kieSession.insert(pongEvent);

        final MessageEvent ping4Event = new MessageEvent(MessageEvent.Type.received, new Message("Ping"));
        this.kieSession.insert(ping4Event);

        this.kieSession.fireAllRules();
        Assertions.assertThat(this.events).as("The rule should have fired for ping event only").containsExactly(ping4Event);
    }

    @Test
    public void testTimeWindowWithOOPath() {
        final String drl =
                "import org.drools.testcoverage.common.model.Message;\n" +
                "import org.drools.testcoverage.common.model.MessageEvent;\n" +
                "global java.util.List events\n" +
                "global java.util.List messages\n" +
                "\n" +
                "declare org.drools.testcoverage.common.model.MessageEvent\n" +
                "  @role( event )\n" +
                "end\n" +
                "rule R when\n" +
                "  $messageEvent: MessageEvent( /msg{ message == 'Ping' } ) over window:time( 3s )\n" +
                "then\n" +
                "  events.add( $messageEvent );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(MODULE_GROUP_ID, KieBaseTestConfiguration.STREAM_EQUALITY, drl);
        this.populateAndVerifyTimeWindowCase(kieBase);
    }

    @Test
    public void testDeclaredTimeWindowWithOOPathInRule() {
        final String drl =
                "import org.drools.testcoverage.common.model.Message;\n" +
                "import org.drools.testcoverage.common.model.MessageEvent;\n" +
                "global java.util.List events\n" +
                "global java.util.List messages\n" +
                "\n" +
                "declare org.drools.testcoverage.common.model.MessageEvent\n" +
                "  @role( event )\n" +
                "end\n" +
                "\n" +
                "declare window Pings\n" +
                "  MessageEvent() over window:time( 3s )\n" +
                "end\n" +
                "rule R when\n" +
                "  $messageEvent: MessageEvent( /msg{ message == 'Ping' } ) from window Pings\n" +
                "then\n" +
                "  events.add( $messageEvent );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(MODULE_GROUP_ID, KieBaseTestConfiguration.STREAM_EQUALITY, drl);
        this.populateAndVerifyTimeWindowCase(kieBase);
    }

    @Test
    public void testDeclaredTimeWindowWithOOPathInWindow() {
        final String drl =
                "import org.drools.testcoverage.common.model.Message;\n" +
                "import org.drools.testcoverage.common.model.MessageEvent;\n" +
                "global java.util.List events\n" +
                "global java.util.List messages\n" +
                "\n" +
                "declare org.drools.testcoverage.common.model.MessageEvent\n" +
                "  @role( event )\n" +
                "end\n" +
                "\n" +
                "declare window Pings\n" +
                "  MessageEvent( /msg{ message == 'Ping' } ) over window:time( 3s )\n" +
                "end\n" +
                "rule R when\n" +
                "  $messageEvent: MessageEvent() from window Pings\n" +
                "then\n" +
                "  events.add( $messageEvent );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(MODULE_GROUP_ID, KieBaseTestConfiguration.STREAM_EQUALITY, drl);
        this.populateAndVerifyTimeWindowCase(kieBase);
    }

    @Test
    public void testDeclaredTimeWindowWithOOPathInRuleAndWindow() {
        final String drl =
                "import org.drools.testcoverage.common.model.Message;\n" +
                "import org.drools.testcoverage.common.model.MessageEvent;\n" +
                "global java.util.List events\n" +
                "global java.util.List messages\n" +
                "\n" +
                "declare org.drools.testcoverage.common.model.MessageEvent\n" +
                "  @role( event )\n" +
                "end\n" +
                "\n" +
                "declare window Pings\n" +
                "  MessageEvent( /msg{ message != 'Pong' } ) over window:time( 3s )\n" +
                "end\n" +
                "rule R when\n" +
                "  $messageEvent: MessageEvent( /msg{ message == 'Ping' } ) from window Pings\n" +
                "then\n" +
                "  events.add( $messageEvent );\n" +
                "end\n";

        final KieBase kieBase = KieBaseUtil.getKieBaseAndBuildInstallModuleFromDrl(MODULE_GROUP_ID, KieBaseTestConfiguration.STREAM_EQUALITY, drl);
        this.populateAndVerifyTimeWindowCase(kieBase);
    }

    private void populateAndVerifyTimeWindowCase(final KieBase kieBase) {
        final KieSessionConfiguration sessionConfiguration = KieSessionUtil.getKieSessionConfigurationWithClock(ClockTypeOption.get("pseudo"), null);
        this.initKieSession(kieBase, sessionConfiguration);
        final SessionPseudoClock clock = this.kieSession.getSessionClock();

        final MessageEvent pingEvent = new MessageEvent(MessageEvent.Type.sent, new Message("Ping"));
        this.kieSession.insert(pingEvent);
        clock.advanceTime(1, TimeUnit.SECONDS);

        final MessageEvent ping2Event = new MessageEvent(MessageEvent.Type.received, new Message("Ping"));
        this.kieSession.insert(ping2Event);
        clock.advanceTime(1, TimeUnit.SECONDS);

        final MessageEvent ping3Event = new MessageEvent(MessageEvent.Type.received, new Message("Ping"));
        this.kieSession.insert(ping3Event);
        clock.advanceTime(1, TimeUnit.SECONDS);

        this.kieSession.fireAllRules();
        Assertions.assertThat(this.events).as("The rule should have fired for 2 events").containsExactly(ping2Event, ping3Event);
        this.events.clear();

        final MessageEvent pongEvent = new MessageEvent(MessageEvent.Type.sent, new Message("Pong"));
        this.kieSession.insert(pongEvent);
        clock.advanceTime(1, TimeUnit.SECONDS);

        final MessageEvent ping4Event = new MessageEvent(MessageEvent.Type.received, new Message("Ping"));
        this.kieSession.insert(ping4Event);
        clock.advanceTime(1, TimeUnit.SECONDS);

        this.kieSession.fireAllRules();
        Assertions.assertThat(this.events).as("The rule should have fired for ping event only").containsExactly(ping4Event);
    }

    private void initKieSession(final KieBase kieBase) {
        this.initKieSession(kieBase, null);
    }

    private void initKieSession(final KieBase kieBase, final KieSessionConfiguration kieSessionConfiguration) {
        this.kieSession = kieBase.newKieSession(kieSessionConfiguration, null);
        this.messages = new ArrayList<>();
        this.kieSession.setGlobal("messages", messages);

        this.events = new ArrayList<>();
        this.kieSession.setGlobal("events", events);
    }

}
