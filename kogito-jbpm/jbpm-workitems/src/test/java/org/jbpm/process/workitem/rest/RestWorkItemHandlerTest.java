package org.jbpm.process.workitem.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Map;

import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class RestWorkItemHandlerTest {

    private final static String serverURL = "http://localhost:9998/test";
    private static Server server;

    @SuppressWarnings({ "rawtypes"})
    @BeforeClass
    public static void initialize() throws Exception {
        SimpleRESTApplication application = new SimpleRESTApplication();
        RuntimeDelegate delegate = RuntimeDelegate.getInstance();

        JAXRSServerFactoryBean bean = delegate.createEndpoint(application, JAXRSServerFactoryBean.class);
        bean.setProvider(new JAXBElementProvider());
        bean.setAddress("http://localhost:9998" + bean.getAddress());
        server = bean.create();
        server.start();
    }

    @AfterClass
    public static void destroy() throws Exception {
        server.stop();
        server.destroy();
    }
    
    @Test
    public void testGETOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL);
        workItem.setParameter( "Method", "GET" );
        
        
        WorkItemManager manager = new TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        
        String result = (String) workItem.getResult("Result");
        assertNotNull("result cannot be null", result);
        assertEquals("Hello from REST", result);
        int responseCode = (Integer) workItem.getResult("Status");
        assertNotNull(responseCode);
        assertEquals(200, responseCode);
        String responseMsg = (String) workItem.getResult("StatusMsg");
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") +" successfully completed OK", responseMsg);
    }
    
    @Test
    public void testGETOperationWithQueryParam() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL+"?param=test");
        workItem.setParameter( "Method", "GET" );
        
        
        WorkItemManager manager = new TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        
        String result = (String) workItem.getResult("Result");
        assertNotNull("result cannot be null", result);
        assertEquals("Hello from REST test", result);
        int responseCode = (Integer) workItem.getResult("Status");
        assertNotNull(responseCode);
        assertEquals(200, responseCode);
        String responseMsg = (String) workItem.getResult("StatusMsg");
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") +" successfully completed OK", responseMsg);
    }
    
    @Test
    public void testPOSTOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
        		"<person><age>25</age><name>Post john</name></person>";
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL+"/xml");
        workItem.setParameter( "Method", "POST" );
        workItem.setParameter( "ContentType", "application/xml" );
        workItem.setParameter( "Content", "<person><name>john</name><age>25</age></person>" );
        
        
        WorkItemManager manager = new TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        
        String result = (String) workItem.getResult("Result");
        assertNotNull("result cannot be null", result);
        assertEquals(expected, result);
        int responseCode = (Integer) workItem.getResult("Status");
        assertNotNull(responseCode);
        assertEquals(200, responseCode);
        String responseMsg = (String) workItem.getResult("StatusMsg");
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") +" successfully completed OK", responseMsg);
    }
    
    @Test
    public void testPOSTOperationWithPathParamAndNoContent() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL+"/john");
        workItem.setParameter( "Method", "POST" );
        
        
        WorkItemManager manager = new TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        
        String result = (String) workItem.getResult("Result");
        assertNotNull("result cannot be null", result);
        assertEquals("Created resource with name john", result);
        int responseCode = (Integer) workItem.getResult("Status");
        assertNotNull(responseCode);
        assertEquals(200, responseCode);
        String responseMsg = (String) workItem.getResult("StatusMsg");
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") +" successfully completed OK", responseMsg);
    }
    
    @Test
    public void testPUTOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<person><age>25</age><name>Put john</name></person>";
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL+"/xml");
        workItem.setParameter( "Method", "PUT" );
        workItem.setParameter( "ContentType", "application/xml" );
        workItem.setParameter( "Content", "<person><name>john</name><age>25</age></person>" );
        
        
        WorkItemManager manager = new TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        
        String result = (String) workItem.getResult("Result");
        assertNotNull("result cannot be null", result);
        assertEquals(expected, result);
        int responseCode = (Integer) workItem.getResult("Status");
        assertNotNull(responseCode);
        assertEquals(200, responseCode);
        String responseMsg = (String) workItem.getResult("StatusMsg");
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") +" successfully completed OK", responseMsg);
    }
    
    @Test
    public void testDELETEOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                "<person><age>-1</age><name>deleted john</name></person>";
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL+"/xml/john");
        workItem.setParameter( "Method", "DELETE" );
        
        
        WorkItemManager manager = new TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
        
        String result = (String) workItem.getResult("Result");
        assertNotNull("result cannot be null", result);
        assertEquals(expected, result);
        int responseCode = (Integer) workItem.getResult("Status");
        assertNotNull(responseCode);
        assertEquals(200, responseCode);
        String responseMsg = (String) workItem.getResult("StatusMsg");
        assertNotNull(responseMsg);
        assertEquals("request to endpoint " + workItem.getParameter("Url") +" successfully completed OK", responseMsg);
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testUnsupportedOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL+"/xml/john");
        workItem.setParameter( "Method", "HEAD" );
        
        
        WorkItemManager manager = new TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
    }
    
    private class TestWorkItemManager implements WorkItemManager {
        
        private WorkItem workItem;
        
        TestWorkItemManager(WorkItem workItem) {
            this.workItem = workItem;
        }

        @Override
        public void completeWorkItem(long id, Map<String, Object> results) {
            ((WorkItemImpl)workItem).setResults(results);
            
        }

        @Override
        public void abortWorkItem(long id) {
            
        }

        @Override
        public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
            
        }
        
    }
}
