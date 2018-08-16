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

package org.jbpm.bpmn2;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.util.IoUtils;
import org.jbpm.bpmn2.handler.ReceiveTaskHandler;
import org.jbpm.bpmn2.handler.SendTaskHandler;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.handler.SignallingTaskHandlerDecorator;
import org.jbpm.bpmn2.objects.ExceptionService;
import org.jbpm.bpmn2.objects.Person;
import org.jbpm.bpmn2.objects.TestWorkItemHandler;
import org.jbpm.process.audit.VariableInstanceLog;
import org.jbpm.process.instance.impl.demo.DoNothingWorkItemHandler;
import org.jbpm.process.instance.impl.demo.SystemOutWorkItemHandler;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.KieBase;
import org.kie.api.event.process.DefaultProcessEventListener;
import org.kie.api.event.process.ProcessStartedEvent;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

import static org.assertj.core.api.Assertions.*;

@RunWith(Parameterized.class)
public class StandaloneBPMNProcessTest extends JbpmBpmn2TestCase {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneBPMNProcessTest.class);
    
    @Parameters
    public static Collection<Object[]> persistence() {
        Object[][] data = new Object[][] {
                { false, false },
                { true, false }, 
                { true, true } 
                };
        return Arrays.asList(data);
    }
    
    public StandaloneBPMNProcessTest(boolean persistence, boolean locking) {
        super(persistence, locking);
    }

    @BeforeClass
    public static void setup() throws Exception {
        setUpDataSource();
    }

    /**
     * Tests
     */
    
    @Test
    public void testMinimalProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MinimalProcess.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Minimal");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMinimalProcessWithGraphical() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MinimalProcessWithGraphical.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Minimal");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMinimalProcessWithDIGraphical() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MinimalProcessWithDIGraphical.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Minimal");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testCompositeProcessWithDIGraphical() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-CompositeProcessWithDIGraphical.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("Composite");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testScriptTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ScriptTask.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("ScriptTask");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testDataObject() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-DataObject.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("employee", "UserId-12345");
        ProcessInstance processInstance = ksession.startProcess("Evaluation", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEvaluationProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("employee", "UserId-12345");
        ProcessInstance processInstance = ksession.startProcess("Evaluation", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEvaluationProcess2() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess2.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("employee", "UserId-12345");
        ProcessInstance processInstance = ksession.startProcess("com.sample.evaluation", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEvaluationProcess3() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EvaluationProcess3.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("RegisterRequest", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("employee", "john2");
        ProcessInstance processInstance = ksession.startProcess("Evaluation", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testUserTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-UserTask.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        Map<String, Object> params = new HashMap<String, Object>();
        String varId = "s";
        String varValue = "initialValue";
        params.put(varId, varValue);
        ProcessInstance processInstance = ksession.startProcess("UserTask", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        // Test jbpm-audit findVariableInstancesByName* methods
        if( isPersistence() ) { 
            List<VariableInstanceLog> varLogs = logService.findVariableInstancesByName(varId, true);
            assertThat(varLogs).isNotEmpty();
            for( VariableInstanceLog varLog : varLogs ) {
                assertThat(varLog.getVariableId()).isEqualTo(varId);
            }
            varLogs = logService.findVariableInstancesByNameAndValue( varId, varValue, true);
            assertThat(varLogs).isNotEmpty();
            for( VariableInstanceLog varLog : varLogs ) {
                assertThat(varLog.getVariableId()).isEqualTo(varId);
                assertThat(varLog.getValue()).isEqualTo(varValue);
            }
        }
        
        ksession = restoreSession(ksession, true);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testLane() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-Lane.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("UserTask");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("ActorId")).isEqualTo("john");
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("ActorId", "mary");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), results);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        assertThat(workItem.getParameter("SwimlaneActorId")).isEqualTo("mary");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testExclusiveSplit() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplit.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "First");
        params.put("y", "Second");
        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testExclusiveSplitDefault() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitDefault.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "NotFirst");
        params.put("y", "Second");
        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testInclusiveSplit() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-InclusiveSplit.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", 15);
        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testInclusiveSplitDefault() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-InclusiveSplitDefault.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", -5);
        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    @Ignore
    public void testExclusiveSplitXPath() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExclusiveSplitXPath.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        
        ksession.getWorkItemManager().registerWorkItemHandler("Email", new SystemOutWorkItemHandler());
        Document document = DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new ByteArrayInputStream(
                        "<myDocument><chapter1>BlaBla</chapter1><chapter2>MoreBlaBla</chapter2></myDocument>".getBytes()));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", document);
        params.put("y", "SomeString");
        ProcessInstance processInstance = ksession.startProcess("com.sample.test", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testEventBasedSplit() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Yes
        ProcessInstance processInstance = ksession.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        // No
        processInstance = ksession.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("No", "NoValue", processInstance.getId());
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testEventBasedSplitBefore() throws Exception {
        // signaling before the split is reached should have no effect
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        // Yes
        ProcessInstance processInstance = ksession.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        // No
        processInstance = ksession.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new DoNothingWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        ksession.signalEvent("No", "NoValue", processInstance.getId());
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
    }

    @Test
    public void testEventBasedSplitAfter() throws Exception {
        // signaling the other alternative after one has been selected should
        // have no effect
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        // Yes
        ProcessInstance processInstance = ksession.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new DoNothingWorkItemHandler());
        // No
        ksession.signalEvent("No", "NoValue", processInstance.getId());
    }

    @Test(timeout=10000)
    public void testEventBasedSplit2() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 2);
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit2.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Yes
        ProcessInstance processInstance = ksession.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Timer
        processInstance = ksession.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    @Ignore("process does not complete")
    public void testEventBasedSplit3() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit3.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        Person jack = new Person();
        jack.setName("Jack");
        // Yes
        ProcessInstance processInstance = ksession.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("Yes", "YesValue", processInstance.getId());
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        // Condition
        processInstance = ksession.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.insert(jack);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testEventBasedSplit4() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit4.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // Yes
        ProcessInstance processInstance = ksession.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ksession.signalEvent("Message-YesMessage", "YesValue", processInstance.getId());
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        // No
        processInstance = ksession.startProcess("com.sample.test");
        ksession.signalEvent("Message-NoMessage", "NoValue", processInstance.getId());
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testEventBasedSplit5() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-EventBasedSplit5.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        // Yes
        ProcessInstance processInstance = ksession.startProcess("com.sample.test");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        receiveTaskHandler.setKnowledgeRuntime(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        receiveTaskHandler.messageReceived("NoMessage", "NoValue");
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Email1", new SystemOutWorkItemHandler());
        ksession.getWorkItemManager().registerWorkItemHandler("Email2", new SystemOutWorkItemHandler());
        receiveTaskHandler.setKnowledgeRuntime(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        // No
        processInstance = ksession.startProcess("com.sample.test");
        receiveTaskHandler.messageReceived("NoMessage", "NoValue");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        receiveTaskHandler.messageReceived("YesMessage", "YesValue");
    }

    @Test
    public void testCallActivity() throws Exception {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-CallActivity.bpmn2"), ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-CallActivitySubProcess.bpmn2"), ResourceType.BPMN2);
        if (!kbuilder.getErrors().isEmpty()) {
            for (KnowledgeBuilderError error : kbuilder.getErrors()) {
                logger.error("{}", error);
            }
            throw new IllegalArgumentException("Errors while parsing knowledge base");
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        KieSession ksession = createKnowledgeSession(kbase);
        
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "oldValue");
        ProcessInstance processInstance = ksession.startProcess("ParentProcess", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
        assertThat(((WorkflowProcessInstance) processInstance).getVariable("y")).isEqualTo("new value");
    }

    @Test
    public void testSubProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SubProcess.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("SubProcess");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMultiInstanceLoopCharacteristicsProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MultiInstanceLoopCharacteristicsProcess.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        List<String> myList = new ArrayList<String>();
        myList.add("First Item");
        myList.add("Second Item");
        params.put("list", myList);
        ProcessInstance processInstance = ksession.startProcess("MultiInstanceLoopCharacteristicsProcess", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testErrorBoundaryEvent() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ErrorBoundaryEventInterrupting.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("ErrorBoundaryEvent");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test(timeout=10000)
    public void testTimerBoundaryEvent() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventDuration.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        countDownListener.waitTillCompleted();
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test(timeout=10000)
    public void testTimerBoundaryEventInterrupting() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("TimerEvent", 1);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerBoundaryEventInterrupting.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("MyTask", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("TimerBoundaryEvent");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        countDownListener.waitTillCompleted();
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    @Ignore("Process does not complete.")
    public void testAdHocSubProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-AdHocSubProcess.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("AdHocSubProcess");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        WorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNull();
        ksession = restoreSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.fireAllRules();
        
        ksession.signalEvent("Hello2", null, processInstance.getId());
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        ksession = restoreSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    @Ignore("Process does not complete.")
    public void testAdHocSubProcessAutoComplete() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-AdHocSubProcessAutoComplete.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ProcessInstance processInstance = ksession.startProcess("AdHocSubProcess");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);

        WorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNull();
        ksession = restoreSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.fireAllRules();
        workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull().withFailMessage("WorkItem should not be null.");
        ksession = restoreSession(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testIntermediateCatchEventSignal() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventSignal.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        ksession.signalEvent("MyMessage", "SomeValue", processInstance.getId());
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testIntermediateCatchEventMessage() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventMessage.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new SystemOutWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession, true);
        // now signal process instance
        ksession.signalEvent("Message-HelloMessage", "SomeValue", processInstance.getId());
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test(timeout=10000)
    public void testIntermediateCatchEventTimer() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("timer", 1);
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventTimerDuration.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        // now wait for 1 second for timer to trigger
        countDownListener.waitTillCompleted();
        ksession = restoreSession(ksession, true);
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", new DoNothingWorkItemHandler());
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    @Ignore("process does not complete")
    public void testIntermediateCatchEventCondition() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateCatchEventCondition.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("IntermediateCatchEvent");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession);
        // now activate condition
        Person person = new Person();
        person.setName("Jack");
        ksession.insert(person);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testErrorEndEventProcess() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ErrorEndEvent.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("ErrorEndEvent");
        assertProcessInstanceAborted(processInstance.getId(), ksession);
    }

    @Test
    public void testServiceTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ServiceProcess.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("ServiceProcess", params);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
        assertThat(processInstance.getVariable("s")).isEqualTo("Hello john!");
    }

    @Test
    public void testSendTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SendTask.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("SendTask", params);
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    public void testReceiveTask() throws Exception {
        KieBase kbase = createKnowledgeBaseWithoutDumper("BPMN2-ReceiveTask.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ReceiveTaskHandler receiveTaskHandler = new ReceiveTaskHandler(ksession);
        ksession.getWorkItemManager().registerWorkItemHandler("Receive Task", receiveTaskHandler);
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("ReceiveTask");
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        ksession = restoreSession(ksession);
        receiveTaskHandler.messageReceived("HelloMessage", "Hello john!");
        assertProcessInstanceCompleted(processInstance.getId(), ksession);
    }

    @Test
    @Ignore("bpmn does not compile")
    public void testConditionalStart() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ConditionalStart.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        Person person = new Person();
        person.setName("jack");
        ksession.insert(person);
        ksession.fireAllRules();
        person = new Person();
        person.setName("john");
        ksession.insert(person);
        ksession.fireAllRules();
    }

    @Test(timeout=10000)
    public void testTimerStart() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("StartProcess", 5);
        KieBase kbase = createKnowledgeBase("BPMN2-TimerStart.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.addEventListener(countDownListener);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() { 
            
            public void beforeProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
            
        });

        assertThat(list.size()).isEqualTo(0);
        countDownListener.waitTillCompleted();
        assertThat(list.size()).isEqualTo(5);
    }

    @Test
    public void testSignalStart() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SignalStart.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        ksession.signalEvent("MySignal", "NewValue");
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testSignalEnd() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-SignalEndEvent.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ksession.startProcess("SignalEndEvent", params);
    }

    @Test
    public void testMessageStart() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MessageStart.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        final List<Long> list = new ArrayList<Long>();
        ksession.addEventListener(new DefaultProcessEventListener() {
            public void afterProcessStarted(ProcessStartedEvent event) {
                list.add(event.getProcessInstance().getId());
            }
        });
        ksession.signalEvent("Message-HelloMessage", "NewValue");
        assertThat(list.size()).isEqualTo(1);
    }

    @Test
    public void testMessageEnd() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-MessageEndEvent.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess("MessageEndEvent", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testMessageIntermediateThrow() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventMessage.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Send Task", new SendTaskHandler());
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess("MessageIntermediateEvent", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testSignalIntermediateThrow() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventSignal.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("x", "MyValue");
        ProcessInstance processInstance = ksession.startProcess("SignalIntermediateEvent", params);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }

    @Test
    public void testNoneIntermediateThrow() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-IntermediateThrowEventNone.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("NoneIntermediateEvent", null);
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
    
    @Test
    public void testErrorSignallingExceptionServiceTask() throws Exception {
        KieBase kbase = createKnowledgeBase("BPMN2-ExceptionServiceProcess-ErrorSignalling.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        
        runTestErrorSignallingExceptionServiceTask(ksession);
    }
    
    public static void runTestErrorSignallingExceptionServiceTask(KieSession ksession) throws Exception {
        
        // Setup
        String eventType = "Error-code";
        SignallingTaskHandlerDecorator signallingTaskWrapper = new SignallingTaskHandlerDecorator(ServiceTaskHandler.class, eventType);
        signallingTaskWrapper.setWorkItemExceptionParameterName(ExceptionService.exceptionParameterName);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", signallingTaskWrapper);
       
        Object [] caughtEventObjectHolder = new Object[1];
        caughtEventObjectHolder[0] = null;
        ExceptionService.setCaughtEventObjectHolder(caughtEventObjectHolder);
        
        TestWorkItemHandler workItemHandler = new TestWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", workItemHandler);
        
        // Start process
        Map<String, Object> params = new HashMap<String, Object>();
        String input = "this is my service input";
        params.put("serviceInputItem", input );
        ProcessInstance processInstance = ksession.startProcess("ServiceProcess", params);

        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE);
        
        WorkItem workItem = workItemHandler.getWorkItem();
        assertThat(workItem).isNotNull();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        // Check that event was passed to Event SubProcess (and grabbed by WorkItemHandler);
        assertThat(caughtEventObjectHolder[0] != null && caughtEventObjectHolder[0] instanceof WorkItem).isTrue().withFailMessage("Event was not passed to Event Subprocess.");
        workItem = (WorkItem) caughtEventObjectHolder[0];
        Object throwObj = workItem.getParameter(ExceptionService.exceptionParameterName);
        assertThat(throwObj instanceof Throwable).isTrue().withFailMessage("WorkItem doesn't contain Throwable.");
        assertThat(((Throwable) throwObj).getMessage().endsWith(input)).isTrue().withFailMessage("Exception message does not match service input.");

        // Complete process
        processInstance = ksession.getProcessInstance(processInstance.getId());
        assertThat(processInstance == null || processInstance.getState() == ProcessInstance.STATE_ABORTED).isTrue().withFailMessage("Process instance has not been aborted.");
        
    }
    
    @Test
    public void testSignallingExceptionServiceTask() throws Exception {
        // dump/reread functionality seems to work for this test 
        // .. but I'm pretty sure that's more coincidence than design (mriet, 2013-03-06)
        KieBase kbase = createKnowledgeBase("BPMN2-ExceptionServiceProcess-Signalling.bpmn2");
        KieSession ksession = createKnowledgeSession(kbase);
        
        runTestSignallingExceptionServiceTask(ksession);
    }
    
    @Test
    public void testXXEProcessVulnerability() throws Exception {
    	Resource processResource = ResourceFactory.newClassPathResource("xxe-protection/BPMN2-XXE-Process.bpmn2");
    	
    	File dtdFile = new File("src/test/resources/xxe-protection/external.dtd");
        assertThat(dtdFile).exists();
    	
    	String dtdContent = IoUtils.readFileAsString(dtdFile);
    	dtdContent = dtdContent.replaceAll("@@PATH@@", dtdFile.getParentFile().getAbsolutePath());
    	
    	IoUtils.write(dtdFile, dtdContent.getBytes("UTF-8"));
    	
    	byte[] data = IoUtils.readBytesFromInputStream(processResource.getInputStream());
    	String processAsString = new String(data, "UTF-8");
    	// replace place holders with actual paths
    	File testFiles = new File("src/test/resources/xxe-protection");

        assertThat(testFiles).exists();
    	
    	String path = testFiles.getAbsolutePath();
    	processAsString = processAsString.replaceAll("@@PATH@@", path);
    	
    	Resource resource = ResourceFactory.newReaderResource(new StringReader(processAsString));
    	resource.setSourcePath(processResource.getSourcePath());
    	resource.setTargetPath(processResource.getTargetPath());
    	
        KieBase kbase = createKnowledgeBaseFromResources(resource);
        KieSession ksession = createKnowledgeSession(kbase);
        ProcessInstance processInstance = ksession.startProcess("async-examples.bp1");
        
        String var1 = getProcessVarValue(processInstance, "testScript1");
        String var2 = getProcessVarValue(processInstance, "testScript2");

        assertThat(var1).isNull();
        assertThat(var2).isNull();
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED);
    }
    
    public static void runTestSignallingExceptionServiceTask(KieSession ksession) throws Exception {
        // Setup
        String eventType = "exception-signal";
        SignallingTaskHandlerDecorator signallingTaskWrapper = new SignallingTaskHandlerDecorator(ServiceTaskHandler.class, eventType);
        signallingTaskWrapper.setWorkItemExceptionParameterName(ExceptionService.exceptionParameterName);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", signallingTaskWrapper);
       
        Object [] caughtEventObjectHolder = new Object[1];
        caughtEventObjectHolder[0] = null;
        ExceptionService.setCaughtEventObjectHolder(caughtEventObjectHolder);
        
        // Start process
        Map<String, Object> params = new HashMap<String, Object>();
        String input = "this is my service input";
        params.put("serviceInputItem", input );
        ProcessInstance processInstance = ksession.startProcess("ServiceProcess", params);

        // Check that event was passed to Event SubProcess (and grabbed by WorkItemHandler);
        assertThat(caughtEventObjectHolder[0] != null && caughtEventObjectHolder[0] instanceof WorkItem).isTrue().withFailMessage("Event was not passed to Event Subprocess.");
        WorkItem workItem = (WorkItem) caughtEventObjectHolder[0];
        Object throwObj = workItem.getParameter(ExceptionService.exceptionParameterName);
        assertThat(throwObj instanceof Throwable).isTrue().withFailMessage("WorkItem doesn't contain Throwable.");
        assertThat(((Throwable) throwObj).getMessage().endsWith(input)).isTrue().withFailMessage("Exception message does not match service input.");

        // Complete process
        assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_ACTIVE).withFailMessage("Process instance is not active.");
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        
        processInstance = ksession.getProcessInstance(processInstance.getId());
        if( processInstance != null ) {
            assertThat(processInstance.getState()).isEqualTo(ProcessInstance.STATE_COMPLETED).withFailMessage("Process instance is not completed.");
        } // otherwise, persistence use => processInstance == null => process is completed
    }
    
}
