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

package org.jbpm.test.functional.task;

import static org.jbpm.test.tools.IterableListenerAssert.assertChangedVariable;
import static org.jbpm.test.tools.IterableListenerAssert.assertLeft;
import static org.jbpm.test.tools.IterableListenerAssert.assertMultipleVariablesChanged;
import static org.jbpm.test.tools.IterableListenerAssert.assertNextNode;
import static org.jbpm.test.tools.IterableListenerAssert.assertProcessCompleted;
import static org.jbpm.test.tools.IterableListenerAssert.assertProcessStarted;
import static org.jbpm.test.tools.IterableListenerAssert.assertTriggered;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.domain.Person;
import org.jbpm.test.listener.IterableProcessEventListener;
import org.junit.Test;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;

/**
 * Testing script task - both Java and MVEL language.
 */
public class ScriptTaskTest extends JbpmTestCase {

    private static final String SCRIPT_TASK =
            "org/jbpm/test/functional/task/ScriptTask.bpmn";
    private static final String SCRIPT_TASK_ID =
            "org.jbpm.test.functional.task.ScriptTask";

    public ScriptTaskTest() {
        super(false);
    }

    /**
     * Object and collection access
     */
    @Test(timeout = 30000)
    public void testScriptTask() {
        KieSession kieSession = createKSession(SCRIPT_TASK);

        IterableProcessEventListener ipel = new IterableProcessEventListener();
        kieSession.addEventListener(ipel);

        Map<String, Object> params = new HashMap<String, Object>();
        Person p = new Person("Vandrovec");
        params.put("person", p);
        List<Person> personList = new ArrayList<Person>();
        personList.add(new Person("Birsky"));
        personList.add(new Person("Korcasko"));
        params.put("personList", personList);

        kieSession.execute((Command<?>) getCommands().newStartProcess(SCRIPT_TASK_ID, params));

        assertMultipleVariablesChanged(ipel, "person", "personList");

        assertProcessStarted(ipel, SCRIPT_TASK_ID);
        assertNextNode(ipel, "start");
        assertTriggered(ipel, "scriptJava");
        assertChangedVariable(ipel, "output", null, "BirskyKorcaskoVandrovec");
        assertLeft(ipel, "scriptJava");
        assertTriggered(ipel, "scriptMvel");
        assertChangedVariable(ipel, "output", "BirskyKorcaskoVandrovec", "VandrovecBirskyKorcasko");
        assertLeft(ipel, "scriptMvel");
        assertTriggered(ipel, "scriptJavaScript");
        assertChangedVariable(ipel, "output", "VandrovecBirskyKorcasko", "JavaScript Node: Vandrovec");
        assertLeft(ipel, "scriptJavaScript");
        assertNextNode(ipel, "end");
        assertProcessCompleted(ipel, SCRIPT_TASK_ID);
    }

}


