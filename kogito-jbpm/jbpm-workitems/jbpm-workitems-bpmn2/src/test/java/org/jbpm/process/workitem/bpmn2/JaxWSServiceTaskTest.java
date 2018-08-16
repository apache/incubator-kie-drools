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

package org.jbpm.process.workitem.bpmn2;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.xml.ws.Endpoint;

import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.jbpm.process.workitem.webservice.WebServiceWorkItemHandler;
import org.jbpm.test.AbstractBaseTest;
import org.jbpm.test.listener.process.NodeLeftCountDownProcessEventListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class JaxWSServiceTaskTest extends AbstractBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(JaxWSServiceTaskTest.class);

    private Endpoint endpoint;
    private Endpoint endpoint2;
    private SimpleService service;

    @Before
    public void setUp() {
        startWebService();
    }

    @After
    public void tearDown() {
        stopWebService();
    }

    @Test
    public void testServiceInvocation() throws Exception {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
                                                              new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s",
                   "john");
        params.put("mode",
                   "sync");

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("WebServiceTask",
                                                                                                  params);
        String variable = (String) processInstance.getVariable("s");
        assertEquals("Hello john",
                     variable);
        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
    }

    @Test(timeout = 10000)
    public void testAsyncServiceInvocation() throws Exception {
        NodeLeftCountDownProcessEventListener countDownListener = new NodeLeftCountDownProcessEventListener("Service Task",
                                                                                            1);
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = createSession(kbase);
        ksession.addEventListener(countDownListener);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
                                                              new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s",
                   "john");
        params.put("mode",
                   "async");

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("WebServiceTask",
                                                                                                  params);
        logger.info("Service invoked async...waiting to get reponse back");
        countDownListener.waitTillCompleted();
        String variable = (String) processInstance.getVariable("s");
        assertEquals("Hello john",
                     variable);
        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
    }

    @Test
    public void testOneWayServiceInvocation() throws Exception {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
                                                              new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s",
                   "john");
        params.put("mode",
                   "oneway");

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("WebServiceTask",
                                                                                                  params);
        logger.info("Execution finished");
        String variable = (String) processInstance.getVariable("s");
        assertNull(variable);
        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
    }

    @Test
    public void testServiceInvocationWithErrorHandled() throws Exception {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
                                                              new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s",
                   "john");
        params.put("mode",
                   "sync");

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("WebServiceTaskError",
                                                                                                  params);
        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
        Object error = processInstance.getVariable("exception");
        assertNotNull(error);
        assertTrue(error instanceof WorkItemHandlerRuntimeException);
    }

    @Test(timeout = 10000)
    public void testServiceInvocationProcessWith2WSImports() throws Exception {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
                                                              new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s",
                   "john");
        params.put("mode",
                   "sync");

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("org.jboss.qa.jbpm.CallWS",
                                                                                                  params);
        String variable = (String) processInstance.getVariable("s");
        assertEquals("Hello john",
                     variable);
        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
    }

    @Test(timeout = 10000)
    public void testServiceInvocationProcessWith2WSImportsWSHandler() throws Exception {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
                                                              new WebServiceWorkItemHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s",
                   "john");
        params.put("mode",
                   "sync");

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("org.jboss.qa.jbpm.CallWS",
                                                                                                  params);
        String variable = (String) processInstance.getVariable("s");
        assertEquals("Hello john",
                     variable);
        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
    }

    @Test
    public void testServiceInvocationWithMultipleParams() throws Exception {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
                                                              new WebServiceWorkItemHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s",
                   new String[]{"john", "doe"});
        params.put("mode",
                   "sync");

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("multiparamws",
                                                                                                  params);
        String variable = (String) processInstance.getVariable("s2");
        assertEquals("Hello doe, john",
                     variable);
        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
    }

    @Test
    public void testServiceInvocationWithMultipleIntParams() throws Exception {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
                                                              new WebServiceWorkItemHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s",
                   new int[]{2, 3});
        params.put("mode",
                   "sync");

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("multiparamws-int",
                                                                                                  params);
        String variable = (String) processInstance.getVariable("s2");
        assertEquals("Hello 2, 3",
                     variable);
        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
    }

    @Test
    public void testOneWayServiceInvocationProcessWSHandler() throws Exception {
        KieBase kbase = readKnowledgeBase();
        KieSession ksession = createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task",
                                                              new WebServiceWorkItemHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s",
                   "john");
        params.put("mode",
                   "oneway");

        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("org.jboss.qa.jbpm.CallWS",
                                                                                                  params);
        logger.info("Execution finished");
        String variable = (String) processInstance.getVariable("s");
        assertNull(variable);
        assertEquals(ProcessInstance.STATE_COMPLETED,
                     processInstance.getState());
    }

    private void startWebService() {
        this.service = new SimpleService();
        this.endpoint = Endpoint.publish("http://127.0.0.1:9876/HelloService/greeting",
                                         service);
        this.endpoint2 = Endpoint.publish("http://127.0.0.1:9877/SecondService/greeting",
                                          service);
    }

    private void stopWebService() {
        this.endpoint.stop();
        this.endpoint2.stop();
    }

    private static KieBase readKnowledgeBase() throws Exception {
        ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-JaxWSServiceTask.bpmn2"),
                     ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-JaxWSServiceTaskWithErrorBoundaryEvent.bpmn2"),
                     ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-TwoWebServiceImports.bpmn"),
                     ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-MultipleParamsWebService.bpmn"),
                     ResourceType.BPMN2);
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-MultipleIntParamsWebService.bpmn"),
                     ResourceType.BPMN2);
        return kbuilder.newKieBase();
    }

    private static KieSession createSession(KieBase kbase) {
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory",
                       "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory",
                       "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        return kbase.newKieSession(config,
                                   EnvironmentFactory.newEnvironment());
    }
}
