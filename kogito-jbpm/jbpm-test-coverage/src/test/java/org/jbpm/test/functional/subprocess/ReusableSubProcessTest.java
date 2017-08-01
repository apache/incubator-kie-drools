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

public class ReusableSubProcessTest extends JbpmTestCase {

    private static final String CALL_ACTIVITY_PARENT =
            "org/jbpm/test/functional/subprocess/ReusableSubProcess-parent.bpmn";
    private static final String CALL_ACTIVITY_PARENT_ID =
            "org.jbpm.test.functional.subprocess.ReusableSubProcess-parent";

    private static final String CALL_ACTIVITY_CHILD =
            "org/jbpm/test/functional/subprocess/ReusableSubProcess-child.bpmn";
    private static final String CALL_ACTIVITY_CHILD_ID =
            "org.jbpm.test.functional.subprocess.ReusableSubProcess-child";

    public ReusableSubProcessTest() {
        super(false);
    }

    @Test(timeout = 30000)
    public void testCallActivity() {
        KieSession ksession = createKSession(CALL_ACTIVITY_CHILD, CALL_ACTIVITY_PARENT);
        IterableProcessEventListener eventListener = new IterableProcessEventListener();

        ksession.addEventListener(eventListener);
        ksession.execute((Command<?>) getCommands().newStartProcess(CALL_ACTIVITY_PARENT_ID));
        assertProcessStarted(eventListener, CALL_ACTIVITY_PARENT_ID);

        assertNextNode(eventListener, "start");
        assertTriggered(eventListener, "script");
        assertChangedVariable(eventListener, "var", null, 1);
        assertLeft(eventListener, "script");

        assertTriggered(eventListener, "reusable");

        assertChangedVariable(eventListener, "inSubVar", null, 1);
        assertProcessStarted(eventListener, CALL_ACTIVITY_CHILD_ID);

        assertNextNode(eventListener, "rs-start");
        assertTriggered(eventListener, "rs-script");
        assertChangedVariable(eventListener, "outSubVar", null, "one");
        assertLeft(eventListener, "rs-script");
        assertNextNode(eventListener, "rs-end");
        assertProcessCompleted(eventListener, CALL_ACTIVITY_CHILD_ID);
        assertChangedVariable(eventListener, "var", 1, "one");
        assertLeft(eventListener, "reusable");
        assertNextNode(eventListener, "end");

        assertProcessCompleted(eventListener, CALL_ACTIVITY_PARENT_ID);
    }

}
