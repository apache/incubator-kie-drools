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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jbpm.test.functional;

import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.IterableProcessEventListener;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;

import static org.jbpm.test.tools.IterableListenerAssert.*;

/**
 * Testing conditional sequence flow without gateway.
 *
 * https://bugzilla.redhat.com/show_bug.cgi?id=807640
 */
public class ConditionalFlowTest extends JbpmTestCase {

    private static final String PROCESS = "org/jbpm/test/functional/ConditionalFlow.bpmn";
    private static final String PROCESS_ID = "org.jbpm.test.functional.ConditionalFlow";

    static {
        System.setProperty("jbpm.enable.multi.con", "true");
    }

    public ConditionalFlowTest() {
        super(false);
    }

    @Test(timeout = 30000)
    public void testConditionalFlow() {
        KieSession ksession = createKSession(PROCESS);

        IterableProcessEventListener listener = new IterableProcessEventListener();
        ksession.addEventListener(listener);
        ksession.execute((Command<?>) getCommands().newStartProcess(PROCESS_ID));

        assertProcessStarted(listener, PROCESS_ID);
        assertNextNode(listener, "start");
        assertTriggered(listener, "script");
        assertChangedVariable(listener, "x", null, 5);
        assertLeft(listener, "script");
        assertNextNode(listener, "end1");
        assertProcessCompleted(listener, PROCESS_ID);
    }

}
