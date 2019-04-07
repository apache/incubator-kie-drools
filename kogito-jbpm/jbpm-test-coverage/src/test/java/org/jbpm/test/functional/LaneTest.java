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
import org.jbpm.test.listener.TrackingProcessEventListener;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;

import static org.jbpm.test.tools.TrackingListenerAssert.*;

/**
 * Simple testing of lanes - there is nothing special to test.
 */
public class LaneTest extends JbpmTestCase {

    public static final String PROCESS = "org/jbpm/test/functional/Lane.bpmn";
    public static final String PROCESS_ID = "org.jbpm.test.functional.Lane";

    public LaneTest() {
        super(false);
    }

    @Test(timeout = 30000)
    public void testLane() {
        KieSession ksession = createKSession(PROCESS);

        TrackingProcessEventListener tpel = new TrackingProcessEventListener();
        ksession.addEventListener(tpel);
        ksession.execute((Command<?>) getCommands().newStartProcess(PROCESS_ID));

        assertProcessStarted(tpel, PROCESS_ID);
        assertTriggeredAndLeft(tpel, "start");
        assertTriggered(tpel, "fork");
        assertLeft(tpel, "fork", 2);
        assertTriggeredAndLeft(tpel, "scriptTask1");
        assertTriggeredAndLeft(tpel, "scriptTask2");
        assertTriggered(tpel, "end1");
        assertTriggered(tpel, "end2");
        assertProcessCompleted(tpel, PROCESS_ID);
    }

}
