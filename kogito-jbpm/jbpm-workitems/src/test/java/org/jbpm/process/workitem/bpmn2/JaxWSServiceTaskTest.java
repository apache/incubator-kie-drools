package org.jbpm.process.workitem.bpmn2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.ws.Endpoint;

import org.drools.compiler.compiler.BPMN2ProcessFactory;
import org.drools.compiler.compiler.ProcessBuilderFactory;
import org.drools.core.impl.EnvironmentFactory;
import org.drools.core.impl.KnowledgeBaseFactoryServiceImpl;
import org.drools.core.marshalling.impl.ProcessMarshallerFactory;
import org.drools.core.runtime.process.ProcessRuntimeFactory;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.KnowledgeBase;
import org.kie.internal.KnowledgeBaseFactory;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;
import org.kie.internal.runtime.StatefulKnowledgeSession;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;

public class JaxWSServiceTaskTest {
    private Endpoint endpoint;
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
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        params.put("mode", "sync");
        
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("WebServiceTask", params);
        String variable = (String) processInstance.getVariable("s");
        assertEquals("Hello john", variable);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    @Test
    public void testAsyncServiceInvocation() throws Exception {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        params.put("mode", "async");
        
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("WebServiceTask", params);
        System.out.println("Service invoked async...waiting to get reponse back");
        Thread.sleep(5000);
        String variable = (String) processInstance.getVariable("s");
        assertEquals("Hello john", variable);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    @Test
    public void testOneWayServiceInvocation() throws Exception {
        KnowledgeBaseFactory.setKnowledgeBaseServiceFactory(new KnowledgeBaseFactoryServiceImpl());
        KnowledgeBase kbase = readKnowledgeBase();
        StatefulKnowledgeSession ksession = createSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        params.put("mode", "oneway");
        
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("WebServiceTask", params);
        System.out.println("Execution finished");
        String variable = (String) processInstance.getVariable("s");
        assertNull(variable);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
        // uncomment sleep to see that web service was in fact invoked
        // Thread.sleep(5000);
    }
    
    private void startWebService() {
        this.service = new SimpleService();
        this.endpoint = Endpoint.publish("http://127.0.0.1:9876/HelloService/greeting", service);
    }

    private void stopWebService() {
        this.endpoint.stop();
    }
    
    private static KnowledgeBase readKnowledgeBase() throws Exception {
        ProcessBuilderFactory.setProcessBuilderFactoryService(new ProcessBuilderFactoryServiceImpl());
        ProcessMarshallerFactory.setProcessMarshallerFactoryService(new ProcessMarshallerFactoryServiceImpl());
        ProcessRuntimeFactory.setProcessRuntimeFactoryService(new ProcessRuntimeFactoryServiceImpl());
        BPMN2ProcessFactory.setBPMN2ProcessProvider(new BPMN2ProcessProviderImpl());
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("BPMN2-JaxWSServiceTask.bpmn2"), ResourceType.BPMN2);
        return kbuilder.newKnowledgeBase();
    }
    
    private static StatefulKnowledgeSession createSession(KnowledgeBase kbase) {
        Properties properties = new Properties();
        properties.put("drools.processInstanceManagerFactory", "org.jbpm.process.instance.impl.DefaultProcessInstanceManagerFactory");
        properties.put("drools.processSignalManagerFactory", "org.jbpm.process.instance.event.DefaultSignalManagerFactory");
        KieSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        return kbase.newStatefulKnowledgeSession(config, EnvironmentFactory.newEnvironment());
    }

}
