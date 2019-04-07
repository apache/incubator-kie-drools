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

package org.jbpm.test.functional.subprocess;

import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.IterableProcessEventListener;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;

import static org.jbpm.test.tools.IterableListenerAssert.*;

public class EmbeddedSubProcessTest extends JbpmTestCase {

    private static final String EMBEDDED = "org/jbpm/test/functional/subprocess/EmbeddedSubProcess.bpmn";
    private static final String EMBEDDED_ID = "org.jbpm.test.functional.subprocess.EmbeddedSubProcess";

    public EmbeddedSubProcessTest() {
        super(false);
    }

    @Test(timeout = 30000)
    public void testProcessWithEmbeddedSubprocess() {
        KieSession kieSession = createKSession(EMBEDDED);
        IterableProcessEventListener eventListener = new IterableProcessEventListener();

        kieSession.addEventListener(eventListener);
        kieSession.execute((Command<?>) getCommands().newStartProcess(EMBEDDED_ID));

        assertProcessStarted(eventListener, EMBEDDED_ID);
        assertNextNode(eventListener, "start");
        assertNextNode(eventListener, "ScriptOuter");
        assertTriggered(eventListener, "embedded");

        logger.debug("inside embedded subprocess");

        assertNextNode(eventListener, "sub-start");
        assertNextNode(eventListener, "ScriptInner");
        assertNextNode(eventListener, "sub-end");
        assertLeft(eventListener, "embedded");

        logger.debug("outside embedded subprocess");

        assertNextNode(eventListener, "end");
        assertProcessCompleted(eventListener, EMBEDDED_ID);
    }

}
