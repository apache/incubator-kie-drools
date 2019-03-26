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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.DebugProcessEventListener;
import org.jbpm.test.listener.IterableProcessEventListener;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import qa.tools.ikeeper.annotation.BZ;

import static org.jbpm.test.tools.IterableListenerAssert.*;

public class MultipleInstancesSubProcessTest extends JbpmTestCase {

    private static final String MULTIPLE_INSTANCES =
            "org/jbpm/test/functional/subprocess/MultipleInstancesSubProcess.bpmn";
    private static final String MULTIPLE_INSTANCES_ID =
            "org.jbpm.test.functional.subprocess.MultipleInstancesSubProcess";

    public MultipleInstancesSubProcessTest() {
        super(false);
    }

    @BZ("802721")
    @Test(timeout = 30000)
    public void testMultipleInstances() {
        KieSession kieSession = createKSession(MULTIPLE_INSTANCES);
        IterableProcessEventListener eventListener = new IterableProcessEventListener();

        kieSession.addEventListener(eventListener);
        kieSession.addEventListener(new DebugProcessEventListener());
        List<String> items = new ArrayList<String>();
        items.add("breakfast");
        items.add("lunch");
        items.add("dinner");
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("list", items);

        kieSession.execute((Command<?>) getCommands().newStartProcess(MULTIPLE_INSTANCES_ID, params));
        assertChangedVariable(eventListener, "list", null, items);
        assertProcessStarted(eventListener, MULTIPLE_INSTANCES_ID);
        assertNextNode(eventListener, "start");
        assertNextNode(eventListener, "script");

        assertTriggered(eventListener, "multipleInstances");
        // collection is passed to multiple-instances node
        for (String str : items) {
            assertChangedMultipleInstancesVariable(eventListener, "listItem", null, str);
        }
        // multiple-instances node is processed for every collection item
        for (String str : items) {
            assertNextNode(eventListener, "innerStart");
            assertTriggered(eventListener, "innerScript");
            assertChangedMultipleInstancesVariable(eventListener, "listItem", str, str + "-eaten");
            assertLeft(eventListener, "innerScript");
            assertNextNode(eventListener, "innerEnd");
        }
        assertLeft(eventListener, "multipleInstances");
        assertNextNode(eventListener, "end");
        assertProcessCompleted(eventListener, MULTIPLE_INSTANCES_ID);
    }

}

