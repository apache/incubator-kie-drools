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

import java.util.HashMap;
import java.util.Map;

import org.drools.core.command.runtime.process.StartProcessCommand;
import org.jbpm.test.JbpmJUnitBaseTestCase;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.domain.Person;
import org.jbpm.test.listener.IterableProcessEventListener;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;

import static org.jbpm.test.tools.IterableListenerAssert.*;
import static org.junit.Assert.*;

/**
 * Testing data object and association.
 */
public class DataObjectTest extends JbpmTestCase {

    public static final String PROCESS = "org/jbpm/test/functional/DataObject.bpmn";
    public static final String PROCESS_ID = "org.jbpm.test.functional.DataObject";

    public DataObjectTest() {
        super(false);
    }

    /**
     * DataObject is linked via association with Human Task. Work item should
     * obtain this DataObject in parameters.
     */
    @Test(timeout = 30000)
    public void testDataObject() {
        KieSession ksession = createKSession(PROCESS);

        IterableProcessEventListener listener = new IterableProcessEventListener();
        ksession.addEventListener(listener);

        Map<String, Object> params = new HashMap<String, Object>();
        Person mojmir = new Person("Mojmir");
        params.put("person", mojmir);

        JbpmJUnitBaseTestCase.TestWorkItemHandler wih = getTestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", wih);

        StartProcessCommand spc = new StartProcessCommand();
        spc.setProcessId(PROCESS_ID);
        spc.setParameters(params);
        ksession.execute(spc);

        assertChangedVariable(listener, "person", null, mojmir);
        assertProcessStarted(listener, PROCESS_ID);
        assertNextNode(listener, "start");
        assertTriggered(listener, "userTask");

        WorkItem wi = wih.getWorkItem();
        assertTrue(wi.getParameters().containsKey("PersonInput"));
        Object param = wi.getParameter("PersonInput");
        assertTrue(param instanceof Person);
        Person userTaskInput = (Person) param;
        assertEquals("Mojmir", userTaskInput.getName());

        listener.clear();
        ksession.getWorkItemManager().completeWorkItem(wi.getId(), null);
        assertLeft(listener, "userTask");

        assertNextNode(listener, "end");
        assertProcessCompleted(listener, PROCESS_ID);
    }

}
