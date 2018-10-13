/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.bpmn2.handler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.bpmn2.JbpmBpmn2TestCase;
import org.jbpm.bpmn2.XMLBPMNProcessDumperTest;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.core.context.variable.VariableScope;
import org.jbpm.workflow.instance.WorkflowRuntimeException;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.ProcessWorkItemHandlerException.HandlingStrategy;
import org.kie.api.runtime.process.WorkItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkItemHandlerExceptionHandlingTest extends JbpmBpmn2TestCase {

    private static final Logger logger = LoggerFactory.getLogger(XMLBPMNProcessDumperTest.class);

    private static Boolean strictVariableSetting = Boolean.parseBoolean(System.getProperty("org.jbpm.variable.strict", "false"));
    public WorkItemHandlerExceptionHandlingTest() {
        super(true);
    }
    
    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
        VariableScope.setVariableStrictOption(false);
        WorkItemNodeInstance.setVariableStrictOption(false);
    }
    
    @AfterClass
    public static void clean() throws Exception {        
        VariableScope.setVariableStrictOption(strictVariableSetting);
        WorkItemNodeInstance.setVariableStrictOption(strictVariableSetting);
    }

    @Test
    public void testErrornousHandlerWithStrategyComplete() throws Exception {
          
        KieBase kbase = createKnowledgeBaseWithoutDumper("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ScriptTask.bpmn2");

        KieSession ksession = createKnowledgeSession(kbase);
        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ScriptTask", HandlingStrategy.COMPLETE);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("com.sample.boolean");
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        
        assertProcessVarValue(processInstance, "isChecked", "true");
        
        WorkItem handledWorkItem = workItemHandler.getWorkItem();
        assertEquals(WorkItem.COMPLETED, handledWorkItem.getState());
    }

    @Test
    public void testErrornousHandlerWithStrategyCompleteWaitState() throws Exception {
          
        KieBase kbase = createKnowledgeBaseWithoutDumper("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ReceiveTask.bpmn2");

        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler testHandler = new TestWorkItemHandler();
        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ReceiveTask", HandlingStrategy.COMPLETE);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", testHandler);
        ProcessInstance processInstance = ksession.startProcess("com.sample.boolean");
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        WorkItem receiveWorkItem = testHandler.getWorkItem();
        
        Map<String, Object> results = new HashMap<>();
        results.put("Message", true);
        ksession.getWorkItemManager().completeWorkItem(receiveWorkItem.getId(), results);
                
        assertProcessVarValue(processInstance, "isChecked", "true");
        assertProcessInstanceCompleted(processInstance);
    }
    
    @Test
    public void testErrornousHandlerWithStrategyAbort() throws Exception {
          
        KieBase kbase = createKnowledgeBaseWithoutDumper("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ScriptTask.bpmn2");

        KieSession ksession = createKnowledgeSession(kbase);
        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ScriptTask", HandlingStrategy.ABORT);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        Map<String, Object> params = new HashMap<>();
        params.put("isChecked", false);
        ProcessInstance processInstance = ksession.startProcess("com.sample.boolean", params);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertProcessVarValue(processInstance, "isChecked", "false");
        
        WorkItem handledWorkItem = workItemHandler.getWorkItem();
        assertEquals(WorkItem.ABORTED, handledWorkItem.getState());
        
    }
    
    @Test
    public void testErrornousHandlerWithStrategyAbortWaitState() throws Exception {
          
        KieBase kbase = createKnowledgeBaseWithoutDumper("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ReceiveTask.bpmn2");

        KieSession ksession = createKnowledgeSession(kbase);
        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ReceiveTask", HandlingStrategy.ABORT);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        TestWorkItemHandler testHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", testHandler);
        
        Map<String, Object> params = new HashMap<>();
        params.put("isChecked", false);
        ProcessInstance processInstance = ksession.startProcess("com.sample.boolean", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        WorkItem receiveWorkItem = testHandler.getWorkItem();
        
        Map<String, Object> results = new HashMap<>();
        results.put("Message", true);
        ksession.getWorkItemManager().completeWorkItem(receiveWorkItem.getId(), results);
                
        assertProcessVarValue(processInstance, "isChecked", "false");
        assertProcessInstanceCompleted(processInstance);        
        
    }
    
    @Test
    public void testErrornousHandlerWithStrategyRethrow() throws Exception {
          
        KieBase kbase = createKnowledgeBaseWithoutDumper("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ScriptTask.bpmn2");

        KieSession ksession = createKnowledgeSession(kbase);
        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ScriptTask", HandlingStrategy.RETHROW);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("isChecked", false);
            ksession.startProcess("com.sample.boolean", params);
            fail("Should fail since strategy is rethrow");
        } catch (WorkflowRuntimeException e) {
            assertEquals("On purpose", e.getCause().getMessage());
        }        
    }
    
    @Test
    public void testErrornousHandlerWithStrategyRetry() throws Exception {
          
        KieBase kbase = createKnowledgeBaseWithoutDumper("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ScriptTask.bpmn2");

        KieSession ksession = createKnowledgeSession(kbase);
        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ScriptTask", HandlingStrategy.RETRY);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        Map<String, Object> params = new HashMap<>();
        params.put("isChecked", false);
        ProcessInstance processInstance = ksession.startProcess("com.sample.boolean", params);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        assertProcessVarValue(processInstance, "isChecked", "true");
              
    }
    
    @Test
    public void testErrornousHandlerWithStrategyRetryWaitState() throws Exception {
          
        KieBase kbase = createKnowledgeBaseWithoutDumper("handler/BPMN2-UserTaskWithBooleanOutput.bpmn2", "handler/BPMN2-ReceiveTask.bpmn2");

        KieSession ksession = createKnowledgeSession(kbase);
        ErrornousWorkItemHandler workItemHandler = new ErrornousWorkItemHandler("ReceiveTask", HandlingStrategy.RETRY);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        TestWorkItemHandler testHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", testHandler);
        
        Map<String, Object> params = new HashMap<>();
        params.put("isChecked", false);
        ProcessInstance processInstance = ksession.startProcess("com.sample.boolean", params);
        assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
        
        WorkItem receiveWorkItem = testHandler.getWorkItem();
        
        Map<String, Object> results = new HashMap<>();
        results.put("Message", true);
        ksession.getWorkItemManager().completeWorkItem(receiveWorkItem.getId(), results);
        assertProcessVarValue(processInstance, "isChecked", "true");
              
    }
}
