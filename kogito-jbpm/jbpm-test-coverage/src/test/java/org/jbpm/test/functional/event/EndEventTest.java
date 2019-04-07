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

package org.jbpm.test.functional.event;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.drools.core.command.runtime.process.RegisterWorkItemHandlerCommand;
import org.drools.core.process.instance.WorkItemHandler;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.IterableProcessEventListener;
import org.jbpm.test.listener.TrackingProcessEventListener;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;
import org.kie.internal.command.CommandFactory;
import qa.tools.ikeeper.annotation.BZ;

import static org.jbpm.test.tools.IterableListenerAssert.*;
import static org.junit.Assert.*;

public class EndEventTest extends JbpmTestCase {

    private static final String COMPENSATE = "org/jbpm/test/functional/event/EndEvent-compensate.bpmn2";
    private static final String COMPENSATE_ID = "org.jbpm.test.functional.event.EndEvent-compensate";

    private static final String ERROR = "org/jbpm/test/functional/event/EndEvent-error.bpmn2";
    private static final String ERROR_ID = "org.jbpm.test.functional.event.EndEvent-error";

    private static final String ESCALATION = "org/jbpm/test/functional/event/EndEvent-escalation.bpmn2";
    private static final String ESCALATION_ID = "org.jbpm.test.functional.event.EndEvent-escalation";

    private static final String MESSAGE = "org/jbpm/test/functional/event/EndEvent-message.bpmn2";
    private static final String MESSAGE_ID = "org.jbpm.test.functional.event.EndEvent-message";

    private static final String NONE = "org/jbpm/test/functional/event/EndEvent-none.bpmn2";
    private static final String NONE_ID = "org.jbpm.test.functional.event.EndEvent-none";

    private static final String TERMINATING = "org/jbpm/test/functional/event/EndEvent-terminating.bpmn2";
    private static final String TERMINATING_ID = "org.jbpm.test.functional.event.EndEvent-terminating";

    public EndEventTest() {
        super(false);
    }

