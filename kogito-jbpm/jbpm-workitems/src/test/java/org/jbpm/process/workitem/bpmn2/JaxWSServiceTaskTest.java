package org.jbpm.process.workitem.bpmn2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.ws.Endpoint;

import org.kie.KnowledgeBase;
import org.kie.KnowledgeBaseFactory;
import org.kie.builder.KnowledgeBuilder;
import org.kie.builder.KnowledgeBuilderFactory;
import org.kie.builder.ResourceType;
import org.drools.compiler.BPMN2ProcessFactory;
import org.drools.compiler.ProcessBuilderFactory;
import org.drools.impl.EnvironmentFactory;
import org.drools.impl.KnowledgeBaseFactoryServiceImpl;
import org.kie.io.ResourceFactory;
import org.drools.marshalling.impl.ProcessMarshallerFactory;
import org.kie.runtime.KnowledgeSessionConfiguration;
import org.kie.runtime.StatefulKnowledgeSession;
import org.kie.runtime.process.ProcessInstance;
import org.drools.runtime.process.ProcessRuntimeFactory;
import org.kie.runtime.process.WorkflowProcessInstance;
import org.jbpm.bpmn2.BPMN2ProcessProviderImpl;
import org.jbpm.marshalling.impl.ProcessMarshallerFactoryServiceImpl;
import org.jbpm.process.builder.ProcessBuilderFactoryServiceImpl;
import org.jbpm.process.instance.ProcessRuntimeFactoryServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
        KnowledgeSessionConfiguration config = KnowledgeBaseFactory.newKnowledgeSessionConfiguration(properties);
        return kbase.newStatefulKnowledgeSession(config, EnvironmentFactory.newEnvironment());
    }

}
