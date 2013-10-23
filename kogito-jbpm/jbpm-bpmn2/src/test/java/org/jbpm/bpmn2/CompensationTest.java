/**
 * Copyright 2010 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.bpmn2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.core.context.exception.CompensationScope;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;

@RunWith(Parameterized.class)
public class CompensationTest extends JbpmBpmn2TestCase {

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };

    private KieSession ksession;

    public CompensationTest(boolean persistence) {
        super(persistence);
    }

    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
    }

    @Before
    public void prepare() {
        clearHistory();
    }

    @After
    public void dispose() {
        if (ksession != null) {
            ksession.dispose();
            ksession = null;
        }
    }

    /**
     * TESTS
     */

    @Test
    public void compensationViaIntermediateThrowEventProcess() throws Exception {
        KieSession ksession = createKnowledgeSession("compensation/BPMN2-Compensation-IntermediateThrowEvent.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "0");
        ProcessInstance processInstance = ksession.startProcess("CompensateIntermediateThrowEvent", params);

        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), null);

        // compensation activity (assoc. with script task) signaled *after* script task
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertProcessVarValue(processInstance, "x", "1" );
    }
    
    @Test
    public void compensationTwiceViaSignal() throws Exception {
        KieSession ksession = createKnowledgeSession("compensation/BPMN2-Compensation-IntermediateThrowEvent.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "0");
        String processId = "CompensateIntermediateThrowEvent";
        ProcessInstance processInstance = ksession.startProcess(processId, params);
        
        // twice
        ksession.signalEvent("Compensation", CompensationScope.IMPLICIT_COMPENSATION_PREFIX + processId, processInstance.getId());
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), null);

        // compensation activity (assoc. with script task) signaled *after* script task
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertProcessVarValue(processInstance, "x", "2");
    }
    
    @Test
    public void compensationViaEventSubProcess() throws Exception {
        KieSession ksession = createKnowledgeSession("compensation/BPMN2-Compensation-EventSubProcess.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
 
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "0");
        ProcessInstance processInstance = ksession.startProcess("CompensationEventSubProcess", params);

        assertProcessInstanceActive(processInstance.getId(), ksession);
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), null);
        
        assertProcessVarValue(processInstance, "x", "1");
    }
    
    @Test
    public void compensationOnlyAfterAssociatedActivityHasCompleted() throws Exception {
        KieSession ksession = createKnowledgeSession("compensation/BPMN2-Compensation-UserTaskBeforeAssociatedActivity.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "0");
        ProcessInstance processInstance = ksession.startProcess("CompensateIntermediateThrowEvent", params);
        
        // should NOT cause compensation since compensated activity has not yet completed (or started)! 
        ksession.signalEvent("Compensation", "_3", processInstance.getId());
        
        // user task -> script task (associated with compensation) --> intermeidate throw compensation event
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), null);
        
        // compensation activity (assoc. with script task) signaled *after* to-compensate script task
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertProcessVarValue(processInstance, "x", "1");
    }
    
    @Test
    public void orderedCompensation() throws Exception { 
        KieSession ksession = createKnowledgeSession("compensation/BPMN2-Compensation-ParallelOrderedCompensation-IntermediateThrowEvent.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "");
        ProcessInstance processInstance = ksession.startProcess("CompensateParallelOrdered", params);
        List<WorkItem> workItems = workItemHandler.getWorkItems();
        List<Long> workItemIds = new ArrayList<Long>();
        for( WorkItem workItem : workItems ) { 
           if( "Thr".equals(workItem.getParameter("NodeName")) )  {
               workItemIds.add(workItem.getId());
           }
        }
        for( WorkItem workItem : workItems ) { 
           if( "Two".equals(workItem.getParameter("NodeName")) )  {
               workItemIds.add(workItem.getId());
           }
        }
        for( WorkItem workItem : workItems ) { 
           if( "One".equals(workItem.getParameter("NodeName")) )  {
               workItemIds.add(workItem.getId());
           }
        }
        for( Long id : workItemIds ) { 
            ksession.getWorkItemManager().completeWorkItem(id, null);
        }
        
        // user task -> script task (associated with compensation) --> intermeidate throw compensation event
        String xVal = getProcessVarValue(processInstance, "x");
        // Compensation happens in the *REVERSE* order of completion
        // Ex: if the order is 3, 17, 282, then compensation should happen in the order of 282, 17, 3
        assertEquals("Compensation did not fire in the same order as the associated activities completed.", "_171:_131:_141:_151:", xVal );
    }
    
    @Test
    public void compensationInSubSubProcesses() throws Exception {
        KieSession ksession = createKnowledgeSession("compensation/BPMN2-Compensation-InSubSubProcess.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "0");
        ProcessInstance processInstance = ksession.startProcess("CompensateSubSubSub", params);

        ksession.signalEvent("Compensation", "_C-2", processInstance.getId());
        
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), null);
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), null);
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), null);

        // compensation activity (assoc. with script task) signaled *after* script task
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertProcessVarValue(processInstance, "x", "1");
    }
    
    @Test
    public void specificCompensationOfASubProcess() throws Exception {
        KieSession ksession = createKnowledgeSession("compensation/BPMN2-Compensation-ThrowSpecificForSubProcess.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "1");
        ProcessInstance processInstance = ksession.startProcess("CompensationSpecificSubProcess", params);
        
        // compensation activity (assoc. with script task) signaled *after* to-compensate script task
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        if( ! isPersistence() ) { 
            assertProcessVarValue(processInstance, "x", null);
        } else { 
            assertProcessVarValue(processInstance, "x", "");
        }
    }
    
    @Test
    @Ignore
    public void compensationViaCancellation() throws Exception {
        KieSession ksession = createKnowledgeSession("compensation/BPMN2-Compensation-IntermediateThrowEvent.bpmn2");
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "0");
        ProcessInstance processInstance = ksession.startProcess("CompensateIntermediateThrowEvent", params);

        ksession.signalEvent("Cancel", null, processInstance.getId());
        ksession.getWorkItemManager().completeWorkItem(workItemHandler.getWorkItem().getId(), null);

        // compensation activity (assoc. with script task) signaled *after* script task
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertProcessVarValue(processInstance, "x", "1");
    }
    
}
