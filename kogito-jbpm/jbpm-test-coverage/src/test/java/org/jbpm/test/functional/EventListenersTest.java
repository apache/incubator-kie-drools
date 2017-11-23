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

package org.jbpm.test.functional;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.TrackingProcessEventListener;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;

import static org.junit.Assert.*;

/**
 * Test to ensure event listeners for processes work properly BRMS 5.3 PRD: BRMS-BPM-10
 */
public class EventListenersTest extends JbpmTestCase {

    private static final String PROCESS = "org/jbpm/test/functional/EventListeners.bpmn";
    private static final String PROCESS_ID = "org.jbpm.test.functional.EventListeners";

    private KieSession ksession;

    public EventListenersTest() {
        super(false);
    }

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        ksession = createKSession(PROCESS);
    }

    @Test(timeout = 60000L)
    public void testClearExecution() {
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        ksession.addEventListener(listener);

        List<Command<?>> commands = new ArrayList<Command<?>>();

        commands.add(getCommands().newStartProcess(PROCESS_ID));
        commands.add(getCommands().newSignalEvent("other-branch", "hello world!"));

        ksession.execute(getCommands().newBatchExecution(commands, null));

        assertTrue(listener.wasNodeTriggered("introduction"));
        assertFalse(listener.wasNodeTriggered("script-warning"));

        assertTrue(listener.wasNodeLeft("introduction"));
        assertFalse(listener.wasNodeLeft("script-warning"));

        assertTrue(listener.wasVariableChanged("signalData"));
        assertFalse(listener.wasVariableChanged("stringVariable"));

        assertTrue(listener.wasProcessStarted(PROCESS_ID));
        assertTrue(listener.wasProcessCompleted(PROCESS_ID));
    }

    @Test(timeout = 60000L)
    public void testUnfinishedProcess() {
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        ksession.addEventListener(listener);

        List<Command<?>> commands = new ArrayList<Command<?>>();

        commands.add(getCommands().newStartProcess(PROCESS_ID));

        ksession.execute(getCommands().newBatchExecution(commands, null));

        assertTrue(listener.wasNodeTriggered("introduction"));
        assertTrue(listener.wasNodeTriggered("split"));

        assertTrue(listener.wasNodeLeft("introduction"));
        assertFalse(listener.wasNodeLeft("xor-gateway"));

        assertTrue(listener.wasProcessStarted(PROCESS_ID));
        assertFalse(listener.wasProcessCompleted(PROCESS_ID));
    }

    @Test(timeout = 60000L)
    public void testBadSignal() {
        TrackingProcessEventListener listener = new TrackingProcessEventListener();
        ksession.addEventListener(listener);

        List<Command<?>> commands = new ArrayList<Command<?>>();

        commands.add(getCommands().newStartProcess(PROCESS_ID));
        commands.add(getCommands().newSignalEvent("bad-signal", "bad signal!"));

        ksession.execute(getCommands().newBatchExecution(commands, null));

        assertTrue(listener.wasNodeTriggered("introduction"));
        assertFalse(listener.wasNodeTriggered("info"));

        assertTrue(listener.wasNodeLeft("introduction"));
        assertFalse(listener.wasNodeLeft("info"));

        assertFalse(listener.wasVariableChanged("signalData"));
        assertFalse(listener.wasVariableChanged("stringVariable"));

        assertTrue(listener.wasNodeLeft("script-warning"));

        assertTrue(listener.wasProcessStarted(PROCESS_ID));
        assertTrue(listener.wasProcessCompleted(PROCESS_ID));

    }

}
