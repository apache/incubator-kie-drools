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

package org.jbpm.test.regression.event;

import java.util.Arrays;
import java.util.Collection;

import org.jbpm.test.JbpmTestCase;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;
import qa.tools.ikeeper.annotation.BZ;

import static org.junit.Assert.*;

@BZ("1175689")
@RunWith(Parameterized.class)
public class BoundaryErrorEventTest extends JbpmTestCase {

    private static final String PROCESS_PREFIX = "org/jbpm/test/regression/event/BoundaryErrorEvent-";
    private static final String PROCESS_ID = "org.jbpm.test.regression.event.BoundaryErrorEvent";

    private KieSession ksession;

    public BoundaryErrorEventTest(boolean persistence) {
        super(persistence);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][]{{false}, {true}};
        return Arrays.asList(data);
    }

    @Override
    @After
    public void tearDown() throws Exception {
        if (ksession != null) {
            ksession.dispose();
            ksession = null;
        }
        super.tearDown();
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithErrorCodeWithStructureRef() {
        try {
            startInstance("WithErrorCodeWithStructureRef.bpmn2");
            fail("This is not a default handler. So WorkflowRuntimeException must be thrown");
        } catch (WorkflowRuntimeException e) {
            //Pass test when thrown
        }
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithErrorCodeWithoutStructureRef() {
        try {
            startInstance("WithErrorCodeWithoutStructureRef.bpmn2");
            fail("This is not a default handler. So WorkflowRuntimeException must be thrown");
        } catch (WorkflowRuntimeException e) {
            //Pass test when thrown
        }
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithErrorCodeMatchWithStructureRef() {
        ProcessInstance processInstance = startInstance("WithErrorCodeMatchWithStructureRef.bpmn2");
        assertNodesTriggered(processInstance);
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithErrorCodeMatchWithoutStructureRef() {
        ProcessInstance processInstance = startInstance("WithErrorCodeMatchWithoutStructureRef.bpmn2");
        assertNodesTriggered(processInstance);
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithoutErrorCodeWithStructureRef() {
        ProcessInstance processInstance = startInstance("WithoutErrorCodeWithStructureRef.bpmn2");
        assertNodesTriggered(processInstance);
    }

    @Test
    public void testBoundaryErrorEventDefaultHandlerWithoutErrorCodeWithoutStructureRef() {
        ProcessInstance processInstance = startInstance("WithoutErrorCodeWithoutStructureRef.bpmn2");
        assertNodesTriggered(processInstance);
    }

    private ProcessInstance startInstance(String resourceSuffix) {
        ksession = createKSession(PROCESS_PREFIX + resourceSuffix);
        ExceptionWorkItemHandler handler = new ExceptionWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", handler);
        return ksession.startProcess(PROCESS_ID);
    }

    private void assertNodesTriggered(ProcessInstance pi) {
        assertNodeTriggered(pi.getId(), "Start", "User Task", "MyBoundaryErrorEvent", "Script Task 1");
    }

    private class ExceptionWorkItemHandler implements WorkItemHandler {

        @Override
        public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
            throw new RuntimeException();
        }

        @Override
        public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {

        }

    }

}