    @BZ("1021631")
    @Test(timeout = 30000)
    public void testCompensateEndEvent() {
        KieSession ksession = createKSession(COMPENSATE);

        IterableProcessEventListener events = new IterableProcessEventListener();
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(events);
        ksession.addEventListener(process);

        Command<ProcessInstance> cmd = getCommands().newStartProcess(COMPENSATE_ID);
        ProcessInstance pi = ksession.execute(cmd);
        Assertions.assertThat(process.wasProcessStarted(COMPENSATE_ID)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(COMPENSATE_ID)).isTrue();

        assertProcessStarted(events, COMPENSATE_ID);
        assertNextNode(events, "start");
        assertTriggered(events, "subprocess");
        assertNextNode(events, "sub-start");
        assertTriggered(events, "script");
        assertChangedVariable(events, "x", null, 0);
        assertLeft(events, "script");
        assertNextNode(events, "sub-end");
        assertLeft(events, "subprocess");

        assertTriggered(events, "end");
        assertLeft(events, "compensate-catch");
        assertTriggered(events, "compensate");
        assertChangedVariable(events, "x", 0, null);
        assertLeft(events, "compensate");

        Assertions.assertThat(pi.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test(timeout = 30000)
    public void testErrorEndEvent() {
        KieSession ksession = createKSession(ERROR);

        IterableProcessEventListener events = new IterableProcessEventListener();
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(events);
        ksession.addEventListener(process);

        Command<?> cmd = getCommands().newStartProcess(ERROR_ID);
        ksession.execute(cmd);

        Assertions.assertThat(process.wasProcessStarted(ERROR_ID)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(ERROR_ID)).isFalse();
        Assertions.assertThat(process.wasProcessAborted(ERROR_ID)).isTrue();

        assertProcessStarted(events, ERROR_ID);
        assertNextNode(events, "start");
        assertNextNode(events, "script");
        assertTriggered(events, "end");
        assertProcessCompleted(events, ERROR_ID);

        Assertions.assertThat(events.hasNext()).isFalse();
    }

    @BZ("1015221")
    @Test(timeout = 30000)
    public void testEscalationEndEvent() {
        KieSession ksession = createKSession(ESCALATION);

        IterableProcessEventListener events = new IterableProcessEventListener();
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(events);
        ksession.addEventListener(process);

        Command<?> cmd = getCommands().newStartProcess(ESCALATION_ID);
        ksession.execute(cmd);

        Assertions.assertThat(process.wasProcessStarted(ESCALATION_ID)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(ESCALATION_ID)).isFalse();
        Assertions.assertThat(process.wasProcessAborted(ESCALATION_ID)).isTrue();

        assertProcessStarted(events, ESCALATION_ID);
        assertNextNode(events, "start");
        assertNextNode(events, "script");
        assertTriggered(events, "end");
        assertProcessCompleted(events, ESCALATION_ID);
        assertFalse(events.hasNext());
    }

    @Test(timeout = 30000)
    public void testMessageEndEvent() {
        KieSession ksession = createKSession(MESSAGE);

        IterableProcessEventListener events = new IterableProcessEventListener();
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(events);
        ksession.addEventListener(process);

        RecordingHandler handler = new RecordingHandler();

        List<Command<?>> cmds = new ArrayList<Command<?>>();
        cmds.add(new RegisterWorkItemHandlerCommand("Send Task", handler));
        cmds.add(getCommands().newStartProcess(MESSAGE_ID));
        ksession.execute(getCommands().newBatchExecution(cmds, null));


        Assertions.assertThat(process.wasProcessStarted(MESSAGE_ID)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(MESSAGE_ID)).isTrue();

        assertProcessStarted(events, MESSAGE_ID);
        assertNextNode(events, "start");
        assertNextNode(events, "script");
        assertNextNode(events, "end");
        assertProcessCompleted(events, MESSAGE_ID);

        Assertions.assertThat(events.hasNext()).isFalse();
        Assertions.assertThat(handler.item).isNotNull();
    }

    @Test(timeout = 30000)
    public void testNoneEndEvent() {
        KieSession ksession = createKSession(NONE);

        IterableProcessEventListener events = new IterableProcessEventListener();
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(events);
        ksession.addEventListener(process);

        Command<?> cmd = CommandFactory.newStartProcess(NONE_ID);
        ksession.execute(cmd);

        Assertions.assertThat(process.wasProcessStarted(NONE_ID)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(NONE_ID)).isTrue();

        assertProcessStarted(events, NONE_ID);
        assertNextNode(events, "start");
        assertNextNode(events, "script");
        assertNextNode(events, "end");
        assertProcessCompleted(events, NONE_ID);

        Assertions.assertThat(events.hasNext()).isFalse();
    }

    @Test(timeout = 30000)
    public void testTerminatingEndEvent() {
        KieSession ksession = createKSession(TERMINATING);

        IterableProcessEventListener events = new IterableProcessEventListener();
        TrackingProcessEventListener process = new TrackingProcessEventListener();
        ksession.addEventListener(events);
        ksession.addEventListener(process);

        Command<?> cmd = getCommands().newStartProcess(TERMINATING_ID);
        ksession.execute(cmd);
        Assertions.assertThat(process.wasProcessStarted(TERMINATING_ID)).isTrue();
        Assertions.assertThat(process.wasProcessCompleted(TERMINATING_ID)).isTrue();

        assertProcessStarted(events, TERMINATING_ID);
        assertNextNode(events, "start");
        assertNextNode(events, "script");
        assertNextNode(events, "end");
        assertProcessCompleted(events, TERMINATING_ID);

        Assertions.assertThat(events.hasNext()).isFalse();
    }

    private static class RecordingHandler implements WorkItemHandler {

        private WorkItem item = null;

        @Override
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            if (item != null) {
                throw new IllegalStateException("Work item is already set!");
            }
            this.item = workItem;
        }

        @Override
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
            // nothing
        }

    }

}
