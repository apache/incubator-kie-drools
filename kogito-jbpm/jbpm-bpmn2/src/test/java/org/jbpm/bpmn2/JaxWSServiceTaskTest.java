package org.jbpm.bpmn2;

import java.util.HashMap;
import java.util.Map;

import javax.xml.ws.Endpoint;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.impl.KnowledgeBaseFactoryServiceImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkflowProcessInstance;
import org.jbpm.bpmn2.handler.ServiceTaskHandler;
import org.jbpm.bpmn2.objects.SimpleService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JaxWSServiceTaskTest extends JbpmBpmn2TestCase {
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
        KnowledgeBase kbase = createKnowledgeBase("BPMN2-JaxWSServiceTask.bpmn2");
        StatefulKnowledgeSession ksession = createKnowledgeSession(kbase);
        ksession.getWorkItemManager().registerWorkItemHandler("Service Task", new ServiceTaskHandler(ksession));
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("s", "john");
        
        WorkflowProcessInstance processInstance = (WorkflowProcessInstance) ksession.startProcess("WebServiceTask", params);
        String variable = (String) processInstance.getVariable("s");
        assertEquals("Hello john", variable);
        assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
    }
    
    private void startWebService() {
        this.service = new SimpleService();
        this.endpoint = Endpoint.publish("http://127.0.0.1:9876/HelloService/greeting", service);
    }

    private void stopWebService() {
        this.endpoint.stop();
    }
    

}
