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

import java.util.Collections;

import org.assertj.core.api.Assertions;
import org.jbpm.test.JbpmTestCase;
import org.jbpm.test.listener.TrackingProcessEventListener;
import org.jbpm.workflow.instance.node.DynamicUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;

import static org.jbpm.test.tools.TrackingListenerAssert.*;
import static org.junit.Assert.*;

/**
 * Flexible process test. (process fragments without strict process flow
 * connecting them)
 *
 * both are general issues, but were revealed in this test case:
 * https://bugzilla.redhat.com/show_bug.cgi?id=826578
 * https://bugzilla.redhat.com/show_bug.cgi?id=826952
 */
public class FlexibleProcessTest extends JbpmTestCase {

    public static final String PROCESS = "org/jbpm/test/functional/FlexibleProcess.bpmn";
    public static final String PROCESS_ID = "org.jbpm.test.functional.FlexibleProcess";

    public FlexibleProcessTest() {
        super(false);
    }

    /**
     * Flexible process with four fragments. -default - with start node, without
     * end event -two fragments which will be signaled -one fragment which won't
     * be signaled - it should not be executed
     */
    @Ignore
    @Test(timeout = 30000)
    public void testFlexibleProcess() throws Exception {
        KieSession ksession = createKSession(PROCESS);

        TrackingProcessEventListener tpel = new TrackingProcessEventListener();
        ksession.addEventListener(tpel);

        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        ProcessInstance pi = ksession.startProcess(PROCESS_ID);

        assertProcessStarted(tpel, PROCESS_ID);
        assertTriggeredAndLeft(tpel, "start");
        assertTriggered(tpel, "task1");

        ksession.signalEvent("userTask", null, pi.getId());
        assertTriggered(tpel, "userTask");

        ksession = restoreKSession(PROCESS);
        Assertions.assertThat(ksession.getProcessEventListeners()).isNotEmpty();

        WorkItem item = handler.getWorkItem();

        ksession.getWorkItemManager().completeWorkItem(item.getId(), null);

        assertTriggered(tpel, "userTask2");

        ksession.signalEvent("task21", null, pi.getId());

        assertTriggeredAndLeft(tpel, "task21");
        assertTriggeredAndLeft(tpel, "task22");
        assertTriggeredAndLeft(tpel, "end1");
        assertProcessCompleted(tpel, PROCESS_ID);

        assertFalse(tpel.wasNodeTriggered("task3"));
        assertFalse(tpel.wasNodeTriggered("end2"));
    }

    /**
     * Tests dynamic insertion of work item node into adhoc top-level process.
     * DynamicUtils does not support adhoc processes yet, but there is improved
     * version on jbpm master branch.
     */
    @Test(timeout = 30000)
    public void testFlexibleProcessAddWorkItem() {
        KieSession ksession = createKSession(PROCESS);

        TrackingProcessEventListener tpel = new TrackingProcessEventListener();
        ksession.addEventListener(tpel);

        TestWorkItemHandler handler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("addedWorkItem", handler);

        ProcessInstance pi = ksession.startProcess(PROCESS_ID);
        assertProcessStarted(tpel, PROCESS_ID);

        DynamicUtils.addDynamicWorkItem(pi, ksession, "addedWorkItem", Collections.<String, Object>emptyMap());

        WorkItem wi = handler.getWorkItem();
        ksession.getWorkItemManager().completeWorkItem(wi.getId(), new java.util.HashMap<String, Object>());

        assertEquals(wi.getName(), "addedWorkItem");
    }

}
