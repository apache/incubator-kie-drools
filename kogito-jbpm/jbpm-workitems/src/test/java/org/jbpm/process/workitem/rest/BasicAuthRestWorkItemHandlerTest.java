package org.jbpm.process.workitem.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.cxf.configuration.security.AuthorizationPolicy;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.ext.RequestHandler;
import org.apache.cxf.jaxrs.model.ClassResourceInfo;
import org.apache.cxf.jaxrs.provider.JAXBElementProvider;
import org.apache.cxf.message.Message;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.jbpm.bpmn2.handler.WorkItemHandlerRuntimeException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

@RunWith(Parameterized.class)
public class BasicAuthRestWorkItemHandlerTest {

    @Parameters(name="Http Client 4.3 api = {0}")
    public static Collection<Object[]> parameters() {
        Object[][] locking = new Object[][] { 
                { true }, 
                { false },
                };
        return Arrays.asList(locking);
    };
  
    private final boolean httpClient43;
	
    private final static String serverURL = "http://localhost:9998/test";
    private static Server server;
    
    private String username = "username";
    private String password = "password";
    
    public BasicAuthRestWorkItemHandlerTest(boolean httpClient43) {
    	this.httpClient43 = httpClient43;
    }

    @SuppressWarnings({ "rawtypes"})
    @BeforeClass
    public static void initialize() throws Exception {

        SimpleRESTApplication application = new SimpleRESTApplication();
        RuntimeDelegate delegate = RuntimeDelegate.getInstance();

        JAXRSServerFactoryBean bean = delegate.createEndpoint(application, JAXRSServerFactoryBean.class);
        bean.setProvider(new JAXBElementProvider());
        bean.setAddress("http://localhost:9998" + bean.getAddress());
        // disabled logging interceptor by default but proves to be useful
        // bean.getInInterceptors().add(new LoggingInInterceptor(new PrintWriter(System.out, true)));
        bean.setProvider(new AuthenticationHandler());
        server = bean.create();
        server.start();
    }

    @AfterClass
    public static void destroy() throws Exception {
        server.stop();
        server.destroy();
    }
    
    @Before
    public void setClientApiVersion() {
    	RESTWorkItemHandler.HTTP_CLIENT_API_43 = httpClient43;
    }
    
    @Test
    public void testGETOperation() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler(username, password);
        
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
    public void testGETOperationWithCustomTimeout() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler(username, password);
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL);
        workItem.setParameter( "Method", "GET" );
        workItem.setParameter( "ConnectTimeout", "30000" );
        workItem.setParameter( "ReadTimeout", "25000" );
        
        
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
    public void testGETOperationWithInvalidTimeout() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler(username, password);
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL);
        workItem.setParameter( "Method", "GET" );
        workItem.setParameter( "ConnectTimeout", "" );
        workItem.setParameter( "ReadTimeout", "" );
        
        
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(username, password);
        
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(username, password);
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(username, password);
        
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(username, password);
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(username, password);
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
        RESTWorkItemHandler handler = new RESTWorkItemHandler(username, password);
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL+"/xml/john");
        workItem.setParameter( "Method", "HEAD" );
        
        
        WorkItemManager manager = new TestWorkItemManager(workItem);
        handler.executeWorkItem(workItem, manager);
    }
    
    @Test
    public void testHandleErrorOnNotSuccessfulResponse() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler(username, password);
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL+"/notexisting");
        workItem.setParameter( "Method", "GET" );
        workItem.setParameter("HandleResponseErrors", "true");
        
        
        WorkItemManager manager = new TestWorkItemManager(workItem);
        try {
        	handler.executeWorkItem(workItem, manager);
        	fail("Should throw exception as it was instructed to do so");
        } catch (WorkItemHandlerRuntimeException ex) {
        	
        	RESTServiceException e = (RESTServiceException) ex.getCause().getCause();
        	assertEquals(405, e.getStatus());
        	assertEquals(serverURL+"/notexisting", e.getEndoint());
        	assertEquals("", e.getResponse());
        }
    }
    
    @Test
    public void testHandleErrorOnNotSuccessfulResponseWrongCredentials() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler(username, "wrongpassword");
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL);
        workItem.setParameter( "Method", "GET" );
        workItem.setParameter("HandleResponseErrors", "true");
        
        
        WorkItemManager manager = new TestWorkItemManager(workItem);
        try {
        	handler.executeWorkItem(workItem, manager);
        	fail("Should throw exception as it was instructed to do so");
        } catch (WorkItemHandlerRuntimeException ex) {
        	
        	RESTServiceException e = (RESTServiceException) ex.getCause().getCause();
        	assertEquals(401, e.getStatus());
        	assertEquals(serverURL, e.getEndoint());
        	assertEquals("", e.getResponse());
        }
    }
    
    @Test
    public void testGETOperationAuthTypeAsParam() {
        RESTWorkItemHandler handler = new RESTWorkItemHandler();
        
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setParameter( "Url", serverURL);
        workItem.setParameter( "Method", "GET" );
        workItem.setParameter( "AuthType", "BASIC" );
        workItem.setParameter( "Username", username );
        workItem.setParameter( "Password", password );
        
        
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
    
    private static class AuthenticationHandler implements RequestHandler {
    	 
        public Response handleRequest(Message m, ClassResourceInfo resourceClass) {
            AuthorizationPolicy policy = (AuthorizationPolicy)m.get(AuthorizationPolicy.class);
            String username = policy.getUserName();
            String password = policy.getPassword(); 
            if (isAuthenticated(username, password)) {
                // let request to continue
                return null;
            } else {
                // authentication failed, request the authetication, add the realm name if needed to the value of WWW-Authenticate 
                return Response.status(401).header("WWW-Authenticate", "Basic").build();
            }
        }

		private boolean isAuthenticated(String username, String password) {
			if ("username".equals(username) && "password".equals(password)) {
				return true;
			}
			return false;
		}
     
    }
}
