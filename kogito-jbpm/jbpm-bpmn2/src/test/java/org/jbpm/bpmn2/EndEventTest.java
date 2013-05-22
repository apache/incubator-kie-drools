/*
Copyright 2013 JBoss Inc

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.*/

package org.jbpm.bpmn2;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(Parameterized.class)
public class EndEventTest extends JbpmTestCase {

    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] { { false }, { true } };
        return Arrays.asList(data);
    };

    private Logger logger = LoggerFactory
            .getLogger(EndEventTest.class);

    private StatefulKnowledgeSession ksession;
    
    public EndEventTest(boolean persistence) {
        super(persistence);
    }

    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
    }

    @After
    public void dispose() {
        if (ksession != null) {
            ksession.dispose();
            ksession = null;
        }
    }

    @Test
    public void testImplicitEndParallel() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ParallelSplit.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("com.sample.test");
        assertProcessInstanceCompleted(processInstance);
        
    }

    @Test
    public void testErrorEndEventProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ErrorEndEvent.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession
                .startProcess("ErrorEndEvent");
        assertProcessInstanceAborted(processInstance);
        assertEquals("error", ((org.jbpm.process.instance.ProcessInstance)processInstance).getOutcome());
        
    }

    @Test
    public void testEscalationEndEventProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EscalationEndEvent.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession
                .startProcess("EscalationEndEvent");
        assertProcessInstanceAborted(processInstance);
        
    }

    @Test
    public void testCompensateEndEventProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-CompensateEndEvent.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession
                .startProcess("CompensateEndEvent");
        assertProcessInstanceCompleted(processInstance);
        // boundary event can only be triggered when node that it is attached to is active
        assertNodeTriggered(processInstance.getId(), "StartProcess", "Task", "CompensateEvent");
        
    }

    @Test
    public void testSignalEnd() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SignalEndEvent.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ksession.startProcess("SignalEndEvent", params);
        
    }

    @Test
    public void testMessageEnd() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MessageEndEvent.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task",
                new SendTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess(
                "MessageEndEvent", params);
        assertProcessInstanceCompleted(processInstance);
        
    }

    @Test
    public void testOnEntryExitScript() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-OnEntryExitScriptProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new SystemOutWorkItemHandler());
        List<String> myList = new ArrayList<String>();
        ksession.setGlobal("list", myList);
        ProcessInstance processInstance = ksession
                .startProcess("OnEntryExitScriptProcess");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(4, myList.size());
        
    }

    @Test
    public void testOnEntryExitNamespacedScript() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-OnEntryExitNamespacedScriptProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new SystemOutWorkItemHandler());
        List<String> myList = new ArrayList<String>();
        ksession.setGlobal("list", myList);
        ProcessInstance processInstance = ksession
                .startProcess("OnEntryExitScriptProcess");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(4, myList.size());
        
    }

    @Test
    public void testOnEntryExitMixedNamespacedScript() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-OnEntryExitMixedNamespacedScriptProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new SystemOutWorkItemHandler());
        List<String> myList = new ArrayList<String>();
        ksession.setGlobal("list", myList);
        ProcessInstance processInstance = ksession
                .startProcess("OnEntryExitScriptProcess");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(4, myList.size());
        
    }
    
    @Test
    public void testOnEntryExitScriptDesigner() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-OnEntryExitDesignerScriptProcess.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask",
                new SystemOutWorkItemHandler());
        List<String> myList = new ArrayList<String>();
        ksession.setGlobal("list", myList);
        ProcessInstance processInstance = ksession
                .startProcess("OnEntryExitScriptProcess");
        assertProcessInstanceCompleted(processInstance);
        assertEquals(4, myList.size());
        
    }
    
}
