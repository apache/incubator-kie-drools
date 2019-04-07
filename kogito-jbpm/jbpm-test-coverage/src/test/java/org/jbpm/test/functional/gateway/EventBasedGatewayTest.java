/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.test.functional.gateway;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.time.SessionPseudoClock;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.TrackingProcessEventListener;
import org.jbpm.test.tools.TrackingListenerAssert;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.command.CommandFactory;

import static org.junit.Assert.*;

/**
 * Event-based gateway execution test. branches: condition event, signal event, message event, timer event
 * (default after 1 sec)
 */
public class EventBasedGatewayTest extends JbpmTestCase {

    private static final String EVENT_BASED_GATEWAY = "org/jbpm/test/functional/gateway/EventBasedGateway.bpmn";
    private static final String EVENT_BASED_GATEWAY_ID = "org.jbpm.test.functional.gateway.EventBasedGateway";

    private KieSession ksession;

    public EventBasedGatewayTest() {
        super(false);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        ksession = createKSession(EVENT_BASED_GATEWAY);
    }

    /**
     * Conditional event branch
     */
    @Test(timeout = 30000)
    public void testConditional() {
        TrackingProcessEventListener tpel = new TrackingProcessEventListener();
        ksession.addEventListener(tpel);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newInsert(5));
        commands.add(CommandFactory.newStartProcess(EVENT_BASED_GATEWAY_ID));
        ksession.execute(CommandFactory.newBatchExecution(commands));

        TrackingListenerAssert.assertProcessStarted(tpel, EVENT_BASED_GATEWAY_ID);
        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "start");

        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "fork");
        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "cond");

        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "join");
        TrackingListenerAssert.assertTriggered(tpel, "end");
        TrackingListenerAssert.assertProcessCompleted(tpel, EVENT_BASED_GATEWAY_ID);
    }

    /**
     * Signal event branch
     */
    @Test(timeout = 30000)
    public void testSignal() {
        TrackingProcessEventListener tpel = new TrackingProcessEventListener();
        ksession.addEventListener(tpel);

        List<Command<?>> commands = new ArrayList<Command<?>>();
        commands.add(CommandFactory.newStartProcess(EVENT_BASED_GATEWAY_ID));
        commands.add(CommandFactory.newSignalEvent("sigkill", null));
        ksession.execute(CommandFactory.newBatchExecution(commands));

        TrackingListenerAssert.assertProcessStarted(tpel, EVENT_BASED_GATEWAY_ID);
        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "start");

        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "fork");
        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "sig");

        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "join");
        TrackingListenerAssert.assertTriggered(tpel, "end");
        TrackingListenerAssert.assertProcessCompleted(tpel, EVENT_BASED_GATEWAY_ID);
    }

    @Test(timeout = 30000)
    public void testMessage() {
        TrackingProcessEventListener tpel = new TrackingProcessEventListener();
        ksession.addEventListener(tpel);

        ProcessInstance pi = (ProcessInstance) ksession.execute((Command<?>)
                CommandFactory.newStartProcess(EVENT_BASED_GATEWAY_ID));
        TrackingListenerAssert.assertProcessStarted(tpel, EVENT_BASED_GATEWAY_ID);
        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "start");

        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "fork");
        TrackingListenerAssert.assertTriggered(tpel, "msg");

        ksession.execute((Command<?>) CommandFactory.newSignalEvent(pi.getId(), "Message-message1", null));

        TrackingListenerAssert.assertLeft(tpel, "msg");
        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "join");
        TrackingListenerAssert.assertTriggered(tpel, "end");
        TrackingListenerAssert.assertProcessCompleted(tpel, EVENT_BASED_GATEWAY_ID);
    }

    /**
     * No branch is selected, timer is triggered after 1 sec
     * @throws Exception 
     */
    @Test(timeout = 30000)
    public void testTimer() throws Exception {
        Assume.assumeFalse(ksession.getSessionClock() instanceof SessionPseudoClock);
        TrackingProcessEventListener tpel = new TrackingProcessEventListener();
        ksession.addEventListener(tpel);

        ksession.execute((Command<?>) CommandFactory.newStartProcess(EVENT_BASED_GATEWAY_ID));
        TrackingListenerAssert.assertProcessStarted(tpel, EVENT_BASED_GATEWAY_ID);
        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "start");
        TrackingListenerAssert.assertTriggered(tpel, "fork");
        TrackingListenerAssert.assertLeft(tpel, "fork", 4);
        TrackingListenerAssert.assertTriggered(tpel, "cond");
        TrackingListenerAssert.assertTriggered(tpel, "msg");
        TrackingListenerAssert.assertTriggered(tpel, "sig");
        String timerNodeName = "timer";
        TrackingListenerAssert.assertTriggered(tpel, timerNodeName);
        assertTrue( "Node '" + timerNodeName + "' was not triggered on time!", 
                tpel.waitForNodeToBeLeft(timerNodeName, 2000));
        TrackingListenerAssert.assertLeft(tpel, timerNodeName);

        assertTrue( "Process was not completed on time!", tpel.waitForProcessToComplete(2000));
        
        TrackingListenerAssert.assertTriggeredAndLeft(tpel, "join");
        TrackingListenerAssert.assertTriggered(tpel, "end");
        
        TrackingListenerAssert.assertProcessCompleted(tpel, EVENT_BASED_GATEWAY_ID);
    }

}
